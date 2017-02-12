package mx.com.broadcastv.widget;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import mx.com.broadcastv.BroadcastvApplication;
import mx.com.broadcastv.R;
import mx.com.broadcastv.data.ServicesContract;
import mx.com.broadcastv.util.BroadcastvSQLUtil;

public class BroadcasTVWidgetRemoteViewService extends RemoteViewsService {

    //    Channel columns
    public static final int CHANNEL_ID_COL = 0;
    public static final int COL_COUNTRY = 1;
    public static final int COL_DESCRIPTION = 2;
    public static final int COL_CHANNEL_ID = 3;
    public static final int COL_NAME = 4;
    public static final int COL_LANGUAGE = 5;
    public static final int COL_LOGO = 6;
    public static final int COL_URL = 7;
    public static final int COL_GROUP_ID = 8;
    public static final int COL_GROUP_NAME = 9;
    public static final int COL_ID_USER_CHANNEL = 10;
    public static final int COL_IS_FAVORITE = 11;
    public final String LOG_TAG = BroadcasTVWidgetRemoteViewService.class.getSimpleName();

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
                // Nothing to do
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
//                Tell the content provider is a call from the app and restore calling identity
//                TODO make an API call to get the User ID and display the content in the widget
                final long identityToken = Binder.clearCallingIdentity();
                Uri favoriteChannelsUri = ServicesContract.ChannelEntry
                        .buildFavoriteChannels(true, BroadcastvApplication.getInstance().getUserId());
                data = getContentResolver().query(favoriteChannelsUri,
                        BroadcastvSQLUtil.CHANNELS_COLUMNS,
                        null,
                        null,
                        null);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.widget_broadcastv_item);
                String channelName = data.getString(COL_NAME);
                String groupName = data.getString(COL_GROUP_NAME);

                views.setTextViewText(R.id.widget_item_name, channelName);
                views.setTextViewText(R.id.widget_item_group, groupName);
//              TODO definir a donde va el item del widget - Detalles o lista principal
//                final Intent fillInIntent = new Intent();
//                String locationSetting =
//                        Utility.getPreferredLocation(DetailWidgetRemoteViewsService.this);
//                Uri weatherUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
//                        locationSetting,
//                        dateInMillis);
//                fillInIntent.setData(weatherUri);
//                views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);
                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_broadcastv_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position))
                    return data.getLong(CHANNEL_ID_COL);
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}

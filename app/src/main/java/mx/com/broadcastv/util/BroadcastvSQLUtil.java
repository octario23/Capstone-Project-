package mx.com.broadcastv.util;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.List;
import java.util.Vector;

import mx.com.broadcastv.data.ServicesContract;
import mx.com.broadcastv.model.Channels;
import mx.com.broadcastv.model.ChannelsResponse;
import mx.com.broadcastv.model.User;

public class BroadcastvSQLUtil {

    public static final String[] CHANNELS_COLUMNS = {
            ServicesContract.ChannelEntry.TABLE_NAME + "." + ServicesContract.ChannelEntry._ID,
            ServicesContract.ChannelEntry.COL_COUNTRY,
            ServicesContract.ChannelEntry.COL_DESCRIPTION,
            ServicesContract.ChannelEntry.COL_CHANNEL_ID,
            ServicesContract.ChannelEntry.COL_NAME,
            ServicesContract.ChannelEntry.COL_LANGUAGE,
            ServicesContract.ChannelEntry.COL_LOGO,
            ServicesContract.ChannelEntry.COL_URL,
            ServicesContract.ChannelEntry.COL_GROUP_ID,
            ServicesContract.ChannelEntry.COL_GROUP_NAME,
            ServicesContract.ChannelEntry.COL_ID_USER,
            ServicesContract.ChannelEntry.COL_IS_FAVORITE,
    };

    public static final String[] USER_COLUMNS = {
            ServicesContract.UserEntry.TABLE_NAME + "." + ServicesContract.UserEntry._ID,
            ServicesContract.UserEntry.COL_LANG_ID,
            ServicesContract.UserEntry.COL_USER_ID,
            ServicesContract.UserEntry.COL_USERNAME,
            ServicesContract.UserEntry.COL_USER_LOGON,
    };

    public static void updateIsFavoriteChannel(Context context, String idUser, int isFavorite, String idChannel) {

        ContentValues channelValues = new ContentValues();
        channelValues.put(ServicesContract.ChannelEntry.COL_ID_USER, idUser);
        channelValues.put(ServicesContract.ChannelEntry.COL_IS_FAVORITE, isFavorite);

        if(context instanceof Activity) {
            context.getContentResolver().update(ServicesContract.ChannelEntry.CONTENT_URI,
                    channelValues,ServicesContract.ChannelEntry.COL_CHANNEL_ID+ "=?",new String[] {idChannel});
        }
    }

    public static void insertChannelIfNotExists(Context context, List<ChannelsResponse> channels) {
        Vector<ContentValues> cVVector = new Vector<ContentValues>(channels.size());
        for (ChannelsResponse channel : channels) {
            boolean addRecord = false;
            Uri uri = ServicesContract.ChannelEntry.buildChannelIdUriQuery(channel.getChannelId());
            Cursor data = context.getContentResolver().query(uri, CHANNELS_COLUMNS, null, null, null);
            if (data == null) {
                continue;
            }
            if (!data.moveToFirst()) {
                data.close();
                ContentValues channelValues = new ContentValues();
                channelValues.put(ServicesContract.ChannelEntry.COL_DESCRIPTION, channel.getChannelText());
                channelValues.put(ServicesContract.ChannelEntry.COL_CHANNEL_ID, channel.getChannelId());
                channelValues.put(ServicesContract.ChannelEntry.COL_ID_USER, channel.getUserId());
                channelValues.put(ServicesContract.ChannelEntry.COL_GROUP_ID, channel.getGroupId());
                channelValues.put(ServicesContract.ChannelEntry.COL_GROUP_NAME, channel.getGroupName());
                channelValues.put(ServicesContract.ChannelEntry.COL_NAME, channel.getChannelName());
                channelValues.put(ServicesContract.ChannelEntry.COL_LANGUAGE, channel.getChannelLanguage());
                channelValues.put(ServicesContract.ChannelEntry.COL_LOGO, channel.getChannelLogo());
                channelValues.put(ServicesContract.ChannelEntry.COL_URL, channel.getChannelURL());
                channelValues.put(ServicesContract.ChannelEntry.COL_IS_FAVORITE, "0");

                cVVector.add(channelValues);
            }
        }

        if ( cVVector.size() > 0 ) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            context.getContentResolver().bulkInsert(ServicesContract.ChannelEntry.CONTENT_URI, cvArray);

            // delete old data so we don't build up an endless history
//            context.getContentResolver().delete(WeatherContract.WeatherEntry.CONTENT_URI,
//                    WeatherContract.WeatherEntry.COLUMN_DATE + " <= ?",
//                    new String[] {Long.toString(dayTime.setJulianDay(julianStartDay-1))});

        }
    }

    public static User insertUserIfNotExists(Context context, List<User> user) {
        Vector<ContentValues> cVVector = new Vector<ContentValues>(user.size());
        User userData = new User();
        for (User usr : user) {
            boolean addRecord = false;
            Uri uri = ServicesContract.UserEntry.buildUserIdUriQuery(usr.getUserId());
            Cursor data = context.getContentResolver().query(uri, USER_COLUMNS, null, null, null);
            if (data == null) {
                continue;
            }
            if (!data.moveToFirst()) {
                data.close();
                ContentValues userValues = new ContentValues();
                userValues.put(ServicesContract.UserEntry.COL_USER_ID, usr.getUserId());
                userValues.put(ServicesContract.UserEntry.COL_LANG_ID, usr.getLangId());
                userValues.put(ServicesContract.UserEntry.COL_USERNAME, usr.getUserName());
                userValues.put(ServicesContract.UserEntry.COL_USER_LOGON, usr.getUserLogon());
                cVVector.add(userValues);
            }
//            return last item of array of users
                userData = usr;
        }

        if ( cVVector.size() > 0 ) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            context.getContentResolver().bulkInsert(ServicesContract.UserEntry.CONTENT_URI, cvArray);

        }

        return userData;
    }
}

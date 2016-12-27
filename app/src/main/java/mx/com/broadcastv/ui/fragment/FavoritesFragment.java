package mx.com.broadcastv.ui.fragment;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import mx.com.broadcastv.R;
import mx.com.broadcastv.adapter.FavoritesViewAdapter;
import mx.com.broadcastv.data.ServicesContract;
import mx.com.broadcastv.ui.MainListActivity;
import mx.com.broadcastv.ui.views.AdjustableRecyclerView;
import mx.com.broadcastv.util.BroadcastvSQLUtil;

public class FavoritesFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String FRAGMENT_TAG = FavoritesFragment.class.getSimpleName();
    public static final String USER_ID = "user_id";
    private static final int USER_LOADER = 0;
    private static final int CHANNEL_LOADER = 1;
    private static final String SELECTED_KEY = "selected_position";
    private static final String USERID = "user_id";

    public static final int COL_ID = 0;
    public static final int COL_LANG_ID = 1;
    public static final int COL_USER_ID = 2;
    public static final int COL_USERNAME = 3;
    public static final int COL_USER_LOGON = 4;

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

    private AdjustableRecyclerView myRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FavoritesViewAdapter myRecyclerViewAdapter;
    private int mPosition = 0;
    private Toolbar toolbar;
    private FrameLayout rootLayout;
    private int selectedChannels = 0;
    private AdView mAdView;
    private FragmentManager fm;
    private TextView noItemsText;
    private FrameLayout noRecordsLayout;


    public static FavoritesFragment newInstance(Bundle args) {
        FavoritesFragment fragment = new FavoritesFragment();
        if (args!=null){
            fragment.setArguments(args);
        }
        return fragment;
    }

    public FavoritesFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fm = getActivity().getSupportFragmentManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        myRecyclerView          = (AdjustableRecyclerView) view.findViewById(R.id.myrecyclerview);
        linearLayoutManager     = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        myRecyclerViewAdapter   = new FavoritesViewAdapter(getContext(), fm);
        toolbar                 = (Toolbar)         getActivity().findViewById(R.id.toolbar);
        rootLayout              = (FrameLayout)     getActivity().findViewById(R.id.rootLayout);
        noItemsText             = (TextView)        view.findViewById(R.id.noItemsSectionText);
        noRecordsLayout         = (FrameLayout)     view.findViewById(R.id.noRecordLayout);

        mAdView = (AdView) view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("2F1E6681F1A74C8C1CCC370E5478F8AD")
                .build();
        mAdView.loadAd(adRequest);

        myRecyclerView.setAdapter(myRecyclerViewAdapter);
        myRecyclerView.setLayoutManager(linearLayoutManager);


        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        if(mPosition != ListView.INVALID_POSITION){
            outState.putInt(SELECTED_KEY,mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (id){
            case USER_LOADER:
                Uri userUri;
                userUri = ServicesContract.UserEntry.buildUserIdUriQuery(MainListActivity.usr.getUserId());

                String mSelectionClause = null;

                return  new CursorLoader(getActivity(),
                        userUri,
                        BroadcastvSQLUtil.USER_COLUMNS,
                        mSelectionClause,
                        null,
                        null);

            case CHANNEL_LOADER:
                if(args !=null) {
                    Uri favoriteChannels = ServicesContract.ChannelEntry.buildFavoriteChannels(
                            true,args.getString(USERID));
                    Log.e("onCreateLoader: ", favoriteChannels.toString());
                    String mSelectionGroupClause = null;

                    return new CursorLoader(getActivity(),
                            favoriteChannels,
                            BroadcastvSQLUtil.CHANNELS_COLUMNS,
                            mSelectionGroupClause,
                            null,
                            null);
                }
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()){
            case USER_LOADER:
                if (data != null && data.moveToNext()) {
                    Bundle loaderArgs = new Bundle();
                    loaderArgs.putString(USERID,data.getString(COL_USER_ID));
                    getLoaderManager().initLoader(CHANNEL_LOADER, loaderArgs, this);
                }
                break;

            case CHANNEL_LOADER:
                if (data != null && data.moveToFirst()) {
                    myRecyclerViewAdapter.swapCursor(data);
                    noRecordsLayout.setVisibility(View.GONE);
                }else {
                    myRecyclerViewAdapter.swapCursor(null);
                    noRecordsLayout.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(USER_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        myRecyclerViewAdapter.swapCursor(null);
    }

    public void onOrderChanged(Bundle args) {
        getLoaderManager().restartLoader(CHANNEL_LOADER, args, this);
    }
}
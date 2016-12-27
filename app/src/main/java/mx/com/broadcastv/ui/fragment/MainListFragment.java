package mx.com.broadcastv.ui.fragment;


import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;

import mx.com.broadcastv.R;
import mx.com.broadcastv.adapter.MainListRecyclerViewAdapter;
import mx.com.broadcastv.data.ServicesContract;
import mx.com.broadcastv.util.BroadcastvSQLUtil;

/**
 * Created by  on 9/27/16.
 */
public class MainListFragment extends Fragment  implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String FRAGMENT_TAG = MainListFragment.class.getSimpleName();
    private static final int CHANNEL_LOADER = 0;

    public static final int COL_ID = 0;
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
    private static final String SELECTED_KEY = "selected_position";
    public static final String CHANNELS_TO_SHOW = "channels_to_show";


    private RecyclerView myRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private MainListRecyclerViewAdapter myRecyclerViewAdapter;
    private int mPosition = 0;
    private Toolbar toolbar;
    private FrameLayout rootLayout;
    private int selectedChannels = 0;
    private FragmentManager fm;

    public static MainListFragment newInstance(Bundle args) {
        MainListFragment fragment = new MainListFragment();
        if (args!=null){
            fragment.setArguments(args);
        }
        return fragment;
    }

    public MainListFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fm = getActivity().getSupportFragmentManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main_list, container, false);
        myRecyclerView          = (RecyclerView)view.findViewById(R.id.myrecyclerview);
        linearLayoutManager     = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        myRecyclerViewAdapter   = new MainListRecyclerViewAdapter(getContext(),fm);
        toolbar             = (Toolbar)         getActivity().findViewById(R.id.toolbar);
        rootLayout          = (FrameLayout)     getActivity().findViewById(R.id.rootLayout);

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
        Uri channelUri;
        if (args != null) {
            selectedChannels = args.getInt(CHANNELS_TO_SHOW);
        }
        if(selectedChannels == 0){
            channelUri = ServicesContract.ChannelEntry.buildChannelUri("*");
        } else {
//           TODO 9999 default channel to only get information using groupId
            channelUri = ServicesContract.ChannelEntry.buildChannelWithGroupId("9999",selectedChannels);
        }

        String mSelectionClause = null;

        return  new CursorLoader(getActivity(),
                channelUri,
                BroadcastvSQLUtil.CHANNELS_COLUMNS,
                mSelectionClause,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            myRecyclerViewAdapter.swapCursor(data);
            if (mPosition != ListView.INVALID_POSITION) {
                myRecyclerView.smoothScrollToPosition(mPosition);
            }
        }else {
            myRecyclerViewAdapter.swapCursor(null);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(CHANNEL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * state of fragment used to set the toolbar properties for watchNow section
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(toolbar!=null) {
            if (rootLayout.getPaddingTop() == 0) {
                rootLayout.setPadding(0, toolbar.getHeight(), 0, 0);
                ColorDrawable background = (ColorDrawable) toolbar.getBackground();
                background.setAlpha(255);
                background.setColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
            }
            toolbar.setTitle(getResources().getString(R.string.nav_option_todos));
            toolbar.setTitleTextColor(getResources().getColor(R.color.material_drawer_dark_primary_text));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        myRecyclerViewAdapter.swapCursor(null);
    }

    public void onOrderChanged(Bundle args) {
        getLoaderManager().restartLoader(CHANNEL_LOADER, args, this);
    }

}

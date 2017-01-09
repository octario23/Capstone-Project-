package mx.com.broadcastv.ui.fragment;


import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.support.v7.graphics.Palette;

import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import mx.com.broadcastv.BroadcastvApplication;
import mx.com.broadcastv.R;
import mx.com.broadcastv.adapter.MainListRecyclerViewAdapter;
import mx.com.broadcastv.adapter.RecommendationsViewAdapter;
import mx.com.broadcastv.data.ServicesContract;
import mx.com.broadcastv.ui.MainListActivity;
import mx.com.broadcastv.ui.interfaces.OnClickCallback;
import mx.com.broadcastv.ui.views.AdjustableRecyclerView;
import mx.com.broadcastv.util.BroadcastvSQLUtil;
import mx.com.broadcastv.util.WindowCompatUtil;


public class DetailChannelFragment extends Fragment
        implements View.OnClickListener, ViewTreeObserver.OnScrollChangedListener,LoaderManager.LoaderCallbacks<Cursor>{

    public static final String FRAGMENT_TAG = DetailChannelFragment.class.getSimpleName();
    public static final String DETAIL_URI = "URI";
    private static final String FILTER_POS = "" ;
    private static final int CHANNEL_LOADER = 1;
    private static final int RECOMMENDATIONS_LOADER = 2;
    private static final String IS_FAVORITE = "is_favorite";
    private static final String CHANNEL_ID_DATA = "channel_id_data";

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
    private static final String SELECTED_KEY = "selected_position";
    private static final String CHANNEL_GROUP_ID = "channel_group_id";
    private static final String CHANNEL_ID = "channel_id" ;

    private LinearLayoutManager linearLayoutManager;
    private RecommendationsViewAdapter myRecyclerViewAdapter;
    private int mPosition = 0;
    private Uri mUri;
    private FragmentManager fm;
    private Toolbar toolbar;
    private FrameLayout rootLayout;
    private ActionBar actionBar;
    private ImageView mThumbnail;
    private View rootview;
    private ImageButton mActionButton;
    private LinearLayout mContent;
    private ProgressBar mProgress;
    private ImageButton mExpandButton;
    private LinearLayout listContainer;
    private RadioButton extrasRadio;
    private AdjustableRecyclerView extrasList;
    private ScrollView scrollViewHandset;
    private int lastPositionFilter;
    private TextView description;
    private String item;
    private FloatingActionButton mFab;

    public static DetailChannelFragment newInstance(Bundle args) {
        DetailChannelFragment fragment = new DetailChannelFragment();
        if (args!=null){
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i(FRAGMENT_TAG, "onAttach");

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(FRAGMENT_TAG, "onCreate");
//        setHasOptionsMenu(true);
        fm = getActivity().getSupportFragmentManager();
        if(getArguments()!=null){
            mUri = Uri.parse(getArguments().getString(DETAIL_URI));
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(CHANNEL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(scrollViewHandset!=null) {
            scrollViewHandset.getViewTreeObserver().addOnScrollChangedListener(this);
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        if(scrollViewHandset!=null) {
            scrollViewHandset.getViewTreeObserver().removeOnScrollChangedListener(this);
        }
    }

    public DetailChannelFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO uncomment layout for landscape for tablet when you might also like is available using same layout as portrair for now
        rootview = inflater.inflate(R.layout.fragment_detail_channel, container, false);
        getActivity().invalidateOptionsMenu();
        toolbar             = (Toolbar)         getActivity().findViewById(R.id.toolbar);
        rootLayout          = (FrameLayout)     getActivity().findViewById(R.id.rootLayout);
//        channelId = getArguments().getString(CHANNEL_ID);
        if(BroadcastvApplication.getInstance().isTablet()){
//            if(!Application.getInstance().isLandscape()){
//                detailsTwoPanel = false;
                toolbar.setBackgroundColor(getResources().getColor(R.color.md_white_1000));
                ColorDrawable background = (ColorDrawable) toolbar.getBackground();
                background.setAlpha(0);
//            }
//            else{
//                detailsTwoPanel = true;
//                toolbar.setBackgroundColor(getResources().getColor(R.color.app_bar_details_landscape));
//                ColorDrawable background = (ColorDrawable) toolbar.getBackground();
//                background.setAlpha(0);
//            }
        }else{
//                detailsTwoPanel = false;
//                ColorDrawable background = (ColorDrawable) toolbar.getBackground();
//                background.setAlpha(0);
        }

        actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        mThumbnail              = (ImageView)       rootview.findViewById(R.id.image);
        mActionButton           = (ImageButton)     rootview.findViewById(R.id.action_button);
        mContent                = (LinearLayout)    rootview.findViewById(R.id.content);
        mProgress               = (ProgressBar)     rootview.findViewById(R.id.loading_progress);
        mExpandButton           = (ImageButton)     rootview.findViewById(R.id.expand_description);
        listContainer           = (LinearLayout)    rootview.findViewById(R.id.lists_container);
        extrasList              = (AdjustableRecyclerView)    rootview.findViewById(R.id.myrecyclerview);
        scrollViewHandset       = (ScrollView)      rootview.findViewById(R.id.scroll);
        description             = (TextView)        rootview.findViewById(R.id.description);
        linearLayoutManager     = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        myRecyclerViewAdapter   = new RecommendationsViewAdapter(getContext(),fm);
        mFab                    = (FloatingActionButton)     getActivity().findViewById(R.id.mFab);
        extrasList.setAdapter(myRecyclerViewAdapter);
        extrasList.setLayoutManager(linearLayoutManager);

        if(savedInstanceState!=null){
            lastPositionFilter = savedInstanceState.getInt(FILTER_POS);
        }
        return rootview;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rootLayout.setPadding(0, 0, 0, 0);
        ColorDrawable background = (ColorDrawable) toolbar.getBackground();
        background.setAlpha(0);
        background.setColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
    }

    public  void setListViewHeightBasedOnChildren(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;
        int totalHeight;
        totalHeight = 72 * listAdapter.getCount();
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = BroadcastvApplication.dpToPx(totalHeight);
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    private void applyPalette(Palette palette, ImageView image) {
        int primaryDark = getResources().getColor(R.color.primary_dark);
        int primary = getResources().getColor(R.color.primary);
        if (actionBar != null) {
//            actionBar.setHomeAsUpIndicator(R.drawable.ic_action_navigation_arrow_back);

            actionBar.setDisplayHomeAsUpEnabled(true);
        }
//        if(!detailsTwoPanel) {
            toolbar.setBackgroundColor(palette.getMutedColor(primary));
            WindowCompatUtil.setStatusBarcolor(getActivity().getWindow(), palette.getDarkMutedColor(primaryDark));
            initScrollFade(image);
            ActivityCompat.startPostponedEnterTransition(getActivity());
//        }
    }
    private void initScrollFade(final ImageView image) {
        setComponentsStatus(scrollViewHandset, image);
    }
    private void setComponentsStatus(View scrollView, ImageView image) {
        int scrollY = scrollView.getScrollY();
        image.setTranslationY(-scrollY / 2);
        ColorDrawable background = (ColorDrawable) toolbar.getBackground();
        int padding = scrollView.getPaddingTop();
        double alpha = (1 - (((double) padding - (double) scrollY) / (double) padding)) * 255.0;
        alpha = alpha < 0 ? 0 : alpha;
        alpha = alpha > 255 ? 255 : alpha;
        background.setAlpha((int) alpha);
        float scrollRatio = (float) (alpha / 255f);
        int titleColor = getAlphaColor(Color.WHITE, scrollRatio);
        toolbar.setTitleTextColor(titleColor);

    }
    private int getAlphaColor(int color, float scrollRatio) {
        return Color.argb((int) (scrollRatio * 255f), Color.red(color), Color.green(color), Color.blue(color));
    }
    public float getFadingAlpha(float padding, float scrollY){
        return 0 + (padding-scrollY)/padding;
    }
    protected void resizePoster(float resize,ImageView view){

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        params.width -= resize;
        params.height -= resize;
        // view.setLayoutParams(params);

        view.setAlpha(resize);
    }


    @Override
    public void onClick(View v) {
        if (v instanceof ImageButton){
            Bundle data = (Bundle) v.getTag();
            int isFavorite = 0;
            if(data.getInt(IS_FAVORITE) == 1){
                ((ImageButton)v).setImageResource(R.mipmap.start_icon);
            } else {
                ((ImageButton)v).setImageResource(R.mipmap.star_icon_selected);
                isFavorite = 1;
            }
            BroadcastvSQLUtil.updateIsFavoriteChannel(getActivity(), MainListActivity.usr.getUserId(),isFavorite,data.getString(CHANNEL_ID_DATA));
            onOrderChanged();
            if(isFavorite == 1) {
                ((OnClickCallback)getActivity()).showInteractiveMsg(getActivity().getResources().getString(R.string.added_favorite));
            } else {
                ((OnClickCallback)getActivity()).showInteractiveMsg(getActivity().getResources().getString(R.string.delete_favorite));
            }
        }
    }


    @Override
    public void onScrollChanged() {
        setComponentsStatus(scrollViewHandset,mThumbnail);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id){
            case CHANNEL_LOADER:
                String mSelectionClause = null;

                return  new CursorLoader(getActivity(),
                        mUri,
                        BroadcastvSQLUtil.CHANNELS_COLUMNS,
                        mSelectionClause,
                        null,
                        null);

            case RECOMMENDATIONS_LOADER:
                if(args !=null) {
                    Uri groupIdUri =
                            ServicesContract.ChannelEntry.buildChannelWithGroupIdAndRemoveSelf(
                                    Integer.parseInt(args.getString(CHANNEL_ID)),
                                    Integer.parseInt(args.getString(CHANNEL_GROUP_ID)),true);
                    Log.e("onCreateLoader: ", groupIdUri.toString());
                    String mSelectionGroupClause = null;

                    return new CursorLoader(getActivity(),
                            groupIdUri,
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
            case CHANNEL_LOADER:
                if (data != null && data.moveToFirst()) {
                    Bundle loaderArgs = new Bundle();
                    loaderArgs.putString(CHANNEL_GROUP_ID,String.valueOf(data.getInt(COL_GROUP_ID)));
                    loaderArgs.putString(CHANNEL_ID,String.valueOf(data.getInt(COL_CHANNEL_ID)));
                    getLoaderManager().initLoader(RECOMMENDATIONS_LOADER, loaderArgs, this);
                    scrollViewHandset.setVisibility(View.VISIBLE);
                    listContainer.setVisibility(View.VISIBLE);
                    extrasList.setVisibility(View.VISIBLE);
                    mProgress.setVisibility(View.GONE);
                    description.setText(data.getString(COL_DESCRIPTION));
                    toolbar.setTitle(data.getString(COL_NAME));
                    toolbar.setTitleTextColor(getResources().getColor(R.color.material_drawer_primary_text));
                    description.setContentDescription(data.getString(COL_DESCRIPTION));
                    if (data.getInt(MainListFragment.COL_IS_FAVORITE) == 1) {
                        mActionButton.setImageResource(R.mipmap.star_icon_selected);
                    } else {
                        mActionButton.setImageResource(R.mipmap.start_icon);
                    }
                    Bundle args = new Bundle();
                    args.putInt(IS_FAVORITE,data.getInt(MainListFragment.COL_IS_FAVORITE));
                    args.putString(CHANNEL_ID_DATA,data.getString(MainListFragment.COL_CHANNEL_ID));
                    mActionButton.setTag(args);
                    mActionButton.setOnClickListener(this);
                    Bitmap bitmap = ((BitmapDrawable) mThumbnail.getDrawable()).getBitmap();
                    Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                        public void onGenerated(Palette palette) {
                            if (isAdded()) {
                                applyPalette(palette, mThumbnail);
                            }
                        }
                    });
                }
                break;

            case RECOMMENDATIONS_LOADER:
                if (data != null && data.moveToFirst()) {
                    myRecyclerViewAdapter.swapCursor(data);
                    if (mPosition != ListView.INVALID_POSITION) {
                        extrasList.smoothScrollToPosition(mPosition);
                    }
                }
                break;
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        myRecyclerViewAdapter.swapCursor(null);
    }

    public void onOrderChanged() {
        getLoaderManager().restartLoader(CHANNEL_LOADER, null, this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        restablishActionBar();
        mFab.setVisibility(View.VISIBLE);
    }

    public void restablishActionBar() {
        rootLayout.setPadding(0, toolbar.getHeight(), 0, 0);
        toolbar.setTitleTextColor(getResources().getColor(R.color.md_white_1000));
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        toolbar.getBackground().setAlpha(255);
        actionBar.setHomeAsUpIndicator(0);
        actionBar.setDisplayHomeAsUpEnabled(true);
//        WindowCompatUtils.setStatusBarcolor(getActivity().getWindow(), R.color.colorPrimaryDark);

    }
}

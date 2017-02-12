package mx.com.broadcastv.ui;

import android.animation.Animator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.util.List;

import mx.com.broadcastv.BroadcastvApplication;
import mx.com.broadcastv.R;
import mx.com.broadcastv.data.ServicesContract;
import mx.com.broadcastv.model.Channels;
import mx.com.broadcastv.model.ChannelsResponse;
import mx.com.broadcastv.model.Request;
import mx.com.broadcastv.model.Token;
import mx.com.broadcastv.model.User;
import mx.com.broadcastv.ui.fragment.AddChannelFragment;
import mx.com.broadcastv.ui.fragment.DetailChannelFragment;
import mx.com.broadcastv.ui.fragment.FavoritesFragment;
import mx.com.broadcastv.ui.fragment.MainListFragment;
import mx.com.broadcastv.ui.interfaces.OnClickCallback;
import mx.com.broadcastv.util.ApplicationConstants;
import mx.com.broadcastv.util.AsyncTaskHelper;
import mx.com.broadcastv.util.BroadcastvSQLUtil;
import mx.com.broadcastv.util.Utils;
import mx.com.broadcastv.util._Callback;

public class MainListActivity extends AppCompatActivity implements OnClickCallback,
        FragmentManager.OnBackStackChangedListener,
        Drawer.OnDrawerNavigationListener,
        View.OnClickListener {

    public static final java.lang.String SEARCH_CHANNEL_URI = "search_channel_uri";
    private static final int REQUEST_CODE = 7;
    private static final String TOKEN = "token";
    private static final String FRAGMENT_ID = "fragment_id";
    private static final String IS_IN_DETAILS = "is_in_details";
    private static final String IS_ADDING_CHANNEL = "adding_channel";
    public static User usr;
    public static String tokenDvc;
    private Drawer drawer = null;
    private FragmentManager fm;
    private FrameLayout rootLayout;
    private DetailChannelFragment detailsFragment;
    private Toolbar toolbar;
    private int FragmentPosition;
    private int lastPositionClicked;
    private long DEFAULT_POSITION = ApplicationConstants.TODOS_MENU_ID;
    private MainListFragment mainListFragment;
    private String TAG = MainListActivity.class.getName();
    private FavoritesFragment favoriteFragment;
    private boolean isInDetailFragment;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private int searchSection;
    private ProgressDialog progress;
    private View mRevealView;
    private FloatingActionButton mFab;
    private CoordinatorLayout content;
    private Uri searchChannelUri = null;
    private CoordinatorLayout fragmentHidden;
    private AddChannelFragment addChannelFragment;
    private boolean isAddingChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);
        fm = getSupportFragmentManager();
        fm.addOnBackStackChangedListener(this);
        rootLayout = (FrameLayout) findViewById(R.id.rootLayout);
        content = (CoordinatorLayout) findViewById(R.id.content);
        mFab = (FloatingActionButton) findViewById(R.id.mFab);
        mFab.setOnClickListener(this);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.rootLayout, new MainListFragment()).commit();
        }
        BroadcastvApplication.getInstance().startTracking();
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-7331083650726794~3780594661");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public boolean onOptionsItemSelected(MenuItem item) {

                return true;
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

                invalidateOptionsMenu();
            }
        };
        createDrawer(toolbar);
        setNavPosition(savedInstanceState);
    }

    private AccountHeader createHeader() {
        return new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        new ProfileDrawerItem().withName("Usuario Firmado").withEmail("Usuario.firmado@emai.com").withIcon(getResources().getDrawable(R.drawable.profile3))
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();
    }

    private void createDrawer(Toolbar toolbar) {

        drawer = new DrawerBuilder()
                .withAccountHeader(createHeader())
                .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(mDrawerToggle)
                .withActionBarDrawerToggleAnimated(true)
                .addDrawerItems(
                        new PrimaryDrawerItem().withIdentifier(ApplicationConstants.FAVORITOS_MENU_ID)
                                .withName(R.string.nav_option_favoritos).withIcon(R.mipmap.start_icon),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withIdentifier(ApplicationConstants.TODOS_MENU_ID)
                                .withName(R.string.nav_option_todos).withIcon(R.drawable.ic_book),
                        new PrimaryDrawerItem().withIdentifier(ApplicationConstants.TVABIERTA_MENU_ID)
                                .withName(R.string.nav_option_tvabierta).withIcon(R.drawable.ic_lock),
                        new PrimaryDrawerItem().withIdentifier(ApplicationConstants.DEPORTES_MENU_ID)
                                .withName(R.string.nav_option_deportes).withIcon(R.drawable.ic_book),
                        new PrimaryDrawerItem().withIdentifier(ApplicationConstants.CULTURA_MENU_ID)
                                .withName(R.string.nav_option_cultura).withIcon(R.drawable.ic_book),
                        new PrimaryDrawerItem().withIdentifier(ApplicationConstants.ENTRETENIMIENTO_MENU_ID)
                                .withName(R.string.nav_option_entretenimiento).withIcon(R.drawable.ic_book),
                        new PrimaryDrawerItem().withIdentifier(ApplicationConstants.NOTICIAS_MENU_ID)
                                .withName(R.string.nav_option_noticias).withIcon(R.drawable.ic_book),
                        new PrimaryDrawerItem().withIdentifier(ApplicationConstants.RADIO_MENU_ID)
                                .withName(R.string.nav_option_radio).withIcon(R.drawable.ic_book),
                        new PrimaryDrawerItem().withIdentifier(ApplicationConstants.INTERNACIONAL_MENU_ID)
                                .withName(R.string.nav_option_internacional).withIcon(R.drawable.ic_book),
                        new PrimaryDrawerItem().withIdentifier(ApplicationConstants.CINE_MENU_ID)
                                .withName(R.string.nav_option_cine).withIcon(R.drawable.ic_theaters),
                        new PrimaryDrawerItem().withIdentifier(ApplicationConstants.PERSONAL_MENU_ID)
                                .withName(R.string.nav_option_personal).withIcon(R.drawable.ic_book),
                        new PrimaryDrawerItem().withIdentifier(ApplicationConstants.INFANTIL_MENU_ID)
                                .withName(R.string.nav_option_infantil).withIcon(R.drawable.ic_book),
                        new PrimaryDrawerItem().withIdentifier(ApplicationConstants.MUSICA_MENU_ID)
                                .withName(R.string.nav_option_musica).withIcon(R.drawable.ic_music),
                        new PrimaryDrawerItem().withIdentifier(ApplicationConstants.CINEONLINE_MENU_ID)
                                .withName(R.string.nav_option_cineonline).withIcon(R.drawable.ic_music),
                        new PrimaryDrawerItem().withIdentifier(ApplicationConstants.RADIO2_MENU_ID)
                                .withName(R.string.nav_option_radio).withIcon(R.drawable.ic_music),
                        new PrimaryDrawerItem().withIdentifier(ApplicationConstants.ADULTOS_MENU_ID)
                                .withName(R.string.nav_option_adultos).withIcon(R.drawable.profile2).withIcon(R.drawable.ic_book)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int i, IDrawerItem iDrawerItem) {
//                        if (isConnected) {
                        selectMaterialDrawerItem(iDrawerItem);
//                        } else {
//                            noNetworkConnectionFragment = (NoNetworkConnectionFragment) fm.findFragmentByTag(NoNetworkConnectionFragment.FRAGMENT_TAG);
//                            if (noNetworkConnectionFragment != null) {
//                                if (noNetworkConnectionFragment.getId() != R.id.rootLayout) {
//                                    fm.beginTransaction().replace(R.id.rootLayout, noNetworkConnectionFragment, NoNetworkConnectionFragment.FRAGMENT_TAG).commit();
//                                }
//                            } else {
//                                noNetworkConnectionFragment = NoNetworkConnectionFragment.newInstance();
//                                fm.beginTransaction().replace(R.id.rootLayout, noNetworkConnectionFragment, NoNetworkConnectionFragment.FRAGMENT_TAG).commit();
//                            }
//                        }

                        return false;
                    }


                })
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {

                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {

                    }

                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {

                    }
                })
                .withOnDrawerNavigationListener(this)
                .build();
    }

    private void selectMaterialDrawerItem(IDrawerItem item) {
        Bundle args = new Bundle();
        FragmentPosition = (int) item.getIdentifier();

        mainListFragment = (MainListFragment) fm.findFragmentByTag(MainListFragment.FRAGMENT_TAG);
        favoriteFragment = (FavoritesFragment) fm.findFragmentByTag(FavoritesFragment.FRAGMENT_TAG);

        switch ((int) item.getIdentifier()) {

            case ApplicationConstants.FAVORITOS_MENU_ID:
                if (getSupportActionBar() != null)
                    getSupportActionBar().setTitle(R.string.nav_option_favoritos);
                args.putString(FavoritesFragment.USER_ID, BroadcastvApplication.getInstance().getUserId());
                checkFragmentToUse(ApplicationConstants.FAVORITOS_MENU_ID, args);
                mFab.setVisibility(View.GONE);
                break;
            case ApplicationConstants.TODOS_MENU_ID:
                if (getSupportActionBar() != null)
                    getSupportActionBar().setTitle(R.string.nav_option_todos);
                checkFragmentToUse(ApplicationConstants.TODOS_MENU_ID, args);
                break;
            case ApplicationConstants.CULTURA_MENU_ID:
                if (getSupportActionBar() != null)
                    getSupportActionBar().setTitle(R.string.nav_option_cultura);
                args.putInt(MainListFragment.CHANNELS_TO_SHOW, ApplicationConstants.CULTURA_GROUP_ID);
                checkFragmentToUse(ApplicationConstants.CULTURA_MENU_ID, args);
                break;
            case ApplicationConstants.ENTRETENIMIENTO_MENU_ID:
                if (getSupportActionBar() != null)
                    getSupportActionBar().setTitle(R.string.nav_option_entretenimiento);
                args.putInt(MainListFragment.CHANNELS_TO_SHOW, ApplicationConstants.ENTRETENIMIENTO_GROUP_ID);
                checkFragmentToUse(ApplicationConstants.ENTRETENIMIENTO_MENU_ID, args);
                break;
            case ApplicationConstants.NOTICIAS_MENU_ID:
                if (getSupportActionBar() != null)
                    getSupportActionBar().setTitle(R.string.nav_option_noticias);
                args.putInt(MainListFragment.CHANNELS_TO_SHOW, ApplicationConstants.NOTICIAS_GROUP_ID);
                checkFragmentToUse(ApplicationConstants.NOTICIAS_MENU_ID, args);
                break;
            case ApplicationConstants.DEPORTES_MENU_ID:
                if (getSupportActionBar() != null)
                    getSupportActionBar().setTitle(R.string.nav_option_deportes);
                args.putInt(MainListFragment.CHANNELS_TO_SHOW, ApplicationConstants.DEPORTES_GROUP_ID);
                checkFragmentToUse(ApplicationConstants.DEPORTES_MENU_ID, args);
                break;
            case ApplicationConstants.RADIO2_MENU_ID:
            case ApplicationConstants.RADIO_MENU_ID:
                if (getSupportActionBar() != null)
                    getSupportActionBar().setTitle(R.string.nav_option_radio);
                args.putInt(MainListFragment.CHANNELS_TO_SHOW, ApplicationConstants.RADIO_GROUP_ID);
                checkFragmentToUse(ApplicationConstants.RADIO_MENU_ID, args);
                break;
            case ApplicationConstants.MUSICA_MENU_ID:
                if (getSupportActionBar() != null)
                    getSupportActionBar().setTitle(R.string.nav_option_musica);
                args.putInt(MainListFragment.CHANNELS_TO_SHOW, ApplicationConstants.MUSICA_GROUP_ID);
                checkFragmentToUse(ApplicationConstants.MUSICA_MENU_ID, args);
                break;
            case ApplicationConstants.INFANTIL_MENU_ID:
                if (getSupportActionBar() != null)
                    getSupportActionBar().setTitle(R.string.nav_option_infantil);
                args.putInt(MainListFragment.CHANNELS_TO_SHOW, ApplicationConstants.INFANTIL_GROUP_ID);
                checkFragmentToUse(ApplicationConstants.INFANTIL_MENU_ID, args);
                break;
            case ApplicationConstants.INTERNACIONAL_MENU_ID:
                if (getSupportActionBar() != null)
                    getSupportActionBar().setTitle(R.string.nav_option_internacional);
                args.putInt(MainListFragment.CHANNELS_TO_SHOW, ApplicationConstants.INTERNACIONAL_GROUP_ID);
                checkFragmentToUse(ApplicationConstants.INTERNACIONAL_MENU_ID, args);
                break;
            case ApplicationConstants.TVABIERTA_MENU_ID:
                if (getSupportActionBar() != null)
                    getSupportActionBar().setTitle(R.string.nav_option_tvabierta);
                args.putInt(MainListFragment.CHANNELS_TO_SHOW, ApplicationConstants.TVABIERTA_GROUP_ID);
                checkFragmentToUse(ApplicationConstants.TVABIERTA_MENU_ID, args);
                break;
            case ApplicationConstants.CINE_MENU_ID:
                if (getSupportActionBar() != null)
                    getSupportActionBar().setTitle(R.string.nav_option_cine);
                args.putInt(MainListFragment.CHANNELS_TO_SHOW, ApplicationConstants.CINE_GROUP_ID);
                checkFragmentToUse(ApplicationConstants.CINE_MENU_ID, args);
                break;
            case ApplicationConstants.PERSONAL_MENU_ID:
                if (getSupportActionBar() != null)
                    getSupportActionBar().setTitle(R.string.nav_option_personal);
                args.putInt(MainListFragment.CHANNELS_TO_SHOW, ApplicationConstants.PERSONAL_GROUP_ID);
                checkFragmentToUse(ApplicationConstants.PERSONAL_MENU_ID, args);
                break;
            case ApplicationConstants.CINEONLINE_MENU_ID:
                if (getSupportActionBar() != null)
                    getSupportActionBar().setTitle(R.string.nav_option_cineonline);
                args.putInt(MainListFragment.CHANNELS_TO_SHOW, ApplicationConstants.CINEONLINE_GROUP_ID);
                checkFragmentToUse(ApplicationConstants.CINEONLINE_MENU_ID, args);
                break;
            case ApplicationConstants.ADULTOS_MENU_ID:
                if (getSupportActionBar() != null)
                    getSupportActionBar().setTitle(R.string.nav_option_adultos);
                args.putInt(MainListFragment.CHANNELS_TO_SHOW, ApplicationConstants.ADULTOS_GROUP_ID);
                checkFragmentToUse(ApplicationConstants.ADULTOS_MENU_ID, args);
                break;
        }

        drawer.closeDrawer();
    }

    public void setNavPosition(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            FragmentPosition = savedInstanceState.getInt(FRAGMENT_ID);
            isInDetailFragment = savedInstanceState.getBoolean(IS_IN_DETAILS);
//            isAddingChannel  = savedInstanceState.getBoolean(IS_ADDING_CHANNEL);
            if (isInDetailFragment) {
                drawer.setSelection(FragmentPosition, false);
            } else {
                drawer.setSelection(FragmentPosition);
            }
        } else {
            drawer.setSelection(DEFAULT_POSITION, true);
            getChannelsFromAPI();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.i(TAG, "onSaveInstanceState");
        Log.i(TAG + "SaveinstancePos", String.valueOf(FragmentPosition));
        outState.putInt(FRAGMENT_ID, FragmentPosition);
        drawer.saveInstanceState(outState);
        outState.putBoolean(IS_IN_DETAILS, isInDetailFragment);
//        outState.putBoolean(IS_ADDING_CHANNEL, isAddingChannel);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        isInDetailFragment = savedInstanceState.getBoolean(IS_IN_DETAILS);
//        isAddingChannel = savedInstanceState.getBoolean(IS_ADDING_CHANNEL);
        if (isInDetailFragment) {
            drawer.getActionBarDrawerToggle().setDrawerIndicatorEnabled(false);
        }
        toolbar.setTitleTextColor(getResources().getColor(R.color.md_white_1000));
        setNavPosition(savedInstanceState);

        Log.i(TAG, "onRestoreInstanceState");

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (searchChannelUri != null) {
            goToChannelDetails(searchChannelUri);
            searchChannelUri = null;
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onItemSelected(Uri elementUri) {
        goToChannelDetails(elementUri);
    }

    @Override
    public void onPlayButtonClicked(String url, String channelName) {
        Tracker tracker = BroadcastvApplication.getInstance().getTracker();
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory(getResources().getString(R.string.play_category))
                .setAction(getResources().getString(R.string.play_action))
                .setLabel(channelName)
                .build());
        Intent intent = new Intent(this, VideoPlayerActivity.class);
        intent.putExtra(ApplicationConstants.VIDEO_URL, url);
        startActivityForResult(intent, ApplicationConstants.VIDEO_PROCESS);
    }

    @Override
    public void showInteractiveMsg(String msg) {
        Snackbar snackbar = Snackbar
                .make(content, msg, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    @Override
    public void closeAddChannelForm() {
        drawer.setSelection(FragmentPosition);
    }

    private void cleanBackStack() {
        detailsFragment = (DetailChannelFragment) fm.findFragmentByTag(DetailChannelFragment.FRAGMENT_TAG);
        if (detailsFragment != null) {
            fm.popBackStackImmediate();
            fm.beginTransaction().remove(detailsFragment).commit();
        }
        addChannelFragment = (AddChannelFragment) fm.findFragmentByTag(AddChannelFragment.FRAGMENT_TAG);
        if (addChannelFragment != null) {
            fm.popBackStackImmediate();
            fm.beginTransaction().remove(addChannelFragment).commit();
        }
    }

    @Override
    public void onBackStackChanged() {
        isInDetailFragment = false;
        if (fm.getBackStackEntryCount() > 0) {

            for (int i = 0; i < fm.getBackStackEntryCount(); i++) {

                if (DetailChannelFragment.FRAGMENT_TAG.equals(fm.getBackStackEntryAt(i).getName())) {
                    isInDetailFragment = true;
                    drawer.getActionBarDrawerToggle().setDrawerIndicatorEnabled(false);
                }
            }
        } else {
            drawer.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);
        }

    }


    private void checkFragmentToUse(int id, Bundle args) {
        if (id == ApplicationConstants.FAVORITOS_MENU_ID) {
            if (favoriteFragment == null) {
                favoriteFragment = new FavoritesFragment();
            } else {
                favoriteFragment.onOrderChanged(args);
            }
            fm.beginTransaction().replace(R.id.rootLayout, favoriteFragment,
                    favoriteFragment.FRAGMENT_TAG).commit();
        } else {
            if (mainListFragment == null) {
                mainListFragment = MainListFragment.newInstance(args);
            } else {
                mainListFragment.onOrderChanged(args);
            }
            fm.beginTransaction().replace(R.id.rootLayout, mainListFragment,
                    mainListFragment.FRAGMENT_TAG).commit();
        }

    }

    @Override
    public boolean onNavigationClickListener(View view) {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        Log.e(TAG, "onBackPressed: ");
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == ApplicationConstants.VIDEO_PROCESS) {
            Log.e(TAG, String.valueOf(requestCode));
//            if(resultCode == Activity.RESULT_OK){
//                String result=data.getStringExtra("result");
//                Validations.showAlert(context, "Profuturo onActivityResult: ", result, null);
//            }
//            if (resultCode == Activity.RESULT_CANCELED) {
//                Validations.showAlert(context, "Profuturo onActivityResult: ", "Fail", null);
//            }
        }
        if (requestCode == ApplicationConstants.SEARCH_PROCESS) {
            if (resultCode == Activity.RESULT_OK) {
                if (data.getExtras() != null) {
                    String channel = data.getExtras().getString(SEARCH_CHANNEL_URI);
                    searchChannelUri = ServicesContract.ChannelEntry.buildChannelIdUriQuery(channel);

                }
            }

//            Log.e("Search Request", String.valueOf());

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.search:
                searchSection = ApplicationConstants.SEARCH_MENU_ID;
                Intent i = new Intent(this, SearchActivity.class);
                startActivityForResult(i, ApplicationConstants.SEARCH_PROCESS);
                break;
            case R.id.refresh:
                getChannelsFromAPI();
                break;
        }


        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    private void getChannelsFromAPI() {
        Token token = new Token();
        final Context ctx = (Activity) this;
        token.setDeviceId(
                Settings.Secure.getString(this.getContentResolver(),
                        Settings.Secure.ANDROID_ID)
        );
        token.setDeviceOSName("Android");
        token.setDeviceOSVersion(String.valueOf(Build.VERSION.RELEASE));
        final Request<Token> request = new Request<Token>();
        request.setRqt(token);
        progress = Utils.broadcastvLoading(this);
        progress.show();
        new AsyncTaskHelper((Activity) ctx, request, (new _Callback() {
            @Override
            public void execute(JsonNode response, Context context) {
                if (response.get("Success") != null && response.get("Success").asBoolean() == true) {
                    Channels channels = new Channels();
                    try {
                        ObjectMapper mapper = new ObjectMapper();
//                                    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
                        TypeReference<List<User>> typeRef = new TypeReference<List<User>>() {
                        };
                        List<User> list = mapper.readValue(response.get("User").traverse(), typeRef);
                        usr = BroadcastvSQLUtil.insertUserIfNotExists(ctx, list);
                        tokenDvc = response.get("Token").asText();
                    } catch (IOException e) {
                        Log.e(TAG, "execute: " + e.toString());
                    }
                    channels.setDeviceToken(response.get("Token").asText());
                    final Request<Channels> rqt = new Request<Channels>();
                    rqt.setRqt(channels);
                    new AsyncTaskHelper((Activity) ctx, rqt, (new _Callback() {
                        @Override
                        public void execute(JsonNode response, Context context) {
                            if (response.get("Success") != null && response.get("Success").asBoolean() == true) {
                                try {
                                    ObjectMapper mapper = new ObjectMapper();
//                                    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
                                    TypeReference<List<ChannelsResponse>> typeRef = new TypeReference<List<ChannelsResponse>>() {
                                    };
                                    List<ChannelsResponse> list = mapper.readValue(response.get("Channels").traverse(), typeRef);
                                    BroadcastvSQLUtil.insertChannelIfNotExists(ctx, list);
                                } catch (IOException e) {
                                    Log.e(TAG, "execute: " + e.toString());
                                }
                            }
                        }

                        @Override
                        public void Failure(JsonNode response, Context context) {
                            Log.e(TAG, String.valueOf(response));
                            progress.hide();
                        }

                        @Override
                        public HttpEntity<?> setHeader(Request request, HttpHeaders headers) {
                            HttpEntity<?> requestEntity = new HttpEntity<Request>(rqt, headers);
                            return requestEntity;
                        }
                    }), "/ObtenerCanal").execute();
                    progress.hide();
                } else {
                    progress.hide();
                    Toast.makeText(ctx,
                            context.getResources().getString(R.string.error_network), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void Failure(JsonNode response, Context context) {
                Log.e(TAG, String.valueOf(response));
                progress.hide();
            }

            @Override
            public HttpEntity<?> setHeader(Request request, HttpHeaders headers) {
                HttpEntity<?> requestEntity = new HttpEntity<Request>(request, headers);
                return requestEntity;
            }
        }), "/ObtenerToken").execute();
    }

    @Override
    public void onClick(View v) {
        if (v instanceof FloatingActionButton) {
            int cx = (mFab.getLeft() + mFab.getRight()) / 2;
            int cy = (mFab.getTop() + mFab.getBottom()) / 2;

            // get the final radius for the clipping circle
            int dx = Math.max(cx, rootLayout.getWidth() - cx);
            int dy = Math.max(cy, rootLayout.getHeight() - cy);
            float finalRadius = (float) Math.hypot(dx, dy);

            // Android native animator
            Animator animator =
                    ViewAnimationUtils.createCircularReveal(rootLayout, cx, cy, 0, finalRadius);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setDuration(400);
            // make the view invisible when the animation is done
//             animator.addListener(new AnimatorListenerAdapter() {
//                 @Override
//                 public void onAnimationEnd(Animator animation) {
//                     super.onAnimationEnd(animation);
////                     rootLayout.setVisibility(View.INVISIBLE);
//                 }
//             });
            if (getSupportActionBar() != null)
                getSupportActionBar().setTitle(R.string.addChannel);
            showChannelForm();
            mFab.setVisibility(View.GONE);
            animator.start();
        }
    }

    public void goToChannelDetails(Uri elementUri) {

        detailsFragment = (DetailChannelFragment) fm.findFragmentByTag(DetailChannelFragment.FRAGMENT_TAG);
        if (detailsFragment != null) {
            if (detailsFragment.getId() == R.id.rootLayout) {
                cleanBackStack();
            }
        }
//        isInMediaDetails = true;

        detailsFragment = (DetailChannelFragment) fm.findFragmentByTag(DetailChannelFragment.FRAGMENT_TAG);
        if (detailsFragment == null) {
            Bundle args = new Bundle();
            args.putString(DetailChannelFragment.DETAIL_URI, String.valueOf(elementUri));
            detailsFragment = DetailChannelFragment.newInstance(args);
        }

        drawer.getActionBarDrawerToggle().setDrawerIndicatorEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (detailsFragment.getId() != R.id.rootLayout) {
            fm.beginTransaction().replace(R.id.rootLayout, detailsFragment,
                    DetailChannelFragment.FRAGMENT_TAG)
                    .addToBackStack(DetailChannelFragment.FRAGMENT_TAG)
                    .commit();
            fm.executePendingTransactions();
        }
        mFab.setVisibility(View.GONE);
    }

    public void showChannelForm() {
        addChannelFragment = (AddChannelFragment) fm.findFragmentByTag(AddChannelFragment.FRAGMENT_TAG);
        if (addChannelFragment != null) {
            if (addChannelFragment.getId() == R.id.rootLayout) {
                cleanBackStack();
            }
        }
//        isInMediaDetails = true;

        addChannelFragment = (AddChannelFragment) fm.findFragmentByTag(AddChannelFragment.FRAGMENT_TAG);
        if (addChannelFragment == null) {
            Bundle args = new Bundle();
            args.putString(TOKEN, tokenDvc);
            addChannelFragment = AddChannelFragment.newInstance(args);
        }

        drawer.getActionBarDrawerToggle().setDrawerIndicatorEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (addChannelFragment.getId() != R.id.rootLayout) {
            fm.beginTransaction().add(R.id.rootLayout, addChannelFragment,
                    AddChannelFragment.FRAGMENT_TAG)
                    .addToBackStack(AddChannelFragment.FRAGMENT_TAG)
                    .commit();
            fm.executePendingTransactions();
        }
    }
}

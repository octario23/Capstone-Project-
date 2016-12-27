package mx.com.broadcastv.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.ads.MobileAds;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;

import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import mx.com.broadcastv.R;
import mx.com.broadcastv.adapter.MainListRecyclerViewAdapter;
import mx.com.broadcastv.data.ServicesContract;
import mx.com.broadcastv.model.Channels;
import mx.com.broadcastv.model.ChannelsResponse;
import mx.com.broadcastv.model.Request;
import mx.com.broadcastv.model.Token;
import mx.com.broadcastv.model.TokenResponse;
import mx.com.broadcastv.model.User;
import mx.com.broadcastv.ui.fragment.DetailChannelFragment;
import mx.com.broadcastv.ui.fragment.FavoritesFragment;
import mx.com.broadcastv.ui.fragment.MainListFragment;
import mx.com.broadcastv.ui.interfaces.OnClickCallback;
import mx.com.broadcastv.ui.views.RevealLayout;
import mx.com.broadcastv.util.ApplicationConstants;
import mx.com.broadcastv.util.AsyncTaskHelper;
import mx.com.broadcastv.util.BroadcastvSQLUtil;
import mx.com.broadcastv.util.Utils;
import mx.com.broadcastv.util._Callback;

public class MainListActivity extends AppCompatActivity implements OnClickCallback,
        FragmentManager.OnBackStackChangedListener,
        Drawer.OnDrawerNavigationListener,
        View.OnClickListener{

    private static final int REQUEST_CODE = 7 ;
    public static final java.lang.String SEARCH_CHANNEL_URI = "search_channel_uri";
    private Drawer drawer = null;
    private FragmentManager fm;
    private FrameLayout rootLayout;
    private DetailChannelFragment detailsFragment;
    private Toolbar toolbar;
    private long FragmentPosition;
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
    private RevealLayout mRevealLayout;
    private View mRevealView;
    private FloatingActionButton mFab;
    private CoordinatorLayout content;
    public  static User usr;
    private Uri searchChannelUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);
        fm = getSupportFragmentManager();
        fm.addOnBackStackChangedListener(this);
        rootLayout = (FrameLayout) findViewById(R.id.rootLayout);
        content = (CoordinatorLayout) findViewById(R.id.content);
        mRevealLayout = (RevealLayout) findViewById(R.id.reveal_layout);
        mRevealView = findViewById(R.id.reveal_view);
        mFab = (FloatingActionButton) findViewById(R.id.mFab);
        mFab.setOnClickListener(this);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.rootLayout, new MainListFragment()).commit();
        }
        getChannelsFromAPI();
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

    private AccountHeader createHeader(){
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

    private void createDrawer(Toolbar toolbar){

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
        FragmentPosition = item.getIdentifier();
        if (FragmentPosition == 0 && lastPositionClicked != 0) {
            FragmentPosition = lastPositionClicked;
        }
        mainListFragment = (MainListFragment) fm.findFragmentByTag(MainListFragment.FRAGMENT_TAG);
        favoriteFragment = (FavoritesFragment) fm.findFragmentByTag(FavoritesFragment.FRAGMENT_TAG);

        switch ((int)item.getIdentifier()) {

            case ApplicationConstants.FAVORITOS_MENU_ID:
                if (getSupportActionBar() != null)
                    getSupportActionBar().setTitle(R.string.nav_option_favoritos);
                    args.putString(FavoritesFragment.USER_ID,usr.getUserId());
                    checkFragmentToUse(ApplicationConstants.FAVORITOS_MENU_ID,args);
                break;
            case ApplicationConstants.TODOS_MENU_ID:
                if (getSupportActionBar() != null)
                    getSupportActionBar().setTitle(R.string.nav_option_todos);
                    checkFragmentToUse(ApplicationConstants.TODOS_MENU_ID,args);
                break;
            case ApplicationConstants.CULTURA_MENU_ID:
                if (getSupportActionBar() != null)
                    getSupportActionBar().setTitle(R.string.nav_option_cultura);
                args.putInt(MainListFragment.CHANNELS_TO_SHOW,ApplicationConstants.CULTURA_GROUP_ID);
                checkFragmentToUse(ApplicationConstants.CULTURA_MENU_ID,args);
                break;
            case ApplicationConstants.ENTRETENIMIENTO_MENU_ID:
                if (getSupportActionBar() != null)
                    getSupportActionBar().setTitle(R.string.nav_option_entretenimiento);
                args.putInt(MainListFragment.CHANNELS_TO_SHOW,ApplicationConstants.ENTRETENIMIENTO_GROUP_ID);
                checkFragmentToUse(ApplicationConstants.ENTRETENIMIENTO_MENU_ID,args);
                break;
            case ApplicationConstants.NOTICIAS_MENU_ID:
                if (getSupportActionBar() != null)
                    getSupportActionBar().setTitle(R.string.nav_option_noticias);
                args.putInt(MainListFragment.CHANNELS_TO_SHOW,ApplicationConstants.NOTICIAS_GROUP_ID);
                checkFragmentToUse(ApplicationConstants.NOTICIAS_MENU_ID,args);
                break;
            case ApplicationConstants.DEPORTES_MENU_ID:
                if (getSupportActionBar() != null)
                    getSupportActionBar().setTitle(R.string.nav_option_deportes);
                args.putInt(MainListFragment.CHANNELS_TO_SHOW,ApplicationConstants.DEPORTES_GROUP_ID);
                checkFragmentToUse(ApplicationConstants.DEPORTES_MENU_ID,args);
                break;
            case ApplicationConstants.RADIO2_MENU_ID:
            case ApplicationConstants.RADIO_MENU_ID:
                if (getSupportActionBar() != null)
                    getSupportActionBar().setTitle(R.string.nav_option_radio);
                args.putInt(MainListFragment.CHANNELS_TO_SHOW,ApplicationConstants.RADIO_GROUP_ID);
                checkFragmentToUse(ApplicationConstants.RADIO_MENU_ID,args);
                break;
            case ApplicationConstants.MUSICA_MENU_ID:
                if (getSupportActionBar() != null)
                    getSupportActionBar().setTitle(R.string.nav_option_musica);
                args.putInt(MainListFragment.CHANNELS_TO_SHOW,ApplicationConstants.MUSICA_GROUP_ID);
                checkFragmentToUse(ApplicationConstants.MUSICA_MENU_ID,args);
                break;
            case ApplicationConstants.INFANTIL_MENU_ID:
                if (getSupportActionBar() != null)
                    getSupportActionBar().setTitle(R.string.nav_option_infantil);
                args.putInt(MainListFragment.CHANNELS_TO_SHOW,ApplicationConstants.INFANTIL_GROUP_ID);
                checkFragmentToUse(ApplicationConstants.INFANTIL_MENU_ID,args);
                break;
            case ApplicationConstants.INTERNACIONAL_MENU_ID:
                if (getSupportActionBar() != null)
                    getSupportActionBar().setTitle(R.string.nav_option_internacional);
                args.putInt(MainListFragment.CHANNELS_TO_SHOW,ApplicationConstants.INTERNACIONAL_GROUP_ID);
                checkFragmentToUse(ApplicationConstants.INTERNACIONAL_MENU_ID,args);
                break;
            case ApplicationConstants.TVABIERTA_MENU_ID:
                if (getSupportActionBar() != null)
                    getSupportActionBar().setTitle(R.string.nav_option_tvabierta);
                args.putInt(MainListFragment.CHANNELS_TO_SHOW,ApplicationConstants.TVABIERTA_GROUP_ID);
                checkFragmentToUse(ApplicationConstants.TVABIERTA_MENU_ID,args);
                break;
            case ApplicationConstants.CINE_MENU_ID:
                if (getSupportActionBar() != null)
                    getSupportActionBar().setTitle(R.string.nav_option_cine);
                    args.putInt(MainListFragment.CHANNELS_TO_SHOW,ApplicationConstants.CINE_GROUP_ID);
                    checkFragmentToUse(ApplicationConstants.CINE_MENU_ID,args);
                break;
            case ApplicationConstants.PERSONAL_MENU_ID:
                if (getSupportActionBar() != null)
                    getSupportActionBar().setTitle(R.string.nav_option_personal);
                args.putInt(MainListFragment.CHANNELS_TO_SHOW,ApplicationConstants.PERSONAL_GROUP_ID);
                checkFragmentToUse(ApplicationConstants.PERSONAL_MENU_ID,args);
                break;
            case ApplicationConstants.CINEONLINE_MENU_ID:
                if (getSupportActionBar() != null)
                    getSupportActionBar().setTitle(R.string.nav_option_cineonline);
                    args.putInt(MainListFragment.CHANNELS_TO_SHOW,ApplicationConstants.CINEONLINE_GROUP_ID);
                    checkFragmentToUse(ApplicationConstants.CINEONLINE_MENU_ID,args);
                break;
            case ApplicationConstants.ADULTOS_MENU_ID:
                if (getSupportActionBar() != null)
                    getSupportActionBar().setTitle(R.string.nav_option_adultos);
                args.putInt(MainListFragment.CHANNELS_TO_SHOW,ApplicationConstants.ADULTOS_GROUP_ID);
                checkFragmentToUse(ApplicationConstants.ADULTOS_MENU_ID,args);
                break;
        }

        drawer.closeDrawer();
    }

    public void setNavPosition(Bundle savedInstanceState) {
            drawer.setSelection(DEFAULT_POSITION, true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.i(TAG, "onSaveInstanceState");
        Log.i(TAG + "SaveinstancePos", String.valueOf(FragmentPosition));
//        outState.putInt(FRAGMENT_ID, FragmentPosition);
//        mDrawer.saveInstanceState(outState);
//        outState.putInt(LAST_SEARCH_SECTION, searchSection);
//
//        outState.putInt(MEDIA_TYPE_SAVED, mTypeSaved);
//        outState.putString(MEDIA_SAVED, mMediaSaved);
//        outState.putString(CATEGORY, mCategory);
//        outState.putBoolean(IS_IN_M_DETAILS, isInMediaDetails);
//        outState.putBoolean(IS_IN_C_DETAILS, isInChannelDetails);
//        outState.putBoolean(LOCATION, gotLocation);
//        outState.putBoolean(FULL_LOADED,fullLoaded);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
//        mTypeSaved = savedInstanceState.getInt(MEDIA_TYPE_SAVED);
//        mMediaSaved = savedInstanceState.getString(MEDIA_SAVED);
//        mCategory = savedInstanceState.getString(CATEGORY);
//        isInMediaDetails = savedInstanceState.getBoolean(IS_IN_M_DETAILS);
//        isInChannelDetails = savedInstanceState.getBoolean(IS_IN_C_DETAILS);
//        fullLoaded = savedInstanceState.getBoolean(FULL_LOADED);
//        if(isInMediaDetails||isInChannelDetails){
//            mDrawer.getActionBarDrawerToggle().setDrawerIndicatorEnabled(false);
//        }
        setNavPosition(savedInstanceState);

        Log.i(TAG, "onRestoreInstanceState");

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(searchChannelUri!=null) {
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
    public void onPlayButtonClicked(String url) {
        Intent intent = new Intent(this,VideoPlayerActivity.class);
        intent.putExtra(ApplicationConstants.VIDEO_URL,url);
        startActivityForResult(intent, ApplicationConstants.VIDEO_PROCESS);
    }

    @Override
    public void showInteractiveMsg(String msg) {
        Snackbar snackbar = Snackbar
                .make(content, msg, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    private void cleanBackStack() {
        detailsFragment = (DetailChannelFragment) fm.findFragmentByTag(DetailChannelFragment.FRAGMENT_TAG);
        if (detailsFragment != null) {
            fm.popBackStackImmediate();
            fm.beginTransaction().remove(detailsFragment).commit();
        }
    }

    @Override
    public void onBackStackChanged() {
        isInDetailFragment = false;
        if(fm.getBackStackEntryCount()>0) {

            for (int i = 0; i < fm.getBackStackEntryCount(); i++) {

                if (DetailChannelFragment.FRAGMENT_TAG.equals(fm.getBackStackEntryAt(i).getName())) {
                    isInDetailFragment = true;
                    drawer.getActionBarDrawerToggle().setDrawerIndicatorEnabled(false);
                }
            }
        }else{
            drawer.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);
        }

    }


    private void checkFragmentToUse(int id,Bundle args){
        if(id == ApplicationConstants.FAVORITOS_MENU_ID){
            if(favoriteFragment==null){
                favoriteFragment = new FavoritesFragment();
            }else {
                favoriteFragment.onOrderChanged(args);
            }
            fm.beginTransaction().replace(R.id.rootLayout, favoriteFragment,
                    favoriteFragment.FRAGMENT_TAG).commit();
        } else {
            if(mainListFragment==null){
                mainListFragment = MainListFragment.newInstance(args);
            }else {
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
            if(resultCode == Activity.RESULT_OK) {
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
        new AsyncTaskHelper((Activity) ctx, request,(new _Callback(){
            @Override
            public void execute(JsonNode response, Context context) {
                if(response.get("Success")!= null && response.get("Success").asBoolean() == true) {
                    Channels channels = new Channels();
                    try {
                        ObjectMapper mapper = new ObjectMapper();
//                                    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
                        TypeReference<List<User>> typeRef = new TypeReference<List<User>>(){};
                        List<User> list = mapper.readValue(response.get("User").traverse(), typeRef);
                        usr = BroadcastvSQLUtil.insertUserIfNotExists(ctx,list);
                    }  catch (IOException e) {
                        Log.e(TAG, "execute: " + e.toString());
                    }
                    channels.setDeviceToken(response.get("Token").asText());
                    final Request<Channels> rqt = new Request<Channels>();
                    rqt.setRqt(channels);
                    new AsyncTaskHelper((Activity) ctx,rqt,(new _Callback(){
                        @Override
                        public void execute(JsonNode response, Context context) {
                            if(response.get("Success")!= null && response.get("Success").asBoolean() == true) {
                                try {
                                    ObjectMapper mapper = new ObjectMapper();
//                                    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
                                    TypeReference<List<ChannelsResponse>> typeRef = new TypeReference<List<ChannelsResponse>>(){};
                                    List<ChannelsResponse> list = mapper.readValue(response.get("Channels").traverse(), typeRef);
                                    BroadcastvSQLUtil.insertChannelIfNotExists(ctx,list);
                                }  catch (IOException e) {
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
                            HttpEntity<?> requestEntity = new HttpEntity<Request>(rqt,headers);
                            return requestEntity;
                        }
                    }),"/ObtenerCanal").execute();
                    progress.hide();
                }else{
                    progress.hide();
                    Toast.makeText(ctx,
                            context.getResources().getString(R.string.error_network),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void Failure(JsonNode response, Context context) {
                Log.e(TAG, String.valueOf(response));
                progress.hide();
            }

            @Override
            public HttpEntity<?> setHeader(Request request, HttpHeaders headers) {
                HttpEntity<?> requestEntity = new HttpEntity<Request>(request,headers);
                return requestEntity;
            }
        }),"/ObtenerToken").execute();
    }

    @Override
    public void onClick(View v) {
         if(v instanceof FloatingActionButton) {
             mFab.setClickable(false); // Avoid naughty guys clicking FAB again and again...
             int[] location = new int[2];
             mFab.getLocationOnScreen(location);
             Log.e(TAG, "onClick: "+ (mFab.getWidth() / 2));
             Log.e(TAG, "onClick: "+ (mFab.getHeight() / 2));
             location[0] += mFab.getWidth() / 2;
             location[1] += mFab.getHeight() / 2;

             Log.e("LocationArray", "onClick: "+ String.valueOf(location[0]));
             Log.e("LocationArray", "onClick: "+ String.valueOf(location[1]));
             final Intent intent = new Intent(MainListActivity.this, SearchActivity.class);

             mRevealView.setVisibility(View.VISIBLE);
             mRevealLayout.setVisibility(View.VISIBLE);

             mRevealLayout.show(location[0],location[1]); // Expand from center of FAB. Actually, it just plays reveal animation.
             mFab.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     startActivity(intent);
                     /**
                      * Without using R.anim.hold, the screen will flash because of transition
                      * of Activities.
                      */
                     overridePendingTransition(0, R.anim.hold);
                 }
             }, 600); // 600 is default duration of reveal animation in RevealLayout
             mFab.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     mFab.setClickable(true);
                     mRevealLayout.setVisibility(View.INVISIBLE);
                     mRevealView.setVisibility(View.INVISIBLE);
                 }
             }, 960);
         }
    }

    public void goToChannelDetails(Uri elementUri) {

        detailsFragment =(DetailChannelFragment) fm.findFragmentByTag(DetailChannelFragment.FRAGMENT_TAG);
        if(detailsFragment!=null){
            if(detailsFragment.getId()==R.id.rootLayout){
                cleanBackStack();
            }
        }
//        isInMediaDetails = true;

        detailsFragment = (DetailChannelFragment)fm.findFragmentByTag(DetailChannelFragment.FRAGMENT_TAG);
        if(detailsFragment==null){
            Bundle args = new Bundle();
            args.putString(DetailChannelFragment.DETAIL_URI,String.valueOf(elementUri));
            detailsFragment = DetailChannelFragment.newInstance(args);
        }

        drawer.getActionBarDrawerToggle().setDrawerIndicatorEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(detailsFragment.getId()!=R.id.rootLayout) {
            fm.beginTransaction().replace(R.id.rootLayout, detailsFragment,
                    DetailChannelFragment.FRAGMENT_TAG)
                    .addToBackStack(DetailChannelFragment.FRAGMENT_TAG)
                    .commit();
            fm.executePendingTransactions();
        }
    }
}
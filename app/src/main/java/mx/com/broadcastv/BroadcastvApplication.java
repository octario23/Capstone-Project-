package mx.com.broadcastv;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import com.facebook.stetho.Stetho;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

public class BroadcastvApplication extends Application {

    public static Context mAppContext;
    private static BroadcastvApplication sInstance;
    private static String userId = "GUEST@JBOX.ONLINE";
    public Tracker mTracker;

    public static BroadcastvApplication getInstance() {
        return sInstance;
    }

    public static int dpToPx(int valueInDP) {
        return (int) (valueInDP
                * mAppContext.getResources().getDisplayMetrics().density + 0.5f);
    }

    public static String getUserId() {
        return userId;
    }

    public static void setUserId(String userID) {
        userId = userID;
    }

    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
        sInstance = this;
        BroadcastvApplication.mAppContext = getApplicationContext();
    }

    public boolean isTablet() {
        return (getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public void startTracking() {
        if (mTracker == null) {
            GoogleAnalytics ga = GoogleAnalytics.getInstance(this);
            mTracker = ga.newTracker(R.xml.track_app);
            ga.enableAutoActivityReports(this);
        }
    }

    public Tracker getTracker() {
        startTracking();

        return mTracker;
    }

}
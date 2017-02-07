package mx.com.broadcastv;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import com.facebook.stetho.Stetho;

public class    BroadcastvApplication extends Application {

    private static BroadcastvApplication sInstance;
    public static Context mAppContext;
    private static String userId = "1";


    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
        sInstance = this;
        BroadcastvApplication.mAppContext = getApplicationContext();
    }

    public static BroadcastvApplication getInstance() {
        return sInstance;
    }

    public boolean isTablet() {
        return (getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
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

}
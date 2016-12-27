package mx.com.broadcastv.util;

import android.app.ProgressDialog;
import android.content.Context;

public class Utils {
    public static ProgressDialog broadcastvLoading(Context context){
        ProgressDialog progress = new ProgressDialog(context){
            @Override
            public void onBackPressed() {
                /** dismiss the progress bar and clean up here **/
            }
        };
        progress.setCanceledOnTouchOutside(false);
        progress.setTitle("BroadcasTV");
        progress.setMessage("Loading...");
//        progress.show();
        return progress;
    }
}

package mx.com.broadcastv.util;

import android.app.ProgressDialog;
import android.content.Context;

public class Utils {
    public static ProgressDialog broadcastvLoading(Context context) {

        final ProgressDialog progress = new ProgressDialog(context) {
            @Override
            public void onBackPressed() {
                /** dismiss the progress bar and clean up here **/
                this.dismiss();
            }
        };
        progress.setCanceledOnTouchOutside(false);
        progress.setTitle("BroadcasTV");
        progress.setMessage("Loading...");
        progress.setCancelable(true);
        return progress;
    }
}

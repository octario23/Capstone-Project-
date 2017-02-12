package mx.com.broadcastv.ui.interfaces;

import android.net.Uri;


public interface OnClickCallback {
    void onItemSelected(Uri elementUri);

    void onPlayButtonClicked(String url, String ChannelName);

    void showInteractiveMsg(String msg);

    void closeAddChannelForm();
}

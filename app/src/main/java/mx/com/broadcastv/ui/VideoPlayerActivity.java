package mx.com.broadcastv.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.VideoView;
import android.net.Uri;
import android.widget.MediaController;

import mx.com.broadcastv.R;
import mx.com.broadcastv.util.ApplicationConstants;

import static android.view.View.getDefaultSize;

public class VideoPlayerActivity extends AppCompatActivity {

    private String url;
    private int lastOrientation = 0;
    private VideoView vidView;
    private int mVideoWidth ;
    private int mVideoHeight ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getIntent() !=null){
            url = getIntent().getStringExtra(ApplicationConstants.VIDEO_URL);
        }
        mVideoWidth = mVideoHeight = 0;
        setContentView(R.layout.activity_video_player);
        vidView = (VideoView)findViewById(R.id.myVideo);
        MediaController vidControl = new MediaController(this);
        vidControl.setAnchorView(vidView);
        vidView.setMediaController(vidControl);
        Uri vidUri = Uri.parse(url);
        vidView.setVideoURI(vidUri);
        vidView.start();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        Intent intent = new Intent();

        switch (requestCode) {
            case ApplicationConstants.VIDEO_PROCESS:
                if(resultCode == Activity.RESULT_OK ){
                    intent.putExtra("result", " VIDEO_PROCESS finished");
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
                break;

            default:
                intent.putExtra("result", "VIDEO_PROCESS failed");
                setResult(Activity.RESULT_CANCELED, intent);
                break;

        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if (vidView != null) {
//            vidView.stopPlayback();
//            vidView = null;
//        }
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        if (vidView != null) {
//            vidView.stopPlayback();
//        }
//    }
}
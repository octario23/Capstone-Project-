package mx.com.broadcastv.util;


import android.media.MediaPlayer;
import android.net.Uri;
import android.view.View;
import android.widget.VideoView;

public class VideoPlayerHelper {
    private static VideoPlayerHelper sInstance = null;

    public VideoView videoView;


    private VideoPlayerHelper() {
    }

    public static VideoPlayerHelper getInstance() {
        if (sInstance == null) {
            sInstance = new VideoPlayerHelper();
        }
        return sInstance;
    }


    public boolean isVideoSetup() {
        return videoView != null;
    }

    public int getVisibility() {

        return videoView.getVisibility();

    }

    public void setVisibility(int visibility) {

        videoView.setVisibility(visibility);

    }

    public boolean requestFocus() {

        return videoView.requestFocus();

    }

    public boolean isPlaying() {

        return videoView.isPlaying();

    }

    public int getDuration() {

        return videoView.getDuration();

    }

    public int getCurrentPosition() {

        return videoView.getCurrentPosition();

    }

    public void start() {

        videoView.start();

    }

    public void pause() {

        videoView.pause();

    }

    public void seekTo(int pos) {

        videoView.seekTo(pos);

    }

    public void stopPlayback() {

        videoView.stopPlayback();

    }

    public void setVideoURI(Uri uri) {

        videoView.setVideoURI(uri);

    }

    public void setVideoPath(String path) {

        videoView.setVideoPath(path);

    }

    public void setOnErrorListener(MediaPlayer.OnErrorListener errorListener) {

        videoView.setOnErrorListener(errorListener);

    }


    public void setOnPreparedListener(MediaPlayer.OnPreparedListener l) {

        videoView.setOnPreparedListener(l);

    }

    public void setOnCompletionListener(MediaPlayer.OnCompletionListener l) {

        videoView.setOnCompletionListener(l);

    }

    public void setOnTouchListener(View.OnTouchListener l) {

        videoView.setOnTouchListener(l);

    }
}

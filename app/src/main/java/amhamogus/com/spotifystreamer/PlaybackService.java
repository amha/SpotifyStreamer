package amhamogus.com.spotifystreamer;

import android.app.IntentService;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class PlaybackService extends IntentService
        implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    private final String TAG = "debug-am";

    private int seekTime = -1;
    private MediaPlayer mediaPlayer;
    protected final IBinder mBinder = new MyBinder();

    public PlaybackService() {
        super("PlaybackService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {

            // If a user decides to play another track during active playback,
            // stop the current track before starting the next track.
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }

            String URL = intent.getStringExtra("PREVIEW");
            Log.d("PLAYBACK", "URL: " + URL);
            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setDataSource(URL);
                mediaPlayer.setOnPreparedListener(this);
                mediaPlayer.setOnCompletionListener(this);
                mediaPlayer.prepareAsync();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if (seekTime != -1) {
            // Resume playback from the paused state.
            mediaPlayer.seekTo(seekTime);
            seekTime = -1;
        }
        mediaPlayer.start();

        Intent playbackIntent = new Intent();
        playbackIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        playbackIntent.setAction("amhamogus.com.spotifystreamer.Tomato");
        playbackIntent.putExtra("Foo", "Bar");
        sendBroadcast(playbackIntent);
    }

    public void setSeekTime(int seekTime) {
        mediaPlayer.seekTo(seekTime);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.d(TAG, "Media player in error.");
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        // Releasing the media player.
        Log.d(TAG, "Done playing the song");
        seekTime = -1;
        mp.release();
        mediaPlayer = null;
    }

    public class MyBinder extends Binder {
        public PlaybackService getService() {
            return PlaybackService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public boolean isMediaPlaying() {
        return mediaPlayer.isPlaying();
    }

    public boolean isMediaplayerNull(){
        if(mediaPlayer == null){
            return true;
        }
        else
        {
            return false;
        }
    }
    public void pause() {
        seekTime = mediaPlayer.getCurrentPosition();
        mediaPlayer.pause();
    }

    public void stop() {
        mediaPlayer.stop();
    }

    public void seekTime(int seekTime) {
        if (seekTime < 0 || seekTime > 31) {
            mediaPlayer.seekTo(seekTime);
        }
    }
}

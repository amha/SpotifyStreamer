package amhamogus.com.spotifystreamer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.FrameLayout;

import java.util.ArrayList;

import amhamogus.com.spotifystreamer.activities.TopTracksActivity;
import amhamogus.com.spotifystreamer.fragments.ArtistSearchFragment;
import amhamogus.com.spotifystreamer.fragments.PlaybackFragment;
import amhamogus.com.spotifystreamer.fragments.TopTracksFragment;
import amhamogus.com.spotifystreamer.model.MyTrack;

/**
 * Main entry point into Spotify Streamer - Stage 1.
 */
public class MainActivity extends Activity implements ArtistSearchFragment.OnFragmentInteractionListener,
        TopTracksFragment.OnTopTrackSelectedListener, PlaybackFragment.PlaybackEventListener {

    private ArtistSearchFragment artistSearchFragment;
    private TopTracksFragment topTracksFragment;
    private PlaybackFragment playbackFragment;
    boolean inTabletMode = false;
    private FrameLayout frame;

    private PlaybackService mService;
    private Boolean isBound = false;
    private PlaybackFragment fragment;
    ArrayList<MyTrack> trackList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check for network connectivity.
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getApplicationContext()
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = connectivityManager.getActiveNetworkInfo();


        if (network != null && network.isConnectedOrConnecting()) {
            if (findViewById(R.id.tablet_frame) != null) {
                frame = (FrameLayout) findViewById(R.id.tablet_frame);
                inTabletMode = true;
            } else {
                inTabletMode = false;
                if (artistSearchFragment != null) {
                    // Reading data from saved instance state
                    artistSearchFragment =
                            (ArtistSearchFragment) getFragmentManager()
                                    .findFragmentByTag("artist_fragment");
                } else {
                    artistSearchFragment = ArtistSearchFragment.newInstance();
                }

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.artist_fragment, artistSearchFragment, "artist_fragment");
                transaction.commit();
            }
        } else {
            // Show alert dialog because there is no internet connectivity.
            AlertDialog.Builder alertContent = new AlertDialog.Builder(this);
            alertContent.setTitle(R.string.no_internet_title)
                    .setMessage(R.string.no_internet_body)
                    .setPositiveButton(R.string.no_internet_positive_button,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                                }
                            })
                    .setNegativeButton(R.string.no_internet_negative_button, null);

            // Show the alert.
            AlertDialog alertDialog = alertContent.create();
            alertDialog.show();
        }

    }

    public void onFragmentInteraction(Bundle bundle) {
        if (inTabletMode) {

            topTracksFragment =
                    TopTracksFragment.newInstance(
                            bundle.getString("ARTIST_ID"),
                            bundle.getString("ARTIST_NAME"));
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.tablet_frame, topTracksFragment, "tracks_fragment");
            transaction.commit();

        } else {
            Intent intent =
                    new Intent(getApplicationContext(), TopTracksActivity.class);
            intent.putExtras(bundle);
            startActivity(intent, bundle);
        }
    }

    public void onTopTrackSelected(Bundle trackDetails) {
        if (inTabletMode) {
            ArrayList<MyTrack> tracks = trackDetails.getParcelableArrayList("trackList");
            playbackFragment = PlaybackFragment.newInstance(
                    trackDetails.getString("trackID"),
                    tracks,
                    trackDetails.getInt("playListNumber", 0)
            );
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            playbackFragment.show(transaction, "playback_fragment");

        } else {
            Intent intent = new Intent(getApplicationContext(), PlaybackActivity.class);
            intent.putExtras(trackDetails);
            startActivity(intent);
        }
    }

    public void passTrackPreview(String previewURL) {
        Bundle args = new Bundle();
        args.putString("PREVIEW", previewURL);

        Log.d("Preview URL", "URL: " + previewURL);
        Intent intent = new Intent();
        intent.putExtras(args);

        mService.onHandleIntent(intent);
    }

    public void pauseTrack() {
        if (mService.isMediaPlaying()) {
            mService.pause();
        }
    }

    public void stopTrack() {
        if (mService.isMediaPlaying()) {
            mService.stop();
        }
    }

    public void seekTo(int seekPosition) {
        mService.setSeekTime(seekPosition);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (inTabletMode) {
            Intent intent = new Intent(this, PlaybackService.class);
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PlaybackService.MyBinder binder = (PlaybackService.MyBinder) service;
            mService = binder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            isBound = false;
        }
    };
}
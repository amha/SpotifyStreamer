/*
 * Copyright (C) 2015 Amha Mogus amha.mogus@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package amhamogus.com.spotifystreamer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
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
 * Main entry point into Spotify Streamer app.
 *
 * @author amhamogus
 */
public class MainActivity extends Activity implements ArtistSearchFragment.OnFragmentInteractionListener,
        TopTracksFragment.OnTopTrackSelectedListener, PlaybackFragment.PlaybackEventListener {

    // fragments to drive master detail experiene
    private ArtistSearchFragment artistSearchFragment;
    private TopTracksFragment topTracksFragment;
    private PlaybackFragment playbackFragment;

    // tablet related member variables
    boolean inTabletMode = false;


    // service related member variables
    private PlaybackService mService;
    private Boolean isBound = false;

    private String ARTIST_ID;
    private String ARTIST_NAME;

    //ArrayList<MyTrack> trackList;

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
                // we're in tablet mode
                inTabletMode = true;

                if (savedInstanceState != null) {

                    FragmentTransaction transaction = getFragmentManager().beginTransaction();

                    if (getFragmentManager()
                            .findFragmentByTag("artist_fragment") != null) {
                        artistSearchFragment = (ArtistSearchFragment) getFragmentManager()
                                .findFragmentByTag("artist_fragment");
                    } else {
                        artistSearchFragment = ArtistSearchFragment.newInstance();
                    }
                    transaction.replace(R.id.search_frame, artistSearchFragment, "artist_fragment");

                    if (getFragmentManager()
                            .findFragmentByTag("tracks_fragment") != null) {

                        //check if this exists
                        topTracksFragment = (TopTracksFragment) getFragmentManager()
                                .findFragmentByTag("tracks_fragment");

                        transaction.replace(R.id.tablet_frame, topTracksFragment, "tracks_fragment");
                        Log.d("AMHA_FRAGMENT", "track_fragment is not null");
                    }
                    transaction.commit();

                } else {
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    // nothing saved, create new search fragment
                    artistSearchFragment = ArtistSearchFragment.newInstance();
                    transaction.replace(R.id.search_frame, artistSearchFragment, "artist_fragment");
                    transaction.commit();
                }

            } else {
                // we're displaying on a mobile phone
                inTabletMode = false;

                if (getFragmentManager().findFragmentByTag("artist_fragment") != null) {
                    artistSearchFragment = (ArtistSearchFragment) getFragmentManager()
                            .findFragmentByTag("artist_fragment");
                } else {
                    artistSearchFragment = ArtistSearchFragment.newInstance();
                }
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                // commit changes
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

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }


    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);
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

    //############################## Playback Handlers ####################################//

    public void onFragmentInteraction(Bundle bundle) {
        if (inTabletMode) {
            topTracksFragment =
                    TopTracksFragment.newInstance(
                            bundle.getString("ARTIST_ID"),
                            bundle.getString("ARTIST_NAME"));

            ARTIST_ID = bundle.getString("ARTIST_ID");
            ARTIST_NAME = bundle.getString("ARTIST_NAME");

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
        mService.stop();
    }

    public void seekTo(int seekPosition) {
        mService.setSeekTime(seekPosition);
    }

    @Override
    public boolean isMediaplayerNull() {
        if (mService.isMediaplayerNull()) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * Bind to {@link PlaybackService} to handle all requests
     * for music playback.
     */
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
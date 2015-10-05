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
package amhamogus.com.spotifystreamer.fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import amhamogus.com.spotifystreamer.R;
import amhamogus.com.spotifystreamer.model.MyTrack;

/**
 * A simple {@link DialogFragment} subclass that displays the playback screen.
 * Activities that use this must implement
 * {@link amhamogus.com.spotifystreamer.fragments.PlaybackFragment.PlaybackEventListener}
 * to handle playback events.
 * To use this class call {@link PlaybackFragment#newInstance(String, ArrayList, int)}
 * factory method.
 */
public class PlaybackFragment extends DialogFragment {


    private BroadcastReceiver mBroadcastReceiver;

    private Handler handler;
    private Runnable runnable;

    // Flags
    private static final String TRACK_PARAM = "trackID";
    private static final String TRACK_LIST_PARAM = "trackList";
    private static final String TRACK_NUMBER_PARAM = "playListNumber";
    private static String currentTrackID;

    PlaybackEventListener mCallback;

    // Playback controls
    private ImageButton playButton;
    private ImageButton pauseButton;
    private ImageButton nextButton;
    private ImageButton previousButton;
    private SeekBar seekBar;
    private TextView playbackSeekValue;

    // Meta-data about the current track
//    private TextView albumName;
//    private TextView artistName;
//    private TextView trackName;
    private ImageView trackImage;

    // Top 10 tracks for a spotify musician.
    private ArrayList<MyTrack> trackList;

    protected MyTrack currentTrack;
    protected int trackNumber;

    /**
     * Factory method for creating Playback Fragments.
     *
     * @param trackID   The track id for a spotify track.
     * @param trackList {@link ArrayList} of {@link MyTrack} objects.
     * @param position  Integer value between 0-9 that is the current, user selected, track.
     * @return
     */
    public static PlaybackFragment newInstance(String trackID, ArrayList<MyTrack> trackList, int position) {
        currentTrackID = trackID;

        Log.d("PLAYBACK FRAGMENT", "Position = " + position);

        PlaybackFragment fragment = new PlaybackFragment();
        fragment.setRetainInstance(true);

        Bundle args = new Bundle();
        args.putString(TRACK_PARAM, trackID);
        args.putParcelableArrayList(TRACK_LIST_PARAM, trackList);
        args.putInt(TRACK_NUMBER_PARAM, position);
        fragment.setArguments(args);
        fragment.setRetainInstance(true);
        return fragment;
    }

    public PlaybackFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        trackNumber = getArguments().getInt("playListNumber");
        trackList = getArguments().getParcelableArrayList(TRACK_LIST_PARAM);

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                handler = new Handler();
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        int seekValue = seekBar.getProgress();
                        if (seekValue < 30) {
                            seekBar.setProgress(seekValue + 1);
                            playbackSeekValue.setText(seekBar.getProgress() + "");
                        }
                        handler.postDelayed(this, 1000);
                    }
                };
                handler.postDelayed(runnable, 1000);
            }
        };

        // Registering intent filter to receive playback initiated events
        IntentFilter filter =
                new IntentFilter("amhamogus.com.spotifystreamer.Tomato");

        getActivity().registerReceiver(mBroadcastReceiver, filter);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_track_playback, container, false);

        // Reference UI Elements associated with a current track.
//        artistName = (TextView) fragment.findViewById(R.id.playback_artist_name);
//        albumName = (TextView) fragment.findViewById(R.id.playback_album_name);
//        trackName = (TextView) fragment.findViewById(R.id.playback_track_name);
        trackImage = (ImageView) fragment.findViewById(R.id.playback_image);
        playbackSeekValue = (TextView) fragment.findViewById(R.id.playback_seek_value);
        seekBar = (SeekBar) fragment.findViewById(R.id.seek_bar);

        // handle seekbar interactions
        seekBar.setSaveEnabled(true);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mCallback.seekTo(seekBar.getProgress());
            }
        });

        // Reference Playback controls.
        playButton = (ImageButton) fragment
                .findViewById(R.id.imageButton);

        pauseButton = (ImageButton) fragment
                .findViewById(R.id.pause_button);

        nextButton = (ImageButton) fragment
                .findViewById(R.id.next_track_button);

        previousButton = (ImageButton) fragment
                .findViewById(R.id.previous_track_button);

        // Toggle visibility of pause button
        pauseButton.setVisibility(View.INVISIBLE);
        updateDisplay(currentTrackID);

        // Handling play button press
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // the user has pressed the play button so hide
                // the play button and show pause button
                playButton.setVisibility(View.INVISIBLE);
                pauseButton.setVisibility(View.VISIBLE);

                // request playback from the the playback servers
                mCallback.passTrackPreview(currentTrack.getPreviewURL());
            }
        });

        // Handling pause button press
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // the user has pressed the pause button so hide
                // the pause button, show the play button
                playButton.setVisibility(View.VISIBLE);
                pauseButton.setVisibility(View.INVISIBLE);

                // send a pause request to the playback service.
                mCallback.pauseTrack();
                handler.removeCallbacks(runnable);
            }
        });

        // Handling next button press
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback.isMediaplayerNull() == false) {
                    // The media player is in playback (e.g. not null)
                    // so we stop the current song.
                    mCallback.stopTrack();
                }

                handler.removeCallbacks(runnable);

                // Set seekbar to 0
                playbackSeekValue.setText(0 + "");
                seekBar.setProgress(0);

                // If we're at the end of the top 10 track list
                // set the track number to the first item on the list.
                if (trackNumber > 8) {
                    trackNumber = 0;
                } else {
                    trackNumber += 1;
                }

                currentTrack = trackList.get(trackNumber);
                updateDisplay(trackNumber + "");

                playButton.setVisibility(View.INVISIBLE);
                pauseButton.setVisibility(View.VISIBLE);
                mCallback.passTrackPreview(currentTrack.getPreviewURL());
            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // The media player is in playback (e.g. not null)
                // so we stop the current song.
                if (mCallback.isMediaplayerNull() == false) {
                    mCallback.stopTrack();
                }
                handler.removeCallbacks(runnable);
                playbackSeekValue.setText(0 + "");

                // If we're at the start of the top 10 track list set the
                // track number to last last item on the track list.
                if (trackNumber < 1) {
                    trackNumber = 9;
                } else {
                    trackNumber -= 1;
                }

                // Set seekbar to 0
                seekBar.setProgress(0);
                currentTrack = trackList.get(trackNumber);

                updateDisplay(trackNumber + "");

                // the user has pressed the previous button so hide
                // the play button, show the pause button
                playButton.setVisibility(View.INVISIBLE);
                pauseButton.setVisibility(View.VISIBLE);

                // request 30 second preview from playback service
                mCallback.passTrackPreview(currentTrack.getPreviewURL());
            }
        });

        // play current song
        playButton.performClick();

        return fragment;
    }

    @Override
    public void onPause() {
        if (mBroadcastReceiver != null) {
            getActivity().unregisterReceiver(mBroadcastReceiver);
            mBroadcastReceiver = null;
        }
        super.onPause();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (PlaybackEventListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement PlaybackEventListener");
        }
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance()) {
            getDialog().setDismissMessage(null);
        }
        super.onDestroyView();
    }

    /**
     * Utility method that's responsible for updating the Playback
     * Fragments UI.
     */
    private void updateDisplay(String currentTrackID) {
        if (currentTrackID != null) {
            currentTrack = trackList.get(trackNumber);

            if (getDialog() != null) {
                // if this fragment is displayed as a dialog
                // set title to track and artist name
                getDialog()
                        .setTitle(currentTrack.getAlbumName()
                                        + " | " + currentTrack.getArtistName());
                //TODO: add subtitle text to the fragment
            } else {
                // we're running within the fragment activity
                // so we update the activity title and subtitle
                getActivity()
                        .setTitle(currentTrack.getTrackName());
                getActivity()
                        .getActionBar()
                        .setSubtitle(currentTrack.getAlbumName() + " | " +
                                currentTrack.getArtistName());
            }

            // Bind current track data with view elements.
//            artistName.setText(currentTrack.getArtistName());
//            albumName.setText(currentTrack.getAlbumName());
//            trackName.setText(currentTrack.getTrackName());

            Picasso.with(getActivity())
                    .load(currentTrack.getAlbumImageUrl()).into(trackImage);
        } else {
            Log.d("Error", "current track id is null in update display method");
        }
    }

    /**
     * Activities that use {@link PlaybackFragment} must implement this interface.
     * The interface methods depend on {@link amhamogus.com.spotifystreamer.PlaybackService}
     */
    public interface PlaybackEventListener {

        /**
         * Sends a playback request to {@link amhamogus.com.spotifystreamer.PlaybackService}
         *
         * @param previewURL URL of a Spotify Preview Track
         */
        public void passTrackPreview(String previewURL);

        /**
         * Sends a pause request to {@link amhamogus.com.spotifystreamer.PlaybackService}
         */
        public void pauseTrack();

        /**
         * Sends a stop request to {@link amhamogus.com.spotifystreamer.PlaybackService}
         */
        public void stopTrack();

        public void seekTo(int seekPosition);

        public boolean isMediaplayerNull();
    }

}

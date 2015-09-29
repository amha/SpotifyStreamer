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
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import amhamogus.com.spotifystreamer.R;
import amhamogus.com.spotifystreamer.model.MyTrack;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaybackFragment extends DialogFragment {

    private BroadcastReceiver mBroadcastReceiver;

    private Handler handler;
    private Runnable runnable;

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

    // Variables for current track
    private TextView albumName;
    private TextView artistName;
    private TextView trackName;
    private ImageView trackImage;
    protected MyTrack currentTrack;
    protected int trackNumber;
    private ArrayList<MyTrack> trackList;
    private TextView playbackSeekValue;

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
        //    fragment.setRetainInstance(true);
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
        artistName = (TextView) fragment.findViewById(R.id.playback_artist_name);
        albumName = (TextView) fragment.findViewById(R.id.playback_album_name);
        trackName = (TextView) fragment.findViewById(R.id.playback_track_name);
        trackImage = (ImageView) fragment.findViewById(R.id.playback_image);
        playbackSeekValue = (TextView) fragment.findViewById(R.id.playback_seek_value);

        // Seekbar interactions.
        seekBar = (SeekBar) fragment.findViewById(R.id.seek_bar);
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
        playButton = (ImageButton) fragment.findViewById(R.id.imageButton);
        pauseButton = (ImageButton) fragment.findViewById(R.id.pause_button);
        nextButton = (ImageButton) fragment.findViewById(R.id.next_track_button);
        previousButton = (ImageButton) fragment.findViewById(R.id.previous_track_button);

        pauseButton.setVisibility(View.INVISIBLE);
        updateDisplay(currentTrackID);

//        if(seekBar.getProgress() == 0) {
//            mCallback.passTrackPreview(currentTrack.getPreviewURL());
//        }

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playButton.setVisibility(View.INVISIBLE);
                pauseButton.setVisibility(View.VISIBLE);
                mCallback.passTrackPreview(currentTrack.getPreviewURL());
            }
        });
        playButton.callOnClick();

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playButton.setVisibility(View.VISIBLE);
                pauseButton.setVisibility(View.INVISIBLE);
                mCallback.pauseTrack();
                handler.removeCallbacks(runnable);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCallback.isMediaplayerNull() == false) {
                    // Stop current track
                    mCallback.stopTrack();
                }
                handler.removeCallbacks(runnable);
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
                // Stop current track
                if(mCallback.isMediaplayerNull() == false) {
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

                seekBar.setProgress(0);
                currentTrack = trackList.get(trackNumber);
                updateDisplay(trackNumber + "");
                playButton.setVisibility(View.INVISIBLE);
                pauseButton.setVisibility(View.VISIBLE);
                mCallback.passTrackPreview(currentTrack.getPreviewURL());
            }
        });
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

    private void updateDisplay(String currentTrackID) {
        if (currentTrackID != null) {

            currentTrack = trackList.get(trackNumber);
            getActivity().setTitle(currentTrack.getArtistName());

            // Bind current track data with view elements.
            artistName.setText(currentTrack.getArtistName());
            albumName.setText(currentTrack.getAlbumName());
            trackName.setText(currentTrack.getTrackName());

            Picasso.with(getActivity())
                    .load(currentTrack.getAlbumImageUrl()).into(trackImage);
        } else {
            Log.d("Error", "current track id is null in update display method");
        }
    }

    public interface PlaybackEventListener {
        public void passTrackPreview(String previewURL);

        public void pauseTrack();

        public void seekTo(int seekPosition);

        public void stopTrack();

        public boolean isMediaplayerNull();
    }

}

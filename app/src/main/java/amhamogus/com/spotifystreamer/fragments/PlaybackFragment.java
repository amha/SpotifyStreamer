package amhamogus.com.spotifystreamer.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import amhamogus.com.spotifystreamer.R;
import amhamogus.com.spotifystreamer.model.MyTrack;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaybackFragment extends DialogFragment {

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

    // Variables for current track
    private TextView albumName;
    private TextView artistName;
    private TextView trackName;
    private ImageView trackImage;
    protected MyTrack currentTrack;
    protected int trackNumber;
    private ArrayList<MyTrack> trackList;

    public static PlaybackFragment newInstance(String trackID, ArrayList<MyTrack> trackList, int position) {
        currentTrackID = trackID;

        Log.d("PLAYBACK FRAGMENT", "Position = " + position);

        PlaybackFragment fragment = new PlaybackFragment();

        Bundle args = new Bundle();
        args.putString(TRACK_PARAM, trackID);
        args.putParcelableArrayList(TRACK_LIST_PARAM, trackList);
        args.putInt(TRACK_NUMBER_PARAM, position);
        fragment.setArguments(args);
        return fragment;
    }

    public PlaybackFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        trackNumber = getArguments().getInt("playListNumber");
        trackList = getArguments().getParcelableArrayList(TRACK_LIST_PARAM);

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

        // Reference Playback controls.
        playButton = (ImageButton) fragment.findViewById(R.id.imageButton);
        pauseButton = (ImageButton) fragment.findViewById(R.id.pause_button);
        nextButton = (ImageButton) fragment.findViewById(R.id.next_track_button);
        previousButton = (ImageButton) fragment.findViewById(R.id.previous_track_button);

        pauseButton.setVisibility(View.INVISIBLE);

        // Bind current track data with view elements.
        currentTrack = trackList.get(trackNumber);
        artistName.setText(currentTrack.getArtistName());
        albumName.setText(currentTrack.getAlbumName());
        trackName.setText(currentTrack.getTrackName());

        Picasso.with(getActivity())
                .load(currentTrack.getAlbumImageUrl()).into(trackImage);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playButton.setVisibility(View.INVISIBLE);
                pauseButton.setVisibility(View.VISIBLE);
                mCallback.passTrackPreview(currentTrack.getPreviewURL());
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playButton.setVisibility(View.VISIBLE);
                pauseButton.setVisibility(View.INVISIBLE);
                mCallback.pauseTrack();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If we're at the end of the top 10 track list
                // set the track number to the first item on the list.
                if (trackNumber > 8) {
                    trackNumber = 0;
                } else {
                    trackNumber += 1;
                }
                currentTrack = trackList.get(trackNumber);
                mCallback.passTrackPreview(currentTrack.getPreviewURL());
            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If we're at the start of the top 10 track list set the
                // track number to last last item on the track list.
                if (trackNumber < 1) {
                    trackNumber = 9;
                } else {
                    trackNumber -= 1;
                }
                currentTrack = trackList.get(trackNumber);
                mCallback.passTrackPreview(currentTrack.getPreviewURL());
            }
        });
        return fragment;
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

    public interface PlaybackEventListener {
        public void passTrackPreview(String previewURL);

        public void pauseTrack();
    }

}

package amhamogus.com.spotifystreamer.fragments;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
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
import amhamogus.com.spotifystreamer.net.SpotifyRequest;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaybackFragment extends DialogFragment {

    private static final String TRACK_PARAM = "trackID";
    private static final String TRACK_LIST_PARAM = "trackList";
    private static String currentTrackID;

    PlaybackEventListener mCallback;

    // Playback controls
    private ImageButton playButton;

    // Variables for current track
    private TextView albumName;
    private TextView artistName;
    private TextView trackName;
    private ImageView trackImage;
    protected MyTrack currentTrack;
    private ArrayList<MyTrack> trackList;

    public static PlaybackFragment newInstance(String trackID, ArrayList<MyTrack> trackList) {
        currentTrackID = trackID;
        //Log.d("PLAYBACK FRAGMENT", "NEW INSTANCE TRACK ID = " + currentTrackID);

        PlaybackFragment fragment = new PlaybackFragment();

        Bundle args = new Bundle();
        args.putString(TRACK_PARAM, trackID);
        args.putParcelableArrayList(TRACK_LIST_PARAM, trackList);

        //Log.d("PLAYBACK_FRAGMENT", "Track List Size:" + trackList.get(0).toString());
        fragment.setArguments(args);
        return fragment;
    }

    public PlaybackFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_track_playback, container, false);

        // Get references to ui elements
        artistName = (TextView) fragment.findViewById(R.id.playback_artist_name);
        albumName = (TextView) fragment.findViewById(R.id.playback_album_name);
        trackName = (TextView) fragment.findViewById(R.id.playback_track_name);
        trackImage = (ImageView) fragment.findViewById(R.id.playback_image);
        playButton = (ImageButton) fragment.findViewById(R.id.imageButton);

        new PlaybackWorker().execute(currentTrackID);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity){
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
    }

    private class PlaybackWorker extends AsyncTask<String, String, MyTrack> {

        protected MyTrack doInBackground(String... string) {
            SpotifyRequest request = new SpotifyRequest();
            currentTrack = request.getTrack(string[0]);
            return currentTrack;
        }

        @Override
        protected void onPostExecute(final MyTrack returnedTrack) {
            if (returnedTrack != null) {
                artistName.setText(returnedTrack.getAlbumName());
                albumName.setText(returnedTrack.getAlbumName());
                trackName.setText(returnedTrack.getTrackName());

                Picasso.with(getActivity())
                        .load(returnedTrack.getAlbumImageUrl()).into(trackImage);

                playButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCallback.passTrackPreview(returnedTrack.getPreviewURL());
                    }
                });

            }
        }
    }
}

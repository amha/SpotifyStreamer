package amhamogus.com.spotifystreamer.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import amhamogus.com.spotifystreamer.R;
import amhamogus.com.spotifystreamer.model.MyTracks;
import amhamogus.com.spotifystreamer.net.SpotifyRequest;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaybackFragment extends DialogFragment {

    private static final String TRACK_PARAM = "trackID";
    private static final String TRACK_LIST_PARAM = "trackList";
    private static String currentTrackID;

    private TextView albumName;
    private TextView artistName;
    private TextView trackName;
    private ImageView trackImage;
    private ArrayList<MyTracks> trackList;

    public static PlaybackFragment newInstance(String trackID, ArrayList<MyTracks> trackList) {
        currentTrackID = trackID;
        Log.d("PLAYBACK FRAGMENT", "NEW INSTANCE TRACK ID = " + currentTrackID);

        PlaybackFragment fragment = new PlaybackFragment();

        Bundle args = new Bundle();
        args.putString(TRACK_PARAM, trackID);
        args.putParcelableArrayList(TRACK_LIST_PARAM, trackList);

        Log.d("PLAYBACK_FRAGMENT", "Track List Size:"+ trackList.get(0).toString());
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

        new PlaybackWorker().execute(currentTrackID);
        return fragment;
    }

    private class PlaybackWorker extends AsyncTask<String, String, MyTracks> {

        protected MyTracks doInBackground(String... string) {
            SpotifyRequest request = new SpotifyRequest();
            MyTracks returnedTrack = request.getTrack(string[0]);
            return returnedTrack;
        }

        @Override
        protected void onPostExecute(MyTracks returnedTrack) {
            if (returnedTrack != null) {
                artistName.setText("NEED TO ADD ARTIST NAME");
                albumName.setText(returnedTrack.getAlbumName());
                trackName.setText(returnedTrack.getTrackName());

                Picasso.with(getActivity())
                        .load(returnedTrack.getAlbumImageUrl()).into(trackImage);
            }
        }
    }
}

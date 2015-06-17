package amhamogus.com.spotifystreamer.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import amhamogus.com.spotifystreamer.R;
import amhamogus.com.spotifystreamer.model.MyTracks;
import amhamogus.com.spotifystreamer.model.TrackListAdapter;
import amhamogus.com.spotifystreamer.net.SpotifyRequest;

/**
 * List of Top Tracks for a given artist.
 */
public class TopTracks extends Activity {


    protected ArrayList<MyTracks> TOP_TRACKS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_tracks);

        // Retrieve artist name and id from intent.
        Intent intent = getIntent();
        String artist_id = intent.getExtras().getString("ARTIST_ID");
        String artistName = intent.getExtras().getString("ARTIST_NAME");

        // Add artist name as a subtitle to the Action bar.
        if (artistName != null) {
            ActionBar actionBar = getActionBar();
            actionBar.setSubtitle(intent.getExtras().getString("ARTIST_NAME"));
        }

        // Get top tracks in the background.
        if (savedInstanceState == null) {
            new TopTrackWorker().execute(artist_id);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outstate) {
        if (outstate != null) {
            outstate.putParcelableArrayList("tracks", TOP_TRACKS);
        }
        super.onSaveInstanceState(outstate);
    }

    @Override
    protected void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        if (bundle != null) {
            TOP_TRACKS = bundle.getParcelableArrayList("tracks");

            TrackListAdapter adapter =
                    new TrackListAdapter(getApplicationContext(), 0, TOP_TRACKS);
            ListView trackList = (ListView) findViewById(R.id.top_track_list);
            trackList.setAdapter(adapter);
        }
    }


    /**
     * Helper class that requests top tracks from Spotify.
     */
    private class TopTrackWorker extends AsyncTask<String, String, ArrayList<MyTracks>> {

        SpotifyRequest topTrackRequest;

        @Override
        protected ArrayList<MyTracks> doInBackground(String... strings) {
            topTrackRequest = new SpotifyRequest();
            TOP_TRACKS = topTrackRequest.searchTopTracks(strings[0]);
            return TOP_TRACKS;
        }

        @Override
        protected void onPostExecute(ArrayList<MyTracks> tracks) {

            if (tracks != null) {
                if (tracks.size() == 0) {
                    // Zero tracks returned from Spotify api.
                    // Inform the user by displaying toast message.
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.no_tracks), Toast.LENGTH_SHORT).show();
                } else {
                    // Display the top tracks returned from from Spotify api.
                    TrackListAdapter adapter =
                            new TrackListAdapter(getApplicationContext(), 0, tracks);
                    ListView trackList = (ListView) findViewById(R.id.top_track_list);
                    trackList.setAdapter(adapter);
                }
            }
        }
    }
}

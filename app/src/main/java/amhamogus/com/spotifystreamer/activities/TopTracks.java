package amhamogus.com.spotifystreamer.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import amhamogus.com.spotifystreamer.R;
import amhamogus.com.spotifystreamer.model.TrackListAdapter;
import amhamogus.com.spotifystreamer.net.SpotifyRequest;
import kaaes.spotify.webapi.android.models.Tracks;

/**
 * List of Top Tracks for a given artist.
 */
public class TopTracks extends Activity {

    protected Tracks TOP_TRACKS;

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
        new TopTrackWorker().execute(artist_id);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_top_tracks, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Helper class that requests top tracks from Spotify.
     */
    private class TopTrackWorker extends AsyncTask<String, String, Tracks> {

        SpotifyRequest topTrackRequest;

        @Override
        protected Tracks doInBackground(String... strings) {
            topTrackRequest = new SpotifyRequest();
            TOP_TRACKS = topTrackRequest.searchTopTracks(strings[0]);
            return TOP_TRACKS;
        }

        @Override
        protected void onPostExecute(Tracks tracks) {

            if (tracks != null) {
                if (tracks.tracks.size() == 0) {
                    // Zero tracks returned from Spotify api.
                    // Inform the user by display toast message.
                    Toast.makeText(getApplicationContext(),
                            "Woops! It looks like someone went ahead and "
                                    + "snatched these tracks!", Toast.LENGTH_SHORT).show();
                } else {
                    // Display the top tracks returned from from Spotify api.
                    TrackListAdapter adapter =
                            new TrackListAdapter(getApplicationContext(), 0, tracks.tracks);
                    ListView trackList = (ListView) findViewById(R.id.top_track_list);
                    trackList.setAdapter(adapter);
                }
            }
        }
    }
}

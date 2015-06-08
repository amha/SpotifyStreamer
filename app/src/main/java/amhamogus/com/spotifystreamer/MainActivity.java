package amhamogus.com.spotifystreamer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import amhamogus.com.spotifystreamer.activities.TopTracks;
import amhamogus.com.spotifystreamer.model.MyArtistAdapter;
import amhamogus.com.spotifystreamer.net.SpotifyRequest;
import kaaes.spotify.webapi.android.models.Artist;

/**
 * Main entry point into Spotify Streamer - Stage 1.
 */
public class MainActivity extends Activity {

    /**
     * List of artist objects.
     */
    protected List<Artist> artistList;

    /** Custom adapter that maps artist object to list view. */
    protected MyArtistAdapter myArtistAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get reference to search term.
        EditText searchInput = (EditText) findViewById(R.id.editText);

        // Setup action listener to respond to user searches.
        searchInput.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean imeActionHandled = false;

                // Triggered when the "go" button is tapped.
                if (actionId == EditorInfo.IME_ACTION_GO) {

                    if (v.getText().toString().equals("")) {
                        // Show toast if search team is blank.
                        Toast.makeText(getApplicationContext(),
                                "Please Enter Artist Name", Toast.LENGTH_SHORT).show();
                    } else {

                        // Hide keyboard so the user can view content.
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                        // Call spotify service
                        new SpotifyWorkerTask().execute(v.getText().toString());
                        imeActionHandled = true;
                    }
                }
                return imeActionHandled;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
     * Helper class that requests a list or Artists from Spotify.
     */
    private class SpotifyWorkerTask extends AsyncTask<String, String, List<Artist>> {

        SpotifyRequest call;

        protected List<Artist> doInBackground(String... strings) {
            call = new SpotifyRequest();
            return call.searchArtist(strings[0]);
        }

        @Override
        protected void onPostExecute(List<Artist> returnedArtists) {
            super.onPostExecute(returnedArtists);
            artistList = returnedArtists;

            myArtistAdapter =
                    new MyArtistAdapter(getApplicationContext(), 0, artistList);

            ListView list = (ListView) findViewById(R.id.artistListView);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Artist selectedArtist = (Artist) parent.getItemAtPosition(position);

                    // Package selected artist's name and ID to send to top track activity.
                    Bundle bundle = new Bundle();
                    bundle.putString("ARTIST_NAME", selectedArtist.name);
                    bundle.putString("ARTIST_ID", selectedArtist.id);

                    Intent intent = new Intent(getApplicationContext(), TopTracks.class);
                    intent.putExtras(bundle);
                    startActivity(intent, bundle);

                }
            });
            list.setAdapter(myArtistAdapter);
        }
    }
}
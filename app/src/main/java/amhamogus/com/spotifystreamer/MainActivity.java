package amhamogus.com.spotifystreamer;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import amhamogus.com.spotifystreamer.model.MyArtistAdapter;
import amhamogus.com.spotifystreamer.net.SpotifyRequest;
import kaaes.spotify.webapi.android.models.Artist;


public class MainActivity extends Activity {

    protected List<Artist> artistList;
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
                if (actionId == EditorInfo.IME_ACTION_GO) {

                    if (v.getText().toString().equals("")) {
                        // Show toast if search team is blank.
                        Toast.makeText(getApplicationContext(),
                                "Please Enter Artist Name", Toast.LENGTH_SHORT).show();
                    } else {
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
                    new MyArtistAdapter(getApplicationContext(), 0,
                            artistList);
            ListView list = (ListView) findViewById(R.id.artistListView);
            list.setAdapter(myArtistAdapter);
        }
    }
}
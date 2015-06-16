package amhamogus.com.spotifystreamer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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

import java.util.ArrayList;

import amhamogus.com.spotifystreamer.activities.TopTracks;
import amhamogus.com.spotifystreamer.model.MyArtist;
import amhamogus.com.spotifystreamer.model.MyArtistAdapter;
import amhamogus.com.spotifystreamer.net.SpotifyRequest;

/**
 * Main entry point into Spotify Streamer - Stage 1.
 */
public class MainActivity extends Activity {

    protected final String TAG = "AMHAMOGUS CONSOLE: ";

    /**
     * List of artist objects.
     */
    //protected List<MyArtist> artistList;
    protected ArrayList<MyArtist> artistList;

    /**
     * Custom adapter that maps artist object to list view.
     */
    protected MyArtistAdapter myArtistAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ConnectivityManager connectivityManager =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = connectivityManager.getActiveNetworkInfo();

        if (network != null && network.isConnectedOrConnecting()) {

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
    protected void onStart() {
        super.onStart();
        Log.e("AMHA-OUT", "ON START METHOD CALLED");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("AMHA-OUT", "ON RESUME METHOD CALLED");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("AMHA-OUT", "ON PAUSE METHOD CALLED");
    }

    protected void onStop() {
        super.onStop();
        Log.e("AMHA-OUT", "ON STOP METHOD CALLED");
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        if (artistList != null) {
            bundle.putParcelableArrayList("key", artistList);
        }
        super.onSaveInstanceState(bundle);
    }

    @Override
    protected void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        artistList = bundle.getParcelableArrayList("key");

        ListView list = (ListView) findViewById(R.id.artistListView);
        myArtistAdapter =
                new MyArtistAdapter(getApplicationContext(), 0, artistList);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyArtist selectedArtist = (MyArtist) parent.getItemAtPosition(position);

                // Package selected artist's name and ID to send to top track activity.
                Bundle bundle = new Bundle();
                bundle.putString("ARTIST_NAME", selectedArtist.getName());
                bundle.putString("ARTIST_ID", selectedArtist.getId());

                Intent intent = new Intent(getApplicationContext(), TopTracks.class);
                intent.putExtras(bundle);
                startActivity(intent, bundle);
            }
        });
        list.setAdapter(myArtistAdapter);
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
    private class SpotifyWorkerTask extends AsyncTask<String, String, ArrayList<MyArtist>> {

        SpotifyRequest call;

        protected ArrayList<MyArtist> doInBackground(String... strings) {
            call = new SpotifyRequest();
            return call.searchArtist(strings[0]);
        }

        @Override
        protected void onPostExecute(ArrayList<MyArtist> returnedArtists) {
            super.onPostExecute(returnedArtists);

            artistList = returnedArtists;

            myArtistAdapter =
                    new MyArtistAdapter(getApplicationContext(), 0, returnedArtists);

            ListView list = (ListView) findViewById(R.id.artistListView);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    MyArtist selectedArtist = (MyArtist) parent.getItemAtPosition(position);

                    // Package selected artist's name and ID to send to top track activity.
                    Bundle bundle = new Bundle();
                    bundle.putString("ARTIST_NAME", selectedArtist.getName());
                    bundle.putString("ARTIST_ID", selectedArtist.getId());

                    Intent intent = new Intent(getApplicationContext(), TopTracks.class);
                    intent.putExtras(bundle);
                    startActivity(intent, bundle);
                }
            });
            list.setAdapter(myArtistAdapter);
        }
    }
}
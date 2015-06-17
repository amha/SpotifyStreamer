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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
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

    /**
     * List of artist objects.
     */
    protected ArrayList<MyArtist> artistList;

    /**
     * Custom adapter that maps artist object to list view.
     */
    protected MyArtistAdapter myArtistAdapter;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Hide progress bar.
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.INVISIBLE);

        // Check for network connectivity.
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = connectivityManager.getActiveNetworkInfo();

        if (network != null && network.isConnectedOrConnecting()) {
            // Get reference to search term.
            SearchView searchInput = (SearchView) findViewById(R.id.search_input);

            // Listener respond to user searches.
            searchInput.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    if (s.equals("")) {
                        // Show toast if search it submitted without a search term.
                        Toast.makeText(getApplicationContext(),
                                "Please Enter Artist Name", Toast.LENGTH_SHORT).show();
                    } else {
                        // Send search request to Spotify in the background.
                        new SpotifyWorkerTask().execute(s);
                    }
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    return false;
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
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
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
        protected void onPreExecute() {
            super.onPreExecute();

            // Check if Progress Bar has been access
            if (progressBar != null) {
                progressBar.setVisibility(View.VISIBLE);
            } else {
                progressBar = (ProgressBar) findViewById(R.id.progress_bar);
                progressBar.setVisibility(View.VISIBLE);
            }
        }


        @Override
        protected void onPostExecute(ArrayList<MyArtist> returnedArtists) {
            super.onPostExecute(returnedArtists);

            progressBar.setVisibility(View.INVISIBLE);

            if (returnedArtists.size() == 0) {
                // Zero artists returned from Spotify api.
                // Inform the user by displaying toast message.
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.no_artists), Toast.LENGTH_SHORT).show();
            } else {
                artistList = returnedArtists;

                myArtistAdapter =
                        new MyArtistAdapter(getApplicationContext(), 0, returnedArtists);

                // Populate list view with artists returned from Spotify.
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
}
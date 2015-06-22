package amhamogus.com.spotifystreamer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import amhamogus.com.spotifystreamer.fragments.ArtistSearchFragment;

/**
 * Main entry point into Spotify Streamer - Stage 1.
 */
public class MainActivity extends Activity implements ArtistSearchFragment.OnFragmentInteractionListener {

    private ArtistSearchFragment artistSearchFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check for network connectivity.
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getApplicationContext()
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = connectivityManager.getActiveNetworkInfo();

        if (network != null && network.isConnectedOrConnecting()) {

            if (savedInstanceState != null) {
                // Reading data from saved instance state
                artistSearchFragment =
                        (ArtistSearchFragment) getFragmentManager()
                                .findFragmentByTag("artist_fragment");
            } else {
                artistSearchFragment = ArtistSearchFragment.newInstance();
            }

            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.artist_fragment, artistSearchFragment, "artist_fragment");
            transaction.commit();

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

    public void onFragmentInteraction(Uri uri) {
        Log.d("AMHA", "Getting feedback from fragment");
    }
}
package amhamogus.com.spotifystreamer.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

import amhamogus.com.spotifystreamer.R;
import amhamogus.com.spotifystreamer.fragments.TopTracksFragment;
import amhamogus.com.spotifystreamer.model.MyTracks;

/**
 * List of Top Tracks for a given artist.
 */
public class TopTracks extends Activity implements TopTracksFragment.OnFragmentInteractionListener {

    protected ArrayList<MyTracks> TOP_TRACKS;
    protected TopTracksFragment topTracksFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_tracks);

        // Retrieve artist name and id from intent.
        Intent intent = getIntent();
        String artist_id = intent.getExtras().getString("ARTIST_ID");
        String artistName = intent.getExtras().getString("ARTIST_NAME");

        if (savedInstanceState != null) {
            // Recreating fragment from saved state.
            topTracksFragment =
                    (TopTracksFragment) getFragmentManager().findFragmentByTag("tracks_fragment");

        } else {
            // Nothing saved; create new fragment.
            topTracksFragment = TopTracksFragment.newInstance(artist_id, artistName);
        }

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.top_track_fragment_holder, topTracksFragment, "tracks_fragment");
        transaction.commit();

        // Add artist name as a subtitle to the Action bar.
        if (artistName != null) {
            ActionBar actionBar = getActionBar();
            actionBar.setSubtitle(intent.getExtras().getString("ARTIST_NAME"));
        }
    }

    /**
     * Helper class that requests top tracks from Spotify.
     */

    public void onFragmentInteraction(String songName) {
        return;
    }
}

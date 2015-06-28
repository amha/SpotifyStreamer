package amhamogus.com.spotifystreamer.activities;


import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

import amhamogus.com.spotifystreamer.R;
import amhamogus.com.spotifystreamer.fragments.TrackPlaybackFragment;

public class TrackPlayback extends Activity {

    private TrackPlaybackFragment fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_playback);

        Intent intent = getIntent();
        String trackID = intent.getStringExtra("trackID");

        fragment = TrackPlaybackFragment.newInstance(trackID);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.playback_fragment_holder, fragment, "tag");
        transaction.commit();
    }
}

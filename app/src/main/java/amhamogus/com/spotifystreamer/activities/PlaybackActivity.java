package amhamogus.com.spotifystreamer.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import java.util.ArrayList;

import amhamogus.com.spotifystreamer.R;
import amhamogus.com.spotifystreamer.fragments.PlaybackFragment;
import amhamogus.com.spotifystreamer.model.MyTracks;

public class PlaybackActivity extends FragmentActivity {

    private PlaybackFragment fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_playback);

        Intent intent = getIntent();
        String trackID = intent.getStringExtra("trackID");
        ArrayList<MyTracks> trackList = intent.getParcelableArrayListExtra("trackList");

        fragment = PlaybackFragment.newInstance(trackID, trackList);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.playback_fragment_holder, fragment, "tag");
        transaction.commit();
    }
}

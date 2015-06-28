package amhamogus.com.spotifystreamer.activities;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import amhamogus.com.spotifystreamer.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class TrackPlaybackFragment extends Fragment {

    public TrackPlaybackFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_track_playback, container, false);
    }
}

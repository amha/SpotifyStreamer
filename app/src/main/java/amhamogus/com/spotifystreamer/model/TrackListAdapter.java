package amhamogus.com.spotifystreamer.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import amhamogus.com.spotifystreamer.R;

/**
 * Maps an Track object to the top track row layout file.
 */
public class TrackListAdapter extends ArrayAdapter<MyTracks> {

    public TrackListAdapter(Context context, int resourceID, ArrayList<MyTracks> tracks) {
        super(context, resourceID, tracks);
    }


    public View getView(int position, View view, ViewGroup parent) {
        View trackView;

        if (view == null) {
            // Instantiate the track row if we haven't done so before.
            trackView = LayoutInflater.from(getContext())
                    .inflate(R.layout.adapter_track_row, parent, false);
        } else {
            trackView = view;
        }

        // Get track data from the List<Tracks> at position.
        MyTracks currentTrack = getItem(position);

        // Setting the name of the track.
        TextView trackName = (TextView) trackView.findViewById(R.id.trackTitle);
        trackName.setText(currentTrack.getTrackName());

        // Setting the album name.
        TextView albumName = (TextView) trackView.findViewById(R.id.albumName);
        albumName.setText(currentTrack.getAlbumName());

        // Setting Album Cover.
        ImageView albumCover = (ImageView) trackView.findViewById(R.id.trackListImage);

        if (currentTrack.albumImageUrl.length() > 0) {
            // Load album image if available
            Picasso.with(getContext())
                    .load(currentTrack.albumImageUrl)
                    .into(albumCover);
        }
        return trackView;
    }
}

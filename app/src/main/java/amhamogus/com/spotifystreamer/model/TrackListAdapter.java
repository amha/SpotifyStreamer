package amhamogus.com.spotifystreamer.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import amhamogus.com.spotifystreamer.R;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Maps an Track object to the top track row layout file.
 */
public class TrackListAdapter extends ArrayAdapter<Track> {

    public TrackListAdapter(Context context, int resourceID, List<Track> tracks) {
        super(context, resourceID, tracks);
    }


    public View getView(int position, View view, ViewGroup parent) {
        View trackView;

        if (view == null) {
            // Instantiate the track row if we haven't done so before.
            trackView = LayoutInflater.from(getContext())
                    .inflate(R.layout.track_row, parent, false);
        } else {
            trackView = view;
        }

        // Get track data from the List<Tracks> at position.
        Track currentTrack = getItem(position);

        // Setting the name of the track.
        TextView trackName = (TextView) trackView.findViewById(R.id.trackTitle);
        trackName.setText(currentTrack.name);

        // Setting the album name.
        TextView albumName = (TextView) trackView.findViewById(R.id.albumName);
        albumName.setText(currentTrack.album.name);

        // Setting Album Cover.
        ImageView albumCover = (ImageView) trackView.findViewById(R.id.trackListImage);

        if (currentTrack.album.images.size() > 0) {
            // Load album image if available
            Picasso.with(getContext())
                    .load(currentTrack.album.images.get(0).url)
                    .into(albumCover);
        }
        return trackView;
    }
}

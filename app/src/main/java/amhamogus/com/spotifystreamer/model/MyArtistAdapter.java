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
import kaaes.spotify.webapi.android.models.Artist;

/**
 * Created by amogus on 6/6/15.
 */
public class MyArtistAdapter extends ArrayAdapter<Artist> {

    List<Artist> artists;

    public MyArtistAdapter(Context context, int resourceID, List<Artist> artists) {
        super(context, resourceID, artists);
        this.artists = artists;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        View artistView;

        if (view == null) {
            artistView = (View) LayoutInflater.from(getContext())
                    .inflate(R.layout.artist_row, parent, false);
        } else {
            artistView = view;
        }

        Artist artist = getItem(position);

        TextView currentArtistName = (TextView) artistView.findViewById(R.id.artistName);
        currentArtistName.setText(artist.name);

        ImageView albumCover = (ImageView) artistView.findViewById(R.id.artistImage);

        String imageURLS;
        if (artist.images.size() > 0) {
            // Load artist image if available
            Picasso.with(getContext()).load(artist.images.get(0).url).into(albumCover);
        }

        return artistView;
    }

}

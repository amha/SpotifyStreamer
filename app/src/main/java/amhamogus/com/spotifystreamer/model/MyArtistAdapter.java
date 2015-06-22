package amhamogus.com.spotifystreamer.model;

import android.content.Context;
import android.util.Log;
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
 * Maps an Artist Object to the artist row layout file.
 */

public class MyArtistAdapter extends ArrayAdapter<MyArtist> {

    ArrayList<MyArtist> artists;

    public MyArtistAdapter(Context context, int resourceID, ArrayList<MyArtist> artists) {
        super(context, resourceID, artists);
        this.artists = artists;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        View artistView;

        if (view == null) {
            // Instantiate the artist row if we haven't done so before.
            artistView = LayoutInflater.from(getContext())
                    .inflate(R.layout.adapter_artist_row, parent, false);
        } else {
            artistView = view;
        }

        // Get artist data from the List<Artists> object at the specified position.
        MyArtist artist = getItem(position);

        // Setting the name of the artist.
        TextView currentArtistName = (TextView) artistView.findViewById(R.id.artistName);
        currentArtistName.setText(artist.name);

        // Get reference to image view.
        ImageView artistImg = (ImageView) artistView.findViewById(R.id.artistImage);

        if (artist.imageURL == null) {
            Log.d("AMHA", "passing null to image");
        } else if (artist.imageURL.length() > 0) {
            // Load artist image if available
            Picasso.with(getContext()).load(artist.imageURL).into(artistImg);
        }

        return artistView;
    }

}

package amhamogus.com.spotifystreamer.net;

import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import amhamogus.com.spotifystreamer.model.MyArtist;
import amhamogus.com.spotifystreamer.model.MyTracks;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.RetrofitError;

/**
 * A user request that is sent to Spotify.
 */
public class SpotifyRequest {

    /**
     * Search for artists based on a name value submitted by the user.
     */
    public ArrayList<MyArtist> searchArtist(String name) {
        SpotifyApi api = new SpotifyApi();
        SpotifyService spotifyService = api.getService();

        ArtistsPager results;
        ArrayList<MyArtist> myArtists = new ArrayList<>();

        try {
            results = spotifyService.searchArtists(name);
            for (Artist artist : results.artists.items) {
                myArtists.add(new MyArtist(artist));
            }
        } catch (RetrofitError error) {
            SpotifyError spotifyError = SpotifyError.fromRetrofitError(error);

            // Notify the user that something has gone wrong.
            Toast.makeText(null, "Can't retrieve artists. Restart the app.", Toast.LENGTH_LONG).show();

            // Pass error text to the console.
            Log.d("Error", "Error Retrieving Artist " + spotifyError.toString());
        }
        return myArtists;
    }

    /**
     * Search for top 10 tracks for a specified artist.
     *
     * @return Tracks - Returns a list
     */
    public ArrayList<MyTracks> searchTopTracks(String artistID) {

        Tracks returnedTracks;

        SpotifyApi api = new SpotifyApi();
        SpotifyService spotifyService = api.getService();

        // Creating a map that represents the top tracks query.
        Map map = new HashMap<>();
        map.put("id", artistID);
        map.put("country", "US");

        ArrayList<MyTracks> myTracks = new ArrayList<>();

        try {
            returnedTracks = spotifyService.getArtistTopTrack(artistID, map);

            for (Track track : returnedTracks.tracks) {
                myTracks.add(new MyTracks(track));
            }
        } catch (RetrofitError error) {
            SpotifyError spotifyError = SpotifyError.fromRetrofitError(error);

            // Pass error text to the console.
            Log.d("Error", "Error Retrieving Top Tracks: " + spotifyError.toString());
        }

        return myTracks;
    }
}
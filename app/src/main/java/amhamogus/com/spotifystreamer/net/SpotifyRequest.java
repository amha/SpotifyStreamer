package amhamogus.com.spotifystreamer.net;

import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;

/**
 * A user request that is sent to Spotify.
 */
public class SpotifyRequest {

   /** Search for artists based on a name value submitted by the user. */
    public List<Artist> searchArtist(String name) {
        SpotifyApi api = new SpotifyApi();
        SpotifyService spotifyService = api.getService();
        ArtistsPager results = spotifyService.searchArtists(name);
        return results.artists.items;
    }
}

package amhamogus.com.spotifystreamer.net;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Tracks;

/**
 * A user request that is sent to Spotify.
 */
public class SpotifyRequest {

    /**
     * Search for artists based on a name value submitted by the user.
     */
    public List<Artist> searchArtist(String name) {
        SpotifyApi api = new SpotifyApi();
        SpotifyService spotifyService = api.getService();
        ArtistsPager results = spotifyService.searchArtists(name);
        return results.artists.items;
    }

    /**
     * Search for top 10 tracks for a specified artist.
     */
    public Tracks searchTopTracks(String artistID) {

        SpotifyApi api = new SpotifyApi();
        SpotifyService spotifyService = api.getService();

        Map map = new HashMap<>();
        map.put("id", artistID);
        map.put("country", "US");

        return spotifyService.getArtistTopTrack(artistID, map);
    }
}

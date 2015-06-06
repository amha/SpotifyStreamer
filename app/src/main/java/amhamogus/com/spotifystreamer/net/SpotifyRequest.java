package amhamogus.com.spotifystreamer.net;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by amogus on 6/6/15.
 */
public class SpotifyRequest {

    OkHttpClient client = new OkHttpClient();

    public String run(String url) throws IOException {
        Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/search?q=" + url + "&type=artist")
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}

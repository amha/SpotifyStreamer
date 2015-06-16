package amhamogus.com.spotifystreamer.model;

import android.os.Parcel;
import android.os.Parcelable;

import kaaes.spotify.webapi.android.models.Track;

/**
 * An object that represents a music track or song.
 *
 * @author Amha Mogus
 */
public class MyTracks implements Parcelable {

    // The name of the track or song.
    protected String trackName;

    // The name of the album the track appears on.
    protected String albumName;

    // URL value to album cover art.
    protected String albumImageUrl;

    public MyTracks(Track track) {
        if (track != null) {
            trackName = track.name;
            albumName = track.album.name;

            // If we don't have an image url, use a default image.
            if (track.album.images.size() > 0) {
                albumImageUrl = track.album.images.get(0).url;
            } else {
                albumImageUrl = "";
            }
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int size) {
        parcel.writeString(this.trackName);
        parcel.writeString(this.albumName);
        parcel.writeString(this.albumImageUrl);
    }

    public static final Parcelable.Creator<MyTracks> CREATOR =
            new Parcelable.Creator<MyTracks>() {
                public MyTracks createFromParcel(Parcel parcel) {
                    return new MyTracks(parcel);
                }

                public MyTracks[] newArray(int size) {
                    return new MyTracks[size];
                }
            };

    private MyTracks(Parcel parcel) {
        this.trackName = parcel.readString();
        this.albumName = parcel.readString();
        this.albumImageUrl = parcel.readString();
    }

    /**
     * Accessor method used to get the track name.
     */
    public String getTrackName() {
        return this.trackName;
    }

    /**
     * Accessor method used to get the name of the album the track is from.
     */
    public String getAlbumName() {
        return this.albumName;
    }
}


package amhamogus.com.spotifystreamer.model;

import android.os.Parcel;
import android.os.Parcelable;

import kaaes.spotify.webapi.android.models.Track;

/**
 * An object that represents a music track or song.
 *
 * @author Amha Mogus
 */
public class MyTrack implements Parcelable {

    // The name of the track or song.
    protected String trackName;

    // The name of the album the track appears on.
    protected String albumName;

    // URL value to album cover art.
    protected String albumImageUrl;

    // URL to audio preview of a track.
    protected String previewURL;

    // Spotify's ID number for a track.
    protected String trackID;

    public MyTrack(Track track) {
        if (track != null) {
            trackName = track.name;
            albumName = track.album.name;

            // If we don't have an image url, use a default image.
            if (track.album.images.size() > 0) {
                albumImageUrl = track.album.images.get(0).url;
            } else {
                albumImageUrl = "";
            }
            previewURL = track.preview_url;
            trackID = track.id;
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
        parcel.writeString(this.trackID);
    }

    public static final Parcelable.Creator<MyTrack> CREATOR =
            new Parcelable.Creator<MyTrack>() {
                public MyTrack createFromParcel(Parcel parcel) {
                    return new MyTrack(parcel);
                }

                public MyTrack[] newArray(int size) {
                    return new MyTrack[size];
                }
            };

    private MyTrack(Parcel parcel) {
        this.trackName = parcel.readString();
        this.albumName = parcel.readString();
        this.albumImageUrl = parcel.readString();
        this.trackID = parcel.readString();
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

    /**
     * Accessor method used to get the url an album art.
     */
    public String getAlbumImageUrl() {
        return this.albumImageUrl;
    }

    /**
     * Accessor method used to get the url an album art.
     */
    public String getTrackID() {
        return this.trackID;
    }

    /**
     * Accessor method used to get audio preview of a track.
     */
    public String getPreviewURL() {
        return this.previewURL;
    }
}



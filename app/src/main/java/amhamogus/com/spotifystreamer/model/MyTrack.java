/*
 * Copyright (C) 2015 Amha Mogus amha.mogus@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
    protected String artistName;

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
            artistName = track.artists.get(0).name;

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
        parcel.writeString(this.artistName);
        parcel.writeString(this.trackName);
        parcel.writeString(this.albumName);
        parcel.writeString(this.albumImageUrl);
        parcel.writeString(this.trackID);
        parcel.writeString(this.previewURL);
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
        this.artistName = parcel.readString();
        this.trackName = parcel.readString();
        this.albumName = parcel.readString();
        this.albumImageUrl = parcel.readString();
        this.trackID = parcel.readString();
        this.previewURL = parcel.readString();
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
    
    /**
     * Accessor method used to get audio preview of a track.
     */
    public String getArtistName() {
        return this.artistName;
    }
}



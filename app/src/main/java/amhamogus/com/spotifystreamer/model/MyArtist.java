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

import kaaes.spotify.webapi.android.models.Artist;

/**
 * An object that represents a musical artist.
 *
 * @author Amha Mogus
 */
public class MyArtist extends Artist implements Parcelable {

    protected String id;
    protected String name;
    protected String imageURL;

    public MyArtist(Artist artist) {
        if (artist != null) {
            this.id = artist.id;
            this.name = artist.name;
            if (artist.images.size() > 0) {
                this.imageURL = artist.images.get(0).url;
            } else {
                this.imageURL = "";
            }
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.id);
        dest.writeString(this.imageURL);
    }

    public static final Parcelable.Creator<MyArtist> CREATOR
            = new Parcelable.Creator<MyArtist>() {
        public MyArtist createFromParcel(Parcel parcel) {
            return new MyArtist(parcel);
        }

        public MyArtist[] newArray(int size) {
            return new MyArtist[size];
        }
    };

    private MyArtist(Parcel parcel) {
        this.name = parcel.readString();
        this.id = parcel.readString();
        this.imageURL = parcel.readString();
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getImageURL() {
        return imageURL;
    }
}

package com.dyadav.chirpntweet.modal;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ExtendedEntities implements Parcelable {
    @SerializedName("media")
    @Expose
    List<Media> media;

    protected ExtendedEntities(Parcel in) {
        media = in.createTypedArrayList(Media.CREATOR);
    }

    public static final Creator<ExtendedEntities> CREATOR = new Creator<ExtendedEntities>() {
        @Override
        public ExtendedEntities createFromParcel(Parcel in) {
            return new ExtendedEntities(in);
        }

        @Override
        public ExtendedEntities[] newArray(int size) {
            return new ExtendedEntities[size];
        }
    };

    public List<Media> getMedia() {
        return media;
    }

    public void setMedia(List<Media> media) {
        this.media = media;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(media);
    }
}

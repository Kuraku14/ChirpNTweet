package com.dyadav.chirpntweet.modal;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Entities implements Parcelable {
    @SerializedName("urls")
    @Expose
    List<Url> urls = null;

    @SerializedName("media")
    @Expose
    List<Media> media;


    protected Entities(Parcel in) {
        urls = in.createTypedArrayList(Url.CREATOR);
        media = in.createTypedArrayList(Media.CREATOR);
    }

    public static final Creator<Entities> CREATOR = new Creator<Entities>() {
        @Override
        public Entities createFromParcel(Parcel in) {
            return new Entities(in);
        }

        @Override
        public Entities[] newArray(int size) {
            return new Entities[size];
        }
    };

    public List<Url> getUrls() {
        return urls;
    }

    public void setUrls(List<Url> urls) {
        this.urls = urls;
    }

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
        dest.writeTypedList(urls);
        dest.writeTypedList(media);
    }
}

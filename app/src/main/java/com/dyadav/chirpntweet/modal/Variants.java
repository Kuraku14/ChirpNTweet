package com.dyadav.chirpntweet.modal;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Variants implements Parcelable {
    @SerializedName("content_type")
    @Expose
    String contentType;

    @SerializedName("url")
    @Expose
    String url;

    protected Variants(Parcel in) {
        contentType = in.readString();
        url = in.readString();
    }

    public static final Creator<Variants> CREATOR = new Creator<Variants>() {
        @Override
        public Variants createFromParcel(Parcel in) {
            return new Variants(in);
        }

        @Override
        public Variants[] newArray(int size) {
            return new Variants[size];
        }
    };

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(contentType);
        dest.writeString(url);
    }
}


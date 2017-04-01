package com.dyadav.chirpntweet.modal;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Media implements Parcelable{

    @SerializedName("id")
    @Expose
    private long uid;

    @SerializedName("type")
    @Expose
    String type;

    @SerializedName("media_url_https")
    @Expose
    String mediaUrlHttps;

    @SerializedName("video_info")
    @Expose
    VideoInfo videoInfo;

    protected Media(Parcel in) {
        uid = in.readLong();
        type = in.readString();
        mediaUrlHttps = in.readString();
        videoInfo = in.readParcelable(VideoInfo.class.getClassLoader());
    }

    public static final Creator<Media> CREATOR = new Creator<Media>() {
        @Override
        public Media createFromParcel(Parcel in) {
            return new Media(in);
        }

        @Override
        public Media[] newArray(int size) {
            return new Media[size];
        }
    };

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getType() {
        return type;
    }

    public String getMediaUrlHttps() {
        return mediaUrlHttps;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setMediaUrlHttps(String mediaUrlHttps) {
        this.mediaUrlHttps = mediaUrlHttps;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(uid);
        dest.writeString(type);
        dest.writeString(mediaUrlHttps);
        dest.writeParcelable(videoInfo, flags);
    }
}

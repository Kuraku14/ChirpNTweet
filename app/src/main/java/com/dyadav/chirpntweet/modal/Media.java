package com.dyadav.chirpntweet.modal;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class Media implements Parcelable{
    String type;
    String mediaUrlHttps;
    String mediaUrl;
    String videoUrlHttps;

    public Media() {
    }

    protected Media(Parcel in) {
        type = in.readString();
        mediaUrlHttps = in.readString();
        mediaUrl = in.readString();
        videoUrlHttps = in.readString();
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

    public String getType() {
        return type;
    }

    public String getMediaUrlHttps() {
        return mediaUrlHttps;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public String getVideoUrlHttps() {
        return videoUrlHttps;
    }

    public static Media fromJson(JSONObject jObject){
        Media media = new Media();

        try {
            media.type = jObject.getString("type");
            media.mediaUrl = jObject.getString("media_url");
            media.mediaUrlHttps = jObject.getString("media_url_https");

            //If media type video
            if(media.type.equals("video"))
                media.videoUrlHttps = jObject.getJSONObject("video_info").
                                        getJSONArray("variants").getJSONObject(0).getString("url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return media;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(type);
        parcel.writeString(mediaUrlHttps);
        parcel.writeString(mediaUrl);
        parcel.writeString(videoUrlHttps);
    }
}

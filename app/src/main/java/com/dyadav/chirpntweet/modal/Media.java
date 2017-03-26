package com.dyadav.chirpntweet.modal;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.dyadav.chirpntweet.data.TwitterDb;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONException;
import org.json.JSONObject;

@Table(database = TwitterDb.class)
public class Media extends BaseModel implements Parcelable{

    @Column
    @PrimaryKey
    private long uid;

    @Column
    String type;

    @Column
    String mediaUrlHttps;

    @Column
    String mediaUrl;

    @Column
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

    public String getMediaUrl() {
        return mediaUrl;
    }

    public String getVideoUrlHttps() {
        return videoUrlHttps;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setMediaUrlHttps(String mediaUrlHttps) {
        this.mediaUrlHttps = mediaUrlHttps;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public void setVideoUrlHttps(String videoUrlHttps) {
        this.videoUrlHttps = videoUrlHttps;
    }

    public static Media fromJson(JSONObject jObject){
        Media media = new Media();
        Log.d("Log", jObject.toString());
        try {
            media.type = jObject.getString("type");
            media.mediaUrl = jObject.getString("media_url");
            media.mediaUrlHttps = jObject.getString("media_url_https");
            media.uid = jObject.getLong("id");

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

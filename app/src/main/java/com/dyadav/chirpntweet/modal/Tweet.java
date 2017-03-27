package com.dyadav.chirpntweet.modal;

import android.os.Parcel;
import android.os.Parcelable;

import com.dyadav.chirpntweet.data.TwitterDb;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

@Table(database = TwitterDb.class)
public class Tweet extends BaseModel implements Parcelable {
    @Column
    private String body;

    @Column
    @PrimaryKey
    private long uid;

    @Column
    private String createdAt;

    @ForeignKey(saveForeignKeyModel = true)
    @Column
    private User user;

    @Column
    private int retweetCount;

    @Column
    private int favoriteCount;

    @Column
    private boolean isRetweeted;

    @Column
    private boolean isFavorited;

    @Column
    private int messageCount;

    @ForeignKey(saveForeignKeyModel = true)
    @Column
    private Media media;

    @ForeignKey(saveForeignKeyModel = true)
    @Column
    private Media extendedMedia;

    public String getBody() {
        return body;
    }

    public long getUid() {
        return uid;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public User getUser() {
        return user;
    }

    public int getRetweetCount() {
        return retweetCount;
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }

    public boolean getRetweeted() {
        return isRetweeted;
    }

    public boolean getFavorited() {
        return isFavorited;
    }

    public int getMessageCount() {
        return messageCount;
    }

    public Media getMedia() {
        return media;
    }

    public Media getExtendedMedia() {
        return extendedMedia;
    }

    public boolean isRetweeted() {
        return isRetweeted;
    }

    public boolean isFavorited() {
        return isFavorited;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setRetweetCount(int retweetCount) {
        this.retweetCount = retweetCount;
    }

    public void setFavoriteCount(int favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public void setRetweeted(boolean retweeted) {
        isRetweeted = retweeted;
    }

    public void setFavorited(boolean favorited) {
        isFavorited = favorited;
    }

    public void setMessageCount(int messageCount) {
        this.messageCount = messageCount;
    }

    public void setMedia(Media media) {
        this.media = media;
    }

    public void setExtendedMedia(Media extendedMedia) {
        this.extendedMedia = extendedMedia;
    }

    public Tweet() {}

    public static Tweet fromJson(JSONObject jObject){
        Tweet tweet = new Tweet();

        try {
            tweet.body = jObject.getString("text");
            tweet.uid = jObject.getLong("id");
            tweet.createdAt = jObject.getString("created_at");
            tweet.user = User.fromJson(jObject.getJSONObject("user"));
            tweet.favoriteCount = jObject.getInt("favorite_count");
            tweet.retweetCount = jObject.getInt("retweet_count");
            if(jObject.has("retweeted"))
                tweet.isRetweeted = jObject.getBoolean("retweeted");
            if(jObject.has("favorited"))
                tweet.isFavorited = jObject.getBoolean("favorited");
            //Get Media/Extended Media
            JSONObject mediaObj = jObject.getJSONObject("entities");
            if(mediaObj.has("media")) {
                JSONArray mediaArray = mediaObj.getJSONArray("media");
                tweet.media = Media.fromJson(mediaArray.getJSONObject(0));
            }

            if(jObject.has("extended_entities")) {
                JSONObject extMediaObj = jObject.getJSONObject("extended_entities");
                if(extMediaObj.has("media")) {
                    JSONArray extendedMediaArray = extMediaObj.getJSONArray("media");
                    tweet.extendedMedia = Media.fromJson(extendedMediaArray.getJSONObject(0));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tweet;
    }

    public static ArrayList<Tweet> fromJSONArray(JSONArray response) {
        ArrayList<Tweet> tweets = new ArrayList<>();

        for (int i = 0; i< response.length(); i++) {
            try {
                JSONObject object = response.getJSONObject(i);
                Tweet tweet = Tweet.fromJson(object);
                if (tweet != null)
                    tweets.add(tweet);
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }
        return tweets;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(body);
        parcel.writeLong(uid);
        parcel.writeString(createdAt);
        parcel.writeParcelable(user, i);
        parcel.writeInt(retweetCount);
        parcel.writeInt(favoriteCount);
        parcel.writeInt(messageCount);
        parcel.writeParcelable(media, i);
        parcel.writeParcelable(extendedMedia, i);
        parcel.writeByte((byte) (isRetweeted ? 1 : 0));
        parcel.writeByte((byte) (isFavorited ? 1 : 0));
    }

    protected Tweet(Parcel in) {
        body = in.readString();
        uid = in.readLong();
        createdAt = in.readString();
        user = in.readParcelable(User.class.getClassLoader());
        retweetCount = in.readInt();
        favoriteCount = in.readInt();
        messageCount = in.readInt();
        media = in.readParcelable(User.class.getClassLoader());
        extendedMedia = in.readParcelable(User.class.getClassLoader());
        isRetweeted = in.readByte() != 0;
        isFavorited = in.readByte() != 0;
    }

    public static final Creator<Tweet> CREATOR = new Creator<Tweet>() {
        @Override
        public Tweet createFromParcel(Parcel in) {
            return new Tweet(in);
        }

        @Override
        public Tweet[] newArray(int size) {
            return new Tweet[size];
        }
    };
}

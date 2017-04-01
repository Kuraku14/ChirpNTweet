package com.dyadav.chirpntweet.modal;

import android.os.Parcel;
import android.os.Parcelable;

import com.dyadav.chirpntweet.data.TwitterDb;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(database = TwitterDb.class)
public class Tweet extends BaseModel implements Parcelable {
    @Column
    @SerializedName("text")
    @Expose
    private String body;

    @Column
    @PrimaryKey
    @SerializedName("id")
    @Expose
    private long uid;

    @Column
    @SerializedName("created_at")
    @Expose
    private String createdAt;

    @ForeignKey(saveForeignKeyModel = true)
    @Column
    @SerializedName("user")
    @Expose
    private User user;

    @Column
    @SerializedName("retweet_count")
    @Expose
    private int retweetCount;

    @Column
    @SerializedName("favorite_count")
    @Expose
    private int favoriteCount;

    @Column
    @SerializedName("retweeted")
    @Expose
    private boolean isRetweeted;

    @Column
    @SerializedName("favorited")
    @Expose
    private boolean isFavorited;

    @Column
    @SerializedName("")
    @Expose
    private int messageCount;

    @SerializedName("entities")
    @Expose
    private Entities entities;

    @SerializedName("extended_entities")
    @Expose
    private ExtendedEntities extendedEntities;

    public String getBody() {
        return body;
    }

    public long getUid() { return uid; }

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

    public Entities getEntities() { return entities; }

    public void setEntities(Entities entities) { this.entities = entities; }

    public ExtendedEntities getExtendedEntities() { return extendedEntities; }

    public void setExtendedEntities(ExtendedEntities extendedEntities) { this.extendedEntities = extendedEntities; }

    public Tweet() {
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
        parcel.writeParcelable(entities, i);
        parcel.writeParcelable(extendedEntities, i);
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
        entities = in.readParcelable(User.class.getClassLoader());
        extendedEntities = in.readParcelable(User.class.getClassLoader());
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

package com.dyadav.chirpntweet.modal;

import android.os.Parcel;
import android.os.Parcelable;

import com.dyadav.chirpntweet.data.TwitterDb;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(database = TwitterDb.class)
public class User extends BaseModel implements Parcelable {

    @Column
    @SerializedName("name")
    @Expose
    private String name;

    @Column
    @PrimaryKey
    @SerializedName("id")
    @Expose
    private long uid;

    @Column
    @SerializedName("screen_name")
    @Expose
    private String screenName;

    @Column
    @SerializedName("profile_image_url")
    @Expose
    private String profileImageURL;

    @Column
    @SerializedName("verified")
    @Expose
    private boolean verified;

    @Column
    @SerializedName("profile_banner_url")
    @Expose
    private String coverImageURL;

    @Column
    @SerializedName("followers_count")
    @Expose
    private String followerCount;

    @Column
    @SerializedName("friends_count")
    @Expose
    private String followingCount;

    @Column
    @SerializedName("description")
    @Expose
    private String description;

    @Column
    @SerializedName("location")
    @Expose
    private String location;

    @Column
    @SerializedName("following")
    @Expose
    private boolean following;

    @Column
    @SerializedName("follow_request_sent")
    @Expose
    private boolean follow_request_sent;

    public User() {
    }

    public boolean isVerified() {
        return verified;
    }

    public String getName() {
        return name;
    }

    public long getUid() {
        return uid;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImageURL() {
        return profileImageURL.replace("_normal", "");
    }

    public boolean getVerified() {
        return verified;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public void setProfileImageURL(String profileImageURL) { this.profileImageURL = profileImageURL;}

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getCoverImageURL() {
        return coverImageURL;
    }

    public void setCoverImageURL(String coverImageURL) {
        this.coverImageURL = coverImageURL;
    }

    public String getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(String followerCount) {
        this.followerCount = followerCount;
    }

    public String getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(String followingCount) {
        this.followingCount = followingCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isFollowing() { return following; }

    public void setFollowing(boolean following) { this.following = following; }

    public boolean isFollow_request_sent() { return follow_request_sent; }

    public void setFollow_request_sent(boolean follow_request_sent) { this.follow_request_sent = follow_request_sent; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeLong(uid);
        parcel.writeString(screenName);
        parcel.writeString(profileImageURL);
        parcel.writeString(coverImageURL);
        parcel.writeByte((byte) (verified ? 1 : 0));
        parcel.writeString(followingCount);
        parcel.writeString(followerCount);
        parcel.writeString(location);
        parcel.writeString(description);
        parcel.writeByte((byte) (following ? 1 : 0));
        parcel.writeByte((byte) (follow_request_sent ? 1 : 0));
    }

    protected User(Parcel in) {
        name = in.readString();
        uid = in.readLong();
        screenName = in.readString();
        profileImageURL = in.readString();
        coverImageURL = in.readString();
        verified = in.readByte() != 0;
        followingCount = in.readString();
        followerCount = in.readString();
        location = in.readString();
        description = in.readString();
        following = in.readByte() != 0;
        follow_request_sent = in.readByte() != 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}

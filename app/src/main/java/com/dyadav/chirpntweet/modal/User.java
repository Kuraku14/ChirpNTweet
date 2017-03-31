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
public class User extends BaseModel implements Parcelable{

    @Column
    private String name;

    @Column
    @PrimaryKey
    private long uid;

    @Column
    private String screenName;

    @Column
    private String profileImageURL;

    @Column
    private boolean verified;

    @Column
    private String coverImageURL;

    @Column
    private String followerCount;

    @Column
    private String followingCount;

    @Column
    private String description;

    @Column
    private String location;

    @Column
    private String url;

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
        return profileImageURL;
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

    public void setProfileImageURL(String profileImageURL) {
        this.profileImageURL = profileImageURL;
    }

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public static User fromJson(JSONObject jsonObject) {
        User user = new User();
        Log.d("user", jsonObject.toString());
        try {
            user.name = jsonObject.getString("name");
            user.uid = jsonObject.getLong("id");
            user.screenName = jsonObject.getString("screen_name");
            String image_url = jsonObject.getString("profile_image_url");
            user.profileImageURL = image_url.replace("_normal", "");
            user.coverImageURL = jsonObject.getString("profile_banner_url");
            user.verified = jsonObject.getBoolean("verified");
            user.followingCount = jsonObject.getString("friends_count");
            user.followerCount = jsonObject.getString("followers_count");
            user.location = jsonObject.getString("location");
            user.description = jsonObject.getString("description");
            user.url = jsonObject.getString("display_url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }

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
        parcel.writeString(url);
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
        url = in.readString();
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

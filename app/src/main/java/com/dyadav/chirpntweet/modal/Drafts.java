package com.dyadav.chirpntweet.modal;

import android.os.Parcel;
import android.os.Parcelable;

import com.dyadav.chirpntweet.data.TwitterDb;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(database = TwitterDb.class)
public class Drafts extends BaseModel implements Parcelable{
    @PrimaryKey(autoincrement = true)
    @Column
    int id;

    @Column
    String draft;

    public Drafts() {
    }

    protected Drafts(Parcel in) {
        id = in.readInt();
        draft = in.readString();
    }

    public static final Creator<Drafts> CREATOR = new Creator<Drafts>() {
        @Override
        public Drafts createFromParcel(Parcel in) {
            return new Drafts(in);
        }

        @Override
        public Drafts[] newArray(int size) {
            return new Drafts[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDraft() {
        return draft;
    }

    public void setDraft(String draft) {
        this.draft = draft;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(draft);
    }
}

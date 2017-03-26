package com.dyadav.chirpntweet.data;

import com.raizlabs.android.dbflow.annotation.Database;

@Database(name = TwitterDb.NAME, version = TwitterDb.VERSION)
public class TwitterDb {
    public static final String NAME = "TwitterDb";

    public static final int VERSION = 1;
}

package com.dyadav.chirpntweet.fragments;


public class UserTimelineFragment extends BaseTimelineFragment{

    @Override
    protected void fetchTimeline() {
        client.getUsersTimeline(fRequest, maxId - 1, user.getScreenName(), handler);
    }
}

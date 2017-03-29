package com.dyadav.chirpntweet.fragments;


public class UserTimelineFragment extends BaseTimelineFragment{

    @Override
    protected void fetchTimeline() {
        client.getMentionsTimeline(fRequest, maxId - 1, handler);
    }
}

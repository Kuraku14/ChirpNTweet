package com.dyadav.chirpntweet.fragments;


public class HomeTimelineFragment extends BaseTimelineFragment {
    @Override
    protected void fetchTimeline() {
        client.getHomeTimeline(fRequest, maxId - 1, handler);
    }
}

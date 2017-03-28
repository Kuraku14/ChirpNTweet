package com.dyadav.chirpntweet.fragments;

public class MentionsTimelineFragment extends BaseTimelineFragment{

    @Override
    protected void fetchTimeline() {
        client.getMentionsTimeline(fRequest, maxId, handler);
    }
}

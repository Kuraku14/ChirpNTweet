package com.dyadav.chirpntweet.fragments;


import android.view.View;

public class UserTimelineFragment extends BaseTimelineFragment{

    @Override
    protected void fetchTimeline() {
        binding.fab.setVisibility(View.GONE);
        client.getUsersTimeline(fRequest, maxId - 1, user.getScreenName(), handler);
    }
}

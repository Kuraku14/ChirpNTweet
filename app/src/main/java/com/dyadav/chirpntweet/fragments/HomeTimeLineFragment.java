package com.dyadav.chirpntweet.fragments;


import android.view.View;

public class HomeTimelineFragment extends BaseTimelineFragment {

    @Override
    protected void fetchTimeline() {
        binding.fab.setVisibility(View.VISIBLE);
        client.getHomeTimeline(fRequest, maxId - 1, handler);
    }
}

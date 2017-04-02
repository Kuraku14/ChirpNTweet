package com.dyadav.chirpntweet.fragments;

import android.view.View;

public class SearchTweetFragment  extends BaseTimelineFragment{
    @Override
    protected void fetchTimeline() {
        binding.fab.setVisibility(View.VISIBLE);
        client.searchQuery("Hello", maxId - 1, handler);
    }
}

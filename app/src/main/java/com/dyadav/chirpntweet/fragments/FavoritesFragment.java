package com.dyadav.chirpntweet.fragments;


import android.view.View;

public class FavoritesFragment extends BaseTimelineFragment {

    @Override
    protected void fetchTimeline() {
        binding.fab.setVisibility(View.GONE);
        client.getFavoriteTweets(fRequest, maxId - 1, user.getScreenName(), handler);
    }
}

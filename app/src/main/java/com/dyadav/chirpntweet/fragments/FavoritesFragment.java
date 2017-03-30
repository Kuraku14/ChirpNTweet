package com.dyadav.chirpntweet.fragments;


public class FavoritesFragment extends BaseTimelineFragment {

    @Override
    protected void fetchTimeline() {
        client.getFavoriteTweets(fRequest, maxId - 1, user.getScreenName(), handler);
    }
}

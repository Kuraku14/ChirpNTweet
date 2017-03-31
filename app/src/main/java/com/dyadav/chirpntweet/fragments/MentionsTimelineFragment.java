package com.dyadav.chirpntweet.fragments;

import android.view.View;

public class MentionsTimelineFragment extends BaseTimelineFragment{

    @Override
    protected void fetchTimeline() {
        binding.fab.setVisibility(View.GONE);
        client.getMentionsTimeline(fRequest, maxId - 1, handler);
    }
}

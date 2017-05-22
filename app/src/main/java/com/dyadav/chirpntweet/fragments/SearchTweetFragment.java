package com.dyadav.chirpntweet.fragments;

import android.os.Bundle;
import android.view.View;

import com.dyadav.chirpntweet.modal.Tweet;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class SearchTweetFragment  extends BaseTimelineFragment{

    String query;

    @Override
    protected void fetchTimeline() {
        client.searchQuery(query, maxId - 1, handler);
    }

    @Override
    void fetchUserInfo() {
        Bundle args = getArguments();
        user = args.getParcelable("user");
        query = args.getString("query");
    }

    JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            if (fRequest)
                mTweetList.clear();

            ArrayList<Tweet> newTweet = new ArrayList<>();
            JSONArray jsonArray = null;
            try {
                jsonArray = response.getJSONArray("statuses");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            
            if (jsonArray == null)
                return;

            Gson gson = new Gson();
            for(int i = 0; i < jsonArray.length(); i++) {
                Tweet tweet = null;
                try {
                    tweet = gson.fromJson(jsonArray.get(i).toString(),Tweet.class);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                newTweet.add(tweet);
            }

            mTweetList.addAll(newTweet);
            addToDb(newTweet);
            if (fRequest)
                mAdapter.notifyDataSetChanged();
            binding.swipeContainer.setRefreshing(false);
            binding.progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject object) {
            binding.swipeContainer.setRefreshing(false);
            binding.progressBar.setVisibility(View.GONE);
        }
    };
}

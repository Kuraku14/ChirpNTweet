package com.dyadav.chirpntweet.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;

import com.dyadav.chirpntweet.R;
import com.dyadav.chirpntweet.adapter.TweetAdapter;
import com.dyadav.chirpntweet.application.TwitterApplication;
import com.dyadav.chirpntweet.databinding.ActivityTimelineBinding;
import com.dyadav.chirpntweet.modal.Tweet;
import com.dyadav.chirpntweet.rest.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    private TwitterClient client;
    ArrayList<Tweet> mTweetList;
    private TweetAdapter mAdapter;
    private ActivityTimelineBinding binding;

    private String TAG = "TimelineActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_timeline);

        mTweetList = new ArrayList<>();
        mAdapter = new TweetAdapter(this, mTweetList);
        binding.rView.setAdapter(mAdapter);
        binding.rView.setItemAnimator(new DefaultItemAnimator());
        binding.rView.setLayoutManager(new LinearLayoutManager(this));

        client = TwitterApplication.getRestClient();
        populateTimeline();
    }

    private void populateTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                mTweetList.clear();
                mTweetList.addAll(Tweet.fromJSONArray(response));
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject object) {
                super.onFailure(statusCode, headers, throwable, object);
            }
        });
    }
}

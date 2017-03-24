package com.dyadav.chirpntweet.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.dyadav.chirpntweet.R;
import com.dyadav.chirpntweet.adapter.TweetAdapter;
import com.dyadav.chirpntweet.application.TwitterApplication;
import com.dyadav.chirpntweet.databinding.ActivityTimelineBinding;
import com.dyadav.chirpntweet.fragments.ComposeDialog;
import com.dyadav.chirpntweet.modal.Tweet;
import com.dyadav.chirpntweet.rest.TwitterClient;
import com.dyadav.chirpntweet.utils.EndlessRecyclerViewScrollListener;
import com.dyadav.chirpntweet.utils.NetworkUtility;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    private TwitterClient client;
    ArrayList<Tweet> mTweetList;
    private TweetAdapter mAdapter;
    EndlessRecyclerViewScrollListener scrollListener;
    LinearLayoutManager mLayoutManager;
    private ActivityTimelineBinding binding;

    private String TAG = "TimelineActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_timeline);
        client = TwitterApplication.getRestClient();
        //Setting toolbar
        setSupportActionBar(binding.toolbar);

        // Display icon in the toolbar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.twitter_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);


        mTweetList = new ArrayList<>();
        mAdapter = new TweetAdapter(this, mTweetList);
        binding.rView.setAdapter(mAdapter);
        binding.rView.setItemAnimator(new DefaultItemAnimator());

        //Recylerview decorater
        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        binding.rView.addItemDecoration(itemDecoration);

        mLayoutManager = new LinearLayoutManager(this);
        binding.rView.setLayoutManager(mLayoutManager);

        //Endless pagination
        scrollListener = new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                //Handle fetching in a thread with delay to avoid error "API Limit reached" = 429
                Handler handler = new Handler();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        populateTimeline(false, getMaxId());
                    }
                }, 1000);
            }
        };
        binding.rView.addOnScrollListener(scrollListener);

        //Swipe to refresh
        binding.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Check internet
                if(!NetworkUtility.isOnline()) {
                    Snackbar.make(binding.cLayout, "Check your internet Connection", Snackbar.LENGTH_LONG).show();
                    return;
                }
                //Fetch first page
                populateTimeline(true, 0);
            }
        });


        // Attach FAB listener
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ComposeDialog fDialog = new ComposeDialog();
                fDialog.show(TimelineActivity.this.getSupportFragmentManager(),"");
            }
        });

        //Fetch first page
        populateTimeline(true, 0);
    }

    private long getMaxId(){
        return mTweetList.get(mTweetList.size()-1).getUid();
    }

    private void populateTimeline(final Boolean fRequest, long id) {

        client.getHomeTimeline(fRequest, id, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d(TAG, String.valueOf(statusCode));
                Log.d(TAG, response.toString());
                if(fRequest)
                    mTweetList.clear();
                mTweetList.addAll(Tweet.fromJSONArray(response));
                mAdapter.notifyDataSetChanged();
                binding.swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject object) {
                Snackbar.make(binding.cLayout, "Error fetching tweets !", Snackbar.LENGTH_LONG).show();
                binding.swipeContainer.setRefreshing(false);
            }
        });
    }
}

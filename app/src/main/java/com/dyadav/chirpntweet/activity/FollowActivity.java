package com.dyadav.chirpntweet.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.dyadav.chirpntweet.R;
import com.dyadav.chirpntweet.adapter.FollowAdapter;
import com.dyadav.chirpntweet.application.TwitterApplication;
import com.dyadav.chirpntweet.databinding.ActivityFollowBinding;
import com.dyadav.chirpntweet.modal.User;
import com.dyadav.chirpntweet.rest.TwitterClient;
import com.dyadav.chirpntweet.utils.EndlessRecyclerViewScrollListener;
import com.dyadav.chirpntweet.utils.NetworkUtility;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static com.raizlabs.android.dbflow.config.FlowManager.getContext;

public class FollowActivity extends AppCompatActivity {

    private ActivityFollowBinding binding;
    private User user;
    private TwitterClient client;
    private String type;
    private FollowAdapter mAdapter;
    private EndlessRecyclerViewScrollListener scrollListener;
    private LinearLayoutManager mLayoutManager;
    private int cursor = -1;
    private ArrayList<User> mUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_follow);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        client = TwitterApplication.getRestClient();

        //Get data from intent
        Intent i = getIntent();
        user = i.getParcelableExtra("user");
        type = i.getStringExtra("list");

        //Setup adapter
        mUsers = new ArrayList<>();
        mAdapter = new FollowAdapter(getContext(), mUsers);
        binding.rvFollowers.setAdapter(mAdapter);
        binding.rvFollowers.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        binding.rvFollowers.addItemDecoration(itemDecoration);

        mLayoutManager = new LinearLayoutManager(getContext());
        binding.rvFollowers.setLayoutManager(mLayoutManager);

        scrollListener = new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                //Handle fetching in a thread with delay to avoid error "API Limit reached" = 429
                Handler handler = new Handler();
                handler.postDelayed(() -> selectTwitterAPI(), 1000);
            }
        };
        binding.rvFollowers.addOnScrollListener(scrollListener);

        //Set Activity title and back button
        if (type.equals("follower")) {
            getSupportActionBar().setTitle("Followers");
        } else {
            getSupportActionBar().setTitle("Following");
        }

        //Swipe to refresh
        binding.swipeContainer.setOnRefreshListener(() -> {
            if (!NetworkUtility.isOnline()) {
                //Snackbar.make(getView(), R.string.connection_error, Snackbar.LENGTH_LONG).show();
                binding.swipeContainer.setRefreshing(false);
                return;
            }
            cursor = -1;
            selectTwitterAPI();
        });

        //Call twitter API
        selectTwitterAPI();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void selectTwitterAPI() {
        if (type.equals("follower")) {
            populateFollowers(cursor, user.getScreenName());
        } else {
            populateFollowing(cursor, user.getScreenName());
        }
    }

    private void populateFollowing(int i, String screenName) {
        client.getFollowingList(String.valueOf(i), screenName, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (cursor == 0)
                    mUsers.clear();

                try {
                    ArrayList<User> user = fromJSONArray(response.getJSONArray("users"));
                    cursor = response.getInt("next_cursor");
                    mUsers.addAll(user);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mAdapter.notifyDataSetChanged();
                binding.swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    private void populateFollowers(int i, String screenName) {
        client.getFollowersList(String.valueOf(i), screenName, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (cursor == 0)
                    mUsers.clear();

                try {
                    ArrayList<User> user = fromJSONArray(response.getJSONArray("users"));
                    mUsers.addAll(user);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mAdapter.notifyDataSetChanged();
                binding.swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    private ArrayList<User> fromJSONArray(JSONArray jsonArray){

        ArrayList<User> list = new ArrayList<>();
        for(int i=0; i< jsonArray.length(); i++){

            try {
                Gson gson = new Gson();
                User user = gson.fromJson(jsonArray.getJSONObject(i).toString(), User.class);
                list.add(user);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
    }
}

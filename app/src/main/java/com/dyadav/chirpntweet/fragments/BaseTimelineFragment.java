package com.dyadav.chirpntweet.fragments;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dyadav.chirpntweet.R;
import com.dyadav.chirpntweet.activity.DetailedActivity;
import com.dyadav.chirpntweet.adapter.TweetAdapter;
import com.dyadav.chirpntweet.application.TwitterApplication;
import com.dyadav.chirpntweet.data.TwitterDb;
import com.dyadav.chirpntweet.databinding.FragmentTimelineBinding;
import com.dyadav.chirpntweet.modal.Tweet;
import com.dyadav.chirpntweet.modal.Tweet_Table;
import com.dyadav.chirpntweet.modal.User;
import com.dyadav.chirpntweet.rest.TwitterClient;
import com.dyadav.chirpntweet.utils.EndlessRecyclerViewScrollListener;
import com.dyadav.chirpntweet.utils.ItemClickSupport;
import com.dyadav.chirpntweet.utils.NetworkUtility;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.ProcessModelTransaction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public abstract class BaseTimelineFragment extends Fragment {

    protected TwitterClient client;
    protected ArrayList<Tweet> mTweetList;
    protected TweetAdapter mAdapter;
    protected EndlessRecyclerViewScrollListener scrollListener;
    protected LinearLayoutManager mLayoutManager;
    protected FragmentTimelineBinding binding;
    protected User user = null;
    protected boolean offlineData = false;
    protected boolean fRequest;
    protected Long maxId;

    public BaseTimelineFragment() {}

    JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
            if (fRequest)
                mTweetList.clear();

            ArrayList<Tweet> newTweet = new ArrayList<>();
            Gson gson = new Gson();
            for(int i = 0; i < response.length(); i++) {
                try {
                    Tweet tweet = gson.fromJson(response.getJSONObject(i).toString(),Tweet.class);
                    newTweet.add(tweet);
                } catch (JSONException e) {
                }
            }

            mTweetList.addAll(newTweet);
            addToDb(newTweet);
            mAdapter.notifyDataSetChanged();
            binding.swipeContainer.setRefreshing(false);
            binding.progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject object) {
            Toast.makeText(getContext(), R.string.error_fetch, Toast.LENGTH_SHORT).show();
            //Show offline data
            fetchOfflineTweets();
            binding.swipeContainer.setRefreshing(false);
            binding.progressBar.setVisibility(View.GONE);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.fragment_timeline, container, false);

        //Client instance
        client = TwitterApplication.getRestClient();

        fetchBundle();

        mTweetList = new ArrayList<>();
        mAdapter = new TweetAdapter(getContext(), mTweetList, user);
        binding.rView.setAdapter(mAdapter);
        binding.rView.setItemAnimator(new DefaultItemAnimator());

        //Recylerview decorater
        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        binding.rView.addItemDecoration(itemDecoration);

        mLayoutManager = new LinearLayoutManager(getContext());
        binding.rView.setLayoutManager(mLayoutManager);

        //Endless pagination
        scrollListener = new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                //Handle fetching in a thread with delay to avoid error "API Limit reached" = 429
                Handler handler = new Handler();

                handler.postDelayed(() -> populateTimeline(false, getMaxId()), 1000);
            }
        };
        binding.rView.addOnScrollListener(scrollListener);

        //Swipe to refresh
        binding.swipeContainer.setOnRefreshListener(() -> {
            //Check internet
            if (!NetworkUtility.isOnline()) {
                Toast.makeText(getContext(), R.string.connection_error, Toast.LENGTH_SHORT).show();
                binding.swipeContainer.setRefreshing(false);
                return;
            }
            //Fetch first page
            populateTimeline(true, 0);
        });

        //Click on tweet for detailed activity
        ItemClickSupport.addTo(binding.rView).setOnItemClickListener((recyclerView, position, v) -> {
            Intent intent = new Intent(getContext(), DetailedActivity.class);
            Tweet tweet = mTweetList.get(position);
            intent.putExtra("tweet", tweet);
            startActivityForResult(intent, 1);
        });

        // Attach FAB listener
        //binding.fab.setOnClickListener(view -> createComposeDialog(null));

        //Fetch first page
        populateTimeline(true, 0);

        //Show compose tweet with delay
        Handler handler = new Handler();

        handler.postDelayed(() -> {
            //Check for implicit Intent
            // Get intent, action and MIME type
            Intent intent = getActivity().getIntent();
            String action = intent.getAction();
            String type = intent.getType();
            if (Intent.ACTION_SEND.equals(action) && type != null) {
                if ("text/plain".equals(type)) {
                    createComposeDialog(intent.getStringExtra(Intent.EXTRA_SUBJECT) + " - " +
                            intent.getStringExtra(Intent.EXTRA_TEXT));
                }
            }
        }, 1000);

        return binding.getRoot();
    }

    void fetchBundle() {
        //Fetch user info
        Bundle args = getArguments();
        user = args.getParcelable("user");
    }

    public void createComposeDialog(String s) {
        ComposeDialog fDialog = new ComposeDialog();
        if (user != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("userinfo", user);
            bundle.putString("intentinfo", s);
            //Dialog listener
            fDialog.setFinishDialogListener(tweet -> {
                if (tweet != null) {
                    mTweetList.add(0, tweet);
                    mAdapter.notifyItemInserted(0);
                    binding.rView.scrollToPosition(0);
                }
            });
            fDialog.setArguments(bundle);
            fDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppThemeFullScreen);
            fDialog.show(getActivity().getSupportFragmentManager(), "");
        }
    }

    private long getMaxId() {
        return mTweetList.get(mTweetList.size() - 1).getUid();
    }

    private void populateTimeline(final boolean request, long id) {

        binding.progressBar.setVisibility(View.VISIBLE);

        maxId = id;
        fRequest = request;

        if (!NetworkUtility.isOnline()) {
            Toast.makeText(getContext(), R.string.connection_error, Toast.LENGTH_SHORT).show();
            binding.progressBar.setVisibility(View.GONE);
            //Show offline data
            fetchOfflineTweets();
            return;
        }

        Log.d("Fetch", String.valueOf(fRequest) + String.valueOf(maxId));

        fetchTimeline();
    }

    protected abstract void fetchTimeline();

    private void fetchOfflineTweets() {
        if (!offlineData) {
            List<Tweet> tweetsFromDb = SQLite.select().from(Tweet.class).orderBy(Tweet_Table.uid, false).queryList();
            mTweetList.addAll(tweetsFromDb);
            offlineData = true;
            mAdapter.notifyDataSetChanged();
        }
    }

    void addToDb(ArrayList<Tweet> newTweet) {
        FlowManager.getDatabase(TwitterDb.class)
                .beginTransactionAsync(new ProcessModelTransaction.Builder<>(
                        (ProcessModelTransaction.ProcessModel<Tweet>) (tweet, wrapper) -> tweet.save()).addAll(newTweet).build())
                .error((transaction, error) -> {

                })
                .success(transaction -> {

                }).build().execute();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}

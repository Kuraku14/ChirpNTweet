package com.dyadav.chirpntweet.fragments;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.dyadav.chirpntweet.R;
import com.dyadav.chirpntweet.activity.DetailedActivity;
import com.dyadav.chirpntweet.adapter.TweetAdapter;
import com.dyadav.chirpntweet.application.TwitterApplication;
import com.dyadav.chirpntweet.data.TwitterDb;
import com.dyadav.chirpntweet.databinding.FragmentHomeTimeLineBinding;
import com.dyadav.chirpntweet.modal.Tweet;
import com.dyadav.chirpntweet.modal.Tweet_Table;
import com.dyadav.chirpntweet.modal.User;
import com.dyadav.chirpntweet.rest.TwitterClient;
import com.dyadav.chirpntweet.utils.EndlessRecyclerViewScrollListener;
import com.dyadav.chirpntweet.utils.ItemClickSupport;
import com.dyadav.chirpntweet.utils.NetworkUtility;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ProcessModelTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class HomeTimeLineFragment extends Fragment {

    private TwitterClient client;
    ArrayList<Tweet> mTweetList;
    private TweetAdapter mAdapter;
    EndlessRecyclerViewScrollListener scrollListener;
    LinearLayoutManager mLayoutManager;
    private FragmentHomeTimeLineBinding binding;
    private User user = null;
    private boolean offlineData = false;

    //Constructor
    public HomeTimeLineFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                                                R.layout.fragment_home_time_line, container, false);

        setHasOptionsMenu(true);

        //Client instance
        client = TwitterApplication.getRestClient();

        mTweetList = new ArrayList<>();
        mAdapter = new TweetAdapter(getContext(), mTweetList);
        binding.rView.setAdapter(mAdapter);
        binding.rView.setItemAnimator(new DefaultItemAnimator());

        //Fetch logged in users info
        fetchUserInfo();

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
                if (!NetworkUtility.isOnline()) {
                    //Snackbar.make(getView(), R.string.connection_error, Snackbar.LENGTH_LONG).show();
                    binding.swipeContainer.setRefreshing(false);
                    return;
                }
                //Fetch first page
                populateTimeline(true, 0);
            }
        });

        //Click on tweet for detailed activity
        ItemClickSupport.addTo(binding.rView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Intent intent = new Intent(getContext(), DetailedActivity.class);
                Tweet tweet = mTweetList.get(position);
                intent.putExtra("tweet", tweet);
                startActivityForResult(intent, 1);
            }
        });

        // Attach FAB listener
        FloatingActionButton fab = (FloatingActionButton) binding.fab;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createComposeDialog(null);
            }
        });

        //Fetch first page
        populateTimeline(true, 0);

        //Show compose tweet with delay
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
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
            }
        }, 1000);

        return binding.getRoot();
    }

    private void createComposeDialog(String s) {
        ComposeDialog fDialog = new ComposeDialog();
        if (user != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("userinfo", user);
            bundle.putString("intentinfo", s);
            //Dialog listener
            fDialog.setFinishDialogListener(new ComposeDialog.ComposeTweetListener() {
                @Override
                public void onFinishDialog(Tweet tweet) {
                    if (tweet != null) {
                        mTweetList.add(0, tweet);
                        mAdapter.notifyItemInserted(0);
                        binding.rView.scrollToPosition(0);
                    }
                }
            });
            fDialog.setArguments(bundle);
            fDialog.show(getActivity().getSupportFragmentManager(), "");
        }

    }

    private void fetchUserInfo() {
        client.getAccountInfo(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                user = User.fromJson(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject object) {
                //Snackbar.make(binding.cLayout, R.string.user_info_error, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private long getMaxId() {
        return mTweetList.get(mTweetList.size() - 1).getUid();
    }

    private void populateTimeline(final boolean fRequest, long id) {

        binding.progressBar.setVisibility(View.VISIBLE);

        if (!NetworkUtility.isOnline()) {
            //Snackbar.make(binding.cLayout, R.string.connection_error, Snackbar.LENGTH_LONG).show();
            binding.progressBar.setVisibility(View.GONE);
            //Show offline data
            fetchOfflineTweets();
            return;
        }

        client.getHomeTimeline(fRequest, id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                if (fRequest)
                    mTweetList.clear();

                ArrayList<Tweet> newTweet = Tweet.fromJSONArray(response);
                mTweetList.addAll(newTweet);
                addToDb(newTweet);
                mAdapter.notifyDataSetChanged();
                binding.swipeContainer.setRefreshing(false);
                binding.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject object) {
                //Snackbar.make(binding.cLayout, R.string.error_fetch, Snackbar.LENGTH_LONG).show();
                //Show offline data
                fetchOfflineTweets();
                binding.swipeContainer.setRefreshing(false);
                binding.progressBar.setVisibility(View.GONE);
            }
        });
    }

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
                        new ProcessModelTransaction.ProcessModel<Tweet>() {
                            @Override
                            public void processModel(Tweet tweet, DatabaseWrapper wrapper) {
                                tweet.save();
                            }
                        }).addAll(newTweet).build())
                .error(new Transaction.Error() {
                    @Override
                    public void onError(Transaction transaction, Throwable error) {

                    }
                })
                .success(new Transaction.Success() {
                    @Override
                    public void onSuccess(Transaction transaction) {

                    }
                }).build().execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        //Fetch again to refersh screen
        populateTimeline(true, 0);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_scroll_to_top:
                binding.rView.smoothScrollToPosition(0);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

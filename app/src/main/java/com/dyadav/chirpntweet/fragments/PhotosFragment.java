package com.dyadav.chirpntweet.fragments;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dyadav.chirpntweet.R;
import com.dyadav.chirpntweet.adapter.ProfilePhotosAdapter;
import com.dyadav.chirpntweet.application.TwitterApplication;
import com.dyadav.chirpntweet.databinding.FragmentPhotosBinding;
import com.dyadav.chirpntweet.modal.Tweet;
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

public class PhotosFragment extends Fragment {

    private FragmentPhotosBinding binding;
    private ProfilePhotosAdapter mAdapter;
    private User user;
    private TwitterClient client;
    private ArrayList<String> mTweetPhotos;
    private EndlessRecyclerViewScrollListener scrollListener;
    private Long maxId = -1L;

    public PhotosFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.fragment_photos, container, false);

        mTweetPhotos = new ArrayList<>();

        //Fetch user infor from bundle and fetch tweets using screenname
        Bundle args = getArguments();
        user = args.getParcelable("user");
        client = TwitterApplication.getRestClient();

        //Setup grid manager
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),2);
        binding.rvPhotos.setLayoutManager(gridLayoutManager);
        mAdapter = new ProfilePhotosAdapter(mTweetPhotos, getActivity());
        binding.rvPhotos.setAdapter(mAdapter);
        binding.rvPhotos.setItemAnimator(new DefaultItemAnimator());

        //Endless pagination
        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                //Handle fetching in a thread with delay to avoid error "API Limit reached" = 429
                Handler handler = new Handler();
                handler.postDelayed(() -> fetchPhotos(false), 1000);
            }
        };
        binding.rvPhotos.addOnScrollListener(scrollListener);

        //Swipe to refresh
        binding.swipeContainer.setOnRefreshListener(() -> {
            //Check internet
            if (!NetworkUtility.isOnline()) {
                //Snackbar.make(getView(), R.string.connection_error, Snackbar.LENGTH_LONG).show();
                binding.swipeContainer.setRefreshing(false);
                return;
            }
            //Fetch first page
            fetchPhotos(true);
        });

        fetchPhotos(true);

        //Click on photos to open a new Screen
        return binding.getRoot();
    }

    private void fetchPhotos(boolean fRequest) {
        binding.progressBar.setVisibility(View.VISIBLE);

        client.getUsersTimeline(fRequest, maxId - 1, user.getScreenName(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                if (fRequest)
                    mTweetPhotos.clear();

                ArrayList<String> newTweet = new ArrayList<>();
                Gson gson = new Gson();
                for(int i = 0; i < response.length(); i++) {
                    try {
                        Tweet tweet = gson.fromJson(response.getJSONObject(i).toString(),Tweet.class);
                        if(tweet.getEntities()!=null && tweet.getEntities().getMedia()!=null &&
                                !tweet.getEntities().getMedia().isEmpty()  &&
                                tweet.getEntities().getMedia().get(0).getMediaUrlHttps()!=null)
                                    newTweet.add(tweet.getEntities().getMedia().get(0).getMediaUrlHttps());

                        if (maxId < tweet.getUid()) {
                            maxId = tweet.getUid();
                        }
                    } catch (JSONException e) {
                    }
                }

                mTweetPhotos.addAll(newTweet);
                mAdapter.notifyDataSetChanged();
                binding.swipeContainer.setRefreshing(false);
                binding.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject object) {
                //Snackbar.make(binding.cLayout, R.string.error_fetch, Snackbar.LENGTH_LONG).show();
                binding.swipeContainer.setRefreshing(false);
                binding.progressBar.setVisibility(View.GONE);
            }
        });
    }
}

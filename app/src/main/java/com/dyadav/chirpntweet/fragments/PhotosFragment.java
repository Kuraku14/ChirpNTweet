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
import android.widget.Toast;

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
    private Long maxId = -1L;

    public PhotosFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.fragment_photos, container, false);

        mTweetPhotos = new ArrayList<>();

        Bundle args = getArguments();
        user = args.getParcelable("user");
        client = TwitterApplication.getRestClient();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),2);
        binding.rvPhotos.setLayoutManager(gridLayoutManager);
        mAdapter = new ProfilePhotosAdapter(mTweetPhotos, getActivity().getApplicationContext());
        binding.rvPhotos.setAdapter(mAdapter);
        binding.rvPhotos.setItemAnimator(new DefaultItemAnimator());

        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Handler handler = new Handler();
                handler.postDelayed(() -> fetchPhotos(false), 1000);
            }
        };
        binding.rvPhotos.addOnScrollListener(scrollListener);

        binding.swipeContainer.setOnRefreshListener(() -> {
            if (!NetworkUtility.isOnline(getActivity().getApplicationContext())) {
                Toast.makeText(getContext(), R.string.connection_error, Toast.LENGTH_SHORT).show();
                binding.swipeContainer.setRefreshing(false);
                return;
            }
            fetchPhotos(true);
        });

        fetchPhotos(true);
        return binding.getRoot();
    }

    private void fetchPhotos(boolean fRequest) {
        if (!NetworkUtility.isOnline(getActivity().getApplicationContext())) {
            Toast.makeText(getContext(), R.string.connection_error, Toast.LENGTH_SHORT).show();
            binding.swipeContainer.setRefreshing(false);
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);

        client.getUsersTimeline(fRequest, maxId - 1, user.getScreenName(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Tweet tweet = null;
                if (fRequest)
                    mTweetPhotos.clear();

                ArrayList<String> newTweet = new ArrayList<>();
                Gson gson = new Gson();
                for(int i = 0; i < response.length(); i++) {
                    try {
                        tweet = gson.fromJson(response.getJSONObject(i).toString(),Tweet.class);
                        if(tweet.getEntities()!=null && tweet.getEntities().getMedia()!=null &&
                                !tweet.getEntities().getMedia().isEmpty()  &&
                                tweet.getEntities().getMedia().get(0).getMediaUrlHttps()!=null)
                            newTweet.add(tweet.getEntities().getMedia().get(0).getMediaUrlHttps());
                    } catch (JSONException ignored) {
                    }
                }

                if (tweet != null)
                    maxId = tweet.getUid();

                if (newTweet.size() > 0) {
                    mTweetPhotos.addAll(newTweet);
                    mAdapter.notifyItemRangeInserted(mAdapter.getItemCount(), mTweetPhotos.size()-1);
                    binding.swipeContainer.setRefreshing(false);
                    binding.progressBar.setVisibility(View.GONE);
                } else {
                    fetchPhotos(false);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject object) {
                binding.swipeContainer.setRefreshing(false);
                binding.progressBar.setVisibility(View.GONE);
            }
        });
    }
}

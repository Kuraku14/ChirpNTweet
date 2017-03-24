package com.dyadav.chirpntweet.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.dyadav.chirpntweet.R;
import com.dyadav.chirpntweet.application.TwitterApplication;
import com.dyadav.chirpntweet.databinding.ComposeFragmentBinding;
import com.dyadav.chirpntweet.modal.Tweet;
import com.dyadav.chirpntweet.modal.User;
import com.dyadav.chirpntweet.rest.TwitterClient;
import com.dyadav.chirpntweet.utils.KeyboardUtility;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ComposeDialog extends DialogFragment {

    private ComposeFragmentBinding binding;
    int MAX_TWEET_LENGTH = 140;

    public ComposeDialog() {}

    public interface ComposeTweetListener {
        void onFinishDialog(Tweet tweet);
    }

    private ComposeTweetListener mListener;

    public void setFinishDialogListener(ComposeTweetListener listener) {
        mListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.rounded_corner_dialog);

        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                                            R.layout.compose_fragment, container, false);

        //Fetch logged in user info
        Bundle bundle = getArguments();
        if (bundle != null) {
            User user = bundle.getParcelable("userinfo");

            Glide.with(getContext())
                    .load(user.getProfileImageURL())
                    .into(binding.profileImage);

            binding.userName.setText(user.getName());
            binding.screenName.setText("@" + user.getScreenName());
        }

        //Attach a listener to count tweet length
        binding.tweetBody.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int remain_char = MAX_TWEET_LENGTH-charSequence.length();
                binding.tweetCount.setText(String.valueOf(remain_char));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //Tweet when button clicked
        binding.tweetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Hide Keyboard
                KeyboardUtility.hideKeyboard(getContext(), getView());
                postTweet();
            }
        });

        //Close fragment when close button pressed
        binding.closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //prompt the user if there is exsisting text
                //Hide Keyboard
                KeyboardUtility.hideKeyboard(getContext(), getView());
                dismiss();
            }
        });

        return binding.getRoot();
    }

    private void postTweet() {
        TwitterClient client;

        client = TwitterApplication.getRestClient();

        client.postTweet( binding.tweetBody.getText().toString(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //Send the data back to HomeTimeline to be added manually
                Tweet tweet = Tweet.fromJson(response);
                mListener.onFinishDialog(tweet);
                dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Snackbar.make(getView(), "Check your internet Connection", Snackbar.LENGTH_LONG).show();
            }
        });
    }
}

package com.dyadav.chirpntweet.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
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
    int remain_char;
    SharedPreferences sharedpreferences;

    public static final String tweetDraft = "draft" ;
    public static final String tweet = "tweet";

    public ComposeDialog() {}

    public interface ComposeTweetListener {
        void onFinishDialog(Tweet tweet);
    }

    private ComposeTweetListener mListener;

    public void setFinishDialogListener(ComposeTweetListener listener) {
        mListener = listener;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if (remain_char > 0)
            notifySave();
    }
    
    private void notifySave() {
        sharedpreferences = getContext().getSharedPreferences(tweetDraft, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedpreferences.edit();

        //Show a popup to save draft to Shared pref
        new AlertDialog.Builder(getContext())
                .setTitle("")
                .setMessage("Do you want to save the tweet ?")
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        editor.putString(tweet, binding.tweetBody.getText().toString());
                        editor.commit();
                        dismiss();
                    }
                })
                .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Empty the shared pref for any draft
                        editor.putString(tweet, null);
                        editor.commit();
                        dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String str = null;
        User user;

        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.rounded_corner_dialog);

        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                                            R.layout.compose_fragment, container, false);

        //Fetch logged in user info
        Bundle bundle = getArguments();
        if (bundle != null) {
            user = bundle.getParcelable("userinfo");
            str = bundle.getString("intentinfo", null);
            Glide.with(getContext())
                    .load(user.getProfileImageURL())
                    .into(binding.profileImage);

            binding.userName.setText(user.getName());
            binding.screenName.setText("@" + user.getScreenName());
        }

        //Fetch from shared preference and saved draft and display
        sharedpreferences = getContext().getSharedPreferences(tweetDraft, Context.MODE_PRIVATE);
        String draft = sharedpreferences.getString(tweet, null);

        if (str != null)
            binding.tweetBody.setText(str);
        else if (draft != null)
            binding.tweetBody.setText(draft);

        //Attach a listener to count tweet length
        binding.tweetBody.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                remain_char = MAX_TWEET_LENGTH-charSequence.length();
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
                //Hide Keyboard
                KeyboardUtility.hideKeyboard(getContext(), getView());
                //prompt the user if there is exsisting text
                if (remain_char > 0)
                    notifySave();
                else
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

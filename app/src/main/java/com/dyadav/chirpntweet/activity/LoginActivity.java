package com.dyadav.chirpntweet.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.codepath.oauth.OAuthLoginActivity;
import com.dyadav.chirpntweet.R;
import com.dyadav.chirpntweet.databinding.ActivityLoginBinding;
import com.dyadav.chirpntweet.rest.TwitterClient;

public class LoginActivity extends OAuthLoginActivity<TwitterClient> {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
    }

    public void loginToRest(View view) {
        getClient().connect();
    }

    @Override
    public void onLoginSuccess() {
        Intent i = new Intent(this, TimelineActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onLoginFailure(Exception e) {
        e.printStackTrace();
    }
}

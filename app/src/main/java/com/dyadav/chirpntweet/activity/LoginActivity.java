package com.dyadav.chirpntweet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.codepath.oauth.OAuthLoginActivity;
import com.dyadav.chirpntweet.R;
import com.dyadav.chirpntweet.application.TwitterApplication;
import com.dyadav.chirpntweet.modal.User;
import com.dyadav.chirpntweet.rest.TwitterClient;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends OAuthLoginActivity<TwitterClient> {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setBackgroundDrawable(null);
    }

    public void loginToRest(View view) {
        getClient().connect();
    }

    @Override
    public void onLoginSuccess() {
        Button button = (Button) findViewById(R.id.loginButton);
        button.setVisibility(View.GONE);

        TwitterClient client = TwitterApplication.getRestClient();
        client.getAccountInfo(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                User user;
                Gson gson = new Gson();
                user = gson.fromJson(response.toString(), User.class);
                Intent i = new Intent(LoginActivity.this, TimelineActivity.class);
                i.putExtra("user", user);
                startActivity(i);
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject object) {
                Toast.makeText(LoginActivity.this, R.string.user_info_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onLoginFailure(Exception e) {
        e.printStackTrace();
    }
}

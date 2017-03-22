package com.dyadav.chirpntweet.rest;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

public class TwitterClient extends OAuthBaseClient{

    public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class;
    public static final String REST_URL = "https://api.twitter.com/1.1";
    public static final String REST_CONSUMER_KEY = "UOW4l50S5Czlcp66LXrNiGeEg";
    public static final String REST_CONSUMER_SECRET = "9TkjldnM1BRot6mifv8Ttm0a0I66LAQcFqe2txtoL1EaMpZShx";
    public static final String REST_CALLBACK_URL = "x-oauthflow-twitter://chirpntweet.com";

    public TwitterClient(Context context) {
        super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
    }

    //1. Get Twitter Timeline
    public void getHomeTimeline(int page, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/home_timeline.json");
        RequestParams params = new RequestParams();
        params.put("count", 100);
        params.put("since_id", page);
        getClient().get(apiUrl, params, handler);
    }

    //2. Post a tweet
    public void postTweet(String message, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/update.json");
        RequestParams params = new RequestParams();
        params.put("status", message);
        getClient().post(apiUrl, params, handler);
    }

    //3. Retweet

    //4. Favorite a tweet

}

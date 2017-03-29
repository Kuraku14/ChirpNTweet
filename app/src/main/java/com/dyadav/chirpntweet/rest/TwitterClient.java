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
    public void getHomeTimeline(boolean fRequest, long id, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/home_timeline.json");
        RequestParams params = new RequestParams();
        params.put("count", 15);
        //only for subsequent requests
        if (!fRequest)
            params.put("max_id", id);
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
    public void retweet(boolean retweet, long id, AsyncHttpResponseHandler handler){
        String apiUrl;
        if(retweet){
            apiUrl = getApiUrl("statuses/retweet/"+id+".json");
        }else{
            apiUrl = getApiUrl("statuses/unretweet/"+id+".json");
        }
        getClient().post(apiUrl,handler);
    }

    //4. Favorite a tweet
    public void setFavorite(boolean fav, long id, AsyncHttpResponseHandler handler){
        String apiUrl;
        if(fav){
            apiUrl = getApiUrl("favorites/create.json");
        }else{
            apiUrl = getApiUrl("favorites/destroy.json");
        }
        RequestParams params = new RequestParams();
        params.put("id", id);
        getClient().post(apiUrl,params,handler);
    }

    //5. Get in logged in user info
    public void getAccountInfo(AsyncHttpResponseHandler handler){
        String apiUrl = getApiUrl("account/verify_credentials.json");
        RequestParams params = new RequestParams();
        client.get(apiUrl, params, handler);
    }

    //6. Send Message

    //7. Reply to Tweet
    public void postReply(long id, String status, AsyncHttpResponseHandler handler){
        String apiUrl = getApiUrl("statuses/update.json");
        RequestParams params = new RequestParams();
        params.put("status", status);
        params.put("in_reply_to_status_id", id);
        getClient().post(apiUrl,params,handler);
    }

    //8. Get Mentions timeline
    public void getMentionsTimeline(boolean fRequest, long id, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/mentions_timeline.json");
        RequestParams params = new RequestParams();
        params.put("count", 15);
        if(!fRequest)
            params.put("max_id",id);

        getClient().get(apiUrl,params,handler);
    }

    //9. User's Timeline
    public void getUsersTimeline(boolean fRequest, long id, String screenName, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/user_timeline.json");
        RequestParams params = new RequestParams();
        params.put("count", 15);
        params.put("screen_name", screenName);
        if(!fRequest)
            params.put("max_id",id);

        getClient().get(apiUrl,params,handler);
    }
}

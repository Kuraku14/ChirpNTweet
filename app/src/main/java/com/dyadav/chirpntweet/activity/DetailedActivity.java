package com.dyadav.chirpntweet.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dyadav.chirpntweet.R;
import com.dyadav.chirpntweet.application.TwitterApplication;
import com.dyadav.chirpntweet.databinding.ActivityDetailedBinding;
import com.dyadav.chirpntweet.modal.Tweet;
import com.dyadav.chirpntweet.modal.User;
import com.dyadav.chirpntweet.rest.TwitterClient;
import com.dyadav.chirpntweet.utils.DateUtility;
import com.dyadav.chirpntweet.utils.KeyboardUtility;
import com.dyadav.chirpntweet.utils.PatternEditableBuilder;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class DetailedActivity extends AppCompatActivity {

    @BindView(R.id.favorite_icon)
    ImageButton favorite_icon;

    @BindView(R.id.retweet_icon)
    ImageButton retweet_icon;

    @BindView(R.id.retweet_count)
    TextView retweet_count;

    @BindView(R.id.facvorite_count)
    TextView favorite_count;

    private ActivityDetailedBinding binding;
    TwitterClient client;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detailed);
        ButterKnife.bind(this);

        //Setting toolbar
        setSupportActionBar(binding.toolbar);

        // Display icon in the toolbar
        getSupportActionBar().setDisplayUseLogoEnabled(false);
        getSupportActionBar().setTitle("Tweet");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Get tweet info and display
        final Tweet tweet = getIntent().getExtras().getParcelable("tweet");
        user = getIntent().getParcelableExtra("user");
        Glide.with(this)
                .load(tweet.getUser().getProfileImageURL())
                .into(binding.profileImage);

        binding.userName.setText(tweet.getUser().getName());
        binding.screenName.setText("@" + tweet.getUser().getScreenName());
        binding.tweetBody.setText(tweet.getBody());

        new PatternEditableBuilder().
                addPattern(Pattern.compile("\\@(\\w+)"), Color.BLUE,
                        text -> openProfileView(text)).into(binding.tweetBody);

        new PatternEditableBuilder().
                addPattern(Pattern.compile("\\#(\\w+)"), Color.BLUE,
                        text -> openTrendsView(text)).into(binding.tweetBody);

        binding.timeStamp.setText(DateUtility.detailedViewFormatDate(tweet.getCreatedAt()));
        if(tweet.getUser().getVerified())
            binding.verified.setImageDrawable(getResources().getDrawable(R.drawable.verified));

        if(tweet.getFavorited())
            favorite_icon.setImageDrawable(getResources().getDrawable(R.drawable.red_heart));

        if(tweet.getRetweeted())
            retweet_icon.setImageDrawable(getResources().getDrawable(R.drawable.green_retweet));
        favorite_count.setText(String.valueOf(tweet.getFavoriteCount()));
        retweet_count.setText(String.valueOf(tweet.getRetweetCount()));

        if(tweet.getEntities()!=null && tweet.getEntities().getMedia()!=null &&
                !tweet.getEntities().getMedia().isEmpty()  &&
                tweet.getEntities().getMedia().get(0).getMediaUrlHttps()!=null) {
            Glide.with(this)
                    .load(tweet.getEntities().getMedia().get(0).getMediaUrlHttps())
                    .bitmapTransform(new RoundedCornersTransformation(this,20,0))
                    .diskCacheStrategy( DiskCacheStrategy.SOURCE )
                    .into(binding.tweetImage);
            binding.tweetImage.setVisibility(View.VISIBLE);
        } else {
            binding.tweetImage.setVisibility(View.GONE);
        }

        binding.replyTweet.setText("@" + tweet.getUser().getScreenName());
        binding.tweetCount.setText(String.valueOf((140- binding.replyTweet.length())));

        //Attach a listener to count tweet length
        binding.replyTweet.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.tweetCount.setText(String.valueOf(140-charSequence.length()));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.retweetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Hide Keyboard
                KeyboardUtility.hideKeyboard(DetailedActivity.this, v);
                replyTweet(tweet.getUid(), binding.replyTweet.getText().toString());
            }
        });

        favorite_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markFavorite(tweet.isFavorited(), tweet.getUid());
            }
        });

        retweet_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retweet(tweet.isRetweeted(), tweet.getUid());
            }
        });
    }

    private void openTrendsView(String text) {
        Intent i = new Intent(this, SearchActivity.class);
        i.putExtra("query", text);
        i.putExtra("user", user);
        startActivity(i);
    }

    private void openProfileView(String text) {
        //Get user info using screename and launch profile activity
        client = TwitterApplication.getRestClient();
        client.lookupUser(text.replace("@", ""), new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    Log.d("user", response.get(0).toString());
                    Gson gson = new Gson();
                    User spanUser = gson.fromJson(response.get(0).toString(), User.class);

                    Intent intent = new Intent(DetailedActivity.this, ProfileActivity.class);
                    intent.putExtra("user", spanUser);
                    DetailedActivity.this.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void markFavorite(boolean favorite, Long id) {
        client = TwitterApplication.getRestClient();
        client.setFavorite(!favorite, id,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Gson gson = new Gson();
                    Tweet tweet = gson.fromJson(response.toString(), Tweet.class);
                    //Update icon and count
                    favorite_count.setText(String.valueOf(tweet.getFavoriteCount()));
                    if(tweet.getFavorited())
                        favorite_icon.setImageDrawable(getResources().getDrawable(R.drawable.red_heart));
                    else
                        favorite_icon.setImageDrawable(getResources().getDrawable(R.drawable.heart));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            }
        });
    }

    private void retweet(boolean isRetweet, Long id) {
        client = TwitterApplication.getRestClient();
        client.retweet(!isRetweet, id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Gson gson = new Gson();
                    Tweet tweet = gson.fromJson(response.toString(), Tweet.class);
                    //Update icon and count
                    retweet_count.setText(String.valueOf(tweet.getRetweetCount()));

                    if(tweet.getRetweeted())
                        retweet_icon.setImageDrawable(getResources().getDrawable(R.drawable.green_retweet));
                    else
                        retweet_icon.setImageDrawable(getResources().getDrawable(R.drawable.retweet));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            }
        });
    }

    private void replyTweet(long uid, String text) {
        client = TwitterApplication.getRestClient();
        client.postReply( uid, text, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Snackbar.make(binding.cLayout, R.string.reply_success, Snackbar.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Snackbar.make(binding.cLayout, R.string.reply_failed, Snackbar.LENGTH_LONG).show();
            }
        });
    }
}

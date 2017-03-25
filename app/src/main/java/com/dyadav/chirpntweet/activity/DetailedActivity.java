package com.dyadav.chirpntweet.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dyadav.chirpntweet.R;
import com.dyadav.chirpntweet.databinding.ActivityDetailedBinding;
import com.dyadav.chirpntweet.modal.Tweet;
import com.dyadav.chirpntweet.utils.DateUtility;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detailed);
        ButterKnife.bind(this);
        //Setting toolbar
        setSupportActionBar(binding.toolbar);

        // Display icon in the toolbar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.twitter_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        //Get tweet info and display
        Tweet tweet = getIntent().getExtras().getParcelable("tweet");
        Glide.with(this)
                .load(tweet.getUser().getProfileImageURL())
                .into(binding.profileImage);

        binding.userName.setText(tweet.getUser().getName());
        binding.screenName.setText("@" + tweet.getUser().getScreenName());
        binding.tweetBody.setText(tweet.getBody());
        binding.timeStamp.setText(DateUtility.getRelativeTimeAgo(tweet.getCreatedAt()));
        if(tweet.getUser().getVerified())
            binding.verified.setImageDrawable(getResources().getDrawable(R.drawable.verified));
        if(tweet.getFavorited())
            favorite_icon.setImageDrawable(getResources().getDrawable(R.drawable.red_heart));

        if(tweet.getRetweeted())
            retweet_icon.setImageDrawable(getResources().getDrawable(R.drawable.green_retweet));
        favorite_count.setText(String.valueOf(tweet.getFavoriteCount()));
        retweet_count.setText(String.valueOf(tweet.getRetweetCount()));
    }
}

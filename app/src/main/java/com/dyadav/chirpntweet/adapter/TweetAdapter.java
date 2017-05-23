package com.dyadav.chirpntweet.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dyadav.chirpntweet.R;
import com.dyadav.chirpntweet.modal.Tweet;
import com.dyadav.chirpntweet.modal.User;
import com.dyadav.chirpntweet.rest.TwitterClient;
import com.dyadav.chirpntweet.utils.DateUtility;
import com.dyadav.chirpntweet.utils.PatternEditableBuilder;

import java.util.ArrayList;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TweetAdapter extends
        RecyclerView.Adapter<TweetAdapter.MyViewHolder> {

    private ArrayList<Tweet> tweetList;
    private Context context;
    private TwitterClient client;
    private User loggedUser;

    public interface profileClickListener {
        void onProfileClicked(User loggedUser, User user);
    }

    private profileClickListener pListener;

    public interface favClickListener {
        void onFavClicked(boolean favorited, long uid, int position);
    }

    private favClickListener fListener;

    public interface retweetClickListener {
        void onRetweetClicked(boolean retweeted, long uid, int position);
    }

    private retweetClickListener rListener;

    public interface replyClickListener {
        void onReplyClicked(User loggedUser, Tweet tweet);
    }

    private replyClickListener repListener;

    public interface hashtagClickListener {
        void hashtagClicked(User loggedUser, String text);
    }

    private hashtagClickListener hListener;

    public interface atClickListener {
        void atClicked(String text);
    }

    private atClickListener aListener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.userName)
        TextView userName;

        @BindView(R.id.tweetBody)
        TextView tweetBody;

        @BindView(R.id.profileImage)
        ImageView userProfileImage;

        @BindView(R.id.screenName)
        TextView screenName;

        @BindView(R.id.verified)
        ImageView verifiedSymbol;

        @BindView(R.id.timeStamp)
        TextView timeStamp;

        @BindView(R.id.reply_icon)
        ImageButton reply;

        @BindView(R.id.retweet_icon)
        ImageButton retweet;

        @BindView(R.id.favorite_icon)
        ImageButton favorite;

        @BindView(R.id.message_icon)
        ImageButton message;

        @BindView(R.id.favorite_count)
        TextView favCount;

        @BindView(R.id.retweet_count)
        TextView retweetCount;

        @BindView(R.id.tweetImage)
        ImageView tweetImage;

        MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public TweetAdapter(Context context, ArrayList<Tweet> tweetList, User loggedUser) {
        this.tweetList = tweetList;
        this.context = context;
        this.loggedUser = loggedUser;
        this.fListener = null;
        this.repListener = null;
        this.rListener = null;
        this.hListener = null;
        this.aListener = null;
        this.pListener = null;
    }

    public void setFavClickListener(favClickListener listener) {
        this.fListener = listener;
    }

    public void setReplyClickListener(replyClickListener listener) {
        this.repListener = listener;
    }

    public void setRetweetClickListener(retweetClickListener listener) {
        this.rListener = listener;
    }

    public void setProfileClickListener(profileClickListener listener) {
        this.pListener = listener;
    }

    public void setHashtagClickListener(hashtagClickListener listener) {
        this.hListener = listener;
    }

    public void setAtClickListener(atClickListener listener) {
        this.aListener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tweet, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Tweet tweet = tweetList.get(position);

        if (tweet != null) {
            Glide.with(context)
                .load(tweet.getUser().getProfileImageURL())
                .bitmapTransform(new RoundedCornersTransformation(context,30,0))
                .diskCacheStrategy( DiskCacheStrategy.SOURCE )
                .into(holder.userProfileImage);

            holder.tweetImage.setImageResource(0);
            if(tweet.getEntities()!=null && tweet.getEntities().getMedia()!=null &&
                    !tweet.getEntities().getMedia().isEmpty()  &&
                    tweet.getEntities().getMedia().get(0).getMediaUrlHttps()!=null) {
                holder.tweetImage.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(tweet.getEntities().getMedia().get(0).getMediaUrlHttps())
                        .bitmapTransform(new RoundedCornersTransformation(context,30,0))
                        .diskCacheStrategy( DiskCacheStrategy.SOURCE )
                        .into(holder.tweetImage);
            } else {
                holder.tweetImage.setVisibility(View.GONE);
            }

            holder.userName.setText(tweet.getUser().getName());
            holder.screenName.setText("@" + tweet.getUser().getScreenName());
            holder.tweetBody.setText(tweet.getBody());
            holder.timeStamp.setText(DateUtility.getRelativeTimeAgo(tweet.getCreatedAt()));
            if(tweet.getUser().getVerified()) {
                holder.verifiedSymbol.setImageDrawable(context.getResources().getDrawable(R.drawable.verified));
                holder.verifiedSymbol.setVisibility(View.VISIBLE);
            } else {
                holder.verifiedSymbol.setVisibility(View.GONE);
            }
            if(tweet.getFavorited())
                holder.favorite.setImageDrawable(context.getResources().getDrawable(R.drawable.red_heart));
            else
                holder.favorite.setImageDrawable(context.getResources().getDrawable(R.drawable.heart));

            if(tweet.getRetweeted())
                holder.retweet.setImageDrawable(context.getResources().getDrawable(R.drawable.green_retweet));
            else
                holder.retweet.setImageDrawable(context.getResources().getDrawable(R.drawable.retweet));
            holder.favCount.setText(String.valueOf(tweet.getFavoriteCount()));
            holder.retweetCount.setText(String.valueOf(tweet.getRetweetCount()));

            holder.userProfileImage.setOnClickListener(v -> pListener.onProfileClicked(loggedUser, tweet.getUser()));

            holder.favorite.setOnClickListener(v -> fListener.onFavClicked(tweet.isFavorited(), tweet.getUid(), position));

            holder.retweet.setOnClickListener(v -> rListener.onRetweetClicked(tweet.isRetweeted(), tweet.getUid(), position));

            holder.reply.setOnClickListener(v -> repListener.onReplyClicked(loggedUser, tweet));

            new PatternEditableBuilder().
                    addPattern(Pattern.compile("@(\\w+)"), Color.BLUE,
                            this::openProfileView).into(holder.tweetBody);

            new PatternEditableBuilder().
                    addPattern(Pattern.compile("#(\\w+)"), Color.BLUE,
                            this::openTrendsView).into(holder.tweetBody);
        }
    }

    private void openTrendsView(String text) {
        hListener.hashtagClicked(loggedUser, text);
    }

    private void openProfileView(String text) {
        aListener.atClicked(text);
    }

    @Override
    public int getItemCount() {
        return tweetList.size();
    }
}

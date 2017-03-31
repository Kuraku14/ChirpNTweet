package com.dyadav.chirpntweet.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.dyadav.chirpntweet.activity.DetailedActivity;
import com.dyadav.chirpntweet.activity.ProfileActivity;
import com.dyadav.chirpntweet.application.TwitterApplication;
import com.dyadav.chirpntweet.modal.Media;
import com.dyadav.chirpntweet.modal.Tweet;
import com.dyadav.chirpntweet.rest.TwitterClient;
import com.dyadav.chirpntweet.utils.DateUtility;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TweetAdapter extends
        RecyclerView.Adapter<TweetAdapter.MyViewHolder> {

    private ArrayList<Tweet> tweetList;
    private Context context;
    private TwitterClient client;

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

        @BindView(R.id.facvorite_count)
        TextView favCount;

        @BindView(R.id.retweet_count)
        TextView retweetCount;

        @BindView(R.id.tweetImage)
        ImageView tweetImage;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public TweetAdapter(Context context, ArrayList<Tweet> tweetList) {
        this.tweetList = tweetList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tweet, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Tweet tweet = tweetList.get(position);

        if (tweet != null) {
            Glide.with(context)
                .load(tweet.getUser().getProfileImageURL())
                .into(holder.userProfileImage);

            Media media = tweet.getMedia();
            Media exMedia = tweet.getExtendedMedia();

            if(media != null) {
                holder.tweetImage.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(media.getMediaUrlHttps())
                        .bitmapTransform(new RoundedCornersTransformation(context,20,0))
                        .diskCacheStrategy( DiskCacheStrategy.SOURCE )
                        .into(holder.tweetImage);
            } else {
                holder.tweetImage.setVisibility(View.GONE);
            }


            holder.userName.setText(tweet.getUser().getName());
            holder.screenName.setText("@" + tweet.getUser().getScreenName());
            holder.tweetBody.setText(tweet.getBody());
            holder.timeStamp.setText(DateUtility.getRelativeTimeAgo(tweet.getCreatedAt()));
            if(tweet.getUser().getVerified())
                holder.verifiedSymbol.setImageDrawable(context.getResources().getDrawable(R.drawable.verified));
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

            holder.userProfileImage.setOnClickListener(v -> {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("user", tweet.getUser());
                context.startActivity(intent);
            });

            holder.favorite.setOnClickListener(v -> markFavorite(tweet.isFavorited(), tweet.getUid(), position));

            holder.retweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    retweet(tweet.isRetweeted(), tweet.getUid(), position);
                }
            });

            holder.reply.setOnClickListener(v -> {
                Intent intent = new Intent(context, DetailedActivity.class);
                intent.putExtra("tweet", tweet);
                context.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return tweetList.size();
    }

    private void markFavorite(boolean favorite, Long id, final int position) {
        client = TwitterApplication.getRestClient();
        client.setFavorite(!favorite, id,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    //JSONObject jsonObject = response.getJSONObject(0);
                    Tweet tweet = Tweet.fromJson(response);
                    tweetList.set(position, tweet);
                    notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            }
        });
    }

    private void retweet(boolean isRetweet, Long id, final int position) {
        client = TwitterApplication.getRestClient();
        client.retweet(!isRetweet, id,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    //JSONObject jsonObject = response.getJSONObject(0);
                    Tweet tweet = Tweet.fromJson(response);
                    tweetList.set(position, tweet);
                    notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            }
        });
    }
}

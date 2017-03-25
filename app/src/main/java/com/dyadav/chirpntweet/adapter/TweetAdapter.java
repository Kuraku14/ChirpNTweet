package com.dyadav.chirpntweet.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dyadav.chirpntweet.R;
import com.dyadav.chirpntweet.modal.Media;
import com.dyadav.chirpntweet.modal.Tweet;
import com.dyadav.chirpntweet.utils.DateUtility;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TweetAdapter extends
        RecyclerView.Adapter<TweetAdapter.MyViewHolder> {

    private ArrayList<Tweet> tweetList;
    private Context context;

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
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Tweet tweet = tweetList.get(position);

        if (tweet != null) {
            Glide.with(context)
                    .load(tweet.getUser().getProfileImageURL())
                    .into(holder.userProfileImage);

            Media media = tweet.getMedia();
            Media exMedia = tweet.getExtendedMedia();
            Log.d("Position", String.valueOf(position));
            if(media != null) {
                Log.d("Media type", media.getType());
                Log.d("Media url", media.getMediaUrlHttps());
                Log.d("Media url https", media.getMediaUrl());

                if (media.getType().equals("Video"))
                    Log.d("Media video url", media.getVideoUrlHttps());
                Glide.with(context)
                        .load(media.getMediaUrlHttps())
                        .into(holder.tweetImage);
                if(exMedia !=null ) {
                    Log.d("Ex Media type", exMedia.getType());
                    Log.d("Ex Media url", exMedia.getMediaUrlHttps());
                    Log.d("Ex Media url https", exMedia.getMediaUrl());
                    if (exMedia.getType().equals("Video"))
                        Log.d("Ex Media video url", exMedia.getVideoUrlHttps());
                }
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

            if(tweet.getRetweeted())
                holder.retweet.setImageDrawable(context.getResources().getDrawable(R.drawable.green_retweet));
            holder.favCount.setText(String.valueOf(tweet.getFavoriteCount()));
            holder.retweetCount.setText(String.valueOf(tweet.getRetweetCount()));
        }
    }

    @Override
    public int getItemCount() {
        return tweetList.size();
    }
}

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

public class TweetAdapter extends
        RecyclerView.Adapter<TweetAdapter.MyViewHolder> {

    private ArrayList<Tweet> tweetList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView userName, tweetBody, screenName, timeStamp, favCount, retweetCount;
        public ImageView userProfileImage, verifiedSymbol, tweetImage;
        public ImageButton reply, retweet, favorite, message;

        public MyViewHolder(View view) {
            super(view);
            userName = (TextView) view.findViewById(R.id.userName);
            tweetBody = (TextView) view.findViewById(R.id.tweetBody);
            userProfileImage = (ImageView) view.findViewById(R.id.profileImage);
            screenName = (TextView) view.findViewById(R.id.screenName);
            verifiedSymbol = (ImageView) view.findViewById(R.id.verified);
            timeStamp = (TextView) view.findViewById(R.id.timeStamp);
            reply = (ImageButton) view.findViewById(R.id.reply_icon);
            retweet = (ImageButton) view.findViewById(R.id.retweet_icon);
            favorite = (ImageButton) view.findViewById(R.id.favorite_icon);
            message = (ImageButton) view.findViewById(R.id.message_icon);
            favCount = (TextView) view.findViewById(R.id.facvorite_count);
            retweetCount = (TextView) view.findViewById(R.id.retweet_count);
            tweetImage = (ImageView) view.findViewById(R.id.tweetImage);
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
                Glide.with(context)
                        .load(media.getMediaUrlHttps())
                        .into(holder.tweetImage);
                if(exMedia !=null ) {
                    Log.d("Ex Media type", exMedia.getType());
                    Log.d("Ex Media url", exMedia.getMediaUrlHttps());
                    Log.d("Ex Media url https", exMedia.getMediaUrl());
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

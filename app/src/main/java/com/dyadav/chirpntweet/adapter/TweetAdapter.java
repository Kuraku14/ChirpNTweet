package com.dyadav.chirpntweet.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dyadav.chirpntweet.R;
import com.dyadav.chirpntweet.modal.Tweet;
import com.dyadav.chirpntweet.utils.DateUtility;

import java.util.ArrayList;

public class TweetAdapter extends
        RecyclerView.Adapter<TweetAdapter.MyViewHolder> {

    private ArrayList<Tweet> tweetList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView userName, tweetBody, screenName, timeStamp;
        public ImageView userProfileImage, verifiedSymbol;

        public MyViewHolder(View view) {
            super(view);
            userName = (TextView) view.findViewById(R.id.userName);
            tweetBody = (TextView) view.findViewById(R.id.tweetBody);
            userProfileImage = (ImageView) view.findViewById(R.id.profileImage);
            screenName = (TextView) view.findViewById(R.id.screenName);
            verifiedSymbol = (ImageView) view.findViewById(R.id.verified);
            timeStamp = (TextView) view.findViewById(R.id.timeStamp);
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

            holder.userName.setText(tweet.getUser().getName());
            holder.screenName.setText("@" + tweet.getUser().getScreenName());
            holder.tweetBody.setText(tweet.getBody());
            holder.timeStamp.setText(DateUtility.getRelativeTimeAgo(tweet.getCreatedAt()));
            if(tweet.getUser().getVerified())
                holder.verifiedSymbol.setImageDrawable(context.getResources().getDrawable(R.drawable.verified));
        }
    }

    @Override
    public int getItemCount() {
        return tweetList.size();
    }
}

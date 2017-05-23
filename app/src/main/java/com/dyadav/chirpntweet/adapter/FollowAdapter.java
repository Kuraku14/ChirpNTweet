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
import com.dyadav.chirpntweet.application.TwitterApplication;
import com.dyadav.chirpntweet.modal.User;
import com.dyadav.chirpntweet.rest.TwitterClient;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FollowAdapter  extends
        RecyclerView.Adapter<FollowAdapter.MyViewHolder> {

    private ArrayList<User> mUsers;
    private Context context;
    private TwitterClient client;

    public interface followClickListener {
        void onFollowClicked(User user, int position);
    }

    private followClickListener fListener;

    public interface profileClickListener {
        void onProfileClicked(User user);
    }

    private profileClickListener pListener;

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.userName)
        TextView userName;

        @BindView(R.id.userDescription)
        TextView description;

        @BindView(R.id.profileImage)
        ImageView userProfileImage;

        @BindView(R.id.screenName)
        TextView screenName;

        @BindView(R.id.verified)
        ImageView verifiedSymbol;

        @BindView(R.id.followIcon)
        ImageView followIcon;


        MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public FollowAdapter(Context context, ArrayList<User> users) {
        this.mUsers = users;
        this.context = context;
        client = TwitterApplication.getRestClient();
        this.fListener = null;
        this.pListener = null;
    }

    public void setFollowClickListener(followClickListener listener) {
        this.fListener = listener;
    }

    public void setProfileClickListener(profileClickListener listener) {
        this.pListener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_followers, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FollowAdapter.MyViewHolder holder, int position) {
        final User user = mUsers.get(position);

        if (user != null) {
            Glide.with(context)
                    .load(user.getProfileImageURL())
                    .into(holder.userProfileImage);

            holder.userName.setText(user.getName());
            holder.screenName.setText("@" + user.getScreenName());
            if (user.getDescription() != null)
                holder.description.setText(user.getDescription());

            if(user.getVerified()) {
                holder.verifiedSymbol.setVisibility(View.VISIBLE);
            } else {
                holder.verifiedSymbol.setVisibility(View.GONE);
            }
            if (user.isFollowing() || user.isFollow_request_sent()) {
                holder.followIcon.setImageResource(R.drawable.following);
                holder.followIcon.setBackgroundResource(R.drawable.following_border);
            }

            holder.userProfileImage.setOnClickListener(v -> pListener.onProfileClicked(user));

            holder.followIcon.setOnClickListener(v -> fListener.onFollowClicked(user, position));
        }
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }
}

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
import com.dyadav.chirpntweet.modal.DirectMessages;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.dyadav.chirpntweet.utils.DateUtility.detailedViewFormatDate;

public class DirectMessageAdapter  extends
        RecyclerView.Adapter<DirectMessageAdapter.MyViewHolder> {

    private ArrayList<DirectMessages> messageList;
    private Context context;

    public DirectMessageAdapter(Context context, ArrayList<DirectMessages> messageList) {
        this.messageList = messageList;
        this.context = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.profileImage)
        ImageView profileImage;

        @BindView(R.id.senderUserName)
        TextView senderUserName;

        @BindView(R.id.senderScreenName)
        TextView senderScreenName;

        @BindView(R.id.message)
        TextView message;

        @BindView(R.id.timeStamp)
        TextView timeStamp;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public DirectMessageAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DirectMessageAdapter.MyViewHolder holder, int position) {
        final DirectMessages message = messageList.get(position);

        if (message != null) {
            Glide.with(context)
                    .load(message.getSender().getProfileImageURL())
                    .into(holder.profileImage);

            holder.senderUserName.setText(message.getSender().getName());
            holder.senderScreenName.setText("@" + message.getSenderScreenName());
            holder.message.setText(message.getText());
            holder.timeStamp.setText(detailedViewFormatDate(message.getCreatedAt()));
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
}

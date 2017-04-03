package com.dyadav.chirpntweet.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dyadav.chirpntweet.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ProfilePhotosAdapter  extends
        RecyclerView.Adapter<ProfilePhotosAdapter.MyViewHolder>  {

    private List<String> userPhotos;
    private Context context;

    public ProfilePhotosAdapter(List<String> userPhotos, Context context) {
        this.userPhotos = userPhotos;
        this.context = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.userTweetsMedia)
        ImageView userPhoto;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public ProfilePhotosAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_photos, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String url = userPhotos.get(position);

        holder.userPhoto.setImageResource(0);
        Glide.with(context).load(url)
            .bitmapTransform(new RoundedCornersTransformation(context,20,0))
            .diskCacheStrategy( DiskCacheStrategy.SOURCE )
            .into(holder.userPhoto);
    }

    @Override
    public int getItemCount() {
        return userPhotos.size();
    }
}

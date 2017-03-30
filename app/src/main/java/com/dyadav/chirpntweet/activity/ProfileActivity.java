package com.dyadav.chirpntweet.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.dyadav.chirpntweet.R;
import com.dyadav.chirpntweet.databinding.ActivityProfileBinding;
import com.dyadav.chirpntweet.modal.User;

public class ProfileActivity extends AppCompatActivity {

    ActivityProfileBinding binding;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);

        //Get user info
        Intent intent = getIntent();
        user = intent.getParcelableExtra("user");

        //Backdrop
        Glide.with(this)
                .load(user.getCoverImageURL())
                .into(binding.backdrop);

        //Profile image
        //Glide.with(this)
        //        .load(user.getProfileImageURL())
        //        .bitmapTransform(new CropCircleTransformation(TimelineActivity.this))
        //        .into(profile);
    }
}

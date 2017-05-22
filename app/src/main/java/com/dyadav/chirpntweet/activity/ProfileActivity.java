package com.dyadav.chirpntweet.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dyadav.chirpntweet.R;
import com.dyadav.chirpntweet.databinding.ActivityProfileBinding;
import com.dyadav.chirpntweet.fragments.FavoritesFragment;
import com.dyadav.chirpntweet.fragments.PhotosFragment;
import com.dyadav.chirpntweet.fragments.UserTimelineFragment;
import com.dyadav.chirpntweet.modal.User;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ProfileActivity extends AppCompatActivity {

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityProfileBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);

        getWindow().setBackgroundDrawable(null);

        //Get user info
        Intent intent = getIntent();
        user = intent.getParcelableExtra("user");

        setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayShowTitleEnabled(false);

        //Backdrop
        Glide.with(this)
                .load(user.getCoverImageURL())
                .into(binding.backdrop);

        //Profile image
        Glide.with(this)
                .load(user.getProfileImageURL())
                .bitmapTransform(new RoundedCornersTransformation(this,30,0))
                .diskCacheStrategy( DiskCacheStrategy.SOURCE )
                .into(binding.profileImage);

        binding.followerCount.setText(user.getFollowerCount());
        binding.followingCount.setText(user.getFollowingCount());
        binding.userName.setText(user.getName());
        binding.screenName.setText("@" + user.getScreenName());
        if (null != user.getDescription()) {
            binding.description.setVisibility(View.VISIBLE);
            binding.description.setText(user.getDescription());
        }else{
            binding.description.setVisibility(View.GONE);
        }

        if (null != user.getLocation()) {
            binding.locationIcon.setVisibility(View.VISIBLE);
            binding.location.setText(user.getLocation());
        } else {
            binding.locationIcon.setVisibility(View.GONE);
        }
        setupViewPager(binding.viewpager);
        binding.tabs.setupWithViewPager(binding.viewpager);

        binding.followerCount.setOnClickListener(v -> {
            Intent intent1 = new Intent(ProfileActivity.this, FollowActivity.class);
            intent1.putExtra("user", user);
            intent1.putExtra("list", "follower");
            startActivity(intent1);
        });

        binding.followingCount.setOnClickListener(v -> {
            Intent intent12 = new Intent(ProfileActivity.this, FollowActivity.class);
            intent12.putExtra("user", user);
            intent12.putExtra("list", "following");
            startActivity(intent12);
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        ProfilePagerAdapter adapter = new ProfilePagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new UserTimelineFragment(), getString(R.string.tab_tweets));
        adapter.addFragment(new PhotosFragment(), getString(R.string.tab_photos));
        adapter.addFragment(new FavoritesFragment(), getString(R.string.tab_favorites));
        viewPager.setAdapter(adapter);
    }

    private class ProfilePagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ProfilePagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("user", user);
            mFragmentList.get(position).setArguments(bundle);
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}

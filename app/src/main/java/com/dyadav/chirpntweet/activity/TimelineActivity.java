package com.dyadav.chirpntweet.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dyadav.chirpntweet.R;
import com.dyadav.chirpntweet.application.TwitterApplication;
import com.dyadav.chirpntweet.databinding.ActivityMainBinding;
import com.dyadav.chirpntweet.fragments.HomeTimelineFragment;
import com.dyadav.chirpntweet.fragments.MentionsTimelineFragment;
import com.dyadav.chirpntweet.modal.User;
import com.dyadav.chirpntweet.rest.TwitterClient;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class TimelineActivity extends BaseActivity {

    private ActionBarDrawerToggle mDrawerToggle;
    private User user;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        //Fetch User info
        fetchUserInfo();

        setSupportActionBar(binding.toolbar);

        getSupportActionBar().setDisplayUseLogoEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setupViewPager(binding.viewpager);
                binding.tabs.setupWithViewPager(binding.viewpager);
            }
        }, 2000);

        setupNavigationDrawer();
        setUpNavigationView();
    }

    private void setupNavigationDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                binding.drawerLayout,
                binding.toolbar,
                R.string.app_name,
                R.string.app_name
        );

        /* mDrawerToggle.setDrawerIndicatorEnabled(false);
        mDrawerToggle.getDrawerArrowDrawable();
        mDrawerToggle.setHomeAsUpIndicator(R.drawable.messages);
        mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });
        */
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle
        // If it returns true, then it has handled
        // the nav drawer indicator touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...
        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {
        TimelinePagerAdapter adapter = new TimelinePagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HomeTimelineFragment(), "HOME");
        adapter.addFragment(new MentionsTimelineFragment(), "MENTIONS");
        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    private void setUpNavigationView() {
        binding.navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.profile:
                        //Send user info
                        Intent i = new Intent(TimelineActivity.this, ProfileActivity.class);
                        i.putExtra("user", user);
                        startActivity(i);
                        binding.drawerLayout.closeDrawers();
                        return true;

                    case R.id.messages:
                        startActivity(new Intent(TimelineActivity.this, DirectMessages.class));
                        binding.drawerLayout.closeDrawers();
                        return true;

                    case R.id.logOut:
                        TwitterClient client;
                        client = TwitterApplication.getRestClient();
                        client.clearAccessToken();
                        finish();
                        //Show Login Screen
                        return true;

                    case R.id.lists:
                    case R.id.connect:
                    case R.id.helpCenter:
                        return true;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                /*
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);
                */
                return true;
            }
        });
    }

    private void fetchUserInfo() {

        TwitterClient client = TwitterApplication.getRestClient();
        client.getAccountInfo(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Gson gson = new Gson();

                user = gson.fromJson(response.toString(), User.class);

                //Set cover image
                ImageView cover = (ImageView) findViewById(R.id.img_header_bg);
                Glide.with(TimelineActivity.this)
                        .load(user.getCoverImageURL())
                        .into(cover);
                //Set profile image
                ImageView profile = (ImageView) findViewById(R.id.img_profile);
                Glide.with(TimelineActivity.this)
                        .load(user.getProfileImageURL())
                        .bitmapTransform(new CropCircleTransformation(TimelineActivity.this))
                        .into(profile);
                //Name
                TextView name = (TextView) findViewById(R.id.name);
                name.setText(user.getName());
                //ScreenName
                TextView screenName = (TextView) findViewById(R.id.screenName);
                screenName.setText(user.getScreenName());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject object) {
                //Snackbar.make(binding.cLayout, R.string.user_info_error, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private class TimelinePagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        TimelinePagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            //set bundle info
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

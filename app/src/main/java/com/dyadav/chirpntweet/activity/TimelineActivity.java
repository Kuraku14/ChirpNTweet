package com.dyadav.chirpntweet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dyadav.chirpntweet.R;
import com.dyadav.chirpntweet.application.TwitterApplication;
import com.dyadav.chirpntweet.fragments.HomeTimelineFragment;
import com.dyadav.chirpntweet.fragments.MentionsTimelineFragment;
import com.dyadav.chirpntweet.modal.User;
import com.dyadav.chirpntweet.rest.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class TimelineActivity extends Baseactivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar toolbar;
    private User user;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Fetch User info
        fetchUserInfo();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayUseLogoEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        setupNavigationDrawer();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        setUpNavigationView();
    }

    private void setupNavigationDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                toolbar,
                R.string.app_name,
                R.string.app_name
        );

        mDrawerToggle.setDrawerIndicatorEnabled(false);
        mDrawerToggle.getDrawerArrowDrawable();
        mDrawerToggle.setHomeAsUpIndicator(R.drawable.messages);
        mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HomeTimelineFragment(), "HOME");
        adapter.addFragment(new MentionsTimelineFragment(), "MENTIONS");
        viewPager.setAdapter(adapter);
    }

    private void setUpNavigationView() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.profile:
                        startActivity(new Intent(TimelineActivity.this, ProfileActivity.class));
                        mDrawerLayout.closeDrawers();
                        return true;

                    case R.id.messages:
                        startActivity(new Intent(TimelineActivity.this, DirectMessages.class));
                        mDrawerLayout.closeDrawers();
                        return true;

                    case R.id.signout:
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
                user = User.fromJson(response);

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

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
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

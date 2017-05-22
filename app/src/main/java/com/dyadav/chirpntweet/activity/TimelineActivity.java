package com.dyadav.chirpntweet.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class TimelineActivity extends AppCompatActivity {

    private ActionBarDrawerToggle mDrawerToggle;
    private User mUser;
    private ActivityMainBinding mBinding;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent i = new Intent(TimelineActivity.this, SearchActivity.class);
                i.putExtra("query", query);
                i.putExtra("mUser", mUser);
                startActivity(i);

                searchItem.collapseActionView();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        getWindow().setBackgroundDrawable(null);

        Intent intent = getIntent();
        mUser = intent.getParcelableExtra("user");

        setupToolbar();
        setupViewPager(mBinding.viewpager);
        setupNavigationDrawer();
        setUpNavigationView();

        HomeTimelineFragment fragment = (HomeTimelineFragment) mFragmentList.get(0);
        mBinding.fab.setOnClickListener(view -> fragment.createComposeDialog(null));
    }

    private void setupToolbar() {
        setSupportActionBar(mBinding.toolbar);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar !=null)
            actionBar.setTitle("Home");
    }

    private void setupNavigationDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mBinding.drawerLayout,
                mBinding.toolbar,
                R.string.app_name,
                R.string.app_name
        );

        View headerLayout = mBinding.navView.getHeaderView(0);
        //Set cover image
        ImageView cover = (ImageView) headerLayout.findViewById(R.id.img_header_bg);
        Glide.with(TimelineActivity.this)
                .load(mUser.getCoverImageURL())
                .into(cover);
        //Set profile image
        ImageView profile = (ImageView) headerLayout.findViewById(R.id.img_profile);
        Glide.with(TimelineActivity.this)
                .load(mUser.getProfileImageURL())
                .bitmapTransform(new CropCircleTransformation(TimelineActivity.this))
                .into(profile);
        //Name
        TextView name = (TextView) headerLayout.findViewById(R.id.name);
        name.setText(mUser.getName());
        //ScreenName
        TextView screenName = (TextView) headerLayout.findViewById(R.id.screenName);
        screenName.setText(mUser.getScreenName());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {
        TimelinePagerAdapter adapter = new TimelinePagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HomeTimelineFragment(), getString(R.string.tab_home));
        adapter.addFragment(new MentionsTimelineFragment(), getString(R.string.tab_mentions));
        viewPager.setAdapter(adapter);

        mBinding.tabs.setupWithViewPager(viewPager);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    private void setUpNavigationView() {
        mBinding.navView.setNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.profile:
                    Intent i = new Intent(TimelineActivity.this, ProfileActivity.class);
                    i.putExtra("user", mUser);
                    startActivity(i);
                    mBinding.drawerLayout.closeDrawers();
                    return true;

                case R.id.messages:
                    startActivity(new Intent(TimelineActivity.this, DirectMessagesActivity.class));
                    mBinding.drawerLayout.closeDrawers();
                    return true;

                case R.id.logOut:
                    TwitterClient client;
                    client = TwitterApplication.getRestClient();
                    client.clearAccessToken();
                    finish();
                    startActivity(new Intent(TimelineActivity.this, LoginActivity.class));
                    return true;

                case R.id.lists:
                case R.id.connect:
                case R.id.helpCenter:
                    return true;
            }
            return true;
        });
    }

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    private class TimelinePagerAdapter extends FragmentPagerAdapter {

        TimelinePagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("mUser", mUser);
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

package com.dyadav.chirpntweet.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class TimelineActivity extends AppCompatActivity {

    private ActionBarDrawerToggle mDrawerToggle;
    private User user;
    private ActivityMainBinding binding;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Launch search activity
                Intent i = new Intent(TimelineActivity.this, SearchActivity.class);
                i.putExtra("query", query);
                i.putExtra("user", user);
                startActivity(i);

                //Cleanup search view
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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        getWindow().setBackgroundDrawable(null);

        //Fetch User info
        fetchUserInfo();

        setSupportActionBar(binding.toolbar);

        getSupportActionBar().setDisplayUseLogoEnabled(false);
        getSupportActionBar().setTitle("Home");


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                setupViewPager(binding.viewpager);
                binding.tabs.setupWithViewPager(binding.viewpager);

                //Setup fab button click
                HomeTimelineFragment fragment = (HomeTimelineFragment) mFragmentList.get(0);
                binding.fab.setOnClickListener(view -> fragment.createComposeDialog(null));
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
                        startActivity(new Intent(TimelineActivity.this, DirectMessagesActivity.class));
                        binding.drawerLayout.closeDrawers();
                        return true;

                    case R.id.logOut:
                        TwitterClient client;
                        client = TwitterApplication.getRestClient();
                        client.clearAccessToken();
                        finish();
                        //Show Login Screen
                        startActivity(new Intent(TimelineActivity.this, LoginActivity.class));
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
                Toast.makeText(TimelineActivity.this, R.string.user_info_error, Toast.LENGTH_SHORT).show();
            }
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

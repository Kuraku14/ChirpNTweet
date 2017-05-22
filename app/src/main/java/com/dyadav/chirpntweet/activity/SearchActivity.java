package com.dyadav.chirpntweet.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.dyadav.chirpntweet.R;
import com.dyadav.chirpntweet.databinding.ActivitySearchBinding;
import com.dyadav.chirpntweet.fragments.SearchTweetFragment;
import com.dyadav.chirpntweet.modal.User;

public class SearchActivity extends AppCompatActivity {

    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySearchBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_search);
        getWindow().setBackgroundDrawable(null);
        setSupportActionBar(binding.toolbar);

        Intent i = getIntent();
        mUser = i.getParcelableExtra("user");
        String query = i.getStringExtra("query");
        loadFragment(query);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar !=null) {
            getSupportActionBar().setTitle(query);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadFragment(String query){
        SearchTweetFragment fragment = new SearchTweetFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", mUser);
        bundle.putString("query", query);
        fragment.setArguments(bundle);
        ft.replace(R.id.fragment, fragment);
        ft.commit();
    }
}

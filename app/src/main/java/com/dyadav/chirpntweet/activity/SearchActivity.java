package com.dyadav.chirpntweet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.dyadav.chirpntweet.R;
import com.dyadav.chirpntweet.fragments.SearchTweetFragment;
import com.dyadav.chirpntweet.modal.User;

public class SearchActivity extends AppCompatActivity {

    private User mUser;
    private String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //Get query and user
        Intent i = getIntent();
        mUser = i.getParcelableExtra("user");
        query = i.getStringExtra("query");
        String query = getIntent().getStringExtra("query");
        loadFragment(query);
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

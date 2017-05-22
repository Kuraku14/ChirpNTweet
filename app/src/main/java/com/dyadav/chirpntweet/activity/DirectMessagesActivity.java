package com.dyadav.chirpntweet.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.Toast;

import com.dyadav.chirpntweet.R;
import com.dyadav.chirpntweet.adapter.DirectMessageAdapter;
import com.dyadav.chirpntweet.application.TwitterApplication;
import com.dyadav.chirpntweet.databinding.ActivityDirectMessagesBinding;
import com.dyadav.chirpntweet.modal.DirectMessages;
import com.dyadav.chirpntweet.rest.TwitterClient;
import com.dyadav.chirpntweet.utils.EndlessRecyclerViewScrollListener;
import com.dyadav.chirpntweet.utils.NetworkUtility;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static com.raizlabs.android.dbflow.config.FlowManager.getContext;

public class DirectMessagesActivity extends AppCompatActivity {

    private ActivityDirectMessagesBinding binding;
    protected TwitterClient client;
    protected ArrayList<DirectMessages> mMessages;
    protected DirectMessageAdapter mAdapter;
    protected EndlessRecyclerViewScrollListener scrollListener;
    protected LinearLayoutManager mLayoutManager;
    protected Long maxId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_direct_messages);

        getWindow().setBackgroundDrawable(null);
        client = TwitterApplication.getRestClient();

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayUseLogoEnabled(false);
        getSupportActionBar().setTitle("Messages");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mMessages = new ArrayList<>();
        mAdapter = new DirectMessageAdapter(getContext(), mMessages);
        binding.rvMessages.setAdapter(mAdapter);
        binding.rvMessages.setItemAnimator(new DefaultItemAnimator());

        //Recylerview decorater
        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        binding.rvMessages.addItemDecoration(itemDecoration);

        mLayoutManager = new LinearLayoutManager(getContext());
        binding.rvMessages.setLayoutManager(mLayoutManager);

        //Endless pagination
        scrollListener = new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                //Handler handler = new Handler();
                //handler.postDelayed(() -> populateMessages(false, getMaxId()), 1000);
            }
        };
        binding.rvMessages.addOnScrollListener(scrollListener);

        //Swipe to refresh
        binding.swipeContainer.setOnRefreshListener(() -> {
            //Check internet
            if (!NetworkUtility.isOnline()) {
                Toast.makeText(this, R.string.connection_error, Toast.LENGTH_SHORT).show();
                binding.swipeContainer.setRefreshing(false);
                return;
            }
            //Fetch first page
            populateMessages(true, 0);
        });

        //Fetch first page
        populateMessages(true, 0);
    }

    private void populateMessages(boolean request, long id) {
        client.getMessages(id,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    if (request)
                        mMessages.clear();

                    ArrayList<DirectMessages> msg = new ArrayList<>();
                    Gson gson = new Gson();
                    for(int i = 0; i < response.length(); i++) {
                        try {
                            msg.add(gson.fromJson(response.getJSONObject(i).toString(),DirectMessages.class));
                        } catch (JSONException e) {
                        }
                    }

                    mMessages.addAll(msg);
                    mAdapter.notifyDataSetChanged();
                    binding.swipeContainer.setRefreshing(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                binding.swipeContainer.setRefreshing(false);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private long getMaxId() {
        return mMessages.get(mMessages.size() - 1).getId();
    }
}

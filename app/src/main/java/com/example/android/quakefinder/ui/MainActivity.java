package com.example.android.quakefinder.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.quakefinder.R;
import com.example.android.quakefinder.data.Earthquake;
import com.example.android.quakefinder.sync.QuakeSyncTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements
        QuakeAdapter.QuakeAdapterOnClickHandler,
        SwipeRefreshLayout.OnRefreshListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.rv_earthquakes)
    RecyclerView recyclerView;

    @BindView(R.id.pb_earthquakes)
    ProgressBar progressBar;

    @BindView(R.id.tv_error_message)
    TextView errorMessageTV;

    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    private QuakeAdapter quakeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setUpActionBar();
        setUpRV();
        setUpRefreshListener();

        loadQuakeData();
    }

    private void setUpActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setUpRV() {
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        quakeAdapter = new QuakeAdapter(this);
        recyclerView.setAdapter(quakeAdapter);
    }

    private void setUpRefreshListener() {
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setRefreshing(true);
        onRefresh();
    }

    @Override
    public void onRefresh() {
        Log.d(LOG_TAG, "onRefresh called");
        loadQuakeData();
        swipeRefreshLayout.setRefreshing(false);
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            Log.d(LOG_TAG, "refresh");
            quakeAdapter.setData(null);
            loadQuakeData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */

    @Override
    public void onClick(Earthquake earthquake) {
        final String url = earthquake.getUrl();
        openWebPage(url);
    }

    private void openWebPage(String url) {
        final Uri uri = Uri.parse(url);
        final Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void loadQuakeData() {
        final VisibilityToggle visibilityToggle = new VisibilityToggle(recyclerView, errorMessageTV, progressBar);
        visibilityToggle.showData();
        quakeAdapter.setData(null);
        new QuakeSyncTask(quakeAdapter, visibilityToggle).execute();
    }

}

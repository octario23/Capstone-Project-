package mx.com.broadcastv.ui;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import mx.com.broadcastv.R;
import mx.com.broadcastv.adapter.SearchAdapter;
import mx.com.broadcastv.data.ServicesContract;
import mx.com.broadcastv.ui.views.AdjustableRecyclerView;
import mx.com.broadcastv.ui.views.MarginDecoration;
import mx.com.broadcastv.util.BroadcastvSQLUtil;

public class SearchActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        SearchView.OnQueryTextListener,
        View.OnClickListener {

    private static final String QUERY = "query";
    public static final String SEARCH_CHANNEL_URI = "search_channel_uri";
    private static final int SEARCH_LOADER = 0;
    private Toolbar tool_bar;
    SearchView searchView;
    private ProgressBar progressBar;
    private AdjustableRecyclerView recyclerView;
    TextView hintText;
    LinearLayout mNoResultFoundText;
    private String mQuery;
    private SearchAdapter searchAdapter;

    public interface SearchSelectionListener {
        void searchSelection(Intent data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_search, menu);


        final MenuItem searchItem = menu.findItem(R.id.search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(getResources().getString(R.string.search_title));
        searchView.setFocusable(true);
        searchView.setIconified(false);
        searchView.requestFocusFromTouch();
        searchView.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);
        if (mQuery != null) {
            searchView.setQuery(mQuery, true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        tool_bar = (Toolbar) findViewById(R.id.toolbar);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        mNoResultFoundText = (LinearLayout) findViewById(R.id.no_results);
        hintText = (TextView) findViewById(R.id.search_encouragement_text);
        setSupportActionBar(tool_bar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        FragmentManager fm = getSupportFragmentManager();

        if (savedInstanceState != null) {
            mQuery = savedInstanceState.getString(QUERY);
        }

        recyclerView = (AdjustableRecyclerView) findViewById(R.id.recycler);
        recyclerView.addItemDecoration(new MarginDecoration(this));
        recyclerView.setHasFixedSize(true);
        searchAdapter = new SearchAdapter(this, listener);
        //actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onQueryTextChange(String query) {

        if (query.length() < 3)
            return false;
        else {
            search(query);
            return true;
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        search(query);
        searchView.clearFocus();
        return true;
    }

    private void search(String query) {
        Log.i(SearchActivity.class.getSimpleName(), "Query: " + query);
        progressBar.setVisibility(View.VISIBLE);
        mQuery = query;
        getSupportLoaderManager().restartLoader(SEARCH_LOADER, null, this);
    }


    @Override
    public void onClick(View v) {
        if (v instanceof TextView) {
            Intent data = new Intent();
            setResult(RESULT_OK, data);
            ((SearchActivity) v.getContext()).finish();
        }
    }

    SearchSelectionListener listener = new SearchSelectionListener() {
        @Override
        public void searchSelection(Intent data) {
            setResult(RESULT_OK, data);
            finish();
        }
    };


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(QUERY, mQuery);
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri channelUri = ServicesContract.ChannelEntry.buildChannelNameUriQuery("9999", mQuery);

        String mSelectionClause = null;

        return new CursorLoader(this,
                channelUri,
                BroadcastvSQLUtil.CHANNELS_COLUMNS,
                mSelectionClause,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        progressBar.setVisibility(View.GONE);
        if (data != null && data.moveToFirst()) {
            hintText.setVisibility(View.GONE);
            mNoResultFoundText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            searchAdapter.swapCursor(data);
            recyclerView.setAdapter(searchAdapter);
        } else {
            searchAdapter.swapCursor(null);
            recyclerView.setVisibility(View.GONE);
            hintText.setVisibility(View.VISIBLE);
            mNoResultFoundText.setVisibility(TextView.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}


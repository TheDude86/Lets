package com.main.lets.lets.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.main.lets.lets.Adapters.NewSearchAdapter;
import com.main.lets.lets.LetsAPI.Event;
import com.main.lets.lets.LetsAPI.L;
import com.main.lets.lets.LetsAPI.Search;
import com.main.lets.lets.R;

public class NewSearchActivity extends AppCompatActivity {

    Search mSearch;
    RecyclerView mRecyclerView;
    Search.onUpdateListener mListener;
    NewSearchAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mListener = new Search.onSearchListener() {

            @Override
            public void onHashtagInit() {

                mAdapter = new NewSearchAdapter(NewSearchActivity.this, mSearch);
                mAdapter.setMode(NewSearchAdapter.Mode.HASHTAG);

                mRecyclerView.setAlpha(1);
                mRecyclerView.setLayoutManager(new GridLayoutManager(NewSearchActivity.this, 1));
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onHashtagSearch() {

                mAdapter.addEvent();
            }

            @Override
            public void onUpdate() {

                mAdapter = new NewSearchAdapter(NewSearchActivity.this, mSearch);
                mAdapter.setMode(NewSearchAdapter.Mode.STRING);

                mRecyclerView.setAlpha(1);
                mRecyclerView.setLayoutManager(new GridLayoutManager(NewSearchActivity.this, 1));
                mRecyclerView.setAdapter(mAdapter);
            }

        };

        mSearch = new Search(mListener);

        mRecyclerView = (RecyclerView) findViewById(R.id.view);
        SearchView s = (SearchView) findViewById(R.id.search_bar);
        assert s != null;
        s.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 0) {

                    if (newText.startsWith("#")) {
                        mSearch.makeHashtagSearch(newText.split(" "));
                        mAdapter.notifyDataSetChanged();

                    } else {
                        mSearch = new Search(mListener);

                        mSearch.makeSearch(newText);

                    }

                }

                return false;
            }
        });

        s.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                mRecyclerView.setAlpha(0);

                return false;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_settings) {


        }


        return super.onOptionsItemSelected(item);
    }
}

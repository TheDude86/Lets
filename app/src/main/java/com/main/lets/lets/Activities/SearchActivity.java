package com.main.lets.lets.Activities;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.main.lets.lets.LetsAPI.Calls;
import com.main.lets.lets.R;
import com.main.lets.lets.Visualizers.SearchFeed;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {
    JSONObject mSearchResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mSearchResults = new JSONObject();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        //Creating the search feed, currently the inputted user id is -1, that needs to change if
        //the feed ends up needing the user's ID.
        final SearchFeed search = new SearchFeed(this, preferences.getString("Token", ""),
                                                 preferences.getInt("UserID", -1));

        //The Spinner chooses which entity to display on the search feed
        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        //The Search bar is for users to input strings and search for users, events, and groups
        SearchView searchView = (SearchView) findViewById(R.id.rotate_left);
        assert searchView != null;

        searchView.setIconifiedByDefault(false);
        searchView.requestFocus();

        //Searches the database for the inputted string and displays the active feed and the results
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Calls.search(newText, 50, 0, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        mSearchResults = response;
                        search.draw(response);

                    }
                });

                return false;
            }
        });

        //What to do when an item is chosen in the spinner
        assert spinner != null;
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Once the user has selected an item from the spinner, the position will be returned here
                //0 = Events
                //1 = People
                //2 = Groups

                switch (position) {
                    case 0:
                        search.updateFeed(mSearchResults, SearchFeed.Viewing.EVENT);

                        break;
                    case 1:
                        search.updateFeed(mSearchResults, SearchFeed.Viewing.USER);

                        break;

                    case 2:
                        search.updateFeed(mSearchResults, SearchFeed.Viewing.GROUP);

                        break;

                    default:

                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }



}

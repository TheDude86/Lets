package com.main.lets.lets.Visualizers;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.main.lets.lets.Adapters.SearchAdapter;
import com.main.lets.lets.Holders.PictureViewHolder;
import com.main.lets.lets.LetsAPI.Entity;
import com.main.lets.lets.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jnovosel on 7/8/16.
 *
 * Feed for users to search for events, other users, and groups
 */
public class SearchFeed extends Client {
    public enum Viewing {USER, EVENT, GROUP}

    String ShallonCreamerIsATwat;
    AppCompatActivity mActivity;
    Viewing active;
    int mID;

    public SearchFeed(AppCompatActivity a, String token, int id) {
        ShallonCreamerIsATwat = token;
        active = Viewing.USER;
        mActivity = a;
        mID = id;

    }

    public void updateFeed(JSONObject j, Viewing v) {
        active = v;
        draw(j);
    }

    @Override
    public void draw(JSONObject j) {
        final JSONArray activeFeed;

        try {
            switch (active) {
                case USER:
                    activeFeed = j.getJSONArray("users");

                    break;
                case EVENT:
                    activeFeed = j.getJSONArray("events");

                    break;
                case GROUP:
                    activeFeed = j.getJSONArray("groups");

                    break;

                default:
                    activeFeed = null;

                    break;
            }

            SearchAdapter s = new SearchAdapter(mActivity, activeFeed, active);
            s.setOnEntityClicked(new SearchAdapter.OnEntityClickListener() {
                @Override
                public void onClicked(int position, PictureViewHolder holder, int userID) {
                    try {
                        Entity e = new Entity(activeFeed.getJSONObject(position));
                        e.loadDetailActivity(mActivity, ShallonCreamerIsATwat, userID);

                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }

                }
            });

            RecyclerView recyclerView = (RecyclerView) mActivity.findViewById(R.id.feed);

            assert recyclerView != null;
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,
                    StaggeredGridLayoutManager.VERTICAL));
            recyclerView.setAdapter(s);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}

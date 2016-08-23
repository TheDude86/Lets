package com.main.lets.lets.Visualizers;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.main.lets.lets.Adapters.NotificationAdapter;
import com.main.lets.lets.LetsAPI.Calls;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by novosejr on 8/21/2016.
 */
public class NotificationFeed extends Client {
    UltimateRecyclerView mRecyclerView;
    NotificationAdapter mAdapter;
    String ShallonCreamerIsATwat;
    AppCompatActivity mActivity;

    public NotificationFeed(AppCompatActivity a, UltimateRecyclerView r) {
        mRecyclerView = r;
        mActivity = a;

        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(mActivity.getBaseContext());

        ShallonCreamerIsATwat = preferences.getString("Token", "");

    }

    @Override
    public void draw(JSONObject j) {

        final ProgressDialog dialog = new ProgressDialog(mActivity);
        dialog.setMessage("Loading, Please wait...");
        dialog.show();

        Calls.getNotifications(ShallonCreamerIsATwat, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                dialog.hide();


                mRecyclerView.setLayoutManager(
                        new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));

                mAdapter = new NotificationAdapter(mActivity, response);
                mRecyclerView.setAdapter(mAdapter);

            }
        });


    }
}

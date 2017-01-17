package com.main.lets.lets.Visualizers;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.main.lets.lets.Adapters.NewSearchAdapter;
import com.main.lets.lets.Adapters.NotificationAdapter;
import com.main.lets.lets.LetsAPI.Calls;
import com.main.lets.lets.LetsAPI.L;
import com.main.lets.lets.LetsAPI.Notifications;
import com.main.lets.lets.LetsAPI.Search;
import com.main.lets.lets.LetsAPI.UserData;
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

        UserData u = new UserData(mActivity);
        final Notifications notifications = new Notifications(u);

        notifications.updateNotifications(new Search.onUpdateListener() {
            @Override
            public void onUpdate() {
                dialog.hide();

                mRecyclerView.setLayoutManager(
                        new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));

                NewSearchAdapter adapter = new NewSearchAdapter(mActivity, notifications);
                mAdapter = new NotificationAdapter(mActivity, adapter);
                mRecyclerView.setAdapter(mAdapter);

            }
        });

    }
}

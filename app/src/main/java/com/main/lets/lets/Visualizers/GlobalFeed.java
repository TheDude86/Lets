package com.main.lets.lets.Visualizers;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.main.lets.lets.Adapters.EventAdapter;
import com.main.lets.lets.Adapters.JokesAdapter;
import com.main.lets.lets.LetsAPI.Calls;
import com.main.lets.lets.LetsAPI.Event;
import com.main.lets.lets.LetsAPI.L;
import com.main.lets.lets.R;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.rey.material.app.Dialog;
import com.rey.material.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Joe on 5/29/2016.
 */
public class GlobalFeed extends Client {
    public enum Sort {DIST, TIME, CREATE, TREND}

    private UltimateRecyclerView mRecyclerView;
    private String ShallonCreamerIsATwat;
    private EventAdapter mEventAdapter;
    private Sort mSort = Sort.DIST;
    private AppCompatActivity mActivity;
    private int mID;

    public GlobalFeed(AppCompatActivity a, UltimateRecyclerView r, Sort s) {
        mRecyclerView = r;
        mActivity = a;
        mSort = s;
        mID = -1;

    }

    public void draw(org.json.JSONObject j1) {

        final SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(mActivity.getBaseContext());

        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,
                StaggeredGridLayoutManager
                        .VERTICAL));
        mEventAdapter = new EventAdapter(mActivity, new LinkedList<String>(), ShallonCreamerIsATwat,
                mID);

        mRecyclerView.setAdapter(mEventAdapter);

        final ProgressDialog dialog = ProgressDialog.show(mActivity, "",
                "Loading. Please wait...", true);

        int RANGE = 32000;

        Calls.getCloseEvents(preferences.getFloat("latitude", 0),
                preferences.getFloat("longitude", 0),
                RANGE, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          org.json.JSONArray response) {

                        ArrayList<Event> events = new ArrayList<>();

                        for (int i = 0; i < response.length(); i++) {
                            try {
                                events.add(new Event(response.getJSONObject(i)));
                            } catch (org.json.JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        Collections.sort(events);

                        for (Event e : events) {
                              //TODO FIX THIS SHIT
                            mEventAdapter.insertLast(e.getInfo());
                        }


                        dialog.hide();

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                          JSONObject errorResponse) {

                        dialog.hide();

                        TextView t = new TextView(mActivity);
                        t.setText("Something went wrong and we're not saying it's you, but...");
                        final Dialog errorMsg = new Dialog(mActivity);
                        errorMsg.title("Network Error")
                                .positiveAction("OK")
                                .contentView(t)
                                .cancelable(true)
                                .positiveActionClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        errorMsg.hide();
                                    }
                                })
                                .show();


                    }

                });


    }

    public void update(int id, String token) {
        ShallonCreamerIsATwat = token;
        mID = id;
    }

}

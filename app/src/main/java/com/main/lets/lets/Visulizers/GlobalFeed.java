package com.main.lets.lets.Visulizers;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.main.lets.lets.Adapters.EventAdapter;
import com.main.lets.lets.LetsAPI.Calls;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.rey.material.app.Dialog;
import com.rey.material.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Joe on 5/29/2016.
 */
public class GlobalFeed extends Client {
    public enum Sort {DIST, TIME, CREATE, TREND}

    private Activity mActivity;
    private UltimateRecyclerView mRecyclerView;
    private Sort mSort = Sort.DIST;
    private EventAdapter mEventAdapter;

    public GlobalFeed(Activity a, UltimateRecyclerView r, Sort s) {
        mRecyclerView = r;
        mActivity = a;
        mSort = s;

    }

    public void draw(org.json.JSONObject j){
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        mEventAdapter = new EventAdapter(mActivity, new LinkedList<String>());
        mRecyclerView.setAdapter(mEventAdapter);

        try {

            final ProgressDialog dialog = ProgressDialog.show(mActivity, "",
                    "Loading. Please wait...", true);

            int RANGE = 980000;

            Calls.getCloseEvents(j.getInt("latitude"), j.getInt("longitude"), RANGE, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, org.json.JSONArray response) {
                    Log.println(Log.ASSERT, "Test", response.toString());

                    for (int i = 0; i < response.length(); i++) {
                        try {
                            mEventAdapter.insertLast(response.getJSONObject(i).toString());
                        } catch (org.json.JSONException e) {
                            e.printStackTrace();
                        }
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

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}

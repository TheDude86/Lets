package com.main.lets.lets.LetsAPI;

import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by novosejr on 1/23/2017.
 */

public class Feed {
    public ArrayList<Event> mEvents = new ArrayList<>();
    public LatLng mCoords;

    public Feed(LatLng pos) {
        mCoords = pos;

    }

    public void loadFeed(final OnUpdateListener l) {

        Calls.getCloseEvents(mCoords.latitude, mCoords.longitude, 10000000, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                for (int i = 0; i < response.length(); i++) {

                    try {
                        mEvents.add(new Event(response.getJSONObject(i)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                l.onUpdate();

            }
        });

    }



    public interface OnUpdateListener {
        void onUpdate();

    }

}

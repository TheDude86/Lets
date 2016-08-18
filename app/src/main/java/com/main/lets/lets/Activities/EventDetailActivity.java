package com.main.lets.lets.Activities;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.main.lets.lets.LetsAPI.Calls;
import com.main.lets.lets.LetsAPI.Event;
import com.main.lets.lets.R;
import com.main.lets.lets.Visualizers.EventDetailFeed;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class EventDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        try {
            if (getIntent().getStringExtra("JSON") != null){

                JSONObject j = new JSONObject(getIntent().getStringExtra("JSON"));

                EventDetailFeed f = new EventDetailFeed(this, (RecyclerView)
                        findViewById(R.id.event_detail_list), getIntent().getStringExtra("token"),
                                                        getIntent().getIntExtra("id",-1));
                Event e = new Event(j);

                final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
                ImageView background = (ImageView) findViewById(R.id.event_detail_background);

                Picasso.with(this).load(e.getImageResourceId(this)).into(background);
                Bitmap photo = BitmapFactory.decodeResource(getResources(), e.getImageResourceId(this));

                Palette.generateAsync(photo, new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        int bgColor = palette.getMutedColor(getResources().getColor(android.R.color.black));
                        collapsingToolbarLayout.setContentScrimColor(bgColor);
                    }
                });

                collapsingToolbarLayout.setTitle(e.getmTitle());

                f.draw(j);

            } else {

                final ProgressDialog dialog = ProgressDialog.show(this, "",
                                                                  "Loading. Please wait...", true);

                int eventID = getIntent().getIntExtra("EventID", -1);

                Calls.getEvent(eventID, new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            JSONObject j = response.getJSONArray("Event_info").getJSONObject(0);

                            SharedPreferences preferences = PreferenceManager
                                    .getDefaultSharedPreferences(getBaseContext());

                            Log.println(Log.ASSERT, "EventDetailActivity", preferences.getInt("UserID", -2) + "");


                            EventDetailFeed f = new EventDetailFeed(EventDetailActivity.this, (RecyclerView)
                                    findViewById(R.id.event_detail_list), getIntent().getStringExtra("token"), preferences.getInt("UserID", -2));
                            Event e = new Event(j);

                            final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
                            ImageView background = (ImageView) findViewById(R.id.event_detail_background);

                            Picasso.with(EventDetailActivity.this).load(e.getImageResourceId(EventDetailActivity.this)).into(background);
                            Bitmap photo = BitmapFactory.decodeResource(getResources(), e.getImageResourceId(EventDetailActivity.this));

                            Palette.generateAsync(photo, new Palette.PaletteAsyncListener() {
                                @Override
                                public void onGenerated(Palette palette) {
                                    int bgColor = palette.getMutedColor(getResources().getColor(android.R.color.black));
                                    collapsingToolbarLayout.setContentScrimColor(bgColor);
                                }
                            });

                            collapsingToolbarLayout.setTitle(e.getmTitle());

                            f.draw(j);
                            dialog.hide();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}

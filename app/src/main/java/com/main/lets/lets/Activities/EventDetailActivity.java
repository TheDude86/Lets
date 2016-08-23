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
    EventDetailFeed mFeed;
    JSONObject mJSON;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        try {
            if (getIntent().getStringExtra("JSON") != null){

                mJSON = new JSONObject(getIntent().getStringExtra("JSON"));

                mFeed = new EventDetailFeed(this, (RecyclerView)
                        findViewById(R.id.event_detail_list));
                Event e = new Event(mJSON);

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

                mFeed.draw(mJSON);

            } else {

                final ProgressDialog dialog = ProgressDialog.show(this, "",
                                                                  "Loading. Please wait...", true);

                int eventID = getIntent().getIntExtra("EventID", -1);

                Calls.getEvent(eventID, new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            mJSON = response.getJSONArray("Event_info").getJSONObject(0);

                            mFeed = new EventDetailFeed(EventDetailActivity.this, (RecyclerView)
                                    findViewById(R.id.event_detail_list));
                            Event e = new Event(mJSON);

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

                            mFeed.draw(mJSON);
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

    @Override
    protected void onRestart() {
        super.onRestart();

        mFeed.draw(mJSON);

    }
}

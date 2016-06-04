package com.main.lets.lets.Activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.widget.ImageView;

import com.main.lets.lets.LetsAPI.Event;
import com.main.lets.lets.R;
import com.main.lets.lets.Visulizers.EventDetailFeed;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class EventDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        try {
            JSONObject j = new JSONObject(getIntent().getStringExtra("JSON"));

            EventDetailFeed f = new EventDetailFeed(this, (UltimateRecyclerView)findViewById(R.id.event_detail_list), j);
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

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}

package com.main.lets.lets.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.main.lets.lets.Adapters.EventDetailAdapter;
import com.main.lets.lets.LetsAPI.Event;
import com.main.lets.lets.R;
import com.squareup.picasso.Picasso;

public class EventDetailActivity extends AppCompatActivity {
    public final static int SELECT_PICTURE = 0;
    public final static int UPLOAD_PICTURE = 1;
    EventDetailAdapter eventAdapter;
    Event mEvent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final ProgressDialog dialog = ProgressDialog.show(this, "",
                "Loading. Please wait...", true);

        int eventID = getIntent().getIntExtra("EventID", -1);

        final CollapsingToolbarLayout collapsingToolbarLayout =
                (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);

        final int[] bgColors = getResources().getIntArray(R.array.category_colors);

        mEvent = new Event(eventID);
        mEvent.getEventByID(new Event.onFullEventLoaded() {
            @Override
            public void EventLoaded(Event e) {

                ImageView background = (ImageView) findViewById(R.id.event_detail_background);

                Picasso.with(EventDetailActivity.this)
                        .load(e.getImageResourceId(EventDetailActivity.this)).into(background);

                assert collapsingToolbarLayout != null;
                collapsingToolbarLayout.setContentScrimColor(bgColors[e.getCategory()]);
                collapsingToolbarLayout.setTitle(e.getTitle());

                RecyclerView recyclerView = (RecyclerView)
                        findViewById(R.id.event_detail_list);

                assert recyclerView != null;
                recyclerView.setLayoutManager(
                        new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
                eventAdapter = new EventDetailAdapter(EventDetailActivity.this, e);
                recyclerView.setAdapter(eventAdapter);


                dialog.hide();

            }

            @Override
            public void HashtagLoaded(String s) {
                eventAdapter.addHashtag(s);

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == SELECT_PICTURE) {
            if (resultCode == RESULT_OK) {

                Uri imageUri = data.getData();

                Intent i = new Intent(this, ImagePreviewActivity.class);
                i.putExtra("path", getRealPathFromURI(imageUri));
                i.putExtra("type", "event");
                i.putExtra("ID", mEvent.getID());
                startActivityForResult(i, UPLOAD_PICTURE);

            }
        } else if (requestCode == UPLOAD_PICTURE) {
            if (resultCode == RESULT_OK) {

//                loadActivity();

            }
        }

    }


    public String getRealPathFromURI(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


}

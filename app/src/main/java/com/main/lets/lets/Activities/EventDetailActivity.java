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
import android.telecom.Call;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.main.lets.lets.Adapters.EventDetailAdapter;
import com.main.lets.lets.LetsAPI.Calls;
import com.main.lets.lets.LetsAPI.Event;
import com.main.lets.lets.LetsAPI.UserData;
import com.main.lets.lets.R;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class EventDetailActivity extends AppCompatActivity {
    public final static int SELECT_PICTURE = 0;
    public final static int UPLOAD_PICTURE = 1;
    Event mEvent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        loadEvent();

        final EditText comment = (EditText)findViewById(R.id.txt_comment);
        findViewById(R.id.btn_comment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!comment.getText().toString().equals("")) {

                    Calls.addComment(mEvent.mID, new UserData(EventDetailActivity.this), comment.getText().toString(), new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers,
                                              JSONObject response) {
                            comment.clearFocus();
                            comment.setText("");
                            loadEvent();
                        }
                    });
                }


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
                i.putExtra("ID", mEvent.getmEventID());
                startActivityForResult(i, UPLOAD_PICTURE);

            }
        } else if (requestCode == UPLOAD_PICTURE) {
            if (resultCode == RESULT_OK) {

//                loadActivity();

            }
        }

    }

    public void loadEvent() {
        final ProgressDialog dialog = ProgressDialog.show(this, "",
                                                          "Loading. Please wait...", true);

        int eventID = getIntent().getIntExtra("EventID", -1);

        final CollapsingToolbarLayout collapsingToolbarLayout =
                (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);

        final int[] bgColors = getResources().getIntArray(R.array.category_colors);

        mEvent = new Event(eventID);
        mEvent.getEventByID(new Event.onEventLoaded() {
            @Override
            public void EventLoaded(Event e) {

                ImageView background = (ImageView) findViewById(R.id.event_detail_background);

                Picasso.with(EventDetailActivity.this)
                        .load(e.getImageResourceId(EventDetailActivity.this)).into(background);

                assert collapsingToolbarLayout != null;
                collapsingToolbarLayout.setContentScrimColor(bgColors[e.getCategory()]);
                collapsingToolbarLayout.setTitle(e.getmTitle());

                RecyclerView recyclerView = (RecyclerView)
                        findViewById(R.id.event_detail_list);

                assert recyclerView != null;
                recyclerView.setLayoutManager(
                        new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
                EventDetailAdapter eventAdapter = new EventDetailAdapter(EventDetailActivity.this, e);
                recyclerView.setAdapter(eventAdapter);


                dialog.hide();

            }
        });

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

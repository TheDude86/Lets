package com.main.lets.lets.Activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.telecom.Call;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

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
    Event mEvent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

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

    //    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_event_detail);
//
//        try {
//            if (getIntent().getStringExtra("JSON") != null){
//
//                mJSON = new JSONObject(getIntent().getStringExtra("JSON"));
//
//                mFeed = new EventDetailFeed(this, (RecyclerView)
//                        findViewById(R.id.event_detail_list));
//                Event e = new Event(mJSON);
//
//                final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
//                ImageView background = (ImageView) findViewById(R.id.event_detail_background);
//
//                Picasso.with(this).load(e.getImageResourceId(this)).into(background);
//
//                final int[] bgColors = getResources().getIntArray(R.array.category_colors);
//
//                assert collapsingToolbarLayout != null;
//                collapsingToolbarLayout.setContentScrimColor(bgColors[e.getCategory()]);
//
//                collapsingToolbarLayout.setTitle(e.getmTitle());
//
//                mFeed.draw(mJSON);
//
//
//            } else {
//
//                final ProgressDialog dialog = ProgressDialog.show(this, "",
//                                                                  "Loading. Please wait...", true);
//
//                int eventID = getIntent().getIntExtra("EventID", -1);
//
//                Calls.getEvent(eventID, new JsonHttpResponseHandler() {
//
//                    @Override
//                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                        try {
//                            mJSON = response.getJSONArray("Event_info").getJSONObject(0);
//
//                            mFeed = new EventDetailFeed(EventDetailActivity.this, (RecyclerView)
//                                    findViewById(R.id.event_detail_list));
//                            Event e = new Event(mJSON);
//
//                            final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
//                            ImageView background = (ImageView) findViewById(R.id.event_detail_background);
//
//                            Picasso.with(EventDetailActivity.this).load(e.getImageResourceId(EventDetailActivity.this)).into(background);
//
//                            final int[] bgColors = getResources().getIntArray(R.array.category_colors);
//
//                            assert collapsingToolbarLayout != null;
//                            collapsingToolbarLayout.setContentScrimColor(bgColors[e.getCategory()]);
//
//                            collapsingToolbarLayout.setTitle(e.getmTitle());
//
//                            mFeed.draw(mJSON);
//                            dialog.hide();
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                });
//            }
//
//
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//
//    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }
}

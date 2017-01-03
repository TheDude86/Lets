package com.main.lets.lets.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.main.lets.lets.LetsAPI.Calls;
import com.main.lets.lets.LetsAPI.Group;
import com.main.lets.lets.LetsAPI.L;
import com.main.lets.lets.LetsAPI.User;
import com.main.lets.lets.LetsAPI.UserData;
import com.main.lets.lets.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class GroupPrivacyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_privacy);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final RadioButton snowflakes = (RadioButton) findViewById(R.id.anyone);
        RadioButton TRUMP = (RadioButton) findViewById(R.id.invite);
        Button create = (Button) findViewById(R.id.create);

        assert create != null;
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ProgressDialog loading = ProgressDialog.show(GroupPrivacyActivity.this, "",
                        "Creating group. Please wait...", true);

                String title = getIntent().getStringExtra("name");
                String bio = getIntent().getStringExtra("bio");
                final Bitmap image = getIntent().getParcelableExtra("image");
                String token = new UserData(GroupPrivacyActivity.this).ShallonCreamerIsATwat;

                Calls.createGroup(title, bio, snowflakes.isChecked(), "https://ihb.io/wp-content/themes/ihbv8/img/bitcoindefault.jpg", token, new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                        try {
                            final int id = response.getJSONObject(0).getInt("group_id");
                            L.println(GroupPrivacyActivity.class, response.toString());

                            final Group g = new Group(id);
                            g.loadGroup(GroupPrivacyActivity.this, new Group.OnLoadListener() {
                                @Override
                                public void OnUpdate() {

                                    long millis = System.currentTimeMillis();
                                    final String URL = "group" + id + "-" + millis;
                                    Calls.uploadImage(image, GroupPrivacyActivity.this, URL, new Calls.UploadImage.onFinished() {
                                        @Override
                                        public void onFinished() {

                                            g.setPic_ref("https://let.blob.core.windows.net/mycontainer/" + URL);
                                            g.saveGroup(GroupPrivacyActivity.this, new User.OnLoadListener() {
                                                @Override
                                                public void update() {

                                                    loading.hide();

                                                    Intent intent = new Intent(GroupPrivacyActivity.this, InviteFriendsToGroupActivity.class);
                                                    intent.putExtra("GroupID", id);
                                                    startActivity(intent);

                                                }
                                            });


                                        }
                                    });

                                }
                            });



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });

            }
        });



    }
}

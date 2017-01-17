package com.main.lets.lets.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.UserInfo;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.main.lets.lets.LetsAPI.Calls;
import com.main.lets.lets.LetsAPI.L;
import com.main.lets.lets.LetsAPI.User;
import com.main.lets.lets.LetsAPI.UserData;
import com.main.lets.lets.R;
import com.main.lets.lets.Visualizers.UserDetailFeed;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class UserDetailActivity extends AppCompatActivity {
    public final static int SELECT_PICTURE = 0;
    public final static int UPLOAD_PICTURE = 1;

    public UserDetailFeed mFeed;
    public boolean fromCreate;
    public int mUserID;
    User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());

        if (preferences.getString("Token", "").equals("")) {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();

            alertDialog.setTitle("Sorry...");

            alertDialog.setMessage("You must sign into an account to view user's profiles");
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Okay", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int id) {
                    finish();

                }
            });


            alertDialog.show();
        }



        fromCreate = getIntent().getBooleanExtra("create", false);

        loadActivity();

    }

    @Override
    protected void onRestart() {
        super.onRestart();

        fromCreate = false;
        loadActivity();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                finish();
                break;
        }
        return true;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == SELECT_PICTURE) {
            if (resultCode == RESULT_OK) {

                Uri imageUri = data.getData();

                Intent i = new Intent(this, ImagePreviewActivity.class);
                i.putExtra("path", getRealPathFromURI(imageUri));
                i.putExtra("type", "user");
                i.putExtra("ID", mUser.mID);
                startActivityForResult(i, UPLOAD_PICTURE);

            }
        } else if (requestCode == UPLOAD_PICTURE) {
            if (resultCode == RESULT_OK) {

                loadActivity();

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


    public void loadActivity() {

        mUserID = getIntent().getIntExtra("UserID", -1);
        mUser = new User(mUserID);

        final ProgressDialog loading = ProgressDialog.show(this, "",
                "Loading. Please wait...", true);

        mUser.loadFull(this, new User.OnLoadListener() {
            @Override
            public void update() {

                mFeed = new UserDetailFeed(UserDetailActivity.this, (RecyclerView) findViewById(R.id.feed), mUser,
                        (new UserData(UserDetailActivity.this)).ShallonCreamerIsATwat);

                mFeed.draw(new JSONObject());

                loading.hide();

                if (fromCreate) {

                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(UserDetailActivity.this);
                    builder.setTitle("Welcome to your profile");
                    builder.setMessage("This is your profile, from here you can edit your account, " +
                            "update your profile picture, and change your interests.  Enjoy!");
                    builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mFeed.notifyFromCreate();

                        }
                    });

                    builder.create().show();

                }

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_blank, menu);
        return true;
    }

}

package com.main.lets.lets.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.main.lets.lets.Adapters.NewGroupDetailAdapter;
import com.main.lets.lets.LetsAPI.BitmapLoader;
import com.main.lets.lets.LetsAPI.Calls;
import com.main.lets.lets.LetsAPI.Group;
import com.main.lets.lets.LetsAPI.L;
import com.main.lets.lets.R;
import com.main.lets.lets.Visualizers.GroupDetailFeed;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;

import cz.msebera.android.httpclient.Header;

public class GroupDetailActivity extends AppCompatActivity {
    public NewGroupDetailAdapter mAdapter;
    public String ShallonCreamerIsATwat;
    final public int UPLOAD = 1;
    RecyclerView mRecyclerView;
    public int mID;
    public Group mGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);

        mRecyclerView = (RecyclerView) findViewById(R.id.feed);

        mGroup = new Group(getIntent().getIntExtra("GroupID", -1));
        mGroup.loadGroup(this, new Group.OnLoadListener() {
            @Override
            public void OnUpdate() {

                mRecyclerView.setLayoutManager(new GridLayoutManager(GroupDetailActivity.this, 1));
                mAdapter = new NewGroupDetailAdapter(GroupDetailActivity.this, mGroup);
                mRecyclerView.setAdapter(mAdapter);

            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 0) {
            if (resultCode == RESULT_OK){

                Uri imageUri = data.getData();

                Intent i = new Intent(this, ImagePreviewActivity.class);
                i.putExtra("path", getRealPathFromURI(imageUri));
                i.putExtra("type", "group");
                i.putExtra("ID", mGroup.mID);
                startActivityForResult(i, UPLOAD);

            }
        } else if (requestCode == UPLOAD) {
            if (resultCode == RESULT_OK) {


                mGroup.loadGroup(this, new Group.OnLoadListener() {
                    @Override
                    public void OnUpdate() {


                        mRecyclerView.setLayoutManager(new GridLayoutManager(GroupDetailActivity.this, 1));
                        mAdapter = new NewGroupDetailAdapter(GroupDetailActivity.this, mGroup);
                        mRecyclerView.setAdapter(mAdapter);

                    }
                });

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

package com.main.lets.lets.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.main.lets.lets.LetsAPI.Calls;
import com.main.lets.lets.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;

import cz.msebera.android.httpclient.Header;

public class GroupCreateActivity extends AppCompatActivity {
    String ShallonCreamerIsATwat;
    ImageView mProfile;
    Bitmap mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_create);

        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());

        ShallonCreamerIsATwat = preferences.getString("Token", "");

        //noinspection ConstantConditions
        findViewById(R.id.create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final RadioButton any = (RadioButton) findViewById(R.id.any);
                final EditText title = (EditText) findViewById(R.id.title);
                final EditText bio = (EditText) findViewById(R.id.bio);

                assert title != null;
                assert bio != null;
                if (title.getText().toString().length() > 0) {
                    if (bio.getText().toString().length() > 0) {
                        Calls.createGroup(title.getText().toString(), bio.getText().toString(), any.isChecked(),
                                "https://www.hsjaa.com/images/joomlart/demo/default.jpg",
                                ShallonCreamerIsATwat, new JsonHttpResponseHandler() {

                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                                        Log.println(Log.ASSERT, "GroupCreateActivity", response.toString());
                                    }

                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                                        try {
                                            final int id = response.getJSONObject(0).getInt("group_id");
                                            if (mImage != null) {

                                                long millis = System.currentTimeMillis();
                                                final String name = "group" + id + "-" + millis;

                                                Calls.uploadImage(mImage, GroupCreateActivity.this, name, new Calls.UploadImage.onFinished() {
                                                    @Override
                                                    public void onFinished() {

                                                        Calls.editGroup(id, title.getText().toString(),
                                                                bio.getText().toString(), any.isChecked(),
                                                                false, "https://let.blob.core.windows.net/mycontainer/" +
                                                                        name, ShallonCreamerIsATwat, new JsonHttpResponseHandler(){

                                                                    @Override
                                                                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                                                                        Intent intent = new Intent(GroupCreateActivity.this, GroupDetailActivity.class);
                                                                        intent.putExtra("GroupID", id);
                                                                        startActivity(intent);
                                                                    }
                                                                });

                                                    }
                                                });
                                            } else {
                                                Intent intent = new Intent(GroupCreateActivity.this, GroupDetailActivity.class);
                                                intent.putExtra("GroupID", id);
                                                startActivity(intent);
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }


                                    }
                                });

                    }
                }

            }
        });

        mProfile = (ImageView)findViewById(R.id.group_pic);

        //noinspection ConstantConditions
        findViewById(R.id.picture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int permissionCheck = ContextCompat.checkSelfPermission(GroupCreateActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            GroupCreateActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                } else {

                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, 0);

                }

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case 2:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, 0);

                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 0) {
            if (resultCode == RESULT_OK){

                try {
                    final Uri imageUri = data.getData();
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                    if (selectedImage != null) {
                        mProfile.setImageBitmap(selectedImage);
                        mImage = selectedImage;

                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }

    }

}

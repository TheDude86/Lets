package com.main.lets.lets.Activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.main.lets.lets.LetsAPI.BitmapLoader;
import com.main.lets.lets.LetsAPI.Calls;
import com.main.lets.lets.LetsAPI.Group;
import com.main.lets.lets.LetsAPI.User;
import com.main.lets.lets.LetsAPI.UserData;
import com.main.lets.lets.R;

import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

public class ImagePreviewActivity extends AppCompatActivity implements View.OnClickListener{
    SubsamplingScaleImageView mImage;
    boolean upload = false;
    String mURL = "";
    boolean isGroup;
    Bitmap mBitmap;
    Group mGroup;
    User mUser;
    int mID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mID = getIntent().getIntExtra("ID", -1);
        isGroup = getIntent().getStringExtra("type").equalsIgnoreCase("group");

        if (isGroup) {
            mGroup = new Group(mID);
            mGroup.loadGroup(this, new Group.OnLoadListener() {
                @Override
                public void OnUpdate() {
                    if (upload)
                        update();
                    else
                        upload = true;

                }
            });
        } else {
            mUser = new User(mID);
            mUser.load(this, new User.OnLoadListener() {
                @Override
                public void update() {
                    if (upload)
                        update();
                    else
                        upload = true;

                }
            });

        }

        mImage = (SubsamplingScaleImageView)findViewById(R.id.image);

        String str = getIntent().getStringExtra("path");
        mImage.setImage(ImageSource.uri(str));
        mBitmap = (new BitmapLoader(this, str)).decodeSampledBitmapFromFile(300, 300);

        findViewById(R.id.rotate_right).setOnClickListener(this);
        findViewById(R.id.rotate_left).setOnClickListener(this);
        findViewById(R.id.cancel).setOnClickListener(this);
        findViewById(R.id.save).setOnClickListener(this);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View view) {

        int r = mImage.getOrientation();
        switch (view.getId()) {

            case R.id.rotate_left:

                mBitmap = RotateBitmap(mBitmap, -90);
                mImage.setOrientation(r - 90);


                break;

            case R.id.rotate_right:

                mBitmap = RotateBitmap(mBitmap, 90);
                mImage.setOrientation(r + 90);


                break;

            case R.id.save:

                long millis = System.currentTimeMillis();
                mURL = "group" + mGroup.mID + "-" + millis;
                Calls.uploadImage(mBitmap, this, mURL, new Calls.UploadImage.onFinished() {
                    @Override
                    public void onFinished() {
                        if (upload)
                            update();
                        else
                            upload = true;

                    }
                });


                break;

            case R.id.cancel:
                finishActivity(false);
                break;


        }

    }

    public void finishActivity(boolean b) {
        Intent i = new Intent();
        i.putExtra("url", "https://let.blob.core.windows.net/mycontainer/" + mURL);
        setResult(b ? Activity.RESULT_OK : RESULT_CANCELED, i);
        finish();

    }

    public void update() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(ImagePreviewActivity.this);
        builder.setTitle("Picture Uploaded");
        builder.setMessage("Your picture has been uploaded");
        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finishActivity(true);
            }
        });

        String token = (new UserData(this)).ShallonCreamerIsATwat;
        JsonHttpResponseHandler j = new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                builder.create().show();

            }
        };

        if (isGroup) {
            Calls.editGroup(mGroup.mID, mGroup.getmTitle(), mGroup.getmBio(), mGroup.isPublic(), mGroup.isHidden(), "https://let.blob.core.windows.net/mycontainer/" + mURL, token, j);

        } else {
//            params.put("bday", new SimpleDateFormat("MM-dd-yyyy", Locale.US).format(mUserInfo.get("birthday")));
//            params.put("interests", mUserInfo.get("interests"));
//            params.put("edit_user_id", mUserInfo.get("id"));
//            params.put("privacy", mUserInfo.get("privacy"));
//            params.put("pic_ref", mUserInfo.get("picRef"));
//            params.put("gender", mUserInfo.get("gender"));
//            params.put("name", mUserInfo.get("name"));
//            params.put("bio", mUserInfo.get("bio"));
            HashMap<String, Object> info = new HashMap<>();
            info.put("birthday", new Date(mUser.getBirthday().getTime()));
            info.put("interests", mUser.getInterests());

            Calls.editProfile(info, token, j);

        }


    }

    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
}

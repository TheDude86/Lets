package com.main.lets.lets.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.main.lets.lets.LetsAPI.BitmapLoader;
import com.main.lets.lets.LetsAPI.Calls;
import com.main.lets.lets.LetsAPI.Event;
import com.main.lets.lets.LetsAPI.Group;
import com.main.lets.lets.LetsAPI.L;
import com.main.lets.lets.LetsAPI.User;
import com.main.lets.lets.LetsAPI.UserData;
import com.main.lets.lets.R;

import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

public class ImagePreviewActivity extends AppCompatActivity implements View.OnClickListener{
    SubsamplingScaleImageView mImage;
    ProgressDialog mPicLoading;

    boolean upload = false;
    int mRotation = 0;
    String mURL = "";
    String imageType;
    boolean isGroup;
    Bitmap mBitmap;
    Event mEvent;
    String mPath;
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
        imageType = getIntent().getStringExtra("type");


        if (imageType.equalsIgnoreCase("group")) {
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

        } else if (imageType.equalsIgnoreCase("user")) {
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

        } else if (imageType.equalsIgnoreCase("event")) {
            mEvent = new Event(mID);
            mEvent.getEventByID(new Event.onEventLoaded() {
                @Override
                public void EventLoaded(Event e) {
                    if (upload)
                        update();
                    else
                        upload = true;
                }
            });

        }


        mImage = (SubsamplingScaleImageView)findViewById(R.id.image);

        mPath = getIntent().getStringExtra("path");
        mImage.setImage(ImageSource.uri(mPath));
        mBitmap = (new BitmapLoader(this, mPath)).decodeSampledBitmapFromFile(300, 300);

        findViewById(R.id.rotate_right).setOnClickListener(this);
        findViewById(R.id.rotate_left).setOnClickListener(this);
        findViewById(R.id.cancel).setOnClickListener(this);
        findViewById(R.id.save).setOnClickListener(this);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public int JoesModFunctionBcJavaIsStupid(int i, int mod) {

        if (i >= 0 && mod >= 0)
            return i % mod;

        return 270;
    }

    @Override
    public void onClick(View view) {

        int r = mImage.getOrientation();
        switch (view.getId()) {

            case R.id.rotate_left:

                mBitmap = RotateBitmap(mBitmap, -90);
                mImage.setOrientation(JoesModFunctionBcJavaIsStupid(r - 90, 360));
                mRotation -= 90;


                break;

            case R.id.rotate_right:

                mBitmap = RotateBitmap(mBitmap, 90);
                mImage.setOrientation((r + 90) % 360);
                mRotation += 90;

                break;

            case R.id.save:

                if (imageType.equalsIgnoreCase("createGroup")) {

                    finishActivity(true);

                } else {


                    mPicLoading = ProgressDialog.show(this, "",
                            "Uploading Sexy Pic. Please wait...", true);


                    long millis = System.currentTimeMillis();

                    if (imageType.equalsIgnoreCase("group")) {
                        mURL = "group" + mGroup.mID + "-" + millis;

                    } else if (imageType.equalsIgnoreCase("user")) {
                        mURL = "user" + mEvent.mID + "-" + millis;


                    } else if (imageType.equalsIgnoreCase("event")) {
                        int ID = (new UserData(this)).ID;
                        mEvent.uploadImage(mBitmap, "user" + ID + "-" + millis, new Event.OnPictureUploaded() {
                            @Override
                            public void ImageUploaded(Uri url) {
                                mPicLoading.hide();
                                finish();
                                
                            }
                        });

                        return;
                    }

                    Calls.uploadImage(mBitmap, this, mURL, new Calls.UploadImage.onFinished() {
                        @Override
                        public void onFinished() {
                            if (upload)
                                update();
                            else
                                upload = true;

                        }
                    });

                }



                break;

            case R.id.cancel:
                finishActivity(false);
                break;


        }

    }

    public void finishActivity(boolean b) {
        Intent i = new Intent();
        i.putExtra("url", "https://let.blob.core.windows.net/mycontainer/" + mURL);
        i.putExtra("path", mPath);
        i.putExtra("rotation", mRotation);
        setResult(b ? Activity.RESULT_OK : RESULT_CANCELED, i);
        finish();

    }

    public void update() {

        if (imageType.equalsIgnoreCase("createGroup")) {


        } else {
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

            if (imageType.equalsIgnoreCase("group")) {
                Calls.editGroup(mGroup.mID, mGroup.getmTitle(), mGroup.getmBio(), mGroup.isPublic(),
                        mGroup.isHidden(), "https://let.blob.core.windows.net/mycontainer/" + mURL, token, new JsonHttpResponseHandler() {

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                                mPicLoading.hide();
                                builder.create().show();

                            }
                        });
            } else if (imageType.equalsIgnoreCase("user")) {
                mUser.setPropic("https://let.blob.core.windows.net/mycontainer/" + mURL);
                mUser.saveUser(this, new User.OnLoadListener() {
                    @Override
                    public void update() {

                        mPicLoading.hide();
                        builder.create().show();

                    }
                });
            } else if (imageType.equalsIgnoreCase("event")) {

            }


        }




    }

    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
}

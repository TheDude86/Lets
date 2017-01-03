package com.main.lets.lets.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.main.lets.lets.LetsAPI.BitmapLoader;
import com.main.lets.lets.LetsAPI.L;
import com.main.lets.lets.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class NewGroupPictureActivity extends AppCompatActivity {
    public final static int SELECT_PICTURE = 0;
    public final static int UPLOAD_PICTURE = 1;

    CircleImageView mImage;
    Bitmap mBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group_picture);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mImage = (CircleImageView) findViewById(R.id.picture);
        Button b = (Button) findViewById(R.id.next);

        assert mImage != null;
        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int permissionCheck = ContextCompat
                        .checkSelfPermission(NewGroupPictureActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            NewGroupPictureActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                } else {

                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    NewGroupPictureActivity.this.startActivityForResult(photoPickerIntent, SELECT_PICTURE);

                }
            }
        });

        assert b != null;
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mBitmap != null) {
                    Intent i = new Intent(NewGroupPictureActivity.this, GroupBioActivity.class);
                    i.putExtra("name", getIntent().getStringExtra("name"));
                    i.putExtra("image", mBitmap);
                    startActivity(i);

                } else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(NewGroupPictureActivity.this);
                    builder.setTitle("You haven't selected a picture!");
                    builder.setMessage("You have to add a picture for your group because a group's " +
                            "photo is like a person's face and how would you feel if you didn't " +
                            "have a face?  Probably pretty bad");

                    builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.create().show();

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
                i.putExtra("type", "createGroup");
                startActivityForResult(i, UPLOAD_PICTURE);

            }
        } else if (requestCode == UPLOAD_PICTURE) {
            if (resultCode == RESULT_OK) {

                String path = data.getStringExtra("path");
                mBitmap = new BitmapLoader(this, path).decodeSampledBitmapFromFile(250, 250);
                mBitmap = RotateBitmap(mBitmap, data.getIntExtra("rotation", 0));
                mImage.setImageBitmap(mBitmap);

            }
        }

    }

    public static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }


    public String getRealPathFromURI(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = getContentResolver().query(contentUri, proj, null, null, null);
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

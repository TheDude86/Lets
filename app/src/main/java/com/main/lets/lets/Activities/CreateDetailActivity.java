package com.main.lets.lets.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.main.lets.lets.LetsAPI.Calls;
import com.main.lets.lets.R;

import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

public class CreateDetailActivity extends AppCompatActivity {
    String mInterests = "";
    ImageView mProfile;
    EditText interests;
    RadioButton female;
    RadioButton freak;
    RadioButton male;
    Bitmap mImage;
    EditText bio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_detail);

        interests = (EditText) findViewById(R.id.interests);
        female = (RadioButton) findViewById(R.id.female);
        freak = (RadioButton) findViewById(R.id.tranny);
        male = (RadioButton) findViewById(R.id.male);
        mProfile = (ImageView) findViewById(R.id.profile);
        bio = (EditText) findViewById(R.id.bio);
        Button save = (Button) findViewById(R.id.save);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("You have successfully created an account!  Fill out this last bit of information to complete registration")
                .setTitle("Congratulations!");

        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        assert interests != null;
        assert mProfile != null;
        assert female != null;
        assert freak != null;
        assert save != null;
        assert male != null;

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(CreateDetailActivity.this);

                builder.setTitle("Error");

                builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

                if (female.isChecked() || male.isChecked() || freak.isChecked()){

                    try {

                        final ProgressDialog dialog = ProgressDialog.show(CreateDetailActivity.this, "",
                                "Loading. Please wait...", true);

                        if (mImage == null) {
                            updateProfile("http://4.bp.blogspot.com/-cDeYCsNL-ZQ/UozsUJ7EqfI/AAAAAAAAGSk/EtuzOVpHoS0/s1600/andy.png", dialog);

                        } else {
                            SharedPreferences preferences = PreferenceManager
                                    .getDefaultSharedPreferences(getBaseContext());

                            int id = preferences.getInt("UserID", -1);

                            long millis = System.currentTimeMillis();
                            final String name = "user" + id + "-" + millis;

                            Calls.uploadImage(mImage, CreateDetailActivity.this, name, new Calls.UploadImage.onFinished() {
                                @Override
                                public void onFinished() {

                                    try {
                                        updateProfile("https://let.blob.core.windows.net/mycontainer/" + name, dialog);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                }
                            });

                        }


                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                } else {
                    builder.setMessage("You must select a gender");

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

            }
        });

        mProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int permissionCheck = ContextCompat.checkSelfPermission(CreateDetailActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            CreateDetailActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                } else {

                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, 0);

                }

            }
        });

        interests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] items = {" Party ", " Eating & Drinking ", " Studying ",
                        " TV & Movies ", " Video Games ", " Sports ", " Music ", " Relax ",
                        " Other "};
                // arraylist to keep the selected items
                final ArrayList<Integer> seletedItems = new ArrayList<>();

                AlertDialog dialog = new AlertDialog.Builder(CreateDetailActivity.this)
                        .setTitle("Select Interests")
                        .setCancelable(true)
                        .setMultiChoiceItems(items, null,
                                new DialogInterface.OnMultiChoiceClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int indexSelected,
                                                        boolean isChecked) {
                                        if (isChecked) {
                                            // If the user checked the item, add
                                            // it to the selected items
                                            seletedItems.add(indexSelected);
                                        } else if (seletedItems
                                                .contains(indexSelected)) {
                                            // Else, if the item is already in
                                            // the array, remove it
                                            seletedItems.remove(Integer.valueOf(
                                                    indexSelected));
                                        }
                                    }
                                })
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                String rawString = MapListToString(seletedItems);

                                interests.setText(rawString);
                                CreateDetailActivity.this.mInterests = toInterestDataString(rawString);

                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                //  Your code when user clicked on Cancel
                            }
                        }).create();
                dialog.show();
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

    public void updateProfile(String image, final ProgressDialog d) throws ParseException{
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());

        HashMap<String, Object> userInfo = new HashMap<>();
        userInfo.put("birthday", new SimpleDateFormat("MM-dd-yyyy").parse(getIntent().getStringExtra("birthday")));
        userInfo.put("name", getIntent().getStringExtra("name"));
        userInfo.put("id", preferences.getInt("UserID", -1));
        userInfo.put("bio", bio.getText().toString());
        userInfo.put("interests", mInterests);
        userInfo.put("picRef", image);
        userInfo.put("privacy", 1);

        if (male.isChecked())
            userInfo.put("gender", 0);
        else if (female.isChecked())
            userInfo.put("gender", 1);
        else if (freak.isChecked())
            userInfo.put("gender", 2);


        Calls.editProfile(userInfo, getIntent().getStringExtra("token"), new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                d.hide();

                AlertDialog.Builder builder = new AlertDialog.Builder(CreateDetailActivity.this);

                builder.setMessage("You have successfully updated your profile!")
                        .setTitle("Profile Updated");

                builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });
    }

    public String toInterestDataString(String rawString){
        rawString = rawString.replace("Eating & Drinking", "Eat/Drink");
        rawString = rawString.replace("TV & Movies", "TV/Movies");
        rawString = rawString.replace("Studying", "Study");
        rawString = rawString.replace("Relaxing", "Relax");
        rawString = rawString.replace(" ", "");
        rawString = rawString.replace("VideoGames", "Video Games");

        return rawString;
    }

    public static String MapListToString(ArrayList l) {
        StringBuilder s = new StringBuilder();

        for (Object i : l) {
            switch ((int) i) {
                case 0:
                    if (s.length() > 0)
                        s.append(", ");
                    s.append("Party");

                    break;
                case 1:
                    if (s.length() > 0)
                        s.append(", ");
                    s.append("Eating & Drinking");

                    break;
                case 2:
                    if (s.length() > 0)
                        s.append(", ");
                    s.append("Studying");

                    break;
                case 3:
                    if (s.length() > 0)
                        s.append(", ");
                    s.append("TV & Movies");

                    break;
                case 4:
                    if (s.length() > 0)
                        s.append(", ");
                    s.append("Video Games");

                    break;
                case 5:
                    if (s.length() > 0)
                        s.append(", ");
                    s.append("Sports");

                    break;
                case 6:
                    if (s.length() > 0)
                        s.append(", ");
                    s.append("Music");

                    break;
                case 7:
                    if (s.length() > 0)
                        s.append(", ");
                    s.append("Relaxing");

                    break;
                case 8:
                    if (s.length() > 0)
                        s.append(", ");
                    s.append("Other");

                    break;

            }
        }

        return s.toString();
    }

}

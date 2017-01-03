package com.main.lets.lets.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.main.lets.lets.LetsAPI.Calls;
import com.main.lets.lets.R;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class AccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        final Button create = (Button) findViewById(R.id.create);

        assert create != null;
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder errorBuilder = new AlertDialog.Builder(AccountActivity.this);

                errorBuilder.setTitle("You done goofed");

                errorBuilder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });


                final EditText email = (EditText) findViewById(R.id.email);
                final EditText password = (EditText) findViewById(R.id.password);
                EditText confirm = (EditText) findViewById(R.id.confirm);

                assert email != null;
                assert confirm != null;
                assert password != null;

                if (email.getText().toString().length() > 0) {
                    if (password.getText().toString().length() > 5) {
                        if (password.getText().toString().equals(confirm.getText().toString())) {


                            final String name = getIntent().getStringExtra("name");
                            final String birthday = getIntent().getStringExtra("birthday");

                            if (create.getText().toString().equals("Account Created!")) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(AccountActivity.this);

                                builder.setMessage("We're not quite sure what you're trying to accomplish " +
                                        "right now but if you want to see your profile, just hit " +
                                        "that little arrow up top until you see a screen with a list of events.");

                                builder.setTitle("What are you doing?");

                                builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                    }
                                });

                                AlertDialog dialog = builder.create();
                                dialog.show();

                            } else {

                                final ProgressDialog loading = ProgressDialog.show(AccountActivity.this, "",
                                        "Loading. Please wait...", true);

                                Calls.createUser(email.getText().toString(), password.getText().toString(), name, birthday, new JsonHttpResponseHandler() {

                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                        try {
                                            loading.hide();

                                            if (response.has("accessToken")) {
                                                create.setText(R.string.account_created);

                                                SharedPreferences preferences = PreferenceManager
                                                        .getDefaultSharedPreferences(getBaseContext());

                                                SharedPreferences.Editor editor = preferences.edit();

                                                editor.putString("Token", "Bearer " + response.getString("accessToken"));
                                                editor.putString("password", password.getText().toString());
                                                editor.putString("email", email.getText().toString());
                                                editor.putInt("UserID", response.getInt("user_id"));
                                                editor.apply();

                                                Intent intent = new Intent(AccountActivity.this, UserDetailActivity.class);
                                                intent.putExtra("UserID", response.getInt("user_id"));
                                                intent.putExtra("create", true);
                                                startActivity(intent);

                                            } else {
                                                errorBuilder.setMessage(response.getString("error"));
                                                AlertDialog errorDialog = errorBuilder.create();
                                                errorDialog.show();
                                            }


                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                });

                            }


                        } else {
                            errorBuilder.setMessage("Okay so you already screwed up putting in " +
                                    "your password once because these two don't match, you should " +
                                    "just start over and try again.");
                            AlertDialog errorDialog = errorBuilder.create();
                            errorDialog.show();
                        }
                    } else {
                        errorBuilder.setMessage("Your password is bad and should be at least 6 " +
                                "characters long, I mean come on, how hard is it to remember 6 " +
                                "characters?");
                        AlertDialog errorDialog = errorBuilder.create();
                        errorDialog.show();
                    }
                } else {
                    errorBuilder.setMessage("You need to put in an email address this what you use " +
                            "to log in, and don't worry about us selling any of your personal info" +
                            " to advertising companies... We don't know how to do that");
                    AlertDialog errorDialog = errorBuilder.create();
                    errorDialog.show();
                }


            }
        });

    }
}

package com.main.lets.lets.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

                errorBuilder.setTitle("Error");

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

                                builder.setMessage("You have already created an account, go back to see your profile")
                                        .setTitle("Account Already Created");

                                builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                    }
                                });

                                AlertDialog dialog = builder.create();
                                dialog.show();

                            } else {
                                Calls.createUser(email.getText().toString(), password.getText().toString(), name, birthday, new JsonHttpResponseHandler() {

                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                        try {

                                            if (response.has("accessToken")) {
                                                create.setText("Account Created!");

                                                SharedPreferences preferences = PreferenceManager
                                                        .getDefaultSharedPreferences(getBaseContext());

                                                SharedPreferences.Editor editor = preferences.edit();

                                                editor.putString("Token", "Bearer " + response.getString("accessToken"));
                                                editor.putString("password", password.getText().toString());
                                                editor.putString("email", email.getText().toString());
                                                editor.putInt("UserID", response.getInt("user_id"));
                                                editor.commit();

                                                Intent intent = new Intent(AccountActivity.this, CreateDetailActivity.class);
                                                intent.putExtra("token", "Bearer " + response.getString("accessToken"));
                                                intent.putExtra("birthday", birthday);
                                                intent.putExtra("name", name);
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
                            errorBuilder.setMessage("Your passwords do not match");
                            AlertDialog errorDialog = errorBuilder.create();
                            errorDialog.show();
                        }
                    } else {
                        errorBuilder.setMessage("Password must be at least six characters long");
                        AlertDialog errorDialog = errorBuilder.create();
                        errorDialog.show();
                    }
                } else {
                    errorBuilder.setMessage("Email cannot be empty");
                    AlertDialog errorDialog = errorBuilder.create();
                    errorDialog.show();
                }


            }
        });

    }
}

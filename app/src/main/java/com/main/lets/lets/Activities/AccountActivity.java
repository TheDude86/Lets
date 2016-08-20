package com.main.lets.lets.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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

                                        }


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            });

                        }
                    }
                }


            }
        });

    }
}

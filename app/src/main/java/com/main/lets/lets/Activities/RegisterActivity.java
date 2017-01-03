package com.main.lets.lets.Activities;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.main.lets.lets.R;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //noinspection ConstantConditions
        findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText name = (EditText)findViewById(R.id.name);
                EditText last = (EditText)findViewById(R.id.last);
                AlertDialog.Builder errorBuilder = new AlertDialog.Builder(RegisterActivity.this);
                errorBuilder.setPositiveButton("Okay", null);

                if ((name != null ? name.getText().toString().length() : 0) > 0) {
                    if ((last != null ? last.getText().toString().length() : 0) > 0) {
                        assert name != null;
                        assert last != null;
                        Intent intent = new Intent(RegisterActivity.this, DateActivity.class);
                        intent.putExtra("name", name.getText().toString() + " " + last.getText().toString());
                        startActivity(intent);

                    } else {
                        errorBuilder.setMessage("We're skeptical you have no name, if you can " +
                                "prove you don't have one then you can make an account without a " +
                                "name but until then, fill out something.");

                        errorBuilder.setTitle("You seriously have no name?");

                        AlertDialog errorDialog = errorBuilder.create();
                        errorDialog.show();
                    }
                } else {

                    errorBuilder.setMessage("We're skeptical you have no name, if you can " +
                            "prove you don't have one then you can make an account without a " +
                            "name but until then, fill out something.");

                    errorBuilder.setTitle("You seriously have no name?");

                    AlertDialog errorDialog = errorBuilder.create();
                    errorDialog.show();
                }

            }
        });
    }
}

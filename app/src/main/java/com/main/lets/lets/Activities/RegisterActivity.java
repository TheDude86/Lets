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
                        intent.putExtra("name", name.getText().toString());
                        intent.putExtra("last", last.getText().toString());
                        startActivity(intent);

                    } else {
                        errorBuilder.setMessage("You must fill in a last name").setTitle("Error");

                        AlertDialog errorDialog = errorBuilder.create();
                        errorDialog.show();
                    }
                } else {

                    errorBuilder.setMessage("You must fill in a name").setTitle("Error");

                    AlertDialog errorDialog = errorBuilder.create();
                    errorDialog.show();
                }

            }
        });
    }
}

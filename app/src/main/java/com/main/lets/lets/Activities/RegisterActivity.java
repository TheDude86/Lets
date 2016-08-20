package com.main.lets.lets.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
                if ((name != null ? name.getText().toString().length() : 0) > 0) {
                    Intent intent = new Intent(RegisterActivity.this, DateActivity.class);
                    intent.putExtra("name", name.getText().toString());
                    startActivity(intent);
                }

            }
        });
    }
}

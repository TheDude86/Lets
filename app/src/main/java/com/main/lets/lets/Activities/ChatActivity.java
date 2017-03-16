package com.main.lets.lets.Activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.vision.text.Text;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.main.lets.lets.Adapters.ChatAdapter;
import com.main.lets.lets.LetsAPI.Chat;
import com.main.lets.lets.LetsAPI.L;
import com.main.lets.lets.LetsAPI.User;
import com.main.lets.lets.LetsAPI.UserData;
import com.main.lets.lets.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ChatActivity extends AppCompatActivity {
    DatabaseReference db;
    GridLayoutManager mManager;
    ChatAdapter mAdapter;
    String mPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mPath = getIntent().getStringExtra("Path");
        db = FirebaseDatabase.getInstance().getReference().child(mPath);

        final Button send = (Button) findViewById(R.id.btn_comment);
        mManager = new GridLayoutManager(this, 1);
        mAdapter = new ChatAdapter(this, mPath);
        mAdapter.setElementAddedListener(new ChatAdapter.ElementAddedListener() {
            @Override
            public void onElementAdded(int position) {
                mManager.scrollToPosition(position - 1);
            }
        });

        RecyclerView r = (RecyclerView) findViewById(R.id.view);
        r.setLayoutManager(mManager);
        r.setAdapter(mAdapter);


        final User u = new User(new UserData(this).ID);

        u.load(this, new User.OnLoadListener() {
            @Override
            public void update() {

                send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        TextView message = (TextView) findViewById(R.id.txt_comment);

                        if (!message.getText().toString().equals("")) {
                            long t = Calendar.getInstance().getTimeInMillis();
                            Chat c = new Chat(message.getText().toString(), u.getName(), u.getPropic(), t, u.getUserID());
                            db.push().setValue(c);

                            message.setText("");

                        }


                    }
                });

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                finish();
                break;
            case R.id.action_leave:
                AlertDialog.Builder b = new AlertDialog.Builder(this);
                b.setTitle("Leave Chat?")
                        .setMessage("Are you sure you want to leave this chat?  If you do, you might " +
                                "miss a message from that one guy who says something mildly funny " +
                                "that makes you exhale out of your nose when you read something " +
                                "funny but not too funny to actually laugh.")
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("Leave", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                String[] pieces = mPath.split("/");

                                String type = (mPath.contains("group")) ? "group" : "event";
                                String s = String.format("users/%d/chats/%s%s", new UserData(ChatActivity.this).ID, type, pieces[1]);
                                FirebaseDatabase.getInstance().getReference().child(s).removeValue();
                                finish();

                            }
                        });

                b.create().show();

                break;
        }
        return true;
    }


    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }
}

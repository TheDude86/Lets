package com.main.lets.lets.Activities;

import android.content.DialogInterface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.main.lets.lets.Adapters.ChatAdapter;
import com.main.lets.lets.LetsAPI.Chat;
import com.main.lets.lets.LetsAPI.User;
import com.main.lets.lets.LetsAPI.UserData;
import com.main.lets.lets.R;

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
    public boolean onCreateOptionsMenu(final Menu menu) {

        FirebaseDatabase.getInstance().getReference().child(mPath + "/settings/list").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(Boolean.class))
                    getMenuInflater().inflate(R.menu.menu_list, menu);
                else
                    getMenuInflater().inflate(R.menu.menu_chat, menu);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



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

            case R.id.action_list:

                LayoutInflater li = LayoutInflater.from(this);
                View layout = li.inflate(R.layout.event_list, null);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Current List");

                Button toggle = (Button) layout.findViewById(R.id.list_action);

                RecyclerView list = (RecyclerView) layout.findViewById(R.id.list);
                list.setLayoutManager(new GridLayoutManager(this, 1));

                list.setAdapter(new RecyclerView.Adapter() {
                    @Override
                    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                        View v = LayoutInflater.from(ChatActivity.this).inflate(R.layout.row_chat_bubble, parent, false);

                        return new Holder(v);
                    }

                    @Override
                    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

                        GradientDrawable bgShape = (GradientDrawable) ((Holder)holder).mBackground.getBackground();
                        bgShape.setColor(0xFFFFFFFF);

                    }

                    @Override
                    public int getItemCount() {
                        return 3;
                    }

                    class Holder extends RecyclerView.ViewHolder {
                        RelativeLayout mBackground;

                        public Holder(View itemView) {
                            super(itemView);
                            mBackground = (RelativeLayout) itemView.findViewById(R.id.background);

                        }
                    }

                });


                toggle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(ChatActivity.this, "Clicked", Toast.LENGTH_LONG).show();
                    }
                });

                builder.setView(layout);
                builder.create().show();

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

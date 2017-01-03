package com.main.lets.lets.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.main.lets.lets.Adapters.InviteFriendsToGroupAdapter;
import com.main.lets.lets.Adapters.InviteFriendsToGroupEntityAdapter;
import com.main.lets.lets.LetsAPI.User;
import com.main.lets.lets.LetsAPI.UserData;
import com.main.lets.lets.R;

@SuppressWarnings("ConstantConditions")
public class InviteFriendsToGroupActivity extends AppCompatActivity implements View.OnClickListener {
    InviteFriendsToGroupAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friends_to_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final ProgressDialog loading = ProgressDialog.show(this, "",
                "Loading friends. Please wait...", true);

        int ID = new UserData(this).ID;

        final User u = new User(ID);
        u.loadFull(this, new User.OnLoadListener() {
            @Override
            public void update() {
                mAdapter = new InviteFriendsToGroupAdapter(InviteFriendsToGroupActivity.this, u.mFriends);

                RecyclerView recyclerView = (RecyclerView) findViewById(R.id.view);
                recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
                recyclerView.setAdapter(mAdapter);

                findViewById(R.id.invite).setOnClickListener(InviteFriendsToGroupActivity.this);
                findViewById(R.id.skip).setOnClickListener(InviteFriendsToGroupActivity.this);



                loading.hide();

            }
        });

    }

    @Override
    public void onClick(View v) {
        final int id = getIntent().getIntExtra("GroupID", -1);


        final ProgressDialog loading = ProgressDialog.show(this, "",
                "Inviting friends. Please wait...", true);

        if (v.getId() == R.id.invite) {
            mAdapter.notifyInvite(id, new User.OnLoadListener() {
                @Override
                public void update() {

                    loading.hide();

                    AlertDialog.Builder builder = new AlertDialog.Builder(InviteFriendsToGroupActivity.this);
                    builder.setTitle("Friends Invited");
                    builder.setMessage("Your friends have been invited to your group");
                    builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(InviteFriendsToGroupActivity.this, GroupDetailActivity.class);
                            intent.putExtra("GroupID", id);
                            startActivity(intent);

                        }
                    });

                    builder.create().show();

                }
            });

        } else {
            Intent intent = new Intent(InviteFriendsToGroupActivity.this, GroupDetailActivity.class);
            intent.putExtra("GroupID", id);
            startActivity(intent);
        }

    }
}

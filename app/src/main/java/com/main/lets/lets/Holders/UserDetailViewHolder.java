package com.main.lets.lets.Holders;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.main.lets.lets.Activities.EditProfileActivity;
import com.main.lets.lets.Activities.UserDetailActivity;
import com.main.lets.lets.Adapters.SearchEntityAdapter;
import com.main.lets.lets.LetsAPI.Calls;
import com.main.lets.lets.LetsAPI.Entity;
import com.main.lets.lets.LetsAPI.EventEntity;
import com.main.lets.lets.LetsAPI.User;
import com.main.lets.lets.LetsAPI.UserData;
import com.main.lets.lets.R;

import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Joe on 6/30/2016.
 */
public class UserDetailViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public RelativeLayout mActions;
    public TextView mFriendsButton;
    public TextView mEventsButton;
    public TextView mGroupsButton;
    public ImageButton mOptions;
    public TextView mInterests;
    public TextView mFriends;
    public TextView mDetail;
    public TextView mScore;
    public EditText mName;
    public ImageView mPic;
    public TextView mBio;

    public RelativeLayout action1;
    public RelativeLayout action2;
    public RelativeLayout action3;
    public RelativeLayout action4;

    public ImageView image1;
    public ImageView image2;
    public ImageView image3;
    public ImageView image4;

    public TextView text1;
    public TextView text2;
    public TextView text3;
    public TextView text4;

    public TextView mPictureText;
    public AppCompatActivity mActivity;
    public User mUser;

    public boolean isEditing;

    public UserDetailViewHolder(View itemView, User u, AppCompatActivity a, boolean editing) {
        super(itemView);

        mUser = u;
        mActivity = a;
        isEditing = editing;
        mPictureText = (TextView) itemView.findViewById(R.id.edit_picture);
        mInterests = (TextView) itemView.findViewById(R.id.txt_interests);
        mFriendsButton = (TextView) itemView.findViewById(R.id.friends);
        mActions = (RelativeLayout) itemView.findViewById(R.id.actions);
        mFriends = (TextView) itemView.findViewById(R.id.txt_friends);
        mGroupsButton = (TextView) itemView.findViewById(R.id.groups);
        mEventsButton = (TextView) itemView.findViewById(R.id.events);
        mOptions = (ImageButton) itemView.findViewById(R.id.options);
        mPic = (ImageView) itemView.findViewById(R.id.img_profile);
        mScore = (TextView) itemView.findViewById(R.id.txt_score);
        mDetail = (TextView) itemView.findViewById(R.id.detail);
        mName = (EditText) itemView.findViewById(R.id.name);
        mBio = (EditText) itemView.findViewById(R.id.bio);

        image1 = (ImageView) itemView.findViewById(R.id.img1);
        image2 = (ImageView) itemView.findViewById(R.id.img2);
        image3 = (ImageView) itemView.findViewById(R.id.img3);
        image4 = (ImageView) itemView.findViewById(R.id.img4);

        action1 = (RelativeLayout) itemView.findViewById(R.id.action1);
        action2 = (RelativeLayout) itemView.findViewById(R.id.action2);
        action3 = (RelativeLayout) itemView.findViewById(R.id.action3);
        action4 = (RelativeLayout) itemView.findViewById(R.id.action4);

        text1 = (TextView) itemView.findViewById(R.id.text1);
        text2 = (TextView) itemView.findViewById(R.id.text2);
        text3 = (TextView) itemView.findViewById(R.id.text3);
        text4 = (TextView) itemView.findViewById(R.id.text4);

        setEditing(isEditing);
        if (isEditing)
            setOwnerToolbar();

        switch (u.mRelationship) {

            case NONE:
                setGuestToolbar();
                setSearchToolbar();

                break;
            case SENT:
                setSentToolbar();
                setSearchToolbar();

                break;
            case RECIEVED:
                setReceivedToolbar();
                setSearchToolbar();

                break;
            case FRIEND:
                setFriendToolbar();
                setSearchToolbar();

                break;
            case OWNER:
                setOwnerToolbar();
                setSearchToolbar();

                break;
        }

    }

    public void setSearchToolbar() {
        image2.setImageResource(R.drawable.ic_list_full_black_24dp);
        text2.setText("Search\nEvents");

        image3.setImageResource(R.drawable.ic_full_person_black_24dp);
        text3.setText("Search\nFriends");

        image4.setImageResource(R.drawable.ic_group_black_24dp);
        text4.setText("Search\nGroups");

        action2.setOnClickListener(this);
        action3.setOnClickListener(this);
        action4.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        String s = "";

        if (v.equals(action2)) {
            s = "Events";
        } else if (v.equals(action3)) {
            s = "Friends";
        } else if (v.equals(action4)) {
            s = "Groups";
        }

        searchDialog(s);

    }

    public void searchDialog(String feed) {

        ArrayList<Entity> entityFeed = new ArrayList<>();

        if (feed.equalsIgnoreCase("friends")) {
            entityFeed = mUser.mFriends;
        }
        else if (feed.equalsIgnoreCase("events")) {
            for (EventEntity e : mUser.mEvents)
                if (e.mStatus)
                   entityFeed.add(e);

        } else if (feed.equalsIgnoreCase("groups")) {
            entityFeed = mUser.mGroups;
        }

        SearchEntityAdapter adapter = new SearchEntityAdapter(entityFeed, mActivity);

        View view = View.inflate(mActivity, R.layout.dialog_search_entity, null);
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.view);
        SearchView searchView = (SearchView) view.findViewById(R.id.search);

        final ArrayList<Entity> finalEntityFeed = entityFeed;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<Entity> newFeed = new ArrayList<>();

                for (Entity e: finalEntityFeed) {
                    if (e.mText.toLowerCase().contains(newText.toLowerCase()))
                        newFeed.add(e);
                }

                SearchEntityAdapter adapter = new SearchEntityAdapter(newFeed, mActivity);
                recyclerView.setLayoutManager(new GridLayoutManager(mActivity, 1));
                recyclerView.setAdapter(adapter);

                return false;
            }
        });

        recyclerView.setLayoutManager(new GridLayoutManager(mActivity, 1));
        recyclerView.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("Searching " + feed);
        builder.setView(view);
        builder.setNegativeButton("Cancel", null);
        builder.create().show();


    }


    public void setFriendToolbar() {
        resetToolbar();
        mDetail.setText("You two are friends!");

        action1.setVisibility(View.GONE);

    }

    public void setReceivedToolbar() {
        resetToolbar();
        mDetail.setText("This person sent you a friend request");

        image1.setImageResource(R.drawable.ic_help_outline_black_24dp);
        text1.setText("Respond to\nFriend Request");

        action1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setTitle("Respond to Friend Request");
                builder.setMessage("Would you like to accept this person's friend request?");
                builder.setNegativeButton("Cancel", null);
                builder.setNeutralButton("Reject", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        UserData d = new UserData(mActivity);
                        Calls.removeFriend(mUser.mID, d.ShallonCreamerIsATwat, new JsonHttpResponseHandler() {

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                setGuestToolbar();
                                setSearchToolbar();

                            }
                        });

                    }
                });

                builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        UserData d = new UserData(mActivity);
                        Calls.sendFriendRequest(mUser.mID, d, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                setFriendToolbar();
                                setSearchToolbar();

                            }
                        });

                    }
                });

                builder.create().show();

            }
        });

    }

    public void setSentToolbar() {
        resetToolbar();
        mDetail.setText("You sent them a friend request");

        image1.setImageResource(R.drawable.ic_sent_download);
        text1.setText("Friend\nRequest Sent");

        action1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setTitle("Revoke Friend Request?");
                builder.setMessage("Would you like to revoke your friend request?");
                builder.setNegativeButton("Cancel", null);

                builder.setPositiveButton("Revoke", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        UserData d = new UserData(mActivity);
                        Calls.removeFriend(mUser.mID, d.ShallonCreamerIsATwat, new JsonHttpResponseHandler() {

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                setGuestToolbar();
                                setSearchToolbar();

                            }
                        });

                    }
                });

                builder.create().show();

            }
        });

    }

    public void setGuestToolbar() {
        resetToolbar();
        mDetail.setText("");

        image1.setImageResource(R.drawable.ic_add_black_24dp);
        text1.setText("Send Friend\nRequest");

        action1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calls.sendFriendRequest(mUser.mID, new UserData(mActivity), new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mActivity);
                        builder.setTitle("Friend Request Sent");
                        builder.setMessage("Your friend request has been sent");
                        builder.setPositiveButton("Okay", null);
                        builder.create().show();

                        setSentToolbar();
                        setSearchToolbar();

                    }
                });
            }
        });

    }

    public void setOwnerToolbar() {
        resetToolbar();
        mDetail.setText("This is your profile, lookin good " + new String(Character.toChars(0x1F609)));


        image1.setImageResource(R.drawable.ic_mode_edit_black_24dp);
        text1.setText("Edit\nProfile");

        action1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEditToolbar();

            }
        });

    }

    public void setEditToolbar() {
        resetToolbar();
        mDetail.setText("");
        setEditing(true);

        final User u = mUser;

        image1.setImageResource(R.drawable.ic_done_black_24dp);
        image2.setImageResource(R.drawable.ic_clear_black_24dp);
        image3.setImageResource(R.drawable.ic_favorite_black_24dp);
        image4.setImageResource(R.drawable.ic_settings_black_24dp);

        text1.setText("Save\nChanges");
        text2.setText("Cancel");
        text3.setText("Change\nInterests");
        text4.setText("More\nSettings");

        action1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUser.setName(mName.getText().toString());
                mUser.setBio(mBio.getText().toString());

                final ProgressDialog dialog = ProgressDialog.show(mActivity, "",
                        "Saving. Please wait...", true);

                mUser.saveUser(mActivity, new User.OnLoadListener() {
                    @Override
                    public void update() {
                        setEditing(false);
                        setOwnerToolbar();
                        setSearchToolbar();
                        dialog.hide();

                    }
                });

            }
        });

        action2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUser = u;
                mName.setText(u.getName());
                mBio.setText(u.getBio());

                setEditing(false);
                setOwnerToolbar();
                setSearchToolbar();

            }
        });

        action3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] items = {" Party ", " Eating & Drinking ", " Studying ",
                        " TV & Movies ", " Video Games ", " Sports ", " Music ", " Relax ",
                        " Other "};
                // arraylist to keep the selected items

                final boolean[] interests = new boolean[mUser.mInterests.size()];

                final String[] categories = {"Party",
                        "Eat/Drink",
                        "Study",
                        "TV/Movies",
                        "Video Games",
                        "Sports",
                        "Music",
                        "Relax",
                        "Other"};

                for (int i = 0; i < interests.length; i++) {
                    interests[i] = mUser.mInterests.get(categories[i]);

                }

                AlertDialog mainDialog = new AlertDialog.Builder(mActivity)
                        .setTitle("Select Interests")
                        .setCancelable(true)
                        .setMultiChoiceItems(items, interests,
                                new DialogInterface.OnMultiChoiceClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                                        interests[indexSelected] = isChecked;

                                    }
                                })
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {

                                for (int i = 0; i < categories.length; i++) {
                                    mUser.mInterests.put(categories[i], interests[i]);

                                }


                                final ProgressDialog loading = ProgressDialog.show(mActivity, "",
                                        "Saving. Please wait...", true);

                                mUser.saveUser(mActivity, new User.OnLoadListener() {
                                    @Override
                                    public void update() {
                                        mInterests.setText(mUser.getInterestsString());
                                        loading.hide();
                                    }
                                });


                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        }).create();
                mainDialog.show();


            }
        });

        action4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(mActivity, EditProfileActivity.class);
                i.putExtra("ID", mUser.mID);
                mActivity.startActivity(i);

            }
        });

    }

    public void setEditing(boolean b) {

        mBio.setHint(b ? "Insert cool bio here <--" : "bio coming soon...");

        mName.setFocusable(b);
        mName.setFocusableInTouchMode(b);

        mBio.setFocusable(b);
        mBio.setFocusableInTouchMode(b);

        mName.getBackground()
                .setColorFilter(b ? Color.BLACK : Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        mBio.getBackground()
                .setColorFilter(b ? Color.BLACK : Color.WHITE, PorterDuff.Mode.SRC_ATOP);

        View.OnClickListener l = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int permissionCheck = ContextCompat
                        .checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                } else {

                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    mActivity.startActivityForResult(photoPickerIntent, UserDetailActivity.SELECT_PICTURE);

                }

            }
        };

        mPic.setOnClickListener(b ? l : null);

        mPictureText.setAlpha(b ? 1 : 0);

    }

    public void resetToolbar() {

        action1.setVisibility(View.VISIBLE);
        action2.setVisibility(View.VISIBLE);
        action3.setVisibility(View.VISIBLE);
        action4.setVisibility(View.VISIBLE);

        action1.setOnClickListener(null);
        action2.setOnClickListener(null);
        action3.setOnClickListener(null);
        action4.setOnClickListener(null);

        image1.setImageBitmap(null);
        image2.setImageBitmap(null);
        image3.setImageBitmap(null);
        image4.setImageBitmap(null);

        text1.setText("");
        text2.setText("");
        text3.setText("");
        text4.setText("");

    }

}

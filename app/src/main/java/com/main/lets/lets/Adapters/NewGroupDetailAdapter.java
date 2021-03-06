package com.main.lets.lets.Adapters;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.main.lets.lets.Activities.ChatActivity;
import com.main.lets.lets.Activities.EventDetailActivity;
import com.main.lets.lets.Activities.InviteActivity;
import com.main.lets.lets.LetsAPI.Calls;
import com.main.lets.lets.LetsAPI.Comment;
import com.main.lets.lets.LetsAPI.Entity;
import com.main.lets.lets.LetsAPI.Event;
import com.main.lets.lets.LetsAPI.Group;
import com.main.lets.lets.LetsAPI.IntentDecorator;
import com.main.lets.lets.LetsAPI.L;
import com.main.lets.lets.LetsAPI.User;
import com.main.lets.lets.LetsAPI.UserData;
import com.main.lets.lets.R;
import com.rey.material.app.TimePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Joe on 12/24/2016.
 */
public class NewGroupDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    AppCompatActivity mActivity;
    NetworkListener mListener;
    long mMessageCount;
    MainHolder mHolder;
    Group mGroup;

    public NewGroupDetailAdapter(AppCompatActivity a, Group g) {

        mActivity = a;
        mGroup = g;

        String s = String.format("groups/%d/chat", mGroup.mID);
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child(s);
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mMessageCount = dataSnapshot.getChildrenCount();

                if (mListener != null)
                    mListener.onFirebaseMessageUpdate();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private interface NetworkListener {
        void onFirebaseMessageUpdate();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == 0)
            return new MainHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_group_detail, parent, false));

        if (viewType == 1)
            return new DiscussionHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_discussion, parent, false));

        if (viewType == 2)
            return new CommentHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_entity_with_picture, parent, false));

        return new EventHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_group_event, parent, false));

    }

    @Override
    public int getItemViewType(int position) {

        if (position == 0)
            return 0;

        if (position == 1)
            return 1;

        String c = mGroup.mComments.get(position - 2).mDetail;

        if (c.startsWith("{") && c.endsWith("}")) {
            try {
                new JSONObject(c);

                return 3;
            } catch (JSONException e) {
                return 2;
            }
        }

        return position;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (position == 0) {
            mHolder = (MainHolder) holder;
            mHolder.populateHolder();

            mHolder.mTitle.setText(mGroup.getmTitle());
            mHolder.mBio.setText(mGroup.getmBio());
            mGroup.loadImage(mActivity, mHolder.mGroupPic);

            mHolder.mMembersList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMembers();
                }
            });

        } else if (position == 1) {
            final DiscussionHolder d = (DiscussionHolder) holder;
            d.mMessages.setText(String.format("There are %d messages", mMessageCount));

            mListener = new NetworkListener() {
                @Override
                public void onFirebaseMessageUpdate() {
                    d.mMessages.setText(String.format("There are %d messages", mMessageCount));

                }
            };

        } else {

            if (holder instanceof CommentHolder) {
                CommentHolder h = (CommentHolder) holder;
                Comment c = mGroup.mComments.get(position - 1);


                h.mAuthor.setText(c.mText);
                h.mComment.setText(c.mDetail);
                h.loadUser(c.mAuthorID);


            } else if (holder instanceof EventHolder) {
                final EventHolder h = (EventHolder) holder;

                final Comment c = mGroup.mComments.get(position - 2);

                h.loadUser(c.mAuthorID);
                h.mHost.setText(c.mText);
                h.mEventContainer.setVisibility(View.GONE);

                h.mEventContainer.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {

                        UserData d = new UserData(mActivity);

                        if (c.mAuthorID == d.ID) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                            builder.setTitle("Delete Event?");
                            builder.setMessage("Are you sure you want to delete this event from the group?");
                            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    Calls.deleteGroupComment(new UserData(mActivity), mGroup.getGroupID(), c.mID, new JsonHttpResponseHandler() {

                                        @Override
                                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                                            notifyDataSetChanged();

                                        }
                                    });

                                }
                            });

                            builder.setNegativeButton("Cancel", null);
                            builder.create().show();
                        }

                        return true;
                    }
                });

                try {
                    JSONObject j = new JSONObject(c.mDetail);

                    Event e = new Event(j.getInt("event id"));
                    e.getEventByID(new Event.onEventLoaded() {
                        @Override
                        public void EventLoaded(final Event e) {

                            if (e == null) {
                                h.mLoadingContainer.setVisibility(View.GONE);

                            } else {
                                if (!e.hasTitle()) {
                                    h.mTitle.setVisibility(View.GONE);
                                    h.mTitlebar.setVisibility(View.GONE);

                                } else
                                    h.mTitle.setText(e.getTitle());


                                h.mDay.setText(e.getDay());
                                h.mMonth.setText(e.getMonth());
                                h.mTime.setText(e.getTimeSpanString());

                                if (!e.hasLocation())
                                    h.mLocationContainer.setVisibility(View.GONE);
                                else
                                    h.mLocation.setText(e.getLocationTitle());

                                h.mEventContainer.setVisibility(View.VISIBLE);
                                h.mLoadingContainer.setVisibility(View.GONE);

                                h.mEventContainer.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent i = new Intent(mActivity, EventDetailActivity.class);
                                        i.putExtra("EventID", e.getID());
                                        mActivity.startActivity(i);
                                    }
                                });

                            }


                        }

                    });


                } catch (JSONException e) {
                    h.mLoadingContainer.setVisibility(View.GONE);

                }

            }


        }

    }

    @Override
    public int getItemCount() {
        return 2 + mGroup.mComments.size();
    }

    class DiscussionHolder extends RecyclerView.ViewHolder {
        public CardView mContainer;
        public TextView mMessages;
        public TextView mJoin;

        public DiscussionHolder(View itemView) {
            super(itemView);

            mContainer = (CardView) itemView.findViewById(R.id.container);
            mMessages = (TextView) itemView.findViewById(R.id.messages);
            mJoin = (TextView) itemView.findViewById(R.id.join);


            mContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int id = new UserData(mActivity).ID;
                    String path = String.format("groups/%d/chat/messages", mGroup.mID);
                    String s = String.format("users/%d/chats/group%d", id, mGroup.mID);
                    DatabaseReference db = FirebaseDatabase.getInstance().getReference().child(s);

                    db.setValue(path);

                    Intent i = new Intent(mActivity, ChatActivity.class);
                    i.putExtra("Path", path);
                    mActivity.startActivity(i);
                }
            });


        }
    }

    class EventHolder extends RecyclerView.ViewHolder {
        RelativeLayout mLocationContainer;
        RelativeLayout mLoadingContainer;
        RelativeLayout mEventContainer;
        RelativeLayout mTitlebar;
        TextView mLocation;
        TextView mMonth;
        TextView mTitle;
        TextView mTime;
        TextView mHost;
        ImageView mPic;
        TextView mDay;


        public EventHolder(View itemView) {
            super(itemView);

            mLocationContainer = (RelativeLayout) itemView.findViewById(R.id.location_layout);
            mLoadingContainer = (RelativeLayout) itemView.findViewById(R.id.loading);
            mEventContainer = (RelativeLayout) itemView.findViewById(R.id.content);
            mTitlebar = (RelativeLayout) itemView.findViewById(R.id.title_bar);
            mLocation = (TextView) itemView.findViewById(R.id.eventLocation);
            mTitle = (TextView) itemView.findViewById(R.id.eventTitle);
            mTime = (TextView) itemView.findViewById(R.id.eventTime);
            mHost = (TextView) itemView.findViewById(R.id.hostName);
            mPic = (ImageView) itemView.findViewById(R.id.picture);
            mMonth = (TextView) itemView.findViewById(R.id.month);
            mDay = (TextView) itemView.findViewById(R.id.day);

        }

        public void loadUser(int userID) {

            final User u = new User(userID);
            u.load(mActivity, new User.OnLoadListener() {
                @Override
                public void update() {
                    u.loadImage(mActivity, mPic);
                }
            });

        }
    }

    public class CommentHolder extends RecyclerView.ViewHolder {
        ImageView mPic;
        TextView mAuthor;
        TextView mComment;
        RelativeLayout mContainer;

        public CommentHolder(View itemView) {
            super(itemView);

            mPic = (ImageView) itemView.findViewById(R.id.image);
            mAuthor = (TextView) itemView.findViewById(R.id.txt_entity_title);
            mComment = (TextView) itemView.findViewById(R.id.txt_entity_detail);
            mContainer = (RelativeLayout) itemView.findViewById(R.id.layout_info);

        }

        public void loadUser(int userID) {

            final User u = new User(userID);
            u.load(mActivity, new User.OnLoadListener() {
                @Override
                public void update() {
                    u.loadImage(mActivity, mPic);
                }
            });

        }


    }

    public void showMembers() {
        final ArrayList<Entity> entityFeed = mGroup.mMembers;

        SearchEntityAdapter adapter = new SearchEntityAdapter(entityFeed, mActivity);

        View view = View.inflate(mActivity, R.layout.dialog_search_entity, null);
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.view);
        SearchView searchView = (SearchView) view.findViewById(R.id.search);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<Entity> newFeed = new ArrayList<>();

                for (Entity e : entityFeed) {
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
        builder.setTitle("Members");
        builder.setView(view);
        builder.setNegativeButton("Cancel", null);
        builder.create().show();


    }

    public void notifyNewImage(Bitmap b) {
        mHolder.updatePhoto(b);

    }

    public void notifyReloadImage(String s) {
        mGroup.setPic_ref(s);
        mHolder.mGroupPic.setImageBitmap(null);
        mGroup.loadImage(mActivity, mHolder.mGroupPic);

    }

    public class MainHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView[] mMembers = new ImageView[5];
        LinearLayout mMembersList;
        RelativeLayout mAction1;
        RelativeLayout mAction2;
        RelativeLayout mAction3;
        RelativeLayout mAction4;
        ImageView mGroupPic;
        TextView mEditPic;
        ImageView mImage1;
        ImageView mImage2;
        ImageView mImage3;
        ImageView mImage4;
        TextView mText1;
        TextView mText2;
        TextView mText3;
        TextView mText4;
        EditText mTitle;
        Bitmap mNewPic;
        EditText mBio;

        public MainHolder(View itemView) {
            super(itemView);

            mMembersList = (LinearLayout) itemView.findViewById(R.id.members);
            mAction1 = (RelativeLayout) itemView.findViewById(R.id.action1);
            mAction2 = (RelativeLayout) itemView.findViewById(R.id.action2);
            mAction3 = (RelativeLayout) itemView.findViewById(R.id.action3);
            mAction4 = (RelativeLayout) itemView.findViewById(R.id.action4);
            mMembers[0] = (ImageView) itemView.findViewById(R.id.member1);
            mMembers[1] = (ImageView) itemView.findViewById(R.id.member2);
            mMembers[2] = (ImageView) itemView.findViewById(R.id.member3);
            mMembers[3] = (ImageView) itemView.findViewById(R.id.member4);
            mMembers[4] = (ImageView) itemView.findViewById(R.id.member5);
            mEditPic = (TextView) itemView.findViewById(R.id.edit_photo);
            mGroupPic = (ImageView) itemView.findViewById(R.id.image);
            mImage1 = (ImageView) itemView.findViewById(R.id.img1);
            mImage2 = (ImageView) itemView.findViewById(R.id.img2);
            mImage3 = (ImageView) itemView.findViewById(R.id.img3);
            mImage4 = (ImageView) itemView.findViewById(R.id.img4);
            mText1 = (TextView) itemView.findViewById(R.id.text1);
            mText2 = (TextView) itemView.findViewById(R.id.text2);
            mText3 = (TextView) itemView.findViewById(R.id.text3);
            mText4 = (TextView) itemView.findViewById(R.id.text4);
            mTitle = (EditText) itemView.findViewById(R.id.txt_name);
            mBio = (EditText) itemView.findViewById(R.id.txt_bio);
            mBio = (EditText) itemView.findViewById(R.id.txt_bio);

            for (ImageView i : mMembers) {
                i.setImageBitmap(null);
            }

            populateHolder();

        }

        public void populateHolder() {
            int members = (mGroup.mMembers.size() < 5) ? mGroup.mMembers.size() : 5;

            for (int i = 0; i < members; i++) {
                mGroup.mMembers.get(i).loadImage(mActivity, mMembers[i]);

            }

            loadToolbar();

        }

        public void updatePhoto(Bitmap b) {
            mGroupPic.setImageBitmap(b);
            mNewPic = b;
        }


        public void loadToolbar() {

            resetToolbar();
            Group.Status s = mGroup.getmStatus();

            switch (s) {

                case UNKNOWN:

                    break;
                case GUEST:

                    if (mGroup.isPublic()) {
                        mImage1.setImageResource(R.drawable.ic_add_black_24dp);
                        mText1.setText("Join\nGroup");

                        mAction1.setOnClickListener(this);

                    } else {

                        mText1.setText("This group is invite only");

                    }

                    mAction2.setVisibility(View.GONE);
                    mAction3.setVisibility(View.GONE);
                    mAction4.setVisibility(View.GONE);

                    break;
                case INVITE:

                    mImage2.setImageResource(R.drawable.ic_done_black_24dp);
                    mText2.setText("Accept\nInvite");

                    mImage3.setImageResource(R.drawable.ic_clear_black_24dp);
                    mText3.setText("Reject\nInvite");

                    mAction2.setOnClickListener(this);
                    mAction3.setOnClickListener(this);

                    break;
                case MEMBER:

                    mImage1.setImageResource(R.drawable.ic_done_black_24dp);
                    mText1.setText("You're a\nmember!");

                    mImage2.setImageResource(R.drawable.ic_person_add_black_24dp);
                    mText2.setText("Invite\nFriends");

                    mAction3.setVisibility(View.GONE);

                    mImage4.setImageResource(R.drawable.ic_more_vert_black_24dp);
                    mText4.setText("More\nActions");

                    mAction4.setOnClickListener(this);
                    setInviteFriendAction(mAction2);

                    break;
                case ADMIN:

                    mImage1.setImageResource(R.drawable.ic_done_black_24dp);
                    mText1.setText("You're an\nadmin!");

                    mImage2.setImageResource(R.drawable.ic_person_add_black_24dp);
                    mText2.setText("Invite\nFriends");

                    mImage3.setImageResource(R.drawable.ic_mode_edit_black_24dp);
                    mText3.setText("Edit\nGroup");

                    mImage4.setImageResource(R.drawable.ic_more_vert_black_24dp);
                    mText4.setText("More\nActions");

                    setInviteFriendAction(mAction2);
                    mAction3.setOnClickListener(this);
                    mAction4.setOnClickListener(this);

                    break;
                case OWNER:
                    mImage1.setImageResource(R.drawable.ic_done_black_24dp);
                    mText1.setText("You're the\nowner!");

                    mImage2.setImageResource(R.drawable.ic_person_add_black_24dp);
                    mText2.setText("Invite\nFriends");

                    mImage3.setImageResource(R.drawable.ic_mode_edit_black_24dp);
                    mText3.setText("Edit\nGroup");

                    mImage4.setImageResource(R.drawable.ic_more_vert_black_24dp);
                    mText4.setText("More\nActions");

                    setInviteFriendAction(mAction2);
                    mAction3.setOnClickListener(this);
                    mAction4.setOnClickListener(this);

                    break;
            }

        }


        public void setEditing(boolean b) {

            mTitle.setFocusable(b);
            mTitle.setFocusableInTouchMode(b);

            mBio.setFocusable(b);
            mBio.setFocusableInTouchMode(b);

            mTitle.getBackground()
                    .setColorFilter(b ? Color.BLACK : Color.WHITE, PorterDuff.Mode.SRC_ATOP);
            mBio.getBackground()
                    .setColorFilter(b ? Color.BLACK : Color.WHITE, PorterDuff.Mode.SRC_ATOP);

            mEditPic.setAlpha(b ? 1 : 0);

        }


        public void setInviteFriendAction(View v) {

            try {
                JSONObject j = new JSONObject();

                j.put("entities", "Friends");
                j.put("invite_id", mGroup.mID);
                j.put("mode", "U2GFG");

                (new IntentDecorator(mActivity, InviteActivity.class)).fillJSON(j).decorate(v);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        public void resetToolbar() {

            mGroupPic.setOnClickListener(null);

            mAction1.setVisibility(View.VISIBLE);
            mAction2.setVisibility(View.VISIBLE);
            mAction3.setVisibility(View.VISIBLE);
            mAction4.setVisibility(View.VISIBLE);

            mAction1.setOnClickListener(null);
            mAction2.setOnClickListener(null);
            mAction3.setOnClickListener(null);
            mAction4.setOnClickListener(null);

            mImage1.setImageBitmap(null);
            mImage2.setImageBitmap(null);
            mImage3.setImageBitmap(null);
            mImage4.setImageBitmap(null);

            mText1.setText("");
            mText2.setText("");
            mText3.setText("");
            mText4.setText("");

        }

        public void saveChanges(final ProgressDialog dialog, String s) {

            String token = (new UserData(mActivity)).ShallonCreamerIsATwat;


            Calls.editGroup(mGroup.mID, mGroup.getmTitle(), mGroup.getmBio(),
                    mGroup.isPublic(), mGroup.isHidden(), s, token,
                    new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers,
                                              JSONObject response) {

                            loadToolbar();
                            setEditing(false);

                            dialog.hide();

                        }

                    });

        }

        @Override
        public void onClick(View view) {

            if (view.equals(mAction1)) {

                UserData d = new UserData(mActivity);

                final ProgressDialog dialog = ProgressDialog.show(mActivity, "",
                        "Joining group. Please wait...",
                        true);

                Calls.joinGroup(d.ID, mGroup.mID, d,
                        new JsonHttpResponseHandler() {


                            @Override
                            public void onSuccess(int statusCode, Header[] headers,
                                                  JSONObject response) {

                                mGroup.loadGroup(mActivity, new Group.OnLoadListener() {
                                    @Override
                                    public void OnUpdate() {

                                        populateHolder();

                                    }
                                });

                                dialog.hide();

                            }

                        });

            } else if (view.equals(mAction2)) {

                UserData d = new UserData(mActivity);
                int ID = d.ID;

                final ProgressDialog dialog = ProgressDialog.show(mActivity, "",
                        "Joining group. Please wait...",
                        true);

                Calls.joinGroup(ID, mGroup.getGroupID(), d, new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                        mGroup.loadGroup(mActivity, new Group.OnLoadListener() {
                            @Override
                            public void OnUpdate() {

                                dialog.hide();
                                notifyDataSetChanged();

                            }
                        });

                    }
                });

            } else if (view.equals(mAction3)) {

                if (mGroup.getmStatus() == Group.Status.INVITE) {

                    final ProgressDialog dialog = ProgressDialog.show(mActivity, "",
                            "Declining invite. Please wait...",
                            true);

                    Calls.leaveGroup(mGroup.getGroupID(),
                            new UserData(mActivity).ShallonCreamerIsATwat,
                            new JsonHttpResponseHandler() {

                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    mGroup.loadGroup(mActivity, new Group.OnLoadListener() {
                                        @Override
                                        public void OnUpdate() {

                                            dialog.hide();
                                            notifyDataSetChanged();

                                        }
                                    });

                                }
                            });

                } else {
                    setEditing(true);
                    resetToolbar();

                    mGroupPic.setOnClickListener(this);

                    mImage2.setImageResource(R.drawable.ic_done_black_24dp);
                    mText2.setText("Save\nChanges");

                    mImage3.setImageResource(R.drawable.ic_clear_black_24dp);
                    mText3.setText("Cancel");


                    mAction2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            final ProgressDialog dialog = ProgressDialog.show(mActivity, "",
                                    "Loading. Please wait...",
                                    true);

                            mGroup.setmTitle(mTitle.getText().toString());
                            mGroup.setmBio(mBio.getText().toString());

                            if (mNewPic != null) {

                                long millis = System.currentTimeMillis();
                                final String name = "group" + mGroup.mID + "-" + millis;

                                Calls.uploadImage(mNewPic, mActivity,
                                        name,
                                        new Calls.UploadImage
                                                .onFinished() {
                                            @Override
                                            public void onFinished() {

                                                String s = "https://let.blob.core.windows" +
                                                        ".net/mycontainer/" + name;

                                                saveChanges(dialog, s);

                                            }

                                        });

                            } else {
                                saveChanges(dialog, mGroup.getPic_ref());
                            }


                        }

                    });

                    mAction3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            loadToolbar();
                            setEditing(false);

                            mTitle.setText(mGroup.getmTitle());
                            mBio.setText(mGroup.getmBio());

                            mGroup.loadImage(mActivity, mGroupPic);

                        }
                    });

                }


            } else if (view.equals(mAction4)) {

                MoreActions a = new MoreActions(mGroup.getmStatus());
                a.show(mActivity.getFragmentManager(), "NO");

            } else if (view.equals(mGroupPic)) {

                int permissionCheck = ContextCompat
                        .checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                } else {

                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    mActivity.startActivityForResult(photoPickerIntent, 0);

                }

            }

        }
    }

    @SuppressLint("ValidFragment")
    public class MoreActions extends DialogFragment {
        CharSequence[] mActions;

        CharSequence[] memberActions = {"Leave Group"};
        CharSequence[] adminActions = {"Create Group Event", "Invite group to Event", "Add Admin", "Remove Admin", "Remove Member",
                "Leave Group"};
        CharSequence[] ownerActions = {"Create Group Event", "Invite group to Event", "Add Admin", "Remove Admin", "Remove Member",
                "Transfer Ownership", "Leave Group", "Delete Group"};


        public MoreActions(Group.Status s) {

            switch (s) {
                case MEMBER:
                    mActions = memberActions;
                    break;
                case ADMIN:
                    mActions = adminActions;
                    break;
                case OWNER:
                    mActions = ownerActions;
                    break;
                default:
                    mActions = new String[0];
            }

        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final CharSequence[] list;

            list = mActions;

            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setTitle("Select Action")
                    .setItems(list, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            runAction(list[which]);

                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }


        public void runAction(CharSequence s) {

            final UserData d = new UserData(mActivity);
            switch (String.valueOf(s)) {
                case "Create Group Event":

                    final Calendar mCalendar = Calendar.getInstance();

                    TimePickerDialog.Builder calBuilder =
                            new TimePickerDialog.Builder(mCalendar.get(Calendar.HOUR_OF_DAY),
                                    mCalendar.get(Calendar.MINUTE)) {

                                /**
                                 * When the user confirm's the start time of the event, update the HashMap
                                 * values and the EditText
                                 * @param fragment the parameter from when the dialog closes containing the
                                 *                 time's information
                                 */
                                //GEFN
                                @SuppressLint("SimpleDateFormat")
                                @Override
                                public void onPositiveActionClicked(com.rey.material.app.DialogFragment fragment) {
                                    TimePickerDialog dialog = (TimePickerDialog) fragment.getDialog();

                                    mCalendar.set(Calendar.HOUR_OF_DAY, dialog.getHour());
                                    mCalendar.set(Calendar.MINUTE, dialog.getMinute());

                                    Calendar current = Calendar.getInstance();

                                    if (mCalendar.before(current)) {
                                        mCalendar.add(Calendar.DAY_OF_YEAR, 1);

                                    }

                                    Calendar end = (Calendar) mCalendar.clone();
                                    end.add(Calendar.HOUR_OF_DAY, 2);

                                    String token = new UserData(mActivity).ShallonCreamerIsATwat;
                                    HashMap<String, String> params = new HashMap<>();

                                    params.put("Category", "9");
                                    params.put("Latitude", "0.0");
                                    params.put("Longitude", "0.0");
                                    params.put("Map Title", "{null}");
                                    params.put("Title", "{null}");
                                    params.put("Description", " ");
                                    params.put("End Time", end.getTimeInMillis() + "");
                                    params.put("Start Time", mCalendar.getTimeInMillis() + "");
                                    params.put("Hidden", "false");


                                    Calls.createEvent(params, token, new JsonHttpResponseHandler() {

                                        @Override
                                        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                                            try {

                                                int ID = response.getJSONObject(0).getInt("Event_ID");

                                                Calls.inviteGroupToEvent(ID, mGroup.getGroupID(),
                                                        new UserData(mActivity), new JsonHttpResponseHandler());


                                                Calls.addGroupComment(mGroup.getGroupID(),
                                                        String.format("{\"event id\" : %d}", ID),
                                                        new UserData(mActivity),
                                                        new JsonHttpResponseHandler() {

                                                            @Override
                                                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                                                mGroup.loadGroup(mActivity, new Group.OnLoadListener() {
                                                                    @Override
                                                                    public void OnUpdate() {

                                                                        notifyDataSetChanged();

                                                                    }
                                                                });
                                                            }
                                                        });


                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }


                                        }
                                    });


                                    super.onPositiveActionClicked(fragment);
                                }

                                @Override
                                public void onNegativeActionClicked(com.rey.material.app.DialogFragment fragment) {
                                    super.onNegativeActionClicked(fragment);
                                }
                            };

                    //Setting the bottom buttons texts' to "OK" and "CANCEL"
                    calBuilder.positiveAction("OK")
                            .negativeAction("CANCEL");

                    //Showing the dialog over the current Activity
                    com.rey.material.app.DialogFragment fragment = com.rey.material.app.DialogFragment.newInstance(calBuilder);
                    fragment.show(mActivity.getSupportFragmentManager(), "Select Time");

                    break;
                case "Leave Group":

                    AlertDialog.Builder b = new AlertDialog.Builder(mActivity);
                    if (mGroup.getmStatus() != Group.Status.OWNER) {
                        b.setTitle("Are you sure?").setMessage("Are you sure you want to leave this group?").setPositiveButton(

                                "Leave", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        final ProgressDialog dialog = ProgressDialog.show(mActivity, "",
                                                "Leaving group. Please wait...",
                                                true);

                                        Calls.leaveGroup(mGroup.mID, (new UserData(mActivity)).ShallonCreamerIsATwat, new JsonHttpResponseHandler() {


                                            @Override
                                            public void onSuccess(int statusCode, Header[] headers,
                                                                  JSONObject response) {

                                                mGroup.loadGroup(mActivity,
                                                        new Group.OnLoadListener() {
                                                            @Override
                                                            public void OnUpdate() {

                                                                mHolder.populateHolder();
                                                                dialog.hide();

                                                            }
                                                        });

                                            }
                                        });

                                    }
                                }).setNegativeButton("Cancel", null).create().show();

                    } else {
                        b.setTitle("Action Denied")
                                .setMessage("You are currently the owner, you must either transfer ownership or delete the group")
                                .setPositiveButton("Okay", null).create().show();

                    }

                    break;
                case "Invite group to Event":

                    Intent eventIntent = new Intent(mActivity, InviteActivity.class);
                    eventIntent.putExtra("invite_id", mGroup.mID);
                    eventIntent.putExtra("entities", "Events");
                    eventIntent.putExtra("mode", "G2EFG");
                    mActivity.startActivity(eventIntent);

                    break;

                case "Add Admin":

                    final ArrayList<Entity> members = new ArrayList<>();

                    for (Entity e : mGroup.mMembers) {
                        if (!mGroup.mAdmins.contains(e))
                            members.add(e);

                    }

                    String[] t = new String[members.size()];
                    final boolean[] admins = new boolean[members.size()];

                    for (int i = 0; i < t.length; i++) {
                        t[i] = members.get(i).mText;
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                    builder.setTitle("Add Admins");
                    builder.setMultiChoiceItems(t, null,
                            new DialogInterface.OnMultiChoiceClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface,
                                                    int i, boolean b) {

                                    admins[i] = b;


                                }
                            });


                    builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int j) {

                            String token = (new UserData(mActivity)).ShallonCreamerIsATwat;

                            for (int i = 0; i < admins.length; i++) {

                                if (admins[i]) {
                                    mGroup.mAdmins.add(members.get(i));
                                    Calls.addAdmin(members.get(i).mID, mGroup.mID, token, new JsonHttpResponseHandler() {

                                    });
                                }

                            }

                        }
                    });

                    builder.setNegativeButton("Cancel", null);
                    builder.create().show();

                    break;

                case "Remove Admin":

                    String[] a = new String[mGroup.mAdmins.size()];
                    final boolean[] removeList = new boolean[a.length];

                    for (int i = 0; i < a.length; i++) {
                        a[i] = mGroup.mAdmins.get(i).mText;

                    }


                    AlertDialog.Builder build = new AlertDialog.Builder(mActivity);
                    build.setTitle("Remove Admins");
                    build.setMultiChoiceItems(a, null,
                            new DialogInterface.OnMultiChoiceClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface,
                                                    int i, boolean b) {

                                    removeList[i] = b;


                                }
                            });


                    build.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int j) {

                            String token = (new UserData(mActivity)).ShallonCreamerIsATwat;

                            ArrayList<Entity> removeEntities = new ArrayList<>();
                            for (int i = 0; i < removeList.length; i++) {
                                if (removeList[i])
                                    removeEntities.add(mGroup.mAdmins.get(i));
                            }

                            for (Iterator<Entity> iterator = mGroup.mAdmins.iterator(); iterator.hasNext(); ) {
                                Entity e = iterator.next();
                                if (removeEntities.contains(e)) {
                                    Calls.removeAdmin(e.mID, mGroup.mID, token, new JsonHttpResponseHandler() {
                                    });
                                    iterator.remove();

                                }

                            }
                        }
                    });

                    build.setNegativeButton("Cancel", null);
                    build.create().show();

                    break;

                case "Transfer Ownership":

                    String[] newOwners = new String[mGroup.mAdmins.size()];

                    final Entity owner = new Entity(0, "", Entity.EntityType.USER);

                    for (int i = 0; i < newOwners.length; i++) {
                        newOwners[i] = mGroup.mAdmins.get(i).mText;
                    }


                    AlertDialog.Builder buildr = new AlertDialog.Builder(mActivity);
                    buildr.setTitle("Transfer Ownership");
                    buildr.setSingleChoiceItems(newOwners, 0,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface,
                                                    int i) {

                                    owner.mID = mGroup.mAdmins.get(i).mID;


                                }
                            });


                    buildr.setPositiveButton("Transfer", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int j) {

                            mGroup.setmStatus(Group.Status.ADMIN);
                            String token = (new UserData(mActivity)).ShallonCreamerIsATwat;
                            Calls.transferOwner(mGroup.mID, owner.mID, token, new JsonHttpResponseHandler() {

                            });

                        }
                    });

                    buildr.setNegativeButton("Cancel", null);
                    buildr.create().show();


                    break;

                case "Delete Group":

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(mActivity);
                    builder1.setTitle("Are you sure?");
                    builder1.setMessage("This group will be gone forever (a really long time!)");
                    builder1.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            Calls.deleteGroup(mGroup.mID, d.ShallonCreamerIsATwat, new JsonHttpResponseHandler() {


                                @Override
                                public void onSuccess(int statusCode, Header[] headers,
                                                      JSONObject response) {
                                    mActivity.finish();
                                }
                            });

                        }
                    });
                    builder1.setNegativeButton("Cancel", null);
                    builder1.create().show();


                    break;

                case "Remove Member":
                    String[] removers = new String[mGroup.mMembers.size()];

                    final Entity remove = new Entity(0, "", Entity.EntityType.USER);

                    for (int i = 0; i < removers.length; i++) {
                        removers[i] = mGroup.mMembers.get(i).mText;
                    }


                    AlertDialog.Builder buildr1 = new AlertDialog.Builder(mActivity);
                    buildr1.setTitle("Remove Member");
                    buildr1.setSingleChoiceItems(removers, 0,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface,
                                                    int i) {

                                    remove.mID = mGroup.mAdmins.get(i).mID;


                                }
                            });


                    buildr1.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int j) {

                            mGroup.setmStatus(Group.Status.ADMIN);
                            String token = (new UserData(mActivity)).ShallonCreamerIsATwat;
                            Calls.removeUserFromGroup(remove.mID, mGroup.mID, token, new JsonHttpResponseHandler() {


                            });


                        }
                    });

                    buildr1.setNegativeButton("Cancel", null);
                    buildr1.create().show();


                    break;


            }


        }


    }


}

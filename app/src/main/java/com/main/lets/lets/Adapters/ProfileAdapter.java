package com.main.lets.lets.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.main.lets.lets.Activities.EventDetailActivity;
import com.main.lets.lets.Activities.MainActivity;
import com.main.lets.lets.LetsAPI.Entity;
import com.main.lets.lets.LetsAPI.User;
import com.main.lets.lets.R;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.quickAdapter.easyRegularAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Joe on 12/13/2015.
 */
public class ProfileAdapter extends easyRegularAdapter<String, UltimateRecyclerviewViewHolder> {
    public enum Viewing {EVENTS, GROUPS, FRIENDS}

    private Viewing active = Viewing.FRIENDS;
    private static final int DETAIL_CODE = 1;
    public ArrayList<String> mEntities;
    public ArrayList<String> mFriends;
    public ArrayList<String> mEvents;
    public ArrayList<String> mGroups;
    public ViewHolder mViewHolder;
    private Activity mActivity;
    private User mUser;
    boolean b = true;

    private ImageView mPicture;


    public ProfileAdapter(Activity context, ArrayList<String> a) throws JSONException {
        super(a);
        mEntities = a;
        mActivity = context;
        mEvents = new ArrayList<>();
        mGroups = new ArrayList<>();
        mFriends = new ArrayList<>();

    }

    public ProfileAdapter(Activity context, ArrayList<String> a, Viewing e) throws JSONException {
        super(a);
        active = e;
        mEntities = a;
        mActivity = context;
        mGroups = new ArrayList<>();
        mEvents = new ArrayList<>();
        mFriends = new ArrayList<>();

    }


    @Override
    protected int getNormalLayoutResId() {
        return R.layout.row_profile;
    }

    @Override
    protected UltimateRecyclerviewViewHolder newViewHolder(View view) {

        return newViewHolder(view);
    }

    @Override
    protected void withBindHolder(UltimateRecyclerviewViewHolder holder, String data, int position) {

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public UltimateRecyclerviewViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        try {
            mUser = new User(new JSONObject(mEntities.get(0)));

            if (viewType == 0) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_profile, parent, false);
                mViewHolder = new ViewHolder(view);


                mViewHolder.name.setText(mUser.getName());
                mViewHolder.bio.setText(mUser.getBio());
                mViewHolder.score.setText("Score: " + mUser.getScore());

                String[] Categories = {"Party", "Eating/ Drinking", "Study", "TV/ Movies",
                        "Video games", "Sports", "Music", "Relax", "Other"};
                char[] filter = new Integer(mUser.getInterests()).toString().toCharArray();
                String interestString = "";

                for (int i = 0; i < filter.length; i++) {
                    if (filter[i] == '1') {
                        if (interestString.length() < 1) {
                            interestString += Categories[i];

                        } else {
                            interestString += ", " + Categories[i];

                        }

                    }

                }

                mViewHolder.interests.setText(interestString);

                return mViewHolder;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_entity_with_space, parent, false);
        EntityHolder e = new EntityHolder(view);
        e.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    switch (active) {
                        case FRIENDS:

                            break;

                        case EVENTS:
                            loadEvent(new Entity(new JSONObject(mEntities.get(viewType))).mID);

                            break;

                    }

                } catch (JSONException e1) {
                    e1.printStackTrace();
                }

            }

        });

        try {
            e.mTitle.setText(new Entity(new JSONObject(mEntities.get(viewType))).mText);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        return e;

    }

    public void loadEvent(int i) {
        RequestParams params = new RequestParams();
        params.put("event_id", i);
        Entity.post("event/getEventById", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, org.json.JSONObject response) {

                try {
                    Intent intent = new Intent(mActivity, EventDetailActivity.class);
                    intent.putExtra("JSON", response.getJSONArray("Event_info").getJSONObject(0)
                            .toString());
                    mActivity.startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                  org.json.JSONArray errorResponse) {
                Log.e("Aync Test Failure", errorResponse.toString());
            }

        });

    }

    public void setmPicture(Bitmap b) {
        mPicture.setImageBitmap(b);

    }

    public void insert(String s) {
        mEntities.add(s);

    }

    public void switchActive(Viewing e) {
        if (active == e || !b)
            return;

        b = false;
        switch (active) {
            case FRIENDS:
                for (String ignored : mFriends)
                    removeLast();

                break;
            case EVENTS:
                for (String ignored : mEvents)
                    removeLast();

                break;

            case GROUPS:
                for (String ignored : mGroups)
                    removeLast();

                break;
        }

        switch (e) {
            case FRIENDS:
                for (String s : mFriends)
                    insert(s);

                break;
            case EVENTS:
                for (String s : mEvents)
                    insert(s);

                break;

            case GROUPS:
                for (String s : mGroups)
                    insert(s);

                break;
        }

        active = e;
        b = true;

    }


    public class ViewHolder extends UltimateRecyclerviewViewHolder {
        public ImageView mPicture;
        public TextView name;
        public TextView bio;
        public TextView friends;
        public TextView score;
        public TextView interests;
        public Button bEvents;
        public Button bFriends;
        public Button bGroups;

        public ViewHolder(View itemView) {
            super(itemView);
            if (mEntities.size() > 0) {
                name = (TextView) itemView.findViewById(R.id.txt_name);
                bio = (TextView) itemView.findViewById(R.id.txt_bio);
                setFriends((TextView) itemView.findViewById(R.id.txt_friends));
                score = (TextView) itemView.findViewById(R.id.txt_score);
                interests = (TextView) itemView.findViewById(R.id.txt_interests);
                bEvents = (Button) itemView.findViewById(R.id.btn_events);
                bFriends = (Button) itemView.findViewById(R.id.btn_friends);
                bGroups = (Button) itemView.findViewById(R.id.btn_groups);
                mPicture = (ImageView) itemView.findViewById(R.id.img_profile);

                try {
                    URL url = new URL(mUser.getPropic());
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    mPicture.setImageBitmap(BitmapFactory.decodeStream(connection.getInputStream()));
                } catch (IOException e) {
                    e.printStackTrace();
                }


                mPicture.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        String[] items = {"Update Picture"};
                        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                        builder.setTitle("Edit Profile Picture");
                        builder.setItems(items, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                Intent pickIntent = new Intent();
                                pickIntent.setType("image/*");
                                pickIntent.setAction(Intent.ACTION_GET_CONTENT);

                                Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                                String pickTitle = "Select or take a new Picture"; // Or get from strings.xml
                                Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
                                chooserIntent.putExtra
                                        (
                                                Intent.EXTRA_INITIAL_INTENTS,
                                                new Intent[]{takePhotoIntent}
                                        );

                                mActivity.startActivityForResult(chooserIntent, MainActivity.SELECT_PICTURE);

                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();

                        return false;
                    }
                });


                bEvents.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        switchActive(Viewing.EVENTS);

                    }
                });

                bFriends.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        switchActive(Viewing.FRIENDS);

                    }
                });


                bGroups.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        switchActive(Viewing.GROUPS);

                    }
                });

            }
        }

        public TextView getFriends() {
            return friends;
        }

        public void setFriends(TextView friends) {
            this.friends = friends;
        }
    }

    protected class EntityHolder extends UltimateRecyclerviewViewHolder {
        RelativeLayout mLayout;
        TextView mTitle;
        Entity mEntity;

        public EntityHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.txt_entity_title);
            mLayout = (RelativeLayout) itemView.findViewById(R.id.layout_info);

        }
    }


}

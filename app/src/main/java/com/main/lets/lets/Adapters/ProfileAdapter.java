package com.main.lets.lets.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.main.lets.lets.Activities.MainActivity;
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

/**
 * Created by Joe on 12/13/2015.
 */
public class ProfileAdapter extends easyRegularAdapter<String, UltimateRecyclerviewViewHolder> {
    private enum Entity {Events, Groups, Friends;}

    private Entity active = Entity.Friends;
    private ArrayList<String> mEntities;
    public ArrayList<String> mFriends;
    public ArrayList<String> mEvents;
    public ArrayList<String> mGroups;
    private Activity mActivity;
    private User mUser;

    private ImageView mPicture;


    public ProfileAdapter(Activity context, ArrayList<String> a) throws JSONException {
        super(a);
        mEntities = a;
        this.mActivity = context;
        mEvents = new ArrayList<>();
        mGroups = new ArrayList<>();
        mFriends = new ArrayList<>();

    }

    public ProfileAdapter(Activity context, ArrayList<String> a, Entity e) throws JSONException {
        mFriends = new ArrayList<>();
        mEvents = new ArrayList<>();
        mGroups = new ArrayList<>();
        mActivity = context;
        mEntities = a;
        active = e;

    }

    public void insert(String s) {
        mEntities.add(s);

    }

    public void removeAll(){
        mEntities.clear();
    }


    @Override
    protected int getNormalLayoutResId() {
        return R.layout.row_profile;
    }

    @Override
    protected UltimateRecyclerviewViewHolder newViewHolder(View view) {
        return null;
    }

    @Override
    protected void withBindHolder(UltimateRecyclerviewViewHolder holder, String data, int position) {

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public UltimateRecyclerviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_profile, parent, false);

            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_entity, parent, false);
            EntityHolder e = new EntityHolder(view);
            switch (active) {
                case Friends:

                    break;
                case Events:

                    break;
                case Groups:


                    break;

            }

            return e;
        }
    }

    public void setmPicture(Bitmap b) {
        mPicture.setImageBitmap(b);

    }

    protected class ViewHolder extends UltimateRecyclerviewViewHolder {
        ImageView mPicture;

        public ViewHolder(View itemView) {
            super(itemView);
            if (mEntities.size() > 0) {
                TextView name = (TextView) itemView.findViewById(R.id.txt_name);
                TextView bio = (TextView) itemView.findViewById(R.id.txt_bio);
                TextView friends = (TextView) itemView.findViewById(R.id.txt_friends);
                TextView score = (TextView) itemView.findViewById(R.id.txt_score);
                TextView interests = (TextView) itemView.findViewById(R.id.txt_interests);
                Button bEvents = (Button) itemView.findViewById(R.id.btn_events);
                Button bFriends = (Button) itemView.findViewById(R.id.btn_friends);
                Button bGroups = (Button) itemView.findViewById(R.id.btn_groups);
                mPicture = (ImageView) itemView.findViewById(R.id.img_profile);

                try {
                    mUser = new User(new JSONObject(mEntities.get(0)));

                    URL url = new URL(mUser.getPropic());
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    mPicture.setImageBitmap(BitmapFactory.decodeStream(connection.getInputStream()));
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                name.setText(mUser.getName());
                bio.setText(mUser.getBio());
                friends.setText(mUser.getFriends() + " Friends");
                score.setText("Score: " + mUser.getScore());

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

                interests.setText(interestString);


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
            }
        }

    }

    protected class EntityHolder extends UltimateRecyclerviewViewHolder {
        TextView mTitle;
        RelativeLayout mLayout;

        public EntityHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.txt_entity_title);
            mLayout = (RelativeLayout) itemView.findViewById(R.id.layout_info);

        }
    }


}

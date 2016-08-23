package com.main.lets.lets.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.main.lets.lets.Activities.EventDetailActivity;
import com.main.lets.lets.Activities.GroupDetailActivity;
import com.main.lets.lets.Activities.UserDetailActivity;
import com.main.lets.lets.Holders.PictureViewHolder;
import com.main.lets.lets.LetsAPI.Entity;
import com.main.lets.lets.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joe on 8/23/2016.
 * <p/>
 * Base class for activities showing entity feeds so less work has to be done across the app.
 */
public abstract class FeedAdapter extends RecyclerView.Adapter {
    public enum Active {USER, EVENT, GROUP, COMMENT}

    public ArrayList<String> mComments = new ArrayList<>();
    public ArrayList<String> mEvents = new ArrayList<>();
    public ArrayList<String> mGroups = new ArrayList<>();
    public ArrayList<String> mUsers = new ArrayList<>();
    public ArrayList<String> mList = new ArrayList<>();
    public AppCompatActivity mActivity;
    public TextView mComment;
    public TextView mEvent;
    public TextView mGroup;
    public TextView mUser;
    public Active mActive;

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        try {
            final Entity e = new Entity(new JSONObject(mList.get(position)));
            ((PictureViewHolder) holder).mText.setText(e.mText);

            if (e.mPic != null) {
                Picasso.with(mActivity).load(e.mPic).into(((PictureViewHolder) holder).mImage);

            } else if (e.mCategory != -1) {
                Picasso.with(mActivity).load(getImageResourceId(mActivity, e.mCategory))
                        .into(((PictureViewHolder) holder).mImage);

            }

            ((PictureViewHolder) holder).mLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mActive == FeedAdapter.Active.USER) {
                        Intent intent = new Intent(mActivity, UserDetailActivity.class);
                            intent.putExtra("UserID", e.mID);

                            mActivity.startActivity(intent);

                    } else if (mActive == Active.EVENT) {
                        Intent intent = new Intent(mActivity, EventDetailActivity.class);
                        intent.putExtra("EventID", e.mID);

                        mActivity.startActivity(intent);

                    } else if (mActive == Active.GROUP) {
                        Intent intent = new Intent(mActivity, GroupDetailActivity.class);
                        intent.putExtra("GroupID", (e.mID));
                        mActivity.startActivity(intent);
                    }


                }
            });

        } catch (JSONException e1) {
            e1.printStackTrace();
        }


    }

    public void setComments(TextView c) {
        mComment = c;
        mComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mActive = Active.COMMENT;

                setActiveButton(mComment);
                clearFeed();

                try {
                    for (String s : mComments) {
                        Entity comment = new Entity(new JSONObject(s));

                        for (String u : mUsers) {
                            Entity user = new Entity(new JSONObject(u));
                            if (comment.mID == user.mID) {

                                JSONObject j = new JSONObject();
                                j.put("user_id", user.mID);
                                j.put("user_name", user.mText);
                                j.put("text", user.mText + ":\n" + comment.mText);

                                addElement(j.toString());

                            }

                        }

                    }

                } catch (JSONException e1) {
                    e1.printStackTrace();
                }

            }
        });
    }

    public void setEvents(TextView e) {
        mEvent = e;
        mEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActive = Active.EVENT;

                setActiveButton(mEvent);
                clearFeed();

                for (String l : mEvents)
                    addElement(l);

            }
        });
    }

    public void setGroups(TextView g) {
        mGroup = g;
        mGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActive = Active.GROUP;

                setActiveButton(mGroup);
                clearFeed();

                for (String l : mGroups)
                    addElement(l);
            }
        });
    }

    public void setUsers(TextView u) {
        mUser = u;
        mUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActive = Active.USER;

                setActiveButton(mUser);
                clearFeed();


                for (String s : mUsers)
                    addElement(s);


            }
        });
    }

    public void addElement(String s) {
        mList.add(s);
        notifyItemInserted(mList.size() - 1);
        notifyDataSetChanged();
    }

    public void clearFeed() {
        int end = mList.size();

        for (int i = 1; i < end; i++) {
            mList.remove(1);
            notifyItemRemoved(1);
            notifyItemRangeChanged(1, mList.size());
        }

    }

    public void setActiveButton(TextView t) {
        t.setBackgroundColor(mActivity.getResources().getColor(R.color.colorPrimary));

        if (mComment != null && !t.equals(mComment))
            mComment.setBackgroundColor(mActivity.getResources().getColor(R.color.white));

        if (mEvent != null && !t.equals(mEvent))
            mEvent.setBackgroundColor(mActivity.getResources().getColor(R.color.white));

        if (mUser != null && !t.equals(mUser))
            mUser.setBackgroundColor(mActivity.getResources().getColor(R.color.white));

        if (mGroup != null && !t.equals(mGroup))
            mGroup.setBackgroundColor(mActivity.getResources().getColor(R.color.white));


    }

    public int getImageResourceId(Context context, int category) {
        return context.getResources().getIdentifier(("j" + Integer.toString(category))
                                                            .replaceAll("\\s+", "").toLowerCase(), "drawable", context.getPackageName());
    }

}

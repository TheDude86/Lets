package com.main.lets.lets.Holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.main.lets.lets.R;

/**
 * Created by Joe on 6/30/2016.
 */
public class UserDetailViewHolder extends RecyclerView.ViewHolder {
    public Button mFriendsButton;
    public Button mGroupsButton;
    public ImageButton mOptions;
    public Button mEventsButton;
    public TextView mInterests;
    public TextView mFriends;
    public TextView mScore;
    public TextView mName;
    public ImageView mPic;
    public TextView mBio;

    public UserDetailViewHolder(View itemView) {
        super(itemView);

        mInterests = (TextView) itemView.findViewById(R.id.txt_interests);
        mOptions = (ImageButton) itemView.findViewById(R.id.options);
        mFriendsButton = (Button) itemView.findViewById(R.id.friends);
        mFriends = (TextView) itemView.findViewById(R.id.txt_friends);
        mGroupsButton = (Button) itemView.findViewById(R.id.groups);
        mEventsButton = (Button) itemView.findViewById(R.id.events);
        mPic = (ImageView) itemView.findViewById(R.id.img_profile);
        mScore = (TextView) itemView.findViewById(R.id.txt_score);
        mName = (TextView) itemView.findViewById(R.id.txt_name);
        mBio = (TextView) itemView.findViewById(R.id.txt_bio);

    }
}

package com.main.lets.lets.Holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.main.lets.lets.R;

/**
 * Created by Joe on 6/30/2016.
 */
public class ProfileDetailViewHolder extends RecyclerView.ViewHolder {
    public TextView mInterests;
    public TextView mFriends;
    public TextView mScore;
    public TextView mName;
    public ImageView mPic;
    public TextView mBio;

    public ProfileDetailViewHolder(View itemView) {
        super(itemView);

        mInterests = (TextView) itemView.findViewById(R.id.txt_interests);
        mFriends = (TextView) itemView.findViewById(R.id.txt_friends);
        mPic = (ImageView) itemView.findViewById(R.id.img_profile);
        mScore = (TextView) itemView.findViewById(R.id.txt_score);
        mName = (TextView) itemView.findViewById(R.id.txt_name);
        mBio = (TextView) itemView.findViewById(R.id.txt_bio);

    }
}

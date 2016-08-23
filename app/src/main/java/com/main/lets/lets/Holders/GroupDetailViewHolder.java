package com.main.lets.lets.Holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.main.lets.lets.R;

/**
 * Created by jnovosel on 7/2/16.
 *
 * Loads all of the view from the group detail xml for the group detail adapter to fill with the
 * specific information.
 *
 */
public class GroupDetailViewHolder extends RecyclerView.ViewHolder {
    public LinearLayout mActionsList;
    public LinearLayout mMainLayout;
    public LinearLayout mActions;
    public ImageView mImage;
    public TextView mComments;
    public TextView mMembers;
    public EditText mName;
    public EditText mBio;

    public GroupDetailViewHolder(View itemView) {
        super(itemView);
        mBio = (EditText) itemView.findViewById(R.id.txt_bio);
        mImage = (ImageView) itemView.findViewById(R.id.image);
        mName = (EditText) itemView.findViewById(R.id.txt_name);
        mMembers = (TextView) itemView.findViewById(R.id.btn_users);
        mComments = (TextView) itemView.findViewById(R.id.btn_comments);
        mActions = (LinearLayout) itemView.findViewById(R.id.actions);
        mMainLayout = (LinearLayout) itemView.findViewById(R.id.main_layout);
        mActionsList = (LinearLayout) itemView.findViewById(R.id.actions_list);

    }
}

package com.main.lets.lets.Holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.main.lets.lets.R;

/**
 * Created by jnovosel on 7/2/16.
 */
public class GroupDetailViewHolder extends RecyclerView.ViewHolder {
    public LinearLayout mActionsList;
    public LinearLayout mActions;
    public ImageView mImage;
    public Button mComments;
    public Button mMembers;
    public TextView mName;
    public TextView mBio;

    public GroupDetailViewHolder(View itemView) {
        super(itemView);
        mBio = (TextView) itemView.findViewById(R.id.txt_bio);
        mImage = (ImageView) itemView.findViewById(R.id.image);
        mName = (TextView) itemView.findViewById(R.id.txt_name);
        mMembers = (Button) itemView.findViewById(R.id.btn_users);
        mComments = (Button) itemView.findViewById(R.id.btn_comments);
        mActions = (LinearLayout) itemView.findViewById(R.id.actions);
        mActionsList = (LinearLayout) itemView.findViewById(R.id.actions_list);

    }
}

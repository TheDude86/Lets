package com.main.lets.lets.Holders;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.main.lets.lets.LetsAPI.L;
import com.main.lets.lets.LetsAPI.User;
import com.main.lets.lets.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by jnovosel on 7/8/16.
 */
public class PictureViewHolder extends RecyclerView.ViewHolder {
    public RelativeLayout mLayout;
    public CircleImageView mImage;
    public TextView mDetail;
    public TextView mText;

    public PictureViewHolder(View itemView) {
        super(itemView);

        mLayout = (RelativeLayout) itemView.findViewById(R.id.layout_info);
        mText = (TextView) itemView.findViewById(R.id.txt_entity_title);
        mImage = (CircleImageView) itemView.findViewById(R.id.image);
        mDetail = (TextView) itemView.findViewById(R.id.txt_entity_detail);

    }

    public void loadUserImage(final AppCompatActivity mActivity, final int userID) {
        final User u = new User(userID);

        mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                L.println(PictureViewHolder.class, "ID:" + u.mID);
            }
        });

        u.load(mActivity, new User.OnLoadListener() {
            @Override
            public void update() {

                u.loadImage(mActivity, mImage);
                L.println(PictureViewHolder.class, "ID:" + u.mID);

            }
        });
    }

}

package com.main.lets.lets.Holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.main.lets.lets.R;

/**
 * Created by jnovosel on 7/8/16.
 */
public class PictureViewHolder extends RecyclerView.ViewHolder {
    public RelativeLayout mLayout;
    public ImageView mImage;
    public boolean clicked;
    public TextView mText;

    public PictureViewHolder(View itemView) {
        super(itemView);

        mLayout = (RelativeLayout) itemView.findViewById(R.id.layout_info);
        mText = (TextView) itemView.findViewById(R.id.txt_entity_title);
        mImage = (ImageView) itemView.findViewById(R.id.image);

    }
}

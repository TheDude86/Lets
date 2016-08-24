package com.main.lets.lets.Holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.main.lets.lets.R;

/**
 * Created by Joe on 6/26/2016.
 */
public class EntityViewHolder extends RecyclerView.ViewHolder {
    public RelativeLayout mLayout;
    public ImageView mImage;
    public TextView mDetail;
    public TextView mTitle;

    public EntityViewHolder(View itemView) {
        super(itemView);

        mLayout = (RelativeLayout) itemView.findViewById(R.id.layout_info);
        mDetail = (TextView) itemView.findViewById(R.id.txt_entity_detail);
        mTitle = (TextView) itemView.findViewById(R.id.txt_entity_title);

        try {
            mImage = (ImageView) itemView.findViewById(R.id.image);
        }catch (NullPointerException e){
            e.printStackTrace();
        }

    }
}

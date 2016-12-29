package com.main.lets.lets.Adapters;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.main.lets.lets.Holders.PictureViewHolder;
import com.main.lets.lets.LetsAPI.BitmapLoader;
import com.main.lets.lets.LetsAPI.Entity;
import com.main.lets.lets.LetsAPI.EntityOnClickListener;
import com.main.lets.lets.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by novosejr on 12/17/2016.
 */
public class SearchEntityAdapter extends RecyclerView.Adapter<SearchEntityAdapter.Holder> {
    ArrayList<Entity> mFeed;
    AppCompatActivity mActivity;

    public SearchEntityAdapter(ArrayList<Entity> e, AppCompatActivity a) {

        mFeed = e;
        mActivity = a;

    }

    @Override
    public SearchEntityAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_search_entity, parent,
                        false));
    }

    @Override
    public void onBindViewHolder(SearchEntityAdapter.Holder holder, int position) {

        Entity e = mFeed.get(position);
        EntityOnClickListener l = new EntityOnClickListener(e);

        holder.mTitle.setText(e.mText);
        holder.mDesc.setText(e.mDetail);

        if (e.mType == Entity.EntityType.EVENT) {
            Bitmap b = new BitmapLoader(mActivity, mActivity.getResources()
                    .getIdentifier("j" + e.mCategory, "drawable", mActivity.getPackageName()))
                    .decodeSampledBitmapFromResource(70, 70);

            holder.mImageView.setImageBitmap(b);

        } else {
            e.loadImage(mActivity, holder.mImageView);

        }

        if (e.mType == Entity.EntityType.EVENT)
            holder.mRelativeLayout.setOnClickListener(l.OnEventClicked(mActivity));
        else if (e.mType == Entity.EntityType.USER)
            holder.mRelativeLayout.setOnClickListener(l.OnUserClicked(mActivity));
        else
            holder.mRelativeLayout.setOnClickListener(l.OnGroupClicked(mActivity));


    }

    @Override
    public int getItemCount() {
        return mFeed.size();
    }

    public void addElement() {
        notifyItemInserted(mFeed.size() - 1);
    }

    public class Holder extends RecyclerView.ViewHolder {
        RelativeLayout mRelativeLayout;
        CircleImageView mImageView;
        TextView mTitle;
        TextView mDesc;


        public Holder(View itemView) {
            super(itemView);

            mRelativeLayout = (RelativeLayout) itemView.findViewById(R.id.layout_info);
            mTitle = (TextView) itemView.findViewById(R.id.txt_entity_title);
            mDesc = (TextView) itemView.findViewById(R.id.txt_entity_detail);
            mImageView = (CircleImageView) itemView.findViewById(R.id.image);

        }
    }

}

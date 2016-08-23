package com.main.lets.lets.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.main.lets.lets.Holders.PictureViewHolder;
import com.main.lets.lets.LetsAPI.Entity;
import com.main.lets.lets.R;
import com.main.lets.lets.Visualizers.SearchFeed;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;


/**
 * Created by jnovosel on 7/8/16.
 */
public class SearchAdapter extends RecyclerView.Adapter {
    public OnEntityClickListener mOnEntityClicked;
    public ArrayList<Integer> mSelected;
    public AppCompatActivity mActivity;
    public SearchFeed.Viewing mActive;
    public boolean mSelectable;
    public JSONArray mList;

    public SearchAdapter(AppCompatActivity a, JSONArray l, SearchFeed.Viewing active) {
        mSelected = new ArrayList<>();
        mActive = active;
        mActivity = a;
        mList = l;

    }

    @Override
    public PictureViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PictureViewHolder(LayoutInflater.from(parent.getContext())
                                            .inflate(R.layout.row_entity_with_picture, parent,
                                                     false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {
            final PictureViewHolder h = (PictureViewHolder) holder;
            final Entity e = new Entity(mList.getJSONObject(h.getAdapterPosition()));
            if (mActive == SearchFeed.Viewing.EVENT) {
            } else if (e.mPic != null) {

                try {
                    Picasso.with(mActivity).load(e.mPic).into(h.mImage);

                } catch (IllegalArgumentException e1) {
                    e1.printStackTrace();
                }
                
            }

            h.mText.setText(e.mText);
            h.mLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnEntityClicked != null) {
                        if (mSelectable) {
                            if (!mSelected.contains(e.mID)) {
                                mSelected.add(e.mID);
                                h.mLayout.setBackgroundColor(Color.rgb(255, 255, 204));

                            } else {
                                mSelected.remove(Integer.valueOf(e.mID));
                                h.mLayout.setBackgroundColor(Color.WHITE);

                            }

                        }

                        mOnEntityClicked.onClicked(h.getAdapterPosition(), h, e.mID);
                    }
                }
            });

            if (mSelected.contains(e.mID))
                h.mLayout.setBackgroundColor(Color.rgb(255, 255, 204));
            else
                h.mLayout.setBackgroundColor(Color.WHITE);


        } catch (JSONException e1) {
            e1.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return mList.length();
    }

    public int getImageResourceId(Context context, int category) {
        return context.getResources().getIdentifier(("j" + Integer.toString(category))
                                                            .replaceAll("\\s+", "").toLowerCase(),
                                                    "drawable", context.getPackageName());
    }

    public void setSelected(ArrayList<Integer> s) {
        mSelected = (ArrayList) s.clone();
    }

    public void setOnEntityClicked(OnEntityClickListener e) {
        mOnEntityClicked = e;
    }

    public interface OnEntityClickListener {
        void onClicked(int position, PictureViewHolder holder, int userID);
    }

}

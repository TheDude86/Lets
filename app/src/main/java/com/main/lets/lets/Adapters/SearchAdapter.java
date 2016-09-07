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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by jnovosel on 7/8/16.
 *
 * This class is used to display all of the entities for when a user searches for something or
 * wants to invite a friend, or group to an event or group
 */
public class SearchAdapter extends FeedAdapter {
    public OnEntityClickListener mOnEntityClicked;
    public ArrayList<Integer> mSelected;
    public SearchFeed.Viewing mActive;
    public boolean mSelectable;

    public SearchAdapter(AppCompatActivity a, JSONArray l, SearchFeed.Viewing active) {
        mSelected = new ArrayList<>();
        mActive = active;
        mActivity = a;

        for (int i = 0; i < l.length(); i++){
            try {
                mList.add(l.getJSONObject(i).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public PictureViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PictureViewHolder(LayoutInflater.from(parent.getContext())
                                            .inflate(R.layout.row_entity_with_picture, parent,
                                                     false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
            try {
                final PictureViewHolder h = (PictureViewHolder) holder;
                final Entity e = new Entity(new JSONObject(mList.get(h.getAdapterPosition())));

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
        return mList.size();
    }

    public int getImageResourceId(Context context, int category) {
        return context.getResources().getIdentifier(("j" + Integer.toString(category))
                                                            .replaceAll("\\s+", "").toLowerCase(),
                                                    "drawable", context.getPackageName());
    }

    public void setSelected(ArrayList<Integer> s) {
        for (Integer i: s)
            mSelected.add(i);
    }

    public void setOnEntityClicked(OnEntityClickListener e) {
        mOnEntityClicked = e;
    }

    public interface OnEntityClickListener {
        void onClicked(int position, PictureViewHolder holder, int userID);
    }

}

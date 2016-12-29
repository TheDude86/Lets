package com.main.lets.lets.Adapters;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.main.lets.lets.Holders.EntityViewHolder;
import com.main.lets.lets.LetsAPI.BitmapLoader;
import com.main.lets.lets.LetsAPI.Entity;
import com.main.lets.lets.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Joe on 6/6/2016.
 */
public class EntityAdapter extends RecyclerView.Adapter {
    private OnEntityClickListener mOnEntityClicked;

    public enum Viewing {EVENTS, GROUPS, FRIENDS}

    private static final int DETAIL_CODE = 1;
    private ArrayList<String> mList;
    private AppCompatActivity mActivity;
    private Viewing active;

    public EntityAdapter(AppCompatActivity a, ArrayList<String> list, Viewing v) {
        mActivity = a;
        mList = list;
        active = v;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (active) {
            case EVENTS:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_entity_with_picture, parent, false);

                return new EntityViewHolder(view);
            case FRIENDS:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_entity_with_picture, parent, false);

                return new EntityViewHolder(view);
            case GROUPS:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_entity_with_picture, parent, false);

                return new EntityViewHolder(view);

        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {

            switch (active) {
                case EVENTS:
                    bindEvent((EntityViewHolder) holder, mList.get(position));

                    break;

                case FRIENDS:
                    bindFriend((EntityViewHolder) holder, mList.get(position));

                    break;
                case GROUPS:
                    bindGroup((EntityViewHolder) holder, mList.get(position));

                    break;

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void bindGroup(EntityViewHolder holder, String data)
            throws JSONException {
        final Entity e = new Entity(new JSONObject(data));
        holder.mTitle.setText(e.mText);
        holder.mDetail.setText(e.mDetail);

        if (e.mPic != null) {
//            e.loadImage(mActivity, holder.mImage);
            holder.mImage.setImageBitmap(new BitmapLoader(mActivity, e.mPic).decodeSampledBitmapFromFile(70, 70));
        }


//            Picasso.with(mActivity).load(e.mPic).into(holder.mImage);


        holder.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnEntityClicked != null)
                    mOnEntityClicked.onClicked(e.mID);

            }
        });

    }

    public void bindFriend(EntityViewHolder holder, final String data)
            throws JSONException {
        final Entity e = new Entity(new JSONObject(data));
        holder.mTitle.setText(e.mText);
        holder.mDetail.setText(e.mDetail);

        if (e.mPic != null)
            Picasso.with(mActivity).load(e.mPic).into((holder).mImage);

        holder.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnEntityClicked != null)
                    mOnEntityClicked.onClicked(e.mID);
            }
        });

    }

    public void bindEvent(final EntityViewHolder holder, final String data)
            throws JSONException {
        final Entity e = new Entity(new JSONObject(data));
        holder.mTitle.setText(e.mText);
        holder.mDetail.setText(e.mDetail);

        if (e.mCategory != -1) {
            holder.mImage.setImageBitmap(new BitmapLoader(mActivity, R.drawable.j0).decodeSampledBitmapFromResource(70, 70));
//            holder.mImage.setImageBitmap(new BitmapLoader(mActivity, e.mPic).decodeSampledBitmapFromFile(70, 70));

        }


        holder.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnEntityClicked != null)
                    mOnEntityClicked.onClicked(e.mID);
            }
        });

    }


    public interface OnEntityClickListener {
        void onClicked(int id);
    }

    public void setOnEntityClickListener(OnEntityClickListener e) {
        mOnEntityClicked = e;

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public int getImageResourceId(Context context, int category) {
        return context.getResources().getIdentifier(("j" + Integer.toString(category))
                                                            .replaceAll("\\s+", "").toLowerCase(), "drawable", context.getPackageName());
    }

}

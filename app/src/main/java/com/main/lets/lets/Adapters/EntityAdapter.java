package com.main.lets.lets.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.main.lets.lets.Activities.EventDetailActivity;
import com.main.lets.lets.LetsAPI.Entity;
import com.main.lets.lets.LetsAPI.Event;
import com.main.lets.lets.R;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Joe on 6/6/2016.
 */
public class EntityAdapter extends RecyclerView.Adapter {
    public enum Viewing {EVENTS, GROUPS, FRIENDS}

    private static final int DETAIL_CODE = 1;
    private ArrayList<String> mList;
    private Activity mActivity;
    private Viewing active;

    public EntityAdapter(Activity a, ArrayList<String> list, Viewing v) {
        mActivity = a;
        mList = list;
        active = v;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (active) {
            case EVENTS:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_event, parent, false);

                return new EventHolder(view);
            case FRIENDS:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_entity_with_space, parent, false);

                return new ViewHolder(view);
            case GROUPS:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_entity_with_space, parent, false);

                return new ViewHolder(view);

        }

        return null;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {

            switch (active) {
                case EVENTS:
                    bindEvent((EventHolder) holder, mList.get(position));

                    break;

                case FRIENDS:
                    bindFriend((ViewHolder) holder, mList.get(position));

                    break;
                case GROUPS:
                    ((ViewHolder) holder).mTitle.setText(new Entity(new JSONObject(mList.get(position))).mText);


                    break;

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void bindFriend(ViewHolder holder, String json) throws JSONException {
        holder.mTitle.setText(new Entity(new JSONObject(json)).mText);
        holder.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    public void bindEvent(final EventHolder holder, final String data) throws JSONException {
            org.json.JSONObject j = new org.json.JSONObject(data);
            Event e = new Event(j);

            holder.mLocation.setText(e.getmLocationTitle());
            holder.mTime.setText(e.getTimeSpanString());
            holder.mTitle.setText(e.getmTitle());

            Bitmap photo = BitmapFactory.decodeResource(mActivity.getResources(), e.getImageResourceId(mActivity));
            Picasso.with(mActivity).load(e.getImageResourceId(mActivity)).into((holder.mBackground));

            Palette.generateAsync(photo, new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    int bgColor = palette.getMutedColor(mActivity.getResources().getColor(android.R.color.black));
                    holder.mTextBackground.setBackgroundColor(bgColor);
                }
            });

            holder.mMainViewHolder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mActivity, EventDetailActivity.class);
                    intent.putExtra("JSON", data);
                    mActivity.startActivityForResult(intent, DETAIL_CODE);

                }
            });

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout mLayout;
        TextView mTitle;

        public ViewHolder(View itemView) {
            super(itemView);

            mLayout = (RelativeLayout) itemView.findViewById(R.id.layout_info);
            mTitle = (TextView) itemView.findViewById(R.id.txt_entity_title);

        }
    }

    protected class EventHolder extends RecyclerView.ViewHolder {
        LinearLayout mMainViewHolder;
        LinearLayout mTextBackground;
        ImageView mBackground;
        TextView mLocation;
        TextView mTitle;
        TextView mTime;

        /**
         * give more control over NORMAL or HEADER view binding
         *
         * @param itemView view binding
         */
        public EventHolder(View itemView) {
            super(itemView);

            mTextBackground = (LinearLayout) itemView.findViewById(R.id.eventNameHolder);
            mMainViewHolder = (LinearLayout) itemView.findViewById(R.id.eventHolder);
            mBackground = (ImageView) itemView.findViewById(R.id.eventImage);
            mLocation = (TextView) itemView.findViewById(R.id.eventLocation);
            mTitle = (TextView) itemView.findViewById(R.id.eventTitle);
            mTime = (TextView) itemView.findViewById(R.id.eventTime);

        }

    }

}
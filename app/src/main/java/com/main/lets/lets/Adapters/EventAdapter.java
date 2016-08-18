package com.main.lets.lets.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.main.lets.lets.Activities.EventDetailActivity;
import com.main.lets.lets.LetsAPI.Event;
import com.main.lets.lets.R;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.quickAdapter.easyRegularAdapter;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.util.List;

/**
 * Created by Joe on 5/29/2016.
 */
public class EventAdapter extends easyRegularAdapter<String, EventAdapter.ViewHolder> {
    private static final int DETAIL_CODE = 1;
    String ShallonCreamerIsATwat;
    Activity mActivity;
    Event e;
    int mID;

    /**
     * dynamic object to start
     *
     * @param list the list source
     */
    public EventAdapter(Activity a, List<String> list, String token, int id) {
        super(list);
        ShallonCreamerIsATwat = token;
        mActivity = a;
        mID = id;

    }

    @Override
    protected int getNormalLayoutResId() {
        return R.layout.row_event;
    }

    @Override
    protected ViewHolder newViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    protected void withBindHolder(final ViewHolder holder, final String data, int position) {
        try {
            final org.json.JSONObject j = new org.json.JSONObject(data);
            e = new Event(j);

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
                    try {
                        Intent intent = new Intent(mActivity, EventDetailActivity.class);
                        intent.putExtra("EventID", j.getInt("Event_ID"));
                        intent.putExtra("token", ShallonCreamerIsATwat);
                        intent.putExtra("id", mID);
                        mActivity.startActivityForResult(intent, DETAIL_CODE);
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }



                }
            });

        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }


    protected class ViewHolder extends UltimateRecyclerviewViewHolder {
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
        public ViewHolder(View itemView) {
            super(itemView);

            mTextBackground = (LinearLayout) itemView.findViewById(R.id.eventNameHolder);
            mMainViewHolder = (LinearLayout) itemView.findViewById(R.id.eventHolder);
            mBackground = (ImageView) itemView.findViewById(R.id.eventImage);
            mLocation = (TextView) itemView.findViewById(R.id.eventLocation);
            mTitle = (TextView) itemView.findViewById(R.id.eventTitle);
            mTime = (TextView) itemView.findViewById(R.id.eventTime);

        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }
}
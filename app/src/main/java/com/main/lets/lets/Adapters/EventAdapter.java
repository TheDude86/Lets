package com.main.lets.lets.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
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
 *
 * This class fills each event with it's specific information on the main activity feed
 *
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

            final int[] bgColors = mActivity.getResources().getIntArray(R.array.category_colors);

            final String[] catgories = mActivity.getResources().getStringArray(R.array.categories);

            final org.json.JSONObject j = new org.json.JSONObject(data);
            e = new Event(j);

            holder.mLocation.setText(e.getmLocationTitle());
            holder.mTime.setText(e.getTimeSpanString());
            holder.mHost.setText(e.getmOwnerName());
            holder.mTitle.setText(e.getmTitle());
            holder.mMonth.setText(e.getMonth());
            holder.mDay.setText(e.getDay());


            Picasso.with(mActivity).load(e.getImageResourceId(mActivity))
                    .into((holder.mBackground));

            holder.mBackgroundContainer.setBackgroundColor(bgColors[j.getInt("Category")]);
            holder.mCategory.setText(catgories[j.getInt("Category")]);


            holder.mBackgroundContainer.setOnClickListener(new View.OnClickListener() {
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
        LinearLayout mBackgroundContainer;
        ImageView mBackground;
        TextView mLocation;
        TextView mCategory;
        TextView mTitle;
        TextView mMonth;
        TextView mHost;
        TextView mTime;
        TextView mDay;

        /**
         * give more control over NORMAL or HEADER view binding
         *
         * @param itemView view binding
         */
        public ViewHolder(View itemView) {
            super(itemView);

            mBackgroundContainer = (LinearLayout) itemView.findViewById(R.id.eventHolder);
            mBackground = (ImageView) itemView.findViewById(R.id.eventImage);
            mLocation = (TextView) itemView.findViewById(R.id.eventLocation);
            mCategory = (TextView) itemView.findViewById(R.id.category);
            mTitle = (TextView) itemView.findViewById(R.id.eventTitle);
            mTime = (TextView) itemView.findViewById(R.id.eventTime);
            mHost = (TextView) itemView.findViewById(R.id.hostName);
            mMonth = (TextView) itemView.findViewById(R.id.month);
            mDay = (TextView) itemView.findViewById(R.id.day);

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
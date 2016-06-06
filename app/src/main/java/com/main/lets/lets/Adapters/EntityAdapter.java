package com.main.lets.lets.Adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.main.lets.lets.LetsAPI.Entity;
import com.main.lets.lets.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Joe on 6/6/2016.
 */
public class EntityAdapter extends RecyclerView.Adapter {
    public enum Viewing {EVENTS, GROUPS, FRIENDS;}
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_entity_with_space, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {
            ((ViewHolder)holder).mTitle.setText(new Entity(new JSONObject(mList.get(position))).mText);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder{
        TextView mTitle;

        public ViewHolder(View itemView) {
            super(itemView);

            mTitle = (TextView)itemView.findViewById(R.id.txt_entity_title);

        }
    }
}

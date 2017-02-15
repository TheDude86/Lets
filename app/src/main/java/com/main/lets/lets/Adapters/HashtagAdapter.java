package com.main.lets.lets.Adapters;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.main.lets.lets.R;

import java.util.ArrayList;

/**
 * Created by novosejr on 2/13/2017.
 */

public class HashtagAdapter extends RecyclerView.Adapter<HashtagAdapter.Holder> {
    ArrayList<String> mList = new ArrayList<>();
    AppCompatActivity mActivity;

    public HashtagAdapter (AppCompatActivity a) {
        mActivity = a;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_hashtag_name,
                        parent, false));
    }

    public void add(String s){
        mList.add(s);
        notifyDataSetChanged();
    }

    public void remove(String s){
        mList.remove(s);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        final String s = mList.get(position);
        holder.mName.setText(s);

        holder.mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                remove(s);
            }
        });


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView mName;
        ImageView mCancel;

        public Holder(View itemView) {
            super(itemView);

            mName = (TextView) itemView.findViewById(R.id.name);
            mCancel = (ImageView) itemView.findViewById(R.id.remove);
        }
    }
}

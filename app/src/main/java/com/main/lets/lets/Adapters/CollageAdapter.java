package com.main.lets.lets.Adapters;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.main.lets.lets.Activities.ImagePreviewActivity;
import com.main.lets.lets.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by novosejr on 2/6/2017.
 */

public class CollageAdapter extends RecyclerView.Adapter<CollageAdapter.Holder> {

    ArrayList<Uri> mPictures = new ArrayList<>();
    AppCompatActivity mActivity;

    public CollageAdapter(AppCompatActivity a) {
        mActivity = a;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_collage, parent,
                        false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        final Uri u = mPictures.get(position);

        Picasso.with(mActivity).load(u).into(holder.mPicture);
        holder.mPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(mActivity, ImagePreviewActivity.class);
                i.putExtra("path", u.toString());
                i.putExtra("type", "display");

                mActivity.startActivity(i);

            }
        });

    }

    @Override
    public int getItemCount() {
        return mPictures.size();
    }

    public void addElement(Uri u) {
        mPictures.add(u);
        notifyDataSetChanged();
    }

    class Holder extends RecyclerView.ViewHolder {
        ImageView mPicture;

        public Holder(View itemView) {
            super(itemView);

            mPicture = (ImageView) itemView.findViewById(R.id.image);
        }
    }

}

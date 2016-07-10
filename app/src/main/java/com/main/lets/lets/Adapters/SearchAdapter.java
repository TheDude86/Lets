package com.main.lets.lets.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.main.lets.lets.Holders.SearchViewHolder;
import com.main.lets.lets.LetsAPI.Calls;
import com.main.lets.lets.LetsAPI.Entity;
import com.main.lets.lets.R;
import com.main.lets.lets.Visualizers.SearchFeed;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;


/**
 * Created by jnovosel on 7/8/16.
 */
public class SearchAdapter extends RecyclerView.Adapter {
    OnEntityClickListener mOnEntityClicked;
    ArrayList<Integer> mSelected;
    AppCompatActivity mActivity;
    SearchFeed.Viewing mActive;
    JSONArray mList;

    public SearchAdapter(AppCompatActivity a, JSONArray l, SearchFeed.Viewing active) {
        mSelected = new ArrayList<>();
        mActive = active;
        mActivity = a;
        mList = l;

    }

    @Override
    public SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SearchViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_entity_with_picture, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        try {
            final SearchViewHolder h = (SearchViewHolder) holder;
            final Entity e = new Entity(mList.getJSONObject(position));
            if (mActive == SearchFeed.Viewing.EVENT) {
                Picasso.with(mActivity).load(getImageResourceId(mActivity, e.mCategory)).into((h.mImage));
            } else if(e.mPic != null){
                Calls.loadImage(e.mPic, new FileAsyncHttpResponseHandler(mActivity) {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                        Log.println(Log.ASSERT, "UserDetailAdapter", "Test failed");

                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, File response) {
                        Bitmap myBitmap = BitmapFactory.decodeFile(response.getAbsolutePath());
                        h.mImage.setImageBitmap(myBitmap);

                    }
                });
            }

            h.mText.setText(e.mText);
            h.mLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnEntityClicked != null){
                        if(e.mPic == null){
                            h.mLayout.setBackgroundColor(h.clicked ? Color.WHITE : Color.rgb(255, 255, 204));
                            h.clicked = !h.clicked;
                        }

                        mOnEntityClicked.onClicked(position);
                    }
                }
            });

            for (Integer i: mSelected){
                if (e.mID == i){
                    h.mLayout.setBackgroundColor(Color.rgb(255, 255, 204));

                }
            }

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
                .replaceAll("\\s+", "").toLowerCase(), "drawable", context.getPackageName());
    }

    public void setSelected(ArrayList<Integer> s){
        mSelected = s;
    }

    public void setOnEntityClicked(OnEntityClickListener e) {
        mOnEntityClicked = e;
    }

    public interface OnEntityClickListener {
        void onClicked(int position);
    }

}

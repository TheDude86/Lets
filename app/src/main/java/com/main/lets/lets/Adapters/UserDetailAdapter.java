package com.main.lets.lets.Adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.main.lets.lets.Holders.ProfileDetailViewHolder;
import com.main.lets.lets.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Joe on 6/30/2016.
 */
public class UserDetailAdapter extends RecyclerView.Adapter {
    ArrayList<String> mTest = new ArrayList<>();
    public ProfileDetailViewHolder mHolder;
    JSONObject mJSON;

    public UserDetailAdapter(JSONObject j) {
        mTest.add("Hi");
        mTest.add("Hi");
        mTest.add("Hi");

        mJSON = j;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ProfileDetailViewHolder(LayoutInflater.from(parent.getContext())
                                                   .inflate(R.layout.row_profile, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        mHolder = (ProfileDetailViewHolder) holder;
        try {
            mHolder.mFriends.setText("Bill fucking fix getFriends");
            mHolder.mName.setText(mJSON.getString("User_Name"));
            mHolder.mBio.setText(mJSON.getString("Biography"));
            mHolder.mScore.setText(mJSON.getInt("Score") + "");
            mHolder.mInterests.setText("Fix this too");



            try {
                URL url = new URL(mJSON.getString("Profile_Picture"));
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);

                mHolder.mPic.setImageBitmap(myBitmap);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        ((ProfileDetailViewHolder) holder).mName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addElement("poo");
            }
        });

    }

    @Override
    public int getItemCount() {
        return mTest.size();
    }

    public void addElement(String s){
        mTest.add("test");
        notifyItemInserted(mTest.size() - 1);
        notifyDataSetChanged();
    }

    public void removeElement(int position){
        mTest.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mTest.size());
    }
}

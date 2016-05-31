package com.main.lets.lets.Visulizers;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.main.lets.lets.Adapters.LoginAdapter;
import com.main.lets.lets.Adapters.ProfileAdapter;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Joe on 5/30/2016.
 */
public class ProfileFeed extends Client {
    public static final String FILENAME = "userInfo";
    UltimateRecyclerView mRecyclerView;
    HashMap<String, Object> mUserInfo;
    String ShallonCreamerIsATwat = "Bearer ";
    ProfileAdapter mProfileAdapter;
    LoginAdapter mLoginAdapter;
    Activity mActivity;
    JSONObject mUser;

    public ProfileFeed(Activity a, UltimateRecyclerView r, String token) {
        ShallonCreamerIsATwat = token;
        mRecyclerView = r;
        mActivity = a;

    }

    public ProfileFeed(Activity a, UltimateRecyclerView r, HashMap<String, Object> m) {
        mRecyclerView = r;
        mActivity = a;
        mUserInfo = m;
        try {

            mProfileAdapter = new ProfileAdapter(mActivity, new ArrayList<String>());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void draw(JSONObject j) {
        if (ShallonCreamerIsATwat.equals("Bearer ")) {
            mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
            mLoginAdapter = new LoginAdapter(mActivity);

            mLoginAdapter.setOnLoginClick(new LoginAdapter.OnItemClickListener() {
                @Override
                public void onItemClick( String email, String password) {
                    login(email, password);

                }

            });


            mRecyclerView.setAdapter(mLoginAdapter);
        }else{
            try {
                mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
                mRecyclerView.setAdapter(mProfileAdapter);
                mProfileAdapter.removeAll();
                mProfileAdapter.insert(mUser.toString());

                loadFriends();

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    public void loadFriends() throws JSONException {
        RequestParams params = new RequestParams();
        client.addHeader("Authorization", ShallonCreamerIsATwat);
        post("friends/getFriends", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, org.json.JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {

                        if (response.getJSONObject(i).getBoolean("status"))
                            mProfileAdapter.mFriends.add(response.getJSONObject(i).toString());

                    } catch (org.json.JSONException e) {
                        e.printStackTrace();
                    }

                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                  org.json.JSONArray errorResponse) {
                Log.e("Aync Test Failure", errorResponse.toString());
            }

        });


    }

    public void login(final String email, final String password) {
        RequestParams params = new RequestParams();
        params.put("email", email);
        params.put("password", password);
        post("user/loginSecure", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, org.json.JSONObject response) {
                try {
                    ShallonCreamerIsATwat += response.getString("accessToken");
                    mUserInfo.put("token", ShallonCreamerIsATwat);
                } catch (org.json.JSONException e) {
                    e.printStackTrace();
                }

                String string = email + ":" + password;
                FileOutputStream fos;
                try {
                    fos = mActivity.openFileOutput(FILENAME, Context.MODE_PRIVATE);
                    fos.write(string.getBytes());
                    fos.close();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                RequestParams params2 = new RequestParams();
                client.addHeader("Authorization", ShallonCreamerIsATwat);
                post("user/getMyProfile", params2, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, org.json.JSONArray response) {
                        try {
                            mUser = response.getJSONObject(0);
                            draw(null);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                          org.json.JSONArray errorResponse) {
                        Log.e("Aync Test Failure", errorResponse.toString());
                    }

                });


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                  org.json.JSONArray errorResponse) {
                Log.e("Aync Test Failure", errorResponse.toString());
            }

        });
    }

}

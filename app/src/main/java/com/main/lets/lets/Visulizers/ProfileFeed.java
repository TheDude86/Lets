package com.main.lets.lets.Visulizers;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.main.lets.lets.Adapters.EntityAdapter;
import com.main.lets.lets.Adapters.LoginAdapter;
import com.main.lets.lets.Adapters.ProfileAdapter;
import com.main.lets.lets.LetsAPI.Calls;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Joe on 5/30/2016.
 */
public class ProfileFeed extends Client {
    public static final String FILENAME = "userInfo";
    String ShallonCreamerIsATwat = "Bearer ";
    UltimateRecyclerView mRecyclerView;
    HashMap<String, Object> mUserInfo;
    ProfileAdapter mProfileAdapter;
    LoginAdapter mLoginAdapter;
    Activity mActivity;
    JSONObject mUser;


    public ProfileFeed(Activity a, UltimateRecyclerView r, HashMap<String, Object> m) {
        mRecyclerView = r;
        mActivity = a;
        mUserInfo = m;

        if (!loginInitial()) {
            try {

                mProfileAdapter =
                        new ProfileAdapter(mActivity, new ArrayList<String>(), ShallonCreamerIsATwat);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    public void draw(JSONObject j) {
        mProfileAdapter.enableLoadMore();
        if (ShallonCreamerIsATwat.equals("Bearer ")) {
            mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
            mLoginAdapter = new LoginAdapter(mActivity);

            mLoginAdapter.setOnLoginClick(new LoginAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(String email, String password) {
                    login(email, password);
                    draw(null);

                }

            });

            mRecyclerView.setAdapter(mLoginAdapter);
        } else {
            try {
                mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));

                loadFriends();
                loadGroups();
                loadAttend();

                mRecyclerView.setAdapter(mProfileAdapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    public void loadUser(int userID) {

        Calls.getProfileByID(userID, ShallonCreamerIsATwat, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, org.json.JSONArray response) {
                try {
                    mUser = response.getJSONObject(0);
                    ArrayList<String> l = new ArrayList<>();
                    l.add(response.getJSONObject(0).toString());
                    mProfileAdapter = new ProfileAdapter(mActivity, l, ShallonCreamerIsATwat);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                  org.json.JSONArray errorResponse) {
                Log.e("Async Test Failure", errorResponse.toString());
            }

        });

    }

    public void loadGroups() throws JSONException {

        Calls.getGroups(mUser.getInt("User_ID"), ShallonCreamerIsATwat, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, org.json.JSONArray response) {
                mProfileAdapter.mGroups = new ArrayList<>();
                for (int i = 0; i < response.length(); i++) {
                    try {

                        mProfileAdapter.mGroups.add(response.getJSONObject(i).toString());

                    } catch (org.json.JSONException e) {
                        e.printStackTrace();
                    }

                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                  org.json.JSONArray errorResponse) {
                Log.e("Async Test Failure", errorResponse.toString());
            }

        });


    }

    public void loadAttend() throws JSONException {

        Calls.getAttended(mUser.getInt("User_ID"), ShallonCreamerIsATwat, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, org.json.JSONArray response) {
                mProfileAdapter.mEvents = new ArrayList<>();
                for (int i = 0; i < response.length(); i++) {
                    try {

                        mProfileAdapter.mEvents.add(response.getJSONObject(i).toString());

                    } catch (org.json.JSONException e) {
                        e.printStackTrace();
                    }

                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                  org.json.JSONArray errorResponse) {
                Log.e("Async Test Failure", errorResponse.toString());
            }

        });

    }

    public void loadFriends() throws JSONException {

        Calls.getFriends(ShallonCreamerIsATwat, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                mProfileAdapter.mFriends = new ArrayList<>();
                for (int i = 0; i < response.length(); i++) {
                    try {

                        if (response.getJSONObject(i).getBoolean("status")) {
                            mProfileAdapter.mFriends.add(response.getJSONObject(i).toString());

                        }


                    } catch (org.json.JSONException e) {
                        e.printStackTrace();
                    }

                }

//                mProfileAdapter.mViewHolder.getFriends().setText(mProfileAdapter.mFriends.size() +
//                        " Friends");

                mProfileAdapter.mDemoHolder.getEntityList().setAdapter(new EntityAdapter(mActivity,
                        mProfileAdapter.mFriends, EntityAdapter.Viewing.FRIENDS, ShallonCreamerIsATwat));

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                  org.json.JSONArray errorResponse) {
                Log.e("Async Test Failure", errorResponse.toString());
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
                    Log.println(Log.ASSERT, "Token", response.toString());
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
                            ArrayList<String> l = new ArrayList<>();
                            l.add(response.getJSONObject(0).toString());
                            mProfileAdapter = new ProfileAdapter(mActivity, l, ShallonCreamerIsATwat);

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
                Log.e("Async Test Failure", errorResponse.toString());
            }

        });
    }

    public boolean loginInitial() {
        try {

            try {
                FileInputStream fis = mActivity.openFileInput(FILENAME);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader bufferedReader = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();
                String line;

                String[] cred = {};
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                if (!sb.toString().equals("blank")) {
                    cred = sb.toString().split(":");
                    login(cred[0], cred[1]);

                } else
                    return false;

            } catch (FileNotFoundException e) {
                FileOutputStream fos = null;
                String string = "blank";
                fos = mActivity.openFileOutput(FILENAME, Context.MODE_PRIVATE);
                fos.write(string.getBytes());
                fos.close();

                return false;
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;

    }


}

package com.main.lets.lets.Visualizers;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.main.lets.lets.Activities.EventDetailActivity;
import com.main.lets.lets.Activities.GroupDetailActivity;
import com.main.lets.lets.Activities.UserDetailActivity;
import com.main.lets.lets.Adapters.EntityAdapter;
import com.main.lets.lets.Adapters.UserDetailAdapter;
import com.main.lets.lets.LetsAPI.Calls;
import com.main.lets.lets.LetsAPI.Entity;
import com.main.lets.lets.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by jnovosel on 6/24/16.
 */
public class UserDetailFeed extends Client {
    ArrayList<String> mFriends, mFriendTags, mGroups, mEvents, mEventTags, mGroupTags;

    enum Relationship {NONE, REQUEST, FRIEND, OWNER}

    enum Viewing {EVENT, GROUP, USER}

    Relationship mRelationship = Relationship.NONE;
    Viewing active = Viewing.USER;
    String ShallonCreamerIsATwat;
    UserDetailAdapter mAdapter;
    RecyclerView mRecyclerView;
    Activity mActivity;
    JSONObject mJSON;
    int mID;

    public UserDetailFeed(Activity a, RecyclerView recyclerView, JSONObject j, String token,
                          int id) {
        mFriendTags = new ArrayList<>();
        mEventTags = new ArrayList<>();
        mGroupTags = new ArrayList<>();
        ShallonCreamerIsATwat = token;
        mRecyclerView = recyclerView;
        mFriends = new ArrayList<>();
        mGroups = new ArrayList<>();
        mEvents = new ArrayList<>();
        mActivity = a;
        mJSON = j;
        mID = id;

        try {
            loadAttend();
            loadGroups();
            loadFriends();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mActivity.findViewById(R.id.add_friend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mRelationship) {
                    case NONE:
                        try {
                            Calls.sendFriendRequest(mJSON.getInt("User_ID"), ShallonCreamerIsATwat,
                                                    new JsonHttpResponseHandler() {
                                                        @Override
                                                        public void onSuccess(int statusCode,
                                                                              Header[] headers,
                                                                              JSONObject response) {
                                                            ((TextView) mActivity
                                                                    .findViewById(R.id.add_text))
                                                                    .setText("Friend request sent");
                                                            mRelationship = Relationship.REQUEST;
                                                        }
                                                    });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        break;
                    case REQUEST:
                        //Dialog to revoke friend request

                        break;
                    case FRIEND:
                        //Hide

                        break;
                    case OWNER:
                        //Load Edit Profile Activity
                }
            }
        });

//        mActivity.findViewById(R.id.options).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.println(Log.ASSERT, "UserDetailFeed", "Add more user commands");
//            }
//        });

    }

    @Override
    public void draw(JSONObject j) {
        try {
            if (mJSON.getInt("User_ID") == mID) {
                mRelationship = Relationship.OWNER;
                ((TextView) mActivity.findViewById(R.id.add_text)).setText("Edit Profile");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        mRecyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        mAdapter = new UserDetailAdapter(mActivity, mJSON);

        mAdapter.setOnFriendClickListener(new UserDetailAdapter.OnFriendClickListener() {
            @Override
            public void OnClick() {
                mAdapter.clearFeed();

                for (String l : mFriendTags)
                    mAdapter.addElement(l);

                active = Viewing.USER;

            }
        });

        mAdapter.setOnEventClickListener(new UserDetailAdapter.OnEventClickListener() {
            @Override
            public void OnClick() {
                mAdapter.clearFeed();
                Log.println(Log.ASSERT, "UserDetailFeed", mEventTags.toString());

                for (String l : mEventTags)
                    mAdapter.addElement(l);

                active = Viewing.EVENT;
            }
        });

        mAdapter.setOnGroupClickListener(new UserDetailAdapter.OnGroupClickListener() {
            @Override
            public void OnClick() {
                mAdapter.clearFeed();

                for (String l : mGroupTags)
                    mAdapter.addElement(l);

                active = Viewing.GROUP;
            }
        });

        mAdapter.setOnEntityClickListener(new UserDetailAdapter.OnEntityClickListener() {
            @Override
            public void OnClick(int position) {
                try {

                    Intent intent;
                    switch (active) {
                        case EVENT:
                                intent = new Intent(mActivity, EventDetailActivity.class);
                                intent.putExtra("EventID", new JSONObject(mEventTags.get(position)).getInt("event_id"));
                                intent.putExtra("token", ShallonCreamerIsATwat);
                                intent.putExtra("id", mID);
                                mActivity.startActivity(intent);

                            break;
                        case GROUP:
                            intent = new Intent(mActivity, GroupDetailActivity.class);
                            intent.putExtra("GroupID", (new JSONObject(mGroupTags.get(position))).getInt("group_id"));
                            intent.putExtra("token", ShallonCreamerIsATwat);
                            intent.putExtra("id", mID);
                            mActivity.startActivity(intent);

                            break;
                        case USER:
                            intent = new Intent(mActivity, UserDetailActivity.class);
                            intent.putExtra("UserID", (new JSONObject(mFriendTags.get(position))
                                    .getInt("user_id")));
                            intent.putExtra("token", ShallonCreamerIsATwat);
                            intent.putExtra("id", mID);
                            mActivity.startActivity(intent);

                            break;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        mRecyclerView.setAdapter(mAdapter);

    }


    /**
     * Network call to get the logged in user's groups and then passes the group list to the
     * profile adapter
     *
     * @throws JSONException
     */
    public void loadGroups() throws JSONException {

        Calls.getGroups(mJSON.getInt("User_ID"), ShallonCreamerIsATwat,
                        new JsonHttpResponseHandler() {
                            /**
                             * When the call is made, it returns a JSON array object of all of
                             * the groups the user attends.  The JSON objects from the array are
                             * then added to the profile adapter.
                             *
                             * @param statusCode (unused)
                             * @param headers (unused)
                             * @param response JSON array of all groups the user is a part of
                             */
                            @Override
                            public void onSuccess(int statusCode, Header[] headers,
                                                  org.json.JSONArray response) {
                                try {
                                    for (int i = 0; i < response.length(); i++) {
                                        //Takes all of the JSON object out of the array and
                                        // placed in a temporary array list
                                        mGroupTags.add(response.getJSONObject(i).toString());

                                    }

                                    loadGroupDetails(0);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

                            /**
                             * Called when an error occurs somewhere with the call.
                             *
                             * @param statusCode (unused)
                             * @param headers (unused)
                             * @param throwable (unused)
                             * @param errorResponse (unused)
                             */
                            @Override
                            public void onFailure(int statusCode, Header[] headers,
                                                  Throwable throwable,
                                                  org.json.JSONArray errorResponse) {
                                Log.e("Async Test Failure", errorResponse.toString());
                            }

                        });


    }

    public void loadGroupDetails(final int index) {
        if (index >= mGroupTags.size())
            return;

        try {
            Entity e = new Entity(new JSONObject(mGroupTags.get(index)));
            Calls.getGroupInfo(e.mID, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers,
                                      org.json.JSONObject response) {
                    mGroups.add(response.toString());
                    loadGroupDetails(index + 1);

                }

                /**
                 * Called when an error occurs somewhere with the call.
                 *
                 * @param statusCode (unused)
                 * @param headers (unused)
                 * @param throwable (unused)
                 * @param errorResponse (unused)
                 */
                @Override
                public void onFailure(int statusCode, Header[] headers,
                                      Throwable throwable,
                                      org.json.JSONArray errorResponse) {
                    Log.e("Async Test Failure", errorResponse.toString());
                }

            });
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * Network call to get the logged in user's events and then passes the group list to the
     * profile adapter
     *
     * @throws JSONException
     */
    public void loadAttend() throws JSONException {
        //Call to get the events the user is attending/ attended
        Calls.getAttended(mJSON.getInt("User_ID"), ShallonCreamerIsATwat,
                          new JsonHttpResponseHandler() {
                              /**
                               * When the call is made, it returns a JSON array object of all of
                               * the events the user attends.  The JSON objects from the array are
                               * then added to the profile adapter.
                               *
                               * @param statusCode (unused)
                               * @param headers (unused)
                               * @param response JSON array of all events the user is a part of
                               */
                              @Override
                              public void onSuccess(int statusCode, Header[] headers,
                                                    org.json.JSONArray response) {
                                  try {
                                      for (int i = 0; i < response.length(); i++) {
                                          //Loads the events in the temporary array list
                                          mEventTags.add(response.getJSONObject(i).toString());

                                      }
                                      loadEventDetails(0);

                                  } catch (org.json.JSONException e) {
                                      e.printStackTrace();
                                  }
                              }

                              /**
                               * Called when an error occurs somewhere with the call.
                               *
                               * @param statusCode (unused)
                               * @param headers (unused)
                               * @param throwable (unused)
                               * @param errorResponse (unused)
                               */
                              @Override
                              public void onFailure(int statusCode, Header[] headers,
                                                    String errorResponse, Throwable throwable) {
                                  Log.e("Async Test Failure", errorResponse);
                              }

                          });

    }

    public void loadEventDetails(final int index) {
        if (index >= mEventTags.size())
            return;

        try {
            Entity e = new Entity(new JSONObject(mEventTags.get(index)));
            Calls.getEvent(e.mID, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers,
                                      org.json.JSONObject response) {
                    mEvents.add(response.toString());
                    loadEventDetails(index + 1);

                }

                /**
                 * Called when an error occurs somewhere with the call.
                 *
                 * @param statusCode (unused)
                 * @param headers (unused)
                 * @param throwable (unused)
                 * @param errorResponse (unused)
                 */
                @Override
                public void onFailure(int statusCode, Header[] headers,
                                      String errorResponse, Throwable throwable) {
                    Log.e("Async Test Failure", errorResponse);
                }

            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * Network call to get the logged in user's friends and then passes the group list to the
     * profile adapter
     *
     * @throws JSONException
     */
    public void loadFriends() throws JSONException {
        //Call to get the user's friends
        Calls.getFriends(mJSON.getInt("User_ID"), new JsonHttpResponseHandler() {
            /**
             * When the call is made, it returns a JSON array object of all of
             * the friends the user attends.  The JSON objects from the array are
             * then added to the profile adapter.
             *
             * @param statusCode (unused)
             * @param headers (unused)
             * @param response JSON array of all friends the user is a part of
             */
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {


                        if (response.getJSONObject(i).getBoolean("status")) {
                            //Loads the friends into a temporary array list
                            mFriendTags.add(response.getJSONObject(i).toString());

                            //Removes the add friend button if the user is a friend of the logged
                            // in user
                            if (new Entity(response.getJSONObject(i)).mID == mID) {
                                mActivity.findViewById(R.id.add_friend).setVisibility(View.GONE);
                                mRelationship = Relationship.FRIEND;
                            }
                        } else {
                            //Removes the add friend button if the user is a friend of the logged
                            // in user
                            if (new Entity(response.getJSONObject(i)).mID == mID) {
                                ((TextView) mActivity.findViewById(R.id.add_text))
                                        .setText("Friend request sent");
                                mRelationship = Relationship.REQUEST;
                            }
                        }


                    } catch (org.json.JSONException e) {
                        e.printStackTrace();
                    }

                }

                mAdapter.mHolder.mFriends.setText(mFriendTags.size() +
                                                          ((mFriendTags.size() > 1) ? " Friends" :
                                                                  " Friend"));
                loadFriendDetails(0);
                for (String l : mFriendTags)
                    mAdapter.addElement(l);

            }

            /**
             * Called when an error occurs somewhere with the call.
             *
             * @param statusCode (unused)
             * @param headers (unused)
             * @param throwable (unused)
             * @param errorResponse (unused)
             */
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                  org.json.JSONArray errorResponse) {
                Log.e("Async Test Failure", errorResponse.toString());
            }

        });

    }

    public void loadFriendDetails(final int index) {
        if (index >= mFriendTags.size())
            return;

        try {
            Entity e = new Entity(new JSONObject(mFriendTags.get(index)));
            Calls.getProfileByID(e.mID, ShallonCreamerIsATwat, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        mFriends.add(response.getJSONArray("info").getJSONObject(0).toString());
                        loadFriendDetails(index + 1);
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }

                /**
                 * Called when an error occurs somewhere with the call.
                 *
                 * @param statusCode (unused)
                 * @param headers (unused)
                 * @param throwable (unused)
                 * @param errorResponse (unused)
                 */
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                      org.json.JSONArray errorResponse) {
                    Log.e("Async Test Failure", errorResponse.toString());
                }

            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}

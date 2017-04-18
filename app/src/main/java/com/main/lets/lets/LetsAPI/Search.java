package com.main.lets.lets.LetsAPI;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by novosejr on 12/14/2016.
 */
public class Search {
    public ArrayList<Entity> mEvents = new ArrayList<>();
    public ArrayList<Entity> mUsers = new ArrayList<>();
    public ArrayList<Entity> mGroups = new ArrayList<>();

    ArrayList<Entity> mFeed = new ArrayList<>();

    String mKeyWord;
    onUpdateListener mListener;
    public boolean[] mLoadMore = {true, true, true};

    public enum Feed {EVENT, GROUP, USER}

    public Search(onUpdateListener l) {

        mListener = l;

    }


    public void makeHashtagSearch(String[] hashtags) {
        mUsers = new ArrayList<>();
        mEvents = new ArrayList<>();
        mGroups = new ArrayList<>();

        if (mListener instanceof onSearchListener) {
            ((onSearchListener) mListener).onHashtagInit();

            for (String s : hashtags) {
                String tag = s.replace("#", "");

                if (tag.length() > 0) {

                    DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("hashtags/" + tag);
                    db.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            int ID = dataSnapshot.getValue(int.class);

                            Event e = new Event(ID);

                            if (!isEventLoaded(e)) {
                                e.getEventByID(new Event.onEventLoaded() {
                                    @Override
                                    public void EventLoaded(Event e) {

                                        mEvents.add(e);
                                        ((onSearchListener) mListener).onHashtagSearch();

                                    }
                                });

                            }

                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }


            }


        }


    }

    public boolean isEventLoaded(Event e) {

        for(Entity entity: mEvents) {

            if (entity instanceof Event) {
                Event event = (Event) entity;

                if (event.getID() == e.getID())
                    return true;

            }

        }

        return false;
    }

    public void makeSearch(String s) {

        mKeyWord = s;
        mUsers = new ArrayList<>();
        mEvents = new ArrayList<>();
        mGroups = new ArrayList<>();

        Calls.search(s, 6, 0, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {
                    JSONArray users = response.getJSONArray("users");
                    JSONArray groups = response.getJSONArray("groups");
                    JSONArray events = response.getJSONArray("events");

                    mFeed.add(new Entity(Entity.UTITLITY_HEADER, "Events", Entity.EntityType.UTITLITY));

                    for (int i = 0; i < 5; i++) {
                        if (users.length() > i)
                            mUsers.add(new Entity(users.getJSONObject(i)));

                        if (groups.length() > i)
                            mGroups.add(new Entity(groups.getJSONObject(i)));

                        if (events.length() > i)
                            mEvents.add(new Entity(events.getJSONObject(i)));

                    }

                    mLoadMore[0] = (events.length() == 6);
                    mLoadMore[1] = (users.length() == 6);
                    mLoadMore[2] = (groups.length() == 6);


                } catch (JSONException e) {
                    e.printStackTrace();
                }


                mListener.onUpdate();

            }
        });

    }

    public void loadMore(final Feed f, final onLoadMoreListener l) {

        int offset = 0;

        switch (f) {
            case EVENT:
                offset = mEvents.size();
                break;
            case USER:
                offset = mUsers.size();
                break;
            case GROUP:
                offset = mGroups.size();
                break;
        }


        Calls.search(mKeyWord, 6, offset, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {

                    switch (f) {
                        case EVENT:

                            JSONArray events = response.getJSONArray("events");

                            for (int i = 0; i < 5; i++) {
                                if (events.length() > i) {
                                    Entity e = new Entity(events.getJSONObject(i));
                                    mEvents.add(e);
                                    l.AddEntity((events.length() == 6));

                                }

                            }

                            mLoadMore[2] = (events.length() == 6);

                            break;
                        case USER:

                            JSONArray users = response.getJSONArray("users");

                            for (int i = 0; i < 5; i++) {
                                if (users.length() > i) {
                                    Entity e = new Entity(users.getJSONObject(i));
                                    mUsers.add(e);
                                    l.AddEntity((users.length() == 6));

                                }
                            }

                            mLoadMore[0] = (users.length() == 6);

                            break;
                        case GROUP:

                            JSONArray groups = response.getJSONArray("groups");

                            for (int i = 0; i < 5; i++) {
                                if (groups.length() > i) {
                                    Entity e = new Entity(groups.getJSONObject(i));
                                    mGroups.add(e);
                                    l.AddEntity((groups.length() == 6));

                                }


                            }

                            mLoadMore[1] = (groups.length() == 6);

                            break;
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }


    public int getSize() {

        return mUsers.size() + mEvents.size() + mGroups.size() + 3;
    }

    public interface onUpdateListener {
        void onUpdate();

    }

    public interface onSearchListener extends onUpdateListener {
        void onHashtagInit();

        void onHashtagSearch();
    }


    public interface onLoadMoreListener {
        void AddEntity(Boolean loadMore);

    }

}

package com.main.lets.lets.Adapters;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.main.lets.lets.LetsAPI.Calls;
import com.main.lets.lets.LetsAPI.Entity;
import com.main.lets.lets.LetsAPI.Search;
import com.main.lets.lets.LetsAPI.User;
import com.main.lets.lets.LetsAPI.UserData;
import com.main.lets.lets.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import cz.msebera.android.httpclient.Header;

/**
 * Created by novosejr on 12/31/2016.
 */
public class InviteFriendsToGroupAdapter extends RecyclerView.Adapter<InviteFriendsToGroupAdapter.EntityHolder> {

    AppCompatActivity mActivity;
    ArrayList<Entity> mFeed;
    EntityHolder mHolder;

    public InviteFriendsToGroupAdapter(AppCompatActivity a, ArrayList<Entity> f) {
        mActivity = a;
        mFeed = f;
    }


    @Override
    public EntityHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new EntityHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_invite_friends_element, parent,
                        false), mFeed, mActivity);
    }

    @Override
    public void onBindViewHolder(EntityHolder holder, int position) {

        mHolder = holder;
        holder.mTitle.setText("Groups are better with friends so you should invite some to join your's");
        holder.loadFeed("");

    }

    @Override
    public int getItemCount() {
        return 1;
    }



    public void notifyInvite(int groupID, final User.OnLoadListener l) {

        final Iterator<Entity> iterator = mHolder.mChecked.iterator();

        while (iterator.hasNext()) {

            Entity e = iterator.next();
            Calls.joinGroup(e.mID, groupID, new UserData(mActivity), new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    if (!iterator.hasNext())
                        l.update();
                }
            });


        }

    }


    public class EntityHolder extends RecyclerView.ViewHolder {
        ArrayList<Entity> mChecked = new ArrayList<>();
        InviteFriendsToGroupEntityAdapter mAdapter;
        TextView mTitle;
        RecyclerView mRecyclerView;
        ArrayList<Entity> mFeed;
        AppCompatActivity mActivity;
        SearchView mSearch;

        public EntityHolder(View itemView, ArrayList<Entity> f, AppCompatActivity a) {
            super(itemView);

            mFeed = f;
            mActivity = a;
            mTitle = (TextView) itemView.findViewById(R.id.title);
            mSearch = (SearchView) itemView.findViewById(R.id.search);
            mRecyclerView = (RecyclerView) itemView.findViewById(R.id.view);

            mSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    loadFeed(newText);
                    return false;
                }
            });

        }

        public void loadFeed(String filter) {

            ArrayList<Entity> feed = new ArrayList<>();

            for (Entity e: mFeed) {
                if (e.mText.contains(filter))
                    feed.add(e);
            }

            mRecyclerView.setLayoutManager(new GridLayoutManager(mActivity, 1));
            mAdapter = new InviteFriendsToGroupEntityAdapter(feed, mChecked, mActivity,
                    new InviteFriendsToGroupEntityAdapter.OnFriendCheckedListener() {

                @Override
                public void onChecked(boolean b, Entity e) {
                    if (b)
                        mChecked.add(e);
                    else
                        mChecked.remove(e);

                }
            });

            mRecyclerView.setAdapter(mAdapter);

        }

    }

}

package com.main.lets.lets.Adapters;

import android.graphics.Typeface;
import android.opengl.Visibility;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.main.lets.lets.LetsAPI.Entity;
import com.main.lets.lets.LetsAPI.L;
import com.main.lets.lets.LetsAPI.Notifications;
import com.main.lets.lets.LetsAPI.Search;
import com.main.lets.lets.R;

import java.util.ArrayList;

/**
 * Created by novosejr on 12/17/2016.
 */
public class NewSearchAdapter extends RecyclerView.Adapter<NewSearchAdapter.SearchEntityHolder> {
    Search mSearch;
    AppCompatActivity mActivity;
    String[] mFeeds = {"Events", "People", "Groups"};

    enum SearchType {SEARCH, NOTIFICATIONS}

    SearchType mSearchType = SearchType.SEARCH;

    public NewSearchAdapter(AppCompatActivity a, Search s) {
        mActivity = a;
        mSearch = s;

        if (s.getClass().equals(Notifications.class)) {
            mFeeds = new String[]{"Event Invites", "Friend Requests", "Group Invites"};
            mSearchType = SearchType.NOTIFICATIONS;

        }
    }


    @Override
    public SearchEntityHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new SearchEntityHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_search_element, parent,
                        false), mSearch, mActivity);
    }

    @Override
    public void onBindViewHolder(SearchEntityHolder holder, int position) {

        holder.mTitle.setText(mFeeds[position]);
        holder.loadFeed(mFeeds[position]);

    }

    @Override
    public int getItemCount() {
        return 3;
    }


    public class SearchEntityHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mTitle;
        TextView mLoadMoreText;
        RelativeLayout mLoadMore;
        RecyclerView mRecyclerView;
        Search mSearch;
        ArrayList<Entity> mFeed;
        AppCompatActivity mActivity;
        String mEntityType;
        SearchEntityAdapter mAdapter;
        ProgressBar mLoading;

        String[] mEmptyFeed = {"No events with this name", "No people with this name", "No groups with this name"};

        public SearchEntityHolder(View itemView, Search s, AppCompatActivity a) {
            super(itemView);

            mSearch = s;
            mActivity = a;
            mTitle = (TextView) itemView.findViewById(R.id.title);
            mLoadMoreText = (TextView) itemView.findViewById(R.id.txt_load_more);
            mLoadMore = (RelativeLayout) itemView.findViewById(R.id.load_more);
            mRecyclerView = (RecyclerView) itemView.findViewById(R.id.view);
            mLoading = (ProgressBar) itemView.findViewById(R.id.load_circle);

        }

        public void loadFeed(String s) {
            mEntityType = s;

            if (mSearchType == SearchType.NOTIFICATIONS) {
                mLoadMore.setVisibility(View.GONE);
                mEmptyFeed = new String[] {"No event invites", "No friend requests", "No group invites"};
            }

            if (s.equalsIgnoreCase(mFeeds[0])) {
                mFeed = mSearch.mEvents;

                if (!mSearch.mLoadMore[0])
                    mLoadMore.setVisibility(View.GONE);

                if (mFeed.size() == 0) {
                    loadEmptyFeed(mEmptyFeed[0]);

                }

            }else if (s.equalsIgnoreCase(mFeeds[1])) {
                mFeed = mSearch.mUsers;

                if (!mSearch.mLoadMore[1])
                    mLoadMore.setVisibility(View.GONE);

                if (mFeed.size() == 0) {
                    loadEmptyFeed(mEmptyFeed[1]);

                }

            }else {
                mFeed = mSearch.mGroups;

                if (!mSearch.mLoadMore[2])
                    mLoadMore.setVisibility(View.GONE);

                if (mFeed.size() == 0) {
                   loadEmptyFeed(mEmptyFeed[2]);

                }
            }

            mLoadMore.setOnClickListener(this);

            mRecyclerView.setLayoutManager(new GridLayoutManager(mActivity, 1));
            mAdapter = new SearchEntityAdapter(mFeed, mActivity);
            mRecyclerView.setAdapter(mAdapter);

        }




        public void loadEmptyFeed(String s) {
            mLoadMore.setVisibility(View.VISIBLE);
            mLoadMoreText.setText(s);
            mLoadMoreText.setTypeface(mLoadMoreText.getTypeface(), Typeface.ITALIC);
        }

        @Override
        public void onClick(View v) {

            mLoading.setAlpha(1);

            Search.onLoadMoreListener l = new Search.onLoadMoreListener() {
                @Override
                public void AddEntity(Boolean loadMore) {
                    mAdapter.addElement();

                    if (!loadMore)
                        mLoadMore.setVisibility(View.GONE);

                    mLoading.setAlpha(0);

                }

            };

            switch (mEntityType) {
                case "Events":
                    mSearch.loadMore(Search.Feed.EVENT, l);
                    break;
                case "People":
                    mSearch.loadMore(Search.Feed.USER, l);
                    break;
                default:
                    mSearch.loadMore(Search.Feed.GROUP, l);
                    break;
            }

        }
    }

}

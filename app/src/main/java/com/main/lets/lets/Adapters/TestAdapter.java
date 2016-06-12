package com.main.lets.lets.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;

import com.main.lets.lets.R;
import com.marshalchen.ultimaterecyclerview.UltimateGridLayoutAdapter;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;

/**
 * Created by Joe on 6/12/2016.
 */
public class TestAdapter extends UltimateViewAdapter<TestAdapter.ViewHolder> {

    @Override
    public TestAdapter.ViewHolder newFooterHolder(View view) {
        return null;
    }

    @Override
    public TestAdapter.ViewHolder newHeaderHolder(View view) {
        return null;
    }

    @Override
    public TestAdapter.ViewHolder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_entity_with_space, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public int getAdapterItemCount() {
        return 10;
    }

    @Override
    public long generateHeaderId(int position) {
        return 0;
    }

    @Override
    public void onBindViewHolder(TestAdapter.ViewHolder holder, int position) {

    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return null;
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    public class ViewHolder extends UltimateRecyclerviewViewHolder{
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}

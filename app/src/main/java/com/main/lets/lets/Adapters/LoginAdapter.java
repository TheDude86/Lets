package com.main.lets.lets.Adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.main.lets.lets.R;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;

/**
 * Created by Joe on 12/13/2015.
 */
public class LoginAdapter extends UltimateViewAdapter<LoginAdapter.ViewHolder> {
    OnItemClickListener mItemClickListener;
    Activity mActivity;


    public LoginAdapter(Activity context) {
        mActivity = context;

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_login, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public ViewHolder newFooterHolder(View view) {
        return null;
    }

    @Override
    public ViewHolder newHeaderHolder(View view) {
        return null;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        return null;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return null;
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 1;
    }

    @Override
    public int getAdapterItemCount() {
        return 0;
    }

    @Override
    public long generateHeaderId(int position) {
        return 0;
    }

    protected class ViewHolder extends UltimateRecyclerviewViewHolder{
        public TextView mPassword;
        public TextView mEmail;
        public Button mLogin;
        public Button mReg;

        public ViewHolder(View itemView) {
            super(itemView);

            mPassword = (TextView) itemView.findViewById(R.id.txt_pass);
            mEmail = (TextView) itemView.findViewById(R.id.txt_login);
            mLogin = (Button) itemView.findViewById(R.id.btn_login);
            mReg = (Button) itemView.findViewById(R.id.btn_reg);

            mLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClick(mEmail.getText().toString(),
                                mPassword.getText().toString());
                    }

                }
            });


            mReg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

        }

    }

    public interface OnItemClickListener {
        void onItemClick(String login, String password);
    }

    public void setOnLoginClick(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

}

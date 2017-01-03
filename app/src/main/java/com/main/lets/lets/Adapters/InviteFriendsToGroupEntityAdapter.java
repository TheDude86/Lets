package com.main.lets.lets.Adapters;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.main.lets.lets.LetsAPI.Entity;
import com.main.lets.lets.LetsAPI.EntityOnClickListener;
import com.main.lets.lets.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by novosejr on 12/31/2016.
 */
public class InviteFriendsToGroupEntityAdapter extends RecyclerView.Adapter<InviteFriendsToGroupEntityAdapter.EntityHolder> {
    ArrayList<Entity> mFeed;
    ArrayList<Entity> mChecked;
    AppCompatActivity mActivity;
    OnFriendCheckedListener mListener;

    public InviteFriendsToGroupEntityAdapter(ArrayList<Entity> e, ArrayList<Entity> c, AppCompatActivity a, OnFriendCheckedListener l) {

        mFeed = e;
        mChecked = c;
        mActivity = a;
        mListener = l;

    }

    @Override
    public EntityHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new EntityHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_invite_friend_to_group, parent,
                        false));
    }

    @Override
    public void onBindViewHolder(EntityHolder holder, int position) {

        final Entity e = mFeed.get(position);

        holder.mTitle.setText(e.mText);
        holder.mDesc.setText(e.mDetail);

        e.loadImage(mActivity, holder.mImageView);

        if (mChecked.contains(e))
            holder.mChecked.setChecked(true);

        holder.mChecked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                mListener.onChecked(isChecked, e);

            }
        });

    }

    @Override
    public int getItemCount() {
        return mFeed.size();
    }

    public void addElement() {
        notifyItemInserted(mFeed.size() - 1);
    }

    public interface OnFriendCheckedListener {
        void onChecked(boolean b, Entity e);
    }

    public class EntityHolder extends RecyclerView.ViewHolder {
        RelativeLayout mRelativeLayout;
        CircleImageView mImageView;
        CheckBox mChecked;
        TextView mTitle;
        TextView mDesc;


        public EntityHolder(View itemView) {
            super(itemView);

            mRelativeLayout = (RelativeLayout) itemView.findViewById(R.id.layout_info);
            mTitle = (TextView) itemView.findViewById(R.id.txt_entity_title);
            mDesc = (TextView) itemView.findViewById(R.id.txt_entity_detail);
            mImageView = (CircleImageView) itemView.findViewById(R.id.image);
            mChecked = (CheckBox) itemView.findViewById(R.id.invite);

        }
    }
}

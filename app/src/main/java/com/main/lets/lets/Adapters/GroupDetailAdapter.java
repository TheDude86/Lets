package com.main.lets.lets.Adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.main.lets.lets.Holders.EntityViewHolder;
import com.main.lets.lets.Holders.GroupDetailViewHolder;
import com.main.lets.lets.Holders.PictureViewHolder;
import com.main.lets.lets.LetsAPI.Calls;
import com.main.lets.lets.LetsAPI.Entity;
import com.main.lets.lets.LetsAPI.Group;
import com.main.lets.lets.R;
import com.rey.material.widget.CheckBox;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

/**
 * Created by jnovosel on 7/2/16.
 */
public class GroupDetailAdapter extends FeedAdapter {
    public OnCommentsClickListener mCommentsClickListener;
    public OnMembersClickListener mMembersClickListener;
    public OnEntityClickListener mEntityClickListener;

    public enum Status {GUEST, INVITE, MEMBER, ADMIN, OWNER}

    public HashMap<String, TextView> mActions;
    public GroupDetailViewHolder mHolder;
    public Status mStatus = Status.GUEST;
    public OnDraw mDraw;

    public GroupDetailAdapter(AppCompatActivity a, String s, int id) {
        mList = new ArrayList<>();
        mActivity = a;
        mList.add(s);

        try {
            if (new JSONObject(s).getInt("god") == id) {
                mStatus = Status.OWNER;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void updateStatus(Status s) {
        if (mStatus != Status.OWNER)
            mStatus = s;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0)
            return new GroupDetailViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_group_detail, parent, false));

        return new PictureViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_entity_with_picture, parent, false));
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position == 0)
            loadGroupInfo((GroupDetailViewHolder) holder);
        else
            super.onBindViewHolder(holder, position);


    }

    private void loadEntityInfo(EntityViewHolder holder, final int index) {
        try {
            Entity e = new Entity(new JSONObject(mList.get(index)));
            holder.mTitle.setText(e.mText);
            holder.mLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mEntityClickListener != null)
                        mEntityClickListener.onClick(index - 1);
                }
            });

        } catch (JSONException e1) {
            holder.mTitle.setText(mList.get(index));
        }

    }

    private void loadGroupInfo(final GroupDetailViewHolder holder) {
        mHolder = holder;

        try {
            JSONObject j = new JSONObject(mList.get(0));

            Picasso.with(mActivity).load(j.getString("pic_ref")).into(holder.mImage);

            mActions = new HashMap<>();
            mActions.put("transfer", formattedTextView("Transfer Ownership"));
            mActions.put("delete", formattedTextView("Delete Group"));

            mActions.put("remove members", formattedTextView("Remove Members"));
            mActions.put("remove admins", formattedTextView("Remove Admins"));
            mActions.put("add admins", formattedTextView("Add Admins"));
            mActions.put("edit", formattedTextView("Edit Group"));

            mActions.put("invite group", formattedTextView("Invite to Event"));
            mActions.put("invite users", formattedTextView("Invite Friends"));
            mActions.put("leave", formattedTextView("Leave Group"));
            mActions.put("comment", formattedTextView("Comment"));

            mDraw.draw(mActions);

            holder.mName.setText(j.getString("group_name"));
            holder.mBio.setText(j.getString("bio"));

            setComments(holder.mComments);
            setUsers(holder.mMembers);

            switch (mStatus) {
                case GUEST:
                    holder.mActions.setVisibility(View.GONE);

                    break;
                case MEMBER:
                    loadMemberActions(holder);

                    break;
                case ADMIN:
                    loadAdminActions(holder);

                    break;
                case OWNER:
                    loadOwnerActions(holder);

                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * Toggles whether the user can edit the group or not.
     *
     * @return if the group is editable, it returns true, if the group is not editable then it
     * returns false.
     */
    public boolean toggleEditable() {
        if (mHolder.mName.isFocusable()) {
            mHolder.mName.setFocusableInTouchMode(false);
            mHolder.mName.setFocusable(false);
            mHolder.mBio.setFocusableInTouchMode(false);
            mHolder.mBio.setFocusable(false);

            mHolder.mName.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
            mHolder.mBio.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

            mHolder.mMainLayout.removeAllViews();

            return false;
        }

        mHolder.mName.setFocusableInTouchMode(true);
        mHolder.mName.setFocusable(true);
        mHolder.mBio.setFocusableInTouchMode(true);
        mHolder.mBio.setFocusable(true);

        try {
            Group g = new Group( new JSONObject(mList.get(0)));
            LinearLayout l = new LinearLayout(mActivity);
            RadioButton publicGroup = new RadioButton(mActivity);
            RadioButton hiddenGroup = new RadioButton(mActivity);
            RadioButton openGroup = new RadioButton(mActivity);
            RadioButton requestGroup = new RadioButton(mActivity);
            RadioButton inviteGroup = new RadioButton(mActivity);
            RadioGroup visibilityGroup = new RadioGroup(mActivity);
            RadioGroup accessGroup = new RadioGroup(mActivity);
            TextView visibilityText = new TextView(mActivity);
            TextView accessText = new TextView(mActivity);

            accessGroup.addView(openGroup);
            accessGroup.addView(requestGroup);
            accessGroup.addView(inviteGroup);

            visibilityGroup.addView(publicGroup);
            visibilityGroup.addView(hiddenGroup);

            openGroup.setText("Open");
            requestGroup.setText("On Request");
            inviteGroup.setText("Invite Only");

            publicGroup.setText("Public");
            hiddenGroup.setText("Hidden");

            if (g.isHidden())
                publicGroup.setChecked(true);
            else
                hiddenGroup.setChecked(true);

            if(g.isPublic())
                openGroup.setChecked(true);
            else
                inviteGroup.setChecked(true);


            accessText.setText("Set Group Joining Options");
            visibilityText.setText("Set Group Visibility");

            l.setOrientation(LinearLayout.VERTICAL);
            l.addView(visibilityText);
            l.addView(visibilityGroup);
            l.addView(accessText);
            l.addView(accessGroup);
            mHolder.mMainLayout.addView(l);
            mHolder.mName.getBackground().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
            mHolder.mBio.getBackground().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return true;
    }

    public void addElement(String s) {
        mList.add(s);
        notifyItemInserted(mList.size() - 1);
        notifyDataSetChanged();
    }

    public void clearFeed() {
        int end = mList.size();

        for (int i = 1; i < end; i++) {
            mList.remove(1);
            notifyItemRemoved(1);
            notifyItemRangeChanged(1, mList.size());
        }

    }

    public void loadOwnerActions(GroupDetailViewHolder holder) {
        loadAdminActions(holder);

        holder.mActionsList.addView(mActions.get("transfer"));
        holder.mActionsList.addView(mActions.get("delete"));
    }

    public void loadAdminActions(GroupDetailViewHolder holder) {
        holder.mActionsList.addView(mActions.get("create event"));
        holder.mActionsList.addView(mActions.get("remove members"));
        holder.mActionsList.addView(mActions.get("remove admins"));
        holder.mActionsList.addView(mActions.get("add admins"));
        holder.mActionsList.addView(mActions.get("edit"));

        loadMemberActions(holder);

    }

    public void loadMemberActions(GroupDetailViewHolder holder) {
        holder.mActionsList.addView(mActions.get("invite users"));
        holder.mActionsList.addView(mActions.get("invite group"));
        holder.mActionsList.addView(mActions.get("comment"));
        holder.mActionsList.addView(mActions.get("leave"));

    }

    public TextView formattedTextView(String s) {
        TextView t = new TextView(mActivity);
        t.setText(s);
        t.setGravity(Gravity.CENTER);
        t.setPadding(45, 0, 45, 0);
        LayoutParams p = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.MATCH_PARENT);

        t.setLayoutParams(p);

        return t;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setOnMembersClickListener(OnMembersClickListener m) {
        mMembersClickListener = m;
    }

    public void setOnCommentsClickListener(OnCommentsClickListener c) {
        mCommentsClickListener = c;
    }

    public void setOnEntityClickListener(OnEntityClickListener e) {
        mEntityClickListener = e;
    }

    public void setOnDraw(OnDraw d) {
        mDraw = d;
    }

    public interface OnDraw {
        void draw(HashMap<String, TextView> actions);
    }

    public interface OnEntityClickListener {
        void onClick(int position);
    }

    public interface OnMembersClickListener {
        void onClick();
    }

    public interface OnCommentsClickListener {
        void onClick();
    }

}

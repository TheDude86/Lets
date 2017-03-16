package com.main.lets.lets.Adapters;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.main.lets.lets.LetsAPI.Chat;
import com.main.lets.lets.LetsAPI.L;
import com.main.lets.lets.LetsAPI.UserData;
import com.main.lets.lets.R;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by novosejr on 3/9/2017.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatHolder> {
    public ArrayList<Chat> mChats = new ArrayList<>();
    public ElementAddedListener mListener;
    public AppCompatActivity mActivity;

    UserData mUser;


    public ChatAdapter(AppCompatActivity a, String d) {
        mActivity = a;
        mUser = new UserData(mActivity);

        FirebaseDatabase.getInstance().getReference().child(d).orderByChild("timestamp").limitToLast(50).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Chat c = dataSnapshot.getValue(Chat.class);
                mChats.add(c);

                notifyItemInserted(mChats.size());

                if (mListener != null)
                    mListener.onElementAdded(mChats.size());

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

    @Override
    public ChatHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ChatHolder c = new ChatHolder(LayoutInflater.from(parent.getContext())
                .inflate((viewType == 1) ? R.layout.row_chat_bubble : R.layout.row_other_chat_bubble,
                        parent, false));

        return c;
    }

    @Override
    public int getItemViewType(int position) {
        Chat c = mChats.get(position);

        return (c.getId() == mUser.ID) ? 1 : 2;
    }

    @Override
    public void onBindViewHolder(ChatHolder holder, int position) {

        Chat c = mChats.get(position);
        int vt = getItemViewType(position);

        holder.mText.setText(c.getMessage());

        if (vt == 1) {
            int red = 238;
            int green = 238;
            int blue = 238;

            GradientDrawable bgShape = (GradientDrawable) holder.mBackground.getBackground();
            bgShape.setColor(getIntFromColor(red, green, blue));
        } else {
            int red = c.getId() * 100003;
            int green = c.getId() * 484459;
            int blue = c.getId() * 920209;

            GradientDrawable bgShape = (GradientDrawable) holder.mBackground.getBackground();
            bgShape.setColor(getIntFromColor(red, green, blue));
            holder.mAuthor.setText(c.getAuthor());

            if (consecutiveMessages(holder.getAdapterPosition())) {
                holder.mAuthor.setVisibility(View.GONE);
            } else {
                holder.mAuthor.setVisibility(View.VISIBLE);
            }

        }

    }

    public boolean consecutiveMessages(int i) {
        if (i == 0) return false;

        Chat c1 = mChats.get(i);
        Chat c2 = mChats.get(i - 1);

        if (c1.getId() == c2.getId()) return true;

        return false;
    }

    public int getIntFromColor(int Red, int Green, int Blue) {
        Red = (Red << 16) & 0x00FF0000; //Shift red 16-bits and mask out other stuff
        Green = (Green << 8) & 0x0000FF00; //Shift Green 8-bits and mask out other stuff
        Blue = Blue & 0x000000FF; //Mask out anything not blue.

        return 0xFF000000 | Red | Green | Blue; //0xFF000000 for 100% Alpha. Bitwise OR everything together.
    }

    public void setElementAddedListener(ElementAddedListener e) {
        mListener = e;
    }

    @Override
    public int getItemCount() {
        return mChats.size();
    }

    class ChatHolder extends RecyclerView.ViewHolder {
        RelativeLayout mBackground;
        TextView mAuthor;
        TextView mText;

        public ChatHolder(View itemView) {
            super(itemView);

            mBackground = (RelativeLayout) itemView.findViewById(R.id.background);
            mText = (TextView) itemView.findViewById(R.id.text);

            View v = itemView.findViewById(R.id.author);
            if (v != null)
                mAuthor = (TextView) v;

        }
    }

    public interface ElementAddedListener {
        void onElementAdded(int position);
    }

}

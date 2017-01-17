package com.main.lets.lets.Adapters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.main.lets.lets.Activities.NewSearchActivity;
import com.main.lets.lets.Activities.RegisterActivity;
import com.main.lets.lets.Activities.UserDetailActivity;
import com.main.lets.lets.LetsAPI.Calls;
import com.main.lets.lets.LetsAPI.Entity;
import com.main.lets.lets.LetsAPI.Event;
import com.main.lets.lets.LetsAPI.EventEntity;
import com.main.lets.lets.LetsAPI.IntentDecorator;
import com.main.lets.lets.LetsAPI.L;
import com.main.lets.lets.LetsAPI.User;
import com.main.lets.lets.LetsAPI.UserData;
import com.main.lets.lets.R;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by novosejr on 12/18/2016.
 */
public class ProfilePreviewAdapter extends UltimateViewAdapter<UltimateRecyclerviewViewHolder> {
    AppCompatActivity mActivity;
    boolean mLoggedIn = false;
    EventHolder mFuture;
    EventHolder mPast;
    MainHolder mMain;
    int mCount = 1;
    User mUser;
    int mID;

    public ProfilePreviewAdapter(final AppCompatActivity a, int i) {
        mActivity = a;
        mID = i;

        if (!(new UserData(a)).ShallonCreamerIsATwat.equals("")) {
            mLoggedIn = true;
            loadUserProfile(a, i);
            mCount = 3;
        }

    }

    public void loadUserProfile(final AppCompatActivity a, int i) {

        final ProgressDialog dialog = ProgressDialog.show(mActivity, "",
                                                          "Loading. Please wait...", true);

        mUser = new User(i);
        mUser.load(a, new User.OnLoadListener() {

            @Override
            public void update() {

                dialog.hide();

                try {
                    mUser.loadImage(a, mMain.mImage);
                    mMain.mName.setText(mUser.getName());


                    for (EventEntity e : mUser.mEvents) {

                        if (e.mEnd.before(Calendar.getInstance().getTime())) {
                            mPast.mAdapter.mFeed.add(e);
                            mPast.mNoEvents.setVisibility(View.GONE);

                        } else {
                            mFuture.mAdapter.mFeed.add(0, e);
                            mFuture.mNoEvents.setVisibility(View.GONE);

                        }

                    }

                    mFuture.mAdapter.notifyDataSetChanged();
                    mPast.mAdapter.notifyDataSetChanged();





                    mPast.mNoEventsText.setText("No past events");
                    mFuture.mNoEventsText.setText("No upcoming events");


                }catch (NullPointerException e) {

                }



            }

        });

    }


    @Override
    public UltimateRecyclerviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (!mLoggedIn)
            return new LoginHolder(LayoutInflater.from(parent.getContext())
                                           .inflate(R.layout.row_login, parent,
                                                    false));

        if (viewType == 0)
            return new MainHolder(LayoutInflater.from(parent.getContext())
                                          .inflate(R.layout.row_profile_preview, parent,
                                                   false));


        return new EventHolder(LayoutInflater.from(parent.getContext())
                                       .inflate(R.layout.row_profile_preview_events, parent,
                                                false));

    }

    @Override
    public UltimateRecyclerviewViewHolder newFooterHolder(View view) {
        return null;
    }

    @Override
    public UltimateRecyclerviewViewHolder newHeaderHolder(View view) {
        return null;
    }

    @Override
    public UltimateRecyclerviewViewHolder onCreateViewHolder(ViewGroup parent) {
        return null;
    }

    @Override
    public void onBindViewHolder(UltimateRecyclerviewViewHolder holder, int position) {

        if (position == 0) {

            if (mLoggedIn)
                mMain = (MainHolder) holder;

        } else if (position == 1) {
            mFuture = (EventHolder) holder;

        } else if (position == 2) {
            mPast = (EventHolder) holder;
            mPast.mTitle.setText("Past Events");
        }


    }

    @Override
    public int getItemViewType(int position) {

        return position;
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
        return mCount;
    }

    @Override
    public int getAdapterItemCount() {
        return 0;
    }

    @Override
    public long generateHeaderId(int position) {
        return 0;
    }


    private class MainHolder extends UltimateRecyclerviewViewHolder {
        CircleImageView mImage;
        TextView mName;

        MainHolder(View itemView) {
            super(itemView);

            mImage = (CircleImageView) itemView.findViewById(R.id.picture);
            mName = (TextView) itemView.findViewById(R.id.name);

            JSONObject j = new JSONObject();
            try {
                j.put("ID", (new UserData(mActivity)).ID);
                (new IntentDecorator(mActivity, UserDetailActivity.class)).fillJSON(j)
                        .decorate(itemView.findViewById(R.id.layout));
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

    }

    private class EventHolder extends UltimateRecyclerviewViewHolder {
        SearchEntityAdapter mAdapter;
        RecyclerView mRecyclerView;
        RelativeLayout mNoEvents;
        RelativeLayout mLayout;
        TextView mNoEventsText;
        TextView mTitle;

        EventHolder(View itemView) {
            super(itemView);

            mTitle = (TextView) itemView.findViewById(R.id.title);
            mRecyclerView = (RecyclerView) itemView.findViewById(R.id.view);
            mNoEvents = (RelativeLayout) itemView.findViewById(R.id.no_events_layout);
            mNoEventsText = (TextView) itemView.findViewById(R.id.no_events);
            mLayout = (RelativeLayout) itemView.findViewById(R.id.layout);

            (new IntentDecorator(mActivity, NewSearchActivity.class)).decorate(mNoEvents);

            mRecyclerView.setLayoutManager(new GridLayoutManager(mActivity, 1));
            mAdapter = new SearchEntityAdapter(new ArrayList<Entity>(), mActivity);
            mRecyclerView.setAdapter(mAdapter);

        }

    }

    private class LoginHolder extends UltimateRecyclerviewViewHolder {
        EditText mEmail;
        EditText mPassword;
        Button mLogin;
        Button mRegister;

        LoginHolder(View itemView) {
            super(itemView);

            mPassword = (EditText) itemView.findViewById(R.id.txt_pass);
            mEmail = (EditText) itemView.findViewById(R.id.txt_login);
            mRegister = (Button) itemView.findViewById(R.id.btn_reg);
            mLogin = (Button) itemView.findViewById(R.id.btn_login);


            (new IntentDecorator(mActivity, RegisterActivity.class)).decorate(mRegister);

            mLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Calls.login(mEmail.getText().toString(), mPassword.getText().toString(),
                                new JsonHttpResponseHandler() {

                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers,
                                                          JSONObject response) {

                                        if (response.has("accessToken")) {
                                            try {
                                                SharedPreferences preferences = PreferenceManager
                                                        .getDefaultSharedPreferences(mActivity);

                                                SharedPreferences.Editor editor = preferences
                                                        .edit();
                                                editor.putInt("UserID", response.getInt("user_id"));
                                                editor.putString("Token", "Bearer " + response
                                                        .getString("accessToken"));
                                                editor.putString("email",
                                                                 mEmail.getText().toString());
                                                editor.putString("password",
                                                                 mPassword.getText().toString());
                                                editor.apply();

                                                Intent intent = new Intent(mActivity,
                                                                           UserDetailActivity.class);

                                                intent.putExtra("UserID", response.getInt("user_id"));
                                                mActivity.startActivity(intent);


                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        } else {

                                            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                                            builder.setTitle("You shall not pass");
                                            builder.setMessage("Your email or password is wrong, you should look into that");
                                            builder.setPositiveButton("Okay", null);
                                            builder.create().show();

                                        }


                                    }
                                });

                }
            });


        }


    }


}

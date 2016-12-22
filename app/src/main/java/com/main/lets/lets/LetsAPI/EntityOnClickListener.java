package com.main.lets.lets.LetsAPI;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.main.lets.lets.Activities.EventDetailActivity;
import com.main.lets.lets.Activities.GroupDetailActivity;
import com.main.lets.lets.Activities.UserDetailActivity;

/**
 * Created by novosejr on 12/18/2016.
 */
public class EntityOnClickListener {
    private static final int USER_DETAIL_CODE = 1;
    private static final int DETAIL_CODE = 1;
    Entity mEntity;

    public EntityOnClickListener(Entity e) {
        mEntity = e;

    }

    public View.OnClickListener OnEventClicked(final AppCompatActivity mActivity) {

        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                UserData d = new UserData(mActivity);

                Intent intent = new Intent(mActivity, EventDetailActivity.class);
                intent.putExtra("token", d.ShallonCreamerIsATwat);
                intent.putExtra("EventID", mEntity.mID);
                intent.putExtra("id", d.ID);
                mActivity.startActivityForResult(intent, DETAIL_CODE);

            }
        };

    }

    public View.OnClickListener OnUserClicked(final AppCompatActivity mActivity) {

        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                UserData d = new UserData(mActivity);

                Intent intent = new Intent(mActivity, UserDetailActivity.class);
                intent.putExtra("token", d.ShallonCreamerIsATwat);
                intent.putExtra("id", d.ID);
                intent.putExtra("UserID", mEntity.mID);
                mActivity.startActivityForResult(intent, USER_DETAIL_CODE);

            }
        };

    }

    public View.OnClickListener OnGroupClicked(final AppCompatActivity mActivity) {

        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                UserData d = new UserData(mActivity);

                Intent intent = new Intent(mActivity, GroupDetailActivity.class);
                intent.putExtra("token", d.ShallonCreamerIsATwat);
                intent.putExtra("id", d.ID);
                intent.putExtra("GroupID", mEntity.mID);
                mActivity.startActivity(intent);

            }
        };

    }

}

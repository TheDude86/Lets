package com.main.lets.lets.LetsAPI;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.main.lets.lets.Activities.InviteActivity;
import com.main.lets.lets.Activities.MainActivity;
import com.main.lets.lets.Activities.NewSearchActivity;
import com.main.lets.lets.Activities.RegisterActivity;
import com.main.lets.lets.Activities.UserDetailActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Joe on 12/22/2016.
 */
public class IntentDecorator extends ViewDecorator{
    HashMap<String, View.OnClickListener> mListeners = new HashMap<>();
    JSONObject mInfo = new JSONObject();
    AppCompatActivity mActivity;
    Class mClass;

    public IntentDecorator(ViewDecorator d, AppCompatActivity a, Class c) {
        super(d);

        mActivity = a;
        mClass = c;

        makeIntentListeners();

    }

    public IntentDecorator(AppCompatActivity a, Class c) {
        super();

        mActivity = a;
        mClass = c;

        makeIntentListeners();

    }

    public IntentDecorator fillJSON(JSONObject j) {

        mInfo = j;

        return this;
    }


    public void makeIntentListeners() {
        mListeners.put("NewSearchActivity", new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mActivity, NewSearchActivity.class);
                mActivity.startActivity(intent);

            }
        });

        mListeners.put("UserDetailActivity", new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mActivity, UserDetailActivity.class);

                try {
                    intent.putExtra("UserID", mInfo.getInt("ID"));
                    mActivity.startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        mListeners.put("RegisterActivity", new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mActivity, RegisterActivity.class);
                mActivity.startActivity(intent);

            }
        });

        mListeners.put("InviteActivity", new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mActivity, InviteActivity.class);
                try {
                    intent.putExtra("invite_id", mInfo.getInt("invite_id"));
                    intent.putExtra("entities", mInfo.getString("entities"));
                    intent.putExtra("mode", mInfo.getString("mode"));
                    mActivity.startActivity(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        });

    }


    @Override
    public View decorate(View v) {

        if (mDecorator != null)
           mDecorator.decorate(v);

        if (mListeners.containsKey(mClass.getSimpleName())) {
            View.OnClickListener l = mListeners.get(mClass.getSimpleName());
            if (l != null)
                v.setOnClickListener(l);

        }

        return v;
    }
}

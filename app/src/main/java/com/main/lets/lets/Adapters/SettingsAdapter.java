package com.main.lets.lets.Adapters;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.main.lets.lets.LetsAPI.UserData;
import com.main.lets.lets.R;

/**
 * Created by novosejr on 1/14/2017.
 */

public class SettingsAdapter extends RecyclerView.Adapter <SettingsAdapter.SettingsViewHolder> {
    AppCompatActivity mActivity;

    public SettingsAdapter(AppCompatActivity a) {
        mActivity = a;

    }


    @Override
    public SettingsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SettingsViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_settings, parent,
                        false));
    }

    @Override
    public void onBindViewHolder(SettingsViewHolder holder, int position) {

        if (position == 0) {

            holder.mText.setText("Edit Profile");
            holder.mImage.setImageResource(R.drawable.ic_person_outline_black_24dp);
            holder.mContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent data = new Intent();
                    //If the user pressed the log out button, "loggedOut" will be true
                    data.putExtra("Edit", true);
                    mActivity.setResult(mActivity.RESULT_OK, data);
                    mActivity.finish();
                }
            });

        } else if (position == 1) {
            holder.mText.setText("Logout");
            holder.mImage.setImageResource(R.drawable.ic_power_settings_new_black_24dp);
            holder.mContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    UserData d = new UserData(mActivity);
                    d.clear();

                    Intent data = new Intent();
                    //If the user pressed the log out button, "loggedOut" will be true
                    data.putExtra("LoggedOut", true);
                    mActivity.setResult(mActivity.RESULT_OK, data);
                    mActivity.finish();
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return 2;
    }

    class SettingsViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout mContainer;
        ImageView mImage;
        TextView mText;

        SettingsViewHolder(View itemView) {
            super(itemView);

            mContainer = (RelativeLayout) itemView.findViewById(R.id.container);
            mImage = (ImageView) itemView.findViewById(R.id.icon);
            mText = (TextView) itemView.findViewById(R.id.text);

        }
    }


}

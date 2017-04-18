package com.main.lets.lets.Adapters;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.main.lets.lets.LetsAPI.L;
import com.main.lets.lets.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by novosejr on 1/2/2017.
 */
public class JokesAdapter extends RecyclerView.Adapter <RecyclerView.ViewHolder> {
    ArrayList<JSONObject> mJokes = new ArrayList<>();

    public JokesAdapter(AppCompatActivity a) throws JSONException {
        String[] jokeArray = a.getResources().getStringArray(R.array.jokes_array);

        for (String s: jokeArray) {
            mJokes.add(new JSONObject(s));
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mJokes.size();
    }


    public class HeaderHolder extends RecyclerView.ViewHolder {

        public HeaderHolder(View itemView) {
            super(itemView);
        }


    }


}

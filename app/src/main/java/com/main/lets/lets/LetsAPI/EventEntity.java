package com.main.lets.lets.LetsAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by novosejr on 12/21/2016.
 */
public class EventEntity extends Entity {
    public Date mEnd;

    public EventEntity(JSONObject j) throws JSONException {
        super(j);

        mEnd = new Date((Long.parseLong(j.getString("end_time")
                .substring(6, j.getString("end_time").length() - 2))));

    }
}

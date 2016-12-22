package com.main.lets.lets.LetsAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by novosejr on 12/1/2016.
 */
public class Comment extends Entity{
    Date commentTime;

    public Comment(int ID, String author, String text) {
        super(ID, author, EntityType.COMMENT);
        this.mDetail = text;

    }

    public Comment(JSONObject j) throws JSONException {
        super(j.getInt("event_id"), j.getString("name"), EntityType.COMMENT);
        this.mDetail = j.getString("text");

        commentTime = new Date((Long.parseLong(j.getString("timestamp")
                .substring(6, j.getString("timestamp").length() - 2))));

    }

}

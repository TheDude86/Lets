package com.main.lets.lets.LetsAPI;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by novosejr on 12/1/2016.
 */
public class Comment extends Entity implements Comparable{
    public Date commentTime;
    public int mAuthorID;

    public Comment(int ID, String author, String text) {
        super(ID, author, EntityType.COMMENT);
        this.mDetail = text;

    }

    public Comment(JSONObject j) throws JSONException {
        super( (j.has("event_id")) ? j.getInt("event_id") : j.getInt("group_id"), j.getString("name"), EntityType.COMMENT);
        this.mDetail = j.getString("text");
        mAuthorID = j.getInt("user_id");

//        mPic = j.getString("pic_ref");

        commentTime = new Date((Long.parseLong(j.getString("timestamp")
                .substring(6, j.getString("timestamp").length() - 2))));

    }


    @Override
    public int compareTo(Object o) {
        if (o.getClass().equals(Comment.class)) {
            Comment c = (Comment) o;

            if (commentTime.before(c.commentTime))
                return -1;
            else
                return 1;

        }

        return 0;
    }
}

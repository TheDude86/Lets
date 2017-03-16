package com.main.lets.lets.LetsAPI;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by novosejr on 3/4/2017.
 */

public class Reminder implements Parcelable{
    public LatLng coords = new LatLng(0.0, 0.0);
    public Date mStart = new Date();
    public String mName = "";
    public int mID;

    public Reminder () {

    }

    public Reminder (JSONObject j) {
        try {
            mName = j.getString("Event Name");
            mStart = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").parse(j.getString("Event Start"));
            coords = new LatLng(j.getDouble("Latitude"), j.getDouble("Longitude"));
            mID = j.getInt("Event ID");


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    protected Reminder(Parcel in) {
        mName = in.readString();
        mID = in.readInt();
    }

    public static final Creator<Reminder> CREATOR = new Creator<Reminder>() {
        @Override
        public Reminder createFromParcel(Parcel in) {
            return new Reminder(in);
        }

        @Override
        public Reminder[] newArray(int size) {
            return new Reminder[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mName);
        parcel.writeInt(mID);
    }
}

package com.main.lets.lets.LetsAPI;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Joe on 12/25/2016.
 */
@SuppressWarnings("ValidFragment")
public class LetsSimpleDialog extends DialogFragment {
    String mTitle;
    String mMessage;
    AppCompatActivity mActivity;

    public LetsSimpleDialog(AppCompatActivity a, String title, String message) {

        mTitle = title;
        mMessage = message;
        mActivity = a;

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

        builder.setTitle(mTitle);
        builder.setMessage(mMessage);

        return builder.create();
    }


}

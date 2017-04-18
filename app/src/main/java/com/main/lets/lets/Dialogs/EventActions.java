package com.main.lets.lets.Dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.main.lets.lets.Activities.CollageActivity;
import com.main.lets.lets.Activities.InviteActivity;
import com.main.lets.lets.Activities.UserDetailActivity;
import com.main.lets.lets.LetsAPI.Calls;
import com.main.lets.lets.LetsAPI.Entity;
import com.main.lets.lets.LetsAPI.Event;
import com.main.lets.lets.LetsAPI.L;
import com.main.lets.lets.LetsAPI.UserData;
import com.main.lets.lets.R;
import com.rey.material.app.SimpleDialog;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by novosejr on 12/14/2016.
 */
@SuppressLint("ValidFragment")
public class EventActions extends DialogFragment {


    CharSequence[] memberActions = { "View Pictures", "Upload Pictures", "Leave Event"};
    CharSequence[] memberActionsCode = {"Enter Code", "View Pictures", "Upload Pictures", "Leave Event"};
    CharSequence[] hostActions = { "View Pictures", "Upload Pictures", "Delete Event", "Add Co-hosts", "Remove Co-hosts", "Edit Event"};

    CharSequence[] guestActions = {"Join Event", "View Pictures"};

    Event.MemberStatus mStatus;
    AppCompatActivity mActivity;
    ScreenUpdate mUpdate;
    UserData mUserData;
    Event mEvent;

    public EventActions(AppCompatActivity a, Event e, ScreenUpdate s) {

        mUserData = new UserData(a);
        mStatus = e.getUserStatus(mUserData.ID);
        mActivity = a;
        mUpdate = s;
        mEvent = e;

    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final CharSequence[] list;

        switch (mStatus) {
            case OWNER:
                list = hostActions;

                break;
            case HOST:
                list = hostActions;

                break;
            case MEMBER:
                if (mEvent.getCategory() == 9)
                    list = memberActionsCode;
                else
                    list = memberActions;

                break;
            case GUEST:
                list = guestActions;

                break;
            default:
                list = guestActions;

        }

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("Select Action")
                .setItems(list, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        runAction(mEvent, list[which].toString());

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public void runAction(final Event event, String s) {
        com.rey.material.app.Dialog.Builder builder = null;
        com.rey.material.app.DialogFragment fragment;

        switch (s) {

            case "Upload Pictures":
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                mActivity.startActivityForResult(photoPickerIntent, UserDetailActivity.SELECT_PICTURE);

                break;
            case "Add Co-hosts":
                Intent inviteIntent = new Intent(mActivity, InviteActivity.class);
                inviteIntent.putExtra("invite_id", event.getID());
                inviteIntent.putExtra("token", mUserData.ShallonCreamerIsATwat);
                inviteIntent.putExtra("entities", "Friends");
                inviteIntent.putExtra("mode", "U2CFE");
                inviteIntent.putExtra("id", mEvent.getID());
                mActivity.startActivity(inviteIntent);

                break;
            case "Remove Co-hosts":
                final int length = mEvent.getCohosts().size();
                CharSequence[] list = new CharSequence[length];
                for (int i = 0; i < length; i++) {
                    list[i] = mEvent.getCohosts().get(i).mText;

                }

                builder = new SimpleDialog.Builder() {
                    @Override
                    public void onPositiveActionClicked(
                            com.rey.material.app.DialogFragment fragment) {
                        CharSequence[] values = getSelectedValues();

                        for (int i = 0; i < values.length; i++) {
                            for (int j = 0; j < length; j++) {
                                Entity user = mEvent.getCohosts().get(j);
                                if (values[i].equals(user.mText)) {


                                    Calls.removeCohost(user.mID, event.getID(), mUserData.ShallonCreamerIsATwat, new JsonHttpResponseHandler() {


                                        @Override
                                        public void onSuccess(int statusCode,
                                                              Header[] headers,
                                                              JSONObject response) {

                                            //Need some interface to call on refresh data

                                        }
                                    });

                                }


                            }

                        }

                        super.onPositiveActionClicked(fragment);
                    }

                    @Override
                    public void onNegativeActionClicked(
                            com.rey.material.app.DialogFragment fragment) {
                        super.onNegativeActionClicked(fragment);
                    }
                };

                ((SimpleDialog.Builder) builder).multiChoiceItems(list)
                        .title("Remove Cohosts")
                        .positiveAction("Remove")
                        .negativeAction("Cancel");

                break;
            case "Leave Event":
                AlertDialog alertDialog = new AlertDialog.Builder(mActivity).create();

                alertDialog.setTitle("Are you sure?");

                alertDialog.setMessage("Are you sure you want to leave this event?");

                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Unattend", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Calls.leaveEvent(mEvent.getID(), mUserData.ShallonCreamerIsATwat, new JsonHttpResponseHandler() {

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                if (mUpdate != null)
                                    mUpdate.onScreenUpdate("Leave");

                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                if (mUpdate != null)
                                    mUpdate.onScreenUpdate("Leave");

                            }
                        });

                    }

                });

                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });

                alertDialog.show();

                break;
            case "Edit Event":
                AlertDialog editDialog = new AlertDialog.Builder(mActivity).create();

                editDialog.setTitle("Coming Soon!");

                editDialog.setMessage("Woot!");

                editDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Awesome", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }

                });

                editDialog.show();

                break;
            case "Join Event":

                if (mUpdate != null) {
                    mUpdate.onScreenUpdate("Join");
                }

                break;

            case "Enter Code":

                AlertDialog.Builder build = new AlertDialog.Builder(mActivity);
                build.setTitle("Enter Code");

                View v = View.inflate(mActivity, R.layout.dialog_enter_code, null);
                final android.widget.EditText code = (android.widget.EditText) v.findViewById(R.id.code);

                build.setView(v);
                build.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String s = code.getText().toString();

                        Calls.enterCode(s, new UserData(mActivity), new JsonHttpResponseHandler() {

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                                AlertDialog.Builder responseDialog = new AlertDialog.Builder(mActivity);
                                responseDialog.setPositiveButton("Okay", null);

                                if (!response.has("error")) {

                                    responseDialog.setTitle("Code Entered!");
                                    responseDialog.setMessage("Congratulations!  You are now entered " +
                                            "to win!  Keep coming back and enter the daily code for" +
                                            " a better chance to win!");

                                } else {

                                    responseDialog.setTitle("WRONG" + new String(Character.toChars(0x1F612)));
                                    responseDialog.setMessage("This is not the code you're looking for...");

                                }



                                responseDialog.create().show();

                            }
                        });


                    }
                });

                build.setNegativeButton("Cancel", null);
                build.create().show();

                break;

            case "Delete Event":

                AlertDialog.Builder delete = new AlertDialog.Builder(mActivity);
                delete.setTitle("Lets Think about what you're doing");
                delete.setMessage("Events are like small children, if you fucked it up or no " +
                        "one likes it, you should just put it down.  We understand.");
                delete.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        final ProgressDialog loading = ProgressDialog.show(mActivity, "",
                                "Destroying Event and Hiding Remains. Please wait...", true);

                        Calls.deleteEvent(new UserData(mActivity), mEvent.mID, new JsonHttpResponseHandler() {

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                                loading.hide();

                                AlertDialog.Builder suc = new AlertDialog.Builder(mActivity);
                                suc.setTitle("Event Terminated");
                                suc.setMessage("Your event has been wiped from existence.  " +
                                        "Also we don't support child murder, it was just a joke, " +
                                        "don't get all angry unless you want to start an internet" +
                                        " campaign in protest, that's usually good for business.");

                                suc.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        mActivity.finish();
                                    }
                                });

                                suc.create().show();

                            }
                        });

                    }
                });

                delete.setNegativeButton("Cancel", null);

                delete.create().show();

                break;

            case  "View Pictures":

                Intent i = new Intent(mActivity, CollageActivity.class);
                i.putExtra("Event", mEvent.getID());
                mActivity.startActivity(i);


                break;

        }

        if (builder != null) {
            fragment = com.rey.material.app.DialogFragment.newInstance(builder);
            fragment.show(mActivity.getSupportFragmentManager(), null);
        }

    }

    public interface ScreenUpdate {
        void onScreenUpdate(String action);
    }

}




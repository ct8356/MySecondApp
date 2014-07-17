package com.ct8356.mysecondapp;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.ct8356.mysecondapp.CustomDialogFragment.MyListener;
import com.ct8356.mysecondapp.DbContract.Tags;

public class DeleteDF extends DialogFragment implements DialogInterface.OnClickListener {
	List<String> mCheckedEntries;
	SharedPreferences mPrefs;
	protected Dialog mDialog;

	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
		//Note how above method used to be an Activity method. Now it is a Fragment method...
		//It is automatically called when DialogFragment is created!!!
		//(I.e. it is alot like the constructor!)
		//BUT, this way is better, because DialogFragment can be in own class! More reusable!
		//It can do this, thanks to...
        // Use the Builder class for convenient dialog construction
	    //DbHelper dbHelper = new DbHelper(getActivity());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure you wish to delete?");
        builder.setPositiveButton("OK", this);
        builder.setNegativeButton("Cancel", this);
        //Create the AlertDialog object and return it
        return builder.create();
    }

	@Override
	public void onClick(DialogInterface dialog, int which) {
		//getActivity.getCheckedPositions();
		//YES! May as well use this, rather than bundle.
		//This is benefit of using dialogs... can do this.
		if (which == dialog.BUTTON_NEGATIVE) {
			//Do nothing.
		} else if (which == dialog.BUTTON_POSITIVE) {
			AbstractManagerActivity activity = (AbstractManagerActivity) getActivity();
			activity.deleteSelectedEntries();
		}
	}
	
}


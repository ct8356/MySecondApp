package com.ct8356.mysecondapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ct8356.mysecondapp.DbContract.Tags;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
//import android.app.DialogFragment;
import android.support.v4.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

public class SingleTagSelectorDF extends DialogFragment {
	List<String> mAllTags;
	SharedPreferences mPrefs;
	protected Dialog mDialog;
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
		//Note how above method used to be an Activity method. Now it is a Fragment method...
		//It is automatically called when DialogFragment is created!!!
		//(I.e. it is alot like the constructor!)
		//BUT, this way is better, because DialogFragment can be in own class! More reusable!
		//It can do this, thanks to...

	    DbHelper dbHelper = new DbHelper(getActivity());
	    dbHelper.openDatabase();
		mAllTags = dbHelper.getAllEntriesColumn(Tags.TABLE_NAME, Tags.TAG);
		dbHelper.close();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Save this session to:");
        //Silly android. Cannot setMessage AND setItems...
        String[] allTagsArray = mAllTags.toArray(new String[0]);
    	builder.setItems(allTagsArray, new MyListener());
    	//Maybe should have RADIO box, so that it CONFIRMS right project...
        //Create the AlertDialog object and return it.
        return builder.create();
    }
	
	public class MyListener implements DialogInterface.OnClickListener {
		@Override
		public void onClick(DialogInterface arg0, int which) {
			String selectedTag = mAllTags.get(which);
			AbstractActivity activity = (AbstractActivity) getActivity();
			activity.mSelectedTags = Arrays.asList(selectedTag);
			activity.saveSelectedTags();
			activity.onDataPass();
		}
	}
	
}

package com.ct8356.mysecondapp;

import java.util.ArrayList;
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

public class CustomDialogFragment extends DialogFragment {
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
        // Use the Builder class for convenient dialog construction
	    DbHelper dbHelper = new DbHelper(getActivity());
	    dbHelper.openDatabase();
		mAllTags = dbHelper.getAllEntriesColumn(Tags.TABLE_NAME, Tags.TAG);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    	builder.setItems(mAllTags.toArray(new String[0]), new MyListener());
        //builder.setMessage("fire missiles");
        // Create the AlertDialog object and return it
        return builder.create();
    }
	
	public class MyListener implements DialogInterface.OnClickListener {
		@Override
		public void onClick(DialogInterface arg0, int which) {
			String selectedTag = mAllTags.get(which);
			AbstractManagerActivity activity = (AbstractManagerActivity) getActivity();
			activity.onDataPass();
		}
	}
	
}

//public class CustomDialogBuilder extends AlertDialog.Builder {
//	List<String> mAllTags;
//	SharedPreferences mPrefs;
//	protected Dialog mDialog;
//	
//	protected CustomDialogBuilder(Context context) {
//		super(context);
//	    DbHelper dbHelper = new DbHelper(context);
//	    dbHelper.openDatabase();
//		mAllTags = dbHelper.getAllEntriesColumn(Tags.TABLE_NAME, Tags.TAG);
//		setItems(mAllTags.toArray(new String[0]), new MyListener());
////		mPrefs = context.
////				getSharedPreferences("com.ct8356.mysecondapp", 0);//0 = Context.MODE_PRIVATE
//		//mPrefs.getString(DbContract.TAG_NAMES, "StringNotFound");
//		mDialog = create();
//		mDialog.show();
//    }
//	
////    AlertDialog.Builder builder = new AlertDialog.Builder(this);
////    AlertDialog dialog = builder.create();
////    dialog.show();
//	
//	public class MyListener implements DialogInterface.OnClickListener {
//		@SuppressLint("NewApi")  //CBTL
//		@Override
//		public void onClick(DialogInterface arg0, int which) {
//			//AbstractManagerActivity.this.mSelectedTags = mTags;
//			Activity activity = mDialog.getOwnerActivity();
////			SharedPreferences.Editor editor = mPrefs.edit();
//			String selectedTag = mAllTags.get(which);
////			editor.putString(DbContract.TAG_NAMES, selectedTag);
////		    editor.commit();
//			activity.dismissDialog(10); //HARDCODE
//			
//		}
//	}
//	
//}

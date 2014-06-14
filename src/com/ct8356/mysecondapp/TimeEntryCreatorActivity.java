package com.ct8356.mysecondapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ct8356.mysecondapp.DbContract.Minutes;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.os.Build;

public class TimeEntryCreatorActivity extends AbstractCreatorActivity {
    
	@Override
	public void initialiseViews() {
		mLayout = (LinearLayout) getLayoutInflater().
	    		  inflate(R.layout.abstract_creator, null);
		setContentView(mLayout);
		mFixedViewCount = mLayout.getChildCount(); // should give 2?
		mSelectedTagsText = (TextView) findViewById(R.id.selected_tags);
		mSelectedTagsText.setText("Selected tags: "+ mSelectedTags);
		//Add specific views.
		mLayout.addView(new TextView(this));
		EditText minutesEdit = new EditText(this);
		minutesEdit.setHint("Enter minutes");
		mLayout.addView(minutesEdit);
		TextView dateEdit = new TextView(this);
		dateEdit.setText(mDbHelper.getDateString());
		mLayout.addView(dateEdit);
		//back to abstract stuff
		if (mRequestCode == AbstractManagerActivity.EDIT_ENTRY) {
			mDbHelper.openDatabase();
			List<String> rowId = Arrays.asList(String.valueOf(mRowId));
			List<List<String>> entry = mDbHelper.getEntries(mTableName, null, DbContract._ID, rowId);
			for (int i = 0; i < mColumnCount; i += 1) {
	        	TextView text = (TextView) mLayout.getChildAt(i+mFixedViewCount);
	        	text.setText(entry.get(0).get(i));
	        	//Note, textView is a super class of editText.
		    }
			mDbHelper.close();
		}
    }
}

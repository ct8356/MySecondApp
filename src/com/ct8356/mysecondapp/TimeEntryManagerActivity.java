package com.ct8356.mysecondapp;

import java.util.ArrayList;
import java.util.List;

import com.ct8356.mysecondapp.AbstractManagerActivity.TagNamesAdapter;
import com.ct8356.mysecondapp.DbContract.Minutes;
import com.ct8356.mysecondapp.DbContract.Tags;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class TimeEntryManagerActivity extends AbstractManagerActivity 
implements AdapterView.OnItemSelectedListener {// CBTL could this interfere with up button?
    int mTotalTime;
    TextView mTotalTimeText;
    
	public void goCreateEntry() {
		Intent intent = new Intent(this, TimeEntryCreatorActivity.class);
	    startActivityForResult(intent, CREATE_ENTRY);
	}
	
	public void goEditEntry(Long rowId) {
		Intent intent = new Intent(this, TimeEntryEditorAct.class);
		intent.putExtra(DbContract._ID, rowId); 
	    startActivityForResult(intent, EDIT_ENTRY);
	}
	
	@Override
	public void initialiseMemberVariables() {
		//Activity Specific
		mTableName = Minutes.TABLE_NAME;
		super.initialiseMemberVariables();
	}
	
	public void initialiseViews() {
		//LAYOUT
		mLayout = (LinearLayout) getLayoutInflater().
				  inflate(R.layout.time_entry_manager, null);
		setContentView(mLayout);
		//SPINNER
		mSpinner = (Spinner) findViewById(R.id.selected_tag);
		mSpinner.setAdapter(mTagNamesAdapter);
		mSpinner.setOnItemSelectedListener(this);
		int pos = mTagNamesAdapter.getPosition(mSelectedTags.get(0));
		mSpinner.setSelection(pos);
		//TOTAL TIME
		mTotalTimeText = (TextView) findViewById(R.id.total_time);
		updateMSumMinutesAndText();
		super.initialiseViews();
	}
    
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        updateMSumMinutesAndText();
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.time_entry_manager, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.new_tag:
			goCreateTag();
			return true;
		}
		return super.onOptionsItemSelected(item); 
		//Don't want this! Makes for double calls!
	}
	
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mSelectedTags = savedInstanceState.getStringArrayList(DbContract.TAG_NAMES);
	} //Nec?
	
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putStringArrayList(DbContract.TAG_NAMES, 
				(ArrayList<String>) mSelectedTags);
	} //Nec?
		
	public void onStop() {
		super.onStop();
		//This is where supposed to do shared pref stuff.
		mPrefs = getSharedPreferences(DbContract.PREFS, 0); //0 is required mode
		SharedPreferences.Editor editor = mPrefs.edit();
		editor.putString(DbContract.TAG_NAMES, mSelectedTags.get(0));
		editor.commit();
	}

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        String selectedTag = parent.getItemAtPosition(pos).toString();
    	mSelectedTags = new ArrayList<String>();
    	mSelectedTags.add(selectedTag);
		//mSelectedTagsText.setText(""+mSelectedTags);
    	updateMSumMinutesAndText();
		updateMEntries();
		updateMChecked();
		mCustomAdapter.notifyDataSetChanged();
    }
    
	public void onNothingSelected(AdapterView<?> arg0) {
		// do nothing.
	}
    
    public void updateMEntries() {
	    if (mSelectedTags.size() != 0) {
			mDbHelper.openDatabase();
			List<String> timeEntryIds = mDbHelper.getRelatedEntryIds(mSelectedTags);
			mColumnNames = mDbHelper.getAllColumnNames(mTableName);
			mEntries = mDbHelper.getEntries(mTableName, mColumnNames, 
					"_id", timeEntryIds); //HARDCODE
			mDbHelper.close();
		}
	}

    @Override
	public void updateMSumMinutesAndText() {
		mDbHelper.openDatabase();
		mTotalTime = mDbHelper.sumMinutes(mSelectedTags);
		mDbHelper.close();
		mTotalTimeText.setText(mTotalTime + " minutes");
		return;
	}
}

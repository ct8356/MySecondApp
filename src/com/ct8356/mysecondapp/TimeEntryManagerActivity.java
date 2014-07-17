package com.ct8356.mysecondapp;

import java.util.ArrayList;
import java.util.List;

import com.ct8356.mysecondapp.AbstractManagerActivity.TagNamesAdapter;
import com.ct8356.mysecondapp.DbContract.Minutes;
import com.ct8356.mysecondapp.DbContract.Tags;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
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
import android.os.Build;

public class TimeEntryManagerActivity extends AbstractManagerActivity 
implements AdapterView.OnItemSelectedListener {
    protected List<String> mTagNames; //a key variable. it is called by getItem().
    //want to keep this in shared preferences!?
    
	public void goCreateEntry() {
		Intent intent = new Intent(this, TimeEntryCreatorActivity.class);
		intent.putExtra(DbContract.TABLE_NAME, Minutes.TABLE_NAME);
		intent.putExtra(DbContract.REQUEST_CODE, CREATE_ENTRY);
		intent.putStringArrayListExtra(DbContract.TAG_NAMES, 
				(ArrayList<String>) mSelectedTags);
	    startActivityForResult(intent, CREATE_ENTRY);
	}
	
	public void goEditEntry(Long rowId) {
		Intent intent = new Intent(this, TimeEntryEditorAct.class);
		intent.putExtra(DbContract.TABLE_NAME, Minutes.TABLE_NAME);
		intent.putExtra(DbContract.REQUEST_CODE, EDIT_ENTRY);
		intent.putStringArrayListExtra(DbContract.TAG_NAMES, 
				(ArrayList<String>) mSelectedTags);
		intent.putExtra(DbContract._ID, rowId); 
	    startActivityForResult(intent, EDIT_ENTRY);
	}
	
	@Override
	public void initialiseMemberVariables() {
		//for the project fragment
		mTagNames = new ArrayList<String>();
		mDbHelper.openDatabase();
		mTagNames = mDbHelper.getAllEntriesColumn(Tags.TABLE_NAME, Tags.TAG);
		mDbHelper.close();
		mTagNamesAdapter = new TagNamesAdapter(this, R.layout.tag_name, 
				mTagNames);
		updateMSelectedTags();
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
		super.initialiseViews();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.time_entry_manager, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.new_tag:
			goCreateTag();
			return true;
		}
		return true;
		//return super.onOptionsItemSelected(item); 
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

}

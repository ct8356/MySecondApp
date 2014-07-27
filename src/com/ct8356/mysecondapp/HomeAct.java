package com.ct8356.mysecondapp;

import java.util.ArrayList;
import java.util.List;

import com.ct8356.mysecondapp.DbContract.Minutes;
import com.ct8356.mysecondapp.DbContract.MTJoins;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.os.Build;

public class HomeAct extends AbstractActivity {
	private static final int GENERAL=0;
	//public TextView mSumMinutesText;
	//private int mSumMinutes;
	
//	public void deselectTags(View view) {
//		mSelectedTags.clear();
//		mSelectedTagsText.setText("Selected tags: " + mSelectedTags); 
//		updateMSumMinutesAndText();
//		//if start using these really often, could put them in own single line method.
//	}
	
//	public void goSelectTags(View view) {
//		Intent intent = new Intent(TimeAccumulatorActivity.this, TagManagerActivity.class);
//		intent.putStringArrayListExtra(DbContract.TAG_NAMES, 
//				(ArrayList<String>) mSelectedTags); 
//		startActivityForResult(intent, ADD_TAG);
//	}
	
	public void goStartSession(View view) {
		Intent intent = new Intent(HomeAct.this, StartSessionActivity.class);
	    startActivityForResult(intent, GENERAL);
	}
	
	public void goManageTimeEntries(View view) {
		Intent intent = new Intent(HomeAct.this, TimeEntryManagerActivity.class);
	    startActivityForResult(intent, GENERAL);
	}
	
	public void goTagManager(View view) {
		Intent intent = new Intent(HomeAct.this, TagManagerActivity.class);
		startActivityForResult(intent, GENERAL);
	}
	
	private void initialiseMemberVariables() {
		mDbHelper = new DbHelper(this);
		mSelectedTags = new ArrayList<String>();
	}
	
	public void initialiseViews() {									
		setContentView(R.layout.home);
		//mSelectedTagsText = (TextView) findViewById(R.id.selected_tags);
		//mSumMinutesText = (TextView) findViewById(R.id.sum_time_entries);
		//mSelectedTagsText.setText("Selected tags: " + mSelectedTags);
		//updateMSumMinutesAndText(); 
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initialiseMemberVariables();
		initialiseViews();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.time_accumulator, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
//	public void updateMSumMinutesAndText() {
//		mDbHelper.openDatabase();
//		mSumMinutes = mDbHelper.sumMinutes(mSelectedTags);
//		mDbHelper.close();
//		mSumMinutesText.setText("Total minutes: " + mSumMinutes);
//		return;
//	}

}

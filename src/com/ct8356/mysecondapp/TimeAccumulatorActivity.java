package com.ct8356.mysecondapp;

import java.util.ArrayList;
import java.util.List;

import com.ct8356.mysecondapp.DbContract.Minutes;
import com.ct8356.mysecondapp.DbContract.MinutesToTagJoins;

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

public class TimeAccumulatorActivity extends ActionBarActivity {
	private static final int ADD_TAG=1;
	private static final int START_SESSION=2;
	private static final int MANAGE_ENTRIES=3;
	private DbHelper mDbHelper;
	public List<String> mSelectedTags;
	public TextView mSelectedTagsText;
	public TextView mSumMinutesText;
	private int mSumMinutes;
	
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
		Intent intent = new Intent(TimeAccumulatorActivity.this, 
				StartSessionActivity.class);
		intent.putStringArrayListExtra(DbContract.TAG_NAMES, 
				(ArrayList<String>) mSelectedTags);
	    startActivityForResult(intent, START_SESSION);
	}
	
	public void goManageTimeEntries(View view) {
		Intent intent = new Intent(TimeAccumulatorActivity.this, 
				TimeEntryManagerActivity.class);
		intent.putExtra(DbContract.TABLE_NAME, Minutes.TABLE_NAME); //Better way than Db.T?
		intent.putExtra(DbContract.CREATOR_ACTIVITY, Minutes.TABLE_NAME); 
		//not needed anymore, but keep in case use it later.
		intent.putStringArrayListExtra(DbContract.TAG_NAMES, 
				(ArrayList<String>) mSelectedTags); 
	    startActivityForResult(intent, MANAGE_ENTRIES);
	}
	
	private void initialiseMemberVariables() {
		mDbHelper = new DbHelper(this);
		mSelectedTags = new ArrayList<String>();
	}
	
	public void initialiseViews() {									
		setContentView(R.layout.time_accumulator);
		//mSelectedTagsText = (TextView) findViewById(R.id.selected_tags);
		//mSumMinutesText = (TextView) findViewById(R.id.sum_time_entries);
		//mSelectedTagsText.setText("Selected tags: " + mSelectedTags);
		//updateMSumMinutesAndText(); 
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (resultCode) {
        case RESULT_CANCELED:
        	//Do nothing
        	break;
        case RESULT_OK:
	        switch (requestCode) {
	        case ADD_TAG:
	        case START_SESSION:
	        case MANAGE_ENTRIES:
	        	mSelectedTags = intent.getStringArrayListExtra(DbContract.TAG_NAMES);
	        	//mSelectedTagsText.setText("Selected tags: " + mSelectedTags);
	        	//updateMSumMinutesAndText();
	        	break;
	        }
	        break;
        }
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
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
	    //super.onRestoreInstanceState(savedInstanceState);
		//They recommend I should call above, but then its a double restore...
		//works fine without it.
	    //mSelectedTags = savedInstanceState.getStringArrayList(DbContract.TAG_NAMES);
	    //mSumMinutes = savedInstanceState.getInt(DbContract.SUM_MINUTES);
	    //mSelectedTagsText.setText("Selected tags: " + mSelectedTags);
    	//updateMSumMinutesAndText();
	} //onRestoreInstanceSt will be called anyway, so may as well use it.
	//To avoid double restore, do little as possible in onCreate...?
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	    savedInstanceState.putStringArrayList(DbContract.TAG_NAMES, (ArrayList<String>) mSelectedTags);
	    savedInstanceState.putInt(DbContract.SUM_MINUTES, mSumMinutes);
	    super.onSaveInstanceState(savedInstanceState);
	}
	
//	public void updateMSumMinutesAndText() {
//		mDbHelper.openDatabase();
//		mSumMinutes = mDbHelper.sumMinutes(mSelectedTags);
//		mDbHelper.close();
//		mSumMinutesText.setText("Total minutes: " + mSumMinutes);
//		return;
//	}

}

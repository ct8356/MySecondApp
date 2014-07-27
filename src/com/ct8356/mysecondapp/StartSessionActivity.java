package com.ct8356.mysecondapp;

import java.util.ArrayList;
import java.util.List;

import com.ct8356.mysecondapp.DbContract.Minutes;
import com.ct8356.mysecondapp.DbContract.MTJoins;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

public class StartSessionActivity extends AbstractActivity {
	private Chronometer mChrono;
	private Button start;
	private Button pause;
	private boolean fresh = true;
	private boolean stopped = true;
	private long startTime;
	private long mElapsedTime;
	//private TextView mSelectedTagsText;
	private static final int SELECT_MIN1_TAGS = 0;
	private static final int SELECT_TAGS = 1;
	private static final int MANAGE_TIME_ENTRIES = 2;
	protected String mMins;
	
	public void goManageTimeEntries() {
		Intent intent = new Intent(this, TimeEntryManagerActivity.class);
		intent.putStringArrayListExtra(DbContract.TAG_NAMES, 
				(ArrayList<String>) mSelectedTags); 
		startActivityForResult(intent, MANAGE_TIME_ENTRIES);
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		//I think this activity must be used similarly to onCreate or onStart.
		//Shame, because leads to duplicate code. But that is way it is...
		//Could make it call onCreate or onStart, but then they called 2wice.
		//Silly android. (That is if onSomething sequence is as described in your notes).
        super.onActivityResult(requestCode, resultCode, intent);
        switch (resultCode) {
        case RESULT_CANCELED:
        	//Do nothing
        	break;
        case RESULT_OK:
	        switch (requestCode) {
//	        case MANAGE_TIME_ENTRIES:
//	        	setResult(RESULT_OK, intent);
//	            finish();
//	        	break;
	    	case SELECT_MIN1_TAGS:
	            updateMSelectedTags();
	            saveState(); 
	            //goManageTimeEntries();
	            //setResult(RESULT_OK, intent);
	            //finish();
	            //SHOW TOAST!!!
	            String text = "Saved "+mMins+" minutes to project '"+mSelectedTags.get(0)+"'";
	            Toast.makeText(this, text, Toast.LENGTH_LONG).show();
	            reset();
	            break;
	    	}
	        break;
        }
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_session);
		mDbHelper = new DbHelper(this);
		mChrono = (Chronometer) findViewById(R.id.chrono);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.start_session, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case R.id.action_settings:
			return true;
		case R.id.view_time_entries:
			goManageTimeEntries();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mElapsedTime = savedInstanceState.getLong(DbContract.ELAPSED_TIME);
		mChrono.setBase(SystemClock.elapsedRealtime() - mElapsedTime);
    	mSelectedTags = savedInstanceState.getStringArrayList(DbContract.TAG_NAMES);
    	//mSelectedTagsText.setText("Selected tags: "+mSelectedTags);
    	//CBTL, this is done twice, here and in onCreate...
	}// Seems silly to me that onCreate is called after orientation change.
	//Why could it not just call onOrientationChange or something?
	//Then could just redraw the views, without having to instantiate them again.
	
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		mElapsedTime = SystemClock.elapsedRealtime() - mChrono.getBase();
		savedInstanceState.putLong(DbContract.ELAPSED_TIME, mElapsedTime);
		savedInstanceState.putStringArrayList(DbContract.TAG_NAMES, 
				(ArrayList<String>) mSelectedTags);
	}
	
    private void saveState() {
        mDbHelper.openDatabase();
    	mMins = String.valueOf((mElapsedTime/1000)/60); //CBTL Turn it to minutes
        mDbHelper.insertEntryAndJoins(Minutes.TABLE_NAME, mMins, 
        		MTJoins.TABLE_NAME, mSelectedTags);
        mDbHelper.close();
    }
			
	public void onStartClick(View view) {
		if (stopped) {
			if (fresh) {
				mChrono.setBase(SystemClock.elapsedRealtime());
				mChrono.start(); 
				fresh = false;
			} else {
				mChrono.setBase(SystemClock.elapsedRealtime() - mElapsedTime);
				mChrono.start(); 
			}
			stopped = false;
		} else {
			mChrono.stop();
			mElapsedTime = SystemClock.elapsedRealtime() - mChrono.getBase();
			stopped = true;
		}
	}
		
	public void onResetClick(View view) {
		reset();
	}
	
	public void onSaveClick(View view) {
		mChrono.stop();
		mElapsedTime = SystemClock.elapsedRealtime() - mChrono.getBase();
		Intent intent;
//			if (mSelectedTags.size() == 0) {
			intent = new Intent(StartSessionActivity.this, OneTagManagerActivity.class);
			intent.putStringArrayListExtra(DbContract.TAG_NAMES, 
					(ArrayList<String>) mSelectedTags);
			startActivityForResult(intent, SELECT_MIN1_TAGS);
//			} else {
//				saveState();
//				intent = new Intent();
//				intent.putStringArrayListExtra(DbContract.TAG_NAMES, 
//						(ArrayList<String>) mSelectedTags); 
//				setResult(RESULT_OK, intent);
//				finish();
//			}
	}
	
	public void reset() {
		mChrono.setBase(SystemClock.elapsedRealtime());
		mChrono.stop();
		//elapsedRealtime is time from device boot up
		stopped = true;
		fresh = true;
	}
	
//	public class SelectTagsListener implements View.OnClickListener {
//		public void onClick(View view) {
//			Intent intent = new Intent(StartSessionActivity.this, OneManagerActivity.class);
//			intent.putStringArrayListExtra(DbContract.TAG_NAMES, 
//					(ArrayList<String>) mSelectedTags); 
//			startActivityForResult(intent, SELECT_TAGS);
//		}
//	}
//Use XML, not this...
}

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

	public void enterMockData() {
		mDbHelper.openDatabase();
		//mDbHelper.insertEntry("tag1");
		//mDbHelper.insertEntry("tag2");
		mDbHelper.close();
	}
	
	public void add10Minutes(List<String> projectName){
		mDbHelper.openDatabase();
		String minutes = "10";
		mDbHelper.insertEntryAndJoins(Minutes.TABLE_NAME, minutes, 
				MinutesToTagJoins.TABLE_NAME, mSelectedTags);
		mDbHelper.close();
	}
	
	private void initialiseMemberVariables() {
		mSelectedTags = new ArrayList<String>();
	}
	
	public void initialiseViews() {									
		//CREATE THE VIEWS
		TAALayout scrollLayout = new TAALayout(this);
		setContentView(scrollLayout);
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
	        	mSelectedTags = intent.getStringArrayListExtra(DbContract.TAG_NAMES);
	        	mSelectedTagsText.setText("Selected tags: " + mSelectedTags);
	        	updateMSumMinutesText();
	        	break;
	        case START_SESSION:
	        	mSelectedTags = intent.getStringArrayListExtra(DbContract.TAG_NAMES);
	        	mSelectedTagsText.setText("Selected tags: " + mSelectedTags);
	        	updateMSumMinutesText();
	        	//needs to update these, incase these are changed during session.
	        	break;
	        case MANAGE_ENTRIES:
	        	mSelectedTags = intent.getStringArrayListExtra(DbContract.TAG_NAMES);
	        	mSelectedTagsText.setText("Selected tags: " + mSelectedTags);
	        	updateMSumMinutesText();
	        	break;
	        }
	        break;
        }
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//enterMockData();
		mDbHelper = new DbHelper(this);
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
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void updateMSumMinutesText() {
		mDbHelper.openDatabase();
		mSumMinutes = mDbHelper.sumMinutes(mSelectedTags);
		mDbHelper.close();
		mSumMinutesText.setText("Total minutes: " + mSumMinutes);
	}
	
	public class TAALayout extends ScrollView {
		public TAALayout(Context context) {
			super(context);
			//CREATE THE VIEWS
			Button removeTags = new Button(context);
			mSelectedTagsText = new TextView(context);
			Button addTags = new Button(context);
			mSumMinutesText = new TextView(context);
			Button add10 = new Button(context);
			Button startSession = new Button(context);
			Button manageTimeEntries = new Button(context);
			LinearLayout layout = new LinearLayout(context);
			layout.setOrientation(1);
			//SET THE TEXT AND ACTIONS;
			mSelectedTagsText.setText("Selected tags: " + mSelectedTags);
			mSumMinutesText.setText("Total minutes: " + mSumMinutes);
			removeTags.setText("Deselect tags");
	        removeTags.setOnClickListener(new RemoveTagsListener());
	        addTags.setText("Select tags");
	        addTags.setOnClickListener(new AddTagListener());
			add10.setText("Add 10 minutes");
	        add10.setOnClickListener(new Add10Listener());
	        startSession.setText("Start work session");
	        startSession.setOnClickListener(new StartSessionListener());
	        manageTimeEntries.setText("Manage time entries");
	        manageTimeEntries.setOnClickListener(new ManageEntriesListener());
			//ADD VIEWS
	        layout.addView(removeTags);
	        layout.addView(mSelectedTagsText);
	        layout.addView(addTags);
			layout.addView(mSumMinutesText);
			layout.addView(add10);
			layout.addView(startSession);
			layout.addView(manageTimeEntries);
			this.addView(layout);
		}
	}
	
	public class RemoveTagsListener implements View.OnClickListener {
		public void onClick(View view) {
			mSelectedTags.clear();
			mSelectedTagsText.setText("Selected tags: " + mSelectedTags); 
			updateMSumMinutesText();
			//if start using these really often, could put them in own single line method.
		}
	}
	
	public class AddTagListener implements View.OnClickListener {
		public void onClick(View view) {
			Intent intent = new Intent(TimeAccumulatorActivity.this, TagManagerActivity.class);
			intent.putStringArrayListExtra(DbContract.TAG_NAMES, 
					(ArrayList<String>) mSelectedTags); 
			startActivityForResult(intent, ADD_TAG);
		}
	}

	public class Add10Listener implements View.OnClickListener {
		public void onClick(View view) {
			add10Minutes(mSelectedTags);
			updateMSumMinutesText();
		}
	}
	
	public class StartSessionListener implements View.OnClickListener {
		public void onClick(View view) {
			Intent intent = new Intent(TimeAccumulatorActivity.this, 
					StartSessionActivity.class);
			intent.putStringArrayListExtra(DbContract.TAG_NAMES, 
					(ArrayList<String>) mSelectedTags);
		    startActivityForResult(intent, START_SESSION);
		}
	}
	
	public class ManageEntriesListener implements View.OnClickListener {
		public void onClick(View view) {
			Intent intent = new Intent(TimeAccumulatorActivity.this, 
					TimeEntryManagerActivity.class);
			intent.putExtra(DbContract.TABLE_NAME, Minutes.TABLE_NAME); //Better way than Db.T?
			intent.putExtra(DbContract.CREATOR_ACTIVITY, Minutes.TABLE_NAME); 
			intent.putStringArrayListExtra(DbContract.TAG_NAMES, 
					(ArrayList<String>) mSelectedTags); 
		    startActivityForResult(intent, MANAGE_ENTRIES);
		}
	}
	
}

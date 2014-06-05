package com.ct8356.mysecondapp;

import java.util.ArrayList;
import java.util.List;

import com.ct8356.mysecondapp.DbContract.Minutes;

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
	private static final int CHOOSE_TAG=0;
	private static final int ADD_TAG=1;
	private static final int START_SESSION=2;
	private static final int MANAGE_ENTRIES=3;
	private DbHelper mDbHelper;
	public List<String> mSelectedTags;
	public TextView mSelectedTagsText;
	public List<String> mRowIds;
	//was getContext() ...
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Gets the data repository in write mode
		mDbHelper = new DbHelper(this);
		mRowIds = new ArrayList<String>();
	}
	
	@Override
	protected void onStart() {
		super.onStart();	
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		//enterMockData();
		updateContent(); //Not sure should do this here...
		//but if do do it here, don't do it in onCreate or onActivityResult().
	}
	
	public void enterMockData() {
		mDbHelper.openDatabase();
		mDbHelper.insertTag("tag1");
		mDbHelper.insertTag("tag2");
		mDbHelper.close();
	}
	
	public void updateContent() {
		//DO DATABASE STUFF
		mDbHelper.openDatabase();
		mSelectedTags = mDbHelper.getTags(mRowIds); //It works I believe!
		int sumMinutes = mDbHelper.sumMinutes(mSelectedTags);
		//it seems you can't look into contents of cursor.
		//but could take an array from it.
		mDbHelper.close();										
		//CREATE THE VIEWS
		TAALayout scrollLayout = new TAALayout(this, sumMinutes);
		setContentView(scrollLayout);
	}
	
	public void add10Minutes(List<String> projectName){
		mDbHelper.openDatabase();
		int minutes = 10;
		mDbHelper.insertRecord(minutes, mSelectedTags);  //HARDCODE
		mDbHelper.close();
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
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (resultCode) {
        case RESULT_CANCELED:
        	//Do nothing
        	break;
        case RESULT_OK:
	        switch (requestCode) {
	        case CHOOSE_TAG:
	        	setIntent(intent); //Error here, because intent is not complete. has null.
				Bundle extras = getIntent().getExtras(); //Seems null intent is passed,
				if (extras != null) {
					mRowIds.clear();
		        	mRowIds.add(extras.getString("tag"));
		        } else {
		    		mRowIds.add("1"); //
		        }
				break;
	        case ADD_TAG:
	        	setIntent(intent);
	        	extras = intent.getExtras();
	        	List<String> selectedTags = extras.getStringArrayList("tags");
	        	mDbHelper = new DbHelper(this);
	        	mDbHelper.openDatabase();
	        	mRowIds = mDbHelper.getTagIds(selectedTags); //causes issue
	        	mDbHelper.close();
	        	break;
	        case START_SESSION:
	        	//Do not set intent.
	        	break;
	        }
	        break;
        }
    }

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(
					R.layout.fragment_time_accumulator, container, false);
			return rootView;
		}
	}
	
	public class TAALayout extends ScrollView {
		public TAALayout(Context context, int sumMinutes) {
			super(context);
			//CREATE THE VIEWS
			Button removeTags = new Button(context);
			mSelectedTagsText = new TextView(context);
			Button addTags = new Button(context);
			TextView textViewSumMinutes = new TextView(context);
			Button add10 = new Button(context);
			Button startSession = new Button(context);
			Button manageTimeEntries = new Button(context);
			LinearLayout layout = new LinearLayout(context);
			layout.setOrientation(1);
			//SET THE TEXT AND ACTIONS;
			mSelectedTagsText.setText("Chosen tags: " + mSelectedTags);
			//Cool. Can put List<String> in String, comes out in brackets.
			textViewSumMinutes.setText("Total minutes: " + sumMinutes);
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
			layout.addView(textViewSumMinutes);
			layout.addView(add10);
			layout.addView(startSession);
			layout.addView(manageTimeEntries);
			this.addView(layout);
		}
	}
	
	public class RemoveTagsListener implements View.OnClickListener {
		public void onClick(View view) {
			mRowIds.clear();
			updateContent();
		}
	}
	
	public class AddTagListener implements View.OnClickListener {
		public void onClick(View view) {
			Intent intent = new Intent(TimeAccumulatorActivity.this, TagManagerActivity.class);
			intent.putStringArrayListExtra("tags", (ArrayList<String>) mSelectedTags); 
			startActivityForResult(intent, ADD_TAG);
		}
	}

	public class Add10Listener implements View.OnClickListener {
		public void onClick(View view) {
			add10Minutes(mSelectedTags);
			updateContent();
		}
	}
	
	public class StartSessionListener implements View.OnClickListener {
		public void onClick(View view) {
			Intent intent = new Intent(TimeAccumulatorActivity.this, 
					StartSessionActivity.class);
			intent.putExtra("tag", "tag1"); //HARDCODE
		    startActivityForResult(intent, START_SESSION);
		}
	}
	
	public class ManageEntriesListener implements View.OnClickListener {
		public void onClick(View view) {
			Intent intent = new Intent(TimeAccumulatorActivity.this, 
					TimeEntryManagerActivity.class);
			intent.putExtra(DbContract.TABLE_NAME, Minutes.TABLE_NAME); //Better way than Db.T?
			intent.putExtra(DbContract.CREATOR_ACTIVITY, Minutes.TABLE_NAME); 
		    startActivityForResult(intent, MANAGE_ENTRIES);
		}
	}
	
}

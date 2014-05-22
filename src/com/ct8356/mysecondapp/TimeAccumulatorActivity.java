package com.ct8356.mysecondapp;

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
	private static final int START_SESSION=1;
	private DbHelper mDbHelper;
	public String mSelectedTagString = "Android";
	public TextView mSelectedTag;
	public String mTagString = "Android";
	public long mRowId;
	//was getContext() ...
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Gets the data repository in write mode
		mDbHelper = new DbHelper(this);
		updateContent();
	}
	
	@Override
	protected void onResume() {
		super.onResume();	
		updateContent();
	}
	
	public void updateContent() {
        //OTHER
		mRowId = 1;
        Bundle extras = getIntent().getExtras();
        //mRowId = extras != null ? extras.getLong("tag") : null; //null pointer exception
        if (extras != null) {
        	mRowId = extras.getLong("tag");
        }
		//DO DATABASE STUFF
		mDbHelper.openDatabase();
		mSelectedTagString = mDbHelper.getTag(mRowId);
		int sumMinutes = mDbHelper.sumMinutes(mSelectedTagString);
		mDbHelper.close();
		//CREATE THE VIEWS
		TAALayout scrollLayout = new TAALayout(this,sumMinutes);
		setContentView(scrollLayout);
	}
	
	public void add10Minutes(String projectName){
		mDbHelper.openDatabase();
		int minutes = 10;
		mDbHelper.insertRecord(projectName, minutes);
		mDbHelper.close();
	}
	
	public void goChooseTag(){
		Intent intent = new Intent(this, ChooseTagActivity.class);
	    startActivityForResult(intent, CHOOSE_TAG);
	}
	
	public void goStartSession(){
		Intent intent = new Intent(this, StartSessionActivity.class);
		intent.putExtra("tag", mSelectedTagString);
	    startActivityForResult(intent, START_SESSION);
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
        switch (requestCode) {
        case CHOOSE_TAG:
        	setIntent(intent);
        case START_SESSION:
        	//Do not set intent.
        }
        updateContent();
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
			Button chooseTag = new Button(context);
			mSelectedTag = new TextView(context);
			//TextView chosenTag = new TextView(this);
			TextView textViewSumMinutes = new TextView(context);
			Button add10 = new Button(context);
			Button startSession = new Button(context);
			LinearLayout layout = new LinearLayout(context);
			layout.setOrientation(1);
			//SET THE TEXT AND ACTIONS;
			mSelectedTag.setText("Chosen tag: " + mSelectedTagString);
			textViewSumMinutes.setText("Total minutes: " + sumMinutes);
			chooseTag.setText("Choose tag");
	        chooseTag.setOnClickListener(new ChooseTagListener());
			add10.setText("Add 10 minutes");
	        add10.setOnClickListener(new add10Listener());
	        startSession.setText("Start work session");
	        startSession.setOnClickListener(new startSessionListener());
			//ADD VIEWS
	        layout.addView(chooseTag);
	        layout.addView(mSelectedTag);
			layout.addView(textViewSumMinutes);
			layout.addView(add10);
			layout.addView(startSession);
			//layout.addView(mTag);
			//layout.addView(addTag);
			this.addView(layout);
		}
	}
	
	public class ChooseTagListener implements View.OnClickListener {
		public void onClick(View view) {
			goChooseTag();
		}
	}

	public class add10Listener implements View.OnClickListener {
		public void onClick(View view) {
			add10Minutes(mSelectedTagString);
			updateContent();
		}
	}
	
	public class startSessionListener implements View.OnClickListener {
		public void onClick(View view) {
			goStartSession();
		}
	}
	
}

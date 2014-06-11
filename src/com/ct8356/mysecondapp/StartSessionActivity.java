package com.ct8356.mysecondapp;

import java.util.ArrayList;
import java.util.List;

import com.ct8356.mysecondapp.DbContract.Minutes;
import com.ct8356.mysecondapp.DbContract.MinutesToTagJoins;

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
import android.os.Build;

public class StartSessionActivity extends ActionBarActivity {
	private Chronometer mChrono;
	private DbHelper mDbHelper;
	private Button start;
	private Button pause;
	private boolean stopped = false;
	private long startTime;
	private long mElapsedTime;
	private List<String> mSelectedTags;
	private TextView mSelectedTagsText;
	private static final int SELECT_MIN1_TAGS = 0;
	private static final int SELECT_TAGS = 1;
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
    	switch (requestCode) {
    	case SELECT_MIN1_TAGS:
            mSelectedTags = intent.getStringArrayListExtra(DbContract.TAG_NAMES);
            //mSelectedTagsText.setText("Selected tags: "+mSelectedTags);
            saveState();
            setResult(RESULT_OK, intent);
            finish();
            break;
    	case SELECT_TAGS:
            mSelectedTags = intent.getStringArrayListExtra(DbContract.TAG_NAMES);
            mSelectedTagsText.setText("Selected tags: "+mSelectedTags);
            break;
    	}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StartSessionLayout layout = new StartSessionLayout(this);
		setContentView(layout);
		mDbHelper = new DbHelper(this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
        	//mSelectedTagsString = extras.getString("tag");
        	mSelectedTags = extras.getStringArrayList(DbContract.TAG_NAMES);
        	mSelectedTagsText.setText("Selected tags: "+mSelectedTags);
        }
		mChrono.start();
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
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
    @Override
    protected void onPause() {
        super.onPause();
    }
    
    private void saveState() {
        mDbHelper.openDatabase();
    	String mins = String.valueOf((mElapsedTime/1000)/60); //CBTL Turn it to minutes
        mDbHelper.insertEntryAndJoins(Minutes.TABLE_NAME, mins, 
        		MinutesToTagJoins.TABLE_NAME, mSelectedTags);
        mDbHelper.close();
     }

	public class StartSessionLayout extends ScrollView {	
		public StartSessionLayout(Context context) {
			super(context);
			//CREATE THE VIEWS
			mSelectedTagsText = new TextView(context);
			Button selectTags = new Button(context);
			mChrono = new Chronometer(context);
			start = new Button(context);
			pause = new Button(context);
			Button reset = new Button(context);
			Button save = new Button(context);
			LinearLayout layout = new LinearLayout(context);
			layout.setOrientation(1);
			//SET THE TEXT AND ACTIONS;
			mSelectedTagsText.setText("Selected tags: "+ mSelectedTags);
			selectTags.setText("Select tags");
			selectTags.setOnClickListener(new SelectTagsListener());
	        start.setText("Start");
	        start.setOnClickListener(new StartListener());
	        start.setVisibility(View.GONE);
	        pause.setText("Pause");
	        pause.setOnClickListener(new StopListener());
	        reset.setText("Reset");
	        reset.setOnClickListener(new ResetListener());
	        save.setText("Stop session and save");
	        save.setOnClickListener(new SaveListener());
			//ADD VIEWS
	        layout.addView(mSelectedTagsText);
	        layout.addView(selectTags);
			layout.addView(mChrono);
			layout.addView(start);
			layout.addView(pause);
			layout.addView(reset);
			layout.addView(save);
			this.addView(layout);
		}
	}
			
	public class StartListener implements View.OnClickListener {
		public void onClick(View view) {
			start.setVisibility(View.GONE);
			pause.setVisibility(View.VISIBLE);
			if (stopped) {
				mChrono.setBase(SystemClock.elapsedRealtime() - mElapsedTime);
				mChrono.start(); 
				stopped = false;
			} else {
				mChrono.setBase(SystemClock.elapsedRealtime());
				mChrono.start(); 
			}
		}
	}
	
	public class StopListener implements View.OnClickListener {
		public void onClick(View view) {
			mChrono.stop();
			pause.setVisibility(View.GONE);
			start.setVisibility(View.VISIBLE);
			start.setText("Resume");
			mElapsedTime = SystemClock.elapsedRealtime() - mChrono.getBase();
			stopped = true;
		}
	}
	
	public class ResetListener implements View.OnClickListener {
		public void onClick(View view) {
			mChrono.setBase(SystemClock.elapsedRealtime());
			mChrono.stop();
			pause.setVisibility(View.GONE);
			start.setVisibility(View.VISIBLE);
			start.setText("Start");
			//elapsedRealtime is time from device boot up
			stopped = false;
		}
	}
	
	public class SaveListener implements View.OnClickListener {
		public void onClick(View view) {
			mChrono.stop();
			mElapsedTime = SystemClock.elapsedRealtime() - mChrono.getBase();
			Intent intent;
			if (mSelectedTags.size() == 0) {
				intent = new Intent(StartSessionActivity.this, Min1TagManagerActivity.class);
				intent.putStringArrayListExtra(DbContract.TAG_NAMES, 
						(ArrayList<String>) mSelectedTags);
				startActivityForResult(intent, SELECT_MIN1_TAGS);
			} else {
				saveState();
				intent = new Intent();
				intent.putStringArrayListExtra(DbContract.TAG_NAMES, 
						(ArrayList<String>) mSelectedTags); 
				setResult(RESULT_OK, intent);
				finish();
			}
		}
	}
	
	public class SelectTagsListener implements View.OnClickListener {
		public void onClick(View view) {
			Intent intent = new Intent(StartSessionActivity.this, TagManagerActivity.class);
			intent.putStringArrayListExtra(DbContract.TAG_NAMES, 
					(ArrayList<String>) mSelectedTags); 
			startActivityForResult(intent, SELECT_TAGS);
		}
	}
	
}

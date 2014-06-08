package com.ct8356.mysecondapp;

import java.util.ArrayList;
import java.util.List;

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
	private String mSelectedTagsString;
	private List<String> mSelectedTags;
	private TextView mSelectedTagsText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StartSessionLayout layout = new StartSessionLayout(this);
		setContentView(layout);
		mDbHelper = new DbHelper(this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
        	//mSelectedTagsString = extras.getString("tag");
        	mSelectedTags = extras.getStringArrayList("tag");
        	mSelectedTagsText.setText("Selected tags: "+mSelectedTags);
        }
		mChrono.start();
		//setContentView(R.layout.activity_start_session);
		//if (savedInstanceState == null) {
		//	getSupportFragmentManager().beginTransaction()
		//			.add(R.id.container, new PlaceholderFragment()).commit();
		//}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.start_session, menu);
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
    protected void onPause() {
        super.onPause();
        saveState();
    }
    
    private void saveState() {
        mDbHelper.openDatabase();
    	int mins = (int) ((mElapsedTime/1000)/60); //CBTL Turn it to minutes
    	//List<String> mSelectedTagsList = new ArrayList<String>();
    	//mSelectedTagsList.add(mSelectedTagsString);
        mDbHelper.insertTimeEntry(mins, mSelectedTags);
        mDbHelper.close();
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
			View rootView = inflater.inflate(R.layout.fragment_start_session,
					container, false);
			return rootView;
		}
	}

	public class StartSessionLayout extends ScrollView {	
		public StartSessionLayout(Context context) {
			super(context);
			//CREATE THE VIEWS
			mSelectedTagsText = new TextView(context);
			mChrono = new Chronometer(context);
			start = new Button(context);
			pause = new Button(context);
			Button reset = new Button(context);
			Button save = new Button(context);
			LinearLayout layout = new LinearLayout(context);
			layout.setOrientation(1);
			//SET THE TEXT AND ACTIONS;
			mSelectedTagsText.setText("Selected tags: "+ mSelectedTags);
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
			setResult(RESULT_OK);
			finish();
		}
	}
	
}

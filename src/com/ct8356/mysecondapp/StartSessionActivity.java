package com.ct8356.mysecondapp;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.os.Build;

public class StartSessionActivity extends ActionBarActivity {
	private Chronometer mChrono;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start_session);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
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
			mChrono = new Chronometer(context);
			Button start = new Button(context);
			Button stop = new Button(context);
			LinearLayout layout = new LinearLayout(context);
			layout.setOrientation(1);
			//SET THE TEXT AND ACTIONS;
	        start.setText("Start");
	        start.setOnClickListener(new startListener());
	        stop.setText("Stop");
	        stop.setOnClickListener(new stopListener());
			//ADD VIEWS
			layout.addView(mChrono);
			layout.addView(start);
			layout.addView(stop);
		}
	}
			
		public class startListener implements View.OnClickListener {
			public void onClick(View view) {
				mChrono.setBase(SystemClock.elapsedRealtime());
				//elapsedRealtime is time from device boot up.
				mChrono.start(); 
			}
		}
		
		public class stopListener implements View.OnClickListener {
			public void onClick(View view) {
				mChrono.stop();
			}
		}
}

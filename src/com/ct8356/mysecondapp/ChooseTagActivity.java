package com.ct8356.mysecondapp;

import com.ct8356.mysecondapp.DbContract.Tags;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.os.Build;

public class ChooseTagActivity extends ActionBarActivity {
	private DbHelper mDbHelper;
	private static final int CREATE_TAG=0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDbHelper = new DbHelper(this);
		updateContent();
	}
	
	@SuppressWarnings("deprecation")
	public void updateContent() {
		//Do database stuff
		mDbHelper.openDatabase();
		Cursor cursor;
		cursor = mDbHelper.getAllTagsCursor();
		//MAKE VIEWS
		ListView listView = new ListView(this);
        startManagingCursor(cursor);
		// Create an array to specify the fields we want to display in the list (only TITLE)
        String[] from = new String[]{Tags.TAG};
        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[]{R.id.textView1};
        // Now create a simple cursor adapter and set it to display
		SimpleCursorAdapter cursorAdapter = 
				new SimpleCursorAdapter(this, R.layout.notes_row, cursor, from, to);
		//Note, if use SCAdapter, have to use resources to define views.
		listView.setAdapter(cursorAdapter); //This works. It takes data from cursor to fill list.
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	        			public void onItemClick(AdapterView l, View v, int position, long id) {
	        				goTimeAccumulator(id);
	        			}});
		setContentView(listView);
		mDbHelper.close(); //duh! this supposed to come AFTER cursor was used!
	}
	
//	public void updateContent2() {
//		//Do database stuff
//		mDbHelper.openDatabase();
//		Cursor cursor;
//		cursor = mDbHelper.getRawCursor();
//		String stringTag = "Tags:";
//		int count = cursor.getCount();
//		while (cursor.moveToNext()) {
//			stringTag += cursor.getString(1);
//		}
//		mDbHelper.close();
//		//MAKE VIEWS
//		TextView textView1 = new TextView(this);
//		LinearLayout layout = new LinearLayout(this);
//		textView1.setText(stringTag);
//		layout.addView(textView1);
//		setContentView(layout);
//	}
	
	public void goTimeAccumulator(long id) {
		Intent intent = new Intent();
		intent.putExtra("tag", String.valueOf(id)); 
		//it seems the intent has a null... try HARDCODE as STRING?
		setResult(RESULT_OK, intent);
		finish();
	}
	
    @Override
    protected void onPause() {
        super.onPause();
    }
    
    @Override
    protected void onStop() {
        super.onStop();
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.choose_tag, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
			case R.id.action_settings:
				return true;
			case R.id.action_create:
				goCreate();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
    
	public void goCreate(){
		Intent intent = new Intent(this, CreateTagActivity.class);
	    startActivityForResult(intent, CREATE_TAG);
	}
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
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
			View rootView = inflater.inflate(R.layout.fragment_choose_tag,
					container, false);
			return rootView;
		}
	}

}

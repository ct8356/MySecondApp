package com.ct8356.mysecondapp;

import java.util.ArrayList;
import java.util.List;

import com.ct8356.mysecondapp.DbContract.Tags;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.os.Build;
import android.widget.AbsListView;

public class TagManagerActivity extends ActionBarActivity {
	private DbHelper mDbHelper;
	private static final int CREATE_TAG=0;
	private List<String> mTagNames;
	private List<Boolean> mItemCheckedQ = new ArrayList<Boolean>();
    private boolean[] mChecked;
    public CustomAdapter mCustomAdapter;
    private ListView mListView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDbHelper = new DbHelper(this);
		//DO DATABASE STUFF
		mDbHelper.openDatabase();
		Cursor cursor = mDbHelper.getCursorTags();
		mTagNames = new ArrayList<String>();
		while (cursor.moveToNext()) {
			mTagNames.add(cursor.getString(cursor.getColumnIndex(Tags.TAG)));
		}
		mDbHelper.close();
		//OTHER
		mCustomAdapter = new CustomAdapter();
        mChecked = new boolean[mCustomAdapter.getCount()];
        for(int i=0; i<mChecked.length; i++){
            mChecked[i]=false;
        }
		updateContent();
	}
	
	public void updateContent() {
		mListView = new ListView(this);
		//Ahah, remember, if want to get from XML, often need to inflate it!
		Button done = new Button(this);
		done.setText("Done");
		mListView.setAdapter(mCustomAdapter);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	        			public void onItemClick(AdapterView l, View v, int position, long id) {
	        				toggle(position);	
	        			}});
		setContentView(mListView);	
	}
	
	public void toggle(int position) {
		boolean[] checked = mChecked;
		mChecked[position] = !mChecked[position];
		checked = mChecked;
		View view = mListView.getChildAt(position);
		mListView.getAdapter().getView(position, view, mListView);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tag_manager, menu);
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
		case R.id.action_done:
			goHome();
			return true;
		case R.id.action_create:
			goCreateTag();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public ArrayList<String> getCheckedTags() {
		ArrayList<String> checkedTags = new ArrayList<String>();
		for (int i=0; i<mTagNames.size(); i+=1) {
			if (mChecked[i]) {
				checkedTags.add(mTagNames.get(i));
			}
		}
		return checkedTags;
	}
	
	public void goHome() {
		Intent intent = new Intent();
		ArrayList<String> checkedTags = getCheckedTags();
		intent.putStringArrayListExtra("tags", checkedTags); 
		setResult(RESULT_OK, intent);
		finish();
	}
	
	public void goCreateTag() {
		Intent intent = new Intent(this, CreateTagActivity.class);
	    startActivityForResult(intent, CREATE_TAG);
	}
	
	private class CustomAdapter extends BaseAdapter {
	    public int getCount() {
			mDbHelper.openDatabase();
			Cursor cursor = mDbHelper.getCursorTags();
			int count = cursor.getCount();
			mDbHelper.close();
			return count;
	    }
	
	    public String getItem(int position) {	
	        return mTagNames.get(position);
	    }
	
	    public long getItemId(int position) {
	    	//Not needed just yet.
	        return 0;
	    }
	    
	    @Override
	    public View getView(int pos, View convertView, ViewGroup parent) {
	         LinearLayout view = (LinearLayout) convertView;
	         if (view == null) {
	              view = (LinearLayout) getLayoutInflater().
	            		  inflate(R.layout.row_tag_manager, parent, false);
	         }
	         TextView tv = (TextView) view.findViewById(R.id.textView1);
	         tv.setText(getItem(pos));
	         CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox1);
	         checkBox.setChecked(mChecked[pos]);    
	         return view;
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
			View rootView = inflater.inflate(R.layout.fragment_tag_manager,
					container, false);
			return rootView;
		}
	}
}
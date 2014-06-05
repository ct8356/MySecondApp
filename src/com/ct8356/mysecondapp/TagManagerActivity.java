package com.ct8356.mysecondapp;

import java.util.ArrayList;
import java.util.Arrays;
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
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.os.Build;
import android.widget.AbsListView;

public class TagManagerActivity extends ActionBarActivity {
	private DbHelper mDbHelper;
	private static final int CREATE_TAG = 0;
	private static final int EDIT_TAG = Menu.FIRST;
	private static final int DELETE_TAG = Menu.FIRST + 1;
	//private List<String> mTagIds;
	private List<String> mTagNames; //a key variable. it is called by getItem().
	//private List<String> mCheckedTagIds;
	//private List<String> mCheckedTags; //Just a translation variable. Can be local.
	private List<Boolean> mChecked; //a key variable. Called by getView().
    public CustomAdapter mCustomAdapter;
    private ListView mListView;

	public List<String> getCheckedTags() { 
		//now this is only called once at end, when needed.
		//and, one less member variable.
		List<String> checkedTags = new ArrayList<String>();
		for (int i=0; i<mTagNames.size(); i+=1) {
			if (mChecked.get(i)) {
				checkedTags.add(mTagNames.get(i));
			}
		}
		return checkedTags;
	}

	public void goHome() {
		Intent intent = new Intent();
		List<String> checkedTags = getCheckedTags();
		intent.putStringArrayListExtra("tags", (ArrayList<String>) checkedTags); 
		setResult(RESULT_OK, intent);
		finish();
	}
	
	public void goCreateTag() {
		Intent intent = new Intent(this, CreateTagActivity.class);
	    startActivityForResult(intent, CREATE_TAG);
	}
	
	public void goEditTag(Long rowId) {
		Intent intent = new Intent(this, CreateTagActivity.class);
		intent.putExtra(Tags._ID, rowId); 
	    startActivityForResult(intent, CREATE_TAG);
	}
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        int previousSize = mTagNames.size();
        updateMTagNames();
        if (mTagNames.size() > previousSize) {
        	mChecked.add(true);
        }
    }
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDbHelper = new DbHelper(this);
		mCustomAdapter = new CustomAdapter();
		initialiseMemberVariables();
		initialiseContent();
		registerForContextMenu(mListView);
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
	
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, EDIT_TAG, 0, R.string.menu_edit);
        menu.add(0, DELETE_TAG, 0, R.string.menu_delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        List<String> tag = Arrays.asList(mTagNames.get(info.position));
        //converts string to List. Ok since don't need to add() to this list.
        mDbHelper.openDatabase();
        Long rowId = Long.valueOf(mDbHelper.getTagIds(tag).get(0));
    	switch(item.getItemId()) {
        case EDIT_TAG:
            goEditTag(rowId);
            return true;
        case DELETE_TAG:
            mDbHelper.deleteTagAndJoins(rowId);
            updateMTagNames(); //not enough. Must notifyListViewOfChange? Yes.
            mCustomAdapter.notifyDataSetChanged();
            mChecked.remove(info.position);
            return true;
        }
        mDbHelper.close();
        return super.onContextItemSelected(item);
    }

	public void initialiseContent() {
		//OTHER
		mListView = new ListView(this);
		//Ahah, remember, if want to get from XML, often need to inflate it!
		mListView.setAdapter(mCustomAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener());
		setContentView(mListView);	
	}
	
	public void initialiseMemberVariables() {
		mTagNames = new ArrayList<String>();
		Bundle extras = getIntent().getExtras();
		List<String> checkedTags = extras.getStringArrayList("tags");
		mChecked = new ArrayList<Boolean>();
		//DO DATABASE STUFF
		mDbHelper.openDatabase();
		mTagNames = mDbHelper.getAllTags(Tags.TAG);
		for (int i=0; i<mTagNames.size(); i+=1) {
			if (checkedTags.contains(mTagNames.get(i))) {
				mChecked.add(true);
			} else {
				mChecked.add(false);
			}
		}
		mDbHelper.close();
	}
	
	public void updateMTagNames() {
		//mTagNames = new ArrayList<String>();
		mDbHelper.openDatabase();
		mTagNames = mDbHelper.getAllTags(Tags.TAG);
		mDbHelper.close();
	}
	
	public void toggle(int position) {
		//update "key" member variable
		mChecked.set(position, !mChecked.get(position));
		//Now update the view in the ListView.
		int index = position - mListView.getFirstVisiblePosition();
		View convertView = mListView.getChildAt(index); 
		mListView.getAdapter().getView(position, convertView, mListView);
	}
	
	private class CustomAdapter extends BaseAdapter {
	    public int getCount() {
			mDbHelper.openDatabase();
			Cursor cursor = mDbHelper.getAllTagsCursor();
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
	         checkBox.setChecked(mChecked.get(pos));    
	         return view;
	    }
	}
	
	public class OnItemClickListener implements AdapterView.OnItemClickListener {
		public void onItemClick(AdapterView listView, View v, int position, long id) {
			toggle(position);	
		}
	}
}

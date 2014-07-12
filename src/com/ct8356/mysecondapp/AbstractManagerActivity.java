package com.ct8356.mysecondapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.ct8356.mysecondapp.DbContract.Minutes;
import com.ct8356.mysecondapp.DbContract.MinutesToTagJoins;
import com.ct8356.mysecondapp.DbContract.Tags;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.animation.AnimatorSet.Builder;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.os.Build;

public abstract class AbstractManagerActivity extends AbstractActivity 
implements AdapterView.OnItemSelectedListener {
	private DbHelper mDbHelper;
	protected SharedPreferences mPrefs;
	protected static final int CREATE_ENTRY = 0;
	protected static final int SELECT_TAGS = 1;
	protected static final int EDIT_ENTRY = 2;
	private static final int DELETE_ENTRY = 3;
	private static final int DIALOG_ALERT = 10;
	protected String PREF_NAME = "com.ct8356.mysecondapp.currentproject";
	private List<List<String>> mEntries; //a key variable. it is called by getItem().
	//Note, every inner list, represents a row.
	//private List<String> mCheckedEntryNames; //Just a translation variable. Can be local.
	private List<Boolean> mChecked; //a key variable. Called by getView().
    public CustomAdapter mCustomAdapter;
    protected TagNamesAdapter mTagNamesAdapter;
    private ListView mListView;
    protected Spinner mSpinner;
    protected String mTableName; //CBTL what does protected mean?
    protected String mCreatorActivity; //leave just in case decide to use it.
    private LinearLayout mLayout;
    protected List<String> mTagNames; //a key variable. it is called by getItem().
    protected List<String> mSelectedTags; //NOTE! Use this, but with single tag in it,
    //want to keep this in shared preferences!?
	private List<String> mColumnNames;
    //private Button mSelectedTagsText;
    
	public List<String> getCheckedEntryIds() { 
		List<String> checkedEntryIds = new ArrayList<String>();
		for (int i=0; i<mEntries.size(); i+=1) {
			//size() here will give number of rows.
			if (mChecked.get(i)) {
				checkedEntryIds.add(mEntries.get(i).get(0));
				//get(0) gets 0th column, which is always _ID.
			}
		}
		return checkedEntryIds;
	} //Not currently used, but save for later...
	
	public void goBackToStarter() {
		Intent intent = new Intent();
		intent.putStringArrayListExtra(DbContract.TAG_NAMES, 
				(ArrayList<String>) mSelectedTags); 
		setResult(RESULT_OK, intent);
		finish();
	}
	
	public abstract void goCreateEntry();
	
	public abstract void goEditEntry(Long rowId);
	
	public void goSelectTags(View view) {
//		Intent intent = new Intent(AbstractManagerActivity.this, OneTagManagerActivity.class);
//		intent.putStringArrayListExtra(DbContract.TAG_NAMES, 
//				(ArrayList<String>) mSelectedTags); 
//		startActivityForResult(intent, SELECT_TAGS);
	    DialogFragment dFragment = new CustomDialogFragment();
	    dFragment.show(getSupportFragmentManager(), "selectTag");
	}

	public void initialiseViews() {
		//LAYOUT
		mLayout = (LinearLayout) getLayoutInflater().
				  inflate(R.layout.abstract_manager, null);
		setContentView(mLayout);
		//mSelectedTagsText = (Button) findViewById(R.id.selected_tags);
		//mSelectedTagsText.setText(""+mSelectedTags);
		//SPINNER
		mSpinner = (Spinner) findViewById(R.id.selected_tag);
		mSpinner.setAdapter(mTagNamesAdapter);
		mSpinner.setOnItemSelectedListener(this);
		int pos = mTagNamesAdapter.getPosition(mSelectedTags.get(0));
		mSpinner.setSelection(pos);
		//LISTVIEW
		mListView = new ListView(this);
		mLayout.addView(mListView);
		mCustomAdapter = new CustomAdapter();
		mListView.setAdapter(mCustomAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener());
	}
	
	@SuppressLint("NewApi") //CBTL
	public void initialiseMemberVariables() {
		//List<String> checkedEntryIds = extras.getStringArrayList(DbContract.CHECKED_IDS);
		//Might need above if activity is killed during ActForResult, due to low memory.
		//mTableName = getIntent().getStringExtra(DbContract.TABLE_NAME);
		//Better to do it in subclass! CBTL.
		mTagNames = new ArrayList<String>();
		mDbHelper.openDatabase();
		mTagNames = mDbHelper.getAllEntriesColumn(Tags.TABLE_NAME, Tags.TAG);
		mDbHelper.close();
		mTagNamesAdapter = new TagNamesAdapter(this, R.layout.tag_name, 
				mTagNames);
		mPrefs = getSharedPreferences(PREF_NAME, 0);
		mSelectedTags = new ArrayList<String>();
		String selectedTagPref = mPrefs.getString(DbContract.TAG_NAMES, "Pref_no_exist");

		if (selectedTagPref != "Pref_no_exist") {
			mSelectedTags.add(selectedTagPref);
		} else {
			mSelectedTags.add(mTagNamesAdapter.getItem(0)); //Get first item in list.
		}
		//mSelectedTags = getIntent().getStringArrayListExtra(DbContract.TAG_NAMES);
		mCreatorActivity = getIntent().getStringExtra(DbContract.CREATOR_ACTIVITY);
		//not used, but keep just in case.
		updateMEntries();
		updateMChecked();
	}
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (resultCode) {
        case RESULT_CANCELED:
        	//Do nothing
        	break;
        case RESULT_OK:
        	mSelectedTags = intent.getStringArrayListExtra(DbContract.TAG_NAMES);
          	//mSelectedTagsText.setText(""+mSelectedTags);
            updateMEntries();
	        switch (requestCode) {
	        case CREATE_ENTRY:          
	            mChecked.add(true);
				break;
	        case EDIT_ENTRY:
	        	//Do nothing.
				break;
	        case SELECT_TAGS:
	        	updateMChecked();
	        	break;
	        }
	     	mCustomAdapter.notifyDataSetChanged();
	        break;
        }
    }
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDbHelper = new DbHelper(this);
		initialiseMemberVariables();
		initialiseViews();
		registerForContextMenu(mListView);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.abstract_manager, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			return true;
		case R.id.action_done:
			goBackToStarter();
			return true;
		case R.id.new_entry:
			goCreateEntry();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
	  switch (id) {
	    case DIALOG_ALERT:
	    CustomDialogFragment builder = new CustomDialogFragment(); 
	  }
	  return super.onCreateDialog(id);
	}
	
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, EDIT_ENTRY, 0, R.string.menu_edit);
        menu.add(0, DELETE_ENTRY, 0, R.string.menu_delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        List<String> entry = mEntries.get(info.position);
        mDbHelper.openDatabase();
        Long rowId = Long.valueOf(entry.get(0)); //0 gets _id.
    	switch(item.getItemId()) {
        case EDIT_ENTRY:
            goEditEntry(rowId);
            return true;
        case DELETE_ENTRY:
			String joinColumnName = mTableName+"ID"; //HARDCODE
            mDbHelper.deleteEntryAndJoins(mTableName, MinutesToTagJoins.TABLE_NAME,
            		joinColumnName, rowId);
            updateMEntries();
            mChecked.remove(info.position);
            mCustomAdapter.notifyDataSetChanged();
            return true;
        }
        mDbHelper.close();
        return super.onContextItemSelected(item);
    }
    
    public void onDataPass(String selectedTag) {
    	mSelectedTags = new ArrayList<String>();
    	mSelectedTags.add(selectedTag);
		//mSelectedTagsText.setText(""+mSelectedTags);
		updateMEntries();
		mCustomAdapter.notifyDataSetChanged();
    }
    
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mSelectedTags = savedInstanceState.getStringArrayList(DbContract.TAG_NAMES);
		//mSelectedTagsText.setText(""+mSelectedTags);
    	boolean[] checked = savedInstanceState.getBooleanArray(DbContract.CHECKED);
    	for (int i = 0; i < checked.length; i++) { 
    		mChecked.set(i, checked[i]); 
    	}
    	//CBTL, this is done twice, here and in onCreate...
	}
	
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putStringArrayList(DbContract.TAG_NAMES, 
				(ArrayList<String>) mSelectedTags);
		boolean[] checked = new boolean[mChecked.size()];
		for (int i = 0; i < mChecked.size(); i++) { checked[i] = mChecked.get(i); }
		savedInstanceState.putBooleanArray(DbContract.CHECKED, checked);
	}
	
	public void onStop() {
		super.onStop();
		//This is where supposed to do shared pref stuff.
		//SHARED PREF
		mPrefs = getSharedPreferences(PREF_NAME, 0); //0 is reqd mode
		SharedPreferences.Editor editor = mPrefs.edit();
		editor.putString(DbContract.TAG_NAMES, mSelectedTags.get(0));
		// Commit the edits!
		editor.commit();
	}
	
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        String selectedTag = parent.getItemAtPosition(pos).toString();
    	mSelectedTags = new ArrayList<String>();
    	mSelectedTags.add(selectedTag);
		//mSelectedTagsText.setText(""+mSelectedTags);
		updateMEntries();
		updateMChecked();
		mCustomAdapter.notifyDataSetChanged();
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing.
    }

	public void updateMChecked() {
		List<String> checkedEntryIds = new ArrayList<String>();
		mChecked = new ArrayList<Boolean>();
		for (int i=0; i<mEntries.size(); i+=1) {
			//mEntries.size returns number of rows.
			if (checkedEntryIds.contains(mEntries.get(i).get(0))) { //CBTL 0 for _ID
				mChecked.add(true);
			} else {
				mChecked.add(false);
			}
		}
	}
	
	public void updateMEntries() {
		mDbHelper.openDatabase();
		switch (mSelectedTags.size()) {
		case 0:
			mEntries = mDbHelper.getAllEntries(mTableName);
			//mEntries is then used by the listView in getView.
			break;
		default:
			List<String> timeEntryIds = mDbHelper.getRelatedEntryIds(mSelectedTags);
			mColumnNames = mDbHelper.getAllColumnNames(mTableName);
			mEntries = mDbHelper.getEntries(mTableName, mColumnNames, 
					"_id", timeEntryIds); //HARDCODE
			break;
		}
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
			return mEntries.size();
	    }
	
	    public List<String> getItem(int position) {	
	        return mEntries.get(position); //this effectively returns a row.
	    }
	
	    public long getItemId(int position) {
	    	//Not needed just yet.
	    	//CBTL, this is where your getting ID code should have gone...?
	        return 0;
	    }
	    
	    @Override
	    public View getView(int pos, View convertView, ViewGroup parent) {
	         LinearLayout view = (LinearLayout) convertView;
	         if (view == null) {
	        	 //Do all initialising here.
	             view = (LinearLayout) getLayoutInflater().
	            		  inflate(R.layout.row_abstract_manager, parent, false);
	 	         for (int i = 0; i < getItem(pos).size(); i += 1) {
			         TextView textView = new TextView(AbstractManagerActivity.this);
			         view.addView(textView);
			         LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                             LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f);
			         textView.setLayoutParams(params);
		         }
	         }
	         for (int i = 0; i < getItem(pos).size(); i += 1) {
	        	 TextView textView = (TextView) view.getChildAt(i+1); 
	        	 //+1 because of checkbox //HARDCODE
		         textView.setText("  "+getItem(pos).get(i)); //get the ith column.
	         } //expensive to do this every time getView is called?
	         // No more than inflating i think... CBTL. 
	         CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox1);
	         checkBox.setChecked(mChecked.get(pos));
	         return view;
	    }
	}
	
	private class TagNamesAdapter extends ArrayAdapter<String> {
	    public TagNamesAdapter(Context context, int resource, List<String> list) {
			super(context, resource, list);
		}
	}

	public class OnItemClickListener implements AdapterView.OnItemClickListener {
		public void onItemClick(AdapterView listView, View v, int position, long id) {
			toggle(position);	
		}
	} //NOte, could probs put this in XML, if put listView in XML.
	
}

package com.ct8356.mysecondapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.ct8356.mysecondapp.DbContract.Minutes;
import com.ct8356.mysecondapp.DbContract.MTJoins;
import com.ct8356.mysecondapp.DbContract.Tags;
import com.ct8356.mysecondapp.TimeEntryCreatorActivity.CustomAdapter;

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
import android.widget.Toast;
import android.os.Build;

public abstract class AbstractManagerActivity extends AbstractActivity {
	protected static final int CREATE_ENTRY = 0;
	protected static final int SELECT_TAGS = 1;
	protected static final int EDIT_ENTRY = 2;
	private static final int DELETE_ENTRY = 3;
	protected static final int CREATE_TAG = 4;
	private static final int DIALOG_ALERT = 10;
	protected List<List<String>> mEntries; //a key variable. it is called by getItem().
	//Note, every inner list, represents a row.
	//private List<String> mCheckedEntryNames; //Just a translation variable. Can be local.
	protected List<Boolean> mChecked; //a key variable. Called by getView().
    public CustomAdapter mCustomAdapter;
    protected ListView mListView;
    protected Spinner mSpinner;
    protected String mTableName; //CBTL what does protected mean?
    protected String mCreatorActivity; //leave just in case decide to use it.
    protected LinearLayout mLayout;
	protected List<String> mColumnNames;
	protected List<Integer> mPositionsToDelete;
	//HAHA! This global variable ended up saving you so much effort 
	//(saved so much passing, and getting, and translating and translator methods)! 
	//Just use global variables there is a chance it might help!
	//Can make code so much neater and easier!
    //private Button mSelectedTagsText;
    protected List<String> mTagNames; //a key variable. it is called by getItem().
    //want to keep this in shared preferences!?
	
	public void deleteSelectedEntries() {
		for (int pos = mEntries.size()-1; pos >= 0; pos--) {
			//reversed so that deletes from end, and does not confuse itself.
			if (mPositionsToDelete.contains(pos)) {
				deleteEntry(pos);
			}
		}
	    updateMEntries();
	    mCustomAdapter.notifyDataSetChanged();
	}
	
	public void deleteEntry(int pos) {
		Long rowId = getRowId(pos);
		String joinColumnName = mTableName+"ID"; //HARDCODE
		mDbHelper.openDatabase();
	    mDbHelper.deleteEntryAndJoins(mTableName, MTJoins.TABLE_NAME,
	    		joinColumnName, rowId);
	    mDbHelper.close();
	    mChecked.remove(pos);
	}
	
	public List<Integer> getCheckedPositions() {
		List<Integer> checkedPositions = new ArrayList<Integer>();
		for (int pos=0; pos<mEntries.size(); pos++) {
			if (mChecked.get(pos)) {
				checkedPositions.add(pos);
			}
		}
		return checkedPositions;
	}
	
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
		setResult(RESULT_OK, intent);
		finish();
	} //Needed? Well, depends on if want to use startActivityForResult...
	
	public void goCreateTag() {
		Intent intent = new Intent(this, TagCreatorAct.class);
	    startActivityForResult(intent, CREATE_TAG);
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

	@SuppressLint("NewApi") //CBTL
	public void initialiseMemberVariables() {
		//mCreatorActivity = getIntent().getStringExtra(DbContract.CREATOR_ACTIVITY);
		//not used, but keep just in case.
		updateMTagNames();
		mTagNamesAdapter = new TagNamesAdapter(this, R.layout.tag_name, 
				mTagNames);
		mCustomAdapter = new CustomAdapter();
		updateMSelectedTags();
		updateMEntries();
		updateMChecked();
	}
	
	public void initialiseViews() {
		//LISTVIEW
		mListView = new ListView(this);
		mLayout.addView(mListView);
		setAdapter();
		mListView.setOnItemClickListener(new OnItemClickListener());
		registerForContextMenu(mListView);
	}
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (resultCode) {
        case RESULT_CANCELED:
        	//Do nothing
        	break;
        case RESULT_OK:
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
	        case CREATE_TAG:
	        	//mTagNamesAdapter.notifyDataSetChanged(); 
	        	//Not sure this works for arrayAdapter...  
	        	updateMTagNames();
	    		mTagNamesAdapter = new TagNamesAdapter(this, R.layout.tag_name, 
	    				mTagNames);
	    		mSpinner.setAdapter(mTagNamesAdapter);
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
		case R.id.action_done:
			goBackToStarter();
			return true;
		case R.id.new_entry:
			goCreateEntry();
			return true;
		case R.id.delete_selected_entries:
			//Create that dialog
			mPositionsToDelete = getCheckedPositions();
			if (mPositionsToDelete.size() > 0) {
				showDeleteDialog();
			} else {
				Toast.makeText(this, "No entries selected.", Toast.LENGTH_LONG).show();
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
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
        Long rowId = getRowId(info.position);
    	switch(item.getItemId()) {
        case EDIT_ENTRY:
            goEditEntry(rowId);
            return true;
        case DELETE_ENTRY:
        	mPositionsToDelete = Arrays.asList(info.position);
        	showDeleteDialog();
            return true;
        }
        
        return super.onContextItemSelected(item);
    }
    
    private Long getRowId(int pos) {
        List<String> entry = mEntries.get(pos);
        Long rowId = Long.valueOf(entry.get(0)); //0 gets _id.
        return rowId;
	}

	public void onDataPass() {
    	//Method used for Dialog box.
		updateMEntries();
		updateMChecked();
		mCustomAdapter.notifyDataSetChanged();
    }
    
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
    	boolean[] checked = savedInstanceState.getBooleanArray(DbContract.CHECKED);
    	for (int i = 0; i < checked.length; i++) { 
    		mChecked.set(i, checked[i]); 
    	}
    	//CBTL, this is done twice, here and in onCreate...
	} //Nec?
	
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		boolean[] checked = new boolean[mChecked.size()];
		for (int i = 0; i < mChecked.size(); i++) { checked[i] = mChecked.get(i); }
		savedInstanceState.putBooleanArray(DbContract.CHECKED, checked);
	} //Nec? Now that not possible to rotate screen? Might want it incase destroyed for space.

	protected void setAdapter() {
		mCustomAdapter = new CustomAdapter();
		mListView.setAdapter(mCustomAdapter);
	}
	
	public void showDeleteDialog() {
		//Create that dialog
	    DialogFragment dFragment = new DeleteDF();
	    //Bundle bundle = new Bundle();
	    //bundle.putStringArrayList("positions", (ArrayList<String>) positions);
	    //dFragment.setArguments(bundle);
	    dFragment.show(getSupportFragmentManager(), "deleteDF");  
	    //note, this is the way you did it before....
	}
	
	public void updateMChecked() {
		List<String> checkedEntryIds = new ArrayList<String>();
		//not nec, but keep in case decide to use it.
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
		mEntries = mDbHelper.getAllEntries(mTableName);
		//mEntries is then used by the listView in getView.
		mDbHelper.close();
	}
	
	public void updateMTagNames() {
		mDbHelper.openDatabase();
		mTagNames = mDbHelper.getAllEntriesColumn(Tags.TABLE_NAME, Tags.TAG);
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
	
	protected class CustomAdapter extends BaseAdapter {
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
	
	protected class TagNamesAdapter extends ArrayAdapter<String> {
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

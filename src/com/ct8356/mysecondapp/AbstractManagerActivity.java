package com.ct8356.mysecondapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ct8356.mysecondapp.DbContract.Minutes;
import com.ct8356.mysecondapp.DbContract.MinutesToTagJoins;
import com.ct8356.mysecondapp.DbContract.Tags;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Intent;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.os.Build;

public abstract class AbstractManagerActivity extends ActionBarActivity {
	private DbHelper mDbHelper;
	protected static final int CREATE_ENTRY = 0;
	protected static final int SELECT_TAGS = 1;
	protected static final int EDIT_ENTRY = Menu.FIRST;
	private static final int DELETE_ENTRY = Menu.FIRST + 1;
	
	private List<List<String>> mEntries; //a key variable. it is called by getItem().
	//private List<String> mCheckedEntryNames; //Just a translation variable. Can be local.
	//Note, every inner list, represents a row.
	private List<Boolean> mChecked; //a key variable. Called by getView().
    public CustomAdapter mCustomAdapter; 
    private ListView mListView; 
    protected String mTableName; //CBTL what does protected mean?
    protected String mCreatorActivity; //leave just in case decide to use it.
    private LinearLayout layout;
    protected List<String> mSelectedTags;
	private List<String> mColumnNames;
    private TextView mSelectedTagsText;
    
	public List<String> getCheckedEntryIds() { 
		//now this is only called once at end, when needed.
		//and, one less member variable.
		List<String> checkedEntryIds = new ArrayList<String>();
		for (int i=0; i<mEntries.size(); i+=1) {
			//size() here will give number of rows.
			if (mChecked.get(i)) {
				checkedEntryIds.add(mEntries.get(i).get(0));
				//get(0) gets 0th column, which is _ID.
			}
		}
		return checkedEntryIds;
	}

	public void goHome() {
		Intent intent = new Intent();
		setResult(RESULT_OK, intent);
		finish();
	}
	
	public abstract void goCreateEntry();
	
	public abstract void goEditEntry(Long rowId); 
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (resultCode) {
        case RESULT_CANCELED:
        	//Do nothing
        	break;
        case RESULT_OK:
	        switch (requestCode) {
	        case CREATE_ENTRY:
	            updateMEntries();
	            mChecked.add(true);
				break;
	        case SELECT_TAGS:
	        	mSelectedTags = intent.getStringArrayListExtra("tags");
	        	updateMEntries();
	        	updateMChecked();
	        	//layout.invalidate();
	        	updateContent();
	        	mCustomAdapter.notifyDataSetChanged(); //nec?
	        	//Well solved the problem!
	        	//ofCourse! If textView doesn't auto-update, listView won't auto-update either!
	        	break;
	        }
	        break;
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
		switch (item.getItemId()) {
		case R.id.action_settings:
			return true;
		case R.id.action_done:
			goHome();
			return true;
		case R.id.action_create:
			goCreateEntry();
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
        List<String> entry = mEntries.get(info.position);
        mDbHelper.openDatabase();
        Long rowId = Long.valueOf(entry.get(0)); //0 gets _id.
    	switch(item.getItemId()) {
        case EDIT_ENTRY:
            goEditEntry(rowId);
            return true;
        case DELETE_ENTRY:
            mDbHelper.deleteEntryAndJoins(mTableName, "MinutesToTagJoins",
            		rowId); //Could be tricky to softcode. //HARDCODE
            updateMEntries(); //not enough. Must notify.
            mChecked.remove(info.position);
            mCustomAdapter.notifyDataSetChanged();
            return true;
        }
        mDbHelper.close();
        return super.onContextItemSelected(item);
    }

	public void initialiseContent() {
		//OTHER
		layout = (LinearLayout) getLayoutInflater().
				inflate(R.layout.fragment_abstract_manager, null);
		setContentView(layout);
		//since want to add views to content view (root view?), have to inflate it yourself.
		mSelectedTagsText = new TextView(this);
		mSelectedTagsText.setText("Selected tags: " + mSelectedTags);
		layout.addView(mSelectedTagsText);
		Button selectTags = new Button(this);
		selectTags.setText("Select tags");
		selectTags.setOnClickListener(new SelectTagsListener());
		layout.addView(selectTags);
		mListView = new ListView(this);
		layout.addView(mListView);
		//Ahah, remember, if want to get from XML, often need to inflate it!
		mListView.setAdapter(mCustomAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener());
		//setContentView(mListView);	
	}
	
	public void initialiseMemberVariables() {
		//List<String> checkedEntries = extras.getStringArrayList("tags"); // CBTL
		List<String> checkedEntryIds = new ArrayList<String>();
		checkedEntryIds.add("0"); //None of the ids will be 0.
		mTableName = getIntent().getStringExtra(DbContract.TABLE_NAME);
		mSelectedTags = getIntent().getStringArrayListExtra(DbContract.TAG_NAMES);
		mCreatorActivity = getIntent().getStringExtra(DbContract.CREATOR_ACTIVITY);
		updateMEntries();
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
	
	public void updateContent() {
		mSelectedTagsText.setText("Selected tags: "+mSelectedTags);
		//I guess that if one of layouts children modified, layout auto-invalidated...
	}
	
	public void updateMChecked() {
		List<String> checkedEntryIds = new ArrayList<String>();
		checkedEntryIds.add("0"); //None of the ids will be 0.
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
		//List<String> selectedTagIds = mDbHelper.getTagIds(mSelectedTags);
		//List<String> columnNames = Arrays.asList(MinutesToTagJoins.MINUTESID);
		//List<List<String>> timeEntryIdsList = mDbHelper.getEntries(MinutesToTagJoins.TABLE_NAME, 
		//		columnNames, MinutesToTagJoins.TAGID, selectedTagIds);
		//causes a crash.
		
//		List<String> timeEntryIds = new ArrayList<String>();
//		for (int i = 0; i < timeEntryIdsList.size(); i++) {
//			timeEntryIds.add(timeEntryIdsList.get(i).get(0));
//		}
		List<String> timeEntryIds = mDbHelper.getRelatedEntryIds(mSelectedTags);
		mColumnNames = mDbHelper.getAllColumnNames(mTableName);
		mEntries = mDbHelper.getEntries(mTableName, mColumnNames, Minutes._ID, timeEntryIds); 
		//get zero gets first row.
		//this is then used by the listView in getView.
		mDbHelper.close();
		//CBTL maybe do not want to get all columns. Only desired columns.
	    //Ok for now, because not that many columns.
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
	    	//Cool. getItem is part of the Adapter interface, but can return any object
	    	//So, do not need to use CursorAdapter afterall.
	    	//(not that there is anything wrong with CA, just means change is nec.
	        return mEntries.get(position); //this effectively returns a row.
	    }
	
	    public long getItemId(int position) {
	    	//Not needed just yet.
	    	//CBTL, this is where your getting ID code should have gone.
	        return 0;
	    }
	    
	    @Override
	    public View getView(int pos, View convertView, ViewGroup parent) {
	         LinearLayout view = (LinearLayout) convertView;
	         if (view == null) {
	        	 //Do all initialising here.
	             view = (LinearLayout) getLayoutInflater().
	            		  inflate(R.layout.row_manager, parent, false);
	 	         for (int i = 0; i < getItem(pos).size(); i += 1) {
			         TextView textView = new TextView(AbstractManagerActivity.this);
			         view.addView(textView);
		         }
	         }
	         //I believe this calls for the view to be built dynamically.
	         //i.e. based on whatever is returned by getItem().
	         //List<TextView> textViews = new ArrayList<TextView>();
	         for (int i = 0; i < getItem(pos).size(); i += 1) {
	        	 TextView textView = (TextView) view.getChildAt(i+1); //+1 because of checkbox
		         textView.setText("  "+getItem(pos).get(i)); //get the ith column.
		         //Yes! it works!
	         } //expensive to do this every time getView is called?
	         // No more than inflating i think... CBTL. 
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
	
	public class SelectTagsListener implements View.OnClickListener {
		public void onClick(View view) {
			Intent intent = new Intent(AbstractManagerActivity.this, TagManagerActivity.class);
			intent.putStringArrayListExtra("tags", (ArrayList<String>) mSelectedTags); 
			startActivityForResult(intent, SELECT_TAGS);
		}
	}
	
}

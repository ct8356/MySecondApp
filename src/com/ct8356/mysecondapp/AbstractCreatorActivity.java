package com.ct8356.mysecondapp;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ct8356.mysecondapp.DbContract.MinutesToTagJoins;
import com.ct8356.mysecondapp.DbContract.Tags;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.os.Build;

public abstract class AbstractCreatorActivity extends ActionBarActivity {
    private DbHelper mDbHelper;
    private Long mRowId;
    private List<String> mSelectedTags;
	protected String mTableName;
	//private List<String> mColumnNames; //Leave in case use again.
	protected int mColumnCount;
	private int mRequestCode;
	private int mFixedViewCount;
	private LinearLayout mLayout;

	public void goBackToStarter() {
			setResult(RESULT_OK);
			finish();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initialiseMemberVariables();
		initialiseViews();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.abstract_creator, menu);
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
		}
		return super.onOptionsItemSelected(item);
	}
	
    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }
    
    public void initialiseMemberVariables() {
		mTableName = getIntent().getStringExtra(DbContract.TABLE_NAME);
		mDbHelper = new DbHelper(this);
		mSelectedTags = getIntent().getStringArrayListExtra(DbContract.TAG_NAMES);
		mFixedViewCount = 1; //HARDCODE
		mDbHelper.openDatabase();
		Cursor cursor = mDbHelper.getAllEntriesCursor(mTableName);
		//mColumnNames = Arrays.asList(cursor.getColumnNames());
		mColumnCount = cursor.getColumnCount();
		mDbHelper.close();
		mRequestCode = getIntent().getIntExtra(DbContract.REQUEST_CODE, 
				AbstractManagerActivity.CREATE_ENTRY); 
		// CBTL means, if no request code, its a create_request.
		if (mRequestCode == AbstractManagerActivity.EDIT_ENTRY) {
			//If it was an edit request...
			mRowId = getIntent().getLongExtra(DbContract._ID, 0); //NullPointerException.
			//return zero, then cursor will be empty.
		}
    }
    
    public void initialiseViews() {
		mLayout = (LinearLayout) getLayoutInflater().
	    		  inflate(R.layout.fragment_abstract_creator, null);
		setContentView(mLayout);
		TextView mSelectedTagsText = new TextView(this);
		mLayout.addView(mSelectedTagsText);
		mSelectedTagsText.setText("Selected tags: "+mSelectedTags);
		TextView idText = new TextView(this);
		mLayout.addView(idText);
		for (int i = 1; i < mColumnCount; i += 1) {
	        mLayout.addView(new EditText(this));
	    }
		if (mRequestCode == AbstractManagerActivity.EDIT_ENTRY) {
			mDbHelper.openDatabase();
			List<String> rowId = Arrays.asList(String.valueOf(mRowId));
			List<List<String>> entry = mDbHelper.getEntries(mTableName, rowId);
			for (int i = 0; i < mColumnCount; i += 1) {
		        	TextView text = (TextView) mLayout.getChildAt(i+mFixedViewCount);
		        	text.setText(entry.get(0).get(i));
		        	//Note, textView is a super class of editText.
		    }
			mDbHelper.close();
		}
    }
	
    private void saveState() {
    	List<String> entry = new ArrayList<String>();
        for (int i = 1; i < mColumnCount; i += 1) {
        	//start with i=1, to skip the id column.
        	 EditText editText = (EditText) mLayout.getChildAt(i+mFixedViewCount); 
        	 //+2 because of enter entry, and Save button views.
   	         entry.add(editText.getText().toString());
        }
        String minutes = entry.get(0); //0 for minutes. HARDCODE
        mDbHelper.openDatabase();
        if (mRowId == null) {
			mRowId = mDbHelper.insertEntryAndJoins(mTableName, minutes, 
					MinutesToTagJoins.TABLE_NAME, mSelectedTags);
			//HARDCODE
        } else {
            mDbHelper.updateEntry(mTableName, entry, mRowId);
        }
		mDbHelper.close();
    }

}

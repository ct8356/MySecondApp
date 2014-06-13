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
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.SystemClock;
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
	protected DbHelper mDbHelper;
    protected Long mRowId;
    protected List<String> mSelectedTags;
	protected String mTableName;
	//private List<String> mColumnNames; //Leave in case use again.
	protected int mColumnCount;
	protected int mRequestCode;
	protected int mFixedViewCount;
	protected LinearLayout mLayout;
	protected TextView mSelectedTagsText;
	private static final int SELECT_MIN1_TAGS = 0;
	private static final int SELECT_TAGS = 1;
	
	public void goBackToStarter() {
		Intent intent;
		if (mSelectedTags.size() == 0) {
			intent = new Intent(this, Min1TagManagerActivity.class);
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
	
	public void goSelectTags(View view) {
		Intent intent = new Intent(this, TagManagerActivity.class);
		intent.putStringArrayListExtra(DbContract.TAG_NAMES, 
				(ArrayList<String>) mSelectedTags); 
		startActivityForResult(intent, SELECT_TAGS);
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (resultCode) {
        case RESULT_CANCELED:
        	//Do nothing
        	break;
        case RESULT_OK:
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
	            //null point exception.
	            break;
	    	}
	        break;
        }
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
    protected void onResume() {
        super.onResume();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }
    
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
    	mSelectedTags = savedInstanceState.getStringArrayList(DbContract.TAG_NAMES);
    	mSelectedTagsText.setText("Selected tags: "+mSelectedTags);
    	//CBTL, this is done twice, here and in onCreate...
	}
	
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putStringArrayList(DbContract.TAG_NAMES, 
				(ArrayList<String>) mSelectedTags);
	}
    
    public void initialiseMemberVariables() {
		mTableName = getIntent().getStringExtra(DbContract.TABLE_NAME);
		mDbHelper = new DbHelper(this);
		mSelectedTags = getIntent().getStringArrayListExtra(DbContract.TAG_NAMES);
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
			mDbHelper.openDatabase();
			mSelectedTags = mDbHelper.getRelatedTags(Arrays.asList(mRowId.toString()));
			mDbHelper.close();
			//return zero, then cursor will be empty.
		}
    }
    
    public abstract void initialiseViews();
	
    private void saveState() {
    	List<String> entry = new ArrayList<String>();
        for (int i = 1; i < mColumnCount; i += 1) {
        	//start with i=1, to skip the id column.
        	 TextView text = (TextView) mLayout.getChildAt(i+mFixedViewCount); 
        	 //+2 because of enter entry, and Save button views.
   	         entry.add(text.getText().toString());
        }
        String minutes = entry.get(0); //0 for minutes. HARDCODE
        mDbHelper.openDatabase();
        if (mRowId == null) {
			mRowId = mDbHelper.insertEntryAndJoins(mTableName, minutes, 
					MinutesToTagJoins.TABLE_NAME, mSelectedTags);//HARDCODE
        } else {
            mDbHelper.updateEntryAndJoins(mTableName, minutes, 
            		MinutesToTagJoins.TABLE_NAME, mSelectedTags, mRowId);//HARDCODE
        }
		mDbHelper.close();
    }

}

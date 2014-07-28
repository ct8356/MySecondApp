package com.ct8356.mysecondapp;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ct8356.mysecondapp.DbContract.MTJoins;
import com.ct8356.mysecondapp.DbContract.Tags;
import com.ct8356.mysecondapp.TimeEntryCreatorActivity.CustomAdapter;

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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.os.Build;

public abstract class AbstractCreatorEditorAct extends AbstractActivity {
    protected Long mRowId;
    protected List<String> mColumnNames;
	protected List<List<String>> mTable;
	protected String mTableName;
	//private List<String> mColumnNames; //Leave in case use again.
	protected int mColumnCount;
	protected int mRequestCode;
	protected int mFixedViewCount;
	protected LinearLayout mLayout;
	protected ListView mListView;
	protected static final int SELECT_MIN1_TAGS = 0;
	private static final int SELECT_TAGS = 1;
	protected CustomAdapter mCustomAdapter;
	
	public void goBackToStarter() {
		setResult(RESULT_OK);
		finish();
	}
	
	public void goSelectTags(View view) {
		Intent intent = new Intent(this, TagManagerActivity.class);
		intent.putStringArrayListExtra(DbContract.TAG_NAMES, 
				(ArrayList<String>) mSelectedTags); 
		startActivityForResult(intent, SELECT_TAGS);
	}
	
	public void initialiseMemberVariables() {
		updateMSelectedTags();
		mDbHelper.openDatabase();
		Cursor cursor = mDbHelper.getAllEntriesCursor(mTableName);
		//mColumnNames = Arrays.asList(cursor.getColumnNames());
		mColumnCount = cursor.getColumnCount();
		mDbHelper.close();
    }

	public abstract void initialiseViews();
	
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
	            //mSelectedTagsText.setText("Selected tags: "+mSelectedTags); 
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
    protected void onPause() {
        super.onPause();
        saveState();
    }
	
    @Override
    protected void onResume() {
        super.onResume();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }
    
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
    	mSelectedTags = savedInstanceState.getStringArrayList(DbContract.TAG_NAMES);
    	//mSelectedTagsText.setText("Selected tags: "+mSelectedTags);
    	//CBTL, this is done twice, here and in onCreate...
	}
	
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putStringArrayList(DbContract.TAG_NAMES, 
				(ArrayList<String>) mSelectedTags);
	}
	
    protected void saveState() {
    	List<String> entry = new ArrayList<String>();
        for (int iCol = 1; iCol < mColumnCount; iCol ++) {
        	 //start with i=1, to skip the id column.
        	 LinearLayout formLine = (LinearLayout) mListView.getChildAt(iCol);
        	 TextView text = (TextView) formLine.getChildAt(1);
   	         entry.add(text.getText().toString());
        }
        String minutes = entry.get(0); //0 for minutes. HARDCODE
        mDbHelper.openDatabase();
        if (mRowId == null) {
			mRowId = mDbHelper.insertEntryAndJoins(mTableName, minutes, 
					MTJoins.TABLE_NAME, mSelectedTags);//HARDCODE
        } else {
            mDbHelper.updateEntry(mTableName, entry, mRowId);//HARDCODE
        }
		mDbHelper.close();
    }
    
    protected abstract void setAdapter();
    
	public void updateMRowId() {
		mRowId = getIntent().getLongExtra(DbContract._ID, 0); //NullPointerException.
	}
    
    protected class CustomAdapter extends BaseAdapter {
	    public int getCount() {
			return mColumnNames.size(); //id, entry, date.
	    }
	
	    public String getItem(int position) {
			return "not_needed";	
	    }
	
	    public long getItemId(int position) {
	    	//Not needed just yet.
	        return 0;
	    }
	    
	    @Override
	    public View getView(int pos, View convertView, ViewGroup parent) {
	    	LinearLayout formLine = (LinearLayout) convertView;
	        if (formLine == null) {
	        	 //Do all initialising here.
	             formLine = (LinearLayout) getLayoutInflater().
	            		  inflate(R.layout.form_line, parent, false);
	        }
	    	TextView label = (TextView) formLine.getChildAt(0);
	        label.setText(mColumnNames.get(pos));
	        return formLine;
	    } //Perhaps could use ArrayAdapter, filled with LinearLayouts?
	}
    
}

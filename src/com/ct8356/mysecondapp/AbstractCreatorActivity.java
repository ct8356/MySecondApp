package com.ct8356.mysecondapp;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
	protected String mTableName;
	//private List<String> mColumnNames; //Leave in case use again.
	protected int mColumnCount;
	private LinearLayout mLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//INITIALISE STUFF
		mTableName = getIntent().getStringExtra(DbContract.TABLE_NAME);
		mDbHelper = new DbHelper(this);
		//DATABASE STUFF
		mDbHelper.openDatabase();
		Cursor cursor = mDbHelper.getAllEntriesCursor(mTableName);
		//mColumnNames = Arrays.asList(cursor.getColumnNames());
		mColumnCount = cursor.getColumnCount();
		mDbHelper.close();
		//VIEW STUFF
		mLayout = (LinearLayout) getLayoutInflater().
	    		  inflate(R.layout.fragment_abstract_creator, null);
		setContentView(mLayout); //Note, this method will auto inflate this view...?
		//but if want to edit a viewGroup dynamically, need to inflate the parent....?
		TextView id = new TextView(this);
		mLayout.addView(id);
		for (int i = 1; i < mColumnCount; i += 1) {
	        mLayout.addView(new EditText(this));
	    }
		int requestCode = getIntent().getIntExtra("requestCode", 
				AbstractManagerActivity.CREATE_ENTRY); 
		// CBTL means, if no request code, its a create_request.
		if (requestCode == AbstractManagerActivity.EDIT_ENTRY) {
			//If it was an edit request...
			mRowId = getIntent().getLongExtra(DbContract._ID, 0); //NullPointerException.
			//return zero, then cursor will be empty.
			//mRowId = (long) 5; //HARDCODE CBTL
			//DATABASE STUFF
			mDbHelper.openDatabase();
			List<String> rowId = new ArrayList<String>();
			rowId.add(String.valueOf(mRowId));
			List<List<String>> entry = mDbHelper.getEntries(mTableName, rowId);
			//might need a for loop here...
			for (int i = 0; i < mColumnCount; i += 1) {
		        	TextView text = (TextView) mLayout.getChildAt(i+2);
		        	text.setText(entry.get(0).get(i));
		        	//Note, textView is a super class of editText.
		    }
			//mEntryEdit.setText(entry.get(0).get(0)); //This seems to cause issues...
			//get 0th row, and 0th column.
			//set text to tagName. 
			mDbHelper.close();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_tag, menu);
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
	
	public void clickSaveEntry(View view) {
			setResult(RESULT_OK);
			finish();
	}
	
    private void saveState() {
    	//Don't actually need this for the first test. 
    	//Just want to see right number of text boxes in create!
    	List<String> entry = new ArrayList<String>();
        for (int i = 1; i < mColumnCount; i += 1) {
        	//start with i=1, to skip the id column.
        	 EditText editText = (EditText) mLayout.getChildAt(i+2); 
        	 //+2 because of enter entry, and Save button views. //HARDCODE
   	         entry.add(editText.getText().toString());
        }
        mDbHelper.openDatabase();
        if (mRowId == null) {
			mRowId = mDbHelper.insertEntry(mTableName, entry);
        } else {
            mDbHelper.updateEntry(mTableName, entry, mRowId);
        }
		mDbHelper.close();
    }

}

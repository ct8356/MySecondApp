package com.ct8356.mysecondapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ct8356.mysecondapp.DbContract.Tags;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.os.Build;

public class CreateTagActivity extends ActionBarActivity {
	//Note, should make this extend AbstractCreatorActivity...
	private EditText mTag;
    private DbHelper mDbHelper;
    private Long mRowId;
	
	public void goBackToStarter() {
		setResult(RESULT_OK);
		finish();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//INITIALISE STUFF
		mDbHelper = new DbHelper(this);
		setContentView(R.layout.create_tag);
		mTag = (EditText) findViewById(R.id.edit_create_tag);
		//needs inflating?
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			mRowId = extras.getLong(Tags._ID); //NullPointerException.
			//DATABASE STUFF
			mDbHelper.openDatabase();
			List<String> rowId = new ArrayList<String>();
			rowId.add(String.valueOf(mRowId));
			List<String> tags = mDbHelper.getEntryColumn(Tags.TABLE_NAME, Tags.TAG, 
					Tags._ID, rowId);
			mTag.setText(tags.get(0)); //This seems to cause issues...
			mDbHelper.close();
		}
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
	
   private void saveState() {
		mTag = (EditText) findViewById(R.id.edit_create_tag);
        String tag = mTag.getText().toString();
        if (tag.length() != 0) {
	        mDbHelper.openDatabase();
	        List<String> columnNames = mDbHelper.getAllColumnNames(Tags.TABLE_NAME);
	        if (mRowId == null) {
				mRowId = mDbHelper.insertEntry(Tags.TABLE_NAME, Arrays.asList(tag));
	        } else {
	            mDbHelper.updateEntry(Tags.TABLE_NAME, Arrays.asList(tag), mRowId);
	        }
			mDbHelper.close();
        }
    }
}

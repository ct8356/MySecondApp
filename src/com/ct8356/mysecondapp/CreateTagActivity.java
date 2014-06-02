package com.ct8356.mysecondapp;

import java.util.ArrayList;
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
	private EditText mTag;
    private DbHelper mDbHelper;
    private Long mRowId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//INITIALISE STUFF
		mDbHelper = new DbHelper(this);
		setContentView(R.layout.fragment_create_tag);
		mTag = (EditText) findViewById(R.id.edit_create_tag);
		//needs inflating?
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			mRowId = extras.getLong(Tags._ID); //NullPointerException.
			//DATABASE STUFF
			mDbHelper.openDatabase();
			List<String> rowId = new ArrayList<String>();
			rowId.add(String.valueOf(mRowId));
			List<String> tags = mDbHelper.getTags(rowId);
			mTag.setText(tags.get(0)); //This seems to cause issues...
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
	
	public void clickSaveTag(View view) {
			setResult(RESULT_OK);
			finish();
	}
	
   private void saveState() {
		mTag = (EditText) findViewById(R.id.edit_create_tag);
        String tag = mTag.getText().toString();
        if (tag.length() != 0) {
	        mDbHelper.openDatabase();
	        if (mRowId == null) {
				mRowId = mDbHelper.insertTag(tag);
	        } else {
	            mDbHelper.updateTag(mRowId, tag);
	        }
			mDbHelper.close();
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
			View rootView = inflater.inflate(R.layout.fragment_create_tag,
					container, false);
			return rootView;
		}
	}

}

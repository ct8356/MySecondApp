package com.ct8356.mysecondapp;

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
import android.widget.EditText;
import android.os.Build;

public class CreateTagActivity extends ActionBarActivity {
	private EditText mTag;
	private long mRowId;
    private DbHelper mDbHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDbHelper = new DbHelper(this);
		setContentView(R.layout.activity_create_tag);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
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
	
	public void clickSaveTag(View view) {
		try{
			setResult(RESULT_OK);
			finish();
		}catch (Exception e) {
			Log.e("ERROR", "ERROR IN CODE: " + e.toString());
			e.printStackTrace();
		}
	}
	
   private void saveState() {
		mTag = (EditText) findViewById(R.id.edit_create_tag);
        String tag = mTag.getText().toString();
        //if (mRowId == null) {
        mDbHelper.openDatabase();
		try {
			long newId = mDbHelper.insertTag(tag);
	        if (newId > 0) {
	            mRowId = newId;
	        } //necessary?
		} catch (SQLiteConstraintException e) {
			//Make pop saying "Tag already exists".
		}
        mDbHelper.close();
        //} else {
            //mDbHelper.updateNote(mRowId, title, body);
        //}
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

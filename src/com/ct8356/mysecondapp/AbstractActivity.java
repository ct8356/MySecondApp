package com.ct8356.mysecondapp;

import java.util.ArrayList;
import java.util.List;

import com.ct8356.mysecondapp.AbstractManagerActivity.TagNamesAdapter;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

public class AbstractActivity extends ActionBarActivity {
	protected SharedPreferences mPrefs;
    protected List<String> mSelectedTags; //NOTE! Use this, but with single tag in it,
    protected TagNamesAdapter mTagNamesAdapter;
	protected DbHelper mDbHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDbHelper = new DbHelper(this);
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.abstract_act, menu);
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

	public void updateMSelectedTags() {
		mPrefs = getSharedPreferences(DbContract.PREFS, 0);
		String selectedTagPref = mPrefs.getString(DbContract.TAG_NAMES, "Pref_no_exist");
		mSelectedTags = new ArrayList<String>();
		if (selectedTagPref != "Pref_no_exist") {
			mSelectedTags.add(selectedTagPref);
		} else {
			mSelectedTags.add(mTagNamesAdapter.getItem(0)); //Get first item in list.
		}
	}
	
	public void saveSelectedTags() {
		SharedPreferences prefs = getSharedPreferences(DbContract.PREFS, 0);
	    SharedPreferences.Editor editor = prefs.edit();
	    editor.putString(DbContract.TAG_NAMES, mSelectedTags.get(0));
	    editor.commit(); //ok, maybe if gonna use startActivityForResult, 
	}
}

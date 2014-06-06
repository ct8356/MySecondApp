package com.ct8356.mysecondapp;

import java.util.ArrayList;

import com.ct8356.mysecondapp.DbContract.Minutes;
import com.ct8356.mysecondapp.DbContract.Tags;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

public class TimeEntryManagerActivity extends AbstractManagerActivity {

	public void goCreateEntry() {
		Intent intent = new Intent(this, TimeEntryCreatorActivity.class);
		intent.putExtra(DbContract.TABLE_NAME, Minutes.TABLE_NAME);
		intent.putExtra("requestCode", CREATE_ENTRY);
	    startActivityForResult(intent, CREATE_ENTRY);
	}
	
	public void goEditEntry(Long rowId) {
		Intent intent = new Intent(this, TimeEntryCreatorActivity.class);
		intent.putExtra(DbContract.TABLE_NAME, Minutes.TABLE_NAME);
		intent.putExtra("requestCode", EDIT_ENTRY);
		intent.putExtra(DbContract._ID, rowId); 
	    startActivityForResult(intent, EDIT_ENTRY);
	}
	
}

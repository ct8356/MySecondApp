package com.ct8356.mysecondapp;

import java.util.Arrays;
import java.util.List;

import com.ct8356.mysecondapp.TimeEntryCreatorActivity.CustomAdapter;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.os.Build;

public class TagEditorAct extends TagCreatorAct {
	
	@Override
	public void initialiseMemberVariables() {
		super.initialiseMemberVariables();
		updateMRowId();
	}
	
	@Override
	protected void setAdapter() {
		mCustomAdapter = new CustomAdapter();
		mListView.setAdapter(mCustomAdapter);
	}
	
	protected class CustomAdapter extends TagCreatorAct.CustomAdapter {
	    @Override
	    public View getView(int pos, View convertView, ViewGroup parent) {
	    	View formLine = super.getView(pos, convertView, parent);
	    	EditText text = (EditText) ((ViewGroup) formLine).getChildAt(1);
	    	mDbHelper.openDatabase();
			List<String> rowId = Arrays.asList(String.valueOf(mRowId));
			List<List<String>> entry = mDbHelper.getEntries(mTableName, null, 
					DbContract._ID, rowId);
			mDbHelper.close();
	    	text.setText(entry.get(0).get(pos));
	    	return formLine;
	    } //Perhaps could use ArrayAdapter, filled with LinearLayouts?
	} //almost same as TimeEntry editor. Duplicate?

}

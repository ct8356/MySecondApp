package com.ct8356.mysecondapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ct8356.mysecondapp.AbstractManagerActivity.OnItemClickListener;
import com.ct8356.mysecondapp.DbContract.Minutes;

import android.widget.AdapterView;
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
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import android.os.Build;

public class TimeEntryCreatorActivity extends AbstractCreatorActivity implements AdapterView.OnItemClickListener {
	protected CustomAdapter mCustomAdapter;
	
	@Override
	public void initialiseViews() {
		//Lot of duplicate code here... needs improving...
		mColumnNames = new ArrayList<String>();
		mColumnNames.add("Id");
		mColumnNames.add("Minutes");
		mColumnNames.add("Date");
		//OTHER
		mLayout = (LinearLayout) getLayoutInflater().
	    		  inflate(R.layout.abstract_creator, null); //Should be a list view...
		setContentView(mLayout);
		mFixedViewCount = mLayout.getChildCount(); // should give 2? Now, 0.
   		//LISTVIEW
		mListView = new ListView(this);
		mLayout.addView(mListView);
		setAdapter();
		mListView.setOnItemClickListener(this);
    }
	
	protected void setAdapter() {
		mCustomAdapter = new CustomAdapter();
		mListView.setAdapter(mCustomAdapter);
	}
	
	protected class CustomAdapter extends BaseAdapter {
	    public int getCount() {
			return 3; //id, entry, date.
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
			//HARDCODE
 			EditText text = (EditText) formLine.getChildAt(1);
 			switch (pos) {
 			case 0:
 				text.setText("New");
 				text.setEnabled(false);
 				break;
 			case 1:
 				text.setHint("Enter minutes");
 				break;
 			case 2:
	 			text.setText(mDbHelper.getDateString());
	 			text.setEnabled(false);
	 			break;
	 		}
	        return formLine;
	    } //Perhaps could use ArrayAdapter, filled with LinearLayouts?
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
	}
	
	public void fillMTable() {
    	List<List<String>> twoDList = new ArrayList<List<String>>();
    	int iRow; int col;
		for (iRow=0; iRow<3; iRow++) {
		  	List<String> row = new ArrayList<String>();
			for (col=0; col<2; col++) {
				if (col == 0) row.add(mColumnNames.get(iRow)); 
				if (col == 1) row.add("");
			}
			twoDList.add(row);
		}
		mTable = twoDList;
    }
	//OR maybe, best just to fill it with empties, then have set text function later?
	
}


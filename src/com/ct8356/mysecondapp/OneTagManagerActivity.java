package com.ct8356.mysecondapp;

import java.util.ArrayList;
import java.util.List;

import com.ct8356.mysecondapp.DbContract.Tags;
import com.ct8356.mysecondapp.TagManagerActivity.OnItemClickListener;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.os.Build;

public class OneTagManagerActivity extends Min1TagManagerActivity {
	//NOTE! might want to not extend actionBarActivity. It means you can't 
	//set theme to DIALOG for APIlevel8.
	@Override
	public void initialiseViews() {
		mListView = new ListView(this);
		//Ahah, remember, if want to get from XML, often need to inflate it!
		mListView.setAdapter(mCustomAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener());
		setContentView(mListView);	
	} //Exactly same. Is it necessary?

	public void updateChecked(int position) {
		//FALSEiFY THE OLD CHECK
		int oldPos = mChecked.indexOf(true);
		if (oldPos >= 0) {
			mChecked.set(oldPos, false);
			//now update the list view
			int index = oldPos - mListView.getFirstVisiblePosition();
			View convertView = mListView.getChildAt(index); 
			mListView.getAdapter().getView(oldPos, convertView, mListView);
		}
		//TRUEiFY THE NEW CHECK
		mChecked.set(position, true);
		int index = position - mListView.getFirstVisiblePosition();
		View convertView = mListView.getChildAt(index); 
		mListView.getAdapter().getView(position, convertView, mListView);
	}
	
	protected class OnItemClickListener implements AdapterView.OnItemClickListener {
		public void onItemClick(AdapterView listView, View v, int position, long id) {
			updateChecked(position);	
			//goBackToStarter();
		}
	}

}

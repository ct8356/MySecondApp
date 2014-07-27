package com.ct8356.mysecondapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ct8356.mysecondapp.AbstractManagerActivity.CustomAdapter;
import com.ct8356.mysecondapp.DbContract.MTJoins;
import com.ct8356.mysecondapp.DbContract.Tags;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.os.Build;
import android.widget.AbsListView;

public class TagManagerActivity extends AbstractManagerActivity {
	//Note, could make this extend abstractManagerActivity...
	//private static final int EDIT_TAG = Menu.FIRST;
	//private static final int DELETE_TAG = Menu.FIRST + 1;
	protected List<String> mTagNames; //a key variable. it is called by getItem().
	protected CustomAdapter mCustomAdapter;
	
	public List<String> getCheckedTags() { 
		List<String> checkedTags = new ArrayList<String>();
		for (int i=0; i<mTagNames.size(); i+=1) {
			if (mChecked.get(i)) {
				checkedTags.add(mTagNames.get(i));
			}
		}
		return checkedTags;
	}
	
	public void goCreateEntry() {
		Intent intent = new Intent(this, TagCreatorAct.class);
	    startActivityForResult(intent, CREATE_ENTRY);
	}
	
	public void goEditEntry(Long rowId) {
		Intent intent = new Intent(this, TagEditorAct.class);
		intent.putExtra(DbContract._ID, rowId); 
	    startActivityForResult(intent, EDIT_ENTRY);
	}
	
	@Override
	public void initialiseMemberVariables() {
		mTableName = Tags.TABLE_NAME;
//		mTagNames = new ArrayList<String>();
//		mDbHelper.openDatabase();
//		mTagNames = mDbHelper.getAllEntriesColumn(mTableName, Tags.TAG);
//		mDbHelper.close();
//		updateMSelectedTags();
//		mChecked = new ArrayList<Boolean>();
//		for (int i=0; i<mTagNames.size(); i+=1) {
//			if (mSelectedTags.contains(mTagNames.get(i))) {
//				mChecked.add(true);
//			} else {
//				mChecked.add(false);
//			}
//		}
		super.initialiseMemberVariables();
	} //KEEP for now.
	
	@Override
	public void initialiseViews() {
		//LAYOUT
		mLayout = (LinearLayout) getLayoutInflater().
				  inflate(R.layout.abstract_manager, null);
		setContentView(mLayout);
		super.initialiseViews();
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        int previousSize = mTagNames.size();
        updateMTagNames();
        if (mTagNames.size() > previousSize) {
        	mChecked.add(true);
        }
    } //KEEP for now.

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.tag_manager, menu);
		return true;
	} //KEEP for now.
	
//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
//        super.onCreateContextMenu(menu, v, menuInfo);
//        menu.add(0, EDIT_TAG, 0, R.string.menu_edit);
//        menu.add(0, DELETE_TAG, 0, R.string.menu_delete);
//    } //KEEP for now.

//    @Override
//    public boolean onContextItemSelected(MenuItem item) {
//        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
//        List<String> tag = Arrays.asList(mTagNames.get(info.position));
//        //converts string to List. Ok since don't need to add() to this list.
//        mDbHelper.openDatabase();
//        Long rowId = Long.valueOf(mDbHelper.getEntryColumn(Tags.TABLE_NAME, Tags._ID, 
//        		Tags.TAG, tag).
//        		get(0));
//    	switch(item.getItemId()) {
//        case EDIT_TAG:
//            goEditEntry(rowId);
//            return true;
//        case DELETE_TAG:
//			String joinColumnName = "TAGID"; //HARDCODE
//            mDbHelper.deleteEntryAndJoins(Tags.TABLE_NAME, MinutesToTagJoins.TABLE_NAME, 
//            		joinColumnName, rowId);
//            updateMTagNames(); //not enough. Must notifyListViewOfChange? Yes.
//            mCustomAdapter.notifyDataSetChanged();
//            mChecked.remove(info.position);
//            return true;
//        }
//        mDbHelper.close();
//        return super.onContextItemSelected(item);
//    } //NEED to keep for now...
    
	public void updateMTagNames() {
		mDbHelper.openDatabase();
		mTagNames = mDbHelper.getAllEntriesColumn(Tags.TABLE_NAME, Tags.TAG);
		mDbHelper.close();
	}

}

package com.ct8356.mysecondapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ct8356.mysecondapp.DbContract.Tags;
import com.ct8356.mysecondapp.TimeEntryCreatorActivity.CustomAdapter;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.os.Build;

public class TagCreatorAct extends AbstractCreatorEditorAct {
	//Note, should make this extend AbstractCreatorActivity...

    @Override 
    public void initialiseMemberVariables() {
     	 mTableName = Tags.TABLE_NAME;
    	 super.initialiseMemberVariables();
    }
    
	@Override
	public void initialiseViews() {
		//Lot of duplicate code here... needs improving...
		mColumnNames = new ArrayList<String>();
		mColumnNames.add("Id");
		mColumnNames.add(getString(R.string.tag_name));
		//OTHER
		mLayout = (LinearLayout) getLayoutInflater().
	    		  inflate(R.layout.abstract_creator, null); //Should be a list view...
		setContentView(mLayout);
		mFixedViewCount = mLayout.getChildCount(); // should give 2? Now, 0.
   		//LISTVIEW
		mListView = new ListView(this);
		mLayout.addView(mListView);
		setAdapter();
    }
	
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		//INITIALISE STUFF
//		mDbHelper = new DbHelper(this);
//		setContentView(R.layout.create_tag);
//		mTag = (EditText) findViewById(R.id.edit_create_tag);
//		//needs inflating?
//		Bundle extras = getIntent().getExtras();
//		if (extras != null) {
//			mRowId = extras.getLong(Tags._ID); //NullPointerException.
//			//DATABASE STUFF
//			mDbHelper.openDatabase();
//			List<String> rowId = new ArrayList<String>();
//			rowId.add(String.valueOf(mRowId));
//			List<String> tags = mDbHelper.getEntryColumn(Tags.TABLE_NAME, Tags.TAG, 
//					Tags._ID, rowId);
//			mTag.setText(tags.get(0)); //This seems to cause issues...
//			mDbHelper.close();
//		}
//	}
	
   @Override
   protected void saveState() {
		//mTag = (EditText) findViewById(R.id.edit_create_tag);
	   	LinearLayout formLine = (LinearLayout) mListView.getChildAt(1); //1 for tagName, not id
  	 	TextView mTag = (TextView) formLine.getChildAt(1); //1 for text, not colName.
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
    } //Nec?

   @Override
   protected void setAdapter() {
		mCustomAdapter = new CustomAdapter();
		mListView.setAdapter(mCustomAdapter);
   }
   
	protected class CustomAdapter extends AbstractCreatorEditorAct.CustomAdapter {
	    @Override  
	    public View getView(int pos, View convertView, ViewGroup parent) {
	    	ViewGroup formLine = (ViewGroup) super.getView(pos, convertView, parent);
	    	//HARDCODE
 			EditText text = (EditText) formLine.getChildAt(1);
 			switch (pos) {
 			case 0:
 				Editable a = text.getText();
 				text.setText("New");
 				text.setEnabled(false);
 				break;
 			case 1:
 				text.setHint("Enter tag name");
 				//What? So when  I use getString(R.string.si) it messes up?
 				break;
	 		}
	        return formLine;
	    }
	}

}

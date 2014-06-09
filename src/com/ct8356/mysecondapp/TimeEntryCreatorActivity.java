package com.ct8356.mysecondapp;

import java.util.ArrayList;
import java.util.List;

import com.ct8356.mysecondapp.DbContract.Minutes;

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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.os.Build;

public class TimeEntryCreatorActivity extends AbstractCreatorActivity {

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
	
//  protected void saveState2() {
//      for (int i = 0; i < mColumnCount; i += 1) {
//     	 TextView textView = (TextView) view.getChildAt(i+1); //+1 because of checkbox
//	         textView.setText("  "+getItem(pos).get(i)); //get the ith column.
//	         //Yes! it works!
//      } //
//	  //mEntryEdit = (EditText) findViewById(R.id.edit_create_entry);
//	  String tableName = Minutes.TABLE_NAME;
//      List<String> entry = new ArrayList<String>();
//      mEntryEdit.getText().toString();
//      List<String> columnNames;
//      if (entry.length() != 0) {
//	        mDbHelper.openDatabase();
//	        if (mRowId == null) {
//				mRowId = mDbHelper.insertEntry(tableName, columnNames, entry);
//	        } else {
//	            mDbHelper.updateTag(mRowId, entry);
//	        }
//			mDbHelper.close();
//      }
//  }

}

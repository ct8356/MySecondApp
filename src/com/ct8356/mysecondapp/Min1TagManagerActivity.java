package com.ct8356.mysecondapp;

import java.util.ArrayList;
import java.util.List;

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

public class Min1TagManagerActivity extends TagManagerActivity {
	
	@Override
	public void goHome() {
		List<String> checkedTags = getCheckedTags();
		if (checkedTags.size() == 0){
			return;
		}
		Intent intent = new Intent();
		intent.putStringArrayListExtra(DbContract.TAG_NAMES, 
				(ArrayList<String>) checkedTags); 
		setResult(RESULT_OK, intent);
		finish();
	}
}

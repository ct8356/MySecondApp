package com.ct8356.mysecondapp;

import java.util.ArrayList;
import java.util.List;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

public class Min1TagManagerActivity extends TagManagerActivity {
	
	@Override
	public void goBackToStarter() {
		//Bit of duplicate code here...
		mSelectedTags = getCheckedTags();
		if (mSelectedTags.size() == 0){
			//If no tags selected, do nothing.
			//Maybe a Toast alert?
			return;
		}     
		saveSelectedTags();
		super.goBackToStarter();
	}
}
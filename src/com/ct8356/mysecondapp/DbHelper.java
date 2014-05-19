package com.ct8356.mysecondapp;

import java.util.ArrayList;
import java.util.List;

import com.ct8356.mysecondapp.DbContract.Minutes;
import com.ct8356.mysecondapp.DbContract.Tags;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
	//Since SQLiteOpenHelper is useful (I like its getWritableDatabase method),
	//may as well use it. Means I have to have SQLiteOpenHelper and SQLiteDatabase,
	//But oh well.
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 7;
    public static final String DATABASE_NAME = "Minutes.db";
    public SQLiteDatabase mDb;
    
    public DbHelper(Context context) { //Makes a simpler constructor...
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    public Cursor getRawCursor() {
    	Cursor cursor = mDb.rawQuery("SELECT * FROM Tags", null);
    	return cursor;
    }
    
    public Cursor getCursor(){
    	Cursor cursor = mDb.query(
	    		true, 
	    		Minutes.TABLE_NAME, 
	    		new String[] {Minutes._ID, Minutes.MINUTES}, 
	    		"projectName='Android'", // Potential ISSUE here, was "columnName = x".
	            null, null, null, null, null
	            );
    	return cursor;
    }
    
    public Cursor getCursorMinutes(String projectName){
    	Cursor cursor = mDb.query(
	    		true, 
	    		Minutes.TABLE_NAME, 
	    		new String[] {Minutes._ID, Minutes.MINUTES}, 
	    		"projectName = ?", // Potential ISSUE here, was "columnName = x".
	    		new String[] {projectName},
	    		null, null, null, null
	            );
    	return cursor;
    }
    
    public Cursor getCursorTags(){
    	Cursor cursor = mDb.query(
	    		true, 
	    		Tags.TABLE_NAME, 
	    		new String[] {Tags._ID, Tags.TAG}, 
	    		null, // Potential ISSUE here, was "columnName = x".
	    		null,
	    		null, null, null, null
	            );
    	return cursor;
    }
    
    public List<String> getAllIds(){
    	Cursor cursor = getCursor();
    	List<String> allIds = new ArrayList<String>();
    	String temp;
    	while (cursor.moveToNext()){
    		temp = cursor.getString(cursor.getColumnIndexOrThrow(Minutes._ID));
    		allIds.add(temp);
    	}
    	return allIds;
    }
    
    public String getAllTags() {
    	Cursor cursor = getCursorTags();
    	String allTags = "allTags:";
    	String temp;
    	while (cursor.moveToNext()){
    		temp = cursor.getString(cursor.getColumnIndexOrThrow(Tags.TAG));
    		allTags += temp;
    	}
		return allTags;
    	
    }
    
	public String getTag(long rowId) {
		Cursor cursor = getSingleTagCursor(rowId);
		String tag;
		cursor.moveToFirst();
		tag = cursor.getString(cursor.getColumnIndexOrThrow(Tags.TAG));
		return tag;
	}
    
    private Cursor getSingleTagCursor(long rowId) {
     	Cursor cursor = mDb.query(
	    		true, 
	    		Tags.TABLE_NAME, 
	    		new String[] {Tags._ID, Tags.TAG}, 
	    		"_id = "+ rowId, // Potential ISSUE here, was "columnName = x".
	            null, null, null, null, null
	            );
    	return cursor;
	}

	public int getRecordMinutes(long recordId){
		Cursor cursor = mDb.query(
	    		true, 
	    		Minutes.TABLE_NAME, 
	    		new String[] {Minutes._ID, Minutes.MINUTES}, 
	    		Minutes._ID + "=" + recordId, 
	            null, null, null, null, null
	            );
		cursor.moveToFirst();
		int minutes = cursor.getInt(cursor.getColumnIndexOrThrow(Minutes.MINUTES));
		return minutes;
    }
    
    public long insertRecord(String projectName, int minutes){
		ContentValues values = new ContentValues();
		values.put(Minutes.PROJECTNAME, projectName); 
		values.put(Minutes.MINUTES, minutes);
		//Insert the new row, returning the primary key value of the new row
		long newRecordId = mDb.insert(Minutes.TABLE_NAME,
				Minutes.MINUTES, //nullColumnHack
				values);
    	return newRecordId;
    }
    
    public long insertTag(String tag) throws SQLiteConstraintException {
		ContentValues values = new ContentValues();
		values.put(Tags.TAG, tag); 
			long newRecordId = mDb.insert(
					Tags.TABLE_NAME,
					Tags.TAG, //nullColumnHack
					values);
    	return newRecordId;
    }
    
    public int sumMinutes(String projectName){
    	//Cursor cursor = getCursor();
    	Cursor cursor = getCursorMinutes(projectName);
    	int sumMinutes = 0;
    	int temp;
    	while (cursor.moveToNext()){
    		temp = cursor.getInt(cursor.getColumnIndexOrThrow(Minutes.MINUTES));
    		sumMinutes += temp;
    	}
    	return sumMinutes;
    }
    
    public void openDatabase(){
	    mDb = getWritableDatabase();
    }
    
    public void onCreate(SQLiteDatabase db) { //i.e. called when getWritableDatabase called.
    	String SQL_CREATE_MINUTES =
    		    "CREATE TABLE " + Minutes.TABLE_NAME + " (" +
    		    Minutes._ID + " INTEGER PRIMARY KEY," +
    		    Minutes.PROJECTNAME + " TEXT," +
    		    Minutes.MINUTES + " TEXT )"; 
    	//The original horrible messy string above was proposed on developer.android.com.
    	//Probably is a much nicer "create" method in Java somewhere.
    	db.execSQL(SQL_CREATE_MINUTES);
    	String SQL_CREATE_TAGS =
    		    "CREATE TABLE " + Tags.TABLE_NAME + " (" +
    		    Tags._ID + " INTEGER PRIMARY KEY," +
    		    Tags.TAG + " TEXT UNIQUE)"; 
    	//The original horrible messy string above was proposed on developer.android.com.
    	//Probably is a much nicer "create" method in Java somewhere.
    	db.execSQL(SQL_CREATE_TAGS);
    }
    
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
    	String SQL_DELETE_MINUTES = "DROP TABLE IF EXISTS " + Minutes.TABLE_NAME;
        db.execSQL(SQL_DELETE_MINUTES);
      	String SQL_DELETE_TAGS = "DROP TABLE IF EXISTS " + Tags.TABLE_NAME;
        db.execSQL(SQL_DELETE_TAGS);
        onCreate(db);
    }
    
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }


}

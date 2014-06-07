package com.ct8356.mysecondapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.ct8356.mysecondapp.DbContract.Minutes;
import com.ct8356.mysecondapp.DbContract.MinutesToTagJoins;
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
    public static final int DATABASE_VERSION = 10;
    public static final String DATABASE_NAME = "Minutes.db";
    public SQLiteDatabase mDb;
    
    public DbHelper(Context context) { //Makes a simpler constructor...
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    public String buildSelectionString(List<String> rowIds) {
		String entryIdsString = join(rowIds, ",");
		String selectionString = DbContract._ID + " IN("+entryIdsString+")"; 
		return selectionString;
    }
    
    public Cursor getRawCursor() {
    	Cursor cursor = mDb.rawQuery("SELECT * FROM Tags", null);
    	return cursor;
    }
    
    public Cursor getAllEntriesCursor(String tableName) {
		Cursor cursor = mDb.query(
	    		false, //Don't really want distinct...
	    		tableName, 
	    		null,
	    		null,
	            null,
	            null, null, null, null
	            );
    	return cursor;
    }

    public Cursor getAllJoinsCursor() {
		Cursor cursor = mDb.query(
	    		false, //Don't really want distinct...
	    		MinutesToTagJoins.TABLE_NAME, 
	    		new String[] {MinutesToTagJoins._ID, MinutesToTagJoins.MINUTESID, 
	    				MinutesToTagJoins.TAGID}, 
	    		null, 
	            null, 
	            null, null, null, null
	            );
    	return cursor;
    }
    
    public Cursor getAllTagsCursor(){
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
    
    public Cursor getAllTimeEntriesCursor() {
		Cursor cursor = mDb.query(
	    		true, 
	    		Minutes.TABLE_NAME, 
	    		new String[] {Minutes._ID, Minutes.MINUTES}, 
	     		null,
	    		null, 
	    		null, null, null, null
	            ); //Needs IN().
		return cursor;
	}
    //Are any of these getAll methods needed anymore?
    
	public Cursor getJoinsCursor2(String joinTableName, String columnName, 
			List<String> entryIds) {
		String[] tagIdsSA = new String[entryIds.size()]; 
		entryIds.toArray(tagIdsSA);
		String entryIdsString = join(entryIds, ",");
		String selectionString = columnName + " IN("+entryIdsString+")"; 
		Cursor cursor = mDb.query(
	    		false, //not distinct 
	    		joinTableName, 
	    		null, //null means get all columns
	    		selectionString,
	    		null,
	    		null, null, null, null
	            ); //ALSO needs IN(),
		return cursor;
	}
	
	public Cursor getEntriesCursor(String tableName, List<String> rowIds) {
	  	String[] rowIdsSA = new String[rowIds.size()];
	  	rowIds.toArray(rowIdsSA);
		String entryIdsString = join(rowIds, ",");
		String selectionString = DbContract._ID + " IN("+entryIdsString+")"; 
		Cursor cursor = mDb.query(
	    		true, //Do want distinct
	    		tableName, 
	    		null, //gets all columns, although this is not efficient apparently.
	    		selectionString, 
	    		null, 
	            null, null, null, null
	            );
    	return cursor;
	}
    
	public Cursor getJoinsCursor(List<String> tagIds) {
		String[] tagIdsSA = new String[tagIds.size()]; 
		tagIds.toArray(tagIdsSA);
		String tagIdsString = join(tagIds, ",");
		String selectionString = MinutesToTagJoins.TAGID + " IN("+tagIdsString+")"; 
		Cursor cursor = mDb.query(
	    		true, 
	    		MinutesToTagJoins.TABLE_NAME, 
	    		new String[] {MinutesToTagJoins._ID, MinutesToTagJoins.MINUTESID, MinutesToTagJoins.TAGID}, 
	    		selectionString,
	    		null,
	    		null, null, null, null
	            ); //ALSO needs IN(),
		return cursor;
	}
	
	public Cursor getTimeEntryIdsFromJoinsCursor(List<String> tagIds) {
		String[] tagIdsSA = new String[tagIds.size()]; 
		tagIds.toArray(tagIdsSA);
		String tagIdsString = join(tagIds, ",");
		String selectionString = MinutesToTagJoins.TAGID + " IN("+tagIdsString+")"; 
		Cursor cursor = mDb.query(
	    		true, 
	    		MinutesToTagJoins.TABLE_NAME, 
	    		new String[] {MinutesToTagJoins.MINUTESID}, 
	    		selectionString,
	    		null,
	    		null, null, null, null
	            ); //ALSO needs IN(),
		return cursor;
	}
	
	public Cursor getJoinsCursorWithTimeEntryIds(List<String> timeEntryIds) {
		String timeEntryIdsString = join(timeEntryIds, ",");
		String selectionString = MinutesToTagJoins.MINUTESID + " IN("+timeEntryIdsString+")"; 
		Cursor cursor = mDb.query(
	    		true, 
	    		MinutesToTagJoins.TABLE_NAME, 
	    		new String[] {MinutesToTagJoins.TAGID}, 
	    		selectionString,
	    		null,
	    		null, null, null, null
	            ); //ALSO needs IN(),
		return cursor;
	}
	
	public Cursor getJoinsCursorWithANDCondition(List<String> tagIds) {
		for (int i = 0; i < tagIds.size(); i+=1) {
			tagIds.set(i, MinutesToTagJoins.TAGID + "=" + tagIds.get(i)); 
		}
		String selectionString = join(tagIds, " AND ");
		Cursor cursor = mDb.query(
	    		true, 
	    		MinutesToTagJoins.TABLE_NAME, 
	    		new String[] {MinutesToTagJoins._ID, MinutesToTagJoins.MINUTESID, MinutesToTagJoins.TAGID}, 
	    		selectionString,
	    		null,
	    		null, null, null, null
	            ); //ALSO needs IN(),
		return cursor;
	}
	
	public Cursor getEntriesCursor(String tableName, List<String> columnNames, 
			List<String> rowIds) {
	  	String[] columnNamesSA = (String[]) columnNames.toArray();
		String entryIdsString = join(rowIds, ",");
		String selectionString = DbContract._ID + " IN("+entryIdsString+")"; 
		Cursor cursor = mDb.query(
	    		false, //Don't want distinct
	    		tableName, 
	    		columnNamesSA,
	    		selectionString, 
	    		null, 
	            null, null, null, null
	            );
    	return cursor;
	}
	
	public Cursor getEntriesCursor(String tableName, List<String> columnNames, 
			String selectionColumnName, List<String> selections) {
	  	String[] columnNamesSA = (String[]) columnNames.toArray();
		String selectionsString = join(selections, ",");
		String selectionString = selectionColumnName + " IN("+selectionsString+")"; 
		Cursor cursor = mDb.query(
	    		false, //Don't want distinct
	    		tableName, 
	    		columnNamesSA,
	    		selectionString, 
	    		null, 
	            null, null, null, null
	            );
    	return cursor;
	}
	
	public Cursor getTagsCursor(List<String> rowIds) {
	  	String[] rowIdsSA = new String[rowIds.size()];
	  	rowIds.toArray(rowIdsSA);
		String tagIdsString = join(rowIds, ",");
		String selectionString = Tags._ID + " IN("+tagIdsString+")"; 
		Cursor cursor = mDb.query(
	    		true, 
	    		Tags.TABLE_NAME, 
	    		new String[] {Tags._ID, Tags.TAG}, 
	    		selectionString, 
	    		null, 
	            null, null, null, null
	            );
    	return cursor;
	}
    
	public Cursor getTagIdsCursor(List<String> tags) {
		String[] tagsSA = new String[tags.size()]; 
		tags.toArray(tagsSA);
		String tagsString = join(tags, "','");
		String selectionString = Tags.TAG + " IN('"+tagsString+"')"; 
    	Cursor cursor = mDb.query(
	    		true, 
	    		Tags.TABLE_NAME, 
	    		new String[] {Tags._ID, Tags.TAG}, 
	     		selectionString,
	    		null, 
	    		null, null, null, null
	            ); //Needs IN().
    	return cursor;
	} 
	//possible this could replace the method above...
	
	public Cursor getTimeEntriesCursor(List<String> timeEntryIds) { 
		//This method causing issues.
		//Perhaps there is no matching value?
		String[] timeEntryIdsSA = new String[timeEntryIds.size()]; 
		timeEntryIds.toArray(timeEntryIdsSA);
		String timeEntryIdsString = join(timeEntryIds, ",");
		String selectionString = Minutes._ID + " IN("+timeEntryIdsString+")"; 
		Cursor cursor = mDb.query(
	    		false, 
	    		Minutes.TABLE_NAME, 
	    		new String[] {Minutes._ID, Minutes.MINUTES}, 
	    		//needed minutesID in here...?
	    		selectionString,
	    		null,
	    		null, null, null, null);
		return cursor;
	}
	
	public List<String> getAllColumnNames(String tableName) {
		Cursor cursor = getAllEntriesCursor(tableName);
		List<String> columnNames = Arrays.asList(cursor.getColumnNames());
		return columnNames;
	}
	
	public List<List<String>> getAllEntries(String tableName) {
		Cursor cursor = getAllEntriesCursor(tableName);
		List<List<String>> entries = lookInCursor(cursor);
		return entries;
	}
	
	public List<String> getAllTags(String columnName) {
		Cursor cursor = getAllTagsCursor();
		List<String> tags = new ArrayList<String>();
		while (cursor.moveToNext()) {
			tags.add(cursor.getString(cursor.getColumnIndexOrThrow(columnName)));
		}
		return tags;
	}
	
	public List<List<String>> getEntries(String tableName, List<String> rowIds) {
		Cursor cursor = getEntriesCursor(tableName, rowIds);
		List<List<String>> entries = lookInCursor(cursor);
		return entries;
	}
	
	public List<List<String>> getEntries(String tableName, List<String> columnNames, 
			List<String> rowIds) {
		Cursor cursor = getEntriesCursor(tableName, columnNames, rowIds);
		List<List<String>> entries = lookInCursor(cursor);
		return entries;
	}
	
	public List<List<String>> getEntries(String tableName, List<String> columnNames, 
			String selectionColumnName, List<String> selections) {
		Cursor cursor = getEntriesCursor(tableName, columnNames, 
				selectionColumnName, selections);
		List<List<String>> entries = lookInCursor(cursor);
		return entries;
	}
	
	public List<String> getTags(List<String> rowIds) {
		Cursor cursor = getTagsCursor(rowIds);
		List<String> tags = new ArrayList<String>();
		while (cursor.moveToNext()) {
			tags.add(cursor.getString(cursor.getColumnIndexOrThrow(Tags.TAG)));
		}
		return tags;
	}

	public List<String> getTagIds(List<String> tags) {
		Cursor cursor = getTagIdsCursor(tags);
		List<String> tagIds = new ArrayList<String>(); //ahah, because List is abstract!
    	while (cursor.moveToNext()){
    		tagIds.add(cursor.getString(cursor.getColumnIndexOrThrow(Tags._ID)));
    	}
    	return tagIds;
	}
	
	public List<String> getTagIdsFromJoin(List<String> timeEntryIds) {
		Cursor cursor = getJoinsCursorWithTimeEntryIds(timeEntryIds);
		//List<List<String>> test = lookInCursor(cursor); 
		//Careful with this, it will move cursor to end!
		List<String> tagIds = new ArrayList<String>(); 
		//ahah, need ArrayList, because List is abstract!
    	while (cursor.moveToNext()){
    		tagIds.add(cursor.getString(cursor.getColumnIndexOrThrow(MinutesToTagJoins.TAGID)));
    	}
    	return tagIds;
	}
	
	public List<String> getTimeEntries(List<String> timeEntryIds) { 
		Cursor cursor = getTimeEntriesCursor(timeEntryIds);
		//List<List<String>> test = lookInCursor(cursor); 
		List<String> timeEntries = new ArrayList<String>();
		while (cursor.moveToNext()) {
			timeEntries.add(cursor.getString(cursor.getColumnIndexOrThrow(Minutes.MINUTES)));
		}
		return timeEntries;
	}

	public List<String> getTimeEntryIds(List<String> tagIds) {
		Cursor cursor = getTimeEntryIdsFromJoinsCursor(tagIds);
		//List<List<String>> test = lookInCursor(cursor); 
		//Careful with this, it will move cursor to end!
		List<String> timeEntryIds = new ArrayList<String>(); 
		//ahah, need ArrayList, because List is abstract!
    	while (cursor.moveToNext()){
    		timeEntryIds.add(cursor.getString(cursor.getColumnIndexOrThrow(MinutesToTagJoins.MINUTESID)));
    	}
    	return timeEntryIds;
	}
	
	public List<String> getTimeEntryIdsWithANDCondition(List<String> tagIds) {
		Cursor cursor = getJoinsCursorWithANDCondition(tagIds);
		//List<List<String>> test = lookInCursor(cursor); 
		//Careful with this, it will move cursor to end!
		List<String> ids = new ArrayList<String>(); 
		//ahah, need ArrayList, because List is abstract!
    	while (cursor.moveToNext()){
    		ids.add(cursor.getString(cursor.getColumnIndexOrThrow(MinutesToTagJoins._ID)));
    	}
    	return ids;
	}

    public long insertRecord(int minutes, List<String> tags){
		ContentValues values = new ContentValues();
		values.put(Minutes.MINUTES, minutes);
		long newTimeEntryId = mDb.insert(
				Minutes.TABLE_NAME,
				Minutes.MINUTES, //nullColumnHack
				values);
		//NOW ADD THE JOINS
		List<String> tagIds = getTagIds(tags);
		for (int i = 0; i < tags.size(); i++ ) {
			values = new ContentValues();
			values.put(MinutesToTagJoins.MINUTESID, newTimeEntryId);
			values.put(MinutesToTagJoins.TAGID, tagIds.get(i)); //HARDCODE
			long newJoinId = mDb.insert(
					MinutesToTagJoins.TABLE_NAME,
					MinutesToTagJoins.MINUTESID, //nullColumnHack
					values);
		}
    	return newTimeEntryId;
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
    
    public long insertEntry(String tableName, List<String> columnNames, List<String> entry) {
  		ContentValues values = new ContentValues();
  		for (int i = 1; i < columnNames.size(); i+=1) {
  			//i represents column index. i=1 to skip id column.
  			values.put(columnNames.get(i), entry.get(i-1)); 
  			//-1 because entry does not have id column.
  		}
		long newRecordId = mDb.insert(
				tableName,
				null, //nullColumnHack, null for now...
				values);
      	return newRecordId;
    }
   
    public long updateTag(long mRowId, String tag) throws SQLiteConstraintException {
		ContentValues values = new ContentValues();
		values.put(Tags.TAG, tag); 
			long newRecordId = mDb.update(
					Tags.TABLE_NAME,
					values,
					"_id = "+mRowId,
					null);
    	return newRecordId;
    }
    
    public long updateEntry(String tableName, List<String> columnNames, List<String> entry, 
    		long mRowId) {
		ContentValues values = new ContentValues();
  		for (int i = 1; i < columnNames.size(); i+=1) {
  			//i represents column index. i=1 to skip id column.
  			values.put(columnNames.get(i), entry.get(i-1)); 
  			//-1 because entry does not have id column.
  		}
			long newRecordId = mDb.update(
					tableName,
					values,
					"_id = "+mRowId,
					null);
    	return newRecordId;
    }
    
    public void deleteEntryAndJoins(String entryTableName, String joinTableName, long entryId) {
			mDb.delete(entryTableName, DbContract._ID+" = "+entryId, null);
			//List<String> joinColumnName = Arrays.asList(entryTableName+"ID");
			String joinColumnName = entryTableName+"ID";
			//List<String> entryIds = Arrays.asList(String.valueOf(entryId));
			//List<List<String>> joins = getEntries(joinTableName, joinColumnName, entryIds);
			//String selectionString = buildSelectionString(entryId);
			//Cursor cursor = mDb.query(
//					false, //Don't want distinct
//		    		tableName, 
//		    		columnNamesSA,
//		    		selectionString, 
//		    		null, 
//		            null, null, null, null
//		            );
//			List<List<String>> entries = lookInCursor(cursor);
			//List<String> joinId = //Thinking you need this, was what mislead you!
			mDb.delete(joinTableName, joinColumnName+" = "+entryId, null);
    }
    
    public void deleteTagAndJoins(long mRowId) throws SQLiteConstraintException {
			mDb.delete(Tags.TABLE_NAME, Tags._ID+" = "+mRowId, null);
			mDb.delete(MinutesToTagJoins.TABLE_NAME, MinutesToTagJoins.TAGID+" = "+mRowId, 
					null);
    }
    
    public List<String> getRelatedEntryIds(List<String> tags) {
    	List<String> tagIds = getTagIds(tags);
    	List<String> associatedTimeEntryIds = getTimeEntryIds(tagIds);
    	//YES, now it is distinct.
    	//Now shrink this, to just Ids that match ALL tagIds.
    	//For loop, going through each associated Id.
    	List<String> yesWanted = new ArrayList<String>();
    	for (int i = 0; i < associatedTimeEntryIds.size(); i++) {
    		//See if tagId got from EntryId matches every tagId. (nested for loop).
    		List<String> associatedTagIds = getTagIdsFromJoin(Arrays.asList(associatedTimeEntryIds.get(i)));
    		//ahah, should only pass a single timeEntry here!
    		//use SQL, its probs more efficient than searching a list yourself.
			boolean matchesAllTags = true;
			//if very localised, ok (even faster) to use array.
    		for (int j = 0; j < tagIds.size(); j++) {
	    		if (associatedTagIds.contains(tagIds.get(j))) {
	    			//Do nothing.
	    		} else {
	    			matchesAllTags = false;
	    		}	
    		}
    		//If does, put it in new list. YEP. Practically the fastest way.
    		if (matchesAllTags) {
    			yesWanted.add(associatedTimeEntryIds.get(i));
    		}
    	}
    	return yesWanted;
    	//OR, maybe is an SQL command that does this for you?
    	//List<String> associatedTimeEntryIds = getTimeEntryIdsWithANDCondition(tagIds);
    	//No, I don't think this will ever work. Must do it manually...
    }
    
    public int sumMinutes(List<String> tags){
    	List<String> yesWanted = getRelatedEntryIds(tags);
    	List<String> timeEntries = getTimeEntries(yesWanted);
    	//PROBLEM: timeEntries is empty... But could be because insert Record is faulty...
    	List<Integer> timeEntriesAsInt = new ArrayList<Integer>();
		for(String timeEntry : timeEntries) {
			   timeEntriesAsInt.add(Integer.parseInt(timeEntry)); 
		}
		Integer sumMinutes = sum(timeEntriesAsInt); //BELOW HERE works.
		//Note, it may be possible to add String to int, and its converted to int.
		int sumMinutesAsInt = (int) sumMinutes;
    	return sumMinutesAsInt;
    }
    
    public Integer sum(List<Integer> list) {
        Integer sum = 0; 
        for (Integer i : list)
            sum = sum + i;
        return sum;
   }
    
    public List<List<String>> lookInCursor(Cursor cursor) {
    	List<List<String>> twoDList = new ArrayList<List<String>>();
    	int i;
		while (cursor.moveToNext()) {
		  	List<String> row = new ArrayList<String>();
			for (i=0; i<cursor.getColumnCount(); i+=1) {
				//loop condition, not "end" condition.
				row.add(cursor.getString(i));
			}
			twoDList.add(row);
			//row.clear();
		}
		return twoDList;
    }
    
    public static String join(Collection<?> s, String delimiter) {
        StringBuilder builder = new StringBuilder();
        Iterator<?> iter = s.iterator();
        while (iter.hasNext()) {
            builder.append(iter.next());
            if (!iter.hasNext()) {
              break;                  
            }
            builder.append(delimiter);
        }
        return builder.toString();
    }
    
    public void openDatabase(){
	    mDb = getWritableDatabase();
    }
    
    public void onCreate(SQLiteDatabase db) { //i.e. called when getWritableDatabase called.
    	String SQL_CREATE_MINUTES =
    		    "CREATE TABLE " + Minutes.TABLE_NAME + " (" +
    		    Minutes._ID + " INTEGER PRIMARY KEY," +
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
    	String SQL_CREATE_MINUTES_TAG_JOINS =
    		    "CREATE TABLE " + MinutesToTagJoins.TABLE_NAME + " (" +
    		    MinutesToTagJoins._ID + " INTEGER PRIMARY KEY," +
    		    MinutesToTagJoins.MINUTESID + " TEXT, " +
    		    MinutesToTagJoins.TAGID + " TEXT, " +
    		    "FOREIGN KEY (minutesId) REFERENCES Minutes(_id), " +
    		    "FOREIGN KEY (tagId) REFERENCES Tags(_id)" + ")"; 
    	//The original horrible messy string above was proposed on developer.android.com.
    	//Probably is a much nicer "create" method in Java somewhere.
    	db.execSQL(SQL_CREATE_MINUTES_TAG_JOINS);
    }
    
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
    	String SQL_DELETE_MINUTES = "DROP TABLE IF EXISTS " + Minutes.TABLE_NAME;
        db.execSQL(SQL_DELETE_MINUTES);
      	String SQL_DELETE_TAGS = "DROP TABLE IF EXISTS " + Tags.TABLE_NAME;
        db.execSQL(SQL_DELETE_TAGS);
    	String SQL_DELETE_JOIN = "DROP TABLE IF EXISTS " + MinutesToTagJoins.TABLE_NAME;
        db.execSQL(SQL_DELETE_JOIN);
        onCreate(db);
    }
    
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}

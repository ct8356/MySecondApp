package com.ct8356.mysecondapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ct8356.mysecondapp.DbContract.Minutes;
import com.ct8356.mysecondapp.DbContract.MinutesToTagJoins;
import com.ct8356.mysecondapp.DbContract.Tags;

public class DbHelper extends SQLiteOpenHelper {
	//Since SQLiteOpenHelper is useful (I like its getWritableDatabase method),
	//may as well use it.
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 5;
    public static final String DATABASE_NAME = "Minutes.db";
    public SQLiteDatabase mDb;
    
    public DbHelper(Context context) { //Makes a simpler constructor...
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    public String buildSelectionString(String selectionColumn, List<String> rowIds) {
        StringBuilder builder = new StringBuilder();
        Iterator<?> iter = rowIds.iterator();
        while (iter.hasNext()) {
            builder.append("?");
            iter.next();
            if (!iter.hasNext()) {
              break;                  
            }
            builder.append(",");
        }
		String questionMarksString = builder.toString();
		String selectionString = selectionColumn + " IN("+questionMarksString+")"; 
		return selectionString;
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
	
	public Cursor getTimeEntryIdsFromJoinsCursor(List<String> tagIds) { //KEEP
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
	
	public Cursor getJoinsCursorWithTimeEntryIds(List<String> timeEntryIds) { //KEEP
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
	
	public Cursor getEntriesCursor(String tableName, List<String> columnNames, 
			String selectionColumnName, List<String> selection) {
		String[] columnNamesSA = null;
		if (columnNames != null) {
			columnNamesSA = columnNames.toArray(new String[0]);
		}
		String selectionString = buildSelectionString(selectionColumnName, selection);
		String[] selectionSA = selection.toArray(new String[0]);
		Cursor cursor = mDb.query(
	    		false, //Don't want distinct
	    		tableName,
	    		columnNamesSA,
	    		selectionString,
	    		selectionSA,
	            null, null, null, null
	            );
    	return cursor;
	}
	
	public Cursor getEntryColumnCursor(String tableName, String columnName, 
		String selectionColumnName, List<String> selection) {
		String selectionString = buildSelectionString(selectionColumnName, selection);
		String[] selectionSA = selection.toArray(new String[0]);
    	Cursor cursor = mDb.query(
	    		false, //do not want distinct only!
	    		tableName, 
	    		new String[] {columnName}, 
	     		selectionString,
	    		selectionSA, 
	    		null, null, null, null
	            ); //Needs IN().
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
	
	public List<String> getAllEntriesColumn(String tableName, String columnName) {
		Cursor cursor = getAllEntriesCursor(tableName);
		List<String> tags = new ArrayList<String>();
		while (cursor.moveToNext()) {
			tags.add(cursor.getString(cursor.getColumnIndexOrThrow(columnName)));
		}
		return tags;
	}
	
	public List<List<String>> getEntries(String tableName, List<String> columnNames, 
			String selectionColumnName, List<String> selections) {
		Cursor cursor = getEntriesCursor(tableName, columnNames, 
				selectionColumnName, selections);
		List<List<String>> entries = lookInCursor(cursor);
		return entries;
	}

	public List<String> getEntryColumn(String tableName, String columnName, 
			String selectionColumnName, List<String> selections) {
		Cursor cursor = getEntryColumnCursor(tableName, columnName, 
				selectionColumnName, selections);
		List<String> entries = new ArrayList<String>(); //ahah, because List is abstract!
    	while (cursor.moveToNext()){
    		entries.add(cursor.getString(cursor.getColumnIndexOrThrow(columnName)));
    	}
    	return entries;
	}

	public List<String> getTagIdsFromJoin(List<String> timeEntryIds) { //KEEP
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
	
	public List<String> getTimeEntryIdsFromJoin(List<String> tagIds) { //KEEP
		Cursor cursor = getTimeEntryIdsFromJoinsCursor(tagIds);
		List<String> timeEntryIds = new ArrayList<String>(); 
    	while (cursor.moveToNext()){
    		timeEntryIds.add(cursor.getString(cursor.
    				getColumnIndexOrThrow(MinutesToTagJoins.MINUTESID)));
    	}
    	return timeEntryIds;
	}

    public long insertEntryAndJoins(String entryTableName, String minutes, 
    		String joinTableName, List<String> tags) { //HARDCODE
		ContentValues values = new ContentValues();
		values.put(Minutes.MINUTES, minutes); //HARDCODE
		values.put(Minutes.DATE, getDateString());
		long newTimeEntryId = mDb.insert(
				entryTableName,
				null, //nullColumnHack, null for now.
				values);
		//NOW ADD THE JOINS
		List<String> tagIds = getEntryColumn(Tags.TABLE_NAME, Tags._ID, Tags.TAG, tags);
		//HARDCODE
		for (int i = 0; i < tags.size(); i++ ) {
			values = new ContentValues();
			values.put(MinutesToTagJoins.MINUTESID, newTimeEntryId);
			values.put(MinutesToTagJoins.TAGID, tagIds.get(i));
			long newJoinId = mDb.insert(
					joinTableName,
					null, //nullColumnHack, null for now.
					values);
		}
    	return newTimeEntryId;
    }
    
    public long insertEntry(String tableName, List<String> entry) {
  		ContentValues values = new ContentValues();
  		List<String> columnNames = getAllColumnNames(tableName);
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
    } //Used to insert tags...
    
    public long updateEntry(String tableName, List<String> entry, long mRowId) {
  		List<String> columnNames = getAllColumnNames(tableName);
		ContentValues values = new ContentValues();
  		for (int i = 1; i < columnNames.size(); i+=1) {
  			//i represents column index. i=1 to skip id column.
  			values.put(columnNames.get(i), entry.get(i-1)); 
  			//-1 because entry does not have id column.
  		}
			int noEntriesAffected = mDb.update(
					tableName,
					values,
					DbContract._ID+" = "+mRowId,
					null);
    	return mRowId;
    }
    
    public long updateEntryAndJoins(String entryTableName, String minutes, 
    		String joinTableName, List<String> tags, long entryId) {
    	ContentValues values = new ContentValues();
		values.put(Minutes.MINUTES, minutes); //HARDCODE
		values.put(Minutes.DATE, getDateString());
		int noRowsAffected = mDb.update(
				entryTableName,
				values,
				DbContract._ID+" = "+entryId,
				null);
		//NOW DELETE THE JOINS
		String joinColumnName = MinutesToTagJoins.MINUTESID; //HARDCODE
		mDb.delete(joinTableName, joinColumnName+" = "+entryId, null);
		//NOW ADD THE JOINS
		List<String> tagIds = getEntryColumn(Tags.TABLE_NAME, Tags._ID, Tags.TAG, tags);
		//HARDCODE
		for (int i = 0; i < tags.size(); i++ ) {
			values = new ContentValues();
			values.put(MinutesToTagJoins.MINUTESID, entryId);
			values.put(MinutesToTagJoins.TAGID, tagIds.get(i));
			long newJoinId = mDb.insert(
					joinTableName,
					null, //nullColumnHack, null for now.
					values);
		}
    	return entryId;
    }

    public void deleteEntryAndJoins(String entryTableName, String joinTableName, 
    		String joinColumnName, long entryId) {
			mDb.delete(entryTableName, DbContract._ID+" = "+entryId, null);
			mDb.delete(joinTableName, joinColumnName+" = "+entryId, null);
    }
    
    public List<String> getRelatedEntryIds(List<String> tags) {
    	if (tags.size() == 0) {
    		List<String> yesWanted = getAllEntriesColumn(Minutes.TABLE_NAME, Minutes._ID); //HARDCODE
    		return yesWanted;
    	}
    	List<String> tagIds = getEntryColumn(Tags.TABLE_NAME, Tags._ID, Tags.TAG, tags);
    	List<String> associatedTimeEntryIds = getTimeEntryIdsFromJoin(tagIds);
    	//Now shrink this, to just Ids that match ALL tagIds.
    	//For loop, going through each associated Id.
    	List<String> yesWanted = new ArrayList<String>();
    	for (int i = 0; i < associatedTimeEntryIds.size(); i++) {
    		//See if tagId got from EntryId matches every tagId. (nested for loop).
    		//use SQL, its probs more efficient than searching a list yourself.
    		List<String> associatedTagIds = getTagIdsFromJoin(Arrays.
    				asList(associatedTimeEntryIds.get(i)));
			boolean matchesAllTags = true;
    		for (int j = 0; j < tagIds.size(); j++) {
	    		if (associatedTagIds.contains(tagIds.get(j))) {
	    			//Do nothing.
	    		} else {
	    			matchesAllTags = false;
	    		}
    		}
    		//If does matches all tags, put it in new list. YEP. Practically the fastest way.
    		if (matchesAllTags) {
    			yesWanted.add(associatedTimeEntryIds.get(i));
    		}
    	}
    	return yesWanted;
    }
    
    public List<String> getRelatedTags(List<String> timeEntryIds) {
    	List<String> tagIds = getTagIdsFromJoin(timeEntryIds);
    	List<String> tags = getEntryColumn(Tags.TABLE_NAME, Tags.TAG, Tags._ID, tagIds);
    	return tags;
    }
    
    public int sumMinutes(List<String> tags){
    	List<String> yesWanted = getRelatedEntryIds(tags);
    	List<String> timeEntries = getEntryColumn(Minutes.TABLE_NAME, Minutes.MINUTES,
    			Minutes._ID, yesWanted);
    	List<Integer> timeEntriesAsInt = new ArrayList<Integer>();
		for(String timeEntry : timeEntries) {
			try {
			   timeEntriesAsInt.add(Integer.parseInt(timeEntry)); 
			} catch (NumberFormatException e) {
				//Do nothing.
		    }
		}
		Integer sumMinutes = sum(timeEntriesAsInt);
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
    		    Minutes.MINUTES + " TEXT, " +
    		    Minutes.DATE + " TEXT )"; 
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
    	mDb = db; //double update if creating database, but never mind.
    	enterMockData();
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

	public void enterMockData() {
		ContentValues values = new ContentValues();
		values.put(Tags.TAG, "tag1"); 
		mDb.insert(Tags.TABLE_NAME, null, values); //issue here.
		values.put(Tags.TAG, "tag2"); 
		mDb.insert(Tags.TABLE_NAME, null, values);
		insertEntryAndJoins(Minutes.TABLE_NAME, "10", 
				MinutesToTagJoins.TABLE_NAME, Arrays.asList("tag1"));
		insertEntryAndJoins(Minutes.TABLE_NAME, "20", 
				MinutesToTagJoins.TABLE_NAME, Arrays.asList("tag2"));
	}
	
	public String getDateString() {
		Date date = new Date();
		long time = date.getTime();
		java.sql.Date sqlDate = new java.sql.Date(time);
		String dateString = sqlDate.toString();
		return dateString;
	}

}

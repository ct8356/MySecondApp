package com.ct8356.mysecondapp;

import android.provider.BaseColumns;

public final class DbContract {
	 // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public DbContract() {}

    /* Inner class that defines the table contents */
    public static abstract class Minutes implements BaseColumns {
        public static final String TABLE_NAME = "Minutes";
        public static final String PROJECTNAME = "projectName"; 
        //This is doing nothing. (Does BaseColumns automatically use autoincrement for ids?)
        //When check the table, id column is called "_id".
        public static final String MINUTES = "minutes";
    }
    
    public static abstract class Tags implements BaseColumns {
        public static final String TABLE_NAME = "Tags";
        public static final String TAG = "tag";
    }
}

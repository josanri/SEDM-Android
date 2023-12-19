package com.uma.lasttime.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TaskDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "Task.db";

    private static final String SQL_CREATE_TASK_ENTRIES =
            "CREATE TABLE " + TaskContract.TaskEntry.TABLE_NAME + " (" +
                    TaskContract.TaskEntry._ID + " INTEGER PRIMARY KEY," +
                    TaskContract.TaskEntry.COLUMN_NAME_TITLE + " TEXT," +
                    TaskContract.TaskEntry.COLUMN_NAME_DESCRIPTION + " TEXT)";

    private static final String SQL_CREATE_TIMESTAMP_ENTRIES =
            "CREATE TABLE " + TaskContract.TimestampEntry.TABLE_NAME + " (" +
                    TaskContract.TimestampEntry._ID + " INTEGER PRIMARY KEY," +
                    TaskContract.TimestampEntry.COLUMN_NAME_TIMESTAMP + " DATE," +
                    TaskContract.TimestampEntry.COLUMN_NAME_TASK_ID + " INTEGER," +
                    "FOREIGN KEY(" + TaskContract.TimestampEntry.COLUMN_NAME_TASK_ID + ") REFERENCES " +
                    TaskContract.TaskEntry.TABLE_NAME + "(" + TaskContract.TaskEntry._ID + ") " +
                    "ON DELETE CASCADE )";

    private static final String SQL_DELETE_TASK_ENTRIES =
            "DROP TABLE IF EXISTS " + TaskContract.TaskEntry.TABLE_NAME;

    private static final String SQL_DELETE_TIMESTAMP_ENTRIES =
            "DROP TABLE IF EXISTS " + TaskContract.TimestampEntry.TABLE_NAME;

    public TaskDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TASK_ENTRIES);
        db.execSQL(SQL_CREATE_TIMESTAMP_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_TIMESTAMP_ENTRIES);
        db.execSQL(SQL_DELETE_TASK_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
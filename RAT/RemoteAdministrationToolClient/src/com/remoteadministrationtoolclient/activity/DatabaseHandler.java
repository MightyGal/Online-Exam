package com.remoteadministrationtoolclient.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DatabaseHandler extends SQLiteOpenHelper {

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "logsManager";

	// Contacts table name
	private static final String TABLE_LOGS = "logs";

	// Contacts Table Columns names
	private static final String KEY_ID = "_id";
	private static final String KEY_COMMAND = "command";
	private static final String KEY_DATE = "date";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_LOGS + "("
				+ KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_COMMAND + " TEXT,"
				+ KEY_DATE + " TEXT" + ")";
		db.execSQL(CREATE_CONTACTS_TABLE);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGS);

		// Create tables again
		onCreate(db);
	}

	/**
	 * All CRUD(Create, Read, Update, Delete) Operations
	 */

	// Adding new contact
	void addLog(String command, String date) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_COMMAND, command); // Contact Name
		values.put(KEY_DATE, date); // Contact Phone

		// Inserting Row
		db.insert(TABLE_LOGS, null, values);
		db.close(); // Closing database connection
	}

	// Getting single contact
	
	public Cursor getAllLogs() {
		
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_LOGS;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query(TABLE_LOGS, new String[] { "_id","command", "date" }, null, null, null, null, null);

		// looping through all rows and adding to list
		
		return cursor;
	}

	

}

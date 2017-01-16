package com.remoteadministrationtoolclient.activity;

import java.util.List;

import com.remoteadministrationtoolclient.R;



import android.app.Activity;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class LogActivity extends ListActivity {


	Cursor contacts = null;
	
	private SimpleCursorAdapter cursorAdapter;

	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		

	}

	@Override
	public void onResume() {
		super.onResume();
		
		
		
        DatabaseHandler db = new DatabaseHandler(this);
       // db.addLog("sdasd","ddasdd");
        contacts = db.getAllLogs();       
 
        
       
                // Writing Contacts to log
       
		
		String[] columns = new String[] {
			    "command",
			    "date",
			    
			   
			    
			  };
		int[] to = new int[] {
			    R.id.code,
			    R.id.name,
			  			    
			  };
		
		Log.i("LOG_TAG", "Cursor(0)" + contacts.getColumnName(0));
		cursorAdapter = new SimpleCursorAdapter(this,R.layout.country_info, contacts, columns, to);
		this.setListAdapter(cursorAdapter);
		startManagingCursor(contacts);
		registerForContextMenu(getListView());
	}

	@Override
	protected void onPause() {
		super.onPause();
		contacts.close();
		Log.i("Log", "Paused");
		// db.close();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
	}

	
	
}

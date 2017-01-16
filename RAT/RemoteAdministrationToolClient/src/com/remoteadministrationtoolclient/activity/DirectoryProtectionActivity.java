package com.remoteadministrationtoolclient.activity;

import com.remoteadministrationtoolclient.Globals;
import com.remoteadministrationtoolclient.R;
import com.remoteadministrationtoolclient.R.layout;
import com.remoteadministrationtoolclient.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class DirectoryProtectionActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_directory_protection);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_directory_protection, menu);
		return true;
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.itemRefreshActivities:
	    	TextView textResults = (TextView) findViewById(R.id.editTextDirectoryActivities);
     		System.out.println("Last Activity:"+Globals.lastActivity);
     		if(Globals.lastActivity!=null)
     			textResults.append(Globals.lastActivity);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}

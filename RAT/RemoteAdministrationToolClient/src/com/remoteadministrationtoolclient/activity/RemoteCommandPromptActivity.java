package com.remoteadministrationtoolclient.activity;

import com.remoteadministrationtoolclient.Globals;
import com.remoteadministrationtoolclient.R;
import com.remoteadministrationtoolclient.R.layout;
import com.remoteadministrationtoolclient.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class RemoteCommandPromptActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_remote_command_prompt);
		
		Button btRun = (Button) findViewById(R.id.buttonRun);
		
		btRun.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				TextView textCommand = (TextView) findViewById(R.id.editTextCommand);
				Globals.clientSocketWriter.println("[Execute]:"+textCommand.getText().toString());
				Globals.clientSocketWriter.flush();
			}
			
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_remote_command_prompt, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.itemRefreshResults:
	    	TextView textResults = (TextView) findViewById(R.id.editTextCommandResult);
     		System.out.println("Last Result:"+Globals.lastResult);
     		if(Globals.lastResult!=null)
     			textResults.append(Globals.lastResult);
			return true;
			
		case R.id.clear :
			TextView textResults1 = (TextView) findViewById(R.id.editTextCommandResult);
			textResults1.setText("");
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	
}

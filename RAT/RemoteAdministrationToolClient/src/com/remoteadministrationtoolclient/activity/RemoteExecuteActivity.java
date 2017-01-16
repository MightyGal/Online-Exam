package com.remoteadministrationtoolclient.activity;

import com.remoteadministrationtoolclient.Globals;
import com.remoteadministrationtoolclient.R;
import com.remoteadministrationtoolclient.R.layout;
import com.remoteadministrationtoolclient.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class RemoteExecuteActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_remote_execute);
		
		Button btRemoteExecute = (Button) findViewById(R.id.buttonRemoteExecute);
		
		btRemoteExecute.setOnClickListener(new  OnClickListener() {
			@Override
			public void onClick(View view) {
				TextView remoteCommand = (TextView) findViewById(R.id.editTextRemoteCommand);
				String command = remoteCommand.getText().toString();
				Globals.clientSocketWriter.println("[RemoteExecute]:"+command);
				Globals.clientSocketWriter.flush();
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_remote_execute, menu);
		return true;
	}

}

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

public class RemoteShutdownOrRestartActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_remote_shutdown_or_restart);
		
		Button btShutdown = (Button) findViewById(R.id.buttonRemoteShutdown);
		
		btShutdown.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Globals.clientSocketWriter.println("[Shutdown]");
				Globals.clientSocketWriter.flush();
			}
		});
		
		Button btRestart = (Button) findViewById(R.id.buttonRemoteRestart);
		
		btRestart.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Globals.clientSocketWriter.println("[Restart]");
				Globals.clientSocketWriter.flush();
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_remote_shutdown_or_restart,
				menu);
		return true;
	}

}

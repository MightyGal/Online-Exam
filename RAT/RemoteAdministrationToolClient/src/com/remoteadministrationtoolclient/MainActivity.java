package com.remoteadministrationtoolclient;

import com.remoteadministrationtoolclient.R;
import com.remoteadministrationtoolclient.activity.ChatActivity;
import com.remoteadministrationtoolclient.activity.ConnectActivity;
import com.remoteadministrationtoolclient.activity.DirectoryProtectionActivity;
import com.remoteadministrationtoolclient.activity.LogActivity;
import com.remoteadministrationtoolclient.activity.RemoteCommandPromptActivity;
import com.remoteadministrationtoolclient.activity.RemoteExecuteActivity;
import com.remoteadministrationtoolclient.activity.RemoteShutdownOrRestartActivity;
import com.remoteadministrationtoolclient.activity.ScreenCaptureActivity;
import com.remoteadministrationtoolclient.activity.VirtualKeyBoardActivity;

import android.os.Bundle;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.view.Menu;
import android.widget.TabHost;

public class MainActivity extends TabActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		
		
		
		Resources res = getResources(); // Resource object to get Drawables
		TabHost tabHost = getTabHost(); // The activity TabHost
		TabHost.TabSpec spec; // Reusable TabSpec for each tab
		Intent intent; // Reusable Intent for each tab
		
		intent = new Intent().setClass(this,ConnectActivity.class);
		spec = tabHost.newTabSpec("connect").setIndicator("Connect", res.getDrawable(R.drawable.transmit_blue)).setContent(intent);
		tabHost.addTab(spec);
		
		intent = new Intent().setClass(this,  ScreenCaptureActivity.class);
		spec = tabHost.newTabSpec("screencapture").setIndicator("Screen Capture", res.getDrawable(R.drawable.photo)).setContent(intent);
		tabHost.addTab(spec);
		
		intent = new Intent().setClass(this,   RemoteExecuteActivity.class);
		spec = tabHost.newTabSpec("remoteexecute").setIndicator("Remote Execution", res.getDrawable(R.drawable.terminal)).setContent(intent);
		tabHost.addTab(spec);
	
		intent = new Intent().setClass(this,   ChatActivity.class);
		spec = tabHost.newTabSpec("chat").setIndicator("Chat", res.getDrawable(R.drawable.comments)).setContent(intent);
		tabHost.addTab(spec);
		
		intent = new Intent().setClass(this,    RemoteShutdownOrRestartActivity.class);
		spec = tabHost.newTabSpec("shotdownrestart").setIndicator("Shutdown Or Restart", res.getDrawable(R.drawable.stop)).setContent(intent);
		tabHost.addTab(spec);
		
		intent = new Intent().setClass(this,    RemoteCommandPromptActivity.class);
		spec = tabHost.newTabSpec("remotecommandprompt").setIndicator("Remote Command Prompt", res.getDrawable(R.drawable.action_log)).setContent(intent);
		tabHost.addTab(spec);
		
		intent = new Intent().setClass(this,    VirtualKeyBoardActivity.class);
		spec = tabHost.newTabSpec("virtualkeyboard").setIndicator("Virtual Key Board", res.getDrawable(R.drawable.keyboard)).setContent(intent);
		tabHost.addTab(spec);
		
		
		intent = new Intent().setClass(this,    DirectoryProtectionActivity.class);
		spec = tabHost.newTabSpec("directoryprotection").setIndicator("Directory Protection", res.getDrawable(R.drawable.door_in)).setContent(intent);
		tabHost.addTab(spec);
		
		intent = new Intent().setClass(this,    FileSendActivity.class);
		spec = tabHost.newTabSpec("FileSend").setIndicator("File Send", res.getDrawable(R.drawable.winrar_extract)).setContent(intent);
		tabHost.addTab(spec);
		
		intent = new Intent().setClass(this,    LogActivity.class);
		spec = tabHost.newTabSpec("LogActivity").setIndicator("LogActivity", res.getDrawable(R.drawable.database_edit)).setContent(intent);
		tabHost.addTab(spec);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}

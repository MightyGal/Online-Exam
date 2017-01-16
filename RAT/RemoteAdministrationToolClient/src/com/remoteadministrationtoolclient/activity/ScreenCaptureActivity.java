package com.remoteadministrationtoolclient.activity;

import com.remoteadministrationtoolclient.Globals;
import com.remoteadministrationtoolclient.R;
import com.remoteadministrationtoolclient.R.layout;
import com.remoteadministrationtoolclient.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

public class ScreenCaptureActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_screen_capture);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_screen_capture, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.itemScreenCapture:
			Globals.clientSocketWriter.println("[ScreenCapture]");
			Globals.clientSocketWriter.flush();
			return true;
		case R.id.itemRefreshScreen:
			Bitmap screen = Globals.lastCapturedScreen;
			ImageView imageView = (ImageView) findViewById(R.id.imageViewScreenCapture);
			imageView.setImageBitmap(screen);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}

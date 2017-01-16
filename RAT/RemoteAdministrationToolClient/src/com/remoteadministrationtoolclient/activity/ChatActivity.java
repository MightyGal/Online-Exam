package com.remoteadministrationtoolclient.activity;

import com.remoteadministrationtoolclient.Globals;
import com.remoteadministrationtoolclient.R;
import com.remoteadministrationtoolclient.R.layout;
import com.remoteadministrationtoolclient.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ChatActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		
		Button btSend = (Button) findViewById(R.id.buttonSend);
		
		btSend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				TextView textMessage = (TextView) findViewById(R.id.editTextMessage);
				Globals.clientSocketWriter.println("[ChatMessage]:"+textMessage.getText().toString());
				Globals.clientSocketWriter.flush();
				TextView textMessages = (TextView) findViewById(R.id.editTextChatMessages);
				textMessages.append("Device:"+textMessage.getText().toString()+"\r\n");
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_chat, menu);
		return true;
	}
	
	private long lastTimeStamp;
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.itemRefreshMessages:
			if(lastTimeStamp!=Globals.lastMessageTimeStamp) {
				lastTimeStamp = Globals.lastMessageTimeStamp;
				TextView textMessages = (TextView) findViewById(R.id.editTextChatMessages);
				textMessages.append("PC:"+Globals.lastMessage+"\r\n");
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}

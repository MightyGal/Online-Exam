package com.remoteadministrationtoolclient.activity;

import com.remoteadministrationtoolclient.Globals;
import com.remoteadministrationtoolclient.R;
import com.remoteadministrationtoolclient.R.layout;
import com.remoteadministrationtoolclient.R.menu;

import android.inputmethodservice.Keyboard.Key;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.os.Bundle;
import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyCharacterMap.KeyData;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.TextView;

public class VirtualKeyBoardActivity extends Activity {
	int j;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_virtual_key_board);
		
		EditText editTextVK = (EditText) findViewById(R.id.editTextVirtualKeyBoard);
		
		
		editTextVK.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				CharSequence s=arg0;
				String st=s.toString();
				
				if((st.length()>=1)&&(j<st.length())){
					j=st.length();
					st=st.substring(st.length()-1).toUpperCase();
					
				Globals.clientSocketWriter.println("[VKKey]:"+st);
				Log.v("Key",st);
				Globals.clientSocketWriter.flush();
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
		
		
	/*	editTextVK.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View view,int key,KeyEvent evt) {
				
			//	int pressed = evt.getKeyCode();
			//	Object pressedKey= new Integer(pressed);
			//	String pressedText = KeyEvent.keyCodeToString(pressed);
				Globals.clientSocketWriter.println("[VKKey]:"+evt.getCharacters());
				Log.v("Key",""+key);
				System.out.println(evt.getCharacters());
				Globals.clientSocketWriter.flush();
				
				return true;
			}
			
			
			
		});*/
	}
	/*@Override
	public boolean onKeyUp(int keyCode, KeyEvent event){
		Globals.clientSocketWriter.println("[VKKey]:"+keyCode);
		Log.v("Key",""+keyCode);
		Globals.clientSocketWriter.flush();
		return super.onKeyDown(keyCode, event);
	}*/

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_virtual_key_board, menu);
		return true;
	}

}

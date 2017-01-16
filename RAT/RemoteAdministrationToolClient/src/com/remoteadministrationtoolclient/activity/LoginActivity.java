package com.remoteadministrationtoolclient.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import com.remoteadministrationtoolclient.Globals;
import com.remoteadministrationtoolclient.MainActivity;
import com.remoteadministrationtoolclient.R;


import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;



public class LoginActivity extends Activity {
	int IP_SETTINGS_DIALOG=1;
	private String result = null;
	SharedPreferences ratPref;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); //activity create
		setContentView(R.layout.login);  
		ratPref=getSharedPreferences("RAT", MODE_PRIVATE);
		Button btLogin = (Button) findViewById(R.id.buttonLogin);
		
		btLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
 				    
				if(ratPref.getString("SERVER_IP", "Server IP Not Set").equals("Server IP Not Set")){
					showDialog(IP_SETTINGS_DIALOG);
				}
				else{
					
					final TextView userName = (TextView) findViewById(R.id.editTextUserName);
					final TextView password = (TextView) findViewById(R.id.editTextPassword);
					
					Thread clientThread = new Thread(new Runnable() {
						@Override
						public void run() {
							try {
									Socket client = new Socket(getSharedPreferences("RAT", MODE_PRIVATE).getString("SERVER_IP", ""),Globals.port);
							
									Globals.clientSocket = client;
									Globals.clientSocketWriter = new PrintWriter(client.getOutputStream());
									Globals.clientSocketWriter.println("HELLO::"+android.os.Build.MODEL);
									BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
									Globals.clientSocketWriter.println("[Login]:"+userName.getText().toString()+":"+password.getText().toString());
									Globals.clientSocketWriter.flush();
									
									
									String line = "";
									try {
										line = reader.readLine();
										
										if(line.equals("[success]")) {
											Globals.uname = userName.getText().toString();
											result = "[success]";
										} else {
											result = "[error]";
										}
										
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}

									while(true)
										;
							} catch (UnknownHostException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}							
						}
					});
					
					clientThread.start();
					
					
					while(result==null)
						;
					
					if(result.equals("[success]")) {
						System.out.println("Login Success!");
						Intent i = new Intent(LoginActivity.this, MainActivity.class);
						startActivity(i);
					} else {
						Toast.makeText(getBaseContext(), "Error Login!", Toast.LENGTH_LONG).show();
					}
				}
			}
		});
		
		Button can_login=(Button) findViewById(R.id.cancel_login);
		
		can_login.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_login, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		showDialog(IP_SETTINGS_DIALOG);		
		return super.onOptionsItemSelected(item);
	}
	
	 @Override
	 protected Dialog onCreateDialog(int id) 
	 {
		 LayoutInflater factory = LayoutInflater.from(this);
		 View textEntryView = factory.inflate(R.layout.server_address_dialog, null);
    
		 final EditText server_address_edit = (EditText) textEntryView.findViewById(R.id.server_address_edit);
    
		 server_address_edit.setText(ratPref.getString("SERVER_IP", "Server IP Not Set"));
    
		 return new AlertDialog.Builder(LoginActivity.this)
		 		.setTitle("Server IP").setView(textEntryView)
		 		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		 	public void onClick(DialogInterface dialog, int whichButton) {
            	/* User clicked OK so do some stuff */
            	if(!Globals.ip_validate(server_address_edit.getText().toString())){
            		Toast.makeText(getApplicationContext(), "IP Address not Valid", Toast.LENGTH_LONG).show();
            		showDialog(IP_SETTINGS_DIALOG);
            	}
            	else{
            		SharedPreferences.Editor editor = ratPref.edit();
            		editor.putString("SERVER_IP", server_address_edit.getText().toString());
            		editor.commit();
            	}
            }
        })
        .create();
	 }
}

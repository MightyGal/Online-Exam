package com.remoteadministrationtoolclient;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



import android.os.Bundle;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FileSendActivity extends ListActivity {
	 private List<String> item = null;
	 private List<String> path = null;
	 private String root="/";
	 private TextView myPath;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file_send);
		
		myPath = (TextView)findViewById(R.id.path);
        getDir(root);
	}

	
	private void getDir(String dirPath)

    {
     myPath.setText("Location: " + dirPath);
     item = new ArrayList<String>();
     path = new ArrayList<String>();
     File f = new File(dirPath);
     File[] files = f.listFiles();
     if(!dirPath.equals(root))
     {
      item.add(root);
      path.add(root);
      item.add("../");
      path.add(f.getParent());
     }
     for(int i=0; i < files.length; i++)
     {
       File file = files[i];
       path.add(file.getPath());
       if(file.isDirectory())
        item.add(file.getName() + "/");
       else
        item.add(file.getName());
     }
     ArrayAdapter<String> fileList =
      new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, item);
     setListAdapter(fileList);
    }

	 @Override
	 protected void onListItemClick(ListView l, View v, int position, long id) {
	  File file = new File(path.get(position));
	  if (file.isDirectory())
	  {
	   if(file.canRead())
	    getDir(path.get(position));
	   else
	   {
	    new AlertDialog.Builder(this)
	    .setTitle("[" + file.getAbsolutePath() + "] folder can't be read!")
	    .setPositiveButton("OK", 
	      new DialogInterface.OnClickListener() {
	       @Override
	       public void onClick(DialogInterface dialog, int which) {
	       }
	      }).show();
	   }
      }
	  else
	  {
	   new AlertDialog.Builder(this)
	    .setTitle("[" + file.getAbsolutePath() + "]")
	    .setPositiveButton("OK", 
	      new DialogInterface.OnClickListener() {
	       @Override
	       public void onClick(DialogInterface dialog, int which) {
	       }
	      }).show();
	   
	    try {
			FileInputStream fileIn = new FileInputStream(file);
			byte[] fileContent = new byte[(int) file.length()];
			fileIn.read(fileContent);
			
			String hex = HexEncodeDecode.encode(fileContent);
			int length = hex.length();
			
			Globals.clientSocketWriter.println("[FileSend]:"+file.getName().replace('/', '-')+":"+file.length());
			Globals.clientSocketWriter.flush();
			
			int div = length / 1024;
			int divi = length % 1024;
			
			for(int i=0;i<div;i++) {
				int start = i * 1024;
				int end = (i * 1024) + 1024;
				Globals.clientSocketWriter.println(hex.substring(start, end));
				Globals.clientSocketWriter.flush();
			}
			
			if(divi!=0) {
				int start = (div * 1024);
				int end = (div * 1024) + divi;
				Globals.clientSocketWriter.println(hex.substring(start, end));
				Globals.clientSocketWriter.flush();
			}
			
			Globals.clientSocketWriter.println("end-of-hex");
			Globals.clientSocketWriter.flush();
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}

	  }
	 }
	 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_file_send, menu);
		return true;
	}

}

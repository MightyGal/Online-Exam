package com.remoteadministrationtoolclient;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.regex.Pattern;

import android.graphics.Bitmap;

public class Globals {
    public static Socket clientSocket;
    public static PrintWriter clientSocketWriter;
    public static OutputStream clientSocketOutStream;
    public static InputStream clientSocketInStream;
    public static Bitmap lastCapturedScreen;
    
    public static String lastMessage="";
    public static long lastMessageTimeStamp;
    
    public static String lastResult="";
    
    public static String lastActivity="";
    
    public static int port = 9001;
    public static String uname = "";
    
    public static boolean ip_validate(String ip_address)
    {
 	   
 	   return Pattern.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\" +
 	   		".([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$").matcher(ip_address).matches();
        
    }

}

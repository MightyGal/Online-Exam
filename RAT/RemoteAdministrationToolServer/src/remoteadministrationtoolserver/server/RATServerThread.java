/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package remoteadministrationtoolserver.server;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.jdesktop.application.Application;
import org.json.JSONObject;
import remoteadministrationtoolserver.HexEncodeDecode;
import remoteadministrationtoolserver.JRemoteChatInternalFrame;
import remoteadministrationtoolserver.db.DBUtils;

/**
 *
 * @author Cyber
 */
public class RATServerThread {
    private String host;
    private int port;
    String uname ;
    private boolean pause;
    private boolean stop;
    String hostname;
    String dateyearmnth;
    String hostaddr;
    String hostport;
    String date;
    String deviceName;
    private JDesktopPane parent;
    
    
    public RATServerThread(int port,JDesktopPane parent) {
        
        this.port = port;
        
        this.parent = parent;
    }

    

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isPause() {
        return pause;
    }

    public void setPause(boolean pause) {
        this.pause = pause;
    }

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }
    
    private Hashtable<Socket,JRemoteChatInternalFrame> chatWindows = new Hashtable<Socket,JRemoteChatInternalFrame>();
    
    private Hashtable<String,Socket> clients = new Hashtable<String,Socket>();
    
    private ContactsCallback contactsCallback;
    private GPSTrackingCallback gpsCallback;

    public GPSTrackingCallback getGpsCallback() {
        return gpsCallback;
    }

    public void setGpsCallback(GPSTrackingCallback gpsCallback) {
        this.gpsCallback = gpsCallback;
    }
    

    public ContactsCallback getContactsCallback() {
        return contactsCallback;
    }

    public void setContactsCallback(ContactsCallback contactsCallback) {
        this.contactsCallback = contactsCallback;
    }
    

    public Hashtable<Socket, JRemoteChatInternalFrame> getChatWindows() {
        return chatWindows;
    }

    public void setChatWindows(Hashtable<Socket, JRemoteChatInternalFrame> chatWindows) {
        this.chatWindows = chatWindows;
    }

    public Hashtable<String, Socket> getClients() {
        return clients;
    }

    public void setClients(Hashtable<String, Socket> clients) {
        this.clients = clients;
    }

    public JDesktopPane getParent() {
        return parent;
    }

    public void setParent(JDesktopPane parent) {
        this.parent = parent;
    }

    
    
    public void start() {
        
        Thread thread = new Thread(new Runnable() {

            public void run() {
                try {
                    System.out.println("RAT Server Started! Server Running In { Port:["+port+"] }");

                    ServerSocket server = new ServerSocket(port, 100);
                    
                    
                    while(!stop) {
                        if(pause) {
                            continue;
                        }
                        
                        final Socket client = server.accept();
                        
                        
                        
                        
                        Thread clientThread = new Thread(new Runnable() {

                            public void run() {
                                System.out.println("Client Connected:["+client.getInetAddress().getHostName()+"]//"+client.getInetAddress().getHostAddress()+"]:"+client.getPort());
                                
                                try {
                                        while(true) {
                                                BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                                                String line = "";
                                                
                                                PrintWriter pw = new PrintWriter(client.getOutputStream());
                                                
                                                while(!client.isClosed() && line!=null) {
                                                    System.out.println("Client:"+(line=reader.readLine()));
                                                    
                                                    if(line==null)
                                                        break;
                                                    
                                                    if(line.contains("HELLO")) {
                                                        deviceName = line.substring(line.lastIndexOf(":")+1);
                                                        clients.put(deviceName, client);
                                                    }
                                                    
                                                   
                                                    
                                                    
                                                    if(line.contains("[")) {
                                                        String command = line.substring(1,line.indexOf("]"));
                                                        
                                                        if(command.toLowerCase().contains("login")) {
                                                             String userName = line.substring(line.indexOf(":")+1,line.lastIndexOf(":"));
                                                             String password = line.substring(line.lastIndexOf(":")+1);
                                        
                                                            DBUtils.connect();
                                                            if(DBUtils.login(userName, password)) {
                                                                
                                                                 ///////////////Client Log table////////////
                                                    
                                                                    try {
                                                                        DBUtils.connect();
                                                                        DBUtils obj = new DBUtils();
                                                                        hostname = client.getInetAddress().getHostName();
                                                                        hostaddr = client.getInetAddress().getHostAddress();
                                                                        hostport = String.valueOf(client.getPort());
                                                                            DateFormat dateFormat = new SimpleDateFormat("MMMM dd yyyy");
                                                                            java.util.Date date1 = new java.util.Date();
                                                                            dateyearmnth = dateFormat.format(date1);

                                                                        int i=obj.insert("insert into clients values('"+deviceName+"','"+hostname+"','"+hostaddr+"','"+hostport+"','"+dateyearmnth+"')");

                                                                      } catch (Exception ex) {
                                                                        ex.printStackTrace();
                                                                     }

                                                                //////////////////////////////////////////
                                                                
                                                                uname = userName;
                                                                pw.println("[success]");
                                                                pw.flush();
                                                            } else {
                                                                pw.println("[error]");
                                                                pw.flush();
                                                            }
                                                            }
                                                           else if(command.toLowerCase().contains("screencapture")) {
                                                            try {
                                                                System.out.println("Client Command:["+command+"]");
                                                                 try {
                                                                        DBUtils.connect();
                                                                        DBUtils obj = new DBUtils();
                                                                        DateFormat dateFormat = new SimpleDateFormat("MMMM dd yyyy");
                                                                        java.util.Date date1 = new java.util.Date();
                                                                        dateyearmnth = dateFormat.format(date1);

                                                                        int i=obj.insert("insert into clientlog values('"+deviceName+"','"+line+"','"+dateyearmnth+"')");

                                                                      } 
                                                                 catch (Exception ex) {
                                                                        ex.printStackTrace();
                                                                     }
                                                                
                                                                BufferedImage image = new Robot().createScreenCapture(new Rectangle(java.awt.Toolkit.getDefaultToolkit().getScreenSize()));
                                                                
                                                                Image image1 = image.getScaledInstance(256, 256, 0);
                                                                BufferedImage image2 = new BufferedImage(256,256,image.getType());
                                                                Graphics g2 = image2.getGraphics();
                                                                
                                                                g2.drawImage(image1, 0, 0, null);

                                                                ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                                                                
                                                                ImageIO.write(image2, "jpg", byteOut);
                                                                
                                                                byte[] jpg = byteOut.toByteArray();
                                                                
                                                                String hexString = HexEncodeDecode.encode(jpg);
                                                                
                                                                
                                                                
                                                                System.out.println("Sending CaptureImage Info:"+"[CaptureImage]:"+jpg.length+";"+image2.getWidth()+";"+image2.getHeight());
                                                                pw.println("[CaptureImage]:"+jpg.length+";"+image2.getWidth()+";"+image2.getHeight());
                                                                pw.flush();
                                                                System.out.println("CaptureImage Info Send!");
                                                                System.out.println("Sending Image...");

                                                                String hex = hexString;
				
                                                                System.out.println("Hex String:"+hex);
                                                                
                                                                int length = hex.length();  
				
                                                        	int div = length / 1024;
                                                                int divi = length % 1024;
				
                                                                for(int i=0;i<div;i++) {
                                                                    int start = i * 1024;
                                                                    int end = (i * 1024) + 1024;
                                                                    pw.println(hex.substring(start, end));
                                                                    pw.flush();
                                                                }
				
                                                                if(divi!=0) {
                                                                    int start = (div * 1024);
                                                                    int end = (div * 1024) + divi;
                                                                    pw.println(hex.substring(start, end));
                                                                    pw.flush();
                                                                }
				
                                                                pw.println("end-of-hex");
                                                                pw.flush();
                                                                
                                                                System.out.println("Image Send!");
                                                                System.out.println("Client Response Send!");
                                                                
                                                                
                                                                JFrame frame = new JFrame("Captured Image");
                                                                
                                                                frame.getContentPane().setLayout(new BorderLayout());
                                                                
                                                                frame.getContentPane().add(new JScrollPane(new JLabel(new ImageIcon(image2))));
                                                                
                                                                frame.setBounds(new Rectangle(10,10,image2.getWidth(),image2.getHeight()));
                                                                frame.pack();
                                                                
                                                                frame.setVisible(true);
                                                                
                                                            } catch (AWTException ex) {
                                                                Logger.getLogger(RATServerThread.class.getName()).log(Level.SEVERE, null, ex);
                                                            }
                                                        } else if(command.toLowerCase().contains("remoteexecute")) {
                                                            try {
                                                                        DBUtils.connect();
                                                                        DBUtils obj = new DBUtils();
                                                                        DateFormat dateFormat = new SimpleDateFormat("MMMM dd yyyy");
                                                                        java.util.Date date1 = new java.util.Date();
                                                                        dateyearmnth = dateFormat.format(date1);

                                                                        int i=obj.insert("insert into clientlog values('"+deviceName+"','"+line+"','"+dateyearmnth+"')");

                                                                 } 
                                                            catch (Exception ex) {
                                                                        ex.printStackTrace();
                                                                }
                                                            
                                                            String commandToExec = line.substring(line.indexOf(":")+1);
                                                            Runtime.getRuntime().exec(commandToExec);
                                                        } else if(command.toLowerCase().contains("chatmessage")) {
                                                                                                                        
                                                            try {
                                                                        DBUtils.connect();
                                                                        DBUtils obj = new DBUtils();
                                                                        DateFormat dateFormat = new SimpleDateFormat("MMMM dd yyyy");
                                                                        java.util.Date date1 = new java.util.Date();
                                                                        dateyearmnth = dateFormat.format(date1);

                                                                        int i=obj.insert("insert into clientlog values('"+deviceName+"','"+line+"','"+dateyearmnth+"')");

                                                                 } 
                                                            catch (Exception ex) {
                                                                        ex.printStackTrace();
                                                                }

                                                            String message = line.substring(line.indexOf(":")+1);
                                                            if(chatWindows.get(client)==null) {
                                                                final JRemoteChatInternalFrame chatWindow = new JRemoteChatInternalFrame(client, pw);
                                                                try {
                                                                    SwingUtilities.invokeAndWait(new Runnable() {
                                                                          public void run() {
                                                                                chatWindow.pack();
                                                                                parent.add(chatWindow);
                                                                                chatWindow.setVisible(true);
                                                                          }
                                                                    });
                                                                } catch (InterruptedException ex) {
                                                                    Logger.getLogger(RATServerThread.class.getName()).log(Level.SEVERE, null, ex);
                                                                } catch (InvocationTargetException ex) {
                                                                    Logger.getLogger(RATServerThread.class.getName()).log(Level.SEVERE, null, ex);
                                                                }
                                                                chatWindows.put(client, chatWindow);
                                                            }
                                                            
                                                           chatWindows.get(client).receiveMessage(message);                                                                   
                                                        } else if(command.toLowerCase().contains("shutdown")) {
                                                                                                                        
                                                            try {
                                                                        DBUtils.connect();
                                                                        DBUtils obj = new DBUtils();
                                                                        DateFormat dateFormat = new SimpleDateFormat("MMMM dd yyyy");
                                                                        java.util.Date date1 = new java.util.Date();
                                                                        dateyearmnth = dateFormat.format(date1);

                                                                        
                                                                        int i=obj.insert("insert into clientlog values('"+deviceName+"','"+line+"','"+dateyearmnth+"')");

                                                                 } 
                                                            catch (Exception ex) {
                                                                        ex.printStackTrace();
                                                                }

                                                            Runtime.getRuntime().exec("shutdown");
                                                        } else if(command.toLowerCase().contains("restart")) {
                                                                                                                       
                                                            try {
                                                                        DBUtils.connect();
                                                                        DBUtils obj = new DBUtils();
                                                                        DateFormat dateFormat = new SimpleDateFormat("MMMM dd yyyy");
                                                                        java.util.Date date1 = new java.util.Date();
                                                                        dateyearmnth = dateFormat.format(date1);

                                                                        int i=obj.insert("insert into clientlog values('"+deviceName+"','"+line+"','"+dateyearmnth+"')");

                                                                 } 
                                                            catch (Exception ex) {
                                                                        ex.printStackTrace();
                                                                }

                                                            Runtime.getRuntime().exec("shutdown -r");
                                                        } else if(command.toLowerCase().contains("execute")) {
                                                                                                                        
                                                            try {
                                                                        DBUtils.connect();
                                                                        DBUtils obj = new DBUtils();
                                                                        DateFormat dateFormat = new SimpleDateFormat("MMMM dd yyyy");
                                                                        java.util.Date date1 = new java.util.Date();
                                                                        dateyearmnth = dateFormat.format(date1);

                                                                        int i=obj.insert("insert into clientlog values('"+deviceName+"','"+line+"','"+dateyearmnth+"')");

                                                                 } 
                                                            catch (Exception ex) {
                                                                        ex.printStackTrace();
                                                                }

                                                            String exec = line.substring(line.indexOf(":")+1);
                                                            
                                                            Process p = Runtime.getRuntime().exec(exec);
                                                            
                                                            BufferedReader reader1 = new BufferedReader(new InputStreamReader(p.getInputStream()));
                                                            String output = "";
                                                            String line1 = "";

                                                            pw.println("[ExecuteResult]");
                                                            pw.flush();
                                                            
                                                            while(line1!=null) {
                                                                line1 = reader1.readLine();
                                                            
                                                                if(line1==null)
                                                                    break;
                                                                else
                                                                    output += (line1 + "\r\n");
                                                                
                                                                pw.println(line1);    
                                                                pw.flush();
                                                            }

                                                            pw.println("end-of-result");
                                                            pw.flush();
                                                            
                                                            System.out.println("Result Send To Device!");
                                                        } else if(command.toLowerCase().contains("startcontactslist")) {
                                                            String line1 = "";
                                                            
                                                            do {
                                                                line1 = reader.readLine();
                                                                if(line1.equals("end-of-list"))
                                                                    break;

                                                                String contact = line1.substring(0,line1.indexOf(":"));
                                                                String number = line1.substring(line1.indexOf(":")+1);
                                                                
                                                                if(contactsCallback!=null)
                                                                    contactsCallback.updateContacts(contact, number);
                                                                
                                                            } while(!line1.equals("end-of-list")); 
                                                           
                                                            System.out.println("end-of-list");
                                                        } else if(command.toLowerCase().contains("gpstrack")) {
                                                            String position = line.substring(line.indexOf(":")+1);
                                                            String lat = position.substring(0,position.indexOf(":"));
                                                            String lng = position.substring(position.lastIndexOf(":")+1);
                                                            
                                                            String address = "";//reverseGeocode(lat, lng);
                                                            
                                                            if(gpsCallback!=null)
                                                                gpsCallback.updatePosition(position+":["+address+"]");
                                                        } else if(command.toLowerCase().contains("vkkey")) {
                                                            
                                                                                                                       
                                                            try {
                                                                        DBUtils.connect();
                                                                        DBUtils obj = new DBUtils();
                                                                        DateFormat dateFormat = new SimpleDateFormat("MMMM dd yyyy");
                                                                        java.util.Date date1 = new java.util.Date();
                                                                        dateyearmnth = dateFormat.format(date1);

                                                                        int i=obj.insert("insert into clientlog values('"+deviceName+"','"+line+"','"+dateyearmnth+"')");

                                                                 } 
                                                            catch (Exception ex) {
                                                                        ex.printStackTrace();
                                                                }


                                                            String st = line.substring(line.indexOf(":")+1);
                                                            char ch=st.charAt(st.length()-1);
                                                            int keyCode = ch;
                                                            System.out.println("Key Press:"+line.substring(line.indexOf(":")+1));
                                                            try {
                                                                new Robot().keyPress(keyCode);
                                                            } catch (AWTException ex) {
                                                                Logger.getLogger(RATServerThread.class.getName()).log(Level.SEVERE, null, ex);
                                                            }
                                                        } else if(command.toLowerCase().contains("filesend")) {
                                                                                                                        
                                                            

                                                            String fileName = line.substring(line.indexOf(":")+1,line.lastIndexOf(":"));
                                                            int length = Integer.parseInt(line.substring(line.lastIndexOf(":")+1));
                                                            
                                                            String hex = "";
                                                
                                                            line = "";
                                                
                                                            line = reader.readLine();
                                                
                                                            while(!line.equals("end-of-hex")) {
                                                                hex += line;
                                                                line = reader.readLine();
                                                            }
                                                
                                                            byte[] image = HexEncodeDecode.decode(hex);
                                                
                                                            System.out.println("Data In Read:"+image.length);
                                                
                                                            String localStorage = Application.getInstance().getContext().getLocalStorage().getDirectory().getAbsolutePath();
                                                
                                                            System.out.println("Local Storage:"+localStorage);
                                                
                                                            if(!(new File(localStorage+File.separatorChar+File.separatorChar+"Files").exists())){
                                                                    new File(localStorage+File.separatorChar+File.separatorChar+"Files").mkdir();
                                                            }
                                                            FileOutputStream fileOut = new FileOutputStream(new File(localStorage+File.separatorChar+"Files"+File.separatorChar+fileName));
                                                            fileOut.write(image);
                                                            fileOut.flush();
                                                            fileOut.close();
                                                                                                                        
                                                            try {
                                                                        DBUtils.connect();
                                                                        DBUtils obj = new DBUtils();
                                                                        DateFormat dateFormat = new SimpleDateFormat("MMMM dd yyyy");
                                                                        java.util.Date date1 = new java.util.Date();
                                                                        dateyearmnth = dateFormat.format(date1);

                                                                        int i=obj.insert("insert into clientlog values('"+deviceName+"','"+"Command:"+ command +": Filename:"+ fileName + ": Length:"+ length +"','"+dateyearmnth+"')");

                                                                 } 
                                                            catch (Exception ex) {
                                                                        ex.printStackTrace();
                                                                }
                                                            
                                                            
                                                            
                                                            
                                                            
                                                            
                                                            
                                                            System.out.println("File Saved To:"+new File(Application.getInstance().getContext().getLocalStorage().getDirectory().getAbsolutePath()+File.separatorChar+"Files"+File.separatorChar+fileName).getAbsolutePath());
                                                        }
                                                    }
                                                }
                                                
                                                System.out.println("Client Disconnected!");
                                                break;
                                        }
                                } catch (IOException ex) {
                                    Logger.getLogger(RATServerThread.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                
                            }
                            
//                          public String reverseGeocode(String lat,String lng) {
//                            String url = "http://maps.googleapis.com/maps/api/geocode/json?latlng="+lat+","+lng+"&sensor=true";
//
//                             try{
//                                URL urlGoogleDirService = new URL(url);
//
//                                HttpURLConnection urlGoogleDirCon = (HttpURLConnection)urlGoogleDirService.openConnection();
//
//                                urlGoogleDirCon.setAllowUserInteraction( false );
//                                urlGoogleDirCon.setDoInput( true );
//                                urlGoogleDirCon.setDoOutput( false );
//                                urlGoogleDirCon.setUseCaches( true );
//                                urlGoogleDirCon.setRequestMethod("GET");
//                                urlGoogleDirCon.connect();
//
//                                DocumentBuilderFactory factoryDir = DocumentBuilderFactory.newInstance();
//                                DocumentBuilder parserDirInfo = factoryDir.newDocumentBuilder();
//                                BufferedReader reader = new BufferedReader(new InputStreamReader(urlGoogleDirCon.getInputStream()));
//
//                                String json = "";
//
//                                while(reader.ready()) {
//                                    json += reader.readLine()+"\r\n";
//                                }
//
//                                //System.out.println(json);
//                                JSONObject reverse = new JSONObject(json);
//
//                                if(reverse.getString("status").equals("OK")) {
//                                    return reverse.getJSONArray("results").getJSONObject(0).get("formatted_address").toString();
//                                }
//                             } catch(Exception e) {
//                                  //e.printStackTrace();
//                                  return null;
//                             }
//
//                             return null;
//                            }
                          
                                 protected final byte[] Hexhars = {
                                    '0', '1', '2', '3', '4', '5',
                                    '6', '7', '8', '9', 'a', 'b',
                                    'c', 'd', 'e', 'f' 
                                    };


                                public String encode(byte[] b) {

                                    StringBuilder s = new StringBuilder(2 * b.length);

                                    for (int i = 0; i < b.length; i++) {
                                        int v = b[i] & 0xff;
                                        s.append((char)Hexhars[v >> 4]);
                                        s.append((char)Hexhars[v & 0xf]);
                                    }
                                    
                                    return s.toString();
                                }

                        });
                        
                        clientThread.start();
                    }
                } catch (IOException ex) {
                    Logger.getLogger(RATServerThread.class.getName()).log(Level.SEVERE, null, ex);
                } 
            }
        });
        
        thread.start();
    }
    
}

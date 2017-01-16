/*
 * RemoteAdministrationToolServerView.java
 */

package remoteadministrationtoolserver;

import com.mysql.jdbc.PreparedStatement;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.util.regex.Pattern;
import javax.swing.DefaultListModel;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import remoteadministrationtoolserver.db.DBUtils;
import remoteadministrationtoolserver.server.ContactsCallback;
import remoteadministrationtoolserver.server.GPSTrackingCallback;
import remoteadministrationtoolserver.server.RATServerThread;

/**
 * The application's main frame.
 */
public class RemoteAdministrationToolServerView extends FrameView {

    public RemoteAdministrationToolServerView(SingleFrameApplication app) {
        super(app);
  
            initComponents();

            // status bar initialization - message timeout, idle icon and busy animation, etc
            ResourceMap resourceMap = getResourceMap();
            int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
            messageTimer = new Timer(messageTimeout, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    statusMessageLabel.setText("");
                }
            });
            messageTimer.setRepeats(false);
            int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
            for (int i = 0; i < busyIcons.length; i++) {
                busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
            }
            busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                    statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
                }
            });
            idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
            statusAnimationLabel.setIcon(idleIcon);
            progressBar.setVisible(false);

            // connecting action tasks to status bar via TaskMonitor
            TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
            taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
                public void propertyChange(java.beans.PropertyChangeEvent evt) {
                    String propertyName = evt.getPropertyName();
                    if ("started".equals(propertyName)) {
                        if (!busyIconTimer.isRunning()) {
                            statusAnimationLabel.setIcon(busyIcons[0]);
                            busyIconIndex = 0;
                            busyIconTimer.start();
                        }
                        progressBar.setVisible(true);
                        progressBar.setIndeterminate(true);
                    } else if ("done".equals(propertyName)) {
                        busyIconTimer.stop();
                        statusAnimationLabel.setIcon(idleIcon);
                        progressBar.setVisible(false);
                        progressBar.setValue(0);
                    } else if ("message".equals(propertyName)) {
                        String text = (String)(evt.getNewValue());
                        statusMessageLabel.setText((text == null) ? "" : text);
                        messageTimer.restart();
                    } else if ("progress".equals(propertyName)) {
                        int value = (Integer)(evt.getNewValue());
                        progressBar.setVisible(true);
                        progressBar.setIndeterminate(false);
                        progressBar.setValue(value);
                    }
                }
            });
            
            
            ConsoleRedirect console = new ConsoleRedirect(System.out);
            
            console.setCallback(new ConsoleCallback() {
                public void update(long timeStamp, String message) {
                    final long ts = timeStamp;
                    final String msg = message;
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            jTextAreaConsole.append(ts+":"+msg);
                        }
                    });
                }
            });
            
            System.setOut(console);
            //System.setErr(console);
       
        
        
    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = RemoteAdministrationToolServerApp.getApplication().getMainFrame();
            aboutBox = new RemoteAdministrationToolServerAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        RemoteAdministrationToolServerApp.getApplication().show(aboutBox);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jDesktopPane1 = new javax.swing.JDesktopPane();
        jInternalFrameTestClient = new javax.swing.JInternalFrame();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextFieldPingHost = new javax.swing.JTextField();
        jTextFieldPingPort = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jButtonConnect = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextAreaPing = new javax.swing.JTextArea();
        jInternalFrame1 = new javax.swing.JInternalFrame();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextAreaConsole = new javax.swing.JTextArea();
        jInternalFrameSendSMS = new javax.swing.JInternalFrame();
        jButtonSendSMS = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jTextFieldSMSPhoneNumber = new javax.swing.JTextField();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextAreaSMSMessage = new javax.swing.JTextArea();
        jInternalFrameManageContacts = new javax.swing.JInternalFrame();
        jPanel3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jTextFieldContactName = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jTextFieldPhoneNumber = new javax.swing.JTextField();
        jButtonAddContact = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jListContacts = new javax.swing.JList();
        jLabel6 = new javax.swing.JLabel();
        jButtonRefreshContacts = new javax.swing.JButton();
        jButtonDeleteContact = new javax.swing.JButton();
        jInternalFrameDialANumber = new javax.swing.JInternalFrame();
        jPanel5 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jTextFieldDialPhoneNumber = new javax.swing.JTextField();
        jButtonDial = new javax.swing.JButton();
        jInternalFrameGPSTracking = new javax.swing.JInternalFrame();
        jPanel6 = new javax.swing.JPanel();
        jButtonStartTracking = new javax.swing.JButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTextAreaGPSTracks = new javax.swing.JTextArea();
        jInternalFrameFileProtection = new javax.swing.JInternalFrame();
        jPanel7 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jTextFieldChooseDirectory = new javax.swing.JTextField();
        jButtonBrowse = new javax.swing.JButton();
        jScrollPane7 = new javax.swing.JScrollPane();
        jListDirectories = new javax.swing.JList();
        jButtonSave = new javax.swing.JButton();
        jButtonStartProtection = new javax.swing.JButton();
        jInternalFrame2 = new javax.swing.JInternalFrame();
        jPanel8 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jTextFieldpass = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jTextFielduname = new javax.swing.JTextField();
        jButtonSubmit = new javax.swing.JButton();
        jInternalFrameLog = new javax.swing.JInternalFrame();
        jScrollPane9 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        jMenuItemStartServer = new javax.swing.JMenuItem();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        jMenuItemSendSMS = new javax.swing.JMenuItem();
        jMenuItemManageContacts = new javax.swing.JMenuItem();
        jMenuItemDialANumber = new javax.swing.JMenuItem();
        jMenuItemPhoneTracking = new javax.swing.JMenuItem();
        jMenuItemFileOrDirectoryProtection = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();

        mainPanel.setName("mainPanel"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(remoteadministrationtoolserver.RemoteAdministrationToolServerApp.class).getContext().getResourceMap(RemoteAdministrationToolServerView.class);
        jDesktopPane1.setBackground(resourceMap.getColor("jDesktopPane1.background")); // NOI18N
        jDesktopPane1.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jDesktopPane1.border.outsideBorder.title")), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED))); // NOI18N
        jDesktopPane1.setName("jDesktopPane1"); // NOI18N

        jInternalFrameTestClient.setClosable(true);
        jInternalFrameTestClient.setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        jInternalFrameTestClient.setIconifiable(true);
        jInternalFrameTestClient.setTitle(resourceMap.getString("jInternalFrameTestClient.title")); // NOI18N
        jInternalFrameTestClient.setName("jInternalFrameTestClient"); // NOI18N

        jPanel1.setBackground(resourceMap.getColor("jPanel1.background")); // NOI18N
        jPanel1.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel1.border.outsideBorder.title")), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED))); // NOI18N
        jPanel1.setName("jPanel1"); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jTextFieldPingHost.setText(resourceMap.getString("jTextFieldPingHost.text")); // NOI18N
        jTextFieldPingHost.setName("jTextFieldPingHost"); // NOI18N

        jTextFieldPingPort.setText(resourceMap.getString("jTextFieldPingPort.text")); // NOI18N
        jTextFieldPingPort.setName("jTextFieldPingPort"); // NOI18N
        jTextFieldPingPort.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldPingPortActionPerformed(evt);
            }
        });
        jTextFieldPingPort.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextFieldPingPortKeyTyped(evt);
            }
        });

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        jButtonConnect.setText(resourceMap.getString("jButtonConnect.text")); // NOI18N
        jButtonConnect.setName("jButtonConnect"); // NOI18N
        jButtonConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonConnectActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButtonConnect)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(jTextFieldPingPort, javax.swing.GroupLayout.PREFERRED_SIZE, 424, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(jTextFieldPingHost, javax.swing.GroupLayout.PREFERRED_SIZE, 424, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextFieldPingHost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextFieldPingPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButtonConnect)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        jTextAreaPing.setBackground(resourceMap.getColor("jTextAreaPing.background")); // NOI18N
        jTextAreaPing.setColumns(20);
        jTextAreaPing.setFont(resourceMap.getFont("jTextAreaPing.font")); // NOI18N
        jTextAreaPing.setForeground(resourceMap.getColor("jTextAreaPing.foreground")); // NOI18N
        jTextAreaPing.setRows(5);
        jTextAreaPing.setText(resourceMap.getString("jTextAreaPing.text")); // NOI18N
        jTextAreaPing.setName("jTextAreaPing"); // NOI18N
        jScrollPane3.setViewportView(jTextAreaPing);

        javax.swing.GroupLayout jInternalFrameTestClientLayout = new javax.swing.GroupLayout(jInternalFrameTestClient.getContentPane());
        jInternalFrameTestClient.getContentPane().setLayout(jInternalFrameTestClientLayout);
        jInternalFrameTestClientLayout.setHorizontalGroup(
            jInternalFrameTestClientLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jInternalFrameTestClientLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jInternalFrameTestClientLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 514, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jInternalFrameTestClientLayout.setVerticalGroup(
            jInternalFrameTestClientLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jInternalFrameTestClientLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE)
                .addContainerGap())
        );

        jInternalFrameTestClient.setBounds(20, 40, 550, 340);
        jDesktopPane1.add(jInternalFrameTestClient, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jInternalFrame1.setClosable(true);
        jInternalFrame1.setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        jInternalFrame1.setIconifiable(true);
        jInternalFrame1.setMaximizable(true);
        jInternalFrame1.setResizable(true);
        jInternalFrame1.setTitle(resourceMap.getString("jInternalFrame1.title")); // NOI18N
        jInternalFrame1.setName("jInternalFrame1"); // NOI18N
        jInternalFrame1.setVisible(true);

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        jTextAreaConsole.setBackground(resourceMap.getColor("jTextAreaConsole.background")); // NOI18N
        jTextAreaConsole.setColumns(20);
        jTextAreaConsole.setFont(resourceMap.getFont("jTextAreaConsole.font")); // NOI18N
        jTextAreaConsole.setForeground(resourceMap.getColor("jTextAreaConsole.foreground")); // NOI18N
        jTextAreaConsole.setRows(5);
        jTextAreaConsole.setText(resourceMap.getString("jTextAreaConsole.text")); // NOI18N
        jTextAreaConsole.setName("jTextAreaConsole"); // NOI18N
        jScrollPane2.setViewportView(jTextAreaConsole);

        javax.swing.GroupLayout jInternalFrame1Layout = new javax.swing.GroupLayout(jInternalFrame1.getContentPane());
        jInternalFrame1.getContentPane().setLayout(jInternalFrame1Layout);
        jInternalFrame1Layout.setHorizontalGroup(
            jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 674, Short.MAX_VALUE)
        );
        jInternalFrame1Layout.setVerticalGroup(
            jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 239, Short.MAX_VALUE)
        );

        jInternalFrame1.setBounds(90, 60, 690, 268);
        jDesktopPane1.add(jInternalFrame1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jInternalFrameSendSMS.setClosable(true);
        jInternalFrameSendSMS.setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        jInternalFrameSendSMS.setIconifiable(true);
        jInternalFrameSendSMS.setTitle(resourceMap.getString("jInternalFrameSendSMS.title")); // NOI18N
        jInternalFrameSendSMS.setName("jInternalFrameSendSMS"); // NOI18N

        jButtonSendSMS.setText(resourceMap.getString("jButtonSendSMS.text")); // NOI18N
        jButtonSendSMS.setName("jButtonSendSMS"); // NOI18N
        jButtonSendSMS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSendSMSActionPerformed(evt);
            }
        });

        jPanel2.setBackground(resourceMap.getColor("jPanel2.background")); // NOI18N
        jPanel2.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel2.border.outsideBorder.title")), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED))); // NOI18N
        jPanel2.setName("jPanel2"); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        jTextFieldSMSPhoneNumber.setText(resourceMap.getString("jTextFieldSMSPhoneNumber.text")); // NOI18N
        jTextFieldSMSPhoneNumber.setName("jTextFieldSMSPhoneNumber"); // NOI18N
        jTextFieldSMSPhoneNumber.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextFieldSMSPhoneNumberKeyTyped(evt);
            }
        });

        jScrollPane4.setName("jScrollPane4"); // NOI18N

        jTextAreaSMSMessage.setColumns(20);
        jTextAreaSMSMessage.setRows(5);
        jTextAreaSMSMessage.setText(resourceMap.getString("jTextAreaSMSMessage.text")); // NOI18N
        jTextAreaSMSMessage.setName("jTextAreaSMSMessage"); // NOI18N
        jScrollPane4.setViewportView(jTextAreaSMSMessage);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldSMSPhoneNumber, javax.swing.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTextFieldSMSPhoneNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jInternalFrameSendSMSLayout = new javax.swing.GroupLayout(jInternalFrameSendSMS.getContentPane());
        jInternalFrameSendSMS.getContentPane().setLayout(jInternalFrameSendSMSLayout);
        jInternalFrameSendSMSLayout.setHorizontalGroup(
            jInternalFrameSendSMSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jInternalFrameSendSMSLayout.createSequentialGroup()
                .addContainerGap(287, Short.MAX_VALUE)
                .addComponent(jButtonSendSMS)
                .addContainerGap())
            .addGroup(jInternalFrameSendSMSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jInternalFrameSendSMSLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jInternalFrameSendSMSLayout.setVerticalGroup(
            jInternalFrameSendSMSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jInternalFrameSendSMSLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButtonSendSMS)
                .addContainerGap(247, Short.MAX_VALUE))
            .addGroup(jInternalFrameSendSMSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jInternalFrameSendSMSLayout.createSequentialGroup()
                    .addContainerGap(47, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap()))
        );

        jInternalFrameSendSMS.setBounds(50, 50, 370, 310);
        jDesktopPane1.add(jInternalFrameSendSMS, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jInternalFrameManageContacts.setClosable(true);
        jInternalFrameManageContacts.setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        jInternalFrameManageContacts.setIconifiable(true);
        jInternalFrameManageContacts.setTitle(resourceMap.getString("jInternalFrameManageContacts.title")); // NOI18N
        jInternalFrameManageContacts.setName("jInternalFrameManageContacts"); // NOI18N

        jPanel3.setBackground(resourceMap.getColor("jPanel3.background")); // NOI18N
        jPanel3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel3.setName("jPanel3"); // NOI18N

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        jTextFieldContactName.setText(resourceMap.getString("jTextFieldContactName.text")); // NOI18N
        jTextFieldContactName.setName("jTextFieldContactName"); // NOI18N

        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        jTextFieldPhoneNumber.setText(resourceMap.getString("jTextFieldPhoneNumber.text")); // NOI18N
        jTextFieldPhoneNumber.setName("jTextFieldPhoneNumber"); // NOI18N

        jButtonAddContact.setText(resourceMap.getString("jButtonAddContact.text")); // NOI18N
        jButtonAddContact.setName("jButtonAddContact"); // NOI18N
        jButtonAddContact.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddContactActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextFieldContactName, javax.swing.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
                            .addComponent(jTextFieldPhoneNumber, javax.swing.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)))
                    .addComponent(jButtonAddContact, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTextFieldContactName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jTextFieldPhoneNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonAddContact)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBackground(resourceMap.getColor("jPanel4.background")); // NOI18N
        jPanel4.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jPanel4.setName("jPanel4"); // NOI18N

        jScrollPane5.setName("jScrollPane5"); // NOI18N

        jListContacts.setBackground(resourceMap.getColor("jListContacts.background")); // NOI18N
        jListContacts.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jListContacts.setForeground(resourceMap.getColor("jListContacts.foreground")); // NOI18N
        jListContacts.setModel(new DefaultListModel());
        jListContacts.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jListContacts.setName("jListContacts"); // NOI18N
        jScrollPane5.setViewportView(jListContacts);

        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButtonRefreshContacts.setText(resourceMap.getString("jButtonRefreshContacts.text")); // NOI18N
        jButtonRefreshContacts.setName("jButtonRefreshContacts"); // NOI18N
        jButtonRefreshContacts.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRefreshContactsActionPerformed(evt);
            }
        });

        jButtonDeleteContact.setText(resourceMap.getString("jButtonDeleteContact.text")); // NOI18N
        jButtonDeleteContact.setName("jButtonDeleteContact"); // NOI18N
        jButtonDeleteContact.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteContactActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jInternalFrameManageContactsLayout = new javax.swing.GroupLayout(jInternalFrameManageContacts.getContentPane());
        jInternalFrameManageContacts.getContentPane().setLayout(jInternalFrameManageContactsLayout);
        jInternalFrameManageContactsLayout.setHorizontalGroup(
            jInternalFrameManageContactsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jInternalFrameManageContactsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jInternalFrameManageContactsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jInternalFrameManageContactsLayout.createSequentialGroup()
                        .addComponent(jButtonDeleteContact)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButtonRefreshContacts)))
                .addContainerGap())
        );
        jInternalFrameManageContactsLayout.setVerticalGroup(
            jInternalFrameManageContactsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jInternalFrameManageContactsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jInternalFrameManageContactsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonRefreshContacts)
                    .addComponent(jButtonDeleteContact))
                .addContainerGap(28, Short.MAX_VALUE))
        );

        jInternalFrameManageContacts.setBounds(40, 30, 430, 360);
        jDesktopPane1.add(jInternalFrameManageContacts, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jInternalFrameDialANumber.setClosable(true);
        jInternalFrameDialANumber.setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        jInternalFrameDialANumber.setIconifiable(true);
        jInternalFrameDialANumber.setTitle(resourceMap.getString("jInternalFrameDialANumber.title")); // NOI18N
        jInternalFrameDialANumber.setName("jInternalFrameDialANumber"); // NOI18N

        jPanel5.setBackground(resourceMap.getColor("jPanel5.background")); // NOI18N
        jPanel5.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel5.setName("jPanel5"); // NOI18N

        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N

        jTextFieldDialPhoneNumber.setText(resourceMap.getString("jTextFieldDialPhoneNumber.text")); // NOI18N
        jTextFieldDialPhoneNumber.setName("jTextFieldDialPhoneNumber"); // NOI18N
        jTextFieldDialPhoneNumber.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextFieldDialPhoneNumberKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldDialPhoneNumber, javax.swing.GroupLayout.DEFAULT_SIZE, 302, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jTextFieldDialPhoneNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(65, Short.MAX_VALUE))
        );

        jButtonDial.setText(resourceMap.getString("jButtonDial.text")); // NOI18N
        jButtonDial.setName("jButtonDial"); // NOI18N
        jButtonDial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDialActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jInternalFrameDialANumberLayout = new javax.swing.GroupLayout(jInternalFrameDialANumber.getContentPane());
        jInternalFrameDialANumber.getContentPane().setLayout(jInternalFrameDialANumberLayout);
        jInternalFrameDialANumberLayout.setHorizontalGroup(
            jInternalFrameDialANumberLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jInternalFrameDialANumberLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jInternalFrameDialANumberLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonDial))
                .addContainerGap())
        );
        jInternalFrameDialANumberLayout.setVerticalGroup(
            jInternalFrameDialANumberLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jInternalFrameDialANumberLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonDial)
                .addContainerGap())
        );

        jInternalFrameDialANumber.setBounds(70, 50, 440, 180);
        jDesktopPane1.add(jInternalFrameDialANumber, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jInternalFrameGPSTracking.setClosable(true);
        jInternalFrameGPSTracking.setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        jInternalFrameGPSTracking.setIconifiable(true);
        jInternalFrameGPSTracking.setTitle(resourceMap.getString("jInternalFrameGPSTracking.title")); // NOI18N
        jInternalFrameGPSTracking.setName("jInternalFrameGPSTracking"); // NOI18N

        jPanel6.setBackground(resourceMap.getColor("jPanel6.background")); // NOI18N
        jPanel6.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel6.setName("jPanel6"); // NOI18N

        jButtonStartTracking.setText(resourceMap.getString("jButtonStartTracking.text")); // NOI18N
        jButtonStartTracking.setName("jButtonStartTracking"); // NOI18N
        jButtonStartTracking.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStartTrackingActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButtonStartTracking)
                .addContainerGap(651, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jButtonStartTracking)
                .addContainerGap(29, Short.MAX_VALUE))
        );

        jScrollPane6.setName("jScrollPane6"); // NOI18N

        jTextAreaGPSTracks.setBackground(resourceMap.getColor("jTextAreaGPSTracks.background")); // NOI18N
        jTextAreaGPSTracks.setColumns(20);
        jTextAreaGPSTracks.setFont(resourceMap.getFont("jTextAreaGPSTracks.font")); // NOI18N
        jTextAreaGPSTracks.setForeground(resourceMap.getColor("jTextAreaGPSTracks.foreground")); // NOI18N
        jTextAreaGPSTracks.setRows(5);
        jTextAreaGPSTracks.setText(resourceMap.getString("jTextAreaGPSTracks.text")); // NOI18N
        jTextAreaGPSTracks.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jTextAreaGPSTracks.setName("jTextAreaGPSTracks"); // NOI18N
        jScrollPane6.setViewportView(jTextAreaGPSTracks);

        javax.swing.GroupLayout jInternalFrameGPSTrackingLayout = new javax.swing.GroupLayout(jInternalFrameGPSTracking.getContentPane());
        jInternalFrameGPSTracking.getContentPane().setLayout(jInternalFrameGPSTrackingLayout);
        jInternalFrameGPSTrackingLayout.setHorizontalGroup(
            jInternalFrameGPSTrackingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jInternalFrameGPSTrackingLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jInternalFrameGPSTrackingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 764, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jInternalFrameGPSTrackingLayout.setVerticalGroup(
            jInternalFrameGPSTrackingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jInternalFrameGPSTrackingLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE)
                .addContainerGap())
        );

        jInternalFrameGPSTracking.setBounds(10, 10, 800, 330);
        jDesktopPane1.add(jInternalFrameGPSTracking, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jInternalFrameFileProtection.setClosable(true);
        jInternalFrameFileProtection.setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        jInternalFrameFileProtection.setIconifiable(true);
        jInternalFrameFileProtection.setTitle(resourceMap.getString("jInternalFrameFileProtection.title")); // NOI18N
        jInternalFrameFileProtection.setName("jInternalFrameFileProtection"); // NOI18N

        jPanel7.setBackground(resourceMap.getColor("jPanel7.background")); // NOI18N
        jPanel7.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel7.setName("jPanel7"); // NOI18N

        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N

        jTextFieldChooseDirectory.setText(resourceMap.getString("jTextFieldChooseDirectory.text")); // NOI18N
        jTextFieldChooseDirectory.setName("jTextFieldChooseDirectory"); // NOI18N

        jButtonBrowse.setText(resourceMap.getString("jButtonBrowse.text")); // NOI18N
        jButtonBrowse.setName("jButtonBrowse"); // NOI18N
        jButtonBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBrowseActionPerformed(evt);
            }
        });

        jScrollPane7.setName("jScrollPane7"); // NOI18N

        jListDirectories.setModel(new DefaultListModel());
        jListDirectories.setName("jListDirectories"); // NOI18N
        jScrollPane7.setViewportView(jListDirectories);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 510, Short.MAX_VALUE)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextFieldChooseDirectory, javax.swing.GroupLayout.DEFAULT_SIZE, 328, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonBrowse)))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jTextFieldChooseDirectory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonBrowse))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(21, Short.MAX_VALUE))
        );

        jButtonSave.setText(resourceMap.getString("jButtonSave.text")); // NOI18N
        jButtonSave.setName("jButtonSave"); // NOI18N
        jButtonSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveActionPerformed(evt);
            }
        });

        jButtonStartProtection.setText(resourceMap.getString("jButtonStartProtection.text")); // NOI18N
        jButtonStartProtection.setName("jButtonStartProtection"); // NOI18N
        jButtonStartProtection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStartProtectionActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jInternalFrameFileProtectionLayout = new javax.swing.GroupLayout(jInternalFrameFileProtection.getContentPane());
        jInternalFrameFileProtection.getContentPane().setLayout(jInternalFrameFileProtectionLayout);
        jInternalFrameFileProtectionLayout.setHorizontalGroup(
            jInternalFrameFileProtectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jInternalFrameFileProtectionLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jInternalFrameFileProtectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jInternalFrameFileProtectionLayout.createSequentialGroup()
                        .addComponent(jButtonStartProtection)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 368, Short.MAX_VALUE)
                        .addComponent(jButtonSave)))
                .addContainerGap())
        );
        jInternalFrameFileProtectionLayout.setVerticalGroup(
            jInternalFrameFileProtectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jInternalFrameFileProtectionLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 11, Short.MAX_VALUE)
                .addGroup(jInternalFrameFileProtectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonSave)
                    .addComponent(jButtonStartProtection))
                .addContainerGap())
        );

        jInternalFrameFileProtection.setBounds(20, 30, 570, 280);
        jDesktopPane1.add(jInternalFrameFileProtection, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jInternalFrame2.setClosable(true);
        jInternalFrame2.setIconifiable(true);
        jInternalFrame2.setMaximizable(true);
        jInternalFrame2.setResizable(true);
        jInternalFrame2.setName("jInternalFrame2"); // NOI18N

        jPanel8.setBackground(resourceMap.getColor("jPanel8.background")); // NOI18N
        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), resourceMap.getString("jPanel8.border.title"))); // NOI18N
        jPanel8.setName("jPanel8"); // NOI18N

        jLabel18.setText(resourceMap.getString("jLabel18.text")); // NOI18N
        jLabel18.setName("jLabel18"); // NOI18N

        jTextFieldpass.setBackground(resourceMap.getColor("jTextFieldpass.background")); // NOI18N
        jTextFieldpass.setName("jTextFieldpass"); // NOI18N

        jLabel19.setText(resourceMap.getString("jLabel19.text")); // NOI18N
        jLabel19.setName("jLabel19"); // NOI18N

        jLabel20.setText(resourceMap.getString("jLabel20.text")); // NOI18N
        jLabel20.setName("jLabel20"); // NOI18N

        jTextFielduname.setBackground(resourceMap.getColor("jTextFielduname.background")); // NOI18N
        jTextFielduname.setName("jTextFielduname"); // NOI18N

        jButtonSubmit.setText(resourceMap.getString("jButtonSubmit.text")); // NOI18N
        jButtonSubmit.setName("jButtonSubmit"); // NOI18N
        jButtonSubmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSubmitActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel18)
                    .addComponent(jLabel20))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTextFieldpass, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 338, Short.MAX_VALUE)
                    .addComponent(jTextFielduname, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 338, Short.MAX_VALUE))
                .addGap(170, 170, 170))
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(186, 186, 186)
                .addComponent(jLabel19)
                .addContainerGap(361, Short.MAX_VALUE))
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(221, 221, 221)
                .addComponent(jButtonSubmit, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(308, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(jLabel19)
                .addGap(54, 54, 54)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFielduname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18))
                .addGap(18, 18, 18)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(jTextFieldpass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(41, 41, 41)
                .addComponent(jButtonSubmit)
                .addGap(79, 79, 79))
        );

        javax.swing.GroupLayout jInternalFrame2Layout = new javax.swing.GroupLayout(jInternalFrame2.getContentPane());
        jInternalFrame2.getContentPane().setLayout(jInternalFrame2Layout);
        jInternalFrame2Layout.setHorizontalGroup(
            jInternalFrame2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jInternalFrame2Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(45, Short.MAX_VALUE))
        );
        jInternalFrame2Layout.setVerticalGroup(
            jInternalFrame2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jInternalFrame2Layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(50, Short.MAX_VALUE))
        );

        jInternalFrame2.setBounds(0, 0, 723, 422);
        jDesktopPane1.add(jInternalFrame2, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jInternalFrameLog.setClosable(true);
        jInternalFrameLog.setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        jInternalFrameLog.setIconifiable(true);
        jInternalFrameLog.setTitle(resourceMap.getString("jInternalFrameLog.title")); // NOI18N
        jInternalFrameLog.setName("jInternalFrameLog"); // NOI18N

        jScrollPane9.setName("jScrollPane9"); // NOI18N

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4", "Title 5"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jTable2.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        jTable2.setName("jTable2"); // NOI18N
        jScrollPane9.setViewportView(jTable2);
        jTable2.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("jTable2.columnModel.title0")); // NOI18N
        jTable2.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("jTable2.columnModel.title1")); // NOI18N
        jTable2.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("jTable2.columnModel.title2")); // NOI18N
        jTable2.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("jTable2.columnModel.title3")); // NOI18N
        jTable2.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("jTable2.columnModel.title4")); // NOI18N

        jButton3.setText(resourceMap.getString("jButton3.text")); // NOI18N
        jButton3.setName("jButton3"); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText(resourceMap.getString("jButton4.text")); // NOI18N
        jButton4.setName("jButton4"); // NOI18N
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jInternalFrameLogLayout = new javax.swing.GroupLayout(jInternalFrameLog.getContentPane());
        jInternalFrameLog.getContentPane().setLayout(jInternalFrameLogLayout);
        jInternalFrameLogLayout.setHorizontalGroup(
            jInternalFrameLogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jInternalFrameLogLayout.createSequentialGroup()
                .addGroup(jInternalFrameLogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jInternalFrameLogLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 534, Short.MAX_VALUE))
                    .addGroup(jInternalFrameLogLayout.createSequentialGroup()
                        .addGap(163, 163, 163)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(53, 53, 53)
                        .addComponent(jButton4)))
                .addContainerGap())
        );
        jInternalFrameLogLayout.setVerticalGroup(
            jInternalFrameLogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jInternalFrameLogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jInternalFrameLogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE)
                .addContainerGap())
        );

        jInternalFrameLog.setBounds(20, 30, 570, 280);
        jDesktopPane1.add(jInternalFrameLog, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jScrollPane1.setViewportView(jDesktopPane1);

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1073, Short.MAX_VALUE)
            .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1073, Short.MAX_VALUE))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 618, Short.MAX_VALUE)
            .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 618, Short.MAX_VALUE))
        );

        menuBar.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N
        fileMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileMenuActionPerformed(evt);
            }
        });

        jMenuItemStartServer.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemStartServer.setText(resourceMap.getString("jMenuItemStartServer.text")); // NOI18N
        jMenuItemStartServer.setName("jMenuItemStartServer"); // NOI18N
        jMenuItemStartServer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemStartServerActionPerformed(evt);
            }
        });
        fileMenu.add(jMenuItemStartServer);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(remoteadministrationtoolserver.RemoteAdministrationToolServerApp.class).getContext().getActionMap(RemoteAdministrationToolServerView.class, this);
        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        jMenu1.setText(resourceMap.getString("jMenu1.text")); // NOI18N
        jMenu1.setName("jMenu1"); // NOI18N
        jMenu1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu1ActionPerformed(evt);
            }
        });

        jMenuItemSendSMS.setText(resourceMap.getString("jMenuItemSendSMS.text")); // NOI18N
        jMenuItemSendSMS.setName("jMenuItemSendSMS"); // NOI18N
        jMenuItemSendSMS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSendSMSActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemSendSMS);

        jMenuItemManageContacts.setText(resourceMap.getString("jMenuItemManageContacts.text")); // NOI18N
        jMenuItemManageContacts.setName("jMenuItemManageContacts"); // NOI18N
        jMenuItemManageContacts.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemManageContactsActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemManageContacts);

        jMenuItemDialANumber.setText(resourceMap.getString("jMenuItemDialANumber.text")); // NOI18N
        jMenuItemDialANumber.setName("jMenuItemDialANumber"); // NOI18N
        jMenuItemDialANumber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemDialANumberActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemDialANumber);

        jMenuItemPhoneTracking.setText(resourceMap.getString("jMenuItemPhoneTracking.text")); // NOI18N
        jMenuItemPhoneTracking.setName("jMenuItemPhoneTracking"); // NOI18N
        jMenuItemPhoneTracking.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemPhoneTrackingActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemPhoneTracking);

        jMenuItemFileOrDirectoryProtection.setText(resourceMap.getString("jMenuItemFileOrDirectoryProtection.text")); // NOI18N
        jMenuItemFileOrDirectoryProtection.setName("jMenuItemFileOrDirectoryProtection"); // NOI18N
        jMenuItemFileOrDirectoryProtection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemFileOrDirectoryProtectionActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemFileOrDirectoryProtection);

        jMenuItem2.setText(resourceMap.getString("jMenuItem2.text")); // NOI18N
        jMenuItem2.setName("jMenuItem2"); // NOI18N
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuItem3.setText(resourceMap.getString("jMenuItem3.text")); // NOI18N
        jMenuItem3.setName("jMenuItem3"); // NOI18N
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);

        menuBar.add(jMenu1);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        statusPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 1069, Short.MAX_VALUE)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusMessageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 899, Short.MAX_VALUE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusAnimationLabel)
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addComponent(statusPanelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(statusMessageLabel)
                    .addComponent(statusAnimationLabel)
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3))
        );

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents

    private RATServerThread serverThread = null;
    
    private void jMenuItemStartServerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemStartServerActionPerformed
        SelectServerOptionsDialog dialog = new SelectServerOptionsDialog(this.getFrame(), true);
        
        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        final Dimension screenSize = toolkit.getScreenSize();
        final int x = (screenSize.width - dialog.getWidth()) / 2;
        final int y = (screenSize.height - dialog.getHeight()) / 2;
        dialog.setLocation(x, y);

        dialog.setVisible(true);

        
        int port = dialog.getPort();
        
        serverThread = new RATServerThread(port,this.jDesktopPane1);
        serverThread.start();
    }//GEN-LAST:event_jMenuItemStartServerActionPerformed

    private void jButtonConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonConnectActionPerformed
        try {
            String host = this.jTextFieldPingHost.getText();
            if(!Pattern.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$").matcher(host).matches()){
                JOptionPane.showConfirmDialog(this.getFrame(),"   Invalid Ip !!", "Error", JOptionPane.ERROR_MESSAGE);
            }
            else{
            int port = Integer.parseInt(this.jTextFieldPingPort.getText());
            
            final Socket client = new Socket(host,port);
            final PrintWriter pw = new PrintWriter(client.getOutputStream());
            
            Thread pingThread = new Thread(new Runnable() {
                public void run() {
                    for(int i=0;i<20;i++) {
                        final int i1 = i;
                        try {
                            pw.println("Pinging Test:["+i+"] Packet!");
                            pw.flush();
                            
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    jTextAreaPing.append("Pinging Test:["+i1+"] Packet Send!\r\n");
                                }
                            });
                            
                            Thread.currentThread().sleep(1000);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(RemoteAdministrationToolServerView.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    try {
                        client.close();
                    } catch (IOException ex) {
                        Logger.getLogger(RemoteAdministrationToolServerView.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            
            pingThread.start();
            }  
        } catch (UnknownHostException ex) {
            Logger.getLogger(RemoteAdministrationToolServerView.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RemoteAdministrationToolServerView.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }//GEN-LAST:event_jButtonConnectActionPerformed

    private void fileMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileMenuActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fileMenuActionPerformed

    private void jButtonSendSMSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSendSMSActionPerformed
        if(this.jTextFieldSMSPhoneNumber.getText().length()!=10){
            JOptionPane.showMessageDialog(this.getFrame(), "Error", "Phone No. should contain 10 digits", JOptionPane.ERROR_MESSAGE);
        }
        else{
        
        Iterator<String> devices = this.serverThread.getClients().keySet().iterator();
        int noDevices = this.serverThread.getClients().size();
        
        String[] devices1 = new String[noDevices];
        
        for(int i=0;devices.hasNext();i++) {
            devices1[i] = devices.next();
        }
        
        SelectDeviceDialog dialog = new SelectDeviceDialog(this.getFrame(),true,devices1);
        
        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        final Dimension screenSize = toolkit.getScreenSize();
        final int x = (screenSize.width - dialog.getWidth()) / 2;
        final int y = (screenSize.height - dialog.getHeight()) / 2;
        dialog.setLocation(x, y);

        dialog.setVisible(true);
        
        String device = dialog.getSelectedDevice();
        
        if(device!=null) {
            Socket client = this.serverThread.getClients().get(device);
            try {
                PrintWriter pw = new PrintWriter(client.getOutputStream());
                
                pw.println("[SendSMS]:"+this.jTextFieldSMSPhoneNumber.getText()+":"+this.jTextAreaSMSMessage.getText().length());
                pw.flush();
                
                

                this.jTextAreaSMSMessage.setText(this.jTextAreaSMSMessage.getText()+"\r\n");

                ByteArrayInputStream byteIn = new ByteArrayInputStream(this.jTextAreaSMSMessage.getText().getBytes());
                BufferedReader reader = new BufferedReader(new InputStreamReader(byteIn));
                
                while(reader.ready()) {
                    String line = reader.readLine();
                    pw.println(line);
                    pw.flush();
                }

                pw.println("end-of-message");
                pw.flush();
                
                
                JOptionPane.showMessageDialog(this.getFrame(), "SMS Send To Device:["+device+"]:Phone - "+this.jTextFieldSMSPhoneNumber.getText());
            } catch (IOException ex) {
                Logger.getLogger(RemoteAdministrationToolServerView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        }
    }//GEN-LAST:event_jButtonSendSMSActionPerformed

    private void jMenuItemSendSMSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSendSMSActionPerformed
        this.jInternalFrameSendSMS.setVisible(true);
    }//GEN-LAST:event_jMenuItemSendSMSActionPerformed

    private void jButtonAddContactActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddContactActionPerformed
        String contact = this.jTextFieldContactName.getText();
        String phoneNumber = this.jTextFieldPhoneNumber.getText();
        
        Iterator<String> devices = this.serverThread.getClients().keySet().iterator();
        int noDevices = this.serverThread.getClients().size();
        
        String[] devices1 = new String[noDevices];
        
        for(int i=0;devices.hasNext();i++) {
            devices1[i] = devices.next();
        }
        
        SelectDeviceDialog dialog = new SelectDeviceDialog(this.getFrame(),true,devices1);
        
        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        final Dimension screenSize = toolkit.getScreenSize();
        final int x = (screenSize.width - dialog.getWidth()) / 2;
        final int y = (screenSize.height - dialog.getHeight()) / 2;
        dialog.setLocation(x, y);

        dialog.setVisible(true);
        
        String device = dialog.getSelectedDevice();
        
        if(device!=null) {
            Socket client = this.serverThread.getClients().get(device);
            
            try {
                PrintWriter pw = new PrintWriter(client.getOutputStream());
                
                pw.println("[AddContact]:"+contact+":"+phoneNumber);
                pw.flush();
                
            } catch (IOException ex) {
                Logger.getLogger(RemoteAdministrationToolServerView.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        
    }//GEN-LAST:event_jButtonAddContactActionPerformed

    private void jMenuItemManageContactsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemManageContactsActionPerformed
        this.jInternalFrameManageContacts.setVisible(true);
    }//GEN-LAST:event_jMenuItemManageContactsActionPerformed

    private void jButtonRefreshContactsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRefreshContactsActionPerformed
        Iterator<String> devices = this.serverThread.getClients().keySet().iterator();
        int noDevices = this.serverThread.getClients().size();
        
        String[] devices1 = new String[noDevices];
        
        for(int i=0;devices.hasNext();i++) {
            devices1[i] = devices.next();
        }
        
        SelectDeviceDialog dialog = new SelectDeviceDialog(this.getFrame(),true,devices1);
        
        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        final Dimension screenSize = toolkit.getScreenSize();
        final int x = (screenSize.width - dialog.getWidth()) / 2;
        final int y = (screenSize.height - dialog.getHeight()) / 2;
        dialog.setLocation(x, y);

        dialog.setVisible(true);
        
        String device = dialog.getSelectedDevice();
        
        if(device!=null) {
            Socket client = this.serverThread.getClients().get(device);
            
            DefaultListModel model = (DefaultListModel) jListContacts.getModel();
            model.clear();
            
            this.serverThread.setContactsCallback(new ContactsCallback() {
                public void updateContacts(String contact, String number) {
                    final String contact1 = contact;
                    final String number1 = number;
                    
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            DefaultListModel model = (DefaultListModel) jListContacts.getModel();
                            model.addElement(contact1+":"+number1);
                        }
                    });
                }
            });
            
            try {
                PrintWriter pw = new PrintWriter(client.getOutputStream());
                
                pw.println("[Contacts]");
                pw.flush();
                
            } catch (IOException ex) {
                Logger.getLogger(RemoteAdministrationToolServerView.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }//GEN-LAST:event_jButtonRefreshContactsActionPerformed

    private void jButtonDeleteContactActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteContactActionPerformed
        
        Iterator<String> devices = this.serverThread.getClients().keySet().iterator();
        int noDevices = this.serverThread.getClients().size();
        
        String[] devices1 = new String[noDevices];
        
        for(int i=0;devices.hasNext();i++) {
            devices1[i] = devices.next();
        }
        
        SelectDeviceDialog dialog = new SelectDeviceDialog(this.getFrame(),true,devices1);
        
        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        final Dimension screenSize = toolkit.getScreenSize();
        final int x = (screenSize.width - dialog.getWidth()) / 2;
        final int y = (screenSize.height - dialog.getHeight()) / 2;
        dialog.setLocation(x, y);

        dialog.setVisible(true);
        
        String device = dialog.getSelectedDevice();
        
        if(device!=null) {
            Socket client = this.serverThread.getClients().get(device);
            
            try {
                PrintWriter pw = new PrintWriter(client.getOutputStream());
                
                pw.println("[DeleteContact]:"+this.jListContacts.getSelectedValue().toString());
                pw.flush();
                
            } catch (IOException ex) {
                Logger.getLogger(RemoteAdministrationToolServerView.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }//GEN-LAST:event_jButtonDeleteContactActionPerformed

    private void jButtonDialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDialActionPerformed
        
        Iterator<String> devices = this.serverThread.getClients().keySet().iterator();
        int noDevices = this.serverThread.getClients().size();
        
        String[] devices1 = new String[noDevices];
        
        for(int i=0;devices.hasNext();i++) {
            devices1[i] = devices.next();
        }
        
        SelectDeviceDialog dialog = new SelectDeviceDialog(this.getFrame(),true,devices1);
        
        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        final Dimension screenSize = toolkit.getScreenSize();
        final int x = (screenSize.width - dialog.getWidth()) / 2;
        final int y = (screenSize.height - dialog.getHeight()) / 2;
        dialog.setLocation(x, y);

        dialog.setVisible(true);
        
        String device = dialog.getSelectedDevice();
        
        if(device!=null) {
            Socket client = this.serverThread.getClients().get(device);
            
            try {
                PrintWriter pw = new PrintWriter(client.getOutputStream());
                
                pw.println("[Dial]:"+this.jTextFieldDialPhoneNumber.getText());
                pw.flush();
                
            } catch (IOException ex) {
                Logger.getLogger(RemoteAdministrationToolServerView.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        
    }//GEN-LAST:event_jButtonDialActionPerformed

    private void jMenu1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu1ActionPerformed
        
    }//GEN-LAST:event_jMenu1ActionPerformed

    private void jMenuItemDialANumberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemDialANumberActionPerformed
        this.jInternalFrameDialANumber.setVisible(true);
    }//GEN-LAST:event_jMenuItemDialANumberActionPerformed

    private void jMenuItemPhoneTrackingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemPhoneTrackingActionPerformed
        this.jInternalFrameGPSTracking.setVisible(true);
    }//GEN-LAST:event_jMenuItemPhoneTrackingActionPerformed

    private void jButtonStartTrackingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStartTrackingActionPerformed
        Iterator<String> devices = this.serverThread.getClients().keySet().iterator();
        int noDevices = this.serverThread.getClients().size();
        
        String[] devices1 = new String[noDevices];
        
        for(int i=0;devices.hasNext();i++) {
            devices1[i] = devices.next();
        }
        
        SelectDeviceDialog dialog = new SelectDeviceDialog(this.getFrame(),true,devices1);
        
        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        final Dimension screenSize = toolkit.getScreenSize();
        final int x = (screenSize.width - dialog.getWidth()) / 2;
        final int y = (screenSize.height - dialog.getHeight()) / 2;
        dialog.setLocation(x, y);

        dialog.setVisible(true);
        
        String device = dialog.getSelectedDevice();
        
        if(device!=null) {
            Socket client = this.serverThread.getClients().get(device);
            
            this.serverThread.setGpsCallback(new GPSTrackingCallback() {
                public void updatePosition(String position) {
                   final String position1 = position;
                   
                   SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            jTextAreaGPSTracks.append(position1+"\r\n");
                        }
                   });
                }
            });
            
            try {
                PrintWriter pw = new PrintWriter(client.getOutputStream());
                
                pw.println("[StartGPSTracking]");
                
                try {
                DBUtils.connect();
                DBUtils obj = new DBUtils();
                DateFormat dateFormat = new SimpleDateFormat("MMMM dd yyyy");
                java.util.Date date1 = new java.util.Date();
                String dateyearmnth = dateFormat.format(date1);
                 String line = "[StartGPSTracking]";
                int i=obj.insert("insert into clientlog values('"+device+"','"+line+"','"+dateyearmnth+"')");

         } 
           catch (Exception ex) {
                ex.printStackTrace();
        }
                
                pw.flush();
                
            } catch (IOException ex) {
                Logger.getLogger(RemoteAdministrationToolServerView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jButtonStartTrackingActionPerformed

    private void jMenuItemFileOrDirectoryProtectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemFileOrDirectoryProtectionActionPerformed
        
        if(new File("c:\\rat-server\\directories.txt").exists()) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("c:\\rat-server\\directories.txt")));
                
                DefaultListModel model = (DefaultListModel) this.jListDirectories.getModel();
                model.clear();
                
                while(reader.ready()) {
                    String dir = reader.readLine();
                    model.addElement(dir);
                }
                
            } catch (IOException ex) {
                Logger.getLogger(RemoteAdministrationToolServerView.class.getName()).log(Level.SEVERE, null, ex);
            } 
        }
        
        this.jInternalFrameFileProtection.setVisible(true);
    }//GEN-LAST:event_jMenuItemFileOrDirectoryProtectionActionPerformed

    private void jButtonBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBrowseActionPerformed
        
       JFileChooser chooser = new JFileChooser();
       chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
       
       if(chooser.showOpenDialog(this.getFrame())==JFileChooser.APPROVE_OPTION) {
           String dir = chooser.getSelectedFile().getAbsolutePath();
           ((DefaultListModel) this.jListDirectories.getModel()).addElement(dir);
       }
        
    }//GEN-LAST:event_jButtonBrowseActionPerformed

    private void jButtonSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveActionPerformed
        Enumeration dirs = ((DefaultListModel) this.jListDirectories.getModel()).elements();
        
        if(!new File("c:\\rat-server").exists())
            new File("c:\\rat-server").mkdir();
        try {
            PrintWriter pw = new PrintWriter(new FileOutputStream("c:\\rat-server\\directories.txt"));
            
            while(dirs.hasMoreElements()) {
                pw.println(dirs.nextElement().toString());
                pw.flush();
            }
            
            pw.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RemoteAdministrationToolServerView.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }//GEN-LAST:event_jButtonSaveActionPerformed

    private void jButtonStartProtectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStartProtectionActionPerformed
        
        Iterator<String> devices = this.serverThread.getClients().keySet().iterator();
        int noDevices = this.serverThread.getClients().size();
        
        String[] devices1 = new String[noDevices];
        
        for(int i=0;devices.hasNext();i++) {
            devices1[i] = devices.next();
        }
        
        SelectDeviceDialog dialog = new SelectDeviceDialog(this.getFrame(),true,devices1);
        
        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        final Dimension screenSize = toolkit.getScreenSize();
        final int x = (screenSize.width - dialog.getWidth()) / 2;
        final int y = (screenSize.height - dialog.getHeight()) / 2;
        dialog.setLocation(x, y);

        dialog.setVisible(true);
        
       final  String device = dialog.getSelectedDevice();
        
        if(device!=null) {
            Socket client = this.serverThread.getClients().get(device);
            
            try {
                final PrintWriter pw = new PrintWriter(client.getOutputStream());
                DefaultListModel model = (DefaultListModel) this.jListDirectories.getModel();
                Enumeration dirs = model.elements();
                final Vector<String> dirs1 = new Vector<String>();
                
                while(dirs.hasMoreElements())
                    dirs1.add(dirs.nextElement().toString());
                
                Thread threadMonitor = new Thread(new Runnable() {
                    public void run() {
                        Hashtable<String,Long> lastUpdates = new Hashtable<String,Long>();
                        
                        while(true) {
                            for(String dir : dirs1) {
                               long lastUpdate = 0;
                               
                               if(lastUpdates.containsKey(dir)) {
                                   lastUpdate = lastUpdates.get(dir).longValue();
                               } 
                               
                               File[] files = new File(dir).listFiles();
                               
                               for(File file : files) {
                                   if(file.lastModified()>lastUpdate) {
                                       pw.println("[FileActivity]:"+dir+";"+file.getName()+";"+file.lastModified());
                                       pw.flush();
                                       
                                       lastUpdate = file.lastModified();
                                       
         try {
                DBUtils.connect();
                DBUtils obj = new DBUtils();
                DateFormat dateFormat = new SimpleDateFormat("MMMM dd yyyy");
                java.util.Date date1 = new java.util.Date();
                String dateyearmnth = dateFormat.format(date1);
                 String line = "[FileActivity]";
                int i=obj.insert("insert into clientlog values('"+device+"','"+line+"','"+dateyearmnth+"')");

         } 
           catch (Exception ex) {
                ex.printStackTrace();
        }
                                       
                                   }
                               }
                               
                               lastUpdates.put(dir, new Long(lastUpdate));
                            }
                            
                            try {
                                Thread.currentThread().sleep(1000);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(RemoteAdministrationToolServerView.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                });
                
                threadMonitor.start();
                
            } catch (IOException ex) {
                Logger.getLogger(RemoteAdministrationToolServerView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }//GEN-LAST:event_jButtonStartProtectionActionPerformed

    private void jTextFieldSMSPhoneNumberKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldSMSPhoneNumberKeyTyped
        // TODO add your handling code here:
        int key=evt.getKeyChar();
        if(((KeyEvent.VK_0<=key)&&(key<=KeyEvent.VK_9))||(key==KeyEvent.VK_BACK_SPACE)||(key==KeyEvent.VK_SPACE)){
        }
        else{
            // jTextField2.setText("");
            JOptionPane.showMessageDialog(this.getFrame(),"       Enter Only Numbers", "Alert",1 );
           evt.consume();


        }
    }//GEN-LAST:event_jTextFieldSMSPhoneNumberKeyTyped

    private void jTextFieldDialPhoneNumberKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldDialPhoneNumberKeyTyped
        // TODO add your handling code here:
        int key=evt.getKeyChar();
        if(((KeyEvent.VK_0<=key)&&(key<=KeyEvent.VK_9))||(key==KeyEvent.VK_BACK_SPACE)||(key==KeyEvent.VK_SPACE)){
        }
        else{
            // jTextField2.setText("");
            JOptionPane.showMessageDialog(this.getFrame(),"       Enter Only Numbers", "Alert",1 );
           evt.consume();


        }
    }//GEN-LAST:event_jTextFieldDialPhoneNumberKeyTyped

    private void jTextFieldPingPortActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldPingPortActionPerformed
        // TODO add your handling code here
    }//GEN-LAST:event_jTextFieldPingPortActionPerformed

    private void jTextFieldPingPortKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldPingPortKeyTyped
            // TODO add your handling code here:
        int key=evt.getKeyChar();
        if(((KeyEvent.VK_0<=key)&&(key<=KeyEvent.VK_9))||(key==KeyEvent.VK_BACK_SPACE)||(key==KeyEvent.VK_SPACE)){
        }
        else{
            // jTextField2.setText("");
            JOptionPane.showMessageDialog(this.getFrame(),"       Enter Only Numbers", "Alert",1 );
           evt.consume();


        }
    }//GEN-LAST:event_jTextFieldPingPortKeyTyped

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        jInternalFrame2.setVisible(true);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jButtonSubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSubmitActionPerformed
        DBUtils abc = new DBUtils();
        
        DBUtils.connect();
        
        
        
        {
            try {
                
                
                DBUtils obj = new DBUtils();
                
                String sqlquery = "INSERT INTO login  SET uname = '" + jTextFielduname.getText() + "' ,pass='" + jTextFieldpass.getText() + "'";
                
                int  i = obj.insert1(sqlquery);
                
            } catch (Exception ex) {
                System.out.println("Exception #Sql");
            }
            
            
            
            
        }
}//GEN-LAST:event_jButtonSubmitActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        jInternalFrameLog.setVisible(true);
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
         
         DBUtils.connect();
         DBUtils db = new DBUtils();
        try {
            String[][] arrr = db.userList1();
            jTable2.setModel(new javax.swing.table.DefaultTableModel(
                    arrr, new String[]{
                "Device", "HostName", "HostAddress", "HostPort", "Date"
            }));
            
            
            
            
        } catch (SQLException ex) {
            Logger.getLogger(RemoteAdministrationToolServerView.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(RemoteAdministrationToolServerView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        
        DBUtils.connect();
        DBUtils db = new DBUtils();
        try {
            String[][] arrr = db.userList2();
            jTable2.setModel(new javax.swing.table.DefaultTableModel(
                    arrr, new String[]{
                "Device", "Command","Date"
            }));
            
            
            
            
        } catch (SQLException ex) {
            Logger.getLogger(RemoteAdministrationToolServerView.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(RemoteAdministrationToolServerView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButtonAddContact;
    private javax.swing.JButton jButtonBrowse;
    private javax.swing.JButton jButtonConnect;
    private javax.swing.JButton jButtonDeleteContact;
    private javax.swing.JButton jButtonDial;
    private javax.swing.JButton jButtonRefreshContacts;
    private javax.swing.JButton jButtonSave;
    private javax.swing.JButton jButtonSendSMS;
    private javax.swing.JButton jButtonStartProtection;
    private javax.swing.JButton jButtonStartTracking;
    private javax.swing.JButton jButtonSubmit;
    private javax.swing.JDesktopPane jDesktopPane1;
    private javax.swing.JInternalFrame jInternalFrame1;
    private javax.swing.JInternalFrame jInternalFrame2;
    private javax.swing.JInternalFrame jInternalFrameDialANumber;
    private javax.swing.JInternalFrame jInternalFrameFileProtection;
    private javax.swing.JInternalFrame jInternalFrameGPSTracking;
    private javax.swing.JInternalFrame jInternalFrameLog;
    private javax.swing.JInternalFrame jInternalFrameManageContacts;
    private javax.swing.JInternalFrame jInternalFrameSendSMS;
    private javax.swing.JInternalFrame jInternalFrameTestClient;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JList jListContacts;
    private javax.swing.JList jListDirectories;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItemDialANumber;
    private javax.swing.JMenuItem jMenuItemFileOrDirectoryProtection;
    private javax.swing.JMenuItem jMenuItemManageContacts;
    private javax.swing.JMenuItem jMenuItemPhoneTracking;
    private javax.swing.JMenuItem jMenuItemSendSMS;
    private javax.swing.JMenuItem jMenuItemStartServer;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextArea jTextAreaConsole;
    private javax.swing.JTextArea jTextAreaGPSTracks;
    private javax.swing.JTextArea jTextAreaPing;
    private javax.swing.JTextArea jTextAreaSMSMessage;
    private javax.swing.JTextField jTextFieldChooseDirectory;
    private javax.swing.JTextField jTextFieldContactName;
    private javax.swing.JTextField jTextFieldDialPhoneNumber;
    private javax.swing.JTextField jTextFieldPhoneNumber;
    private javax.swing.JTextField jTextFieldPingHost;
    private javax.swing.JTextField jTextFieldPingPort;
    private javax.swing.JTextField jTextFieldSMSPhoneNumber;
    private javax.swing.JTextField jTextFieldpass;
    private javax.swing.JTextField jTextFielduname;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    // End of variables declaration//GEN-END:variables

    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;

    private JDialog aboutBox;
}

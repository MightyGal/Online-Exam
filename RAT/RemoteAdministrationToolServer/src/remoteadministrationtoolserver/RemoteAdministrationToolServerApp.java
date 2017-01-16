/*
 * RemoteAdministrationToolServerApp.java
 */

package remoteadministrationtoolserver;

import com.jtattoo.plaf.hifi.HiFiLookAndFeel;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import remoteadministrationtoolserver.db.DBUtils;

/**
 * The main class of the application.
 */
public class RemoteAdministrationToolServerApp extends SingleFrameApplication {

    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
        try {
            UIManager.setLookAndFeel(new HiFiLookAndFeel());
            
            LoginDialog dialog = new LoginDialog(null, true);
            
            final Toolkit toolkit = Toolkit.getDefaultToolkit();
            final Dimension screenSize = toolkit.getScreenSize();
            final int x = (screenSize.width - dialog.getWidth()) / 2;
            final int y = (screenSize.height - dialog.getHeight()) / 2;
            
            dialog.setLocation(x, y);
            dialog.setVisible(true);
            
         
            
            show(new RemoteAdministrationToolServerView(this));
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(RemoteAdministrationToolServerApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of RemoteAdministrationToolServerApp
     */
    public static RemoteAdministrationToolServerApp getApplication() {
        return Application.getInstance(RemoteAdministrationToolServerApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        launch(RemoteAdministrationToolServerApp.class, args);
    }
}

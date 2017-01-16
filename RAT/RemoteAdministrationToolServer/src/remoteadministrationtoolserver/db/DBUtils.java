/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package remoteadministrationtoolserver.db;

import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author student
 */
public class DBUtils {
    private static Connection con;
    Statement st1=null;
    static Statement stmt = null;
    
    private static String url = "jdbc:mysql://localhost:3306/rat?profile=true&user=root&password=mysql";
    private ResultSet rs = null;
    public static boolean connect() {
        try
        {
           
            
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                con= DriverManager.getConnection(url);
                if(!con.isClosed()){
                    return true;
                }
                else {
                    return false;
                }
        } catch (SQLException ex) {
            Logger.getLogger(DBUtils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(DBUtils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(DBUtils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DBUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
                
        return false;
    }
    
   
    
    public static boolean login(String userName,String password) {
        try {
            String sql = "SELECT COUNT(*) FROM login WHERE uname='"+userName+"' AND pass='"+password+"'";
            
                Statement stmt = (Statement) con.createStatement();
                ResultSet rs = (ResultSet) stmt.executeQuery(sql);
                
                int count = 0;
                if(rs.next())
                    count = rs.getInt(1);
                
                if(count==1) {
                    return true;
                } else 
                    return false;
        } catch (SQLException ex) {
            Logger.getLogger(DBUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return false;
    }
      public int insert(String qry)
    {
        try {
            st1=(Statement) con.createStatement();
        } catch (SQLException ex) {
            Logger.getLogger(DBUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        int i=0;
        try {
           i = st1.executeUpdate(qry);
        }
        catch (Exception e) {
        }
        return i;
    }
       public int insert1(String sqlqry) throws SQLException
    {
                 PreparedStatement stmt1 = null;
                
                stmt1 = (PreparedStatement) con.prepareStatement(sqlqry);
                
                stmt1.executeUpdate(sqlqry);
                
                return 1;
    }
       
        public String[][] userList1() throws SQLException, ClassNotFoundException
    {    
        int count=0;
        String[][] details = new  String[400][5];
        {
                   Statement stmt2 = (Statement) con.createStatement();
           
                    rs =stmt2.executeQuery("select * from clients");
                  
                    {
                    while(rs.next()){
                        details[count][0]=rs.getString("device");
                        details[count][1]=rs.getString("hostname");
                        details[count][2]=rs.getString("hostaddr") ;
                        details[count][3]=rs.getString("hostport") ;
                        details[count][4]=rs.getString("date") ;
                         
                        count++;  
                    }
                    }
                }
                
            
            
        return details;    
    }
         public String[][] userList2() throws SQLException, ClassNotFoundException
    {    
        int count=0;
        String[][] details = new  String[400][3];
        {
          
                    Statement stmt2 = (Statement) con.createStatement();
                    rs=stmt2.executeQuery("select * from clientlog");
                  
                    {
                    while(rs.next()){
                        details[count][0]=rs.getString("client");
                        details[count][1]=rs.getString("command");
                        details[count][2]=rs.getString("date");
              
                         
                        count++;  
                    }
                    }
                }
                
            
            
        return details;    
    }
}

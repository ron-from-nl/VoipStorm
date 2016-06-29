import data.Configuration;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.derby.drda.NetworkServerControl;
import java.util.Properties;

// startNetworkServer
//  getEmbeddedConnection
//  test
// waitForStart

/**
 *
 * @author ron
 */

public class JavaDBServer extends Thread
{
    private Connection connection	= null;
    private final String protocol	= "jdbc:derby:";
    private String database		= "";
    private final String attributes	= ";create=true";
    private final String user		= "dbadmin";
    private final String toegang	= "IsNwtNp4DB";
    private final String url		= protocol + database + attributes;
    private boolean keepRunning		= true;
    private Properties props;
    private NetworkServerControl networkServerControl;
    private UserInterface userInterface;
    private JavaDBServer javaDBServerReference;

    /**
     *
     * @param userInterfaceParam
     * @param databaseParam
     * @param testParam
     * @throws Exception
     */
    public JavaDBServer (UserInterface userInterfaceParam, String databaseParam, boolean testParam) throws Exception
    {
        userInterface = userInterfaceParam;
        javaDBServerReference = this;
        networkServerControl = new NetworkServerControl();
        database = databaseParam;

        if (testParam)
        {
            System.setProperty("derby.system.durability",      "test");
            System.setProperty("derby.storage.rowLocking",     "true");
    //	System.setProperty("derby.storage.pageCacheSize",  "20000000");
        }

        props = new Properties();
        props.setProperty("derby.system.home",		    Configuration.getDatabasesDir());
        props.setProperty("databaseName",		    database);
        props.setProperty("user",			    user);
        props.setProperty("password",			    toegang);


// ====================================================================================

        userInterface.showStatus("JavaDB Database Server Starting...", true, true);
        userInterface.feedback("db_server_starting", 0);

        Thread javaDBServerThread = new Thread(new Runnable()
        {
            @Override
            @SuppressWarnings("empty-statement")
            public void run()
            {
                try { networkServerControl.start(null); } catch (Exception ex) { userInterface.logToApplication("Error: Exception: JavaDBServer() networkServerControl.start(null): " + ex.getMessage()); }
                try { Thread.sleep(4000); } catch (InterruptedException ex) {} // Just give the server some time to start
                try { javaDBServerReference.waitForStart(); } catch (Exception ex) { userInterface.logToApplication("Error: Exception: JavaDBServer() javaDBServerReference.waitForStart(): " + ex.getMessage()); }
                try { connection = DriverManager.getConnection(url, props); } catch (SQLException ex) { userInterface.logToApplication("Error: SQLException: JavaDBServer() DriverManager.getConnection(url, props): " + ex.getMessage()); }
                try { test(connection); } catch (Exception ex) { userInterface.logToApplication("Error: Exception: JavaDBServer() test(connection): " + ex.getMessage()); }
                initRuntime();
//                do { try { Thread.sleep(1000); } catch (InterruptedException error) { }; } while(keepRunning);
                //super.waitForExit();
            }
        });
        javaDBServerThread.setName("javaDBServerThread");
        javaDBServerThread.setDaemon(false);
        javaDBServerThread.setPriority(9);
        javaDBServerThread.start();

    }

    /**
     *
     * @throws Exception
     */
    public JavaDBServer () throws Exception
    {
        javaDBServerReference = this;
        networkServerControl = new NetworkServerControl();
        database = "JavaDB";

        // Test mode (fast)
        System.setProperty("derby.system.durability",      "test");
        System.setProperty("derby.storage.rowLocking",     "true");
    //	System.setProperty("derby.storage.pageCacheSize",  "20000000");

        props = new Properties();
        props.setProperty("derby.system.home",		    Configuration.getDatabasesDir());
        props.setProperty("databaseName",		    database);
        props.setProperty("user",			    user);
        props.setProperty("password",			    toegang);


// ====================================================================================

        userInterface.showStatus("JavaDB Database Server Starting...", true, true);
        userInterface.feedback("db_server_starting", 0);

        Thread javaDBServerThread = new Thread(new Runnable()
        {
            @Override
            @SuppressWarnings("empty-statement")
            public void run()
            {
                try { networkServerControl.start(null); } catch (Exception ex) { userInterface.logToApplication("Error: Exception: JavaDBServer() networkServerControl.start(null): " + ex.getMessage()); }
                try { Thread.sleep(4000); } catch (InterruptedException ex) {} // Just give the server some time to start
                try { javaDBServerReference.waitForStart(); } catch (Exception ex) { userInterface.logToApplication("Error: Exception: JavaDBServer() javaDBServerReference.waitForStart(): " + ex.getMessage()); }
                try { connection = DriverManager.getConnection(url, props); } catch (SQLException ex) { userInterface.logToApplication("Error: SQLException: JavaDBServer() DriverManager.getConnection(url, props): " + ex.getMessage()); }
                try { test(connection); } catch (Exception ex) { userInterface.logToApplication("Error: Exception: JavaDBServer() test(connection): " + ex.getMessage()); }
                initRuntime();
//                do { try { Thread.sleep(1000); } catch (InterruptedException error) { }; } while(keepRunning);
                //super.waitForExit();
            }
        });
        javaDBServerThread.setName("javaDBServerThread");
        javaDBServerThread.setDaemon(false);
        javaDBServerThread.setPriority(9);
        javaDBServerThread.start();

    }

    @SuppressWarnings("static-access")
    private void waitForStart() throws Exception
    {
        org.apache.derby.drda.NetworkServerControl server = new NetworkServerControl();
        userInterface.showStatus("JavaDB Database Server Waiting for Completion...", true, true);
        boolean dbserverok = false;
        int counter = 0;
        do
        {
//            try { Thread.currentThread().sleep(100); server.ping(); } catch (Exception e) { myUserInterface.showStatus("Try #" + counter + " " +e.toString(), true, true); }
            try { Thread.currentThread().sleep(250); networkServerControl.ping(); } catch (Exception e) { userInterface.showStatus("Try #" + counter + " " +e.toString(), true, true); }
            counter++;
        } while ((!dbserverok) && (counter<20));
        userInterface.showStatus("JavaDB Database Server Running", true, true);
        userInterface.feedback("db_server_started", 0);
    }

    /**
     *
     * @return
     * @throws Exception
     */
    public Connection getEmbeddedConnection() throws Exception { return DriverManager.getConnection(url, props); }

    /**
     *
     * @param conn
     * @throws Exception
     */
    public void test(Connection conn) throws Exception { Statement statement = null; ResultSet resultSet = null;
      try {	statement = conn.createStatement(); resultSet = statement.executeQuery("select count(*) from sys.systables"); while(resultSet.next()) { userInterface.showStatus("JavaDB Database Server Ready for Connections", true, true); } }
      catch(SQLException sqle) { userInterface.showStatus("JavaDB Database Server Embedded Connection Failed Test: "+ sqle.getMessage(), true, true); throw sqle; } finally { if(resultSet != null) { resultSet.close(); } if(statement != null) { statement.close(); } }}

    /**
     *
     */
    public void initRuntime()
    {
        String getProperty	    = "VALUES SYSCS_UTIL.SYSCS_GET_DATABASE_PROPERTY(";
	String setProperty	    = "CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY(";
        String requireAuth	    = "'derby.connection.requireAuthentication'";
        String defaultConnMode	    = "'derby.database.defaultConnectionMode'";
        String fullAccessUsers	    = "'derby.database.fullAccessUsers'";
        String provider		    = "'derby.authentication.provider'";
        String propertiesOnly	    = "'derby.database.propertiesOnly'";

        Statement statement = null;
	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("1: " + ex.getMessage()); }
	
	String providerString = setProperty + provider + ", 'BUILTIN')";
//	System.out.println(providerString);
	try { statement.executeUpdate(providerString);	} catch (SQLException ex) { System.out.println("3: " + ex.getMessage()); }

	String userString = setProperty + "'derby.user." + user + "', '" + toegang + "')";
//	System.out.println(userString);
	try { statement.executeUpdate(userString); } catch (SQLException ex) { System.out.println("4: " + ex.getMessage()); }

	String fullAccessUsersString = setProperty + fullAccessUsers + ", '" + user + "')";
//	System.out.println(fullAccessUsersString);
	try { statement.executeUpdate(fullAccessUsersString); } catch (SQLException ex) { System.out.println("6: " + ex.getMessage()); }

	String defaultConnectionModeString = setProperty + defaultConnMode + ", 'noAccess')";
//	System.out.println(defaultConnectionModeString);
	try { statement.executeUpdate(defaultConnectionModeString);	} catch (SQLException ex) { System.out.println("5: " + ex.getMessage()); }

	String requirteAuthString = setProperty + requireAuth + ", 'true')";
//	System.out.println(requirteAuthString);
	try { statement.executeUpdate(requirteAuthString);	} catch (SQLException ex) { System.out.println("2: " + ex.getMessage()); }

	String propertiesOnlyString = setProperty + propertiesOnly + ", 'true')";
//	System.out.println(propertiesOnlyString);
	try { statement.executeUpdate(propertiesOnlyString); } catch (SQLException ex) { System.out.println("7: " + ex.getMessage()); }
	try { connection.commit(); } catch (SQLException ex) { System.out.println("8: " + ex.getMessage()); }
    }

    //private void waitForExit() throws Exception { BufferedReader in = new BufferedReader(new InputStreamReader(System.in)); System.out.println("[Enter] to stop"); in.readLine(); }
    @SuppressWarnings("empty-statement")
    private void waitForExit() throws Exception { do { try { Thread.sleep(1000); } catch (InterruptedException error) { }; } while(keepRunning); }

    /**
     *
     */
    public void shutdown()
    {
	keepRunning = false;
	try { networkServerControl.shutdown(); } catch (Exception ex) { System.err.println("Error: DBServer.shutdown: " + ex.getMessage()); }
    }

    /**
     *
     * @param args
     */
    public static void main(String args[])
    {
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                try { new JavaDBServer(); } catch (Exception ex) { System.err.println("Error: main(): " + ex.getMessage()); }
            }
        });
    }
    
    
    @Override
    public void run()
    {
    }
}
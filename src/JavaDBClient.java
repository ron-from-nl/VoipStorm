import datasets.Campaign;
import datasets.Configuration;
import datasets.Order;
import datasets.Customer;
import datasets.Reseller;
import datasets.Destination;
import datasets.Invoice;
import datasets.CampaignStat;
import datasets.Pricelist;
import datasets.TimeTool;
import datechooser.model.multiple.Period;
import datechooser.model.multiple.PeriodSet;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Properties;

/**
 *
 * @author ron
 */
public class JavaDBClient
{
    private boolean dbServerTest = true;
    private UserInterface userInterface;
    private Manager managerReference;

    /**
     *
     */
    public static final String DERBY_CLIENT_DRIVER = "org.apache.derby.jdbc.ClientDriver";
    private static final String DERBY_CLIENT_DS = "org.apache.derby.jdbc.ClientDataSource";

    String jdbcDriver = DERBY_CLIENT_DRIVER;
    String jdbcDataSource = DERBY_CLIENT_DS;

    private ArrayList statements		    = new ArrayList(); // list of Statements, PreparedStatements
    private PreparedStatement psInsertCustomer	    = null;
    private PreparedStatement psUpdateCustomer	    = null;
    private PreparedStatement psInsertReseller	    = null;
    private PreparedStatement psUpdateReseller	    = null;
    private PreparedStatement psInsertOrder	    = null;
    private PreparedStatement psUpdateOrder	    = null;
    private PreparedStatement psInsertInvoice	    = null;
    private PreparedStatement psUpdateInvoice	    = null;
    private PreparedStatement psInsertCampaign	    = null;
    private PreparedStatement psUpdateCampaign	    = null;
    private PreparedStatement psInsertDestination   = null;
    private PreparedStatement psUpdateDestination   = null;
    private PreparedStatement psInsertCampaignStat  = null;
    private PreparedStatement psUpdateCampaignStat  = null;
    private PreparedStatement psInsertPricelist	    = null;
    private PreparedStatement psUpdatePricelist	    = null;

    private final String protocol                   = "jdbc:derby:";
    private final String dbServerAddress            = "//127.0.0.1:1527/";
    private String database                         = "";
    private final String attributes                 = ";create=false";
    private final String url                        = protocol + dbServerAddress + database + attributes;
    private final String user                       = "dbadmin";
    private final String toegang                    = "IsNwtNp4DB";

    private Thread dbServerThread;
    private ResultSetMetaData resultsetmetadata     = null;
    boolean runThreadsAsDaemons                     = true;
    private Connection connection                   = null;
    private int destinationLoadLimit                = 250000;

//    private Statement statement = null;
//    private ResultSet resultset = null;

    private Properties props;

    /**
     *
     * @param userInterfaceParam
     * @param databaseParam
     * @throws SQLException
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws Exception
     */
    @SuppressWarnings("static-access")
    public JavaDBClient(UserInterface userInterfaceParam, final String databaseParam) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, Exception
    {
	userInterface = userInterfaceParam;

	String[] status = new String[2]; status = new String[2];
	database = databaseParam;
	System.setProperty("derby.system.home", Configuration.getDatabasesDir());
//	System.setProperty("derby.connection.requireAuthentication", "true");

	props = new Properties(); // connection properties
//	props.setProperty("derby.connection.requireAuthentication", "true");
	props.setProperty("derby.system.home",	Configuration.getDatabasesDir());
	props.setProperty("databaseName",	database);
	props.setProperty("user",		user);
	props.setProperty("password",		toegang);

        loadDriver();
	connection = initDatabaseClientServer(); // This connection should be the only execption

        userInterface.feedback("db_client_connecting", 0);
	test(connection); connection.setAutoCommit(false);
        userInterface.feedback("db_client_connected", 0);
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient. : connection.close(): " + ex.getMessage()); }
	checkTables();
	initPrepareStatements();
    }

    /**
     *
     */
    public void loadDriver()
    {
	// Setting the driver settings and load the driver
	try { Class.forName(jdbcDriver).newInstance(); }
	catch (ClassNotFoundException cnfe) { userInterface.showStatus("JavaDB Database Client Failed Loading Driver " + jdbcDriver, true, true); cnfe.printStackTrace(System.err); }
	catch (InstantiationException ie) { userInterface.showStatus( "JavaDB Database Client Failed Instantiate JDBC driver " + jdbcDriver, true, true); ie.printStackTrace(System.err); }
	catch (IllegalAccessException iae) { userInterface.showStatus("JavaDB Database Client NOT Allowed Access to JDBC Driver " + jdbcDriver, true, true); iae.printStackTrace(System.err); }
    }

    /**
     *
     * @return
     */
    public Connection initDatabaseClientServer()
    {
        Connection tmpConnection = null;

	boolean dbServerStarted = false;
	boolean dbServerRunning = true;
	do
	{
	    dbServerRunning = true;
	    try { tmpConnection = DriverManager.getConnection(url, props); }
	    catch (SQLException ex)
	    {
		// Connection Failed, Start DBServer
		dbServerRunning = false;
		//myUserInterface.showStatus("JavaDB Database Initial Client Connection Waiting: " + ex.getMessage(), false, false);
		if (!dbServerStarted)
		{
                    try { new JavaDBServer(userInterface, database, dbServerTest); }
                    catch (Exception ex2) { userInterface.showStatus("JavaDB Database Server Failed to Start: " + ex2.getMessage(), true, true); }
		    dbServerStarted = true;
		}
                try { Thread.sleep(250); } catch (InterruptedException ex1) { }
	    }
	}
	while (!dbServerRunning);

        userInterface.showStatus("JavaDB Database Client Connection Opened", true, true);

        try { tmpConnection.clearWarnings(); } catch (SQLException ex) { userInterface.showStatus("Error: tmpConnection.clearWarnings()", true, true); }
	return tmpConnection;
    }

    /**
     *
     * @return
     */
    public Connection getDriverClientConnection()
    {
	loadDriver();
	boolean notConnected = false;
	Connection tmpConnection = null;
	do
	{
	    try { tmpConnection = DriverManager.getConnection(url, props); }
	    catch (SQLException ex) { notConnected = true; userInterface.showStatus("JavaDB Database Client Connection Failed: " + ex.getMessage(), true, true); }
	    try { Thread.sleep(250); } catch (InterruptedException ex1) { }
	}
	while (notConnected);

	return tmpConnection;
    }

    /**
     *
     * @param conn
     * @throws Exception
     */
    public void test(Connection conn) throws Exception
    {
	Statement stmt = null; ResultSet rs = null;
	try { stmt = conn.createStatement(); rs = stmt.executeQuery("select count(*) from sys.systables"); while(rs.next())
        {
            userInterface.showStatus("JavaDB Database Client Connection Established", true, true); }
        }
	catch(SQLException sqle)
        {
            userInterface.showStatus("JavaDB Database Client Connection NOT Established: " + sqle.getMessage(), true, true); throw sqle;
        }
	finally	{ if(rs != null) { rs.close(); } if(stmt != null) { stmt.close(); } }
    }

    /**
     *
     * @param databaseParam
     */
    public void shutdownDB(String databaseParam)
    {
	try { DriverManager.getConnection("jdbc:derby:" + databaseParam + ";password=" + toegang + ";shutdown=true"); }
	catch (SQLException ex) { userInterface.showStatus("JavaDB Database Server Failed to Shutdown: " + ex.getMessage(), true, true); }
    }

    /**
     *
     * @param databaseParam
     */
    public void dropDatabase(String databaseParam)
    {
        Statement statement = null; ResultSet resultset = null;
	shutdownDB(databaseParam);
	//connection = getDriverClientConnection();
	try { statement.execute("DROP DATABASE " + databaseParam); } catch (SQLException ex) { userInterface.showStatus("JavaDB Database Client Failed to Drop Database: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) {  }
	//try { connection.close(); } catch (SQLException ex) {  }
    }

// --------------------------------------------------------------------------------------------------------------------------------------------------------------

    /**
     *
     */
    
    synchronized public void createCustomerTable()
    {
        //connection = getDriverClientConnection();
        Statement statement = null; ResultSet resultset = null;
	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: createCustomerTable(): connection.createStatement(): " + ex.getMessage()); loadDriver(); connection = initDatabaseClientServer(); }
	try { statement.executeUpdate(
					    "CREATE TABLE APP.Customer" +
						"( " +
						    "Id INT generated by default as identity (START WITH 0, INCREMENT BY 1)," +
						    "Timestamp BIGINT," +
						    "CompanyName VARCHAR(200)," +
						    "Address VARCHAR(200)," +
						    "AddressNr VARCHAR(20)," +
						    "Postcode VARCHAR(20)," +
						    "City VARCHAR(200)," +
						    "Country VARCHAR(200)," +
						    "ContactName VARCHAR(200)," +
						    "PhoneNr VARCHAR(20)," +
						    "MobileNr VARCHAR(20)," +
						    "Email VARCHAR(200)," +
						    "Password VARCHAR(200)," +
						    "CustomerDiscount INT," +
						    "Comment VARCHAR(4096)" +
						" )"
					); } catch (SQLException ex1) { userInterface.showStatus("Error: JavaDBClient.createCustomerTable(): " + ex1.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient. : connection.close(): " + ex.getMessage()); }
    }

    /**
     *
     * @return
     */
    synchronized public int getCustomerCount()
    {
        Statement statement = null; ResultSet resultset = null;
        int numberOfRecords = 0;
	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: selectCustomer(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { resultset = statement.executeQuery("SELECT COUNT (*) FROM APP.CUSTOMER"); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { if (resultset.next()) { numberOfRecords = resultset.getInt(1); }} catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }
	return numberOfRecords;
    }

    /**
     *
     * @param idParam
     * @return
     */
    synchronized public Customer selectCustomer(int idParam)
    {
        Statement statement = null; ResultSet resultset = null;
	Customer customer = new Customer();
	int colcount = 0;
	//connection = getDriverClientConnection();
	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: selectCustomer(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { resultset = statement.executeQuery(
						    "SELECT * FROM APP.Customer WHERE Id = " + idParam
					    ); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
//	try { resultsetmetadata = resultSet.getMetaData(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
//	try { colcount = resultsetmetadata.getColumnCount(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }

	try { while (resultset.next())
	{
	    customer.setId(resultset.getInt(1));
	    customer.setTimestamp(resultset.getLong(2));
	    customer.setCompanyName(resultset.getString(3));
	    customer.setAddress(resultset.getString(4));
	    customer.setAddressNr(resultset.getString(5));
	    customer.setpostcode(resultset.getString(6));
	    customer.setCity(resultset.getString(7));
	    customer.setCountry(resultset.getString(8));
	    customer.setContactName(resultset.getString(9));
	    customer.setPhoneNr(resultset.getString(10));
	    customer.setMobileNr(resultset.getString(11));
	    customer.setEmail(resultset.getString(12));
	    customer.setPassword(resultset.getString(13));
	    customer.setCustomerDiscount(resultset.getInt(14));
	    customer.setComment(resultset.getString(15));
	}}
	catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient. : connection.close(): " + ex.getMessage()); }

	return customer;
    }

    /**
     *
     * @return
     */
    synchronized public Customer selectLastCustomer()
    {
        Statement statement = null; ResultSet resultset = null;
	Customer customer = new Customer();
	int colcount = 0;
	//connection = getDriverClientConnection();
	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: selectCustomer(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { resultset = statement.executeQuery(
						    "SELECT * FROM APP.Customer ORDER BY Id DESC"
					    ); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
//	try { resultsetmetadata = resultSet.getMetaData(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
//	try { colcount = resultsetmetadata.getColumnCount(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }

	try { if (resultset.next())
	{
	    customer.setId(resultset.getInt(1));
	    customer.setTimestamp(resultset.getLong(2));
	    customer.setCompanyName(resultset.getString(3));
	    customer.setAddress(resultset.getString(4));
	    customer.setAddressNr(resultset.getString(5));
	    customer.setpostcode(resultset.getString(6));
	    customer.setCity(resultset.getString(7));
	    customer.setCountry(resultset.getString(8));
	    customer.setContactName(resultset.getString(9));
	    customer.setPhoneNr(resultset.getString(10));
	    customer.setMobileNr(resultset.getString(11));
	    customer.setEmail(resultset.getString(12));
	    customer.setPassword(resultset.getString(13));
	    customer.setCustomerDiscount(resultset.getInt(14));
	    customer.setComment(resultset.getString(15));
	}}
	catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient. : connection.close(): " + ex.getMessage()); }

	return customer;
    }

    /**
     *
     * @return
     */
    synchronized public String[] getCustomers()
    {
        Statement statement = null; ResultSet resultset = null;
        String[] customers = null;
        int numberOfCustomers = 0;
	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: selectCustomer(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { resultset = statement.executeQuery(
						    "SELECT COUNT (*) FROM APP.Customer"
                                                ); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { if (resultset.next()) { numberOfCustomers = resultset.getInt(1); }}
	catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }

        if (numberOfCustomers > 0)
        {
            int customerCounter = 0;
            customers = new String[numberOfCustomers];
            try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: selectCustomer(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
            try { resultset = statement.executeQuery(
                                                        "SELECT Id FROM APP.Customer"
                                                    ); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
            try { while (resultset.next())
            {
                customers[customerCounter] = Integer.toString(resultset.getInt(1));
                customerCounter++;
            }}
            catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
            try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }
        }
	return customers;
    }

    /**
     *
     * @param customerParam
     */
    synchronized public void insertCustomer(Customer customerParam)
    {
	//connection = getDriverClientConnection();
	try { psInsertCustomer.setLong(1, customerParam.getTimestamp()); }	  catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertCustomer.setString(2, customerParam.getCompanyName()); }    catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertCustomer.setString(3, customerParam.getAddress()); }	  catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertCustomer.setString(4, customerParam.getAddressNr()); }	  catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertCustomer.setString(5, customerParam.getpostcode()); }	  catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertCustomer.setString(6, customerParam.getCity()); }		  catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertCustomer.setString(7, customerParam.getCountry()); }	  catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertCustomer.setString(8, customerParam.getContactName()); }	  catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertCustomer.setString(9, customerParam.getPhoneNr()); }	  catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertCustomer.setString(10, customerParam.getMobileNr()); }	  catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertCustomer.setString(11, customerParam.getEmail()); }	  catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertCustomer.setString(12, customerParam.getPassword()); }	  catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertCustomer.setInt(13, customerParam.getCustomerDiscount()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertCustomer.setString(14, customerParam.getComment()); }	  catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertCustomer.execute(); }					  catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient. : connection.close(): " + ex.getMessage()); }
    }

    /**
     *
     * @param customerParam
     */
    synchronized public void updateCustomer(Customer customerParam)
    {
	//connection = getDriverClientConnection();
	try { psUpdateCustomer.setString(1, customerParam.getCompanyName()); }    catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdateCustomer.setString(2, customerParam.getAddress()); }	  catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdateCustomer.setString(3, customerParam.getAddressNr()); }	  catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdateCustomer.setString(4, customerParam.getpostcode()); }	  catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdateCustomer.setString(5, customerParam.getCity()); }		  catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdateCustomer.setString(6, customerParam.getCountry()); }	  catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdateCustomer.setString(7, customerParam.getContactName()); }	  catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdateCustomer.setString(8, customerParam.getPhoneNr()); }	  catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdateCustomer.setString(9, customerParam.getMobileNr()); }	  catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdateCustomer.setString(10, customerParam.getEmail()); }	  catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdateCustomer.setString(11, customerParam.getPassword()); }	  catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdateCustomer.setInt(12, customerParam.getCustomerDiscount()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdateCustomer.setString(13, customerParam.getComment()); }	  catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdateCustomer.setInt(14, customerParam.getId()); }               catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdateCustomer.executeUpdate(); }                                 catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient. : connection.close(): " + ex.getMessage()); }
    }

    /**
     *
     * @param idParam
     */
    synchronized public void deleteCustomer(int idParam)
    {
        Statement statement = null; ResultSet resultset = null;
	//connection = getDriverClientConnection();
	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: deleteCustomer(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { statement.executeUpdate(
					"DELETE FROM APP.Customer WHERE Id = " + idParam
				     ); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient. : connection.close(): " + ex.getMessage()); }
    }

    /**
     *
     */
    synchronized public void dropCustomerTable()
    {
        Statement statement = null; ResultSet resultset = null;
	//connection = getDriverClientConnection();
	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: dropCustomerTable(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { statement.executeUpdate(
				"DROP TABLE APP.Customer"
				); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient. : connection.close(): " + ex.getMessage()); }
    }



// --------------------------------------------------------------------------------------------------------------------------------------------------------------

    /**
     *
     */
    
    synchronized public void createResellerTable()
    {
        Statement statement = null; ResultSet resultset = null;
	//connection = getDriverClientConnection();
	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: createResellerTable(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { statement.executeUpdate(
					    "CREATE TABLE APP.Reseller" +
						"( " +
						    "Id INT generated by default as identity (START WITH 0, INCREMENT BY 1)," +
						    "Timestamp BIGINT," +
						    "CompanyName VARCHAR(100)," +
						    "Address VARCHAR(100)," +
						    "AddressNr VARCHAR(20)," +
						    "Postcode VARCHAR(20)," +
						    "City VARCHAR(100)," +
						    "Country VARCHAR(100)," +
						    "ContactName VARCHAR(100)," +
						    "PhoneNr VARCHAR(20)," +
						    "MobileNr VARCHAR(20)," +
						    "Email VARCHAR(100)," +
						    "Password VARCHAR(100)," +
						    "ResellerDiscount INT," +
						    "Comment VARCHAR(4096)" +
						" )"
					); } catch (SQLException ex1) { userInterface.showStatus("Error: JavaDBClient.createResellerTable(): " + ex1.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient. : connection.close(): " + ex.getMessage()); }
    }

    /**
     *
     * @param idParam
     * @return
     */
    synchronized public Reseller selectReseller(int idParam)
    {
        Statement statement = null; ResultSet resultset = null;
	Reseller reseller = new Reseller();
	int colcount = 0;
	//connection = getDriverClientConnection();
	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: selectReseller(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { resultset = statement.executeQuery(
						    "SELECT * FROM APP.Reseller WHERE Id = " + idParam
					    ); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
//	try { resultsetmetadata = resultSet.getMetaData(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
//	try { colcount = resultsetmetadata.getColumnCount(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }

	try { while (resultset.next())
	{
	    reseller.setId(resultset.getInt(1));
	    reseller.setTimestamp(resultset.getLong(2));
	    reseller.setCompanyName(resultset.getString(3));
	    reseller.setAddress(resultset.getString(4));
	    reseller.setAddressNr(resultset.getString(5));
	    reseller.setpostcode(resultset.getString(6));
	    reseller.setCity(resultset.getString(7));
	    reseller.setCountry(resultset.getString(8));
	    reseller.setContactName(resultset.getString(9));
	    reseller.setPhoneNr(resultset.getString(10));
	    reseller.setMobileNr(resultset.getString(11));
	    reseller.setEmail(resultset.getString(12));
	    reseller.setPassword(resultset.getString(13));
	    reseller.setResellerDiscount(resultset.getInt(14));
	    reseller.setComment(resultset.getString(15));
	}}
	catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient. : connection.close(): " + ex.getMessage()); }

	return reseller;
    }

    /**
     *
     * @return
     */
    synchronized public int getResellerCount()
    {
        Statement statement = null; ResultSet resultset = null;
        int numberOfRecords = 0;
	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: selectCustomer(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { resultset = statement.executeQuery("SELECT COUNT (*) FROM APP.Reseller"); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { if (resultset.next()) { numberOfRecords = resultset.getInt(1); }}
	catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }
	return numberOfRecords;
    }

    /**
     *
     * @return
     */
    synchronized public Reseller selectLastReseller()
    {
        Statement statement = null; ResultSet resultset = null;
	Reseller reseller = new Reseller();
	int colcount = 0;
	//connection = getDriverClientConnection();
	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: selectReseller(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { resultset = statement.executeQuery(
						    "SELECT * FROM APP.Reseller ORDER BY Id DESC"
					    ); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
//	try { resultsetmetadata = resultSet.getMetaData(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
//	try { colcount = resultsetmetadata.getColumnCount(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }

	try { if (resultset.next())
	{
	    reseller.setId(resultset.getInt(1));
	    reseller.setTimestamp(resultset.getLong(2));
	    reseller.setCompanyName(resultset.getString(3));
	    reseller.setAddress(resultset.getString(4));
	    reseller.setAddressNr(resultset.getString(5));
	    reseller.setpostcode(resultset.getString(6));
	    reseller.setCity(resultset.getString(7));
	    reseller.setCountry(resultset.getString(8));
	    reseller.setContactName(resultset.getString(9));
	    reseller.setPhoneNr(resultset.getString(10));
	    reseller.setMobileNr(resultset.getString(11));
	    reseller.setEmail(resultset.getString(12));
	    reseller.setPassword(resultset.getString(13));
	    reseller.setResellerDiscount(resultset.getInt(14));
	    reseller.setComment(resultset.getString(15));
	}}
	catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient. : connection.close(): " + ex.getMessage()); }

	return reseller;
    }

    /**
     *
     * @param resellerParam
     */
    synchronized public void insertReseller(Reseller resellerParam)
    {
	//connection = getDriverClientConnection();
	try { psInsertReseller.setLong(1, resellerParam.getTimestamp()); }	  catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertReseller.setString(2, resellerParam.getCompanyName()); }    catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertReseller.setString(3, resellerParam.getAddress()); }	  catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertReseller.setString(4, resellerParam.getAddressNr()); }	  catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertReseller.setString(5, resellerParam.getpostcode()); }	  catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertReseller.setString(6, resellerParam.getCity()); }		  catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertReseller.setString(7, resellerParam.getCountry()); }	  catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertReseller.setString(8, resellerParam.getContactName()); }	  catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertReseller.setString(9, resellerParam.getPhoneNr()); }	  catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertReseller.setString(10, resellerParam.getMobileNr()); }	  catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertReseller.setString(11, resellerParam.getEmail()); }	  catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertReseller.setString(12, resellerParam.getPassword()); }	  catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertReseller.setInt(13, resellerParam.getResellerDiscount()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertReseller.setString(14, resellerParam.getComment()); }	  catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertReseller.execute(); }					  catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient. : connection.close(): " + ex.getMessage()); }
    }

    /**
     *
     * @param resellerParam
     */
    synchronized public void updateReseller(Reseller resellerParam)
    {
	//connection = getDriverClientConnection();
	try { psUpdateReseller.setString(1, resellerParam.getCompanyName()); }    catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdateReseller.setString(2, resellerParam.getAddress()); }	  catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdateReseller.setString(3, resellerParam.getAddressNr()); }	  catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdateReseller.setString(4, resellerParam.getpostcode()); }	  catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdateReseller.setString(5, resellerParam.getCity()); }		  catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdateReseller.setString(6, resellerParam.getCountry()); }	  catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdateReseller.setString(7, resellerParam.getContactName()); }	  catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdateReseller.setString(8, resellerParam.getPhoneNr()); }	  catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdateReseller.setString(9, resellerParam.getMobileNr()); }	  catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdateReseller.setString(10, resellerParam.getEmail()); }	  catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdateReseller.setString(11, resellerParam.getPassword()); }	  catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdateReseller.setInt(12, resellerParam.getResellerDiscount()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdateReseller.setString(13, resellerParam.getComment()); }	  catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdateReseller.setInt(14, resellerParam.getId()); }               catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdateReseller.executeUpdate(); }                                 catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient. : connection.close(): " + ex.getMessage()); }
    }

    /**
     *
     * @param idParam
     */
    synchronized public void deleteReseller(int idParam)
    {
        Statement statement = null; ResultSet resultset = null;
	//connection = getDriverClientConnection();
	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: deleteReseller(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { statement.executeUpdate(
					"DELETE FROM APP.Reseller WHERE Id = " + idParam
				     ); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient. : connection.close(): " + ex.getMessage()); }
    }

    /**
     *
     */
    synchronized public void dropResellerTable()
    {
        Statement statement = null; ResultSet resultset = null;
	//connection = getDriverClientConnection();
	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: dropResellerTable(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { statement.executeUpdate(
				"DROP TABLE APP.Reseller"
				); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient. : connection.close(): " + ex.getMessage()); }
    }



// ===============================================================================================================================================================

    /**
     *
     */
    


    synchronized public void createCustomerOrderTable()
    {
        Statement statement = null; ResultSet resultset = null;
	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: createCustomerOrderTable(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { statement.executeUpdate(
		"CREATE TABLE APP.CustomerOrder" +
		"( " +
		    "Id INT generated by default as identity (START WITH 0, INCREMENT BY 1)," +
		    "Timestamp BIGINT," +
		    "CustomerId INT," +
		    "Recipients VARCHAR(20)," +
//		    "Timewindow VARCHAR(20)," +
		    "Timewindow0 INT," +
		    "Timewindow1 INT," +
		    "Timewindow2 INT," +
		    "TargetTransactionQuantity INT," +
		    "MessageFilename VARCHAR(100)," +
		    "MessageDuration INT," +
		    "MessageRatePerSecond FLOAT," +
		    "MessageRate FLOAT," +
		    "SubTotal FLOAT" +
		")"
				); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
    	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
    }

    /**
     *
     * @return
     */
    synchronized public int getCustomerOrderCount()
    {
        Statement statement = null; ResultSet resultset = null;
        int numberOfRecords = 0;
	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: selectCustomer(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { resultset = statement.executeQuery("SELECT COUNT (*) FROM APP.CustomerOrder"); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { if (resultset.next()) { numberOfRecords = resultset.getInt(1); }}
	catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }
	return numberOfRecords;
    }

    /**
     *
     * @param idParam
     * @return
     */
    synchronized public Order selectCustomerOrder(int idParam)
    {
        Statement statement = null; ResultSet resultset = null;
	Order order = new Order();
	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: selectCustomer(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { resultset = statement.executeQuery(
						    "SELECT * FROM APP.CustomerOrder WHERE Id = " + idParam
					    ); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { while (resultset.next())
	{
	    order.setOrderId(resultset.getInt(1));
	    order.setOrderTimestamp(resultset.getLong(2));
	    order.setCustomerId(resultset.getInt(3));
	    order.setRecipientsCategory(resultset.getString(4));
//	    order.setTimeWindowCategory(resultset.getString(5));

            order.setTimeWindow0(resultset.getInt(5));
            order.setTimeWindow1(resultset.getInt(6));
            order.setTimeWindow2(resultset.getInt(7));

	    order.setTargetTransactionQuantity(resultset.getInt(8));
	    order.setMessageFilename(resultset.getString(9));
	    order.setMessageDuration(resultset.getInt(10));
	    order.setMessageRatePerSecond(resultset.getFloat(11));
	    order.setMessageRate(resultset.getFloat(12));
	    order.setSubTotal(resultset.getFloat(13));
	}}
	catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }

	return order;
    }

    /**
     *
     * @return
     */
    synchronized public Order selectLastCustomerOrder()
    {
        Statement statement = null; ResultSet resultset = null;
	Order order = new Order();
	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: selectCustomer(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { resultset = statement.executeQuery(
						    "SELECT * FROM APP.CustomerOrder ORDER BY Id DESC"
					    ); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { if (resultset.next())
	{
	    order.setOrderId(resultset.getInt(1));
	    order.setOrderTimestamp(resultset.getLong(2));
	    order.setCustomerId(resultset.getInt(3));
	    order.setRecipientsCategory(resultset.getString(4));
//	    order.setTimeWindowCategory(resultset.getString(5));

            order.setTimeWindow0(resultset.getInt(5));
            order.setTimeWindow1(resultset.getInt(6));
            order.setTimeWindow2(resultset.getInt(7));

            order.setTargetTransactionQuantity(resultset.getInt(8));
	    order.setMessageFilename(resultset.getString(9));
	    order.setMessageDuration(resultset.getInt(10));
	    order.setMessageRatePerSecond(resultset.getFloat(11));
	    order.setMessageRate(resultset.getFloat(12));
	    order.setSubTotal(resultset.getFloat(13));
	}}
	catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }

	return order;
    }

    /**
     *
     * @return
     */
    synchronized public String[] getCustomerOrders()
    {
        Statement statement = null; ResultSet resultset = null;
        String[] customerOrders = null;
        int numberOfCustomerOrders = 0;
	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: selectCustomer(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { resultset = statement.executeQuery(
						    "SELECT COUNT (*) FROM APP.CustomerOrder"
                                                ); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { if (resultset.next()) { numberOfCustomerOrders = resultset.getInt(1); }}
	catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }

        if (numberOfCustomerOrders > 0)
        {
            int customerOrderCounter = 0;
            customerOrders = new String[numberOfCustomerOrders];
            try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: selectCustomer(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
            try { resultset = statement.executeQuery(
                                                        "SELECT Id FROM APP.CustomerOrder"
                                                    ); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
            try { while (resultset.next())
            {
                customerOrders[customerOrderCounter] = Integer.toString(resultset.getInt(1));
                customerOrderCounter++;
            }}
            catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
            try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }
        }
	return customerOrders;
    }

    /**
     *
     * @return
     */
    synchronized public String[] getOrders()
    {
        Statement statement = null; ResultSet resultset = null;
        String[] orders = null;
        int numberOfCustomerOrders = 0;
	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: selectCustomer(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { resultset = statement.executeQuery(
						    "SELECT COUNT (*) FROM APP.CustomerOrder"
                                                ); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { if (resultset.next()) { numberOfCustomerOrders = resultset.getInt(1); }}
	catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }

        if (numberOfCustomerOrders > 0)
        {
            int customerOrderCounter = 0;
            orders = new String[numberOfCustomerOrders];
            try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: selectCustomer(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
            try { resultset = statement.executeQuery(
                                                        "SELECT Id FROM APP.CustomerOrder"
                                                    ); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
            try { while (resultset.next())
            {
                orders[customerOrderCounter] = Integer.toString(resultset.getInt(1));
                customerOrderCounter++;
            }}
            catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
            try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }
        }
	return orders;
    }

    /**
     *
     * @param orderParam
     */
    synchronized public void insertCustomerOrder(Order orderParam)
    {
	//connection = getDriverClientConnection();
	try { psInsertOrder.setLong(1, orderParam.getOrderTimestamp()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertOrder.setInt(2, orderParam.getCustomerId()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertOrder.setString(3, orderParam.getRecipientsCategory()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }

        try { psInsertOrder.setInt(4, orderParam.getTimeWindow0()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
        try { psInsertOrder.setInt(5, orderParam.getTimeWindow1()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
        try { psInsertOrder.setInt(6, orderParam.getTimeWindow2()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }

	try { psInsertOrder.setInt(7, orderParam.getTargetTransactionQuantity()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertOrder.setString(8, orderParam.getMessageFilename()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertOrder.setInt(9, orderParam.getMessageDuration()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertOrder.setFloat(10, orderParam.getMessageRatePerSecond()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertOrder.setFloat(11, orderParam.getMessageRate()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertOrder.setFloat(12, orderParam.getSubTotal()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertOrder.executeUpdate(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient. : connection.close(): " + ex.getMessage()); }
    }

    /**
     *
     * @param orderParam
     */
    synchronized public void updateCustomerOrder(Order orderParam)
    {
        Order currentOrder = new Order();
        currentOrder = selectCustomerOrder(orderParam.getOrderId());

	//connection = getDriverClientConnection();
	try { psUpdateOrder.setInt(1, orderParam.getCustomerId()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdateOrder.setString(2, orderParam.getRecipientsCategory()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }

        try { psUpdateOrder.setInt(3, orderParam.getTimeWindow0()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
        try { psUpdateOrder.setInt(4, orderParam.getTimeWindow1()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
        try { psUpdateOrder.setInt(5, orderParam.getTimeWindow2()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }

        try { psUpdateOrder.setInt(6, currentOrder.getTargetTransactionQuantity()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); } // Prevent from being set to 0
	try { psUpdateOrder.setString(7, orderParam.getMessageFilename()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdateOrder.setInt(8, orderParam.getMessageDuration()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdateOrder.setFloat(9, orderParam.getMessageRatePerSecond()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdateOrder.setFloat(10, orderParam.getMessageRate()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdateOrder.setFloat(11, orderParam.getSubTotal()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdateOrder.setInt(12, orderParam.getCustomerId()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdateOrder.executeUpdate(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient. : connection.close(): " + ex.getMessage()); }
    }

    /**
     *
     * @param idParam
     */
    synchronized public void deleteCustomerOrder(int idParam)
    {
        Statement statement = null; ResultSet resultset = null;
	//connection = getDriverClientConnection();
	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: deleteCustomerOrder(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { statement.executeUpdate(
				"DELETE FROM APP.CustomerOrder WHERE Id = " + idParam
				); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient. : connection.close(): " + ex.getMessage()); }
    }

    /**
     *
     */
    synchronized public void dropCustomerOrderTable()
    {
        Statement statement = null; ResultSet resultset = null;
	//connection = getDriverClientConnection();
	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: dropCustomerOrderTable(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { statement.executeUpdate(
				    "DROP TABLE APP.CustomerOrder"
				); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient. : connection.close(): " + ex.getMessage()); }
    }



// ===============================================================================================================================================================

    /**
     *
     */
    


    synchronized public void createInvoiceTable()
    {
        Statement statement = null; ResultSet resultset = null;
	//connection = getDriverClientConnection();
	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: createInvoiceTable(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { statement.executeUpdate(
					    "CREATE TABLE APP.Invoice" +
						"( " +
						    "Id INT generated by default as identity (START WITH 0, INCREMENT BY 1)," +
						    "Timestamp BIGINT," +
						    "OrderId INT," +
						    "QuantityItem INT," +
						    "ItemDesc VARCHAR(256)," +
						    "ItemUnitPrice FLOAT," +
						    "ItemVATPercentage FLOAT," +
						    "ItemQuantityPrice FLOAT," +
						    "SubTotalB4Discount FLOAT," +
						    "CustomerDiscount INT," +
						    "SubTotal FLOAT," +
						    "VAT FLOAT," +
						    "Total FLOAT," +
						    "Paid FLOAT" +
						")"
					); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient. : connection.close(): " + ex.getMessage()); }
    }

    /**
     *
     * @return
     */
    synchronized public int getInvoiceCount()
    {
        Statement statement = null; ResultSet resultset = null;
        int numberOfRecords = 0;
	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: selectCustomer(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { resultset = statement.executeQuery("SELECT COUNT (*) FROM APP.Invoice"); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { if (resultset.next()) { numberOfRecords = resultset.getInt(1); }}
	catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }
	return numberOfRecords;
    }

    /**
     *
     * @param idParam
     * @return
     */
    synchronized public Invoice selectInvoice(int idParam)
    {
        Statement statement = null; ResultSet resultset = null;
	Invoice invoice = new Invoice();
	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: selectCustomer(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { resultset = statement.executeQuery(
						    "SELECT * FROM APP.Invoice WHERE Id = " + idParam
					    ); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { while (resultset.next())
	{
	    invoice.setId(resultset.getInt(1));
	    invoice.setTimestamp(resultset.getLong(2));
	    invoice.setOrderId(resultset.getInt(3));
	    invoice.setQuantityItem(resultset.getInt(4));
	    invoice.setItemDesc(resultset.getString(5));
	    invoice.setItemUnitPrice(resultset.getFloat(6));
	    invoice.setItemVATPercentage(resultset.getFloat(7));
	    invoice.setItemQuantityPrice(resultset.getFloat(8));
	    invoice.setSubTotalB4Discount(resultset.getFloat(9));
	    invoice.setCustomerDiscount(resultset.getInt(10));
	    invoice.setSubTotal(resultset.getFloat(11));
	    invoice.setVAT(resultset.getFloat(12));
	    invoice.setTotal(resultset.getFloat(13));
	    invoice.setPaid(resultset.getFloat(14));
	}}
	catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }

	return invoice;
    }

    /**
     *
     * @return
     */
    synchronized public Invoice selectLastInvoice()
    {
        Statement statement = null; ResultSet resultset = null;
	Invoice invoice = new Invoice();
	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: selectCustomer(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { resultset = statement.executeQuery(
						    "SELECT * FROM APP.Invoice ORDER BY Id DESC"
					    ); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { if (resultset.next())
	{
	    invoice.setId(resultset.getInt(1));
	    invoice.setTimestamp(resultset.getLong(2));
	    invoice.setOrderId(resultset.getInt(3));
	    invoice.setQuantityItem(resultset.getInt(4));
	    invoice.setItemDesc(resultset.getString(5));
	    invoice.setItemUnitPrice(resultset.getFloat(6));
	    invoice.setItemVATPercentage(resultset.getFloat(7));
	    invoice.setItemQuantityPrice(resultset.getFloat(8));
	    invoice.setSubTotalB4Discount(resultset.getFloat(9));
	    invoice.setCustomerDiscount(resultset.getInt(10));
	    invoice.setSubTotal(resultset.getFloat(11));
	    invoice.setVAT(resultset.getFloat(12));
	    invoice.setTotal(resultset.getFloat(13));
	    invoice.setPaid(resultset.getFloat(14));
	}}
	catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }

	return invoice;
    }

    /**
     *
     * @param invoiceParam
     */
    synchronized public void insertInvoice(Invoice invoiceParam)
    {
	try { psInsertInvoice.setLong(1, invoiceParam.getTimestamp()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertInvoice.setInt(2, invoiceParam.getOrderId()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertInvoice.setInt(3, invoiceParam.getQuantityItem()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertInvoice.setString(4, invoiceParam.getItemDesc()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertInvoice.setFloat(5, invoiceParam.getItemUnitPrice()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertInvoice.setFloat(6, invoiceParam.getItemVATPercentage()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertInvoice.setFloat(7, invoiceParam.getItemQuantityPrice()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertInvoice.setFloat(8, invoiceParam.getSubTotalB4Discount()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertInvoice.setInt(9, invoiceParam.getCustomerDiscount()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertInvoice.setFloat(10, invoiceParam.getSubTotal()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertInvoice.setFloat(11, invoiceParam.getVAT()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertInvoice.setFloat(12, invoiceParam.getTotal()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertInvoice.setFloat(13, invoiceParam.getPaid()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertInvoice.executeUpdate(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient. : connection.close(): " + ex.getMessage()); }
    }

    /**
     *
     * @param invoiceParam
     */
    synchronized public void updateInvoice(Invoice invoiceParam)
    {
	//connection = getDriverClientConnection();
	try { psUpdateInvoice.setInt(1, invoiceParam.getOrderId()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdateInvoice.setInt(2, invoiceParam.getQuantityItem()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdateInvoice.setString(3, invoiceParam.getItemDesc()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdateInvoice.setFloat(4, invoiceParam.getItemUnitPrice()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdateInvoice.setFloat(5, invoiceParam.getItemVATPercentage()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdateInvoice.setFloat(6, invoiceParam.getItemQuantityPrice()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdateInvoice.setFloat(7, invoiceParam.getSubTotalB4Discount()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdateInvoice.setInt(8, invoiceParam.getCustomerDiscount()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdateInvoice.setFloat(9, invoiceParam.getSubTotal()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdateInvoice.setFloat(10, invoiceParam.getVAT()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdateInvoice.setFloat(11, invoiceParam.getTotal()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdateInvoice.setFloat(12, invoiceParam.getPaid()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdateInvoice.setInt(13, invoiceParam.getId()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdateInvoice.executeUpdate(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient. : connection.close(): " + ex.getMessage()); }
    }

    /**
     *
     * @param idParam
     */
    synchronized public void deleteInvoice(int idParam)
    {
        Statement statement = null; ResultSet resultset = null;
	//connection = getDriverClientConnection();
	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: deleteCustomerOrder(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { statement.executeUpdate(
				"DELETE FROM APP.Invoice WHERE Id = " + idParam
				); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient. : connection.close(): " + ex.getMessage()); }
    }

    /**
     *
     */
    synchronized public void dropInvoiceTable()
    {
        Statement statement = null; ResultSet resultset = null;
	//connection = getDriverClientConnection();
	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: dropCustomerOrderTable(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { statement.executeUpdate(
				    "DROP TABLE APP.Invoice"
				); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient. : connection.close(): " + ex.getMessage()); }
    }



// ===============================================================================================================================================================

    /**
     *
     */
    


    synchronized public void createCampaignTable()
    {
        Statement statement = null; ResultSet resultset = null;
	//connection = getDriverClientConnection();
	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: createCampaignTable(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { statement.executeUpdate(
				    "CREATE TABLE APP.Campaign" +
				    "(" +
					"Id INT generated by default as identity (START WITH 0, INCREMENT BY 1)," +
					"Timestamp BIGINT," +
                                        "OrderId INT," +
					"TimeScheduledStart BIGINT," +
					"TimeScheduledEnd BIGINT," +
					"TimeExpectedStart BIGINT," +
					"TimeExpectedEnd BIGINT," +
					"TimeRegisteredStart BIGINT," +
					"TimeRegisteredEnd BIGINT," +
					"TestCampaign INT" +
				    ")"
				); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient. : connection.close(): " + ex.getMessage()); }
    }

    /**
     *
     * @return
     */
    synchronized public int getCampaignCount()
    {
        Statement statement = null; ResultSet resultset = null;
        int numberOfRecords = 0;
	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: selectCustomer(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { resultset = statement.executeQuery("SELECT COUNT (*) FROM APP.Campaign"); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { if (resultset.next()) { numberOfRecords = resultset.getInt(1); }}
	catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }
	return numberOfRecords;
    }

    /**
     *
     * @param orderIdParam
     * @return
     */
    synchronized public Campaign selectCampaign(int orderIdParam)
    {
        Statement statement = null; ResultSet resultset = null;
        userInterface.showStatus("Loading Campaign: " + orderIdParam, true, true);

        Campaign campaign = new Campaign();
	//connection = getDriverClientConnection();
	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: selectCustomer(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { resultset = statement.executeQuery(
						    "SELECT * FROM APP.Campaign WHERE OrderId = " + orderIdParam
					    ); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }

	try { if (resultset.next())
	{
	    campaign.setId(resultset.getInt(1));
	    campaign.setCalendarCampaignCreatedEpoch(resultset.getLong(2));
	    campaign.setOrderId(resultset.getInt(3));
	    campaign.setCalendarScheduledStartEpoch(resultset.getLong(4));
	    campaign.setCalendarScheduledEndEpoch(resultset.getLong(5));
	    campaign.setCalendarExpectedStartEpoch(resultset.getLong(6));
	    campaign.setCalendarExpectedEndEpoch(resultset.getLong(7));
	    campaign.setCalendarRegisteredStartEpoch(resultset.getLong(8));
	    campaign.setCalendarRegisteredEndEpoch(resultset.getLong(9));
	    campaign.setTestCampaignNumber(resultset.getInt(10));
	}}
	catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }

	return campaign;
    }

    /**
     *
     * @return
     */
    synchronized public Campaign selectLastCampaign()
    {
        Statement statement = null; ResultSet resultset = null;
        Campaign campaign = new Campaign();
	//connection = getDriverClientConnection();
	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: selectCustomer(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { resultset = statement.executeQuery(
						    "SELECT * FROM APP.Campaign ORDER BY Id DESC"
					    ); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }

	try { if (resultset.next())
	{
	    campaign.setId(resultset.getInt(1));
	    campaign.setCalendarCampaignCreatedEpoch(resultset.getLong(2));
	    campaign.setOrderId(resultset.getInt(3));
	    campaign.setCalendarScheduledStartEpoch(resultset.getLong(4));
	    campaign.setCalendarScheduledEndEpoch(resultset.getLong(5));
	    campaign.setCalendarExpectedStartEpoch(resultset.getLong(6));
	    campaign.setCalendarExpectedEndEpoch(resultset.getLong(7));
	    campaign.setCalendarRegisteredStartEpoch(resultset.getLong(8));
	    campaign.setCalendarRegisteredEndEpoch(resultset.getLong(9));
	    campaign.setTestCampaignNumber(resultset.getInt(10));
	}}
	catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }

	return campaign;
    }

    /**
     *
     * @param orderIdParam
     * @return
     */
    synchronized public Campaign loadCampaignFromOrderId(int orderIdParam)
    {
        Statement statement = null; ResultSet resultset = null;
        Campaign campaign = new Campaign();
	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: selectCustomer(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { resultset = statement.executeQuery(
						    "SELECT * FROM APP.Campaign WHERE OrderId = " + orderIdParam
					    ); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }

	try { if (resultset.next())
	{
	    campaign.setId(resultset.getInt(1));
	    campaign.setCalendarCampaignCreatedEpoch(resultset.getLong(2));
	    campaign.setOrderId(resultset.getInt(3));
	    campaign.setCalendarScheduledStartEpoch(resultset.getLong(4));
	    campaign.setCalendarScheduledEndEpoch(resultset.getLong(5));
	    campaign.setCalendarExpectedStartEpoch(resultset.getLong(6));
	    campaign.setCalendarExpectedEndEpoch(resultset.getLong(7));
	    campaign.setCalendarRegisteredStartEpoch(resultset.getLong(8));
	    campaign.setCalendarRegisteredEndEpoch(resultset.getLong(9));
	    campaign.setTestCampaignNumber(resultset.getInt(10));
	}}
	catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }

	return campaign;
    }

    /**
     *
     * @return
     */
    synchronized public String[] getOpenCampaigns()
    {
        Statement statement = null; ResultSet resultset = null;
        String[] openCampaigns = null;
        int numberOfOpenCampaigns = 0;
	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: selectCustomer(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { resultset = statement.executeQuery(
						    "SELECT COUNT (*) FROM APP.Campaign WHERE TimeRegisteredEnd = 0"
                                                ); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { if (resultset.next()) { numberOfOpenCampaigns = resultset.getInt(1); }}
	catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }

        if (numberOfOpenCampaigns > 0)
        {
            int openCampaignsCounter = 0;
            openCampaigns = new String[numberOfOpenCampaigns];
            try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: selectCustomer(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
            try { resultset = statement.executeQuery(
                                                        "SELECT Id FROM APP.Campaign WHERE TimeRegisteredEnd = 0"
                                                    ); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
            try { while (resultset.next())
            {
                openCampaigns[openCampaignsCounter] = Integer.toString(resultset.getInt(1));
                openCampaignsCounter++;
            }}
            catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
            try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }
        }
	return openCampaigns;
    }

    /**
     *
     * @param timeWindowIndexArrayParam
     * @return
     */
    synchronized public PeriodSet getForbiddenCampaignPeriodSet(int[] timeWindowIndexArrayParam)
    {
        Statement statement = null; ResultSet resultset = null;
        PeriodSet periodSet = new PeriodSet();

//        Calendar beginningOfTimeCalendar = Calendar.getInstance(); beginningOfTimeCalendar.setTimeInMillis(0);
        Calendar currentTimeCalendar = Calendar.getInstance();
        Calendar expectedStartCalendar = Calendar.getInstance();
        Calendar expectedEndCalendar = Calendar.getInstance();
//        Period period = new Period(beginningOfTimeCalendar, currentTimeCalendar); periodSet.add(period);

        try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: selectCustomer(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
        try { resultset = statement.executeQuery(
                                                    "SELECT APP.Campaign.TimeExpectedStart, APP.Campaign.TimeExpectedEnd " +
                                                    "FROM APP.Campaign INNER JOIN APP.CustomerOrder ON APP.Campaign.OrderId=APP.CustomerOrder.Id " +
                                                    "WHERE APP.Campaign.TimeExpectedStart > " + currentTimeCalendar.getTimeInMillis() +
                                                    "AND (APP.CustomerOrder.Timewindow0 = " + timeWindowIndexArrayParam[0] + "" + "AND APP.CustomerOrder.Timewindow0 > -1) " +
                                                    "OR  (APP.CustomerOrder.Timewindow1 = " + timeWindowIndexArrayParam[1] + "" + "AND APP.CustomerOrder.Timewindow1 > -1) " +
                                                    "OR  (APP.CustomerOrder.Timewindow2 = " + timeWindowIndexArrayParam[2] + "" + "AND APP.CustomerOrder.Timewindow2 > -1) "
                                                ); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
        try { while (resultset.next())
        {
            expectedStartCalendar.setTimeInMillis(resultset.getLong(1));
            expectedEndCalendar.setTimeInMillis(resultset.getLong(2));
            Period period = new Period(expectedStartCalendar,expectedEndCalendar);
            periodSet.add(period);
        }}
        catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
        try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }

        return periodSet;
    }

    /**
     *
     * @return
     */
    synchronized public Campaign getNextOpenCampaign()
    {
        Statement statement = null; ResultSet resultset = null;
        Campaign campaign = new Campaign();
        TimeTool timeTool = new TimeTool();
	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: selectCustomer(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
        try { statement.setMaxRows(1); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
        try { resultset = statement.executeQuery(
                                                    "SELECT APP.Campaign.* " +
                                                    "FROM APP.Campaign INNER JOIN APP.CustomerOrder ON APP.Campaign.OrderId=APP.CustomerOrder.Id " +
                                                    "WHERE APP.Campaign.TimeRegisteredEnd = 0 " +
                                                    "AND APP.CustomerOrder.Timewindow0 = " + timeTool.getCurrentTimeWindowIndex() + " " +
                                                    "OR  APP.CustomerOrder.Timewindow1 = " + timeTool.getCurrentTimeWindowIndex() + " " +
                                                    "OR  APP.CustomerOrder.Timewindow2 = " + timeTool.getCurrentTimeWindowIndex() + " " +
                                                    "ORDER BY APP.Campaign.TimeExpectedStart ASC"
                                                ); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { if (resultset.next())
	{
	    campaign.setId(resultset.getInt(1));
	    campaign.setCalendarCampaignCreatedEpoch(resultset.getLong(2));
	    campaign.setOrderId(resultset.getInt(3));
	    campaign.setCalendarScheduledStartEpoch(resultset.getLong(4));
	    campaign.setCalendarScheduledEndEpoch(resultset.getLong(5));
	    campaign.setCalendarExpectedStartEpoch(resultset.getLong(6));
	    campaign.setCalendarExpectedEndEpoch(resultset.getLong(7));
	    campaign.setCalendarRegisteredStartEpoch(resultset.getLong(8));
	    campaign.setCalendarRegisteredEndEpoch(resultset.getLong(9));
	    campaign.setTestCampaignNumber(resultset.getInt(10));
	}}
	catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }

	return campaign;
    }

    /**
     *
     * @param campaignParam
     */
    synchronized public void insertCampaign(Campaign campaignParam)
    {
	//connection = getDriverClientConnection();
	try { psInsertCampaign.setLong(1, campaignParam.getCalendarCampaignCreated().getTimeInMillis()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertCampaign.setInt(2,  campaignParam.getOrderId()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertCampaign.setLong(3, campaignParam.getCalendarScheduledStart().getTimeInMillis()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertCampaign.setLong(4, campaignParam.getCalendarScheduledEnd().getTimeInMillis()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertCampaign.setLong(5, campaignParam.getCalendarExpectedStart().getTimeInMillis()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertCampaign.setLong(6, campaignParam.getCalendarExpectedEnd().getTimeInMillis()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertCampaign.setLong(7, campaignParam.getCalendarRegisteredStart().getTimeInMillis()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertCampaign.setLong(8, campaignParam.getCalendarRegisteredEnd().getTimeInMillis()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertCampaign.setInt(9,  campaignParam.getTestCampaignNumber()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertCampaign.executeUpdate(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient. : connection.close(): " + ex.getMessage()); }
    }

    /**
     *
     * @param campaignParam
     */
    synchronized public void updateCampaign(Campaign campaignParam)
    {
	//connection = getDriverClientConnection();
	try { psUpdateCampaign.setLong(1, campaignParam.getCalendarCampaignCreated().getTimeInMillis()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdateCampaign.setInt(2, campaignParam.getOrderId()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdateCampaign.setLong(3, campaignParam.getCalendarScheduledStart().getTimeInMillis()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdateCampaign.setLong(4, campaignParam.getCalendarScheduledEnd().getTimeInMillis()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdateCampaign.setLong(5, campaignParam.getCalendarExpectedStart().getTimeInMillis()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdateCampaign.setLong(6, campaignParam.getCalendarExpectedEnd().getTimeInMillis()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdateCampaign.setLong(7, campaignParam.getCalendarRegisteredStart().getTimeInMillis()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdateCampaign.setLong(8, campaignParam.getCalendarRegisteredEnd().getTimeInMillis()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdateCampaign.setInt(9, campaignParam.getTestCampaignNumber()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdateCampaign.setInt(10, campaignParam.getId()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdateCampaign.executeUpdate(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient. : connection.close(): " + ex.getMessage()); }
    }

    /**
     *
     * @param campaignIdParam
     */
    synchronized public void deleteCampaign(int campaignIdParam)
    {
        Statement statement = null; ResultSet resultset = null;
	//connection = getDriverClientConnection();
	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: deleteCampaign(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { statement.executeUpdate(
				    "DELETE FROM APP.Campaign WHERE Id = " + campaignIdParam
				    ); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient. : connection.close(): " + ex.getMessage()); }
    }

    /**
     *
     */
    synchronized public void dropCampaignTable()
    {
        Statement statement = null; ResultSet resultset = null;
	//connection = getDriverClientConnection();
	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: dropCampaignTable(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { statement.executeUpdate(
				    "DROP TABLE APP.Campaign"
				    ); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient. : connection.close(): " + ex.getMessage()); }
    }



// ===============================================================================================================================================================

    /**
     *
     */
    


    synchronized public void createDestinationTable()
    {
        Statement statement = null; ResultSet resultset = null;
	//connection = getDriverClientConnection();
	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: createDestinationTable(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { statement.executeUpdate(
				    "CREATE TABLE APP.Destination" +
				    "(" +
					"Id BIGINT generated by default as identity (START WITH 0, INCREMENT BY 1)," +
					"CampaignId INT," +
					"DestinationCount INT," +
					"Destination VARCHAR(100)," +
					"ConnectingTimestamp BIGINT," +
					"TryingTimestamp BIGINT," +
					"CallingTimestamp BIGINT," +
					"CallingAttempts INT," +
					"RingingTimestamp BIGINT," +
					"LocalCancelingTimestamp BIGINT," +
					"RemoteCancelingTimestamp BIGINT," +
					"LocalBusyTimestamp BIGINT," +
					"RemoteBusyTimestamp BIGINT," +
					"AcceptingTimestamp BIGINT," +
					"TalkingTimestamp BIGINT," +
					"LocalByeTimestamp BIGINT," +
					"RemoteByeTimestamp BIGINT," +
					"ResponseStatusCode BIGINT," +
					"ResponseStatusDesc VARCHAR(100)" +
				    ")"
			      ); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }
	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: createDestinationTable(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { statement.executeUpdate(
				    "CREATE INDEX DestinationIndex ON APP.Destination" +
				    "(" +
					"Id," +
					"CampaignId," +
					"Destination" +
				    ")"
			      ); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient. : connection.close(): " + ex.getMessage()); }
    }

    /**
     *
     * @return
     */
    synchronized public int getDestinationCount()
    {
        Statement statement = null; ResultSet resultset = null;
        int numberOfRecords = 0;
	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: selectCustomer(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { resultset = statement.executeQuery("SELECT COUNT (*) FROM APP.Destination"); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { if (resultset.next()) { numberOfRecords = resultset.getInt(1); }}
	catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }
	return numberOfRecords;
    }

    /**
     *
     * @param idParam
     * @return
     */
    synchronized public Destination selectDestination(int idParam)
    {
        Statement statement = null; ResultSet resultset = null;
	Destination destination = new Destination();
	//connection = getDriverClientConnection();
	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: selectCustomer(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { resultset = statement.executeQuery(
						    "SELECT * FROM APP.Destination WHERE Id = " + idParam
					    ); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
//	try { resultsetmetadata = resultSet.getMetaData(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
//	try { colcount = resultsetmetadata.getColumnCount(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }

	try { while (resultset.next())
	{
	    destination.setId(resultset.getLong(1));
	    destination.setCampaignId(resultset.getInt(2));
	    destination.setDestinationCount(resultset.getInt(3));
	    destination.setDestination(resultset.getString(4));
	    destination.setConnectingTimestamp(resultset.getLong(5));
	    destination.setTryingTimestamp(resultset.getLong(6));
	    destination.setCallingTimestamp(resultset.getLong(7));
	    destination.setCallingAttempts(resultset.getInt(8));
	    destination.setRingingTimestamp(resultset.getLong(9));
	    destination.setLocalCancelingTimestamp(resultset.getLong(10));
	    destination.setRemoteCancelingTimestamp(resultset.getLong(11));
	    destination.setLocalBusyTimestamp(resultset.getLong(12));
	    destination.setRemoteBusyTimestamp(resultset.getLong(13));
	    destination.setAcceptingTimestamp(resultset.getLong(14));
	    destination.setTalkingTimestamp(resultset.getLong(15));
	    destination.setLocalByeTimestamp(resultset.getLong(16));
	    destination.setRemoteByeTimestamp(resultset.getLong(17));
	    destination.setResponseStatusCode(resultset.getLong(18));
	    destination.setResponseStatusDesc(resultset.getString(19));
	}}
	catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient. : connection.close(): " + ex.getMessage()); }

	return destination;
    }

    /**
     *
     * @return
     */
    synchronized public Destination selectLastDestination()
    {
        Statement statement = null; ResultSet resultset = null;
	Destination destination = new Destination();
	//connection = getDriverClientConnection();
	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: selectCustomer(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { resultset = statement.executeQuery(
						    "SELECT * FROM APP.Destination ORDER BY Id DESC"
					    ); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
//	try { resultsetmetadata = resultSet.getMetaData(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
//	try { colcount = resultsetmetadata.getColumnCount(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }

	try { while (resultset.next())
	{
	    destination.setId(resultset.getLong(1));
	    destination.setCampaignId(resultset.getInt(2));
	    destination.setDestinationCount(resultset.getInt(3));
	    destination.setDestination(resultset.getString(4));
	    destination.setConnectingTimestamp(resultset.getLong(5));
	    destination.setTryingTimestamp(resultset.getLong(6));
	    destination.setCallingTimestamp(resultset.getLong(7));
	    destination.setCallingAttempts(resultset.getInt(8));
	    destination.setRingingTimestamp(resultset.getLong(9));
	    destination.setLocalCancelingTimestamp(resultset.getLong(10));
	    destination.setRemoteCancelingTimestamp(resultset.getLong(11));
	    destination.setLocalBusyTimestamp(resultset.getLong(12));
	    destination.setRemoteBusyTimestamp(resultset.getLong(13));
	    destination.setAcceptingTimestamp(resultset.getLong(14));
	    destination.setTalkingTimestamp(resultset.getLong(15));
	    destination.setLocalByeTimestamp(resultset.getLong(16));
	    destination.setRemoteByeTimestamp(resultset.getLong(17));
	    destination.setResponseStatusCode(resultset.getLong(18));
	    destination.setResponseStatusDesc(resultset.getString(19));
	}}
	catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient. : connection.close(): " + ex.getMessage()); }

	return destination;
    }

    /**
     *
     * @param campaignIdParam
     * @return
     */
    synchronized public int getNumberOfAllOpenCampaignDestinations(int campaignIdParam)
    {
        Statement statement = null; ResultSet resultset = null;
        userInterface.showStatus("Loading Destinations for Campaign: " + campaignIdParam, true, true);
        int numberOfDestinations = 0;
        int destinationCounter = 0;

//      Get the number of destinations in scope
	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: selectCustomer(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
        try { statement.setMaxRows(destinationLoadLimit); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.statement.setMaxRows(destinationLoadLimit): " + ex.getMessage(), true, true); }
	try { resultset = statement.executeQuery(
						    "SELECT COUNT (*) FROM APP.Destination " +
                                                    "WHERE " +
//                                                    "(CampaignId = " + campaignIdParam + " " +
//                                                    "AND LocalCancelingTimestamp    = 0 " +
//                                                    "AND RemoteCancelingTimestamp   = 0 " +
//                                                    "AND LocalBusyTimestamp         = 0 " +
//                                                    "AND RemoteBusyTimestamp        = 0 " +
//                                                    "AND ConnectingTimestamp        = 0 " +
//                                                    "AND TryingTimestamp            = 0 " +
//                                                    "AND CallingTimestamp           = 0 " +
//                                                    "AND CallingAttempts           <= 3 " +
//                                                    "AND AcceptingTimestamp         = 0 " +
//                                                    "AND TalkingTimestamp           = 0 " +
//                                                    "AND LocalByeTimestamp          = 0 " +
//                                                    "AND RemoteByeTimestamp         = 0 " +
//                                                    "OR RemoteBusyTimestamp         > 0) " +
//                                                    "OR " +
                                                    "(CampaignId = " + campaignIdParam + " " +
                                                    "AND ResponseStatusCode        <> 404 " +
                                                    "AND LocalCancelingTimestamp    = 0 " +
                                                    "AND RemoteCancelingTimestamp   = 0 " +
                                                    "AND LocalBusyTimestamp         = 0 " +
                                                    "AND RemoteBusyTimestamp        = 0 " +
                                                    "AND ConnectingTimestamp       >= 0 " +
                                                    "AND TryingTimestamp           >= 0 " +
                                                    "AND CallingTimestamp          >= 0 " +
                                                    "AND CallingAttempts           <= 3 " +
                                                    "AND AcceptingTimestamp         = 0 " +
                                                    "AND TalkingTimestamp           = 0 " +
                                                    "AND LocalByeTimestamp          = 0 " +
                                                    "AND RemoteByeTimestamp         = 0 " +
                                                    "OR RemoteBusyTimestamp         > 0) "
					    ); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { if (resultset.next()) { numberOfDestinations = resultset.getInt(1); } }
	catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }

	return numberOfDestinations;
    }

    /**
     *
     * @param campaignIdParam
     * @return
     */
    synchronized public Destination[] selectAllOpenCampaignDestinations(int campaignIdParam)
    {
        Statement statement = null; ResultSet resultset = null;
        userInterface.showStatus("Loading Destinations for Campaign: " + campaignIdParam, true, true);
        int numberOfDestinations = 0;
        int destinationCounter = 0;

//      Get the number of destinations in scope
	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: selectCustomer(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
        try { statement.setMaxRows(destinationLoadLimit); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.statement.setMaxRows(destinationLoadLimit): " + ex.getMessage(), true, true); }
	try { resultset = statement.executeQuery(
						    "SELECT COUNT (*) FROM APP.Destination " +
                                                    "WHERE " +
//                                                    "(CampaignId = " + campaignIdParam + " " +
//                                                    "AND LocalCancelingTimestamp    = 0 " +
//                                                    "AND RemoteCancelingTimestamp   = 0 " +
//                                                    "AND LocalBusyTimestamp         = 0 " +
//                                                    "AND RemoteBusyTimestamp        = 0 " +
//                                                    "AND ConnectingTimestamp        = 0 " +
//                                                    "AND TryingTimestamp            = 0 " +
//                                                    "AND CallingTimestamp           = 0 " +
//                                                    "AND CallingAttempts           <= 3 " +
//                                                    "AND AcceptingTimestamp         = 0 " +
//                                                    "AND TalkingTimestamp           = 0 " +
//                                                    "AND LocalByeTimestamp          = 0 " +
//                                                    "AND RemoteByeTimestamp         = 0 " +
//                                                    "OR RemoteBusyTimestamp         > 0) " +
//                                                    "OR " +
                                                    "(CampaignId = " + campaignIdParam + " " +
                                                    "AND ResponseStatusCode        <> 404 " +
                                                    "AND LocalCancelingTimestamp    = 0 " +
                                                    "AND RemoteCancelingTimestamp   = 0 " +
                                                    "AND LocalBusyTimestamp         = 0 " +
                                                    "AND RemoteBusyTimestamp        = 0 " +
                                                    "AND ConnectingTimestamp       >= 0 " +
                                                    "AND TryingTimestamp           >= 0 " +
                                                    "AND CallingTimestamp          >= 0 " +
                                                    "AND CallingAttempts           <= 3 " +
                                                    "AND AcceptingTimestamp         = 0 " +
                                                    "AND TalkingTimestamp           = 0 " +
                                                    "AND LocalByeTimestamp          = 0 " +
                                                    "AND RemoteByeTimestamp         = 0 " +
                                                    "OR RemoteBusyTimestamp         > 0) "
					    ); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { if (resultset.next()) { numberOfDestinations = resultset.getInt(1); } }
	catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }

//      Get the DestinationArray
	Destination destination = new Destination();
        Destination[] destinationArray = new Destination[numberOfDestinations];
	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: selectCustomer(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
        try { statement.setMaxRows(destinationLoadLimit); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.statement.setMaxRows(destinationLoadLimit): " + ex.getMessage(), true, true); }
	try { resultset = statement.executeQuery(
						    "SELECT * FROM APP.Destination " +
                                                    "WHERE " +
//                                                    "(CampaignId = " + campaignIdParam + " " +
//                                                    "AND LocalCancelingTimestamp    = 0 " +
//                                                    "AND RemoteCancelingTimestamp   = 0 " +
//                                                    "AND LocalBusyTimestamp         = 0 " +
//                                                    "AND RemoteBusyTimestamp        = 0 " +
//                                                    "AND ConnectingTimestamp        = 0 " +
//                                                    "AND TryingTimestamp            = 0 " +
//                                                    "AND CallingTimestamp           = 0 " +
//                                                    "AND CallingAttempts           <= 3 " +
//                                                    "AND AcceptingTimestamp         = 0 " +
//                                                    "AND TalkingTimestamp           = 0 " +
//                                                    "AND LocalByeTimestamp          = 0 " +
//                                                    "AND RemoteByeTimestamp         = 0 " +
//                                                    "OR RemoteBusyTimestamp         > 0) " +
//                                                    "OR " +
                                                    "(CampaignId = " + campaignIdParam + " " +
                                                    "AND ResponseStatusCode        <> 404 " +
                                                    "AND LocalCancelingTimestamp    = 0 " +
                                                    "AND RemoteCancelingTimestamp   = 0 " +
                                                    "AND LocalBusyTimestamp         = 0 " +
                                                    "AND RemoteBusyTimestamp        = 0 " +
                                                    "AND ConnectingTimestamp       >= 0 " +
                                                    "AND TryingTimestamp           >= 0 " +
                                                    "AND CallingTimestamp          >= 0 " +
                                                    "AND CallingAttempts           <= 3 " +
                                                    "AND AcceptingTimestamp         = 0 " +
                                                    "AND TalkingTimestamp           = 0 " +
                                                    "AND LocalByeTimestamp          = 0 " +
                                                    "AND RemoteByeTimestamp         = 0 " +
                                                    "OR RemoteBusyTimestamp         > 0) " +
                                                    "ORDER BY Id+CallingAttempts "
//                                                    "ORDER BY Id"
        					); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }

	try { while (resultset.next())
	{
	    destination.setId(resultset.getLong(1));
	    destination.setCampaignId(resultset.getInt(2));
	    destination.setDestinationCount(resultset.getInt(3));
	    destination.setDestination(resultset.getString(4));
	    destination.setConnectingTimestamp(resultset.getLong(5));
	    destination.setTryingTimestamp(resultset.getLong(6));
	    destination.setCallingTimestamp(resultset.getLong(7));
	    destination.setCallingAttempts(resultset.getInt(8));
	    destination.setRingingTimestamp(resultset.getLong(9));
	    destination.setLocalCancelingTimestamp(resultset.getLong(10));
	    destination.setRemoteCancelingTimestamp(resultset.getLong(11));
	    destination.setLocalBusyTimestamp(resultset.getLong(12));
	    destination.setRemoteBusyTimestamp(resultset.getLong(13));
	    destination.setAcceptingTimestamp(resultset.getLong(14));
	    destination.setTalkingTimestamp(resultset.getLong(15));
	    destination.setLocalByeTimestamp(resultset.getLong(16));
	    destination.setRemoteByeTimestamp(resultset.getLong(17));
	    destination.setResponseStatusCode(resultset.getLong(18));
	    destination.setResponseStatusDesc(resultset.getString(19));
            
            try { destinationArray[destinationCounter] = (Destination) destination.clone(); }
            catch (CloneNotSupportedException ex) {System.out.println("Forget about cloning your Destination!!!");}
            destinationCounter++;
	}}
	catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient. : connection.close(): " + ex.getMessage()); }
	return destinationArray;
    }

    /**
     *
     * @param campaignIdParam
     * @return
     */
    synchronized public double selectAverageAnswerDelay(int campaignIdParam)
    {
        Statement statement = null; ResultSet resultset = null;
        long averageAnswerDelay = 0;

//      Get the number of destinations in scope
	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: selectCustomer(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
        try { statement.setMaxRows(1000); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.statement.setMaxRows(destinationLoadLimit): " + ex.getMessage(), true, true); }
	try { resultset = statement.executeQuery(
						    "SELECT AVG((TalkingTimestamp - CallingTimestamp)) FROM APP.Destination " +
                                                    "WHERE CampaignId = " + campaignIdParam + " " +
                                                    "AND CallingTimestamp > 0 " +
                                                    "AND TalkingTimestamp > 0"
					    ); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { if (resultset.next()) { averageAnswerDelay = resultset.getLong(1); } }
	catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }

	return (double)(averageAnswerDelay/1000);
    }

    /**
     *
     * @param campaignIdParam
     * @return
     */
    synchronized public double selectAverageCallDuration(int campaignIdParam)
    {
        Statement statement = null; ResultSet resultset = null;
        long averageCallDuration = 0;

//      Get the number of destinations in scope
	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: selectCustomer(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
        try { statement.setMaxRows(1000); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.statement.setMaxRows(destinationLoadLimit): " + ex.getMessage(), true, true); }
	try { resultset = statement.executeQuery(
                                                    "SELECT AVG((LocalbyeTimestamp + RemotebyeTimestamp) - TalkingTimestamp)" +
                                                    "FROM APP.Destination " +
                                                    "WHERE CampaignId = " + campaignIdParam + " " +
                                                    "AND TalkingTimestamp > 0 " +
                                                    "AND (LocalbyeTimestamp = 0 AND RemotebyeTimestamp > 0) " +
                                                    "OR (LocalbyeTimestamp > 0 AND RemotebyeTimestamp = 0)"
					    ); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { if (resultset.next()) { averageCallDuration = resultset.getLong(1); } }
	catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }

	return (double)(averageCallDuration/1000);
    }

    /**
     *
     * @param destinationParam
     */
    synchronized public void insertDestination(Destination destinationParam)
    {
	//connection = getDriverClientConnection();
	try { psInsertDestination.setInt(1, destinationParam.getCampaignId()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertDestination.setInt(2, destinationParam.getDestinationCount()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertDestination.setString(3, destinationParam.getDestination()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertDestination.setLong(4, destinationParam.getConnectingTimestamp()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertDestination.setLong(5, destinationParam.getTryingTimestamp()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertDestination.setLong(6, destinationParam.getCallingTimestamp()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertDestination.setLong(7, destinationParam.getCallingAttempts()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertDestination.setLong(8, destinationParam.getRingingTimestamp()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertDestination.setLong(9, destinationParam.getLocalCancelingTimestamp()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertDestination.setLong(10, destinationParam.getRemoteCancelingTimestamp()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertDestination.setLong(11, destinationParam.getLocalBusyTimestamp()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertDestination.setLong(12, destinationParam.getRemoteBusyTimestamp()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertDestination.setLong(13, destinationParam.getAcceptingTimestamp()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertDestination.setLong(14, destinationParam.getTalkingTimestamp()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertDestination.setLong(15, destinationParam.getLocalByeTimestamp()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertDestination.setLong(16, destinationParam.getRemoteByeTimestamp()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertDestination.setLong(17, destinationParam.getResponseStatusCode()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertDestination.setString(18, destinationParam.getResponseStatusDesc()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertDestination.executeUpdate(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient. : connection.close(): " + ex.getMessage()); }
    }

    /**
     *
     * @param destinationParam
     */
    synchronized public void updateDestination(Destination destinationParam)
    {
	//connection = getDriverClientConnection();
	try { psUpdateDestination.setInt(1, destinationParam.getCampaignId()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.1: " + ex.getMessage(), true, true); }
	try { psUpdateDestination.setInt(2, destinationParam.getDestinationCount()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.2: " + ex.getMessage(), true, true); }
	try { psUpdateDestination.setString(3, destinationParam.getDestination()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.3: " + ex.getMessage(), true, true); }
	try { psUpdateDestination.setLong(4, destinationParam.getConnectingTimestamp()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.4: " + ex.getMessage(), true, true); }
	try { psUpdateDestination.setLong(5, destinationParam.getTryingTimestamp()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.4: " + ex.getMessage(), true, true); }
	try { psUpdateDestination.setLong(6, destinationParam.getCallingTimestamp()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.5: " + ex.getMessage(), true, true); }
	try { psUpdateDestination.setLong(7, destinationParam.getCallingAttempts()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.5: " + ex.getMessage(), true, true); }
	try { psUpdateDestination.setLong(8, destinationParam.getRingingTimestamp()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.6: " + ex.getMessage(), true, true); }
	try { psUpdateDestination.setLong(9, destinationParam.getLocalCancelingTimestamp()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.7: " + ex.getMessage(), true, true); }
	try { psUpdateDestination.setLong(10, destinationParam.getRemoteCancelingTimestamp()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.8: " + ex.getMessage(), true, true); }
	try { psUpdateDestination.setLong(11, destinationParam.getLocalBusyTimestamp()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.9: " + ex.getMessage(), true, true); }
	try { psUpdateDestination.setLong(12, destinationParam.getRemoteBusyTimestamp()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.10: " + ex.getMessage(), true, true); }
	try { psUpdateDestination.setLong(13, destinationParam.getAcceptingTimestamp()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.11: " + ex.getMessage(), true, true); }
	try { psUpdateDestination.setLong(14, destinationParam.getTalkingTimestamp()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.12: " + ex.getMessage(), true, true); }
	try { psUpdateDestination.setLong(15, destinationParam.getLocalByeTimestamp()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.13: " + ex.getMessage(), true, true); }
	try { psUpdateDestination.setLong(16, destinationParam.getRemoteByeTimestamp()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.14: " + ex.getMessage(), true, true); }
	try { psUpdateDestination.setLong(17, destinationParam.getResponseStatusCode()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.14: " + ex.getMessage(), true, true); }
	try { psUpdateDestination.setString(18, destinationParam.getResponseStatusDesc()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.14: " + ex.getMessage(), true, true); }
	try { psUpdateDestination.setLong(19, destinationParam.getId()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.15: " + ex.getMessage(), true, true); }
	try { psUpdateDestination.executeUpdate(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.executeupd: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient : connection.commit(): " + ex.getMessage(), true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient. : connection.close(): " + ex.getMessage()); }
    }

    /**
     *
     * @param idParam
     */
    synchronized public void deleteDestination(long idParam)
    {
        Statement statement = null; ResultSet resultset = null;
	//connection = getDriverClientConnection();
	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: deleteDestination(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { statement.executeUpdate(
				    "DELETE FROM APP.Destination WHERE Id = " + idParam
				    ); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient. : connection.close(): " + ex.getMessage()); }
    }

    /**
     *
     */
    synchronized public void dropDestinationTable()
    {
        Statement statement = null; ResultSet resultset = null;
	//connection = getDriverClientConnection();
	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: dropDestination(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { statement.executeUpdate(
				    "DROP TABLE APP.Destination"
				    ); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient. : connection.close(): " + ex.getMessage()); }
    }



// ===============================================================================================================================================================

    /**
     *
     */
    

    synchronized public void createCampaignStatTable()
    {
        Statement statement = null; ResultSet resultset = null;
	//connection = getDriverClientConnection();
	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: createCampaignStatTable(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { statement.executeUpdate(
				    "CREATE TABLE APP.CampaignStat" +
				    "(" +
					"CampaignId INT generated by default as identity (START WITH 0, INCREMENT BY 1)," +
					"OnAC INT," +
					"IdleAC INT," +
					"ConnectingAC INT," +
					"ConnectingTT INT," +
					"TryingAC INT," +
					"TryingTT INT," +
					"CallingAC INT," +
					"CallingTT INT," +
					"RingingAC INT," +
					"RingingTT INT," +
					"AcceptingAC INT," +
					"AcceptingTT INT," +
					"TalkingAC INT," +
					"TalkingTT INT," +
					"LocalCancelTT INT," +
					"RemoteCancelTT INT," +
					"LocalBusyTT INT," +
					"RemoteBusyTT INT," +
					"LocalByeTT INT," +
					"RemoteByeTT INT" +
				    ")"
				); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient. : connection.close(): " + ex.getMessage()); }
    }

    /**
     *
     * @return
     */
    synchronized public int getCampaignStatsCount()
    {
        Statement statement = null; ResultSet resultset = null;
        int numberOfRecords = 0;
	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: selectCustomer(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { resultset = statement.executeQuery("SELECT COUNT (*) FROM APP.CampaignStat"); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { if (resultset.next()) { numberOfRecords = resultset.getInt(1); }}
	catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }
	return numberOfRecords;
    }

    /**
     *
     * @param campaignIdParam
     * @return
     */
    synchronized public CampaignStat selectCampaignStat(int campaignIdParam)
    {
        Statement statement = null; ResultSet resultset = null;
	CampaignStat campaignStat = new CampaignStat();
	//connection = getDriverClientConnection();
	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: selectCustomer(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { resultset = statement.executeQuery(
						    "SELECT * FROM APP.CampaignStat WHERE CampaignId = " + Integer.toString(campaignIdParam)
						); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
//	try { resultsetmetadata = resultSet.getMetaData(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
//	try { colcount = resultsetmetadata.getColumnCount(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }

	try { if (resultset.next())
	{
	    campaignStat.setCampaignId(resultset.getInt(1));
	    campaignStat.setOnAC(resultset.getInt(2));
	    campaignStat.setIdleAC(resultset.getInt(3));
	    campaignStat.setConnectingAC(resultset.getInt(4));
	    campaignStat.setConnectingTT(resultset.getInt(5));
	    campaignStat.setTryingAC(resultset.getInt(6));
	    campaignStat.setTryingTT(resultset.getInt(7));
	    campaignStat.setCallingAC(resultset.getInt(8));
	    campaignStat.setCallingTT(resultset.getInt(9));
	    campaignStat.setRingingAC(resultset.getInt(10));
	    campaignStat.setRingingTT(resultset.getInt(11));
	    campaignStat.setAcceptingAC(resultset.getInt(12));
	    campaignStat.setAcceptingTT(resultset.getInt(13));
	    campaignStat.setTalkingAC(resultset.getInt(14));
	    campaignStat.setTalkingTT(resultset.getInt(15));
	    campaignStat.setLocalCancelTT(resultset.getInt(16));
	    campaignStat.setRemoteCancelTT(resultset.getInt(17));
	    campaignStat.setLocalBusyTT(resultset.getInt(18));
	    campaignStat.setRemoteBusyTT(resultset.getInt(19));
	    campaignStat.setLocalByeTT(resultset.getInt(20));
	    campaignStat.setRemoteByeTT(resultset.getInt(21));
	}}
	catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient. : connection.close(): " + ex.getMessage()); }

	return campaignStat;
    }

    /**
     *
     * @return
     */
    synchronized public CampaignStat selectLastCampaignStat()
    {
        Statement statement = null; ResultSet resultset = null;
	CampaignStat campaignStat = new CampaignStat();
	int colcount = 0;
	//connection = getDriverClientConnection();
	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: selectCustomer(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { resultset = statement.executeQuery(
						    "SELECT * FROM APP.CampaignStat ORDER BY CampaignId DESC"
						); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
//	try { resultsetmetadata = resultSet.getMetaData(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
//	try { colcount = resultsetmetadata.getColumnCount(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }

	try { if (resultset.next())
	{
	    campaignStat.setCampaignId(resultset.getInt(1));
	    campaignStat.setOnAC(resultset.getInt(2));
	    campaignStat.setIdleAC(resultset.getInt(3));
	    campaignStat.setConnectingAC(resultset.getInt(4));
	    campaignStat.setConnectingTT(resultset.getInt(5));
	    campaignStat.setTryingAC(resultset.getInt(6));
	    campaignStat.setTryingTT(resultset.getInt(7));
	    campaignStat.setCallingAC(resultset.getInt(8));
	    campaignStat.setCallingTT(resultset.getInt(9));
	    campaignStat.setRingingAC(resultset.getInt(10));
	    campaignStat.setRingingTT(resultset.getInt(11));
	    campaignStat.setAcceptingAC(resultset.getInt(12));
	    campaignStat.setAcceptingTT(resultset.getInt(13));
	    campaignStat.setTalkingAC(resultset.getInt(14));
	    campaignStat.setTalkingTT(resultset.getInt(15));
	    campaignStat.setLocalCancelTT(resultset.getInt(16));
	    campaignStat.setRemoteCancelTT(resultset.getInt(17));
	    campaignStat.setLocalBusyTT(resultset.getInt(18));
	    campaignStat.setRemoteBusyTT(resultset.getInt(19));
	    campaignStat.setLocalByeTT(resultset.getInt(20));
	    campaignStat.setRemoteByeTT(resultset.getInt(21));
	}}
	catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient. : connection.close(): " + ex.getMessage()); }

	return campaignStat;
    }

    /**
     *
     * @param campaignStatParam
     */
    synchronized public void insertCampaignStat(CampaignStat campaignStatParam)
    {
	//connection = getDriverClientConnection();
	try { psInsertCampaignStat.setInt(1, campaignStatParam.getOnAC()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.insertCampaignStat: " + ex.getMessage(), true, true); }
	try { psInsertCampaignStat.setInt(2, campaignStatParam.getIdleAC()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.insertCampaignStat: " + ex.getMessage(), true, true); }
	try { psInsertCampaignStat.setInt(3, campaignStatParam.getConnectingAC()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.insertCampaignStat: " + ex.getMessage(), true, true); }
	try { psInsertCampaignStat.setInt(4, campaignStatParam.getConnectingTT()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.insertCampaignStat: " + ex.getMessage(), true, true); }
	try { psInsertCampaignStat.setInt(5, campaignStatParam.getTryingAC()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.insertCampaignStat: " + ex.getMessage(), true, true); }
	try { psInsertCampaignStat.setInt(6, campaignStatParam.getTryingTT()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.insertCampaignStat: " + ex.getMessage(), true, true); }
	try { psInsertCampaignStat.setInt(7, campaignStatParam.getCallingAC()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.insertCampaignStat: " + ex.getMessage(), true, true); }
	try { psInsertCampaignStat.setInt(8, campaignStatParam.getCallingTT()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.insertCampaignStat: " + ex.getMessage(), true, true); }
	try { psInsertCampaignStat.setInt(9, campaignStatParam.getRingingAC()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.insertCampaignStat: " + ex.getMessage(), true, true); }
	try { psInsertCampaignStat.setInt(10, campaignStatParam.getRingingTT()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.insertCampaignStat: " + ex.getMessage(), true, true); }
	try { psInsertCampaignStat.setInt(11, campaignStatParam.getAcceptingAC()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.insertCampaignStat: " + ex.getMessage(), true, true); }
	try { psInsertCampaignStat.setInt(12, campaignStatParam.getAcceptingTT()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.insertCampaignStat: " + ex.getMessage(), true, true); }
	try { psInsertCampaignStat.setInt(13, campaignStatParam.getTalkingAC()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.insertCampaignStat: " + ex.getMessage(), true, true); }
	try { psInsertCampaignStat.setInt(14, campaignStatParam.getTalkingTT()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.insertCampaignStat: " + ex.getMessage(), true, true); }
	try { psInsertCampaignStat.setInt(15, campaignStatParam.getLocalCancelTT()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.insertCampaignStat: " + ex.getMessage(), true, true); }
	try { psInsertCampaignStat.setInt(16, campaignStatParam.getRemoteCancelTT()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.insertCampaignStat: " + ex.getMessage(), true, true); }
	try { psInsertCampaignStat.setInt(17, campaignStatParam.getLocalBusyTT()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.insertCampaignStat: " + ex.getMessage(), true, true); }
	try { psInsertCampaignStat.setInt(18, campaignStatParam.getRemoteBusyTT()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.insertCampaignStat: " + ex.getMessage(), true, true); }
	try { psInsertCampaignStat.setInt(19, campaignStatParam.getLocalByeTT()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.insertCampaignStat: " + ex.getMessage(), true, true); }
	try { psInsertCampaignStat.setInt(20, campaignStatParam.getRemoteByeTT()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.insertCampaignStat: " + ex.getMessage(), true, true); }
	try { psInsertCampaignStat.executeUpdate(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.insertCampaignStat.executeUpdate: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient. : connection.close(): " + ex.getMessage()); }
    }

    /**
     *
     * @param campaignStatParam
     */
    synchronized public void updateCampaignStat(CampaignStat campaignStatParam)
    {
	//connection = getDriverClientConnection();
	try { psUpdateCampaignStat.setInt(1, campaignStatParam.getOnAC()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.updateCampaignStat: " + ex.getMessage(), true, true); }
	try { psUpdateCampaignStat.setInt(2, campaignStatParam.getIdleAC()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.updateCampaignStat: " + ex.getMessage(), true, true); }
	try { psUpdateCampaignStat.setInt(3, campaignStatParam.getConnectingAC()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.updateCampaignStat: " + ex.getMessage(), true, true); }
	try { psUpdateCampaignStat.setInt(4, campaignStatParam.getConnectingTT()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.updateCampaignStat: " + ex.getMessage(), true, true); }
	try { psUpdateCampaignStat.setInt(5, campaignStatParam.getTryingAC()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.updateCampaignStat: " + ex.getMessage(), true, true); }
	try { psUpdateCampaignStat.setInt(6, campaignStatParam.getTryingTT()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.updateCampaignStat: " + ex.getMessage(), true, true); }
	try { psUpdateCampaignStat.setInt(7, campaignStatParam.getCallingAC()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.updateCampaignStat: " + ex.getMessage(), true, true); }
	try { psUpdateCampaignStat.setInt(8, campaignStatParam.getCallingTT()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.updateCampaignStat: " + ex.getMessage(), true, true); }
	try { psUpdateCampaignStat.setInt(9, campaignStatParam.getRingingAC()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.updateCampaignStat: " + ex.getMessage(), true, true); }
	try { psUpdateCampaignStat.setInt(10, campaignStatParam.getRingingTT()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.updateCampaignStat: " + ex.getMessage(), true, true); }
	try { psUpdateCampaignStat.setInt(11, campaignStatParam.getAcceptingAC()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.updateCampaignStat: " + ex.getMessage(), true, true); }
	try { psUpdateCampaignStat.setInt(12, campaignStatParam.getAcceptingTT()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.updateCampaignStat: " + ex.getMessage(), true, true); }
	try { psUpdateCampaignStat.setInt(13, campaignStatParam.getTalkingAC()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.updateCampaignStat: " + ex.getMessage(), true, true); }
	try { psUpdateCampaignStat.setInt(14, campaignStatParam.getTalkingTT()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.updateCampaignStat: " + ex.getMessage(), true, true); }
	try { psUpdateCampaignStat.setInt(15, campaignStatParam.getLocalCancelTT()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.updateCampaignStat: " + ex.getMessage(), true, true); }
	try { psUpdateCampaignStat.setInt(16, campaignStatParam.getRemoteCancelTT()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.updateCampaignStat: " + ex.getMessage(), true, true); }
	try { psUpdateCampaignStat.setInt(17, campaignStatParam.getLocalBusyTT()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.updateCampaignStat: " + ex.getMessage(), true, true); }
	try { psUpdateCampaignStat.setInt(18, campaignStatParam.getRemoteBusyTT()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.updateCampaignStat: " + ex.getMessage(), true, true); }
	try { psUpdateCampaignStat.setInt(19, campaignStatParam.getLocalByeTT()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.updateCampaignStat: " + ex.getMessage(), true, true); }
	try { psUpdateCampaignStat.setInt(20, campaignStatParam.getRemoteByeTT()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.updateCampaignStat: " + ex.getMessage(), true, true); }
	try { psUpdateCampaignStat.setInt(21, campaignStatParam.getCampaignId()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.updateCampaignStat.: " + ex.getMessage(), true, true); }
	try { psUpdateCampaignStat.executeUpdate(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.updateCampaignStat.executeUpdate: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient. : connection.close(): " + ex.getMessage()); }
    }

    /**
     *
     * @param campaignIdParam
     */
    synchronized public void deleteCampaignStat(int campaignIdParam)
    {
        Statement statement = null; ResultSet resultset = null;
	//connection = getDriverClientConnection();
	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: deleteDestination(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { statement.executeUpdate(
					"DELETE FROM APP.CampaignStat WHERE CampaignId = " + campaignIdParam
				    ); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient. : connection.close(): " + ex.getMessage()); }
    }

    /**
     *
     */
    synchronized public void dropCampaignStatTable()
    {
        Statement statement = null; ResultSet resultset = null;
	//connection = getDriverClientConnection();
	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: dropCampaignStatTable(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { statement.executeUpdate(
				    "DROP TABLE APP.CampaignStat"
				    ); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient. : connection.close(): " + ex.getMessage()); }
    }



// ===============================================================================================================================================================

    /**
     *
     */
    


    synchronized public void createPricelistTable()
    {
        Statement statement = null; ResultSet resultset = null;
	//connection = getDriverClientConnection();
	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: createPricelistTable(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { statement.executeUpdate(
					"CREATE TABLE APP.Pricelist" +
					"(" +
					    "B2BDaytimePerSecond DECIMAL(4,3)," +
					    "B2BEveningPerSecond DECIMAL(4,3)," +
					    "B2CDaytimePerSecond DECIMAL(4,3)," +
					    "B2CEveningPerSecond DECIMAL(4,3)," +
					    "A2SDaytimePerSecond DECIMAL(4,3)," +
					    "A2SEveningPerSecond DECIMAL(4,3)" +
					")"
				     ); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }

	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: createPricelistTable(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { statement.executeUpdate(
					"INSERT INTO APP.Pricelist VALUES (0.01,0.01,0.001,0.005,0.01,0.01)"
				     ); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient. : connection.close(): " + ex.getMessage()); }
    }

    /**
     *
     * @return
     */
    synchronized public int getPricelistCount()
    {
        Statement statement = null; ResultSet resultset = null;
        int numberOfRecords = 0;
	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: selectCustomer(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { resultset = statement.executeQuery("SELECT COUNT (*) FROM APP.Pricelist"); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { if (resultset.next()) { numberOfRecords = resultset.getInt(1); }}
	catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }
	return numberOfRecords;
    }

    /**
     *
     * @return
     */
    synchronized public Pricelist selectPricelist()
    {
        Statement statement = null; ResultSet resultset = null;
	Pricelist pricelist = new Pricelist();
	int colcount = 0;
	//connection = getDriverClientConnection();
	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: selectCustomer(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { resultset = statement.executeQuery(
						    "SELECT * FROM APP.Pricelist"
					    ); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
//	try { resultsetmetadata = resultSet.getMetaData(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
//	try { colcount = resultsetmetadata.getColumnCount(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }

	try { while (resultset.next())
	{
	    pricelist.setB2BDaytimeRatePerSecond(resultset.getFloat(1));
	    pricelist.setB2BEveningRatePerSecond(resultset.getFloat(2));
	    pricelist.setB2CDaytimeRatePerSecond(resultset.getFloat(3));
	    pricelist.setB2CEveningRatePerSecond(resultset.getFloat(4));
	    pricelist.setA2SDaytimeRatePerSecond(resultset.getFloat(5));
	    pricelist.setA2SEveningRatePerSecond(resultset.getFloat(6));
	}}
	catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient. : connection.close(): " + ex.getMessage()); }

	return pricelist;
    }

    /**
     *
     * @param pricelistParam
     */
    synchronized public void insertPricelist(Pricelist pricelistParam)
    {
	//connection = getDriverClientConnection();
	try { psInsertPricelist.setFloat(1, pricelistParam.getB2BDaytimeRatePerSecond()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertPricelist.setFloat(2, pricelistParam.getB2BEveningRatePerSecond()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertPricelist.setFloat(3, pricelistParam.getB2CDaytimeRatePerSecond()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertPricelist.setFloat(4, pricelistParam.getB2CEveningRatePerSecond()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertPricelist.setFloat(5, pricelistParam.getA2SDaytimeRatePerSecond()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertPricelist.setFloat(6, pricelistParam.getA2SEveningRatePerSecond()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertPricelist.execute(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient. : connection.close(): " + ex.getMessage()); }
    }

    /**
     *
     * @param pricelistParam
     */
    synchronized public void updatePricelist(Pricelist pricelistParam)
    {
	//connection = getDriverClientConnection();
	try { psUpdatePricelist.setFloat(1, pricelistParam.getB2BDaytimeRatePerSecond()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdatePricelist.setFloat(2, pricelistParam.getB2BEveningRatePerSecond()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdatePricelist.setFloat(3, pricelistParam.getB2CDaytimeRatePerSecond()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdatePricelist.setFloat(4, pricelistParam.getB2CEveningRatePerSecond()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdatePricelist.setFloat(5, pricelistParam.getA2SDaytimeRatePerSecond()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdatePricelist.setFloat(6, pricelistParam.getA2SEveningRatePerSecond()); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdatePricelist.executeUpdate(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient. : connection.close(): " + ex.getMessage()); }
    }

    /**
     *
     */
    synchronized public void deletePricelist()
    {
        Statement statement = null; ResultSet resultset = null;
	//connection = getDriverClientConnection();
	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: deletePricelist(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { statement.executeUpdate(
						    "DELETE FROM APP.Pricelist"
						    ); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	int number; boolean failure = false;try { if (!resultset.next()) { failure = true; reportFailure("No rows in ResultSet"); } } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { if ((number = resultset.getInt(1)) != 300) { failure = true; reportFailure("Wrong row returned, expected num=300, got " + number); } } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { if (!resultset.next()) { failure = true; reportFailure("Too few rows"); } } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { if ((number = resultset.getInt(1)) != 1910) { failure = true; reportFailure("Wrong row returned, expected num=1910, got " + number); } } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { if (resultset.next()) { failure = true; reportFailure("Too many rows"); }	} catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	if (!failure) { System.out.println("Verified the rows"); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient. : connection.close(): " + ex.getMessage()); }
    }

    /**
     *
     */
    synchronized public void dropPricelistTable()
    {
        Statement statement = null; ResultSet resultset = null;
	//connection = getDriverClientConnection();
	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: dropPricelistTable(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { statement.executeUpdate(
				"DROP TABLE APP.Pricelist"
				); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient. : connection.close(): " + ex.getMessage()); }
    }



// ===============================================================================================================================================================



    private String[] executeSQL(String sqlStatementParam)
    {
        Statement statement = null; ResultSet resultset = null;
	String[] status = new String[2];status[0] = "0"; status[1] = "0";
	//connection = getDriverClientConnection();
	try { statement = connection.createStatement(); } catch (SQLException ex) { status[0] = "1"; status[1] = "Error: executeSQL(): connection.createStatement(): " + ex.getMessage(); return status; }
	try { statement.executeUpdate(sqlStatementParam); } catch (SQLException ex) { status[0] = "1"; status[1] = "Error: executeSQL(): statement.executeUpdate(sqlStatementParam): " + ex.getMessage();  return status; }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient. : connection.close(): " + ex.getMessage()); }
	return status;
    }

    private void checkTables()
    {
        Statement statement = null; ResultSet resultset = null;
	//connection = getDriverClientConnection();
        userInterface.feedback("db_tables_checking", 0);

	boolean tablesMissing = false;
	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: checkTables(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { resultset = statement.executeQuery("SELECT * FROM SYS.SYSTABLES WHERE SYS.SYSTABLES.TABLETYPE = 'T' AND SYS.SYSTABLES.TABLENAME = 'CUSTOMER'"); } catch (SQLException ex) { System.out.println("Error: createTables(): SQLException: " + ex.getMessage()); }
	try { if ((resultset.next()) && (resultset.getString(2).equals("CUSTOMER"))) {}	else { tablesMissing = true; createCustomerTable(); userInterface.showStatus("JavaDB Database Client Created table: Customer", true, true); }}
	catch (SQLException ex) { System.out.println("Error: createTables(): checkCustomerTable: " + ex.getMessage()); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }

	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: checkTables(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { resultset = statement.executeQuery("SELECT * FROM SYS.SYSTABLES WHERE SYS.SYSTABLES.TABLETYPE = 'T' AND SYS.SYSTABLES.TABLENAME = 'RESELLER'"); } catch (SQLException ex) { System.out.println("Error: createTables(): SQLException: " + ex.getMessage()); }
	try { if ((resultset.next()) && (resultset.getString(2).equals("RESELLER"))) {}	else { tablesMissing = true; createResellerTable(); userInterface.showStatus("JavaDB Database Client Created table: Reseller", true, true); }}
	catch (SQLException ex) { System.out.println("Error: createTables(): checkResellerTable: " + ex.getMessage()); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }

	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: checkTables(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { resultset = statement.executeQuery("SELECT * FROM SYS.SYSTABLES WHERE SYS.SYSTABLES.TABLETYPE = 'T' AND SYS.SYSTABLES.TABLENAME = 'CUSTOMERORDER'"); } catch (SQLException ex) { System.out.println("Error: createTables(): SQLException: " + ex.getMessage()); }
	try { if ((resultset.next()) && (resultset.getString(2).equals("CUSTOMERORDER"))) {}
	else { tablesMissing = true; createCustomerOrderTable(); userInterface.showStatus("JavaDB Database Client Created table: CustomerOrder", true, true); }}
	catch (SQLException ex) { System.out.println("Error: createTables(): checkCustomerOrderTable: " + ex.getMessage()); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }

	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: checkTables(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { resultset = statement.executeQuery("SELECT * FROM SYS.SYSTABLES WHERE SYS.SYSTABLES.TABLETYPE = 'T' AND SYS.SYSTABLES.TABLENAME = 'CAMPAIGN'"); } catch (SQLException ex) { System.out.println("Error: createTables(): SQLException: " + ex.getMessage()); }
	try { if ((resultset.next()) && (resultset.getString(2).equals("CAMPAIGN"))) {}
	else { tablesMissing = true; createCampaignTable(); userInterface.showStatus("JavaDB Database Client Created table: Campaign", true, true); }}
	catch (SQLException ex) { System.out.println("Error: createTables(): checkCampaignTable: " + ex.getMessage()); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }

	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: checkTables(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { resultset = statement.executeQuery("SELECT * FROM SYS.SYSTABLES WHERE SYS.SYSTABLES.TABLETYPE = 'T' AND SYS.SYSTABLES.TABLENAME = 'DESTINATION'"); } catch (SQLException ex) { System.out.println("Error: createTables(): SQLException: " + ex.getMessage()); }
	try { if ((resultset.next()) && (resultset.getString(2).equals("DESTINATION"))) {}
	else { tablesMissing = true; createDestinationTable(); userInterface.showStatus("JavaDB Database Client Created table: Destination", true, true); }}
	catch (SQLException ex) { System.out.println("Error: createTables(): checkDestinationTable: " + ex.getMessage()); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }

	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: checkTables(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { resultset = statement.executeQuery("SELECT * FROM SYS.SYSTABLES WHERE SYS.SYSTABLES.TABLETYPE = 'T' AND SYS.SYSTABLES.TABLENAME = 'CAMPAIGNSTAT'"); } catch (SQLException ex) { System.out.println("Error: createTables(): SQLException: " + ex.getMessage()); }
	try { if ((resultset.next()) && (resultset.getString(2).equals("CAMPAIGNSTAT"))) {}
	else { tablesMissing = true; createCampaignStatTable(); userInterface.showStatus("JavaDB Database Client Created table: CampaignStat", true, true); }}
	catch (SQLException ex) { System.out.println("Error: createTables(): checkCampaignStatTable: " + ex.getMessage()); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }

	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: checkTables(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { resultset = statement.executeQuery("SELECT * FROM SYS.SYSTABLES WHERE SYS.SYSTABLES.TABLETYPE = 'T' AND SYS.SYSTABLES.TABLENAME = 'INVOICE'"); } catch (SQLException ex) { System.out.println("Error: createTables(): SQLException: " + ex.getMessage()); }
	try { if ((resultset.next()) && (resultset.getString(2).equals("INVOICE"))) {}
	else { tablesMissing = true; createInvoiceTable(); userInterface.showStatus("JavaDB Database Client Created table: Invoice", true, true); }}
	catch (SQLException ex) { System.out.println("Error: createTables(): checkInvoiceTable: " + ex.getMessage()); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }

	try { statement = connection.createStatement(); } catch (SQLException ex) { System.out.println("Error: checkTables(): connection.createStatement(): " + ex.getMessage()); try { connection.close(); } catch (SQLException ex1) {} connection = initDatabaseClientServer(); }
	try { resultset = statement.executeQuery("SELECT * FROM SYS.SYSTABLES WHERE SYS.SYSTABLES.TABLETYPE = 'T' AND SYS.SYSTABLES.TABLENAME = 'PRICELIST'"); } catch (SQLException ex) { System.out.println("Error: createTables(): SQLException: " + ex.getMessage()); }
	try { if ((resultset.next()) && (resultset.getString(2).equals("PRICELIST"))) {} else { tablesMissing = true; createPricelistTable(); userInterface.showStatus("JavaDB Database Client Created table: Pricelist", true, true); }}
	catch (SQLException ex) { System.out.println("Error: createTables(): checkPricelistTable: " + ex.getMessage()); }
	try { connection.commit(); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient. : connection.commit(): " + ex.getMessage(), true, true); }

	if (tablesMissing) { userInterface.showStatus("JavaDB Database Client Created " + ECallCenter21.getBrand() + " Database", true, true); }

	//try { connection.close(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient. : connection.close(): " + ex.getMessage()); }
        userInterface.feedback("db_tables_checked", 0);
    }

    synchronized private void reportFailure(String message) { userInterface.showStatus("JavaDB Database Client Data Verification Failed: " + message, true, true); }

    /**
     *
     * @param error
     */
    synchronized public void printSQLException(SQLException error)
    {
        // Unwraps the entire exception chain to unveil the real cause of the Exception.
        while (error != null) { userInterface.showStatus("SQLException: ", true, true); userInterface.showStatus(" SQL State: " + error.getSQLState(), true, true); userInterface.showStatus(" Error Code: " + error.getErrorCode(), true, true); userInterface.showStatus(" Message: " + error.getMessage(), true, true); error = error.getNextException(); }
    }

    /**
     *
     */
    public void shutdownDB()
    {
	// Single DB Shutdown SQL state is "08006", and the error code is 45000.
	try { DriverManager.getConnection("jdbc:derby:;shutdown=true"); }
	catch (SQLException se) { if (( (se.getErrorCode() == 50000) && ("XJ015".equals(se.getSQLState()) ))) { userInterface.showStatus("JavaDB Database Client Driver Shutdown Successfull", true, true); }
	else { userInterface.showStatus("JavaDB Database Client Driver Shutdown Unsuccessfull", true, true); printSQLException(se); }}
    }

    /**
     *
     */
    public void unloadDriver()
    {
        Statement statement = null; ResultSet resultset = null;
	try { if (resultset != null) { resultset.close(); resultset = null; } } catch (SQLException sqle) { printSQLException(sqle); }
	int i = 0; while (!statements.isEmpty()) { Statement st = (Statement)statements.remove(i); try { if (st != null) { st.close(); st = null; } } catch (SQLException sqle) { printSQLException(sqle); }}

	try { if (connection != null) { connection.close(); connection = null; }} catch (SQLException sqle) { printSQLException(sqle); }
    }

    private void initPrepareStatements()
    {
	//connection = getDriverClientConnection();
	try { psInsertPricelist = connection.prepareStatement("INSERT INTO APP.Pricelist VALUES (?, ?, ?, ?, ?, ?)"); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdatePricelist = connection.prepareStatement("UPDATE APP.Pricelist SET B2BDaytimePerSecond = ?, B2BEveningPerSecond = ?, B2CDaytimePerSecond = ?, B2CEveningPerSecond = ?, A2SDaytimePerSecond = ?, A2SEveningPerSecond = ?"); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertCustomer = connection.prepareStatement("INSERT INTO APP.Customer VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdateCustomer = connection.prepareStatement("UPDATE APP.Customer SET CompanyName = ?, Address = ?, AddressNr = ?, Postcode = ?, City = ?, Country = ?, ContactName = ?, PhoneNr = ?, MobileNr = ?, Email = ?, Password = ?, CustomerDiscount = ?, Comment = ? WHERE Id = ?"); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertReseller = connection.prepareStatement("INSERT INTO APP.Reseller VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdateReseller = connection.prepareStatement("UPDATE APP.Reseller SET CompanyName = ?, Address = ?, AddressNr = ?, Postcode = ?, City = ?, Country = ?, ContactName = ?, PhoneNr = ?, MobileNr = ?, Email = ?, Password = ?, ResellerDiscount = ?, Comment = ? WHERE Id = ?"); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertOrder = connection.prepareStatement("INSERT INTO APP.CustomerOrder VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdateOrder = connection.prepareStatement("UPDATE APP.CustomerOrder SET CustomerId = ?, Recipients = ?, Timewindow0 = ?, Timewindow1 = ?, Timewindow2 = ?, TargetTransactionQuantity = ?, MessageFilename = ?, messageDuration = ?, MessageRatePerSecond = ?, MessageRate = ?, SubTotal = ? WHERE Id = ?"); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertInvoice = connection.prepareStatement("INSERT INTO APP.Invoice VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdateInvoice = connection.prepareStatement("UPDATE APP.Invoice SET OrderId = ?, QuantityItem = ?, ItemDesc = ?, ItemUnitPrice = ?, ItemVATPercentage = ?, ItemQuantityPrice = ?, SubTotalB4Discount = ?, CustomerDiscount = ?, SubTotal = ?, VAT = ?, Total = ?, Paid = ? WHERE Id = ?"); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertCampaign = connection.prepareStatement("INSERT INTO APP.Campaign VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?)"); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdateCampaign = connection.prepareStatement("UPDATE APP.Campaign SET Timestamp = ?, OrderId = ?, TimeScheduledStart = ?, TimeScheduledEnd = ?, TimeExpectedStart = ?, TimeExpectedEnd = ?, TimeRegisteredStart = ?, TimeRegisteredEnd = ?, TestCampaign = ? WHERE Id = ?"); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertDestination = connection.prepareStatement("INSERT INTO APP.Destination VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdateDestination = connection.prepareStatement("UPDATE APP.Destination SET CampaignId = ?, DestinationCount = ?, Destination = ?, ConnectingTimestamp = ?, TryingTimestamp = ?, CallingTimestamp = ?, CallingAttempts = ?, RingingTimestamp = ?, LocalCancelingTimestamp = ?, RemoteCancelingTimestamp = ?, LocalBusyTimestamp = ?, RemoteBusyTimestamp = ?, AcceptingTimestamp = ?, TalkingTimestamp = ?, LocalByeTimestamp = ?, RemoteByeTimestamp = ?, ResponseStatusCode = ?,  ResponseStatusDesc = ? WHERE Id = ?"); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psInsertCampaignStat = connection.prepareStatement("INSERT INTO APP.CampaignStat VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	try { psUpdateCampaignStat = connection.prepareStatement("UPDATE APP.CampaignStat SET OnAC = ?, IdleAC = ?, ConnectingAC = ?, ConnectingTT = ?, TryingAC = ?, TryingTT = ?, CallingAC = ?, CallingTT = ?, RingingAC = ?, RingingTT = ?, AcceptingAC = ?, AcceptingTT = ?, TalkingAC = ?, TalkingTT = ?, LocalCancelTT = ?, RemoteCancelTT = ?, LocalBusyTT = ?, RemoteBusyTT = ?, LocalByeTT = ?, RemoteByeTT = ? WHERE CampaignId = ?"); } catch (SQLException ex) { userInterface.showStatus("Error: JavaDBClient.: " + ex.getMessage(), true, true); }
	//try { connection.close(); } catch (SQLException ex) { myUserInterface.showStatus("Error: JavaDBClient. : connection.close(): " + ex.getMessage()); }
    }

    /**
     *
     * @return
     */
    public boolean getDBServerTest()
    {
        return dbServerTest;
    }
}

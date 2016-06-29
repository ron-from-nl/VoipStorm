import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import net.sf.atomicdate.Client;

/**
 *
 * @author ron
 */
public class NTPDate extends java.util.Date
{
	private static final long serialVersionUID=1064411106013132402L;
        private String[]            status;

	/**
	 * The SNTP server address JVM property name. The value must be in form
	 * <code>host:port</code>.
	 */
	public static final String SERVER_ADDRESS_PROPERTY = "time.euro.apple.com";

    /**
     *
     */
    public static final String DEFAULT_SERVER_ADDRESS = "pool.ntp.org"; // gets used if SERVER_ADDRESS_PROPERTY = not set

    /**
     *
     */
    public NTPDate()
	{
		super();
                status = new String[2]; status[0] = "0"; status[1]  = "";
//		try { synchronize(); } catch (final Throwable t) { /* System.err.println("NTPError: synchronizing." + " Details: " + t.getClass().getName()+": "+t.getMessage()); super.setTime((new java.util.Date()).getTime()); */ }
	}

    /**
     *
     * @param haddr
     * @throws IOException
     */
    public NTPDate(final String haddr) throws IOException
	{
		this(haddr, Client.DEFAULT_SNTP_PORT);
	}

    /**
     *
     * @param haddr
     * @param port
     * @throws IOException
     */
    public NTPDate(final String haddr, final int port) throws IOException
	{
		this(InetAddress.getByName(haddr), port);
	}

    /**
     *
     * @param raddr
     * @throws IOException
     */
    public NTPDate(final InetAddress raddr) throws IOException
	{
		this(raddr, Client.DEFAULT_SNTP_PORT);
	}

    /**
     *
     * @param addr
     * @param port
     * @throws IOException
     */
    public NTPDate(final InetAddress addr, final int port) throws IOException
	{
		super();
		synchronize(addr, port);
	}

    /**
     *
     * @return
     */
    public String[] synchronize()
	{
		final String property = System.getProperty(SERVER_ADDRESS_PROPERTY, DEFAULT_SERVER_ADDRESS);
		final int hostIndex = property.indexOf(":");
		final String host;
		final int port;
		if (hostIndex != -1)
                {
                    host = property.substring(0, hostIndex);
                    try { port = Integer.parseInt(property.substring( hostIndex + 1 )); } catch (final NumberFormatException nfe) { throw new RuntimeException("Invalid default SNTP server port."); }
		}
                else
                {
                    host = property;
                    port = Client.DEFAULT_SNTP_PORT;
		}
                try { status = synchronize(InetAddress.getByName(host), port); } catch (UnknownHostException ex) {  status[0] = "1"; status[1] = "Error: UnknownHostException: synchronize(InetAddress.getByName(host), port): " + ex.getMessage(); return status; }
                return status;
	}

    /**
     *
     * @param addr
     * @param port
     * @return
     */
    public String[] synchronize(final InetAddress addr, final int port)
	{
                status = new String[2]; status[0] = "0"; status[1]  = "";
                Client ntpClient = null;
                try { ntpClient = new Client(); } catch (SocketException ex) { status[0] = "1"; status[1] = "Error: SocketException: ntpClient = new Client(): " + ex.getMessage(); return status;}
                try { super.setTime(System.currentTimeMillis() + ntpClient.getOffset(addr)); } catch (IOException ex) { status[0] = "1"; status[1] = "Error: IOException: super.setTime(System.currentTimeMillis() + ntpClient.getOffset(addr)): " + ex.getMessage(); return status;}
                if (ntpClient!=null) { ntpClient.close(); ntpClient = null;}
                return status;
	}


}

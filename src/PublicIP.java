import java.net.URL;
import java.net.HttpURLConnection;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.InputStream;

/**
 *
 * @author ron
 */
public class PublicIP
{

    /**
     *
     * @return
     */
    public static String getPublicIP()
    {
        String publicIP = null;
        try
        {
            URL tempURL = new URL("http://www.whatismyip.org/");
            HttpURLConnection tempConn = (HttpURLConnection)tempURL.openConnection();
            InputStream tempInStream = tempConn.getInputStream();
            InputStreamReader tempIsr = new InputStreamReader(tempInStream);
            BufferedReader tempBr = new BufferedReader(tempIsr);
            publicIP = tempBr.readLine();
            tempBr.close();
            tempInStream.close();
        }
        catch (Exception ex)
        {
            publicIP = "<Could-Not-Resolve-Public-IP-Address>";
        }
        return publicIP;
    }
}


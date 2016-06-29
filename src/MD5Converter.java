import java.io.File;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author ron
 */
public class MD5Converter {

    /**
     *
     * @param input
     * @return
     */
    public static String getMD5SumFromString(String input)
    {
        String output = "";

        byte[] inputBytes = input.getBytes();
        MessageDigest myMessageDigest = null;
        try { myMessageDigest = MessageDigest.getInstance("MD5"); } catch (NoSuchAlgorithmException ex) {  }
        myMessageDigest.reset();
        myMessageDigest.update(inputBytes);
        byte messageDigest[] = myMessageDigest.digest();
        StringBuffer hexString = new StringBuffer();
        for (int i=0; i < messageDigest.length; i++ ) { hexString.append(Integer.toHexString(0xFF & messageDigest[i])); }
        output = hexString+"";

        return output;
    }

    /**
     *
     * @param byteArray
     * @return
     */
    public static String getMD5SumFromByteArray(byte[] byteArray)
    {
        String output = "";
        MessageDigest myMessageDigest = null;
        try { myMessageDigest = MessageDigest.getInstance("MD5"); } catch (NoSuchAlgorithmException ex) {  }
        myMessageDigest.reset();
        myMessageDigest.update(byteArray);
        byte messageDigest[] = myMessageDigest.digest();
        StringBuffer hexString = new StringBuffer();
        for (int i=0; i < messageDigest.length; i++ ) { hexString.append(Integer.toHexString(0xFF & messageDigest[i])); }
        output = hexString+"";

        return output;
    }
}

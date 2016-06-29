import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 *
 * @author ron
 */
public class VoipStormTools
{

    /**
     *
     * @param cal
     * @return
     */
    public static String getHumanDate(Calendar cal)
      {
            String dateString = "";
            dateString = "" +
            String.format("%04d", cal.get(Calendar.YEAR)) + "-" +
            String.format("%02d", cal.get(Calendar.MONTH) + 1) + "-" +
            String.format("%02d", cal.get(Calendar.DAY_OF_MONTH));
//            String.format("%02d", cal.get(Calendar.SECOND));

//            dateString += " [" + cal.getTimeInMillis() +"]";
            return dateString;
      }

    /**
     *
     * @param cal
     * @return
     */
    public static String getHumanDateLong(Calendar cal)
      {
            String dateString = "";
            dateString = "" +
            String.format("%04d", cal.get(Calendar.YEAR)) + "-" +
            String.format("%02d", cal.get(Calendar.MONTH) + 1) + "-" +
            String.format("%02d", cal.get(Calendar.DAY_OF_MONTH)) + " " +
            String.format("%02d", cal.get(Calendar.HOUR_OF_DAY)) + ":" +
            String.format("%02d", cal.get(Calendar.MINUTE)); // + ":" +
//            String.format("%02d", cal.get(Calendar.SECOND));

//            dateString += " [" + cal.getTimeInMillis() +"]";
            return dateString;
      }

    /**
     *
     * @param filenameParam
     * @param dataParam
     */
    public static void writeToFile(final String filenameParam, final byte[] dataParam)
  {
        Thread logToFileThread = new Thread(new Runnable()
        {
            private FileWriter logFileWriter;
            @Override
            @SuppressWarnings({"static-access"})
            public void run()
            {
                FileOutputStream fileoutputstream = null;
                try { fileoutputstream = new FileOutputStream(filenameParam); } catch (FileNotFoundException ex) {}
                try { fileoutputstream.write(dataParam); }                      catch (IOException ex) {}
                try { fileoutputstream.close(); }                               catch (IOException ex) { }
            }
        });
        logToFileThread.setName("logToFileThread");
        logToFileThread.start();
  }

    /**
     *
     * @param wholeString
     * @param subString
     * @return
     */
    public static boolean isSubString( String wholeString, String subString ) // Better Use java.lang.String.indexOf method
  {
    boolean output = false;

    String regex = new String("s/"+subString+"/");
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(subString);
    if (matcher.find()) { output = true; }
    return output;
  }

    /**
     *
     * @param text
     * @param regex
     * @return
     */
    public static String substitude(String text, String regex)
  {
    //String text = "Hallo, hij zei: \"geef mijn 'pen' terug.\", dus; dat deed ik.";

    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(text);
    StringBuffer newtext = new StringBuffer();
    while (matcher.find())
    {
      matcher.appendReplacement(newtext,"");
    }
    matcher.appendTail(newtext);
    //System.out.println(newtext);
    return newtext.toString();
  }

    /**
     *
     * @param text
     * @param separator
     * @return
     */
    public static String split(String text, String separator)
  {
    //String text = "Hallo, hij zei: \"geef mijn 'pen' terug.\", dus; dat deed ik.";

    Pattern pattern = Pattern.compile(separator);
    Matcher matcher = pattern.matcher(text);
    StringBuffer newtext = new StringBuffer();
    while (matcher.find())
    {
      matcher.appendReplacement(newtext,"");
    }
    matcher.appendTail(newtext);
    //System.out.println(newtext);
    return newtext.toString();
  }

    /**
     *
     * @param text
     * @return
     */
    public static String validateSIPAddress(String text)
  {
    StringBuffer finaltext = new StringBuffer("");
    StringBuffer newtext = new StringBuffer("");
    // Stage 1
    Pattern pattern = Pattern.compile("[^\\w\\-\\@\\:\\<\\>\\&\\.\\' ]");
    Matcher matcher = pattern.matcher(text);
    newtext = new StringBuffer(); while (matcher.find()) { matcher.appendReplacement(newtext,""); } matcher.appendTail(newtext);
    // Stage 2
    pattern = Pattern.compile("\\'");
    matcher = pattern.matcher(newtext);
    finaltext = new StringBuffer(); while (matcher.find()) { matcher.appendReplacement(finaltext,"\\\\\'"); } matcher.appendTail(finaltext);
    return finaltext.toString();

  }

    /**
     *
     * @param digitsParam
     * @return
     */
    public static String getRandom(int digitsParam)
  {
    String output = "";
    for (int counter = 0; counter < digitsParam;counter++)
    {
        output += Integer.toString((int)(Math.random()*9));    
    }
    return output;
  }

    /**
     *
     * @param i
     * @return
     */
    public static boolean isLong(String i)
    {
       try { Long.parseLong(i); return true; } catch(NumberFormatException nfe) { return false; }
    }
}

package data;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.*;
import java.net.NetworkInterface;
import java.util.Enumeration;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

/**
 *
 * @author ron
 */
public class Configuration implements Cloneable
{
    private final static String      dataDir= "data/";;
    private final static String      databasesDir= dataDir + "databases/";;
    private final String      xmlConfigDir= dataDir + "config/";;
    private String      xmlFileName;
    private String      xmlFileBase;
    private String      xmlFileExtention;
    private String      domain;
    private String      clientIP;
    private String      publicIP;
    private String      clientPort;
    private String      serverIP;
    private String      serverPort;
    private String      prefPhoneLines; // Prefered number of Phonelines to start in CallCenter (Can't overule Licensed number of Phonelines)
    private String      username;
    private String      toegang;
    private String      register;
    private String      icons; // 1 shows icons in ECallCenter21.phonesPoolTable in stead of Text Chars
    private InetAddress myIP;
    private String[]    status;

    DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = null;
    Document xmlDocument = null;

    public Configuration()
    {
//        try { myIP = InetAddress.getLocalHost(); } catch( UnknownHostException error) { System.out.println("Error: InetAddress.getLocalHost(): " + error.getMessage()); }
        try { myIP = getLocalHostLANAddress(); } catch( UnknownHostException error) { System.out.println("Error: InetAddress.getLocalHost(): " + error.getMessage()); }
//	dataDir		    = "data/";
//        xmlConfigDir        = dataDir + "config/";
        xmlFileName         = "";
        xmlFileBase         = "network";
        xmlFileExtention    = ".xml";
        domain              = "voipstorm.nl";
        clientIP            = myIP.getHostAddress();
        publicIP               = "";
        clientPort          = "auto";
        serverIP            = "";
        serverPort          = "5060";
        prefPhoneLines      = "500";
        username            = "";
        toegang             = "";
        register            = "0";
        icons               = "1";
        status              = new String[2];
    }

    // a full Constructor

    /**
     *
     * @param domainParam
     * @param clientIPParam
     * @param pubIPParam
     * @param clientPortParam
     * @param serverIPParam
     * @param serverPortParam
     * @param prefPhoneLinesParam
     * @param usernameParam
     * @param passwordParam
     * @param registerParam
     * @param iconsParam
     */
        public Configuration(
                            String  domainParam,
                            String  clientIPParam,
                            String  pubIPParam,
                            String  clientPortParam,
                            String  serverIPParam,
                            String  serverPortParam,
                            String  prefPhoneLinesParam,
                            String  usernameParam,
                            String  passwordParam,
                            String  registerParam,
                            String  iconsParam
                        )
                        {
                            try { myIP     = InetAddress.getLocalHost(); } catch( UnknownHostException error) { System.out.println("Error: InetAddress.getLocalHost(): " + error.getMessage()); }
                            domain         = domainParam;
                            clientIP       = clientIPParam;
                            publicIP          = pubIPParam;
                            clientPort     = domainParam;
                            serverIP       = serverIPParam;
                            serverPort     = serverPortParam;
                            prefPhoneLines = prefPhoneLinesParam;
                            username       = usernameParam;
                            toegang        = passwordParam;
                            register       = registerParam;
                            register       = iconsParam;
                        }

    public String[] createConfiguration()
    {
        status[0] = "0"; status[1] = "";
        try { myIP = InetAddress.getLocalHost(); } catch( UnknownHostException error) { status[0] = "1"; status[1] = "createConfiguration Error: myIP = InetAddress.getLocalHost(): UnknownHostException: " + error.getMessage(); return status; }
        setDomain("");
        setClientIP(myIP.getHostAddress());
        setPublicIP("");
        setClientPort("auto");
        setServerIP(myIP.getHostAddress());
        setServerPort("5060");
        setPrefPhoneLines("500");
        setUsername("");
        setToegang("");
        setRegister("0");
        setIcons("1");
        return status;
    }

    private static InetAddress getLocalHostLANAddress() throws UnknownHostException {
        try {
            InetAddress candidateAddress = null;
            // Iterate all NICs (network interface cards)...
            for (Enumeration ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements();) {
                NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
                // Iterate all IP addresses assigned to each card...
                for (Enumeration inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements();) {
                    InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
                    if (!inetAddr.isLoopbackAddress()) {

                        if (inetAddr.isSiteLocalAddress()) {
                            // Found non-loopback site-local address. Return it immediately...
                            return inetAddr;
                        }
                        else if (candidateAddress == null) {
                            // Found non-loopback address, but not necessarily site-local.
                            // Store it as a candidate to be returned if site-local address is not subsequently found...
                            candidateAddress = inetAddr;
                            // Note that we don't repeatedly assign non-loopback non-site-local addresses as candidates,
                            // only the first. For subsequent iterations, candidate will be non-null.
                        }
                    }
                }
            }
            if (candidateAddress != null) {
                // We did not find a site-local address, but we found some other non-loopback address.
                // Server might have a non-site-local address assigned to its NIC (or it might be running
                // IPv6 which deprecates the "site-local" concept).
                // Return this non-loopback candidate address...
                return candidateAddress;
            }
            // At this point, we did not find a non-loopback address.
            // Fall back to returning whatever InetAddress.getLocalHost() returns...
            InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
            if (jdkSuppliedAddress == null) {
                throw new UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
            }
            return jdkSuppliedAddress;
        }
        catch (Exception e) {
            UnknownHostException unknownHostException = new UnknownHostException("Failed to determine LAN address: " + e);
            unknownHostException.initCause(e);
            throw unknownHostException;
        }
    }
    
    public String[] loadConfiguration(String configNumberParam) // Loads xmlfile content into attributes
    {
        status[0] = "0"; status[1] = "";
        if ( (configNumberParam != null) && ( ! configNumberParam.equals("")))
        { xmlFileName = xmlConfigDir + xmlFileBase + configNumberParam + xmlFileExtention; }
        else
        { xmlFileName = xmlConfigDir + xmlFileBase + "1" + xmlFileExtention; }

        // Get the configuration from file
        builderFactory = DocumentBuilderFactory.newInstance();
        builder = null;
        xmlDocument = null;
        try { builder = builderFactory.newDocumentBuilder(); }
        catch (ParserConfigurationException error) { status[0] = "1"; status[1] = "loadConfiguration Error: builder = builderFactory.newDocumentBuilder(): ParserConfigurationException: " + error.getMessage(); return status;}

        try { xmlDocument = builder.parse(xmlFileName); }
        catch (SAXException error) { status[0] = "1"; status[1] = "loadConfiguration Error: xmlDocument = builder.parse(xmlFile): SAXException: " + error.getMessage(); return status; }
        catch (IOException error) { status[0] = "1"; status[1] = "loadConfiguration Error: xmlDocument = builder.parse(xmlFile): IOException: " + error.getMessage(); return status; }

        //set up a transformer
        TransformerFactory transfac = TransformerFactory.newInstance();
        Transformer trans = null; try { trans = transfac.newTransformer(); }
        catch (TransformerConfigurationException error) { status[0] = "1"; status[1] = "loadConfiguration Error: trans = transfac.newTransformer(): TransformerConfigurationException: " + error.getMessage(); return status; }
        trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        trans.setOutputProperty(OutputKeys.INDENT, "yes");

        //create string from xml tree
        StringWriter stringWriter = new StringWriter();
        StreamResult result = new StreamResult(stringWriter);
        DOMSource source = new DOMSource(xmlDocument);
        try { trans.transform(source, result); } catch (TransformerException error) { status[0] = "1"; status[1] = "loadConfiguration Error: trans.transform(source, result): TransformerException: " + error.getMessage(); return status; }
        String xmlString = stringWriter.toString();
        //System.out.println("loadConfiguration: DOM String\n" + xmlString);

        // Now we have a DOM Tree and a DOM String, now let's turn it into a Configuration Object

        String nodeName, nodeValue;
        Node node = xmlDocument.getFirstChild(); // softphoneconfiguration
        NodeList nodelist = node.getChildNodes(); // all the children that form the configuration nodes

        int nodelistcount = nodelist.getLength();
        for (int i = 1; i < nodelistcount; i = i + 2)
        {
            //System.out.println("nodelist item: " + i + " = " + nodelist.item(i).getNodeName() + ": " + nodelist.item(i).getTextContent());
            nodeName = nodelist.item(i).getNodeName();
            nodeValue = nodelist.item(i).getTextContent();

            if      ( nodeName.equals("domain"))         { setDomain         (nodeValue);}
            else if ( nodeName.equals("clientIP"))       { setClientIP       (myIP.getHostAddress());}
            else if ( nodeName.equals("pubIP"))          { setPublicIP          (nodeValue);}
            else if ( nodeName.equals("clientPort"))     { setClientPort     (nodeValue);}
            else if ( nodeName.equals("serverIP"))       { setServerIP       (nodeValue);}
            else if ( nodeName.equals("serverPort"))     { setServerPort     (nodeValue);}
            else if ( nodeName.equals("prefPhoneLines")) { setPrefPhoneLines (nodeValue);}
            else if ( nodeName.equals("username"))       { setUsername       (nodeValue);}
            else if ( nodeName.equals("password"))       { setToegang       (nodeValue);}
            else if ( nodeName.equals("register"))       { setRegister       (nodeValue);}
            else if ( nodeName.equals("icons"))          { setIcons          (nodeValue);}
        }

        //System.out.println("loadConfiguration myConfig.toString()\n" + myConfiguration.toString());
        return status;
    }

    /**
     *
     * @param configNumberParam
     * @return
     */
    public String[] saveConfiguration(String configNumberParam) // Saves attributes to xmlfile
    {
        status[0] = "0"; status[1] = "";

        // Copy config from UserInterface to this SoftPhone instance

        if ( (configNumberParam != null) && ( ! configNumberParam.equals("")))
        { xmlFileName = xmlConfigDir + xmlFileBase + configNumberParam + xmlFileExtention; }
        else
        { xmlFileName = xmlConfigDir + xmlFileBase + "1" + xmlFileExtention; }

        //Build a DOM from the configuration
        Element root, child;
        Text text;

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;
        try { docBuilder = documentBuilderFactory.newDocumentBuilder(); }
        catch (ParserConfigurationException error) { status[0] = "1"; status[1] = "saveConfiguration Error: docBuilder = documentBuilderFactory.newDocumentBuilder(): ParserConfigurationException: " + error.getMessage(); return status; }

        //Creating the XML tree
        xmlDocument = docBuilder.newDocument();

        //create the root element and add it to the document
        root = xmlDocument.createElement("softphoneconfiguration");
        xmlDocument.appendChild(root);

        //create a comment and put it in the root element
        Comment comment = xmlDocument.createComment("SoftPhone Configuration");
        root.appendChild(comment);

        //create child elements, possibly add an attribute, and add to root
        child = xmlDocument.createElement("domain");            text = xmlDocument.createTextNode(getDomain());         child.appendChild(text); root.appendChild(child);
        child = xmlDocument.createElement("clientIP");          text = xmlDocument.createTextNode(getClientIP());       child.appendChild(text); root.appendChild(child);
        child = xmlDocument.createElement("pubIP");             text = xmlDocument.createTextNode(getPublicIP());          child.appendChild(text); root.appendChild(child);
        child = xmlDocument.createElement("clientPort");        text = xmlDocument.createTextNode(getClientPort());     child.appendChild(text); root.appendChild(child);
        child = xmlDocument.createElement("serverIP");          text = xmlDocument.createTextNode(getServerIP());       child.appendChild(text); root.appendChild(child);
        child = xmlDocument.createElement("serverPort");        text = xmlDocument.createTextNode(getServerPort());     child.appendChild(text); root.appendChild(child);
        child = xmlDocument.createElement("prefPhoneLines");    text = xmlDocument.createTextNode(getPrefPhoneLines()); child.appendChild(text); root.appendChild(child);
        child = xmlDocument.createElement("username");          text = xmlDocument.createTextNode(getUsername());       child.appendChild(text); root.appendChild(child);
        child = xmlDocument.createElement("password");          text = xmlDocument.createTextNode(getToegang());       child.appendChild(text); root.appendChild(child);
        child = xmlDocument.createElement("register");          text = xmlDocument.createTextNode(getRegister());       child.appendChild(text); root.appendChild(child);
        child = xmlDocument.createElement("icons");             text = xmlDocument.createTextNode(getIcons());          child.appendChild(text); root.appendChild(child);
        //child.setAttribute("name", "value"); // attributes not (yet) needed

        //Save the DOM to file

        //set up a transformer
        TransformerFactory transfac = TransformerFactory.newInstance();
        Transformer trans = null; try { trans = transfac.newTransformer(); }
        catch (TransformerConfigurationException error) { status[0] = "1"; status[1] = "saveConfiguration Error: trans = transfac.newTransformer(): TransformerConfigurationException: " + error.getMessage(); return status; }
        trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        trans.setOutputProperty(OutputKeys.INDENT, "yes");

        //create string from xml tree
        StringWriter stringWriter = new StringWriter();
        StreamResult result = new StreamResult(stringWriter);
        DOMSource source = new DOMSource(xmlDocument);
        try { trans.transform(source, result); } catch (TransformerException error) { status[0] = "1"; status[1] = "saveConfiguration Error: trans.transform(source, result): TransformerException: " + error.getMessage(); return status; }
        String xmlString = stringWriter.toString();
        //System.out.println("saveConfiguration DOM String: " + xmlString);

        // DOMString is ready, now save it to file
        FileWriter fileWriter = null;
        File fileToWrite = new File(xmlFileName);
        try { fileWriter = new FileWriter(fileToWrite); } catch (IOException error) { status[0] = "1"; status[1] = "saveConfiguration Error: fileWriter = new FileWriter(fileToWrite): IOException: " + error.getMessage(); return status; }
        try { fileWriter.write(xmlString); } catch (IOException error) { status[0] = "1"; status[1] = "saveConfiguration Error: fileWriter.write(xmlString): IOException: " + error.getMessage(); return status; }
        try { fileWriter.flush(); } catch (IOException error) { status[0] = "1"; status[1] = "saveConfiguration Error: fileWriter.flush(): IOException: " + error.getMessage(); return status; }
        return status;
    }

    // Just the getters and setters

    /**
     *
     * @return
     */
        public String  getDomain                ()                              {return domain;}

    /**
     *
     * @return
     */
    public String  getClientIP              ()                              {return clientIP;}

    /**
     *
     * @return
     */
    public String  getPublicIP              ()                              {return publicIP;}

    /**
     *
     * @return
     */
    public String  getClientPort            ()                              {return clientPort;}

    /**
     *
     * @return
     */
    public String  getServerIP              ()                              {return serverIP;}

    /**
     *
     * @return
     */
    public String  getServerPort            ()                              {return serverPort;}

    /**
     *
     * @return
     */
    public String  getPrefPhoneLines        ()                              {return prefPhoneLines;}

    /**
     *
     * @return
     */
    public String  getUsername              ()                              {return username;}

    /**
     *
     * @return
     */
    public String  getToegang               ()                              {return toegang;}

    /**
     *
     * @return
     */
    public String  getRegister              ()                              {return register;}

    /**
     *
     * @return
     */
    public String  getIcons                 ()                              {return icons;}

    /**
     *
     * @return
     */
    public static String  getDataDir        ()                              {return dataDir;}

    /**
     *
     * @return
     */
    public static String  getDatabasesDir   ()                              {return databasesDir;}

    /**
     *
     * @param domainParam
     */
    public void setDomain                   (String  domainParam)           {domain            = domainParam;}

    /**
     *
     * @param clientIPParam
     */
    public void setClientIP                 (String  clientIPParam)         {clientIP          = clientIPParam;}

    /**
     *
     * @param pubIPParam
     */
    public void setPublicIP                 (String  pubIPParam)            {publicIP          = pubIPParam;}

    /**
     *
     * @param clientPortParam
     */
    public void setClientPort               (String  clientPortParam)       {clientPort        = clientPortParam;}

    /**
     *
     * @param serverIPParam
     */
    public void setServerIP                 (String  serverIPParam)         {serverIP          = serverIPParam;}

    /**
     *
     * @param serverPortParam
     */
    public void setServerPort               (String  serverPortParam)       {serverPort        = serverPortParam;}

    /**
     *
     * @param prefPhoneLinesParam
     */
    public void setPrefPhoneLines           (String  prefPhoneLinesParam)   {prefPhoneLines    = prefPhoneLinesParam;}

    /**
     *
     * @param usernameParam
     */
    public void setUsername                 (String  usernameParam)         {username          = usernameParam;}

    /**
     *
     * @param passwordParam
     */
    public void setToegang                  (String  passwordParam)         {toegang           = passwordParam;}

    /**
     *
     * @param registerParam
     */
    public void setRegister                 (String  registerParam)         {register          = registerParam;}

    /**
     *
     * @param iconsParam
     */
    public void setIcons                    (String  iconsParam)            {icons             = iconsParam;}

    @Override
    public String toString()
    {
        String output = null;
        output  = "domain: "                + getDomain()                   + "\n";
        output += "clientIP: "              + getClientIP()                 + "\n";
        output += "pubIP: "                 + getPublicIP()                 + "\n";
        output += "clientPort: "            + getClientPort()               + "\n";
        output += "serverIP: "              + getServerIP()                 + "\n";
        output += "serverPort: "            + getServerPort()               + "\n";
        output += "prefPhoneLines: "        + getPrefPhoneLines()           + "\n";
        output += "username: "              + getUsername()                 + "\n";
        output += "password: "              + getToegang()                  + "\n";
        output += "register: "              + getRegister()                 + "\n";
        output += "icons: "                 + getIcons()                    + "\n";

        return output;
    }

    @Override
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }
}

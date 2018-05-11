/*
 * Copyright © 2008 Ron de Jong (ronuitzaandam@gmail.com).
 *
 * This is free software; you can redistribute it 
 * under the terms of the Creative Commons License
 * Creative Commons License: (CC BY-NC-ND 4.0) as published by
 * https://creativecommons.org/licenses/by-nc-nd/4.0/ ; either
 * version 4.0 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
 * Creative Commons Attribution-NonCommercial-NoDerivatives 4.0
 * International Public License for more details.
 *
 * You should have received a copy of the Creative Commons 
 * Public License License along with this software;
 */

import java.net.InetAddress;
import java.io.*;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Calendar;
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
public class Vergunning implements Cloneable
{
    private static final int    SECOND                          = 1000;
    private static final int    MINUTE                          = SECOND * 60;
    private static final int    HOUR                            = MINUTE * 60;
    private static final int    DAY                             = HOUR   * 24;
    private static final int    WEEK                            = DAY    * 7;
    private static final int    YEAR                            = DAY    * 365;

    public        final int     PHONELINES_DEMO                 = 500;
    public        final int     CALLSPERHOUR_DEMO               = 20000; // 5000
    public        final int     MAXCALLS_DEMO                   = 1000000000; // 100
    public        final int     DESTINATIONDIGITS_DEMO          = 100; // 100
    public        final int     PHONELINES_STANDARD             = 500; // 500
    public        final int     CALLSPERHOUR_STANDARD           = 20000; // 1000
    public        final int     MAXCALLS_STANDARD               = 1000000000; // 10000
    public        final int     DESTINATIONDIGITS_STANDARD      = 100; // 100
    public        final int     PHONELINES_PROFESSIONAL         = 500; // 500
    public        final int     CALLSPERHOUR_PROFESSIONAL       = 20000; // 5000
    public        final int     MAXCALLS_PROFESSIONAL           = 1000000000; //100000
    public        final int     DESTINATIONDIGITS_PROFESSIONAL  = 100; // 100
    public        final int     PHONELINES_ENTERPRISE           = 500; // 500
    public static final int     CALLSPERHOUR_ENTERPRISE         = 20000; // 20000
    public        final int     MAXCALLS_ENTERPRISE             = 1000000000; // 1000000000
    public        final int     DESTINATIONDIGITS_ENTERPRISE    = 100; // 100
    public static final String BRAND                            = "VoipStorm";
    public static final String BUSINESS                         = "Telemarketing";
    public static final String BRAND_DESCRIPTION                = BRAND + " offers 21st Century TeleMarketing Software. Select a soundfile, copy and paste your phonenumbers and start a lightning fast Telephone Advertisement Campaign. TeleMarketing has never been so Fast and Easy at only a tiny Fraction of the costs of Traditional TeleMarketing channels like: CallCenters, Radio and TV !";
    public static final String PRODUCT                          = "ECallCenter21";
    public static final String VERSION                          = "v3.2";
    public static final String PRODUCT_DESCRIPTION              = PRODUCT + " is the driving force behind " + BRAND + ". " + PRODUCT + " can make " + Vergunning.CALLSPERHOUR_ENTERPRISE + " Phonecalls per Hour, " + Math.round(Vergunning.CALLSPERHOUR_ENTERPRISE * 16 / 1000) + " thousand people hear your message in 16 Hours. " + Math.round(Vergunning.CALLSPERHOUR_ENTERPRISE * 24 * 365 / 1000000) + " million calls a year (non stop!)";
    public static final String WEBLINK                          = "https://sites.google.com/site/voipstorm2/";
    public static final String REQUEST_VERGUNNINGLINK           = "https://sites.google.com/site/voipstorm2/";
    private static final String VERGUNNINGTOEKENNERTOEGANG      = "IsNwtNp4L";
    public static final String WARNING                          = "Please use VoipStorm software carefully, responsibly and according your country's legislation.";
    public static final String COPYRIGHT                        = "© " + Calendar.getInstance().get(Calendar.YEAR);
    public static final String AUTHOR                           = "Ron de Jong";

    private boolean             debugging = false; // Check Object Weblog() for startup delays
    private final static String dataDir= "data/";;
    private final String        xmlVergunningDir= dataDir + "license/";;
    private String              xmlFileName;
    private String              xmlFileBase;
    private String              xmlFileExtention;
    private boolean             vergunningLoaded;
    private boolean             vergunningOrderInProgress;
    private String              activationCodeFromFile;
    private String              vergunningCodeFromFile;
    private String              vergunningType;
    private Calendar            vergunningStartCalendar;
    private Calendar            vergunningEndCalendar;
    private Calendar            systemTimeCalendar;
//    private Calendar            ntpTimeCalendar;
//    private NTPDate             ntpDate;
    private String              vergunningPeriod;
    private String              activationCodeFromSystem;
    private String              vergunningCodeFromSystem;
    private boolean             vergunningValid;
    private int                 phoneLines;
    private int                 callsPerHour;
    private int                 maxCalls;
    private int                 destinationDigits;
    private String[]            status;
    private InetAddress         myIP;
//    private NetworkInterface    networkInterfaceList;
    private byte[]              myBytes;
    private String              vergunningInvalidReason = "";
    private String              vergunningInvalidAdvise = "";

    private String              output = "";
    private String              totOutput = "";
    private NetworkInterface    networkInterface;
    private Enumeration         networkInterfaceList;

    DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = null;
    Document xmlDocument = null;

    public Vergunning()
    {
        xmlFileName                 = "";
        xmlFileBase                 = "license";
        xmlFileExtention            = ".xml";
        activationCodeFromFile      = "";
        vergunningCodeFromFile         = "";
        status                      = new String[2];
        vergunningLoaded            = true;
        vergunningValid             = true;
        vergunningOrderInProgress   = false;
        vergunningType              = "Enterprise";
        vergunningPeriod            = "";
        phoneLines                  = PHONELINES_ENTERPRISE;
        callsPerHour                = CALLSPERHOUR_ENTERPRISE;
//        callsPerHour                = 0;
        maxCalls                    = MAXCALLS_ENTERPRISE;
        destinationDigits           = DESTINATIONDIGITS_ENTERPRISE;

//        status[0] = "0"; status[1]  = "";
//        status = loadVergunning();
//        if (status[0].equals("0"))
//        {
//            vergunningLoaded = true;
//        }
//        else
//        {
//            vergunningLoaded = false;
//        }
        vergunningStartCalendar = Calendar.getInstance();
        vergunningEndCalendar = Calendar.getInstance();
        systemTimeCalendar = Calendar.getInstance();
//        ntpTimeCalendar = Calendar.getInstance();
//
//        ntpDate = new NTPDate();
    }

    // a full Constructor

    /**
     *
     * @param activationCodeParam
     * @param vergunningCodeParam
     */
        public Vergunning(
                            String  activationCodeParam,
                            String  vergunningCodeParam
                        )
                        {
                            activationCodeFromFile  = activationCodeParam;
                            vergunningCodeFromFile     = vergunningCodeParam;
                        }

    /**
     *
     * @return
     */
    public String[] createVergunning()
    {
        status[0] = "0"; status[1] = "";
        setActivationCode("");
        setVergunningCode("");
        return status;
    }

    /**
     *
     * @return
     */
    public String[] loadVergunning() // Loads xmlfile content into attributes
    {
        vergunningLoaded = false;
        status[0] = "0"; status[1] = "";
        xmlFileName = xmlVergunningDir + xmlFileBase + xmlFileExtention;

        // Get the configuration from file
        builderFactory = DocumentBuilderFactory.newInstance();
        builder = null;
        xmlDocument = null;
        try { builder = builderFactory.newDocumentBuilder(); }
        catch (ParserConfigurationException error) { status[0] = "1"; status[1] = "loadLicense Error: builder = builderFactory.newDocumentBuilder(): ParserConfigurationException: " + error.getMessage(); return status;}

        try { xmlDocument = builder.parse(xmlFileName); }
        catch (SAXException error) { status[0] = "1"; status[1] = "loadLicense Error: xmlDocument = builder.parse(xmlFile): SAXException: " + error.getMessage(); return status; }
        catch (IOException error) { status[0] = "1"; status[1] = "loadLicense Error: xmlDocument = builder.parse(xmlFile): IOException: " + error.getMessage(); return status; }

        //set up a transformer
        TransformerFactory transfac = TransformerFactory.newInstance();
        Transformer trans = null; try { trans = transfac.newTransformer(); }
        catch (TransformerConfigurationException error) { status[0] = "1"; status[1] = "loadLicense Error: trans = transfac.newTransformer(): TransformerConfigurationException: " + error.getMessage(); return status; }
        trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        trans.setOutputProperty(OutputKeys.INDENT, "yes");

        //create string from xml tree
        StringWriter stringWriter = new StringWriter();
        StreamResult result = new StreamResult(stringWriter);
        DOMSource source = new DOMSource(xmlDocument);
        try { trans.transform(source, result); } catch (TransformerException error) { status[0] = "1"; status[1] = "loadLicense Error: trans.transform(source, result): TransformerException: " + error.getMessage(); return status; }
        String xmlString = stringWriter.toString();
        //System.out.println("loadVergunning: DOM String\n" + xmlString);

        // Now we have a DOM Tree and a DOM String, now let's turn it into a License Object

        String nodeName, nodeValue;
        Node node = xmlDocument.getFirstChild(); // softphone license
        NodeList nodelist = node.getChildNodes(); // all the children that form the configuration nodes

        int nodelistcount = nodelist.getLength();
        for (int i = 1; i < nodelistcount; i = i + 2)
        {
            //System.out.println("nodelist item: " + i + " = " + nodelist.item(i).getNodeName() + ": " + nodelist.item(i).getTextContent());
            nodeName = nodelist.item(i).getNodeName();
            nodeValue = nodelist.item(i).getTextContent();

            if      ( nodeName.equals("activationCode"))   { setActivationCode      (nodeValue);}
            else if ( nodeName.equals("licenseCode"))      { setVergunningCode         (nodeValue);}
        }
        vergunningLoaded = true;

        //System.out.println("loadVergunning myConfig.toString()\n" + myVergunning.toString());
        return status;
    }

    /**
     *
     * @return
     */
    public String[] saveVergunning() // Saves attributes to xmlfile
    {
        status[0] = "0"; status[1] = "";

        // Copy vergunning from UserInterface to this SoftPhone instance

        xmlFileName = xmlVergunningDir + xmlFileBase + xmlFileExtention;

        //Build a DOM from the configuration
        Element root, child;
        Text text;

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;
        try { docBuilder = documentBuilderFactory.newDocumentBuilder(); }
        catch (ParserConfigurationException error) { status[0] = "1"; status[1] = "saveLicense Error: docBuilder = documentBuilderFactory.newDocumentBuilder(): ParserConfigurationException: " + error.getMessage(); return status; }

        //Creating the XML tree
        xmlDocument = docBuilder.newDocument();

        //create the root element and add it to the document
        root = xmlDocument.createElement("license");
        xmlDocument.appendChild(root);

        //create a comment and put it in the root element
        Comment comment = xmlDocument.createComment("VoipStorm License");
        root.appendChild(comment);

        //create child elements, possibly add an attribute, and add to root
        child = xmlDocument.createElement("activationCode");   text = xmlDocument.createTextNode(getActivationCode()); child.appendChild(text); root.appendChild(child);
        child = xmlDocument.createElement("licenseCode");      text = xmlDocument.createTextNode(getVergunningCode());    child.appendChild(text); root.appendChild(child);
        //child.setAttribute("name", "value"); // attributes not (yet) needed

        //Save the DOM to file

        //set up a transformer
        TransformerFactory transfac = TransformerFactory.newInstance();
        Transformer trans = null; try { trans = transfac.newTransformer(); }
        catch (TransformerConfigurationException error) { status[0] = "1"; status[1] = "saveLicense Error: trans = transfac.newTransformer(): TransformerConfigurationException: " + error.getMessage(); return status; }
        trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        trans.setOutputProperty(OutputKeys.INDENT, "yes");

        //create string from xml tree
        StringWriter stringWriter = new StringWriter();
        StreamResult result = new StreamResult(stringWriter);
        DOMSource source = new DOMSource(xmlDocument);
        try { trans.transform(source, result); } catch (TransformerException error) { status[0] = "1"; status[1] = "saveLicense Error: trans.transform(source, result): TransformerException: " + error.getMessage(); return status; }
        String xmlString = stringWriter.toString();
        //System.out.println("saveVergunning DOM String: " + xmlString);

        // DOMString is ready, now save it to file
        FileWriter fileWriter = null;
        File fileToWrite = new File(xmlFileName);
        try { fileWriter = new FileWriter(fileToWrite); } catch (IOException error) { status[0] = "1"; status[1] = "saveLicense Error: fileWriter = new FileWriter(fileToWrite): IOException: " + error.getMessage(); return status; }
        try { fileWriter.write(xmlString); } catch (IOException error) { status[0] = "1"; status[1] = "saveLicense Error: fileWriter.write(xmlString): IOException: " + error.getMessage(); return status; }
        try { fileWriter.flush(); } catch (IOException error) { status[0] = "1"; status[1] = "saveLicense Error: fileWriter.flush(): IOException: " + error.getMessage(); return status; }
        return status;
    }

    /**
     *
     * @return
     */
    public boolean controleerVergunning()
    {
        status = loadVergunning();
        if (status[0].equals("0"))
        {
            vergunningLoaded = true;

            // Split up the activationCode from file to determine vergunning type, date and duration
//            System.out.println("AC" + activationCodeFromFile);

            String[] activationCodeField = new String[10];
            activationCodeField = activationCodeFromFile.split("-");

            // Current Date & Time gets detemined
            systemTimeCalendar = Calendar.getInstance();

//            status[0] = "0"; status[1] = ""; status = ntpDate.synchronize();
//            if ( status[0].equals("0") )
//            {
//                ntpTimeCalendar.setTimeInMillis(ntpDate.getTime());
//            }
//            else
//            {
//                System.out.println("Internet Time Access issue!!! make sure VoipStorm has Internet Access!");
//                vergunningValid = false;
//                return vergunningValid;
//            }

            if (debugging)
            {
                System.out.println("systemTimeCalendar:   " + String.format("%04d", systemTimeCalendar.get(Calendar.YEAR)) + "-" + String.format("%02d", (systemTimeCalendar.get(Calendar.MONTH))) + "-" + String.format("%02d", systemTimeCalendar.get(Calendar.DAY_OF_MONTH)) + " " + String.format("%02d", systemTimeCalendar.get(Calendar.HOUR_OF_DAY)) + ":" + String.format("%02d", systemTimeCalendar.get(Calendar.MINUTE)) + ":" + String.format("%02d", systemTimeCalendar.get(Calendar.SECOND)));
//                System.out.println("ntpTimeCalendar:      " + String.format("%04d", ntpTimeCalendar.get(Calendar.YEAR)) + "-" + String.format("%02d", (ntpTimeCalendar.get(Calendar.MONTH))) + "-" + String.format("%02d", ntpTimeCalendar.get(Calendar.DAY_OF_MONTH)) + " " + String.format("%02d", ntpTimeCalendar.get(Calendar.HOUR_OF_DAY)) + ":" + String.format("%02d", ntpTimeCalendar.get(Calendar.MINUTE)) + ":" + String.format("%02d", ntpTimeCalendar.get(Calendar.SECOND)));
//                System.out.println("diff:      " + Long.toString(systemTimeCalendar.getTimeInMillis() - ntpTimeCalendar.getTimeInMillis()));
            }

            // vergunning Start Date gets determined
            vergunningStartCalendar = Calendar.getInstance();
            vergunningStartCalendar.set(Integer.parseInt(activationCodeField[1]), (Integer.parseInt(activationCodeField[2])-1), Integer.parseInt(activationCodeField[3]));
            vergunningStartCalendar.set(Calendar.HOUR_OF_DAY, 0); vergunningStartCalendar.set(Calendar.MINUTE, 0); vergunningStartCalendar.set(Calendar.SECOND, 0);

            // vergunning Type gets determined
            vergunningType = activationCodeField[0];
            if      ( vergunningType.equals("Demo"))		{ phoneLines = PHONELINES_DEMO;         callsPerHour = CALLSPERHOUR_DEMO;           maxCalls = MAXCALLS_DEMO;           destinationDigits = DESTINATIONDIGITS_DEMO; }
            else if ( vergunningType.equals("Standard"))	{ phoneLines = PHONELINES_STANDARD;     callsPerHour = CALLSPERHOUR_STANDARD;       maxCalls = MAXCALLS_STANDARD;       destinationDigits = DESTINATIONDIGITS_STANDARD; }
            else if ( vergunningType.equals("Professional"))	{ phoneLines = PHONELINES_PROFESSIONAL; callsPerHour = CALLSPERHOUR_PROFESSIONAL;   maxCalls = MAXCALLS_PROFESSIONAL;   destinationDigits = DESTINATIONDIGITS_PROFESSIONAL; }
            else if ( vergunningType.equals("Enterprise"))	{ phoneLines = PHONELINES_ENTERPRISE;   callsPerHour = CALLSPERHOUR_ENTERPRISE;     maxCalls = MAXCALLS_ENTERPRISE;     destinationDigits = DESTINATIONDIGITS_ENTERPRISE; }
	    else						{ phoneLines = PHONELINES_ENTERPRISE;   callsPerHour = CALLSPERHOUR_ENTERPRISE;     maxCalls = MAXCALLS_ENTERPRISE;     destinationDigits = DESTINATIONDIGITS_ENTERPRISE; }

            // vergunning Period and therefore vergunning End Date get determined
            vergunningEndCalendar = Calendar.getInstance();
            vergunningEndCalendar.setTimeInMillis(vergunningStartCalendar.getTimeInMillis());
            vergunningEndCalendar.set(Calendar.HOUR_OF_DAY, 0); vergunningEndCalendar.set(Calendar.MINUTE, 0); vergunningEndCalendar.set(Calendar.SECOND, 0);
            vergunningPeriod   = activationCodeField[4];
            if      ( vergunningPeriod.equals("Day") )     { vergunningEndCalendar.add(Calendar.DAY_OF_YEAR, 1); }
            else if ( vergunningPeriod.equals("Week") )    { vergunningEndCalendar.add(Calendar.WEEK_OF_YEAR, 1); }
            else if ( vergunningPeriod.equals("Month") )   { vergunningEndCalendar.add(Calendar.MONTH, 1); }
            else if ( vergunningPeriod.equals("Year") )    { vergunningEndCalendar.add(Calendar.YEAR, 1); }
            else					   { vergunningEndCalendar.add(Calendar.YEAR, 100); }

            // vergunning hardware id gets determined
            status = getAK();
            if (status[0].equals("0"))
            {
                String activationCodeKeyString = null;
                
                activationCodeKeyString = status[1];
                activationCodeFromSystem =  vergunningType + "-" +
                                        String.format("%04d", vergunningStartCalendar.get(Calendar.YEAR)) + "-" +
                                        String.format("%02d", ((vergunningStartCalendar.get(Calendar.MONTH))) + 1) + "-" +
                                        String.format("%02d", vergunningStartCalendar.get(Calendar.DAY_OF_MONTH)) + "-" +
                                        vergunningPeriod + "-" +
                                        activationCodeKeyString;
                vergunningCodeFromSystem = MD5Converter.getMD5SumFromString(activationCodeFromSystem + VERGUNNINGTOEKENNERTOEGANG);

                if (debugging)
                {
                    System.out.println();
                    System.out.println("ACFF: " + activationCodeFromFile);
                    System.out.println("ACFS: " + activationCodeFromSystem);
                    System.out.println();
                    System.out.println("LCFF: " + vergunningCodeFromFile);
                    System.out.println("LCFS: " + vergunningCodeFromSystem);
                }

                vergunningValid = true;

//                if      ((systemTimeCalendar.getTimeInMillis() - ntpTimeCalendar.getTimeInMillis()) < -600000)   { vergunningValid = false; vergunningInvalidReason = "Time in Past";         vergunningInvalidAdvise = "Please correct your System Time"; }
//                else if ((systemTimeCalendar.getTimeInMillis() - ntpTimeCalendar.getTimeInMillis()) > 600000)    { vergunningValid = false; vergunningInvalidReason = "Time in Future";       vergunningInvalidAdvise = "Please correct your System Time"; }
                if (vergunningCodeFromSystem == null)                                                       { vergunningValid = false; vergunningInvalidReason = "LicenseCode Missing";  vergunningInvalidAdvise = "Please contact " + BRAND; }
                else if (vergunningCodeFromSystem.length() == 0)                                                 { vergunningValid = false; vergunningInvalidReason = "LicenseCode Missing";  vergunningInvalidAdvise = "Please contact " + BRAND; }
                else if ( ! vergunningCodeFromSystem.equals(vergunningCodeFromFile))                             { vergunningValid = false; vergunningInvalidReason = "LicenseCode Invalid";  vergunningInvalidAdvise = "Please fill in correct LicenseCode"; }
                else if (systemTimeCalendar.before(vergunningStartCalendar))                                     { vergunningValid = false; vergunningInvalidReason = "License in Future";    vergunningInvalidAdvise = "Please wait until LicenseStart Date"; }
                else if (systemTimeCalendar.after(vergunningEndCalendar))                                        { vergunningValid = false; vergunningInvalidReason = "License Expired";      vergunningInvalidAdvise = "Please renew your LicenseCode"; }

                if (vergunningValid)
                {
                    if (debugging)
                    {
                        System.out.println();
                        System.out.println("licenseStartCalendar: " + String.format("%04d", vergunningStartCalendar.get(Calendar.YEAR)) + "-" + String.format("%02d", (vergunningStartCalendar.get(Calendar.MONTH))) + "-" + String.format("%02d", vergunningStartCalendar.get(Calendar.DAY_OF_MONTH)) + " " + String.format("%02d", vergunningStartCalendar.get(Calendar.HOUR_OF_DAY)) + ":" + String.format("%02d", vergunningStartCalendar.get(Calendar.MINUTE)) + ":" + String.format("%02d", vergunningStartCalendar.get(Calendar.SECOND)));
                        System.out.println("systemTimeCalendar:   " + String.format("%04d", systemTimeCalendar.get(Calendar.YEAR)) + "-" + String.format("%02d", (systemTimeCalendar.get(Calendar.MONTH))) + "-" + String.format("%02d", systemTimeCalendar.get(Calendar.DAY_OF_MONTH)) + " " + String.format("%02d", systemTimeCalendar.get(Calendar.HOUR_OF_DAY)) + ":" + String.format("%02d", systemTimeCalendar.get(Calendar.MINUTE)) + ":" + String.format("%02d", systemTimeCalendar.get(Calendar.SECOND)));
//                        System.out.println("ntpTimeCalendar:      " + String.format("%04d", ntpTimeCalendar.get(Calendar.YEAR)) + "-" + String.format("%02d", (ntpTimeCalendar.get(Calendar.MONTH))) + "-" + String.format("%02d", ntpTimeCalendar.get(Calendar.DAY_OF_MONTH)) + " " + String.format("%02d", ntpTimeCalendar.get(Calendar.HOUR_OF_DAY)) + ":" + String.format("%02d", ntpTimeCalendar.get(Calendar.MINUTE)) + ":" + String.format("%02d", ntpTimeCalendar.get(Calendar.SECOND)));
                        System.out.println("licenseEndCalendar:   " + String.format("%04d", vergunningEndCalendar.get(Calendar.YEAR)) + "-" + String.format("%02d", (vergunningEndCalendar.get(Calendar.MONTH))) + "-" + String.format("%02d", vergunningEndCalendar.get(Calendar.DAY_OF_MONTH)) + " " + String.format("%02d", vergunningEndCalendar.get(Calendar.HOUR_OF_DAY)) + ":" + String.format("%02d", vergunningEndCalendar.get(Calendar.MINUTE)) + ":" + String.format("%02d", vergunningEndCalendar.get(Calendar.SECOND)));
                    }
                }
                else
                {
                    if (debugging)
                    {
                        System.out.println();
                        System.out.println(vergunningInvalidReason);
                        System.out.println();
                        System.out.println("licenseStartCalendar: " + String.format("%04d", vergunningStartCalendar.get(Calendar.YEAR)) + "-" + String.format("%02d", (vergunningStartCalendar.get(Calendar.MONTH))) + "-" + String.format("%02d", vergunningStartCalendar.get(Calendar.DAY_OF_MONTH)) + " " + String.format("%02d", vergunningStartCalendar.get(Calendar.HOUR_OF_DAY)) + ":" + String.format("%02d", vergunningStartCalendar.get(Calendar.MINUTE)) + ":" + String.format("%02d", vergunningStartCalendar.get(Calendar.SECOND)));
                        System.out.println("systemTimeCalendar:   " + String.format("%04d", systemTimeCalendar.get(Calendar.YEAR)) + "-" + String.format("%02d", (systemTimeCalendar.get(Calendar.MONTH))) + "-" + String.format("%02d", systemTimeCalendar.get(Calendar.DAY_OF_MONTH)) + " " + String.format("%02d", systemTimeCalendar.get(Calendar.HOUR_OF_DAY)) + ":" + String.format("%02d", systemTimeCalendar.get(Calendar.MINUTE)) + ":" + String.format("%02d", systemTimeCalendar.get(Calendar.SECOND)));
//                        System.out.println("ntpTimeCalendar:      " + String.format("%04d", ntpTimeCalendar.get(Calendar.YEAR)) + "-" + String.format("%02d", (ntpTimeCalendar.get(Calendar.MONTH))) + "-" + String.format("%02d", ntpTimeCalendar.get(Calendar.DAY_OF_MONTH)) + " " + String.format("%02d", ntpTimeCalendar.get(Calendar.HOUR_OF_DAY)) + ":" + String.format("%02d", ntpTimeCalendar.get(Calendar.MINUTE)) + ":" + String.format("%02d", ntpTimeCalendar.get(Calendar.SECOND)));
                        System.out.println("licenseEndCalendar:   " + String.format("%04d", vergunningEndCalendar.get(Calendar.YEAR)) + "-" + String.format("%02d", (vergunningEndCalendar.get(Calendar.MONTH))) + "-" + String.format("%02d", vergunningEndCalendar.get(Calendar.DAY_OF_MONTH)) + " " + String.format("%02d", vergunningEndCalendar.get(Calendar.HOUR_OF_DAY)) + ":" + String.format("%02d", vergunningEndCalendar.get(Calendar.MINUTE)) + ":" + String.format("%02d", vergunningEndCalendar.get(Calendar.SECOND)));
                    }
                }
            }
        }
        else
        {
//            vergunningLoaded = false;
            vergunningLoaded = true;
        }

        return vergunningValid;
    }

    // This actually is meant as the HW Id key behind the vergunning part

    /**
     *
     * @return
     */
        public String[] getAK() // This is part of the AK
    {
        try // This is part of the AK
        {
            status[0] = "0"; status[1] = "";
            output = "";
            totOutput = "";
            networkInterfaceList = NetworkInterface.getNetworkInterfaces();
            for (networkInterfaceList = NetworkInterface.getNetworkInterfaces(); networkInterfaceList.hasMoreElements(); )
            {
                networkInterface = (NetworkInterface) networkInterfaceList.nextElement();
//                if (debug) { dcmDesktop.log("Networkinterface: " + networkInterface.getName(),true,true,true); } // Use this to troubleshoot
                if ( (networkInterface.isUp()) && (!networkInterface.isLoopback()) && (!networkInterface.isVirtual()))
                {                
//                    try { myBytes = new byte[networkInterface.getHardwareAddress().length]; } catch (SocketException ex) { System.out.println("myBytes = new byte[netif.getHardwareAddress().length]: " + ex.getMessage()); }
                    try { myBytes = networkInterface.getHardwareAddress();} catch (SocketException ex) { System.out.println("myBytes = netif.getHardwareAddress(): " + ex.getMessage()); }
                }

                if ( (networkInterface.isUp()) && (!networkInterface.isLoopback()) && (!networkInterface.isVirtual()) && (myBytes != null) && (myBytes.length >0 ) )
                {
                    for(Byte myByte : myBytes)
                    {
                        int highNibble = 0;
                        int high2lowNibble = 0;
                        int lowNibble = 0;

                        if (( myByte != null ) && (myByte >= 0))
                        {
                            highNibble = (myByte & 0xF0); high2lowNibble = highNibble >>> 4;
                            lowNibble =  (myByte & 0x0F);
//                            if (debug) { dcmDesktop.log(Integer.toHexString(high2lowNibble)+Integer.toHexString(lowNibble),true,true,true); } // Use this to troubleshoot
                        }
                        output += Integer.toHexString(high2lowNibble) + Integer.toHexString(lowNibble); // M4C Addr
                    }
                    totOutput += output;                
    //                System.out.println("Output: " + output);                         
                }
            }
            
//            if (debug) { dcManager.log("Key: " + MD5Converter.getMD5SumFromString(totOutput),true,true,true); }
            
            status[1] += MD5Converter.getMD5SumFromString(totOutput);
        } catch (SocketException ex) { System.out.println("Error: UnknownHostException: Vergunning.getAK()): NetworkInterface.getByInetAddress(inetAddress): " + ex.getMessage()); }
        return status;
    }


    
//    public String[] getAK()
//    {
//	status[0] = "0"; status[1] = "";
//        String output = "";
//
//        try { myIP = InetAddress.getLocalHost(); } catch( UnknownHostException error) { }
//        try { networkInterfaceList = NetworkInterface.getByInetAddress(myIP); } catch (SocketException ex) { }
//        try { myBytes = new byte[networkInterfaceList.getHardwareAddress().length]; } catch (SocketException ex) { status[0] = "1"; status[1] = "myBytes = new byte[netif.getHardwareAddress().length]: " + ex.getMessage(); return status; }
//        try { myBytes = networkInterfaceList.getHardwareAddress();} catch (SocketException ex) { status[0] = "1"; status[1] = "myBytes = netif.getHardwareAddress(): " + ex.getMessage(); return status; }
//
//        for(Byte myByte : myBytes)
//        {
//            int highNibble = 0;
//            int high2lowNibble = 0;
//            int lowNibble = 0;
//
//            if (( myByte != null ) && (myByte >= 0))
//            {
//                highNibble = (myByte & 0xF0); high2lowNibble = highNibble >>> 4;
//                lowNibble =  (myByte & 0x0F);
////                System.out.print(Integer.toHexString(high2lowNibble));
////                System.out.println(Integer.toHexString(lowNibble));
//            }
//            output += Integer.toHexString(high2lowNibble) + Integer.toHexString(lowNibble);
//        }
//
//        status[1] += MD5Converter.getMD5SumFromString(output);
//        return status;
//    }

//    public String[] syncNTPCalendar()
//    {
//        status[0] = "0"; status[1] = ""; status = ntpDate.synchronize();
//        if ( status[0].equals("0") ) { ntpTimeCalendar.setTimeInMillis(ntpDate.getTime()); } else { return status; }
//
//        if (debugging)
//        {
//            System.out.println("systemTimeCalendar:   " + String.format("%04d", systemTimeCalendar.get(Calendar.YEAR)) + "-" + String.format("%02d", (systemTimeCalendar.get(Calendar.MONTH))) + "-" + String.format("%02d", systemTimeCalendar.get(Calendar.DAY_OF_MONTH)) + " " + String.format("%02d", systemTimeCalendar.get(Calendar.HOUR_OF_DAY)) + ":" + String.format("%02d", systemTimeCalendar.get(Calendar.MINUTE)) + ":" + String.format("%02d", systemTimeCalendar.get(Calendar.SECOND)));
//            System.out.println("ntpTimeCalendar:      " + String.format("%04d", ntpTimeCalendar.get(Calendar.YEAR)) + "-" + String.format("%02d", (ntpTimeCalendar.get(Calendar.MONTH))) + "-" + String.format("%02d", ntpTimeCalendar.get(Calendar.DAY_OF_MONTH)) + " " + String.format("%02d", ntpTimeCalendar.get(Calendar.HOUR_OF_DAY)) + ":" + String.format("%02d", ntpTimeCalendar.get(Calendar.MINUTE)) + ":" + String.format("%02d", ntpTimeCalendar.get(Calendar.SECOND)));
//            System.out.println("diff:      " + Long.toString(systemTimeCalendar.getTimeInMillis() - ntpTimeCalendar.getTimeInMillis()));
//        }
//        return status;
//    }

    // Just the getters and setters

    public boolean  isValid                     ()                             {return true;} // used to be return vergunningValid
    public String   getVergunningInvalidReason  ()                             {return vergunningInvalidReason;}
    public String   getVergunningInvalidAdvise  ()                             {return vergunningInvalidAdvise;}
    public boolean  vergunningOrderInProgress   ()                             {return vergunningOrderInProgress;}
    public String   getActivationCode           ()                             {return activationCodeFromFile;}
    public String   getVergunningCode           ()                             {return vergunningCodeFromFile;}
    public String   getVergunningType           ()                             {return vergunningType;}
    public Calendar getVergunningStartDate      ()                             {return vergunningStartCalendar;}
    public Calendar getVergunningEndDate        ()                             {return vergunningEndCalendar;}
    public String   getVergunningPeriod         ()                             {return vergunningPeriod;}
    public int      getPhoneLines               ()                             {return phoneLines;}
    public int      getCallsPerHour             ()                             {return callsPerHour;}
    public int      getMaxCalls                 ()                             {return maxCalls;}
    public int      getDestinationDigits        ()                             {return destinationDigits;}
    public int      getOutboundBurstRate        ()
    {
        double outboundBurstRate = 0;
//        System.out.println("callsPerHour: " + Integer.toString(callsPerHour));
        if (callsPerHour > 0) { outboundBurstRate = Math.round((double)1/((double)callsPerHour / (double)3600)*(double)1000); }
//        System.out.println("outboundBurstRate: " + Float.toString((float) outboundBurstRate));
        return (int) Math.round(outboundBurstRate);
    }

    public void setActivationCode               (String   activationCodeParam)              {activationCodeFromFile        = activationCodeParam;}
    public void setVergunningCode               (String   vergunningCodeParam)              {vergunningCodeFromFile        = vergunningCodeParam;}
    public void setVergunningValid              (boolean  vergunningValidParam)             {vergunningValid               = vergunningValidParam;}
    public void setVergunningOrderInProgress    (boolean  vergunningOrderInProgressParam)   {vergunningOrderInProgress     = vergunningOrderInProgressParam;}
    public void setVergunningType               (String   vergunningTypeParam)              {vergunningType                = vergunningTypeParam;}
    public void setVergunningStartDate          (Calendar vergunningStartCalendarParam)     {vergunningStartCalendar       = vergunningStartCalendarParam;}
    public void setVergunningEndDate            (Calendar vergunningEndCalendarParam)       {vergunningEndCalendar         = vergunningEndCalendarParam;}
    public void setVergunningPeriod             (String   vergunningPeriodParam)            {vergunningPeriod              = vergunningPeriodParam;}
    public void setPhoneLines                   (int      phoneLinesParam)                  {phoneLines                    = phoneLinesParam;}
    public void setCallsPerHour                 (int      callsPerHourParam)                {callsPerHour                  = callsPerHourParam;}
    public void setMaxCalls                     (int      maxCallsParam)                    {maxCalls                      = maxCallsParam;}
    public void setDestinationDigits            (int      destinationDigitsParam)           {destinationDigits             = destinationDigitsParam;}

    @Override
    public String toString()
    {
        String output = null;
        output  = "activationCode: "        + getActivationCode()  + "\n";
        output += "licenseCode: "           + getVergunningCode()     + "\n";

        return output;
    }

    @Override
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }
}

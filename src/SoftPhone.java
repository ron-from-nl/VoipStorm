import data.SpeakerData;
import data.DisplayData;
import data.Configuration;
import data.Destination;
import gov.nist.javax.sip.header.*;
import java.text.ParseException;
import java.util.*;
import java.net.*;
import javax.media.NoDataSourceException;
import javax.media.NoProcessorException;
import javax.media.format.UnsupportedFormatException;
import javax.sip.*;
import javax.sip.message.*;
import javax.sip.header.*;
import javax.sip.address.*;
import java.io.*;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ExecutorService;

/**
 *
 * @author ron
 */
public class SoftPhone extends Thread implements SipListener
{
    private static final String THISPRODUCT	    = "SoftPhone";
    private static final String VERSION		    = "v1.0";
    
    private String sipDestination;
//    private String clientPort;
//    private String method;
//    private String username;
//    private String password;
//    private String aor;
//    private String contact;
    private String aor;      // sip:usr@mydomain.com
    private String from;     // sip:usr@mydomain.com
    private String to;       // sip:usr@hisdomain.com
    private String via;      // Via:SIP/2.0/UDP 1.2.3.4:5060; branch=a4d5c6g7h6  (pub ip addr)
    private String contact;  // sip:usr@1.2.3.4 (pub ip)
//    private String sipStackName;

    /**
     *
     */
    public int sipstate;

    /**
     *
     */
    public static final int SIPSTATE_OFF                        = 0;

    /**
     *
     */
    public static final int SIPSTATE_ON                         = 1;

    /**
     *
     */
    public static final int SIPSTATE_IDLE                       = 2;

    /**
     *
     */
    public static final int SIPSTATE_WAIT_CONNECT               = 3; // Sent out invite

    /**
     *
     */
    public static final int SIPSTATE_WAIT_PROV                  = 4; // Received Trying

    /**
     *
     */
    public static final int SIPSTATE_WAIT_FINAL                 = 5; // Received Ringing

    /**
     *
     */
    public static final int SIPSTATE_WAIT_ACK                   = 6; // Happens in createResponse picking up phone sending OK back

    /**
     *
     */
    public static final int SIPSTATE_RINGING                    = 7; // Happens in processResponse 100 - 179

    /**
     *
     */
    public static final int SIPSTATE_ESTABLISHED                = 8;

    /**
     *
     */
    public static final int SIPSTATE_TRANSITION_LOCALCANCEL     = 9;

    /**
     *
     */
    public static final int SIPSTATE_TRANSITION_REMOTECANCEL    = 10;

    /**
     *
     */
    public static final int SIPSTATE_TRANSITION_LOCALBUSY       = 11;

    /**
     *
     */
    public static final int SIPSTATE_TRANSITION_REMOTEBUSY      = 12;

    /**
     *
     */
    public static final int SIPSTATE_TRANSITION_LOCALBYE        = 13;

    /**
     *
     */
    public static final int SIPSTATE_TRANSITION_REMOTEBYE       = 14;

    /**
     *
     */
    public static final int SIPSTATE_TRANSITION_CONFIG          = 15;

    /**
     *
     */
    public static final String[] SIPSTATE_DESCRIPTION           = {"SIPSTATE_OFF","SIPSTATE_ON","SIPSTATE_IDLE","SIPSTATE_WAIT_PROV","SIPSTATE_WAIT_FINAL","SIPSTATE_WAIT_ACK","SIPSTATE_RINGING","SIPSTATE_ESTABLISHED","SIPSTATE_TRANSITION_LOCALCANCEL",
                                                                   "SIPSTATE_TRANSITION_REMOTECANCEL","SIPSTATE_TRANSITION_LOCALBUSY","SIPSTATE_TRANSITION_REMOTEBUSY","SIPSTATE_TRANSITION_LOCALBYE","SIPSTATE_TRANSITION_REMOTEBYE","SIPSTATE_TRANSITION_CONFIG"};

    /**
     *
     */
    public static final int SOFTPHONE_ACTIVITY_NORMAL           = 0;

    /**
     *
     */
    public static final int SOFTPHONE_ACTIVITY_REGISTRATION     = 1;

    /**
     *
     */
    public static final int SOFTPHONE_ACTIVITY_MAINTENANCE      = 2;

    /**
     *
     */
    public static final int SOFTPHONE_ACTIVITY_REFRESH          = 3;

    /**
     *
     */
    public static final int LOGINSTATE_UNREGISTERED             = 0;

    /**
     *
     */
    public static final int LOGINSTATE_REGISTERED               = 1;

    private       final int PRIORITY_LOW                        = 5;
    private       final int PRIORITY_HIGH                       = 9;

    private static final int    CALLING             = 0;
    private static final int    SCANNING            = 1;

    /**
     *
     */
    public int lastsipstate;

    /**
     *
     */
    public int loginstate; // UNREGISTERED, REGISTERING, REGISTERED

    /**
     *
     */
    public int lastloginstate; // UNREGISTERED, REGISTERING, REGISTERED

    /**
     *
     */
    public String availability = "Unavailable"; // UNREGISTERED, REGISTERING, REGISTERED

    /**
     *
     */
    public String registered = "Ready"; // UNREGISTERED, REGISTERING, REGISTERED

    /**
     *
     */
    public String unregistered = "Callout"; // UNREGISTERED, REGISTERING, REGISTERED

    /**
     *
     */
    public String off = "Off"; // UNREGISTERED, REGISTERING, REGISTERED
    private int localSIPPort;
    private int requestedClientAudioPort;
    private int assignedClientAudioPort;
    private int requestedServerAudioPort;
    private int assignedServerAudioPort;
    private String transport;
    private String udpTransport;
//    private String tcpTransport;
    private String inviteAckRequestBranch;
//    private String myFromHeaderTag;
//    private String myToHeaderTag;
    private String rinstance;
    boolean runThreadsAsDaemons = true;
    boolean isSIPAddress;

    // integers (primitive)
    private int ultraShortMessagePeriod;
    private int eyeBlinkMessagePeriod;
    private int shortMessagePeriod;

    /**
     *
     */
    public int registerRequestCounter           = 1; // Keep this one global for now !!!
    private int audioCodec                    = 3;
    private int videoPort                     = -1; // Means that video will not be used
    private int videoCodec                    = -1;
    private static int registerLoginTimeout;
    private int registerLogoutTimeout;

    MessageDigestAlgorithm messageDigestAlgorithm;

    // ByteArrays
    private SDPConvert                  sdpConvert; // Keep Global
    private byte[]                      offerSDPBytes;
    private byte[]                      answerSDPBytes;
    private SDPInfo                     offerSDPInfo; // Used by multiple methods
    private SDPInfo                     answerSDPInfo; // Keep global to

    // Properties
    private Properties sipStackProperties;

    // The big sip stuff
    private SipFactory sipFactory;
    private SipStack sipStack;
    private ListeningPoint udpListeningPoint;
//    private ListeningPoint tcpListeningPoint;

    private MessageFactory messageFactory;
    private HeaderFactory headerFactory;
    private AddressFactory addressFactory;
    private SipProvider sipProvider; // Certainly keep global, as our listening socket needs to survive different / all methods untill end of life / poweroff requested
    private InetAddress localIPAddress; // Keep Global
    private int serverSocketBackLog; // Keep Global

    // Addresses
//    private Address addressOfRecord;
    private Address destAddress;
    private Address toAddress;
    private Address fromAddress;
    private Address routeAddress;
    private Address registrarAddress;
    private Address privateContactAddress; // Keep this one global
    private Address publicContactAddress; // Keep this one global
    
    // Headers
    private RouteHeader                 routeHeader;
    private ContentTypeHeader           contentTypeHeader;
    private CallIdHeader                callIdHeader;
    private FromHeader                  fromHeader;
    private ToHeader                    toHeader;
    private ToHeader                    responseToHeader;
    private ViaHeader                   viaHeader;
    private ContactHeader               privateContactHeader; // Keep this one global for now !!!
    private ContactHeader               publicContactHeader; // Keep this one global for now !!!
    private CSeqHeader                  commandSequenceHeader;
    private ExpiresHeader               expiresHeader;
    private AuthorizationHeader         authorizationHeader;
    private ProxyAuthorizationHeader    proxyAuthorizationHeader;
    private MaxForwardsHeader           maxForwardsHeader;
    private UserAgentHeader             userAgentHeader;
    private AllowHeader                 allowHeader;

    private ArrayList                   viaHeaders = new ArrayList();
    private ArrayList                   userAgentList = new ArrayList();

    // Requests
    private Request                     request; // Keep this one global
    private Request                     ackRequest; // Keep this one global
    private Request                     originalRequest; // Keep global
    private Request                     registerRequest;

    private String                      destinationDisplay; // Keep Global
    private String                      filename; // Keep Global
    private int                         registerTimeout; // Keep Global
    private int                         retryInterval;
//    private int inviteTimeout;

    // Responses
    Response                            response;

    private javax.sip.address.URI       requestURI  = null; // Keep this one global !!!
    private String                      registerRequestBranch; // Keep Global
    private String                      inviteRequestBranch; // Keep Global
    private String                      byeRequestBranch; // Keep Global
    private String                      cancelRequestBranch; // Keep Global
    private String                      serverToTag; // Certainly keep Global

    private ClientTransaction           clientTransaction; // Keep Global cancelRequest is dependent on earlier instances elsewhere
    private ServerTransaction           serverTransaction; // Keep global to just like clientTransaction
    private Dialog                      dialog; // Keep global to

    /**
     *
     */
    public  SoundStreamer               soundStreamer; // Keep Global, through different methods the SoundStreamer is used switching between several (update) stages (Invite and Call acceptance in the processResponse method and enableMute method)
//    private VideoTool myVideoTool;

    private boolean                     debugging                   = false; // Certainly keep Global
    private int                         debugginglevel; // Keep Global

    /**
     *
     */
    public  boolean                     autoEndCall                 = false; // Keep Global, the constructor sets it and processRequest

    /**
     *
     */
    public  int                         autoRingingResponse; // Keep Global, the constructor sets it and processRequest
    private final int                   NONE = 0; // Keep Global, the constructor sets it and processRequest
    private final int                   ANSWER = 1; // Keep Global, the constructor sets it and processRequest
    private final int                   CANCEL = 2; // Keep Global, the constructor sets it and processRequest
    private final int                   RANDOM = 3; // Keep Global, the constructor sets it and processRequest
    private int                         autoRingingResponseDelay; // Keep Global, the constructor sets it and processRequest
    private int                         busyRatioPercentage          = 0; // Keep Global, the constructor sets it and processRequest
    private int                         inboundEndTimerDelay; // Keep Global, the constructor sets it and processRequest
//    private int                         outboundEndTimerDelay        = 0; // Keep Global, the constructor sets it and processRequest

    /**
     *
     */
        public  boolean                     audioIsMuted      = false; // Keep Global
    private UserInterface               userInterface1, userInterface2; // KEEP GLOBAL!!! userInterface1 = EPhoneGUI, userInterface2 = ECallCenterGUI
    private int                         softPhoneInstanceId; // KEEP GLOBAL!!!

    /**
     *
     */
    public  Configuration               configuration; // KEEP EVEN MORE GLOBAL!!!

    /**
     *
     */
    public  String                      activeLineNumber; // Keep Global, Tricky one, this is not spread accross several methods, but just one method that gets set differently on different occations, one occation sets it and another occation uses it (line1-power & saveConfig)
    private String                      fromHeaderAddress; // Keep Global
    private String                      toHeaderAddress; // Keep Global
    private DisplayData                 displayData; // Keep Global as we need the state of every individual Cell maintained through all the methods, similar to videomemory
    private SpeakerData                 speakerData; // This one is like displayData, imagine that while a bell or call tone rings, the enable / disabletone could go as well, without one interupting the other
    private boolean                     powerOffRequested           = false; // Keep Global (userInput sets and while processResponse (unregistered part) finishes it)
    private boolean                     keepRunning; // Keep Global accessed from anywhere
    private Timer                       inboundEndCallTimer;
    private Timer                       outboundEndCallTimer;
//    private int                         runcount                    = 0;
    private Destination                 destination;
    private boolean                     scan                        = false;
    private SoftPhone                   softPhoneReference;
    private boolean                     ePhoneGUIActive             = true;

    private final int                   LINE1BUTTON =               0;
    private final int                   LINE2BUTTON =               1;
    private final int                   SAVEBUTTON =                2;
    private final int                   REGISTERBUTTON =            3;
    private final int                   ANSWERBUTTON =              4;
    private final int                   CANCELBUTTON =              5;
    private final int                   RANDOMRINGRESPONSEBUTTON =  6;
    private final int                   ENDTIMERBUTTON =            7;
    private final int                   MUTEAUDIOBUTTON =           8;
    private final int                   DEBUGBUTTON =               9;
    private final int                   CALLBUTTON =                10;
    private final int                   ENDBUTTON =                 11;
    private final int                   RESTARTSOFTPHONEBUTTON =    12;
    private final int                   MAXFORWARDS =               70;

//    private ExecutorService             processResponseThreadPool;
//    private ExecutorService             processRequestThreadPool;

    /**
     *
     * @param userInterfaceParam
     * @param softPhoneInstanceIdParam
     * @param debuggingParam
     */
    
    public SoftPhone(UserInterface userInterfaceParam, int softPhoneInstanceIdParam, boolean debuggingParam) // No config parsed, so let's load config from disk
    {
        String[] status = new String[2];

//        processResponseThreadPool = Executors.newCachedThreadPool();
//        processRequestThreadPool = Executors.newCachedThreadPool();

        softPhoneReference                      = this;

//        sipStackName                            = "VoipStorm";
        userInterface1                          = userInterfaceParam;
        userInterface2                          = null;
        softPhoneInstanceId                     = softPhoneInstanceIdParam;
	ultraShortMessagePeriod                 = 0;
	eyeBlinkMessagePeriod                   = 100; // Default 250
	shortMessagePeriod                      = 200; // Default 500
        debugging                               = debuggingParam;
        debugginglevel                          = 0;
	status                                  = new String[2];
        configuration                           = new Configuration(); status = configuration.loadConfiguration("1");
//	System.out.println(myConfiguration.toString());
        messageDigestAlgorithm                  = new MessageDigestAlgorithm();
//        inviteTimeout = 300;
        offerSDPInfo                            = new SDPInfo();
        answerSDPInfo                           = new SDPInfo();
        sdpConvert                              = new SDPConvert();
//        soundStreamer                           = new SoundStreamer(userInterface1, softPhoneInstanceId);
        try { localIPAddress                         = InetAddress.getLocalHost(); } catch( UnknownHostException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: InetAddress.getLocalHost(): " + error.getMessage()); }
	serverSocketBackLog                     = 1000; // Default: 100
        requestedClientAudioPort                = 0;
        assignedClientAudioPort                 = 0;
        requestedServerAudioPort                = 0;
        assignedServerAudioPort                 = 0;
        transport                               = "udp";
        udpTransport                            = "udp";
//        tcpTransport                            = "tcp";

        availability                            = off; lastsipstate = sipstate; sipstate = SIPSTATE_ON;

        registerLoginTimeout                    = 3600;
        registerLogoutTimeout                   = 0;
        retryInterval                           = 10;
        destination                             = new Destination();
        autoRingingResponse                     = NONE;

//        if (ePhoneGUIActive)
//        {
            displayData                             = new DisplayData();
            displayData.setOnFlag(false);
            displayData.setPrimaryStatusCell("Phone Powered " + availability);
            displayData.setPrimaryStatusDetailsCell("");
            displayData.setSecondaryStatusCell("");
            displayData.setSecondaryStatusDetailsCell("");
            speakerData                             = new SpeakerData();
            userInterface1.phoneDisplay(displayData); if (userInterface2 != null) {userInterface2.phoneDisplay(displayData);}
//        }
        dispatchSipState(sipstate, lastsipstate, loginstate, SOFTPHONE_ACTIVITY_NORMAL, softPhoneInstanceId, destination);
        ePhoneGUIActive = true; // Turned on for new Call (CLI) Object
	keepRunning                             = true;
    }

    /**
     *
     * @param userInterfaceParam
     * @param softPhoneInstanceIdParam
     * @param debuggingParam
     * @param configurationParam
     * @throws CloneNotSupportedException
     */
    public SoftPhone(UserInterface userInterfaceParam, int softPhoneInstanceIdParam, boolean debuggingParam, Configuration configurationParam) throws CloneNotSupportedException // Config is parsed from UserInterface, so let's not load it from storage
    {
        this(userInterfaceParam, softPhoneInstanceIdParam, debuggingParam);
        configuration = new Configuration(); configuration = (Configuration) configurationParam.clone();
        ePhoneGUIActive = true; // When put to false then buttons aren't put to their actual state
    }

    /**
     *
     * @param buttonParam
     * @param userInputParam1
     * @param userInputParam2
     * @param userInputParam3
     * @return
     */
    public String[] userInput(int buttonParam, String userInputParam1, String userInputParam2, String userInputParam3)
    {
        String[] status = new String[2]; status[0] = "0"; status[1] = "";
        // Determine keyCode
        if      (( buttonParam == LINE1BUTTON) || ( buttonParam == LINE2BUTTON) )
        {
            activeLineNumber = userInputParam1;
            if ( userInputParam2.equals("1") ) // This param tells On or Off is required
            {
                if (userInputParam3.equals("0")) // Not ManagedByCallCenter
                {
                    // Load Config from file and set the config fields in the GUI
                    status = configuration.loadConfiguration(userInputParam1); // This param is a linenumber (confignumber) param
                    if (status[0].equals("1"))
                    {
                        userInterface1.showStatus("Error: loadConfiguration: " + status[1], true, true);
                        userInterface1.showStatus("Creating new configuration", true, true);
                        status[0] = "0"; status[1] = "";
                        status = configuration.createConfiguration();
                        if (status[0].equals("1"))
                        {
                            userInterface1.showStatus("Error: Creating configuration, please setup configuration manually!", true, true);
                        }
                        else
                        {
                            userInterface1.showStatus("Creating Configuration Successfull", true, true);
                            userInterface1.showStatus("Saving Configuration", true, true);
                            status[0] = "0"; status[1] = "";
                            status = configuration.saveConfiguration(userInputParam1);
                            if (status[0].equals("1"))
                            {
                                userInterface1.showStatus("Error: Saving Configuration: " + status[1] + " please contact your administrator!!", true, true);
                            }
                            else
                            {
                                userInterface1.showStatus("Saved new Configuration, please check new Configuration", true, true);
                                status[0] = "1"; status[1] = "Please restart Line"; return status;
                            }
                        }
                    }
                    else // Configuration Completed
                    {
                        status = startListener(configuration.getClientPort());
                        if ( status[0].equals("1") )
                        {
                            userInterface1.showStatus("Power Failure: " + status[1] + " (Configuration Cause?)", true, true);

                            if (ePhoneGUIActive)
                            {
                                displayData.setPrimaryStatusCell("Line" + activeLineNumber + " Failure");
                                displayData.setPrimaryStatusDetailsCell("");
                                displayData.setSecondaryStatusCell("");
                                displayData.setSecondaryStatusDetailsCell("");
                                userInterface1.phoneDisplay(displayData); if (userInterface2 != null) {userInterface2.phoneDisplay(displayData);}
                            }
                            return status;
                        }
                    } // Power Turned On, Listener Started
                }
                else
                {
                    status = startListener(configuration.getClientPort());
                    if ( status[0].equals("1") )
                    {
                        userInterface1.showStatus("Power Failure: " + status[1] + " (Configuration Cause?)", true, true);
                        if (ePhoneGUIActive)
                        {
                            displayData.setPrimaryStatusCell("Line" + activeLineNumber + " Failure");
                            displayData.setPrimaryStatusDetailsCell("");
                            displayData.setSecondaryStatusCell("");
                            displayData.setSecondaryStatusDetailsCell("");
                            userInterface1.phoneDisplay(displayData); if (userInterface2 != null) {userInterface2.phoneDisplay(displayData);}
                        }
                        return status;
                    }
                }
            }
            else
            {
                if (loginstate == LOGINSTATE_REGISTERED)
                {
                    status = sendRegister(registerLogoutTimeout);
                    powerOffRequested = true;
                    if (status[0].equals("1"))
                    {
                        status = stopListener();
                        return status;
                    }
                }
                else
                {
                    status = stopListener();
                }
            }
        }
        else if ( buttonParam == SAVEBUTTON)
        {
            // Saving the config to the ActiveLineNumber
            configuration.saveConfiguration(activeLineNumber);
        }
        else if ( buttonParam == REGISTERBUTTON)
        {
//	    myUserInterface.sipstateUpdate(sipstate, lastsipstate, loginstate, SOFTPHONE_ACTIVITY_REGISTRATION, softPhoneInstanceId);
            if (userInputParam1.equals("1"))
            {
                status = sendRegister(registerLoginTimeout);
                if (status[0].equals("1")) { return status; }
            }
            else
            {
                status = sendRegister(registerLogoutTimeout);
                if (status[0].equals("1")) { return status; }
            }
//	    try { Thread.sleep(ultraShortMessagePeriod); } catch (InterruptedException error) { myUserInterface.log("Error: Thread.sleep(ultraShortMessagePeriod);: InterruptedException: " + error.getMessage()); }
	    dispatchSipState(sipstate, lastsipstate, loginstate, SOFTPHONE_ACTIVITY_REFRESH, softPhoneInstanceId, destination);
        }
        else if ( buttonParam == ANSWERBUTTON)
        {
	    dispatchSipState(sipstate, lastsipstate, loginstate, SOFTPHONE_ACTIVITY_MAINTENANCE, softPhoneInstanceId, destination);
	    try { Thread.sleep(ultraShortMessagePeriod); } catch (InterruptedException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: Thread.sleep(ultraShortMessagePeriod);: InterruptedException: " + error.getMessage()); }
	    if (userInputParam1.equals("1"))
	    {
		autoRingingResponse = ANSWER;
		autoRingingResponseDelay = Integer.parseInt(userInputParam2);

                if (ePhoneGUIActive)
                {
                    displayData.setAnswerFlag(true);
                    userInterface1.phoneDisplay(displayData); if (userInterface2 != null) {userInterface2.phoneDisplay(displayData);}
                    speakerData.setAnswerEnabledToneFlag(true);
                    userInterface1.speaker(speakerData);
                    speakerData.setAnswerEnabledToneFlag(false);
                }
	    }
	    else
	    {
		autoRingingResponse = NONE;
		autoRingingResponseDelay = 0;

                if (ePhoneGUIActive)
                {
                    displayData.setAnswerFlag(false);
                    userInterface1.phoneDisplay(displayData); if (userInterface2 != null) {userInterface2.phoneDisplay(displayData);}
                    speakerData.setAnswerDisabledToneFlag(true);
                    userInterface1.speaker(speakerData);
                    speakerData.setAnswerDisabledToneFlag(false);
                }
	    }
	    dispatchSipState(sipstate, lastsipstate, loginstate, SOFTPHONE_ACTIVITY_REFRESH, softPhoneInstanceId, destination);
	    try { Thread.sleep(ultraShortMessagePeriod); } catch (InterruptedException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: Thread.sleep(ultraShortMessagePeriod);: InterruptedException: " + error.getMessage()); }
        }
        else if ( buttonParam == CANCELBUTTON)
        {
	    dispatchSipState(sipstate, lastsipstate, loginstate, SOFTPHONE_ACTIVITY_MAINTENANCE, softPhoneInstanceId, destination);
	    try { Thread.sleep(ultraShortMessagePeriod); } catch (InterruptedException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: Thread.sleep(ultraShortMessagePeriod);: InterruptedException: " + error.getMessage()); }
	    if (userInputParam1.equals("1"))
	    {
		autoRingingResponse = CANCEL;
		autoRingingResponseDelay = Integer.parseInt(userInputParam2);
                if (ePhoneGUIActive)
                {                    
                    displayData.setCancelFlag(true);
                    userInterface1.phoneDisplay(displayData); if (userInterface2 != null) {userInterface2.phoneDisplay(displayData);}
                    speakerData.setCancelEnabledToneFlag(true);
                    userInterface1.speaker(speakerData);
                    speakerData.setCancelEnabledToneFlag(false);
                }
	    }
	    else
	    {
		autoRingingResponse = NONE;
		autoRingingResponseDelay = 0;

                if (ePhoneGUIActive)
                {
                    displayData.setCancelFlag(false);
                    userInterface1.phoneDisplay(displayData); if (userInterface2 != null) {userInterface2.phoneDisplay(displayData);}
                    speakerData.setCancelDisabledToneFlag(true);
                    userInterface1.speaker(speakerData);
                    speakerData.setCancelDisabledToneFlag(false);
                }
	    }
	    dispatchSipState(sipstate, lastsipstate, loginstate, SOFTPHONE_ACTIVITY_REFRESH, softPhoneInstanceId, destination);
	    try { Thread.sleep(ultraShortMessagePeriod); } catch (InterruptedException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: Thread.sleep(ultraShortMessagePeriod);: InterruptedException: " + error.getMessage()); }
        }
        else if ( buttonParam == RANDOMRINGRESPONSEBUTTON)// B1 = Button, P1 on/off, P2 RingResponseDelay, P3 BusyPercentage
        {
	    dispatchSipState(sipstate, lastsipstate, loginstate, SOFTPHONE_ACTIVITY_MAINTENANCE, softPhoneInstanceId, destination);
	    try { Thread.sleep(ultraShortMessagePeriod); } catch (InterruptedException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: Thread.sleep(ultraShortMessagePeriod);: InterruptedException: " + error.getMessage()); }
	    if (userInputParam1.equals("1"))
	    {
		autoRingingResponse = RANDOM;
		autoRingingResponseDelay = Integer.parseInt(userInputParam2);
                busyRatioPercentage = Integer.parseInt(userInputParam3);

                if (ePhoneGUIActive)
                {
                    displayData.setCancelFlag(true);
                    displayData.setAnswerFlag(true);
                    userInterface1.phoneDisplay(displayData); if (userInterface2 != null) {userInterface2.phoneDisplay(displayData);}
                    speakerData.setAnswerEnabledToneFlag(true);
                    userInterface1.speaker(speakerData);
                    speakerData.setAnswerEnabledToneFlag(false);
                }
	    }
	    else
	    {
		autoRingingResponse = NONE;
		autoRingingResponseDelay = 0;

                if (ePhoneGUIActive)
                {
                    displayData.setCancelFlag(false);
                    displayData.setAnswerFlag(false);
                    userInterface1.phoneDisplay(displayData); if (userInterface2 != null) {userInterface2.phoneDisplay(displayData);}
                    speakerData.setAnswerDisabledToneFlag(true);
                    userInterface1.speaker(speakerData);
                    speakerData.setAnswerDisabledToneFlag(false);
                }
	    }
	    dispatchSipState(sipstate, lastsipstate, loginstate, SOFTPHONE_ACTIVITY_REFRESH, softPhoneInstanceId, destination);
	    try { Thread.sleep(ultraShortMessagePeriod); } catch (InterruptedException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: Thread.sleep(ultraShortMessagePeriod);: InterruptedException: " + error.getMessage()); }
        }
        else if ( buttonParam == ENDTIMERBUTTON)
        {
	    if (userInputParam1.equals("1"))
	    {
		inboundEndTimerDelay = Integer.parseInt(userInputParam2);
	    }
	    else
	    {
		inboundEndTimerDelay = 0;
	    }
        }
        else if ( buttonParam == MUTEAUDIOBUTTON)
        {
	    dispatchSipState(sipstate, lastsipstate, loginstate, SOFTPHONE_ACTIVITY_MAINTENANCE, softPhoneInstanceId, destination);
	    try { Thread.sleep(ultraShortMessagePeriod); } catch (InterruptedException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: Thread.sleep(ultraShortMessagePeriod);: InterruptedException: " + error.getMessage()); }
            if (userInputParam1.equals("1"))
            {
                if (ePhoneGUIActive)
                {
                    displayData.setMuteFlag(true);
                    userInterface1.phoneDisplay(displayData); if (userInterface2 != null) {userInterface2.phoneDisplay(displayData);}
                    speakerData.setMuteEnabledToneFlag(true);
                    userInterface1.speaker(speakerData);
                    speakerData.setMuteEnabledToneFlag(false);
                }

                if (soundStreamer != null)
                {
                    audioIsMuted = true;
                    status = muteAudio();
                    if (status[0].equals("1")) { userInterface1.logToApplication(status[1]);}
                }
            }
            else
            {
                if (soundStreamer != null)
                {
                    audioIsMuted = false;
                    status = unMuteAudio();
                    if (status[0].equals("1")) { userInterface1.logToApplication(status[1]);}
                }

                if (ePhoneGUIActive)
                {
                    displayData.setMuteFlag(false);
                    userInterface1.phoneDisplay(displayData); if (userInterface2 != null) {userInterface2.phoneDisplay(displayData);}
                    speakerData.setMuteDisabledToneFlag(true);
                    userInterface1.speaker(speakerData);
                    speakerData.setMuteDisabledToneFlag(false);
                }

                if (status[0].equals("1")) { userInterface1.logToApplication(status[1]);}
            }
	    dispatchSipState(sipstate, lastsipstate, loginstate, SOFTPHONE_ACTIVITY_REFRESH, softPhoneInstanceId, destination);
	    try { Thread.sleep(ultraShortMessagePeriod); } catch (InterruptedException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: Thread.sleep(ultraShortMessagePeriod);: InterruptedException: " + error.getMessage()); }
        }
        else if ( buttonParam == RESTARTSOFTPHONEBUTTON)
        {
            status = restartListener(); if (status[0].equals("1")) { return status;}
        }
        else if ( buttonParam == DEBUGBUTTON)
        {
	    dispatchSipState(sipstate, lastsipstate, loginstate, SOFTPHONE_ACTIVITY_MAINTENANCE, softPhoneInstanceId, destination);
	    try { Thread.sleep(ultraShortMessagePeriod); } catch (InterruptedException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: Thread.sleep(ultraShortMessagePeriod);: InterruptedException: " + error.getMessage()); }
            if (userInputParam1.equals("1")) { debugging = true; } else { debugging = false; }
	    dispatchSipState(sipstate, lastsipstate, loginstate, SOFTPHONE_ACTIVITY_REFRESH, softPhoneInstanceId, destination);
	    try { Thread.sleep(ultraShortMessagePeriod); } catch (InterruptedException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: Thread.sleep(ultraShortMessagePeriod);: InterruptedException: " + error.getMessage()); }
        }
        else if ( buttonParam == CALLBUTTON)
        {
            if (userInputParam3.equals(Integer.toString(SCANNING))) { scan = true; } else { scan = false; }
	    dispatchSipState(sipstate, lastsipstate, loginstate, SOFTPHONE_ACTIVITY_MAINTENANCE, softPhoneInstanceId, destination);
            // Determine machinestate
            if      (sipstate == SIPSTATE_OFF)         { status[0] = "1"; status[1] = Integer.toString(sipstate); return status; } // Returned state will be used used by selfhealing mechanism
            else if (sipstate == SIPSTATE_ON)          { status[0] = "1"; status[1] = Integer.toString(sipstate); return status; } // idem
            else if (sipstate == SIPSTATE_IDLE)
	    {
		if ((userInputParam1 != null) && (userInputParam2 != null) && (!userInputParam1.equals("")) && (!userInputParam2.equals("")))
		{
		    status = sendRequest(userInputParam1, userInputParam2);
                    if (status[0].equals("1")) { return status; }
		}
                else
                {
                    status[0] = "1"; status[1] = "Error: UserInput: CallButton UserInputParamater Error";
                    dispatchSipState(sipstate, lastsipstate, loginstate, SOFTPHONE_ACTIVITY_REFRESH, softPhoneInstanceId, destination);
                    return status;
                }
	    }
            else if (sipstate == SIPSTATE_WAIT_PROV)   { status[0] = "1"; status[1] = Integer.toString(sipstate); return status; }
            else if (sipstate == SIPSTATE_WAIT_FINAL)  { status[0] = "1"; status[1] = Integer.toString(sipstate); return status; }
            else if (sipstate == SIPSTATE_WAIT_ACK)    { status[0] = "1"; status[1] = Integer.toString(sipstate); return status; }
            else if (sipstate == SIPSTATE_RINGING)     { status = createResponse(); if (status[0].equals("1")) { return status; } }
            else if (sipstate == SIPSTATE_ESTABLISHED) { status[0] = "1"; status[1] = Integer.toString(sipstate); return status; }
            else                                       { status[0] = "1"; status[1] = Integer.toString(sipstate); return status; }
	    try { Thread.sleep(ultraShortMessagePeriod); } catch (InterruptedException error) {  }
	    dispatchSipState(sipstate, lastsipstate, loginstate, SOFTPHONE_ACTIVITY_REFRESH, softPhoneInstanceId, destination);
        }
        else if ( buttonParam == ENDBUTTON)
        {
	    dispatchSipState(sipstate, lastsipstate, loginstate, SOFTPHONE_ACTIVITY_MAINTENANCE, softPhoneInstanceId, destination);
            // Determine machinestate
            if      (sipstate == SIPSTATE_OFF)         { stopTones(); }
            else if (sipstate == SIPSTATE_ON)          { stopTones(); }
            else if (sipstate == SIPSTATE_IDLE)        { stopTones(); }
            else if (sipstate == SIPSTATE_WAIT_PROV)   { status = cancelRequest(); if (status[0].equals("1")) { return status; } }
            else if (sipstate == SIPSTATE_WAIT_FINAL)  { status = cancelRequest(); if (status[0].equals("1")) { return status; } }
            else if (sipstate == SIPSTATE_WAIT_ACK)    { status = cancelRequest(); if (status[0].equals("1")) { return status; } }
            else if (sipstate == SIPSTATE_RINGING)     { status = busyResponse(); if (status[0].equals("1")) { return status; } }
            else if (sipstate == SIPSTATE_ESTABLISHED) { status = byeRequest(); if (status[0].equals("1")) { return status; } }
            else                                       { status[0] = "1"; status[1] = "State Error: Sipstate Unknown"; return status; }
	    try { Thread.sleep(ultraShortMessagePeriod); } catch (InterruptedException error) {  }
	    dispatchSipState(sipstate, lastsipstate, loginstate, SOFTPHONE_ACTIVITY_REFRESH, softPhoneInstanceId, destination);
        }
        else
        {
            status[0] = "1"; status[1] = "Button not recognized"; return status;
        }
        return status;
    }

    private void stopTones()
    {
        speakerData.setRingToneFlag(false);
        speakerData.setCallToneFlag(false);
        userInterface1.speaker(speakerData);
    }

    /**
     *
     * @return
     */
    public Configuration getConfiguration()
    {
        return configuration;
    }

    /**
     *
     * @return
     */
    @SuppressWarnings("static-access")
    public int findFreePort() // This actually is the clientsocket
    {
        ServerSocket serverSocket = null;
        do
        {
            try { serverSocket = new ServerSocket(0,serverSocketBackLog,localIPAddress); } catch (IOException ex) {} // Sets up the ServerSocket Object
            try { Thread.sleep(1); } catch (InterruptedException ex) {  }
            try { serverSocket.setReuseAddress(true); } catch (SocketException ex) {  }
            try { serverSocket.bind(null); } catch (IOException ex) {  }
            try { Thread.sleep(1); } catch (InterruptedException ex) {  }
        }
        while ( ! serverSocket.isBound());
        int autoPort = serverSocket.getLocalPort(); // This actually is the slightly mallfunctioning object FIX!!!
        try { serverSocket.close(); } catch (IOException ex) { userInterface1.showStatus("☎ " + softPhoneInstanceId + " Error: IOException: findFreePort(): serverSocket.close() " + ex.getMessage(), keepRunning, autoEndCall);}
        return autoPort;
    }

    /**
     *
     * @param sourcePortParam
     * @return
     */
    public String[] startListener(String sourcePortParam) // Remember you can set sourcePortParam to "auto"
    {
        sipStackProperties = new Properties();

        String[] status = new String[2]; status[0] = "0"; status[1] = "";

        // Get the IP & hostname
//        try { name  = InetAddress.getLocalHost().getHostName(); } catch(UnknownHostException error) { status[0] = "1"; status[1] = "Error: InetAddress.getLocalHost().getHostName(): " + error.getMessage(); }
//        try { sipStackName  = InetAddress.getLocalHost().getHostName(); } catch(UnknownHostException error) { status[0] = "1"; status[1] = "Error: InetAddress.getLocalHost().getHostName(): " + error.getMessage(); }

        //Create the SipFactory
        sipFactory = SipFactory.getInstance();
        sipFactory.setPathName("gov.nist"); // Set the Path

        //Create Properties (for the SipStack)
//        sipStackProperties.setProperty("javax.sip.STACK_NAME", name + "-" + getId() + "-" + softPhoneInstanceId); // Needs to be unique
        sipStackProperties.setProperty("javax.sip.STACK_NAME", Integer.toString(softPhoneInstanceId)); // Needs to be unique
        sipStackProperties.setProperty("javax.sip.MAX_MESSAGE_SIZE", "4096");
//        sipStackProperties.setProperty("javax.sip.CACHE_SERVER_CONNECTIONS", "true"); // unset default = true. Setting this to false highly increased instability
//        sipStackProperties.setProperty("javax.sip.THREAD_POOL_SIZE", "10"); // unset default = infinity. Setting a number 10 (4 threads per listener measured) didn't turn out well
//        sipStackProperties.setProperty("javax.sip.REENTRANT_LISTENER", "true"); // unset default = false (Performance Impact). Did not increase stability
//        sipStackProperties.setProperty("javax.sip.MAX_CONNECTIONS", "5000"); // unset default = 5000 (high watermark, low watermark = 80% of highwatermark) Did not increase stability
//        sipStackProperties.setProperty("javax.sip.PASS_INIVTE_NON2XX_ACK_TO_LISTENER", "true"); // unset default = false.  Did not increase stability
//        sipStackProperties.setProperty("javax.sip.MAX_LISTENER_RESPONSE_TIME", "10"); // (seconds) unset default = infinitly with a risk of memory leakage. Did not increase stability
//        sipStackProperties.setProperty("javax.sip.DELIVER_TERMINATED_EVENT_FOR_ACK", "true"); // unset default = false. Did not increase stability
//        sipStackProperties.setProperty("javax.sip.ADDRESS_RESOLVER", "192.168.0.3"); // alternative DNS
        sipStackProperties.setProperty("javax.sip.THREAD_AUDIT_INTERVAL_IN_MILLISECS", "1000"); // Stack Health Audit unset default = off. Did not increase stability

        //Create the SipStack
        try { sipStack = sipFactory.createSipStack(sipStackProperties); }
        catch(PeerUnavailableException error) { status[0] = "1"; status[1] = "Error: mySipFactory.createSipStack(mySipStackProperties): " + error.getMessage(); return status; }

        boolean createUDPListeningPointUnSuccessFull = false;
        int retryCounter = 0;
        do
        {
            createUDPListeningPointUnSuccessFull = false;
            //Create the ListeningPoint
            ServerSocket serverSocket = null;

            if ( (sourcePortParam.equals("auto")) || (sourcePortParam.equals("")) )
            {
                try { serverSocket = new ServerSocket(0,serverSocketBackLog,localIPAddress); } catch (IOException ex) {  }
                localSIPPort = serverSocket.getLocalPort();
            }
            else
            {
                localSIPPort = Integer.parseInt(sourcePortParam);
            }

            try { udpListeningPoint = sipStack.createListeningPoint( configuration.getClientIP(), localSIPPort, udpTransport); }
            catch( TransportNotSupportedException error)    { createUDPListeningPointUnSuccessFull = true; if (retryCounter == 3 ) { status[0] = "1"; status[1] = "☎ " + softPhoneInstanceId + " [" + retryCounter + "] TransportNotSupportedException: mySipStack.createListeningPoint(..udp..) " + error.getMessage() ; return status; } }
            catch( InvalidArgumentException error)          { createUDPListeningPointUnSuccessFull = true; if (retryCounter == 3 ) { status[0] = "1"; status[1] = "☎ " + softPhoneInstanceId + " [" + retryCounter + "] InvalidArgumentException: mySipStack.createListeningPoint(..udp..) " + error.getMessage() ; return status; } }
            retryCounter++;
        }
        while((retryCounter <= 3) && (createUDPListeningPointUnSuccessFull));

//        boolean createTCPListeningPointUnSuccessFull = false;
//        retryCounter = 0;
//        do
//        {
//            createTCPListeningPointUnSuccessFull = false;
//            //Create the ListeningPoint
//            try { tcpListeningPoint = sipStack.createListeningPoint( configuration.getClientIP(), sipPort, tcpTransport); }
//            catch( TransportNotSupportedException error)    { createTCPListeningPointUnSuccessFull = true; if (retryCounter == 3 ) { status[0] = "1"; status[1] = "☎ " + softPhoneInstanceId + " [" + retryCounter + "] TransportNotSupportedException: mySipStack.createListeningPoint(..tcp..) " + error.getMessage() ; return status; } }
//            catch( InvalidArgumentException error)          { createTCPListeningPointUnSuccessFull = true; if (retryCounter == 3 ) { status[0] = "1"; status[1] = "☎ " + softPhoneInstanceId + " [" + retryCounter + "] InvalidArgumentException: mySipStack.createListeningPoint(..tcp..) " + error.getMessage() ; return status; } }
//            retryCounter++;
//        }
//        while((retryCounter <= 3) && (createTCPListeningPointUnSuccessFull));

        //Create other Factories
        try { messageFactory = sipFactory.createMessageFactory(); }
        catch(PeerUnavailableException error) { status[0] = "1"; status[1] = "Error: mySipFactory.createMessageFactory(): " + error.getMessage(); return status; }
        try { addressFactory = sipFactory.createAddressFactory(); }
        catch(PeerUnavailableException error) { status[0] = "1"; status[1] = "Error: mySipFactory.createMessageFactory(): " + error.getMessage(); return status; }
        try { headerFactory = sipFactory.createHeaderFactory(); }
        catch(PeerUnavailableException error) { status[0] = "1"; status[1] = "Error: mySipFactory.createHeaderFactory(): " + error.getMessage(); return status; }
        try { addressFactory = sipFactory.createAddressFactory(); }
        catch(PeerUnavailableException error) { status[0] = "1"; status[1] = "Error: mySipFactory.createAddressFactory(): " + error.getMessage(); return status; }

//        try { contactAddress = myAddressFactory.createAddress("sip:" + myIP.getHostAddress() + ":" + myPort); }
//        catch (ParseException error) { status[0] = "1"; status[1] = "Error: mySipFactory.createAddressFactory(): " + error.getMessage(); return status; }

        try { privateContactAddress = addressFactory.createAddress("sip:" + configuration.getUsername() + "@" + configuration.getClientIP() + ":" + localSIPPort); }
        catch (ParseException error) { status[0] = "1"; status[1] = "Error: mySipFactory.createAddressFactory(): " + error.getMessage(); return status; }

        // NAT / Firewall Traversal (The ContactHeader and ViaHeader should be public when traveling through firewalls)

        if (configuration.getPublicIP().length() > 0)
        {
            try { publicContactAddress = addressFactory.createAddress("sip:" + configuration.getUsername() + "@" + configuration.getPublicIP() + ":" + localSIPPort); }
            catch (ParseException error) { status[0] = "1"; status[1] = "Error: mySipFactory.createAddressFactory(): " + error.getMessage(); return status; }
        }
        else
        {
            try { publicContactAddress = addressFactory.createAddress("sip:" + configuration.getUsername() + "@" + configuration.getClientIP() + ":" + localSIPPort); }
            catch (ParseException error) { status[0] = "1"; status[1] = "Error: mySipFactory.createAddressFactory(): " + error.getMessage(); return status; }            
        }

        privateContactHeader = headerFactory.createContactHeader(privateContactAddress);
        publicContactHeader = headerFactory.createContactHeader(publicContactAddress);

        if (configuration.getPublicIP().length() == 0)
        {
            rinstance = VoipStormTools.getRandom(8);
            try { privateContactHeader.setParameter("rinstance", rinstance); } catch (ParseException error) { status[0] = "1"; status[1] = "Error: myContactHeader.setParameter(\"rinstance\", SoftPhoneTools.getRandom(6)): ParseException: " + error.getMessage(); return status; }
            try { privateContactHeader.setParameter("transport", transport); } catch (ParseException error) { status[0] = "1"; status[1] = "Error: myContactHeader.setParameter(\"rinstance\", SoftPhoneTools.getRandom(6)): ParseException: " + error.getMessage(); return status; }
        }
        else
        {
            rinstance = VoipStormTools.getRandom(8);
            try { privateContactHeader.setParameter("rinstance", rinstance); } catch (ParseException error) { status[0] = "1"; status[1] = "Error: myContactHeader.setParameter(\"rinstance\", SoftPhoneTools.getRandom(6)): ParseException: " + error.getMessage(); return status; }
            try { privateContactHeader.setParameter("transport", transport); } catch (ParseException error) { status[0] = "1"; status[1] = "Error: myContactHeader.setParameter(\"rinstance\", SoftPhoneTools.getRandom(6)): ParseException: " + error.getMessage(); return status; }
        }

        // Put my own UserAgent Identification string among the other SIP Headers
//        userAgentList.add("Asterisk PBX"); try { userAgentHeader = myHeaderFactory.createUserAgentHeader(userAgentList); } catch (ParseException error) { status[0] = "1"; status[1] = "Error: userAgentHeader = myHeaderFactory.createUserAgentHeader(viaHeaders): ParseException: " + error.getMessage(); return status; }
//        userAgentList.add(getWindowTitle()); try { userAgentHeader = headerFactory.createUserAgentHeader(userAgentList); } catch (ParseException error) { status[0] = "1"; status[1] = "Error: userAgentHeader = myHeaderFactory.createUserAgentHeader(viaHeaders): ParseException: " + error.getMessage(); return status; }
        userAgentList.add(Vergunning.BRAND + " " + Vergunning.PRODUCT); try { userAgentHeader = headerFactory.createUserAgentHeader(userAgentList); } catch (ParseException error) { status[0] = "1"; status[1] = "Error: userAgentHeader = myHeaderFactory.createUserAgentHeader(viaHeaders): ParseException: " + error.getMessage(); return status; }
        try { allowHeader = headerFactory.createAllowHeader("INVITE,ACK,CANCEL,OPTIONS,BYE,REFER,NOTIFY,MESSAGE,SUBSCRIBE,INFO"); } catch (ParseException error) { status[0] = "1"; status[1] = "Error: allowHeader = myHeaderFactory.createAllowHeader(...): ParseException: " + error.getMessage(); return status; }

//        //Create the SipProvider
        try { sipProvider = sipStack.createSipProvider(udpListeningPoint); }
        catch(ObjectInUseException error){ status[0] = "1"; status[1] = "Error: sipStack.createSipProvider(udpisteningPoint): " + error.getMessage(); return status; }

//        try { sipProvider.addListeningPoint(tcpListeningPoint); }
//        catch (ObjectInUseException error)                  { status[0] = "1"; status[1] = "Error: ObjectInUseException: sipProvider.addListeningPoint(tcpListeningPoint):" + error.getMessage(); return status; }
//        catch (TransportAlreadySupportedException error)    { status[0] = "1"; status[1] = "Error: TransportAlreadySupportedException: sipProvider.addListeningPoint(tcpListeningPoint):" + error.getMessage(); return status; }

        //Add the SipListener to the SipProvider
        try { sipProvider.addSipListener(this); } catch (TooManyListenersException error) { status[0] = "1"; status[1] = "Error: sipProvider.addSipListener(this): " + error.getMessage(); return status; }

        if (status[0].equals("0"))
        {
            status[1] = Integer.toString(localSIPPort);
        }

        availability = unregistered;
        lastsipstate = sipstate;
        sipstate = SIPSTATE_IDLE;
        dispatchSipState(sipstate, lastsipstate, loginstate, SOFTPHONE_ACTIVITY_NORMAL, softPhoneInstanceId, destination);
	try { Thread.sleep(ultraShortMessagePeriod); } catch (InterruptedException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: Thread.sleep(ultraShortMessagePeriod);: InterruptedException: " + error.getMessage()); }

//        if (ePhoneGUIActive)
//        {
            displayData.setSoftphoneInfoCell("Phone: sip:" + configuration.getClientIP() + ":" + localSIPPort);
            displayData.setPrimaryStatusCell("Phone \"" + configuration.getUsername() + "\" " + availability);
            displayData.setPrimaryStatusDetailsCell("");
            displayData.setSecondaryStatusCell("");
            displayData.setSecondaryStatusDetailsCell("");
            displayData.resetSip();
    //        displayData.setOnFlag(true);
            displayData.setIdleFlag(true);
            userInterface1.phoneDisplay(displayData); if (userInterface2 != null) {userInterface2.phoneDisplay(displayData);}
//        }
//        myUserInterface.showStatus("sip:" + myIP.getHostAddress() + ":" + status[1]);

        return status;
    }

    /**
     *
     * @return
     */
    public String[] stopListener()
    {
        String[] status = new String[2]; status[0] = "0"; status[1] = "";
        sipStack.stop(); // Keep this one global and make sure it stops gracefully returning all resources!!!

////	if ( mySoundStreamer.isListening() ) { mySoundStreamer.stopListener(); }
//        if ( mySipProvider != null )
//        {
//            mySipProvider.removeSipListener(this);
//
//// ======================== Long Shutdown if below uncommented ======================
//
////            try { mySipProvider.removeListeningPoint(myTCPListeningPoint); }
////            catch (ObjectInUseException error) { status[0] = "1"; status[1] = "Error: mySipProvider.removeListeningPoint(myTCPListeningPoint): ObjectInUseException: " + error.getMessage(); return status; }
//            try { mySipProvider.removeListeningPoint(myUDPListeningPoint); }
//            catch (ObjectInUseException error) { status[0] = "1"; status[1] = "Error: mySipProvider.removeListeningPoint(myUDPListeningPoint): ObjectInUseException: " + error.getMessage(); return status; }
////            try { mySipStack.deleteListeningPoint(myTCPListeningPoint); }
////            catch (ObjectInUseException error) { status[0] = "1"; status[1] = "Error: mySipStack.deleteListeningPoint(myTCPListeningPoint): ObjectInUseException: " + error.getMessage(); return status; }
//            try { mySipStack.deleteListeningPoint(myUDPListeningPoint); }
//            catch (ObjectInUseException error) { status[0] = "1"; status[1] = "Error: mySipStack.deleteListeningPoint(myTCPListeningPoint): ObjectInUseException: " + error.getMessage(); return status; }
//            try { mySipStack.deleteSipProvider(mySipProvider); } // Very Slow!!!
//            catch (ObjectInUseException error) { status[0] = "1"; status[1] = "Error: mySipStack.deleteSipProvider(mySipProvider): ObjectInUseException: " + error.getMessage(); return status; }
//        }
//
//        myMessageFactory = null;
//        myAddressFactory = null;
//        myHeaderFactory = null;
//        mySipProvider = null;

//        mySipStack.stop(); // Keep this one global and make sure it stops gracefully returning all resources!!!
        
//        mySipFactory.resetFactory();
//        mySipFactory = null;

// ======================== Long Shutdown if above uncommented ======================

        if (debugging == true) { if (debugginglevel == 1) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " stopListener\n\n"); } }

        availability = "On";
        lastsipstate = sipstate;
        sipstate = SIPSTATE_ON;

        dispatchSipState(sipstate, lastsipstate, loginstate, SOFTPHONE_ACTIVITY_NORMAL, softPhoneInstanceId, destination);
	try { Thread.sleep(ultraShortMessagePeriod); } catch (InterruptedException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: Thread.sleep(ultraShortMessagePeriod);: InterruptedException: " + error.getMessage()); }

//        if (ePhoneGUIActive)
//        {
            displayData.setSoftphoneInfoCell("");
            displayData.setPrimaryStatusCell("Phone Powered " + availability);
            displayData.setPrimaryStatusDetailsCell("");
            displayData.setSecondaryStatusCell("");
            displayData.setSecondaryStatusDetailsCell("");
            displayData.resetSip();
            displayData.setOnFlag(true);
            userInterface1.phoneDisplay(displayData); if (userInterface2 != null) {userInterface2.phoneDisplay(displayData);}
//        }
        return status;
    }

    /**
     *
     * @return
     */
    public String[] restartListener()
    {
        dispatchSipState(sipstate, lastsipstate, loginstate, SOFTPHONE_ACTIVITY_MAINTENANCE, softPhoneInstanceId, destination);
        try { Thread.sleep(ultraShortMessagePeriod); } catch (InterruptedException error) {  }

        String[] status = new String[2]; status[0] = "0"; status[1] = "";

        userInterface1.showStatus("Reloading SoftPhone Instance: " + getInstanceId(), true, true);
        status = stopListener(); if (status[0].equals("1")) { return status;}
        status = startListener(Integer.toString(localSIPPort));  if (status[0].equals("1")) { return status;}

        dispatchSipState(sipstate, lastsipstate, loginstate, SOFTPHONE_ACTIVITY_REFRESH, softPhoneInstanceId, destination);
	try { Thread.sleep(ultraShortMessagePeriod); } catch (InterruptedException error) {  }

        return status;
    }

    /**
     *
     * @param registerTimeoutParam
     * @return
     */
    @SuppressWarnings("empty-statement")
    public String[] sendRegister(int registerTimeoutParam) // Caller sends REGISTER
    {
        registerRequestCounter = 1;

        String[] status = new String[2]; status[0] = "0"; status[1] = "";

//        clientPort     = myConfiguration.getClientPort();
//        aor            = "sip:" + myConfiguration.getUsername() + "@" + myConfiguration.getDomain();
//        contact        = "sip:" + myConfiguration.getUsername() + "@" + myConfiguration.getClientIP() + ":" + myPort;

        // NAT / Firewall traversal
        if (configuration.getPublicIP().length() == 0)
        {
            aor            = "sip:" + configuration.getUsername() + "@" + configuration.getDomain();
            from           = "sip:" + configuration.getUsername() + "@" + configuration.getDomain();
            to             = "sip:" + configuration.getUsername() + "@" + configuration.getDomain();
            via            = configuration.getClientIP();
            try { privateContactAddress = addressFactory.createAddress("sip:" + configuration.getUsername() + "@" + configuration.getClientIP() + ":" + localSIPPort); }
            catch (ParseException error) { status[0] = "1"; status[1] = "Error: mySipFactory.createAddressFactory(): " + error.getMessage(); return status; }
        }
        else
        {
            aor            = "sip:" + configuration.getUsername() + "@" + configuration.getDomain();
            from           = "sip:" + configuration.getUsername() + "@" + configuration.getDomain();
            to             = "sip:" + configuration.getUsername() + "@" + configuration.getDomain();
            via            = configuration.getPublicIP();
            try { privateContactAddress = addressFactory.createAddress("sip:" + configuration.getUsername() + "@" + configuration.getPublicIP() + ":" + localSIPPort); }
            catch (ParseException error) { status[0] = "1"; status[1] = "Error: mySipFactory.createAddressFactory(): " + error.getMessage(); return status; }
        }

//        privateContactHeader = headerFactory.createContactHeader(privateContactAddress);

        registerTimeout    = registerTimeoutParam;

        //Create Addresses
        try     { registrarAddress = addressFactory.createAddress("sip:" + configuration.getServerIP() + ":" + configuration.getServerPort()); } // To whom this message is for
        catch (ParseException error) { status[0] = "1"; status[1] = "Error: registrarAddress = myAddressFactory.createAddress(\"sip:\" + server): ParseException: " + error.getMessage(); return status; }

        try { fromAddress = addressFactory.createAddress(from); } catch (ParseException error) { status[0] = "1"; status[1] = "Error: fromAddress = myAddressFactory.createAddress(from): ParseException: " + error.getMessage(); return status; }

        try { toAddress = addressFactory.createAddress(to); } // Where I can be contacted NO IP's or FQDN (should be domain)
        catch (ParseException error) { status[0] = "1"; status[1] = "Error: myAddressFactory.createAddress(to): " + error.getMessage(); return status;}

        try { toHeader = headerFactory.createToHeader(toAddress, null); }
        catch (ParseException error) { status[0] = "1"; status[1] = "Error: myHeaderFactory.createToHeader(registerToAddress, null): " + error.getMessage(); return status; }

        try { fromHeader = headerFactory.createFromHeader(fromAddress, VoipStormTools.getRandom(6)); }
        catch (ParseException error) { status[0] = "1"; status[1] = "Error: myHeaderFactory.createFromHeader(registerFromAddress, \"RNDNUMSTRING\"): " + error.getMessage(); return status; } // Tag used together with CallId for identification

        // Tag used together with CallId for identification

        if (configuration.getPublicIP().length() == 0) // Remove ContactHeader tags
        {
            try { privateContactHeader.setParameter("rinstance", rinstance); }
            catch (ParseException error) { status[0] = "1"; status[1] = "Error: myContactHeader.setParameter\\(\"rinstance\",: ParseException: " + error.getMessage(); return status; }
            try { privateContactHeader.setParameter("transport", transport); }
            catch (ParseException error) { status[0] = "1"; status[1] = "Error: myContactHeader.setParameter\\(\"transport\",: ParseException: " + error.getMessage(); return status; }
            try { privateContactHeader.setParameter("expires", Integer.toString(registerTimeoutParam)); }
            catch (ParseException error) { status[0] = "1"; status[1] = "Error: myContactHeader.setParameter\\(\"transport\",: ParseException: " + error.getMessage(); return status; }            
        }
        else
        {
            try { publicContactHeader.setParameter("rinstance", rinstance); }
            catch (ParseException error) { status[0] = "1"; status[1] = "Error: myContactHeader.setParameter\\(\"rinstance\",: ParseException: " + error.getMessage(); return status; }
            try { publicContactHeader.setParameter("transport", transport); }
            catch (ParseException error) { status[0] = "1"; status[1] = "Error: myContactHeader.setParameter\\(\"transport\",: ParseException: " + error.getMessage(); return status; }
            try { publicContactHeader.setParameter("expires", Integer.toString(registerTimeoutParam)); }
            catch (ParseException error) { status[0] = "1"; status[1] = "Error: myContactHeader.setParameter\\(\"transport\",: ParseException: " + error.getMessage(); return status; }
        }

        viaHeaders = new ArrayList();

        // NAT / Firewall traversal
        if (configuration.getPublicIP().length() == 0)
        {
            try {  viaHeader = headerFactory.createViaHeader( configuration.getClientIP(), localSIPPort, transport, null); } // Clients hostname / ip address for routing back to client and for Proxy loopdetection
            catch (ParseException error) { status[0] = "1"; status[1] = "Error: myViaHeader = myHeaderFactory.createViaHeader( configuration.getClientIP(), myPort, myTransport, null): ParseException: " + error.getMessage(); return status; }
            catch (InvalidArgumentException error) { status[0] = "1"; status[1] = "Error: myViaHeader = myHeaderFactory.createViaHeader( configuration.getClientIP(), myPort, myTransport, null): InvalidArgumentException: " + error.getMessage(); return status; }
        }
        else
        {
            try {  viaHeader = headerFactory.createViaHeader( configuration.getPublicIP(), localSIPPort, transport, null); } // Clients hostname / ip address for routing back to client and for Proxy loopdetection
            catch (ParseException error) { status[0] = "1"; status[1] = "Error: myViaHeader = myHeaderFactory.createViaHeader( configuration.getPubIP(), myPort, myTransport, null): ParseException: " + error.getMessage(); return status; }
            catch (InvalidArgumentException error) { status[0] = "1"; status[1] = "Error: myViaHeader = myHeaderFactory.createViaHeader( configuration.getPubIP(), myPort, myTransport, null): InvalidArgumentException: " + error.getMessage(); return status; }
//            try {  myViaHeader = myHeaderFactory.createViaHeader( configuration.getClientIP(), myPort, myTransport, null); } // Clients hostname / ip address for routing back to client and for Proxy loopdetection
//            catch (ParseException error) { status[0] = "1"; status[1] = "Error: myViaHeader = myHeaderFactory.createViaHeader( configuration.getClientIP(), myPort, myTransport, null): ParseException: " + error.getMessage(); return status; }
//            catch (InvalidArgumentException error) { status[0] = "1"; status[1] = "Error: myViaHeader = myHeaderFactory.createViaHeader( configuration.getClientIP(), myPort, myTransport, null): InvalidArgumentException: " + error.getMessage(); return status; }
        }

//        try { viaHeader.setParameter("rport", null); } catch (ParseException error) { status[0] = "1"; status[1] = "Error: myViaHeader.setParameter(\"rport\", null): ParseException: " + error.getMessage(); return status; }
        viaHeaders.add(viaHeader);

        try { maxForwardsHeader = headerFactory.createMaxForwardsHeader(MAXFORWARDS); } catch (InvalidArgumentException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myMaxForwardsHeader = myHeaderFactory.createMaxForwardsHeader(x);: InvalidArgumentException: " + error.getMessage()); }

        callIdHeader = sipProvider.getNewCallId();
        commandSequenceHeader = null; try { commandSequenceHeader = headerFactory.createCSeqHeader((long)1, Request.REGISTER ); }
        catch (ParseException error) { status[0] = "1"; status[1] = "Error: myHeaderFactory.createCSeqHeader(1, " + Request.REGISTER + "): ParseException: " + error.getMessage(); return status; }
        catch (InvalidArgumentException error) { status[0] = "1"; status[1] = "Error: myHeaderFactory.createCSeqHeader(1, " + Request.REGISTER + "): InvalidArgumentException: " + error.getMessage(); return status; }

        try { expiresHeader = headerFactory.createExpiresHeader(registerTimeoutParam); }
        catch (InvalidArgumentException error) { status[0] = "1"; status[1] = "Error: myExpiresHeader = myHeaderFactory.createExpiresHeader(registerTimeout);: InvalidArgumentException: " + error.getMessage(); return status; }

        requestURI = (SipURI) registrarAddress.getURI();

        //Create Request
        try { registerRequest = messageFactory.createRequest(requestURI, Request.REGISTER, callIdHeader, commandSequenceHeader, fromHeader, toHeader, viaHeaders, maxForwardsHeader); }
        catch (ParseException error) { status[0] = "1"; status[1] = "Error: myMessageFactory.createRequest(: ParseException: " + error.getMessage(); return status; }

        if (configuration.getPublicIP().length() == 0) // Remove ContactHeader tags
        {
            registerRequest.addHeader(privateContactHeader);
        }
        else
        {
            registerRequest.addHeader(publicContactHeader);
        }

        registerRequest.addHeader(expiresHeader);
        registerRequest.addHeader(userAgentHeader);
//        registerRequest.addHeader(allowHeader); // not in communicator

        // Create new ClientTransaction
        try { clientTransaction = sipProvider.getNewClientTransaction(registerRequest); }
        catch (TransactionUnavailableException error) { status[0] = "1"; status[1] = "Error: mySipProvider.getNewClientTransaction(myRegisterRequest): TransactionUnavailableException: " + error.getMessage(); return status; }

        try { clientTransaction.sendRequest(); } catch (SipException error) { status[0] = "1"; status[1] = "Error: myClientTransaction.sendRequest(): SipException" + error.getMessage(); return status; } // Default
        registerRequestBranch = clientTransaction.getBranchId();

        if (debugging == true)
        {
            if (debugginglevel == 1)
            { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " sendRegister: " + registerRequest.getMethod() + "-Request Sent:\n\n" + registerRequest.toString()); }
            else
            { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " sendRegister: " + registerRequest.getMethod() + "-Request Sent."); }
        }
        return status;
    }

    /**
     *
     * @param destinationParam
     * @param filenameParam
     * @return
     */
    public String[] sendRequest(String destinationParam, String filenameParam) // Caller sends INVITE
    {
        String toHeaderString; // Leave this one local
        String namePart1; // Leave this one local

        String[] status = new String[2]; status[0] = "0"; status[1] = "";
        if ((destinationParam != null) && (! destinationParam.equals("")) && (filenameParam != null) && (! filenameParam.equals("")))
        {
            if ((destinationParam.startsWith("sip:")) || (destinationParam.startsWith("<sip:")) || (destinationParam.startsWith("\"")))// Starts with sip:
            {
                isSIPAddress = true;
                sipDestination    = destination.getDestination();
//                aor            = "sip:" + myConfiguration.getUsername() + "@" + myConfiguration.getDomain();
//                contact        = "sip:" + myConfiguration.getUsername() + "@" + myConfiguration.getClientIP() + ":" + myPort;

                // NAT / Firewall Traversal
                if (configuration.getPublicIP().length() == 0)
                {
                    from           = "sip:" + configuration.getUsername() + "@" + configuration.getDomain();
                    to             = destinationParam;
                    via            = configuration.getClientIP();
                    try { privateContactAddress = addressFactory.createAddress("sip:" + configuration.getUsername() + "@" + configuration.getClientIP() + ":" + localSIPPort); }
                    catch (ParseException error) { status[0] = "1"; status[1] = "Error: mySipFactory.createAddressFactory(): " + error.getMessage(); return status; }
                }
                else
                {
                    from           = "sip:" + configuration.getUsername() + "@" + configuration.getDomain();
                    to             = destinationParam;
                    via            = configuration.getClientIP();
                    try { privateContactAddress = addressFactory.createAddress("sip:" + configuration.getUsername() + "@" + configuration.getClientIP() + ":" + localSIPPort); }
                    catch (ParseException error) { status[0] = "1"; status[1] = "Error: mySipFactory.createAddressFactory(): " + error.getMessage(); return status; }
                }
//                to             = phoneNumber;
//                via            = configuration.getClientIP();
                filename       = filenameParam; // Invite
            }
            else // Is just a phonenumber
            {
                isSIPAddress = false;
                // NAT / Firewall Traversal

                filename       = filenameParam; // Invite
                sipDestination    = "sip:" + VoipStormTools.substitude(destinationParam, "[^0-9]") + "@" + configuration.getServerIP() + ":" + configuration.getServerPort();

                if (configuration.getPublicIP().length() == 0)
                {
                    from           = "sip:" + configuration.getUsername() + "@" + configuration.getDomain();
                    to             = "sip:" + VoipStormTools.substitude(destinationParam, "[^0-9]") + "@" + configuration.getDomain();
                    via            = configuration.getClientIP();
                    try { privateContactAddress = addressFactory.createAddress("sip:" + configuration.getUsername() + "@" + configuration.getClientIP() + ":" + localSIPPort); }
                    catch (ParseException error) { status[0] = "1"; status[1] = "Error: mySipFactory.createAddressFactory(): " + error.getMessage(); return status; }
                }
                else
                {
                    from           = "sip:" + configuration.getUsername() + "@" + configuration.getDomain();
                    to             = "sip:" + VoipStormTools.substitude(destinationParam, "[^0-9]") + "@" + configuration.getDomain();
//                    to             = "sip:" + VoipStormTools.substitude(destinationParam, "[^0-9]") + "@" + configuration.getServerIP();
//                    via            = configuration.getClientIP();
                    via            = configuration.getPublicIP();
//                    try { contactAddress = addressFactory.createAddress("sip:" + configuration.getUsername() + "@" + configuration.getClientIP() + ":" + localSIPPort); }
                    try { privateContactAddress = addressFactory.createAddress("sip:" + configuration.getUsername() + "@" + configuration.getPublicIP() + ":" + localSIPPort); }
                    catch (ParseException error) { status[0] = "1"; status[1] = "Error: mySipFactory.createAddressFactory(): " + error.getMessage(); return status; }
                }
            }
            destinationDisplay = destinationParam;//VoipStormTools.substitude(destinationParam, "[^0-9]");
        }
        else
        {
            status[0] = "1"; status[1] = "Invalid sendRequest Paramer(s): destinationParam: " + destinationParam + " filenameParam: " + filenameParam; return status;
        }

        do
        {
            soundStreamer = new SoundStreamer(userInterface1, softPhoneInstanceId);
            status = soundStreamer.startListener(configuration.getClientIP(), requestedClientAudioPort, configuration.getClientIP(), assignedServerAudioPort);
            if  (
                    (status[0].equals("1")) ||
                    (status[0].equals("0")) && (status[1].equals(""))
                )
            {
                userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: SoundStreamer.startListener(" + configuration.getClientIP() + "," + requestedClientAudioPort + "," + configuration.getClientIP() + "," + assignedServerAudioPort+")");
                try { Thread.sleep(retryInterval); } catch (InterruptedException error2) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Sleep error: " + error2.getMessage()); }
            }
        }
        while(
                (status[0].equals("1")) ||
                (status[0].equals("0")) && (status[1].equals(""))
             );


        if (
                (status[0].equals("1")) ||
                (status[0].equals("0")) && (status[1].equals(""))
           )
        {
                userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: (sendRequest) status(" + assignedClientAudioPort + ") = soundStreamer.startListener(" + configuration.getClientIP() + ","  + requestedClientAudioPort + "," + configuration.getClientIP() + "," + assignedServerAudioPort + ")");
        }
        else
        {
            assignedClientAudioPort = Integer.parseInt(status[1]);
            if (debugging == true)
            {
                userInterface1.logToApplication("☎ " + softPhoneInstanceId + " (sendRequest) status(" + assignedClientAudioPort + ") = soundStreamer.startListener(" + configuration.getClientIP() + ","  + requestedClientAudioPort + "," + configuration.getClientIP() + "," + assignedServerAudioPort + ")");
            }
            muteAudio();
        }

        //Create Addresses

        try { fromAddress = addressFactory.createAddress(from); } // Where I can be contacted NO IP's or FQDN (should be domain)
        catch (ParseException error) { status[0] = "1"; status[1] = "Error: myAddressFactory.createAddress(from): " + error.getMessage(); return status;}

        try { toAddress = addressFactory.createAddress(to); } // Where I can be contacted NO IP's or FQDN (should be domain)
        catch (ParseException error) { status[0] = "1"; status[1] = "Error: myAddressFactory.createAddress(to): " + error.getMessage(); return status;}

        //Create Headers
        viaHeaders = new ArrayList();
        try {  viaHeader = headerFactory.createViaHeader( via, localSIPPort, transport, null ); } // Clients hostname / ip address for routing back to client and for Proxy loopdetection. null means random branch
        catch (ParseException error) { status[0] = "1"; status[1] = "Error: myViaHeader = myHeaderFactory.createViaHeader( via, myPort, myTransport, null ): ParseException: " + error.getMessage(); return status; }
        catch (InvalidArgumentException error) { status[0] = "1"; status[1] = "Error: myViaHeader = myHeaderFactory.createViaHeader( via, myPort, myTransport, null ), null): InvalidArgumentException: " + error.getMessage(); return status; }
        try { viaHeader.setParameter("branch", VoipStormTools.getRandom(6)); } catch (ParseException error) { status[0] = "1"; status[1] = "Error: myViaHeader.setParameter(\"branch\", SoftPhoneTools.getRandom(6)): ParseException: " + error.getMessage(); return status; }

//        try { viaHeader.setParameter("rport", null); } catch (ParseException error) { status[0] = "1"; status[1] = "Error: myViaHeader.setParameter(\"rport\", null): ParseException: " + error.getMessage(); return status; } // not in communicator

        viaHeaders.add(viaHeader);

        try { maxForwardsHeader = headerFactory.createMaxForwardsHeader(MAXFORWARDS); }
        catch (InvalidArgumentException error) { status[0] = "1"; status[1] = "Error: myHeaderFactory.createMaxForwardsHeader(MAXFORWARDS): " + error.getMessage(); return status; }

        try { fromHeader = headerFactory.createFromHeader(fromAddress, VoipStormTools.getRandom(6)); }
        catch (ParseException error) { status[0] = "1"; status[1] = "Error: myHeaderFactory.createFromHeader(addressOfRecord, \"RNDNUMSTRING\"): " + error.getMessage(); return status; } // Tag used together with CallId for identification

        try { toHeader = headerFactory.createToHeader(toAddress, null); }
        catch (ParseException error) { status[0] = "1"; status[1] = "Error: myHeaderFactory.createToHeader(destAddress, null): " + error.getMessage(); return status; }

        if ((configuration.getPublicIP().length() > 0) && (! isSIPAddress))
        {
            try { publicContactHeader.setParameter("transport", transport); } catch (ParseException error) { status[0] = "1"; status[1] = "Error: myContactHeader.setParameter(\"transport\", transport): ParseException: " + error.getMessage(); return status; }
            try { privateContactHeader.setParameter("registering_acc", "sip1_budgetphone_nl"); } catch (ParseException error) { status[0] = "1"; status[1] = "Error: myContactHeader.setParameter(\"registering_acc\", sip1_budgetphone_nl): ParseException: " + error.getMessage(); return status; }
        }
        else
        {
            try { publicContactHeader.setParameter("transport", transport); } catch (ParseException error) { status[0] = "1"; status[1] = "Error: myContactHeader.setParameter(\"transport\", transport): ParseException: " + error.getMessage(); return status; }
            try { privateContactHeader.setParameter("registering_acc", "sip1_budgetphone_nl"); } catch (ParseException error) { status[0] = "1"; status[1] = "Error: myContactHeader.setParameter(\"registering_acc\", sip1_budgetphone_nl): ParseException: " + error.getMessage(); return status; }
        }

        callIdHeader = sipProvider.getNewCallId();
        commandSequenceHeader = null; try { commandSequenceHeader = headerFactory.createCSeqHeader(1L, Request.INVITE ); } // 1L = long type, not integer
        catch (ParseException error) { status[0] = "1"; status[1] = "Error: myHeaderFactory.createCSeqHeader(1, " + Request.INVITE + " ): ParseException: " + error.getMessage(); return status; }
        catch (InvalidArgumentException error) { status[0] = "1"; status[1] = "Error: myHeaderFactory.createCSeqHeader(1, " + Request.INVITE + " ): InvalidArgumentException: " + error.getMessage(); return status; }
//        myContactHeader = myHeaderFactory.createContactHeader(contactAddress);
//        try { myContactHeader.setParameter("transport", myTransport); } catch (ParseException error) { status[0] = "1"; status[1] = "Error: myContactHeader.setParameter(\"transport\", \"udp\"): ParseException: " + error.getMessage(); return status; }
//        try { myContactHeader.setParameter("expire", Integer.toString(inviteTimeout)); } catch (ParseException error) { status[0] = "1"; status[1] = "Error: myContactHeader.setParameter(\"expire\", \"inviteTimeoutParam\"): ParseException: " + error.getMessage(); return status; }

        //Create Request

        try     { destAddress = addressFactory.createAddress(sipDestination); } // To whom this message is for
        catch (ParseException error) { status[0] = "1"; status[1] = "Error: myAddressFactory.createAddress(destination): " + error.getMessage(); return status;}

        requestURI = destAddress.getURI();

        try { request = messageFactory.createRequest(requestURI, Request.INVITE, callIdHeader, commandSequenceHeader, fromHeader, toHeader, viaHeaders, maxForwardsHeader); }
        catch (ParseException error) { status[0] = "1"; status[1] = "Error: myMessageFactory.createRequest(myRequestURI, " + Request.INVITE + " , myCallIdHeader, myCSeqHeader, myFromHeader, myToHeader, viaHeaders, myMaxForwardsHeader): " + error.getMessage(); return status; }

//        if ((loginstate == LOGINSTATE_REGISTERED) || (!isSIPAddress) || (configuration.getPublicIP().length() > 0))
//        if (configuration.getPublicIP().length() > 0) // not in communicator
//        {
//            try { routeAddress = addressFactory.createAddress("sip:" + configuration.getServerIP() + ";lr"); } catch (ParseException error) { status[0] = "1"; status[1] = "Error: myAddressFactory.createAddress(\"sip:\" + sipServer + \";lr\"): " + error.getMessage(); return status; }
//            routeHeader = headerFactory.createRouteHeader(routeAddress);
//
//            try { request.addFirst(routeHeader); }
//            catch (SipException error) { status[0] = "1"; status[1] = "Error: myRequest.addFirst(myRouteHeader): SipException: " + error.getMessage(); return status; }
//            catch (NullPointerException error) { status[0] = "1"; status[1] = "Error: myRequest.addFirst(myRouteHeader): NullPointerException: " + error.getMessage(); return status; }
//        }
        if ((configuration.getPublicIP().length() > 0) && (! isSIPAddress))
        {
            request.addHeader(publicContactHeader);
        }
        else
        {
            request.addHeader(privateContactHeader);
        }
        request.addHeader(userAgentHeader);
//        request.addHeader(allowHeader); // not in communicator

        //System.out.println("myAssignedClientAudioPort: "+myAssignedClientAudioPort);

        // Generate the Session Description Protocol SDP info
        sdpConvert = new SDPConvert();
        offerSDPInfo = new SDPInfo();
//        offerSDPInfo.setIPAddress(myIP.getHostAddress());

        // NAT / Firewall Traversal
        if ((configuration.getPublicIP().length() > 0) && (! isSIPAddress))
        {
            offerSDPInfo.setIPAddress(configuration.getPublicIP());
        }
        else
        {
            offerSDPInfo.setIPAddress(configuration.getClientIP()); // communicator
        }

        offerSDPInfo.setUser(configuration.getUsername()); // communicator
        offerSDPInfo.setAudioPort(assignedClientAudioPort);
        offerSDPInfo.setAudioFormat(audioCodec);
        offerSDPInfo.setVideoPort(videoPort);
        offerSDPInfo.setVideoFormat(videoCodec);
        try { contentTypeHeader = headerFactory.createContentTypeHeader("application", "sdp"); } catch (ParseException error) { status[0] = "1"; status[1] = "Error: myHeaderFactory.createContentTypeHeader(\"application\", \"sdp\"): ParseException: " + error.getMessage(); return status; }

        offerSDPBytes = sdpConvert.info2Bytes(offerSDPInfo,audioCodec); // INVITE MESSAGE SDP Payload

        try { request.setContent(offerSDPBytes, contentTypeHeader); } catch (ParseException error) { status[0] = "1"; status[1] = "Error: myRequest.setContent(content, contentTypeHeader);: ParseException: " + error.getMessage(); return status; }


        try { clientTransaction = sipProvider.getNewClientTransaction(request); }
        catch (TransactionUnavailableException error) { status[0] = "1"; status[1] = "Error: mySipProvider.getNewClientTransaction(myRequest): TransactionUnavailableException: " + error.getMessage(); return status; }

        do
        {
            status[0] = "0"; status[1] = "";
            try { clientTransaction.sendRequest(); } catch (SipException error)
            {
                status[0] = "1"; status[1] = "Error: sendRequest: myClientTransaction.sendRequest(): SipException" + error.getMessage();
                userInterface1.logToApplication("☎ " + softPhoneInstanceId + " status[1]");
                try { Thread.sleep(retryInterval); } catch (InterruptedException error2) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Sleep error: " + error2.getMessage()); }
            }
        } while(status[0].equals("1"));
        inviteRequestBranch = clientTransaction.getBranchId();
        
        if (debugging == true)
        {
            if (debugginglevel == 1)
            { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " sendRequest:     " + request.getMethod() + " Request to: " + sipDestination + " Sent:\n\n" + request.toString()); }
            else
            { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " sendRequest:     " + request.getMethod() + " Request to: " + sipDestination + " Sent."); }
        }

        toHeaderString = request.getHeader("To").toString();
        namePart1 = VoipStormTools.substitude(toHeaderString, "^.+<");
        toHeaderAddress = VoipStormTools.substitude(namePart1, ">.*");
        
        lastsipstate = sipstate;
        sipstate = SIPSTATE_WAIT_CONNECT;
//        sipstate = SIPSTATE_WAIT_PROV;

        dispatchSipState(sipstate, lastsipstate, loginstate, SOFTPHONE_ACTIVITY_NORMAL, softPhoneInstanceId, destination);

        try { Thread.sleep(ultraShortMessagePeriod); } catch (InterruptedException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: Thread.sleep(ultraShortMessagePeriod);: InterruptedException: " + error.getMessage()); }

        if (ePhoneGUIActive)
        {
            displayData.setPrimaryStatusCell("Call Outgoing");
            displayData.setPrimaryStatusDetailsCell("To: " + toHeaderAddress);
            displayData.setSecondaryStatusCell("");
            displayData.setSecondaryStatusDetailsCell("");
            displayData.resetSip();
            displayData.setConnectFlag(true);
            userInterface1.phoneDisplay(displayData); if (userInterface2 != null) {userInterface2.phoneDisplay(displayData);}
        }
        return status;
    }

    /**
     *
     * @return
     */
    public String[] cancelRequest()
    {
        Request myCancelRequest = null;
        String[] status = new String[2]; status[0] = "0"; status[1] = "";

        try { myCancelRequest = clientTransaction.createCancel(); }
        catch (SipException error) { status[0] = "1"; status[1] = "Error: myCancelRequest = myClientTransaction.createCancel();: SipException: " + error.getMessage();return status; }

        if ((configuration.getPublicIP().length() > 0) && (! isSIPAddress))
        {
            myCancelRequest.addHeader(publicContactHeader);
        }
        else
        {
            myCancelRequest.addHeader(privateContactHeader);
        }

        try { clientTransaction = sipProvider.getNewClientTransaction(myCancelRequest); }
        catch (TransactionUnavailableException error) { status[0] = "1"; status[1] = "Error: myClientTransaction = mySipProvider.getNewClientTransaction(myCancelRequest): TransactionUnavailableException: " + error.getMessage();return status; }

        do
        {
            status[0] = "0"; status[1] = "";
            try { clientTransaction.sendRequest(); } catch (SipException error)
            {
                status[0] = "1"; status[1] = "Error: sendRequest: myClientTransaction.sendRequest(): SipException" + error.getMessage();
                userInterface1.logToApplication("☎ " + softPhoneInstanceId + " status[1]");
                try { Thread.sleep(retryInterval); } catch (InterruptedException error2) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Sleep error: " + error2.getMessage()); }
            }
        } while(status[0].equals("1"));

	dialog = clientTransaction.getDialog();

        if (debugging == true)
        {
            if (debugginglevel == 1) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " cancelRequest: " + myCancelRequest.getMethod() + "-Request Sent.  DialogStatus: " + dialog.getState() + "\n\n" + myCancelRequest.toString());}
            else { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " cancelRequest: " + myCancelRequest.getMethod() + "-Request Sent.  DialogStatus: " + dialog.getState());}
        }

        cancelRequestBranch = clientTransaction.getBranchId();

        stopTones();

	lastsipstate = sipstate;
	sipstate = SIPSTATE_TRANSITION_LOCALCANCEL;
	dispatchSipState(sipstate, lastsipstate, loginstate, SOFTPHONE_ACTIVITY_NORMAL, softPhoneInstanceId, destination);
	try { Thread.sleep(eyeBlinkMessagePeriod); } catch (InterruptedException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: Thread.sleep(ultraShortMessagePeriod);: InterruptedException: " + error.getMessage()); }

        lastsipstate = sipstate;
        sipstate = SIPSTATE_IDLE;

        dispatchSipState(sipstate, lastsipstate, loginstate, SOFTPHONE_ACTIVITY_NORMAL, softPhoneInstanceId, destination);
        try { Thread.sleep(ultraShortMessagePeriod); } catch (InterruptedException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: Thread.sleep(ultraShortMessagePeriod);: InterruptedException: " + error.getMessage()); }

        if (ePhoneGUIActive)
        {
            displayData.setPrimaryStatusCell("Calling: " + destinationDisplay + "...");
            displayData.setPrimaryStatusDetailsCell("");
            displayData.setSecondaryStatusCell("Call Canceled Locally");
            displayData.setSecondaryStatusDetailsCell("");
            displayData.resetSip();
            displayData.setIdleFlag(true);
            userInterface1.phoneDisplay(displayData); if (userInterface2 != null) {userInterface2.phoneDisplay(displayData);}
        }

        try { Thread.sleep(shortMessagePeriod); } catch (InterruptedException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: Thread.sleep(shortMessagePeriod);: InterruptedException: " + error.getMessage()); }

        if (ePhoneGUIActive)
        {
            displayData.setPrimaryStatusCell("Phone \"" + configuration.getUsername() + "\" " + availability);
            displayData.setPrimaryStatusDetailsCell("");
            displayData.setSecondaryStatusCell("");
            displayData.setSecondaryStatusDetailsCell("");
            userInterface1.phoneDisplay(displayData); if (userInterface2 != null) {userInterface2.phoneDisplay(displayData);}
        }

        return status;
    }

    /**
     *
     * @param responseReceivedEvent
     */
    @Override
    synchronized public void processResponse(final ResponseEvent responseReceivedEvent) // must be of returntype void unfortunately (no status feedback when client received a response (synchronized is for the wait / notify event)
    {
        Thread processResponseThread = new Thread(new Runnable() // Please remove if it fails
        {
            @Override
            public void run()
            {
                String[] status = new String[2];
                final Response response = responseReceivedEvent.getResponse();
                ClientTransaction clientTransaction = null;

//                Thread updateResponseStatusCodeThread = new Thread(new Runnable() // Please remove if it fails
//                {
//                    @Override
//                    public void run()
//                    {
                        destination.setResponseStatusCode(response.getStatusCode());
                        destination.setResponseStatusDesc(response.getReasonPhrase());
                        userInterface1.responseUpdate(response.getStatusCode(), response.getReasonPhrase(), softPhoneInstanceId, destination);
//                    }
//                });
//                updateResponseStatusCodeThread.setName("updateResponseStatusCodeThread");
//                updateResponseStatusCodeThread.setDaemon(runThreadsAsDaemons);
//                updateResponseStatusCodeThread.start();

                String myResponseBranch = new String();
                if (responseReceivedEvent.getClientTransaction() != null) { clientTransaction = responseReceivedEvent.getClientTransaction(); myResponseBranch = responseReceivedEvent.getClientTransaction().getBranchId(); }

                if ( ( response.getStatusCode() >= 100 ) && ( response.getStatusCode() < 180 ) ) // Trying etc.
                {
                    if (debugging == true)
                    {
                        if (debugginglevel == 1) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + response.getStatusCode() + " " + response.getReasonPhrase() + " Response Received & Recognized.\n\n" + response.toString()); }
                        else { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + response.getStatusCode() + " " + response.getReasonPhrase() + " Response Received & Recognized."); }
                    }

//                    myClientTransaction = responseReceivedEvent.getClientTransaction();

                    dialog = clientTransaction.getDialog();

                    lastsipstate = sipstate;
                    sipstate = SIPSTATE_WAIT_PROV;

                    dispatchSipState(sipstate, lastsipstate, loginstate, SOFTPHONE_ACTIVITY_NORMAL, softPhoneInstanceId, destination);
                    try { Thread.sleep(ultraShortMessagePeriod); } catch (InterruptedException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: Thread.sleep(ultraShortMessagePeriod);: InterruptedException: " + error.getMessage()); }

                    if (ePhoneGUIActive)
                    {
                        displayData.setPrimaryStatusCell("Call Outgoing");
                        displayData.setPrimaryStatusDetailsCell("To: " + toHeaderAddress);
                        displayData.setSecondaryStatusCell("Phone: " + response.getStatusCode() + " " + response.getReasonPhrase());
                        displayData.setSecondaryStatusDetailsCell("");
                        userInterface1.phoneDisplay(displayData); if (userInterface2 != null) {userInterface2.phoneDisplay(displayData);}
                    }
                }
                else if ( ( response.getStatusCode() >= 180 ) && ( response.getStatusCode() < 200 ) ) // Phone is Ringing, Progressing etc.
                {
                    if (debugging == true)
                    {
                        if (debugginglevel == 1) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + response.getStatusCode() + " " + response.getReasonPhrase() + " Response Received & Recognized DialogStatus: " + dialog.getState() + "\n\n" + response.toString()); }
                        else { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + response.getStatusCode() + " " + response.getReasonPhrase() + " Response Received & Recognized DialogStatus: " + dialog.getState() + "."); }
                    }
//                    myClientTransaction = responseReceivedEvent.getClientTransaction();
                    dialog = clientTransaction.getDialog();

        //	    if ( myResponse.getStatusCode() == 180 ) { ToHeader myTMPToHeader; myTMPToHeader = (ToHeader) myResponse.getHeader("To:"); myToHeaderTag = myTMPToHeader.getTag(); }
                    stopTones();
                    speakerData.setCallToneFlag(true);
                    userInterface1.speaker(speakerData);

                    lastsipstate = sipstate;
                    sipstate = SIPSTATE_WAIT_FINAL;

                    dispatchSipState(sipstate, lastsipstate, loginstate, SOFTPHONE_ACTIVITY_NORMAL, softPhoneInstanceId, destination);
                    try { Thread.sleep(ultraShortMessagePeriod); } catch (InterruptedException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: Thread.sleep(ultraShortMessagePeriod);: InterruptedException: " + error.getMessage()); }

                    if (ePhoneGUIActive)
                    {
                        displayData.setPrimaryStatusCell("Call Outgoing");
                        displayData.setPrimaryStatusDetailsCell("To: " + toHeaderAddress);
                        displayData.setSecondaryStatusCell("Phone: " + response.getStatusCode() + " " + response.getReasonPhrase());
                        displayData.setSecondaryStatusDetailsCell("");
                        displayData.resetSip();
            //            displayData.setIdleFlag(false);
                        displayData.setCallingFlag(true);
                        userInterface1.phoneDisplay(displayData); if (userInterface2 != null) {userInterface2.phoneDisplay(displayData);}
                    }

                    if (scan)
                    {
//                        try { Thread.sleep(shortMessagePeriod); } catch (InterruptedException error) { userInterface1.log("☎ " + softPhoneInstanceId + " Error: Thread.sleep(shortMessagePeriod);: InterruptedException: " + error.getMessage()); }
                        status = userInput(ENDBUTTON, "", "", "");
                        if (status[0].equals("1")) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " End Button Failure: " + status[1]); }
                    }
                }
                else if ( response.getStatusCode() == 200 ) // Lots of 200's but first we're after 200 after (INVITE or REGISTER)
                {
                    // We're receiving multiple OK Responses with different meanings, so let's test to what Request they belong
                    if ( myResponseBranch.equals(registerRequestBranch) ) // 200
                    {
//                        myClientTransaction = responseReceivedEvent.getClientTransaction();
                        dialog = clientTransaction.getDialog();

                        if (debugging == true)
                        {
                            if (debugginglevel == 1) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + response.getStatusCode() + " " + response.getReasonPhrase() + " (REGISTER) Response Received & Recognized.\n\n" + response.toString()); }
                            else { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + response.getStatusCode() + " " + response.getReasonPhrase() + " (REGISTER) Response Received & Recognized."); }
                        }

        //                if (registerTimeout > 0)

                        if ((response != null) && (response.getExpires() != null)) // 1325 comes a null pointer acception and I can't figure out what is causing it
                        {
                            if ( response.getExpires().getExpires() > 0) // Expiry of 0 means unregister and > 0 means register
                            {
                                loginstate = LOGINSTATE_REGISTERED;

                                dispatchSipState(sipstate, lastsipstate, loginstate, SOFTPHONE_ACTIVITY_REGISTRATION, softPhoneInstanceId, destination);
//                                        try { Thread.sleep(ultraShortMessagePeriod); } catch (InterruptedException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: Thread.sleep(ultraShortMessagePeriod);: InterruptedException: " + error.getMessage()); }

                                availability = registered;

                                if (ePhoneGUIActive)
                                {
                                    displayData.setProxyInfoCell("Proxy: " + configuration.getServerIP() + ":" + configuration.getServerPort());
                                    displayData.setPrimaryStatusCell("Phone \"" + configuration.getUsername() + "\" " + availability);
                                    displayData.setRegisteredFlag(true);
                                    userInterface1.phoneDisplay(displayData); if (userInterface2 != null) {userInterface2.phoneDisplay(displayData);}
                                    speakerData.setRegisterEnabledToneFlag(true);
                                    userInterface1.speaker(speakerData);
                                    speakerData.setRegisterEnabledToneFlag(false);
                                }
                            }
                            else
                            {
            //                    lastloginstate = loginstate;
                                loginstate  = LOGINSTATE_UNREGISTERED;
            //                    myUserInterface.loginstateUpdate(loginstate, lastloginstate);

                                dispatchSipState(sipstate, lastsipstate, loginstate, SOFTPHONE_ACTIVITY_REGISTRATION, softPhoneInstanceId, destination);
                                try { Thread.sleep(ultraShortMessagePeriod); } catch (InterruptedException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: Thread.sleep(ultraShortMessagePeriod);: InterruptedException: " + error.getMessage()); }

                                availability = unregistered;

                                if (ePhoneGUIActive)
                                {
                                    displayData.setProxyInfoCell("");
                                    displayData.setPrimaryStatusCell("Phone \"" + configuration.getUsername() + "\" " + availability);
                                    displayData.setRegisteredFlag(false);
                                    userInterface1.phoneDisplay(displayData); if (userInterface2 != null) {userInterface2.phoneDisplay(displayData);}
                                    speakerData.setRegisterDisabledToneFlag(true);
                                    userInterface1.speaker(speakerData);
                                    speakerData.setRegisterDisabledToneFlag(false);
                                }

                                if (powerOffRequested) { stopListener();}
                            }
                        }
                    }
                    else if ( myResponseBranch.equals(inviteRequestBranch) ) // 200 After OK (aswerer answered the call))
                    {
                        // Probably also OK responses to outgoing cancelationrequests are processed here incorrectly
                        // Fortunately they coincidently get blocked from execution by a nullpointer exception at the "get the SDPAnswer" in this section
                        stopTones();

                        //Create statefull transaction
//                        myClientTransaction = responseReceivedEvent.getClientTransaction();

                        if (debugging == true)
                        {
                            if (debugginglevel == 1)
                                    { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + response.getStatusCode() + " " + response.getReasonPhrase() + " (INVITE) Response Received & Recognized.\n\n" + response.toString()); }
                            else    { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + response.getStatusCode() + " " + response.getReasonPhrase() + " (INVITE) Response Received & Recognized."); }
                        }

                        //First get the SDP ansswer
                        byte[] answerSDPBytes = response.getRawContent();
//                        answerSDPBytes = (byte[]) response.getContent();

                        if (answerSDPBytes != null)
                        {
                            answerSDPInfo = sdpConvert.bytes2Info(answerSDPBytes,audioCodec); // Answerer picked up phone informing us about remote audioport
//                            ViaHeader tmpViaHeader = (ViaHeader) response.getHeader("Via"); answerSDPInfo.setIPAddress(tmpViaHeader.getReceived()); // Workaround solution for SessionDescription.getConnection().getAddress() bug
                            soundStreamer.updateDestination(answerSDPInfo.getIPAddress(), Integer.toString(answerSDPInfo.getAudioPort()));

                            if (debugging == true)
                            {
                                userInterface1.logToApplication("☎ " + softPhoneInstanceId + " soundStreamer.updateDestination(" + answerSDPInfo.getIPAddress() + ","  + Integer.toString(answerSDPInfo.getAudioPort()) + ")");
                            }

                        } // nullpointer after caller cancles call
                        else
                        {
                                userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: answerSDPBytes == null");
                        }

//                        System.out.println("Received response block: " + response.toString());
//                        System.out.println("Received SDPinfo: answerSDPInfo.toString()" + answerSDPInfo.toString());

//                        if (answerSDPInfo.getVideoPort() > 0 )
//                        {
//                            myVideoTool = new VideoTool();
//                            myVideoTool.startListener(offerSDPInfo.getIPAddress(), Integer.toString(offerSDPInfo.getVideoPort()), Integer.toString(answerSDPInfo.getAudioPort()), Integer.toString(answerSDPInfo.getAudioFormat()));
//                        }

                        try { ackRequest = clientTransaction.createAck(); } catch (SipException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myAckRequest = myClientTransaction.createAck(): SipException:" + error.getMessage()); }

                        try { dialog.sendAck(ackRequest); } catch (SipException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myDialog.sendAck(myAckRequest): SipException: " + error.getMessage() + " localhost ?");  }

                        if (debugging == true)
                        {
                            if (debugginglevel == 1) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + ackRequest.getMethod() + "-Request Sent. DialogStatus: " + dialog.getState() + "\n\n" + ackRequest.toString()); }
                            else { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + ackRequest.getMethod() + "-Request Sent. DialogStatus: " + dialog.getState()); }
                        }

                        stopTones();
                        
                        lastsipstate = sipstate;
                        sipstate = SIPSTATE_ESTABLISHED;

                        dispatchSipState(sipstate, lastsipstate, loginstate, SOFTPHONE_ACTIVITY_NORMAL, softPhoneInstanceId, destination);
                        try { Thread.sleep(ultraShortMessagePeriod); } catch (InterruptedException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: Thread.sleep(ultraShortMessagePeriod);: InterruptedException: " + error.getMessage()); }

                        //myUserInterface.showStatus("Call Answered");

                        if (ePhoneGUIActive)
                        {
                            displayData.setPrimaryStatusCell("Call Outgoing");
                            displayData.setPrimaryStatusDetailsCell("To: " + toHeaderAddress);
                            displayData.setSecondaryStatusCell("Call Established");
                            displayData.setSecondaryStatusDetailsCell("");
                            displayData.resetSip();
                            displayData.setTalkingFlag(true);
                            userInterface1.phoneDisplay(displayData); if (userInterface2 != null) {userInterface2.phoneDisplay(displayData);}
                        }

                        if ((soundStreamer != null) && (answerSDPInfo != null) && (filename != null))
                        {
                            try { status = soundStreamer.startStreamer(answerSDPInfo.getIPAddress(), Integer.toString(answerSDPInfo.getAudioPort()), filename, answerSDPInfo.getAudioFormat()); }
                            catch (NoDataSourceException ex)        { userInterface1.showStatus("Error: SoftPhone: NoDataSourceException: soundStreamer.startStreamer(.......) " + ex.getMessage(), true, true); }
                            catch (NoProcessorException ex)         { userInterface1.showStatus("Error: SoftPhone: NoProcessorException: soundStreamer.startStreamer(.......) " + ex.getMessage(), true, true); }
                            catch (UnsupportedFormatException ex)   { userInterface1.showStatus("Error: SoftPhone: UnsupportedFormatException: soundStreamer.startStreamer(.......) " + ex.getMessage(), true, true); }

                            if (status[0].equals("1"))
                            {
                                userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: status(" + assignedClientAudioPort + ") = soundStreamer.startStreamer(" + answerSDPInfo.getIPAddress() + ","  + Integer.toString(answerSDPInfo.getAudioPort()) + "," + filename + "," + answerSDPInfo.getAudioFormat() + ")");
                                userInterface1.logToApplication(status[1]);
                            }
                            else
                            {
//                                if (debugging == true) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " status(" + assignedClientAudioPort + ") = soundStreamer.startStreamer(" + answerSDPInfo.getIPAddress() + ","  + Integer.toString(answerSDPInfo.getAudioPort()) + "," + filename + "," + answerSDPInfo.getAudioFormat() + ")"); }
                                long mediaDuration = 1000; mediaDuration = soundStreamer.mediaDuration;
                                if ( autoEndCall) { outboundEndCallTimer = new Timer(); outboundEndCallTimer.schedule(new EndCallTimer(softPhoneReference), (long)(mediaDuration)); }
                            }
                        } else { userInterface1.logToApplication("Error: SoftPhone: Line 1469: Could not start SoundStreamer: soundStreamer == null"); }
                    }
                    else if ( myResponseBranch.equals(inviteAckRequestBranch) ) // 200
                    {
                        if (debugging == true)
                        {
                            if (debugginglevel == 1) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + response.getStatusCode() + " " + response.getReasonPhrase() + " (After Ack?) Response Received & Recognized.\n\n" + response.toString()); }
                            else { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + response.getStatusCode() + " " + response.getReasonPhrase() + " (After Ack?) Response Received & Recognized."); }
                        }

                        //Create statefull transaction
//                        myClientTransaction = responseReceivedEvent.getClientTransaction();
                        dialog = clientTransaction.getDialog();

                        stopTones();
                        
                        lastsipstate = sipstate;
                        sipstate = SIPSTATE_ESTABLISHED;

                        dispatchSipState(sipstate, lastsipstate, loginstate, SOFTPHONE_ACTIVITY_NORMAL, softPhoneInstanceId, destination);
                        try { Thread.sleep(ultraShortMessagePeriod); } catch (InterruptedException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: Thread.sleep(ultraShortMessagePeriod);: InterruptedException: " + error.getMessage()); }

                        if (ePhoneGUIActive)
                        {
                            displayData.setPrimaryStatusCell("Call Outgoing");
                            displayData.setPrimaryStatusDetailsCell("To: " + toHeaderAddress);
                            displayData.setSecondaryStatusCell("Call Established");
                            displayData.setSecondaryStatusDetailsCell("");
                            displayData.resetSip();
                            displayData.setTalkingFlag(true);
                            userInterface1.phoneDisplay(displayData); if (userInterface2 != null) {userInterface2.phoneDisplay(displayData);}
                        }
                    }
                    else if ( myResponseBranch.equals(cancelRequestBranch) ) // 200
                    {
                        if (debugging == true)
                        {
                            if (debugginglevel == 1) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + response.getStatusCode() + " OK (After CANCEL) Response Received & Recognized.\n\n" + response.toString()); }
                            else { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + response.getStatusCode() + " " + response.getReasonPhrase() + " (After CANCEL) Response Received & Recognized."); }
                        }

                        //Create statefull transaction
//                        myClientTransaction = responseReceivedEvent.getClientTransaction();
                        dialog = clientTransaction.getDialog();

                        if (debugging == true)
                        {
                            if (debugginglevel == 1) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + response.getStatusCode() + " (Cancel OK) Response Recognized."); }
                            else { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + response.getStatusCode() + " " + response.getReasonPhrase() + " (Cancel OK) Response Recognized. DialogStatus: " + dialog.getState()); }
                        }

                        lastsipstate = sipstate;
                        sipstate = SIPSTATE_IDLE;

                        dispatchSipState(sipstate, lastsipstate, loginstate, SOFTPHONE_ACTIVITY_NORMAL, softPhoneInstanceId, destination);
                        try { Thread.sleep(ultraShortMessagePeriod); } catch (InterruptedException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: Thread.sleep(ultraShortMessagePeriod);: InterruptedException: " + error.getMessage()); }

                        if (ePhoneGUIActive)
                        {
                            displayData.setPrimaryStatusCell("Call Outgoing");
                            displayData.setPrimaryStatusDetailsCell("To: " + toHeaderAddress);
                            displayData.setSecondaryStatusCell("Call Canceled Locally");
                            displayData.setSecondaryStatusDetailsCell("");
                            displayData.resetSip();
                            displayData.setIdleFlag(true);
                            userInterface1.phoneDisplay(displayData); if (userInterface2 != null) {userInterface2.phoneDisplay(displayData);}
                            try { Thread.sleep(shortMessagePeriod); } catch (InterruptedException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: Thread.sleep(shortMessagePeriod);: InterruptedException: " + error.getMessage()); }
                            displayData.setPrimaryStatusCell("Phone \"" + configuration.getUsername() + "\" " + availability);
                            displayData.setSecondaryStatusCell("");
                            displayData.setPrimaryStatusDetailsCell("");
                            displayData.setSecondaryStatusDetailsCell("");
                            userInterface1.phoneDisplay(displayData); if (userInterface2 != null) {userInterface2.phoneDisplay(displayData);}
                        }
                    }
                    else if ( myResponseBranch.equals(byeRequestBranch) ) // 200
                    {
                        if (debugging == true)
                        {
                            if (debugginglevel == 1) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + response.getStatusCode() + " " + response.getReasonPhrase() + " (After Bye) Response Received & Recognized.\n\n" + response.toString()); }
                            else { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + response.getStatusCode() + " " + response.getReasonPhrase() + "  (After Bye) Response Received & Recognized."); }
                        }

                        //Create statefull transaction
//                        myClientTransaction = responseReceivedEvent.getClientTransaction();
                        dialog = clientTransaction.getDialog();

                        lastsipstate = sipstate;
                        sipstate = SIPSTATE_IDLE;

                        dispatchSipState(sipstate, lastsipstate, loginstate, SOFTPHONE_ACTIVITY_NORMAL, softPhoneInstanceId, destination);
                        try { Thread.sleep(ultraShortMessagePeriod); } catch (InterruptedException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: Thread.sleep(ultraShortMessagePeriod);: InterruptedException: " + error.getMessage()); }

                        if (ePhoneGUIActive)
                        {
                            displayData.setPrimaryStatusCell("Call Outgoing");
                            displayData.setPrimaryStatusDetailsCell("To: " + toHeaderAddress);
                            displayData.setSecondaryStatusCell("Call Ended Locally");
                            displayData.setSecondaryStatusDetailsCell("");
                            displayData.resetSip();
                            displayData.setIdleFlag(true);
                            userInterface1.phoneDisplay(displayData); if (userInterface2 != null) {userInterface2.phoneDisplay(displayData);}
                            try { Thread.sleep(shortMessagePeriod); } catch (InterruptedException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: Thread.sleep(shortMessagePeriod);: InterruptedException: " + error.getMessage()); }
                            displayData.setPrimaryStatusCell("Phone \"" + configuration.getUsername() + "\" " + availability);
                            displayData.setSecondaryStatusCell("");
                            displayData.setPrimaryStatusDetailsCell("");
                            displayData.setSecondaryStatusDetailsCell("");
                            userInterface1.phoneDisplay(displayData); if (userInterface2 != null) {userInterface2.phoneDisplay(displayData);}
                        }
                    }
                    else // 200 no branch correlated
                    {
                        if (debugging == true)
                        {
                            if (debugginglevel == 1) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + response.getStatusCode() + " " + response.getReasonPhrase() + " Uncorrelated Response Received & Recognized.\n\n" + response.toString()); }
                            else { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + response.getStatusCode() + " " + response.getReasonPhrase() + " Uncorrelated Response Received & Recognized."); }
                        }
                    }
                }

                else if ( ( response.getStatusCode() >= 300 ) && ( response.getStatusCode() <= 400 ) ) // All sorts of failures
                {
                    if (debugging == true)
                    {
//                        if (debugginglevel == 1) { userInterface1.log("☎ " + softPhoneInstanceId + " processResponse: " + myResponse.getStatusCode() + " Response Received & Recognized DialogStatus: " + myDialog.getState() + "\n\n" + myResponse.toString()); }
                        if (debugginglevel == 1) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + response.getStatusCode() + " " + response.getReasonPhrase() + " Response Received & Recognized\n\n" + response.toString()); }
                        else { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + response.getStatusCode() + " " + response.getReasonPhrase() + " Response Received & Recognized DialogStatus: " + dialog.getState() + "."); }
                    }

//                    myClientTransaction = responseReceivedEvent.getClientTransaction();
                    dialog = clientTransaction.getDialog();

                    lastsipstate = sipstate;
                    sipstate = SIPSTATE_IDLE;
                    dispatchSipState(sipstate, lastsipstate, loginstate, SOFTPHONE_ACTIVITY_NORMAL, softPhoneInstanceId, destination);
                    try { Thread.sleep(ultraShortMessagePeriod); } catch (InterruptedException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: Thread.sleep(ultraShortMessagePeriod);: InterruptedException: " + error.getMessage()); }

                    stopTones();
                    if (soundStreamer != null) { soundStreamer.stopListener(); soundStreamer = null; }

                    if (ePhoneGUIActive)
                    {
                        displayData.setPrimaryStatusCell("Call Outgoing");
                        displayData.setPrimaryStatusDetailsCell("To: " + toHeaderAddress);
                        displayData.setSecondaryStatusCell(response.getReasonPhrase());
                        displayData.setSecondaryStatusDetailsCell("");
                        displayData.resetSip();
                        displayData.setIdleFlag(true);
                        userInterface1.phoneDisplay(displayData); if (userInterface2 != null) {userInterface2.phoneDisplay(displayData);}
                        try { Thread.sleep(shortMessagePeriod); } catch (InterruptedException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: Thread.sleep(shortMessagePeriod);: InterruptedException: " + error.getMessage()); }
                        displayData.setPrimaryStatusCell("Phone \"" + configuration.getUsername() + "\" " + availability);
                        displayData.setSecondaryStatusCell("");
                        displayData.setPrimaryStatusDetailsCell("");
                        displayData.setSecondaryStatusDetailsCell("");
                        userInterface1.phoneDisplay(displayData); if (userInterface2 != null) {userInterface2.phoneDisplay(displayData);}
                        speakerData.setErrorToneFlag(true);
                        userInterface1.speaker(speakerData);
                        speakerData.setErrorToneFlag(false);
                    }
                }
                else if ( response.getStatusCode() == 401 ) // 401 UNAUTHORIZED
                {
                    if ( myResponseBranch.equals(registerRequestBranch) )
                    {
                        if (registerRequestCounter == 2) { return;}
                        registerRequestCounter++;

                        if (debugging == true)
                        {
                            if (debugginglevel == 1) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + response.getStatusCode() + " " + response.getReasonPhrase() + " Response Received & Recognized.\n\n" + response.toString()); }
                            else { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + response.getStatusCode() + " " + response.getReasonPhrase() + " Response Received & Recognized."); }
                        }

//                        myResponse = responseReceivedEvent.getResponse();

                        //Create Headers
                        viaHeaders = new ArrayList();
                        viaHeader = (ViaHeader) response.getHeader("Via");
                        try { viaHeader.setParameter("branch", VoipStormTools.getRandom(6)); } catch (ParseException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myViaHeader.setParameter(\"branch\", SoftPhoneTools.getRandom(6)): ParseException: " + error.getMessage()); }
                        viaHeaders.add(viaHeader);

                        try { maxForwardsHeader = headerFactory.createMaxForwardsHeader(MAXFORWARDS); }
                        catch (InvalidArgumentException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myHeaderFactory.createMaxForwardsHeader(MAXFORWARDS): " + error.getMessage()); }

                        fromHeader    = (FromHeader) response.getHeader("From");
                        toHeader      = (ToHeader) response.getHeader("To");
                        callIdHeader  = (CallIdHeader) response.getHeader("Call-ID");

                        try { commandSequenceHeader = headerFactory.createCSeqHeader((long)2, "REGISTER" ); }
                        catch (ParseException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myHeaderFactory.createCSeqHeader(1, " + Request.REGISTER + " ): ParseException: " + error.getMessage()); }
                        catch (InvalidArgumentException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myHeaderFactory.createCSeqHeader(1, " + Request.REGISTER + " ): InvalidArgumentException: " + error.getMessage()); }

                        if ((configuration.getPublicIP().length() > 0) && (! isSIPAddress))
                        {
                            try { publicContactHeader.setParameter("transport", transport); } catch (ParseException error) { status[0] = "1"; status[1] = "Error: publicContactHeader.setParameter\\(\"transport\",: ParseException: " + error.getMessage(); }
                            try { publicContactHeader.setParameter("transport", transport); } catch (ParseException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: publicContactHeader.setParameter(\"transport\", \"udp\"): ParseException: " + error.getMessage()); }
                            try { publicContactHeader.setParameter("expires", Integer.toString(registerTimeout)); }
                            catch (ParseException error) { status[0] = "1"; status[1] = "Error: myContactHeader.setParameter\\(\"transport\",: ParseException: " + error.getMessage(); }
                        }
                        else
                        {
                            try { privateContactHeader.setParameter("transport", transport); } catch (ParseException error) { status[0] = "1"; status[1] = "Error: privateContactHeader.setParameter\\(\"transport\",: ParseException: " + error.getMessage(); }
                            try { privateContactHeader.setParameter("transport", transport); } catch (ParseException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: privateContactHeader.setParameter(\"transport\", \"udp\"): ParseException: " + error.getMessage()); }
                            try { privateContactHeader.setParameter("expires", Integer.toString(registerTimeout)); }
                            catch (ParseException error) { status[0] = "1"; status[1] = "Error: myContactHeader.setParameter\\(\"transport\",: ParseException: " + error.getMessage(); }
                        }

        //              ================ Above is the Header copying / preparation part, below is the create 2nd INVITE request part

                        //Create Request
                        try { registerRequest = messageFactory.createRequest(requestURI, Request.REGISTER, callIdHeader, commandSequenceHeader, fromHeader, toHeader, viaHeaders, maxForwardsHeader); }
                        catch (ParseException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myRegisterRequest = myMessageFactory.createRequest(: ParseException: " + error.getMessage()); }

                        if (configuration.getPublicIP().length() > 0) // leave privateContactHeader for now (just registering)
                        {
                            registerRequest.addHeader(privateContactHeader);
                        }
                        else
                        {
                            registerRequest.addHeader(privateContactHeader);
                        }

                        try { expiresHeader = headerFactory.createExpiresHeader(registerTimeout); }
                        catch (InvalidArgumentException error) { status[0] = "1"; status[1] = "Error: myExpiresHeader = myHeaderFactory.createExpiresHeader(registerTimeout);: InvalidArgumentException: " + error.getMessage(); }
                        registerRequest.addHeader(expiresHeader);

                        WWWAuthenticateHeader myWWWAuthenticateHeader = (WWWAuthenticateHeader) response.getHeader(SIPHeaderNames.WWW_AUTHENTICATE.toString());
                        String algorithm = new String(); if (myWWWAuthenticateHeader.getAlgorithm() != null) {algorithm = myWWWAuthenticateHeader.getAlgorithm(); } else {algorithm="MD5";}
                        String realm            = myWWWAuthenticateHeader.getRealm();
                        String nonce_value      = myWWWAuthenticateHeader.getNonce();
                        String nc_value         = myWWWAuthenticateHeader.getParameter("nc");
                        String cnonce_value     = myWWWAuthenticateHeader.getParameter("cnonce");
                        String method_value     = Request.REGISTER;
                        String digest_uri_value = requestURI.toString();
                        String entity_body      = myWWWAuthenticateHeader.getParameter("entity_body");
                        String qop_value        = myWWWAuthenticateHeader.getQop();
                        String scheme           = myWWWAuthenticateHeader.getScheme();
                        String domain           = myWWWAuthenticateHeader.getDomain();
                        String responseString   = messageDigestAlgorithm.calculateResponse(
                                                                                            algorithm,
                                                                                            configuration.getUsername(),
                                                                                            myWWWAuthenticateHeader.getRealm(),
                                                                                            configuration.getToegang(),
                                                                                            myWWWAuthenticateHeader.getNonce(),
                                                                                            myWWWAuthenticateHeader.getParameter("nc"),
                                                                                            myWWWAuthenticateHeader.getParameter("cnonce"),
                                                                                            Request.REGISTER,
                                                                                            digest_uri_value,
                                                                                            requestURI.toString(),
                                                                                            myWWWAuthenticateHeader.getQop()
                                                                                          );
                        try { authorizationHeader = headerFactory.createAuthorizationHeader(scheme); }  catch (ParseException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myHeaderFactory.createAuthorizationHeader(\"Digest\"): " + error.getMessage());  }
                        try { authorizationHeader.setUsername(configuration.getUsername());  }                               catch (ParseException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myAuthorizationHeader.setUsername(\"\"): " + error.getMessage());  }
                        try { authorizationHeader.setAlgorithm(algorithm);  }                             catch (ParseException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myAuthorizationHeader.setAlgorithm(\"MD5\"): " + error.getMessage());  }
                        try { authorizationHeader.setRealm(realm); }                                      catch (ParseException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myAuthorizationHeader.setRealm(realm): " + error.getMessage());  }
                        try { authorizationHeader.setNonce(nonce_value); }                                catch (ParseException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myAuthorizationHeader.setNonce(nonce): " + error.getMessage()); }
                        try { authorizationHeader.setResponse(responseString); }                                catch (ParseException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myAuthorizationHeader.setResponse(response): " + error.getMessage());  }
                        registerRequest.addHeader(authorizationHeader);

                        // INVITE Request is now fully built and it needs to be send
                        try { clientTransaction = sipProvider.getNewClientTransaction(registerRequest); }
                        catch (TransactionUnavailableException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: mySipProvider.getNewClientTransaction(myRequest): TransactionUnavailableException: " + error.getMessage()); }

                        do
                        {
                            status[0] = "0"; status[1] = "";
                            try { clientTransaction.sendRequest(); } catch (SipException error)
                            {
                                status[0] = "1"; status[1] = "Error: processResponse: Register Unauthorized: myClientTransaction.sendRequest(): SipException" + error.getMessage();
                                userInterface1.logToApplication("☎ " + softPhoneInstanceId + " status[1]");
                                try { Thread.sleep(retryInterval); } catch (InterruptedException error2) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Sleep error: " + error2.getMessage()); }
                            }
                        } while(status[0].equals("1"));

        //                try { myClientTransaction.sendRequest(); } catch (SipException error) { myUserInterface.log("Error: myClientTransaction.sendRequest(): SipException" + error.getMessage()); } // Default
                        registerRequestBranch = clientTransaction.getBranchId();

                        dialog = clientTransaction.getDialog();

                        if (debugging == true)
                        {
                            if (debugginglevel == 1) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + registerRequest.getMethod() + "-Request with Authorization Header Sent:\n\n" + registerRequest.toString()); }
                            else { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + registerRequest.getMethod() + "-Request with Authorization Header Sent."); }
                        }
                    }
                    else if ( myResponseBranch.equals(inviteRequestBranch) ) // 401 UNAUTHORIZED myInviteRequestBranch
                    {
                        if (debugging == true)
                        {
                            if (debugginglevel == 1) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + response.getStatusCode() + " " + response.getReasonPhrase() + " Response Received & Recognized.\n\n" + response.toString()); }
                            else { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + response.getStatusCode() + " " + response.getReasonPhrase() + " Response Received & Recognized."); }
                        }

                        // Send an ACK in between, just like X-Lite does :-)
                        dialog = clientTransaction.getDialog();
                        try { ackRequest = clientTransaction.createAck(); } catch (SipException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myAckRequest = myClientTransaction.createAck(): SipException:" + error.getMessage()); }
                        try { dialog.sendAck(ackRequest); } catch (SipException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myDialog.sendAck(myAckRequest): SipException: " + error.getMessage() + " localhost ?");  }

                        if (debugging == true)
                        {
                            if (debugginglevel == 1) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + ackRequest.getMethod() + "-Request Sent:\n\n" + ackRequest.toString()); }
                            else { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + ackRequest.getMethod() + "-Request Sent."); }
                        }

// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------

//                        myResponse = responseReceivedEvent.getResponse();

                        //Create Headers
                        viaHeaders = new ArrayList();
                        viaHeader = (ViaHeader) response.getHeader("Via");
                        try { viaHeader.setParameter("branch", VoipStormTools.getRandom(6)); } catch (ParseException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myViaHeader.setParameter(\"branch\", SoftPhoneTools.getRandom(6)): ParseException: " + error.getMessage()); }
                        viaHeader.removeParameter("rport"); // VoipBuster Change
                        viaHeader.removeParameter("tag"); // VoipBuster Change

                        viaHeaders.add(viaHeader);

                        try { maxForwardsHeader = headerFactory.createMaxForwardsHeader(MAXFORWARDS); }
                        catch (InvalidArgumentException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myHeaderFactory.createMaxForwardsHeader(MAXFORWARDS): " + error.getMessage()); }

                        fromHeader    = (FromHeader) response.getHeader("From");
                        toHeader      = (ToHeader) response.getHeader("To");
                        callIdHeader  = (CallIdHeader) response.getHeader("Call-ID");

                        try { commandSequenceHeader = headerFactory.createCSeqHeader((long)2, Request.INVITE ); }
                        catch (ParseException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myHeaderFactory.createCSeqHeader(1, " + Request.INVITE + " ): ParseException: " + error.getMessage()); }
                        catch (InvalidArgumentException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myHeaderFactory.createCSeqHeader(1, " + Request.INVITE + " ): InvalidArgumentException: " + error.getMessage()); }

                        if ((configuration.getPublicIP().length() > 0) && (! isSIPAddress))
                        {
                            try { publicContactHeader.setParameter("transport", transport); } catch (ParseException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myContactHeader.setParameter(\"transport\", \"udp\"): ParseException: " + error.getMessage()); }
                        }
                        else
                        {
                            try { privateContactHeader.setParameter("transport", transport); } catch (ParseException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myContactHeader.setParameter(\"transport\", \"udp\"): ParseException: " + error.getMessage()); }
                        }
                        //try { myContactHeader.setParameter("expire", Integer.toString(inviteTimeout)); } catch (ParseException error) { myUserInterface.log("Error: myContactHeader.setParameter(\"expire\", \"inviteTimeoutParam\"): ParseException: " + error.getMessage()); }

        //                try { myExpiresHeader = myHeaderFactory.createExpiresHeader(registerTimeout); }
        //                catch (InvalidArgumentException error) { status[0] = "1"; status[1] = "Error: myExpiresHeader = myHeaderFactory.createExpiresHeader(registerTimeout);: InvalidArgumentException: " + error.getMessage(); }

        //              ================ Above is the Header copying / preparation part, below is the create 2nd INVITE request part

                        try { request = messageFactory.createRequest(requestURI, Request.INVITE, callIdHeader, commandSequenceHeader, fromHeader, toHeader, viaHeaders, maxForwardsHeader); }
                        catch (ParseException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myMessageFactory.createRequest(myRequestURI, " + Request.INVITE + " , myCallIdHeader, myCSeqHeader, myFromHeader, myToHeader, viaHeaders, myMaxForwardsHeader): " + error.getMessage()); }

//                        if (loginstate == LOGINSTATE_REGISTERED)
//                        {
//                            // From here is when this request needs to be rerouted to a proxy
//                            try { routeAddress = addressFactory.createAddress("sip:" + configuration.getServerIP() + ";lr"); } catch (ParseException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myAddressFactory.createAddress(\"sip:\" + sipServer + \";lr\"): " + error.getMessage()); }
//                            routeHeader = headerFactory.createRouteHeader(routeAddress);
//
//                            try { request.addFirst(routeHeader); }
//                            catch (SipException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myRequest.addFirst(myRouteHeader): SipException: " + error.getMessage()); }
//                            catch (NullPointerException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myRequest.addFirst(myRouteHeader): NullPointerException: " + error.getMessage()); }
//                        }

                        if ((configuration.getPublicIP().length() > 0) && (! isSIPAddress))
                        {
                            request.addHeader(publicContactHeader);
                        }
                        else
                        {
                            request.addHeader(privateContactHeader);
                        }

                        // Generate the Session Description Protocol SDP info
                        sdpConvert = new SDPConvert();
                        offerSDPInfo = new SDPInfo();
//                        offerSDPInfo.setIPAddress(myIP.getHostAddress());

                        // NAT / Firewall Traversal
                        if ((configuration.getPublicIP().length() > 0) && (! isSIPAddress))
                        {
                            offerSDPInfo.setIPAddress(configuration.getPublicIP());
                        }
                        else
                        {
                            offerSDPInfo.setIPAddress(configuration.getClientIP());
                        }

                        offerSDPInfo.setUser(configuration.getUsername());
                        offerSDPInfo.setAudioPort(assignedClientAudioPort);
                        offerSDPInfo.setAudioFormat(audioCodec);
                        offerSDPInfo.setVideoPort(videoPort);
                        offerSDPInfo.setVideoFormat(videoCodec);

                        // Setting the MIME Type of the SIP Payload data (SDP)
                        try { contentTypeHeader = headerFactory.createContentTypeHeader("application", "sdp"); } catch (ParseException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myHeaderFactory.createContentTypeHeader(\"application\", \"sdp\"): ParseException: " + error.getMessage()); }
                        offerSDPBytes = sdpConvert.info2Bytes(offerSDPInfo,audioCodec); // Rebuilding Invite Request with AuthorizationHeader incl. new SDP Content
                        try { request.setContent(offerSDPBytes, contentTypeHeader); } catch (ParseException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myRequest.setContent(content, contentTypeHeader);: ParseException: " + error.getMessage()); }

                        // INVITE Request is now Rebuilt, but needs an added Authorization header
                        WWWAuthenticateHeader myWWWAuthenticateHeader = (WWWAuthenticateHeader) response.getHeader(SIPHeaderNames.WWW_AUTHENTICATE.toString());
                        String algorithm        = new String(); if (myWWWAuthenticateHeader.getAlgorithm() != null) {algorithm = myWWWAuthenticateHeader.getAlgorithm(); } else {algorithm="MD5";}
                        String realm            = myWWWAuthenticateHeader.getRealm();
                        String nonce_value      = myWWWAuthenticateHeader.getNonce();
                        String nc_value         = myWWWAuthenticateHeader.getParameter("nc");
                        String cnonce_value     = myWWWAuthenticateHeader.getParameter("cnonce");
                        String method_value     = Request.INVITE;
                        String digest_uri_value = requestURI.toString();
                        String entity_body      = myWWWAuthenticateHeader.getParameter("entity_body");
                        String qop_value        = myWWWAuthenticateHeader.getQop();
                        String scheme           = myWWWAuthenticateHeader.getScheme();
                        String domain           = myWWWAuthenticateHeader.getDomain();
                        String responseString   = messageDigestAlgorithm.calculateResponse(
                                                                                            algorithm,
                                                                                            configuration.getUsername(),
                                                                                            myWWWAuthenticateHeader.getRealm(),
                                                                                            configuration.getToegang(),
                                                                                            myWWWAuthenticateHeader.getNonce(),
                                                                                            myWWWAuthenticateHeader.getParameter("nc"),
                                                                                            myWWWAuthenticateHeader.getParameter("cnonce"),
                                                                                            Request.INVITE,
                                                                                            requestURI.toString(),
                                                                                            myWWWAuthenticateHeader.getParameter("entity_body"),
                                                                                            myWWWAuthenticateHeader.getQop()
                                                                                          );
                        try { authorizationHeader = headerFactory.createAuthorizationHeader(scheme); }  catch (ParseException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myHeaderFactory.createAuthorizationHeader(\"Digest\"): " + error.getMessage());  }
                        try { authorizationHeader.setUsername(configuration.getUsername());  }          catch (ParseException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myAuthorizationHeader.setUsername(\"\"): " + error.getMessage());  }
                        try { authorizationHeader.setAlgorithm(algorithm);  }                           catch (ParseException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myAuthorizationHeader.setAlgorithm(\"MD5\"): " + error.getMessage());  }
                        try { authorizationHeader.setRealm(realm); }                                    catch (ParseException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myAuthorizationHeader.setRealm(realm): " + error.getMessage());  }
                        try { authorizationHeader.setNonce(nonce_value); }                              catch (ParseException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myAuthorizationHeader.setNonce(nonce): " + error.getMessage()); }
                        try { authorizationHeader.setResponse(responseString); }                        catch (ParseException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myAuthorizationHeader.setResponse(response): " + error.getMessage());  }
                        request.addHeader(authorizationHeader);

                        // INVITE Request is now fully built and it needs to be send
                        try { clientTransaction = sipProvider.getNewClientTransaction(request); }
                        catch (TransactionUnavailableException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: mySipProvider.getNewClientTransaction(myRequest): TransactionUnavailableException: " + error.getMessage()); }

                        int retryCounter = 0; int retryLimit = 3;
                        do
                        {
                            status[0] = "0"; status[1] = "";
                            try { clientTransaction.sendRequest(); } catch (SipException error)
                            {
                                status[0] = "1"; status[1] = " Error: processResponse: Invite with AuthorizationHeader: line 1946: clientTransaction.sendRequest(): SipException: " + error.getMessage();
                                userInterface1.logToApplication("☎ " + softPhoneInstanceId + status[1]);
                                try { Thread.sleep(retryInterval); } catch (InterruptedException error2) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Sleep error: " + error2.getMessage()); }
                            }
                        } while((status[0].equals("1") && (retryCounter <= retryLimit)));

        //                try { myClientTransaction.sendRequest(); } catch (SipException error) { myUserInterface.log("Error: myClientTransaction.sendRequest(): SipException" + error.getMessage()); } // Default

                        inviteRequestBranch = clientTransaction.getBranchId();

                        dialog = clientTransaction.getDialog();

                        lastsipstate = sipstate;
                        sipstate = SIPSTATE_WAIT_PROV;

                        dispatchSipState(sipstate, lastsipstate, loginstate, SOFTPHONE_ACTIVITY_NORMAL, softPhoneInstanceId, destination);
                        try { Thread.sleep(ultraShortMessagePeriod); } catch (InterruptedException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: Thread.sleep(ultraShortMessagePeriod);: InterruptedException: " + error.getMessage()); }

                        if (ePhoneGUIActive)
                        {
                            displayData.resetSip();
                            displayData.setCallingFlag(true);
                            userInterface1.phoneDisplay(displayData); if (userInterface2 != null) {userInterface2.phoneDisplay(displayData);}
                        }

                        if (debugging == true)
                        {
                            if (debugginglevel == 1) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + request.getMethod() + "-Request with Authorization Header Sent:\n\n" + request.toString()); }
                            else { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + request.getMethod() + "-Request with Authorization Header Sent."); }
                        }
                    }
                    else // 401 UNAUTHORIZED UncorrelatedBranch
                    {
                        if (debugging == true)
                        {
                            if (debugginglevel == 1) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + response.getStatusCode() + " " + response.getReasonPhrase() + " Uncorrelated Response Received.\n\n" + response.toString()); }
                            else { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + response.getStatusCode() + " " + response.getReasonPhrase() + " Uncorrelated Response Received & Recognized."); }
                        }
                    }
                }
                else if ( response.getStatusCode() == 404 ) // Not Found
                {
                    if (debugging == true)
                    {
                        if (debugginglevel == 1) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + response.getStatusCode() + " " + response.getReasonPhrase() + " Response Received & Recognized.\n\n" + response.toString()); }
                        else { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + response.getStatusCode() + " " + response.getReasonPhrase() + " Response Received & Recognized."); }
                    }

//                    myClientTransaction = responseReceivedEvent.getClientTransaction();
                    dialog = clientTransaction.getDialog();

                    stopTones();
                    if (soundStreamer != null) { soundStreamer.stopListener(); soundStreamer = null; }

                    lastsipstate = sipstate;
                    sipstate = SIPSTATE_IDLE;
                    
                    dispatchSipState(sipstate, lastsipstate, loginstate, SOFTPHONE_ACTIVITY_NORMAL, softPhoneInstanceId, destination);
                    userInterface1.showStatus(response.getStatusCode() + " " + response.getReasonPhrase(), true, true);

                    if (ePhoneGUIActive)
                    {
                        speakerData.setDeadToneFlag(true);
                        userInterface1.speaker(speakerData);
                        speakerData.setDeadToneFlag(false);
                        try { Thread.sleep(ultraShortMessagePeriod); } catch (InterruptedException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: Thread.sleep(ultraShortMessagePeriod);: InterruptedException: " + error.getMessage()); }
                        displayData.setPrimaryStatusCell("Call Outgoing");
                        displayData.setPrimaryStatusDetailsCell("To: " + toHeaderAddress);
                        displayData.setSecondaryStatusCell(response.getReasonPhrase());
                        displayData.setSecondaryStatusDetailsCell("");
                        displayData.resetSip();
                        displayData.setIdleFlag(true);
                        userInterface1.phoneDisplay(displayData); if (userInterface2 != null) {userInterface2.phoneDisplay(displayData);}
                        try { Thread.sleep(shortMessagePeriod); } catch (InterruptedException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: Thread.sleep(shortMessagePeriod);: InterruptedException: " + error.getMessage()); }
                        displayData.setPrimaryStatusCell("Phone \"" + configuration.getUsername() + "\" " + availability);
                        displayData.setSecondaryStatusCell("");
                        displayData.setPrimaryStatusDetailsCell("");
                        displayData.setSecondaryStatusDetailsCell("");
                        userInterface1.phoneDisplay(displayData); if (userInterface2 != null) {userInterface2.phoneDisplay(displayData);}
                    }
                    destination.setResponseStatusCode(response.getStatusCode());
                    destination.setResponseStatusDesc(response.getReasonPhrase());
                    userInterface1.responseUpdate(response.getStatusCode(), response.getReasonPhrase(), softPhoneInstanceId, destination);
                }
                else if ( response.getStatusCode() == 407 ) // 407 Proxy Authentication Required
                {
                    if ( myResponseBranch.equals(registerRequestBranch) ) // Just copied this one
                    {
                        if (registerRequestCounter == 2) { return;}
                        registerRequestCounter++;

                        if (debugging == true)
                        {
                            if (debugginglevel == 1) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + response.getStatusCode() + " " + response.getReasonPhrase() + " Response Received & Recognized.\n\n" + response.toString()); }
                            else { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + response.getStatusCode() + " " + response.getReasonPhrase() + " Response Received & Recognized."); }
                        }

//                        myResponse = responseReceivedEvent.getResponse();

                        //Create Headers
                        viaHeaders = new ArrayList();
                        viaHeader = (ViaHeader) response.getHeader("Via");
                        try { viaHeader.setParameter("branch", VoipStormTools.getRandom(6)); } catch (ParseException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myViaHeader.setParameter(\"branch\", SoftPhoneTools.getRandom(6)): ParseException: " + error.getMessage()); }
                        viaHeaders.add(viaHeader);

                        try { maxForwardsHeader = headerFactory.createMaxForwardsHeader(MAXFORWARDS); }
                        catch (InvalidArgumentException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myHeaderFactory.createMaxForwardsHeader(MAXFORWARDS): " + error.getMessage()); }

                        fromHeader    = (FromHeader) response.getHeader("From");
//                        toHeader      = (ToHeader) response.getHeader("To");
                        callIdHeader  = (CallIdHeader) response.getHeader("Call-ID");

                        try { commandSequenceHeader = headerFactory.createCSeqHeader((long)2, "REGISTER" ); }
                        catch (ParseException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myHeaderFactory.createCSeqHeader(1, " + Request.REGISTER + " ): ParseException: " + error.getMessage()); }
                        catch (InvalidArgumentException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myHeaderFactory.createCSeqHeader(1, " + Request.REGISTER + " ): InvalidArgumentException: " + error.getMessage()); }

                        if ((configuration.getPublicIP().length() > 0) && (! isSIPAddress))
                        {
                            try { publicContactHeader.setParameter("transport", transport); } catch (ParseException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: publicContactHeader.setParameter(\"transport\", \"udp\"): ParseException: " + error.getMessage()); }
                            try { publicContactHeader.setParameter("expires", Integer.toString(registerTimeout)); }
                            catch (ParseException error) { status[0] = "1"; status[1] = "Error: myContactHeader.setParameter\\(\"transport\",: ParseException: " + error.getMessage(); }
                        }
                        else
                        {
                            try { privateContactHeader.setParameter("transport", transport); } catch (ParseException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: privateContactHeader.setParameter(\"transport\", \"udp\"): ParseException: " + error.getMessage()); }
                            try { privateContactHeader.setParameter("expires", Integer.toString(registerTimeout)); }
                            catch (ParseException error) { status[0] = "1"; status[1] = "Error: myContactHeader.setParameter\\(\"transport\",: ParseException: " + error.getMessage(); }
                        }



        //              ================ Above is the Header copying / preparation part, below is the create 2nd INVITE request part

                        //Create Request
                        try { registerRequest = messageFactory.createRequest(requestURI, Request.REGISTER, callIdHeader, commandSequenceHeader, fromHeader, toHeader, viaHeaders, maxForwardsHeader); }
                        catch (ParseException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myRegisterRequest = myMessageFactory.createRequest(: ParseException: " + error.getMessage()); }

                        if ((configuration.getPublicIP().length() > 0) && (! isSIPAddress))
                        {
                            registerRequest.addHeader(publicContactHeader);
                        }
                        else
                        {
                            registerRequest.addHeader(privateContactHeader);
                        }

                        try { expiresHeader = headerFactory.createExpiresHeader(registerTimeout); }
                        catch (InvalidArgumentException error) { status[0] = "1"; status[1] = "Error: myExpiresHeader = myHeaderFactory.createExpiresHeader(registerTimeout);: InvalidArgumentException: " + error.getMessage(); }
                        registerRequest.addHeader(expiresHeader);

                        WWWAuthenticateHeader myWWWAuthenticateHeader = (WWWAuthenticateHeader) response.getHeader(SIPHeaderNames.WWW_AUTHENTICATE.toString());
                        String algorithm = new String(); if (myWWWAuthenticateHeader.getAlgorithm() != null) {algorithm = myWWWAuthenticateHeader.getAlgorithm(); } else {algorithm="MD5";}
                        String realm            = myWWWAuthenticateHeader.getRealm();
                        String nonce_value      = myWWWAuthenticateHeader.getNonce();
                        String nc_value         = myWWWAuthenticateHeader.getParameter("nc");
                        String cnonce_value     = myWWWAuthenticateHeader.getParameter("cnonce");
                        String method_value     = Request.REGISTER;
                        String digest_uri_value = requestURI.toString();
                        String entity_body      = myWWWAuthenticateHeader.getParameter("entity_body");
                        String qop_value        = myWWWAuthenticateHeader.getQop();
                        String scheme           = myWWWAuthenticateHeader.getScheme();
                        String domain           = myWWWAuthenticateHeader.getDomain();
                        String responseString   = messageDigestAlgorithm.calculateResponse(
                                                                                            algorithm,configuration.getUsername(),
                                                                                            realm,
                                                                                            configuration.getToegang(),
                                                                                            nonce_value,
                                                                                            nc_value,
                                                                                            cnonce_value,
                                                                                            method_value,
                                                                                            digest_uri_value,
                                                                                            entity_body,
                                                                                            qop_value
                                                                                          );
                        try { authorizationHeader = headerFactory.createAuthorizationHeader(scheme); }  catch (ParseException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myHeaderFactory.createAuthorizationHeader(\"Digest\"): " + error.getMessage());  }
                        try { authorizationHeader.setUsername(configuration.getUsername());  }          catch (ParseException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myAuthorizationHeader.setUsername(\"\"): " + error.getMessage());  }
                        try { authorizationHeader.setAlgorithm(algorithm);  }                           catch (ParseException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myAuthorizationHeader.setAlgorithm(\"MD5\"): " + error.getMessage());  }
                        try { authorizationHeader.setRealm(realm); }                                    catch (ParseException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myAuthorizationHeader.setRealm(realm): " + error.getMessage());  }
                        try { authorizationHeader.setNonce(nonce_value); }                              catch (ParseException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myAuthorizationHeader.setNonce(nonce): " + error.getMessage()); }
                        try { authorizationHeader.setResponse(responseString); }                        catch (ParseException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myAuthorizationHeader.setResponse(response): " + error.getMessage());  }
                        registerRequest.addHeader(authorizationHeader);

                        // INVITE Request is now fully built and it needs to be send
                        try { clientTransaction = sipProvider.getNewClientTransaction(registerRequest); }
                        catch (TransactionUnavailableException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: mySipProvider.getNewClientTransaction(myRequest): TransactionUnavailableException: " + error.getMessage()); }

                        do
                        {
                            status[0] = "0"; status[1] = "";
                            try { clientTransaction.sendRequest(); } catch (SipException error)
                            {
                                status[0] = "1"; status[1] = "Error: processResponse: Register Unauthorized: myClientTransaction.sendRequest(): SipException" + error.getMessage();
                                userInterface1.logToApplication("☎ " + softPhoneInstanceId + " status[1]");
                                try { Thread.sleep(retryInterval); } catch (InterruptedException error2) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Sleep error: " + error2.getMessage()); }
                            }
                        } while(status[0].equals("1"));

        //                try { myClientTransaction.sendRequest(); } catch (SipException error) { myUserInterface.log("Error: myClientTransaction.sendRequest(): SipException" + error.getMessage()); } // Default
                        registerRequestBranch = clientTransaction.getBranchId();

                        dialog = clientTransaction.getDialog();

                        if (debugging == true)
                        {
                            if (debugginglevel == 1) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + registerRequest.getMethod() + "-Request with Proxy Authorization Header Sent:\n\n" + registerRequest.toString()); }
                            else { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + registerRequest.getMethod() + "-Request with Proxy Authorization Header Sent."); }
                        }
                    }
                    else if ( myResponseBranch.equals(inviteRequestBranch) ) // 407 Proxy Authentication Required myInviteRequestBranch
                    {
                        if (debugging == true)
                        {
                            if (debugginglevel == 1) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + response.getStatusCode() + " " + response.getReasonPhrase() + " Response Received & Recognized.\n\n" + response.toString()); }
                            else { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + response.getStatusCode() + " " + response.getReasonPhrase() + " Response Received & Recognized."); }
                        }

//                        // Send an ACK in between, just like X-Lite does :-) // automatic ack sent
//                        dialog = clientTransaction.getDialog();
//                        try { ackRequest = clientTransaction.createAck(); } catch (SipException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myAckRequest = myClientTransaction.createAck(): SipException:" + error.getMessage()); }
//                        try { dialog.sendAck(ackRequest); } catch (SipException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myDialog.sendAck(myAckRequest): SipException: " + error.getMessage() + " localhost ?");  }

// ---------------------------------------------------------------------------------------------------------------------------------------------------------------------

//                        myResponse = responseReceivedEvent.getResponse();

                        //Create Headers
                        viaHeaders = new ArrayList();
                        viaHeader = (ViaHeader) response.getHeader("Via");
                        try { viaHeader.setParameter("branch", VoipStormTools.getRandom(6)); } catch (ParseException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myViaHeader.setParameter(\"branch\", SoftPhoneTools.getRandom(6)): ParseException: " + error.getMessage()); }
                        viaHeader.removeParameter("rport"); // communicator
//                        viaHeader.removeParameter("received"); // communicator
                        
                        viaHeaders.add(viaHeader);

                        try { maxForwardsHeader = headerFactory.createMaxForwardsHeader(MAXFORWARDS); }
                        catch (InvalidArgumentException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myHeaderFactory.createMaxForwardsHeader(MAXFORWARDS): " + error.getMessage()); }

                        fromHeader    = (FromHeader) response.getHeader("From");
//                        toHeader      = (ToHeader) response.getHeader("To");
                        viaHeader.removeParameter("tag"); // communicator

                        callIdHeader  = (CallIdHeader) response.getHeader("Call-ID");

                        try { commandSequenceHeader = headerFactory.createCSeqHeader((long)2, Request.INVITE ); }
                        catch (ParseException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myHeaderFactory.createCSeqHeader(1, " + Request.INVITE + " ): ParseException: " + error.getMessage()); }
                        catch (InvalidArgumentException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myHeaderFactory.createCSeqHeader(1, " + Request.INVITE + " ): InvalidArgumentException: " + error.getMessage()); }

                        if ((configuration.getPublicIP().length() > 0) && (! isSIPAddress))
                        {
                            try { publicContactHeader.setParameter("transport", transport); } catch (ParseException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: publicContactHeader.setParameter(\"transport\", \"udp\"): ParseException: " + error.getMessage()); }
                        }
                        else
                        {
                            try { privateContactHeader.setParameter("transport", transport); } catch (ParseException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: privateContactHeader.setParameter(\"transport\", \"udp\"): ParseException: " + error.getMessage()); }
                        }

        //              ================ Above is the Header copying / preparation part, below is the create 2nd INVITE request part

                        try { request = messageFactory.createRequest(requestURI, Request.INVITE, callIdHeader, commandSequenceHeader, fromHeader, toHeader, viaHeaders, maxForwardsHeader); }
                        catch (ParseException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myMessageFactory.createRequest(myRequestURI, " + Request.INVITE + " , myCallIdHeader, myCSeqHeader, myFromHeader, myToHeader, viaHeaders, myMaxForwardsHeader): " + error.getMessage()); }

//                        if (loginstate == LOGINSTATE_REGISTERED)
//                        {
//                            // From here is when this request needs to be rerouted to a proxy
//                            try { routeAddress = addressFactory.createAddress("sip:" + configuration.getServerIP() + ";lr"); } catch (ParseException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myAddressFactory.createAddress(\"sip:\" + sipServer + \";lr\"): " + error.getMessage()); }
//                            routeHeader = headerFactory.createRouteHeader(routeAddress);
//
//                            try { request.addFirst(routeHeader); }
//                            catch (SipException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myRequest.addFirst(myRouteHeader): SipException: " + error.getMessage()); }
//                            catch (NullPointerException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myRequest.addFirst(myRouteHeader): NullPointerException: " + error.getMessage()); }
//                        }

                        if ((configuration.getPublicIP().length() > 0) && (! isSIPAddress))
                        {
                            request.addHeader(publicContactHeader);
                        }
                        else
                        {
                            request.addHeader(privateContactHeader);
                        }

                        // Generate the Session Description Protocol SDP info
                        sdpConvert = new SDPConvert();
                        offerSDPInfo = new SDPInfo();
//                        offerSDPInfo.setIPAddress(myIP.getHostAddress());

                        // NAT / Firewall Traversal
                        if ((configuration.getPublicIP().length() > 0) && (! isSIPAddress))
                        {
                            offerSDPInfo.setIPAddress(configuration.getPublicIP());
                        }
                        else
                        {
                            offerSDPInfo.setIPAddress(configuration.getClientIP());
                        }

                        offerSDPInfo.setUser(configuration.getUsername());
                        offerSDPInfo.setAudioPort(assignedClientAudioPort);
                        offerSDPInfo.setAudioFormat(audioCodec);
                        offerSDPInfo.setVideoPort(videoPort);
                        offerSDPInfo.setVideoFormat(videoCodec);

                        // Setting the MIME Type of the SIP Payload data (SDP)
                        try { contentTypeHeader = headerFactory.createContentTypeHeader("application", "sdp"); } catch (ParseException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myHeaderFactory.createContentTypeHeader(\"application\", \"sdp\"): ParseException: " + error.getMessage()); }
                        offerSDPBytes = sdpConvert.info2Bytes(offerSDPInfo,audioCodec); // Rebuilding Invite Request with ProxyAuthorizationHeader incl. new SDP Content
                        try { request.setContent(offerSDPBytes, contentTypeHeader); } catch (ParseException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myRequest.setContent(content, contentTypeHeader);: ParseException: " + error.getMessage()); }

                        // INVITE Request is now Rebuilt, but needs an added Authorization header
//                        WWWAuthenticateHeader myWWWAuthenticateHeader = (WWWAuthenticateHeader) myResponse.getHeader(SIPHeaderNames.WWW_AUTHENTICATE.toString());
//                      SipSecurityManager.java from Sip-Communicator (look for ProxyAuthenticateHeader)
                        
//                        WWWAuthenticateHeader authHeader = (WWWAuthenticateHeader) response.getHeader(SIPHeaderNames.PROXY_AUTHENTICATE.toString());
                        ProxyAuthenticateHeader authHeader = (ProxyAuthenticateHeader) response.getHeader(SIPHeaderNames.PROXY_AUTHENTICATE.toString());
                        String algorithm = ""; if (authHeader.getAlgorithm() != null) { algorithm = authHeader.getAlgorithm();} else {algorithm = "MD5";}
                        String realm            = authHeader.getRealm();
                        String nonce_value      = authHeader.getNonce();
                        String nc_value         = "00000001";
                        String cnonce_value     = "xyz";
                        String method_value     = Request.INVITE;
//                        String digest_uri_value = requestURI.toString();
                        String uriString        = configuration.getServerIP();
//                        javax.sip.address.URI uriString        = proxyAuthenticateHeader.getURI();
                        String entity_body      = authHeader.getParameter("entity_body");
//                        String qop_value        = proxyAuthenticateHeader.getQop();
                        String qop_value        = "auth";
                        String scheme           = authHeader.getScheme();
                        String domain           = authHeader.getDomain();
                        String qopList          = authHeader.getQop();
                        String qop              = (qopList != null) ? "auth" : null;
                        //                        String responseString         = messageDigestAlgorithm.calculateResponse(algorithm,configuration.getUsername(),realm,configuration.getToegang(),nonce_value,nc_value,cnonce_value,method_value,digest_uri_value,entity_body,qop_value);
                        String responseString   = messageDigestAlgorithm.calculateResponse(
                                                                                            algorithm,
                                                                                            configuration.getUsername(),
                                                                                            authHeader.getRealm(),
                                                                                            new String(configuration.getToegang()),
                                                                                            authHeader.getNonce(),
                                                                                            authHeader.getParameter("nc"),
                                                                                            authHeader.getParameter("cnonce"),
                                                                                            Request.INVITE,
                                                                                            requestURI.toString(),
                                                                                            entity_body,
                                                                                            qop
                                                                                                );
                        try { proxyAuthorizationHeader = headerFactory.createProxyAuthorizationHeader(scheme); }    catch (ParseException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myHeaderFactory.createAuthorizationHeader(\"Digest\"): " + error.getMessage());  }

                        try { proxyAuthorizationHeader.setUsername(configuration.getUsername());  }                 catch (ParseException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: proxyAuthorizationHeader.setUsername(\"\"): " + error.getMessage());  }
                        try { proxyAuthorizationHeader.setRealm(authHeader.getRealm()); }                           catch (ParseException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: proxyAuthorizationHeader.setRealm(realm): " + error.getMessage());  }
                        try { proxyAuthorizationHeader.setNonce(authHeader.getNonce()); }                           catch (ParseException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: proxyAuthorizationHeader.setNonce(nonce): " + error.getMessage()); }
                        try { proxyAuthorizationHeader.setParameter("uri", request.getRequestURI().toString()); }   catch (ParseException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: proxyAuthorizationHeader.setNonce(nonce): " + error.getMessage()); }
//                        proxyAuthorizationHeader.setURI(request.getRequestURI());
                        try { proxyAuthorizationHeader.setResponse(responseString); }                               catch (ParseException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: proxyAuthorizationHeader.setResponse(response): " + error.getMessage());  }
                        try { proxyAuthorizationHeader.setAlgorithm(algorithm); }                                   catch (ParseException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: proxyAuthorizationHeader.setAlgorithm(algorithm): " + error.getMessage());  }

                        if (authHeader.getOpaque() != null)
                        {
                            try { proxyAuthorizationHeader.setOpaque(authHeader.getOpaque()); }        catch (ParseException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: proxyAuthorizationHeader.setOpaque(proxyAuthenticateHeader.getOpaque(): " + error.getMessage()); }
                        }

                        if (qop!=null)
                        {
                            try { proxyAuthorizationHeader.setQop(qop); } catch (ParseException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: proxyAuthorizationHeader.setQop(qop): " + error.getMessage()); }
                            try { proxyAuthorizationHeader.setCNonce(authHeader.getParameter("cnonce")); } catch (ParseException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: proxyAuthorizationHeader.setCNonce(proxyAuthenticateHeader.getParameter(\"cnonce\")): " + error.getMessage()); }
                            try { proxyAuthorizationHeader.setNonceCount(Integer.parseInt(nc_value)); } catch (ParseException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: proxyAuthorizationHeader.setNonceCount(Integer.parseInt(nc_value)): " + error.getMessage()); }
                        }

                        try { proxyAuthorizationHeader.setResponse(responseString); }                               catch (ParseException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: proxyAuthorizationHeader.setResponse(response): " + error.getMessage());  }
                        request.addHeader(proxyAuthorizationHeader);

                        // INVITE Request is now fully built and it needs to be send
                        try { clientTransaction = sipProvider.getNewClientTransaction(request); }
                        catch (TransactionUnavailableException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: mySipProvider.getNewClientTransaction(myRequest): TransactionUnavailableException: " + error.getMessage()); }

                        do
                        {
                            status[0] = "0"; status[1] = "";
                            try { clientTransaction.sendRequest(); } catch (SipException error)
                            {
                                status[0] = "1"; status[1] = "Error: processResponse: Invite Unauthorized: myClientTransaction.sendRequest(): SipException" + error.getMessage();
                                userInterface1.logToApplication("☎ " + softPhoneInstanceId + " status[1]");
                                try { Thread.sleep(retryInterval); } catch (InterruptedException error2) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Sleep error: " + error2.getMessage()); }
                            }
                        } while(status[0].equals("1"));

        //                try { myClientTransaction.sendRequest(); } catch (SipException error) { myUserInterface.log("Error: myClientTransaction.sendRequest(): SipException" + error.getMessage()); } // Default

                        inviteRequestBranch = clientTransaction.getBranchId();

                        dialog = clientTransaction.getDialog();

                        lastsipstate = sipstate;
                        sipstate = SIPSTATE_WAIT_PROV;

                        dispatchSipState(sipstate, lastsipstate, loginstate, SOFTPHONE_ACTIVITY_NORMAL, softPhoneInstanceId, destination);
                        try { Thread.sleep(ultraShortMessagePeriod); } catch (InterruptedException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: Thread.sleep(ultraShortMessagePeriod);: InterruptedException: " + error.getMessage()); }

                        if (ePhoneGUIActive)
                        {
                            displayData.resetSip();
                            displayData.setCallingFlag(true);
                            userInterface1.phoneDisplay(displayData); if (userInterface2 != null) {userInterface2.phoneDisplay(displayData);}
                        }

                        if (debugging == true)
                        {
                            if (debugginglevel == 1) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + request.getMethod() + "-Request with Proxy Authorization Header Sent:\n\n" + request.toString()); }
                            else { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + request.getMethod() + "-Request with Proxy Authorization Header Sent."); }
                        }
                    }
                    else // 407 UNAUTHORIZED UncorrelatedBranch
                    {
                        if (debugging == true)
                        {
                            if (debugginglevel == 1) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + response.getStatusCode() + " " + response.getReasonPhrase() + " Uncorrelated Response Received.\n\n" + response.toString()); }
                            else { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + response.getStatusCode() + " " + response.getReasonPhrase() + " Uncorrelated Response Received & Recognized."); }
                        }
                    }
                }
                else if ( response.getStatusCode() == 480 ) // Temporarily Not Available
                {
                    if (debugging == true)
                    {
                        if (debugginglevel == 1) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + response.getStatusCode() + " " + response.getReasonPhrase() + " (INVITE) Response Received & Recognized.\n\n" + response.toString()); }
                        else { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + response.getStatusCode() + " " + response.getReasonPhrase() + " (INVITE) Response Received & Recognized."); }
                    }

//                    myClientTransaction = responseReceivedEvent.getClientTransaction();
                    dialog = clientTransaction.getDialog();

                    stopTones();
                    if (soundStreamer != null) { soundStreamer.stopListener(); soundStreamer = null; }

                    lastsipstate = sipstate;
                    sipstate = SIPSTATE_IDLE;

                    dispatchSipState(sipstate, lastsipstate, loginstate, SOFTPHONE_ACTIVITY_NORMAL, softPhoneInstanceId, destination);
                    try { Thread.sleep(ultraShortMessagePeriod); } catch (InterruptedException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: Thread.sleep(ultraShortMessagePeriod);: InterruptedException: " + error.getMessage()); }

                    userInterface1.showStatus(response.getStatusCode() + " " + response.getReasonPhrase(), true, true);

                    if (ePhoneGUIActive)
                    {
                        speakerData.setBusyToneFlag(true);
                        userInterface1.speaker(speakerData);
                        speakerData.setBusyToneFlag(false);
                        displayData.setPrimaryStatusCell("Call Outgoing");
                        displayData.setPrimaryStatusDetailsCell("To: " + toHeaderAddress);
                        displayData.setSecondaryStatusCell(response.getReasonPhrase());
                        displayData.setSecondaryStatusDetailsCell("");
                        displayData.resetSip();
                        displayData.setIdleFlag(true);
                        userInterface1.phoneDisplay(displayData); if (userInterface2 != null) {userInterface2.phoneDisplay(displayData);}
                        try { Thread.sleep(shortMessagePeriod); } catch (InterruptedException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: Thread.sleep(shortMessagePeriod);: InterruptedException: " + error.getMessage()); }
                        displayData.setPrimaryStatusCell("Phone \"" + configuration.getUsername() + "\" " + availability);
                        displayData.setSecondaryStatusCell("");
                        displayData.setPrimaryStatusDetailsCell("");
                        displayData.setSecondaryStatusDetailsCell("");
                        userInterface1.phoneDisplay(displayData); if (userInterface2 != null) {userInterface2.phoneDisplay(displayData);}
                    }
                }
                else if ( response.getStatusCode() == 481 ) // Call Leg / Transaction does not exist
                {
                    if (debugging == true)
                    {
                        if (debugginglevel == 1) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + response.getStatusCode() + " " + response.getReasonPhrase() + " (INVITE) Response Received & Recognized.\n\n" + response.toString()); }
                        else { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + response.getStatusCode() + " " + response.getReasonPhrase() + " (INVITE) Response Received & Recognized."); }
                    }

//                    myClientTransaction = responseReceivedEvent.getClientTransaction();
                    dialog = clientTransaction.getDialog();

                    if (soundStreamer != null) { soundStreamer.stopListener(); soundStreamer = null; }
                    stopTones();

                    lastsipstate = sipstate;
                    sipstate = SIPSTATE_IDLE;

                    dispatchSipState(sipstate, lastsipstate, loginstate, SOFTPHONE_ACTIVITY_NORMAL, softPhoneInstanceId, destination);
                    try { Thread.sleep(ultraShortMessagePeriod); } catch (InterruptedException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: Thread.sleep(ultraShortMessagePeriod);: InterruptedException: " + error.getMessage()); }

                    userInterface1.showStatus(response.getStatusCode() + " " + response.getReasonPhrase(), true, true);

                    if (ePhoneGUIActive)
                    {
                        displayData.setPrimaryStatusCell("Phone \"" + configuration.getUsername() + "\" " + availability);
                        displayData.setSecondaryStatusCell("");
                        displayData.setPrimaryStatusDetailsCell("");
                        displayData.setSecondaryStatusDetailsCell("");
                        userInterface1.phoneDisplay(displayData); if (userInterface2 != null) {userInterface2.phoneDisplay(displayData);}
                    }
                }
                else if ( ( response.getStatusCode() >= 402 ) && ( response.getStatusCode() <= 485 ) ) // All sorts of failures
                {
                    if (debugging == true)
                    {
                        if (debugginglevel == 1) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + response.getStatusCode() + " " + response.getReasonPhrase() + " (INVITE) Response Received & Recognized.\n\n" + response.toString()); }
                        else { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + response.getStatusCode() + " " + response.getReasonPhrase() + " (INVITE) Response Received & Recognized."); }
                    }

//                    myClientTransaction = responseReceivedEvent.getClientTransaction();
                    dialog = clientTransaction.getDialog();

                    if (soundStreamer != null) { soundStreamer.stopListener(); soundStreamer = null; }
                    stopTones();

                    lastsipstate = sipstate;
                    sipstate = SIPSTATE_IDLE;
                    
                    speakerData.setErrorToneFlag(true);
                    userInterface1.speaker(speakerData);
                    speakerData.setErrorToneFlag(false);

                    userInterface1.showStatus(response.getStatusCode() + " " + response.getReasonPhrase(), true, true);

                    dispatchSipState(sipstate, lastsipstate, loginstate, SOFTPHONE_ACTIVITY_NORMAL, softPhoneInstanceId, destination);
                    try { Thread.sleep(ultraShortMessagePeriod); } catch (InterruptedException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: Thread.sleep(ultraShortMessagePeriod);: InterruptedException: " + error.getMessage()); }

                    if (ePhoneGUIActive)
                    {
                        displayData.setPrimaryStatusCell("Call Outgoing");
                        displayData.setPrimaryStatusDetailsCell("To: " + toHeaderAddress);
                        displayData.setSecondaryStatusCell(response.getReasonPhrase());
                        displayData.setSecondaryStatusDetailsCell("");
                        displayData.resetSip();
                        displayData.setIdleFlag(true);
                        userInterface1.phoneDisplay(displayData); if (userInterface2 != null) {userInterface2.phoneDisplay(displayData);}
                        try { Thread.sleep(shortMessagePeriod); } catch (InterruptedException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: Thread.sleep(shortMessagePeriod);: InterruptedException: " + error.getMessage()); }
                        displayData.setPrimaryStatusCell("Phone \"" + configuration.getUsername() + "\" " + availability);
                        displayData.setSecondaryStatusCell("");
                        displayData.setPrimaryStatusDetailsCell("");
                        displayData.setSecondaryStatusDetailsCell("");
                        userInterface1.phoneDisplay(displayData); if (userInterface2 != null) {userInterface2.phoneDisplay(displayData);}
                    }
                }
                else if ( response.getStatusCode() == 486 ) // Busy Here
                {
                    if (debugging == true)
                    {
                        if (debugginglevel == 1) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + response.getStatusCode() + " " + response.getReasonPhrase() + " (INVITE) Response Received & Recognized.\n\n" + response.toString()); }
                        else { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + response.getStatusCode() + " " + response.getReasonPhrase() + " Response Received & Recognized."); }
                    }

//                    myClientTransaction = responseReceivedEvent.getClientTransaction();
                    dialog = clientTransaction.getDialog();

                    lastsipstate = sipstate;
                    sipstate = SIPSTATE_TRANSITION_REMOTEBUSY;
                    dispatchSipState(sipstate, lastsipstate, loginstate, SOFTPHONE_ACTIVITY_NORMAL, softPhoneInstanceId, destination);
                    try { Thread.sleep(eyeBlinkMessagePeriod); } catch (InterruptedException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: Thread.sleep(ultraShortMessagePeriod);: InterruptedException: " + error.getMessage()); }

                    if (soundStreamer != null) { soundStreamer.stopListener(); soundStreamer = null; }
                    stopTones();
                    
                    lastsipstate = sipstate;
                    sipstate = SIPSTATE_IDLE;
                    
                    dispatchSipState(sipstate, lastsipstate, loginstate, SOFTPHONE_ACTIVITY_NORMAL, softPhoneInstanceId, destination);
                    try { Thread.sleep(ultraShortMessagePeriod); } catch (InterruptedException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: Thread.sleep(ultraShortMessagePeriod);: InterruptedException: " + error.getMessage()); }

                    if (ePhoneGUIActive)
                    {
                        speakerData.setBusyToneFlag(true);
                        userInterface1.speaker(speakerData);
                        speakerData.setBusyToneFlag(false);
                        displayData.setPrimaryStatusCell("Call Outgoing");
                        displayData.setPrimaryStatusDetailsCell("To: " + toHeaderAddress);
                        displayData.setSecondaryStatusCell(response.getReasonPhrase());
                        displayData.setSecondaryStatusDetailsCell("");
                        displayData.resetSip();
                        displayData.setIdleFlag(true);
                        userInterface1.phoneDisplay(displayData); if (userInterface2 != null) {userInterface2.phoneDisplay(displayData);}
                        try { Thread.sleep(shortMessagePeriod); } catch (InterruptedException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: Thread.sleep(shortMessagePeriod);: InterruptedException: " + error.getMessage()); }
                        displayData.setPrimaryStatusCell("Phone \"" + configuration.getUsername() + "\" " + availability);
                        displayData.setSecondaryStatusCell("");
                        displayData.setPrimaryStatusDetailsCell("");
                        displayData.setSecondaryStatusDetailsCell("");
                        userInterface1.phoneDisplay(displayData); if (userInterface2 != null) {userInterface2.phoneDisplay(displayData);}
                    }
                }
                else if ( response.getStatusCode() == 487 ) // Request Terminated
                {
                    if ( myResponseBranch.equals(cancelRequestBranch) )
                    {
                        if (debugging == true)
                        {
                            if (debugginglevel == 1) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + response.getStatusCode() + " " + response.getReasonPhrase() + " (After Cancel) Response Received & Recognized.\n\n" + response.toString()); }
                            else { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + response.getStatusCode() + " " + response.getReasonPhrase() + " (After Cancel) Response Received & Recognized."); }
                        }

                        //Create statefull transaction
//                        myClientTransaction = responseReceivedEvent.getClientTransaction();
                        dialog = clientTransaction.getDialog();
                        if (soundStreamer != null) { soundStreamer.stopListener(); soundStreamer = null; } // not sure about stopping here

                        lastsipstate = sipstate;
                        sipstate = SIPSTATE_IDLE;
                    
                        dispatchSipState(sipstate, lastsipstate, loginstate, SOFTPHONE_ACTIVITY_NORMAL, softPhoneInstanceId, destination);
                        try { Thread.sleep(ultraShortMessagePeriod); } catch (InterruptedException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: Thread.sleep(ultraShortMessagePeriod);: InterruptedException: " + error.getMessage()); }

                        if (ePhoneGUIActive)
                        {
                            displayData.setPrimaryStatusCell("Call Outgoing");
                            displayData.setPrimaryStatusDetailsCell("To: " + toHeaderAddress);
                            displayData.setSecondaryStatusCell("Call Canceled Locally");
                            displayData.setSecondaryStatusDetailsCell("");
                            displayData.resetSip();
                            displayData.setIdleFlag(true);
                            userInterface1.phoneDisplay(displayData); if (userInterface2 != null) {userInterface2.phoneDisplay(displayData);}
                            try { Thread.sleep(shortMessagePeriod); } catch (InterruptedException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: Thread.sleep(shortMessagePeriod);: InterruptedException: " + error.getMessage()); }
                            displayData.setPrimaryStatusCell("Phone \"" + configuration.getUsername() + "\" " + availability);
                            displayData.setSecondaryStatusCell("");
                            displayData.setPrimaryStatusDetailsCell("");
                            displayData.setSecondaryStatusDetailsCell("");
                            userInterface1.phoneDisplay(displayData); if (userInterface2 != null) {userInterface2.phoneDisplay(displayData);}
                        }
                    }
                    else
                    {
                        if (debugging == true)
                        {
                            if (debugginglevel == 1) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + response.getStatusCode() + " " + response.getReasonPhrase() + " Uncorrelated Response Received.\n\n" + response.toString()); }
                            else { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + response.getStatusCode() + " " + response.getReasonPhrase() + " Uncorrelated Response Received."); }
                        }
                    }
                }
                else if ( response.getStatusCode() == 488 ) // Not Acceptable
                {
                    if (debugging == true)
                    {
                        if (debugginglevel == 1) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + response.getStatusCode() + " " + response.getReasonPhrase() + " (INVITE) Response Received & Recognized.\n\n" + response.toString()); }
                        else { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + response.getStatusCode() + " " + response.getReasonPhrase() + " (INVITE) Response Received & Recognized."); }
                    }

//                    myClientTransaction = responseReceivedEvent.getClientTransaction();
                    dialog = clientTransaction.getDialog();
                    if (soundStreamer != null) { soundStreamer.stopListener(); soundStreamer = null; }
                    stopTones();

                    lastsipstate = sipstate;
                    sipstate = SIPSTATE_IDLE;
                    
                    dispatchSipState(sipstate, lastsipstate, loginstate, SOFTPHONE_ACTIVITY_NORMAL, softPhoneInstanceId, destination);
                    try { Thread.sleep(ultraShortMessagePeriod); } catch (InterruptedException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: Thread.sleep(ultraShortMessagePeriod);: InterruptedException: " + error.getMessage()); }

                    speakerData.setErrorToneFlag(true);
                    userInterface1.speaker(speakerData);
                    speakerData.setErrorToneFlag(false);

                    userInterface1.showStatus(response.getStatusCode() + " " + response.getReasonPhrase(), true, true);

                    if (ePhoneGUIActive)
                    {
                        displayData.setPrimaryStatusCell("Call Outgoing");
                        displayData.setPrimaryStatusDetailsCell("To: " + toHeaderAddress);
                        displayData.setSecondaryStatusCell(response.getReasonPhrase());
                        displayData.setSecondaryStatusDetailsCell("");
                        displayData.resetSip();
                        displayData.setIdleFlag(true);
                        userInterface1.phoneDisplay(displayData); if (userInterface2 != null) {userInterface2.phoneDisplay(displayData);}
                        try { Thread.sleep(shortMessagePeriod); } catch (InterruptedException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: Thread.sleep(shortMessagePeriod);: InterruptedException: " + error.getMessage()); }
                        displayData.setPrimaryStatusCell("Phone \"" + configuration.getUsername() + "\" " + availability);
                        displayData.setSecondaryStatusCell("");
                        displayData.setPrimaryStatusDetailsCell("");
                        displayData.setSecondaryStatusDetailsCell("");
                        userInterface1.phoneDisplay(displayData); if (userInterface2 != null) {userInterface2.phoneDisplay(displayData);}
                    }
                }
                else if ( ( response.getStatusCode() >= 488 ) && ( response.getStatusCode() <= 490 ) ) // All sorts of failures
                {
                    if (debugging == true)
                    {
                        if (debugginglevel == 1) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + response.getStatusCode() + " " + response.getReasonPhrase() + " (INVITE) Response Received & Recognized.\n\n" + response.toString()); }
                        else { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + response.getStatusCode() + " " + response.getReasonPhrase() + " (INVITE) Response Received & Recognized."); }
                    }

//                    myClientTransaction = responseReceivedEvent.getClientTransaction();
                    dialog = clientTransaction.getDialog();

                    if (soundStreamer != null) { soundStreamer.stopListener(); soundStreamer = null; }
                    stopTones();
                    
                    lastsipstate = sipstate;
                    sipstate = SIPSTATE_IDLE;
                    
                    dispatchSipState(sipstate, lastsipstate, loginstate, SOFTPHONE_ACTIVITY_NORMAL, softPhoneInstanceId, destination);
                    try { Thread.sleep(ultraShortMessagePeriod); } catch (InterruptedException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: Thread.sleep(ultraShortMessagePeriod);: InterruptedException: " + error.getMessage()); }

                    speakerData.setErrorToneFlag(true);
                    userInterface1.speaker(speakerData);
                    speakerData.setErrorToneFlag(false);

                    userInterface1.showStatus(response.getStatusCode() + " " + response.getReasonPhrase(), true, true);

                    if (ePhoneGUIActive)
                    {
                        displayData.setPrimaryStatusCell("Call Outgoing");
                        displayData.setPrimaryStatusDetailsCell("To: " + toHeaderAddress);
                        displayData.setSecondaryStatusCell(response.getReasonPhrase());
                        displayData.setSecondaryStatusDetailsCell("");
                        displayData.resetSip();
                        displayData.setIdleFlag(true);
                        userInterface1.phoneDisplay(displayData); if (userInterface2 != null) {userInterface2.phoneDisplay(displayData);}
                        try { Thread.sleep(shortMessagePeriod); } catch (InterruptedException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: Thread.sleep(shortMessagePeriod);: InterruptedException: " + error.getMessage()); }
                        displayData.setPrimaryStatusCell("Phone \"" + configuration.getUsername() + "\" " + availability);
                        displayData.setSecondaryStatusCell("");
                        displayData.setPrimaryStatusDetailsCell("");
                        displayData.setSecondaryStatusDetailsCell("");
                        userInterface1.phoneDisplay(displayData); if (userInterface2 != null) {userInterface2.phoneDisplay(displayData);}
                    }
                }
                else if ( response.getStatusCode() > 491 ) // All sorts of failures
                {
                    if (debugging == true)
                    {
                        if (debugginglevel == 1) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + response.getStatusCode() + " " + response.getReasonPhrase() + " Response Received & Recognized DialogStatus: " + dialog.getState() + "\n\n" + response.toString()); }
                        else { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + response.getStatusCode() + " " + response.getReasonPhrase() + " Response Received & Recognized DialogStatus: " + dialog.getState() + "."); }
                    }

//                    myClientTransaction = responseReceivedEvent.getClientTransaction();
                    if (clientTransaction != null) { dialog = clientTransaction.getDialog(); }
                    if (soundStreamer != null) { soundStreamer.stopListener(); soundStreamer = null; }
                    stopTones();
                    
                    lastsipstate = sipstate;
                    sipstate = SIPSTATE_IDLE;
                    
                    dispatchSipState(sipstate, lastsipstate, loginstate, SOFTPHONE_ACTIVITY_NORMAL, softPhoneInstanceId, destination);
                    try { Thread.sleep(ultraShortMessagePeriod); } catch (InterruptedException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: Thread.sleep(ultraShortMessagePeriod);: InterruptedException: " + error.getMessage()); }

                    speakerData.setErrorToneFlag(true);
                    userInterface1.speaker(speakerData);
                    speakerData.setErrorToneFlag(false);

                    userInterface1.showStatus(response.getStatusCode() + " " + response.getReasonPhrase(), true, true);

                    if (ePhoneGUIActive)
                    {
                        displayData.setPrimaryStatusCell("Call Outgoing");
                        displayData.setPrimaryStatusDetailsCell("To: " + toHeaderAddress);
                        displayData.setSecondaryStatusCell(response.getReasonPhrase());
                        displayData.setSecondaryStatusDetailsCell("");
                        displayData.resetSip();
                        displayData.setIdleFlag(true);
                        userInterface1.phoneDisplay(displayData); if (userInterface2 != null) {userInterface2.phoneDisplay(displayData);}
                        try { Thread.sleep(shortMessagePeriod); } catch (InterruptedException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: Thread.sleep(shortMessagePeriod);: InterruptedException: " + error.getMessage()); }
                        displayData.setPrimaryStatusCell("Phone \"" + configuration.getUsername() + "\" " + availability);
                        displayData.setSecondaryStatusCell("");
                        displayData.setPrimaryStatusDetailsCell("");
                        displayData.setSecondaryStatusDetailsCell("");
                        userInterface1.phoneDisplay(displayData); if (userInterface2 != null) {userInterface2.phoneDisplay(displayData);}
                    }
                }
                else
                {
                    if (debugging == true)
                    {
                        if (debugginglevel == 1) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + response.getStatusCode() + " " + response.getReasonPhrase() + " Fully Uncorrelated Response Received.\n\n" + response.toString()); }
                        else { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processResponse: " + response.getStatusCode() + " " + response.getReasonPhrase() + " Fully Request Terminated Uncorrelated Response Received."); }
                    }
                }
            }
        });
        processResponseThread.setName("processResponseThread");
        processResponseThread.setDaemon(runThreadsAsDaemons);
        processResponseThread.setPriority(9);
        processResponseThread.start();
    }

    /**
     *
     * @param requestReceivedEvent
     */
    @SuppressWarnings("static-access")
    @Override
    synchronized public void processRequest(final RequestEvent requestReceivedEvent) // must be of returntype void unfortunately (no status feedback when serverIP received a request)
    {
        Thread processRequestThread = new Thread(new Runnable() // Please remove if it fails
//        processRequestThreadPool.execute(new Runnable() // Please remove if it fails
        {
            @Override
            public void run()
            {
                String[] status = new String[2];
                Response response = null;
                String fromHeaderString; // leave local
                String namePart1; // leave local
                //Retreive the request
                request = requestReceivedEvent.getRequest(); // Keep global, also used in busyrequest

                fromHeaderString = request.getHeader("From").toString();
                namePart1 = VoipStormTools.substitude(fromHeaderString, "^.+<");
                fromHeaderAddress = VoipStormTools.substitude(namePart1, ">.*");

                if ( request.getMethod().equals(Request.OPTIONS))
                {
                    if (debugging == true)
                    {
                        if (debugginglevel == 1) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processRequest " + request.getMethod() + " Request Received & Recognized:\n\n" + request.toString()); }
                        else { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processRequest " + request.getMethod() + " Request Received & Recognized."); }
                    }

                    //Create the OK Message
                    try { response = messageFactory.createResponse(Response.OK, request); }
                    catch (ParseException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myMessageFactory.createResponse(Response.OK, myRequest): ParseException: " + error.getMessage());  }

                    //Send the OK Message

                    try { sipProvider.sendResponse(response); }
                    catch (SipException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: mySipProvider.sendResponse(myResponse): SipException: " + error.getMessage());  }

                    if (debugging == true)
                    {
                        if (debugginglevel == 1) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processRequest " + response.getStatusCode() + " (OK) Response (After OPTIONS Request) Sent.\n\n" + response.toString()); } // Related to qualify=yes in asterisk
                        else { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processRequest " + response.getStatusCode() + " (OK) Response (After OPTIONS Request) Sent."); }
                    }
                }
                else if ( request.getMethod().equals(Request.INVITE) ) // Received INVITE Send RINGING BACK
                {
                    if (debugging == true)
                    {
                        if (debugginglevel == 1) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processRequest " + request.getMethod() + " Request Received & Recognized:\n\n" + request.toString()); }
                        else { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processRequest " + request.getMethod() + " INVITE Request Received & Recognized."); }
                    }

                    //Create ServerTransaction
                    Response myTMPResponse = null;
                    try { serverTransaction = sipProvider.getNewServerTransaction(request); }
                    catch (TransactionAlreadyExistsException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: TransactionAlreadyExistsException: mySipProvider.getNewServerTransaction(myRequest)"); }
                    catch (TransactionUnavailableException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: TransactionUnavailableException: mySipProvider.getNewServerTransaction(myRequest)"); }

        // ==================================================================================================================

// Send Trying
                    try { myTMPResponse = messageFactory.createResponse( Response.TRYING, request); } catch (ParseException error) { userInterface1.logToApplication("Error: myMessageFactory.createResponse(" + Response.TRYING + ", myRequest): " + error.getMessage());  } // Ringing
                    try { response = messageFactory.createResponse( Response.RINGING, request); } catch (ParseException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myMessageFactory.createResponse(" + Response.RINGING + ", myRequest): " + error.getMessage());  } // Ringing

                    // Copying the "To" header into the response
                    responseToHeader = (ToHeader) response.getHeader("To");

                    //Set a tag behind the "To" header
                    serverToTag = VoipStormTools.getRandom(6);
                    try { responseToHeader.setTag(serverToTag); }
                    catch (ParseException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: responseToHeader.setTag(\"454326\"): ParseException: " + error.getMessage());  }

                    //Add the contactHeader to the response
                    myTMPResponse.addHeader(privateContactHeader);
                    response.addHeader(privateContactHeader);

                    myTMPResponse.setHeader(responseToHeader);
                    response.setHeader(responseToHeader);

                    // Get offerer SDP Content
//                    offerSDPBytes = (byte[]) request.getContent();
                    byte[] offerSDPBytes = request.getRawContent();

                    offerSDPInfo = sdpConvert.bytes2Info(offerSDPBytes,audioCodec); // myAudioCodec might wrong here
//                    ViaHeader tmpViaHeader = (ViaHeader) request.getHeader("Via"); offerSDPInfo.setIPAddress(tmpViaHeader.getReceived()); // Workaround solution for SessionDescription.getConnection().getAddress() bug

                    try { serverTransaction.sendResponse(myTMPResponse); } // Send Trying
                    catch (SipException error) { userInterface1.logToApplication("Error: myServerTransaction.sendResponse(myTMPResponse): SipException: " + error.getMessage());  }
                    catch (InvalidArgumentException error) { userInterface1.logToApplication("Error: myServerTransaction.sendResponse(myTMPResponse): InvalidArgumentException: " + error.getMessage());  }
        
        	    dialog = serverTransaction.getDialog();

                    if (ePhoneGUIActive)
                    {
                        displayData.setPrimaryStatusCell("Call Incoming");
                        displayData.setPrimaryStatusDetailsCell("From: " + fromHeaderAddress);
                        displayData.setSecondaryStatusCell("Trying Sent");
                        displayData.setSecondaryStatusDetailsCell("");
                        displayData.resetSip();
                        displayData.setRingingFlag(true);
                        userInterface1.phoneDisplay(displayData); if (userInterface2 != null) {userInterface2.phoneDisplay(displayData);}
                    }

        	    if (debugging == true)
                    {
                        if (debugginglevel == 1) { userInterface1.logToApplication( "processRequest " + myTMPResponse.getStatusCode() + " (TRYING) Response Sent.\n\n" + myTMPResponse.toString()); }
                        else    { userInterface1.logToApplication( "processRequest " + myTMPResponse.getStatusCode() + " (TRYING) Response Sent.\n\n"); }
                    }

// Send Ringing
                    try { serverTransaction.sendResponse(response); } // Send Ringing
                    catch (SipException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myServerTransaction.sendResponse(myResponse): SipException: " + error.getMessage());  }
                    catch (InvalidArgumentException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myAddressFactory.createAddress: InvalidArgumentException: " + error.getMessage());  }

                    dialog = serverTransaction.getDialog();

                    if (debugging == true)
                    {
                        if (debugginglevel == 1) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processRequest " + response.getStatusCode() + " (RINGING) Response Sent. DialogStatus: " + dialog.getState() + "\n\n" + response.toString()); }
                        else    { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processRequest " + response.getStatusCode() + " (RINGING) Response Sent. DialogStatus: " + dialog.getState()); }
                    }

                    stopTones();
                    lastsipstate = sipstate;
                    sipstate = SIPSTATE_RINGING;

                    dispatchSipState(sipstate, lastsipstate, loginstate, SOFTPHONE_ACTIVITY_NORMAL, softPhoneInstanceId, destination);
                    try { Thread.sleep(ultraShortMessagePeriod); } catch (InterruptedException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: Thread.sleep(ultraShortMessagePeriod);: InterruptedException: " + error.getMessage()); }

                    stopTones();
                    if (ePhoneGUIActive)
                    {
                        speakerData.setRingToneFlag(true);
                        userInterface1.speaker(speakerData);
                        speakerData.setRingToneFlag(false);
                        displayData.setPrimaryStatusCell("Call Incoming");
                        displayData.setPrimaryStatusDetailsCell("From: " + fromHeaderAddress);
                        displayData.setSecondaryStatusCell("Phone Ringing");
                        displayData.setSecondaryStatusDetailsCell("");
                        displayData.resetSip();
                        displayData.setRingingFlag(true);
                        userInterface1.phoneDisplay(displayData); if (userInterface2 != null) {userInterface2.phoneDisplay(displayData);}
                    }

// Make Automated dicissions
                    boolean isBusy = false; if (((Math.round(Math.random() * 100)) <= busyRatioPercentage)) { isBusy = true; }
                    if (((autoRingingResponse == RANDOM) && (isBusy)) || (autoRingingResponse == CANCEL))// B1 = Button, P1 on/off, P2 RingResponseDelay, P3 BusyPercentage
                    {
                        stopTones();
                        if (ePhoneGUIActive)
                        {
                            speakerData.setBusyToneFlag(true);
                            userInterface1.speaker(speakerData);
                            speakerData.setBusyToneFlag(false);
                            displayData.setPrimaryStatusCell("Call Incoming");
                            displayData.setPrimaryStatusDetailsCell("From: " + fromHeaderAddress);
                            displayData.setSecondaryStatusCell("Call Canceled Locally");
                            displayData.setSecondaryStatusDetailsCell("");
                            displayData.resetSip();
                            displayData.setRingingFlag(false);
                            userInterface1.phoneDisplay(displayData); if (userInterface2 != null) {userInterface2.phoneDisplay(displayData);}
                        }

                        busyResponse();
                    }
                    else if (
                                ((autoRingingResponse == RANDOM) && (!isBusy)) ||
                                (autoRingingResponse == ANSWER)
                           )
                    {
                        try { Thread.sleep(autoRingingResponseDelay); } catch (InterruptedException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: Thread.sleep(shortMessagePeriod);: InterruptedException: " + error.getMessage()); }
                        createResponse();
                    }
                }
                else if ( request.getMethod().equals(Request.ACK) ) // I took the call sent the OK and now received an ACK, thus Call Established
                {
                    if (debugging == true)
                    {
                        if (debugginglevel == 1) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processRequest " + request.getMethod() + " Ack Request Received & Recognized:\n\n" + request.toString()); }
                        else    { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processRequest " + request.getMethod() + " Ack Request Received & Recognized."); }
                    }

                    if ( inboundEndTimerDelay > 0) { inboundEndCallTimer = new Timer(); inboundEndCallTimer.schedule(new EndCallTimer(softPhoneReference), (long)(inboundEndTimerDelay)); }

                    serverTransaction = requestReceivedEvent.getServerTransaction();
                    dialog = serverTransaction.getDialog();
                    stopTones();

                    lastsipstate = sipstate;
                    sipstate = SIPSTATE_ESTABLISHED;
                    
                    dispatchSipState(sipstate, lastsipstate, loginstate, SOFTPHONE_ACTIVITY_NORMAL, softPhoneInstanceId, destination);
                    try { Thread.sleep(ultraShortMessagePeriod); } catch (InterruptedException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: Thread.sleep(ultraShortMessagePeriod);: InterruptedException: " + error.getMessage()); }

                    if (ePhoneGUIActive)
                    {
                        displayData.setPrimaryStatusCell("Call Incoming");
                        displayData.setPrimaryStatusDetailsCell("From: " + fromHeaderAddress);
                        displayData.setSecondaryStatusCell("Call Established");
                        displayData.setSecondaryStatusDetailsCell("");
                        displayData.resetSip();
                        displayData.setTalkingFlag(true);
                        userInterface1.phoneDisplay(displayData); if (userInterface2 != null) {userInterface2.phoneDisplay(displayData);}
                    }
                }
                else if ( request.getMethod().equals(Request.BYE) ) // Send OK BACK
                {
                    if (debugging == true)
                    {
                        if (debugginglevel == 1) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processRequest " + request.getMethod() + " BYE Request Received & Recognized:\n\n" + request.toString()); }
                        else { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processRequest " + request.getMethod() + " BYE Request Received & Recognized."); }
                    }

                    if (inboundEndCallTimer != null) {inboundEndCallTimer.cancel(); inboundEndCallTimer.purge();}
                    if (outboundEndCallTimer != null) {outboundEndCallTimer.cancel(); outboundEndCallTimer.purge();}
                    stopTones();
                    soundStreamer.stopMedia();
                    if (soundStreamer != null) { soundStreamer.stopListener(); soundStreamer = null; }

                    serverTransaction = requestReceivedEvent.getServerTransaction();
                    dialog = serverTransaction.getDialog();

                    //Create the OK Message
                    try { response = messageFactory.createResponse(Response.OK, request); }
                    catch (ParseException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myMessageFactory.createResponse(Response.OK, myRequest): ParseException: " + error.getMessage());  }

                    //Send the OK Message
                    try { serverTransaction.sendResponse(response); }
                    catch (SipException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myServerTransaction.sendResponse(myResponse): SipException: " + error.getMessage());  }
                    catch (InvalidArgumentException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myServerTransaction.sendResponse(myResponse): InvalidArgumentException: " + error.getMessage());  }
                    dialog = serverTransaction.getDialog();

                    if (debugging == true)
                    {
                        if (debugginglevel == 1) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processRequest " + response.getStatusCode() + " (OK) Response Sent. DialogStatus: " + dialog.getState() + "\n\n" + response.toString()); }
                        else { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processRequest " + response.getStatusCode() + " (OK) Response Sent. DialogStatus: " + dialog.getState()); }
                    }

                    lastsipstate = sipstate;
                    sipstate = SIPSTATE_TRANSITION_REMOTEBYE;
                    
                    dispatchSipState(sipstate, lastsipstate, loginstate, SOFTPHONE_ACTIVITY_NORMAL, softPhoneInstanceId, destination);
                    try { Thread.sleep(eyeBlinkMessagePeriod); } catch (InterruptedException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: Thread.sleep(ultraShortMessagePeriod);: InterruptedException: " + error.getMessage()); }

                    lastsipstate = sipstate;
                    sipstate = SIPSTATE_IDLE;
                    dispatchSipState(sipstate, lastsipstate, loginstate, SOFTPHONE_ACTIVITY_NORMAL, softPhoneInstanceId, destination);
                    try { Thread.sleep(ultraShortMessagePeriod); } catch (InterruptedException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: Thread.sleep(ultraShortMessagePeriod);: InterruptedException: " + error.getMessage()); }

        //	    stopTones();
        //            speakerData.setBusyToneFlag(true);
        //            myUserInterface.speaker(speakerData);
        //            speakerData.setBusyToneFlag(false);

                    if (ePhoneGUIActive)
                    {
                        displayData.setPrimaryStatusCell("Call Incoming");
                        displayData.setPrimaryStatusDetailsCell("From: " + fromHeaderAddress);
                        displayData.setSecondaryStatusCell("Call Ended Remotely");
                        displayData.setSecondaryStatusDetailsCell("");
                        displayData.resetSip();
                        displayData.setIdleFlag(true);
                        userInterface1.phoneDisplay(displayData); if (userInterface2 != null) {userInterface2.phoneDisplay(displayData);}
                        try { Thread.sleep(shortMessagePeriod); } catch (InterruptedException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: Thread.sleep(shortMessagePeriod);: InterruptedException: " + error.getMessage()); }
                        displayData.setPrimaryStatusCell("Phone \"" + configuration.getUsername() + "\" " + availability);
                        displayData.setSecondaryStatusCell("");
                        displayData.setPrimaryStatusDetailsCell("");
                        displayData.setSecondaryStatusDetailsCell("");
                        userInterface1.phoneDisplay(displayData); if (userInterface2 != null) {userInterface2.phoneDisplay(displayData);}
                    }
                }
                else if ( request.getMethod().equals(Request.CANCEL) ) // While RINGING the caller Canceled
                {
                    if (debugging == true)
                    {
                        if (debugginglevel == 1) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processRequest " + request.getMethod() + " CANCEL Request Received & Recognized:\n\n" + request.toString()); }
                        else { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processRequest " + request.getMethod() + " CANCEL Request Received & Recognized."); }
                    }

                    serverTransaction = requestReceivedEvent.getServerTransaction();
                    dialog = serverTransaction.getDialog();

                    originalRequest = serverTransaction.getRequest();

                    //if (SoundStreamer != null) { SoundStreamer.stopListener(); }

                    stopTones();

                    //Create and send the OK response
                    try { response = messageFactory.createResponse(Response.OK, originalRequest); }
                    catch (ParseException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myMessageFactory.createResponse(487, myRequest): ParseException: " + error.getMessage());  }

                    // Copying the "To" header into the response
                    responseToHeader = (ToHeader) response.getHeader("To");

                    try { responseToHeader.setTag(serverToTag); }
                    catch (ParseException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: responseToHeader.setTag(\"454326\"): ParseException: " + error.getMessage());  }

                    response.setHeader(responseToHeader);
                    response.addHeader(privateContactHeader);

                    try { dialog.sendReliableProvisionalResponse(response); }
                    catch (SipException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: myDialog.sendReliableProvisionalResponse(myResponse): SipException: " + error.getMessage()); }

                    lastsipstate = sipstate;
                    sipstate = SIPSTATE_TRANSITION_REMOTECANCEL;
                    dispatchSipState(sipstate, lastsipstate, loginstate, SOFTPHONE_ACTIVITY_NORMAL, softPhoneInstanceId, destination);
                    try { Thread.sleep(eyeBlinkMessagePeriod); } catch (InterruptedException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: Thread.sleep(ultraShortMessagePeriod);: InterruptedException: " + error.getMessage()); }

                    lastsipstate = sipstate;
                    sipstate = SIPSTATE_IDLE;
                    dispatchSipState(sipstate, lastsipstate, loginstate, SOFTPHONE_ACTIVITY_NORMAL, softPhoneInstanceId, destination);
                    try { Thread.sleep(ultraShortMessagePeriod); } catch (InterruptedException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: Thread.sleep(ultraShortMessagePeriod);: InterruptedException: " + error.getMessage()); }

                    if (debugging == true)
                    {
                        if (debugginglevel == 1) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processRequest " + response.getStatusCode() + " (OK) Response Sent. DialogStatus: " + dialog.getState() + "\n\n" + response.toString()); }
                        else { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processRequest " + response.getStatusCode() + " (OK) Response Sent. DialogStatus: " + dialog.getState()); }
                    }

                    if (ePhoneGUIActive)
                    {
                        displayData.setPrimaryStatusCell("Call Incoming");
                        displayData.setPrimaryStatusDetailsCell("From: " + fromHeaderAddress);
                        displayData.setSecondaryStatusCell("Call Canceled Remotely");
                        displayData.setSecondaryStatusDetailsCell("");
                        displayData.resetSip();
                        displayData.setIdleFlag(true);
                        userInterface1.phoneDisplay(displayData); if (userInterface2 != null) {userInterface2.phoneDisplay(displayData);}
                        try { Thread.sleep(shortMessagePeriod); } catch (InterruptedException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: Thread.sleep(shortMessagePeriod);: InterruptedException: " + error.getMessage()); }
                        displayData.setPrimaryStatusCell("Phone \"" + configuration.getUsername() + "\" " + availability);
                        displayData.setSecondaryStatusCell("");
                        displayData.setPrimaryStatusDetailsCell("");
                        displayData.setSecondaryStatusDetailsCell("");
                        userInterface1.phoneDisplay(displayData); if (userInterface2 != null) {userInterface2.phoneDisplay(displayData);}
                    }
                }
                else
                {
                    if (soundStreamer != null) { soundStreamer.stopListener(); soundStreamer = null; }

                    if (debugging == true)
                    {
                        if (debugginglevel == 1) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processRequest " + request.getMethod() + " Request NOT Recognized. DialogStatus: " + dialog.getState() + "\n\n" + request.toString()); }
                        else { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processRequest " + request.getMethod() + " Request NOT Recognized. DialogStatus: " + dialog.getState()); }
                    }
                }
            }
        });
        processRequestThread.setName("processRequestThread");
        processRequestThread.setDaemon(runThreadsAsDaemons);
//        processRequestThread.setPriority(8);
        processRequestThread.start();
    }

    /**
     *
     * @return
     */
    @SuppressWarnings("static-access")
    synchronized public String[] createResponse() // Send OK (Answerer accepted the call)
    {
        String[] status = new String[2]; status[0] = "0"; status[1] = "";

        if (sipstate == SIPSTATE_RINGING)
        {
            dialog = serverTransaction.getDialog();

            // Prepare Media before sending the SIP message with SDP payload
            soundStreamer = new SoundStreamer(userInterface1, softPhoneInstanceId);
            status = soundStreamer.startListener(configuration.getClientIP(), requestedServerAudioPort, offerSDPInfo.getIPAddress(), offerSDPInfo.getAudioPort());
            if (status[0].equals("1"))
            {
                userInterface1.logToApplication("☎ " + softPhoneInstanceId + " (createResponse) status(" + status[1] + ") = soundStreamer.startListener(" + configuration.getClientIP() + ","  + requestedServerAudioPort + "," + offerSDPInfo.getIPAddress() + "," + offerSDPInfo.getAudioPort() + ")");
                userInterface1.logToApplication("inbound soundStreamer.startListener(configuration.getClientIP() basically failed with sipStatus: " + status[1]); return status;
            }
            else
            {
                assignedServerAudioPort = Integer.parseInt(status[1]);
                if (debugging == true) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " (createResponse) status(" + Integer.parseInt(status[1]) + ") = soundStreamer.startListener(" + configuration.getClientIP() + ","  + requestedServerAudioPort + "," + offerSDPInfo.getIPAddress() + "," + offerSDPInfo.getAudioPort() + ")"); }
                muteAudio();
            }

            originalRequest = serverTransaction.getRequest();
            try { response = messageFactory.createResponse( Response.OK, originalRequest); }
            catch (ParseException error) { status[0] = "1"; status[1] = "Error: myMessageFactory.createResponse(200, originalRequest): " + error.getMessage(); return status; }

            //Copying the "To" header into the response
            responseToHeader = (ToHeader) response.getHeader("To");

            //Set a tag behind the "To" header
            try { responseToHeader.setTag(serverToTag); } catch (ParseException error) { status[0] = "1"; status[1] = "Error: responseToHeader.setTag(\"454326\"): ParseException: " + error.getMessage(); return status; }
            response.addHeader(responseToHeader);

            response.addHeader(privateContactHeader);

            // Generating the SDP answer content
//            answerSDPInfo.setIPAddress(myIP.getHostAddress());

            // NAT / Firewall Traversal
            if ((configuration.getPublicIP().length() > 0) && (! isSIPAddress))
            {
                answerSDPInfo.setIPAddress(configuration.getClientIP()); // Don't set this to public (inbound calls only expected from internal LAN)
            }
            else
            {
                answerSDPInfo.setIPAddress(configuration.getClientIP());
            }
            answerSDPInfo.setAudioPort(assignedServerAudioPort);
            answerSDPInfo.setAudioFormat(offerSDPInfo.getAudioFormat());


            //if      (offerSDPInfo.getVideoPort() == -1) { answerSDPInfo.setVideoPort(-1);}
            //else if (myVideoPort == -1)                 { answerSDPInfo.setVideoPort(0);answerSDPInfo.setVideoFormat(offerSDPInfo.getVideoFormat()); }
            //else                                        { answerSDPInfo.setVideoPort(myVideoPort);answerSDPInfo.setVideoFormat(offerSDPInfo.getVideoFormat()); }

            try { contentTypeHeader = headerFactory.createContentTypeHeader("application", "sdp"); }
            catch (ParseException error) { status[0] = "1"; status[1] = "Error: contentTypeHeader = myHeaderFactory.createContentTypeHeader(\"application\", \"sdp\"): ParseException: " + error.getMessage(); return status; }

            answerSDPBytes = sdpConvert.info2Bytes(answerSDPInfo,offerSDPInfo.getAudioFormat()); // Picking up the phone, adding SDP Content to SIP Message informing about assignedServerAudioPort
            try { response.setContent(answerSDPBytes, contentTypeHeader); } catch (ParseException error) { status[0] = "1"; status[1] = "Error: myResponse.setContent(answerContent, contentTypeHeader): ParseException: " + error.getMessage(); return status; }

//            System.out.println("Create SDPinfo: answerSDPInfo.toString(): " + answerSDPInfo.toString());

//            if (answerSDPInfo.getVideoPort() > 0 )
//            {
//                myVideoTool = new VideoTool();
//                myVideoTool.startListener(offerSDPInfo.getIPAddress(), Integer.toString(offerSDPInfo.getVideoPort()), Integer.toString(answerSDPInfo.getVideoPort()), Integer.toString(offerSDPInfo.getVideoFormat()));
//            }

            dialog = serverTransaction.getDialog();

            try { dialog.sendReliableProvisionalResponse(response); } catch (SipException error) { status[0] = "1"; status[1] = "Error: myDialog.sendReliableProvisionalResponse(myResponse): SipException: " + error.getMessage(); return status; }

            if (debugging == true)
            {
                if (debugginglevel == 1) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " createResponse " + response.getStatusCode() + " (OK) Response Sent. DialogStatus: " + dialog.getState() + "\n\n" + response.toString()); }
                else { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " createResponse " + response.getStatusCode() + " (OK) Response Sent. DialogStatus: " + dialog.getState()); }
            }
            lastsipstate = sipstate;
            sipstate = SIPSTATE_WAIT_ACK;

            dispatchSipState(sipstate, lastsipstate, loginstate, SOFTPHONE_ACTIVITY_NORMAL, softPhoneInstanceId, destination);
            try { Thread.sleep(ultraShortMessagePeriod); } catch (InterruptedException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: Thread.sleep(ultraShortMessagePeriod);: InterruptedException: " + error.getMessage()); return status; }

            if (ePhoneGUIActive)
            {
                displayData.resetSip();
                displayData.setAcceptFlag(true);
            }

            stopTones();

            if (audioIsMuted) {muteAudio();} // Without this it makes a hell of a noise!!!
        }
        else
        {
            if (debugging == true)
            {
                if (debugginglevel == 1) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " createResponse NOT executed. Reason: in required state!\n"); }
            }
        }
        return status;
    }

    /**
     *
     * @return
     */
    synchronized public String[] busyResponse()
    {
//        Thread busyResponseThread = new Thread(new Runnable() // Please remove if it fails
//        {
//            @Override
//            public void run()
//            {
                String[] status = new String[2]; status[0] = "0"; status[1] = "";

                lastsipstate = sipstate;
                sipstate = SIPSTATE_TRANSITION_LOCALBUSY;

                dispatchSipState(sipstate, lastsipstate, loginstate, SOFTPHONE_ACTIVITY_NORMAL, softPhoneInstanceId, destination);
                
                try { Thread.sleep(eyeBlinkMessagePeriod); } catch (InterruptedException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: Thread.sleep(ultraShortMessagePeriod);: InterruptedException: " + error.getMessage()); }

                //Create BUSY Message
                try { response = messageFactory.createResponse( Response.BUSY_HERE, request); }
                catch (ParseException error) { status[0] = "1"; status[1] = "Error: myMessageFactory.createResponse(" + Response.RINGING + ", myRequest): " + error.getMessage(); } // Ringing // return status removed

                // Copying the "To" header into the response
                responseToHeader = (ToHeader) response.getHeader("To");

                //Set a tag behind the "To" header
                try { responseToHeader.setTag(VoipStormTools.getRandom(6)); }
                catch (ParseException error) { status[0] = "1"; status[1] = "Error: responseToHeader.setTag(\"RNDNUMSTRING\"): ParseException: " + error.getMessage(); } // return status removed

                //Add the contactHeader to the response
                response.addHeader(privateContactHeader);
                response.addHeader(responseToHeader);

                try { serverTransaction.sendResponse(response); }
                catch (SipException error) { status[0] = "1"; status[1] = "Error: myAddressFactory.createAddress: ParseException: " + error.getMessage(); }
                catch (InvalidArgumentException error) { status[0] = "1"; status[1] = "Error: myAddressFactory.createAddress: ParseException: " + error.getMessage(); } // return status removed
                dialog = serverTransaction.getDialog();

                if (debugging == true)
                {
                    if (debugginglevel == 1) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " endRequest " + response.getStatusCode() + " (Busy Here) Response Sent. DialogStatus: " + dialog.getState() + "\n\n" + response.toString()); }
                    else { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " endRequest " + response.getStatusCode() + " (Busy Here) Response Sent. DialogStatus: " + dialog.getState()); }
                }

                lastsipstate = sipstate;
                sipstate = SIPSTATE_IDLE;
                dispatchSipState(sipstate, lastsipstate, loginstate, SOFTPHONE_ACTIVITY_NORMAL, softPhoneInstanceId, destination);
                try { Thread.sleep(ultraShortMessagePeriod); } catch (InterruptedException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: Thread.sleep(ultraShortMessagePeriod);: InterruptedException: " + error.getMessage()); }

                stopTones();

                if (ePhoneGUIActive)
                {
                    displayData.setPrimaryStatusCell("Call Incoming");
                    displayData.setPrimaryStatusDetailsCell("From: " + fromHeaderAddress);
                    displayData.setSecondaryStatusCell("Call Denied");
                    displayData.setSecondaryStatusDetailsCell("");
                    displayData.resetSip();
                    displayData.setIdleFlag(true);
                    userInterface1.phoneDisplay(displayData); if (userInterface2 != null) {userInterface2.phoneDisplay(displayData);}
                    try { Thread.sleep(shortMessagePeriod); } catch (InterruptedException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: Thread.sleep(shortMessagePeriod);: InterruptedException: " + error.getMessage()); }
                    displayData.setPrimaryStatusCell("Phone \"" + configuration.getUsername() + "\" " + availability);
                    displayData.setSecondaryStatusCell("");
                    displayData.setPrimaryStatusDetailsCell("");
                    displayData.setSecondaryStatusDetailsCell("");
                    userInterface1.phoneDisplay(displayData); if (userInterface2 != null) {userInterface2.phoneDisplay(displayData);}
                }
//            }
//        });
//        busyResponseThread.setName("busyResponseThread");
//        busyResponseThread.setDaemon(runThreadsAsDaemons);
//        busyResponseThread.start();

        return status;
    }

    /**
     *
     * @return
     */
    synchronized public String[] byeRequest()
    {
//        Thread byeRequestThread = new Thread(new Runnable() // Please remove if it fails
//        {
//            @Override
//            public void run()
//            {
                String[] status = new String[2]; status[0] = "0"; status[1] = "";

                if (sipstate == SIPSTATE_ESTABLISHED)
                {
                    try { request = dialog.createRequest("BYE"); }
                    catch (SipException error) { status[0] = "1"; status[1] = "Error: myDialog.createRequest(\"BYE\"): SipException: " + error.getMessage(); } // return status removed

                    request.addHeader(privateContactHeader);
                    try { clientTransaction = sipProvider.getNewClientTransaction(request); }
                    catch (TransactionUnavailableException error) { status[0] = "1"; status[1] = "Error: mySipProvider.getNewClientTransaction(myRequest): TransactionUnavailableException: " + error.getMessage(); } // return status removed

                    try { dialog.sendRequest(clientTransaction); }
                    catch (TransactionDoesNotExistException error) { status[0] = "1"; status[1] = "Error: myDialog.sendRequest(myClientTransaction): TransactionDoesNotExistException: " + error.getMessage(); } // return status removed
                    catch (SipException error) { status[0] = "1"; status[1] = "Error: myDialog.sendRequest(myClientTransaction): SipException: " + error.getMessage(); } // return status removed

                    if (debugging == true)
                    {
                        if (debugginglevel == 1) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " endRequest " + request.getMethod() + " (Bye) Request Sent. DialogStatus: " + dialog.getState() + "\n\n" + request.toString()); }
                        else { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " endRequest " + request.getMethod() + " (Bye) Request Sent. DialogStatus: " + dialog.getState()); }
                    }

                    byeRequestBranch = clientTransaction.getBranchId();

                    lastsipstate = sipstate;
                    sipstate = SIPSTATE_TRANSITION_LOCALBYE;

                    dispatchSipState(sipstate, lastsipstate, loginstate, SOFTPHONE_ACTIVITY_NORMAL, softPhoneInstanceId, destination);
                    try { Thread.sleep(eyeBlinkMessagePeriod); } catch (InterruptedException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: Thread.sleep(ultraShortMessagePeriod);: InterruptedException: " + error.getMessage()); }

                    lastsipstate = sipstate;
                    sipstate = SIPSTATE_IDLE;
                    dispatchSipState(sipstate, lastsipstate, loginstate, SOFTPHONE_ACTIVITY_NORMAL, softPhoneInstanceId, destination);
                    try { Thread.sleep(ultraShortMessagePeriod); } catch (InterruptedException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: Thread.sleep(ultraShortMessagePeriod);: InterruptedException: " + error.getMessage()); }

                    stopTones();
                    soundStreamer.stopMedia();
                    if (soundStreamer != null) { soundStreamer.stopListener(); soundStreamer = null; }

                    if (ePhoneGUIActive)
                    {
                        displayData.setPrimaryStatusCell("Call Incoming");
                        displayData.setPrimaryStatusDetailsCell("From: " + fromHeaderAddress);
                        displayData.setSecondaryStatusCell("Call Ended Locally");
                        displayData.setSecondaryStatusDetailsCell("");
                        displayData.resetSip();
                        displayData.setIdleFlag(true);
                        userInterface1.phoneDisplay(displayData); if (userInterface2 != null) {userInterface2.phoneDisplay(displayData);}
                        try { Thread.sleep(shortMessagePeriod); } catch (InterruptedException error) { userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: Thread.sleep(shortMessagePeriod);: InterruptedException: " + error.getMessage()); }
                        displayData.setPrimaryStatusCell("Phone \"" + configuration.getUsername() + "\" " + availability);
                        displayData.setSecondaryStatusCell("");
                        displayData.setPrimaryStatusDetailsCell("");
                        displayData.setSecondaryStatusDetailsCell("");
                        userInterface1.phoneDisplay(displayData); if (userInterface2 != null) {userInterface2.phoneDisplay(displayData);}
                    }
                    
                    if (inboundEndCallTimer != null) {inboundEndCallTimer.cancel(); inboundEndCallTimer.purge();}
                    if (outboundEndCallTimer != null) {outboundEndCallTimer.cancel(); outboundEndCallTimer.purge();}
                }
//            }
//        });
//        byeRequestThread.setName("byeRequestThread");
//        byeRequestThread.setDaemon(runThreadsAsDaemons);
//        byeRequestThread.start();

        return status;
    }

    // Dynamically set thread priority (much when needed, less when not)
    synchronized private void dispatchSipState(int sipstateParam, int lastsipstateParam, int loginstateParam, int softphoneActivityParam, int softPhoneInstanceIdParam, Destination destinationParam)
    {
        if (sipstateParam != lastsipstateParam)
        {
            if (sipstate == SIPSTATE_IDLE ) { softPhoneReference.setPriority(PRIORITY_LOW); } else { softPhoneReference.setPriority(PRIORITY_HIGH); }
        }
        
        userInterface1.sipstateUpdate(sipstateParam, lastsipstateParam, loginstateParam, softphoneActivityParam, softPhoneInstanceIdParam, destinationParam);
    }

    /**
     *
     * @return
     */
    synchronized public String[] muteAudio()
    {
        String[] status = new String[2]; status[0] = "0"; status[1] = "";

        status = soundStreamer.muteAudio();

        displayData.setMuteFlag(true);
        userInterface1.phoneDisplay(displayData); if (userInterface2 != null) {userInterface2.phoneDisplay(displayData);}
        speakerData.setMuteEnabledToneFlag(true);
        userInterface1.speaker(speakerData);
        speakerData.setMuteEnabledToneFlag(false);

        return status;
    }

    /**
     *
     * @return
     */
    synchronized public String[] unMuteAudio()
    {
        String[] status = new String[2]; status[0] = "0"; status[1] = "";

        displayData.setMuteFlag(false);
        userInterface1.phoneDisplay(displayData); if (userInterface2 != null) {userInterface2.phoneDisplay(displayData);}
        speakerData.setMuteDisabledToneFlag(true);
        userInterface1.speaker(speakerData);
        speakerData.setMuteDisabledToneFlag(false);

        status = soundStreamer.unMuteAudio();
        return status;
    }

    /**
     *
     * @param arg0
     */
    @SuppressWarnings("static-access")
    @Override
    synchronized public void processTimeout(TimeoutEvent arg0) // This is an error
    {
	userInterface1.responseUpdate(0, "processTimeout: " + arg0.toString(), softPhoneInstanceId, destination);
        userInterface1.logToApplication("☎ " + softPhoneInstanceId + " processTimeout!!!");
        userInterface1.logToFile("☎ " + softPhoneInstanceId + " processTimeout!!!");
        restartListener();
    }

    /**
     *
     * @param arg0
     */
    @SuppressWarnings("static-access")
    @Override
    synchronized public void processIOException(IOExceptionEvent arg0) // This I don't know
    {
        userInterface1.logToApplication("☎ " + softPhoneInstanceId + " Error: processIOException: ");
        userInterface1.logToFile("☎ " + softPhoneInstanceId + " Error: processIOException: ");
        restartListener();
    }

    /**
     *
     * @param transactionTerminatedEvent
     */
    @SuppressWarnings("static-access")
    @Override
    synchronized public void processTransactionTerminated(TransactionTerminatedEvent transactionTerminatedEvent) // This is not an error, but allways happens after every termination
    {
    }

    /**
     *
     * @param dialogTerminatedEvent
     */
    @SuppressWarnings("static-access")
    @Override
    synchronized public void processDialogTerminated(DialogTerminatedEvent dialogTerminatedEvent) // This is not an error, but allways happens after every termination
    {
    }

    /**
     *
     */
    public void             terminate()                                                 { keepRunning = false; }

    /**
     *
     */
    public void             pleaseStop()                                                { keepRunning = false; }

    /**
     *
     * @return
     */
    public boolean          keepRunning()                                               { return keepRunning; }

    /**
     *
     * @return
     */
    public static String    getBrand()                                                  { return Vergunning.BRAND; }

    /**
     *
     * @return
     */
    public static String    getBusiness()                                               { return Vergunning.BUSINESS; }

    /**
     *
     * @return
     */
    public static String    getBrandDescription()                                       { return Vergunning.BRAND_DESCRIPTION; }

    /**
     *
     * @return
     */
    public static String    getProduct()                                                { return Vergunning.PRODUCT; }

    /**
     *
     * @return
     */
    public static String    getProductDescription()                                     { return Vergunning.PRODUCT_DESCRIPTION; }

    /**
     *
     * @return
     */
    public static String    getCopyright()                                              { return Vergunning.COPYRIGHT; }

    /**
     *
     * @return
     */
    public static String    getAuthor()                                                 { return Vergunning.AUTHOR; }

    /**
     *
     * @return
     */
    public static String    getVersion()                                                { return VERSION; }

    /**
     *
     * @return
     */
    public static String    getWindowTitle()                                            { return Vergunning.BRAND + " " + Vergunning.PRODUCT + " " + THISPRODUCT + " " + VERSION; }

    /**
     *
     * @return
     */
    public int              getInstanceId()                                             { return softPhoneInstanceId; }

    /**
     *
     * @return
     */
    public static int       getRegisterLoginTimeout()                                   { return registerLoginTimeout; }

    /**
     *
     * @return
     */
    public int              getSipState()                                               { return sipstate; }

    /**
     *
     * @return
     */
    public int              getSipPort()                                                { return localSIPPort; }

    /**
     *
     * @return
     */
    public int              getLoginState()                                             { return loginstate; }

    /**
     *
     * @return
     */
    public Destination      getDestination()                                            { return destination; }

    /**
     *
     * @param configurationParam
     * @throws CloneNotSupportedException
     */
    synchronized public void             setConfiguration(Configuration configurationParam) throws CloneNotSupportedException
    {
        configuration = (Configuration) configurationParam.clone();
        userInterface1.showStatus("SoftPhone Configuration Set", false, false);
    }

    /**
     *
     * @param enableParam
     * @param debugginglevelParam
     */
    public void             setDebugging(boolean enableParam, int debugginglevelParam)  { debugging = enableParam; debugginglevel = debugginglevelParam;}

    /**
     *
     * @param sipstateParam
     */
    public void             setSipState(int sipstateParam)                              { sipstate = sipstateParam; }

    /**
     *
     * @param inboundEndTimerDelayParam
     */
    public void             setInboundEndTimer(int inboundEndTimerDelayParam)           { inboundEndTimerDelay = inboundEndTimerDelayParam;}
//    public void             setOutboundEndTimer(int outboundEndTimerDelayParam)         { outboundEndTimerDelay = outboundEndTimerDelayParam;}

    /**
     *
     * @param myUserInterface2Param
     */
        public void             setUserInterface2(UserInterface myUserInterface2Param)      { userInterface2 = myUserInterface2Param; }

    /**
     *
     * @param destinationParam
     */
    public void             setDestination(Destination destinationParam)                { destination = destinationParam; }

    /**
     *
     * @param enableParam
     */
    public void             setEPhoneGUIActive(boolean enableParam)                     { ePhoneGUIActive = enableParam; }

    /**
     *
     */
    public void             updateDisplay()                                             { userInterface2.phoneDisplay(displayData); }

//    public void             setDebugging(boolean enableParam)                           { debugging = enableParam; }
    
    /**
     *
     * @param autoRingingResponseParam
     * @param autoRingingResponseDelayParam
     */
        
    public void             setRingingResponse(int autoRingingResponseParam, int autoRingingResponseDelayParam)
                                                                                        { autoRingingResponse = autoRingingResponseParam; autoRingingResponseDelay = autoRingingResponseDelayParam; }

    @Override
    @SuppressWarnings("empty-statement")
    public void run()
    {
//	do
//        {
//            try { Thread.sleep(1000); } catch (InterruptedException error) { };
//        }
//	while(keepRunning);
//	return;
    }
}
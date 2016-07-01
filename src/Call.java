import datasets.SpeakerData;
import datasets.DisplayData;
import datasets.Destination;
import java.io.File;

/**
 *
 * @author ron
 */
public class Call implements UserInterface
{
    private     Destination destination;
    private     String      toegang;
    private     String      mediaFile;
    private     String      dataDir         = "data/";
    private     String      soundsDir       = dataDir + "sounds/";
    private     boolean     debugging       = false;
    SoftPhone               softphone;
    private     Thread      softphoneThread;
    private     AudioTool   audioTool;
    private     Vergunning  vergunning;
    private     Call        mySoftPhoneCLI;

    private     File        file;
    private     String      vergunningDir;
    private     String      databasesDir;
    private     String      configDir;
    private     String      binDir;
    private     String      logDir;
    private     String      fileSeparator;
    private     String      lineTerminator;
    private     String      platform;

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
    public static final int SIPSTATE_WAIT_PROV                  = 3;

    /**
     *
     */
    public static final int SIPSTATE_WAIT_FINAL                 = 4;

    /**
     *
     */
    public static final int SIPSTATE_WAIT_ACK                   = 5;

    /**
     *
     */
    public static final int SIPSTATE_RINGING                    = 6;

    /**
     *
     */
    public static final int SIPSTATE_ESTABLISHED                = 7;

    /**
     *
     */
    public static final int SIPSTATE_TRANSITION_LOCALCANCEL     = 8;

    /**
     *
     */
    public static final int SIPSTATE_TRANSITION_REMOTECANCEL    = 9;

    /**
     *
     */
    public static final int SIPSTATE_TRANSITION_LOCALBUSY       = 10;

    /**
     *
     */
    public static final int SIPSTATE_TRANSITION_REMOTEBUSY      = 11;

    /**
     *
     */
    public static final int SIPSTATE_TRANSITION_LOCALBYE        = 12;

    /**
     *
     */
    public static final int SIPSTATE_TRANSITION_REMOTEBYE       = 13;

    /**
     *
     */
    public static final int SIPSTATE_TRANSITION_CONFIG          = 14;

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

    private final int LINE1BUTTON =               0;
    private final int LINE2BUTTON =               1;
    private final int SAVEBUTTON =                2;
    private final int REGISTERBUTTON =            3;
    private final int ANSWERBUTTON =              4;
    private final int CANCELBUTTON =              5;
    private final int RANDOMRINGRESPONSEBUTTON =  6;
    private final int ENDTIMERBUTTON =            7;
    private final int MUTEAUDIOBUTTON =           8;
    private final int DEBUGBUTTON =               9;
    private final int CALLBUTTON =                10;
    private final int ENDBUTTON =                 11;

    private final DisplayData displayData;
    private final SpeakerData speakerData;

    /**
     *
     * @param phoneNumberParam
     * @param soundfileParam
     * @param debuggingParam
     */
    @SuppressWarnings("static-access")
    public Call(String phoneNumberParam, String soundfileParam, boolean debuggingParam)
    {
        debugging = debuggingParam;
        String[] status = new String[2]; status[0] = "0"; status[1] = "";
        platform = System.getProperty("os.name").toLowerCase();
        if ( platform.indexOf("windows") != -1 ) { fileSeparator = "\\"; lineTerminator = "\r\n"; } else { fileSeparator = "/"; lineTerminator = "\r\n"; }

        dataDir = "data" + fileSeparator;
        soundsDir = dataDir + "sounds" + fileSeparator;
        vergunningDir = dataDir + "license" + fileSeparator;
        databasesDir = dataDir + "databases" + fileSeparator;
        configDir = dataDir + "config" + fileSeparator;
        binDir = dataDir + "bin" + fileSeparator;
        logDir = dataDir + "log" + fileSeparator;

        file = new File(dataDir);       if (!file.exists()) { if (new File(dataDir).mkdir())        { System.out.println("Warning:  Creating Directory: " + dataDir); } }
        file = new File(soundsDir);     if (!file.exists()) { if (new File(soundsDir).mkdir())      { System.out.println("Critical: Creating Directory: " + soundsDir); } }
        file = new File(vergunningDir);    if (!file.exists()) { if (new File(vergunningDir).mkdir())     { System.out.println("Info:     Creating Directory: " + vergunningDir); } }
        file = new File(databasesDir);  if (!file.exists()) { if (new File(databasesDir).mkdir())   { System.out.println("Info:     Creating Directory: " + databasesDir); } }
        file = new File(configDir);     if (!file.exists()) { if (new File(configDir).mkdir())      { System.out.println("Info:     Creating Directory: " + configDir); } }
        file = new File(binDir);        if (!file.exists()) { if (new File(binDir).mkdir())         { System.out.println("Critical: Creating Directory: " + binDir); } }
        file = new File(logDir);        if (!file.exists()) { if (new File(logDir).mkdir())         { System.out.println("Info:     Creating Directory: " + logDir); } }

        vergunning = new Vergunning();
        vergunning.controleerVergunning();
        if ( ! vergunning.isValid())
        {
            System.out.println("\r\tInvalid License, please order your license at http://www.voipstorm.nl/"); System.exit(0);
        }

        audioTool   = new AudioTool();

        displayData = new DisplayData();
        speakerData = new SpeakerData();

        destination = new Destination();
        destination.setDestination(phoneNumberParam);
        mediaFile = "file:" + soundfileParam;

        power();
        call();
    }

    private void power()
    {
        //        softphone = new SoftPhone(this, 0, debugging);
        String[] status = new String[2]; status[0] = "0"; status[1] = "";
        
        softphoneThread = new Thread();
        softphoneThread = (SoftPhone) new SoftPhone(this, 0, debugging); // callerRef, instance, debugging
        softphoneThread.setDaemon(true);
        softphoneThread.setPriority(9);
        softphoneThread.start();
        
        softphone = (SoftPhone) softphoneThread;

        softphone.setDestination(destination);
        softphone.setDebugging(debugging, 1);

        status = softphone.userInput(LINE1BUTTON, "1", "1", ""); // LineNr, On/Off, ManagedByCallCenter
        if (status[0].equals("1"))
        {
            audioTool.play(audioTool.getFailuretoneClip());
            showStatus("Line1 Error: " + status[1], true, true);
        }
        else
        {
//                successTonesTool.play();
            audioTool.play(audioTool.getSuccesstoneClip());
        }
    }

    private void call()
    {
        String[] status = new String[2]; status[0] = "0"; status[1] = "";
        showStatus("Calling: " + VoipStormTools.validateSIPAddress(destination.getDestination()) + " playing: " + mediaFile, false, false);
        status = softphone.userInput(CALLBUTTON, VoipStormTools.validateSIPAddress(destination.getDestination()), mediaFile, "");
        if (status[0].equals("1")) { showStatus("Call Failure: " + status[1], false, false); }
    }

    /**
     *
     * @param speakerParam
     */
    @Override synchronized public void speaker(SpeakerData speakerParam)
    {
        if (speakerParam.getDialToneFlag())                { audioTool.play(audioTool.getDialtoneClip()); }
        if (speakerParam.getCallToneFlag())                { audioTool.playLoop(audioTool.getCalltoneClip()); } else { audioTool.stop(audioTool.getCalltoneClip()); }
        if (speakerParam.getBusyToneFlag())                { audioTool.stop(audioTool.getCalltoneClip()); audioTool.play(audioTool.getBusytoneClip()); }
        if (speakerParam.getDeadToneFlag())                { audioTool.stop(audioTool.getCalltoneClip()); audioTool.play(audioTool.getDeadtoneClip()); }
        if (speakerParam.getErrorToneFlag())               { audioTool.stop(audioTool.getCalltoneClip()); audioTool.play(audioTool.getErrortoneClip()); }
        if (speakerParam.getRingToneFlag())                { audioTool.playLoop(audioTool.getRingtoneClip()); } else { audioTool.stop(audioTool.getRingtoneClip()); }
        if (speakerParam.getRegisterEnabledToneFlag())     { audioTool.play(audioTool.getRegisterenabledtoneClip()); }
        if (speakerParam.getRegisterDisabledToneFlag())    { audioTool.play(audioTool.getRegisterdisabledtoneClip()); }
        if (speakerParam.getAnswerEnabledToneFlag())       { audioTool.play(audioTool.getAnswerenabledtoneClip()); }
        if (speakerParam.getAnswerDisabledToneFlag())      { audioTool.play(audioTool.getAnswerdisabledtoneClip()); }
        if (speakerParam.getCancelEnabledToneFlag())       { audioTool.play(audioTool.getCancelenabledClip()); }
        if (speakerParam.getCancelDisabledToneFlag())      { audioTool.play(audioTool.getCanceldisabledClip()); }
        if (speakerParam.getMuteEnabledToneFlag())         { audioTool.play(audioTool.getMuteenabledClip()); }
        if (speakerParam.getMuteDisabledToneFlag())        { audioTool.play(audioTool.getMutedisabledClip()); }
    }

    /**
     *
     * @param displayParam
     */
    @Override public void phoneDisplay(DisplayData displayParam) {  }

    /**
     *
     * @param messageParam
     * @param logToApplicationParam
     * @param logToFileParam
     */
    @Override public void showStatus(String messageParam, boolean logToApplicationParam, boolean logToFileParam) { System.out.println(messageParam); }

    /**
     *
     * @param message
     */
    @Override public void logToApplication(String message) { System.out.println(message); }

    /**
     *
     * @param message
     */
    @Override public void logToFile(String message) { /*System.out.println(message);*/ }

    /**
     *
     */
    @Override public void resetLog() { throw new UnsupportedOperationException("Not supported yet."); }

    /**
     *
     * @param sipstateParam
     * @param lastsipstateParam
     * @param loginstateParam
     * @param softphoneActivityParam
     * @param softPhoneInstanceIdParam
     * @param destinationParam
     */
    @Override synchronized public void sipstateUpdate(final int sipstateParam, final int lastsipstateParam, final int loginstateParam, final int softphoneActivityParam, final int softPhoneInstanceIdParam, final Destination destinationParam)
    {
//        System.out.println("\r\nsipstate: " + SIPSTATE_DESCRIPTION[sipstateParam] + " last-sipstate: " + SIPSTATE_DESCRIPTION[lastsipstateParam] + " destination:\r\n" + destinationParam.toString());
        if ((sipstateParam == SIPSTATE_IDLE ) && (lastsipstateParam == SIPSTATE_TRANSITION_REMOTEBYE )) { System.out.println("Call Ended Remotely. Exiting"); System.exit(0); }
    }

    /**
     *
     * @param responseCodeParam
     * @param responseReasonPhraseParam
     * @param softPhoneInstanceIdParam
     * @param destinationParam
     */
    @Override public void responseUpdate(int responseCodeParam, String responseReasonPhraseParam, int softPhoneInstanceIdParam, Destination destinationParam) {    }

    /**
     *
     * @param args
     * @throws CloneNotSupportedException
     */
    public static void main(String[] args) throws CloneNotSupportedException
    {
        if ( (args.length != 2) && ((args.length != 3))) // Deal with Command Line parameters
        {
            System.out.println("Usage: java -cp VoipStorm.jar Call \"phonenumber\" \"data/sounds/voipstorm.wav\" [debugging]\n");
            System.out.println("Example: java -cp VoipStorm.jar Call 2000 \"data/sounds/voipstorm.wav\" &\n");
            System.out.println("Example: perl -e 'for ( $length=2000; $length < 2020; $length++) { print \"$length\\n\"; }' | while read phonenumber;do (java -cp VoipStorm.jar Call \"$phonenumber\" \"data/sounds/voipstorm.wav\" & ) ; done");
        }
        else // Deal with the dynamic configuration
        {
            String destination = args[0];
            String mediaFile = args[1];
            boolean debugging = false;
            if (args.length == 3) { if (args[2].equals("debugging")) { debugging = true; } else { debugging = false; } }
            Call call = new Call(destination, mediaFile, debugging);
        }
    }

    /**
     *
     * @param messageParam
     * @param valueParam
     */
    @Override
    public void feedback(String messageParam, int valueParam) {    }
}
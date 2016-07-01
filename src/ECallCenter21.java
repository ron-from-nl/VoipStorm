import datasets.Coordinate;
import datasets.SpeakerData;
import datasets.DisplayData;
import datasets.Configuration;
import datasets.Order;
import datasets.Campaign;
import datasets.Destination;
import datasets.CampaignStat;
import datasets.TimeTool;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.io.File;
import java.io.FileWriter;
import javax.swing.SwingConstants;
import javax.swing.BorderFactory;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartFactory;
import org.jfree.data.general.DefaultPieDataset;

//import java.util.concurrent.Executors;
//import java.util.concurrent.ExecutorService;

/**
 *
 * @author ron
 */

public class ECallCenter21 extends javax.swing.JFrame implements UserInterface
{
    // MinWindow 710x598, MaxWindow 710x830

    private static final String         THISPRODUCT                 = "ECallCenter21";
    private static final String         VERSION                     = "v1.0.2";
    private static final String         DATABASE                    = Vergunning.BRAND + "DB";
    private        final URL            URL                         = new URL(Vergunning.WEBLINK);

    private static final int            PLAF_GTK                    = 0;
    private static final int            PLAF_MOTIF                  = 1;
    private static final int            PLAF_NIMBUS                 = 2;
    private static final int            PLAF_WINDOWS                = 3;

    private static final int            INBOUND_PORT                = 1969;
    private static final int            OUTBOUND_PORT               = 1970;
    private static final int            POWEREDOFF                  = 0;
    private static final int            POWERINGON                  = 1;
    private static final int            POWEREDON                   = 2;
    private static final int            LOADCAMPAIGN                = 3;
    private static final int            RUNNING                     = 4;
    private static final int            PAUSING                     = 5;
    private static final int            RERUNBREAK                  = 6;
    private static final int            STOPPED                     = 7;

    private static final int            CALLING                     = 0;
    private static final int            SCANNING                    = 1;

    private static final String[]       callCenterStatusDescription = new String[]{"POWERED OFF","POWERING ON","POWERED ON","LOADING CAMPAIGN","RUNNING","PAUSING","RECOVERING FOR RERUN","STOPPED"};
    private static final String         VERGUNNINGTOEKENNERTOEGANG  = "IsNwtNp4L";
//    SoftPhone inboundSoftPhoneInstance;
//    SoftPhone outboundSoftPhoneInstance;
    ThreadGroup                         allThreadsGroup;
//    private String destination;
    private int                         ultraShortMessagePeriod;
    private int                         smoothMovementPeriod;
//    private int eyeBlinkMessagePeriod;
//    private int shortMessagePeriod;
    private int                         mediumMessagePeriod;

    boolean                             debugging;
    boolean                             runThreadsAsDaemons         = true;
    private Vergunning                  vergunning;
    private Configuration               configurationCallCenter;
    private Configuration               configurationSoftPhone;

//    private String vsHeaderImageFile = "vsimages.gif";
    private String                      filename;
    private String[]                    vmUsageStatus;
    private String[]                    memFreeStatus;

//    private DisplayData displayInput;
//    private String displayOutput;
    private DisplayData                 localDisplayData;
//    private SpeakerData localSpeakerData;
//    private Thread[] inboundSoftPhoneThreadArray;
    private Thread[]                    threadArray;

    //private Vector[] outboundSoftPhoneThreadVector;
    private int                         inboundSoftPhonesAvailable;
    private int                         outboundSoftPhonesAvailable;
    private int                         softphonesQuantity;

    /**
     *
     */
    public int                          inboundInstanceCounter;

    /**
     *
     */
    public int                          outboundInstanceCounter;
//    private int inboundSoftPhonesAvailableCounter;
    private int                         powerCounter;
    private int                         runCampaignCounter;
    private int                         registerCounter;
    private int                         restartSoftPhonesCounter;
    private int                         debugCounter;
    private int                         callCounter;
    private int                         endCounter;
//    private int destinationsTotal;
    private int                         destinationsCounter;
    private long                         usernameStart;

//    private long startCallsEpochTime;
//    private long endCallsEpochTime;
//    private long durationCallsEpochTime;

//    private long second = 1;
//    private long minute = 60;
    private long                        hour = 3600;
//    private long day = 86400;

    int                                 phonesPoolTablePreferredColumns = 25;
    int                                 phonesTableRowsNeeded           = Math.round(inboundSoftPhonesAvailable / phonesPoolTablePreferredColumns);
    String[][]                          phonesPoolTableCellsArray;
    String[]                            phonesPoolTableColumnTitlesArray;
//    Object[][] phonesPoolTableRowArrayOffColumnObjectArray;
//    Object[] phonesPoolTableColumnTitlesObjectArray;
//    ImageIcon[][] phonesPoolTableRowArrayOffColumnImageIconArray;
//    ImageIcon[] phonesPoolTableColumnTitlesImageIconArray;
    private int                         PHONESPOOLTABLECOLUMNWIDTH      = 26;
    private int                         PHONESPOOLTABLECOLUMNHEIGHT     = 16;

    private String                      username;
    private String                      toegang;
    private String                      prefixToegang;
    private String                      suffixToegang;
    private String                      soundFileToStream;

//    private SoundTool myClickOnSoundTool;
//    private SoundTool myClickOffSoundTool;
//    private SoundTool mySuccessSoundTool;
//    private SoundTool myPowerSuccessSoundTool;
//    private SoundTool myFailureSoundTool;
//    private SoundTool myTickSoundTool;
//    private SoundTool myRegisterEnabledSoundTool;
//    private SoundTool myRegisterDisabledSoundTool;
//    private SoundTool myAnswerEnabledSoundTool;
//    private SoundTool myAnswerDisabledSoundTool;
//    private SoundTool myCancelEnabledSoundTool;
//    private SoundTool myCancelDisabledSoundTool;
//    private SoundTool myMuteEnabledSoundTool;
//    private SoundTool myMuteDisabledSoundTool;
//
//    private SoundTool myRingToneSoundTool;
//    private SoundTool myDialToneSoundTool;
//    private SoundTool myCallToneSoundTool;
//    private SoundTool myBusyToneSoundTool;
//    private SoundTool myDeadToneSoundTool;
//    private SoundTool myErrorToneSoundTool;
//instancesTally
//    private int onActiveCount, offActiveCount, registeredTally, registeredActiveCount, idleActiveCount, wait_provActiveCount, wait_finalActiveCount, wait_ackActiveCount, callingActiveCount, ringingActiveCount, callingTotalTally, connectTotalTally, acceptedTotalTally, ringingTotalTally, establishedActiveCount, establishedTotalTally, localCancelTotalTally, remoteCancelTotalTally, localBusyTotalTally, remoteBusyTotalTally, localByeTotalTally, remoteByeTotalTally, busyTotalTally;
    private int                         offActiveCount, registeredActiveCount;
    private int                         infoTally, successTally, redirectionTally, clientErrorTally, serverErrorTally, generalErrorTally, timeoutTally;
    private boolean                     outboundCallsInProgress             = false;
    private boolean                     callCenterIsOutBound                = false;
    private boolean                     autoSlidersEnabled                  = true;
    private boolean                     isRegistering                       = false;
//    private int progressValue = 0;

    private int                         connectingTallyLimit;
    private int                         callingTallyLimit;
    private int                         establishedTallyLimit;
    private int                         registrationBurstDelay; // mS
    private int                         outboundBurstDelay; // mS
    private long                        vmUsage;
    private float                       vmUsagePauseThreashold; // Actually MinuteLoad
    private int                         vmUsageDecelerationThreashold       = 70; // Does not pause campaign, but slowdown outboundburstrate (autospeed)
    private long                        memFree; // VarMEmFree Tot / Real depends on platform
    private long                        memFreeThreshold; // RealMEmFree
    private long                        heapMemTot; // the amount of heapspace in use by JVM
    private long                        heapMemMax; // the amount of heapspace the jvm tries to allocate
    private long                        heapMemFree; // the amount of free mem this application has left
    private long                        heapMemFreeThreshold;
//    private SNMPClient mySNMP; // SNMP Client with predefined OIDs to fetch from the localmachine

    private Timer                       updateSystemStatsTimer;
    private Timer                       updateStallerTimer;
    private Timer                       updateDashboardTimer;
    private Timer                       updateAutoSpeedTimer;
    private Timer                       updateVergunningTimer;
    private Timer                       reRegisterTimer;

    private long                        updateSystemStatsTimerFastInterval  = 5000; // mS
    private long                        updateSystemStatsTimerSlowInterval  = 5000; // mS
    private long                        updateDashboardTimerInterval        = 5000; // mS
    private long                        updateAutoSpeedTimerInterval        = 3000; // mS
    private long                        updateVergunningTimerInterval          = 3600000; // mS
    private long                        updateStallerTimerInterval          = 10000; // mS

    private Icons                       icons;

    private Coordinate                  myCoordinate;
    private ChartPanel                  chartPanel;
    private ChartPanel                  performanceChartPanel;
    private int                         performanceDialSize                 = 165;
    private DefaultPieDataset           callRatioChartData;
    private JFreeChart                  callRatioChart;
    private JavaDBClient                dbClient;
    private ECallCenter21               eCallCenterReference;
    private Calendar                    currentTimeCalendar;
    private Calendar                    currentTimeDashboardCalendar;
    private Calendar                    lastTimeDashboardCalendar;
    private Calendar                    difRegStartCurTimeCalendar;
    private Calendar                    difCurTimeExpEndCalendar;
    private Calendar                    difRegStartExpEndCalendar;
    private Order                       order;

    /**
     *
     */
    public Campaign                     campaign;
    private Destination                 destination;
    private Destination[]               destinationArray;
    private CampaignStat                campaignStat;
    private CampaignStat                lastStallerCampaignStat;
    private CampaignStat                lastTimeDashboardCampaignStat;
    private Shell                       shell;
    private int                         campaignReRunLimit                  = 3;
    private boolean                     campaignStopRequested               = false;
    private int                         callCenterStatus                    = POWEREDOFF;
    private boolean                     autoPowerOff                        = false;
    private NetManagerServer            outboundNetManagerServer;
    private NetManagerServer            inboundNetManagerServer;
    private int                         campaignProgressPercentage          = 0;
    private int                         campaignReRunStage;
    private String                      boundMode;
    private int                         pid;
    private Locale                      nlLocale;
    private int                         callSpeedInterval                   = 1000; // mS
    private int                         outboundBurstRateExtraInterval      = 1000; // mS
    private int                         throughputFactor                    = 20;
    private boolean                     stalling                            = false;

    /**
     *
     */
    public int                          stallingCounterLimit                = 120; // Only Inbound as Outbound stalling is managed by manager

    /**
     *
     */
    public int                          stallingCounter                     = 120;
    private boolean                     callCenterIsNetManaged;

    /**
     *
     */
    public int                          selfDestructCounterLimit            = 30;

    /**
     *
     */
    public int                          selfDestructCounter                 = 30;

    private PerformanceMeter            performanceMeter;
    private Calendar                    vergunningStartCalendar;
    private Calendar                    vergunningEndCalendar;
    private String []                   plaf;
    private String                      plafSelected;
    private boolean                     performanceMeterIsLocked            = false;
    private boolean                     moveVMUSageMeterIsLocked            = false;
    private boolean                     moveCallSpeedSliderIsLocked         = false;
    private int                         lastMessageDuration                 = 0;

    private final int                   LINE1BUTTON                         = 0;
    private final int                   LINE2BUTTON                         = 1;
    private final int                   SAVEBUTTON                          = 2;
    private final int                   REGISTERBUTTON                      = 3;
    private final int                   ANSWERBUTTON                        = 4;
    private final int                   CANCELBUTTON                        = 5;
    private final int                   RANDOMRINGRESPONSEBUTTON            = 6;
    private final int                   ENDTIMERBUTTON                      = 7;
    private final int                   MUTEAUDIOBUTTON                     = 8;
    private final int                   DEBUGBUTTON                         = 9;
    private final int                   CALLBUTTON                          = 10;
    private final int                   ENDBUTTON                           = 11;
    private final int                   RESTARTSOFTPHONEBUTTON              = 12;

    private File                        file;
    private String                      dataDir;
    private String                      soundsDir;
    private String                      vergunningDir;
    private String                      databasesDir;
    private String                      configDir;
    private String                      binDir;
    private String                      logDir;
    private String                      fileSeparator;
    private String                      lineTerminator;
    private String                      platform;

    private String                      logDateString;
    private FileWriter                  logFileWriter;
    private String                      logFileString;
    private String                      logBuffer                           = "";

    private TableCellRenderer           phonesPoolTableCellRenderer;
    private TableColumn                 phonesPoolTableColumn;

    private SysMonitor                  sysMonitor;
    private TimeTool                    timeTool;

//    private ExecutorService             sipstateUpdateThreadPool;
//    private ExecutorService             responseUpdateThreadPool;

    private boolean                     defaultConstructorIsReady = false;
    private WebLog weblog;

    /**
     *
     * @throws SQLException
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws Exception
     */
    @SuppressWarnings({"static-access", "static-access", "static-access"})
    public ECallCenter21() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, Exception
    {
        eCallCenterReference = this; // A thread doesn't inherit local varables, but it does local finals / constants

        String[] status = new String[2];

//        sipstateUpdateThreadPool = Executors.newCachedThreadPool();
//        responseUpdateThreadPool = Executors.newCachedThreadPool();

        platform = System.getProperty("os.name").toLowerCase();
        if ( platform.indexOf("windows") != -1 ) { fileSeparator = "\\"; lineTerminator = "\r\n"; } else { fileSeparator = "/"; lineTerminator = "\r\n"; }

        plaf = new String[4];
        plafSelected = new String();
        plaf[0] = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
        plaf[1] = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
        plaf[2] = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
        plaf[3] = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";

        setLookAndFeel(PLAF_NIMBUS);

        setMinimumSize(new Dimension(710,598)); setMaximumSize(new Dimension(710,830)); setPreferredSize(getMaximumSize()); setResizable(false); setVisible(false); setVisible(true);
	initComponents();

        Thread defaultConstructorThread = new Thread( allThreadsGroup, new Runnable()
        {
            @Override
            public void run()
            {
                String[] status = new String[2];
                String imgName = "/images/voipstormboxicon.jpg"; URL imgURL = getClass().getResource(imgName); Image image = Toolkit.getDefaultToolkit().getImage(imgURL); setIconImage(image);
                setImagePanelVisible(true);
                initSlidersSmooth();

                sysMonitor = new SysMonitor();

                dataDir = "data" + fileSeparator;
                soundsDir = dataDir + "sounds" + fileSeparator;
                vergunningDir = dataDir + "license" + fileSeparator;
                databasesDir = dataDir + "databases" + fileSeparator;
                configDir = dataDir + "config" + fileSeparator;
                binDir = dataDir + "bin" + fileSeparator;
                logDir = dataDir + "log" + fileSeparator;

                currentTimeCalendar = Calendar.getInstance();
                logDateString = "" +
                String.format("%04d", currentTimeCalendar.get(Calendar.YEAR)) +
                String.format("%02d", currentTimeCalendar.get(Calendar.MONTH) + 1) +
                String.format("%02d", currentTimeCalendar.get(Calendar.DAY_OF_MONTH)) + "_" +
                String.format("%02d", currentTimeCalendar.get(Calendar.HOUR_OF_DAY)) +
                String.format("%02d", currentTimeCalendar.get(Calendar.MINUTE)) +
                String.format("%02d", currentTimeCalendar.get(Calendar.SECOND));
                logFileString = logDir + logDateString + "_" + THISPRODUCT + ".log";

        //        System.out.println("\r\nChecking Directories...");
                showStatus(Vergunning.PRODUCT + "Checking Directories...", true, false);
                boolean missingDirsDetected = false;
                boolean missingCriticalDirsDetected = false;
                file = new File(logDir);        if (!file.exists()) { if (new File(logDir).mkdir())         { missingDirsDetected = true; showStatus("Info:     Creating missing directory: " + logDir, true, false); } }
                file = new File(dataDir);       if (!file.exists()) { if (new File(dataDir).mkdir())        { missingDirsDetected = true; showStatus("Warning:  Creating missing directory: " + dataDir, true, true); } }
                file = new File(soundsDir);     if (!file.exists()) { if (new File(soundsDir).mkdir())      { missingDirsDetected = true; showStatus("Critical: Creating missing directory: " + soundsDir, true, true); missingCriticalDirsDetected = true; } }
                file = new File(vergunningDir); if (!file.exists()) { if (new File(vergunningDir).mkdir())  { missingDirsDetected = true; showStatus("Info:     Creating missing directory: " + vergunningDir, true, true); } }
                file = new File(databasesDir);  if (!file.exists()) { if (new File(databasesDir).mkdir())   { missingDirsDetected = true; showStatus("Info:     Creating missing directory: " + databasesDir, true, true); } }
                file = new File(configDir);     if (!file.exists()) { if (new File(configDir).mkdir())      { missingDirsDetected = true; showStatus("Info:     Creating missing directory: " + configDir, true, true); } }
                file = new File(binDir);        if (!file.exists()) { if (new File(binDir).mkdir())         { missingDirsDetected = true; showStatus("Critical: Creating missing directory: " + binDir, true, true); missingCriticalDirsDetected = true; } }
                if ( missingCriticalDirsDetected )  { showStatus("Critical directories were missing!!! Please download the entire VoipStorm package at: " + Vergunning.WEBLINK, true, true); try { Thread.sleep(4000); } catch (InterruptedException ex) { } }
                if ( missingDirsDetected )          { showStatus("VoipStorm directory structure built", true, true); try { Thread.sleep(1000); } catch (InterruptedException ex) { } }

                try { weblog = new WebLog(); } catch (Exception ex) { }

                Thread webLogThread = new Thread(new Runnable()
                {
                    @Override
                    @SuppressWarnings({"static-access"})
                    public void run()
                    {
                        try { weblog.send(THISPRODUCT + " Starting"); } catch (Exception ex) { }
                    }
                });
                webLogThread.setName("webLogThread");
                webLogThread.setDaemon(runThreadsAsDaemons);
                webLogThread.start();

                registerSpeedValue.setText(Integer.toString(registrationBurstDelay)); registrationBurstDelay = registerSpeedSlider.getValue();
                inboundRingingResponseDelayValue.setText(Integer.toString(inboundRingingResponseDelaySlider.getValue()));
                inboundRingingResponseBusyRatioValue.setText(Integer.toString(inboundRingingResponseBusyRatioSlider.getValue()));
                inboundEndDelayValue.setText(Integer.toString(inboundEndDelaySlider.getValue()));

                vmUsagePauseValue.setText(Integer.toString(vmUsageThresholdSlider.getValue())); vmUsagePauseThreashold = vmUsageThresholdSlider.getValue();
                memFreeThresholdValue.setText(Integer.toString(memFreeThresholdSlider.getValue())); memFreeThreshold = memFreeThresholdSlider.getValue();
                heapMemFreeThresholdValue.setText(Integer.toString(heapMemFreeThresholdSlider.getValue())); heapMemFreeThreshold = heapMemFreeThresholdSlider.getValue();
                connectingTallyLimitValue.setText(Integer.toString(connectingTallyLimitSlider.getValue())); connectingTallyLimit = connectingTallyLimitSlider.getValue();
                callingTallyLimitValue.setText(Integer.toString(callingTallyLimitSlider.getValue())); callingTallyLimit = callingTallyLimitSlider.getValue();
                establishedTallyLimitValue.setText(Integer.toString(establishedTallyLimitSlider.getValue())); establishedTallyLimit = establishedTallyLimitSlider.getValue();
                callSpeedValue.setText(Integer.toString(callSpeedSlider.getValue())); outboundBurstDelay = callSpeedSlider.getValue();

                status	  = new String[2];
                status[0] = "0"; status[1] = "";

                nlLocale = new Locale("nl");
                boundMode = "Outbound";
                callCenterStatus = POWEREDOFF;

        //        status = shell.getPID(); if (status[0].equals("0"))
        //        {
        //            pid = Integer.parseInt(status[1]);
        //            outboundCallsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "In/Outbound Campaign Controls " + Integer.toString(pid), javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("STHeiti", 0, 14), new java.awt.Color(255, 255, 255))); // NOI18N
        //        }
        //        else { pid = 0; }

                softphonesQuantity = 0;

                setTitle(getWindowTitle());
        //	mainPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, getBrand() + " " + getProduct() + " " + getVersion(), javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("STHeiti", 0, 12), new java.awt.Color(102, 102, 102))); // NOI18N
        //	configurationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Proxy Configuration", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("STHeiti", 0, 12), new java.awt.Color(255, 255, 255))); // NOI18N

                // Starting the Database Server

                ultraShortMessagePeriod = 0;
                smoothMovementPeriod = 40;
        //	eyeBlinkMessagePeriod = 250;
//        	shortMessagePeriod = 1000;
                mediumMessagePeriod = 2000;
                myCoordinate = new Coordinate();

                brandLabel.setText(Vergunning.BRAND);
                brandDescriptionLabel.setText(Vergunning.BRAND_DESCRIPTION);
                productLabel.setText(Vergunning.PRODUCT);
                productDescriptionLabel.setText(Vergunning.PRODUCT_DESCRIPTION);
                copyrightLabel.setText(getWarning() + " " + getCopyright() + " " + getBrand() + " " + getBusiness() + " - Author: " + getAuthor());

                debugging           = false;
                allThreadsGroup	    = new ThreadGroup("AllThreads");
                vmUsageStatus	    = new String[2];
                memFreeStatus	    = new String[2];

                localDisplayData    = new DisplayData();
        //	localSpeakerData    = new SpeakerData();

                vmUsageStatus[0] = "0"; vmUsageStatus[1] = "";
                memFreeStatus[0] = "0"; memFreeStatus[1] = "";

        //	inboundSoftPhonesAvailableCounter = 0;
                inboundInstanceCounter = 0;
                outboundInstanceCounter = 0;
            //        outboundPowerToggleButton.setEnabled(false);

            //        myClickOnSoundTool          = new SoundTool(SoundTool.CLICKONTONE);
            //        myClickOffSoundTool         = new SoundTool(SoundTool.CLICKOFFTONE);
            //        mySuccessSoundTool          = new SoundTool(SoundTool.SUCCESSTONE);
            //        myPowerSuccessSoundTool     = new SoundTool(SoundTool.POWERSUCCESSTONE);
            //        myFailureSoundTool          = new SoundTool(SoundTool.FAILURETONE);
            //        myTickSoundTool             = new SoundTool(SoundTool.TICKTONE);
            //        myRegisterEnabledSoundTool  = new SoundTool(SoundTool.REGISTERENABLEDTONE);
            //        myRegisterDisabledSoundTool = new SoundTool(SoundTool.REGISTERDISABLEDTONE);
            //        myAnswerEnabledSoundTool    = new SoundTool(SoundTool.ANSWERENABLEDTONE);
            //        myAnswerDisabledSoundTool   = new SoundTool(SoundTool.ANSWERDISABLEDTONE);
            //        myCancelEnabledSoundTool    = new SoundTool(SoundTool.CANCELENABLEDTONE);
            //        myCancelDisabledSoundTool   = new SoundTool(SoundTool.CANCELDISABLEDTONE);
            //        myMuteEnabledSoundTool      = new SoundTool(SoundTool.MUTEENABLEDTONE);
            //        myMuteDisabledSoundTool     = new SoundTool(SoundTool.MUTEDISABLEDTONE);
            //
            //        myRingToneSoundTool         = new SoundTool(SoundTool.RINGTONE);
            //        myDialToneSoundTool         = new SoundTool(SoundTool.DEADTONE);
            //        myCallToneSoundTool         = new SoundTool(SoundTool.CALLTONE);
            //        myBusyToneSoundTool         = new SoundTool(SoundTool.BUSYTONE);
            //        myDeadToneSoundTool         = new SoundTool(SoundTool.DEADTONE);
            //        myErrorToneSoundTool        = new SoundTool(SoundTool.ERRORTONE);

                configurationCallCenter = new Configuration();
                showStatus("Loading CallCenter Configuration...", true, true); /* true = logToApplic, true = logToFile */
                status = configurationCallCenter.loadConfiguration("3");
                if ( status[0].equals("1") ) // loadConfig failed
                {
                    logToApplication("Loading CallCenter Configuration Failed: " + status[1]);
                    showStatus("Loading CallCenter Configuration Failed, creating new Inbound Config", true, true); /* true = logToApplic, true = logToFile */
                    configurationCallCenter.createConfiguration();
                    clientIPField.setText(configurationCallCenter.getClientIP());
                    pubIPField.setText(configurationCallCenter.getPublicIP());
                    clientPortField.setText(configurationCallCenter.getClientPort());
                    domainField.setText(configurationCallCenter.getDomain());
                    serverIPField.setText(configurationCallCenter.getServerIP());
                    serverPortField.setText(configurationCallCenter.getServerPort());
                    prefPhoneLinesSlider.setMaximum(vergunning.getPhoneLines());
                    prefPhoneLinesSlider.setValue(vergunning.getPhoneLines());
                    usernameField.setText(configurationCallCenter.getUsername());
                    toegangField.setText(configurationCallCenter.getToegang());
                    if (configurationCallCenter.getRegister().equals("1")) {registerCheckBox.setSelected(true);}    else {registerCheckBox.setSelected(false);}
                    if (configurationCallCenter.getIcons().equals("1"))    {iconsCheckBox.setSelected(true);}       else {iconsCheckBox.setSelected(false);}
                    showStatus("Saving new CallCenter Configuration...", true, true); /* true = logToApplic, true = logToFile */
                    configurationCallCenter.saveConfiguration("3");
            //            myFailureSoundTool.play();
                }
                else // loadConfig Succeeded
                {
            //            myPowerSuccessSoundTool.play();
                    clientIPField.setText(configurationCallCenter.getClientIP());
                    pubIPField.setText(configurationCallCenter.getPublicIP());
                    clientPortField.setText(configurationCallCenter.getClientPort());
                    domainField.setText(configurationCallCenter.getDomain());
                    serverIPField.setText(configurationCallCenter.getServerIP());
                    serverPortField.setText(configurationCallCenter.getServerPort());

                    prefPhoneLinesSlider.setMaximum(Integer.parseInt(configurationCallCenter.getPrefPhoneLines())); prefPhoneLinesSlider.setValue(Integer.parseInt(configurationCallCenter.getPrefPhoneLines()));

                    usernameField.setText(configurationCallCenter.getUsername());
                    toegangField.setText(configurationCallCenter.getToegang());
                    if (configurationCallCenter.getRegister().equals("1")) {registerCheckBox.setSelected(true);}    else {registerCheckBox.setSelected(false);}
                    if (configurationCallCenter.getIcons().equals("1"))    {iconsCheckBox.setSelected(true);}       else {iconsCheckBox.setSelected(false);}
                    showStatus("CallCenter Configuration Loaded Successfully", true, true); /* true = logToApplic, true = logToFile */
                }

                icons = new Icons(PHONESPOOLTABLECOLUMNWIDTH, PHONESPOOLTABLECOLUMNHEIGHT, iconsCheckBox.isSelected());

                lastTimeDashboardCalendar = Calendar.getInstance(); currentTimeDashboardCalendar = Calendar.getInstance(); // Prevent nullpointer in dashboard timer

                updateSystemStatsTimer  = new Timer(); updateSystemStatsTimer.scheduleAtFixedRate(new UpdateSystemStatsTimer(eCallCenterReference), (long)(0), updateSystemStatsTimerFastInterval);
                showStatus("updateSystemStatsTimer Scheduled immediate at " + Math.round(updateSystemStatsTimerFastInterval / 1000) + " Sec Interval", true, true); /* true = logToApplic, true = logToFile */
                updateStallerTimer      = new Timer(); updateStallerTimer.scheduleAtFixedRate(new UpdateStallerDetectorTimer(eCallCenterReference), (long)(0), updateStallerTimerInterval);
                showStatus("updateStallerTimer     Scheduled immediate at " + Math.round(updateStallerTimerInterval / 1000) + " Sec Interval", true, true); /* true = logToApplic, true = logToFile */
                updateVergunningTimer      = new Timer(); updateVergunningTimer.scheduleAtFixedRate(new UpdateVergunningTimer(eCallCenterReference), (long)(0), updateVergunningTimerInterval);
                showStatus("updateLicenseTimer     Scheduled immediate at " + Math.round(updateVergunningTimerInterval / 1000) + " Sec Interval", true, true); /* true = logToApplic, true = logToFile */
                updateDashboardTimer    = new Timer(); updateDashboardTimer.scheduleAtFixedRate(new UpdateDashboardTimer(eCallCenterReference),     (long)(0), updateDashboardTimerInterval);
                showStatus("updateDashboardTimer   Scheduled immediate at " + Math.round(updateDashboardTimerInterval / 1000) + " Sec Interval", true, true); /* true = logToApplic, true = logToFile */
                updateAutoSpeedTimer    = new Timer(); updateAutoSpeedTimer.scheduleAtFixedRate(new UpdateAutoSpeedTimer(eCallCenterReference),     (long)(0), updateAutoSpeedTimerInterval);
                showStatus("updateAutoSpeedTimer   Scheduled immediate at " + Math.round(updateAutoSpeedTimerInterval / 1000) + " Sec Interval", true, true); /* true = logToApplic, true = logToFile */

                shell = new Shell();
                platform = shell.getPlatform().toLowerCase();
                if	( platform.indexOf("mac os x") != -1 )      { systemStatsTable.setValueAt("RealMemFree", 2, 0); }
                else if ( platform.indexOf("linux") != -1 )         { systemStatsTable.setValueAt("TotMemFree", 2, 0); } //phonesPoolTable.setFont(new java.awt.Font("STHeiti", 0, 12));
                else if ( platform.indexOf("sunos") != -1 )         { systemStatsTable.setValueAt("TotMemFree", 2, 0); }
                else if ( platform.indexOf("hpux") != -1 )          { systemStatsTable.setValueAt("TotMemFree", 2, 0); }
                else if ( platform.indexOf("aix") != -1 )           { systemStatsTable.setValueAt("TotMemFree", 2, 0); }
                else if ( platform.indexOf("bsd") != -1 )           { systemStatsTable.setValueAt("TotMemFree", 2, 0); }
                else if ( platform.indexOf("windows") != -1 )       { systemStatsTable.setValueAt("TotMemFree", 2, 0); }
                else                                                { systemStatsTable.setValueAt(platform + "?", 2, 0); setAutoSpeed(false); }

        //	if (snmpCheckBox.isSelected())
        //	{
        //	    mySNMP = new SNMPClient();
        //	    showStatus("Checking your SNMP server...", true, true); status = mySNMP.getStat(mySNMP.CPUIDLEOID);
        //	    if (status[0].equals("1")) { showStatus("Is your SNMP server running?", true, true); System.exit(1);}
        //
        //	    // Setup the infrequent SystemStats Timer
        //	    updateSystemStatsTimer.cancel(); updateSystemStatsTimer.purge();
        //            showStatus("updateSystemStatsTimer Canceled!", true, true); /* true = logToApplic, true = logToFile */
        //	    updateSystemStatsTimer = new Timer(); updateSystemStatsTimer.scheduleAtFixedRate(new UpdateSystemStatsTimer(this), (long)(0), (updateSystemStatsTimerFastInterval));
        //            showStatus("updateSystemStatsTimer Scheduled immediate at " + Math.round(updateSystemStatsTimerFastInterval / 1000) + " Sec Interval", true, true); /* true = logToApplic, true = logToFile */
        //	}

        //	captionTable.setValueAt(onSymbol                    + " ON", 0, 0);
        //	captionTable.setValueAt("IDL/REG", 0, 1);
        //	captionTable.setValueAt(connectingSymbol            + " CON", 0, 2);
        //	captionTable.setValueAt(callingSymbol               + " CLL", 0, 3);
        //	captionTable.setValueAt(ringingSymbol               + " RNG", 0, 4);
        //	captionTable.setValueAt(acceptingSymbol             + " ACC", 0, 5);
        //	captionTable.setValueAt(talkingSymbol               + " TLK" , 0, 6);
        //	captionTable.setValueAt(localcancelSymbol           + " CAN", 0, 7);
        //	captionTable.setValueAt(localbusySymbol             + " BSY", 0, 8);
        //	captionTable.setValueAt(localbyeSymbol              + " " + remotebyeSymbol  + " BYE", 0, 9);

                captionTable.setValueAt("ON", 0, 0);
                captionTable.setValueAt("IDL/REG", 0, 1);
                captionTable.setValueAt("CON", 0, 2);
                captionTable.setValueAt("TRY", 0, 3);
                captionTable.setValueAt("CLL", 0, 4);
                captionTable.setValueAt("RNG", 0, 5);
                captionTable.setValueAt("ACC", 0, 6);
                captionTable.setValueAt("TLK", 0, 7);
                captionTable.setValueAt("CAN", 0, 8);
                captionTable.setValueAt("BSY", 0, 9);
                captionTable.setValueAt("BYE", 0, 10);

                // Set the CallRatio Pie Chart
                callRatioChartData = new DefaultPieDataset(); // callRatioChartData.setValue("Slack", 0); callRatioChartData.setValue("Busy", 0); callRatioChartData.setValue("Success", 0);
                callRatioChart = ChartFactory.createPieChart("Waiting for Campaign...", callRatioChartData, true, true, false ); // legend? // tooltips? // URLs?
                chartPanel = new ChartPanel(callRatioChart);

                org.jdesktop.layout.GroupLayout graphInnerPanelLayout = new org.jdesktop.layout.GroupLayout(graphInnerPanel);
                graphInnerPanel.setLayout(graphInnerPanelLayout);
                graphInnerPanelLayout.setHorizontalGroup(graphInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(chartPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 706, Short.MAX_VALUE));
                graphInnerPanelLayout.setVerticalGroup(graphInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(chartPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE));
                chartPanel.setFont(new java.awt.Font("STHeiti", 0, 10)); // NOI18N
                graphInnerPanel.setVisible(false); chartPanel.setVisible(false); chartPanel.setDoubleBuffered(true);

                // Set the PerformanceMeter Dial
                performanceMeter = new PerformanceMeter("Performance", vmUsageDecelerationThreashold, (Vergunning.CALLSPERHOUR_ENTERPRISE / 100));
                performanceChartPanel = new ChartPanel(performanceMeter.chart1);

                org.jdesktop.layout.GroupLayout graphInnerPanelLayout2 = new org.jdesktop.layout.GroupLayout(performanceMeterPanel);
                performanceMeterPanel.setLayout(graphInnerPanelLayout2);
                graphInnerPanelLayout2.setHorizontalGroup(graphInnerPanelLayout2.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(performanceChartPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, performanceDialSize, Short.MAX_VALUE));
                graphInnerPanelLayout2.setVerticalGroup(graphInnerPanelLayout2.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(performanceChartPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, performanceDialSize, Short.MAX_VALUE));
                performanceChartPanel.setFont(new java.awt.Font("STHeiti", 0, 10)); // NOI18N
                performanceMeterPanel.setVisible(true); performanceChartPanel.setVisible(true);
                performanceMeter.setCallPerHourNeedle(0);

                destination = new Destination();
//                destinationElement = new Destination();
                campaignStat = new CampaignStat();
                lastStallerCampaignStat = new CampaignStat();
                lastTimeDashboardCampaignStat = new CampaignStat();

                // Last but not least, loading the Database Client
                try {
                    dbClient = new JavaDBClient(eCallCenterReference, DATABASE);
                } catch (SQLException ex) {
                } catch (ClassNotFoundException ex) {
                } catch (InstantiationException ex) {
                } catch (IllegalAccessException ex) {
                } catch (NoSuchMethodException ex) {
                } catch (InvocationTargetException ex) {
                } catch (Exception ex) {
                }

                // Check for Open Campaigns
                String[] openCampaigns = dbClient.getOpenCampaigns();
                if ((openCampaigns != null) && (openCampaigns.length > 0))
                {
                    campaignComboBox.setModel(new javax.swing.DefaultComboBoxModel(openCampaigns));
                    campaignComboBox.setEnabled(true);
                } else {campaignComboBox.setEnabled(false); runCampaignToggleButton.setEnabled(false); stopCampaignButton.setEnabled(false);}

                callCenterIsNetManaged = false;

                vergunningStartCalendar = Calendar.getInstance();
                vergunningEndCalendar   = Calendar.getInstance();
                vergunningStartCalendar.set(Calendar.HOUR_OF_DAY, (int)0);
                vergunningStartCalendar.set(Calendar.MINUTE, (int)0);
                vergunningStartCalendar.set(Calendar.SECOND, (int)0);

                vergunning = new Vergunning();
                executeVergunning();
                if ( ! vergunning.isValid())
                {
                    vergunningCodeField.setText("");
                }
                else
                {
                    performanceMeter.setCallPerHourScale(0, (vergunning.getCallsPerHour() / 100), (vergunning.getCallsPerHour() / 1000));
                }

                timeTool = new TimeTool();
                defaultConstructorIsReady = true;
            }
        });
        defaultConstructorThread.setName("defaultConstructorThread");
        defaultConstructorThread.setDaemon(runThreadsAsDaemons);
        defaultConstructorThread.setPriority(5);
        defaultConstructorThread.start();
    }

    // Regular Mode

    /**
     *
     * @param callCenterModeParam
     * @param managedModeParam
     * @throws SQLException
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws Exception
     */
        public ECallCenter21(final String callCenterModeParam, final boolean managedModeParam) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, Exception
    {
        this(); // Execute default constructor
        while (! defaultConstructorIsReady ) { try { Thread.sleep(100); } catch (InterruptedException ex) {  } }

        Thread inboundCampaignThread = new Thread( allThreadsGroup, new Runnable()
        {
            @Override
            public void run()
            {
                callCenterIsNetManaged = managedModeParam;

                if (callCenterModeParam.equals("Inbound"))
                {
                    positionWindow("Right");
                    callCenterIsOutBound = false;
                    try { Thread.sleep(mediumMessagePeriod); } catch (InterruptedException ex) {  }
                    setPowerOn(true);
                    while (getCallCenterStatus() != POWEREDON) { try { Thread.sleep(1000); } catch (InterruptedException ex) {  } }
                    powerToggleButton.setSelected(true);
                    register();

                    initSlidersSmooth();
                }
                else if (callCenterModeParam.equals("Outbound"))
                {
                    positionWindow("Left");
                    callCenterIsOutBound = true;
                    try { Thread.sleep(mediumMessagePeriod); } catch (InterruptedException ex) {  }
                    setPowerOn(true);
                    while (getCallCenterStatus() != POWEREDON) { try { Thread.sleep(1000); } catch (InterruptedException ex) {  } }
                    powerToggleButton.setSelected(true);

                    if (callCenterIsNetManaged)
                    {
                        netManagerOutboundServerToggleButton.setEnabled(callCenterIsNetManaged);
                        netManagerOutboundServerToggleButton.setSelected(callCenterIsNetManaged);
                        enableOutboundNetManagerServer(true);
                    }

                    initSlidersSmooth();
                }
                else
                {
                    positionWindow("Right");
                    callCenterIsOutBound = true;
                    try { Thread.sleep(mediumMessagePeriod); } catch (InterruptedException ex) {  }
                    setPowerOn(true);
                    while (getCallCenterStatus() != POWEREDON) { try { Thread.sleep(1000); } catch (InterruptedException ex) {  } }
                    powerToggleButton.setSelected(true);
                    initSlidersSmooth();
                    try { Thread.sleep(5000); } catch (InterruptedException ex) {  }
                }
            }
        });
        inboundCampaignThread.setName("inboundCampaignThread");
        inboundCampaignThread.setDaemon(runThreadsAsDaemons);
        inboundCampaignThread.setPriority(7);
        inboundCampaignThread.start();
    }

    // Outbound Campaign Run Mode

    /**
     *
     * @param callCenterModeParam
     * @param managedModeParam
     * @param campaignIdParam
     * @throws SQLException
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws Exception
     */
        public ECallCenter21(final String callCenterModeParam, final boolean managedModeParam, final int campaignIdParam) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, Exception
    {
        this();
        while (! defaultConstructorIsReady ) { try { Thread.sleep(100); } catch (InterruptedException ex) {  } }

        Thread outboundCampaignThread = new Thread( allThreadsGroup, new Runnable()
        {
            @Override
            public void run()
            {
                callCenterIsNetManaged = managedModeParam;

                if (callCenterModeParam.equals("Outbound"))
                {
                    positionWindow("Left");
                    callCenterIsOutBound = true;
                    try { Thread.sleep(mediumMessagePeriod); } catch (InterruptedException ex) {  }
                    autoPowerOff = true;
                    setPowerOn(true);
                    while (
                            (getCallCenterStatus() != POWEREDON) &&
                            (getCallCenterStatus() != RUNNING) &&
                            (getCallCenterStatus() != PAUSING) &&
                            (getCallCenterStatus() != RERUNBREAK)
                          )
                    { try { Thread.sleep(100); } catch (InterruptedException ex) {  } }
                    initSlidersSmooth();
                    try { Thread.sleep(1000); } catch (InterruptedException ex) {  }
                    powerToggleButton.setSelected(true);
                    try { Thread.sleep(1000); } catch (InterruptedException ex) {  }
                    campaignComboBox.setSelectedItem(campaignIdParam);
                    campaignComboBox.setEnabled(false);

                    if (callCenterIsNetManaged)
                    {
                        netManagerOutboundServerToggleButton.setEnabled(callCenterIsNetManaged);
                        netManagerOutboundServerToggleButton.setSelected(callCenterIsNetManaged);
                        enableOutboundNetManagerServer(true);
                    }

                    runCampaignToggleButton.setSelected(true);
                    runCampaign(campaignIdParam);
                }
                else { usage(); System.exit(0);}
            }
        });
        outboundCampaignThread.setName("outboundCampaignThread");
        outboundCampaignThread.setDaemon(runThreadsAsDaemons);
        outboundCampaignThread.setPriority(7);
        outboundCampaignThread.start();
    }

    private void setLookAndFeel(int plafIndexParam)
    {
        plafSelected = plaf[plafIndexParam];
        try { UIManager.setLookAndFeel(plafSelected); } catch (ClassNotFoundException ex) {} catch (InstantiationException ex) {} catch (IllegalAccessException ex) {} catch (UnsupportedLookAndFeelException ex) {}
        setVisible(false); setVisible(true);
    }

    private void orderVergunningCode()
    {
        String[] status = new String[2];
//        activationCodeField.setText(Long.toString(Calendar.getInstance().getTimeInMillis()));
        String activationCodeString = null;
        String activationCodeKeyString = null;

        vergunning.setVergunningOrderInProgress(true);
        performanceMeter.setCallPerHourScale(0, (Vergunning.CALLSPERHOUR_ENTERPRISE / 100), (Vergunning.CALLSPERHOUR_ENTERPRISE / 1000));

        // Prematurely writing Vergunning Type Details
        if (! vergunningTypeList.isSelectionEmpty())
        {
            if           (vergunningTypeList.getSelectedValue().equals("Demo"))
            {
                vergunning.setVergunningType("Demo"); vergunning.setPhoneLines(vergunning.PHONELINES_DEMO); vergunning.setCallsPerHour(vergunning.CALLSPERHOUR_DEMO); vergunning.setMaxCalls(vergunning.MAXCALLS_DEMO); vergunning.setDestinationDigits(vergunning.DESTINATIONDIGITS_DEMO);
                vergunningDetailsTable.setValueAt(vergunning.getVergunningType(), 1, 1);
                vergunningDetailsTable.setValueAt(Integer.toString(vergunning.getPhoneLines()), 5, 1);
                vergunningDetailsTable.setValueAt(Integer.toString(vergunning.getCallsPerHour()), 6, 1);
                vergunningDetailsTable.setValueAt(Integer.toString(vergunning.getMaxCalls()), 7, 1);
                vergunningDetailsTable.setValueAt(Integer.toString(vergunning.getDestinationDigits()), 8, 1);
            }
            else if      (vergunningTypeList.getSelectedValue().equals("Standard"))
            {
                vergunning.setVergunningType("Standard"); vergunning.setPhoneLines(vergunning.PHONELINES_STANDARD); vergunning.setCallsPerHour(vergunning.CALLSPERHOUR_STANDARD); vergunning.setMaxCalls(vergunning.MAXCALLS_STANDARD); vergunning.setDestinationDigits(vergunning.DESTINATIONDIGITS_STANDARD);
                vergunningDetailsTable.setValueAt(vergunning.getVergunningType(), 1, 1);
                vergunningDetailsTable.setValueAt(Integer.toString(vergunning.getPhoneLines()), 5, 1);
                vergunningDetailsTable.setValueAt(Integer.toString(vergunning.getCallsPerHour()), 6, 1);
                vergunningDetailsTable.setValueAt(Integer.toString(vergunning.getMaxCalls()), 7, 1);
                vergunningDetailsTable.setValueAt(Integer.toString(vergunning.getDestinationDigits()), 8, 1);
            }
            else if      (vergunningTypeList.getSelectedValue().equals("Professional"))
            {
                vergunning.setVergunningType("Professional"); vergunning.setPhoneLines(vergunning.PHONELINES_PROFESSIONAL); vergunning.setCallsPerHour(vergunning.CALLSPERHOUR_PROFESSIONAL); vergunning.setMaxCalls(vergunning.MAXCALLS_PROFESSIONAL); vergunning.setDestinationDigits(vergunning.DESTINATIONDIGITS_PROFESSIONAL);
                vergunningDetailsTable.setValueAt(vergunning.getVergunningType(), 1, 1);
                vergunningDetailsTable.setValueAt(Integer.toString(vergunning.getPhoneLines()), 5, 1);
                vergunningDetailsTable.setValueAt(Integer.toString(vergunning.getCallsPerHour()), 6, 1);
                vergunningDetailsTable.setValueAt(Integer.toString(vergunning.getMaxCalls()), 7, 1);
                vergunningDetailsTable.setValueAt(Integer.toString(vergunning.getDestinationDigits()), 8, 1);
            }
            else if      (vergunningTypeList.getSelectedValue().equals("Enterprise"))
            {
                vergunning.setVergunningType("Enterprise"); vergunning.setPhoneLines(vergunning.PHONELINES_ENTERPRISE); vergunning.setCallsPerHour(vergunning.CALLSPERHOUR_ENTERPRISE); vergunning.setMaxCalls(vergunning.MAXCALLS_ENTERPRISE); vergunning.setDestinationDigits(vergunning.DESTINATIONDIGITS_ENTERPRISE);
                vergunningDetailsTable.setValueAt(vergunning.getVergunningType(), 1, 1);
                vergunningDetailsTable.setValueAt(Integer.toString(vergunning.getPhoneLines()), 5, 1);
                vergunningDetailsTable.setValueAt(Integer.toString(vergunning.getCallsPerHour()), 6, 1);
                vergunningDetailsTable.setValueAt(Integer.toString(vergunning.getMaxCalls()), 7, 1);
                vergunningDetailsTable.setValueAt(Integer.toString(vergunning.getDestinationDigits()), 8, 1);
            }
        }
        
        // Prematurely writing vergunning Start Date Details
        if (vergunningDateChooserPanel.getSelectedDate() != null)
        {
            vergunningEndCalendar.setTimeInMillis(vergunningStartCalendar.getTimeInMillis());
            if      ( vergunningPeriodList.getSelectedValue().equals("Day") )
            {
                vergunningEndCalendar.add(Calendar.DAY_OF_YEAR, 1); vergunning.setVergunningEndDate(vergunningEndCalendar);
            }
            else if ( vergunningPeriodList.getSelectedValue().equals("Week") )
            {
                vergunningEndCalendar.add(Calendar.WEEK_OF_YEAR, 1); vergunning.setVergunningEndDate(vergunningEndCalendar);
            }
            else if ( vergunningPeriodList.getSelectedValue().equals("Month") )
            {
                vergunningEndCalendar.add(Calendar.MONTH, 1); vergunning.setVergunningEndDate(vergunningEndCalendar);
            }
            else if ( vergunningPeriodList.getSelectedValue().equals("Year") )
            {
                vergunningEndCalendar.add(Calendar.YEAR, 1); vergunning.setVergunningEndDate(vergunningEndCalendar);
            }

            vergunningDetailsTable.setValueAt( String.format("%04d", vergunningStartCalendar.get(Calendar.YEAR)) + "-" + String.format("%02d", (vergunningStartCalendar.get(Calendar.MONTH)) + 1) + "-" + String.format("%02d", vergunningStartCalendar.get(Calendar.DAY_OF_MONTH)), 3, 1 );
            vergunningDetailsTable.setValueAt( String.format("%04d", vergunningEndCalendar.get(Calendar.YEAR)) + "-" + String.format("%02d", (vergunningEndCalendar.get(Calendar.MONTH)) + 1) + "-" + String.format("%02d", vergunningEndCalendar.get(Calendar.DAY_OF_MONTH)), 4, 1  );
        }

        // Prematurely writing vergunning Period Details
        if ( ! vergunningPeriodList.isSelectionEmpty()) { vergunning.setVergunningPeriod(vergunningPeriodList.getSelectedValue().toString()); vergunningDetailsTable.setValueAt(vergunning.getVergunningPeriod(), 2, 1); }

        // If all vergunning fields are selected then write ActivationCode
        if (
                (vergunningTypeList.getSelectedValue() != null) &&
                (vergunningDateChooserPanel.getSelectedDate() != null) &&
                (vergunningPeriodList.getSelectedValue() != null)
           )
        {
            status = vergunning.getAK();
            if (status[0].equals("0"))
            {
                activationCodeKeyString = status[1];
                activationCodeString =  vergunningTypeList.getSelectedValue().toString() + "-" +
                                        String.format("%04d", vergunningStartCalendar.get(Calendar.YEAR)) + "-" +
                                        String.format("%02d", (vergunningStartCalendar.get(Calendar.MONTH)) + 1 ) + "-" +
                                        String.format("%02d", vergunningStartCalendar.get(Calendar.DAY_OF_MONTH)) + "-" +
                                        vergunningPeriodList.getSelectedValue().toString() + "-" +
                                        activationCodeKeyString;
                activationCodeField.setText(activationCodeString);
                requestVergunningButton.setEnabled(true);
                showStatus("Please goto www." + Vergunning.BRAND.toLowerCase() + ".nl and request your LicenseCode", false, false);
                vergunningCodeField.setText("");
                vergunningCodeField.setEnabled(true);
            }
        }
        
        // Put a little show on stage
        if (getCallCenterStatus() == POWEREDOFF) { movePerformanceMeter((vergunning.getCallsPerHour()/100), true); } // true = smooth
    }

    /**
     *
     * @param toParam
     * @param smoothParam
     */
    synchronized protected void movePerformanceMeter(final double toParam, boolean smoothParam)
    {
        if ((smoothParam) && (!performanceMeterIsLocked))
        {
            performanceMeterIsLocked = true;
            Thread movePerformanceMeterThread = new Thread( allThreadsGroup, new Runnable()
            {
                @Override
                @SuppressWarnings("empty-statement")
                public void run()
                {
                    if (performanceMeter != null)
                    {
                        double from = performanceMeter.getCallPerHourNeedle().doubleValue();
                        double counter = from;
                        double to   = toParam;

                        if (from < to)
                        {
                            for (counter = from; counter < to; counter += 1 ) { performanceMeter.setCallPerHourNeedle(counter); try { Thread.sleep(3); } catch (InterruptedException ex) { } }
                        }
                        else
                        {
                            for (counter = from; counter > to; counter -= 1 ) { performanceMeter.setCallPerHourNeedle(counter); try { Thread.sleep(3); } catch (InterruptedException ex) { } }
                        }
                    }
                    performanceMeterIsLocked = false;
                }
            });
            movePerformanceMeterThread.setName("movePerformanceMeterThread");
            movePerformanceMeterThread.setDaemon(runThreadsAsDaemons);
            movePerformanceMeterThread.setPriority(5);
            movePerformanceMeterThread.start();
        }
        else
        {
            if (performanceMeter != null)
            {
                performanceMeter.setCallPerHourNeedle(toParam);
            }
        }
    }

    synchronized void moveVMUsageMeter(final int toParam, boolean smoothParam)
    {
        if ((smoothParam) && (!moveVMUSageMeterIsLocked))
        {
            moveVMUSageMeterIsLocked = true;
            Thread moveVMUsageMeterThread = new Thread( allThreadsGroup, new Runnable()
            {
                @Override
                @SuppressWarnings("empty-statement")
                public void run()
                {
                    if (performanceMeter != null)
                    {
                        double from = performanceMeter.getVMUsageNeedle().doubleValue();
                        double counter = from;
                        double to   = toParam;

                        if (from < to)
                        {
                            for (counter = from; counter < to; counter++ ) { performanceMeter.setVMUsageNeedle(counter); try { Thread.sleep(5); } catch (InterruptedException ex) { } }
                        }
                        else
                        {
                            for (counter = from; counter > to; counter-- ) { performanceMeter.setVMUsageNeedle(counter); try { Thread.sleep(5); } catch (InterruptedException ex) { } }
                        }
                    }
                    moveVMUSageMeterIsLocked = false;
                }
            });
            moveVMUsageMeterThread.setName("moveVMUsageMeterThread");
            moveVMUsageMeterThread.setDaemon(runThreadsAsDaemons);
            moveVMUsageMeterThread.setPriority(5);
            moveVMUsageMeterThread.start();
        }
        else
        {
            if (performanceMeter != null)
            {
                performanceMeter.setVMUsageNeedle(toParam);
            }
        }
    }

    synchronized void moveCallSpeedSlider(final int toParam, boolean smoothParam) // When going faster, this routine does not move to "toParam" but instead to a little less than current slider value
    {
        if ((smoothParam) && (!moveCallSpeedSliderIsLocked))
        {
            moveCallSpeedSliderIsLocked = true;
            Thread moveCallSpeedSliderThread = new Thread( allThreadsGroup, new Runnable()
            {
                @Override
                @SuppressWarnings("empty-statement")
                public void run()
                {
                    if (callSpeedSlider != null)
                    {
                        int from = callSpeedSlider.getValue();
                        int counter = from;
                        int to   = toParam;
                        int step = Math.round((callSpeedSlider.getMaximum() - callSpeedSlider.getMinimum()) / 100);

                        int get = (callSpeedSlider.getValue() - callSpeedSlider.getMinimum());
                        int max = (callSpeedSlider.getMaximum() - callSpeedSlider.getMinimum());
                        int perdecimal = (get / (max / 10)) + 2;

                        if (from < to) // Sliding down to longer intervals
                        {
                            for (counter = from; counter < to; counter += step ) { callSpeedSlider.setValue(counter); try { Thread.sleep(5); } catch (InterruptedException ex) { } }
                        }
                        else
                        {
                            // for (counter = from; counter > (from - (step * 5)); counter -= step ) { callSpeedSlider.setValue(counter); try { Thread.sleep(100); } catch (InterruptedException ex) { } }
                            for (counter = from; counter > (from - (step * perdecimal)); counter -= step ) { callSpeedSlider.setValue(counter); try { Thread.sleep(100); } catch (InterruptedException ex) { } }
                        }
                    }
                    moveCallSpeedSliderIsLocked = false;
                }
            });
            moveCallSpeedSliderThread.setName("moveCallSpeedSliderThread");
            moveCallSpeedSliderThread.setDaemon(runThreadsAsDaemons);
            moveCallSpeedSliderThread.setPriority(5);
            moveCallSpeedSliderThread.start();
        }
        else
        {
            if (callSpeedSlider != null)
            {
                callSpeedSlider.setValue(toParam);
            }
        }
    }

    private void executeVergunning()
    {
//        vergunningStartCalendar = licenseDateChooserPanel.getSelectedDate();

//        vergunning = new Vergunning();
        vergunning.controleerVergunning();

        if (vergunning.isValid()) { vergunningDetailsTable.setValueAt("Yes", 0, 1); } else { vergunningDetailsTable.setValueAt("No", 0, 1); }
	
        vergunningDetailsTable.setValueAt(vergunning.getVergunningType(), 1, 1);
        vergunningDetailsTable.setValueAt(vergunning.getVergunningPeriod(), 2, 1);
        vergunningDetailsTable.setValueAt(
                                        String.format("%04d", vergunning.getVergunningStartDate().get(Calendar.YEAR)) + "-" +
                                        String.format("%02d", (vergunning.getVergunningStartDate().get(Calendar.MONTH)) + 1) + "-" +
                                        String.format("%02d", vergunning.getVergunningStartDate().get(Calendar.DAY_OF_MONTH)), 3, 1
                                      );
        vergunningDetailsTable.setValueAt(
                                        String.format("%04d", vergunning.getVergunningEndDate().get(Calendar.YEAR)) + "-" +
                                        String.format("%02d", (vergunning.getVergunningEndDate().get(Calendar.MONTH)) + 1) + "-" +
                                        String.format("%02d", vergunning.getVergunningEndDate().get(Calendar.DAY_OF_MONTH)), 4, 1
                                      );
        vergunningDetailsTable.setValueAt(vergunning.getPhoneLines(), 5, 1);
        vergunningDetailsTable.setValueAt(vergunning.getCallsPerHour(), 6, 1);
        vergunningDetailsTable.setValueAt(vergunning.getMaxCalls(), 7, 1);
        vergunningDetailsTable.setValueAt(vergunning.getDestinationDigits(), 8, 1);

        if (vergunning.isValid())
        {
            if ((prefPhoneLinesSlider.getMaximum() == 0) || (prefPhoneLinesSlider.getMaximum() > vergunning.getPhoneLines()))
            {
                prefPhoneLinesSlider.setMaximum(vergunning.getPhoneLines()); prefPhoneLinesSlider.setValue(vergunning.getPhoneLines());
            }
            else
            {
                prefPhoneLinesSlider.setMaximum(vergunning.getPhoneLines()); prefPhoneLinesSlider.setValue(Integer.parseInt(configurationCallCenter.getPrefPhoneLines()));
            }

            if (! vergunning.vergunningOrderInProgress())
            {
                activationCodeField.setText(vergunning.getActivationCode());
                vergunningCodeField.setText(vergunning.getVergunningCode());
                vergunningTypeList.setSelectedValue(vergunning.getVergunningType(), false);
                vergunningPeriodList.setSelectedValue(vergunning.getVergunningPeriod(), false);
            }
            if ( Integer.parseInt(configurationCallCenter.getPrefPhoneLines()) > vergunning.getPhoneLines() ) { softphonesQuantity = vergunning.getPhoneLines(); } else { softphonesQuantity = Integer.parseInt(configurationCallCenter.getPrefPhoneLines()); }
            callSpeedSlider.setMinimum(vergunning.getOutboundBurstRate());
            vergunningCodeField.setEnabled(false);
//            applyLicenseButton.setEnabled(false);
            vergunning.setVergunningOrderInProgress(false);
            powerToggleButton.setEnabled(true);
            phoneButton.setEnabled(true);
        }
        else
        {
            prefPhoneLinesSlider.setMaximum(Integer.parseInt(configurationCallCenter.getPrefPhoneLines())); prefPhoneLinesSlider.setValue(Integer.parseInt(configurationCallCenter.getPrefPhoneLines()));
            activationCodeField.setText(vergunning.getActivationCode());
            if ( (callCenterStatus == POWEREDOFF ) && ( vergunning.vergunningOrderInProgress()) ) { vergunningCodeField.setText(""); } else { vergunningCodeField.setText(vergunning.getVergunningCode()); }
            vergunningCodeField.setForeground(Color.red);
            vergunningCodeField.setForeground(Color.black);
            if ( Integer.parseInt(configurationCallCenter.getPrefPhoneLines()) > vergunning.getPhoneLines() ) { softphonesQuantity = vergunning.getPhoneLines(); } else { softphonesQuantity = Integer.parseInt(configurationCallCenter.getPrefPhoneLines()); }
            powerToggleButton.setEnabled(false);
            phoneButton.setEnabled(false);
        }
    }

   @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lookAndFeelGroup = new javax.swing.ButtonGroup();
        colorMaskPanel = new javax.swing.JPanel();
        tabPane = new javax.swing.JTabbedPane();
        callCenterPanel = new javax.swing.JPanel();
        layeredImagePane = new javax.swing.JLayeredPane();
        phonesPoolTableScrollPane = new javax.swing.JScrollPane();
        phonesPoolTable = new javax.swing.JTable();

        imageBrandLabel = new javax.swing.JLabel();
        imageProductLabel = new javax.swing.JLabel();
        imagePostfixLabel = new javax.swing.JLabel();
        imageLinkLabel = new javax.swing.JLabel();
        imageIconLabel = new javax.swing.JLabel();
        statisticsPanel = new javax.swing.JPanel();
        mainPanel = new javax.swing.JPanel();
        systemStatsLabel = new javax.swing.JLabel();
        systemStatsScrollPane = new javax.swing.JScrollPane();
        systemStatsTable = new javax.swing.JTable();
        orderLabel = new javax.swing.JLabel();
        orderStatsScrollPane = new javax.swing.JScrollPane();
        orderTable = new javax.swing.JTable();
        phoneStatsLabel = new javax.swing.JLabel();
        phoneStatsScrollPane = new javax.swing.JScrollPane();
        phoneStatsTable = new javax.swing.JTable();
        reponseStatsLabel = new javax.swing.JLabel();
        responseStatsScrollPane = new javax.swing.JScrollPane();
        responseStatsTable = new javax.swing.JTable();
        campaignLabel = new javax.swing.JLabel();
        campaignScrollPane = new javax.swing.JScrollPane();
        campaignTable = new javax.swing.JTable();
        turnoverStatsLabel = new javax.swing.JLabel();
        turnoverStatsScrollPane = new javax.swing.JScrollPane();
        turnoverStatsTable = new javax.swing.JTable();
        graphPanel = new javax.swing.JPanel();
        graphInnerPanel = new javax.swing.JPanel();
        phoneDisplayTabPanel = new javax.swing.JPanel();
        phoneDisplayPanel = new javax.swing.JPanel();
        softphoneInfoLabel = new javax.swing.JLabel();
        proxyInfoLabel = new javax.swing.JLabel();
        primaryStatusLabel = new javax.swing.JLabel();
        primaryStatusDetailsLabel = new javax.swing.JLabel();
        secondaryStatusLabel = new javax.swing.JLabel();
        secondaryStatusDetailsLabel = new javax.swing.JLabel();
        onPanel = new javax.swing.JPanel();
        onLabel = new javax.swing.JLabel();
        idlePanel = new javax.swing.JPanel();
        idleLabel = new javax.swing.JLabel();
        connectingPanel = new javax.swing.JPanel();
        connectingLabel = new javax.swing.JLabel();
        callingPanel = new javax.swing.JPanel();
        callingLabel = new javax.swing.JLabel();
        ringingPanel = new javax.swing.JPanel();
        ringingLabel = new javax.swing.JLabel();
        acceptingPanel = new javax.swing.JPanel();
        acceptingLabel = new javax.swing.JLabel();
        talkingPanel = new javax.swing.JPanel();
        talkingLabel = new javax.swing.JLabel();
        registeredPanel = new javax.swing.JPanel();
        registeredLabel = new javax.swing.JLabel();
        answerPanel = new javax.swing.JPanel();
        answerLabel = new javax.swing.JLabel();
        mutePanel = new javax.swing.JPanel();
        muteLabel = new javax.swing.JLabel();
        cancelPanel = new javax.swing.JPanel();
        cancelLabel = new javax.swing.JLabel();
        toolsPanel = new javax.swing.JPanel();
        toolsInnerPanel = new javax.swing.JPanel();
        netManagerOutboundServerToggleButton = new javax.swing.JToggleButton();
        netManagerInboundServerToggleButton = new javax.swing.JToggleButton();
        controlsPanel = new javax.swing.JPanel();
        displayLabel = new javax.swing.JLabel();
        enableDisplayCheckBox = new javax.swing.JCheckBox();
        snmpLabel1 = new javax.swing.JLabel();
        smoothCheckBox = new javax.swing.JCheckBox();
        scanCheckBox = new javax.swing.JCheckBox();
        smoothLabel = new javax.swing.JLabel();
        lookAndFeelPanel = new javax.swing.JPanel();
        lookAndFeelRButtonMotif = new javax.swing.JRadioButton();
        lookAndFeelRButtonGTK = new javax.swing.JRadioButton();
        lookAndFeelRButtonNimbus = new javax.swing.JRadioButton();
        lookAndFeelRButtonWindows = new javax.swing.JRadioButton();
        sipInfoPanel = new javax.swing.JPanel();
        destinationScrollPane = new javax.swing.JScrollPane();
        destinationTextArea = new javax.swing.JTextArea();
        netConfigPanel = new javax.swing.JPanel();
        authenticationPanel = new javax.swing.JPanel();
        iconsLabel = new javax.swing.JLabel();
        iconsCheckBox = new javax.swing.JCheckBox();
        clientIPLabel = new javax.swing.JLabel();
        clientIPField = new javax.swing.JTextField();
        pubIPLabel = new javax.swing.JLabel();
        pubIPField = new javax.swing.JTextField();
        clientPortLabel = new javax.swing.JLabel();
        registerCheckBox = new javax.swing.JCheckBox();
        registerLabel = new javax.swing.JLabel();
        clientPortField = new javax.swing.JTextField();
        domainLabel = new javax.swing.JLabel();
        domainField = new javax.swing.JTextField();
        serverIPLabel = new javax.swing.JLabel();
        serverIPField = new javax.swing.JTextField();
        serverPortLabel = new javax.swing.JLabel();
        serverPortField = new javax.swing.JTextField();
        pfixLabel = new javax.swing.JLabel();
        usersecretLabel = new javax.swing.JLabel();
        suffixLabel = new javax.swing.JLabel();
        prefixField = new javax.swing.JTextField();
        usernameField = new javax.swing.JTextField();
        suffixField = new javax.swing.JTextField();
        saveConfigurationButton = new javax.swing.JButton();
        toegangField = new javax.swing.JPasswordField();
        secretLabel = new javax.swing.JLabel();
        prefPhoneLinesPanel = new javax.swing.JPanel();
        prefPhoneLinesSlider = new javax.swing.JSlider();
        licensePanel = new javax.swing.JPanel();
        licenseTypePanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        vergunningTypeList = new javax.swing.JList();
        licenseDatePanel = new javax.swing.JPanel();
        vergunningDateChooserPanel = new datechooser.beans.DateChooserPanel();
        licensePeriodPanel = new javax.swing.JPanel();
        licensePeriodScrollPane = new javax.swing.JScrollPane();
        vergunningPeriodList = new javax.swing.JList();
        activationCodePanel = new javax.swing.JPanel();
        activationCodeField = new javax.swing.JTextField();
        licenseCodePanel = new javax.swing.JPanel();
        vergunningCodeField = new javax.swing.JTextField();
        licenseDetailsPanel = new javax.swing.JPanel();
        licenseDetailsScrollPane = new javax.swing.JScrollPane();
        vergunningDetailsTable = new javax.swing.JTable();
        applyVergunningButton = new javax.swing.JButton();
        requestVergunningButton = new javax.swing.JButton();
        logPanel = new javax.swing.JPanel();
        logScrollPane = new javax.swing.JScrollPane();
        textLogArea = new javax.swing.JTextArea();
        aboutPanel = new javax.swing.JPanel();
        brandLabel = new javax.swing.JLabel();
        brandDescriptionLabel = new javax.swing.JTextArea();
        productLabel = new javax.swing.JLabel();
        productDescriptionLabel = new javax.swing.JTextArea();
        copyrightLabel = new javax.swing.JTextArea();
        displayPanel = new javax.swing.JPanel();
        captionTable = new javax.swing.JTable();
        statusBar = new javax.swing.JTextPane();
        controlButtonPanel = new javax.swing.JPanel();
        callButton = new javax.swing.JButton();
        serviceLoopProgressBar = new javax.swing.JProgressBar();
        autoSpeedToggleButton = new javax.swing.JToggleButton();
        powerToggleButton = new javax.swing.JToggleButton();
        runCampaignToggleButton = new javax.swing.JToggleButton();
        endButton = new javax.swing.JButton();
        phoneButton = new javax.swing.JButton();
        stopCampaignButton = new javax.swing.JButton();
        muteAudioToggleButton = new javax.swing.JToggleButton();
        campaignProgressBar = new javax.swing.JProgressBar();
        humanResponseSimulatorToggleButton = new javax.swing.JToggleButton();
        registerToggleButton = new javax.swing.JToggleButton();
        campaignComboBox = new javax.swing.JComboBox();
        debugToggleButton = new javax.swing.JToggleButton();
        resizeWindowButton = new javax.swing.JButton();
        controlSliderPanel = new javax.swing.JPanel();
        buttonPanel = new javax.swing.JPanel();
        outboundSliderPanel = new javax.swing.JPanel();
        vmUsageThresholdLabel = new javax.swing.JLabel();
        vmUsagePauseValue = new javax.swing.JLabel();
        vmUsageThresholdSlider = new javax.swing.JSlider();
        memFreeThresholdLabel = new javax.swing.JLabel();
        memFreeThresholdValue = new javax.swing.JLabel();
        memFreeThresholdSlider = new javax.swing.JSlider();
        heapMemFreeThresholdLabel = new javax.swing.JLabel();
        heapMemFreeThresholdValue = new javax.swing.JLabel();
        heapMemFreeThresholdSlider = new javax.swing.JSlider();
        connectingTallyLimitLabel = new javax.swing.JLabel();
        connectingTallyLimitValue = new javax.swing.JLabel();
        connectingTallyLimitSlider = new javax.swing.JSlider();
        callingTallyLimitLabel = new javax.swing.JLabel();
        callingTallyLimitValue = new javax.swing.JLabel();
        callingTallyLimitSlider = new javax.swing.JSlider();
        establishedTallyLimitLabel = new javax.swing.JLabel();
        establishedTallyLimitValue = new javax.swing.JLabel();
        establishedTallyLimitSlider = new javax.swing.JSlider();
        callSpeedLabel = new javax.swing.JLabel();
        callSpeedValue = new javax.swing.JLabel();
        callSpeedSlider = new javax.swing.JSlider();
        performanceMeterPanel = new javax.swing.JPanel();
        inboundSliderPanel = new javax.swing.JPanel();
        registerSpeedLabel = new javax.swing.JLabel();
        inboundRingingResponseDelayLabel = new javax.swing.JLabel();
        inboundRingingResponseBusyRatioLabel = new javax.swing.JLabel();
        inboundEndDelayLabel = new javax.swing.JLabel();
        inboundEndDelayValue = new javax.swing.JLabel();
        registerSpeedValue = new javax.swing.JLabel();
        inboundRingingResponseDelayValue = new javax.swing.JLabel();
        inboundRingingResponseBusyRatioValue = new javax.swing.JLabel();
        registerSpeedSlider = new javax.swing.JSlider();
        inboundRingingResponseDelaySlider = new javax.swing.JSlider();
        inboundRingingResponseBusyRatioSlider = new javax.swing.JSlider();
        inboundEndDelaySlider = new javax.swing.JSlider();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(216, 216, 222));
        setBounds(new java.awt.Rectangle(0, 22, 710, 796));
        setFocusable(false);
        setFont(new java.awt.Font("STHeiti", 0, 10));
        setMinimumSize(new java.awt.Dimension(710, 598));
        setName(""); // NOI18N
        setSize(new java.awt.Dimension(710, 830));

        colorMaskPanel.setFont(new java.awt.Font("STHeiti", 0, 13));
        colorMaskPanel.setMaximumSize(new java.awt.Dimension(700, 785));
        colorMaskPanel.setMinimumSize(new java.awt.Dimension(700, 400));

        tabPane.setBackground(new java.awt.Color(204, 204, 204));
        tabPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("STHeiti", 0, 13))); // NOI18N
        tabPane.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        tabPane.setToolTipText("");
        tabPane.setFocusTraversalKeysEnabled(false);
        tabPane.setFont(new java.awt.Font("STHeiti", 0, 13));
        tabPane.setMaximumSize(new java.awt.Dimension(695, 390));
        tabPane.setMinimumSize(new java.awt.Dimension(695, 390));
        tabPane.setNextFocusableComponent(statisticsPanel);
        tabPane.setPreferredSize(new java.awt.Dimension(695, 390));
        tabPane.setSize(new java.awt.Dimension(695, 390));
        tabPane.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabPaneMouseClicked(evt);
            }
        });

        callCenterPanel.setBackground(new java.awt.Color(255, 255, 255));
        callCenterPanel.setToolTipText("Visualised status & stats of all Phones in Pool");
        callCenterPanel.setFocusTraversalKeysEnabled(false);
        callCenterPanel.setFont(new java.awt.Font("STHeiti", 0, 12));
        callCenterPanel.setNextFocusableComponent(statisticsPanel);
        callCenterPanel.setOpaque(false);
        callCenterPanel.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                callCenterPanelKeyPressed(evt);
            }
        });

        layeredImagePane.setFont(new java.awt.Font("STHeiti", 0, 13));
        layeredImagePane.setMaximumSize(new java.awt.Dimension(670, 333));
        layeredImagePane.setSize(new java.awt.Dimension(670, 333));

        phonesPoolTableScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        phonesPoolTableScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        phonesPoolTableScrollPane.setAutoscrolls(true);
        phonesPoolTableScrollPane.setColumnHeaderView(null);
        phonesPoolTableScrollPane.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        phonesPoolTableScrollPane.setEnabled(false);
        phonesPoolTableScrollPane.setFocusTraversalKeysEnabled(false);
        phonesPoolTableScrollPane.setFocusable(false);
        phonesPoolTableScrollPane.setMaximumSize(new java.awt.Dimension(670, 330));
        phonesPoolTableScrollPane.setMinimumSize(new java.awt.Dimension(670, 330));
        phonesPoolTableScrollPane.setPreferredSize(new java.awt.Dimension(670, 330));

        phonesPoolTable.setFont(new java.awt.Font("STHeiti", 0, 14)); // NOI18N
        phonesPoolTable.setForeground(new java.awt.Color(102, 102, 102));
        phonesPoolTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        phonesPoolTable.setToolTipText("Double click to show Phone");
        phonesPoolTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        phonesPoolTable.setAutoscrolls(false);
        phonesPoolTable.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        phonesPoolTable.setDoubleBuffered(true);
        phonesPoolTable.setFocusTraversalKeysEnabled(false);
        phonesPoolTable.setFocusable(false);
        phonesPoolTable.setRequestFocusEnabled(false);
        phonesPoolTable.setRowSelectionAllowed(false);
        phonesPoolTable.setSelectionBackground(new java.awt.Color(204, 204, 204));
        phonesPoolTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        phonesPoolTable.setShowGrid(false);
        phonesPoolTable.getTableHeader().setResizingAllowed(false);
        phonesPoolTable.getTableHeader().setReorderingAllowed(false);
        phonesPoolTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                phonesPoolTableMouseClicked(evt);
            }
        });
        phonesPoolTableScrollPane.setViewportView(phonesPoolTable);

        phonesPoolTableScrollPane.setBounds(0, 0, 660, 330);
        layeredImagePane.add(phonesPoolTableScrollPane, javax.swing.JLayeredPane.DEFAULT_LAYER);

        imageBrandLabel.setFont(new java.awt.Font("STHeiti", 0, 48));
        imageBrandLabel.setForeground(new java.awt.Color(255, 255, 255));
        imageBrandLabel.setText("VoipStorm");
        imageBrandLabel.setToolTipText("");
        imageBrandLabel.setBounds(30, 30, 250, 50);
        layeredImagePane.add(imageBrandLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        imageProductLabel.setFont(new java.awt.Font("STHeiti", 0, 18));
        imageProductLabel.setForeground(new java.awt.Color(233, 232, 232));
        imageProductLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        imageProductLabel.setText("ECallCenter 21");
        imageProductLabel.setToolTipText("");
        imageProductLabel.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        imageProductLabel.setBounds(30, 80, 140, 20);
        layeredImagePane.add(imageProductLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        imagePostfixLabel.setFont(new java.awt.Font("STHeiti", 2, 18));
        imagePostfixLabel.setForeground(new java.awt.Color(203, 254, 254));
        imagePostfixLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        imagePostfixLabel.setText("st Century");
        imagePostfixLabel.setToolTipText("");
        imagePostfixLabel.setBounds(170, 80, 100, 20);
        layeredImagePane.add(imagePostfixLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        imageLinkLabel.setFont(new java.awt.Font("STHeiti", 0, 14));
        imageLinkLabel.setForeground(new java.awt.Color(204, 204, 204));
        imageLinkLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        imageLinkLabel.setText("http://www.voipstorm.nl/");
        imageLinkLabel.setToolTipText("");
        imageLinkLabel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        imageLinkLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                imageLinkLabelMouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                imageLinkLabelMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                imageLinkLabelMouseEntered(evt);
            }
        });
        imageLinkLabel.setBounds(240, 300, 190, 20);
        layeredImagePane.add(imageLinkLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        imageIconLabel.setFont(new java.awt.Font("STHeiti", 0, 12));
        imageIconLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        imageIconLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/vsheader.jpg"))); // NOI18N
        imageIconLabel.setToolTipText("");
        imageIconLabel.setMaximumSize(new java.awt.Dimension(670, 333));
        imageIconLabel.setMinimumSize(new java.awt.Dimension(670, 333));
        imageIconLabel.setOpaque(true);
        imageIconLabel.setPreferredSize(new java.awt.Dimension(670, 333));
        imageIconLabel.setBounds(-2, 0, 670, 330);
        layeredImagePane.add(imageIconLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        org.jdesktop.layout.GroupLayout callCenterPanelLayout = new org.jdesktop.layout.GroupLayout(callCenterPanel);
        callCenterPanel.setLayout(callCenterPanelLayout);
        callCenterPanelLayout.setHorizontalGroup(
            callCenterPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(callCenterPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(layeredImagePane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 656, Short.MAX_VALUE))
        );
        callCenterPanelLayout.setVerticalGroup(
            callCenterPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(callCenterPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(layeredImagePane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 333, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabPane.addTab("Call Center", callCenterPanel);

        statisticsPanel.setToolTipText("Overview Overall Statistics Campaign");
        statisticsPanel.setFocusTraversalKeysEnabled(false);
        statisticsPanel.setFont(new java.awt.Font("STHeiti", 0, 12));
        statisticsPanel.setNextFocusableComponent(graphPanel);
        statisticsPanel.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                statisticsPanelKeyPressed(evt);
            }
        });

        mainPanel.setBackground(new java.awt.Color(255, 255, 255));
        mainPanel.setFont(new java.awt.Font("STHeiti", 0, 13));
        mainPanel.setMaximumSize(new java.awt.Dimension(1600, 1600));
        mainPanel.setPreferredSize(new java.awt.Dimension(800, 241));

        systemStatsLabel.setFont(new java.awt.Font("STHeiti", 0, 12));
        systemStatsLabel.setForeground(new java.awt.Color(102, 102, 102));
        systemStatsLabel.setText("System (Health)");

        systemStatsScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        systemStatsScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        systemStatsTable.setFont(new java.awt.Font("STHeiti", 0, 10)); // NOI18N
        systemStatsTable.setForeground(new java.awt.Color(102, 102, 102));
        systemStatsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"VM Usage", new Long(0), "%"},
                {"Threads", new Long(0), ""},
                {"MemFree", new Long(0), "MB"},
                {"HeapMemMax", new Long(0), "MB"},
                {"HeapMemTot", new Long(0), "MB"},
                {"HeapMemFree", new Long(0), "MB"},
                {" ", null, null}
            },
            new String [] {
                "", "", ""
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Long.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        systemStatsTable.setToolTipText("System Utilization Statistics used by Automated Call Burst Rate Slider");
        systemStatsTable.setAutoCreateRowSorter(true);
        systemStatsTable.setAutoscrolls(false);
        systemStatsTable.setDoubleBuffered(true);
        systemStatsTable.setFocusable(false);
        systemStatsTable.setMaximumSize(new java.awt.Dimension(55, 110));
        systemStatsTable.setMinimumSize(new java.awt.Dimension(55, 110));
        systemStatsTable.setName("name"); // NOI18N
        systemStatsTable.setPreferredSize(new java.awt.Dimension(55, 110));
        systemStatsTable.setRowHeight(15);
        systemStatsTable.setRowSelectionAllowed(false);
        systemStatsTable.setSelectionBackground(new java.awt.Color(51, 102, 255));
        systemStatsTable.setShowGrid(false);
        systemStatsTable.setSize(new java.awt.Dimension(55, 110));
        systemStatsScrollPane.setViewportView(systemStatsTable);
        systemStatsTable.getColumnModel().getColumn(0).setResizable(false);
        systemStatsTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        systemStatsTable.getColumnModel().getColumn(1).setResizable(false);
        systemStatsTable.getColumnModel().getColumn(1).setPreferredWidth(50);
        systemStatsTable.getColumnModel().getColumn(2).setResizable(false);
        systemStatsTable.getColumnModel().getColumn(2).setPreferredWidth(20);

        orderLabel.setFont(new java.awt.Font("STHeiti", 0, 12));
        orderLabel.setForeground(new java.awt.Color(102, 102, 102));
        orderLabel.setText("Order");

        orderStatsScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        orderStatsScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        orderTable.setFont(new java.awt.Font("STHeiti", 0, 10));
        orderTable.setForeground(new java.awt.Color(102, 102, 102));
        orderTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Recipients", "-"},
                {"Time Window", "-"},
                {"Total Calls", "-"},
                {"Call Duration", "-"},
                {"Message Rate", "-"},
                {"Message Rate", "-"},
                {"SubTotal", "-"}
            },
            new String [] {
                "", ""
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        orderTable.setToolTipText("Represents highlevel Running Campaign Info based on Customer Order");
        orderTable.setAutoCreateRowSorter(true);
        orderTable.setAutoscrolls(false);
        orderTable.setDoubleBuffered(true);
        orderTable.setFocusable(false);
        orderTable.setMaximumSize(new java.awt.Dimension(55, 110));
        orderTable.setMinimumSize(new java.awt.Dimension(55, 110));
        orderTable.setName("name"); // NOI18N
        orderTable.setPreferredSize(new java.awt.Dimension(55, 110));
        orderTable.setRowHeight(15);
        orderTable.setRowSelectionAllowed(false);
        orderTable.setSelectionBackground(new java.awt.Color(51, 102, 255));
        orderTable.setShowGrid(false);
        orderTable.setSize(new java.awt.Dimension(55, 110));
        orderStatsScrollPane.setViewportView(orderTable);
        orderTable.getColumnModel().getColumn(0).setResizable(false);
        orderTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        orderTable.getColumnModel().getColumn(1).setResizable(false);
        orderTable.getColumnModel().getColumn(1).setPreferredWidth(15);

        phoneStatsLabel.setFont(new java.awt.Font("STHeiti", 0, 12));
        phoneStatsLabel.setForeground(new java.awt.Color(102, 102, 102));
        phoneStatsLabel.setText("Phone Pool");

        phoneStatsScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        phoneStatsScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        phoneStatsScrollPane.setFont(new java.awt.Font("STHeiti", 0, 13));

        phoneStatsTable.setFont(new java.awt.Font("STHeiti", 0, 10));
        phoneStatsTable.setForeground(new java.awt.Color(102, 102, 102));
        phoneStatsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Phones", new Long(0)},
                {"Processing", new Long(0)},
                {"On", new Long(0)},
                {"Registered", new Long(0)},
                {"Listening", new Long(0)},
                {"Connecting", new Long(0)},
                {"Trying", new Long(0)},
                {"Calling", new Long(0)},
                {"Accepting", new Long(0)},
                {"Ringing", new Long(0)},
                {"Established", new Long(0)},
                {"Total Calls", new Long(0)},
                {" Total Established", new Long(0)},
                {" ", null},
                {" ", null},
                {" ", null},
                {" ", null},
                {" ", null},
                {" ", null},
                {null, null},
                {" ", null}
            },
            new String [] {
                "", ""
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Long.class
            };
            boolean[] canEdit = new boolean [] {
                true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        phoneStatsTable.setToolTipText("Overall Status of all Phones in Pool");
        phoneStatsTable.setAutoCreateRowSorter(true);
        phoneStatsTable.setAutoscrolls(false);
        phoneStatsTable.setDoubleBuffered(true);
        phoneStatsTable.setEditingColumn(0);
        phoneStatsTable.setEditingRow(0);
        phoneStatsTable.setFocusable(false);
        phoneStatsTable.setMaximumSize(new java.awt.Dimension(1400, 330));
        phoneStatsTable.setMinimumSize(new java.awt.Dimension(30, 330));
        phoneStatsTable.setName("name"); // NOI18N
        phoneStatsTable.setPreferredSize(new java.awt.Dimension(75, 330));
        phoneStatsTable.setRowHeight(15);
        phoneStatsTable.setRowSelectionAllowed(false);
        phoneStatsTable.setSelectionBackground(new java.awt.Color(51, 102, 255));
        phoneStatsTable.setShowGrid(false);
        phoneStatsTable.setSize(new java.awt.Dimension(75, 190));
        phoneStatsScrollPane.setViewportView(phoneStatsTable);
        phoneStatsTable.getColumnModel().getColumn(0).setResizable(false);
        phoneStatsTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        phoneStatsTable.getColumnModel().getColumn(1).setResizable(false);
        phoneStatsTable.getColumnModel().getColumn(1).setPreferredWidth(40);

        reponseStatsLabel.setFont(new java.awt.Font("STHeiti", 0, 12));
        reponseStatsLabel.setForeground(new java.awt.Color(102, 102, 102));
        reponseStatsLabel.setText("Response");

        responseStatsScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        responseStatsScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        responseStatsTable.setFont(new java.awt.Font("STHeiti", 0, 10));
        responseStatsTable.setForeground(new java.awt.Color(102, 102, 102));
        responseStatsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Info", "1XX", new Long(0)},
                {"Success", "2XX", new Long(0)},
                {"Redirection", "3XX", new Long(0)},
                {"Client Error", "4XX", new Long(0)},
                {"Server Error", "5XX", new Long(0)},
                {"Gen. Error", "6XX", new Long(0)},
                {"", "", null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {"Timeout", null, new Long(0)},
                {" ", null, null},
                {" ", null, null},
                {" ", null, null},
                {" ", null, null},
                {" ", null, null},
                {" ", null, null},
                {" ", null, null},
                {" ", null, null},
                {" ", null, null}
            },
            new String [] {
                "", "", ""
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Long.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        responseStatsTable.setToolTipText("Represents Highlevel Proxy/Answerer Response Statistics");
        responseStatsTable.setAutoCreateRowSorter(true);
        responseStatsTable.setAutoscrolls(false);
        responseStatsTable.setDoubleBuffered(true);
        responseStatsTable.setFocusable(false);
        responseStatsTable.setMaximumSize(new java.awt.Dimension(2000, 330));
        responseStatsTable.setMinimumSize(new java.awt.Dimension(45, 330));
        responseStatsTable.setName("name"); // NOI18N
        responseStatsTable.setPreferredSize(new java.awt.Dimension(95, 330));
        responseStatsTable.setRowHeight(15);
        responseStatsTable.setRowSelectionAllowed(false);
        responseStatsTable.setSelectionBackground(new java.awt.Color(51, 102, 255));
        responseStatsTable.setShowGrid(false);
        responseStatsScrollPane.setViewportView(responseStatsTable);
        responseStatsTable.getColumnModel().getColumn(0).setResizable(false);
        responseStatsTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        responseStatsTable.getColumnModel().getColumn(1).setResizable(false);
        responseStatsTable.getColumnModel().getColumn(1).setPreferredWidth(10);
        responseStatsTable.getColumnModel().getColumn(2).setResizable(false);
        responseStatsTable.getColumnModel().getColumn(2).setPreferredWidth(25);

        campaignLabel.setFont(new java.awt.Font("STHeiti", 0, 12));
        campaignLabel.setForeground(new java.awt.Color(102, 102, 102));
        campaignLabel.setText("Campaign");

        campaignScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        campaignScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        campaignTable.setFont(new java.awt.Font("STHeiti", 0, 10));
        campaignTable.setForeground(new java.awt.Color(102, 102, 102));
        campaignTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Start Sched", "-"},
                {"End Sched", "-"},
                {"Start Exp", "-"},
                {"End Exp", "-"},
                {"Start Reg", "-"},
                {"End Reg", "-"},
                {"Time Total", "-"},
                {"Time Past", "-"},
                {"Time ETA", "-"},
                {"Throughput", "-"},
                {" ", null},
                {" ", null}
            },
            new String [] {
                "", ""
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        campaignTable.setToolTipText("Time Statistics related to running Campaign");
        campaignTable.setAutoCreateRowSorter(true);
        campaignTable.setAutoscrolls(false);
        campaignTable.setDoubleBuffered(true);
        campaignTable.setEditingColumn(0);
        campaignTable.setEditingRow(0);
        campaignTable.setFocusable(false);
        campaignTable.setMaximumSize(new java.awt.Dimension(55, 180));
        campaignTable.setMinimumSize(new java.awt.Dimension(55, 180));
        campaignTable.setName("name"); // NOI18N
        campaignTable.setPreferredSize(new java.awt.Dimension(55, 180));
        campaignTable.setRowHeight(15);
        campaignTable.setRowSelectionAllowed(false);
        campaignTable.setSelectionBackground(new java.awt.Color(51, 102, 255));
        campaignTable.setShowGrid(false);
        campaignTable.setSize(new java.awt.Dimension(55, 160));
        campaignScrollPane.setViewportView(campaignTable);
        campaignTable.getColumnModel().getColumn(0).setResizable(false);
        campaignTable.getColumnModel().getColumn(0).setPreferredWidth(30);
        campaignTable.getColumnModel().getColumn(1).setResizable(false);
        campaignTable.getColumnModel().getColumn(1).setPreferredWidth(75);

        turnoverStatsLabel.setFont(new java.awt.Font("STHeiti", 0, 12));
        turnoverStatsLabel.setForeground(new java.awt.Color(102, 102, 102));
        turnoverStatsLabel.setText("Turnover");

        turnoverStatsScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        turnoverStatsScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        turnoverStatsTable.setFont(new java.awt.Font("STHeiti", 0, 10));
        turnoverStatsTable.setForeground(new java.awt.Color(102, 102, 102));
        turnoverStatsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Hourly", "€", new Float(0.0)},
                {"Progress", "€", new Float(0.0)},
                {"Total (excl.)", "€", new Float(0.0)},
                {" ", null, null},
                {" ", null, null},
                {" ", null, null},
                {" ", null, null},
                {" ", null, null},
                {" ", null, null},
                {" ", null, null},
                {" ", null, null},
                {null, null, null}
            },
            new String [] {
                "", "", ""
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        turnoverStatsTable.setToolTipText("Financial Statistics");
        turnoverStatsTable.setAutoCreateRowSorter(true);
        turnoverStatsTable.setAutoscrolls(false);
        turnoverStatsTable.setDoubleBuffered(true);
        turnoverStatsTable.setFocusable(false);
        turnoverStatsTable.setMaximumSize(new java.awt.Dimension(55, 180));
        turnoverStatsTable.setMinimumSize(new java.awt.Dimension(55, 180));
        turnoverStatsTable.setName("name"); // NOI18N
        turnoverStatsTable.setPreferredSize(new java.awt.Dimension(55, 180));
        turnoverStatsTable.setRowHeight(15);
        turnoverStatsTable.setRowSelectionAllowed(false);
        turnoverStatsTable.setSelectionBackground(new java.awt.Color(51, 102, 255));
        turnoverStatsTable.setShowGrid(false);
        turnoverStatsTable.setSize(new java.awt.Dimension(55, 160));
        turnoverStatsScrollPane.setViewportView(turnoverStatsTable);
        turnoverStatsTable.getColumnModel().getColumn(0).setResizable(false);
        turnoverStatsTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        turnoverStatsTable.getColumnModel().getColumn(1).setResizable(false);
        turnoverStatsTable.getColumnModel().getColumn(1).setPreferredWidth(10);
        turnoverStatsTable.getColumnModel().getColumn(2).setResizable(false);
        turnoverStatsTable.getColumnModel().getColumn(2).setPreferredWidth(50);

        org.jdesktop.layout.GroupLayout mainPanelLayout = new org.jdesktop.layout.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanelLayout.createSequentialGroup()
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(mainPanelLayout.createSequentialGroup()
                        .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(mainPanelLayout.createSequentialGroup()
                                .add(60, 60, 60)
                                .add(systemStatsLabel))
                            .add(mainPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(systemStatsScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
                                    .add(campaignScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE))))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(mainPanelLayout.createSequentialGroup()
                                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                    .add(turnoverStatsScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)
                                    .add(orderStatsScrollPane, 0, 0, Short.MAX_VALUE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED))
                            .add(mainPanelLayout.createSequentialGroup()
                                .add(orderLabel)
                                .add(80, 80, 80))))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, mainPanelLayout.createSequentialGroup()
                        .add(75, 75, 75)
                        .add(campaignLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 93, Short.MAX_VALUE)
                        .add(turnoverStatsLabel)
                        .add(69, 69, 69)))
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, mainPanelLayout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(phoneStatsScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 140, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(responseStatsScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, mainPanelLayout.createSequentialGroup()
                        .add(43, 43, 43)
                        .add(phoneStatsLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 90, Short.MAX_VALUE)
                        .add(reponseStatsLabel)
                        .add(53, 53, 53)))
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanelLayout.createSequentialGroup()
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(mainPanelLayout.createSequentialGroup()
                        .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(reponseStatsLabel)
                            .add(phoneStatsLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(phoneStatsScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
                            .add(responseStatsScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)))
                    .add(mainPanelLayout.createSequentialGroup()
                        .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(systemStatsLabel)
                            .add(orderLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(systemStatsScrollPane, 0, 0, Short.MAX_VALUE)
                            .add(orderStatsScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(turnoverStatsLabel)
                            .add(campaignLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(turnoverStatsScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 166, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, campaignScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 166, Short.MAX_VALUE))))
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout statisticsPanelLayout = new org.jdesktop.layout.GroupLayout(statisticsPanel);
        statisticsPanel.setLayout(statisticsPanelLayout);
        statisticsPanelLayout.setHorizontalGroup(
            statisticsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 662, Short.MAX_VALUE)
        );
        statisticsPanelLayout.setVerticalGroup(
            statisticsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 335, Short.MAX_VALUE)
        );

        tabPane.addTab("Statistics", statisticsPanel);

        graphPanel.setEnabled(false);
        graphPanel.setFocusTraversalKeysEnabled(false);
        graphPanel.setFont(new java.awt.Font("STHeiti", 0, 12));
        graphPanel.setNextFocusableComponent(phoneDisplayPanel);
        graphPanel.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                graphPanelKeyPressed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout graphInnerPanelLayout = new org.jdesktop.layout.GroupLayout(graphInnerPanel);
        graphInnerPanel.setLayout(graphInnerPanelLayout);
        graphInnerPanelLayout.setHorizontalGroup(
            graphInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 662, Short.MAX_VALUE)
        );
        graphInnerPanelLayout.setVerticalGroup(
            graphInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 323, Short.MAX_VALUE)
        );

        org.jdesktop.layout.GroupLayout graphPanelLayout = new org.jdesktop.layout.GroupLayout(graphPanel);
        graphPanel.setLayout(graphPanelLayout);
        graphPanelLayout.setHorizontalGroup(
            graphPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 662, Short.MAX_VALUE)
            .add(graphPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(graphInnerPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        graphPanelLayout.setVerticalGroup(
            graphPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 335, Short.MAX_VALUE)
            .add(graphPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(graphPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .add(graphInnerPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        tabPane.addTab("Graph", graphPanel);

        phoneDisplayTabPanel.setToolTipText("Display for all individual SoftPhones in Pool");
        phoneDisplayTabPanel.setFocusTraversalKeysEnabled(false);
        phoneDisplayTabPanel.setFont(new java.awt.Font("STHeiti", 0, 12));
        phoneDisplayTabPanel.setNextFocusableComponent(netConfigPanel);
        phoneDisplayTabPanel.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                phoneDisplayTabPanelKeyPressed(evt);
            }
        });

        phoneDisplayPanel.setBackground(new java.awt.Color(255, 255, 255));
        phoneDisplayPanel.setToolTipText("");
        phoneDisplayPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                phoneDisplayPanelMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                phoneDisplayPanelMouseEntered(evt);
            }
        });

        softphoneInfoLabel.setFont(new java.awt.Font("STHeiti", 0, 8));
        softphoneInfoLabel.setForeground(new java.awt.Color(102, 102, 102));
        softphoneInfoLabel.setToolTipText("");

        proxyInfoLabel.setFont(new java.awt.Font("STHeiti", 0, 8));
        proxyInfoLabel.setForeground(new java.awt.Color(102, 102, 102));
        proxyInfoLabel.setToolTipText("");
        proxyInfoLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        primaryStatusLabel.setFont(new java.awt.Font("STHeiti", 1, 24));
        primaryStatusLabel.setForeground(new java.awt.Color(102, 102, 102));
        primaryStatusLabel.setToolTipText("");
        primaryStatusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        primaryStatusDetailsLabel.setFont(new java.awt.Font("STHeiti", 0, 12));
        primaryStatusDetailsLabel.setForeground(new java.awt.Color(102, 102, 102));
        primaryStatusDetailsLabel.setToolTipText("");
        primaryStatusDetailsLabel.setHorizontalAlignment(SwingConstants.CENTER);

        secondaryStatusLabel.setFont(new java.awt.Font("STHeiti", 1, 24));
        secondaryStatusLabel.setForeground(new java.awt.Color(102, 102, 102));
        secondaryStatusLabel.setToolTipText("");
        secondaryStatusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        secondaryStatusDetailsLabel.setFont(new java.awt.Font("STHeiti", 0, 12));
        secondaryStatusDetailsLabel.setForeground(new java.awt.Color(102, 102, 102));
        secondaryStatusDetailsLabel.setToolTipText("");
        secondaryStatusDetailsLabel.setHorizontalAlignment(SwingConstants.CENTER);

        onPanel.setBackground(new java.awt.Color(255, 255, 255));
        onPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        onPanel.setToolTipText("Powered On");

        onLabel.setFont(new java.awt.Font("STHeiti", 1, 8));
        onLabel.setForeground(new java.awt.Color(204, 204, 204));
        onLabel.setText("ON");

        org.jdesktop.layout.GroupLayout onPanelLayout = new org.jdesktop.layout.GroupLayout(onPanel);
        onPanel.setLayout(onPanelLayout);
        onPanelLayout.setHorizontalGroup(
            onPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, onPanelLayout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(onLabel)
                .addContainerGap())
        );
        onPanelLayout.setVerticalGroup(
            onPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(onLabel)
        );

        idlePanel.setBackground(new java.awt.Color(255, 255, 255));
        idlePanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        idlePanel.setToolTipText("Phone is Ready");

        idleLabel.setFont(new java.awt.Font("STHeiti", 1, 8));
        idleLabel.setForeground(new java.awt.Color(204, 204, 204));
        idleLabel.setText("IDL");

        org.jdesktop.layout.GroupLayout idlePanelLayout = new org.jdesktop.layout.GroupLayout(idlePanel);
        idlePanel.setLayout(idlePanelLayout);
        idlePanelLayout.setHorizontalGroup(
            idlePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, idlePanelLayout.createSequentialGroup()
                .addContainerGap(11, Short.MAX_VALUE)
                .add(idleLabel)
                .addContainerGap())
        );
        idlePanelLayout.setVerticalGroup(
            idlePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(idleLabel)
        );

        connectingPanel.setBackground(new java.awt.Color(255, 255, 255));
        connectingPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        connectingPanel.setToolTipText("Connecting Callout");

        connectingLabel.setFont(new java.awt.Font("STHeiti", 1, 8));
        connectingLabel.setForeground(new java.awt.Color(204, 204, 204));
        connectingLabel.setText("CON");

        org.jdesktop.layout.GroupLayout connectingPanelLayout = new org.jdesktop.layout.GroupLayout(connectingPanel);
        connectingPanel.setLayout(connectingPanelLayout);
        connectingPanelLayout.setHorizontalGroup(
            connectingPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(connectingPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(connectingLabel)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        connectingPanelLayout.setVerticalGroup(
            connectingPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(connectingLabel)
        );

        callingPanel.setBackground(new java.awt.Color(255, 255, 255));
        callingPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        callingPanel.setToolTipText("Calling");

        callingLabel.setFont(new java.awt.Font("STHeiti", 1, 8));
        callingLabel.setForeground(new java.awt.Color(204, 204, 204));
        callingLabel.setText("CLL");

        org.jdesktop.layout.GroupLayout callingPanelLayout = new org.jdesktop.layout.GroupLayout(callingPanel);
        callingPanel.setLayout(callingPanelLayout);
        callingPanelLayout.setHorizontalGroup(
            callingPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(callingPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(callingLabel)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        callingPanelLayout.setVerticalGroup(
            callingPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(callingLabel)
        );

        ringingPanel.setBackground(new java.awt.Color(255, 255, 255));
        ringingPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        ringingPanel.setToolTipText("Ringing");

        ringingLabel.setFont(new java.awt.Font("STHeiti", 1, 8));
        ringingLabel.setForeground(new java.awt.Color(204, 204, 204));
        ringingLabel.setText("RNG");

        org.jdesktop.layout.GroupLayout ringingPanelLayout = new org.jdesktop.layout.GroupLayout(ringingPanel);
        ringingPanel.setLayout(ringingPanelLayout);
        ringingPanelLayout.setHorizontalGroup(
            ringingPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, ringingPanelLayout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(ringingLabel)
                .addContainerGap())
        );
        ringingPanelLayout.setVerticalGroup(
            ringingPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(ringingLabel)
        );

        acceptingPanel.setBackground(new java.awt.Color(255, 255, 255));
        acceptingPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        acceptingPanel.setToolTipText("Accepting Incoming Call");

        acceptingLabel.setFont(new java.awt.Font("STHeiti", 1, 8));
        acceptingLabel.setForeground(new java.awt.Color(204, 204, 204));
        acceptingLabel.setText("ACC");

        org.jdesktop.layout.GroupLayout acceptingPanelLayout = new org.jdesktop.layout.GroupLayout(acceptingPanel);
        acceptingPanel.setLayout(acceptingPanelLayout);
        acceptingPanelLayout.setHorizontalGroup(
            acceptingPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(acceptingPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(acceptingLabel)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        acceptingPanelLayout.setVerticalGroup(
            acceptingPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(acceptingLabel)
        );

        talkingPanel.setBackground(new java.awt.Color(255, 255, 255));
        talkingPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        talkingPanel.setToolTipText("Phonecall Established (Talking)");

        talkingLabel.setFont(new java.awt.Font("STHeiti", 1, 8));
        talkingLabel.setForeground(new java.awt.Color(204, 204, 204));
        talkingLabel.setText("TALK");

        org.jdesktop.layout.GroupLayout talkingPanelLayout = new org.jdesktop.layout.GroupLayout(talkingPanel);
        talkingPanel.setLayout(talkingPanelLayout);
        talkingPanelLayout.setHorizontalGroup(
            talkingPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(talkingPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(talkingLabel)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        talkingPanelLayout.setVerticalGroup(
            talkingPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(talkingLabel)
        );

        registeredPanel.setBackground(new java.awt.Color(255, 255, 255));
        registeredPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        registeredPanel.setToolTipText("Registered with Proxy (Incoming Proxy Calls Enabled)");

        registeredLabel.setFont(new java.awt.Font("STHeiti", 1, 8));
        registeredLabel.setForeground(new java.awt.Color(204, 204, 204));
        registeredLabel.setText("REG");

        org.jdesktop.layout.GroupLayout registeredPanelLayout = new org.jdesktop.layout.GroupLayout(registeredPanel);
        registeredPanel.setLayout(registeredPanelLayout);
        registeredPanelLayout.setHorizontalGroup(
            registeredPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(registeredPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(registeredLabel)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        registeredPanelLayout.setVerticalGroup(
            registeredPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(registeredLabel)
        );

        answerPanel.setBackground(new java.awt.Color(255, 255, 255));
        answerPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        answerPanel.setToolTipText("Auto Answer Calls");

        answerLabel.setFont(new java.awt.Font("STHeiti", 1, 8));
        answerLabel.setForeground(new java.awt.Color(204, 204, 204));
        answerLabel.setText("ANS");

        org.jdesktop.layout.GroupLayout answerPanelLayout = new org.jdesktop.layout.GroupLayout(answerPanel);
        answerPanel.setLayout(answerPanelLayout);
        answerPanelLayout.setHorizontalGroup(
            answerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(answerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(answerLabel)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        answerPanelLayout.setVerticalGroup(
            answerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(answerLabel)
        );

        mutePanel.setBackground(new java.awt.Color(255, 255, 255));
        mutePanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        mutePanel.setToolTipText("Mute Audio");

        muteLabel.setFont(new java.awt.Font("STHeiti", 1, 8));
        muteLabel.setForeground(new java.awt.Color(204, 204, 204));
        muteLabel.setText("MUT");

        org.jdesktop.layout.GroupLayout mutePanelLayout = new org.jdesktop.layout.GroupLayout(mutePanel);
        mutePanel.setLayout(mutePanelLayout);
        mutePanelLayout.setHorizontalGroup(
            mutePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mutePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(muteLabel)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        mutePanelLayout.setVerticalGroup(
            mutePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(muteLabel)
        );

        cancelPanel.setBackground(new java.awt.Color(255, 255, 255));
        cancelPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        cancelPanel.setToolTipText("Auto Cancel Calls");

        cancelLabel.setFont(new java.awt.Font("STHeiti", 1, 8));
        cancelLabel.setForeground(new java.awt.Color(204, 204, 204));
        cancelLabel.setText("CAN");

        org.jdesktop.layout.GroupLayout cancelPanelLayout = new org.jdesktop.layout.GroupLayout(cancelPanel);
        cancelPanel.setLayout(cancelPanelLayout);
        cancelPanelLayout.setHorizontalGroup(
            cancelPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(cancelPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(cancelLabel)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        cancelPanelLayout.setVerticalGroup(
            cancelPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(cancelLabel)
        );

        org.jdesktop.layout.GroupLayout phoneDisplayPanelLayout = new org.jdesktop.layout.GroupLayout(phoneDisplayPanel);
        phoneDisplayPanel.setLayout(phoneDisplayPanelLayout);
        phoneDisplayPanelLayout.setHorizontalGroup(
            phoneDisplayPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(phoneDisplayPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(phoneDisplayPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(phoneDisplayPanelLayout.createSequentialGroup()
                        .add(phoneDisplayPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(primaryStatusLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 650, Short.MAX_VALUE)
                            .add(phoneDisplayPanelLayout.createSequentialGroup()
                                .add(softphoneInfoLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 351, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(proxyInfoLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 287, Short.MAX_VALUE))
                            .add(primaryStatusDetailsLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 650, Short.MAX_VALUE)
                            .add(secondaryStatusDetailsLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 650, Short.MAX_VALUE)
                            .add(secondaryStatusLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 650, Short.MAX_VALUE))
                        .addContainerGap())
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, phoneDisplayPanelLayout.createSequentialGroup()
                        .add(onPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(idlePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(connectingPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 31, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(callingPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(ringingPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 31, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(acceptingPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 31, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(talkingPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 31, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(registeredPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(answerPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cancelPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(mutePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(148, 148, 148))))
        );
        phoneDisplayPanelLayout.setVerticalGroup(
            phoneDisplayPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(phoneDisplayPanelLayout.createSequentialGroup()
                .add(phoneDisplayPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(softphoneInfoLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(proxyInfoLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(24, 24, 24)
                .add(primaryStatusLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 31, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(primaryStatusDetailsLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(24, 24, 24)
                .add(secondaryStatusLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 31, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(secondaryStatusDetailsLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 143, Short.MAX_VALUE)
                .add(phoneDisplayPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(phoneDisplayPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(phoneDisplayPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(acceptingPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, phoneDisplayPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                .add(ringingPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, phoneDisplayPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                    .add(callingPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, phoneDisplayPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                        .add(connectingPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(org.jdesktop.layout.GroupLayout.LEADING, phoneDisplayPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                            .add(idlePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                            .add(org.jdesktop.layout.GroupLayout.LEADING, onPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))))
                        .add(talkingPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(phoneDisplayPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, registeredPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(answerPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(cancelPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(mutePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout phoneDisplayTabPanelLayout = new org.jdesktop.layout.GroupLayout(phoneDisplayTabPanel);
        phoneDisplayTabPanel.setLayout(phoneDisplayTabPanelLayout);
        phoneDisplayTabPanelLayout.setHorizontalGroup(
            phoneDisplayTabPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(phoneDisplayPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        phoneDisplayTabPanelLayout.setVerticalGroup(
            phoneDisplayTabPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(phoneDisplayPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        tabPane.addTab("Display", phoneDisplayTabPanel);

        toolsInnerPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Tools", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("STHeiti", 0, 14))); // NOI18N
        toolsInnerPanel.setToolTipText("");
        toolsInnerPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                toolsInnerPanelMouseClicked(evt);
            }
        });

        netManagerOutboundServerToggleButton.setFont(new java.awt.Font("STHeiti", 0, 8));
        netManagerOutboundServerToggleButton.setText("OutManagementClient");
        netManagerOutboundServerToggleButton.setToolTipText("Outbound NetManager Server (Managed Mode)");
        netManagerOutboundServerToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                netManagerOutboundServerToggleButtonActionPerformed(evt);
            }
        });

        netManagerInboundServerToggleButton.setFont(new java.awt.Font("STHeiti", 0, 8));
        netManagerInboundServerToggleButton.setText("InManagementClient");
        netManagerInboundServerToggleButton.setToolTipText("Start Inbound NetManager Server for CallCenter Manager");
        netManagerInboundServerToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                netManagerInboundServerToggleButtonActionPerformed(evt);
            }
        });

        controlsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Controls", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("STHeiti", 0, 10))); // NOI18N

        displayLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
        displayLabel.setText("Display");

        enableDisplayCheckBox.setFont(new java.awt.Font("STHeiti", 0, 10));
        enableDisplayCheckBox.setToolTipText("Enable SoftPhones Display Feedback");
        enableDisplayCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        enableDisplayCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        enableDisplayCheckBox.setIconTextGap(0);

        snmpLabel1.setFont(new java.awt.Font("STHeiti", 0, 10));
        snmpLabel1.setText("Scan");

        smoothCheckBox.setFont(new java.awt.Font("STHeiti", 0, 10));
        smoothCheckBox.setSelected(true);
        smoothCheckBox.setToolTipText("Smooth Meter Animation (disabling slightly increases performance)");
        smoothCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        smoothCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        smoothCheckBox.setIconTextGap(0);

        scanCheckBox.setFont(new java.awt.Font("STHeiti", 0, 10));
        scanCheckBox.setToolTipText("Campaign Scan Phonenumbers Mode");
        scanCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        scanCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        scanCheckBox.setIconTextGap(0);

        smoothLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
        smoothLabel.setText("Smooth");

        org.jdesktop.layout.GroupLayout controlsPanelLayout = new org.jdesktop.layout.GroupLayout(controlsPanel);
        controlsPanel.setLayout(controlsPanelLayout);
        controlsPanelLayout.setHorizontalGroup(
            controlsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(controlsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(controlsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(displayLabel)
                    .add(enableDisplayCheckBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(controlsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(snmpLabel1)
                    .add(scanCheckBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(10, 10, 10)
                .add(controlsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(smoothLabel)
                    .add(controlsPanelLayout.createSequentialGroup()
                        .add(6, 6, 6)
                        .add(smoothCheckBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(183, Short.MAX_VALUE))
        );

        controlsPanelLayout.linkSize(new java.awt.Component[] {enableDisplayCheckBox, scanCheckBox, smoothCheckBox}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        controlsPanelLayout.setVerticalGroup(
            controlsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(controlsPanelLayout.createSequentialGroup()
                .add(displayLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(enableDisplayCheckBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 11, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(12, 12, 12))
            .add(controlsPanelLayout.createSequentialGroup()
                .add(controlsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(snmpLabel1)
                    .add(smoothLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(controlsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(smoothCheckBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, Short.MAX_VALUE)
                    .add(scanCheckBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 11, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        controlsPanelLayout.linkSize(new java.awt.Component[] {enableDisplayCheckBox, scanCheckBox, smoothCheckBox}, org.jdesktop.layout.GroupLayout.VERTICAL);

        lookAndFeelPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Look and Feel", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("STHeiti", 0, 10))); // NOI18N

        lookAndFeelGroup.add(lookAndFeelRButtonMotif);
        lookAndFeelRButtonMotif.setFont(new java.awt.Font("STHeiti", 0, 8));
        lookAndFeelRButtonMotif.setText("Motif");
        lookAndFeelRButtonMotif.setToolTipText("Set Look & Feel");
        lookAndFeelRButtonMotif.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lookAndFeelRButtonMotifMouseClicked(evt);
            }
        });

        lookAndFeelGroup.add(lookAndFeelRButtonGTK);
        lookAndFeelRButtonGTK.setFont(new java.awt.Font("STHeiti", 0, 8));
        lookAndFeelRButtonGTK.setText("GTK");
        lookAndFeelRButtonGTK.setToolTipText("Set Look & Feel");
        lookAndFeelRButtonGTK.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lookAndFeelRButtonGTKMouseClicked(evt);
            }
        });

        lookAndFeelGroup.add(lookAndFeelRButtonNimbus);
        lookAndFeelRButtonNimbus.setFont(new java.awt.Font("STHeiti", 0, 8));
        lookAndFeelRButtonNimbus.setSelected(true);
        lookAndFeelRButtonNimbus.setText("Nimbus");
        lookAndFeelRButtonNimbus.setToolTipText("Set Look & Feel");
        lookAndFeelRButtonNimbus.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lookAndFeelRButtonNimbusMouseClicked(evt);
            }
        });

        lookAndFeelGroup.add(lookAndFeelRButtonWindows);
        lookAndFeelRButtonWindows.setFont(new java.awt.Font("STHeiti", 0, 8));
        lookAndFeelRButtonWindows.setText("Windows");
        lookAndFeelRButtonWindows.setToolTipText("Set Look & Feel");
        lookAndFeelRButtonWindows.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lookAndFeelRButtonWindowsMouseClicked(evt);
            }
        });

        org.jdesktop.layout.GroupLayout lookAndFeelPanelLayout = new org.jdesktop.layout.GroupLayout(lookAndFeelPanel);
        lookAndFeelPanel.setLayout(lookAndFeelPanelLayout);
        lookAndFeelPanelLayout.setHorizontalGroup(
            lookAndFeelPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(lookAndFeelPanelLayout.createSequentialGroup()
                .add(19, 19, 19)
                .add(lookAndFeelRButtonNimbus)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(lookAndFeelRButtonWindows)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lookAndFeelRButtonGTK)
                .add(18, 18, 18)
                .add(lookAndFeelRButtonMotif)
                .addContainerGap(18, Short.MAX_VALUE))
        );
        lookAndFeelPanelLayout.setVerticalGroup(
            lookAndFeelPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(lookAndFeelPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(lookAndFeelPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lookAndFeelRButtonNimbus, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lookAndFeelRButtonWindows, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lookAndFeelRButtonGTK, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lookAndFeelRButtonMotif, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout toolsInnerPanelLayout = new org.jdesktop.layout.GroupLayout(toolsInnerPanel);
        toolsInnerPanel.setLayout(toolsInnerPanelLayout);
        toolsInnerPanelLayout.setHorizontalGroup(
            toolsInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(toolsInnerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(toolsInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, lookAndFeelPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, controlsPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, toolsInnerPanelLayout.createSequentialGroup()
                        .add(netManagerOutboundServerToggleButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 153, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(netManagerInboundServerToggleButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 153, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        toolsInnerPanelLayout.setVerticalGroup(
            toolsInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(toolsInnerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(toolsInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(netManagerOutboundServerToggleButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 37, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(netManagerInboundServerToggleButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(controlsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lookAndFeelPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(75, Short.MAX_VALUE))
        );

        toolsInnerPanelLayout.linkSize(new java.awt.Component[] {netManagerInboundServerToggleButton, netManagerOutboundServerToggleButton}, org.jdesktop.layout.GroupLayout.VERTICAL);

        sipInfoPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "SIP Information", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("STHeiti", 0, 12))); // NOI18N
        sipInfoPanel.setToolTipText("");

        destinationScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        destinationTextArea.setBackground(new java.awt.Color(204, 204, 204));
        destinationTextArea.setColumns(1);
        destinationTextArea.setFont(new java.awt.Font("STHeiti", 0, 10));
        destinationTextArea.setRows(16);
        destinationTextArea.setToolTipText("Telephone Addresses");
        destinationTextArea.setEnabled(false);
        destinationTextArea.setMinimumSize(new java.awt.Dimension(330, 65));
        destinationScrollPane.setViewportView(destinationTextArea);

        org.jdesktop.layout.GroupLayout sipInfoPanelLayout = new org.jdesktop.layout.GroupLayout(sipInfoPanel);
        sipInfoPanel.setLayout(sipInfoPanelLayout);
        sipInfoPanelLayout.setHorizontalGroup(
            sipInfoPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(destinationScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
        );
        sipInfoPanelLayout.setVerticalGroup(
            sipInfoPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(sipInfoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(destinationScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout toolsPanelLayout = new org.jdesktop.layout.GroupLayout(toolsPanel);
        toolsPanel.setLayout(toolsPanelLayout);
        toolsPanelLayout.setHorizontalGroup(
            toolsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(toolsPanelLayout.createSequentialGroup()
                .add(toolsInnerPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(sipInfoPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(47, Short.MAX_VALUE))
        );
        toolsPanelLayout.setVerticalGroup(
            toolsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(toolsPanelLayout.createSequentialGroup()
                .add(toolsInnerPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(54, 54, 54))
            .add(toolsPanelLayout.createSequentialGroup()
                .add(sipInfoPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        toolsPanelLayout.linkSize(new java.awt.Component[] {sipInfoPanel, toolsInnerPanel}, org.jdesktop.layout.GroupLayout.VERTICAL);

        tabPane.addTab("Tools", toolsPanel);

        netConfigPanel.setToolTipText("Mainly Proxy Configuration");
        netConfigPanel.setFocusTraversalKeysEnabled(false);
        netConfigPanel.setFont(new java.awt.Font("STHeiti", 0, 12));
        netConfigPanel.setNextFocusableComponent(logPanel);
        netConfigPanel.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                netConfigPanelKeyPressed(evt);
            }
        });

        authenticationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Network Configuration", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("STHeiti", 0, 12))); // NOI18N
        authenticationPanel.setToolTipText("");

        iconsLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
        iconsLabel.setText("Icons");

        iconsCheckBox.setFont(new java.awt.Font("STHeiti", 0, 10));
        iconsCheckBox.setSelected(true);
        iconsCheckBox.setToolTipText("Smooth needle movement Meters / Dials");
        iconsCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        iconsCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        iconsCheckBox.setIconTextGap(0);

        clientIPLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
        clientIPLabel.setText("Client");

        clientIPField.setBackground(new java.awt.Color(204, 204, 204));
        clientIPField.setFont(new java.awt.Font("STHeiti", 0, 10));
        clientIPField.setToolTipText("Your computer's IP (Automatic)");

        pubIPLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
        pubIPLabel.setText("Pub IP");

        pubIPField.setFont(new java.awt.Font("STHeiti", 0, 10));
        pubIPField.setToolTipText("Please fill in your public IP address (type: \"myip\" in google to find you public ip address)");
        pubIPField.setNextFocusableComponent(usernameField);

        clientPortLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
        clientPortLabel.setText("Port");

        registerCheckBox.setFont(new java.awt.Font("STHeiti", 0, 10));
        registerCheckBox.setToolTipText("Enable Automatic Proxy Login at startup");
        registerCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        registerCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        registerCheckBox.setIconTextGap(0);

        registerLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
        registerLabel.setText("Reg");

        clientPortField.setBackground(new java.awt.Color(204, 204, 204));
        clientPortField.setFont(new java.awt.Font("STHeiti", 0, 10));
        clientPortField.setToolTipText("Client Port (Tip: \"auto\")");
        clientPortField.setNextFocusableComponent(usernameField);

        domainLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
        domainLabel.setText("Domain");

        domainField.setFont(new java.awt.Font("STHeiti", 0, 10));
        domainField.setToolTipText("Internet Telephone Provider Domain (Tip: sip1.budgetphone.nl)");

        serverIPLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
        serverIPLabel.setText("Server");

        serverIPField.setFont(new java.awt.Font("STHeiti", 0, 10));
        serverIPField.setToolTipText("Internet Telephone Provider Server (Tip: sip1.budgetphone.nl)");
        serverIPField.setNextFocusableComponent(usernameField);

        serverPortLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
        serverPortLabel.setText("Port");

        serverPortField.setFont(new java.awt.Font("STHeiti", 0, 10));
        serverPortField.setToolTipText("Internet Telephone Provider Port (Tip: \"5060\")");
        serverPortField.setNextFocusableComponent(usernameField);

        pfixLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
        pfixLabel.setText("PFix");

        usersecretLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
        usersecretLabel.setText("User");

        suffixLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
        suffixLabel.setText("SFix");

        prefixField.setBackground(new java.awt.Color(204, 204, 204));
        prefixField.setFont(new java.awt.Font("STHeiti", 0, 10));
        prefixField.setToolTipText("Password Prefix (advanced usage normally not needed)");
        prefixField.setNextFocusableComponent(usernameField);

        usernameField.setFont(new java.awt.Font("STHeiti", 0, 10));
        usernameField.setToolTipText("Username (comes from your Internet Telephone Provider)");

        suffixField.setBackground(new java.awt.Color(204, 204, 204));
        suffixField.setFont(new java.awt.Font("STHeiti", 0, 10));
        suffixField.setToolTipText("Password Suffix (advanced usage normally not needed)");
        suffixField.setNextFocusableComponent(usernameField);

        saveConfigurationButton.setFont(new java.awt.Font("STHeiti", 0, 10));
        saveConfigurationButton.setText("Save");
        saveConfigurationButton.setToolTipText("Saves Config (Tip: Power Cycle again after Save)");
        saveConfigurationButton.setFocusPainted(false);
        saveConfigurationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveConfigurationButtonActionPerformed(evt);
            }
        });

        toegangField.setFont(new java.awt.Font("STHeiti", 0, 10));
        toegangField.setToolTipText("Password (comes from your Internet Telephone Provider)");

        secretLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
        secretLabel.setText("Secr");

        prefPhoneLinesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Preferred number of CallCenter Phone Lines", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("STHeiti", 0, 10))); // NOI18N

        prefPhoneLinesSlider.setFont(new java.awt.Font("STHeiti", 0, 8));
        prefPhoneLinesSlider.setMajorTickSpacing(50);
        prefPhoneLinesSlider.setMaximum(500);
        prefPhoneLinesSlider.setMinimum(100);
        prefPhoneLinesSlider.setMinorTickSpacing(25);
        prefPhoneLinesSlider.setPaintLabels(true);
        prefPhoneLinesSlider.setPaintTicks(true);
        prefPhoneLinesSlider.setSnapToTicks(true);
        prefPhoneLinesSlider.setToolTipText("The number of preferred CallCenter phonelines");
        prefPhoneLinesSlider.setValue(500);

        org.jdesktop.layout.GroupLayout prefPhoneLinesPanelLayout = new org.jdesktop.layout.GroupLayout(prefPhoneLinesPanel);
        prefPhoneLinesPanel.setLayout(prefPhoneLinesPanelLayout);
        prefPhoneLinesPanelLayout.setHorizontalGroup(
            prefPhoneLinesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(prefPhoneLinesPanelLayout.createSequentialGroup()
                .add(18, 18, 18)
                .add(prefPhoneLinesSlider, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 383, Short.MAX_VALUE)
                .addContainerGap())
        );
        prefPhoneLinesPanelLayout.setVerticalGroup(
            prefPhoneLinesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(prefPhoneLinesPanelLayout.createSequentialGroup()
                .add(prefPhoneLinesSlider, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 34, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(7, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout authenticationPanelLayout = new org.jdesktop.layout.GroupLayout(authenticationPanel);
        authenticationPanel.setLayout(authenticationPanelLayout);
        authenticationPanelLayout.setHorizontalGroup(
            authenticationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(authenticationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(authenticationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(authenticationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(prefPhoneLinesPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(authenticationPanelLayout.createSequentialGroup()
                            .add(authenticationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                .add(clientIPLabel)
                                .add(serverIPLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(domainLabel))
                            .add(13, 13, 13)
                            .add(authenticationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(authenticationPanelLayout.createSequentialGroup()
                                    .add(authenticationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                        .add(authenticationPanelLayout.createSequentialGroup()
                                            .add(pfixLabel)
                                            .add(21, 21, 21))
                                        .add(prefixField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 35, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                    .add(authenticationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(usernameField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 43, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(usersecretLabel))
                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                    .add(authenticationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(toegangField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 53, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(secretLabel))
                                    .add(authenticationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(authenticationPanelLayout.createSequentialGroup()
                                            .add(11, 11, 11)
                                            .add(suffixLabel)
                                            .add(17, 17, 17))
                                        .add(authenticationPanelLayout.createSequentialGroup()
                                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                            .add(suffixField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE))))
                                .add(serverIPField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 245, Short.MAX_VALUE)
                                .add(org.jdesktop.layout.GroupLayout.TRAILING, authenticationPanelLayout.createSequentialGroup()
                                    .add(clientIPField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE)
                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                    .add(pubIPLabel)
                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                    .add(pubIPField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 85, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .add(domainField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 245, Short.MAX_VALUE))
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(authenticationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(authenticationPanelLayout.createSequentialGroup()
                                    .add(authenticationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(clientPortLabel)
                                        .add(serverPortLabel))
                                    .add(17, 17, 17)
                                    .add(authenticationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                        .add(org.jdesktop.layout.GroupLayout.LEADING, serverPortField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE)
                                        .add(org.jdesktop.layout.GroupLayout.LEADING, clientPortField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE)))
                                .add(authenticationPanelLayout.createSequentialGroup()
                                    .add(registerCheckBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(24, 24, 24)
                                    .add(authenticationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(iconsCheckBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(iconsLabel)))
                                .add(registerLabel))
                            .addContainerGap()))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, authenticationPanelLayout.createSequentialGroup()
                        .add(saveConfigurationButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 94, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(166, 166, 166))))
        );

        authenticationPanelLayout.linkSize(new java.awt.Component[] {clientPortLabel, serverPortLabel}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        authenticationPanelLayout.setVerticalGroup(
            authenticationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(authenticationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(authenticationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(authenticationPanelLayout.createSequentialGroup()
                        .add(authenticationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(clientIPLabel)
                            .add(clientPortLabel)
                            .add(clientIPField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(pubIPLabel)
                            .add(pubIPField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(authenticationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(domainLabel)
                            .add(domainField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(authenticationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(serverIPField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(serverIPLabel)
                            .add(serverPortLabel)))
                    .add(authenticationPanelLayout.createSequentialGroup()
                        .add(clientPortField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(47, 47, 47)
                        .add(serverPortField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(7, 7, 7)
                .add(authenticationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(authenticationPanelLayout.createSequentialGroup()
                        .add(suffixLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(authenticationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                            .add(prefixField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(toegangField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(usernameField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(suffixField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(registerCheckBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(iconsCheckBox, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .add(authenticationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(pfixLabel)
                        .add(usersecretLabel)
                        .add(secretLabel))
                    .add(registerLabel)
                    .add(iconsLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(prefPhoneLinesPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(saveConfigurationButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        authenticationPanelLayout.linkSize(new java.awt.Component[] {clientIPLabel, domainLabel, serverIPLabel}, org.jdesktop.layout.GroupLayout.VERTICAL);

        authenticationPanelLayout.linkSize(new java.awt.Component[] {clientIPField, clientPortField, domainField, prefixField, pubIPField, serverIPField, serverPortField, suffixField, toegangField, usernameField}, org.jdesktop.layout.GroupLayout.VERTICAL);

        authenticationPanelLayout.linkSize(new java.awt.Component[] {clientPortLabel, serverPortLabel}, org.jdesktop.layout.GroupLayout.VERTICAL);

        org.jdesktop.layout.GroupLayout netConfigPanelLayout = new org.jdesktop.layout.GroupLayout(netConfigPanel);
        netConfigPanel.setLayout(netConfigPanelLayout);
        netConfigPanelLayout.setHorizontalGroup(
            netConfigPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, netConfigPanelLayout.createSequentialGroup()
                .addContainerGap(114, Short.MAX_VALUE)
                .add(authenticationPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(111, 111, 111))
        );
        netConfigPanelLayout.setVerticalGroup(
            netConfigPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(netConfigPanelLayout.createSequentialGroup()
                .add(authenticationPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(67, Short.MAX_VALUE))
        );

        tabPane.addTab("Config", netConfigPanel);

        licensePanel.setName("licensePanel"); // NOI18N
        licensePanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                licensePanelMouseClicked(evt);
            }
        });

        licenseTypePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "License Type", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("STHeiti", 0, 10))); // NOI18N
        licenseTypePanel.setToolTipText("");
        licenseTypePanel.setFont(new java.awt.Font("STHeiti", 0, 13));
        licenseTypePanel.setMaximumSize(new java.awt.Dimension(200, 239));
        licenseTypePanel.setPreferredSize(new java.awt.Dimension(200, 239));
        licenseTypePanel.setSize(new java.awt.Dimension(200, 239));

        vergunningTypeList.setFont(new java.awt.Font("Courier New", 1, 12));
        vergunningTypeList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Demo", "Standard", "Professional", "Enterprise" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        vergunningTypeList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        vergunningTypeList.setToolTipText("");
        vergunningTypeList.setSelectedIndices(new int[] {0});
        vergunningTypeList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                vergunningTypeListMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(vergunningTypeList);

        org.jdesktop.layout.GroupLayout licenseTypePanelLayout = new org.jdesktop.layout.GroupLayout(licenseTypePanel);
        licenseTypePanel.setLayout(licenseTypePanelLayout);
        licenseTypePanelLayout.setHorizontalGroup(
            licenseTypePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE)
        );
        licenseTypePanelLayout.setVerticalGroup(
            licenseTypePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(licenseTypePanelLayout.createSequentialGroup()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 157, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(17, Short.MAX_VALUE))
        );

        licenseDatePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "License Start", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("STHeiti", 0, 10))); // NOI18N
        licenseDatePanel.setToolTipText("");
        licenseDatePanel.setFont(new java.awt.Font("STHeiti", 0, 13));
        licenseDatePanel.setMaximumSize(new java.awt.Dimension(200, 239));
        licenseDatePanel.setMinimumSize(new java.awt.Dimension(200, 239));
        licenseDatePanel.setSize(new java.awt.Dimension(200, 239));

        vergunningDateChooserPanel.setCurrentView(new datechooser.view.appearance.AppearancesList("Bordered",
            new datechooser.view.appearance.ViewAppearance("custom",
                new datechooser.view.appearance.swing.SwingCellAppearance(new java.awt.Font("Lucida Grande", java.awt.Font.PLAIN, 13),
                    new java.awt.Color(0, 0, 0),
                    new java.awt.Color(0, 0, 255),
                    false,
                    true,
                    new datechooser.view.appearance.swing.ButtonPainter()),
                new datechooser.view.appearance.swing.SwingCellAppearance(new java.awt.Font("Lucida Grande", java.awt.Font.PLAIN, 13),
                    new java.awt.Color(0, 0, 0),
                    new java.awt.Color(0, 0, 255),
                    true,
                    true,
                    new datechooser.view.appearance.swing.ButtonPainter()),
                new datechooser.view.appearance.swing.SwingCellAppearance(new java.awt.Font("Lucida Grande", java.awt.Font.PLAIN, 13),
                    new java.awt.Color(0, 0, 255),
                    new java.awt.Color(0, 0, 255),
                    false,
                    true,
                    new datechooser.view.appearance.swing.ButtonPainter()),
                new datechooser.view.appearance.swing.SwingCellAppearance(new java.awt.Font("Lucida Grande", java.awt.Font.PLAIN, 13),
                    new java.awt.Color(128, 128, 128),
                    new java.awt.Color(0, 0, 255),
                    false,
                    true,
                    new datechooser.view.appearance.swing.LabelPainter()),
                new datechooser.view.appearance.swing.SwingCellAppearance(new java.awt.Font("Lucida Grande", java.awt.Font.PLAIN, 13),
                    new java.awt.Color(0, 0, 0),
                    new java.awt.Color(0, 0, 255),
                    false,
                    true,
                    new datechooser.view.appearance.swing.LabelPainter()),
                new datechooser.view.appearance.swing.SwingCellAppearance(new java.awt.Font("Lucida Grande", java.awt.Font.PLAIN, 13),
                    new java.awt.Color(0, 0, 0),
                    new java.awt.Color(255, 0, 0),
                    false,
                    false,
                    new datechooser.view.appearance.swing.ButtonPainter()),
                (datechooser.view.BackRenderer)null,
                false,
                true)));
    vergunningDateChooserPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.LOWERED,
        (java.awt.Color)null,
        (java.awt.Color)null));
vergunningDateChooserPanel.setLocale(new java.util.Locale("en", "", ""));
vergunningDateChooserPanel.setNavigateFont(new java.awt.Font("STHeiti", java.awt.Font.PLAIN, 10));
vergunningDateChooserPanel.setBehavior(datechooser.model.multiple.MultyModelBehavior.SELECT_SINGLE);
vergunningDateChooserPanel.addSelectionChangedListener(new datechooser.events.SelectionChangedListener() {
    public void onSelectionChange(datechooser.events.SelectionChangedEvent evt) {
        vergunningDateChooserPanelOnSelectionChange(evt);
    }
    });

    org.jdesktop.layout.GroupLayout licenseDatePanelLayout = new org.jdesktop.layout.GroupLayout(licenseDatePanel);
    licenseDatePanel.setLayout(licenseDatePanelLayout);
    licenseDatePanelLayout.setHorizontalGroup(
        licenseDatePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(org.jdesktop.layout.GroupLayout.TRAILING, licenseDatePanelLayout.createSequentialGroup()
            .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(vergunningDateChooserPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 220, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addContainerGap())
    );
    licenseDatePanelLayout.setVerticalGroup(
        licenseDatePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(licenseDatePanelLayout.createSequentialGroup()
            .add(vergunningDateChooserPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 155, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addContainerGap(19, Short.MAX_VALUE))
    );

    licensePeriodPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "License Period", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("STHeiti", 0, 10))); // NOI18N
    licensePeriodPanel.setToolTipText("");
    licensePeriodPanel.setFont(new java.awt.Font("STHeiti", 0, 13));
    licensePeriodPanel.setMaximumSize(new java.awt.Dimension(200, 239));
    licensePeriodPanel.setPreferredSize(new java.awt.Dimension(200, 239));
    licensePeriodPanel.setSize(new java.awt.Dimension(200, 239));

    vergunningPeriodList.setFont(new java.awt.Font("Courier New", 1, 12));
    vergunningPeriodList.setModel(new javax.swing.AbstractListModel() {
        String[] strings = { "Month", "Year" };
        public int getSize() { return strings.length; }
        public Object getElementAt(int i) { return strings[i]; }
    });
    vergunningPeriodList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    vergunningPeriodList.setToolTipText("");
    vergunningPeriodList.setEnabled(false);
    vergunningPeriodList.setFocusable(false);
    vergunningPeriodList.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            vergunningPeriodListMouseClicked(evt);
        }
    });
    licensePeriodScrollPane.setViewportView(vergunningPeriodList);

    org.jdesktop.layout.GroupLayout licensePeriodPanelLayout = new org.jdesktop.layout.GroupLayout(licensePeriodPanel);
    licensePeriodPanel.setLayout(licensePeriodPanelLayout);
    licensePeriodPanelLayout.setHorizontalGroup(
        licensePeriodPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(licensePeriodScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE)
    );
    licensePeriodPanelLayout.setVerticalGroup(
        licensePeriodPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(licensePeriodPanelLayout.createSequentialGroup()
            .add(licensePeriodScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 156, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addContainerGap(18, Short.MAX_VALUE))
    );

    activationCodePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Activation Code", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("STHeiti", 0, 10))); // NOI18N
    activationCodePanel.setToolTipText("");
    activationCodePanel.setFont(new java.awt.Font("STHeiti", 0, 13));
    activationCodePanel.setMaximumSize(new java.awt.Dimension(200, 239));
    activationCodePanel.setMinimumSize(new java.awt.Dimension(200, 239));
    activationCodePanel.setSize(new java.awt.Dimension(200, 239));

    activationCodeField.setBackground(new java.awt.Color(204, 204, 204));
    activationCodeField.setFont(new java.awt.Font("Courier New", 1, 10));
    activationCodeField.setToolTipText("Please give this ActivationCode to the Sales department");
    activationCodeField.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
    activationCodeField.setMaximumSize(new java.awt.Dimension(483, 20));
    activationCodeField.setMinimumSize(new java.awt.Dimension(483, 20));
    activationCodeField.setPreferredSize(new java.awt.Dimension(483, 20));
    activationCodeField.setSize(new java.awt.Dimension(483, 20));

    org.jdesktop.layout.GroupLayout activationCodePanelLayout = new org.jdesktop.layout.GroupLayout(activationCodePanel);
    activationCodePanel.setLayout(activationCodePanelLayout);
    activationCodePanelLayout.setHorizontalGroup(
        activationCodePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(activationCodePanelLayout.createSequentialGroup()
            .add(activationCodeField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 461, Short.MAX_VALUE)
            .addContainerGap())
    );
    activationCodePanelLayout.setVerticalGroup(
        activationCodePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(activationCodePanelLayout.createSequentialGroup()
            .add(activationCodeField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addContainerGap(17, Short.MAX_VALUE))
    );

    licenseCodePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "License Code", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("STHeiti", 0, 10))); // NOI18N
    licenseCodePanel.setToolTipText("");
    licenseCodePanel.setFont(new java.awt.Font("STHeiti", 0, 13));
    licenseCodePanel.setMaximumSize(new java.awt.Dimension(200, 239));
    licenseCodePanel.setMinimumSize(new java.awt.Dimension(200, 239));
    licenseCodePanel.setSize(new java.awt.Dimension(200, 239));

    vergunningCodeField.setFont(new java.awt.Font("Courier New", 1, 10));
    vergunningCodeField.setToolTipText("Please fill in the LicenseCode here that you recieved from the Sales Department or Doubleclick to enter License Authorisation Code");
    vergunningCodeField.setEnabled(false);
    vergunningCodeField.setMaximumSize(new java.awt.Dimension(483, 20));
    vergunningCodeField.setMinimumSize(new java.awt.Dimension(483, 20));
    vergunningCodeField.setPreferredSize(new java.awt.Dimension(483, 20));
    vergunningCodeField.setSize(new java.awt.Dimension(483, 20));
    vergunningCodeField.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            vergunningCodeFieldMouseClicked(evt);
        }
    });
    vergunningCodeField.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            vergunningCodeFieldActionPerformed(evt);
        }
    });
    vergunningCodeField.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyReleased(java.awt.event.KeyEvent evt) {
            vergunningCodeFieldKeyReleased(evt);
        }
    });

    org.jdesktop.layout.GroupLayout licenseCodePanelLayout = new org.jdesktop.layout.GroupLayout(licenseCodePanel);
    licenseCodePanel.setLayout(licenseCodePanelLayout);
    licenseCodePanelLayout.setHorizontalGroup(
        licenseCodePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(licenseCodePanelLayout.createSequentialGroup()
            .add(vergunningCodeField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 461, Short.MAX_VALUE)
            .addContainerGap())
    );
    licenseCodePanelLayout.setVerticalGroup(
        licenseCodePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(licenseCodePanelLayout.createSequentialGroup()
            .add(vergunningCodeField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addContainerGap(17, Short.MAX_VALUE))
    );

    licenseDetailsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "License Details", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("STHeiti", 0, 10))); // NOI18N
    licenseDetailsPanel.setToolTipText("");
    licenseDetailsPanel.setFont(new java.awt.Font("STHeiti", 0, 13));
    licenseDetailsPanel.setMaximumSize(new java.awt.Dimension(200, 239));
    licenseDetailsPanel.setPreferredSize(new java.awt.Dimension(200, 239));
    licenseDetailsPanel.setSize(new java.awt.Dimension(200, 239));

    licenseDetailsScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    licenseDetailsScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
    licenseDetailsScrollPane.setFont(new java.awt.Font("STHeiti", 0, 13));

    vergunningDetailsTable.setFont(new java.awt.Font("STHeiti", 0, 8));
    vergunningDetailsTable.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][] {
            {"Licensed", null},
            {"Type", null},
            {"Period", null},
            {"Start Date", null},
            {"End Date", null},
            {"Phonelines", null},
            {"Calls / Hour", null},
            {"Calls / Run", null},
            {"☎№ Digits", null},
            {"", null}
        },
        new String [] {
            "", ""
        }
    ) {
        Class[] types = new Class [] {
            java.lang.String.class, java.lang.String.class
        };
        boolean[] canEdit = new boolean [] {
            true, false
        };

        public Class getColumnClass(int columnIndex) {
            return types [columnIndex];
        }

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return canEdit [columnIndex];
        }
    });
    vergunningDetailsTable.setToolTipText("Overall Status of all Phones in Pool");
    vergunningDetailsTable.setAutoCreateRowSorter(true);
    vergunningDetailsTable.setAutoscrolls(false);
    vergunningDetailsTable.setDoubleBuffered(true);
    vergunningDetailsTable.setEditingColumn(0);
    vergunningDetailsTable.setEditingRow(0);
    vergunningDetailsTable.setFocusable(false);
    vergunningDetailsTable.setMaximumSize(new java.awt.Dimension(75, 190));
    vergunningDetailsTable.setMinimumSize(new java.awt.Dimension(75, 190));
    vergunningDetailsTable.setName("name"); // NOI18N
    vergunningDetailsTable.setPreferredSize(new java.awt.Dimension(75, 190));
    vergunningDetailsTable.setRowSelectionAllowed(false);
    vergunningDetailsTable.setSelectionBackground(new java.awt.Color(51, 102, 255));
    vergunningDetailsTable.setShowGrid(false);
    vergunningDetailsTable.setSize(new java.awt.Dimension(75, 190));
    licenseDetailsScrollPane.setViewportView(vergunningDetailsTable);
    vergunningDetailsTable.getColumnModel().getColumn(0).setResizable(false);
    vergunningDetailsTable.getColumnModel().getColumn(0).setPreferredWidth(50);
    vergunningDetailsTable.getColumnModel().getColumn(1).setResizable(false);
    vergunningDetailsTable.getColumnModel().getColumn(1).setPreferredWidth(50);

    org.jdesktop.layout.GroupLayout licenseDetailsPanelLayout = new org.jdesktop.layout.GroupLayout(licenseDetailsPanel);
    licenseDetailsPanel.setLayout(licenseDetailsPanelLayout);
    licenseDetailsPanelLayout.setHorizontalGroup(
        licenseDetailsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(licenseDetailsScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
    );
    licenseDetailsPanelLayout.setVerticalGroup(
        licenseDetailsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(licenseDetailsPanelLayout.createSequentialGroup()
            .add(licenseDetailsScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 156, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addContainerGap(18, Short.MAX_VALUE))
    );

    applyVergunningButton.setFont(new java.awt.Font("STHeiti", 0, 14));
    applyVergunningButton.setText("Apply License");
    applyVergunningButton.setToolTipText("Apply LicenseCode");
    applyVergunningButton.setEnabled(false);
    applyVergunningButton.setFocusPainted(false);
    applyVergunningButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            applyVergunningButtonActionPerformed(evt);
        }
    });

    requestVergunningButton.setFont(new java.awt.Font("STHeiti", 0, 14));
    requestVergunningButton.setText("Request License");
    requestVergunningButton.setToolTipText("Request a License");
    requestVergunningButton.setEnabled(false);
    requestVergunningButton.setFocusPainted(false);
    requestVergunningButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            requestVergunningButtonActionPerformed(evt);
        }
    });

    org.jdesktop.layout.GroupLayout licensePanelLayout = new org.jdesktop.layout.GroupLayout(licensePanel);
    licensePanel.setLayout(licensePanelLayout);
    licensePanelLayout.setHorizontalGroup(
        licensePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(licensePanelLayout.createSequentialGroup()
            .addContainerGap()
            .add(licensePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(licensePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(licensePanelLayout.createSequentialGroup()
                        .add(licenseTypePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 128, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(licenseDatePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(licensePeriodPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 95, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(6, 6, 6))
                    .add(licensePanelLayout.createSequentialGroup()
                        .add(activationCodePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))
                .add(licensePanelLayout.createSequentialGroup()
                    .add(licenseCodePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))
            .add(licensePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(requestVergunningButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(applyVergunningButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE)
                .add(licenseDetailsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 153, Short.MAX_VALUE))
            .add(56, 56, 56))
    );

    licensePanelLayout.linkSize(new java.awt.Component[] {activationCodePanel, licenseCodePanel}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

    licensePanelLayout.setVerticalGroup(
        licensePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(licensePanelLayout.createSequentialGroup()
            .add(licensePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(licenseDatePanel, 0, 197, Short.MAX_VALUE)
                .add(licenseTypePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 197, Short.MAX_VALUE)
                .add(licensePeriodPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 197, Short.MAX_VALUE)
                .add(licenseDetailsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 197, Short.MAX_VALUE))
            .add(5, 5, 5)
            .add(licensePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(requestVergunningButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 61, Short.MAX_VALUE)
                .add(activationCodePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(licensePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                .add(applyVergunningButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(licenseCodePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, Short.MAX_VALUE))
            .addContainerGap())
    );

    licensePanelLayout.linkSize(new java.awt.Component[] {licenseDatePanel, licenseTypePanel}, org.jdesktop.layout.GroupLayout.VERTICAL);

    licensePanelLayout.linkSize(new java.awt.Component[] {activationCodePanel, licenseCodePanel}, org.jdesktop.layout.GroupLayout.VERTICAL);

    tabPane.addTab("License", licensePanel);

    logPanel.setToolTipText("Clicking clears Display");
    logPanel.setFocusTraversalKeysEnabled(false);
    logPanel.setFont(new java.awt.Font("STHeiti", 0, 12));
    logPanel.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyPressed(java.awt.event.KeyEvent evt) {
            logPanelKeyPressed(evt);
        }
    });

    logScrollPane.setForeground(new java.awt.Color(255, 255, 255));
    logScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    logScrollPane.setDoubleBuffered(true);
    logScrollPane.setFont(new java.awt.Font("STHeiti", 0, 13));

    textLogArea.setBackground(new java.awt.Color(51, 51, 51));
    textLogArea.setColumns(20);
    textLogArea.setEditable(false);
    textLogArea.setFont(new java.awt.Font("Courier", 0, 8));
    textLogArea.setForeground(new java.awt.Color(255, 255, 255));
    textLogArea.setLineWrap(true);
    textLogArea.setRows(5);
    textLogArea.setToolTipText("Doubleclick to Clear");
    textLogArea.setDoubleBuffered(true);
    textLogArea.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            textLogAreaMouseClicked(evt);
        }
    });
    logScrollPane.setViewportView(textLogArea);

    org.jdesktop.layout.GroupLayout logPanelLayout = new org.jdesktop.layout.GroupLayout(logPanel);
    logPanel.setLayout(logPanelLayout);
    logPanelLayout.setHorizontalGroup(
        logPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(logScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 662, Short.MAX_VALUE)
    );
    logPanelLayout.setVerticalGroup(
        logPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(org.jdesktop.layout.GroupLayout.TRAILING, logScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 335, Short.MAX_VALUE)
    );

    tabPane.addTab("Log", logPanel);

    aboutPanel.setBackground(new java.awt.Color(51, 51, 51));
    aboutPanel.setToolTipText("Background Information on this Product");
    aboutPanel.setFocusTraversalKeysEnabled(false);
    aboutPanel.setFont(new java.awt.Font("STHeiti", 0, 12));
    aboutPanel.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyPressed(java.awt.event.KeyEvent evt) {
            aboutPanelKeyPressed(evt);
        }
    });

    brandLabel.setFont(new java.awt.Font("STHeiti", 1, 24));
    brandLabel.setForeground(new java.awt.Color(51, 51, 51));
    brandLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

    brandDescriptionLabel.setBackground(new java.awt.Color(51, 51, 51));
    brandDescriptionLabel.setColumns(20);
    brandDescriptionLabel.setEditable(false);
    brandDescriptionLabel.setFont(new java.awt.Font("STHeiti", 0, 14));
    brandDescriptionLabel.setForeground(new java.awt.Color(51, 51, 51));
    brandDescriptionLabel.setLineWrap(true);
    brandDescriptionLabel.setRows(5);
    brandDescriptionLabel.setWrapStyleWord(true);
    brandDescriptionLabel.setAutoscrolls(false);
    brandDescriptionLabel.setBorder(null);
    brandDescriptionLabel.setDragEnabled(false);
    brandDescriptionLabel.setFocusable(false);

    productLabel.setFont(new java.awt.Font("STHeiti", 1, 24));
    productLabel.setForeground(new java.awt.Color(51, 51, 51));
    productLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

    productDescriptionLabel.setBackground(new java.awt.Color(51, 51, 51));
    productDescriptionLabel.setColumns(20);
    productDescriptionLabel.setEditable(false);
    productDescriptionLabel.setFont(new java.awt.Font("STHeiti", 0, 14));
    productDescriptionLabel.setForeground(new java.awt.Color(51, 51, 51));
    productDescriptionLabel.setLineWrap(true);
    productDescriptionLabel.setRows(5);
    productDescriptionLabel.setWrapStyleWord(true);
    productDescriptionLabel.setAutoscrolls(false);
    productDescriptionLabel.setBorder(null);
    productDescriptionLabel.setDragEnabled(false);
    productDescriptionLabel.setFocusable(false);

    copyrightLabel.setBackground(new java.awt.Color(51, 51, 51));
    copyrightLabel.setColumns(20);
    copyrightLabel.setEditable(false);
    copyrightLabel.setFont(new java.awt.Font("STHeiti", 0, 14));
    copyrightLabel.setForeground(new java.awt.Color(51, 51, 51));
    copyrightLabel.setLineWrap(true);
    copyrightLabel.setRows(5);
    copyrightLabel.setWrapStyleWord(true);
    copyrightLabel.setAutoscrolls(false);
    copyrightLabel.setBorder(null);
    copyrightLabel.setDragEnabled(false);
    copyrightLabel.setFocusable(false);

    org.jdesktop.layout.GroupLayout aboutPanelLayout = new org.jdesktop.layout.GroupLayout(aboutPanel);
    aboutPanel.setLayout(aboutPanelLayout);
    aboutPanelLayout.setHorizontalGroup(
        aboutPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(org.jdesktop.layout.GroupLayout.TRAILING, aboutPanelLayout.createSequentialGroup()
            .addContainerGap()
            .add(aboutPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                .add(org.jdesktop.layout.GroupLayout.LEADING, productDescriptionLabel)
                .add(org.jdesktop.layout.GroupLayout.LEADING, brandLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 650, Short.MAX_VALUE)
                .add(org.jdesktop.layout.GroupLayout.LEADING, brandDescriptionLabel)
                .add(org.jdesktop.layout.GroupLayout.LEADING, productLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 650, Short.MAX_VALUE))
            .add(22, 22, 22))
        .add(aboutPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(aboutPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(copyrightLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 650, Short.MAX_VALUE)
                .addContainerGap()))
    );

    aboutPanelLayout.linkSize(new java.awt.Component[] {brandDescriptionLabel, brandLabel, copyrightLabel, productDescriptionLabel, productLabel}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

    aboutPanelLayout.setVerticalGroup(
        aboutPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(aboutPanelLayout.createSequentialGroup()
            .addContainerGap()
            .add(brandLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 27, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
            .add(brandDescriptionLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
            .add(productLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 34, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
            .add(productDescriptionLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addContainerGap(92, Short.MAX_VALUE))
        .add(aboutPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(aboutPanelLayout.createSequentialGroup()
                .add(277, 277, 277)
                .add(copyrightLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 36, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(22, Short.MAX_VALUE)))
    );

    tabPane.addTab("About", aboutPanel);

    displayPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
    displayPanel.setToolTipText("");
    displayPanel.setFont(new java.awt.Font("STHeiti", 0, 13));
    displayPanel.setMaximumSize(new java.awt.Dimension(695, 105));
    displayPanel.setMinimumSize(new java.awt.Dimension(695, 105));
    displayPanel.setPreferredSize(new java.awt.Dimension(695, 105));
    displayPanel.setSize(new java.awt.Dimension(695, 105));

    captionTable.setBackground(new java.awt.Color(240, 240, 240));
    captionTable.setFont(new java.awt.Font("STHeiti", 0, 10));
    captionTable.setForeground(new java.awt.Color(102, 102, 102));
    captionTable.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][] {
            {"", null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null, null}
        },
        new String [] {
            "", "", "", "", "", "", "", "", "", "", ""
        }
    ) {
        Class[] types = new Class [] {
            java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
        };
        boolean[] canEdit = new boolean [] {
            false, false, false, false, false, false, false, false, false, false, false
        };

        public Class getColumnClass(int columnIndex) {
            return types [columnIndex];
        }

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return canEdit [columnIndex];
        }
    });
    captionTable.setToolTipText("CallCenter Statistics");
    captionTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
    captionTable.setAutoscrolls(false);
    captionTable.setColumnSelectionAllowed(true);
    captionTable.setDoubleBuffered(true);
    captionTable.setEnabled(false);
    captionTable.setFocusable(false);
    captionTable.setIntercellSpacing(new java.awt.Dimension(0, 0));
    captionTable.setMaximumSize(new java.awt.Dimension(660, 45));
    captionTable.setMinimumSize(new java.awt.Dimension(660, 45));
    captionTable.setPreferredSize(new java.awt.Dimension(660, 45));
    captionTable.setRowHeight(15);
    captionTable.setRowSelectionAllowed(false);
    captionTable.setShowGrid(false);
    captionTable.setSize(new java.awt.Dimension(660, 45));
    captionTable.setUpdateSelectionOnSort(false);
    captionTable.setVerifyInputWhenFocusTarget(false);

    statusBar.setBackground(new java.awt.Color(230, 230, 230));
    statusBar.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    statusBar.setEditable(false);
    statusBar.setFont(new java.awt.Font("Synchro LET", 2, 14));
    statusBar.setForeground(new java.awt.Color(102, 102, 102));
    statusBar.setToolTipText("Status Bar");
    statusBar.setMaximumSize(new java.awt.Dimension(500, 25));
    statusBar.setMinimumSize(new java.awt.Dimension(500, 25));
    statusBar.setPreferredSize(new java.awt.Dimension(500, 25));
    statusBar.setSize(new java.awt.Dimension(500, 25));

    org.jdesktop.layout.GroupLayout displayPanelLayout = new org.jdesktop.layout.GroupLayout(displayPanel);
    displayPanel.setLayout(displayPanelLayout);
    displayPanelLayout.setHorizontalGroup(
        displayPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(captionTable, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 683, Short.MAX_VALUE)
        .add(statusBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 683, Short.MAX_VALUE)
    );
    displayPanelLayout.setVerticalGroup(
        displayPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(displayPanelLayout.createSequentialGroup()
            .add(captionTable, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(statusBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
            .addContainerGap())
    );

    captionTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    captionTable.getColumnModel().getColumn(0).setResizable(false);
    captionTable.getColumnModel().getColumn(1).setResizable(false);
    captionTable.getColumnModel().getColumn(2).setResizable(false);
    captionTable.getColumnModel().getColumn(3).setResizable(false);
    captionTable.getColumnModel().getColumn(4).setResizable(false);
    captionTable.getColumnModel().getColumn(5).setResizable(false);
    captionTable.getColumnModel().getColumn(6).setResizable(false);
    captionTable.getColumnModel().getColumn(7).setResizable(false);
    captionTable.getColumnModel().getColumn(8).setResizable(false);
    captionTable.getColumnModel().getColumn(9).setResizable(false);
    captionTable.getColumnModel().getColumn(10).setResizable(false);

    controlButtonPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("STHeiti", 0, 13), new java.awt.Color(255, 255, 255))); // NOI18N
    controlButtonPanel.setMaximumSize(new java.awt.Dimension(695, 63));
    controlButtonPanel.setMinimumSize(new java.awt.Dimension(695, 63));
    controlButtonPanel.setPreferredSize(new java.awt.Dimension(695, 63));
    controlButtonPanel.setSize(new java.awt.Dimension(695, 63));

    callButton.setFont(new java.awt.Font("STHeiti", 0, 8));
    callButton.setForeground(new java.awt.Color(51, 204, 0));
    callButton.setText("☎");
    callButton.setToolTipText("Call Button");
    callButton.setEnabled(false);
    callButton.setFocusPainted(false);
    callButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            callButtonActionPerformed(evt);
        }
    });

    serviceLoopProgressBar.setFont(new java.awt.Font("STHeiti", 0, 8));
    serviceLoopProgressBar.setToolTipText("Service Progress");
    serviceLoopProgressBar.setBorderPainted(false);
    serviceLoopProgressBar.setEnabled(false);
    serviceLoopProgressBar.setFocusTraversalKeysEnabled(false);
    serviceLoopProgressBar.setFocusable(false);
    serviceLoopProgressBar.setName("progressBar"); // NOI18N
    serviceLoopProgressBar.setOpaque(true);
    serviceLoopProgressBar.setStringPainted(true);

    autoSpeedToggleButton.setFont(new java.awt.Font("STHeiti", 0, 8));
    autoSpeedToggleButton.setSelected(true);
    autoSpeedToggleButton.setText("☯");
    autoSpeedToggleButton.setToolTipText("Automatic Speed");
    autoSpeedToggleButton.setEnabled(false);
    autoSpeedToggleButton.setFocusPainted(false);
    autoSpeedToggleButton.setFocusable(false);
    autoSpeedToggleButton.setMaximumSize(new java.awt.Dimension(55, 29));
    autoSpeedToggleButton.setMinimumSize(new java.awt.Dimension(55, 29));
    autoSpeedToggleButton.setPreferredSize(new java.awt.Dimension(55, 29));
    autoSpeedToggleButton.setRequestFocusEnabled(false);
    autoSpeedToggleButton.setSize(new java.awt.Dimension(55, 29));
    autoSpeedToggleButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            autoSpeedToggleButtonActionPerformed(evt);
        }
    });

    powerToggleButton.setFont(new java.awt.Font("STHeiti", 0, 8));
    powerToggleButton.setText("⎋");
    powerToggleButton.setToolTipText("Power Button");
    powerToggleButton.setEnabled(false);
    powerToggleButton.setMaximumSize(new java.awt.Dimension(50, 29));
    powerToggleButton.setMinimumSize(new java.awt.Dimension(50, 29));
    powerToggleButton.setPreferredSize(new java.awt.Dimension(50, 29));
    powerToggleButton.setSize(new java.awt.Dimension(55, 29));
    powerToggleButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            powerToggleButtonActionPerformed(evt);
        }
    });

    runCampaignToggleButton.setFont(new java.awt.Font("STHeiti", 0, 8));
    runCampaignToggleButton.setText("►");
    runCampaignToggleButton.setToolTipText("Run / Pause Campaign");
    runCampaignToggleButton.setEnabled(false);
    runCampaignToggleButton.setFocusPainted(false);
    runCampaignToggleButton.setFocusable(false);
    runCampaignToggleButton.setMaximumSize(new java.awt.Dimension(55, 29));
    runCampaignToggleButton.setMinimumSize(new java.awt.Dimension(55, 29));
    runCampaignToggleButton.setPreferredSize(new java.awt.Dimension(55, 29));
    runCampaignToggleButton.setRequestFocusEnabled(false);
    runCampaignToggleButton.setSize(new java.awt.Dimension(55, 29));
    runCampaignToggleButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            runCampaignToggleButtonActionPerformed(evt);
        }
    });

    endButton.setFont(new java.awt.Font("STHeiti", 0, 8));
    endButton.setForeground(new java.awt.Color(255, 0, 0));
    endButton.setText("☎");
    endButton.setToolTipText("End Button");
    endButton.setEnabled(false);
    endButton.setFocusPainted(false);
    endButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            endButtonActionPerformed(evt);
        }
    });

    phoneButton.setFont(new java.awt.Font("STHeiti", 0, 8));
    phoneButton.setForeground(new java.awt.Color(51, 51, 255));
    phoneButton.setText("☎");
    phoneButton.setToolTipText("SoftPhone");
    phoneButton.setEnabled(false);
    phoneButton.setFocusPainted(false);
    phoneButton.setMaximumSize(new java.awt.Dimension(100, 29));
    phoneButton.setMinimumSize(new java.awt.Dimension(100, 29));
    phoneButton.setPreferredSize(new java.awt.Dimension(100, 29));
    phoneButton.setSize(new java.awt.Dimension(35, 30));
    phoneButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            phoneButtonActionPerformed(evt);
        }
    });

    stopCampaignButton.setFont(new java.awt.Font("STHeiti", 0, 5));
    stopCampaignButton.setText("█");
    stopCampaignButton.setToolTipText("Stop Campaign");
    stopCampaignButton.setEnabled(false);
    stopCampaignButton.setFocusPainted(false);
    stopCampaignButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            stopCampaignButtonActionPerformed(evt);
        }
    });

    muteAudioToggleButton.setFont(new java.awt.Font("STHeiti", 0, 8));
    muteAudioToggleButton.setText("Ⓜ");
    muteAudioToggleButton.setToolTipText("Mute Audio");
    muteAudioToggleButton.setAlignmentY(0.0F);
    muteAudioToggleButton.setEnabled(false);
    muteAudioToggleButton.setFocusable(false);
    muteAudioToggleButton.setMaximumSize(new java.awt.Dimension(100, 30));
    muteAudioToggleButton.setMinimumSize(new java.awt.Dimension(100, 30));
    muteAudioToggleButton.setPreferredSize(new java.awt.Dimension(100, 30));
    muteAudioToggleButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            muteAudioToggleButtonActionPerformed(evt);
        }
    });

    campaignProgressBar.setFont(new java.awt.Font("STHeiti", 0, 8));
    campaignProgressBar.setMaximum(1000);
    campaignProgressBar.setToolTipText("Campaign Progress");
    campaignProgressBar.setBorderPainted(false);
    campaignProgressBar.setEnabled(false);
    campaignProgressBar.setFocusTraversalKeysEnabled(false);
    campaignProgressBar.setFocusable(false);
    campaignProgressBar.setName("campaignProgressBar"); // NOI18N
    campaignProgressBar.setOpaque(true);
    campaignProgressBar.setStringPainted(true);

    humanResponseSimulatorToggleButton.setFont(new java.awt.Font("STHeiti", 0, 8));
    humanResponseSimulatorToggleButton.setText("Ⓢ");
    humanResponseSimulatorToggleButton.setToolTipText("Simulate Human Behavior (Inbound)");
    humanResponseSimulatorToggleButton.setAlignmentY(0.0F);
    humanResponseSimulatorToggleButton.setEnabled(false);
    humanResponseSimulatorToggleButton.setFocusable(false);
    humanResponseSimulatorToggleButton.setMaximumSize(new java.awt.Dimension(100, 30));
    humanResponseSimulatorToggleButton.setMinimumSize(new java.awt.Dimension(100, 30));
    humanResponseSimulatorToggleButton.setPreferredSize(new java.awt.Dimension(100, 30));
    humanResponseSimulatorToggleButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            humanResponseSimulatorToggleButtonActionPerformed(evt);
        }
    });

    registerToggleButton.setFont(new java.awt.Font("STHeiti", 0, 8));
    registerToggleButton.setText("Ⓡ");
    registerToggleButton.setToolTipText("Register to PBX (Switchboard)");
    registerToggleButton.setAlignmentY(0.0F);
    registerToggleButton.setEnabled(false);
    registerToggleButton.setFocusable(false);
    registerToggleButton.setMaximumSize(new java.awt.Dimension(100, 30));
    registerToggleButton.setMinimumSize(new java.awt.Dimension(100, 30));
    registerToggleButton.setPreferredSize(new java.awt.Dimension(100, 30));
    registerToggleButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            registerToggleButtonActionPerformed(evt);
        }
    });

    campaignComboBox.setFont(new java.awt.Font("STHeiti", 0, 8));
    campaignComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "►►" }));
    campaignComboBox.setToolTipText("Choose Campaign");
    campaignComboBox.setEnabled(false);
    campaignComboBox.setFocusable(false);
    campaignComboBox.setMinimumSize(new java.awt.Dimension(70, 35));
    campaignComboBox.setPreferredSize(new java.awt.Dimension(70, 35));
    campaignComboBox.setSize(new java.awt.Dimension(70, 35));
    campaignComboBox.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            campaignComboBoxActionPerformed(evt);
        }
    });

    debugToggleButton.setFont(new java.awt.Font("STHeiti", 0, 8));
    debugToggleButton.setText("Ⓓ");
    debugToggleButton.setToolTipText("Debugging");
    debugToggleButton.setAlignmentY(0.0F);
    debugToggleButton.setEnabled(false);
    debugToggleButton.setFocusable(false);
    debugToggleButton.setMaximumSize(new java.awt.Dimension(100, 30));
    debugToggleButton.setMinimumSize(new java.awt.Dimension(100, 30));
    debugToggleButton.setPreferredSize(new java.awt.Dimension(100, 30));
    debugToggleButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            debugToggleButtonActionPerformed(evt);
        }
    });

    resizeWindowButton.setFont(new java.awt.Font("STHeiti", 0, 8));
    resizeWindowButton.setText("⊼");
    resizeWindowButton.setToolTipText("Hide Controls");
    resizeWindowButton.setFocusPainted(false);
    resizeWindowButton.setMaximumSize(new java.awt.Dimension(100, 29));
    resizeWindowButton.setMinimumSize(new java.awt.Dimension(100, 29));
    resizeWindowButton.setPreferredSize(new java.awt.Dimension(100, 29));
    resizeWindowButton.setSize(new java.awt.Dimension(35, 30));
    resizeWindowButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            resizeWindowButtonActionPerformed(evt);
        }
    });

    org.jdesktop.layout.GroupLayout controlButtonPanelLayout = new org.jdesktop.layout.GroupLayout(controlButtonPanel);
    controlButtonPanel.setLayout(controlButtonPanelLayout);
    controlButtonPanelLayout.setHorizontalGroup(
        controlButtonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(controlButtonPanelLayout.createSequentialGroup()
            .addContainerGap()
            .add(controlButtonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                .add(controlButtonPanelLayout.createSequentialGroup()
                    .add(registerToggleButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 38, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(humanResponseSimulatorToggleButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 30, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(muteAudioToggleButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 32, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(debugToggleButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 32, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(serviceLoopProgressBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .add(9, 9, 9)
            .add(controlButtonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                .add(controlButtonPanelLayout.createSequentialGroup()
                    .add(phoneButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 35, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(powerToggleButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(resizeWindowButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 46, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(campaignComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 70, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(runCampaignToggleButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 38, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(stopCampaignButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 35, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(callButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 34, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(endButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 32, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(autoSpeedToggleButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 36, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(7, 7, 7))
                .add(controlButtonPanelLayout.createSequentialGroup()
                    .add(campaignProgressBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 492, Short.MAX_VALUE)
                    .addContainerGap())))
    );

    controlButtonPanelLayout.linkSize(new java.awt.Component[] {debugToggleButton, humanResponseSimulatorToggleButton, muteAudioToggleButton, registerToggleButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

    controlButtonPanelLayout.linkSize(new java.awt.Component[] {autoSpeedToggleButton, callButton, endButton, phoneButton, runCampaignToggleButton, stopCampaignButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

    controlButtonPanelLayout.setVerticalGroup(
        controlButtonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(controlButtonPanelLayout.createSequentialGroup()
            .addContainerGap()
            .add(controlButtonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(controlButtonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(debugToggleButton, 0, 0, Short.MAX_VALUE)
                    .add(phoneButton, 0, 0, Short.MAX_VALUE)
                    .add(powerToggleButton, 0, 0, Short.MAX_VALUE))
                .add(registerToggleButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(humanResponseSimulatorToggleButton, 0, 0, Short.MAX_VALUE)
                .add(muteAudioToggleButton, 0, 0, Short.MAX_VALUE)
                .add(autoSpeedToggleButton, 0, 0, Short.MAX_VALUE)
                .add(runCampaignToggleButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(stopCampaignButton, 0, 0, Short.MAX_VALUE)
                .add(endButton, 0, 0, Short.MAX_VALUE)
                .add(callButton, 0, 0, Short.MAX_VALUE)
                .add(campaignComboBox, 0, 0, Short.MAX_VALUE)
                .add(resizeWindowButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(controlButtonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(serviceLoopProgressBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 9, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(campaignProgressBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 9, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(9, 9, 9))
    );

    controlButtonPanelLayout.linkSize(new java.awt.Component[] {autoSpeedToggleButton, callButton, campaignComboBox, debugToggleButton, endButton, humanResponseSimulatorToggleButton, muteAudioToggleButton, phoneButton, powerToggleButton, registerToggleButton, resizeWindowButton, runCampaignToggleButton, stopCampaignButton}, org.jdesktop.layout.GroupLayout.VERTICAL);

    controlSliderPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("STHeiti", 0, 10), new java.awt.Color(51, 51, 51))); // NOI18N
    controlSliderPanel.setToolTipText("");
    controlSliderPanel.setFont(new java.awt.Font("STHeiti", 0, 13));
    controlSliderPanel.setMaximumSize(new java.awt.Dimension(695, 220));
    controlSliderPanel.setMinimumSize(new java.awt.Dimension(695, 0));
    controlSliderPanel.setPreferredSize(new java.awt.Dimension(695, 220));
    controlSliderPanel.setSize(new java.awt.Dimension(695, 220));

    buttonPanel.setToolTipText("");
    buttonPanel.setFont(new java.awt.Font("STHeiti", 0, 10));
    buttonPanel.setMaximumSize(new java.awt.Dimension(670, 32767));
    buttonPanel.setMinimumSize(new java.awt.Dimension(670, 0));
    buttonPanel.setPreferredSize(new java.awt.Dimension(670, 252));

    outboundSliderPanel.setToolTipText("");
    outboundSliderPanel.setFont(new java.awt.Font("STHeiti", 0, 13));
    outboundSliderPanel.setOpaque(false);
    outboundSliderPanel.setPreferredSize(new java.awt.Dimension(304, 190));

    vmUsageThresholdLabel.setFont(new java.awt.Font("STHeiti", 0, 8));
    vmUsageThresholdLabel.setText("CPU %");
    registerSpeedValue.setHorizontalAlignment(SwingConstants.CENTER);

    vmUsagePauseValue.setFont(new java.awt.Font("STHeiti", 0, 8));
    vmUsagePauseValue.setText("0");
    vmUsagePauseValue.setHorizontalAlignment(SwingConstants.CENTER);

    vmUsageThresholdSlider.setFont(new java.awt.Font("STHeiti", 0, 5));
    vmUsageThresholdSlider.setMajorTickSpacing(10);
    vmUsageThresholdSlider.setMinorTickSpacing(1);
    vmUsageThresholdSlider.setOrientation(javax.swing.JSlider.VERTICAL);
    vmUsageThresholdSlider.setPaintLabels(true);
    vmUsageThresholdSlider.setPaintTicks(true);
    vmUsageThresholdSlider.setSnapToTicks(true);
    vmUsageThresholdSlider.setToolTipText("VM Usage Threshold (Pause Campaign)");
    vmUsageThresholdSlider.setValue(100);
    vmUsageThresholdSlider.setFocusable(false);
    vmUsageThresholdSlider.setMaximumSize(new java.awt.Dimension(40, 150));
    vmUsageThresholdSlider.setMinimumSize(new java.awt.Dimension(40, 150));
    vmUsageThresholdSlider.setPreferredSize(new java.awt.Dimension(40, 150));
    vmUsageThresholdSlider.setSize(new java.awt.Dimension(40, 150));
    vmUsageThresholdSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent evt) {
            vmUsageThresholdSliderStateChanged(evt);
        }
    });

    memFreeThresholdLabel.setFont(new java.awt.Font("STHeiti", 0, 8));
    memFreeThresholdLabel.setText("MF MB");
    registerSpeedValue.setHorizontalAlignment(SwingConstants.CENTER);

    memFreeThresholdValue.setFont(new java.awt.Font("STHeiti", 0, 8));
    memFreeThresholdValue.setText("0");
    memFreeThresholdValue.setHorizontalAlignment(SwingConstants.CENTER);

    memFreeThresholdSlider.setFont(new java.awt.Font("STHeiti", 0, 5));
    memFreeThresholdSlider.setMajorTickSpacing(10);
    memFreeThresholdSlider.setMinorTickSpacing(1);
    memFreeThresholdSlider.setOrientation(javax.swing.JSlider.VERTICAL);
    memFreeThresholdSlider.setPaintLabels(true);
    memFreeThresholdSlider.setPaintTicks(true);
    memFreeThresholdSlider.setSnapToTicks(true);
    memFreeThresholdSlider.setToolTipText("Memory Free Threshold (Pause Campaign)");
    memFreeThresholdSlider.setValue(5);
    memFreeThresholdSlider.setFocusable(false);
    memFreeThresholdSlider.setMaximumSize(new java.awt.Dimension(40, 150));
    memFreeThresholdSlider.setMinimumSize(new java.awt.Dimension(40, 150));
    memFreeThresholdSlider.setPreferredSize(new java.awt.Dimension(40, 150));
    memFreeThresholdSlider.setSize(new java.awt.Dimension(40, 150));
    memFreeThresholdSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent evt) {
            memFreeThresholdSliderStateChanged(evt);
        }
    });

    heapMemFreeThresholdLabel.setFont(new java.awt.Font("STHeiti", 0, 8));
    heapMemFreeThresholdLabel.setText("HMF MB");
    registerSpeedValue.setHorizontalAlignment(SwingConstants.CENTER);

    heapMemFreeThresholdValue.setFont(new java.awt.Font("STHeiti", 0, 8));
    heapMemFreeThresholdValue.setText("0");
    heapMemFreeThresholdValue.setHorizontalAlignment(SwingConstants.CENTER);

    heapMemFreeThresholdSlider.setFont(new java.awt.Font("STHeiti", 0, 5));
    heapMemFreeThresholdSlider.setMajorTickSpacing(10);
    heapMemFreeThresholdSlider.setMinorTickSpacing(1);
    heapMemFreeThresholdSlider.setOrientation(javax.swing.JSlider.VERTICAL);
    heapMemFreeThresholdSlider.setPaintLabels(true);
    heapMemFreeThresholdSlider.setPaintTicks(true);
    heapMemFreeThresholdSlider.setSnapToTicks(true);
    heapMemFreeThresholdSlider.setToolTipText("HeapMemory Free Threshold (Pause Campaign)");
    heapMemFreeThresholdSlider.setValue(0);
    heapMemFreeThresholdSlider.setFocusable(false);
    heapMemFreeThresholdSlider.setMaximumSize(new java.awt.Dimension(40, 150));
    heapMemFreeThresholdSlider.setMinimumSize(new java.awt.Dimension(40, 150));
    heapMemFreeThresholdSlider.setPreferredSize(new java.awt.Dimension(40, 150));
    heapMemFreeThresholdSlider.setSize(new java.awt.Dimension(40, 150));
    heapMemFreeThresholdSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent evt) {
            heapMemFreeThresholdSliderStateChanged(evt);
        }
    });

    connectingTallyLimitLabel.setFont(new java.awt.Font("STHeiti", 0, 8));
    connectingTallyLimitLabel.setText("CON");
    registerSpeedValue.setHorizontalAlignment(SwingConstants.CENTER);

    connectingTallyLimitValue.setFont(new java.awt.Font("STHeiti", 0, 8));
    connectingTallyLimitValue.setText("0");
    heapMemFreeThresholdValue.setHorizontalAlignment(SwingConstants.CENTER);

    connectingTallyLimitSlider.setFont(new java.awt.Font("STHeiti", 0, 5));
    connectingTallyLimitSlider.setMajorTickSpacing(10);
    connectingTallyLimitSlider.setMinorTickSpacing(1);
    connectingTallyLimitSlider.setOrientation(javax.swing.JSlider.VERTICAL);
    connectingTallyLimitSlider.setPaintLabels(true);
    connectingTallyLimitSlider.setPaintTicks(true);
    connectingTallyLimitSlider.setSnapToTicks(true);
    connectingTallyLimitSlider.setToolTipText("Concurrent Connecting States Maximum (Pause Campaign)");
    connectingTallyLimitSlider.setValue(10);
    connectingTallyLimitSlider.setFocusable(false);
    connectingTallyLimitSlider.setMaximumSize(new java.awt.Dimension(40, 150));
    connectingTallyLimitSlider.setMinimumSize(new java.awt.Dimension(40, 150));
    connectingTallyLimitSlider.setName(""); // NOI18N
    connectingTallyLimitSlider.setPreferredSize(new java.awt.Dimension(40, 150));
    connectingTallyLimitSlider.setSize(new java.awt.Dimension(40, 150));
    connectingTallyLimitSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent evt) {
            connectingTallyLimitSliderStateChanged(evt);
        }
    });

    callingTallyLimitLabel.setFont(new java.awt.Font("STHeiti", 0, 8));
    callingTallyLimitLabel.setText("CALL");
    registerSpeedValue.setHorizontalAlignment(SwingConstants.CENTER);

    callingTallyLimitValue.setFont(new java.awt.Font("STHeiti", 0, 8));
    callingTallyLimitValue.setText("0");
    callingTallyLimitValue.setHorizontalAlignment(SwingConstants.CENTER);

    callingTallyLimitSlider.setFont(new java.awt.Font("STHeiti", 0, 5));
    callingTallyLimitSlider.setMajorTickSpacing(10);
    callingTallyLimitSlider.setMaximum(200);
    callingTallyLimitSlider.setMinorTickSpacing(5);
    callingTallyLimitSlider.setOrientation(javax.swing.JSlider.VERTICAL);
    callingTallyLimitSlider.setPaintLabels(true);
    callingTallyLimitSlider.setPaintTicks(true);
    callingTallyLimitSlider.setSnapToTicks(true);
    callingTallyLimitSlider.setToolTipText("Concurrent Calls Maximum (Pause Campaign)");
    callingTallyLimitSlider.setValue(100);
    callingTallyLimitSlider.setExtent(20);
    callingTallyLimitSlider.setFocusable(false);
    callingTallyLimitSlider.setMaximumSize(new java.awt.Dimension(40, 150));
    callingTallyLimitSlider.setMinimumSize(new java.awt.Dimension(40, 150));
    callingTallyLimitSlider.setPreferredSize(new java.awt.Dimension(40, 150));
    callingTallyLimitSlider.setSize(new java.awt.Dimension(40, 150));
    callingTallyLimitSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent evt) {
            callingTallyLimitSliderStateChanged(evt);
        }
    });

    establishedTallyLimitLabel.setFont(new java.awt.Font("STHeiti", 0, 8));
    establishedTallyLimitLabel.setText("EST");
    registerSpeedValue.setHorizontalAlignment(SwingConstants.CENTER);

    establishedTallyLimitValue.setFont(new java.awt.Font("STHeiti", 0, 8));
    establishedTallyLimitValue.setText("0");
    establishedTallyLimitValue.setHorizontalAlignment(SwingConstants.CENTER);

    establishedTallyLimitSlider.setFont(new java.awt.Font("STHeiti", 0, 5));
    establishedTallyLimitSlider.setMajorTickSpacing(10);
    establishedTallyLimitSlider.setMaximum(200);
    establishedTallyLimitSlider.setMinorTickSpacing(5);
    establishedTallyLimitSlider.setOrientation(javax.swing.JSlider.VERTICAL);
    establishedTallyLimitSlider.setPaintLabels(true);
    establishedTallyLimitSlider.setPaintTicks(true);
    establishedTallyLimitSlider.setSnapToTicks(true);
    establishedTallyLimitSlider.setToolTipText("Concurrent Established Calls Maximum (Pause Campaign)");
    establishedTallyLimitSlider.setFocusable(false);
    establishedTallyLimitSlider.setMaximumSize(new java.awt.Dimension(40, 150));
    establishedTallyLimitSlider.setMinimumSize(new java.awt.Dimension(40, 150));
    establishedTallyLimitSlider.setPreferredSize(new java.awt.Dimension(40, 150));
    establishedTallyLimitSlider.setSize(new java.awt.Dimension(40, 150));
    establishedTallyLimitSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent evt) {
            establishedTallyLimitSliderStateChanged(evt);
        }
    });

    callSpeedLabel.setFont(new java.awt.Font("STHeiti", 0, 8));
    callSpeedLabel.setText("Speed");
    callSpeedValue.setHorizontalAlignment(SwingConstants.CENTER);

    callSpeedValue.setFont(new java.awt.Font("STHeiti", 0, 8));
    callSpeedValue.setText("0");
    callSpeedValue.setHorizontalAlignment(SwingConstants.CENTER);

    callSpeedSlider.setFont(new java.awt.Font("STHeiti", 0, 5));
    callSpeedSlider.setMajorTickSpacing(1000);
    callSpeedSlider.setMaximum(10000);
    callSpeedSlider.setMinimum(350);
    callSpeedSlider.setMinorTickSpacing(1);
    callSpeedSlider.setOrientation(javax.swing.JSlider.VERTICAL);
    callSpeedSlider.setPaintLabels(true);
    callSpeedSlider.setPaintTicks(true);
    callSpeedSlider.setSnapToTicks(true);
    callSpeedSlider.setToolTipText("Automated Outbound Call Rate (mSec)");
    callSpeedSlider.setFocusable(false);
    callSpeedSlider.setInverted(true);
    callSpeedSlider.setMaximumSize(new java.awt.Dimension(40, 150));
    callSpeedSlider.setMinimumSize(new java.awt.Dimension(40, 150));
    callSpeedSlider.setPreferredSize(new java.awt.Dimension(40, 150));
    callSpeedSlider.setSize(new java.awt.Dimension(40, 150));
    callSpeedSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent evt) {
            callSpeedSliderStateChanged(evt);
        }
    });

    org.jdesktop.layout.GroupLayout outboundSliderPanelLayout = new org.jdesktop.layout.GroupLayout(outboundSliderPanel);
    outboundSliderPanel.setLayout(outboundSliderPanelLayout);
    outboundSliderPanelLayout.setHorizontalGroup(
        outboundSliderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(outboundSliderPanelLayout.createSequentialGroup()
            .addContainerGap()
            .add(outboundSliderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(outboundSliderPanelLayout.createSequentialGroup()
                    .add(vmUsageThresholdSlider, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 40, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(memFreeThresholdSlider, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 40, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(heapMemFreeThresholdSlider, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 40, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(connectingTallyLimitSlider, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 40, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(callingTallyLimitSlider, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 40, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(establishedTallyLimitSlider, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 40, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(callSpeedSlider, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 40, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
                .add(outboundSliderPanelLayout.createSequentialGroup()
                    .add(outboundSliderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                        .add(outboundSliderPanelLayout.createSequentialGroup()
                            .add(vmUsageThresholdLabel)
                            .add(14, 14, 14))
                        .add(vmUsagePauseValue, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 30, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(10, 10, 10)
                    .add(outboundSliderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                        .add(outboundSliderPanelLayout.createSequentialGroup()
                            .add(memFreeThresholdValue, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(16, 16, 16))
                        .add(outboundSliderPanelLayout.createSequentialGroup()
                            .add(memFreeThresholdLabel)
                            .add(11, 11, 11)))
                    .add(outboundSliderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                        .add(heapMemFreeThresholdLabel)
                        .add(heapMemFreeThresholdValue, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                    .add(outboundSliderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                        .add(outboundSliderPanelLayout.createSequentialGroup()
                            .add(connectingTallyLimitLabel)
                            .add(7, 7, 7))
                        .add(connectingTallyLimitValue, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(18, 18, 18)
                    .add(outboundSliderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, callingTallyLimitLabel)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, callingTallyLimitValue, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(18, 18, 18)
                    .add(outboundSliderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(establishedTallyLimitLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 29, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(establishedTallyLimitValue, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(outboundSliderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                        .add(callSpeedValue, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(callSpeedLabel))
                    .add(28, 28, 28))))
    );

    outboundSliderPanelLayout.linkSize(new java.awt.Component[] {connectingTallyLimitSlider, heapMemFreeThresholdSlider}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

    outboundSliderPanelLayout.setVerticalGroup(
        outboundSliderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(outboundSliderPanelLayout.createSequentialGroup()
            .addContainerGap()
            .add(outboundSliderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                .add(outboundSliderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(vmUsageThresholdLabel)
                    .add(outboundSliderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                        .add(outboundSliderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(outboundSliderPanelLayout.createSequentialGroup()
                                .add(memFreeThresholdLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(memFreeThresholdValue))
                            .add(outboundSliderPanelLayout.createSequentialGroup()
                                .add(15, 15, 15)
                                .add(heapMemFreeThresholdValue))
                            .add(heapMemFreeThresholdLabel)
                            .add(outboundSliderPanelLayout.createSequentialGroup()
                                .add(connectingTallyLimitLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(connectingTallyLimitValue)))
                        .add(vmUsagePauseValue, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 9, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(outboundSliderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(outboundSliderPanelLayout.createSequentialGroup()
                        .add(establishedTallyLimitLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(establishedTallyLimitValue))
                    .add(outboundSliderPanelLayout.createSequentialGroup()
                        .add(callingTallyLimitLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(callingTallyLimitValue)))
                .add(outboundSliderPanelLayout.createSequentialGroup()
                    .add(callSpeedLabel)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(callSpeedValue)))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(outboundSliderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(establishedTallyLimitSlider, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 150, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(callingTallyLimitSlider, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 150, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(connectingTallyLimitSlider, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 150, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(heapMemFreeThresholdSlider, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                .add(memFreeThresholdSlider, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                .add(vmUsageThresholdSlider, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                .add(callSpeedSlider, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 150, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(186, 186, 186))
    );

    performanceMeterPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Calls per Hour", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("STHeiti", 0, 10), new java.awt.Color(102, 102, 102))); // NOI18N
    performanceMeterPanel.setToolTipText("Call Speed & VM Workload");
    performanceMeterPanel.setFocusable(false);
    performanceMeterPanel.setFont(new java.awt.Font("STHeiti", 0, 13));
    performanceMeterPanel.setMaximumSize(new java.awt.Dimension(190, 190));
    performanceMeterPanel.setMinimumSize(new java.awt.Dimension(190, 190));
    performanceMeterPanel.setPreferredSize(new java.awt.Dimension(190, 190));
    performanceMeterPanel.setSize(new java.awt.Dimension(190, 190));

    org.jdesktop.layout.GroupLayout performanceMeterPanelLayout = new org.jdesktop.layout.GroupLayout(performanceMeterPanel);
    performanceMeterPanel.setLayout(performanceMeterPanelLayout);
    performanceMeterPanelLayout.setHorizontalGroup(
        performanceMeterPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(0, 176, Short.MAX_VALUE)
    );
    performanceMeterPanelLayout.setVerticalGroup(
        performanceMeterPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(0, 167, Short.MAX_VALUE)
    );

    inboundSliderPanel.setToolTipText("");
    inboundSliderPanel.setFont(new java.awt.Font("STHeiti", 0, 13));
    inboundSliderPanel.setMaximumSize(new java.awt.Dimension(190, 190));
    inboundSliderPanel.setMinimumSize(new java.awt.Dimension(190, 190));
    inboundSliderPanel.setOpaque(false);
    inboundSliderPanel.setPreferredSize(new java.awt.Dimension(190, 190));
    inboundSliderPanel.setSize(new java.awt.Dimension(190, 190));

    registerSpeedLabel.setFont(new java.awt.Font("STHeiti", 0, 8));
    registerSpeedLabel.setText("Speed");
    registerSpeedValue.setHorizontalAlignment(SwingConstants.CENTER);

    inboundRingingResponseDelayLabel.setFont(new java.awt.Font("STHeiti", 0, 8));
    inboundRingingResponseDelayLabel.setText("RngDlay");
    inboundRingingResponseDelayLabel.setToolTipText("Auto Ringing Response Delay Limit in mSec");
    registerSpeedValue.setHorizontalAlignment(SwingConstants.CENTER);

    inboundRingingResponseBusyRatioLabel.setFont(new java.awt.Font("STHeiti", 0, 8));
    inboundRingingResponseBusyRatioLabel.setText("Busy %");
    inboundRingingResponseBusyRatioLabel.setToolTipText("The Ratio Between Answering or Denying inbound Phonecalls (bottom is deny)");
    registerSpeedValue.setHorizontalAlignment(SwingConstants.CENTER);

    inboundEndDelayLabel.setFont(new java.awt.Font("STHeiti", 0, 8));
    inboundEndDelayLabel.setText("EndDlay");
    inboundEndDelayLabel.setToolTipText("Auto Ringing Response Delay Limit in mSec");
    registerSpeedValue.setHorizontalAlignment(SwingConstants.CENTER);

    inboundEndDelayValue.setFont(new java.awt.Font("STHeiti", 0, 8));
    inboundEndDelayValue.setText("0");
    registerSpeedValue.setHorizontalAlignment(SwingConstants.CENTER);

    registerSpeedValue.setFont(new java.awt.Font("STHeiti", 0, 8));
    registerSpeedValue.setText("0");
    registerSpeedValue.setHorizontalAlignment(SwingConstants.CENTER);

    inboundRingingResponseDelayValue.setFont(new java.awt.Font("STHeiti", 0, 8));
    inboundRingingResponseDelayValue.setText("0");
    registerSpeedValue.setHorizontalAlignment(SwingConstants.CENTER);

    inboundRingingResponseBusyRatioValue.setFont(new java.awt.Font("STHeiti", 0, 8));
    inboundRingingResponseBusyRatioValue.setText("0");
    registerSpeedValue.setHorizontalAlignment(SwingConstants.CENTER);

    registerSpeedSlider.setFont(new java.awt.Font("STHeiti", 0, 5));
    registerSpeedSlider.setMajorTickSpacing(10);
    registerSpeedSlider.setMinorTickSpacing(1);
    registerSpeedSlider.setOrientation(javax.swing.JSlider.VERTICAL);
    registerSpeedSlider.setPaintLabels(true);
    registerSpeedSlider.setPaintTicks(true);
    registerSpeedSlider.setSnapToTicks(true);
    registerSpeedSlider.setToolTipText("Register Speed");
    registerSpeedSlider.setValue(20);
    registerSpeedSlider.setFocusable(false);
    registerSpeedSlider.setInverted(true);
    registerSpeedSlider.setMaximumSize(new java.awt.Dimension(40, 150));
    registerSpeedSlider.setMinimumSize(new java.awt.Dimension(40, 150));
    registerSpeedSlider.setName(""); // NOI18N
    registerSpeedSlider.setPreferredSize(new java.awt.Dimension(40, 150));
    registerSpeedSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent evt) {
            registerSpeedSliderStateChanged(evt);
        }
    });

    inboundRingingResponseDelaySlider.setFont(new java.awt.Font("STHeiti", 0, 5));
    inboundRingingResponseDelaySlider.setMajorTickSpacing(5000);
    inboundRingingResponseDelaySlider.setMaximum(60000);
    inboundRingingResponseDelaySlider.setMinorTickSpacing(1000);
    inboundRingingResponseDelaySlider.setOrientation(javax.swing.JSlider.VERTICAL);
    inboundRingingResponseDelaySlider.setPaintLabels(true);
    inboundRingingResponseDelaySlider.setPaintTicks(true);
    inboundRingingResponseDelaySlider.setSnapToTicks(true);
    inboundRingingResponseDelaySlider.setToolTipText("Random Ring Delay");
    inboundRingingResponseDelaySlider.setValue(30000);
    inboundRingingResponseDelaySlider.setFocusable(false);
    inboundRingingResponseDelaySlider.setMaximumSize(new java.awt.Dimension(40, 150));
    inboundRingingResponseDelaySlider.setMinimumSize(new java.awt.Dimension(40, 150));
    inboundRingingResponseDelaySlider.setName(""); // NOI18N
    inboundRingingResponseDelaySlider.setPreferredSize(new java.awt.Dimension(40, 150));
    inboundRingingResponseDelaySlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent evt) {
            inboundRingingResponseDelaySliderStateChanged(evt);
        }
    });

    inboundRingingResponseBusyRatioSlider.setFont(new java.awt.Font("STHeiti", 0, 5));
    inboundRingingResponseBusyRatioSlider.setMajorTickSpacing(10);
    inboundRingingResponseBusyRatioSlider.setMinorTickSpacing(1);
    inboundRingingResponseBusyRatioSlider.setOrientation(javax.swing.JSlider.VERTICAL);
    inboundRingingResponseBusyRatioSlider.setPaintLabels(true);
    inboundRingingResponseBusyRatioSlider.setPaintTicks(true);
    inboundRingingResponseBusyRatioSlider.setSnapToTicks(true);
    inboundRingingResponseBusyRatioSlider.setToolTipText("Busy Percentage");
    inboundRingingResponseBusyRatioSlider.setValue(10);
    inboundRingingResponseBusyRatioSlider.setFocusable(false);
    inboundRingingResponseBusyRatioSlider.setMaximumSize(new java.awt.Dimension(40, 150));
    inboundRingingResponseBusyRatioSlider.setMinimumSize(new java.awt.Dimension(40, 150));
    inboundRingingResponseBusyRatioSlider.setName(""); // NOI18N
    inboundRingingResponseBusyRatioSlider.setPreferredSize(new java.awt.Dimension(40, 150));
    inboundRingingResponseBusyRatioSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent evt) {
            inboundRingingResponseBusyRatioSliderStateChanged(evt);
        }
    });

    inboundEndDelaySlider.setFont(new java.awt.Font("STHeiti", 0, 5));
    inboundEndDelaySlider.setMajorTickSpacing(6000);
    inboundEndDelaySlider.setMaximum(60000);
    inboundEndDelaySlider.setMinimum(1000);
    inboundEndDelaySlider.setMinorTickSpacing(1000);
    inboundEndDelaySlider.setOrientation(javax.swing.JSlider.VERTICAL);
    inboundEndDelaySlider.setPaintLabels(true);
    inboundEndDelaySlider.setPaintTicks(true);
    inboundEndDelaySlider.setSnapToTicks(true);
    inboundEndDelaySlider.setToolTipText("Random End Call Delay");
    inboundEndDelaySlider.setValue(30000);
    inboundEndDelaySlider.setFocusable(false);
    inboundEndDelaySlider.setMaximumSize(new java.awt.Dimension(40, 150));
    inboundEndDelaySlider.setMinimumSize(new java.awt.Dimension(40, 150));
    inboundEndDelaySlider.setName(""); // NOI18N
    inboundEndDelaySlider.setPreferredSize(new java.awt.Dimension(40, 150));
    inboundEndDelaySlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent evt) {
            inboundEndDelaySliderStateChanged(evt);
        }
    });

    org.jdesktop.layout.GroupLayout inboundSliderPanelLayout = new org.jdesktop.layout.GroupLayout(inboundSliderPanel);
    inboundSliderPanel.setLayout(inboundSliderPanelLayout);
    inboundSliderPanelLayout.setHorizontalGroup(
        inboundSliderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(inboundSliderPanelLayout.createSequentialGroup()
            .addContainerGap()
            .add(inboundSliderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(inboundSliderPanelLayout.createSequentialGroup()
                    .add(inboundSliderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(registerSpeedLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 31, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(registerSpeedValue, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(inboundSliderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(inboundSliderPanelLayout.createSequentialGroup()
                            .add(2, 2, 2)
                            .add(inboundRingingResponseDelayLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 35, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(inboundRingingResponseDelayValue, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 29, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(inboundSliderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                        .add(inboundRingingResponseBusyRatioLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 31, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(inboundSliderPanelLayout.createSequentialGroup()
                            .add(2, 2, 2)
                            .add(inboundRingingResponseBusyRatioValue, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 29, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(inboundSliderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(inboundEndDelayValue, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 29, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(inboundEndDelayLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 35, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(inboundSliderPanelLayout.createSequentialGroup()
                    .add(registerSpeedSlider, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 34, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(inboundRingingResponseDelaySlider, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(inboundRingingResponseBusyRatioSlider, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(inboundEndDelaySlider, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
            .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
    inboundSliderPanelLayout.setVerticalGroup(
        inboundSliderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(inboundSliderPanelLayout.createSequentialGroup()
            .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(inboundSliderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(inboundSliderPanelLayout.createSequentialGroup()
                    .add(inboundRingingResponseDelayLabel)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(inboundRingingResponseDelayValue))
                .add(inboundSliderPanelLayout.createSequentialGroup()
                    .add(inboundSliderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(inboundSliderPanelLayout.createSequentialGroup()
                            .add(registerSpeedLabel)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(registerSpeedValue))
                        .add(inboundSliderPanelLayout.createSequentialGroup()
                            .add(inboundRingingResponseBusyRatioLabel)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(inboundRingingResponseBusyRatioValue))
                        .add(inboundSliderPanelLayout.createSequentialGroup()
                            .add(inboundEndDelayLabel)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(inboundEndDelayValue)))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(inboundSliderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, inboundEndDelaySlider, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 150, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, inboundSliderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(registerSpeedSlider, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 150, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(inboundRingingResponseDelaySlider, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 150, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(inboundRingingResponseBusyRatioSlider, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 150, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))))
    );

    org.jdesktop.layout.GroupLayout buttonPanelLayout = new org.jdesktop.layout.GroupLayout(buttonPanel);
    buttonPanel.setLayout(buttonPanelLayout);
    buttonPanelLayout.setHorizontalGroup(
        buttonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(org.jdesktop.layout.GroupLayout.TRAILING, buttonPanelLayout.createSequentialGroup()
            .addContainerGap()
            .add(inboundSliderPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 164, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(12, 12, 12)
            .add(performanceMeterPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 188, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(outboundSliderPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE)
            .addContainerGap())
    );
    buttonPanelLayout.setVerticalGroup(
        buttonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(buttonPanelLayout.createSequentialGroup()
            .addContainerGap()
            .add(buttonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(performanceMeterPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(buttonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, inboundSliderPanel, 0, 186, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, outboundSliderPanel, 0, 186, Short.MAX_VALUE)))
            .addContainerGap(12, Short.MAX_VALUE))
    );

    org.jdesktop.layout.GroupLayout controlSliderPanelLayout = new org.jdesktop.layout.GroupLayout(controlSliderPanel);
    controlSliderPanel.setLayout(controlSliderPanelLayout);
    controlSliderPanelLayout.setHorizontalGroup(
        controlSliderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(controlSliderPanelLayout.createSequentialGroup()
            .add(buttonPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addContainerGap(13, Short.MAX_VALUE))
    );
    controlSliderPanelLayout.setVerticalGroup(
        controlSliderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(buttonPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
    );

    org.jdesktop.layout.GroupLayout colorMaskPanelLayout = new org.jdesktop.layout.GroupLayout(colorMaskPanel);
    colorMaskPanel.setLayout(colorMaskPanelLayout);
    colorMaskPanelLayout.setHorizontalGroup(
        colorMaskPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(colorMaskPanelLayout.createSequentialGroup()
            .addContainerGap()
            .add(colorMaskPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(controlSliderPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(tabPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(displayPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(controlButtonPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .addContainerGap(9, Short.MAX_VALUE))
    );
    colorMaskPanelLayout.setVerticalGroup(
        colorMaskPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(colorMaskPanelLayout.createSequentialGroup()
            .add(tabPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(displayPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(controlButtonPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(controlSliderPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addContainerGap(32, Short.MAX_VALUE))
    );

    org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
        layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(colorMaskPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
    );
    layout.setVerticalGroup(
        layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(colorMaskPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
    );

    pack();
    }// </editor-fold>//GEN-END:initComponents

    private void saveConfigurationButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_saveConfigurationButtonActionPerformed
        configurationCallCenter.setDomain(domainField.getText());
        configurationCallCenter.setClientIP(clientIPField.getText());
        configurationCallCenter.setPublicIP(pubIPField.getText());
        configurationCallCenter.setClientPort(clientPortField.getText());
        configurationCallCenter.setServerIP(serverIPField.getText());
        configurationCallCenter.setServerPort(serverPortField.getText());
        configurationCallCenter.setPrefPhoneLines(Integer.toString(prefPhoneLinesSlider.getValue()));
        configurationCallCenter.setUsername(usernameField.getText());
        configurationCallCenter.setToegang(new String(toegangField.getPassword()));
        configurationCallCenter.setRegister("1");
        if (registerCheckBox.isSelected())  {configurationCallCenter.setRegister("1");}     else {configurationCallCenter.setRegister("0");}
        if (iconsCheckBox.isSelected())     {configurationCallCenter.setIcons("1");}        else {configurationCallCenter.setIcons("0");}
        configurationCallCenter.saveConfiguration("3");
        configurationCallCenter.loadConfiguration("3");
        if (Integer.parseInt(configurationCallCenter.getPrefPhoneLines()) > vergunning.getPhoneLines()) { configurationCallCenter.setPrefPhoneLines(Integer.toString(vergunning.getPhoneLines())); }
}//GEN-LAST:event_saveConfigurationButtonActionPerformed

    private void humanResponseSimulatorToggleButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_humanResponseSimulatorToggleButtonActionPerformed
        if (humanResponseSimulatorToggleButton.isSelected()) { humanResponseSimulator("1"); } else { humanResponseSimulator("0"); }
    }//GEN-LAST:event_humanResponseSimulatorToggleButtonActionPerformed

    private void humanResponseSimulator(final String buttonsOnParam)
    {
//	final String buttonOn = new String(buttonsOnParam);
        Thread humanResponseSimulatorThread = new Thread( allThreadsGroup, new Runnable()
        {
            @Override
            public void run()
            {
                String[] status = new String[2];
		if ( buttonsOnParam.equals("1") )
                {
                    humanResponseSimulatorToggleButton.setSelected(true);
                }

                serviceLoopProgressBar.setEnabled(true);
                int simulateCounter = 0;
                showStatus("Setting Inbound SoftPhones Simulation...", true, true); /* true = logToApplic, true = logToFile */
                while (simulateCounter < outboundSoftPhonesAvailable) // Starts looping through the user-range
                {
                    phoneStatsTable.setValueAt(simulateCounter + 1, 1, 1);

                    // Sets Human Simulation for Inbound Mode
                    SoftPhone thisSoftPhoneInstance =  (SoftPhone) threadArray[simulateCounter];
                    status = thisSoftPhoneInstance.userInput(RANDOMRINGRESPONSEBUTTON, buttonsOnParam, Integer.toString((int)(Math.random()*inboundRingingResponseDelaySlider.getValue())), Integer.toString(inboundRingingResponseBusyRatioSlider.getValue()));
                    if (status[0].equals("1")) { showStatus("Human Simulation Error: " + status[1], true, true); /* true = logToApplic, true = logToFile */ }
                    try { Thread.sleep(1); } catch (InterruptedException ex) {  }

                    // Sets the CallEnd Timer to autoEnd this Call after a certain random period (The answerer lost interest)
                    status = thisSoftPhoneInstance.userInput(ENDTIMERBUTTON, buttonsOnParam, Integer.toString((int)(Math.random()*inboundEndDelaySlider.getValue())), "");
                    if (status[0].equals("1")) { showStatus("End Timer Error: " + status[1], true, true); /* true = logToApplic, true = logToFile */ }
                    serviceLoopProgressBar.setValue(simulateCounter);
                    simulateCounter++;
                }
		if ( buttonsOnParam.equals("1") )
		{
                    callCenterIsOutBound = false; boundMode = "Inbound";
		    humanResponseSimulatorToggleButton.setForeground(Color.BLUE);
                    setAutoSpeed(false);
		    phoneStatsTable.setValueAt("-", 1, 1);
                    showStatus(Vergunning.PRODUCT + " Inbound Test Mode Enabled", true, true); /* true = logToApplic, true = logToFile */
		}
		else
		{
		    humanResponseSimulatorToggleButton.setForeground(Color.BLACK);
		    humanResponseSimulatorToggleButton.setSelected(false);
                    setAutoSpeed(true);
		    phoneStatsTable.setValueAt("-", 1, 1);
                    showStatus(Vergunning.PRODUCT + " Inbound Test Mode Disabled", true, true); /* true = logToApplic, true = logToFile */
		}
		serviceLoopProgressBar.setValue(0); serviceLoopProgressBar.setEnabled(false);
                return;
            }
        });
        humanResponseSimulatorThread.setName("humanResponseSimulatorThread");
        humanResponseSimulatorThread.setDaemon(runThreadsAsDaemons);
        humanResponseSimulatorThread.start();

	// The Human Simulator must allways wait for the RegistrationTimerThread
    }


    private void muteAudioToggleButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_muteAudioToggleButtonActionPerformed
        if (muteAudioToggleButton.isSelected())
        {
            muteAudio(true);
        }
        else
        {
            muteAudio(false);
        }
    }//GEN-LAST:event_muteAudioToggleButtonActionPerformed

    private void muteAudio(final boolean muteParam)
    {
        Thread muteAudioThread = new Thread( allThreadsGroup, new Runnable()
        {
            @Override
            public void run()
            {
                int muteAudioCounter = 0;
                String[] status = new String[2];
		serviceLoopProgressBar.setEnabled(true);
                if (muteParam)
                {
//                    myClickOnSoundTool.play();
                    
		    showStatus("Enable Mute Audio SoftPhone...", true, true); /* true = logToApplic, true = logToFile */
                    while (muteAudioCounter < outboundSoftPhonesAvailable) // Starts looping through the user-range
                    {
                        SoftPhone thisSoftPhoneInstance =  (SoftPhone) threadArray[muteAudioCounter]; // Get the reference to the SoftPhone object in the loop
                        status = thisSoftPhoneInstance.userInput(MUTEAUDIOBUTTON, "1", "", ""); // Send a registerButton response to this object's method userInput
                        if (status[0].equals("1")) { showStatus("Mute Audio Error: " + status[1], true, true); /* true = logToApplic, true = logToFile */ }
			phoneStatsTable.setValueAt(muteAudioCounter + 1, 1, 1);
			serviceLoopProgressBar.setValue(muteAudioCounter);
                        try { Thread.sleep(1); } catch (InterruptedException ex) {  }
                        muteAudioCounter++;
                    }
		    showStatus("Enable Mute Audio SoftPhone Completed", true, true); /* true = logToApplic, true = logToFile */
                    muteAudioToggleButton.setForeground(Color.blue);
                    phoneStatsTable.setValueAt("-", 1, 1);
                }
                else
                {
//                    myClickOffSoundTool.play();
                    muteAudioCounter = 0;
		    showStatus("Disable Mute Audio SoftPhone...", true, true); /* true = logToApplic, true = logToFile */
                    while (muteAudioCounter < outboundSoftPhonesAvailable) // Starts looping through the user-range
                    {
                        SoftPhone thisSoftPhoneInstance = (SoftPhone) threadArray[muteAudioCounter]; // Get the reference to the SoftPhone object in the loop
                        status = thisSoftPhoneInstance.userInput(MUTEAUDIOBUTTON, "0", "", ""); // Send a registerButton response to this object's method userInput
                        if (status[0].equals("1")) { showStatus("Mute Audio Error: " + status[1], true, true); /* true = logToApplic, true = logToFile */ }
			phoneStatsTable.setValueAt(muteAudioCounter + 1, 1, 1); // ProcessingInstance
			serviceLoopProgressBar.setValue(muteAudioCounter);
                        try { Thread.sleep(1); } catch (InterruptedException ex) {  }
                        muteAudioCounter++;
                    }
		    showStatus("Disable Mute Audio SoftPhone Completed", true, true); /* true = logToApplic, true = logToFile */
                    muteAudioToggleButton.setForeground(Color.black);
                    phoneStatsTable.setValueAt("-", 1, 1);
                }
		serviceLoopProgressBar.setValue(0); serviceLoopProgressBar.setEnabled(false);
                return;
            }
        });
        muteAudioThread.setName("muteAudioThread");
        muteAudioThread.setDaemon(runThreadsAsDaemons);
        muteAudioThread.start();
    }

    private void registerToggleButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_registerToggleButtonActionPerformed
	if (registerToggleButton.isSelected())
	{
            register();
        }
	else
	{
	    // Setup the frequent SystemStats Timer
	    updateSystemStatsTimer.cancel(); updateSystemStatsTimer.purge();
            showStatus("updateSystemStatsTimer Canceled!", true, true); /* true = logToApplic, true = logToFile */
	    updateSystemStatsTimer = new Timer(); updateSystemStatsTimer.scheduleAtFixedRate(new UpdateSystemStatsTimer(this), (long)(0), updateSystemStatsTimerFastInterval);
            showStatus("updateSystemStatsTimer Scheduled immediate at " + Math.round(updateSystemStatsTimerFastInterval / 1000) + " Sec Interval", true, true); /* true = logToApplic, true = logToFile */

    	    // Delete the infrequent ReRegister to Proxy timer
            reRegisterTimer.cancel(); reRegisterTimer.purge();
            unRegister();
	}
    }//GEN-LAST:event_registerToggleButtonActionPerformed

    private void callButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_callButtonActionPerformed
        Thread outboundEndButtonActionPerformedThread8 = new Thread( allThreadsGroup, new Runnable()
        {
            @Override
            public void run()
            {
                serviceLoopProgressBar.setEnabled(true);
                callCounter = 0;
		showStatus("Sending Call Button Activation to SoftPhones...", true, true); /* true = logToApplic, true = logToFile */
                while (callCounter < outboundSoftPhonesAvailable) // Starts looping through the user-range
                {
                    // Creating a temp reference to softphone instance in loop

                    Thread callButtonThread = new Thread( allThreadsGroup, new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            String[] status = new String[2];
                            SoftPhone thisSoftPhoneInstance = (SoftPhone) threadArray[callCounter];
                            status = thisSoftPhoneInstance.userInput(CALLBUTTON, "", "", "");
                            if (status[0].equals("1")) { showStatus("Call Failure: " + status[1], true, true); /* true = logToApplic, true = logToFile */ }
                        }
                    });
                    callButtonThread.setName("callButtonThread");
                    callButtonThread.setDaemon(runThreadsAsDaemons);
                    callButtonThread.start();
                    
                    try { Thread.sleep(smoothMovementPeriod); } catch (InterruptedException ex) {  }
		    phoneStatsTable.setValueAt(callCounter + 1, 1, 1); // ProcessingInstance
                    serviceLoopProgressBar.setValue(callCounter);
                    callCounter++;
                }
		showStatus("Sending Call Button Activation to SoftPhones Completed", true, true); /* true = logToApplic, true = logToFile */
                phoneStatsTable.setValueAt("-", 1, 1); // ProcessingInstance
                serviceLoopProgressBar.setValue(0); serviceLoopProgressBar.setEnabled(false);
                return;
            }
        });
        outboundEndButtonActionPerformedThread8.setName("outboundEndButtonActionPerformedThread8");
        outboundEndButtonActionPerformedThread8.setDaemon(runThreadsAsDaemons);
        outboundEndButtonActionPerformedThread8.start();

    }//GEN-LAST:event_callButtonActionPerformed

    private void endButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_endButtonActionPerformed
        Thread outboundEndButtonActionPerformedThread8 = new Thread( allThreadsGroup, new Runnable()
        {
            @Override
            public void run()
            {
                serviceLoopProgressBar.setEnabled(true);
                endCounter = 0;
		showStatus("Sending End Button Activation to SoftPhones...", true, true); /* true = logToApplic, true = logToFile */
                while (endCounter < outboundSoftPhonesAvailable) // Starts looping through the user-range
                {
                    // Creating a temp reference to softphone instance in loop
                    Thread endThread = new Thread( allThreadsGroup, new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            String[] status = new String[2];
                            SoftPhone thisSoftPhoneInstance = (SoftPhone) threadArray[endCounter];
                            status = thisSoftPhoneInstance.userInput(ENDBUTTON, "", "", "");
                            if (status[0].equals("1")) { showStatus("End Failure: " + status[1], true, true); /* true = logToApplic, true = logToFile */ }
                        }
                    });
                    endThread.setName("endThread");
                    endThread.setDaemon(runThreadsAsDaemons);
                    endThread.start();

                    try { Thread.sleep(smoothMovementPeriod); } catch (InterruptedException ex) {  }
		    phoneStatsTable.setValueAt(endCounter + 1, 1, 1); // ProcessingInstance
                    serviceLoopProgressBar.setValue(endCounter);
                    endCounter++;
                }
		showStatus("Sending End Button Activation to SoftPhones Completed", true, true); /* true = logToApplic, true = logToFile */
                phoneStatsTable.setValueAt("-", 1, 1); // ProcessingInstance
                serviceLoopProgressBar.setValue(0); serviceLoopProgressBar.setEnabled(false);
                return;
            }
        });
        outboundEndButtonActionPerformedThread8.setName("outboundEndButtonActionPerformedThread8");
        outboundEndButtonActionPerformedThread8.setDaemon(runThreadsAsDaemons);
        outboundEndButtonActionPerformedThread8.start();
    }//GEN-LAST:event_endButtonActionPerformed

    private void powerToggleButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_powerToggleButtonActionPerformed
        if (powerToggleButton.isSelected()) { setPowerOn(true); } else { setPowerOn(false); }
    }//GEN-LAST:event_powerToggleButtonActionPerformed

    /**
     *
     * @param onParam
     */
    public void setPowerOn(final boolean onParam) {
        while (! defaultConstructorIsReady ) { try { Thread.sleep(100); } catch (InterruptedException ex) {  } }
        
        Thread outboundPowerToggleButtonActionPerformedThread9 = new Thread( allThreadsGroup, new Runnable()
        {
            @Override
            @SuppressWarnings("static-access")
            public void run()
            {
                String[] status = new String[2];
                if ((onParam) && (vergunning.isValid()))
                {
//                    myClickOnSoundTool.play();
                    setImagePanelVisible(false);
//                    smoothCheckBox.setSelected(false);
                    
                    String varUsername;
                    callCenterStatus = POWERINGON;
		    powerToggleButton.setSelected(true);powerToggleButton.setForeground(Color.BLUE);
                    showStatus("PowerOn " + Vergunning.PRODUCT + "...", true, true); /* true = logToApplic, true = logToFile */
                    try { Thread.sleep(1000); } catch (InterruptedException ex) { }
                    eCallCenterGUI.destinationTextArea.setEnabled(true);
                    String clientIP     = configurationCallCenter.getClientIP();

                    if (VoipStormTools.isLong(configurationCallCenter.getUsername()))
                    {
                        usernameStart       = Long.parseLong(configurationCallCenter.getUsername());
                    }
                    else
                    {
                        usernameStart       = 0L;
                    }
                    prefixToegang      = prefixField.getText();
                    suffixToegang      = suffixField.getText();
                    outboundSoftPhonesAvailable = softphonesQuantity;

		    serviceLoopProgressBar.setMaximum(outboundSoftPhonesAvailable -1);
                    threadArray = new Thread[outboundSoftPhonesAvailable + 1];

                    // Now that we know how many instances are required, we can reinstantiate the phonesTable.
                    // Set the preferred number of columns and calculate the rowcount

                    final int phonesTableRowsNeeded = Math.round(outboundSoftPhonesAvailable / phonesPoolTablePreferredColumns);

                    PhonesPoolTableModel tableModel = new PhonesPoolTableModel();
                    phonesPoolTable.setModel(tableModel); // [rows][cols] so rows is an array of columns // Originally DefaultTableModel
                    phonesPoolTableCellRenderer = new PhonesPoolTableCellRenderer();

                    for ( int columnCounter = 0; columnCounter<phonesPoolTablePreferredColumns; columnCounter++ )
                    {
                        phonesPoolTableColumn = phonesPoolTable.getColumnModel().getColumn(columnCounter);
                        phonesPoolTableColumn.setCellRenderer(phonesPoolTableCellRenderer);
                        phonesPoolTable.getColumnModel().getColumn(columnCounter).setResizable(false);
                        phonesPoolTable.getColumnModel().getColumn(columnCounter).setMinWidth(PHONESPOOLTABLECOLUMNWIDTH);
                        phonesPoolTable.getColumnModel().getColumn(columnCounter).setMaxWidth(PHONESPOOLTABLECOLUMNWIDTH);
                        phonesPoolTable.getColumnModel().getColumn(columnCounter).setPreferredWidth(PHONESPOOLTABLECOLUMNWIDTH);
                    }
                    phonesPoolTable.setRowHeight(PHONESPOOLTABLECOLUMNHEIGHT);                    

                    phonesPoolTable.setForeground(new java.awt.Color(102, 102, 102));
                    phonesPoolTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
                    phonesPoolTable.setAutoscrolls(false);
                    phonesPoolTable.setDoubleBuffered(true);
                    phonesPoolTable.setFocusTraversalKeysEnabled(false);
                    phonesPoolTable.setFocusable(false);
                    phonesPoolTable.setRequestFocusEnabled(false);
                    phonesPoolTable.setSelectionBackground(new java.awt.Color(204, 204, 204));
                    phonesPoolTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
                    phonesPoolTable.setRowSelectionAllowed(false);
                    phonesPoolTable.setColumnSelectionAllowed(false);
                    phonesPoolTable.getTableHeader().setResizingAllowed(false);
                    phonesPoolTable.getTableHeader().setReorderingAllowed(false);
                    phonesPoolTable.setShowGrid(false);

                    outboundInstanceCounter = 0;
                    powerCounter = 0;
		    serviceLoopProgressBar.setEnabled(true);

		    showStatus("Powering SoftPhones...", true, true); /* true = logToApplic, true = logToFile */
                    while (powerCounter < outboundSoftPhonesAvailable)
                    {
                        try { configurationSoftPhone = (Configuration) configurationCallCenter.clone(); }
                        catch (CloneNotSupportedException ex) { showStatus(ex.getMessage(), true, true); }


//                        if ((VoipStormTools.isLong(configurationCallCenter.getUsername())) && (configurationSoftPhone.getPublicIP().length() == 0))
//                        {
//                            varUsername = Long.toString(usernameStart + (long)powerCounter);
//                            configurationSoftPhone.setUsername(varUsername);
//                            configurationSoftPhone.setToegang(prefixToegang + varUsername + suffixToegang);
//                        }
//                        else // or single account to remote PBX (budgetphone)
//                        {
                            configurationSoftPhone.setUsername(configurationCallCenter.getUsername());
                            configurationSoftPhone.setToegang(configurationCallCenter.getToegang());
//                        }

//                        if (configurationSoftPhone.getPublicIP().length() == 0) // multiple accounts to local proxy
//                        {
//                            varUsername = Long.toString(usernameStart + (long)powerCounter);
//                            configurationSoftPhone.setUsername(varUsername);
//                            configurationSoftPhone.setToegang(prefixToegang + varUsername + suffixToegang);
//                        }
//                        else // or single account to remote PBX (budgetphone)
//                        {
//                            configurationSoftPhone.setUsername(configurationCallCenter.getUsername());
//                            configurationSoftPhone.setToegang(configurationCallCenter.getToegang());
//                        }

                        // SoftPhone instance
                        try { threadArray[powerCounter] = new SoftPhone(eCallCenterGUI, powerCounter, debugging, configurationSoftPhone); }
                        catch (CloneNotSupportedException error) { showStatus("Error: threadArray[powerCounter] = new SoftPhone(..)" + error.getMessage(), true, true); /* true = logToApplic, true = logToFile */ }



                        threadArray[powerCounter].setName("SoftPhone" + threadArray[powerCounter]);
                        threadArray[powerCounter].setDaemon(runThreadsAsDaemons); // Starts the SoftPhone object as a thread
                        threadArray[powerCounter].setPriority(5); // Starts the SoftPhone object as a thread
                        threadArray[powerCounter].start(); // Starts the SoftPhone object as a thread

                        // New included start lsteners all in one go
                        status[0]=""; status[1]="";
                        SoftPhone thisSoftPhoneInstance = (SoftPhone) threadArray[powerCounter];
                        thisSoftPhoneInstance.autoEndCall = true; // Make sure the calls end after streaming the media

                        status = thisSoftPhoneInstance.startListener(eCallCenterGUI.configurationSoftPhone.getClientPort());
                        if (status[0].equals("1")) { showStatus("startListener Error: " + status[1], true, true); }
                        else
                        {
                            if (powerCounter < (outboundSoftPhonesAvailable - 1)) // get rid of the last empty line if else construction
                            {
                                eCallCenterGUI.destinationTextArea.append("sip:" + clientIP + ":" + status[1] + lineTerminator);
                            }
                            else
                            {
                                eCallCenterGUI.destinationTextArea.append("sip:" + clientIP + ":" + status[1]);
                            }
                        }

                        phoneStatsTable.setValueAt((outboundInstanceCounter + 1), 0, 1);
                        phoneStatsTable.setValueAt((powerCounter + 1), 1, 1);
                        outboundInstanceCounter++;
                        powerCounter++;
			serviceLoopProgressBar.setValue(powerCounter);
                        try { Thread.sleep(ultraShortMessagePeriod); } catch (InterruptedException ex) {  }
                    }
		    serviceLoopProgressBar.setValue(0); serviceLoopProgressBar.setEnabled(false);

                    phoneStatsTable.setValueAt("-", 1, 1); // ProcessingInstance
		    serviceLoopProgressBar.setValue(0); serviceLoopProgressBar.setEnabled(false);

//		    // Sets Mute
//		    powerCounter = 0;
//		    serviceLoopProgressBar.setEnabled(true);
//		    showStatus("Mute Audio SoftPhones...", true, true); /* true = logToApplic, true = logToFile */
//		    while (powerCounter < outboundSoftPhonesAvailable) // Starts looping through the user-range
//		    {
//			SoftPhone thisSoftPhoneInstance =  (SoftPhone) threadArray[powerCounter];
//			status = thisSoftPhoneInstance.userInput(MUTEAUDIOBUTTON, "1", "", "");
//			if (status[0].equals("1")) { showStatus("Mute Audio Error: " + status[1], true, true); /* true = logToApplic, true = logToFile */ }
//			phoneStatsTable.setValueAt(powerCounter + 1, 1, 1);
//			try { Thread.sleep(ultraShortMessagePeriod); } catch (InterruptedException ex) {  }
//			serviceLoopProgressBar.setValue(powerCounter);
//			powerCounter++;
//		    }
//		    phoneStatsTable.setValueAt("-", 1, 1);
//		    serviceLoopProgressBar.setValue(0); serviceLoopProgressBar.setEnabled(false);
//		    callRatioChartData.setValue("Slack", 0); callRatioChartData.setValue("Busy", 0); callRatioChartData.setValue("Success", 0); graphInnerPanel.setVisible(true); chartPanel.setVisible(true);


                    if (outboundSoftPhonesAvailable > 0) // Successfully started our SoftPhone pool
                    {
                        showStatus(Vergunning.PRODUCT + " Ready", true, true);
                        powerToggleButton.setForeground(Color.blue);
                        callButton.setEnabled(true);
                        endButton.setEnabled(true);
                        autoSpeedToggleButton.setEnabled(true);
                        campaignProgressBar.setEnabled(true);

			muteAudioToggleButton.setEnabled(true);
			muteAudioToggleButton.setSelected(true);
			muteAudioToggleButton.setForeground(Color.blue);
			registerToggleButton.setEnabled(true);
			humanResponseSimulatorToggleButton.setEnabled(true);
                        debugToggleButton.setEnabled(true);

                        vergunningTypeList.setEnabled(false);
                        vergunningDateChooserPanel.setEnabled(false);
                        vergunningPeriodList.setEnabled(false);

                        String[] openCampaigns = dbClient.getOpenCampaigns();
                        if ((openCampaigns != null) && (openCampaigns.length > 0))
                        {
                            campaignComboBox.setModel(new javax.swing.DefaultComboBoxModel(openCampaigns));
                            campaignComboBox.setEnabled(true);
                        } else {campaignComboBox.setEnabled(false); runCampaignToggleButton.setEnabled(false); runCampaignToggleButton.setEnabled(false);}
                    }
                    callCenterStatus = POWEREDON;
                }
                else
                {
//                    myClickOffSoundTool.play();
                    powerCounter = outboundInstanceCounter;
                    showStatus("Powering Off " + Vergunning.PRODUCT, true, true);
                    while (powerCounter > 0) // Starts looping through the user-range
                    {
                        powerCounter--;
                        Thread powerOffThread = new Thread( allThreadsGroup, new Runnable()
                        {
                            @Override
                            @SuppressWarnings("static-access")
                            public void run()
                            {
                                SoftPhone thisSoftPhoneInstance = (SoftPhone) threadArray[powerCounter]; // Get the reference to the SoftPhone object in the loop
                                thisSoftPhoneInstance.pleaseStop();
                                String[] status = new String[2];
                                status = thisSoftPhoneInstance.userInput(LINE1BUTTON, "1", "0", "");
                                if (status[0].equals("1")) { showStatus("Power Error: " + status[1], true, true); }
                            }
                        });
                        powerOffThread.setName("powerOffThread");
                        powerOffThread.setDaemon(runThreadsAsDaemons);
                        powerOffThread.start();

                        outboundInstanceCounter--; phoneStatsTable.setValueAt(outboundInstanceCounter + outboundInstanceCounter, 0, 1); // Instance
                        phoneStatsTable.setValueAt(powerCounter + outboundInstanceCounter, 0, 1); // Processing
                        serviceLoopProgressBar.setValue(powerCounter);
                        try { Thread.sleep(smoothMovementPeriod); } catch (InterruptedException ex) {  }
                    }
                    powerToggleButton.setForeground(Color.black);
                    registerToggleButton.setSelected(false); registerToggleButton.setForeground(Color.black); registerToggleButton.setEnabled(false);
                    humanResponseSimulatorToggleButton.setSelected(false); humanResponseSimulatorToggleButton.setForeground(Color.black); humanResponseSimulatorToggleButton.setEnabled(false);
                    muteAudioToggleButton.setSelected(false); muteAudioToggleButton.setForeground(Color.black); muteAudioToggleButton.setEnabled(false);
                    debugToggleButton.setSelected(false); debugToggleButton.setForeground(Color.black); debugToggleButton.setEnabled(false);

                    callButton.setEnabled(false);
                    endButton.setEnabled(false);
                    autoSpeedToggleButton.setEnabled(false);

                    vergunningTypeList.setEnabled(true);
                    vergunningDateChooserPanel.setEnabled(true);
//                    licensePeriodList.setEnabled(true);

                    showStatus(Vergunning.PRODUCT + " Powered Off", true, true);
                    phoneStatsTable.setValueAt("-", 1, 1); // Processing
                    callCenterStatus = POWEREDOFF;

                    if ( ! vergunning.isValid())
                    {
                        showStatus("Please select \"License Type\" and see \"License Details\"...", false, false);
                        tabPane.setSelectedIndex(6);
//                        setImagePanelVisible(true);
                    }
                    try { Thread.sleep(mediumMessagePeriod); } catch (InterruptedException ex) {  }
                    setImagePanelVisible(true);
                }
                return;
            }
        });
        outboundPowerToggleButtonActionPerformedThread9.setName("outboundPowerToggleButtonActionPerformedThread9");
        outboundPowerToggleButtonActionPerformedThread9.setDaemon(runThreadsAsDaemons);
        outboundPowerToggleButtonActionPerformedThread9.start();
    }

    private void registerSpeedSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_registerSpeedSliderStateChanged
//        myTickSoundTool.play();
        registrationBurstDelay = registerSpeedSlider.getValue();
        registerSpeedValue.setText(Integer.toString(registrationBurstDelay));
    }//GEN-LAST:event_registerSpeedSliderStateChanged

    private void vmUsageThresholdSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_vmUsageThresholdSliderStateChanged
//	myTickSoundTool.play();
        vmUsagePauseThreashold = vmUsageThresholdSlider.getValue();
        vmUsagePauseValue.setText(Integer.toString((int) vmUsagePauseThreashold));
    }//GEN-LAST:event_vmUsageThresholdSliderStateChanged

    private void memFreeThresholdSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_memFreeThresholdSliderStateChanged
//	myTickSoundTool.play();
        memFreeThreshold = memFreeThresholdSlider.getValue();
        memFreeThresholdValue.setText(Integer.toString((int) memFreeThreshold));
    }//GEN-LAST:event_memFreeThresholdSliderStateChanged

    private void heapMemFreeThresholdSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_heapMemFreeThresholdSliderStateChanged
//	myTickSoundTool.play();
        heapMemFreeThreshold = heapMemFreeThresholdSlider.getValue();
        heapMemFreeThresholdValue.setText(Integer.toString((int) heapMemFreeThreshold));
    }//GEN-LAST:event_heapMemFreeThresholdSliderStateChanged

    private void callingTallyLimitSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_callingTallyLimitSliderStateChanged
//	myTickSoundTool.play();
        callingTallyLimit = callingTallyLimitSlider.getValue();
        callingTallyLimitValue.setText(Integer.toString((int) callingTallyLimit));
    }//GEN-LAST:event_callingTallyLimitSliderStateChanged

    private void establishedTallyLimitSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_establishedTallyLimitSliderStateChanged
//	myTickSoundTool.play();
        establishedTallyLimit = establishedTallyLimitSlider.getValue();
        establishedTallyLimitValue.setText(Integer.toString((int) establishedTallyLimit));
    }//GEN-LAST:event_establishedTallyLimitSliderStateChanged

    private void callSpeedSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_callSpeedSliderStateChanged
//        myTickSoundTool.play();
        outboundBurstDelay = callSpeedSlider.getValue();
        callSpeedValue.setText(Integer.toString(outboundBurstDelay));
    }//GEN-LAST:event_callSpeedSliderStateChanged

    private void inboundRingingResponseDelaySliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_inboundRingingResponseDelaySliderStateChanged
//        myTickSoundTool.play();
//        inboundRingingResponseDelayValue = inboundRingingResponseDelaySlider.getValue();
        inboundRingingResponseDelayValue.setText(Integer.toString(inboundRingingResponseDelaySlider.getValue()));
    }//GEN-LAST:event_inboundRingingResponseDelaySliderStateChanged

    private void inboundRingingResponseBusyRatioSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_inboundRingingResponseBusyRatioSliderStateChanged
        inboundRingingResponseBusyRatioValue.setText(Integer.toString(inboundRingingResponseBusyRatioSlider.getValue()));
    }//GEN-LAST:event_inboundRingingResponseBusyRatioSliderStateChanged

    private void inboundEndDelaySliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_inboundEndDelaySliderStateChanged
        inboundEndDelayValue.setText(Integer.toString(inboundEndDelaySlider.getValue()));
    }//GEN-LAST:event_inboundEndDelaySliderStateChanged

    private void runCampaignToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runCampaignToggleButtonActionPerformed
	if (runCampaignToggleButton.isSelected())
        {
            if (callCenterIsNetManaged)
            {
                runCampaign(campaign.getId());
            }
            else
            {
                runCampaign(Integer.parseInt(campaignComboBox.getSelectedItem().toString()));
            }
        }
    }//GEN-LAST:event_runCampaignToggleButtonActionPerformed

    /**
     *
     * @param campaignIdParam
     */
    public void runCampaign(int campaignIdParam)
    {
        callCenterStatus = LOADCAMPAIGN;
        runCampaignToggleButton.removeActionListener(runCampaignToggleButton.getActionListeners()[0]);
        final int campaignId = campaignIdParam;
        Thread outboundCallButtonActionPerformedThread7 = new Thread( allThreadsGroup, new Runnable()
        {
            @Override
            @SuppressWarnings({"static-access", "static-access", "static-access", "static-access", "static-access", "static-access", "static-access"})
            public void run()
            {
                String[] status = new String[2];
//              Prepare the Campaign Run loading the data objects
                campaignStopRequested = false;
                campaign = dbClient.loadCampaignFromOrderId(campaignId);

                order = dbClient.selectCustomerOrder(campaign.getOrderId());
                lastMessageDuration = order.getMessageDuration();

                // Load the Campaign Destinations
                destinationArray = dbClient.selectAllOpenCampaignDestinations(campaignId);

// Get the saved campaignStat record
                int onAC = campaignStat.getOnAC();int idleAC = campaignStat.getIdleAC(); campaignStat = dbClient.selectCampaignStat(campaignId); campaignStat.setOnAC(onAC); campaignStat.setIdleAC(idleAC); campaignStat.resetActiveCounts();
                try { lastTimeDashboardCampaignStat = (CampaignStat) campaignStat.clone(); } catch (CloneNotSupportedException ex) { /* Nonsens in this case*/ } // Make sure there is no difference between this and lastCampaignStat (prevent dashboard going wild on first run)
                if (campaignStat.getConnectingTT() == 0) { campaign.setCalendarRegisteredStart(Calendar.getInstance(nlLocale)); dbClient.updateCampaign(campaign); } // First run setting starttime

                soundFileToStream = order.getMessageFilename();
                toegangField.setText(usernameField.getText());
//                durationCallsEpochTime = 0;
                outboundCallsInProgress = true;
                callCenterIsOutBound = true;
                campaignProgressBar.setEnabled(true);
                runCampaignToggleButton.setEnabled(true); stopCampaignButton.setEnabled(true);
                campaignProgressBar.setValue(0);

                callRatioChartData.setValue("Connecting", 0);
                callRatioChartData.setValue("Trying", 0);
                callRatioChartData.setValue("Busy", 0);
                callRatioChartData.setValue("Success", 0);
                graphInnerPanel.setVisible(true); chartPanel.setVisible(true);

                turnoverStatsTable.setValueAt(0, 0, 2);
                turnoverStatsTable.setValueAt(0, 1, 2);
                turnoverStatsTable.setValueAt(0, 2, 2);

                // Scheduled Start
                campaignLabel.setText("Campaign " + campaign.getId());
                if (campaign.getCalendarScheduledStart().getTimeInMillis() != 0)
                {
                    campaignTable.setValueAt(
                                                    String.format("%04d", campaign.getCalendarScheduledStart().get(Calendar.YEAR)) + "-" +
                                                    String.format("%02d", campaign.getCalendarScheduledStart().get(Calendar.MONTH) + 1) + "-" +
                                                    String.format("%02d", campaign.getCalendarScheduledStart().get(Calendar.DAY_OF_MONTH)) + " " +
                                                    String.format("%02d", campaign.getCalendarScheduledStart().get(Calendar.HOUR_OF_DAY)) + ":" +
                                                    String.format("%02d", campaign.getCalendarScheduledStart().get(Calendar.MINUTE)) + ":" +
                                                    String.format("%02d", campaign.getCalendarScheduledStart().get(Calendar.SECOND))
                                                    , 0, 1);
                }
                // Scheduled End
                if (campaign.getCalendarScheduledEnd().getTimeInMillis() != 0)
                {
                    campaignTable.setValueAt(
                                                    String.format("%04d", campaign.getCalendarScheduledEnd().get(Calendar.YEAR)) + "-" +
                                                    String.format("%02d", campaign.getCalendarScheduledEnd().get(Calendar.MONTH) + 1) + "-" +
                                                    String.format("%02d", campaign.getCalendarScheduledEnd().get(Calendar.DAY_OF_MONTH)) + " " +
                                                    String.format("%02d", campaign.getCalendarScheduledEnd().get(Calendar.HOUR_OF_DAY)) + ":" +
                                                    String.format("%02d", campaign.getCalendarScheduledEnd().get(Calendar.MINUTE)) + ":" +
                                                    String.format("%02d", campaign.getCalendarScheduledEnd().get(Calendar.SECOND))
                                                    , 1, 1);
                }
                // Expect Start
                if (campaign.getCalendarExpectedStart().getTimeInMillis() != 0)
                {
                    campaignTable.setValueAt(
                                                    String.format("%04d", campaign.getCalendarExpectedStart().get(Calendar.YEAR)) + "-" +
                                                    String.format("%02d", campaign.getCalendarExpectedStart().get(Calendar.MONTH) + 1) + "-" +
                                                    String.format("%02d", campaign.getCalendarExpectedStart().get(Calendar.DAY_OF_MONTH)) + " " +
                                                    String.format("%02d", campaign.getCalendarExpectedStart().get(Calendar.HOUR_OF_DAY)) + ":" +
                                                    String.format("%02d", campaign.getCalendarExpectedStart().get(Calendar.MINUTE)) + ":" +
                                                    String.format("%02d", campaign.getCalendarExpectedStart().get(Calendar.SECOND))
                                                    , 2, 1);
                }
                // Expect End
                if (campaign.getCalendarExpectedEnd().getTimeInMillis() != 0)
                {
                    campaignTable.setValueAt(
                                                    String.format("%04d", campaign.getCalendarExpectedEnd().get(Calendar.YEAR)) + "-" +
                                                    String.format("%02d", campaign.getCalendarExpectedEnd().get(Calendar.MONTH) + 1) + "-" +
                                                    String.format("%02d", campaign.getCalendarExpectedEnd().get(Calendar.DAY_OF_MONTH)) + " " +
                                                    String.format("%02d", campaign.getCalendarExpectedEnd().get(Calendar.HOUR_OF_DAY)) + ":" +
                                                    String.format("%02d", campaign.getCalendarExpectedEnd().get(Calendar.MINUTE)) + ":" +
                                                    String.format("%02d", campaign.getCalendarExpectedEnd().get(Calendar.SECOND))
                                                    , 3, 1);
                }
                // Registered Start
                if (campaign.getCalendarRegisteredStart().getTimeInMillis() != 0)
                {
                    campaignTable.setValueAt(
                                                    String.format("%04d", campaign.getCalendarRegisteredStart().get(Calendar.YEAR)) + "-" +
                                                    String.format("%02d", campaign.getCalendarRegisteredStart().get(Calendar.MONTH) + 1) + "-" +
                                                    String.format("%02d", campaign.getCalendarRegisteredStart().get(Calendar.DAY_OF_MONTH)) + " " +
                                                    String.format("%02d", campaign.getCalendarRegisteredStart().get(Calendar.HOUR_OF_DAY)) + ":" +
                                                    String.format("%02d", campaign.getCalendarRegisteredStart().get(Calendar.MINUTE)) + ":" +
                                                    String.format("%02d", campaign.getCalendarRegisteredStart().get(Calendar.SECOND))
                                                    , 4, 1);
                }
                // Registered End
                if (campaign.getCalendarRegisteredEnd().getTimeInMillis() != 0)
                {
                    campaignTable.setValueAt(
                                                    String.format("%04d", campaign.getCalendarRegisteredEnd().get(Calendar.YEAR)) + "-" +
                                                    String.format("%02d", campaign.getCalendarRegisteredEnd().get(Calendar.MONTH) + 1) + "-" +
                                                    String.format("%02d", campaign.getCalendarRegisteredEnd().get(Calendar.DAY_OF_MONTH)) + " " +
                                                    String.format("%02d", campaign.getCalendarRegisteredEnd().get(Calendar.HOUR_OF_DAY)) + ":" +
                                                    String.format("%02d", campaign.getCalendarRegisteredEnd().get(Calendar.MINUTE)) + ":" +
                                                    String.format("%02d", campaign.getCalendarRegisteredEnd().get(Calendar.SECOND))
                                                    , 5, 1);
                }
                // The rest
                campaignTable.setValueAt("-", 6, 1); // Time Tot
                campaignTable.setValueAt("-", 7, 1); // Time Elap
                campaignTable.setValueAt("-", 8, 1); // Time End
                campaignTable.setValueAt("-", 9, 1); // Throughput Calls

// Set the static proxy config

                status          = new String[2];
                username        = configurationCallCenter.getUsername();
//                toegang         = prefixField.getText() + configurationCallCenter.getToegang() + suffixField.getText(); // User for Asterisk
                toegang         = configurationCallCenter.getToegang();
                filename	= "file:" + soundFileToStream;

                String text     = destinationTextArea.getText();

// ==============================================================================================================================

                // Sets the Order Object and after that displays the OrderMembers in the orderTable and turnover info
                orderLabel.setText("Order " + order.getOrderId());
                orderTable.setValueAt(order.getRecipientsCategory(), 0, 1);
//                orderTable.setValueAt(order.getTimeWindowCategory(), 1, 1);
                orderTable.setValueAt(order.getTimeWindow0() + " " + order.getTimeWindow1() + " " + order.getTimeWindow2(), 1, 1);
                orderTable.setValueAt(order.getTargetTransactionQuantity(), 2, 1);
                orderTable.setValueAt(order.getTargetTransactionQuantity(), 2, 1);
                orderTable.setValueAt(order.getMessageDuration() + " Sec", 3, 1);
                orderTable.setValueAt(order.getMessageRatePerSecond() + " / Sec", 4, 1);
                orderTable.setValueAt(order.getMessageRate(), 5, 1);
                orderTable.setValueAt(order.getSubTotal(), 6, 1);
                turnoverStatsTable.setValueAt((float)(order.getTargetTransactionQuantity() * order.getMessageRate()), 2, 2); // Total Turnover

                // Make sure the outboundBurstRateSlider adapts to the message length in relation to the Call / Message Duration when message is longer than 10 seconds
                if (Math.round(order.getMessageDuration() * 100) < (eCallCenterGUI.callSpeedSlider.getMinimum()) ) // Soundfile results below minimum
                {
                    eCallCenterGUI.callSpeedSlider.setMaximum(eCallCenterGUI.callSpeedSlider.getMinimum());
                }
                else
                {
                    eCallCenterGUI.callSpeedSlider.setMaximum(order.getMessageDuration() * 50);
                    eCallCenterGUI.callSpeedSlider.setMajorTickSpacing(Math.round((eCallCenterGUI.callSpeedSlider.getMaximum() - eCallCenterGUI.callSpeedSlider.getMinimum()) / 10));
                    eCallCenterGUI.callSpeedSlider.setPaintLabels(true);
                }
                callSpeedInterval = Math.round(eCallCenterGUI.callSpeedSlider.getMaximum() / 2);
                callSpeedSlider.setValue(callSpeedInterval);
                
//                campaignProgressBar.setMaximum(order.getTargetTransactionQuantity()-1);
                campaignProgressBar.setMaximum(dbClient.getNumberOfAllOpenCampaignDestinations(campaign.getId()));

// This is where the Campaign Re-run loop start
                campaignRerunForLoop: for (int campaignReRunCounter = 1;campaignReRunCounter <= campaignReRunLimit;campaignReRunCounter++)
                {
                    campaignReRunStage = campaignReRunCounter;
                    destinationArray = dbClient.selectAllOpenCampaignDestinations(campaignId);

                    try { configurationSoftPhone = (Configuration) configurationCallCenter.clone(); } // clone the config
                    catch (CloneNotSupportedException ex) { showStatus(ex.getMessage(), true, true); }

                    configurationSoftPhone.setUsername(username);
                    configurationSoftPhone.setToegang(toegang);

    // ==============================================================================================================================

                    destinationsCounter = 0;
                    runCampaignCounter = 0;

                    // Call Queuer
// This is where the Call Loop start
                    campaignRunForLoop: for(Destination destinationElement : destinationArray)
                    {
                        // TimeWindow Protector
                        if (callCenterIsNetManaged)
                        {
//                            if (!TimeWindow.getCurrentTimeWindow().equals(order.getTimeWindowCategory()))

                            boolean legalTimeWindow = false;
                            for (int orderTimewindow : order.getTimewindowIndexArray())
                            {
                                if ( timeTool.getCurrentTimeWindowIndex() == orderTimewindow) { legalTimeWindow = true;}
                            }

                            if ( ! legalTimeWindow)
                            {
                                showStatus          ("Self Destructing: " + Vergunning.PRODUCT + " running outside TimeWindow !", true, true);
                                try { Thread.sleep(5000); } catch (InterruptedException ex) { }
                                System.exit(0);
                            }                            
                        }
                        
                        if (campaignStopRequested)
                        {
                            runCampaignToggleButton.addActionListener(new java.awt.event.ActionListener()
                            {   @Override public void actionPerformed(java.awt.event.ActionEvent evt) { runCampaignToggleButtonActionPerformed(evt); } });
                            runCampaignToggleButton.setSelected(false);
                            showStatus("Campaign " + campaign.getId() + " Stopped by user.", true, true);
                            runCampaignToggleButton.setText("►"); runCampaignToggleButton.setForeground(Color.BLACK);
                            callCenterStatus = STOPPED;

                            campaignProgressBar.setValue(0);
                            campaignProgressBar.setEnabled(false);
                            outboundCallsInProgress = false;
                            phoneStatsTable.setValueAt("-", 1, 1); // ProcessingInstance

                            // Campaign is ready updating open campaignlist
                            String[] openCampaigns = dbClient.getOpenCampaigns();
                            if ((openCampaigns != null) && (openCampaigns.length > 0))
                            {
                                if (!callCenterIsNetManaged)
                                {
                                    campaignComboBox.setModel(new javax.swing.DefaultComboBoxModel(openCampaigns));
                                    campaignComboBox.setEnabled(true);
                                }
                            }
                            else
                            {
                                campaignComboBox.setEnabled(false);
                                runCampaignToggleButton.setEnabled(false);
                                stopCampaignButton.setEnabled(false);
                            }
                            return;
                        }
                        
                        destinationElement.resetTimestamps(); dbClient.updateDestination(destinationElement); // Makes sure progresstimestamps are reset  on e.g. second campaign round
                        if (runCampaignCounter == outboundSoftPhonesAvailable )
                        {
                            runCampaignCounter = 0;
                        } // This actually makes the loop roundrobin connecting the end with the beginning

                        // Overheat Protector
                        while(
//                                (
//                                    (order.getTimeWindowCategory().equals(TimeWindow.getDAYTIME_DECRIPTION())) &&
//                                    (currentTimeCalendar.get(Calendar.HOUR_OF_DAY)  == TimeWindow.getDAYTIMEENDHOUR()) &&
//                                    (currentTimeCalendar.get(Calendar.MINUTE)       == TimeWindow.getDAYTIMEENDMINUTE())
//                                )                                                                                           ||
//                                (
//                                    (order.getTimeWindowCategory().equals(TimeWindow.getEVENING_DECRIPTION())) &&
//                                    (currentTimeCalendar.get(Calendar.HOUR_OF_DAY)  == TimeWindow.getEVENINGENDHOUR()) &&
//                                    (currentTimeCalendar.get(Calendar.MINUTE)       == TimeWindow.getEVENINGENDMINUTE())
//                                )                                                                                           ||

                                (
                                    (currentTimeCalendar.get(Calendar.HOUR_OF_DAY)  == timeTool.getCurrentTimeWindow().getEndHour()) &&
                                    (currentTimeCalendar.get(Calendar.MINUTE)       == timeTool.getCurrentTimeWindow().getEndMinute())
                                )                                                                                           ||

                                ( vmUsage                               >= vmUsagePauseThreashold)                                ||
                                ( memFree                               <= memFreeThreshold )                               ||
                                ( heapMemFree                           <= heapMemFreeThreshold )                           ||
                                ( campaignStat.getConnectingAC()        >= connectingTallyLimit )                           ||
                                ( campaignStat.getCallingAC()           >= callingTallyLimit )                              ||
                                ( campaignStat.getTalkingAC()           >= establishedTallyLimit )                          ||
                                ( ! runCampaignToggleButton.isSelected() )                                                  ||
                                ( ! powerToggleButton.isSelected() )
                             )
                        {
                            callCenterStatus = PAUSING;
                            showStatus("Campaign: " + campaign.getId() + " Run: " + campaignReRunCounter + "-" + campaignReRunLimit + " Pausing...", false, false);
                            runCampaignToggleButton.setText("❙ ❙");  runCampaignToggleButton.setForeground(Color.ORANGE);
                            if ( vmUsage                            >= vmUsagePauseThreashold )            { eCallCenterGUI.vmUsageThresholdLabel.setForeground(Color.RED); eCallCenterGUI.vmUsagePauseValue.setForeground(Color.RED); eCallCenterGUI.vmUsageThresholdSlider.setForeground(Color.RED); }
                                                                                                else { eCallCenterGUI.vmUsageThresholdLabel.setForeground(Color.BLACK); eCallCenterGUI.vmUsagePauseValue.setForeground(Color.BLACK); eCallCenterGUI.vmUsageThresholdSlider.setForeground(Color.BLACK); }
                            if ( memFree                            <= memFreeThreshold )            { eCallCenterGUI.memFreeThresholdLabel.setForeground(Color.RED); eCallCenterGUI.memFreeThresholdValue.setForeground(Color.RED); eCallCenterGUI.memFreeThresholdSlider.setForeground(Color.RED); }
                                                                                                else { eCallCenterGUI.memFreeThresholdLabel.setForeground(Color.BLACK); eCallCenterGUI.memFreeThresholdValue.setForeground(Color.BLACK); eCallCenterGUI.memFreeThresholdSlider.setForeground(Color.BLACK); }
                            if ( heapMemFree                        <= heapMemFreeThreshold )        { eCallCenterGUI.heapMemFreeThresholdLabel.setForeground(Color.RED); eCallCenterGUI.heapMemFreeThresholdValue.setForeground(Color.RED); eCallCenterGUI.heapMemFreeThresholdSlider.setForeground(Color.RED); }
                                                                                                else { eCallCenterGUI.heapMemFreeThresholdLabel.setForeground(Color.BLACK); eCallCenterGUI.heapMemFreeThresholdValue.setForeground(Color.BLACK); eCallCenterGUI.heapMemFreeThresholdSlider.setForeground(Color.BLACK); }
                            if ( campaignStat.getConnectingAC()     >= connectingTallyLimit )	     { eCallCenterGUI.connectingTallyLimitLabel.setForeground(Color.RED); eCallCenterGUI.connectingTallyLimitValue.setForeground(Color.RED); eCallCenterGUI.connectingTallyLimitSlider.setForeground(Color.RED); }
                                                                                                else { eCallCenterGUI.connectingTallyLimitLabel.setForeground(Color.BLACK); eCallCenterGUI.connectingTallyLimitValue.setForeground(Color.BLACK); eCallCenterGUI.connectingTallyLimitSlider.setForeground(Color.BLACK); }
                            if ( campaignStat.getCallingAC()        >= callingTallyLimit )	     { eCallCenterGUI.callingTallyLimitLabel.setForeground(Color.RED); eCallCenterGUI.callingTallyLimitValue.setForeground(Color.RED); eCallCenterGUI.callingTallyLimitSlider.setForeground(Color.RED); }
                                                                                                else { eCallCenterGUI.callingTallyLimitLabel.setForeground(Color.BLACK); eCallCenterGUI.callingTallyLimitValue.setForeground(Color.BLACK); eCallCenterGUI.callingTallyLimitSlider.setForeground(Color.BLACK); }
                            if ( campaignStat.getTalkingAC()        >= establishedTallyLimit )	     { eCallCenterGUI.establishedTallyLimitLabel.setForeground(Color.RED); eCallCenterGUI.establishedTallyLimitValue.setForeground(Color.RED); eCallCenterGUI.establishedTallyLimitSlider.setForeground(Color.RED); }
                                                                                                else { eCallCenterGUI.establishedTallyLimitLabel.setForeground(Color.BLACK); eCallCenterGUI.establishedTallyLimitValue.setForeground(Color.BLACK); eCallCenterGUI.establishedTallyLimitSlider.setForeground(Color.BLACK); }
                            try { Thread.sleep(outboundBurstDelay); } catch (InterruptedException ex) {}
                        }
                        
                        if ( destinationElement.getDestinationCount() > vergunning.getMaxCalls()) { break; }
//                        if ((order.getTargetTransactionQuantity() / 100) != 0) {campaignProgressPercentage = Math.round(destinationElement.getDestinationCount() / (order.getTargetTransactionQuantity() / 100));}
                        if ((order.getTargetTransactionQuantity() / 100) != 0) {campaignProgressPercentage = Math.round(destinationsCounter / (order.getTargetTransactionQuantity() / 100));}
                        showStatus("Campaign: " + Integer.toString(campaign.getId()) + " Run: " + Integer.toString(campaignReRunCounter) + "-" + Integer.toString(campaignReRunLimit) + " " + icons.getTalkChar() + destinationElement.getDestination() + " (" + Integer.toString(campaignProgressPercentage) + "%) [" + Integer.toString(destinationElement.getDestinationCount()) + "-" + Integer.toString(order.getTargetTransactionQuantity()) + "]", false, false);

    //                    float cumTurnoverPrecise = destinationsCounter * order.getMessageRate();
                        float cumTurnoverPrecise = campaignStat.getCallingTT() * order.getMessageRate();
                        float cumTurnoverRounded = (float)(Math.round(cumTurnoverPrecise*100.0) / 100.0);
                        turnoverStatsTable.setValueAt(cumTurnoverRounded, 1, 2); // Cummulative Turnover

//                        campaignProgressBar.setValue(destinationElement.getDestinationCount());
//                        campaignProgressBar.setValue(destinationsCounter);
                        campaignProgressBar.setValue(campaignStat.getCallingTT());
                        runCampaignToggleButton.setText("►"); runCampaignToggleButton.setForeground(Color.GREEN);
                        callCenterStatus = RUNNING;
                        eCallCenterGUI.vmUsageThresholdLabel.setForeground(Color.BLACK); eCallCenterGUI.vmUsagePauseValue.setForeground(Color.BLACK); eCallCenterGUI.vmUsageThresholdSlider.setForeground(Color.BLACK);
                        eCallCenterGUI.memFreeThresholdLabel.setForeground(Color.BLACK); eCallCenterGUI.memFreeThresholdValue.setForeground(Color.BLACK); eCallCenterGUI.memFreeThresholdSlider.setForeground(Color.BLACK);
                        eCallCenterGUI.heapMemFreeThresholdLabel.setForeground(Color.BLACK); eCallCenterGUI.heapMemFreeThresholdValue.setForeground(Color.BLACK); eCallCenterGUI.heapMemFreeThresholdSlider.setForeground(Color.BLACK);
                        eCallCenterGUI.connectingTallyLimitLabel.setForeground(Color.BLACK); eCallCenterGUI.connectingTallyLimitValue.setForeground(Color.BLACK); eCallCenterGUI.connectingTallyLimitSlider.setForeground(Color.BLACK);
                        eCallCenterGUI.callingTallyLimitLabel.setForeground(Color.BLACK); eCallCenterGUI.callingTallyLimitValue.setForeground(Color.BLACK); eCallCenterGUI.callingTallyLimitSlider.setForeground(Color.BLACK);
                        eCallCenterGUI.establishedTallyLimitLabel.setForeground(Color.BLACK); eCallCenterGUI.establishedTallyLimitValue.setForeground(Color.BLACK); eCallCenterGUI.establishedTallyLimitSlider.setForeground(Color.BLACK);

//                        outboundSoftPhoneInstance = (SoftPhone) threadArray[runCampaignCounter];
    // -- Make PhoneCall
                        if ((destinationElement.getDestination().length() != 0)&&(destinationElement.getDestination().length() <= vergunning.getDestinationDigits())) // If destination / phonenumber is larger than 0 bytes
                        {
                            try // If destination / phonenumber is larger than 0 bytes
                            {
                                final Destination callDestination = (Destination) destinationElement.clone();
                                Thread campaignCallThread = new Thread(allThreadsGroup, new Runnable()
                                {

                                    @Override
                                    public void run()
                                    {
                                        SoftPhone thisSoftPhone = (SoftPhone) threadArray[runCampaignCounter]; // work from a copy softphone reference as the call loop carries on
                                        int callMode = 0; if (scanCheckBox.isSelected()) { callMode = SCANNING; } else { callMode = CALLING; }
                                        thisSoftPhone.setDestination(callDestination); // Load the phonenumber into the softphone instance before calling

                                        String[] status2 = new String[2]; status2[0]="0"; status2[1]=""; status2 = thisSoftPhone.userInput(CALLBUTTON, callDestination.getDestination(), filename, Integer.toString(callMode));
                                        if (status2[0].equals("1"))
                                        {
                                            // Starting Instant SelfHealing Mechanism (I know it's not a mechanism, but it sounds so much better than automation)
                                            if (thisSoftPhone.getSipState() != thisSoftPhone.SIPSTATE_IDLE)
                                            {
                                                showStatus(icons.getIdleChar() + " " + thisSoftPhone.getInstanceId() + " Unexpected Sipstatus: " + thisSoftPhone.SIPSTATE_DESCRIPTION[thisSoftPhone.getSipState()] + "...", true, true);
                                                if (thisSoftPhone.getSipState() > thisSoftPhone.SIPSTATE_IDLE)
                                                {
                                                    String[] status3 = new String[2]; status3[0] = ""; status3[1] = ""; status3 = thisSoftPhone.stopListener();
                                                    if (status3[0].equals("0"))
                                                    {
                                                        showStatus(icons.getIdleChar() + " " + thisSoftPhone.getInstanceId() + " Listener Stopped Successfully to Sipstate: " + thisSoftPhone.SIPSTATE_DESCRIPTION[thisSoftPhone.getSipState()], true, true);
                                                    }
                                                    else
                                                    {
                                                        showStatus(icons.getIdleChar() + " " + thisSoftPhone.getInstanceId() + " Listener Stopped Unsuccessfully to Sipstate: " + thisSoftPhone.SIPSTATE_DESCRIPTION[thisSoftPhone.getSipState()], true, true);
                                                    }
                                                }
                                                if (thisSoftPhone.getSipState() < thisSoftPhone.SIPSTATE_IDLE)
                                                {
                                                    String[] status4 = new String[2]; status4[0] = ""; status4[1] = ""; status4 = thisSoftPhone.startListener(thisSoftPhone.getConfiguration().getClientPort());
                                                    if (status4[0].equals("0"))
                                                    {
                                                        showStatus(icons.getIdleChar() + " " + thisSoftPhone.getInstanceId() + " Listener Started Successfully to Sipstate: " + thisSoftPhone.SIPSTATE_DESCRIPTION[thisSoftPhone.getSipState()], true, true);
                                                        thisSoftPhone.userInput(CALLBUTTON, callDestination.getDestination(), filename, Integer.toString(callMode));
                                                    }
                                                    else
                                                    {
                                                        showStatus(icons.getIdleChar() + " " + thisSoftPhone.getInstanceId() + " Listener Started Unsuccessfully to Sipstate: " + thisSoftPhone.SIPSTATE_DESCRIPTION[thisSoftPhone.getSipState()], true, true);
                                                    }
                                                }
                                            }
                                            else // The softphone is okay, so make the call
                                            {
                                                thisSoftPhone.userInput(CALLBUTTON, callDestination.getDestination(), filename, Integer.toString(callMode));
                                            }
                                        }
                                    }
                                });
                                campaignCallThread.setName("campaignCallThread");
                                campaignCallThread.setDaemon(runThreadsAsDaemons);
                                campaignCallThread.start();
                            } catch (CloneNotSupportedException ex) {}
                        }

    // -- End of Valid Destinstion Call Routine

                        phoneStatsTable.setValueAt(runCampaignCounter + 1, 1, 1); // ProcessingInstance
                        try { Thread.sleep(outboundBurstDelay); } catch (InterruptedException ex) {}
                        destinationsCounter++;
                        runCampaignCounter++;
                    } // CampaignRun Loop
                    
                    // Wait until all phone become available again or 1 minute has passed
                    callCenterStatus = RERUNBREAK;
                    int reRunBreakCounter = 60;
                    while (( campaignStat.getIdleAC() < outboundSoftPhonesAvailable ) && (reRunBreakCounter > 0) && (!campaignStopRequested))
                    {
                        showStatus("Campaign: " + campaign.getId() + " ReRun: " + campaignReRunCounter + " Break, Waiting Max: " + reRunBreakCounter + " seconds...", false, false);
                        try { Thread.sleep(1000); } catch (InterruptedException ex) {}
                        reRunBreakCounter--;
                    }
                } // CampaignReRuns Loop

                runCampaignToggleButton.addActionListener(new java.awt.event.ActionListener()
                {   @Override public void actionPerformed(java.awt.event.ActionEvent evt) { runCampaignToggleButtonActionPerformed(evt); } });
                runCampaignToggleButton.setSelected(false);
                showStatus("Campaign Completed...", true, true);
                runCampaignToggleButton.setText("►"); runCampaignToggleButton.setForeground(Color.BLACK);

                campaignProgressBar.setValue(0);
                campaignProgressBar.setEnabled(false);
                outboundCallsInProgress = false;
                phoneStatsTable.setValueAt("-", 1, 1); // ProcessingInstance

                // Writing Completion of campaign to database
                campaign.setCalendarRegisteredEnd(Calendar.getInstance(nlLocale));
                dbClient.updateCampaign(campaign);
                campaignTable.setValueAt(
                                                String.format("%04d", campaign.getCalendarRegisteredEnd().get(Calendar.YEAR)) + "-" +
                                                String.format("%02d", campaign.getCalendarRegisteredEnd().get(Calendar.MONTH) + 1) + "-" +
                                                String.format("%02d", campaign.getCalendarRegisteredEnd().get(Calendar.DAY_OF_MONTH)) + " " +
                                                String.format("%02d", campaign.getCalendarRegisteredEnd().get(Calendar.HOUR_OF_DAY)) + ":" +
                                                String.format("%02d", campaign.getCalendarRegisteredEnd().get(Calendar.MINUTE)) + ":" +
                                                String.format("%02d", campaign.getCalendarRegisteredEnd().get(Calendar.SECOND))
                                                , 5, 1);

                // Campaign is ready updating open campaignlist
                String[] openCampaigns = dbClient.getOpenCampaigns();
                if ((openCampaigns != null) && (openCampaigns.length > 0))
                {
                    campaignComboBox.setModel(new javax.swing.DefaultComboBoxModel(openCampaigns));
                    campaignComboBox.setEnabled(true);
                } else {campaignComboBox.setEnabled(false); runCampaignToggleButton.setEnabled(false); stopCampaignButton.setEnabled(false);}

                if (autoPowerOff) {System.exit(0);}
                return;
            }
        });
        outboundCallButtonActionPerformedThread7.setName("outboundCallButtonActionPerformedThread7");
        outboundCallButtonActionPerformedThread7.setDaemon(runThreadsAsDaemons);
        outboundCallButtonActionPerformedThread7.start();
    }

    private void phoneButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_phoneButtonActionPerformed
        startEPhoneGUI();
        //        Thread startPhoneThread = new Thread( allThreadsGroup, new Runnable()
//        {
//            @Override
//            public void run()
//            {
//                java.awt.EventQueue.invokeLater(new Runnable()
//                {
//                    @Override
//                    public void run() {
//                        EPhoneGUI mySoftPhoneGUI = new EPhoneGUI();
//                        mySoftPhoneGUI.setVisible(true);
//                    }
//                });
//            }
//        });
//        startPhoneThread.setName("startPhoneThread");
//        startPhoneThread.setDaemon(runThreadsAsDaemons);
//        startPhoneThread.start();
    }//GEN-LAST:event_phoneButtonActionPerformed

    private void callCenterPanelKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_callCenterPanelKeyPressed
	if (evt.getKeyCode() == 9) { callCenterPanel.getNextFocusableComponent().requestFocusInWindow(); }
    }//GEN-LAST:event_callCenterPanelKeyPressed

    private void statisticsPanelKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_statisticsPanelKeyPressed
	if (evt.getKeyCode() == 9) { statisticsPanel.getNextFocusableComponent().requestFocusInWindow(); }
    }//GEN-LAST:event_statisticsPanelKeyPressed

    private void graphPanelKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_graphPanelKeyPressed
	if (evt.getKeyCode() == 9) { graphPanel.getNextFocusableComponent().requestFocusInWindow(); }
    }//GEN-LAST:event_graphPanelKeyPressed

    private void phoneDisplayTabPanelKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_phoneDisplayTabPanelKeyPressed
	if (evt.getKeyCode() == 9) { phoneDisplayTabPanel.getNextFocusableComponent().requestFocusInWindow(); }
    }//GEN-LAST:event_phoneDisplayTabPanelKeyPressed

    private void netConfigPanelKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_netConfigPanelKeyPressed
	if (evt.getKeyCode() == 9) { netConfigPanel.getNextFocusableComponent().requestFocusInWindow(); }
    }//GEN-LAST:event_netConfigPanelKeyPressed

    private void phonesPoolTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_phonesPoolTableMouseClicked
        if (evt.getClickCount() == 2)
        {
            myCoordinate = new Coordinate(phonesPoolTable.getSelectedRow(),phonesPoolTable.getSelectedColumn());
            final int selectedSoftPhoneInstance = getSoftPhoneInstance(myCoordinate); // Calculate the SoftPhone instance according the selected coordinate
            final SoftPhone softPhoneInstance = (SoftPhone) threadArray[selectedSoftPhoneInstance]; // Get the related SoftPhone instance reference
            softPhoneInstance.setEPhoneGUIActive(true);

            Thread selectPhoneThread = new Thread( allThreadsGroup, new Runnable()
            {
                @Override
                public void run()
                {
                    java.awt.EventQueue.invokeLater(new Runnable()
                    {
                        @Override
                        public void run() {
                            EPhone mySoftPhoneGUI = new EPhone(softPhoneInstance); // SoftPhoneGUI usually tied up with new SoftPhone instance, but in this case it's not a new instance it's an existing instance
                            mySoftPhoneGUI.setVisible(true);
                            softPhoneInstance.setUserInterface2(mySoftPhoneGUI);
                            softPhoneInstance.updateDisplay();
                        }
                    });
                }
            });
            selectPhoneThread.setName("selectPhoneThread");
            selectPhoneThread.setDaemon(runThreadsAsDaemons);
            selectPhoneThread.setPriority(4);
            selectPhoneThread.start();            
        }
    }//GEN-LAST:event_phonesPoolTableMouseClicked

    private void campaignComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_campaignComboBoxActionPerformed
        // Get the selected objects
        int campaignId = Integer.parseInt(campaignComboBox.getSelectedItem().toString());
        loadCampaign(campaignId);
    }//GEN-LAST:event_campaignComboBoxActionPerformed

    private void loadCampaign(int campaignIdParam)
    {
        campaign = dbClient.loadCampaignFromOrderId(campaignIdParam);
        order = dbClient.selectCustomerOrder(campaign.getOrderId());

        // Sets the Order Object and after that displays the OrderMembers in the orderTable and turnover info
        orderLabel.setText("Order " + order.getOrderId());
        orderTable.setValueAt(order.getRecipientsCategory(), 0, 1);
//        orderTable.setValueAt(order.getTimeWindowCategory(), 1, 1);
        orderTable.setValueAt(order.getTimeWindow0() + " " + order.getTimeWindow1() + " " + order.getTimeWindow2(), 1, 1);
        orderTable.setValueAt(order.getTargetTransactionQuantity(), 2, 1);
        orderTable.setValueAt(order.getTargetTransactionQuantity(), 2, 1);
        orderTable.setValueAt(order.getMessageDuration() + " Sec", 3, 1);
        orderTable.setValueAt(order.getMessageRatePerSecond() + " / Sec", 4, 1);
        orderTable.setValueAt(order.getMessageRate(), 5, 1);
        orderTable.setValueAt(order.getSubTotal(), 6, 1);
        turnoverStatsTable.setValueAt((float)(order.getTargetTransactionQuantity() * order.getMessageRate()), 2, 2); // Total Turnover

        // Scheduled Start
        campaignLabel.setText("Campaign " + campaign.getId());
        if (campaign.getCalendarScheduledStart().getTimeInMillis() != 0)
        {
            campaignTable.setValueAt(
                                            String.format("%04d", campaign.getCalendarScheduledStart().get(Calendar.YEAR)) + "-" +
                                            String.format("%02d", campaign.getCalendarScheduledStart().get(Calendar.MONTH) + 1) + "-" +
                                            String.format("%02d", campaign.getCalendarScheduledStart().get(Calendar.DAY_OF_MONTH)) + " " +
                                            String.format("%02d", campaign.getCalendarScheduledStart().get(Calendar.HOUR_OF_DAY)) + ":" +
                                            String.format("%02d", campaign.getCalendarScheduledStart().get(Calendar.MINUTE)) + ":" +
                                            String.format("%02d", campaign.getCalendarScheduledStart().get(Calendar.SECOND))
                                            , 0, 1);
        }
        // Scheduled End
        if (campaign.getCalendarScheduledEnd().getTimeInMillis() != 0)
        {
            campaignTable.setValueAt(
                                            String.format("%04d", campaign.getCalendarScheduledEnd().get(Calendar.YEAR)) + "-" +
                                            String.format("%02d", campaign.getCalendarScheduledEnd().get(Calendar.MONTH) + 1) + "-" +
                                            String.format("%02d", campaign.getCalendarScheduledEnd().get(Calendar.DAY_OF_MONTH)) + " " +
                                            String.format("%02d", campaign.getCalendarScheduledEnd().get(Calendar.HOUR_OF_DAY)) + ":" +
                                            String.format("%02d", campaign.getCalendarScheduledEnd().get(Calendar.MINUTE)) + ":" +
                                            String.format("%02d", campaign.getCalendarScheduledEnd().get(Calendar.SECOND))
                                            , 1, 1);
        }
        // Expect Start
        if (campaign.getCalendarExpectedStart().getTimeInMillis() != 0)
        {
            campaignTable.setValueAt(
                                            String.format("%04d", campaign.getCalendarExpectedStart().get(Calendar.YEAR)) + "-" +
                                            String.format("%02d", campaign.getCalendarExpectedStart().get(Calendar.MONTH) + 1) + "-" +
                                            String.format("%02d", campaign.getCalendarExpectedStart().get(Calendar.DAY_OF_MONTH)) + " " +
                                            String.format("%02d", campaign.getCalendarExpectedStart().get(Calendar.HOUR_OF_DAY)) + ":" +
                                            String.format("%02d", campaign.getCalendarExpectedStart().get(Calendar.MINUTE)) + ":" +
                                            String.format("%02d", campaign.getCalendarExpectedStart().get(Calendar.SECOND))
                                            , 2, 1);
        }
        // Expect End
        if (campaign.getCalendarExpectedEnd().getTimeInMillis() != 0)
        {
            campaignTable.setValueAt(
                                            String.format("%04d", campaign.getCalendarExpectedEnd().get(Calendar.YEAR)) + "-" +
                                            String.format("%02d", campaign.getCalendarExpectedEnd().get(Calendar.MONTH) + 1) + "-" +
                                            String.format("%02d", campaign.getCalendarExpectedEnd().get(Calendar.DAY_OF_MONTH)) + " " +
                                            String.format("%02d", campaign.getCalendarExpectedEnd().get(Calendar.HOUR_OF_DAY)) + ":" +
                                            String.format("%02d", campaign.getCalendarExpectedEnd().get(Calendar.MINUTE)) + ":" +
                                            String.format("%02d", campaign.getCalendarExpectedEnd().get(Calendar.SECOND))
                                            , 3, 1);
        }
        // Registered Start
        if (campaign.getCalendarRegisteredStart().getTimeInMillis() != 0)
        {
            campaignTable.setValueAt(
                                            String.format("%04d", campaign.getCalendarRegisteredStart().get(Calendar.YEAR)) + "-" +
                                            String.format("%02d", campaign.getCalendarRegisteredStart().get(Calendar.MONTH) + 1) + "-" +
                                            String.format("%02d", campaign.getCalendarRegisteredStart().get(Calendar.DAY_OF_MONTH)) + " " +
                                            String.format("%02d", campaign.getCalendarRegisteredStart().get(Calendar.HOUR_OF_DAY)) + ":" +
                                            String.format("%02d", campaign.getCalendarRegisteredStart().get(Calendar.MINUTE)) + ":" +
                                            String.format("%02d", campaign.getCalendarRegisteredStart().get(Calendar.SECOND))
                                            , 4, 1);
        }
        // Registered End
        if (campaign.getCalendarRegisteredEnd().getTimeInMillis() != 0)
        {
            campaignTable.setValueAt(
                                            String.format("%04d", campaign.getCalendarRegisteredEnd().get(Calendar.YEAR)) + "-" +
                                            String.format("%02d", campaign.getCalendarRegisteredEnd().get(Calendar.MONTH) + 1) + "-" +
                                            String.format("%02d", campaign.getCalendarRegisteredEnd().get(Calendar.DAY_OF_MONTH)) + " " +
                                            String.format("%02d", campaign.getCalendarRegisteredEnd().get(Calendar.HOUR_OF_DAY)) + ":" +
                                            String.format("%02d", campaign.getCalendarRegisteredEnd().get(Calendar.MINUTE)) + ":" +
                                            String.format("%02d", campaign.getCalendarRegisteredEnd().get(Calendar.SECOND))
                                            , 5, 1);
        }
        // The rest
        campaignTable.setValueAt("-", 6, 1); // Time Tot
        campaignTable.setValueAt("-", 7, 1); // Time Elap
        campaignTable.setValueAt("-", 8, 1); // Time End
        campaignTable.setValueAt("-", 9, 1); // Throughput Calls

        if (outboundSoftPhonesAvailable > 0)
        {
            runCampaignToggleButton.setEnabled(true); runCampaignToggleButton.setEnabled(true);
        }        
    }

    private void connectingTallyLimitSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_connectingTallyLimitSliderStateChanged
//	myTickSoundTool.play();
        connectingTallyLimit = connectingTallyLimitSlider.getValue();
        connectingTallyLimitValue.setText(Integer.toString((int) connectingTallyLimit));
    }//GEN-LAST:event_connectingTallyLimitSliderStateChanged

    private void stopCampaignButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopCampaignButtonActionPerformed
        campaignStopRequested = true;
        runCampaignToggleButton.setSelected(true);
    }//GEN-LAST:event_stopCampaignButtonActionPerformed

    private void netManagerInboundServerToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_netManagerInboundServerToggleButtonActionPerformed
        if (netManagerInboundServerToggleButton.isSelected())
        {
            enableInboundNetManagerServer(true);
        }
        else
        {
            enableInboundNetManagerServer(false);
        }
    }//GEN-LAST:event_netManagerInboundServerToggleButtonActionPerformed

    private void netManagerOutboundServerToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_netManagerOutboundServerToggleButtonActionPerformed
        if (netManagerOutboundServerToggleButton.isSelected())
        {
            enableOutboundNetManagerServer(true);
        }
        else
        {
            enableOutboundNetManagerServer(false);
        }

    }//GEN-LAST:event_netManagerOutboundServerToggleButtonActionPerformed

    private void enableInboundNetManagerServer(boolean enableParam)
    {
        if (enableParam)
        {
            inboundNetManagerServer = new NetManagerServer(this, INBOUND_PORT);
            if (!netManagerInboundServerToggleButton.isSelected())
            {
                netManagerInboundServerToggleButton.setSelected(true);
            }
        }
        else
        {
            if (inboundNetManagerServer != null)
            {
                inboundNetManagerServer.stopServer();
                if (netManagerInboundServerToggleButton.isSelected())
                {
                    netManagerInboundServerToggleButton.setSelected(false);
                }
            }
        }
    }

    private void enableOutboundNetManagerServer(boolean enableParam)
    {
        if (enableParam)
        {
            outboundNetManagerServer = new NetManagerServer(this, OUTBOUND_PORT);
            if (!netManagerOutboundServerToggleButton.isSelected())
            {
                netManagerOutboundServerToggleButton.setSelected(true);
            }
        }
        else
        {
            if (outboundNetManagerServer != null)
            {
                outboundNetManagerServer.stopServer();
                if (netManagerOutboundServerToggleButton.isSelected())
                {
                    netManagerOutboundServerToggleButton.setSelected(false);
                }
            }
        }
    }

    private void debugToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_debugToggleButtonActionPerformed
        Thread debugToggleButtonActionPerformedThread = new Thread( allThreadsGroup, new Runnable()
        {
            @Override
            public void run()
            {
                String[] status = new String[2];
		serviceLoopProgressBar.setEnabled(true);
                if (debugToggleButton.isSelected())
                {
//                    myClickOnSoundTool.play();
                    debugCounter = 0;
		    showStatus("Enable Debug SoftPhones...", true, true);
                    while (debugCounter < outboundSoftPhonesAvailable) // Starts looping through the user-range
                    {
                        SoftPhone thisSoftPhoneInstance =  (SoftPhone) threadArray[debugCounter]; // Get the reference to the SoftPhone object in the loop
                        status = thisSoftPhoneInstance.userInput(DEBUGBUTTON, "1", "", ""); // Send a registerButton response to this object's method userInput
                        if (status[0].equals("1")) { showStatus("SoftPhone Debug Error: " + status[1], true, true); }
			phoneStatsTable.setValueAt(debugCounter + 1, 1, 1);
			serviceLoopProgressBar.setValue(debugCounter);
                        try { Thread.sleep(1); } catch (InterruptedException ex) {  }
                        debugCounter++;
                    }
		    showStatus("Enable Debug SoftPhones Completed", true, true);
                    debugToggleButton.setForeground(Color.blue);
                    phoneStatsTable.setValueAt("-", 1, 1);
                }
                else
                {
		    showStatus("Disable Debug SoftPhones...", true, true);
//                    myClickOffSoundTool.play();
                    debugCounter = 0;
                    while (debugCounter < outboundSoftPhonesAvailable) // Starts looping through the user-range
                    {
                        SoftPhone thisSoftPhoneInstance = (SoftPhone) threadArray[debugCounter]; // Get the reference to the SoftPhone object in the loop
                        status = thisSoftPhoneInstance.userInput(DEBUGBUTTON, "0", "", ""); // Send a registerButton response to this object's method userInput
                        if (status[0].equals("1")) { showStatus("SoftPhone Debug Error: " + status[1], true, true); }
			phoneStatsTable.setValueAt(debugCounter + 1, 1, 1); // ProcessingInstance
			serviceLoopProgressBar.setValue(debugCounter);
                        try { Thread.sleep(1); } catch (InterruptedException ex) {  }
                        debugCounter++;
                    }
		    showStatus("Disable Debug SoftPhones Completed", true, true);
                    debugToggleButton.setForeground(Color.black);
                    phoneStatsTable.setValueAt("-", 1, 1);
                }
		serviceLoopProgressBar.setValue(0); serviceLoopProgressBar.setEnabled(false);
                return;
            }
        });
        debugToggleButtonActionPerformedThread.setName("debugToggleButtonActionPerformedThread");
        debugToggleButtonActionPerformedThread.setDaemon(runThreadsAsDaemons);
        debugToggleButtonActionPerformedThread.start();
    }//GEN-LAST:event_debugToggleButtonActionPerformed

    private void vergunningTypeListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_vergunningTypeListMouseClicked
        if (vergunningTypeList.getSelectedIndex() == 0 ) { vergunningPeriodList.setSelectedIndex(0); } else { vergunningPeriodList.setSelectedIndex(1); }
        orderVergunningCode();
}//GEN-LAST:event_vergunningTypeListMouseClicked

    private void vergunningCodeFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vergunningCodeFieldActionPerformed
        if (vergunningCodeField.getText().equals(VERGUNNINGTOEKENNERTOEGANG))
        {
            vergunningCodeField.setText(MD5Converter.getMD5SumFromString(activationCodeField.getText() + VERGUNNINGTOEKENNERTOEGANG));
            vergunningCodeField.setForeground(Color.BLACK);
            licenseCodePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "License Code", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("STHeiti", 0, 10))); // NOI18N
            applyVergunningButton.setEnabled(false);
            applyVergunning();
        }
        else
        {
            licenseCodePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "License Code", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("STHeiti", 0, 10))); // NOI18N
            applyVergunningButton.setEnabled(false);
            applyVergunning();
        }
}//GEN-LAST:event_vergunningCodeFieldActionPerformed

    private void vergunningPeriodListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_vergunningPeriodListMouseClicked
        vergunning.setVergunningPeriod(vergunningPeriodList.getSelectedValue().toString());
        orderVergunningCode();
}//GEN-LAST:event_vergunningPeriodListMouseClicked

    private void vergunningDateChooserPanelOnSelectionChange(datechooser.events.SelectionChangedEvent evt) {//GEN-FIRST:event_vergunningDateChooserPanelOnSelectionChange
        vergunningStartCalendar = vergunningDateChooserPanel.getSelectedDate();
        vergunningStartCalendar.set(Calendar.HOUR_OF_DAY, (int)0);
        vergunningStartCalendar.set(Calendar.MINUTE, (int)0);
        vergunningStartCalendar.set(Calendar.SECOND, (int)0);
        vergunningEndCalendar.setTimeInMillis(vergunningStartCalendar.getTimeInMillis());
        orderVergunningCode();
}//GEN-LAST:event_vergunningDateChooserPanelOnSelectionChange

    private void applyVergunningButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyVergunningButtonActionPerformed
        applyVergunning();
}//GEN-LAST:event_applyVergunningButtonActionPerformed

    private void applyVergunning()
    {
        applyVergunningButton.setEnabled(false);
        vergunning.setActivationCode(activationCodeField.getText());
        vergunning.setVergunningCode(vergunningCodeField.getText());
        vergunning.saveVergunning();
        vergunning.setVergunningOrderInProgress(false);
        executeVergunning();
        if (vergunning.isValid())
        {
            performanceMeter.setCallPerHourScale(0, (vergunning.getCallsPerHour() / 100), (vergunning.getCallsPerHour() / 1000));
            movePerformanceMeter(0, true);
            if ((prefPhoneLinesSlider.getMaximum() == 0) || (prefPhoneLinesSlider.getMaximum() > vergunning.getPhoneLines()))
            {
                prefPhoneLinesSlider.setMaximum(vergunning.getPhoneLines()); prefPhoneLinesSlider.setValue(vergunning.getPhoneLines());
            }
            else
            {
                prefPhoneLinesSlider.setMaximum(vergunning.getPhoneLines()); prefPhoneLinesSlider.setValue(Integer.parseInt(configurationCallCenter.getPrefPhoneLines()));
            }

            Thread validLicenseAppliedThread = new Thread( allThreadsGroup, new Runnable()
            {
                @Override
                public void run()
                {
                    showStatus("Congratulations and thank you for choosing " + Vergunning.BRAND + " " + Vergunning.PRODUCT + ".", true, true);
                    applyVergunningButton.setEnabled(false);
                    requestVergunningButton.setEnabled(false);
                    try { Thread.sleep(1000); } catch (InterruptedException ex) { }
                    tabPane.setSelectedIndex(0);
                    try { Thread.sleep(2000); } catch (InterruptedException ex) { }
                    setImagePanelVisible(false);
                    setPowerOn(true);
                }
            });
            validLicenseAppliedThread.setName("validLicenseAppliedThread");
            validLicenseAppliedThread.setDaemon(runThreadsAsDaemons);
            validLicenseAppliedThread.start();
        }
        else
        {
            prefPhoneLinesSlider.setMaximum(Integer.parseInt(configurationCallCenter.getPrefPhoneLines())); prefPhoneLinesSlider.setValue(Integer.parseInt(configurationCallCenter.getPrefPhoneLines()));
            Thread invalidLicenseAppliedThread = new Thread( allThreadsGroup, new Runnable()
            {
                @Override
                public void run()
                {
                    applyVergunningButton.setEnabled(false);
                    requestVergunningButton.setEnabled(false);
                    //licenseCodeField.setEnabled(false);
                    vergunningCodeField.setEditable(false);
                    vergunningCodeField.setForeground(Color.red);
                    if ( vergunning.getVergunningInvalidReason().length() == 0 )
                    {
                        showStatus("License Code is Invalid due to Internet Connectivity", true, true);
                    }
                    else
                    {
                        showStatus("License Code is Invalid due to " + vergunning.getVergunningInvalidReason() + ". " + vergunning.getVergunningInvalidAdvise(), true, true);
                    }
                    try { Thread.sleep(5000); } catch (InterruptedException ex) { }
                    //licenseCodeField.setEnabled(true);
                    vergunningCodeField.setEditable(true);
                    vergunningCodeField.setForeground(Color.black);
                    licenseCodePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "License Code", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("STHeiti", 0, 10))); // NOI18N
                }
            });
            invalidLicenseAppliedThread.setName("invalidLicenseAppliedThread");
            invalidLicenseAppliedThread.setDaemon(runThreadsAsDaemons);
            invalidLicenseAppliedThread.start();
        }
    }

    private void vergunningCodeFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_vergunningCodeFieldKeyReleased
        if ((vergunningCodeField.getText().length() > 0 ) && (evt.getKeyCode() != 10))
                { applyVergunningButton.setEnabled(true); requestVergunningButton.setEnabled(true); }
        else    { applyVergunningButton.setEnabled(false); requestVergunningButton.setEnabled(false); }
    }//GEN-LAST:event_vergunningCodeFieldKeyReleased

    private void vergunningCodeFieldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_vergunningCodeFieldMouseClicked
        if ( evt.getClickCount() == 2 )
        {
            licenseCodePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "License Authorisation Code", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("STHeiti", 0, 10))); // NOI18N
            vergunningCodeField.setForeground(Color.WHITE);
        }
    }//GEN-LAST:event_vergunningCodeFieldMouseClicked

    private void autoSpeedToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoSpeedToggleButtonActionPerformed
	if ( autoSpeedToggleButton.isSelected() )
	{
	    setAutoSpeed(true);
	}
	else
	{
	    setAutoSpeed(false);
	}
    }//GEN-LAST:event_autoSpeedToggleButtonActionPerformed

    private void aboutPanelKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_aboutPanelKeyPressed
        if (evt.getKeyCode() == 9) { aboutPanel.getNextFocusableComponent().requestFocusInWindow(); }
}//GEN-LAST:event_aboutPanelKeyPressed

    private void logPanelKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_logPanelKeyPressed
        if (evt.getKeyCode() == 9) { logPanel.getNextFocusableComponent().requestFocusInWindow(); }
}//GEN-LAST:event_logPanelKeyPressed

    private void imageLinkLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_imageLinkLabelMouseClicked
        try { java.awt.Desktop.getDesktop().browse(java.net.URI.create(Vergunning.WEBLINK)); } catch (IOException ex) { }
    }//GEN-LAST:event_imageLinkLabelMouseClicked

    private void imageLinkLabelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_imageLinkLabelMouseEntered
        imageLinkLabel.setForeground(Color.darkGray);
    }//GEN-LAST:event_imageLinkLabelMouseEntered

    private void imageLinkLabelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_imageLinkLabelMouseExited
        imageLinkLabel.setForeground(Color.LIGHT_GRAY);
    }//GEN-LAST:event_imageLinkLabelMouseExited

    private void licensePanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_licensePanelMouseClicked
    }//GEN-LAST:event_licensePanelMouseClicked

    private void lookAndFeelRButtonWindowsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lookAndFeelRButtonWindowsMouseClicked
        setLookAndFeel(PLAF_WINDOWS);
}//GEN-LAST:event_lookAndFeelRButtonWindowsMouseClicked

    private void lookAndFeelRButtonNimbusMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lookAndFeelRButtonNimbusMouseClicked
        setLookAndFeel(PLAF_NIMBUS);
}//GEN-LAST:event_lookAndFeelRButtonNimbusMouseClicked

    private void lookAndFeelRButtonGTKMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lookAndFeelRButtonGTKMouseClicked
        setLookAndFeel(PLAF_GTK);
}//GEN-LAST:event_lookAndFeelRButtonGTKMouseClicked

    private void lookAndFeelRButtonMotifMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lookAndFeelRButtonMotifMouseClicked
        setLookAndFeel(PLAF_MOTIF);
}//GEN-LAST:event_lookAndFeelRButtonMotifMouseClicked

    private void phoneDisplayPanelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_phoneDisplayPanelMouseEntered
        enableDisplayCheckBox.setSelected(true);
    }//GEN-LAST:event_phoneDisplayPanelMouseEntered

    private void phoneDisplayPanelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_phoneDisplayPanelMouseExited
        enableDisplayCheckBox.setSelected(false);
    }//GEN-LAST:event_phoneDisplayPanelMouseExited

    private void requestVergunningButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_requestVergunningButtonActionPerformed
        try { java.awt.Desktop.getDesktop().browse(java.net.URI.create(Vergunning.REQUEST_VERGUNNINGLINK)); } catch (IOException ex) { }
    }//GEN-LAST:event_requestVergunningButtonActionPerformed

    private void textLogAreaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_textLogAreaMouseClicked
        if (evt.getClickCount() == 2) {textLogArea.setText(""); }
    }//GEN-LAST:event_textLogAreaMouseClicked

    private void resizeWindowButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resizeWindowButtonActionPerformed
            Thread resizeWindowThread = new Thread(new Runnable()
            {
                @Override
                @SuppressWarnings({"static-access"})
                public void run()
                {
                    if ( getSize().getHeight() == getMinimumSize().getHeight() )
                    {
                        colorMaskPanel.setVisible(false);
                        int dimWidth = (int) getMaximumSize().getWidth();
                        int dimHeight = (int) getMinimumSize().getHeight();
                        double step = 70;
                        while ( dimHeight < (int) getMaximumSize().getHeight() )
                        {
                            setSize(dimWidth, dimHeight);
                            try { Thread.sleep(2); } catch (InterruptedException ex) { }
                            dimHeight += step;
                            step = ( step * 0.65 ) + 1;
                        }
                        setSize(getMaximumSize());
                        colorMaskPanel.setVisible(true);
                        resizeWindowButton.setText(icons.getResizeUpChar());
                        resizeWindowButton.setToolTipText("Hide Controls");
                    }
                    else
                    {
                        colorMaskPanel.setVisible(false);
                        int dimWidth = (int) getMaximumSize().getWidth();
                        int dimHeight = (int) getMaximumSize().getHeight();
                        double step = 70;
                        while ( dimHeight > (int) getMinimumSize().getHeight() )
                        {
                            setSize(dimWidth, dimHeight);
                            try { Thread.sleep(2); } catch (InterruptedException ex) { }
                            dimHeight -= step;
                            step = ( step * 0.65 ) + 1;
                        }
                        setSize(getMinimumSize());
                        colorMaskPanel.setVisible(true);
                        resizeWindowButton.setText(icons.getResizeDownChar());
                        resizeWindowButton.setToolTipText("Show Controls");
                    }
                }
            });
            resizeWindowThread.setName("resizeWindowThread");
            resizeWindowThread.setDaemon(runThreadsAsDaemons);
            resizeWindowThread.start();
}//GEN-LAST:event_resizeWindowButtonActionPerformed

    private void tabPaneMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabPaneMouseClicked
        if (tabPane.getSelectedIndex() == 8)
        {
            if (brandLabel.getForeground().getRGB() == aboutPanel.getBackground().getRGB())
            {
                Thread aboutFadeThread = new Thread(new Runnable()
                {
                    @Override
                    @SuppressWarnings({"static-access"})
                    public void run()
                    {
                        Color textColor = aboutPanel.getBackground();
                        while(brandLabel.getForeground().getGreen() < 255)
                        {
                            textColor = new Color(textColor.getRed() + 1,textColor.getGreen() + 1,textColor.getBlue() + 1);
                            brandLabel.setForeground(textColor);
                            brandDescriptionLabel.setForeground(textColor);
                            productLabel.setForeground(textColor);
                            productDescriptionLabel.setForeground(textColor);
                            copyrightLabel.setForeground(textColor);
                            try { Thread.sleep(10); } catch (InterruptedException ex) { }
                        }
                    }
                });
                aboutFadeThread.setName("aboutFadeThread");
                aboutFadeThread.setDaemon(runThreadsAsDaemons);
                aboutFadeThread.start();
            }
        }
    }//GEN-LAST:event_tabPaneMouseClicked

    private void toolsInnerPanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_toolsInnerPanelMouseClicked
        if (evt.getClickCount() == 2) { startJConsole(); }
}//GEN-LAST:event_toolsInnerPanelMouseClicked

    private void startJConsole()
    {
        Thread startJConsoleThread = new Thread(new Runnable()
        {
            @Override
            @SuppressWarnings({"static-access"})
            public void run()
            {
                String[] status = new String[2];
                showStatus("Starting JConsole", true, true);
                status[0] = "0"; status[1] = "";
                shell.startJConsole();
                if (status[0].equals("0")) { showStatus(status[1], true, true); } else { showStatus(status[1], true, true); }
                return;
            }
        });
        startJConsoleThread.setName("startJConsoleThread");
        startJConsoleThread.setDaemon(runThreadsAsDaemons);
        startJConsoleThread.start();
    }

    private void setAutoSpeed(boolean autoSpeedParam)
    {
        if (autoSpeedParam)
        {
	    autoSlidersEnabled = true;
	    callSpeedLabel.setForeground(Color.BLACK);
            callSpeedValue.setForeground(Color.BLACK);
            callSpeedSlider.setForeground(Color.BLACK);
            autoSpeedToggleButton.setSelected(true);
            autoSpeedToggleButton.setForeground(Color.blue);
        }
        else
        {
	    autoSlidersEnabled = false;
	    callSpeedLabel.setForeground(Color.RED);
            callSpeedValue.setForeground(Color.RED);
            callSpeedSlider.setForeground(Color.RED);
            autoSpeedToggleButton.setSelected(false);
            moveVMUsageMeter(0, true);
            autoSpeedToggleButton.setForeground(Color.black);
        }
    }

    private void debugSoftPhones(final boolean muteParam)
    {
        Thread debugSoftPhonesThread = new Thread( allThreadsGroup, new Runnable()
        {
            @Override
            public void run()
            {
		serviceLoopProgressBar.setEnabled(true);
                if (muteParam)
                {
//                    myClickOnSoundTool.play();
                    runCampaignCounter = 0;
		    showStatus("Enable Mute Audio SoftPhone...", true, true);
                    while (runCampaignCounter < outboundSoftPhonesAvailable) // Starts looping through the user-range
                    {
                        SoftPhone thisSoftPhoneInstance =  (SoftPhone) threadArray[runCampaignCounter]; // Get the reference to the SoftPhone object in the loop
                        thisSoftPhoneInstance.userInput(DEBUGBUTTON, "1", "", ""); // Send a registerButton response to this object's method userInput
			phoneStatsTable.setValueAt(runCampaignCounter + 1, 1, 1);
			serviceLoopProgressBar.setValue(runCampaignCounter);
                        try { Thread.sleep(1); } catch (InterruptedException ex) {  }
                        runCampaignCounter++;
                    }
		    showStatus("Enable Mute Audio SoftPhone Completed", true, true);
                    muteAudioToggleButton.setForeground(Color.blue);
                    phoneStatsTable.setValueAt("-", 1, 1);
                }
                else
                {
		    showStatus("Disable Mute Audio SoftPhone...", true, true);
//                    myClickOffSoundTool.play();
                    runCampaignCounter = 0;
                    while (runCampaignCounter < outboundSoftPhonesAvailable) // Starts looping through the user-range
                    {
                        SoftPhone thisSoftPhoneInstance = (SoftPhone) threadArray[runCampaignCounter]; // Get the reference to the SoftPhone object in the loop
                        thisSoftPhoneInstance.userInput(DEBUGBUTTON, "0", "", ""); // Send a registerButton response to this object's method userInput
			phoneStatsTable.setValueAt(runCampaignCounter + 1, 1, 1); // ProcessingInstance
			serviceLoopProgressBar.setValue(runCampaignCounter);
                        try { Thread.sleep(1); } catch (InterruptedException ex) {  }
                        runCampaignCounter++;
                    }
		    showStatus("Disable Mute Audio SoftPhone Completed", true, true);
                    muteAudioToggleButton.setForeground(Color.black);
                    phoneStatsTable.setValueAt("-", 1, 1);
                }
		serviceLoopProgressBar.setValue(0); serviceLoopProgressBar.setEnabled(false);
                return;
            }
        });
        debugSoftPhonesThread.setName("debugSoftPhonesThread");
        debugSoftPhonesThread.setDaemon(runThreadsAsDaemons);
        debugSoftPhonesThread.start();
    }

    /**
     *
     * @param remoteDisplayData
     */
    @Override
    synchronized public void phoneDisplay(final DisplayData remoteDisplayData)
    {
	if ( enableDisplayCheckBox.isSelected() )
	{
            Thread displayThread14 = new Thread( allThreadsGroup, new Runnable()
            {
                @Override
                @SuppressWarnings({"static-access"})
                public void run()
                {
                    if (!localDisplayData.getSoftphoneInfoCell().equals(remoteDisplayData.getSoftphoneInfoCell()))
                    { localDisplayData.setSoftphoneInfoCell(remoteDisplayData.getSoftphoneInfoCell());  softphoneInfoLabel.setText(localDisplayData.getSoftphoneInfoCell()); }
                    if (!localDisplayData.getProxyInfoCell().equals(remoteDisplayData.getProxyInfoCell()))
                    { localDisplayData.setProxyInfoCell(remoteDisplayData.getProxyInfoCell());          proxyInfoLabel.setText(localDisplayData.getProxyInfoCell()); }
                    if (!localDisplayData.getPrimaryStatusCell().equals(remoteDisplayData.getPrimaryStatusCell()))
                    { localDisplayData.setPrimaryStatusCell(remoteDisplayData.getPrimaryStatusCell());  primaryStatusLabel.setText(localDisplayData.getPrimaryStatusCell()); }
                    if (!localDisplayData.getPrimaryStatusDetailsCell().equals(remoteDisplayData.getPrimaryStatusDetailsCell()))
                    { localDisplayData.setPrimaryStatusDetailsCell(remoteDisplayData.getPrimaryStatusDetailsCell());  primaryStatusDetailsLabel.setText(localDisplayData.getPrimaryStatusDetailsCell()); }
                    if (!localDisplayData.getSecondaryStatusCell().equals(remoteDisplayData.getSecondaryStatusCell()))
                    { localDisplayData.setSecondaryStatusCell(remoteDisplayData.getSecondaryStatusCell());  secondaryStatusLabel.setText(localDisplayData.getSecondaryStatusCell()); }
                    if (!localDisplayData.getSecondaryStatusDetailsCell().equals(remoteDisplayData.getSecondaryStatusDetailsCell()))
                    { localDisplayData.setSecondaryStatusDetailsCell(remoteDisplayData.getSecondaryStatusDetailsCell()); secondaryStatusDetailsLabel.setText(localDisplayData.getSecondaryStatusDetailsCell()); }

                    if (localDisplayData.getOnFlag() != remoteDisplayData.getOnFlag())
                    {
                        localDisplayData.setOnFlag(remoteDisplayData.getOnFlag());
                        if (localDisplayData.getOnFlag())       { onPanel.setBorder(BorderFactory.createLineBorder(localDisplayData.ONCOLOR)); onLabel.setForeground(localDisplayData.ONCOLOR); }
                        else { onPanel.setBorder(BorderFactory.createLineBorder(localDisplayData.INACTIVECOLOR)); onLabel.setForeground(localDisplayData.INACTIVECOLOR); }
                    }
                    if (localDisplayData.getIdleFlag() != remoteDisplayData.getIdleFlag())
                    {
                        localDisplayData.setIdleFlag(remoteDisplayData.getIdleFlag());
                        if (localDisplayData.getIdleFlag())     { idlePanel.setBorder(BorderFactory.createLineBorder(localDisplayData.IDLECOLOR)); idleLabel.setForeground(localDisplayData.IDLECOLOR); }
                        else { idlePanel.setBorder(BorderFactory.createLineBorder(localDisplayData.INACTIVECOLOR)); idleLabel.setForeground(localDisplayData.INACTIVECOLOR); }
                    }
                    if (localDisplayData.getConnectFlag() != remoteDisplayData.getConnectFlag())
                    {
                        localDisplayData.setConnectFlag(remoteDisplayData.getConnectFlag());
                        if (localDisplayData.getConnectFlag())  { connectingPanel.setBorder(BorderFactory.createLineBorder(localDisplayData.CALLINGCOLOR)); connectingLabel.setForeground(localDisplayData.CONNECTCOLOR); }
                        else { connectingPanel.setBorder(BorderFactory.createLineBorder(localDisplayData.INACTIVECOLOR)); connectingLabel.setForeground(localDisplayData.INACTIVECOLOR); }
                    }
                    if (localDisplayData.getCallingFlag() != remoteDisplayData.getCallingFlag())
                    {
                        localDisplayData.setCallingFlag(remoteDisplayData.getCallingFlag());
                        if (localDisplayData.getCallingFlag())  { callingPanel.setBorder(BorderFactory.createLineBorder(localDisplayData.CALLINGCOLOR)); callingLabel.setForeground(localDisplayData.CALLINGCOLOR); }
                        else { callingPanel.setBorder(BorderFactory.createLineBorder(localDisplayData.INACTIVECOLOR)); callingLabel.setForeground(localDisplayData.INACTIVECOLOR); }
                    }
                    if (localDisplayData.getRingingFlag() != remoteDisplayData.getRingingFlag())
                    {
                        localDisplayData.setRingingFlag(remoteDisplayData.getRingingFlag());
                        if (localDisplayData.getRingingFlag())  { ringingPanel.setBorder(BorderFactory.createLineBorder(localDisplayData.RINGINGCOLOR)); ringingLabel.setForeground(localDisplayData.RINGINGCOLOR); }
                        else { ringingPanel.setBorder(BorderFactory.createLineBorder(localDisplayData.INACTIVECOLOR)); ringingLabel.setForeground(localDisplayData.INACTIVECOLOR); }
                    }
                    if (localDisplayData.getAcceptFlag() != remoteDisplayData.getAcceptFlag())
                    {
                        localDisplayData.setAcceptFlag(remoteDisplayData.getAcceptFlag());
                        if (localDisplayData.getAcceptFlag())   { acceptingPanel.setBorder(BorderFactory.createLineBorder(localDisplayData.ACCEPTCOLOR)); acceptingLabel.setForeground(localDisplayData.ACCEPTCOLOR); }
                        else { acceptingPanel.setBorder(BorderFactory.createLineBorder(localDisplayData.INACTIVECOLOR)); acceptingLabel.setForeground(localDisplayData.INACTIVECOLOR); }
                    }
                    if (localDisplayData.getTalkingFlag() != remoteDisplayData.getTalkingFlag())
                    {
                        localDisplayData.setTalkingFlag(remoteDisplayData.getTalkingFlag());
                        if (localDisplayData.getTalkingFlag())  { talkingPanel.setBorder(BorderFactory.createLineBorder(localDisplayData.TALKINGCOLOR)); talkingLabel.setForeground(localDisplayData.TALKINGCOLOR); }
                        else { talkingPanel.setBorder(BorderFactory.createLineBorder(localDisplayData.INACTIVECOLOR)); talkingLabel.setForeground(localDisplayData.INACTIVECOLOR); }
                    }
                    if (localDisplayData.getRegisteredFlag() != remoteDisplayData.getRegisteredFlag())
                    {
                        localDisplayData.setRegisteredFlag(remoteDisplayData.getRegisteredFlag());
                        if (localDisplayData.getRegisteredFlag())
                        {
                            registeredPanel.setBorder(BorderFactory.createLineBorder(localDisplayData.REGISTEREDCOLOR)); registeredLabel.setForeground(localDisplayData.REGISTEREDCOLOR);
                            registerToggleButton.setSelected(true); registerToggleButton.setForeground(Color.BLUE);
                        }
                        else
                        {
                            registeredPanel.setBorder(BorderFactory.createLineBorder(localDisplayData.INACTIVECOLOR)); registeredLabel.setForeground(localDisplayData.INACTIVECOLOR);
                            registerToggleButton.setSelected(false); registerToggleButton.setForeground(Color.BLACK);
                        }
                    }
                    if (localDisplayData.getAnswerFlag() != remoteDisplayData.getAnswerFlag())
                    {
                        localDisplayData.setAnswerFlag(remoteDisplayData.getAnswerFlag());
                        if (localDisplayData.getAnswerFlag())
                        {
                            answerPanel.setBorder(BorderFactory.createLineBorder(localDisplayData.ANSWERCOLOR)); answerLabel.setForeground(localDisplayData.ANSWERCOLOR);
                            cancelPanel.setBorder(BorderFactory.createLineBorder(localDisplayData.INACTIVECOLOR)); cancelLabel.setForeground(localDisplayData.INACTIVECOLOR);
                        }
                        else
                        {
                            answerPanel.setBorder(BorderFactory.createLineBorder(localDisplayData.INACTIVECOLOR)); answerLabel.setForeground(localDisplayData.INACTIVECOLOR);
                        }
                    }
                    if (localDisplayData.getCancelFlag() != remoteDisplayData.getCancelFlag())
                    {
                        localDisplayData.setCancelFlag(remoteDisplayData.getCancelFlag());
                        if (localDisplayData.getCancelFlag())
                        {
                            cancelPanel.setBorder(BorderFactory.createLineBorder(localDisplayData.CANCELCOLOR)); cancelLabel.setForeground(localDisplayData.CANCELCOLOR);
                            answerPanel.setBorder(BorderFactory.createLineBorder(localDisplayData.INACTIVECOLOR)); answerLabel.setForeground(localDisplayData.INACTIVECOLOR);
                        }
                        else
                        {
                            cancelPanel.setBorder(BorderFactory.createLineBorder(localDisplayData.INACTIVECOLOR)); cancelLabel.setForeground(localDisplayData.INACTIVECOLOR);
                        }
                    }
                    if (localDisplayData.getMuteFlag() != remoteDisplayData.getMuteFlag())
                    {
                        localDisplayData.setMuteFlag(remoteDisplayData.getMuteFlag());
                        if (localDisplayData.getMuteFlag())
                        {
                            mutePanel.setBorder(BorderFactory.createLineBorder(localDisplayData.MUTECOLOR)); muteLabel.setForeground(localDisplayData.MUTECOLOR);
                            muteAudioToggleButton.setSelected(true); muteAudioToggleButton.setForeground(Color.BLUE);
                        }
                        else
                        {
                            mutePanel.setBorder(BorderFactory.createLineBorder(localDisplayData.INACTIVECOLOR)); muteLabel.setForeground(localDisplayData.INACTIVECOLOR);
                            muteAudioToggleButton.setSelected(false); muteAudioToggleButton.setForeground(Color.BLACK);
                        }
                    }
                }
            });
            displayThread14.setName("displayThread14");
            displayThread14.setDaemon(runThreadsAsDaemons);
            displayThread14.start();

	}
        return;
    }

    /**
     *
     * @param remoteSpeakerData
     */
    @Override
    synchronized public void speaker(SpeakerData remoteSpeakerData)
    {
//        System.out.println("Test: " + remoteSpeakerData.toString());
//        if (remoteSpeakerData.getDialToneFlag())                { myDialToneSoundTool.play(); }
//        if (remoteSpeakerData.getCallToneFlag())                { myCallToneSoundTool.playLoop(); } else { myCallToneSoundTool.stop(); }
//        if (remoteSpeakerData.getBusyToneFlag())                { myCallToneSoundTool.stop(); myBusyToneSoundTool.play(); }
//        if (remoteSpeakerData.getDeadToneFlag())                { myCallToneSoundTool.stop(); myDeadToneSoundTool.play(); }
//        if (remoteSpeakerData.getErrorToneFlag())               { myCallToneSoundTool.stop(); myErrorToneSoundTool.play(); }
//        if (remoteSpeakerData.getRingToneFlag())                { myRingToneSoundTool.playLoop(); } else { myRingToneSoundTool.stop(); }
//        if (remoteSpeakerData.getRegisterEnabledToneFlag())     { myRegisterEnabledSoundTool.play(); }
//        if (remoteSpeakerData.getRegisterDisabledToneFlag())    { myRegisterDisabledSoundTool.play(); }
//        if (remoteSpeakerData.getAnswerEnabledToneFlag())       { myAnswerEnabledSoundTool.play(); }
//        if (remoteSpeakerData.getAnswerDisabledToneFlag())      { myAnswerDisabledSoundTool.play(); }
//        if (remoteSpeakerData.getCancelEnabledToneFlag())       { myCancelEnabledSoundTool.play(); }
//        if (remoteSpeakerData.getCancelDisabledToneFlag())      { myCancelDisabledSoundTool.play(); }
//        if (remoteSpeakerData.getMuteEnabledToneFlag())         { myMuteEnabledSoundTool.play(); }
//        if (remoteSpeakerData.getMuteDisabledToneFlag())        { myMuteDisabledSoundTool.play(); }
    }

    /**
     *
     */
    public void timedLicenseUpdate() { if ( ( vergunning != null ) && ( vergunning.isValid() && (powerToggleButton.isSelected()) ) ) { executeVergunning(); } }

    /**
     *
     */
    public void timedSystemStatsUpdate()
    {
	Thread timedSystemStatsUpdateThread = new Thread( allThreadsGroup, new Runnable()
	{
	    @Override
            @SuppressWarnings("static-access")
	    public void run()
	    {
                String[] status = new String[2];
// Get CustomerOrder in case of any runtime changes like a changed soundfile
                if ((campaign != null) && (order != null) && (boundMode.equals("Outbound")) && (callCenterStatus == RUNNING))
                {
                    order = dbClient.selectCustomerOrder(campaign.getOrderId());
                    if  (lastMessageDuration != order.getMessageDuration())
                    {
                        soundFileToStream = order.getMessageFilename(); filename = "file:" + soundFileToStream;
                        // Make sure the callSpeedSlider adapts to the message length in relation to the Call / Message Duration when message is longer than 10 seconds
                        if (Math.round(order.getMessageDuration() * 100) < (eCallCenterGUI.callSpeedSlider.getMinimum()) ) // Soundfile results below minimum
                        {
                            eCallCenterGUI.callSpeedSlider.setMaximum(eCallCenterGUI.callSpeedSlider.getMinimum());
                        }
                        else
                        {
                            eCallCenterGUI.callSpeedSlider.setMaximum(order.getMessageDuration() * 50);
                            eCallCenterGUI.callSpeedSlider.setMajorTickSpacing(Math.round((eCallCenterGUI.callSpeedSlider.getMaximum() - eCallCenterGUI.callSpeedSlider.getMinimum()) / 10));
                            eCallCenterGUI.callSpeedSlider.setPaintLabels(true);
                        }
                        callSpeedInterval = Math.round(eCallCenterGUI.callSpeedSlider.getMaximum() / 2);
                        callSpeedSlider.setValue(callSpeedInterval);
                        lastMessageDuration = order.getMessageDuration();
                    }
                }

                memFree = sysMonitor.getPhysMem(); systemStatsTable.setValueAt(Long.toString(memFree), 2, 1);

		heapMemMax = (Runtime.getRuntime().maxMemory()/(1024*1024)); systemStatsTable.setValueAt(Long.toString(heapMemMax), 3, 1); // KB
		heapMemTot = (Runtime.getRuntime().totalMemory()/(1024*1024)); systemStatsTable.setValueAt(Long.toString(heapMemTot), 4, 1); // KB
		heapMemFree = (Runtime.getRuntime().freeMemory()/(1024*1024)); systemStatsTable.setValueAt(Long.toString(heapMemFree), 5, 1); // KB
                systemStatsTable.setValueAt(Thread.activeCount(), 1, 1);

// Display time / performance facts
                currentTimeCalendar = Calendar.getInstance(nlLocale);
                difRegStartExpEndCalendar = Calendar.getInstance(nlLocale);
                difRegStartCurTimeCalendar = Calendar.getInstance(nlLocale);
                difCurTimeExpEndCalendar = Calendar.getInstance(nlLocale);

                if ( ( outboundCallsInProgress ) && (campaignStat.getCallingTT() > 0))
                {

                    // Set & Display the ExpectedEnd timestamp in human readable format
//                    campaign.setCalendarExpectedEnd(TimeWindow.getEstimatedEndCalendar(Calendar.getInstance(), order, throughputFactor, destination.getDestinationCount())); // The new way
                    campaign.setCalendarExpectedEnd(timeTool.getEstimatedEndCalendar(Calendar.getInstance(), order, throughputFactor, destination.getDestinationCount(), order.getTimewindowIndexArray())); // The new way
                    dbClient.updateCampaign(campaign);

                    difRegStartExpEndCalendar.setTimeInMillis(campaign.getCalendarExpectedEnd().getTimeInMillis() - campaign.getCalendarRegisteredStart().getTimeInMillis());
                    difRegStartCurTimeCalendar.setTimeInMillis(currentTimeCalendar.getTimeInMillis() - campaign.getCalendarRegisteredStart().getTimeInMillis());
                    difCurTimeExpEndCalendar.setTimeInMillis(campaign.getCalendarExpectedEnd().getTimeInMillis() - currentTimeCalendar.getTimeInMillis());

                    // Sets the End Expected Timestamp
                    campaignTable.setValueAt(
                                                    String.format("%04d", campaign.getCalendarExpectedEnd().get(Calendar.YEAR)) + "-" +
                                                    String.format("%02d", campaign.getCalendarExpectedEnd().get(Calendar.MONTH) + 1) + "-" +
                                                    String.format("%02d", campaign.getCalendarExpectedEnd().get(Calendar.DAY_OF_MONTH)) + " " +
                                                    String.format("%02d", campaign.getCalendarExpectedEnd().get(Calendar.HOUR_OF_DAY)) + ":" +
                                                    String.format("%02d", campaign.getCalendarExpectedEnd().get(Calendar.MINUTE)) + ":" +
                                                    String.format("%02d", campaign.getCalendarExpectedEnd().get(Calendar.SECOND))
                                                    , 3, 1);

                    // Sets the Past / Elapsed Campaign Time
                    campaignTable.setValueAt(
                                                    String.format("%04d", difRegStartExpEndCalendar.get(Calendar.YEAR) - 1970) + "-" +
                                                    String.format("%02d", difRegStartExpEndCalendar.get(Calendar.MONTH)) + "-" +
                                                    String.format("%02d", difRegStartExpEndCalendar.get(Calendar.DAY_OF_MONTH) -1 ) + " " +
                                                    String.format("%02d", difRegStartExpEndCalendar.get(Calendar.HOUR_OF_DAY) -1 ) + ":" +
                                                    String.format("%02d", difRegStartExpEndCalendar.get(Calendar.MINUTE)) + ":" +
                                                    String.format("%02d", difRegStartExpEndCalendar.get(Calendar.SECOND))
                                                    , 6, 1);

                    // Sets the Campaign Ending Countdown Time (ETA)
                    campaignTable.setValueAt(
                                                    String.format("%04d", difRegStartCurTimeCalendar.get(Calendar.YEAR) - 1970) + "-" +
                                                    String.format("%02d", difRegStartCurTimeCalendar.get(Calendar.MONTH)) + "-" +
                                                    String.format("%02d", difRegStartCurTimeCalendar.get(Calendar.DAY_OF_MONTH) -1 ) + " " +
                                                    String.format("%02d", difRegStartCurTimeCalendar.get(Calendar.HOUR_OF_DAY) -1 ) + ":" +
                                                    String.format("%02d", difRegStartCurTimeCalendar.get(Calendar.MINUTE)) + ":" +
                                                    String.format("%02d", difRegStartCurTimeCalendar.get(Calendar.SECOND))
                                                    , 7, 1);

                    campaignTable.setValueAt(
                                                    String.format("%04d", difCurTimeExpEndCalendar.get(Calendar.YEAR) - 1970) + "-" +
                                                    String.format("%02d", difCurTimeExpEndCalendar.get(Calendar.MONTH)) + "-" +
                                                    String.format("%02d", difCurTimeExpEndCalendar.get(Calendar.DAY_OF_MONTH) -1 ) + " " +
                                                    String.format("%02d", difCurTimeExpEndCalendar.get(Calendar.HOUR_OF_DAY) -1 ) + ":" +
                                                    String.format("%02d", difCurTimeExpEndCalendar.get(Calendar.MINUTE)) + ":" +
                                                    String.format("%02d", difCurTimeExpEndCalendar.get(Calendar.SECOND))
                                                    , 8, 1);
                    
                    dbClient.updateCampaign(campaign);
                }

// Display PieGraph & Update CampaignStat
		if ((callCenterIsOutBound) && (callRatioChartData != null) && (callRatioChart != null) && (dbClient != null) &&(( callCenterStatus == RUNNING ) ||( callCenterStatus == RERUNBREAK )) )
		{
		    callRatioChartData.setValue("Connecting", (campaignStat.getConnectingTT() - campaignStat.getTryingTT()));
		    callRatioChartData.setValue("Trying",     (campaignStat.getTryingTT() - campaignStat.getCallingTT()));
                    callRatioChartData.setValue("Busy", campaignStat.getRemoteBusyTT());
                    callRatioChartData.setValue("Success", campaignStat.getTalkingTT());
		    callRatioChart.setTitle("Call Ratio");

                    // Store the CampaignStats in database
                    dbClient.updateCampaignStat(campaignStat);
		}

// Only in correct TimeWindow
//                if ( order != null)
//                {
//                    if ((callCenterIsNetManaged) && (callCenterIsOutBound) && (!TimeWindow.getCurrentTimeWindow().equals(order.getTimeWindowCategory())))
//                    {
//                        showStatus          (License.PRODUCT + " Shutdown: Running outside " + order.getTimeWindowCategory(), true, true);
//                        try { Thread.sleep(5000); } catch (InterruptedException ex) { }
//                        System.exit(0);
//                    }
//                }


                if ( order != null)
                {
                    boolean legalTimeWindow = false;
                    for (int orderTimewindow : order.getTimewindowIndexArray())
                    {
                        if (orderTimewindow == timeTool.getCurrentTimeWindowIndex()) { legalTimeWindow = true;}
                    }

                    if ( ! legalTimeWindow)
                    {
                        showStatus          ("Self Destructing: " + Vergunning.PRODUCT + " running outside TimeWindow !", true, true);
                        try { Thread.sleep(5000); } catch (InterruptedException ex) { }
                        System.exit(0);
                    }
                }

                // Management Server Heartbeat (Manager heartbeat Lost)
                if (callCenterIsNetManaged)
                {
                    //Exit if no management signal is received in time
                    if (selfDestructCounter == 0)
                    {
                        showStatus          (Vergunning.PRODUCT + " Shutdown: Management Heartbeat Lost", true, true);
                        try { Thread.sleep(5000); } catch (InterruptedException ex) { }
                        System.exit(0); 
                    }
                    
                    if (selfDestructCounter > 0)  { selfDestructCounter--; }
                    if (selfDestructCounter < ( selfDestructCounterLimit - 2 )) // Warning and Restart
                    {
                        if (!callCenterIsOutBound)
                        {
                            if (! isRegistering)
                            {
                                showStatus("Restarting Inbound Management Server", true, true);
                                enableInboundNetManagerServer(false);
                                try { Thread.sleep(500); } catch (InterruptedException ex) { }
                                enableInboundNetManagerServer(true);
                            }
                        }
                        else// if (getCallCenterStatus() == RUNNING)
                        {
                            showStatus("Restarting Outbound Management Server", true, true);
                            enableOutboundNetManagerServer(false);
                            try { Thread.sleep(500); } catch (InterruptedException ex) { }
                            enableOutboundNetManagerServer(true);
                        }
                    }
                    else
                    {
                        if (!callCenterIsOutBound)
                        {
                            netManagerInboundServerToggleButton.setForeground(Color.BLACK);
                        }
                        else
                        {
                            netManagerOutboundServerToggleButton.setForeground(Color.BLACK);
                        }
                    }
                }
	    } // Run
	}); // Thread
        timedSystemStatsUpdateThread.setName("timedSystemStatsUpdateThread");
	timedSystemStatsUpdateThread.setDaemon(runThreadsAsDaemons);
	timedSystemStatsUpdateThread.start();
    }

    /**
     *
     */
    public void timedDashboardUpdate()
    {
	Thread timedDashboardUpdateThread = new Thread( allThreadsGroup, new Runnable()
	{
	    @Override
	    @SuppressWarnings({"static-access", "empty-statement"})
	    public void run()
	    {
                // Calculate calls per second
                double callsPerSecondPrecise = 0;
                currentTimeDashboardCalendar = Calendar.getInstance();

                if ((campaignStat != null) && (lastTimeDashboardCampaignStat != null))
                {
                    if ((campaignStat.getCallingTT() > campaignStat.getRingingTT()))
                    { callsPerSecondPrecise = ( ( ( (double)campaignStat.getCallingTT() - (double)lastTimeDashboardCampaignStat.getCallingTT() ) /  ( (currentTimeDashboardCalendar.getTimeInMillis() / 1000) - (lastTimeDashboardCalendar.getTimeInMillis() / 1000)) ) ); }
                    else { callsPerSecondPrecise = ( ( ( (double)campaignStat.getRingingTT() - (double)lastTimeDashboardCampaignStat.getRingingTT() ) /  ( (currentTimeDashboardCalendar.getTimeInMillis() / 1000) - (lastTimeDashboardCalendar.getTimeInMillis() / 1000)) ) ); }
                    double callsPerSecondRounded = (double)((callsPerSecondPrecise*100.0) / (double)100.0);
                    double callsPerMinutePrecise = (callsPerSecondRounded * 60);
                    double callsPerMinuteRounded = (double)((callsPerMinutePrecise*100.0) / (double)100.0);
                    double callsPerHourRounded   = (double)((callsPerMinuteRounded*60.0));
                    if (((currentTimeDashboardCalendar.getTimeInMillis() / 1000) - (lastTimeDashboardCalendar.getTimeInMillis() / 1000) > 0)) { campaignTable.setValueAt(callsPerHourRounded + " per Hour", 9, 1); }
                    try { lastTimeDashboardCampaignStat = (CampaignStat) campaignStat.clone(); } catch (CloneNotSupportedException ex) { /* Nonsens in this case*/ };
                    lastTimeDashboardCalendar = currentTimeDashboardCalendar.getInstance();
                    if (! vergunning.vergunningOrderInProgress()) { movePerformanceMeter((callsPerHourRounded / 100), smoothCheckBox.isSelected()); }

// Calculate the estimated number of seconds the campaign will take
                    double durationCallsEpochTimePrecise = 0;
                    if (( (double)callsPerSecondPrecise != 0 ) && (order != null))
                    {
                        durationCallsEpochTimePrecise = Math.round(order.getTargetTransactionQuantity() / (double)callsPerSecondPrecise);
//                        durationCallsEpochTime = (long)durationCallsEpochTimePrecise;
                        throughputFactor = (int)Math.round(order.getMessageDuration() / (1 / callsPerSecondPrecise));
                        double hourlyTurnoverPrecise = (order.getMessageRate() * callsPerSecondPrecise * hour);
                        double hourlyTurnoverRounded = (double)(Math.round(hourlyTurnoverPrecise*100.0) / 100.0);
                        turnoverStatsTable.setValueAt(hourlyTurnoverRounded,0,2); // Hourly Turnover
                    }
                }
	    }
	});
	timedDashboardUpdateThread.setName("timedDashboardUpdateThread");
	timedDashboardUpdateThread.setDaemon(runThreadsAsDaemons);
	timedDashboardUpdateThread.start();
    }

    /**
     *
     */
    public void timedAutoSpeedUpdate()
    {
	Thread timedAutoSpeedUpdateThread = new Thread( allThreadsGroup, new Runnable()
	{
	    @Override
	    @SuppressWarnings({"static-access", "empty-statement"})
	    public void run()
	    {
// Get the vmUsage load
                vmUsage = (sysMonitor.getProcessTime());
                //showStatus("vmUsage: " + vmUsage, true, false);

// Set the vmUsage visualisers
                systemStatsTable.setValueAt(Long.toString(vmUsage), 0, 1);
                if (smoothCheckBox.isSelected()) { moveVMUsageMeter(((int)vmUsage), true); } else { moveVMUsageMeter(((int)vmUsage), false); }
                
// Adjust Sliders
                if ( autoSlidersEnabled )
                {
                    if (outboundSoftPhonesAvailable > 0)
                    {
                        long percentageRangeUsed;
                        if ( (int)vmUsage > vmUsageDecelerationThreashold )
                        {
                            int slowDownRange = 100 - vmUsageDecelerationThreashold;
                            int vmUsageAboveThreshhold = ((int)vmUsage - vmUsageDecelerationThreashold);
                            percentageRangeUsed = Math.round(vmUsageAboveThreshhold / (slowDownRange * 0.01));
                        }
                        else
                        {
                            percentageRangeUsed = 0;
                        }

                        if (smoothCheckBox.isSelected())
                        {
                            moveCallSpeedSlider((int)Math.round((percentageRangeUsed * ((callSpeedSlider.getMaximum() - callSpeedSlider.getMinimum()) * 0.01)) + callSpeedSlider.getMinimum()), true);
                        }
                        else
                        {
                            moveCallSpeedSlider((int)Math.round((percentageRangeUsed * ((callSpeedSlider.getMaximum() - callSpeedSlider.getMinimum()) * 0.01)) + callSpeedSlider.getMinimum()), false);
                        }
                    }
                }
	    }
	});
	timedAutoSpeedUpdateThread.setName("timedAutoSpeedUpdateThread");
	timedAutoSpeedUpdateThread.setDaemon(runThreadsAsDaemons);
	timedAutoSpeedUpdateThread.start();
    }

    /**
     *
     */
    public void timedStallingDetectionUpdate()
    {
	Thread timedStallingDetectionUpdateThread = new Thread( allThreadsGroup, new Runnable()
	{
	    @Override
	    @SuppressWarnings({"static-access", "empty-statement"})
	    public void run()
	    {
                // Outbound Stalling Detector. Only invokes once every 10 secs, CallCenter is Outbound & Running)
                if ( ( boundMode.equals("Outbound")) && ( callCenterStatus == RUNNING ) )
                {
                    if ( (lastStallerCampaignStat.getConnectingTT() == campaignStat.getConnectingTT()) &&  (lastStallerCampaignStat.getCallingTT() == campaignStat.getCallingTT()) ) { stalling = true; } else {stalling = false;}
                    try     { lastStallerCampaignStat = (CampaignStat) campaignStat.clone(); } catch (CloneNotSupportedException ex) { /* Nonsens in this case*/ }
                }
                else if ( ( ( boundMode.equals("Outbound")) && (( callCenterStatus == PAUSING ) || (callCenterStatus == RERUNBREAK)) ) ) { stalling = false; }

                // Inbound Stalling Detector. Only invokes once every 10 secs, CallCenter is Inbound & Running)
                if ( ( boundMode.equals("Inbound")) && (( callCenterStatus == POWERINGON ) || ( callCenterStatus == POWEREDON )) && ( callCenterIsNetManaged ) )
                {
                    if ( (lastStallerCampaignStat.getRingingTT() == campaignStat.getRingingTT()) && (lastStallerCampaignStat.getTalkingTT() == campaignStat.getTalkingTT()) ) { stalling = true; } else {stalling = false; stallingCounter = stallingCounterLimit;}
                    try     { lastStallerCampaignStat = (CampaignStat) campaignStat.clone(); } catch (CloneNotSupportedException ex) { /* Nonsens in this case*/ }
                }

                if (( boundMode.equals("Inbound")) && (stalling) && (callCenterIsNetManaged))
                {
                    stallingCounter -= Math.round(updateStallerTimerInterval / 1000); // minus StallingInterval seconds
                    if (stallingCounter < stallingCounterLimit - 60) { showStatus("Inbound CallCenter Stalling: Shutdown in " + stallingCounter + " Seconds",false, false); }
                    if (stallingCounter == 0 )
                    {
                        showStatus("Inbound CallCenter Stalling: Shutdown Now!", true, true);
                        try { Thread.sleep(5000); } catch (InterruptedException ex) { }
                        System.exit(0);
                    }
                }
                else if ((!isRegistering) && (boundMode != null) && ( boundMode.equals("Inbound")))
                {
                    if ( stallingCounter >= stallingCounterLimit - 60) { statusBar.setText("Inbound CallCenter Received " + campaignStat.getRingingTT() + " Phone Calls"); }
                }
                System.gc();
	    }
	});
	timedStallingDetectionUpdateThread.setName("timedStallingDetectionUpdateThread");
	timedStallingDetectionUpdateThread.setDaemon(runThreadsAsDaemons);
	timedStallingDetectionUpdateThread.start();
    }

    /**
     *
     */
    public void blinkNetManagerToggleButton()
    {
	Thread blinkNetManagerToggleButtonThread = new Thread( allThreadsGroup, new Runnable()
	{
	    @Override
	    @SuppressWarnings({"static-access", "empty-statement"})
	    public void run()
	    {
                if      (netManagerInboundServerToggleButton.isSelected())
                {
                    netManagerInboundServerToggleButton.setForeground(Color.GREEN);
                    powerToggleButton.setForeground(Color.GREEN);
                    try { Thread.sleep(smoothMovementPeriod); } catch (InterruptedException ex) { }
                    netManagerInboundServerToggleButton.setForeground(Color.BLUE);
                    powerToggleButton.setForeground(Color.BLUE);
                }
                else if (netManagerOutboundServerToggleButton.isSelected())
                {
                    netManagerOutboundServerToggleButton.setForeground(Color.GREEN);
                    powerToggleButton.setForeground(Color.GREEN);
                    try { Thread.sleep(smoothMovementPeriod); } catch (InterruptedException ex) { }
                    netManagerOutboundServerToggleButton.setForeground(Color.BLUE);
                    powerToggleButton.setForeground(Color.BLUE);
                }
	    }
	});
	blinkNetManagerToggleButtonThread.setName("blinkNetManagerToggleButtonThread");
	blinkNetManagerToggleButtonThread.setDaemon(runThreadsAsDaemons);
	blinkNetManagerToggleButtonThread.start();
    }

    /**
     *
     */
    public void register() // Will also set reregisters
    {
        isRegistering = true;
	Thread registerThread = new Thread( allThreadsGroup, new Runnable()
        {
            @Override
            @SuppressWarnings("static-access")
            public void run()
            {
                boundMode = "Inbound";
                callCenterIsOutBound = false;

                runCampaignToggleButton.setEnabled(false); stopCampaignButton.setEnabled(false);
                campaignComboBox.setEnabled(false);

                registerToggleButton.setEnabled(true);
                registerToggleButton.setSelected(true);
                autoSpeedToggleButton.setSelected(false); // We dont need auto speed and system-load checking in inbound test mode
                muteAudioToggleButton.setEnabled(false);
                humanResponseSimulatorToggleButton.setEnabled(false);

                // Registration
                showStatus("Sending registration requests to PBX...", true, true);
                serviceLoopProgressBar.setValue(0);
                serviceLoopProgressBar.setEnabled(true);
                registerCounter = 0;
                registerToggleButton.setSelected(true);
                while (registerCounter < outboundSoftPhonesAvailable) // Starts looping through the user-range
                {
                    Thread registerInnerThread = new Thread( allThreadsGroup, new Runnable()
                    {
                        @Override
                        @SuppressWarnings({"static-access", "empty-statement"})
                        public void run()
                        {
                            String[] status = new String[2];
                            SoftPhone thisSoftPhoneInstance =  (SoftPhone) threadArray[registerCounter]; // Get the reference to the SoftPhone object in the loop
                            status = thisSoftPhoneInstance.userInput(REGISTERBUTTON, "1", "", ""); // 1 ringResponseDelay, 2 BusyPercentage, 3 EndDelay
                            if (status[0].equals("1")) { showStatus("Registration Error: " + status[1], true, true); }
                        }
                    });
                    registerInnerThread.setName("registerInnerThread");
                    registerInnerThread.setDaemon(runThreadsAsDaemons);
                    registerInnerThread.start();
                    try { Thread.sleep(registrationBurstDelay); } catch (InterruptedException ex) {  }

                    phoneStatsTable.setValueAt(registerCounter + 1, 1, 1); // ProcessingInstance
                    serviceLoopProgressBar.setValue(registerCounter);
                    registerCounter++;
                }

                serviceLoopProgressBar.setEnabled(false);
                serviceLoopProgressBar.setValue(0);
                // Wait for delayed registerResponses from PBX
                int lastRegisteredActiveCount = 0;
                showStatus("Receiving delayed PBX registration responses...", true, true);
                while (lastRegisteredActiveCount != registeredActiveCount ) // This codeblock waits until no more PBX registries are detected in time before doing another last (extra) register round
                {
                    lastRegisteredActiveCount = registeredActiveCount;
                    try { Thread.sleep(1000); } catch (InterruptedException ex) {  }
                }

// Extra registrations
                serviceLoopProgressBar.setValue(0);
                serviceLoopProgressBar.setEnabled(true);
                registerCounter = 0;
                showStatus("Sending extra registration requests to PBX...", true, true);
                while ((registeredActiveCount < outboundSoftPhonesAvailable) && (registerCounter < outboundSoftPhonesAvailable)) // Starts looping through the user-range
                {
                    Thread registerInnerThread = new Thread( allThreadsGroup, new Runnable()
                    {
                        @Override
                        @SuppressWarnings({"static-access", "empty-statement"})
                        public void run()
                        {
                            SoftPhone thisSoftPhoneInstance =  (SoftPhone) threadArray[registerCounter]; // Get the reference to the SoftPhone object in the loop
                            if (thisSoftPhoneInstance.getLoginState() == thisSoftPhoneInstance.LOGINSTATE_UNREGISTERED)
                            {
                                String[] status = new String[2]; status = thisSoftPhoneInstance.userInput(REGISTERBUTTON, "1", "", ""); // 1 ringResponseDelay, 2 BusyPercentage, 3 EndDelay
                                if (status[0].equals("1")) { showStatus("Registration Error: " + status[1], true, true); }
                            }
                        }
                    });
                    registerInnerThread.setName("registerInnerThread");
                    registerInnerThread.setDaemon(runThreadsAsDaemons);
                    registerInnerThread.start();

                    try { Thread.sleep(Math.round(registrationBurstDelay / 4)); } catch (InterruptedException ex) {  }
                    phoneStatsTable.setValueAt(registerCounter + 1, 1, 1); // ProcessingInstance
                    serviceLoopProgressBar.setValue(registerCounter);
                    registerCounter++;
                }

                serviceLoopProgressBar.setEnabled(false); serviceLoopProgressBar.setValue(0);
                lastRegisteredActiveCount = 0;
                showStatus("Receiving extra PBX registration responses...", true, true);
                while (lastRegisteredActiveCount != registeredActiveCount ) // This codeblock waits until no more PBX registries are detected in time before doing another last (extra) register round
                {
                    lastRegisteredActiveCount = registeredActiveCount;
                    try { Thread.sleep(1000); } catch (InterruptedException ex) {  }
                }

// Final registrations
                serviceLoopProgressBar.setValue(0);
                serviceLoopProgressBar.setEnabled(true);
                registerCounter = 0;
                showStatus("Sending final registration requests to PBX...", true, true);
                while ((registeredActiveCount < outboundSoftPhonesAvailable) && (registerCounter < outboundSoftPhonesAvailable)) // Starts looping through the user-range
                {
                    Thread registerInnerThread = new Thread( allThreadsGroup, new Runnable()
                    {
                        @Override
                        @SuppressWarnings({"static-access", "empty-statement"})
                        public void run()
                        {
                            SoftPhone thisSoftPhoneInstance =  (SoftPhone) threadArray[registerCounter]; // Get the reference to the SoftPhone object in the loop
                            if (thisSoftPhoneInstance.getLoginState() == thisSoftPhoneInstance.LOGINSTATE_UNREGISTERED)
                            {
                                String[] status = new String[2]; status = thisSoftPhoneInstance.userInput(REGISTERBUTTON, "1", "", ""); // 1 ringResponseDelay, 2 BusyPercentage, 3 EndDelay
                                if (status[0].equals("1")) { showStatus("Registration Error: " + status[1], true, true); }
                            }
                        }
                    });
                    registerInnerThread.setName("registerInnerThread");
                    registerInnerThread.setDaemon(runThreadsAsDaemons);
                    registerInnerThread.start();

                    try { Thread.sleep(Math.round(registrationBurstDelay / 8)); } catch (InterruptedException ex) {  }

                    phoneStatsTable.setValueAt(registerCounter + 1, 1, 1); // ProcessingInstance
                    serviceLoopProgressBar.setValue(registerCounter);
                    registerCounter++;
                }

                serviceLoopProgressBar.setEnabled(false); serviceLoopProgressBar.setValue(0);
                lastRegisteredActiveCount = 0;
                showStatus("Receiving final PBX registration responses...", true, true);
                while (lastRegisteredActiveCount != registeredActiveCount ) // This codeblock waits until no more PBX registries are detected in time before doing another last (extra) register round
                {
                    lastRegisteredActiveCount = registeredActiveCount;
                    try { Thread.sleep(1000); } catch (InterruptedException ex) {  }
                }

// Setting the Human Answer Simulator Settings
                humanResponseSimulator("1");

                if (callCenterIsNetManaged)
                {
                    netManagerInboundServerToggleButton.setEnabled(callCenterIsNetManaged);
                    netManagerInboundServerToggleButton.setSelected(callCenterIsNetManaged);
                    enableInboundNetManagerServer(true);
                }

                serviceLoopProgressBar.setValue(0); serviceLoopProgressBar.setEnabled(false);
                registerToggleButton.setForeground(Color.BLUE);
                phoneStatsTable.setValueAt("-", 1, 1); // ProcessingInstance
        //		reRegistering = false;

                muteAudioToggleButton.setEnabled(true);
                humanResponseSimulatorToggleButton.setEnabled(true);
                setAutoSpeed(false);

//                // Setup the infrequent SystemStats Timer
//                updateSystemStatsTimer.cancel(); updateSystemStatsTimer.purge();
//                showStatus("updateSystemStatsTimer Canceled!", true, true);
//                updateSystemStatsTimer = new Timer(); updateSystemStatsTimer.scheduleAtFixedRate(new UpdateSystemStatsTimer(eCallCenterGUI), (long)(0), updateSystemStatsTimerSlowInterval);
//                showStatus("updateSystemStatsTimer Scheduled immediate at " + Math.round(updateSystemStatsTimerSlowInterval / 1000) + " Sec Interval", true, true);

                // Setup the infrequent ReRegister to Proxy timer
                reRegisterTimer = new Timer(); reRegisterTimer.scheduleAtFixedRate(new ReRegisterTimer(eCallCenterGUI), (SoftPhone.getRegisterLoginTimeout() * 1000L), (SoftPhone.getRegisterLoginTimeout() * 1000L)); // milliSec
                showStatus("reRegisterTimer        Scheduled " + SoftPhone.getRegisterLoginTimeout() + " Sec postponed at " + SoftPhone.getRegisterLoginTimeout() + " Sec Interval", true, true);
                isRegistering = false;
            }
        });
        registerThread.setName("registerThread");
        registerThread.setDaemon(runThreadsAsDaemons);
        registerThread.start();

    }

    /**
     *
     */
    public void unRegister() {
	Thread registerToggleButtonActionPerformedThread5 = new Thread( allThreadsGroup, new Runnable()
        {
            @Override
            @SuppressWarnings("static-access")
            public void run()
            {
                String[] status = new String[2];
//              myClickOffSoundTool.play();
                runCampaignCounter = 0;

                showStatus("Sending Unregister requests to PBX...", true, true);
                registerToggleButton.setSelected(false);
                registerCounter = 0;
                while (registerCounter < outboundSoftPhonesAvailable) // Starts looping through the user-range
                {
                    SoftPhone thisSoftPhoneInstance =  (SoftPhone) threadArray[registerCounter]; // Get the reference to the SoftPhone object in the loop
                    if ( thisSoftPhoneInstance != null)
                    {
                        status = thisSoftPhoneInstance.userInput(REGISTERBUTTON, "0", "", ""); // 1 ringResponseDelay, 2 BusyPercentage, 3 EndDelay
                        if (status[0].equals("1")) { showStatus("Registration Error: " + status[1], true, true); }
                        try { Thread.sleep(registrationBurstDelay); } catch (InterruptedException ex) {  }
                    }
                    phoneStatsTable.setValueAt(registerCounter + 1, 1, 1); // ProcessingInstance
                    serviceLoopProgressBar.setValue(registerCounter);
                    registerCounter++;
                }
                serviceLoopProgressBar.setEnabled(false); serviceLoopProgressBar.setValue(0);
                // Do another round to recheck and redo the leftovers
                registerCounter = 0;
                int lastRegisteredActiveCount = 0;
                showStatus("Receiving delayed PBX Unregister responses...", true, true);
                while (lastRegisteredActiveCount != registeredActiveCount ) // This codeblock waits until no more PBX registries are detected in time before doing another last (extra) register round
                {
                    lastRegisteredActiveCount = registeredActiveCount;
                    try { Thread.sleep(1000); } catch (InterruptedException ex) {  }
                }
                showStatus("Sending Unregister requests to PBX completed", true, true);
                registerToggleButton.setForeground(Color.BLACK);
                phoneStatsTable.setValueAt("-", 1, 1); // ProcessingInstance
            }
        });
        registerToggleButtonActionPerformedThread5.setName("registerToggleButtonActionPerformed5");
        registerToggleButtonActionPerformedThread5.setDaemon(runThreadsAsDaemons);
        registerToggleButtonActionPerformedThread5.start();

        humanResponseSimulator("0"); // Human Simulator must wait for reregistering
    }

    /**
     *
     */
    public void reRegister()
    {
//	reRegistering = true;
	Thread reRegisterThread = new Thread( allThreadsGroup, new Runnable()
	{
	    @Override
	    @SuppressWarnings({"static-access", "empty-statement"})
	    public void run()
	    {
                String[] status = new String[2];

// Unregister before ReRegistering
                if ((registerToggleButton.isSelected() && (boundMode.equals("Inbound"))))
                {
                    runCampaignCounter = 0;
                    callCenterIsOutBound = false;

                    showStatus("Sending Unregister requests to PBX...", true, true);
                    registerToggleButton.setSelected(false);
                    registerCounter = 0;
                    while (registerCounter < outboundSoftPhonesAvailable) // Starts looping through the user-range
                    {
                        SoftPhone thisSoftPhoneInstance =  (SoftPhone) threadArray[registerCounter]; // Get the reference to the SoftPhone object in the loop
                        if ( thisSoftPhoneInstance != null)
                        {
                            status = thisSoftPhoneInstance.userInput(REGISTERBUTTON, "0", "", ""); // 1 ringResponseDelay, 2 BusyPercentage, 3 EndDelay
                            if (status[0].equals("1")) { showStatus("UnRegistration Error: " + status[1], true, true); }
                            try { Thread.sleep(registrationBurstDelay); } catch (InterruptedException ex) {  }
                        }
                        phoneStatsTable.setValueAt(registerCounter + 1, 1, 1); // ProcessingInstance
                        serviceLoopProgressBar.setValue(registerCounter);
                        registerCounter++;
                    }
                    serviceLoopProgressBar.setEnabled(false); serviceLoopProgressBar.setValue(0);
                    // Do another round to recheck and redo the leftovers
                    registerCounter = 0;
                    int lastRegisteredActiveCount = 0;
                    showStatus("Receiving delayed PBX Unregister responses...", true, true);
                    while (lastRegisteredActiveCount != registeredActiveCount ) // This codeblock waits until no more PBX registries are detected in time before doing another last (extra) register round
                    {
                        lastRegisteredActiveCount = registeredActiveCount;
                        try { Thread.sleep(1000); } catch (InterruptedException ex) {  }
                    }
                    showStatus("Sending re-Unregister requests to PBX completed", true, true);
                    registerToggleButton.setForeground(Color.BLACK);
                    phoneStatsTable.setValueAt("-", 1, 1); // ProcessingInstance
                }

                registerToggleButton.setEnabled(true);
                registerToggleButton.setSelected(true);
                autoSpeedToggleButton.setSelected(false); // We dont need auto speed and system-load checking in inbound test mode
                moveVMUsageMeter(0, true);
		muteAudioToggleButton.setEnabled(false);
		humanResponseSimulatorToggleButton.setEnabled(false);

// Extra initial re-registrations
                showStatus("Sending initial re-registration requests to PBX...", true, true);
                serviceLoopProgressBar.setValue(0);
                serviceLoopProgressBar.setEnabled(true);
                registerCounter = 0;
                registerToggleButton.setSelected(true);
                while (registerCounter < outboundSoftPhonesAvailable) // Starts looping through the user-range
                {
                    SoftPhone thisSoftPhoneInstance =  (SoftPhone) threadArray[registerCounter]; // Get the reference to the SoftPhone object in the loop
                    if ( thisSoftPhoneInstance != null)
                    {
                        status = thisSoftPhoneInstance.userInput(REGISTERBUTTON, "1", "", ""); // 1 ringResponseDelay, 2 BusyPercentage, 3 EndDelay
                        if (status[0].equals("1")) { showStatus("Re-Registration Error: " + status[1], true, true); }
                        try { Thread.sleep(registrationBurstDelay); } catch (InterruptedException ex) {  }
                    }
                    phoneStatsTable.setValueAt(registerCounter + 1, 1, 1); // ProcessingInstance
                    serviceLoopProgressBar.setValue(registerCounter);
                    registerCounter++;
                }

                serviceLoopProgressBar.setEnabled(false);
                serviceLoopProgressBar.setValue(0);
                // Wait for delayed registerResponses from PBX
                int lastRegisteredActiveCount = 0;
                showStatus("Receiving initial delayed PBX re-registration responses...", true, true);
                while (lastRegisteredActiveCount != registeredActiveCount ) // This codeblock waits until no more PBX registries are detected in time before doing another last (extra) register round
                {
                    lastRegisteredActiveCount = registeredActiveCount;
                    try { Thread.sleep(1000); } catch (InterruptedException ex) {  }
                }

// Extra re-registrations
                serviceLoopProgressBar.setValue(0);
                serviceLoopProgressBar.setEnabled(true);
                registerCounter = 0;
                showStatus("Sending extra re-registration requests to PBX...", true, true);
                while ((registeredActiveCount < outboundSoftPhonesAvailable) && (registerCounter < outboundSoftPhonesAvailable)) // Starts looping through the user-range
                {
                    SoftPhone thisSoftPhoneInstance =  (SoftPhone) threadArray[registerCounter]; // Get the reference to the SoftPhone object in the loop
                    if ( thisSoftPhoneInstance != null)
                    {
                        if (thisSoftPhoneInstance.getLoginState() == thisSoftPhoneInstance.LOGINSTATE_UNREGISTERED)
                        {
                            status = thisSoftPhoneInstance.userInput(REGISTERBUTTON, "1", "", ""); // 1 ringResponseDelay, 2 BusyPercentage, 3 EndDelay
                            if (status[0].equals("1")) { showStatus("Registration Error: " + status[1], true, true); }
                            try { Thread.sleep(Math.round(registrationBurstDelay / 4)); } catch (InterruptedException ex) {  }
                        }
                    }
                    phoneStatsTable.setValueAt(registerCounter + 1, 1, 1); // ProcessingInstance
                    serviceLoopProgressBar.setValue(registerCounter);
                    registerCounter++;
                }

                serviceLoopProgressBar.setEnabled(false); serviceLoopProgressBar.setValue(0);
                lastRegisteredActiveCount = 0;
                showStatus("Receiving extra PBX re-registration responses...", true, true);
                while (lastRegisteredActiveCount != registeredActiveCount ) // This codeblock waits until no more PBX registries are detected in time before doing another last (extra) register round
                {
                    lastRegisteredActiveCount = registeredActiveCount;
                    try { Thread.sleep(1000); } catch (InterruptedException ex) {  }
                }

// Extra final re-registrations
                serviceLoopProgressBar.setValue(0);
                serviceLoopProgressBar.setEnabled(true);
                registerCounter = 0;
                showStatus("Sending final re-registration requests to PBX...", true, true);
                while ((registeredActiveCount < outboundSoftPhonesAvailable) && (registerCounter < outboundSoftPhonesAvailable)) // Starts looping through the user-range
                {
                    SoftPhone thisSoftPhoneInstance =  (SoftPhone) threadArray[registerCounter]; // Get the reference to the SoftPhone object in the loop
                    if ( thisSoftPhoneInstance != null)
                    {
                        if (thisSoftPhoneInstance.getLoginState() == thisSoftPhoneInstance.LOGINSTATE_UNREGISTERED)
                        {
                            status = thisSoftPhoneInstance.userInput(REGISTERBUTTON, "1", "", ""); // 1 ringResponseDelay, 2 BusyPercentage, 3 EndDelay
                            if (status[0].equals("1")) { showStatus("Registration Error: " + status[1], true, true); }
                            try { Thread.sleep(Math.round(registrationBurstDelay / 8)); } catch (InterruptedException ex) {  }
                        }
                    }
                    phoneStatsTable.setValueAt(registerCounter + 1, 1, 1); // ProcessingInstance
                    serviceLoopProgressBar.setValue(registerCounter);
                    registerCounter++;
                }

                serviceLoopProgressBar.setEnabled(false); serviceLoopProgressBar.setValue(0);
                lastRegisteredActiveCount = 0;
                showStatus("Receiving final PBX re-registration responses...", true, true);
                while (lastRegisteredActiveCount != registeredActiveCount ) // This codeblock waits until no more PBX registries are detected in time before doing another last (extra) register round
                {
                    lastRegisteredActiveCount = registeredActiveCount;
                    try { Thread.sleep(1000); } catch (InterruptedException ex) {  }
                }

                if (callCenterIsNetManaged)
                {
                    netManagerInboundServerToggleButton.setEnabled(callCenterIsNetManaged);
                    netManagerInboundServerToggleButton.setSelected(callCenterIsNetManaged);
                    enableInboundNetManagerServer(true);
                }

		serviceLoopProgressBar.setValue(0); serviceLoopProgressBar.setEnabled(false);
		registerToggleButton.setForeground(Color.BLUE);
		phoneStatsTable.setValueAt("-", 1, 1); // ProcessingInstance
//		reRegistering = false;

		muteAudioToggleButton.setEnabled(true);
		humanResponseSimulatorToggleButton.setEnabled(true);
                setAutoSpeed(false);
                return;
	    }
	});
	reRegisterThread.setName("reRegisterThread");
	reRegisterThread.setDaemon(runThreadsAsDaemons);
	reRegisterThread.start();
    }

    final ECallCenter21 eCallCenterGUI = this; // A thread doesn't inherit local varables, but it does local finals / constants

    /**
     *
     * @param sipstateParam
     * @param lastsipstateParam
     * @param loginstateParam
     * @param softphoneActivityParam
     * @param softPhoneInstanceIdParam
     * @param destinationParam
     */
    @Override
    synchronized public void sipstateUpdate(final int sipstateParam, final int lastsipstateParam, final int loginstateParam, final int softphoneActivityParam, final int softPhoneInstanceIdParam, Destination destinationParam)
    {
        // Get Current Time of Event
        long timestamp = (new java.util.Date().getTime());
        // Translates / converts the softphoneinstanceid to row & col coordinate
        myCoordinate = getSoftPhoneCoordinate(softPhoneInstanceIdParam);
        // Display the SoftPhone Instance invoking this method
        captionTable.setValueAt(icons.getProcessChar() + softPhoneInstanceIdParam, 2, 0);

        // setting the Idle Symbol depending on Registered or not
        if (loginstateParam == SoftPhone.LOGINSTATE_REGISTERED)
        {
            icons.setIdleIsRegistered(true);
        }
        else
        {
            icons.setIdleIsRegistered(false);
        }

        if ( softphoneActivityParam == SoftPhone.SOFTPHONE_ACTIVITY_REGISTRATION)
        {
            if (loginstateParam == SoftPhone.LOGINSTATE_REGISTERED)
            {
                if (registeredActiveCount < outboundSoftPhonesAvailable) // prevents going out of range
                {
                    registeredActiveCount++;
                    captionTable.setValueAt(registeredActiveCount, 2, 1);
//                    phonesPoolTable.setValueAt(idleRegisteredSymbol, myCoordinate.getRow(), myCoordinate.getColumn());
                    phonesPoolTable.setValueAt(icons.getIdleRegisteredSymbol(), myCoordinate.getRow(), myCoordinate.getColumn());
                }
            }
            else
            {
                if (registeredActiveCount > 0)
                {
                    registeredActiveCount--;
                    captionTable.setValueAt(registeredActiveCount, 2, 1);
//                    phonesPoolTable.setValueAt(idleRegisteredSymbol, myCoordinate.getRow(), myCoordinate.getColumn());
                    phonesPoolTable.setValueAt(icons.getIdleRegisteredSymbol(), myCoordinate.getRow(), myCoordinate.getColumn());
                }
            }
        }

        // Set the counters and update the Database
        if (( sipstateParam != lastsipstateParam) && (softphoneActivityParam == SoftPhone.SOFTPHONE_ACTIVITY_NORMAL))
        {
            // Write Counters to RAM and Storage
            if (lastsipstateParam == SoftPhone.SIPSTATE_OFF)                    { offActiveCount--;                  }
            if (lastsipstateParam == SoftPhone.SIPSTATE_ON)                     { campaignStat.subOnAC();            }
            if (lastsipstateParam == SoftPhone.SIPSTATE_IDLE)                   { campaignStat.subIdleAC();          }
            if (lastsipstateParam == SoftPhone.SIPSTATE_WAIT_CONNECT)           { campaignStat.subConnectingAC();    } // Connecting
            if (lastsipstateParam == SoftPhone.SIPSTATE_WAIT_PROV)              { campaignStat.subTryingAC();        } // Trying
            if (lastsipstateParam == SoftPhone.SIPSTATE_WAIT_FINAL)             { campaignStat.subCallingAC();       } // Calling
            if (lastsipstateParam == SoftPhone.SIPSTATE_WAIT_ACK)               { campaignStat.subAcceptingAC();     } // Accepted
            if (lastsipstateParam == SoftPhone.SIPSTATE_RINGING)                { campaignStat.subRingingAC();       } // Ringing
            if (lastsipstateParam == SoftPhone.SIPSTATE_ESTABLISHED)            { campaignStat.subTalkingAC();       } // Talking

            if (sipstateParam == SoftPhone.SIPSTATE_OFF)			{ offActiveCount++;                  }
            if (sipstateParam == SoftPhone.SIPSTATE_ON)				{ campaignStat.addOnAC();            }
            if (sipstateParam == SoftPhone.SIPSTATE_IDLE)			{ campaignStat.addIdleAC();          }

            if (sipstateParam == SoftPhone.SIPSTATE_WAIT_CONNECT)
            {
                campaignStat.addConnectingAC(); campaignStat.addConnectingTT();
                if (callCenterIsOutBound) { destinationParam.setConnectingTimestamp(timestamp); dbClient.updateDestination(destinationParam); }
            }
            if (sipstateParam == SoftPhone.SIPSTATE_WAIT_PROV)
            {
                campaignStat.addTryingAC(); campaignStat.addTryingTT();
                if (callCenterIsOutBound) { destinationParam.setTryingTimestamp(timestamp); dbClient.updateDestination(destinationParam); }
            }
            if (sipstateParam == SoftPhone.SIPSTATE_WAIT_FINAL)
            {
                campaignStat.addCallingAC(); campaignStat.addCallingTT(); destinationParam.addCallingAttempts();
                if (callCenterIsOutBound) { destinationParam.setCallingTimestamp(timestamp); dbClient.updateDestination(destinationParam); }
            }
            if (sipstateParam == SoftPhone.SIPSTATE_WAIT_ACK)
            {
                campaignStat.addAcceptingAC(); campaignStat.addAcceptingTT();
                if (callCenterIsOutBound) { destinationParam.setAcceptingTimestamp(timestamp); dbClient.updateDestination(destinationParam); }
            }
            if (sipstateParam == SoftPhone.SIPSTATE_RINGING)
            {
                campaignStat.addRingingAC(); campaignStat.addRingingTT();
                if (callCenterIsOutBound) { destinationParam.setRingingTimestamp(timestamp); dbClient.updateDestination(destinationParam); }
            }
            if (sipstateParam == SoftPhone.SIPSTATE_ESTABLISHED)
            {
                campaignStat.addTalkingAC(); campaignStat.addTalkingTT();
                if (callCenterIsOutBound) { destinationParam.setTalkingTimestamp(timestamp); dbClient.updateDestination(destinationParam); }
            }

            if (sipstateParam == SoftPhone.SIPSTATE_TRANSITION_LOCALCANCEL)
            {
                campaignStat.addLocalCancelTT();
                if (callCenterIsOutBound) { destinationParam.setLocalCancelingTimestamp(timestamp); dbClient.updateDestination(destinationParam); }
            }
            if (sipstateParam == SoftPhone.SIPSTATE_TRANSITION_REMOTECANCEL)
            {
                campaignStat.addRemoteCancelTT();
                if (callCenterIsOutBound) { destinationParam.setRemoteCancelingTimestamp(timestamp); dbClient.updateDestination(destinationParam); }
            }
            if (sipstateParam == SoftPhone.SIPSTATE_TRANSITION_LOCALBUSY)
            {
                campaignStat.addLocalBusyTT();
                if (callCenterIsOutBound) { destinationParam.setLocalBusyTimestamp(timestamp); dbClient.updateDestination(destinationParam); }
            }
            if (sipstateParam == SoftPhone.SIPSTATE_TRANSITION_REMOTEBUSY)
            {
                campaignStat.addRemoteBusyTT();
                if (callCenterIsOutBound) { destinationParam.setRemoteBusyTimestamp(timestamp); dbClient.updateDestination(destinationParam); }
            }
            if (sipstateParam == SoftPhone.SIPSTATE_TRANSITION_LOCALBYE)
            {
                campaignStat.addLocalByeTT();
                if (callCenterIsOutBound) { destinationParam.setLocalByeTimestamp(timestamp); dbClient.updateDestination(destinationParam); }
            }
            if (sipstateParam == SoftPhone.SIPSTATE_TRANSITION_REMOTEBYE)
            {
                campaignStat.addRemoteByeTT();
                if (callCenterIsOutBound) { destinationParam.setRemoteByeTimestamp(timestamp); dbClient.updateDestination(destinationParam); }
            }
        }

        // Display the counters
        if ( softphoneActivityParam == SoftPhone.SOFTPHONE_ACTIVITY_NORMAL)
        {
            // Display the counters in the phonestats table
            phoneStatsTable.setValueAt(campaignStat.getOnAC(),		2, 1);
            phoneStatsTable.setValueAt(campaignStat.getIdleAC(),	4, 1);
            phoneStatsTable.setValueAt(campaignStat.getConnectingAC(),	5, 1);
            phoneStatsTable.setValueAt(campaignStat.getTryingAC(),	6, 1);
            phoneStatsTable.setValueAt(campaignStat.getCallingAC(),	7, 1);
            phoneStatsTable.setValueAt(campaignStat.getRingingAC(),	8, 1);
            phoneStatsTable.setValueAt(campaignStat.getAcceptingAC(),	9, 1);
            phoneStatsTable.setValueAt(campaignStat.getTalkingAC(),	10, 1);
            phoneStatsTable.setValueAt(campaignStat.getCallingTT(),	11, 1);
            phoneStatsTable.setValueAt(campaignStat.getTalkingTT(),	12, 1);

            // Display the counters in the caption table
            captionTable.setValueAt(campaignStat.getOnAC(),                                     1, 0);
            captionTable.setValueAt(campaignStat.getIdleAC(),                                   1, 1);
            captionTable.setValueAt(campaignStat.getConnectingAC(),                             1, 2); // Connect
            captionTable.setValueAt(campaignStat.getConnectingTT(),                             2, 2); // Connect
            captionTable.setValueAt(campaignStat.getTryingAC(),                                 1, 3); // Trying
            captionTable.setValueAt(campaignStat.getTryingTT(),                                 2, 3); // Trying
            captionTable.setValueAt(campaignStat.getCallingAC(),                                1, 4); // Calling
            captionTable.setValueAt(campaignStat.getCallingTT(),                                2, 4); // Calling
            captionTable.setValueAt(campaignStat.getRingingAC(),                                1, 5); // Ringing
            captionTable.setValueAt(campaignStat.getRingingTT(),                                2, 5); // Ringing
            captionTable.setValueAt(campaignStat.getAcceptingAC(),                              1, 6); // Accepting
            captionTable.setValueAt(campaignStat.getAcceptingTT(),                              2, 6); // Accepted
            captionTable.setValueAt(campaignStat.getTalkingAC(),                                1, 7); // Talking
            captionTable.setValueAt(campaignStat.getTalkingTT(),                                2, 7); // Talking
            captionTable.setValueAt(icons.getUpChar() + campaignStat.getLocalCancelTT(),	1, 8);
            captionTable.setValueAt(icons.getDownChar() + campaignStat.getRemoteCancelTT(),	2, 8);
            captionTable.setValueAt(icons.getUpChar() + campaignStat.getLocalBusyTT(),		1, 9);
            captionTable.setValueAt(icons.getDownChar() + campaignStat.getRemoteBusyTT(),	2, 9);
            captionTable.setValueAt(icons.getUpChar() + campaignStat.getLocalByeTT(),		1, 10);
            captionTable.setValueAt(icons.getDownChar() + campaignStat.getRemoteByeTT(),	2, 10);
        }

//	Thread sipstateUpdateThread = new Thread( allThreadsGroup, new Runnable()
//	sipstateUpdateThreadPool.execute(new Runnable()
//        {
//            @Override
//            public void run()
//            {
                // Display the related icon of the current instance in the phonesPool table
                if (( softphoneActivityParam == SoftPhone.SOFTPHONE_ACTIVITY_NORMAL) || ( softphoneActivityParam == SoftPhone.SOFTPHONE_ACTIVITY_REFRESH))
                {
                    if (sipstateParam == SoftPhone.SIPSTATE_OFF)		    { phonesPoolTable.setValueAt(icons.getOffSymbol(), myCoordinate.getRow(), myCoordinate.getColumn()); } // phonesPoolTable.setValueAt(icons.getIdleRegisteredSymbol(), myCoordinate.getRow(), myCoordinate.getColumn());
                    if (sipstateParam == SoftPhone.SIPSTATE_ON)			    { phonesPoolTable.setValueAt(icons.getOnSymbol(), myCoordinate.getRow(), myCoordinate.getColumn()); }
                    if (sipstateParam == SoftPhone.SIPSTATE_IDLE)		    { phonesPoolTable.setValueAt(icons.getIdleSymbol(), myCoordinate.getRow(), myCoordinate.getColumn()); }
                    if (sipstateParam == SoftPhone.SIPSTATE_WAIT_CONNECT)	    { phonesPoolTable.setValueAt(icons.getConnectSymbol(), myCoordinate.getRow(), myCoordinate.getColumn()); }
                    if (sipstateParam == SoftPhone.SIPSTATE_WAIT_PROV)		    { phonesPoolTable.setValueAt(icons.getTrySymbol(), myCoordinate.getRow(), myCoordinate.getColumn()); }
                    if (sipstateParam == SoftPhone.SIPSTATE_WAIT_FINAL)		    { phonesPoolTable.setValueAt(icons.getCallSymbol(), myCoordinate.getRow(), myCoordinate.getColumn()); }
                    if (sipstateParam == SoftPhone.SIPSTATE_WAIT_ACK)		    { phonesPoolTable.setValueAt(icons.getAcceptSymbol(), myCoordinate.getRow(), myCoordinate.getColumn()); }
                    if (sipstateParam == SoftPhone.SIPSTATE_RINGING)		    { phonesPoolTable.setValueAt(icons.getRingSymbol(), myCoordinate.getRow(), myCoordinate.getColumn()); }
                    if (sipstateParam == SoftPhone.SIPSTATE_ESTABLISHED)	    { phonesPoolTable.setValueAt(icons.getTalkSymbol(), myCoordinate.getRow(), myCoordinate.getColumn()); }
                    if (sipstateParam == SoftPhone.SIPSTATE_TRANSITION_LOCALBUSY)   { phonesPoolTable.setValueAt(icons.getLocalBusySymbol(), myCoordinate.getRow(), myCoordinate.getColumn()); }
                    if (sipstateParam == SoftPhone.SIPSTATE_TRANSITION_REMOTEBUSY)  { phonesPoolTable.setValueAt(icons.getRemoteBusySymbol(), myCoordinate.getRow(), myCoordinate.getColumn()); }
                    if (sipstateParam == SoftPhone.SIPSTATE_TRANSITION_LOCALBYE)    { phonesPoolTable.setValueAt(icons.getLocalByeSymbol(), myCoordinate.getRow(), myCoordinate.getColumn()); }
                    if (sipstateParam == SoftPhone.SIPSTATE_TRANSITION_REMOTEBYE)   { phonesPoolTable.setValueAt(icons.getRemoteByeSymbol(), myCoordinate.getRow(), myCoordinate.getColumn()); }
                }

                if ( softphoneActivityParam == SoftPhone.SOFTPHONE_ACTIVITY_MAINTENANCE)
                {
        //            phonesPoolTable.setValueAt(actionSymbol, myCoordinate.getRow(), myCoordinate.getColumn());
                    phonesPoolTable.setValueAt(icons.getProcessSymbol(), myCoordinate.getRow(), myCoordinate.getColumn());
                }
                else if ( softphoneActivityParam == SoftPhone.SOFTPHONE_ACTIVITY_REGISTRATION)
                {
                    phoneStatsTable.setValueAt(registeredActiveCount, 3, 1);
                }
//            }
//        });
//        sipstateUpdateThread.setName("sipstateUpdateThread");
//        sipstateUpdateThread.setDaemon(runThreadsAsDaemons);
//        sipstateUpdateThread.start();
    }

    /**
     *
     * @param responseCodeParam
     * @param responseReasonPhraseParam
     * @param softPhoneInstanceIdParam
     * @param destinationParam
     */
    @Override
    synchronized public void responseUpdate(final int responseCodeParam, final String responseReasonPhraseParam, final int softPhoneInstanceIdParam, final Destination destinationParam) // Mainly used for the systemStatsTable in this gui
    {
        if ( destinationParam.getResponseStatusCode() == 404 ) { dbClient.updateDestination(destinationParam); }

        // infoTally, successTally, redirectionTally, clientErrorTally, serverErrorTally, generalErrorTally;
        if ((responseCodeParam >= 100) && (responseCodeParam < 200)) { infoTally++; }
        if ((responseCodeParam >= 200) && (responseCodeParam < 300)) { successTally++; }
        if ((responseCodeParam >= 300) && (responseCodeParam < 400))
        {
            redirectionTally++;
            logToApplication("Redirection: " + responseCodeParam + " " + responseReasonPhraseParam + " Instance: " + softPhoneInstanceIdParam + " Phonenumber: " + destinationParam.getDestination());
        }
        if ((responseCodeParam >= 400) && (responseCodeParam < 500))
        {
            clientErrorTally++;
            if ((responseCodeParam != 401) && (responseCodeParam != 486))
            { logToApplication("Client Error: " + responseCodeParam + " " + responseReasonPhraseParam + " Instance: " + softPhoneInstanceIdParam + " Phonenumber: " + destinationParam.getDestination()); }
        }
        if ((responseCodeParam >= 500) && (responseCodeParam < 600))
        {
            serverErrorTally++;
            logToApplication("Server Error: " + responseCodeParam + " " + responseReasonPhraseParam + " Instance: " + softPhoneInstanceIdParam + " Phonenumber: " + destinationParam.getDestination());
        }
        if ((responseCodeParam >= 600) && (responseCodeParam < 700))
        {
            generalErrorTally++;
            logToApplication("General Error: " + responseCodeParam + " " + responseReasonPhraseParam + " Instance: " + softPhoneInstanceIdParam + " Phonenumber: " + destinationParam.getDestination());
        }
        if (responseCodeParam == 0)				     { timeoutTally++; }

	Thread responseUpdateThread = new Thread( allThreadsGroup, new Runnable()
//	responseUpdateThreadPool.execute(new Runnable()
        {
            @Override
            public void run()
            {
		responseStatsTable.setValueAt(infoTally, 0, 2); // onCell
		responseStatsTable.setValueAt(successTally, 1, 2); // idleCell
		responseStatsTable.setValueAt(redirectionTally, 2, 2); // wait_prov
		responseStatsTable.setValueAt(clientErrorTally, 3, 2); // wait_final
		responseStatsTable.setValueAt(serverErrorTally, 4, 2); // wait_act
		responseStatsTable.setValueAt(generalErrorTally, 5, 2); // calling
		responseStatsTable.setValueAt(timeoutTally, 11, 2); // calling
            }
        });
        responseUpdateThread.setName("responseUpdateThread");
        responseUpdateThread.setDaemon(runThreadsAsDaemons);
        responseUpdateThread.start();
    }

    /**
     *
     */
    public void startEPhoneGUI()
    {
        Thread startEPhoneGUIThread = new Thread(new Runnable()
        {
            @Override
            @SuppressWarnings({"static-access"})
            public void run()
            {
                String[] status = new String[2]; 
                showStatus("Starting EPhone", true, true);
                status[0] = "0"; status[1] = "";
                shell.startEPhone("");
                if (status[0].equals("0")) { showStatus(status[1], true, true); } else { showStatus(status[1], true, true); }
                return;
            }
        });
        startEPhoneGUIThread.setName("startEPhoneGUIThread");
        startEPhoneGUIThread.setDaemon(runThreadsAsDaemons);
        startEPhoneGUIThread.start();
    }

    /**
     *
     * @param messageParam
     * @param logToApplicationParam
     * @param logToFileParam
     */
    @Override
    @SuppressWarnings("static-access")
    synchronized public void showStatus(String messageParam, boolean logToApplicationParam, boolean logToFileParam)
    {
	statusBar.setText(messageParam);
	if (logToApplicationParam) {logToApplication(messageParam);}
//	if (logToFileParam) {logToFile(messageParam);}
    }

    /**
     *
     */
    @Override
    synchronized public void resetLog() { textLogArea.setText(""); }

    /**
     *
     * @param softPhoneInstanceIdParam
     * @return
     */
    public Coordinate getSoftPhoneCoordinate(int softPhoneInstanceIdParam)
    {
	Coordinate thisCoordinate = new Coordinate();
	thisCoordinate.setRow(Math.round(softPhoneInstanceIdParam / eCallCenterGUI.phonesPoolTablePreferredColumns ));
	thisCoordinate.setColumn((softPhoneInstanceIdParam - (Math.round(softPhoneInstanceIdParam / phonesPoolTablePreferredColumns)) * phonesPoolTablePreferredColumns));
	return thisCoordinate;
    }

    /**
     *
     * @param coordinateParam
     * @return
     */
    public int getSoftPhoneInstance(Coordinate coordinateParam)
    {
	int instance = (coordinateParam.getRow() * phonesPoolTablePreferredColumns ) + coordinateParam.getColumn();
	return instance;
    }

    /**
     *
     * @return
     */
    public int getSelfDestructCounterLimit() { return selfDestructCounterLimit; }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JPanel aboutPanel;
    javax.swing.JLabel acceptingLabel;
    javax.swing.JPanel acceptingPanel;
    javax.swing.JTextField activationCodeField;
    javax.swing.JPanel activationCodePanel;
    javax.swing.JLabel answerLabel;
    javax.swing.JPanel answerPanel;
    javax.swing.JButton applyVergunningButton;
    javax.swing.JPanel authenticationPanel;
    javax.swing.JToggleButton autoSpeedToggleButton;
    javax.swing.JTextArea brandDescriptionLabel;
    javax.swing.JLabel brandLabel;
    javax.swing.JPanel buttonPanel;
    javax.swing.JButton callButton;
    javax.swing.JPanel callCenterPanel;
    javax.swing.JLabel callSpeedLabel;
    javax.swing.JSlider callSpeedSlider;
    javax.swing.JLabel callSpeedValue;
    javax.swing.JLabel callingLabel;
    javax.swing.JPanel callingPanel;
    javax.swing.JLabel callingTallyLimitLabel;
    javax.swing.JSlider callingTallyLimitSlider;
    javax.swing.JLabel callingTallyLimitValue;
    javax.swing.JComboBox campaignComboBox;
    javax.swing.JLabel campaignLabel;
    javax.swing.JProgressBar campaignProgressBar;
    javax.swing.JScrollPane campaignScrollPane;
    javax.swing.JTable campaignTable;
    javax.swing.JLabel cancelLabel;
    javax.swing.JPanel cancelPanel;
    javax.swing.JTable captionTable;
    javax.swing.JTextField clientIPField;
    javax.swing.JLabel clientIPLabel;
    javax.swing.JTextField clientPortField;
    javax.swing.JLabel clientPortLabel;
    javax.swing.JPanel colorMaskPanel;
    javax.swing.JLabel connectingLabel;
    javax.swing.JPanel connectingPanel;
    javax.swing.JLabel connectingTallyLimitLabel;
    javax.swing.JSlider connectingTallyLimitSlider;
    javax.swing.JLabel connectingTallyLimitValue;
    javax.swing.JPanel controlButtonPanel;
    javax.swing.JPanel controlSliderPanel;
    javax.swing.JPanel controlsPanel;
    javax.swing.JTextArea copyrightLabel;
    javax.swing.JToggleButton debugToggleButton;
    javax.swing.JScrollPane destinationScrollPane;
    javax.swing.JTextArea destinationTextArea;
    javax.swing.JLabel displayLabel;
    javax.swing.JPanel displayPanel;
    javax.swing.JTextField domainField;
    javax.swing.JLabel domainLabel;
    javax.swing.JCheckBox enableDisplayCheckBox;
    javax.swing.JButton endButton;
    javax.swing.JLabel establishedTallyLimitLabel;
    javax.swing.JSlider establishedTallyLimitSlider;
    javax.swing.JLabel establishedTallyLimitValue;
    javax.swing.JPanel graphInnerPanel;
    javax.swing.JPanel graphPanel;
    javax.swing.JLabel heapMemFreeThresholdLabel;
    javax.swing.JSlider heapMemFreeThresholdSlider;
    javax.swing.JLabel heapMemFreeThresholdValue;
    javax.swing.JToggleButton humanResponseSimulatorToggleButton;
    javax.swing.JCheckBox iconsCheckBox;
    javax.swing.JLabel iconsLabel;
    javax.swing.JLabel idleLabel;
    javax.swing.JPanel idlePanel;
    javax.swing.JLabel imageBrandLabel;
    javax.swing.JLabel imageIconLabel;
    javax.swing.JLabel imageLinkLabel;
    javax.swing.JLabel imagePostfixLabel;
    javax.swing.JLabel imageProductLabel;
    javax.swing.JLabel inboundEndDelayLabel;
    javax.swing.JSlider inboundEndDelaySlider;
    javax.swing.JLabel inboundEndDelayValue;
    javax.swing.JLabel inboundRingingResponseBusyRatioLabel;
    javax.swing.JSlider inboundRingingResponseBusyRatioSlider;
    javax.swing.JLabel inboundRingingResponseBusyRatioValue;
    javax.swing.JLabel inboundRingingResponseDelayLabel;
    javax.swing.JSlider inboundRingingResponseDelaySlider;
    javax.swing.JLabel inboundRingingResponseDelayValue;
    javax.swing.JPanel inboundSliderPanel;
    javax.swing.JScrollPane jScrollPane1;
    javax.swing.JLayeredPane layeredImagePane;
    javax.swing.JPanel licenseCodePanel;
    javax.swing.JPanel licenseDatePanel;
    javax.swing.JPanel licenseDetailsPanel;
    javax.swing.JScrollPane licenseDetailsScrollPane;
    javax.swing.JPanel licensePanel;
    javax.swing.JPanel licensePeriodPanel;
    javax.swing.JScrollPane licensePeriodScrollPane;
    javax.swing.JPanel licenseTypePanel;
    javax.swing.JPanel logPanel;
    javax.swing.JScrollPane logScrollPane;
    javax.swing.ButtonGroup lookAndFeelGroup;
    javax.swing.JPanel lookAndFeelPanel;
    javax.swing.JRadioButton lookAndFeelRButtonGTK;
    javax.swing.JRadioButton lookAndFeelRButtonMotif;
    javax.swing.JRadioButton lookAndFeelRButtonNimbus;
    javax.swing.JRadioButton lookAndFeelRButtonWindows;
    javax.swing.JPanel mainPanel;
    javax.swing.JLabel memFreeThresholdLabel;
    javax.swing.JSlider memFreeThresholdSlider;
    javax.swing.JLabel memFreeThresholdValue;
    javax.swing.JToggleButton muteAudioToggleButton;
    javax.swing.JLabel muteLabel;
    javax.swing.JPanel mutePanel;
    javax.swing.JPanel netConfigPanel;
    javax.swing.JToggleButton netManagerInboundServerToggleButton;
    javax.swing.JToggleButton netManagerOutboundServerToggleButton;
    javax.swing.JLabel onLabel;
    javax.swing.JPanel onPanel;
    javax.swing.JLabel orderLabel;
    javax.swing.JScrollPane orderStatsScrollPane;
    javax.swing.JTable orderTable;
    javax.swing.JPanel outboundSliderPanel;
    javax.swing.JPanel performanceMeterPanel;
    javax.swing.JLabel pfixLabel;
    javax.swing.JButton phoneButton;
    javax.swing.JPanel phoneDisplayPanel;
    javax.swing.JPanel phoneDisplayTabPanel;
    javax.swing.JLabel phoneStatsLabel;
    javax.swing.JScrollPane phoneStatsScrollPane;
    javax.swing.JTable phoneStatsTable;
    javax.swing.JTable phonesPoolTable;
    javax.swing.JScrollPane phonesPoolTableScrollPane;
    javax.swing.JToggleButton powerToggleButton;
    javax.swing.JPanel prefPhoneLinesPanel;
    javax.swing.JSlider prefPhoneLinesSlider;
    javax.swing.JTextField prefixField;
    javax.swing.JLabel primaryStatusDetailsLabel;
    javax.swing.JLabel primaryStatusLabel;
    javax.swing.JTextArea productDescriptionLabel;
    javax.swing.JLabel productLabel;
    javax.swing.JLabel proxyInfoLabel;
    javax.swing.JTextField pubIPField;
    javax.swing.JLabel pubIPLabel;
    javax.swing.JCheckBox registerCheckBox;
    javax.swing.JLabel registerLabel;
    javax.swing.JLabel registerSpeedLabel;
    javax.swing.JSlider registerSpeedSlider;
    javax.swing.JLabel registerSpeedValue;
    javax.swing.JToggleButton registerToggleButton;
    javax.swing.JLabel registeredLabel;
    javax.swing.JPanel registeredPanel;
    javax.swing.JLabel reponseStatsLabel;
    javax.swing.JButton requestVergunningButton;
    javax.swing.JButton resizeWindowButton;
    javax.swing.JScrollPane responseStatsScrollPane;
    javax.swing.JTable responseStatsTable;
    javax.swing.JLabel ringingLabel;
    javax.swing.JPanel ringingPanel;
    javax.swing.JToggleButton runCampaignToggleButton;
    javax.swing.JButton saveConfigurationButton;
    javax.swing.JCheckBox scanCheckBox;
    javax.swing.JLabel secondaryStatusDetailsLabel;
    javax.swing.JLabel secondaryStatusLabel;
    javax.swing.JLabel secretLabel;
    javax.swing.JTextField serverIPField;
    javax.swing.JLabel serverIPLabel;
    javax.swing.JTextField serverPortField;
    javax.swing.JLabel serverPortLabel;
    javax.swing.JProgressBar serviceLoopProgressBar;
    javax.swing.JPanel sipInfoPanel;
    javax.swing.JCheckBox smoothCheckBox;
    javax.swing.JLabel smoothLabel;
    javax.swing.JLabel snmpLabel1;
    javax.swing.JLabel softphoneInfoLabel;
    javax.swing.JPanel statisticsPanel;
    javax.swing.JTextPane statusBar;
    javax.swing.JButton stopCampaignButton;
    javax.swing.JTextField suffixField;
    javax.swing.JLabel suffixLabel;
    javax.swing.JLabel systemStatsLabel;
    javax.swing.JScrollPane systemStatsScrollPane;
    javax.swing.JTable systemStatsTable;
    javax.swing.JTabbedPane tabPane;
    javax.swing.JLabel talkingLabel;
    javax.swing.JPanel talkingPanel;
    javax.swing.JTextArea textLogArea;
    javax.swing.JPasswordField toegangField;
    javax.swing.JPanel toolsInnerPanel;
    javax.swing.JPanel toolsPanel;
    javax.swing.JLabel turnoverStatsLabel;
    javax.swing.JScrollPane turnoverStatsScrollPane;
    javax.swing.JTable turnoverStatsTable;
    javax.swing.JTextField usernameField;
    javax.swing.JLabel usersecretLabel;
    javax.swing.JTextField vergunningCodeField;
    public datechooser.beans.DateChooserPanel vergunningDateChooserPanel;
    javax.swing.JTable vergunningDetailsTable;
    javax.swing.JList vergunningPeriodList;
    javax.swing.JList vergunningTypeList;
    javax.swing.JLabel vmUsagePauseValue;
    javax.swing.JLabel vmUsageThresholdLabel;
    javax.swing.JSlider vmUsageThresholdSlider;
    // End of variables declaration//GEN-END:variables

    private void chooseDigit(String digit) { String content = destinationTextArea.getText(); content += digit; destinationTextArea.setText(content); }

    private void enableTelephoneUsage()
    {
	tabPane.setEnabled(true);
	saveConfigurationButton.setEnabled(true);
	registerToggleButton.setEnabled(true);
	registerToggleButton.setSelected(false);
	registerToggleButton.setForeground(Color.BLACK);
	humanResponseSimulatorToggleButton.setEnabled(true);
	humanResponseSimulatorToggleButton.setSelected(false);
	humanResponseSimulatorToggleButton.setForeground(Color.BLACK);
	muteAudioToggleButton.setEnabled(true);
	muteAudioToggleButton.setSelected(false);
	muteAudioToggleButton.setForeground(Color.BLACK);
	domainLabel.setEnabled(true);
	domainField.setEnabled(true);
	serverPortLabel.setEnabled(true);
	serverPortField.setEnabled(true);
	prefPhoneLinesSlider.setEnabled(true);
	serverIPLabel.setEnabled(true);
	serverIPField.setEnabled(true);
	usernameField.setEnabled(true);
	registerLabel.setEnabled(true);
	registerCheckBox.setEnabled(true);
	iconsCheckBox.setEnabled(true);
	destinationTextArea.setEnabled(true);
    }

    private void disableTelephoneUsage()
    {
        tabPane.setEnabled(false);
        phoneDisplayTabPanel.setEnabled(false);
        logPanel.setEnabled(true);
        saveConfigurationButton.setEnabled(false);
        registerToggleButton.setEnabled(false);
        humanResponseSimulatorToggleButton.setEnabled(false);
        muteAudioToggleButton.setEnabled(false);
        muteAudioToggleButton.setSelected(false);
        domainLabel.setEnabled(false);
        domainField.setEnabled(false);
        serverPortLabel.setEnabled(false);
        serverPortField.setEnabled(false);
	prefPhoneLinesSlider.setEnabled(false);
        serverIPLabel.setEnabled(false);
        serverIPField.setEnabled(false);
        usernameField.setEnabled(false);
        registerLabel.setEnabled(false);
        registerCheckBox.setEnabled(false);
        iconsCheckBox.setEnabled(false);
        destinationTextArea.setEnabled(false);

    }

    private void enableConfigurationUsage()
    {
	tabPane.setEnabled(true);
	clientIPLabel.setEnabled(true);
	clientPortLabel.setEnabled(true);
	clientIPField.setEnabled(true);
	pubIPField.setEnabled(true);
	clientPortField.setEnabled(true);
	registerLabel.setEnabled(true);
	registerCheckBox.setEnabled(true);
        iconsCheckBox.setEnabled(true);
	saveConfigurationButton.setEnabled(true);
    }

    private void disableConfigurationUsage()
    {
        clientIPLabel.setEnabled(false);
        clientIPField.setEnabled(false);
        pubIPField.setEnabled(false);
        clientPortLabel.setEnabled(false);
        clientPortField.setEnabled(false);
        registerLabel.setEnabled(false);
        registerCheckBox.setEnabled(false);
        iconsCheckBox.setEnabled(false);
    }

    private void initSlidersSmooth()
    {
        Thread inboundBurstRateSliderThread = new Thread( allThreadsGroup, new Runnable()
        {
            @Override
            public void run()
            {
                int counter = 0; int to = registerSpeedSlider.getValue();
                while (counter < to)
                {
                    registerSpeedSlider.setValue(counter);
                    counter += 5;
                    try { Thread.sleep(smoothMovementPeriod * 1); } catch (InterruptedException ex) {  }
                }
                registerSpeedSlider.setValue(to);
                return;
            }
        });
        inboundBurstRateSliderThread.setName("inboundBurstRateSliderThread");
        inboundBurstRateSliderThread.setDaemon(runThreadsAsDaemons);
        inboundBurstRateSliderThread.start();

        Thread inboundRingingResponseDelaySliderThread = new Thread( allThreadsGroup, new Runnable()
        {
            @Override
            public void run()
            {
                int counter = 0; int to = inboundRingingResponseDelaySlider.getValue();
                while (counter < to)
                {
                    inboundRingingResponseDelaySlider.setValue(counter);
                    counter += 5000;
                    try { Thread.sleep(smoothMovementPeriod * 2); } catch (InterruptedException ex) {  }
                }
                inboundRingingResponseDelaySlider.setValue(to);
                return;
            }
        });
        inboundRingingResponseDelaySliderThread.setName("inboundRingingResponseDelaySliderThread");
        inboundRingingResponseDelaySliderThread.setDaemon(runThreadsAsDaemons);
        inboundRingingResponseDelaySliderThread.start();

        Thread inboundRingingResponseBusyRatioSliderThread = new Thread( allThreadsGroup, new Runnable()
        {
            @Override
            public void run()
            {
                int counter = 0; int to = inboundRingingResponseBusyRatioSlider.getValue();
                while (counter < to)
                {
                    inboundRingingResponseBusyRatioSlider.setValue(counter);
                    counter++;
                    try { Thread.sleep(smoothMovementPeriod * 4); } catch (InterruptedException ex) {  }
                }
                inboundRingingResponseBusyRatioSlider.setValue(to);
                return;
            }
        });
        inboundRingingResponseBusyRatioSliderThread.setName("inboundRingingResponseBusyRatioSliderThread");
        inboundRingingResponseBusyRatioSliderThread.setDaemon(runThreadsAsDaemons);
        inboundRingingResponseBusyRatioSliderThread.start();

        Thread inboundEndDelaySliderThread = new Thread( allThreadsGroup, new Runnable()
        {
            @Override
            public void run()
            {
                int counter = 0; int to = inboundEndDelaySlider.getValue();
                while (counter < to)
                {
                    inboundEndDelaySlider.setValue(counter);
                    counter += 1000;
                    try { Thread.sleep(smoothMovementPeriod); } catch (InterruptedException ex) {  }
                }
                inboundEndDelaySlider.setValue(to);
                return;
            }
        });
        inboundEndDelaySliderThread.setName("inboundEndDelaySliderThread");
        inboundEndDelaySliderThread.setDaemon(runThreadsAsDaemons);
        inboundEndDelaySliderThread.start();

        Thread vmUsageThresholdSliderThread = new Thread( allThreadsGroup, new Runnable()
        {
            @Override
            public void run()
            {
                int counter = 0; int to = vmUsageThresholdSlider.getValue();
                while (counter < to)
                {
                    vmUsageThresholdSlider.setValue(counter);
                    counter += 5;
                    try { Thread.sleep(smoothMovementPeriod * 1); } catch (InterruptedException ex) {  }
                }
                vmUsageThresholdSlider.setValue(to);
                return;
            }
        });
        vmUsageThresholdSliderThread.setName("vmUsageThresholdSliderThread");
        vmUsageThresholdSliderThread.setDaemon(runThreadsAsDaemons);
        vmUsageThresholdSliderThread.start();

        Thread memFreeThresholdSliderThread = new Thread( allThreadsGroup, new Runnable()
        {
            @Override
            public void run()
            {
                int counter = 0; int to = memFreeThresholdSlider.getValue();
                while (counter < to)
                {
                    memFreeThresholdSlider.setValue(counter);
                    counter += 1;
                    try { Thread.sleep(smoothMovementPeriod * 1); } catch (InterruptedException ex) {  }
                }
                memFreeThresholdSlider.setValue(to);
                return;
            }
        });
        memFreeThresholdSliderThread.setName("memFreeThresholdSliderThread");
        memFreeThresholdSliderThread.setDaemon(runThreadsAsDaemons);
        memFreeThresholdSliderThread.start();

        Thread heapMemFreeThresholdSliderThread = new Thread( allThreadsGroup, new Runnable()
        {
            @Override
            public void run()
            {
                int counter = 0; int to = heapMemFreeThresholdSlider.getValue();
                while (counter < to)
                {
                    heapMemFreeThresholdSlider.setValue(counter);
                    counter++;
                    try { Thread.sleep(smoothMovementPeriod * 2); } catch (InterruptedException ex) {  }
                }
                heapMemFreeThresholdSlider.setValue(to);
                return;
            }
        });
        heapMemFreeThresholdSliderThread.setName("heapMemFreeThresholdSliderThread");
        heapMemFreeThresholdSliderThread.setDaemon(runThreadsAsDaemons);
        heapMemFreeThresholdSliderThread.start();

        Thread connectingTallyLimitSliderThread = new Thread( allThreadsGroup, new Runnable()
        {
            @Override
            public void run()
            {
                int counter = 0; int to = connectingTallyLimitSlider.getValue();
                while (counter < to)
                {
                    connectingTallyLimitSlider.setValue(counter);
                    counter++;
                    try { Thread.sleep(smoothMovementPeriod * 2); } catch (InterruptedException ex) {  }
                }
                connectingTallyLimitSlider.setValue(to);
                return;
            }
        });

        connectingTallyLimitSliderThread.setName("connectingTallyLimitSliderThread");
        connectingTallyLimitSliderThread.setDaemon(runThreadsAsDaemons);
        connectingTallyLimitSliderThread.start();

        Thread callingTallyLimitSliderThread = new Thread( allThreadsGroup, new Runnable()
        {
            @Override
            public void run()
            {
                int counter = 0; int to = callingTallyLimitSlider.getValue();
                while (counter < to)
                {
                    callingTallyLimitSlider.setValue(counter);
                    counter += 10;
                    try { Thread.sleep(smoothMovementPeriod); } catch (InterruptedException ex) {  }
                }
                callingTallyLimitSlider.setValue(to);
                return;
            }
        });
        callingTallyLimitSliderThread.setName("callingTallyLimitSliderThread");
        callingTallyLimitSliderThread.setDaemon(runThreadsAsDaemons);
        callingTallyLimitSliderThread.start();

        Thread establishedTallyLimitSliderThread = new Thread( allThreadsGroup, new Runnable()
        {
            @Override
            public void run()
            {
                int counter = 0; int to = establishedTallyLimitSlider.getValue();
                while (counter <= to)
                {
                    establishedTallyLimitSlider.setValue(counter);
                    counter += 2;
                    try { Thread.sleep(smoothMovementPeriod * 2); } catch (InterruptedException ex) {  }
                }
                establishedTallyLimitSlider.setValue(to);
                return;
            }
        });
        establishedTallyLimitSliderThread.setName("establishedTallyLimitSliderThread");
        establishedTallyLimitSliderThread.setDaemon(runThreadsAsDaemons);
        establishedTallyLimitSliderThread.start();

        Thread callSpeedSliderThread = new Thread( allThreadsGroup, new Runnable()
        {
            @Override
            public void run()
            {
                int counter = 0;
                int to      = callSpeedSlider.getValue();
                
                while (counter <= to)
                {
                    callSpeedSlider.setValue(counter);
                    counter += 500;
                    try { Thread.sleep(smoothMovementPeriod * 1); } catch (InterruptedException ex) {  }
                }
                callSpeedSlider.setValue(to);
                return;
            }
        });
        callSpeedSliderThread.setName("callSpeedSliderThread");
        callSpeedSliderThread.setDaemon(runThreadsAsDaemons);
        callSpeedSliderThread.start();
    }

    /**
     *
     * @return
     */
    public static String getBrand()		    { return Vergunning.BRAND; }

    /**
     *
     * @return
     */
    public static String getBusiness()		    { return Vergunning.BUSINESS; }

    /**
     *
     * @return
     */
    public static String getBrandDescription()	    { return Vergunning.BRAND_DESCRIPTION; }

    /**
     *
     * @return
     */
    public static String getProduct()		    { return Vergunning.PRODUCT; }

    /**
     *
     * @return
     */
    public static String getWindowTitle()	    { return Vergunning.BRAND + " " + Vergunning.PRODUCT + " " + VERSION; }

    /**
     *
     * @return
     */
    public static String getProductDescription()    { return Vergunning.PRODUCT_DESCRIPTION; }

    /**
     *
     * @return
     */
    public static String getCopyright()		    { return Vergunning.COPYRIGHT; }

    /**
     *
     * @return
     */
    public static String getAuthor()		    { return Vergunning.AUTHOR; }

    /**
     *
     * @return
     */
    public static String getWarning()		    { return Vergunning.WARNING; }

    /**
     *
     * @return
     */
    public static String getVersion()		    { return VERSION; }

    /**
     *
     * @return
     */
    public String  getBoundMode()                   { return boundMode; }

    /**
     *
     * @return
     */
    public String  getPID()                         { return Integer.toString(pid); }

    /**
     *
     * @return
     */
    public int     getCallCenterStatus()            { return callCenterStatus; }

    /**
     *
     * @return
     */
    public String  getCallCenterStatusDescription() { return callCenterStatusDescription[callCenterStatus]; }

    /**
     *
     * @return
     */
    public int     getCampaignReRunStage()          { return campaignReRunStage; }

    /**
     *
     * @return
     */
    public int     getCampaignProgressPercentage()  { return campaignProgressPercentage; }

    /**
     *
     * @return
     */
    public String  isStalling()                     { if (stalling) { return "TRUE"; } else {return "FALSE"; } }

    /**
     *
     * @return
     */
    public int     getSoftphonesQuantity()          { return softphonesQuantity; }

    /**
     *
     * @return
     */
    public int     getIdleAC()                      { return campaignStat.getIdleAC(); }

    /**
     *
     * @return
     */
    public int     getConnectingAC()                { return campaignStat.getConnectingAC(); }

    /**
     *
     * @return
     */
    public int     getConnectingTT()                { return campaignStat.getConnectingTT(); }

    /**
     *
     * @return
     */
    public int     getCallingAC()                   { return campaignStat.getCallingAC(); }

    /**
     *
     * @return
     */
    public int     getCallingTT()                   { return campaignStat.getCallingTT(); }

    /**
     *
     * @return
     */
    public int     getTalkingAC()                   { return campaignStat.getTalkingAC(); }

    /**
     *
     * @return
     */
    public int     getTalkingTT()                   { return campaignStat.getTalkingTT(); }

//    public void setSoftphonesQuantity(int softphonesQuantityParam)      { softphonesQuantity = softphonesQuantityParam; }

    /**
     *
     */
        public void stopCampaign()                      { campaignStopRequested = true; }

    /**
     *
     * @param imageVisibleParam
     */
    public void    setImagePanelVisible(boolean imageVisibleParam)
    {
        if (imageVisibleParam)
        {
            phonesPoolTableScrollPane.setVisible(false);
            phonesPoolTable.setVisible(false);

            imageBrandLabel.setVisible(true);
            imageProductLabel.setVisible(true);
            imagePostfixLabel.setVisible(true);
            imageLinkLabel.setVisible(true);
            imageIconLabel.setVisible(true);
        }
        else
        {
            phonesPoolTableScrollPane.setVisible(true);
            phonesPoolTable.setVisible(true);

            imageBrandLabel.setVisible(false);
            imageProductLabel.setVisible(false);
            imagePostfixLabel.setVisible(false);
            imageLinkLabel.setVisible(false);
            imageIconLabel.setVisible(false);
        }
    }

    /**
     *
     * @param args
     */
    public static void main(final String args[])
    {
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                ECallCenter21 eCallCenter21 = null;
                if (args.length == 0) // Custom Mode
                {
                    try {
                        eCallCenter21 = new ECallCenter21();
                    } catch (SQLException ex) {
                    } catch (ClassNotFoundException ex) {
                    } catch (InstantiationException ex) {
                    } catch (IllegalAccessException ex) {
                    } catch (NoSuchMethodException ex) {
                    } catch (InvocationTargetException ex) {
                    } catch (Exception ex) {
                    }
                    eCallCenter21.setVisible(true);
                }
                else if (args.length == 1) { System.out.print("\r\n\r\nError: One parameter is invalid\r\n"); usage(); }
                else if (args.length == 2) // Inbound Test Mode
                {
                    boolean managedMode = false;
                    if (args[1].equals("Managed")) { managedMode = true; } else { managedMode = false; }
                    try {
                        eCallCenter21 = new ECallCenter21(args[0], managedMode); // "Inbound", managedMode
                    } catch (SQLException ex) {
                    } catch (ClassNotFoundException ex) {
                    } catch (InstantiationException ex) {
                    } catch (IllegalAccessException ex) {
                    } catch (NoSuchMethodException ex) {
                    } catch (InvocationTargetException ex) {
                    } catch (Exception ex) {
                    }
                    eCallCenter21.setVisible(true);
                }
                else if (args.length == 3)
                {
                    boolean managedMode = false;
                    if (args[1].equals("Managed")) { managedMode = true; } else { managedMode = false; }
                    try {
                        eCallCenter21 = new ECallCenter21(args[0], managedMode, Integer.parseInt(args[2])); // "Outbound", managedMode, CampaignId
                    } catch (SQLException ex) {
                    } catch (ClassNotFoundException ex) {
                    } catch (InstantiationException ex) {
                    } catch (IllegalAccessException ex) {
                    } catch (NoSuchMethodException ex) {
                    } catch (InvocationTargetException ex) {
                    } catch (Exception ex) {
                    }
                    eCallCenter21.setVisible(true);
                }
                else { System.out.print("\r\n\r\nError: More than 2 parameters !\r\n"); usage(); }
            }
        });
    }

    private void positionWindow(String sideParam)
    {
        // Put window in Top-Center
        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        int winWidth = (int)getWidth();
        int winHeight = (int)getHeight();
        int posX = 0;
        int posY = 0;
        if (sideParam.equals("Left")) { posX = Math.round( (screenDim.width / 2) - winWidth); } else { posX = Math.round( (screenDim.width / 2)); }
        posY = Math.round((screenDim.height - winHeight));
        setLocation(posX, posY);
    }

    private static void usage()
    {
        System.out.println(
                            "\r\nUsage:\r\n\r\n" +
                            "" +
                            "ECallCenter (Starts up CallCenter) in Custom Mode\r\n" +
                            "ECallCenter [<Inbound>]\r\n" +
                            "ECallCenter [<Outbound>] [<CampaignId>]\r\n\r\n" +
                            "   Mode: [<Inbound>] Put CallCenter to Inbound Test Mode\r\n" +
                            "   Mode: [<Outbound>] <[CampaignId]> Run Outbound Campaign\r\n\r\n"
                          );
    }

    /**
     *
     */
    public void closeCallCenter() {
        System.exit(0);
    }

    /**
     *
     * @param displaymessage
     */
    @Override
    synchronized public void logToApplication(String displaymessage)
    {
        Calendar logCalendar = Calendar.getInstance();

        String humanDate = "" +
        String.format("%04d", logCalendar.get(Calendar.YEAR)) + "-" +
        String.format("%02d", logCalendar.get(Calendar.MONTH) + 1) + "-" +
        String.format("%02d", logCalendar.get(Calendar.DAY_OF_MONTH)) + " " +
        String.format("%02d", logCalendar.get(Calendar.HOUR_OF_DAY)) + ":" +
        String.format("%02d", logCalendar.get(Calendar.MINUTE)) + ":" +
        String.format("%02d", logCalendar.get(Calendar.SECOND));
        textLogArea.append(humanDate + " " + displaymessage + lineTerminator);

        logToFile(displaymessage);
    }

    /**
     *
     * @param displaymessage
     */
    @Override
    synchronized public void logToFile(final String displaymessage)
    {
        Thread logToFileThread = new Thread(new Runnable()
        {
            @Override
            @SuppressWarnings({"static-access"})
            public void run()
            {
                Calendar logCalendar = Calendar.getInstance();

                String humanDate = "" +
                String.format("%04d", logCalendar.get(Calendar.YEAR)) + "-" +
                String.format("%02d", logCalendar.get(Calendar.MONTH) + 1) + "-" +
                String.format("%02d", logCalendar.get(Calendar.DAY_OF_MONTH)) + " " +
                String.format("%02d", logCalendar.get(Calendar.HOUR_OF_DAY)) + ":" +
                String.format("%02d", logCalendar.get(Calendar.MINUTE)) + ":" +
                String.format("%02d", logCalendar.get(Calendar.SECOND));

                try { logFileWriter = new FileWriter(logFileString, true ); }
                catch (IOException ex) { showStatus("Error: IOException: new FileWriter(" + logFileString + ")" + ex.getMessage(), false, false); logBuffer += humanDate + " " + displaymessage + lineTerminator; return; }

                try { logFileWriter.flush(); }
                catch (IOException ex) { showStatus("Error: IOException: logFileWriter.flush()1;", false, false); logBuffer += humanDate + " " + displaymessage + lineTerminator; return; }

                try { logFileWriter.write(logBuffer + humanDate + " " + displaymessage + lineTerminator); }
                catch (IOException ex) { showStatus("Error: IOException: logFileWriter.write()", false, false); logBuffer += humanDate + " " + displaymessage + lineTerminator; return; }

                try { logFileWriter.flush(); }
                catch (IOException ex) { showStatus("Error: IOException: logFileWriter.flush()2;", false, false); logBuffer += humanDate + " " + displaymessage + lineTerminator; return; }

                logBuffer = "";

                try { logFileWriter.close(); }
                catch (IOException ex) { showStatus("Error: IOException: logFileWriter.close();", false, false); return; }
            }
        });
        logToFileThread.setName("logToFileThread");
        logToFileThread.setDaemon(runThreadsAsDaemons);
        logToFileThread.start();
    }

    /**
     *
     * @param messageParam
     * @param valueParam
     */
    @Override
    public void feedback(String messageParam, int valueParam) {    }
}

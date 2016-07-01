import datechooser.model.exeptions.IncompatibleDataExeption;
import java.io.IOException;
import java.net.MalformedURLException;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.BadLocationException;
import datasets.Configuration;
import datasets.SpeakerData;
import datasets.DisplayData;
import datasets.Order;
import datasets.Customer;
import datasets.Reseller;
import datasets.Campaign;
import datasets.Invoice;
import datasets.Destination;
import datasets.CampaignStat;
import datasets.Pricelist;
import datasets.TimeTool;
import java.awt.Color;

import java.util.*;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Calendar;
import datechooser.model.multiple.Period;
import datechooser.model.multiple.PeriodSet;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.FileWriter;
import java.io.File;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.UIManager;
import org.jfree.chart.ChartPanel;

/**
 *
 * @author ron
 */
public class Manager extends javax.swing.JFrame implements UserInterface {

    // MinWindow 735x275, MaxWindow 735x650

    private int                 throughputFactor                                = 35; // The higher the shorter the campaign is expected to run

    private static final String THISPRODUCT                                     = "Manager";
    private static final String VERSION                                         = "v1.0.11";

    private static final String DATABASE                                        = Vergunning.BRAND + "DB";
    private static final int    INBOUND_PORT                                    = 1969;
    private static final int    OUTBOUND_PORT                                   = 1970;

    private static final int    PLAF_GTK                                        = 0;
    private static final int    PLAF_MOTIF                                      = 1;
    private static final int    PLAF_NIMBUS                                     = 2;
    private static final int    PLAF_WINDOWS                                    = 3;

//    private TimeWindow timeWindow;
    private TimeTool            timeTool;

    private NetManagerClient    outboundNetManagerClient;
    private NetManagerClient    inboundNetManagerClient;
    private NetManagerClient    netManagerClient3;
    private JavaDBClient        dbClient;
    private Shell               shell;
    private boolean             runThreadsAsDaemons                             = true;

    /**
     *
     */
    public Calendar             currentTimeCalendar;
    private String              soundFileToStream;
    private Configuration       configurationCallCenter;
    private String              filename;
    private Destination         destination;
    private Destination[]       destinationArray;
    private CampaignStat        campaignStat;
    private CampaignStat        lastStallingDetectorCampaignStat;
    private CampaignStat        lastTimeDashboardCampaignStat;
    private boolean             lastTimeDashboardCampaignStatSyncedFirstTime    = false;
    private CampaignStat        tmpCampaignStat;
    private Pricelist           pricelist;
    private Order               order;
    private Order               myOrder;
    private Order               tmpOrder;
    private Customer            customer;
    private Reseller            reseller;

    /**
     *
     */
    public Campaign             campaign;

    /**
     *
     */
    public Campaign             tmpCampaign;

    /**
     *
     */
    public Campaign[]           campaignArray;
    private Invoice             invoice;
    private CampaignCalendar    campaignCalendar;
    private int                 softphonesQuantity                              = 500; // Can be overrided by commandline parameter
    private static final String VERGUNNINGTOEKENNERTOEGANG                      = "IsNwtNp4L";

    private final   int         DISCONNECTED                                    = 0;
    private final   int         CONNECTED                                       = 1;

    private final   int         NOTRUNNING                                      = 0;
    private final   int         STARTING                                        = 1;
    private final   int         RUNNING                                         = 2;
    private final   int         FAILING                                         = 3;

    private boolean             inboundCallCenterShouldBeRunning                = false;
    private int                 inboundCallCenterState                          = DISCONNECTED;
    private int                 inboundCallCenterStatus                         = NOTRUNNING;

    private int                 inboundCallCenterStartingTimer                  = 0;
    private int                 inboundCallCenterStartingTimerLimit             = 120;
    private int                 inboundCallCenterUnrespondingTimer              = 0;
    private int                 inboundCallCenterUnrespondingTimerLimit         = 30;
    private String              inboundCallCenterStatusDescription              = "";

    private boolean             outboundCallCenterShouldBeRunning               = false;
    private int                 outboundCallCenterState                         = DISCONNECTED;
    private int                 outboundCallCenterStatus                        = NOTRUNNING;

    private int                 outboundCallCenterStartingTimer                 = 0;
    private int                 outboundCallCenterStartingTimerLimit            = 60;
    private int                 outboundCallCenterUnrespondingTimer             = 0;
    private int                 outboundCallCenterUnrespondingTimerLimit        = 30;
    private String              outboundCallCenterStatusDescription             = "";

    private int                 outboundCallCenterIsStallingGraceTime           = 60;
    private int                 outboundCallCenterIsStallingCounter             = outboundCallCenterIsStallingGraceTime;

    /**
     *
     */
    public int                  stallingCheckCounter                            = 0;

    /**
     *
     */
    public int                  stallingCheckCounterLimit                       = 10;
    private Locale              nlLocale;
    private boolean             stalling                                        = false;
    private Period              period;
    private PeriodSet           periodSet;
    private float               testModePreparationFactor                       = (float)0.4;
    private float               prodModePreparationFactor                       = (float)2;

    /**
     *
     */
    public float                preparationFactor                               = prodModePreparationFactor;
    private Calendar            calendar;
    private Color               timeFieldColorQueueInactive;
    private Color               timeFieldColorQueueWaiting;
    private Color               timeFieldColorQueueRunning;
    private final FileBrowser   fileBrowser;
    private boolean             platformIsNetManaged                            = true; // The CallCenter's NetManager should be working reliably (highly unreliable in Windows)
    private String              remoteJarURL                                    = "http://www.voipstorm.nl/VoipStorm.jar";
    private String              localJarURL                                     = "VoipStorm.jar";
    private Manager             managerReference;
    private VersionChecker      versionChecker                                  = null;
    private boolean             stopRequested                                   = false;
    private final String []     plaf;
    private String              plafSelected;
    private String              sysProp;
    private long                heapMemTot; // the amount of heapspace in use by JVM
    private long                heapMemMax; // the amount of heapspace the jvm tries to allocate
    private long                heapMemFree; // the amount of free mem this application has left
    private long                threads; // the amount of free mem this application has left
    private int                 smoothMovementPeriod                            = 40;
    private Vergunning          vergunning;
    private final DashboardMeter      callsPerHourMeter, busyRatioMeter, callDurationMeter, answerDelayMeter;
    private final ChartPanel          callsPerHourChartPanel, busyRatioChartPanel, callDurationChartPanel, answerDelayChartPanel;
    private int                 dashboardMeterSize                              = 40;
    private boolean             moveCallsPerHourMeterIsLocked                   = false;
    private boolean             moveCPUMeterIsLocked                            = false;
    private boolean             moveBusyRatioMeterIsLocked                      = false;
    private boolean             moveAnswerDelayMeterIsLocked                    = false;
    private boolean             moveCallDurationMeterIsLocked                   = false;
    private Timer               serviceTimelineTimer;
    private long                serviceTimelineTimerInterval                    = 1000; // mS
    private Timer               updateManagerDashboardTimer;
    private long                updateManagerDashboardTimerInterval             = 30000; // mS
    private Calendar            currentTimeDashboardCalendar;
    private Calendar            lastTimeDashboardCalendar;
    private int                 heapmem                                         = 256;
    private int                 lastMessageDuration                             = 0;

    private File                file;
    private String              dataDir;
    private String              soundsDir;
    private String              vergunningDir;
    private String              databasesDir;
    private String              configDir;
    private String              binDir;
    private String              logDir;
    private String              fileSeparator;
    private String              lineTerminator;
    private String              platform;

    private final String              logDateString;
    private int                 logFileSequence                                 = 1;
    private FileWriter          logFileWriter;
    private final String              logFileString;
    private String              logBuffer                                       = "";

    private int                 PHONESPOOLTABLECOLUMNWIDTH                      = 26;
    private int                 PHONESPOOLTABLECOLUMNHEIGHT                     = 16;

    private final Icons               icons;
    private SysMonitor          sysMonitor;
    private long                vmUsage;

    private ExecutorService     threadExecutor;

    private boolean             callPerHourScaleNeedsRescaling                  = false;

    /**
     *
     */
    public  int                 displayMessagesRunning                          = 0;

    private Calendar            vergunningStartCalendar;
    private Calendar            vergunningEndCalendar;
    private WebLog              weblog;

    /**
     *
     */
    public Manager()
    {
        managerReference        = this;

        String[] status = new String[2];

        threadExecutor = Executors.newCachedThreadPool();

        platform = System.getProperty("os.name").toLowerCase();
        if ( platform.contains("windows") ) { fileSeparator = "\\"; lineTerminator = "\r\n"; } else { fileSeparator = "/"; lineTerminator = "\r\n"; }

	try
	{
	    //        dataDir = "data" + fileSeparator;
//        soundsDir = dataDir + "sounds" + fileSeparator;
//        vergunningDir = dataDir + "license" + fileSeparator;
//        databasesDir = dataDir + "databases" + fileSeparator;
//        configDir = dataDir + "config" + fileSeparator;
//        binDir = dataDir + "bin" + fileSeparator;
//        logDir = dataDir + "log" + fileSeparator;
//
////        System.out.println("\r\nChecking Directories...");
//        boolean missingDirsDetected = false;
//        boolean missingCriticalDirsDetected = false;
//        file = new File(dataDir);       if (!file.exists()) { if (new File(dataDir).mkdir())        { missingDirsDetected = true; System.out.println("Warning:  Creating missing directory: " + dataDir); } }
//        file = new File(soundsDir);     if (!file.exists()) { if (new File(soundsDir).mkdir())      { missingDirsDetected = true; System.out.println("Critical: Creating missing directory: " + soundsDir); missingCriticalDirsDetected = true; } }
//        file = new File(vergunningDir);    if (!file.exists()) { if (new File(vergunningDir).mkdir())     { missingDirsDetected = true; System.out.println("Info:     Creating missing directory: " + vergunningDir); } }
//        file = new File(databasesDir);  if (!file.exists()) { if (new File(databasesDir).mkdir())   { missingDirsDetected = true; System.out.println("Info:     Creating missing directory: " + databasesDir); } }
//        file = new File(configDir);     if (!file.exists()) { if (new File(configDir).mkdir())      { missingDirsDetected = true; System.out.println("Info:     Creating missing directory: " + configDir); } }
//        file = new File(binDir);        if (!file.exists()) { if (new File(binDir).mkdir())         { missingDirsDetected = true; System.out.println("Critical: Creating missing directory: " + binDir); missingCriticalDirsDetected = true; } }
//        file = new File(logDir);        if (!file.exists()) { if (new File(logDir).mkdir())         { missingDirsDetected = true; System.out.println("Info:     Creating missing directory: " + logDir); } }
//        if ( missingCriticalDirsDetected )  { System.out.println("Critical directories were missing!!! Please download the entire VoipStorm package at: " + Vergunning.WEBLINK); try { Thread.sleep(4000); } catch (InterruptedException ex) { } }
//        if ( missingDirsDetected )          {System.out.println("VoipStorm directory structure built"); try { Thread.sleep(1000); } catch (InterruptedException ex) { } }
	    
	  MyNIO.copyTree(MyNIO.getJarFS().getPath("data"),MyNIO.getUserDir()); } catch (IOException ex)	{  }
        try { weblog = new WebLog(); } catch (Exception ex) { }

        currentTimeCalendar = Calendar.getInstance();
        logDateString = "" +
        String.format("%04d", currentTimeCalendar.get(Calendar.YEAR)) +
        String.format("%02d", currentTimeCalendar.get(Calendar.MONTH) + 1) +
        String.format("%02d", currentTimeCalendar.get(Calendar.DAY_OF_MONTH)) + "_" +
        String.format("%02d", currentTimeCalendar.get(Calendar.HOUR_OF_DAY)) +
        String.format("%02d", currentTimeCalendar.get(Calendar.MINUTE)) +
        String.format("%02d", currentTimeCalendar.get(Calendar.SECOND));
        logFileString = logDir + logDateString + "_" + THISPRODUCT + ".log";

        plaf = new String[4];
        plafSelected = new String();
        plaf[0] = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
        plaf[1] = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
        plaf[2] = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
        plaf[3] = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";

        setLookAndFeel(PLAF_NIMBUS);

        setMinimumSize(new Dimension(735,275)); setMaximumSize(new Dimension(735,650));
//        setPreferredSize(getMinimumSize()); setResizable(false);
//        setVisible(false); setVisible(true);
        
        initComponents();

//        if ( platform.indexOf("mac os x") != -1 ) { javaOptionsField.setText("-client -d32 -Xss2048"); }
        if ( platform.contains("mac os x") ) { javaOptionsField.setText("-Xss2048k"); }

        // Initiate the Manager Dashboard
        // Call Per Hour Meter
        callsPerHourMeter = new DashboardMeter("Calls / Hour", "X100", 0, (Vergunning.CALLSPERHOUR_ENTERPRISE / 100), (Vergunning.CALLSPERHOUR_ENTERPRISE / 1000), true); /* true = extra red needle*/ /* true = extra red needle*/ /* true = extra red needle*/ callsPerHourChartPanel = new ChartPanel(callsPerHourMeter.chart);
        org.jdesktop.layout.GroupLayout graphInnerPanelLayout1 = new org.jdesktop.layout.GroupLayout(callsPerHourPanel); callsPerHourPanel.setLayout(graphInnerPanelLayout1);
        graphInnerPanelLayout1.setHorizontalGroup(graphInnerPanelLayout1.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(callsPerHourChartPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, dashboardMeterSize, Short.MAX_VALUE));
        graphInnerPanelLayout1.setVerticalGroup(graphInnerPanelLayout1.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(callsPerHourChartPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, dashboardMeterSize, Short.MAX_VALUE));
        callsPerHourChartPanel.setFont(new java.awt.Font("STHeiti", 0, 10)); callsPerHourMeter.removeColorScale(); callsPerHourPanel.setVisible(true); callsPerHourChartPanel.setVisible(true);

        // Busy Ratio Meter
        busyRatioMeter = new DashboardMeter("Busy Ratio %", "Perc", 0, 100, 10, false); busyRatioChartPanel = new ChartPanel(busyRatioMeter.chart);
        org.jdesktop.layout.GroupLayout graphInnerPanelLayout2 = new org.jdesktop.layout.GroupLayout(busyRatioPanel); busyRatioPanel.setLayout(graphInnerPanelLayout2);
        graphInnerPanelLayout2.setHorizontalGroup(graphInnerPanelLayout2.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(busyRatioChartPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, dashboardMeterSize, Short.MAX_VALUE));
        graphInnerPanelLayout2.setVerticalGroup(graphInnerPanelLayout2.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(busyRatioChartPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, dashboardMeterSize, Short.MAX_VALUE));
        busyRatioChartPanel.setFont(new java.awt.Font("STHeiti", 0, 10));
        busyRatioMeter.setColorScale(66,100,33,66,0,33);
        busyRatioPanel.setVisible(true); busyRatioChartPanel.setVisible(true);

        // Answer Delay Meter
        answerDelayMeter = new DashboardMeter("Answer Dalay", "Sec", 0, 60, 5, false); answerDelayChartPanel = new ChartPanel(answerDelayMeter.chart);
        org.jdesktop.layout.GroupLayout graphInnerPanelLayout3 = new org.jdesktop.layout.GroupLayout(answerDelayPanel); answerDelayPanel.setLayout(graphInnerPanelLayout3);
        graphInnerPanelLayout3.setHorizontalGroup(graphInnerPanelLayout3.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(answerDelayChartPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, dashboardMeterSize, Short.MAX_VALUE));
        graphInnerPanelLayout3.setVerticalGroup(graphInnerPanelLayout3.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(answerDelayChartPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, dashboardMeterSize, Short.MAX_VALUE));
        answerDelayChartPanel.setFont(new java.awt.Font("STHeiti", 0, 10)); answerDelayMeter.removeColorScale(); answerDelayPanel.setVisible(true); answerDelayChartPanel.setVisible(true);

        // Call Duration Meter
        callDurationMeter = new DashboardMeter("Call Duration", "Sec", 0, 60, 5, false); callDurationChartPanel = new ChartPanel(callDurationMeter.chart);
        org.jdesktop.layout.GroupLayout graphInnerPanelLayout4 = new org.jdesktop.layout.GroupLayout(callDurationPanel); callDurationPanel.setLayout(graphInnerPanelLayout4);
        graphInnerPanelLayout4.setHorizontalGroup(graphInnerPanelLayout4.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(callDurationChartPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, dashboardMeterSize, Short.MAX_VALUE));
        graphInnerPanelLayout4.setVerticalGroup(graphInnerPanelLayout4.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(callDurationChartPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, dashboardMeterSize, Short.MAX_VALUE));
        callDurationChartPanel.setFont(new java.awt.Font("STHeiti", 0, 10)); callDurationPanel.setVisible(true); callDurationChartPanel.setVisible(true);

        fileBrowser = new FileBrowser(managerReference);
        icons = new Icons(PHONESPOOLTABLECOLUMNWIDTH, PHONESPOOLTABLECOLUMNHEIGHT, iconsCheckBox.isSelected());

        setBackground(new java.awt.Color(216, 216, 222));

        // Put window in Top-Center
        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        int winWidth = (int)getWidth();
        int winHeight = (int)getHeight();
        int posX = Math.round((screenDim.width / 2) - (winWidth / 2));
        int posY = Math.round((screenDim.height / 2) - (winHeight / 2));
        setLocation(posX, 0);

        Thread defaultConstructorThread = new Thread(new Runnable()
        {
            @Override
            @SuppressWarnings({"static-access"})
            public void run() throws java.lang.IllegalStateException // Thrown by the dashboard meters (not properly instantiated / swing compliant)
            {
                String[] status = new String[2];

//                resizeWindow();
//                mainTabbedPane.setSelectedIndex(5);

                campaignProgressBar.setEnabled(false);campaignProgressBar.setVisible(false);
                String imgName = "/images/voipstormboxicon.jpg"; URL imgURL = getClass().getResource(imgName); Image image = Toolkit.getDefaultToolkit().getImage(imgURL); setIconImage(image);

                managerVersionLabel.setText(VERSION);
                callcenterVersionLabel.setText(ECallCenter21.getVersion());
                ephoneVersionLabel.setText(EPhone.getVersion());

                sysMonitor = new SysMonitor();

                sysProp = new String();
                sysProp = "sun.arch.data.model";    sysPropsTable.setValueAt(sysProp, 0, 0);
                String model = System.getProperty(sysProp); sysPropsTable.setValueAt(model, 0, 1);
                sysProp = "sun.cpu.endian";         sysPropsTable.setValueAt(sysProp, 1, 0);
                String endian = System.getProperty(sysProp); sysPropsTable.setValueAt(endian, 1, 1);
                sysProp = "os.arch";                sysPropsTable.setValueAt(sysProp, 2, 0);
                String osarch = System.getProperty(sysProp); sysPropsTable.setValueAt(osarch, 2, 1);
                sysProp = "os.name";                sysPropsTable.setValueAt(sysProp, 3, 0);
                String osname = System.getProperty(sysProp); sysPropsTable.setValueAt(osname, 3, 1);
                sysProp = "os.version";             sysPropsTable.setValueAt(sysProp, 4, 0);
                String osversion = System.getProperty(sysProp); sysPropsTable.setValueAt(osversion, 4, 1);
                sysProp = "user.country";           sysPropsTable.setValueAt(sysProp, 5, 0);
                String country = System.getProperty(sysProp); sysPropsTable.setValueAt(country, 5, 1);
                sysProp = "user.language";          sysPropsTable.setValueAt(sysProp, 6, 0);
                String language = System.getProperty(sysProp); sysPropsTable.setValueAt(language, 6, 1);
                sysProp = "java.vendor";            sysPropsTable.setValueAt(sysProp, 7, 0);
                String javavendor = System.getProperty(sysProp); sysPropsTable.setValueAt(javavendor, 7, 1);
                sysProp = "java.version";           sysPropsTable.setValueAt(sysProp, 8, 0);
                String javaversion = System.getProperty(sysProp); sysPropsTable.setValueAt(javaversion, 8, 1);
                sysProp = "java.class.version";     sysPropsTable.setValueAt(sysProp, 9, 0);
                String classversion = System.getProperty(sysProp); sysPropsTable.setValueAt(classversion, 9, 1);
                heapMemMax = (Runtime.getRuntime().maxMemory()/(1024*1024)); sysProp = "heap max";
                sysPropsTable.setValueAt(sysProp, 10, 0);  sysPropsTable.setValueAt(Long.toString(heapMemMax) + " MB", 10, 1);
                heapMemTot = (Runtime.getRuntime().totalMemory()/(1024*1024));sysProp = "heap tot";
                sysPropsTable.setValueAt(sysProp, 11, 0);  sysPropsTable.setValueAt(Long.toString(heapMemTot) + " MB", 11, 1);
                heapMemFree = (Runtime.getRuntime().freeMemory()/(1024*1024));sysProp = "heap free";
                sysPropsTable.setValueAt(sysProp, 12, 0);  sysPropsTable.setValueAt(Long.toString(heapMemFree) + " MB", 12, 1);
                threads = Thread.activeCount();                               sysProp = "Threads";
                sysPropsTable.setValueAt(sysProp, 13, 0);  sysPropsTable.setValueAt(Long.toString(threads), 13, 1);
                String envstring = "model: " + model + " endian: " + endian + " osarch: " + osarch + " osname: " + osname + " osversion: " + osversion + " country: " + country + " lang: " + language + " javavendor: " + javavendor + " javaversion: " + javaversion + " classversion: " + classversion;

// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!! WARNING CONNECTION DELAY !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                
//                try { weblog.send(THISPRODUCT + " Starting: " + envstring); } catch (Exception ex) { }

                setTitle(getWindowTitle());
                
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

                nlLocale = new Locale("nl");
                shell = new Shell();

        //        timeWindow = new TimeWindow();
                timeTool = new TimeTool();

                displayMessage("*** Welcome to VoipStorm ***");

                timeFieldColorQueueInactive = new Color(167,150,233);
                timeFieldColorQueueWaiting = new Color(107,88,6);
                timeFieldColorQueueRunning = new Color(255,204,0);

                currentTimeDashboardCalendar    = Calendar.getInstance();
                lastTimeDashboardCalendar       = Calendar.getInstance();

                serviceTimelineTimer = new Timer(); serviceTimelineTimer.scheduleAtFixedRate(new ManagerTimer(managerReference), (long)(0), serviceTimelineTimerInterval);
                logToApplication("serviceTimelineTimer          Scheduled immediate at " + Math.round(serviceTimelineTimerInterval / 1000) + " Sec Interval");

                updateManagerDashboardTimer = new Timer(); updateManagerDashboardTimer.scheduleAtFixedRate(new UpdateManagerDashboardTimer(managerReference),     (long)(0), updateManagerDashboardTimerInterval);
                logToApplication("updateManagerDashboardTimer   Scheduled immediate at " + Math.round(updateManagerDashboardTimerInterval / 1000) + " Sec Interval");

                try { dbClient = new JavaDBClient(managerReference, DATABASE); }
                catch (SQLException ex) { }
                catch (ClassNotFoundException ex)       { }
                catch (InstantiationException ex)       { }
                catch (IllegalAccessException ex)       { }
                catch (NoSuchMethodException ex)        { }
                catch (InvocationTargetException ex)    { }
                catch (Exception ex)                    { }

                updateDatabaseStats();

                exampleCustomerButton.setEnabled(true);
                customerIdField.setEnabled(true);
                customerCompanyNameField.setEnabled(true);
                customerAddressField.setEnabled(true);
                customerAddressNrField.setEnabled(true);
                customerCityField.setEnabled(true);
                customerPostcodeField.setEnabled(true);
                customerCountryField.setEnabled(true);
                customerContactNameField.setEnabled(true);
                customerEmailField.setEnabled(true);
                customerPhoneNrField.setEnabled(true);
                customerMobileNrField.setEnabled(true);
                customerDiscountField.setEnabled(true);
                customerDateField.setEnabled(true);

                brandLabel.setText(Vergunning.BRAND);
                brandDescriptionLabel.setText(Vergunning.BRAND_DESCRIPTION);
                productLabel.setText(Vergunning.PRODUCT);
                productDescriptionLabel.setText(Vergunning.PRODUCT_DESCRIPTION);
                copyrightLabel.setText(getWarning() + " " + getCopyright() + " " + getBrand() + " " + getBusiness() + " - Author: " + getAuthor());

                configurationCallCenter = new Configuration();
                showStatus("Loading Configuration...", true, true);
                status = configurationCallCenter.loadConfiguration("3");
                if ( status[0].equals("1") )
                {
                    logToApplication("Loading Configuration Failed: " + status[1]);
                    showStatus("Conf-Load Failed. Creating new...", true, true);
                    configurationCallCenter.createConfiguration();
                    clientIPField.setText(configurationCallCenter.getClientIP());
                    pubIPField.setText(configurationCallCenter.getPublicIP());
                    clientPortField.setText(configurationCallCenter.getClientPort());
                    domainField.setText(configurationCallCenter.getDomain());
                    serverIPField.setText(configurationCallCenter.getServerIP());
                    prefPhoneLinesSlider.setValue(Integer.parseInt(configurationCallCenter.getPrefPhoneLines()));
                    serverPortField.setText(configurationCallCenter.getServerPort());
                    usernameField.setText(configurationCallCenter.getUsername());
                    toegangField.setText(configurationCallCenter.getToegang());
                    if (configurationCallCenter.getRegister().equals("1"))      {registerCheckBox.setSelected(true);} else {registerCheckBox.setSelected(false);}
                    if (configurationCallCenter.getIcons().equals("1"))         {iconsCheckBox.setSelected(true);} else {iconsCheckBox.setSelected(false);}
            //            myFailureSoundTool.play();
                }
                else
                {
            //            myPowerSuccessSoundTool.play();
                    clientIPField.setText(configurationCallCenter.getClientIP());
                    pubIPField.setText(configurationCallCenter.getPublicIP());
                    clientPortField.setText(configurationCallCenter.getClientPort());
                    domainField.setText(configurationCallCenter.getDomain());
                    serverIPField.setText(configurationCallCenter.getServerIP());
                    serverPortField.setText(configurationCallCenter.getServerPort());
                    usernameField.setText(configurationCallCenter.getUsername());
                    toegangField.setText(configurationCallCenter.getToegang());
                    if (configurationCallCenter.getRegister().equals("1")) {registerCheckBox.setSelected(true);} else {registerCheckBox.setSelected(false);}
                    if (configurationCallCenter.getIcons().equals("1"))         {iconsCheckBox.setSelected(true);} else {iconsCheckBox.setSelected(false);}
                    showStatus("Configuration Loaded Successfully", true, true);
                }

                campaignCalendar = new CampaignCalendar(managerReference);
                campaignCalendar.setVisible(false);

                customer                            = new Customer();
                order                               = new Order();
                campaign                            = new Campaign();
                tmpCampaign                         = new Campaign();
                campaignStat                        = new CampaignStat();
                tmpCampaignStat                     = new CampaignStat();
                lastStallingDetectorCampaignStat    = new CampaignStat();
                lastTimeDashboardCampaignStat       = new CampaignStat();

//                orderDestinationsQuantityField.setText(Integer.toString(orderDestinationsQuantitySlider.getValue()));

//                resizeWindow();

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

                vergunningJumpstart();
            }
        });
        defaultConstructorThread.setName("defaultConstructorThread");
        defaultConstructorThread.setDaemon(true);
        defaultConstructorThread.setPriority(8);
        defaultConstructorThread.start();
    }

    private void executeVergunning()
    {
//        vergunningStartCalendar = licenseDateChooserPanel.getSelectedDate();

//        vergunning = new Vergunning();

        feedback("vergunning_valideren", 0);

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
            feedback("vergunning_goed", 0);

            if ((prefPhoneLinesSlider.getMaximum() == 0) || (prefPhoneLinesSlider.getMaximum() > vergunning.getPhoneLines()))
            {
                prefPhoneLinesSlider.setMaximum(vergunning.getPhoneLines()); prefPhoneLinesSlider.setValue(vergunning.getPhoneLines());
            }
            else
            {
                prefPhoneLinesSlider.setMaximum(vergunning.getPhoneLines()); prefPhoneLinesSlider.setValue(Integer.parseInt(configurationCallCenter.getPrefPhoneLines()));
            }

//            if (! vergunning.vergunningOrderInProgress())
//            {
                activationCodeField.setText(vergunning.getActivationCode());
                vergunningCodeField.setText(vergunning.getVergunningCode());
                vergunningTypeList.setSelectedValue(vergunning.getVergunningType(), false);
                vergunningPeriodList.setSelectedValue(vergunning.getVergunningPeriod(), false);
//            }
            if ( Integer.parseInt(configurationCallCenter.getPrefPhoneLines()) > vergunning.getPhoneLines() ) { softphonesQuantity = vergunning.getPhoneLines(); } else { softphonesQuantity = Integer.parseInt(configurationCallCenter.getPrefPhoneLines()); }
            vergunningCodeField.setEnabled(false);
//            applyLicenseButton.setEnabled(false);
            vergunning.setVergunningOrderInProgress(false);
        }
        else
        {
            prefPhoneLinesSlider.setMaximum(Integer.parseInt(configurationCallCenter.getPrefPhoneLines())); prefPhoneLinesSlider.setValue(Integer.parseInt(configurationCallCenter.getPrefPhoneLines()));
            activationCodeField.setText(vergunning.getActivationCode());
            vergunningCodeField.setForeground(Color.red);
            vergunningCodeField.setForeground(Color.black);
            if ( Integer.parseInt(configurationCallCenter.getPrefPhoneLines()) > vergunning.getPhoneLines() ) { softphonesQuantity = vergunning.getPhoneLines(); } else { softphonesQuantity = Integer.parseInt(configurationCallCenter.getPrefPhoneLines()); }
        }
    }

    private void displayMessage(final String messageParam)
    {
        Thread scrollTimeFieldThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while (displayMessagesRunning > 0){try { Thread.sleep(100); } catch (InterruptedException ex) { }}
                displayMessagesRunning++;
                String prefix  = "                                                          ";
                String postfix = "                                                          ";
                String totalMessage = prefix + messageParam + postfix;
                int messageLength = totalMessage.length();
                
//                for (int counter = 0; counter < (messageLength - 30); counter++)
//                {
//                    timeTextField.setText(totalMessage.substring(counter, counter + 30));
//                    try { Thread.sleep(80); } catch (InterruptedException ex) { }
//                }
                timeTextField.setText(totalMessage);
                for (int counter = 0; counter < (infoScrollPane.getHorizontalScrollBar().getMaximum()-300); counter+=4)
                {
                    infoScrollPane.getHorizontalScrollBar().setValue(counter);
                    try { Thread.sleep(20); } catch (InterruptedException ex) { }
                }
                timeTextField.setText("");
                displayMessagesRunning--;
            }
        });
        scrollTimeFieldThread.setName("scrollTimeFieldThread");
        scrollTimeFieldThread.setDaemon(runThreadsAsDaemons);
        scrollTimeFieldThread.setPriority(Thread.NORM_PRIORITY);
        scrollTimeFieldThread.start();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lookAndFeelGroup = new javax.swing.ButtonGroup();
        colorMaskPanel = new javax.swing.JPanel();
        tabPane = new javax.swing.JTabbedPane();
        dashboardTab = new javax.swing.JPanel();
        detailsPanel = new javax.swing.JPanel();
        orderLabel = new javax.swing.JLabel();
        campaignLabel = new javax.swing.JLabel();
        customerLabel = new javax.swing.JLabel();
        customerTableScrollPane = new javax.swing.JScrollPane();
        customerTable = new javax.swing.JTable();
        orderStatsScrollPane = new javax.swing.JScrollPane();
        orderTable = new javax.swing.JTable();
        campaignTableScrollPane = new javax.swing.JScrollPane();
        campaignTable = new javax.swing.JTable();
        callsPerHourPanel = new javax.swing.JPanel();
        busyRatioPanel = new javax.swing.JPanel();
        answerDelayPanel = new javax.swing.JPanel();
        callDurationPanel = new javax.swing.JPanel();
        campaignTab = new javax.swing.JTabbedPane();
        customerManagerPanel = new javax.swing.JPanel();
        clearCustomerFieldsButton = new javax.swing.JButton();
        exampleCustomerButton = new javax.swing.JButton();
        searchCustomerButton = new javax.swing.JButton();
        selectCustomerButton = new javax.swing.JButton();
        insertCustomerButton = new javax.swing.JButton();
        updateCustomerButton = new javax.swing.JButton();
        deleteCustomerButton = new javax.swing.JButton();
        companyInformationPanel = new javax.swing.JPanel();
        companyIdLabel = new javax.swing.JLabel();
        companyNameLabel = new javax.swing.JLabel();
        companyAddressLabel = new javax.swing.JLabel();
        companyAddressNrLabel = new javax.swing.JLabel();
        companyPostcodeLabel = new javax.swing.JLabel();
        companyCityLabel = new javax.swing.JLabel();
        companyCountryLabel = new javax.swing.JLabel();
        customerIdField = new javax.swing.JTextField();
        customerCompanyNameField = new javax.swing.JTextField();
        customerAddressField = new javax.swing.JTextField();
        customerAddressNrField = new javax.swing.JTextField();
        customerCityField = new javax.swing.JTextField();
        customerPostcodeField = new javax.swing.JTextField();
        customerCountryField = new javax.swing.JTextField();
        customerDateField = new javax.swing.JTextField();
        contactInformationPanel = new javax.swing.JPanel();
        contactNameLabel = new javax.swing.JLabel();
        contactEmailLabel = new javax.swing.JLabel();
        contactPhoneLabel = new javax.swing.JLabel();
        contactMobileLabel = new javax.swing.JLabel();
        customerDiscountLabel = new javax.swing.JLabel();
        customerContactNameField = new javax.swing.JTextField();
        customerEmailField = new javax.swing.JTextField();
        customerPhoneNrField = new javax.swing.JTextField();
        customerMobileNrField = new javax.swing.JTextField();
        customerDiscountField = new javax.swing.JTextField();
        previousCustomerButton = new javax.swing.JButton();
        nextCustomerButton = new javax.swing.JButton();
        campaignManagerPanel = new javax.swing.JPanel();
        selectOrderButton = new javax.swing.JButton();
        updateOrderButton = new javax.swing.JButton();
        deleteOrderButton = new javax.swing.JButton();
        serviceLoopProgressBar = new javax.swing.JProgressBar();
        orderInner2PanelPanel = new javax.swing.JPanel();
        destinationScrollPane = new javax.swing.JScrollPane();
        destinationTextArea = new javax.swing.JTextArea();
        orderDateField = new javax.swing.JTextField();
        orderFilenameField = new javax.swing.JTextField();
        browseFileButton = new javax.swing.JButton();
        orderCustomerIdComboBox = new javax.swing.JComboBox();
        orderIdComboBox = new javax.swing.JComboBox();
        timewindowScrollPane = new javax.swing.JScrollPane();
        timewindowList = new javax.swing.JList();
        recipientsScrollPane = new javax.swing.JScrollPane();
        recipientsList = new javax.swing.JList();
        orderInnerPanel = new javax.swing.JPanel();
        orderDestinationsQuantityLabel = new javax.swing.JLabel();
        orderMessageDurationLabel = new javax.swing.JLabel();
        orderMessageRatesLabel = new javax.swing.JLabel();
        orderMessageRatesLabel1 = new javax.swing.JLabel();
        orderMessageDurationField = new javax.swing.JTextField();
        orderMessageRatePerSecondField = new javax.swing.JTextField();
        orderMessageRateField = new javax.swing.JTextField();
        orderDestinationsQuantityField = new javax.swing.JTextField();
        orderSubTotalLabel = new javax.swing.JLabel();
        orderSubTotalField = new javax.swing.JTextField();
        confirmOrderButton = new javax.swing.JButton();
        scheduleButton = new javax.swing.JButton();
        toolsTab = new javax.swing.JPanel();
        toolsInnerPanel = new javax.swing.JPanel();
        netManagerInboundClientToggleButton = new javax.swing.JToggleButton();
        netManagerOutboundClientToggleButton = new javax.swing.JToggleButton();
        controlsPanel = new javax.swing.JPanel();
        callCenterEnabledCheckBox = new javax.swing.JCheckBox();
        callCenterEnabledLabel = new javax.swing.JLabel();
        managedCheckBox = new javax.swing.JCheckBox();
        managedLabel = new javax.swing.JLabel();
        smoothCheckBox = new javax.swing.JCheckBox();
        smoothLabel = new javax.swing.JLabel();
        testPhoneButton = new javax.swing.JButton();
        lookAndFeelPanel = new javax.swing.JPanel();
        lookAndFeelRButtonGTK = new javax.swing.JRadioButton();
        jRadioButtonWindows = new javax.swing.JRadioButton();
        lookAndFeelRButtonNimbus = new javax.swing.JRadioButton();
        lookAndFeelRButtonMotif = new javax.swing.JRadioButton();
        toolsSeparator = new javax.swing.JSeparator();
        javaOptionsField = new javax.swing.JTextField();
        javaOptionsLabel = new javax.swing.JLabel();
        systemPropertiesPanel = new javax.swing.JPanel();
        sysPropsScrollPane = new javax.swing.JScrollPane();
        sysPropsTable = new javax.swing.JTable();
        configTab = new javax.swing.JPanel();
        authenticationPanel = new javax.swing.JPanel();
        clientIPLabel = new javax.swing.JLabel();
        clientIPField = new javax.swing.JTextField();
        pubIPLabel = new javax.swing.JLabel();
        pubIPField = new javax.swing.JTextField();
        clientPortLabel = new javax.swing.JLabel();
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
        registerLabel = new javax.swing.JLabel();
        registerCheckBox = new javax.swing.JCheckBox();
        prefPhoneLinesPanel = new javax.swing.JPanel();
        prefPhoneLinesSlider = new javax.swing.JSlider();
        iconsLabel = new javax.swing.JLabel();
        iconsCheckBox = new javax.swing.JCheckBox();
        subscribeToVOIPProviderButton = new javax.swing.JButton();
        updateTab = new javax.swing.JPanel();
        maintenancePanel1 = new javax.swing.JPanel();
        headerLabel1 = new javax.swing.JLabel();
        applicationLabel = new javax.swing.JLabel();
        versionLabel = new javax.swing.JLabel();
        managerLabel = new javax.swing.JLabel();
        customerTableLabel10 = new javax.swing.JLabel();
        ephoneLabel = new javax.swing.JLabel();
        managerVersionLabel = new javax.swing.JLabel();
        callcenterVersionLabel = new javax.swing.JLabel();
        ephoneVersionLabel = new javax.swing.JLabel();
        updateVersonSeparator = new javax.swing.JSeparator();
        checkVersionButton = new javax.swing.JButton();
        licenseTab = new javax.swing.JPanel();
        licenseTypePanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
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
        logTab = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        textLogArea = new javax.swing.JTextArea();
        backofficeTab = new javax.swing.JTabbedPane();
        invoicePanel = new javax.swing.JPanel();
        selectInvoiceButton = new javax.swing.JButton();
        insertInvoiceButton = new javax.swing.JButton();
        updateInvoiceButton = new javax.swing.JButton();
        deleteInvoiceButton = new javax.swing.JButton();
        invoiceInnerPanel = new javax.swing.JPanel();
        invoiceIdLabel = new javax.swing.JLabel();
        invoiceDateLabel = new javax.swing.JLabel();
        orderIdLabel = new javax.swing.JLabel();
        invoiceQTYLabel = new javax.swing.JLabel();
        invoiceItemUnitPriceLabel = new javax.swing.JLabel();
        invoiceItemVATPercentageLabel = new javax.swing.JLabel();
        invoiceItemQTYPriceLabel = new javax.swing.JLabel();
        invoiceIdField = new javax.swing.JTextField();
        invoiceDateField = new javax.swing.JTextField();
        invoiceOrderIdField = new javax.swing.JTextField();
        invoiceQuantityField = new javax.swing.JTextField();
        invoiceItemDescField = new javax.swing.JTextField();
        invoiceItemUnitPriceField = new javax.swing.JTextField();
        invoiceItemVATPercentageField = new javax.swing.JTextField();
        invoiceItemQuantityPriceField = new javax.swing.JTextField();
        invoiceTotalsPanel = new javax.swing.JPanel();
        invoiceSubTotalB4DiscountLabel = new javax.swing.JLabel();
        invoiceCustomerDiscountLabel = new javax.swing.JLabel();
        invoiceSubTotalLabel = new javax.swing.JLabel();
        invoiceVATLabel = new javax.swing.JLabel();
        invoiceTotalLabel = new javax.swing.JLabel();
        invoicePaidLabel = new javax.swing.JLabel();
        invoiceSubTotalB4DiscountField = new javax.swing.JTextField();
        invoiceCustomerDiscountField = new javax.swing.JTextField();
        invoiceSubTotalField = new javax.swing.JTextField();
        invoiceVATField = new javax.swing.JTextField();
        invoiceTotalField = new javax.swing.JTextField();
        invoicePaidField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        previousInvoiceButton = new javax.swing.JButton();
        nextInvoiceButton = new javax.swing.JButton();
        campaignPanel = new javax.swing.JPanel();
        selectCampaignButton = new javax.swing.JButton();
        insertCampaignButton = new javax.swing.JButton();
        updateCampaignButton = new javax.swing.JButton();
        deleteCampaignButton = new javax.swing.JButton();
        campaignInnerPanel = new javax.swing.JPanel();
        campaignIdLabel = new javax.swing.JLabel();
        timeScheduledLabel = new javax.swing.JLabel();
        timeExpectedLabel = new javax.swing.JLabel();
        timeRegisteredLabel = new javax.swing.JLabel();
        timeStartLabel = new javax.swing.JLabel();
        timeEndLabel = new javax.swing.JLabel();
        campaignTimestampLabel = new javax.swing.JLabel();
        idField = new javax.swing.JTextField();
        campaignTimestampField = new javax.swing.JTextField();
        campaignInnerSeparator = new javax.swing.JSeparator();
        timeScheduledStartField = new javax.swing.JTextField();
        timeExpectedStartField = new javax.swing.JTextField();
        timeScheduledEndField = new javax.swing.JTextField();
        timeExpectedEndField = new javax.swing.JTextField();
        timeRegisteredStartField = new javax.swing.JTextField();
        timeRegisteredEndField = new javax.swing.JTextField();
        campaignOrderIdField = new javax.swing.JTextField();
        oidLabel = new javax.swing.JLabel();
        campaignTestCheckBox = new javax.swing.JCheckBox();
        testLabel = new javax.swing.JLabel();
        previousCampaignButton = new javax.swing.JButton();
        nextCampaignButton = new javax.swing.JButton();
        destinationsPanel = new javax.swing.JPanel();
        selectDestinationButton = new javax.swing.JButton();
        insertDestinationButton = new javax.swing.JButton();
        updateDestinationButton = new javax.swing.JButton();
        deleteDestinationButton = new javax.swing.JButton();
        destinationTimestampPanel = new javax.swing.JPanel();
        idLabel = new javax.swing.JLabel();
        telLabel = new javax.swing.JLabel();
        destinationCountLabel = new javax.swing.JLabel();
        conLabel = new javax.swing.JLabel();
        destIdField = new javax.swing.JTextField();
        campaignIdField = new javax.swing.JTextField();
        destCountField = new javax.swing.JTextField();
        connectingTimestampField = new javax.swing.JTextField();
        destinationField = new javax.swing.JTextField();
        tryLabel = new javax.swing.JLabel();
        tryingTimestampField = new javax.swing.JTextField();
        talkLabel = new javax.swing.JLabel();
        actLabel = new javax.swing.JLabel();
        ringLabel = new javax.swing.JLabel();
        callLabel = new javax.swing.JLabel();
        ringingTimestampField = new javax.swing.JTextField();
        callingTimestampField = new javax.swing.JTextField();
        talkingTimestampField = new javax.swing.JTextField();
        acceptingTimestampField = new javax.swing.JTextField();
        timeOfCompletionPanel = new javax.swing.JPanel();
        localCancelingTimestampField = new javax.swing.JTextField();
        remoteCancelingTimestampField = new javax.swing.JTextField();
        localBusyTimestampField = new javax.swing.JTextField();
        remoteBusyTimestampField = new javax.swing.JTextField();
        localByeTimestampField = new javax.swing.JTextField();
        remoteByeTimestampField = new javax.swing.JTextField();
        localLabel = new javax.swing.JLabel();
        remoteLabel = new javax.swing.JLabel();
        canceledLabel = new javax.swing.JLabel();
        busyLabel = new javax.swing.JLabel();
        endedLabel = new javax.swing.JLabel();
        callingAttemptsField = new javax.swing.JTextField();
        responseStatusCodeLabelLabel = new javax.swing.JLabel();
        responseStatusCodeField = new javax.swing.JTextField();
        responseStatusDescField = new javax.swing.JTextField();
        previousDestinationButton = new javax.swing.JButton();
        nextDestinationButton = new javax.swing.JButton();
        campaignStatsPanel = new javax.swing.JPanel();
        selectCampaignStatsButton = new javax.swing.JButton();
        insertCampaignStatsButton = new javax.swing.JButton();
        updateCampaignStatsButton = new javax.swing.JButton();
        deleteCampaignStatsButton = new javax.swing.JButton();
        campaignStatisticsInnerPanel = new javax.swing.JPanel();
        callInitPanel = new javax.swing.JPanel();
        campaignId2Label = new javax.swing.JLabel();
        onACLabel = new javax.swing.JLabel();
        idleACLabel = new javax.swing.JLabel();
        campaignStatIdField = new javax.swing.JTextField();
        onACField = new javax.swing.JTextField();
        idleACField = new javax.swing.JTextField();
        callProgressPanel = new javax.swing.JPanel();
        concurrentLabel = new javax.swing.JLabel();
        totalCountLabel = new javax.swing.JLabel();
        progressRingingLabel = new javax.swing.JLabel();
        progressAcceptingLabel = new javax.swing.JLabel();
        progressTalkingLabel = new javax.swing.JLabel();
        ringingACField = new javax.swing.JTextField();
        ringingTTField = new javax.swing.JTextField();
        acceptingACField = new javax.swing.JTextField();
        acceptingTTField = new javax.swing.JTextField();
        talkingACField = new javax.swing.JTextField();
        talkingTTField = new javax.swing.JTextField();
        callCompletionPanel = new javax.swing.JPanel();
        local2Label = new javax.swing.JLabel();
        remote2Label = new javax.swing.JLabel();
        cancelTotalLabel = new javax.swing.JLabel();
        busyTotalLabel = new javax.swing.JLabel();
        byeTotalLabel = new javax.swing.JLabel();
        localCancelTTField = new javax.swing.JTextField();
        remoteCancelTTField = new javax.swing.JTextField();
        localBusyTTField = new javax.swing.JTextField();
        remoteBusyTTField = new javax.swing.JTextField();
        localByeTTField = new javax.swing.JTextField();
        remoteByeTTField = new javax.swing.JTextField();
        callInitPanel1 = new javax.swing.JPanel();
        connectInitLabel = new javax.swing.JLabel();
        tryingInitLabel = new javax.swing.JLabel();
        tryingTTField = new javax.swing.JTextField();
        concurrentInitLabel = new javax.swing.JLabel();
        totalCountInitLabel = new javax.swing.JLabel();
        connectACField = new javax.swing.JTextField();
        tryingACField = new javax.swing.JTextField();
        connectTTField = new javax.swing.JTextField();
        callingTTField = new javax.swing.JTextField();
        callingInitLabel1 = new javax.swing.JLabel();
        callingACField = new javax.swing.JTextField();
        pricelistPanel = new javax.swing.JPanel();
        selectPricelistButton = new javax.swing.JButton();
        insertPricelistButton = new javax.swing.JButton();
        updatePricelistButton = new javax.swing.JButton();
        deletePricelistButton = new javax.swing.JButton();
        pricelistInnerPanel = new javax.swing.JPanel();
        daytimeLabel = new javax.swing.JLabel();
        eveningLabel = new javax.swing.JLabel();
        b2bLabel = new javax.swing.JLabel();
        b2cLabel = new javax.swing.JLabel();
        freeLecectLabel = new javax.swing.JLabel();
        pricelistB2BDaytimePerSecondField = new javax.swing.JTextField();
        pricelistB2BEveningPerSecondField = new javax.swing.JTextField();
        pricelistB2CDaytimePerSecondField = new javax.swing.JTextField();
        pricelistB2CEveningPerSecondField = new javax.swing.JTextField();
        pricelistA2SDaytimePerSecondField = new javax.swing.JTextField();
        pricelistA2SEveningPerSecondField = new javax.swing.JTextField();
        euroSignLabel = new javax.swing.JLabel();
        euroSignLabel1 = new javax.swing.JLabel();
        euroSignLabel2 = new javax.swing.JLabel();
        euroSignLabel3 = new javax.swing.JLabel();
        euroSignLabel4 = new javax.swing.JLabel();
        euroSignLabel5 = new javax.swing.JLabel();
        daytimeLabel1 = new javax.swing.JLabel();
        daytimeLabel2 = new javax.swing.JLabel();
        daytimeLabel3 = new javax.swing.JLabel();
        daytimeLabel4 = new javax.swing.JLabel();
        daytimeLabel5 = new javax.swing.JLabel();
        daytimeLabel6 = new javax.swing.JLabel();
        resellerPanel = new javax.swing.JPanel();
        selectResellerButton = new javax.swing.JButton();
        insertResellerButton = new javax.swing.JButton();
        updateResellerButton = new javax.swing.JButton();
        deleteResellerButton = new javax.swing.JButton();
        officeInformationPanel = new javax.swing.JPanel();
        officeIdLabel = new javax.swing.JLabel();
        officeNameLabel = new javax.swing.JLabel();
        officeAddressLabel = new javax.swing.JLabel();
        officeAddressNrLabel = new javax.swing.JLabel();
        officePostcodeLabel = new javax.swing.JLabel();
        officeCityLabel = new javax.swing.JLabel();
        officeCountryLabel = new javax.swing.JLabel();
        resellerIdField = new javax.swing.JTextField();
        resellerDateField = new javax.swing.JTextField();
        resellerCompanyNameField = new javax.swing.JTextField();
        resellerAddressField = new javax.swing.JTextField();
        resellerAddressNrField = new javax.swing.JTextField();
        resellerPostcodeField = new javax.swing.JTextField();
        resellerCityField = new javax.swing.JTextField();
        resellerCountryField = new javax.swing.JTextField();
        resellerInformationPanel = new javax.swing.JPanel();
        resellerNameLabel = new javax.swing.JLabel();
        resellerEmailLabel = new javax.swing.JLabel();
        resellerPhoneLabel = new javax.swing.JLabel();
        resellerMobileLabel = new javax.swing.JLabel();
        resellerDiscountLabel = new javax.swing.JLabel();
        resellerContactNameField = new javax.swing.JTextField();
        resellerEmailField = new javax.swing.JTextField();
        resellerPhoneNrField = new javax.swing.JTextField();
        resellerMobileNrField = new javax.swing.JTextField();
        resellerDiscountField = new javax.swing.JTextField();
        clearResellerFieldsButton = new javax.swing.JButton();
        exampleResellerButton = new javax.swing.JButton();
        searchResellerButton = new javax.swing.JButton();
        dbPanel = new javax.swing.JPanel();
        maintenancePanel = new javax.swing.JPanel();
        headerLabel = new javax.swing.JLabel();
        customerTableLabel = new javax.swing.JLabel();
        customerTableLabel1 = new javax.swing.JLabel();
        customerTableLabel2 = new javax.swing.JLabel();
        customerTableLabel3 = new javax.swing.JLabel();
        customerTableLabel4 = new javax.swing.JLabel();
        customerTableLabel5 = new javax.swing.JLabel();
        customerTableLabel6 = new javax.swing.JLabel();
        customerTableLabel7 = new javax.swing.JLabel();
        dropCustomerTableButton = new javax.swing.JButton();
        dropOrderTableButton = new javax.swing.JButton();
        dropInvoiceTableButton = new javax.swing.JButton();
        dropCampaignTableButton = new javax.swing.JButton();
        dropDestinationTableButton = new javax.swing.JButton();
        dropCampaignStatsTableButton = new javax.swing.JButton();
        dropPricelistTableButton = new javax.swing.JButton();
        dropResellerTableButton = new javax.swing.JButton();
        customerTableLabel8 = new javax.swing.JLabel();
        dropAllTablesButton = new javax.swing.JButton();
        customerRecords = new javax.swing.JLabel();
        orderRecords = new javax.swing.JLabel();
        invoiceRecords = new javax.swing.JLabel();
        campaignRecords = new javax.swing.JLabel();
        destinationRecords = new javax.swing.JLabel();
        campaignStatsRecords = new javax.swing.JLabel();
        pricelistRecords = new javax.swing.JLabel();
        resellerRecords = new javax.swing.JLabel();
        aboutTab = new javax.swing.JPanel();
        sizeControlPanel = new javax.swing.JPanel();
        brandLabel = new javax.swing.JLabel();
        brandDescriptionLabel = new javax.swing.JTextArea();
        productLabel = new javax.swing.JLabel();
        productDescriptionLabel = new javax.swing.JTextArea();
        copyrightLabel = new javax.swing.JTextArea();
        summaryDisplayPanel = new javax.swing.JPanel();
        statusBarOutbound = new javax.swing.JTextPane();
        captionTable = new javax.swing.JTable();
        statusBarInbound = new javax.swing.JTextPane();
        resizeWindowButton = new javax.swing.JButton();
        controlPanel = new javax.swing.JPanel();
        displayPanel = new javax.swing.JPanel();
        infoScrollPane = new javax.swing.JScrollPane();
        timeTextField = new javax.swing.JTextField();
        campaignProgressBar = new javax.swing.JProgressBar();
        outboundButtonPanel = new javax.swing.JPanel();
        outboundCallCenterButton = new javax.swing.JButton();
        inboundButtonPanel = new javax.swing.JPanel();
        inboundCallCenterButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(216, 216, 222));
        setMinimumSize(new java.awt.Dimension(735, 275));
        setSize(new java.awt.Dimension(735, 650));

        colorMaskPanel.setFont(new java.awt.Font("STHeiti", 0, 13));
        colorMaskPanel.setMaximumSize(new java.awt.Dimension(731, 476));
        colorMaskPanel.setMinimumSize(new java.awt.Dimension(731, 0));

        tabPane.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        tabPane.setToolTipText("");
        tabPane.setEnabled(false);
        tabPane.setFont(new java.awt.Font("STHeiti", 0, 13));
        tabPane.setMaximumSize(new java.awt.Dimension(680, 360));
        tabPane.setMinimumSize(new java.awt.Dimension(680, 0));
        tabPane.setPreferredSize(new java.awt.Dimension(690, 360));
        tabPane.setSize(new java.awt.Dimension(680, 360));
        tabPane.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabPaneMouseClicked(evt);
            }
        });

        dashboardTab.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("STHeiti", 0, 10), new java.awt.Color(255, 255, 255))); // NOI18N
        dashboardTab.setToolTipText("");
        dashboardTab.setMaximumSize(new java.awt.Dimension(703, 320));
        dashboardTab.setMinimumSize(new java.awt.Dimension(703, 320));

        detailsPanel.setForeground(new java.awt.Color(51, 51, 51));
        detailsPanel.setToolTipText("");
        detailsPanel.setFont(new java.awt.Font("STHeiti", 0, 13));
        detailsPanel.setPreferredSize(new java.awt.Dimension(700, 178));

        orderLabel.setFont(new java.awt.Font("STHeiti", 0, 12));
        orderLabel.setForeground(new java.awt.Color(102, 102, 102));
        orderLabel.setText("Order");

        campaignLabel.setFont(new java.awt.Font("STHeiti", 0, 12));
        campaignLabel.setForeground(new java.awt.Color(102, 102, 102));
        campaignLabel.setText("Campaign");

        customerLabel.setFont(new java.awt.Font("STHeiti", 0, 12));
        customerLabel.setForeground(new java.awt.Color(102, 102, 102));
        customerLabel.setText("Customer");

        customerTableScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        customerTableScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        customerTable.setFont(new java.awt.Font("STHeiti", 0, 10));
        customerTable.setForeground(new java.awt.Color(102, 102, 102));
        customerTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Company", "-"},
                {"Address / Nr", "-"},
                {"Postcode / City", "-"},
                {"Country", "-"},
                {"Phone", "-"},
                {"Mobile", "-"},
                {"Email", "-"},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "", ""
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        customerTable.setToolTipText("");
        customerTable.setAutoCreateRowSorter(true);
        customerTable.setAutoscrolls(false);
        customerTable.setDoubleBuffered(true);
        customerTable.setEditingColumn(0);
        customerTable.setEditingRow(0);
        customerTable.setFocusable(false);
        customerTable.setMaximumSize(new java.awt.Dimension(55, 155));
        customerTable.setMinimumSize(new java.awt.Dimension(55, 155));
        customerTable.setName("name"); // NOI18N
        customerTable.setPreferredSize(new java.awt.Dimension(55, 155));
        customerTable.setRequestFocusEnabled(false);
        customerTable.setRowHeight(14);
        customerTable.setRowSelectionAllowed(false);
        customerTable.setSelectionBackground(new java.awt.Color(51, 102, 255));
        customerTable.setShowGrid(false);
        customerTable.setSize(new java.awt.Dimension(55, 110));
        customerTableScrollPane.setViewportView(customerTable);
        customerTable.getColumnModel().getColumn(0).setResizable(false);
        customerTable.getColumnModel().getColumn(0).setPreferredWidth(15);
        customerTable.getColumnModel().getColumn(1).setResizable(false);

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
                {"Mess. Rate/S", "-"},
                {"Mess. Rate", "-"},
                {"SubTotal   €", "-"},
                {" ", null},
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
        orderTable.setToolTipText("");
        orderTable.setAutoCreateRowSorter(true);
        orderTable.setAutoscrolls(false);
        orderTable.setDoubleBuffered(true);
        orderTable.setFocusable(false);
        orderTable.setMaximumSize(new java.awt.Dimension(55, 155));
        orderTable.setMinimumSize(new java.awt.Dimension(55, 155));
        orderTable.setName("name"); // NOI18N
        orderTable.setPreferredSize(new java.awt.Dimension(55, 155));
        orderTable.setRowHeight(14);
        orderTable.setRowSelectionAllowed(false);
        orderTable.setSelectionBackground(new java.awt.Color(51, 102, 255));
        orderTable.setShowGrid(false);
        orderTable.setSize(new java.awt.Dimension(55, 110));
        orderStatsScrollPane.setViewportView(orderTable);
        orderTable.getColumnModel().getColumn(0).setResizable(false);
        orderTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        orderTable.getColumnModel().getColumn(1).setResizable(false);

        campaignTableScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        campaignTableScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

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
                {"Test", "-"},
                {" ", null},
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
        campaignTable.setToolTipText("");
        campaignTable.setAutoCreateRowSorter(true);
        campaignTable.setAutoscrolls(false);
        campaignTable.setDoubleBuffered(true);
        campaignTable.setEditingColumn(0);
        campaignTable.setEditingRow(0);
        campaignTable.setFocusable(false);
        campaignTable.setMaximumSize(new java.awt.Dimension(55, 155));
        campaignTable.setMinimumSize(new java.awt.Dimension(55, 155));
        campaignTable.setName("name"); // NOI18N
        campaignTable.setPreferredSize(new java.awt.Dimension(55, 155));
        campaignTable.setRowHeight(14);
        campaignTable.setRowSelectionAllowed(false);
        campaignTable.setSelectionBackground(new java.awt.Color(51, 102, 255));
        campaignTable.setShowGrid(false);
        campaignTable.setSize(new java.awt.Dimension(55, 110));
        campaignTableScrollPane.setViewportView(campaignTable);
        campaignTable.getColumnModel().getColumn(0).setResizable(false);
        campaignTable.getColumnModel().getColumn(0).setPreferredWidth(20);
        campaignTable.getColumnModel().getColumn(1).setResizable(false);

        org.jdesktop.layout.GroupLayout detailsPanelLayout = new org.jdesktop.layout.GroupLayout(detailsPanel);
        detailsPanel.setLayout(detailsPanelLayout);
        detailsPanelLayout.setHorizontalGroup(
            detailsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(detailsPanelLayout.createSequentialGroup()
                .add(110, 110, 110)
                .add(customerLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 169, Short.MAX_VALUE)
                .add(orderLabel)
                .add(165, 165, 165)
                .add(campaignLabel)
                .add(83, 83, 83))
            .add(detailsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(customerTableScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 244, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(orderStatsScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(campaignTableScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 227, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        detailsPanelLayout.setVerticalGroup(
            detailsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(detailsPanelLayout.createSequentialGroup()
                .add(detailsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(customerLabel)
                    .add(campaignLabel)
                    .add(orderLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(detailsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(campaignTableScrollPane, 0, 0, Short.MAX_VALUE)
                    .add(detailsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, orderStatsScrollPane, 0, 0, Short.MAX_VALUE)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, customerTableScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 111, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(7, Short.MAX_VALUE))
        );

        callsPerHourPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Calls per Hour", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("STHeiti", 0, 10), new java.awt.Color(102, 102, 102))); // NOI18N
        callsPerHourPanel.setToolTipText("Call Speed & VM Workload");
        callsPerHourPanel.setFont(new java.awt.Font("STHeiti", 0, 13));
        callsPerHourPanel.setMaximumSize(new java.awt.Dimension(150, 150));
        callsPerHourPanel.setPreferredSize(new java.awt.Dimension(150, 150));
        callsPerHourPanel.setSize(new java.awt.Dimension(150, 150));

        org.jdesktop.layout.GroupLayout callsPerHourPanelLayout = new org.jdesktop.layout.GroupLayout(callsPerHourPanel);
        callsPerHourPanel.setLayout(callsPerHourPanelLayout);
        callsPerHourPanelLayout.setHorizontalGroup(
            callsPerHourPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 150, Short.MAX_VALUE)
        );
        callsPerHourPanelLayout.setVerticalGroup(
            callsPerHourPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 139, Short.MAX_VALUE)
        );

        busyRatioPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Busy Ratio %", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("STHeiti", 0, 10), new java.awt.Color(102, 102, 102))); // NOI18N
        busyRatioPanel.setToolTipText("Percentage Busy Responses Received");
        busyRatioPanel.setFont(new java.awt.Font("STHeiti", 0, 13));
        busyRatioPanel.setMaximumSize(new java.awt.Dimension(150, 150));
        busyRatioPanel.setPreferredSize(new java.awt.Dimension(150, 150));
        busyRatioPanel.setSize(new java.awt.Dimension(150, 150));

        org.jdesktop.layout.GroupLayout busyRatioPanelLayout = new org.jdesktop.layout.GroupLayout(busyRatioPanel);
        busyRatioPanel.setLayout(busyRatioPanelLayout);
        busyRatioPanelLayout.setHorizontalGroup(
            busyRatioPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 150, Short.MAX_VALUE)
        );
        busyRatioPanelLayout.setVerticalGroup(
            busyRatioPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 139, Short.MAX_VALUE)
        );

        answerDelayPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Answer Delay", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("STHeiti", 0, 10), new java.awt.Color(102, 102, 102))); // NOI18N
        answerDelayPanel.setToolTipText("The average time it takes outbound calls are answered");
        answerDelayPanel.setFont(new java.awt.Font("STHeiti", 0, 13));
        answerDelayPanel.setMaximumSize(new java.awt.Dimension(150, 150));
        answerDelayPanel.setPreferredSize(new java.awt.Dimension(150, 150));
        answerDelayPanel.setSize(new java.awt.Dimension(150, 150));

        org.jdesktop.layout.GroupLayout answerDelayPanelLayout = new org.jdesktop.layout.GroupLayout(answerDelayPanel);
        answerDelayPanel.setLayout(answerDelayPanelLayout);
        answerDelayPanelLayout.setHorizontalGroup(
            answerDelayPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 150, Short.MAX_VALUE)
        );
        answerDelayPanelLayout.setVerticalGroup(
            answerDelayPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 139, Short.MAX_VALUE)
        );

        callDurationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Call Duration", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("STHeiti", 0, 10), new java.awt.Color(102, 102, 102))); // NOI18N
        callDurationPanel.setToolTipText("The average time established outbound calls take");
        callDurationPanel.setFont(new java.awt.Font("STHeiti", 0, 13));
        callDurationPanel.setMaximumSize(new java.awt.Dimension(150, 150));
        callDurationPanel.setPreferredSize(new java.awt.Dimension(150, 150));
        callDurationPanel.setSize(new java.awt.Dimension(150, 150));

        org.jdesktop.layout.GroupLayout callDurationPanelLayout = new org.jdesktop.layout.GroupLayout(callDurationPanel);
        callDurationPanel.setLayout(callDurationPanelLayout);
        callDurationPanelLayout.setHorizontalGroup(
            callDurationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 150, Short.MAX_VALUE)
        );
        callDurationPanelLayout.setVerticalGroup(
            callDurationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 139, Short.MAX_VALUE)
        );

        org.jdesktop.layout.GroupLayout dashboardTabLayout = new org.jdesktop.layout.GroupLayout(dashboardTab);
        dashboardTab.setLayout(dashboardTabLayout);
        dashboardTabLayout.setHorizontalGroup(
            dashboardTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(dashboardTabLayout.createSequentialGroup()
                .add(dashboardTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(dashboardTabLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(callsPerHourPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 162, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(busyRatioPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 162, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(answerDelayPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 162, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(callDurationPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 162, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(detailsPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 680, Short.MAX_VALUE))
                .addContainerGap())
        );

        dashboardTabLayout.linkSize(new java.awt.Component[] {answerDelayPanel, busyRatioPanel, callDurationPanel, callsPerHourPanel}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        dashboardTabLayout.setVerticalGroup(
            dashboardTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(dashboardTabLayout.createSequentialGroup()
                .add(detailsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 137, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(dashboardTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(callDurationPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE)
                    .add(busyRatioPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE)
                    .add(callsPerHourPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE)
                    .add(answerDelayPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE))
                .add(8, 8, 8))
        );

        dashboardTabLayout.linkSize(new java.awt.Component[] {answerDelayPanel, busyRatioPanel, callDurationPanel, callsPerHourPanel}, org.jdesktop.layout.GroupLayout.VERTICAL);

        tabPane.addTab("Dashboard", dashboardTab);

        campaignTab.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("STHeiti", 0, 13), new java.awt.Color(255, 255, 255))); // NOI18N
        campaignTab.setForeground(new java.awt.Color(51, 51, 51));
        campaignTab.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        campaignTab.setToolTipText("");
        campaignTab.setFocusTraversalKeysEnabled(false);
        campaignTab.setFont(new java.awt.Font("STHeiti", 0, 12));
        campaignTab.setMaximumSize(new java.awt.Dimension(762, 307));
        campaignTab.setMinimumSize(new java.awt.Dimension(762, 307));
        campaignTab.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                campaignTabMouseClicked(evt);
            }
        });
        campaignTab.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                campaignTabKeyPressed(evt);
            }
        });

        customerManagerPanel.setToolTipText("");
        customerManagerPanel.setFocusTraversalKeysEnabled(false);
        customerManagerPanel.setFont(new java.awt.Font("STHeiti", 0, 13));
        customerManagerPanel.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                customerManagerPanelKeyPressed(evt);
            }
        });

        clearCustomerFieldsButton.setFont(new java.awt.Font("STHeiti", 0, 10));
        clearCustomerFieldsButton.setText("Clear");
        clearCustomerFieldsButton.setToolTipText("Clear Fields");
        clearCustomerFieldsButton.setEnabled(false);
        clearCustomerFieldsButton.setFocusTraversalKeysEnabled(false);
        clearCustomerFieldsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearCustomerFieldsButtonActionPerformed(evt);
            }
        });

        exampleCustomerButton.setFont(new java.awt.Font("STHeiti", 0, 10));
        exampleCustomerButton.setText("Example");
        exampleCustomerButton.setToolTipText("Example Customer");
        exampleCustomerButton.setEnabled(false);
        exampleCustomerButton.setFocusTraversalKeysEnabled(false);
        exampleCustomerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exampleCustomerButtonActionPerformed(evt);
            }
        });

        searchCustomerButton.setFont(new java.awt.Font("STHeiti", 0, 10));
        searchCustomerButton.setText("Search");
        searchCustomerButton.setToolTipText("Search Customer");
        searchCustomerButton.setEnabled(false);
        searchCustomerButton.setFocusTraversalKeysEnabled(false);

        selectCustomerButton.setFont(new java.awt.Font("STHeiti", 0, 10));
        selectCustomerButton.setText("Select");
        selectCustomerButton.setToolTipText("Select Customer");
        selectCustomerButton.setEnabled(false);
        selectCustomerButton.setFocusTraversalKeysEnabled(false);
        selectCustomerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectCustomerButtonActionPerformed(evt);
            }
        });
        selectCustomerButton.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                selectCustomerButtonKeyPressed(evt);
            }
        });

        insertCustomerButton.setFont(new java.awt.Font("STHeiti", 0, 10));
        insertCustomerButton.setText("Insert");
        insertCustomerButton.setToolTipText("Insert Customer");
        insertCustomerButton.setEnabled(false);
        insertCustomerButton.setFocusTraversalKeysEnabled(false);
        insertCustomerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                insertCustomerButtonActionPerformed(evt);
            }
        });
        insertCustomerButton.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                insertCustomerButtonKeyPressed(evt);
            }
        });

        updateCustomerButton.setFont(new java.awt.Font("STHeiti", 0, 10));
        updateCustomerButton.setText("Update");
        updateCustomerButton.setToolTipText("Update Customer");
        updateCustomerButton.setEnabled(false);
        updateCustomerButton.setFocusTraversalKeysEnabled(false);
        updateCustomerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateCustomerButtonActionPerformed(evt);
            }
        });
        updateCustomerButton.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                updateCustomerButtonKeyPressed(evt);
            }
        });

        deleteCustomerButton.setFont(new java.awt.Font("STHeiti", 0, 10));
        deleteCustomerButton.setText("Delete");
        deleteCustomerButton.setToolTipText("Delete Customer (based on Id Field)");
        deleteCustomerButton.setEnabled(false);
        deleteCustomerButton.setFocusTraversalKeysEnabled(false);
        deleteCustomerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteCustomerButtonActionPerformed(evt);
            }
        });
        deleteCustomerButton.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                deleteCustomerButtonKeyPressed(evt);
            }
        });

        companyInformationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Company Information", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("STHeiti", 0, 14), new java.awt.Color(51, 51, 51))); // NOI18N
        companyInformationPanel.setToolTipText("");

        companyIdLabel.setFont(new java.awt.Font("STHeiti", 0, 12));
        companyIdLabel.setForeground(new java.awt.Color(51, 51, 51));
        companyIdLabel.setText("Id / Date");

        companyNameLabel.setFont(new java.awt.Font("STHeiti", 0, 12));
        companyNameLabel.setForeground(new java.awt.Color(51, 51, 51));
        companyNameLabel.setText("Name");

        companyAddressLabel.setFont(new java.awt.Font("STHeiti", 0, 12));
        companyAddressLabel.setForeground(new java.awt.Color(51, 51, 51));
        companyAddressLabel.setText("Address");

        companyAddressNrLabel.setFont(new java.awt.Font("STHeiti", 0, 12));
        companyAddressNrLabel.setForeground(new java.awt.Color(51, 51, 51));
        companyAddressNrLabel.setText("Nr");

        companyPostcodeLabel.setFont(new java.awt.Font("STHeiti", 0, 12));
        companyPostcodeLabel.setForeground(new java.awt.Color(51, 51, 51));
        companyPostcodeLabel.setText("Postcode");

        companyCityLabel.setFont(new java.awt.Font("STHeiti", 0, 12));
        companyCityLabel.setForeground(new java.awt.Color(51, 51, 51));
        companyCityLabel.setText("City");

        companyCountryLabel.setFont(new java.awt.Font("STHeiti", 0, 12));
        companyCountryLabel.setForeground(new java.awt.Color(51, 51, 51));
        companyCountryLabel.setText("Country");
        companyCountryLabel.setToolTipText("");

        customerIdField.setFont(new java.awt.Font("STHeiti", 0, 10));
        customerIdField.setText("0");
        customerIdField.setToolTipText("Customer Id");
        customerIdField.setEnabled(false);
        customerIdField.setFocusCycleRoot(true);
        customerIdField.setFocusTraversalKeysEnabled(false);
        customerIdField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                customerIdFieldKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                customerIdFieldKeyReleased(evt);
            }
        });

        customerCompanyNameField.setFont(new java.awt.Font("STHeiti", 0, 10));
        customerCompanyNameField.setToolTipText("");
        customerCompanyNameField.setEnabled(false);
        customerCompanyNameField.setFocusTraversalKeysEnabled(false);
        customerCompanyNameField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                customerCompanyNameFieldKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                customerCompanyNameFieldKeyReleased(evt);
            }
        });

        customerAddressField.setFont(new java.awt.Font("STHeiti", 0, 10));
        customerAddressField.setToolTipText("");
        customerAddressField.setEnabled(false);
        customerAddressField.setFocusTraversalKeysEnabled(false);
        customerAddressField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                customerAddressFieldKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                customerAddressFieldKeyReleased(evt);
            }
        });

        customerAddressNrField.setFont(new java.awt.Font("STHeiti", 0, 10));
        customerAddressNrField.setToolTipText("");
        customerAddressNrField.setEnabled(false);
        customerAddressNrField.setFocusTraversalKeysEnabled(false);
        customerAddressNrField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                customerAddressNrFieldKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                customerAddressNrFieldKeyReleased(evt);
            }
        });

        customerCityField.setFont(new java.awt.Font("STHeiti", 0, 10));
        customerCityField.setToolTipText("");
        customerCityField.setEnabled(false);
        customerCityField.setFocusTraversalKeysEnabled(false);
        customerCityField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                customerCityFieldKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                customerCityFieldKeyReleased(evt);
            }
        });

        customerPostcodeField.setFont(new java.awt.Font("STHeiti", 0, 10));
        customerPostcodeField.setToolTipText("");
        customerPostcodeField.setEnabled(false);
        customerPostcodeField.setFocusTraversalKeysEnabled(false);
        customerPostcodeField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                customerPostcodeFieldKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                customerPostcodeFieldKeyReleased(evt);
            }
        });

        customerCountryField.setFont(new java.awt.Font("STHeiti", 0, 10));
        customerCountryField.setToolTipText("");
        customerCountryField.setEnabled(false);
        customerCountryField.setFocusTraversalKeysEnabled(false);
        customerCountryField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                customerCountryFieldKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                customerCountryFieldKeyReleased(evt);
            }
        });

        customerDateField.setFont(new java.awt.Font("STHeiti", 0, 10));
        customerDateField.setToolTipText("Creation Date Epoch Format");
        customerDateField.setEnabled(false);
        customerDateField.setFocusCycleRoot(true);
        customerDateField.setFocusTraversalKeysEnabled(false);
        customerDateField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                customerDateFieldKeyPressed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout companyInformationPanelLayout = new org.jdesktop.layout.GroupLayout(companyInformationPanel);
        companyInformationPanel.setLayout(companyInformationPanelLayout);
        companyInformationPanelLayout.setHorizontalGroup(
            companyInformationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(companyInformationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(companyInformationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(companyInformationPanelLayout.createSequentialGroup()
                        .add(companyIdLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 56, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(customerIdField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 71, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(customerDateField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE))
                    .add(companyInformationPanelLayout.createSequentialGroup()
                        .add(companyInformationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(companyNameLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 56, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(companyAddressLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 56, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(companyPostcodeLabel)
                            .add(companyCountryLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 56, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(companyInformationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(companyInformationPanelLayout.createSequentialGroup()
                                .add(customerAddressField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(companyAddressNrLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(customerAddressNrField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE))
                            .add(customerCountryField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, companyInformationPanelLayout.createSequentialGroup()
                                .add(customerPostcodeField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 61, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(companyCityLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(customerCityField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 67, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(customerCompanyNameField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE))))
                .addContainerGap())
        );
        companyInformationPanelLayout.setVerticalGroup(
            companyInformationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(companyInformationPanelLayout.createSequentialGroup()
                .add(companyInformationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(customerIdField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(companyIdLabel)
                    .add(customerDateField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(companyInformationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(companyNameLabel)
                    .add(customerCompanyNameField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(companyInformationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(companyAddressLabel)
                    .add(customerAddressField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(companyAddressNrLabel)
                    .add(customerAddressNrField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(companyInformationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(companyPostcodeLabel)
                    .add(customerPostcodeField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(customerCityField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(companyCityLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(companyInformationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(companyCountryLabel)
                    .add(customerCountryField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        contactInformationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Personal Information", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("STHeiti", 0, 14), new java.awt.Color(51, 51, 51))); // NOI18N
        contactInformationPanel.setToolTipText("");

        contactNameLabel.setFont(new java.awt.Font("STHeiti", 0, 12));
        contactNameLabel.setForeground(new java.awt.Color(51, 51, 51));
        contactNameLabel.setText("Full Name");

        contactEmailLabel.setFont(new java.awt.Font("STHeiti", 0, 12));
        contactEmailLabel.setForeground(new java.awt.Color(51, 51, 51));
        contactEmailLabel.setText("Email");

        contactPhoneLabel.setFont(new java.awt.Font("STHeiti", 0, 12));
        contactPhoneLabel.setForeground(new java.awt.Color(51, 51, 51));
        contactPhoneLabel.setText("Phone");

        contactMobileLabel.setFont(new java.awt.Font("STHeiti", 0, 12));
        contactMobileLabel.setForeground(new java.awt.Color(51, 51, 51));
        contactMobileLabel.setText("Mobile");

        customerDiscountLabel.setFont(new java.awt.Font("STHeiti", 0, 12));
        customerDiscountLabel.setForeground(new java.awt.Color(51, 51, 51));
        customerDiscountLabel.setText("Discount");

        customerContactNameField.setFont(new java.awt.Font("STHeiti", 0, 10));
        customerContactNameField.setToolTipText("");
        customerContactNameField.setEnabled(false);
        customerContactNameField.setFocusTraversalKeysEnabled(false);
        customerContactNameField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                customerContactNameFieldKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                customerContactNameFieldKeyReleased(evt);
            }
        });

        customerEmailField.setFont(new java.awt.Font("STHeiti", 0, 10));
        customerEmailField.setToolTipText("");
        customerEmailField.setEnabled(false);
        customerEmailField.setFocusTraversalKeysEnabled(false);
        customerEmailField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                customerEmailFieldKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                customerEmailFieldKeyReleased(evt);
            }
        });

        customerPhoneNrField.setFont(new java.awt.Font("STHeiti", 0, 10));
        customerPhoneNrField.setToolTipText("");
        customerPhoneNrField.setEnabled(false);
        customerPhoneNrField.setFocusTraversalKeysEnabled(false);
        customerPhoneNrField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                customerPhoneNrFieldKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                customerPhoneNrFieldKeyReleased(evt);
            }
        });

        customerMobileNrField.setFont(new java.awt.Font("STHeiti", 0, 10));
        customerMobileNrField.setToolTipText("");
        customerMobileNrField.setEnabled(false);
        customerMobileNrField.setFocusTraversalKeysEnabled(false);
        customerMobileNrField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                customerMobileNrFieldKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                customerMobileNrFieldKeyReleased(evt);
            }
        });

        customerDiscountField.setFont(new java.awt.Font("STHeiti", 0, 10));
        customerDiscountField.setToolTipText("Customer Discount Percentage");
        customerDiscountField.setEnabled(false);
        customerDiscountField.setFocusTraversalKeysEnabled(false);
        customerDiscountField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                customerDiscountFieldKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                customerDiscountFieldKeyReleased(evt);
            }
        });

        org.jdesktop.layout.GroupLayout contactInformationPanelLayout = new org.jdesktop.layout.GroupLayout(contactInformationPanel);
        contactInformationPanel.setLayout(contactInformationPanelLayout);
        contactInformationPanelLayout.setHorizontalGroup(
            contactInformationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(contactInformationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(contactInformationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(contactEmailLabel)
                    .add(contactNameLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(contactPhoneLabel)
                    .add(contactMobileLabel)
                    .add(customerDiscountLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .add(contactInformationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(contactInformationPanelLayout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(contactInformationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, customerEmailField)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, customerContactNameField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE)))
                    .add(contactInformationPanelLayout.createSequentialGroup()
                        .add(12, 12, 12)
                        .add(contactInformationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(customerPhoneNrField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE)
                            .add(customerMobileNrField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE)
                            .add(customerDiscountField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE))))
                .addContainerGap())
        );
        contactInformationPanelLayout.setVerticalGroup(
            contactInformationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(contactInformationPanelLayout.createSequentialGroup()
                .add(contactInformationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(contactNameLabel)
                    .add(customerContactNameField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(contactInformationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(customerEmailField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(contactEmailLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(contactInformationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(customerPhoneNrField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(contactPhoneLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(contactInformationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(customerMobileNrField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(contactMobileLabel))
                .add(contactInformationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(contactInformationPanelLayout.createSequentialGroup()
                        .add(12, 12, 12)
                        .add(customerDiscountLabel))
                    .add(contactInformationPanelLayout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(customerDiscountField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
        );

        contactInformationPanelLayout.linkSize(new java.awt.Component[] {customerContactNameField, customerDiscountField, customerEmailField, customerMobileNrField, customerPhoneNrField}, org.jdesktop.layout.GroupLayout.VERTICAL);

        previousCustomerButton.setFont(new java.awt.Font("STHeiti", 0, 10));
        previousCustomerButton.setText("<");
        previousCustomerButton.setToolTipText("Select Customer");
        previousCustomerButton.setFocusTraversalKeysEnabled(false);
        previousCustomerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previousCustomerButtonActionPerformed(evt);
            }
        });
        previousCustomerButton.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                previousCustomerButtonKeyPressed(evt);
            }
        });

        nextCustomerButton.setFont(new java.awt.Font("STHeiti", 0, 10));
        nextCustomerButton.setText(">");
        nextCustomerButton.setToolTipText("Select Customer");
        nextCustomerButton.setFocusTraversalKeysEnabled(false);
        nextCustomerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextCustomerButtonActionPerformed(evt);
            }
        });
        nextCustomerButton.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                nextCustomerButtonKeyPressed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout customerManagerPanelLayout = new org.jdesktop.layout.GroupLayout(customerManagerPanel);
        customerManagerPanel.setLayout(customerManagerPanelLayout);
        customerManagerPanelLayout.setHorizontalGroup(
            customerManagerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(customerManagerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(customerManagerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(customerManagerPanelLayout.createSequentialGroup()
                        .add(customerManagerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, clearCustomerFieldsButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, exampleCustomerButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, searchCustomerButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE)
                            .add(customerManagerPanelLayout.createSequentialGroup()
                                .add(previousCustomerButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 45, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(nextCustomerButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 44, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                        .add(24, 24, 24))
                    .add(customerManagerPanelLayout.createSequentialGroup()
                        .add(customerManagerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(customerManagerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, insertCustomerButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, updateCustomerButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, deleteCustomerButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 86, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, selectCustomerButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))
                .add(companyInformationPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(contactInformationPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(37, 37, 37))
        );

        customerManagerPanelLayout.linkSize(new java.awt.Component[] {deleteCustomerButton, insertCustomerButton, selectCustomerButton, updateCustomerButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        customerManagerPanelLayout.linkSize(new java.awt.Component[] {nextCustomerButton, previousCustomerButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        customerManagerPanelLayout.linkSize(new java.awt.Component[] {clearCustomerFieldsButton, exampleCustomerButton, searchCustomerButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        customerManagerPanelLayout.setVerticalGroup(
            customerManagerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(customerManagerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(customerManagerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(contactInformationPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 171, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(companyInformationPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(customerManagerPanelLayout.createSequentialGroup()
                        .add(clearCustomerFieldsButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(exampleCustomerButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(searchCustomerButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(customerManagerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(previousCustomerButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(nextCustomerButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(selectCustomerButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(insertCustomerButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(updateCustomerButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(deleteCustomerButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(71, Short.MAX_VALUE))
        );

        customerManagerPanelLayout.linkSize(new java.awt.Component[] {clearCustomerFieldsButton, deleteCustomerButton, exampleCustomerButton, insertCustomerButton, searchCustomerButton, selectCustomerButton, updateCustomerButton}, org.jdesktop.layout.GroupLayout.VERTICAL);

        customerManagerPanelLayout.linkSize(new java.awt.Component[] {companyInformationPanel, contactInformationPanel}, org.jdesktop.layout.GroupLayout.VERTICAL);

        campaignTab.addTab("Customer", customerManagerPanel);

        campaignManagerPanel.setToolTipText("");
        campaignManagerPanel.setFocusTraversalKeysEnabled(false);
        campaignManagerPanel.setFont(new java.awt.Font("STHeiti", 0, 13));
        campaignManagerPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                campaignManagerPanelMouseClicked(evt);
            }
        });

        selectOrderButton.setFont(new java.awt.Font("STHeiti", 0, 10));
        selectOrderButton.setText("Select");
        selectOrderButton.setToolTipText("Select Order");
        selectOrderButton.setEnabled(false);
        selectOrderButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectOrderButtonActionPerformed(evt);
            }
        });

        updateOrderButton.setFont(new java.awt.Font("STHeiti", 0, 10));
        updateOrderButton.setText("Update");
        updateOrderButton.setToolTipText("Update Order");
        updateOrderButton.setEnabled(false);
        updateOrderButton.setFocusable(false);
        updateOrderButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateOrderButtonActionPerformed(evt);
            }
        });

        deleteOrderButton.setFont(new java.awt.Font("STHeiti", 0, 10));
        deleteOrderButton.setText("Delete");
        deleteOrderButton.setToolTipText("Delete Order");
        deleteOrderButton.setEnabled(false);
        deleteOrderButton.setFocusable(false);
        deleteOrderButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteOrderButtonActionPerformed(evt);
            }
        });

        serviceLoopProgressBar.setFont(new java.awt.Font("STHeiti", 0, 12));
        serviceLoopProgressBar.setOrientation(1);
        serviceLoopProgressBar.setToolTipText("Progress Bar creating destinations (phonenumbers)");
        serviceLoopProgressBar.setBorderPainted(false);
        serviceLoopProgressBar.setEnabled(false);
        serviceLoopProgressBar.setFocusTraversalKeysEnabled(false);
        serviceLoopProgressBar.setFocusable(false);
        serviceLoopProgressBar.setName("progressBar"); // NOI18N
        serviceLoopProgressBar.setOpaque(true);
        serviceLoopProgressBar.setStringPainted(true);

        orderInner2PanelPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Order Campaign", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("STHeiti", 0, 12))); // NOI18N
        orderInner2PanelPanel.setToolTipText("");
        orderInner2PanelPanel.setFont(new java.awt.Font("STHeiti", 0, 13));
        orderInner2PanelPanel.setMaximumSize(new java.awt.Dimension(307, 228));
        orderInner2PanelPanel.setMinimumSize(new java.awt.Dimension(307, 228));
        orderInner2PanelPanel.setSize(new java.awt.Dimension(307, 228));

        destinationTextArea.setBackground(java.awt.Color.lightGray);
        destinationTextArea.setColumns(1);
        destinationTextArea.setFont(new java.awt.Font("STHeiti", 0, 10));
        destinationTextArea.setRows(3);
        destinationTextArea.setToolTipText("Phonenumbers / Destinations");
        destinationTextArea.setDoubleBuffered(true);
        destinationTextArea.setEnabled(false);
        destinationTextArea.setMinimumSize(new java.awt.Dimension(330, 65));
        destinationTextArea.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                destinationTextAreaCaretUpdate(evt);
            }
        });
        destinationTextArea.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                destinationTextAreaFocusGained(evt);
            }
        });
        destinationScrollPane.setViewportView(destinationTextArea);

        orderDateField.setFont(new java.awt.Font("STHeiti", 0, 10));
        orderDateField.setForeground(new java.awt.Color(204, 204, 204));
        orderDateField.setText("0");
        orderDateField.setToolTipText("Creation Date Epoch Format");
        orderDateField.setEnabled(false);
        orderDateField.setFocusTraversalKeysEnabled(false);
        orderDateField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                orderDateFieldKeyPressed(evt);
            }
        });

        orderFilenameField.setFont(new java.awt.Font("STHeiti", 0, 10));
        orderFilenameField.setToolTipText("Soundfile (*.wav) to play during Calls");
        orderFilenameField.setEnabled(false);
        orderFilenameField.setFocusTraversalKeysEnabled(false);
        orderFilenameField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                orderFilenameFieldKeyReleased(evt);
            }
        });

        browseFileButton.setFont(new java.awt.Font("STHeiti", 0, 10));
        browseFileButton.setText("❒");
        browseFileButton.setToolTipText("Select a SoundFile (*.wav) for your campaign");
        browseFileButton.setEnabled(false);
        browseFileButton.setFocusTraversalKeysEnabled(false);
        browseFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseFileButtonActionPerformed(evt);
            }
        });

        orderCustomerIdComboBox.setFont(new java.awt.Font("STHeiti", 0, 10));
        orderCustomerIdComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Customer" }));
        orderCustomerIdComboBox.setToolTipText("Customer Id");
        orderCustomerIdComboBox.setEnabled(false);
        orderCustomerIdComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                orderCustomerIdComboBoxItemStateChanged(evt);
            }
        });
        orderCustomerIdComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                orderCustomerIdComboBoxActionPerformed(evt);
            }
        });

        orderIdComboBox.setFont(new java.awt.Font("STHeiti", 0, 10));
        orderIdComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Order" }));
        orderIdComboBox.setToolTipText("Order Id");
        orderIdComboBox.setEnabled(false);
        orderIdComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                orderIdComboBoxItemStateChanged(evt);
            }
        });
        orderIdComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                orderIdComboBoxActionPerformed(evt);
            }
        });

        timewindowScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        timewindowScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        timewindowList.setBackground(new java.awt.Color(204, 204, 204));
        timewindowList.setFont(new java.awt.Font("STHeiti", 0, 12));
        timewindowList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Morning", "Daytime", "Evening" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        timewindowList.setToolTipText("The timewindow(s) your campaign will be running (use <CTRL> to select multiple timewindows)");
        timewindowList.setEnabled(false);
        timewindowList.setSelectedIndex(0);
        timewindowList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                timewindowListValueChanged(evt);
            }
        });
        timewindowScrollPane.setViewportView(timewindowList);

        recipientsScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        recipientsScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        recipientsList.setBackground(new java.awt.Color(204, 204, 204));
        recipientsList.setFont(new java.awt.Font("STHeiti", 0, 12));
        recipientsList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Business", "Consumers", "Custom" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        recipientsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        recipientsList.setToolTipText("Campaign Type: Business to: [Business / Consumers / Custom] (Optionally used to charge your customer)");
        recipientsList.setEnabled(false);
        recipientsList.setSelectedIndex(0);
        recipientsList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                recipientsListValueChanged(evt);
            }
        });
        recipientsScrollPane.setViewportView(recipientsList);

        org.jdesktop.layout.GroupLayout orderInner2PanelPanelLayout = new org.jdesktop.layout.GroupLayout(orderInner2PanelPanel);
        orderInner2PanelPanel.setLayout(orderInner2PanelPanelLayout);
        orderInner2PanelPanelLayout.setHorizontalGroup(
            orderInner2PanelPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(orderInner2PanelPanelLayout.createSequentialGroup()
                .add(6, 6, 6)
                .add(orderInner2PanelPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, destinationScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, orderInner2PanelPanelLayout.createSequentialGroup()
                        .add(recipientsScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 141, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(timewindowScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 141, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(orderInner2PanelPanelLayout.createSequentialGroup()
                        .add(browseFileButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 46, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(orderFilenameField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE))
                    .add(orderInner2PanelPanelLayout.createSequentialGroup()
                        .add(orderCustomerIdComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 98, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(orderIdComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 79, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(orderDateField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 99, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(6, 6, 6))
        );

        orderInner2PanelPanelLayout.linkSize(new java.awt.Component[] {recipientsScrollPane, timewindowScrollPane}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        orderInner2PanelPanelLayout.setVerticalGroup(
            orderInner2PanelPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(orderInner2PanelPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(orderInner2PanelPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(orderDateField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
                    .add(orderCustomerIdComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(orderIdComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(orderInner2PanelPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(browseFileButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(orderFilenameField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(orderInner2PanelPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(timewindowScrollPane, 0, 0, Short.MAX_VALUE)
                    .add(recipientsScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 58, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(destinationScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 91, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        orderInner2PanelPanelLayout.linkSize(new java.awt.Component[] {orderDateField, orderFilenameField}, org.jdesktop.layout.GroupLayout.VERTICAL);

        orderInner2PanelPanelLayout.linkSize(new java.awt.Component[] {recipientsScrollPane, timewindowScrollPane}, org.jdesktop.layout.GroupLayout.VERTICAL);

        orderInnerPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Total", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("STHeiti", 0, 12))); // NOI18N
        orderInnerPanel.setToolTipText("");
        orderInnerPanel.setFont(new java.awt.Font("STHeiti", 0, 13));
        orderInnerPanel.setMaximumSize(new java.awt.Dimension(223, 228));
        orderInnerPanel.setMinimumSize(new java.awt.Dimension(223, 228));
        orderInnerPanel.setSize(new java.awt.Dimension(223, 228));

        orderDestinationsQuantityLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
        orderDestinationsQuantityLabel.setForeground(new java.awt.Color(51, 51, 51));
        orderDestinationsQuantityLabel.setText("Quantity");

        orderMessageDurationLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
        orderMessageDurationLabel.setForeground(new java.awt.Color(51, 51, 51));
        orderMessageDurationLabel.setText("M. Length Sec");

        orderMessageRatesLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
        orderMessageRatesLabel.setForeground(new java.awt.Color(51, 51, 51));
        orderMessageRatesLabel.setText("Mess Rate/S €");

        orderMessageRatesLabel1.setFont(new java.awt.Font("STHeiti", 0, 10));
        orderMessageRatesLabel1.setForeground(new java.awt.Color(51, 51, 51));
        orderMessageRatesLabel1.setText("Mess Rate    €");

        orderMessageDurationField.setBackground(new java.awt.Color(204, 204, 204));
        orderMessageDurationField.setEditable(false);
        orderMessageDurationField.setFont(new java.awt.Font("STHeiti", 0, 10));
        orderMessageDurationField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        orderMessageDurationField.setToolTipText("The number of seconds the message takes");
        orderMessageDurationField.setFocusTraversalKeysEnabled(false);
        orderMessageDurationField.setFocusable(false);
        orderMessageDurationField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                orderMessageDurationFieldKeyPressed(evt);
            }
        });

        orderMessageRatePerSecondField.setBackground(new java.awt.Color(204, 204, 204));
        orderMessageRatePerSecondField.setEditable(false);
        orderMessageRatePerSecondField.setFont(new java.awt.Font("STHeiti", 0, 10));
        orderMessageRatePerSecondField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        orderMessageRatePerSecondField.setToolTipText("The rate of the message per second that you could charge your customer");
        orderMessageRatePerSecondField.setFocusTraversalKeysEnabled(false);
        orderMessageRatePerSecondField.setFocusable(false);
        orderMessageRatePerSecondField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                orderMessageRatePerSecondFieldKeyPressed(evt);
            }
        });

        orderMessageRateField.setBackground(new java.awt.Color(204, 204, 204));
        orderMessageRateField.setEditable(false);
        orderMessageRateField.setFont(new java.awt.Font("STHeiti", 0, 10));
        orderMessageRateField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        orderMessageRateField.setToolTipText("The rate per message you could be charging your customer");
        orderMessageRateField.setFocusTraversalKeysEnabled(false);
        orderMessageRateField.setFocusable(false);
        orderMessageRateField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                orderMessageRateFieldKeyPressed(evt);
            }
        });

        orderDestinationsQuantityField.setBackground(new java.awt.Color(204, 204, 204));
        orderDestinationsQuantityField.setFont(new java.awt.Font("STHeiti", 0, 10));
        orderDestinationsQuantityField.setForeground(new java.awt.Color(51, 51, 51));
        orderDestinationsQuantityField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        orderDestinationsQuantityField.setText("0");
        orderDestinationsQuantityField.setToolTipText("Total number of Calls Ordered");
        orderDestinationsQuantityField.setFocusTraversalKeysEnabled(false);
        orderDestinationsQuantityField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                orderDestinationsQuantityFieldKeyPressed(evt);
            }
        });

        orderSubTotalLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
        orderSubTotalLabel.setForeground(new java.awt.Color(51, 51, 51));
        orderSubTotalLabel.setText("SubTotal       €");

        orderSubTotalField.setBackground(new java.awt.Color(204, 204, 204));
        orderSubTotalField.setEditable(false);
        orderSubTotalField.setFont(new java.awt.Font("STHeiti", 0, 10));
        orderSubTotalField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        orderSubTotalField.setToolTipText("The SubTotal of this Order (excl VAT/BTW)");
        orderSubTotalField.setFocusTraversalKeysEnabled(false);
        orderSubTotalField.setFocusable(false);

        org.jdesktop.layout.GroupLayout orderInnerPanelLayout = new org.jdesktop.layout.GroupLayout(orderInnerPanel);
        orderInnerPanel.setLayout(orderInnerPanelLayout);
        orderInnerPanelLayout.setHorizontalGroup(
            orderInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(orderInnerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(orderInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(orderDestinationsQuantityLabel)
                    .add(orderMessageRatesLabel)
                    .add(orderSubTotalLabel)
                    .add(orderMessageDurationLabel)
                    .add(orderMessageRatesLabel1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 47, Short.MAX_VALUE)
                .add(orderInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(orderSubTotalField)
                    .add(orderDestinationsQuantityField)
                    .add(orderMessageRateField)
                    .add(orderMessageRatePerSecondField)
                    .add(orderMessageDurationField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 73, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        orderInnerPanelLayout.setVerticalGroup(
            orderInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(orderInnerPanelLayout.createSequentialGroup()
                .add(orderInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(orderMessageDurationField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(orderMessageDurationLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(orderInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(orderMessageRatePerSecondField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(orderMessageRatesLabel))
                .add(6, 6, 6)
                .add(orderInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(orderMessageRateField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(orderMessageRatesLabel1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(orderInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(orderDestinationsQuantityField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(orderDestinationsQuantityLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 86, Short.MAX_VALUE)
                .add(orderInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(orderSubTotalField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(orderSubTotalLabel))
                .addContainerGap())
        );

        orderInnerPanelLayout.linkSize(new java.awt.Component[] {orderDestinationsQuantityField, orderMessageDurationField, orderMessageRateField, orderMessageRatePerSecondField, orderSubTotalField}, org.jdesktop.layout.GroupLayout.VERTICAL);

        confirmOrderButton.setFont(new java.awt.Font("STHeiti", 0, 10));
        confirmOrderButton.setText("Confirm");
        confirmOrderButton.setToolTipText("Confirm and create Order");
        confirmOrderButton.setEnabled(false);
        confirmOrderButton.setFocusable(false);
        confirmOrderButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                confirmOrderButtonActionPerformed(evt);
            }
        });

        scheduleButton.setFont(new java.awt.Font("STHeiti", 0, 10));
        scheduleButton.setText("Schedule");
        scheduleButton.setToolTipText("Activate the scheduler window");
        scheduleButton.setEnabled(false);
        scheduleButton.setFocusable(false);
        scheduleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scheduleButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout campaignManagerPanelLayout = new org.jdesktop.layout.GroupLayout(campaignManagerPanel);
        campaignManagerPanel.setLayout(campaignManagerPanelLayout);
        campaignManagerPanelLayout.setHorizontalGroup(
            campaignManagerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(campaignManagerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(campaignManagerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(confirmOrderButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, serviceLoopProgressBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, selectOrderButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, updateOrderButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, deleteOrderButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, scheduleButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(orderInner2PanelPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(orderInnerPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 214, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(21, 21, 21))
        );

        campaignManagerPanelLayout.linkSize(new java.awt.Component[] {confirmOrderButton, deleteOrderButton, scheduleButton, selectOrderButton, serviceLoopProgressBar, updateOrderButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        campaignManagerPanelLayout.setVerticalGroup(
            campaignManagerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(campaignManagerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(campaignManagerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(orderInnerPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(orderInner2PanelPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(campaignManagerPanelLayout.createSequentialGroup()
                        .add(selectOrderButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(updateOrderButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(deleteOrderButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(serviceLoopProgressBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 92, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(scheduleButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(confirmOrderButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 40, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        campaignManagerPanelLayout.linkSize(new java.awt.Component[] {deleteOrderButton, selectOrderButton, updateOrderButton}, org.jdesktop.layout.GroupLayout.VERTICAL);

        campaignTab.addTab("Campaign", campaignManagerPanel);

        tabPane.addTab("Campaign Manager", campaignTab);

        toolsTab.setToolTipText("");
        toolsTab.setFocusTraversalKeysEnabled(false);
        toolsTab.setFont(new java.awt.Font("STHeiti", 0, 12));
        toolsTab.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                toolsTabKeyPressed(evt);
            }
        });

        toolsInnerPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Tools", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("STHeiti", 0, 14))); // NOI18N
        toolsInnerPanel.setToolTipText("");
        toolsInnerPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                toolsInnerPanelMouseClicked(evt);
            }
        });

        netManagerInboundClientToggleButton.setFont(new java.awt.Font("STHeiti", 0, 10));
        netManagerInboundClientToggleButton.setText("Check Inbound");
        netManagerInboundClientToggleButton.setToolTipText("Start Inbound NetManager Client");
        netManagerInboundClientToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                netManagerInboundClientToggleButtonActionPerformed(evt);
            }
        });

        netManagerOutboundClientToggleButton.setFont(new java.awt.Font("STHeiti", 0, 10));
        netManagerOutboundClientToggleButton.setText("Check Outbound");
        netManagerOutboundClientToggleButton.setToolTipText("Start Outbound NetManager Client");
        netManagerOutboundClientToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                netManagerOutboundClientToggleButtonActionPerformed(evt);
            }
        });

        controlsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Controls", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("STHeiti", 0, 10))); // NOI18N

        callCenterEnabledCheckBox.setFont(new java.awt.Font("STHeiti", 0, 10));
        callCenterEnabledCheckBox.setSelected(true);
        callCenterEnabledCheckBox.setToolTipText("Enable automated startup of Call Center(s)");
        callCenterEnabledCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        callCenterEnabledCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        callCenterEnabledCheckBox.setIconTextGap(0);

        callCenterEnabledLabel.setFont(new java.awt.Font("STHeiti", 0, 8));
        callCenterEnabledLabel.setText("Enable");

        managedCheckBox.setFont(new java.awt.Font("STHeiti", 0, 10));
        managedCheckBox.setSelected(true);
        managedCheckBox.setToolTipText("CallCenters Start in Managed Mode");
        managedCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        managedCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        managedCheckBox.setIconTextGap(0);
        managedCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                managedCheckBoxActionPerformed(evt);
            }
        });

        managedLabel.setFont(new java.awt.Font("STHeiti", 0, 8));
        managedLabel.setText("Managed");

        smoothCheckBox.setFont(new java.awt.Font("STHeiti", 0, 10)); // NOI18N
        smoothCheckBox.setSelected(true);
        smoothCheckBox.setToolTipText("Smooth Meter Animation (disabling slightly increases performance)");
        smoothCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        smoothCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        smoothCheckBox.setIconTextGap(0);

        smoothLabel.setFont(new java.awt.Font("STHeiti", 0, 8));
        smoothLabel.setText("Smooth");

        testPhoneButton.setFont(new java.awt.Font("STHeiti", 0, 10));
        testPhoneButton.setText("☎");
        testPhoneButton.setToolTipText("Test Phone");
        testPhoneButton.setFocusTraversalKeysEnabled(false);
        testPhoneButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                testPhoneButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout controlsPanelLayout = new org.jdesktop.layout.GroupLayout(controlsPanel);
        controlsPanel.setLayout(controlsPanelLayout);
        controlsPanelLayout.setHorizontalGroup(
            controlsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(controlsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(controlsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(callCenterEnabledLabel)
                    .add(controlsPanelLayout.createSequentialGroup()
                        .add(6, 6, 6)
                        .add(callCenterEnabledCheckBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(42, 42, 42)
                .add(controlsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(managedLabel)
                    .add(controlsPanelLayout.createSequentialGroup()
                        .add(6, 6, 6)
                        .add(managedCheckBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(27, 27, 27)
                .add(controlsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(controlsPanelLayout.createSequentialGroup()
                        .add(6, 6, 6)
                        .add(smoothCheckBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(smoothLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 106, Short.MAX_VALUE)
                .add(testPhoneButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 42, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        controlsPanelLayout.linkSize(new java.awt.Component[] {callCenterEnabledCheckBox, managedCheckBox}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        controlsPanelLayout.setVerticalGroup(
            controlsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(controlsPanelLayout.createSequentialGroup()
                .add(controlsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(controlsPanelLayout.createSequentialGroup()
                        .add(managedLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(managedCheckBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 11, Short.MAX_VALUE))
                    .add(controlsPanelLayout.createSequentialGroup()
                        .add(callCenterEnabledLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(callCenterEnabledCheckBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 11, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(controlsPanelLayout.createSequentialGroup()
                        .add(smoothLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(smoothCheckBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 0, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(testPhoneButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 26, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        controlsPanelLayout.linkSize(new java.awt.Component[] {callCenterEnabledCheckBox, managedCheckBox, smoothCheckBox}, org.jdesktop.layout.GroupLayout.VERTICAL);

        lookAndFeelPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Look and Feel", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("STHeiti", 0, 10))); // NOI18N

        lookAndFeelGroup.add(lookAndFeelRButtonGTK);
        lookAndFeelRButtonGTK.setFont(new java.awt.Font("STHeiti", 0, 8));
        lookAndFeelRButtonGTK.setText("GTK");
        lookAndFeelRButtonGTK.setToolTipText("Set Look & Feel");
        lookAndFeelRButtonGTK.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lookAndFeelRButtonGTKMouseClicked(evt);
            }
        });

        lookAndFeelGroup.add(jRadioButtonWindows);
        jRadioButtonWindows.setFont(new java.awt.Font("STHeiti", 0, 8));
        jRadioButtonWindows.setSelected(true);
        jRadioButtonWindows.setText("Nimbus");
        jRadioButtonWindows.setToolTipText("Set Look & Feel");
        jRadioButtonWindows.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jRadioButtonWindowsMouseClicked(evt);
            }
        });

        lookAndFeelGroup.add(lookAndFeelRButtonNimbus);
        lookAndFeelRButtonNimbus.setFont(new java.awt.Font("STHeiti", 0, 8));
        lookAndFeelRButtonNimbus.setText("Windows");
        lookAndFeelRButtonNimbus.setToolTipText("Set Look & Feel");
        lookAndFeelRButtonNimbus.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lookAndFeelRButtonNimbusMouseClicked(evt);
            }
        });

        lookAndFeelGroup.add(lookAndFeelRButtonMotif);
        lookAndFeelRButtonMotif.setFont(new java.awt.Font("STHeiti", 0, 8));
        lookAndFeelRButtonMotif.setText("Motif");
        lookAndFeelRButtonMotif.setToolTipText("Set Look & Feel");
        lookAndFeelRButtonMotif.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lookAndFeelRButtonMotifMouseClicked(evt);
            }
        });

        org.jdesktop.layout.GroupLayout lookAndFeelPanelLayout = new org.jdesktop.layout.GroupLayout(lookAndFeelPanel);
        lookAndFeelPanel.setLayout(lookAndFeelPanelLayout);
        lookAndFeelPanelLayout.setHorizontalGroup(
            lookAndFeelPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, lookAndFeelPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jRadioButtonWindows)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(lookAndFeelRButtonNimbus)
                .add(51, 51, 51)
                .add(lookAndFeelRButtonGTK)
                .add(18, 18, 18)
                .add(lookAndFeelRButtonMotif)
                .add(34, 34, 34))
        );
        lookAndFeelPanelLayout.setVerticalGroup(
            lookAndFeelPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(lookAndFeelPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(lookAndFeelPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jRadioButtonWindows, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lookAndFeelRButtonNimbus, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lookAndFeelRButtonMotif, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lookAndFeelRButtonGTK, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lookAndFeelPanelLayout.linkSize(new java.awt.Component[] {jRadioButtonWindows, lookAndFeelRButtonGTK, lookAndFeelRButtonMotif, lookAndFeelRButtonNimbus}, org.jdesktop.layout.GroupLayout.VERTICAL);

        javaOptionsField.setFont(new java.awt.Font("STHeiti", 0, 10));
        javaOptionsField.setToolTipText("Java Options: Advise: Windows: [-client -Xss16k] Mac: [-client -d32 -Xss2048]");
        javaOptionsField.setFocusCycleRoot(true);
        javaOptionsField.setFocusTraversalKeysEnabled(false);
        javaOptionsField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                javaOptionsFieldKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                javaOptionsFieldKeyReleased(evt);
            }
        });

        javaOptionsLabel.setFont(new java.awt.Font("STHeiti", 0, 8));
        javaOptionsLabel.setText("Java Options");

        org.jdesktop.layout.GroupLayout toolsInnerPanelLayout = new org.jdesktop.layout.GroupLayout(toolsInnerPanel);
        toolsInnerPanel.setLayout(toolsInnerPanelLayout);
        toolsInnerPanelLayout.setHorizontalGroup(
            toolsInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(toolsInnerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(toolsInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(toolsInnerPanelLayout.createSequentialGroup()
                        .add(netManagerOutboundClientToggleButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 164, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(netManagerInboundClientToggleButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 152, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(lookAndFeelPanel, 0, 334, Short.MAX_VALUE)
                    .add(controlsPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(toolsInnerPanelLayout.createSequentialGroup()
                        .add(6, 6, 6)
                        .add(toolsSeparator, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 317, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(toolsInnerPanelLayout.createSequentialGroup()
                        .add(6, 6, 6)
                        .add(javaOptionsLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(javaOptionsField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE)))
                .addContainerGap())
        );

        toolsInnerPanelLayout.linkSize(new java.awt.Component[] {netManagerInboundClientToggleButton, netManagerOutboundClientToggleButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        toolsInnerPanelLayout.setVerticalGroup(
            toolsInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(toolsInnerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(toolsInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(netManagerOutboundClientToggleButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 38, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(netManagerInboundClientToggleButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 35, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(toolsSeparator, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(controlsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(lookAndFeelPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(toolsInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(javaOptionsField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(javaOptionsLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 11, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        toolsInnerPanelLayout.linkSize(new java.awt.Component[] {netManagerInboundClientToggleButton, netManagerOutboundClientToggleButton}, org.jdesktop.layout.GroupLayout.VERTICAL);

        systemPropertiesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "System Properties", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("STHeiti", 0, 14))); // NOI18N

        sysPropsScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sysPropsScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        sysPropsScrollPane.setFont(new java.awt.Font("STHeiti", 0, 13));

        sysPropsTable.setFont(new java.awt.Font("STHeiti", 0, 12));
        sysPropsTable.setForeground(new java.awt.Color(102, 102, 102));
        sysPropsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"", ""},
                {"", ""},
                {"", ""},
                {"", ""},
                {"", ""},
                {"", ""},
                {"", ""},
                {"", ""},
                {"", ""},
                {"", ""},
                {" ", null},
                {" ", null},
                {null, null},
                {null, null},
                {null, null},
                {null, ""}
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
        sysPropsTable.setToolTipText("");
        sysPropsTable.setAutoCreateRowSorter(true);
        sysPropsTable.setAutoscrolls(false);
        sysPropsTable.setDoubleBuffered(true);
        sysPropsTable.setEditingColumn(0);
        sysPropsTable.setEditingRow(0);
        sysPropsTable.setEnabled(false);
        sysPropsTable.setFocusable(false);
        sysPropsTable.setMaximumSize(new java.awt.Dimension(55, 250));
        sysPropsTable.setMinimumSize(new java.awt.Dimension(55, 250));
        sysPropsTable.setName("name"); // NOI18N
        sysPropsTable.setOpaque(false);
        sysPropsTable.setPreferredSize(new java.awt.Dimension(55, 250));
        sysPropsTable.setRowSelectionAllowed(false);
        sysPropsTable.setSelectionBackground(new java.awt.Color(51, 102, 255));
        sysPropsTable.setShowGrid(false);
        sysPropsTable.setSize(new java.awt.Dimension(55, 250));
        sysPropsScrollPane.setViewportView(sysPropsTable);
        sysPropsTable.getColumnModel().getColumn(0).setResizable(false);
        sysPropsTable.getColumnModel().getColumn(0).setPreferredWidth(70);
        sysPropsTable.getColumnModel().getColumn(1).setResizable(false);
        sysPropsTable.getColumnModel().getColumn(1).setPreferredWidth(30);

        org.jdesktop.layout.GroupLayout systemPropertiesPanelLayout = new org.jdesktop.layout.GroupLayout(systemPropertiesPanel);
        systemPropertiesPanel.setLayout(systemPropertiesPanelLayout);
        systemPropertiesPanelLayout.setHorizontalGroup(
            systemPropertiesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(systemPropertiesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(sysPropsScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE)
                .addContainerGap())
        );
        systemPropertiesPanelLayout.setVerticalGroup(
            systemPropertiesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(systemPropertiesPanelLayout.createSequentialGroup()
                .add(sysPropsScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout toolsTabLayout = new org.jdesktop.layout.GroupLayout(toolsTab);
        toolsTab.setLayout(toolsTabLayout);
        toolsTabLayout.setHorizontalGroup(
            toolsTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(toolsTabLayout.createSequentialGroup()
                .addContainerGap()
                .add(toolsInnerPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(systemPropertiesPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(42, Short.MAX_VALUE))
        );
        toolsTabLayout.setVerticalGroup(
            toolsTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(toolsTabLayout.createSequentialGroup()
                .add(toolsTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(toolsInnerPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(systemPropertiesPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        toolsTabLayout.linkSize(new java.awt.Component[] {systemPropertiesPanel, toolsInnerPanel}, org.jdesktop.layout.GroupLayout.VERTICAL);

        tabPane.addTab("Tools", toolsTab);

        configTab.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("STHeiti", 0, 12), new java.awt.Color(255, 255, 255))); // NOI18N
        configTab.setToolTipText("");
        configTab.setFocusTraversalKeysEnabled(false);
        configTab.setFont(new java.awt.Font("STHeiti", 0, 12));
        configTab.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                configTabKeyPressed(evt);
            }
        });

        authenticationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Configuration", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("STHeiti", 0, 14))); // NOI18N
        authenticationPanel.setToolTipText("");

        clientIPLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
        clientIPLabel.setText("Client");

        clientIPField.setBackground(new java.awt.Color(204, 204, 204));
        clientIPField.setFont(new java.awt.Font("STHeiti", 0, 10));
        clientIPField.setToolTipText("Your computer's IP (Automatic)");

        pubIPLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
        pubIPLabel.setText("Pub IP");

        pubIPField.setFont(new java.awt.Font("STHeiti", 0, 10));
        pubIPField.setToolTipText("Fill in your public IP address (Tip: Double Click to see your Public IP Address in Google)");
        pubIPField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pubIPFieldMouseClicked(evt);
            }
        });

        clientPortLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
        clientPortLabel.setText("Port");

        clientPortField.setBackground(new java.awt.Color(204, 204, 204));
        clientPortField.setFont(new java.awt.Font("STHeiti", 0, 10));
        clientPortField.setToolTipText("Client Port (Tip: \"auto\")");

        domainLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
        domainLabel.setText("Domain");

        domainField.setFont(new java.awt.Font("STHeiti", 0, 10));
        domainField.setText("sip1.budgetphone.nl");
        domainField.setToolTipText("Internet Telephone Provider Domain (Tip: sip1.budgetphone.nl)");

        serverIPLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
        serverIPLabel.setText("Server");

        serverIPField.setFont(new java.awt.Font("STHeiti", 0, 10));
        serverIPField.setText("sip1.budgetphone.nl");
        serverIPField.setToolTipText("Internet Telephone Provider Server (Tip: sip1.budgetphone.nl)");

        serverPortLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
        serverPortLabel.setText("Port");

        serverPortField.setFont(new java.awt.Font("STHeiti", 0, 10));
        serverPortField.setToolTipText("Internet Telephone Provider Port (Tip: \"5060\")");

        pfixLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
        pfixLabel.setText("PFix");

        usersecretLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
        usersecretLabel.setText("User");

        suffixLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
        suffixLabel.setText("SFix");

        prefixField.setBackground(new java.awt.Color(204, 204, 204));
        prefixField.setFont(new java.awt.Font("STHeiti", 0, 10));
        prefixField.setToolTipText("Password Prefix (advanced usage normally not needed)");

        usernameField.setFont(new java.awt.Font("STHeiti", 0, 10));
        usernameField.setToolTipText("Username (comes from your Internet Telephone Provider)");

        suffixField.setBackground(new java.awt.Color(204, 204, 204));
        suffixField.setFont(new java.awt.Font("STHeiti", 0, 10));
        suffixField.setToolTipText("Password Suffix (advanced usage normally not needed)");

        saveConfigurationButton.setFont(new java.awt.Font("STHeiti", 0, 12));
        saveConfigurationButton.setText("Save");
        saveConfigurationButton.setToolTipText("Saves CallCenter Network Configuration (data/config/network3.xml)");
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

        registerLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
        registerLabel.setText("Reg");

        registerCheckBox.setFont(new java.awt.Font("STHeiti", 0, 10));
        registerCheckBox.setToolTipText("Register to PBX / Proxy (normally not used)");
        registerCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        registerCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        registerCheckBox.setIconTextGap(0);

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
                .addContainerGap()
                .add(prefPhoneLinesSlider, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE)
                .addContainerGap())
        );
        prefPhoneLinesPanelLayout.setVerticalGroup(
            prefPhoneLinesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(prefPhoneLinesPanelLayout.createSequentialGroup()
                .add(prefPhoneLinesSlider, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 38, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        iconsLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
        iconsLabel.setText("Icons");
        iconsLabel.setToolTipText("");

        iconsCheckBox.setFont(new java.awt.Font("STHeiti", 0, 10));
        iconsCheckBox.setToolTipText("Enables Icons to be displayed in CallCenter instead of Text Symbols");
        iconsCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        iconsCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        iconsCheckBox.setIconTextGap(0);

        subscribeToVOIPProviderButton.setFont(new java.awt.Font("STHeiti", 0, 12));
        subscribeToVOIPProviderButton.setText("Subscribe to BudgetPhone");
        subscribeToVOIPProviderButton.setToolTipText("Subscribe to BudgetPhone via  the Web");
        subscribeToVOIPProviderButton.setFocusPainted(false);
        subscribeToVOIPProviderButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subscribeToVOIPProviderButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout authenticationPanelLayout = new org.jdesktop.layout.GroupLayout(authenticationPanel);
        authenticationPanel.setLayout(authenticationPanelLayout);
        authenticationPanelLayout.setHorizontalGroup(
            authenticationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, authenticationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(authenticationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(authenticationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                        .add(domainLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(clientIPLabel))
                    .add(serverIPLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(authenticationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(authenticationPanelLayout.createSequentialGroup()
                        .add(authenticationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(domainField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 271, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, authenticationPanelLayout.createSequentialGroup()
                                .add(clientIPField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(pubIPLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(pubIPField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 97, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(serverIPField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 271, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(authenticationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(clientPortLabel)
                            .add(serverPortLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(authenticationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(clientPortField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 45, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(serverPortField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 46, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(authenticationPanelLayout.createSequentialGroup()
                        .add(authenticationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(authenticationPanelLayout.createSequentialGroup()
                                .add(pfixLabel)
                                .add(27, 27, 27))
                            .add(authenticationPanelLayout.createSequentialGroup()
                                .add(prefixField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 35, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)))
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
                                .add(suffixLabel))
                            .add(authenticationPanelLayout.createSequentialGroup()
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(suffixField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 46, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(authenticationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(authenticationPanelLayout.createSequentialGroup()
                                .add(registerCheckBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(18, 18, 18))
                            .add(authenticationPanelLayout.createSequentialGroup()
                                .add(registerLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)))
                        .add(authenticationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(iconsLabel)
                            .add(iconsCheckBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(78, Short.MAX_VALUE))))
            .add(prefPhoneLinesPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(authenticationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(saveConfigurationButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 124, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(subscribeToVOIPProviderButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 256, Short.MAX_VALUE)
                .addContainerGap())
        );

        authenticationPanelLayout.linkSize(new java.awt.Component[] {clientIPLabel, domainLabel, serverIPLabel}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        authenticationPanelLayout.setVerticalGroup(
            authenticationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(authenticationPanelLayout.createSequentialGroup()
                .add(authenticationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(authenticationPanelLayout.createSequentialGroup()
                        .add(authenticationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(clientIPLabel)
                            .add(clientIPField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(authenticationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(domainLabel)
                            .add(domainField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(authenticationPanelLayout.createSequentialGroup()
                        .add(authenticationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(clientPortLabel)
                            .add(clientPortField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(pubIPField)
                            .add(pubIPLabel))
                        .add(35, 35, 35)
                        .add(authenticationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(serverPortField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(serverPortLabel)
                            .add(serverIPField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(serverIPLabel))))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(authenticationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, registerCheckBox)
                    .add(authenticationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(pfixLabel)
                        .add(usersecretLabel)
                        .add(secretLabel))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, authenticationPanelLayout.createSequentialGroup()
                        .add(suffixLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(authenticationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(suffixField)
                            .add(authenticationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                .add(usernameField)
                                .add(prefixField))
                            .add(toegangField)))
                    .add(authenticationPanelLayout.createSequentialGroup()
                        .add(authenticationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(iconsLabel)
                            .add(registerLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(iconsCheckBox)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(prefPhoneLinesPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(authenticationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(saveConfigurationButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(subscribeToVOIPProviderButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        authenticationPanelLayout.linkSize(new java.awt.Component[] {clientIPField, clientPortField, domainField, prefixField, pubIPField, serverIPField, serverPortField, suffixField, toegangField, usernameField}, org.jdesktop.layout.GroupLayout.VERTICAL);

        authenticationPanelLayout.linkSize(new java.awt.Component[] {clientIPLabel, domainLabel, serverIPLabel}, org.jdesktop.layout.GroupLayout.VERTICAL);

        org.jdesktop.layout.GroupLayout configTabLayout = new org.jdesktop.layout.GroupLayout(configTab);
        configTab.setLayout(configTabLayout);
        configTabLayout.setHorizontalGroup(
            configTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(configTabLayout.createSequentialGroup()
                .add(130, 130, 130)
                .add(authenticationPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(146, Short.MAX_VALUE))
        );
        configTabLayout.setVerticalGroup(
            configTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(configTabLayout.createSequentialGroup()
                .add(authenticationPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(41, Short.MAX_VALUE))
        );

        tabPane.addTab("Config", configTab);

        updateTab.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("STHeiti", 0, 13), new java.awt.Color(255, 255, 255))); // NOI18N

        maintenancePanel1.setBackground(new java.awt.Color(51, 51, 51));
        maintenancePanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("STHeiti", 0, 10), new java.awt.Color(255, 255, 255))); // NOI18N

        headerLabel1.setFont(new java.awt.Font("STHeiti", 0, 24));
        headerLabel1.setForeground(new java.awt.Color(255, 255, 255));
        headerLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        headerLabel1.setText("Version Update Manager");
        headerLabel1.setToolTipText("");

        applicationLabel.setFont(new java.awt.Font("STHeiti", 0, 14));
        applicationLabel.setForeground(new java.awt.Color(255, 255, 255));
        applicationLabel.setText("Application");
        applicationLabel.setToolTipText("");

        versionLabel.setFont(new java.awt.Font("STHeiti", 0, 14));
        versionLabel.setForeground(new java.awt.Color(255, 255, 255));
        versionLabel.setText("Version");

        managerLabel.setFont(new java.awt.Font("STHeiti", 0, 14));
        managerLabel.setForeground(new java.awt.Color(51, 255, 51));
        managerLabel.setText("Manager:");
        managerLabel.setToolTipText("The Manager (this application) Automated control of Campaigns and CallCenters");

        customerTableLabel10.setFont(new java.awt.Font("STHeiti", 0, 14));
        customerTableLabel10.setForeground(new java.awt.Color(51, 255, 51));
        customerTableLabel10.setText("ECallCenter:");
        customerTableLabel10.setToolTipText("ECallCenter21 actually runs Campaigns handling all automated Phonecalls");

        ephoneLabel.setFont(new java.awt.Font("STHeiti", 0, 14));
        ephoneLabel.setForeground(new java.awt.Color(51, 255, 51));
        ephoneLabel.setText("EPhone");
        ephoneLabel.setToolTipText("EPhone is the UserInterface of the VoipStorm buildingblock: The SoftPhone!");

        managerVersionLabel.setFont(new java.awt.Font("STHeiti", 0, 14));
        managerVersionLabel.setForeground(new java.awt.Color(204, 204, 204));
        managerVersionLabel.setText("0");
        managerVersionLabel.setToolTipText("The Manager (this application) Automated control of Campaigns and CallCenters");

        callcenterVersionLabel.setFont(new java.awt.Font("STHeiti", 0, 14));
        callcenterVersionLabel.setForeground(new java.awt.Color(204, 204, 204));
        callcenterVersionLabel.setText("0");
        callcenterVersionLabel.setToolTipText("ECallCenter21 actually runs Campaigns handling all automated Phonecalls");

        ephoneVersionLabel.setFont(new java.awt.Font("STHeiti", 0, 14));
        ephoneVersionLabel.setForeground(new java.awt.Color(204, 204, 204));
        ephoneVersionLabel.setText("0");
        ephoneVersionLabel.setToolTipText("EPhone is the UserInterface of the VoipStorm buildingblock: The SoftPhone!");

        checkVersionButton.setFont(new java.awt.Font("STHeiti", 0, 14));
        checkVersionButton.setText("Check Version");
        checkVersionButton.setToolTipText("Check the Internet for new Updates");
        checkVersionButton.setFocusTraversalKeysEnabled(false);
        checkVersionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkVersionButtonActionPerformed(evt);
            }
        });
        checkVersionButton.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                checkVersionButtonKeyPressed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout maintenancePanel1Layout = new org.jdesktop.layout.GroupLayout(maintenancePanel1);
        maintenancePanel1.setLayout(maintenancePanel1Layout);
        maintenancePanel1Layout.setHorizontalGroup(
            maintenancePanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(maintenancePanel1Layout.createSequentialGroup()
                .add(maintenancePanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(maintenancePanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .add(maintenancePanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(headerLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 356, Short.MAX_VALUE)
                            .add(maintenancePanel1Layout.createSequentialGroup()
                                .add(maintenancePanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(applicationLabel)
                                    .add(customerTableLabel10)
                                    .add(ephoneLabel)
                                    .add(managerLabel))
                                .add(221, 221, 221)
                                .add(maintenancePanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(managerVersionLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 49, Short.MAX_VALUE)
                                    .add(versionLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .add(callcenterVersionLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 49, Short.MAX_VALUE)
                                    .add(ephoneVersionLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 49, Short.MAX_VALUE)))))
                    .add(maintenancePanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .add(updateVersonSeparator, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 356, Short.MAX_VALUE))
                    .add(maintenancePanel1Layout.createSequentialGroup()
                        .add(113, 113, 113)
                        .add(checkVersionButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 142, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        maintenancePanel1Layout.setVerticalGroup(
            maintenancePanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(maintenancePanel1Layout.createSequentialGroup()
                .add(headerLabel1)
                .add(21, 21, 21)
                .add(maintenancePanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(maintenancePanel1Layout.createSequentialGroup()
                        .add(applicationLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(managerLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(customerTableLabel10)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(ephoneLabel))
                    .add(maintenancePanel1Layout.createSequentialGroup()
                        .add(versionLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(managerVersionLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(callcenterVersionLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(ephoneVersionLabel)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(updateVersonSeparator, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(checkVersionButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 39, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        maintenancePanel1Layout.linkSize(new java.awt.Component[] {ephoneVersionLabel, versionLabel}, org.jdesktop.layout.GroupLayout.VERTICAL);

        org.jdesktop.layout.GroupLayout updateTabLayout = new org.jdesktop.layout.GroupLayout(updateTab);
        updateTab.setLayout(updateTabLayout);
        updateTabLayout.setHorizontalGroup(
            updateTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(updateTabLayout.createSequentialGroup()
                .add(150, 150, 150)
                .add(maintenancePanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(156, Short.MAX_VALUE))
        );
        updateTabLayout.setVerticalGroup(
            updateTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(updateTabLayout.createSequentialGroup()
                .add(38, 38, 38)
                .add(maintenancePanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(68, Short.MAX_VALUE))
        );

        tabPane.addTab("Update", updateTab);

        licenseTab.setName("licenseTab"); // NOI18N
        licenseTab.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                licenseTabMouseClicked(evt);
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
        jScrollPane2.setViewportView(vergunningTypeList);

        org.jdesktop.layout.GroupLayout licenseTypePanelLayout = new org.jdesktop.layout.GroupLayout(licenseTypePanel);
        licenseTypePanel.setLayout(licenseTypePanelLayout);
        licenseTypePanelLayout.setHorizontalGroup(
            licenseTypePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE)
        );
        licenseTypePanelLayout.setVerticalGroup(
            licenseTypePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(licenseTypePanelLayout.createSequentialGroup()
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 157, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
            .addContainerGap()
            .add(vergunningDateChooserPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 251, Short.MAX_VALUE)
            .addContainerGap())
    );
    licenseDatePanelLayout.setVerticalGroup(
        licenseDatePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(licenseDatePanelLayout.createSequentialGroup()
            .add(vergunningDateChooserPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 155, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addContainerGap(8, Short.MAX_VALUE))
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
            .addContainerGap(7, Short.MAX_VALUE))
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
            .add(activationCodeField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 492, Short.MAX_VALUE)
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
            .add(vergunningCodeField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 492, Short.MAX_VALUE)
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

    org.jdesktop.layout.GroupLayout licenseDetailsPanelLayout = new org.jdesktop.layout.GroupLayout(licenseDetailsPanel);
    licenseDetailsPanel.setLayout(licenseDetailsPanelLayout);
    licenseDetailsPanelLayout.setHorizontalGroup(
        licenseDetailsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(licenseDetailsScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
    );
    licenseDetailsPanelLayout.setVerticalGroup(
        licenseDetailsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(licenseDetailsPanelLayout.createSequentialGroup()
            .add(licenseDetailsScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 156, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addContainerGap(7, Short.MAX_VALUE))
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
    requestVergunningButton.setToolTipText("Request a License for this computer");
    requestVergunningButton.setEnabled(false);
    requestVergunningButton.setFocusPainted(false);
    requestVergunningButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            requestVergunningButtonActionPerformed(evt);
        }
    });

    org.jdesktop.layout.GroupLayout licenseTabLayout = new org.jdesktop.layout.GroupLayout(licenseTab);
    licenseTab.setLayout(licenseTabLayout);
    licenseTabLayout.setHorizontalGroup(
        licenseTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(licenseTabLayout.createSequentialGroup()
            .addContainerGap()
            .add(licenseTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(licenseTabLayout.createSequentialGroup()
                    .add(licenseTypePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 128, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(licenseDatePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(licensePeriodPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 95, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(6, 6, 6))
                .add(org.jdesktop.layout.GroupLayout.TRAILING, licenseTabLayout.createSequentialGroup()
                    .add(licenseTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, licenseCodePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(activationCodePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))
            .add(licenseTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(applyVergunningButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                .add(requestVergunningButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                .add(licenseDetailsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 170, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .addContainerGap())
    );
    licenseTabLayout.setVerticalGroup(
        licenseTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(licenseTabLayout.createSequentialGroup()
            .add(licenseTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(licenseDetailsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 186, Short.MAX_VALUE)
                .add(licenseDatePanel, 0, 186, Short.MAX_VALUE)
                .add(licenseTypePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 186, Short.MAX_VALUE)
                .add(licensePeriodPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 186, Short.MAX_VALUE))
            .add(5, 5, 5)
            .add(licenseTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(requestVergunningButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
                .add(activationCodePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(licenseTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                .add(applyVergunningButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
                .add(licenseCodePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, Short.MAX_VALUE))
            .addContainerGap())
    );

    tabPane.addTab("License", licenseTab);

    logTab.setToolTipText("Clicking clears Display");
    logTab.setFocusTraversalKeysEnabled(false);
    logTab.setFont(new java.awt.Font("STHeiti", 0, 12));
    logTab.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyPressed(java.awt.event.KeyEvent evt) {
            logTabKeyPressed(evt);
        }
    });

    jScrollPane1.setForeground(new java.awt.Color(255, 255, 255));
    jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

    textLogArea.setBackground(new java.awt.Color(51, 51, 51));
    textLogArea.setColumns(20);
    textLogArea.setEditable(false);
    textLogArea.setFont(new java.awt.Font("Courier", 0, 8));
    textLogArea.setForeground(new java.awt.Color(255, 255, 255));
    textLogArea.setLineWrap(true);
    textLogArea.setRows(5);
    textLogArea.setToolTipText("Doubleclick to Clear");
    textLogArea.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            textLogAreaMouseClicked(evt);
        }
    });
    jScrollPane1.setViewportView(textLogArea);

    org.jdesktop.layout.GroupLayout logTabLayout = new org.jdesktop.layout.GroupLayout(logTab);
    logTab.setLayout(logTabLayout);
    logTabLayout.setHorizontalGroup(
        logTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 698, Short.MAX_VALUE)
    );
    logTabLayout.setVerticalGroup(
        logTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(org.jdesktop.layout.GroupLayout.TRAILING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 317, Short.MAX_VALUE)
    );

    tabPane.addTab("Log", logTab);

    backofficeTab.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
    backofficeTab.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
    backofficeTab.setToolTipText("");
    backofficeTab.setFont(new java.awt.Font("STHeiti", 0, 13));
    backofficeTab.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            backofficeTabMouseClicked(evt);
        }
    });

    invoicePanel.setToolTipText("");
    invoicePanel.setFocusTraversalKeysEnabled(false);
    invoicePanel.setFont(new java.awt.Font("STHeiti", 0, 13));
    invoicePanel.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyPressed(java.awt.event.KeyEvent evt) {
            invoicePanelKeyPressed(evt);
        }
    });

    selectInvoiceButton.setFont(new java.awt.Font("STHeiti", 0, 10));
    selectInvoiceButton.setText("Select");
    selectInvoiceButton.setToolTipText("Select Invoice");
    selectInvoiceButton.setFocusable(false);
    selectInvoiceButton.setMaximumSize(new java.awt.Dimension(75, 21));
    selectInvoiceButton.setMinimumSize(new java.awt.Dimension(75, 21));
    selectInvoiceButton.setPreferredSize(new java.awt.Dimension(75, 21));
    selectInvoiceButton.setSize(new java.awt.Dimension(75, 21));
    selectInvoiceButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            selectInvoiceButtonActionPerformed(evt);
        }
    });

    insertInvoiceButton.setFont(new java.awt.Font("STHeiti", 0, 10));
    insertInvoiceButton.setText("Insert");
    insertInvoiceButton.setToolTipText("Insert Invoice (automatically done in Order Menu)");
    insertInvoiceButton.setFocusable(false);
    insertInvoiceButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            insertInvoiceButtonActionPerformed(evt);
        }
    });

    updateInvoiceButton.setFont(new java.awt.Font("STHeiti", 0, 10));
    updateInvoiceButton.setText("Update");
    updateInvoiceButton.setToolTipText("Update Invoice");
    updateInvoiceButton.setFocusable(false);
    updateInvoiceButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            updateInvoiceButtonActionPerformed(evt);
        }
    });

    deleteInvoiceButton.setFont(new java.awt.Font("STHeiti", 0, 10));
    deleteInvoiceButton.setText("Delete");
    deleteInvoiceButton.setToolTipText("Delete Invoice");
    deleteInvoiceButton.setFocusable(false);
    deleteInvoiceButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            deleteInvoiceButtonActionPerformed(evt);
        }
    });

    invoiceInnerPanel.setBackground(new java.awt.Color(204, 204, 204));
    invoiceInnerPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("STHeiti", 0, 14))); // NOI18N
    invoiceInnerPanel.setToolTipText("");
    invoiceInnerPanel.setFont(new java.awt.Font("STHeiti", 0, 13));
    invoiceInnerPanel.setMaximumSize(new java.awt.Dimension(526, 192));

    invoiceIdLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
    invoiceIdLabel.setText("Id");

    invoiceDateLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
    invoiceDateLabel.setText("Date");

    orderIdLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
    orderIdLabel.setText("Oid");

    invoiceQTYLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
    invoiceQTYLabel.setText("QTY");

    invoiceItemUnitPriceLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
    invoiceItemUnitPriceLabel.setText("UPrice");

    invoiceItemVATPercentageLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
    invoiceItemVATPercentageLabel.setText("VAT%");

    invoiceItemQTYPriceLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
    invoiceItemQTYPriceLabel.setText("Price");

    invoiceIdField.setFont(new java.awt.Font("STHeiti", 0, 10));
    invoiceIdField.setText("0");
    invoiceIdField.setToolTipText("Invoice Id");
    invoiceIdField.setFocusTraversalKeysEnabled(false);

    invoiceDateField.setFont(new java.awt.Font("STHeiti", 0, 10));
    invoiceDateField.setText("0");
    invoiceDateField.setToolTipText("Date in Epoch Format");
    invoiceDateField.setFocusTraversalKeysEnabled(false);

    invoiceOrderIdField.setFont(new java.awt.Font("STHeiti", 0, 10));
    invoiceOrderIdField.setText("0");
    invoiceOrderIdField.setToolTipText("OrderId");
    invoiceOrderIdField.setFocusTraversalKeysEnabled(false);

    invoiceQuantityField.setFont(new java.awt.Font("STHeiti", 0, 10));
    invoiceQuantityField.setText("0");
    invoiceQuantityField.setToolTipText("Quantity of Calls");
    invoiceQuantityField.setFocusTraversalKeysEnabled(false);

    invoiceItemDescField.setFont(new java.awt.Font("STHeiti", 0, 10));
    invoiceItemDescField.setToolTipText("Invoice Description");
    invoiceItemDescField.setFocusTraversalKeysEnabled(false);

    invoiceItemUnitPriceField.setFont(new java.awt.Font("STHeiti", 0, 10));
    invoiceItemUnitPriceField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    invoiceItemUnitPriceField.setText("0");
    invoiceItemUnitPriceField.setToolTipText("Price per VoiceMessage / Call");
    invoiceItemUnitPriceField.setFocusTraversalKeysEnabled(false);

    invoiceItemVATPercentageField.setFont(new java.awt.Font("STHeiti", 0, 10));
    invoiceItemVATPercentageField.setText("19");
    invoiceItemVATPercentageField.setToolTipText("VAT Percentage");
    invoiceItemVATPercentageField.setFocusTraversalKeysEnabled(false);

    invoiceItemQuantityPriceField.setFont(new java.awt.Font("STHeiti", 0, 10));
    invoiceItemQuantityPriceField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    invoiceItemQuantityPriceField.setText("0");
    invoiceItemQuantityPriceField.setToolTipText("SubTotal");
    invoiceItemQuantityPriceField.setFocusTraversalKeysEnabled(false);

    invoiceTotalsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("STHeiti", 0, 13))); // NOI18N
    invoiceTotalsPanel.setToolTipText("");

    invoiceSubTotalB4DiscountLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
    invoiceSubTotalB4DiscountLabel.setForeground(new java.awt.Color(51, 51, 51));
    invoiceSubTotalB4DiscountLabel.setText("Subtotal   €");

    invoiceCustomerDiscountLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
    invoiceCustomerDiscountLabel.setForeground(new java.awt.Color(51, 51, 51));
    invoiceCustomerDiscountLabel.setText("Discount");

    invoiceSubTotalLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
    invoiceSubTotalLabel.setForeground(new java.awt.Color(51, 51, 51));
    invoiceSubTotalLabel.setText("SubTotal  €");

    invoiceVATLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
    invoiceVATLabel.setForeground(new java.awt.Color(51, 51, 51));
    invoiceVATLabel.setText("VAT/BTW €");

    invoiceTotalLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
    invoiceTotalLabel.setForeground(new java.awt.Color(51, 51, 51));
    invoiceTotalLabel.setText("Total       €");

    invoicePaidLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
    invoicePaidLabel.setForeground(new java.awt.Color(51, 51, 51));
    invoicePaidLabel.setText("Paid       €");

    invoiceSubTotalB4DiscountField.setFont(new java.awt.Font("STHeiti", 0, 10));
    invoiceSubTotalB4DiscountField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    invoiceSubTotalB4DiscountField.setText("0");
    invoiceSubTotalB4DiscountField.setFocusTraversalKeysEnabled(false);

    invoiceCustomerDiscountField.setFont(new java.awt.Font("STHeiti", 0, 10));
    invoiceCustomerDiscountField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    invoiceCustomerDiscountField.setText("0");
    invoiceCustomerDiscountField.setToolTipText("Customer Discount Percentage");
    invoiceCustomerDiscountField.setFocusTraversalKeysEnabled(false);

    invoiceSubTotalField.setFont(new java.awt.Font("STHeiti", 0, 10));
    invoiceSubTotalField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    invoiceSubTotalField.setText("0");
    invoiceSubTotalField.setToolTipText("SubTotal");
    invoiceSubTotalField.setFocusTraversalKeysEnabled(false);

    invoiceVATField.setFont(new java.awt.Font("STHeiti", 0, 10));
    invoiceVATField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    invoiceVATField.setText("0");
    invoiceVATField.setToolTipText("VAT");
    invoiceVATField.setFocusTraversalKeysEnabled(false);

    invoiceTotalField.setFont(new java.awt.Font("STHeiti", 0, 10));
    invoiceTotalField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    invoiceTotalField.setText("0");
    invoiceTotalField.setToolTipText("Total incl. VAT");
    invoiceTotalField.setFocusTraversalKeysEnabled(false);

    invoicePaidField.setFont(new java.awt.Font("STHeiti", 0, 10));
    invoicePaidField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    invoicePaidField.setText("0");
    invoicePaidField.setToolTipText("Customer Paid");
    invoicePaidField.setFocusTraversalKeysEnabled(false);

    org.jdesktop.layout.GroupLayout invoiceTotalsPanelLayout = new org.jdesktop.layout.GroupLayout(invoiceTotalsPanel);
    invoiceTotalsPanel.setLayout(invoiceTotalsPanelLayout);
    invoiceTotalsPanelLayout.setHorizontalGroup(
        invoiceTotalsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(invoiceTotalsPanelLayout.createSequentialGroup()
            .addContainerGap()
            .add(invoiceTotalsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(invoiceSubTotalLabel)
                .add(invoiceSubTotalB4DiscountLabel)
                .add(invoiceVATLabel)
                .add(invoiceCustomerDiscountLabel)
                .add(invoiceTotalLabel)
                .add(invoicePaidLabel))
            .add(3, 3, 3)
            .add(invoiceTotalsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(org.jdesktop.layout.GroupLayout.TRAILING, invoiceTotalField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                .add(org.jdesktop.layout.GroupLayout.TRAILING, invoiceSubTotalB4DiscountField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                .add(org.jdesktop.layout.GroupLayout.TRAILING, invoiceCustomerDiscountField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                .add(org.jdesktop.layout.GroupLayout.TRAILING, invoiceSubTotalField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                .add(org.jdesktop.layout.GroupLayout.TRAILING, invoiceVATField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                .add(org.jdesktop.layout.GroupLayout.TRAILING, invoicePaidField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE))
            .addContainerGap())
    );

    invoiceTotalsPanelLayout.linkSize(new java.awt.Component[] {invoiceCustomerDiscountField, invoicePaidField, invoiceSubTotalB4DiscountField, invoiceSubTotalField, invoiceTotalField, invoiceVATField}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

    invoiceTotalsPanelLayout.setVerticalGroup(
        invoiceTotalsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(invoiceTotalsPanelLayout.createSequentialGroup()
            .add(invoiceTotalsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(invoiceSubTotalB4DiscountField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(invoiceSubTotalB4DiscountLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 19, Short.MAX_VALUE))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(invoiceTotalsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(invoiceCustomerDiscountField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(invoiceCustomerDiscountLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 13, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(invoiceTotalsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(invoiceSubTotalField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(invoiceSubTotalLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(invoiceTotalsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(invoiceVATField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(invoiceVATLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(18, 18, 18)
            .add(invoiceTotalsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(invoiceTotalField)
                .add(invoiceTotalLabel))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(invoiceTotalsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(invoicePaidField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(invoicePaidLabel)))
    );

    invoiceTotalsPanelLayout.linkSize(new java.awt.Component[] {invoiceCustomerDiscountField, invoicePaidField, invoiceSubTotalB4DiscountField, invoiceSubTotalField, invoiceTotalField, invoiceVATField}, org.jdesktop.layout.GroupLayout.VERTICAL);

    jLabel1.setFont(new java.awt.Font("STHeiti", 1, 24));
    jLabel1.setText("INVOICE");

    org.jdesktop.layout.GroupLayout invoiceInnerPanelLayout = new org.jdesktop.layout.GroupLayout(invoiceInnerPanel);
    invoiceInnerPanel.setLayout(invoiceInnerPanelLayout);
    invoiceInnerPanelLayout.setHorizontalGroup(
        invoiceInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(org.jdesktop.layout.GroupLayout.TRAILING, invoiceInnerPanelLayout.createSequentialGroup()
            .addContainerGap()
            .add(invoiceInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(org.jdesktop.layout.GroupLayout.TRAILING, invoiceInnerPanelLayout.createSequentialGroup()
                    .add(jLabel1)
                    .add(114, 114, 114))
                .add(invoiceItemDescField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE)
                .add(org.jdesktop.layout.GroupLayout.TRAILING, invoiceInnerPanelLayout.createSequentialGroup()
                    .add(invoiceInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(invoiceInnerPanelLayout.createSequentialGroup()
                            .add(6, 6, 6)
                            .add(invoiceInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(invoiceIdLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
                                .add(orderIdLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)))
                        .add(invoiceOrderIdField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 76, Short.MAX_VALUE))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(invoiceInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(invoiceQuantityField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 62, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(invoiceInnerPanelLayout.createSequentialGroup()
                            .add(6, 6, 6)
                            .add(invoiceQTYLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE))
                        .add(invoiceIdField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 57, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(invoiceInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(invoiceInnerPanelLayout.createSequentialGroup()
                            .add(invoiceItemUnitPriceField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 46, Short.MAX_VALUE)
                            .add(3, 3, 3))
                        .add(invoiceItemUnitPriceLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 49, Short.MAX_VALUE))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(invoiceInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                        .add(invoiceInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(invoiceItemVATPercentageField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 37, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(invoiceItemVATPercentageLabel))
                        .add(invoiceDateLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(invoiceInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, invoiceDateField)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, invoiceItemQuantityPriceField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE)
                        .add(invoiceItemQTYPriceLabel))))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(invoiceTotalsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addContainerGap())
    );

    invoiceInnerPanelLayout.linkSize(new java.awt.Component[] {invoiceIdField, invoiceQuantityField}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

    invoiceInnerPanelLayout.setVerticalGroup(
        invoiceInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(org.jdesktop.layout.GroupLayout.TRAILING, invoiceInnerPanelLayout.createSequentialGroup()
            .addContainerGap()
            .add(jLabel1)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 54, Short.MAX_VALUE)
            .add(invoiceInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                .add(invoiceInnerPanelLayout.createSequentialGroup()
                    .add(invoiceItemUnitPriceLabel)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(invoiceItemUnitPriceField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(invoiceInnerPanelLayout.createSequentialGroup()
                    .add(invoiceInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(invoiceIdLabel)
                        .add(invoiceIdField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(invoiceInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(orderIdLabel)
                        .add(invoiceQTYLabel))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(invoiceInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(invoiceOrderIdField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(invoiceQuantityField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(invoiceInnerPanelLayout.createSequentialGroup()
                    .add(invoiceInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(invoiceDateField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(invoiceDateLabel))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(invoiceInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, invoiceInnerPanelLayout.createSequentialGroup()
                            .add(invoiceInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                .add(invoiceItemQTYPriceLabel)
                                .add(invoiceItemVATPercentageLabel))
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(invoiceItemQuantityPriceField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, invoiceItemVATPercentageField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(invoiceItemDescField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(15, 15, 15))
        .add(invoiceInnerPanelLayout.createSequentialGroup()
            .add(invoiceTotalsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    invoiceInnerPanelLayout.linkSize(new java.awt.Component[] {invoiceDateField, invoiceIdField, invoiceItemDescField, invoiceItemQuantityPriceField, invoiceItemUnitPriceField, invoiceItemVATPercentageField, invoiceOrderIdField, invoiceQuantityField}, org.jdesktop.layout.GroupLayout.VERTICAL);

    previousInvoiceButton.setFont(new java.awt.Font("STHeiti", 0, 10));
    previousInvoiceButton.setText("<");
    previousInvoiceButton.setToolTipText("Previous");
    previousInvoiceButton.setFocusable(false);
    previousInvoiceButton.setMaximumSize(new java.awt.Dimension(80, 29));
    previousInvoiceButton.setMinimumSize(new java.awt.Dimension(80, 29));
    previousInvoiceButton.setPreferredSize(new java.awt.Dimension(80, 29));
    previousInvoiceButton.setSize(new java.awt.Dimension(80, 29));
    previousInvoiceButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            previousInvoiceButtonActionPerformed(evt);
        }
    });

    nextInvoiceButton.setFont(new java.awt.Font("STHeiti", 0, 10));
    nextInvoiceButton.setText(">");
    nextInvoiceButton.setToolTipText("Previous");
    nextInvoiceButton.setFocusable(false);
    nextInvoiceButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            nextInvoiceButtonActionPerformed(evt);
        }
    });

    org.jdesktop.layout.GroupLayout invoicePanelLayout = new org.jdesktop.layout.GroupLayout(invoicePanel);
    invoicePanel.setLayout(invoicePanelLayout);
    invoicePanelLayout.setHorizontalGroup(
        invoicePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(org.jdesktop.layout.GroupLayout.TRAILING, invoicePanelLayout.createSequentialGroup()
            .addContainerGap()
            .add(invoicePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(org.jdesktop.layout.GroupLayout.TRAILING, invoicePanelLayout.createSequentialGroup()
                    .add(previousInvoiceButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 40, Short.MAX_VALUE)
                    .add(2, 2, 2)
                    .add(nextInvoiceButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 34, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(selectInvoiceButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 82, Short.MAX_VALUE)
                .add(insertInvoiceButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 82, Short.MAX_VALUE)
                .add(updateInvoiceButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(deleteInvoiceButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 82, Short.MAX_VALUE))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(invoiceInnerPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(37, 37, 37))
    );

    invoicePanelLayout.linkSize(new java.awt.Component[] {deleteInvoiceButton, insertInvoiceButton, selectInvoiceButton, updateInvoiceButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

    invoicePanelLayout.linkSize(new java.awt.Component[] {nextInvoiceButton, previousInvoiceButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

    invoicePanelLayout.setVerticalGroup(
        invoicePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(invoicePanelLayout.createSequentialGroup()
            .add(invoicePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(invoicePanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .add(invoicePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(previousInvoiceButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(nextInvoiceButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(selectInvoiceButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(insertInvoiceButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(updateInvoiceButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(deleteInvoiceButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(invoiceInnerPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .addContainerGap(52, Short.MAX_VALUE))
    );

    invoicePanelLayout.linkSize(new java.awt.Component[] {deleteInvoiceButton, insertInvoiceButton, selectInvoiceButton, updateInvoiceButton}, org.jdesktop.layout.GroupLayout.VERTICAL);

    backofficeTab.addTab("Invoice", invoicePanel);

    campaignPanel.setToolTipText("");
    campaignPanel.setFocusTraversalKeysEnabled(false);
    campaignPanel.setFont(new java.awt.Font("STHeiti", 0, 13));
    campaignPanel.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyPressed(java.awt.event.KeyEvent evt) {
            campaignPanelKeyPressed(evt);
        }
    });

    selectCampaignButton.setFont(new java.awt.Font("STHeiti", 0, 10));
    selectCampaignButton.setText("Select");
    selectCampaignButton.setToolTipText("Select Campaign");
    selectCampaignButton.setFocusable(false);
    selectCampaignButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            selectCampaignButtonActionPerformed(evt);
        }
    });

    insertCampaignButton.setFont(new java.awt.Font("STHeiti", 0, 10));
    insertCampaignButton.setText("Insert");
    insertCampaignButton.setToolTipText("Insert Campaign (automatically done in Order Menu)");
    insertCampaignButton.setFocusable(false);
    insertCampaignButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            insertCampaignButtonActionPerformed(evt);
        }
    });

    updateCampaignButton.setFont(new java.awt.Font("STHeiti", 0, 10));
    updateCampaignButton.setText("Update");
    updateCampaignButton.setToolTipText("Update Campaign");
    updateCampaignButton.setFocusable(false);
    updateCampaignButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            updateCampaignButtonActionPerformed(evt);
        }
    });

    deleteCampaignButton.setFont(new java.awt.Font("STHeiti", 0, 10));
    deleteCampaignButton.setText("Delete");
    deleteCampaignButton.setToolTipText("Delete Campaign");
    deleteCampaignButton.setFocusable(false);
    deleteCampaignButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            deleteCampaignButtonActionPerformed(evt);
        }
    });

    campaignInnerPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Campaign", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("STHeiti", 0, 14), new java.awt.Color(51, 51, 51))); // NOI18N
    campaignInnerPanel.setToolTipText("");

    campaignIdLabel.setFont(new java.awt.Font("STHeiti", 0, 14));
    campaignIdLabel.setForeground(new java.awt.Color(51, 51, 51));
    campaignIdLabel.setText("Id");

    timeScheduledLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
    timeScheduledLabel.setForeground(new java.awt.Color(51, 51, 51));
    timeScheduledLabel.setText("Scheduled");

    timeExpectedLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
    timeExpectedLabel.setForeground(new java.awt.Color(51, 51, 51));
    timeExpectedLabel.setText("Expected");

    timeRegisteredLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
    timeRegisteredLabel.setForeground(new java.awt.Color(51, 51, 51));
    timeRegisteredLabel.setText("Registered");

    timeStartLabel.setFont(new java.awt.Font("STHeiti", 0, 12));
    timeStartLabel.setForeground(new java.awt.Color(51, 51, 51));
    timeStartLabel.setText("Start");

    timeEndLabel.setFont(new java.awt.Font("STHeiti", 0, 12));
    timeEndLabel.setForeground(new java.awt.Color(51, 51, 51));
    timeEndLabel.setText("End");

    campaignTimestampLabel.setFont(new java.awt.Font("STHeiti", 0, 12));
    campaignTimestampLabel.setForeground(new java.awt.Color(51, 51, 51));
    campaignTimestampLabel.setText("Time");

    idField.setFont(new java.awt.Font("STHeiti", 0, 12));
    idField.setText("0");
    idField.setFocusTraversalKeysEnabled(false);

    campaignTimestampField.setFont(new java.awt.Font("STHeiti", 0, 12));
    campaignTimestampField.setText("0");
    campaignTimestampField.setFocusTraversalKeysEnabled(false);

    campaignInnerSeparator.setFocusable(true);

    timeScheduledStartField.setFont(new java.awt.Font("STHeiti", 0, 12));
    timeScheduledStartField.setText("0");
    timeScheduledStartField.setFocusTraversalKeysEnabled(false);

    timeExpectedStartField.setFont(new java.awt.Font("STHeiti", 0, 12));
    timeExpectedStartField.setText("0");
    timeExpectedStartField.setFocusTraversalKeysEnabled(false);

    timeScheduledEndField.setFont(new java.awt.Font("STHeiti", 0, 12));
    timeScheduledEndField.setText("0");
    timeScheduledEndField.setFocusTraversalKeysEnabled(false);

    timeExpectedEndField.setFont(new java.awt.Font("STHeiti", 0, 12));
    timeExpectedEndField.setText("0");
    timeExpectedEndField.setFocusTraversalKeysEnabled(false);

    timeRegisteredStartField.setFont(new java.awt.Font("STHeiti", 0, 12));
    timeRegisteredStartField.setText("0");
    timeRegisteredStartField.setFocusTraversalKeysEnabled(false);

    timeRegisteredEndField.setFont(new java.awt.Font("STHeiti", 0, 12));
    timeRegisteredEndField.setText("0");
    timeRegisteredEndField.setFocusTraversalKeysEnabled(false);

    campaignOrderIdField.setFont(new java.awt.Font("STHeiti", 0, 12));
    campaignOrderIdField.setText("0");
    campaignOrderIdField.setFocusTraversalKeysEnabled(false);

    oidLabel.setFont(new java.awt.Font("STHeiti", 0, 12));
    oidLabel.setForeground(new java.awt.Color(51, 51, 51));
    oidLabel.setText("OID");

    campaignTestCheckBox.setFont(new java.awt.Font("STHeiti", 0, 13));

    testLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
    testLabel.setForeground(new java.awt.Color(51, 51, 51));
    testLabel.setText("Test");

    org.jdesktop.layout.GroupLayout campaignInnerPanelLayout = new org.jdesktop.layout.GroupLayout(campaignInnerPanel);
    campaignInnerPanel.setLayout(campaignInnerPanelLayout);
    campaignInnerPanelLayout.setHorizontalGroup(
        campaignInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(campaignInnerPanelLayout.createSequentialGroup()
            .addContainerGap()
            .add(campaignInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(campaignInnerPanelLayout.createSequentialGroup()
                    .add(campaignInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(campaignInnerSeparator, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 458, Short.MAX_VALUE)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, campaignInnerPanelLayout.createSequentialGroup()
                            .add(campaignIdLabel)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                            .add(idField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 54, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(65, 65, 65)
                            .add(campaignTimestampLabel)
                            .add(50, 50, 50)
                            .add(campaignTimestampField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE)
                            .add(18, 18, 18)
                            .add(oidLabel)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(campaignOrderIdField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 49, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(campaignInnerPanelLayout.createSequentialGroup()
                            .add(campaignInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(testLabel)
                                .add(timeRegisteredLabel)
                                .add(timeExpectedLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 71, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(timeScheduledLabel))
                            .add(26, 26, 26)
                            .add(campaignInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(campaignInnerPanelLayout.createSequentialGroup()
                                    .add(campaignInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(org.jdesktop.layout.GroupLayout.TRAILING, timeRegisteredStartField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE)
                                        .add(timeExpectedStartField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE)
                                        .add(timeScheduledStartField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 118, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                    .add(campaignInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(timeRegisteredEndField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE)
                                        .add(timeExpectedEndField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE)
                                        .add(timeScheduledEndField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE))
                                    .add(94, 94, 94))
                                .add(campaignTestCheckBox))))
                    .addContainerGap())
                .add(org.jdesktop.layout.GroupLayout.TRAILING, campaignInnerPanelLayout.createSequentialGroup()
                    .add(timeStartLabel)
                    .add(99, 99, 99)
                    .add(timeEndLabel)
                    .add(170, 170, 170))))
    );

    campaignInnerPanelLayout.linkSize(new java.awt.Component[] {timeExpectedEndField, timeExpectedStartField, timeRegisteredEndField, timeRegisteredStartField, timeScheduledEndField, timeScheduledStartField}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

    campaignInnerPanelLayout.setVerticalGroup(
        campaignInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(campaignInnerPanelLayout.createSequentialGroup()
            .add(campaignInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(campaignIdLabel)
                .add(campaignOrderIdField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(oidLabel)
                .add(idField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(campaignTimestampLabel)
                .add(campaignTimestampField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(campaignInnerSeparator, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(campaignInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(timeStartLabel)
                .add(timeEndLabel))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(campaignInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                .add(campaignInnerPanelLayout.createSequentialGroup()
                    .add(timeScheduledEndField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(4, 4, 4)
                    .add(timeExpectedEndField, 0, 0, Short.MAX_VALUE))
                .add(campaignInnerPanelLayout.createSequentialGroup()
                    .add(campaignInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(timeScheduledStartField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(timeScheduledLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(4, 4, 4)
                    .add(campaignInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(timeExpectedStartField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(timeExpectedLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 13, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
            .add(campaignInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(campaignInnerPanelLayout.createSequentialGroup()
                    .add(9, 9, 9)
                    .add(timeRegisteredLabel))
                .add(campaignInnerPanelLayout.createSequentialGroup()
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(campaignInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(timeRegisteredStartField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(timeRegisteredEndField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
            .add(6, 6, 6)
            .add(campaignInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(testLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(campaignTestCheckBox))
            .addContainerGap(53, Short.MAX_VALUE))
    );

    campaignInnerPanelLayout.linkSize(new java.awt.Component[] {timeExpectedEndField, timeExpectedStartField, timeRegisteredEndField, timeRegisteredStartField, timeScheduledEndField, timeScheduledStartField}, org.jdesktop.layout.GroupLayout.VERTICAL);

    previousCampaignButton.setFont(new java.awt.Font("STHeiti", 0, 10));
    previousCampaignButton.setText("<");
    previousCampaignButton.setToolTipText("Previous");
    previousCampaignButton.setFocusable(false);
    previousCampaignButton.setMaximumSize(new java.awt.Dimension(80, 29));
    previousCampaignButton.setMinimumSize(new java.awt.Dimension(80, 29));
    previousCampaignButton.setPreferredSize(new java.awt.Dimension(80, 29));
    previousCampaignButton.setSize(new java.awt.Dimension(80, 29));
    previousCampaignButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            previousCampaignButtonActionPerformed(evt);
        }
    });

    nextCampaignButton.setFont(new java.awt.Font("STHeiti", 0, 10));
    nextCampaignButton.setText(">");
    nextCampaignButton.setToolTipText("Previous");
    nextCampaignButton.setFocusable(false);
    nextCampaignButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            nextCampaignButtonActionPerformed(evt);
        }
    });

    org.jdesktop.layout.GroupLayout campaignPanelLayout = new org.jdesktop.layout.GroupLayout(campaignPanel);
    campaignPanel.setLayout(campaignPanelLayout);
    campaignPanelLayout.setHorizontalGroup(
        campaignPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(campaignPanelLayout.createSequentialGroup()
            .addContainerGap()
            .add(campaignPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                .add(campaignPanelLayout.createSequentialGroup()
                    .add(previousCampaignButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 40, Short.MAX_VALUE)
                    .add(2, 2, 2)
                    .add(nextCampaignButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 34, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(deleteCampaignButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 82, Short.MAX_VALUE)
                .add(updateCampaignButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(insertCampaignButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 82, Short.MAX_VALUE)
                .add(selectCampaignButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 82, Short.MAX_VALUE))
            .add(18, 18, 18)
            .add(campaignInnerPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(77, 77, 77))
    );

    campaignPanelLayout.linkSize(new java.awt.Component[] {deleteCampaignButton, insertCampaignButton, selectCampaignButton, updateCampaignButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

    campaignPanelLayout.linkSize(new java.awt.Component[] {nextCampaignButton, previousCampaignButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

    campaignPanelLayout.setVerticalGroup(
        campaignPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(campaignPanelLayout.createSequentialGroup()
            .add(campaignPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(campaignPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .add(campaignPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(previousCampaignButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(nextCampaignButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(selectCampaignButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(insertCampaignButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(updateCampaignButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(deleteCampaignButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(campaignInnerPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addContainerGap())
    );

    campaignPanelLayout.linkSize(new java.awt.Component[] {deleteCampaignButton, insertCampaignButton, selectCampaignButton, updateCampaignButton}, org.jdesktop.layout.GroupLayout.VERTICAL);

    backofficeTab.addTab("Campaign", campaignPanel);

    destinationsPanel.setToolTipText("");
    destinationsPanel.setFocusTraversalKeysEnabled(false);
    destinationsPanel.setFont(new java.awt.Font("STHeiti", 0, 13));
    destinationsPanel.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyPressed(java.awt.event.KeyEvent evt) {
            destinationsPanelKeyPressed(evt);
        }
    });

    selectDestinationButton.setFont(new java.awt.Font("STHeiti", 0, 10));
    selectDestinationButton.setText("Select");
    selectDestinationButton.setToolTipText("Select Destination");
    selectDestinationButton.setFocusable(false);
    selectDestinationButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            selectDestinationButtonActionPerformed(evt);
        }
    });

    insertDestinationButton.setFont(new java.awt.Font("STHeiti", 0, 10));
    insertDestinationButton.setText("Insert");
    insertDestinationButton.setToolTipText("Insert Campaign (automatically done in Order Menu)");
    insertDestinationButton.setFocusable(false);
    insertDestinationButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            insertDestinationButtonActionPerformed(evt);
        }
    });

    updateDestinationButton.setFont(new java.awt.Font("STHeiti", 0, 10));
    updateDestinationButton.setText("Update");
    updateDestinationButton.setToolTipText("Update Destination");
    updateDestinationButton.setFocusable(false);
    updateDestinationButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            updateDestinationButtonActionPerformed(evt);
        }
    });

    deleteDestinationButton.setFont(new java.awt.Font("STHeiti", 0, 10));
    deleteDestinationButton.setText("Delete");
    deleteDestinationButton.setToolTipText("Delete Destination");
    deleteDestinationButton.setFocusable(false);
    deleteDestinationButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            deleteDestinationButtonActionPerformed(evt);
        }
    });

    destinationTimestampPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("STHeiti", 0, 14), new java.awt.Color(51, 51, 51))); // NOI18N
    destinationTimestampPanel.setToolTipText("");

    idLabel.setFont(new java.awt.Font("STHeiti", 0, 12));
    idLabel.setForeground(new java.awt.Color(51, 51, 51));
    idLabel.setText("Id");

    telLabel.setFont(new java.awt.Font("STHeiti", 0, 14));
    telLabel.setForeground(new java.awt.Color(51, 51, 51));
    telLabel.setText("PhoneNr");

    destinationCountLabel.setFont(new java.awt.Font("STHeiti", 0, 12));
    destinationCountLabel.setForeground(new java.awt.Color(51, 51, 51));
    destinationCountLabel.setText("Cnt");

    conLabel.setFont(new java.awt.Font("STHeiti", 0, 12));
    conLabel.setForeground(new java.awt.Color(51, 51, 51));
    conLabel.setText("Con");

    destIdField.setBackground(new java.awt.Color(204, 204, 204));
    destIdField.setFont(new java.awt.Font("STHeiti", 0, 10));
    destIdField.setText("0");
    destIdField.setToolTipText("Destination Id");
    destIdField.setFocusTraversalKeysEnabled(false);

    campaignIdField.setBackground(new java.awt.Color(204, 204, 204));
    campaignIdField.setFont(new java.awt.Font("STHeiti", 0, 10));
    campaignIdField.setToolTipText("Campaign Id");
    campaignIdField.setFocusTraversalKeysEnabled(false);

    destCountField.setBackground(new java.awt.Color(204, 204, 204));
    destCountField.setFont(new java.awt.Font("STHeiti", 0, 10));
    destCountField.setToolTipText("Destination Count");
    destCountField.setFocusTraversalKeysEnabled(false);

    connectingTimestampField.setBackground(new java.awt.Color(204, 204, 204));
    connectingTimestampField.setFont(new java.awt.Font("STHeiti", 0, 10));
    connectingTimestampField.setToolTipText("Call Connected Timestamp Epoch");
    connectingTimestampField.setFocusTraversalKeysEnabled(false);

    destinationField.setFont(new java.awt.Font("STHeiti", 0, 10));
    destinationField.setForeground(new java.awt.Color(102, 102, 102));
    destinationField.setToolTipText("PhoneNumber");
    destinationField.setFocusTraversalKeysEnabled(false);

    tryLabel.setFont(new java.awt.Font("STHeiti", 0, 12));
    tryLabel.setForeground(new java.awt.Color(51, 51, 51));
    tryLabel.setText("Try");

    tryingTimestampField.setBackground(new java.awt.Color(204, 204, 204));
    tryingTimestampField.setFont(new java.awt.Font("STHeiti", 0, 10));
    tryingTimestampField.setToolTipText("Call Connected Timestamp Epoch");
    tryingTimestampField.setFocusTraversalKeysEnabled(false);

    talkLabel.setFont(new java.awt.Font("STHeiti", 0, 12));
    talkLabel.setForeground(new java.awt.Color(51, 51, 51));
    talkLabel.setText("Est");

    actLabel.setFont(new java.awt.Font("STHeiti", 0, 12));
    actLabel.setForeground(new java.awt.Color(51, 51, 51));
    actLabel.setText("Ac");

    ringLabel.setFont(new java.awt.Font("STHeiti", 0, 12));
    ringLabel.setForeground(new java.awt.Color(51, 51, 51));
    ringLabel.setText("Ring");

    callLabel.setFont(new java.awt.Font("STHeiti", 0, 12));
    callLabel.setForeground(new java.awt.Color(51, 51, 51));
    callLabel.setText("Call");

    ringingTimestampField.setBackground(new java.awt.Color(204, 204, 204));
    ringingTimestampField.setFont(new java.awt.Font("STHeiti", 0, 10));
    ringingTimestampField.setToolTipText("Ring Timestamp Epoch");
    ringingTimestampField.setFocusTraversalKeysEnabled(false);

    callingTimestampField.setBackground(new java.awt.Color(204, 204, 204));
    callingTimestampField.setFont(new java.awt.Font("STHeiti", 0, 10));
    callingTimestampField.setToolTipText("Call Timestamp Epoch");
    callingTimestampField.setFocusTraversalKeysEnabled(false);

    talkingTimestampField.setBackground(new java.awt.Color(204, 204, 204));
    talkingTimestampField.setFont(new java.awt.Font("STHeiti", 0, 10));
    talkingTimestampField.setToolTipText("Call Established Timestamp Epoch");
    talkingTimestampField.setFocusTraversalKeysEnabled(false);

    acceptingTimestampField.setBackground(new java.awt.Color(204, 204, 204));
    acceptingTimestampField.setFont(new java.awt.Font("STHeiti", 0, 10));
    acceptingTimestampField.setToolTipText("Call Accepted Timestamp Epoch");
    acceptingTimestampField.setFocusTraversalKeysEnabled(false);

    timeOfCompletionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Time of Completion", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("STHeiti", 0, 14), new java.awt.Color(51, 51, 51))); // NOI18N
    timeOfCompletionPanel.setToolTipText("");

    localCancelingTimestampField.setBackground(new java.awt.Color(204, 204, 204));
    localCancelingTimestampField.setFont(new java.awt.Font("STHeiti", 0, 10));
    localCancelingTimestampField.setToolTipText("Call Canceled Locally Timestamp Epoch");
    localCancelingTimestampField.setFocusTraversalKeysEnabled(false);

    remoteCancelingTimestampField.setBackground(new java.awt.Color(204, 204, 204));
    remoteCancelingTimestampField.setFont(new java.awt.Font("STHeiti", 0, 10));
    remoteCancelingTimestampField.setToolTipText("Call Canceled Remotely Timestamp Epoch");
    remoteCancelingTimestampField.setFocusTraversalKeysEnabled(false);

    localBusyTimestampField.setBackground(new java.awt.Color(204, 204, 204));
    localBusyTimestampField.setFont(new java.awt.Font("STHeiti", 0, 10));
    localBusyTimestampField.setToolTipText("Call Busy Locally Timestamp Epoch");
    localBusyTimestampField.setFocusTraversalKeysEnabled(false);

    remoteBusyTimestampField.setBackground(new java.awt.Color(204, 204, 204));
    remoteBusyTimestampField.setFont(new java.awt.Font("STHeiti", 0, 10));
    remoteBusyTimestampField.setToolTipText("Call Busy Remotely Timestamp Epoch");
    remoteBusyTimestampField.setFocusTraversalKeysEnabled(false);

    localByeTimestampField.setBackground(new java.awt.Color(204, 204, 204));
    localByeTimestampField.setFont(new java.awt.Font("STHeiti", 0, 10));
    localByeTimestampField.setToolTipText("Call Ended Locally Timestamp Epoch");
    localByeTimestampField.setFocusTraversalKeysEnabled(false);

    remoteByeTimestampField.setBackground(new java.awt.Color(204, 204, 204));
    remoteByeTimestampField.setFont(new java.awt.Font("STHeiti", 0, 10));
    remoteByeTimestampField.setToolTipText("Call Ended Remotely Timestamp Epoch");
    remoteByeTimestampField.setFocusTraversalKeysEnabled(false);

    localLabel.setFont(new java.awt.Font("STHeiti", 0, 14));
    localLabel.setForeground(new java.awt.Color(51, 51, 51));
    localLabel.setText("Local");

    remoteLabel.setFont(new java.awt.Font("STHeiti", 0, 14));
    remoteLabel.setForeground(new java.awt.Color(51, 51, 51));
    remoteLabel.setText("Remote");

    canceledLabel.setFont(new java.awt.Font("STHeiti", 0, 12));
    canceledLabel.setForeground(new java.awt.Color(51, 51, 51));
    canceledLabel.setText("Canceled");

    busyLabel.setFont(new java.awt.Font("STHeiti", 0, 12));
    busyLabel.setForeground(new java.awt.Color(51, 51, 51));
    busyLabel.setText("Busy");

    endedLabel.setFont(new java.awt.Font("STHeiti", 0, 12));
    endedLabel.setForeground(new java.awt.Color(51, 51, 51));
    endedLabel.setText("Ended");

    org.jdesktop.layout.GroupLayout timeOfCompletionPanelLayout = new org.jdesktop.layout.GroupLayout(timeOfCompletionPanel);
    timeOfCompletionPanel.setLayout(timeOfCompletionPanelLayout);
    timeOfCompletionPanelLayout.setHorizontalGroup(
        timeOfCompletionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(timeOfCompletionPanelLayout.createSequentialGroup()
            .addContainerGap()
            .add(timeOfCompletionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(endedLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 59, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(busyLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 59, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(canceledLabel))
            .add(12, 12, 12)
            .add(timeOfCompletionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(org.jdesktop.layout.GroupLayout.TRAILING, timeOfCompletionPanelLayout.createSequentialGroup()
                    .add(timeOfCompletionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, localCancelingTimestampField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 111, Short.MAX_VALUE)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, localBusyTimestampField)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, localByeTimestampField))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(timeOfCompletionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(remoteByeTimestampField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 105, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(remoteCancelingTimestampField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 105, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(remoteBusyTimestampField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 105, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(timeOfCompletionPanelLayout.createSequentialGroup()
                    .add(43, 43, 43)
                    .add(localLabel)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 64, Short.MAX_VALUE)
                    .add(remoteLabel)
                    .add(25, 25, 25)))
            .addContainerGap())
    );
    timeOfCompletionPanelLayout.setVerticalGroup(
        timeOfCompletionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(timeOfCompletionPanelLayout.createSequentialGroup()
            .addContainerGap()
            .add(timeOfCompletionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(localLabel)
                .add(remoteLabel))
            .add(timeOfCompletionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(localCancelingTimestampField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(remoteCancelingTimestampField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(canceledLabel))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(timeOfCompletionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(localBusyTimestampField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(remoteBusyTimestampField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(busyLabel))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(timeOfCompletionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(org.jdesktop.layout.GroupLayout.TRAILING, timeOfCompletionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(localByeTimestampField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(endedLabel))
                .add(org.jdesktop.layout.GroupLayout.TRAILING, remoteByeTimestampField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .addContainerGap(53, Short.MAX_VALUE))
    );

    callingAttemptsField.setBackground(new java.awt.Color(204, 204, 204));
    callingAttemptsField.setFont(new java.awt.Font("STHeiti", 0, 10));
    callingAttemptsField.setText("0");
    callingAttemptsField.setToolTipText("Calling Attempts");
    callingAttemptsField.setFocusTraversalKeysEnabled(false);

    responseStatusCodeLabelLabel.setFont(new java.awt.Font("STHeiti", 0, 14));
    responseStatusCodeLabelLabel.setForeground(new java.awt.Color(51, 51, 51));
    responseStatusCodeLabelLabel.setText("Response");

    responseStatusCodeField.setBackground(new java.awt.Color(204, 204, 204));
    responseStatusCodeField.setFont(new java.awt.Font("STHeiti", 0, 10));
    responseStatusCodeField.setForeground(new java.awt.Color(102, 102, 102));
    responseStatusCodeField.setText("0");
    responseStatusCodeField.setToolTipText("SIP Response Code");
    responseStatusCodeField.setFocusTraversalKeysEnabled(false);

    responseStatusDescField.setBackground(new java.awt.Color(204, 204, 204));
    responseStatusDescField.setFont(new java.awt.Font("STHeiti", 0, 10));
    responseStatusDescField.setForeground(new java.awt.Color(102, 102, 102));
    responseStatusDescField.setToolTipText("SIP Response Code");
    responseStatusDescField.setFocusTraversalKeysEnabled(false);

    org.jdesktop.layout.GroupLayout destinationTimestampPanelLayout = new org.jdesktop.layout.GroupLayout(destinationTimestampPanel);
    destinationTimestampPanel.setLayout(destinationTimestampPanelLayout);
    destinationTimestampPanelLayout.setHorizontalGroup(
        destinationTimestampPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(destinationTimestampPanelLayout.createSequentialGroup()
            .add(destinationTimestampPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(destinationTimestampPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .add(destinationTimestampPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                        .add(idLabel)
                        .add(destinationCountLabel)
                        .add(conLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(tryLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                        .add(callLabel)
                        .add(ringLabel)
                        .add(actLabel)
                        .add(talkLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(destinationTimestampPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, destinationTimestampPanelLayout.createSequentialGroup()
                            .add(destinationTimestampPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, talkingTimestampField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, acceptingTimestampField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, ringingTimestampField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, tryingTimestampField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)
                                .add(connectingTimestampField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, destinationTimestampPanelLayout.createSequentialGroup()
                                    .add(callingTimestampField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 97, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                    .add(callingAttemptsField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)))
                            .add(18, 18, 18))
                        .add(destinationTimestampPanelLayout.createSequentialGroup()
                            .add(destIdField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 97, Short.MAX_VALUE)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(campaignIdField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 45, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(destCountField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 148, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(destinationTimestampPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, telLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 64, Short.MAX_VALUE)
                        .add(responseStatusCodeLabelLabel))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(destinationTimestampPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(destinationTimestampPanelLayout.createSequentialGroup()
                            .add(responseStatusCodeField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 44, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(responseStatusDescField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 197, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(destinationField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 251, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(destinationTimestampPanelLayout.createSequentialGroup()
                    .add(203, 203, 203)
                    .add(timeOfCompletionPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
            .addContainerGap())
    );

    destinationTimestampPanelLayout.linkSize(new java.awt.Component[] {actLabel, callLabel, conLabel, destinationCountLabel, idLabel, ringLabel, talkLabel, tryLabel}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

    destinationTimestampPanelLayout.linkSize(new java.awt.Component[] {callingAttemptsField, campaignIdField}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

    destinationTimestampPanelLayout.linkSize(new java.awt.Component[] {callingTimestampField, destIdField}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

    destinationTimestampPanelLayout.linkSize(new java.awt.Component[] {responseStatusCodeLabelLabel, telLabel}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

    destinationTimestampPanelLayout.setVerticalGroup(
        destinationTimestampPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(destinationTimestampPanelLayout.createSequentialGroup()
            .add(destinationTimestampPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(destIdField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(campaignIdField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(telLabel)
                .add(destinationField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(idLabel))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(destinationTimestampPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(destinationTimestampPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(destCountField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(destinationCountLabel))
                .add(destinationTimestampPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(responseStatusCodeLabelLabel)
                    .add(responseStatusCodeField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(responseStatusDescField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(destinationTimestampPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                .add(org.jdesktop.layout.GroupLayout.LEADING, destinationTimestampPanelLayout.createSequentialGroup()
                    .add(destinationTimestampPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(connectingTimestampField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(conLabel))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(destinationTimestampPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(tryingTimestampField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(tryLabel))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(destinationTimestampPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(callingTimestampField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(callingAttemptsField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(callLabel))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(destinationTimestampPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(ringingTimestampField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(ringLabel))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(destinationTimestampPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(acceptingTimestampField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(actLabel))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(destinationTimestampPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(talkingTimestampField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(talkLabel)))
                .add(timeOfCompletionPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addContainerGap())
    );

    destinationTimestampPanelLayout.linkSize(new java.awt.Component[] {acceptingTimestampField, callingAttemptsField, callingTimestampField, campaignIdField, connectingTimestampField, destCountField, destIdField, destinationField, responseStatusCodeField, responseStatusDescField, ringingTimestampField, talkingTimestampField, tryingTimestampField}, org.jdesktop.layout.GroupLayout.VERTICAL);

    destinationTimestampPanelLayout.linkSize(new java.awt.Component[] {responseStatusCodeLabelLabel, telLabel}, org.jdesktop.layout.GroupLayout.VERTICAL);

    previousDestinationButton.setFont(new java.awt.Font("STHeiti", 0, 10));
    previousDestinationButton.setText("<");
    previousDestinationButton.setToolTipText("Previous");
    previousDestinationButton.setFocusable(false);
    previousDestinationButton.setMaximumSize(new java.awt.Dimension(80, 29));
    previousDestinationButton.setMinimumSize(new java.awt.Dimension(80, 29));
    previousDestinationButton.setPreferredSize(new java.awt.Dimension(80, 29));
    previousDestinationButton.setSize(new java.awt.Dimension(80, 29));
    previousDestinationButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            previousDestinationButtonActionPerformed(evt);
        }
    });

    nextDestinationButton.setFont(new java.awt.Font("STHeiti", 0, 10));
    nextDestinationButton.setText(">");
    nextDestinationButton.setToolTipText("Previous");
    nextDestinationButton.setFocusable(false);
    nextDestinationButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            nextDestinationButtonActionPerformed(evt);
        }
    });

    org.jdesktop.layout.GroupLayout destinationsPanelLayout = new org.jdesktop.layout.GroupLayout(destinationsPanel);
    destinationsPanel.setLayout(destinationsPanelLayout);
    destinationsPanelLayout.setHorizontalGroup(
        destinationsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(org.jdesktop.layout.GroupLayout.TRAILING, destinationsPanelLayout.createSequentialGroup()
            .addContainerGap()
            .add(destinationsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(destinationsPanelLayout.createSequentialGroup()
                    .add(destinationsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                        .add(deleteDestinationButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(updateDestinationButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(insertDestinationButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED))
                .add(org.jdesktop.layout.GroupLayout.TRAILING, destinationsPanelLayout.createSequentialGroup()
                    .add(destinationsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, selectDestinationButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE)
                        .add(destinationsPanelLayout.createSequentialGroup()
                            .add(previousDestinationButton, 0, 40, Short.MAX_VALUE)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(nextDestinationButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 34, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(8, 8, 8)))
            .add(destinationTimestampPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(44, 44, 44))
    );

    destinationsPanelLayout.linkSize(new java.awt.Component[] {deleteDestinationButton, insertDestinationButton, selectDestinationButton, updateDestinationButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

    destinationsPanelLayout.linkSize(new java.awt.Component[] {nextDestinationButton, previousDestinationButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

    destinationsPanelLayout.setVerticalGroup(
        destinationsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(destinationsPanelLayout.createSequentialGroup()
            .add(destinationsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(destinationTimestampPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(destinationsPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .add(destinationsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(previousDestinationButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(nextDestinationButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(selectDestinationButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(insertDestinationButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(updateDestinationButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(deleteDestinationButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
            .addContainerGap())
    );

    destinationsPanelLayout.linkSize(new java.awt.Component[] {deleteDestinationButton, insertDestinationButton, selectDestinationButton, updateDestinationButton}, org.jdesktop.layout.GroupLayout.VERTICAL);

    backofficeTab.addTab("Destinations", destinationsPanel);

    campaignStatsPanel.setToolTipText("");
    campaignStatsPanel.setFocusTraversalKeysEnabled(false);
    campaignStatsPanel.setFont(new java.awt.Font("STHeiti", 0, 13));
    campaignStatsPanel.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyPressed(java.awt.event.KeyEvent evt) {
            campaignStatsPanelKeyPressed(evt);
        }
    });

    selectCampaignStatsButton.setFont(new java.awt.Font("STHeiti", 0, 10));
    selectCampaignStatsButton.setText("Select");
    selectCampaignStatsButton.setToolTipText("Select CampaignStat");
    selectCampaignStatsButton.setFocusable(false);
    selectCampaignStatsButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            selectCampaignStatsButtonActionPerformed(evt);
        }
    });

    insertCampaignStatsButton.setFont(new java.awt.Font("STHeiti", 0, 10));
    insertCampaignStatsButton.setText("Insert");
    insertCampaignStatsButton.setToolTipText("Insert CampaignStat (automatically done in Order Menu)");
    insertCampaignStatsButton.setFocusable(false);
    insertCampaignStatsButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            insertCampaignStatsButtonActionPerformed(evt);
        }
    });

    updateCampaignStatsButton.setFont(new java.awt.Font("STHeiti", 0, 10));
    updateCampaignStatsButton.setText("Update");
    updateCampaignStatsButton.setToolTipText("Update CampaignStat");
    updateCampaignStatsButton.setFocusable(false);
    updateCampaignStatsButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            updateCampaignStatsButtonActionPerformed(evt);
        }
    });

    deleteCampaignStatsButton.setFont(new java.awt.Font("STHeiti", 0, 10));
    deleteCampaignStatsButton.setText("Delete");
    deleteCampaignStatsButton.setToolTipText("Update CampaignStat");
    deleteCampaignStatsButton.setFocusable(false);
    deleteCampaignStatsButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            deleteCampaignStatsButtonActionPerformed(evt);
        }
    });

    campaignStatisticsInnerPanel.setToolTipText("");

    callInitPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Pool", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("STHeiti", 0, 12), new java.awt.Color(51, 51, 51))); // NOI18N
    callInitPanel.setToolTipText("");
    callInitPanel.setFocusable(false);
    callInitPanel.setFont(new java.awt.Font("STHeiti", 0, 13));

    campaignId2Label.setFont(new java.awt.Font("STHeiti", 0, 10));
    campaignId2Label.setForeground(new java.awt.Color(51, 51, 51));
    campaignId2Label.setText("Cmpgn Id");

    onACLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
    onACLabel.setForeground(new java.awt.Color(51, 51, 51));
    onACLabel.setText("OnAC");

    idleACLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
    idleACLabel.setForeground(new java.awt.Color(51, 51, 51));
    idleACLabel.setText("IdleAC");

    campaignStatIdField.setBackground(new java.awt.Color(204, 204, 204));
    campaignStatIdField.setFont(new java.awt.Font("STHeiti", 0, 10));
    campaignStatIdField.setText("0");
    campaignStatIdField.setToolTipText("Campaign Id");
    campaignStatIdField.setFocusTraversalKeysEnabled(false);
    campaignStatIdField.setMaximumSize(new java.awt.Dimension(60, 20));
    campaignStatIdField.setMinimumSize(new java.awt.Dimension(60, 20));
    campaignStatIdField.setPreferredSize(new java.awt.Dimension(60, 20));
    campaignStatIdField.setSize(new java.awt.Dimension(60, 20));

    onACField.setBackground(new java.awt.Color(204, 204, 204));
    onACField.setFont(new java.awt.Font("STHeiti", 0, 10));
    onACField.setToolTipText("Phones Powered On");
    onACField.setFocusTraversalKeysEnabled(false);
    onACField.setMaximumSize(new java.awt.Dimension(60, 20));
    onACField.setMinimumSize(new java.awt.Dimension(60, 20));
    onACField.setPreferredSize(new java.awt.Dimension(60, 20));

    idleACField.setBackground(new java.awt.Color(204, 204, 204));
    idleACField.setFont(new java.awt.Font("STHeiti", 0, 10));
    idleACField.setToolTipText("Phones in Idle State");
    idleACField.setFocusTraversalKeysEnabled(false);
    idleACField.setMaximumSize(new java.awt.Dimension(60, 20));
    idleACField.setMinimumSize(new java.awt.Dimension(60, 20));
    idleACField.setPreferredSize(new java.awt.Dimension(60, 20));

    org.jdesktop.layout.GroupLayout callInitPanelLayout = new org.jdesktop.layout.GroupLayout(callInitPanel);
    callInitPanel.setLayout(callInitPanelLayout);
    callInitPanelLayout.setHorizontalGroup(
        callInitPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(callInitPanelLayout.createSequentialGroup()
            .addContainerGap()
            .add(callInitPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                .add(callInitPanelLayout.createSequentialGroup()
                    .add(callInitPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(onACField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(callInitPanelLayout.createSequentialGroup()
                            .add(14, 14, 14)
                            .add(onACLabel)))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(callInitPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(idleACField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(callInitPanelLayout.createSequentialGroup()
                            .add(13, 13, 13)
                            .add(idleACLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 47, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .add(callInitPanelLayout.createSequentialGroup()
                    .add(campaignId2Label)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(campaignStatIdField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 126, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
            .add(54, 54, 54))
    );

    callInitPanelLayout.linkSize(new java.awt.Component[] {idleACField, onACField}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

    callInitPanelLayout.setVerticalGroup(
        callInitPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(callInitPanelLayout.createSequentialGroup()
            .add(callInitPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(campaignStatIdField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(campaignId2Label))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(callInitPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(idleACLabel)
                .add(onACLabel))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(callInitPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(idleACField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(onACField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
    );

    callInitPanelLayout.linkSize(new java.awt.Component[] {campaignStatIdField, idleACField, onACField}, org.jdesktop.layout.GroupLayout.VERTICAL);

    callProgressPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Progress", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("STHeiti", 0, 12), new java.awt.Color(51, 51, 51))); // NOI18N
    callProgressPanel.setToolTipText("");
    callProgressPanel.setFocusable(false);
    callProgressPanel.setFont(new java.awt.Font("STHeiti", 0, 13));

    concurrentLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
    concurrentLabel.setForeground(new java.awt.Color(51, 51, 51));
    concurrentLabel.setText("Concurrent");

    totalCountLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
    totalCountLabel.setForeground(new java.awt.Color(51, 51, 51));
    totalCountLabel.setText("Total Count");

    progressRingingLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
    progressRingingLabel.setForeground(new java.awt.Color(51, 51, 51));
    progressRingingLabel.setText("Ringing");

    progressAcceptingLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
    progressAcceptingLabel.setForeground(new java.awt.Color(51, 51, 51));
    progressAcceptingLabel.setText("Accepting");

    progressTalkingLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
    progressTalkingLabel.setForeground(new java.awt.Color(51, 51, 51));
    progressTalkingLabel.setText("Talking");

    ringingACField.setBackground(new java.awt.Color(204, 204, 204));
    ringingACField.setFont(new java.awt.Font("STHeiti", 0, 10));
    ringingACField.setToolTipText("Phones Ringing");
    ringingACField.setFocusTraversalKeysEnabled(false);
    ringingACField.setMaximumSize(new java.awt.Dimension(60, 20));
    ringingACField.setMinimumSize(new java.awt.Dimension(60, 20));
    ringingACField.setPreferredSize(new java.awt.Dimension(60, 23));

    ringingTTField.setBackground(new java.awt.Color(204, 204, 204));
    ringingTTField.setFont(new java.awt.Font("STHeiti", 0, 10));
    ringingTTField.setToolTipText("Phone Rings Counted ");
    ringingTTField.setFocusTraversalKeysEnabled(false);
    ringingTTField.setMaximumSize(new java.awt.Dimension(60, 20));
    ringingTTField.setMinimumSize(new java.awt.Dimension(60, 20));
    ringingTTField.setPreferredSize(new java.awt.Dimension(60, 20));

    acceptingACField.setBackground(new java.awt.Color(204, 204, 204));
    acceptingACField.setFont(new java.awt.Font("STHeiti", 0, 10));
    acceptingACField.setToolTipText("Phones Accepting");
    acceptingACField.setFocusTraversalKeysEnabled(false);
    acceptingACField.setMaximumSize(new java.awt.Dimension(60, 20));
    acceptingACField.setMinimumSize(new java.awt.Dimension(60, 20));
    acceptingACField.setPreferredSize(new java.awt.Dimension(60, 20));

    acceptingTTField.setBackground(new java.awt.Color(204, 204, 204));
    acceptingTTField.setFont(new java.awt.Font("STHeiti", 0, 10));
    acceptingTTField.setToolTipText("Phones Accepts Counted");
    acceptingTTField.setFocusTraversalKeysEnabled(false);
    acceptingTTField.setMaximumSize(new java.awt.Dimension(60, 20));
    acceptingTTField.setMinimumSize(new java.awt.Dimension(60, 20));
    acceptingTTField.setPreferredSize(new java.awt.Dimension(60, 20));

    talkingACField.setBackground(new java.awt.Color(204, 204, 204));
    talkingACField.setFont(new java.awt.Font("STHeiti", 0, 10));
    talkingACField.setToolTipText("Phonecalls Established");
    talkingACField.setFocusTraversalKeysEnabled(false);
    talkingACField.setMaximumSize(new java.awt.Dimension(60, 20));
    talkingACField.setMinimumSize(new java.awt.Dimension(60, 20));
    talkingACField.setPreferredSize(new java.awt.Dimension(60, 20));

    talkingTTField.setBackground(new java.awt.Color(204, 204, 204));
    talkingTTField.setFont(new java.awt.Font("STHeiti", 0, 10));
    talkingTTField.setToolTipText("Phonecalls Established Counted");
    talkingTTField.setFocusTraversalKeysEnabled(false);
    talkingTTField.setMaximumSize(new java.awt.Dimension(60, 20));
    talkingTTField.setMinimumSize(new java.awt.Dimension(60, 20));
    talkingTTField.setPreferredSize(new java.awt.Dimension(60, 20));

    org.jdesktop.layout.GroupLayout callProgressPanelLayout = new org.jdesktop.layout.GroupLayout(callProgressPanel);
    callProgressPanel.setLayout(callProgressPanelLayout);
    callProgressPanelLayout.setHorizontalGroup(
        callProgressPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(org.jdesktop.layout.GroupLayout.TRAILING, callProgressPanelLayout.createSequentialGroup()
            .addContainerGap()
            .add(callProgressPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(totalCountLabel)
                .add(concurrentLabel))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
            .add(callProgressPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                .add(callProgressPanelLayout.createSequentialGroup()
                    .add(ringingTTField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(acceptingTTField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(talkingTTField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(org.jdesktop.layout.GroupLayout.TRAILING, callProgressPanelLayout.createSequentialGroup()
                    .add(ringingACField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(acceptingACField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(talkingACField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(callProgressPanelLayout.createSequentialGroup()
                    .add(14, 14, 14)
                    .add(progressRingingLabel)
                    .add(18, 18, 18)
                    .add(progressAcceptingLabel)
                    .add(28, 28, 28)
                    .add(progressTalkingLabel)))
            .addContainerGap())
    );

    callProgressPanelLayout.linkSize(new java.awt.Component[] {acceptingACField, acceptingTTField, ringingACField, ringingTTField, talkingACField, talkingTTField}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

    callProgressPanelLayout.setVerticalGroup(
        callProgressPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(callProgressPanelLayout.createSequentialGroup()
            .add(callProgressPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(progressAcceptingLabel)
                .add(progressTalkingLabel)
                .add(progressRingingLabel))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(callProgressPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(ringingACField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(acceptingACField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 12, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(talkingACField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 12, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(concurrentLabel))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(callProgressPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(ringingTTField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
                .add(acceptingTTField, 0, 0, Short.MAX_VALUE)
                .add(talkingTTField, 0, 0, Short.MAX_VALUE)
                .add(totalCountLabel))
            .addContainerGap())
    );

    callProgressPanelLayout.linkSize(new java.awt.Component[] {acceptingACField, acceptingTTField, ringingACField, ringingTTField, talkingACField, talkingTTField}, org.jdesktop.layout.GroupLayout.VERTICAL);

    callCompletionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Completion", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("STHeiti", 0, 12), new java.awt.Color(51, 51, 51))); // NOI18N
    callCompletionPanel.setToolTipText("");
    callCompletionPanel.setFocusable(false);
    callCompletionPanel.setFont(new java.awt.Font("STHeiti", 0, 13));

    local2Label.setFont(new java.awt.Font("STHeiti", 0, 10));
    local2Label.setForeground(new java.awt.Color(51, 51, 51));
    local2Label.setText("Local");

    remote2Label.setFont(new java.awt.Font("STHeiti", 0, 10));
    remote2Label.setForeground(new java.awt.Color(51, 51, 51));
    remote2Label.setText("Remote");

    cancelTotalLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
    cancelTotalLabel.setForeground(new java.awt.Color(51, 51, 51));
    cancelTotalLabel.setText("Cancel");

    busyTotalLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
    busyTotalLabel.setForeground(new java.awt.Color(51, 51, 51));
    busyTotalLabel.setText("Busy");

    byeTotalLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
    byeTotalLabel.setForeground(new java.awt.Color(51, 51, 51));
    byeTotalLabel.setText("Bye");

    localCancelTTField.setBackground(new java.awt.Color(204, 204, 204));
    localCancelTTField.setFont(new java.awt.Font("STHeiti", 0, 10));
    localCancelTTField.setToolTipText("Phonecalls Canceled Locally Counted");
    localCancelTTField.setFocusTraversalKeysEnabled(false);
    localCancelTTField.setMaximumSize(new java.awt.Dimension(60, 20));
    localCancelTTField.setMinimumSize(new java.awt.Dimension(60, 20));
    localCancelTTField.setPreferredSize(new java.awt.Dimension(60, 23));

    remoteCancelTTField.setBackground(new java.awt.Color(204, 204, 204));
    remoteCancelTTField.setFont(new java.awt.Font("STHeiti", 0, 10));
    remoteCancelTTField.setToolTipText("Phonecalls Canceled Remotely Counted");
    remoteCancelTTField.setFocusTraversalKeysEnabled(false);
    remoteCancelTTField.setMaximumSize(new java.awt.Dimension(60, 20));
    remoteCancelTTField.setMinimumSize(new java.awt.Dimension(60, 20));
    remoteCancelTTField.setPreferredSize(new java.awt.Dimension(60, 20));

    localBusyTTField.setBackground(new java.awt.Color(204, 204, 204));
    localBusyTTField.setFont(new java.awt.Font("STHeiti", 0, 10));
    localBusyTTField.setToolTipText("Phonecalls Busy Locally Counted");
    localBusyTTField.setFocusTraversalKeysEnabled(false);
    localBusyTTField.setMaximumSize(new java.awt.Dimension(60, 20));
    localBusyTTField.setMinimumSize(new java.awt.Dimension(60, 20));
    localBusyTTField.setPreferredSize(new java.awt.Dimension(60, 20));

    remoteBusyTTField.setBackground(new java.awt.Color(204, 204, 204));
    remoteBusyTTField.setFont(new java.awt.Font("STHeiti", 0, 10));
    remoteBusyTTField.setToolTipText("Phonecalls Busy Remotely Counted");
    remoteBusyTTField.setFocusTraversalKeysEnabled(false);
    remoteBusyTTField.setMaximumSize(new java.awt.Dimension(60, 20));
    remoteBusyTTField.setMinimumSize(new java.awt.Dimension(60, 20));
    remoteBusyTTField.setPreferredSize(new java.awt.Dimension(60, 20));

    localByeTTField.setBackground(new java.awt.Color(204, 204, 204));
    localByeTTField.setFont(new java.awt.Font("STHeiti", 0, 10));
    localByeTTField.setToolTipText("Phonecalls Ended Locally Counted");
    localByeTTField.setFocusTraversalKeysEnabled(false);
    localByeTTField.setMaximumSize(new java.awt.Dimension(60, 20));
    localByeTTField.setMinimumSize(new java.awt.Dimension(60, 20));
    localByeTTField.setPreferredSize(new java.awt.Dimension(60, 20));

    remoteByeTTField.setBackground(new java.awt.Color(204, 204, 204));
    remoteByeTTField.setFont(new java.awt.Font("STHeiti", 0, 10));
    remoteByeTTField.setToolTipText("Phonecalls Ended Remotely Counted");
    remoteByeTTField.setFocusTraversalKeysEnabled(false);
    remoteByeTTField.setMaximumSize(new java.awt.Dimension(60, 20));
    remoteByeTTField.setMinimumSize(new java.awt.Dimension(60, 20));
    remoteByeTTField.setPreferredSize(new java.awt.Dimension(60, 20));

    org.jdesktop.layout.GroupLayout callCompletionPanelLayout = new org.jdesktop.layout.GroupLayout(callCompletionPanel);
    callCompletionPanel.setLayout(callCompletionPanelLayout);
    callCompletionPanelLayout.setHorizontalGroup(
        callCompletionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(callCompletionPanelLayout.createSequentialGroup()
            .addContainerGap()
            .add(callCompletionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(local2Label, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 46, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(remote2Label))
            .add(18, 18, 18)
            .add(callCompletionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(callCompletionPanelLayout.createSequentialGroup()
                    .add(localCancelTTField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(localBusyTTField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(localByeTTField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(callCompletionPanelLayout.createSequentialGroup()
                    .add(remoteCancelTTField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(remoteBusyTTField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(remoteByeTTField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(callCompletionPanelLayout.createSequentialGroup()
                    .add(13, 13, 13)
                    .add(cancelTotalLabel)
                    .add(37, 37, 37)
                    .add(busyTotalLabel)
                    .add(45, 45, 45)
                    .add(byeTotalLabel)))
            .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    callCompletionPanelLayout.linkSize(new java.awt.Component[] {localBusyTTField, localByeTTField, localCancelTTField, remoteBusyTTField, remoteByeTTField, remoteCancelTTField}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

    callCompletionPanelLayout.setVerticalGroup(
        callCompletionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(org.jdesktop.layout.GroupLayout.TRAILING, callCompletionPanelLayout.createSequentialGroup()
            .add(callCompletionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(cancelTotalLabel)
                .add(busyTotalLabel)
                .add(byeTotalLabel))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(callCompletionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(localBusyTTField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
                .add(localByeTTField, 0, 0, Short.MAX_VALUE)
                .add(callCompletionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(localCancelTTField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(local2Label)))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(callCompletionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(org.jdesktop.layout.GroupLayout.TRAILING, callCompletionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(remoteCancelTTField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
                    .add(remote2Label))
                .add(org.jdesktop.layout.GroupLayout.TRAILING, remoteBusyTTField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .add(org.jdesktop.layout.GroupLayout.TRAILING, remoteByeTTField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
            .addContainerGap())
    );

    callCompletionPanelLayout.linkSize(new java.awt.Component[] {localBusyTTField, localByeTTField, localCancelTTField, remoteBusyTTField, remoteByeTTField, remoteCancelTTField}, org.jdesktop.layout.GroupLayout.VERTICAL);

    callInitPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Init", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("STHeiti", 0, 12), new java.awt.Color(51, 51, 51))); // NOI18N
    callInitPanel1.setToolTipText("");
    callInitPanel1.setFocusable(false);
    callInitPanel1.setMaximumSize(new java.awt.Dimension(223, 100));

    connectInitLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
    connectInitLabel.setForeground(new java.awt.Color(51, 51, 51));
    connectInitLabel.setText("Connect");

    tryingInitLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
    tryingInitLabel.setForeground(new java.awt.Color(51, 51, 51));
    tryingInitLabel.setText("Trying");

    tryingTTField.setBackground(new java.awt.Color(204, 204, 204));
    tryingTTField.setFont(new java.awt.Font("STHeiti", 0, 10));
    tryingTTField.setToolTipText("Phone Calls Counted");
    tryingTTField.setFocusTraversalKeysEnabled(false);
    tryingTTField.setMaximumSize(new java.awt.Dimension(60, 20));
    tryingTTField.setMinimumSize(new java.awt.Dimension(60, 20));
    tryingTTField.setPreferredSize(new java.awt.Dimension(60, 20));

    concurrentInitLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
    concurrentInitLabel.setForeground(new java.awt.Color(51, 51, 51));
    concurrentInitLabel.setText("Active");

    totalCountInitLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
    totalCountInitLabel.setForeground(new java.awt.Color(51, 51, 51));
    totalCountInitLabel.setText("Total");

    connectACField.setBackground(new java.awt.Color(204, 204, 204));
    connectACField.setFont(new java.awt.Font("STHeiti", 0, 10));
    connectACField.setToolTipText("Phone Connections");
    connectACField.setFocusTraversalKeysEnabled(false);
    connectACField.setMaximumSize(new java.awt.Dimension(60, 20));
    connectACField.setMinimumSize(new java.awt.Dimension(60, 20));
    connectACField.setPreferredSize(new java.awt.Dimension(60, 20));

    tryingACField.setBackground(new java.awt.Color(204, 204, 204));
    tryingACField.setFont(new java.awt.Font("STHeiti", 0, 10));
    tryingACField.setToolTipText("Phones Calling");
    tryingACField.setFocusTraversalKeysEnabled(false);
    tryingACField.setMaximumSize(new java.awt.Dimension(60, 20));
    tryingACField.setMinimumSize(new java.awt.Dimension(60, 20));
    tryingACField.setPreferredSize(new java.awt.Dimension(60, 20));

    connectTTField.setBackground(new java.awt.Color(204, 204, 204));
    connectTTField.setFont(new java.awt.Font("STHeiti", 0, 10));
    connectTTField.setToolTipText("Phone Connections Counted");
    connectTTField.setFocusTraversalKeysEnabled(false);
    connectTTField.setMaximumSize(new java.awt.Dimension(60, 20));
    connectTTField.setMinimumSize(new java.awt.Dimension(60, 20));
    connectTTField.setPreferredSize(new java.awt.Dimension(60, 20));

    callingTTField.setBackground(new java.awt.Color(204, 204, 204));
    callingTTField.setFont(new java.awt.Font("STHeiti", 0, 10));
    callingTTField.setToolTipText("Phone Calls Counted");
    callingTTField.setFocusTraversalKeysEnabled(false);
    callingTTField.setMaximumSize(new java.awt.Dimension(60, 20));
    callingTTField.setMinimumSize(new java.awt.Dimension(60, 20));
    callingTTField.setPreferredSize(new java.awt.Dimension(60, 20));

    callingInitLabel1.setFont(new java.awt.Font("STHeiti", 0, 10));
    callingInitLabel1.setForeground(new java.awt.Color(51, 51, 51));
    callingInitLabel1.setText("Calling");

    callingACField.setBackground(new java.awt.Color(204, 204, 204));
    callingACField.setFont(new java.awt.Font("STHeiti", 0, 10));
    callingACField.setToolTipText("Phones Calling");
    callingACField.setFocusTraversalKeysEnabled(false);
    callingACField.setMaximumSize(new java.awt.Dimension(60, 20));
    callingACField.setMinimumSize(new java.awt.Dimension(60, 20));
    callingACField.setPreferredSize(new java.awt.Dimension(60, 20));

    org.jdesktop.layout.GroupLayout callInitPanel1Layout = new org.jdesktop.layout.GroupLayout(callInitPanel1);
    callInitPanel1.setLayout(callInitPanel1Layout);
    callInitPanel1Layout.setHorizontalGroup(
        callInitPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(callInitPanel1Layout.createSequentialGroup()
            .addContainerGap()
            .add(callInitPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(totalCountInitLabel)
                .add(concurrentInitLabel))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(callInitPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                .add(callInitPanel1Layout.createSequentialGroup()
                    .add(callInitPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                        .add(connectTTField, 0, 0, Short.MAX_VALUE)
                        .add(connectACField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 54, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(callInitPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(tryingTTField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(tryingACField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(org.jdesktop.layout.GroupLayout.TRAILING, callInitPanel1Layout.createSequentialGroup()
                    .add(connectInitLabel)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(tryingInitLabel)
                    .add(17, 17, 17)))
            .add(callInitPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                .add(callInitPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(callInitPanel1Layout.createSequentialGroup()
                        .add(12, 12, 12)
                        .add(callingInitLabel1)
                        .add(20, 20, 20))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, callInitPanel1Layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 6, Short.MAX_VALUE)
                        .add(callingACField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(callInitPanel1Layout.createSequentialGroup()
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(callingTTField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
            .addContainerGap())
    );

    callInitPanel1Layout.linkSize(new java.awt.Component[] {callingTTField, connectACField, connectTTField, tryingACField, tryingTTField}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

    callInitPanel1Layout.setVerticalGroup(
        callInitPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(callInitPanel1Layout.createSequentialGroup()
            .add(callInitPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(callInitPanel1Layout.createSequentialGroup()
                    .add(17, 17, 17)
                    .add(callInitPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(connectACField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(tryingACField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(callingACField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(concurrentInitLabel))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(callInitPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(connectTTField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(tryingTTField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(callingTTField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(totalCountInitLabel)))
                .add(callInitPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(callingInitLabel1)
                    .add(tryingInitLabel)
                    .add(connectInitLabel)))
            .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    callInitPanel1Layout.linkSize(new java.awt.Component[] {callingACField, callingTTField, connectACField, connectTTField, tryingACField, tryingTTField}, org.jdesktop.layout.GroupLayout.VERTICAL);

    org.jdesktop.layout.GroupLayout campaignStatisticsInnerPanelLayout = new org.jdesktop.layout.GroupLayout(campaignStatisticsInnerPanel);
    campaignStatisticsInnerPanel.setLayout(campaignStatisticsInnerPanelLayout);
    campaignStatisticsInnerPanelLayout.setHorizontalGroup(
        campaignStatisticsInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(campaignStatisticsInnerPanelLayout.createSequentialGroup()
            .addContainerGap()
            .add(campaignStatisticsInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                .add(callInitPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(callInitPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(campaignStatisticsInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(callProgressPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                .add(callCompletionPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .addContainerGap())
    );

    campaignStatisticsInnerPanelLayout.linkSize(new java.awt.Component[] {callCompletionPanel, callProgressPanel}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

    campaignStatisticsInnerPanelLayout.setVerticalGroup(
        campaignStatisticsInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(campaignStatisticsInnerPanelLayout.createSequentialGroup()
            .add(campaignStatisticsInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(callInitPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(callProgressPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(campaignStatisticsInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                .add(callInitPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(callCompletionPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .addContainerGap())
    );

    campaignStatisticsInnerPanelLayout.linkSize(new java.awt.Component[] {callCompletionPanel, callInitPanel, callInitPanel1, callProgressPanel}, org.jdesktop.layout.GroupLayout.VERTICAL);

    org.jdesktop.layout.GroupLayout campaignStatsPanelLayout = new org.jdesktop.layout.GroupLayout(campaignStatsPanel);
    campaignStatsPanel.setLayout(campaignStatsPanelLayout);
    campaignStatsPanelLayout.setHorizontalGroup(
        campaignStatsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(campaignStatsPanelLayout.createSequentialGroup()
            .addContainerGap()
            .add(campaignStatsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                .add(deleteCampaignStatsButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(updateCampaignStatsButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(insertCampaignStatsButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(selectCampaignStatsButton))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(campaignStatisticsInnerPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addContainerGap())
    );

    campaignStatsPanelLayout.linkSize(new java.awt.Component[] {deleteCampaignStatsButton, insertCampaignStatsButton, selectCampaignStatsButton, updateCampaignStatsButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

    campaignStatsPanelLayout.setVerticalGroup(
        campaignStatsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(campaignStatsPanelLayout.createSequentialGroup()
            .addContainerGap()
            .add(selectCampaignStatsButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(insertCampaignStatsButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(updateCampaignStatsButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(deleteCampaignStatsButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addContainerGap(154, Short.MAX_VALUE))
        .add(campaignStatsPanelLayout.createSequentialGroup()
            .add(campaignStatisticsInnerPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(59, 59, 59))
    );

    campaignStatsPanelLayout.linkSize(new java.awt.Component[] {deleteCampaignStatsButton, insertCampaignStatsButton, selectCampaignStatsButton, updateCampaignStatsButton}, org.jdesktop.layout.GroupLayout.VERTICAL);

    backofficeTab.addTab("CampaignStats", campaignStatsPanel);

    pricelistPanel.setToolTipText("");
    pricelistPanel.setFocusTraversalKeysEnabled(false);
    pricelistPanel.setFont(new java.awt.Font("STHeiti", 0, 13));
    pricelistPanel.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyPressed(java.awt.event.KeyEvent evt) {
            pricelistPanelKeyPressed(evt);
        }
    });

    selectPricelistButton.setFont(new java.awt.Font("STHeiti", 0, 10));
    selectPricelistButton.setText("Select");
    selectPricelistButton.setToolTipText("Select Pricelist");
    selectPricelistButton.setFocusable(false);
    selectPricelistButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            selectPricelistButtonActionPerformed(evt);
        }
    });

    insertPricelistButton.setFont(new java.awt.Font("STHeiti", 0, 10));
    insertPricelistButton.setText("Insert");
    insertPricelistButton.setToolTipText("Insert Pricelist");
    insertPricelistButton.setFocusable(false);
    insertPricelistButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            insertPricelistButtonActionPerformed(evt);
        }
    });

    updatePricelistButton.setFont(new java.awt.Font("STHeiti", 0, 10));
    updatePricelistButton.setText("Update");
    updatePricelistButton.setToolTipText("Update Pricelist");
    updatePricelistButton.setFocusable(false);
    updatePricelistButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            updatePricelistButtonActionPerformed(evt);
        }
    });

    deletePricelistButton.setFont(new java.awt.Font("STHeiti", 0, 10));
    deletePricelistButton.setText("Delete");
    deletePricelistButton.setToolTipText("Delete Pricelist");
    deletePricelistButton.setFocusable(false);
    deletePricelistButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            deletePricelistButtonActionPerformed(evt);
        }
    });

    pricelistInnerPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Price per Message/Sec", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("STHeiti", 0, 14), new java.awt.Color(51, 51, 51))); // NOI18N
    pricelistInnerPanel.setToolTipText("");

    daytimeLabel.setFont(new java.awt.Font("STHeiti", 0, 14));
    daytimeLabel.setForeground(new java.awt.Color(51, 51, 51));
    daytimeLabel.setText("Daytime");

    eveningLabel.setFont(new java.awt.Font("STHeiti", 0, 14));
    eveningLabel.setForeground(new java.awt.Color(51, 51, 51));
    eveningLabel.setText("Evening");

    b2bLabel.setFont(new java.awt.Font("STHeiti", 0, 14));
    b2bLabel.setForeground(new java.awt.Color(51, 51, 51));
    b2bLabel.setText("Business to Business");

    b2cLabel.setFont(new java.awt.Font("STHeiti", 0, 14));
    b2cLabel.setForeground(new java.awt.Color(51, 51, 51));
    b2cLabel.setText("Bus. to Consumers");

    freeLecectLabel.setFont(new java.awt.Font("STHeiti", 0, 14));
    freeLecectLabel.setForeground(new java.awt.Color(51, 51, 51));
    freeLecectLabel.setText("Any to Specific");

    pricelistB2BDaytimePerSecondField.setFont(new java.awt.Font("STHeiti", 0, 14));
    pricelistB2BDaytimePerSecondField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    pricelistB2BDaytimePerSecondField.setText("0.01");
    pricelistB2BDaytimePerSecondField.setToolTipText("");

    pricelistB2BEveningPerSecondField.setFont(new java.awt.Font("STHeiti", 0, 14));
    pricelistB2BEveningPerSecondField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    pricelistB2BEveningPerSecondField.setText("0.01");
    pricelistB2BEveningPerSecondField.setToolTipText("");

    pricelistB2CDaytimePerSecondField.setFont(new java.awt.Font("STHeiti", 0, 14));
    pricelistB2CDaytimePerSecondField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    pricelistB2CDaytimePerSecondField.setText("0.001");
    pricelistB2CDaytimePerSecondField.setToolTipText("");

    pricelistB2CEveningPerSecondField.setFont(new java.awt.Font("STHeiti", 0, 14));
    pricelistB2CEveningPerSecondField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    pricelistB2CEveningPerSecondField.setText("0.005");
    pricelistB2CEveningPerSecondField.setToolTipText("");

    pricelistA2SDaytimePerSecondField.setFont(new java.awt.Font("STHeiti", 0, 14));
    pricelistA2SDaytimePerSecondField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    pricelistA2SDaytimePerSecondField.setText("0.01");
    pricelistA2SDaytimePerSecondField.setToolTipText("");

    pricelistA2SEveningPerSecondField.setFont(new java.awt.Font("STHeiti", 0, 14));
    pricelistA2SEveningPerSecondField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    pricelistA2SEveningPerSecondField.setText("0.01");
    pricelistA2SEveningPerSecondField.setToolTipText("");

    euroSignLabel.setFont(new java.awt.Font("STHeiti", 0, 14));
    euroSignLabel.setForeground(new java.awt.Color(51, 51, 51));
    euroSignLabel.setText("€");

    euroSignLabel1.setFont(new java.awt.Font("STHeiti", 0, 14));
    euroSignLabel1.setForeground(new java.awt.Color(51, 51, 51));
    euroSignLabel1.setText("€");

    euroSignLabel2.setFont(new java.awt.Font("STHeiti", 0, 14));
    euroSignLabel2.setForeground(new java.awt.Color(51, 51, 51));
    euroSignLabel2.setText("€");

    euroSignLabel3.setFont(new java.awt.Font("STHeiti", 0, 14));
    euroSignLabel3.setForeground(new java.awt.Color(51, 51, 51));
    euroSignLabel3.setText("€");

    euroSignLabel4.setFont(new java.awt.Font("STHeiti", 0, 14));
    euroSignLabel4.setForeground(new java.awt.Color(51, 51, 51));
    euroSignLabel4.setText("€");

    euroSignLabel5.setFont(new java.awt.Font("STHeiti", 0, 14));
    euroSignLabel5.setForeground(new java.awt.Color(51, 51, 51));
    euroSignLabel5.setText("€");

    daytimeLabel1.setFont(new java.awt.Font("STHeiti", 0, 14));
    daytimeLabel1.setForeground(new java.awt.Color(102, 102, 102));
    daytimeLabel1.setText("/Sec");

    daytimeLabel2.setFont(new java.awt.Font("STHeiti", 0, 14));
    daytimeLabel2.setForeground(new java.awt.Color(102, 102, 102));
    daytimeLabel2.setText("/Sec");

    daytimeLabel3.setFont(new java.awt.Font("STHeiti", 0, 14));
    daytimeLabel3.setForeground(new java.awt.Color(102, 102, 102));
    daytimeLabel3.setText("/Sec");

    daytimeLabel4.setFont(new java.awt.Font("STHeiti", 0, 14));
    daytimeLabel4.setForeground(new java.awt.Color(102, 102, 102));
    daytimeLabel4.setText("/Sec");

    daytimeLabel5.setFont(new java.awt.Font("STHeiti", 0, 14));
    daytimeLabel5.setForeground(new java.awt.Color(102, 102, 102));
    daytimeLabel5.setText("/Sec");

    daytimeLabel6.setFont(new java.awt.Font("STHeiti", 0, 14));
    daytimeLabel6.setForeground(new java.awt.Color(102, 102, 102));
    daytimeLabel6.setText("/Sec");

    org.jdesktop.layout.GroupLayout pricelistInnerPanelLayout = new org.jdesktop.layout.GroupLayout(pricelistInnerPanel);
    pricelistInnerPanel.setLayout(pricelistInnerPanelLayout);
    pricelistInnerPanelLayout.setHorizontalGroup(
        pricelistInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(pricelistInnerPanelLayout.createSequentialGroup()
            .addContainerGap()
            .add(pricelistInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(freeLecectLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 128, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(b2cLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 128, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(b2bLabel))
            .add(18, 18, 18)
            .add(pricelistInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(pricelistInnerPanelLayout.createSequentialGroup()
                    .add(euroSignLabel2)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(pricelistA2SDaytimePerSecondField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(pricelistInnerPanelLayout.createSequentialGroup()
                    .add(euroSignLabel)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(pricelistInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(pricelistB2BDaytimePerSecondField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(daytimeLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(pricelistInnerPanelLayout.createSequentialGroup()
                    .add(euroSignLabel1)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(pricelistB2CDaytimePerSecondField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(pricelistInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(daytimeLabel1)
                .add(daytimeLabel2)
                .add(daytimeLabel3))
            .add(31, 31, 31)
            .add(pricelistInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                .add(euroSignLabel3)
                .add(euroSignLabel4)
                .add(euroSignLabel5))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(pricelistInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                .add(eveningLabel)
                .add(pricelistB2BEveningPerSecondField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 63, Short.MAX_VALUE)
                .add(pricelistB2CEveningPerSecondField)
                .add(pricelistA2SEveningPerSecondField))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(pricelistInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(daytimeLabel6)
                .add(daytimeLabel4)
                .add(daytimeLabel5))
            .add(65, 65, 65))
    );
    pricelistInnerPanelLayout.setVerticalGroup(
        pricelistInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(pricelistInnerPanelLayout.createSequentialGroup()
            .addContainerGap()
            .add(pricelistInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                .add(pricelistInnerPanelLayout.createSequentialGroup()
                    .add(pricelistInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                        .add(pricelistInnerPanelLayout.createSequentialGroup()
                            .add(daytimeLabel6)
                            .add(40, 40, 40))
                        .add(pricelistInnerPanelLayout.createSequentialGroup()
                            .add(eveningLabel)
                            .add(12, 12, 12)
                            .add(pricelistInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                .add(pricelistB2BEveningPerSecondField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(euroSignLabel3))
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(pricelistInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                .add(pricelistB2CEveningPerSecondField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(euroSignLabel4)
                                .add(daytimeLabel5))))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(pricelistInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(pricelistA2SEveningPerSecondField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(euroSignLabel5)
                        .add(daytimeLabel4)))
                .add(pricelistInnerPanelLayout.createSequentialGroup()
                    .add(daytimeLabel)
                    .add(12, 12, 12)
                    .add(pricelistInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(pricelistB2BDaytimePerSecondField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(euroSignLabel)
                        .add(daytimeLabel1)
                        .add(b2bLabel))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(pricelistInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(pricelistB2CDaytimePerSecondField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(euroSignLabel1)
                        .add(daytimeLabel2)
                        .add(b2cLabel))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(pricelistInnerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(euroSignLabel2)
                        .add(pricelistA2SDaytimePerSecondField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(daytimeLabel3)
                        .add(freeLecectLabel))))
            .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    org.jdesktop.layout.GroupLayout pricelistPanelLayout = new org.jdesktop.layout.GroupLayout(pricelistPanel);
    pricelistPanel.setLayout(pricelistPanelLayout);
    pricelistPanelLayout.setHorizontalGroup(
        pricelistPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(pricelistPanelLayout.createSequentialGroup()
            .addContainerGap()
            .add(pricelistPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                .add(selectPricelistButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(insertPricelistButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(updatePricelistButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(deletePricelistButton))
            .add(49, 49, 49)
            .add(pricelistInnerPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(103, 103, 103))
    );

    pricelistPanelLayout.linkSize(new java.awt.Component[] {deletePricelistButton, insertPricelistButton, selectPricelistButton, updatePricelistButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

    pricelistPanelLayout.setVerticalGroup(
        pricelistPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(pricelistPanelLayout.createSequentialGroup()
            .addContainerGap()
            .add(pricelistPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(pricelistInnerPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(pricelistPanelLayout.createSequentialGroup()
                    .add(selectPricelistButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(insertPricelistButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(updatePricelistButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(deletePricelistButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
            .addContainerGap(102, Short.MAX_VALUE))
    );

    pricelistPanelLayout.linkSize(new java.awt.Component[] {deletePricelistButton, insertPricelistButton, selectPricelistButton, updatePricelistButton}, org.jdesktop.layout.GroupLayout.VERTICAL);

    backofficeTab.addTab("Pricelist", pricelistPanel);

    resellerPanel.setToolTipText("");
    resellerPanel.setFocusTraversalKeysEnabled(false);
    resellerPanel.setFont(new java.awt.Font("STHeiti", 0, 13));

    selectResellerButton.setFont(new java.awt.Font("STHeiti", 0, 10));
    selectResellerButton.setText("Select");
    selectResellerButton.setToolTipText("Select Reseller");
    selectResellerButton.setEnabled(false);
    selectResellerButton.setFocusTraversalKeysEnabled(false);
    selectResellerButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            selectResellerButtonActionPerformed(evt);
        }
    });
    selectResellerButton.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyPressed(java.awt.event.KeyEvent evt) {
            selectResellerButtonKeyPressed(evt);
        }
    });

    insertResellerButton.setFont(new java.awt.Font("STHeiti", 0, 10));
    insertResellerButton.setText("Insert");
    insertResellerButton.setToolTipText("Insert Reseller");
    insertResellerButton.setEnabled(false);
    insertResellerButton.setFocusTraversalKeysEnabled(false);
    insertResellerButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            insertResellerButtonActionPerformed(evt);
        }
    });
    insertResellerButton.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyPressed(java.awt.event.KeyEvent evt) {
            insertResellerButtonKeyPressed(evt);
        }
    });

    updateResellerButton.setFont(new java.awt.Font("STHeiti", 0, 10));
    updateResellerButton.setText("Update");
    updateResellerButton.setToolTipText("Update Reseller");
    updateResellerButton.setEnabled(false);
    updateResellerButton.setFocusTraversalKeysEnabled(false);
    updateResellerButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            updateResellerButtonActionPerformed(evt);
        }
    });
    updateResellerButton.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyPressed(java.awt.event.KeyEvent evt) {
            updateResellerButtonKeyPressed(evt);
        }
    });

    deleteResellerButton.setFont(new java.awt.Font("STHeiti", 0, 10));
    deleteResellerButton.setText("Delete");
    deleteResellerButton.setToolTipText("Delete Reseller");
    deleteResellerButton.setEnabled(false);
    deleteResellerButton.setFocusTraversalKeysEnabled(false);
    deleteResellerButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            deleteResellerButtonActionPerformed(evt);
        }
    });
    deleteResellerButton.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyPressed(java.awt.event.KeyEvent evt) {
            deleteResellerButtonKeyPressed(evt);
        }
    });

    officeInformationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Office Information", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("STHeiti", 0, 14), new java.awt.Color(51, 51, 51))); // NOI18N
    officeInformationPanel.setToolTipText("");

    officeIdLabel.setFont(new java.awt.Font("STHeiti", 0, 12));
    officeIdLabel.setForeground(new java.awt.Color(51, 51, 51));
    officeIdLabel.setText("Id / Date");

    officeNameLabel.setFont(new java.awt.Font("STHeiti", 0, 12));
    officeNameLabel.setForeground(new java.awt.Color(51, 51, 51));
    officeNameLabel.setText("Name");

    officeAddressLabel.setFont(new java.awt.Font("STHeiti", 0, 12));
    officeAddressLabel.setForeground(new java.awt.Color(51, 51, 51));
    officeAddressLabel.setText("Address");

    officeAddressNrLabel.setFont(new java.awt.Font("STHeiti", 0, 12));
    officeAddressNrLabel.setForeground(new java.awt.Color(51, 51, 51));
    officeAddressNrLabel.setText("Nr");

    officePostcodeLabel.setFont(new java.awt.Font("STHeiti", 0, 12));
    officePostcodeLabel.setForeground(new java.awt.Color(51, 51, 51));
    officePostcodeLabel.setText("Postcode");

    officeCityLabel.setFont(new java.awt.Font("STHeiti", 0, 12));
    officeCityLabel.setForeground(new java.awt.Color(51, 51, 51));
    officeCityLabel.setText("City");

    officeCountryLabel.setFont(new java.awt.Font("STHeiti", 0, 12));
    officeCountryLabel.setForeground(new java.awt.Color(51, 51, 51));
    officeCountryLabel.setText("Country");
    officeCountryLabel.setToolTipText("");

    resellerIdField.setFont(new java.awt.Font("STHeiti", 0, 10));
    resellerIdField.setText("0");
    resellerIdField.setToolTipText("Reseller Id");
    resellerIdField.setFocusCycleRoot(true);
    resellerIdField.setFocusTraversalKeysEnabled(false);
    resellerIdField.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyPressed(java.awt.event.KeyEvent evt) {
            resellerIdFieldKeyPressed(evt);
        }
        public void keyReleased(java.awt.event.KeyEvent evt) {
            resellerIdFieldKeyReleased(evt);
        }
    });

    resellerDateField.setFont(new java.awt.Font("STHeiti", 0, 10));
    resellerDateField.setToolTipText("Creation Date Epoch Format");
    resellerDateField.setFocusCycleRoot(true);
    resellerDateField.setFocusTraversalKeysEnabled(false);
    resellerDateField.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyPressed(java.awt.event.KeyEvent evt) {
            resellerDateFieldKeyPressed(evt);
        }
    });

    resellerCompanyNameField.setFont(new java.awt.Font("STHeiti", 0, 10));
    resellerCompanyNameField.setToolTipText("");
    resellerCompanyNameField.setFocusTraversalKeysEnabled(false);
    resellerCompanyNameField.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyPressed(java.awt.event.KeyEvent evt) {
            resellerCompanyNameFieldKeyPressed(evt);
        }
        public void keyReleased(java.awt.event.KeyEvent evt) {
            resellerCompanyNameFieldKeyReleased(evt);
        }
    });

    resellerAddressField.setFont(new java.awt.Font("STHeiti", 0, 10));
    resellerAddressField.setToolTipText("");
    resellerAddressField.setFocusTraversalKeysEnabled(false);
    resellerAddressField.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyPressed(java.awt.event.KeyEvent evt) {
            resellerAddressFieldKeyPressed(evt);
        }
        public void keyReleased(java.awt.event.KeyEvent evt) {
            resellerAddressFieldKeyReleased(evt);
        }
    });

    resellerAddressNrField.setFont(new java.awt.Font("STHeiti", 0, 10));
    resellerAddressNrField.setToolTipText("");
    resellerAddressNrField.setFocusTraversalKeysEnabled(false);
    resellerAddressNrField.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyPressed(java.awt.event.KeyEvent evt) {
            resellerAddressNrFieldKeyPressed(evt);
        }
        public void keyReleased(java.awt.event.KeyEvent evt) {
            resellerAddressNrFieldKeyReleased(evt);
        }
    });

    resellerPostcodeField.setFont(new java.awt.Font("STHeiti", 0, 10));
    resellerPostcodeField.setToolTipText("");
    resellerPostcodeField.setFocusTraversalKeysEnabled(false);
    resellerPostcodeField.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyPressed(java.awt.event.KeyEvent evt) {
            resellerPostcodeFieldKeyPressed(evt);
        }
        public void keyReleased(java.awt.event.KeyEvent evt) {
            resellerPostcodeFieldKeyReleased(evt);
        }
    });

    resellerCityField.setFont(new java.awt.Font("STHeiti", 0, 10));
    resellerCityField.setToolTipText("");
    resellerCityField.setFocusTraversalKeysEnabled(false);
    resellerCityField.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            resellerCityFieldActionPerformed(evt);
        }
    });
    resellerCityField.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyPressed(java.awt.event.KeyEvent evt) {
            resellerCityFieldKeyPressed(evt);
        }
        public void keyReleased(java.awt.event.KeyEvent evt) {
            resellerCityFieldKeyReleased(evt);
        }
    });

    resellerCountryField.setFont(new java.awt.Font("STHeiti", 0, 10));
    resellerCountryField.setToolTipText("");
    resellerCountryField.setFocusTraversalKeysEnabled(false);
    resellerCountryField.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyPressed(java.awt.event.KeyEvent evt) {
            resellerCountryFieldKeyPressed(evt);
        }
        public void keyReleased(java.awt.event.KeyEvent evt) {
            resellerCountryFieldKeyReleased(evt);
        }
    });

    org.jdesktop.layout.GroupLayout officeInformationPanelLayout = new org.jdesktop.layout.GroupLayout(officeInformationPanel);
    officeInformationPanel.setLayout(officeInformationPanelLayout);
    officeInformationPanelLayout.setHorizontalGroup(
        officeInformationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(officeInformationPanelLayout.createSequentialGroup()
            .addContainerGap()
            .add(officeInformationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(officeInformationPanelLayout.createSequentialGroup()
                    .add(officeIdLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 56, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(resellerIdField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 71, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(resellerDateField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 85, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(officeInformationPanelLayout.createSequentialGroup()
                    .add(officeInformationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(officeNameLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 56, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(officeAddressLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 56, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(officePostcodeLabel)
                        .add(officeCountryLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 56, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(officeInformationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(resellerCompanyNameField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE)
                        .add(officeInformationPanelLayout.createSequentialGroup()
                            .add(resellerAddressField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(officeAddressNrLabel)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(resellerAddressNrField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE))
                        .add(officeInformationPanelLayout.createSequentialGroup()
                            .add(resellerPostcodeField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 63, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(officeCityLabel)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(resellerCityField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 65, Short.MAX_VALUE))
                        .add(resellerCountryField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE))))
            .addContainerGap())
    );
    officeInformationPanelLayout.setVerticalGroup(
        officeInformationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(officeInformationPanelLayout.createSequentialGroup()
            .add(officeInformationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(resellerIdField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(officeIdLabel)
                .add(resellerDateField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(officeInformationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(officeNameLabel)
                .add(resellerCompanyNameField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(officeInformationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(officeAddressLabel)
                .add(resellerAddressField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(officeAddressNrLabel)
                .add(resellerAddressNrField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(officeInformationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(officePostcodeLabel)
                .add(resellerPostcodeField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(resellerCityField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(officeCityLabel))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(officeInformationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(officeCountryLabel)
                .add(resellerCountryField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    resellerInformationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Personal Information", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("STHeiti", 0, 14), new java.awt.Color(51, 51, 51))); // NOI18N
    resellerInformationPanel.setToolTipText("");

    resellerNameLabel.setFont(new java.awt.Font("STHeiti", 0, 12));
    resellerNameLabel.setForeground(new java.awt.Color(51, 51, 51));
    resellerNameLabel.setText("Full Name");

    resellerEmailLabel.setFont(new java.awt.Font("STHeiti", 0, 12));
    resellerEmailLabel.setForeground(new java.awt.Color(51, 51, 51));
    resellerEmailLabel.setText("Email");

    resellerPhoneLabel.setFont(new java.awt.Font("STHeiti", 0, 12));
    resellerPhoneLabel.setForeground(new java.awt.Color(51, 51, 51));
    resellerPhoneLabel.setText("Phone");

    resellerMobileLabel.setFont(new java.awt.Font("STHeiti", 0, 12));
    resellerMobileLabel.setForeground(new java.awt.Color(51, 51, 51));
    resellerMobileLabel.setText("Mobile");

    resellerDiscountLabel.setFont(new java.awt.Font("STHeiti", 0, 12));
    resellerDiscountLabel.setForeground(new java.awt.Color(51, 51, 51));
    resellerDiscountLabel.setText("Discount");

    resellerContactNameField.setFont(new java.awt.Font("STHeiti", 0, 10));
    resellerContactNameField.setToolTipText("");
    resellerContactNameField.setFocusTraversalKeysEnabled(false);
    resellerContactNameField.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyPressed(java.awt.event.KeyEvent evt) {
            resellerContactNameFieldKeyPressed(evt);
        }
        public void keyReleased(java.awt.event.KeyEvent evt) {
            resellerContactNameFieldKeyReleased(evt);
        }
    });

    resellerEmailField.setFont(new java.awt.Font("STHeiti", 0, 10));
    resellerEmailField.setToolTipText("");
    resellerEmailField.setFocusTraversalKeysEnabled(false);
    resellerEmailField.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyPressed(java.awt.event.KeyEvent evt) {
            resellerEmailFieldKeyPressed(evt);
        }
        public void keyReleased(java.awt.event.KeyEvent evt) {
            resellerEmailFieldKeyReleased(evt);
        }
    });

    resellerPhoneNrField.setFont(new java.awt.Font("STHeiti", 0, 10));
    resellerPhoneNrField.setToolTipText("");
    resellerPhoneNrField.setFocusTraversalKeysEnabled(false);
    resellerPhoneNrField.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyPressed(java.awt.event.KeyEvent evt) {
            resellerPhoneNrFieldKeyPressed(evt);
        }
        public void keyReleased(java.awt.event.KeyEvent evt) {
            resellerPhoneNrFieldKeyReleased(evt);
        }
    });

    resellerMobileNrField.setFont(new java.awt.Font("STHeiti", 0, 10));
    resellerMobileNrField.setToolTipText("");
    resellerMobileNrField.setFocusTraversalKeysEnabled(false);
    resellerMobileNrField.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyPressed(java.awt.event.KeyEvent evt) {
            resellerMobileNrFieldKeyPressed(evt);
        }
        public void keyReleased(java.awt.event.KeyEvent evt) {
            resellerMobileNrFieldKeyReleased(evt);
        }
    });

    resellerDiscountField.setFont(new java.awt.Font("STHeiti", 0, 10));
    resellerDiscountField.setToolTipText("");
    resellerDiscountField.setFocusTraversalKeysEnabled(false);
    resellerDiscountField.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyPressed(java.awt.event.KeyEvent evt) {
            resellerDiscountFieldKeyPressed(evt);
        }
        public void keyReleased(java.awt.event.KeyEvent evt) {
            resellerDiscountFieldKeyReleased(evt);
        }
    });

    org.jdesktop.layout.GroupLayout resellerInformationPanelLayout = new org.jdesktop.layout.GroupLayout(resellerInformationPanel);
    resellerInformationPanel.setLayout(resellerInformationPanelLayout);
    resellerInformationPanelLayout.setHorizontalGroup(
        resellerInformationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(resellerInformationPanelLayout.createSequentialGroup()
            .addContainerGap()
            .add(resellerInformationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                .add(resellerEmailLabel)
                .add(resellerNameLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(resellerPhoneLabel)
                .add(resellerMobileLabel)
                .add(resellerDiscountLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .add(resellerInformationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(resellerInformationPanelLayout.createSequentialGroup()
                    .add(12, 12, 12)
                    .add(resellerInformationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(resellerPhoneNrField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
                        .add(resellerMobileNrField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
                        .add(resellerDiscountField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)))
                .add(resellerInformationPanelLayout.createSequentialGroup()
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                    .add(resellerInformationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, resellerEmailField)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, resellerContactNameField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE))))
            .addContainerGap())
    );
    resellerInformationPanelLayout.setVerticalGroup(
        resellerInformationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(resellerInformationPanelLayout.createSequentialGroup()
            .add(resellerInformationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(resellerNameLabel)
                .add(resellerContactNameField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(resellerInformationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(resellerEmailField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(resellerEmailLabel))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(resellerInformationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(resellerPhoneNrField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(resellerPhoneLabel))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(resellerInformationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(resellerMobileNrField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(resellerMobileLabel))
            .add(resellerInformationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(resellerInformationPanelLayout.createSequentialGroup()
                    .add(12, 12, 12)
                    .add(resellerDiscountLabel))
                .add(resellerInformationPanelLayout.createSequentialGroup()
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(resellerDiscountField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
            .addContainerGap())
    );

    clearResellerFieldsButton.setFont(new java.awt.Font("STHeiti", 0, 10));
    clearResellerFieldsButton.setText("Clear");
    clearResellerFieldsButton.setToolTipText("Clear Fields");
    clearResellerFieldsButton.setEnabled(false);
    clearResellerFieldsButton.setFocusTraversalKeysEnabled(false);
    clearResellerFieldsButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            clearResellerFieldsButtonActionPerformed(evt);
        }
    });

    exampleResellerButton.setFont(new java.awt.Font("STHeiti", 0, 10));
    exampleResellerButton.setText("Example");
    exampleResellerButton.setToolTipText("Example Reseller");
    exampleResellerButton.setFocusTraversalKeysEnabled(false);
    exampleResellerButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            exampleResellerButtonActionPerformed(evt);
        }
    });

    searchResellerButton.setFont(new java.awt.Font("STHeiti", 0, 10));
    searchResellerButton.setText("Search");
    searchResellerButton.setToolTipText("Search Reseller");
    searchResellerButton.setEnabled(false);
    searchResellerButton.setFocusTraversalKeysEnabled(false);

    org.jdesktop.layout.GroupLayout resellerPanelLayout = new org.jdesktop.layout.GroupLayout(resellerPanel);
    resellerPanel.setLayout(resellerPanelLayout);
    resellerPanelLayout.setHorizontalGroup(
        resellerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(org.jdesktop.layout.GroupLayout.TRAILING, resellerPanelLayout.createSequentialGroup()
            .addContainerGap()
            .add(resellerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(selectResellerButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                .add(insertResellerButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                .add(updateResellerButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                .add(deleteResellerButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                .add(clearResellerFieldsButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                .add(exampleResellerButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                .add(searchResellerButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE))
            .add(12, 12, 12)
            .add(officeInformationPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(resellerInformationPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(48, 48, 48))
    );

    resellerPanelLayout.linkSize(new java.awt.Component[] {clearResellerFieldsButton, deleteResellerButton, exampleResellerButton, insertResellerButton, searchResellerButton, selectResellerButton, updateResellerButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

    resellerPanelLayout.setVerticalGroup(
        resellerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(resellerPanelLayout.createSequentialGroup()
            .addContainerGap()
            .add(resellerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(resellerPanelLayout.createSequentialGroup()
                    .add(officeInformationPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap())
                .add(resellerPanelLayout.createSequentialGroup()
                    .add(resellerInformationPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
                .add(resellerPanelLayout.createSequentialGroup()
                    .add(clearResellerFieldsButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(exampleResellerButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(searchResellerButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(selectResellerButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(insertResellerButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(updateResellerButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(deleteResellerButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
    );

    resellerPanelLayout.linkSize(new java.awt.Component[] {clearResellerFieldsButton, deleteResellerButton, exampleResellerButton, insertResellerButton, searchResellerButton, selectResellerButton, updateResellerButton}, org.jdesktop.layout.GroupLayout.VERTICAL);

    resellerPanelLayout.linkSize(new java.awt.Component[] {officeInformationPanel, resellerInformationPanel}, org.jdesktop.layout.GroupLayout.VERTICAL);

    backofficeTab.addTab("Reseller", resellerPanel);

    maintenancePanel.setBackground(new java.awt.Color(0, 0, 0));
    maintenancePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("STHeiti", 0, 10), new java.awt.Color(255, 255, 255))); // NOI18N

    headerLabel.setFont(new java.awt.Font("STHeiti", 0, 14));
    headerLabel.setForeground(new java.awt.Color(255, 255, 255));
    headerLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    headerLabel.setText("Database Maintenance");
    headerLabel.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            headerLabelMouseClicked(evt);
        }
    });

    customerTableLabel.setFont(new java.awt.Font("STHeiti", 0, 12));
    customerTableLabel.setForeground(new java.awt.Color(255, 153, 0));
    customerTableLabel.setText("Customer");

    customerTableLabel1.setFont(new java.awt.Font("STHeiti", 0, 12));
    customerTableLabel1.setForeground(new java.awt.Color(255, 153, 0));
    customerTableLabel1.setText("Order");

    customerTableLabel2.setFont(new java.awt.Font("STHeiti", 0, 12));
    customerTableLabel2.setForeground(new java.awt.Color(255, 153, 0));
    customerTableLabel2.setText("Invoice");

    customerTableLabel3.setFont(new java.awt.Font("STHeiti", 0, 12));
    customerTableLabel3.setForeground(new java.awt.Color(255, 153, 0));
    customerTableLabel3.setText("Campaign");

    customerTableLabel4.setFont(new java.awt.Font("STHeiti", 0, 12));
    customerTableLabel4.setForeground(new java.awt.Color(0, 204, 51));
    customerTableLabel4.setText("Destination");

    customerTableLabel5.setFont(new java.awt.Font("STHeiti", 0, 12));
    customerTableLabel5.setForeground(new java.awt.Color(255, 153, 0));
    customerTableLabel5.setText("CampaignStats");

    customerTableLabel6.setFont(new java.awt.Font("STHeiti", 0, 12));
    customerTableLabel6.setForeground(new java.awt.Color(255, 153, 0));
    customerTableLabel6.setText("Pricelist");

    customerTableLabel7.setFont(new java.awt.Font("STHeiti", 0, 12));
    customerTableLabel7.setForeground(new java.awt.Color(255, 153, 0));
    customerTableLabel7.setText("Reseller");

    dropCustomerTableButton.setFont(new java.awt.Font("STHeiti", 0, 10));
    dropCustomerTableButton.setText("Delete Data");
    dropCustomerTableButton.setToolTipText("DB Control be Carefull!!!");
    dropCustomerTableButton.setFocusTraversalKeysEnabled(false);
    dropCustomerTableButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            dropCustomerTableButtonActionPerformed(evt);
        }
    });
    dropCustomerTableButton.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyPressed(java.awt.event.KeyEvent evt) {
            dropCustomerTableButtonKeyPressed(evt);
        }
    });

    dropOrderTableButton.setFont(new java.awt.Font("STHeiti", 0, 10));
    dropOrderTableButton.setText("Delete Data");
    dropOrderTableButton.setToolTipText("DB Control be Carefull!!!");
    dropOrderTableButton.setFocusable(false);
    dropOrderTableButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            dropOrderTableButtonActionPerformed(evt);
        }
    });

    dropInvoiceTableButton.setFont(new java.awt.Font("STHeiti", 0, 10));
    dropInvoiceTableButton.setText("Delete Data");
    dropInvoiceTableButton.setToolTipText("DB Control be Carefull!!!");
    dropInvoiceTableButton.setFocusable(false);
    dropInvoiceTableButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            dropInvoiceTableButtonActionPerformed(evt);
        }
    });

    dropCampaignTableButton.setFont(new java.awt.Font("STHeiti", 0, 10));
    dropCampaignTableButton.setText("Delete Data");
    dropCampaignTableButton.setToolTipText("DB Control be Carefull!!!");
    dropCampaignTableButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            dropCampaignTableButtonActionPerformed(evt);
        }
    });

    dropDestinationTableButton.setFont(new java.awt.Font("STHeiti", 0, 10));
    dropDestinationTableButton.setText("Delete Data");
    dropDestinationTableButton.setToolTipText("DB Control be Carefull!!!");
    dropDestinationTableButton.setFocusable(false);
    dropDestinationTableButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            dropDestinationTableButtonActionPerformed(evt);
        }
    });

    dropCampaignStatsTableButton.setFont(new java.awt.Font("STHeiti", 0, 10));
    dropCampaignStatsTableButton.setText("Delete Data");
    dropCampaignStatsTableButton.setToolTipText("DB Control be Carefull!!!");
    dropCampaignStatsTableButton.setFocusable(false);
    dropCampaignStatsTableButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            dropCampaignStatsTableButtonActionPerformed(evt);
        }
    });

    dropPricelistTableButton.setFont(new java.awt.Font("STHeiti", 0, 10));
    dropPricelistTableButton.setText("Delete Data");
    dropPricelistTableButton.setToolTipText("DB Control be Carefull!!!");
    dropPricelistTableButton.setFocusable(false);
    dropPricelistTableButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            dropPricelistTableButtonActionPerformed(evt);
        }
    });

    dropResellerTableButton.setFont(new java.awt.Font("STHeiti", 0, 10));
    dropResellerTableButton.setText("Delete Data");
    dropResellerTableButton.setToolTipText("DB Control be Carefull!!!");
    dropResellerTableButton.setFocusTraversalKeysEnabled(false);
    dropResellerTableButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            dropResellerTableButtonActionPerformed(evt);
        }
    });
    dropResellerTableButton.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyPressed(java.awt.event.KeyEvent evt) {
            dropResellerTableButtonKeyPressed(evt);
        }
    });

    customerTableLabel8.setFont(new java.awt.Font("STHeiti", 0, 12));
    customerTableLabel8.setForeground(new java.awt.Color(255, 0, 51));
    customerTableLabel8.setText("Database");

    dropAllTablesButton.setFont(new java.awt.Font("STHeiti", 0, 10));
    dropAllTablesButton.setText("Delete All Data");
    dropAllTablesButton.setToolTipText("Reset All Database Data !!!");
    dropAllTablesButton.setFocusTraversalKeysEnabled(false);
    dropAllTablesButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            dropAllTablesButtonActionPerformed(evt);
        }
    });
    dropAllTablesButton.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyPressed(java.awt.event.KeyEvent evt) {
            dropAllTablesButtonKeyPressed(evt);
        }
    });

    customerRecords.setFont(new java.awt.Font("STHeiti", 0, 12));
    customerRecords.setForeground(new java.awt.Color(204, 204, 204));
    customerRecords.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    customerRecords.setText("0");
    customerRecords.setToolTipText("Number of records in table");

    orderRecords.setFont(new java.awt.Font("STHeiti", 0, 12));
    orderRecords.setForeground(new java.awt.Color(204, 204, 204));
    orderRecords.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    orderRecords.setText("0");
    orderRecords.setToolTipText("Number of records in table");

    invoiceRecords.setFont(new java.awt.Font("STHeiti", 0, 12));
    invoiceRecords.setForeground(new java.awt.Color(204, 204, 204));
    invoiceRecords.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    invoiceRecords.setText("0");
    invoiceRecords.setToolTipText("Number of records in table");

    campaignRecords.setFont(new java.awt.Font("STHeiti", 0, 12));
    campaignRecords.setForeground(new java.awt.Color(204, 204, 204));
    campaignRecords.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    campaignRecords.setText("0");
    campaignRecords.setToolTipText("Number of records in table");

    destinationRecords.setFont(new java.awt.Font("STHeiti", 0, 12));
    destinationRecords.setForeground(new java.awt.Color(204, 204, 204));
    destinationRecords.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    destinationRecords.setText("0");
    destinationRecords.setToolTipText("Number of records in table");

    campaignStatsRecords.setFont(new java.awt.Font("STHeiti", 0, 12));
    campaignStatsRecords.setForeground(new java.awt.Color(204, 204, 204));
    campaignStatsRecords.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    campaignStatsRecords.setText("0");
    campaignStatsRecords.setToolTipText("Number of records in table");

    pricelistRecords.setFont(new java.awt.Font("STHeiti", 0, 12));
    pricelistRecords.setForeground(new java.awt.Color(204, 204, 204));
    pricelistRecords.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    pricelistRecords.setText("0");
    pricelistRecords.setToolTipText("Number of records in table");

    resellerRecords.setFont(new java.awt.Font("STHeiti", 0, 12));
    resellerRecords.setForeground(new java.awt.Color(204, 204, 204));
    resellerRecords.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    resellerRecords.setText("0");
    resellerRecords.setToolTipText("Number of records in table");

    org.jdesktop.layout.GroupLayout maintenancePanelLayout = new org.jdesktop.layout.GroupLayout(maintenancePanel);
    maintenancePanel.setLayout(maintenancePanelLayout);
    maintenancePanelLayout.setHorizontalGroup(
        maintenancePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(maintenancePanelLayout.createSequentialGroup()
            .add(183, 183, 183)
            .add(maintenancePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(org.jdesktop.layout.GroupLayout.TRAILING, headerLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE)
                .add(maintenancePanelLayout.createSequentialGroup()
                    .add(maintenancePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(customerTableLabel4)
                        .add(customerTableLabel3)
                        .add(customerTableLabel)
                        .add(customerTableLabel1)
                        .add(customerTableLabel2)
                        .add(customerTableLabel5))
                    .add(31, 31, 31)
                    .add(maintenancePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, maintenancePanelLayout.createSequentialGroup()
                            .add(campaignStatsRecords, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(dropCampaignStatsTableButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 104, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, maintenancePanelLayout.createSequentialGroup()
                            .add(destinationRecords, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 65, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(dropDestinationTableButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 104, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, maintenancePanelLayout.createSequentialGroup()
                            .add(campaignRecords, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 65, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(dropCampaignTableButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 104, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, maintenancePanelLayout.createSequentialGroup()
                            .add(invoiceRecords, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 65, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(dropInvoiceTableButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 104, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, maintenancePanelLayout.createSequentialGroup()
                            .add(orderRecords, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 73, Short.MAX_VALUE)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(dropOrderTableButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 104, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, maintenancePanelLayout.createSequentialGroup()
                            .add(customerRecords, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 73, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(dropCustomerTableButton))))
                .add(org.jdesktop.layout.GroupLayout.TRAILING, maintenancePanelLayout.createSequentialGroup()
                    .add(maintenancePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(maintenancePanelLayout.createSequentialGroup()
                            .add(maintenancePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(customerTableLabel6)
                                .add(customerTableLabel7))
                            .add(76, 76, 76)
                            .add(maintenancePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                .add(resellerRecords, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 63, Short.MAX_VALUE)
                                .add(pricelistRecords, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 63, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                        .add(customerTableLabel8))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(maintenancePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(dropAllTablesButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 104, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(maintenancePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(dropResellerTableButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(dropPricelistTableButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE)))))
            .add(153, 153, 153))
    );

    maintenancePanelLayout.linkSize(new java.awt.Component[] {campaignRecords, campaignStatsRecords, customerRecords, destinationRecords, invoiceRecords, orderRecords, pricelistRecords, resellerRecords}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

    maintenancePanelLayout.linkSize(new java.awt.Component[] {dropAllTablesButton, dropCampaignStatsTableButton, dropCampaignTableButton, dropCustomerTableButton, dropDestinationTableButton, dropInvoiceTableButton, dropOrderTableButton, dropPricelistTableButton, dropResellerTableButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

    maintenancePanelLayout.setVerticalGroup(
        maintenancePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(maintenancePanelLayout.createSequentialGroup()
            .add(headerLabel)
            .add(12, 12, 12)
            .add(maintenancePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(dropCustomerTableButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(customerTableLabel)
                .add(customerRecords))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(maintenancePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(dropOrderTableButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(customerTableLabel1)
                .add(orderRecords))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(maintenancePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(dropInvoiceTableButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(customerTableLabel2)
                .add(invoiceRecords))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(maintenancePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(dropCampaignTableButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(customerTableLabel3)
                .add(campaignRecords))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(maintenancePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(dropDestinationTableButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(customerTableLabel4)
                .add(destinationRecords))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(maintenancePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(dropCampaignStatsTableButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(campaignStatsRecords)
                .add(customerTableLabel5))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(maintenancePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(dropPricelistTableButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(customerTableLabel6)
                .add(pricelistRecords))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(maintenancePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(dropResellerTableButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(customerTableLabel7)
                .add(resellerRecords))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
            .add(maintenancePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(dropAllTablesButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(customerTableLabel8))
            .addContainerGap(22, Short.MAX_VALUE))
    );

    maintenancePanelLayout.linkSize(new java.awt.Component[] {dropAllTablesButton, dropCampaignStatsTableButton, dropCampaignTableButton, dropCustomerTableButton, dropDestinationTableButton, dropInvoiceTableButton, dropOrderTableButton, dropPricelistTableButton, dropResellerTableButton}, org.jdesktop.layout.GroupLayout.VERTICAL);

    org.jdesktop.layout.GroupLayout dbPanelLayout = new org.jdesktop.layout.GroupLayout(dbPanel);
    dbPanel.setLayout(dbPanelLayout);
    dbPanelLayout.setHorizontalGroup(
        dbPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(org.jdesktop.layout.GroupLayout.TRAILING, dbPanelLayout.createSequentialGroup()
            .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(maintenancePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addContainerGap())
    );
    dbPanelLayout.setVerticalGroup(
        dbPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(dbPanelLayout.createSequentialGroup()
            .add(maintenancePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    backofficeTab.addTab("DB Maintenance", dbPanel);

    tabPane.addTab("DB", backofficeTab);

    aboutTab.setToolTipText("Background Information on this Product");
    aboutTab.setDoubleBuffered(false);
    aboutTab.setFocusTraversalKeysEnabled(false);
    aboutTab.setFocusable(false);
    aboutTab.setFont(new java.awt.Font("STHeiti", 0, 12));
    aboutTab.setMaximumSize(new java.awt.Dimension(679, 311));
    aboutTab.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyPressed(java.awt.event.KeyEvent evt) {
            aboutTabKeyPressed(evt);
        }
    });

    sizeControlPanel.setBackground(new java.awt.Color(51, 51, 51));
    sizeControlPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("STHeiti", 0, 13), new java.awt.Color(255, 255, 255))); // NOI18N
    sizeControlPanel.setToolTipText("");

    brandLabel.setFont(new java.awt.Font("STHeiti", 1, 24));
    brandLabel.setForeground(new java.awt.Color(51, 51, 51));
    brandLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

    brandDescriptionLabel.setBackground(new java.awt.Color(51, 51, 51));
    brandDescriptionLabel.setColumns(20);
    brandDescriptionLabel.setEditable(false);
    brandDescriptionLabel.setFont(new java.awt.Font("STHeiti", 0, 12));
    brandDescriptionLabel.setForeground(new java.awt.Color(51, 51, 51));
    brandDescriptionLabel.setLineWrap(true);
    brandDescriptionLabel.setRows(5);
    brandDescriptionLabel.setToolTipText("");
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
    productDescriptionLabel.setFont(new java.awt.Font("STHeiti", 0, 12));
    productDescriptionLabel.setForeground(new java.awt.Color(51, 51, 51));
    productDescriptionLabel.setLineWrap(true);
    productDescriptionLabel.setRows(5);
    productDescriptionLabel.setWrapStyleWord(true);
    productDescriptionLabel.setAutoscrolls(false);
    productDescriptionLabel.setBorder(null);
    productDescriptionLabel.setDragEnabled(false);
    productDescriptionLabel.setFocusable(false);
    productDescriptionLabel.setMaximumSize(new java.awt.Dimension(100, 13));

    copyrightLabel.setBackground(new java.awt.Color(51, 51, 51));
    copyrightLabel.setColumns(20);
    copyrightLabel.setEditable(false);
    copyrightLabel.setFont(new java.awt.Font("STHeiti", 0, 12));
    copyrightLabel.setForeground(new java.awt.Color(51, 51, 51));
    copyrightLabel.setLineWrap(true);
    copyrightLabel.setRows(5);
    copyrightLabel.setWrapStyleWord(true);
    copyrightLabel.setAutoscrolls(false);
    copyrightLabel.setBorder(null);
    copyrightLabel.setDragEnabled(false);
    copyrightLabel.setFocusable(false);
    copyrightLabel.setMaximumSize(new java.awt.Dimension(100, 13));

    org.jdesktop.layout.GroupLayout sizeControlPanelLayout = new org.jdesktop.layout.GroupLayout(sizeControlPanel);
    sizeControlPanel.setLayout(sizeControlPanelLayout);
    sizeControlPanelLayout.setHorizontalGroup(
        sizeControlPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(org.jdesktop.layout.GroupLayout.TRAILING, sizeControlPanelLayout.createSequentialGroup()
            .add(sizeControlPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                .add(org.jdesktop.layout.GroupLayout.LEADING, productDescriptionLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(sizeControlPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, productLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 687, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, brandDescriptionLabel)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, brandLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 687, Short.MAX_VALUE)))
            .add(12, 12, 12))
        .add(sizeControlPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(sizeControlPanelLayout.createSequentialGroup()
                .add(copyrightLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 693, Short.MAX_VALUE)
                .addContainerGap()))
    );

    sizeControlPanelLayout.linkSize(new java.awt.Component[] {brandDescriptionLabel, brandLabel, copyrightLabel, productDescriptionLabel, productLabel}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

    sizeControlPanelLayout.setVerticalGroup(
        sizeControlPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(sizeControlPanelLayout.createSequentialGroup()
            .add(brandLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
            .add(brandDescriptionLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
            .add(productLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 37, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
            .add(productDescriptionLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 56, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addContainerGap(83, Short.MAX_VALUE))
        .add(sizeControlPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(sizeControlPanelLayout.createSequentialGroup()
                .add(245, 245, 245)
                .add(copyrightLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 41, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE)))
    );

    org.jdesktop.layout.GroupLayout aboutTabLayout = new org.jdesktop.layout.GroupLayout(aboutTab);
    aboutTab.setLayout(aboutTabLayout);
    aboutTabLayout.setHorizontalGroup(
        aboutTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(aboutTabLayout.createSequentialGroup()
            .add(sizeControlPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
    aboutTabLayout.setVerticalGroup(
        aboutTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(sizeControlPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
    );

    tabPane.addTab("About", aboutTab);

    summaryDisplayPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
    summaryDisplayPanel.setToolTipText("");
    summaryDisplayPanel.setFont(new java.awt.Font("STHeiti", 0, 13));
    summaryDisplayPanel.setMaximumSize(new java.awt.Dimension(700, 100));
    summaryDisplayPanel.setMinimumSize(new java.awt.Dimension(700, 100));
    summaryDisplayPanel.setOpaque(false);
    summaryDisplayPanel.setPreferredSize(new java.awt.Dimension(700, 100));
    summaryDisplayPanel.setSize(new java.awt.Dimension(700, 100));

    statusBarOutbound.setBackground(new java.awt.Color(230, 230, 230));
    statusBarOutbound.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    statusBarOutbound.setEditable(false);
    statusBarOutbound.setFont(new java.awt.Font("Synchro LET", 2, 12));
    statusBarOutbound.setForeground(new java.awt.Color(102, 102, 102));
    statusBarOutbound.setToolTipText("Status Bar");
    statusBarOutbound.setMaximumSize(new java.awt.Dimension(334, 25));
    statusBarOutbound.setMinimumSize(new java.awt.Dimension(334, 25));
    statusBarOutbound.setPreferredSize(new java.awt.Dimension(334, 25));
    statusBarOutbound.setSize(new java.awt.Dimension(334, 25));

    captionTable.setBackground(new java.awt.Color(240, 240, 240));
    captionTable.setBorder(javax.swing.BorderFactory.createEtchedBorder());
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
    captionTable.setToolTipText("Outbound CallCenter Statistics");
    captionTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
    captionTable.setAutoscrolls(false);
    captionTable.setDoubleBuffered(true);
    captionTable.setEnabled(false);
    captionTable.setFocusable(false);
    captionTable.setIntercellSpacing(new java.awt.Dimension(0, 0));
    captionTable.setMaximumSize(new java.awt.Dimension(670, 45));
    captionTable.setMinimumSize(new java.awt.Dimension(670, 45));
    captionTable.setPreferredSize(new java.awt.Dimension(670, 45));
    captionTable.setRowHeight(15);
    captionTable.setRowSelectionAllowed(false);
    captionTable.setShowGrid(false);
    captionTable.setSize(new java.awt.Dimension(670, 45));
    captionTable.setUpdateSelectionOnSort(false);
    captionTable.setVerifyInputWhenFocusTarget(false);

    statusBarInbound.setBackground(new java.awt.Color(230, 230, 230));
    statusBarInbound.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    statusBarInbound.setEditable(false);
    statusBarInbound.setFont(new java.awt.Font("Synchro LET", 2, 12));
    statusBarInbound.setForeground(new java.awt.Color(102, 102, 102));
    statusBarInbound.setToolTipText("Status Bar");
    statusBarInbound.setMaximumSize(new java.awt.Dimension(500, 22));
    statusBarInbound.setMinimumSize(new java.awt.Dimension(500, 22));
    statusBarInbound.setPreferredSize(new java.awt.Dimension(670, 22));
    statusBarInbound.setSize(new java.awt.Dimension(670, 22));

    resizeWindowButton.setFont(new java.awt.Font("STHeiti", 0, 8));
    resizeWindowButton.setText("⊻");
    resizeWindowButton.setToolTipText("Show Administration");
    resizeWindowButton.setEnabled(false);
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

    controlPanel.setToolTipText("");
    controlPanel.setFont(new java.awt.Font("STHeiti", 0, 13));
    controlPanel.setMaximumSize(new java.awt.Dimension(685, 118));
    controlPanel.setMinimumSize(new java.awt.Dimension(685, 118));
    controlPanel.setOpaque(false);
    controlPanel.setPreferredSize(new java.awt.Dimension(685, 118));
    controlPanel.setSize(new java.awt.Dimension(685, 118));

    displayPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("STHeiti", 0, 14))); // NOI18N
    displayPanel.setToolTipText("");
    displayPanel.setFont(new java.awt.Font("STHeiti", 0, 13));
    displayPanel.setMaximumSize(new java.awt.Dimension(415, 70));
    displayPanel.setMinimumSize(new java.awt.Dimension(415, 70));
    displayPanel.setOpaque(false);
    displayPanel.setPreferredSize(new java.awt.Dimension(415, 70));

    infoScrollPane.setBackground(new java.awt.Color(0, 0, 0));
    infoScrollPane.setBorder(null);
    infoScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    infoScrollPane.setToolTipText("");
    infoScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
    infoScrollPane.setColumnHeaderView(null);
    infoScrollPane.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    infoScrollPane.setDoubleBuffered(true);
    infoScrollPane.setFocusable(false);
    infoScrollPane.setFont(new java.awt.Font("Synchro LET", 0, 13));
    infoScrollPane.setViewportView(timeTextField);

    timeTextField.setBackground(new java.awt.Color(0, 0, 0));
    timeTextField.setEditable(false);
    timeTextField.setFont(new java.awt.Font("Synchro LET", 1, 24));
    timeTextField.setForeground(new java.awt.Color(167, 150, 233));
    timeTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
    timeTextField.setToolTipText("");
    timeTextField.setAutoscrolls(false);
    timeTextField.setBorder(null);
    timeTextField.setDisabledTextColor(new java.awt.Color(50, 50, 50));
    timeTextField.setDragEnabled(false);
    timeTextField.setFocusable(false);
    timeTextField.setRequestFocusEnabled(false);
    timeTextField.setSize(new java.awt.Dimension(84, 28));
    infoScrollPane.setViewportView(timeTextField);

    campaignProgressBar.setFont(new java.awt.Font("STHeiti", 0, 8));
    campaignProgressBar.setToolTipText("Campaign Progress");
    campaignProgressBar.setBorderPainted(false);
    campaignProgressBar.setEnabled(false);
    campaignProgressBar.setFocusTraversalKeysEnabled(false);
    campaignProgressBar.setFocusable(false);
    campaignProgressBar.setMaximumSize(new java.awt.Dimension(405, 20));
    campaignProgressBar.setMinimumSize(new java.awt.Dimension(405, 20));
    campaignProgressBar.setName("progressBar"); // NOI18N
    campaignProgressBar.setOpaque(true);
    campaignProgressBar.setPreferredSize(new java.awt.Dimension(405, 20));
    campaignProgressBar.setStringPainted(true);

    org.jdesktop.layout.GroupLayout displayPanelLayout = new org.jdesktop.layout.GroupLayout(displayPanel);
    displayPanel.setLayout(displayPanelLayout);
    displayPanelLayout.setHorizontalGroup(
        displayPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(displayPanelLayout.createSequentialGroup()
            .add(displayPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(displayPanelLayout.createSequentialGroup()
                    .add(7, 7, 7)
                    .add(infoScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 396, Short.MAX_VALUE))
                .add(displayPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .add(campaignProgressBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 380, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
            .addContainerGap())
    );
    displayPanelLayout.setVerticalGroup(
        displayPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(org.jdesktop.layout.GroupLayout.TRAILING, displayPanelLayout.createSequentialGroup()
            .add(infoScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 74, Short.MAX_VALUE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(campaignProgressBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
    );

    outboundButtonPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("STHeiti", 0, 10), new java.awt.Color(255, 255, 255))); // NOI18N
    outboundButtonPanel.setToolTipText("");
    outboundButtonPanel.setFont(new java.awt.Font("STHeiti", 0, 13));
    outboundButtonPanel.setOpaque(false);
    outboundButtonPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

    outboundCallCenterButton.setFont(new java.awt.Font("STHeiti", 0, 24));
    outboundCallCenterButton.setForeground(new java.awt.Color(51, 51, 51));
    outboundCallCenterButton.setText("☎☎");
    outboundCallCenterButton.setToolTipText("Start CallCenter on Left");
    outboundCallCenterButton.setEnabled(false);
    outboundCallCenterButton.setFocusable(false);
    outboundCallCenterButton.setMaximumSize(new java.awt.Dimension(120, 70));
    outboundCallCenterButton.setMinimumSize(new java.awt.Dimension(120, 70));
    outboundCallCenterButton.setPreferredSize(new java.awt.Dimension(120, 70));
    outboundCallCenterButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            outboundCallCenterButtonActionPerformed(evt);
        }
    });
    outboundButtonPanel.add(outboundCallCenterButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 96, 84));

    inboundButtonPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("STHeiti", 0, 13), new java.awt.Color(255, 255, 255))); // NOI18N
    inboundButtonPanel.setToolTipText("");
    inboundButtonPanel.setFont(new java.awt.Font("STHeiti", 0, 13));
    inboundButtonPanel.setOpaque(false);
    inboundButtonPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

    inboundCallCenterButton.setFont(new java.awt.Font("STHeiti", 0, 24));
    inboundCallCenterButton.setText("☏☏");
    inboundCallCenterButton.setToolTipText("Start CallCenter on Right");
    inboundCallCenterButton.setEnabled(false);
    inboundCallCenterButton.setFocusable(false);
    inboundCallCenterButton.setMaximumSize(new java.awt.Dimension(120, 70));
    inboundCallCenterButton.setMinimumSize(new java.awt.Dimension(120, 70));
    inboundCallCenterButton.setPreferredSize(new java.awt.Dimension(120, 70));
    inboundCallCenterButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            inboundCallCenterButtonActionPerformed(evt);
        }
    });
    inboundButtonPanel.add(inboundCallCenterButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 96, 84));

    org.jdesktop.layout.GroupLayout controlPanelLayout = new org.jdesktop.layout.GroupLayout(controlPanel);
    controlPanel.setLayout(controlPanelLayout);
    controlPanelLayout.setHorizontalGroup(
        controlPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(controlPanelLayout.createSequentialGroup()
            .addContainerGap()
            .add(outboundButtonPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 116, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
            .add(displayPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 421, Short.MAX_VALUE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
            .add(inboundButtonPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 106, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(18, 18, 18))
    );

    controlPanelLayout.linkSize(new java.awt.Component[] {inboundButtonPanel, outboundButtonPanel}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

    controlPanelLayout.setVerticalGroup(
        controlPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(org.jdesktop.layout.GroupLayout.TRAILING, controlPanelLayout.createSequentialGroup()
            .addContainerGap()
            .add(controlPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                .add(org.jdesktop.layout.GroupLayout.LEADING, displayPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
                .add(org.jdesktop.layout.GroupLayout.LEADING, inboundButtonPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
                .add(org.jdesktop.layout.GroupLayout.LEADING, outboundButtonPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE))
            .addContainerGap())
    );

    controlPanelLayout.linkSize(new java.awt.Component[] {displayPanel, inboundButtonPanel, outboundButtonPanel}, org.jdesktop.layout.GroupLayout.VERTICAL);

    org.jdesktop.layout.GroupLayout summaryDisplayPanelLayout = new org.jdesktop.layout.GroupLayout(summaryDisplayPanel);
    summaryDisplayPanel.setLayout(summaryDisplayPanelLayout);
    summaryDisplayPanelLayout.setHorizontalGroup(
        summaryDisplayPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(summaryDisplayPanelLayout.createSequentialGroup()
            .add(summaryDisplayPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                .add(org.jdesktop.layout.GroupLayout.LEADING, controlPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 701, Short.MAX_VALUE)
                .add(org.jdesktop.layout.GroupLayout.LEADING, summaryDisplayPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .add(summaryDisplayPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, captionTable, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, summaryDisplayPanelLayout.createSequentialGroup()
                            .add(statusBarOutbound, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 328, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(resizeWindowButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 35, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(statusBarInbound, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 309, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 11, Short.MAX_VALUE)))
            .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
    summaryDisplayPanelLayout.setVerticalGroup(
        summaryDisplayPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(org.jdesktop.layout.GroupLayout.TRAILING, summaryDisplayPanelLayout.createSequentialGroup()
            .addContainerGap()
            .add(controlPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(captionTable, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 45, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(summaryDisplayPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(resizeWindowButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 0, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(statusBarOutbound, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(statusBarInbound, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(34, 34, 34))
    );

    summaryDisplayPanelLayout.linkSize(new java.awt.Component[] {resizeWindowButton, statusBarInbound, statusBarOutbound}, org.jdesktop.layout.GroupLayout.VERTICAL);

    org.jdesktop.layout.GroupLayout colorMaskPanelLayout = new org.jdesktop.layout.GroupLayout(colorMaskPanel);
    colorMaskPanel.setLayout(colorMaskPanelLayout);
    colorMaskPanelLayout.setHorizontalGroup(
        colorMaskPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(colorMaskPanelLayout.createSequentialGroup()
            .addContainerGap()
            .add(colorMaskPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                .add(org.jdesktop.layout.GroupLayout.LEADING, summaryDisplayPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 719, Short.MAX_VALUE)
                .add(org.jdesktop.layout.GroupLayout.LEADING, tabPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 719, Short.MAX_VALUE))
            .addContainerGap(10, Short.MAX_VALUE))
    );
    colorMaskPanelLayout.setVerticalGroup(
        colorMaskPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(colorMaskPanelLayout.createSequentialGroup()
            .addContainerGap()
            .add(summaryDisplayPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 234, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
            .add(tabPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addContainerGap(20, Short.MAX_VALUE))
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

    private void vergunningJumpstart()
    {
        setVisible(true);
//        while ((!isVisible()) || (!isActive())) { try { Thread.sleep(250); } catch (InterruptedException ex) { } }
        Thread vergunningJumpstartThread = new Thread(new Runnable()
        {
            @Override
            @SuppressWarnings({"static-access"})
            public void run()
            {
                showStatusInbound("Verifying License, please wait...", true, true);
                vergunning = new Vergunning();
                vergunning.controleerVergunning();
                if (!vergunning.isValid())
                {
                    displayMessage("Please select your License Type");
                    showStatus(Vergunning.PRODUCT + " not Licensed", true, true); // showStatusInbound("", false, false);
                    try { Thread.sleep(1000); } catch (InterruptedException ex) { }
                    showStatus("Select your License Type", false, false);
                    showStatusInbound("", false, false);
                    tabPane.setSelectedIndex(5);
                    licenseTab.setEnabled(true);
                    resizeWindowButton.setEnabled(true);
                }
                else
                {
                    showStatus(vergunning.getVergunningType() + " License Validated", true, true);
                    showStatusInbound("", false, false); // Cleaning of whatever was there before
                    callsPerHourMeter.setScale(0, (vergunning.getCallsPerHour() / 100), (vergunning.getCallsPerHour() / 1000));
                    resizeWindowButton.setEnabled(true);
                    prefPhoneLinesSlider.setMaximum(vergunning.getPhoneLines());

                    inboundCallCenterButton.setEnabled(true);
                    outboundCallCenterButton.setEnabled(true);

                    if ( Integer.parseInt(configurationCallCenter.getPrefPhoneLines()) > vergunning.getPhoneLines() )
                    {
                        softphonesQuantity = vergunning.getPhoneLines();
                    }
                    else
                    {
                        softphonesQuantity = Integer.parseInt(configurationCallCenter.getPrefPhoneLines()); prefPhoneLinesSlider.setValue(Integer.parseInt(configurationCallCenter.getPrefPhoneLines()));
                    }

                    if
                    (
                        (pubIPField.getText().length() == 0) ||
                        (domainField.getText().length() == 0) ||
                        (serverIPField.getText().length() == 0) ||
                        (usernameField.getText().length() == 0) ||
                        (toegangField.getPassword().length == 0)
                    )
                    {
                        displayMessage("You need a VOIP Provider to make real phonecalls, we recommend: www.budgetphone.nl");
                        showStatus("Please Configure Network Settings", true, false);
                        showStatusInbound("Tooltips for help are available", false, false);
                        tabPane.setSelectedIndex(3); // Config

                        Thread getPublicIPThread = new Thread(new Runnable()
                        {
                            @Override
                            @SuppressWarnings({"static-access"})
                            public void run()
                            {
                                pubIPField.setText(PublicIP.getPublicIP());
                            }
                        });
                        getPublicIPThread.setName("getPublicIPThread");
                        getPublicIPThread.setDaemon(true);
                        getPublicIPThread.setPriority(1);
                        getPublicIPThread.start();
                    }
                    else
                    {
                        tabPane.setEnabled(true);
                        inboundCallCenterButton.setEnabled(true);
                        outboundCallCenterButton.setEnabled(true);
                    }
                }
            }
        });
        vergunningJumpstartThread.setName("vergunningJumpstartThread");
        vergunningJumpstartThread.setDaemon(true);
        vergunningJumpstartThread.setPriority(3);
        vergunningJumpstartThread.start();
    }

    private void checkVersion()
    {
        Thread checkVersionThread = new Thread(new Runnable()
        {
            @Override
            @SuppressWarnings({"static-access"})
            public void run()
            {
//                showStatus("Checking for updates...", true, true);
                showStatusInbound("", false, false);
                try { versionChecker = new VersionChecker(managerReference); }
                catch (MalformedURLException ex) { showStatus("URL Software Update is Bad!", true, true); }
                catch (IOException ex)
                {
                    showStatus("Software Update Network Failure!", true, true);
                    managerVersionLabel.setForeground(Color.yellow);
                    callcenterVersionLabel.setForeground(Color.yellow);
                    ephoneVersionLabel.setForeground(Color.yellow);
                    try { Thread.sleep(3000); } catch (InterruptedException ex1) { }
                    showStatus("Please Download and Overwrite", true, true);
                    showStatusInbound("http://www.voipstorm.nl/VoipStorm.jar", true, true);
                }
            }
        });
        checkVersionThread.setName("versionCheckerThread");
        checkVersionThread.setDaemon(true);
        checkVersionThread.setPriority(1);
        checkVersionThread.start();
    }

    private void setLookAndFeel(int plafIndexParam)
    {
        plafSelected = plaf[plafIndexParam];
        try { UIManager.setLookAndFeel(plafSelected); } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {}
        setVisible(false); setVisible(true);
    }

    private void orderVergunningCode()
    {
        String[] status = new String[2];
//        activationCodeField.setText(Long.toString(Calendar.getInstance().getTimeInMillis()));
        String activationCodeString = null;
        String activationCodeKeyString = null;

        vergunning.setVergunningOrderInProgress(true);
//        performanceMeter.setCallPerHourScale(0, (Vergunning.CALLSPERHOUR_ENTERPRISE / 100), (Vergunning.CALLSPERHOUR_ENTERPRISE / 1000));

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
                vergunning.setVergunningType("Enterprise");
		vergunning.setPhoneLines(vergunning.PHONELINES_ENTERPRISE);
		vergunning.setCallsPerHour(vergunning.CALLSPERHOUR_ENTERPRISE);
		vergunning.setMaxCalls(vergunning.MAXCALLS_ENTERPRISE);
		vergunning.setDestinationDigits(vergunning.DESTINATIONDIGITS_ENTERPRISE);
                
		vergunningDetailsTable.setValueAt(vergunning.getVergunningType(), 1, 1);
                vergunningDetailsTable.setValueAt(Integer.toString(vergunning.getPhoneLines()), 5, 1);
                vergunningDetailsTable.setValueAt(Integer.toString(vergunning.getCallsPerHour()), 6, 1);
                vergunningDetailsTable.setValueAt(Integer.toString(vergunning.getMaxCalls()), 7, 1);
                vergunningDetailsTable.setValueAt(Integer.toString(vergunning.getDestinationDigits()), 8, 1);
            }
            else
            {
                vergunning.setVergunningType("Enterprise");
		vergunning.setPhoneLines(vergunning.PHONELINES_ENTERPRISE);
		vergunning.setCallsPerHour(vergunning.CALLSPERHOUR_ENTERPRISE);
		vergunning.setMaxCalls(vergunning.MAXCALLS_ENTERPRISE);
		vergunning.setDestinationDigits(vergunning.DESTINATIONDIGITS_ENTERPRISE);
                
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
                showStatus("Visit www." + Vergunning.BRAND.toLowerCase() + ".nl and request your LicenseCode", false, false);
                vergunningCodeField.setText("");
                vergunningCodeField.setEnabled(true);
            }
        }

        // Put a little show on stage
    }

    /**
     *
     * @param toParam
     * @param smoothParam
     */
    synchronized protected void moveCallsPerHourMeter(final double toParam, boolean smoothParam)
    {
        if ((smoothParam) && (!moveCallsPerHourMeterIsLocked))
        {
            moveCallsPerHourMeterIsLocked = true;
            Thread moveCallsPerHourMeterThread = new Thread(new Runnable()
            {
                @Override
                @SuppressWarnings("empty-statement")
                public void run()
                {
                    if (callsPerHourMeter != null)
                    {
                        double from = callsPerHourMeter.getValue().doubleValue();
                        double counter = from;
                        double to   = toParam;

                        if (from < to)
                        {
                            for (counter = from; counter <= to; counter += 1 ) { callsPerHourMeter.setValue(counter); try { Thread.sleep(10); } catch (InterruptedException ex) { } }
                        }
                        else
                        {
                            for (counter = from; counter >= to; counter -= 1 ) { callsPerHourMeter.setValue(counter); try { Thread.sleep(10); } catch (InterruptedException ex) { } }
                        }
                    }
                moveCallsPerHourMeterIsLocked = false;
                }
            });
            moveCallsPerHourMeterThread.setName("moveCallsPerHourMeterThread");
            moveCallsPerHourMeterThread.setDaemon(runThreadsAsDaemons);
            moveCallsPerHourMeterThread.setPriority(4);
            moveCallsPerHourMeterThread.start();
        }
        else if (!moveCallsPerHourMeterIsLocked)
        {
            if (callsPerHourMeter != null)
            {
                callsPerHourMeter.setValue(toParam);
            }
        }
    }

    /**
     *
     * @param toParam
     * @param smoothParam
     */
    synchronized protected void moveCPUMeter(final int toParam, boolean smoothParam)
    {
        if ((smoothParam) && (!moveCPUMeterIsLocked))
        {
            moveCPUMeterIsLocked = true;
            Thread moveCPUMeterThread = new Thread( new Runnable()
            {
                @Override
                @SuppressWarnings("empty-statement")
                public void run()
                {
                    if (callsPerHourMeter != null)
                    {
                        double from = callsPerHourMeter.getVMUsageValue().doubleValue();
                        double counter = from;
                        double to   = toParam;

                        if (from < to)
                        {
                            for (counter = from; counter <= to; counter++ ) { callsPerHourMeter.setVMUsageValue(counter); try { Thread.sleep(5); } catch (InterruptedException ex) { } }
                        }
                        else
                        {
                            for (counter = from; counter >= to; counter-- ) { callsPerHourMeter.setVMUsageValue(counter); try { Thread.sleep(5); } catch (InterruptedException ex) { } }
                        }
                    }
                    moveCPUMeterIsLocked = false;
                }
            });
            moveCPUMeterThread.setName("moveCPUMeterThread");
            moveCPUMeterThread.setDaemon(runThreadsAsDaemons);
            moveCPUMeterThread.setPriority(5);
            moveCPUMeterThread.start();
        }
        else
        {
            if (callsPerHourMeter != null)
            {
                callsPerHourMeter.setVMUsageValue(toParam);
            }
        }
    }

    /**
     *
     * @param toParam
     * @param smoothParam
     */
    synchronized protected void moveBusyRatioMeter(final double toParam, boolean smoothParam)
    {
        if ((smoothParam) && (!moveBusyRatioMeterIsLocked))
        {
            moveBusyRatioMeterIsLocked = true;
            Thread moveBusyRatioMeterThread = new Thread(new Runnable()
            {
                @Override
                @SuppressWarnings("empty-statement")
                public void run()
                {
                    if (busyRatioMeter != null)
                    {
                        double from = busyRatioMeter.getValue().doubleValue();;
                        double counter = from;
                        double to   = toParam;

                        if (from < to)
                        {
                            for (counter = from; counter <= to; counter++ ) { busyRatioMeter.setValue(counter); try { Thread.sleep(10); } catch (InterruptedException ex) { } }
                        }
                        else
                        {
                            for (counter = from; counter >= to; counter-- ) { busyRatioMeter.setValue(counter); try { Thread.sleep(10); } catch (InterruptedException ex) { } }
                        }
                    }
                moveBusyRatioMeterIsLocked = false;
                }
            });
            moveBusyRatioMeterThread.setName("moveBusyRatioMeterThread");
            moveBusyRatioMeterThread.setDaemon(runThreadsAsDaemons);
            moveBusyRatioMeterThread.setPriority(4);
            moveBusyRatioMeterThread.start();
        }
        else if (!moveBusyRatioMeterIsLocked)
        {
            if (busyRatioMeter != null)
            {
                busyRatioMeter.setValue(toParam);
            }
        }
    }

    /**
     *
     * @param toParam
     * @param smoothParam
     */
    synchronized protected void moveAnswerDelayMeter(final double toParam, boolean smoothParam)
    {
        if ((smoothParam) && (!moveAnswerDelayMeterIsLocked))
        {
            moveAnswerDelayMeterIsLocked = true;
            Thread moveAnswerDelayMeterThread = new Thread(new Runnable()
            {
                @Override
                @SuppressWarnings("empty-statement")
                public void run()
                {
                    if (answerDelayMeter != null)
                    {
                        double from = answerDelayMeter.getValue().doubleValue();;
                        double counter = from;
                        double to   = toParam;

                        if (from < to)
                        {
                            for (counter = from; counter <= to; counter++ ) { answerDelayMeter.setValue(counter); try { Thread.sleep(10); } catch (InterruptedException ex) { } }
                        }
                        else
                        {
                            for (counter = from; counter >= to; counter-- ) { answerDelayMeter.setValue(counter); try { Thread.sleep(10); } catch (InterruptedException ex) { } }
                        }
                    }
                moveAnswerDelayMeterIsLocked = false;
                }
            });
            moveAnswerDelayMeterThread.setName("moveAnswerDelayMeterThread");
            moveAnswerDelayMeterThread.setDaemon(runThreadsAsDaemons);
            moveAnswerDelayMeterThread.setPriority(4);
            moveAnswerDelayMeterThread.start();
        }
        else if (!moveAnswerDelayMeterIsLocked)
        {
            if (answerDelayMeter != null)
            {
                answerDelayMeter.setValue(toParam);
            }
        }
    }

    /**
     *
     * @param toParam
     * @param smoothParam
     */
    synchronized protected void moveCallDurationMeter(final double toParam, boolean smoothParam)
    {
        if ((smoothParam) && (!moveCallDurationMeterIsLocked))
        {
            moveCallDurationMeterIsLocked = true;
            Thread moveCallDurationMeterThread = new Thread(new Runnable()
            {
                @Override
                @SuppressWarnings("empty-statement")
                public void run()
                {
                    if (callDurationMeter != null)
                    {
                        double from = callDurationMeter.getValue().doubleValue();
                        double counter = from;
                        double to   = toParam;

                        if (from < to)
                        {
                            for (counter = from; counter <= to; counter++ ) { callDurationMeter.setValue(counter); try { Thread.sleep(10); } catch (InterruptedException ex) { } }
                        }
                        else
                        {
                            for (counter = from; counter >= to; counter-- ) { callDurationMeter.setValue(counter); try { Thread.sleep(10); } catch (InterruptedException ex) { } }
                        }
                    }
                moveCallDurationMeterIsLocked = false;
                }
            });
            moveCallDurationMeterThread.setName("moveCallDurationMeterThread");
            moveCallDurationMeterThread.setDaemon(runThreadsAsDaemons);
            moveCallDurationMeterThread.setPriority(4);
            moveCallDurationMeterThread.start();
        }
        else if (!moveCallDurationMeterIsLocked)
        {
            if (callDurationMeter != null)
            {
                callDurationMeter.setValue(toParam);
            }
        }
    }

    /**
     *
     */
    synchronized public void timedDashboardManagerUpdate()
    {
	Thread timedDashboardManagerUpdateThread = new Thread(new Runnable()
	{
	    @Override
	    @SuppressWarnings({"static-access", "empty-statement"})
	    public void run()
	    {                
                // Calculate calls per second
                if (outboundCallCenterShouldBeRunning)
                {
                    //Call PerHour
                    if (campaignStat.getCallingTT() > 0)
                    {
                        callsPerHourPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Calls per Hour", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("STHeiti", 0, 10), new java.awt.Color(255,153,0))); // NOI18N
                        double callsPerSecondPrecise = 0; currentTimeDashboardCalendar = Calendar.getInstance();
                        callsPerSecondPrecise = (((double)campaignStat.getCallingTT()  - (double)lastTimeDashboardCampaignStat.getCallingTT()) / ((currentTimeDashboardCalendar.getTimeInMillis() / 1000) - (lastTimeDashboardCalendar.getTimeInMillis() / 1000)));
                        double callsPerSecondRounded = (double)((callsPerSecondPrecise*100.0) / (double)100.0);
                        double callsPerMinutePrecise = (callsPerSecondRounded * 60);
                        double callsPerMinuteRounded = (double)((callsPerMinutePrecise*100.0) / (double)100.0);
                        double callsPerHourRounded   = (double)((callsPerMinuteRounded*60.0));
                        if (((currentTimeDashboardCalendar.getTimeInMillis() / 1000) - (lastTimeDashboardCalendar.getTimeInMillis() / 1000) > 0)) { campaignTable.setValueAt(callsPerHourRounded + " per Hour", 9, 1); }
                        try { lastTimeDashboardCampaignStat = (CampaignStat) campaignStat.clone(); } catch (CloneNotSupportedException ex) { /* Nonsens in this case*/ };
                        lastTimeDashboardCalendar = currentTimeDashboardCalendar.getInstance();
                        moveCallsPerHourMeter((callsPerHourRounded / 100), smoothCheckBox.isSelected());
                        callsPerHourPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Calls per Hour", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("STHeiti", 0, 10), new java.awt.Color(102, 102, 102))); // NOI18N
                    }
                    
                    // Busy Ratio
                    if ((campaignStat.getCallingTT() > 0) && (campaignStat.getRemoteBusyTT() > 0))
                    {
                        busyRatioPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Busy Ratio %", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("STHeiti", 0, 10), new java.awt.Color(255,153,0))); // NOI18N
                        moveBusyRatioMeter((campaignStat.getRemoteBusyTT() / (campaignStat.getCallingTT() * 0.01)), smoothCheckBox.isSelected());
                        busyRatioPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Busy Ratio %", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("STHeiti", 0, 10), new java.awt.Color(102, 102, 102))); // NOI18N
                    }

                    // Answer Delay
                    if (campaignStat.getTalkingTT() > 0)
                    {
                        answerDelayPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Answer Delay", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("STHeiti", 0, 10), new java.awt.Color(255,153,0))); // NOI18N
                        moveAnswerDelayMeter(dbClient.selectAverageAnswerDelay(campaign.getId()),smoothCheckBox.isSelected());
                        answerDelayPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Answer Delay", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("STHeiti", 0, 10), new java.awt.Color(102, 102, 102))); // NOI18N
                    }

                    // Call Duration
                    if (campaignStat.getTalkingTT() > 0)
                    {
                        callDurationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Call Duration", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("STHeiti", 0, 10), new java.awt.Color(255,153,0))); // NOI18N
                        moveCallDurationMeter(dbClient.selectAverageCallDuration(campaign.getId()),smoothCheckBox.isSelected());
                        callDurationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Call Duration", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("STHeiti", 0, 10), new java.awt.Color(102, 102, 102))); // NOI18N
                    }
                    System.gc();
                }
                else
                {
                    moveCallsPerHourMeter(0, smoothCheckBox.isSelected());
                    moveBusyRatioMeter(0, smoothCheckBox.isSelected());
                    moveAnswerDelayMeter(0,smoothCheckBox.isSelected());
                    moveCallDurationMeter(0,smoothCheckBox.isSelected());
                }

	    }
	});
	timedDashboardManagerUpdateThread.setName("timedDashboardManagerUpdateThread");
	timedDashboardManagerUpdateThread.setDaemon(runThreadsAsDaemons);
        timedDashboardManagerUpdateThread.setPriority(2);
	timedDashboardManagerUpdateThread.start();
    }

    /**
     *
     */
    @SuppressWarnings("static-access")
    public void serviceTimeline() // TimeLine Campain Manager
    {
        if (stopRequested ) { System.exit(0); }
// Get, set and display Current Time
        currentTimeCalendar = Calendar.getInstance();

        if (displayMessagesRunning <= 0)
        {
            timeTextField.setText(
                                String.format("%02d", currentTimeCalendar.get(Calendar.DAY_OF_MONTH)) + "-" +
                                                      currentTimeCalendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, nlLocale) + "-" +
                                String.format("%04d", currentTimeCalendar.get(Calendar.YEAR)) + " " +
                                String.format("%02d", currentTimeCalendar.get(Calendar.HOUR_OF_DAY)) + ":" +
                                String.format("%02d", currentTimeCalendar.get(Calendar.MINUTE)) + ":" +
                                String.format("%02d", currentTimeCalendar.get(Calendar.SECOND))
                             );
        }

// Refresh the Heap Memory Stats in the config panel
        heapMemMax = (Runtime.getRuntime().maxMemory()/(1024*1024)); sysProp = "heap max";   sysPropsTable.setValueAt(sysProp, 10, 0);  sysPropsTable.setValueAt(Long.toString(heapMemMax) + " MB", 10, 1);
        heapMemTot = (Runtime.getRuntime().totalMemory()/(1024*1024));sysProp = "heap tot";  sysPropsTable.setValueAt(sysProp, 11, 0);  sysPropsTable.setValueAt(Long.toString(heapMemTot) + " MB", 11, 1);
        heapMemFree = (Runtime.getRuntime().freeMemory()/(1024*1024));sysProp = "heap free"; sysPropsTable.setValueAt(sysProp, 12, 0);  sysPropsTable.setValueAt(Long.toString(heapMemFree) + " MB", 12, 1);
        threads = Thread.activeCount();                               sysProp = "Threads";      sysPropsTable.setValueAt(sysProp, 13, 0);  sysPropsTable.setValueAt(Long.toString(threads), 13, 1);

        // Set the red CPULoad needle
        vmUsage = (sysMonitor.getProcessTime()); moveCPUMeter((int)vmUsage, smoothCheckBox.isSelected());

        if ((vergunning != null) && (vergunning.isValid()))
        {
    // Check State CallCenters in the background
            if ( callPerHourScaleNeedsRescaling ) { callsPerHourMeter.setScale(0, (vergunning.getCallsPerHour() / 100), (vergunning.getCallsPerHour() / 1000)); callPerHourScaleNeedsRescaling = false; } // vergunning (callsperhour) changed so we need rescaling

            if ((prefPhoneLinesSlider.getMaximum() == 0) || (prefPhoneLinesSlider.getMaximum() > vergunning.getPhoneLines()))
            {
                prefPhoneLinesSlider.setMaximum(vergunning.getPhoneLines()); prefPhoneLinesSlider.setValue(vergunning.getPhoneLines());
            }
            if ((platformIsNetManaged))
            {
                if (inboundCallCenterShouldBeRunning)
                {
                    Thread checkInboundCallCentersThread = new Thread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            String[] status = new String[2]; status[0] = "0"; status[1] = "";
                            // Check if Inbound / Test CallCenter is Running
                            if (!netManagerInboundClientToggleButton.isSelected()) { netManagerInboundClientToggleButton.setSelected(true); }
                            inboundNetManagerClient = new NetManagerClient(managerReference,INBOUND_PORT);

                            long startTime = Calendar.getInstance().getTimeInMillis();
                            status = inboundNetManagerClient.connectAndSend("getCallCenterStatusDescription");
                            long stopTime = Calendar.getInstance().getTimeInMillis();
                            String responseTime = "Chk Inbound [" + String.format("%03d", stopTime - startTime) + " ms]";

                            if (status[0].equals("0"))
                            {
                                inboundCallCenterState = CONNECTED; inboundCallCenterStatusDescription = status[1];
                                blinkInboundNetManagerToggleButton(Color.green, responseTime);
                                try { Thread.sleep(100); } catch (InterruptedException ex) { }
                                blinkInboundNetManagerToggleButton(Color.blue, responseTime);
                            }
                            else
                            {
                                inboundCallCenterState = DISCONNECTED;
                                blinkInboundNetManagerToggleButton(Color.red, responseTime);
                                try { Thread.sleep(100); } catch (InterruptedException ex) { }
                                blinkInboundNetManagerToggleButton(Color.blue, responseTime);
                            }
                        }
                    });
                    checkInboundCallCentersThread.setName("checkInboundCallCentersThread");
                    checkInboundCallCentersThread.setDaemon(runThreadsAsDaemons);
                    checkInboundCallCentersThread.setPriority(8);
                    checkInboundCallCentersThread.start();
                }
                else { if (netManagerInboundClientToggleButton.isSelected()) { netManagerInboundClientToggleButton.setSelected(false); } }

                if (outboundCallCenterShouldBeRunning)
                {
                    Thread checkOutboundCallCentersThread = new Thread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            String[] status = new String[2]; status[0] = "0"; status[1] = "";
                            // Check if Outbound CallCenter is Running
                            if (!netManagerOutboundClientToggleButton.isSelected()) { netManagerOutboundClientToggleButton.setSelected(true); }
                            outboundNetManagerClient = new NetManagerClient(managerReference,OUTBOUND_PORT);

                            long startTime = Calendar.getInstance().getTimeInMillis();
                            status = outboundNetManagerClient.connectAndSend("getCallCenterStatusDescription");
                            long stopTime = Calendar.getInstance().getTimeInMillis();
                            String responseTime = "Chk Outbound [" + String.format("%03d", stopTime - startTime) + " ms]";

                            if (status[0].equals("0"))
                            {
                                outboundCallCenterState = CONNECTED; outboundCallCenterStatusDescription = status[1];
                                blinkOutboundNetManagerToggleButton(Color.green, responseTime);
                                try { Thread.sleep(100); } catch (InterruptedException ex) { }
                                blinkOutboundNetManagerToggleButton(Color.blue, responseTime);
                            }
                            else
                            {
                                outboundCallCenterState = DISCONNECTED;
                                blinkOutboundNetManagerToggleButton(Color.red, responseTime);
                                try { Thread.sleep(100); } catch (InterruptedException ex) { }
                                blinkOutboundNetManagerToggleButton(Color.blue, responseTime);
                            }
                        }
                    });
                    checkOutboundCallCentersThread.setName("checkOutboundCallCentersThread");
                    checkOutboundCallCentersThread.setDaemon(runThreadsAsDaemons);
                    checkOutboundCallCentersThread.setPriority(8);
                    checkOutboundCallCentersThread.start();
                }
                else
                {
                    if (outboundNetManagerClient != null)
                    {
                        if (netManagerOutboundClientToggleButton.isSelected())
                        {
                            netManagerOutboundClientToggleButton.setSelected(false);
                        }
                        // outboundNetManagerClient.closeConnection(); outboundNetManagerClient = null;
                    }
                    moveCallsPerHourMeter(0, true);
                    moveBusyRatioMeter(0, true);
                    moveAnswerDelayMeter(0, true);
                    moveCallDurationMeter(0, true);
                }
            }

    // Get the next Campaign in line
            campaign = dbClient.getNextOpenCampaign(); // Much relies on the results of this statement

            if (campaign.getCalendarCampaignCreated().getTimeInMillis() > 0)
            {
                // Get Open Campaigns for this TimeWindow
                order    = dbClient.selectCustomerOrder(campaign.getOrderId());
                customer = dbClient.selectCustomer(order.getCustomerId());
                campaignStat = dbClient.selectCampaignStat(campaign.getId());
                if ( ! lastTimeDashboardCampaignStatSyncedFirstTime )
                {
                    double messDuration = order.getMessageDuration();
                    try { lastTimeDashboardCampaignStat = (CampaignStat) campaignStat.clone(); } catch (CloneNotSupportedException ex) { }
                    callDurationMeter.setScale(0, (double)order.getMessageDuration(), ((double)(order.getMessageDuration() * 0.1)));
                    callDurationMeter.setColorScale(0, (messDuration * 0.33), ((messDuration * 0.33)), (messDuration * 0.66), (messDuration * 0.66), (messDuration));
                    lastMessageDuration = order.getMessageDuration();
                    lastTimeDashboardCampaignStatSyncedFirstTime = true;
                    campaignProgressBar.setMinimum(0); campaignProgressBar.setMaximum(order.getTargetTransactionQuantity());
//                    campaignProgressBar.setMinimum(0); campaignProgressBar.setMaximum(dbClient.getNumberOfAllOpenCampaignDestinations(campaign.getId()));
                    campaignProgressBar.setValue(0);campaignProgressBar.setEnabled(true);campaignProgressBar.setVisible(true);
                }
                if (lastMessageDuration != order.getMessageDuration()) // When Soundfile changes after order creation then we need to change the CallDuration Performance Meter as well
                {
                    double messDuration = order.getMessageDuration();
                    callDurationMeter.setScale(0, (double)messDuration, ((double)(messDuration * 0.1)));
                    callDurationMeter.setColorScale(0, (messDuration * 0.33), ((messDuration * 0.33)), (messDuration * 0.66), (messDuration * 0.66), (messDuration));
                    lastMessageDuration = order.getMessageDuration();
                }
               
                displayCampaign(campaign, order, customer, campaignStat);

                // Time Criteria compliance
                if (
                        (campaign.getCalendarCampaignCreated().getTimeInMillis() > 0) &&
                        (currentTimeCalendar.getTimeInMillis() > campaign.getCalendarExpectedStart().getTimeInMillis())
                   )
                {
                    timeTextField.setForeground(timeFieldColorQueueRunning);
                    outboundCallCenterShouldBeRunning = true;
                    if (campaign.getTestCampaign()) { inboundCallCenterShouldBeRunning = true; } else { inboundCallCenterShouldBeRunning = false; }
                }
                else
                {
                    timeTextField.setForeground(timeFieldColorQueueWaiting);
                    outboundCallCenterShouldBeRunning = false;
                    inboundCallCenterShouldBeRunning = false;
                }

            }
            else
            {
                resetDisplay();
                timeTextField.setForeground(timeFieldColorQueueInactive);
                outboundCallCenterShouldBeRunning = false;
                inboundCallCenterShouldBeRunning = false;
                if (campaignProgressBar.isVisible()) { campaignProgressBar.setValue(0); campaignProgressBar.setEnabled(false); campaignProgressBar.setVisible(false); }
            }

    // Manage CallCenters
            if (platformIsNetManaged)
            {

    // Inbound CallCenter
                if (inboundCallCenterShouldBeRunning)
                {
                    if      (inboundCallCenterState == DISCONNECTED)
                    {
                        if      (inboundCallCenterStatus == NOTRUNNING) // NotRunning to Starting Transition
                        {
                            inboundCallCenterButton.setForeground(Color.RED);
                            if (callCenterEnabledCheckBox.isSelected())
                            {
                                startInboundCallCenter(true, platformIsNetManaged);
                                inboundCallCenterStatus = STARTING;
                                inboundCallCenterButton.setToolTipText("Inbound CallCenter Starting");
                                inboundCallCenterStartingTimer = 0;
                            }
                            else
                            {
                                showStatusInbound("Inbound CallCenter Starting Disabled!", false, false);
                            }
                        }
                        else if (inboundCallCenterStatus == STARTING) // Starting Timer
                        {
                            inboundCallCenterButton.setForeground(Color.YELLOW);
                            if ( inboundCallCenterStartingTimer == inboundCallCenterStartingTimerLimit ) { inboundCallCenterStatus = NOTRUNNING; }
                            showStatusInbound("Inbound CallCenter Starting [" + Integer.toString(inboundCallCenterStartingTimer) + "-" + Integer.toString(inboundCallCenterStartingTimerLimit) + "]", false, false);
                            inboundCallCenterStartingTimer++;
                        }
                        else if (inboundCallCenterStatus == RUNNING) // Running to Failing Transition
                        {
                            inboundCallCenterButton.setForeground(Color.RED);
                            inboundCallCenterButton.setToolTipText("Inbound CallCenter Disconnected");
                            inboundCallCenterStatus = FAILING;
                            showStatusInbound("Inbound CallCenter Disconnected", true, true);
                            inboundCallCenterUnrespondingTimer = 0;
                        }
                        else if (inboundCallCenterStatus == FAILING) // Failing Timer
                        {
                            inboundCallCenterButton.setForeground(Color.GRAY);
                            if ( inboundCallCenterUnrespondingTimer == inboundCallCenterUnrespondingTimerLimit ) { inboundCallCenterStatus = NOTRUNNING; inboundCallCenterUnrespondingTimer = 0; }
                            showStatusInbound("Inbound CallCenter Disconnected [" + Integer.toString(inboundCallCenterUnrespondingTimer) + "-" + Integer.toString(inboundCallCenterUnrespondingTimerLimit) + "]", false, false);
                            inboundCallCenterUnrespondingTimer++;
                        }
                    }
                    else // inboundCallCenterState == CONNECTED
                    {
                        if      (inboundCallCenterStatus == STARTING) // Starting to Running Transition
                        {
                            inboundCallCenterButton.setForeground(Color.GREEN);
                            inboundCallCenterStatus = RUNNING;
                            inboundCallCenterButton.setToolTipText("Inbound CallCenter is Running");
                            showStatusInbound("Inbound CallCenter Running", true, true);
                            inboundCallCenterStartingTimer = 0;
                        }
                        else if (inboundCallCenterStatus == RUNNING) // Steady Operation
                        {
                            inboundCallCenterButton.setForeground(Color.GREEN);
                        }
                        else if (inboundCallCenterStatus == FAILING) // Disconnected to Connected Transition
                        {
                            inboundCallCenterButton.setForeground(Color.GREEN);
                            inboundCallCenterStatus = RUNNING;
                            inboundCallCenterButton.setToolTipText("Inbound CallCenter is Running");
                            showStatusInbound("Inbound CallCenter Running", true, true);
                            inboundCallCenterUnrespondingTimer = 0;
                        }
                    }
                }
                else // InboundCallCenterShouldNotBeRunning
                {
                    if  (inboundCallCenterState == CONNECTED)
                    {
                        if (inboundCallCenterStatus == RUNNING)
                        {
                            inboundCallCenterButton.setForeground(Color.BLACK);
                            inboundCallCenterButton.setToolTipText("Stop Inbound CallCenter");
                            closeInboundCallCenter();
                            inboundCallCenterStatus = NOTRUNNING;
                            try { Thread.sleep(1000); } catch (InterruptedException ex) { }
                            showStatusInbound("Inbound CallCenter Stopped", true, true);
                        }
                    }
                    else // DISCONNECTED
                    {
                        inboundCallCenterButton.setForeground(Color.BLACK);
                        inboundCallCenterState = DISCONNECTED;
                        inboundCallCenterStatus = NOTRUNNING;
                    }
                    inboundCallCenterButton.setForeground(Color.BLACK);
                    inboundCallCenterState = DISCONNECTED;
                    inboundCallCenterStatus = NOTRUNNING;
                }

    // Outbound CallCenter
                if (outboundCallCenterShouldBeRunning)
                {
                    if      (outboundCallCenterState == DISCONNECTED)
                    {
                        if      (outboundCallCenterStatus == NOTRUNNING) // NotRunning to Starting Transition
                        {

                            if (callCenterEnabledCheckBox.isSelected())
                            {
                                if ( campaign.getTestCampaign())
                                {
                                    if ( inboundCallCenterState == CONNECTED )
                                    {
                                        outboundCallCenterButton.setForeground(Color.RED);
                                        startOutboundCallCenter(campaign, true, platformIsNetManaged);
                                        outboundCallCenterStatus = STARTING;
                                        outboundCallCenterButton.setToolTipText("Outbound CallCenter Starting");
                                        outboundCallCenterStartingTimer = 0;
                                    }
                                    else
                                    {
                                        outboundCallCenterButton.setForeground(Color.YELLOW);
                                        showStatusOutbound("Outbound CallCenter Start Postponed", false, false);
                                    }
                                }
                                else
                                {
                                    outboundCallCenterButton.setForeground(Color.RED);
                                    startOutboundCallCenter(campaign, true, platformIsNetManaged);
                                    outboundCallCenterStatus = STARTING;
                                    outboundCallCenterButton.setToolTipText("Outbound CallCenter Starting");
                                    outboundCallCenterStartingTimer = 0;
                                }
                            }
                            else
                            {
                                showStatusOutbound("Outbound CallCenter Starting Disabled!", false, false);
                            } // true is animated Button, from here it should go to connected
                        }
                        else if (outboundCallCenterStatus == STARTING) // Starting Timer
                        {
                            outboundCallCenterButton.setForeground(Color.YELLOW);
                            if ( outboundCallCenterStartingTimer == outboundCallCenterStartingTimerLimit ) { outboundCallCenterStatus = NOTRUNNING; }
                            showStatusOutbound("Outbound CallCenter Starting [" + Integer.toString(outboundCallCenterStartingTimer) + "-" + Integer.toString(outboundCallCenterStartingTimerLimit) + "]", false, false);
                            outboundCallCenterStartingTimer++;
                        }
                        else if (outboundCallCenterStatus == RUNNING) // Running to Failing Transition
                        {
                            outboundCallCenterButton.setForeground(Color.RED);
                            outboundCallCenterButton.setToolTipText("Outbound CallCenter Disconnected");
                            outboundCallCenterStatus = FAILING;
                            showStatusOutbound("Outbound CallCenter Disconnected", true, true);
                            outboundCallCenterUnrespondingTimer = 0;
                        }
                        else if (outboundCallCenterStatus == FAILING) // Failing Timer
                        {
                            outboundCallCenterButton.setForeground(Color.GRAY);
                            if ( outboundCallCenterUnrespondingTimer == outboundCallCenterUnrespondingTimerLimit ) { outboundCallCenterStatus = NOTRUNNING; outboundCallCenterUnrespondingTimer = 0; }
                            showStatusOutbound("Outbound CallCenter Disconnected [" + Integer.toString(outboundCallCenterUnrespondingTimer) + "-" + Integer.toString(outboundCallCenterUnrespondingTimerLimit) + "]", false, false);
                            outboundCallCenterUnrespondingTimer++;
                        }
                    }
                    else // outboundCallCenterState == CONNECTED
                    {
                        if      (outboundCallCenterStatus == STARTING) // Starting to Running Transition
                        {
                            outboundCallCenterButton.setForeground(Color.GREEN);
                            outboundCallCenterStatus = RUNNING;
                            outboundCallCenterButton.setToolTipText("Outbound CallCenter is Running");
                            showStatusOutbound("Outbound CallCenter Running", true, true);
                            outboundCallCenterStartingTimer = 0;
                        }
                        else if (outboundCallCenterStatus == RUNNING) // Steady Operation
                        {
                            outboundCallCenterButton.setForeground(Color.GREEN);
                            checkOutboundStalling();
                        }
                        else if (outboundCallCenterStatus == FAILING) // Disconnected to Connected Transition
                        {
                            outboundCallCenterButton.setForeground(Color.GREEN);
                            outboundCallCenterStatus = RUNNING;
                            outboundCallCenterButton.setToolTipText("Outbound CallCenter is Running");
                            showStatusOutbound("Outbound CallCenter Running", true, true);
                            outboundCallCenterUnrespondingTimer = 0;
                        }
                    }
                }
                else // OutboundCallCenterShouldNotBeRunning
                {
                    if  (outboundCallCenterState == CONNECTED)
                    {
                        if (outboundCallCenterStatus == RUNNING)
                        {
                            outboundCallCenterButton.setForeground(Color.BLACK);
                            outboundCallCenterButton.setToolTipText("Stop Outbound CallCenter");
                            closeOutboundCallCenter();
                            outboundCallCenterStatus = NOTRUNNING;
                            try { Thread.sleep(1000); } catch (InterruptedException ex) { }
                            showStatusOutbound("Outbound CallCenter Stopped", true, true);
                            if (campaignProgressBar.isVisible()) { campaignProgressBar.setValue(0); campaignProgressBar.setEnabled(false); campaignProgressBar.setVisible(false); }
                        }
                    }
                    else // DISCONNECTED
                    {
                        outboundCallCenterButton.setForeground(Color.BLACK);
                        outboundCallCenterState = DISCONNECTED;
                        outboundCallCenterStatus = NOTRUNNING;
                    }
                }
            }
            else
            {
                if (inboundCallCenterShouldBeRunning)  { inboundCallCenterButton.setForeground(Color.BLUE); } else { inboundCallCenterButton.setForeground(Color.BLACK); }
                if (outboundCallCenterShouldBeRunning) { outboundCallCenterButton.setForeground(Color.BLUE); } else { outboundCallCenterButton.setForeground(Color.BLACK); }
            }
        }
        else
        {
            if ( vergunning != null ) { vergunning.controleerVergunning(); }
            callPerHourScaleNeedsRescaling = true;
        }
    }
//(stallingCheckCounter == stallingCheckCounterLimit)
    private void checkOutboundStalling() // Inbound Stalling is handled by Inbound CallCenter internally as it doesn't write to CampaignStat Table in DB
    {
        if (
                (
                    (( outboundCallCenterStatusDescription.equals("RUNNING")) || ( outboundCallCenterStatusDescription.equals("LOADING CAMPAIGN"))) &&
                    (stallingCheckCounter == stallingCheckCounterLimit)
                )
           )
        {
            if ( (campaignStat.getCallingTT() - lastStallingDetectorCampaignStat.getCallingTT()) >= 2 ) { stalling = false; } else { stalling = true; }
            try { lastStallingDetectorCampaignStat = (CampaignStat) campaignStat.clone(); } catch (CloneNotSupportedException ex) { /* Nonsens in this case*/ }
        }

        if (stalling)
        {
            outboundCallCenterButton.setForeground(Color.ORANGE); // Bad news!
            showStatusOutbound("CallCenter Stalling - Shutdown in " + outboundCallCenterIsStallingCounter + " Sec", false, false);
            outboundCallCenterIsStallingCounter--;
            if (outboundCallCenterIsStallingCounter == 0)
            {
                showStatusOutbound("Outbound CallCenter Stalling Shutdown Now!", true, true);
                closeOutboundCallCenter(); outboundCallCenterIsStallingCounter = outboundCallCenterIsStallingGraceTime;
            }
        }
        else
        {
            outboundCallCenterButton.setForeground(Color.GREEN);
            outboundCallCenterIsStallingCounter = outboundCallCenterIsStallingGraceTime;
            showStatusOutbound("Campaign " + campaign.getId() + " is " + outboundCallCenterStatusDescription + "...", false, false);
        }
        if (stallingCheckCounter == stallingCheckCounterLimit) { stallingCheckCounter = 0; } else { stallingCheckCounter++; } // Loops from 0 to 10 and over again to create a trigger every 10 seconds
    }

    /**
     *
     * @param colorParam
     * @param buttonText
     */
    public void blinkInboundNetManagerToggleButton(final Color colorParam, final String buttonText)
    {
	Thread blinkInboundNetManagerToggleButtonThread = new Thread(new Runnable()
	{
	    @Override
	    @SuppressWarnings({"static-access", "empty-statement"})
	    public void run()
	    {
                netManagerInboundClientToggleButton.setForeground(colorParam);
                netManagerInboundClientToggleButton.setText(buttonText);
	    }
	});
	blinkInboundNetManagerToggleButtonThread.setName("blinkInboundNetManagerToggleButtonThread");
	blinkInboundNetManagerToggleButtonThread.setDaemon(runThreadsAsDaemons);
	blinkInboundNetManagerToggleButtonThread.start();
    }

    /**
     *
     * @param colorParam
     * @param buttonText
     */
    public void blinkOutboundNetManagerToggleButton(final Color colorParam, final String buttonText)
    {
	Thread blinkOutboundNetManagerToggleButtonThread = new Thread(new Runnable()
	{
	    @Override
	    @SuppressWarnings({"static-access", "empty-statement"})
	    public void run()
	    {
                netManagerOutboundClientToggleButton.setForeground(colorParam);
                netManagerOutboundClientToggleButton.setText(buttonText);
	    }
	});
	blinkOutboundNetManagerToggleButtonThread.setName("blinkOutboundNetManagerToggleButtonThread");
	blinkOutboundNetManagerToggleButtonThread.setDaemon(runThreadsAsDaemons);
	blinkOutboundNetManagerToggleButtonThread.start();
    }

    /**
     *
     * @param animateButtonParam
     * @param managedModeParam
     */
    public void startCallCenterLeft(final boolean animateButtonParam, final boolean managedModeParam)
    {
        Thread startCallCenterLeftThread = new Thread(new Runnable()
        {
            @Override
            @SuppressWarnings({"static-access"})
            public void run()
            {
                showStatus("Starting CallCenter", true, true);
                if (animateButtonParam) { outboundCallCenterButton.setSelected(true); }
                try { Thread.sleep(100); } catch (InterruptedException ex) { }
                if (animateButtonParam) { outboundCallCenterButton.setSelected(false); }
                if (managedModeParam) { shell.startManagedCallCenterLeft(heapmem, javaOptionsField.getText()); } else { shell.startUnManagedCallCenterLeft(heapmem, javaOptionsField.getText()); }
            }
        });
        startCallCenterLeftThread.setName("startCallCenterLeftThread");
        startCallCenterLeftThread.setDaemon(runThreadsAsDaemons);
        startCallCenterLeftThread.start();
    }

    /**
     *
     * @param animateButtonParam
     * @param managedModeParam
     */
    public void startCallCenterRight(final boolean animateButtonParam, final boolean managedModeParam)
    {
        Thread startCallCenterRightThread = new Thread(new Runnable()
        {
            @Override
            @SuppressWarnings({"static-access"})
            public void run()
            {
                showStatusInbound("Starting CallCenter", true, true);
                if (animateButtonParam) { outboundCallCenterButton.setSelected(true); }
                try { Thread.sleep(100); } catch (InterruptedException ex) { }
                if (animateButtonParam) { outboundCallCenterButton.setSelected(false); }
                if (managedModeParam) { shell.startManagedCallCenterRight(heapmem, javaOptionsField.getText()); } else { shell.startUnManagedCallCenterRight(heapmem, javaOptionsField.getText()); }
            }
        });
        startCallCenterRightThread.setName("startCallCenterRightThread");
        startCallCenterRightThread.setDaemon(runThreadsAsDaemons);
        startCallCenterRightThread.start();
    }

    /**
     *
     * @param campaignParam
     * @param animateButtonParam
     * @param managedModeParam
     */
    public void startOutboundCallCenter(Campaign campaignParam, final boolean animateButtonParam, final boolean managedModeParam)
    {
        try { tmpCampaign = (Campaign) campaignParam.clone(); } catch (CloneNotSupportedException ex) { /*Nonsens, it IS clonable!*/ }
        Thread startOutboundCallCenterThread = new Thread(new Runnable()
        {
            @Override
            @SuppressWarnings({"static-access"})
            public void run()
            {
                showStatusOutbound("Starting Outbound CallCenter Running Campaign: " + tmpCampaign.getId(), true, true);
                if (animateButtonParam) { outboundCallCenterButton.setSelected(true); }
                try { Thread.sleep(100); } catch (InterruptedException ex) { }
                if (animateButtonParam) { outboundCallCenterButton.setSelected(false); }
                if (managedModeParam)
                {
                    shell.startManagedCallCenterOutbound(tmpCampaign.getId(),   (int)(heapmem + Math.round((double)order.getTargetTransactionQuantity() / 1000)), javaOptionsField.getText());
                }
                else
                {
                    shell.startUnManagedCallCenterOutbound(tmpCampaign.getId(), (int)(heapmem + Math.round((double)order.getTargetTransactionQuantity() / 1000)), javaOptionsField.getText());
                }
            }
        });
        startOutboundCallCenterThread.setName("startOutboundCallCenterThread");
        startOutboundCallCenterThread.setDaemon(runThreadsAsDaemons);
        startOutboundCallCenterThread.start();
    }

    /**
     *
     * @param animateButtonParam
     * @param managedModeParam
     */
    public void startInboundCallCenter(final boolean animateButtonParam, final boolean managedModeParam)
    {
        Thread startInboundCallCenterThread = new Thread(new Runnable()
        {
            @Override
            @SuppressWarnings({"static-access"})
            public void run()
            {
                showStatusInbound("Starting Inbound Test CallCenter", true, true);
                if (animateButtonParam) { inboundCallCenterButton.setSelected(true); }
                try { Thread.sleep(100); } catch (InterruptedException ex) { }
                if (animateButtonParam) { inboundCallCenterButton.setSelected(false); }
                if (managedModeParam)
                {
                    shell.startManagedCallCenterInbound(heapmem, javaOptionsField.getText());
                }
                else
                {
                    shell.startUnManagedCallCenterInbound(heapmem, javaOptionsField.getText());
                }
            }
        });
        startInboundCallCenterThread.setName("startInboundCallCenterThread");
        startInboundCallCenterThread.setDaemon(runThreadsAsDaemons);
        startInboundCallCenterThread.start();
    }

    private void displayCampaign(Campaign campaignParam, Order orderParam, Customer customerParam, CampaignStat campaignStatParam)
    {
        // Set Customer Data
        customerLabel.setText("Customer " + customerParam.getId());
        customerTable.setValueAt(customerParam.getCompanyName(), 0, 1);
        customerTable.setValueAt(customerParam.getAddress() + " " + customer.getAddressNr(), 1, 1);
        customerTable.setValueAt(customerParam.getpostcode() + " " + customer.getCity(), 2, 1);
        customerTable.setValueAt(customerParam.getCountry(), 3, 1);
        customerTable.setValueAt(customerParam.getPhoneNr(), 4, 1);
        customerTable.setValueAt(customerParam.getMobileNr(), 5, 1);
        customerTable.setValueAt(customerParam.getEmail(), 6, 1);

        // Sets the Order Object and after that displays the OrderMembers in the orderTable and turnover info
        orderLabel.setText("Order " + orderParam.getOrderId());
        orderTable.setValueAt(orderParam.getRecipientsCategory(), 0, 1);
//        orderTable.setValueAt(orderParam.getTimeWindowCategory(), 1, 1);
        orderTable.setValueAt(orderParam.getTimeWindow0() + " " + orderParam.getTimeWindow1() + " " + orderParam.getTimeWindow2(), 1, 1);
        orderTable.setValueAt(orderParam.getTargetTransactionQuantity(), 2, 1);
        orderTable.setValueAt(orderParam.getTargetTransactionQuantity(), 2, 1);
        orderTable.setValueAt(orderParam.getMessageDuration() + " Sec", 3, 1);
        orderTable.setValueAt(orderParam.getMessageRatePerSecond() + " / Sec", 4, 1);
        orderTable.setValueAt(orderParam.getMessageRate(), 5, 1);
        orderTable.setValueAt(orderParam.getSubTotal(), 6, 1);

        // Scheduled Start
        campaignLabel.setText("Campaign " + campaignParam.getId());
        if (campaignParam.getCalendarScheduledStart().getTimeInMillis() != 0)
        {
            campaignTable.setValueAt(
                                            String.format("%04d", campaignParam.getCalendarScheduledStart().get(Calendar.YEAR)) + "-" +
                                            String.format("%02d", campaignParam.getCalendarScheduledStart().get(Calendar.MONTH) + 1) + "-" +
                                            String.format("%02d", campaignParam.getCalendarScheduledStart().get(Calendar.DAY_OF_MONTH)) + " " +
                                            String.format("%02d", campaignParam.getCalendarScheduledStart().get(Calendar.HOUR_OF_DAY)) + ":" +
                                            String.format("%02d", campaignParam.getCalendarScheduledStart().get(Calendar.MINUTE)) + ":" +
                                            String.format("%02d", campaignParam.getCalendarScheduledStart().get(Calendar.SECOND))
                                            , 0, 1);
        }
        // Scheduled End
        if (campaignParam.getCalendarScheduledEnd().getTimeInMillis() != 0)
        {
            campaignTable.setValueAt(
                                            String.format("%04d", campaignParam.getCalendarScheduledEnd().get(Calendar.YEAR)) + "-" +
                                            String.format("%02d", campaignParam.getCalendarScheduledEnd().get(Calendar.MONTH) + 1) + "-" +
                                            String.format("%02d", campaignParam.getCalendarScheduledEnd().get(Calendar.DAY_OF_MONTH)) + " " +
                                            String.format("%02d", campaignParam.getCalendarScheduledEnd().get(Calendar.HOUR_OF_DAY)) + ":" +
                                            String.format("%02d", campaignParam.getCalendarScheduledEnd().get(Calendar.MINUTE)) + ":" +
                                            String.format("%02d", campaignParam.getCalendarScheduledEnd().get(Calendar.SECOND))
                                            , 1, 1);
        }
        // Expect Start
        if (campaignParam.getCalendarExpectedStart().getTimeInMillis() != 0)
        {
            campaignTable.setValueAt(
                                            String.format("%04d", campaignParam.getCalendarExpectedStart().get(Calendar.YEAR)) + "-" +
                                            String.format("%02d", campaignParam.getCalendarExpectedStart().get(Calendar.MONTH) + 1) + "-" +
                                            String.format("%02d", campaignParam.getCalendarExpectedStart().get(Calendar.DAY_OF_MONTH)) + " " +
                                            String.format("%02d", campaignParam.getCalendarExpectedStart().get(Calendar.HOUR_OF_DAY)) + ":" +
                                            String.format("%02d", campaignParam.getCalendarExpectedStart().get(Calendar.MINUTE)) + ":" +
                                            String.format("%02d", campaignParam.getCalendarExpectedStart().get(Calendar.SECOND))
                                            , 2, 1);
        }
        // Expect End
        if (campaignParam.getCalendarExpectedEnd().getTimeInMillis() != 0)
        {
            campaignTable.setValueAt(
                                            String.format("%04d", campaignParam.getCalendarExpectedEnd().get(Calendar.YEAR)) + "-" +
                                            String.format("%02d", campaignParam.getCalendarExpectedEnd().get(Calendar.MONTH) + 1) + "-" +
                                            String.format("%02d", campaignParam.getCalendarExpectedEnd().get(Calendar.DAY_OF_MONTH)) + " " +
                                            String.format("%02d", campaignParam.getCalendarExpectedEnd().get(Calendar.HOUR_OF_DAY)) + ":" +
                                            String.format("%02d", campaignParam.getCalendarExpectedEnd().get(Calendar.MINUTE)) + ":" +
                                            String.format("%02d", campaignParam.getCalendarExpectedEnd().get(Calendar.SECOND))
                                            , 3, 1);
        }
        // Registered Start
        if (campaignParam.getCalendarRegisteredStart().getTimeInMillis() != 0)
        {
            campaignTable.setValueAt(
                                            String.format("%04d", campaignParam.getCalendarRegisteredStart().get(Calendar.YEAR)) + "-" +
                                            String.format("%02d", campaignParam.getCalendarRegisteredStart().get(Calendar.MONTH) + 1) + "-" +
                                            String.format("%02d", campaignParam.getCalendarRegisteredStart().get(Calendar.DAY_OF_MONTH)) + " " +
                                            String.format("%02d", campaignParam.getCalendarRegisteredStart().get(Calendar.HOUR_OF_DAY)) + ":" +
                                            String.format("%02d", campaignParam.getCalendarRegisteredStart().get(Calendar.MINUTE)) + ":" +
                                            String.format("%02d", campaignParam.getCalendarRegisteredStart().get(Calendar.SECOND))
                                            , 4, 1);
        }
        // Registered End
        if (campaignParam.getCalendarRegisteredEnd().getTimeInMillis() != 0)
        {
            campaignTable.setValueAt(
                                            String.format("%04d", campaignParam.getCalendarRegisteredEnd().get(Calendar.YEAR)) + "-" +
                                            String.format("%02d", campaignParam.getCalendarRegisteredEnd().get(Calendar.MONTH) + 1) + "-" +
                                            String.format("%02d", campaignParam.getCalendarRegisteredEnd().get(Calendar.DAY_OF_MONTH)) + " " +
                                            String.format("%02d", campaignParam.getCalendarRegisteredEnd().get(Calendar.HOUR_OF_DAY)) + ":" +
                                            String.format("%02d", campaignParam.getCalendarRegisteredEnd().get(Calendar.MINUTE)) + ":" +
                                            String.format("%02d", campaignParam.getCalendarRegisteredEnd().get(Calendar.SECOND))
                                            , 5, 1);
        }
        campaignTable.setValueAt(campaign.getTestCampaign(), 6, 1);

        // Display the counters in the caption table
        captionTable.setValueAt(campaignStatParam.getOnAC(),                                            1, 0);
        captionTable.setValueAt(campaignStatParam.getIdleAC(),                                          1, 1);
        captionTable.setValueAt(campaignStatParam.getConnectingAC(),                                    1, 2); // Connect
        captionTable.setValueAt(campaignStatParam.getConnectingTT(),                                    2, 2); // Connect
        captionTable.setValueAt(campaignStatParam.getTryingAC(),                                        1, 3); // Trying
        captionTable.setValueAt(campaignStatParam.getTryingTT(),                                        2, 3); // Trying
        captionTable.setValueAt(campaignStatParam.getCallingAC(),                                       1, 4); // Calling
        captionTable.setValueAt(campaignStatParam.getCallingTT(),                                       2, 4); // Calling
        captionTable.setValueAt(campaignStatParam.getRingingAC(),                                       1, 5); // Ringing
        captionTable.setValueAt(campaignStatParam.getRingingTT(),                                       2, 5); // Ringing
        captionTable.setValueAt(campaignStatParam.getAcceptingAC(),                                     1, 6); // Accepting
        captionTable.setValueAt(campaignStatParam.getAcceptingTT(),                                     2, 6); // Accepted
        captionTable.setValueAt(campaignStatParam.getTalkingAC(),                                       1, 7); // Talking
        captionTable.setValueAt(campaignStatParam.getTalkingTT(),                                       2, 7); // Talking
        captionTable.setValueAt(icons.getUpChar()   + campaignStatParam.getLocalCancelTT(),		1, 8);
        captionTable.setValueAt(icons.getDownChar() + campaignStatParam.getRemoteCancelTT(),            2, 8);
        captionTable.setValueAt(icons.getUpChar()   + campaignStatParam.getLocalBusyTT(),		1, 9);
        captionTable.setValueAt(icons.getDownChar() + campaignStatParam.getRemoteBusyTT(),		2, 9);
        captionTable.setValueAt(icons.getUpChar()   + campaignStatParam.getLocalByeTT(),		1, 10);
        captionTable.setValueAt(icons.getDownChar() + campaignStatParam.getRemoteByeTT(),		2, 10);

        // Display Progress Bar
        campaignProgressBar.setValue(campaignStat.getCallingTT());
    }

    private void resetDisplay()
    {
        // Set Customer Data
        customerLabel.setText("Customer");
        customerTable.setValueAt("-", 0, 1);
        customerTable.setValueAt("-", 1, 1);
        customerTable.setValueAt("-", 2, 1);
        customerTable.setValueAt("-", 3, 1);
        customerTable.setValueAt("-", 4, 1);
        customerTable.setValueAt("-", 5, 1);
        customerTable.setValueAt("-", 6, 1);

        // Sets the Order Object and after that displays the OrderMembers in the orderTable and turnover info
        orderLabel.setText("Order");
        orderTable.setValueAt("-", 0, 1);
        orderTable.setValueAt("-", 1, 1);
        orderTable.setValueAt("-", 2, 1);
        orderTable.setValueAt("-", 2, 1);
        orderTable.setValueAt("-", 3, 1);
        orderTable.setValueAt("-", 4, 1);
        orderTable.setValueAt("-", 5, 1);
        orderTable.setValueAt("-", 6, 1);

        campaignLabel.setText("Campaign");
        campaignTable.setValueAt("-", 0,1);
        campaignTable.setValueAt("-", 1,1);
        campaignTable.setValueAt("-", 2,1);
        campaignTable.setValueAt("-", 3,1);
        campaignTable.setValueAt("-", 4,1);
        campaignTable.setValueAt("-", 5,1);
        campaignTable.setValueAt("-", 6,1);

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

        // Display the counters in the caption table
        captionTable.setValueAt("", 1, 0);
        captionTable.setValueAt("", 1, 1);
        captionTable.setValueAt("", 1, 2); // Connecting
        captionTable.setValueAt("", 2, 2); // Connecting
        captionTable.setValueAt("", 1, 3); // Trying
        captionTable.setValueAt("", 2, 3); // Trying
        captionTable.setValueAt("", 1, 4); // Calling
        captionTable.setValueAt("", 2, 4); // Calling
        captionTable.setValueAt("", 1, 5); // Ringing
        captionTable.setValueAt("", 2, 5); // Ringing
        captionTable.setValueAt("", 1, 6); // Accepting
        captionTable.setValueAt("", 2, 6); // Accepted
        captionTable.setValueAt("", 1, 7); // Talking
        captionTable.setValueAt("", 2, 7); // Talking
        captionTable.setValueAt("", 1, 8);
        captionTable.setValueAt("", 2, 8);
        captionTable.setValueAt("", 1, 9);
        captionTable.setValueAt("", 2, 9);
        captionTable.setValueAt("", 1, 10);
        captionTable.setValueAt("", 2, 10);
    }

    private void customerButtonsControl()
    {
        // All Fields empty
        if (
                ( customerIdField.getText().length() == 0) &&
                ( customerCompanyNameField.getText().length() == 0) &&
                ( customerAddressField.getText().length() == 0) &&
                ( customerAddressNrField.getText().length() == 0) &&
                ( customerPostcodeField.getText().length() == 0) &&
                ( customerCityField.getText().length() == 0) &&
                ( customerCountryField.getText().length() == 0) &&
                ( customerContactNameField.getText().length() == 0) &&
                ( customerEmailField.getText().length() == 0) &&
                ( customerPhoneNrField.getText().length() == 0) &&
                ( customerMobileNrField.getText().length() == 0) &&
                ( customerDiscountField.getText().length() == 0)
           )
        {
                clearCustomerFieldsButton.setEnabled(false);
                exampleCustomerButton.setEnabled(true);
//                searchCustomerButton.setEnabled(true);
                selectCustomerButton.setEnabled(false);
                insertCustomerButton.setEnabled(false);
                updateCustomerButton.setEnabled(false);
                deleteCustomerButton.setEnabled(false);
        }

        // Only Idfield populated
        else if (
                    ( customerIdField.getText().length() != 0) &&
                    ( customerCompanyNameField.getText().length() == 0) &&
                    ( customerAddressField.getText().length() == 0) &&
                    ( customerAddressNrField.getText().length() == 0) &&
                    ( customerPostcodeField.getText().length() == 0) &&
                    ( customerCityField.getText().length() == 0) &&
                    ( customerCountryField.getText().length() == 0) &&
                    ( customerContactNameField.getText().length() == 0) &&
                    ( customerEmailField.getText().length() == 0) &&
                    ( customerPhoneNrField.getText().length() == 0) &&
                    ( customerMobileNrField.getText().length() == 0) &&
                    ( customerDiscountField.getText().length() == 0)
               )
        {
                clearCustomerFieldsButton.setEnabled(true);
                exampleCustomerButton.setEnabled(true);
//                searchCustomerButton.setEnabled(true);
                selectCustomerButton.setEnabled(true);
                insertCustomerButton.setEnabled(false);
                updateCustomerButton.setEnabled(false);
                deleteCustomerButton.setEnabled(false);
        }

        // Idfield populated & any other field
        else if (
                    ( customerIdField.getText().length() != 0) &&
                        (
                            ( customerCompanyNameField.getText().length() != 0) ||
                            ( customerAddressField.getText().length() != 0) ||
                            ( customerAddressNrField.getText().length() != 0) ||
                            ( customerPostcodeField.getText().length() != 0) ||
                            ( customerCityField.getText().length() != 0) ||
                            ( customerCountryField.getText().length() != 0) ||
                            ( customerContactNameField.getText().length() != 0) ||
                            ( customerEmailField.getText().length() != 0) ||
                            ( customerPhoneNrField.getText().length() != 0) ||
                            ( customerMobileNrField.getText().length() != 0) ||
                            ( customerDiscountField.getText().length() != 0)
                        )
                )
        {
                clearCustomerFieldsButton.setEnabled(true);
                exampleCustomerButton.setEnabled(true);
//                searchCustomerButton.setEnabled(true);
                selectCustomerButton.setEnabled(true);
                insertCustomerButton.setEnabled(false);
                updateCustomerButton.setEnabled(true);
                deleteCustomerButton.setEnabled(true);
        }

        // Idfield empty any other field populated
        else if (
                ( customerIdField.getText().length() == 0) &&
                    (
                        ( customerCompanyNameField.getText().length() != 0) ||
                        ( customerAddressField.getText().length() != 0) ||
                        ( customerAddressNrField.getText().length() != 0) ||
                        ( customerPostcodeField.getText().length() != 0) ||
                        ( customerCityField.getText().length() != 0) ||
                        ( customerCountryField.getText().length() != 0) ||
                        ( customerContactNameField.getText().length() != 0) ||
                        ( customerEmailField.getText().length() != 0) ||
                        ( customerPhoneNrField.getText().length() != 0) ||
                        ( customerMobileNrField.getText().length() != 0) ||
                        ( customerDiscountField.getText().length() != 0)
                    )
                )
        {
                clearCustomerFieldsButton.setEnabled(true);
                exampleCustomerButton.setEnabled(true);
//                searchCustomerButton.setEnabled(true);
                selectCustomerButton.setEnabled(false);
                insertCustomerButton.setEnabled(true);
                updateCustomerButton.setEnabled(false);
                deleteCustomerButton.setEnabled(false);
        }
    }

    /**
     *
     */
    protected void orderButtonsControl()
    {
        // All Fields empty
        if (
                ( (!orderIdComboBox.isEnabled()) && ( orderIdComboBox.getSelectedIndex()             != -1 )) &&
                ( (!orderCustomerIdComboBox.isEnabled()) && ( orderIdComboBox.getSelectedIndex()     != -1 ))
           )
        {
                selectOrderButton.setEnabled(false);
                updateOrderButton.setEnabled(false);
                deleteOrderButton.setEnabled(false);
        }

        // Only OrderIdfield populated
        else if (
                    ( (orderIdComboBox.isEnabled()) && ( orderIdComboBox.getSelectedIndex()          != -1 )) &&
                    ( (!orderCustomerIdComboBox.isEnabled()) && ( orderIdComboBox.getSelectedIndex() == -1 ))
               )
        {
                selectOrderButton.setEnabled(true);
                updateOrderButton.setEnabled(false);
                deleteOrderButton.setEnabled(false);
        }

        // OrderIdfield populated & any other field
        else if (
                    ( (orderIdComboBox.isEnabled()) && ( orderIdComboBox.getSelectedIndex()         != -1 )) &&
                    ( (orderCustomerIdComboBox.isEnabled()) && ( orderIdComboBox.getSelectedIndex() != -1 ))
                )
        {
                selectOrderButton.setEnabled(true);
                updateOrderButton.setEnabled(true);
                deleteOrderButton.setEnabled(true);
        }

        // Idfield empty any other field populated
        else if (
                    ( (!orderIdComboBox.isEnabled()) && ( orderIdComboBox.getSelectedIndex()         == -1 )) &&
                    ( (orderCustomerIdComboBox.isEnabled()) && ( orderIdComboBox.getSelectedIndex()  != -1 ))
                )
        {
                selectOrderButton.setEnabled(false);
                updateOrderButton.setEnabled(false);
                deleteOrderButton.setEnabled(false);
        }
    }

    private void resellerButtonsControl()
    {
        // All Fields empty
        if (
                ( resellerIdField.getText().length() == 0) &&
                ( resellerCompanyNameField.getText().length() == 0) &&
                ( resellerAddressField.getText().length() == 0) &&
                ( resellerAddressNrField.getText().length() == 0) &&
                ( resellerPostcodeField.getText().length() == 0) &&
                ( resellerCityField.getText().length() == 0) &&
                ( resellerCountryField.getText().length() == 0) &&
                ( resellerContactNameField.getText().length() == 0) &&
                ( resellerEmailField.getText().length() == 0) &&
                ( resellerPhoneNrField.getText().length() == 0) &&
                ( resellerMobileNrField.getText().length() == 0) &&
                ( resellerDiscountField.getText().length() == 0)
           )
        {
                clearResellerFieldsButton.setEnabled(false);
                exampleResellerButton.setEnabled(true);
//                searchResellerButton.setEnabled(true);
                selectResellerButton.setEnabled(false);
                insertResellerButton.setEnabled(false);
                updateResellerButton.setEnabled(false);
                deleteResellerButton.setEnabled(false);
        }

        // Only Idfield populated
        else if (
                    ( resellerIdField.getText().length() != 0) &&
                    ( resellerCompanyNameField.getText().length() == 0) &&
                    ( resellerAddressField.getText().length() == 0) &&
                    ( resellerAddressNrField.getText().length() == 0) &&
                    ( resellerPostcodeField.getText().length() == 0) &&
                    ( resellerCityField.getText().length() == 0) &&
                    ( resellerCountryField.getText().length() == 0) &&
                    ( resellerContactNameField.getText().length() == 0) &&
                    ( resellerEmailField.getText().length() == 0) &&
                    ( resellerPhoneNrField.getText().length() == 0) &&
                    ( resellerMobileNrField.getText().length() == 0) &&
                    ( resellerDiscountField.getText().length() == 0)
               )
        {
                clearResellerFieldsButton.setEnabled(true);
                exampleResellerButton.setEnabled(true);
//                searchResellerButton.setEnabled(true);
                selectResellerButton.setEnabled(true);
                insertResellerButton.setEnabled(false);
                updateResellerButton.setEnabled(false);
                deleteResellerButton.setEnabled(false);
        }

        // Idfield populated & any other field
        else if (
                    ( resellerIdField.getText().length() != 0) &&
                        (
                            ( resellerCompanyNameField.getText().length() != 0) ||
                            ( resellerAddressField.getText().length() != 0) ||
                            ( resellerAddressNrField.getText().length() != 0) ||
                            ( resellerPostcodeField.getText().length() != 0) ||
                            ( resellerCityField.getText().length() != 0) ||
                            ( resellerCountryField.getText().length() != 0) ||
                            ( resellerContactNameField.getText().length() != 0) ||
                            ( resellerEmailField.getText().length() != 0) ||
                            ( resellerPhoneNrField.getText().length() != 0) ||
                            ( resellerMobileNrField.getText().length() != 0) ||
                            ( resellerDiscountField.getText().length() != 0)
                        )
                )
        {
                clearResellerFieldsButton.setEnabled(true);
                exampleResellerButton.setEnabled(true);
//                searchResellerButton.setEnabled(true);
                selectResellerButton.setEnabled(true);
                insertResellerButton.setEnabled(false);
                updateResellerButton.setEnabled(true);
                deleteResellerButton.setEnabled(true);
        }

        // Idfield empty any other field populated
        else if (
                ( resellerIdField.getText().length() == 0) &&
                    (
                        ( resellerCompanyNameField.getText().length() != 0) ||
                        ( resellerAddressField.getText().length() != 0) ||
                        ( resellerAddressNrField.getText().length() != 0) ||
                        ( resellerPostcodeField.getText().length() != 0) ||
                        ( resellerCityField.getText().length() != 0) ||
                        ( resellerCountryField.getText().length() != 0) ||
                        ( resellerContactNameField.getText().length() != 0) ||
                        ( resellerEmailField.getText().length() != 0) ||
                        ( resellerPhoneNrField.getText().length() != 0) ||
                        ( resellerMobileNrField.getText().length() != 0) ||
                        ( resellerDiscountField.getText().length() != 0)
                    )
                )
        {
                clearResellerFieldsButton.setEnabled(true);
                exampleResellerButton.setEnabled(true);
//                searchResellerButton.setEnabled(true);
                selectResellerButton.setEnabled(false);
                insertResellerButton.setEnabled(true);
                updateResellerButton.setEnabled(false);
                deleteResellerButton.setEnabled(false);
        }
    }


    private void selectCustomerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectCustomerButtonActionPerformed
        selectCustomer();
}//GEN-LAST:event_selectCustomerButtonActionPerformed

    private void selectCustomer()
    {
        if ( customerIdField.getText().equals("") ) { customerIdField.setText("0"); }
        Customer tmpCustomer = dbClient.selectCustomer(Integer.parseInt(customerIdField.getText()));
        customerIdField.setText(Integer.toString(tmpCustomer.getId()));
        customerDateField.setText(Long.toString(tmpCustomer.getTimestamp()));
        customerCompanyNameField.setText(tmpCustomer.getCompanyName());
        customerAddressField.setText(tmpCustomer.getAddress());
        customerAddressNrField.setText(tmpCustomer.getAddressNr());
        customerPostcodeField.setText(tmpCustomer.getpostcode());
        customerCityField.setText(tmpCustomer.getCity());
        customerCountryField.setText(tmpCustomer.getCountry());
        customerContactNameField.setText(tmpCustomer.getContactName());
        customerEmailField.setText(tmpCustomer.getEmail());
        customerPhoneNrField.setText(tmpCustomer.getPhoneNr());
        customerMobileNrField.setText(tmpCustomer.getMobileNr());
        customerDiscountField.setText(Integer.toString(tmpCustomer.getCustomerDiscount()));
        customerButtonsControl();

        if ( customerIdField.getText().equals("") ) { customerIdField.setText("0"); }
        tmpCustomer = dbClient.selectCustomer(Integer.parseInt(customerIdField.getText()));
        customerIdField.setText(Integer.toString(tmpCustomer.getId()));
        customerDateField.setText(Long.toString(tmpCustomer.getTimestamp()));
        customerCompanyNameField.setText(tmpCustomer.getCompanyName());
        customerAddressField.setText(tmpCustomer.getAddress());
        customerAddressNrField.setText(tmpCustomer.getAddressNr());
        customerPostcodeField.setText(tmpCustomer.getpostcode());
        customerCityField.setText(tmpCustomer.getCity());
        customerCountryField.setText(tmpCustomer.getCountry());
        customerContactNameField.setText(tmpCustomer.getContactName());
        customerEmailField.setText(tmpCustomer.getEmail());
        customerPhoneNrField.setText(tmpCustomer.getPhoneNr());
        customerMobileNrField.setText(tmpCustomer.getMobileNr());
        customerDiscountField.setText(Integer.toString(tmpCustomer.getCustomerDiscount()));
        customerButtonsControl();
    }

    private void selectCustomerButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_selectCustomerButtonKeyPressed
}//GEN-LAST:event_selectCustomerButtonKeyPressed

    private void insertCustomerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_insertCustomerButtonActionPerformed
        if ( customerDiscountField.getText().length() == 0 ) { customerDiscountField.setText("0"); }
        Customer tmpCustomer = new Customer(
                                                0,
                                                Calendar.getInstance().getTimeInMillis(),
                                                customerCompanyNameField.getText(),
                                                customerAddressField.getText(),
                                                customerAddressNrField.getText(),
                                                customerPostcodeField.getText(),
                                                customerCityField.getText(),
                                                customerCountryField.getText(),
                                                customerContactNameField.getText(),
                                                customerPhoneNrField.getText(),
                                                customerMobileNrField.getText(),
                                                customerEmailField.getText(),
                                                new String("pw"),
                                                Integer.parseInt(customerDiscountField.getText()),
                                                new String("")
                                            );
        dbClient.insertCustomer(tmpCustomer);
        customerRecords.setText(Integer.toString(dbClient.getCustomerCount()));

        tmpCustomer = dbClient.selectLastCustomer();
        customerIdField.setText(Integer.toString(tmpCustomer.getId()));
        customerDateField.setText(Long.toString(tmpCustomer.getTimestamp()));
        customerCompanyNameField.setText(tmpCustomer.getCompanyName());
        customerAddressField.setText(tmpCustomer.getAddress());
        customerAddressNrField.setText(tmpCustomer.getAddressNr());
        customerPostcodeField.setText(tmpCustomer.getpostcode());
        customerCityField.setText(tmpCustomer.getCity());
        customerCountryField.setText(tmpCustomer.getCountry());
        customerContactNameField.setText(tmpCustomer.getContactName());
        customerEmailField.setText(tmpCustomer.getEmail());
        customerPhoneNrField.setText(tmpCustomer.getPhoneNr());
        customerMobileNrField.setText(tmpCustomer.getMobileNr());
        customerDiscountField.setText(Integer.toString(tmpCustomer.getCustomerDiscount()));
        customerButtonsControl();
        refreshCustomersAndOrders();
        campaignTab.setSelectedIndex(1);
}//GEN-LAST:event_insertCustomerButtonActionPerformed

    private void insertCustomerButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_insertCustomerButtonKeyPressed
        if (evt.getKeyCode() == 9) { insertCustomerButton.getNextFocusableComponent().requestFocusInWindow(); }
}//GEN-LAST:event_insertCustomerButtonKeyPressed

    private void updateCustomerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateCustomerButtonActionPerformed
        //System.out.println(Integer.parseInt(idField.getText()));
        Customer tmpCustomer = new Customer(
                                                Integer.parseInt( customerIdField.getText()),
                                                Long.parseLong(customerDateField.getText()),
                                                customerCompanyNameField.getText(),
                                                customerAddressField.getText(),
                                                customerAddressNrField.getText(),
                                                customerPostcodeField.getText(),
                                                customerCityField.getText(),
                                                customerCountryField.getText(),
                                                customerContactNameField.getText(),
                                                customerPhoneNrField.getText(),
                                                customerMobileNrField.getText(),
                                                customerEmailField.getText(),
                                                new String("pw"),
                                                Integer.parseInt(customerDiscountField.getText()),
                                                new String("")
                                           );
        dbClient.updateCustomer(tmpCustomer);
}//GEN-LAST:event_updateCustomerButtonActionPerformed

    private void updateCustomerButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_updateCustomerButtonKeyPressed
        if (evt.getKeyCode() == 9) { updateCustomerButton.getNextFocusableComponent().requestFocusInWindow(); }
}//GEN-LAST:event_updateCustomerButtonKeyPressed

    private void deleteCustomerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteCustomerButtonActionPerformed
        dbClient.deleteCustomer(Integer.parseInt(customerIdField.getText()));
        customerIdField.setText("");
        customerDateField.setText("");
        customerCompanyNameField.setText("");
        customerAddressField.setText("");
        customerAddressNrField.setText("");
        customerPostcodeField.setText("");
        customerCityField.setText("");
        customerCountryField.setText("");
        customerContactNameField.setText("");
        customerEmailField.setText("");
        customerPhoneNrField.setText("");
        customerMobileNrField.setText("");
        customerDiscountField.setText("");
        customerButtonsControl();
        refreshCustomersAndOrders();
        customerRecords.setText(Integer.toString(dbClient.getCustomerCount()));
}//GEN-LAST:event_deleteCustomerButtonActionPerformed

    private void deleteCustomerButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_deleteCustomerButtonKeyPressed
        if (evt.getKeyCode() == 9) { deleteCustomerButton.getNextFocusableComponent().requestFocusInWindow(); }
}//GEN-LAST:event_deleteCustomerButtonKeyPressed

    private void dropCustomerTableButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dropCustomerTableButtonActionPerformed
        dbClient.dropCustomerTable(); dbClient.createCustomerTable();
        updateDatabaseStats();
        customerRecords.setText(Integer.toString(dbClient.getCustomerCount()));
}//GEN-LAST:event_dropCustomerTableButtonActionPerformed

    private void dropCustomerTableButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dropCustomerTableButtonKeyPressed
        if (evt.getKeyCode() == 9) { dropCustomerTableButton.getNextFocusableComponent().requestFocusInWindow(); }
}//GEN-LAST:event_dropCustomerTableButtonKeyPressed

    private void customerIdFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_customerIdFieldKeyPressed
        if (evt.getKeyCode() == 9) { customerIdField.getNextFocusableComponent().requestFocusInWindow(); }
}//GEN-LAST:event_customerIdFieldKeyPressed

    private void customerCompanyNameFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_customerCompanyNameFieldKeyPressed
        if (evt.getKeyCode() == 9) { customerCompanyNameField.getNextFocusableComponent().requestFocusInWindow(); }
}//GEN-LAST:event_customerCompanyNameFieldKeyPressed

    private void customerAddressFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_customerAddressFieldKeyPressed
        if (evt.getKeyCode() == 9) { customerAddressField.getNextFocusableComponent().requestFocusInWindow(); }
}//GEN-LAST:event_customerAddressFieldKeyPressed

    private void customerAddressNrFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_customerAddressNrFieldKeyPressed
        if (evt.getKeyCode() == 9) { customerAddressNrField.getNextFocusableComponent().requestFocusInWindow(); }
}//GEN-LAST:event_customerAddressNrFieldKeyPressed

    private void customerCityFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_customerCityFieldKeyPressed
        if (evt.getKeyCode() == 9) { customerCityField.getNextFocusableComponent().requestFocusInWindow(); }
}//GEN-LAST:event_customerCityFieldKeyPressed

    private void customerPostcodeFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_customerPostcodeFieldKeyPressed
        if (evt.getKeyCode() == 9) { customerPostcodeField.getNextFocusableComponent().requestFocusInWindow(); }
}//GEN-LAST:event_customerPostcodeFieldKeyPressed

    private void customerCountryFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_customerCountryFieldKeyPressed
        if (evt.getKeyCode() == 9) { customerCountryField.getNextFocusableComponent().requestFocusInWindow(); }
}//GEN-LAST:event_customerCountryFieldKeyPressed

    private void customerDateFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_customerDateFieldKeyPressed
}//GEN-LAST:event_customerDateFieldKeyPressed

    private void customerContactNameFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_customerContactNameFieldKeyPressed
        if (evt.getKeyCode() == 9) { customerContactNameField.getNextFocusableComponent().requestFocusInWindow(); }
}//GEN-LAST:event_customerContactNameFieldKeyPressed

    private void customerEmailFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_customerEmailFieldKeyPressed
        if (evt.getKeyCode() == 9) { customerEmailField.getNextFocusableComponent().requestFocusInWindow(); }
}//GEN-LAST:event_customerEmailFieldKeyPressed

    private void customerPhoneNrFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_customerPhoneNrFieldKeyPressed
        if (evt.getKeyCode() == 9) { customerPhoneNrField.getNextFocusableComponent().requestFocusInWindow(); }
}//GEN-LAST:event_customerPhoneNrFieldKeyPressed

    private void customerMobileNrFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_customerMobileNrFieldKeyPressed
        if (evt.getKeyCode() == 9) { customerMobileNrField.getNextFocusableComponent().requestFocusInWindow(); }
}//GEN-LAST:event_customerMobileNrFieldKeyPressed

    private void customerDiscountFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_customerDiscountFieldKeyPressed
        if (evt.getKeyCode() == 9) { customerDiscountField.getNextFocusableComponent().requestFocusInWindow(); }
}//GEN-LAST:event_customerDiscountFieldKeyPressed

    private void customerManagerPanelKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_customerManagerPanelKeyPressed
        if (evt.getKeyCode() == 9) { customerManagerPanel.getNextFocusableComponent().requestFocusInWindow(); }
}//GEN-LAST:event_customerManagerPanelKeyPressed

    private void selectOrderButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectOrderButtonActionPerformed
        tmpOrder = dbClient.selectCustomerOrder(Integer.parseInt(orderIdComboBox.getSelectedItem().toString()));

        orderIdComboBox.setSelectedItem(Integer.toString(tmpOrder.getOrderId()));
        orderCustomerIdComboBox.setSelectedItem(Integer.toString(tmpOrder.getCustomerId()));

        orderDateField.setText(Long.toString(tmpOrder.getOrderTimestamp()));
//        recipientsComboBox.setSelectedItem(tmpOrder.getRecipientsCategory());
        recipientsList.setSelectedValue(tmpOrder.getRecipientsCategory(), false);
//        timewindowComboBox.setSelectedItem(tmpOrder.getTimeWindowCategory());

        timewindowList.setSelectedIndices(tmpOrder.getTimewindowIndexArray());

//        orderDestinationsQuantitySlider.setValue(tmpOrder.getTargetTransactionQuantity());
        orderDestinationsQuantityField.setText(Integer.toString(tmpOrder.getTargetTransactionQuantity()));
        orderFilenameField.setText(tmpOrder.getMessageFilename());
        orderMessageDurationField.setText(Integer.toString(tmpOrder.getMessageDuration()));
        orderMessageRatePerSecondField.setText(Float.toString(tmpOrder.getMessageRatePerSecond()));
        orderMessageRateField.setText(Float.toString(tmpOrder.getMessageRate()));
        orderSubTotalField.setText(Float.toString(tmpOrder.getSubTotal()));
        orderButtonsControl();
}//GEN-LAST:event_selectOrderButtonActionPerformed

    private void insertOrder()
    {
        int[] timewindowIndexArray = new int[3]; Order order = new Order();
        timewindowIndexArray = order.getIndices2Static(timewindowList.getSelectedIndices());

        tmpOrder = new Order(
                                0,
                                Calendar.getInstance().getTimeInMillis(),
                                Integer.parseInt(customerIdField.getText()),
//                                recipientsComboBox.getSelectedItem().toString(),
                                recipientsList.getSelectedValue().toString(),
//                                timewindowComboBox.getSelectedItem().toString(),

                                timewindowIndexArray[0],
                                timewindowIndexArray[1],
                                timewindowIndexArray[2],

//                                orderDestinationsQuantitySlider.getValue(),
                                Integer.parseInt(orderDestinationsQuantityField.getText()),

                                orderFilenameField.getText(),
                                Integer.parseInt(orderMessageDurationField.getText()),
                                Float.parseFloat(orderMessageRatePerSecondField.getText()),
                                Float.parseFloat(orderMessageRateField.getText()),
                                Float.parseFloat(orderSubTotalField.getText())
                            );
        dbClient.insertCustomerOrder(tmpOrder);
    }

    private void updateOrderButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateOrderButtonActionPerformed
        updateOrder();

        int[] timewindowIndexArray = new int[3]; Order order = new Order();
        timewindowIndexArray = order.getIndices2Static(timewindowList.getSelectedIndices());

        int     orderId = Integer.parseInt(orderIdComboBox.getSelectedItem().toString());
        int     customerId = Integer.parseInt(orderCustomerIdComboBox.getSelectedItem().toString());
        long    orderDate = Long.parseLong(orderDateField.getText());
        String  recipient = recipientsList.getSelectedValue().toString();
        int     orderDestinationsQuantity = Integer.parseInt(orderDestinationsQuantityField.getText());
        String  soundFilename = orderFilenameField.getText();
        int     orderMessageDuration = Integer.parseInt(orderMessageDurationField.getText());
        float   orderMessageRatePerSecond = Float.parseFloat(orderMessageRatePerSecondField.getText());
        float   orderMessageRate = Float.parseFloat(orderMessageRateField.getText());
        float   orderSubTotal = Float.parseFloat(orderSubTotalField.getText());

        tmpOrder = new Order( orderId, orderDate, customerId, recipient, timewindowIndexArray[0], timewindowIndexArray[1], timewindowIndexArray[2], orderDestinationsQuantity, soundFilename, orderMessageDuration, orderMessageRatePerSecond, orderMessageRate, orderSubTotal );
        dbClient.updateCustomerOrder(tmpOrder);
}//GEN-LAST:event_updateOrderButtonActionPerformed

    private void deleteOrderButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteOrderButtonActionPerformed
        dbClient.deleteCustomerOrder(Integer.parseInt(orderIdComboBox.getSelectedItem().toString()));
        orderButtonsControl();
        orderRecords.setText(Integer.toString(dbClient.getCustomerOrderCount()));
}//GEN-LAST:event_deleteOrderButtonActionPerformed

    private void dropOrderTableButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dropOrderTableButtonActionPerformed
        dbClient.dropCustomerOrderTable(); dbClient.createCustomerOrderTable();
        orderRecords.setText(Integer.toString(dbClient.getCustomerOrderCount()));
}//GEN-LAST:event_dropOrderTableButtonActionPerformed

    private void orderFilenameFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_orderFilenameFieldKeyReleased
        if (evt.getKeyCode() == 10) { updateOrder(); }
}//GEN-LAST:event_orderFilenameFieldKeyReleased

    private void orderMessageDurationFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_orderMessageDurationFieldKeyPressed
        if (evt.getKeyCode() == 9) { orderMessageDurationField.getNextFocusableComponent().requestFocusInWindow(); }
}//GEN-LAST:event_orderMessageDurationFieldKeyPressed

    private void orderMessageRatePerSecondFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_orderMessageRatePerSecondFieldKeyPressed
        if (evt.getKeyCode() == 9) { orderMessageRatePerSecondField.getNextFocusableComponent().requestFocusInWindow(); }
}//GEN-LAST:event_orderMessageRatePerSecondFieldKeyPressed

    private void orderMessageRateFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_orderMessageRateFieldKeyPressed
        if (evt.getKeyCode() == 9) { orderMessageRateField.getNextFocusableComponent().requestFocusInWindow(); }
}//GEN-LAST:event_orderMessageRateFieldKeyPressed

    private void orderDestinationsQuantityFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_orderDestinationsQuantityFieldKeyPressed
        if (evt.getKeyCode() == 9) { orderDestinationsQuantityField.getNextFocusableComponent().requestFocusInWindow(); }
        if (evt.getKeyCode() == 10) // pressed enter
        {
            try
            {
                if (Integer.valueOf(orderDestinationsQuantityField.getText()) instanceof Integer)
                {
                    if ( Integer.parseInt(orderDestinationsQuantityField.getText()) > 0 )
                    {
                        Thread generateListThread = new Thread(new Runnable() {
                            @Override
                            public void run()
                            {
                                String destinationsString = "";
                                destinationTextArea.setText("");
                                int totalQuantity = Integer.parseInt(orderDestinationsQuantityField.getText());
                                serviceLoopProgressBar.setMaximum(totalQuantity);
                                serviceLoopProgressBar.setEnabled(true);
                                int progressBarIntervalCounter = 0;
                                long destinationStart = Long.parseLong(usernameField.getText());
                                long phonenumber = destinationStart;
                                showStatus("Generating " + totalQuantity + " ☎ nrs...", true, true);
                                for (int destCounter = 0; destCounter < totalQuantity; destCounter++) {
                                    if ( progressBarIntervalCounter == Math.round(totalQuantity / 100)) {
                                        serviceLoopProgressBar.setValue(destCounter);
                                        progressBarIntervalCounter = 0;
                                    }
                                    destinationTextArea.append(Long.toString(phonenumber) + lineTerminator);
                                    phonenumber++; if (phonenumber >= (destinationStart + softphonesQuantity)) { phonenumber = destinationStart; }
                                    progressBarIntervalCounter++;
                                }
                                serviceLoopProgressBar.setValue(0); serviceLoopProgressBar.setEnabled(false);
                                showStatus("Generating " + totalQuantity + " ☎ nrs Completed", true, true);
                                updateOrder();
                            }
                        });
                        generateListThread.setName("generateListThread");
                        generateListThread.setDaemon(runThreadsAsDaemons);
                        generateListThread.start();
                    }
                }
                else
                {
                    orderDestinationsQuantityField.setText("0");
                }
            }
            catch (NumberFormatException ex) {orderDestinationsQuantityField.setText("0");}
        }
}//GEN-LAST:event_orderDestinationsQuantityFieldKeyPressed

    private void orderDateFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_orderDateFieldKeyPressed
        if (evt.getKeyCode() == 9) { orderDateField.getNextFocusableComponent().requestFocusInWindow(); }
}//GEN-LAST:event_orderDateFieldKeyPressed

    private void scheduleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scheduleButtonActionPerformed
        Calendar currentTimePlusOffSetCalendar = Calendar.getInstance();
        if (dbClient.getDBServerTest()) { preparationFactor = testModePreparationFactor; } else { preparationFactor = testModePreparationFactor; }

//        currentTimePlusOffSetCalendar.setTimeInMillis(Calendar.getInstance().getTimeInMillis() + Math.round(orderDestinationsQuantitySlider.getValue() * preparationFactor + 60000));
        currentTimePlusOffSetCalendar.setTimeInMillis(Calendar.getInstance().getTimeInMillis() + Math.round(Integer.parseInt(orderDestinationsQuantityField.getText()) * preparationFactor + 60000));


//        campaignCalendar.setStartTimeWindow(timewindowComboBox.getSelectedItem().toString());

        int timewindowIndex = timewindowList.getSelectedIndices()[0]; campaignCalendar.setStartTimeWindow(timewindowIndex);

        campaignCalendar.hourSlider.setValue(currentTimePlusOffSetCalendar.get(Calendar.HOUR_OF_DAY));
        campaignCalendar.minuteSlider.setValue(currentTimePlusOffSetCalendar.get(Calendar.MINUTE));
//        campaignCalendar.hourSlider.setValue(currentTime.get(Calendar.HOUR_OF_DAY));
//        campaignCalendar.minuteSlider.setValue(currentTime.get(Calendar.MINUTE));

//      Make sure we disable everything before yesterday
        Calendar yesterdayCalendar = Calendar.getInstance(); yesterdayCalendar.setTimeInMillis(Calendar.getInstance().getTimeInMillis() - 86400000); campaignCalendar.dateChooserPanel.setMinDate(yesterdayCalendar);
//        try { campaignCalendar.dateChooserPanel.setForbiddenPeriods(dbClient.getForbiddenCampaignPeriodSet(timewindowComboBox.getSelectedItem().toString())); } catch (IncompatibleDataExeption ex) { }
            try { campaignCalendar.dateChooserPanel.setForbiddenPeriods(dbClient.getForbiddenCampaignPeriodSet(order.getIndices2Static(timewindowList.getSelectedIndices()))); } catch (IncompatibleDataExeption ex) { }
        campaignCalendar.setVisible(true);
}//GEN-LAST:event_scheduleButtonActionPerformed

    @SuppressWarnings("static-access")
    private void confirmOrder(boolean testParam)
    {
        // Disable startup of CallCenters during database write IO

        callCenterEnabledCheckBox.setSelected(false);
        confirmOrderButton.setEnabled(false);

        // Building data package blocks etc.
        campaignCalendar.setVisible(false);
        updateOrder();

        int[] timewindowIndexArray = new int[3]; Order order = new Order();
        timewindowIndexArray = order.getIndices2Static(timewindowList.getSelectedIndices());

        // Create the Order
        tmpOrder = new Order(
                                0,
                                Calendar.getInstance().getTimeInMillis(),
                                Integer.parseInt(orderCustomerIdComboBox.getSelectedItem().toString()),
//                                recipientsComboBox.getSelectedItem().toString(),
                                recipientsList.getSelectedValue().toString(),
//                                timewindowComboBox.getSelectedItem().toString(),

                                timewindowIndexArray[0],
                                timewindowIndexArray[1],
                                timewindowIndexArray[2],

                                Integer.parseInt(orderDestinationsQuantityField.getText()),
                                orderFilenameField.getText(),
                                Integer.parseInt(orderMessageDurationField.getText()),
                                Float.parseFloat(orderMessageRatePerSecondField.getText()),
                                Float.parseFloat(orderMessageRateField.getText()),
                                Float.parseFloat(orderSubTotalField.getText())
                            );
        dbClient.insertCustomerOrder(tmpOrder);
        orderRecords.setText(Integer.toString(dbClient.getCustomerOrderCount()));

        tmpOrder = dbClient.selectLastCustomerOrder();

        // If the payment came through
        invoice = new Invoice(
                                (int)0, // invoiceId
                                Calendar.getInstance().getTimeInMillis(), // Timestamp
                                tmpOrder.getOrderId(), // invoiceOrderId
                                tmpOrder.getTargetTransactionQuantity(), // invoiceQuantityField
                                Vergunning.BRAND + " " + tmpOrder.getRecipientsCategory() + " " + tmpOrder.getMessageDuration() + "Sec Message Campaign", // ItemDescription
                                tmpOrder.getMessageRate(), // invoiceItemUnitPriceField
                                (int)19, // invoiceItemVATPercentageField
                                (tmpOrder.getTargetTransactionQuantity()*tmpOrder.getMessageRate()), // invoiceItemQuantityPriceField
                                (tmpOrder.getTargetTransactionQuantity()*tmpOrder.getMessageRate()), // nvoiceSubTotalB4DiscountField (double value? with the one directly above)
                                (int)0, // invoiceCustomerDiscountField will do thia later
                                (tmpOrder.getTargetTransactionQuantity()*tmpOrder.getMessageRate()), // invoiceSubTotalField
                                (tmpOrder.getTargetTransactionQuantity()*tmpOrder.getMessageRate())*19/100, //Float.parseFloat(invoiceVATField.getText()),
                                (tmpOrder.getTargetTransactionQuantity()*tmpOrder.getMessageRate()) + (tmpOrder.getTargetTransactionQuantity()*tmpOrder.getMessageRate())*19/100, // TotalField
                                (float)0 // invoicePaidField
                            );
        dbClient.insertInvoice(invoice);
        invoiceRecords.setText(Integer.toString(dbClient.getInvoiceCount()));

        // Create the Campaign
        tmpCampaign = new Campaign(
                                    (int)0, // Id
                                    Calendar.getInstance(), // Campaign Creation Timestamp
                                    (int)tmpOrder.getOrderId(),
                                    Calendar.getInstance(), // Scheduled Start
                                    Calendar.getInstance(), // Expected Start
                                    Calendar.getInstance(), // Registered Start
                                    Calendar.getInstance(), // Scheduled End
                                    Calendar.getInstance(), // Expected End
                                    Calendar.getInstance(), // Registered End
                                    testParam
                               );

        // Set the Start Calendars
        tmpCampaign.setCalendarScheduledStartEpoch(Long.parseLong(orderDateField.getText()));
        tmpCampaign.setCalendarExpectedStartEpoch(Long.parseLong(orderDateField.getText()));



//        tmpCampaign.setCalendarScheduledEnd(timeWindow.getEstimatedEndCalendar(tmpCampaign.getCalendarExpectedStart(), tmpOrder, throughputFactor, 0));
        tmpCampaign.setCalendarScheduledEnd(timeTool.getEstimatedEndCalendar(tmpCampaign.getCalendarExpectedStart(), tmpOrder, throughputFactor, 0, timewindowList.getSelectedIndices()));
        tmpCampaign.setCalendarExpectedEnd(tmpCampaign.getCalendarScheduledEnd());

//        // Calculate and set the End Calendars
//        long expectedCampaignDurationSeconds = 0;
//        if (order.getTimeWindowCategory().equalsIgnoreCase("daytime"))
//        {
//            expectedCampaignDurationSeconds = Math.round(((order.getMessageDuration() * order.getTargetTransactionQuantity()) / predictFactor) * daytimeFactor);
//        }
//        else
//        {
//            expectedCampaignDurationSeconds = Math.round(((order.getMessageDuration() * order.getTargetTransactionQuantity()) / predictFactor) * eveningFactor);
//        }
//        tmpCampaign.setCalendarScheduledEndEpoch(((tmpCampaign.getCalendarScheduledStart().getTimeInMillis() / 1000) + expectedCampaignDurationSeconds) * 1000);
//        tmpCampaign.setCalendarExpectedEndEpoch(((tmpCampaign.getCalendarExpectedStart().getTimeInMillis() / 1000) + expectedCampaignDurationSeconds) * 1000);


        // Reset the Future Register Calendars
        tmpCampaign.resetCalendarRegisteredStart();
        tmpCampaign.resetCalendarRegisteredEnd();
        dbClient.insertCampaign(tmpCampaign);
        campaignRecords.setText(Integer.toString(dbClient.getCampaignCount()));

        tmpCampaign = dbClient.selectLastCampaign();

        // Insert related CampaignStat record as we at least need one CampaignStat record to update during the running campaign
        tmpCampaignStat.setCampaignId(tmpCampaign.getId());
        dbClient.insertCampaignStat(tmpCampaignStat);
        campaignStatsRecords.setText(Integer.toString(dbClient.getCampaignStatsCount()));

        // Generate the Destinations

        destination = new Destination();
        final int campaignId = tmpCampaign.getId();

        destinationRecords.setText("wait");
        Thread insertDestinationsThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                String[] status = new String[2];
                long endTimestamp = 0;
                long  durationInMillis;
                long startTimestamp = Calendar.getInstance().getTimeInMillis();
                // Reading through TextArea lines
                status          = new String[2];
                String text     = destinationTextArea.getText();
                int totalQuantity = (destinationTextArea.getLineCount() -1 ); // Get the number of lines in the textArea
                showStatus("Inserting  " + totalQuantity + " ☎ nrs...", true, true);

                // Building the destinationArray
                int start;
                int end;
                serviceLoopProgressBar.setMaximum(tmpOrder.getTargetTransactionQuantity());
                serviceLoopProgressBar.setEnabled(true);
                int progressBarIntervalCounter = 0;
                for (int lineCounter=0; lineCounter < totalQuantity; lineCounter++)
                {
                    try
                    {
                        if (progressBarIntervalCounter == Math.round(tmpOrder.getTargetTransactionQuantity() / 100)) {
                            serviceLoopProgressBar.setValue(lineCounter);
                            progressBarIntervalCounter = 0;
                        }
                        progressBarIntervalCounter++;
                        start = destinationTextArea.getLineStartOffset(lineCounter); // Get the first position of the current line
                        end = destinationTextArea.getLineEndOffset(lineCounter); // Get the last position of the current line
                        String line = text.substring(start, end); // Get the entire line
                        destination.setCampaignId(campaignId);
                        destination.setDestination(VoipStormTools.validateSIPAddress(line));
                        destination.setDestinationCount(lineCounter);
                        dbClient.insertDestination(destination);
                    } catch (BadLocationException ex) { }
                }
                endTimestamp = Calendar.getInstance().getTimeInMillis();
                durationInMillis = endTimestamp - startTimestamp;
                int numberOfTransactionsPerSecond = Math.round((float)((float)tmpOrder.getTargetTransactionQuantity() / (durationInMillis))*1000);
                serviceLoopProgressBar.setValue(0); serviceLoopProgressBar.setEnabled(false);
                callCenterEnabledCheckBox.setSelected(true); // Make sure this stays in the inner thread loop
                confirmOrderButton.setEnabled(true);
                destinationTextArea.setText("");
                showStatus("Inserting  " + totalQuantity + " ☎ nrs Completed (" + numberOfTransactionsPerSecond + " tps) ", true, true);
                try { Thread.sleep(10); } catch (InterruptedException ex) { }
                tabPane.setSelectedIndex(0);
                destinationRecords.setText(Integer.toString(dbClient.getDestinationCount()));
            }
        });
        insertDestinationsThread.setName("insertDestinationsThread");
        insertDestinationsThread.setDaemon(runThreadsAsDaemons);
        insertDestinationsThread.start();

        refreshCustomersAndOrders();
    }

    private void campaignManagerPanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_campaignManagerPanelMouseClicked
        //updateOrder();
}//GEN-LAST:event_campaignManagerPanelMouseClicked

    private void selectInvoiceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectInvoiceButtonActionPerformed
        selectInvoice();
}//GEN-LAST:event_selectInvoiceButtonActionPerformed

    private void selectInvoice()
    {
        if ( invoiceIdField.getText().equals("") ) { invoiceIdField.setText("0"); }
        Invoice tmpInvoice = dbClient.selectInvoice(Integer.parseInt(invoiceIdField.getText()));
//        invoiceIdField.setText(Integer.toString(tmpInvoice.getId()));
        invoiceDateField.setText(Long.toString(tmpInvoice.getTimestamp()));
        invoiceOrderIdField.setText(Integer.toString(tmpInvoice.getOrderId()));
        invoiceQuantityField.setText(Integer.toString(tmpInvoice.getQuantityItem()));
        invoiceItemDescField.setText(tmpInvoice.getItemDesc());
        invoiceItemUnitPriceField.setText(Float.toString(tmpInvoice.getItemUnitPrice()));
        invoiceItemVATPercentageField.setText(Float.toString(tmpInvoice.getItemVATPercentage()));
        invoiceItemQuantityPriceField.setText(Float.toString(tmpInvoice.getItemQuantityPrice()));
        invoiceSubTotalB4DiscountField.setText(Float.toString(tmpInvoice.getSubTotalB4Discount()));
        invoiceCustomerDiscountField.setText(Integer.toString(tmpInvoice.getCustomerDiscount()));
        invoiceSubTotalField.setText(Float.toString(tmpInvoice.getSubTotal()));
        invoiceVATField.setText(Float.toString(tmpInvoice.getVAT()));
        invoiceTotalField.setText(Float.toString(tmpInvoice.getTotal()));
        invoicePaidField.setText(Float.toString(tmpInvoice.getPaid()));

        if ( invoiceIdField.getText().equals("") ) { invoiceIdField.setText("0"); }
        tmpInvoice = dbClient.selectInvoice(Integer.parseInt(invoiceIdField.getText()));
//        invoiceIdField.setText(Integer.toString(tmpInvoice.getId()));
        invoiceDateField.setText(Long.toString(tmpInvoice.getTimestamp()));
        invoiceOrderIdField.setText(Integer.toString(tmpInvoice.getOrderId()));
        invoiceQuantityField.setText(Integer.toString(tmpInvoice.getQuantityItem()));
        invoiceItemDescField.setText(tmpInvoice.getItemDesc());
        invoiceItemUnitPriceField.setText(Float.toString(tmpInvoice.getItemUnitPrice()));
        invoiceItemVATPercentageField.setText(Float.toString(tmpInvoice.getItemVATPercentage()));
        invoiceItemQuantityPriceField.setText(Float.toString(tmpInvoice.getItemQuantityPrice()));
        invoiceSubTotalB4DiscountField.setText(Float.toString(tmpInvoice.getSubTotalB4Discount()));
        invoiceCustomerDiscountField.setText(Integer.toString(tmpInvoice.getCustomerDiscount()));
        invoiceSubTotalField.setText(Float.toString(tmpInvoice.getSubTotal()));
        invoiceVATField.setText(Float.toString(tmpInvoice.getVAT()));
        invoiceTotalField.setText(Float.toString(tmpInvoice.getTotal()));
        invoicePaidField.setText(Float.toString(tmpInvoice.getPaid()));
    }

    private void insertInvoiceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_insertInvoiceButtonActionPerformed
        Invoice tmpInvoice = new Invoice(
                                            0,
                                            Calendar.getInstance().getTimeInMillis(),
                                            Integer.parseInt(invoiceOrderIdField.getText()),
                                            Integer.parseInt(invoiceQuantityField.getText()),
                                            invoiceItemDescField.getText(),
                                            Float.parseFloat(invoiceItemUnitPriceField.getText()),
                                            Integer.parseInt(invoiceItemVATPercentageField.getText()),
                                            Float.parseFloat(invoiceItemQuantityPriceField.getText()),
                                            Float.parseFloat(invoiceSubTotalB4DiscountField.getText()),
                                            Integer.parseInt(invoiceCustomerDiscountField.getText()),
                                            Float.parseFloat(invoiceSubTotalField.getText()),
                                            Float.parseFloat(invoiceVATField.getText()),
                                            Float.parseFloat(invoiceTotalField.getText()),
                                            Float.parseFloat(invoicePaidField.getText())
                                        );
        dbClient.insertInvoice(tmpInvoice);
        invoiceRecords.setText(Integer.toString(dbClient.getInvoiceCount()));
}//GEN-LAST:event_insertInvoiceButtonActionPerformed

    private void updateInvoiceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateInvoiceButtonActionPerformed
        Invoice tmpInvoice = new Invoice(
                                            Integer.parseInt(invoiceIdField.getText()),
                                            Calendar.getInstance().getTimeInMillis(),
                                            Integer.parseInt(invoiceOrderIdField.getText()),
                                            Integer.parseInt(invoiceQuantityField.getText()),
                                            invoiceItemDescField.getText(),
                                            Float.parseFloat(invoiceItemUnitPriceField.getText()),
                                            Integer.parseInt(invoiceItemVATPercentageField.getText()),
                                            Float.parseFloat(invoiceItemQuantityPriceField.getText()),
                                            Float.parseFloat(invoiceSubTotalB4DiscountField.getText()),
                                            Integer.parseInt(invoiceCustomerDiscountField.getText()),
                                            Float.parseFloat(invoiceSubTotalField.getText()),
                                            Float.parseFloat(invoiceVATField.getText()),
                                            Float.parseFloat(invoiceTotalField.getText()),
                                            Float.parseFloat(invoicePaidField.getText())
                                        );
        dbClient.updateInvoice(tmpInvoice);
}//GEN-LAST:event_updateInvoiceButtonActionPerformed

    private void deleteInvoiceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteInvoiceButtonActionPerformed
        dbClient.deleteInvoice(Integer.parseInt(invoiceIdField.getText()));
        invoiceRecords.setText(Integer.toString(dbClient.getInvoiceCount()));
}//GEN-LAST:event_deleteInvoiceButtonActionPerformed

    private void dropInvoiceTableButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dropInvoiceTableButtonActionPerformed
        dbClient.dropInvoiceTable(); dbClient.createInvoiceTable();
        invoiceRecords.setText(Integer.toString(dbClient.getInvoiceCount()));
}//GEN-LAST:event_dropInvoiceTableButtonActionPerformed

    private void invoicePanelKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_invoicePanelKeyPressed
        if (evt.getKeyCode() == 9) { invoicePanel.getNextFocusableComponent().requestFocusInWindow(); }
}//GEN-LAST:event_invoicePanelKeyPressed

    private void selectCampaignButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectCampaignButtonActionPerformed
        selectCampaign();
}//GEN-LAST:event_selectCampaignButtonActionPerformed

    private void selectCampaign()
    {
        if ( idField.getText().equals("") ) { idField.setText("0"); }
        Campaign tmpCampaign = dbClient.loadCampaignFromOrderId(Integer.parseInt(idField.getText()));
//        idField.setText(Integer.toString(tmpCampaign.getId()));
        campaignTimestampField.setText(Long.toString(tmpCampaign.getCalendarCampaignCreated().getTimeInMillis()));
        campaignOrderIdField.setText(Integer.toString(tmpCampaign.getOrderId()));
        timeScheduledStartField.setText(Long.toString(tmpCampaign.getCalendarScheduledStart().getTimeInMillis()));
        timeScheduledEndField.setText(Long.toString(tmpCampaign.getCalendarScheduledEnd().getTimeInMillis()));
        timeExpectedStartField.setText(Long.toString(tmpCampaign.getCalendarExpectedStart().getTimeInMillis()));
        timeExpectedEndField.setText(Long.toString(tmpCampaign.getCalendarExpectedEnd().getTimeInMillis()));
        timeRegisteredStartField.setText(Long.toString(tmpCampaign.getCalendarRegisteredStart().getTimeInMillis()));
        timeRegisteredEndField.setText(Long.toString(tmpCampaign.getCalendarRegisteredEnd().getTimeInMillis()));
        campaignTestCheckBox.setSelected(tmpCampaign.getTestCampaign());

        if ( idField.getText().equals("") ) { idField.setText("0"); }
        tmpCampaign = dbClient.loadCampaignFromOrderId(Integer.parseInt(idField.getText()));
//        idField.setText(Integer.toString(tmpCampaign.getId()));
        campaignTimestampField.setText(Long.toString(tmpCampaign.getCalendarCampaignCreated().getTimeInMillis()));
        campaignOrderIdField.setText(Integer.toString(tmpCampaign.getOrderId()));
        timeScheduledStartField.setText(Long.toString(tmpCampaign.getCalendarScheduledStart().getTimeInMillis()));
        timeScheduledEndField.setText(Long.toString(tmpCampaign.getCalendarScheduledEnd().getTimeInMillis()));
        timeExpectedStartField.setText(Long.toString(tmpCampaign.getCalendarExpectedStart().getTimeInMillis()));
        timeExpectedEndField.setText(Long.toString(tmpCampaign.getCalendarExpectedEnd().getTimeInMillis()));
        timeRegisteredStartField.setText(Long.toString(tmpCampaign.getCalendarRegisteredStart().getTimeInMillis()));
        timeRegisteredEndField.setText(Long.toString(tmpCampaign.getCalendarRegisteredEnd().getTimeInMillis()));
        campaignTestCheckBox.setSelected(tmpCampaign.getTestCampaign());
    }

    private void insertCampaignButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_insertCampaignButtonActionPerformed
        Campaign tmpCampaign = new Campaign(
                                                0,
                                                Calendar.getInstance(),
                                                Integer.parseInt(campaignOrderIdField.getText()),
                                                Calendar.getInstance(),
                                                Calendar.getInstance(),
                                                Calendar.getInstance(),
                                                Calendar.getInstance(),
                                                Calendar.getInstance(),
                                                Calendar.getInstance(),
                                                campaignTestCheckBox.isSelected()
                                           );
        tmpCampaign.setCalendarScheduledStartEpoch(Long.parseLong(timeScheduledStartField.getText()));
        tmpCampaign.setCalendarScheduledEndEpoch(Long.parseLong(timeScheduledEndField.getText()));
        tmpCampaign.setCalendarExpectedStartEpoch(Long.parseLong(timeExpectedStartField.getText()));
        tmpCampaign.setCalendarExpectedEndEpoch(Long.parseLong(timeExpectedEndField.getText()));
        tmpCampaign.setCalendarRegisteredStartEpoch(Long.parseLong(timeRegisteredStartField.getText()));
        tmpCampaign.setCalendarRegisteredEndEpoch(Long.parseLong(timeRegisteredEndField.getText()));
        tmpCampaign.setTestCampaign(campaignTestCheckBox.isSelected());
        dbClient.insertCampaign(tmpCampaign);
        campaignRecords.setText(Integer.toString(dbClient.getCampaignCount()));
}//GEN-LAST:event_insertCampaignButtonActionPerformed

    private void updateCampaignButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateCampaignButtonActionPerformed
        Campaign tmpCampaign = new Campaign(
                                                Integer.parseInt(idField.getText()),
                                                Calendar.getInstance(),
                                                Integer.parseInt(campaignOrderIdField.getText()),
                                                Calendar.getInstance(),
                                                Calendar.getInstance(),
                                                Calendar.getInstance(),
                                                Calendar.getInstance(),
                                                Calendar.getInstance(),
                                                Calendar.getInstance(),
                                                campaignTestCheckBox.isSelected()
                                           );
        tmpCampaign.setCalendarScheduledStartEpoch(Long.parseLong(timeScheduledStartField.getText()));
        tmpCampaign.setCalendarScheduledEndEpoch(Long.parseLong(timeScheduledEndField.getText()));
        tmpCampaign.setCalendarExpectedStartEpoch(Long.parseLong(timeExpectedStartField.getText()));
        tmpCampaign.setCalendarExpectedEndEpoch(Long.parseLong(timeExpectedEndField.getText()));
        tmpCampaign.setCalendarRegisteredStartEpoch(Long.parseLong(timeRegisteredStartField.getText()));
        tmpCampaign.setCalendarRegisteredEndEpoch(Long.parseLong(timeRegisteredEndField.getText()));
        tmpCampaign.setTestCampaign(campaignTestCheckBox.isSelected());
        dbClient.updateCampaign(tmpCampaign);
}//GEN-LAST:event_updateCampaignButtonActionPerformed

    private void deleteCampaignButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteCampaignButtonActionPerformed
        dbClient.deleteCampaign(Integer.parseInt(idField.getText()));
        campaignRecords.setText(Integer.toString(dbClient.getCampaignCount()));
}//GEN-LAST:event_deleteCampaignButtonActionPerformed

    private void dropCampaignTableButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dropCampaignTableButtonActionPerformed
        dbClient.dropCampaignTable(); dbClient.createCampaignTable();
        campaignRecords.setText(Integer.toString(dbClient.getCampaignCount()));
}//GEN-LAST:event_dropCampaignTableButtonActionPerformed

    private void campaignPanelKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_campaignPanelKeyPressed
        if (evt.getKeyCode() == 9) { campaignPanel.getNextFocusableComponent().requestFocusInWindow(); }
}//GEN-LAST:event_campaignPanelKeyPressed

    private void selectDestinationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectDestinationButtonActionPerformed
        selectDestination();
}//GEN-LAST:event_selectDestinationButtonActionPerformed

    private void insertDestinationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_insertDestinationButtonActionPerformed
        Destination tmpDestination = new Destination(
                                                        0,
                                                        Integer.parseInt(campaignIdField.getText()),
                                                        Integer.parseInt(destCountField.getText()),
                                                        destinationField.getText(),
                                                        Long.parseLong(connectingTimestampField.getText()),
                                                        Long.parseLong(tryingTimestampField.getText()),
                                                        Long.parseLong(callingTimestampField.getText()),
                                                        Integer.parseInt(callingAttemptsField.getText()),
                                                        Long.parseLong(ringingTimestampField.getText()),
                                                        Long.parseLong(localCancelingTimestampField.getText()),
                                                        Long.parseLong(remoteCancelingTimestampField.getText()),
                                                        Long.parseLong(localBusyTimestampField.getText()),
                                                        Long.parseLong(remoteBusyTimestampField.getText()),
                                                        Long.parseLong(acceptingTimestampField.getText()),
                                                        Long.parseLong(talkingTimestampField.getText()),
                                                        Long.parseLong(localByeTimestampField.getText()),
                                                        Long.parseLong(remoteByeTimestampField.getText()),
                                                        Long.parseLong(responseStatusCodeField.getText()),
                                                        responseStatusDescField.getText()
                                                    );
        dbClient.insertDestination(tmpDestination);
        destinationRecords.setText(Integer.toString(dbClient.getDestinationCount()));
}//GEN-LAST:event_insertDestinationButtonActionPerformed

    private void updateDestinationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateDestinationButtonActionPerformed
        Destination tmpDestination = new Destination(
                                                        Long.parseLong(destIdField.getText()),
                                                        Integer.parseInt(campaignIdField.getText()),
                                                        Integer.parseInt(destCountField.getText()),
                                                        destinationField.getText(),
                                                        Long.parseLong(connectingTimestampField.getText()),
                                                        Long.parseLong(tryingTimestampField.getText()),
                                                        Long.parseLong(callingTimestampField.getText()),
                                                        Integer.parseInt(callingAttemptsField.getText()),
                                                        Long.parseLong(ringingTimestampField.getText()),
                                                        Long.parseLong(localCancelingTimestampField.getText()),
                                                        Long.parseLong(remoteCancelingTimestampField.getText()),
                                                        Long.parseLong(localBusyTimestampField.getText()),
                                                        Long.parseLong(remoteBusyTimestampField.getText()),
                                                        Long.parseLong(acceptingTimestampField.getText()),
                                                        Long.parseLong(talkingTimestampField.getText()),
                                                        Long.parseLong(localByeTimestampField.getText()),
                                                        Long.parseLong(remoteByeTimestampField.getText()),
                                                        Long.parseLong(responseStatusCodeField.getText()),
                                                        responseStatusDescField.getText()
                                                    );
        dbClient.updateDestination(tmpDestination);
}//GEN-LAST:event_updateDestinationButtonActionPerformed

    private void deleteDestinationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteDestinationButtonActionPerformed
        dbClient.deleteDestination(Long.parseLong(destIdField.getText()));
        updateDatabaseStats();
}//GEN-LAST:event_deleteDestinationButtonActionPerformed

    private void dropDestinationTableButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dropDestinationTableButtonActionPerformed
        dbClient.dropDestinationTable(); dbClient.createDestinationTable();
        destinationRecords.setText(Integer.toString(dbClient.getDestinationCount()));
}//GEN-LAST:event_dropDestinationTableButtonActionPerformed

    private void destinationsPanelKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_destinationsPanelKeyPressed
        if (evt.getKeyCode() == 9) { destinationsPanel.getNextFocusableComponent().requestFocusInWindow(); }
}//GEN-LAST:event_destinationsPanelKeyPressed

    private void selectCampaignStatsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectCampaignStatsButtonActionPerformed
        if ( campaignStatIdField.getText().equals("") ) { campaignStatIdField.setText("0"); } // If Id field is empty it puts in a 0 (first campaignStat)
        CampaignStat tmpCampaignStat = dbClient.selectCampaignStat(Integer.parseInt(campaignStatIdField.getText()));
        campaignStatIdField.setText(Integer.toString(tmpCampaignStat.getCampaignId()));
        onACField.setText(Integer.toString(tmpCampaignStat.getOnAC()));
        idleACField.setText(Integer.toString(tmpCampaignStat.getIdleAC()));
        connectACField.setText(Integer.toString(tmpCampaignStat.getConnectingAC()));
        connectTTField.setText(Integer.toString(tmpCampaignStat.getConnectingTT()));
        tryingACField.setText(Integer.toString(tmpCampaignStat.getTryingAC()));
        tryingTTField.setText(Integer.toString(tmpCampaignStat.getTryingTT()));
        callingACField.setText(Integer.toString(tmpCampaignStat.getCallingAC()));
        callingTTField.setText(Integer.toString(tmpCampaignStat.getCallingTT()));
        ringingACField.setText(Integer.toString(tmpCampaignStat.getRingingAC()));
        ringingTTField.setText(Integer.toString(tmpCampaignStat.getRingingTT()));
        acceptingACField.setText(Integer.toString(tmpCampaignStat.getAcceptingAC()));
        acceptingTTField.setText(Integer.toString(tmpCampaignStat.getAcceptingTT()));
        talkingACField.setText(Integer.toString(tmpCampaignStat.getTalkingAC()));
        talkingTTField.setText(Integer.toString(tmpCampaignStat.getTalkingTT()));
        localCancelTTField.setText(Integer.toString(tmpCampaignStat.getLocalCancelTT()));
        remoteCancelTTField.setText(Integer.toString(tmpCampaignStat.getRemoteCancelTT()));
        localBusyTTField.setText(Integer.toString(tmpCampaignStat.getLocalBusyTT()));
        remoteBusyTTField.setText(Integer.toString(tmpCampaignStat.getRemoteBusyTT()));
        localByeTTField.setText(Integer.toString(tmpCampaignStat.getLocalByeTT()));
        remoteByeTTField.setText(Integer.toString(tmpCampaignStat.getRemoteByeTT()));
}//GEN-LAST:event_selectCampaignStatsButtonActionPerformed

    private void insertCampaignStatsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_insertCampaignStatsButtonActionPerformed
        CampaignStat tmpCampaignStat = new CampaignStat(
                                                            0,
                                                            Integer.parseInt(onACField.getText()),
                                                            Integer.parseInt(idleACField.getText()),
                                                            Integer.parseInt(connectACField.getText()),
                                                            Integer.parseInt(connectTTField.getText()),
                                                            Integer.parseInt(tryingACField.getText()),
                                                            Integer.parseInt(tryingTTField.getText()),
                                                            Integer.parseInt(callingACField.getText()),
                                                            Integer.parseInt(callingTTField.getText()),
                                                            Integer.parseInt(ringingACField.getText()),
                                                            Integer.parseInt(ringingTTField.getText()),
                                                            Integer.parseInt(acceptingACField.getText()),
                                                            Integer.parseInt(acceptingTTField.getText()),
                                                            Integer.parseInt(talkingACField.getText()),
                                                            Integer.parseInt(talkingTTField.getText()),
                                                            Integer.parseInt(localCancelTTField.getText()),
                                                            Integer.parseInt(remoteCancelTTField.getText()),
                                                            Integer.parseInt(localBusyTTField.getText()),
                                                            Integer.parseInt(remoteBusyTTField.getText()),
                                                            Integer.parseInt(localByeTTField.getText()),
                                                            Integer.parseInt(remoteByeTTField.getText())
                                                       );
        dbClient.insertCampaignStat(tmpCampaignStat);
        campaignStatsRecords.setText(Integer.toString(dbClient.getCampaignStatsCount()));
}//GEN-LAST:event_insertCampaignStatsButtonActionPerformed

    private void updateCampaignStatsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateCampaignStatsButtonActionPerformed
        CampaignStat tmpCampaignStat = new CampaignStat(
                                                            Integer.parseInt(campaignStatIdField.getText()),
                                                            Integer.parseInt(onACField.getText()),
                                                            Integer.parseInt(idleACField.getText()),
                                                            Integer.parseInt(connectACField.getText()),
                                                            Integer.parseInt(connectTTField.getText()),
                                                            Integer.parseInt(tryingACField.getText()),
                                                            Integer.parseInt(tryingTTField.getText()),
                                                            Integer.parseInt(callingACField.getText()),
                                                            Integer.parseInt(callingTTField.getText()),
                                                            Integer.parseInt(ringingACField.getText()),
                                                            Integer.parseInt(ringingTTField.getText()),
                                                            Integer.parseInt(acceptingACField.getText()),
                                                            Integer.parseInt(acceptingTTField.getText()),
                                                            Integer.parseInt(talkingACField.getText()),
                                                            Integer.parseInt(talkingTTField.getText()),
                                                            Integer.parseInt(localCancelTTField.getText()),
                                                            Integer.parseInt(remoteCancelTTField.getText()),
                                                            Integer.parseInt(localBusyTTField.getText()),
                                                            Integer.parseInt(remoteBusyTTField.getText()),
                                                            Integer.parseInt(localByeTTField.getText()),
                                                            Integer.parseInt(remoteByeTTField.getText())
                                                       );
        dbClient.updateCampaignStat(tmpCampaignStat);
}//GEN-LAST:event_updateCampaignStatsButtonActionPerformed

    private void deleteCampaignStatsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteCampaignStatsButtonActionPerformed
        dbClient.deleteCampaignStat(Integer.parseInt(campaignStatIdField.getText()));
        campaignStatsRecords.setText(Integer.toString(dbClient.getCampaignStatsCount()));
}//GEN-LAST:event_deleteCampaignStatsButtonActionPerformed

    private void dropCampaignStatsTableButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dropCampaignStatsTableButtonActionPerformed
        dbClient.dropCampaignStatTable(); dbClient.createCampaignStatTable();
        campaignStatsRecords.setText(Integer.toString(dbClient.getCampaignStatsCount()));
}//GEN-LAST:event_dropCampaignStatsTableButtonActionPerformed

    private void campaignStatsPanelKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_campaignStatsPanelKeyPressed
        if (evt.getKeyCode() == 9) { campaignStatsPanel.getNextFocusableComponent().requestFocusInWindow(); }
}//GEN-LAST:event_campaignStatsPanelKeyPressed

    private void dropPricelistTableButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dropPricelistTableButtonActionPerformed
        dbClient.dropPricelistTable(); dbClient.createPricelistTable();
        pricelistRecords.setText(Integer.toString(dbClient.getPricelistCount()));
}//GEN-LAST:event_dropPricelistTableButtonActionPerformed

    private void selectPricelistButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectPricelistButtonActionPerformed
        Pricelist tmpPricelist = dbClient.selectPricelist();
        pricelistB2BDaytimePerSecondField.setText(Float.toString(tmpPricelist.getB2BDaytimeRatePerSecond()));
        pricelistB2BEveningPerSecondField.setText(Float.toString(tmpPricelist.getB2BEveningRatePerSecond()));
        pricelistB2CDaytimePerSecondField.setText(Float.toString(tmpPricelist.getB2CDaytimeRatePerSecond()));
        pricelistB2CEveningPerSecondField.setText(Float.toString(tmpPricelist.getB2CEveningRatePerSecond()));
        pricelistA2SDaytimePerSecondField.setText(Float.toString(tmpPricelist.getA2SDaytimeRatePerSecond()));
        pricelistA2SEveningPerSecondField.setText(Float.toString(tmpPricelist.getA2SEveningRatePerSecond()));
}//GEN-LAST:event_selectPricelistButtonActionPerformed

    private void insertPricelistButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_insertPricelistButtonActionPerformed
        Pricelist tmpPricelist = new Pricelist(
                Float.parseFloat(pricelistB2BDaytimePerSecondField.getText()),
                Float.parseFloat(pricelistB2BEveningPerSecondField.getText()),
                Float.parseFloat(pricelistB2CDaytimePerSecondField.getText()),
                Float.parseFloat(pricelistB2CEveningPerSecondField.getText()),
                Float.parseFloat(pricelistA2SDaytimePerSecondField.getText()),
                Float.parseFloat(pricelistA2SEveningPerSecondField.getText())
                );
        dbClient.insertPricelist(tmpPricelist);
        pricelistRecords.setText(Integer.toString(dbClient.getPricelistCount()));
}//GEN-LAST:event_insertPricelistButtonActionPerformed

    private void updatePricelistButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updatePricelistButtonActionPerformed
        Pricelist tmpPricelist = new Pricelist(
                Float.parseFloat(pricelistB2BDaytimePerSecondField.getText()),
                Float.parseFloat(pricelistB2BEveningPerSecondField.getText()),
                Float.parseFloat(pricelistB2CDaytimePerSecondField.getText()),
                Float.parseFloat(pricelistB2CEveningPerSecondField.getText()),
                Float.parseFloat(pricelistA2SDaytimePerSecondField.getText()),
                Float.parseFloat(pricelistA2SEveningPerSecondField.getText())
                );
        dbClient.updatePricelist(tmpPricelist);
}//GEN-LAST:event_updatePricelistButtonActionPerformed

    private void deletePricelistButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deletePricelistButtonActionPerformed
        dbClient.deletePricelist();
        pricelistRecords.setText(Integer.toString(dbClient.getPricelistCount()));
}//GEN-LAST:event_deletePricelistButtonActionPerformed

    private void pricelistPanelKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_pricelistPanelKeyPressed
        if (evt.getKeyCode() == 9) { pricelistPanel.getNextFocusableComponent().requestFocusInWindow(); }
}//GEN-LAST:event_pricelistPanelKeyPressed

    private void campaignTabKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_campaignTabKeyPressed
        if (evt.getKeyCode() == 9) { campaignTab.getNextFocusableComponent().requestFocusInWindow(); }
}//GEN-LAST:event_campaignTabKeyPressed

    private void saveConfigurationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveConfigurationButtonActionPerformed
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
        if (registerCheckBox.isSelected()) {configurationCallCenter.setRegister("1");} else {configurationCallCenter.setRegister("0");}
        if (iconsCheckBox.isSelected()) {configurationCallCenter.setIcons("1");} else {configurationCallCenter.setIcons("0");}
        configurationCallCenter.saveConfiguration("3");
        configurationCallCenter.loadConfiguration("3");

        if
        (
            (pubIPField.getText().length() == 0) ||
            (domainField.getText().length() == 0) ||
            (serverIPField.getText().length() == 0) ||
            (usernameField.getText().length() == 0) ||
            (toegangField.getPassword().length == 0)
        )
        {
            displayMessage("Please fill in White Empty Fields");
        }
        else
        {
            if ( Integer.parseInt(configurationCallCenter.getPrefPhoneLines()) > vergunning.getPhoneLines() ) { softphonesQuantity = vergunning.getPhoneLines(); } else { softphonesQuantity = Integer.parseInt(configurationCallCenter.getPrefPhoneLines()); }
            tabPane.setSelectedIndex(1);
            statusBarOutbound.setText("");
            statusBarInbound.setText("");
            tabPane.setEnabled(true);
            displayMessage("Congratulations, VoipStorm is now Ready");
        }
}//GEN-LAST:event_saveConfigurationButtonActionPerformed

    private void configTabKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_configTabKeyPressed
        if (evt.getKeyCode() == 9) { configTab.getNextFocusableComponent().requestFocusInWindow(); }
}//GEN-LAST:event_configTabKeyPressed

    private void inboundCallCenterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inboundCallCenterButtonActionPerformed
        if (inboundCallCenterShouldBeRunning)
        {
            if      (inboundCallCenterState == DISCONNECTED)
            {
                if (inboundCallCenterStatus == STARTING) // Starting Timer
                {
                    startInboundCallCenter(false, platformIsNetManaged);
                    inboundCallCenterStatus = STARTING;
                    inboundCallCenterStartingTimer = 0;
                    displayMessage("Starting ECallCenter21 on Right");
                }
                if (inboundCallCenterStatus == FAILING) // Failing Timer
                {
                    startInboundCallCenter(false, platformIsNetManaged);
                    inboundCallCenterStatus = STARTING;
                    inboundCallCenterStartingTimer = 0;
                    displayMessage("Starting ECallCenter21 on Left");
                }
            }
            else // inboundCallCenterState == CONNECTED
            {
                if (inboundCallCenterStatus == RUNNING) // Steady Operation
                {
                    inboundCallCenterButton.setForeground(Color.GREEN);
                }
            }
        }
        else // InboundCallCenterShouldNotBeRunning
        {
            if  (inboundCallCenterState == CONNECTED)
            {
                if (inboundCallCenterStatus == RUNNING)
                {
                    inboundCallCenterButton.setForeground(Color.BLACK);
                    inboundCallCenterButton.setToolTipText("Stop Inbound CallCenter");
                    closeInboundCallCenter();
                    inboundCallCenterStatus = NOTRUNNING;
                    showStatusInbound("Inbound CallCenter Stopped", true, true);
                }
            }
            else // DISCONNECTED
            {
                inboundCallCenterButton.setForeground(Color.BLACK);
                inboundCallCenterStatus = NOTRUNNING;
                startCallCenterRight(false, false);
                displayMessage("Starting ECallCenter21 on Right");
            }
        }
    }//GEN-LAST:event_inboundCallCenterButtonActionPerformed

    private void outboundCallCenterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_outboundCallCenterButtonActionPerformed
        if (outboundCallCenterShouldBeRunning)
        {
            if      (outboundCallCenterState == DISCONNECTED)
            {
                if (outboundCallCenterStatus == STARTING) // Starting Timer
                {
                    startOutboundCallCenter(campaign, false, platformIsNetManaged);
                    outboundCallCenterStatus = STARTING;
                    outboundCallCenterStartingTimer = 0;
                    displayMessage("Starting ECallCenter21 on Left");
                }
                if (outboundCallCenterStatus == FAILING) // Failing Timer
                {
                    startOutboundCallCenter(campaign, false, platformIsNetManaged);
                    outboundCallCenterStatus = STARTING;
                    outboundCallCenterStartingTimer = 0;
                    displayMessage("Starting ECallCenter21 on Left");
                }
            }
            else // outboundCallCenterState == CONNECTED
            {
                if (outboundCallCenterStatus == RUNNING) // Steady Operation
                {
                    outboundCallCenterButton.setForeground(Color.GREEN);
                }
            }
        }
        else // OutboundCallCenterShouldNotBeRunning
        {
            if  (outboundCallCenterState == CONNECTED)
            {
                if (outboundCallCenterStatus == RUNNING)
                {
                    outboundCallCenterButton.setForeground(Color.BLACK);
                    outboundCallCenterButton.setToolTipText("Stop Outbound CallCenter");
                    closeOutboundCallCenter();
                    outboundCallCenterStatus = NOTRUNNING;
                    showStatusOutbound("Outbound CallCenter Stopped", true, true);
                }
            }
            else // DISCONNECTED
            {
                outboundCallCenterButton.setForeground(Color.BLACK);
                outboundCallCenterStatus = NOTRUNNING;
                startCallCenterLeft(false, false);
                displayMessage("Starting ECallCenter21 on Left");
            }
        }
    }//GEN-LAST:event_outboundCallCenterButtonActionPerformed

    private void closeInboundCallCenter()
    {
        String[] status = new String[2];
        netManagerClient3 = new NetManagerClient(this,INBOUND_PORT);
        status[0] = "0"; status[1] = ""; status = netManagerClient3.connectAndSend("closeCallCenter");
        if (status[0].equals("0")) { showStatusInbound(status[1], true, true); } else { showStatusInbound(status[1], true, true); }
        netManagerClient3.closeConnection();
    }

    private void closeOutboundCallCenter()
    {
        String[] status = new String[2];
        netManagerClient3 = new NetManagerClient(this,OUTBOUND_PORT);
        status[0] = "0"; status[1] = ""; status = netManagerClient3.connectAndSend("closeCallCenter");
        if (status[0].equals("0")) { showStatusOutbound(status[1], true, true); } else { showStatusOutbound(status[1], true, true); }
        netManagerClient3.closeConnection();
    }

    private void confirmOrderButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_confirmOrderButtonActionPerformed
        confirmOrder(false); // With a testbutton, just do a confirmOrder(true);
    }//GEN-LAST:event_confirmOrderButtonActionPerformed

    private void aboutTabKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_aboutTabKeyPressed
        if (evt.getKeyCode() == 9) { aboutTab.getNextFocusableComponent().requestFocusInWindow(); }
}//GEN-LAST:event_aboutTabKeyPressed

    private void logTabKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_logTabKeyPressed
        if (evt.getKeyCode() == 9) { logTab.getNextFocusableComponent().requestFocusInWindow(); }
}//GEN-LAST:event_logTabKeyPressed

    private void selectResellerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectResellerButtonActionPerformed
        if ( resellerIdField.getText().equals("") ) { resellerIdField.setText("0"); } // If Id field is empty it puts in a 0 (first reseller)
        Reseller tmpReseller = dbClient.selectReseller(Integer.parseInt(resellerIdField.getText()));
        resellerIdField.setText(Integer.toString(tmpReseller.getId()));
        resellerDateField.setText(Long.toString(tmpReseller.getTimestamp()));
        resellerCompanyNameField.setText(tmpReseller.getCompanyName());
        resellerAddressField.setText(tmpReseller.getAddress());
        resellerAddressNrField.setText(tmpReseller.getAddressNr());
        resellerPostcodeField.setText(tmpReseller.getpostcode());
        resellerCityField.setText(tmpReseller.getCity());
        resellerCountryField.setText(tmpReseller.getCountry());
        resellerContactNameField.setText(tmpReseller.getContactName());
        resellerEmailField.setText(tmpReseller.getEmail());
        resellerPhoneNrField.setText(tmpReseller.getPhoneNr());
        resellerMobileNrField.setText(tmpReseller.getMobileNr());
        resellerDiscountField.setText(Integer.toString(tmpReseller.getResellerDiscount()));
        resellerButtonsControl();
    }//GEN-LAST:event_selectResellerButtonActionPerformed

    private void selectResellerButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_selectResellerButtonKeyPressed
        if (evt.getKeyCode() == 9) { selectResellerButton.getNextFocusableComponent().requestFocusInWindow(); }
    }//GEN-LAST:event_selectResellerButtonKeyPressed

    private void insertResellerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_insertResellerButtonActionPerformed
        if ( resellerDiscountField.getText().length() == 0 ) { resellerDiscountField.setText("0"); }
        Reseller tmpReseller = new Reseller(
                                                0,
                                                (new java.util.Date().getTime()),
                                                resellerCompanyNameField.getText(),
                                                resellerAddressField.getText(),
                                                resellerAddressNrField.getText(),
                                                resellerPostcodeField.getText(),
                                                resellerCityField.getText(),
                                                resellerCountryField.getText(),
                                                resellerContactNameField.getText(),
                                                resellerPhoneNrField.getText(),
                                                resellerMobileNrField.getText(),
                                                resellerEmailField.getText(),
                                                new String("pw"),
                                                Integer.parseInt(resellerDiscountField.getText()),
                                                new String("")
                                            );
        dbClient.insertReseller(tmpReseller);

        tmpReseller = dbClient.selectLastReseller();
        resellerIdField.setText(Integer.toString(tmpReseller.getId()));
        resellerDateField.setText(Long.toString(tmpReseller.getTimestamp()));
        resellerCompanyNameField.setText(tmpReseller.getCompanyName());
        resellerAddressField.setText(tmpReseller.getAddress());
        resellerAddressNrField.setText(tmpReseller.getAddressNr());
        resellerPostcodeField.setText(tmpReseller.getpostcode());
        resellerCityField.setText(tmpReseller.getCity());
        resellerCountryField.setText(tmpReseller.getCountry());
        resellerContactNameField.setText(tmpReseller.getContactName());
        resellerEmailField.setText(tmpReseller.getEmail());
        resellerPhoneNrField.setText(tmpReseller.getPhoneNr());
        resellerMobileNrField.setText(tmpReseller.getMobileNr());
        resellerDiscountField.setText(Integer.toString(tmpReseller.getResellerDiscount()));
        resellerButtonsControl();
        resellerRecords.setText(Integer.toString(dbClient.getResellerCount()));
    }//GEN-LAST:event_insertResellerButtonActionPerformed

    private void insertResellerButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_insertResellerButtonKeyPressed
        if (evt.getKeyCode() == 9) { insertResellerButton.getNextFocusableComponent().requestFocusInWindow(); }
    }//GEN-LAST:event_insertResellerButtonKeyPressed

    private void updateResellerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateResellerButtonActionPerformed
        //System.out.println(Integer.parseInt(idField.getText()));
        Reseller tmpReseller = new Reseller(
                Integer.parseInt( resellerIdField.getText()),
                (new java.util.Date().getTime()),
                resellerCompanyNameField.getText(),
                resellerAddressField.getText(),
                resellerAddressNrField.getText(),
                resellerPostcodeField.getText(),
                resellerCityField.getText(),
                resellerCountryField.getText(),
                resellerContactNameField.getText(),
                resellerPhoneNrField.getText(),
                resellerMobileNrField.getText(),
                resellerEmailField.getText(),
                new String("pw"),
                Integer.parseInt(resellerDiscountField.getText()),
                new String("")
                );
        dbClient.updateReseller(tmpReseller);
    }//GEN-LAST:event_updateResellerButtonActionPerformed

    private void updateResellerButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_updateResellerButtonKeyPressed
        if (evt.getKeyCode() == 9) { updateResellerButton.getNextFocusableComponent().requestFocusInWindow(); }
    }//GEN-LAST:event_updateResellerButtonKeyPressed

    private void deleteResellerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteResellerButtonActionPerformed
        dbClient.deleteReseller(Integer.parseInt(resellerIdField.getText()));
        resellerIdField.setText("");
        resellerDateField.setText("");
        resellerCompanyNameField.setText("");
        resellerAddressField.setText("");
        resellerAddressNrField.setText("");
        resellerPostcodeField.setText("");
        resellerCityField.setText("");
        resellerCountryField.setText("");
        resellerContactNameField.setText("");
        resellerEmailField.setText("");
        resellerPhoneNrField.setText("");
        resellerMobileNrField.setText("");
        resellerDiscountField.setText("");
        resellerButtonsControl();
        resellerRecords.setText(Integer.toString(dbClient.getResellerCount()));
    }//GEN-LAST:event_deleteResellerButtonActionPerformed

    private void deleteResellerButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_deleteResellerButtonKeyPressed
        if (evt.getKeyCode() == 9) { deleteResellerButton.getNextFocusableComponent().requestFocusInWindow(); }
    }//GEN-LAST:event_deleteResellerButtonKeyPressed

    private void dropResellerTableButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dropResellerTableButtonActionPerformed
        dbClient.dropResellerTable(); dbClient.createResellerTable();
        resellerRecords.setText(Integer.toString(dbClient.getResellerCount()));
    }//GEN-LAST:event_dropResellerTableButtonActionPerformed

    private void dropResellerTableButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dropResellerTableButtonKeyPressed
        if (evt.getKeyCode() == 9) { dropResellerTableButton.getNextFocusableComponent().requestFocusInWindow(); }
    }//GEN-LAST:event_dropResellerTableButtonKeyPressed

    private void resellerIdFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_resellerIdFieldKeyPressed
        if (evt.getKeyCode() == 9) { resellerIdField.getNextFocusableComponent().requestFocusInWindow(); }
    }//GEN-LAST:event_resellerIdFieldKeyPressed

    private void resellerCompanyNameFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_resellerCompanyNameFieldKeyPressed
        if (evt.getKeyCode() == 9) { resellerCompanyNameField.getNextFocusableComponent().requestFocusInWindow(); }
    }//GEN-LAST:event_resellerCompanyNameFieldKeyPressed

    private void resellerAddressFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_resellerAddressFieldKeyPressed
        if (evt.getKeyCode() == 9) { resellerAddressField.getNextFocusableComponent().requestFocusInWindow(); }
    }//GEN-LAST:event_resellerAddressFieldKeyPressed

    private void resellerAddressNrFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_resellerAddressNrFieldKeyPressed
        if (evt.getKeyCode() == 9) { resellerAddressNrField.getNextFocusableComponent().requestFocusInWindow(); }
    }//GEN-LAST:event_resellerAddressNrFieldKeyPressed

    private void resellerCityFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resellerCityFieldActionPerformed
    }//GEN-LAST:event_resellerCityFieldActionPerformed

    private void resellerCityFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_resellerCityFieldKeyPressed
        if (evt.getKeyCode() == 9) { resellerCityField.getNextFocusableComponent().requestFocusInWindow(); }
    }//GEN-LAST:event_resellerCityFieldKeyPressed

    private void resellerPostcodeFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_resellerPostcodeFieldKeyPressed
        if (evt.getKeyCode() == 9) { resellerPostcodeField.getNextFocusableComponent().requestFocusInWindow(); }
    }//GEN-LAST:event_resellerPostcodeFieldKeyPressed

    private void resellerCountryFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_resellerCountryFieldKeyPressed
        if (evt.getKeyCode() == 9) { resellerCountryField.getNextFocusableComponent().requestFocusInWindow(); }
    }//GEN-LAST:event_resellerCountryFieldKeyPressed

    private void resellerDateFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_resellerDateFieldKeyPressed
        if (evt.getKeyCode() == 9) { resellerDateField.getNextFocusableComponent().requestFocusInWindow(); }
    }//GEN-LAST:event_resellerDateFieldKeyPressed

    private void resellerContactNameFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_resellerContactNameFieldKeyPressed
        if (evt.getKeyCode() == 9) { resellerContactNameField.getNextFocusableComponent().requestFocusInWindow(); }
    }//GEN-LAST:event_resellerContactNameFieldKeyPressed

    private void resellerEmailFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_resellerEmailFieldKeyPressed
        if (evt.getKeyCode() == 9) { resellerEmailField.getNextFocusableComponent().requestFocusInWindow(); }
    }//GEN-LAST:event_resellerEmailFieldKeyPressed

    private void resellerPhoneNrFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_resellerPhoneNrFieldKeyPressed
        if (evt.getKeyCode() == 9) { resellerPhoneNrField.getNextFocusableComponent().requestFocusInWindow(); }
    }//GEN-LAST:event_resellerPhoneNrFieldKeyPressed

    private void resellerMobileNrFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_resellerMobileNrFieldKeyPressed
        if (evt.getKeyCode() == 9) { resellerMobileNrField.getNextFocusableComponent().requestFocusInWindow(); }
    }//GEN-LAST:event_resellerMobileNrFieldKeyPressed

    private void resellerDiscountFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_resellerDiscountFieldKeyPressed
        if (evt.getKeyCode() == 9) { resellerDiscountField.getNextFocusableComponent().requestFocusInWindow(); }
    }//GEN-LAST:event_resellerDiscountFieldKeyPressed

    private void destinationTextAreaCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_destinationTextAreaCaretUpdate
        updateOrder();
    }//GEN-LAST:event_destinationTextAreaCaretUpdate

    private void browseFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseFileButtonActionPerformed
        fileBrowser.setVisible(true);
    }//GEN-LAST:event_browseFileButtonActionPerformed

    private void clearResellerFieldsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearResellerFieldsButtonActionPerformed
        resellerIdField.setText("");
        resellerDateField.setText("");
        resellerCompanyNameField.setText("");
        resellerAddressField.setText("");
        resellerAddressNrField.setText("");
        resellerPostcodeField.setText("");
        resellerCityField.setText("");
        resellerCountryField.setText("");
        resellerContactNameField.setText("");
        resellerEmailField.setText("");
        resellerPhoneNrField.setText("");
        resellerMobileNrField.setText("");
        resellerDiscountField.setText("");
        resellerButtonsControl();
    }//GEN-LAST:event_clearResellerFieldsButtonActionPerformed

    private void exampleResellerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exampleResellerButtonActionPerformed
        resellerIdField.setText("");
        resellerDateField.setText("");
        resellerCompanyNameField.setText("Telemarketing Inc.");
        resellerAddressField.setText("Internetstreet");
        resellerAddressNrField.setText("1");
        resellerPostcodeField.setText("4321 BC");
        resellerCityField.setText("Voipcity");
        resellerCountryField.setText("The Netherlands");
        resellerContactNameField.setText("Jan Jansen");
        resellerEmailField.setText("info@voipstorm.nl");
        resellerPhoneNrField.setText("+31 ...");
        resellerMobileNrField.setText("+31 6 ...");
        resellerDiscountField.setText("10");
        resellerButtonsControl();
    }//GEN-LAST:event_exampleResellerButtonActionPerformed

    private void clearCustomerFieldsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearCustomerFieldsButtonActionPerformed
        customerIdField.setText("");
        customerDateField.setText("");
        customerCompanyNameField.setText("");
        customerAddressField.setText("");
        customerAddressNrField.setText("");
        customerPostcodeField.setText("");
        customerCityField.setText("");
        customerCountryField.setText("");
        customerContactNameField.setText("");
        customerEmailField.setText("");
        customerPhoneNrField.setText("");
        customerMobileNrField.setText("");
        customerDiscountField.setText("");
        customerButtonsControl();
    }//GEN-LAST:event_clearCustomerFieldsButtonActionPerformed

    private void exampleCustomerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exampleCustomerButtonActionPerformed
        customerIdField.setText("");
        customerDateField.setText("");
        customerCompanyNameField.setText("Customer Inc.");
        customerAddressField.setText("Busystreet");
        customerAddressNrField.setText("1");
        customerPostcodeField.setText("1234 AB");
        customerCityField.setText("Atlantis");
        customerCountryField.setText("The Netherlands");
        customerContactNameField.setText("John Doe");
        customerEmailField.setText("johndoe@domain.nl");
        customerPhoneNrField.setText("+31 ...");
        customerMobileNrField.setText("+31 6 ...");
        customerDiscountField.setText("10");
        customerButtonsControl();
    }//GEN-LAST:event_exampleCustomerButtonActionPerformed

    private void customerIdFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_customerIdFieldKeyReleased
        customerButtonsControl();
    }//GEN-LAST:event_customerIdFieldKeyReleased

    private void customerCompanyNameFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_customerCompanyNameFieldKeyReleased
        if ((evt.getKeyCode() != 9) && (evt.getKeyCode() != 10)) { customerButtonsControl(); }
    }//GEN-LAST:event_customerCompanyNameFieldKeyReleased

    private void customerAddressFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_customerAddressFieldKeyReleased
        if ((evt.getKeyCode() != 9) && (evt.getKeyCode() != 10)) { customerButtonsControl(); }
    }//GEN-LAST:event_customerAddressFieldKeyReleased

    private void customerAddressNrFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_customerAddressNrFieldKeyReleased
        if ((evt.getKeyCode() != 9) && (evt.getKeyCode() != 10)) { customerButtonsControl(); }
    }//GEN-LAST:event_customerAddressNrFieldKeyReleased

    private void customerPostcodeFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_customerPostcodeFieldKeyReleased
        if ((evt.getKeyCode() != 9) && (evt.getKeyCode() != 10)) { customerButtonsControl(); }
    }//GEN-LAST:event_customerPostcodeFieldKeyReleased

    private void customerCityFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_customerCityFieldKeyReleased
        if ((evt.getKeyCode() != 9) && (evt.getKeyCode() != 10)) { customerButtonsControl(); }
    }//GEN-LAST:event_customerCityFieldKeyReleased

    private void customerCountryFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_customerCountryFieldKeyReleased
        if ((evt.getKeyCode() != 9) && (evt.getKeyCode() != 10)) { customerButtonsControl(); }
    }//GEN-LAST:event_customerCountryFieldKeyReleased

    private void customerContactNameFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_customerContactNameFieldKeyReleased
        if ((evt.getKeyCode() != 9) && (evt.getKeyCode() != 10)) { customerButtonsControl(); }
    }//GEN-LAST:event_customerContactNameFieldKeyReleased

    private void customerEmailFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_customerEmailFieldKeyReleased
        if ((evt.getKeyCode() != 9) && (evt.getKeyCode() != 10)) { customerButtonsControl(); }
    }//GEN-LAST:event_customerEmailFieldKeyReleased

    private void customerPhoneNrFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_customerPhoneNrFieldKeyReleased
        if ((evt.getKeyCode() != 9) && (evt.getKeyCode() != 10)) { customerButtonsControl(); }
    }//GEN-LAST:event_customerPhoneNrFieldKeyReleased

    private void customerMobileNrFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_customerMobileNrFieldKeyReleased
        if ((evt.getKeyCode() != 9) && (evt.getKeyCode() != 10)) { customerButtonsControl(); }
    }//GEN-LAST:event_customerMobileNrFieldKeyReleased

    private void customerDiscountFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_customerDiscountFieldKeyReleased
        if ((evt.getKeyCode() != 9) && (evt.getKeyCode() != 10)) { customerButtonsControl(); }
    }//GEN-LAST:event_customerDiscountFieldKeyReleased

    private void resellerIdFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_resellerIdFieldKeyReleased
        if ((evt.getKeyCode() != 9) && (evt.getKeyCode() != 10)) { resellerButtonsControl(); }
    }//GEN-LAST:event_resellerIdFieldKeyReleased

    private void resellerCompanyNameFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_resellerCompanyNameFieldKeyReleased
        if ((evt.getKeyCode() != 9) && (evt.getKeyCode() != 10)) { resellerButtonsControl(); }
    }//GEN-LAST:event_resellerCompanyNameFieldKeyReleased

    private void resellerAddressFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_resellerAddressFieldKeyReleased
        if ((evt.getKeyCode() != 9) && (evt.getKeyCode() != 10)) { resellerButtonsControl(); }
    }//GEN-LAST:event_resellerAddressFieldKeyReleased

    private void resellerAddressNrFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_resellerAddressNrFieldKeyReleased
        if ((evt.getKeyCode() != 9) && (evt.getKeyCode() != 10)) { resellerButtonsControl(); }
    }//GEN-LAST:event_resellerAddressNrFieldKeyReleased

    private void resellerPostcodeFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_resellerPostcodeFieldKeyReleased
        if ((evt.getKeyCode() != 9) && (evt.getKeyCode() != 10)) { resellerButtonsControl(); }
    }//GEN-LAST:event_resellerPostcodeFieldKeyReleased

    private void resellerCityFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_resellerCityFieldKeyReleased
        if ((evt.getKeyCode() != 9) && (evt.getKeyCode() != 10)) { resellerButtonsControl(); }
    }//GEN-LAST:event_resellerCityFieldKeyReleased

    private void resellerCountryFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_resellerCountryFieldKeyReleased
        if ((evt.getKeyCode() != 9) && (evt.getKeyCode() != 10)) { resellerButtonsControl(); }
    }//GEN-LAST:event_resellerCountryFieldKeyReleased

    private void resellerContactNameFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_resellerContactNameFieldKeyReleased
        if ((evt.getKeyCode() != 9) && (evt.getKeyCode() != 10)) { resellerButtonsControl(); }
    }//GEN-LAST:event_resellerContactNameFieldKeyReleased

    private void resellerEmailFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_resellerEmailFieldKeyReleased
        if ((evt.getKeyCode() != 9) && (evt.getKeyCode() != 10)) { resellerButtonsControl(); }
    }//GEN-LAST:event_resellerEmailFieldKeyReleased

    private void resellerPhoneNrFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_resellerPhoneNrFieldKeyReleased
        if ((evt.getKeyCode() != 9) && (evt.getKeyCode() != 10)) { resellerButtonsControl(); }
    }//GEN-LAST:event_resellerPhoneNrFieldKeyReleased

    private void resellerMobileNrFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_resellerMobileNrFieldKeyReleased
        if ((evt.getKeyCode() != 9) && (evt.getKeyCode() != 10)) { resellerButtonsControl(); }
    }//GEN-LAST:event_resellerMobileNrFieldKeyReleased

    private void resellerDiscountFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_resellerDiscountFieldKeyReleased
        if ((evt.getKeyCode() != 9) && (evt.getKeyCode() != 10)) { resellerButtonsControl(); }
    }//GEN-LAST:event_resellerDiscountFieldKeyReleased

    private void campaignTabMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_campaignTabMouseClicked
//        if(campaignTab.getSelectedIndex() == 0)
//        {
//            displayMessage("Manage your Customers here");
//        }
//        else if(campaignTab.getSelectedIndex() == 1)
//        {
//            displayMessage("Manage your Campaigns here");
//        }

        refreshCustomersAndOrders();
    }//GEN-LAST:event_campaignTabMouseClicked

    private void refreshCustomersAndOrders()
    {
        String[] customers = dbClient.getCustomers();
        if ((customers != null) && (customers.length > 0))
        {
            orderCustomerIdComboBox.setModel(new javax.swing.DefaultComboBoxModel(customers));
            orderCustomerIdComboBox.setEnabled(true);
        }
        else
        {
            orderCustomerIdComboBox.setEnabled(false);
            browseFileButton.setEnabled(false);
            orderFilenameField.setEnabled(false);
            recipientsList.setEnabled(false); recipientsList.setBackground(Color.lightGray);
            timewindowList.setEnabled(false); timewindowList.setBackground(Color.lightGray);
//            orderDestinationsQuantitySlider.setEnabled(false);
            destinationTextArea.setEnabled(false);
            scheduleButton.setEnabled(false);
            confirmOrderButton.setEnabled(false);
        }

        String[] orders = dbClient.getOrders();
        if ((orders != null) && (orders.length > 0))
        {
            orderIdComboBox.setModel(new javax.swing.DefaultComboBoxModel(orders));
            orderIdComboBox.setEnabled(true);
        }
        else
        {
            orderIdComboBox.setEnabled(false);
//            browseFileButton.setEnabled(false);
//            orderFilenameField.setEnabled(false);
        }
    }

    private void orderIdComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_orderIdComboBoxActionPerformed
        int orderId = Integer.parseInt(orderIdComboBox.getSelectedItem().toString());
        selectOrderButton.setEnabled(true);
        orderButtonsControl();
    }//GEN-LAST:event_orderIdComboBoxActionPerformed

    private void orderCustomerIdComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_orderCustomerIdComboBoxActionPerformed
//        timewindowComboBox.setSelectedItem(timeWindow.getCurrentTimeWindow());
        timewindowList.setSelectedIndex(timeTool.getCurrentTimeWindowIndex());
        int timewindowIndex = timewindowList.getSelectedIndices()[0]; campaignCalendar.setStartTimeWindow(timewindowIndex);
        browseFileButton.setEnabled(true);
        orderFilenameField.setEnabled(true);
        orderButtonsControl();
    }//GEN-LAST:event_orderCustomerIdComboBoxActionPerformed

    private void lookAndFeelRButtonGTKMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lookAndFeelRButtonGTKMouseClicked
        setLookAndFeel(PLAF_GTK);
    }//GEN-LAST:event_lookAndFeelRButtonGTKMouseClicked

    private void lookAndFeelRButtonMotifMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lookAndFeelRButtonMotifMouseClicked
        setLookAndFeel(PLAF_MOTIF);
    }//GEN-LAST:event_lookAndFeelRButtonMotifMouseClicked

    private void jRadioButtonWindowsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jRadioButtonWindowsMouseClicked
        setLookAndFeel(PLAF_NIMBUS);
    }//GEN-LAST:event_jRadioButtonWindowsMouseClicked

    private void lookAndFeelRButtonNimbusMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lookAndFeelRButtonNimbusMouseClicked
        setLookAndFeel(PLAF_WINDOWS);
    }//GEN-LAST:event_lookAndFeelRButtonNimbusMouseClicked

    private void managedCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_managedCheckBoxActionPerformed
        if ( managedCheckBox.isSelected() ) {platformIsNetManaged = true;} else {platformIsNetManaged = false;}
    }//GEN-LAST:event_managedCheckBoxActionPerformed

    private void netManagerInboundClientToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_netManagerInboundClientToggleButtonActionPerformed
        if (netManagerInboundClientToggleButton.isSelected()) {
            enableInboundNetManagerClient(true);
        } else {
            enableInboundNetManagerClient(false);
        }
}//GEN-LAST:event_netManagerInboundClientToggleButtonActionPerformed

    private void netManagerOutboundClientToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_netManagerOutboundClientToggleButtonActionPerformed
        if (netManagerOutboundClientToggleButton.isSelected()) {
            enableOutboundNetManagerClient(true);
        } else {
            enableOutboundNetManagerClient(false);
        }
    }//GEN-LAST:event_netManagerOutboundClientToggleButtonActionPerformed

    private void orderIdComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_orderIdComboBoxItemStateChanged
        orderButtonsControl();
    }//GEN-LAST:event_orderIdComboBoxItemStateChanged

    private void orderCustomerIdComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_orderCustomerIdComboBoxItemStateChanged
        orderButtonsControl();
    }//GEN-LAST:event_orderCustomerIdComboBoxItemStateChanged

    private void textLogAreaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_textLogAreaMouseClicked
        if (evt.getClickCount() == 2) {textLogArea.setText(""); }
    }//GEN-LAST:event_textLogAreaMouseClicked

    private void resizeWindowButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resizeWindowButtonActionPerformed
        resizeWindow();
}//GEN-LAST:event_resizeWindowButtonActionPerformed

    private void tabPaneMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabPaneMouseClicked
//        if (tabPane.getSelectedIndex() == 0)
//        {
//            displayMessage("Dashboard Running Campaign");
//        }
//        else if(tabPane.getSelectedIndex() == 1)
//        {
//            displayMessage("Manage Customers and Campaigns");
//        }
//        else if(tabPane.getSelectedIndex() == 2)
//        {
//            displayMessage("Generic Tools and Information");
//        }
//        else if(tabPane.getSelectedIndex() == 3)
//        {
//            displayMessage("Internet Telephone Provider Configuration");
//        }
//        else if(tabPane.getSelectedIndex() == 4)
//        {
//            displayMessage("Software Update Manager");
//        }
//        else if(tabPane.getSelectedIndex() == 5)
//        {
//            displayMessage("License Manager");
//        }
//        else if(tabPane.getSelectedIndex() == 6)
//        {
//            displayMessage("Activity and Debug Logging");
//        }
//        else if(tabPane.getSelectedIndex() == 7)
//        {
//            displayMessage("Database Manager");
//        }
        if(tabPane.getSelectedIndex() == 8)
        {
            if (brandLabel.getForeground().getRGB() == sizeControlPanel.getBackground().getRGB())
            {


                threadExecutor.execute(new Runnable()
                {

                    @Override
                    @SuppressWarnings({"static-access"})
                    public void run()
                    {

                        Color textColor = sizeControlPanel.getBackground();
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

//                Thread aboutFadeThread = new Thread(new Runnable()
//                {
//                    @Override
//                    @SuppressWarnings({"static-access"})
//                    public void run()
//                    {
//                        Color textColor = sizeControlPanel.getBackground();
//                        while(brandLabel.getForeground().getGreen() < 255)
//                        {
//                            textColor = new Color(textColor.getRed() + 1,textColor.getGreen() + 1,textColor.getBlue() + 1);
//                            brandLabel.setForeground(textColor);
//                            brandDescriptionLabel.setForeground(textColor);
//                            productLabel.setForeground(textColor);
//                            productDescriptionLabel.setForeground(textColor);
//                            copyrightLabel.setForeground(textColor);
//                            try { Thread.sleep(10); } catch (InterruptedException ex) { }
//                        }
//                    }
//                });
//                aboutFadeThread.setName("aboutFadeThread");
//                aboutFadeThread.setDaemon(runThreadsAsDaemons);
//                aboutFadeThread.start();
            }
        }
    }//GEN-LAST:event_tabPaneMouseClicked

    private void dropAllTablesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dropAllTablesButtonActionPerformed
        final boolean lastStateManagedCheckBox = managedCheckBox.isSelected();
        callCenterEnabledCheckBox.setSelected(false);
        dbClient.dropCustomerTable(); dbClient.createCustomerTable();
        dbClient.dropCustomerOrderTable(); dbClient.createCustomerOrderTable();
        dbClient.dropInvoiceTable(); dbClient.createInvoiceTable();
        dbClient.dropCampaignTable(); dbClient.createCampaignTable();
        dbClient.dropDestinationTable(); dbClient.createDestinationTable();
        dbClient.dropCampaignStatTable(); dbClient.createCampaignStatTable();
        dbClient.dropPricelistTable(); dbClient.createPricelistTable();
        dbClient.dropResellerTable(); dbClient.createResellerTable();
        managedCheckBox.setSelected(lastStateManagedCheckBox);
        updateDatabaseStats();
    }//GEN-LAST:event_dropAllTablesButtonActionPerformed

    private void dropAllTablesButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dropAllTablesButtonKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_dropAllTablesButtonKeyPressed

    private void toolsTabKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_toolsTabKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_toolsTabKeyPressed

    private void toolsInnerPanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_toolsInnerPanelMouseClicked
        if (evt.getClickCount() == 2) { startJConsole(); }
    }//GEN-LAST:event_toolsInnerPanelMouseClicked

    private void checkVersionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkVersionButtonActionPerformed
        checkVersion();
    }//GEN-LAST:event_checkVersionButtonActionPerformed

    private void checkVersionButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_checkVersionButtonKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_checkVersionButtonKeyPressed

    private void javaOptionsFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_javaOptionsFieldKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_javaOptionsFieldKeyPressed

    private void javaOptionsFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_javaOptionsFieldKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_javaOptionsFieldKeyReleased

    private void recipientsListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_recipientsListValueChanged
        updateOrder();
    }//GEN-LAST:event_recipientsListValueChanged

    private void timewindowListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_timewindowListValueChanged
//        if (orderFilenameField.getText().length() > 0 )
//        {
////            campaignCalendar.setTimeWindow(timewindowComboBox.getSelectedItem().toString()); campaignCalendar.dateChooserPanel.setMinDate(currentTimeCalendar);
//            int earliestTimewindowIndex = 0; int counter = 0; // Let's just say the earliestTimewindowIndex will be the lowest selected entry in the timewindowList to set a starttime range for the hour and minute sliders in campaignCalendar
//            for (int timewindowIndex:timewindowList.getSelectedIndices()) { if ( timewindowIndex != -1) { earliestTimewindowIndex = counter; } counter++; }
//            campaignCalendar.setStartTimeWindow(earliestTimewindowIndex);
//
//            Order order = new Order();
//
//            campaignCalendar.dateChooserPanel.setMinDate(currentTimeCalendar);
//            Calendar yesterdayCalendar = Calendar.getInstance(); yesterdayCalendar.setTimeInMillis(Calendar.getInstance().getTimeInMillis() - 86400000); campaignCalendar.dateChooserPanel.setMinDate(yesterdayCalendar);
//            try { campaignCalendar.dateChooserPanel.setForbiddenPeriods(dbClient.getForbiddenCampaignPeriodSet(order.getIndices2Static(timewindowList.getSelectedIndices()))); } catch (IncompatibleDataExeption ex) { }
//            updateOrder();
//        }
        if (orderFilenameField.getText().length() > 0 )
        {
            int timewindowIndex = timewindowList.getSelectedIndices()[0]; campaignCalendar.setStartTimeWindow(timewindowIndex);

            campaignCalendar.dateChooserPanel.setMinDate(currentTimeCalendar);
            Calendar yesterdayCalendar = Calendar.getInstance(); yesterdayCalendar.setTimeInMillis(Calendar.getInstance().getTimeInMillis() - 86400000); campaignCalendar.dateChooserPanel.setMinDate(yesterdayCalendar); // set forbidden period
//            try { campaignCalendar.dateChooserPanel.setForbiddenPeriods(dbClient.getForbiddenCampaignPeriodSet(timewindowComboBox.getSelectedItem().toString())); } catch (IncompatibleDataExeption ex) { }
            try { campaignCalendar.dateChooserPanel.setForbiddenPeriods(dbClient.getForbiddenCampaignPeriodSet(order.getIndices2Static(timewindowList.getSelectedIndices()))); } catch (IncompatibleDataExeption ex) { }
            updateOrder();
        }
    }//GEN-LAST:event_timewindowListValueChanged

    private void destinationTextAreaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_destinationTextAreaFocusGained
        if ( destinationTextArea.getText().equals("Phonenumber List") ) { destinationTextArea.setText(""); }
    }//GEN-LAST:event_destinationTextAreaFocusGained

    private void testPhoneButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_testPhoneButtonActionPerformed
        Thread startEPhoneThread = new Thread(new Runnable()
        {
            String[] status = new String[2];
            @Override
            @SuppressWarnings({"static-access"})
            public void run()
            {
                showStatus("Starting EPhone", true, true);
                status[0] = "0"; status[1] = "";
                shell.startEPhone(javaOptionsField.getText());
                if (status[0].equals("0")) { showStatus(status[1], true, true); } else { showStatus(status[1], true, true); }
                return;
            }
        });
        startEPhoneThread.setName("startEPhoneThread");
        startEPhoneThread.setDaemon(runThreadsAsDaemons);
        startEPhoneThread.start();
    }//GEN-LAST:event_testPhoneButtonActionPerformed

    private void headerLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_headerLabelMouseClicked
        updateDatabaseStats();
    }//GEN-LAST:event_headerLabelMouseClicked

    private void vergunningTypeListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_vergunningTypeListMouseClicked
        if (vergunningTypeList.getSelectedIndex() == 0 ) { vergunningPeriodList.setSelectedIndex(0); } else { vergunningPeriodList.setSelectedIndex(1); }
        displayMessage("Please click Request License");
        orderVergunningCode();
}//GEN-LAST:event_vergunningTypeListMouseClicked

    private void vergunningDateChooserPanelOnSelectionChange(datechooser.events.SelectionChangedEvent evt) {//GEN-FIRST:event_vergunningDateChooserPanelOnSelectionChange
        vergunningStartCalendar = vergunningDateChooserPanel.getSelectedDate();
        vergunningStartCalendar.set(Calendar.HOUR_OF_DAY, (int)0);
        vergunningStartCalendar.set(Calendar.MINUTE, (int)0);
        vergunningStartCalendar.set(Calendar.SECOND, (int)0);
        vergunningEndCalendar.setTimeInMillis(vergunningStartCalendar.getTimeInMillis());
        orderVergunningCode();
}//GEN-LAST:event_vergunningDateChooserPanelOnSelectionChange

    private void vergunningPeriodListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_vergunningPeriodListMouseClicked
        vergunning.setVergunningPeriod(vergunningPeriodList.getSelectedValue().toString());
        orderVergunningCode();
}//GEN-LAST:event_vergunningPeriodListMouseClicked

    private void vergunningCodeFieldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_vergunningCodeFieldMouseClicked
        if ( evt.getClickCount() == 2 ) {
            licenseCodePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "License Authorisation Code", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("STHeiti", 0, 10))); // NOI18N
            vergunningCodeField.setForeground(Color.WHITE);
        }
}//GEN-LAST:event_vergunningCodeFieldMouseClicked

    private void vergunningCodeFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vergunningCodeFieldActionPerformed
        if (vergunningCodeField.getText().equals(VERGUNNINGTOEKENNERTOEGANG)) {
            vergunningCodeField.setText(MD5Converter.getMD5SumFromString(activationCodeField.getText() + VERGUNNINGTOEKENNERTOEGANG));
            vergunningCodeField.setForeground(Color.BLACK);
            licenseCodePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "License Code", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("STHeiti", 0, 10))); // NOI18N
            applyVergunningButton.setEnabled(false);
            applyVergunning();
        } else {
            licenseCodePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "License Code", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("STHeiti", 0, 10))); // NOI18N
            applyVergunningButton.setEnabled(false);
            applyVergunning();
        }
}//GEN-LAST:event_vergunningCodeFieldActionPerformed

    private void vergunningCodeFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_vergunningCodeFieldKeyReleased
        if ((vergunningCodeField.getText().length() > 0 ) && (evt.getKeyCode() != 10)) {
            applyVergunningButton.setEnabled(true); requestVergunningButton.setEnabled(true); } else    { applyVergunningButton.setEnabled(false); requestVergunningButton.setEnabled(false); }
}//GEN-LAST:event_vergunningCodeFieldKeyReleased

    private void applyVergunningButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyVergunningButtonActionPerformed
        applyVergunning();
}//GEN-LAST:event_applyVergunningButtonActionPerformed

    private void requestVergunningButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_requestVergunningButtonActionPerformed
        try { java.awt.Desktop.getDesktop().browse(java.net.URI.create(Vergunning.REQUEST_VERGUNNINGLINK + "servlet?Page=Order_License_Page&Form=Order_License_Form&Activation_Code_Field=" + activationCodeField.getText() + "&Order_License_Button=Bestel")); } catch (IOException ex) { }
}//GEN-LAST:event_requestVergunningButtonActionPerformed

    private void licenseTabMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_licenseTabMouseClicked

}//GEN-LAST:event_licenseTabMouseClicked

    private void pubIPFieldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pubIPFieldMouseClicked
        if (evt.getClickCount() == 2)
        {
            try { java.awt.Desktop.getDesktop().browse(java.net.URI.create("http://www.google.nl/search?q=my+ip+address&ie=UTF-8")); } catch (IOException ex) { }
        }
    }//GEN-LAST:event_pubIPFieldMouseClicked

    private void subscribeToVOIPProviderButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subscribeToVOIPProviderButtonActionPerformed
        try { java.awt.Desktop.getDesktop().browse(java.net.URI.create("http://www.budgetphone.nl/page.php?page=form_postpaid&company=1")); } catch (IOException ex) { }
    }//GEN-LAST:event_subscribeToVOIPProviderButtonActionPerformed

    private void previousInvoiceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previousInvoiceButtonActionPerformed
        if ( invoiceIdField.getText().equals("") ) { invoiceIdField.setText("0"); } // If Id field is empty it puts in a 0 (first destination)
        if (Integer.parseInt(invoiceIdField.getText()) > 0) { invoiceIdField.setText(Integer.toString(Integer.parseInt(invoiceIdField.getText())-1)); }
        selectInvoice();
    }//GEN-LAST:event_previousInvoiceButtonActionPerformed

    private void nextInvoiceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextInvoiceButtonActionPerformed
        if ( invoiceIdField.getText().equals("") ) { invoiceIdField.setText("0"); } // If Id field is empty it puts in a 0 (first destination)
        invoiceIdField.setText(Integer.toString(Integer.parseInt(invoiceIdField.getText())+1));
        selectInvoice();
    }//GEN-LAST:event_nextInvoiceButtonActionPerformed

    private void previousCampaignButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previousCampaignButtonActionPerformed
        if ( idField.getText().equals("") ) { idField.setText("0"); } // If Id field is empty it puts in a 0 (first destination)
        if (Integer.parseInt(idField.getText()) > 0) { idField.setText(Integer.toString(Integer.parseInt(idField.getText())-1)); }
        selectCampaign();
    }//GEN-LAST:event_previousCampaignButtonActionPerformed

    private void nextCampaignButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextCampaignButtonActionPerformed
        if ( idField.getText().equals("") ) { idField.setText("0"); } // If Id field is empty it puts in a 0 (first destination)
        idField.setText(Integer.toString(Integer.parseInt(idField.getText())+1));
        selectCampaign();
    }//GEN-LAST:event_nextCampaignButtonActionPerformed

    private void previousDestinationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previousDestinationButtonActionPerformed
        if ( destIdField.getText().equals("") ) { destIdField.setText("0"); }
        if ( responseStatusCodeField.getText().equals("") ) { responseStatusCodeField.setText("0"); }
        if (Integer.parseInt(destIdField.getText()) > 0) { destIdField.setText(Integer.toString(Integer.parseInt(destIdField.getText())-1)); }
        selectDestination();
    }//GEN-LAST:event_previousDestinationButtonActionPerformed

    private void selectDestination()
    {
        if ( destIdField.getText().equals("") ) { destIdField.setText("0"); }
        Destination tmpDestination = dbClient.selectDestination(Integer.parseInt(destIdField.getText()));
//        destIdField.setText(Long.toString(tmpDestination.getId()));
        campaignIdField.setText(Long.toString(tmpDestination.getCampaignId()));
        destinationField.setText(tmpDestination.getDestination());
        destCountField.setText(Integer.toString(tmpDestination.getDestinationCount()));
        connectingTimestampField.setText(Long.toString(tmpDestination.getConnectingTimestamp()));
        tryingTimestampField.setText(Long.toString(tmpDestination.getTryingTimestamp()));
        callingTimestampField.setText(Long.toString(tmpDestination.getCallingTimestamp()));
        callingAttemptsField.setText(Integer.toString(tmpDestination.getCallingAttempts()));
        ringingTimestampField.setText(Long.toString(tmpDestination.getRingingTimestamp()));
        acceptingTimestampField.setText(Long.toString(tmpDestination.getAcceptingTimestamp()));
        talkingTimestampField.setText(Long.toString(tmpDestination.getTalkingTimestamp()));
        localCancelingTimestampField.setText(Long.toString(tmpDestination.getLocalCancelingTimestamp()));
        remoteCancelingTimestampField.setText(Long.toString(tmpDestination.getRemoteCancelingTimestamp()));
        localBusyTimestampField.setText(Long.toString(tmpDestination.getLocalBusyTimestamp()));
        remoteBusyTimestampField.setText(Long.toString(tmpDestination.getRemoteBusyTimestamp()));
        localByeTimestampField.setText(Long.toString(tmpDestination.getLocalByeTimestamp()));
        remoteByeTimestampField.setText(Long.toString(tmpDestination.getRemoteByeTimestamp()));
        responseStatusCodeField.setText(Long.toString(tmpDestination.getResponseStatusCode()));
        responseStatusDescField.setText(tmpDestination.getResponseStatusDesc());

        if ( destIdField.getText().equals("") ) { destIdField.setText("0"); }
        if ( responseStatusCodeField.getText().equals("") ) { responseStatusCodeField.setText("0"); }

        tmpDestination = dbClient.selectDestination(Integer.parseInt(destIdField.getText()));
//        destIdField.setText(Long.toString(tmpDestination.getId()));
        campaignIdField.setText(Long.toString(tmpDestination.getCampaignId()));
        destinationField.setText(tmpDestination.getDestination());
        destCountField.setText(Integer.toString(tmpDestination.getDestinationCount()));
        connectingTimestampField.setText(Long.toString(tmpDestination.getConnectingTimestamp()));
        tryingTimestampField.setText(Long.toString(tmpDestination.getTryingTimestamp()));
        callingTimestampField.setText(Long.toString(tmpDestination.getCallingTimestamp()));
        callingAttemptsField.setText(Integer.toString(tmpDestination.getCallingAttempts()));
        ringingTimestampField.setText(Long.toString(tmpDestination.getRingingTimestamp()));
        acceptingTimestampField.setText(Long.toString(tmpDestination.getAcceptingTimestamp()));
        talkingTimestampField.setText(Long.toString(tmpDestination.getTalkingTimestamp()));
        localCancelingTimestampField.setText(Long.toString(tmpDestination.getLocalCancelingTimestamp()));
        remoteCancelingTimestampField.setText(Long.toString(tmpDestination.getRemoteCancelingTimestamp()));
        localBusyTimestampField.setText(Long.toString(tmpDestination.getLocalBusyTimestamp()));
        remoteBusyTimestampField.setText(Long.toString(tmpDestination.getRemoteBusyTimestamp()));
        localByeTimestampField.setText(Long.toString(tmpDestination.getLocalByeTimestamp()));
        remoteByeTimestampField.setText(Long.toString(tmpDestination.getRemoteByeTimestamp()));
        responseStatusCodeField.setText(Long.toString(tmpDestination.getResponseStatusCode()));
        responseStatusDescField.setText(tmpDestination.getResponseStatusDesc());
    }


    private void nextDestinationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextDestinationButtonActionPerformed
        if ( destIdField.getText().equals("") ) { destIdField.setText("0"); }
        if ( responseStatusCodeField.getText().equals("") ) { responseStatusCodeField.setText("0"); }
        destIdField.setText(Integer.toString(Integer.parseInt(destIdField.getText())+1));
        selectDestination();
    }//GEN-LAST:event_nextDestinationButtonActionPerformed

    private void previousCustomerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previousCustomerButtonActionPerformed
        if ( customerIdField.getText().equals("") ) { customerIdField.setText("0"); }
        if (Integer.parseInt(customerIdField.getText()) > 0) { customerIdField.setText(Integer.toString(Integer.parseInt(customerIdField.getText())-1)); }
        selectCustomer();
    }//GEN-LAST:event_previousCustomerButtonActionPerformed

    private void previousCustomerButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_previousCustomerButtonKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_previousCustomerButtonKeyPressed

    private void nextCustomerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextCustomerButtonActionPerformed
        if ( customerIdField.getText().equals("") ) { customerIdField.setText("0"); }
        customerIdField.setText(Integer.toString(Integer.parseInt(customerIdField.getText())+1));
        selectCustomer();
    }//GEN-LAST:event_nextCustomerButtonActionPerformed

    private void nextCustomerButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_nextCustomerButtonKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_nextCustomerButtonKeyPressed

    private void backofficeTabMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_backofficeTabMouseClicked
//        if(backofficeTab.getSelectedIndex() == 0)
//        {
//            displayMessage("Invoice Editor");
//        }
//        else if(backofficeTab.getSelectedIndex() == 1)
//        {
//            displayMessage("Campaign Editor");
//        }
//        else if(backofficeTab.getSelectedIndex() == 2)
//        {
//            displayMessage("Phonenumber / Destination Editor");
//        }
//        else if(backofficeTab.getSelectedIndex() == 3)
//        {
//            displayMessage("CampaignStats Editor");
//        }
//        else if(backofficeTab.getSelectedIndex() == 4)
//        {
//            displayMessage("Pricelist Editor");
//        }
//        else if(backofficeTab.getSelectedIndex() == 5)
//        {
//            displayMessage("Reseller Editor");
//        }
//        else if(backofficeTab.getSelectedIndex() == 6)
//        {
//            displayMessage("Database Table Status and Reset");
//        }
    }//GEN-LAST:event_backofficeTabMouseClicked

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
            inboundCallCenterButton.setEnabled(true);
            outboundCallCenterButton.setEnabled(true);
//            performanceMeter.setCallPerHourScale(0, (vergunning.getCallsPerHour() / 100), (vergunning.getCallsPerHour() / 1000));
//            movePerformanceMeter(0, true);
            if ((prefPhoneLinesSlider.getMaximum() == 0) || (prefPhoneLinesSlider.getMaximum() > vergunning.getPhoneLines()))
            {
                prefPhoneLinesSlider.setMaximum(vergunning.getPhoneLines()); prefPhoneLinesSlider.setValue(vergunning.getPhoneLines());
            }
            else
            {
                prefPhoneLinesSlider.setMaximum(vergunning.getPhoneLines()); prefPhoneLinesSlider.setValue(Integer.parseInt(configurationCallCenter.getPrefPhoneLines()));
            }

            Thread validLicenseAppliedThread = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    displayMessage("You need a VOIP Provider to make real phonecalls, we recommend: www.budgetphone.nl");
                    showStatus("Please Configure Network Settings", true, false);
                    showStatusInbound("Tooltips for help are available", false, false);

                    Thread getPublicIPThread = new Thread(new Runnable()
                    {
                        @Override
                        @SuppressWarnings({"static-access"})
                        public void run()
                        {
                            pubIPField.setText(PublicIP.getPublicIP());
                        }
                    });
                    getPublicIPThread.setName("getPublicIPThread");
                    getPublicIPThread.setDaemon(true);
                    getPublicIPThread.setPriority(1);
                    getPublicIPThread.start();

                    applyVergunningButton.setEnabled(false);
                    requestVergunningButton.setEnabled(false);
                    try { Thread.sleep(1000); } catch (InterruptedException ex) { }

                    if
                    (
                        (pubIPField.getText().length() == 0) ||
                        (domainField.getText().length() == 0) ||
                        (serverIPField.getText().length() == 0) ||
                        (usernameField.getText().length() == 0) ||
                        (toegangField.getPassword().length == 0)
                    )
                    {
                        tabPane.setSelectedIndex(3); // Config
                    }
                    else
                    {
                        tabPane.setEnabled(true);
                        tabPane.setSelectedIndex(1); // Campaign
                        statusBarOutbound.setText("");
                        statusBarInbound.setText("");
                    }
                }
            });
            validLicenseAppliedThread.setName("validLicenseAppliedThread");
            validLicenseAppliedThread.setDaemon(runThreadsAsDaemons);
            validLicenseAppliedThread.start();
        }
        else
        {
            prefPhoneLinesSlider.setMaximum(Integer.parseInt(configurationCallCenter.getPrefPhoneLines())); prefPhoneLinesSlider.setValue(Integer.parseInt(configurationCallCenter.getPrefPhoneLines()));
            Thread invalidLicenseAppliedThread = new Thread(new Runnable()
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

    private void resizeWindow()
    {
        Thread resizeWindowThread = new Thread(new Runnable()
        {
            @Override
            @SuppressWarnings({"static-access"})
            public void run()
            {
                if ( getSize().getHeight() == getMinimumSize().getHeight() )
                {
                    tabPane.setVisible(false);
                    summaryDisplayPanel.setVisible(false);
                    int dimWidth = (int) getMaximumSize().getWidth();
                    int dimHeight = (int) getMinimumSize().getHeight();
                    double step = 120;
                    while ( dimHeight < (int) getMaximumSize().getHeight() )
                    {
                        setSize(dimWidth, dimHeight);
                        try { Thread.sleep(2); } catch (InterruptedException ex) { }
//                            showStatus("Resizing: " + dimHeight, false);
                        dimHeight += step;
                        step = ( step * 0.65 ) + 1;
                    }
                    setSize(getMaximumSize());
                    summaryDisplayPanel.setVisible(true);
                    tabPane.setVisible(true);
                    resizeWindowButton.setText(icons.getResizeUpChar());
                    resizeWindowButton.setToolTipText("Hide Administration");
                }
                else
                {
                    tabPane.setVisible(false);
                    summaryDisplayPanel.setVisible(false);
                    int dimWidth = (int) getMaximumSize().getWidth();
                    int dimHeight = (int) getMaximumSize().getHeight();
                    double step = 120;
                    while ( dimHeight > (int) getMinimumSize().getHeight() )
                    {
                        setSize(dimWidth, dimHeight);
                        try { Thread.sleep(2); } catch (InterruptedException ex) { }
//                            showStatus("Resizing: " + dimHeight, false);
                        dimHeight -= step;
                        step = ( step * 0.65 ) + 1;
                    }
                    setSize(getMinimumSize());
                    summaryDisplayPanel.setVisible(true);
                    tabPane.setVisible(true);
                    resizeWindowButton.setText(icons.getResizeDownChar());
                    resizeWindowButton.setToolTipText("Show Administration");
                }
            }
        });
        resizeWindowThread.setName("resizeWindowThread");
        resizeWindowThread.setDaemon(runThreadsAsDaemons);
        resizeWindowThread.start();
    }

    private void enableInboundNetManagerClient(boolean enableParam)
    {
        if (enableParam)    { if (inboundNetManagerClient == null) { inboundNetManagerClient = new NetManagerClient(this, INBOUND_PORT); } }
        else                { if (inboundNetManagerClient != null) { /* inboundNetManagerClient.closeConnection(); */ inboundNetManagerClient = null;} }
    }

    private void enableOutboundNetManagerClient(boolean enableParam)
    {
        if (enableParam)    { if (outboundNetManagerClient == null) { outboundNetManagerClient = new NetManagerClient(this, OUTBOUND_PORT); } }
        else                { if (outboundNetManagerClient != null) { /* outboundNetManagerClient.closeConnection(); */ outboundNetManagerClient = null;} }
    }

    /**
     *
     */
    public void updateOrder()
    {
	if (!selectOrderButton.hasFocus())
	{
	    showStatus("Specify valid Soundfile (*.wav)", false, false);

	    // Reset the Fields just to prevent invalid outdated values to remain after an error
	    orderMessageDurationField.setText("");
	    orderMessageRatePerSecondField.setText("");
	    orderMessageRateField.setText("");
	    orderSubTotalField.setText("");

	    // Calculate and Set Message Duration
	    soundFileToStream = orderFilenameField.getText();
	    filename = "file:" + soundFileToStream;
	    SoundTool mySoundTool = null; mySoundTool = new SoundTool("file:" + soundFileToStream);
	    orderMessageDurationField.setText(Integer.toString(mySoundTool.getMediaDuration()));
	    int messageDuration = (mySoundTool.getMediaDuration());
	    mySoundTool.dispose(); mySoundTool = null;

	    // Get, Round and Set MessageRatePerSecond
	    pricelist = new Pricelist();
	    float messageRatePerSecondPrecise = 0;
//	    String recipients = recipientsComboBox.getSelectedItem().toString();
	    String recipients = recipientsList.getSelectedValue().toString();
	    String timewindow = timewindowList.getSelectedValue().toString();

	    // Set the MessageRatePerSecond related to choosen Recipients and Timewindow Category
	    if ((recipients.equals("Business"))  && (timewindow.equals("Daytime"))) { messageRatePerSecondPrecise = pricelist.getB2BDaytimeRatePerSecond(); }
	    if ((recipients.equals("Business"))  && (timewindow.equals("Evening"))) { messageRatePerSecondPrecise = pricelist.getB2BEveningRatePerSecond(); }
	    if ((recipients.equals("Consumers")) && (timewindow.equals("Daytime"))) { messageRatePerSecondPrecise = pricelist.getB2CDaytimeRatePerSecond(); }
	    if ((recipients.equals("Consumers")) && (timewindow.equals("Evening"))) { messageRatePerSecondPrecise = pricelist.getB2CEveningRatePerSecond(); }
	    if ((recipients.equals("Custom"))    && (timewindow.equals("Daytime"))) { messageRatePerSecondPrecise = pricelist.getA2SDaytimeRatePerSecond(); }
	    if ((recipients.equals("Custom"))    && (timewindow.equals("Evening"))) { messageRatePerSecondPrecise = pricelist.getA2SEveningRatePerSecond(); }

	    float messageRatePerSecondRounded = (float)(Math.round(messageRatePerSecondPrecise*1000.0) / 1000.0);

	    // Round and Set MessageRate
	    float messageRatePrecise = (messageRatePerSecondRounded * messageDuration);
	    if (messageRatePrecise < (float)0.01) { messageRatePrecise = (float)0.01; }
	    float messageRateRounded = (float)(Math.round(messageRatePrecise*1000.0) / 1000.0);

	    // Calc and Set SubTotal
            float subTotal = 0;
            subTotal = ((destinationTextArea.getLineCount() - 1) * messageRateRounded );
            orderDestinationsQuantityField.setText((Integer.toString((destinationTextArea.getLineCount() -1 ))));
            
            int[] timewindowIndexArray = new int[3]; Order order = new Order();
            timewindowIndexArray = order.getIndices2Static(timewindowList.getSelectedIndices());

            tmpOrder = new Order();

	    tmpOrder.setOrderId(0);
	    tmpOrder.setCustomerId(0);
//	    tmpOrder.setRecipientsCategory(recipientsComboBox.getSelectedItem().toString());
	    tmpOrder.setRecipientsCategory(recipientsList.getSelectedValue().toString());
//	    tmpOrder.setTimeWindowCategory(timewindowComboBox.getSelectedItem().toString());

//	    tmpOrder.setTimeWindowIndices(timewindowList.getSelectedIndices());
            tmpOrder.setTimeWindow0(timewindowIndexArray[0]);
            tmpOrder.setTimeWindow1(timewindowIndexArray[1]);
            tmpOrder.setTimeWindow2(timewindowIndexArray[2]);

//	    tmpOrder.setTargetTransactionQuantity(orderDestinationsQuantitySlider.getValue());
	    tmpOrder.setTargetTransactionQuantity(Integer.parseInt(orderDestinationsQuantityField.getText()));
	    tmpOrder.setMessageFilename(orderFilenameField.getText());
	    tmpOrder.setMessageDuration(messageDuration);
	    tmpOrder.setMessageRatePerSecond(messageRatePerSecondRounded);
	    tmpOrder.setMessageRate(messageRateRounded);
	    tmpOrder.setSubTotal(subTotal);

	    orderMessageDurationField.setText(Integer.toString(tmpOrder.getMessageDuration()));
	    orderMessageRatePerSecondField.setText(Float.toString(tmpOrder.getMessageRatePerSecond()));
	    orderMessageRateField.setText(Float.toString(tmpOrder.getMessageRate()));
//	    orderSubTotalField.setText(Float.toString(tmpOrder.getSubTotal()));
//	    orderSubTotalField.setText(String.format("%f",Float.toString(tmpOrder.getSubTotal())));
	    orderSubTotalField.setText(Float.toString( ((float)Math.round(tmpOrder.getSubTotal()*100))/100) );

            if (messageDuration > 0)
            {
                if (!recipientsList.isEnabled())                    { recipientsList.setEnabled(true); recipientsList.setBackground(Color.WHITE); }
                if (!timewindowList.isEnabled())                    { timewindowList.setEnabled(true); timewindowList.setBackground(Color.WHITE); }
                if (!scheduleButton.isEnabled())                    { scheduleButton.setEnabled(true); }
                if (!destinationTextArea.isEnabled())
                {
                    destinationTextArea.setEnabled(true);
                    destinationTextArea.setBackground(Color.WHITE);
                    destinationTextArea.append("Phonenumber List");
                }
            }
            else
            {
                if (recipientsList.isEnabled())                     { recipientsList.setEnabled(false); recipientsList.setBackground(Color.lightGray); }
                if (timewindowList.isEnabled())                     { timewindowList.setEnabled(false); timewindowList.setBackground(Color.lightGray); }
                if (scheduleButton.isEnabled())                     { scheduleButton.setEnabled(false); }
                if (destinationTextArea.isEnabled())                { destinationTextArea.setEnabled(false); }
                if (scheduleButton.isEnabled())                     { scheduleButton.setEnabled(false);}
                if (confirmOrderButton.isEnabled())                 { confirmOrderButton.setEnabled(false);}
            }

            if ((orderDestinationsQuantityField.getText() != null) && (Integer.parseInt(orderDestinationsQuantityField.getText()) > 0) && (destinationTextArea.isEnabled()) && (destinationTextArea.getLineCount() > 0))
            {
                confirmOrderButton.setEnabled(true);
            }
            else
            {
                confirmOrderButton.setEnabled(false);
            }

	    showStatus("Updating Soundfile Completed", false, false);
	}
    }

    private void updateDatabaseStats()
    {
        Thread updateDatabaseStatsThread = new Thread(new Runnable()
        {
            @Override
            @SuppressWarnings({"static-access"})
            public void run()
            {
                customerRecords.setText(Integer.toString(dbClient.getCustomerCount()));
                orderRecords.setText(Integer.toString(dbClient.getCustomerOrderCount()));
                invoiceRecords.setText(Integer.toString(dbClient.getInvoiceCount()));
                campaignRecords.setText(Integer.toString(dbClient.getCampaignCount()));
                destinationRecords.setText(Integer.toString(dbClient.getDestinationCount()));
                campaignStatsRecords.setText(Integer.toString(dbClient.getCampaignStatsCount()));
                pricelistRecords.setText(Integer.toString(dbClient.getPricelistCount()));
                resellerRecords.setText(Integer.toString(dbClient.getResellerCount()));
            }
        });
        updateDatabaseStatsThread.setName("updateDatabaseStatsThread");
        updateDatabaseStatsThread.setDaemon(runThreadsAsDaemons);
        updateDatabaseStatsThread.start();
    }

    public static String getBrand()                             { return Vergunning.BRAND; }
    public static String getBusiness()                          { return Vergunning.BUSINESS; }
    public static String getBrandDescription()                  { return Vergunning.BRAND_DESCRIPTION; }
    public static String getProduct()                           { return Vergunning.PRODUCT; }
    public static String getWindowTitle()                       { return Vergunning.BRAND + " " + THISPRODUCT + " " + VERSION; }
    public static String getProductDescription()                { return Vergunning.PRODUCT_DESCRIPTION; }
    public static String getCopyright()                         { return Vergunning.COPYRIGHT; }
    public static String getAuthor()                            { return Vergunning.AUTHOR; }
    public static String getWarning()                           { return Vergunning.WARNING; }
    public static String getVersion()                           { return VERSION; }
    public void          setSoundFile(String soundFileParam)    { orderFilenameField.setText(soundFileParam); }

//    public void showMessage(String messageParam)
//    {
//        showStatus(messageParam);
//        netManagerClient.closeConnection();
//    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel aboutTab;
    private javax.swing.JTextField acceptingACField;
    private javax.swing.JTextField acceptingTTField;
    private javax.swing.JTextField acceptingTimestampField;
    private javax.swing.JLabel actLabel;
    private javax.swing.JTextField activationCodeField;
    private javax.swing.JPanel activationCodePanel;
    private javax.swing.JPanel answerDelayPanel;
    private javax.swing.JLabel applicationLabel;
    private javax.swing.JButton applyVergunningButton;
    private javax.swing.JPanel authenticationPanel;
    private javax.swing.JLabel b2bLabel;
    private javax.swing.JLabel b2cLabel;
    private javax.swing.JTabbedPane backofficeTab;
    private javax.swing.JTextArea brandDescriptionLabel;
    private javax.swing.JLabel brandLabel;
    private javax.swing.JButton browseFileButton;
    private javax.swing.JLabel busyLabel;
    private javax.swing.JPanel busyRatioPanel;
    private javax.swing.JLabel busyTotalLabel;
    private javax.swing.JLabel byeTotalLabel;
    private javax.swing.JCheckBox callCenterEnabledCheckBox;
    private javax.swing.JLabel callCenterEnabledLabel;
    private javax.swing.JPanel callCompletionPanel;
    private javax.swing.JPanel callDurationPanel;
    private javax.swing.JPanel callInitPanel;
    private javax.swing.JPanel callInitPanel1;
    private javax.swing.JLabel callLabel;
    private javax.swing.JPanel callProgressPanel;
    public javax.swing.JLabel callcenterVersionLabel;
    private javax.swing.JTextField callingACField;
    private javax.swing.JTextField callingAttemptsField;
    private javax.swing.JLabel callingInitLabel1;
    private javax.swing.JTextField callingTTField;
    private javax.swing.JTextField callingTimestampField;
    private javax.swing.JPanel callsPerHourPanel;
    private javax.swing.JLabel campaignId2Label;
    private javax.swing.JTextField campaignIdField;
    private javax.swing.JLabel campaignIdLabel;
    private javax.swing.JPanel campaignInnerPanel;
    private javax.swing.JSeparator campaignInnerSeparator;
    private javax.swing.JLabel campaignLabel;
    private javax.swing.JPanel campaignManagerPanel;
    private javax.swing.JTextField campaignOrderIdField;
    private javax.swing.JPanel campaignPanel;
    private javax.swing.JProgressBar campaignProgressBar;
    private javax.swing.JLabel campaignRecords;
    private javax.swing.JTextField campaignStatIdField;
    private javax.swing.JPanel campaignStatisticsInnerPanel;
    private javax.swing.JPanel campaignStatsPanel;
    private javax.swing.JLabel campaignStatsRecords;
    private javax.swing.JTabbedPane campaignTab;
    private javax.swing.JTable campaignTable;
    private javax.swing.JScrollPane campaignTableScrollPane;
    private javax.swing.JCheckBox campaignTestCheckBox;
    private javax.swing.JTextField campaignTimestampField;
    private javax.swing.JLabel campaignTimestampLabel;
    private javax.swing.JLabel cancelTotalLabel;
    private javax.swing.JLabel canceledLabel;
    private javax.swing.JTable captionTable;
    private javax.swing.JButton checkVersionButton;
    private javax.swing.JButton clearCustomerFieldsButton;
    private javax.swing.JButton clearResellerFieldsButton;
    private javax.swing.JTextField clientIPField;
    private javax.swing.JLabel clientIPLabel;
    private javax.swing.JTextField clientPortField;
    private javax.swing.JLabel clientPortLabel;
    private javax.swing.JPanel colorMaskPanel;
    private javax.swing.JLabel companyAddressLabel;
    private javax.swing.JLabel companyAddressNrLabel;
    private javax.swing.JLabel companyCityLabel;
    private javax.swing.JLabel companyCountryLabel;
    private javax.swing.JLabel companyIdLabel;
    private javax.swing.JPanel companyInformationPanel;
    private javax.swing.JLabel companyNameLabel;
    private javax.swing.JLabel companyPostcodeLabel;
    private javax.swing.JLabel conLabel;
    private javax.swing.JLabel concurrentInitLabel;
    private javax.swing.JLabel concurrentLabel;
    private javax.swing.JPanel configTab;
    javax.swing.JButton confirmOrderButton;
    private javax.swing.JTextField connectACField;
    private javax.swing.JLabel connectInitLabel;
    private javax.swing.JTextField connectTTField;
    private javax.swing.JTextField connectingTimestampField;
    private javax.swing.JLabel contactEmailLabel;
    private javax.swing.JPanel contactInformationPanel;
    private javax.swing.JLabel contactMobileLabel;
    private javax.swing.JLabel contactNameLabel;
    private javax.swing.JLabel contactPhoneLabel;
    private javax.swing.JPanel controlPanel;
    private javax.swing.JPanel controlsPanel;
    private javax.swing.JTextArea copyrightLabel;
    private javax.swing.JTextField customerAddressField;
    private javax.swing.JTextField customerAddressNrField;
    private javax.swing.JTextField customerCityField;
    private javax.swing.JTextField customerCompanyNameField;
    private javax.swing.JTextField customerContactNameField;
    private javax.swing.JTextField customerCountryField;
    private javax.swing.JTextField customerDateField;
    private javax.swing.JTextField customerDiscountField;
    private javax.swing.JLabel customerDiscountLabel;
    private javax.swing.JTextField customerEmailField;
    private javax.swing.JTextField customerIdField;
    private javax.swing.JLabel customerLabel;
    private javax.swing.JPanel customerManagerPanel;
    private javax.swing.JTextField customerMobileNrField;
    private javax.swing.JTextField customerPhoneNrField;
    private javax.swing.JTextField customerPostcodeField;
    private javax.swing.JLabel customerRecords;
    private javax.swing.JTable customerTable;
    private javax.swing.JLabel customerTableLabel;
    private javax.swing.JLabel customerTableLabel1;
    private javax.swing.JLabel customerTableLabel10;
    private javax.swing.JLabel customerTableLabel2;
    private javax.swing.JLabel customerTableLabel3;
    private javax.swing.JLabel customerTableLabel4;
    private javax.swing.JLabel customerTableLabel5;
    private javax.swing.JLabel customerTableLabel6;
    private javax.swing.JLabel customerTableLabel7;
    private javax.swing.JLabel customerTableLabel8;
    private javax.swing.JScrollPane customerTableScrollPane;
    private javax.swing.JPanel dashboardTab;
    private javax.swing.JLabel daytimeLabel;
    private javax.swing.JLabel daytimeLabel1;
    private javax.swing.JLabel daytimeLabel2;
    private javax.swing.JLabel daytimeLabel3;
    private javax.swing.JLabel daytimeLabel4;
    private javax.swing.JLabel daytimeLabel5;
    private javax.swing.JLabel daytimeLabel6;
    private javax.swing.JPanel dbPanel;
    private javax.swing.JButton deleteCampaignButton;
    private javax.swing.JButton deleteCampaignStatsButton;
    private javax.swing.JButton deleteCustomerButton;
    private javax.swing.JButton deleteDestinationButton;
    private javax.swing.JButton deleteInvoiceButton;
    private javax.swing.JButton deleteOrderButton;
    private javax.swing.JButton deletePricelistButton;
    private javax.swing.JButton deleteResellerButton;
    private javax.swing.JTextField destCountField;
    private javax.swing.JTextField destIdField;
    private javax.swing.JLabel destinationCountLabel;
    private javax.swing.JTextField destinationField;
    private javax.swing.JLabel destinationRecords;
    private javax.swing.JScrollPane destinationScrollPane;
    public javax.swing.JTextArea destinationTextArea;
    private javax.swing.JPanel destinationTimestampPanel;
    private javax.swing.JPanel destinationsPanel;
    private javax.swing.JPanel detailsPanel;
    private javax.swing.JPanel displayPanel;
    private javax.swing.JTextField domainField;
    private javax.swing.JLabel domainLabel;
    private javax.swing.JButton dropAllTablesButton;
    private javax.swing.JButton dropCampaignStatsTableButton;
    private javax.swing.JButton dropCampaignTableButton;
    private javax.swing.JButton dropCustomerTableButton;
    private javax.swing.JButton dropDestinationTableButton;
    private javax.swing.JButton dropInvoiceTableButton;
    private javax.swing.JButton dropOrderTableButton;
    private javax.swing.JButton dropPricelistTableButton;
    private javax.swing.JButton dropResellerTableButton;
    private javax.swing.JLabel endedLabel;
    private javax.swing.JLabel ephoneLabel;
    public javax.swing.JLabel ephoneVersionLabel;
    private javax.swing.JLabel euroSignLabel;
    private javax.swing.JLabel euroSignLabel1;
    private javax.swing.JLabel euroSignLabel2;
    private javax.swing.JLabel euroSignLabel3;
    private javax.swing.JLabel euroSignLabel4;
    private javax.swing.JLabel euroSignLabel5;
    private javax.swing.JLabel eveningLabel;
    public javax.swing.JButton exampleCustomerButton;
    private javax.swing.JButton exampleResellerButton;
    private javax.swing.JLabel freeLecectLabel;
    private javax.swing.JLabel headerLabel;
    private javax.swing.JLabel headerLabel1;
    private javax.swing.JCheckBox iconsCheckBox;
    private javax.swing.JLabel iconsLabel;
    private javax.swing.JTextField idField;
    private javax.swing.JLabel idLabel;
    private javax.swing.JTextField idleACField;
    private javax.swing.JLabel idleACLabel;
    private javax.swing.JPanel inboundButtonPanel;
    private javax.swing.JButton inboundCallCenterButton;
    private javax.swing.JScrollPane infoScrollPane;
    private javax.swing.JButton insertCampaignButton;
    private javax.swing.JButton insertCampaignStatsButton;
    private javax.swing.JButton insertCustomerButton;
    private javax.swing.JButton insertDestinationButton;
    private javax.swing.JButton insertInvoiceButton;
    private javax.swing.JButton insertPricelistButton;
    private javax.swing.JButton insertResellerButton;
    private javax.swing.JTextField invoiceCustomerDiscountField;
    private javax.swing.JLabel invoiceCustomerDiscountLabel;
    private javax.swing.JTextField invoiceDateField;
    private javax.swing.JLabel invoiceDateLabel;
    private javax.swing.JTextField invoiceIdField;
    private javax.swing.JLabel invoiceIdLabel;
    private javax.swing.JPanel invoiceInnerPanel;
    private javax.swing.JTextField invoiceItemDescField;
    private javax.swing.JLabel invoiceItemQTYPriceLabel;
    private javax.swing.JTextField invoiceItemQuantityPriceField;
    private javax.swing.JTextField invoiceItemUnitPriceField;
    private javax.swing.JLabel invoiceItemUnitPriceLabel;
    private javax.swing.JTextField invoiceItemVATPercentageField;
    private javax.swing.JLabel invoiceItemVATPercentageLabel;
    private javax.swing.JTextField invoiceOrderIdField;
    private javax.swing.JTextField invoicePaidField;
    private javax.swing.JLabel invoicePaidLabel;
    private javax.swing.JPanel invoicePanel;
    private javax.swing.JLabel invoiceQTYLabel;
    private javax.swing.JTextField invoiceQuantityField;
    private javax.swing.JLabel invoiceRecords;
    private javax.swing.JTextField invoiceSubTotalB4DiscountField;
    private javax.swing.JLabel invoiceSubTotalB4DiscountLabel;
    private javax.swing.JTextField invoiceSubTotalField;
    private javax.swing.JLabel invoiceSubTotalLabel;
    private javax.swing.JTextField invoiceTotalField;
    private javax.swing.JLabel invoiceTotalLabel;
    private javax.swing.JPanel invoiceTotalsPanel;
    private javax.swing.JTextField invoiceVATField;
    private javax.swing.JLabel invoiceVATLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JRadioButton jRadioButtonWindows;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField javaOptionsField;
    private javax.swing.JLabel javaOptionsLabel;
    private javax.swing.JPanel licenseCodePanel;
    private javax.swing.JPanel licenseDatePanel;
    private javax.swing.JPanel licenseDetailsPanel;
    private javax.swing.JScrollPane licenseDetailsScrollPane;
    private javax.swing.JPanel licensePeriodPanel;
    private javax.swing.JScrollPane licensePeriodScrollPane;
    private javax.swing.JPanel licenseTab;
    private javax.swing.JPanel licenseTypePanel;
    private javax.swing.JLabel local2Label;
    private javax.swing.JTextField localBusyTTField;
    private javax.swing.JTextField localBusyTimestampField;
    private javax.swing.JTextField localByeTTField;
    private javax.swing.JTextField localByeTimestampField;
    private javax.swing.JTextField localCancelTTField;
    private javax.swing.JTextField localCancelingTimestampField;
    private javax.swing.JLabel localLabel;
    private javax.swing.JPanel logTab;
    private javax.swing.ButtonGroup lookAndFeelGroup;
    private javax.swing.JPanel lookAndFeelPanel;
    private javax.swing.JRadioButton lookAndFeelRButtonGTK;
    private javax.swing.JRadioButton lookAndFeelRButtonMotif;
    private javax.swing.JRadioButton lookAndFeelRButtonNimbus;
    private javax.swing.JPanel maintenancePanel;
    private javax.swing.JPanel maintenancePanel1;
    private javax.swing.JCheckBox managedCheckBox;
    private javax.swing.JLabel managedLabel;
    private javax.swing.JLabel managerLabel;
    public javax.swing.JLabel managerVersionLabel;
    private javax.swing.JToggleButton netManagerInboundClientToggleButton;
    private javax.swing.JToggleButton netManagerOutboundClientToggleButton;
    private javax.swing.JButton nextCampaignButton;
    private javax.swing.JButton nextCustomerButton;
    private javax.swing.JButton nextDestinationButton;
    private javax.swing.JButton nextInvoiceButton;
    private javax.swing.JLabel officeAddressLabel;
    private javax.swing.JLabel officeAddressNrLabel;
    private javax.swing.JLabel officeCityLabel;
    private javax.swing.JLabel officeCountryLabel;
    private javax.swing.JLabel officeIdLabel;
    private javax.swing.JPanel officeInformationPanel;
    private javax.swing.JLabel officeNameLabel;
    private javax.swing.JLabel officePostcodeLabel;
    private javax.swing.JLabel oidLabel;
    private javax.swing.JTextField onACField;
    private javax.swing.JLabel onACLabel;
    private javax.swing.JComboBox orderCustomerIdComboBox;
    javax.swing.JTextField orderDateField;
    public javax.swing.JTextField orderDestinationsQuantityField;
    private javax.swing.JLabel orderDestinationsQuantityLabel;
    private javax.swing.JTextField orderFilenameField;
    private javax.swing.JComboBox orderIdComboBox;
    private javax.swing.JLabel orderIdLabel;
    private javax.swing.JPanel orderInner2PanelPanel;
    private javax.swing.JPanel orderInnerPanel;
    private javax.swing.JLabel orderLabel;
    private javax.swing.JTextField orderMessageDurationField;
    private javax.swing.JLabel orderMessageDurationLabel;
    private javax.swing.JTextField orderMessageRateField;
    private javax.swing.JTextField orderMessageRatePerSecondField;
    private javax.swing.JLabel orderMessageRatesLabel;
    private javax.swing.JLabel orderMessageRatesLabel1;
    private javax.swing.JLabel orderRecords;
    private javax.swing.JScrollPane orderStatsScrollPane;
    private javax.swing.JTextField orderSubTotalField;
    private javax.swing.JLabel orderSubTotalLabel;
    private javax.swing.JTable orderTable;
    private javax.swing.JPanel outboundButtonPanel;
    private javax.swing.JButton outboundCallCenterButton;
    private javax.swing.JLabel pfixLabel;
    private javax.swing.JPanel prefPhoneLinesPanel;
    private javax.swing.JSlider prefPhoneLinesSlider;
    private javax.swing.JTextField prefixField;
    private javax.swing.JButton previousCampaignButton;
    private javax.swing.JButton previousCustomerButton;
    private javax.swing.JButton previousDestinationButton;
    private javax.swing.JButton previousInvoiceButton;
    private javax.swing.JTextField pricelistA2SDaytimePerSecondField;
    private javax.swing.JTextField pricelistA2SEveningPerSecondField;
    private javax.swing.JTextField pricelistB2BDaytimePerSecondField;
    private javax.swing.JTextField pricelistB2BEveningPerSecondField;
    private javax.swing.JTextField pricelistB2CDaytimePerSecondField;
    private javax.swing.JTextField pricelistB2CEveningPerSecondField;
    private javax.swing.JPanel pricelistInnerPanel;
    private javax.swing.JPanel pricelistPanel;
    private javax.swing.JLabel pricelistRecords;
    private javax.swing.JTextArea productDescriptionLabel;
    private javax.swing.JLabel productLabel;
    private javax.swing.JLabel progressAcceptingLabel;
    private javax.swing.JLabel progressRingingLabel;
    private javax.swing.JLabel progressTalkingLabel;
    private javax.swing.JTextField pubIPField;
    private javax.swing.JLabel pubIPLabel;
    private javax.swing.JList recipientsList;
    private javax.swing.JScrollPane recipientsScrollPane;
    private javax.swing.JCheckBox registerCheckBox;
    private javax.swing.JLabel registerLabel;
    private javax.swing.JLabel remote2Label;
    private javax.swing.JTextField remoteBusyTTField;
    private javax.swing.JTextField remoteBusyTimestampField;
    private javax.swing.JTextField remoteByeTTField;
    private javax.swing.JTextField remoteByeTimestampField;
    private javax.swing.JTextField remoteCancelTTField;
    private javax.swing.JTextField remoteCancelingTimestampField;
    private javax.swing.JLabel remoteLabel;
    private javax.swing.JButton requestVergunningButton;
    private javax.swing.JTextField resellerAddressField;
    private javax.swing.JTextField resellerAddressNrField;
    private javax.swing.JTextField resellerCityField;
    private javax.swing.JTextField resellerCompanyNameField;
    private javax.swing.JTextField resellerContactNameField;
    private javax.swing.JTextField resellerCountryField;
    private javax.swing.JTextField resellerDateField;
    private javax.swing.JTextField resellerDiscountField;
    private javax.swing.JLabel resellerDiscountLabel;
    private javax.swing.JTextField resellerEmailField;
    private javax.swing.JLabel resellerEmailLabel;
    private javax.swing.JTextField resellerIdField;
    private javax.swing.JPanel resellerInformationPanel;
    private javax.swing.JLabel resellerMobileLabel;
    private javax.swing.JTextField resellerMobileNrField;
    private javax.swing.JLabel resellerNameLabel;
    private javax.swing.JPanel resellerPanel;
    private javax.swing.JLabel resellerPhoneLabel;
    private javax.swing.JTextField resellerPhoneNrField;
    private javax.swing.JTextField resellerPostcodeField;
    private javax.swing.JLabel resellerRecords;
    private javax.swing.JButton resizeWindowButton;
    private javax.swing.JTextField responseStatusCodeField;
    private javax.swing.JLabel responseStatusCodeLabelLabel;
    private javax.swing.JTextField responseStatusDescField;
    private javax.swing.JLabel ringLabel;
    private javax.swing.JTextField ringingACField;
    private javax.swing.JTextField ringingTTField;
    private javax.swing.JTextField ringingTimestampField;
    private javax.swing.JButton saveConfigurationButton;
    private javax.swing.JButton scheduleButton;
    private javax.swing.JButton searchCustomerButton;
    private javax.swing.JButton searchResellerButton;
    private javax.swing.JLabel secretLabel;
    private javax.swing.JButton selectCampaignButton;
    private javax.swing.JButton selectCampaignStatsButton;
    private javax.swing.JButton selectCustomerButton;
    private javax.swing.JButton selectDestinationButton;
    private javax.swing.JButton selectInvoiceButton;
    private javax.swing.JButton selectOrderButton;
    private javax.swing.JButton selectPricelistButton;
    private javax.swing.JButton selectResellerButton;
    private javax.swing.JTextField serverIPField;
    private javax.swing.JLabel serverIPLabel;
    private javax.swing.JTextField serverPortField;
    private javax.swing.JLabel serverPortLabel;
    private javax.swing.JProgressBar serviceLoopProgressBar;
    private javax.swing.JPanel sizeControlPanel;
    protected javax.swing.JCheckBox smoothCheckBox;
    private javax.swing.JLabel smoothLabel;
    private javax.swing.JTextPane statusBarInbound;
    private javax.swing.JTextPane statusBarOutbound;
    private javax.swing.JButton subscribeToVOIPProviderButton;
    private javax.swing.JTextField suffixField;
    private javax.swing.JLabel suffixLabel;
    private javax.swing.JPanel summaryDisplayPanel;
    private javax.swing.JScrollPane sysPropsScrollPane;
    private javax.swing.JTable sysPropsTable;
    private javax.swing.JPanel systemPropertiesPanel;
    private javax.swing.JTabbedPane tabPane;
    private javax.swing.JLabel talkLabel;
    private javax.swing.JTextField talkingACField;
    private javax.swing.JTextField talkingTTField;
    private javax.swing.JTextField talkingTimestampField;
    private javax.swing.JLabel telLabel;
    private javax.swing.JLabel testLabel;
    private javax.swing.JButton testPhoneButton;
    private javax.swing.JTextArea textLogArea;
    private javax.swing.JLabel timeEndLabel;
    private javax.swing.JTextField timeExpectedEndField;
    private javax.swing.JLabel timeExpectedLabel;
    private javax.swing.JTextField timeExpectedStartField;
    private javax.swing.JPanel timeOfCompletionPanel;
    private javax.swing.JTextField timeRegisteredEndField;
    private javax.swing.JLabel timeRegisteredLabel;
    private javax.swing.JTextField timeRegisteredStartField;
    private javax.swing.JTextField timeScheduledEndField;
    private javax.swing.JLabel timeScheduledLabel;
    private javax.swing.JTextField timeScheduledStartField;
    private javax.swing.JLabel timeStartLabel;
    private javax.swing.JTextField timeTextField;
    private javax.swing.JList timewindowList;
    private javax.swing.JScrollPane timewindowScrollPane;
    private javax.swing.JPasswordField toegangField;
    private javax.swing.JPanel toolsInnerPanel;
    private javax.swing.JSeparator toolsSeparator;
    private javax.swing.JPanel toolsTab;
    private javax.swing.JLabel totalCountInitLabel;
    private javax.swing.JLabel totalCountLabel;
    private javax.swing.JLabel tryLabel;
    private javax.swing.JTextField tryingACField;
    private javax.swing.JLabel tryingInitLabel;
    private javax.swing.JTextField tryingTTField;
    private javax.swing.JTextField tryingTimestampField;
    private javax.swing.JButton updateCampaignButton;
    private javax.swing.JButton updateCampaignStatsButton;
    private javax.swing.JButton updateCustomerButton;
    private javax.swing.JButton updateDestinationButton;
    private javax.swing.JButton updateInvoiceButton;
    private javax.swing.JButton updateOrderButton;
    private javax.swing.JButton updatePricelistButton;
    private javax.swing.JButton updateResellerButton;
    private javax.swing.JPanel updateTab;
    private javax.swing.JSeparator updateVersonSeparator;
    private javax.swing.JTextField usernameField;
    private javax.swing.JLabel usersecretLabel;
    private javax.swing.JTextField vergunningCodeField;
    public datechooser.beans.DateChooserPanel vergunningDateChooserPanel;
    private javax.swing.JTable vergunningDetailsTable;
    private javax.swing.JList vergunningPeriodList;
    private javax.swing.JList vergunningTypeList;
    private javax.swing.JLabel versionLabel;
    // End of variables declaration//GEN-END:variables

    @Override public void resetLog() { textLogArea.setText(""); }
    @Override public void phoneDisplay(DisplayData displayParam) { throw new UnsupportedOperationException("Not supported yet."); }
    @Override public void speaker(SpeakerData speakerParam) { throw new UnsupportedOperationException("Not supported yet."); }

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
    public void sipstateUpdate(int sipstateParam, int lastsipstateParam, int loginstateParam, int softphoneActivityParam, int softPhoneInstanceIdParam, Destination destinationParam) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     *
     * @param responseCodeParam
     * @param responseReasonPhraseParam
     * @param softPhoneInstanceIdParam
     * @param destinationParam
     */
    @Override
    public void responseUpdate(int responseCodeParam, String responseReasonPhraseParam, int softPhoneInstanceIdParam, Destination destinationParam) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     *
     */
    public void requestStop() { stopRequested = true;}

    /**
     *
     * @param args
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run()
            {
                new Manager().setVisible(true);
            }
        });
    }

    /**
     *
     * @param messageParam
     * @param logToApplicationParam
     * @param logToFileParam
     */
    @Override
    synchronized public void showStatus(String messageParam, boolean logToApplicationParam, boolean logToFileParam) {
        statusBarOutbound.setText(messageParam);
        if (logToApplicationParam) {logToApplication(messageParam);}
//        if (logToFileParam) {logToFile(messageParam);}
    }

    /**
     *
     * @param messageParam
     * @param logToApplicationParam
     * @param logToFileParam
     */
    synchronized public void showStatusInbound(String messageParam, boolean logToApplicationParam, boolean logToFileParam) {
        statusBarInbound.setText(messageParam);
        if (logToApplicationParam) {logToApplication(messageParam);}
//        if (logToFileParam) {logToFile(messageParam);}
    }

    /**
     *
     * @param messageParam
     * @param logToApplicationParam
     * @param logToFileParam
     */
    synchronized public void showStatusOutbound(String messageParam, boolean logToApplicationParam, boolean logToFileParam) {
        statusBarOutbound.setText(messageParam);
        if (logToApplicationParam) {logToApplication(messageParam);}
//        if (logToFileParam) {logToFile(messageParam);}
    }

    /**
     *
     * @param displaymessage
     */
    @Override
    synchronized public void logToApplication(final String displaymessage)
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
        
        Thread webLogThread = new Thread(new Runnable()
        {
            @Override
            @SuppressWarnings({"static-access"})
            public void run()
            {
                try { weblog.send(THISPRODUCT + " " + displaymessage); } catch (Exception ex) { }
            }
        });
        webLogThread.setName("webLogThread");
        webLogThread.setDaemon(runThreadsAsDaemons);
        webLogThread.start();
    }

    /**
     *
     * @param displaymessage
     */
    @Override
    public synchronized void logToFile(final String displaymessage)
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

                try { logFileWriter = new FileWriter(logFileString, true ); } catch (IOException ex) { showStatus("Error: IOException: new FileWriter(" + logFileString + ")" + ex.getMessage(), false, false); logBuffer += humanDate + " " + displaymessage + lineTerminator; return; }
                try { logFileWriter.flush(); } catch (IOException ex) { showStatus("Error: IOException: logFileWriter.flush()1;", false, false); logBuffer += humanDate + " " + displaymessage + lineTerminator; return; }
                try { logFileWriter.write(logBuffer + humanDate + " " + displaymessage + lineTerminator); } catch (IOException ex) { showStatus("Error: IOException: logFileWriter.write()", false, false); logBuffer += humanDate + " " + displaymessage + lineTerminator; return; }
                try { logFileWriter.flush(); } catch (IOException ex) { showStatus("Error: IOException: logFileWriter.flush()2;", false, false); logBuffer += humanDate + " " + displaymessage + lineTerminator; return; }

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
    public void feedback(String messageParam, int valueParam)
    {
        if (messageParam.equals("db_server_starting"))   { moveCallsPerHourMeter(100, smoothCheckBox.isSelected()); } else if (messageParam.equals("db_server_started"))    { moveCallsPerHourMeter(0, smoothCheckBox.isSelected()); }
        if (messageParam.equals("db_client_connecting")) { moveBusyRatioMeter(50, smoothCheckBox.isSelected()); }     else if (messageParam.equals("db_client_connected"))  { moveBusyRatioMeter(0, smoothCheckBox.isSelected()); }
        if (messageParam.equals("db_tables_checking"))   { moveAnswerDelayMeter(30, smoothCheckBox.isSelected()); }   else if (messageParam.equals("db_tables_checked"))    { moveAnswerDelayMeter(0, smoothCheckBox.isSelected()); }
        if (messageParam.equals("vergunning_valideren")) { moveCallDurationMeter(30, smoothCheckBox.isSelected()); }  else if (messageParam.equals("vergunning_goed"))      { moveCallDurationMeter(0, smoothCheckBox.isSelected()); }
    }
}
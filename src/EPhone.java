import datasets.SpeakerData;
import datasets.DisplayData;
import datasets.Configuration;
import datasets.Destination;
import java.awt.Color;
import java.util.Calendar;
import java.util.Locale;
import javax.swing.SwingConstants;
import javax.swing.BorderFactory;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.io.File;

/**
 *
 * @author ron
 */
public class EPhone extends javax.swing.JFrame implements UserInterface
{

    SoftPhone softPhone1 = null;

    /**
     *
     */
    public boolean  managedByCallCenter = false;
    Thread[] softPhoneThreadArray;

    int inviteTimeout = 30;

//    private static final String BRAND		    = "VoipStorm";
//    private static final String BUSINESS	    = "Telemarketing";
//    private static final String BRAND_DESCRIPTION   = BRAND + " offers 21st Century TeleMarketing Software. Select a Soundfile, Copy & Paste your Phonenumbers and start your Campaign. TeleMarketing has never been so Easy, Fast and Costeffective. Our Software only Costs a Fraction of Traditional Telemarketing, Radio and TV Advertisement !";
//    private static final String PRODUCT		    = "ECallCenter21";
//    private static final String PRODUCT_DESCRIPTION = PRODUCT + " is the driving force behind " + BRAND + ". " + PRODUCT + " can make " + License.CALLSPERHOUR_ENTERPRISE + " Phonecalls per Hour, calling approxiamtely a Quarter of a Million people and let them listen to your soundfile in just 16 Hours ! (or 50 million phonecalls a year !)";
//    private static final String COPYRIGHT	    = "© " + Calendar.getInstance().get(Calendar.YEAR);
//    private static final String AUTHOR		    = "Ron de Jong";

    private static final String THISPRODUCT         = "EPhone";
    private static final String VERSION		    = "v1.0";

    private static final int PLAF_GTK               = 0;
    private static final int PLAF_MOTIF             = 1;
    private static final int PLAF_NIMBUS            = 2;
    private static final int PLAF_WINDOWS           = 3;

    private boolean debugging;

    /**
     *
     */
    public Calendar logCalendar;
    private Locale nlLocale;
    private boolean externalSoftPhoneInstance;
    boolean runThreadsAsDaemons;
    private Configuration myConfiguration;
    private String displayOutput = "";
    private DisplayData displayData;
    private SpeakerData speakerData;

//    private SoundTool clickOnTonesTool;
//    private SoundTool clickOffTonesTool;
//    private SoundTool successTonesTool;
//    private SoundTool powerSuccessTonesTool;
//    private SoundTool failureTonesTool;
//    private SoundTool tickTonesTool;
//    private SoundTool registerEnabledTonesTool;
//    private SoundTool registerDisabledTonesTool;
//    private SoundTool answerEnabledTonesTool;
//    private SoundTool answerDisabledTonesTool;
//    private SoundTool cancelEnabledTonesTool;
//    private SoundTool cancelDisabledTonesTool;
//    private SoundTool muteEnabledTonesTool;
//    private SoundTool muteDisabledTonesTool;
//
//    private SoundTool ringToneTonesTool;
//    private SoundTool dialToneTonesTool;
//    private SoundTool callToneTonesTool;
//    private SoundTool busyToneTonesTool;
//    private SoundTool deadToneTonesTool;
//    private SoundTool errorToneTonesTool;

    private AudioTool audioTool;
    private boolean audioToolWanted;

    private Destination destination;
    private String [] plaf;
    private String plafSelected;

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

    private File file;
    private String dataDir;
    private String soundsDir;
    private String vergunningDir;
    private String databasesDir;
    private String configDir;
    private String binDir;
    private String logDir;
    private String fileSeparator;
    private String lineTerminator;
    private String platform;

    // Constructor Creates new form SoftPhoneGUI */

    /**
     *
     */
        @SuppressWarnings("static-access")
    public EPhone()
    {
        String[] status = new String[2];
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

        plaf = new String[4];
        plafSelected = new String();
        plaf[0] = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
        plaf[1] = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
        plaf[2] = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
        plaf[3] = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
        setLookAndFeel(PLAF_NIMBUS);

        initComponents();

        displayPanel.setToolTipText(getCopyright() + " " + getBrand() + " " + getBusiness() + " - Author: " + getAuthor());
	setTitle(getWindowTitle());
//        configTabPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, SoftPhone.getCopyright() + " " + SoftPhone.getAuthor() + " " + SoftPhone.getVersion(), javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("STHeiti", 0, 12), new java.awt.Color(255, 255, 255))); // NOI18N
        status = new String[2];
        debugging = false;
	runThreadsAsDaemons = true;

        if (audioToolWanted) { audioTool              = new AudioTool(); }

        displayData            = new DisplayData();
        speakerData            = new SpeakerData();

        destination = new Destination();

//	externalSoftPhoneInstance = true;
        //mySoftPhone1 = new SoftPhone(this, debugging);

        disableConfigurationUsage();
        disableTelephoneUsage();
        nlLocale = new Locale("nl");
//        showStatus("Power Off", true);
    }

    /**
     *
     * @param softPhoneInstanceReferenceParam
     */
    @SuppressWarnings("static-access")
    public EPhone(SoftPhone softPhoneInstanceReferenceParam)
    {
	this(); // Invoke Default Constructor
	externalSoftPhoneInstance = true;
        managedByCallCenter = true;

        softPhone1 = softPhoneInstanceReferenceParam;
	clientIPField.setText(softPhone1.configuration.getClientIP());
	pubIPField.setText(softPhone1.configuration.getPublicIP());
	clientPortField.setText(softPhone1.configuration.getServerPort());
	serverIPField.setText(softPhone1.configuration.getServerIP());
	serverPortField.setText(softPhone1.configuration.getServerPort());
	domainField.setText(softPhone1.configuration.getDomain());
        usernameField.setText(softPhone1.configuration.getUsername());
        toegangField.setText(softPhone1.configuration.getToegang());

        if ( softPhone1.getLoginState() == softPhone1.LOGINSTATE_REGISTERED ) {registerCheckBox.setSelected(true);} else {registerCheckBox.setSelected(false);}

//        clickOnTonesTool.play();
        if (audioToolWanted) { audioTool.play(audioTool.getClickontoneClip()); }

	enableConfigurationUsage();
	enableTelephoneUsage();
	line1ToggleButton.setSelected(true);
//	line1ToggleButton.setEnabled(false);
	line2ToggleButton.setEnabled(false);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    }

   @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lookAndFeelGroup = new javax.swing.ButtonGroup();
        colorMaskPanel = new javax.swing.JPanel();
        telephonePanel = new javax.swing.JPanel();
        line2ToggleButton = new javax.swing.JToggleButton();
        registerToggleButton = new javax.swing.JToggleButton();
        autoAnswerToggleButton = new javax.swing.JToggleButton();
        muteAudioToggleButton = new javax.swing.JToggleButton();
        destinationField = new javax.swing.JTextField();
        dialPanel = new javax.swing.JPanel();
        callButton = new javax.swing.JButton();
        endButton = new javax.swing.JButton();
        button0 = new javax.swing.JButton();
        button1 = new javax.swing.JButton();
        button2 = new javax.swing.JButton();
        button3 = new javax.swing.JButton();
        button4 = new javax.swing.JButton();
        button5 = new javax.swing.JButton();
        button6 = new javax.swing.JButton();
        button7 = new javax.swing.JButton();
        button8 = new javax.swing.JButton();
        button9 = new javax.swing.JButton();
        buttonHash = new javax.swing.JButton();
        buttonStar = new javax.swing.JButton();
        filenameField = new javax.swing.JTextField();
        poolButton = new javax.swing.JButton();
        line1ToggleButton = new javax.swing.JToggleButton();
        autoCancelToggleButton = new javax.swing.JToggleButton();
        randomRingResponseToggleButton = new javax.swing.JToggleButton();
        tabPanel = new javax.swing.JTabbedPane();
        displayTabPanel = new javax.swing.JPanel();
        displayPanel = new javax.swing.JPanel();
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
        cancelPanel = new javax.swing.JPanel();
        cancelLabel = new javax.swing.JLabel();
        mutePanel = new javax.swing.JPanel();
        muteLabel = new javax.swing.JLabel();
        configTabPanel = new javax.swing.JPanel();
        authenticationPanel = new javax.swing.JPanel();
        clientIPLabel = new javax.swing.JLabel();
        clientIPField = new javax.swing.JTextField();
        pubIPField = new javax.swing.JTextField();
        pubIPLabel = new javax.swing.JLabel();
        clientPortLabel = new javax.swing.JLabel();
        clientPortField = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        domainLabel = new javax.swing.JLabel();
        domainField = new javax.swing.JTextField();
        serverIPLabel = new javax.swing.JLabel();
        serverIPField = new javax.swing.JTextField();
        serverPortLabel = new javax.swing.JLabel();
        serverPortField = new javax.swing.JTextField();
        usernameLabel = new javax.swing.JLabel();
        usernameField = new javax.swing.JTextField();
        toegangLabel = new javax.swing.JLabel();
        toegangField = new javax.swing.JPasswordField();
        registerLabel = new javax.swing.JLabel();
        registerCheckBox = new javax.swing.JCheckBox();
        saveProxyConfigButton = new javax.swing.JButton();
        lookAndFeelRButtonGTK = new javax.swing.JRadioButton();
        lookAndFeelRButtonMotif = new javax.swing.JRadioButton();
        lookAndFeelRButtonNimbus = new javax.swing.JRadioButton();
        lookAndFeelRButtonWindows = new javax.swing.JRadioButton();
        debugCheckBox = new javax.swing.JCheckBox();
        debugLabel = new javax.swing.JLabel();
        logTabPanel = new javax.swing.JPanel();
        textLogAreaScrollPane = new javax.swing.JScrollPane();
        textLogArea = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(216, 216, 222));
        setFocusable(false);
        setFont(new java.awt.Font("STHeiti", 0, 10));
        setName("Phone"); // NOI18N
        setResizable(false);
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                formKeyTyped(evt);
            }
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                formKeyReleased(evt);
            }
        });

        colorMaskPanel.setToolTipText("");

        telephonePanel.setToolTipText("");

        line2ToggleButton.setFont(new java.awt.Font("STHeiti", 0, 10));
        line2ToggleButton.setText("➁");
        line2ToggleButton.setToolTipText("Power (Line2)");
        line2ToggleButton.setAlignmentY(0.0F);
        line2ToggleButton.setFocusable(false);
        line2ToggleButton.setMaximumSize(new java.awt.Dimension(100, 30));
        line2ToggleButton.setMinimumSize(new java.awt.Dimension(100, 30));
        line2ToggleButton.setPreferredSize(new java.awt.Dimension(100, 30));
        line2ToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                line2ToggleButtonActionPerformed(evt);
            }
        });

        registerToggleButton.setFont(new java.awt.Font("STHeiti", 0, 10));
        registerToggleButton.setText("Ⓡ");
        registerToggleButton.setToolTipText("Register to PBX (Switchboard)");
        registerToggleButton.setAlignmentY(0.0F);
        registerToggleButton.setFocusPainted(false);
        registerToggleButton.setFocusable(false);
        registerToggleButton.setMaximumSize(new java.awt.Dimension(100, 30));
        registerToggleButton.setMinimumSize(new java.awt.Dimension(100, 30));
        registerToggleButton.setPreferredSize(new java.awt.Dimension(100, 30));
        registerToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                registerToggleButtonActionPerformed(evt);
            }
        });

        autoAnswerToggleButton.setFont(new java.awt.Font("STHeiti", 0, 10));
        autoAnswerToggleButton.setText("Ⓐ");
        autoAnswerToggleButton.setToolTipText("Auto Answer");
        autoAnswerToggleButton.setAlignmentY(0.0F);
        autoAnswerToggleButton.setFocusable(false);
        autoAnswerToggleButton.setMaximumSize(new java.awt.Dimension(100, 30));
        autoAnswerToggleButton.setMinimumSize(new java.awt.Dimension(100, 30));
        autoAnswerToggleButton.setPreferredSize(new java.awt.Dimension(100, 30));
        autoAnswerToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoAnswerToggleButtonActionPerformed(evt);
            }
        });

        muteAudioToggleButton.setFont(new java.awt.Font("STHeiti", 0, 10));
        muteAudioToggleButton.setText("Ⓜ");
        muteAudioToggleButton.setToolTipText("Mute Audio");
        muteAudioToggleButton.setAlignmentY(0.0F);
        muteAudioToggleButton.setFocusable(false);
        muteAudioToggleButton.setMaximumSize(new java.awt.Dimension(100, 30));
        muteAudioToggleButton.setMinimumSize(new java.awt.Dimension(100, 30));
        muteAudioToggleButton.setPreferredSize(new java.awt.Dimension(100, 30));
        muteAudioToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                muteAudioToggleButtonActionPerformed(evt);
            }
        });

        destinationField.setBackground(new java.awt.Color(238, 238, 238));
        destinationField.setFont(new java.awt.Font("STHeiti", 0, 18));
        destinationField.setForeground(new java.awt.Color(102, 102, 102));
        destinationField.setToolTipText("Phonenumber / Destination");
        destinationField.setNextFocusableComponent(callButton);
        destinationField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                destinationFieldKeyReleased(evt);
            }
        });

        dialPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("STHeiti", 0, 12), new java.awt.Color(255, 255, 255))); // NOI18N
        dialPanel.setToolTipText("");

        callButton.setFont(new java.awt.Font("STHeiti", 0, 12));
        callButton.setForeground(new java.awt.Color(51, 204, 0));
        callButton.setText("Call");
        callButton.setToolTipText("");
        callButton.setFocusPainted(false);
        callButton.setNextFocusableComponent(endButton);
        callButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                callButtonActionPerformed(evt);
            }
        });

        endButton.setFont(new java.awt.Font("STHeiti", 0, 12));
        endButton.setForeground(new java.awt.Color(255, 0, 0));
        endButton.setText("End");
        endButton.setToolTipText("");
        endButton.setFocusPainted(false);
        endButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                endButtonActionPerformed(evt);
            }
        });

        button0.setFont(new java.awt.Font("STHeiti", 0, 14));
        button0.setText("0");
        button0.setFocusPainted(false);
        button0.setFocusable(false);
        button0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button0ActionPerformed(evt);
            }
        });

        button1.setFont(new java.awt.Font("STHeiti", 0, 14));
        button1.setText("1");
        button1.setFocusPainted(false);
        button1.setFocusable(false);
        button1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button1ActionPerformed(evt);
            }
        });

        button2.setFont(new java.awt.Font("STHeiti", 0, 14));
        button2.setText("2");
        button2.setFocusPainted(false);
        button2.setFocusable(false);
        button2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button2ActionPerformed(evt);
            }
        });

        button3.setFont(new java.awt.Font("STHeiti", 0, 14));
        button3.setText("3");
        button3.setFocusPainted(false);
        button3.setFocusable(false);
        button3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button3ActionPerformed(evt);
            }
        });

        button4.setFont(new java.awt.Font("STHeiti", 0, 14));
        button4.setText("4");
        button4.setFocusPainted(false);
        button4.setFocusable(false);
        button4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button4ActionPerformed(evt);
            }
        });

        button5.setFont(new java.awt.Font("STHeiti", 0, 14));
        button5.setText("5");
        button5.setFocusPainted(false);
        button5.setFocusable(false);
        button5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button5ActionPerformed(evt);
            }
        });

        button6.setFont(new java.awt.Font("STHeiti", 0, 14));
        button6.setText("6");
        button6.setFocusPainted(false);
        button6.setFocusable(false);
        button6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button6ActionPerformed(evt);
            }
        });

        button7.setFont(new java.awt.Font("STHeiti", 0, 14));
        button7.setText("7");
        button7.setFocusPainted(false);
        button7.setFocusable(false);
        button7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button7ActionPerformed(evt);
            }
        });

        button8.setFont(new java.awt.Font("STHeiti", 0, 14));
        button8.setText("8");
        button8.setFocusPainted(false);
        button8.setFocusable(false);
        button8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button8ActionPerformed(evt);
            }
        });

        button9.setFont(new java.awt.Font("STHeiti", 0, 14));
        button9.setText("9");
        button9.setFocusPainted(false);
        button9.setFocusable(false);
        button9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button9ActionPerformed(evt);
            }
        });

        buttonHash.setFont(new java.awt.Font("STHeiti", 0, 14));
        buttonHash.setText("#");
        buttonHash.setFocusPainted(false);
        buttonHash.setFocusable(false);

        buttonStar.setFont(new java.awt.Font("STHeiti", 0, 14));
        buttonStar.setText("*");
        buttonStar.setFocusPainted(false);
        buttonStar.setFocusable(false);

        filenameField.setFont(new java.awt.Font("STHeiti", 0, 12));
        filenameField.setText("voipstorm.wav");
        filenameField.setToolTipText("Soundfile (*.wav) to play during Call");

        poolButton.setFont(new java.awt.Font("STHeiti", 0, 12));
        poolButton.setText("Scan");
        poolButton.setToolTipText("Quick Call-Cancel Scan");
        poolButton.setFocusPainted(false);
        poolButton.setNextFocusableComponent(endButton);
        poolButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                poolButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout dialPanelLayout = new org.jdesktop.layout.GroupLayout(dialPanel);
        dialPanel.setLayout(dialPanelLayout);
        dialPanelLayout.setHorizontalGroup(
            dialPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, dialPanelLayout.createSequentialGroup()
                .add(84, 84, 84)
                .add(dialPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, filenameField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, dialPanelLayout.createSequentialGroup()
                        .add(dialPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(dialPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                .add(dialPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, button7, 0, 0, Short.MAX_VALUE)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, button4, 0, 0, Short.MAX_VALUE)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, button1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 62, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .add(org.jdesktop.layout.GroupLayout.LEADING, buttonStar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 62, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(callButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 62, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(dialPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(dialPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, button0, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 52, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(dialPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, button8, 0, 0, Short.MAX_VALUE)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, button5, 0, 0, Short.MAX_VALUE)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, button2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 52, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                            .add(poolButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 52, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(dialPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(buttonHash, 0, 0, Short.MAX_VALUE)
                            .add(button3, 0, 0, Short.MAX_VALUE)
                            .add(endButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 61, Short.MAX_VALUE)
                            .add(button9, 0, 0, Short.MAX_VALUE)
                            .add(button6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 58, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .add(78, 78, 78))
        );

        dialPanelLayout.linkSize(new java.awt.Component[] {button0, button1, button2, button3, button4, button5, button6, button7, button8, button9, buttonHash, buttonStar, callButton, endButton, poolButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        dialPanelLayout.setVerticalGroup(
            dialPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(dialPanelLayout.createSequentialGroup()
                .add(dialPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(dialPanelLayout.createSequentialGroup()
                        .add(callButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(button1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(button4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(button7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(buttonStar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(dialPanelLayout.createSequentialGroup()
                        .add(poolButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(button2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(button5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(button8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(button0, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(dialPanelLayout.createSequentialGroup()
                        .add(endButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(button3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(button6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(button9, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(buttonHash, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(filenameField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        dialPanelLayout.linkSize(new java.awt.Component[] {button0, button1, button2, button3, button4, button5, button6, button7, button8, button9, buttonHash, buttonStar, callButton, endButton, poolButton}, org.jdesktop.layout.GroupLayout.VERTICAL);

        line1ToggleButton.setFont(new java.awt.Font("STHeiti", 0, 10));
        line1ToggleButton.setText("➀");
        line1ToggleButton.setToolTipText("Power (Line1)");
        line1ToggleButton.setAlignmentY(0.0F);
        line1ToggleButton.setFocusable(false);
        line1ToggleButton.setMaximumSize(new java.awt.Dimension(100, 30));
        line1ToggleButton.setMinimumSize(new java.awt.Dimension(100, 30));
        line1ToggleButton.setPreferredSize(new java.awt.Dimension(100, 30));
        line1ToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                line1ToggleButtonActionPerformed(evt);
            }
        });

        autoCancelToggleButton.setFont(new java.awt.Font("STHeiti", 0, 10));
        autoCancelToggleButton.setText("Ⓒ");
        autoCancelToggleButton.setToolTipText("Auto Cancel Phonecalls");
        autoCancelToggleButton.setAlignmentY(0.0F);
        autoCancelToggleButton.setFocusable(false);
        autoCancelToggleButton.setMaximumSize(new java.awt.Dimension(100, 30));
        autoCancelToggleButton.setMinimumSize(new java.awt.Dimension(100, 30));
        autoCancelToggleButton.setPreferredSize(new java.awt.Dimension(100, 30));
        autoCancelToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoCancelToggleButtonActionPerformed(evt);
            }
        });

        randomRingResponseToggleButton.setFont(new java.awt.Font("STHeiti", 0, 10));
        randomRingResponseToggleButton.setText("Ⓢ");
        randomRingResponseToggleButton.setToolTipText("Simulate Human Behavior (Inbound)");
        randomRingResponseToggleButton.setAlignmentY(0.0F);
        randomRingResponseToggleButton.setFocusable(false);
        randomRingResponseToggleButton.setMaximumSize(new java.awt.Dimension(100, 30));
        randomRingResponseToggleButton.setMinimumSize(new java.awt.Dimension(100, 30));
        randomRingResponseToggleButton.setPreferredSize(new java.awt.Dimension(100, 30));
        randomRingResponseToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                randomRingResponseToggleButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout telephonePanelLayout = new org.jdesktop.layout.GroupLayout(telephonePanel);
        telephonePanel.setLayout(telephonePanelLayout);
        telephonePanelLayout.setHorizontalGroup(
            telephonePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(telephonePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(telephonePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, dialPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, destinationField)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, telephonePanelLayout.createSequentialGroup()
                        .add(line1ToggleButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 46, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(line2ToggleButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 46, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(registerToggleButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 46, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(autoAnswerToggleButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 46, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(randomRingResponseToggleButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 48, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(autoCancelToggleButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 46, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(muteAudioToggleButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 44, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        telephonePanelLayout.linkSize(new java.awt.Component[] {autoAnswerToggleButton, autoCancelToggleButton, line1ToggleButton, line2ToggleButton, muteAudioToggleButton, randomRingResponseToggleButton, registerToggleButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        telephonePanelLayout.setVerticalGroup(
            telephonePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(telephonePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(telephonePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(line2ToggleButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(registerToggleButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(line1ToggleButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(autoAnswerToggleButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(randomRingResponseToggleButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(autoCancelToggleButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(muteAudioToggleButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(destinationField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 31, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(dialPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(18, Short.MAX_VALUE))
        );

        telephonePanelLayout.linkSize(new java.awt.Component[] {autoAnswerToggleButton, autoCancelToggleButton, line1ToggleButton, line2ToggleButton, muteAudioToggleButton, randomRingResponseToggleButton, registerToggleButton}, org.jdesktop.layout.GroupLayout.VERTICAL);

        tabPanel.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        tabPanel.setToolTipText("");
        tabPanel.setFocusable(false);
        tabPanel.setFont(new java.awt.Font("STHeiti", 0, 13));

        displayTabPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("STHeiti", 0, 13))); // NOI18N
        displayTabPanel.setToolTipText("");

        displayPanel.setBackground(new java.awt.Color(255, 255, 255));

        softphoneInfoLabel.setFont(new java.awt.Font("STHeiti", 0, 8));
        softphoneInfoLabel.setForeground(new java.awt.Color(102, 102, 102));
        softphoneInfoLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        softphoneInfoLabel.setToolTipText("");

        proxyInfoLabel.setFont(new java.awt.Font("STHeiti", 0, 8));
        proxyInfoLabel.setForeground(new java.awt.Color(102, 102, 102));
        proxyInfoLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        proxyInfoLabel.setToolTipText("");
        proxyInfoLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        primaryStatusLabel.setFont(new java.awt.Font("STHeiti", 1, 24));
        primaryStatusLabel.setForeground(new java.awt.Color(102, 102, 102));
        primaryStatusLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        primaryStatusLabel.setToolTipText("");
        primaryStatusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        primaryStatusDetailsLabel.setFont(new java.awt.Font("STHeiti", 0, 12));
        primaryStatusDetailsLabel.setForeground(new java.awt.Color(102, 102, 102));
        primaryStatusDetailsLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        primaryStatusDetailsLabel.setToolTipText("");
        primaryStatusDetailsLabel.setHorizontalAlignment(SwingConstants.CENTER);

        secondaryStatusLabel.setFont(new java.awt.Font("STHeiti", 1, 24));
        secondaryStatusLabel.setForeground(new java.awt.Color(102, 102, 102));
        secondaryStatusLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        secondaryStatusLabel.setToolTipText("");
        secondaryStatusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        secondaryStatusDetailsLabel.setFont(new java.awt.Font("STHeiti", 0, 12));
        secondaryStatusDetailsLabel.setForeground(new java.awt.Color(102, 102, 102));
        secondaryStatusDetailsLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        secondaryStatusDetailsLabel.setToolTipText("");
        secondaryStatusDetailsLabel.setHorizontalAlignment(SwingConstants.CENTER);

        onPanel.setBackground(new java.awt.Color(255, 255, 255));
        onPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        onPanel.setToolTipText("Phowered On");

        onLabel.setFont(new java.awt.Font("STHeiti", 1, 8));
        onLabel.setForeground(new java.awt.Color(204, 204, 204));
        onLabel.setText("ON");

        org.jdesktop.layout.GroupLayout onPanelLayout = new org.jdesktop.layout.GroupLayout(onPanel);
        onPanel.setLayout(onPanelLayout);
        onPanelLayout.setHorizontalGroup(
            onPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, onPanelLayout.createSequentialGroup()
                .addContainerGap(13, Short.MAX_VALUE)
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
                .addContainerGap(14, Short.MAX_VALUE)
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
                .addContainerGap(11, Short.MAX_VALUE))
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
                .addContainerGap(8, Short.MAX_VALUE)
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
                .addContainerGap(8, Short.MAX_VALUE))
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
                .addContainerGap(10, Short.MAX_VALUE))
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
                .addContainerGap(10, Short.MAX_VALUE))
        );
        answerPanelLayout.setVerticalGroup(
            answerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(answerLabel)
        );

        cancelPanel.setBackground(new java.awt.Color(255, 255, 255));
        cancelPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        cancelPanel.setToolTipText("Auto Answer Calls");

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
                .addContainerGap(7, Short.MAX_VALUE))
        );
        cancelPanelLayout.setVerticalGroup(
            cancelPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(cancelLabel)
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
                .addContainerGap(11, Short.MAX_VALUE))
        );
        mutePanelLayout.setVerticalGroup(
            mutePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(muteLabel)
        );

        org.jdesktop.layout.GroupLayout displayPanelLayout = new org.jdesktop.layout.GroupLayout(displayPanel);
        displayPanel.setLayout(displayPanelLayout);
        displayPanelLayout.setHorizontalGroup(
            displayPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(displayPanelLayout.createSequentialGroup()
                .add(softphoneInfoLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 245, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(proxyInfoLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 249, Short.MAX_VALUE)
                .addContainerGap())
            .add(primaryStatusLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 506, Short.MAX_VALUE)
            .add(primaryStatusDetailsLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 506, Short.MAX_VALUE)
            .add(secondaryStatusLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 506, Short.MAX_VALUE)
            .add(secondaryStatusDetailsLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 506, Short.MAX_VALUE)
            .add(displayPanelLayout.createSequentialGroup()
                .add(32, 32, 32)
                .add(onPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(idlePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(connectingPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(callingPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(ringingPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(acceptingPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(talkingPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(registeredPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(answerPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cancelPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(mutePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(40, Short.MAX_VALUE))
        );

        displayPanelLayout.linkSize(new java.awt.Component[] {acceptingPanel, answerPanel, callingPanel, cancelPanel, connectingPanel, idlePanel, mutePanel, onPanel, registeredPanel, ringingPanel, talkingPanel}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        displayPanelLayout.setVerticalGroup(
            displayPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(displayPanelLayout.createSequentialGroup()
                .add(displayPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(softphoneInfoLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(proxyInfoLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(38, 38, 38)
                .add(primaryStatusLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 31, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(primaryStatusDetailsLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(24, 24, 24)
                .add(secondaryStatusLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 31, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(secondaryStatusDetailsLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(24, 24, 24)
                .add(displayPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(mutePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, connectingPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, answerPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, callingPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, registeredPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, ringingPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, cancelPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, idlePanel, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, acceptingPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, onPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, talkingPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .add(12, 12, 12))
        );

        org.jdesktop.layout.GroupLayout displayTabPanelLayout = new org.jdesktop.layout.GroupLayout(displayTabPanel);
        displayTabPanel.setLayout(displayTabPanelLayout);
        displayTabPanelLayout.setHorizontalGroup(
            displayTabPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(displayPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        displayTabPanelLayout.setVerticalGroup(
            displayTabPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(displayPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        tabPanel.addTab("Display", displayTabPanel);

        configTabPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("STHeiti", 0, 12), new java.awt.Color(255, 255, 255))); // NOI18N
        configTabPanel.setToolTipText("");

        authenticationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("STHeiti", 0, 8))); // NOI18N
        authenticationPanel.setToolTipText("");

        clientIPLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
        clientIPLabel.setText("Client");

        clientIPField.setBackground(new java.awt.Color(204, 204, 204));
        clientIPField.setFont(new java.awt.Font("STHeiti", 0, 10)); // NOI18N
        clientIPField.setToolTipText("Your computer's IP (Automatic)");

        pubIPField.setFont(new java.awt.Font("STHeiti", 0, 10)); // NOI18N
        pubIPField.setToolTipText("Please fill in your public IP address (type: \"myip\" in google to find you public ip address)");
        pubIPField.setNextFocusableComponent(usernameField);

        pubIPLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
        pubIPLabel.setText("Pub IP");

        clientPortLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
        clientPortLabel.setText("Port");

        clientPortField.setBackground(new java.awt.Color(204, 204, 204));
        clientPortField.setFont(new java.awt.Font("STHeiti", 0, 10)); // NOI18N
        clientPortField.setToolTipText("Client Port (Tip: \"auto\")");
        clientPortField.setNextFocusableComponent(usernameField);

        domainLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
        domainLabel.setText("Domain");

        domainField.setFont(new java.awt.Font("STHeiti", 0, 10)); // NOI18N
        domainField.setToolTipText("Internet Telephone Provider Domain (Tip: sip1.budgetphone.nl)");

        serverIPLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
        serverIPLabel.setText("Server");

        serverIPField.setFont(new java.awt.Font("STHeiti", 0, 10)); // NOI18N
        serverIPField.setToolTipText("Internet Telephone Provider Server (Tip: sip1.budgetphone.nl)");
        serverIPField.setNextFocusableComponent(usernameField);

        serverPortLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
        serverPortLabel.setText("Port");

        serverPortField.setFont(new java.awt.Font("STHeiti", 0, 10)); // NOI18N
        serverPortField.setToolTipText("Internet Telephone Provider Port (Tip: \"5060\")");
        serverPortField.setNextFocusableComponent(usernameField);

        usernameLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
        usernameLabel.setText("User");

        usernameField.setFont(new java.awt.Font("STHeiti", 0, 10)); // NOI18N
        usernameField.setToolTipText("Username (comes from your Internet Telephone Provider)");
        usernameField.setNextFocusableComponent(toegangField);

        toegangLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
        toegangLabel.setText("Secr");

        toegangField.setFont(new java.awt.Font("STHeiti", 0, 10)); // NOI18N
        toegangField.setToolTipText("Password (comes from your Internet Telephone Provider)");

        registerLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
        registerLabel.setText("Reg");

        registerCheckBox.setFont(new java.awt.Font("STHeiti", 0, 10));
        registerCheckBox.setToolTipText("Enable Automatic Proxy Login");
        registerCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        registerCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        registerCheckBox.setIconTextGap(0);

        saveProxyConfigButton.setFont(new java.awt.Font("STHeiti", 1, 10));
        saveProxyConfigButton.setText("Save");
        saveProxyConfigButton.setToolTipText("Saves Config (Tip: Powercycle again after Save)");
        saveProxyConfigButton.setFocusPainted(false);
        saveProxyConfigButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveProxyConfigButtonActionPerformed(evt);
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

        lookAndFeelGroup.add(lookAndFeelRButtonMotif);
        lookAndFeelRButtonMotif.setFont(new java.awt.Font("STHeiti", 0, 8));
        lookAndFeelRButtonMotif.setText("Motif");
        lookAndFeelRButtonMotif.setToolTipText("Set Look & Feel");
        lookAndFeelRButtonMotif.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lookAndFeelRButtonMotifMouseClicked(evt);
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

        debugCheckBox.setFont(new java.awt.Font("STHeiti", 0, 10));
        debugCheckBox.setToolTipText("Enable Debugging Level 0");
        debugCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        debugCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        debugCheckBox.setIconTextGap(0);
        debugCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                debugCheckBoxActionPerformed(evt);
            }
        });

        debugLabel.setFont(new java.awt.Font("STHeiti", 0, 10));
        debugLabel.setText("Debug");
        debugLabel.setToolTipText("Enable Debugging Level 1");
        debugLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                debugLabelMouseClicked(evt);
            }
        });

        org.jdesktop.layout.GroupLayout authenticationPanelLayout = new org.jdesktop.layout.GroupLayout(authenticationPanel);
        authenticationPanel.setLayout(authenticationPanelLayout);
        authenticationPanelLayout.setHorizontalGroup(
            authenticationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(authenticationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(authenticationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(authenticationPanelLayout.createSequentialGroup()
                        .add(authenticationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(authenticationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, usernameLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, toegangLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .add(serverIPLabel)
                            .add(domainLabel)
                            .add(registerLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(authenticationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(authenticationPanelLayout.createSequentialGroup()
                                .add(authenticationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, toegangField)
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, usernameField)
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, serverIPField)
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, domainField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 256, Short.MAX_VALUE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(serverPortLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(serverPortField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 49, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(authenticationPanelLayout.createSequentialGroup()
                                .add(registerCheckBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 151, Short.MAX_VALUE)
                                .add(saveProxyConfigButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(146, 146, 146))))
                    .add(authenticationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, jSeparator1)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, authenticationPanelLayout.createSequentialGroup()
                            .add(clientIPLabel)
                            .add(18, 18, 18)
                            .add(clientIPField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 113, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(pubIPLabel)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(pubIPField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(clientPortLabel)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(clientPortField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 54, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(authenticationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lookAndFeelRButtonMotif)
                    .add(lookAndFeelRButtonGTK)
                    .add(lookAndFeelRButtonNimbus)
                    .add(lookAndFeelRButtonWindows)
                    .add(authenticationPanelLayout.createSequentialGroup()
                        .add(debugLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(debugCheckBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        authenticationPanelLayout.linkSize(new java.awt.Component[] {clientPortLabel, serverPortLabel}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        authenticationPanelLayout.setVerticalGroup(
            authenticationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(authenticationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(authenticationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(authenticationPanelLayout.createSequentialGroup()
                        .add(authenticationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(clientIPLabel)
                            .add(clientIPField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(pubIPField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(pubIPLabel)
                            .add(clientPortLabel)
                            .add(clientPortField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(authenticationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(domainLabel)
                            .add(domainField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(authenticationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(serverIPLabel)
                            .add(serverIPField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(serverPortLabel)
                            .add(serverPortField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(authenticationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(usernameLabel)
                            .add(usernameField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(authenticationPanelLayout.createSequentialGroup()
                        .add(authenticationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(authenticationPanelLayout.createSequentialGroup()
                                .add(23, 23, 23)
                                .add(lookAndFeelRButtonMotif, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(lookAndFeelRButtonGTK, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(lookAndFeelRButtonNimbus, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(lookAndFeelRButtonWindows, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(authenticationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(authenticationPanelLayout.createSequentialGroup()
                        .add(authenticationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(toegangLabel)
                            .add(toegangField))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(authenticationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(registerLabel)
                            .add(registerCheckBox, 0, 0, Short.MAX_VALUE)
                            .add(saveProxyConfigButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 13, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(authenticationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(debugCheckBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 13, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(debugLabel)))
                .add(12, 12, 12))
        );

        authenticationPanelLayout.linkSize(new java.awt.Component[] {clientIPLabel, domainLabel, serverIPLabel, toegangLabel}, org.jdesktop.layout.GroupLayout.VERTICAL);

        authenticationPanelLayout.linkSize(new java.awt.Component[] {clientIPField, clientPortField, domainField, pubIPField, serverIPField, serverPortField, toegangField, usernameField}, org.jdesktop.layout.GroupLayout.VERTICAL);

        authenticationPanelLayout.linkSize(new java.awt.Component[] {clientPortLabel, serverPortLabel}, org.jdesktop.layout.GroupLayout.VERTICAL);

        org.jdesktop.layout.GroupLayout configTabPanelLayout = new org.jdesktop.layout.GroupLayout(configTabPanel);
        configTabPanel.setLayout(configTabPanelLayout);
        configTabPanelLayout.setHorizontalGroup(
            configTabPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(authenticationPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        configTabPanelLayout.setVerticalGroup(
            configTabPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(configTabPanelLayout.createSequentialGroup()
                .add(authenticationPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(32, Short.MAX_VALUE))
        );

        tabPanel.addTab("Configure", configTabPanel);

        textLogAreaScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        textLogAreaScrollPane.setFont(new java.awt.Font("STHeiti", 0, 8));

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
        textLogAreaScrollPane.setViewportView(textLogArea);

        org.jdesktop.layout.GroupLayout logTabPanelLayout = new org.jdesktop.layout.GroupLayout(logTabPanel);
        logTabPanel.setLayout(logTabPanelLayout);
        logTabPanelLayout.setHorizontalGroup(
            logTabPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(logTabPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(textLogAreaScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 506, Short.MAX_VALUE)
                .addContainerGap())
        );
        logTabPanelLayout.setVerticalGroup(
            logTabPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(logTabPanelLayout.createSequentialGroup()
                .add(textLogAreaScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 242, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabPanel.addTab("Log", logTabPanel);

        org.jdesktop.layout.GroupLayout colorMaskPanelLayout = new org.jdesktop.layout.GroupLayout(colorMaskPanel);
        colorMaskPanel.setLayout(colorMaskPanelLayout);
        colorMaskPanelLayout.setHorizontalGroup(
            colorMaskPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(colorMaskPanelLayout.createSequentialGroup()
                .add(colorMaskPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(colorMaskPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(tabPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 539, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(colorMaskPanelLayout.createSequentialGroup()
                        .add(79, 79, 79)
                        .add(telephonePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        colorMaskPanelLayout.setVerticalGroup(
            colorMaskPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(colorMaskPanelLayout.createSequentialGroup()
                .add(tabPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 291, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(telephonePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(7, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(colorMaskPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(colorMaskPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void setLookAndFeel(int plafIndexParam)
    {
        plafSelected = plaf[plafIndexParam];
        try { UIManager.setLookAndFeel(plafSelected); } catch (ClassNotFoundException ex) {} catch (InstantiationException ex) {} catch (IllegalAccessException ex) {} catch (UnsupportedLookAndFeelException ex) {}
        setVisible(false); setVisible(true);
    }

    private void button1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button1ActionPerformed
        chooseDigit("1");
    }//GEN-LAST:event_button1ActionPerformed

    private void button2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button2ActionPerformed
        chooseDigit("2");
    }//GEN-LAST:event_button2ActionPerformed

    private void button3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button3ActionPerformed
        chooseDigit("3");
    }//GEN-LAST:event_button3ActionPerformed

    private void button6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button6ActionPerformed
        chooseDigit("6");
    }//GEN-LAST:event_button6ActionPerformed

    private void button4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button4ActionPerformed
        chooseDigit("4");
    }//GEN-LAST:event_button4ActionPerformed

    private void button5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button5ActionPerformed
        chooseDigit("5");
    }//GEN-LAST:event_button5ActionPerformed

    private void button0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button0ActionPerformed
        chooseDigit("0");
    }//GEN-LAST:event_button0ActionPerformed

    private void button9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button9ActionPerformed
        chooseDigit("9");
    }//GEN-LAST:event_button9ActionPerformed

    private void button7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button7ActionPerformed
        chooseDigit("7");
    }//GEN-LAST:event_button7ActionPerformed

    private void endButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_endButtonActionPerformed
        String[] status = new String[2]; status = softPhone1.userInput(ENDBUTTON, "", "", "");
        if (status[0].equals("1")) { logToApplication("End Button Failure: " + status[1]); }
    }//GEN-LAST:event_endButtonActionPerformed

    private void callButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_callButtonActionPerformed
	String[] status = new String[2]; status = softPhone1.userInput(CALLBUTTON, VoipStormTools.validateSIPAddress(destinationField.getText()), "file:" + soundsDir + filenameField.getText(), "");
        if (status[0].equals("1"))
        {
            logToApplication("Call Failure: " + status[1]);
        }
    }//GEN-LAST:event_callButtonActionPerformed

    private void button8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button8ActionPerformed
        chooseDigit("8");
    }//GEN-LAST:event_button8ActionPerformed

    private void registerToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_registerToggleButtonActionPerformed
        if (registerToggleButton.isSelected())
        {
//            clickOnTonesTool.play();
            if (audioToolWanted) { audioTool.play(audioTool.getClickontoneClip()); }
            softPhone1.registerRequestCounter = 1;
            String[] status = new String[2]; status = softPhone1.userInput(REGISTERBUTTON, "1", "", "");
            if (status[0].equals("1"))
            {
                logToApplication("Registration Error: " + status[1]);
//                failureTonesTool.play();
                if (audioToolWanted) { audioTool.play(audioTool.getFailuretoneClip()); }
            }
        }
        else
        {
//            clickOffTonesTool.play();
            if (audioToolWanted) { audioTool.play(audioTool.getClickofftoneClip()); }
            String[] status = new String[2]; status = softPhone1.userInput(REGISTERBUTTON, "0", "", "");
            if (status[0].equals("1"))
            {
                logToApplication("Registration Error: " + status[1]);
//                failureTonesTool.play();
                if (audioToolWanted) { audioTool.play(audioTool.getFailuretoneClip()); }
            }
        }
    }//GEN-LAST:event_registerToggleButtonActionPerformed

    private void muteAudioToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_muteAudioToggleButtonActionPerformed
        if (muteAudioToggleButton.isSelected())
        {
            if (softPhone1.soundStreamer != null)
            {
//                clickOnTonesTool.play();
                if (audioToolWanted) { audioTool.play(audioTool.getClickontoneClip()); }
                muteAudioToggleButton.setForeground(Color.blue);
                String[] status = new String[2]; status = softPhone1.userInput(MUTEAUDIOBUTTON, "1", "", "");
                if (status[0].equals("1"))
                {
                    showStatus("Mute Audio Error: " + status[1], true, true);
                }
            }
        }
        else
        {
//            clickOffTonesTool.play();
            if (audioToolWanted) { audioTool.play(audioTool.getClickofftoneClip()); }
            String[] status = new String[2]; status = softPhone1.userInput(MUTEAUDIOBUTTON, "0", "", "");
            if (status[0].equals("1"))
            {
                showStatus("Mute Audio Error: " + status[1], true, true);
            }
        }
    }//GEN-LAST:event_muteAudioToggleButtonActionPerformed

    private void line1ToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_line1ToggleButtonActionPerformed
        String[] status = new String[2];
        if (line1ToggleButton.isSelected())
        {
//            clickOnTonesTool.play();
            if (audioToolWanted) { audioTool.play(audioTool.getClickontoneClip()); }
            if (!managedByCallCenter)
            {
                softPhoneThreadArray = new Thread[1];                                 // Create a 2 alements Thread Array
                softPhoneThreadArray[0] = (SoftPhone) new SoftPhone(this, 0, debugging); // Instantiate the SoftPhone in Thread element 0
                softPhoneThreadArray[0].setDaemon(true);                              // Make sure it dies when parent dies
                softPhoneThreadArray[0].setPriority(9);
                softPhoneThreadArray[0].start();                                      // Start the Thread / SoftPhone
                softPhone1 = (SoftPhone) softPhoneThreadArray[0];                   // Make a named SoftPhone reference call mySoftPhone1
            }
            enableConfigurationUsage();
            enableTelephoneUsage();
            if (!managedByCallCenter)
            {
                status = softPhone1.userInput(LINE1BUTTON, "1", "1", "0"); // LineNr, On/Off, ManagedByCallCenter
            }
            else
            {
                status = softPhone1.userInput(LINE1BUTTON, "1", "1", "1"); // LineNr, On/Off, ManagedByCallCenter
            }
            if (status[0].equals("1"))
            {
//                failureTonesTool.play();
                if (audioToolWanted) { audioTool.play(audioTool.getFailuretoneClip()); }
                showStatus("Line1 Error: " + status[1], true, true);
                line1ToggleButton.setSelected(false);
            }
            else
            {
//                successTonesTool.play();
                if (audioToolWanted) { audioTool.play(audioTool.getSuccesstoneClip()); }
                line1ToggleButton.setForeground(Color.blue);
                line2ToggleButton.setEnabled(false);

                if (!managedByCallCenter)
                {
                    // Copy over the softphone instance configuration to softphonegui config fields just to see how they reflect in the softphone itself
                    clientIPField.setText(softPhone1.configuration.getClientIP());
                    pubIPField.setText(softPhone1.configuration.getPublicIP());
                    clientPortField.setText(softPhone1.configuration.getClientPort());
                    domainField.setText(softPhone1.configuration.getDomain());
                    serverIPField.setText(softPhone1.configuration.getServerIP());
                    serverPortField.setText(softPhone1.configuration.getServerPort());
                    usernameField.setText(softPhone1.configuration.getUsername());
                    toegangField.setText(softPhone1.configuration.getToegang());
                }
		destinationField.setText("sip:" + softPhone1.configuration.getClientIP() + ":" + status[1]);

		if (softPhone1.configuration.getRegister().equals("1")) {registerCheckBox.setSelected(true);} else {registerCheckBox.setSelected(false);}

                // If all is well then check whether autoregister is set
                if (softPhone1.configuration.getRegister().equals("1")) // Checks the registerCheckBox
                {
                    softPhone1.registerRequestCounter = 1;

                    status = softPhone1.userInput(REGISTERBUTTON, "1", "", "");
                    if (status[0].equals("1"))
                    {
                        showStatus("Registration Error: " + status[1], true, true);
//                        failureTonesTool.play();

                    }
                    else
                    {
                        //enabledTonesTool.play();
                    }
                }
                if (managedByCallCenter) {  }
            }
        }
        else
        {
//            clickOffTonesTool.play();
            if (audioToolWanted) { audioTool.play(audioTool.getClickofftoneClip()); }
            status = softPhone1.userInput(LINE1BUTTON, "1", "0", "");
            if (status[0].equals("1"))
            {
//                failureTonesTool.play();
                if (audioToolWanted) { audioTool.play(audioTool.getFailuretoneClip()); }
                showStatus("Line1 Error: " + status[1], true, true);
                line1ToggleButton.setSelected(false);
            }
            //softPhone1.terminate();
            line1ToggleButton.setForeground(Color.black);
            if ( ! externalSoftPhoneInstance ) { line2ToggleButton.setEnabled(true); }

            if (!managedByCallCenter)
            {
                clientIPField.setText("");
                pubIPField.setText("");
                clientPortField.setText("");
                domainField.setText("");
                serverIPField.setText("");
                serverPortField.setText("");
                usernameField.setText("");
                toegangField.setText("");
                registerCheckBox.setSelected(false);
            }
            destinationField.setText("");
            disableConfigurationUsage();
            disableTelephoneUsage();
            //destinationField.setText("");
            debugCheckBox.setSelected(false);
            setDebugging(false, 0);
            resetLog();
            showStatus("EPhone switched Off", true, false);
        }
    }//GEN-LAST:event_line1ToggleButtonActionPerformed

    private void line2ToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_line2ToggleButtonActionPerformed
        String[] status = new String[2];
        if (line2ToggleButton.isSelected())
        {
//            clickOnTonesTool.play();
            if (audioToolWanted) { audioTool.play(audioTool.getClickontoneClip()); }
            if (!managedByCallCenter)
            {
                softPhoneThreadArray = new Thread[1];
                softPhoneThreadArray[0] = (SoftPhone) new SoftPhone(this, 1, debugging);
                softPhoneThreadArray[0].setDaemon(true);
                softPhoneThreadArray[0].start();
                softPhone1 = (SoftPhone) softPhoneThreadArray[0];
            }
            enableConfigurationUsage();
            enableTelephoneUsage();
            if (!managedByCallCenter)
            {
                status = softPhone1.userInput(LINE2BUTTON, "2", "1", "0"); // LineNr, On/Off, ManagedByCallCenter
            }
            else
            {
                status = softPhone1.userInput(LINE2BUTTON, "2", "1", "1"); // LineNr, On/Off, ManagedByCallCenter
            }
            if (status[0].equals("1"))
            {
//                failureTonesTool.play();
                if (audioToolWanted) { audioTool.play(audioTool.getFailuretoneClip()); }
                showStatus("Line2 Error: " + status[1], true, true);
                line2ToggleButton.setSelected(false);
            }
            else
            {
//                successTonesTool.play();
                if (audioToolWanted) { audioTool.play(audioTool.getSuccesstoneClip()); }
                line2ToggleButton.setForeground(Color.blue);
                line1ToggleButton.setEnabled(false);

                if (!managedByCallCenter)
                {
                    clientIPField.setText(softPhone1.configuration.getClientIP());
                    pubIPField.setText(softPhone1.configuration.getPublicIP());
                    clientPortField.setText(softPhone1.configuration.getClientPort());
                    domainField.setText(softPhone1.configuration.getDomain());
                    serverIPField.setText(softPhone1.configuration.getServerIP());
                    serverPortField.setText(softPhone1.configuration.getServerPort());
                    usernameField.setText(softPhone1.configuration.getUsername());
                    toegangField.setText(softPhone1.configuration.getToegang());
                }
		destinationField.setText("sip:" + softPhone1.configuration.getClientIP() + ":" + status[1]);

		if (softPhone1.configuration.getRegister().equals("1")) {registerCheckBox.setSelected(true);} else {registerCheckBox.setSelected(false);}
                
                // If all is well then check whether autoregister is set
                if (softPhone1.configuration.getRegister().equals("1"))
                {
                    softPhone1.registerRequestCounter = 1;

                    status = softPhone1.userInput(REGISTERBUTTON, "1", "", "");
                    if (status[0].equals("1"))
                    {
                        showStatus("Registration Error: " + status[1], true, true);
//                        failureTonesTool.play();
                        if (audioToolWanted) { audioTool.play(audioTool.getFailuretoneClip()); }
                    }
                    else
                    {
                        //enabledTonesTool.play();
                    }
                }
            }
        }
        else
        {
//            clickOffTonesTool.play();
            if (audioToolWanted) { audioTool.play(audioTool.getClickofftoneClip()); }
            status = softPhone1.userInput(LINE2BUTTON, "2", "0", "");
            if (status[0].equals("1"))
            {
//                failureTonesTool.play();
                if (audioToolWanted) { audioTool.play(audioTool.getFailuretoneClip()); }
                showStatus("Line2 Error: " + status[1], true, true);
                line2ToggleButton.setSelected(false);
            }
            //softPhone1.terminate();
            
            line2ToggleButton.setForeground(Color.black);
            line1ToggleButton.setEnabled(true);
            if (!managedByCallCenter)
            {
                clientIPField.setText("");
                pubIPField.setText("");
                clientPortField.setText("");
                domainField.setText("");
                serverIPField.setText("");
                serverPortField.setText("");
                usernameField.setText("");
                toegangField.setText("");
                registerCheckBox.setSelected(false);
            }
            destinationField.setText("");
            disableConfigurationUsage();
            disableTelephoneUsage();
            //destinationField.setText("");
            debugCheckBox.setSelected(false);
            setDebugging(false, 0);
            resetLog();
            showStatus("EPhone switched Off", true, true);
        }

    }//GEN-LAST:event_line2ToggleButtonActionPerformed

    private void autoAnswerToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoAnswerToggleButtonActionPerformed
        String[] status = new String[2];
        if (autoAnswerToggleButton.isSelected())
        {
//            clickOnTonesTool.play();
            if (audioToolWanted) { audioTool.play(audioTool.getClickofftoneClip()); }

            if (autoCancelToggleButton.isSelected())
            { status = softPhone1.userInput(CANCELBUTTON, "0", "", ""); if (status[0].equals("1")) { showStatus("AutoCancel Error: " + status[1], true, true); } }
              status = softPhone1.userInput(ANSWERBUTTON, "1", "1000", ""); if (status[0].equals("1")) { showStatus("AutoAnswer Error: " + status[1], true, true); }
        }
        else
        {
//            clickOffTonesTool.play();
            if (audioToolWanted) { audioTool.play(audioTool.getClickofftoneClip()); }
            status = softPhone1.userInput(ANSWERBUTTON, "0", "", "");
            if (status[0].equals("1")) { showStatus("AutoAnswer Error: " + status[1], true, true); }
        }
    }//GEN-LAST:event_autoAnswerToggleButtonActionPerformed

    private void poolButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_poolButtonActionPerformed
	String[] status = new String[2]; status = softPhone1.userInput(CALLBUTTON, VoipStormTools.validateSIPAddress(destinationField.getText()), "file:" + soundsDir + filenameField.getText(), "Scan");
        if (status[0].equals("1"))
        {
            logToApplication("Call Failure: " + status[1]);
        }
    }//GEN-LAST:event_poolButtonActionPerformed

    private void formKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyReleased
    }//GEN-LAST:event_formKeyReleased

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
    }//GEN-LAST:event_formKeyPressed

    private void formKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyTyped
    }//GEN-LAST:event_formKeyTyped

    private void saveProxyConfigButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveProxyConfigButtonActionPerformed
//        clickOnTonesTool.play();
        if (audioToolWanted) { audioTool.play(audioTool.getClickontoneClip()); }
        softPhone1.configuration.setDomain(domainField.getText());
        softPhone1.configuration.setClientIP(clientIPField.getText());
        softPhone1.configuration.setPublicIP(pubIPField.getText());
        softPhone1.configuration.setClientPort(clientPortField.getText());
        softPhone1.configuration.setServerIP(serverIPField.getText());
        softPhone1.configuration.setServerPort(serverPortField.getText());
        softPhone1.configuration.setUsername(usernameField.getText());
        softPhone1.configuration.setToegang(new String(toegangField.getPassword()));
        if (registerCheckBox.isSelected()) {softPhone1.configuration.setRegister("1");} else {softPhone1.configuration.setRegister("0");}
        softPhone1.configuration.saveConfiguration(softPhone1.activeLineNumber);
        softPhone1.configuration.loadConfiguration(softPhone1.activeLineNumber);
}//GEN-LAST:event_saveProxyConfigButtonActionPerformed

    private void autoCancelToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoCancelToggleButtonActionPerformed
        String[] status = new String[2];
        if (autoCancelToggleButton.isSelected())
        {
//            clickOnTonesTool.play();
            if (audioToolWanted) { audioTool.play(audioTool.getClickontoneClip()); }
            if (autoAnswerToggleButton.isSelected())
            { status = softPhone1.userInput(ANSWERBUTTON, "0", "", ""); if (status[0].equals("1")) { showStatus("AutoAnswer Error: " + status[1], true, true); } }
            status = softPhone1.userInput(CANCELBUTTON, "1", "1000", ""); if (status[0].equals("1")) { showStatus("AutoCancel Error: " + status[1], true, true); }
        }
        else
        {
//            clickOffTonesTool.play();
            if (audioToolWanted) { audioTool.play(audioTool.getClickofftoneClip()); }
            status = softPhone1.userInput(CANCELBUTTON, "0", "", "");
            if (status[0].equals("1")) { showStatus("AutoCancel Error: " + status[1], true, true); }
        }
    }//GEN-LAST:event_autoCancelToggleButtonActionPerformed

    private void destinationFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_destinationFieldKeyReleased
        destination.setDestination(destinationField.getText());
        softPhone1.setDestination(destination);
    }//GEN-LAST:event_destinationFieldKeyReleased

    private void randomRingResponseToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_randomRingResponseToggleButtonActionPerformed
        String[] status = new String[2];
        if (randomRingResponseToggleButton.isSelected())
        {
//            clickOnTonesTool.play();
            if (audioToolWanted) { audioTool.play(audioTool.getClickontoneClip()); }
//            status = mySoftPhone1.userInput("RandomRingResponseButton", "1", "50", ""); if (status[0].equals("1")) { showStatus("RandomRingResponse Error: " + status[1], true); }

            // Sets Human Simulation for Inbound Mode
            status = softPhone1.userInput(RANDOMRINGRESPONSEBUTTON, "1", "50", "25");
            if (status[0].equals("1")) { showStatus("Human Simulation Error: " + status[1], true, true); }

            // Sets the CallEnd Timer to autoEnd this Call after a certain random period (The answerer lost interest)
            status = softPhone1.userInput(ENDTIMERBUTTON, "1", "30000", "");
            if (status[0].equals("1")) { showStatus("End Timer Error: " + status[1], true, true); }
        }
        else
        {
//            clickOffTonesTool.play();
            if (audioToolWanted) { audioTool.play(audioTool.getClickofftoneClip()); }
//            status = mySoftPhone1.userInput("RandomRingResponseButton", "0", "50", ""); if (status[0].equals("1")) { showStatus("RandomRingResponse Error: " + status[1], true); }

            // Sets Human Simulation for Inbound Mode
            status = softPhone1.userInput(RANDOMRINGRESPONSEBUTTON, "0", "50", "25");
            if (status[0].equals("1")) { showStatus("Human Simulation Error: " + status[1], true, true); }

            // Sets the CallEnd Timer to autoEnd this Call after a certain random period (The answerer lost interest)
            status = softPhone1.userInput(ENDTIMERBUTTON, "0", "30000", "");
            if (status[0].equals("1")) { showStatus("End Timer Error: " + status[1], true, true); }
        }
    }//GEN-LAST:event_randomRingResponseToggleButtonActionPerformed

    private void lookAndFeelRButtonMotifMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lookAndFeelRButtonMotifMouseClicked
        setLookAndFeel(PLAF_MOTIF);
}//GEN-LAST:event_lookAndFeelRButtonMotifMouseClicked

    private void lookAndFeelRButtonGTKMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lookAndFeelRButtonGTKMouseClicked
        setLookAndFeel(PLAF_GTK);
    }//GEN-LAST:event_lookAndFeelRButtonGTKMouseClicked

    private void lookAndFeelRButtonNimbusMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lookAndFeelRButtonNimbusMouseClicked
        setLookAndFeel(PLAF_NIMBUS);
    }//GEN-LAST:event_lookAndFeelRButtonNimbusMouseClicked

    private void lookAndFeelRButtonWindowsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lookAndFeelRButtonWindowsMouseClicked
        setLookAndFeel(PLAF_WINDOWS);
    }//GEN-LAST:event_lookAndFeelRButtonWindowsMouseClicked

    private void debugCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_debugCheckBoxActionPerformed
        if (debugCheckBox.isSelected()) {setDebugging(true, 0);} else {setDebugging(false, 0);}
    }//GEN-LAST:event_debugCheckBoxActionPerformed

    private void textLogAreaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_textLogAreaMouseClicked
        if (evt.getClickCount() == 2) {textLogArea.setText(""); }
    }//GEN-LAST:event_textLogAreaMouseClicked

    private void debugLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_debugLabelMouseClicked
        if (!debugCheckBox.isSelected()) {debugCheckBox.setSelected(true); setDebugging(true, 1);} else {debugCheckBox.setSelected(false); setDebugging(false, 0);}
    }//GEN-LAST:event_debugLabelMouseClicked

    /**
     *
     * @param remoteDisplayData
     */
    @Override
    synchronized public void phoneDisplay(DisplayData remoteDisplayData)
    {
        if (!displayData.getSoftphoneInfoCell().equals(remoteDisplayData.getSoftphoneInfoCell()))
        { displayData.setSoftphoneInfoCell(remoteDisplayData.getSoftphoneInfoCell());  softphoneInfoLabel.setText(displayData.getSoftphoneInfoCell()); }
        if (!displayData.getProxyInfoCell().equals(remoteDisplayData.getProxyInfoCell()))
        { displayData.setProxyInfoCell(remoteDisplayData.getProxyInfoCell());          proxyInfoLabel.setText(displayData.getProxyInfoCell()); }
        if (!displayData.getPrimaryStatusCell().equals(remoteDisplayData.getPrimaryStatusCell()))
        { displayData.setPrimaryStatusCell(remoteDisplayData.getPrimaryStatusCell());  primaryStatusLabel.setText(displayData.getPrimaryStatusCell()); }
        if (!displayData.getPrimaryStatusDetailsCell().equals(remoteDisplayData.getPrimaryStatusDetailsCell()))
        { displayData.setPrimaryStatusDetailsCell(remoteDisplayData.getPrimaryStatusDetailsCell());  primaryStatusDetailsLabel.setText(displayData.getPrimaryStatusDetailsCell()); }
        if (!displayData.getSecondaryStatusCell().equals(remoteDisplayData.getSecondaryStatusCell()))
        { displayData.setSecondaryStatusCell(remoteDisplayData.getSecondaryStatusCell());  secondaryStatusLabel.setText(displayData.getSecondaryStatusCell()); }
        if (!displayData.getSecondaryStatusDetailsCell().equals(remoteDisplayData.getSecondaryStatusDetailsCell()))
        { displayData.setSecondaryStatusDetailsCell(remoteDisplayData.getSecondaryStatusDetailsCell()); secondaryStatusDetailsLabel.setText(displayData.getSecondaryStatusDetailsCell()); }

        if (displayData.getOnFlag() != remoteDisplayData.getOnFlag())
        {
            displayData.setOnFlag(remoteDisplayData.getOnFlag());
            if (displayData.getOnFlag())       { onPanel.setBorder(BorderFactory.createLineBorder(displayData.ONCOLOR)); onLabel.setForeground(displayData.ONCOLOR); }
            else { onPanel.setBorder(BorderFactory.createLineBorder(displayData.INACTIVECOLOR)); onLabel.setForeground(displayData.INACTIVECOLOR); }
        }
        if (displayData.getIdleFlag() != remoteDisplayData.getIdleFlag())
        {
            displayData.setIdleFlag(remoteDisplayData.getIdleFlag());
            if (displayData.getIdleFlag())     { idlePanel.setBorder(BorderFactory.createLineBorder(displayData.IDLECOLOR)); idleLabel.setForeground(displayData.IDLECOLOR); }
            else { idlePanel.setBorder(BorderFactory.createLineBorder(displayData.INACTIVECOLOR)); idleLabel.setForeground(displayData.INACTIVECOLOR); }
        }
        if (displayData.getConnectFlag() != remoteDisplayData.getConnectFlag())
        {
            displayData.setConnectFlag(remoteDisplayData.getConnectFlag());
            if (displayData.getConnectFlag())  { connectingPanel.setBorder(BorderFactory.createLineBorder(displayData.CALLINGCOLOR)); connectingLabel.setForeground(displayData.CONNECTCOLOR); }
            else { connectingPanel.setBorder(BorderFactory.createLineBorder(displayData.INACTIVECOLOR)); connectingLabel.setForeground(displayData.INACTIVECOLOR); }
        }
        if (displayData.getCallingFlag() != remoteDisplayData.getCallingFlag())
        {
            displayData.setCallingFlag(remoteDisplayData.getCallingFlag());
            if (displayData.getCallingFlag())  { callingPanel.setBorder(BorderFactory.createLineBorder(displayData.CALLINGCOLOR)); callingLabel.setForeground(displayData.CALLINGCOLOR); }
            else { callingPanel.setBorder(BorderFactory.createLineBorder(displayData.INACTIVECOLOR)); callingLabel.setForeground(displayData.INACTIVECOLOR); }
        }
        if (displayData.getRingingFlag() != remoteDisplayData.getRingingFlag())
        {
            displayData.setRingingFlag(remoteDisplayData.getRingingFlag());
            if (displayData.getRingingFlag())  { ringingPanel.setBorder(BorderFactory.createLineBorder(displayData.RINGINGCOLOR)); ringingLabel.setForeground(displayData.RINGINGCOLOR); }
            else { ringingPanel.setBorder(BorderFactory.createLineBorder(displayData.INACTIVECOLOR)); ringingLabel.setForeground(displayData.INACTIVECOLOR); }
        }
        if (displayData.getAcceptFlag() != remoteDisplayData.getAcceptFlag())
        {
            displayData.setAcceptFlag(remoteDisplayData.getAcceptFlag());
            if (displayData.getAcceptFlag())   { acceptingPanel.setBorder(BorderFactory.createLineBorder(displayData.ACCEPTCOLOR)); acceptingLabel.setForeground(displayData.ACCEPTCOLOR); }
            else { acceptingPanel.setBorder(BorderFactory.createLineBorder(displayData.INACTIVECOLOR)); acceptingLabel.setForeground(displayData.INACTIVECOLOR); }
        }
        if (displayData.getTalkingFlag() != remoteDisplayData.getTalkingFlag())
        {
            displayData.setTalkingFlag(remoteDisplayData.getTalkingFlag());
            if (displayData.getTalkingFlag())  { talkingPanel.setBorder(BorderFactory.createLineBorder(displayData.TALKINGCOLOR)); talkingLabel.setForeground(displayData.TALKINGCOLOR); }
            else { talkingPanel.setBorder(BorderFactory.createLineBorder(displayData.INACTIVECOLOR)); talkingLabel.setForeground(displayData.INACTIVECOLOR); }
        }
        if (displayData.getRegisteredFlag() != remoteDisplayData.getRegisteredFlag())
        {
            displayData.setRegisteredFlag(remoteDisplayData.getRegisteredFlag());
            if (displayData.getRegisteredFlag())
            {
                registeredPanel.setBorder(BorderFactory.createLineBorder(displayData.REGISTEREDCOLOR)); registeredLabel.setForeground(displayData.REGISTEREDCOLOR);
                registerToggleButton.setSelected(true); registerToggleButton.setForeground(Color.BLUE); destinationField.setText("");
            }
            else
            {
                registeredPanel.setBorder(BorderFactory.createLineBorder(displayData.INACTIVECOLOR)); registeredLabel.setForeground(displayData.INACTIVECOLOR);
                registerToggleButton.setSelected(false); registerToggleButton.setForeground(Color.BLACK);
            }
        }
        if (displayData.getAnswerFlag() != remoteDisplayData.getAnswerFlag())
        {
            displayData.setAnswerFlag(remoteDisplayData.getAnswerFlag());
            if (displayData.getAnswerFlag())
            {
                answerPanel.setBorder(BorderFactory.createLineBorder(displayData.ANSWERCOLOR)); answerLabel.setForeground(displayData.ANSWERCOLOR);
                autoAnswerToggleButton.setSelected(true); autoAnswerToggleButton.setForeground(Color.BLUE);
            }
            else
            {
                answerPanel.setBorder(BorderFactory.createLineBorder(displayData.INACTIVECOLOR)); answerLabel.setForeground(displayData.INACTIVECOLOR);
                autoAnswerToggleButton.setSelected(false); autoAnswerToggleButton.setForeground(Color.BLACK);
            }
        }
        if (displayData.getCancelFlag() != remoteDisplayData.getCancelFlag())
        {
            displayData.setCancelFlag(remoteDisplayData.getCancelFlag());
            if (displayData.getCancelFlag())
            {
                cancelPanel.setBorder(BorderFactory.createLineBorder(displayData.CANCELCOLOR)); cancelLabel.setForeground(displayData.CANCELCOLOR);
                autoCancelToggleButton.setSelected(true); autoCancelToggleButton.setForeground(Color.BLUE);
            }
            else
            {
                cancelPanel.setBorder(BorderFactory.createLineBorder(displayData.INACTIVECOLOR)); cancelLabel.setForeground(displayData.INACTIVECOLOR);
                autoCancelToggleButton.setSelected(false); autoCancelToggleButton.setForeground(Color.BLACK);
            }
        }
        if (displayData.getMuteFlag() != remoteDisplayData.getMuteFlag())
        {
            displayData.setMuteFlag(remoteDisplayData.getMuteFlag());
            if (displayData.getMuteFlag())
            {
                mutePanel.setBorder(BorderFactory.createLineBorder(displayData.MUTECOLOR)); muteLabel.setForeground(displayData.MUTECOLOR);
                muteAudioToggleButton.setSelected(true); muteAudioToggleButton.setForeground(Color.BLUE);
            }
            else
            {
                mutePanel.setBorder(BorderFactory.createLineBorder(displayData.INACTIVECOLOR)); muteLabel.setForeground(displayData.INACTIVECOLOR);
                muteAudioToggleButton.setSelected(false); muteAudioToggleButton.setForeground(Color.BLACK);
            }
        }
        return;
    }

    /**
     *
     * @param remoteSpeakerData
     */
    @Override
    public void speaker(SpeakerData remoteSpeakerData)
    {
        if ((!managedByCallCenter) && (audioToolWanted))
        {
            if (remoteSpeakerData.getDialToneFlag())                { audioTool.play(audioTool.getDialtoneClip()); }
            if (remoteSpeakerData.getCallToneFlag())                { audioTool.playLoop(audioTool.getCalltoneClip()); } else { audioTool.stop(audioTool.getCalltoneClip()); }
            if (remoteSpeakerData.getBusyToneFlag())                { audioTool.stop(audioTool.getCalltoneClip()); audioTool.play(audioTool.getBusytoneClip()); }
            if (remoteSpeakerData.getDeadToneFlag())                { audioTool.stop(audioTool.getCalltoneClip()); audioTool.play(audioTool.getDeadtoneClip()); }
            if (remoteSpeakerData.getErrorToneFlag())               { audioTool.stop(audioTool.getCalltoneClip()); audioTool.play(audioTool.getErrortoneClip()); }
            if (remoteSpeakerData.getRingToneFlag())                { audioTool.playLoop(audioTool.getRingtoneClip()); } else { audioTool.stop(audioTool.getRingtoneClip()); }
            if (remoteSpeakerData.getRegisterEnabledToneFlag())     { audioTool.play(audioTool.getRegisterenabledtoneClip()); }
            if (remoteSpeakerData.getRegisterDisabledToneFlag())    { audioTool.play(audioTool.getRegisterdisabledtoneClip()); }
            if (remoteSpeakerData.getAnswerEnabledToneFlag())       { audioTool.play(audioTool.getAnswerenabledtoneClip()); }
            if (remoteSpeakerData.getAnswerDisabledToneFlag())      { audioTool.play(audioTool.getAnswerdisabledtoneClip()); }
            if (remoteSpeakerData.getCancelEnabledToneFlag())       { audioTool.play(audioTool.getCancelenabledClip()); }
            if (remoteSpeakerData.getCancelDisabledToneFlag())      { audioTool.play(audioTool.getCanceldisabledClip()); }
            if (remoteSpeakerData.getMuteEnabledToneFlag())         { audioTool.play(audioTool.getMuteenabledClip()); }
            if (remoteSpeakerData.getMuteDisabledToneFlag())        { audioTool.play(audioTool.getMutedisabledClip()); }
        }
    }

    /**
     *
     * @param messageParam
     * @param logToApplicationParam
     * @param logToFileParam
     */
    @Override
    synchronized public void showStatus(String messageParam, boolean logToApplicationParam, boolean logToFileParam)
    {
        logToApplication(messageParam);
    }

    /**
     *
     * @param displaymessage
     */
    @Override
    synchronized public void logToApplication(String displaymessage)
    {
        logCalendar = Calendar.getInstance();

        String humanDate = "" +
        String.format("%04d", logCalendar.get(Calendar.YEAR)) + "-" +
        String.format("%02d", logCalendar.get(Calendar.MONTH) + 1) + "-" +
        String.format("%02d", logCalendar.get(Calendar.DAY_OF_MONTH)) + " " +
        String.format("%02d", logCalendar.get(Calendar.HOUR_OF_DAY)) + ":" +
        String.format("%02d", logCalendar.get(Calendar.MINUTE)) + ":" +
        String.format("%02d", logCalendar.get(Calendar.SECOND));
        textLogArea.append(humanDate + " " + displaymessage + lineTerminator);
    }

    /**
     *
     * @param displaymessage
     */
    @Override
    synchronized public void logToFile(String displaymessage)
    {
        logCalendar = Calendar.getInstance();

        String humanDate = "" +
        String.format("%04d", logCalendar.get(Calendar.YEAR)) + "-" +
        String.format("%02d", logCalendar.get(Calendar.MONTH) + 1) + "-" +
        String.format("%02d", logCalendar.get(Calendar.DAY_OF_MONTH)) + " " +
        String.format("%02d", logCalendar.get(Calendar.HOUR_OF_DAY)) + ":" +
        String.format("%02d", logCalendar.get(Calendar.MINUTE)) + ":" +
        String.format("%02d", logCalendar.get(Calendar.SECOND));
        textLogArea.append(humanDate + " " + displaymessage + lineTerminator);
    }

    /**
     *
     */
    @Override
    synchronized public void resetLog() { textLogArea.setText(""); }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel acceptingLabel;
    private javax.swing.JPanel acceptingPanel;
    private javax.swing.JLabel answerLabel;
    private javax.swing.JPanel answerPanel;
    private javax.swing.JPanel authenticationPanel;
    private javax.swing.JToggleButton autoAnswerToggleButton;
    private javax.swing.JToggleButton autoCancelToggleButton;
    private javax.swing.JButton button0;
    private javax.swing.JButton button1;
    private javax.swing.JButton button2;
    private javax.swing.JButton button3;
    private javax.swing.JButton button4;
    private javax.swing.JButton button5;
    private javax.swing.JButton button6;
    private javax.swing.JButton button7;
    private javax.swing.JButton button8;
    private javax.swing.JButton button9;
    private javax.swing.JButton buttonHash;
    private javax.swing.JButton buttonStar;
    private javax.swing.JButton callButton;
    private javax.swing.JLabel callingLabel;
    private javax.swing.JPanel callingPanel;
    private javax.swing.JLabel cancelLabel;
    private javax.swing.JPanel cancelPanel;
    private javax.swing.JTextField clientIPField;
    private javax.swing.JLabel clientIPLabel;
    private javax.swing.JTextField clientPortField;
    private javax.swing.JLabel clientPortLabel;
    private javax.swing.JPanel colorMaskPanel;
    private javax.swing.JPanel configTabPanel;
    private javax.swing.JLabel connectingLabel;
    private javax.swing.JPanel connectingPanel;
    private javax.swing.JCheckBox debugCheckBox;
    private javax.swing.JLabel debugLabel;
    private javax.swing.JTextField destinationField;
    private javax.swing.JPanel dialPanel;
    private javax.swing.JPanel displayPanel;
    private javax.swing.JPanel displayTabPanel;
    private javax.swing.JTextField domainField;
    private javax.swing.JLabel domainLabel;
    private javax.swing.JButton endButton;
    private javax.swing.JTextField filenameField;
    private javax.swing.JLabel idleLabel;
    private javax.swing.JPanel idlePanel;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JToggleButton line1ToggleButton;
    private javax.swing.JToggleButton line2ToggleButton;
    private javax.swing.JPanel logTabPanel;
    private javax.swing.ButtonGroup lookAndFeelGroup;
    private javax.swing.JRadioButton lookAndFeelRButtonGTK;
    private javax.swing.JRadioButton lookAndFeelRButtonMotif;
    private javax.swing.JRadioButton lookAndFeelRButtonNimbus;
    private javax.swing.JRadioButton lookAndFeelRButtonWindows;
    private javax.swing.JToggleButton muteAudioToggleButton;
    private javax.swing.JLabel muteLabel;
    private javax.swing.JPanel mutePanel;
    private javax.swing.JLabel onLabel;
    private javax.swing.JPanel onPanel;
    private javax.swing.JButton poolButton;
    private javax.swing.JLabel primaryStatusDetailsLabel;
    private javax.swing.JLabel primaryStatusLabel;
    private javax.swing.JLabel proxyInfoLabel;
    private javax.swing.JTextField pubIPField;
    private javax.swing.JLabel pubIPLabel;
    private javax.swing.JToggleButton randomRingResponseToggleButton;
    private javax.swing.JCheckBox registerCheckBox;
    private javax.swing.JLabel registerLabel;
    private javax.swing.JToggleButton registerToggleButton;
    private javax.swing.JLabel registeredLabel;
    private javax.swing.JPanel registeredPanel;
    private javax.swing.JLabel ringingLabel;
    private javax.swing.JPanel ringingPanel;
    private javax.swing.JButton saveProxyConfigButton;
    private javax.swing.JLabel secondaryStatusDetailsLabel;
    private javax.swing.JLabel secondaryStatusLabel;
    private javax.swing.JTextField serverIPField;
    private javax.swing.JLabel serverIPLabel;
    private javax.swing.JTextField serverPortField;
    private javax.swing.JLabel serverPortLabel;
    private javax.swing.JLabel softphoneInfoLabel;
    private javax.swing.JTabbedPane tabPanel;
    private javax.swing.JLabel talkingLabel;
    private javax.swing.JPanel talkingPanel;
    private javax.swing.JPanel telephonePanel;
    private javax.swing.JTextArea textLogArea;
    private javax.swing.JScrollPane textLogAreaScrollPane;
    private javax.swing.JPasswordField toegangField;
    private javax.swing.JLabel toegangLabel;
    private javax.swing.JTextField usernameField;
    private javax.swing.JLabel usernameLabel;
    // End of variables declaration//GEN-END:variables

    private void chooseDigit(String digit) {
        String content = destinationField.getText();
        content += digit;
        destinationField.setText(content);
    }

    private void enableTelephoneUsage()
    {
            tabPanel.setEnabled(true);
            saveProxyConfigButton.setEnabled(true);
            registerToggleButton.setEnabled(true);
            registerToggleButton.setSelected(false);
            registerToggleButton.setForeground(Color.BLACK);
            autoAnswerToggleButton.setEnabled(true);
            autoAnswerToggleButton.setSelected(false);
            autoAnswerToggleButton.setForeground(Color.BLACK);
            randomRingResponseToggleButton.setEnabled(true);
            randomRingResponseToggleButton.setSelected(false);
            randomRingResponseToggleButton.setForeground(Color.BLACK);
            autoCancelToggleButton.setEnabled(true);
            autoCancelToggleButton.setSelected(false);
            autoCancelToggleButton.setForeground(Color.BLACK);
            muteAudioToggleButton.setEnabled(true);
            muteAudioToggleButton.setSelected(false);
            muteAudioToggleButton.setForeground(Color.BLACK);
            callButton.setEnabled(true);
            endButton.setEnabled(true);
            button0.setEnabled(true);
            button1.setEnabled(true);
            button2.setEnabled(true);
            button3.setEnabled(true);
            button4.setEnabled(true);
            button5.setEnabled(true);
            button6.setEnabled(true);
            button7.setEnabled(true);
            button8.setEnabled(true);
            button9.setEnabled(true);
            buttonStar.setEnabled(true);
            buttonHash.setEnabled(true);
            domainLabel.setEnabled(true);
            domainField.setEnabled(true);
            serverPortLabel.setEnabled(true);
            serverPortField.setEnabled(true);
            serverIPLabel.setEnabled(true);
            serverIPField.setEnabled(true);
            filenameField.setEnabled(true);
            usernameLabel.setEnabled(true);
            usernameField.setEnabled(true);
            toegangLabel.setEnabled(true);
            toegangField.setEnabled(true);
            registerLabel.setEnabled(true);
            registerCheckBox.setEnabled(true);
            destinationField.setEnabled(true);
    }

    private void disableTelephoneUsage()
    {
        tabPanel.setEnabled(false);
        displayTabPanel.setEnabled(false);
        logTabPanel.setEnabled(true);
        saveProxyConfigButton.setEnabled(false);
        registerToggleButton.setEnabled(false);
        autoAnswerToggleButton.setEnabled(false);
        randomRingResponseToggleButton.setEnabled(false);
        randomRingResponseToggleButton.setSelected(false);
        randomRingResponseToggleButton.setForeground(Color.BLACK);
        autoCancelToggleButton.setEnabled(false);
        muteAudioToggleButton.setEnabled(false);
        muteAudioToggleButton.setSelected(false);
        callButton.setEnabled(false);
        endButton.setEnabled(false);
        button0.setEnabled(false);
        button1.setEnabled(false);
        button2.setEnabled(false);
        button3.setEnabled(false);
        button4.setEnabled(false);
        button5.setEnabled(false);
        button6.setEnabled(false);
        button7.setEnabled(false);
        button8.setEnabled(false);
        button9.setEnabled(false);
        buttonStar.setEnabled(false);
        buttonHash.setEnabled(false);
        domainLabel.setEnabled(false);
        domainField.setEnabled(false);
        serverPortLabel.setEnabled(false);
        serverPortField.setEnabled(false);
        serverIPLabel.setEnabled(false);
        serverIPField.setEnabled(false);
        filenameField.setEnabled(false);
        usernameLabel.setEnabled(false);
        usernameField.setEnabled(false);
        toegangLabel.setEnabled(false);
        toegangField.setEnabled(false);
        registerLabel.setEnabled(false);
        registerCheckBox.setEnabled(false);
        destinationField.setEnabled(false);

    }

    private void enableConfigurationUsage()
    {
            tabPanel.setEnabled(true);
            logTabPanel.setEnabled(true);
            clientIPLabel.setEnabled(true);
            clientIPField.setEnabled(true);
            pubIPLabel.setEnabled(true);
            pubIPField.setEnabled(true);
            clientPortLabel.setEnabled(true);
            clientPortField.setEnabled(true);
            registerLabel.setEnabled(true);
            registerCheckBox.setEnabled(true);
            saveProxyConfigButton.setEnabled(true);
    }

    private void disableConfigurationUsage()
    {
        tabPanel.setEnabled(false);
        logTabPanel.setEnabled(false);
        clientIPLabel.setEnabled(false);
        clientIPField.setEnabled(false);
        pubIPLabel.setEnabled(false);
        pubIPField.setEnabled(false);
        clientPortLabel.setEnabled(false);
        clientPortField.setEnabled(false);
        registerLabel.setEnabled(false);
        registerCheckBox.setEnabled(false);
    }

    /**
     *
     */
    public void applicationFinished()
    {
        dispose();
    }

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
    public void sipstateUpdate(final int sipstateParam, final int lastsipstateParam, final int loginstateParam, final int softphoneActivityParam, final int softPhoneInstanceIdParam, final Destination destinationParam) {    }

    /**
     *
     * @param responseCodeParam
     * @param responseReasonPhraseParam
     * @param softPhoneInstanceIdParam
     * @param destinationParam
     */
    @Override
    public void responseUpdate(int responseCodeParam, String responseReasonPhraseParam, int softPhoneInstanceIdParam, Destination destinationParam) {    }

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
    public static String getWindowTitle()	    { return Vergunning.BRAND + " " + THISPRODUCT + " " + VERSION; }

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
    public static String getVersion()		    { return VERSION; }

    private       void   setDebugging(boolean enableParam, int debuggingLevelParam) { if (softPhone1 != null) {softPhone1.setDebugging(enableParam, debuggingLevelParam);} else {debugCheckBox.setSelected(false);}}

    /**
     *
     * @param args
     */
    public static void main(String args[])
    {
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            @Override
            public void run() {
                EPhone mySoftPhoneGUI = new EPhone();
                mySoftPhoneGUI.setVisible(true);
            }
        });
    }

    /**
     *
     * @param messageParam
     * @param valueParam
     */
    @Override
    public void feedback(String messageParam, int valueParam) {    }
}

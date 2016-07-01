import datasets.SpeakerData;
import datasets.DisplayData;
import datasets.Destination;
import java.net.*;
import javax.media.NoDataSourceException;
import javax.media.NoProcessorException;
import javax.media.format.UnsupportedFormatException;

/**
 *
 * @author ron
 */
public class SoundStreamerGUI extends javax.swing.JFrame implements UserInterface {

    private InetAddress myIP = null;

    /**
     *
     */
    public static String bufferDisplay = null;
    private SoundStreamer soundStreamer = null;
    private boolean powerOn = false;
    private String[] status = null;

    /**
     *
     * @param sourceIPParam
     * @param sourcePortParam
     * @param destIPParam
     * @param destPortParam
     */
    public SoundStreamerGUI(String sourceIPParam, String sourcePortParam, String destIPParam, String destPortParam) // Constructor
    {
        initComponents();
        try { myIP = InetAddress.getLocalHost(); } catch( UnknownHostException error) { showStatus("Error: InetAddress.getLocalHost(): " + error.getMessage(), true, true); }
        sourceIPField.setText(sourceIPParam);
        sourcePortField.setText(sourcePortParam);
        destinationIPField.setText(destIPParam);
        destinationPortField.setText(destPortParam);
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
                if ( args.length != 4)
                {
//                    System.out.println("Usage: SoundStreamerGUI \"sourceIP\" \"sourcePort\" \"destIP\" \"destPort\"\n"); return;
                    String myIPString = null; try { myIPString = InetAddress.getLocalHost().getHostAddress(); } catch( UnknownHostException error) {  }
                    new SoundStreamerGUI(myIPString , "0" , myIPString , "0").setVisible(true);
                }
                else
                {
                    new SoundStreamerGUI(args[0] , args[1] , args[2] , args[3]).setVisible(true);
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        powerButton = new javax.swing.JButton();
        statusDisplayPane = new javax.swing.JScrollPane();
        logDisplay = new javax.swing.JEditorPane();
        statusDisplayPane1 = new javax.swing.JScrollPane();
        statusDisplay = new javax.swing.JEditorPane();
        sourcePanel = new javax.swing.JPanel();
        sourceIPLabel = new javax.swing.JLabel();
        sourceIPField = new javax.swing.JTextField();
        sourcePortLabel = new javax.swing.JLabel();
        sourcePortField = new javax.swing.JTextField();
        destinationPanel = new javax.swing.JPanel();
        destinationIPLabel = new javax.swing.JLabel();
        destinationIPField = new javax.swing.JTextField();
        destinationPortLabel = new javax.swing.JLabel();
        destinationPortField = new javax.swing.JTextField();
        updateDestinationButton = new javax.swing.JButton();
        mediaPanel = new javax.swing.JPanel();
        audioFormatLabel = new javax.swing.JLabel();
        audioFormatBox = new javax.swing.JComboBox();
        filenameField = new javax.swing.JTextField();
        audioFormatLabel1 = new javax.swing.JLabel();
        streamButtonsPanel = new javax.swing.JPanel();
        startStreamButton = new javax.swing.JButton();
        stopStreamButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        powerButton.setFont(new java.awt.Font("STHeiti", 0, 13));
        powerButton.setText("Power");
        powerButton.setName("powerButton"); // NOI18N
        powerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                powerButtonActionPerformed(evt);
            }
        });

        statusDisplayPane.setHorizontalScrollBar(null);
        statusDisplayPane.setName("statusDisplayPane"); // NOI18N

        logDisplay.setBackground(new java.awt.Color(230, 230, 230));
        logDisplay.setEditable(false);
        logDisplay.setFont(new java.awt.Font("Courier New", 0, 8)); // NOI18N
        logDisplay.setDragEnabled(false);
        logDisplay.setMinimumSize(new java.awt.Dimension(600, 200));
        logDisplay.setName("logDisplay"); // NOI18N
        logDisplay.setPreferredSize(new java.awt.Dimension(600, 200));
        logDisplay.setRequestFocusEnabled(false);
        logDisplay.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                logDisplayMouseClicked(evt);
            }
        });
        statusDisplayPane.setViewportView(logDisplay);

        statusDisplayPane1.setHorizontalScrollBar(null);
        statusDisplayPane1.setName("statusDisplayPane1"); // NOI18N

        statusDisplay.setBackground(new java.awt.Color(230, 230, 230));
        statusDisplay.setEditable(false);
        statusDisplay.setFont(new java.awt.Font("STHeiti", 0, 8)); // NOI18N
        statusDisplay.setDragEnabled(false);
        statusDisplay.setMinimumSize(new java.awt.Dimension(600, 200));
        statusDisplay.setName("statusDisplay"); // NOI18N
        statusDisplay.setPreferredSize(new java.awt.Dimension(600, 200));
        statusDisplay.setRequestFocusEnabled(false);
        statusDisplayPane1.setViewportView(statusDisplay);

        sourcePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Source", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("STHeiti", 0, 13))); // NOI18N
        sourcePanel.setFont(new java.awt.Font("STHeiti", 0, 13));
        sourcePanel.setName("sourcePanel"); // NOI18N

        sourceIPLabel.setFont(new java.awt.Font("STHeiti", 0, 13));
        sourceIPLabel.setText("Source IP");
        sourceIPLabel.setName("sourceIPLabel"); // NOI18N

        sourceIPField.setFont(new java.awt.Font("STHeiti", 0, 13));
        sourceIPField.setName("sourceIPField"); // NOI18N

        sourcePortLabel.setFont(new java.awt.Font("STHeiti", 0, 13));
        sourcePortLabel.setText("Source Port");
        sourcePortLabel.setName("sourcePortLabel"); // NOI18N

        sourcePortField.setFont(new java.awt.Font("STHeiti", 0, 13));
        sourcePortField.setName("sourcePortField"); // NOI18N

        org.jdesktop.layout.GroupLayout sourcePanelLayout = new org.jdesktop.layout.GroupLayout(sourcePanel);
        sourcePanel.setLayout(sourcePanelLayout);
        sourcePanelLayout.setHorizontalGroup(
            sourcePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(sourcePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(sourcePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(sourcePortLabel)
                    .add(sourceIPLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 113, Short.MAX_VALUE)
                .add(sourcePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(sourcePortField)
                    .add(sourceIPField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE))
                .addContainerGap())
        );
        sourcePanelLayout.setVerticalGroup(
            sourcePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(sourcePanelLayout.createSequentialGroup()
                .add(sourcePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(sourceIPLabel)
                    .add(sourceIPField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(sourcePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(sourcePortLabel)
                    .add(sourcePortField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        destinationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Media", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("STHeiti", 0, 13))); // NOI18N
        destinationPanel.setFont(new java.awt.Font("STHeiti", 0, 13));
        destinationPanel.setName("destinationPanel"); // NOI18N

        destinationIPLabel.setFont(new java.awt.Font("STHeiti", 0, 13));
        destinationIPLabel.setText("Dest IP");
        destinationIPLabel.setName("destinationIPLabel"); // NOI18N

        destinationIPField.setFont(new java.awt.Font("STHeiti", 0, 13));
        destinationIPField.setName("destinationIPField"); // NOI18N

        destinationPortLabel.setFont(new java.awt.Font("STHeiti", 0, 13));
        destinationPortLabel.setText("Dest Port");
        destinationPortLabel.setName("destinationPortLabel"); // NOI18N

        destinationPortField.setFont(new java.awt.Font("STHeiti", 0, 13));
        destinationPortField.setName("destinationPortField"); // NOI18N

        updateDestinationButton.setFont(new java.awt.Font("STHeiti", 0, 13));
        updateDestinationButton.setText("Update");
        updateDestinationButton.setName("updateDestinationButton"); // NOI18N
        updateDestinationButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                updateDestinationButtonMouseClicked(evt);
            }
        });

        org.jdesktop.layout.GroupLayout destinationPanelLayout = new org.jdesktop.layout.GroupLayout(destinationPanel);
        destinationPanel.setLayout(destinationPanelLayout);
        destinationPanelLayout.setHorizontalGroup(
            destinationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, destinationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(destinationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(destinationIPLabel)
                    .add(destinationPortLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 130, Short.MAX_VALUE)
                .add(destinationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, updateDestinationButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(destinationPortField)
                    .add(destinationIPField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 206, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        destinationPanelLayout.setVerticalGroup(
            destinationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(destinationPanelLayout.createSequentialGroup()
                .add(destinationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(destinationIPLabel)
                    .add(destinationIPField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(destinationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(destinationPortLabel)
                    .add(destinationPortField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(updateDestinationButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 35, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        mediaPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Media", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("STHeiti", 0, 13))); // NOI18N
        mediaPanel.setFont(new java.awt.Font("STHeiti", 0, 13));
        mediaPanel.setName("mediaPanel"); // NOI18N

        audioFormatLabel.setFont(new java.awt.Font("STHeiti", 0, 13));
        audioFormatLabel.setText("Audio Format");
        audioFormatLabel.setName("audioFormatLabel"); // NOI18N

        audioFormatBox.setFont(new java.awt.Font("STHeiti", 0, 13));
        audioFormatBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "GSM_RTP", "LINEAR" }));
        audioFormatBox.setName("audioFormatBox"); // NOI18N

        filenameField.setFont(new java.awt.Font("STHeiti", 0, 13));
        filenameField.setText("file://var/tmp/softphone.wav");
        filenameField.setName("filenameField"); // NOI18N

        audioFormatLabel1.setFont(new java.awt.Font("STHeiti", 0, 13));
        audioFormatLabel1.setText("Media File");
        audioFormatLabel1.setName("audioFormatLabel1"); // NOI18N

        org.jdesktop.layout.GroupLayout mediaPanelLayout = new org.jdesktop.layout.GroupLayout(mediaPanel);
        mediaPanel.setLayout(mediaPanelLayout);
        mediaPanelLayout.setHorizontalGroup(
            mediaPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mediaPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(mediaPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(audioFormatLabel)
                    .add(audioFormatLabel1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 98, Short.MAX_VALUE)
                .add(mediaPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(filenameField)
                    .add(audioFormatBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 207, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        mediaPanelLayout.setVerticalGroup(
            mediaPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mediaPanelLayout.createSequentialGroup()
                .add(mediaPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(audioFormatLabel)
                    .add(audioFormatBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(mediaPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(audioFormatLabel1)
                    .add(filenameField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        streamButtonsPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        streamButtonsPanel.setName("streamButtonsPanel"); // NOI18N

        startStreamButton.setFont(new java.awt.Font("STHeiti", 0, 13));
        startStreamButton.setText("Start Stream");
        startStreamButton.setName("startStreamButton"); // NOI18N
        startStreamButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startStreamButtonActionPerformed(evt);
            }
        });

        stopStreamButton.setFont(new java.awt.Font("STHeiti", 0, 13));
        stopStreamButton.setText("Stop Stream");
        stopStreamButton.setName("stopStreamButton"); // NOI18N
        stopStreamButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopStreamButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout streamButtonsPanelLayout = new org.jdesktop.layout.GroupLayout(streamButtonsPanel);
        streamButtonsPanel.setLayout(streamButtonsPanelLayout);
        streamButtonsPanelLayout.setHorizontalGroup(
            streamButtonsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, streamButtonsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(startStreamButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 150, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 101, Short.MAX_VALUE)
                .add(stopStreamButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 150, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        streamButtonsPanelLayout.setVerticalGroup(
            streamButtonsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(streamButtonsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(streamButtonsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(startStreamButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 71, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(stopStreamButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 71, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(sourcePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(statusDisplayPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 415, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, statusDisplayPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 415, Short.MAX_VALUE)
                            .add(destinationPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, mediaPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(streamButtonsPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(powerButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 150, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(123, 123, 123))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(powerButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(statusDisplayPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 105, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(statusDisplayPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(sourcePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(destinationPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(mediaPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(streamButtonsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void logDisplayMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logDisplayMouseClicked
        resetStatusDisplay();
}//GEN-LAST:event_logDisplayMouseClicked

    private void startStreamButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startStreamButtonActionPerformed
        try { soundStreamer.startStreamer(destinationIPField.getText(), destinationPortField.getText(), filenameField.getText().toString(), audioFormatBox.getSelectedIndex()); }
        catch (NoDataSourceException ex)        {        }
        catch (NoProcessorException ex)         {        }
        catch (UnsupportedFormatException ex)   {        }
    }//GEN-LAST:event_startStreamButtonActionPerformed

    private void stopStreamButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopStreamButtonActionPerformed
        soundStreamer.stopMedia();
        //myVoiceTool.startListener(sourceIPField.getText(), sourcePortField.getText(), destIPField.getText(), destPortField.getText());
    }//GEN-LAST:event_stopStreamButtonActionPerformed

    private void powerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_powerButtonActionPerformed
        if ( powerOn == false ) // powering on
        {
            soundStreamer = new SoundStreamer(this,0);
            status = soundStreamer.startListener(sourceIPField.getText(), Integer.parseInt(sourcePortField.getText()), destinationIPField.getText(), Integer.parseInt(destinationPortField.getText()));
            if (status[0].equals("1")) {logToApplication(status[1]);} else {this.sourcePortField.setText(status[1]);}

            powerOn = true;

            sourceIPField.setEnabled(false);
            sourcePortField.setEditable(false);
            showStatus("Media listener started.", true, true);

        }
        else if (powerOn == true) // powering off
        {
            powerOn = false;

            soundStreamer.stopMedia();
            soundStreamer.stopListener();
            
            sourceIPField.setEnabled(true);
            sourcePortField.setEditable(true);
            sourcePortField.setText("0");
            showStatus("Power Off", true, true);
//            resetStatusDisplay();
        }

    }//GEN-LAST:event_powerButtonActionPerformed

        private void updateDestinationButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_updateDestinationButtonMouseClicked
            status = soundStreamer.updateDestination(destinationIPField.getText(),destinationPortField.getText());
            if (status[0].equals("1"))
            {
                logToApplication("Error: " + status[1]);
            }
            else
            {
                logToApplication("Succes: " + status[1]);
            }
        }//GEN-LAST:event_updateDestinationButtonMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox audioFormatBox;
    private javax.swing.JLabel audioFormatLabel;
    private javax.swing.JLabel audioFormatLabel1;
    private javax.swing.JTextField destinationIPField;
    private javax.swing.JLabel destinationIPLabel;
    private javax.swing.JPanel destinationPanel;
    private javax.swing.JTextField destinationPortField;
    private javax.swing.JLabel destinationPortLabel;
    private javax.swing.JTextField filenameField;
    private static javax.swing.JEditorPane logDisplay;
    private javax.swing.JPanel mediaPanel;
    private javax.swing.JButton powerButton;
    private javax.swing.JTextField sourceIPField;
    private javax.swing.JLabel sourceIPLabel;
    private javax.swing.JPanel sourcePanel;
    private javax.swing.JTextField sourcePortField;
    private javax.swing.JLabel sourcePortLabel;
    private javax.swing.JButton startStreamButton;
    private static javax.swing.JEditorPane statusDisplay;
    private javax.swing.JScrollPane statusDisplayPane;
    private javax.swing.JScrollPane statusDisplayPane1;
    private javax.swing.JButton stopStreamButton;
    private javax.swing.JPanel streamButtonsPanel;
    private javax.swing.JButton updateDestinationButton;
    // End of variables declaration//GEN-END:variables

    /**
     *
     * @param displayParam
     */
    @Override
    public void phoneDisplay(DisplayData displayParam)
    {
        //bufferDisplay += displaymessage + "\n";
        //logDisplay.setText(bufferDisplay);
    }

    /**
     *
     * @param logmessage
     */
    @Override
    public void logToApplication(String logmessage)
    {
        bufferDisplay += logmessage + "\n";
        logDisplay.setText(bufferDisplay);
    }

    /**
     *
     * @param logmessage
     */
    @Override
    public void logToFile(String logmessage)
    {
        bufferDisplay += logmessage + "\n";
        logDisplay.setText(bufferDisplay);
    }

    /**
     *
     */
    public static void resetDisplay()
    {
        bufferDisplay = "";
        logDisplay.setText(bufferDisplay);
    }

    /**
     *
     * @param messageParam
     * @param logToApplicationParam
     * @param logToFileParam
     */
    @Override
    public void showStatus(String messageParam, boolean logToApplicationParam, boolean logToFileParam)
    {
        statusDisplay.setText(messageParam);
    }

    /**
     *
     */
    public static void resetStatusDisplay()
    {
        logDisplay.setText("");

    }

    /**
     *
     * @param statusParam
     */
    public void applicationConfigurationStatus(String statusParam) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     *
     */
    public void applicationFinished() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     *
     */
    @Override
    public void resetLog() {
        throw new UnsupportedOperationException("Not supported yet.");
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
    //public void sipstateUpdate(Integer offTally, Integer idleTally, Integer wait_provTally, Integer wait_finalTally, Integer wait_ackTally, Integer ringingTally, Integer establishedTally) {
    public void sipstateUpdate(final int sipstateParam, final int lastsipstateParam, final int loginstateParam, final int softphoneActivityParam, final int softPhoneInstanceIdParam, final Destination destinationParam) {    }

    /**
     *
     * @param speakerParam
     */
    @Override
    public void speaker(SpeakerData speakerParam) {
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
     * @param messageParam
     * @param valueParam
     */
    @Override
    public void feedback(String messageParam, int valueParam) {    }
    
//    public void main(String sourceIPParam, String sourcePortParam, String destIPParam, String destPortParam) {
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() { new VoiceToolGUI(sourceIPField,  sourcePortField,  destIPField,  destPort).setVisible(true); }
//        });
//    }
}

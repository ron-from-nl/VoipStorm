import datasets.TimeTool;
import javax.swing.SwingConstants;
import java.util.Calendar;
//import data.TimeWindow2;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author ron
 */
public class CampaignCalendar extends javax.swing.JFrame {

//    private TimeWindow2 timeWindow;
    private TimeTool timeTool;
    private Calendar calendar;
    private Manager manager;

    /**
     *
     */
    public CampaignCalendar()
    {
        try { UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel"); } catch (ClassNotFoundException ex) {} catch (InstantiationException ex) {} catch (IllegalAccessException ex) {} catch (UnsupportedLookAndFeelException ex) {}

        initComponents();

        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        int winWidth = (int)getWidth();
        int winHeight = (int)getHeight();
        int posX = Math.round((screenDim.width / 2) - (winWidth / 2));
        int posY = Math.round((screenDim.height / 2) - (winHeight / 2));
        setLocation(posX, posY);

//        timeWindow = new TimeWindow2();
        timeTool = new TimeTool();
        calendar = Calendar.getInstance();
//        calendar.setTimeZone(TimeZone.getDefault());
        hourSlider.setValue(calendar.get(Calendar.HOUR_OF_DAY));
        minuteSlider.setValue(calendar.get(Calendar.MINUTE));
        setTimeField(calendar);
    }

    /**
     *
     * @param eCallCenterGUIParam
     */
    public CampaignCalendar(Manager eCallCenterGUIParam)
    {
        try { UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel"); } catch (ClassNotFoundException ex) {} catch (InstantiationException ex) {} catch (IllegalAccessException ex) {} catch (UnsupportedLookAndFeelException ex) {}

        initComponents();

        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        int winWidth = (int)getWidth();
        int winHeight = (int)getHeight();
        int posX = Math.round((screenDim.width / 2) - (winWidth / 2));
        int posY = Math.round((screenDim.height / 2) - (winHeight / 2));
        setLocation(posX, posY);

//        timeWindow = new TimeWindow2();
        timeTool = new TimeTool();
        calendar = Calendar.getInstance();
//        calendar.setTimeZone(TimeZone.getDefault());
        manager = eCallCenterGUIParam;
        hourSlider.setValue(calendar.get(Calendar.HOUR_OF_DAY));
        minuteSlider.setValue(calendar.get(Calendar.MINUTE));
        setTimeField(calendar);
    }

    /**
     *
     * @param eCallCenterGUIParam
     * @param epochParam
     */
    public CampaignCalendar(Manager eCallCenterGUIParam, Long epochParam)
    {
        this(eCallCenterGUIParam);
        calendar.setTimeInMillis(epochParam);
        dateChooserPanel.setSelectedDate(calendar);
        hourSlider.setValue(calendar.get(Calendar.HOUR_OF_DAY));
        minuteSlider.setValue(calendar.get(Calendar.MINUTE));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dateSelectorPanel = new javax.swing.JPanel();
        dateChooserPanel = new datechooser.beans.DateChooserPanel();
        timeSelectorPanel = new javax.swing.JPanel();
        hourSlider = new javax.swing.JSlider();
        minuteSlider = new javax.swing.JSlider();
        timeField = new javax.swing.JTextField();
        doneButton = new javax.swing.JButton();

        setBackground(new java.awt.Color(216, 216, 222));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        dateSelectorPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Start Date Selector", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("STHeiti", 0, 14))); // NOI18N
        dateSelectorPanel.setFont(new java.awt.Font("STHeiti", 0, 12));

        dateChooserPanel.setCurrentView(new datechooser.view.appearance.AppearancesList("Bordered",
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
    dateChooserPanel.setLocale(new java.util.Locale("en", "", ""));
    dateChooserPanel.setNavigateFont(new java.awt.Font("STHeiti", java.awt.Font.PLAIN, 10));
    dateChooserPanel.setBehavior(datechooser.model.multiple.MultyModelBehavior.SELECT_SINGLE);
    dateChooserPanel.addSelectionChangedListener(new datechooser.events.SelectionChangedListener() {
        public void onSelectionChange(datechooser.events.SelectionChangedEvent evt) {
            dateChooserPanelOnSelectionChange(evt);
        }
    });

    org.jdesktop.layout.GroupLayout dateSelectorPanelLayout = new org.jdesktop.layout.GroupLayout(dateSelectorPanel);
    dateSelectorPanel.setLayout(dateSelectorPanelLayout);
    dateSelectorPanelLayout.setHorizontalGroup(
        dateSelectorPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(dateSelectorPanelLayout.createSequentialGroup()
            .addContainerGap()
            .add(dateChooserPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 282, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addContainerGap())
    );
    dateSelectorPanelLayout.setVerticalGroup(
        dateSelectorPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(dateSelectorPanelLayout.createSequentialGroup()
            .add(dateChooserPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 226, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addContainerGap())
    );

    timeSelectorPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Start Time Selector", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("STHeiti", 0, 14))); // NOI18N
    timeSelectorPanel.setFont(new java.awt.Font("STHeiti", 0, 13));

    hourSlider.setFont(new java.awt.Font("STHeiti", 0, 10));
    hourSlider.setMajorTickSpacing(1);
    hourSlider.setMaximum(23);
    hourSlider.setMinorTickSpacing(1);
    hourSlider.setOrientation(javax.swing.JSlider.VERTICAL);
    hourSlider.setPaintLabels(true);
    hourSlider.setPaintTicks(true);
    hourSlider.setSnapToTicks(true);
    hourSlider.setValue(12);
    hourSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent evt) {
            hourSliderStateChanged(evt);
        }
    });

    minuteSlider.setFont(new java.awt.Font("STHeiti", 0, 10));
    minuteSlider.setMajorTickSpacing(5);
    minuteSlider.setMaximum(59);
    minuteSlider.setMinorTickSpacing(1);
    minuteSlider.setOrientation(javax.swing.JSlider.VERTICAL);
    minuteSlider.setPaintLabels(true);
    minuteSlider.setPaintTicks(true);
    minuteSlider.setSnapToTicks(true);
    minuteSlider.setValue(30);
    minuteSlider.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent evt) {
            minuteSliderStateChanged(evt);
        }
    });

    timeField.setBackground(new java.awt.Color(0, 0, 0));
    timeField.setFont(new java.awt.Font("Synchro LET", 0, 24));
    timeField.setForeground(new java.awt.Color(255, 204, 0));
    timeField.setText("TIME");
    timeField.setHorizontalAlignment(SwingConstants.CENTER);

    org.jdesktop.layout.GroupLayout timeSelectorPanelLayout = new org.jdesktop.layout.GroupLayout(timeSelectorPanel);
    timeSelectorPanel.setLayout(timeSelectorPanelLayout);
    timeSelectorPanelLayout.setHorizontalGroup(
        timeSelectorPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(timeSelectorPanelLayout.createSequentialGroup()
            .add(timeSelectorPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                .add(org.jdesktop.layout.GroupLayout.LEADING, timeSelectorPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .add(timeField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE))
                .add(org.jdesktop.layout.GroupLayout.LEADING, timeSelectorPanelLayout.createSequentialGroup()
                    .add(41, 41, 41)
                    .add(hourSlider, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                    .add(minuteSlider, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
            .addContainerGap())
    );
    timeSelectorPanelLayout.setVerticalGroup(
        timeSelectorPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(org.jdesktop.layout.GroupLayout.TRAILING, timeSelectorPanelLayout.createSequentialGroup()
            .add(timeField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 35, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(timeSelectorPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(minuteSlider, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE)
                .add(hourSlider, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE)))
    );

    doneButton.setFont(new java.awt.Font("STHeiti", 1, 18));
    doneButton.setText("Done");
    doneButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            doneButtonActionPerformed(evt);
        }
    });

    org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
        layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
            .addContainerGap()
            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                .add(org.jdesktop.layout.GroupLayout.LEADING, doneButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 512, Short.MAX_VALUE)
                .add(layout.createSequentialGroup()
                    .add(dateSelectorPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(timeSelectorPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
            .addContainerGap())
    );
    layout.setVerticalGroup(
        layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
        .add(layout.createSequentialGroup()
            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(timeSelectorPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(org.jdesktop.layout.GroupLayout.TRAILING, dateSelectorPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
            .add(doneButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 55, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addContainerGap())
    );

    pack();
    }// </editor-fold>//GEN-END:initComponents

    private void dateChooserPanelOnSelectionChange(datechooser.events.SelectionChangedEvent evt) {//GEN-FIRST:event_dateChooserPanelOnSelectionChange
        calendar = dateChooserPanel.getSelectedDate();
        calendar.set(Calendar.HOUR_OF_DAY, hourSlider.getValue());
        calendar.set(Calendar.MINUTE, minuteSlider.getValue());
        calendar.set(Calendar.SECOND, (int)0);
        setTimeField(calendar);
    }//GEN-LAST:event_dateChooserPanelOnSelectionChange

    private void hourSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_hourSliderStateChanged
        calendar.set(Calendar.HOUR_OF_DAY, hourSlider.getValue());
        calendar.set(Calendar.MINUTE, minuteSlider.getValue());
        calendar.set(Calendar.SECOND, (int)0);
        setTimeField(calendar);
    }//GEN-LAST:event_hourSliderStateChanged

    private void minuteSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_minuteSliderStateChanged
        calendar.set(Calendar.HOUR_OF_DAY, hourSlider.getValue());
        calendar.set(Calendar.MINUTE, minuteSlider.getValue());
        calendar.set(Calendar.SECOND, (int)0);
        setTimeField(calendar);
    }//GEN-LAST:event_minuteSliderStateChanged

    private void doneButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doneButtonActionPerformed
        setVisible(false);
        manager.orderButtonsControl();
    }//GEN-LAST:event_doneButtonActionPerformed

    private void setTimeField(Calendar calendarParam)
    {
        timeField.setText(String.format("%02d", hourSlider.getValue()) + ":" + String.format("%02d", minuteSlider.getValue()));
        manager.orderDateField.setText(Long.toString(calendar.getTimeInMillis()));
//        if (calendar.getTimeInMillis() <= (Calendar.getInstance().getTimeInMillis() + Math.round(manager.orderDestinationsQuantitySlider.getValue() * manager.preparationFactor)))
        if (calendar.getTimeInMillis() <= (Calendar.getInstance().getTimeInMillis() + Math.round(Integer.parseInt(manager.orderDestinationsQuantityField.getText()) * manager.preparationFactor)))
        {
            manager.confirmOrderButton.setEnabled(false);
        }
        else if ((manager.destinationTextArea.isEnabled()) && (manager.destinationTextArea.getLineCount() > 1) )
        {
            manager.confirmOrderButton.setEnabled(true);
        }
    }

    /**
     *
     * @param earliestTimewindowIndexParam
     */
    @SuppressWarnings("static-access")
    public void setStartTimeWindow(int earliestTimewindowIndexParam)
    {
        TimeTool timeTool = new TimeTool();
        hourSlider.setMinimum(timeTool.getTimeWindow(earliestTimewindowIndexParam).getStartHour());
        hourSlider.setMaximum(timeTool.getTimeWindow(earliestTimewindowIndexParam).getEndHour());
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public datechooser.beans.DateChooserPanel dateChooserPanel;
    private javax.swing.JPanel dateSelectorPanel;
    private javax.swing.JButton doneButton;
    javax.swing.JSlider hourSlider;
    javax.swing.JSlider minuteSlider;
    private javax.swing.JTextField timeField;
    private javax.swing.JPanel timeSelectorPanel;
    // End of variables declaration//GEN-END:variables

}

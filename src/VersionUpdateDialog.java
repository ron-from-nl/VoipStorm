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

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 *
 * @author ron
 */
public class VersionUpdateDialog extends javax.swing.JDialog
{

    /**
     *
     */
    public static final int RET_CANCEL = 0;

    /**
     *
     */
    public static final int RET_OK = 1;
    private int returnStatus = RET_CANCEL;

    /**
     *
     * @param parent
     * @param modal
     */
    public VersionUpdateDialog(java.awt.Frame parent, boolean modal)
    {
        super(parent, modal);
        initComponents();

        // Put window in Top-Center
        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        int winWidth = (int)getWidth();
        int winHeight = (int)getHeight();
        int posX = Math.round((screenDim.width - winWidth) / 2);
        int posY = Math.round((screenDim.height - winHeight) / 2);
        setLocation(posX, 0);
    }

    /**
     *
     * @return
     */
    public int getReturnStatus() {
        return returnStatus;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLayeredPane1 = new javax.swing.JLayeredPane();
        cancelButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();
        messageLabel = new javax.swing.JLabel();
        headerLabel = new javax.swing.JLabel();
        lightbulbLabel = new javax.swing.JLabel();
        imageLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        cancelButton.setBounds(180, 80, 86, 21);
        jLayeredPane1.add(cancelButton, javax.swing.JLayeredPane.DEFAULT_LAYER);

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        okButton.setBounds(90, 80, 86, 21);
        jLayeredPane1.add(okButton, javax.swing.JLayeredPane.DEFAULT_LAYER);

        messageLabel.setFont(new java.awt.Font("STHeiti", 0, 14));
        messageLabel.setForeground(new java.awt.Color(255, 255, 255));
        messageLabel.setText("Would you like to install and restart now ?");
        messageLabel.setBounds(50, 50, 290, 20);
        jLayeredPane1.add(messageLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        headerLabel.setFont(new java.awt.Font("STHeiti", 0, 36));
        headerLabel.setForeground(new java.awt.Color(255, 255, 255));
        headerLabel.setText("Software Update");
        headerLabel.setBounds(50, 10, 300, 40);
        jLayeredPane1.add(headerLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        lightbulbLabel.setFont(new java.awt.Font("STHeiti", 0, 13));
        lightbulbLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lightbulb.png"))); // NOI18N
        lightbulbLabel.setBounds(0, -10, 40, 90);
        jLayeredPane1.add(lightbulbLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        imageLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        imageLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/vsheader.jpg"))); // NOI18N
        imageLabel.setBounds(-10, -90, 650, 500);
        jLayeredPane1.add(imageLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jLayeredPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 359, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jLayeredPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        doClose(RET_OK);
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        doClose(RET_CANCEL);
    }//GEN-LAST:event_cancelButtonActionPerformed

    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        doClose(RET_CANCEL);
    }//GEN-LAST:event_closeDialog

    private void doClose(int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }

    /**
     *
     * @param args
     * @throws MalformedURLException
     * @throws IOException
     */
    public static void main(String args[])  throws java.net.MalformedURLException, java.io.IOException {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                VersionUpdateDialog dialog = null;
                dialog = new VersionUpdateDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel headerLabel;
    private javax.swing.JLabel imageLabel;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JLabel lightbulbLabel;
    private javax.swing.JLabel messageLabel;
    private javax.swing.JButton okButton;
    // End of variables declaration//GEN-END:variables
}

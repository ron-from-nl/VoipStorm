import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author ron
 */
public class FileBrowser extends javax.swing.JFrame {

    private String platform;
    private String fileSeparator;
    private File file;
    private File directory;
    private Manager manager;
    private ExtensionFilter extensionFilter;

    /**
     *
     */
    public FileBrowser() {
        initComponents();

        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        int winWidth = (int)getWidth();
        int winHeight = (int)getHeight();
        int posX = Math.round((screenDim.width / 2) - (winWidth / 2));
        int posY = Math.round((screenDim.height / 2) - (winHeight / 2));
        setLocation(posX, posY);

        file = new File("");
        directory = new File("");
        platform = System.getProperty("os.name").toLowerCase();
        if ( platform.indexOf("windows") != -1 ) { fileSeparator = "\\"; } else { fileSeparator = "/"; }
        try { directory = new File(new File(".").getCanonicalPath() + fileSeparator + "data" + fileSeparator + "sounds"); } catch (IOException ex) { }
        fileChooser.setCurrentDirectory(directory);
        fileChooser.setMultiSelectionEnabled(false);
        extensionFilter = new ExtensionFilter(".wav","Sound files (*.wav)");
        fileChooser.addChoosableFileFilter(extensionFilter);
        fileChooser.setFileFilter(extensionFilter);
    }

    /**
     *
     * @param eCallCenterManagerParam
     */
    public FileBrowser(Manager eCallCenterManagerParam) {
        this();
        manager = eCallCenterManagerParam;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fileChooser = new javax.swing.JFileChooser();

        fileChooser.setCurrentDirectory(null);
        fileChooser.setDialogTitle("Select SoundFile (*.wav)");
        fileChooser.setFileFilter(null);
        fileChooser.setFont(new java.awt.Font("STHeiti", 0, 13)); // NOI18N
        fileChooser.setToolTipText("");
        fileChooser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileChooserActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(fileChooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(fileChooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 394, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    @SuppressWarnings("static-access")
    private void fileChooserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileChooserActionPerformed
        manager.setSoundFile(fileChooser.getCurrentDirectory() + file.separator + fileChooser.getName(fileChooser.getSelectedFile()));
        setVisible(false);
        manager.updateOrder();
        manager.orderButtonsControl();
    }//GEN-LAST:event_fileChooserActionPerformed

    /**
     *
     * @param args
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new FileBrowser().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFileChooser fileChooser;
    // End of variables declaration//GEN-END:variables

}

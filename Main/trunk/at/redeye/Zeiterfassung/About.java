/*
 * About.java
 *
 * Created on 14. Juli 2009, 20:39
 */

package at.redeye.Zeiterfassung;

import at.redeye.FrameWork.base.BaseDialogDialog;
import at.redeye.FrameWork.base.Root;
import javax.swing.JFrame;

/**
 *
 * @author  martin
 */
public class About extends BaseDialogDialog {

    /** Creates new form About */
    public About(JFrame owner, Root root)
    {
        super(owner, root,"Über");
        initComponents();
        
        jLVersion.setText("Version " + Version.getVersion());

        if( at.redeye.Dongle.AppliactionModes.getAppliactionModes().isDemoVersion() )
            jLabelPic.setIcon(new javax.swing.ImageIcon(getClass().getResource("/at/redeye/Zeiterfassung/resources/icons/redeye15b-dev.png")));
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLTitle = new javax.swing.JLabel();
        jBCancel = new javax.swing.JButton();
        jLabelPic = new javax.swing.JLabel();
        jLVersion = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLTitle.setFont(new java.awt.Font("Dialog", 1, 18));
        jLTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLTitle.setText("Über das Zeiterfassungssystem");

        jBCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/at/redeye/FrameWork/base/resources/icons/fileclose.gif"))); // NOI18N
        jBCancel.setText("Schließen");
        jBCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBCancelActionPerformed(evt);
            }
        });

        jLabelPic.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelPic.setIcon(new javax.swing.ImageIcon(getClass().getResource("/at/redeye/Zeiterfassung/resources/icons/redeye15b.png"))); // NOI18N

        jLVersion.setFont(new java.awt.Font("Dialog", 1, 14));
        jLVersion.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLVersion.setText("Version");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLTitle, javax.swing.GroupLayout.DEFAULT_SIZE, 428, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabelPic, javax.swing.GroupLayout.DEFAULT_SIZE, 416, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jBCancel)
                        .addContainerGap())
                    .addComponent(jLVersion, javax.swing.GroupLayout.DEFAULT_SIZE, 428, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLTitle)
                .addGap(4, 4, 4)
                .addComponent(jLVersion)
                .addGap(22, 22, 22)
                .addComponent(jLabelPic, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                .addComponent(jBCancel)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void jBCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBCancelActionPerformed
    if( canClose() )    
        close();
}//GEN-LAST:event_jBCancelActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBCancel;
    private javax.swing.JLabel jLTitle;
    private javax.swing.JLabel jLVersion;
    private javax.swing.JLabel jLabelPic;
    // End of variables declaration//GEN-END:variables

}

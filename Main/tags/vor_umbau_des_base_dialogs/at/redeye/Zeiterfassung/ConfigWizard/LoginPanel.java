/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * LoginPanel.java
 *
 * Created on 08.05.2010, 21:17:05
 */

package at.redeye.Zeiterfassung.ConfigWizard;

import at.redeye.FrameWork.base.AutoMBox;
import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.base.wizards.WizardAction;
import at.redeye.FrameWork.base.wizards.WizardClientActionInterface;
import at.redeye.UserManagement.UserManagementInterface;
import at.redeye.UserManagement.bindtypes.DBPb;
import at.redeye.UserManagement.impl.UserDataHandling;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import org.apache.log4j.Logger;

/**
 *
 * @author martin
 */
public class LoginPanel extends javax.swing.JPanel
{		
    class EnterKeyListener implements KeyListener
    {
        @Override
        public void keyPressed(KeyEvent e) {

            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                jBLoginActionPerformed(null);
            }
        }

        public void keyTyped(KeyEvent e) {
            
            if (e.getKeyChar() == '\n') {
                return;
            }
            
            System.out.println("heere " + e);
            wizard_action.applyAction(WizardAction.WIZARD_ACTION_NEXT, false);
        }

        public void keyReleased(KeyEvent e) {}
    }

    WizardClientActionInterface wizard_action;
    Root root;
    UserManagementInterface um;
    public static Logger logger = Logger.getRootLogger();

    public LoginPanel(Root root, WizardClientActionInterface wizard_action) {
        initComponents();
        this.root = root;
        this.wizard_action = wizard_action;
        um = new UserDataHandling(root);        
        
        jtPasswd.addKeyListener(new EnterKeyListener());
        jTLogin.addKeyListener(new EnterKeyListener());
        jBLogin.addKeyListener(new EnterKeyListener());
    }

    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTLogin = new javax.swing.JTextField();
        jtPasswd = new javax.swing.JPasswordField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jBLogin = new javax.swing.JButton();

        jLabel1.setText("Login");

        jLabel2.setText("Passwort");

        jTextArea1.setColumns(20);
        jTextArea1.setEditable(false);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setText("Die verwendete Datenbank ist bereits vollständig eingerichtet, bitte melden Sie sich mit einem gültigen Benutzernamen und Passwort an um fortzufahren.");
        jTextArea1.setWrapStyleWord(true);
        jTextArea1.setOpaque(false);
        jScrollPane1.setViewportView(jTextArea1);

        jBLogin.setText("Anmelden");
        jBLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBLoginActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTLogin, javax.swing.GroupLayout.DEFAULT_SIZE, 291, Short.MAX_VALUE)
                            .addComponent(jtPasswd, javax.swing.GroupLayout.DEFAULT_SIZE, 291, Short.MAX_VALUE)))
                    .addComponent(jBLogin, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTLogin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jtPasswd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBLogin)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jBLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBLoginActionPerformed

        AutoMBox mb = new AutoMBox(this.getClass().getName()) {

            @Override
            public void do_stuff() throws Exception {
                DBPb pb = um.checkUserData(jTLogin.getText(), new String(jtPasswd.getPassword()), false);
                if (pb != null) {
                    root.setAktivUser(pb);
                    um.updateListeners();
                    logger.info("User " + root.getUserName() + " [Level "
                            + root.getUserPermissionLevel()
                            + "] successfully signed on!");

                    if( root.getUserPermissionLevel() ==  UserManagementInterface.UM_PERMISSIONLEVEL_ADMIN )
                    {
                        wizard_action.applyAction(WizardAction.WIZARD_ACTION_NEXT, true);
                    }
                    else
                    {
                        throw new Exception("Sie haben leider keine Administratorenrechte, ein fortführen des Assistenten " +
                                "ist leider nicht möglich, aber vielleicht auch gar nicht notwendig. " +
                                "Die Datenbank ist vollständig eingerichtet, klicken Sie auf \"Fertig\" um sich nun " +
                                "anzumelden." );
                    }
                }
            }
        };

        if( mb.isFailed() )
        {
             wizard_action.applyAction(WizardAction.WIZARD_ACTION_NEXT, false);
        }
    }//GEN-LAST:event_jBLoginActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBLogin;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTLogin;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JPasswordField jtPasswd;
    // End of variables declaration//GEN-END:variables
    



}

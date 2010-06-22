/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ChangeUserName.java
 *
 * Created on 20.06.2010, 12:54:36
 */

package at.redeye.Zeiterfassung;

import at.redeye.FrameWork.base.AutoMBox;
import at.redeye.FrameWork.base.BaseDialog;
import at.redeye.FrameWork.base.DefaultInsertOrUpdater;
import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.base.transaction.Transaction;
import at.redeye.FrameWork.widgets.GridLayout2;
import at.redeye.FrameWork.widgets.NoticeIfChangedTextField;
import at.redeye.UserManagement.UserManagementInterface;
import at.redeye.UserManagement.bindtypes.DBPb;
import at.redeye.UserManagement.impl.AdminDlg;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.JLabel;

/**
 *
 * @author martin
 */
public class ChangeUserName extends BaseDialog {

    DBPb pb = new DBPb();

    NoticeIfChangedTextField TTitle;
    NoticeIfChangedTextField TName;
    NoticeIfChangedTextField TSurname;

    public ChangeUserName(final Root root) {
        super(root, "Benutzername");

        initComponents();

        panel.setLayout(new GridLayout2(0,2) );

        TTitle   = new NoticeIfChangedTextField();
        TName    = new NoticeIfChangedTextField();
        TSurname = new NoticeIfChangedTextField();


        TTitle.setColumns(20);
        TName.setColumns(20);
        TSurname.setColumns(20);

        panel.add(new JLabel("Titel"));
        panel.add( TTitle );

        panel.add(new JLabel("Vorname"));
        panel.add( TName );

        panel.add(new JLabel("Name"));
        panel.add( TSurname );

        new AutoMBox(ChangeUserName.class.getName()) {

            @Override
            public void do_stuff() throws Exception {

                Transaction trans = getTransaction();

                Vector<DBPb> res = trans.fetchTable2(pb, "where " + trans.markColumn(pb.login) + "='" + root.getLogin() + "'");

                if( res.isEmpty() ) {

                    logger.error("loading default user failed, createing one");

                    HashMap<String, Object> map = new HashMap<String, Object>();

                    map.put("name", "Initiales Setup");
                    map.put("pwd", "  ---  ");
                    map.put("login", root.getLogin() );
                    map.put("plevel",
                            UserManagementInterface.UM_PERMISSIONLEVEL_ADMIN);
                    map.put("locked", UserManagementInterface.UM_ACCOUNT_UNLOCKED);
                    pb.consume(map);

                } else {
                    pb = res.get(0);
                }
            }
        };

        bindVar(TTitle, pb.title);
        bindVar(TName, pb.name);
        bindVar(TSurname, pb.surname);

        var_to_gui();

        setEdited(false);
    }

    @Override
    public void close()
    {
         AutoMBox mb = new AutoMBox(ChangeUserName.class.getName()) {

            @Override
            public void do_stuff() throws Exception {
                gui_to_var();

                Transaction trans = getTransaction();

                if (pb.id.getValue() == 0) {
                    pb.id.loadFromCopy(getNewSequenceValue(AdminDlg.UM_ID_SEQ));
                }

                DefaultInsertOrUpdater.insertOrUpdateValuesWithPrimKey(trans, pb, pb.hist, root.getUserName());

                trans.commit();
            }
        };

        if (!mb.isFailed()) {
            super.close();
        }
    }
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLTitle = new javax.swing.JLabel();
        jBCancel = new javax.swing.JButton();
        panel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLTitle.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLTitle.setText("Benutzername");

        jBCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/at/redeye/FrameWork/base/resources/icons/fileclose.gif"))); // NOI18N
        jBCancel.setText("Schließen");
        jBCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBCancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 340, Short.MAX_VALUE)
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 88, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLTitle, javax.swing.GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE)
                    .addComponent(jBCancel, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jBCancel)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jBCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBCancelActionPerformed

        close();

}//GEN-LAST:event_jBCancelActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBCancel;
    private javax.swing.JLabel jLTitle;
    private javax.swing.JPanel panel;
    // End of variables declaration//GEN-END:variables

}

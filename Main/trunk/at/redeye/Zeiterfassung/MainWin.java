/*
 * MainWin.java
 *
 * Created on 2. Januar 2009, 10:07
 */

package at.redeye.Zeiterfassung;

import java.util.Vector;

import javax.swing.JOptionPane;

import org.joda.time.DateMidnight;

import at.redeye.FrameWork.base.AutoLogger;
import at.redeye.FrameWork.base.AutoMBox;
import at.redeye.FrameWork.base.BaseDialog;
import at.redeye.FrameWork.base.ConnectionDialog;
import at.redeye.FrameWork.base.GlobalConfig;
import at.redeye.FrameWork.base.LocalConfig;
import at.redeye.FrameWork.base.LogWin;
import at.redeye.FrameWork.base.MemInfo;
import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.base.bindtypes.DBStrukt;
import at.redeye.FrameWork.utilities.HMSTime;
import at.redeye.FrameWork.utilities.Rounding;
import at.redeye.FrameWork.utilities.StringUtils;
import at.redeye.FrameWork.utilities.calendar.AustrianHolidays;
import at.redeye.FrameWork.utilities.calendar.GermanHolidays;
import at.redeye.FrameWork.utilities.calendar.HolidayMerger;
import at.redeye.FrameWork.utilities.calendar.SwitzerlandHolidays;
import at.redeye.FrameWork.widgets.calendarday.CalendarDay;
import at.redeye.FrameWork.widgets.calendarday.DayEventListener;
import at.redeye.UserManagement.UserManagementDialogs;
import at.redeye.UserManagement.UserManagementInterface;
import at.redeye.UserManagement.impl.UserDataHandling;
import at.redeye.Zeiterfassung.bindtypes.DBJobType;
import at.redeye.Zeiterfassung.bindtypes.DBUserPerMonth;

/**
 *
 * @author  martin
 */
public class MainWin extends BaseDialog implements DayEventListener, MonthSumInfo {
        
	private static final long serialVersionUID = 1L;
    private int mon = 1;
    private int year = 2009;
    private HolidayMerger merger = new HolidayMerger();
    private TimeEntryCache cache = new TimeEntryCache();
    private CalcMonthStuff month_stuff = null;

        /** Creates new form MainWin
         * @param root 
         */
    public MainWin(Root root) {
        super( root, "Zeiterfassung" );
        
        initComponents();
        
        month.setInfoRenderer(new TimeEntryRenderer(getTransaction(), root, cache,merger));
        
        DateMidnight today = new DateMidnight();
        
        year = today.getYear();        
        mon = today.getMonthOfYear();

        month_stuff = new CalcMonthStuff(month, getTransaction(), root);
        
        String res = root.getSetup().getLocalConfig("HolidaysAustria", 
                new Boolean(jCBHolidaysAustria.getState()).toString());
        
        jCBHolidaysAustria.setState(new Boolean(res));
        
        res = root.getSetup().getLocalConfig("HolidaysGermany", 
                new Boolean(jCBHolidaysGermany.getState()).toString());
        
        jCBHolidaysGermany.setState(new Boolean(res));
        
        res = root.getSetup().getLocalConfig("HolidaysSwitzerland", 
                new Boolean(jCBHolidaysSwitzerland.getState()).toString());
        
        jCBHolidaysSwitzerland.setState(new Boolean(res));
                                        
        if( root.getSetup().getLocalConfig("MainHolidaysSitzerland","false").contentEquals("true") ) {
            jRBHolidaysSwitzerland.setSelected(true);
            jRBHolidaysSwitzerlandActionPerformed(null);
        }
        
        if( root.getSetup().getLocalConfig("MainHolidaysAustria","false").contentEquals("true") ) {
            jRBHolidaysAustria.setSelected(true);
            jRBHolidaysAustriaActionPerformed(null);
        }
        
        if( root.getSetup().getLocalConfig("MainHolidaysGermany","false").contentEquals("true") ) {
            jRBHolidaysGermany.setSelected(true);
            jRBHolidaysGermanyActionPerformed(null);
        }                
                
        if( root.getUserPermissionLevel() != UserManagementInterface.UM_PERMISSIONLEVEL_ADMIN )
        {
            jMDatabase.setVisible(false);
            jMGlobalConfig.setVisible(false);
            jMJobTypes.setVisible(false);
            JMUserPerMonth.setVisible(false);                             
        }
        
        if( root.getUserPermissionLevel() < UserManagementInterface.UM_PERMISSIONLEVEL_PRIVILEGED )
        {
            jMMonthBlocks.setVisible(false);
            jMExtraHolidays.setVisible(false);
        }
        
        handleHolidays();
        
        month.setListener(this);
        
        updateMonthSumInfo();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        month = new at.redeye.FrameWork.widgets.calendar.CalendarComponent();
        jPanel2 = new javax.swing.JPanel();
        jBErrorLog = new javax.swing.JButton();
        plusMon = new javax.swing.JButton();
        minusMon = new javax.swing.JButton();
        jBHelp = new javax.swing.JButton();
        jLSum = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenuProgram = new javax.swing.JMenu();
        jMDatabase = new javax.swing.JMenuItem();
        jMenuQuit = new javax.swing.JMenuItem();
        jMUser = new javax.swing.JMenu();
        Hauptfeiertage = new javax.swing.JMenu();
        jRBHolidaysAustria = new javax.swing.JRadioButtonMenuItem();
        jRBHolidaysGermany = new javax.swing.JRadioButtonMenuItem();
        jRBHolidaysSwitzerland = new javax.swing.JRadioButtonMenuItem();
        jMenuHollidays = new javax.swing.JMenu();
        jCBHolidaysAustria = new javax.swing.JCheckBoxMenuItem();
        jCBHolidaysGermany = new javax.swing.JCheckBoxMenuItem();
        jCBHolidaysSwitzerland = new javax.swing.JCheckBoxMenuItem();
        jMExtraHolidays = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMJobTypes = new javax.swing.JMenuItem();
        JMUserPerMonth = new javax.swing.JMenuItem();
        jMMonthBlocks = new javax.swing.JMenuItem();
        jMGlobalConfig = new javax.swing.JMenuItem();
        jMLocalConfig = new javax.swing.JMenuItem();
        JMDefaultData = new javax.swing.JMenu();
        JMCustomers = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        jMMonthReport = new javax.swing.JMenuItem();
        jMenuInfo = new javax.swing.JMenu();
        jMInfo = new javax.swing.JMenuItem();
        jMMemInfo = new javax.swing.JMenuItem();
        jMChangeLog = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        month.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(month, javax.swing.GroupLayout.DEFAULT_SIZE, 1106, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(month, javax.swing.GroupLayout.DEFAULT_SIZE, 643, Short.MAX_VALUE)
                .addContainerGap())
        );

        jBErrorLog.setIcon(new javax.swing.ImageIcon(getClass().getResource("/at/redeye/FrameWork/base/resources/icons/info.png"))); // NOI18N
        jBErrorLog.setBorderPainted(false);
        jBErrorLog.setContentAreaFilled(false);
        jBErrorLog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBErrorLogActionPerformed(evt);
            }
        });

        plusMon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/at/redeye/FrameWork/base/resources/icons/next.png"))); // NOI18N
        plusMon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                plusMonActionPerformed(evt);
            }
        });

        minusMon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/at/redeye/FrameWork/base/resources/icons/prev.png"))); // NOI18N
        minusMon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                minusMonActionPerformed(evt);
            }
        });

        jBHelp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/at/redeye/FrameWork/base/resources/icons/help.png"))); // NOI18N
        jBHelp.setText("Hilfe");
        jBHelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBHelpActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(minusMon, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(plusMon, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLSum, javax.swing.GroupLayout.PREFERRED_SIZE, 671, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBErrorLog, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 169, Short.MAX_VALUE)
                .addComponent(jBHelp))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jBHelp)
                        .addContainerGap())
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(minusMon)
                            .addContainerGap())
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(plusMon, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
                            .addComponent(jLSum, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE)
                            .addComponent(jBErrorLog, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE)))))
        );

        jMenuProgram.setText("Programm");

        jMDatabase.setText("Datenbankverbindung");
        jMDatabase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMDatabaseActionPerformed(evt);
            }
        });
        jMenuProgram.add(jMDatabase);

        jMenuQuit.setText("Beenden");
        jMenuQuit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuQuitActionPerformed(evt);
            }
        });
        jMenuProgram.add(jMenuQuit);

        jMenuBar1.add(jMenuProgram);

        jMUser.setText("Einstellungen");
        jMUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMUserActionPerformed(evt);
            }
        });

        Hauptfeiertage.setText("Standort Feiertage");

        jRBHolidaysAustria.setText("Österreich");
        jRBHolidaysAustria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBHolidaysAustriaActionPerformed(evt);
            }
        });
        Hauptfeiertage.add(jRBHolidaysAustria);

        jRBHolidaysGermany.setText("Deutschland");
        jRBHolidaysGermany.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBHolidaysGermanyActionPerformed(evt);
            }
        });
        Hauptfeiertage.add(jRBHolidaysGermany);

        jRBHolidaysSwitzerland.setText("Schweiz");
        jRBHolidaysSwitzerland.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBHolidaysSwitzerlandActionPerformed(evt);
            }
        });
        Hauptfeiertage.add(jRBHolidaysSwitzerland);

        jMUser.add(Hauptfeiertage);

        jMenuHollidays.setText("zusätzliche Feiertage");

        jCBHolidaysAustria.setText("Österreich");
        jCBHolidaysAustria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBHolidaysAustriaActionPerformed(evt);
            }
        });
        jMenuHollidays.add(jCBHolidaysAustria);

        jCBHolidaysGermany.setText("Deutschland");
        jCBHolidaysGermany.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBHolidaysGermanyActionPerformed(evt);
            }
        });
        jMenuHollidays.add(jCBHolidaysGermany);

        jCBHolidaysSwitzerland.setText("Schweiz");
        jCBHolidaysSwitzerland.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBHolidaysSwitzerlandActionPerformed(evt);
            }
        });
        jMenuHollidays.add(jCBHolidaysSwitzerland);

        jMExtraHolidays.setText("zusätzliche Feitertage");
        jMExtraHolidays.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMExtraHolidaysActionPerformed(evt);
            }
        });
        jMenuHollidays.add(jMExtraHolidays);

        jMUser.add(jMenuHollidays);

        jMenuItem1.setText("Benutzer");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMUser.add(jMenuItem1);

        jMJobTypes.setText("Tätigkeiten");
        jMJobTypes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMJobTypesActionPerformed(evt);
            }
        });
        jMUser.add(jMJobTypes);

        JMUserPerMonth.setText("Monatseinstellungen für die Benutzer");
        JMUserPerMonth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                JMUserPerMonthActionPerformed(evt);
            }
        });
        jMUser.add(JMUserPerMonth);

        jMMonthBlocks.setText("Editierbarkeit der Monate");
        jMMonthBlocks.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMMonthBlocksActionPerformed(evt);
            }
        });
        jMUser.add(jMMonthBlocks);

        jMGlobalConfig.setText("Globale Einstellungen");
        jMGlobalConfig.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMGlobalConfigActionPerformed(evt);
            }
        });
        jMUser.add(jMGlobalConfig);

        jMLocalConfig.setText("Lokale Einstellungen");
        jMLocalConfig.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMLocalConfigActionPerformed(evt);
            }
        });
        jMUser.add(jMLocalConfig);

        jMenuBar1.add(jMUser);

        JMDefaultData.setText("Stammdaten");

        JMCustomers.setText("Kunden");
        JMCustomers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                JMCustomersActionPerformed(evt);
            }
        });
        JMDefaultData.add(JMCustomers);

        jMenuBar1.add(JMDefaultData);

        jMenu1.setText("Berichte");

        jMMonthReport.setText("Monatsbericht");
        jMMonthReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMMonthReportActionPerformed(evt);
            }
        });
        jMenu1.add(jMMonthReport);

        jMenuBar1.add(jMenu1);

        jMenuInfo.setText("Info");

        jMInfo.setText("Über");
        jMInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMInfoActionPerformed(evt);
            }
        });
        jMenuInfo.add(jMInfo);

        jMMemInfo.setText("Speicherinformationen");
        jMMemInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMMemInfoActionPerformed(evt);
            }
        });
        jMenuInfo.add(jMMemInfo);

        jMChangeLog.setText("Änderungsprotokoll");
        jMChangeLog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMChangeLogActionPerformed(evt);
            }
        });
        jMenuInfo.add(jMChangeLog);

        jMenuBar1.add(jMenuInfo);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void jMenuQuitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuQuitActionPerformed

    close();
}//GEN-LAST:event_jMenuQuitActionPerformed

private void jMDatabaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMDatabaseActionPerformed

    java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {                
                new ConnectionDialog( root ).setVisible( true );
            }
     });    
}//GEN-LAST:event_jMDatabaseActionPerformed

private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
// TODO add your handling code here:
	
    java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {                
                // new ConnectionDialog( root ).setVisible( true );
                // Mario hier dialogaufruf für Benutzerwartungsdialog
            	UserManagementInterface um = new UserDataHandling (root);
            	um.requestDialog(UserManagementDialogs.UM_ADMINISTRATION_DIALOG);
            }
     });        
}//GEN-LAST:event_jMenuItem1ActionPerformed

private void minusMonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_minusMonActionPerformed
// TODO add your handling code here:
    mon--;
    
    if( mon <= 0 )
    {
        mon = 12;
        year--;
    }
    
    month.setMonth(mon, year);
    updateMonthSumInfo();
}//GEN-LAST:event_minusMonActionPerformed

private void plusMonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_plusMonActionPerformed
// TODO add your handling code here:
   mon++;
   
   if( mon > 12 )
   {
       mon = 1;
       year++;
   }
   
   month.setMonth(mon, year);
   updateMonthSumInfo();
}//GEN-LAST:event_plusMonActionPerformed

private void jCBHolidaysAustriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBHolidaysAustriaActionPerformed
// TODO add your handling code here:
    handleHolidays();
}//GEN-LAST:event_jCBHolidaysAustriaActionPerformed

private void jCBHolidaysGermanyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBHolidaysGermanyActionPerformed
// TODO add your handling code here:
    handleHolidays();
}//GEN-LAST:event_jCBHolidaysGermanyActionPerformed

private void jCBHolidaysSwitzerlandActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBHolidaysSwitzerlandActionPerformed
// TODO add your handling code here:
    handleHolidays();
}//GEN-LAST:event_jCBHolidaysSwitzerlandActionPerformed

private void jRBHolidaysAustriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBHolidaysAustriaActionPerformed
// TODO add your handling code here:
        
    jRBHolidaysSwitzerland.setSelected(false);
    jRBHolidaysGermany.setSelected(false);        
    
    jCBHolidaysSwitzerland.setEnabled(true);
    jCBHolidaysGermany.setEnabled(true);
    
    jCBHolidaysAustria.setEnabled(false);    
    jCBHolidaysAustria.setSelected(false);
    
    handleHolidays();
}//GEN-LAST:event_jRBHolidaysAustriaActionPerformed

private void jRBHolidaysGermanyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBHolidaysGermanyActionPerformed
// TODO add your handling code here:
    jRBHolidaysSwitzerland.setSelected(false);
    jRBHolidaysAustria.setSelected(false);
    
    jCBHolidaysSwitzerland.setEnabled(true);
    jCBHolidaysAustria.setEnabled(true);
    
    jCBHolidaysGermany.setSelected(false);
    jCBHolidaysGermany.setEnabled(false);
    
    handleHolidays();
}//GEN-LAST:event_jRBHolidaysGermanyActionPerformed

private void jRBHolidaysSwitzerlandActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBHolidaysSwitzerlandActionPerformed
// TODO add your handling code here:
    jRBHolidaysAustria.setSelected(false);
    jRBHolidaysGermany.setSelected(false);
    
    jCBHolidaysAustria.setEnabled(true);
    jCBHolidaysGermany.setEnabled(true);
    
    jCBHolidaysSwitzerland.setSelected(false);
    jCBHolidaysSwitzerland.setEnabled(false);
    
    handleHolidays();
}//GEN-LAST:event_jRBHolidaysSwitzerlandActionPerformed

private void jMJobTypesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMJobTypesActionPerformed

    
    java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {                
                new JobTypes(root).setVisible(true);
            }
     });

     
}//GEN-LAST:event_jMJobTypesActionPerformed

private void jMUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMUserActionPerformed


    
}//GEN-LAST:event_jMUserActionPerformed

private void JMUserPerMonthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_JMUserPerMonthActionPerformed
// TODO add your handling code here:
    java.awt.EventQueue.invokeLater(new Runnable() {

        public void run() {
            new UserPerMonth(root).setVisible(true);
        }
    });
    
}//GEN-LAST:event_JMUserPerMonthActionPerformed

private void jMMonthReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMMonthReportActionPerformed
// TODO add your handling code here:
    
    java.awt.EventQueue.invokeLater(new Runnable() {

        public void run() {
            new MonthReportPerUser(root, mon, year).setVisible(true);
        }
    });    
}//GEN-LAST:event_jMMonthReportActionPerformed

private void jBHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBHelpActionPerformed

    java.awt.EventQueue.invokeLater(new Runnable() {

        public void run() {
            new LocalHelpWin(root,"MainWin").setVisible(true);
        }
    }); 
    
}//GEN-LAST:event_jBHelpActionPerformed

private void jMGlobalConfigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMGlobalConfigActionPerformed
        
    java.awt.EventQueue.invokeLater(new Runnable() {//GEN-LAST:event_jMGlobalConfigActionPerformed

   public void run() {
            new GlobalConfig(root).setVisible(true);
        }
  }); 

}

        
private void jMExtraHolidaysActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMExtraHolidaysActionPerformed


    final MainWin mw = this;
    
    java.awt.EventQueue.invokeLater(new Runnable() {                                              

   public void run() {
            new ExtraHolidays(root, mw).setVisible(true);
        }
  }); 

    
}//GEN-LAST:event_jMExtraHolidaysActionPerformed

private void jMLocalConfigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMLocalConfigActionPerformed

    java.awt.EventQueue.invokeLater(new Runnable() {//GEN-LAST:event_jMLocalConfigActionPerformed

    public void run() {
       new LocalConfig(root).setVisible(true);
    }
  }); 

}



private void jMMonthBlocksActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMMonthBlocksActionPerformed

   java.awt.EventQueue.invokeLater(new Runnable() {                                             

    public void run() {
       new MonthBlocks(root).setVisible(true);
    }
  }); 
    
    
}//GEN-LAST:event_jMMonthBlocksActionPerformed

private void jMInfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMInfoActionPerformed
// TODO add your handling code here:
    java.awt.EventQueue.invokeLater(new Runnable() {

        public void run() {
            new About(root).setVisible(true);
        }
    });
}//GEN-LAST:event_jMInfoActionPerformed

private void jMMemInfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMMemInfoActionPerformed
// TODO add your handling code here:
    java.awt.EventQueue.invokeLater(new Runnable() {

        public void run() {
            new MemInfo(root).setVisible(true);
        }
    });
}//GEN-LAST:event_jMMemInfoActionPerformed

private void jMChangeLogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMChangeLogActionPerformed
    // TODO add your handling code here:
    java.awt.EventQueue.invokeLater(new Runnable() {

        public void run() {
            new LocalHelpWin(root,"ChangeLog").setVisible(true);
        }
    });
}//GEN-LAST:event_jMChangeLogActionPerformed

private void jBErrorLogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBErrorLogActionPerformed
    // TODO add your handling code here:

    java.awt.EventQueue.invokeLater(new Runnable() {

        public void run() {
            new LogWin( root, "Fehlermeldungen", month_stuff.getError() ).setVisible(true);
        }
    });

}//GEN-LAST:event_jBErrorLogActionPerformed

private void JMCustomersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_JMCustomersActionPerformed
    // TODO add your handling code here:
    java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new Customers(root).setVisible(true);
            }
        });

}//GEN-LAST:event_JMCustomersActionPerformed




public void handleHolidays()
{        
    if( jCBHolidaysAustria.getState() == true )
    {
        merger.add( new AustrianHolidays() );        
    } else {
        merger.remove(new AustrianHolidays() );
    }
    
    if( jCBHolidaysGermany.getState() == true )
    {
        merger.add( new GermanHolidays() );        
    } else {
        merger.remove(new GermanHolidays() );
    }
    
    if( jCBHolidaysSwitzerland.getState() == true )
    {
        merger.add( new SwitzerlandHolidays() );        
    } else {
        merger.remove(new SwitzerlandHolidays() );
    }
    
    if( jRBHolidaysAustria.isSelected() == true )
    {
        merger.setPrimaryCalendar(new AustrianHolidays() );        
    } else if( jRBHolidaysGermany.isSelected() == true ) {    
        merger.setPrimaryCalendar(new GermanHolidays() );
        
    } else if( jRBHolidaysSwitzerland.isSelected() == true ) {
        merger.setPrimaryCalendar(new SwitzerlandHolidays() );
    }
    
    
    new AutoLogger("LocalHolidays") 
    {

        @Override
        public void do_stuff() throws Exception {
            LocalHolidays lh = new LocalHolidays(getTransaction());
            lh.load();

            merger.add(lh);
        }
    };
        
    month.setHolidays(merger);
    month.setMonth(mon, year);   
}

@Override
public void close()
{
    root.getSetup().setLocalConfig("HolidaysAustria", new Boolean(jCBHolidaysAustria.getState()).toString());
    root.getSetup().setLocalConfig("HolidaysGermany", new Boolean(jCBHolidaysGermany.getState()).toString());
    root.getSetup().setLocalConfig("HolidaysSwitzerland", new Boolean(jCBHolidaysSwitzerland.getState()).toString());
    
    root.getSetup().setLocalConfig("MainHolidaysAustria", new Boolean(jRBHolidaysAustria.isSelected()).toString());
    root.getSetup().setLocalConfig("MainHolidaysGermany", new Boolean(jRBHolidaysGermany.isSelected()).toString());
    root.getSetup().setLocalConfig("MainHolidaysSwitzerland", new Boolean(jRBHolidaysSwitzerland.isSelected()).toString());
    
    super.close();
    root.appExit();
    this.dispose();
}



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu Hauptfeiertage;
    private javax.swing.JMenuItem JMCustomers;
    private javax.swing.JMenu JMDefaultData;
    private javax.swing.JMenuItem JMUserPerMonth;
    private javax.swing.JButton jBErrorLog;
    private javax.swing.JButton jBHelp;
    private javax.swing.JCheckBoxMenuItem jCBHolidaysAustria;
    private javax.swing.JCheckBoxMenuItem jCBHolidaysGermany;
    private javax.swing.JCheckBoxMenuItem jCBHolidaysSwitzerland;
    private javax.swing.JLabel jLSum;
    private javax.swing.JMenuItem jMChangeLog;
    private javax.swing.JMenuItem jMDatabase;
    private javax.swing.JMenuItem jMExtraHolidays;
    private javax.swing.JMenuItem jMGlobalConfig;
    private javax.swing.JMenuItem jMInfo;
    private javax.swing.JMenuItem jMJobTypes;
    private javax.swing.JMenuItem jMLocalConfig;
    private javax.swing.JMenuItem jMMemInfo;
    private javax.swing.JMenuItem jMMonthBlocks;
    private javax.swing.JMenuItem jMMonthReport;
    private javax.swing.JMenu jMUser;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenu jMenuHollidays;
    private javax.swing.JMenu jMenuInfo;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenu jMenuProgram;
    private javax.swing.JMenuItem jMenuQuit;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JRadioButtonMenuItem jRBHolidaysAustria;
    private javax.swing.JRadioButtonMenuItem jRBHolidaysGermany;
    private javax.swing.JRadioButtonMenuItem jRBHolidaysSwitzerland;
    private javax.swing.JButton minusMon;
    private at.redeye.FrameWork.widgets.calendar.CalendarComponent month;
    private javax.swing.JButton plusMon;
    // End of variables declaration//GEN-END:variables

    boolean checkJobTypes()
    {
        AutoLogger al = new AutoLogger("checkJobTypes")
        {

            @Override
            public void do_stuff() throws Exception {
                
                result = new Boolean(false);
                
                Vector<DBStrukt> entries = getTransaction().fetchTable(new DBJobType(),
                        "where " + getTransaction().markColumn("locked") + "='NEIN'");
                        
                if( entries.size() == 0 )
                {
                    JOptionPane.showMessageDialog(null,
                    	StringUtils.autoLineBreak(
                            "Sie müssen zuerst Tätigkeiten, die zur Benutzung neuer" +
                            "Zeiteinträge verwendet werden können, erstellen. " +
                            "Menüpunkt: Einstellungen -> Tätigkeiten.\n" +
                            "Sollte dieser Menüpunkt Ihnen nicht zur Verfügungstehen, so wenden Sie sich " +
                            "bitte an Ihren Administrator.", 40),
                            "Fehler",
                            JOptionPane.OK_OPTION);
                    
                } else {
                    result = new Boolean(true)                    ;
                }
            }
            
        };
        
        return (Boolean)al.result;
    }
    
    boolean checkMonthSettings()
    {
        AutoLogger al = new AutoLogger("checkMonthSettings")
        {

            @Override
            public void do_stuff() throws Exception {
                
                result = new Boolean(false);
                
                Vector<DBStrukt> entries = getTransaction().fetchTable(new DBUserPerMonth(),
                        "where " + getTransaction().markColumn("locked") + "='NEIN'" +
                        " and " +
                        getTransaction().markColumn("user") + "='" + root.getUserId() +"'" );
                        
                if( entries.size() == 0 )
                {
                    JOptionPane.showMessageDialog(null,
                    	StringUtils.autoLineBreak(
                            "Sie müssen zuerst die Normalarbeitszeit für die Benutzer festlegen. " +
                            "Menüpunkt: Einstellungen -> \"Monatseinstellungen für die Benutzer\".\n" +
                            "Sollte dieser Menüpunkt Ihnen nicht zur Verfügungstehen, so wenden Sie sich " +
                            "bitte an Ihren Administrator."
                            , 40),
                            "Fehler",
                            JOptionPane.OK_OPTION);
                    
                } else {
                    result = new Boolean(true)                    ;
                }
            }
            
        };
        
        return (Boolean)al.result;
    }
    
    public void onClicked(CalendarDay day) {        
        
        if( checkJobTypes() == true && checkMonthSettings() == true )
        {        
            // System.out.println("Clicked on day " + month.isWhatDayOfMonth(day) );
            BookDay book_day = new BookDay(root,
                    new DateMidnight(year, mon, month.isWhatDayOfMonth(day)),
                    month.getDay(day), this,
                    month_stuff.hours_per_day,
                    cache);
            book_day.setVisible(true);            
        }
    }        
    
    public void updateMonthSumInfo() {       
       
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                
                if( cache != null )                
                    cache.setOutdated();                
                
                month.setMonth(mon, year);
                
                jLSum.setText("");

                new AutoMBox("updateMonthSumInfo") {

                    @Override
                    public void do_stuff() throws Exception {
                        month_stuff.calc();

                        StringBuilder text = new StringBuilder();

                        text.append("Soll: ");
                        text.append(month_stuff.getFormatedHoursPerMonth());
                        text.append(" Ist: ");
                        text.append(month_stuff.complete_time.toString("HH:mm"));

                        if( month_stuff.time_correction_month_done.getMillis() != 0 )
                        {
                            text.append(" ");

                            if( month_stuff.time_correction_month_done.getMillis() > 0 )
                            {
                                text.append("+");
                            }

                            text.append(month_stuff.time_correction_month_done.toString("HH:mm"));

                            text.append(" = ");

                            HMSTime t = new HMSTime();
                            t.setTime(month_stuff.getHoursPerMonthDoneinMillis());
                            t.addMillis(month_stuff.time_correction_month_done.getMillis());

                            text.append(t.toString("HH:mm"));

                        }

                        text.append(" Gleitzeitkonto: ");
                        text.append(month_stuff.overtime.toString("HH:mm"));
                        text.append(" Resturlaub: ");
                        text.append(month_stuff.remaining_leave.toString("HH:mm"));

                        if(  month_stuff.hours_per_day > 0 )
                            text.append(" (" + Rounding.RndDouble(month_stuff.remaining_leave.getHours() / month_stuff.hours_per_day,1) + " Tage)" );

                        jLSum.setText(text.toString());

                        jBErrorLog.setVisible(month_stuff.hasError());
                    }
                };
            }
        });


    }

}

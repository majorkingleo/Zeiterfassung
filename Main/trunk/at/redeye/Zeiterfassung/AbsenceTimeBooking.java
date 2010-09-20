/*
 * AbsenceTimeBooking.java
 *
 * Created on 15.09.2010, 21:09:38
 */

package at.redeye.Zeiterfassung;

import at.redeye.FrameWork.base.AutoMBox;
import at.redeye.FrameWork.base.BaseDialog;
import at.redeye.FrameWork.widgets.documentfields.DocumentFieldHourMinute;
import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.base.bindtypes.DBFlagJaNein;
import at.redeye.FrameWork.base.transaction.Transaction;
import at.redeye.FrameWork.utilities.StringUtils;
import at.redeye.FrameWork.utilities.calendar.Holidays.HolidayInfo;
import at.redeye.Zeiterfassung.bindtypes.DBJobType;
import at.redeye.Zeiterfassung.bindtypes.DBTimeEntries;
import java.util.Calendar;
import java.util.List;
import javax.swing.JOptionPane;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

/**
 *
 * @author martin
 */
public class AbsenceTimeBooking extends BaseDialog {    

    static class JobNameWrapper
    {
        DBJobType job_type;
        
        public JobNameWrapper( DBJobType job_type )
        {
            this.job_type = job_type;
        }
        
        @Override
        public String toString()
        {
            return job_type.name.toString();
        }
    };

    int min_num_of_chars = 4;
    MonthSumInfo suminfo;
    double hours_per_day;
    String normal_start_time;
    String normal_stop_time;

    private String MESSAGE_LEAST_ONE_CHARACTER;
    private String MESSAGE_LEAST_X_CHARACTER;
    private String MESSAGE_MISSING_COMMENTS;
    private String MESSAGE_MISSING_DATE;

    public AbsenceTimeBooking(final Root root, MonthSumInfo suminfo, double hours_per_day) {
        super(root, "Abwesenheitszeiten Buchen");        

        initComponents();

        initMessages();

        this.suminfo = suminfo;
        this.hours_per_day = hours_per_day;

        final Transaction trans = getTransaction();

        final DBJobType job_type = new DBJobType();

        new AutoMBox(AbsenceTimeBooking.class.getName()) {

            @Override
            public void do_stuff() throws Exception {
                List<DBJobType> jt = trans.fetchTable2(job_type,
                "where " + trans.markColumn(job_type.locked) + "='" + DBFlagJaNein.FLAGTYPES.NEIN.toString() + "' " +
                " order by " + trans.markColumn(job_type.name));

                int preselect = -1;
                int count = 0;

                for( DBJobType j : jt )
                {
                    jCNLZ.addItem(new JobNameWrapper(j));
                    if( j.is_holliday.isYes() )
                    {
                        preselect = count;
                    }
                    
                    count++;
                }

                if( preselect >= 0 )
                    jCNLZ.setSelectedIndex(preselect);
            }
        };

        jDFrom.setDateNow();
        jDTo.setDateNow();

        jTFrom.setDocument( new DocumentFieldHourMinute() );
        jTTo.setDocument( new DocumentFieldHourMinute() );

        normal_start_time = root.getSetup().getConfig(AppConfigDefinitions.NormalWorkTimeStart);
        normal_stop_time = root.getSetup().getConfig(AppConfigDefinitions.NormalWorkTimeStop);

        jTFrom.setText(normal_start_time);
        jTTo.setText(normal_stop_time);

        new AutoMBox(getTitle()) {

            public void do_stuff() throws Exception {
                String comment_chars = root.getSetup().getConfig(
                        AppConfigDefinitions.NumberOfMinimumCommentChars);
                Integer num = Integer.valueOf(comment_chars);
                min_num_of_chars = num;
            }
        };

        if( hours_per_day == 0.0 )
        {
            jCWholeDay.setSelected(false);
            jCWholeDayActionPerformed(null);
            jCWholeDay.setVisible(false);
        }

        registerHelpWin(new Runnable() {

            public void run() {
                 invokeDialogUnique(new LocalHelpWin(root, "AbsenceTimeBooking"));
            }
        });

    }

    private void initMessages()
    {
        if( MESSAGE_LEAST_ONE_CHARACTER != null )
            return;

        MESSAGE_LEAST_ONE_CHARACTER = MlM("Es muß mindestens ein Zeichen eingeben werden.");
        MESSAGE_LEAST_X_CHARACTER = MlM( "Es müssen mindestens %d Zeichen eingeben werden.");
        MESSAGE_MISSING_COMMENTS = MlM( "Es fehlt der Kommentar, oder der eingegebene Text '%s' ist zu kurz.");
        MESSAGE_MISSING_DATE = MlM( "Du hast kein Datum ausgewählt" );
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLTitle = new javax.swing.JLabel();
        jBHelp = new javax.swing.JButton();
        jBClose = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jCNLZ = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        jTFrom = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jTTo = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jTComment = new javax.swing.JTextField();
        jDFrom = new at.redeye.Plugins.JDatePicker.JDatePicker(root);
        jDTo = new at.redeye.Plugins.JDatePicker.JDatePicker(root);
        jCWholeDay = new javax.swing.JCheckBox();
        jBSave = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLTitle.setFont(new java.awt.Font("Dialog", 1, 18));
        jLTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLTitle.setText("Abwesenheitszeiten Buchen");

        jBHelp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/at/redeye/FrameWork/base/resources/icons/help.png"))); // NOI18N
        jBHelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBHelpActionPerformed(evt);
            }
        });

        jBClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/at/redeye/FrameWork/base/resources/icons/fileclose.gif"))); // NOI18N
        jBClose.setText("Schließen");
        jBClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBCloseActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setText("Von:");

        jLabel2.setText("Bis:");

        jLabel3.setText("Tätigkeit:");

        jLabel4.setText("Täglich von:");

        jTFrom.setEnabled(false);

        jLabel5.setText("Täglich bis:");

        jTTo.setEnabled(false);

        jLabel6.setText("Kommentar:");

        jDFrom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jDFromActionPerformed(evt);
            }
        });

        jDTo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jDToActionPerformed(evt);
            }
        });

        jCWholeDay.setSelected(true);
        jCWholeDay.setText("ganzer Tag");
        jCWholeDay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCWholeDayActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel3)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1))
                .addGap(22, 22, 22)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jCNLZ, javax.swing.GroupLayout.Alignment.LEADING, 0, 304, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jTTo, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTFrom, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jCWholeDay))
                    .addComponent(jTComment, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 304, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jDTo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jDFrom, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jDFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jDTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jCNLZ, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCWholeDay))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jTTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jTComment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(60, 60, 60))
        );

        jBSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/at/redeye/FrameWork/base/resources/icons/button_ok.gif"))); // NOI18N
        jBSave.setText("Buchen");
        jBSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBSaveActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jLTitle, javax.swing.GroupLayout.DEFAULT_SIZE, 396, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBHelp, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jBSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 199, Short.MAX_VALUE)
                        .addComponent(jBClose)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jBHelp)
                    .addComponent(jLTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jBClose)
                    .addComponent(jBSave))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jBHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBHelpActionPerformed

       callHelpWin();

    }//GEN-LAST:event_jBHelpActionPerformed

    private void jBCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBCloseActionPerformed

        if( canClose() )
            close();

    }//GEN-LAST:event_jBCloseActionPerformed

    private void jBSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSaveActionPerformed


        AutoMBox am = new AutoMBox(AbsenceTimeBooking.class.getName()) {

            @Override
            public void do_stuff() throws Exception {
                DBTimeEntries entry = new DBTimeEntries();

                entry.comment.loadFromString(jTComment.getText());

                if (min_num_of_chars != 0) {
                    if (entry.comment.getValue().isEmpty()
                            || entry.comment.getValue().length() < min_num_of_chars) {
                        String minimum_msg = "";

                        if (min_num_of_chars > 0) {
                            if (min_num_of_chars == 1) {
                                minimum_msg = MESSAGE_LEAST_ONE_CHARACTER;
                            } else {
                                minimum_msg = String.format(MESSAGE_LEAST_X_CHARACTER,min_num_of_chars);
                            }
                        }

                        JOptionPane.showMessageDialog(null, StringUtils.autoLineBreak(
                                String.format(MESSAGE_MISSING_COMMENTS, (entry.comment.getValue().toString().isEmpty() ? "   " : entry.comment.getValue()) )
                                + " " + minimum_msg), MlM("Fehler"),
                                JOptionPane.OK_OPTION);

                        logical_failure = true;
                        return;
                    }
                }

                entry.jobtype.loadFromCopy(((JobNameWrapper)jCNLZ.getSelectedItem()).job_type.id.getValue());
                entry.user.loadFromCopy(root.getUserId());

                entry.hist.setAnHist(root.getUserName());

                Calendar cal_from = jDFrom.getSelectedDate();
                Calendar cal_to = jDTo.getSelectedDate();

                if( cal_from == null && cal_to == null )
                {
                    JOptionPane.showMessageDialog(null, StringUtils.autoLineBreak(
                            MESSAGE_MISSING_DATE ), MlM("Fehler"), JOptionPane.OK_OPTION );
                    logical_failure = true;
                    return;
                }
                else if( cal_to == null )
                {
                    cal_to = cal_from;
                }
                else if( cal_from == null )
                {
                    cal_from = cal_to;
                }

                DateMidnight mFrom = new DateMidnight(cal_from);
                DateMidnight mTo = new DateMidnight(cal_to);

                Transaction trans = getTransaction();

                List<DBTimeEntries> entries = trans.fetchTable2(entry,
                        "where " + trans.getPeriodStmt(entry.from, mFrom, mTo) + " and " +
                        trans.markColumn(entry.user) + "=" + root.getUserId());

                for( DateMidnight mCurrent = mFrom; 
                        mCurrent.isBefore(mTo.getMillis()) ||
                        mCurrent.isEqual(mTo.getMillis()) ;
                    mCurrent = mCurrent.plusDays(1))
                {
                    HolidayInfo info = root.getHolidays().getHolidayForDay(mCurrent);

                    if( info != null )
                        continue;

                    if( mCurrent.getDayOfWeek() == DateTimeConstants.SATURDAY ||
                        mCurrent.getDayOfWeek() == DateTimeConstants.SUNDAY )
                    {
                        continue;
                    }

                    DBTimeEntries e = new DBTimeEntries();

                    e.loadFromCopy(entry);

                    e.id.loadFromCopy(new Integer(getNewSequenceValue(entry.getName())));

                    e.from.loadFromCopy(mCurrent.toDate());                    
                    e.to.loadFromCopy(mCurrent.toDate());

                    if( !jCWholeDay.isSelected() )
                    {
                        e.from.loadTimePart(jTFrom.getText() + ":00");
                        e.to.loadTimePart(jTTo.getText() + ":00");
                    }
                    else
                    {
                        e.from.loadTimePart(normal_start_time + ":00");                        
                        DateTime dt = new DateTime( e.from.getValue() );
                        dt = dt.plusMinutes((int)Math.ceil(hours_per_day*60.0));
                        e.to.loadFromCopy(dt.toDate());
                    }

                    boolean found = false;

                    for( DBTimeEntries ee : entries )
                    {
                        if( DBTimeEntries.overlap(ee, e) )
                        {
                            found = true;
                            break;
                        }
                    }

                    if( found )
                        continue;

                    getTransaction().insertValues(e);
                }

                 getTransaction().commit();
            }
        };

       if( am.isFailed() )
       {
           new AutoMBox(AbsenceTimeBooking.class.getName()) {

                @Override
                public void do_stuff() throws Exception {
                    getTransaction().rollback();
                }
            };
       } else {
           new AutoMBox(AbsenceTimeBooking.class.getName()) {

                @Override
                public void do_stuff() throws Exception {
                    getTransaction().commit();
                    close();
                    suminfo.updateMonthSumInfo();
                }
            };
       }     

    }//GEN-LAST:event_jBSaveActionPerformed

    private void jDFromActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDFromActionPerformed

        Calendar cal_from = jDFrom.getSelectedDate();
        Calendar cal_to = jDTo.getSelectedDate();

        if( cal_from == null || cal_to == null )
            return;

        DateMidnight mFrom = new DateMidnight(cal_from);

        if( mFrom.isAfter(cal_to.getTimeInMillis()) )
        {
            jDTo.setDate(cal_from);
            jDFrom.setDate(cal_to);
        }

    }//GEN-LAST:event_jDFromActionPerformed

    private void jDToActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDToActionPerformed

        Calendar cal_from = jDFrom.getSelectedDate();
        Calendar cal_to = jDTo.getSelectedDate();

        if( cal_from == null || cal_to == null )
            return;

        DateMidnight mTo = new DateMidnight(cal_to);

        if( mTo.isBefore(cal_from.getTimeInMillis()) )
        {
            jDTo.setDate(cal_from);
            jDFrom.setDate(cal_to);
        }        

    }//GEN-LAST:event_jDToActionPerformed

    private void jCWholeDayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCWholeDayActionPerformed
      
        if( jCWholeDay.isSelected() )
        {
            jTFrom.setEnabled(false);
            jTTo.setEnabled(false);
        }
        else
        {
            jTFrom.setEnabled(true);
            jTTo.setEnabled(true);
        }

    }//GEN-LAST:event_jCWholeDayActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBClose;
    private javax.swing.JButton jBHelp;
    private javax.swing.JButton jBSave;
    private javax.swing.JComboBox jCNLZ;
    private javax.swing.JCheckBox jCWholeDay;
    private at.redeye.Plugins.JDatePicker.JDatePicker jDFrom;
    private at.redeye.Plugins.JDatePicker.JDatePicker jDTo;
    private javax.swing.JLabel jLTitle;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField jTComment;
    private javax.swing.JTextField jTFrom;
    private javax.swing.JTextField jTTo;
    // End of variables declaration//GEN-END:variables

}

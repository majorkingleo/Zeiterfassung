/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * MonthlyReportOverview.java
 *
 * Created on 19.07.2010, 22:39:53
 */

package at.redeye.Zeiterfassung.reports.activity;

import at.redeye.FrameWork.base.AutoMBox;
import at.redeye.FrameWork.base.BaseDialog;
import at.redeye.FrameWork.base.Root;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.HyperlinkListener;
import org.joda.time.DateMidnight;

/**
 *
 * @author martin
 */
public class MonthlyReportActivity extends BaseDialog {

    int mon;
    int year;
    MonthReportActivityRenderer renderer;

    /** Creates new form MonthlyReportOverview */
    public MonthlyReportActivity(Root root, int mon, int year) {
        super(root,"Montatsübersicht Aktivitäten");
        initComponents();

        if( mon <= 0 ||
            mon > 12 ||
            year <= 0 )
        {
            DateMidnight dm = new DateMidnight();
            mon = dm.getMonthOfYear();
            year = dm.getYear();
        }

        this.mon = mon;
        this.year = year;

        renderer = new MonthReportActivityRenderer(getTransaction());

        jReport.addHyperlinkListener(new HyperlinkListener() {

            public void hyperlinkUpdate(HyperlinkEvent e) {
                if( e.getEventType() == EventType.ACTIVATED )
                {
                    renderer.handleUrl( e.getURL() );
                    int pos = jReport.getCaretPosition();
                    jReport.setText(renderer.render());
                    jReport.setCaretPosition(pos);
                }
            }
        });

        setMonth();
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        minusMon = new javax.swing.JButton();
        plusMon = new javax.swing.JButton();
        jBPrint = new javax.swing.JButton();
        jBClose = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jReport = new javax.swing.JEditorPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        minusMon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/at/redeye/FrameWork/base/resources/icons/prev.png"))); // NOI18N
        minusMon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                minusMonActionPerformed(evt);
            }
        });

        plusMon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/at/redeye/FrameWork/base/resources/icons/next.png"))); // NOI18N
        plusMon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                plusMonActionPerformed(evt);
            }
        });

        jBPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/at/redeye/FrameWork/base/resources/icons/print.png"))); // NOI18N
        jBPrint.setText("Drucken");
        jBPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBPrintActionPerformed(evt);
            }
        });

        jBClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/at/redeye/FrameWork/base/resources/icons/fileclose.gif"))); // NOI18N
        jBClose.setText("Schließen");
        jBClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBCloseActionPerformed(evt);
            }
        });

        jReport.setContentType("text/html");
        jReport.setEditable(false);
        jScrollPane1.setViewportView(jReport);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(minusMon)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(plusMon)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 159, Short.MAX_VALUE)
                .addComponent(jBPrint)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBClose)
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(358, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(minusMon, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(plusMon))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jBClose)
                        .addComponent(jBPrint)))
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 337, Short.MAX_VALUE)
                    .addGap(53, 53, 53)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void minusMonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_minusMonActionPerformed

        mon--;

        if( mon <= 0 ) {
            mon = 12;
            year--;
        }

        setMonth();
}//GEN-LAST:event_minusMonActionPerformed

    private void plusMonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_plusMonActionPerformed

        mon++;

        if( mon >= 12 ) {
            mon = 1;
            year++;
        }

        setMonth();
}//GEN-LAST:event_plusMonActionPerformed

    private void jBPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBPrintActionPerformed


        new AutoMBox("jBPrintActionPerformed") {
            @Override
            public void do_stuff() throws Exception {
                jReport.print();
            }
        };
    }//GEN-LAST:event_jBPrintActionPerformed

    private void jBCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBCloseActionPerformed

        new AutoMBox(getTitle()) {

            @Override
            public void do_stuff() throws Exception {
                if( canClose() ) {
                    getTransaction().rollback();
                    close();
                }
            }
        };
}//GEN-LAST:event_jBCloseActionPerformed


    private void setMonth()
    {
        renderer.setMonth(mon,year);
        
        jReport.setText(renderer.render());
        jReport.setCaretPosition(0);
        
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBClose;
    private javax.swing.JButton jBPrint;
    private javax.swing.JEditorPane jReport;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton minusMon;
    private javax.swing.JButton plusMon;
    // End of variables declaration//GEN-END:variables

}

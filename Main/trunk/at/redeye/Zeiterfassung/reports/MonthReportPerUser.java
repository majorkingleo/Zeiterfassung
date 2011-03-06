/*
 * MonthReport.java
 *
 * Created on 6. März 2009, 10:26
 */

package at.redeye.Zeiterfassung.reports;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import org.joda.time.DateMidnight;

import at.redeye.FrameWork.base.AutoLogger;
import at.redeye.FrameWork.base.AutoMBox;
import at.redeye.FrameWork.base.BaseDialog;
import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.base.bindtypes.DBStrukt;
import at.redeye.FrameWork.base.reports.ReportRenderer;
import at.redeye.FrameWork.utilities.calendar.Holidays;
import at.redeye.FrameWork.utilities.calendar.MonthNames;
import at.redeye.UserManagement.UserManagementInterface;
import at.redeye.UserManagement.bindtypes.DBPb;

/**
 * 
 * @author martin
 */
public class MonthReportPerUser extends BaseDialog {

	public static class UserItem {
		String name;
		DBPb pb;

		public UserItem(String name, DBPb pb) {
			this.name = name;
			this.pb = pb;
		}

		@Override
		public String toString() {
			return name;
		}

		public DBPb getPb() {
			return pb;
		}
	}

	int mon;
	int year;
	String currentUser;
	int currentUserId;
        Holidays holidays;

	/** Creates new form MonthReport */
	public MonthReportPerUser(Root root, int mon, int year, Holidays holidays) {
		super(root, "Monatsbericht für " + root.getUserName());
		initComponents();

		if (mon <= 0 || mon > 12 || year <= 0) {
			DateMidnight dm = new DateMidnight();
			mon = dm.getMonthOfYear();
			year = dm.getYear();
		}

		this.mon = mon;
		this.year = year;
                this.holidays = holidays;
		currentUser = root.getUserName();
		currentUserId = root.getUserId();

		if (root.getUserPermissionLevel() > UserManagementInterface.UM_PERMISSIONLEVEL_NORMAL) {
			fetchUsers();

			JCUser.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					UserItem ui = (UserItem) JCUser.getSelectedItem();

					if (ui == null) {
						return;
					}
					currentUser = ui.toString();
					currentUserId = ui.getPb().getUserId();

					setMonth();
				}
			});
		} else {
			JCUser.setVisible(false);
		}

            java.awt.EventQueue.invokeLater(new Runnable() {

                public void run() {
                    setMonth();

                }
            });
		
	}

	public static String getTitle(int mon, int year) {
		return MonthNames.getFullMonthName(mon) + " " + Integer.toString(year);
	}

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jReport = new javax.swing.JEditorPane();
        plusMon = new javax.swing.JButton();
        minusMon = new javax.swing.JButton();
        jBPrint = new javax.swing.JButton();
        JCUser = new javax.swing.JComboBox();
        jBClose = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jReport.setContentType("text/html");
        jReport.setEditable(false);
        jReport.setText("<html>   <head>    </head>   <body>     <p style=\"margin-top: 0\">          Daten werden geladen....  </p>   </body> </html> ");
        jScrollPane1.setViewportView(jReport);

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

        jBPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/at/redeye/FrameWork/base/resources/icons/print.png"))); // NOI18N
        jBPrint.setText("Drucken");
        jBPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBPrintActionPerformed(evt);
            }
        });

        JCUser.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jBClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/at/redeye/FrameWork/base/resources/icons/fileclose.gif"))); // NOI18N
        jBClose.setText("Schließen");
        jBClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBCloseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 780, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(minusMon)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(plusMon)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 409, Short.MAX_VALUE)
                        .addComponent(jBPrint)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBClose))
                    .addComponent(JCUser, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(JCUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 382, Short.MAX_VALUE)
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(minusMon, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(plusMon))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jBClose)
                        .addComponent(jBPrint)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

	private void plusMonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_plusMonActionPerformed

		mon++;

		if (mon >= 12) {
			mon = 1;
			year++;
		}

		setMonth();
	}//GEN-LAST:event_plusMonActionPerformed

	private void minusMonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_minusMonActionPerformed

		mon--;

		if (mon <= 0) {
			mon = 12;
			year--;
		}

		setMonth();
	}//GEN-LAST:event_minusMonActionPerformed

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
				if (canClose()) {
					getTransaction().rollback();
					close();
				}
			}
		};
	}//GEN-LAST:event_jBCloseActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox JCUser;
    private javax.swing.JButton jBClose;
    private javax.swing.JButton jBPrint;
    private javax.swing.JEditorPane jReport;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton minusMon;
    private javax.swing.JButton plusMon;
    // End of variables declaration//GEN-END:variables

	private void setMonth() {
		setTitle("Monatsbericht " + getTitle(mon, year) + " für " + currentUser);

		ReportRenderer rr = new MonthReportPerUserRenderer(root,getTransaction(),
				mon, year, currentUser, currentUserId, holidays);

		if (rr.collectData()) {
			jReport.setText(rr.render());
			jReport.setCaretPosition(0);
		}

	}

	private void fetchUsers() {
		new AutoLogger("fetchUsers") {

			@Override
			public void do_stuff() throws Exception {

				// auf locked wollen wir hier nicht einschränken,
				// denn auch bei Usersn, die gesperrt sind dürfen wir
				// nachschauen

				List<DBStrukt> users = getTransaction().fetchTable(new DBPb(),
						" order by " + getTransaction().markColumn("surname"));

				JCUser.removeAllItems();

				for (DBStrukt s : users) {
					DBPb pb = (DBPb) s;

					UserItem ui = new UserItem(pb.getUserName(), pb);

					JCUser.addItem(ui);

					if (pb.getUserId() == currentUserId) {
						JCUser.setSelectedItem(ui);
					}
				}
			}
		};
	}

}

/*
 * ExtraHolidays.java
 *
 * Created on 13. März 2009, 11:57
 */

package at.redeye.Zeiterfassung;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;

import org.joda.time.DateMidnight;

import at.redeye.FrameWork.base.AutoMBox;
import at.redeye.FrameWork.base.BaseDialog;
import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.base.bindtypes.DBStrukt;
import at.redeye.FrameWork.base.tablemanipulator.TableManipulator;
import at.redeye.FrameWork.base.tablemanipulator.validators.DateValidator;
import at.redeye.FrameWork.utilities.StringUtils;
import at.redeye.Zeiterfassung.bindtypes.DBExtraHolidays;

/**
 * 
 * @author martin
 */
public class ExtraHolidays extends BaseDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	List<DBStrukt> values = new ArrayList<DBStrukt>();
	TableManipulator tm;
	MainWin mainwin;

	/** Creates new form ExtraHolidays */
	public ExtraHolidays(final Root root, MainWin mainwin) {
		super(root, "Zusätzliche Feiertage");

		this.mainwin = mainwin;

		initComponents();

		DBExtraHolidays e = new DBExtraHolidays();

		tm = new TableManipulator(root, jTContent, e);

		tm.hide(e.id);
		tm.hide(e.hist.lo_user);
		tm.hide(e.hist.lo_zeit);

		tm.setEditable(e.date);
		tm.setEditable(e.title);
		tm.setEditable(e.official_holiday);

		tm.setValidator(e.date, new DateValidator());

		tm.prepareTable();

		feed_table(false);

		if (values.isEmpty())
			newEntry();

		tm.autoResize();

            registerHelpWin(new Runnable() {

                public void run() {
                    invokeDialogUnique(new LocalHelpWin(root, "ExtraHolidays"));
                }
            });
	}

	private boolean check_entries() {

		for (int i = 0; i < values.size(); i++) {
			DBExtraHolidays e1 = (DBExtraHolidays) values.get(i);
			Date t1 = (Date) e1.date.getValue();
			DateMidnight d1 = new DateMidnight(t1);

			for (int j = 0; j < values.size(); j++) {
				if (i == j)
					continue;

				DBExtraHolidays e2 = (DBExtraHolidays) values.get(j);

				Date t2 = (Date) e2.date.getValue();

				DateMidnight d2 = new DateMidnight(t2);

				if (d2.equals(d1)) {
					JOptionPane.showMessageDialog(
							null,
							StringUtils.autoLineBreak("Eintrag " + i + 1
									+ " und Eintrag " + j + 1
									+ "sind der selbe Tag."), "Fehler",
							JOptionPane.OK_OPTION);
					return false;
				}
			}
		}

		return true;
	}

	private void feed_table() {
		feed_table(false);
	}

	private void feed_table(boolean autombox) {
		new AutoMBox("ExtraHolidays", autombox) {

			@Override
			public void do_stuff() throws Exception {
				tm.clear();
				clearEdited();

				values = getTransaction().fetchTable(new DBExtraHolidays());

				tm.addAll(values);
			}
		};
	}

	//<editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		jLTitle = new javax.swing.JLabel();
		jBHelp = new javax.swing.JButton();
		jScrollPane1 = new javax.swing.JScrollPane();
		jTContent = new javax.swing.JTable();
		jBClose = new javax.swing.JButton();
		jBDel = new javax.swing.JButton();
		jBNew = new javax.swing.JButton();
		jBSave = new javax.swing.JButton();

		setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

		jLTitle.setFont(new java.awt.Font("Dialog", 1, 18));
		jLTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		jLTitle.setText("Zusätzliche Feiertage");

		jBHelp.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/at/redeye/FrameWork/base/resources/icons/help.png"))); // NOI18N
		jBHelp.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jBHelpActionPerformed(evt);
			}
		});

		jTContent.setModel(new javax.swing.table.DefaultTableModel(
				new Object[][] { { null, null, null, null },
						{ null, null, null, null }, { null, null, null, null },
						{ null, null, null, null } }, new String[] { "Title 1",
						"Title 2", "Title 3", "Title 4" }));
		jScrollPane1.setViewportView(jTContent);

		jBClose.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/at/redeye/FrameWork/base/resources/icons/fileclose.gif"))); // NOI18N
		jBClose.setText("Schließen");
		jBClose.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jBCloseActionPerformed(evt);
			}
		});

		jBDel.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/at/redeye/FrameWork/base/resources/icons/edittrash.gif"))); // NOI18N
		jBDel.setText("Löschen");
		jBDel.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jBDelActionPerformed(evt);
			}
		});

		jBNew.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/at/redeye/FrameWork/base/resources/icons/bookmark.png"))); // NOI18N
		jBNew.setText("Neu");
		jBNew.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jBNewActionPerformed(evt);
			}
		});

		jBSave.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/at/redeye/FrameWork/base/resources/icons/button_ok.gif"))); // NOI18N
		jBSave.setText("Speichern");
		jBSave.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jBSaveActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						javax.swing.GroupLayout.Alignment.TRAILING,
						layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.TRAILING)
												.addComponent(
														jScrollPane1,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														602, Short.MAX_VALUE)
												.addGroup(
														layout.createSequentialGroup()
																.addComponent(
																		jBSave)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(
																		jBNew)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(
																		jBDel)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED,
																		156,
																		Short.MAX_VALUE)
																.addComponent(
																		jBClose))
												.addGroup(
														layout.createSequentialGroup()
																.addComponent(
																		jLTitle,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		558,
																		Short.MAX_VALUE)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(
																		jBHelp,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		32,
																		javax.swing.GroupLayout.PREFERRED_SIZE)))
								.addContainerGap()));
		layout.setVerticalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(jBHelp)
												.addComponent(jLTitle))
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(jScrollPane1,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										281, Short.MAX_VALUE)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING)
												.addGroup(
														layout.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
																.addComponent(
																		jBSave)
																.addComponent(
																		jBNew)
																.addComponent(
																		jBDel))
												.addComponent(jBClose))
								.addContainerGap()));

		pack();
	}//</editor-fold>//GEN-END:initComponents

	private void jBHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBHelpActionPerformed

		callHelpWin();

	}//GEN-LAST:event_jBHelpActionPerformed

	private void jBCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBCloseActionPerformed

		new AutoMBox(getTitle()) {
			@Override
			public void do_stuff() throws Exception {

				if (canClose()) {
					getTransaction().rollback();
					close();

					java.awt.EventQueue.invokeLater(new Runnable() {

						public void run() {
							mainwin.handleHolidays();
						}
					});
				}
			}
		};

	}//GEN-LAST:event_jBCloseActionPerformed

	private void jBDelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBDelActionPerformed
		// TODO add your handling code here:
		if (!checkAnyAndSingleSelection(jTContent))
			return;

		final int i = jTContent.getSelectedRow();

		if (i < 0 || i >= values.size())
			return;

		new AutoMBox(getTitle()) {

			public void do_stuff() throws Exception {
				DBExtraHolidays entry = (DBExtraHolidays) values.get(i);

				getTransaction().updateValues(
						"delete from " + entry.getName() + " where "
								+ getTransaction().markColumn("id") + " = '"
								+ ((Integer) entry.id.getValue()).toString()
								+ "'");

				setEdited();
				values.remove(i);
				tm.remove(i);
			}
		};

	}//GEN-LAST:event_jBDelActionPerformed

	private void jBNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBNewActionPerformed
		newEntry();
	}//GEN-LAST:event_jBNewActionPerformed

	private void jBSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSaveActionPerformed
		// TODO add your handling code here:

		if (check_entries() == false)
			return;

		new AutoMBox(getTitle()) {

			@Override
			public void do_stuff() throws Exception {

				for (Integer i : tm.getEditedRows()) {
					DBExtraHolidays entry = (DBExtraHolidays) values.get(i);
					insertOrUpdateValues(entry);
				}

				getTransaction().commit();
				feed_table();
			}

			private void insertOrUpdateValues(DBExtraHolidays entry)
					throws Exception {

				DBExtraHolidays e = new DBExtraHolidays();

				e.id.loadFromCopy(entry.id.getValue());

				if (getTransaction().fetchTableWithPrimkey(e) == true) {
					entry.hist.setAeHist(root.getUserName());
					getTransaction().updateValues(entry);
				} else {
					getTransaction().insertValues(entry);
				}
			}

		};

	}//GEN-LAST:event_jBSaveActionPerformed

	//Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton jBClose;
	private javax.swing.JButton jBDel;
	private javax.swing.JButton jBHelp;
	private javax.swing.JButton jBNew;
	private javax.swing.JButton jBSave;
	private javax.swing.JLabel jLTitle;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JTable jTContent;

	//End of variables declaration//GEN-END:variables

	private void newEntry() {
		DBExtraHolidays e = new DBExtraHolidays();

		e.hist.setAnHist(root.getUserName());
		values.add(e);
		tm.add(e);
	}

	@Override
	public boolean canClose() {
		int ret = checkSave(tm);

		if (ret == 1) {
			jBSaveActionPerformed(null);
		} else if (ret == -1) {
			return false;
		}
		return true;
	}
}

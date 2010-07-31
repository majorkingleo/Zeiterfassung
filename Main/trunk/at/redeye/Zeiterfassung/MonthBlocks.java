/*
 * MonthBlocks.java
 *
 * Created on 2. April 2009, 23:13
 */

package at.redeye.Zeiterfassung;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import at.redeye.FrameWork.base.AutoMBox;
import at.redeye.FrameWork.base.BaseDialog;
import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.base.bindtypes.DBDateTime;
import at.redeye.FrameWork.base.bindtypes.DBStrukt;
import at.redeye.FrameWork.base.tablemanipulator.TableManipulator;
import at.redeye.FrameWork.base.tablemanipulator.validators.DateValidator;
import at.redeye.FrameWork.base.transaction.Transaction;
import at.redeye.SqlDBInterface.SqlDBIO.impl.TableBindingNotRegisteredException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.UnsupportedDBDataTypeException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.WrongBindFileFormatException;
import at.redeye.Zeiterfassung.bindtypes.DBMonthBlocks;
import at.redeye.Zeiterfassung.bindtypes.UserQuery;

/**
 * 
 * @author martin
 */
public class MonthBlocks extends BaseDialog {
	private static final long serialVersionUID = 1L;

	List<DBStrukt> values = new ArrayList<DBStrukt>();
	TableManipulator tm;
	StringBuffer date_all = new StringBuffer();

	/** Creates new form MonthBlocks */
	public MonthBlocks(Root root) {
		super(root, "Editierbarkeit der Monate");

		initComponents();

		DBMonthBlocks upm = new DBMonthBlocks(new UserQuery(getTransaction()));

		tm = new TableManipulator(root, jTContent, upm);

		tm.hide(upm.id);
		tm.hide(upm.hist.lo_user);
		tm.hide(upm.hist.lo_zeit);

		tm.setEditable(upm.user);
		tm.setEditable(upm.from);

		tm.setValidator(upm.from, new DateValidator());

		tm.prepareTable();

		feed_table(false);

		tm.autoResize();

		date_all.append(DBDateTime.getDateStr(new Date()));

		bindVar(jTDateAll, date_all);

		var_to_gui();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed"
	// desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		jBSave = new javax.swing.JButton();
		jBNew = new javax.swing.JButton();
		jBDel = new javax.swing.JButton();
		jBClose = new javax.swing.JButton();
		jScrollPane1 = new javax.swing.JScrollPane();
		jTContent = new javax.swing.JTable();
		jLTitle = new javax.swing.JLabel();
		jBHelp = new javax.swing.JButton();
		jBSetAll = new javax.swing.JButton();
		jSeparator1 = new javax.swing.JSeparator();
		jLabel1 = new javax.swing.JLabel();
		jTDateAll = new javax.swing.JTextField();

		setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

		jBSave.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/at/redeye/FrameWork/base/resources/icons/button_ok.gif"))); // NOI18N
		jBSave.setText("Speichern");
		jBSave.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jBSaveActionPerformed(evt);
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

		jBDel.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/at/redeye/FrameWork/base/resources/icons/edittrash.gif"))); // NOI18N
		jBDel.setText("Löschen");
		jBDel.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jBDelActionPerformed(evt);
			}
		});

		jBClose.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/at/redeye/FrameWork/base/resources/icons/fileclose.gif"))); // NOI18N
		jBClose.setText("Schließen");
		jBClose.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jBCloseActionPerformed(evt);
			}
		});

		jTContent.setModel(new javax.swing.table.DefaultTableModel(
				new Object[][] { { null, null, null, null },
						{ null, null, null, null }, { null, null, null, null },
						{ null, null, null, null } }, new String[] { "Title 1",
						"Title 2", "Title 3", "Title 4" }));
		jScrollPane1.setViewportView(jTContent);

		jLTitle.setFont(new java.awt.Font("Dialog", 1, 18));
		jLTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		jLTitle.setText("Editierbarkeit der Monate");

		jBHelp.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/at/redeye/FrameWork/base/resources/icons/help.png"))); // NOI18N
		jBHelp.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jBHelpActionPerformed(evt);
			}
		});

		jBSetAll.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/at/redeye/FrameWork/base/resources/icons/player_end.png"))); // NOI18N
		jBSetAll.setText("Festlegen");
		jBSetAll.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jBSetAllActionPerformed(evt);
			}
		});

		jLabel1.setText("Datum für alle festlegen:");

		jTDateAll.setText("2009-12-12");

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(
														jScrollPane1,
														javax.swing.GroupLayout.Alignment.TRAILING,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														699, Short.MAX_VALUE)
												.addComponent(
														jSeparator1,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														699, Short.MAX_VALUE)
												.addGroup(
														javax.swing.GroupLayout.Alignment.TRAILING,
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
																		253,
																		Short.MAX_VALUE)
																.addComponent(
																		jBClose))
												.addGroup(
														javax.swing.GroupLayout.Alignment.TRAILING,
														layout.createSequentialGroup()
																.addComponent(
																		jLTitle,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		655,
																		Short.MAX_VALUE)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(
																		jBHelp,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		32,
																		javax.swing.GroupLayout.PREFERRED_SIZE))
												.addGroup(
														layout.createSequentialGroup()
																.addComponent(
																		jLabel1)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(
																		jTDateAll,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(
																		jBSetAll)))
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
								.addGap(20, 20, 20)
								.addComponent(jSeparator1,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(jBSetAll)
												.addComponent(
														jTDateAll,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(jLabel1))
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(jScrollPane1,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										265, Short.MAX_VALUE)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
	}// </editor-fold>//GEN-END:initComponents

	private void jBSaveActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jBSaveActionPerformed
		// TODO add your handling code here:

		new AutoMBox(getTitle()) {

			@Override
			public void do_stuff() throws Exception {

				for (Integer i : tm.getEditedRows()) {
					DBMonthBlocks entry = (DBMonthBlocks) values.get(i);
					insertOrUpdateValues(entry);
				}

				getTransaction().commit();
				feed_table();
			}

		};
	}// GEN-LAST:event_jBSaveActionPerformed

	private void jBNewActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jBNewActionPerformed
		newEntry();
	}// GEN-LAST:event_jBNewActionPerformed

	private void jBDelActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jBDelActionPerformed
		// TODO add your handling code here:
		if (!checkAnyAndSingleSelection(jTContent))
			return;

		final int i = jTContent.getSelectedRow();

		if (i < 0 || i >= values.size())
			return;

		new AutoMBox(getTitle()) {

			public void do_stuff() throws Exception {
				DBMonthBlocks entry = (DBMonthBlocks) values.get(i);

				getTransaction().updateValues(
						"delete from " + entry.getName() + " where "
								+ getTransaction().markColumn("id") + " = '"
								+ ((Integer) entry.id.getValue()).toString()
								+ "'");

				values.remove(i);
				tm.remove(i);
				setEdited();
			}
		};
	}// GEN-LAST:event_jBDelActionPerformed

	private void jBCloseActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jBCloseActionPerformed
		new AutoMBox(getTitle()) {

			@Override
			public void do_stuff() throws Exception {
				if (canClose()) {
					getTransaction().rollback();
					close();
				}
			}
		};
	}// GEN-LAST:event_jBCloseActionPerformed

	private void jBHelpActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jBHelpActionPerformed

		invokeDialogUnique(new LocalHelpWin(root, "MonthBlocks"));

	}// GEN-LAST:event_jBHelpActionPerformed

	private void jBSetAllActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jBSetAllActionPerformed

		gui_to_var();

		tm.clear();

		for (int i = 0; i < values.size(); i++) {
			DBMonthBlocks block = (DBMonthBlocks) values.get(i);

			block.from.loadFromString(date_all + " 00:00:00");
		}

		tm.addAll(values);
		tm.setEditedAll();

		setEdited();

	}// GEN-LAST:event_jBSetAllActionPerformed

	private void feed_table() {
		feed_table(true);
	}

	private void feed_table(boolean autombox) {
		new AutoMBox("MonthBlocks", autombox) {

			@Override
			public void do_stuff() throws Exception {
				tm.clear();
				clearEdited();

				Transaction trans = getTransaction();
				values = trans.fetchTable(new DBMonthBlocks(new UserQuery(
						getTransaction())));

				for (DBStrukt entry : values) {
					tm.add(entry);
				}
			}
		};
	}

	private void insertOrUpdateValues(DBMonthBlocks entry)
			throws UnsupportedDBDataTypeException,
			WrongBindFileFormatException, SQLException,
			TableBindingNotRegisteredException, IOException {
		DBMonthBlocks e = new DBMonthBlocks();

		e.loadFromCopy(entry);

		if (getTransaction().fetchTableWithPrimkey(e) == false) {
			getTransaction().insertValues(e);
		} else {
			entry.hist.setAeHist(root.getUserName());
			getTransaction().updateValues(entry);
		}
	}

	void newEntry() {
		newEntry(false);
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

	private void newEntry(boolean autombox) {
		new AutoMBox("MonthBlocks", autombox) {

			@Override
			public void do_stuff() throws Exception {

				DBMonthBlocks upm = new DBMonthBlocks(new UserQuery(
						getTransaction()));

				upm.id.loadFromCopy(getNewSequenceValue(upm.getName()));
				upm.hist.setAnHist(root.getUserName());

				values.add(upm);
				tm.add(upm, true);
			}
		};
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton jBClose;
	private javax.swing.JButton jBDel;
	private javax.swing.JButton jBHelp;
	private javax.swing.JButton jBNew;
	private javax.swing.JButton jBSave;
	private javax.swing.JButton jBSetAll;
	private javax.swing.JLabel jLTitle;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JSeparator jSeparator1;
	private javax.swing.JTable jTContent;
	private javax.swing.JTextField jTDateAll;
	// End of variables declaration//GEN-END:variables

}

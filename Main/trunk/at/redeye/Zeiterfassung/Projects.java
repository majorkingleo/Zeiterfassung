/*
 * JobTypes.java
 *
 * Created on 27. Februar 2009, 11:10
 */

package at.redeye.Zeiterfassung;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import at.redeye.FrameWork.base.AutoMBox;
import at.redeye.FrameWork.base.BaseDialog;
import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.base.bindtypes.DBStrukt;
import at.redeye.FrameWork.base.tablemanipulator.TableManipulator;
import at.redeye.FrameWork.base.transaction.Transaction;
import at.redeye.SqlDBInterface.SqlDBIO.impl.TableBindingNotRegisteredException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.UnsupportedDBDataTypeException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.WrongBindFileFormatException;
import at.redeye.Zeiterfassung.bindtypes.DBCustomers;
import at.redeye.Zeiterfassung.bindtypes.DBProjects;

/**
 * 
 * @author martin
 */
public class Projects extends BaseDialog {

	private static final long serialVersionUID = 1L;
	TableManipulator tm;
	List<DBStrukt> values = new Vector<DBStrukt>();
	DBCustomers cust;

	public Projects(final Root root, DBCustomers cust) {
		super(root, "Projekte");
		initComponents();

		this.cust = cust;

		jLTitle.setText(getTitle(cust));
                setTitle(getTitle(cust));

		DBProjects projects = new DBProjects();

		tm = new TableManipulator(root, jTContent, projects);

		tm.hide(projects.id);
		tm.hide(projects.hist.lo_user);
		tm.hide(projects.hist.lo_zeit);
		tm.hide(projects.customer);

		tm.setEditable(projects.name);
		tm.setEditable(projects.currency);
		tm.setEditable(projects.hourly_rate);
		tm.setEditable(projects.locked);
		tm.setEditable(projects.autocreate_subproject);

		tm.prepareTable();

		feed_table(false);

		if (values.size() == 0)
			newEntry(false);

		tm.autoResize();

            registerHelpWin(new Runnable() {

                public void run() {
                    invokeDialogUnique(new LocalHelpWin(root, "Projects"));
                }
            });
	}

	private String getTitle(DBCustomers cust) {
		return MlM("Projekte") + " " + cust.name.toString() + " - "
				+ cust.comment.toString();
	}

	private void feed_table() {
		feed_table(true);
	}

	private void feed_table(boolean autombox) {
		new AutoMBox(getTitle(), autombox) {

			@Override
			public void do_stuff() throws Exception {

				tm.clear();
				clearEdited();

				DBProjects projects = new DBProjects();

				Transaction trans = getTransaction();
				values = trans.fetchTable(projects,
						"where " + trans.markColumn(projects.customer) + "="
								+ cust.id.toString() + " order by " + trans.markColumn(projects.name));

				for (DBStrukt entry : values) {
					tm.add(entry);
				}
			}
		};
	}

	private void newEntry() {
		newEntry(true);
	}

	private void newEntry(boolean autombox) {
		new AutoMBox(getTitle(), autombox) {

			@Override
			public void do_stuff() throws Exception {

				DBProjects jt = new DBProjects();

				jt.id.loadFromCopy(new Integer(
						getNewSequenceValue(jt.getName())));
				jt.customer.loadFromCopy(cust.id.getValue());
				jt.hourly_rate.loadFromCopy(cust.hourly_rate.getValue());
				jt.currency.loadFromCopy(cust.currency.getValue());
				jt.hist.setAnHist(root.getUserName());
				jt.autocreate_subproject
						.loadFromString(root
								.getSetup()
								.getConfig(
										AppConfigDefinitions.AutoCreateSupProjectDefaultValue));

				tm.add(jt, true);
				values.add(jt);
			}
		};
	}

	private void insertOrUpdateValues(DBProjects entry)
			throws UnsupportedDBDataTypeException,
			WrongBindFileFormatException, SQLException,
			TableBindingNotRegisteredException, IOException {

		DBProjects e = new DBProjects();

		e.loadFromCopy(entry);

		if (getTransaction().fetchTableWithPrimkey(e) == false) {
			getTransaction().insertValues(e);
		} else {
			getTransaction().updateValues(entry);
		}
	}

	//<editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		jScrollPane1 = new javax.swing.JScrollPane();
		jTContent = new javax.swing.JTable();
		jBSave = new javax.swing.JButton();
		jBNew = new javax.swing.JButton();
		jBDel = new javax.swing.JButton();
		jBClose = new javax.swing.JButton();
		jLTitle = new javax.swing.JLabel();
		jBHelp = new javax.swing.JButton();
		jSeparator1 = new javax.swing.JSeparator();
		jPanel1 = new javax.swing.JPanel();
		jBSubProjects = new javax.swing.JButton();

		setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

		jTContent.setModel(new javax.swing.table.DefaultTableModel(
				new Object[][] { { null, null, null, null },
						{ null, null, null, null }, { null, null, null, null },
						{ null, null, null, null } }, new String[] { "Title 1",
						"Title 2", "Title 3", "Title 4" }));
		jScrollPane1.setViewportView(jTContent);

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

		jLTitle.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
		jLTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		jLTitle.setText("Projekte");

		jBHelp.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/at/redeye/FrameWork/base/resources/icons/help.png"))); // NOI18N
		jBHelp.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jBHelpActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(
				jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 174,
				Short.MAX_VALUE));
		jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 37,
				Short.MAX_VALUE));

		jBSubProjects.setText("Unterprojekte");
		jBSubProjects.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jBSubProjectsActionPerformed(evt);
			}
		});

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
												.addGroup(
														layout.createSequentialGroup()
																.addGroup(
																		layout.createParallelGroup(
																				javax.swing.GroupLayout.Alignment.LEADING)
																				.addGroup(
																						layout.createSequentialGroup()
																								.addComponent(
																										jLTitle,
																										javax.swing.GroupLayout.DEFAULT_SIZE,
																										727,
																										Short.MAX_VALUE)
																								.addPreferredGap(
																										javax.swing.LayoutStyle.ComponentPlacement.RELATED))
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
																										jBDel)))
																.addGap(12, 12,
																		12)
																.addGroup(
																		layout.createParallelGroup(
																				javax.swing.GroupLayout.Alignment.TRAILING)
																				.addComponent(
																						jBHelp,
																						javax.swing.GroupLayout.PREFERRED_SIZE,
																						32,
																						javax.swing.GroupLayout.PREFERRED_SIZE)
																				.addComponent(
																						jBClose))
																.addContainerGap())
												.addGroup(
														layout.createSequentialGroup()
																.addComponent(
																		jBSubProjects)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(
																		jPanel1,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		Short.MAX_VALUE)
																.addGap(570,
																		570,
																		570))
												.addGroup(
														javax.swing.GroupLayout.Alignment.TRAILING,
														layout.createSequentialGroup()
																.addGroup(
																		layout.createParallelGroup(
																				javax.swing.GroupLayout.Alignment.TRAILING)
																				.addComponent(
																						jScrollPane1,
																						javax.swing.GroupLayout.Alignment.LEADING,
																						javax.swing.GroupLayout.DEFAULT_SIZE,
																						858,
																						Short.MAX_VALUE)
																				.addComponent(
																						jSeparator1,
																						javax.swing.GroupLayout.DEFAULT_SIZE,
																						858,
																						Short.MAX_VALUE))
																.addContainerGap()))));
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
								.addComponent(jSeparator1,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										3,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(
														jPanel1,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(jBSubProjects))
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(jScrollPane1,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										358, Short.MAX_VALUE)
								.addGap(18, 18, 18)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(jBSave)
												.addComponent(jBNew)
												.addComponent(jBDel)
												.addComponent(jBClose))
								.addContainerGap()));

		pack();
	}//</editor-fold>//GEN-END:initComponents

	private void jBSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSaveActionPerformed
	

		new AutoMBox(getTitle()) {

			@Override
			public void do_stuff() throws Exception {
				for (Integer i : tm.getEditedRows()) {

					DBProjects entry = (DBProjects) values.get(i);

					entry.hist.setAeHist(root.getUserName());

					insertOrUpdateValues(entry);
				}

				getTransaction().commit();
				feed_table();
			}
		};
	}//GEN-LAST:event_jBSaveActionPerformed

	private void jBNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBNewActionPerformed
	
		newEntry();
	}//GEN-LAST:event_jBNewActionPerformed

	private void jBCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBCloseActionPerformed
	
		if (canClose()) {
			close();
		}
	}//GEN-LAST:event_jBCloseActionPerformed

	private void jBDelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBDelActionPerformed
	
		if (!checkAnyAndSingleSelection(jTContent))
			return;

		final int i = tm.getSelectedRow();

		if (i < 0 || i >= values.size())
			return;

		new AutoMBox(getTitle()) {

			public void do_stuff() throws Exception {
				DBProjects entry = (DBProjects) values.get(i);

				getTransaction().updateValues(
						"delete from " + entry.getName() + " where "
								+ getTransaction().markColumn("id") + " = "
								+ ((Integer) entry.id.getValue()).toString());

				values.remove(i);
				tm.remove(i);
				setEdited();
			}
		};

	}//GEN-LAST:event_jBDelActionPerformed

	private void jBHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBHelpActionPerformed

		callHelpWin();

	}//GEN-LAST:event_jBHelpActionPerformed

	private void jBSubProjectsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSubProjectsActionPerformed
		// TODO add your handling code here:
		if (!checkAnyAndSingleSelection(jTContent)) {
			return;
		}

		final int i = jTContent.getSelectedRow();

		java.awt.EventQueue.invokeLater(new Runnable() {

			public void run() {
				new SubProjects(root, (DBProjects) values.get(i))
						.setVisible(true);
			}
		});
	}//GEN-LAST:event_jBSubProjectsActionPerformed

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton jBClose;
	private javax.swing.JButton jBDel;
	private javax.swing.JButton jBHelp;
	private javax.swing.JButton jBNew;
	private javax.swing.JButton jBSave;
	private javax.swing.JButton jBSubProjects;
	private javax.swing.JLabel jLTitle;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JSeparator jSeparator1;
	private javax.swing.JTable jTContent;

	// End of variables declaration//GEN-END:variables

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

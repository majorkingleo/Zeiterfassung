/*
 * JobTypes.java
 *
 * Created on 27. Februar 2009, 11:10
 */

package at.redeye.Zeiterfassung;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import at.redeye.FrameWork.base.AutoMBox;
import at.redeye.FrameWork.base.BaseDialog;
import at.redeye.FrameWork.base.BaseDialogBase;
import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.base.bindtypes.DBStrukt;
import at.redeye.FrameWork.base.tablemanipulator.TableManipulator;
import at.redeye.FrameWork.base.transaction.Transaction;
import at.redeye.FrameWork.utilities.StringUtils;
import at.redeye.SqlDBInterface.SqlDBIO.impl.TableBindingNotRegisteredException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.UnsupportedDBDataTypeException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.WrongBindFileFormatException;
import at.redeye.Zeiterfassung.bindtypes.DBCustomerAddresses;
import at.redeye.Zeiterfassung.bindtypes.DBCustomers;
import at.redeye.Zeiterfassung.bindtypes.DBProjects;

/**
 * 
 * @author martin
 */
public class Customers extends BaseDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(Customers.class
			.getSimpleName());
	TableManipulator tm;
	List<DBStrukt> values = new ArrayList<DBStrukt>();

	/**
	 * Creates new form JobTypes
	 * 
	 * @param root
	 */
	public Customers(final Root root) {
		super(root, "Kunden");
		initComponents();

		DBCustomers cust = new DBCustomers();

		tm = new TableManipulator(root, jTContent, cust);

		tm.hide(cust.id);
		tm.hide(cust.hist.lo_user);
		tm.hide(cust.hist.lo_zeit);

		tm.setEditable(cust.name);
		tm.setEditable(cust.locked);
		tm.setEditable(cust.comment);
		tm.setEditable(cust.currency);
		tm.setEditable(cust.hourly_rate);
		tm.setEditable(cust.locked);

		tm.prepareTable();

		feed_table(false);

		if (values.size() == 0)
			newEntry(false);

		tm.autoResize();

               registerHelpWin(new Runnable() {

                public void run() {
                    invokeDialogUnique((BaseDialogBase) new LocalHelpWin(root, "Customers"));
                }
                });
	}

	private boolean check_entries() {

		int counter1 = 0;

		for (DBStrukt entry : values) {
			counter1++;

			DBCustomers type = (DBCustomers) entry;

			int counter2 = 0;

			for (DBStrukt sub : values) {
				counter2++;

				if (type.equals(sub))
					continue;

				DBCustomers sub_type = (DBCustomers) sub;

				// if(
				// type.type.toString().equalsIgnoreCase(sub_type.type.toString())
				// ) {
				if (type.name.toString().equalsIgnoreCase(
						sub_type.name.toString())) {
					logger.error("Eintrag " + counter1 + " und Eintrag "
							+ counter2 + " gleichen sich.");
					JOptionPane.showMessageDialog(
							null,
							StringUtils.autoLineBreak("Eintrag " + counter1
									+ " und Eintrag " + counter2
									+ " gleichen sich."), "Fehler",
							JOptionPane.OK_OPTION);
					return false;
				}
				// }
			}

			if (!type.name.toString().matches("....*")) {
				logger.error("Bei Eintrag "
						+ counter1
						+ " fehlt der Name, "
						+ "oder der eingegebene Text '"
						+ (type.name.getValue().toString().isEmpty() ? "   "
								: type.name.getValue()) + "' ist zu kurz.");
				JOptionPane
						.showMessageDialog(
								null,
								StringUtils.autoLineBreak("Bei Eintrag "
										+ counter1
										+ " fehlt der Name, "
										+ "oder der eingegebene Text '"
										+ (type.name.getValue().toString()
												.isEmpty() ? "   " : type.name
												.getValue()) + "' ist zu kurz."),
								"Fehler", JOptionPane.OK_OPTION);
				return false;
			}
		}

		return true;
	}

	private boolean entriesExist(final DBCustomers entry) {
		try {
			DBCustomerAddresses cust_addr = new DBCustomerAddresses();

			List<DBStrukt> res = getTransaction().fetchTable(
					cust_addr,
					"where " + getTransaction().markColumn(cust_addr.customer)
							+ " = '" + entry.id.toString() + "'");

			if (res.size() > 0) {
				return true;
			}

			DBProjects projects = new DBProjects();

			res = getTransaction().fetchTable(
					projects,
					"where " + getTransaction().markColumn(cust_addr.customer)
							+ " = '" + entry.id.toString() + "'");

			if (res.size() > 0) {
				return true;
			}
		} catch (Exception ex) {
			logger.error("Exception: " + ex.toString() + "\n"
					+ ex.getLocalizedMessage());
			ex.printStackTrace();
		}

		return false;
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

				Transaction trans = getTransaction();
				values = trans.fetchTable(new DBCustomers());

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

				DBCustomers jt = new DBCustomers();

				jt.id.loadFromCopy(new Integer(
						getNewSequenceValue(jt.getName())));

				jt.hist.setAnHist(root.getUserName());

				tm.add(jt, true);
				values.add(jt);
			}
		};
	}

	private void insertOrUpdateValues(DBCustomers entry)
			throws UnsupportedDBDataTypeException,
			WrongBindFileFormatException, SQLException,
			TableBindingNotRegisteredException, IOException {

		DBCustomers e = new DBCustomers();

		e.loadFromCopy(entry);

		if (getTransaction().fetchTableWithPrimkey(e) == false) {
			getTransaction().insertValues(e);
		} else {
			getTransaction().updateValues(entry);
		}
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */

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
		jPanel1 = new javax.swing.JPanel();
		jBAddresses = new javax.swing.JButton();
		jBProjects = new javax.swing.JButton();
		jSeparator1 = new javax.swing.JSeparator();

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
		jLTitle.setText("Kunden");

		jBHelp.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/at/redeye/FrameWork/base/resources/icons/help.png"))); // NOI18N
		jBHelp.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jBHelpActionPerformed(evt);
			}
		});

		jBAddresses.setText("Addressen");
		jBAddresses.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jBAddressesActionPerformed(evt);
			}
		});

		jBProjects.setText("Projekte");
		jBProjects.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jBProjectsActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(
				jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout
				.setHorizontalGroup(jPanel1Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel1Layout
										.createSequentialGroup()
										.addGap(0, 0, 0)
										.addComponent(jBAddresses)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(jBProjects)
										.addContainerGap(537, Short.MAX_VALUE)));
		jPanel1Layout
				.setVerticalGroup(jPanel1Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel1Layout
										.createSequentialGroup()
										.addGap(0, 0, 0)
										.addGroup(
												jPanel1Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																jBAddresses)
														.addComponent(
																jBProjects))
										.addContainerGap(0, Short.MAX_VALUE)));

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
														jSeparator1,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														760, Short.MAX_VALUE)
												.addComponent(
														jScrollPane1,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														760, Short.MAX_VALUE)
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
																		314,
																		Short.MAX_VALUE)
																.addComponent(
																		jBClose))
												.addGroup(
														javax.swing.GroupLayout.Alignment.TRAILING,
														layout.createSequentialGroup()
																.addGroup(
																		layout.createParallelGroup(
																				javax.swing.GroupLayout.Alignment.LEADING)
																				.addGroup(
																						layout.createSequentialGroup()
																								.addComponent(
																										jLTitle,
																										javax.swing.GroupLayout.DEFAULT_SIZE,
																										716,
																										Short.MAX_VALUE)
																								.addPreferredGap(
																										javax.swing.LayoutStyle.ComponentPlacement.RELATED))
																				.addComponent(
																						jPanel1,
																						javax.swing.GroupLayout.Alignment.TRAILING,
																						javax.swing.GroupLayout.DEFAULT_SIZE,
																						javax.swing.GroupLayout.DEFAULT_SIZE,
																						Short.MAX_VALUE))
																.addComponent(
																		jBHelp,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		32,
																		javax.swing.GroupLayout.PREFERRED_SIZE)))
								.addContainerGap()));
		layout.setVerticalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						javax.swing.GroupLayout.Alignment.TRAILING,
						layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(jBHelp)
												.addComponent(jLTitle))
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(jSeparator1,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										10,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGap(3, 3, 3)
								.addComponent(jPanel1,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(jScrollPane1,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										320, Short.MAX_VALUE)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
		// TODO add your handling code here:

		if (check_entries() == false)
			return;

		new AutoMBox(getTitle()) {

			@Override
			public void do_stuff() throws Exception {
				for (Integer i : tm.getEditedRows()) {

					DBCustomers entry = (DBCustomers) values.get(i);

					entry.hist.setAeHist(root.getUserName());

					insertOrUpdateValues(entry);
				}

				getTransaction().commit();
				feed_table();
			}
		};
	}//GEN-LAST:event_jBSaveActionPerformed

	private void jBNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBNewActionPerformed
		// TODO add your handling code here:
		newEntry();
	}//GEN-LAST:event_jBNewActionPerformed

	private void jBCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBCloseActionPerformed
		// TODO add your handling code here:
		if (canClose()) {
			close();
		}
	}//GEN-LAST:event_jBCloseActionPerformed

	private void jBDelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBDelActionPerformed
		// TODO add your handling code here:
		if (!checkAnyAndSingleSelection(jTContent))
			return;

		final int i = jTContent.getSelectedRow();

		if (i < 0 || i >= values.size()) {
			return;
		}

		// TODO es dürfen nur Einträge gelöscht werden für die es noch keine
		// Zeitstempel gibt
		if (entriesExist((DBCustomers) values.get(i))) {
			String msg = "Dieser Eintrag kann nicht mehr gelöscht, "
					+ "sondern nur gesperrt werden, "
					+ "da es bereits Einträge gibt, auf diesen Kunden verweisen.";

			logger.error(msg);
			JOptionPane.showMessageDialog(null, StringUtils.autoLineBreak(msg),
					"Fehler", JOptionPane.OK_OPTION);
			return;
		}

		new AutoMBox(getTitle()) {

			public void do_stuff() throws Exception {
				DBCustomers entry = (DBCustomers) values.get(i);

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

	}//GEN-LAST:event_jBDelActionPerformed

	private void jBHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBHelpActionPerformed

		callHelpWin();
	}//GEN-LAST:event_jBHelpActionPerformed

	private void jBAddressesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBAddressesActionPerformed

		if (!checkAnyAndSingleSelection(jTContent))
			return;

		final int i = jTContent.getSelectedRow();

		java.awt.EventQueue.invokeLater(new Runnable() {

			public void run() {
				new CustomersAddresses(root, (DBCustomers) values.get(i))
						.setVisible(true);
			}
		});
	}//GEN-LAST:event_jBAddressesActionPerformed

	private void jBProjectsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBProjectsActionPerformed

		if (!checkAnyAndSingleSelection(jTContent)) {
			return;
		}

		final int i = jTContent.getSelectedRow();

		java.awt.EventQueue.invokeLater(new Runnable() {

			public void run() {
				new Projects(root, (DBCustomers) values.get(i))
						.setVisible(true);
			}
		});

	}//GEN-LAST:event_jBProjectsActionPerformed

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton jBAddresses;
	private javax.swing.JButton jBClose;
	private javax.swing.JButton jBDel;
	private javax.swing.JButton jBHelp;
	private javax.swing.JButton jBNew;
	private javax.swing.JButton jBProjects;
	private javax.swing.JButton jBSave;
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

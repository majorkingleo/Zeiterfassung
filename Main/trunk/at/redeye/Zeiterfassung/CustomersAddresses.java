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

import org.apache.log4j.Logger;

import at.redeye.FrameWork.base.AutoMBox;
import at.redeye.FrameWork.base.BaseDialog;
import at.redeye.FrameWork.base.DefaultInsertOrUpdater;
import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.base.bindtypes.DBStrukt;
import at.redeye.FrameWork.base.tablemanipulator.TableManipulator;
import at.redeye.FrameWork.base.transaction.Transaction;
import at.redeye.SqlDBInterface.SqlDBIO.impl.TableBindingNotRegisteredException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.UnsupportedDBDataTypeException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.WrongBindFileFormatException;
import at.redeye.Zeiterfassung.bindtypes.DBCustomerAddresses;
import at.redeye.Zeiterfassung.bindtypes.DBCustomers;

/**
 * 
 * @author martin
 */
public class CustomersAddresses extends BaseDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	TableManipulator tm;
	List<DBStrukt> values = new ArrayList<DBStrukt>();
	DBCustomers cust;

	/**
	 * Creates new form JobTypes
	 * 
	 * @param root
	 */
	public CustomersAddresses(final Root root, DBCustomers cust) {
		super(root, "CustomerAddresses");
		initComponents();

		this.cust = cust;

		jLTitle.setText(getTitle(cust));
                setTitle(getTitle(cust));

		DBCustomerAddresses cust_addr = new DBCustomerAddresses();

		tm = new TableManipulator(root, jTContent, cust_addr);

		tm.hide(cust_addr.id);
		tm.hide(cust_addr.hist.lo_user);
		tm.hide(cust_addr.hist.lo_zeit);
		tm.hide(cust_addr.customer);

		tm.setEditable(cust_addr.name);
		tm.setEditable(cust_addr.city);
		tm.setEditable(cust_addr.company);
		tm.setEditable(cust_addr.contact);
		tm.setEditable(cust_addr.country);
		tm.setEditable(cust_addr.email);
		tm.setEditable(cust_addr.mobile);
		tm.setEditable(cust_addr.name);
		tm.setEditable(cust_addr.postcode);
		tm.setEditable(cust_addr.state);
		tm.setEditable(cust_addr.street);
		tm.setEditable(cust_addr.tel);

		tm.prepareTable();

		feed_table(false);

		if (values.size() == 0)
			newEntry(false);

		tm.autoResize();

            registerHelpWin(new Runnable() {

                public void run() {
                    invokeDialogUnique(new LocalHelpWin(root, "CustomerAddresses"));
                }
            });
	}

	private String getTitle(DBCustomers cust) {
		String ret = MlM("Addressen") + " " + cust.name.toString();

		if (!cust.comment.getValue().isEmpty())
			return ret + " - " + cust.comment.getValue().toString();

		return ret;
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

				DBCustomerAddresses cust_addr = new DBCustomerAddresses();

				Transaction trans = getTransaction();
				values = trans.fetchTable(cust_addr,
						"where " + trans.markColumn(cust_addr.customer) + "="
								+ cust.id.toString());

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

				DBCustomerAddresses jt = new DBCustomerAddresses();

				jt.id.loadFromCopy(new Integer(
						getNewSequenceValue(jt.getName())));
				jt.customer.loadFromCopy(cust.id.getValue());

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

		DBCustomerAddresses e = new DBCustomerAddresses();

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
		jBDel.setText("L??schen");
		jBDel.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jBDelActionPerformed(evt);
			}
		});

		jBClose.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/at/redeye/FrameWork/base/resources/icons/fileclose.gif"))); // NOI18N
		jBClose.setText("Schlie??en");
		jBClose.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jBCloseActionPerformed(evt);
			}
		});

		jLTitle.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
		jLTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		jLTitle.setText("Addressen");

		jBHelp.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/at/redeye/FrameWork/base/resources/icons/help.png"))); // NOI18N
		jBHelp.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jBHelpActionPerformed(evt);
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
														596, Short.MAX_VALUE)
												.addGroup(
														javax.swing.GroupLayout.Alignment.LEADING,
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
																		150,
																		Short.MAX_VALUE)
																.addComponent(
																		jBClose))
												.addGroup(
														layout.createSequentialGroup()
																.addComponent(
																		jLTitle,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		552,
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
						javax.swing.GroupLayout.Alignment.TRAILING,
						layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(jBHelp)
												.addComponent(
														jLTitle,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														22,
														javax.swing.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(jScrollPane1,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										303, Short.MAX_VALUE)
								.addGap(18, 18, 18)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(jBSave)
												.addComponent(jBNew)
												.addComponent(jBClose)
												.addComponent(jBDel))
								.addContainerGap()));

		pack();
	}//</editor-fold>//GEN-END:initComponents

	private void jBSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSaveActionPerformed
	

		new AutoMBox(getTitle()) {

			@Override
			public void do_stuff() throws Exception {
				for (Integer i : tm.getEditedRows()) {

					DBCustomerAddresses entry = (DBCustomerAddresses) values
							.get(i);					

                                        DefaultInsertOrUpdater.insertOrUpdateValuesWithPrimKey(getTransaction(), entry, entry.hist, root.getUserName());
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
				DBCustomerAddresses entry = (DBCustomerAddresses) values.get(i);

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

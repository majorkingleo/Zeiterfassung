/*
 * UserPerMonth.java
 *
 * Created on 1. März 2009, 17:13
 */

package at.redeye.Zeiterfassung;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;

import at.redeye.FrameWork.base.AutoMBox;
import at.redeye.FrameWork.base.BaseDialog;
import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.base.bindtypes.DBStrukt;
import at.redeye.FrameWork.base.bindtypes.DBValue;
import at.redeye.FrameWork.base.tablemanipulator.TableManipulator;
import at.redeye.FrameWork.base.tablemanipulator.TableValidator;
import at.redeye.FrameWork.base.tablemanipulator.validators.DateValidator;
import at.redeye.FrameWork.base.transaction.Transaction;
import at.redeye.FrameWork.utilities.Rounding;
import at.redeye.FrameWork.utilities.StringUtils;
import at.redeye.FrameWork.utilities.Time;
import at.redeye.SqlDBInterface.SqlDBIO.impl.TableBindingNotRegisteredException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.UnsupportedDBDataTypeException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.WrongBindFileFormatException;
import at.redeye.Zeiterfassung.AddUserWizard.WizardStepMonthSettingsForUser;
import at.redeye.Zeiterfassung.bindtypes.DBUserPerMonth;
import at.redeye.Zeiterfassung.bindtypes.UserQuery;

/**
 * 
 * @author martin
 */
public class UserPerMonth extends BaseDialog {

	private static final long serialVersionUID = 1L;

	List<DBStrukt> values = new Vector<DBStrukt>();
	TableManipulator tm;
	UserQuery user_query;

        private String MESSAGE_OVERLAP_ENTRY;

	/** Creates new form UserPerMonth */
	public UserPerMonth(Root root) {
		super(root, "Monatseinstellungen für die Benutzer");

		initComponents();

		initCommon();
	}

	public UserPerMonth(Root root, WizardStepMonthSettingsForUser aThis) {
		super(root, "Monatseinstellungen für die Benutzer");

		initComponents();

		jBClose.setVisible(false);

		initCommon();
	}

	private void initCommon() {
		user_query = new UserQuery(getTransaction());

                MESSAGE_OVERLAP_ENTRY = MlM( "Eintrag %d und Eintrag %d überschneiden sich!");

		DBUserPerMonth upm = new DBUserPerMonth(user_query);

		tm = new TableManipulator(root, jTContent, upm);

		tm.hide(upm.id);
		tm.hide(upm.hist.lo_user);
		tm.hide(upm.hist.lo_zeit);

		tm.setEditable(upm.from);
		tm.setEditable(upm.locked);
		tm.setEditable(upm.to);
		tm.setEditable(upm.usage);
		tm.setEditable(upm.user);
		tm.setEditable(upm.hours_per_week);
		tm.setEditable(upm.hours_holidays);
		tm.setEditable(upm.days_holidays);
		tm.setEditable(upm.hours_overtime);
		tm.setEditable(upm.days_per_week);
		tm.setEditable(upm.overtime_rule);

		tm.setValidator(upm.from, new DateValidator());
		tm.setValidator(upm.to, new DateValidator());
		tm.setValidator(upm.days_holidays, new TableValidator() {

			@Override
			public boolean wantDoLoadSelf() {
				return true;
			}

			@Override
			public boolean loadToValue(DBValue val, String s, int row) {
				if (!val.acceptString(s))
					return false;

				val.loadFromString(s);

				DBUserPerMonth u = (DBUserPerMonth) values.get(row);

				if (u == null)
					return false;

				double hpd = CalcMonthStuff.getHoursPerDay(u);

				if (hpd == 0)
					hpd = 8;

				Double days_holidays = (Double) val.getValue();
				Double hh = Rounding.rndDouble(days_holidays * hpd, 3);
				u.hours_holidays.loadFromCopy(hh);

				tm.updateValue(u.hours_holidays, row);

				return true;
			}

		});

		tm.setValidator(upm.hours_holidays, new TableValidator() {

			@Override
			public boolean wantDoLoadSelf() {
				return true;
			}

			@Override
			public boolean loadToValue(DBValue val, String s, int row) {
				if (!val.acceptString(s))
					return false;

				val.loadFromString(s);

				DBUserPerMonth u = (DBUserPerMonth) values.get(row);

				if (u == null)
					return false;

				double hpd = CalcMonthStuff.getHoursPerDay(u);

				if (hpd == 0)
					hpd = 8;

				Double hours_holidays = (Double) val.getValue();
				Double hh = Rounding.rndDouble(hours_holidays / hpd, 3);
				u.days_holidays.loadFromCopy(hh);

				tm.updateValue(u.days_holidays, row);

				return true;
			}

		});

		tm.prepareTable();

		feed_table(false);

		tm.autoResize();

            registerHelpWin(new Runnable() {

                public void run() {
                    invokeDialogUnique(new LocalHelpWin(root, "UserPerMonth"));
                }
            });
	}

	private boolean check_entries() {
		int counter1 = 0;

		for (DBStrukt s : values) {
			counter1++;

			DBUserPerMonth upm1 = (DBUserPerMonth) s;
			int uid1 = (Integer) upm1.user.getValue();
			Date efrom = (Date) upm1.from.getValue();
			Date eto = (Date) upm1.to.getValue();
			long etfrom = efrom.getTime();
			long etto = eto.getTime();

			if (Time.isMinimumTime(etto))
				etto = Long.MAX_VALUE;

			int counter2 = 0;

			for (DBStrukt s2 : values) {
				counter2++;

				DBUserPerMonth upm2 = (DBUserPerMonth) s2;

				if (upm1.equals(upm2))
					continue;

				int uid2 = (Integer) upm2.user.getValue();

				// uns iteressieren nur Einträge des selben Users
				if (uid2 != uid1)
					continue;

				Date ofrom = (Date) upm2.from.getValue();
				Date oto = (Date) upm2.to.getValue();

				long otfrom = ofrom.getTime();
				long otto = oto.getTime();

				if (Time.isMinimumTime(otto))
					otto = Long.MAX_VALUE;

				boolean failed = false;

				if (etfrom < otfrom && etto > otfrom) // links überlappend
				{
					failed = true;
				} else if (etfrom > otfrom && etto < otfrom) // mittig
				{
					failed = true;
				} else if (etfrom > otfrom && etfrom < otto) // rechts
																// überlappend
				{
					failed = true;
				} else if (etfrom == otfrom) {
					failed = true;
				} else if (etto == otto) {
					failed = true;
				}

				if (failed == true) {
					JOptionPane.showMessageDialog(
							null,
							StringUtils.autoLineBreak(
                                                        String.format(MESSAGE_OVERLAP_ENTRY, counter1,counter2)), MlM("Fehler"),
							JOptionPane.OK_OPTION);
					return false;
				}
			}
		}

		return true;
	}

	public void reload() {
		reload(false);
	}

	public void reload(boolean autombox) {
		user_query.refresh();

		tm.prepareTable();

		feed_table(autombox);

		tm.autoResize();

	}

	private void feed_table() {
		feed_table(true);
	}

	private void feed_table(boolean autombox) {
		new AutoMBox("UserPerMonth", autombox) {

			@Override
			public void do_stuff() throws Exception {
				tm.clear();
				clearEdited();

				Transaction trans = getTransaction();
				values = trans.fetchTable(new DBUserPerMonth(new UserQuery(
						getTransaction())));

				for (DBStrukt entry : values) {
					tm.add(entry);
				}
			}
		};
	}

	//<editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		jLTitle = new javax.swing.JLabel();
		jScrollPane1 = new javax.swing.JScrollPane();
		jTContent = new javax.swing.JTable();
		jBSave = new javax.swing.JButton();
		jBNew = new javax.swing.JButton();
		jBDel = new javax.swing.JButton();
		jBClose = new javax.swing.JButton();
		jBHelp = new javax.swing.JButton();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

		jLTitle.setFont(new java.awt.Font("Dialog", 1, 18));
		jLTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		jLTitle.setText("Monatseinstellungen für die Benutzer");

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
														832, Short.MAX_VALUE)
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
																		386,
																		Short.MAX_VALUE)
																.addComponent(
																		jBClose))
												.addGroup(
														layout.createSequentialGroup()
																.addComponent(
																		jLTitle,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		788,
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
										376, Short.MAX_VALUE)
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

	private void jBSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSaveActionPerformed
	// TODO add your handling code here:

		if (check_entries() == false)
			return;

		new AutoMBox(getTitle()) {

			@Override
			public void do_stuff() throws Exception {

				for (Integer i : tm.getEditedRows()) {
					DBUserPerMonth entry = (DBUserPerMonth) values.get(i);
					insertOrUpdateValues(entry);
				}

				getTransaction().commit();
				feed_table();
			}

		};
	}//GEN-LAST:event_jBSaveActionPerformed

	private void insertOrUpdateValues(DBUserPerMonth entry)
			throws UnsupportedDBDataTypeException,
			WrongBindFileFormatException, SQLException,
			TableBindingNotRegisteredException, IOException {
		DBUserPerMonth e = new DBUserPerMonth();

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

	private void jBNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBNewActionPerformed
		newEntry();
	}//GEN-LAST:event_jBNewActionPerformed

	private void jBDelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBDelActionPerformed
	// TODO add your handling code here:
		if (!checkAnyAndSingleSelection(jTContent))
			return;

		final int i = tm.getSelectedRow();

		if (i < 0 || i >= values.size())
			return;

		new AutoMBox(getTitle()) {

			public void do_stuff() throws Exception {
				DBUserPerMonth entry = (DBUserPerMonth) values.get(i);

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

	private void jBHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBHelpActionPerformed

		callHelpWin();

	}//GEN-LAST:event_jBHelpActionPerformed

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton jBClose;
	private javax.swing.JButton jBDel;
	private javax.swing.JButton jBHelp;
	private javax.swing.JButton jBNew;
	private javax.swing.JButton jBSave;
	private javax.swing.JLabel jLTitle;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JTable jTContent;

	// End of variables declaration//GEN-END:variables

	private void newEntry(boolean autombox) {
		new AutoMBox("UserPerMonth", autombox) {

			@Override
			public void do_stuff() throws Exception {

				DBUserPerMonth upm = new DBUserPerMonth(new UserQuery(
						getTransaction()));

				upm.id.loadFromCopy(getNewSequenceValue(upm.getName()));
				upm.usage.loadFromString(root.getSetup().getConfig(
						AppConfigDefinitions.NormalWorkPercent));
				upm.hours_per_week.loadFromString(root.getSetup().getConfig(
						AppConfigDefinitions.HoursPerWeek));
				upm.hist.setAnHist(root.getUserName());
				upm.days_per_week.loadFromString(root.getSetup().getConfig(
						AppConfigDefinitions.DaysPerWeek));

				values.add(upm);
				tm.add(upm, true);
			}
		};
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

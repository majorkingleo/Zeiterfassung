/*
 * BookDay.java
 *
 * Created on 9. Februar 2009, 19:45
 */

package at.redeye.Zeiterfassung;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.joda.time.DateMidnight;

import at.redeye.FrameWork.base.AutoLogger;
import at.redeye.FrameWork.base.AutoMBox;
import at.redeye.FrameWork.base.BaseDialog;
import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.base.UniqueDialogHelper;
import at.redeye.FrameWork.base.bindtypes.DBDateTime;
import at.redeye.FrameWork.base.bindtypes.DBStrukt;
import at.redeye.FrameWork.base.prm.bindtypes.DBConfig;
import at.redeye.FrameWork.base.tablemanipulator.TableManipulator;
import at.redeye.FrameWork.base.tablemanipulator.validators.TimeHourMinuteValidator;
import at.redeye.FrameWork.base.transaction.Transaction;
import at.redeye.FrameWork.utilities.StringUtils;
import at.redeye.FrameWork.utilities.calendar.MonthNames;
import at.redeye.FrameWork.widgets.JoinTableCell;
import at.redeye.FrameWork.widgets.calendarday.DisplayDay;
import at.redeye.SqlDBInterface.SqlDBIO.impl.TableBindingNotRegisteredException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.UnsupportedDBDataTypeException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.WrongBindFileFormatException;
import at.redeye.Zeiterfassung.bindtypes.DBTimeEntries;

/**
 * 
 * @author martin
 */
public class BookDay extends BaseDialog {

	private static final long serialVersionUID = 1L;

	DateMidnight day;
	TableManipulator tm;
	DisplayDay display_day;
	List<DBStrukt> values = new ArrayList<DBStrukt>();
	MonthSumInfo sum_info;
	int min_num_of_chars = 4;
	double hours_per_day;

	/** Creates new form BookDay */
	public BookDay(final Root root, DateMidnight day, DisplayDay display_day,
			MonthSumInfo suminfo, double hours_per_day, TimeEntryCache cache) {
		super(root, "Tag Buchen: " + getTitle(day));

		initComponents();

		this.day = day;
		this.display_day = display_day;
		this.sum_info = suminfo;
		this.hours_per_day = hours_per_day;

		setTitle(day);

		DBTimeEntries te = getBindType();

		tm = new TableManipulator(root, jTContent, te);
                tm.setResortingAllowed(false);

		tm.hide(te.id);
		tm.hide(te.user);
		tm.hide(te.locked);
		tm.hide(te.hist.lo_user);
		tm.hide(te.hist.lo_zeit);

		boolean use_projects = StringUtils.isYes(root.getSetup().getConfig(
				AppConfigDefinitions.UseCustomersAndProjects));

		if (!use_projects) {
			tm.hide(te.customer);
			tm.hide(te.project);
			tm.hide(te.sub_project);

		}

		tm.setEditable(te.comment);
		tm.setEditable(te.to);
		tm.setEditable(te.from);
		tm.setEditable(te.jobtype);

		if (use_projects) {
			tm.setEditable(te.customer);
			tm.setEditable(te.project);
			tm.setEditable(te.sub_project);
		}

		tm.setValidator(te.to, new TimeHourMinuteValidator());
		tm.setValidator(te.from, new TimeHourMinuteValidator());

		Vector<Object> additional_values_to = new Vector<Object>();
		additional_values_to.addAll(this.getEndTimes());
		tm.setAdditionalAutocompleteData(te.to, additional_values_to);

		Vector<Object> additional_values_from = new Vector<Object>();
		additional_values_from.addAll(this.getStartTimes());
		tm.setAdditionalAutocompleteData(te.from, additional_values_from);

		tm.setAdditionalAutocompleteData(te.comment, cache
				.getAutoCompleteInfoFor(te.comment).get());

		tm.setValidator(te.project, new JoinTableCell(te.customer,
				getTransaction()));

		tm.setValidator(te.sub_project, new SubProjectValidator(this, tm,
				te.project));

		tm.prepareTable();

		feed_table(false);

		if (values.size() == 0)
			newEntry(false, false);

		tm.autoResize();

		new AutoLogger(getTitle()) {
			public void do_stuff() throws Exception {
				String comment_chars = root.getSetup().getConfig(
						AppConfigDefinitions.NumberOfMinimumCommentChars);
				Integer num = Integer.valueOf(comment_chars);
				min_num_of_chars = num;
			}
		};


            registerHelpWin(new Runnable() {

                public void run() {
                    invokeDialogUnique(new LocalHelpWin(root, "BookDay"));
                }
            });
	}

	protected static String getTitle(DateMidnight d) {
		return d.dayOfWeek().getAsText(Locale.GERMAN) + " "
				+ d.dayOfMonth().getAsText(Locale.GERMAN) + " "
				+ MonthNames.getFullMonthName(d.getMonthOfYear()) + " "
				+ Integer.toString(d.getYear());
	}

	public void setTitle(DateMidnight d) {
		jLTitle.setText(getTitle(d));
	}

	private DBTimeEntries getBindType() {
		return new DBTimeEntries(getTransaction(), true);
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */

	// <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		jPanel1 = new javax.swing.JPanel();
		jLTitle = new javax.swing.JLabel();
		jBHelp = new javax.swing.JButton();
		jPanel2 = new javax.swing.JPanel();
		jScrollPane1 = new javax.swing.JScrollPane();
		jTContent = new javax.swing.JTable();
		jPanel3 = new javax.swing.JPanel();
		jBSave = new javax.swing.JButton();
		jBClose = new javax.swing.JButton();
		jBNew = new javax.swing.JButton();
		jBDel = new javax.swing.JButton();

		setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

		jLTitle.setFont(new java.awt.Font("Dialog", 1, 18));
		jLTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		jLTitle.setText("Montag 22.Jänner 2009");

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
		jPanel1Layout
				.setHorizontalGroup(jPanel1Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								javax.swing.GroupLayout.Alignment.TRAILING,
								jPanel1Layout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(
												jLTitle,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												592, Short.MAX_VALUE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												jBHelp,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												32,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addContainerGap()));
		jPanel1Layout
				.setVerticalGroup(jPanel1Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								javax.swing.GroupLayout.Alignment.TRAILING,
								jPanel1Layout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												jPanel1Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.TRAILING)
														.addComponent(jBHelp)
														.addComponent(
																jLTitle,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																32,
																Short.MAX_VALUE))
										.addContainerGap()));

		jTContent.setModel(new javax.swing.table.DefaultTableModel(
				new Object[][] { { null, null, null, null },
						{ null, null, null, null }, { null, null, null, null },
						{ null, null, null, null } }, new String[] { "Title 1",
						"Title 2", "Title 3", "Title 4" }));
		jScrollPane1.setViewportView(jTContent);

		javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(
				jPanel2);
		jPanel2.setLayout(jPanel2Layout);
		jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				jPanel2Layout
						.createSequentialGroup()
						.addContainerGap()
						.addComponent(jScrollPane1,
								javax.swing.GroupLayout.DEFAULT_SIZE, 636,
								Short.MAX_VALUE).addContainerGap()));
		jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addComponent(
				jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 342,
				Short.MAX_VALUE));

		jBSave.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/at/redeye/FrameWork/base/resources/icons/button_ok.gif"))); // NOI18N
		jBSave.setText("Speichern");
		jBSave.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jBSaveActionPerformed(evt);
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

		javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(
				jPanel3);
		jPanel3.setLayout(jPanel3Layout);
		jPanel3Layout
				.setHorizontalGroup(jPanel3Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel3Layout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(jBSave)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(jBNew)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(jBDel)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED,
												190, Short.MAX_VALUE)
										.addComponent(jBClose)
										.addContainerGap()));
		jPanel3Layout
				.setVerticalGroup(jPanel3Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel3Layout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												jPanel3Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																jPanel3Layout
																		.createParallelGroup(
																				javax.swing.GroupLayout.Alignment.BASELINE)
																		.addComponent(
																				jBSave)
																		.addComponent(
																				jBNew)
																		.addComponent(
																				jBDel))
														.addComponent(jBClose))
										.addContainerGap(
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE,
						javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE,
						javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(jPanel3,
						javax.swing.GroupLayout.Alignment.TRAILING,
						javax.swing.GroupLayout.DEFAULT_SIZE,
						javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		layout.setVerticalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addComponent(jPanel1,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										43,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(jPanel2,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(jPanel3,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.PREFERRED_SIZE)));

		pack();
	}// </editor-fold>//GEN-END:initComponents

	private boolean check_entries() {
		int counter1 = 0;

		for (DBStrukt s : values) {
			counter1++;

			DBTimeEntries entry = (DBTimeEntries) s;

			Date efrom = (Date) entry.from.getValue();
			Date eto = (Date) entry.to.getValue();

			long etfrom = efrom.getTime();
			long etto = eto.getTime();

			int counter2 = 0;

			for (DBStrukt ss : values) {
				counter2++;

				DBTimeEntries other = (DBTimeEntries) ss;

				if (other.equals(entry))
					continue;

				Date ofrom = other.from.getValue();
				Date oto = other.to.getValue();

				long otfrom = ofrom.getTime();
				long otto = oto.getTime();

				boolean failed = false;

				if (etfrom < otfrom && etto > otfrom) // links überlappend
					failed = true;
				else if (etfrom > otfrom && etto < otfrom) // mittig
					failed = true;
				else if (etfrom > otfrom && etfrom < otto) // rechts überlappend
					failed = true;
				else if (etfrom == otfrom)
					failed = true;
				else if (etto == otto)
					failed = true;

				if (failed == true) {
					JOptionPane.showMessageDialog(
							null,
							StringUtils.autoLineBreak("Eintrag " + counter1
									+ " und Eintrag " + counter2
									+ " überschneiden sich!"), "Fehler",
							JOptionPane.OK_OPTION);
					return false;
				}
			}

			if (min_num_of_chars != 0) {
				if (entry.comment.getValue().isEmpty()
						|| entry.comment.getValue().length() < min_num_of_chars) {
					String minimum_msg = "";

					if (min_num_of_chars > 0) {
						if (min_num_of_chars == 1) {
							minimum_msg = " Es muß mindestens ein Zeichen eingeben werden.";
						} else {
							minimum_msg = " Es müssen mindestens "
									+ min_num_of_chars
									+ " Zeichen eingeben werden.";
						}
					}

					JOptionPane.showMessageDialog(null, StringUtils
							.autoLineBreak("Bei Eintrag "
									+ counter1
									+ " fehlt der Kommentar, "
									+ "oder der eingegebene Text '"
									+ (entry.comment.getValue().toString()
											.isEmpty() ? "   " : entry.comment
											.getValue()) + "' ist zu kurz."
									+ minimum_msg), "Fehler",
							JOptionPane.OK_OPTION);
					return false;
				}
			}

			if (etfrom >= etto) {
				JOptionPane.showMessageDialog(
						null,
						StringUtils.autoLineBreak("Eintrag " + counter1
								+ " enthält keine gültigen Zeitstempel."),
						"Fehler", JOptionPane.OK_OPTION);
				return false;
			}
		}
		return true;
	}

	private void jBSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSaveActionPerformed

		if (check_entries() == false)
			return;

		new AutoMBox(getTitle()) {

			@Override
			public void do_stuff() throws Exception {
				for (Integer i : tm.getEditedRows()) {
					DBTimeEntries entry = (DBTimeEntries) values.get(i);
					logger.trace("Edited from: " + entry.from + " to: "
							+ entry.to + " Comment: " + entry.comment);

					insertOrUpdateValues(entry);
				}

				getTransaction().commit();
				feed_table();
				display_day.update();

				if (sum_info != null)
					sum_info.updateMonthSumInfo();
			}
		};

	}//GEN-LAST:event_jBSaveActionPerformed

	private void insertOrUpdateValues(DBTimeEntries entry)
			throws UnsupportedDBDataTypeException,
			WrongBindFileFormatException, SQLException,
			TableBindingNotRegisteredException, IOException {
		DBTimeEntries e = new DBTimeEntries();

		e.loadFromCopy(entry);

		if (getTransaction().fetchTableWithPrimkey(e) == false) {
			getTransaction().insertValues(e);
		} else {
			entry.hist.setAeHist(root.getUserName());
			getTransaction().updateValues(entry);
		}
	}

	protected void newEntry(boolean autombox, final boolean setEdited) {
		new AutoMBox(getTitle(), autombox) {

			@Override
			public void do_stuff() throws Exception {

				DBTimeEntries te = getBindType();

				te.id.loadFromCopy(new Integer(
						getNewSequenceValue(te.getName())));
				te.user.loadFromCopy(new Integer(root.getUserId()));

				// Wenn geht gleich die Werte plausiebel vorbelegen.
				if (values.size() > 0) {
					DBTimeEntries last_entry = (DBTimeEntries) values
							.get(values.size() - 1);

					te.from.loadFromCopy(last_entry.to.getValue());
				} else {
					/* Parameter Wert benutzen */
					String start_time = root.getSetup().getConfig(
							AppConfigDefinitions.NormalWorkTimeStart);

					te.from.loadFromCopy(new Date(day.getMillis()));

					if (!te.from.loadTimePart(start_time + ":00")) {
						logger.debug("Invalid time format string '"
								+ start_time
								+ "' in parameter "
								+ AppConfigDefinitions.NormalWorkTimeStart
										.getConfigName());
					}
				}

				DateMidnight today = new DateMidnight();

				if (day.isEqual(today)) {
					te.to.loadFromCopy(new Date(System.currentTimeMillis()));
				} else {
					String stop_time = root.getSetup().getConfig(
							AppConfigDefinitions.NormalWorkTimeStop);

					te.to.loadFromCopy(new Date(day.getMillis()));

					if (!te.to.loadTimePart(stop_time + ":00")) {
						logger.debug("Invalid time format string '"
								+ stop_time
								+ "' in parameter "
								+ AppConfigDefinitions.NormalWorkTimeStop
										.getConfigName());
					}
				}

				te.hist.setAnHist(root.getUserName());
				tm.add(te, setEdited);

				values.add(te);
			}
		};
	}

	protected void newEntry() {
		newEntry(true, true);
	}

	private void jBNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBNewActionPerformed

		newEntry();

	}//GEN-LAST:event_jBNewActionPerformed

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

	private void jBDelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBDelActionPerformed

		if (!checkAnyAndSingleSelection(jTContent))
			return;

		final int i = tm.getSelectedRow();

		if (i < 0 || i >= values.size())
			return;

		new AutoMBox(getTitle()) {

			public void do_stuff() throws Exception {
				System.out.println("i: " + i);

				DBTimeEntries entry = (DBTimeEntries) values.get(i);

				getTransaction().updateValues(
						"delete from " + entry.getName() + " where "
								+ getTransaction().markColumn("id") + " = "
								+ ((Integer) entry.id.getValue()).toString());

				setEdited();
				values.remove(i);
				tm.remove(i);
			}
		};

	}//GEN-LAST:event_jBDelActionPerformed

	private void jBHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBHelpActionPerformed

		callHelpWin();

	}//GEN-LAST:event_jBHelpActionPerformed

	private void feed_table(boolean autombox) {
		new AutoMBox(getTitle(), autombox) {

			@Override
			public void do_stuff() throws Exception {

				clearEdited();
				tm.clear();
				Transaction trans = getTransaction();
				values = trans.fetchTable(getBindType(), "where "
						+ getTransaction().getDayStmt("from", day) + " and "
						+ trans.markColumn("user") + " = " + root.getUserId()
						+ " order by " + trans.markColumn("from"));

				for (DBStrukt entry : values) {
					tm.add(entry);
				}
			}
		};
	}

	private void feed_table() {
		feed_table(true);
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

	protected void loadFromParam(DBDateTime value, DBConfig param) {
		String time = root.getSetup().getConfig(param);

		value.loadFromCopy(new Date(day.getMillis()));

		if (!value.loadTimePart(time + ":00")) {
			logger.debug("Invalid time format string '" + time
					+ "' in parameter " + param.getConfigName());
		}
	}

	protected Vector<Object> getEndTimes() {
		Vector<Object> times = new Vector<Object>();
		DBTimeEntries te = new DBTimeEntries();

		DBDateTime dt = te.to.getCopy();

		// Aufgrund der Normalarbeitszeit wird das "normale" Ende vorgeschlagen
		loadFromParam(dt, AppConfigDefinitions.NormalWorkTimeStart);

		long normal_endtime = (long) (dt.getValue().getTime() + hours_per_day * 60 * 60 * 1000);
		dt.loadFromCopy(new Date(normal_endtime));

		System.out.println("normales Ende: " + new Date(normal_endtime));

		times.add(dt.getCopy());

		loadFromParam(dt, AppConfigDefinitions.NormalWorkTimeStop);

		times.add(dt.getCopy());

		getNow(dt);

		times.add(dt.getCopy());

		return times;
	}

	protected void getNow(DBDateTime dt) {
		Date now = new Date(System.currentTimeMillis());
		dt.loadTimePart(DBDateTime.getTimeStr(now));
	}

	protected Vector<Object> getStartTimes() {
		Vector<Object> times = new Vector<Object>();
		DBTimeEntries te = new DBTimeEntries();

		DBDateTime dt = te.from.getCopy();

		loadFromParam(dt, AppConfigDefinitions.NormalWorkTimeStart);
		times.add(dt.getCopy());

		getNow(dt);
		times.add(dt.getCopy());

		return times;
	}

	@Override
	public String getUniqueDialogIdentifier(Object requester) {

		if (requester instanceof String) {
			String reason = (String) requester;

			// der is dafür zuständig, dass ein dialog nur einmal geöffnet
			// werden kann,
			// aber in unserem Fall er den Dialog mehrfach öffnen, aber dor
			// größen sollen
			// alle gleich sein.
			if (reason.equals(UniqueDialogHelper.ID_STRING))
				return super.getUniqueDialogIdentifier(requester);
		}

		return this.getClass().getName();
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton jBClose;
	private javax.swing.JButton jBDel;
	private javax.swing.JButton jBHelp;
	private javax.swing.JButton jBNew;
	private javax.swing.JButton jBSave;
	private javax.swing.JLabel jLTitle;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JPanel jPanel3;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JTable jTContent;
	// End of variables declaration//GEN-END:variables

}

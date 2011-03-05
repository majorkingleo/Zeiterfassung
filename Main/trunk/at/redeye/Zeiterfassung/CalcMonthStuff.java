/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Zeiterfassung;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.joda.time.DateMidnight;

import at.redeye.FrameWork.base.bindtypes.DBDateTime;
import at.redeye.FrameWork.base.bindtypes.DBStrukt;
import at.redeye.FrameWork.base.transaction.Transaction;
import at.redeye.FrameWork.utilities.HMSTime;
import at.redeye.FrameWork.utilities.Rounding;
import at.redeye.FrameWork.utilities.Time;
import at.redeye.SqlDBInterface.SqlDBIO.impl.TableBindingNotRegisteredException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.UnsupportedDBDataTypeException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.WrongBindFileFormatException;
import at.redeye.Zeiterfassung.bindtypes.DBJobType;
import at.redeye.Zeiterfassung.bindtypes.DBJobType.JOBTYPES;
import at.redeye.Zeiterfassung.bindtypes.DBOvertimeRule.SCHEMAS;
import at.redeye.Zeiterfassung.bindtypes.DBTimeEntries;
import at.redeye.Zeiterfassung.bindtypes.DBUserPerMonth;
import at.redeye.Zeiterfassung.overtime.FlatRate;
import at.redeye.Zeiterfassung.overtime.None;
import at.redeye.Zeiterfassung.overtime.OvertimeInterface;
import at.redeye.Zeiterfassung.overtime.Flatrate_Short_Friday_01;
import at.redeye.Zeiterfassung.overtime.Schema_1;
import org.joda.time.LocalDate;

/**
 * 
 * @author martin
 */
public class CalcMonthStuff {
        private static final Logger logger = Logger.getLogger(CalcMonthStuff.class);
	
	private double hours_per_month = 0;
	private double hours_per_month_done = 0;
	private Transaction trans;
	private CalcMonthStuffDataInterface display_month;
	private HMSTime complete_time = new HMSTime();
	private HMSTime remaining_leave = new HMSTime();
	private HMSTime flextime = new HMSTime();
        private HMSTime overtime = new HMSTime();
	private StringBuilder error_log = new StringBuilder();
	private HMSTime time_correction_month_done = new HMSTime();
        private int user_id;
        private int days_of_month=0;
        private OvertimeInterface calc_overtime;
        private double remaining_leave_in_days=0;
        private int holidays_for_month_in_days = 0;

	public CalcMonthStuff(CalcMonthStuffDataInterface month, Transaction trans, int user_id) {
		this.display_month = month;
		this.trans = trans;		
                this.user_id = user_id;
	}

        public double getHoursPerDay(DateMidnight date)
        {
            return calc_overtime.getHours4Day(date, display_month.getHolidays());
        }

	public DBUserPerMonth getUPMRecord(int year, int month)
			throws TableBindingNotRegisteredException,
			UnsupportedDBDataTypeException, SQLException,
			WrongBindFileFormatException, CloneNotSupportedException,
			DuplicateRecordException {
		DBUserPerMonth upm;

		upm = GetUserPerMonthRecord.getValidRecordForMonth(trans,
				user_id, year, month);

		if (upm == null) {
			logger.error("Der Monatseintrag des Benutzers konnte nicht berechnet werden!");
			error("Der Monatseintrag des Benutzers konnte nicht berechnet werden!\n"
					+ "Überprüfen sie die Daten im Menü \"Einstellungen\" => \"Monatseinstellungen für die Benutzer\".");
			return null;
		}

		return upm;
	}

	private void calcHoursPerMonth() throws TableBindingNotRegisteredException, UnsupportedDBDataTypeException, SQLException, WrongBindFileFormatException, CloneNotSupportedException, DuplicateRecordException {
		hours_per_month = 0;

                DateMidnight dm = new DateMidnight(display_month.getYear(), display_month.getMonth(),1);
                final DateMidnight month_end = dm.plusMonths(1);

                DBUserPerMonth upm = getUPMRecord(display_month.getYear(), display_month.getMonth() );

		if (upm == null) {
			return;
		}

                calc_overtime = getOverTimeforUPM(upm);

                for( ; dm.isBefore(month_end); dm = dm.plusDays(1) )
                {                   
                    hours_per_month += calc_overtime.getHours4Day(dm, display_month.getHolidays());
                }
	}
        
	private boolean isHoliday(Date day) {
		return isHoliday(new DateMidnight(day));
	}
        
	private boolean isHoliday(DateMidnight day) {
                return display_month.isHoliday(day);
	}

	public int getWorkDaysForMonth(Date date) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		cal.set(Calendar.DAY_OF_MONTH, 1);

		int local_days_of_month = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

		int days_to_work = 0;

		for (int i = 1; i <= local_days_of_month; i++) {
			cal.set(Calendar.DAY_OF_MONTH, i);

			if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
				continue;

			if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
				continue;

			if (isHoliday(new DateMidnight(cal)))
				continue;

			days_to_work++;
		}

		return days_to_work;
	}

	public void calcHoursPerMonthDone() throws SQLException,
			TableBindingNotRegisteredException, UnsupportedDBDataTypeException,
			WrongBindFileFormatException, CloneNotSupportedException,
			DuplicateRecordException {
		hours_per_month_done = 0;
		time_correction_month_done.setTime(0);

		int m = display_month.getMonth();
		int y = display_month.getYear();

		DBUserPerMonth upm = getUPMRecord(y, m);

		if (upm == null) {
			return;
		}

		DateMidnight dmStart = new DateMidnight(y, m, 1);

		// One month later minus 1 second
		// (e.g. 01.01.2010 00:00:00 till 31.01.2010 23:59:59)
		DateMidnight dmEnd = dmStart.plusMonths(1).minusDays(1);

		String where = "where " + trans.markColumn("user") + " = "
				+ user_id + " and "
				+ trans.getPeriodStmt("from", dmStart, dmEnd) + " order by "
				+ trans.markColumn("from");

		List<DBStrukt> res = trans.fetchTable(new DBTimeEntries(), where);

		long millis = 0;

		List<DBTimeEntries> last_day = new ArrayList<DBTimeEntries>();

		Calendar cal_before = new GregorianCalendar();
		Calendar cal_act = new GregorianCalendar();		

		for (DBStrukt s : res) {
			DBTimeEntries te = (DBTimeEntries) s;

			millis += te.calcDuration();

			if (last_day.isEmpty()) {
				cal_before.setTime(te.from.getValue());
				last_day.add(te);
			} else {
				cal_act.setTime(te.from.getValue());
				if (cal_before.get(Calendar.DAY_OF_MONTH) != cal_act
						.get(Calendar.DAY_OF_MONTH)) {
					cal_before.setTime(te.from.getValue());
					long ot = calc_overtime.calcExtraTimeForDay(last_day,isHoliday(te.from.getValue()));
					last_day.clear();
					last_day.add(te);

					if (ot != 0) {
						logger.info(DBDateTime.getDateStr(te.from.getValue())
								+ " added " + ot + " to overtime.");
					}

					time_correction_month_done.addMillis(ot);
				} else {
					last_day.add(te);
				}
			}
		}

		if (last_day.size() > 0) {
			long ot = calc_overtime.calcExtraTimeForDay(last_day, isHoliday(last_day.get(0).from.getValue()) );

			if (ot > 0) {
				logger.info(DBDateTime.getDateStr(last_day.get(0).from
						.getValue()) + " added " + ot + " to overtime.");
			}

			time_correction_month_done.addMillis(ot);
		}

		hours_per_month_done = millis;

		complete_time = new HMSTime(millis);
	}

	public void calc() throws TableBindingNotRegisteredException,
			UnsupportedDBDataTypeException, SQLException,
			WrongBindFileFormatException, CloneNotSupportedException,
			DuplicateRecordException {
            error_log.setLength(0);

            days_of_month = new GregorianCalendar(display_month.getYear(), display_month.getMonth() - 1, 1).getActualMaximum(Calendar.DAY_OF_MONTH);


            calcHoursPerMonth();
            calcHoursPerMonthDone();

            if (!calcRemainingLeave()) {
                error("Der Resturlaub konnte nicht berechnet werden.");
            }

            if (!calcFlexTime()) {
                error("Der Gesammtarbeitszeit konnte nicht berechnet werden.");
            }

	}

	private boolean matchUserEntryForDate(DBUserPerMonth upm, Date date) {
		Date from = (Date) upm.from.getValue();
		Date to = (Date) upm.to.getValue();

		long lfrom = from.getTime();
		long lto = to.getTime();

		if (Time.isMinimumTime(lto)) {
			lto = 0;
		}

		if (Time.isMinimumTime(lfrom)) {
			lfrom = 0;
		}

		long current = date.getTime();

		if (lfrom < current && (lto >= current || lto == 0)) {
			return true;
		}

		return false;
	}

	private String getJobTyesForHoliday() throws SQLException,
			TableBindingNotRegisteredException, UnsupportedDBDataTypeException,
			WrongBindFileFormatException {
		DBJobType types = new DBJobType();

		List<DBStrukt> res = trans.fetchTable(types,
				"where " + trans.markColumn(types.is_holliday) + "='JA'");

		StringBuilder line = new StringBuilder();

		for (int i = 0; i < res.size(); i++) {
			DBJobType type = (DBJobType) res.get(i);

			if (i > 0)
				line.append(",");

			line.append(type.id.toString());

		}

		return line.toString();
	}

	private String getJobTyesForWork() throws SQLException,
			TableBindingNotRegisteredException, UnsupportedDBDataTypeException,
			WrongBindFileFormatException {
		DBJobType types = new DBJobType();

		List<DBStrukt> res = trans.fetchTable(types,
				"where " + trans.markColumn(types.is_holliday) + "='JA'"
						+ " or " + trans.markColumn(types.type) + "='"
						+ JOBTYPES.LZ.toString() + "'");

		StringBuilder line = new StringBuilder();

		for (int i = 0; i < res.size(); i++) {
			DBJobType type = (DBJobType) res.get(i);

			if (i > 0)
				line.append(",");

			line.append(type.id.toString());

		}

		return line.toString();
	}

	/*
	 * berechnet den Resturlaub
	 */
	public boolean calcRemainingLeave() throws SQLException,
			TableBindingNotRegisteredException, UnsupportedDBDataTypeException,
			WrongBindFileFormatException, DuplicateRecordException {
		GregorianCalendar gdate = new GregorianCalendar(display_month.getYear(),
				display_month.getMonth() - 1, 1);

		Date date = gdate.getTime();

		DBUserPerMonth upm = GetUserPerMonthRecord.getValidRecordForMonth(
				trans, user_id, display_month.getYear(), display_month.getMonth());

		if (upm == null) {
			String msg = "Keinen passenden Monatseintrag gefunden. Die Urlaubsstunden können nicht berechnet werden.";
			logger.error(msg);
			error(msg);
			return false;
		}

		DBTimeEntries entries = new DBTimeEntries();

		Date from = upm.from.getValue();
		Date to = upm.to.getValue();

		logger.info(to.getTime());

		if (Time.isMinimumTime(to.getTime())) {
			// der letzte tag des aktuellen Monats
			GregorianCalendar gdate2 = new GregorianCalendar(display_month.getYear(),
					display_month.getMonth() - 1, days_of_month);
			to = gdate2.getTime();
		}

		String JobTypeString = getJobTyesForHoliday();

		if (JobTypeString.isEmpty()) {
			String msg = "Keine Urlaubstätigkeiten gefunden. Resturlaubsberechnung nicht möglich.\n"
					+ "Bitte kontrollieren Sie die konfigurierten Tätigkeiten im Menü \"Einstellungen\" => \"Tätigkeiten\"";
			logger.info(msg);
			error(msg);
			return false;
		}

		String job_type_sql = " and " + trans.markColumn(entries.jobtype)
				+ " in (" + JobTypeString + ") ";

		List<DBStrukt> res = trans.fetchTable(
				entries,
				"where "
						+ trans.getPeriodStmt("from", new DateMidnight(from),
								new DateMidnight(to)) + " and "
						+ trans.markColumn(entries.user) + " = "
						+ user_id + job_type_sql + " order by "
						+ trans.markColumn(entries.from));

		if (res.size() <= 0) {
			logger.warn("Noch keine Einträge gefunden mit: " + trans.getSql());
		}

		Double durlaub = upm.hours_holidays.getValue();
		long urlaub = durlaub.longValue() * 60 * 60 * 1000;

                remaining_leave_in_days = upm.days_holidays.getValue();

                LocalDate last_day = null;

                for (int i = 0; i < res.size(); i++) {
                   DBTimeEntries entry = (DBTimeEntries) res.get(i);
                    urlaub -= entry.calcDuration();

                    // Wenn sich das Datum ändert, dann wird ein Urlaubstag abgezogen
                    if (last_day == null) {
                        last_day = new LocalDate(entry.from.getValue());
                        remaining_leave_in_days -= 1;                        
                    } else {
                        LocalDate other_entry = new LocalDate(entry.from.getValue());

                        if (!other_entry.isEqual(last_day)) {
                            remaining_leave_in_days -= 1;
                            last_day = other_entry;                            
                        }
                    }
                }

		remaining_leave = new HMSTime(urlaub);

		return true;
	}

	private boolean calcFlexTime() throws SQLException,
			TableBindingNotRegisteredException, UnsupportedDBDataTypeException,
			WrongBindFileFormatException, DuplicateRecordException {
		GregorianCalendar gdate = new GregorianCalendar(display_month.getYear(),
				display_month.getMonth() - 1, 1);

		Date date = gdate.getTime();

		DBUserPerMonth upm = GetUserPerMonthRecord.getValidRecordForMonth(
				trans, user_id, display_month.getYear(), display_month.getMonth());

		if (upm == null) {
			String msg = "Keinen passenden Monatseintrag gefunden. Die Überstunden können nicht berechnet werden.\n"
					+ "Bitte kontrollieren Sie die \"Monatseinstellungen für die Benutzer\"";
			logger.error(msg);
			error(msg);
			return false;
		}

		DBTimeEntries entries = new DBTimeEntries();

		Date from = upm.from.getValue();
		Date to = upm.to.getValue();

		logger.info(to.getTime());

		Double dflextime = upm.hours_overtime.getValue();
		long lflextime = dflextime.longValue() * 60 * 60 * 1000;
                long lovertime = dflextime.longValue() * 60 * 60 * 1000;

                flextime.setTime(lflextime);
                overtime.setTime(lovertime);

                HMSTime debug_overtime = new HMSTime(lovertime);

		if (Time.isMinimumTime(to.getTime())) {
			// der letzte Tag des aktuellen Monats
			GregorianCalendar gdate2 = new GregorianCalendar(display_month.getYear(),
					display_month.getMonth() - 1, days_of_month);
			to = gdate2.getTime();
			logger.trace("Setting to to:" + DBDateTime.getDateStr(to));
		}

		List<DBStrukt> res = trans.fetchTable(
				entries,
				"where "
						+ trans.getPeriodStmt("from", new DateMidnight(from),
								new DateMidnight(to)) + " and "
						+ trans.markColumn(entries.user) + " = "
						+ user_id + " and "
						+ trans.markColumn(entries.jobtype) + " in ("
						+ getJobTyesForWork() + ")");

		if (res.size() <= 0) {
			logger.warn("Noch keine Einträge gefunden mit: " + trans.getSql());
			return true;
		}

		logger.trace(trans.getSql());

		List<DBTimeEntries> last_day = new ArrayList<DBTimeEntries>();

		Calendar cal_before = new GregorianCalendar();
		Calendar cal_act = new GregorianCalendar();		

		for (int i = 0; i < res.size(); i++) {
			DBTimeEntries entry = (DBTimeEntries) res.get(i);
			lflextime += entry.calcDuration();

			if (last_day.isEmpty()) {
				cal_before.setTime(entry.from.getValue());
				last_day.add(entry);
			} else {
				cal_act.setTime(entry.from.getValue());
				if (cal_before.get(Calendar.DAY_OF_MONTH) != cal_act
						.get(Calendar.DAY_OF_MONTH)) {
					cal_before.setTime(entry.from.getValue());

					long ft_extra = calc_overtime.calcExtraTimeForDay(last_day,isHoliday(entry.from.getValue()));
                                        long ot = calc_overtime.calcOverTimeForDay(last_day,isHoliday(entry.from.getValue()));

                                        debug_overtime.addMillis(ot);
                                        debug_overtime.addMillis(ft_extra);

					last_day.clear();
					last_day.add(entry);

					if (ft_extra > 0) {
						logger.trace(DBDateTime.getDateStr(entry.from
								.getValue()) + " added " + ft_extra + " to overtime.");
					}

					lflextime += ft_extra;
                                        lovertime += ot + ft_extra;
				} else {
					last_day.add(entry);
				}
			}

                    if (logger.isTraceEnabled()) {
                        String msg = String.format("%s - %s => %s (%s)", DBDateTime.getDateStr(entry.from.getValue()), DBDateTime.getDateStr(entry.to.getValue()),
                                new HMSTime(entry.calcDuration()).toString("HH:mm"),
                                entry.comment.getValue());

                        logger.trace(msg);
                    }
		}

		if (last_day.size() > 0) {
                    long ft_extra = calc_overtime.calcExtraTimeForDay(last_day, isHoliday(last_day.get(0).from.getValue()));
                    long ot = calc_overtime.calcOverTimeForDay(last_day, isHoliday(last_day.get(0).from.getValue()));

                     debug_overtime.addMillis(ot);
                     debug_overtime.addMillis(ft_extra);

                    if (ft_extra > 0) {
                        logger.trace(DBDateTime.getDateStr(last_day.get(0).from.getValue()) + " added " + ft_extra + " to flex time. Overtime " + ot );
                    }

                    lflextime += ft_extra;
                    lovertime += ot + ft_extra;
		}

		// so jetzt haben wir auf der Haben Seite alle Arbeitszeiten
		// zusammenaddiert.
		// nun muß die Sollartbeitszeit abgezogen werden.

		DateMidnight cal_from = new DateMidnight(from);
		DateMidnight cal_to = new DateMidnight(to);

		double regular_work_time = 0;

		logger.info("from: " + DBDateTime.getDateStr(cal_from) + " to "
				+ DBDateTime.getDateStr(cal_to));

               if( upm == null || upm.hours_per_week.getValue() <= 0.0 )
                {
                    error("Der Arbeitsstunden konnten nicht berechnet werden da keine Wochenstundenanzahl eingetragen ist.");
                } else {

                int distance = (cal_to.getYear() - cal_from.getYear()) * 12 + (cal_to.getMonthOfYear() - cal_from.getMonthOfYear());

                // int start = cal_from.getMonthOfYear();
                // for (int i = start; i <= cal_to.getMonthOfYear(); i++) {
                for (int i = 0; i <= distance; i++) {
                    //Date aktual_day = cal_from.plusMonths(start - i).toDate();
                    Date aktual_day = cal_from.plusMonths(i).toDate();
                    long work_days_for_month = getWorkDaysForMonth(aktual_day);

                    double work_time_for_month = hours_per_month;

                    logger.info(String.format("%d %s Working Days: %d regular working hours per month: %f",
                            i, DBDateTime.getDateStr(aktual_day),
                            work_days_for_month, work_time_for_month));


                    regular_work_time += work_time_for_month;
                }
            }
		long correction = 0;

		if (calc_overtime != null)
			correction = calc_overtime.calcExtraTimeForMonth(
					(long) (regular_work_time * 60 * 60 * 1000), lflextime);

		double flextime_result = (lflextime - correction) / 60 / 60 / 1000.0
				- (regular_work_time);

		logger.info(String
				.format("overal working hours from %s to %s: %.3f regular working hours: %.3f result: %.3f",
						DBDateTime.getDateStr(from), DBDateTime.getDateStr(to),
						Rounding.rndDouble(lflextime / 60 / 60 / 1000.0, 3),
						Rounding.rndDouble(regular_work_time, 3),
						Rounding.rndDouble(flextime_result, 3)));

		flextime.setTime((long) (flextime_result * 60 * 60 * 1000));
                overtime.setTime(lovertime);

		return true;
	}

	public String getError() {
		return error_log.toString();
	}

	public boolean hasError() {
		return error_log.length() > 0;
	}

	private void error(String text) {
		if (error_log.length() > 0)
			error_log.append("\n");
		error_log.append(text);
	}

    public static OvertimeInterface getOverTimeforUPM(DBUserPerMonth upm) {
        SCHEMAS schema = SCHEMAS.valueOf(upm.overtime_rule.getValue());

        switch (schema) {
            case KEINES:
                return new None(upm);
            case PAUSCHALIST:
                return new FlatRate(upm);
            case ÜBERSTUNDENSCHEMA_01:
                return new Schema_1(upm);
            case FLATRATE_SFR_01:
                return new Flatrate_Short_Friday_01(upm);
        }

        return null;
    }

        /**
         * @return die Soll-Arbeitsstunden im Monat
         */
	public String getFormatedHoursPerMonth() {
		HMSTime hms_time = new HMSTime();
		hms_time.setTime(getHoursPerMonthInMillis());
		return hms_time.toString("HH:mm");
	}

	public long getHoursPerMonthInMillis() {
		return (long) (hours_per_month * 1000 * 60 * 60);
	}

        public HMSTime getCompleteTime()
        {
            return complete_time;
        }

        public HMSTime getFlexTime()
        {
            return flextime;
        }

        public HMSTime getRemainingLeave()
        {
            return remaining_leave;
        }
        
        public HMSTime getExtraTimePerMonthDone()
        {
            return time_correction_month_done;
        }

        public HMSTime getOverTime()
        {
            return overtime;
        }

        public OvertimeInterface getOverTimeInterface()
        {
            return calc_overtime;
        }

        /**
         * Hier wird der Urlaub in ganzen Tagen gerechnet.
         * Damit ist nicht nur eine Umrechnung in Tage gemeint, sondern
         * wenn eine Urlaubseintrag an einem Tag vorhanden war, wird ein ganzer
         * Urlaubstag vom Resturlaub abgezogen.
         * @return der berechnete Resturlaub in ganzen Tagen
         */
        public double getRemainingLeaveInDays()
        {
            return remaining_leave_in_days;
        }

        /**
         * Hier wird der Urlaub in ganzen Tagen gerechnet.
         * Damit ist nicht nur eine Umrechnung in Tage gemeint, sondern
         * wenn eine Urlaubseintrag an einem Tag vorhanden war, wird ein ganzer
         * Urlaubstag vom Resturlaub abgezogen.
         * @return der berechnete Resturlaub in ganzen Tagen
         */
        public double getHolidaysForMonthInDays()
        {
            return holidays_for_month_in_days;
        }
}

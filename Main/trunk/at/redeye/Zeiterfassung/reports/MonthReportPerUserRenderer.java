/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Zeiterfassung.reports;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;

import at.redeye.FrameWork.base.AutoLogger;
import at.redeye.FrameWork.base.bindtypes.DBFlagJaNein.FLAGTYPES;
import at.redeye.FrameWork.base.bindtypes.DBStrukt;
import at.redeye.FrameWork.base.reports.BaseReportRenderer;
import at.redeye.FrameWork.base.reports.ReportRenderer;
import at.redeye.FrameWork.base.transaction.Transaction;
import at.redeye.FrameWork.utilities.HMSTime;
import at.redeye.FrameWork.utilities.Rounding;
import at.redeye.FrameWork.utilities.calendar.Holidays;
import at.redeye.FrameWork.utilities.calendar.Holidays.HolidayInfo;
import at.redeye.Zeiterfassung.CalcMonthStuff;
import at.redeye.Zeiterfassung.CalcMonthStuffDataInterface;
import at.redeye.Zeiterfassung.bindtypes.DBJobType;
import at.redeye.Zeiterfassung.bindtypes.DBJobType.JOBTYPES;
import at.redeye.Zeiterfassung.bindtypes.DBTimeEntries;
import at.redeye.Zeiterfassung.bindtypes.DBUserPerMonth;
import at.redeye.Zeiterfassung.overtime.OvertimeInterface;
import java.util.GregorianCalendar;

/**
 * 
 * @author martin
 */
public class MonthReportPerUserRenderer extends BaseReportRenderer implements ReportRenderer, CalcMonthStuffDataInterface {
	Transaction trans;
	List<DBStrukt> data = new ArrayList<DBStrukt>();
	HashMap<Integer, DBJobType> job_types = new HashMap<Integer, DBJobType>();
	int mon;
	int year;
	String username;
	int user_id;
        Holidays holidays;
        CalcMonthStuff calc_month_stuff;
        DBUserPerMonth upm;
        OvertimeInterface over_time;

	public MonthReportPerUserRenderer(Transaction trans, int mon, int year,
			String username, int user_id, Holidays holidays) {
		this.trans = trans;
		this.mon = mon;
		this.year = year;
		this.user_id = user_id;
		this.username = username;
                this.holidays = holidays;
                calc_month_stuff = new CalcMonthStuff(this, trans, user_id);
	}

	public boolean collectData() {
		AutoLogger al = new AutoLogger("MonthReportPerUserRenderer") {

			@Override
			public void do_stuff() throws Exception {
				result = new Boolean(false);

                                DBTimeEntries entries = new DBTimeEntries();

				String where = " where "
						+ trans.markColumn(entries.user)
						+ " = "
						+ user_id
						+ " and "
						+ trans.getPeriodStmt(entries.from, new DateMidnight(year,
								mon, 1), new DateMidnight(year, mon, 1)
								.plusMonths(1).minusDays(1)) + " order by "
						+ trans.markColumn(entries.from);

				data = trans.fetchTable(entries, where);

				// alle Jobtypes ins Hirn blasen
				List<DBStrukt> res = trans.fetchTable(new DBJobType());

				for (DBStrukt s : res) {
					DBJobType jt = (DBJobType) s;
					job_types.put(jt.id.getValue(), jt);
				}

                                calc_month_stuff.calc();
                                upm = calc_month_stuff.getUPMRecord(year, mon);       
                                
                                if( upm != null )
                                    over_time = CalcMonthStuff.getOverTimeforUPM(upm);

				result = new Boolean(true);
			}
		};

		return (Boolean) al.result;
	}

	public String render() {
		clear();

		html_start();

		html_setTitle("Monatsbericht " + MonthReportPerUser.getTitle(mon, year)
				+ " für " + username);

		int day = 0;

		HashMap<Integer, Long> millis_per_jobtype_month = new HashMap<Integer, Long>();
		HashMap<Integer, Long> millis_per_jobtype_day = new HashMap<Integer, Long>();

                List<DBTimeEntries> entries_per_day = new ArrayList<DBTimeEntries>();
                long over_time_for_month_in_millis = 0;
                long extra_time_for_month_in_millis = 0;

		for (DBStrukt s : data) {
			DBTimeEntries e = (DBTimeEntries) s;

			Date dt_from = (Date) e.from.getValue();
			Date dt_to = (Date) e.to.getValue();

			DateTime d_from = new DateTime(dt_from.getTime());
			DateTime d_to = new DateTime(dt_to.getTime());

			if (day != d_from.getDayOfMonth()) {
				if (day != 0) {
					// summe pro Tag
					html_newline();
					write_sum_day(millis_per_jobtype_day);
					millis_per_jobtype_day.clear();

                                        if( !entries_per_day.isEmpty() ) {
                                            long over_time_in_millis = over_time.calcOverTimeForDay(entries_per_day, isHoliday(entries_per_day.get(0).from.getValue()));
                                            long extra_time_in_millis = over_time.calcExtraTimeForDay(entries_per_day, isHoliday(entries_per_day.get(0).from.getValue()));

                                            if( over_time_in_millis > 0 )
                                            {
                                                html_newline();
                                                html_bold("Überstunden: " + getDurFromMilli(over_time_in_millis + extra_time_in_millis));

                                                if( extra_time_in_millis > 0 )
                                                    html_normal_text(" ( " + getDurFromMilli(over_time_in_millis) + " * " + over_time.getOverTimeFactor() + " )" );
                                            }

                                            over_time_for_month_in_millis += over_time_in_millis;
                                            extra_time_for_month_in_millis += extra_time_in_millis;
                                        }
					html_blockquote_end();
				}

				day = d_from.getDayOfMonth();

				html_bold_title(d_from.dayOfMonth().getAsText(Locale.GERMAN)
						+ ". " + d_from.dayOfWeek().getAsText(Locale.GERMAN));

				html_blockquote_start();

				millis_per_jobtype_day.clear();
                                entries_per_day.clear();
			}

                        entries_per_day.add(e);

			Long duration = millis_per_jobtype_day.get((Integer) e.jobtype
					.getValue());

			if (duration == null)
				duration = new Long(0);

			millis_per_jobtype_day.put((Integer) e.jobtype.getValue(), duration
					+ e.calcDuration());

			duration = millis_per_jobtype_month.get((Integer) e.jobtype
					.getValue());

			if (duration == null)
				duration = new Long(0);

			millis_per_jobtype_month.put((Integer) e.jobtype.getValue(),
					duration + e.calcDuration());

			DateTime dur = new DateTime(new Time(
					e.calcDuration() - 60 * 60 * 1000));

			html_bold(d_from.toString("HH:mm") + " - " + d_to.toString("HH:mm")
					+ " = " + dur.toString("HH:mm"));
			html_normal_text(" ["
					+ job_types.get((Integer) e.jobtype.getValue()).name
							.toString() + "]");
			html_normal_text(" " + e.comment.getValue().toString());

			html_newline();
		}

		html_newline();
		write_sum_day(millis_per_jobtype_day);

                if (!entries_per_day.isEmpty()) {
                    long over_time_in_millis = over_time.calcOverTimeForDay(entries_per_day, isHoliday(entries_per_day.get(0).from.getValue()));
                    long extra_time_in_millis = over_time.calcExtraTimeForDay(entries_per_day, isHoliday(entries_per_day.get(0).from.getValue()));

                    if (over_time_in_millis > 0) {
                        html_newline();
                        html_bold("Überstunden: " + getDurFromMilli(over_time_in_millis + extra_time_in_millis));

                        if (extra_time_in_millis > 0) {
                            html_normal_text(" ( " + getDurFromMilli(over_time_in_millis) + " * " + over_time.getOverTimeFactor() + " )");
                        }
                    }

                    over_time_for_month_in_millis += over_time_in_millis;
                    extra_time_for_month_in_millis += extra_time_in_millis;
                }

		if (data.size() > 0)
			html_blockquote_end();

		html_bold_title("Gesamt:");
		html_blockquote_start();

		write_sum_month(millis_per_jobtype_month, over_time_for_month_in_millis, extra_time_for_month_in_millis);

		html_blockquote_end();

		html_stop();

		return text.toString();
	}

	private void write_sum_month(HashMap<Integer, Long> sum_data, long over_time_for_month_in_millis, long extra_time_for_month_in_millis) {

		Set<Integer> keys = sum_data.keySet();

                text.append("<table>");

		for (Integer key : keys) {
                        text.append("<tr><td>");
			html_bold(job_types.get(key).name.toString() + ":");
                        text.append("</td><td>");
			html_bold(getDurFromMilli(sum_data.get(key)));
                        text.append("</td></tr>");			
		}

		// Leistungszeit gesammt
		long lz = 0;
		long nlz = 0;
		long urlaub = 0;
		long summe = 0;

		for (Integer key : keys) {
			DBJobType jt = job_types.get(key);

			if (jt.type.getValue().equals(JOBTYPES.LZ.toString()))
				lz += sum_data.get(key);
			else
				nlz += sum_data.get(key);

			if (jt.is_holliday.getValue().equals(FLAGTYPES.JA.toString()))
				urlaub += sum_data.get(key);

			summe += sum_data.get(key);
		}

                text.append("<tr><td>");
                html_newline();
		text.append("</td></tr>");

                text.append("<tr><td>");
		html_bold("Leistungszeit: ");
                text.append("</td><td>");
                html_bold(getDurFromMilli(lz));
                text.append("</td></tr>");

                text.append("<tr><td>");
		html_bold("Nichtleistungszeit: ");
                text.append("</td><td>");
                html_bold(getDurFromMilli(nlz));
		text.append("</td></tr>");

                text.append("<tr><td>");
		html_bold("Urlaub: ");
                text.append("</td><td>");
                html_bold(getDurFromMilli(urlaub));
		text.append("</td></tr>");

                text.append("<tr><td colspan=2><hr/></td></tr>");

                text.append("<tr><td>");
		html_bold("Summe vor Überstunden:");
                text.append("</td><td>");
                html_bold(getDurFromMilli(summe));
		text.append("</td></tr>");

                text.append("<tr><td>");
                html_bold("Überstunden: ");
                text.append("</td><td>");
                html_bold(getDurFromMilli(over_time_for_month_in_millis + extra_time_for_month_in_millis));
                text.append("</td></tr>");


                text.append("<tr><td colspan=2><hr/></td></tr>");
                text.append("<tr><td>");
                html_bold("Summe: ");
                text.append("</td><td>");
                html_bold(getDurFromMilli(summe + over_time_for_month_in_millis + extra_time_for_month_in_millis));
                text.append("</td></tr>");

                text.append("</table>");

                html_newline();
                html_newline();

                text.append("<table>");

                text.append("<tr><td>");
                html_normal_text("Arbeitstage im Monat:");
                text.append("</td><td>");
                html_normal_text(String.valueOf(calc_month_stuff.getWorkDaysForMonth(new GregorianCalendar(year,mon,1).getTime())));
                text.append("</td></tr>");

                text.append("<tr><td>");
                html_normal_text("Sollstunden:");
                text.append("</td><td>");
                html_normal_text(getDurFromMilli(calc_month_stuff.getHoursPerMonthInMillis()));
                text.append("</td></tr>");

                text.append("<tr><td>");
                html_normal_text("Mehrstunden:");
                text.append("</td><td>");

                html_normal_text("<i>todo</i>");
                // html_normal_text(getDurFromMilli(calc_month_stuff.getCompleteTime().getMillis()));
                
                text.append("</td></tr>");

                text.append("<tr><td>");
                html_normal_text("Überstunden:");
                text.append("</td><td>");
                html_normal_text(calc_month_stuff.getOverTime().toString("HH:mm"));
                text.append("</td></tr>");

                text.append("<tr><td>");
                html_normal_text("Resturlaub:");
                text.append("</td><td>");

                text.append( calc_month_stuff.getRemainingLeave().toString("HH:mm") );

                double days = Rounding.rndDouble(calc_month_stuff.getRemainingLeave().getHours() / calc_month_stuff.getHoursPerDay(), 1);

                html_normal_text( " ( " + days + " Tage ) ");
                text.append("</td></tr>");

                text.append("</table>");

	}

	private String getDurFromMilli(long milli) {
		// DateTime dur = new DateTime(new Time(milli-60*60*1000));
		// return dur.toString("HH:mm");
		HMSTime hms_time = new HMSTime(milli);
		return hms_time.toString("HH:mm");
	}

	private void write_sum_day(HashMap<Integer, Long> sum_data) {

		Set<Integer> keys = sum_data.keySet();

		StringBuilder line = new StringBuilder();

		for (Integer key : keys) {
			line.append(" ");
			line.append(job_types.get(key).name.toString() + ": "
					+ getDurFromMilli(sum_data.get(key)));
		}

		html_bold(line.toString());
	}

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return mon;
    }

    public int getUserId() {
        return user_id;
    }

    private boolean isHoliday(Date day)
    {
        return isHoliday(new DateMidnight(day));
    }    

    public boolean isHoliday(DateMidnight day)
    {
        if (holidays == null) {
            return false;
        }

        HolidayInfo hi = holidays.getHolidayForDay(day);

        if (hi == null) {
            return false;
        }

        return hi.official_holiday;
    }
}

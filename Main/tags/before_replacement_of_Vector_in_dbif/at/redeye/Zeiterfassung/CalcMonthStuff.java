/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Zeiterfassung;

import at.redeye.FrameWork.utilities.calendar.Holidays.HolidayInfo;
import java.sql.SQLException;
import java.util.Vector;

import org.joda.time.DateMidnight;

import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.base.bindtypes.DBDateTime;
import at.redeye.FrameWork.base.bindtypes.DBStrukt;
import at.redeye.FrameWork.base.transaction.Transaction;
import at.redeye.FrameWork.utilities.HMSTime;
import at.redeye.FrameWork.utilities.Rounding;
import at.redeye.FrameWork.utilities.Time;
import at.redeye.FrameWork.widgets.calendar.DisplayMonth;
import at.redeye.FrameWork.widgets.calendarday.DisplayDay;
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
import at.redeye.Zeiterfassung.overtime.Schema_1;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.apache.log4j.Logger;

/**
 *
 * @author martin
 */
public class CalcMonthStuff 
{
    double hours_per_day=0;
    double hours_per_month=0;
    double hours_per_month_done=0;
    Transaction trans;
    DisplayMonth month;
    Root root;
    HMSTime complete_time = new HMSTime();
    HMSTime remaining_leave = new HMSTime();
    HMSTime overtime = new HMSTime();
    private static Logger logger = Logger.getLogger(CalcMonthStuff.class);
    StringBuilder error_log = new StringBuilder();
    HMSTime time_correction_month_done = new HMSTime();
    
    public CalcMonthStuff( DisplayMonth month, Transaction trans, Root root )
    {
        this.month = month;
        this.trans = trans;
        this.root = root;        
    }
    
    public boolean calcHoursPerDay() throws TableBindingNotRegisteredException, UnsupportedDBDataTypeException, SQLException, WrongBindFileFormatException, CloneNotSupportedException, DuplicateRecordException
    {
        hours_per_day = getHoursPerDay(month.getYear(), month.getMonth());

        if( hours_per_day >= 0 )
            return true;

        return false;
    }

    public double getHoursPerDay( Date date ) throws TableBindingNotRegisteredException, UnsupportedDBDataTypeException, SQLException, WrongBindFileFormatException, CloneNotSupportedException, DuplicateRecordException
    {
        DateMidnight dm = new DateMidnight( date );
        return getHoursPerDay( dm.getYear(), dm.getMonthOfYear() );
    }

    public static double getHoursPerDay(DBUserPerMonth upm)
    {
        double usage = (Double) upm.usage.getValue();

        if (usage <= 0) {
            return -1;
        }

        Double days_per_week = (Double) upm.days_per_week.getValue();

        if (days_per_week == 0.0) {
            days_per_week = 5.0;
        }

        double dhours_per_day = (((Double) upm.hours_per_week.getValue()) /
                days_per_week) * (usage / 100.0);

        return dhours_per_day;
    }

    public double getHoursPerDay( int year, int month ) throws TableBindingNotRegisteredException, UnsupportedDBDataTypeException, SQLException, WrongBindFileFormatException, CloneNotSupportedException, DuplicateRecordException
    {
        DBUserPerMonth upm = getUPMRecord( year, month );

        if( upm == null )
            return -1;

        return getHoursPerDay(upm);
    }

    private DBUserPerMonth getUPMRecord( int year, int month ) throws TableBindingNotRegisteredException, UnsupportedDBDataTypeException, SQLException, WrongBindFileFormatException, CloneNotSupportedException, DuplicateRecordException
    {
        DBUserPerMonth upm;

        upm = GetUserPerMonthRecord.getValidRecordForMonth(trans, root.getUserId(), year, month);

        if( upm == null )
        {
            logger.error("Der Monatseintrag des Benutzers konnte nicht berechnet werden!");
            error("Der Monatseintrag des Benutzers konnte nicht berechnet werden!\n"+
                  "Überprüfen sie die Daten im Menü \"Einstellungen\" => \"Monatseinstellungen für die Benutzer\".");
            return null;
        }

        return upm;
    }

    public void calcHoursPerMonth()
    {
        hours_per_month = 0;
        
        for( int i = 1; i <= month.getDaysOfMonth(); i++ )
        {            
            DisplayDay day = month.getDay(i);
            
            if( day.isHoliday() )
                continue;
            
            if( day.isSaturday() )
                continue;
                        
            if( day.isSunday() )            
                continue;
            
            hours_per_month += hours_per_day;            
        }
    }

    public static int getDaysOfMonth( Date date )
    {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);

        return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public boolean isHoliday( Date day )
    {
        return isHoliday(new DateMidnight(day));
    }

    public boolean isHoliday( DateMidnight day )
    {
        HolidayInfo holidays = month.getHolidays().getHolidayForDay(day);

        if( holidays == null )
            return false;

        return holidays.official_holiday;
    }

    public int getWorkDaysForMonth( Date date )
    {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);

        int days_of_month = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        int days_to_work = 0;

        for( int i = 1; i <= days_of_month; i++ )
        {
           cal.set(Calendar.DAY_OF_MONTH, i);

           if( cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY )
               continue;

           if( cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY )
               continue;

           if( isHoliday(new DateMidnight(cal)) )
               continue;

           days_to_work++;
        }

        return days_to_work;
    }
    
    public void calcHoursPerMonthDone() throws SQLException, TableBindingNotRegisteredException, UnsupportedDBDataTypeException, WrongBindFileFormatException, CloneNotSupportedException, DuplicateRecordException
    {
        hours_per_month_done = 0;
        time_correction_month_done.setTime(0);
        
        int m = month.getMonth();
        int y = month.getYear();                                               

        DBUserPerMonth upm = getUPMRecord(y, m);

        if (upm==null) {
            return;
        }

        DateMidnight dmStart = new DateMidnight( y, m , 1 );
        
        // One month later minus 1 second 
        // (e.g. 01.01.2010 00:00:00 till 31.01.2010 23:59:59)
        DateMidnight dmEnd = dmStart.plusMonths(1).minusDays(1);
        
        
        String where = "where " 
        	+ trans.markColumn("user")
        	+ " = " + root.getUserId()
        	+ " and " 
        	+ trans.getPeriodStmt("from", dmStart, dmEnd )
        	+ " order by " + trans.markColumn("from");
        
        Vector<DBStrukt> res = trans.fetchTable(new DBTimeEntries(), where );
        
        long millis = 0;                

        Vector<DBTimeEntries> last_day = new Vector<DBTimeEntries>();

        Calendar cal_before = new GregorianCalendar();
        Calendar cal_act = new GregorianCalendar();
        OvertimeInterface calc_overtime = getOverTimeforUPM(upm);

        for( DBStrukt s : res )
        {
            DBTimeEntries te = (DBTimeEntries) s;
                        
            millis += te.calcDuration();

            if( last_day.isEmpty() )
            {
                cal_before.setTime(te.from.getValue());
                last_day.add(te);
            } else {
                cal_act.setTime(te.from.getValue());
                if( cal_before.get(Calendar.DAY_OF_MONTH) != cal_act.get(Calendar.DAY_OF_MONTH) )
                {
                    cal_before.setTime(te.from.getValue());
                    long ot = calc_overtime.calcExtraTimeForDay(last_day, month.getDay(cal_act.get(Calendar.DAY_OF_MONTH)).isHoliday() );
                    last_day.clear();
                    last_day.add(te);

                    if( ot != 0 )
                    {
                        logger.info( DBDateTime.getDateStr(te.from.getValue()) + " added " + ot + " to overtime.");
                    }

                    time_correction_month_done.addMillis(ot);
                } else {
                    last_day.add(te);
                }
            }
        }

        if( last_day.size() > 0 )
        {
           long ot = calc_overtime.calcExtraTimeForDay(last_day, month.getDay(cal_act.get(Calendar.DAY_OF_MONTH)).isHoliday() );

           if( ot > 0 )
           {
             logger.info( DBDateTime.getDateStr(last_day.get(0).from.getValue()) + " added " + ot + " to overtime.");
           }

           time_correction_month_done.addMillis(ot);
        }
                                
        hours_per_month_done = millis;
                
        complete_time = new HMSTime( millis );
    }
    
    public void calc() throws TableBindingNotRegisteredException, UnsupportedDBDataTypeException, SQLException, WrongBindFileFormatException, CloneNotSupportedException, DuplicateRecordException
    {
        error_log.setLength(0);

        if( !calcHoursPerDay() )
        {
            error("Die Anzahl der Arbeitsstunden pro Tag konnte nicht berechnet werden.");
        } else {

            calcHoursPerMonth();

            calcHoursPerMonthDone();

            if( !calcRemainingLeave() )
                error("Der Resturlaub konnte nicht berechnet werden.");

            if( !calcOverTime() )
                error("Der Gesammtarbeitszeit konnte nicht berechnet werden.");
        }
    }

    private boolean matchUserEntryForDate( DBUserPerMonth upm, Date date )
    {
        Date from = (Date) upm.from.getValue();
        Date to = (Date) upm.to.getValue();

        long lfrom = from.getTime();
        long lto = to.getTime();

        if ( Time.isMinimumTime(lto) ) {
            lto = 0;
        }

        if (Time.isMinimumTime(lfrom) ) {
            lfrom = 0;
        }

        long current = date.getTime();

        if (lfrom < current && (lto >= current || lto == 0)) {
            return true;
        }

        return false;
    }

    private String getJobTyesForHoliday() throws SQLException, TableBindingNotRegisteredException, UnsupportedDBDataTypeException, WrongBindFileFormatException
    {
        DBJobType types = new DBJobType();

        Vector<DBStrukt> res = trans.fetchTable(types,
                "where " + trans.markColumn(types.is_holliday) + "='JA'");

        StringBuilder line = new StringBuilder();

        for( int i = 0; i < res.size(); i++ )
        {
            DBJobType type = (DBJobType) res.get(i);

            if( i > 0 )
                line.append(",");

            line.append(type.id.toString());
           
        }

        return line.toString();
    }

    private String getJobTyesForWork() throws SQLException, TableBindingNotRegisteredException, UnsupportedDBDataTypeException, WrongBindFileFormatException
    {
        DBJobType types = new DBJobType();

        Vector<DBStrukt> res = trans.fetchTable(types,
                "where "
                + trans.markColumn(types.is_holliday) + "='JA'"
                + " or "
                + trans.markColumn(types.type) + "='" + JOBTYPES.LZ.toString() + "'");

        StringBuilder line = new StringBuilder();

        for( int i = 0; i < res.size(); i++ )
        {
            DBJobType type = (DBJobType) res.get(i);

            if( i > 0 )
                line.append(",");

            line.append(type.id.toString());
           
        }

        return line.toString();
    }

    /*
     * berechnet den Resturlaub
     */
    public boolean calcRemainingLeave() throws SQLException, TableBindingNotRegisteredException, UnsupportedDBDataTypeException, WrongBindFileFormatException, DuplicateRecordException
    {
        GregorianCalendar gdate = new GregorianCalendar(month.getYear(), month.getMonth()-1, 1);

        Date date = gdate.getTime();

        DBUserPerMonth upm = GetUserPerMonthRecord.getValidRecordForMonth(trans, root.getUserId(), month.getYear(), month.getMonth() );

        if( upm == null )
        {
            String msg = "Keinen passenden Monatseintrag gefunden. Die Urlaubsstunden können nicht berechnet werden.";
            logger.error(msg);
            error(msg);
            return false;
        }

        DBTimeEntries entries = new DBTimeEntries();

        Date from = upm.from.getValue();
        Date to = upm.to.getValue();

        logger.info(to.getTime());

        if( Time.isMinimumTime( to.getTime() ) )
        {
            // der letzte tag des aktuellen Monats
            GregorianCalendar gdate2 = new GregorianCalendar(month.getYear(), month.getMonth()-1, month.getDaysOfMonth());
            to = gdate2.getTime();
        }

        String JobTypeString = getJobTyesForHoliday();

        if( JobTypeString.isEmpty() )
        {
            String msg = "Keine Urlaubstätigkeiten gefunden. Resturlaubsberechnung nicht möglich.\n" +
                    "Bitte kontrollieren Sie die konfigurierten Tätigkeiten im Menü \"Einstellungen\" => \"Tätigkeiten\"";
            logger.info(msg);
            error(msg);
            return false;
        }

        String job_type_sql = " and " + trans.markColumn(entries.jobtype) + " in ("  + JobTypeString + ") ";

        Vector<DBStrukt> res = trans.fetchTable(
                entries,
                "where " + trans.getPeriodStmt("from", new DateMidnight(from), new DateMidnight(to)) +
                " and " + trans.markColumn(entries.user) + " = " + root.getUserId() + 
                job_type_sql +
                " order by " + trans.markColumn(entries.from));


        if( res.size() <= 0 )
        {
            logger.warn("Noch keine Einträge gefunden mit: " + trans.getSql() );            
        }

        Double durlaub = upm.hours_holidays.getValue();
        long urlaub = durlaub.longValue() * 60*60*1000;

        for( int i = 0; i < res.size(); i++ )
        {
            DBTimeEntries entry = (DBTimeEntries) res.get(i);
            urlaub -= entry.calcDuration();
        }

        remaining_leave = new HMSTime(urlaub);

        return true;
    }

    private boolean calcOverTime() throws SQLException, TableBindingNotRegisteredException, UnsupportedDBDataTypeException, WrongBindFileFormatException, DuplicateRecordException
    {
        GregorianCalendar gdate = new GregorianCalendar(month.getYear(), month.getMonth()-1, 1);

        Date date = gdate.getTime();

        DBUserPerMonth upm = GetUserPerMonthRecord.getValidRecordForMonth(trans, root.getUserId(), month.getYear(), month.getMonth() );

        if( upm == null )
        {
            String msg = "Keinen passenden Monatseintrag gefunden. Die Überstunden können nicht berechnet werden.\n" +
                    "Bitte kontrollieren Sie die \"Monatseinstellungen für die Benutzer\"";
            logger.error(msg);
            error(msg);
            return false;
        }

        DBTimeEntries entries = new DBTimeEntries();

        Date from = upm.from.getValue();
        Date to = upm.to.getValue();

        logger.info(to.getTime());

        if( Time.isMinimumTime(to.getTime() ) )
        {
            // der letzte Tag des aktuellen Monats
            GregorianCalendar gdate2 = new GregorianCalendar(month.getYear(), month.getMonth()-1, month.getDaysOfMonth());
            to = gdate2.getTime();
            logger.trace("Setting to to:" +  DBDateTime.getDateStr(to));
        }

        Vector<DBStrukt> res = trans.fetchTable(
                entries,
                "where " + trans.getPeriodStmt("from", new DateMidnight(from), new DateMidnight(to)) +
                " and " + trans.markColumn(entries.user) + " = " + root.getUserId() + 
                " and " + trans.markColumn(entries.jobtype) + " in ("  + getJobTyesForWork() + ")");


        if( res.size() <= 0 )
        {
            logger.warn("Noch keine Einträge gefunden mit: " + trans.getSql() );
            return true;
        }

        Double dovertime = upm.hours_overtime.getValue();
        long lovertime = dovertime.longValue() * 60*60*1000;

        logger.trace(trans.getSql());

        Vector<DBTimeEntries> last_day = new Vector<DBTimeEntries>();

        Calendar cal_before = new GregorianCalendar();
        Calendar cal_act = new GregorianCalendar();
        OvertimeInterface calc_overtime = getOverTimeforUPM(upm);

        for( int i = 0; i < res.size(); i++ )
        {
            DBTimeEntries entry = (DBTimeEntries) res.get(i);
            lovertime += entry.calcDuration();

            if( last_day.isEmpty() )
            {
                cal_before.setTime(entry.from.getValue());
                last_day.add(entry);
            } else {
                cal_act.setTime(entry.from.getValue());
                if( cal_before.get(Calendar.DAY_OF_MONTH) != cal_act.get(Calendar.DAY_OF_MONTH) )
                {
                    cal_before.setTime(entry.from.getValue());                                        

                    long ot = calc_overtime.calcExtraTimeForDay(last_day, isHoliday(entry.from.getValue()) );
                    last_day.clear();
                    last_day.add(entry);

                    if( ot > 0 )
                    {
                        logger.trace( DBDateTime.getDateStr(entry.from.getValue()) + " added " + ot + " to overtime.");
                    }

                    lovertime += ot;
                } else {
                    last_day.add(entry);
                }
            }

            String msg = String.format("%s - %s => %s (%s)",
                        DBDateTime.getDateStr(entry.from.getValue()),
                        DBDateTime.getDateStr(entry.to.getValue()),
                        new HMSTime(entry.calcDuration()).toString("HH:mm"),
                        entry.comment.getValue());

            logger.trace(msg);
        }

        if( last_day.size() > 0 )
        {
            long ot = calc_overtime.calcExtraTimeForDay(last_day, month.getDay(cal_act.get(Calendar.DAY_OF_MONTH)).isHoliday() );

            if( ot > 0 )
            {
                  logger.trace( DBDateTime.getDateStr(last_day.get(0).from.getValue()) + " added " + ot + " to overtime.");
            }

            lovertime += ot;
        }

        // so jetzt haben wir auf der Haben Seite alle Arbeitszeiten zusammenaddiert.
        // nun muß die Sollartbeitszeit abgezogen werden.

        DateMidnight cal_from = new DateMidnight(from);
        DateMidnight cal_to = new DateMidnight(to);

        double regular_work_time = 0;

        logger.info( "from: " + DBDateTime.getDateStr(cal_from) + " to " + DBDateTime.getDateStr(cal_to) );

        int start = cal_from.getMonthOfYear();
        for( int i = start; i <= cal_to.getMonthOfYear(); i++ )
        {
            Date aktual_day = cal_from.plusMonths(start-i).toDate();
            long work_days_for_month = getWorkDaysForMonth(aktual_day);
            double dhours_per_day = getHoursPerDay(upm);

            // ansonsten werden die Feiertage nicht berücksichtigt, was auch schlecht ist.
            // double work_time_for_month = work_days_for_month * dhours_per_day;

            double work_time_for_month = hours_per_month;

            logger.info(String.format("%d %s Working Days: %d regular working hours per month: %f working hours per day: %f",
                    i, DBDateTime.getDateStr(aktual_day), work_days_for_month, work_time_for_month, dhours_per_day));

            regular_work_time += work_time_for_month;
        }

        long correction = 0;
        
        if( calc_overtime != null )
            correction = calc_overtime.calcExtraTimeForMonth((long) (regular_work_time * 60 * 60 * 1000), lovertime);
                
        double overtime_result = (lovertime-correction)/60/60/1000.0 - (regular_work_time);

        logger.info(String.format("overal working hours from %s to %s: %.3f regular working hours: %.3f result: %.3f",
                DBDateTime.getDateStr(from),
                DBDateTime.getDateStr(to),
                Rounding.rndDouble(lovertime/60/60/1000.0, 3),
                Rounding.rndDouble(regular_work_time, 3),
                Rounding.rndDouble(overtime_result,3)));

        overtime.setTime((long)(overtime_result*60*60*1000));

        return true;
    }

    public String getError()
    {
        return error_log.toString();
    }

    public boolean hasError()
    {
        return error_log.length() > 0;
    }

    private void error(String text)
    {
        if( error_log.length() > 0 )
            error_log.append("\n");
        error_log.append(text);
    }

    public static OvertimeInterface getOverTimeforUPM( DBUserPerMonth upm )
    {
        SCHEMAS schema = SCHEMAS.valueOf(upm.overtime_rule.getValue());


        switch( schema )
        {
            case KEINES: return new None();
            case PAUSCHALIST: return new FlatRate();
            case ÜBERSTUNDENSCHEMA_01: return new Schema_1();
        }

        return null;
    }

    public String getFormatedHoursPerMonth()
    {
       HMSTime hms_time = new HMSTime();
       hms_time.setTime(getHoursPerMonthDoneinMillis());
       return hms_time.toString("HH:mm");
    }

    public long getHoursPerMonthDoneinMillis() {
        return (long) (hours_per_month * 1000 * 60 * 60);
    }
}

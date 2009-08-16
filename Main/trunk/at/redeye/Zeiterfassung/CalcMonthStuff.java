/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Zeiterfassung;

import java.sql.SQLException;
import java.util.Vector;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;

import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.base.bindtypes.DBDateTime;
import at.redeye.FrameWork.base.bindtypes.DBStrukt;
import at.redeye.FrameWork.base.transaction.Transaction;
import at.redeye.FrameWork.utilities.HMSTime;
import at.redeye.FrameWork.widgets.calendar.DisplayMonth;
import at.redeye.FrameWork.widgets.calendarday.DisplayDay;
import at.redeye.SqlDBInterface.SqlDBIO.impl.TableBindingNotRegisteredException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.UnsupportedDBDataTypeException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.WrongBindFileFormatException;
import at.redeye.Zeiterfassung.bindtypes.DBJobType;
import at.redeye.Zeiterfassung.bindtypes.DBTimeEntries;
import at.redeye.Zeiterfassung.bindtypes.DBUserPerMonth;
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
    
    public CalcMonthStuff( DisplayMonth month, Transaction trans, Root root )
    {
        this.month = month;
        this.trans = trans;
        this.root = root;        
    }
    
    public boolean calcHoursPerDay() throws TableBindingNotRegisteredException, UnsupportedDBDataTypeException, SQLException, WrongBindFileFormatException, CloneNotSupportedException, DuplicateRecordException
    {
        hours_per_day = getHoursPerDay(month.getYear(), month.getMonth());

        if( hours_per_day > 0 )
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
            return 0;
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
        DBUserPerMonth upm;        
        
        upm = GetUserPerMonthRecord.getValidRecordForMonth(trans, root.getUserId(), year, month);
        
        if( upm == null )
        {
            logger.error("Der Moantseintrag des Benutzers konnte nicht berechnet werden!");
            return 0;
        }
        
        return getHoursPerDay(upm);
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
    
    public void calcHoursPerMonthDone() throws SQLException, TableBindingNotRegisteredException, UnsupportedDBDataTypeException, WrongBindFileFormatException
    {
        hours_per_month_done = 0;
        
        int m = month.getMonth();
        int y = month.getYear();                                               
        
        DateMidnight dmStart = new DateMidnight( y, m , 1 );
        
        // One month later minus 1 second 
        // (e.g. 01.01.2010 00:00:00 till 31.01.2010 23:59:59)
        DateTime dmEnd = new DateTime(dmStart.plusMonths(1)); 
        
        
        String where = "where " 
        	+ trans.markColumn("user")
        	+ "='" + root.getUserId() + "' " 
        	+ " and " 
        	+ trans.getPeriodStmt("from", 
        			DBDateTime.getStdString(dmStart), 
        			DBDateTime.getStdString(dmEnd) )
        	+ " order by " + trans.markColumn("from");
        
        Vector<DBStrukt> res = trans.fetchTable(new DBTimeEntries(), where );
        
        long millis = 0;                
        
        for( DBStrukt s : res )
        {
            DBTimeEntries te = (DBTimeEntries) s;
                        
            millis += te.calcDuration();
        }
                                
        hours_per_month_done = millis;
                
        complete_time = new HMSTime( millis );
    }
    
    public void calc() throws TableBindingNotRegisteredException, UnsupportedDBDataTypeException, SQLException, WrongBindFileFormatException, CloneNotSupportedException, DuplicateRecordException
    {
        if( calcHoursPerDay() )
        {
            calcHoursPerMonth();
            calcHoursPerMonthDone();
            calcRemainingLeave();
            calcOverTime();
        }
    }

    private boolean matchUserEntryForDate( DBUserPerMonth upm, Date date )
    {
        Date from = (Date) upm.from.getValue();
        Date to = (Date) upm.to.getValue();

        long lfrom = from.getTime();
        long lto = to.getTime();

        if (lto <= 2000 * 60 * 60) {
            lto = 0;
        }

        if (lfrom <= 2000 * 60 * 60) {
            lfrom = 2000 * 60 * 60;
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

            line.append("'");
            line.append(type.id.toString());
            line.append("'");
        }

        return line.toString();
    }

    /*
     * berechnet den Resturlaub
     */
    public void calcRemainingLeave() throws SQLException, TableBindingNotRegisteredException, UnsupportedDBDataTypeException, WrongBindFileFormatException, DuplicateRecordException
    {
        GregorianCalendar gdate = new GregorianCalendar(month.getYear(), month.getMonth(), 1);

        Date date = gdate.getTime();

        DBUserPerMonth upm = GetUserPerMonthRecord.getValidRecordForMonth(trans, root.getUserId(), month.getYear(), month.getMonth() );

        if( upm == null )
        {
            logger.error("Keinen passenden Monatseintrag gefunden. Die Urlaubsstunden können nicht berechnet werden.");
            return;
        }

        DBTimeEntries entries = new DBTimeEntries();

        Date from = upm.from.getValue();
        Date to = upm.to.getValue();

        logger.info(to.getTime());

        if( to.getTime() <= 2000*60*60 )
        {
            // der letzte tag des aktuellen Monats
            GregorianCalendar gdate2 = new GregorianCalendar(month.getYear(), month.getMonth(), month.getDaysOfMonth());
            to = gdate2.getTime();
        }

        Vector<DBStrukt> res = trans.fetchTable(
                entries,
                "where " + trans.getPeriodStmt("from", new DateMidnight(from), new DateMidnight(to)) +
                " and " + trans.markColumn(entries.user) + "='" + root.getUserId() + "'" +
                " and " + trans.markColumn(entries.jobtype) + " in ("  + getJobTyesForHoliday() + ")");


        if( res.size() <= 0 )
        {
            logger.warn("Noch keine Einträge gefunden mit: " + trans.getSql() );
            return;
        }

        Double durlaub = upm.hours_holidays.getValue();
        long urlaub = durlaub.longValue() * 60*60*1000;

        for( int i = 0; i < res.size(); i++ )
        {
            DBTimeEntries entry = (DBTimeEntries) res.get(i);
            urlaub -= entry.calcDuration();
        }

        remaining_leave = new HMSTime(urlaub);
    }

    private void calcOverTime()
    {

    }
}

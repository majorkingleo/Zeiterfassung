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
import at.redeye.Zeiterfassung.bindtypes.DBTimeEntries;
import at.redeye.Zeiterfassung.bindtypes.DBUserPerMonth;

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
    
    public CalcMonthStuff( DisplayMonth month, Transaction trans, Root root )
    {
        this.month = month;
        this.trans = trans;
        this.root = root;        
    }
    
    public boolean calcHoursPerDay() throws TableBindingNotRegisteredException, UnsupportedDBDataTypeException, SQLException, WrongBindFileFormatException, CloneNotSupportedException, DuplicateRecordException
    {
        /*
        Vector<DBStrukt> res = trans.fetchTable(new DBUserPerMonth(), 
                "where " + 
                    trans.markColumn("user")  + "='" + root.getUserId() + "'" +
                    " and " +
                    trans.markColumn( "locked" ) + "='NEIN'" );
                         
        if( res.size() == 0 )
            return false;
        */
        
        DBUserPerMonth upm;
        
        
        upm = GetUserPerMonthRecord.getValidRecordForMonth(trans, root.getUserId(), month.getYear(), month.getMonth());
        
        if( upm == null )
            return false;
        
        double usage = (Double)upm.usage.getValue();
        
        if( usage <= 0 )
            return false;
      
        Double days_per_week = (Double) upm.days_per_week.getValue();
        
        if( days_per_week == 0.0 )
            days_per_week = 5.0;
        
        hours_per_day = (((Double)upm.hours_per_week.getValue()) / 
                            days_per_week) * ( usage / 100.0 );
        
        return true;
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
        }
    }
}

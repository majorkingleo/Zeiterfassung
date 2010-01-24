/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Zeiterfassung;

import java.sql.SQLException;
import java.util.Date;
import java.util.Vector;

import org.joda.time.DateMidnight;

import at.redeye.FrameWork.base.bindtypes.DBStrukt;
import at.redeye.FrameWork.base.bindtypes.DBValue;
import at.redeye.FrameWork.base.transaction.Transaction;
import at.redeye.FrameWork.widgets.AutoCompleteTextField;
import at.redeye.SqlDBInterface.SqlDBIO.impl.TableBindingNotRegisteredException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.UnsupportedDBDataTypeException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.WrongBindFileFormatException;
import at.redeye.Zeiterfassung.bindtypes.DBTimeEntries;
import at.redeye.Zeiterfassung.bindtypes.DBUserPerMonth;
import java.util.HashMap;

/**
 *
 * @author martin
 */
public class TimeEntryCache {
    
    Vector<DBStrukt> entries = new Vector<DBStrukt>();
    boolean outdated = true;
    HashMap<String,AutoCompleteInfo> info_map = new HashMap<String,AutoCompleteInfo>();

    public DBUserPerMonth getUPM( Transaction trans, int userid, DateMidnight day ) throws SQLException, TableBindingNotRegisteredException, UnsupportedDBDataTypeException, WrongBindFileFormatException, DuplicateRecordException
    {
        DBUserPerMonth upm = GetUserPerMonthRecord.getValidRecordForMonth(trans, userid, day.getYear(), day.getMonthOfYear());
        return upm;
    }
    
    public Vector<DBStrukt> getEntries( Transaction trans, int userid, DateMidnight day ) throws SQLException, TableBindingNotRegisteredException, UnsupportedDBDataTypeException, WrongBindFileFormatException
    {
        refresh( trans, userid, day );
           
        Vector<DBStrukt> res = new Vector<DBStrukt>();
        
        long begin = day.getMillis();
        long end = day.plusDays(1).getMillis();
        
        // System.out.println( "Requested: " + day.toString() + " from " + begin + " till " + end );
        
        for( int i = 0; i < entries.size(); i++ )
        {
            DBTimeEntries e = (DBTimeEntries) entries.get(i);
            
            Date d = (Date) e.from.getValue();
                        
            long millis = d.getTime();
                                    
            if( begin <= millis && 
                millis < end ) 
            {
             //   System.out.println( "Found: " + d.toString() );
                res.add(e);
            } else {                
           //     System.out.println( "not match: " + d.toString() + " millis: " + millis );
            }
            /*
            if( end > millis )
                break;
             * */
        }
        
        return res;
    }
    
    protected void refresh( Transaction trans, int userid, DateMidnight day ) throws SQLException, TableBindingNotRegisteredException, UnsupportedDBDataTypeException, WrongBindFileFormatException
    {
        if( !outdated )
            return;                
        
        DateMidnight dm_from = day.minusDays(day.getDayOfMonth() + 10);
        DateMidnight dm_to = day.plusMonths(1).plusDays(12);
        
        String where = "where "
            		+ trans.markColumn("user")
            		+ "=" + userid + " " 
                 	+ " and "
                 	+ trans.getPeriodStmt("from", dm_from, dm_to )
                 	+ " order by " + trans.markColumn("from");
                
        entries = trans.fetchTable( new DBTimeEntries(), where );
        
        System.out.println( "number of entries: "  + entries.size());
        
        outdated = false;
    }
    
    public void setOutdated()
    {
        outdated = true;
        info_map.clear();
    }

    public AutoCompleteInfo getAutoCompleteInfoFor( DBValue val )
    {
        AutoCompleteInfo info = info_map.get(val.getName());

        if( info != null )
            return info;

        info = new AutoCompleteInfo();

        for( int i = 0; i < entries.size(); i++ )
        {
            DBStrukt s = entries.get(i);
            DBValue value = s.getValue(val);

            if( value != null )
                info.add(value);
        }

        return info;
    }

}

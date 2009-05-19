/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Zeiterfassung;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.Vector;

import org.joda.time.DateMidnight;

import at.redeye.FrameWork.base.bindtypes.DBStrukt;
import at.redeye.FrameWork.base.transaction.Transaction;
import at.redeye.FrameWork.utilities.calendar.Holidays;
import at.redeye.SqlDBInterface.SqlDBIO.impl.TableBindingNotRegisteredException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.UnsupportedDBDataTypeException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.WrongBindFileFormatException;
import at.redeye.Zeiterfassung.bindtypes.DBExtraHolidays;

/**
 *
 * @author martin
 */
public class LocalHolidays implements Holidays
{
    Transaction trans;
    Vector<DBStrukt> values = new Vector<DBStrukt>();
    
    public LocalHolidays(Transaction trans)
    {
        this.trans=trans;   
    }
    
    public void load() throws SQLException, TableBindingNotRegisteredException, UnsupportedDBDataTypeException, WrongBindFileFormatException, CloneNotSupportedException
    {
       values = trans.fetchTable(new DBExtraHolidays());
    }

    public Collection<HolidayInfo> getHolidays(int year) 
    {
        Vector<HolidayInfo> hi = new Vector<HolidayInfo>();
        
        for( int i = 0; i < values.size(); i++ )
        {
           DBExtraHolidays e = (DBExtraHolidays) values.get(i);
            
           DateMidnight d1 = new DateMidnight(((Date)e.date.getValue()));
           
           if( d1.getYear() == year )
           {                               
               hi.add(new HolidayInfo(d1,
                                      false,
                                      ((String)e.official_holiday.getValue()).equalsIgnoreCase("JA"),
                                      e.title.getValue().toString(),
                                      "",
                                      true) );
           }
        }
        
        return hi;
    }

    public int getNumberOfCountryCodes() {
        return 1;
    }

    public String getPrimaryCountryCode() {
        return "";
    }

}

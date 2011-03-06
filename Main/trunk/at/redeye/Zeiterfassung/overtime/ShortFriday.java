/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Zeiterfassung.overtime;

import at.redeye.FrameWork.utilities.calendar.Holidays;
import at.redeye.FrameWork.utilities.calendar.Holidays.HolidayInfo;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

/**
 *
 * @author martin
 */
public class ShortFriday implements Hours4DayInterface
{
    double monday_til_saturday;
    double friday;

    public ShortFriday( double monday_til_saturday, double friday )
    {
        this.monday_til_saturday = monday_til_saturday;
        this.friday = friday;
    }

    @Override
    public double getHours4Day(LocalDate dm, Holidays holidays)
    {
        HolidayInfo holiday =  holidays.getHolidayForDay(dm);

         if( holiday != null && holiday.official_holiday )
         {
             return 0;
         }

         switch( dm.getDayOfWeek() )
         {
             case DateTimeConstants.SATURDAY:
             case DateTimeConstants.SUNDAY:
                 return 0;

             case DateTimeConstants.MONDAY:
             case DateTimeConstants.TUESDAY:
             case DateTimeConstants.WEDNESDAY:
             case DateTimeConstants.THURSDAY:
                 return monday_til_saturday;

             case DateTimeConstants.FRIDAY:
                 return friday;
         }

         return 0;
    }

}

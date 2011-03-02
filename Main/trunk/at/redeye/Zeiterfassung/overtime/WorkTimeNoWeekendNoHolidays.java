/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Zeiterfassung.overtime;

import at.redeye.FrameWork.utilities.calendar.Holidays;
import at.redeye.FrameWork.utilities.calendar.Holidays.HolidayInfo;
import at.redeye.Zeiterfassung.bindtypes.DBUserPerMonth;
import org.joda.time.DateMidnight;
import org.joda.time.DateTimeConstants;

/**
 *
 * @author martin
 */
public class WorkTimeNoWeekendNoHolidays implements Hours4DayInterface
{
    DBUserPerMonth upm;

    public WorkTimeNoWeekendNoHolidays( DBUserPerMonth upm )
    {
        this.upm = upm;
    }

    public double getHours4Day(DateMidnight dm, Holidays holidays)
    {
         if( dm.getDayOfWeek() == DateTimeConstants.SATURDAY )
            return 0;

         if( dm.getDayOfWeek() == DateTimeConstants.SUNDAY )
            return 0;

         HolidayInfo holiday =  holidays.getHolidayForDay(dm);

         if( holiday != null && holiday.official_holiday )
         {
             return 0;
         }

         return getHoursPerDay();
    }

    public double getHoursPerDay()
    {
        double usage = (Double) upm.usage.getValue();

        if (usage <= 0) {
            return -1;
        }

        Double days_per_week = (Double) upm.days_per_week.getValue();

        if (days_per_week == 0.0) {
            days_per_week = 5.0;
        }

        double dhours_per_day = (((Double) upm.hours_per_week.getValue()) / days_per_week)
                * (usage / 100.0);

        return dhours_per_day;
    }

    public DBUserPerMonth getUPM()
    {
        return upm;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Zeiterfassung.overtime;

import at.redeye.FrameWork.utilities.HMSTime;
import at.redeye.FrameWork.utilities.calendar.Holidays;
import at.redeye.Zeiterfassung.bindtypes.DBTimeEntries;
import at.redeye.Zeiterfassung.bindtypes.DBUserPerMonth;
import java.util.Collection;
import org.joda.time.LocalDate;

/**
 *
 * @author martin
 */
public class FlatRate implements OvertimeInterface
{
    Hours4DayInterface hours4day;

    public FlatRate( DBUserPerMonth upm )
    {
        this(new WorkTimeNoWeekendNoHolidays(upm));
    }

    public FlatRate( Hours4DayInterface hours_per_day )
    {
        this.hours4day = hours_per_day;
    }

    public long calcExtraTimeForDay(Collection<DBTimeEntries> entries_per_day, boolean is_holiday) {
        return 0;
    }

    public long calcExtraTimeForMonth(long regular_work_time, long real_work_time)
    {
        if( real_work_time > regular_work_time)
        {
            return real_work_time - regular_work_time;
        }

        return 0;
    }

    public long calcOverTimeForDay(Collection<DBTimeEntries> entries_per_day, boolean holiday) {
        return 0;
    }

    public double getOverTimeFactor() {
        return 0;
    }

    public double getHours4Day(LocalDate dm, Holidays holidays)
    {
        return hours4day.getHours4Day(dm, holidays);
    }

    public void everyDayHook(LocalDate today, HMSTime flextime, HMSTime flextime_no_extra, HMSTime overtime, HMSTime overtime_hours) {
        
    }

}

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
public class None implements OvertimeInterface
{
    Hours4DayInterface hours_per_day;

    public None( DBUserPerMonth upm )
    {
        hours_per_day = new WorkTimeNoWeekendNoHolidays(upm);
    }


    public long calcExtraTimeForDay(Collection<DBTimeEntries> entries_per_day, boolean is_holiday) {
        return 0;
    }

    public long calcExtraTimeForMonth(long regular_work_time, long real_work_time) {
        return 0;
    }

    public long calcOverTimeForDay(Collection<DBTimeEntries> entries_per_day, boolean holiday) {
        return 0;
    }

    public double getOverTimeFactor() {
        return 0;
    }

    public double getHours4Day(LocalDate dm, Holidays holidays) {
        return hours_per_day.getHours4Day(dm, holidays);
    }

    public void everyDayHook(LocalDate today, HMSTime flextime, HMSTime flextime_no_extra, HMSTime overtime, HMSTime overtime_hours) {
        
    }

    public long calcMoreTime4Day(Collection<DBTimeEntries> entries_per_day, boolean holiday) {
        return 0;
    }

}

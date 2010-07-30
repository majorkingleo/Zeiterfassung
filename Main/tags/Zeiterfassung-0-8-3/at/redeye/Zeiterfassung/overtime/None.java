/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Zeiterfassung.overtime;

import at.redeye.Zeiterfassung.bindtypes.DBTimeEntries;
import java.util.Collection;

/**
 *
 * @author martin
 */
public class None implements OvertimeInterface
{

    public long calcExtraTimeForDay(Collection<DBTimeEntries> entries_per_day, boolean is_holiday) {
        return 0;
    }

    public long calcExtraTimeForMonth(long regular_work_time, long real_work_time) {
        return 0;
    }

}

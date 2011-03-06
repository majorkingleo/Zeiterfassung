/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Zeiterfassung.overtime;

import at.redeye.FrameWork.utilities.calendar.Holidays;
import org.joda.time.LocalDate;

/**
 *
 * @author martin
 */
public interface Hours4DayInterface
{
    double getHours4Day(LocalDate dm, Holidays holidays);
}

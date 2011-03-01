/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Zeiterfassung;

import org.joda.time.DateMidnight;

/**
 *
 * @author martin
 */
public interface CalcMonthStuffDataInterface
{
    /**     
     * @return year for this calculation
     */
    int getYear();

    /**
     * @return month for this calculation
     */
    int getMonth();

    /**
     * @return userid this calculation shoudl be made for
     */
    int getUserId();

    /**
     * @param day
     * @return true if this day is an holiday
     */
    boolean isHoliday( DateMidnight day );
}

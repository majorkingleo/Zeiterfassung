/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Zeiterfassung.test.db;

import at.redeye.FrameWork.utilities.calendar.AustrianHolidays;
import at.redeye.FrameWork.utilities.calendar.Holidays;
import at.redeye.FrameWork.utilities.calendar.Holidays.HolidayInfo;
import at.redeye.UserManagement.bindtypes.DBPb;
import at.redeye.Zeiterfassung.CalcMonthStuffDataInterface;
import java.util.Date;
import org.joda.time.DateMidnight;
import org.joda.time.LocalDate;

/**
 *
 * @author martin
 */
public class CalcMonthStuffInputGenerator implements CalcMonthStuffDataInterface
{
    int year;
    int month;
    DBPb user;
    Holidays holidays;
    
    public CalcMonthStuffInputGenerator( int year, int month, DBPb user )
    {
        this.year = year;
        this.month = month;
        this.user = user;
        holidays = new AustrianHolidays();
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getUserId() {
        return user.getUserId();
    }

    public boolean isHoliday(Date day) {
        return isHoliday(new LocalDate(day));
    }

    public boolean isHoliday(DateMidnight day) {
        return isHoliday(day.toLocalDate());
    }

    public boolean isHoliday(LocalDate day) {
        if (holidays == null) {
            return false;
        }

        HolidayInfo hi = holidays.getHolidayForDay(day);

        if (hi == null) {
            return false;
        }

        return hi.official_holiday;
    }

    public Holidays getHolidays() {
        return holidays;
    }


}

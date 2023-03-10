/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Zeiterfassung.overtime;

import at.redeye.FrameWork.utilities.HMSTime;
import at.redeye.FrameWork.utilities.calendar.Holidays;
import at.redeye.Zeiterfassung.bindtypes.DBTimeEntries;
import at.redeye.Zeiterfassung.bindtypes.DBUserPerMonth;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import org.apache.log4j.Logger;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

/**
 *
 * @author martin => BJV System
 *
 * Wir haben auch ein Überstunden-System, dh konkret eine Stunde zählt dann 1,5 Stunden.
 * Das sind alle Stunden zwischen 20:00 und 6:30, wenn über 9 Stunden an einem Tag
 * gearbeitet wird und Samstag bzw. Sonntag.
 *
 * Überstunden sind:
 *   Immer die 10. Arbeitsstunde am Tag; d.h. nach 9 geleisteten Stunden sind alle Stunden Überstunden;
 *   z.B. Arbeitszeit 9.00 bis 22.00 = 9 Stunden + 6 Überstunden
 *
 *   Von 20:00 bis 6:30 sind alle Stunden Überstunden; z.B. Arbeitszeit 15.00 bis 24:00 = 5 Stunden + 6 Überstunden
 *
 *   Am Ende des Quartals d.h. mit Gültigkeit ab 1.4., 1.6., 1.9., 1.1. ist folgendes geregelt: übersteigt die
 *   Summe der mehr+Überstunden insgesamt eine Wochenarbeitszeit, so ist diese Differenz auch als Überstunden
 *   zu werten; bei 38,5 Stunden W-Arbeitszeit also z.B.: Ende 30. März: 33 Mehrstunden
 *   plus 15 Ü Stunden = 48 abzüglich 38, 5 = 9,5 *0,5=4,75;
 *   ergibt für die Arbeitnehmerin mit 1. April 33 Mehrstunden plus 19:45 Ü-Stunden
 */
public class Schema_1 implements OvertimeInterface
{
    private static final Logger logger = Logger.getLogger(Schema_1.class);

    private static final double OVERTIME_FACTOR = 1.5;
    private static final double EXTRATIME_FACTOR = 0.5;

    static class Times
    {
        long extra_time = 0;
        long over_time = 0;
        
        /**
         * Mehrstunden. Zeit am Tag die mehr als gefordert gearbeitet wurde.
         */
        long more_time = 0;
    }

    boolean is_saturday = false;
    boolean is_sunday = false;

    Hours4DayInterface hours4day;
    DBUserPerMonth upm;

    public Schema_1( DBUserPerMonth upm )
    {
        this.upm = upm;

        if( upm.hours_per_week.getValue() >= 38.5 )
            hours4day = new ShortFriday(8.0,upm.hours_per_week.getValue()-8.0*4);
        else
            hours4day = new ShortFriday(7.0,upm.hours_per_week.getValue()-7.0*4);
    }

    Date calcMorning( Date date )
    {
        GregorianCalendar cal = new GregorianCalendar();

        cal.setTime(date);

        cal.set(Calendar.HOUR_OF_DAY, 6);
        cal.set(Calendar.MINUTE, 30);
        cal.set(Calendar.SECOND, 0);

        if( cal.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.SATURDAY )
        {
            is_saturday = true;
        }
        else
        {
            is_saturday = false;

            if( cal.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.SUNDAY )
                is_sunday = true;
            else
                is_sunday = false;
        }

        return cal.getTime();
    }

    Date calcEvening( Date date )
    {
        GregorianCalendar cal = new GregorianCalendar();

        cal.setTime(date);

        cal.set(Calendar.HOUR_OF_DAY, 20);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        return cal.getTime();
    }

    public long calcExtraTimeForDay(Collection<DBTimeEntries> entries_per_day, boolean is_holiday )
    {
        Times times = calcTimesForDay(entries_per_day, is_holiday);

        return times.extra_time;
    }

    public long calcMoreTime4Day(Collection<DBTimeEntries> entries_per_day, boolean is_holiday) {
        Times times = calcTimesForDay(entries_per_day, is_holiday);

        return times.more_time;
    }

    Times calcTimesForDay(Collection<DBTimeEntries> entries_per_day, boolean is_holiday )
    {
        long duration_per_day = 0;
        long duration_before_morning=0;
        long duration_after_evening=0;
        long duration_extra = 0;

        Times times = new Times();

        if( entries_per_day.isEmpty() )
            return times;

        Date d_morning = null;
        Date d_evening = null;

        for( DBTimeEntries e : entries_per_day )
        {
            duration_per_day += e.calcDuration();

            Date d_from = e.from.getValue();
            Date d_to = e.to.getValue();

            if( d_morning == null )
                d_morning = calcMorning( d_from );

            if( d_from.before(d_morning) && ( d_to.before(d_morning) || d_to.equals(d_morning)))
            {
                duration_before_morning += e.calcDuration();
            }
            else if( d_from.before(d_morning) && d_to.after(d_morning) )
            {
                duration_before_morning += d_morning.getTime() - d_from.getTime();
            }

            if( d_evening == null )
                d_evening = calcEvening( d_from );


            if( d_from.after(d_evening) || d_from.equals(d_evening) )
            {
                duration_after_evening += e.calcDuration();
            }
            else if( d_from.before(d_evening) && d_to.after(d_evening))
            {
                duration_after_evening += d_to.getTime() - d_evening.getTime();
            }            
        }

        if( duration_per_day > 1000*60*60*9 )
        {
            duration_extra = duration_per_day  - 1000*60*60*9 - duration_after_evening - duration_before_morning;
        }

        if( is_saturday || is_sunday || is_holiday )
        {
            times.extra_time = (long)(duration_per_day * EXTRATIME_FACTOR);
            times.over_time = duration_per_day;
        } else {
            times.extra_time = (long)((duration_after_evening + duration_before_morning + duration_extra ) * EXTRATIME_FACTOR);
            times.over_time = duration_after_evening + duration_before_morning + duration_extra;


            double h4d = hours4day.getHours4Day(new LocalDate(d_morning), null);

            times.more_time = duration_per_day - times.over_time - (long)(h4d * 60 * 60 * 1000.0);

            if( times.more_time < 0 )
                times.more_time = 0;
        }

        return times;
    }

    public long calcExtraTimeForMonth(long regular_work_time, long real_work_time)
    {
        return 0;
    }

    public long calcOverTimeForDay(Collection<DBTimeEntries> entries_per_day, boolean is_holiday)
    {
        Times times = calcTimesForDay(entries_per_day, is_holiday);

        return times.over_time;
    }

    public double getOverTimeFactor() {
        return OVERTIME_FACTOR;
    }

    public double getHours4Day(LocalDate dm, Holidays holidays) {
        return hours4day.getHours4Day(dm, holidays);
    }

    static private boolean isEndOfQuater(LocalDate date)
    {
        switch( date.getMonthOfYear() )
        {
            case DateTimeConstants.MARCH:
            case DateTimeConstants.MAY:
            case DateTimeConstants.AUGUST:
            case DateTimeConstants.DECEMBER:
                
                if( date.getDayOfMonth() == 31 )
                    return true;
                break;
        }

        return false;
    }

    /*
     *   Am Ende des Quartals d.h. mit Gültigkeit ab 1.4., 1.6., 1.9., 1.1. ist folgendes geregelt: übersteigt die
     *   Summe der mehr+Überstunden insgesamt eine Wochenarbeitszeit, so ist diese Differenz auch als Überstunden
     *   zu werten; bei 38,5 Stunden W-Arbeitszeit also z.B.: Ende 30. März: 33 Mehrstunden
     *   plus 15 Ü Stunden = 48 abzüglich 38, 5 = 9,5 *0,5=4,75;
     *   ergibt für die Arbeitnehmerin mit 1. April 33 Mehrstunden plus 19:45 Ü-Stunden
     */
    public void everyDayHook(LocalDate today, HMSTime flextime, HMSTime flextime_no_extra, HMSTime overtime, HMSTime overtime_hours)
    {
        if( !isEndOfQuater(today) )
            return;
        
        long diff = flextime.getMillis() + overtime.getMillis();
        long hours_per_week = (long)(upm.hours_per_week.getValue() * 60 * 60 * 1000);

        if( diff > hours_per_week )
        {
            HMSTime hms_diff = new HMSTime(diff);
            HMSTime hms_hours_per_week = new HMSTime(hours_per_week);

            logger.info("Quartalszuschlag; Überstunden (" + overtime.toString("HH:mm") + ")" +
                " + Mehrstunden (" + flextime.toString("HH:mm") + "): " + hms_diff + " Arbeitsstunden pro Woche: " + hms_hours_per_week );

            diff -= hours_per_week;
            overtime.addMillis((long)(diff * getOverTimeFactor()));
            overtime_hours.addMillis(diff);
            flextime.addMillis((long)(diff * getOverTimeFactor()));
            flextime_no_extra.addMillis(diff);
        }          
         
    }

}

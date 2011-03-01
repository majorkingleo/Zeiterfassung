/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Zeiterfassung.overtime;

import at.redeye.Zeiterfassung.bindtypes.DBTimeEntries;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

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
    boolean is_saturday = false;
    boolean is_sunday = false;

    private Date calcMorning( Date date )
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

    private Date calcEvening( Date date )
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
        long duration_per_day = 0;
        long duration_before_morning=0;
        long duration_after_evening=0;
        long duration_extra = 0;

        if( entries_per_day.isEmpty() )
            return 0;

        Date d_morning = null;
        Date d_evening = null;

        for( DBTimeEntries e : entries_per_day )
        {
            duration_per_day += e.calcDuration();

            Date d_from = e.from.getValue();
            Date d_to = e.to.getValue();

            if( d_morning == null )
                d_morning = calcMorning( d_from );

            if( d_from.before(d_morning) && d_to.before(d_morning) )
            {
                duration_before_morning += e.calcDuration();
            }
            else if( d_from.before(d_morning) && d_to.after(d_morning) )
            {
                duration_before_morning += d_morning.getTime() - d_from.getTime();
            }

            if( d_evening == null )
                d_evening = calcEvening( d_from );


            if( d_from.after(d_evening) )
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
            return (long) (duration_per_day * 0.5);
        }

        return (long) ((duration_after_evening + duration_before_morning + duration_extra ) * 0.5);
    }

    public long calcExtraTimeForMonth(long regular_work_time, long real_work_time)
    {
        return 0;
    }

    public long calcOverTimeForDay(List<DBTimeEntries> entries_per_day, boolean holiday) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}

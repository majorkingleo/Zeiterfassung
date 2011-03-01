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
public interface OvertimeInterface
{

    /**
     * Soll die mehrabeitszeit für einen bestimmten Tag berechnen. Sprich wenn überstunden
     * mit 1.5 abgegolten werden und an dem Tag eine Überstunde angefallen ist, dann soll
     * diese Funktion 0.5 Stunden zurückgeben.
     * @return Die Zusätzliche Zeit in Millisekunden
     */
    public long calcExtraTimeForDay( Collection<DBTimeEntries> entries_per_day, boolean is_holiday );
    public long calcExtraTimeForMonth( long regular_work_time, long real_work_time );

    /**
     * Berechnet die anzahl der Stunden, die als Überstunden gewertet werden.
     * Werden überstunden zb mit 1.5 Stunden ZA abgegolten, so ist dieser Faktor
     * hier noch nicht eingeflossen.
     * @param entries_per_day
     * @param holiday
     * @return die Anzhal der Stunden, die als Überstunden gelten in millisekunden.
     */
    public long calcOverTimeForDay(Collection<DBTimeEntries> entries_per_day, boolean holiday);

    /**     
     * @return Den Faktor mit dem die Überstunden multipliziert werden um die ZA Stunden 
     * zu erhalten.
     */
    public double getOverTimeFactor();
}

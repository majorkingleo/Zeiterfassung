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
     * Sol die mehrabeitszeit für einen bestimmten Tag berechnen. Sprich wenn überstunden
     * mit 1.5 abgegolten werden und an dem Tag eine Überstunde angefallen ist, dann soll
     * diese Funktion 0.5 Stunden zurückgeben.
     * @return Die Zusätzliche Zeit in Millisekunden
     */
    public long calcExtraTimeForDay( Collection<DBTimeEntries> entries_per_day, boolean is_holiday );
    public long calcExtraTimeForMonth( long regular_work_time, long real_work_time );

}

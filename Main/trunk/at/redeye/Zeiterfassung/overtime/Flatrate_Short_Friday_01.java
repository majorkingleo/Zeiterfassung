/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Zeiterfassung.overtime;

import at.redeye.Zeiterfassung.bindtypes.DBUserPerMonth;

/**
 *
 * @author martin
 */
public class Flatrate_Short_Friday_01 extends FlatRate
{
    public Flatrate_Short_Friday_01( DBUserPerMonth upm )
    {
        super( upm );

        if( upm.hours_per_week.getValue() >= 38.5 )
            hours4day = new ShortFriday(8.0,upm.hours_per_week.getValue()-8.0*4);
        else
            hours4day = new ShortFriday(7.0,upm.hours_per_week.getValue()-7.0*4);
    }
}

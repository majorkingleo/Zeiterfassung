/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Zeiterfassung;

import at.redeye.FrameWork.base.bindtypes.DBValue;
import java.util.Vector;

/**
 *
 * @author martin
 */
public class AutoCompleteInfo
{
    private Vector<Object> values = new Vector<Object>();

    public AutoCompleteInfo()
    {

    }

    public void add(DBValue val)
    {
        values.add(val);
    }

    public Vector<Object> get()
    {
        return values;
    }
}

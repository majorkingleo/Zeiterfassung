/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Zeiterfassung.bindtypes;

import at.redeye.FrameWork.base.transaction.Transaction;

/**
 *
 * @author martin
 */
public class SubProjectQuery extends NameNotLockedQuery
{
    public SubProjectQuery( Transaction trans, boolean with_null_entry )
    {
        super( trans, new DBSubProjects() );

        if( with_null_entry )
            setNullEntryAsDefault();
    }

    public SubProjectQuery( Transaction trans )
    {
        super( trans, new DBSubProjects() );
    }
}

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
public class CustomerQuery extends NameNotLockedQuery
{
    public CustomerQuery( Transaction trans )
    {
        super(trans, new DBCustomers() );
    }

    public CustomerQuery( Transaction trans, boolean with_null_entry )
    {
        super(trans, new DBCustomers() );

        if( with_null_entry )
            setNullEntryAsDefault();
    }
}

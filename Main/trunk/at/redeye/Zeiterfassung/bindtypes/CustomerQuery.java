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
        this(trans, new DBCustomers(), false);
    }

    public CustomerQuery( Transaction trans, boolean with_null_entry )
    {
        this(trans, new DBCustomers(), with_null_entry);
    }

    private CustomerQuery( Transaction trans, DBCustomers cust, boolean with_null_entry )
    {
        super( trans, cust );

        if( with_null_entry )
            setNullEntryAsDefault();

        setOrderBySql(" order by " + trans.markColumn(cust.name));
    }
}

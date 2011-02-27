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
        this( trans, new DBSubProjects(), with_null_entry );
    }

    public SubProjectQuery( Transaction trans )
    {
        this( trans, new DBSubProjects(), false );
    }

    private SubProjectQuery( Transaction trans, DBSubProjects proj, boolean with_null_entry )
    {
        super( trans, proj );

        if( with_null_entry )
            setNullEntryAsDefault();

        setOrderBySql(" order by " + trans.markColumn(proj.name));
    }
}

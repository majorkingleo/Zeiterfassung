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
public class ProjectQuery extends NameNotLockedQuery
{
    public ProjectQuery( Transaction trans )
    {
        this( trans, new DBProjects(), false );
    }

    public ProjectQuery( Transaction trans, boolean with_null_entry )
    {        
        this( trans, new DBProjects(), with_null_entry );
    }

    private ProjectQuery( Transaction trans, DBProjects proj, boolean with_null_entry )
    {
        super( trans, proj );

        if( with_null_entry )
            setNullEntryAsDefault();

        setOrderBySql(" order by " + trans.markColumn(proj.name));
    }
}

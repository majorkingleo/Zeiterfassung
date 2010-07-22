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
        super( trans, new DBProjects() );
    }

    public ProjectQuery( Transaction trans, boolean with_null_entry )
    {
        super( trans, new DBProjects() );

        if( with_null_entry )
            setNullEntryAsDefault();
    }
}

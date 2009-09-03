/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Zeiterfassung.bindtypes;

import at.redeye.FrameWork.base.transaction.Transaction;
import java.util.Date;

import at.redeye.FrameWork.base.bindtypes.DBDateTime;
import at.redeye.FrameWork.base.bindtypes.DBHistory;
import at.redeye.FrameWork.base.bindtypes.DBInteger;
import at.redeye.FrameWork.base.bindtypes.DBSqlAsInteger;
import at.redeye.FrameWork.base.bindtypes.DBString;
import at.redeye.FrameWork.base.bindtypes.DBStrukt;
import at.redeye.FrameWork.base.bindtypes.DBValue;

/**
 *
 * @author martin
 */
public class DBTimeEntries extends DBStrukt {
    
    private static final long serialVersionUID = 1L;

    public DBInteger  id = new DBInteger("Id");
    public DBInteger  user = new DBInteger("User", "Benutzer");
    public DBDateTime from = new DBDateTime( "From", "Von");
    public DBDateTime to = new DBDateTime( "To", "Bis");
    public DBValue    jobtype = new DBInteger( "JobType", "Tätigkeit" );
    public DBString   comment = new DBString( "Comment", "Kommentar", 200 );
    public DBInteger  locked = new DBInteger( "locked", "gesperrt" );
    public DBValue    customer = new DBInteger( "customer", "Kunde" );
    public DBValue    project = new DBInteger( "Project", "Projekt" );
    public DBValue    sub_project = new DBInteger( "SubProject", "Unterprojekt" );
    public DBHistory  hist = new DBHistory( "hist" );

    public static final String table_name = "TIMEENTRIES";
    
    void add_all()
    {
        add( id );
        add( user );
        add( from );
        add( to );
        add( jobtype );
        add( comment );
        add( locked );
        add( customer, 2 );
        add( project );
        add( sub_project );
        add( hist );
        
        id.setAsPrimaryKey();        
        user.setShouldHaveIndex();
        from.setShouldHaveIndex();

        setVersion(2);
    }

    void add_all( DBSqlAsInteger.SqlQuery jobtype_query,
                  DBSqlAsInteger.SqlQuery customer_query,
                  DBSqlAsInteger.SqlQuery project_query,
                  DBSqlAsInteger.SqlQuery subproject_query )
    {
        jobtype     = new DBSqlAsInteger( "JobType", "Tätigkeit", jobtype_query );
        customer    = new DBSqlAsInteger( "customer", "Kunde", customer_query );
        project     = new DBSqlAsInteger( "project", "Projekt", project_query );
        sub_project = new DBSqlAsInteger( "SubProject", "Unterprojekt", subproject_query );

        add_all();
    }
    
    


    public DBTimeEntries(Transaction transaction, boolean with_null_entry )
    {
        super(table_name);

        add_all( new JobTypeQuery(transaction),
                 new CustomerQuery(transaction,with_null_entry),
                 new ProjectQuery(transaction,with_null_entry),
                 new SubProjectQuery(transaction,with_null_entry)
                );
    }


    public DBTimeEntries(Transaction transaction )
    {
        super(table_name);

        add_all( new JobTypeQuery(transaction),
                 new CustomerQuery(transaction),
                 new ProjectQuery(transaction),
                 new SubProjectQuery(transaction)
                );
    }

    public DBTimeEntries( DBSqlAsInteger.SqlQuery jobtype_query,
                          DBSqlAsInteger.SqlQuery customer_query,  
                          DBSqlAsInteger.SqlQuery project_query, 
                          DBSqlAsInteger.SqlQuery subproject_query )
    {
        super(table_name);
               
        add_all(jobtype_query, customer_query, project_query, subproject_query);
    }
    
    public DBTimeEntries()
    {
        super(table_name); 
        
        add_all();
    }
    
    @Override
    public DBStrukt getNewOne() {
        
        if( jobtype instanceof DBInteger )
            return new DBTimeEntries();
        else
            return new DBTimeEntries( 
                    ((DBSqlAsInteger)jobtype).query,
                    ((DBSqlAsInteger)customer).query,
                    ((DBSqlAsInteger)project).query,
                    ((DBSqlAsInteger)sub_project).query);
    }
    
    /*
     * return the duraction in millis between from an to
     */ 
    public long calcDuration()
    {
          Date time_from = (Date)from.getValue();
          Date time_to = (Date)to.getValue();
          
          return (time_to.getTime() - time_from.getTime());
    }

}

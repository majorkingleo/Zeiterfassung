/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Zeiterfassung.bindtypes;

import at.redeye.FrameWork.base.bindtypes.DBDateTime;
import at.redeye.FrameWork.base.bindtypes.DBHistory;
import at.redeye.FrameWork.base.bindtypes.DBInteger;
import at.redeye.FrameWork.base.bindtypes.DBSqlAsInteger;
import at.redeye.FrameWork.base.bindtypes.DBStrukt;
import at.redeye.FrameWork.base.bindtypes.DBValue;

/**
 *
 * @author martin
 */
public class DBMonthBlocks extends DBStrukt 
{
    public DBInteger       id             = new DBInteger("id" );
    public DBValue         user           = new DBInteger( "user", "Benutzer" );    
    public DBDateTime      from           = new DBDateTime( "from", "Editierbar ab" );
    public DBHistory       hist           = new DBHistory( "hist" );     
    
    void add_all()
    {
        add( id );
        add( user );        
        add( from );
        add( hist );
        
        id.setAsPrimaryKey();
    }
        
    public DBMonthBlocks( DBSqlAsInteger.SqlQuery query_user )
    {
        super( "MONTHBLOCKS" );                
        
        user = new DBSqlAsInteger( "user", "Benutzer", query_user );
        
        add_all();
    }
    
    public DBMonthBlocks()
    {
        super( "MONTHBLOCKS" );                             
        
        add_all();
    }
        
    @Override
    public DBStrukt getNewOne() {
        
        if( user instanceof DBInteger )        
            return new DBMonthBlocks();
        else
            return new DBMonthBlocks( ((DBSqlAsInteger)user).query );
    }
}

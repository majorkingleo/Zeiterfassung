/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Zeiterfassung.bindtypes;

import at.redeye.FrameWork.base.bindtypes.DBDateTime;
import at.redeye.FrameWork.base.bindtypes.DBDouble;
import at.redeye.FrameWork.base.bindtypes.DBFlagJaNein;
import at.redeye.FrameWork.base.bindtypes.DBHistory;
import at.redeye.FrameWork.base.bindtypes.DBInteger;
import at.redeye.FrameWork.base.bindtypes.DBSqlAsInteger;
import at.redeye.FrameWork.base.bindtypes.DBStrukt;
import at.redeye.FrameWork.base.bindtypes.DBValue;

/**
 *
 * @author martin
 */
public class DBUserPerMonth extends DBStrukt 
{
    public DBInteger       id             = new DBInteger("id" );
    public DBValue         user           = new DBInteger( "user", "Benutzer" );
    public DBDateTime      from           = new DBDateTime( "from", "Gültig von" );
    public DBDateTime      to             = new DBDateTime( "to", "Gültig bis" );
    public DBFlagJaNein    locked         = new DBFlagJaNein( "locked", "gesperrt" );
    public DBDouble        usage          = new DBDouble( "usage", "Arbeitszeitanteil" );
    public DBDouble        hours_per_week = new DBDouble( "hours_per_week", "Arbeitsstunden pro Woche" );
    public DBDouble        days_per_week  = new DBDouble( "days_per_week", "Arbeitstage pro Woche" );
    public DBDouble        hours_holidays = new DBDouble( "hours_holidays", "Urlaubsstunden");
    public DBDouble        days_holidays  = new DBDouble( "days_holidays", "Urlaub in Tagen");
    public DBDouble        hours_overtime = new DBDouble( "hours_overtime", "Überstunden");
    public DBHistory       hist           = new DBHistory( "hist" );    
    
    void add_all()
    {
        add( id );
        add( user );
        add( from );
        add( to );
        add( locked );
        add( usage );
        add( hours_per_week );        
        add( days_per_week );
        add( hours_holidays );
        add( days_holidays, 2 );
        add( hours_overtime );
        add( hist );
        
        id.setAsPrimaryKey();
        
        setVersion(2);
    }
    
    public DBUserPerMonth( DBSqlAsInteger.SqlQuery query_user )
    {
        super( "USERPERMONTH" );                
        
        user = new DBSqlAsInteger( "user", "Benutzer", query_user );
        
        add_all();
    }
    
    public DBUserPerMonth()
    {
        super( "USERPERMONTH" );                             
        
        add_all();
    }
    
    @Override
    public DBStrukt getNewOne() {
        
        if( user instanceof DBInteger )        
            return new DBUserPerMonth();
        else
            return new DBUserPerMonth( ((DBSqlAsInteger)user).query );
    }

}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Zeiterfassung.bindtypes;

import at.redeye.FrameWork.base.bindtypes.CurrencyEnumHandler;
import at.redeye.FrameWork.base.bindtypes.DBDouble;
import at.redeye.FrameWork.base.bindtypes.DBEnum;
import at.redeye.FrameWork.base.bindtypes.DBFlagJaNein;
import at.redeye.FrameWork.base.bindtypes.DBHistory;
import at.redeye.FrameWork.base.bindtypes.DBInteger;
import at.redeye.FrameWork.base.bindtypes.DBString;
import at.redeye.FrameWork.base.bindtypes.DBStrukt;

/**
 *
 * @author martin
 */
public class DBCustomers extends DBStrukt implements NameIdLockedInterface
{
    public DBInteger    id          = new DBInteger("id");
    public DBString     name        = new DBString( "title", "Name", 300 );
    public DBString     comment     = new DBString( "comment", "Kommentar", 300 );    
    public DBDouble     hourly_rate = new DBDouble("hourly_rate", "Stundensatz");
    public DBEnum       currency    = new DBEnum( "currency", "Währung", new CurrencyEnumHandler());
    public DBFlagJaNein locked      = new DBFlagJaNein("locked","Gesperrt");
    public DBHistory    hist        = new DBHistory( "hist" );

    public DBCustomers()
    {
        super( "CUSTOMERS" );

        add( id );
        add( name );
        add( comment );        
        add( hourly_rate );
        add( currency );
        add( locked );
        add( hist );

        id.setAsPrimaryKey();
    }

    @Override
    public DBStrukt getNewOne()
    {
        return new DBCustomers();
    }

    public String getNameName() {
        return name.getName();
    }

    public String getNameValue() {
        return name.getValue();
    }

    public String getIdName() {
        return id.getName();
    }

    public Integer getIdValue() {
        return id.getValue();
    }

    public String getLockedName() {
        return locked.getName();
    }


}

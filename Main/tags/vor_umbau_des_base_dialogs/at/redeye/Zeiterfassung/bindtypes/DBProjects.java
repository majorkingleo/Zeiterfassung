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
import at.redeye.FrameWork.base.bindtypes.DBSqlAsInteger;
import at.redeye.FrameWork.base.bindtypes.DBString;
import at.redeye.FrameWork.base.bindtypes.DBStrukt;
import at.redeye.FrameWork.base.bindtypes.DBValue;

/**
 *
 * @author martin
 */
public class DBProjects extends DBStrukt  implements NameIdLockedInterface {

    public DBInteger     id                    = new DBInteger("id");
    public DBValue       customer              = new DBInteger("customer", "Kunde");
    public DBString      name                  = new DBString( "name", "Name", 100 );
    public DBFlagJaNein  locked                = new DBFlagJaNein("locked","Gesperrt");
    public DBDouble      hourly_rate           = new DBDouble("hourly_rate", "Stundensatz");
    public DBEnum        currency              = new DBEnum( "currency", "Währung", new CurrencyEnumHandler());
    public DBFlagJaNein  autocreate_subproject = new DBFlagJaNein("autocreate","Auto - Unterprojekt");
    public DBHistory     hist                  = new DBHistory("hist");

    public static String table_name = "PROJECTS";

    public DBProjects( DBSqlAsInteger.SqlQuery query_customer )
    {
        super(table_name);

        customer = new DBSqlAsInteger("customer", "Kunde", query_customer);

        add_all();
    }


    public DBProjects()
    {
        super(table_name);

        add_all();
    }

    private void add_all() {
        add(id);
        add(customer);
        add(name);
        add(locked);
        add(hourly_rate);
        add(currency);
        add(autocreate_subproject);
        add(hist);

        id.setAsPrimaryKey();
    }

    @Override
    public DBStrukt getNewOne() {

        if (customer instanceof DBInteger) {
            return new DBProjects();
        } else {
            return new DBProjects(((DBSqlAsInteger) customer).query);
        }
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

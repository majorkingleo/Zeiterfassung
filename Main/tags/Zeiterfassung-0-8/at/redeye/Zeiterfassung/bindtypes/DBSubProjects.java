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
public class DBSubProjects extends DBStrukt implements NameIdLockedInterface {

    public DBInteger     id                    = new DBInteger("id");
    public DBValue       project               = new DBInteger("project", "Projekt");
    public DBString      name                  = new DBString( "name", "Name", 100 );    
    public DBDouble      hourly_rate           = new DBDouble("hourly_rate", "Stundensatz");
    public DBEnum        currency              = new DBEnum( "currency", "Währung", new CurrencyEnumHandler());
    public DBFlagJaNein  locked                = new DBFlagJaNein("locked","Gesperrt");
    public DBHistory     hist                  = new DBHistory("hist");

    public static String table_name = "SUBPROJECTS";

    public DBSubProjects( DBSqlAsInteger.SqlQuery query_project )
    {
        super(table_name);

        project = new DBSqlAsInteger("project", "Projekt", query_project);

        add_all();
    }


    public DBSubProjects()
    {
        super(table_name);

        add_all();
    }

    private void add_all() {
        add(id);
        add(project);
        add(name);        
        add(hourly_rate);
        add(currency);
        add(locked);
        add(hist);

        id.setAsPrimaryKey();
    }

    @Override
    public DBStrukt getNewOne() {

        if (project instanceof DBInteger) {
            return new DBSubProjects();
        } else {
            return new DBSubProjects(((DBSqlAsInteger) project).query);
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

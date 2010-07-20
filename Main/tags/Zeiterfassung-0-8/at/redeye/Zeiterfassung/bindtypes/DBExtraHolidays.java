/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Zeiterfassung.bindtypes;

import at.redeye.FrameWork.base.bindtypes.DBDateTime;
import at.redeye.FrameWork.base.bindtypes.DBFlagJaNein;
import at.redeye.FrameWork.base.bindtypes.DBHistory;
import at.redeye.FrameWork.base.bindtypes.DBInteger;
import at.redeye.FrameWork.base.bindtypes.DBString;
import at.redeye.FrameWork.base.bindtypes.DBStrukt;

/**
 *
 * @author martin
 */
public class DBExtraHolidays extends DBStrukt
{
    public DBInteger    id   = new DBInteger("id");
    public DBDateTime   date = new DBDateTime("date", "Datum");
    public DBString     title = new DBString("title","Titel",50);
    public DBFlagJaNein official_holiday = new DBFlagJaNein("official_holiday", "Gesetzlicher Feiertag");
    public DBHistory    hist = new DBHistory("hist");
    
    public DBExtraHolidays()
    {
        super("EXTRAHOLIDAYS");
        
        add(id);
        add(date);
        add(title);
        add(official_holiday);
        add(hist);
        
        id.setAsPrimaryKey();
    }
    
    @Override
    public DBStrukt getNewOne() {
        return new DBExtraHolidays();
    }

}

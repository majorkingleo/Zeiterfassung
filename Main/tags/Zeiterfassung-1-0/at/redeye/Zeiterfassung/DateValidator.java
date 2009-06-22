/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Zeiterfassung;

import java.text.SimpleDateFormat;
import java.util.Date;

import at.redeye.FrameWork.base.bindtypes.DBDateTime;
import at.redeye.FrameWork.base.bindtypes.DBValue;
import at.redeye.FrameWork.base.tablemanipulator.TableValidator;
import at.redeye.SqlDBInterface.SqlDBIO.MOMMStmtExecInterface;

/**
 *
 * @author martin
 */
public class DateValidator extends TableValidator {

    @Override
    public String formatData(Object data) {
        // System.out.println("HERE");
        DBDateTime val = (DBDateTime) data;

        SimpleDateFormat formater_time = new SimpleDateFormat(MOMMStmtExecInterface.SQLIF_STD_DATE_FORMAT);

        Date time = (Date) val.getValue();

        String res = formater_time.format(time.getTime());

        return res;
    }

    @Override
    public boolean wantDoLoadSelf() {
        return true;
    }

    @Override
    public boolean loadToValue(DBValue val, String s) {
       
    	Date time = (Date) val.getValue();
        //Add dummy time for parsing
        s += " 00:00:00";
        
        val.loadFromString(s);
        return true;
    }

    @Override
    public boolean acceptData(String data) {
        if (data.matches("[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]") == true) {
            return true;
        }
        return false;
    }
}

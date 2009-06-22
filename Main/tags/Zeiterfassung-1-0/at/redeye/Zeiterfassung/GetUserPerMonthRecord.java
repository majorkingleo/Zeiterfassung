/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Zeiterfassung;

import java.sql.SQLException;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.joda.time.DateMidnight;

import at.redeye.FrameWork.base.bindtypes.DBStrukt;
import at.redeye.FrameWork.base.transaction.Transaction;
import at.redeye.SqlDBInterface.SqlDBIO.impl.TableBindingNotRegisteredException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.UnsupportedDBDataTypeException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.WrongBindFileFormatException;
import at.redeye.Zeiterfassung.bindtypes.DBUserPerMonth;

/**
 *
 * @author martin
 */
public class GetUserPerMonthRecord {

    public static DBUserPerMonth getValidRecordForMonth( Transaction trans, int userId, int year, int month ) throws SQLException, SQLException, TableBindingNotRegisteredException, UnsupportedDBDataTypeException, WrongBindFileFormatException, DuplicateRecordException
    {
        
        DBUserPerMonth upm = new DBUserPerMonth();
        
        DateMidnight dm_from = new DateMidnight( year, month, 1 );
        
        Vector<DBStrukt> res = trans.fetchTable(new DBUserPerMonth(),
                "where " 
                    + trans.markColumn(upm.user) + "='" + userId + "'" +
                    " and "
                    + trans.markColumn(upm.locked) + "='NEIN'" + 
                    " and "
                    + trans.getPeriodStmt(upm.from, upm.to, dm_from)                    
                );
        
        if( res.size() == 0 )
        {
            // gibt es einen 1.1.1970 Eintrag?
            
            dm_from = new DateMidnight( 0L );
            
            res = trans.fetchTable(new DBUserPerMonth(),
                "where " 
                    + trans.markColumn(upm.user) + "='" + userId + "'" +
                    " and "
                    + trans.markColumn(upm.locked) + "='NEIN'" + 
                    " and "
                    + trans.getPeriodStmt(upm.from, upm.to, dm_from)                    
                );    
        }
        
        if( res.size() != 1 )
        {
            Logger logger = Logger.getLogger("GetUserPerMonthRecord");
            
            if( res.size() == 0 )
            {
                logger.warn("Keine Eintrag für den Zeitraum gefunden"); 
                return null;
            }
            else
            {
                String message = 
                          "Mehr als einen Eintrag den Benutzer " 
                        + userId + " gefunden "
                        + "( " + year + "-" + month + "-" + "01 )";
                        
                            
                logger.error(message);  
                logger.error("Liste der Einträge:");  
                
                for( int i = 0; i < res.size(); i++ )
                {
                    DBUserPerMonth rec = (DBUserPerMonth) res.get(0);
                    
                    logger.error( (i+1) + " Id: " + rec.id + " from: " + rec.from.getDateStr() + " to: " + rec.to.getDateStr() );
                }
                
                throw new DuplicateRecordException(message);
            }
        }
        
        return (DBUserPerMonth) res.get(0);
    }
    
}

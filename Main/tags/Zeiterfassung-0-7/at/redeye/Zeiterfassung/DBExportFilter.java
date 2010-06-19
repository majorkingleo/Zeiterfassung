/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Zeiterfassung;

import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.base.bindtypes.DBString;
import at.redeye.FrameWork.base.bindtypes.DBStrukt;
import at.redeye.FrameWork.base.bindtypes.DBValue;
import at.redeye.Setup.dbexport.DatabaseExport;
import at.redeye.SqlDBInterface.SqlDBIO.impl.UnsupportedDBDataTypeException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.WrongBindFileFormatException;
import at.redeye.Zeiterfassung.bindtypes.DBTimeEntries;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Vector;

/**
 *
 * @author martin
 */
public class DBExportFilter extends DatabaseExport
{
    public DBExportFilter( Root root, String file_name )
    {
        super( root, file_name );
    }

    @Override
    public int insertValues( DBStrukt table ) throws UnsupportedDBDataTypeException, WrongBindFileFormatException, SQLException, IOException
    {
        Vector<DBValue> values = table.getAllValues();

        for( DBValue val : values )
        {
            if( val instanceof DBString )
            {
                DBString dbs = (DBString) val;

                String s = dbs.toString();

                if( s.contains("'") )
                {
                    dbs.loadFromString(s.replace("'", ""));
                }
            }
        }
        
        return super.insertValues(table);
    }
}

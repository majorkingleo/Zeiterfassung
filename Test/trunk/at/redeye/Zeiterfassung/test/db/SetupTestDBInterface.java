/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Zeiterfassung.test.db;

import at.redeye.FrameWork.base.Root;
import at.redeye.SqlDBInterface.SqlDBConnection.impl.MissingConnectionParamException;
import at.redeye.SqlDBInterface.SqlDBConnection.impl.UnSupportedDatabaseException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.TableBindingNotRegisteredException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.UnsupportedDBDataTypeException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.WrongBindFileFormatException;
import at.redeye.UserManagement.bindtypes.DBPb;
import java.sql.SQLException;

/**
 *
 * @author martin
 */
public interface SetupTestDBInterface
{
    Root getRoot();
    void invoke() throws ClassNotFoundException, UnSupportedDatabaseException, SQLException, MissingConnectionParamException;

    public void close();

    DBPb getDB4User( String login_name ) throws SQLException, TableBindingNotRegisteredException, UnsupportedDBDataTypeException, WrongBindFileFormatException;
}

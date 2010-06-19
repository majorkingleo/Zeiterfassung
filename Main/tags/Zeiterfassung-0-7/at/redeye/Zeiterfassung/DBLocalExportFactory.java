/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Zeiterfassung;

import at.redeye.FrameWork.base.Root;
import at.redeye.Setup.dbexport.DBExImpFactory;
import at.redeye.Setup.dbexport.DatabaseExport;

/**
 *
 * @author martin
 */
public class DBLocalExportFactory extends DBExImpFactory
{
    @Override
    public DatabaseExport getNewExporter( Root root, String target_file_name )
    {
        return new DBExportFilter( root, target_file_name );
    }
}

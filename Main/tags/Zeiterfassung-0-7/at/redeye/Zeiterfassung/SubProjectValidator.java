/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Zeiterfassung;

import at.redeye.FrameWork.base.AutoLogger;
import at.redeye.FrameWork.base.AutoMBox;
import at.redeye.FrameWork.base.BaseDialog;
import at.redeye.FrameWork.base.bindtypes.DBSqlAsInteger;
import at.redeye.FrameWork.base.bindtypes.DBValue;
import at.redeye.FrameWork.base.tablemanipulator.TableManipulator;
import at.redeye.FrameWork.widgets.JoinTableCell;
import at.redeye.Zeiterfassung.bindtypes.DBProjects;
import at.redeye.Zeiterfassung.bindtypes.DBSubProjects;
import at.redeye.Zeiterfassung.bindtypes.DBTimeEntries;
import java.util.Vector;

/**
 *
 * @author martin
 */
public class SubProjectValidator extends JoinTableCell
{
    TableManipulator tm;
     BaseDialog dlg;

    public SubProjectValidator( BaseDialog dlg, TableManipulator tm, DBValue joinedColumn )
    {
        super( joinedColumn, dlg.getTransaction() );

        this.tm = tm;
        this.dlg = dlg;
    }

    @Override
    public boolean wantDoLoadSelf() {
        return true;
    }

    @Override
    public boolean acceptData(String data)
    {
        return true;
    }

    @Override
    public boolean loadToValue(final DBValue val, final String s, int row)
    {
        // ob wir das nun akzeptieren hängt vom Kunde, ab genaugenommen vom Feld: DBProjects.autocreate_subproject

        if( val.acceptString(s) )
        {
            val.loadFromString(s);
            return true;
        }

        // so jetzt akzeptiert das unser Option Menü nicht, na dann sehen wir nach
        // ob hier das Unterprojekt automatisch angelegt werden darf.

        // als erstes holen wir uns die aktuelle Zeile
        DBValue project = getProjectValue(row);

        if( project == null )
            return false;

        DBProjects proj = getProjectsEntry(project);

        if( proj == null )
            return false;

        // nun Auswerten, was wir nun überhaupt tun dürfen:

        if( proj.autocreate_subproject.getValue().equals("JA") )
        {
            // Neues Unterprojekt anlegen.
            final DBSubProjects sub_project = SubProjects.createNewSubProjectEntry(dlg, proj, true);

            sub_project.name.loadFromString(s);

            AutoMBox al = new AutoMBox(SubProjectValidator.class.getName(), true)
            {
                @Override
                public void do_stuff() throws Exception {
                    trans.insertValues(sub_project);
                    System.out.println( "Inserted new Subproject: " + s );
                }
            };

            if( al.isFailed() )
                return false;

            if( val instanceof DBSqlAsInteger )
            {
                DBSqlAsInteger easi = (DBSqlAsInteger) tm.getTabledesign().getColOfRow(val, row);
                easi.refresh();

                for( String ss : easi.getPossibleValues() )
                    System.out.println( "Values: " + ss );
            }

            if( val.acceptString(s) )
            {
                val.loadFromString(s);
                return true;
            }
            else
            {
                return false;
            }

        }
        else
        {
            return false;
        }
    }

    private DBValue getProjectValue( int row )
    {
        Vector vec = tm.getTabledesign().rows.get(row);

        DBTimeEntries te = new DBTimeEntries();
        return tm.getTabledesign().getColOfRow(te.project, row);
    }

    private DBProjects getProjectsEntry( DBValue project_number )
    {
        final DBProjects proj = new DBProjects();

        proj.id.loadFromCopy(project_number.getValue());

        AutoLogger al = new AutoLogger(SubProjectValidator.class.getName())
        {
            public void do_stuff() throws Exception
            {
                trans.fetchTableWithPrimkey(proj);
            }
        };

        if( al.isFailed() )
            return null;

        return proj;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Zeiterfassung.bindtypes;

import java.util.Vector;

import at.redeye.FrameWork.base.AutoLogger;
import at.redeye.FrameWork.base.bindtypes.DBSqlAsInteger;
import at.redeye.FrameWork.base.bindtypes.DBStrukt;
import at.redeye.FrameWork.base.bindtypes.DBSqlAsInteger.SqlQuery;
import at.redeye.FrameWork.base.transaction.Transaction;
import at.redeye.UserManagement.bindtypes.DBPb;

/**
 *
 * @author martin
 */
public class UserQuery extends DBSqlAsInteger.SqlQuery {

    Vector<SqlQuery.Pair> pairs = new Vector<SqlQuery.Pair>();
    Transaction trans;

    public UserQuery( Transaction trans  )
    {
        this.trans = trans;
        refresh();
    }

    public void refresh()
    {
        new AutoLogger("UserQuery") {

            public void do_stuff() throws Exception {

                Vector<DBStrukt> res = trans.fetchTable(new DBPb(),
                        "where " +
                        trans.markColumn("locked") +
                        "= '0'");

                for (DBStrukt s : res) {
                    DBPb jt = (DBPb) s;

                    String text = jt.surname.toString() + " " + jt.name.toString();

                    if (text.matches(" +")) {
                        text = jt.login.toString();
                    }

                    pairs.add(new SqlQuery.Pair((Integer) jt.id.getValue(), text));
                }
            }
        };
    }

    @Override
    public Vector<SqlQuery.Pair> getPossibleValues() {
        return pairs;
    }

    @Override
    public int getDefaultValue() {
        if( pairs.size() > 0 )
            return pairs.get(0).val;
        
        return 0;
    }

}

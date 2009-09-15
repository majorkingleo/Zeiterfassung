/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Zeiterfassung.bindtypes;

import at.redeye.FrameWork.base.AutoLogger;
import at.redeye.FrameWork.base.bindtypes.DBSqlAsInteger;
import at.redeye.FrameWork.base.bindtypes.DBSqlAsInteger.SqlQuery;
import at.redeye.FrameWork.base.bindtypes.DBStrukt;
import at.redeye.FrameWork.base.transaction.Transaction;
import java.util.Vector;

/**
 *
 * @author martin
 */
public class NameQuery extends DBSqlAsInteger.SqlQuery
{
    Vector<SqlQuery.Pair> pairs = new Vector<SqlQuery.Pair>();
    int default_value = 0;
    Transaction trans;
    NameIdInterface strukt;

    public NameQuery( Transaction trans, NameIdInterface strukt )
    {
        this.trans = trans;
        this.strukt = strukt;
        refresh();
    }

    @Override
    public Vector<SqlQuery.Pair> getPossibleValues() {
        return pairs;
    }

    @Override
    public int getDefaultValue() {
        if( pairs.size() > 0 )
            return pairs.get(default_value).val;

        return 0;
    }

    public void setNullEntryAsDefault()
    {
        pairs.insertElementAt(new SqlQuery.Pair(0,""), 0);
        default_value = 0;
    }

    @Override
    public void refresh() {
        new AutoLogger(NameQuery.class.getName()) {

            public void do_stuff() throws Exception {

                Vector<DBStrukt> res = trans.fetchTable(strukt.getNewOne());

                for (DBStrukt s : res) {
                    NameIdLockedInterface sub = (NameIdLockedInterface) s;

                    String text = sub.getNameValue().toString();

                    pairs.add(new SqlQuery.Pair((Integer) sub.getIdValue(), text));
                }
            }
        };
    }
}

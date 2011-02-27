/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Zeiterfassung.bindtypes;

import java.util.ArrayList;
import java.util.List;

import at.redeye.FrameWork.base.AutoLogger;
import at.redeye.FrameWork.base.bindtypes.DBSqlAsInteger;
import at.redeye.FrameWork.base.bindtypes.DBSqlAsInteger.SqlQuery;
import at.redeye.FrameWork.base.bindtypes.DBStrukt;
import at.redeye.FrameWork.base.transaction.Transaction;

/**
 * 
 * @author martin
 */
public class NameNotLockedQuery extends DBSqlAsInteger.SqlQuery {

	List<SqlQuery.Pair> pairs = new ArrayList<SqlQuery.Pair>();
	int default_value = 0;
	Transaction trans;
	NameIdLockedInterface strukt;

	public NameNotLockedQuery(Transaction trans, NameIdLockedInterface strukt) {
		this.trans = trans;
		this.strukt = strukt;

		refresh();
	}

	public void refresh() {
		new AutoLogger(NameNotLockedQuery.class.getName()) {

			public void do_stuff() throws Exception {

				pairs.clear();

				List<DBStrukt> res = trans.fetchTable(strukt.getNewOne(),
						"where " + trans.markColumn(strukt.getLockedName())
								+ "= 'NEIN' " + getExtraSql()
                                                                + " " + getOrderBySql());

				// System.out.println( "sql: " + trans.getSql() );

				for (DBStrukt s : res) {
					NameIdLockedInterface sub = (NameIdLockedInterface) s;

					String text = sub.getNameValue().toString();

					pairs.add(new SqlQuery.Pair((Integer) sub.getIdValue(),
							text));
				}
			}
		};
	}

	@Override
	public List<SqlQuery.Pair> getPossibleValues() {
		return pairs;
	}

	@Override
	public int getDefaultValue() {
		if (pairs.size() > 0)
			return pairs.get(default_value).val;

		return 0;
	}

	public void setNullEntryAsDefault() {
		pairs.add(0, new SqlQuery.Pair(0, ""));

		default_value = 0;
	}
}

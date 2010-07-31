/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Zeiterfassung.bindtypes;

import java.util.List;
import java.util.Vector;

import at.redeye.FrameWork.base.AutoLogger;
import at.redeye.FrameWork.base.bindtypes.DBSqlAsInteger;
import at.redeye.FrameWork.base.bindtypes.DBSqlAsInteger.SqlQuery;
import at.redeye.FrameWork.base.bindtypes.DBStrukt;
import at.redeye.FrameWork.base.transaction.Transaction;

/**
 * 
 * @author martin
 */
public class JobTypeQuery extends DBSqlAsInteger.SqlQuery {

	Vector<SqlQuery.Pair> pairs = new Vector<SqlQuery.Pair>();
	Transaction trans;

	public JobTypeQuery(Transaction trans) {
		this.trans = trans;
		refresh();
	}

	@Override
	public Vector<SqlQuery.Pair> getPossibleValues() {
		return pairs;
	}

	@Override
	public int getDefaultValue() {
		if (pairs.size() > 0)
			return pairs.get(0).val;

		return 0;
	}

	@Override
	public void refresh() {
		new AutoLogger("jobTypeQuery") {

			public void do_stuff() throws Exception {

				List<DBStrukt> res = trans.fetchTable(new DBJobType(), "where "
						+ trans.markColumn("locked") + " = 'NEIN'");

				for (DBStrukt s : res) {
					DBJobType jt = (DBJobType) s;

					String text = jt.name.toString();

					/*
					 * if( !jt.help.toString().isEmpty() ) text += " ... " +
					 * jt.help.toString();
					 */

					pairs.add(new SqlQuery.Pair((Integer) jt.id.getValue(),
							text));
				}
			}
		};
	}

}

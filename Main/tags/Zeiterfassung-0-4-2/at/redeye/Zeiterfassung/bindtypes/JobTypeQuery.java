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

/**
 *
 * @author martin
 */
public class JobTypeQuery extends DBSqlAsInteger.SqlQuery {

    Vector<SqlQuery.Pair> pairs = new Vector<SqlQuery.Pair>();
    
    public JobTypeQuery( final Transaction trans )
    {
        new AutoLogger("jobTypeQuery") {

            public void do_stuff() throws Exception {
                   
                Vector<DBStrukt> res = trans.fetchTable( new DBJobType(), "where "+trans.markColumn("locked")+" = 'NEIN'" );
        
                for( DBStrukt s : res )
                {
                    DBJobType jt = (DBJobType)s;
                    
                    String text = jt.name.toString();
                    
                    /*
                    if( !jt.help.toString().isEmpty() )
                        text += " ... " + jt.help.toString();
                    */
                    
                    pairs.add( new SqlQuery.Pair( (Integer)jt.id.getValue(), text ) );
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

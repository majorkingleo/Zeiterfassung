/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Zeiterfassung;

import java.sql.Time;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;

import at.redeye.FrameWork.base.AutoLogger;
import at.redeye.FrameWork.base.bindtypes.DBStrukt;
import at.redeye.FrameWork.base.reports.BaseReportRenderer;
import at.redeye.FrameWork.base.reports.ReportRenderer;
import at.redeye.FrameWork.base.transaction.Transaction;
import at.redeye.Zeiterfassung.bindtypes.DBTimeEntries;

/**
 *
 * @author martin
 */
public class MonthReportPerUserRenderer extends BaseReportRenderer implements ReportRenderer
{    
    Transaction trans;
    Vector<DBStrukt> data = new Vector<DBStrukt>();
    int mon;
    int year;
    String username;
    int user_id;
    
    public MonthReportPerUserRenderer(Transaction trans, int mon, int year, String username, int user_id )
    {        
        this.trans = trans;
        this.mon = mon;
        this.year = year;
        this.user_id = user_id;
        this.username = username;
    }
    
    public boolean collectData() 
    {
        AutoLogger al = new AutoLogger("MonthReportPerUserRenderer") {

            @Override
            public void do_stuff() throws Exception {
                result = new Boolean(false);
                
                String where = " where " + trans.markColumn("user") + " = '" + user_id + "' " 
                        + " and "                         
                        //+ trans.markColumn("from") 
                        //+ " like '" 
                        //+ new DateMidnight(year, mon, 1).toString("yyyy-MM-") + "%'"
                        + trans.getPeriodStmt("from", new DateMidnight(year, mon, 1), 
                        		new DateMidnight(year, mon+1, 1))
                        + " order by " + trans.markColumn("from");

                data = trans.fetchTable(new DBTimeEntries(), where);
                
                result = new Boolean(true);
            }
        };
        
        return (Boolean)al.result;
    }

    public String render() 
    {
        clear();
        
        html_start();
                
        html_setTitle("Monatsbericht " + MonthReportPerUser.getTitle(mon, year) + " für " + username );
        
        int day = 0;
        long milli_per_day = 0;
        long milli_per_month = 0;
        
        for( DBStrukt s : data )
        {
            DBTimeEntries e = (DBTimeEntries)s;
            
            Date dt_from = (Date)e.from.getValue();
            Date dt_to = (Date)e.to.getValue();
            
            DateTime d_from = new DateTime(dt_from.getTime());
            DateTime d_to = new DateTime(dt_to.getTime());
            
            if( day != d_from.getDayOfMonth() )
            {                                
                if( day != 0 )
                {
                    // summe pro Tag
                    html_newline();
                    write_sum(milli_per_day);
                    milli_per_day = 0;
                    
                    html_blockquote_end();
                }
                
                day = d_from.getDayOfMonth();
                
                html_bold_title( d_from.dayOfMonth().getAsText(Locale.GERMAN) + ". " +
                                 d_from.dayOfWeek().getAsText(Locale.GERMAN) );
                
                html_blockquote_start();
                
                milli_per_day = 0;
            }
            
            milli_per_day += e.calcDuration();
            milli_per_month += e.calcDuration();
            
            DateTime dur = new DateTime(new Time(e.calcDuration()-60*60*1000));
            
            html_bold( d_from.toString("HH:mm" ) + " - " + d_to.toString("HH:mm") + " = " +  dur.toString("HH:mm") );
            html_normal_text( " " + e.comment.getValue().toString() );
            
            html_newline();
        }
        
        write_sum(milli_per_day);
        
        if( data.size() > 0 )
            html_blockquote_end();
        
        html_bold_title( "Gesamt:" );
        html_blockquote_start();
        
        write_sum(milli_per_month);
        
        html_blockquote_end();
        
        html_stop();
        
        return text.toString();
    }

    private void write_sum(long milli_per_day) {                
        DateTime dur = new DateTime(new Time(milli_per_day-60*60*1000));
         
        html_bold( "Summe: " + dur.toString("HH:mm") );                
    }
    

    
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Zeiterfassung.reports;

import java.sql.Time;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;

import at.redeye.FrameWork.base.AutoLogger;
import at.redeye.FrameWork.base.bindtypes.DBFlagJaNein.FLAGTYPES;
import at.redeye.FrameWork.base.bindtypes.DBStrukt;
import at.redeye.FrameWork.base.reports.BaseReportRenderer;
import at.redeye.FrameWork.base.reports.ReportRenderer;
import at.redeye.FrameWork.base.transaction.Transaction;
import at.redeye.FrameWork.utilities.HMSTime;
import at.redeye.Zeiterfassung.bindtypes.DBJobType;
import at.redeye.Zeiterfassung.bindtypes.DBJobType.JOBTYPES;
import at.redeye.Zeiterfassung.bindtypes.DBTimeEntries;
import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author martin
 */
public class MonthReportPerUserRenderer extends BaseReportRenderer implements ReportRenderer
{    
    Transaction trans;
    Vector<DBStrukt> data = new Vector<DBStrukt>();
    HashMap<Integer,DBJobType> job_types = new HashMap<Integer,DBJobType>();
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
                
                String where = " where " + trans.markColumn("user") + " = " + user_id 
                        + " and "                         
                        //+ trans.markColumn("from") 
                        //+ " like '" 
                        //+ new DateMidnight(year, mon, 1).toString("yyyy-MM-") + "%'"
                        + trans.getPeriodStmt("from", new DateMidnight(year, mon, 1), 
                        		new DateMidnight(year, mon, 1).plusMonths(1))
                        + " order by " + trans.markColumn("from");

                data = trans.fetchTable(new DBTimeEntries(), where);

                // alle Jobtypes ins Hirn blasen
                Vector<DBStrukt> res = trans.fetchTable(new DBJobType());

                for( DBStrukt s : res )
                {
                    DBJobType jt = (DBJobType) s;
                    job_types.put(jt.id.getValue(), jt);
                }

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

        HashMap<Integer,Long> millis_per_jobtype_month = new HashMap<Integer,Long>();
        HashMap<Integer,Long> millis_per_jobtype_day = new HashMap<Integer,Long>();
        
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
                    write_sum_day(millis_per_jobtype_day);
                    millis_per_jobtype_day.clear();
                    
                    html_blockquote_end();
                }
                
                day = d_from.getDayOfMonth();
                
                html_bold_title( d_from.dayOfMonth().getAsText(Locale.GERMAN) + ". " +
                                 d_from.dayOfWeek().getAsText(Locale.GERMAN) );
                
                html_blockquote_start();
                
                millis_per_jobtype_day.clear();
            }

            Long duration =  millis_per_jobtype_day.get((Integer)e.jobtype.getValue());

            if( duration == null )
                duration = new Long(0);

            millis_per_jobtype_day.put((Integer)e.jobtype.getValue(),duration + e.calcDuration());

            duration =  millis_per_jobtype_month.get((Integer)e.jobtype.getValue());

            if( duration == null )
                duration = new Long(0);

            millis_per_jobtype_month.put((Integer)e.jobtype.getValue(),duration + e.calcDuration());
            
            DateTime dur = new DateTime(new Time(e.calcDuration()-60*60*1000));
            
            html_bold( d_from.toString("HH:mm" ) + " - " + d_to.toString("HH:mm") + " = " +  dur.toString("HH:mm") );
            html_normal_text( " [" + job_types.get((Integer)e.jobtype.getValue()).name.toString() + "]" );
            html_normal_text( " " + e.comment.getValue().toString() );
            
            html_newline();
        }
        
        html_newline();
        write_sum_day(millis_per_jobtype_day);
        
        if( data.size() > 0 )
            html_blockquote_end();
        
        html_bold_title( "Gesamt:" );
        html_blockquote_start();
        
        write_sum_month(millis_per_jobtype_month);
        
        html_blockquote_end();
        
        html_stop();
        
        return text.toString();
    }

    private void write_sum_month(HashMap<Integer,Long> sum_data) {

        Set<Integer> keys = sum_data.keySet();
        
        for( Integer key : keys )
        {                        
            html_bold(job_types.get(key).name.toString() + ": " + getDurFromMilli(sum_data.get(key)) );
            html_newline();
        }

        // Leistungszeit gesammt
        long lz=0;
        long nlz=0;
        long urlaub=0;
        long summe=0;

        for( Integer key : keys )
        {
          DBJobType jt = job_types.get(key);

          if( jt.type.getValue().equals(JOBTYPES.LZ.toString()) )
            lz += sum_data.get(key);
          else
            nlz += sum_data.get(key);

          if(jt.is_holliday.getValue().equals(FLAGTYPES.JA.toString()))
              urlaub += sum_data.get(key);

          summe += sum_data.get(key);
        }

        html_newline();
        html_bold("Leistungszeit: " + getDurFromMilli(lz)); html_newline();
        html_bold("Nichtleistungszeit: " + getDurFromMilli(nlz)); html_newline();
        html_bold("Urlaub: " + getDurFromMilli(urlaub)); html_newline();
        html_bold("Summe: " + getDurFromMilli(summe)); html_newline();
    }

    private String getDurFromMilli(long milli)
    {
        // DateTime dur = new DateTime(new Time(milli-60*60*1000));
        // return dur.toString("HH:mm");
        HMSTime hms_time = new HMSTime( milli );
        return hms_time.toString("HH:mm");
    }

    private void write_sum_day(HashMap<Integer,Long> sum_data) {

        Set<Integer> keys = sum_data.keySet();

        StringBuilder line = new StringBuilder();

        for( Integer key : keys )
        {            
            line.append(" ");
            line.append(job_types.get(key).name.toString() + ": " + getDurFromMilli(sum_data.get(key)));
        }

        html_bold(line.toString());
    }
}

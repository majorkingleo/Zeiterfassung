/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Zeiterfassung;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import org.joda.time.DateMidnight;

import at.redeye.FrameWork.base.AutoLogger;
import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.base.bindtypes.DBStrukt;
import at.redeye.FrameWork.base.transaction.Transaction;
import at.redeye.FrameWork.widgets.calendarday.InfoRenderer;
import at.redeye.Zeiterfassung.bindtypes.DBTimeEntries;

/**
 *
 * @author martin
 */
public class TimeEntryRenderer implements InfoRenderer 
{
    protected Transaction trans;    
    protected String info = new String();
    protected StringBuilder text = new StringBuilder();
    protected DateMidnight day = null;
    protected Root root;
    protected Vector<DBStrukt> rows = null;
    protected SimpleDateFormat formater_time = new SimpleDateFormat("HH:mm");
    protected String sum = new String();
    protected TimeEntryCache cache = null;
    
    public TimeEntryRenderer( Transaction trans, Root root, TimeEntryCache cache )
    {
        this.trans = trans;
        this.root = root;
        this.cache = cache;
    }
    
    public void clear() {
        text.setLength(0);
        info = "";
        sum = "";
    }

    public InfoRenderer getNewInstance() {
        return new TimeEntryRenderer(trans, root, cache);
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void update() {     
        sum = "";
        
        if( day == null )
            return;
        
        new AutoLogger("TimeEntryRenderer") {

            @Override
            public void do_stuff() throws Exception {                                
                     
                if( cache != null )
                {
                    rows = cache.getEntries(trans, root.getUserId(), day);
                }
                else
                {
                    String where = "where "
                        + trans.markColumn("user")
                        + "='" + root.getUserId() + "' " 
                        + " and "
                        + trans.getDayStmt("from", day )
                        + " order by " + trans.markColumn("from");
            	
                       rows = trans.fetchTable(new DBTimeEntries(), where);  
                }            		                      
            }
        };
        
        
    }

    public String render() 
    {                
        text.setLength(0);
        text.append("<html><body>");        
        text.append("<font size=\"2\" color=\"#800000\">" + info.toString() + "</font>\n");
         
        if( rows != null )        
        {
            text.append("<font size=\"2\">");
            
            long millis = 0;
            
            for( DBStrukt e : rows )
            {
                DBTimeEntries te = (DBTimeEntries)e;
                
                text.append("<br/>\n");
                                
                Date time = (Date)te.from.getValue();                
                String res = formater_time.format(time.getTime()); 
                text.append(res);
                
                text.append("-");
                
                time = (Date)te.to.getValue();
                res = formater_time.format(time.getTime()); 
                text.append(res);
                
                text.append(" ");
                
                text.append(te.comment.toString());
                            
                millis += te.calcDuration();                
            }
            
            text.append("</font>");
            
            if( millis > 0 )
            {
  ///              System.out.println("millis: " + millis);
                sum = formater_time.format(new Time(millis-60*60*1000) );
            }
        }
        
        text.append("</body></html>");
        return text.toString();
    }

    public void addContent(Object data) {
        // nix
    }

    public void setDay(DateMidnight day) {
        this.day = day;
    }
    
    public String renderSum()
    {
        return sum;
    }
}

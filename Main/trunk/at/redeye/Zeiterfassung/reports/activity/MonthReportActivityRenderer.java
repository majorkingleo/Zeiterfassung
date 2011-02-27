/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Zeiterfassung.reports.activity;

import java.net.URL;
import java.util.Date;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;

import at.redeye.FrameWork.base.AutoLogger;
import at.redeye.FrameWork.base.reports.BaseReportRenderer;
import at.redeye.FrameWork.base.reports.ReportRenderer;
import at.redeye.FrameWork.base.transaction.Transaction;
import at.redeye.FrameWork.utilities.HMSTime;
import at.redeye.FrameWork.utilities.calendar.MonthNames;
import at.redeye.Zeiterfassung.bindtypes.DBCustomers;
import at.redeye.Zeiterfassung.bindtypes.DBProjects;
import at.redeye.Zeiterfassung.bindtypes.DBTimeEntries;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

/**
 *
 * @author martin
 */
public class MonthReportActivityRenderer extends BaseReportRenderer implements ReportRenderer
{    
    Transaction trans;
    ArrayList<DBTimeEntries> data = null;
    HashMap<Integer,DBCustomers> customers= new HashMap<Integer,DBCustomers>();
    HashMap<Integer,DBProjects> projects= new HashMap<Integer,DBProjects>();
    int mon=1;
    int year=2010;

    public static final String CUST_PROPS = "customer";
    public static final String PROJECT_PROPS = "project";

    Properties settings;

    public MonthReportActivityRenderer(Transaction trans, int mon, int year )
    {        
        this.trans = trans;
        this.mon = mon;
        this.year = year;
    }

    
    public MonthReportActivityRenderer(Transaction trans)
    {        
        this.trans = trans;        
    }

    public boolean collectData() 
    {
        AutoLogger al = new AutoLogger(MonthReportActivityRenderer.class.getSimpleName()) {

            @Override
            public void do_stuff() throws Exception {
                result = false;

                projects.clear();
                customers.clear();

                String where = " where " 
                        + trans.getPeriodStmt("from", new DateMidnight(year, mon, 1), 
                        		new DateMidnight(year, mon, 1).plusMonths(1).minusDays(1))
                        + " order by " + trans.markColumn("from");

                data = trans.fetchTableList(new DBTimeEntries(), where);

                ArrayList<DBCustomers> cust = trans.fetchTableList(new DBCustomers());

                for( DBCustomers c : cust )
                    customers.put(c.id.getValue(), c);

                ArrayList<DBProjects> proj = trans.fetchTableList(new DBProjects());

                for( DBProjects p : proj )
                    projects.put(p.id.getValue(), p);

                result = true;
            }
        };
        
        return (Boolean)al.result;
    }

    public String render() 
    {
        if( data == null )
        {
            if( !collectData() )
                return "failed";
        }

        clear();
        
        html_start();
                
        html_setTitle("Monatsbericht " + MonthNames.getFullMonthName(mon) + " " + year );
            
        // TreeMap, weil automatisch  Alphabetisch sortiert
        HashMap<Integer,Long> millis_per_customer = new HashMap<Integer,Long> ();
        HashMap<Integer,Long> millis_per_project = new HashMap<Integer,Long> ();

        for( DBTimeEntries e : data )
        {                        
            Date dt_from = (Date)e.from.getValue();
            Date dt_to = (Date)e.to.getValue();
            
            DateTime d_from = new DateTime(dt_from.getTime());
            DateTime d_to = new DateTime(dt_to.getTime());
            

            Integer customer = (Integer)e.customer.getValue();

            Long val = millis_per_customer.get(customer);
            
            if( val == null )            
            {
                val = 0L;
            }

            val += e.calcDuration();
            millis_per_customer.put(customer, val);

            Integer project = (Integer) e.project.getValue();

            val = millis_per_project.get(project);

            if (val == null) {
                val = 0L;
            }

            val += e.calcDuration();
            millis_per_project.put(project, val);

        }

        for( Integer customer_id : millis_per_customer.keySet() )
        {
            DBCustomers cust = customers.get(customer_id);

            String name;

            if( cust == null )
            {
                logger.error("cust is null where requesting " + customer_id);
                name = "unknown";
            }
            else
                name = cust.getNameValue();


            html_bold_title(name);
            html_normal_text(getDurFromMilli(millis_per_customer.get(customer_id)));

            if( haveCustomerDetails(customer_id) ) {

                

                html_add_link_to_self(CUST_PROPS + "=-" + customer_id, "(-)");
                
                html_blockquote_start();
                
                for( Integer project_id : millis_per_project.keySet() )
                {
                    DBProjects proj = projects.get(project_id);

                    String proj_name;
                    
                    if( proj != null ) {
                        proj_name = proj.name.getValue();

                        if( !proj.customer.getValue().equals(customer_id) )
                            continue;
                        
                    } else {
                        proj_name = "unknown";
                        continue;
                    }

                    html_normal_text(proj_name + ": " + getDurFromMilli(millis_per_project.get(project_id)));
                    html_newline();
                }
                
                html_blockquote_end();
            } else {
                html_add_link_to_self(CUST_PROPS + "=" + customer_id, "(+)");
            }
        }
        
        html_stop();
        
        return text.toString();
    }

    private String getDurFromMilli(long milli)
    {
        // DateTime dur = new DateTime(new Time(milli-60*60*1000));
        // return dur.toString("HH:mm");
        HMSTime hms_time = new HMSTime( milli );
        return hms_time.toString("HH:mm");
    }

    public void html_add_link_to_self( String id, String name )
    {
        html_normal_text(" <a href=\"http://reports.localhost.com?" + id + "\">" + name + "</a>");
    }

    void setMonth(int mon, int year) {
        this.mon = mon;
        this.year = year;

        data = null;
    }

    Properties UrlParams2Props( String surl )
    {
        int index = surl.indexOf('?');

        if( index < 0 )
        {
            logger.error("index of ? is :" + index );
            return null;
        }

        surl = surl.substring(index+1);

        Properties props = new Properties();

        String parts[] = surl.split("&");

        for( String p : parts )
        {
            String property[] = p.split("=");

            if( property.length > 1 )
                props.setProperty(property[0], property[1]);
            else
                props.setProperty(property[0],"1" );
        }

        return props;
    }

    void handleUrl(URL url) {

        if( url == null ) {
            logger.error("url is null. don't know what todo?");
            return;
        }

        String surl = url.toString();

        logger.info(surl);

        applyProps( UrlParams2Props(surl) );
    }

    private void applyProps(Properties UrlParams2Props)
    {
        settings = UrlParams2Props;
    }

    private String getSetting( String value )
    {
        if( settings == null )
            return null;

        return settings.getProperty(value);
    }

    private boolean haveCustomerDetails()
    {
        String s = getSetting(CUST_PROPS);

        if( s == null )
            return false;

        if( Integer.parseInt(s) > 0 )
            return true;

        return false;
    }

    private boolean haveCustomerDetails(Integer id)
    {
        String s = getSetting(CUST_PROPS);

        if( s == null )
            return false;

        if( s.equals(id.toString()) )
            return true;

        return false;
    }
}

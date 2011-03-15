/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Zeiterfassung;

import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.base.transaction.Transaction;
import at.redeye.FrameWork.utilities.HMSTime;
import at.redeye.SqlDBInterface.SqlDBConnection.impl.MissingConnectionParamException;
import at.redeye.SqlDBInterface.SqlDBConnection.impl.UnSupportedDatabaseException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.TableBindingNotRegisteredException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.UnsupportedDBDataTypeException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.WrongBindFileFormatException;
import at.redeye.UserManagement.bindtypes.DBPb;
import at.redeye.Zeiterfassung.bindtypes.DBUserPerMonth;
import at.redeye.Zeiterfassung.overtime.OvertimeInterface;
import at.redeye.Zeiterfassung.overtime.Schema_1;
import at.redeye.Zeiterfassung.test.db.CalcMonthStuffInputGenerator;
import at.redeye.Zeiterfassung.test.db.SetupTestDB;
import at.redeye.Zeiterfassung.test.db.SetupTestDBInterface;
import java.sql.SQLException;
import java.util.List;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Wir Testen hier nun eine 38.5 Stunden Woche mit Überstundenschema 1 im May 2010
 * Hier wird eine Mehrstunde, aber keine Überstunden entstehen
 * @author martin
 */
public class CalcMonthStuffTestSchema1June {

    static SetupTestDBInterface setup_test_db;
    static CalcMonthStuff calc_month_stuff;
    static Root root;
    static Transaction trans;
    static DBPb user;
    static CalcMonthStuffInputGenerator calc_month_stuff_generator;
    final LocalDate from;
    final LocalDate to;

    public CalcMonthStuffTestSchema1June() throws ClassNotFoundException, UnSupportedDatabaseException, SQLException, MissingConnectionParamException, TableBindingNotRegisteredException, UnsupportedDBDataTypeException, WrongBindFileFormatException, CloneNotSupportedException, DuplicateRecordException
    {
        from = new LocalDate( 2010,6,1);
        to = new LocalDate( 2010,6,30);
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        if( setup_test_db == null )
        {
            setup_test_db = new SetupTestDB();
            setup_test_db.invoke();
            user = setup_test_db.getDB4User("martin.test");
            root = setup_test_db.getRoot();        
            trans = root.getDBConnection().getDefaultTransaction();
            calc_month_stuff_generator = new CalcMonthStuffInputGenerator(2010, 6, user);
            calc_month_stuff = new CalcMonthStuff(calc_month_stuff_generator, trans, user.getUserId());
            calc_month_stuff.calc();
        }
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
        
    }

    /**
     * Test of getHoursPerDay method, of class CalcMonthStuff.
     */
    @Test
    public void testGetHoursPerDay() {                
        for (LocalDate today = from; today.isBefore(to) || today.isEqual(to); today = today.plusDays(1)) 
        {
            System.out.println("getHoursPerDay for " + today);
            CalcMonthStuff instance = calc_month_stuff;
            double expResult = 0.0;

            switch( today.getDayOfWeek() )
            {
                case DateTimeConstants.MONDAY:
                case DateTimeConstants.TUESDAY:
                case DateTimeConstants.WEDNESDAY:
                case DateTimeConstants.THURSDAY:
                    expResult = 8.0;
                    break;

                case DateTimeConstants.FRIDAY:
                    expResult = 6.5;
                    break;

                case DateTimeConstants.SATURDAY:
                case DateTimeConstants.SUNDAY:
                    expResult = 0;
                    break;
            }

            if( calc_month_stuff_generator.isHoliday(today) )
                expResult = 0.0;


            double result = instance.getHoursPerDay(today);
            assertEquals(expResult, result, 0.0);            
        }
    }

    /**
     * Test of getUPMRecord method, of class CalcMonthStuff.
     */
    @Test
    public void testGetUPMRecord() throws Exception {
        System.out.println("getUPMRecord");
        
        DBUserPerMonth upm = new DBUserPerMonth();

        LocalDate from_april = new LocalDate( 2010,4,1);
        LocalDate to_april = new LocalDate( 2010,4,30);
        
        List<DBUserPerMonth> upms = trans.fetchTable2(upm, 
            "where " + trans.getDayStmt(upm.from, from_april) +
            " and " + trans.getDayStmt(upm.to, to_april) +
            " and " + trans.markColumn(upm.user) + "=" + user.id.toString() );
        
        assertEquals( upms.size(), 1 );
        
        upm = upms.get(0);
        
        Integer expResult = upm.id.getValue();
        DBUserPerMonth result = calc_month_stuff.getUPMRecord(from.getYear(), from.getMonthOfYear());
        assertEquals(expResult, result.id.getValue());
    }

    /**
     * Test of getWorkDaysForMonth method, of class CalcMonthStuff.
     */
    @Test
    public void testGetWorkDaysForMonth() {

        System.out.println("getWorkDaysForMonth");
        
        int expResult = 21;
        int result = calc_month_stuff.getWorkDaysForMonth(from.toDateTimeAtStartOfDay().toDate());
        assertEquals(expResult, result);
        
    }

    /**
     * Test of getError method, of class CalcMonthStuff.
     */
    @Test
    public void testGetError() {
        System.out.println("getError");
        
        String expResult = "";
        String result = calc_month_stuff.getError();
        assertEquals(expResult, result);        
    }

    /**
     * Test of hasError method, of class CalcMonthStuff.
     */
    @Test
    public void testHasError() {
        System.out.println("hasError");
        
        boolean expResult = false;
        boolean result = calc_month_stuff.hasError();
        assertEquals(expResult, result);        
    }

    /**
     * Test of getOverTimeforUPM method, of class CalcMonthStuff.
     */
    @Test
    public void testGetOverTimeforUPM() throws TableBindingNotRegisteredException, UnsupportedDBDataTypeException, SQLException, WrongBindFileFormatException, CloneNotSupportedException, DuplicateRecordException {
        System.out.println("getOverTimeforUPM");
        DBUserPerMonth upm = calc_month_stuff.getUPMRecord(from.getYear(), from.getMonthOfYear());

        OvertimeInterface result = CalcMonthStuff.getOverTimeforUPM(upm);

        if( !(result instanceof Schema_1) )
            fail("expected Schema_1 instance, but got " + result.toString());
    }

    /**
     * Test of getFormatedHoursPerMonth method, of class CalcMonthStuff.
     */
    @Test
    public void testGetFormatedHoursPerMonth() {
        System.out.println("getFormatedHoursPerMonth");
        
        String expResult = "162:00";
        String result = calc_month_stuff.getFormatedHoursPerMonth();
        assertEquals(expResult, result);
        
    }

    /**
     * Test of getHoursPerMonthInMillis method, of class CalcMonthStuff.
     */
    @Test
    public void testGetHoursPerMonthInMillis() {
        System.out.println("getHoursPerMonthInMillis");
        
        long expResult = (long)(162 * 60 * 60 * 1000);
        long result = calc_month_stuff.getHoursPerMonthInMillis();
        assertEquals(expResult, result);
        
    }

    /**
     * Test of getCompleteTime method, of class CalcMonthStuff.
     */
    @Test
    public void testGetCompleteTime() {
        System.out.println("getCompleteTime");
        
        HMSTime expResult = new HMSTime((long)(163*60*60*1000));
        HMSTime result = calc_month_stuff.getCompleteTime();
        assertEquals(expResult, result);
        
    }

    /**
     * Test of getFlexTime method, of class CalcMonthStuff.
     */
    @Test
    public void testGetFlexTime() {
        System.out.println("getFlexTime");
       
        HMSTime expResult = new HMSTime((long)(2.5 * 1000*60*60));
        HMSTime result = calc_month_stuff.getFlexTime();
        assertEquals(expResult, result);
        
    }

    /**
     * Test of getFlexTimeNoExtra method, of class CalcMonthStuff.
     */
    @Test
    public void testGetFlexTimeNoExtra() {
        System.out.println("getFlexTimeNoExtra");
        
        HMSTime expResult = new HMSTime(2 * 1000*60*60);
        HMSTime result = calc_month_stuff.getFlexTimeNoExtra();
        assertEquals(expResult, result);
        
    }

    /**
     * Test of getRemainingLeave method, of class CalcMonthStuff.
     */
    @Test
    public void testGetRemainingLeave() {
        System.out.println("getRemainingLeave");
        
        HMSTime expResult = new HMSTime(0);
        HMSTime result = calc_month_stuff.getRemainingLeave();
        assertEquals(expResult, result);
        
    }

    /**
     * Test of getExtraTimePerMonthDone method, of class CalcMonthStuff.
     */
    @Test
    public void testGetExtraTimePerMonthDone() {
        System.out.println("getExtraTimePerMonthDone");
        
        HMSTime expResult = new HMSTime((long)(0.5*60*60*1000));
        HMSTime result = calc_month_stuff.getExtraTimePerMonthDone();
        assertEquals(expResult, result);
        
    }

    /**
     * Test of getOverTimeNoExraPerMonthDone method, of class CalcMonthStuff.
     */
    @Test
    public void testGetOverTimeNoExtraPerMonthDone() {
        System.out.println("getOverTimeNoExtraPerMonthDone");

        HMSTime expResult = new HMSTime(60*60*1000);
        HMSTime result = calc_month_stuff.getOverTimeNoExtraPerMonthDone();
        assertEquals(expResult, result);

    }

    /**
     * Test of getOverTimePerMonthDone method, of class CalcMonthStuff.
     */
    @Test
    public void testGetOverTimePerMonthDone() {
        System.out.println("getOverTimePerMonthDone");
        
        HMSTime expResult = new HMSTime((long)(1.5 * 60*60*1000));
        HMSTime result = calc_month_stuff.getOverTimePerMonthDone();
        assertEquals(expResult, result);
        
    }

    /**
     * Test of getOverTime method, of class CalcMonthStuff.
     */
    @Test
    public void testGetOverTime() {
        System.out.println("getOverTime");
        
        HMSTime expResult = new HMSTime((long)(1.5 * 60*60*1000));
        HMSTime result = calc_month_stuff.getOverTime();
        assertEquals(expResult, result);
        
    }

    /**
     * Test of getOverTimeNoExtra method, of class CalcMonthStuff.
     */
    @Test
    public void testGetOverTimeNoExtra() {
        System.out.println("getOverTimeNoExtra");
        
        HMSTime expResult =  new HMSTime(60*60*1000);
        HMSTime result = calc_month_stuff.getOverTimeNoExtra();
        assertEquals(expResult, result);
        
    }

    /**
     * Test of getOverTimeInterface method, of class CalcMonthStuff.
     */
    @Test
    public void testGetOverTimeInterface() {
        System.out.println("getOverTimeInterface");
        
        OvertimeInterface result = calc_month_stuff.getOverTimeInterface();

        if( result == null )
            fail( "no nullpointer expected" );

        if( !(result instanceof Schema_1))
            fail("expected Schema_1 but got " + result.toString());
    }

    /**
     * Test of getRemainingLeaveInDays method, of class CalcMonthStuff.
     */
    @Test
    public void testGetRemainingLeaveInDays() {
        System.out.println("getRemainingLeaveInDays");
        
        double expResult = 25.0;
        double result = calc_month_stuff.getRemainingLeaveInDays();
        assertEquals(expResult, result, 0.0);
        
    }

   /**
     * Test of getFlexTimeEOM method, of class CalcMonthStuff.
     */
    @Test
    public void testgetFlexTimeEOM() {
        System.out.println("getFlexTimeEOM");

        HMSTime expResult = new HMSTime(1*60*60*1000);
        HMSTime result = calc_month_stuff.getFlexTimeEOM();
        assertEquals(expResult, result);

    }


    /**
     * Test of getOverTimeEOM method, of class CalcMonthStuff.
     */
    @Test
    public void testgetOvertimeEOM() {
        System.out.println("getOverTimeEOM");

        HMSTime expResult = new HMSTime((long)(1.5*60*60*1000));
        HMSTime result = calc_month_stuff.getOverTimeEOM();
        assertEquals(expResult, result);

    }
}
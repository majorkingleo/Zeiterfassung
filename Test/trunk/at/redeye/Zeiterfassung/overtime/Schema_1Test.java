/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Zeiterfassung.overtime;

import at.redeye.Zeiterfassung.bindtypes.DBTimeEntries;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author martin
 */
public class Schema_1Test {

    static final long SECONDS = 1000;
    static final long MINUTES = 1000*60;
    static final long HOURS = 1000*60*60;
    static final Date TODAY = new GregorianCalendar(2011,03,01,20,36,00).getTime();

    static class TestCase
    {
        Collection<DBTimeEntries> entries = new ArrayList<DBTimeEntries>();
        long expected_result_for_extra_time = -1;
        long expected_result_for_over_time = -1;
        boolean is_holiday = false;

        String name;

        TestCase( String name )
        {
            this.name = name;
        }
        
        void add( DBTimeEntries entry )
        {
            entries.add(entry);
        }

        void addEntry( String from, String to )
        {
            DBTimeEntries entry = new DBTimeEntries();
            entry.from.loadFromCopy(TODAY);
            entry.to.loadFromCopy(TODAY);

            if( !entry.from.loadTimePart(from) )
                fail("Load time part from '" + from + "'");
            
            if( !entry.to.loadTimePart(to) )
                fail("Load time part from '" + from + "'");

            entries.add(entry);
        }

        @Override
        public String toString()
        {
            StringBuilder buf = new StringBuilder();

            buf.append(name);
            buf.append(' ');

            boolean first = true;

            for( DBTimeEntries entry : entries )
            {
                if( first )
                    first = false;
                else
                    buf.append(", ");

                buf.append(entry.from.getTimeStr());
                buf.append(" - ");
                buf.append(entry.to.getTimeStr());
            }

            return buf.toString();
        }
    }

    Collection<TestCase> test_cases = new ArrayList<TestCase>();

    public Schema_1Test()
    {
        {
            TestCase test = new TestCase("No Entries");
            test_cases.add(test);

            test.expected_result_for_extra_time = 0;
            test.expected_result_for_over_time = 0;
        }

        {
            // 19:00 bis 20:00
            TestCase test = new TestCase("One Hour");
            test_cases.add(test);

            test.addEntry("11:00:00", "12:00:00");

            test.expected_result_for_extra_time = 0;
            test.expected_result_for_over_time = 0;
        }

        {
            // 19:00 bis 20:00
            TestCase test = new TestCase("One Hour on Holiday");
            test_cases.add(test);

            test.is_holiday = true;
            test.addEntry("11:00:00", "12:00:00" );

            test.expected_result_for_extra_time = 30 * MINUTES;
            test.expected_result_for_over_time = 1 * HOURS;
        }

        {
            // 09:00 - 22:00
            TestCase test = new TestCase("9 Hours + 6 Hours Overtime");
            test_cases.add(test);
            
            test.addEntry("09:00:00", "22:00:00" );

            test.expected_result_for_extra_time = 2 * HOURS;
            test.expected_result_for_over_time = 4 * HOURS;
        }

        {
            // 15:00 - 23:59
            TestCase test = new TestCase("9 Hours + 4 of them Overtime");
            test_cases.add(test);

            test.addEntry("15:00:00", "23:59:00" );

            test.expected_result_for_extra_time = (3 * HOURS + 59 * MINUTES)/2;
            test.expected_result_for_over_time = 3 * HOURS + 59 * MINUTES;
        }

        {
            // eine Stunde arbeiten außerhalb der Normalarbeitszeit
            TestCase test = new TestCase("One Hour Overtime");
            test_cases.add(test);

            test.addEntry("20:00:00", "21:00:00");

            test.expected_result_for_extra_time = 30 * MINUTES;
            test.expected_result_for_over_time = 1 * HOURS;
        }

    }

    @BeforeClass
    public static void setUpClass() throws Exception {
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
     * Test of calcExtraTimeForDay method, of class Schema_1.
     */
    @Test
    public void testCalcExtraTimeForDay() {
        System.out.println("calcExtraTimeForDay");
        Collection<DBTimeEntries> entries_per_day = null;
        boolean is_holiday = false;
        Schema_1 instance = new Schema_1();

        for( TestCase test : test_cases )
        {
            System.out.println("    => " + test.toString());
            long expResult = test.expected_result_for_extra_time;
            long result = instance.calcExtraTimeForDay(test.entries, test.is_holiday);
            assertEquals(expResult, result);
        }
    }

    /**
     * Test of calcExtraTimeForMonth method, of class Schema_1.
     */
    @Test
    public void testCalcExtraTimeForMonth() {
        System.out.println("calcExtraTimeForMonth");
        long regular_work_time = 0L;
        long real_work_time = 0L;
        Schema_1 instance = new Schema_1();
        long expResult = 0L;
        long result = instance.calcExtraTimeForMonth(regular_work_time, real_work_time);
        assertEquals(expResult, result);                
    }

    /**
     * Test of calcOverTimeForDay method, of class Schema_1.
     */
    @Test
    public void testCalcOverTimeForDay() {
        System.out.println("calcOverTimeForDay");
        List<DBTimeEntries> entries_per_day = null;
        boolean holiday = false;
        Schema_1 instance = new Schema_1();

        for( TestCase test : test_cases )
        {
            System.out.println("    => " + test.toString());
            long expResult = test.expected_result_for_over_time;
            long result = instance.calcOverTimeForDay(test.entries, test.is_holiday);
            assertEquals(expResult, result);
        }
    }

}
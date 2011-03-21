/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Zeiterfassung.overtime;

import at.redeye.FrameWork.utilities.calendar.AustrianHolidays;
import at.redeye.FrameWork.utilities.calendar.Holidays;
import at.redeye.Zeiterfassung.bindtypes.DBTimeEntries;
import at.redeye.Zeiterfassung.bindtypes.DBUserPerMonth;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import org.joda.time.LocalDate;
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
        long expected_result_for_more_time = -1;
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
    DBUserPerMonth upm_38_5;
    DBUserPerMonth upm_32;

    public Schema_1Test()
    {
        upm_38_5 = new DBUserPerMonth();
        upm_38_5.hours_per_week.loadFromCopy(38.5);

        upm_32 = new DBUserPerMonth();
        upm_32.hours_per_week.loadFromCopy(32.0);


        {
            TestCase test = new TestCase("No Entries");
            test_cases.add(test);

            test.expected_result_for_extra_time = 0;
            test.expected_result_for_over_time = 0;
            test.expected_result_for_more_time = 0;
        }

        {
            // 19:00 bis 20:00
            TestCase test = new TestCase("One Hour");
            test_cases.add(test);

            test.addEntry("11:00:00", "12:00:00");

            test.expected_result_for_extra_time = 0;
            test.expected_result_for_over_time = 0;
            test.expected_result_for_more_time = 0;
        }

        {
            // 19:00 bis 20:00
            TestCase test = new TestCase("One Hour on Holiday");
            test_cases.add(test);

            test.is_holiday = true;
            test.addEntry("11:00:00", "12:00:00" );

            test.expected_result_for_extra_time = 30 * MINUTES;
            test.expected_result_for_over_time = 1 * HOURS;
            test.expected_result_for_more_time = 0;
        }

        {
            // 09:00 - 22:00
            TestCase test = new TestCase("9 Hours + 6 Hours Overtime");
            test_cases.add(test);
            
            test.addEntry("09:00:00", "22:00:00" );

            test.expected_result_for_extra_time = 2 * HOURS;
            test.expected_result_for_over_time = 4 * HOURS;
            test.expected_result_for_more_time = 2 * HOURS + 30 * MINUTES;
        }

        {
            // 15:00 - 23:59
            TestCase test = new TestCase("9 Hours + 4 of them Overtime");
            test_cases.add(test);

            test.addEntry("15:00:00", "23:59:00" );

            test.expected_result_for_extra_time = (3 * HOURS + 59 * MINUTES)/2;
            test.expected_result_for_over_time = 3 * HOURS + 59 * MINUTES;
            test.expected_result_for_more_time = 0;
        }

        {
            // eine Stunde arbeiten außerhalb der Normalarbeitszeit
            TestCase test = new TestCase("One Hour Overtime");
            test_cases.add(test);

            test.addEntry("20:00:00", "21:00:00");

            test.expected_result_for_extra_time = 30 * MINUTES;
            test.expected_result_for_over_time = 1 * HOURS;
            test.expected_result_for_more_time = 0;
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
        
        boolean is_holiday = false;
        Schema_1 instance = new Schema_1(upm_38_5);

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
        Schema_1 instance = new Schema_1(upm_38_5);
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
        Schema_1 instance = new Schema_1(upm_38_5);

        for( TestCase test : test_cases )
        {
            System.out.println("    => " + test.toString());
            long expResult = test.expected_result_for_over_time;
            long result = instance.calcOverTimeForDay(test.entries, test.is_holiday);
            assertEquals(expResult, result);
        }
    }

    /**
     * Test of calcMorning method, of class Schema_1.
     */
    @Test
    public void testCalcMorning() {
        System.out.println("calcMorning");
        Date date = TODAY;
        Schema_1 instance = new Schema_1(upm_32);
        Date expResult = new GregorianCalendar(2011,03,01,6,30).getTime();
        Date result = instance.calcMorning(date);
        assertEquals(expResult, result);
                
    }

    /**
     * Test of calcEvening method, of class Schema_1.
     */
    @Test
    public void testCalcEvening() {
        System.out.println("calcEvening");
        Date date = TODAY;
        Schema_1 instance = new Schema_1(upm_32);
        Date expResult = new GregorianCalendar(2011,03,01,20,00).getTime();
        Date result = instance.calcEvening(date);
        assertEquals(expResult, result);
        
    }

    /**
     * Test of getOverTimeFactor method, of class Schema_1.
     */
    @Test
    public void testGetOverTimeFactor() {
        System.out.println("getOverTimeFactor");
        Schema_1 instance = new Schema_1(upm_32);
        double expResult = 1.5;
        double result = instance.getOverTimeFactor();
        assertEquals(expResult, result, 0.0);        
    }

    /**
     * Test of getHours4Day method, of class Schema_1.
     */
    @Test
    public void testGetHours4Day() {
        System.out.println("getHours4Day");

        {
            LocalDate dm = new LocalDate(2011, 3, 1);
            LocalDate dm_end = dm.plusMonths(1);

            Schema_1 instance = new Schema_1(upm_38_5);
            double hours_per_month = 0;
            Holidays holidays = new AustrianHolidays();

            for (; dm.isBefore(dm_end); dm = dm.plusDays(1)) {
                hours_per_month += instance.getHours4Day(dm, holidays);
            }

            assertEquals(178, hours_per_month, 0.0);
        }

        {
            LocalDate dm = new LocalDate(2010, 4, 1);
            LocalDate dm_end = dm.plusMonths(1);

            Schema_1 instance = new Schema_1(upm_38_5);
            double hours_per_month = 0;
            Holidays holidays = new AustrianHolidays();

            for (; dm.isBefore(dm_end); dm = dm.plusDays(1)) {
                hours_per_month += instance.getHours4Day(dm, holidays);
            }

            assertEquals(160.5, hours_per_month, 0.0);
        }

        {
            LocalDate dm = new LocalDate(2011, 3, 1);
            LocalDate dm_end = dm.plusMonths(1);
            Schema_1 instance = new Schema_1(upm_32);
            double hours_per_month = 0;
            Holidays holidays = new AustrianHolidays();

            for (; dm.isBefore(dm_end); dm = dm.plusDays(1)) {
                hours_per_month += instance.getHours4Day(dm, holidays);
            }

            assertEquals(149, hours_per_month, 0.0);
        }
    }

    /**
     * Test of calcMoreTime4Day method, of class Schema_1.
     */
    @Test
    public void testCalcMoreTime4Day() {
        System.out.println("getMoreTime4Day");

        List<DBTimeEntries> entries_per_day = null;
        boolean holiday = false;
        Schema_1 instance = new Schema_1(upm_38_5);

        for( TestCase test : test_cases )
        {
            System.out.println("    => " + test.toString());
            long expResult = test.expected_result_for_more_time;
            long result = instance.calcMoreTime4Day(test.entries, test.is_holiday);
            assertEquals(expResult, result);
        }
    }

}
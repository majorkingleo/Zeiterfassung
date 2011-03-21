/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Zeiterfassung.test.db.data;

import at.redeye.FrameWork.base.AutoLogger;
import at.redeye.FrameWork.base.DefaultInsertOrUpdater;
import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.base.transaction.Transaction;
import at.redeye.SqlDBInterface.SqlDBIO.impl.TableBindingNotRegisteredException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.UnsupportedDBDataTypeException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.WrongBindFileFormatException;
import at.redeye.UserManagement.bindtypes.DBPb;
import at.redeye.UserManagement.impl.UserDataHandling;
import at.redeye.Zeiterfassung.bindtypes.DBJobType;
import at.redeye.Zeiterfassung.bindtypes.DBOvertimeRule;
import at.redeye.Zeiterfassung.bindtypes.DBTimeEntries;
import at.redeye.Zeiterfassung.bindtypes.DBUserPerMonth;
import at.redeye.Zeiterfassung.test.db.SetupTestDB;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.joda.time.LocalDate;

/**
 *
 * @author martin
 */
public class CreateCommonData {

    Root root;
    Integer normal_job_type_id = null;
    Integer holiday_job_type_id = null;

    private CreateCommonData(Root root) {
        this.root = root;
    }

    private DBPb create_userdata() throws SQLException, TableBindingNotRegisteredException, UnsupportedDBDataTypeException, WrongBindFileFormatException, IOException
    {
        Transaction trans = root.getDBConnection().getDefaultTransaction();

        DBPb pb = new DBPb();

        List<DBPb> pbs = trans.fetchTable2(pb, "where " + trans.markColumn(pb.login) + "=" + "'martin.test'");

        if( !pbs.isEmpty() )
        {
            pb = pbs.get(0);
        }

        pb.locked.loadFromString("NEIN");
        pb.login.loadFromString("martin.test");
        pb.name.loadFromString("Martin");
        pb.surname.loadFromString("Schema 1 38.5");
        pb.plevel.loadFromCopy(3);
        pb.title.loadFromCopy("Ing");
        pb.pwd.loadFromString(UserDataHandling.getEncryptedPwd("test"));

        DefaultInsertOrUpdater.insertOrUpdateValuesWithPrimKey(trans, pb);

        trans.commit();

        return pb;
    }

    private void create_upmdata_for_user(DBPb pb) throws SQLException, TableBindingNotRegisteredException, UnsupportedDBDataTypeException, WrongBindFileFormatException, IOException {
        Transaction trans = root.getDBConnection().getDefaultTransaction();

        DBUserPerMonth upm = new DBUserPerMonth();
        DBTimeEntries entry = new DBTimeEntries();        

        trans.updateValues("delete from " + trans.markTable(upm) + " where " + trans.markColumn(upm.user) + "=" + pb.id.toString());
        trans.updateValues("delete from " + trans.markTable(entry) + " where " + trans.markColumn(entry.user)  + " = " + pb.id.toString());

        upm.id.loadFromCopy(0);
        upm.days_per_week.loadFromCopy(5.0);
        upm.days_holidays.loadFromCopy(25.0);
        upm.from.loadFromString("2010-01-01 00:00:00");
        upm.to.loadFromString("2010-03-31 00:00:00");
        upm.usage.loadFromCopy(100.0);
        upm.hours_per_week.loadFromCopy(38.5);
        upm.id.loadFromCopy(trans.getNewSequenceValue(upm.getName(), 1234567));
        upm.locked.loadFromString("NEIN");
        upm.overtime_rule.loadFromString(DBOvertimeRule.SCHEMAS.ÜBERSTUNDENSCHEMA_01.toString());
        upm.user.loadFromCopy(pb.id.getValue());

        DefaultInsertOrUpdater.insertOrUpdateValuesWithPrimKey(trans, upm);

        upm.id.loadFromCopy(0);
        upm.days_per_week.loadFromCopy(5.0);
        upm.days_holidays.loadFromCopy(25.0);
        upm.from.loadFromString("2010-04-01 00:00:00");
        upm.to.loadFromString("2010-12-31 00:00:00");
        upm.usage.loadFromCopy(100.0);
        upm.hours_per_week.loadFromCopy(38.5);
        upm.id.loadFromCopy(trans.getNewSequenceValue(upm.getName(), 1234567));
        upm.locked.loadFromString("NEIN");
        upm.overtime_rule.loadFromString(DBOvertimeRule.SCHEMAS.ÜBERSTUNDENSCHEMA_01.toString());
        upm.user.loadFromCopy(pb.id.getValue());

        DefaultInsertOrUpdater.insertOrUpdateValuesWithPrimKey(trans, upm);

        upm.id.loadFromCopy(0);
        upm.days_per_week.loadFromCopy(5.0);
        upm.days_holidays.loadFromCopy(20.0);
        upm.from.loadFromString("2011-04-01 00:00:00");
        upm.to.loadFromString("2020-01-01 00:00:00");
        upm.usage.loadFromCopy(100.0);
        upm.hours_per_week.loadFromCopy(38.5);
        upm.id.loadFromCopy(trans.getNewSequenceValue(upm.getName(), 1234567));
        upm.locked.loadFromString("NEIN");
        upm.overtime_rule.loadFromString(DBOvertimeRule.SCHEMAS.ÜBERSTUNDENSCHEMA_01.toString());
        upm.user.loadFromCopy(pb.id.getValue());

        DefaultInsertOrUpdater.insertOrUpdateValuesWithPrimKey(trans, upm);

        trans.commit();
    }

    private void create_normal_time_entries_for_user(DBPb pb, LocalDate from, LocalDate to, String StartAtTime, String EndTime) throws SQLException, TableBindingNotRegisteredException, UnsupportedDBDataTypeException, WrongBindFileFormatException, IOException {
        create_time_entries_for_user( pb, from, to, StartAtTime, EndTime, getNormalJobTypeId(),"Normal");
    }

    private void create_holiday_time_entries_for_user(DBPb pb, LocalDate from, LocalDate to, String StartAtTime, String EndTime) throws SQLException, TableBindingNotRegisteredException, UnsupportedDBDataTypeException, WrongBindFileFormatException, IOException {
        create_time_entries_for_user( pb, from, to, StartAtTime, EndTime, getHolidayJobTypeId(),"Urlaub");
    }

    private void create_time_entries_for_user(DBPb pb, LocalDate from, LocalDate to, String StartAtTime, String EndTime, Integer job_type, String text) throws SQLException, TableBindingNotRegisteredException, UnsupportedDBDataTypeException, WrongBindFileFormatException, IOException {
        Transaction trans = root.getDBConnection().getDefaultTransaction();

        DBTimeEntries entry = new DBTimeEntries();        

        List<DBTimeEntries> entries = new ArrayList();

        for( ; from.isBefore(to) || from.isEqual(to); from = from.plusDays(1))
        {
            DBTimeEntries e = new DBTimeEntries();

            e.user.loadFromCopy(pb.id.getValue());
            e.comment.loadFromString(text);
            e.from.loadFromCopy(from.toDateTimeAtStartOfDay().toDate());
            e.from.loadTimePart(StartAtTime);
            e.to.loadFromCopy(from.toDateTimeAtStartOfDay().toDate());
            e.to.loadTimePart(EndTime);
            e.jobtype.loadFromCopy(job_type);

            entries.add(e);
        }

        int startval = trans.getNewSequenceValues(entry.getName(), entries.size(), 1234567);

        for( DBTimeEntries e : entries )
        {
            e.id.loadFromCopy(startval++);
            trans.insertValues(e);
        }

        //trans.commit();
    }

    private void create_normal_time_entries_for_martin_test_2010( DBPb pb ) throws SQLException, TableBindingNotRegisteredException, UnsupportedDBDataTypeException, WrongBindFileFormatException, IOException
    {
        final String NORMAL_START = "09:00:00";
        final String NORMAL_END = "17:00:00";
        final String FRIDAY_END = "15:30:00";

        // Jänner 2010
        // 1 Mehrstunde, 1 Überstunde aber am Ende des Monats darf nur eine Mehrstunde überigbleiben
        create_normal_time_entries_for_user( pb, new LocalDate( 2010,1, 4), new LocalDate( 2010,1, 5), NORMAL_START, NORMAL_END );
        create_normal_time_entries_for_user( pb, new LocalDate( 2010,1, 7), new LocalDate( 2010,1, 7), NORMAL_START, NORMAL_END );
        create_normal_time_entries_for_user( pb, new LocalDate( 2010,1, 8), new LocalDate( 2010,1, 8), NORMAL_START, FRIDAY_END );

        create_normal_time_entries_for_user( pb, new LocalDate( 2010,1,11), new LocalDate( 2010,1,14), NORMAL_START, NORMAL_END );
        create_normal_time_entries_for_user( pb, new LocalDate( 2010,1,15), new LocalDate( 2010,1,15), NORMAL_START, FRIDAY_END );

        create_normal_time_entries_for_user( pb, new LocalDate( 2010,1,18), new LocalDate( 2010,1,21), NORMAL_START, NORMAL_END );
        create_normal_time_entries_for_user( pb, new LocalDate( 2010,1,22), new LocalDate( 2010,1,22), NORMAL_START, FRIDAY_END );

        create_normal_time_entries_for_user( pb, new LocalDate( 2010,1,25), new LocalDate( 2010,1,25), NORMAL_START, "18:00:00");
        create_normal_time_entries_for_user( pb, new LocalDate( 2010,1,26), new LocalDate( 2010,1,26), "05:30:00",   "13:30:00" );
        create_normal_time_entries_for_user( pb, new LocalDate( 2010,1,27), new LocalDate( 2010,1,27), NORMAL_START, NORMAL_END );
        create_normal_time_entries_for_user( pb, new LocalDate( 2010,1,28), new LocalDate( 2010,1,28), NORMAL_START, "16:00:00" );
        create_normal_time_entries_for_user( pb, new LocalDate( 2010,1,29), new LocalDate( 2010,1,29), NORMAL_START, FRIDAY_END );


        // April 2010

        create_normal_time_entries_for_user( pb, new LocalDate( 2010,4, 1), new LocalDate( 2010,4, 1), NORMAL_START, NORMAL_END );
        create_normal_time_entries_for_user( pb, new LocalDate( 2010,4, 2), new LocalDate( 2010,4, 2), NORMAL_START, FRIDAY_END );

        create_normal_time_entries_for_user( pb, new LocalDate( 2010,4, 6), new LocalDate( 2010,4, 8), NORMAL_START, NORMAL_END );
        create_normal_time_entries_for_user( pb, new LocalDate( 2010,4, 9), new LocalDate( 2010,4, 9), NORMAL_START, FRIDAY_END );

        create_normal_time_entries_for_user( pb, new LocalDate( 2010,4,12), new LocalDate( 2010,4,15), NORMAL_START, NORMAL_END );
        create_normal_time_entries_for_user( pb, new LocalDate( 2010,4,16), new LocalDate( 2010,4,16), NORMAL_START, FRIDAY_END );

        create_normal_time_entries_for_user( pb, new LocalDate( 2010,4,19), new LocalDate( 2010,4,22), NORMAL_START, NORMAL_END );
        create_normal_time_entries_for_user( pb, new LocalDate( 2010,4,23), new LocalDate( 2010,4,23), NORMAL_START, FRIDAY_END );

        create_normal_time_entries_for_user( pb, new LocalDate( 2010,4,26), new LocalDate( 2010,4,29), NORMAL_START, NORMAL_END );
        create_normal_time_entries_for_user( pb, new LocalDate( 2010,4,30), new LocalDate( 2010,4,30), NORMAL_START, FRIDAY_END );

        // Mai 2010
        // 1 Mehrstunde wird gebastelt
        create_normal_time_entries_for_user( pb, new LocalDate( 2010,5, 3), new LocalDate( 2010,5, 6), NORMAL_START, NORMAL_END );
        create_normal_time_entries_for_user( pb, new LocalDate( 2010,5, 7), new LocalDate( 2010,5, 7), NORMAL_START, FRIDAY_END );

        create_normal_time_entries_for_user( pb, new LocalDate( 2010,5,10), new LocalDate( 2010,5,12), NORMAL_START, NORMAL_END );
        create_normal_time_entries_for_user( pb, new LocalDate( 2010,5,14), new LocalDate( 2010,5,14), NORMAL_START, FRIDAY_END );

        create_normal_time_entries_for_user( pb, new LocalDate( 2010,5,17), new LocalDate( 2010,5,20), NORMAL_START, NORMAL_END );
        create_normal_time_entries_for_user( pb, new LocalDate( 2010,5,21), new LocalDate( 2010,5,21), NORMAL_START, FRIDAY_END );

        create_normal_time_entries_for_user( pb, new LocalDate( 2010,5,25), new LocalDate( 2010,5,27), NORMAL_START, NORMAL_END );
        create_normal_time_entries_for_user( pb, new LocalDate( 2010,5,28), new LocalDate( 2010,5,28), NORMAL_START, FRIDAY_END );

        create_normal_time_entries_for_user( pb, new LocalDate( 2010,5,31), new LocalDate( 2010,5,31), NORMAL_START, "18:00:00" );

        // Juni 2010
        // 1 Überstunde wird gebastelt
        create_normal_time_entries_for_user( pb, new LocalDate( 2010,6, 1), new LocalDate( 2010,6, 2), NORMAL_START, NORMAL_END );
        create_normal_time_entries_for_user( pb, new LocalDate( 2010,6, 4), new LocalDate( 2010,6, 4), NORMAL_START, FRIDAY_END );

        create_normal_time_entries_for_user( pb, new LocalDate( 2010,6, 7), new LocalDate( 2010,6,10), NORMAL_START, NORMAL_END );
        create_normal_time_entries_for_user( pb, new LocalDate( 2010,6,11), new LocalDate( 2010,6,11), NORMAL_START, FRIDAY_END );

        create_normal_time_entries_for_user( pb, new LocalDate( 2010,6,14), new LocalDate( 2010,6,17), NORMAL_START, NORMAL_END );
        create_normal_time_entries_for_user( pb, new LocalDate( 2010,6,18), new LocalDate( 2010,6,18), NORMAL_START, FRIDAY_END );

        create_normal_time_entries_for_user( pb, new LocalDate( 2010,6,21), new LocalDate( 2010,6,24), NORMAL_START, NORMAL_END );
        create_normal_time_entries_for_user( pb, new LocalDate( 2010,6,25), new LocalDate( 2010,6,25), NORMAL_START, FRIDAY_END );
        create_normal_time_entries_for_user( pb, new LocalDate( 2010,6,26), new LocalDate( 2010,6,26), "10:00:00", "11:00:00" );
        
        create_normal_time_entries_for_user( pb, new LocalDate( 2010,6,28), new LocalDate( 2010,6,30), NORMAL_START, NORMAL_END );


        // Juli 2010
        // 1 Urlaubstag wird gebastelt
        create_normal_time_entries_for_user( pb, new LocalDate( 2010,7, 1), new LocalDate( 2010,7, 1), NORMAL_START, NORMAL_END );
        create_normal_time_entries_for_user( pb, new LocalDate( 2010,7, 2), new LocalDate( 2010,7, 2), NORMAL_START, FRIDAY_END );

        create_normal_time_entries_for_user( pb, new LocalDate( 2010,7, 5), new LocalDate( 2010,7, 8), NORMAL_START, NORMAL_END );
        create_normal_time_entries_for_user( pb, new LocalDate( 2010,7, 9), new LocalDate( 2010,7, 9), NORMAL_START, FRIDAY_END );

        create_normal_time_entries_for_user( pb, new LocalDate( 2010,7,12), new LocalDate( 2010,7,15), NORMAL_START, NORMAL_END );
        create_normal_time_entries_for_user( pb, new LocalDate( 2010,7,16), new LocalDate( 2010,7,16), NORMAL_START, FRIDAY_END );

        create_normal_time_entries_for_user( pb, new LocalDate( 2010,7,19), new LocalDate( 2010,7,22), NORMAL_START, NORMAL_END );
        create_normal_time_entries_for_user( pb, new LocalDate( 2010,7,23), new LocalDate( 2010,7,23), NORMAL_START, FRIDAY_END );

        create_normal_time_entries_for_user( pb, new LocalDate( 2010,7,26), new LocalDate( 2010,7,29), NORMAL_START, NORMAL_END );
        create_holiday_time_entries_for_user( pb, new LocalDate( 2010,7,30), new LocalDate( 2010,7,30), NORMAL_START, FRIDAY_END );

        // August 2010
        // Es wird zu wenig gearbeitet, sprich ZA genommen
        create_normal_time_entries_for_user( pb, new LocalDate( 2010,8, 2), new LocalDate( 2010,8, 5), NORMAL_START, NORMAL_END );
        create_normal_time_entries_for_user( pb, new LocalDate( 2010,8, 6), new LocalDate( 2010,8, 6), NORMAL_START, FRIDAY_END );

        create_normal_time_entries_for_user( pb, new LocalDate( 2010,8, 9), new LocalDate( 2010,8, 12), NORMAL_START, NORMAL_END );
        create_normal_time_entries_for_user( pb, new LocalDate( 2010,8,13), new LocalDate( 2010,8, 13), NORMAL_START, FRIDAY_END );

        create_normal_time_entries_for_user( pb, new LocalDate( 2010,8,16), new LocalDate( 2010,8,19), NORMAL_START, NORMAL_END );
        create_normal_time_entries_for_user( pb, new LocalDate( 2010,8,20), new LocalDate( 2010,8,20), NORMAL_START, FRIDAY_END );

        create_normal_time_entries_for_user( pb, new LocalDate( 2010,8,23), new LocalDate( 2010,8,26), NORMAL_START, NORMAL_END );
        create_normal_time_entries_for_user( pb, new LocalDate( 2010,8,27), new LocalDate( 2010,8,27), NORMAL_START, FRIDAY_END );

        create_normal_time_entries_for_user( pb, new LocalDate( 2010,8,30), new LocalDate( 2010,8,31), NORMAL_START, "16:00:00" );


        root.getDBConnection().getDefaultTransaction().commit();
    }

    private void create_normal_time_entries_for_martin_test_2011( DBPb pb ) throws SQLException, TableBindingNotRegisteredException, UnsupportedDBDataTypeException, WrongBindFileFormatException, IOException
    {
        final String NORMAL_START = "10:00:00";
        final String NORMAL_END = "18:00:00";
        final String FRIDAY_END = "16:30:00";

        // April 2011

        create_normal_time_entries_for_user(pb, new LocalDate( 2011,4, 1), new LocalDate( 2011,4, 1), NORMAL_START, FRIDAY_END );

        create_holiday_time_entries_for_user(pb, new LocalDate( 2011,4, 4), new LocalDate( 2011,4, 7), NORMAL_START, NORMAL_END );
        create_holiday_time_entries_for_user(pb, new LocalDate( 2011,4, 8), new LocalDate( 2011,4, 8), NORMAL_START, FRIDAY_END );

        create_normal_time_entries_for_user(pb, new LocalDate( 2011,4,11), new LocalDate( 2011,4,11), NORMAL_START, NORMAL_END );
        create_normal_time_entries_for_user(pb, new LocalDate( 2011,4,12), new LocalDate( 2011,4,12), "06:00:00",   "14:00:00" );
        create_normal_time_entries_for_user(pb, new LocalDate( 2011,4,13), new LocalDate( 2011,4,13), NORMAL_START, NORMAL_END );
        create_normal_time_entries_for_user(pb, new LocalDate( 2011,4,14), new LocalDate( 2011,4,14), "14:00:00",   "22:00:00" );
        create_normal_time_entries_for_user(pb, new LocalDate( 2011,4,15), new LocalDate( 2011,4,15), NORMAL_START, FRIDAY_END );

        create_normal_time_entries_for_user(pb, new LocalDate( 2011,4,18), new LocalDate( 2011,4,21), NORMAL_START, NORMAL_END );
        create_normal_time_entries_for_user(pb, new LocalDate( 2011,4,22), new LocalDate( 2011,4,22), NORMAL_START, FRIDAY_END );

        create_normal_time_entries_for_user(pb, new LocalDate( 2011,4,26), new LocalDate( 2011,4,28), NORMAL_START, NORMAL_END );
        create_normal_time_entries_for_user(pb, new LocalDate( 2011,4,29), new LocalDate( 2011,4,29), NORMAL_START, "17:30:00" );

        root.getDBConnection().getDefaultTransaction().commit();
    }

    public Integer getNormalJobTypeId() throws SQLException, TableBindingNotRegisteredException, UnsupportedDBDataTypeException, WrongBindFileFormatException
    {
        if( normal_job_type_id != null )
            return normal_job_type_id;

        Transaction trans = root.getDBConnection().getDefaultTransaction();

        DBJobType jt = new DBJobType();


        List<DBJobType> jts = trans.fetchTable2(jt, "where " + trans.markColumn(jt.name) + "= 'Normal' and " + trans.markColumn(jt.locked) + "=0" );

        if( jts.isEmpty() )
            return null;

        normal_job_type_id = jts.get(0).id.getValue();

        return normal_job_type_id;
    }

    public Integer getHolidayJobTypeId() throws SQLException, TableBindingNotRegisteredException, UnsupportedDBDataTypeException, WrongBindFileFormatException
    {
        if( holiday_job_type_id != null )
            return holiday_job_type_id;

        Transaction trans = root.getDBConnection().getDefaultTransaction();

        DBJobType jt = new DBJobType();


        List<DBJobType> jts = trans.fetchTable2(jt, "where " + trans.markColumn(jt.is_holliday) + "= 'JA' and " + trans.markColumn(jt.locked) + "=0" );

        if( jts.isEmpty() )
            return null;

        holiday_job_type_id = jts.get(0).id.getValue();

        return holiday_job_type_id;
    }

    public static void main( String[] args )
    {
        new AutoLogger(CreateCommonData.class.getName()) {

            @Override
            public void do_stuff() throws Exception {
                SetupTestDB setup = new SetupTestDB();


                setup.invoke();

                final CreateCommonData data_inserter = new CreateCommonData(setup.root);
                DBPb pb = data_inserter.create_userdata();
                data_inserter.create_upmdata_for_user(pb);
                data_inserter.create_normal_time_entries_for_martin_test_2010(pb);
                data_inserter.create_normal_time_entries_for_martin_test_2011(pb);
            }
        };

    }

}

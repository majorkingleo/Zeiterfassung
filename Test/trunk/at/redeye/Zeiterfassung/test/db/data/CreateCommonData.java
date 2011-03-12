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

        upm.days_holidays.loadFromCopy(0.0);
        upm.days_per_week.loadFromCopy(5.0);
        upm.from.loadFromString("2010-04-01 00:00:00");
        upm.to.loadFromString("2010-04-30 00:00:00");
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
        Transaction trans = root.getDBConnection().getDefaultTransaction();

        DBTimeEntries entry = new DBTimeEntries();        

        List<DBTimeEntries> entries = new ArrayList();

        for( ; from.isBefore(to) || from.isEqual(to); from = from.plusDays(1))
        {
            DBTimeEntries e = new DBTimeEntries();

            e.user.loadFromCopy(pb.id.getValue());
            e.comment.loadFromString("test");
            e.from.loadFromCopy(from.toDateTimeAtStartOfDay().toDate());
            e.from.loadTimePart(StartAtTime);
            e.to.loadFromCopy(from.toDateTimeAtStartOfDay().toDate());
            e.to.loadTimePart(EndTime);
            e.jobtype.loadFromCopy(getNormalJobTypeId());

            entries.add(e);
        }

        int startval = trans.getNewSequenceValues(entry.getName(), entries.size(), 1234567);

        for( DBTimeEntries e : entries )
        {
            e.id.loadFromCopy(startval++);
            trans.insertValues(e);
        }

        trans.commit();
    }

    private void create_normal_time_entries_for_april_2010( DBPb pb ) throws SQLException, TableBindingNotRegisteredException, UnsupportedDBDataTypeException, WrongBindFileFormatException, IOException
    {
        final String NORMAL_START = "09:00:00";
        final String NORMAL_END = "17:00:00";
        final String FRIDAY_END = "15:30:00";

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

        return jts.get(0).id.getValue();
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
                data_inserter.create_normal_time_entries_for_april_2010(pb);
            }
        };

    }

}

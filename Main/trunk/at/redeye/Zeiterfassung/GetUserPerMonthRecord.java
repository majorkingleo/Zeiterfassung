/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Zeiterfassung;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.joda.time.DateMidnight;

import at.redeye.FrameWork.base.bindtypes.DBStrukt;
import at.redeye.FrameWork.base.transaction.Transaction;
import at.redeye.SqlDBInterface.SqlDBIO.impl.TableBindingNotRegisteredException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.UnsupportedDBDataTypeException;
import at.redeye.SqlDBInterface.SqlDBIO.impl.WrongBindFileFormatException;
import at.redeye.Zeiterfassung.bindtypes.DBUserPerMonth;
import org.joda.time.LocalDate;

/**
 * 
 * @author martin
 */
public class GetUserPerMonthRecord {

	private static Logger logger = Logger
			.getLogger(GetUserPerMonthRecord.class);

	public static class SameTriple<Index, Content> {
		private Index a, b, c;
		private Content content;

		SameTriple(Index a, Index b, Index c, Content content) {
			this.a = a;
			this.b = b;
			this.c = c;
			this.content = content;
		}

		public boolean equals(SameTriple<Index, Content> other) {
			if (other.a.equals(a) && other.b.equals(b) && other.c.equals(c)) {
				return true;
			}

			return false;
		}

		public boolean equals(Integer aa, Integer bb, Integer cc) {
			if (aa.equals(a) && bb.equals(b) && cc.equals(c)) {
				return true;
			}

			return false;
		}

		public Content getContent() {
			return content;
		}
	}

	Vector<SameTriple<Integer, DBUserPerMonth>> hashed_content = new Vector<SameTriple<Integer, DBUserPerMonth>>();

	public DBUserPerMonth getValidRecordForMonthCached(Transaction trans,
			Integer userId, Integer year, Integer month) throws SQLException,
			SQLException, TableBindingNotRegisteredException,
			UnsupportedDBDataTypeException, WrongBindFileFormatException,
			DuplicateRecordException {
		for (SameTriple<Integer, DBUserPerMonth> triple : hashed_content) {
			if (triple.equals(userId, year, month))
				return triple.getContent();
		}

		DBUserPerMonth upm = getValidRecordForMonth(trans, userId, year, month);

		if (upm != null)
			hashed_content.add(new SameTriple<Integer, DBUserPerMonth>(userId,
					year, month, upm));

		return upm;
	}

	public static DBUserPerMonth getValidRecordForMonth(Transaction trans,
			int userId, int year, int month) throws SQLException, SQLException,
			TableBindingNotRegisteredException, UnsupportedDBDataTypeException,
			WrongBindFileFormatException, DuplicateRecordException {

		DBUserPerMonth upm = new DBUserPerMonth();

		DateMidnight dm_from = new DateMidnight(year, month, 1);

		List<DBStrukt> res = trans
				.fetchTable(
						new DBUserPerMonth(),
						"where "
								+ trans.markColumn(upm.user)
								+ "="
								+ userId
								+ " and "
								+ trans.markColumn(upm.locked)
								+ "='NEIN'"
								+ " and "
								+ trans.getLowerDate(upm.from, dm_from)
								+ " and "
								+ " ( "
								+ trans.getHigherDate(upm.to, dm_from)
								+ "    or "
								+ trans.getDayStmt(upm.to, new DateMidnight(0L))
								+ " )");

		if (res.size() != 1)
			logger.info("res: " + res.size() + " " + trans.getSql());

		if (res.size() == 0) {
			// gibt es einen 1.1.1970 Eintrag?

			dm_from = new DateMidnight(0L);

			res = trans.fetchTable(
					new DBUserPerMonth(),
					"where " + trans.markColumn(upm.user) + "=" + userId
							+ " and " + trans.markColumn(upm.locked)
							+ "='NEIN'" + " and "
							+ trans.getPeriodStmt(upm.from, upm.to, dm_from));

			logger.info("retry: " + res.size() + " " + trans.getSql());
		}

		if (res.size() != 1) {
			if (res.size() == 0) {
				logger.warn("Keine Eintrag für den Zeitraum gefunden");
				return null;
			} else {

                LocalDate from = new LocalDate( year, month, 1);
                LocalDate to   = from.plusMonths(1).minusDays(1);

                DBUserPerMonth matching_upm = null;

                for( DBStrukt rec : res )
                {
                    DBUserPerMonth u = (DBUserPerMonth) rec;

                    LocalDate uf = new LocalDate( u.from.getValue() );
                    LocalDate ut = new LocalDate( u.to.getValue() );

                    if( ( from.isBefore(uf) || from.isEqual(uf) ) &&
                        ( to.isBefore(ut) || to.isEqual(ut)) )
                    {
                        if( matching_upm != null )
                        {
                            String message = "Mehr als einen Eintrag für den Benutzer "
                                + userId + " gefunden " + "( " + year + "-" + month
                                + "-" + "01 )";

                            logger.error(message);
                            logger.error("Liste der Einträge:");
                            logger.error("Sql: " + trans.getSql());

                            for (int i = 0; i < res.size(); i++) {
                                DBUserPerMonth r = (DBUserPerMonth) res.get(i);

                                logger.error((i + 1) + "UPM Id: " + r.id + " from: "
                                    + r.from.getDateStr() + " to: "
                                    + r.to.getDateStr());
                            }

                            throw new DuplicateRecordException(message);
                        }
                        else
                        {
                            matching_upm = u;
                        }
                    }  // if
                }

                if (matching_upm == null) {
                    logger.warn("Keine Eintrag für den Zeitraum gefunden");
                    return null;
                } else {
                    logger.info("UPM Id: " + matching_upm.id + " from: " + matching_upm.from.getDateStr()
                        + " to: " + matching_upm.to.getDateStr() + " holiday hours "
                        + matching_upm.hours_holidays);
                }
			}
		} else if (res.size() == 1) {

			DBUserPerMonth rec = (DBUserPerMonth) res.get(0);
			logger.info("UPM Id: " + rec.id + " from: " + rec.from.getDateStr()
					+ " to: " + rec.to.getDateStr() + " holiday hours "
					+ rec.hours_holidays);
		}

		return (DBUserPerMonth) res.get(0);
	}

}

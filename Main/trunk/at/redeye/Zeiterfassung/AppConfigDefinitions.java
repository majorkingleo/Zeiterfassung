/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.Zeiterfassung;

import at.redeye.FrameWork.base.prm.PrmDefaultChecksInterface;
import at.redeye.FrameWork.base.prm.impl.GlobalConfigDefinitions;
import at.redeye.FrameWork.base.prm.impl.LocalConfigDefinitions;
import at.redeye.FrameWork.base.prm.bindtypes.DBConfig;
import at.redeye.FrameWork.base.prm.impl.PrmDefaultCheckSuite;
import org.apache.log4j.Level;

/**
 *
 * @author martin
 */
public class AppConfigDefinitions {

    public static DBConfig HoursPerWeek = new DBConfig("Wochenstunden", "38.5", "Voeingestellte Wochenstundenanzahl", new PrmDefaultCheckSuite(PrmDefaultChecksInterface.PRM_IS_DOUBLE));
    public static DBConfig NormalWorkPercent = new DBConfig("Normarbeits-Pensum", "100", "Voreingestellter Normalarbeitszeitanteil in Prozent", new PrmDefaultCheckSuite(PrmDefaultChecksInterface.PRM_IS_DOUBLE));
    public static DBConfig NormalWorkTimeStart = new DBConfig("Normalarbeitszeitbeginn", "09:00", "Vorbelegter Wert für den Beginn der Normalarbeitszeit");
    public static DBConfig NormalWorkTimeStop = new DBConfig("Normalarbeitszeitende", "17:00", "Vorbelegter Wert für das Ende der Normalarbeitszeit");
    public static DBConfig DaysPerWeek = new DBConfig("Arbeitstage pro Woche", "5", "Vorbelegter Wert für die Anzahl der zu arbeintenden Tage pro Woche.", new PrmDefaultCheckSuite(PrmDefaultChecksInterface.PRM_IS_LONG));
    public static DBConfig NumberOfMinimumCommentChars = new DBConfig("Minimalekommentareingabenlänge", "4", "Minimale Anzahl an Zeichen, die der Benutzer bei " +
            "einem Zeiteintrag eingeben muß. Null bedeuted das kein Kommentar eingegeben werden muß.", new PrmDefaultCheckSuite(PrmDefaultChecksInterface.PRM_IS_LONG));
    public static DBConfig AutoCreateSupProjectDefaultValue = new DBConfig("Standardwert für das Automatische anlengen von Unterprojekten", "JA",
            "Der Voreingestellte Wert bei neu angelegt Projekten.", new PrmDefaultCheckSuite(PrmDefaultChecksInterface.PRM_IS_TRUE_FALSE));
    public static DBConfig DoLogging = new DBConfig("Log-Meldungen Schreiben", "NEIN", "Sollen Logmeldungn in einer LogDatei mitgeschrieben werden.", new PrmDefaultCheckSuite(PrmDefaultChecksInterface.PRM_IS_TRUE_FALSE));
    public static DBConfig LoggingDir = new DBConfig("Log-Verzeichnis", "", "Verzeichnis in das die Logdateien geschrieben werden sollen.");
    private static String[] validLevels = {"MML", Level.DEBUG.toString(), Level.TRACE.toString(), Level.ALL.toString(), Level.INFO.toString()};
    public static DBConfig LoggingLevel = new DBConfig("Log-Level", "DEBUG", "Schwellwert für die Informationen in der Logdatei.", new PrmDefaultCheckSuite(PrmDefaultChecksInterface.PRM_HAS_VALUE), validLevels);

    public static void registerDefinitions() {
        add(HoursPerWeek);
        add(NormalWorkPercent);
        add(NormalWorkTimeStart);
        add(NormalWorkTimeStop);
        add(DaysPerWeek);
        add(NumberOfMinimumCommentChars);
        add(AutoCreateSupProjectDefaultValue);

        addLocal(DoLogging);
        addLocal(LoggingDir);
        addLocal(LoggingLevel);

        GlobalConfigDefinitions.add_help_path("/at/redeye/Zeiterfassung/resources/Help/Params/");
        LocalConfigDefinitions.add_help_path("/at/redeye/Zeiterfassung/resources/Help/Params/");
    }

    static void add(String name, String value, String descr) {
        GlobalConfigDefinitions.add(new DBConfig(name, value, descr));
    }

    static void add(DBConfig c) {
        GlobalConfigDefinitions.add(c);
    }

    static void addLocal(DBConfig c) {
        LocalConfigDefinitions.add(c);
    }
}

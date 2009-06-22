/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Zeiterfassung;

import at.redeye.FrameWork.base.GlobalConfigDefinitions;
import at.redeye.FrameWork.base.LocalConfigDefinitions;
import at.redeye.FrameWork.base.bindtypes.DBConfig;

/**
 *
 * @author martin
 */
public class AppConfigDefinitions {
    
    public static DBConfig HoursPerWeek = new DBConfig("Wochenstunden", "38.5", "Voeingestellte Wochenstundenanzahl" );
    public static DBConfig NormalWorkPercent = new DBConfig("Normarbeits-Pensum", "100", "Voreingestellter Normalarbeitszeitanteil in Prozent" );
    public static DBConfig NormalWorkTimeStart = new DBConfig("Normalarbeitszeitbeginn", "09:00", "Vorbelegter Wert für den Beginn der Normalarbeitszeit" );
    public static DBConfig NormalWorkTimeStop = new DBConfig("Normalarbeitszeitende", "17:00", "Vorbelegter Wert für das Ende der Normalarbeitszeit" );
    public static DBConfig DaysPerWeek = new DBConfig("Arbeitstage pro Woche", "5", "Vorbelegter Wert für die Anzahl der zu arbeintenden Tage pro Woche." );
    
    public static DBConfig DoLogging = new DBConfig("Log-Meldungen Schreiben", "NEIN", "Sollen Logmeldungn in einer LogDatei mitgeschrieben werden.");
    public static DBConfig LoggingDir = new DBConfig("Log-Verzeichnis", "", "Verzeichnis in das die Logdateien geschrieben werden sollen.");
    public static DBConfig LoggingLevel = new DBConfig("Log-Level", "DEBUG", "Schwellwert für die Informationen in der Logdatei.");
    
    public static void registerDefinitions()
    {
       add(HoursPerWeek);
       add(NormalWorkPercent);
       add(NormalWorkTimeStart);
       add(NormalWorkTimeStop);            
       add(DaysPerWeek);
       
       addLocal(DoLogging);
       addLocal(LoggingDir);
       addLocal(LoggingLevel);
       
       GlobalConfigDefinitions.add_help_path("/at/redeye/Zeiterfassung/resources/Help/Params/");
       LocalConfigDefinitions.add_help_path("/at/redeye/Zeiterfassung/resources/Help/Params/");
    }
    
    
    static void add( String name, String value, String descr )
    {
        GlobalConfigDefinitions.add(new DBConfig(name,value,descr));
    }
    
    static void add( DBConfig c )
    {
        GlobalConfigDefinitions.add(c);
    }        

    
    static void addLocal( DBConfig c )
    {
        LocalConfigDefinitions.add(c);
    }        

}

package at.redeye.Zeiterfassung;

import at.redeye.FrameWork.base.AutoLogger;
import java.io.IOException;

import javax.swing.JOptionPane;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

import at.redeye.FrameWork.base.FrameWorkConfigDefinitions;
import at.redeye.FrameWork.base.LocalRoot;
import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.base.prm.bindtypes.DBConfig;
import at.redeye.FrameWork.base.prm.impl.PrmDBInit;
import at.redeye.FrameWork.base.sequence.bindtypes.DBSequences;
import at.redeye.FrameWork.base.transaction.Transaction;
import at.redeye.FrameWork.utilities.StringUtils;
import at.redeye.UserManagement.UserManagementDialogs;
import at.redeye.UserManagement.UserManagementInterface;
import at.redeye.UserManagement.bindtypes.DBPb;
import at.redeye.UserManagement.impl.UserDataHandling;
import at.redeye.Zeiterfassung.bindtypes.DBCustomerAddresses;
import at.redeye.Zeiterfassung.bindtypes.DBCustomers;
import at.redeye.Zeiterfassung.bindtypes.DBExtraHolidays;
import at.redeye.Zeiterfassung.bindtypes.DBJobType;
import at.redeye.Zeiterfassung.bindtypes.DBMonthBlocks;
import at.redeye.Zeiterfassung.bindtypes.DBProjects;
import at.redeye.Zeiterfassung.bindtypes.DBSubProjects;
import at.redeye.Zeiterfassung.bindtypes.DBTimeEntries;
import at.redeye.Zeiterfassung.bindtypes.DBUserPerMonth;

public class ModuleLauncher implements at.redeye.UserManagement.UserManagementListener {

    private static Root root = null;
    private UserManagementInterface um = null;
    private static Logger logger = Logger.getRootLogger();
    private boolean first_run = true;

    public ModuleLauncher() {

        String name = "MOMM";

        if (at.redeye.Dongle.AppliactionModes.getAppliactionModes().isDemoVersion()) {
            name += "-dev";
        }

        root = new LocalRoot(name);
        um = new UserDataHandling(root);
    }

    public void relogin()
    {
        root.closeAllWindowsNoAppExit();
        um.setAutoLogin(false);
        invoke();
    }

    protected void invoke() {

        try {
            root.loadDBConnectionFromSetup();
        } catch (NoClassDefFoundError ex) {
            System.out.println(ex);
        }

        AppConfigDefinitions.registerDefinitions();
        FrameWorkConfigDefinitions.registerDefinitions();
        root.getBindtypeManager().register(new DBPb());
        root.getBindtypeManager().register(new DBSequences());
        root.getBindtypeManager().register(new DBTimeEntries());
        root.getBindtypeManager().register(new DBJobType());
        root.getBindtypeManager().register(new DBUserPerMonth());
        root.getBindtypeManager().register(new DBConfig());
        root.getBindtypeManager().register(new DBExtraHolidays());
        root.getBindtypeManager().register(new DBMonthBlocks());
        root.getBindtypeManager().register(new DBCustomers());
        root.getBindtypeManager().register(new DBCustomerAddresses());
        root.getBindtypeManager().register(new DBProjects());
        root.getBindtypeManager().register(new DBSubProjects());

        configureLogging();

        PrmDBInit prmDBInit =  new PrmDBInit(root);
        prmDBInit.initDb();

        if( first_run )
            um.addUMListener(this);

        if (um.tryAutoLogin() == false) {
            
            if (at.redeye.Dongle.AppliactionModes.getAppliactionModes().isDemoVersion()) {
                um.setLogo("/at/redeye/Zeiterfassung/resources/icons/redeye15b-dev.png");
            } else {
                um.setLogo("/at/redeye/Zeiterfassung/resources/icons/redeye15b.png");
            }

            um.requestDialog(UserManagementDialogs.UM_LOGIN_DIALOG);
        }

        first_run = false;

    }

    @Override
    public void accessGranted() {

        // Now the user is known - apply allowed value to AutoLoginUser-PRM
        String [] autoLoginValues = {root.getLogin(), ""}; // myself or nothing
        root.getSetup().getLocalConfig(FrameWorkConfigDefinitions.AutoLoginUser.getConfigName()).setPossibleValues(autoLoginValues);


        // Extend existing logfile-name to format log.OS-XX-APPL-XX
        RollingFileAppender fileAppender = (RollingFileAppender) logger.getAppender(RollingFileAppender.class.getSimpleName());

        if (fileAppender != null) {
            try {

                String filename = fileAppender.getFile().split(".txt")[0] + "-APPL-" + root.getLogin() + ".txt";
                System.out.println("FileName: " + filename);
                logger.removeAppender(RollingFileAppender.class.getSimpleName());
                RollingFileAppender rfa = new RollingFileAppender(fileAppender.getLayout(), filename);
                logger.addAppender(rfa);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(
                        null,
                        StringUtils.autoLineBreak("Der Logger konnte nicht korrekt initialisiert werden!"),
                        "User Management", JOptionPane.WARNING_MESSAGE);
            }

        }

        checkTableVersions();

        // Don't start as thread, because BaseDialog attempts wrong closing of
        // application
        new MainWin(root).setVisible(true);

    }

    private void checkTableVersions() {
        new AutoLogger(ModuleLauncher.class.getCanonicalName()) {

            @Override
            public void do_stuff() throws Exception {

                Transaction trans = root.getDBConnection().getDefaultTransaction();

                if (trans.isOpen()) {
                    root.getBindtypeManager().setTransaction(trans);
                    root.getBindtypeManager().check_table_versions_with_message(root.getUserPermissionLevel());
                }
            }
        };
    }

    private void configureLogging() {

        PatternLayout layout = new PatternLayout(
                "%d{ISO8601} %-5p (%F:%L): %m%n");
        ConsoleAppender consoleAppender = new ConsoleAppender(layout);

        String logFileDir = root.getSetup().getLocalConfig(
                AppConfigDefinitions.LoggingDir);
        System.out.println("logFileDir: " + logFileDir);
        String logFileLevel = root.getSetup().getLocalConfig(
                AppConfigDefinitions.LoggingLevel);
        String loggingEnabled = root.getSetup().getLocalConfig(
                AppConfigDefinitions.DoLogging);

        String filename = logFileDir + (logFileDir.isEmpty() ? "" : "/") + "log.OS-" + System.getProperty("user.name", "unknown-user") + ".txt";

        System.out.println("Filename: " + filename);

        logger.setLevel(Level.toLevel(logFileLevel));
        logger.addAppender(consoleAppender);

        if (loggingEnabled.equalsIgnoreCase("ja") ||
                loggingEnabled.equalsIgnoreCase("yes") ||
                loggingEnabled.equalsIgnoreCase("true")) {

            try {

                RollingFileAppender fileAppender = new RollingFileAppender(
                        layout, filename);
                fileAppender.setAppend(true);
                fileAppender.setMaxFileSize("3MB");
                fileAppender.setName(RollingFileAppender.class.getSimpleName());

                logger.addAppender(fileAppender);

            } catch (IOException e) {
                JOptionPane.showMessageDialog(
                        null,
                        StringUtils.autoLineBreak("Das Logger konnte nicht korrekt initialisiert werden!"),
                        "User Management", JOptionPane.WARNING_MESSAGE);

            }
        }

    }
}

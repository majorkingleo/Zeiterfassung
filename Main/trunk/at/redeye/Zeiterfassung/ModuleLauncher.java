package at.redeye.Zeiterfassung;

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
import at.redeye.FrameWork.base.bindtypes.DBConfig;
import at.redeye.FrameWork.base.sequence.bindtypes.DBSequences;
import at.redeye.FrameWork.utilities.StringUtils;
import at.redeye.UserManagement.UserManagementDialogs;
import at.redeye.UserManagement.UserManagementInterface;
import at.redeye.UserManagement.bindtypes.DBPb;
import at.redeye.UserManagement.impl.UserDataHandling;
import at.redeye.Zeiterfassung.bindtypes.DBExtraHolidays;
import at.redeye.Zeiterfassung.bindtypes.DBJobType;
import at.redeye.Zeiterfassung.bindtypes.DBMonthBlocks;
import at.redeye.Zeiterfassung.bindtypes.DBTimeEntries;
import at.redeye.Zeiterfassung.bindtypes.DBUserPerMonth;

public class ModuleLauncher implements at.redeye.UserManagement.UserManagementListener {

	private static Root root = new LocalRoot("MOMM");
	private UserManagementInterface um = new UserDataHandling(root);
	private static Logger logger = Logger.getRootLogger();

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

		configureLogging();

		um.addUMListener(this);
		um.requestDialog(UserManagementDialogs.UM_LOGIN_DIALOG);

	}

	@Override
	public void accessGranted() {

		// Extend existing logfile-name to format log.OS-XX-APPL-XX
		RollingFileAppender fileAppender = (RollingFileAppender) logger
				.getAppender(RollingFileAppender.class.getSimpleName());

		if (fileAppender != null) {
			try {

				String filename = fileAppender.getFile().split(".txt")[0]
						+ "-APPL-" + root.getLogin() + ".txt";
				System.out.println("FileName: " + filename);
				logger
						.removeAppender(RollingFileAppender.class
								.getSimpleName());
				RollingFileAppender rfa = new RollingFileAppender(fileAppender
						.getLayout(), filename);
				logger.addAppender(rfa);
			} catch (IOException e) {
				JOptionPane
						.showMessageDialog(
								null,
								StringUtils
										.autoLineBreak("Das Logger konnte nicht korrekt initialisiert werden!"),
								"User Management", JOptionPane.WARNING_MESSAGE);
			}

		}

		// Don't start as thread, because BaseDialog attempts wrong closing of
		// application
		new MainWin(root).setVisible(true);

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

		String filename = logFileDir + (logFileDir.isEmpty() ? "" : "/")
				+ "log.OS-" + System.getProperty("user.name", "unknown-user")
				+ ".txt";

		System.out.println("Filename: " + filename);

		logger.setLevel(Level.toLevel(logFileLevel));
		logger.addAppender(consoleAppender);

		if (loggingEnabled.equalsIgnoreCase("ja")) {

			try {

				RollingFileAppender fileAppender = new RollingFileAppender(
						layout, filename);
				fileAppender.setAppend(true);
				fileAppender.setMaxFileSize("3MB");
				fileAppender.setName(RollingFileAppender.class.getSimpleName());

				logger.addAppender(fileAppender);

			} catch (IOException e) {
				JOptionPane
						.showMessageDialog(
								null,
								StringUtils
										.autoLineBreak("Das Logger konnte nicht korrekt initialisiert werden!"),
								"User Management", JOptionPane.WARNING_MESSAGE);

			}
		}

	}
}
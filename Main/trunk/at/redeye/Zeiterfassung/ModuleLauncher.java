package at.redeye.Zeiterfassung;

import at.redeye.FrameWork.base.prm.PrmCustomChecksInterface;
import at.redeye.FrameWork.base.prm.PrmDefaultChecksInterface;
import at.redeye.FrameWork.base.prm.impl.PrmActionEvent;
import at.redeye.Setup.ConfigCheck.CheckConfigBase;
import at.redeye.FrameWork.base.BaseModuleLauncher;
import java.io.IOException;

import javax.swing.JOptionPane;

import org.apache.log4j.RollingFileAppender;

import at.redeye.FrameWork.base.FrameWorkConfigDefinitions;
import at.redeye.FrameWork.base.LocalRoot;
import at.redeye.FrameWork.base.prm.PrmListener;
import at.redeye.FrameWork.base.prm.bindtypes.DBConfig;
import at.redeye.FrameWork.base.prm.impl.PrmDBInit;
import at.redeye.FrameWork.base.prm.impl.PrmDefaultCheckSuite;
import at.redeye.FrameWork.base.sequence.bindtypes.DBSequences;
import at.redeye.FrameWork.base.wizards.impl.WizardListener;
import at.redeye.FrameWork.utilities.StringUtils;
import at.redeye.FrameWork.widgets.StartupWindow;
import at.redeye.UserManagement.UserManagementDialogs;
import at.redeye.UserManagement.UserManagementInterface;
import at.redeye.UserManagement.bindtypes.DBPb;
import at.redeye.UserManagement.impl.UserDataHandling;
import at.redeye.Zeiterfassung.ConfigWizard.CheckConfig;
import at.redeye.Zeiterfassung.ConfigWizard.ConfigWizard;
import at.redeye.Zeiterfassung.bindtypes.DBCustomerAddresses;
import at.redeye.Zeiterfassung.bindtypes.DBCustomers;
import at.redeye.Zeiterfassung.bindtypes.DBExtraHolidays;
import at.redeye.Zeiterfassung.bindtypes.DBJobType;
import at.redeye.Zeiterfassung.bindtypes.DBMonthBlocks;
import at.redeye.Zeiterfassung.bindtypes.DBProjects;
import at.redeye.Zeiterfassung.bindtypes.DBSubProjects;
import at.redeye.Zeiterfassung.bindtypes.DBTimeEntries;
import at.redeye.Zeiterfassung.bindtypes.DBUserPerMonth;

public class ModuleLauncher extends BaseModuleLauncher implements
		at.redeye.UserManagement.UserManagementListener {

	private UserManagementInterface um = null;
	private boolean first_run = true;

	public ModuleLauncher(String[] args) {
            super(args);            

		String name = getStartupParam("appname", "appname", "APPNAME","MOMM");

		String url = "";
		String title;

		if (at.redeye.Dongle.AppliactionModes.getAppliactionModes()
				.isDemoVersion()) {
			name += "-dev";

                        if( splashEnabled() )
                        {
                            splash = new StartupWindow(
					"/at/redeye/Zeiterfassung/resources/icons/redeye15b-dev.png");
                        }
			url = getWebStartUrl("http://redeye.hoffer.cx/Zeiterfassung-developer/launch.jnlp");
			title = getStartupParam("apptitle", "apptitle", "APPTITLE","ZES-DEV");
		} else {

                        if( splashEnabled() )
                        {
                            splash = new StartupWindow(
                            		"/at/redeye/Zeiterfassung/resources/icons/redeye15b.png");
                        }

			url = getWebStartUrl("http://redeye.hoffer.cx/Zeiterfassung/launch.jnlp");
			title = getStartupParam("apptitle", "apptitle", "APPTITLE","Zeiterfassung");
		}

		root = new LocalRoot(name, title);
		um = new UserDataHandling(root);
		root.setWebStartUlr(url);

                configureLogging();

                initDBConnectionFromParams();

            FrameWorkConfigDefinitions.LookAndFeel.addPrmListener(new PrmListener() {

                PrmDefaultCheckSuite checker = new PrmDefaultCheckSuite(PrmDefaultChecksInterface.PRM_IS_LOOKANDFEEL);

                void onChange(PrmActionEvent event) {

                    if( checker.doChecks(event) == true )
                    {
                        java.awt.EventQueue.invokeLater(new Runnable() {

                            public void run() {
                                setLookAndFeel(root);
                                reopen();
                            }
                        });
                    }
                }

                public void onChange(PrmDefaultChecksInterface defaultChecks, PrmActionEvent event) {
                    onChange(event);
                }

                public void onChange(PrmCustomChecksInterface customChecks, PrmActionEvent event) {
                    onChange(event);
                }
            });

	}

        public void relogin()
        {
            relogin(false);
        }

	public void relogin(boolean try_auto_login) {
		root.closeAllWindowsNoAppExit();

                if( !try_auto_login )
                    um.setAutoLogin(false);

		invoke();
	}

        public void reopen()
        {
            root.closeAllWindowsNoAppExit();
            accessGranted();
        }

	protected void invoke() {

		boolean wizardStarted = false;

		CheckConfigBase check_config = new CheckConfig(root, this);

		setCommonLoggingLevel();

		boolean failed_connect_db = true;

		try {
			if (root.loadDBConnectionFromSetup()) {
				failed_connect_db = false;
			}
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

            if (check_config.shouldPopUpWizard()) {

                ConfigWizard config_wizard = new ConfigWizard(root, new WizardListener() {

                    public boolean onStateChange(WizardStatus currentWizardStatus) {
                        if (currentWizardStatus == WizardStatus.CLOSED) {
                            openLoginDialog();
                            return false;
                        }

                        return true;
                    }
                });


                config_wizard.startWizard();

                closeSplash();

                wizardStarted = true;
            }
		
            PrmDBInit prmDBInit = new PrmDBInit(root);
            prmDBInit.initDb();

            setLookAndFeel(root);
		

		if (first_run)
			um.addUMListener(this);

		if (um.tryAutoLogin() == false) {

                    closeSplash();

			if (at.redeye.Dongle.AppliactionModes.getAppliactionModes()
					.isDemoVersion()) {
				um
						.setLogo("/at/redeye/Zeiterfassung/resources/icons/redeye15b-dev.png");
			} else {
				um
						.setLogo("/at/redeye/Zeiterfassung/resources/icons/redeye15b.png");
			}
			if (wizardStarted == false) {
				um.requestDialog(UserManagementDialogs.UM_LOGIN_DIALOG);
			}

		} else {
			closeSplash();
		}

		first_run = false;

		updateJnlp();
	}

	@Override
	public void accessGranted() {

		// Now the user is known - apply allowed value to AutoLoginUser-PRM
		String[] autoLoginValues = { root.getLogin(), "" }; // myself or nothing
		root.getSetup().getLocalConfig(
				FrameWorkConfigDefinitions.AutoLoginUser.getConfigName())
				.setPossibleValues(autoLoginValues);

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
										.autoLineBreak("Der Logger konnte nicht korrekt initialisiert werden!"),
								"User Management", JOptionPane.WARNING_MESSAGE);
			}

		}

		checkTableVersions();

		// Don't start as thread, because BaseDialog attempts wrong closing of
		// application
		new MainWin(root).setVisible(true);

	}

	@Override
	public String getVersion() {
		return Version.getVersion();
	}

    public void openLoginDialog() {
        if( um != null )
            um.requestDialog(UserManagementDialogs.UM_LOGIN_DIALOG);
    }

}

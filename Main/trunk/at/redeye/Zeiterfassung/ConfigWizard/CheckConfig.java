/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Zeiterfassung.ConfigWizard;

import at.redeye.FrameWork.base.DBConnection;
import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.base.prm.bindtypes.DBConfig;
import at.redeye.FrameWork.base.transaction.Transaction;
import at.redeye.FrameWork.utilities.StringUtils;

import java.sql.SQLException;

import javax.swing.JOptionPane;

/**
 * 
 * @author martin
 */
public class CheckConfig {
	Root root;

	boolean had_config_file;

	public CheckConfig(Root root) {
		this.root = root;
		had_config_file = hadConfigFile();
	}

	public boolean shouldPopUpWizard() {
		if (!had_config_file) {
			System.out.println("hadConfigFile failed");
			return true;
		}

		if (!haveDbConnection()) {
			System.out.println("hadConfigFile failed");
			return true;
		}

		return false;
	}

	boolean hadConfigFile() {
		// return !root.getSetup().initialRun();

		// Das geht bei mir nicht!

		return true;
	}

	private boolean haveDbConnection() {
		DBConnection con = root.getDBConnection();

		if (con == null)
			return false;

		Transaction trans = con.getDefaultTransaction();

		if (trans == null) {
			return false;
		}

		try {

			if (!trans.isOpen()) {
				return false;
			}

			DBConfig config = new DBConfig();

			if (!root.getDBManager().tableExists(config.getName())) {
				return false;
			}
		} catch (SQLException ex) {
			return false;
		}

		return true;
	}
}

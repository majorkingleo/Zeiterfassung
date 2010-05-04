/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Zeiterfassung.ConfigWizard;

import java.sql.SQLException;

import at.redeye.FrameWork.base.DBConnection;
import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.base.prm.bindtypes.DBConfig;
import at.redeye.FrameWork.base.transaction.Transaction;

/**
 * 
 * @author martin
 */
public class CheckConfig {
	
	private Root root;

	public CheckConfig(Root root) {
		this.root = root;
		
	}

	public boolean shouldPopUpWizard() {
		
		
		if (hadConfigFile() == false) {
			System.out.println("hadConfigFile failed");
			return true;
		}

		if (!haveDbConnection()) {
			System.out.println("hadConfigFile failed");
			return true;
		}

		return false;
	}

	private boolean hadConfigFile() {
		return true;
		//return !root.getSetup().initialRun();

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

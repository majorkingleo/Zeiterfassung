/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Zeiterfassung.ConfigWizard;

import at.redeye.FrameWork.base.BaseModuleLauncher;
import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.utilities.StringUtils;
import at.redeye.Setup.ConfigCheck.CheckConfigBase;
import at.redeye.Setup.ConfigCheck.Checks.CreatedAlreadyAUser;
import at.redeye.Setup.ConfigCheck.Checks.HaveDbConnection;
import at.redeye.Setup.ConfigCheck.Checks.InitialRun;

/**
 *
 * @author martin
 */
public class CheckConfig extends CheckConfigBase
{
    public CheckConfig( Root root, BaseModuleLauncher module_launcher )
    {
        super( root );

        if( !StringUtils.isYes(module_launcher.getStartupParam(null, null, "DONT_WIZARD_ON_INITIAL_RUN") ) )
        {
            addCheck( new InitialRun(root) );
        }

        addCheck( new HaveDbConnection( root ));
        addCheck( new CreatedAlreadyAUser((root)));
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Zeiterfassung.ConfigWizard;

import at.redeye.FrameWork.base.Root;
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
    public CheckConfig( Root root )
    {
        super( root );

        addCheck( new InitialRun(root) );
        addCheck( new HaveDbConnection( root ));
        addCheck( new CreatedAlreadyAUser((root)));
    }
}

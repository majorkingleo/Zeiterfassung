/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.Zeiterfassung.ConfigWizard;

import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.base.wizards.WizardAction;
import at.redeye.FrameWork.base.wizards.WizardWindowInterface;
import at.redeye.FrameWork.base.wizards.impl.Wizard;
import at.redeye.FrameWork.base.wizards.impl.WizardBaseWindow;
import at.redeye.Setup.ConfigCheck.Checks.AdminUserExists;
import at.redeye.UserManagement.UserManagementInterface;
import at.redeye.UserManagement.bindtypes.DBPermissionLevel.PERMISSIONLEVEL;
import at.redeye.Zeiterfassung.JobTypes;
import java.awt.Dimension;

/**
 *
 * @author Mario
 */
public class WizardStepCreateJobTypes extends WizardBaseWindow implements WizardWindowInterface {

	private static final long serialVersionUID = 1L;
	private JobTypes dlg = null;
    private Wizard parentWizard = null;

    public WizardStepCreateJobTypes(Root root, Wizard parent) {

        super(root, "Tätigkeiten");
        this.parentWizard = parent;
        dlg = new JobTypes(root,parent);

    }

    public boolean allowJumpNextWindow() {
        return true;
    }

    public boolean allowJumpPrevWindow() {
        return true;
    }

    public boolean allowJumpToEnd() {
        return true;
    }

    public boolean allowCloseBeforeEnd() {
        return true;
    }

    public void onClose(WizardAction current_action) {    	
        super.close();
    }

    public void onInit() {
        setGuestContent();
        super.setVisible(true);

        if( root.getUserPermissionLevel() != UserManagementInterface.UM_PERMISSIONLEVEL_ADMIN )
        {
            setSkipThisStep(true);
        }
        else
        {
            setSkipThisStep(false);
        }
    }

    @Override
    protected void setGuestContent() {

        super.panelGuestContent.add(dlg.getContentPane());
        super.panelGuestContent.setPreferredSize(new Dimension(300, 200));
        super.panelGuestContent.updateUI();        
    }

    @Override
    protected String getHelptext() {
        return "\n Hier werden neue Tätigkeiten angelgt und eingerichtet.";
    }

    @Override
    protected Wizard getParentWizard() {
        return parentWizard;
    }
}

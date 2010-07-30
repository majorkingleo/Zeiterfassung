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
import java.awt.Dimension;

/**
 *
 * @author Mario
 */
public class WizardStepLogin extends WizardBaseWindow implements WizardWindowInterface {

	private static final long serialVersionUID = 1L;
	private LoginPanel dlg = null;
    private Wizard parentWizard = null;

    public WizardStepLogin(Root root, Wizard parent) {

        super(root, "Anmeldung");
        this.parentWizard = parent;
        dlg = new LoginPanel(root, parent);

    }

    public boolean allowJumpNextWindow() {
        return false;
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

        AdminUserExists check = new AdminUserExists(root);

        if( !check.doIHaveRequiredFeature() )
        {
            setSkipThisStep(true);
        } else {
            setSkipThisStep(false);
        }
    }

    @Override
    protected void setGuestContent() {

        super.panelGuestContent.add(dlg);
        super.panelGuestContent.setPreferredSize(new Dimension(300, 200));
        super.panelGuestContent.updateUI();        
    }

    @Override
    protected String getHelptext() {
        return "\n In diesem Setup-Abschnitt müssen Sie sich anmelden";
    }

    @Override
    protected Wizard getParentWizard() {
        return parentWizard;
    }
}

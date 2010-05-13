/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Zeiterfassung.AddUserWizard;

import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.base.wizards.WizardProperties;
import at.redeye.FrameWork.base.wizards.impl.Wizard;
import at.redeye.FrameWork.base.wizards.impl.WizardListener;
import at.redeye.Setup.wizard.impl.WizardStepFinished;
import at.redeye.Setup.wizard.impl.WizardStepWelcome;

/**
 *
 * @author Mario
 */

public class AddUserWizard
{    
    private Root root;
    private WizardListener wizard_listener;

    public AddUserWizard( Root root )
    {
        this.root = root;
    }

    /**
     * Starts the config Wizard
     * @param root
     * @param wizard_listener can be null, if not null the wizard_listener will be informed,
     * when the configuration step is finished.
     */
    public AddUserWizard( Root root, WizardListener wizard_listener)
    {
        this.root = root;
        this.wizard_listener = wizard_listener;
    }

    
    public void startWizard()
    {       
        WizardProperties props = new WizardProperties();
        props.setButtonNextText("Vorwärts");        
        Wizard wizard = new Wizard(props);
        wizard.addWizardListener(wizard_listener);        
        WizardStepWelcome welcome = new WizardStepWelcome(root, wizard);                
        WizardStepFinished finish = new WizardStepFinished(root, wizard);
        WizardStepUserData user = new WizardStepUserData(root, wizard);
        WizardStepMonthSettingsForUser month = new WizardStepMonthSettingsForUser(root,wizard);

        wizard.addWindow(welcome);
        wizard.addWindow(user);
        wizard.addWindow(month);
        wizard.addWindow(finish);

        wizard.start();       
    }

}

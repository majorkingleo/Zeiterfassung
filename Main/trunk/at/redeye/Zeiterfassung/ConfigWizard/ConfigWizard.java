/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Zeiterfassung.ConfigWizard;

import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.base.wizards.WizardProperties;
import at.redeye.FrameWork.base.wizards.impl.Wizard;
import at.redeye.FrameWork.base.wizards.impl.WizardListener;
import at.redeye.Setup.wizard.impl.WizardStepDBSetup;
import at.redeye.Setup.wizard.impl.WizardStepFinished;
import at.redeye.Setup.wizard.impl.WizardStepUserData;
import at.redeye.Setup.wizard.impl.WizardStepWelcome;

/**
 *
 * @author Mario
 */

public class ConfigWizard
{    
    private Root root;
    private WizardListener wizard_listener;

    public ConfigWizard( Root root )
    {
        this.root = root;
    }

    /**
     * Starts the config Wizard
     * @param root
     * @param wizard_listener can be null, if not null the wizard_listener will be informed,
     * when the configuration step is finished.
     */
    public ConfigWizard( Root root, WizardListener wizard_listener)
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
        WizardStepDBSetup dbSetup = new WizardStepDBSetup(root, wizard);
        WizardStepWelcome welcome = new WizardStepWelcome(root, wizard);
        WizardStepUserData user = new WizardStepUserData(root, wizard);
        WizardStepFinished finish = new WizardStepFinished(root, wizard);
        
        wizard.addWindow(welcome);
        wizard.addWindow(dbSetup);
        wizard.addWindow(user);
        wizard.addWindow(finish);

        wizard.start();       
    }

}

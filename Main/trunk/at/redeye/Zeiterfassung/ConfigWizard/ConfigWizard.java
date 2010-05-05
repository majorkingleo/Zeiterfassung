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
import at.redeye.Zeiterfassung.ModuleLauncher;
import org.apache.log4j.Logger;

/**
 *
 * @author Mario
 */

public class ConfigWizard
{
    private static Logger logger = Logger.getLogger (ConfigWizard.class.getSimpleName());

    Root root;
    ModuleLauncher module_launcher;

    public ConfigWizard( Root root, ModuleLauncher module_launcher)
    {
        this.root = root;
        this.module_launcher = module_launcher;
    }

    
    public void startWizard()
    {
       
        WizardProperties props = new WizardProperties();
        props.setButtonNextText("Vorwärts");        
        final Wizard wizard = new Wizard(props);
        wizard.addWizardListener( new WizardListener() {

            public boolean onStateChange(WizardStatus currentWizardStatus) {
                if (currentWizardStatus == WizardStatus.CLOSED) {
                    module_launcher.openLoginDialog();
                    return false;
                }

                return true;
            }

        });
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

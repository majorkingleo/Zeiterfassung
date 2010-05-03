/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Zeiterfassung.ConfigWizard;

import at.redeye.FrameWork.base.Root;
import at.redeye.FrameWork.base.wizards.WizardProperties;
import at.redeye.FrameWork.base.wizards.impl.Wizard;
import at.redeye.Setup.wizard.impl.WizardStepDBSetup;
import at.redeye.Setup.wizard.impl.WizardStepFinished;
import at.redeye.Setup.wizard.impl.WizardStepUserData;
import at.redeye.Setup.wizard.impl.WizardStepWelcome;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

/**
 *
 * @author Mario
 */

public class ConfigWizard {

    private static Logger logger = Logger.getLogger (ConfigWizard.class.getSimpleName());

    Root root;

    public ConfigWizard( Root root )
    {
        this.root = root;
    }

    public void startWizard()
    {
        BasicConfigurator.configure();
        WizardProperties props = new WizardProperties();
        props.setButtonNextText("Vorwärts");        
        Wizard wizard = new Wizard(props);
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

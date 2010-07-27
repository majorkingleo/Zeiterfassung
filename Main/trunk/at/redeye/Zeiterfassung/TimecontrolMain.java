package at.redeye.Zeiterfassung;

import java.net.ProxySelector;

public class TimecontrolMain {

    private static ModuleLauncher ml;

    public static void relogin()
    {
        ml.relogin();
    }

    public static void relogin(boolean try_autologin)
    {
        ml.relogin(try_autologin);
    }

    public static void reopen()
    {
        ml.reopen();
    }

	public static void main(String[] args) {
		
            ml = new ModuleLauncher(args);

            ml.invoke();
	}

}

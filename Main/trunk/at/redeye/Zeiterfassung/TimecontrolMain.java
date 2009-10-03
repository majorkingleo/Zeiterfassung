package at.redeye.Zeiterfassung;

public class TimecontrolMain {

    private static ModuleLauncher ml;

    public static void relogin()
    {
        ml.relogin();
    }

	public static void main(String[] args) {
		// TODO code application logic here
		ml = new ModuleLauncher();

        ml.invoke();
	}

}

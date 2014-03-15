package net.softwarepage.voclearn.code;

public class OSValidator {
	 
	private static String OS = System.getProperty("os.name").toLowerCase();
 
	public static boolean isWindows() {
 
		return (OS.startsWith("win"));
 
	}
}
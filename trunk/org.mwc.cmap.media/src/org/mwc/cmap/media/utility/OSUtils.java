package org.mwc.cmap.media.utility;

public class OSUtils {
	
	public static final boolean WIN;
	public static final boolean MAC;
	public static final boolean LINUX;
	public static final boolean IS_64BIT;
	static {
		String os = System.getProperty("os.name").toLowerCase();
		boolean win = false, mac = false, linux = false;
		if (os.indexOf("win") != -1) {
			win = true;
		}
		if (os.indexOf("mac os") != -1) {
			mac = true;
		}
		if (os.indexOf("linux") != -1) {
			linux = true;
		}		
		WIN = win;
		MAC = mac;
		LINUX = linux;
		String jvmArch = System.getProperty("os.arch");
		IS_64BIT = jvmArch != null && jvmArch.contains("64");
	}	
}

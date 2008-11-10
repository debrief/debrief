package MWC.GUI.TabPanel;

/**
 *
 *
 *
 * @see
 *
 * @version 1.0, Nov 26, 1996
 *
 * @author	Symantec
 *
 */

// 	02/15/97	RKM	Added isSolaris

/**
 * This class identifies the operating system that a program is running under.
 * <p>
 * It does not need to be instantiated by the user.
 */
public final class OS
{
    private static boolean isWindows95 = false;
    private static boolean isWindowsNT = false;
    private static boolean isMacintosh = false;
    private static boolean isSolaris = false;

    static
    {
        String s;

        s = System.getProperty("os.name");

        if(s.equals("Windows NT"))
        {
            isWindowsNT = true;
        }
        else if(s.equals("Windows 95"))
        {
            isWindows95 = true;
        }
        else if (s.equals("Macintosh") ||
				 s.equals("macos") ||		//Applet Viewer
				 s.equals("Mac OS") ||		//Netscape
				 s.equals("MacOS"))			//Internet Exploader
        {
            isMacintosh = true;
        }
        else if (s.equals("SunOS") ||
				 s.equals("Solaris"))
        {
            isSolaris = true;
        }
    }

    private OS()
    {
    }

    /**
     * Returns true if running under the Windows 95 or Windows NT operating system.
     */
    public static boolean isWindows()
    {
        return (isWindows95() || isWindowsNT());
    }

    /**
     * Returns true if running under the Windows 95 operating system.
     */
    public static boolean isWindows95()
    {
        return (isWindows95);
    }

    /**
     * Returns true if running under the Windows NT operating system.
     */
    public static boolean isWindowsNT()
    {
        return (isWindowsNT);
    }

    /**
     * Returns true if running under the Macintosh operating system.
     */
    public static boolean isMacintosh()
    {
        return (isMacintosh);
    }

    /**
     * Returns true if running under the Solaris operating system.
     */
    public static boolean isSolaris()
    {
        return (isSolaris);
    }
}
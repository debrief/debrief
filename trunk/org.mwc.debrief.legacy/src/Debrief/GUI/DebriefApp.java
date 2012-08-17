package Debrief.GUI;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: DebriefApp.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.3 $
// $Log: DebriefApp.java,v $
// Revision 1.3  2005/12/13 09:06:15  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.2  2004/07/06 09:17:26  Ian.Mayo
// Intellij tidying
//
// Revision 1.1.1.2  2003/07/21 14:46:53  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.2  2002-05-29 10:05:04+01  ian_mayo
// minor tidying
//
// Revision 1.1  2002-05-28 12:19:16+01  ian_mayo
// Following file name change


/**
 * This class can take a variable number of parameters on the command
 * line. Program execution begins with the main() method. The class
 * constructor is not invoked unless an object of type 'Class1'
 * created in the main() method.
 */

import Debrief.GUI.Frames.Application;
import Debrief.GUI.Frames.AWT.AWTApplication;
import Debrief.GUI.Frames.Swing.SwingApplication;

/**
 * Top level class for Debrief 3 application
 */
public class DebriefApp {

    /**
     * Whether to create a SWING application, or an AWT application
     */
    final boolean USE_SWING = true;

    /**
     * the application we start
     */
    protected static Application aw;

    /**
     * The main entry point for the application.
     *
     * @param args Array of parameters passed to the application via the command line.
     */
    public static void main(String[] args) {

        new DebriefApp();

        // and now open any files, as requested
        final int num = args.length;
        aw.setCursor(java.awt.Cursor.WAIT_CURSOR);
        for (int i = 0; i < num; i++) {
            aw.openFile(new java.io.File(args[i]));
        }
        aw.setCursor(java.awt.Cursor.DEFAULT_CURSOR);

    }

    /**
     * Constructor for this class
     */
    public DebriefApp() {
        if (USE_SWING) {
            // try setting the look & feel
//            try {
// //           UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
//                UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
//            } catch (ClassNotFoundException e) {
//                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//            } catch (InstantiationException e) {
//                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//            } catch (UnsupportedLookAndFeelException e) {
//                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//            }
            aw = new SwingApplication();
        } else
            aw = new AWTApplication();
    }

}


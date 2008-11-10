// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: ImportManager.java,v $
// @author $Author: ian.mayo $
// @version $Revision: 1.6 $
// $Log: ImportManager.java,v $
// Revision 1.6  2007/06/01 13:46:05  ian.mayo
// Improve performance of export text to clipboard
//
// Revision 1.5  2006/01/10 09:26:07  Ian.Mayo
// Don't clear the clipboard on export
//
// Revision 1.4  2004/12/06 08:48:43  Ian.Mayo
// Recognise when trying to import missing file.
//
// Revision 1.3  2004/10/07 14:23:26  Ian.Mayo
// Reflect fact that enum is now keyword - change our usage to enumer
//
// Revision 1.2  2004/05/24 16:24:35  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:27  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:51  Ian.Mayo
// Initial import
//
// Revision 1.5  2003-03-03 10:29:33+00  ian_mayo
// Empty clipboard before writing to it
//
// Revision 1.4  2003-01-15 16:13:32+00  ian_mayo
// Recognise when a file is zero size
//
// Revision 1.3  2002-07-23 08:50:39+01  ian_mayo
// Show error for file not found
//
// Revision 1.2  2002-05-28 09:26:06+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:13:55+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 13:03:24+01  ian_mayo
// Initial revision
//
// Revision 1.5  2002-01-23 15:26:06+00  administrator
// Close the input streams we create
//
// Revision 1.4  2001-08-29 19:19:59+01  administrator
// Bug fix
//
// Revision 1.3  2001-08-21 12:06:39+01  administrator
// Tidy up management of empty file sets
//
// Revision 1.2  2001-08-17 07:55:59+01  administrator
// Clear up memory leaks
//
// Revision 1.1  2001-08-06 09:38:55+01  administrator
// part way through implementing progress meters
//
// Revision 1.0  2001-07-17 08:42:47+01  administrator
// Initial revision
//
// Revision 1.3  2001-07-09 13:59:19+01  novatech
// minor tidying up
//
// Revision 1.2  2001-01-17 09:41:57+00  novatech
// remove unnecessary import statements
//
// Revision 1.4  2000-10-09 13:35:43+01  ian_mayo
// Switched stack traces to go to log file
//
// Revision 1.3  2000-03-08 16:26:54+00  ian_mayo
// tidied up imports
//
// Revision 1.2  2000-02-25 09:04:46+00  ian_mayo
// Corrected location of "break" line when finding suitable importer
//
// Revision 1.2  1999-11-23 17:24:13+00  ian_mayo
// attempt at multi-threaded
//
// Revision 1.2  1999-07-27 09:27:28+01  administrator
// added more error handlign
// Revision 1.4  1999-06-16 15:24:22+01  sm11td
// before move around/ end of phase 1
//
// Revision 1.3  1999-06-01 16:49:21+01  sm11td
// Reading in tracks aswell as fixes, commenting large portions of source code
//
// Revision 1.2  1999-02-01 16:08:48+00  sm11td
// creating new sessions & panes, starting import management


package MWC.Utilities.ReaderWriter;


import MWC.GUI.Layers;
import MWC.GUI.Plottable;

import java.io.File;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Factory for the types of file which may be imported into Debrief,
 * as you can see for yourself this stores the types of importer available
 * <p/>
 * <B><P>Import Manager</P>
 * </B><P>This is a factory class, using ready-created instances of import filters. </P>
 * <P>An import is initiated by calling the "Import This" method of this class,
 * passing the filename, an {importThis() InputStream} based on the file,
 * and a Layers object to con.  The class then determines the correct type of
 * filter to employ, and passes the @see MWC.Layers to it.</P>
 * The filter then imports the data.  If a session is open,
 * the new layers are added to the session,
 * else a default session is opened.
 * <p/>
 * Uses patterns: Singleton, Factory,
 */
public class ImportManager
{

  ////////////////////////////////////////
  // member variables
  ////////////////////////////////////////
  /**
   * static copy of manager
   */
  private static ImportManager theManager = null;
  /**
   * the list of import operators
   */
  private java.util.Vector _theImporters;

  ////////////////////////////////////////
  // constructor
  ////////////////////////////////////////
  public ImportManager()
  {
    // create the array of import handlers, by
    _theImporters = new Vector(0, 1);

  }

  ////////////////////////////////////////
  // member functions
  ////////////////////////////////////////

  /**
   * find the current exporter, and export the item
   */
  static public void exportThis(Plottable item)
  {
    if (theManager == null)
    {
      theManager = new ImportManager();
    }

    if (theManager._theImporters != null)
    {

    	// NOTE: WE'RE NOT CLEARING THE CLIPBOARD AT THE START OF THE EXPORT PROCESS,
    	//  - since this method is called multiple times when exporting a whole layer,
    	//    and we don't want to only show the last item in the layer...
    	
      // this our "entry" method to exporting to the clipboard.  So, from this call we reset
      // the contents of the clipboard to just contain our data
//      java.awt.datatransfer.Clipboard cl = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
//      java.awt.datatransfer.StringSelection ss = new java.awt.datatransfer.StringSelection("");
//      cl.setContents(ss, ss);

      // just use the first exporter, for now
      PlainImporter pi = (PlainImporter) theManager._theImporters.elementAt(0);
      
      // get the export ready
      pi.startExport(item);
      
      // do the export
      pi.exportThis(item);
      
      // and finish the process
      pi.endExport(item);
      
    }

  }

  /**
   * find the current exporter, and export the item
   */
  static public void exportThis(String item)
  {
    if (theManager == null)
    {
      theManager = new ImportManager();
    }

    if (theManager._theImporters != null)
    {
      // just use the first exporter, for now
      PlainImporter pi = (PlainImporter) theManager._theImporters.elementAt(0);
      pi.exportThis(item);
    }

  }


  /**
   * import the specified file into the data structure passed
   *
   * @param fName   the name of the file, we are to
   *                determine the type from the suffix
   * @param theData the Destination for the data read in
   */
  public static void importThis(String fName,
                                Layers theData)
  {


    System.out.println("importing: " + fName);
    importThisThread it = new importThisThread(fName, theData);

    it.start();

    // wait for the thread to finish
    while (it.isAlive())
    {
      try
      {
        Thread.currentThread().sleep(200);
      }
      catch (java.lang.InterruptedException e)
      {
        MWC.Utilities.Errors.Trace.trace(e);
      }
    }
  }

  /**
   * import the specified file into the data structure passed
   *
   * @param _theFiles the name of the file, we are to
   *                  determine the type from the suffix
   * @param theData   the Stream to read the data from
   * @param theCaller the Destination for the data read in
   */
  public static void importThese(java.io.File[] _theFiles,
                                 Layers theData,
                                 ImportCaller theCaller)
  {

    if (_theFiles == null)
      return;

    // work through the files
    // loop through
    for (int i = 0; i < _theFiles.length; i++)
    {
      java.io.File fl = _theFiles[i];

      // have we got file?
      if ((fl != null) &&
        (!fl.getName().equals("nullnull")))
      {
        importThisThread it = new importThisThread(fl.getPath(), theData);

        it.start();

        // wait for the thread to finish
        while (it.isAlive())
        {
          try
          {
            Thread.currentThread().sleep(200);
          }
          catch (java.lang.InterruptedException e)
          {
            MWC.Utilities.Errors.Trace.trace(e);
          }
        }

        // ok, this one is done, now for the next!
        if (theCaller != null)
          theCaller.fileFinished(fl, theData);

      }
    }

    // ok, this one is done, now for the next!
    if (theCaller != null)
      theCaller.allFilesFinished(_theFiles, theData);


  }


  protected static class importThisThread extends Thread
  {
    protected String fName;
    protected Layers theData;

    importThisThread(String _fName,
                     Layers _theData)
    {
      fName = _fName;
      theData = _theData;
    }

    public void run()
    {
      // check we are alive!
      if (theManager == null)
      {
        theManager = new ImportManager();
      }

      // look through types of import handler
      Enumeration enumer = theManager._theImporters.elements();

      while (enumer.hasMoreElements())
      {
        PlainImporter thisImporter = (PlainImporter) enumer.nextElement();

        // is this handler correct type?
        if (thisImporter.canImportThisFile(fName))
        {
          // handle the import
          importThisOne(thisImporter, fName, theData);

          // finished, drop out of loop
          break;
        }

      } // while we have more elements

      // forget our local references
      theData = null;
      fName = null;
    }
  }


  private static void importThisOne(PlainImporter theImporter, String fName, Layers theData)
  {
    try
    {
      // just check that we have a filename
      if (fName != null)
      {
        // get the file
        File theFile = new File(fName);

        // first check if the file exists
        if(!theFile.exists())
        {
          MWC.GUI.Dialogs.DialogFactory.showMessage("Read file", "This file cannot be found. It may have been deleted.");
          return;
        }

        // is it there?
        if (theFile.length() == 0)
        {
          MWC.GUI.Dialogs.DialogFactory.showMessage("Read file", "This file does not contain any data (zero size)");
          return;
        }

        // hey, create the input stream
        java.io.InputStream is = new java.io.FileInputStream(theFile);
        java.io.BufferedInputStream bs = new java.io.BufferedInputStream(is);

        // now get the import to continue with it's struggle!
        theImporter.importThis(fName, bs, theData);

        // and finally close the streams
        bs.close();
        is.close();
      }

    }
    catch (java.io.FileNotFoundException fe)
    {
      MWC.GUI.Dialogs.DialogFactory.showMessage("Open File", "Sorry file not found:" + fName);
    }
    catch (java.io.IOException ie)
    {
      MWC.Utilities.Errors.Trace.trace(ie, "Failed to close file: " + fName);
    }

  }

  static public void addImporter(PlainImporter newImporter)
  {
    if (theManager == null)
    {
      theManager = new ImportManager();
    }

    theManager.addThisImporter(newImporter);
  }

  public void addThisImporter(PlainImporter newImporter)
  {
    _theImporters.addElement(newImporter);
  }

  /**
   * Embedded interface which provides callbacks for the calling class
   */
  public static interface ImportCaller
  {
    public void fileFinished(java.io.File fName, Layers newData);

    public void allFilesFinished(java.io.File[] fNames, Layers newData);
  }


  /**
   * default implementation import caller object - it's an abstract class just begging
   * to be completed by a child class
   */
  static abstract public class BaseImportCaller extends Thread implements ImportCaller
  {
    /**
     * the list of files to open
     */
    java.io.File[] _fileNames;

    /**
     * the list of layers to drop into
     */
    Layers _theData;

    /**
     * constructor, get ready to pass the data to the child
     */
    public BaseImportCaller(java.io.File[] fileNames,
                            Layers theData)
    {
      _fileNames = fileNames;
      _theData = theData;
    }

    /**
     * ok, thread has been constructed, etc. we must now be ready to go for it
     */
    public void run()
    {
      MWC.Utilities.ReaderWriter.ImportManager.importThese(_fileNames,
                                                           _theData,
                                                           this);

      // and clear the local data
      _theData = null;
      _fileNames = null;
    }
  }

}









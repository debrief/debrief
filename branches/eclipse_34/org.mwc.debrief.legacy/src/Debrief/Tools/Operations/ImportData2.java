// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: ImportData2.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.4 $
// $Log: ImportData2.java,v $
// Revision 1.4  2005/12/13 09:04:46  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.3  2004/02/18 10:25:07  Ian.Mayo
// *** empty log message ***
//
// Revision 1.2  2003/08/12 09:28:51  Ian.Mayo
// Include import of DTF files
//
// Revision 1.1.1.2  2003/07/21 14:48:30  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.3  2003-03-19 15:37:17+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.2  2002-05-28 12:28:25+01  ian_mayo
// after update
//
// Revision 1.1  2002-05-28 09:11:56+01  ian_mayo
// Initial revision
//
// Revision 1.0  2002-04-30 09:14:53+01  ian
// Initial revision
//
// Revision 1.1  2002-04-23 12:28:57+01  ian_mayo
// Initial revision
//
// Revision 1.6  2002-03-26 08:40:56+00  administrator
// Only insert the data for a new session, else just fire modified flag
//
// Revision 1.5  2002-01-29 07:53:14+00  administrator
// Use Trace method instead of System.out
//
// Revision 1.4  2001-11-13 21:11:55+00  administrator
// Correct where we are trying to import data without a session present
//
// Revision 1.3  2001-10-02 09:29:16+01  administrator
// Corrected bug where imported data was not loaded into session
//
// Revision 1.2  2001-08-24 16:36:13+01  administrator
// Put the new data into existing layers, not a fresh one (we need to do this for reading in sensor data, which may rely on retrieving track data)
//
// Revision 1.1  2001-08-21 15:20:02+01  administrator
// Allow use of dsf suffix
//
// Revision 1.0  2001-08-13 12:50:11+01  administrator
// Initial revision
//
// Revision 1.0  2001-07-17 08:41:20+01  administrator
// Initial revision
//
// Revision 1.2  2001-07-09 14:03:10+01  novatech
// handle giving the NarrativeWrapper it's StepControl pointer
//
// Revision 1.1  2001-01-03 13:40:30+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 20:48:19  ianmayo
// initial import of files
//
// Revision 1.19  2000-11-24 10:52:22+00  ian_mayo
// add imported filename to MRU
//
// Revision 1.18  2000-10-09 13:37:40+01  ian_mayo
// Switch stackTrace to go to file
//
// Revision 1.17  2000-09-21 12:23:18+01  ian_mayo
// check for definition of default replay file location
//
// Revision 1.16  2000-08-30 14:49:48+01  ian_mayo
// experimenting with MRU
//
// Revision 1.15  2000-08-21 15:28:14+01  ian_mayo
// create Working Session variable - to stop us re-using the last one created
//
// Revision 1.14  2000-08-09 16:51:04+01  ian_mayo
// remove d-lines
//
// Revision 1.13  2000-08-09 16:43:51+01  ian_mayo
// make lastDirectory stay alive between sessions
//
// Revision 1.12  2000-08-09 16:03:57+01  ian_mayo
// remove stray semi-colons
//
// Revision 1.11  2000-08-07 12:23:20+01  ian_mayo
// tidy icon filename
//
// Revision 1.10  2000-04-19 11:27:59+01  ian_mayo
// don't make it undoable
//
// Revision 1.9  2000-04-03 10:17:58+01  ian_mayo
// put in reminder of Replay file suffix
//
// Revision 1.8  2000-03-14 09:49:14+00  ian_mayo
// assign icon names to tools
//
// Revision 1.7  2000-03-07 14:48:16+00  ian_mayo
// optimised algorithms
//
// Revision 1.6  2000-02-22 13:49:19+00  ian_mayo
// ImportManager location changed, and export now receives Plottable, not PlainWrapper
//
// Revision 1.5  1999-12-13 10:36:32+00  ian_mayo
// removed some screen updates, they're handled elsewhere
//
// Revision 1.4  1999-12-02 09:45:27+00  ian_mayo
// use buffered stream
//
// Revision 1.3  1999-11-26 15:51:42+00  ian_mayo
// tidying up
//
// Revision 1.2  1999-11-18 11:17:44+00  ian_mayo
// uses DialogFactory & better file opening commands
//
// Revision 1.1  1999-10-12 15:34:08+01  ian_mayo
// Initial revision
//
// Revision 1.3  1999-07-27 09:27:02+01  administrator
// tidying up use of tools
//
// Revision 1.2  1999-07-16 10:01:49+01  administrator
// Nearing end of phase 2
//
// Revision 1.1  1999-07-07 11:10:15+01  administrator
// Initial revision
//

package Debrief.Tools.Operations;


import java.io.File;

import javax.swing.SwingUtilities;

import Debrief.GUI.Frames.*;
import MWC.GUI.*;
import MWC.GUI.Tools.*;

/** command to import a file (initially just Replay) into Debrief.
 * The data used to implement the command is stored as a command,
 * so that it may be added to an undo buffer.
 */
public final class ImportData2 extends PlainTool {

  ///////////////////////////////////////////////////////////////
  // member variables
  ///////////////////////////////////////////////////////////////
  /** keep a copy of the parent Session, */
  private final Session _theSession;

  /** keep a copy of the parent application, in case
   * we don't have a session
   */
  private final Application _theApplication;

  /** remember the last file location we read from
   */
  private static String _lastDirectory = null;

  ///////////////////////////////////////////////////////////////
  // constructor
  ///////////////////////////////////////////////////////////////
  /** constructor, taking information ready for when the button
   * gets pressed
   * @param theParent the ToolParent window which we control the cursor of
   * @param theApplication the Application to create a blank
   * session to import the file into, if the session val is null
   * @param theSessionVal the Session to add the file to (or null, see above)
   */
  public ImportData2(ToolParent theParent,
                    Application theApplication,
                    Session theSessionVal){
    super(theParent, "Import data", "images/import_rep.gif");
    // store the Session
    _theSession = theSessionVal;
    _theApplication = theApplication;
    if(_lastDirectory == null)
      _lastDirectory = "";
  }


  /** collate the data ready to perform the operations
   */
  public final Action getData()
  {

    // pass a duff stepper control to the NarrativeImporter, since a this point we
    // may not have a View open, nor a StepControl.  After the "load" operation,
    // set the step control at that point.
    Debrief.ReaderWriter.Replay.ImportReplay.setStepper(null);

    /** temporary session variable.  Either use the existing session variable
     * (which was assigned because we only ever read into one session),
     * or create a fresh one each time (if we need to retrieve the variable
     * from the application).
     */
    Session tmpSession = _theSession;

    ImportAction res = null;

    // see if we have an old directory to retrieve
    if(_lastDirectory == "")
    {
      String val = getParent().getProperty("REP_Directory");
      if(val != null)
        _lastDirectory = val;
    }

    // get the filename of the file to import
    File[] fList = MWC.GUI.Dialogs.DialogFactory.getOpenFileName("*.rep,*.dsf,*.dtf",
                                                              "Replay Files (*.rep)",
                                                              _lastDirectory);

    res = new ImportAction(tmpSession, fList, _theApplication);

    // return the product
    return res;
  }


  ///////////////////////////////////////////////////////
  // store action information
  ///////////////////////////////////////////////////////
  static public final class ImportAction implements Action{
    /** the filename we originally read the data from (note that by this
     * point the data has already been read, and stored in the Layers object)
     */
    private final File[] _theFiles;
    /** the session we are going to import the data into
     */
    private Session _theSession;
    /** the structure containing the imported data
     */
    private final Layers _theLayers;

    private final Application _theApplication;

    /** constructor - produced AFTER we have read in the data, but
     * before we have added it to the session
     */
    public ImportAction(Session theSession,
                        java.io.File[] theFiles,
                        Application theApplication){
      _theSession = theSession;
      _theFiles = theFiles;
      if(_theSession != null)
        _theLayers = _theSession.getData();
      else
      {
        // can we retrieve it from the application?
        _theSession = theApplication.getCurrentSession();

        // did it work?
        if(_theSession != null)
        {
          _theLayers = _theSession.getData();
        }
        else
          _theLayers = new Layers();
      }


      _theApplication = theApplication;
    }

    public final boolean isRedoable(){
      return false;
    }


    public final boolean isUndoable(){
      return false;
    }

    public final String toString(){
      return "import files";
    }

    public final void undo(){
      // delete the plottables from the Session object
    }

    // call back from the import thread, to say the data is now ready
    public final void finished()
    {

      if(_theSession == null){
        _theSession = _theApplication.getCurrentSession();

        if(_theSession == null){
          // if we haven't got a session, then create one
          _theApplication.newSession(null);

          // now try again
          _theSession = _theApplication.getCurrentSession();

          // check it worked
          if(_theSession == null)
          {
            MWC.GUI.Dialogs.DialogFactory.showMessage("Import data","Import data - create session failed!");
          }

        }
      }

      // we are now sure that we have a session, add the new data to it
      // now add this fresh data

      // just check that we're not working with the same object
      if(_theSession.getData() == _theLayers)
      {
        // hey, the data's been loaded into the session already, just fire "Modified"
        _theSession.getData().fireExtended();
      }
      else
      {
        // looks like the data was loaded into a new layers object, we'll have to add it
        // to our existing one.
        _theSession.getData().addThis(_theLayers);
      }

      // inform any NarrativeWrapper objects of the StepContorl
      final Debrief.GUI.Views.AnalysisView av = (Debrief.GUI.Views.AnalysisView) _theSession.getCurrentView();
      Debrief.GUI.Tote.StepControl stepper = av.getTote().getStepper();

      // find any narratives
      int len = _theSession.getData().size();
      for(int i=0;i<len;i++)
      {
        Layer ly = _theSession.getData().elementAt(i);
        if(ly instanceof Debrief.Wrappers.NarrativeWrapper)
        {
          Debrief.Wrappers.NarrativeWrapper nw = (Debrief.Wrappers.NarrativeWrapper)ly;
          nw.setStepper(stepper);
        }
      }


      // clear the step control pointer temporarily set in the ImportReplay object
      Debrief.ReaderWriter.Replay.ImportReplay.setStepper(null);

      // instruct the session to rescale, following this import
      _theSession.getData().fireExtended();

      /** put the scren update bit into a runnable object - so that we can
       * make it run once the load is complete
       */
      Runnable runnable = new Runnable()
      {
        public void run()
        {
          // and resize the plot
          av.rescale();

          // and get the plot to redraw itself
          av.update();
        }
      };
      SwingUtilities.invokeLater(runnable);

    }

    public final void execute()
    {
      // get our thread to import this
      MWC.Utilities.ReaderWriter.ImportManager.BaseImportCaller reader =
        new MWC.Utilities.ReaderWriter.ImportManager.BaseImportCaller(_theFiles,_theLayers)
      {
        // handle the completion of each file
        public void fileFinished(File fName, Layers newData)
        {
    //      System.out.println("file finished received for:" + fName.getPath());
          Application.addToMru(fName.getPath());
        }

        // handle completion of the full import process
        public void allFilesFinished(File[] fNames, Layers newData)
        {
   //       System.out.println("ImportData: all files finished received!");
          finished();
        }
      };

      // and start it running
      reader.start();

      // ok, drop out now, our finished method will get called back at the end of the relevant bits
    }
  }

}

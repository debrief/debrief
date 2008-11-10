// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: ImportData.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: ImportData.java,v $
// Revision 1.2  2005/12/13 09:04:45  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.1.1.2  2003/07/21 14:48:30  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.3  2003-03-19 15:37:07+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.2  2002-05-28 12:28:26+01  ian_mayo
// after update
//
// Revision 1.1  2002-05-28 09:11:56+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:28:56+01  ian_mayo
// Initial revision
//
// Revision 1.1  2001-08-06 12:44:40+01  administrator
// pass in the full path, not just the filename
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


import java.io.*;

import Debrief.GUI.Frames.*;
import MWC.GUI.*;
import MWC.GUI.Tools.*;

/** command to import a file (initially just Replay) into Debrief.
 * The data used to implement the command is stored as a command,
 * so that it may be added to an undo buffer.
 */
public final class ImportData extends PlainTool {

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
  public ImportData(ToolParent theParent,
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
    File[] fList = MWC.GUI.Dialogs.DialogFactory.getOpenFileName("*.rep",
                                                              "Replay Files (*.rep)",
                                                              _lastDirectory);

    // check if anything was returned
    if(fList != null)
    {

      // got the filename now do the import
      Layers theLayers = new Layers();

      // loop through
      for(int i=0;i<fList.length;i++)
      {
        File fl = fList[i];

        // have we got file?
        if((fl != null) &&
           (!fl.getName().equals("nullnull")))
        {

          // store the directory name
          _lastDirectory = fl.getParent();
          FileInputStream is = null;

          try{
            is = new FileInputStream(fl);
            MWC.Utilities.ReaderWriter.ImportManager.importThis(fl.getPath(),
                                                        theLayers);


            // add the filename to the MRU
            Application.addToMru(fl.getAbsolutePath());

          }catch(java.io.FileNotFoundException e){
            MWC.Utilities.Errors.Trace.trace(e);
          }
          finally
          {
            // make sure that the file gets closed
            try{
              is.close();
            }
            catch(java.io.IOException ex)
            {
            MWC.Utilities.Errors.Trace.trace(ex, "Closing REPLAY file");
            }
          }

          // sort out what session we are importing the new layers into
          if(tmpSession == null){
            tmpSession = _theApplication.getCurrentSession();

            if(tmpSession == null){
              // if we haven't got a session, then create one
              _theApplication.newSession(null);

              // now try again
              tmpSession = _theApplication.getCurrentSession();

              // check it worked
              if(tmpSession == null)
              {
                MWC.GUI.Dialogs.DialogFactory.showMessage("Import data","Import data - create session failed!");
              }

            }
          }

        }      	// loop through list of files

        // data is collated, now create 'action' function
        res = new ImportAction(fl.getName(),
                               tmpSession,
                               theLayers);


      } //
    }  // whether a real file list was returned

    // clear the step control pointer temporarily set in the ImportReplay object
    Debrief.ReaderWriter.Replay.ImportReplay.setStepper(null);

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
    private final String _theFileName;
    /** the session we are going to import the data into
     */
    private final Session _theSession;
    /** the structure containing the imported data
     */
    private final Layers _theLayers;

    /** constructor - produced AFTER we have read in the data, but
     * before we have added it to the session
     */
    public ImportAction(String theFileName,
                        Session theSession,
                        Layers theLayers){
      _theSession = theSession;
      _theFileName = theFileName;
      _theLayers = theLayers;
    }

    public final boolean isRedoable(){
      return false;
    }


    public final boolean isUndoable(){
      return false;
    }

    public final String toString(){
      return "import " + _theFileName;
    }

    public final void undo(){
      // delete the plottables from the Session object
    }
    public final void execute()
    {
      // add the plottables to the indicated session
      _theSession.getData().addThis(_theLayers);

      // inform any NarrativeWrapper objects of the StepContorl
      Debrief.GUI.Views.AnalysisView av = (Debrief.GUI.Views.AnalysisView) _theSession.getCurrentView();
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

      // instruct the session to rescale, following this import
      _theSession.getData().fireExtended();

    }
  }
}

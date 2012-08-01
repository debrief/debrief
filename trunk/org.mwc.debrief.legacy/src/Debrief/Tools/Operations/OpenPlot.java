package Debrief.Tools.Operations;

import MWC.GUI.Tools.*;
import MWC.GUI.*;
import Debrief.GUI.Frames.*;
import java.io.*;


public final class OpenPlot extends MWC.GUI.Tools.Operations.Open
{
  /////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////

  /** copy of the parent application for this tool
   */
  private Application _myApplication = null;

  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////

  public OpenPlot(ToolParent theParent,
                  java.awt.Image theImage,
                  Application theApplication){
    super(theParent, "Open Plot", "*.dpl", "Debrief Plot Files (*.dpl)");

    // store local data

    // see if we have an old directory to retrieve
    if(_lastDirectory.equals(""))
    {
      String val = getParent().getProperty("DPL_Directory");
      if(val != null)
        _lastDirectory = val;
    }

    _myApplication = theApplication;

  }

  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////

  public final Action doOpen(String filename)
  {
    Action res = null;

    // data is collated, now create 'action' function
    res = new OpenPlotAction(filename,
                             _myApplication);

    // return the product
    return res;
  }

  ///////////////////////////////////////////////////////
  // store action information
  ///////////////////////////////////////////////////////
  static public final class OpenPlotAction implements Action{
    /** the filename we originally read the data from (note that by this
     * point the data has already been read, and stored in the Layers object)
     */
    private final String _theFileName;
    /** the application we are going to import the data into
     */
    private final Application _theApplication;

    /** constructor - produced AFTER we have read in the data, but
     * before we have added it to the Application
     */
    public OpenPlotAction(String theFileName,
                        Application theApplication){
      _theApplication = theApplication;
      _theFileName = theFileName;
    }

    public final boolean isRedoable(){
      return false;
    }


    public final boolean isUndoable(){
      return false;
    }

    public final String toString(){
      return "Open " + _theFileName;
    }

    public final void undo(){
      // delete the plottables from the Application object
    }
		public final void execute()
		{
			_theApplication.setCursor(java.awt.Cursor.WAIT_CURSOR);

      FileInputStream is = null;

      try{

        // create new object input stream
        is = new FileInputStream(_theFileName);
        InputStream bi = new BufferedInputStream(is);
        ObjectInputStream ois = new ObjectInputStream(bi);

        // read in the session
        Session st = (Session)ois.readObject();
        
        ois.close();

        // setup the GUI
        st.initialiseForm(_theApplication);

        // add this session to our application
        _theApplication.newSession(st);

        // also inform the Session of it's filename
        st.setFileName(_theFileName);

        // put the filename into the MRU
        Debrief.GUI.Frames.Application.addToMru (_theFileName);

      }
      catch(java.io.InvalidClassException e){
        // do nothing;
        MWC.GUI.Dialogs.DialogFactory.showMessage("Open Plot",
                                                  "Incompatible File Versions:  " + e.getMessage());
      }
      catch(ClassNotFoundException e){
        MWC.Utilities.Errors.Trace.trace(e);
      }
      catch(IOException e){
        // do nothing;
        MWC.Utilities.Errors.Trace.trace(e);
      }
      finally
      {
        // do a closing "close" operation on the file - put it this late so that earlier errors get caught
        try{
        	if(is != null)
            is.close();
        }
        catch(java.io.IOException ex)
        {
        MWC.Utilities.Errors.Trace.trace(ex, "Closing REPLAY file");
        }
      }

			_theApplication.restoreCursor();



    }

  }

}

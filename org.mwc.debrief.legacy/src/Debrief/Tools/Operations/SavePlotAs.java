/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package Debrief.Tools.Operations;

import MWC.GUI.Tools.*;
import MWC.GUI.*;
import Debrief.GUI.Frames.*;
import java.io.*;

public class SavePlotAs extends MWC.GUI.Tools.Operations.Save
{
  /////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////
  private Session _theSession = null;

  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////
  public SavePlotAs(final ToolParent theParent,
                    final Session theSession){
    this(theParent, theSession,  "Save Plot As...", "images/saveas.gif");
  }

  public SavePlotAs(final ToolParent theParent,
                    final Session theSession,
                    final String theTitle,
                    final String theImage)
  {
    super(theParent, theTitle, "*.dpl", theImage);


    // store the session parameter
    _theSession = theSession;

    // see if we have an old directory to retrieve
    if(_lastDirectory.equals(""))
    {
      final String val = getParent().getProperty("DPL_Directory");
      if(val != null)
        _lastDirectory = val;
    }
  }

  /////////////////////////////////////////////////////////////
  // member methods
  ////////////////////////////////////////////////////////////
  protected final Action doSave(final String filename)
  {
    Action res = null;

    // now save session to this file
    try
    {

      // open the file
      final OutputStream os = new FileOutputStream(filename);

      // inform the session of it's filename
      _theSession.setFileName(filename);

      // create the object output stream
      final ObjectOutputStream oos = new ObjectOutputStream(os);

      // do the save
      oos.writeObject(_theSession);

      // and relax
      oos.close();
      os.close();

      res =  new SavePlotAction(_theSession.getName());

    }
    catch(final IOException e)
    {
      MWC.Utilities.Errors.Trace.trace(e);
    }

    return res;
  }


  final Session getSession()
  {
    return _theSession;
  }

  ///////////////////////////////////////////////////////
  // store action information
  ///////////////////////////////////////////////////////
  protected final static class SavePlotAction implements Action{
    /** store the name of the session we have saved
     */
    final String _theSessionName;

    public SavePlotAction(final String theName){
      _theSessionName = theName;
    }

    public final boolean isRedoable(){
      return false;
    }


    public final boolean isUndoable(){
      return false;
    }

    public final String toString(){
      return "Save " + _theSessionName;
    }

    public final void undo(){
      // delete the plottables from the Application object
    }

    public final void execute(){
    }

  }


  public final void close()
  {
    super.close();

    _theSession = null;
  }
}

package Debrief.Tools.Operations;

import Debrief.GUI.Frames.*;
import MWC.GUI.*;

public final class SavePlotXML extends SavePlotAsXML
{
  ///////////////////////////////////
  // member variables
  //////////////////////////////////

  ///////////////////////////////////
  // constructor
  //////////////////////////////////
  public SavePlotXML(ToolParent theParent,
                  Session theSession){
    super(theParent, theSession, "Save Plot", "images/save.gif");

  }

  ///////////////////////////////////
  // member functions
  //////////////////////////////////

  public final void execute()
  {
    // do we have a filename already?
    String fn = getSession().getFileName();

    if(fn != null)
    {
      // just re-save the session to the (known) filename
      this.doSave(fn);
    }
    else
    {
      // just let the parent do it's normal processing
      super.execute();
    }
  }
}

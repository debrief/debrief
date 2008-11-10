package Debrief.Tools.Operations;

import Debrief.GUI.Frames.*;
import MWC.GUI.*;

final class SavePlot extends SavePlotAs
{
  ///////////////////////////////////
  // member variables
  //////////////////////////////////

  ///////////////////////////////////
  // constructor
  //////////////////////////////////
  public SavePlot(ToolParent theParent,
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

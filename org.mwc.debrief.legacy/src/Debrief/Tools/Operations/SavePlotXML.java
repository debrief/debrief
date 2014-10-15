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
  public SavePlotXML(final ToolParent theParent,
                  final Session theSession){
    super(theParent, theSession, "Save Plot", "images/save.gif");

  }

  ///////////////////////////////////
  // member functions
  //////////////////////////////////

  public final void execute()
  {
    // do we have a filename already?
    final String fn = getSession().getFileName();

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

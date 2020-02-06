/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *  
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *  
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *******************************************************************************/
package org.mwc.debrief.lite.gui;

import java.io.File;

import Debrief.GUI.Frames.Application;
import Debrief.GUI.Frames.Session;
import Debrief.Tools.Operations.ImportData2.ImportAction;
import MWC.GUI.ToolParent;
import MWC.GUI.Tools.Action;
import MWC.GUI.Tools.PlainTool;

public class DebriefLiteImpotData extends PlainTool
{

  @SuppressWarnings("unused")
  private final ToolParent theParent;

  private final Application theApplication;

  private final Session session;

  public DebriefLiteImpotData(final ToolParent theParent,
      final Application theApplication, final Session theSessionVal)
  {

    this.theParent = theParent;
    this.theApplication = theApplication;
    this.session = theSessionVal;
  }

  @Override
  public Action getData()
  {
    final Session session = this.session;
    final File[] files = new File[1];
    files[0] = new File("");
    final ImportAction res = new ImportAction(session, files, theApplication);
    return res;
  }

}

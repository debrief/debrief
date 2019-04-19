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

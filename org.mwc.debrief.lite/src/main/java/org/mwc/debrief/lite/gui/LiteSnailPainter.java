package org.mwc.debrief.lite.gui;

import Debrief.GUI.Tote.AnalysisTote;
import Debrief.GUI.Tote.Painters.TotePainter;
import MWC.GUI.CanvasType;
import MWC.GUI.Layers;
import MWC.GUI.PlainChart;
import MWC.GenericData.HiResDate;

public class LiteSnailPainter extends TotePainter
{

  public LiteSnailPainter(PlainChart theChart, Layers theData,
      AnalysisTote theTote)
  {
    super(theChart, theData, theTote);
  }

  @Override
  public void newTime(HiResDate oldDTG, HiResDate newDTG, CanvasType canvas)
  {
    super.newTime(oldDTG, newDTG, canvas);
  }

  
  
}

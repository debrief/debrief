/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2018, Deep Blue C Technology Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package org.mwc.debrief.core.wizards.dynshapes;

import java.awt.Color;
import java.util.Date;

import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.Wrappers.DynamicShapeWrapper;
import MWC.GUI.Shapes.PlainShape;
import MWC.GUI.Shapes.RectangleShape;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;

/**
 * @author Ayesha
 *
 */
public class DynamicRectangleWizard extends DynamicShapeWizard
{

  private DynamicShapeTimingsWizardPage _shapeTimingsPage;
  private DynamicRectangleBoundsPage _boundsPage;
  private DynamicShapeStylingPage _stylingPage;
  private DynamicShapeWrapper dynamicShape;
  private Date _startDate;
  private Date _endDate;
  public DynamicRectangleWizard(Date startDate,Date endDate)
  {
    _startDate = startDate;
    _endDate = endDate;
  }
  
  @Override
  public void addPages()
  {
    _shapeTimingsPage = new DynamicShapeTimingsWizardPage("Timings","Rectangle",_startDate,_endDate);
    _boundsPage = new DynamicRectangleBoundsPage("Bounds");
    _stylingPage = new DynamicShapeStylingPage("Styling", "Rectangle");
    addPage(_shapeTimingsPage);
    addPage(_boundsPage);
    addPage(_stylingPage);
  }
  /* (non-Javadoc)
   * @see org.eclipse.jface.wizard.Wizard#performFinish()
   */
  @Override
  public boolean performFinish()
  {
    Date startTime = _shapeTimingsPage.getStartTime();
    Date endTime = _shapeTimingsPage.getEndTime();
    WorldLocation topLeft = _boundsPage.getTopLeftLocation();
    WorldLocation bottomRight = _boundsPage.getBottomRightLocation();
    PlainShape rectangle = new RectangleShape(topLeft, bottomRight);
    final Color theColor = ImportReplay.replayColorFor(_stylingPage.getSymbology());
    if(startTime!=null) {
      dynamicShape = new DynamicShapeWrapper(_stylingPage.getShapeLabel(),rectangle,theColor,new HiResDate(startTime),"rectangle");
    }
    else {
      dynamicShape = new DynamicShapeWrapper(_stylingPage.getShapeLabel(),rectangle,theColor,null,"rectangle");
    }
    if(endTime!=null) {
      dynamicShape.setTimeEnd(new HiResDate(_shapeTimingsPage.getEndTime()));
    }
    return true;
  }
  
  @Override
  public DynamicShapeWrapper getDynamicShapeWrapper()
  {
    return dynamicShape;
  }

}

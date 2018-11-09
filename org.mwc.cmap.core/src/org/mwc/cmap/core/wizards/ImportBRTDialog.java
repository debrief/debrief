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
package org.mwc.cmap.core.wizards;

import java.awt.Color;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import Debrief.ReaderWriter.BRT.BRTHelper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GenericData.WorldDistance;

public class ImportBRTDialog extends CoreFreqImportDialog implements BRTHelper
{

  public ImportBRTDialog()
  {
    this(Display.getDefault().getActiveShell());
  }

  public ImportBRTDialog(Shell parentShell)
  {
    super(parentShell);
    // TODO Auto-generated constructor stub
  }

  @Override
  public Boolean isTowed()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public WorldDistance arrayOffset()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public TrackWrapper select(TrackWrapper[] tracks)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Color getColor()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public WorldDistance defaultLength()
  {
    // TODO Auto-generated method stub
    return null;
  }

}

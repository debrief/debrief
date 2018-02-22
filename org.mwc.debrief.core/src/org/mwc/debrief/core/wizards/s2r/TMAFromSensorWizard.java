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
package org.mwc.debrief.core.wizards.s2r;

import java.awt.Color;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.mwc.cmap.core.wizards.RangeBearingPage;
import org.mwc.cmap.core.wizards.SelectColorPage;
import org.mwc.debrief.core.wizards.EnterSolutionPage;

import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldSpeed;

public class TMAFromSensorWizard extends Wizard
{
  private static final String PAGE_TITLE = "Generate TMA segment";
  RangeBearingPage selectOffsetPage;
  EnterSolutionPage enterSolutionPage;
  private final double _brgDegs;
  private final WorldDistance _range;
  private final double _initialCourse;
  private final WorldSpeed _initialSpeed;
  private boolean _showOffset;
  private SelectColorPage colorPage;
  private final Color _color;

  public TMAFromSensorWizard(final double brgDegs, final WorldDistance range,
      final double initialCourse, final WorldSpeed initialSpeed,
      final Color color)
  {
    this(brgDegs, range, initialCourse, initialSpeed, true, color);
  }

  public TMAFromSensorWizard(final double brgDegs, final WorldDistance range,
      final double initialCourse, final WorldSpeed initialSpeed,
      boolean showOffset, final Color color)
  {
    _brgDegs = brgDegs;
    _range = range;
    _initialCourse = initialCourse;
    _initialSpeed = initialSpeed;
    _showOffset = showOffset;
    _color = color;
  }

  public void addPages()
  {
    final String imagePath = "images/grid_wizard.gif";

    final String helpContext = null;

    // now for the easy fields
    // ok, we need to let the user enter the solution wrapper name
    selectOffsetPage = new RangeBearingPage(null, PAGE_TITLE,
        "Now specify the offset to the track start",
        "range from ownship to start of track",
        "bearing from ownship to start of track", imagePath, helpContext,
        _range, _brgDegs);

    if (_showOffset)
    {
      addPage(selectOffsetPage);
    }

    enterSolutionPage = new EnterSolutionPage(null, PAGE_TITLE,
        "This page lets you enter an initial solution", imagePath, helpContext,
        _initialSpeed, _initialCourse);

    addPage(enterSolutionPage);

    // do we have color?
    if (_color != null)
    {
      // ok, we need to let the user enter the solution wrapper name
      colorPage = new SelectColorPage(null, _color, PAGE_TITLE,
          "Format the sensor cuts used for this leg",
          "The colour to shade the selected sensor cuts (or leave unchanged)",
          imagePath, helpContext, null, true);
      addPage(colorPage);
    }
  }

  public boolean performFinish()
  {
    return true;
  }

  @Override
  public IWizardPage getPage(final String name)
  {
    return super.getPage(name);
  }

}

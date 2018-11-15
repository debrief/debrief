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

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;

import Debrief.ReaderWriter.BRT.BRTHelper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Properties.DebriefColors;
import MWC.GenericData.WorldDistance;

public class ImportBRTDialog extends Wizard implements BRTHelper
{

  private final EnterBooleanPage isTowedPage;
  private final EnterRangePage towedOffsetPage;
  private final SelectColorPage colorPage;
  private final EnterRangePage cutLengthPage;
  private final SelectTrackPage trackPage;
  // private final EnterStringPage
  // Create a page that returns a wizard.
  // It must have a list of tracks in the constructor.
  // having a getSelection method which returns the selected.
  // check setPageComplete ONLY if the track was selected.
  //
  /* private final WizardDialog dialog; */

  @Override
  public Boolean isTowed()
  {
    return isTowedPage.getBoolean();
  }

  public ImportBRTDialog(TrackWrapper autoSelectedTrack, TrackWrapper[] allTracks)
  {
    final String imagePath = "images/NameSensor.jpg";

    final WorldDistance defaultWidth = new WorldDistance(1, WorldDistance.NM);

    this.isTowedPage = new EnterBooleanPage(null, true, "Is It a Towed Array?",
        "BRT Import", "Please, indicate if It is a Towed Array. (yes/no)", null,
        imagePath, "Click Yes if is a Towed Array");
    this.towedOffsetPage = new EnterRangePage(null, "Import Sensor data",
        "Please provide a default range for the sensor cuts \n(or enter 0.0 to leave them as infinite length)",
        "Default range", defaultWidth, imagePath, null, null);
    this.colorPage = new SelectColorPage(null, DebriefColors.BLUE,
        "Import Sensor data", "Now format the new sensor cut",
        "The color for this new sensor cut", imagePath, null, null, false);
    final WorldDistance defRange = new WorldDistance(5, WorldDistance.KYDS);
    this.cutLengthPage = new EnterRangePage(null, "Import Sensor data",
        "Please provide a default length for the sensor cuts \n(or enter 0.0 to leave them as infinite length)",
        "Default range", defRange, imagePath, null, null);
    this.trackPage = new SelectTrackPage(null, "Import Sensor data",
        "Select a track", "Please, select the track to add the sensor data",
        imagePath, null, false, null, allTracks, autoSelectedTrack);
  }

  @Override
  public WorldDistance arrayOffset()
  {
    return towedOffsetPage.getRange();
  }

  @Override
  public TrackWrapper select()
  {
    return trackPage.getValue();
  }

  @Override
  public Color getColor()
  {
    return colorPage.getColor();
  }

  @Override
  public WorldDistance defaultLength()
  {
    return cutLengthPage.getRange();
  }

  @Override
  public boolean performFinish()
  {
    // TODO Auto-generated method stub
    return false;
  }
}

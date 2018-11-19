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
package org.mwc.debrief.core.wizards;

import java.awt.Color;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.mwc.cmap.core.wizards.EnterBooleanPage;
import org.mwc.cmap.core.wizards.EnterRangePage;
import org.mwc.cmap.core.wizards.EnterStringPage;
import org.mwc.cmap.core.wizards.SelectColorPage;
import org.mwc.cmap.core.wizards.SelectTrackPage;

import Debrief.ReaderWriter.BRT.BRTHelper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Properties.DebriefColors;
import MWC.GenericData.WorldDistance;

public class ImportBRTDialog extends Wizard implements BRTHelper
{

  private final EnterStringPage namePage;
  private final EnterBooleanPage isTowedPage;
  private final EnterRangePage towedOffsetPage;
  private final SelectColorPage colorPage;
  private final EnterRangePage cutLengthPage;
  private final SelectTrackPage trackPage;
  private final EnterBooleanPage showSensorOnTrackPage;
  private final TrackWrapper defaultTrack;
  
  // Create a page that returns a wizard.
  // It must have a list of tracks in the constructor.
  // having a getSelection method which returns the selected.
  // check setPageComplete ONLY if the track was selected.
  //

  public ImportBRTDialog(final TrackWrapper autoSelectedTrack,
      final TrackWrapper[] allTracks, final String defaultSensorName)
  {
    final String imagePath = "images/NameSensor.jpg";

    final WorldDistance defaultWidth = new WorldDistance(1, WorldDistance.NM);
    final String TOWED_OFFSET_PREF = "BRT_SENSOR_OFFSET_PREF";
    final String CUTLENGTH_PREF = "BRT_SENSOR_CUTLENGTH_PREF";

    final String PAGE_TITLE = "Import BRT Sensor data";
    final String helpContext = null;
    
    defaultTrack = autoSelectedTrack;
    
    // ok, we need to let the user enter the solution wrapper name
    this.namePage = new EnterStringPage(null, defaultSensorName, PAGE_TITLE,
        "This wizard will lead you through creating a new Sensor.\n"
            + "Please provide the name for this sensor",
        "a one-word title for this block of sensor contacts (e.g. S2046)", imagePath, helpContext, false, null);
    this.isTowedPage = new EnterBooleanPage(null, true, PAGE_TITLE,
        "Please indicate if this data is for a Towed Array",
        "Towed Array Sensor? (yes/no)", imagePath, helpContext,
        "(You'll next be invited to enter the array length if it is)");
    this.towedOffsetPage = new EnterRangePage(null, PAGE_TITLE,
        "Please provide the length of the Towed Array Offset, typically a"
            + " negative value, since it is behind the vessel.", "Array Offset",
        defaultWidth, imagePath, helpContext, null, TOWED_OFFSET_PREF);
    this.colorPage = new SelectColorPage(null, DebriefColors.BLUE, PAGE_TITLE,
        "Please now format the new sensor",
        "The default color for this new sensor", imagePath, helpContext, null,
        false);
    final WorldDistance defRange = new WorldDistance(5, WorldDistance.KYDS);
    this.cutLengthPage = new EnterRangePage(null, PAGE_TITLE,
        "Please provide a default range for the bearing lines\n(or enter 0.0 to leave them as infinite length)",
        "Default range", defRange, imagePath, helpContext, null,
        CUTLENGTH_PREF);
    
    if(defaultTrack == null && allTracks.length > 0)
    {
      this.trackPage = new SelectTrackPage(null, PAGE_TITLE, "Select a track",
          "Please, select the track to add the sensor data", imagePath,
          helpContext, false, null, allTracks, allTracks[0]);      
    }
    else
    {
      this.trackPage = null;
    }
    
    this.showSensorOnTrackPage = new EnterBooleanPage(null, true, PAGE_TITLE,
        "Please, indicate if want the sensor visible once loaded",
        "Sensor visibility (yes/no)", imagePath, helpContext, null);
  }

  @Override
  public void addPages()
  {
    addPage(namePage);
    if (trackPage != null)
    {
      addPage(trackPage);
    }
    addPage(isTowedPage);
    addPage(towedOffsetPage);
    addPage(colorPage);
    addPage(cutLengthPage);
    addPage(showSensorOnTrackPage);
  }

  @Override
  public WorldDistance arrayOffset()
  {
    return towedOffsetPage.getRange();
  }

  @Override
  public WorldDistance defaultLength()
  {
    return cutLengthPage.getRange();
  }

  @Override
  public Color getColor()
  {
    return colorPage.getColor();
  }

  @Override
  public IWizardPage getNextPage(final IWizardPage page)
  {
    if (page == isTowedPage && !isTowedPage.getBoolean())
    {
      return colorPage;
    }
    return super.getNextPage(page);
  }

  @Override
  public Boolean isTowed()
  {
    return isTowedPage.getBoolean();
  }

  @Override
  public Boolean isVisible()
  {
    return showSensorOnTrackPage.getBoolean();
  }

  @Override
  public boolean performFinish()
  {
    return true;
  }

  @Override
  public TrackWrapper select()
  {
    if (defaultTrack != null)
      return defaultTrack;
    else
      return trackPage.getValue();
  }

  @Override
  public String getName()
  {
    return namePage.getString();
  }
}

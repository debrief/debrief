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
package org.mwc.debrief.lite.gui.custom.narratives;

import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.JList;

import org.mwc.debrief.lite.gui.custom.AbstractSelection;
import org.mwc.debrief.lite.gui.custom.JPopupList;

import MWC.TacticalData.NarrativeEntry;

public class JSelectTrackFilter extends JPopupList<TrackNameColor>
{

  /**
   *
   */
  private static final long serialVersionUID = 7136974124331608166L;

  public static JList<TrackNameColor> createTrackFilters(
      final AbstractNarrativeConfiguration model)
  {
    final ArrayList<TrackNameColor> differentTrackNames = new ArrayList<>();
    final HashSet<String> addedTracknames = new HashSet<>();
    for (final AbstractSelection<NarrativeEntry> narrative : model
        .getNarratives())
    {
      if (!addedTracknames.contains(narrative.getItem().getTrackName()))
      {
        addedTracknames.add(narrative.getItem().getTrackName());
        differentTrackNames.add(new TrackNameColor(narrative.getItem()
            .getTrackName(), narrative.getItem().getColor()));
      }
    }

    return new JList<>(differentTrackNames.toArray(new TrackNameColor[]
    {}));
  }

  public JSelectTrackFilter(final AbstractNarrativeConfiguration model)
  {
    super(new TrackPanelItemRenderer(), createTrackFilters(model));
  }
}

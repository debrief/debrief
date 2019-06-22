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
package org.mwc.debrief.lite.gui.custom.narratives;

import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.JList;

import org.mwc.debrief.lite.gui.custom.AbstractSelection;
import org.mwc.debrief.lite.gui.custom.JPopupList;

import MWC.TacticalData.NarrativeEntry;

public class JSelectTypeFilter extends JPopupList<String>
{

  /**
   * 
   */
  private static final long serialVersionUID = 7136974124331608166L;

  public JSelectTypeFilter(final AbstractNarrativeConfiguration model)
  {
    super(new TypeItemRenderer(), createTypeFilters(model));

  }

  public static JList<String> createTypeFilters(
      final AbstractNarrativeConfiguration model)
  {
    final ArrayList<String> differentTypes = new ArrayList<>();
    final HashSet<String> addedTypes = new HashSet<>();
    for (AbstractSelection<NarrativeEntry> narrative : model.getNarratives())
    {
      if (!addedTypes.contains(narrative.getItem().getType()))
      {
        addedTypes.add(narrative.getItem().getTrackName());
        differentTypes.add(narrative.getItem().getTrackName());
      }
    }

    return new JList<>(differentTypes.toArray(new String[]
    {}));
  }
}

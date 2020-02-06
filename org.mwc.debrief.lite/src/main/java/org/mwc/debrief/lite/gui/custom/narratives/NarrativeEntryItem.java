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

import MWC.TacticalData.NarrativeEntry;

public class NarrativeEntryItem implements Comparable<NarrativeEntryItem>
{
  private final NarrativeEntry _entry;
  private final AbstractNarrativeConfiguration _model;

  public NarrativeEntryItem(final NarrativeEntry _entry,
      final AbstractNarrativeConfiguration _model)
  {
    super();
    this._entry = _entry;
    this._model = _model;
  }

  @Override
  public int compareTo(final NarrativeEntryItem o)
  {
    return _entry.getDTG().compareTo(o.getEntry().getDTG());
  }

  public NarrativeEntry getEntry()
  {
    return _entry;
  }

  public AbstractNarrativeConfiguration getModel()
  {
    return _model;
  }

}

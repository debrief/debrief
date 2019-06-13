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

import org.mwc.debrief.lite.gui.custom.JPopupList;

import MWC.TacticalData.NarrativeEntry;

public class JSelectTypeFilter extends JPopupList<NarrativeEntry>
{

  /**
   * 
   */
  private static final long serialVersionUID = 7136974124331608166L;

  final AbstractNarrativeConfiguration _model;
  
  public JSelectTypeFilter(final AbstractNarrativeConfiguration model)
  {
    super(new NarrativePanelItemRenderer(), model.getNarratives());
    this._model = model;

  }
}

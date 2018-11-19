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
package org.mwc.cmap.core.property_support;

import java.util.ArrayList;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.mwc.cmap.core.wizards.SelectTrackPage.TrackDataItem;

import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;

public class TrackSelectionHelper extends EditorHelper
{

  public TrackSelectionHelper()
  {
    super(TrackWrapper.class);
  }

  @Override
  public CellEditor getCellEditorFor(final Composite parent)
  {
    return null;
  }

  @Override
  public Control getEditorControlFor(final Composite parent,
      final IDebriefProperty property)
  {
    final Combo comboBox = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
    final Editable editable = ((DebriefProperty) property).getEditable();
    final TrackDataItem trackDataItem = (TrackDataItem) editable;
    final ArrayList<String> items = new ArrayList<>();
    
    // find the current item
    final TrackWrapper current = trackDataItem.getTrack();
    final String currentStr = current != null ? current.getName() : null;
    
    int ctr = 0;
    int currentIndex = -1;
    for (final TrackWrapper item : trackDataItem.getAllTracksAvailable())
    {
      items.add(item.getName());
      
      // is this the current one?
      if(item.getName().equals(currentStr))
      {
        currentIndex = ctr;
      }
      ctr++;
    }
    comboBox.setItems(items.toArray(new String[]
    {}));
    
    // did we find the index of the current one?
    if(currentIndex != -1)
    {      
      // ok, select it
      comboBox.select(currentIndex);
    }
    
    comboBox.addSelectionListener(new SelectionListener()
    {
      @Override
      public void widgetDefaultSelected(final SelectionEvent e)
      {
        property.setValue(trackDataItem.getItemByName(comboBox.getText()));
      }

      @Override
      public void widgetSelected(final SelectionEvent e)
      {
        property.setValue(trackDataItem.getItemByName(comboBox.getText()));
      }
    });
    return comboBox;
  }

}

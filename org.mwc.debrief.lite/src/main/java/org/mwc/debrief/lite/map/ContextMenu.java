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
package org.mwc.debrief.lite.map;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import Debrief.Wrappers.LabelWrapper;
import MWC.GUI.BaseLayer;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GenericData.WorldLocation;

public class ContextMenu extends JPopupMenu
{
  /**
   * 
   */
  private static final long serialVersionUID = 4242777138907125044L;

  final WorldLocation _clickPosition;
  final Layers _layers;
  final public static String DEFAULT_LAYER_MARKERS = "Markers";
  
  public ContextMenu(final WorldLocation clickPosition, 
      final Layers layers)
  {
    this._clickPosition = clickPosition;
    this._layers = layers;
    
    final JMenuItem addLabelMenuItem = new JMenuItem("Add label under cursor");
    addLabelMenuItem.addActionListener(
        createAddLabelAction());
    
    add(addLabelMenuItem);
  }

  private ActionListener createAddLabelAction()
  {
    return new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        final WorldLocation newLocation = new WorldLocation(_clickPosition);
        final LabelWrapper newLabel = new LabelWrapper("blank label",
            newLocation,
            MWC.GUI.Properties.DebriefColors.ORANGE);
        
        Layer layer = _layers.findLayer(DEFAULT_LAYER_MARKERS, true);
        
        if (layer == null) // Marker layer doesn't exist. :(
        {
          layer = new BaseLayer();
          layer.setName(DEFAULT_LAYER_MARKERS);
          _layers.addThisLayer(layer);
        }
        
        // Ok, Let's add it now.
        layer.add(newLabel);
        // And let's fire the modifiers
        // and fire the extended event
        _layers.fireExtended(newLabel, layer);
      }
    };
  }
}

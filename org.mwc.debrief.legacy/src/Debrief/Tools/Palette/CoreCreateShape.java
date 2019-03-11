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
package Debrief.Tools.Palette;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import MWC.GUI.BaseLayer;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.ToolParent;
import MWC.GUI.Tools.PlainTool;
import MWC.GenericData.WorldArea;

/**
 * @author Ayesha <ayesha.ma@gmail.com>
 *
 */
public abstract class CoreCreateShape extends PlainTool
{
  
  /** the layers we are going to drop this shape into
   */
  protected Layers _theData;
  
  protected JComboBox<String> selectedLayerSource;
  
  protected final BoundsProvider _theBounds;

  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////
  public CoreCreateShape(final ToolParent theParent,
      final String theName,
      final String theImage,
      final Layers theData,
      final BoundsProvider bounds)
  {
    super(theParent, theName, theImage);
    _theData = theData;
    _theBounds = bounds;
  }


  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////
  /** get the current visible data area
   * 
   */
  final protected WorldArea getBounds()
  {
    return _theBounds.getViewport();
  }
  
  //used in debrief lite, to get the select layer from ribbon tab
  public final void setSelectedLayerSource(JComboBox<String> jCombo) {
    selectedLayerSource = jCombo;
  }
  
  
  protected final String getSelectedLayer() {
    if(selectedLayerSource!=null) {
      return (String)selectedLayerSource.getSelectedItem();
    }
    return null;
  }
  /**
   * @return
   */
  protected String getLayerName()
  {
    String res = null;
    // ok, are we auto-deciding?
    if (!AutoSelectTarget.getAutoSelectTarget())
    {
      // nope, just use the default layer
      res = Layers.DEFAULT_TARGET_LAYER;
    }
    else
    {
      // get the non-track layers
      final Layers theLayers = _theData;
      final String[] ourLayers = theLayers.trimmedLayers();
      ListLayersDialog listDialog = new ListLayersDialog(ourLayers);
      listDialog.setSize(350,300);
      listDialog.setLocationRelativeTo(null);
      listDialog.setModal(true);
      listDialog.setVisible(true);
      String selection = listDialog.getSelectedItem();
      // did user say yes?
      if (selection != null)
      {
        // hmm, is it our add layer command?
        if (selection.equals(Layers.NEW_LAYER_COMMAND))
        {
          // better create one. Ask the user

          // create input box dialog
          String txt = JOptionPane.showInputDialog(null, "Enter name for new layer");
          // check there's something there
          if (!txt.isEmpty())
          {
            res = txt;
            // create base layer
            final Layer newLayer = new BaseLayer();
            newLayer.setName(res);

            // add to layers object
            theLayers.addThisLayer(newLayer);
          }
          else
          {
            res = null;
          }
        }
        else {
          res = selection;
        }
      }
    }
      
    return res;
  }

}

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
import MWC.GUI.PlainWrapper;
import MWC.GUI.ToolParent;
import MWC.GUI.Properties.PropertiesPanel;
import MWC.GUI.Tools.Action;
import MWC.GUI.Tools.PlainTool;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

/**
 * @author Ayesha <ayesha.ma@gmail.com>
 *
 */
public abstract class CoreCreateShape extends PlainTool
{

  public static final String USER_SELECTED_LAYER_COMMAND =
      "User-selected Layer";
  
  public static final String SELECT_LAYER_COMMAND =
      "Select Layer";
  
  /**
   * the layers we are going to drop this shape into
   */
  protected Layers _theData;
  protected PropertiesPanel _thePanel;

  protected JComboBox<String> selectedLayerSource;

  protected final BoundsProvider _theBounds;

  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////
  public CoreCreateShape(final ToolParent theParent, final String theName,
      final String theImage, final Layers theData, final BoundsProvider bounds)
  {
    super(theParent, theName, theImage);
    _theData = theData;
    _theBounds = bounds;
  }

  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////
  /**
   * get the current visible data area
   * 
   */
  final protected WorldArea getBounds()
  {
    return _theBounds.getViewport();
  }

  /**
   * used in debrief lite, to get the select layer from ribbon tab
   * 
   * @param jCombo
   */
  public final void setSelectedLayerSource(JComboBox<String> jCombo)
  {
    selectedLayerSource = jCombo;
  }

  protected final String getSelectedLayer()
  {
    if (selectedLayerSource != null && !SELECT_LAYER_COMMAND.equals(selectedLayerSource.getSelectedItem()))
    {
        return (String) selectedLayerSource.getSelectedItem();
    }
    return null;
  }
  
  abstract protected Action createAction(PropertiesPanel thePanel, Layer theLayer, PlainWrapper theWrapper, final Layers theLayers) ;
  abstract protected PlainWrapper createWrapper(WorldLocation centre) ;
    
  public Action getData()
  {
    final Action res;
    final WorldArea wa = getBounds();
    boolean userSelected=false;
    if(wa != null)
    {
      // put the label in the centre of the plot (at the surface)
      final WorldLocation centre = wa.getCentreAtSurface();

      final PlainWrapper theWrapper = createWrapper(centre);
      final Layer theLayer;
      String layerToAddTo = getSelectedLayer();
      final boolean wantsUserSelected =
          CoreCreateShape.USER_SELECTED_LAYER_COMMAND.equals(layerToAddTo);
      if (wantsUserSelected || Layers.NEW_LAYER_COMMAND.equals(layerToAddTo))
      {
        userSelected = true;
        if (wantsUserSelected)
        {
          layerToAddTo = getLayerName();
        }
        else
        {
          String txt = JOptionPane.showInputDialog(null,
              "Enter name for new layer");
          // check there's something there
          if (txt != null && !txt.isEmpty())
          {
            layerToAddTo = txt;
            // create base layer
            final Layer newLayer = new BaseLayer();
            newLayer.setName(layerToAddTo);

            // add to layers object
            _theData.addThisLayer(newLayer);
          }
        }      
      }
      
      // do we know the target layer name?
      if (layerToAddTo != null)
      {
        theLayer = _theData.findLayer(layerToAddTo);
      }
      else
      {
        theLayer = null;
      }

      // do we know the target layer?
      if(theLayer == null)
      {
        // no, did the user choose to not select a layer?
        if(userSelected)
        {
          // works for debrief-legacy
          // user cancelled.
          JOptionPane.showMessageDialog(null,
              "A layer can only be created if a name is provided. "
                  + "The shape has not been created", "Error",
              JOptionPane.ERROR_MESSAGE);
          res = null;          
        }
        else
        {
          // create a default layer, for the item to go into
          if(_theData.findLayer("Misc")==null) {
            final BaseLayer tmpLayer = new BaseLayer();
            tmpLayer.setName("Misc");
            _theData.addThisLayer(tmpLayer);
            res = createAction(_thePanel, tmpLayer, theWrapper, _theData);
          }
          else {
            final Layer tmpLayer = _theData.findLayer("Misc");
            res = createAction(_thePanel, tmpLayer, theWrapper, _theData);
          }
          
          // action to put the shape into this new layer
          
        }
      }
      else
      {
        res = createAction(_thePanel, theLayer, theWrapper, _theData);
      }
    }
    else
    {
      // we haven't got an area, inform the user
      MWC.GUI.Dialogs.DialogFactory.showMessage("Create Feature",
          "Sorry, we can't create a shape until the area is defined.  Try adding a coastline first");
      res = null;
    }
    return res;
  }

  /**
   * @return
   */
  protected String getLayerName()
  {
    String res = null;
    // get the non-track layers
    final Layers theLayers = _theData;
    final String[] ourLayers = theLayers.trimmedLayers();
    ListLayersDialog listDialog = new ListLayersDialog(ourLayers);
    listDialog.setSize(350, 300);
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
        String txt = JOptionPane.showInputDialog(null,
            "Enter name for new layer", "New Layer");
        // check there's something there
        if (txt != null && !txt.isEmpty())
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
      else
      {
        res = selection;
      }
    }
    return res;
  }
}

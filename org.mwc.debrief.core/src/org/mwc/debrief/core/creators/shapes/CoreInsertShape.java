/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.debrief.core.creators.shapes;

import java.util.Date;
import java.util.Enumeration;

import javax.swing.JOptionPane;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.ListDialog;
import org.mwc.debrief.core.creators.chartFeatures.CoreInsertChartFeature;

import Debrief.Wrappers.ShapeWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.PlainChart;
import MWC.GUI.Plottable;
import MWC.GUI.Shapes.PlainShape;
import MWC.GenericData.WorldLocation;

/**
 * @author ian.mayo
 */
abstract public class CoreInsertShape extends CoreInsertChartFeature
{

  /**
   * the target layer where we dump new items
   * 
   */
  private static final String DEFAULT_TARGET_LAYER = "Misc";

  /**
   * get a plottable object
   * 
   * @param centre
   * @param theChart
   * @return
   */
  protected Plottable getPlottable(final PlainChart theChart)
  {
    // get centre of area
    final WorldLocation centre = new WorldLocation(getCentre(theChart));

    // create the shape, based on the centre
    final PlainShape shape = getShape(centre);

    // and now wrap the shape
    final ShapeWrapper theWrapper = new ShapeWrapper("New " + getShapeName(),
        shape, PlainShape.DEFAULT_COLOR, null);

    return theWrapper;

  }

  /**
   * @return
   */
  protected String getLayerName()
  {
    final String res;
    // ok, are we auto-deciding?
    if (!AutoSelectTarget.getAutoSelectTarget())
    {
      // nope, just use the default layer
      res = DEFAULT_TARGET_LAYER;
    }
    else
    {
      // ok, get the non-track layers for the current plot

      // get the current plot
      final PlainChart theChart = getChart();

      // get the non-track layers
      final Layers theLayers = theChart.getLayers();
      final String[] ourLayers = theLayers.trimmedLayers();

      // popup the layers in a question dialog
      final IStructuredContentProvider theVals = new ArrayContentProvider();
      final ILabelProvider theLabels = new LabelProvider();

      // collate the dialog
      final ListDialog list = new ListDialog(Display.getCurrent()
          .getActiveShell());
      list.setContentProvider(theVals);
      list.setLabelProvider(theLabels);
      list.setInput(ourLayers);
      list.setMessage("Please select the destination layer for new feature");
      list.setTitle("Adding new drawing feature");
      list.setHelpAvailable(false);

      // select the first item, so it's valid to press OK immediately
      list.setInitialSelections(new Object[]
      {ourLayers[0]});

      // open it
      final int selection = list.open();

      // did user say yes?
      if (selection != ListDialog.CANCEL)
      {
        // yup, store it's name
        final Object[] val = list.getResult();

        // check something got selected
        if (val.length > 0)
        {
          final String selStr = val[0].toString();

          // hmm, is it our add layer command?
          if (selStr.equals(Layers.NEW_LAYER_COMMAND))
          {
            // better create one. Ask the user

            // create input box dialog
            InputDialog dlg = new InputDialog(Display.getCurrent()
                .getActiveShell(), "Please enter name", "New Layer", "", null);

            if (dlg.open() == Window.OK)
            {
              res = dlg.getValue();
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
            // just use the selected string
            res = selStr;
          }
        }
        else
        {
          res = null;
        }
      }
      else
      {
        res = null;
      }
    }
    return res;
  }

  /**
   * produce the shape for the user
   * 
   * @param centre
   *          the current centre of the screen
   * @return a shape, based on the centre
   */
  abstract protected PlainShape getShape(WorldLocation centre);

  /**
   * return the name of this shape, used give the shape an initial name
   * 
   * @return the name of this type of shape, eg: rectangle
   */
  abstract protected String getShapeName();

  protected Date getTimeControllerDate(Layers layers, boolean startDate)
  {
    Date timeControllerDate = null;
    Enumeration<Editable> elements = layers.elements();
    while (elements.hasMoreElements())
    {
      Editable elem = elements.nextElement();
      if (elem instanceof TrackWrapper)
      {
        TrackWrapper theTrack = (TrackWrapper) elem;
        if (startDate)
        {
          if (timeControllerDate == null || theTrack.getStartDTG().getDate()
              .before(timeControllerDate))
          {
            timeControllerDate = theTrack.getStartDTG().getDate();
          }
        }
        else
        {
          if (timeControllerDate == null || theTrack.getEndDTG().getDate()
              .after(timeControllerDate))
          {
            timeControllerDate = theTrack.getEndDTG().getDate();
          }
        }
      }
    }
    return timeControllerDate;
  }

}
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

import java.util.Enumeration;
import java.util.Vector;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.ListDialog;
import org.mwc.debrief.core.creators.chartFeatures.CoreInsertChartFeature;

import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.PlainChart;

/** 
 * This class is to add a sensor arc to a track.
 *  
 * @author Ayesha
 */
abstract public class CoreInsertSensorArc extends CoreInsertChartFeature
{
  //  this is abstract, we are not implementing getplottable here.
  
  /**
   * @return
   */
  protected String getLayerName()
  {
    String res = null;
    final PlainChart theChart = getChart();

    // get the non-track layers
    final Layers theLayers = theChart.getLayers();

    //it is a sensor arc, so add to track
    final String[] ourLayers = trimmedLayers(theLayers);
    final String listTitle = "Add sensor arc to a track";
    final String listDescription = "Please select the destination track for the sensor arc";
    // popup the layers in a question dialog
    final IStructuredContentProvider theVals = new ArrayContentProvider();
    final ILabelProvider theLabels = new LabelProvider();

    // collate the dialog
    final ListDialog list = new ListDialog(Display.getCurrent().getActiveShell());
    list.setContentProvider(theVals);
    list.setLabelProvider(theLabels);
    list.setInput(ourLayers);
    list.setMessage(listDescription);
    list.setTitle(listTitle);
    list.setHelpAvailable(false);

    // select the first item, so it's valid to press OK immediately
    list.setInitialSelections(new Object[]
        { ourLayers[0] });

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
        res = val[0].toString();
      }
    }
    return res;
  }


  /**
   * find the list of layers that could receive a new dynamic shape item.
   * we only want to add a dynamic shape to a track
   * 
   * @param theLayers
   *          the list to search through
   * @return receptive layers (those derived from BaseLayer).
   */
  private String[] trimmedLayers(final Layers theLayers)
  {
    final Vector<String> res = new Vector<String>(0, 1);
    final Enumeration<Editable> enumer = theLayers.elements();
    while (enumer.hasMoreElements())
    {
      final Layer thisLayer = (Layer) enumer.nextElement();
      if (thisLayer instanceof TrackWrapper)
      {
        res.add(thisLayer.getName());
      }
    }

    final String[] sampleArray = new String[]
        { "aa" };
    return res.toArray(sampleArray);
  }


}
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
package Debrief.ReaderWriter.XML.Formatters;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.w3c.dom.Element;

import Debrief.Wrappers.Formatters.HideLayerFormatListener;
import Debrief.Wrappers.Formatters.TrackNameAtEndFormatListener;
import MWC.Utilities.ReaderWriter.AbstractPlainLineImporter;

public abstract class HideLayerFormatHandler extends
    MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{
  private static final String MY_TYPE = "HideLayerFormatter";

  /**
   * and define the strings used to describe the shape
   * 
   */
  private static final String NAME = "Name";
  private static final String ACTIVE = "Active";
  private static final String LAYERS = "Layers";

  private String fName;
  private List<String> layerNames;

  protected boolean active;

  public HideLayerFormatHandler()
  {
    super(MY_TYPE);

    addAttributeHandler(new HandleAttribute(NAME)
    {
      public void setValue(final String name, final String value)
      {
        fName = value;
      }
    });
    addAttributeHandler(new HandleAttribute(LAYERS)
    {
      public void setValue(final String name, final String value)
      {
        // check it's non-empty
        if (value.length() > 0)
        {
          layerNames = new ArrayList<String>();

          // ok, parse the tracks
          // get a stream from the string
          final StringTokenizer st = new StringTokenizer(value);

          while (st.hasMoreElements())
          {
            String nextItem =
                AbstractPlainLineImporter.checkForQuotedName(st).trim();
            if (nextItem != null && nextItem.length() > 0)
            {
              layerNames.add(nextItem);
            }
          }
        }

      }
    });
    addAttributeHandler(new HandleBooleanAttribute(ACTIVE)
    {
      public void setValue(final String name, final boolean value)
      {
        active = value;
      }
    });

  }

  public void elementClosed()
  {
    String[] names = null;
    if (layerNames != null)
    {
      names = layerNames.toArray(new String[]
      {});
    }

    // create the object
    TrackNameAtEndFormatListener listener =
        new TrackNameAtEndFormatListener(fName, names);

    addFormatter(listener);

    // reset the local parameters
    fName = null;
    layerNames = null;
    active = true;
  }

  abstract public void addFormatter(MWC.GUI.Editable editable);

  static public void exportThisPlottable(final MWC.GUI.Plottable plottable,
      final org.w3c.dom.Element parent, final org.w3c.dom.Document doc)
  {

    Element theFormatter = doc.createElement(MY_TYPE);
    parent.appendChild(theFormatter);

    final HideLayerFormatListener theShape =
        (HideLayerFormatListener) plottable;

    // put the parameters into the parent
    theFormatter.setAttribute(NAME, theShape.getName());
    StringBuffer layerNames = new StringBuffer();
    String[] tracks = theShape.getLayers();
    for (int i = 0; i < tracks.length; i++)
    {
      String string = tracks[i];
      layerNames.append(string);
      layerNames.append(" ");
    }

    theFormatter.setAttribute(NAME, theShape.getName());
    theFormatter.setAttribute(LAYERS, layerNames.toString());
    theFormatter.setAttribute(ACTIVE, writeThis(theShape.getVisible()));
  }

}
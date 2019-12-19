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

import Debrief.Wrappers.Formatters.SliceTrackFormatListener;
import MWC.Utilities.ReaderWriter.AbstractPlainLineImporter;
import MWC.Utilities.ReaderWriter.XML.Util.DurationHandler;

public abstract class SliceTargetFormatHandler extends
    MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{
  private static final String MY_TYPE = "SplitTrackFormatter";

  /**
   * and define the strings used to describe the shape
   *
   */
  private static final String NAME = "Name";
  private static final String ACTIVE = "Active";
  private static final String T_NAMES = "Track_Names";
  private static final String INTERVAL = "Interval";

  static public void exportThisPlottable(final MWC.GUI.Plottable plottable,
      final org.w3c.dom.Element parent, final org.w3c.dom.Document doc)
  {

    final Element theFormatter = doc.createElement(MY_TYPE);
    parent.appendChild(theFormatter);

    final SliceTrackFormatListener theShape =
        (SliceTrackFormatListener) plottable;

    // put the parameters into the parent
    theFormatter.setAttribute(NAME, theShape.getName());
    final StringBuffer layerNames = new StringBuffer();
    final List<String> tracks = theShape.getTrackNames();
    for (final String name : tracks)
    {
      layerNames.append(name);
      layerNames.append(" ");
    }

    theFormatter.setAttribute(NAME, theShape.getName());
    theFormatter.setAttribute(ACTIVE, writeThis(theShape.getVisible()));
    theFormatter.setAttribute(T_NAMES, layerNames.toString());    
    DurationHandler.exportDuration(INTERVAL, theShape.getInterval(), theFormatter, doc);
  }

  private String fName;
  private List<String> track_names;

  private long interval;

  protected boolean active;

  public SliceTargetFormatHandler()
  {
    super(MY_TYPE);

    addAttributeHandler(new HandleAttribute(NAME)
    {
      @Override
      public void setValue(final String name, final String value)
      {
        fName = value;
      }
    });
    addAttributeHandler(new HandleAttribute(INTERVAL)
    {
      @Override
      public void setValue(final String name, final String value)
      {
        interval = Long.parseLong(value);
      }
    });
    addAttributeHandler(new HandleAttribute(T_NAMES)
    {
      @Override
      public void setValue(final String name, final String value)
      {
        // check it's non-empty
        if (value.length() > 0)
        {
          track_names = new ArrayList<String>();

          // ok, parse the tracks
          // get a stream from the string
          final StringTokenizer st = new StringTokenizer(value);

          while (st.hasMoreElements())
          {
            final String nextItem = AbstractPlainLineImporter
                .checkForQuotedName(st).trim();
            if (nextItem != null && nextItem.length() > 0)
            {
              track_names.add(nextItem);
            }
          }
        }

      }
    });
    addAttributeHandler(new HandleBooleanAttribute(ACTIVE)
    {
      @Override
      public void setValue(final String name, final boolean value)
      {
        active = value;
      }
    });

  }

  abstract public void addFormatter(MWC.GUI.Editable editable);

  @Override
  public void elementClosed()
  {
    // create the object
    final SliceTrackFormatListener listener = new SliceTrackFormatListener(
        fName, interval, track_names);

    addFormatter(listener);

    // reset the local parameters
    fName = null;
    track_names = null;
    active = true;
  }

}
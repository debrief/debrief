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
package Debrief.ReaderWriter.XML.GUI;

import java.util.Vector;

import MWC.GUI.Editable;
import MWC.GenericData.WatchableList;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 *
 * @author Ian Mayo
 * @version 1.0
 */


public final class ToteHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{

  final Debrief.GUI.Tote.AnalysisTote _myTote;
  private final MWC.GUI.Layers _theData;

  public ToteHandler(final Debrief.GUI.Tote.AnalysisTote theTote, final MWC.GUI.Layers theData)
  {
    // inform our parent what type of class we are
    super("tote");

    _myTote = theTote;

    if (_myTote == null)
      System.err.println("Tote information missing from Tote handler");

    _theData = theData;

    addHandler(new PrimarySecondaryHandler("primary")
    {
      public void setTrack(final String name)
      {
        final MWC.GenericData.WatchableList wa = getTrack(name);
        if (wa != null)
        {
          if (_myTote != null)
          {
            // set this as primary
            _myTote.setPrimary(wa);
          }
        }
      }
    });

    addHandler(new PrimarySecondaryHandler("secondary")
    {
      public void setTrack(final String name)
      {
        final MWC.GenericData.WatchableList wa = getTrack(name);
        if (wa != null)
        {
          if (_myTote != null)
          {
            // set this as primary
            _myTote.setSecondary(wa);
          }
        }
      }
    });

  }

  MWC.GenericData.WatchableList getTrack(final String name)
  {
    MWC.GenericData.WatchableList res = null;

    // look at the data
    MWC.GUI.Plottable ly = _theData.findLayer(name);

    if (ly == null)
    {
      // no, this isn't a top level layer, maybe it's an element

      // find the nearest editable item
      final int num = _theData.size();
      for (int i = 0; i < num; i++)
      {
        final MWC.GUI.Layer thisL = _theData.elementAt(i);
        // go through this layer
        final java.util.Enumeration<Editable> iter = thisL.elements();
        while (iter.hasMoreElements())
        {
          final MWC.GUI.Plottable p = (MWC.GUI.Plottable) iter.nextElement();
          final String nm = p.getName();
          if (nm.equals(name))
          {
            ly = p;
            break;
          }
        }
      }

    }

    if (ly instanceof MWC.GenericData.WatchableList)
    {
      res = (MWC.GenericData.WatchableList) ly;
    }

    return res;
  }

  public static void exportTote(final Debrief.GUI.Frames.Session session, final org.w3c.dom.Element parent,
                                final org.w3c.dom.Document doc)
  {
    // create the element to put it in
    final org.w3c.dom.Element tote = doc.createElement("tote");
    final Debrief.GUI.Views.PlainView pv = session.getCurrentView();
    Debrief.GUI.Views.AnalysisView av = null;
    if (pv instanceof Debrief.GUI.Views.AnalysisView)
    {
      av = (Debrief.GUI.Views.AnalysisView) pv;
    }

    if (av == null)
      return;

    // get the tote itself
    final Debrief.GUI.Tote.AnalysisTote _theTote = av.getTote();


    // now output the parts of the tote
    // find the primary
    final MWC.GenericData.WatchableList primary = _theTote.getPrimary();
    final Vector<WatchableList> secondaries = _theTote.getSecondary();

    if (primary != null)
    {
      final org.w3c.dom.Element pri = doc.createElement("primary");
      pri.setAttribute("Name", primary.getName());
      tote.appendChild(pri);
    }

    if (secondaries != null)
    {
      if (secondaries.size() > 0)
      {
        final java.util.Enumeration<WatchableList> iter = secondaries.elements();
        while (iter.hasMoreElements())
        {
          final MWC.GenericData.WatchableList was = iter.nextElement();
          final org.w3c.dom.Element sec = doc.createElement("secondary");
          sec.setAttribute("Name", was.getName());
          tote.appendChild(sec);
        }
      }
    }

    //////////////////////////////
    // and finally add ourselves to the parent
    parent.appendChild(tote);
  }

}
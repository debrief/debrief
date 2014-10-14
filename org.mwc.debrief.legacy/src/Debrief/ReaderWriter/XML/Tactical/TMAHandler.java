/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package Debrief.ReaderWriter.XML.Tactical;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import MWC.GUI.Editable;
import MWC.Utilities.ReaderWriter.XML.Util.ColourHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;


abstract public class TMAHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{

  private static final String MY_NAME = "tma";

  private static final String SHOW_BEARING_LINES = "BearingLineVisible";
  private static final String SHOW_LABELS = "LabelsVisible";

  // our "working" track
  Debrief.Wrappers.TMAWrapper _mySolutionTrack;

  public TMAHandler()
  {
    // inform our parent what type of class we are
    super(MY_NAME);

    addHandler(new ColourHandler()
    {
      public void setColour(final java.awt.Color res)
      {
        _mySolutionTrack.setColor(res);
      }
    });

    addHandler(new TMAContactHandler()
    {
      public void addSolution(final MWC.GUI.Plottable contact)
      {
        addThisContact(contact);
      }
    });
    addAttributeHandler(new HandleAttribute("Name")
    {
      public void setValue(final String name, final String val)
      {
        _mySolutionTrack.setName(fromXML(val));
      }
    });
    addAttributeHandler(new HandleAttribute("TrackName")
    {
      public void setValue(final String name, final String val)
      {
        _mySolutionTrack.setTrackName(val);
      }
    });
    addAttributeHandler(new HandleBooleanAttribute("Visible")
    {
      public void setValue(final String name, final boolean val)
      {
        _mySolutionTrack.setVisible(val);
      }
    });
    addAttributeHandler(new HandleBooleanAttribute(SHOW_BEARING_LINES)
    {
      public void setValue(final String name, final boolean val)
      {
        _mySolutionTrack.setUnderlyingBearingLineVisibility(val);
      }
    });
        addAttributeHandler(new HandleBooleanAttribute(SHOW_LABELS)
    {
      public void setValue(final String name, final boolean val)
      {
        _mySolutionTrack.setShowLabels(val);
      }
    });
    addAttributeHandler(new HandleIntegerAttribute("LineThickness")
    {
      public void setValue(final String name, final int val)
      {
        _mySolutionTrack.setLineThickness(val);
      }
    });

  }

  // this is one of ours, so get on with it!
  protected final void handleOurselves(final String name, final Attributes attributes)
  {
    _mySolutionTrack = new Debrief.Wrappers.TMAWrapper("");

    super.handleOurselves(name, attributes);

  }

  void addThisContact(final MWC.GUI.Plottable val)
  {
    // store in our list
    _mySolutionTrack.add(val);

    // and tell it that we are it's sensors
    final Debrief.Wrappers.TMAContactWrapper scw = (Debrief.Wrappers.TMAContactWrapper) val;
    scw.setTMATrack(_mySolutionTrack);
  }

  public final void elementClosed()
  {
    // our layer is complete, add it to the parent!
    addContact(_mySolutionTrack);

    _mySolutionTrack = null;
  }

  abstract public void addContact(Debrief.Wrappers.TMAWrapper data);

  public static void exportSolutionTrack(final Debrief.Wrappers.TMAWrapper sensor, final Element parent, final Document doc)
  {

    /*
    <!ELEMENT sensor (colour,((fix|contact)*))>
    <!ATTLIST track
      name CDATA #REQUIRED
      visible (TRUE|FALSE) "TRUE"
      PositionsLinked (TRUE|FALSE) "TRUE"
      NameVisible (TRUE|FALSE) "TRUE"
      PositionsVisible (TRUE|FALSE) "TRUE"
      NameAtStart (TRUE|FALSE) "TRUE"
      NameLocation (Top|Left|Bottom|Centre|Right) "Right"
      Symbol CDATA "SQUARE"
    >
    */
    final Element trk = doc.createElement(MY_NAME);
    trk.setAttribute("Name", toXML(sensor.getName()));
    trk.setAttribute("Visible", writeThis(sensor.getVisible()));
    trk.setAttribute("TrackName", sensor.getTrackName());
    trk.setAttribute("LineThickness", writeThis(sensor.getLineThickness()));
    trk.setAttribute(SHOW_BEARING_LINES, writeThis(sensor.getShowBearingLines()));
    trk.setAttribute(SHOW_LABELS, writeThis(sensor.getShowLabels()));
    ColourHandler.exportColour(sensor.getColor(), trk, doc);

    // now the points
    final java.util.Enumeration<Editable> iter = sensor.elements();
    while (iter.hasMoreElements())
    {
      final MWC.GUI.Plottable pl = (MWC.GUI.Plottable) iter.nextElement();
      if (pl instanceof Debrief.Wrappers.TMAContactWrapper)
      {
        final Debrief.Wrappers.TMAContactWrapper fw = (Debrief.Wrappers.TMAContactWrapper) pl;
        TMAContactHandler.exportSolution(fw, trk, doc);
      }

    }

    parent.appendChild(trk);
  }


}
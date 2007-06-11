package Debrief.ReaderWriter.XML.Tactical;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

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
  private Debrief.Wrappers.TMAWrapper _mySolutionTrack;

  public TMAHandler()
  {
    // inform our parent what type of class we are
    super(MY_NAME);

    addHandler(new ColourHandler()
    {
      public void setColour(java.awt.Color res)
      {
        _mySolutionTrack.setColor(res);
      }
    });

    addHandler(new TMAContactHandler()
    {
      public void addSolution(MWC.GUI.Plottable contact)
      {
        addThisContact(contact);
      }
    });
    addAttributeHandler(new HandleAttribute("Name")
    {
      public void setValue(String name, String val)
      {
        _mySolutionTrack.setName(fromXML(val));
      }
    });
    addAttributeHandler(new HandleAttribute("TrackName")
    {
      public void setValue(String name, String val)
      {
        _mySolutionTrack.setTrackName(val);
      }
    });
    addAttributeHandler(new HandleBooleanAttribute("Visible")
    {
      public void setValue(String name, boolean val)
      {
        _mySolutionTrack.setVisible(val);
      }
    });
    addAttributeHandler(new HandleBooleanAttribute(SHOW_BEARING_LINES)
    {
      public void setValue(String name, boolean val)
      {
        _mySolutionTrack.setUnderlyingBearingLineVisibility(val);
      }
    });
        addAttributeHandler(new HandleBooleanAttribute(SHOW_LABELS)
    {
      public void setValue(String name, boolean val)
      {
        _mySolutionTrack.setShowLabels(val);
      }
    });
    addAttributeHandler(new HandleIntegerAttribute("LineThickness")
    {
      public void setValue(String name, int val)
      {
        _mySolutionTrack.setLineThickness(val);
      }
    });

  }

  // this is one of ours, so get on with it!
  protected final void handleOurselves(String name, Attributes attributes)
  {
    _mySolutionTrack = new Debrief.Wrappers.TMAWrapper("");

    super.handleOurselves(name, attributes);

  }

  private void addThisContact(MWC.GUI.Plottable val)
  {
    // store in our list
    _mySolutionTrack.add(val);

    // and tell it that we are it's sensors
    Debrief.Wrappers.TMAContactWrapper scw = (Debrief.Wrappers.TMAContactWrapper) val;
    scw.setTMATrack(_mySolutionTrack);
  }

  public final void elementClosed()
  {
    // our layer is complete, add it to the parent!
    addContact(_mySolutionTrack);

    _mySolutionTrack = null;
  }

  abstract public void addContact(Debrief.Wrappers.TMAWrapper data);

  public static void exportSolutionTrack(Debrief.Wrappers.TMAWrapper sensor, Element parent, Document doc)
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
    Element trk = doc.createElement(MY_NAME);
    trk.setAttribute("Name", toXML(sensor.getName()));
    trk.setAttribute("Visible", writeThis(sensor.getVisible()));
    trk.setAttribute("TrackName", sensor.getTrackName());
    trk.setAttribute("LineThickness", writeThis(sensor.getLineThickness()));
    trk.setAttribute(SHOW_BEARING_LINES, writeThis(sensor.getShowBearingLines()));
    trk.setAttribute(SHOW_LABELS, writeThis(sensor.getShowLabels()));
    ColourHandler.exportColour(sensor.getColor(), trk, doc);

    // now the points
    java.util.Enumeration iter = sensor.elements();
    while (iter.hasMoreElements())
    {
      MWC.GUI.Plottable pl = (MWC.GUI.Plottable) iter.nextElement();
      if (pl instanceof Debrief.Wrappers.TMAContactWrapper)
      {
        Debrief.Wrappers.TMAContactWrapper fw = (Debrief.Wrappers.TMAContactWrapper) pl;
        TMAContactHandler.exportSolution(fw, trk, doc);
      }

    }

    parent.appendChild(trk);
  }


}
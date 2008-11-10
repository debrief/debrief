package Debrief.ReaderWriter.XML.Tactical;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import Debrief.Wrappers.SensorContactWrapper;
import MWC.Utilities.ReaderWriter.XML.Util.ColourHandler;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;


abstract public class SensorHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{
  // our "working" track
  private Debrief.Wrappers.SensorWrapper _mySensor;

  public SensorHandler()
  {
    // inform our parent what type of class we are
    super("sensor");

    addHandler(new ColourHandler()
    {
      public void setColour(java.awt.Color res)
      {
        _mySensor.setColor(res);
      }
    });

    addHandler(new SensorContactHandler()
    {
      public void addContact(MWC.GUI.Plottable contact)
      {
        addThisContact(contact);
        
        // and set the sensor for that contact
        SensorContactWrapper sc = (SensorContactWrapper) contact;
        sc.setSensor(_mySensor);
        
      }
    });
    addAttributeHandler(new HandleAttribute("Name")
    {
      public void setValue(String name, String val)
      {
        _mySensor.setName(fromXML(val));
      }
    });
    addAttributeHandler(new HandleAttribute("TrackName")
    {
      public void setValue(String name, String val)
      {
        _mySensor.setTrackName(val);
      }
    });
    addAttributeHandler(new HandleBooleanAttribute("Visible")
    {
      public void setValue(String name, boolean val)
      {
        _mySensor.setVisible(val);
      }
    });
    addAttributeHandler(new HandleIntegerAttribute("LineThickness")
    {
      public void setValue(String name, int val)
      {
        _mySensor.setLineThickness(val);
      }
    });
  }

  // this is one of ours, so get on with it!
  protected final void handleOurselves(String name, Attributes attributes)
  {
    _mySensor = new Debrief.Wrappers.SensorWrapper("");

    super.handleOurselves(name, attributes);

  }

  private void addThisContact(MWC.GUI.Plottable val)
  {
    // store in our list
    _mySensor.add(val);
  }

  public final void elementClosed()
  {
    // our layer is complete, add it to the parent!
    addSensor(_mySensor);

    _mySensor = null;
  }

  abstract public void addSensor(Debrief.Wrappers.SensorWrapper data);

  public static void exportSensor(Debrief.Wrappers.SensorWrapper sensor, org.w3c.dom.Element parent,
                                  org.w3c.dom.Document doc)
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
    Element trk = doc.createElement("sensor");
    trk.setAttribute("Name", toXML(sensor.getName()));
    trk.setAttribute("Visible", writeThis(sensor.getVisible()));
    trk.setAttribute("TrackName", sensor.getTrackName());
    trk.setAttribute("LineThickness", writeThis(sensor.getLineThickness()));
    ColourHandler.exportColour(sensor.getColor(), trk, doc);

    // now the points
    java.util.Enumeration iter = sensor.elements();
    while (iter.hasMoreElements())
    {
      MWC.GUI.Plottable pl = (MWC.GUI.Plottable) iter.nextElement();
      if (pl instanceof Debrief.Wrappers.SensorContactWrapper)
      {
        Debrief.Wrappers.SensorContactWrapper fw = (Debrief.Wrappers.SensorContactWrapper) pl;
        SensorContactHandler.exportFix(fw, trk, doc);
      }

    }

    parent.appendChild(trk);
  }


}
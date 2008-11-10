package Debrief.ReaderWriter.XML.Tactical;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import org.w3c.dom.Element;
import org.xml.sax.Attributes;

import MWC.Utilities.ReaderWriter.XML.Util.*;


abstract public class SensorContactHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{
  private Debrief.Wrappers.SensorContactWrapper _theContact;

  /**
   * class which contains list of textual representations of label locations
   */
  static private final MWC.GUI.Properties.LocationPropertyEditor lp
    = new MWC.GUI.Properties.LocationPropertyEditor();

  /**
   * class which contains list of textual representations of label locations
   */
  static private final MWC.GUI.Properties.LineLocationPropertyEditor ll
    = new MWC.GUI.Properties.LineLocationPropertyEditor();

  /**
   * class which contains list of textual representations of label locations
   */
  static private final MWC.GUI.Properties.LineStylePropertyEditor ls
    = new MWC.GUI.Properties.LineStylePropertyEditor();

  public SensorContactHandler()
  {
    // inform our parent what type of class we are
    super("sensor_contact");

    addAttributeHandler(new HandleAttribute("Label")
    {
      public void setValue(String name, String value)
      {
        _theContact.setLabel(fromXML(value));
      }
    });
   
    addAttributeHandler(new HandleAttribute("Range")
    {
      public void setValue(String name, String value)
      {
        try
        {
          _theContact.setRange(readThisDouble(value));
        }
        catch (java.text.ParseException pe)
        {
          MWC.Utilities.Errors.Trace.trace(pe, "Failed reading in:" + name + " value is:" + value);
        }

      }
    });
    addAttributeHandler(new HandleAttribute("Bearing")
    {
      public void setValue(String name, String value)
      {
        try
        {
          _theContact.setBearing(readThisDouble(value));
        }
        catch (java.text.ParseException pe)
        {
          MWC.Utilities.Errors.Trace.trace(pe, "Failed reading in:" + name + " value is:" + value);
        }

      }
    });

    addAttributeHandler(new HandleAttribute("Dtg")
    {
      public void setValue(String name, String value)
      {
        _theContact.setDTG(parseThisDate(value));
      }
    });

    addHandler(new LocationHandler("centre")
    {
      public void setLocation(MWC.GenericData.WorldLocation res)
      {
        _theContact.setOrigin(res);
      }
    });

    addHandler(new ColourHandler()
    {
      public void setColour(java.awt.Color theVal)
      {
        _theContact.setColor(theVal);
      }
    });

    addAttributeHandler(new HandleBooleanAttribute("LabelShowing")
    {
      public void setValue(String name, boolean value)
      {
        _theContact.setLabelVisible(value);
      }
    });

    addAttributeHandler(new HandleBooleanAttribute("Visible")
    {
      public void setValue(String name, boolean value)
      {
        _theContact.setVisible(value);
      }
    });


    addAttributeHandler(new HandleAttribute("LabelLocation")
    {
      public void setValue(String name, String val)
      {
        lp.setAsText(val);
        Integer res = (Integer) lp.getValue();
        if (res != null)
          _theContact.setLabelLocation(res);
      }
    });
    addAttributeHandler(new HandleAttribute("PutLabelAt")
    {
      public void setValue(String name, String val)
      {
        ll.setAsText(val);
        Integer res = (Integer) ll.getValue();
        if (res != null)
          _theContact.setPutLabelAt(res);
      }
    });
    addAttributeHandler(new HandleAttribute("LineStyle")
    {
      public void setValue(String name, String val)
      {
        ls.setAsText(val.replace('_', ' '));
        Integer res = (Integer) ls.getValue();
        if (res != null)
          _theContact.setLineStyle(res);
      }
    });

  }

  public final void handleOurselves(String name, Attributes atts)
  {
    // create the new items
    _theContact = new Debrief.Wrappers.SensorContactWrapper();

    lp.setValue(null);
    ls.setValue(null);
    ll.setValue(null);

    super.handleOurselves(name, atts);
  }

  public final void elementClosed()
  {
    addContact(_theContact);

    // reset our variables
    _theContact = null;
  }

  abstract public void addContact(MWC.GUI.Plottable plottable);

  public static void exportFix(Debrief.Wrappers.SensorContactWrapper contact, org.w3c.dom.Element parent,
                               org.w3c.dom.Document doc)
  {
    /*

  Dtg CDATA #REQUIRED
  Track CDATA #REQUIRED
  Bearing CDATA #REQUIRED
  Range CDATA #REQUIRED
  Visible (TRUE|FALSE) "TRUE"
  Label CDATA #REQUIRED
  LabelShowing (TRUE|FALSE) "TRUE"
  LabelLocation (Top|Left|Bottom|Centre|Right) "Left"
  LineStyle (SOLID|DOTTED|DOT_DASH|SHORT_DASHES|LONG_DASHES|UNCONNECTED) "SOLID"

    */
    Element eFix = doc.createElement("sensor_contact");

    eFix.setAttribute("Dtg", writeThis(contact.getDTG()));

    eFix.setAttribute("Visible", writeThis(contact.getVisible()));
    eFix.setAttribute("Bearing", writeThis(contact.getBearing()));
    eFix.setAttribute("Range", writeThis(contact.getRange()));
    eFix.setAttribute("LabelShowing", writeThis(contact.getLabelVisible()));
    eFix.setAttribute("Label", toXML(contact.getLabel()));

    // sort out the line style
    ls.setValue(contact.getLineStyle());
    eFix.setAttribute("LineStyle", ls.getAsText().replace(' ', '_')); // note, we swap spaces for underscores

    // and the line label location
    ll.setValue(contact.getPutLabelAt());
    eFix.setAttribute("PutLabelAt", ll.getAsText());

    // and the label itself
    lp.setValue(contact.getLabelLocation());
    eFix.setAttribute("LabelLocation", lp.getAsText());

    // note, we are accessing the "actual" colour for this fix, we are not using the
    // normal getColor method which may return the track colour
    java.awt.Color fCol = contact.getActualColor();
    if (fCol != null)
      MWC.Utilities.ReaderWriter.XML.Util.ColourHandler.exportColour(fCol, eFix, doc);

    // and now the centre item,
    MWC.GenericData.WorldLocation origin = contact.getOrigin(null);
    if (origin != null)
      MWC.Utilities.ReaderWriter.XML.Util.LocationHandler.exportLocation(origin, "centre", eFix, doc);

    parent.appendChild(eFix);

  }


}
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
package Debrief.ReaderWriter.XML.Tactical;

import java.text.ParseException;

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

import MWC.GUI.Properties.LineLocationPropertyEditor;
import MWC.GUI.Properties.LineStylePropertyEditor;
import MWC.GUI.Properties.LocationPropertyEditor;
import MWC.GenericData.WorldDistance;
import MWC.Utilities.Errors.Trace;
import MWC.Utilities.ReaderWriter.XML.Util.*;

abstract public class SensorContactHandler extends
    MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{

  private static final String SENSOR_CONTACT = "sensor_contact";

  private static final String LINE_STYLE = "LineStyle";

  private static final String PUT_LABEL_AT = "PutLabelAt";

  private static final String LABEL_LOCATION = "LabelLocation";

  private static final String VISIBLE = "Visible";

  private static final String LABEL_SHOWING = "LabelShowing";

  private static final String LABEL = "Label";

  private static final String BEARING = "Bearing";

  private static final String CENTRE = "centre";

  private static final String DTG = "Dtg";

  private static final String AMBIGUOUS_BEARING = "AmbiguousBearing";
  private static final String HAS_AMBIGUOUS_BEARING = "HasAmbiguousBearing";

  private static final String FREQUENCY = "Frequency";
  private static final String HAS_FREQUENCY = "HasFrequency";

  private static final String RANGE = "Range";

  private Debrief.Wrappers.SensorContactWrapper _theContact;

  /**
   * class which contains list of textual representations of label locations
   */
  private static final LocationPropertyEditor lp = new LocationPropertyEditor();

  /**
   * class which contains list of textual representations of label locations
   */
  private static final LineLocationPropertyEditor ll =
      new LineLocationPropertyEditor();

  /**
   * class which contains list of textual representations of label locations
   */
  private static final LineStylePropertyEditor ls =
      new LineStylePropertyEditor();

  private WorldDistance _myRange = null;

  /**
   * default constructor - using fixed name
   * 
   */
  public SensorContactHandler()
  {
    // inform our parent what type of class we are
    this(SENSOR_CONTACT);
  }

  /**
   * versatile constructor that allows any item name to be specified
   * 
   * @param typeName
   *          the element name that we're looking for
   */
  public SensorContactHandler(final String typeName)
  {
    // inform our parent what type of class we are
    super(typeName);

    addAttributeHandler(new HandleAttribute(LABEL)
    {
      public void setValue(final String name, final String value)
      {
        _theContact.setLabel(fromXML(value));
      }
    });
    addAttributeHandler(new HandleAttribute("Comment")
    {
      public void setValue(final String name, final String value)
      {
        _theContact.setComment(fromXML(value));
      }
    });

    addAttributeHandler(new HandleDoubleAttribute(RANGE)
    {
      public void setValue(final String name, final double value)
      {
        _theContact.setRange(new WorldDistance(value, WorldDistance.YARDS));
      }
    });

    addAttributeHandler(new HandleDoubleAttribute(BEARING)
    {
      public void setValue(final String name, final double value)
      {
        _theContact.setBearing(value);
      }
    });

    addAttributeHandler(new HandleDoubleAttribute(AMBIGUOUS_BEARING)
    {
      public void setValue(final String name, final double value)
      {
        _theContact.setAmbiguousBearing(value);
      }
    });
    addAttributeHandler(new HandleBooleanAttribute(HAS_AMBIGUOUS_BEARING)
    {
      public void setValue(final String name, final boolean value)
      {
        _theContact.setHasAmbiguousBearing(value);
      }
    });

    addAttributeHandler(new HandleDoubleAttribute(FREQUENCY)
    {
      public void setValue(final String name, final double value)
      {
        _theContact.setFrequency(value);
      }
    });
    addAttributeHandler(new HandleBooleanAttribute(HAS_FREQUENCY)
    {
      public void setValue(final String name, final boolean value)
      {
        _theContact.setHasFrequency(value);
      }
    });

    addAttributeHandler(new HandleAttribute(DTG)
    {
      public void setValue(final String name, final String value)
      {
        try
        {
          _theContact.setDTG(parseThisDate(value));
        }
        catch (ParseException e)
        {
          Trace.trace(e, "While parsing date");
        }
      }
    });

    addHandler(new LocationHandler(CENTRE)
    {
      public void setLocation(final MWC.GenericData.WorldLocation res)
      {
        _theContact.setOrigin(res);
      }
    });

    addHandler(new ColourHandler()
    {
      public void setColour(final java.awt.Color theVal)
      {
        _theContact.setColor(theVal);
      }
    });

    addAttributeHandler(new HandleBooleanAttribute(LABEL_SHOWING)
    {
      public void setValue(final String name, final boolean value)
      {
        _theContact.setLabelVisible(value);
      }
    });

    addAttributeHandler(new HandleBooleanAttribute(VISIBLE)
    {
      public void setValue(final String name, final boolean value)
      {
        _theContact.setVisible(value);
      }
    });

    addAttributeHandler(new HandleAttribute(LABEL_LOCATION)
    {
      public void setValue(final String name, final String val)
      {
        lp.setAsText(val);
        final Integer res = (Integer) lp.getValue();
        if (res != null)
          _theContact.setLabelLocation(res);
      }
    });
    addAttributeHandler(new HandleAttribute(PUT_LABEL_AT)
    {
      public void setValue(final String name, final String val)
      {
        ll.setAsText(val);
        final Integer res = (Integer) ll.getValue();
        if (res != null)
          _theContact.setPutLabelAt(res);
      }
    });
    addAttributeHandler(new HandleAttribute(LINE_STYLE)
    {
      public void setValue(final String name, final String val)
      {
        ls.setAsText(val.replace('_', ' '));
        final Integer res = (Integer) ls.getValue();
        if (res != null)
          _theContact.setLineStyle(res);
      }
    });

    addHandler(new WorldDistanceHandler(RANGE)
    {
      public void setWorldDistance(final WorldDistance value)
      {
        _myRange = value;
      }
    });
  }

  public final void handleOurselves(final String name, final Attributes atts)
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
    // do we have a range?
    if (_myRange != null)
    {
      _theContact.setRange(_myRange);

      // and clear it
      _myRange = null;
    }

    // and store it
    addContact(_theContact);

    // reset our variables
    _theContact = null;
    _myRange = null;
  }

  abstract public void addContact(MWC.GUI.Plottable plottable);

  /**
   * export this item using the default xml element name
   * 
   * @param contact
   *          the item to export
   * @param parent
   *          the xml parent element
   * @param doc
   *          the document we're being written to
   */
  public static void exportFix(
      final Debrief.Wrappers.SensorContactWrapper contact,
      final org.w3c.dom.Element parent, final org.w3c.dom.Document doc)
  {
    exportFix(SENSOR_CONTACT, contact, parent, doc);
  }

  /**
   * export this item using the specified element name
   * 
   * @param typeName
   *          the xml element name to use
   * @param contact
   *          the item to export
   * @param parent
   *          the xml parent element
   * @param doc
   *          the document we're being written to
   */
  public static void exportFix(final String typeName,
      final Debrief.Wrappers.SensorContactWrapper contact,
      final org.w3c.dom.Element parent, final org.w3c.dom.Document doc)
  {
    /*
     * 
     * Dtg CDATA #REQUIRED Track CDATA #REQUIRED Bearing CDATA #REQUIRED Range CDATA #REQUIRED
     * Visible (TRUE|FALSE) "TRUE" Label CDATA #REQUIRED LabelShowing (TRUE|FALSE) "TRUE"
     * LabelLocation (Top|Left|Bottom|Centre|Right) "Left" LineStyle
     * (SOLID|DOTTED|DOT_DASH|SHORT_DASHES|LONG_DASHES|UNCONNECTED) "SOLID"
     */
    final Element eFix = doc.createElement(typeName);

    eFix.setAttribute(DTG, writeThis(contact.getDTG()));

    eFix.setAttribute(VISIBLE, writeThis(contact.getVisible()));

    if (contact.getHasBearing())
      eFix.setAttribute(BEARING, writeThis(contact.getBearing()));

    eFix.setAttribute(LABEL_SHOWING, writeThis(contact.getLabelVisible()));
    eFix.setAttribute(LABEL, toXML(contact.getLabel()));
    if (contact.getComment() != null)
    {
      eFix.setAttribute("Comment", toXML(contact.getComment()));
    }

    // we no longer export range as an attribute, but as an element-so we can
    // store units
    // eFix.setAttribute("Range", writeThis(contact.getRange().getValueIn(
    // WorldDistance.YARDS)));

    // do we have ambiguous data?
    final double ambig = contact.getAmbiguousBearing();
    if (!Double.isNaN(ambig))
    {
      eFix.setAttribute(AMBIGUOUS_BEARING, writeThis(contact
          .getAmbiguousBearing()));
    }
    eFix.setAttribute(HAS_AMBIGUOUS_BEARING, writeThis(contact
        .getHasAmbiguousBearing()));

    // do we have frequency data?
    eFix.setAttribute(FREQUENCY, writeThis(contact.getFrequency()));
    eFix.setAttribute(HAS_FREQUENCY, writeThis(contact.getHasFrequency()));

    // sort out the range
    // do we have range?
    if (contact.getRange() != null)
      WorldDistanceHandler.exportDistance(RANGE, contact.getRange(), eFix, doc);

    // sort out the line style
    ls.setValue(contact.getLineStyle());
    eFix.setAttribute(LINE_STYLE, ls.getAsText().replace(' ', '_')); // note,
    // we
    // swap
    // spaces
    // for
    // underscores

    // and the line label location
    ll.setValue(contact.getPutLabelAt());
    eFix.setAttribute(PUT_LABEL_AT, ll.getAsText());

    // and the label itself
    lp.setValue(contact.getLabelLocation());
    eFix.setAttribute(LABEL_LOCATION, lp.getAsText());

    // note, we are accessing the "actual" colour for this fix, we are not
    // using the
    // normal getColor method which may return the track colour
    final java.awt.Color fCol = contact.getActualColor();
    if (fCol != null)
      MWC.Utilities.ReaderWriter.XML.Util.ColourHandler.exportColour(fCol, eFix,
          doc);

    // and now the centre item,
    final MWC.GenericData.WorldLocation origin = contact.getOrigin();
    if (origin != null)
      MWC.Utilities.ReaderWriter.XML.Util.LocationHandler.exportLocation(origin,
          CENTRE, eFix, doc);

    parent.appendChild(eFix);

  }

}
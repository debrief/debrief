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

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

import Debrief.Wrappers.FixWrapper;
import MWC.GUI.Plottable;
import MWC.GUI.Properties.LocationPropertyEditor;
import MWC.GenericData.HiResDate;
import MWC.TacticalData.Fix;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import MWC.Utilities.ReaderWriter.XML.Util.ColourHandler;
import MWC.Utilities.ReaderWriter.XML.Util.FontHandler;
import MWC.Utilities.ReaderWriter.XML.Util.LocationHandler;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;

abstract public class FixHandler extends MWCXMLReader
{

  public static void exportFix(final FixWrapper fix, final Element parent,
      final Document doc)
  {
    /*
     * <!ELEMENT fix (colour?, centre)> <!ATTLIST fix course CDATA #REQUIRED speed CDATA #REQUIRED
     * dtg CDATA #REQUIRED visible (TRUE|FALSE) "TRUE" label CDATA #IMPLIED LabelShowing
     * (TRUE|FALSE) #IMPLIED SymbolShowing (TRUE|FALSE) "TRUE" LabelLocation
     * (Top|Left|Bottom|Centre|Right) "Left"
     */
    final Element eFix = doc.createElement("fix");
    eFix.setAttribute("Course", ""
        + writeThis(MWC.Algorithms.Conversions.Rads2Degs(fix.getCourse())));
    eFix.setAttribute("Speed", "" + writeThis(fix.getSpeed()));
    eFix.setAttribute("Dtg", writeThis(fix.getTime()));
    eFix.setAttribute("Visible", writeThis(fix.getVisible()));
    eFix.setAttribute("Label", toXML(fix.getLabel()));
    if (fix.getComment() != null)
    {
      eFix.setAttribute("Comment", toXML(fix.getComment()));
      eFix.setAttribute("DisplayComment", writeThis(fix.getDisplayComment()));
    }
    eFix.setAttribute("LabelShowing", writeThis(fix.getLabelShowing()));
    eFix.setAttribute("LineShowing", writeThis(fix.getLineShowing()));
    eFix.setAttribute("SymbolShowing", writeThis(fix.getSymbolShowing()));
    eFix.setAttribute("ArrowShowing", writeThis(fix.getArrowShowing()));
    lp.setValue(fix.getLabelLocation());
    eFix.setAttribute("LabelLocation", lp.getAsText());

    // note, we are accessing the "actual" colour for this fix, we are not using the
    // normal getColor method which may return the track colour
    final java.awt.Color fCol = fix.getActualColor();
    if (fCol != null)
    {
      // just see if the colour is different to the parent
      final java.awt.Color parentColor = fix.getTrackWrapper().getColor();
      if (fCol.equals(parentColor))
      {
        // hey, don't bother outputting the parent color
      }
      else
      {
        ColourHandler.exportColour(fCol, eFix, doc);
      }

    }

    // and the font
    final java.awt.Font theFont = fix.getFont();
    if (theFont != null)
    {
      // ok, compare the font to the parent
      if (!theFont.equals(FixWrapper.PLAIN_FONT))
      {
        // ok, it's non-standard export it.
        FontHandler.exportFont(theFont, eFix, doc);
      }
    }

    // and now the centre item,
    LocationHandler.exportLocation(fix.getLocation(), "centre", eFix, doc);

    parent.appendChild(eFix);

  }

  private Fix _theFix;

  private FixWrapper _theFixWrapper;

  /**
   * class which contains list of textual representations of label locations
   */
  private static final LocationPropertyEditor lp = new LocationPropertyEditor();

  public FixHandler()
  {
    // inform our parent what type of class we are
    super("fix");

    addAttributeHandler(new HandleAttribute("Label")
    {
      @Override
      public void setValue(final String name, final String value)
      {
        _theFixWrapper.setLabel(fromXML(value));
      }
    });
    addAttributeHandler(new HandleAttribute("Comment")
    {
      @Override
      public void setValue(final String name, final String value)
      {
        _theFixWrapper.setComment(fromXML(value));
      }
    });
    addAttributeHandler(new HandleBooleanAttribute("DisplayComment")
    {
      @Override
      public void setValue(final String name, final boolean value)
      {
        _theFixWrapper.setDisplayComment(value);
      }
    });
    addHandler(new FontHandler()
    {
      @Override
      public void setFont(final java.awt.Font font)
      {
        _theFixWrapper.setFont(font);
      }
    });
    addAttributeHandler(new HandleAttribute("Course")
    {
      @Override
      public void setValue(final String name, final String value)
      {
        try
        {
          double courseVal = readThisDouble(value);
          if (courseVal < 0)
          {
            // trim it back to positive domain
            courseVal += 360;
          }
          _theFix.setCourse(MWC.Algorithms.Conversions.Degs2Rads(courseVal));
        }
        catch (final java.text.ParseException pe)
        {
          MWC.Utilities.Errors.Trace.trace(pe, "Failed reading in:" + name
              + " value is:" + value);
        }
      }
    });

    addAttributeHandler(new HandleAttribute("Speed")
    {
      @Override
      public void setValue(final String name, final String value)
      {
        try
        {
          _theFix.setSpeed(MWC.Algorithms.Conversions
              .Kts2Yps(readThisDouble(value)));
        }
        catch (final java.text.ParseException pe)
        {
          MWC.Utilities.Errors.Trace.trace(pe, "Failed reading in:" + name
              + " value is:" + value);
        }

      }
    });

    addAttributeHandler(new HandleAttribute("Dtg")
    {
      @Override
      public void setValue(final String name, final String value)
      {
        final HiResDate hrf = DebriefFormatDateTime.parseThis(value);
        _theFix.setTime(hrf);
      }
    });

    addHandler(new LocationHandler("centre")
    {
      @Override
      public void setLocation(final MWC.GenericData.WorldLocation res)
      {
        _theFixWrapper.setFixLocation(res);
      }
    });

    addHandler(new ColourHandler()
    {
      @Override
      public void setColour(final java.awt.Color theVal)
      {

        _theFixWrapper.setColor(theVal);
      }
    });

    addAttributeHandler(new HandleBooleanAttribute("LabelShowing")
    {
      @Override
      public void setValue(final String name, final boolean value)
      {
        _theFixWrapper.setLabelShowing(value);
      }
    });

    addAttributeHandler(new HandleBooleanAttribute("SymbolShowing")
    {
      @Override
      public void setValue(final String name, final boolean value)
      {
        _theFixWrapper.setSymbolShowing(value);
      }
    });

    addAttributeHandler(new HandleBooleanAttribute("ArrowShowing")
    {
      @Override
      public void setValue(final String name, final boolean value)
      {
        _theFixWrapper.setArrowShowing(value);
      }
    });

    addAttributeHandler(new HandleBooleanAttribute("LineShowing")
    {
      @Override
      public void setValue(final String name, final boolean value)
      {
        _theFixWrapper.setLineShowing(value);
      }
    });

    addAttributeHandler(new HandleBooleanAttribute("Visible")
    {
      @Override
      public void setValue(final String name, final boolean value)
      {
        _theFixWrapper.setVisible(value);
      }
    });
    addAttributeHandler(new HandleAttribute("LabelLocation")
    {
      @Override
      public void setValue(final String name, final String val)
      {
        lp.setAsText(val);
        final Integer res = (Integer) lp.getValue();
        if (res != null)
        {
          _theFixWrapper.setLabelLocation(res);
        }
      }
    });
  }

  abstract public void addPlottable(Plottable plottable);

  @Override
  public final void elementClosed()
  {
    addPlottable(_theFixWrapper);

    // reset our variables
    _theFix = null;
    _theFixWrapper = null;
  }

  @Override
  public final void handleOurselves(final String name, final Attributes atts)
  {
    // create the new items
    _theFix = new Fix();
    _theFixWrapper = new FixWrapper(_theFix);
    lp.setValue(null);

    super.handleOurselves(name, atts);
  }

}
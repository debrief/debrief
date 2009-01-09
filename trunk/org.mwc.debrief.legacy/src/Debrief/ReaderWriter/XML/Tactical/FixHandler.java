package Debrief.ReaderWriter.XML.Tactical;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import MWC.GenericData.HiResDate;
import MWC.Utilities.ReaderWriter.XML.Util.ColourHandler;
import MWC.Utilities.ReaderWriter.XML.Util.FontHandler;
import MWC.Utilities.ReaderWriter.XML.Util.LocationHandler;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;


abstract public class FixHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{

  MWC.TacticalData.Fix _theFix;
  Debrief.Wrappers.FixWrapper _theFixWrapper;

  /**
   * class which contains list of textual representations of label locations
   */
  static final MWC.GUI.Properties.LocationPropertyEditor lp
    = new MWC.GUI.Properties.LocationPropertyEditor();

  public FixHandler()
  {
    // inform our parent what type of class we are
    super("fix");

    addAttributeHandler(new HandleAttribute("Label")
    {
      public void setValue(String name, String value)
      {
        _theFixWrapper.setLabel(fromXML(value));
      }
    });
    addHandler(new FontHandler()
    {
      public void setFont(java.awt.Font font)
      {
        _theFixWrapper.setFont(font);
      }
    });
    addAttributeHandler(new HandleAttribute("Course")
    {
      public void setValue(String name, String value)
      {
        try
        {
          _theFix.setCourse(MWC.Algorithms.Conversions.Degs2Rads(readThisDouble(value)));
        }
        catch (java.text.ParseException pe)
        {
          MWC.Utilities.Errors.Trace.trace(pe, "Failed reading in:" + name + " value is:" + value);
        }
      }
    });


    addAttributeHandler(new HandleAttribute("Speed")
    {
      public void setValue(String name, String value)
      {
        try
        {
          _theFix.setSpeed(MWC.Algorithms.Conversions.Kts2Yps(readThisDouble(value)));
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
        HiResDate hrf = DebriefFormatDateTime.parseThis(value);
        _theFix.setTime(hrf);
      }
    });

    addHandler(new LocationHandler("centre")
    {
      public void setLocation(MWC.GenericData.WorldLocation res)
      {
        _theFixWrapper.setFixLocation(res);
      }
    });

    addHandler(new ColourHandler()
    {
      public void setColour(java.awt.Color theVal)
      {

        _theFixWrapper.setColor(theVal);
      }
    });

    addAttributeHandler(new HandleBooleanAttribute("LabelShowing")
    {
      public void setValue(String name, boolean value)
      {
        _theFixWrapper.setLabelShowing(value);
      }
    });

    addAttributeHandler(new HandleBooleanAttribute("SymbolShowing")
    {
      public void setValue(String name, boolean value)
      {
        _theFixWrapper.setSymbolShowing(value);
      }
    });

    addAttributeHandler(new HandleBooleanAttribute("LineShowing")
    {
      public void setValue(String name, boolean value)
      {
        _theFixWrapper.setLineShowing(value);
      }
    });

    addAttributeHandler(new HandleBooleanAttribute("Visible")
    {
      public void setValue(String name, boolean value)
      {
        _theFixWrapper.setVisible(value);
      }
    });


    addAttributeHandler(new HandleAttribute("LabelLocation")
    {
      public void setValue(String name, String val)
      {
        lp.setAsText(val);
        Integer res = (Integer) lp.getValue();
        if (res != null)
          _theFixWrapper.setLabelLocation(res);
      }
    });

  }

  public final void handleOurselves(String name, Attributes atts)
  {
    // create the new items
    _theFix = new MWC.TacticalData.Fix();
    _theFixWrapper = new Debrief.Wrappers.FixWrapper(_theFix);
    lp.setValue(null);

    super.handleOurselves(name, atts);
  }

  public final void elementClosed()
  {
    addPlottable(_theFixWrapper);

    // reset our variables
    _theFix = null;
    _theFixWrapper = null;
  }

  abstract public void addPlottable(MWC.GUI.Plottable plottable);

  public static void exportFix(Debrief.Wrappers.FixWrapper fix, org.w3c.dom.Element parent, org.w3c.dom.Document doc)
  {
    /*
<!ELEMENT fix (colour?, centre)>
<!ATTLIST fix
  course CDATA #REQUIRED
  speed CDATA #REQUIRED
  dtg CDATA #REQUIRED
  visible (TRUE|FALSE) "TRUE"
  label CDATA #IMPLIED
  LabelShowing (TRUE|FALSE) #IMPLIED
  SymbolShowing (TRUE|FALSE) "TRUE"
  LabelLocation (Top|Left|Bottom|Centre|Right) "Left"
    */
    Element eFix = doc.createElement("fix");
    eFix.setAttribute("Course", "" + writeThis(MWC.Algorithms.Conversions.Rads2Degs(fix.getCourse())));
    eFix.setAttribute("Speed", "" + writeThis(fix.getSpeed()));
    eFix.setAttribute("Dtg", writeThis(fix.getTime()));
    eFix.setAttribute("Visible", writeThis(fix.getVisible()));
    eFix.setAttribute("Label", toXML(fix.getLabel()));
    eFix.setAttribute("LabelShowing", writeThis(fix.getLabelShowing()));
    eFix.setAttribute("LineShowing", writeThis(fix.getLineShowing()));
    eFix.setAttribute("SymbolShowing", writeThis(fix.getSymbolShowing()));
    lp.setValue(fix.getLabelLocation());
    eFix.setAttribute("LabelLocation", lp.getAsText());

    // note, we are accessing the "actual" colour for this fix, we are not using the
    // normal getColor method which may return the track colour
    java.awt.Color fCol = fix.getActualColor();
    if (fCol != null)
    {
      // just see if the colour is different to the parent
      java.awt.Color parentColor = fix.getTrackWrapper().getColor();
      if (fCol.equals(parentColor))
      {
        // hey, don't bother outputting the parent color
      }
      else
      {
        MWC.Utilities.ReaderWriter.XML.Util.ColourHandler.exportColour(fCol, eFix, doc);
      }

    }

    // and the font
    java.awt.Font theFont = fix.getFont();
    if (theFont != null)
    {
      // ok, compare the font to the parent
      if(theFont.equals(fix.getTrackWrapper().getTrackFont()))
      {
        // don't bother outputting the font - it's the same as the parent anyway
      }
      else
      {
        FontHandler.exportFont(theFont, eFix, doc);
      }
    }

    // and now the centre item,
    MWC.Utilities.ReaderWriter.XML.Util.LocationHandler.exportLocation(fix.getLocation(), "centre", eFix, doc);

    parent.appendChild(eFix);

  }


}
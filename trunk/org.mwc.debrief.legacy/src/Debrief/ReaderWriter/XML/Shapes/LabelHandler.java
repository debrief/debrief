package Debrief.ReaderWriter.XML.Shapes;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import org.w3c.dom.Element;

import MWC.GenericData.HiResDate;
import MWC.Utilities.ReaderWriter.XML.PlottableExporter;
import MWC.Utilities.ReaderWriter.XML.Util.*;


abstract public class LabelHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader implements PlottableExporter
{

  java.awt.Color _theColor;
  boolean _isVisible;
  MWC.GenericData.WorldLocation _centre;
  String _labelLocation;
  HiResDate _startDTG = null;
  HiResDate _endDTG = null;
  java.awt.Font _font = null;
  String _symbol;
  String _label;
  boolean _symbolVisible;
  boolean _labelVisible;
  String _scale;

  /** class which contains list of textual representations of label locations
   */
  static private final MWC.GUI.Properties.LocationPropertyEditor lp
    = new MWC.GUI.Properties.LocationPropertyEditor();


  static private final MWC.GUI.Shapes.Symbols.SymbolScalePropertyEditor ss_editor
    = new MWC.GUI.Shapes.Symbols.SymbolScalePropertyEditor();

  public LabelHandler()
  {
    // inform our parent what type of class we are
    super("textlabel");

    addAttributeHandler(new HandleBooleanAttribute("Visible")
    {
      public void setValue(String name, boolean value)
      {
        _isVisible = value;
      }
    });
    addHandler(new ColourHandler()
    {
      public void setColour(java.awt.Color color)
      {
        _theColor = color;
      }
    });

    addHandler(new FontHandler()
    {
      public void setFont(java.awt.Font font)
      {
        _font = font;
      }
    });
    addHandler(new LocationHandler("centre")
    {
      public void setLocation(MWC.GenericData.WorldLocation res)
      {
        _centre = res;
      }
    });
    addAttributeHandler(new HandleAttribute("LabelLocation")
    {
      public void setValue(String name, String value)
      {
        _labelLocation = value;
      }
    });
    addAttributeHandler(new HandleAttribute("Symbol")
    {
      public void setValue(String name, String value)
      {
        _symbol = value;
      }
    });
    addAttributeHandler(new HandleAttribute("Label")
    {
      public void setValue(String name, String value)
      {
        _label = fromXML(value);
      }
    });
    addAttributeHandler(new HandleAttribute("Scale")
    {
      public void setValue(String name, String value)
      {
        _scale = value;
      }
    });
    addHandler(new TimeRangeHandler()
    {
      public void setTimeRange(HiResDate start, HiResDate end)
      {
        _startDTG = start;
        _endDTG = end;
      }
    });
    addAttributeHandler(new HandleBooleanAttribute("SymbolVisible")
    {
      public void setValue(String name, boolean value)
      {
        _symbolVisible = value;
      }
    });
    addAttributeHandler(new HandleBooleanAttribute("LabelVisible")
    {
      public void setValue(String name, boolean value)
      {
        _labelVisible = value;
      }
    });
  }


 public final void elementClosed()
  {

    // create a Label from this data
    Debrief.Wrappers.LabelWrapper lw = new Debrief.Wrappers.LabelWrapper(_label, _centre, _theColor);
    lw.setTimeStart(_startDTG);
    lw.setTime_End(_endDTG);
    lw.setVisible(_isVisible);
    lw.setSymbolVisible(_symbolVisible);
    lw.setLabelVisible(_labelVisible);
    // do we know the location?
    if(_labelLocation != null)
    {
      lp.setAsText(_labelLocation);
      lw.setLabelLocation((Integer) lp.getValue());
    }
    if(_symbol != null)
    {
      lw.setSymbolType(_symbol);
    }
    if(_font != null)
    {
      lw.setFont(_font);
    }

    // now the scale
    if(_scale != null)
    {
      ss_editor.setAsText(_scale);
      Double val = (Double) ss_editor.getValue();
      lw.setSymbolSize(val);
    }

    addPlottable(lw);

    // reset our variables
    _theColor = null;
    _isVisible = false;
    _labelLocation = null;
    _symbol = null;
    _startDTG = null;
    _endDTG = null;
  }

  abstract public void addPlottable(MWC.GUI.Plottable plottable);


  public final void exportThisPlottable(MWC.GUI.Plottable plottable, org.w3c.dom.Element parent, org.w3c.dom.Document doc)
  {


    Debrief.Wrappers.LabelWrapper lw = (Debrief.Wrappers.LabelWrapper) plottable;
    Element label = doc.createElement("textlabel");

    // do the visibility
    label.setAttribute("Visible", writeThis(lw.getVisible()));
    label.setAttribute("Label", toXML(lw.getLabel()));
    label.setAttribute("SymbolVisible", writeThis(lw.getSymbolVisible()));
    label.setAttribute("LabelVisible", writeThis(lw.getLabelVisible()));
    label.setAttribute("Symbol", lw.getSymbolType());

    ss_editor.setValue(lw.getSymbolSize());
    label.setAttribute("Scale", ss_editor.getAsText());

    lp.setValue(lw.getLabelLocation());
    String loc = lp.getAsText();
    label.setAttribute("LabelLocation", loc);

    // do the colour
    ColourHandler.exportColour(lw.getColor(), label, doc);

    // sort out the time range, if it looks like we have any time data
    if(lw.getStartDTG() != null)
    {
      TimeRangeHandler.exportThis(lw.getStartDTG(), lw.getEndDTG(), label, doc);
    }

    // and the font
    java.awt.Font theFont = lw.getFont();
    if(lw != null)
    {
      FontHandler.exportFont(theFont, label, doc);
    }

    // and now the centre point
    LocationHandler.exportLocation(lw.getLocation(), "centre", label, doc);

    // finished
    parent.appendChild(label);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////
   // testing for this class
   //////////////////////////////////////////////////////////////////////////////////////////////////
   static public final class testMe extends junit.framework.TestCase
   {
     static public final String TEST_ALL_TEST_TYPE  = "UNIT";
     public testMe(final String val)
     {
       super(val);
     }
     public final void testConversion()
     {
       final String start = "here\\nand there\\nalpha";

        String other = LabelHandler.fromXML(start);
       assertEquals("strings match on way to XML","here\nand there\nalpha" ,other);

        String andBack = LabelHandler.toXML(other);

       assertEquals("strings match on way back to text",start , andBack);

        if(!andBack.equals(start))
        {
          System.out.println("second one failed");
        }

       // check they handle null strings
       other = LabelHandler.toXML(null);
       assertEquals("handled null string going to XML", "", other);

       other = LabelHandler.fromXML(null);
       assertEquals("handled null string going from XML", "", other);

       // check they handle short strings
       other = LabelHandler.toXML("");
       assertEquals("handled empty string going to XML", "", other);

       other = LabelHandler.fromXML("");
       assertEquals("handled empty string going from XML", "", other);
     }
   }



  public static void main(String[] args)
  {
    testMe tm = new testMe("here");
    tm.testConversion();

  }

}
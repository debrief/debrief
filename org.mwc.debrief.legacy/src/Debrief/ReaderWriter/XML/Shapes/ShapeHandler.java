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
package Debrief.ReaderWriter.XML.Shapes;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import Debrief.Wrappers.ShapeWrapper;
import MWC.GUI.CanvasType;
import MWC.GenericData.HiResDate;
import MWC.Utilities.ReaderWriter.XML.Util.ColourHandler;
import MWC.Utilities.ReaderWriter.XML.Util.FontHandler;
import MWC.Utilities.ReaderWriter.XML.Util.TimeRangeHandler;


abstract public class ShapeHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{

  java.awt.Color _col = null;
  HiResDate _startDTG = null;
  HiResDate _endDTG = null;
  String _myType = null;
  String _label = null;
  java.awt.Font _font = null;
  Integer _theLocation = null;
  Integer _lineStyle = null;
  Integer _lineThickness = null;
  java.awt.Color _fontCol = null;
  boolean _isVisible = false;
  boolean _labelVisible = true;

  /**
   * class which contains list of textual representations of label locations
   */
  static final MWC.GUI.Properties.LocationPropertyEditor lp
    = new MWC.GUI.Properties.LocationPropertyEditor();

  /** and define the strings used to describe the shape
   *
   */
  private static final String LABEL_VISIBLE = "LabelVisible";
  private static final String SHAPE_VISIBLE = "Visible";
  private static final String LABEL_LOCATION = "LabelLocation";
  private static final String LABEL_TEXT = "Label";
  private static final String FONT_COLOUR = "fontcolour";
  private static final String LINE_STYLE = "LineStyle";
  private static final String LINE_THICKNESS = "LineThickness";

  public ShapeHandler(final String type)
  {
    // inform our parent what type of class we are
    super(type);
    _myType = type;

    addHandler(new ColourHandler(FONT_COLOUR)
    {
      public void setColour(final java.awt.Color res)
      {
        _fontCol = res;
      }
    });

    addHandler(new ColourHandler()
    {
      public void setColour(final java.awt.Color res)
      {
        _col = res;
      }
    });

    addHandler(new TimeRangeHandler()
    {
      public void setTimeRange(final HiResDate start, final HiResDate end)
      {
        _startDTG = start;
        _endDTG = end;
      }
    });

    addHandler(new FontHandler()
    {
      public void setFont(final java.awt.Font font)
      {
        _font = font;
      }
    });


    addAttributeHandler(new HandleAttribute(LABEL_TEXT)
    {
      public void setValue(final String name, final String value)
      {
        _label = fromXML(value);
      }
    });


    addAttributeHandler(new HandleAttribute(LABEL_LOCATION)
    {
      public void setValue(final String name, final String val)
      {
        lp.setAsText(val);
        _theLocation = (Integer) lp.getValue();
      }
    });

    addAttributeHandler(new HandleIntegerAttribute(LINE_STYLE)
    {
      public void setValue(final String name, final int value)
      {
        _lineStyle = new Integer(value);
      }
    });

    addAttributeHandler(new HandleIntegerAttribute(LINE_THICKNESS)
    {
      public void setValue(final String name, final int value)
      {
        _lineThickness = new Integer(value);
      }
    });


    addAttributeHandler(new HandleBooleanAttribute(SHAPE_VISIBLE)
    {
      public void setValue(final String name, final boolean value)
      {
        _isVisible = value;
      }
    });

    addAttributeHandler(new HandleBooleanAttribute(LABEL_VISIBLE)
    {
      public void setValue(final String name, final boolean value)
      {
        _labelVisible = value;
      }
    });


  }
  
  
  protected ShapeWrapper getWrapper()
  {
    final MWC.GUI.Shapes.PlainShape shape = getShape();
    shape.setColor(_col);
    final Debrief.Wrappers.ShapeWrapper sw = new Debrief.Wrappers.ShapeWrapper(_myType, shape, _col, null);
    return sw; 	
  }

  public void elementClosed()
  {

  	final ShapeWrapper sw = getWrapper();
  	
    if (_label != null)
    {
      sw.setLabel(_label);
    }
    if (_font != null)
    {
      sw.setFont(_font);
    }
    if (_startDTG != null)
    {
      sw.setTime_Start(_startDTG);
    }
    if ((_endDTG !=null) && (! _endDTG.equals(_startDTG)))
    {
      sw.setTimeEnd(_endDTG);
    }
    if (_theLocation != null)
    {
      sw.setLabelLocation(_theLocation);
    }
    if (_fontCol != null)
    {
      sw.setLabelColor(_fontCol);
    }
    // line style?
    if(_lineStyle != null)
    {
    	sw.getShape().setLineStyle(_lineStyle.intValue());
    }
    // line width?
    if(_lineThickness != null)
    {
    	sw.getShape().setLineWidth(_lineThickness.intValue());
    }
    sw.setVisible(_isVisible);
    sw.setLabelVisible(_labelVisible);
    

    addPlottable(sw);

    // reset the local parameters
    _startDTG = _endDTG = null;
    _col = null;
    _fontCol = null;
    _label = null;
    _font = null;
    _theLocation = null;
    _isVisible = true;
    _lineStyle = null;
    _lineThickness = null;
  }

  protected abstract MWC.GUI.Shapes.PlainShape getShape();

  abstract public void addPlottable(MWC.GUI.Plottable plottable);

  void exportThisPlottable(final MWC.GUI.Plottable plottable,
                           final org.w3c.dom.Element theShape,
                           final org.w3c.dom.Document doc)
  {
    final Debrief.Wrappers.ShapeWrapper sw = (Debrief.Wrappers.ShapeWrapper) plottable;

    // put the parameters into the parent
    theShape.setAttribute(LABEL_TEXT, toXML(sw.getLabel()));
    lp.setValue(sw.getLabelLocation());
    theShape.setAttribute(LABEL_LOCATION, lp.getAsText());
    theShape.setAttribute(SHAPE_VISIBLE, writeThis(sw.getVisible()));
    theShape.setAttribute(LABEL_VISIBLE, writeThis(sw.getLabelVisible()));

    // output the colour for the shape
    MWC.Utilities.ReaderWriter.XML.Util.ColourHandler.exportColour(sw.getShape().getColor(), theShape, doc);

    // output the colour for the label on the
    MWC.Utilities.ReaderWriter.XML.Util.ColourHandler.exportColour(sw.getLabelColor(), theShape, doc, FONT_COLOUR);

    // sort out the time range
    TimeRangeHandler.exportThis(sw.getStartDTG(), sw.getEndDTG(), theShape, doc);
    
    // does it have an unusual line style?
    if(sw.getShape().getLineStyle() != CanvasType.SOLID)
    {
      theShape.setAttribute(LINE_STYLE, writeThis(sw.getShape().getLineStyle()));
    }
    
    // and output the line thickness
    theShape.setAttribute(LINE_THICKNESS, writeThis(sw.getShape().getLineWidth()));

    // and the font
    final java.awt.Font theFont = sw.getFont();
    if (sw != null)
    {
      FontHandler.exportFont(theFont, theShape, doc);
    }

  }


}
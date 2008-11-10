package Debrief.ReaderWriter.XML.Shapes;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import MWC.GUI.CanvasType;
import MWC.GenericData.HiResDate;
import MWC.Utilities.ReaderWriter.XML.Util.ColourHandler;
import MWC.Utilities.ReaderWriter.XML.Util.FontHandler;
import MWC.Utilities.ReaderWriter.XML.Util.TimeRangeHandler;


abstract public class ShapeHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{

  private java.awt.Color _col = null;
  private HiResDate _startDTG = null;
  private HiResDate _endDTG = null;
  String _myType = null;
  private String _label = null;
  private java.awt.Font _font = null;
  private Integer _theLocation = null;;
  private Integer _lineStyle = null;
  private Integer _lineThickness = null;
  private java.awt.Color _fontCol = null;
  private boolean _isVisible = false;
  private boolean _labelVisible = true;

  /**
   * class which contains list of textual representations of label locations
   */
  static private final MWC.GUI.Properties.LocationPropertyEditor lp
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

  public ShapeHandler(String type)
  {
    // inform our parent what type of class we are
    super(type);
    _myType = type;

    addHandler(new ColourHandler(FONT_COLOUR)
    {
      public void setColour(java.awt.Color res)
      {
        _fontCol = res;
      }
    });

    addHandler(new ColourHandler()
    {
      public void setColour(java.awt.Color res)
      {
        _col = res;
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

    addHandler(new FontHandler()
    {
      public void setFont(java.awt.Font font)
      {
        _font = font;
      }
    });


    addAttributeHandler(new HandleAttribute(LABEL_TEXT)
    {
      public void setValue(String name, String value)
      {
        _label = fromXML(value);
      }
    });


    addAttributeHandler(new HandleAttribute(LABEL_LOCATION)
    {
      public void setValue(String name, String val)
      {
        lp.setAsText(val);
        _theLocation = (Integer) lp.getValue();
      }
    });

    addAttributeHandler(new HandleIntegerAttribute(LINE_STYLE)
    {
      public void setValue(String name, int value)
      {
        _lineStyle = new Integer(value);
      }
    });

    addAttributeHandler(new HandleIntegerAttribute(LINE_THICKNESS)
    {
      public void setValue(String name, int value)
      {
        _lineThickness = new Integer(value);
      }
    });


    addAttributeHandler(new HandleBooleanAttribute(SHAPE_VISIBLE)
    {
      public void setValue(String name, boolean value)
      {
        _isVisible = value;
      }
    });

    addAttributeHandler(new HandleBooleanAttribute(LABEL_VISIBLE)
    {
      public void setValue(String name, boolean value)
      {
        _labelVisible = value;
      }
    });


  }

  public void elementClosed()
  {
    MWC.GUI.Shapes.PlainShape shape = getShape();
    shape.setColor(_col);
    Debrief.Wrappers.ShapeWrapper sw = new Debrief.Wrappers.ShapeWrapper(_myType, shape, _col, null);

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
    	sw.setLineStyle(_lineStyle.intValue());
    }
    // line width?
    if(_lineThickness != null)
    {
    	sw.setLineThickness(_lineThickness.intValue());
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

  void exportThisPlottable(MWC.GUI.Plottable plottable,
                           org.w3c.dom.Element theShape,
                           org.w3c.dom.Document doc)
  {
    Debrief.Wrappers.ShapeWrapper sw = (Debrief.Wrappers.ShapeWrapper) plottable;

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
    if(sw.getLineStyle() != CanvasType.SOLID)
    {
      theShape.setAttribute(LINE_STYLE, writeThis(sw.getLineStyle()));
    }
    
    // and output the line thickness
    theShape.setAttribute(LINE_THICKNESS, writeThis(sw.getLineThickness()));

    // and the font
    java.awt.Font theFont = sw.getFont();
    if (sw != null)
    {
      FontHandler.exportFont(theFont, theShape, doc);
    }

  }


}
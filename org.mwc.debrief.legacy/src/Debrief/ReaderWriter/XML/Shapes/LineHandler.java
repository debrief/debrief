package Debrief.ReaderWriter.XML.Shapes;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import MWC.Utilities.ReaderWriter.XML.PlottableExporter;
import MWC.Utilities.ReaderWriter.XML.Util.LocationHandler;


abstract public class LineHandler extends ShapeHandler implements PlottableExporter
{

	private final static String ARROW_AT_END = "ArrowAtEnd";
	private final static String SHOW_AUTO_CALC = "ShowAutoCalc";
	
    MWC.GenericData.WorldLocation _start;
    MWC.GenericData.WorldLocation _end;

	protected boolean _arrowAtEnd = false;
	protected boolean _showAutoCalc = false;

  public LineHandler()
  {
    // inform our parent what type of class we are
    super("line");


    addHandler(new LocationHandler("tl"){
      public void setLocation(final MWC.GenericData.WorldLocation res)
      {
        _start = res;
      }
    });
    addHandler(new LocationHandler("br"){
      public void setLocation(final MWC.GenericData.WorldLocation res)
      {
        _end = res;
      }
    });
    addAttributeHandler(new HandleBooleanAttribute(ARROW_AT_END)
    {
      public void setValue(final String name, final boolean value)
      {
        _arrowAtEnd = value;
      }
    });
    addAttributeHandler(new HandleBooleanAttribute(SHOW_AUTO_CALC)
    {
      public void setValue(final String name, final boolean value)
      {
        _showAutoCalc = value;
      }
    });

  }

  public final MWC.GUI.Shapes.PlainShape getShape()
  {
    final MWC.GUI.Shapes.LineShape ls = new MWC.GUI.Shapes.LineShape(_start, _end);
    ls.setArrowAtEnd(_arrowAtEnd);
    ls.setShowAutoCalc(_showAutoCalc);
    return ls;
  }

  public final void elementClosed()
  {
    super.elementClosed();

    // reset the local parameters
    _start = _end = null;
    _arrowAtEnd = false;
    _showAutoCalc = false;
  }

  public final void exportThisPlottable(final MWC.GUI.Plottable plottable,final org.w3c.dom.Element parent, final org.w3c.dom.Document doc)
  {
    // output the shape related stuff first
    final org.w3c.dom.Element ePlottable = doc.createElement(_myType);

    super.exportThisPlottable(plottable, ePlottable, doc);

    // now our circle related stuff

    // get the circle
    final Debrief.Wrappers.ShapeWrapper sw = (Debrief.Wrappers.ShapeWrapper)plottable;
    final MWC.GUI.Shapes.PlainShape ps = sw.getShape();
    if(ps instanceof MWC.GUI.Shapes.LineShape)
    {
      // export the attributes
      final MWC.GUI.Shapes.LineShape cs = (MWC.GUI.Shapes.LineShape)ps;
      MWC.Utilities.ReaderWriter.XML.Util.LocationHandler.exportLocation(cs.getLine_Start(), "tl", ePlottable, doc);
      MWC.Utilities.ReaderWriter.XML.Util.LocationHandler.exportLocation(cs.getLineEnd(), "br", ePlottable, doc);
      ePlottable.setAttribute(ARROW_AT_END, writeThis(cs.getArrowAtEnd()));
      ePlottable.setAttribute(SHOW_AUTO_CALC, writeThis(cs.isShowAutoCalc()));
    }
    else
    {
      throw new java.lang.RuntimeException("wrong shape passed to line exporter");
    }

    // add ourselves to the output
    parent.appendChild(ePlottable);
  }


}
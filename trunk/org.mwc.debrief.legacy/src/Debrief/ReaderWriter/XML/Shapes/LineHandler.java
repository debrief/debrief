package Debrief.ReaderWriter.XML.Shapes;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import MWC.Utilities.ReaderWriter.XML.LayerHandler;
import MWC.Utilities.ReaderWriter.XML.Util.LocationHandler;


abstract public class LineHandler extends ShapeHandler implements LayerHandler.exporter
{

  private MWC.GenericData.WorldLocation _start;
  private MWC.GenericData.WorldLocation _end;

  public LineHandler()
  {
    // inform our parent what type of class we are
    super("line");


    addHandler(new LocationHandler("tl"){
      public void setLocation(MWC.GenericData.WorldLocation res)
      {
        _start = res;
      }
    });
    addHandler(new LocationHandler("br"){
      public void setLocation(MWC.GenericData.WorldLocation res)
      {
        _end = res;
      }
    });

  }

  public final MWC.GUI.Shapes.PlainShape getShape()
  {
    MWC.GUI.Shapes.LineShape ls = new MWC.GUI.Shapes.LineShape(_start, _end);
    return ls;
  }

  public final void elementClosed()
  {
    super.elementClosed();

    // reset the local parameters
    _start = _end = null;
  }

  public final void exportThisPlottable(MWC.GUI.Plottable plottable,org.w3c.dom.Element parent, org.w3c.dom.Document doc)
  {
    // output the shape related stuff first
    org.w3c.dom.Element ePlottable = doc.createElement(_myType);

    super.exportThisPlottable(plottable, ePlottable, doc);

    // now our circle related stuff

    // get the circle
    Debrief.Wrappers.ShapeWrapper sw = (Debrief.Wrappers.ShapeWrapper)plottable;
    MWC.GUI.Shapes.PlainShape ps = sw.getShape();
    if(ps instanceof MWC.GUI.Shapes.LineShape)
    {
      // export the attributes
      MWC.GUI.Shapes.LineShape cs = (MWC.GUI.Shapes.LineShape)ps;
      MWC.Utilities.ReaderWriter.XML.Util.LocationHandler.exportLocation(cs.getLine_Start(), "tl", ePlottable, doc);
      MWC.Utilities.ReaderWriter.XML.Util.LocationHandler.exportLocation(cs.getLineEnd(), "br", ePlottable, doc);
    }
    else
    {
      throw new java.lang.RuntimeException("wrong shape passed to line exporter");
    }

    // add ourselves to the output
    parent.appendChild(ePlottable);
  }


}
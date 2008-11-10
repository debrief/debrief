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



abstract public class RectangleHandler extends ShapeHandler implements LayerHandler.exporter
{

  private MWC.GenericData.WorldLocation _start;
  private MWC.GenericData.WorldLocation _end;
  private Boolean _filled;

  public RectangleHandler()
  {
    // inform our parent what type of class we are
    super("rectangle");


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

    addAttributeHandler(new HandleBooleanAttribute("Filled")
    {
      public void setValue(String name, boolean value)
      {
        _filled = new Boolean(value);
      }});
  }

  public final MWC.GUI.Shapes.PlainShape getShape()
  {
    MWC.GUI.Shapes.RectangleShape ls = new MWC.GUI.Shapes.RectangleShape(_start, _end);
    if(_filled != null)
      ls.setFilled(_filled.booleanValue());
    return ls;
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
    if(ps instanceof MWC.GUI.Shapes.RectangleShape)
    {
      // export the attributes
      MWC.GUI.Shapes.RectangleShape cs = (MWC.GUI.Shapes.RectangleShape)ps;
      MWC.Utilities.ReaderWriter.XML.Util.LocationHandler.exportLocation(cs.getCorner_TopLeft(), "tl", ePlottable, doc);
      MWC.Utilities.ReaderWriter.XML.Util.LocationHandler.exportLocation(cs.getCornerBottomRight(), "br", ePlottable, doc);
      ePlottable.setAttribute("Filled", writeThis(cs.getFilled()));

    }
    else
    {
      throw new java.lang.RuntimeException("wrong shape passed to rectangle exporter");
    }

    // add ourselves to the output
    parent.appendChild(ePlottable);
  }


}
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



abstract public class RectangleHandler extends ShapeHandler implements PlottableExporter
{
	
  private static final String TL = "tl";
  private static final String BR = "br";
  private static final String FILLED = "Filled";
  private static final String ORIENTATION = "Orientation";

  MWC.GenericData.WorldLocation _start;
  MWC.GenericData.WorldLocation _end;
  Boolean _filled;
  Integer _orientation = null;

  public RectangleHandler()
  {
    // inform our parent what type of class we are
    super("rectangle");


    addHandler(new LocationHandler(TL){
      public void setLocation(MWC.GenericData.WorldLocation res)
      {
        _start = res;
      }
    });
    addHandler(new LocationHandler(BR){
      public void setLocation(MWC.GenericData.WorldLocation res)
      {
        _end = res;
      }
    });

    addAttributeHandler(new HandleBooleanAttribute(FILLED)
    {
      public void setValue(String name, boolean value)
      {
        _filled = new Boolean(value);
      }});
    
    addAttributeHandler(new HandleIntegerAttribute(ORIENTATION)
    {
      public void setValue(String name, int value)
      {
        _orientation = new Integer(value);
      }});
  }

  public final MWC.GUI.Shapes.PlainShape getShape()
  {
    MWC.GUI.Shapes.RectangleShape ls = new MWC.GUI.Shapes.RectangleShape(_start, _end);
    if (_orientation != null)
    	//TODO: check if applicable
    	ls.setOrientation(_orientation);
    if(_filled != null)
      ls.setFilled(_filled.booleanValue());
    _orientation = null;
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
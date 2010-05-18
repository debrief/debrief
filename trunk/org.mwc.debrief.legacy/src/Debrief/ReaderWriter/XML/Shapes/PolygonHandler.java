package Debrief.ReaderWriter.XML.Shapes;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import org.xml.sax.Attributes;

import MWC.GUI.Shapes.PolygonShape;
import MWC.GenericData.WorldPath;
import MWC.Utilities.ReaderWriter.XML.PlottableExporter;
import MWC.Utilities.ReaderWriter.XML.Util.WorldPathHandler;


abstract public class PolygonHandler extends ShapeHandler implements PlottableExporter
{

  private static final String FILLED_STR = "Filled";
  private static final String CLOSED_STR = "Closed";
	WorldPath _polygon;
  Boolean _filled = null;
  Boolean _closed = null;
  

  public PolygonHandler()
  {
    // inform our parent what type of class we are
    super("polygon");


    addHandler(new WorldPathHandler(){
      public void setPath(WorldPath path)
      {
        _polygon = path;
      }
    });

    addAttributeHandler(new HandleBooleanAttribute(FILLED_STR)
    {
      public void setValue(String name, boolean value)
      {
        _filled = new Boolean(value);
      }});

    addAttributeHandler(new HandleBooleanAttribute(CLOSED_STR)
    {
      public void setValue(String name, boolean value)
      {
        _closed = new Boolean(value);
      }});    
  }

  // this is one of ours, so get on with it!
  protected final void handleOurselves(String name, Attributes attributes)
  {
    super.handleOurselves(name, attributes);
  }



  public final MWC.GUI.Shapes.PlainShape getShape()
  {
    PolygonShape poly = new PolygonShape(_polygon);
    if(_filled != null)
      poly.setFilled(_filled.booleanValue());
    if(_closed != null)
    	poly.setClosed(_closed.booleanValue());

    return poly;
  }


	public void elementClosed()
	{
		super.elementClosed();
		
		// reset our temp variables
		
    _polygon = null;
    _filled = null;
    _closed = null;
		
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
    if(ps instanceof MWC.GUI.Shapes.PolygonShape)
    {
      // export the attributes
      MWC.GUI.Shapes.PolygonShape cs = (MWC.GUI.Shapes.PolygonShape)ps;
      ePlottable.setAttribute(FILLED_STR, writeThis(cs.getFilled()));
      ePlottable.setAttribute(CLOSED_STR, writeThis(cs.getClosed()));
      MWC.Utilities.ReaderWriter.XML.Util.WorldPathHandler.exportThis(cs.getPoints(), ePlottable, doc);
    }
    else
    {
      throw new RuntimeException("wrong shape passed to Polygon exporter");
    }

    // add ourselves to the output
    parent.appendChild(ePlottable);
  }
}
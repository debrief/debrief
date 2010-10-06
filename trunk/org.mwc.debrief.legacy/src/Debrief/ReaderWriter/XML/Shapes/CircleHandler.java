package Debrief.ReaderWriter.XML.Shapes;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import MWC.GenericData.WorldDistance;
import MWC.Utilities.ReaderWriter.XML.PlottableExporter;
import MWC.Utilities.ReaderWriter.XML.Util.LocationHandler;
import MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler;

import org.xml.sax.Attributes;


abstract public class CircleHandler extends ShapeHandler implements PlottableExporter
{

  protected MWC.GenericData.WorldLocation _centre;
  protected double _radius;     // in kiloyards
  protected Boolean _filled;
	protected WorldDistance _radiusDist = null;
  private static final String MY_TYPE = "circle";
  private static final String CENTRE = "centre";
  private static final String RADIUS = "Radius";
  private static final String FILLED = "Filled";

  public CircleHandler()
  {
    this(MY_TYPE);
  }

  public CircleHandler(String theType)
  {

    // inform our parent what type of class we are
    super(theType);


    addHandler(new LocationHandler(CENTRE)
    {
      public void setLocation(MWC.GenericData.WorldLocation res)
      {
        _centre = res;
      }
    });
    
    addHandler(new WorldDistanceHandler(RADIUS)
		{
			
			@Override
			public void setWorldDistance(WorldDistance res)
			{
				_radiusDist  = res;
			}
		});

    addAttributeHandler(new HandleAttribute(RADIUS)
    {
      public void setValue(String name, String val)
      {
        try
        {
          _radius = readThisDouble(val);
        }
        catch (java.text.ParseException pe)
        {
          MWC.Utilities.Errors.Trace.trace(pe, "Failed reading in:" + name + " value is:" + val);
        }


      }
    });

    addAttributeHandler(new HandleBooleanAttribute(FILLED)
    {
      public void setValue(String name, boolean value)
      {
        _filled = new Boolean(value);
      }
    });

  }

  // this is one of ours, so get on with it!
  protected void handleOurselves(String name, Attributes attributes)
  {
    _radius = 0.0;
    _centre = null;
    _filled = null;
    _radiusDist = null;
    super.handleOurselves(name, attributes);
  }


  public MWC.GUI.Shapes.PlainShape getShape()
  {
    MWC.GUI.Shapes.CircleShape ls = null;
    if(_radiusDist != null)
    {
    	ls = new MWC.GUI.Shapes.CircleShape(_centre, _radiusDist);
    }
    else
    	ls = new MWC.GUI.Shapes.CircleShape(_centre, new WorldDistance(_radius * 1000, WorldDistance.YARDS));
    
    if (_filled != null)
      ls.setFilled(_filled.booleanValue());

    return ls;
  }

  public void exportThisPlottable(MWC.GUI.Plottable plottable, org.w3c.dom.Element parent, org.w3c.dom.Document doc)
  {
    // output the shape related stuff first
    org.w3c.dom.Element ePlottable = doc.createElement(_myType);

    super.exportThisPlottable(plottable, ePlottable, doc);

    // now our circle related stuff

    // get the circle
    Debrief.Wrappers.ShapeWrapper sw = (Debrief.Wrappers.ShapeWrapper) plottable;
    MWC.GUI.Shapes.PlainShape ps = sw.getShape();
    // export the attributes
    MWC.GUI.Shapes.CircleShape cs = (MWC.GUI.Shapes.CircleShape) ps;
    exportCircleAttributes(ePlottable, cs, doc);

    // add ourselves to the output
    parent.appendChild(ePlottable);
  }

  // export the circle  specific components
  protected void exportCircleAttributes(org.w3c.dom.Element ePlottable, MWC.GUI.Shapes.CircleShape cs,
                                        org.w3c.dom.Document doc)
  {
    ePlottable.setAttribute(FILLED, writeThis(cs.getFilled()));
    LocationHandler.exportLocation(cs.getCentre(), CENTRE, ePlottable, doc);
    WorldDistanceHandler.exportDistance(RADIUS, cs.getRadius(), ePlottable, doc);
  }

}
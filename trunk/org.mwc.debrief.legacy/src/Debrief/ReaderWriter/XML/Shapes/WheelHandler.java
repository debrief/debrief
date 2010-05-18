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

import MWC.GUI.Properties.*;
import MWC.GenericData.WorldDistance;
import MWC.Utilities.ReaderWriter.XML.PlottableExporter;
import MWC.Utilities.ReaderWriter.XML.Util.LocationHandler;

abstract public class WheelHandler extends ShapeHandler implements PlottableExporter
{

	private static final String OUTER_RADIUS = "Outer";

	private static final String INNER_RADIUS = "Inner";

	MWC.GenericData.WorldLocation _centre;

	double _inner; // degs

	double _outer; // degs

	Integer _spokeSize = null; // degs

	public WheelHandler()
	{
		// inform our parent what type of class we are
		super("wheel");

		addHandler(new LocationHandler("centre")
		{
			public void setLocation(MWC.GenericData.WorldLocation res)
			{
				_centre = res;
			}
		});

		addAttributeHandler(new HandleAttribute(INNER_RADIUS)
		{
			public void setValue(String name, String val)
			{
				try
				{
					_inner = readThisDouble(val);
				}
				catch (java.text.ParseException pe)
				{
					MWC.Utilities.Errors.Trace.trace(pe, "Failed reading in:" + name + " value is:"
							+ val);
				}

			}
		});
		addAttributeHandler(new HandleAttribute(OUTER_RADIUS)
		{
			public void setValue(String name, String val)
			{
				try
				{
					_outer = readThisDouble(val);
				}
				catch (java.text.ParseException pe)
				{
					MWC.Utilities.Errors.Trace.trace(pe, "Failed reading in:" + name + " value is:"
							+ val);
				}

			}
		});
		addAttributeHandler(new HandleAttribute("SpokeSize")
		{
			public void setValue(String name, String val)
			{
				_spokeSize = Integer.valueOf(val);
			}
		});

	}

	// this is one of ours, so get on with it!
	protected final void handleOurselves(String name, Attributes attributes)
	{
		_inner = _outer = 0.0;
		_centre = null;
		super.handleOurselves(name, attributes);
	}

	public final MWC.GUI.Shapes.PlainShape getShape()
	{
		MWC.GUI.Shapes.WheelShape ls = new MWC.GUI.Shapes.WheelShape(_centre, _inner, _outer);
		if(_spokeSize != null)
			 ls.setSpokeSize(new SteppingBoundedInteger(_spokeSize.intValue(), 0,10, 1));
		
		return ls;
	}

	public final void exportThisPlottable(MWC.GUI.Plottable plottable,
			org.w3c.dom.Element parent, org.w3c.dom.Document doc)
	{
		// output the shape related stuff first
		org.w3c.dom.Element ePlottable = doc.createElement(_myType);

		super.exportThisPlottable(plottable, ePlottable, doc);

		// now our circle related stuff

		// get the circle
		Debrief.Wrappers.ShapeWrapper sw = (Debrief.Wrappers.ShapeWrapper) plottable;
		MWC.GUI.Shapes.PlainShape ps = sw.getShape();
		if (ps instanceof MWC.GUI.Shapes.WheelShape)
		{
			// export the attributes
			MWC.GUI.Shapes.WheelShape cs = (MWC.GUI.Shapes.WheelShape) ps;
			ePlottable.setAttribute(INNER_RADIUS, writeThis(cs.getRadiusInner().getValueIn(
					WorldDistance.YARDS)));
			ePlottable.setAttribute(OUTER_RADIUS, writeThis(cs.getRadiusOuter().getValueIn(
					WorldDistance.YARDS)));
			ePlottable.setAttribute("SpokeSize", writeThis(cs.getSpokeSize().getCurrent()));
			MWC.Utilities.ReaderWriter.XML.Util.LocationHandler.exportLocation(cs.getCentre(),
					"centre", ePlottable, doc);
		}
		else
		{
			throw new java.lang.RuntimeException("wrong shape passed to Wheel exporter");
		}

		// add ourselves to the output
		parent.appendChild(ePlottable);
	}

}
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

import MWC.GUI.Shapes.EllipseShape;
import MWC.GenericData.WorldDistance;
import MWC.Utilities.ReaderWriter.XML.PlottableExporter;
import MWC.Utilities.ReaderWriter.XML.Util.LocationHandler;
import MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler;

abstract public class EllipseHandler extends ShapeHandler implements
		PlottableExporter
{

	MWC.GenericData.WorldLocation _centre;
	WorldDistance _maxDist = null;
	WorldDistance _minDist = null;
	double _maxima; // in kiloyards
	double _minima; // in kiloyards
	double _orient;
	Boolean _filled;

	public EllipseHandler()
	{
		// inform our parent what type of class we are
		super("ellipse");

		addHandler(new LocationHandler("centre")
		{
			public void setLocation(MWC.GenericData.WorldLocation res)
			{
				_centre = res;
			}
		});

		addHandler(new WorldDistanceHandler("Maxima")
		{

			@Override
			public void setWorldDistance(WorldDistance res)
			{
				_maxDist = res;
			}
		});
		addHandler(new WorldDistanceHandler("Minima")
		{

			@Override
			public void setWorldDistance(WorldDistance res)
			{
				_minDist = res;
			}
		});

		addAttributeHandler(new HandleAttribute("Maxima")
		{
			public void setValue(String name, String val)
			{
				try
				{
					_maxima = readThisDouble(val);
				}
				catch (java.text.ParseException pe)
				{
					MWC.Utilities.Errors.Trace.trace(pe, "Failed reading in:" + name
							+ " value is:" + val);
				}
			}
		});
		addAttributeHandler(new HandleAttribute("Minima")
		{
			public void setValue(String name, String val)
			{
				try
				{
					_minima = readThisDouble(val);
				}
				catch (java.text.ParseException pe)
				{
					MWC.Utilities.Errors.Trace.trace(pe, "Failed reading in:" + name
							+ " value is:" + val);
				}
			}
		});
		addAttributeHandler(new HandleAttribute("Orient")
		{
			public void setValue(String name, String val)
			{
				try
				{
					_orient = readThisDouble(val);
				}
				catch (java.text.ParseException pe)
				{
					MWC.Utilities.Errors.Trace.trace(pe, "Failed reading in:" + name
							+ " value is:" + val);
				}
			}
		});

		addAttributeHandler(new HandleBooleanAttribute("Filled")
		{
			public void setValue(String name, boolean value)
			{
				_filled = new Boolean(value);
			}
		});

	}

	// this is one of ours, so get on with it!
	protected final void handleOurselves(String name, Attributes attributes)
	{
		_maxima = _minima = _orient = 0.0;
		super.handleOurselves(name, attributes);
	}

	public final MWC.GUI.Shapes.PlainShape getShape()
	{
		EllipseShape ls = null;
		if (_maxDist != null)
		{
			ls = new EllipseShape(_centre, _orient, _maxDist, _minDist);
		}
		else
		{
			double max_degs = MWC.Algorithms.Conversions.Yds2Degs(_maxima * 1000);
			double min_degs = MWC.Algorithms.Conversions.Yds2Degs(_minima * 1000);
			ls = new MWC.GUI.Shapes.EllipseShape(_centre, _orient, new WorldDistance(
					max_degs, WorldDistance.DEGS), new WorldDistance(min_degs,
					WorldDistance.DEGS));
		}
		if (_filled != null)
			ls.setFilled(_filled.booleanValue());
		return ls;
	}

	public final void elementClosed()
	{
		// get the parent to do the closing stuff
		super.elementClosed();

		// reset the local parameters
		_centre = null;
		_maxDist = _minDist = null;
		_orient = _maxima = _minima = 0.0;
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
		if (ps instanceof MWC.GUI.Shapes.EllipseShape)
		{
			// export the attributes
			MWC.GUI.Shapes.EllipseShape cs = (MWC.GUI.Shapes.EllipseShape) ps;
			LocationHandler.exportLocation(cs.getCentre(), "centre", ePlottable, doc);
			ePlottable.setAttribute("Orient", writeThis(cs.getOrientation()));
			ePlottable.setAttribute("Filled", writeThis(cs.getFilled()));
			WorldDistanceHandler.exportDistance("Maxima", cs.getMaxima(), ePlottable,
					doc);
			WorldDistanceHandler.exportDistance("Minima", cs.getMinima(), ePlottable,
					doc);
		}
		else
		{
			throw new java.lang.RuntimeException(
					"wrong shape passed to ellipse exporter");
		}

		// add ourselves to the output
		parent.appendChild(ePlottable);
	}

}
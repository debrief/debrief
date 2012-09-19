package Debrief.ReaderWriter.XML.Shapes;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import MWC.GUI.Properties.LabelLocationPropertyEditor;
import MWC.GUI.Shapes.RangeRingShape;
import MWC.GenericData.WorldDistance;
import MWC.Utilities.ReaderWriter.XML.PlottableExporter;
import MWC.Utilities.ReaderWriter.XML.Util.LocationHandler;
import MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler;

abstract public class RangeRingsHandler extends ShapeHandler implements
		PlottableExporter
{

	private static final String RING_WIDTH = "RingWidth";
	private static final String NUM_RINGS = "NumRings";
	private static final String CENTRE = "centre";
	private static final String RANGE_LABEL_LOC = "RangeLabelLoc";

	MWC.GenericData.WorldLocation _centre;
	WorldDistance _ringWidth;
	protected String _labelLoc;
	protected Integer _numRings;

	public RangeRingsHandler()
	{
		// inform our parent what type of class we are
		super("range_rings");

		addHandler(new LocationHandler(CENTRE)
		{
			public void setLocation(MWC.GenericData.WorldLocation res)
			{
				_centre = res;
			}
		});

		addHandler(new WorldDistanceHandler(RING_WIDTH)
		{

			@Override
			public void setWorldDistance(WorldDistance res)
			{
				_ringWidth = res;
			}
		});

		addAttributeHandler(new HandleAttribute(NUM_RINGS)
		{
			public void setValue(String name, String val)
			{
				_numRings = Integer.valueOf(val);
			}
		});

		addAttributeHandler(new HandleAttribute(RANGE_LABEL_LOC)
		{
			public void setValue(String name, String val)
			{
				_labelLoc = val;
			}
		});

	}

	public final MWC.GUI.Shapes.PlainShape getShape()
	{
		MWC.GUI.Shapes.RangeRingShape ls = new MWC.GUI.Shapes.RangeRingShape(
				_centre, _numRings, _ringWidth);

		if (_labelLoc != null)
		{
			LabelLocationPropertyEditor lp = new LabelLocationPropertyEditor();
			lp.setValue(_labelLoc);
			ls.setRangeLabelLocation((Integer) lp.getValue());
		}

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
		if (ps instanceof MWC.GUI.Shapes.RangeRingShape)
		{
			// export the attributes
			RangeRingShape cs = (MWC.GUI.Shapes.RangeRingShape) ps;

			LabelLocationPropertyEditor lpe = new LabelLocationPropertyEditor();
			lpe.setValue(cs.getRangeLabelLocation());
			ePlottable.setAttribute(RANGE_LABEL_LOC, lpe.getAsText());
			ePlottable.setAttribute(NUM_RINGS, writeThis(cs.getNumRings()
					.getCurrent()));
			LocationHandler.exportLocation(cs.getCentre(), CENTRE, ePlottable, doc);
			WorldDistanceHandler.exportDistance(RING_WIDTH, cs.getRingWidth(),
					ePlottable, doc);
		}
		else
		{
			throw new java.lang.RuntimeException(
					"wrong shape passed to range ring exporter");
		}

		// add ourselves to the output
		parent.appendChild(ePlottable);
	}

}
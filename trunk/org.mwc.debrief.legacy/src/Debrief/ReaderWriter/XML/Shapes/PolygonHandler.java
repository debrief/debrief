package Debrief.ReaderWriter.XML.Shapes;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import org.xml.sax.Attributes;

import Debrief.Wrappers.ShapeWrapper;
import MWC.GUI.Shapes.PolygonShape;
import MWC.GUI.Shapes.PolygonShape.PolygonNode;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldPath;
import MWC.Utilities.ReaderWriter.XML.PlottableExporter;
import MWC.Utilities.ReaderWriter.XML.Util.WorldPathHandler;

abstract public class PolygonHandler extends ShapeHandler implements
		PlottableExporter
{

	private static final String FILLED_STR = "Filled";
	private static final String CLOSED_STR = "Closed";
	private static final String LABEL_NODES = "LabelNodes";
	WorldPath _polygon;
	Boolean _filled = null;
	Boolean _closed = null;
	Boolean _labelNodes = null;

	public PolygonHandler()
	{
		// inform our parent what type of class we are
		super("polygon");

		addHandler(new WorldPathHandler()
		{
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
			}
		});
		addAttributeHandler(new HandleBooleanAttribute(LABEL_NODES)
		{
			public void setValue(String name, boolean value)
			{
				_labelNodes = new Boolean(value);
			}
		});

		addAttributeHandler(new HandleBooleanAttribute(CLOSED_STR)
		{
			public void setValue(String name, boolean value)
			{
				_closed = new Boolean(value);
			}
		});
	}

	// this is one of ours, so get on with it!
	protected final void handleOurselves(String name, Attributes attributes)
	{
		super.handleOurselves(name, attributes);
	}

	public final MWC.GUI.Shapes.PlainShape getShape()
	{
		Vector<PolygonNode> nodes = new Vector<PolygonNode>();
		PolygonShape poly = new PolygonShape(nodes);
		Collection<WorldLocation> iter = _polygon.getPoints();
		for (Iterator<WorldLocation> iterator = iter.iterator(); iterator.hasNext();)
		{
			WorldLocation worldLocation = (WorldLocation) iterator.next();
			nodes.add(new PolygonNode("" + (nodes.size() + 1), worldLocation, poly));
		}
		if (_filled != null)
			poly.setFilled(_filled.booleanValue());
		if (_closed != null)
			poly.setClosed(_closed.booleanValue());
		if (_labelNodes != null)
			poly.setShowNodeLabels(_labelNodes);

		return poly;
	}

	protected ShapeWrapper getWrapper()
	{
		PolygonShape shape = (PolygonShape) getShape();
		shape.setColor(_col);
		Debrief.Wrappers.PolygonWrapper sw = new Debrief.Wrappers.PolygonWrapper(
				this._label, shape, _col, null);
		sw.setName(_label);
		sw.setColor(_col);
		return sw;
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
		if (ps instanceof MWC.GUI.Shapes.PolygonShape)
		{
			// export the attributes
			MWC.GUI.Shapes.PolygonShape cs = (MWC.GUI.Shapes.PolygonShape) ps;
			ePlottable.setAttribute(FILLED_STR, writeThis(cs.getFilled()));
			ePlottable.setAttribute(CLOSED_STR, writeThis(cs.getClosed()));
			ePlottable.setAttribute(LABEL_NODES, writeThis(cs.getShowNodeLabels()));

			MWC.Utilities.ReaderWriter.XML.Util.WorldPathHandler.exportThis(
					cs.getPoints(), ePlottable, doc);
		}
		else
		{
			throw new RuntimeException("wrong shape passed to Polygon exporter");
		}

		// add ourselves to the output
		parent.appendChild(ePlottable);
	}
}
package MWC.Utilities.ReaderWriter.XML.Features;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import java.awt.Color;

import MWC.GenericData.WorldLocation;
import MWC.Utilities.ReaderWriter.XML.PlottableExporter;
import MWC.Utilities.ReaderWriter.XML.Util.ColourHandler;
import MWC.Utilities.ReaderWriter.XML.Util.FontHandler;
import MWC.Utilities.ReaderWriter.XML.Util.LocationHandler;

abstract public class ChartBoundsHandler extends
		MWC.Utilities.ReaderWriter.XML.MWCXMLReader implements  PlottableExporter
{

	private static final String CHART_REFERENCE = "ChartReference";
	private static final String FILE_NAME = "FileName";
	private static final String BOTTOM_RIGHT = "BottomRight";
	private static final String TOP_LEFT = "TopLeft";
	String _myType = null;
	String _label = null;
	java.awt.Font _font = null;
	Integer _theLocation = null;
	boolean _isVisible = false;
	boolean _labelVisible = true;
	private WorldLocation _tl;
	private WorldLocation _br;
	private Color _col;
	protected String _filename;

	/**
	 * class which contains list of textual representations of label locations
	 */
	static final MWC.GUI.Properties.LocationPropertyEditor lp = new MWC.GUI.Properties.LocationPropertyEditor();

	/**
	 * and define the strings used to describe the shape
	 * 
	 */
	private static final String LABEL_VISIBLE = "LabelVisible";
	private static final String SHAPE_VISIBLE = "Visible";
	private static final String LABEL_LOCATION = "LabelLocation";
	private static final String LABEL_TEXT = "Label";
	public ChartBoundsHandler()
	{
		// inform our parent what type of class we are
		super(CHART_REFERENCE);

		addHandler(new LocationHandler(TOP_LEFT)
		{
			@Override
			public void setLocation(WorldLocation res)
			{
				_tl = res;
			}
		});
		addHandler(new LocationHandler(BOTTOM_RIGHT)
		{
			@Override
			public void setLocation(WorldLocation res)
			{
				_br = res;
			}
		});

		addHandler(new FontHandler()
		{
			public void setFont(java.awt.Font font)
			{
				_font = font;
			}
		});

		addAttributeHandler(new HandleAttribute(LABEL_TEXT)
		{
			public void setValue(String name, String value)
			{
				_label = fromXML(value);
			}
		});

		addHandler(new ColourHandler()
		{
			@Override
			public void setColour(Color res)
			{
				_col = res;
			}
		});

		addAttributeHandler(new HandleAttribute(LABEL_LOCATION)
		{
			public void setValue(String name, String val)
			{
				lp.setAsText(val);
				_theLocation = (Integer) lp.getValue();
			}
		});

		addAttributeHandler(new HandleAttribute(FILE_NAME)
		{
			public void setValue(String name, String val)
			{
				_filename = val;
			}
		});

		addAttributeHandler(new HandleBooleanAttribute(SHAPE_VISIBLE)
		{
			public void setValue(String name, boolean value)
			{
				_isVisible = value;
			}
		});

		addAttributeHandler(new HandleBooleanAttribute(LABEL_VISIBLE)
		{
			public void setValue(String name, boolean value)
			{
				_labelVisible = value;
			}
		});

	}

	public void elementClosed()
	{

		MWC.GUI.Shapes.ChartBoundsWrapper sw = new MWC.GUI.Shapes.ChartBoundsWrapper(
				_label, _tl, _br, _col, _filename);

		if (_theLocation != null)
		{
			sw.setLabelLocation(_theLocation);
		}
		sw.setVisible(_isVisible);
		sw.setLabelVisible(_labelVisible);

		addPlottable(sw);

		// reset the local parameters
		_label = null;
		_theLocation = null;
		_isVisible = true;
		_filename = null;
	}

	abstract public void addPlottable(MWC.GUI.Plottable plottable);

	public void exportThisPlottable(MWC.GUI.Plottable plottable,
			org.w3c.dom.Element parent, org.w3c.dom.Document doc)
	{

		// output the shape related stuff first
		org.w3c.dom.Element theShape = doc.createElement(CHART_REFERENCE);

		MWC.GUI.Shapes.ChartBoundsWrapper sw = (MWC.GUI.Shapes.ChartBoundsWrapper) plottable;

		// put the parameters into the parent
		theShape.setAttribute(LABEL_TEXT, toXML(sw.getName()));
		theShape.setAttribute(FILE_NAME, sw.getFileName());
		lp.setValue(sw.getLabelLocation());
		theShape.setAttribute(LABEL_LOCATION, lp.getAsText());
		theShape.setAttribute(SHAPE_VISIBLE, writeThis(sw.getVisible()));
		theShape.setAttribute(LABEL_VISIBLE, writeThis(sw.getLabelVisible()));

		// output the colour for the shape
		MWC.Utilities.ReaderWriter.XML.Util.ColourHandler.exportColour(sw
				.getShape().getColor(), theShape, doc);

		// and the rectangle corners
		LocationHandler.exportLocation(sw.getShape().getCorner_TopLeft(), TOP_LEFT,
				theShape, doc);
		LocationHandler.exportLocation(sw.getShape().getCornerBottomRight(),
				BOTTOM_RIGHT, theShape, doc);

		// add ourselves to the output
		parent.appendChild(theShape);
	}

}
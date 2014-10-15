/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package Debrief.ReaderWriter.XML.Tactical;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import java.awt.Color;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import Debrief.Wrappers.Track.PlanningSegment;
import Debrief.Wrappers.Track.PlanningSegment.ClosingSegment;
import Debrief.Wrappers.Track.TrackSegment;
import MWC.GUI.CanvasType;
import MWC.GUI.Properties.LineStylePropertyEditor;
import MWC.GUI.Properties.PlanningLegCalcModelPropertyEditor;
import MWC.GenericData.Duration;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldSpeed;
import MWC.Utilities.ReaderWriter.XML.Util.ColourHandler;
import MWC.Utilities.ReaderWriter.XML.Util.DurationHandler;
import MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler;
import MWC.Utilities.ReaderWriter.XML.Util.WorldSpeedHandler;

abstract public class PlanningSegmentHandler extends
		MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{
	private static final String COURSE = "course";
	public static final String CLOSING_SEGMENT = "closing_segment";
	private static final String PLANNING_SEGMENT = "planning_segment";
	public static final String VISIBLE = "Visible";
	public static final String NAME = "Name";
	public static final String LINE_STYLE = "LineStyle";
	private static final String CALC_MODEL = "calcModel";

	private boolean _visible;
	private String _name;
	private int _lineStyle = CanvasType.SOLID;
	private Duration _duration;
	private WorldSpeed _speed;
	private WorldDistance _length;
	private double _course;
	private int _calcModel;
	private Color _color;

	public PlanningSegmentHandler()
	{
		this(PLANNING_SEGMENT);
	}

	public PlanningSegmentHandler(final String segType)
	{
		// inform our parent what type of class we are
		super(segType);

		addAttributeHandler(new HandleBooleanAttribute(VISIBLE)
		{
			@Override
			public void setValue(final String name, final boolean val)
			{
				_visible = val;
			}
		});
		addAttributeHandler(new HandleAttribute(LINE_STYLE)
		{
			@Override
			public void setValue(final String name, final String val)
			{
				final LineStylePropertyEditor lp = new LineStylePropertyEditor();
				lp.setAsText(val);
				final Integer iVal = (Integer) lp.getValue();
				_lineStyle = iVal.intValue();
			}
		});
		addAttributeHandler(new HandleAttribute(NAME)
		{
			@Override
			public void setValue(final String name, final String val)
			{
				_name = val;
			}
		});

		addHandler(new DurationHandler()
		{
			@Override
			public void setDuration(final Duration res)
			{
				_duration = res;
			}
		});
		addHandler(new WorldDistanceHandler()
		{
			@Override
			public void setWorldDistance(final WorldDistance res)
			{
				_length = res;
			}
		});
		addHandler(new ColourHandler()
		{
			@Override
			public void setColour(final Color res)
			{
				_color = res;
			}
		});
		addHandler(new WorldSpeedHandler()
		{
			@Override
			public void setSpeed(final WorldSpeed res)
			{
				_speed = res;
			}
		});
		addAttributeHandler(new HandleDoubleAttribute(COURSE)
		{
			@Override
			public void setValue(final String name, final double value)
			{
				_course = value;
			}
		});
		addAttributeHandler(new HandleAttribute(CALC_MODEL)
		{

			@Override
			public void setValue(final String name, final String value)
			{
				final PlanningLegCalcModelPropertyEditor ed = new PlanningLegCalcModelPropertyEditor();
				ed.setAsText(value);
				final Integer val = (Integer) ed.getValue();
				_calcModel = val.intValue();
			}
		});

	}

	protected PlanningSegment createSegment()
	{
		final PlanningSegment res = new PlanningSegment(_name, _course, _speed, _length,
				_color);
		res.setDuration(_duration);
		res.setCalculation(_calcModel);
		return res;
	}
	
	protected PlanningSegment createClosingSegment()
	{
		final PlanningSegment res = new ClosingSegment(_name, _course, _speed, _length,
				_color);
		res.setDuration(_duration);
		res.setCalculation(_calcModel);
		return res;
	}

	public final void elementClosed()
	{
		PlanningSegment segment ;

		// see if it's a planning segment, or a closing segment
		if(this.canHandleThis(CLOSING_SEGMENT))
			segment = createClosingSegment();
		else
			segment = createSegment();
		
		segment.setVisible(_visible);
		segment.setName(_name);
		segment.setLineStyle(_lineStyle);
		segment.setColor(_color);
		addSegment(segment);
		segment = null;
	}

	public static Element exportThisSegment(final org.w3c.dom.Document doc,
			final Element trk, final PlanningSegment seg)
	{
		return exportThisSegment(doc, trk, seg, PLANNING_SEGMENT);
	}

	public static Element exportThisSegment(final org.w3c.dom.Document doc,
			final Element trk, final PlanningSegment seg, final String segName)
	{
		final PlanningSegment ps = (PlanningSegment) seg;
		final Element segE = doc.createElement(segName);
		trk.appendChild(segE);

		final LineStylePropertyEditor ls = new LineStylePropertyEditor();
		ls.setValue(seg.getLineStyle());

		// sort out the attributes
		segE.setAttribute(VISIBLE, writeThis(seg.getVisible()));
		segE.setAttribute(NAME, seg.getName());
		segE.setAttribute(LINE_STYLE, ls.getAsText());

		final PlanningLegCalcModelPropertyEditor ed = new PlanningLegCalcModelPropertyEditor();
		ed.setValue(new Integer(ps.getCalculation()));
		segE.setAttribute(CALC_MODEL, ed.getAsText());

		// and the planning items
		WorldDistanceHandler.exportDistance(ps.getDistance(), segE, doc);
		WorldSpeedHandler.exportSpeed(ps.getSpeed(), segE, doc);
		ColourHandler.exportColour(ps.getColor(), segE, doc);
		DurationHandler.exportDuration(ps.getDuration(), segE, doc);
		segE.setAttribute(COURSE, writeThis(ps.getCourse()));

		return segE;
	}

	abstract public void addSegment(TrackSegment segment);

	public static Element exportThisClosingSegment(final Document doc, final Element trk,
			final PlanningSegment segment)
	{
		return exportThisSegment(doc, trk, segment, CLOSING_SEGMENT);
	}

}
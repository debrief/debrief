package Debrief.ReaderWriter.XML.Tactical;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import org.w3c.dom.Element;

import Debrief.Wrappers.Track.PlanningSegment;
import Debrief.Wrappers.Track.TrackSegment;
import MWC.GUI.CanvasType;
import MWC.GUI.Properties.LineStylePropertyEditor;
import MWC.GUI.Properties.PlanningLegCalcModelPropertyEditor;
import MWC.GenericData.Duration;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldSpeed;
import MWC.Utilities.ReaderWriter.XML.Util.DurationHandler;
import MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler;
import MWC.Utilities.ReaderWriter.XML.Util.WorldSpeedHandler;

abstract public class PlanningSegmentHandler extends
		MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{
	private static final String COURSE = "course";
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

	public PlanningSegmentHandler()
	{
		// inform our parent what type of class we are
		super(PLANNING_SEGMENT);

		addAttributeHandler(new HandleBooleanAttribute(VISIBLE)
		{
			@Override
			public void setValue(String name, boolean val)
			{
				_visible = val;
			}
		});
		addAttributeHandler(new HandleAttribute(LINE_STYLE)
		{
			@Override
			public void setValue(String name, String val)
			{
				LineStylePropertyEditor lp = new LineStylePropertyEditor();
				lp.setAsText(val);
				Integer iVal = (Integer) lp.getValue();
				_lineStyle = iVal.intValue();
			}
		});
		addAttributeHandler(new HandleAttribute(NAME)
		{
			@Override
			public void setValue(String name, String val)
			{
				_name = val;
			}
		});

		addHandler(new DurationHandler()
		{
			@Override
			public void setDuration(Duration res)
			{
				_duration = res;
			}
		});
		addHandler(new WorldDistanceHandler()
		{
			@Override
			public void setWorldDistance(WorldDistance res)
			{
				_length = res;
			}
		});
		addHandler(new WorldSpeedHandler()
		{
			@Override
			public void setSpeed(WorldSpeed res)
			{
				_speed = res;
			}
		});
		addAttributeHandler(new HandleDoubleAttribute(COURSE)
		{
			@Override
			public void setValue(String name, double value)
			{
				_course = value;
			}
		});
		addAttributeHandler(new HandleAttribute(CALC_MODEL)
		{

			@Override
			public void setValue(String name, String value)
			{
				PlanningLegCalcModelPropertyEditor ed = new PlanningLegCalcModelPropertyEditor();
				ed.setAsText(value);
				Integer val = (Integer) ed.getValue();
				_calcModel = val.intValue();
			}
		});

	}

	protected TrackSegment createTrack()
	{
		PlanningSegment res = new PlanningSegment();
		res.setCourse(_course);
		res.setDuration(_duration);
		res.setDistance(_length);
		res.setSpeed(_speed);
		res.setCalculation(_calcModel);
		return res;
	}

	public final void elementClosed()
	{
		TrackSegment segment = createTrack();
		segment.setVisible(_visible);
		segment.setName(_name);
		segment.setLineStyle(_lineStyle);
		addSegment(segment);
		segment = null;
	}

	public static Element exportThisSegment(org.w3c.dom.Document doc, Element trk,
			PlanningSegment seg)
	{
		PlanningSegment ps = (PlanningSegment) seg;
		final Element segE = doc.createElement(PLANNING_SEGMENT);
		trk.appendChild(segE);

		LineStylePropertyEditor ls = new LineStylePropertyEditor();
		ls.setValue(seg.getLineStyle());

		// sort out the attributes
		segE.setAttribute(VISIBLE, writeThis(seg.getVisible()));
		segE.setAttribute(NAME, seg.getName());
		segE.setAttribute(LINE_STYLE, ls.getAsText());
		
		PlanningLegCalcModelPropertyEditor ed = new PlanningLegCalcModelPropertyEditor();
		ed.setValue(new Integer(ps.getCalculation()));
		segE.setAttribute(CALC_MODEL, ed.getAsText());
		
		// and the planning items
		WorldDistanceHandler.exportDistance(ps.getDistance(), segE, doc);
		WorldSpeedHandler.exportSpeed(ps.getSpeed(), segE, doc);
		DurationHandler.exportDuration(ps.getDuration(), segE, doc);
		segE.setAttribute(COURSE,writeThis(ps.getCourse()));

		return segE;
	}

	abstract public void addSegment(TrackSegment segment);

}
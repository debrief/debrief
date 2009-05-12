package Debrief.ReaderWriter.XML.Tactical;

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

import org.w3c.dom.Element;
import org.xml.sax.Attributes;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.Track.TrackSegment;
import MWC.GUI.Editable;
import MWC.GUI.Plottable;

abstract public class TrackSegmentHandler extends
		MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{
	private static final String TRACK_SEGMENT = "TrackSegment";
	public static final String PLOT_RELATIVE = "PlotRelative";
	public static final String VISIBLE = "Visible";
	public static final String NAME = "Name";

	private TrackSegment _segment;
	private boolean _relativeMode = false;
	private boolean _visible;
	private String _name;

	public TrackSegmentHandler()
	{
		// inform our parent what type of class we are
		super(TRACK_SEGMENT);

		addAttributeHandler(new HandleBooleanAttribute(PLOT_RELATIVE)
		{
			@Override
			public void setValue(String name, boolean val)
			{
				_relativeMode = val;
			}
		});
		addAttributeHandler(new HandleBooleanAttribute(VISIBLE)
		{
			@Override
			public void setValue(String name, boolean val)
			{
				_visible = val;
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
		
		addHandler(new FixHandler()
		{
			public void addPlottable(Plottable plottable)
			{
				_segment.addFix((FixWrapper) plottable);
			}
		});

	}

	// this is one of ours, so get on with it!
	protected final void handleOurselves(String name, Attributes attributes)
	{
		_segment = new TrackSegment();
		super.handleOurselves(name, attributes);
	}

	public final void elementClosed()
	{
		_segment.setPlotRelative(_relativeMode);
		_segment.setVisible(_visible);
		_segment.setName(_name);
		addSegment(_segment);
		_segment = null;
	}
	
	public static void exportThisSegment(org.w3c.dom.Document doc, Element trk,
			TrackSegment seg)
	{
		final Element segE = doc.createElement(TRACK_SEGMENT);

		// sort out the attributes
		segE.setAttribute(VISIBLE, writeThis(seg.getVisible()));
		segE.setAttribute(NAME, seg.getName());
		segE.setAttribute(PLOT_RELATIVE, writeThis(seg.getPlotRelative()));

		// insert the fixes
		final Collection<Editable> pts = seg.getData();
		for (final Iterator<Editable> iterator = pts.iterator(); iterator.hasNext();)
		{
			final FixWrapper fix = (FixWrapper) iterator.next();
			FixHandler.exportFix(fix, segE, doc);
		}

		trk.appendChild(segE);
	}

	abstract public void addSegment(TrackSegment segment);

}
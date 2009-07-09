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
import java.util.Vector;

import org.w3c.dom.Element;
import org.xml.sax.Attributes;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.Track.TrackSegment;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Plottable;
import MWC.GUI.Properties.LineStylePropertyEditor;

abstract public class CoreTrackSegmentHandler extends
		MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{
	public static final String VISIBLE = "Visible";
	public static final String NAME = "Name";
	public static final String LINE_STYLE = "LineStyle";

	private Vector<FixWrapper> _fixes;
	private boolean _visible;
	private String _name;
	private int _lineStyle = CanvasType.SOLID;

	public CoreTrackSegmentHandler(String name)
	{
		// inform our parent what type of class we are
		super(name);


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
				Integer iVal =(Integer) lp.getValue(); 
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
		
		addHandler(new FixHandler()
		{
			public void addPlottable(Plottable plottable)
			{
				_fixes.add((FixWrapper) plottable);
			}
		});

	}

	// this is one of ours, so get on with it!
	protected final void handleOurselves(String name, Attributes attributes)
	{
		_fixes = new Vector<FixWrapper>();
		super.handleOurselves(name, attributes);
	}

	abstract protected TrackSegment createTrack();
	
	public final void elementClosed()
	{
		TrackSegment segment = createTrack();
		segment.setVisible(_visible);
		segment.setName(_name);
		segment.setLineStyle(_lineStyle);
		// give it the fixes
		for (Iterator<FixWrapper> iterator = _fixes.iterator(); iterator.hasNext();)
		{
			FixWrapper fix = (FixWrapper) iterator.next();
			segment.addFixSilent(fix);
		}
		addSegment(segment);
		segment = null;
	}
	
	public static Element exportThisSegment(org.w3c.dom.Document doc,
			TrackSegment seg, String segmentName)
	{
		final Element segE = doc.createElement(segmentName);

		LineStylePropertyEditor ls = new LineStylePropertyEditor();
		ls.setValue(seg.getLineStyle());
		
		// sort out the attributes
		segE.setAttribute(VISIBLE, writeThis(seg.getVisible()));
		segE.setAttribute(NAME, seg.getName());
		segE.setAttribute(LINE_STYLE, ls.getAsText());

		// insert the fixes
		final Collection<Editable> pts = seg.getData();
		for (final Iterator<Editable> iterator = pts.iterator(); iterator.hasNext();)
		{
			final FixWrapper fix = (FixWrapper) iterator.next();
			FixHandler.exportFix(fix, segE, doc);
		}
		
		return segE;
	}

	abstract public void addSegment(TrackSegment segment);

}
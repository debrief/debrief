/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package Debrief.ReaderWriter.XML.Tactical;

import java.text.ParseException;
import java.util.Enumeration;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import Debrief.Wrappers.CompositeTrackWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.ICompositeTrackSegment;
import Debrief.Wrappers.Track.PlanningSegment;
import Debrief.Wrappers.Track.TrackSegment;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import MWC.GUI.Properties.TimeFrequencyPropertyEditor;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.Errors.Trace;
import MWC.Utilities.ReaderWriter.XML.Util.LocationHandler;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;

public class CompositeTrackHandler extends TrackHandler
{
	private static final String COMPOSITE_TRACK = "composite_track";
	private static final String ORIGIN = "Origin";
	private static final String START_TIME = "StartTime";
	private static final String SYMBOL_INTERVAL = "SymbolIntervalMillis";
	private static final String LABEL_INTERVAL = "LabelIntervalMillis";
	protected WorldLocation _origin;
	protected HiResDate _startTime;
	protected Integer _symInt;
	protected Integer _labInt;

	public CompositeTrackHandler(final Layers theLayers)
	{
		super(theLayers, COMPOSITE_TRACK);

		// add our own special handlers
		addHandler(new LocationHandler(ORIGIN)
		{

			@Override
			public void setLocation(final WorldLocation res)
			{
				_origin = res;
			}
		});

		addAttributeHandler(new HandleAttribute(START_TIME)
		{
			public void setValue(final String name, final String value)
			{
				try
        {
          _startTime = DebriefFormatDateTime.parseThis(value);
        }
        catch (ParseException e)
        {
          Trace.trace(e, "While parsing date");        
        }
			}
		});

		addAttributeHandler(new HandleAttribute(SYMBOL_INTERVAL)
		{
			public void setValue(final String name, final String value)
			{
				_symInt = Integer.parseInt(value);
			}
		});
		addAttributeHandler(new HandleAttribute(LABEL_INTERVAL)
		{
			public void setValue(final String name, final String value)
			{
				_labInt = Integer.parseInt(value);
			}
		});

	}

	@Override
	protected TrackWrapper getWrapper()
	{
		return new CompositeTrackWrapper(null, null);
	}

	@Override
	public void elementClosed()
	{
		// sort out the origin and start time
		final CompositeTrackWrapper comp = (CompositeTrackWrapper) super._myTrack;
		comp.setOrigin(_origin);
		comp.setStartDate(_startTime);

		// and let the parent do its bit
		super.elementClosed();

		// and trigger a recalculation
		comp.recalculate();

		// and do some resetting
		_origin = null;
		_startTime = null;
		_symInt = null;
		_labInt = null;
	}

	public static void exportTrack(final Debrief.Wrappers.TrackWrapper track,
			final org.w3c.dom.Element parent, final org.w3c.dom.Document doc)
	{
		final CompositeTrackWrapper comp = (CompositeTrackWrapper) track;
		final Element trk = doc.createElement(COMPOSITE_TRACK);
		parent.appendChild(trk);

		// now the child track elements
		exportTrackObject(track, trk, doc);
			
		// start off with the origin for the data
		LocationHandler.exportLocation(comp.getOrigin(), ORIGIN, trk, doc);

		// now the child composite_track elements
		exportCompositeTrackObject(track, trk, doc);
			
		// and the DTG
		trk.setAttribute(START_TIME, writeThis(comp.getStartDate()));

		// we also wish to store the symbol and label frequencies - they're more
		// effective in
		// planning charts
		long symInt = track.getSymbolFrequency().getMicros();
		long labInt = track.getLabelFrequency().getMicros();

		if (symInt != TimeFrequencyPropertyEditor.SHOW_ALL_FREQUENCY)
			symInt /= 1000;
		if (labInt != TimeFrequencyPropertyEditor.SHOW_ALL_FREQUENCY)
			labInt /= 1000;

		trk.setAttribute(SYMBOL_INTERVAL, writeThis(symInt));
		trk.setAttribute(LABEL_INTERVAL, writeThis(labInt));
	}

	private static void exportCompositeTrackObject(TrackWrapper track,
		Element trk, Document doc)
	{
		final Enumeration<Editable> allItems = track.elements();
		while (allItems.hasMoreElements())
		{
			final Editable next = allItems.nextElement();
			if (next instanceof TrackSegment && next instanceof ICompositeTrackSegment)
			{
				exportThisCompositeTrackSegment(doc, trk, (TrackSegment) next);
			}
		}
	}

	private static void exportThisCompositeTrackSegment(final org.w3c.dom.Document doc,
			final Element trk, final TrackSegment segment)
	{
		if (segment instanceof PlanningSegment.ClosingSegment)
		{
			PlanningSegmentHandler.exportThisClosingSegment(doc, trk,
					(PlanningSegment) segment);
		}
		else if (segment instanceof PlanningSegment)
		{
			PlanningSegmentHandler.exportThisSegment(doc, trk,
					(PlanningSegment) segment);
		}
	}

}

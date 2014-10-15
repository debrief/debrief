/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package Debrief.ReaderWriter.XML.Tactical;

import org.w3c.dom.Element;

import Debrief.Wrappers.CompositeTrackWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Layers;
import MWC.GUI.Properties.TimeFrequencyPropertyEditor;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
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
				_startTime = DebriefFormatDateTime.parseThis(value);
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

		// and the symbol intervals
		if (_symInt != null)
		{
			if (_symInt == -1)
				comp.setSymbolFrequency(new HiResDate(0, _symInt));
			else
				comp.setSymbolFrequency(new HiResDate(_symInt));
		}

		// and the symbol intervals
		if (_labInt != null)
		{
			if (_labInt == -1)
				comp.setLabelFrequency(new HiResDate(0, _labInt));
			else
				comp.setLabelFrequency(new HiResDate(_labInt));
		}

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
		exportTrackObject(track, trk, doc);

		// we also need to send the DTG & origin
		LocationHandler.exportLocation(comp.getOrigin(), ORIGIN, trk, doc);
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

}

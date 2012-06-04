package Debrief.ReaderWriter.XML.Tactical;

import org.w3c.dom.Element;

import Debrief.Wrappers.CompositeTrackWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Layers;
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
	protected int _symInt;
	protected int _labInt;

	public CompositeTrackHandler(Layers theLayers)
	{
		super(theLayers, COMPOSITE_TRACK);

		// add our own special handlers
		addHandler(new LocationHandler(ORIGIN)
		{

			@Override
			public void setLocation(WorldLocation res)
			{
				_origin = res;
			}
		});

		addAttributeHandler(new HandleAttribute(START_TIME)
		{
			public void setValue(String name, String value)
			{
				_startTime = DebriefFormatDateTime.parseThis(value);
			}
		});

		addAttributeHandler(new HandleAttribute(SYMBOL_INTERVAL)
		{
			public void setValue(String name, String value)
			{
				_symInt = Integer.parseInt(value);
			}
		});
		addAttributeHandler(new HandleAttribute(LABEL_INTERVAL)
		{
			public void setValue(String name, String value)
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
		CompositeTrackWrapper comp = (CompositeTrackWrapper) super._myTrack;
		comp.setOrigin(_origin);
		comp.setStartDate(_startTime);

		// and the symbol intervals
		if (_symInt != -1)
		{
			comp.setSymbolFrequency(new HiResDate(_symInt));
		}

		// and the symbol intervals
		if (_labInt != -1)
		{
			comp.setLabelFrequency(new HiResDate(_labInt));
		}

		// and let the parent do its bit
		super.elementClosed();

		// and trigger a recalculation
		comp.recalculate();

		// and do some resetting
		_origin = null;
		_startTime = null;
		_symInt = -1;
		_labInt = -1;
	}

	public static void exportTrack(Debrief.Wrappers.TrackWrapper track,
			org.w3c.dom.Element parent, org.w3c.dom.Document doc)
	{
		CompositeTrackWrapper comp = (CompositeTrackWrapper) track;
		final Element trk = doc.createElement(COMPOSITE_TRACK);
		parent.appendChild(trk);
		exportTrackObject(track, trk, doc);

		// we also need to send the DTG & origin
		LocationHandler.exportLocation(comp.getOrigin(), ORIGIN, trk, doc);
		trk.setAttribute(START_TIME, writeThis(comp.getStartDate()));

		// we also wish to store the symbol and label frequencies - they're more
		// effective in
		// planning charts
		HiResDate symInt = track.getSymbolFrequency();
		HiResDate labInt = track.getLabelFrequency();

		trk.setAttribute(SYMBOL_INTERVAL, writeThis(symInt.getDate().getTime()));
		trk.setAttribute(LABEL_INTERVAL, writeThis(labInt.getDate().getTime()));
	}

}

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
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;

import org.w3c.dom.Element;
import org.xml.sax.Attributes;

import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TMAWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.AbsoluteTMASegment;
import Debrief.Wrappers.Track.PlanningSegment;
import Debrief.Wrappers.Track.RelativeTMASegment;
import Debrief.Wrappers.Track.TrackSegment;
import Debrief.Wrappers.Track.TrackWrapper_Support.SegmentList;
import MWC.GUI.Editable;
import MWC.GenericData.WorldDistance;
import MWC.Utilities.ReaderWriter.XML.Util.ColourHandler;
import MWC.Utilities.ReaderWriter.XML.Util.FontHandler;
import MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler;

public class TrackHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{

	private static final String TRACK = "track";
	private static final String SYMBOL_COLOR = "SymbolColor";
	private static final String SYMBOL_WIDTH = "SYMBOL_WIDTH";
	private static final String SYMBOL_LENGTH = "SYMBOL_LENGTH";
	private static final String SOLUTIONS_VISIBLE = "SolutionsVisible";
	private static final String SENSORS_VISIBLE = "SensorsVisible";
	private static final String PLOT_ARRAY_CENTRE = "PlotArrayCentre";
	private static final String LINK_POSITIONS = "LinkPositions";
	private static final String LINE_THICKNESS = "LineThickness";
	private static final String LINE_STYLE = "LineStyle";
	private static final String INTERPOLATE_POINTS = "InterpolatePoints";

	final MWC.GUI.Layers _theLayers;

	// private MWC.GUI.Layer _myLayer;

	// our "working" track
	Debrief.Wrappers.TrackWrapper _myTrack;
	protected WorldDistance _symWidth;
	protected WorldDistance _symLength;
	protected Color _symCol;

	/**
	 * class which contains list of textual representations of label locations
	 */
	static final MWC.GUI.Properties.LocationPropertyEditor lp = new MWC.GUI.Properties.LocationPropertyEditor();

	public static void exportTrack(Debrief.Wrappers.TrackWrapper track,
			org.w3c.dom.Element parent, org.w3c.dom.Document doc)
	{
		final Element trk = doc.createElement(TRACK);
		parent.appendChild(trk);
		exportTrackObject(track, trk, doc);
	}

	protected static void exportTrackObject(Debrief.Wrappers.TrackWrapper track,
			org.w3c.dom.Element trk, org.w3c.dom.Document doc)
	{

		/*
		 * <!ELEMENT track (colour,((fix|contact)*))> <!ATTLIST track name CDATA
		 * #REQUIRED visible (TRUE|FALSE) "TRUE" PositionsLinked (TRUE|FALSE) "TRUE"
		 * NameVisible (TRUE|FALSE) "TRUE" PositionsVisible (TRUE|FALSE) "TRUE"
		 * NameAtStart (TRUE|FALSE) "TRUE" NameLocation
		 * (Top|Left|Bottom|Centre|Right) "Right" Symbol CDATA "SQUARE" >
		 */

		trk.setAttribute("Name", toXML(track.getName()));
		trk.setAttribute("Visible", writeThis(track.getVisible()));
		trk.setAttribute("PositionsVisible", writeThis(track.getPositionsVisible()));
		trk.setAttribute("NameVisible", writeThis(track.getNameVisible()));
		trk.setAttribute("NameAtStart", writeThis(track.getNameAtStart()));
		trk.setAttribute(LINE_THICKNESS, writeThis(track.getLineThickness()));
		trk.setAttribute(LINE_STYLE, writeThis(track.getLineStyle()));
		trk.setAttribute(PLOT_ARRAY_CENTRE, writeThis(track.getPlotArrayCentre()));
		trk.setAttribute(INTERPOLATE_POINTS,
				writeThis(track.getInterpolatePoints()));
		trk.setAttribute(LINK_POSITIONS, writeThis(track.getLinkPositions()));
		lp.setValue(track.getNameLocation());
		trk.setAttribute("NameLocation", lp.getAsText());
		trk.setAttribute("Symbol", track.getSymbolType());

		// whether the sensor/solution layers should be visible
		trk.setAttribute(SENSORS_VISIBLE,
				writeThis(track.getSensors().getVisible()));
		trk.setAttribute(SOLUTIONS_VISIBLE, writeThis(track.getSolutions()
				.getVisible()));

		ColourHandler.exportColour(track.getColor(), trk, doc);
		ColourHandler.exportColour(track.getSymbolColor(), trk, doc, SYMBOL_COLOR);

		// and the font
		final java.awt.Font theFont = track.getTrackFont();
		if (theFont != null)
		{
			FontHandler.exportFont(theFont, trk, doc);
		}

		// and the symbol
		if (track.getSymbolLength() != null)
			WorldDistanceHandler.exportDistance(SYMBOL_LENGTH,
					track.getSymbolLength(), trk, doc);
		if (track.getSymbolWidth() != null)
			WorldDistanceHandler.exportDistance(SYMBOL_WIDTH, track.getSymbolWidth(),
					trk, doc);

		// first output any sensor data
		final Enumeration<Editable> sensors = track.getSensors().elements();

		// check if there is any data!
		if (sensors != null)
		{
			while (sensors.hasMoreElements())
			{
				final Debrief.Wrappers.SensorWrapper thisS = (SensorWrapper) sensors
						.nextElement();
				SensorHandler.exportSensor(thisS, trk, doc);
			}
		}

		// first output any sensor data
		final Enumeration<Editable> solutions = track.getSolutions().elements();

		// check if there is any data!
		if (solutions != null)
		{
			while (solutions.hasMoreElements())
			{
				final Debrief.Wrappers.TMAWrapper thisS = (TMAWrapper) solutions
						.nextElement();
				TMAHandler.exportSolutionTrack(thisS, trk, doc);
			}
		}

		Enumeration<Editable> allItems = track.elements();
		while (allItems.hasMoreElements())
		{
			Editable next = allItems.nextElement();
			if (next instanceof SegmentList)
			{
				final SegmentList list = (SegmentList) next;
				Element sList = doc.createElement(SegmentListHandler.SEGMENT_LIST);
				final Collection<Editable> items = list.getData();
				for (final Iterator<Editable> iterator = items.iterator(); iterator
						.hasNext();)
				{
					final TrackSegment editable = (TrackSegment) iterator.next();
					exportThisTrackSegment(doc, trk, editable);
				}
				trk.appendChild(sList);
				break;
			}
			else if (next instanceof TrackSegment)
			{
				exportThisTrackSegment(doc, trk, (TrackSegment) next);
				break;
			}
		}
	}

	private static void exportThisTrackSegment(org.w3c.dom.Document doc,
			final Element trk, TrackSegment segment)
	{
		// right, sort out what type it is
		if (segment instanceof RelativeTMASegment)
		{
			RelativeTMASegmentHandler.exportThisTMASegment(doc, trk,
					(RelativeTMASegment) segment);
		}
		else if (segment instanceof AbsoluteTMASegment)
		{
			AbsoluteTMASegmentHandler.exportThisTMASegment(doc, trk,
					(AbsoluteTMASegment) segment);
		}
		else if (segment instanceof PlanningSegment)
		{
			PlanningSegmentHandler.exportThisSegment(doc, trk,
					(PlanningSegment) segment);
		}
		else

			TrackSegmentHandler.exportThisSegment(doc, trk, (TrackSegment) segment);
	}

	public TrackHandler(MWC.GUI.Layers theLayers)
	{
		this(theLayers, TRACK);
	}

	protected TrackHandler(MWC.GUI.Layers theLayers, String name)
	{
		// inform our parent what type of class we are
		super(name);

		// store the layers object, so that we can add ourselves to it
		_theLayers = theLayers;

		addHandler(new SensorHandler()
		{
			@Override
			public void addSensor(Debrief.Wrappers.SensorWrapper sensor)
			{
				addThis(sensor);
			}
		});

		addHandler(new SegmentListHandler(_theLayers)
		{
			@Override
			public void addThisSegment(TrackSegment list)
			{
				addThis(list);
			}
		});

		addHandler(new RelativeTMASegmentHandler(_theLayers)
		{
			public void addSegment(TrackSegment segment)
			{
				addThis(segment);
			}
		});

		addHandler(new PlanningSegmentHandler()
		{
			public void addSegment(TrackSegment segment)
			{
				addThis(segment);
			}
		});

		addHandler(new AbsoluteTMASegmentHandler()
		{
			public void addSegment(TrackSegment segment)
			{
				addThis(segment);
			}
		});

		addHandler(new TrackSegmentHandler()
		{
			@Override
			public void addSegment(TrackSegment list)
			{
				addThis(list);
			}
		});

		addHandler(new TMAHandler()
		{
			@Override
			public void addContact(TMAWrapper data)
			{
				addThis(data);
			}
		});

		addHandler(new ColourHandler()
		{
			@Override
			public void setColour(java.awt.Color res)
			{
				// nope, give it the track color
				_myTrack.setColor(res);
				// has the symbol been set?
				if (_symCol == null)
				{
					_myTrack.setSymbolColor(res);
				}
			}
		});

		addHandler(new ColourHandler(SYMBOL_COLOR)
		{
			@Override
			public void setColour(java.awt.Color res)
			{
				_symCol = res;
				_myTrack.setSymbolColor(res);
			}
		});

		addHandler(new FontHandler()
		{
			@Override
			public void setFont(java.awt.Font font)
			{
				_myTrack.setTrackFont(font);
			}
		});

		addHandler(new FixHandler()
		{
			@Override
			public void addPlottable(MWC.GUI.Plottable fix)
			{
				addThis(fix);
			}
		});
		addAttributeHandler(new HandleAttribute("Name")
		{
			@Override
			public void setValue(String name, String val)
			{
				_myTrack.setName(fromXML(val));
			}
		});
		addAttributeHandler(new HandleBooleanAttribute("Visible")
		{
			@Override
			public void setValue(String name, boolean val)
			{
				_myTrack.setVisible(val);
			}
		});
		addAttributeHandler(new HandleBooleanAttribute(SENSORS_VISIBLE)
		{
			@Override
			public void setValue(String name, boolean val)
			{
				_myTrack.getSensors().setVisible(val);
			}
		});
		addAttributeHandler(new HandleBooleanAttribute(SOLUTIONS_VISIBLE)
		{
			@Override
			public void setValue(String name, boolean val)
			{
				_myTrack.getSolutions().setVisible(val);
			}
		});
		addAttributeHandler(new HandleBooleanAttribute("PositionsVisible")
		{
			@Override
			public void setValue(String name, boolean val)
			{
				_myTrack.setPositionsVisible(val);
			}
		});
		addAttributeHandler(new HandleBooleanAttribute(LINK_POSITIONS)
		{
			@Override
			public void setValue(String name, boolean val)
			{
				_myTrack.setLinkPositions(val);
			}
		});
		addAttributeHandler(new HandleBooleanAttribute(PLOT_ARRAY_CENTRE)
		{
			@Override
			public void setValue(String name, boolean val)
			{
				_myTrack.setPlotArrayCentre(val);
			}
		});
		addAttributeHandler(new HandleBooleanAttribute("NameVisible")
		{
			@Override
			public void setValue(String name, boolean val)
			{
				_myTrack.setNameVisible(val);
			}
		});
		addAttributeHandler(new HandleBooleanAttribute("NameAtStart")
		{
			@Override
			public void setValue(String name, boolean val)
			{
				_myTrack.setNameAtStart(val);
			}
		});
		addAttributeHandler(new HandleBooleanAttribute(INTERPOLATE_POINTS)
		{
			@Override
			public void setValue(String name, boolean val)
			{
				_myTrack.setInterpolatePoints(val);
			}
		});
		addAttributeHandler(new HandleAttribute("NameLocation")
		{
			@Override
			public void setValue(String name, String val)
			{
				lp.setAsText(val);
				_myTrack.setNameLocation((Integer) lp.getValue());
			}
		});
		addAttributeHandler(new HandleAttribute("Symbol")
		{
			@Override
			public void setValue(String name, String value)
			{
				_myTrack.setSymbolType(value);
			}
		});
		addAttributeHandler(new HandleIntegerAttribute(LINE_STYLE)
		{
			@Override
			public void setValue(String name, int value)
			{
				_myTrack.setLineStyle(value);
			}
		});

		addAttributeHandler(new HandleIntegerAttribute(LINE_THICKNESS)
		{
			@Override
			public void setValue(String name, int value)
			{
				_myTrack.setLineThickness(value);
			}
		});

		addHandler(new WorldDistanceHandler(SYMBOL_WIDTH)
		{
			@Override
			public void setWorldDistance(WorldDistance res)
			{
				_symWidth = res;
			}
		});

		addHandler(new WorldDistanceHandler(SYMBOL_LENGTH)
		{
			@Override
			public void setWorldDistance(WorldDistance res)
			{
				_symLength = res;
			}
		});

	}

	void addThis(MWC.GUI.Plottable val)
	{
		_myTrack.add(val);
	}

	@Override
	public void elementClosed()
	{
		// ok, the symbol should be sorted, do the lengths
		if (_symLength != null)
			_myTrack.setSymbolLength(_symLength);
		if (_symWidth != null)
			_myTrack.setSymbolWidth(_symWidth);

		// our layer is complete, add it to the parent!
		_theLayers.addThisLayer(_myTrack);

		_myTrack = null;
		_symWidth = null;
		_symLength = null;
		_symCol = null;
	}

	// this is one of ours, so get on with it!
	@Override
	protected final void handleOurselves(String name, Attributes attributes)
	{
		// create the wrapper
		_myTrack = getWrapper();

		// marry them together
		super.handleOurselves(name, attributes);
	}

	protected TrackWrapper getWrapper()
	{
		return new TrackWrapper();
	}

}
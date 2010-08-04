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
import java.util.Enumeration;
import java.util.Iterator;

import org.w3c.dom.Element;
import org.xml.sax.Attributes;

import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TMAWrapper;
import Debrief.Wrappers.Track.AbsoluteTMASegment;
import Debrief.Wrappers.Track.RelativeTMASegment;
import Debrief.Wrappers.Track.TrackSegment;
import Debrief.Wrappers.Track.TrackWrapper_Support.SegmentList;
import MWC.GUI.Editable;
import MWC.Utilities.ReaderWriter.XML.Util.ColourHandler;
import MWC.Utilities.ReaderWriter.XML.Util.FontHandler;

public final class TrackHandler extends
		MWC.Utilities.ReaderWriter.XML.MWCXMLReader {

	private static final String PLOT_ARRAY_CENTRE = "PlotArrayCentre";
	private static final String LINK_POSITIONS = "LinkPositions";
	private static final String LINE_THICKNESS = "LineThickness";
	private static final String INTERPOLATE_POINTS = "InterpolatePoints";

	final MWC.GUI.Layers _theLayers;

	// private MWC.GUI.Layer _myLayer;

	// our "working" track
	Debrief.Wrappers.TrackWrapper _myTrack;

	/**
	 * class which contains list of textual representations of label locations
	 */
	static final MWC.GUI.Properties.LocationPropertyEditor lp = new MWC.GUI.Properties.LocationPropertyEditor();

	public static void exportTrack(Debrief.Wrappers.TrackWrapper track,
			org.w3c.dom.Element parent, org.w3c.dom.Document doc) {

		/*
		 * <!ELEMENT track (colour,((fix|contact)*))> <!ATTLIST track name CDATA
		 * #REQUIRED visible (TRUE|FALSE) "TRUE" PositionsLinked (TRUE|FALSE)
		 * "TRUE" NameVisible (TRUE|FALSE) "TRUE" PositionsVisible (TRUE|FALSE)
		 * "TRUE" NameAtStart (TRUE|FALSE) "TRUE" NameLocation
		 * (Top|Left|Bottom|Centre|Right) "Right" Symbol CDATA "SQUARE" >
		 */

		final Element trk = doc.createElement("track");
		trk.setAttribute("Name", toXML(track.getName()));
		trk.setAttribute("Visible", writeThis(track.getVisible()));
		trk.setAttribute("PositionsVisible", writeThis(track
				.getPositionsVisible()));
		trk.setAttribute("NameVisible", writeThis(track.getNameVisible()));
		trk.setAttribute("NameAtStart", writeThis(track.getNameAtStart()));
		trk.setAttribute(LINE_THICKNESS, writeThis(track.getLineThickness()));
		trk.setAttribute(PLOT_ARRAY_CENTRE, writeThis(track.getPlotArrayCentre()));
		trk.setAttribute(INTERPOLATE_POINTS, writeThis(track
				.getInterpolatePoints()));
		trk.setAttribute(LINK_POSITIONS, writeThis(track.getLinkPositions()));
		lp.setValue(track.getNameLocation());
		trk.setAttribute("NameLocation", lp.getAsText());
		trk.setAttribute("Symbol", track.getSymbolType());
		ColourHandler.exportColour(track.getColor(), trk, doc);

		// and the font
		final java.awt.Font theFont = track.getTrackFont();
		if (theFont != null) {
			FontHandler.exportFont(theFont, trk, doc);
		}

		// first output any sensor data
		final java.util.Enumeration<SensorWrapper> sensors = track.getSensors();

		// check if there is any data!
		if (sensors != null) {
			while (sensors.hasMoreElements()) {
				final Debrief.Wrappers.SensorWrapper thisS = sensors
						.nextElement();
				SensorHandler.exportSensor(thisS, trk, doc);
			}
		}

		// first output any sensor data
		final java.util.Enumeration<TMAWrapper> solutions = track
				.getSolutions();

		// check if there is any data!
		if (solutions != null) {
			while (solutions.hasMoreElements()) {
				final Debrief.Wrappers.TMAWrapper thisS = solutions
						.nextElement();
				TMAHandler.exportSolutionTrack(thisS, trk, doc);
			}
		}

		Enumeration<Editable> allItems = track.elements();
		while (allItems.hasMoreElements()) {
			Editable next = allItems.nextElement();
			if (next instanceof SegmentList) {
				final SegmentList list = (SegmentList) next;
				Element sList = doc
						.createElement(SegmentListHandler.SEGMENT_LIST);
				final Collection<Editable> items = list.getData();
				for (final Iterator<Editable> iterator = items.iterator(); iterator
						.hasNext();) {
					final TrackSegment editable = (TrackSegment) iterator
							.next();
					exportThisTrackSegment(doc, trk, editable);
				}
				trk.appendChild(sList);
				break;
			} else if (next instanceof TrackSegment) {
				exportThisTrackSegment(doc, trk, (TrackSegment) next);
				break;
			}
		}

		parent.appendChild(trk);
	}

	private static void exportThisTrackSegment(org.w3c.dom.Document doc,
			final Element trk, TrackSegment segment) {
		// right, sort out what type it is
		if (segment instanceof RelativeTMASegment) {
			RelativeTMASegmentHandler.exportThisTMASegment(doc, trk,
					(RelativeTMASegment) segment);
		} else if (segment instanceof AbsoluteTMASegment) {
			AbsoluteTMASegmentHandler.exportThisTMASegment(doc, trk,
					(AbsoluteTMASegment) segment);
		}
		else

			TrackSegmentHandler.exportThisSegment(doc, trk,
					(TrackSegment) segment);
	}

	public TrackHandler(MWC.GUI.Layers theLayers) {
		// inform our parent what type of class we are
		super("track");

		// store the layers object, so that we can add ourselves to it
		_theLayers = theLayers;

		addHandler(new SensorHandler() {
			@Override
			public void addSensor(Debrief.Wrappers.SensorWrapper sensor) {
				addThis(sensor);
			}
		});

		addHandler(new SegmentListHandler(_theLayers) {
			@Override
			public void addThisSegment(TrackSegment list) {
				addThis(list);
			}
		});

		addHandler(new RelativeTMASegmentHandler(_theLayers) {
			public void addSegment(TrackSegment segment) {
				addThis(segment);
			}
		});

		addHandler(new AbsoluteTMASegmentHandler(_theLayers) {
			public void addSegment(TrackSegment segment) {
				addThis(segment);
			}
		});

		addHandler(new TrackSegmentHandler() {
			@Override
			public void addSegment(TrackSegment list) {
				addThis(list);
			}
		});

		addHandler(new TMAHandler() {
			@Override
			public void addContact(TMAWrapper data) {
				addThis(data);
			}
		});

		addHandler(new ColourHandler() {
			@Override
			public void setColour(java.awt.Color res) {
				_myTrack.setColor(res);
			}
		});

		addHandler(new FontHandler() {
			@Override
			public void setFont(java.awt.Font font) {
				_myTrack.setTrackFont(font);
			}
		});

		addHandler(new FixHandler() {
			@Override
			public void addPlottable(MWC.GUI.Plottable fix) {
				addThis(fix);
			}
		});
		addAttributeHandler(new HandleAttribute("Name") {
			@Override
			public void setValue(String name, String val) {
				_myTrack.setName(fromXML(val));
			}
		});
		addAttributeHandler(new HandleBooleanAttribute("Visible") {
			@Override
			public void setValue(String name, boolean val) {
				_myTrack.setVisible(val);
			}
		});
		addAttributeHandler(new HandleBooleanAttribute("PositionsVisible") {
			@Override
			public void setValue(String name, boolean val) {
				_myTrack.setPositionsVisible(val);
			}
		});
		addAttributeHandler(new HandleBooleanAttribute(LINK_POSITIONS) {
			@Override
			public void setValue(String name, boolean val) {
				_myTrack.setLinkPositions(val);
			}
		});
		addAttributeHandler(new HandleBooleanAttribute(PLOT_ARRAY_CENTRE) {
			@Override
			public void setValue(String name, boolean val) {
				_myTrack.setPlotArrayCentre(val);
			}
		});
		addAttributeHandler(new HandleBooleanAttribute("NameVisible") {
			@Override
			public void setValue(String name, boolean val) {
				_myTrack.setNameVisible(val);
			}
		});
		addAttributeHandler(new HandleBooleanAttribute("NameAtStart") {
			@Override
			public void setValue(String name, boolean val) {
				_myTrack.setNameAtStart(val);
			}
		});
		addAttributeHandler(new HandleBooleanAttribute(INTERPOLATE_POINTS) {
			@Override
			public void setValue(String name, boolean val) {
				_myTrack.setInterpolatePoints(val);
			}
		});
		addAttributeHandler(new HandleAttribute("NameLocation") {
			@Override
			public void setValue(String name, String val) {
				lp.setAsText(val);
				_myTrack.setNameLocation((Integer) lp.getValue());
			}
		});
		addAttributeHandler(new HandleAttribute("Symbol") {
			@Override
			public void setValue(String name, String value) {
				_myTrack.setSymbolType(value);
			}
		});
		addAttributeHandler(new HandleIntegerAttribute(LINE_THICKNESS) {
			@Override
			public void setValue(String name, int value) {
				_myTrack.setLineThickness(value);
			}
		});

	}

	void addThis(MWC.GUI.Plottable val) {
		_myTrack.add(val);
	}

	@Override
	public final void elementClosed() {
		// our layer is complete, add it to the parent!
		_theLayers.addThisLayer(_myTrack);

		_myTrack = null;
	}

	// this is one of ours, so get on with it!
	@Override
	protected final void handleOurselves(String name, Attributes attributes) {
		// create the wrapper
		_myTrack = new Debrief.Wrappers.TrackWrapper();

		// marry them together

		super.handleOurselves(name, attributes);

	}

}
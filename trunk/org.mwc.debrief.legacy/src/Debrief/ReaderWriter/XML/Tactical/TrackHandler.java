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
import org.xml.sax.Attributes;

import Debrief.Wrappers.TMAWrapper;
import MWC.Utilities.ReaderWriter.XML.Util.*;

public final class TrackHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{

	private static final String LINE_THICKNESS = "LineThickness";

	private static final String INTERPOLATE_POINTS = "InterpolatePoints";

	private final MWC.GUI.Layers _theLayers;

	// private MWC.GUI.Layer _myLayer;

	// our "working" track
	private Debrief.Wrappers.TrackWrapper _myTrack;

	private MWC.TacticalData.Track _theTrack;

	/**
	 * class which contains list of textual representations of label locations
	 */
	static private final MWC.GUI.Properties.LocationPropertyEditor lp = new MWC.GUI.Properties.LocationPropertyEditor();

	public TrackHandler(MWC.GUI.Layers theLayers)
	{
		// inform our parent what type of class we are
		super("track");

		// store the layers object, so that we can add ourselves to it
		_theLayers = theLayers;

		addHandler(new SensorHandler()
		{
			public void addSensor(Debrief.Wrappers.SensorWrapper sensor)
			{
				addThis(sensor);
			}
		});

		addHandler(new TMAHandler()
		{
			public void addContact(TMAWrapper data)
			{
				addThis(data);
			}
		});

		addHandler(new ColourHandler()
		{
			public void setColour(java.awt.Color res)
			{
				_myTrack.setColor(res);
			}
		});

		addHandler(new FontHandler()
		{
			public void setFont(java.awt.Font font)
			{
				_myTrack.setTrackFont(font);
			}
		});

		addHandler(new FixHandler()
		{
			public void addPlottable(MWC.GUI.Plottable fix)
			{
				addThis(fix);
			}
		});
		addAttributeHandler(new HandleAttribute("Name")
		{
			public void setValue(String name, String val)
			{
				_myTrack.setName(fromXML(val));
			}
		});
		addAttributeHandler(new HandleBooleanAttribute("Visible")
		{
			public void setValue(String name, boolean val)
			{
				_myTrack.setVisible(val);
			}
		});
		addAttributeHandler(new HandleBooleanAttribute("PositionsLinked")
		{
			public void setValue(String name, boolean val)
			{
				_myTrack.setPositionsLinked(val);
			}
		});
		addAttributeHandler(new HandleBooleanAttribute("PositionsVisible")
		{
			public void setValue(String name, boolean val)
			{
				_myTrack.setPositionsVisible(val);
			}
		});
		addAttributeHandler(new HandleBooleanAttribute("NameVisible")
		{
			public void setValue(String name, boolean val)
			{
				_myTrack.setNameVisible(val);
			}
		});
		addAttributeHandler(new HandleBooleanAttribute("NameAtStart")
		{
			public void setValue(String name, boolean val)
			{
				_myTrack.setNameAtStart(val);
			}
		});
		addAttributeHandler(new HandleBooleanAttribute(INTERPOLATE_POINTS)
		{
			public void setValue(String name, boolean val)
			{
				_myTrack.setInterpolatePoints(val);
			}
		});
		addAttributeHandler(new HandleAttribute("NameLocation")
		{
			public void setValue(String name, String val)
			{
				lp.setAsText(val);
				_myTrack.setNameLocation((Integer) lp.getValue());
			}
		});
		addAttributeHandler(new HandleAttribute("Symbol")
		{
			public void setValue(String name, String value)
			{
				_myTrack.setSymbolType(value);
			}
		});
		addAttributeHandler(new HandleIntegerAttribute(LINE_THICKNESS)
		{
			public void setValue(String name, int value)
			{
				_myTrack.setLineThickness(value);
			}
		});

	}

	// this is one of ours, so get on with it!
	protected final void handleOurselves(String name, Attributes attributes)
	{
		// create the track data item
		_theTrack = new MWC.TacticalData.Track();

		// create the wrapper
		_myTrack = new Debrief.Wrappers.TrackWrapper();

		// marry them together
		_myTrack.setTrack(_theTrack);

		super.handleOurselves(name, attributes);

	}

	private void addThis(MWC.GUI.Plottable val)
	{
		_myTrack.add(val);
	}

	public final void elementClosed()
	{
		// our layer is complete, add it to the parent!
		_theLayers.addThisLayer(_myTrack);

		_myTrack = null;
	}

	public static void exportTrack(Debrief.Wrappers.TrackWrapper track,
			org.w3c.dom.Element parent, org.w3c.dom.Document doc)
	{

		/*
		 * <!ELEMENT track (colour,((fix|contact)*))> <!ATTLIST track name CDATA
		 * #REQUIRED visible (TRUE|FALSE) "TRUE" PositionsLinked (TRUE|FALSE) "TRUE"
		 * NameVisible (TRUE|FALSE) "TRUE" PositionsVisible (TRUE|FALSE) "TRUE"
		 * NameAtStart (TRUE|FALSE) "TRUE" NameLocation
		 * (Top|Left|Bottom|Centre|Right) "Right" Symbol CDATA "SQUARE" >
		 */

		Element trk = doc.createElement("track");
		trk.setAttribute("Name", toXML(track.getName()));
		trk.setAttribute("Visible", writeThis(track.getVisible()));
		trk.setAttribute("PositionsLinked", writeThis(track.getPositionsLinked()));
		trk.setAttribute("PositionsVisible", writeThis(track.getPositionsVisible()));
		trk.setAttribute("NameVisible", writeThis(track.getNameVisible()));
		trk.setAttribute("NameAtStart", writeThis(track.getNameAtStart()));
		trk.setAttribute(LINE_THICKNESS, writeThis(track.getLineThickness()));
		trk.setAttribute(INTERPOLATE_POINTS, writeThis(track.getInterpolatePoints()));
		lp.setValue(track.getNameLocation());
		trk.setAttribute("NameLocation", lp.getAsText());
		trk.setAttribute("Symbol", track.getSymbolType());
		ColourHandler.exportColour(track.getColor(), trk, doc);

		// and the font
		java.awt.Font theFont = track.getTrackFont();
		if (theFont != null)
		{
			FontHandler.exportFont(theFont, trk, doc);
		}

		// first output any sensor data
		java.util.Enumeration sensors = track.getSensors();

		// check if there is any data!
		if (sensors != null)
		{
			while (sensors.hasMoreElements())
			{
				Debrief.Wrappers.SensorWrapper thisS = (Debrief.Wrappers.SensorWrapper) sensors
						.nextElement();
				SensorHandler.exportSensor(thisS, trk, doc);
			}
		}

		// first output any sensor data
		java.util.Enumeration solutions = track.getSolutions();

		// check if there is any data!
		if (solutions != null)
		{
			while (solutions.hasMoreElements())
			{
				Debrief.Wrappers.TMAWrapper thisS = (Debrief.Wrappers.TMAWrapper) solutions
						.nextElement();
				TMAHandler.exportSolutionTrack(thisS, trk, doc);
			}
		}

		// now the points
		java.util.Enumeration iter = track.getPositions();
		while (iter.hasMoreElements())
		{
			MWC.GUI.Plottable pl = (MWC.GUI.Plottable) iter.nextElement();
			// check that this isn't a naughty Sensor data item
			if (pl instanceof Debrief.Wrappers.FixWrapper)
			{
				Debrief.Wrappers.FixWrapper fw = (Debrief.Wrappers.FixWrapper) pl;
				FixHandler.exportFix(fw, trk, doc);
			}

		}
		parent.appendChild(trk);
	}

}
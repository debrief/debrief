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

import Debrief.Wrappers.SensorContactWrapper;
import MWC.GUI.Editable;
import MWC.GenericData.WorldDistance;
import MWC.Utilities.ReaderWriter.XML.Util.ColourHandler;
import MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler;

abstract public class SensorHandler extends
		MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{
	Debrief.Wrappers.SensorWrapper _mySensor;
	private static final String WORM_IN_HOLE = "WormInHole";

	private static final String OFFSET = "Offset";

	public SensorHandler()
	{
		// inform our parent what type of class we are
		super("sensor");

		addHandler(new ColourHandler()
		{
			public void setColour(java.awt.Color res)
			{
				_mySensor.setColor(res);
			}
		});

		addHandler(new SensorContactHandler()
		{
			public void addContact(MWC.GUI.Plottable contact)
			{
				addThisContact(contact);

				// and set the sensor for that contact
				SensorContactWrapper sc = (SensorContactWrapper) contact;
				sc.setSensor(_mySensor);

			}
		});
		addAttributeHandler(new HandleAttribute("Name")
		{
			public void setValue(String name, String val)
			{
				_mySensor.setName(fromXML(val));
			}
		});
		addAttributeHandler(new HandleAttribute("TrackName")
		{
			public void setValue(String name, String val)
			{
				_mySensor.setTrackName(val);
			}
		});
		addAttributeHandler(new HandleBooleanAttribute("Visible")
		{
			public void setValue(String name, boolean val)
			{
				_mySensor.setVisible(val);
			}
		});
		addAttributeHandler(new HandleIntegerAttribute("LineThickness")
		{
			public void setValue(String name, int val)
			{
				_mySensor.setLineThickness(val);
			}
		});

		addHandler(new WorldDistanceHandler(OFFSET)
		{
			public void setWorldDistance(WorldDistance value)
			{
				// just check it contains a credible value
				if (value.getValue() != 0)
					_mySensor.setSensorOffset(new WorldDistance.ArrayLength(value));
			}
		});

		addAttributeHandler(new HandleBooleanAttribute(WORM_IN_HOLE)
		{

			@Override
			public void setValue(String name, boolean value)
			{
				_mySensor.setWormInHole(value);
			}
		});

	}

	// this is one of ours, so get on with it!
	protected final void handleOurselves(String name, Attributes attributes)
	{
		_mySensor = new Debrief.Wrappers.SensorWrapper("");

		super.handleOurselves(name, attributes);

	}

	void addThisContact(MWC.GUI.Plottable val)
	{
		// store in our list
		_mySensor.add(val);
	}

	public final void elementClosed()
	{
		// our layer is complete, add it to the parent!
		addSensor(_mySensor);

		_mySensor = null;
	}

	abstract public void addSensor(Debrief.Wrappers.SensorWrapper data);

	public static void exportSensor(Debrief.Wrappers.SensorWrapper sensor,
			org.w3c.dom.Element parent, org.w3c.dom.Document doc)
	{

		/*
		 * <!ELEMENT sensor (colour,((fix|contact)*))> <!ATTLIST track name CDATA
		 * #REQUIRED visible (TRUE|FALSE) "TRUE" PositionsLinked (TRUE|FALSE) "TRUE"
		 * NameVisible (TRUE|FALSE) "TRUE" PositionsVisible (TRUE|FALSE) "TRUE"
		 * NameAtStart (TRUE|FALSE) "TRUE" NameLocation
		 * (Top|Left|Bottom|Centre|Right) "Right" Symbol CDATA "SQUARE" >
		 */
		Element trk = doc.createElement("sensor");
		trk.setAttribute("Name", toXML(sensor.getName()));
		trk.setAttribute("Visible", writeThis(sensor.getVisible()));
		trk.setAttribute("TrackName", sensor.getTrackName());
		trk.setAttribute("LineThickness", writeThis(sensor.getLineThickness()));
		ColourHandler.exportColour(sensor.getColor(), trk, doc);

		// do we have an offset?
		WorldDistance theOFfset = sensor.getSensorOffset();
		if (theOFfset != null)
		{
			// check it has a legitimate value
			if (theOFfset.getValue() != 0)
				MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler
						.exportDistance(OFFSET, theOFfset, trk, doc);
		}

		// and the worm in the hole indicator?
		Boolean wormy = sensor.getWormInHole();
		if (wormy != null)
		{
			trk.setAttribute(WORM_IN_HOLE, writeThis(wormy));
		}

		// now the points
		java.util.Enumeration<Editable> iter = sensor.elements();
		while (iter.hasMoreElements())
		{
			MWC.GUI.Plottable pl = (MWC.GUI.Plottable) iter.nextElement();
			if (pl instanceof Debrief.Wrappers.SensorContactWrapper)
			{
				Debrief.Wrappers.SensorContactWrapper fw = (Debrief.Wrappers.SensorContactWrapper) pl;
				SensorContactHandler.exportFix(fw, trk, doc);
			}

		}

		parent.appendChild(trk);
	}

}
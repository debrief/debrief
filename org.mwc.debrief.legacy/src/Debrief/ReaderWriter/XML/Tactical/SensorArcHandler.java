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

import org.w3c.dom.Element;
import org.xml.sax.Attributes;

import Debrief.Wrappers.SensorArcContactWrapper;
import MWC.GUI.Editable;

abstract public class SensorArcHandler extends
		MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{
	Debrief.Wrappers.SensorArcWrapper _sensorArcs;
			
	public SensorArcHandler()
	{
		// inform our parent what type of class we are
		super("sensorarc");

		addHandler(new SensorArcContactHandler()
		{
			public void addContact(final MWC.GUI.Plottable contact)
			{
				addThisContact(contact);

				// and set the sensor for that contact
				final SensorArcContactWrapper sc = (SensorArcContactWrapper) contact;
				sc.setSensor(_sensorArcs);

			}
		});
		addAttributeHandler(new HandleAttribute("Name")
		{
			public void setValue(final String name, final String val)
			{
				_sensorArcs.setName(fromXML(val));
			}
		});
		addAttributeHandler(new HandleBooleanAttribute("Visible")
		{
			public void setValue(final String name, final boolean val)
			{
				_sensorArcs.setVisible(val);
			}
		});
		addAttributeHandler(new HandleIntegerAttribute("LineThickness")
		{
			public void setValue(final String name, final int val)
			{
				_sensorArcs.setLineThickness(val);
			}
		});
	}

	// this is one of ours, so get on with it!
	protected final void handleOurselves(final String name, final Attributes attributes)
	{
		_sensorArcs = new Debrief.Wrappers.SensorArcWrapper("");

		super.handleOurselves(name, attributes);
	}

	void addThisContact(final MWC.GUI.Plottable val)
	{
		// store in our list
		_sensorArcs.add(val);
	}

	public final void elementClosed()
	{
		// our layer is complete, add it to the parent!
		addSensor(_sensorArcs);


		_sensorArcs = null;
	}

	abstract public void addSensor(Debrief.Wrappers.SensorArcWrapper data);

	public static void exportSensorArc(final Debrief.Wrappers.SensorArcWrapper sensor,
			final org.w3c.dom.Element parent, final org.w3c.dom.Document doc)
	{

		final Element trk = doc.createElement("sensorarc");
		trk.setAttribute("Name", toXML(sensor.getName()));
		trk.setAttribute("Visible", writeThis(sensor.getVisible()));
		trk.setAttribute("TrackName", sensor.getHost().getName());
		trk.setAttribute("LineThickness", writeThis(sensor.getLineThickness()));

		
		// now the points
		final java.util.Enumeration<Editable> iter = sensor.elements();
		while (iter.hasMoreElements())
		{
			final MWC.GUI.Plottable pl = (MWC.GUI.Plottable) iter.nextElement();
			if (pl instanceof Debrief.Wrappers.SensorArcContactWrapper)
			{
				final Debrief.Wrappers.SensorArcContactWrapper fw = (Debrief.Wrappers.SensorArcContactWrapper) pl;
				SensorArcContactHandler.exportFix(fw, trk, doc);
			}

		}

		parent.appendChild(trk);
	}

}
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

import Debrief.Wrappers.DynamicTrackShapes.DynamicTrackShapeWrapper;
import MWC.GUI.Editable;

abstract public class DynamicTrackShapeSetHandler extends
		MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{
	private static final String MY_TYPE = "DynamicShapeSet";
	Debrief.Wrappers.DynamicTrackShapes.DynamicTrackShapeSetWrapper _dynamicShapes;
			
	public DynamicTrackShapeSetHandler()
	{
		// inform our parent what type of class we are
		super(MY_TYPE);

		addHandler(new DynamicTrackCoverageHandler()
		{
			public void addContact(final MWC.GUI.Plottable contact)
			{
				addThisShape(contact);

				// and set the sensor for that contact
				final DynamicTrackShapeWrapper sc = (DynamicTrackShapeWrapper) contact;
				sc.setParent(_dynamicShapes);
			}
		});
		addAttributeHandler(new HandleAttribute("Name")
		{
			public void setValue(final String name, final String val)
			{
				_dynamicShapes.setName(fromXML(val));
			}
		});
		addAttributeHandler(new HandleBooleanAttribute("Visible")
		{
			public void setValue(final String name, final boolean val)
			{
				_dynamicShapes.setVisible(val);
			}
		});
		addAttributeHandler(new HandleIntegerAttribute("LineThickness")
		{
			public void setValue(final String name, final int val)
			{
				_dynamicShapes.setLineThickness(val);
			}
		});
	}

	// this is one of ours, so get on with it!
	protected final void handleOurselves(final String name, final Attributes attributes)
	{
		_dynamicShapes = new Debrief.Wrappers.DynamicTrackShapes.DynamicTrackShapeSetWrapper("");

		super.handleOurselves(name, attributes);
	}

	void addThisShape(final MWC.GUI.Plottable val)
	{
		// store in our list
		_dynamicShapes.add(val);
	}

	public final void elementClosed()
	{
		// our layer is complete, add it to the parent!
		addDynamicTrackShapes(_dynamicShapes);

		_dynamicShapes = null;
	}

	abstract public void addDynamicTrackShapes(Debrief.Wrappers.DynamicTrackShapes.DynamicTrackShapeSetWrapper data);

	public static void exportShapeSet(final Debrief.Wrappers.DynamicTrackShapes.DynamicTrackShapeSetWrapper sensor,
			final org.w3c.dom.Element parent, final org.w3c.dom.Document doc)
	{

		final Element trk = doc.createElement(MY_TYPE);
		trk.setAttribute("Name", toXML(sensor.getName()));
		trk.setAttribute("Visible", writeThis(sensor.getVisible()));
		trk.setAttribute("TrackName", sensor.getHost().getName());
		trk.setAttribute("LineThickness", writeThis(sensor.getLineThickness()));

		
		// now the points
		final java.util.Enumeration<Editable> iter = sensor.elements();
		while (iter.hasMoreElements())
		{
			final MWC.GUI.Plottable pl = (MWC.GUI.Plottable) iter.nextElement();
			if (pl instanceof Debrief.Wrappers.DynamicTrackShapes.DynamicTrackShapeWrapper)
			{
				final Debrief.Wrappers.DynamicTrackShapes.DynamicTrackShapeWrapper fw = (Debrief.Wrappers.DynamicTrackShapes.DynamicTrackShapeWrapper) pl;
				DynamicTrackCoverageHandler.exportFix(fw, trk, doc);
			}

		}

		parent.appendChild(trk);
	}


}
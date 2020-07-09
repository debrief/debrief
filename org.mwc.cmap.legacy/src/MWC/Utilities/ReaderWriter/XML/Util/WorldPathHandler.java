
package MWC.Utilities.ReaderWriter.XML.Util;

/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

import java.util.Iterator;
import java.util.Vector;

import MWC.GUI.Shapes.PolygonShape.PolygonNode;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldPath;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

abstract public class WorldPathHandler extends MWCXMLReader {

	static final private String _myType = "WorldPath";
	static final private String POINT = "Point";

	public static void exportThis(final Vector<PolygonNode> nodes, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		final org.w3c.dom.Element eLoc = doc.createElement(_myType);

		// step through the list
		final Iterator<PolygonNode> it = nodes.iterator();

		while (it.hasNext()) {
			final WorldLocation wl = it.next().getLocation();
			LocationHandler.exportLocation(wl, POINT, eLoc, doc);
		}
		parent.appendChild(eLoc);
	}

	public static void exportThis(final WorldPath path, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		final org.w3c.dom.Element eLoc = doc.createElement(_myType);

		// step through the list
		final Iterator<WorldLocation> it = path.getPoints().iterator();

		while (it.hasNext()) {
			final WorldLocation wl = it.next();
			LocationHandler.exportLocation(wl, POINT, eLoc, doc);
		}
		parent.appendChild(eLoc);
	}

	private WorldPath _myPath;

	public WorldPathHandler() {
		super("WorldPath");

		addHandler(new LocationHandler(POINT) {
			@Override
			public void setLocation(final WorldLocation res) {
				addThis(res);
			}
		});

	}

	public void addThis(final WorldLocation res) {
		if (_myPath == null)
			_myPath = new WorldPath();

		_myPath.addPoint(res);
	}

	@Override
	public void elementClosed() {
		setPath(_myPath);
		_myPath = null;
	}

	abstract public void setPath(WorldPath path);

}
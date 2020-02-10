
package ASSET.Util.XML.Utils;

import java.util.Iterator;

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

import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldPath;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

abstract public class ASSETWorldPathHandler extends MWCXMLReader {

	static final private String type = "WorldPath";
	static final private String POINT = "Point";

	public static void exportThis(final WorldPath path, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		final org.w3c.dom.Element eLoc = doc.createElement(type);

		// step through the list
		final Iterator<WorldLocation> it = path.getPoints().iterator();

		while (it.hasNext()) {
			final WorldLocation wl = it.next();
			ASSETLocationHandler.exportLocation(wl, POINT, eLoc, doc);
		}

		parent.appendChild(eLoc);
	}

	private WorldPath _myPath;

	public ASSETWorldPathHandler() {
		super("WorldPath");

		addHandler(new ASSETLocationHandler(POINT) {
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
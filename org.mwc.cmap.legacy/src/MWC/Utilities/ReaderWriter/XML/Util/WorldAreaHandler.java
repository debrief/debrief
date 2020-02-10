
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

import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

abstract public class WorldAreaHandler extends MWCXMLReader {

	static final private String _myType = "WorldArea";
	static final private String TOP_LEFT = "TopLeft";
	static final private String BOTTOM_RIGHT = "BottomRight";

	public static void exportThis(final WorldArea area, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		exportThis(area, parent, doc, _myType);
	}

	public static void exportThis(final WorldArea area, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc, final String name) {
		final org.w3c.dom.Element eLoc = doc.createElement(name);

		// step through the list
		LocationHandler.exportLocation(area.getTopLeft(), TOP_LEFT, eLoc, doc);
		LocationHandler.exportLocation(area.getBottomRight(), BOTTOM_RIGHT, eLoc, doc);

		parent.appendChild(eLoc);
	}

	WorldLocation _topLeft;

	WorldLocation _bottomRight;

	public WorldAreaHandler() {
		this(_myType);
	}

	public WorldAreaHandler(final String name) {
		super(name);
		addHandler(new LocationHandler(TOP_LEFT) {
			@Override
			public void setLocation(final WorldLocation res) {
				_topLeft = res;
			}
		});
		addHandler(new LocationHandler(BOTTOM_RIGHT) {
			@Override
			public void setLocation(final WorldLocation res) {
				_bottomRight = res;
			}
		});
	}

	@Override
	public void elementClosed() {
		setArea(new WorldArea(_topLeft, _bottomRight));
		_topLeft = null;
		_bottomRight = null;
	}

	abstract public void setArea(WorldArea area);

}
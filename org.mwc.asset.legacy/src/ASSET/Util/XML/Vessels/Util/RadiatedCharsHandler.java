
package ASSET.Util.XML.Vessels.Util;

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

import ASSET.Models.Vessels.Radiated.RadiatedCharacteristics;
import ASSET.Models.Vessels.Radiated.RadiatedCharacteristics.Medium;

abstract public class RadiatedCharsHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader {

	private static final String MY_TYPE = "RadiatedCharacteristics";

	static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		final RadiatedCharacteristics chars = (RadiatedCharacteristics) toExport;

		final org.w3c.dom.Element me = doc.createElement(MY_TYPE);

		// step through the mediums
		final java.util.Iterator<Medium> indices = chars.getMediums().iterator();

		while (indices.hasNext()) {
			final RadiatedCharacteristics.Medium med = indices.next();
			if (med instanceof ASSET.Models.Mediums.BroadbandRadNoise)
				ASSET.Util.XML.Vessels.Util.Mediums.BBHandler.exportThis(med, me, doc);
			else if (med instanceof ASSET.Models.Mediums.Optic)
				ASSET.Util.XML.Vessels.Util.Mediums.OpticHandler.exportThis(med, me, doc);
			else if (med instanceof ASSET.Models.Mediums.SSKBroadband)
				ASSET.Util.XML.Vessels.Util.Mediums.SSKBBHandler.exportThis(med, me, doc);
		}

		parent.appendChild(me);

	}

	private ASSET.Models.Vessels.Radiated.RadiatedCharacteristics _myChars = new ASSET.Models.Vessels.Radiated.RadiatedCharacteristics();

	public RadiatedCharsHandler() {
		this(MY_TYPE);
	}

	RadiatedCharsHandler(final String type) {
		super(type);
		addHandler(new ASSET.Util.XML.Vessels.Util.Mediums.OpticHandler() {
			@Override
			public void setMedium(final int index,
					final ASSET.Models.Vessels.Radiated.RadiatedCharacteristics.Medium med) {
				addMedium(new Integer(index), med);
			}
		});
		addHandler(new ASSET.Util.XML.Vessels.Util.Mediums.BBHandler() {
			@Override
			public void setMedium(final int index,
					final ASSET.Models.Vessels.Radiated.RadiatedCharacteristics.Medium med) {
				addMedium(new Integer(index), med);
			}
		});
		addHandler(new ASSET.Util.XML.Vessels.Util.Mediums.NBHandler() {
			@Override
			public void setMedium(final int index,
					final ASSET.Models.Vessels.Radiated.RadiatedCharacteristics.Medium med) {
				addMedium(new Integer(index), med);
			}
		});
		addHandler(new ASSET.Util.XML.Vessels.Util.Mediums.SSKBBHandler() {
			@Override
			public void setMedium(final int index,
					final ASSET.Models.Vessels.Radiated.RadiatedCharacteristics.Medium med) {
				addMedium(new Integer(index), med);
			}
		});
	}

	void addMedium(final Integer index, final ASSET.Models.Vessels.Radiated.RadiatedCharacteristics.Medium medium) {
		_myChars.add(index.intValue(), medium);
	}

	@Override
	public void elementClosed() {
		// pass it to the parent
		setRadiation(_myChars);

		// reset the mediums
		_myChars = new ASSET.Models.Vessels.Radiated.RadiatedCharacteristics();
	}

	abstract public void setRadiation(ASSET.Models.Vessels.Radiated.RadiatedCharacteristics rads);
}
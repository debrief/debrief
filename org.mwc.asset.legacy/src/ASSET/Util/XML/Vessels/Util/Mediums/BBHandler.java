
package ASSET.Util.XML.Vessels.Util.Mediums;

import ASSET.Models.Environment.EnvironmentType;

/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

abstract public class BBHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader {

	final static private String type = "Broadband";
	final static private String LEVEL = "BaseNoiseLevel";

	static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		final ASSET.Models.Mediums.BroadbandRadNoise bb = (ASSET.Models.Mediums.BroadbandRadNoise) toExport;

		final org.w3c.dom.Element ele = doc.createElement(type);

		ele.setAttribute(LEVEL, writeThis(bb.getBaseNoiseLevel()));

		parent.appendChild(ele);
	}

	double _myNoise;

	public BBHandler() {
		super(type);

		addAttributeHandler(new HandleDoubleAttribute(LEVEL) {
			@Override
			public void setValue(final String name, final double val) {
				_myNoise = val;
			}
		});
	}

	@Override
	public void elementClosed() {
		final ASSET.Models.Mediums.BroadbandRadNoise bb = new ASSET.Models.Mediums.BroadbandRadNoise(_myNoise);
		setMedium(EnvironmentType.BROADBAND_PASSIVE, bb);
	}

	abstract public void setMedium(int index, ASSET.Models.Vessels.Radiated.RadiatedCharacteristics.Medium med);
}
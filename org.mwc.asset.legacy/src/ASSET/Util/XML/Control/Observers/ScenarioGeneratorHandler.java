
package ASSET.Util.XML.Control.Observers;

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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

abstract public class ScenarioGeneratorHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader {
	private final static String type = "ScenarioGenerator";

	public static void exportThis(final Object generator, final Element parent, final Document doc) {
		// create ourselves
		final Element sens = doc.createElement(type);

		parent.appendChild(sens);

	}

	public ScenarioGeneratorHandler() {
		// inform our parent what type of class we are
		super(type);
	}

	@Override
	public void elementClosed() {
		setScenarioGenerator(null);
	}

	abstract public void setScenarioGenerator(Object genny);

}

package ASSET.Util.XML.Control.Observers;

import java.io.File;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

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

import ASSET.Scenario.Observers.ScenarioObserver;
import ASSET.Util.XML.ASSETReaderWriter;
import ASSET.Util.XML.MonteCarlo.ScenarioGeneratorHandler;

abstract public class ScenarioControllerHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader {
	public final static String type = "ScenarioController";

	private static final String OUTPUT_DIRECTORY = "OutputDirectory";

	private static final String RANDOM_SEED = "RandomSeed";

	public static void exportThis(final Vector<ScenarioObserver> list, final Object generator, final Element parent,
			final Document doc) {
		// create ourselves
		final Element sens = doc.createElement(type);

		ObserverListHandler.exportThis(list, sens, doc);
		// todo export the scenario generator stuff

		parent.appendChild(sens);

	}

	Vector<ScenarioObserver> _myObserverList;

	File _outputDirectory;

	Integer _randomSeed = null;

	public ScenarioControllerHandler() {
		// inform our parent what type of class we are
		super(type);

		addHandler(new ObserverListHandler() {
			@Override
			public void setObserverList(final Vector<ScenarioObserver> list) {
				_myObserverList = list;
			}
		});

		addAttributeHandler(new HandleAttribute(OUTPUT_DIRECTORY) {
			@Override
			public void setValue(final String name, final String value) {
				_outputDirectory = new File(value);
			}
		});

		addAttributeHandler(new HandleAttribute(RANDOM_SEED) {
			@Override
			public void setValue(final String name, final String value) {
				_randomSeed = Integer.valueOf(value);
			}
		});

		addHandler(new ScenarioGeneratorHandler() {
		});
	}

	@Override
	public void elementClosed() {

		// build the results object
		final ASSETReaderWriter.ResultsContainer rc = new ASSETReaderWriter.ResultsContainer();
		rc.observerList = _myObserverList;
		rc.outputDirectory = _outputDirectory;
		rc.randomSeed = _randomSeed;

		// ok, now output it
		setResults(rc);

		// and clear
		_myObserverList = null;
		_randomSeed = null;
		_outputDirectory = null;
	}

	abstract public void setResults(ASSETReaderWriter.ResultsContainer results);

}
package ASSET.Util.XML.Control.Observers;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import ASSET.Scenario.Observers.ScenarioObserver;
import ASSET.Util.XML.ASSETReaderWriter;
import ASSET.Util.XML.MonteCarlo.ScenarioGeneratorHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.util.Vector;

abstract public class ScenarioControllerHandler extends
		MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{
	private final static String type = "ScenarioController";

	Vector<ScenarioObserver> _myObserverList;

	File _outputDirectory;

	private static final String OUTPUT_DIRECTORY = "OutputDirectory";
	private static final String RANDOM_SEED = "RandomSeed";

	Integer _randomSeed = null;

	public ScenarioControllerHandler()
	{
		// inform our parent what type of class we are
		super(type);

		addHandler(new ObserverListHandler()
		{
			public void setObserverList(Vector<ScenarioObserver> list)
			{
				_myObserverList = list;
			}
		});

		addAttributeHandler(new HandleAttribute(OUTPUT_DIRECTORY)
		{
			public void setValue(String name, String value)
			{
				_outputDirectory = new File(value);
			}
		});

		addAttributeHandler(new HandleAttribute(RANDOM_SEED)
		{
			public void setValue(String name, String value)
			{
				_randomSeed = Integer.valueOf(value);
			}
		});

		addHandler(new ScenarioGeneratorHandler()
		{
			@SuppressWarnings("unused")
			public void setScenarioGenerator(Object genny)
			{
				// don't bother doing anything, we're just including this to stop an
				// error being thrown
				// later on
			}
		});
	}

	public void elementClosed()
	{

		// build the results object
		ASSETReaderWriter.ResultsContainer rc = new ASSETReaderWriter.ResultsContainer();
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

	public static void exportThis(final Vector<ScenarioObserver> list,
			final Object generator, final Element parent, final Document doc)
	{
		// create ourselves
		final Element sens = doc.createElement(type);

		ObserverListHandler.exportThis(list, sens, doc);
		// todo export the scenario generator stuff

		parent.appendChild(sens);

	}

}
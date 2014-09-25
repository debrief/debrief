package com.planetmayo.debrief.satc.model.contributions;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.junit.Before;
import org.junit.Test;
import org.mwc.debrief.core.editors.PlotEditor;
import org.mwc.debrief.core.loaders.XMLLoader;
import org.mwc.debrief.core.loaders.xml_handlers.DebriefEclipseXMLReaderWriter;

import MWC.GUI.Layers;

public class Range1959ForecastContributionTest
{

	private Range1959ForecastContribution _freq;

	@Before
	public void setUp() throws Exception
	{
		_freq = new Range1959ForecastContribution();
	}
	
	public void populate() throws FileNotFoundException
	{
		// ok, load the data-file.
		DebriefEclipseXMLReaderWriter loader = new DebriefEclipseXMLReaderWriter();
		String fName = "tests/com/planetmayo/debrief/satc/model/contributions/FreqTracks.xml";
		FileInputStream is = new FileInputStream(fName);
		Layers theData = new Layers();
		PlotEditor dPlot = new PlotEditor();
		loader.importThis(fName, is, theData,null, dPlot);
	}

	@Test
	public void testActUpon()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testCalcError()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testAddMeasurement() throws FileNotFoundException
	{
		assertNotNull("Model exists", _freq);
		assertEquals("no measurements",  0, _freq.size());
		
		populate();
	}

}

package com.planetmayo.debrief.satc.model.contributions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import org.junit.Before;
import org.junit.Test;
import org.mwc.cmap.core.interfaces.IControllableViewport;
import org.mwc.debrief.core.editors.PlotEditor;
import org.mwc.debrief.core.loaders.xml_handlers.DebriefEclipseXMLReaderWriter;
import org.mwc.debrief.core.loaders.xml_handlers.PlotHandler;
import org.mwc.debrief.core.loaders.xml_handlers.SessionHandler;
import org.mwc.debrief.satc_interface.actions.CreateSolutionFromSensorData;
import org.mwc.debrief.satc_interface.readerwriter.SATCHandler;

import Debrief.ReaderWriter.XML.DebriefLayersHandler;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.Algorithms.PlainProjection;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import MWC.GenericData.WorldArea;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;

public class Range1959ForecastContributionTest
{

	private Range1959ForecastContribution _freq;
	private Layers _layers;

	@Before
	public void setUp() throws Exception
	{
		_freq = new Range1959ForecastContribution();
	}
	
	public void populate() throws FileNotFoundException
	{
		// ok, load the data-file.
		DebriefEclipseXMLReaderWriter loader = new DebriefEclipseXMLReaderWriter();
		
		String fName = "../org.mwc.cmap.combined.feature/root_installs/sample_data/SATC/FreqTracks.xml";
		FileInputStream is = new FileInputStream(fName);
		 _layers = new Layers();
		PlotEditor plot = new PlotEditor();
		IControllableViewport view = new IControllableViewport(){
			public void setViewport(WorldArea target)
			{}
			public WorldArea getViewport()
			{return null;}
			public void setProjection(PlainProjection proj)
			{}
			public PlainProjection getProjection()
			{return null;}
			public void update()
			{}
			public void rescale()
			{}};
		PlotHandler handler = new PlotHandler(fName, _layers, view, plot);
		
		Vector<MWCXMLReader> handlers = handler.getHandlers();
		Iterator<MWCXMLReader> iter = handlers.iterator();
		while (iter.hasNext())
		{
			MWCXMLReader reader = (MWCXMLReader) iter.next();
			if(reader instanceof SessionHandler)
			{
				SessionHandler sH = (SessionHandler) reader;
				Vector<MWCXMLReader> handlers2 = sH.getHandlers();
				Iterator<MWCXMLReader> iter2 = handlers2.iterator();
				while (iter2.hasNext())
				{
					MWCXMLReader reader2 = (MWCXMLReader) iter2.next();
					if(reader2 instanceof DebriefLayersHandler)
					{
						DebriefLayersHandler lH = (DebriefLayersHandler) reader2;
						lH.addHandler(new SATCHandler());
					}
				}
			}
		}
		
		loader.importThis(fName, is, handler);
	}

//	@Test
//	public void testActUpon()
//	{
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testCalcError()
//	{
//		fail("Not yet implemented");
//	}

	@Test
	public void testAddMeasurement() throws FileNotFoundException
	{
		assertNotNull("Model exists", _freq);
		assertEquals("no measurements",  0, _freq.size());
		
		populate();
		
		assertNotNull("layers created", _layers);
		assertEquals("found layers",  3, _layers.size());
		
		// get the sensor layer
		TrackWrapper sensor = (TrackWrapper) _layers.findLayer("SENSOR");
		assertNotNull("found sensor",  sensor);
		
		//	get the sensor data
		SensorWrapper theS = (SensorWrapper) sensor.getSensors().last();
		Collection<Editable> legOneCuts = theS.getItemsBetween(DebriefFormatDateTime.parseThis("100112 131000"), DebriefFormatDateTime.parseThis("100112 132001"));
		Collection<Editable> legTwoCuts = theS.getItemsBetween(DebriefFormatDateTime.parseThis("100112 133500"), DebriefFormatDateTime.parseThis("100112 134051"));
		assertEquals("got all cuts", 13, legOneCuts.size());
		assertEquals("got all cuts", 8, legTwoCuts.size());
		
		ArrayList<SensorContactWrapper> legOneArr = wrapThis(legOneCuts);
		ArrayList<SensorContactWrapper> legTwoArr = wrapThis(legTwoCuts);
		
		BaseContribution cont = CreateSolutionFromSensorData.generate1959(legOneArr);
		
	 see contents of BearingMeasurementContributionTest
	}

	private ArrayList<SensorContactWrapper> wrapThis(Collection<Editable> cuts)
	{
		ArrayList<SensorContactWrapper> res = new ArrayList<SensorContactWrapper>();
		for (Iterator<Editable> iter = cuts.iterator(); iter.hasNext();)
		{
			SensorContactWrapper editable = (SensorContactWrapper) iter.next();
			res.add(editable);
		}
		return res;
	}
	
}

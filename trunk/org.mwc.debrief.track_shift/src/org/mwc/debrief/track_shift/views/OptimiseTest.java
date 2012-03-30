package org.mwc.debrief.track_shift.views;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.eclipse.swt.widgets.Composite;
import org.jfree.chart.plot.XYPlot;
import org.mockito.Mockito;
import org.mwc.cmap.core.DataTypes.TrackData.TrackDataProvider;
import org.mwc.cmap.core.interfaces.IControllableViewport;
import org.mwc.debrief.core.editors.PlotEditor;
import org.mwc.debrief.core.loaders.xml_handlers.DebriefEclipseXMLReaderWriter;

import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.ErrorLogger;
import MWC.GUI.Layers;
import MWC.GenericData.WatchableList;

public class OptimiseTest
{

	public static void main(String[] args) throws FileNotFoundException
	{
		// get some data
		Layers layers = getTheData();

		// create a mockup of the plot
		StackedDotHelper helper = new StackedDotHelper();

		XYPlot dotPlot = Mockito.mock(XYPlot.class);
		XYPlot linePlot= Mockito.mock(XYPlot.class);
		TrackDataProvider tracks = new TProv();
		Composite holder  = Mockito.mock(Composite.class);
		ErrorLogger logger= Mockito.mock(ErrorLogger.class);
		// run setup optimise
		helper.updateBearingData(dotPlot, linePlot, tracks, true, true, false, holder, logger, true);

//		HAVE A LOOK AT THE UPDATE BEARING DATA - THE COST FUNCTION IS IN THERE 
		
		
		// and optimise it
	}

	private static class TProv implements TrackDataProvider
	{

		@Override
		public void addTrackDataListener(TrackDataListener listener)
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void removeTrackDataListener(TrackDataListener listener)
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void addTrackShiftListener(TrackShiftListener listener)
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void removeTrackShiftListener(TrackShiftListener listener)
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void fireTrackShift(TrackWrapper target)
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void fireTracksChanged()
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public WatchableList getPrimaryTrack()
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public WatchableList[] getSecondaryTracks()
		{
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	private static Layers getTheData() throws FileNotFoundException
	{
		DebriefEclipseXMLReaderWriter reader = new DebriefEclipseXMLReaderWriter();
		Layers res = new Layers();
		String path = "src/org/mwc/debrief/track_shift/views/";
		String fName = "midflow2.xml";
		InputStream is = new FileInputStream(path + fName);
		IControllableViewport view = Mockito.mock(IControllableViewport.class);
		PlotEditor plot = Mockito.mock(PlotEditor.class);
		reader.importThis(fName, is, res, view, plot);
		return res;
	}

}

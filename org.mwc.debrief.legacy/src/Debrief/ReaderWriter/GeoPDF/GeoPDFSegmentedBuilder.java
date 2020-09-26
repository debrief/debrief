package Debrief.ReaderWriter.GeoPDF;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;

import Debrief.ReaderWriter.GeoPDF.GeoPDF.GeoPDFLayerTrack;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Layers;
import MWC.GenericData.TimePeriod;

public class GeoPDFSegmentedBuilder extends AbstractGeoPDFBuilder{

	@Override
	public GeoPDF build(Layers layers, GeoPDFConfiguration configuration)
			throws IOException, InterruptedException, NoSuchFieldException, SecurityException, IllegalArgumentException,
			IllegalAccessException, ClassNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void createMinutesLayer(GeoPDFConfiguration configuration, ArrayList<File> filesToDelete,
			TrackWrapper currentTrack, GeoPDFLayerTrack newTrackLayer, TimePeriod period, String dateFormat)
			throws FileNotFoundException, JsonProcessingException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void createOnePointLayer(GeoPDFConfiguration configuration, ArrayList<File> filesToDelete,
			TrackWrapper currentTrack, GeoPDFLayerTrack newTrackLayer, TimePeriod period)
			throws FileNotFoundException, JsonProcessingException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void createLabelsLayer(GeoPDFConfiguration configuration, ArrayList<File> filesToDelete,
			TrackWrapper currentTrack, GeoPDFLayerTrack newTrackLayer, TimePeriod period, String dateFormat)
			throws FileNotFoundException, JsonProcessingException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void createTrackLine(ArrayList<File> filesToDelete, TrackWrapper currentTrack,
			GeoPDFLayerTrack newTrackLayer, TimePeriod period, String dateFormat)
			throws FileNotFoundException, JsonProcessingException {
		// TODO Auto-generated method stub
		
	}

}

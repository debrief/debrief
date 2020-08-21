package Debrief.ReaderWriter.GeoPDF;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import MWC.GenericData.WorldArea;

public class GeoPDF {
	
	public static class GeoPDFPage{
		
		private int dpi;
		private double width;
		private double height;
		
		private WorldArea area;
	}
	
	public static class GeoPDFLayer{
		private String id;
		private String name;
		private ArrayList<OutputStream> data;
		
	}
	
	public static class GeoPDFLayerBackground extends GeoPDFLayer{
		private ArrayList<HashMap<String, String>> raster = new ArrayList();
		
		
	}
	
	public static class GeoPDFLayerTrack extends GeoPDFLayer{
		
	}
	
	private ArrayList<GeoPDFLayer> layers = new ArrayList<GeoPDF.GeoPDFLayer>();
	
	private String author;

	
	/**
	 * We will create here the XML Composition.
	 */
	@Override
	public String toString() {
		return "GeoPDF []";
	}
	
}

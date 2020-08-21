package Debrief.ReaderWriter.GeoPDF;

import java.util.ArrayList;
import java.util.HashMap;

import MWC.GenericData.WorldArea;

public class GeoPDF {

	public static class GeoPDFPage {

		private int dpi;
		private double width;
		private double height;

		private WorldArea area;
		private ArrayList<GeoPDFLayer> layers = new ArrayList<GeoPDF.GeoPDFLayer>();

		public void addLayer(final GeoPDFLayer newLayer) {
			layers.add(newLayer);
		}

		public int getDpi() {
			return dpi;
		}

		public void setDpi(int dpi) {
			this.dpi = dpi;
		}

		public double getWidth() {
			return width;
		}

		public void setWidth(double width) {
			this.width = width;
		}

		public double getHeight() {
			return height;
		}

		public void setHeight(double height) {
			this.height = height;
		}

		public WorldArea getArea() {
			return area;
		}

		public void setArea(WorldArea area) {
			this.area = area;
		}

		public ArrayList<GeoPDFLayer> getLayers() {
			return layers;
		}
	}

	public static class GeoPDFLayerVector {
		private String style;
		private String data;
		public String getStyle() {
			return style;
		}
		public void setStyle(String style) {
			this.style = style;
		}
		public String getData() {
			return data;
		}
		public void setData(String data) {
			this.data = data;
		}
	}

	public static class GeoPDFLayer {
		private String id;
		private String name;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}

	public static class GeoPDFLayerBackground extends GeoPDFLayer {
		private ArrayList<HashMap<String, String>> raster = new ArrayList();

	}

	public static class GeoPDFLayerTrack extends GeoPDFLayer {

		private ArrayList<GeoPDFLayerVector> vectors = new ArrayList<GeoPDFLayerVector>();

		public void addVector(final GeoPDFLayerVector vector) {
			vectors.add(vector);
		}

		public ArrayList<GeoPDFLayerVector> getVectors() {
			return vectors;
		}
	}

	private String author;
	private ArrayList<GeoPDFPage> pages = new ArrayList<GeoPDF.GeoPDFPage>();

	public GeoPDFPage createNewPage() {
		final GeoPDFPage newPage = new GeoPDFPage();
		pages.add(newPage);
		return newPage;
	}

	/**
	 * We will create here the XML Composition.
	 */
	@Override
	public String toString() {
		return "GeoPDF []";
	}

	
}

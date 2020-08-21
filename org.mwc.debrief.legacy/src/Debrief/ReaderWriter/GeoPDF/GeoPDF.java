package Debrief.ReaderWriter.GeoPDF;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;

import MWC.GenericData.WorldArea;
import net.n3.nanoxml.IXMLElement;
import net.n3.nanoxml.XMLElement;
import net.n3.nanoxml.XMLWriter;

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

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String toXML() throws IOException {
		final XMLElement pdfComposition = new XMLElement("PDFComposition");
		final XMLElement metadata = new XMLElement("Metadata");
		pdfComposition.addChild(metadata);
		final IXMLElement author = new XMLElement("Author");
		metadata.addChild(author);
		author.setContent(getAuthor());

		final IXMLElement layerTree = new XMLElement("LayerTree");
		pdfComposition.addChild(layerTree);
		layerTree.setAttribute("displayOnlyOnVisiblePages", "true");

		for (GeoPDFPage page : pages) {
			for (GeoPDFLayer layer : page.getLayers()) {
				final IXMLElement newLayerXML = new XMLElement("Layer");
				layerTree.addChild(newLayerXML);
				newLayerXML.setAttribute("id", layer.getId());
				newLayerXML.setAttribute("name", layer.getName());
			}
		}

		for (int i = 0; i < pages.size(); i++) {
			final GeoPDFPage currentPage = pages.get(i);
			final IXMLElement pageXML = new XMLElement("Page");
			pdfComposition.addChild(pageXML);
			pageXML.setAttribute("id", "page_" + (i + 1));
			
			final IXMLElement dpiXML = new XMLElement("DPI");
			pageXML.addChild(dpiXML);
			dpiXML.setContent(currentPage.getDpi() + "");
			
			final IXMLElement widthXML = new XMLElement("Width");
			pageXML.addChild(widthXML);
			widthXML.setContent(currentPage.getWidth() + "");
			
			final IXMLElement heightXML = new XMLElement("Height");
			pageXML.addChild(heightXML);
			heightXML.setContent(currentPage.getHeight() + "");
			
			
		}
		
		XMLWriter xmlWrite = new XMLWriter(System.out);
		
		xmlWrite.write(pdfComposition);
		
		return null;
	}

	/**
	 * We will create here the XML Composition.
	 */
	@Override
	public String toString() {
		return "GeoPDF []";
	}

}

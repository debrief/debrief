package Debrief.ReaderWriter.GeoPDF;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;

import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import net.n3.nanoxml.IXMLElement;
import net.n3.nanoxml.XMLElement;
import net.n3.nanoxml.XMLWriter;

public class GeoPDF {

	public static class GeoPDFPage {

		private int id;
		private int dpi;
		private double width;
		private double height;

		private WorldArea area;
		private ArrayList<GeoPDFLayer> layers = new ArrayList<GeoPDF.GeoPDFLayer>();

		public GeoPDFPage(final int id) {
			this.id = id;
		}

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

		public int getId() {
			return id;
		}

		public IXMLElement toXML() {
			final IXMLElement pageXML = new XMLElement("Page");
			pageXML.setAttribute("id", "page_" + getId());

			final IXMLElement dpiXML = new XMLElement("DPI");
			pageXML.addChild(dpiXML);
			dpiXML.setContent(getDpi() + "");

			final IXMLElement widthXML = new XMLElement("Width");
			pageXML.addChild(widthXML);
			widthXML.setContent(getWidth() + "");

			final IXMLElement heightXML = new XMLElement("Height");
			pageXML.addChild(heightXML);
			heightXML.setContent(getHeight() + "");

			final IXMLElement geoReferencingXML = new XMLElement("Georeferencing");
			pageXML.addChild(geoReferencingXML);

			final String georeferencingId = "georeferenced_" + getId();
			geoReferencingXML.setAttribute("id", georeferencingId);

			final IXMLElement srsXML = new XMLElement("SRS");
			srsXML.setContent("EPSG:4326");
			geoReferencingXML.addChild(srsXML);

			WorldArea plotArea = getArea();
			if (plotArea == null) {
				// TODO This is just for testing. Area should never be null
				plotArea = new WorldArea(new WorldLocation(50, -0.8, 0), new WorldLocation(50.4, -0.1, 0));
			}

			// TOP LEFT
			final IXMLElement topLeftXML = new XMLElement("ControlPoint");
			topLeftXML.setAttribute("x", "1");
			topLeftXML.setAttribute("y", "1");
			topLeftXML.setAttribute("GeoX", plotArea.getTopLeft().getLat() + "");
			topLeftXML.setAttribute("GeoY", plotArea.getTopLeft().getLong() + "");
			geoReferencingXML.addChild(topLeftXML);

			// TOP RIGHT
			final IXMLElement topRightXML = new XMLElement("ControlPoint");
			topRightXML.setAttribute("x", "1");
			topRightXML.setAttribute("y", getHeight() + "");
			topRightXML.setAttribute("GeoX", plotArea.getTopRight().getLat() + "");
			topRightXML.setAttribute("GeoY", plotArea.getTopRight().getLong() + "");
			geoReferencingXML.addChild(topRightXML);

			// BOTTOM LEFT
			final IXMLElement bottomLeftXML = new XMLElement("ControlPoint");
			bottomLeftXML.setAttribute("x", getWidth() + "");
			bottomLeftXML.setAttribute("y", "1");
			bottomLeftXML.setAttribute("GeoX", plotArea.getBottomLeft().getLat() + "");
			bottomLeftXML.setAttribute("GeoY", plotArea.getBottomLeft().getLong() + "");
			geoReferencingXML.addChild(bottomLeftXML);

			// BOTTOM RIGHT
			final IXMLElement bottomRightXML = new XMLElement("ControlPoint");
			bottomRightXML.setAttribute("x", getWidth() + "");
			bottomRightXML.setAttribute("y", getHeight() + "");
			bottomRightXML.setAttribute("GeoX", plotArea.getBottomRight().getLat() + "");
			bottomRightXML.setAttribute("GeoY", plotArea.getBottomRight().getLong() + "");
			geoReferencingXML.addChild(bottomRightXML);

			final IXMLElement contentXml = new XMLElement("Content");
			pageXML.addChild(contentXml);

			/**
			 * Now Let's add all the layers of the page.
			 */
			for (GeoPDFLayer layer : layers) {
				contentXml.addChild(layer.toXML(georeferencingId));
			}

			return pageXML;
		}
	}

	public static class GeoPDFLayerVector {

		public static class LogicalStructure {
			private String name;
			private String fieldToDisplay;

			public LogicalStructure(String name, String fieldToDisplay) {
				this.name = name;
				this.fieldToDisplay = fieldToDisplay;
			}

			public String getName() {
				return name;
			}

			public String getFieldToDisplay() {
				return fieldToDisplay;
			}

			public IXMLElement toXML() {
				final IXMLElement logicalStructureXML = new XMLElement("LogicalStructure");
				logicalStructureXML.setAttribute("displayLayerName", getName());
				logicalStructureXML.setAttribute("fieldToDisplay", getFieldToDisplay());
				return logicalStructureXML;
			}

		}

		private String style;
		private String data;
		private String name;
		private LogicalStructure logicalStructure;

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

		public LogicalStructure getLogicalStructure() {
			return logicalStructure;
		}

		public void setLogicalStructure(LogicalStructure logicalStructure) {
			this.logicalStructure = logicalStructure;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public IXMLElement toXML(String geoReferenceId) {
			final IXMLElement vectorXML = new XMLElement("Vector");
			vectorXML.setAttribute("dataset", getData());
			vectorXML.setAttribute("layer", getName());
			vectorXML.setAttribute("georeferencingId", geoReferenceId);
			vectorXML.setAttribute("ogrStyleString", getStyle());

			if (getLogicalStructure() != null) {
				vectorXML.addChild(getLogicalStructure().toXML());
			}

			return vectorXML;
		}
	}

	public static abstract class GeoPDFLayer {
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

		public abstract IXMLElement toXML(final String geoReferenceId);
	}

	public static class GeoPDFLayerBackground extends GeoPDFLayer {
		private ArrayList<String> rasters = new ArrayList<String>();

		public ArrayList<String> getRasters() {
			return rasters;
		}

		public void addRaster(final String rasterTif) {
			rasters.add(rasterTif);
		}

		@Override
		public IXMLElement toXML(final String geoReferenceId) {
			final IXMLElement layer = new XMLElement("IfLayerOn");
			layer.setAttribute("layerId", getId());

			for (String raster : getRasters()) {
				final IXMLElement rasterXML = new XMLElement("Raster");
				rasterXML.setAttribute("dataset", raster);
				rasterXML.setAttribute("georeferencingId", geoReferenceId);
				layer.addChild(rasterXML);
			}

			return layer;
		}
	}

	public static class GeoPDFLayerTrack extends GeoPDFLayer {

		private ArrayList<GeoPDFLayerVector> vectors = new ArrayList<GeoPDFLayerVector>();

		public void addVector(final GeoPDFLayerVector vector) {
			vectors.add(vector);
		}

		public ArrayList<GeoPDFLayerVector> getVectors() {
			return vectors;
		}

		@Override
		public IXMLElement toXML(final String geoReferenceId) {
			final IXMLElement layer = new XMLElement("IfLayerOn");
			layer.setAttribute("layerId", getId());

			for (GeoPDFLayerVector vector : vectors) {
				layer.addChild(vector.toXML(geoReferenceId));
			}

			return layer;
		}
	}

	private String author;
	private String producer;
	private String creator;
	private String creationDate;
	private String subject;
	private String title;
	private String keywords;
	private ArrayList<GeoPDFPage> pages = new ArrayList<GeoPDF.GeoPDFPage>();

	public GeoPDFPage createNewPage() {
		final GeoPDFPage newPage = new GeoPDFPage(pages.size() + 1);
		pages.add(newPage);
		return newPage;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getProducer() {
		return producer;
	}

	public void setProducer(String producer) {
		this.producer = producer;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public ArrayList<GeoPDFPage> getPages() {
		return pages;
	}

	public void setPages(ArrayList<GeoPDFPage> pages) {
		this.pages = pages;
	}

	public IXMLElement toXML() throws IOException {
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
			pdfComposition.addChild(currentPage.toXML());
		}

		return pdfComposition;
	}

	@Override
	public String toString() {
		try {
			final OutputStream outputStream = new OutputStream() {

				private StringBuilder builder = new StringBuilder();

				@Override
				public void write(int b) throws IOException {
					builder.append((char) b);
				}

				public String toString() {
					return builder.toString();
				}
			};

			final XMLWriter xmlWrite = new XMLWriter(outputStream);
			xmlWrite.write(toXML(), true);

			return outputStream.toString();
		} catch (IOException e) {
			// This will never be called....
		}
		return null;
	}

}

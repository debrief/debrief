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

package Debrief.ReaderWriter.GeoPDF;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import net.n3.nanoxml.IXMLElement;
import net.n3.nanoxml.XMLElement;
import net.n3.nanoxml.XMLWriter;

public class GeoPDF {

	public static abstract class GeoPDFLayer {
		private String id;
		private String name;
		private final ArrayList<GeoPDFLayerTrack> children = new ArrayList<GeoPDF.GeoPDFLayerTrack>();

		public void addChild(final GeoPDFLayerTrack child) {
			children.add(child);
		}

		public ArrayList<GeoPDFLayerTrack> getChildren() {
			return children;
		}

		public String getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public void setId(final String id) {
			this.id = id;
		}

		public void setName(final String name) {
			this.name = name;
		}

		public abstract IXMLElement toXML(final String geoReferenceId);
	}

	public static class GeoPDFLayerBackground extends GeoPDFLayer {
		private final ArrayList<String> rasters = new ArrayList<String>();

		public void addRaster(final String rasterTif) {
			rasters.add(rasterTif);
		}

		public ArrayList<String> getRasters() {
			return rasters;
		}

		@Override
		public IXMLElement toXML(final String geoReferenceId) {
			final IXMLElement layer = new XMLElement("IfLayerOn");
			layer.setAttribute("layerId", getId());

			for (final String raster : getRasters()) {
				final IXMLElement rasterXML = new XMLElement("Raster");
				rasterXML.setAttribute("dataset", raster);
				rasterXML.setAttribute("georeferencingId", geoReferenceId);
				layer.addChild(rasterXML);
			}

			return layer;
		}
	}

	public static class GeoPDFLayerTrack extends GeoPDFLayer {

		private final ArrayList<GeoPDFLayerVector> vectors = new ArrayList<GeoPDFLayerVector>();

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

			for (final GeoPDFLayerVector vector : vectors) {
				layer.addChild(vector.toXML(geoReferenceId));
			}

			for (final GeoPDFLayerTrack child : getChildren()) {
				layer.addChild(child.toXML(geoReferenceId));
			}

			return layer;
		}
	}

	public static class GeoPDFLayerVector {

		public static class LogicalStructure {
			private final String name;
			private final String fieldToDisplay;

			public LogicalStructure(final String name, final String fieldToDisplay) {
				this.name = name;
				this.fieldToDisplay = fieldToDisplay;
			}

			public String getFieldToDisplay() {
				return fieldToDisplay;
			}

			public String getName() {
				return name;
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

		public String getData() {
			return data;
		}

		public LogicalStructure getLogicalStructure() {
			return logicalStructure;
		}

		public String getName() {
			return name;
		}

		public String getStyle() {
			return style;
		}

		public void setData(final String data) {
			this.data = data;
		}

		public void setLogicalStructure(final LogicalStructure logicalStructure) {
			this.logicalStructure = logicalStructure;
		}

		public void setName(final String name) {
			this.name = name;
		}

		public void setStyle(final String style) {
			this.style = style;
		}

		public IXMLElement toXML(final String geoReferenceId) {
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

	public static class GeoPDFLayerVectorLabel extends GeoPDFLayerVector {

		@Override
		public IXMLElement toXML(final String geoReferenceId) {
			final IXMLElement ans = super.toXML(geoReferenceId);
			ans.setName("VectorLabel");
			return ans;
		}

	}

	public static class GeoPDFPage {

		private final int id;
		private int dpi;
		private double width;
		private double height;
		private double margin;

		private WorldArea area;
		private final ArrayList<GeoPDFLayer> layers = new ArrayList<GeoPDF.GeoPDFLayer>();

		public GeoPDFPage(final int id) {
			this.id = id;
		}

		public void addLayer(final GeoPDFLayer newLayer) {
			layers.add(newLayer);
		}

		public WorldArea getArea() {
			return area;
		}

		public int getDpi() {
			return dpi;
		}

		public double getHeight() {
			return height;
		}

		public int getId() {
			return id;
		}

		public ArrayList<GeoPDFLayer> getLayers() {
			return layers;
		}

		public double getMargin() {
			return margin;
		}

		public double getWidth() {
			return width;
		}

		public void setArea(final WorldArea area) {
			this.area = area;
		}

		public void setDpi(final int dpi) {
			this.dpi = dpi;
		}

		public void setHeight(final double height) {
			this.height = height;
		}

		public void setMargin(final double margin) {
			this.margin = margin;
		}

		public void setWidth(final double width) {
			this.width = width;
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
				plotArea = new WorldArea(new WorldLocation(50, -0.8, 0), new WorldLocation(50.4, -0.1, 0));
			}

			// Bottom LEFT
			final IXMLElement topLeftXML = new XMLElement("ControlPoint");
			topLeftXML.setAttribute("x", "1");
			topLeftXML.setAttribute("y", "1");
			topLeftXML.setAttribute("GeoX", plotArea.getBottomLeft().getLong() + "");
			topLeftXML.setAttribute("GeoY", plotArea.getBottomLeft().getLat() + "");
			geoReferencingXML.addChild(topLeftXML);

			// TOP LEFT
			final IXMLElement topRightXML = new XMLElement("ControlPoint");
			topRightXML.setAttribute("x", "1");
			topRightXML.setAttribute("y", getHeight() + "");
			topRightXML.setAttribute("GeoX", plotArea.getTopLeft().getLong() + "");
			topRightXML.setAttribute("GeoY", plotArea.getTopLeft().getLat() + "");
			geoReferencingXML.addChild(topRightXML);

			// BOTTOM LEFT
			final IXMLElement bottomLeftXML = new XMLElement("ControlPoint");
			bottomLeftXML.setAttribute("x", getWidth() + "");
			bottomLeftXML.setAttribute("y", "1");
			bottomLeftXML.setAttribute("GeoX", plotArea.getBottomRight().getLong() + "");
			bottomLeftXML.setAttribute("GeoY", plotArea.getBottomRight().getLat() + "");
			geoReferencingXML.addChild(bottomLeftXML);

			// BOTTOM RIGHT
			final IXMLElement bottomRightXML = new XMLElement("ControlPoint");
			bottomRightXML.setAttribute("x", getWidth() + "");
			bottomRightXML.setAttribute("y", getHeight() + "");
			bottomRightXML.setAttribute("GeoX", plotArea.getTopRight().getLong() + "");
			bottomRightXML.setAttribute("GeoY", plotArea.getTopRight().getLat() + "");
			geoReferencingXML.addChild(bottomRightXML);

			final IXMLElement contentXml = new XMLElement("Content");
			pageXML.addChild(contentXml);

			/**
			 * Now Let's add all the layers of the page.
			 */
			for (final GeoPDFLayer layer : layers) {
				contentXml.addChild(layer.toXML(georeferencingId));
			}

			return pageXML;
		}
	}

	private final ArrayList<File> filesToDelete = new ArrayList<File>();

	private String author;

	private String producer;
	private String creator;
	private String creationDate;
	private String subject;
	private String title;
	private String keywords;
	private ArrayList<GeoPDFPage> pages = new ArrayList<GeoPDF.GeoPDFPage>();
	private String javascript;

	private IXMLElement createLayerIndex(final GeoPDFLayer layer) {
		final IXMLElement newLayerXML = new XMLElement("Layer");
		newLayerXML.setAttribute("id", layer.getId());
		newLayerXML.setAttribute("name", layer.getName());
		for (final GeoPDFLayer child : layer.getChildren()) {
			newLayerXML.addChild(createLayerIndex(child));
		}
		return newLayerXML;
	}

	public GeoPDFPage createNewPage() {
		final GeoPDFPage newPage = new GeoPDFPage(pages.size() + 1);
		pages.add(newPage);
		return newPage;
	}

	public String getAuthor() {
		return author;
	}

	public String getCreationDate() {
		return creationDate;
	}

	public String getCreator() {
		return creator;
	}

	public ArrayList<File> getFilesToDelete() {
		return filesToDelete;
	}

	public String getJavascript() {
		return javascript;
	}

	public String getKeywords() {
		return keywords;
	}

	public ArrayList<GeoPDFPage> getPages() {
		return pages;
	}

	public String getProducer() {
		return producer;
	}

	public String getSubject() {
		return subject;
	}

	public String getTitle() {
		return title;
	}

	public void setAuthor(final String author) {
		this.author = author;
	}

	public void setCreationDate(final String creationDate) {
		this.creationDate = creationDate;
	}

	public void setCreator(final String creator) {
		this.creator = creator;
	}

	public void setJavascript(final String javascript) {
		this.javascript = javascript;
	}

	public void setKeywords(final String keywords) {
		this.keywords = keywords;
	}

	public void setPages(final ArrayList<GeoPDFPage> pages) {
		this.pages = pages;
	}

	public void setProducer(final String producer) {
		this.producer = producer;
	}

	public void setSubject(final String subject) {
		this.subject = subject;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		try {
			final OutputStream outputStream = new OutputStream() {

				private final StringBuilder builder = new StringBuilder();

				@Override
				public String toString() {
					return builder.toString();
				}

				@Override
				public void write(final int b) throws IOException {
					builder.append((char) b);
				}
			};

			final XMLWriter xmlWrite = new XMLWriter(outputStream);
			xmlWrite.write(toXML(), true);

			return outputStream.toString();
		} catch (final IOException e) {
			// This will never be called....
		}
		return null;
	}

	public IXMLElement toXML() throws IOException {
		final XMLElement pdfComposition = new XMLElement("PDFComposition");
		final XMLElement metadata = new XMLElement("Metadata");
		pdfComposition.addChild(metadata);
		final IXMLElement author = new XMLElement("Author");
		metadata.addChild(author);
		author.setContent(getAuthor());

		if (getJavascript() != null) {
			final IXMLElement javascript = new XMLElement("Javascript");
			javascript.setContent(getJavascript());
			pdfComposition.addChild(javascript);
		}

		final IXMLElement layerTree = new XMLElement("LayerTree");
		pdfComposition.addChild(layerTree);
		layerTree.setAttribute("displayOnlyOnVisiblePages", "true");

		for (final GeoPDFPage page : pages) {
			for (final GeoPDFLayer layer : page.getLayers()) {
				layerTree.addChild(createLayerIndex(layer));
			}
		}

		for (int i = 0; i < pages.size(); i++) {
			final GeoPDFPage currentPage = pages.get(i);
			pdfComposition.addChild(currentPage.toXML());
		}

		return pdfComposition;
	}

}

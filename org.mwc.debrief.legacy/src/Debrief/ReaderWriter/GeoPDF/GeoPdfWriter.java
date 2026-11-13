/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2026, Deep Blue C Technology Ltd
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

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.DeflaterOutputStream;

import javax.imageio.ImageIO;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDDocumentNameDictionary;
import org.apache.pdfbox.pdmodel.PDJavascriptNameTreeNode;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceGray;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.optionalcontent.PDOptionalContentGroup;
import org.apache.pdfbox.pdmodel.graphics.optionalcontent.PDOptionalContentProperties;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionJavaScript;
import org.apache.pdfbox.util.Matrix;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import Debrief.ReaderWriter.GeoPDF.GeoPDF.GeoPDFLayer;
import Debrief.ReaderWriter.GeoPDF.GeoPDF.GeoPDFLayerBackground;
import Debrief.ReaderWriter.GeoPDF.GeoPDF.GeoPDFLayerTrack;
import Debrief.ReaderWriter.GeoPDF.GeoPDF.GeoPDFLayerVector;
import Debrief.ReaderWriter.GeoPDF.GeoPDF.GeoPDFLayerVectorLabel;
import Debrief.ReaderWriter.GeoPDF.GeoPDF.GeoPDFPage;
import Debrief.ReaderWriter.GeoPDF.GeoPDF.GeoPDFRaster;
import MWC.GenericData.WorldArea;

/**
 * Writes a {@link GeoPDF} as a geospatial PDF, using PDFBox. It replaces GDAL's
 * {@code gdal_create} (with a composition file), and follows what that produced:
 * <ul>
 * <li>an ISO 32000 geospatial viewport ({@code /VP} with a {@code /GEO}
 * measure) in WGS84, so map readers show positions;</li>
 * <li>one optional content group (layer) per {@link GeoPDFLayer}, nested as the
 * layers are, so a reader can switch them on and off and the animation script
 * can find them by name;</li>
 * <li>vectors read from each layer's GeoJSON file and drawn with its OGR style
 * string ({@code PEN}, {@code SYMBOL} and {@code LABEL});</li>
 * <li>background images, already in lat/long;</li>
 * <li>the document-level animation JavaScript.</li>
 * </ul>
 * GDAL also wrote each feature's attributes into the PDF's logical structure.
 * That isn't reproduced: the attributes aren't shown on the map, and the
 * animation doesn't use them.
 */
public class GeoPdfWriter {

	public static class GeoPdfWriterTest extends junit.framework.TestCase {

		public void testLabelText() throws IOException {
			final JsonNode props = new ObjectMapper().readTree("{\"time_str\":\"0930\",\"name\":\"NELSON\"}");
			assertEquals("0930", labelText("{time_str}", props));
			assertEquals("NELSON at 0930", labelText("{name} at {time_str}", props));
			assertEquals("literal, with comma", labelText("\"literal, with comma\"", props));
			assertEquals("missing field gives nothing", "", labelText("{nope}", props));
		}

		public void testLoadImageKeepsGreyValues() throws IOException {
			final BufferedImage grey = new BufferedImage(4, 3, BufferedImage.TYPE_BYTE_GRAY);
			grey.getRaster().setSample(1, 1, 0, 82);
			final File png = File.createTempFile("geopdf-grey", ".png");
			try (PDDocument doc = new PDDocument()) {
				ImageIO.write(grey, "png", png);
				final PDImageXObject image = loadImage(doc, png);
				assertEquals("grey stays grey", PDDeviceGray.INSTANCE, image.getColorSpace());
				// PDFBox's own route would have lightened 82 to about 154
				assertEquals("raw grey value kept", 82, image.getRawRaster().getSample(1, 1, 0));
			} finally {
				png.delete();
			}
		}

		public void testPageMap() {
			final WorldArea area = new WorldArea(new MWC.GenericData.WorldLocation(10, 20, 0),
					new MWC.GenericData.WorldLocation(12, 24, 0));
			final PageMap map = new PageMap(area, 401, 201);
			// the corners map to (1,1) and (width,height), as GDAL's control points did
			assertEquals(1f, map.x(20), 1e-4);
			assertEquals(401f, map.x(24), 1e-4);
			assertEquals(1f, map.y(10), 1e-4);
			assertEquals(201f, map.y(12), 1e-4);
			assertEquals(201f, map.x(22), 1e-4);
		}

		public void testParseStyle() {
			final List<StyleTool> tools = parseStyle(
					"LABEL(t:\"A, B\",c: #0064bd,s:28pt,p:2,dy:10mm,bo:1);PEN(c:#e01c3e,w:5px)");
			assertEquals(2, tools.size());
			final StyleTool label = tools.get(0);
			assertEquals("LABEL", label.name);
			assertEquals("quoted text keeps its comma", "\"A, B\"", label.params.get("t"));
			assertEquals(new Color(0x00, 0x64, 0xbd), label.color(Color.BLACK));
			assertEquals(28, label.number("s", 0), 1e-9);
			assertEquals(10, label.number("dy", 0), 1e-9);
			final StyleTool pen = tools.get(1);
			assertEquals("PEN", pen.name);
			assertEquals(new Color(0xe0, 0x1c, 0x3e), pen.color(Color.BLACK));
			assertEquals(5, pen.number("w", 0), 1e-9);
			assertEquals("missing value falls back", 7, pen.number("s", 7), 1e-9);
		}
	}

	/** maps WGS84 lat/long to page coordinates, as GDAL's control points did */
	static final class PageMap {
		private final double lonMin, lonMax, latMin, latMax, width, height;

		PageMap(final WorldArea area, final double width, final double height) {
			lonMin = area.getBottomLeft().getLong();
			lonMax = area.getTopRight().getLong();
			latMin = area.getBottomLeft().getLat();
			latMax = area.getTopRight().getLat();
			this.width = width;
			this.height = height;
		}

		/** page units per degree of latitude */
		double yScale() {
			return (height - 1) / (latMax - latMin);
		}

		/** page units per degree of longitude */
		double xScale() {
			return (width - 1) / (lonMax - lonMin);
		}

		/**
		 * the page position of a lat/long. The page's corner points (1,1) and
		 * (width,height) are the area's corners: the control points the composition
		 * file gave GDAL.
		 */
		float x(final double lon) {
			return (float) (1 + (lon - lonMin) * xScale());
		}

		float y(final double lat) {
			return (float) (1 + (lat - latMin) * yScale());
		}
	}

	/**
	 * one tool of an OGR feature style string, such as {@code PEN(c:#ff0000,w:5px)}
	 */
	static final class StyleTool {
		final String name;
		final Map<String, String> params = new HashMap<>();

		StyleTool(final String name) {
			this.name = name;
		}

		Color color(final Color fallback) {
			final String c = params.get("c");
			if (c == null) {
				return fallback;
			}
			final String hex = c.trim().replace("#", "");
			if (hex.length() < 6) {
				return fallback;
			}
			return new Color(Integer.parseInt(hex.substring(0, 6), 16));
		}

		/** a numeric parameter, with any unit (px, pt, mm, g) dropped */
		double number(final String key, final double fallback) {
			final String v = params.get(key);
			if (v == null) {
				return fallback;
			}
			final String digits = v.trim().replaceAll("[a-zA-Z]+$", "");
			try {
				return Double.parseDouble(digits);
			} catch (final NumberFormatException e) {
				return fallback;
			}
		}
	}

	/**
	 * GDAL converts style sizes given in points or pixels (line widths, font sizes)
	 * by this factor, so we do the same to keep the output looking as it did
	 */
	static final double STYLE_SIZE_FACTOR = 25.4 / 72;

	/** GDAL draws symbols with a one-unit outline */
	private static final float SYMBOL_OUTLINE_WIDTH = 1f;

	/** Bezier control-point factor for drawing a circle with four curves */
	private static final float CIRCLE_KAPPA = 0.5523f;

	private static final String WGS84_WKT = "GEOGCS[\"GCS_WGS_1984\",DATUM[\"D_WGS_1984\",SPHEROID[\"WGS_1984\",6378137.0,298.257223563]],PRIMEM[\"Greenwich\",0.0],UNIT[\"Degree\",0.0174532925199433]]";

	/** split a style string into its tools: {@code PEN(...);LABEL(...)} */
	static List<StyleTool> parseStyle(final String style) {
		final List<StyleTool> tools = new ArrayList<>();
		if (style == null) {
			return tools;
		}
		int i = 0;
		while (i < style.length()) {
			final int open = style.indexOf('(', i);
			if (open < 0) {
				break;
			}
			final String name = style.substring(i, open).replace(";", "").trim();
			// find the matching close, ignoring brackets inside quotes
			int close = open + 1;
			boolean quoted = false;
			while (close < style.length() && (quoted || style.charAt(close) != ')')) {
				if (style.charAt(close) == '"') {
					quoted = !quoted;
				}
				close++;
			}
			final StyleTool tool = new StyleTool(name);
			for (final String param : splitParams(style.substring(open + 1, Math.min(close, style.length())))) {
				final int colon = param.indexOf(':');
				if (colon > 0) {
					tool.params.put(param.substring(0, colon).trim(), param.substring(colon + 1).trim());
				}
			}
			tools.add(tool);
			i = close + 1;
		}
		return tools;
	}

	/** split on commas that aren't inside quotes */
	private static List<String> splitParams(final String params) {
		final List<String> res = new ArrayList<>();
		final StringBuilder current = new StringBuilder();
		boolean quoted = false;
		for (final char c : params.toCharArray()) {
			if (c == '"') {
				quoted = !quoted;
				current.append(c);
			} else if (c == ',' && !quoted) {
				res.add(current.toString());
				current.setLength(0);
			} else {
				current.append(c);
			}
		}
		if (current.length() > 0) {
			res.add(current.toString());
		}
		return res;
	}

	/**
	 * the text of a label: a quoted literal, or {field} replaced by that feature
	 * property
	 */
	static String labelText(final String template, final JsonNode properties) {
		if (template == null) {
			return "";
		}
		String t = template.trim();
		if (t.startsWith("\"") && t.endsWith("\"") && t.length() >= 2) {
			t = t.substring(1, t.length() - 1);
		}
		final StringBuilder res = new StringBuilder();
		int i = 0;
		while (i < t.length()) {
			final int open = t.indexOf('{', i);
			final int close = open < 0 ? -1 : t.indexOf('}', open);
			if (open < 0 || close < 0) {
				res.append(t.substring(i));
				break;
			}
			res.append(t, i, open);
			final JsonNode value = properties == null ? null : properties.get(t.substring(open + 1, close));
			if (value != null && !value.isNull()) {
				res.append(value.asText());
			}
			i = close + 1;
		}
		return res.toString();
	}

	private final ObjectMapper mapper = new ObjectMapper();

	private final Map<GeoPDFLayer, PDOptionalContentGroup> groups = new HashMap<>();

	private void addLayerGroups(final GeoPDFLayer layer, final PDOptionalContentProperties properties,
			final COSArray order) {
		final PDOptionalContentGroup group = new PDOptionalContentGroup(layer.getName());
		properties.addGroup(group);
		groups.put(layer, group);
		order.add(group.getCOSObject());
		if (!layer.getChildren().isEmpty()) {
			final COSArray children = new COSArray();
			for (final GeoPDFLayer child : layer.getChildren()) {
				addLayerGroups(child, properties, children);
			}
			order.add(children);
		}
	}

	private void drawImage(final PDDocument doc, final PDPageContentStream content, final GeoPDFRaster raster,
			final PageMap map) throws IOException {
		final PDImageXObject image = loadImage(doc, raster.getImage());
		final WorldArea b = raster.getBounds();
		final float x0 = map.x(b.getBottomLeft().getLong());
		final float y0 = map.y(b.getBottomLeft().getLat());
		final float x1 = map.x(b.getTopRight().getLong());
		final float y1 = map.y(b.getTopRight().getLat());
		content.drawImage(image, x0, y0, x1 - x0, y1 - y0);
	}

	/**
	 * embed an image. PDFBox treats Java's greyscale images as linear and
	 * converts them to sRGB, which washes them out, so an 8-bit greyscale image
	 * is written with its raw grey values instead, as GDAL wrote them.
	 */
	static PDImageXObject loadImage(final PDDocument doc, final File file) throws IOException {
		final BufferedImage image = ImageIO.read(file);
		if (image == null) {
			throw new IOException("Can't read background image " + file);
		}
		final Raster raster = image.getRaster();
		if (raster.getNumBands() == 1 && raster.getSampleModel().getSampleSize(0) == 8
				&& !(image.getColorModel() instanceof IndexColorModel)) {
			final int w = image.getWidth();
			final int h = image.getHeight();
			final byte[] grey = new byte[w * h];
			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {
					grey[y * w + x] = (byte) raster.getSample(x, y, 0);
				}
			}
			final ByteArrayOutputStream compressed = new ByteArrayOutputStream();
			try (DeflaterOutputStream out = new DeflaterOutputStream(compressed)) {
				out.write(grey);
			}
			return new PDImageXObject(doc, new ByteArrayInputStream(compressed.toByteArray()), COSName.FLATE_DECODE,
					w, h, 8, PDDeviceGray.INSTANCE);
		}
		return LosslessFactory.createFromImage(doc, image);
	}

	private void drawLabel(final PDPageContentStream content, final StyleTool label, final JsonNode properties,
			final float px, final float py, final PageMap map) throws IOException {
		final String text = labelText(label.params.get("t"), properties);
		if (text.isEmpty()) {
			return;
		}
		final PDType1Font font = "1".equals(label.params.get("bo")) ? PDType1Font.HELVETICA_BOLD
				: PDType1Font.HELVETICA;
		// GDAL sized text in map units through the page's lat/long scaling, so it is
		// stretched vertically by the map's aspect ratio. Do the same.
		final float sizeX = (float) (label.number("s", 12) * STYLE_SIZE_FACTOR);
		final float sizeY = (float) (sizeX * map.yScale() / map.xScale());
		// GDAL measured text with the regular Helvetica widths, bold or not. Measure
		// the same way, so labels sit where they used to.
		final float width = PDType1Font.HELVETICA.getStringWidth(text) / 1000f * sizeX;
		final float capHeight = font.getFontDescriptor().getCapHeight() / 1000f * sizeX;
		final int anchor = (int) label.number("p", 1);
		final int column = (anchor - 1) % 3; // 0 left, 1 centre, 2 right
		final int row = (anchor - 1) / 3; // 0 bottom, 1 centre, 2 top, 3 baseline
		float x = px + (float) label.number("dx", 0);
		float y = py + (float) label.number("dy", 0);
		x -= column * width / 2;
		if (row == 1) {
			y -= capHeight / 2;
		} else if (row == 2) {
			y -= capHeight;
		}
		content.beginText();
		content.setNonStrokingColor(label.color(Color.BLACK));
		content.setFont(font, 1);
		content.setTextMatrix(new Matrix(sizeX, 0, 0, sizeY, x, y));
		content.showText(text);
		content.endText();
	}

	private void drawLayer(final PDDocument doc, final PDPageContentStream content, final GeoPDFLayer layer,
			final PageMap map) throws IOException {
		content.beginMarkedContent(COSName.OC, groups.get(layer));
		if (layer instanceof GeoPDFLayerBackground) {
			for (final GeoPDFRaster raster : ((GeoPDFLayerBackground) layer).getRasters()) {
				drawImage(doc, content, raster, map);
			}
		}
		if (layer instanceof GeoPDFLayerTrack) {
			final List<GeoPDFLayerVector> vectors = ((GeoPDFLayerTrack) layer).getVectors();
			// lines and symbols first, then labels on top of them, as GDAL did
			for (final GeoPDFLayerVector vector : vectors) {
				if (!(vector instanceof GeoPDFLayerVectorLabel)) {
					drawVector(content, vector, map);
				}
			}
			for (final GeoPDFLayerVector vector : vectors) {
				if (vector instanceof GeoPDFLayerVectorLabel) {
					drawVector(content, vector, map);
				}
			}
		}
		for (final GeoPDFLayer child : layer.getChildren()) {
			drawLayer(doc, content, child, map);
		}
		content.endMarkedContent();
	}

	private void drawLine(final PDPageContentStream content, final StyleTool pen, final JsonNode line,
			final PageMap map) throws IOException {
		if (line == null || line.size() < 2) {
			return;
		}
		content.setStrokingColor(pen.color(Color.BLACK));
		content.setLineWidth((float) (pen.number("w", 1) * STYLE_SIZE_FACTOR));
		content.setLineCapStyle(0);
		content.setLineJoinStyle(0);
		content.moveTo(map.x(line.get(0).get(0).asDouble()), map.y(line.get(0).get(1).asDouble()));
		for (int i = 1; i < line.size(); i++) {
			content.lineTo(map.x(line.get(i).get(0).asDouble()), map.y(line.get(i).get(1).asDouble()));
		}
		content.stroke();
	}

	private void drawSymbol(final PDPageContentStream content, final StyleTool symbol, final float x, final float y)
			throws IOException {
		// GDAL draws its symbols (ogr-sym-3 is a filled circle) with radius s
		final float r = (float) symbol.number("s", 2);
		final Color color = symbol.color(Color.BLACK);
		final float k = CIRCLE_KAPPA * r;
		content.setLineWidth(SYMBOL_OUTLINE_WIDTH);
		content.setStrokingColor(color);
		content.setNonStrokingColor(color);
		content.moveTo(x - r, y);
		content.curveTo(x - r, y - k, x - k, y - r, x, y - r);
		content.curveTo(x + k, y - r, x + r, y - k, x + r, y);
		content.curveTo(x + r, y + k, x + k, y + r, x, y + r);
		content.curveTo(x - k, y + r, x - r, y + k, x - r, y);
		content.closeAndFillAndStrokeEvenOdd();
	}

	private void drawVector(final PDPageContentStream content, final GeoPDFLayerVector vector, final PageMap map)
			throws IOException {
		final List<StyleTool> style = parseStyle(vector.getStyle());
		if (vector.getData() == null || style.isEmpty()) {
			return;
		}
		final JsonNode root = mapper.readTree(new File(vector.getData()));
		final JsonNode features = root.get("features");
		if (features == null) {
			return;
		}
		for (final JsonNode feature : features) {
			final JsonNode geometry = feature.get("geometry");
			if (geometry == null || geometry.isNull()) {
				continue;
			}
			final String type = geometry.path("type").asText();
			final JsonNode coords = geometry.get("coordinates");
			final JsonNode properties = feature.get("properties");
			for (final StyleTool tool : style) {
				switch (tool.name) {
				case "PEN":
					if ("LineString".equals(type)) {
						drawLine(content, tool, coords, map);
					} else if ("MultiLineString".equals(type)) {
						for (final JsonNode line : coords) {
							drawLine(content, tool, line, map);
						}
					}
					break;
				case "SYMBOL":
				case "LABEL":
					for (final JsonNode point : points(type, coords)) {
						final float x = map.x(point.get(0).asDouble());
						final float y = map.y(point.get(1).asDouble());
						if ("SYMBOL".equals(tool.name)) {
							drawSymbol(content, tool, x, y);
						} else {
							drawLabel(content, tool, properties, x, y, map);
						}
					}
					break;
				default:
					break;
				}
			}
		}
	}

	private static List<JsonNode> points(final String type, final JsonNode coords) {
		final List<JsonNode> res = new ArrayList<>();
		if (coords == null) {
			return res;
		}
		if ("Point".equals(type)) {
			res.add(coords);
		} else if ("MultiPoint".equals(type) || "LineString".equals(type)) {
			coords.forEach(res::add);
		} else if ("MultiLineString".equals(type)) {
			for (final JsonNode line : coords) {
				line.forEach(res::add);
			}
		}
		return res;
	}

	/** an ISO 32000 geospatial viewport, covering the whole page */
	private static COSDictionary viewport(final PageMap map, final float width, final float height)
			throws IOException {
		final COSDictionary gcs = new COSDictionary();
		gcs.setItem(COSName.TYPE, COSName.getPDFName("GEOGCS"));
		gcs.setInt(COSName.getPDFName("EPSG"), 4326);
		gcs.setItem(COSName.getPDFName("WKT"), new COSString(WGS84_WKT));

		// the four corner control points, as normalised page positions (LPTS) and
		// their latitudes and longitudes (GPTS)
		final float[][] corners = { { 1, 1 }, { 1, height }, { width, 1 }, { width, height } };
		final COSArray lpts = new COSArray();
		final COSArray gpts = new COSArray();
		for (final float[] c : corners) {
			lpts.add(new COSFloat(c[0] / width));
			lpts.add(new COSFloat(c[1] / height));
			// written at full (double) precision: a float would be out by centimetres
			gpts.add(new COSFloat(Double.toString(map.latMin + (c[1] - 1) / map.yScale())));
			gpts.add(new COSFloat(Double.toString(map.lonMin + (c[0] - 1) / map.xScale())));
		}
		final COSDictionary measure = new COSDictionary();
		measure.setItem(COSName.TYPE, COSName.getPDFName("Measure"));
		measure.setItem(COSName.SUBTYPE, COSName.getPDFName("GEO"));
		measure.setItem(COSName.getPDFName("GCS"), gcs);
		measure.setItem(COSName.getPDFName("LPTS"), lpts);
		measure.setItem(COSName.getPDFName("GPTS"), gpts);

		final COSArray bbox = new COSArray();
		bbox.add(COSInteger.ZERO);
		bbox.add(COSInteger.ZERO);
		bbox.add(new COSFloat(width));
		bbox.add(new COSFloat(height));
		final COSDictionary vp = new COSDictionary();
		vp.setItem(COSName.TYPE, COSName.getPDFName("Viewport"));
		vp.setItem(COSName.BBOX, bbox);
		vp.setString(COSName.NAME, "Layer");
		vp.setItem(COSName.getPDFName("Measure"), measure);
		return vp;
	}

	/** write the GeoPDF to a file */
	public void write(final GeoPDF geoPdf, final File output) throws IOException {
		try (PDDocument doc = new PDDocument()) {
			final PDDocumentCatalog catalog = doc.getDocumentCatalog();

			final PDDocumentInformation info = doc.getDocumentInformation();
			info.setAuthor(geoPdf.getAuthor());
			info.setTitle(geoPdf.getTitle());
			info.setSubject(geoPdf.getSubject());
			info.setKeywords(geoPdf.getKeywords());
			info.setCreator(geoPdf.getCreator());
			info.setProducer(geoPdf.getProducer());

			// the layers, and the tree a reader shows them in
			final PDOptionalContentProperties properties = new PDOptionalContentProperties();
			catalog.setOCProperties(properties);
			final COSArray order = new COSArray();
			for (final GeoPDFPage page : geoPdf.getPages()) {
				for (final GeoPDFLayer layer : page.getLayers()) {
					addLayerGroups(layer, properties, order);
				}
			}
			final COSDictionary defaults = (COSDictionary) properties.getCOSObject()
					.getDictionaryObject(COSName.D);
			defaults.setItem(COSName.getPDFName("Order"), order);
			defaults.setItem(COSName.getPDFName("ListMode"), COSName.getPDFName("VisiblePages"));

			for (final GeoPDFPage page : geoPdf.getPages()) {
				final float width = (float) page.getWidth();
				final float height = (float) page.getHeight();
				final PDPage pdfPage = new PDPage(new PDRectangle(width, height));
				doc.addPage(pdfPage);
				final PageMap map = new PageMap(pageArea(page), width, height);

				final COSArray viewports = new COSArray();
				viewports.add(viewport(map, width, height));
				pdfPage.getCOSObject().setItem(COSName.getPDFName("VP"), viewports);

				try (PDPageContentStream content = new PDPageContentStream(doc, pdfPage)) {
					for (final GeoPDFLayer layer : page.getLayers()) {
						drawLayer(doc, content, layer, map);
					}
				}
			}

			if (geoPdf.getJavascript() != null) {
				final PDJavascriptNameTreeNode scripts = new PDJavascriptNameTreeNode();
				scripts.setNames(Collections.singletonMap("Debrief", new PDActionJavaScript(geoPdf.getJavascript())));
				final PDDocumentNameDictionary names = new PDDocumentNameDictionary(catalog);
				names.setJavascript(scripts);
				catalog.setNames(names);
			}

			doc.save(output);
		}
	}

	/** the page's area, with the same default the composition file used */
	private static WorldArea pageArea(final GeoPDFPage page) {
		if (page.getArea() != null) {
			return page.getArea();
		}
		return new WorldArea(new MWC.GenericData.WorldLocation(50, -0.8, 0),
				new MWC.GenericData.WorldLocation(50.4, -0.1, 0));
	}
}

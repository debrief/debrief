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

import static org.junit.jupiter.api.Assertions.*;

import static Debrief.ReaderWriter.GeoPDF.GeoPdfWriter.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import Debrief.ReaderWriter.GeoPDF.GeoPdfWriter.*;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
import org.junit.jupiter.api.Test;
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

public class GeoPdfWriterTest {

	@Test
	public void testLabelText() throws IOException {
		final JsonNode props = new ObjectMapper().readTree("{\"time_str\":\"0930\",\"name\":\"NELSON\"}");
		assertEquals("0930", labelText("{time_str}", props));
		assertEquals("NELSON at 0930", labelText("{name} at {time_str}", props));
		assertEquals("literal, with comma", labelText("\"literal, with comma\"", props));
		assertEquals("", labelText("{nope}", props), "missing field gives nothing");
	}

	@Test
	public void testLoadImageKeepsGreyValues() throws IOException {
		final BufferedImage grey = new BufferedImage(4, 3, BufferedImage.TYPE_BYTE_GRAY);
		grey.getRaster().setSample(1, 1, 0, 82);
		final File png = File.createTempFile("geopdf-grey", ".png");
		try (PDDocument doc = new PDDocument()) {
			ImageIO.write(grey, "png", png);
			final PDImageXObject image = loadImage(doc, png);
			assertEquals(PDDeviceGray.INSTANCE, image.getColorSpace(), "grey stays grey");
			// PDFBox's own route would have lightened 82 to about 154
			assertEquals(82, image.getRawRaster().getSample(1, 1, 0), "raw grey value kept");
		} finally {
			png.delete();
		}
	}

	@Test
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

	@Test
	public void testParseStyle() {
		final List<StyleTool> tools = parseStyle(
				"LABEL(t:\"A, B\",c: #0064bd,s:28pt,p:2,dy:10mm,bo:1);PEN(c:#e01c3e,w:5px)");
		assertEquals(2, tools.size());
		final StyleTool label = tools.get(0);
		assertEquals("LABEL", label.name);
		assertEquals("\"A, B\"", label.params.get("t"), "quoted text keeps its comma");
		assertEquals(new Color(0x00, 0x64, 0xbd), label.color(Color.BLACK));
		assertEquals(28, label.number("s", 0), 1e-9);
		assertEquals(10, label.number("dy", 0), 1e-9);
		final StyleTool pen = tools.get(1);
		assertEquals("PEN", pen.name);
		assertEquals(new Color(0xe0, 0x1c, 0x3e), pen.color(Color.BLACK));
		assertEquals(5, pen.number("w", 0), 1e-9);
		assertEquals(7, pen.number("s", 7), 1e-9, "missing value falls back");
	}
}

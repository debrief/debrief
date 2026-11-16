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

import static Debrief.ReaderWriter.GeoPDF.AbstractGeoPDFBuilder.*;
import static org.junit.jupiter.api.Assertions.*;

import Debrief.ReaderWriter.GeoPDF.AbstractGeoPDFBuilder.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import com.fasterxml.jackson.core.JsonProcessingException;
import Debrief.GUI.Frames.Application;
import Debrief.ReaderWriter.GeoPDF.GeoPDF.GeoPDFLayerTrack;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Layers;
import MWC.GUI.ToolParent;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.WorldArea;
import org.junit.jupiter.api.Test;

public class AbstractGeoPDFBuilderTest {

//		private final static String boat1rep = "../org.mwc.cmap.combined.feature/root_installs/sample_data/boat1.rep";
//		private final static String boat2rep = "../org.mwc.cmap.combined.feature/root_installs/sample_data/boat2.rep";

	/**
	 * export sample.dpf as a GeoPDF, then check what a GeoPDF reader relies on:
	 * the layer tree, the geospatial viewport and the animation script. GeoPDF
	 * export used to need GDAL, so this couldn't run before (issue 4969).
	 */
	@Test
	public void testBuild() throws Exception {
			final java.util.TimeZone oldZone = java.util.TimeZone.getDefault();
			java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("GMT"));
			final File pdf = File.createTempFile("geopdf-test", ".pdf");
			try {
				final String plot = new String(java.nio.file.Files.readAllBytes(new File(
						"../org.mwc.cmap.combined.feature/root_installs/sample_data/sample.dpf").toPath()),
						java.nio.charset.StandardCharsets.UTF_8);
				final String layersXml = plot.substring(plot.indexOf("<layers"),
						plot.indexOf("</layers>") + "</layers>".length());
				final Layers layers = new Layers();
				new Debrief.ReaderWriter.XML.DebriefXMLReaderWriter(null).importThis("sample.dpf",
						new java.io.ByteArrayInputStream(layersXml.getBytes(java.nio.charset.StandardCharsets.UTF_8)),
						layers);

				final GeoPDFConfiguration configuration = new GeoPDFConfiguration();
				final TimePeriod period = layers.getTimePeriod();
				configuration.setStartTime(period.getStartDTG());
				configuration.setEndTime(period.getEndDTG());
				configuration.setStepDeltaMilliSeconds(30 * 60 * 1000L);
				configuration.setDateFormat("ddHHmm");
				configuration.setTempFolder("geopdf-test-" + System.currentTimeMillis());
				configuration.setPdfOutputPath(pdf.getAbsolutePath());

				final GeoPDFSegmentedBuilder builder = new GeoPDFSegmentedBuilder();
				final GeoPDF geoPdf = builder.build(layers, configuration);
				builder.generatePDF(geoPdf, configuration);

				try (org.apache.pdfbox.pdmodel.PDDocument doc = org.apache.pdfbox.pdmodel.PDDocument.load(pdf)) {
					assertEquals(1, doc.getNumberOfPages(), "one page");

					// layers: two tracks, the interactive group, 14 half-hour steps, and
					// each track within each step
					final java.util.List<String> names = new ArrayList<>();
					for (final org.apache.pdfbox.pdmodel.graphics.optionalcontent.PDOptionalContentGroup group : doc
							.getDocumentCatalog().getOCProperties().getOptionalContentGroups()) {
						names.add(group.getName());
					}
					assertEquals(2 + 1 + 14 + 14 * 2, names.size(), "layers");
					assertTrue(names.contains("NELSON" + NON_INTERACTIVE_SUFFIX));
					assertTrue(names.contains("COLLINGWOOD" + NON_INTERACTIVE_SUFFIX));
					assertTrue(names.contains(INTERACTIVE_LAYER_NAME));
					assertTrue(names.contains("120500"), "first time step");

					// the geospatial viewport covers the plot, in WGS84
					final org.apache.pdfbox.cos.COSDictionary viewport = (org.apache.pdfbox.cos.COSDictionary) ((org.apache.pdfbox.cos.COSArray) doc
							.getPage(0).getCOSObject().getDictionaryObject(org.apache.pdfbox.cos.COSName.getPDFName("VP")))
									.getObject(0);
					final org.apache.pdfbox.cos.COSDictionary measure = (org.apache.pdfbox.cos.COSDictionary) viewport
							.getDictionaryObject(org.apache.pdfbox.cos.COSName.getPDFName("Measure"));
					assertEquals("GEO", measure.getCOSName(org.apache.pdfbox.cos.COSName.SUBTYPE).getName());
					final org.apache.pdfbox.cos.COSDictionary gcs = (org.apache.pdfbox.cos.COSDictionary) measure
							.getDictionaryObject(org.apache.pdfbox.cos.COSName.getPDFName("GCS"));
					assertEquals(4326, gcs.getInt(org.apache.pdfbox.cos.COSName.getPDFName("EPSG")));
					final org.apache.pdfbox.cos.COSArray gpts = (org.apache.pdfbox.cos.COSArray) measure
							.getDictionaryObject(org.apache.pdfbox.cos.COSName.getPDFName("GPTS"));
					final WorldArea area = layers.getBounds();
					assertEquals(area.getBottomLeft().getLat(), ((org.apache.pdfbox.cos.COSNumber) gpts.getObject(0)).doubleValue(),
							1e-9, "first point is the bottom-left latitude");
					assertEquals(area.getBottomLeft().getLong(), ((org.apache.pdfbox.cos.COSNumber) gpts.getObject(1)).doubleValue(),
							1e-9, "and longitude");

					// the animation script, with its time steps filled in
					final String script = doc.getDocumentCatalog().getNames().getJavaScript().getNames()
							.get("Debrief").getAction();
					assertTrue(script.contains("\"ocg_name\" : \"120500\""), "script has its time steps");
					assertFalse(script.contains(JAVASCRIPT_TIMESTAMP_TAG), "placeholders replaced");

					// and something is drawn
					final java.awt.image.BufferedImage page = new org.apache.pdfbox.rendering.PDFRenderer(doc)
							.renderImageWithDPI(0, 36);
					int coloured = 0;
					for (int y = 0; y < page.getHeight(); y++) {
						for (int x = 0; x < page.getWidth(); x++) {
							final int rgb = page.getRGB(x, y) & 0xffffff;
							if (rgb != 0xffffff) {
								coloured++;
							}
						}
					}
					assertTrue(coloured > 500, "tracks drawn: " + coloured);
				}
			} finally {
				java.util.TimeZone.setDefault(oldZone);
				pdf.delete();
			}
		}

	@Test
	public void testCreateTempFile() throws FileNotFoundException {
			final File test = createTempFile("test.txt", "Test", "test");

			final Scanner scanner = new Scanner(test);
			assertEquals("Test", scanner.next(), "Correct file writted with data");
			scanner.close();

		}
	}

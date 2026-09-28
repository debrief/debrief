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
package Debrief.ReaderWriter.powerPoint.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.junit.Test;

import Debrief.ReaderWriter.powerPoint.DebriefException;
import Debrief.ReaderWriter.powerPoint.PlotTracks;
import Debrief.ReaderWriter.powerPoint.model.Track;
import Debrief.ReaderWriter.powerPoint.model.TrackData;
import Debrief.ReaderWriter.powerPoint.model.TrackPoint;
import net.lingala.zip4j.exception.ZipException;

public class PlotTracksTest {

	private static final String path = Utils.testFolder + File.separator + "PlotTracks";

	@Test
	public void coordinateTransformationTest() {
		final double exilon = 1e-5;

		final PlotTracks plotter = new PlotTracks();
		float[] result = plotter.coordinateTransformation(0.07269773f, 0.33273053f, 9144000.0f, 6858000.0f, .0f, .0f,
				1.0f, 1.0f, 0);
		assertTrue(Math.abs(result[0] - 7.95032E-9f) < exilon && Math.abs(result[1] - 4.8517137E-8f) < exilon);

		result = plotter.coordinateTransformation(664748.0f, 2281866.0f, 9144000.0f, 6858000.0f, .0f, .0f, 1.0f, 1.0f,
				0);
		assertTrue(Math.abs(result[0] - 0.07269773f) < exilon && Math.abs(result[1] - 0.33273053f) < exilon);

		result = plotter.coordinateTransformation(250.0f, 178.0f, 867.0f, 803.0f, 0.07269773f, 0.33273053f, 0.6098697f,
				0.6098697f, 1);
		assertTrue(Math.abs(result[0] - 0.24855405f) < exilon && Math.abs(result[1] - 0.8074112f) < exilon);
	}

	@Test
	public void retrieveMapTest() throws IOException, ZipException, DebriefException {
		final String prefix = "retrieveMap";
		final PlotTracks plotter = new PlotTracks();
		final HashMap<String, String> result = plotter
				.retrieveMapProperties(path + File.separator + prefix + File.separator + "donor.pptx");
		final HashMap<String, String> expectedResult = new HashMap<String, String>() {
			/**
			 * Known Result
			 */
			private static final long serialVersionUID = -4264437335359313998L;

			{
				put("cx", "439");
				put("cy", "318");
				put("name", "map");
				put("x", "52");
				put("y", "180");
			}
		};
		assertEquals(expectedResult, result);
	}

	@Test
	public void validateDonorFileTest() {
		final String prefix = "validateDonor";
		final String[] donors = new String[] { "correct.pptx", "missingPresentation.pptx", "missingSlides.pptx" };
		final String[] expectedResults = new String[] { null, "Corrupted presentation file", "Corrupted File" };

		for (int i = 0; i < donors.length; i++) {
			final PlotTracks plotTracks = new PlotTracks();
			final String result = plotTracks
					.validateDonorFile(path + File.separator + prefix + File.separator + donors[i]);
			assertEquals(result, expectedResults[i]);
		}
	}

	private static Track makeTrack(final String name, final int stepsToSkip, final int firstStep, final int numSteps) {
		final Track track = new Track(name, Color.RED, stepsToSkip);
		for (int i = firstStep; i < firstStep + numSteps; i++) {
			track.getPoints().add(new TrackPoint(100 + i, 100 + i * 5, 0, new Date(i * 60000L), "T" + i));
		}
		return track;
	}

	/**
	 * two tracks, as the recorder would produce them: B starts late (after two
	 * steps), and happens to be first in the list. A covers steps 0-3, B covers
	 * steps 2-4
	 */
	private static TrackData makeStaggeredTracks() {
		final TrackData td = new TrackData();
		td.setName("staggered");
		td.setIntervals(1000);
		td.setWidth(800);
		td.setHeight(600);
		td.getTracks().add(makeTrack("B", 2, 2, 3));
		td.getTracks().add(makeTrack("A", 0, 0, 4));
		td.getStepTimes().addAll(Arrays.asList("T0", "T1", "T2", "T3", "T4"));
		return td;
	}

	@Test
	public void stepTimesTest() {
		final TrackData td = makeStaggeredTracks();
		assertEquals(Arrays.asList("T0", "T1", "T2", "T3", "T4"), PlotTracks.getStepTimes(td));

		// without recorded step times (e.g. TrackParser data) use the first track
		td.getStepTimes().clear();
		assertEquals(Arrays.asList("T2", "T3", "T4"), PlotTracks.getStepTimes(td));
	}

	/**
	 * the time captions in the exported slide must be one per step, starting at
	 * the first step, not taken from whichever track is first in the list
	 */
	@Test
	public void timeCaptionsPerStepTest() throws IOException, ZipException, DebriefException {
		final TrackData td = makeStaggeredTracks();
		final File out = File.createTempFile("staggered", ".pptx");
		out.delete();
		final String donor = "../org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/master_template.pptx";
		final String exported = new PlotTracks().export(td, donor, out.getAbsolutePath());
		final List<String> captions = new ArrayList<>();
		try (final ZipFile zip = new ZipFile(exported)) {
			final ZipEntry slide = zip.getEntry("ppt/slides/slide1.xml");
			try (final InputStream is = zip.getInputStream(slide)) {
				final String xml = new String(is.readAllBytes(), StandardCharsets.UTF_8);
				final Matcher m = Pattern.compile("<a:t>(T\\d+)</a:t>").matcher(xml);
				while (m.find()) {
					captions.add(m.group(1));
				}
			}
		} finally {
			new File(exported).delete();
		}
		assertEquals(Arrays.asList("T0", "T1", "T2", "T3", "T4"), captions);
	}
}

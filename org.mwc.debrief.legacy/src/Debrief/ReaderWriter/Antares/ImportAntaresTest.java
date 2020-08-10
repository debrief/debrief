package Debrief.ReaderWriter.Antares;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Enumeration;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.Track.TrackSegment;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import junit.framework.TestCase;

public class ImportAntaresTest extends TestCase {

	public static void testCanRead() {
		final String inputFileContent = "TRACK/010101-0800/2512.0N-03010.5W/10/5/0//";

		final InputStream targetStream = new ByteArrayInputStream(inputFileContent.getBytes());

		assertTrue("Antares can read (valid file line)", ImportAntaresImpl.canLoadThisStream(targetStream));
	}

	public static void testCanRead2() {
		final String inputFileContent = "NELSON/010101-0800/2512.0N-03010.5W/10/5/0//";

		final InputStream targetStream = new ByteArrayInputStream(inputFileContent.getBytes());

		assertFalse("Antares can read (valid file line)", ImportAntaresImpl.canLoadThisStream(targetStream));
	}

	public static void testImportThis() {

		final String inputFileContent = "TRACK/010530Z/2512.0N-03010.5W/30/2/-5//\n"
				+ "TRACK/010532Z/2514.0N-03012.5W/31/3/-3//\n" + "TRACK/010534Z/2516.0N-03014.5W/35/4/-1//";

		final InputStream targetStream = new ByteArrayInputStream(inputFileContent.getBytes());
		final Layers layers = new Layers();

		ImportAntaresImpl.importThis(targetStream, layers, "TRACK_SAUL", 3, 95);

		assertEquals("Correct Layer Unit", 1, layers.size());
		final Layer saulTrack = layers.findLayer("TRACK_SAUL");

		final Enumeration<Editable> enume = saulTrack.elements();

		int totalFixes = 0;
		while (enume.hasMoreElements()) {
			final Editable nextEditable = enume.nextElement();
			assertTrue("Correct type created in the ImportAntares process", nextEditable instanceof TrackSegment);
			final TrackSegment trackSegment = (TrackSegment) nextEditable;

			final double[] expectedSpeed = new double[] {2, 3, 4};
			final double[] expectedCourse = new double[] {30, 31, 35};
			final double[] expectedDepth = new double[] {-5, -3, -1};
			final long[] expectedTime = new long[] {-59161602600000000L, -59161602480000000L, -59161602360000000L};
			final Enumeration<Editable> enumSegment = trackSegment.elements();
			while (enumSegment.hasMoreElements()) {
				final Editable nextFix = enumSegment.nextElement();
				assertTrue("Correct type created in the fix for ImportAntares process", nextFix instanceof FixWrapper);
				final FixWrapper fixWrapper = (FixWrapper) nextFix;

				assertEquals("Correct Depth ", expectedDepth[totalFixes], fixWrapper.getDepth(), 1e-8);

				assertEquals("Correct Course ", expectedCourse[totalFixes], fixWrapper.getCourse(), 1e-8);

				assertEquals("Correct Speed ", expectedSpeed[totalFixes], fixWrapper.getFix().getSpeed(), 1e-8);
				
				assertEquals("Correct DTG ", expectedTime[totalFixes], fixWrapper.getFix().getTime().getMicros());
				
				++totalFixes;
			}
		}

		assertEquals("Correct Amount of Fixes", 3, totalFixes);
	}
}

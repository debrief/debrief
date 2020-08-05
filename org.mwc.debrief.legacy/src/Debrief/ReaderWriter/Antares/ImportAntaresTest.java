package Debrief.ReaderWriter.Antares;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Enumeration;

import Debrief.Wrappers.FixWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import junit.framework.TestCase;

public class ImportAntaresTest extends TestCase{
	
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
				+ "TRACK/010532Z/2514.0N-03012.5W/30/2/-5//\n"
				+ "TRACK/010534Z/2516.0N-03014.5W/30/2/-5//";

		final InputStream targetStream = new ByteArrayInputStream(inputFileContent.getBytes());
		final Layers layers = new Layers();
		
		ImportAntaresImpl.importThis(targetStream, layers, "TRACK_SAUL", 3, 95);
		

		assertEquals("Correct Layer Unit", 1, layers.size());
		final Layer saulTrack = layers.findLayer("TRACK_SAUL");
		
		final Enumeration<Editable> enume = saulTrack.elements();
		
		int totalFixes = 0;
		while (enume.hasMoreElements()) {
			final Editable nextEditable = enume.nextElement();
			assertTrue("Correct type created in the ImportAntares process", nextEditable instanceof FixWrapper);
			final FixWrapper fixWrapper = (FixWrapper) nextEditable;
			
			assertEquals("Correct Depth ", -5.0, fixWrapper.getDepth(), 1e-8);
			
			assertEquals("Correct Course ", 30.0, fixWrapper.getCourse(), 1e-8);
			
			assertEquals("Correct Speed ", 2, fixWrapper.getFix().getSpeed(), 1e-8);
			++totalFixes;
		}
		
		assertEquals("Correct Amount of Fixes", 3, totalFixes);
	}
}

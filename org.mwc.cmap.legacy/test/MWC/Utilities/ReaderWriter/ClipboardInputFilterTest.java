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

package MWC.Utilities.ReaderWriter;

import static MWC.Utilities.ReaderWriter.ClipboardInputFilter.*;
import static org.junit.jupiter.api.Assertions.*;

import MWC.Utilities.ReaderWriter.ClipboardInputFilter.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputFilter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GUI.Chart.Painters.GridPainter;
import MWC.GenericData.HiResDate;
import MWC.TacticalData.NarrativeEntry;
import org.junit.jupiter.api.Test;

public class ClipboardInputFilterTest {

	private static byte[] serialise(final Object o) throws IOException {
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
			oos.writeObject(o);
		}
		return bos.toByteArray();
	}

	private static Object read(final byte[] bytes) throws IOException, ClassNotFoundException {
		try (ObjectInputStream ois = createStream(new ByteArrayInputStream(bytes))) {
			return ois.readObject();
		}
	}

	private static void assertRejected(final Object o) throws IOException, ClassNotFoundException {
		try {
			read(serialise(o));
			fail("should have been rejected:" + o.getClass());
		} catch (final InvalidClassException e) {
			assertTrue(e.getMessage().contains("REJECTED"), e.getMessage());
		}
	}

	/**
	 * allowed data passes (see also TrackWrapper_Test, for the Debrief wrappers)
	 */
	@Test
	public void testAllowsDebriefData() throws Exception {
		final BaseLayer layer = new BaseLayer();
		layer.setName("layer");
		layer.add(new NarrativeEntry("trk", new HiResDate(1000), "some text"));
		final GridPainter grid = new GridPainter();
		grid.setColor(java.awt.Color.RED);

		final Editable[] items = new Editable[] { layer, grid };
		final Editable[] res = (Editable[]) read(serialise(items));
		assertEquals(2, res.length);
		assertEquals("layer", res[0].getName());
		assertEquals(1, ((BaseLayer) res[0]).size());
		assertEquals(java.awt.Color.RED, ((GridPainter) res[1]).getColor());

		// primitive arrays are fine
		final double[] vals = (double[]) read(serialise(new double[] { 1d, 2d }));
		assertEquals(2, vals.length);
	}

	@Test
	public void testRejectsOtherClasses() throws Exception {
		// a Serializable JDK class that isn't in our allow-list (the root of the
		// well known URLDNS gadget)
		assertRejected(new java.net.URL("http://example.com"));

		// same, hidden inside an allowed collection or array
		final ArrayList<Object> list = new ArrayList<Object>();
		list.add(new java.io.File("tmp"));
		assertRejected(list);
		assertRejected(new Object[] { new javax.swing.JLabel("x") });
	}

	@Test
	public void testRejectsDeepGraphs() throws Exception {
		// nested collections, as used in serialisation denial-of-service attacks
		Set<Object> root = new HashSet<Object>();
		final Set<Object> top = root;
		for (int i = 0; i < 200; i++) {
			final Set<Object> child = new HashSet<Object>();
			root.add(child);
			root = child;
		}
		assertRejected(top);
	}

	@Test
	public void testRejectsHugeArrays() throws Exception {
		assertRejected(new byte[6000000]);
	}
}

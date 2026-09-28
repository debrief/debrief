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
import junit.framework.TestCase;

/**
 * Filter for Java objects read back from the system clipboard. Anything placed
 * on the clipboard can come from another process, so we only allow the Debrief
 * data classes (plus the JDK classes they use), and limit the size and depth of
 * the object graph. Anything else is rejected before it is instantiated.
 *
 * Use {@link #createStream(InputStream)} (or {@link #apply(ObjectInputStream)})
 * for every ObjectInputStream that reads clipboard contents.
 */
public final class ClipboardInputFilter {

	/**
	 * the filter, in {@link ObjectInputFilter.Config#createFilter(String)} syntax.
	 * Array classes are checked against their element type. Primitive values and
	 * arrays are always allowed (subject to the limits).
	 */
	public static final String PATTERN =
			// limits: depth of nested objects, array length, total stream size.
			// Real Debrief data measured at depth 16, and only small arrays
			"maxdepth=100;maxarray=5000000;maxbytes=268435456;"
					// Debrief data classes
					+ "Debrief.**;MWC.**;org.mwc.**;"
					// basic JDK types used in the wrappers
					+ "java.lang.Boolean;java.lang.Byte;java.lang.Character;java.lang.Short;java.lang.Integer;"
					+ "java.lang.Long;java.lang.Float;java.lang.Double;java.lang.Number;java.lang.String;"
					+ "java.lang.Enum;java.lang.Object;java.util.*;java.util.concurrent.ConcurrentSkipListMap;"
					+ "java.util.concurrent.ConcurrentSkipListSet;java.util.concurrent.ConcurrentHashMap;"
					+ "java.util.concurrent.CopyOnWriteArrayList;"
					// ConcurrentHashMap serialises through its segments and their locks
					+ "java.util.concurrent.ConcurrentHashMap$Segment;java.util.concurrent.locks.*;"
					// towed array datasets, held in sensor additional data
					+ "org.eclipse.january.**;"
					+ "java.awt.Color;java.awt.Font;"
					+ "java.awt.font.TextAttribute;java.text.AttributedCharacterIterator$Attribute;"
					+ "java.awt.Dimension;java.awt.Point;java.awt.Rectangle;java.awt.geom.*;"
					+ "java.beans.PropertyChangeSupport;java.text.*;java.math.*;sun.util.calendar.ZoneInfo;"
					// and nothing else
					+ "!*";

	public static final ObjectInputFilter FILTER = ObjectInputFilter.Config.createFilter(PATTERN);

	/**
	 * install the clipboard filter on this stream (before anything is read)
	 *
	 * @param stream the stream to protect
	 * @return the same stream
	 */
	public static ObjectInputStream apply(final ObjectInputStream stream) {
		stream.setObjectInputFilter(FILTER);
		return stream;
	}

	/**
	 * create an object stream that only accepts Debrief clipboard data
	 *
	 * @param in the raw clipboard bytes
	 * @return filtered stream
	 * @throws IOException if the stream header can't be read
	 */
	public static ObjectInputStream createStream(final InputStream in) throws IOException {
		return apply(new ObjectInputStream(in));
	}

	private ClipboardInputFilter() {
	}

	public static class ClipboardInputFilterTest extends TestCase {

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
				assertTrue(e.getMessage(), e.getMessage().contains("REJECTED"));
			}
		}

		/**
		 * allowed data passes (see also TrackWrapper_Test, for the Debrief wrappers)
		 */
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

		public void testRejectsHugeArrays() throws Exception {
			assertRejected(new byte[6000000]);
		}
	}
}

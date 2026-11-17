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

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputFilter;
import java.io.ObjectInputStream;


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
}

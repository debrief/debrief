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

package Debrief.ReaderWriter.Replay;

import java.awt.Color;
import java.text.ParseException;
import java.util.StringTokenizer;

import Debrief.Wrappers.ShapeWrapper;
import MWC.GUI.Shapes.PlainShape;
import MWC.GUI.Shapes.VectorShape;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.ReaderWriter.AbstractPlainLineImporter;

/**
 * class to parse a vector from a line of text
 */
final class ImportVector extends AbstractPlainLineImporter {
	/**
	 * the type for this string
	 */
	private final String _myType = ";VECTOR:";

	/**
	 * indicate if you can export this type of object
	 *
	 * @param val the object to test
	 * @return boolean saying whether you can do it
	 */
	@Override
	public final boolean canExportThis(final Object val) {
		boolean res = false;

		if (val instanceof ShapeWrapper) {
			final ShapeWrapper sw = (ShapeWrapper) val;
			final PlainShape ps = sw.getShape();
			res = (ps instanceof VectorShape);
		}

		return res;

	}

	/**
	 * export the specified shape as a string
	 *
	 * @return the shape in String form
	 * @param shape the Shape we are exporting
	 */
	@Override
	public final String exportThis(final MWC.GUI.Plottable theWrapper) {
		final ShapeWrapper theShape = (ShapeWrapper) theWrapper;

		final VectorShape vector = (VectorShape) theShape.getShape();

		// result value
		String line;

		line = _myType + " " + ImportReplay.replaySymbolFor(vector.getColor(), null) + "  ";

		line = line + " " + MWC.Utilities.TextFormatting.DebriefFormatLocation.toString(vector.getLine_Start());

		line = line + " " + vector.getDistance().getValueIn(WorldDistance.YARDS) + " " + vector.getBearing();

		line = line + " " + theWrapper.getName();

		return line;

	}

	/**
	 * determine the identifier returning this type of annotation
	 */
	@Override
	public final String getYourType() {
		return _myType;
	}

	/**
	 * read in this string and return a Label
	 */
	@Override
	public final Object readThisLine(final String theLine) {
		// get a stream from the string
		final StringTokenizer st = new StringTokenizer(theLine);

		// declare local variables
		WorldLocation start;

		// skip the comment identifier
		st.nextToken();

		// start with the symbology
		symbology = st.nextToken();

		try {
			// now the start location
			start = ImportLine.extractStart(st);

			final String range = st.nextToken();
			final WorldDistance distance = new WorldDistance(Double.parseDouble(range), WorldDistance.YARDS);
			final String bearingString = st.nextToken();

			String theText = "";
			// see if there are any more tokens waiting,
			if (st.hasMoreTokens()) {
				// and lastly read in the message
				theText = st.nextToken("\r").trim();
			}

			// create the Vector object
			final VectorShape sp = new VectorShape(start, Double.parseDouble(bearingString), distance);
			final Color c = ImportReplay.replayColorFor(symbology);
			sp.setColor(c);

			final WorldArea tmp = new WorldArea(start, sp.getLineEnd());
			tmp.normalise();

			// and put it into a shape
			final ShapeWrapper sw = new ShapeWrapper(theText, sp, c, null);

			return sw;
		} catch (final ParseException pe) {
			MWC.Utilities.Errors.Trace.trace(pe, "Whilst import Vector");
			return null;
		}
	}

}

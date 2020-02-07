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
package info.limpet.persistence.csv;

import java.awt.geom.Point2D;
import java.util.Date;
import java.util.Iterator;

import info.limpet.IDocument;
import info.limpet.IStoreItem;
import info.limpet.impl.LocationDocument;
import info.limpet.impl.NumberDocument;

public class CsvGenerator {
	private static final String LEFT_BRACKET = "[";

	private static final String RIGHT_BRACKET = "]";
	private static final String RIGHT_PARENTHESES = ")";
	private static final String LEFT_PARENTHESES = "(";
	private static final String COMMA_SEPARATOR = ",";
	private static final String LINE_SEPARATOR = "\n";

	private static void addUnit(final StringBuilder header, final IDocument<?> collection) {
		if (collection.isQuantity()) {
			header.append(LEFT_PARENTHESES);
			final NumberDocument nd = (NumberDocument) collection;
			final String unitSymbol = nd.getUnits().toString();
			// DEGREE_ANGLE
			if ("°".equals(unitSymbol)) {
				header.append("Degs");
			} else {
				header.append(unitSymbol);
			}
			header.append(RIGHT_PARENTHESES);
		}
	}

	public static String generate(final IStoreItem doc) {
		if (!(doc instanceof IDocument)) {
			return null;
		}
		final IDocument<?> collection = (IDocument<?>) doc;
		final StringBuilder header = new StringBuilder();
		if (collection.isIndexed()) {
			header.append("Time,");
		}
		if (collection instanceof LocationDocument) {
			header.append("Lat(Degs),Long(Degs)");
		} else {
			// "(" and "(" has special meaning in CsvParser (separate unit)
			// replace with "[" and "]"
			String name = collection.getName();
			name = name.replace(LEFT_PARENTHESES, LEFT_BRACKET);
			name = name.replace(RIGHT_PARENTHESES, RIGHT_BRACKET);
			header.append(name);
			addUnit(header, collection);
		}
		header.append(LINE_SEPARATOR);

		Iterator<Double> indexIterator = null;
		if (collection.isIndexed()) {
			indexIterator = collection.getIndexIterator();
		}

		if (collection instanceof LocationDocument) {
			final LocationDocument ldoc = (LocationDocument) collection;
			final Iterator<Point2D> locs = ldoc.getLocationIterator();
			while (locs.hasNext()) {
				if (indexIterator != null && indexIterator.hasNext()) {
					final double time = indexIterator.next();
					header.append(CsvParser.getDateFormat().format(new Date((long) time)));
					header.append(COMMA_SEPARATOR);
				}
				final Point2D point = locs.next();
				header.append(point.getY());
				header.append(COMMA_SEPARATOR);
				header.append(point.getX());
				header.append(LINE_SEPARATOR);
			}
		} else if (collection instanceof NumberDocument) {
			final NumberDocument ldoc = (NumberDocument) collection;
			final Iterator<Double> locs = ldoc.getIterator();
			while (locs.hasNext()) {
				if (indexIterator != null && indexIterator.hasNext()) {
					final double time = indexIterator.next();
					header.append(CsvParser.getDateFormat().format(new Date((long) time)));
					header.append(COMMA_SEPARATOR);
				}
				final double point = locs.next();
				header.append(point);
				header.append(LINE_SEPARATOR);
			}
		}

		return header.toString();
	}

	/**
	 * prevent accidental instance declaration
	 *
	 */
	protected CsvGenerator() {

	}
}

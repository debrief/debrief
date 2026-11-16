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

package org.mwc.cmap.xyplot.views;

import java.awt.Color;
import java.beans.PropertyChangeSupport;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.lang.reflect.Field;

import javax.swing.event.EventListenerList;

import org.jfree.data.general.Series;
import org.jfree.data.general.SeriesChangeListener;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYSeriesCollection;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.StaxDriver;


/**
 * The XStream set-up {@link XYPlotView} uses to keep a plot's dataset and
 * formatter in the workbench memento, so a plot survives a restart. Held in
 * one place so that an XStream upgrade (and its security allow-list) changes
 * one class, and so the round trip can be tested without building the view.
 */
public final class XYPlotStateXml {

	/**
	 * the types a memento may hold, beyond the plain Java ones (strings, numbers,
	 * collections) that XStream allows by default: JFreeChart's datasets and
	 * series, Debrief's data items and formatters, and colours. Anything else in
	 * the memento is refused rather than instantiated.
	 */
	private static final String[] ALLOWED_WILDCARDS = { "org.jfree.**", "MWC.GUI.JFreeChart.**" };

	private static final Class<?>[] ALLOWED_TYPES = { Color.class };

	/**
	 * a JFreeChart series holds its listeners in fields that aren't transient: a
	 * Swing EventListenerList and java.beans change supports. Java 21 doesn't let
	 * XStream reach into those, so they are left out of the memento, and
	 * {@link #readDataset} wires the series up again after reading.
	 */
	private static final String[] SERIES_LISTENER_FIELDS = { "listeners", "propertyChangeSupport",
			"vetoableChangeSupport" };

	private static XStream configure(final XStream xs) {
		for (final String field : SERIES_LISTENER_FIELDS) {
			xs.omitField(Series.class, field);
		}
		return xs;
	}

	/** the XStream that reads a dataset, formatter or attribute back from the memento */
	public static XStream reader() {
		final XStream xs = configure(new XStream(new DomDriver()));
		xs.allowTypesByWildcard(ALLOWED_WILDCARDS);
		xs.allowTypes(ALLOWED_TYPES);
		return xs;
	}

	/** the XStream that writes a dataset, formatter or attribute into the memento */
	public static XStream writer() {
		return configure(new XStream(new StaxDriver()));
	}

	/**
	 * read a dataset written by {@link #writer()}, and give each series new,
	 * empty listener lists, listened to by its collection, as
	 * {@code addSeries()} would have left it
	 */
	public static Object readDataset(final XStream reader, final String xml) {
		final Object res = reader.fromXML(xml);
		if (res instanceof TimeSeriesCollection) {
			final TimeSeriesCollection tsc = (TimeSeriesCollection) res;
			for (int i = 0; i < tsc.getSeriesCount(); i++) {
				relink(tsc.getSeries(i), tsc);
			}
		} else if (res instanceof XYSeriesCollection) {
			final XYSeriesCollection xyc = (XYSeriesCollection) res;
			for (int i = 0; i < xyc.getSeriesCount(); i++) {
				relink(xyc.getSeries(i), xyc);
			}
		}
		return res;
	}

	private static void relink(final Series series, final Object collection) {
		try {
			setField(series, "listeners", new EventListenerList());
			setField(series, "propertyChangeSupport", new PropertyChangeSupport(series));
			setField(series, "vetoableChangeSupport", new VetoableChangeSupport(series));
		} catch (final ReflectiveOperationException e) {
			throw new IllegalStateException("Can't restore the listeners of series " + series.getKey(), e);
		}
		series.addChangeListener((SeriesChangeListener) collection);
		if (collection instanceof VetoableChangeListener) {
			series.addVetoableChangeListener((VetoableChangeListener) collection);
		}
	}

	private static void setField(final Object target, final String name, final Object value)
			throws ReflectiveOperationException {
		final Field f = Series.class.getDeclaredField(name);
		f.setAccessible(true);
		f.set(target, value);
	}

	private XYPlotStateXml() {
	}
}

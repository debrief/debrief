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

package com.planetmayo.debrief.satc.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.DefaultXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.planetmayo.debrief.satc.model.states.LocationRange;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

public class StraightLineCullingTestForm extends JFrame {
	private static final long serialVersionUID = 1L;

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				final StraightLineCullingTestForm form = new StraightLineCullingTestForm();
				form.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
				form.setVisible(true);
			}
		});
	}

	private XYSeriesCollection collections;
	private JFreeChart chart;

	private final JFileChooser fc = new JFileChooser();

	public StraightLineCullingTestForm() throws HeadlessException {
		super("Straight line culling");
		createChart();
		createMenu();

		pack();
		setLocationRelativeTo(null);
	}

	private void createChart() {
		collections = new XYSeriesCollection();
		chart = ChartFactory.createXYLineChart("main", "x", "y", collections, PlotOrientation.VERTICAL, false, false,
				false);

		final ChartPanel panel = new ChartPanel(chart);
		panel.setPreferredSize(new Dimension(800, 600));
		setContentPane(panel);
	}

	private void createMenu() {
		final JMenu menu = new JMenu("File");
		menu.add(new AbstractAction("Load") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				if (fc.showOpenDialog(StraightLineCullingTestForm.this) == JFileChooser.APPROVE_OPTION) {
					try {
						loadFile(fc.getSelectedFile());
					} catch (final IOException ex) {
					}
				}
			}
		});

		final JMenuBar menuBar = new JMenuBar();
		menuBar.add(menu);
		setJMenuBar(menuBar);
	}

	private void culling(final List<List<Coordinate>> polygons) {
		final DefaultXYItemRenderer renderer = new DefaultXYItemRenderer();
		renderer.setDefaultShapesVisible(false);
		((XYPlot) chart.getPlot()).setRenderer(renderer);
		collections.removeAllSeries();

		final List<LocationRange> ranges = new ArrayList<LocationRange>(polygons.size());
		final GeometryFactory factory = new GeometryFactory();
		for (final List<Coordinate> coordinates : polygons) {
			final Geometry geo = factory.createPolygon(coordinates.toArray(new Coordinate[0]));
			ranges.add(new LocationRange(geo));
		}
		final StraightLineCulling culling = new StraightLineCulling(ranges);
		culling.process();

		int i = 0;
		for (final Geometry geometry : culling.getFiltered()) {
			final XYSeries series = new XYSeries("polygon " + (++i), false);
			for (final Coordinate c : geometry.getCoordinates()) {
				series.add(c.x, c.y);
			}
			collections.addSeries(series);
		}
		if (culling.hasResults()) {
			drawResultLineAndPolygon(1, culling.getFirstCrissCrossLine(), culling.getConstrainedStart());
			drawResultLineAndPolygon(2, culling.getSecondCrissCrossLine(), culling.getConstrainedEnd());
		}
	}

	private void drawResultLineAndPolygon(final int num, final Coordinate[] line, final Geometry geometry) {
		final XYItemRenderer renderer = ((XYPlot) chart.getPlot()).getRenderer();

		final XYSeries lineSeries = new XYSeries("line " + num);
		lineSeries.add(line[0].x, line[0].y);
		lineSeries.add(line[1].x, line[1].y);
		collections.addSeries(lineSeries);

		final XYSeries polygonSeries = new XYSeries("result " + num, false);
		for (final Coordinate c : geometry.getCoordinates()) {
			polygonSeries.add(c.x, c.y);
		}
		collections.addSeries(polygonSeries);

		renderer.setSeriesPaint(collections.getSeriesCount() - 2, Color.MAGENTA);
		renderer.setSeriesStroke(collections.getSeriesCount() - 2, new BasicStroke(1.0f));
		renderer.setSeriesPaint(collections.getSeriesCount() - 1, Color.BLACK);
		renderer.setSeriesStroke(collections.getSeriesCount() - 1, new BasicStroke(3.0f));
	}

	@SuppressWarnings("resource")
	private void loadFile(final File file) throws IOException {
		final Scanner scanner = new Scanner(file);
		scanner.useLocale(Locale.US);
		final List<List<Coordinate>> coordinates = new ArrayList<List<Coordinate>>();
		while (true) {
			try {
				int num = scanner.nextInt();
				scanner.next();
				final double x = scanner.nextDouble();
				final double y = scanner.nextDouble();

				if (num > 0) {
					num--;
					while (coordinates.size() <= num) {
						coordinates.add(new ArrayList<Coordinate>());
					}
					final Coordinate coordinate = new Coordinate(x, y);
					coordinates.get(num).add(coordinate);
				}
			} catch (final NoSuchElementException ex) {
				break;
			}
		}
		for (int i = 0; i < coordinates.size(); i++) {
			final Coordinate c = coordinates.get(i).get(0);
			coordinates.get(i).add(c);
		}
		culling(coordinates);
	}

}

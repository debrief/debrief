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

package com.planetmayo.debrief.satc.model.generator.impl.sa;

import java.util.Random;

import com.planetmayo.debrief.satc.util.GeoSupport;
import com.planetmayo.debrief.satc.util.MathUtils;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

public class PointsGenerator {
	private final Random rnd;
	private final Geometry geometry;
	private final Geometry envelope;
	private final SAParameters parameters;

	private double width;
	private double height;
	private double startX;
	private double startY;
	private double[][] edges;

	public PointsGenerator(final Geometry geometry, final Random random, final SAParameters parameters) {
		this.rnd = random;
		this.geometry = geometry;
		this.envelope = geometry.getEnvelope();
		this.parameters = parameters;

		calcParameters();
	}

	private void calcParameters() {
		double endX, endY;
		startX = startY = Double.MAX_VALUE;
		endX = endY = -Double.MAX_VALUE;
		final Coordinate[] coords = envelope.getCoordinates();
		for (final Coordinate c : coords) {
			startX = Math.min(startX, c.x);
			endX = Math.max(endX, c.x);
			startY = Math.min(startY, c.y);
			endY = Math.max(endY, c.y);
		}
		width = endX - startX;
		height = endY - startY;

		edges = new double[coords.length][2];
		for (int i = 0; i < coords.length; i++) {
			final Coordinate a = coords[i];
			final Coordinate b = coords[(i + 1) % coords.length];
			edges[i][0] = Math.min(a.y, b.y);
			edges[i][1] = Math.max(a.y, b.y);
		}
	}

	private double distance(final double T) {
		return parameters.getSaFunctions().neighborDistance(parameters, rnd, T);
	}

	private double distance(final double T, final double a, final double b) {
		final double x = distance(T);
		if (x < 0) {
			return x * (-a);
		}
		return x * (b);
	}

	public double findX(final int edgeNum, final double y) {
		Coordinate a = envelope.getCoordinates()[edgeNum];
		Coordinate b = envelope.getCoordinates()[(edgeNum + 1) % envelope.getNumPoints()];
		if (b.y > a.y) {
			final Coordinate t = b;
			b = a;
			a = t;
		}
		final double t = (y - a.y) / (b.y - a.y);
		return t * (b.x - a.x) + a.x;
	}

	public double getArea() {
		return geometry.getArea();
	}

	public Point newPoint(final Point p, final double T) {
		if (T < 0.3) {
			return waltToVertexes(p, T);
		}
		return walkThroughCoords(p, T);
	}

	public Point startPoint() {
		return envelope.getCentroid();
	}

	public Point toPoint(final Coordinate coord) {
		return GeoSupport.getFactory().createPoint(coord);
	}

	public Point walkThroughCoords(final Point p, final double T) {
		final double y1 = (startY - p.getY()) / height;
		final double y2 = 1 + y1;
		final double yCoef = distance(T, y1, y2);
		final double y = p.getY() + yCoef * height;
		int startEdge = -1, endEdge = -1;
		for (int i = 0; i < edges.length; i++) {
			if (y >= edges[i][0] && y <= edges[i][1] && edges[i][0] != edges[i][1]) {
				if (startEdge == -1) {
					startEdge = i;
				} else {
					endEdge = i;
					break;
				}
			}
		}
		double xMin = findX(startEdge, y);
		double xMax = findX(endEdge, y);
		if (xMin > xMax) {
			final double t = xMin;
			xMin = xMax;
			xMax = t;
		}
		if (p.getX() >= xMin && p.getX() <= xMax) {
			final double x1 = (xMin - p.getX()) / width;
			final double x2 = (xMax - p.getX()) / width;
			final double xCoef = distance(T, x1, x2);
			return GeoSupport.createPoint(p.getX() + xCoef * width, y);
		}
		return GeoSupport.createPoint(xMin + rnd.nextDouble() * (xMax - xMin), y);
	}

	public Point waltToVertexes(final Point p, final double T) {
		final int coords = envelope.getNumPoints();
		final Coordinate border1 = envelope.getCoordinates()[rnd.nextInt(coords)];
		Coordinate border2;
		do {
			border2 = envelope.getCoordinates()[rnd.nextInt(coords)];
		} while (border1 == border2);

		final Point p1 = MathUtils.calculateBezier(Math.abs(distance(T * T)), p, toPoint(border1), null);
		final Point p2 = MathUtils.calculateBezier(Math.abs(distance(T * T)), p, toPoint(border2), null);
		return MathUtils.calculateBezier(rnd.nextDouble(), p1, p2, null);
	}
}

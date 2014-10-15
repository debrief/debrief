/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package com.planetmayo.debrief.satc.model.states;

import org.junit.Test;

import com.planetmayo.debrief.satc.model.ModelTestBase;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.util.GeoSupport;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import static org.junit.Assert.*;

public class LocationRangeTest extends ModelTestBase
{
	
	@Test
	public void testCreate() throws IncompatibleStateException 
	{
		Coordinate[] array = {new Coordinate(0, 0), new Coordinate(1, 0), 
				new Coordinate(1, 1), new Coordinate(0, 1), new Coordinate(0, 0)};
		Geometry polygon = GeoSupport.getFactory().createPolygon(array);
		LocationRange range = new LocationRange(polygon);
		assertEquals("polygons aren't equal", polygon, range.getGeometry());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testCreateWithNull() throws Exception {
		new LocationRange((Geometry) null);
	}
	
	@Test
	public void testCloneCreate() throws Exception {
		Coordinate[] array = {new Coordinate(0, 0), new Coordinate(1, 0), 
				new Coordinate(1, 1), new Coordinate(0, 1), new Coordinate(0, 0)};
		Geometry polygon = GeoSupport.getFactory().createPolygon(array);
		LocationRange range1 = new LocationRange(polygon);
		LocationRange range2 = new LocationRange(range1);
		assertTrue(range1.getGeometry().equals(range2.getGeometry()));
	}
	
	@Test
	public void testConstraintToContinuous() throws IncompatibleStateException
	{
		Coordinate[] squareCoords = {new Coordinate(0, 0), new Coordinate(1, 0), 
				new Coordinate(1, 1), new Coordinate(0, 1), new Coordinate(0, 0)};
		Coordinate[] triangleCoords = {new Coordinate(0, 0), new Coordinate(1, 0),
				new Coordinate(1, 1), new Coordinate(0, 0)};
		
		Geometry square = GeoSupport.getFactory().createPolygon(squareCoords);
		Geometry triangle = GeoSupport.getFactory().createPolygon(triangleCoords);
		LocationRange range1 = new LocationRange(square);
		LocationRange range2 = new LocationRange(triangle);
		
		assertEquals("square area must be 1", 1d, range1.getGeometry().getArea(), EPS);		
		range1.constrainTo(range2);
		Point centroid = range1.getGeometry().getCentroid();
		
		assertEquals("square wasn't split on two triangles", Polygon.class, range1.getGeometry().getClass());
		assertEquals("triangle area must be 1/2", 0.5d, range1.getGeometry().getArea(), EPS);
		assertEquals("center of triangle must (2/3, 1/3)", 0.666666667d, centroid.getX(), EPS);
		assertEquals("center of triangle must (2/3, 1/3)", 0.333333333d, centroid.getY(), EPS);
	}
	
	@Test(expected = IncompatibleStateException.class)
	public void testConstraintToIncompatible() throws IncompatibleStateException
	{
		Coordinate[] squareCoords = {new Coordinate(0, 0), new Coordinate(1, 0), 
				new Coordinate(1, 1), new Coordinate(0, 1), new Coordinate(0, 0)};
		Coordinate[] triangleCoords = {new Coordinate(2, 2), new Coordinate(3, 2),
				new Coordinate(3, 3), new Coordinate(2, 2)};
		
		Geometry square = GeoSupport.getFactory().createPolygon(squareCoords);
		Geometry triangle = GeoSupport.getFactory().createPolygon(triangleCoords);
		new LocationRange(square).constrainTo(new LocationRange(triangle));
	}
	
	@Test
	public void testConstraintToNonContinuous() throws IncompatibleStateException
	{
		Coordinate[] squareCoords = {new Coordinate(0, 0), new Coordinate(1, 0), 
				new Coordinate(1, 1), new Coordinate(0, 1), new Coordinate(0, 0)};
		Coordinate[] polygonCoords = {new Coordinate(-1, 0), new Coordinate(0.5, 0), new Coordinate(0.5, 0.33),
				new Coordinate(0, 0.33), new Coordinate(0, 0.67), new Coordinate(0.5, 0.67), new Coordinate(0.5, 1),
				new Coordinate(-1, 1), new Coordinate(-1, 0)};
		
		Geometry square = GeoSupport.getFactory().createPolygon(squareCoords);
		Geometry polygon = GeoSupport.getFactory().createPolygon(polygonCoords);
		LocationRange range1 = new LocationRange(square);
		LocationRange range2 = new LocationRange(polygon);
		range1.constrainTo(range2);
		
		assertEquals("GeometryCollection is used for non continuous range", GeometryCollection.class, range1.getGeometry().getClass());
		assertEquals("GeometryCollection has three geometries: (line and 2 polygons)", 3, range1.getGeometry().getNumGeometries());
	}	
}

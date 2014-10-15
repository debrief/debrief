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
package com.planetmayo.debrief.satc.util.calculator;

import java.awt.geom.Point2D;

import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.coordinate.Position;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.datum.Ellipsoid;
import org.opengis.referencing.operation.TransformException;

public interface GeodeticCalculator
{
	
	CoordinateReferenceSystem getCoordinateReferenceSystem();
	
	GeographicCRS getGeographicCRS();
	
	Ellipsoid getEllipsoid();
	
	void setStartingGeographicPoint(double longitude, double latitude) throws IllegalArgumentException;
	
	void setStartingGeographicPoint(final Point2D point) throws IllegalArgumentException;
	
	void setStartingPosition(final Position position) throws TransformException;
	
	Point2D getStartingGeographicPoint();
	
	DirectPosition getStartingPosition() throws TransformException;
	
	void setDestinationGeographicPoint(double longitude, double latitude) throws IllegalArgumentException;
	
	void setDestinationGeographicPoint(final Point2D point) throws IllegalArgumentException;
	
	void setDestinationPosition(final Position position) throws TransformException;
	
	Point2D getDestinationGeographicPoint() throws IllegalStateException;
	
	DirectPosition getDestinationPosition() throws TransformException;
	
	void setDirection(double azimuth, final double distance) throws IllegalArgumentException;
	
	double getAzimuth() throws IllegalStateException;
	
	double getOrthodromicDistance() throws IllegalStateException;
	
	double getMeridianArcLength(final double latitude1, final double latitude2);
	
}

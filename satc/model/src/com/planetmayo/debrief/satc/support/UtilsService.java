package com.planetmayo.debrief.satc.support;

import java.util.Date;
import java.util.TreeSet;

import com.planetmayo.debrief.satc.model.GeoPoint;

public interface UtilsService
{

	String formatDate(String pattern, Date date);

	Date parseDate(String pattern, String text);

	<T> T higherElement(TreeSet<T> set, T currentElement);

	String formatGeoPoint(GeoPoint geoPoint);
}

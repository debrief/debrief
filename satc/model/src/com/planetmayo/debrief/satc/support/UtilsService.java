package com.planetmayo.debrief.satc.support;

import java.util.Date;
import java.util.TreeSet;

public interface UtilsService
{

	String formatDate(String pattern, Date date);

	Date parseDate(String pattern, String text);
	
	<T> T higherElement(TreeSet<T> set, T currentElement);
}

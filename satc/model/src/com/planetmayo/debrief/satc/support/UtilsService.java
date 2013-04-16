package com.planetmayo.debrief.satc.support;

import java.util.Date;
import java.util.Set;
import java.util.SortedSet;

public interface UtilsService
{

	String formatDate(String pattern, Date date);

	Date parseDate(String pattern, String text);

	<T> T higherElement(SortedSet<T> set, T currentElement);
	
	<T> SortedSet<T> newConcurrentSortedSet();
	
	<T> Set<T> newConcurrentSet();
}

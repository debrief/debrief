package com.planetmayo.debrief.satc.support;

import java.util.Date;
import java.util.TreeSet;

public interface UtilsService
{

	public abstract String formatDate(String pattern, Date date);

	public abstract Date parseDate(String pattern, String text);

	public abstract <T> T higherElement(TreeSet<T> set, T currentElement);
}

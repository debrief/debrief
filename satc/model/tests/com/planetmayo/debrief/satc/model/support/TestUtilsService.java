package com.planetmayo.debrief.satc.model.support;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import com.planetmayo.debrief.satc.support.UtilsService;

public class TestUtilsService implements UtilsService
{

	@Override
	public String formatDate(String pattern, Date date)
	{
		return new SimpleDateFormat(pattern).format(date);
	}

	@Override
	public Date parseDate(String pattern, String text)
	{
		try
		{
			return new SimpleDateFormat(pattern).parse(text);
		} 
		catch (ParseException ex) 
		{
			return null;
		}
	}
	
	@Override
	public <T> T higherElement(SortedSet<T> set, T currentElement)
	{
		if (set == null || set.isEmpty())
		{
			return null;
		}
		if (currentElement == null) 
		{
			return set.first();
		}
		if (set instanceof NavigableSet) 
		{
			NavigableSet<T> navigable = (NavigableSet<T>) set;
			return navigable.higher(currentElement);
		} 
		else 
		{
			SortedSet<T> tail = set.tailSet(currentElement);
			if (tail.size() <= 1)
			{
				return null;
			}
			Iterator<T> iterator = tail.iterator();
			iterator.next();
			return iterator.next();
		}
	}

	@Override
	public <T> SortedSet<T> newConcurrentSortedSet()
	{
		return new ConcurrentSkipListSet<T>();
	}

	@Override
	public <T> Set<T> newConcurrentSet()
	{
		return Collections.newSetFromMap(new ConcurrentHashMap<T, Boolean>());
	}
	
}

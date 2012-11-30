package com.planetmayo.debrief.satc.util;

public class ObjectUtils
{
	
	public static <T> T safe(T object, T def) 
	{
		return object == null ? def : object;
	}
}

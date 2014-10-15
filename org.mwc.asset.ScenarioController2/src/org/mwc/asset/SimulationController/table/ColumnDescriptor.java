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
package org.mwc.asset.SimulationController.table;

public class ColumnDescriptor
{

	private final String myName;

	private final int myIndex;

	private boolean myIsVisible;

	public ColumnDescriptor(final String name, final int index, final boolean visible)
	{
		myName = name;
		myIndex = index;
		myIsVisible = visible;
	}

	public int getIndex()
	{
		return myIndex;
	}

	public String getName()
	{
		return myName;
	}

	public boolean isVisible()
	{
		return myIsVisible;
	}

	public void setVisible(final boolean visible)
	{
		myIsVisible = visible;
	}
}

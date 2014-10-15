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
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package MWC.GUI.S57.features;

import MWC.GUI.CanvasType;
import MWC.GUI.Plottable;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

public abstract class S57Feature implements Plottable
{

	private boolean _visible = true;

	protected EditorType _myEditor = null;

	final private String _myName;

	final private Double _minScale;

	public S57Feature(final String name, final Double minScale)
	{
		_myName = name;
		_minScale = minScale;
	}

	public WorldArea getBounds()
	{
		return null;
	}

	/**
	 * actually do the paint operation
	 * 
	 * @param dest
	 */
	abstract void doPaint(CanvasType dest);

	/**
	 * @param dest
	 */
	final public void paint(final CanvasType dest)
	{
		// are we visible
		if (_visible)
		{
			final boolean inScale = true;

			if (_minScale != null)
			{
				// sort out the scale
//				double worldVal = dest.getProjection().getVisibleDataArea().getFlatEarthWidth();
//				double worldMetres = worldVal * 60 * 60 * 30;
//				double scrMetres = 0.3;
//				double theScale = worldMetres / scrMetres;
				
//				if (theScale > _minScale.doubleValue())
//				{
//					inScale = false;
//				}
				
			}
			if (inScale)
				doPaint(dest);
		}
	}

	public boolean getVisible()
	{
		return _visible;
	}

	public double rangeFrom(final WorldLocation other)
	{
		return 0;
	}

	public void setVisible(final boolean val)
	{
		_visible = val;
	}

	public EditorType getInfo()
	{
		if (_myEditor == null)
			_myEditor = createEditor();
		return _myEditor;
	}

	abstract EditorType createEditor();

	public String getName()
	{
		return _myName;
	}
	
	public String toString()
	{
		return getName();
	}

	public boolean hasEditor()
	{
		return true;
	}

	public int compareTo(final Plottable arg0)
	{
		return 0;
	}
}
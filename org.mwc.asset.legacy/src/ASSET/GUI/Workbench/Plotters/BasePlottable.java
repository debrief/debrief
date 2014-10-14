/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package ASSET.GUI.Workbench.Plotters;

import java.util.Enumeration;

import MWC.GUI.*;
import MWC.GenericData.*;

public class BasePlottable implements Layer
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** the thing we're wrapping
	 */
	final private Editable _myModel;
	
  final	private Layer _parentLayer;
	
	public BasePlottable(Editable myModel, Layer parentLayer)
	{
		_myModel = myModel;
		_parentLayer = parentLayer;
	}	
	
	public boolean hasOrderedChildren()
	{
		return false;
	}
	
	public String toString()
	{
		return getName();
	}

	protected Editable getModel()
	{
		return _myModel;
	}
	
	public String getName()
	{
		return _myModel.getName();
	}
	
	public Layer getTopLevelLayer()
	{
		return _parentLayer;
	}
	
	public EditorType getInfo()
	{
		return _myModel.getInfo();
	}

	public boolean hasEditor()
	{
		return _myModel.hasEditor();
	}
	
	public void setName(String name)
	{
		// ignore...
	}
	

	public WorldArea getBounds()
	{
		return null;
	}

	public boolean getVisible()
	{
		return true;
	}

	public void paint(CanvasType dest)
	{
	}

	public double rangeFrom(WorldLocation other)
	{
		return -1;
	}

	public void setVisible(boolean val)
	{
	}

	public int compareTo(Plottable arg0)
	{
		BasePlottable other = (BasePlottable) arg0;
		Editable otherM = other._myModel;
		return _myModel.getName().compareTo(otherM.getName());
	}

	public void add(Editable point)
	{
	}

	public void append(Layer other)
	{
	}

	public Enumeration<Editable> elements()
	{
		return null;
	}

	public void exportShape()
	{
	}

	public int getLineThickness()
	{
		return 1;
	}

	public void removeElement(Editable point)
	{
	}

}

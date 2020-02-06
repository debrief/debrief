/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *  
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *  
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *******************************************************************************/
package org.mwc.cmap.naturalearth.view;

import org.eclipse.core.runtime.Status;
import org.mwc.cmap.naturalearth.Activator;

import MWC.GUI.Editable;
import MWC.GUI.Plottables;

public class NEFeature extends Plottables
{ 
	private static final long serialVersionUID = 1L;

	private NEFeature parent;

	public NEFeature(String name)
	{
		super();
		setName(name);
	}

	public NEFeature getParent()
	{
		return parent;
	}

	public void setParent(NEFeature parent)
	{
		this.parent = parent;
	}
	
	@Override
	public void add(Editable thePlottable)
	{
		if (!(thePlottable instanceof NEFeature))
		{
			Activator.logError(Status.WARNING,
					"Should not be adding this to a NE Feature:" + thePlottable, null);
		}
		else
		{
			super.add(thePlottable);
			((NEFeature) thePlottable).setParent(this);
		}
	}

	@Override
	public int hashCode()
	{
		String name = getName();
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((parent == null) ? 0 : parent.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NEFeature other = (NEFeature) obj;
		String name = getName();
		if (name == null)
		{
			if (other.getName() != null)
				return false;
		}
		else if (!name.equals(other.getName()))
			return false;
		if (parent == null)
		{
			if (other.parent != null)
				return false;
		}
		else if (!parent.equals(other.parent))
			return false;
		return true;
	}

}

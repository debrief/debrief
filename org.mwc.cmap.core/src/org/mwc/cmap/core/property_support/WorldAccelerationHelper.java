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
package org.mwc.cmap.core.property_support;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.mwc.cmap.core.property_support.ui.ValueWithUnitsCellEditor2;
import org.mwc.cmap.core.property_support.ui.ValueWithUnitsControl;
import org.mwc.cmap.core.property_support.ui.ValueWithUnitsDataModel;

import MWC.GenericData.WorldAcceleration;

public class WorldAccelerationHelper extends EditorHelper
{
	public static class WorldAccelerationModel implements ValueWithUnitsDataModel
	{
		/** the world distance we're editing
		 * 
		 */
		WorldAcceleration _myVal;
		
		/**
		 * @return
		 */
		public int getUnitsValue()
		{
			return _myVal.getUnits();
		}

		/**
		 * @return
		 */
		public double getDoubleValue()
		{
			return _myVal.getValue();
		}

		/**
		 * @return
		 */
		public String[] getTagsList()
		{
			return WorldAcceleration.UnitLabels;
		}
		
		/**
		 * @param dist the value typed in
		 * @param units the units for the value
		 * @return an object representing the new data value
		 */
		public Object createResultsObject(final double dist, final int units)
		{
			return new WorldAcceleration(dist, units);
		}

		/** convert the object to our data units
		 * 
		 * @param value
		 */
		public void storeMe(final Object value)
		{
			_myVal = (WorldAcceleration) value;
		}
		
	}

	/** constructor..
	 *
	 */
	public WorldAccelerationHelper()
	{
		super(WorldAcceleration.class);
	}

	/** create an instance of the cell editor suited to our data-type
	 * 
	 * @param parent
	 * @return
	 */
	public CellEditor getCellEditorFor(final Composite parent)
	{
		return new ValueWithUnitsCellEditor2(parent, "Acceleration", "Units", new WorldAccelerationModel());
	}

	public ILabelProvider getLabelFor(final Object currentValue)
	{
		final ILabelProvider label1 = new LabelProvider()
		{
			public String getText(final Object element)
			{
				return element.toString();
			}

			public Image getImage(final Object element)
			{
				return null;
			}

		};
		return label1;
	}
	

	@Override
	public Control getEditorControlFor(final Composite parent, final IDebriefProperty property)
	{
		return new ValueWithUnitsControl(parent, "Acceleration", "Units", new WorldAccelerationModel(), property);
	}	
}
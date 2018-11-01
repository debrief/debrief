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

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.property_support.ui.ValueWithUnitsCellEditor2;
import org.mwc.cmap.core.property_support.ui.ValueWithUnitsControl;
import org.mwc.cmap.core.property_support.ui.ValueWithUnitsDataModel;

import MWC.GenericData.WorldDistance;

public class DistanceWithUnitsHelper extends EditorHelper
{
	
	
	public static class DistanceModel implements ValueWithUnitsDataModel
	{
		/** the world distance we're editing
		 * 
		 */
		WorldDistance _myVal;
		
		private boolean _isNull = false;
		
		/**
		 * @return
		 */
		public int getUnitsValue()
		{
			return WorldDistance.getUnitIndexFor(_myVal.getUnitsLabel());
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
			return WorldDistance.UnitLabels;
		}
		
		/**
		 * @param dist the value typed in
		 * @param units the units for the value
		 * @return an object representing the new data value
		 */
		public Object createResultsObject(final double dist, final int units)
		{
			final WorldDistance res;
			// ok, was it null?
			if(_isNull && dist == 0)
			{
				res = null;
			}
			else
				res = new WorldDistance(dist, units);
			
			return  res;
		}

		/** convert the object to our data units
		 * 
		 * @param value
		 */
		public void storeMe(final Object value)
		{
			if(value == null)
			{
				_isNull = true;
				_myVal = new WorldDistance(0, WorldDistance.YARDS);
			}
			else
				_myVal = (WorldDistance) value;
		}
		
	}
	

	/** constructor..
	 *
	 */
	public DistanceWithUnitsHelper()
	{
		super(WorldDistance.class);
	}

	/** produce editable version.  In our data model we may rely on a null value to describe
	 * an attribute.  But, our editor cannot edit null values.  So, if we receive a null
	 * distance, convert it to a zero length.  
	 */
	public Object translateToSWT(final Object value)
	{
		Object theValue = value;
		// right, is it null? if so provide zero world distance
		if(theValue == null)
			theValue = new WorldDistance(0, WorldDistance.METRES);
		
		// ok, done. let the parent look at it - just out of politeness
		return super.translateToSWT(theValue);
	}

	/** produce editable version.  In our data model we may rely on a null value to describe
	 * an attribute.  But, our editor cannot edit null values.  We convert a null value
	 * to an editable range - but we also need to be able to convert back.  So convert a 
	 * zero range to a null value.  
	 */
	public Object translateFromSWT(final Object value)
	{
		Object theValue = value;
		if(theValue != null)
		{
			final WorldDistance dist = (WorldDistance) theValue;
			if(dist.getValue() == 0)
				theValue = null;
		}
		
		// ok, done. let the parent look at it - just out of politeness
		return super.translateFromSWT(theValue);
	}
	
	/** create an instance of the cell editor suited to our data-type
	 * 
	 * @param parent
	 * @return
	 */
	public CellEditor getCellEditorFor(final Composite parent)
	{
		return new ValueWithUnitsCellEditor2(parent, "Distance", "Units", new DistanceModel());
	}

	public ILabelProvider getLabelFor(final Object currentValue)
	{
    final ILabelProvider label1 = new LabelProvider()
    {
      public String getText(final Object element)
      {
        final WorldDistance val = (WorldDistance) element;
        String res = "unassigned";
        if (val != null)
        {
          // we're getting an intermittent error in
          // Double.isFinite. Trap it here, to help diagnosis
          try
          {
            res = val.toString();
          }
          catch(final Exception e)
          {
            CorePlugin.logError(Status.ERROR, "Trouble writing distance as string", e);
            res = "suppressed";
          }
        }
        else
        {
          res = "unset";
        }
        return res;
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
		return new ValueWithUnitsControl(parent, "Distance", "Units", new DistanceModel(), property);
	}	
}
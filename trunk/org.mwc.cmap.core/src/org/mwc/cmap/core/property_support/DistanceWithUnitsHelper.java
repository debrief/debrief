/**
 * 
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

import MWC.GenericData.WorldDistance;

public class DistanceWithUnitsHelper extends EditorHelper
{
	
	
	public static class DistanceModel implements ValueWithUnitsDataModel
	{
		/** the world distance we're editing
		 * 
		 */
		WorldDistance _myVal;
		
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
		public Object createResultsObject(double dist, int units)
		{
			return new WorldDistance(dist, units);
		}

		/** convert the object to our data units
		 * 
		 * @param value
		 */
		public void storeMe(Object value)
		{
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
	public Object translateToSWT(Object value)
	{
		// right, is it null? if so provide zero world distance
		if(value == null)
			value = new WorldDistance(0, WorldDistance.METRES);
		
		// ok, done. let the parent look at it - just out of politeness
		return super.translateToSWT(value);
	}

	/** produce editable version.  In our data model we may rely on a null value to describe
	 * an attribute.  But, our editor cannot edit null values.  We convert a null value
	 * to an editable range - but we also need to be able to convert back.  So convert a 
	 * zero range to a null value.  
	 */
	public Object translateFromSWT(Object value)
	{
		if(value != null)
		{
			WorldDistance dist = (WorldDistance) value;
			if(dist.getValue() == 0)
				value = null;
		}
		
		// ok, done. let the parent look at it - just out of politeness
		return super.translateFromSWT(value);
	}
	
	/** create an instance of the cell editor suited to our data-type
	 * 
	 * @param parent
	 * @return
	 */
	public CellEditor getCellEditorFor(Composite parent)
	{
		return new ValueWithUnitsCellEditor2(parent, "Distance", "Units", new DistanceModel());
	}

	public ILabelProvider getLabelFor(Object currentValue)
	{
		ILabelProvider label1 = new LabelProvider()
		{
			public String getText(Object element)
			{
				WorldDistance val = (WorldDistance) element;
				return val.toString();
			}

			public Image getImage(Object element)
			{
				return null;
			}

		};
		return label1;
	}
	

	public Control getEditorControlFor(Composite parent, final DebriefProperty property)
	{
		return new ValueWithUnitsControl(parent, "Distance", "Units", new DistanceModel(), property);
	}	
}
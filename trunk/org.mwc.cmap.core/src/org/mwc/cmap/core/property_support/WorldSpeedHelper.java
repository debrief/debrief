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
import org.mwc.cmap.core.property_support.ui.ValueWithUnitsControl;
import org.mwc.cmap.core.property_support.ui.ValueWithUnitsCellEditor2;
import org.mwc.cmap.core.property_support.ui.ValueWithUnitsDataModel;

import MWC.GenericData.WorldSpeed;

public class WorldSpeedHelper extends EditorHelper
{

	public static class WorldSpeedModel implements ValueWithUnitsDataModel
	{
		/**
		 * the world distance we're editing
		 * 
		 */
		WorldSpeed _myVal;

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
			return WorldSpeed.UnitLabels;
		}

		/**
		 * @param dist
		 *          the value typed in
		 * @param units
		 *          the units for the value
		 * @return an object representing the new data value
		 */
		public Object createResultsObject(double dist, int units)
		{
			return new WorldSpeed(dist, units);
		}

		/**
		 * convert the object to our data units
		 * 
		 * @param value
		 */
		public void storeMe(Object value)
		{
			_myVal = (WorldSpeed) value;
		}
	}

	/**
	 * constructor..
	 * 
	 */
	public WorldSpeedHelper()
	{
		super(WorldSpeed.class);
	}

	/**
	 * create an instance of the cell editor suited to our data-type
	 * 
	 * @param parent
	 * @return
	 */
	public CellEditor getCellEditorFor(Composite parent)
	{
		return new ValueWithUnitsCellEditor2(parent, "speed", "units",
				new WorldSpeedModel());
	}

	public ILabelProvider getLabelFor(Object currentValue)
	{
		ILabelProvider label1 = new LabelProvider()
		{
			public String getText(Object element)
			{
				return element.toString();
			}

			public Image getImage(Object element)
			{
				return null;
			}

		};
		return label1;
	}

	public Control getEditorControlFor(Composite parent,
			final DebriefProperty property)
	{
		ValueWithUnitsControl control = new ValueWithUnitsControl(parent, "Speed", "Units",
				new WorldSpeedModel());
		return control;
	}
}
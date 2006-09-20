/**
 * 
 */
package org.mwc.cmap.core.property_support;

import java.io.Serializable;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;

import MWC.GenericData.*;

public class WorldDistanceHelper extends EditorHelper implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * constructor..
	 */
	public WorldDistanceHelper()
	{
		super(WorldDistance.class);
	}

	/**
	 * create an instance of the cell editor suited to our data-type
	 * 
	 * @param parent
	 * @return
	 */
	public CellEditor getCellEditorFor(Composite parent)
	{
		return new WorldDistanceWithUnitsCellEditor(parent);
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

	public Control getEditorControlFor(Composite parent, final DebriefProperty property)
	{
		// TODO create the editor
		final Button myCheckbox = new Button(parent, SWT.CHECK);
		myCheckbox.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				Boolean val = new Boolean(myCheckbox.getSelection());
				property.setValue(val);
			}
		});
		return myCheckbox;
	}

	public static class WorldDistanceWithUnitsCellEditor extends ValueWithUnitsCellEditor
	{
		/**
		 * the world distance we're editing
		 */
		WorldDistance _myVal;

		public WorldDistanceWithUnitsCellEditor(Composite parent)
		{
			super(parent, "Distance", "Units");
		}

		/**
		 * @return
		 */
		protected int getUnitsValue()
		{
			// so, what are the preferred units?
			int theUnits = WorldDistance.selectUnitsFor(_myVal.getValueIn(WorldDistance
					.getSIUnits()));
			return theUnits;
		}

		/**
		 * @return
		 */
		protected double getDoubleValue()
		{
			// so, what are the preferred units?
			int theUnits = WorldDistance.selectUnitsFor(_myVal.getValueIn(WorldDistance
					.getSIUnits()));

			double theValue = _myVal.getValueIn(theUnits);

			// try to round it to a sensible value
			theValue = Math.round(theValue * 100) / 100d;

			return theValue;
		}

		/**
		 * @return
		 */
		protected String[] getTagsList()
		{
			return WorldDistance.UnitLabels;
		}

		/**
		 * @param dist
		 *          the value typed in
		 * @param units
		 *          the units for the value
		 * @return an object representing the new data value
		 */
		protected Object createResultsObject(double dist, int units)
		{
			return new WorldDistance(dist, units);
		}

		/**
		 * convert the object to our data units
		 * 
		 * @param value
		 */
		protected void storeMe(Object value)
		{
			_myVal = (WorldDistance) value;
		}
	};
}

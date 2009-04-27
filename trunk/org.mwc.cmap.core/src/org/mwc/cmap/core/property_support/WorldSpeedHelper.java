/**
 * 
 */
package org.mwc.cmap.core.property_support;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;

import MWC.GenericData.*;

public class WorldSpeedHelper extends EditorHelper
{

	/** constructor..
	 *
	 */
	public WorldSpeedHelper()
	{
		super(WorldSpeed.class);
	}

	/** create an instance of the cell editor suited to our data-type
	 * 
	 * @param parent
	 * @return
	 */
	public CellEditor getCellEditorFor(Composite parent)
	{
		return new ValueWithUnitsCellEditor(parent, "Speed", "Units")
		{
			/** the world distance we're editing
			 * 
			 */
			WorldSpeed _myVal;
			
			/**
			 * @return
			 */
			protected int getUnitsValue()
			{
		    return _myVal.getUnits();
			}

			/**
			 * @return
			 */
			protected double getDoubleValue()
			{
				return _myVal.getValue();
			}

			/**
			 * @return
			 */
			protected String[] getTagsList()
			{
				return WorldSpeed.UnitLabels;
			}
			
			/**
			 * @param dist the value typed in
			 * @param units the units for the value
			 * @return an object representing the new data value
			 */
			protected Object createResultsObject(double dist, int units)
			{
				return new WorldSpeed(dist, units);
			}

			/** convert the object to our data units
			 * 
			 * @param value
			 */
			protected void storeMe(Object value)
			{
				_myVal = (WorldSpeed) value;
			}
		};
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
		final Button myCheckbox = new Button(parent, SWT.CHECK);
		myCheckbox.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e)
			{
				Boolean val = new Boolean(myCheckbox.getSelection());
				property.setValue(val);
			}});
		return myCheckbox;
	}	
}
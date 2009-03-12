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

public class WorldAccelerationHelper extends EditorHelper
{

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
	public CellEditor getCellEditorFor(Composite parent)
	{
		return new ValueWithUnitsCellEditor(parent, "Acceleration", "Units")
		{
			/** the world distance we're editing
			 * 
			 */
			WorldAcceleration _myVal;
			
			/**
			 * @return
			 */
			protected int getUnitsValue()
			{
		    // so, what are the preferred units?
		    int theUnits = WorldAcceleration.selectUnitsFor(_myVal.getValueIn(WorldAcceleration.getSIUnits()));
		    return theUnits;
			}

			/**
			 * @return
			 */
			protected double getDoubleValue()
			{
		    // so, what are the preferred units?
		    int theUnits = WorldAcceleration.selectUnitsFor(_myVal.getValueIn(WorldAcceleration.getSIUnits()));

		    double theValue = _myVal.getValueIn(theUnits);				
				return theValue;
			}

			/**
			 * @return
			 */
			protected String[] getTagsList()
			{
				return WorldAcceleration.UnitLabels;
			}
			
			/**
			 * @param dist the value typed in
			 * @param units the units for the value
			 * @return an object representing the new data value
			 */
			protected Object createResultsObject(double dist, int units)
			{
				return new WorldAcceleration(dist, units);
			}

			/** convert the object to our data units
			 * 
			 * @param value
			 */
			protected void storeMe(Object value)
			{
				_myVal = (WorldAcceleration) value;
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
		// TODO create the editor
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
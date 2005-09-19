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

public class DurationHelper extends EditorHelper
{

	/** embedded cell editor for durations
	 * 
	 * @author ian.mayo
	 *
	 */
	public static class DurationCellEditor extends ValueWithUnitsCellEditor
	{
		
		public DurationCellEditor(Composite parent)
		{
			super(parent, "Duration", "Units");
			// TODO Auto-generated constructor stub
		}

		/** the world distance we're editing
		 * 
		 */
		Duration _myVal;
		
		/**
		 * @return
		 */
		protected int getUnitsValue()
		{
	    // so, what are the preferred units?
	    int theUnits = Duration.selectUnitsFor(_myVal.getValueIn(Duration.MILLISECONDS));
	    return theUnits;
		}

		/**
		 * @return
		 */
		protected double getDoubleValue()
		{
	    // so, what are the preferred units?
	    int theUnits = Duration.selectUnitsFor(_myVal.getValueIn(Duration.MILLISECONDS));

	    double theValue = _myVal.getValueIn(theUnits);				
			return theValue;
		}

		/**
		 * @return
		 */
		protected String[] getTagsList()
		{
			return Duration.UnitLabels;
		}
		
		/**
		 * @param dist the value typed in
		 * @param units the units for the value
		 * @return an object representing the new data value
		 */
		protected Object createResultsObject(double dist, int units)
		{
			return new Duration(dist, units);
		}

		/** convert the object to our data units
		 * 
		 * @param value
		 */
		protected void storeMe(Object value)
		{
			_myVal = (Duration) value;
		}
	}
	
	/** constructor..
	 *
	 */
	public DurationHelper()
	{
		super(Duration.class);
	}

	/** create an instance of the cell editor suited to our data-type
	 * 
	 * @param parent
	 * @return
	 */
	public CellEditor getCellEditorFor(Composite parent)
	{
		return new DurationCellEditor(parent);
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
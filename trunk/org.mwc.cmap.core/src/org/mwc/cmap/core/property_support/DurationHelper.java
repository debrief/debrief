/**
 * 
 */
package org.mwc.cmap.core.property_support;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.mwc.cmap.core.property_support.ui.ValueWithUnitsCellEditor2;
import org.mwc.cmap.core.property_support.ui.ValueWithUnitsControl;
import org.mwc.cmap.core.property_support.ui.ValueWithUnitsDataModel;

import MWC.GUI.Properties.TimeIntervalPropertyEditor;
import MWC.GenericData.Duration;

public class DurationHelper extends EditorHelper
{
	
	public static class DurationModel implements ValueWithUnitsDataModel
	{

		/** the time period we're editing
		 * 
		 */
		Duration _myVal;
		
		/**
		 * @return
		 */
		public int getUnitsValue()
		{
	    // so, what are the preferred units?
	    int theUnits = Duration.selectUnitsFor(_myVal.getValueIn(Duration.MILLISECONDS));
	    return theUnits;
		}

		/**
		 * @return
		 */
		public double getDoubleValue()
		{
	    // so, what are the preferred units?
	    int theUnits = Duration.selectUnitsFor(_myVal.getValueIn(Duration.MILLISECONDS));

	    double theValue = _myVal.getValueIn(theUnits);				
			return theValue;
		}

		/**
		 * @return
		 */
		public String[] getTagsList()
		{
			return Duration.UnitLabels;
		}
		
		/**
		 * @param dist the value typed in
		 * @param units the units for the value
		 * @return an object representing the new data value
		 */
		public Object createResultsObject(double dist, int units)
		{
			return new Duration(dist, units);
		}

		/** convert the object to our data units
		 * 
		 * @param value
		 */
		public void storeMe(Object value)
		{
			_myVal = (Duration) value;
		}		
	}
	
	/** embedded cell editor for durations
	 * 
	 * @author ian.mayo
	 *
	 */
	public static class TimeIntervalEditor extends ComboBoxCellEditor
	{
	
		public TimeIntervalEditor(Composite parent)
		{
			super(parent, TimeIntervalPropertyEditor.getTagList());
		}

		/**
		 * @return
		 */
		protected Object doGetValue()
		{
			Integer index = (Integer) super.doGetValue();
			int res = TimeIntervalPropertyEditor.getValueList()[index.intValue()];
			return new Duration(res, Duration.MILLISECONDS);
		}

		/**
		 * @param value
		 */
		protected void doSetValue(Object value)
		{
			// ok - received duration, declare it
			Duration dur = (Duration) value;
			int millis = (int) dur.getValueIn(Duration.MILLISECONDS);
			
			
	    int[] list = TimeIntervalPropertyEditor.getValueList();
	    int res = 0;
	    for (int i = 0; i < list.length; i++)
	    {
	      double v = list[i];
	      if (v == millis)
	      {
	        res = i;
	        break;
	      }

	    }
	    
			super.doSetValue(new Integer(res));
		}
		
		
		
	}
	
	/** embedded cell editor for durations
	 * 
	 * @author ian.mayo
	 *
	 */
	public static class DurationCellEditor extends ValueWithUnitsCellEditor2
	{
		public DurationCellEditor(Composite parent)
		{
			super(parent, "Duration", "Units", new DurationModel());
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
	

	@Override
	public Control getEditorControlFor(Composite parent, final IDebriefProperty property)
	{
		return new ValueWithUnitsControl(parent, "Duration", "Units", new DurationModel(), property);
	
	}	
}
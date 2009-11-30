/**
 * 
 */
package org.mwc.cmap.core.property_support;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.mwc.cmap.core.property_support.lengtheditor.LengthPropertyCellEditor;
import org.mwc.cmap.gridharness.data.WorldDistance;

import MWC.GenericData.WorldDistance.ArrayLength;


public class ArrayLengthHelper extends EditorHelper
{

	public ArrayLengthHelper()
	{
		super(ArrayLength.class);
	}

	public CellEditor getCellEditorFor(Composite parent)
	{
		CellEditor res = new LengthPropertyCellEditor(parent);
		return res; 
	}

	@SuppressWarnings("unchecked")
	public boolean editsThis(Class target)
	{
		return (target == ArrayLength.class);
	}

	public Object translateToSWT(Object value)
	{
		ArrayLength len = (ArrayLength) value;
		return len.getValueIn(WorldDistance.METRES) + " m";
	}

	public Object translateFromSWT(Object value)
	{
		String theStr = (String) value;
		ArrayLength newVal = new ArrayLength(Double.parseDouble(theStr)); 
		return newVal;
	}

	public ILabelProvider getLabelFor(Object currentValue)
	{
		ILabelProvider label1 = new LabelProvider()
		{
			public String getText(Object element)
			{
				String res = (String) element;
				return res;
			}

			public Image getImage(Object element)
			{
				return null;
			}

		};
		return label1;
	}
}
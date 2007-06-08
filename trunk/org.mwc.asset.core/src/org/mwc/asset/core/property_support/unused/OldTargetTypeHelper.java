package org.mwc.asset.core.property_support.unused;
/**
 * 
 */

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.mwc.cmap.core.property_support.EditorHelper;

import ASSET.Models.Decision.TargetType;

public class OldTargetTypeHelper extends EditorHelper
{

	/** constructor..
	 *
	 */
	public OldTargetTypeHelper()
	{
		super(TargetType.class);
	}

	/** create an instance of the cell editor suited to our data-type
	 * 
	 * @param parent
	 * @return
	 */
	public CellEditor getCellEditorFor(Composite parent)
	{
		return new OldTargetTypeCellEditor(parent);
	}

	public ILabelProvider getLabelFor(Object currentValue)
	{
		ILabelProvider label1 = new LabelProvider()
		{
			public String getText(Object element)
			{
				TargetType val = (TargetType) element;
				return val.toString();
			}

			public Image getImage(Object element)
			{
				return null;
			}

		};
		return label1;
	}
//	
//
//	public Control getEditorControlFor(Composite parent, final DebriefProperty property)
//	{
//		final Button myCheckbox = new Button(parent, SWT.CHECK);
//		myCheckbox.addSelectionListener(new SelectionAdapter(){
//			public void widgetSelected(SelectionEvent e)
//			{
//				Boolean val = new Boolean(myCheckbox.getSelection());
//				property.setValue(val);
//			}});
//		return myCheckbox;
//	}	
}
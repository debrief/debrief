/**
 * 
 */
package org.mwc.cmap.core.property_support;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.*;

import MWC.GenericData.WorldPath;

public class WorldPathHelper extends EditorHelper
{

	/** constructor..
	 *
	 */
	public WorldPathHelper()
	{
		super(WorldPath.class);
	}

	/** create an instance of the cell editor suited to our data-type
	 * 
	 * @param parent
	 * @return
	 */
	public CellEditor getCellEditorFor(Composite parent)
	{
		return new WorldPathCellEditor(parent, "Acceleration")
		{
		
		};
	}

	public ILabelProvider getLabelFor(Object currentValue)
	{
		ILabelProvider label1 = new LabelProvider()
		{
			public String getText(Object element)
			{
				WorldPath wp = (WorldPath) element;
				return "Path (" + wp.getPoints().size() + " points)";
			}

			public Image getImage(Object element)
			{
				return null;
			}

		};
		return label1;
	}
	
	abstract public class WorldPathCellEditor extends CellEditor
	{
		/** hmm, the text bit.
		 * 
		 */
		Text _myText;
		
		/** and the drop-down units bit
		 * 
		 */
		Combo _myCombo;
		
		
		/** the title for what we're editing
		 * 
		 */
		final private String _title;
		
		/** the world distance we're editing
		 * 
		 */
		WorldPath _myVal;
		

		
		public WorldPathCellEditor(Composite parent, String textTip)
		{
			super(parent);
			_title = textTip;
		}

		protected Control createControl(Composite parent)
		{
			return createControl(parent, _title);
		}
		
		protected Control createControl(Composite parent, String tipOne)
		{
			Composite holder = new Composite(parent, SWT.NONE);
//			Button btn = new Button(parent, SWT.NONE);
//			btn.setText("push to test");
//			Label lbl = new Label(parent, SWT.NONE);
//			lbl.setText("aaa");
			
			RowLayout rows = new RowLayout();
			rows.marginLeft = rows.marginRight = 0;
			rows.marginTop = rows.marginBottom	 = 0;
			rows.fill = false;
			rows.spacing = 0;
			rows.pack = false;
			holder.setLayout(rows);
			
			_myText = new Text(holder, SWT.BORDER);
			_myText.setTextLimit(7);
			_myText.setToolTipText(tipOne);
			
			List _myList = new List(holder, SWT.SINGLE);
			_myList.setItems(new String[]{"aa", "bb", "cc"});

			
//			_myCombo = new Combo(holder, SWT.DROP_DOWN);
//			_myCombo.setItems(new String[]{"aa", "bb", "cc"});
//			_myCombo.setToolTipText("bingo");
			
			return holder;
		}

		/**
		 * 
		 */
		final private void doUpdate()
		{
			// get the best units
//			final int units = getUnitsValue();
//			final String txt = "" + getDoubleValue();
//			_myCombo.select(units);
//			_myText.setText(txt);
		}

		
		protected Object doGetValue()
		{
			return _myVal;
		}


		protected void doSetFocus()
		{
		}

		protected void doSetValue(Object value)
		{
			storeMe(value);
			doUpdate();
		}

		/** convert the object to our data units
		 * 
		 * @param value
		 */
		protected void storeMe(Object value)
		{
			_myVal = (WorldPath) value;
		}				
		
	}	
}
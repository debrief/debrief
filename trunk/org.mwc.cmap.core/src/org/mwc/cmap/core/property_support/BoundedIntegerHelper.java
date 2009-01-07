/**
 * 
 */
package org.mwc.cmap.core.property_support;

import java.text.DecimalFormat;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import MWC.GUI.Properties.*;

public class BoundedIntegerHelper extends EditorHelper
{

	
	private static class SliderEditor extends CellEditor
	{

		Composite _myControl = null;
		protected Label _myLabel;
		protected Slider _theSlider;
		
		public SliderEditor(Composite parent)
		{
			super(parent, SWT.NONE);
		}
		
		/**
		 * @return
		 */
		public LayoutData getLayoutData()
		{
			CellEditor.LayoutData res =  super.getLayoutData();
			res.grabHorizontal = true;
			return res;
		}

		protected Control createControl(Composite parent)
		{
	    Font font = parent.getFont();
      Color bg = parent.getBackground();

      _myControl = new Composite(parent, getStyle());
      _myControl.setFont(font);
      _myControl.setBackground(bg);
      
			
//			RowLayout rl = new RowLayout();
//			rl.wrap = false;
//			rl.type = SWT.HORIZONTAL;
//			rl.marginHeight = 0;
//			rl.marginWidth = 0;
      GridLayout rl = new GridLayout();
      rl.marginWidth = 0;
      rl.marginHeight = 0;
      rl.numColumns = 8;

			_myControl.setLayout(rl);
//			
			_myLabel = new Label(_myControl, SWT.NONE);
			_myLabel.setText("000");
			_myLabel.setBackground(bg);
			GridData gd1 = new GridData(GridData.FILL_HORIZONTAL);
			_myLabel.setLayoutData(gd1);
			
			_theSlider = new Slider(_myControl, SWT.NONE);
			GridData gd2 = new GridData(GridData.FILL_HORIZONTAL);
			gd2.horizontalSpan = 7;
			_theSlider.setLayoutData(gd2);
			_theSlider.addSelectionListener(new SelectionListener(){

				public void widgetSelected(SelectionEvent e)
				{
					_myLabel.setText(formatMe(_theSlider.getSelection()));
					_myLabel.update();
				}

				public void widgetDefaultSelected(SelectionEvent e)
				{
					// TODO Auto-generated method stub
					
				}});
			
			return _myControl;
		}

		protected Object doGetValue()
		{
			Object res = null;
			if(_theSlider != null) 
			{
				res = new BoundedInteger(_theSlider.getSelection(), _theSlider.getMinimum(), _theSlider.getMaximum());
			}
			return res;
		}

		protected void doSetFocus()
		{
			// TODO Auto-generated method stub
			_theSlider.setFocus();
		}

		DecimalFormat _df  = null;
		
		protected String formatMe(int value)
		{
			if(_df == null)
				_df = new DecimalFormat("000");
			
			return _df.format(value);
		}
		
		protected void doSetValue(Object value)
		{
			BoundedInteger curr = (BoundedInteger) value;
			if(_myLabel != null) 
				_myLabel.setText(formatMe(curr.getCurrent()));
			if(_theSlider != null)
			{
				_theSlider.setMinimum(curr.getMin());
				// we have to add one to the max value, since it appears to be exclusive of the max value
				_theSlider.setMaximum(curr.getMax() + 1);
				_theSlider.setSelection(curr.getCurrent());
				_theSlider.setThumb(1);
			}
		}
		
	}

	public BoundedIntegerHelper()
	{
		super(BoundedInteger.class);
	}
	
	/** provide better constructor - so child implementations can pass target class
	 * back up the chain
	 * @param targetClass
	 */
	@SuppressWarnings("unchecked")
	public BoundedIntegerHelper(Class targetClass)
	{
		super(targetClass);
	}

	public CellEditor getCellEditorFor(Composite parent)
	{
		return new SliderEditor(parent);
	}

	public Object translateToSWT(Object value)
	{
		return value;
	}

	public Object translateFromSWT(Object value)
	{
		return value;
	}

	public ILabelProvider getLabelFor(Object currentValue)
	{
		ILabelProvider label1 = new LabelProvider()
		{
			public String getText(Object element)
			{
				BoundedInteger rgb = (BoundedInteger) element;
				String res = "" + rgb.getCurrent();
				return res;
			}

		};
		return label1;
	}

	

	public static class SteppingBoundedIntegerHelper extends BoundedIntegerHelper
	{
		public SteppingBoundedIntegerHelper()
		{
			super(SteppingBoundedInteger.class);
		}
		
		public CellEditor getCellEditorFor(Composite parent)
		{
			return new SteppingSliderEditor(parent);
		}
		

		public ILabelProvider getLabelFor(Object currentValue)
		{
			ILabelProvider label1 = new LabelProvider()
			{
				public String getText(Object element)
				{
					SteppingBoundedInteger rgb = (SteppingBoundedInteger) element;
					String res = "" + rgb.getCurrent();
					return res;
				}

			};
			return label1;
		}		
	}
	
	////////////////////////////////////////////////////////
	// and now for stepping bounded integer support
	///////////////////////////////////////////////////////
	
	private static class SteppingSliderEditor extends SliderEditor
	{

		public SteppingSliderEditor(Composite parent)
		{
			super(parent);
		}
	

		protected Object doGetValue()
		{
			Object res = null;
			if(_theSlider != null) 
			{
				res = new SteppingBoundedInteger(_theSlider.getSelection(), _theSlider.getMinimum(), _theSlider.getMaximum(), _theSlider.getIncrement());
			}
			return res;
		}
		
		protected void doSetValue(Object value)
		{
			
			// TODO Auto-generated method stub
			SteppingBoundedInteger curr = (SteppingBoundedInteger) value;
			if(_myLabel != null) 
				_myLabel.setText(formatMe(curr.getCurrent()));
			if(_theSlider != null)
			{
				_theSlider.setMinimum(curr.getMin());
				// we have to add one to the max value, since it appears to be exclusive of the max value
				_theSlider.setMaximum(curr.getMax() + 1);
				_theSlider.setSelection(curr.getCurrent());
				_theSlider.setIncrement(curr.getStep());
				_theSlider.setThumb(1);
			}
		}		
	}
		
	
}
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
		
		public SliderEditor(final Composite parent)
		{
			super(parent, SWT.NONE);
		}
		
		/**
		 * @return
		 */
		public LayoutData getLayoutData()
		{
			final CellEditor.LayoutData res =  super.getLayoutData();
			res.grabHorizontal = true;
			return res;
		}

		protected Control createControl(final Composite parent)
		{
	    final Font font = parent.getFont();
      final Color bg = parent.getBackground();

      _myControl = new Composite(parent, getStyle());
      _myControl.setFont(font);
      _myControl.setBackground(bg);
      
			
//			RowLayout rl = new RowLayout();
//			rl.wrap = false;
//			rl.type = SWT.HORIZONTAL;
//			rl.marginHeight = 0;
//			rl.marginWidth = 0;
      final GridLayout rl = new GridLayout();
      rl.marginWidth = 0;
      rl.marginHeight = 0;
      rl.numColumns = 8;

			_myControl.setLayout(rl);
//			
			_myLabel = new Label(_myControl, SWT.NONE);
			_myLabel.setText("000");
			_myLabel.setBackground(bg);
			final GridData gd1 = new GridData(GridData.FILL_HORIZONTAL);
			_myLabel.setLayoutData(gd1);
			
			_theSlider = new Slider(_myControl, SWT.NONE);
			final GridData gd2 = new GridData(GridData.FILL_HORIZONTAL);
			gd2.horizontalSpan = 7;
			_theSlider.setLayoutData(gd2);
			_theSlider.addSelectionListener(new SelectionListener(){

				public void widgetSelected(final SelectionEvent e)
				{
					_myLabel.setText(formatMe(_theSlider.getSelection()));
					_myLabel.update();
				}

				public void widgetDefaultSelected(final SelectionEvent e)
				{
					
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
			_theSlider.setFocus();
		}

		DecimalFormat _df  = null;
		
		protected String formatMe(final int value)
		{
			if(_df == null)
				_df = new DecimalFormat("000");
			
			return _df.format(value);
		}
		
		protected void doSetValue(final Object value)
		{
			final BoundedInteger curr = (BoundedInteger) value;
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
	@SuppressWarnings("rawtypes")
	public BoundedIntegerHelper(final Class targetClass)
	{
		super(targetClass);
	}

	public CellEditor getCellEditorFor(final Composite parent)
	{
		return new SliderEditor(parent);
	}

	public Object translateToSWT(final Object value)
	{
		return value;
	}

	public Object translateFromSWT(final Object value)
	{
		return value;
	}

	public ILabelProvider getLabelFor(final Object currentValue)
	{
		final ILabelProvider label1 = new LabelProvider()
		{
			public String getText(final Object element)
			{
				final BoundedInteger rgb = (BoundedInteger) element;
				final String res = "" + rgb.getCurrent();
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
		
		public CellEditor getCellEditorFor(final Composite parent)
		{
			return new SteppingSliderEditor(parent);
		}
		

		public ILabelProvider getLabelFor(final Object currentValue)
		{
			final ILabelProvider label1 = new LabelProvider()
			{
				public String getText(final Object element)
				{
					final SteppingBoundedInteger rgb = (SteppingBoundedInteger) element;
					final String res = "" + rgb.getCurrent();
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

		public SteppingSliderEditor(final Composite parent)
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
		
		protected void doSetValue(final Object value)
		{
			
			final SteppingBoundedInteger curr = (SteppingBoundedInteger) value;
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
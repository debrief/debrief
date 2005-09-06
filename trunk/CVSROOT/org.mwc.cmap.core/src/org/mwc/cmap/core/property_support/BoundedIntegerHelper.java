/**
 * 
 */
package org.mwc.cmap.core.property_support;

import java.text.DecimalFormat;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.*;

import MWC.GUI.Properties.BoundedInteger;

public class BoundedIntegerHelper extends EditorHelper
{

	private static class SliderEditor extends CellEditor
	{

		Composite _myControl = null;
		private Label _myLabel;
		private Slider _theSlider;
		
		public SliderEditor(Composite parent)
		{
			super(parent, SWT.NONE);
		}
		
		protected Control createControl(Composite parent)
		{

	    Font font = parent.getFont();
      Color bg = parent.getBackground();

      _myControl = new Composite(parent, getStyle());
      _myControl.setFont(font);
      _myControl.setBackground(bg);
      
			
//			_myControl = new Composite(parent, SWT.NONE);
			RowLayout rl = new RowLayout();
			rl.wrap = false;
			rl.type = SWT.HORIZONTAL;
			rl.marginHeight = 0;
			rl.marginWidth = 0;
			_myControl.setLayout(rl);
//			
			_myLabel = new Label(_myControl, SWT.NONE);
			_myLabel.setText("000");
			_myLabel.setBackground(bg);
			_theSlider = new Slider(_myControl, SWT.NONE);
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
		
		private String formatMe(int value)
		{
			if(_df == null)
				_df = new DecimalFormat("000");
			
			return _df.format(value);
		}
		
		protected void doSetValue(Object value)
		{
			
			// TODO Auto-generated method stub
			BoundedInteger curr = (BoundedInteger) value;
			if(_myLabel != null) 
				_myLabel.setText(formatMe(curr.getCurrent()));
			if(_theSlider != null)
			{
				_theSlider.setMinimum(curr.getMin());
				_theSlider.setMaximum(curr.getMax());
				_theSlider.setSelection(curr.getCurrent());
				_theSlider.setThumb(1);
			}
		}
		
	}

	public BoundedIntegerHelper()
	{
		super(BoundedInteger.class);
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

}
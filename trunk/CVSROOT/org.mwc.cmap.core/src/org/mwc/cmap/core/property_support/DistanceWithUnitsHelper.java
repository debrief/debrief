/**
 * 
 */
package org.mwc.cmap.core.property_support;

import java.awt.BorderLayout;

import javax.swing.*;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.CellEditor.LayoutData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.*;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.property_support.LatLongHelper.LatLongPropertySource;

import MWC.GenericData.WorldDistanceWithUnits;

public class DistanceWithUnitsHelper extends EditorHelper
{

	public static class MyCellEditor extends CellEditor
	{
		Text _myText;
		Combo _myCombo;
		
		WorldDistanceWithUnits _myVal = null;
		
		/** just have the one of each listener, so we can easily remove them when the value gets updated
		 * 
		 */
		private ModifyListener _modifyListener;
		private SelectionListener _selectionListener;
		
		
		
		public MyCellEditor(Composite parent)
		{
			super(parent);
		}

		protected Control createControl(Composite parent)
		{
			return createControl(parent, "The distance value", "The units");
		}

		private ModifyListener getModifyListener()
		{
			if(_modifyListener == null)
			{
				_modifyListener = new ModifyListener(){
					public void modifyText(ModifyEvent e)
					{
						System.out.println("new text:" + _myText.getText());
					}};
			}
			return _modifyListener;
		}
		
		private SelectionListener getSelectionListener()
		{
			if(_selectionListener == null)
			{
				_selectionListener = new SelectionListener(){
					public void widgetSelected(SelectionEvent e)
					{
						Combo combo = (Combo) e.getSource();
						System.out.println("new val:" + combo.getSelectionIndex());
					}
					public void widgetDefaultSelected(SelectionEvent e)
					{
					}
				};
			}
			return _selectionListener;
		}
		
		protected Control createControl(Composite parent, String tipOne, String tipTwo)
		{
			System.out.println("creating new control...");
			Composite holder = new Composite(parent, SWT.NONE);
			RowLayout rows = new RowLayout();
			rows.marginLeft = rows.marginRight = 0;
			rows.marginTop = rows.marginBottom	 = 0;
			rows.fill = false;
			rows.spacing = 0;
			rows.pack = false;
			holder.setLayout(rows);
			
			_myText = new Text(holder, SWT.BORDER);
			_myText.addModifyListener(getModifyListener());
			_myText.setTextLimit(5);
			_myCombo = new Combo(holder, SWT.DROP_DOWN);
			_myCombo.addSelectionListener(getSelectionListener());
			_myCombo.setItems(WorldDistanceWithUnits.UnitLabels);
			
			if(_myVal != null)
			{
	      // get the best units
	      int units = WorldDistanceWithUnits.getUnitIndexFor(_myVal.getUnitsLabel());
	      _myCombo.select(units);
	      _myText.setText("" + _myVal.getDistance());			
	     }
			
			return holder;
		}

//		
//    public LayoutData getLayoutData() {
//      LayoutData layoutData = super.getLayoutData();
//      if ((_myCombo == null) || _myCombo.isDisposed())
//          layoutData.minimumWidth = 60;
//      else {
//          // make the comboBox 10 characters wide
//          GC gc = new GC(_myCombo);
//          layoutData.minimumWidth = (gc.getFontMetrics()
//                  .getAverageCharWidth() * 10) + 10;
//          gc.dispose();
//      }
//      return layoutData;
//  }
		
		protected Object doGetValue()
		{
			String distTxt = _myText.getText();
			double dist = new Double(distTxt).doubleValue();
			int units = _myCombo.getSelectionIndex();
			WorldDistanceWithUnits res = new WorldDistanceWithUnits(dist, units);
			System.out.println("returning value of:" + res);
			return res;
		}

		protected void doSetFocus()
		{
		}

		protected void doSetValue(Object value)
		{
			_myVal = (WorldDistanceWithUnits) value;
			if(_myText != null)
			{
				_myText.setText("" + _myVal.getDistance());
				_myCombo.select(WorldDistanceWithUnits.getUnitIndexFor(_myVal.getUnitsLabel()));
			}
			else
				System.out.println("setting value, haven't created editor yet");
		}
		
	}
	
	public DistanceWithUnitsHelper()
	{
		super(WorldDistanceWithUnits.class);
	}

	public CellEditor getCellEditorFor(Composite parent)
	{
		return new MyCellEditor(parent);
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
				WorldDistanceWithUnits val = (WorldDistanceWithUnits) element;
				return val.toString();
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
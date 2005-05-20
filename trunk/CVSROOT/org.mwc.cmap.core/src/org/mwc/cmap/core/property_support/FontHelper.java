/**
 * 
 */
package org.mwc.cmap.core.property_support;

import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

public class FontHelper extends EditorHelper
{
	Control _parentControl;

	public FontHelper(Control parentControl)
	{
		super(java.awt.Font.class);
		_parentControl = parentControl;
	}

	public CellEditor getEditorFor(Composite parent)
	{
		return null;
	}

	public Object translateToSWT(Object value)
	{
		// ok, convert the AWT color to SWT
		java.awt.Font col = (java.awt.Font) value;
		return convertFont(col);
	}

	public Object translateFromSWT(Object value)
	{
		// ok, convert the AWT color to SWT
		FontData col = (FontData) value;
		return convertFont(col);
	}

	private static FontRegistry _fontRegistry;
	
	public static java.awt.Font convertFont(org.eclipse.swt.graphics.FontData swtFont)
	{
		// ok, convert the AWT color to SWT
		java.awt.Font res = null;
		res = new java.awt.Font(swtFont.getName(), swtFont.getStyle(), swtFont.getHeight());
		return res;		
	}
	
	public static org.eclipse.swt.graphics.Font convertFont(java.awt.Font javaFont)
	{
		
		// check we have our registry
		if(_fontRegistry == null)
			_fontRegistry = new FontRegistry(Display.getCurrent(), true);
		
		final String fontName = javaFont.toString();
		
		// retrieve the color
		org.eclipse.swt.graphics.Font thisFont = _fontRegistry.get(fontName);
		
		// ok. do we have the color?
		if(thisFont == null)
		{
			System.out.println("creating new color for:" + javaFont);
			
			// bugger, we'll have to  create it
			FontData newF = new FontData(javaFont.getName(), javaFont.getStyle(), javaFont.getSize());
			_fontRegistry.put(fontName,new FontData[]{newF});
			
			// and try to retrieve it again
			thisFont = _fontRegistry.get(fontName);
		}
		
		return thisFont;
	}
	
	public ILabelProvider getLabelFor(Object currentValue)
	{
		ILabelProvider label1 = new LabelProvider()
		{
			public String getText(Object element)
			{
				Font font = (Font) element;
				FontData[] datas = font.getFontData();
				FontData data = datas[0];
				String res = "(" + data.getName() + ", " + data.getHeight() + ")";
				return res;
			}

			public Image getImage(Object element)
			{
				Image res = null;
				return res;
			}

		};
		return label1;
	}

}
/**
 * 
 */
package org.mwc.cmap.core.property_support;

import java.util.HashMap;

import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;

public class FontHelper extends EditorHelper
{

	private static HashMap _myFontList;

	private static FontRegistry _fontRegistry;

	public static class FontDataDialogCellEditor extends DialogCellEditor
	{

		public FontDataDialogCellEditor(Composite parent)
		{
			super(parent);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.DialogCellEditor#getDefaultLabel()
		 */
		protected Label getDefaultLabel()
		{
			// TODO Auto-generated method stub
			return super.getDefaultLabel();
		}

		protected Object openDialogBox(Control cellEditorWindow)
		{
			Font res = null;
			FontDialog ftDialog = new FontDialog(cellEditorWindow.getShell());
			Font thisFont = (Font) getValue();
			if (thisFont != null)
			{
				FontData[] list = thisFont.getFontData();
				ftDialog.setFontList(list);
			}
			FontData fData = ftDialog.open();
			if (fData != null)
			{
				res = new Font(Display.getCurrent(), fData);

			}

			return res;
		}

	}

	public FontHelper()
	{
		super(java.awt.Font.class);
	}

	public CellEditor getCellEditorFor(Composite parent)
	{
		CellEditor editor = new FontDataDialogCellEditor(parent);
		return editor;
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
		Font font = (Font) value;
		return convertFont(font);
	}

	public static java.awt.Font convertFont(org.eclipse.swt.graphics.Font swtFont)
	{
		// ok, convert the AWT color to SWT
		java.awt.Font res = null;
		FontData fd = swtFont.getFontData()[0];
		res = new java.awt.Font(fd.getName(), fd.getStyle(), fd.getHeight());
		return res;
	}

	public static org.eclipse.swt.graphics.Font convertFont(java.awt.Font javaFont)
	{
	
		// check we have our registry
		if (_fontRegistry == null)
			_fontRegistry = new FontRegistry(Display.getCurrent(), true);

		if (_myFontList == null)
			_myFontList = new HashMap();

		// see if we've got the font in our local list
		org.eclipse.swt.graphics.Font thisFont = (Font) _myFontList.get(javaFont);

		// did we find it?
		if (thisFont == null)
		{ 
			// nope, better go and get it then...
			final String fontName = "" + javaFont.hashCode();

			// ok - now see if we've got the font
			thisFont = _fontRegistry.get(fontName);

			// do we have a font for this style?
			if (!_fontRegistry.hasValueFor(fontName))
			{
				// bugger, we'll have to create it

				int size = javaFont.getSize();
				int style = javaFont.getStyle();
				String name = javaFont.getName();

				// WORKAROUND
				// - on windows, our 'sans serif' recorded in the xml file doesn't get
				// translated to Arial. So, we do it by hand
				if (name.equals("Sans Serif"))
					name = "Arial";

				FontData newF = new FontData(name, size, style);
				_fontRegistry.put(fontName, new FontData[] { newF });
			}

			// ok, try to receive it. if we don't we'll just get a default one any
			// way. cool.
			thisFont = _fontRegistry.get(fontName);
			
			_myFontList.put(javaFont, thisFont);
		}

		return thisFont;
	}

	public static org.eclipse.swt.graphics.Font getFont(FontData fd)
	{
		org.eclipse.swt.graphics.Font res = null;

		return res;
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
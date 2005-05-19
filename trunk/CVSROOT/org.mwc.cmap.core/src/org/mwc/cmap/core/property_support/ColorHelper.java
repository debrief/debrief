/**
 * 
 */
package org.mwc.cmap.core.property_support;

import java.awt.Color;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Tree;

public class ColorHelper extends EditorHelper
{
	Control _parentControl;

	public ColorHelper(Control parentControl)
	{
		super(java.awt.Color.class);
		_parentControl = parentControl;
	}

	public CellEditor getEditorFor(Composite parent)
	{
		return new org.eclipse.jface.viewers.ColorCellEditor(parent);
	}

	public Object translateToSWT(Object value)
	{
		// ok, convert the AWT color to SWT
		Object res;
		Color col = (Color) value;
		res = new RGB(col.getRed(), col.getGreen(), col.getBlue());
		return res;
	}

	public Object translateFromSWT(Object value)
	{
		// ok, convert the AWT color to SWT
		Object res = null;
		RGB col = (RGB) value;
		res = new Color(col.red, col.green, col.blue);
		return res;
	}

	private ImageData createColorImage(RGB color)
	{

		ImageData data = null;

//		GC gc = new GC(_parentControl.getParent().getParent());
//		FontMetrics fm = gc.getFontMetrics();
//		int size = fm.getAscent();
//		gc.dispose();
		int size = 14;

		int indent = 6;
		int extent = 8;
		
//		if (_parentControl instanceof Table)
//			extent = ((Table) _parentControl).getItemHeight() - 1;
//		else if (_parentControl instanceof Tree)
//			extent = ((Tree) _parentControl).getItemHeight() - 1;

		if (size > extent)
			size = extent;

		int width = indent + size;
		int height = extent;

		int xoffset = indent;
		int yoffset = (height - size) / 2;

		RGB black = new RGB(0, 0, 0);
		PaletteData dataPalette = new PaletteData(
				new RGB[] { black, black, color });
		data = new ImageData(width, height, 4, dataPalette);
		data.transparentPixel = 0;

		int end = size - 1;
		for (int y = 0; y < size; y++)
		{
			for (int x = 0; x < size; x++)
			{
				if (x == 0 || y == 0 || x == end || y == end)
					data.setPixel(x + xoffset, y + yoffset, 1);
				else
					data.setPixel(x + xoffset, y + yoffset, 2);
			}
		}

		return data;
	}

	public ILabelProvider getLabelFor(Object currentValue)
	{
		ILabelProvider label1 = new LabelProvider()
		{
			public String getText(Object element)
			{
				RGB rgb = (RGB) element;
				String res = "(" + rgb.red + ", " + rgb.green + ", " + rgb.blue + ")";
				return res;
			}

			public Image getImage(Object element)
			{
				Image res = null;
				RGB rgb = (RGB) element;
				ImageData id = createColorImage(rgb);
				ImageData mask = id.getTransparencyMask();
				res = new Image(Display.getCurrent(), id, mask);
				return res;
			}

		};
		return label1;
	}

}
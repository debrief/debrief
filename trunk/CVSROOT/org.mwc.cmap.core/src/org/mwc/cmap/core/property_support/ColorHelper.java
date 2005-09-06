/**
 * 
 */
package org.mwc.cmap.core.property_support;

import java.awt.Color;

import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.*;

public class ColorHelper extends EditorHelper
{
	// Control _parentControl;

	public ColorHelper(Control parentControl)
	{
		super(java.awt.Color.class);
		// _parentControl = parentControl;
	}

	public CellEditor getCellEditorFor(Composite parent)
	{
		return new org.eclipse.jface.viewers.ColorCellEditor(parent);
	}

	public Object translateToSWT(Object value)
	{
		// ok, convert the AWT color to SWT
		Color col = (Color) value;
		return convertColor(col);
	}

	public Object translateFromSWT(Object value)
	{
		// ok, convert the AWT color to SWT
		RGB col = (RGB) value;
		return convertColor(col);
	}

	private static ColorRegistry _colRegistry;

	public static java.awt.Color convertColor(org.eclipse.swt.graphics.RGB swtCol)
	{
		// ok, convert the AWT color to SWT
		java.awt.Color res = null;
		RGB col = (RGB) swtCol;
		res = new Color(col.red, col.green, col.blue);
		return res;
	}

	public static org.eclipse.swt.graphics.Color getColor(java.awt.Color javaCol)
	{

		// check we have our registry
		if (_colRegistry == null)
			_colRegistry = new ColorRegistry();

		final String colName = javaCol.toString();

		// retrieve the color
		org.eclipse.swt.graphics.Color thisCol = _colRegistry.get(colName);

		// ok. do we have the color?
		if (thisCol == null)
		{
			// bugger, we'll have to create it
			RGB newData = new RGB(javaCol.getRed(), javaCol.getGreen(), javaCol
					.getBlue());
			_colRegistry.put(colName, newData);

			// and try to retrieve it again
			thisCol = _colRegistry.get(colName);
		}

		return thisCol;
	}

	public static org.eclipse.swt.graphics.RGB convertColor(java.awt.Color javaCol)
	{
		RGB res = null;
		org.eclipse.swt.graphics.Color thisCol = getColor(javaCol);
		if (thisCol != null)
			res = thisCol.getRGB();
		return res;
	}

	private ImageData createColorImage(RGB color)
	{

		ImageData data = null;

		// GC gc = new GC(_parentControl.getParent().getParent());
		// FontMetrics fm = gc.getFontMetrics();
		// int size = fm.getAscent();
		// gc.dispose();
		int size = 14;

		int indent = 6;
		int extent = 8;

		// if (_parentControl instanceof Table)
		// extent = ((Table) _parentControl).getItemHeight() - 1;
		// else if (_parentControl instanceof Tree)
		// extent = ((Tree) _parentControl).getItemHeight() - 1;

		if (size > extent)
			size = extent;

		int width = indent + size;
		int height = extent;

		int xoffset = indent;
		int yoffset = (height - size) / 2;

		RGB black = new RGB(0, 0, 0);
		PaletteData dataPalette = new PaletteData(new RGB[] { black, black, color });
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

	public static class ColorLabelProvider extends LabelProvider
	{

		public ColorLabelProvider()
		{
		}
		
		
	}
	
	public static class ColorsListProvider extends ArrayContentProvider
	{
		
	}
	
	/**
	 * @param parent
	 * @param property
	 * @return
	 */
	public Control getEditorControlFor(Composite parent,
			final DebriefProperty property)
	{

		// do JFace bits for better color editor
		final ComboViewer myViewer = new ComboViewer(parent);
		myViewer.setLabelProvider(new ColorLabelProvider());
		myViewer.setContentProvider(new ColorsListProvider());

		// also insert a listener
		myViewer.getCombo().addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				// process the new selection
				int index = myViewer.getCombo().getSelectionIndex();
				if (index != -1)
				{
					String res = _theTags[index];
					property.setValue(res);
				}
			}
		});

		return myViewer.getCombo();
	}

}
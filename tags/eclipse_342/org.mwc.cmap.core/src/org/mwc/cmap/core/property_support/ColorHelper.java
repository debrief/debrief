/**
 * 
 */
package org.mwc.cmap.core.property_support;

import java.awt.Color;
import java.util.HashMap;

import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.util.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.mwc.cmap.core.CorePlugin;

public class ColorHelper extends EditorHelper
{
	// Control _parentControl;

	// also keep a local hashmap of colours. we need to generate a string to get a
	// color using
	// the SWT color-registry. we'll keep our own local list instead...
	private static HashMap<Color, org.eclipse.swt.graphics.Color> _myColorList = new HashMap<Color, org.eclipse.swt.graphics.Color>();
	
	private final static java.awt.Color OFF_WHITE = new Color(255,255,254);;

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

		// hmm, we're having trouble with white. is this white?
		if(javaCol.equals(java.awt.Color.WHITE))
		{
			//  right - switch White to off-white. For some reason SWT won't plot white
			javaCol = OFF_WHITE;
		}
		
		// see if we have it in our own private list
		org.eclipse.swt.graphics.Color thisCol = (org.eclipse.swt.graphics.Color) _myColorList
				.get(javaCol);

		if (thisCol == null)
		{

			// check we have our registry
			if (_colRegistry == null)
				_colRegistry = new ColorRegistry();

			String colName = "" + javaCol.getRGB();

			// retrieve the color
			thisCol = _colRegistry.get(colName);

			// ok. do we have the color?
			if (thisCol == null)
			{
				
				
				// bugger, we'll have to create it
				final int red = javaCol.getRed();
				final int green = javaCol.getGreen();
				final int blue = javaCol.getBlue();
				RGB newData = new RGB(red, green, blue);
				_colRegistry.put(colName, newData);

				// and try to retrieve it again
				thisCol = _colRegistry.get(colName);
			}

			// ok, if we found it, try to store it locally
			if (thisCol != null)
				_myColorList.put(javaCol, thisCol);
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

	static ImageData createColorImage(RGB color)
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

		/**
		 * @param element
		 * @return
		 */
		public Image getImage(Object element)
		{
			// RGB rgCol = (RGB) element;
			// ImageData theD = createColorImage(rgCol);
			// Image theI = new Image(Display.getDefault(), theD);
			// return theI;

			String imageKey = "vpf.gif";
			imageKey = ISharedImages.IMG_OBJ_FOLDER;

			Image theImage = PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);

			if (theImage == null)
			{
				// ok, try to get the image from our own registry
				theImage = CorePlugin.getImageFromRegistry(imageKey);
			}

			return theImage;

		}

		/**
		 * @param element
		 * @return
		 */
		public String getText(Object element)
		{
			return super.getText(element);
		}

	}

	public static class ColorsListProvider extends ArrayContentProvider
	{
		/**
		 * @param inputElement
		 * @return
		 */
		public Object[] getElements(Object inputElement)
		{
			RGB[] cols = new RGB[] { new RGB(255, 0, 0), new RGB(0, 255, 0), new RGB(0, 0, 255) };
			return cols;
		}

	}

	/**
	 * @param parent
	 * @param property
	 * @return
	 */
	public Control getEditorControlFor(Composite parent, final DebriefProperty property)
	{

		final ColorSelector sel = new ColorSelector(parent);
		sel.addListener(new IPropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent event)
			{
				RGB theCol = sel.getColorValue();
				property.setValue(theCol);
			}
		});

		// try to set the default color
		RGB current = (RGB) property.getValue();
		sel.setColorValue(current);

		return sel.getButton();

	}

}
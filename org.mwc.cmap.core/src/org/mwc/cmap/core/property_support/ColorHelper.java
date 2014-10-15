/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)

 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.cmap.core.property_support;

import java.awt.Color;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

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
	private static HashMap<Color, org.eclipse.swt.graphics.Color> _myColorList = new LinkedHashMap<Color, org.eclipse.swt.graphics.Color>();

	private final static java.awt.Color OFF_WHITE = new Color(255, 255, 254);

	protected static final int IMAGES_SIZE = 200;

	private final CustomColorsStore _customColorsStore = new CustomColorsStore();
	
	private final Map<RGB, Image> rgbImagesMap = new HashMap<RGB, Image>();

	public ColorHelper(final Control parentControl)
	{
		super(java.awt.Color.class);

		// _parentControl = parentControl;
	}

	@Override
	public CellEditor getCellEditorFor(final Composite parent)
	{
		return new ColorCellEditor(parent);
	}

	@Override
	public Object translateToSWT(final Object value)
	{
		// ok, convert the AWT color to SWT
		final Color col = (Color) value;
		return convertColor(col);
	}

	@Override
	public Object translateFromSWT(final Object value)
	{
		// ok, convert the AWT color to SWT
		final RGB col = (RGB) value;
		return convertColor(col);
	}

	private static ColorRegistry _colRegistry;

	public static java.awt.Color convertColor(final org.eclipse.swt.graphics.RGB swtCol)
	{
		// ok, convert the AWT color to SWT
		java.awt.Color res = null;
		final RGB col = (RGB) swtCol;
		res = new Color(col.red, col.green, col.blue);
		return res;
	}

	public static org.eclipse.swt.graphics.Color getColor(final java.awt.Color javaCol)
	{

		java.awt.Color theColor = javaCol;
		// hmm, we're having trouble with white. is this white?
		if (theColor.equals(java.awt.Color.WHITE))
		{
			// right - switch White to off-white. For some reason SWT won't plot white
			theColor = OFF_WHITE;
		}

		// see if we have it in our own private list
		org.eclipse.swt.graphics.Color thisCol = (org.eclipse.swt.graphics.Color) _myColorList
				.get(theColor);

		if (thisCol == null)
		{

			// check we have our registry
			if (_colRegistry == null)
				_colRegistry = new ColorRegistry();

			final String colName = "" + theColor.getRGB();

			// retrieve the color
			thisCol = _colRegistry.get(colName);

			// ok. do we have the color?
			if (thisCol == null)
			{

				// bugger, we'll have to create it
				final int red = theColor.getRed();
				final int green = theColor.getGreen();
				final int blue = theColor.getBlue();
				final RGB newData = new RGB(red, green, blue);
				_colRegistry.put(colName, newData);

				// and try to retrieve it again
				thisCol = _colRegistry.get(colName);
			}

			// ok, if we found it, try to store it locally
			if (thisCol != null)
				_myColorList.put(theColor, thisCol);
		}
		return thisCol;
	}

	public static org.eclipse.swt.graphics.RGB convertColor(final java.awt.Color javaCol)
	{
		RGB res = null;
		final org.eclipse.swt.graphics.Color thisCol = getColor(javaCol);
		if (thisCol != null)
			res = thisCol.getRGB();
		return res;
	}

	static ImageData createColorImage(final RGB color)
	{

		ImageData data = null;

		// GC gc = new GC(_parentControl.getParent().getParent());
		// FontMetrics fm = gc.getFontMetrics();
		// int size = fm.getAscent();
		// gc.dispose();
		int size = 14;

		final int indent = 6;
		final int extent = 8;

		// if (_parentControl instanceof Table)
		// extent = ((Table) _parentControl).getItemHeight() - 1;
		// else if (_parentControl instanceof Tree)
		// extent = ((Tree) _parentControl).getItemHeight() - 1;

		if (size > extent)
			size = extent;

		final int width = indent + size;
		final int height = extent;

		final int xoffset = indent;
		final int yoffset = (height - size) / 2;

		final RGB black = new RGB(0, 0, 0);
		final PaletteData dataPalette = new PaletteData(new RGB[]
		{ black, black, color });
		data = new ImageData(width, height, 4, dataPalette);
		data.transparentPixel = 0;

		final int end = size - 1;
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

	@Override
	public ILabelProvider getLabelFor(final Object currentValue)
	{
		final ILabelProvider label1 = new LabelProvider()
		{
			public String getText(final Object element)
			{
				final RGB rgb = (RGB) element;
				final String res = "(" + rgb.red + ", " + rgb.green + ", " + rgb.blue + ")";
				return res;
			}

			public Image getImage(final Object element)
			{
				Image res = null;
				final RGB rgb = (RGB) element;
				Image image = rgbImagesMap.get(rgb);
				if (image != null && !image.isDisposed()) {
					return image;
				}
				if (rgbImagesMap.size() > IMAGES_SIZE) {
					Collection<Image> images = rgbImagesMap.values();
					for (Image img:images) {
						if (img!=null && !img.isDisposed()) {
							img.dispose();
						}
					}
					rgbImagesMap.clear();
				}
				final ImageData id = createColorImage(rgb);
				final ImageData mask = id.getTransparencyMask();
				res = new Image(Display.getCurrent(), id, mask);
				rgbImagesMap.put(rgb, res);
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
		public Image getImage(final Object element)
		{
			// RGB rgCol = (RGB) element;
			// ImageData theD = createColorImage(rgCol);
			// Image theI = new Image(Display.getDefault(), theD);
			// return theI;

			String imageKey = "vpf.gif";
			imageKey = ISharedImages.IMG_OBJ_FOLDER;

			Image theImage = PlatformUI.getWorkbench().getSharedImages()
					.getImage(imageKey);

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
		public String getText(final Object element)
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
		public Object[] getElements(final Object inputElement)
		{
			final RGB[] cols = new RGB[]
			{ new RGB(255, 0, 0), new RGB(0, 255, 0), new RGB(0, 0, 255) };
			return cols;
		}

	}

	/**
	 * @param parent
	 * @param property
	 * @return
	 */
	@Override
	public Control getEditorControlFor(final Composite parent,
			final IDebriefProperty property)
	{

		final ColorSelector sel = new ColorSelector(parent);
		sel.addListener(new IPropertyChangeListener()
		{
			public void propertyChange(final PropertyChangeEvent event)
			{
				final RGB theCol = sel.getColorValue();
				property.setValue(theCol);
			}
		});

		// try to set the default color
		final RGB current = (RGB) property.getValue();
		sel.setColorValue(current);

		return sel.getButton();

	}

	class ColorCellEditor extends org.eclipse.jface.viewers.ColorCellEditor
	{

		public ColorCellEditor(final Composite parent)
		{
			super(parent);
		}

		protected Object openDialogBox(final Control cellEditorWindow)
		{
			final ColorDialog dialog = new ColorDialog(cellEditorWindow.getShell());
			Object value = getValue();
			if (value != null)
			{
				dialog.setRGB((RGB) value);
			}

			Vector<RGB> initialColors = _customColorsStore.load();
			// iterate through keys since they are ordered by insertion time
			if (initialColors == null || initialColors.size() == 0)
			{
				initialColors = new Vector<RGB>();
				for (final org.eclipse.swt.graphics.Color color : _myColorList.values())
				{
					initialColors.add(color.getRGB());
				}

			}
			dialog
					.setRGBs((RGB[]) initialColors.toArray(new RGB[initialColors.size()]));
			_customColorsStore.save(initialColors);
			value = dialog.open();
			if (value != null)
				_customColorsStore.addColor((RGB) value);
			return dialog.getRGB();

		}

		@Override
		public void dispose()
		{
			super.dispose();
		}

	}

}
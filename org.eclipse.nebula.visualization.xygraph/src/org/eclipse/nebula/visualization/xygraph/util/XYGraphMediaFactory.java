/*************************************************************************
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *************************************************************************/
package org.eclipse.nebula.visualization.xygraph.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * A factory, which provides convenience methods for the creation of Images and
 * Fonts.
 * 
 * All resources created via this factory get automatically disposed, when the
 * application is stopped.
 * 
 * @author Sven Wende, Xihui Chen
 * @version $Revision$
 * 
 */
public final class XYGraphMediaFactory {
	/**
	 * The shared instance.
	 */
	private static XYGraphMediaFactory _instance;

	/**
	 * The color registry.
	 */
	private ColorRegistry _colorRegistry;

	/**
	 * The image registry.
	 */
	private ImageRegistry _imageRegistry;

	/**
	 * The font registry.
	 */
	private FontRegistry _fontRegistry;

	private HashMap<String, Cursor> cursorRegistry;

	/**
	 * Map that holds the provided image descriptors.
	 */
	private HashMap<ImageDescriptor, Image> _imageCache;

	public final static String CURSOR_GRABBING_PATH = "images/Grabbing.png";
	public final static String CURSOR_GRABBING_ON_AXIS_PATH = "images/GrabbingOnAxis.png";

	public void disposeResources() {

		if (cursorRegistry != null) {
			for (Cursor cursor : cursorRegistry.values()) {
				if (cursor != null && !cursor.isDisposed())
					cursor.dispose();
			}
			cursorRegistry.clear();
		}

	}

	public Cursor getCursor(String cursorImagePath) {
		Cursor cursor = cursorRegistry.get(cursorImagePath);
		if (cursor == null) {
			cursor = new Cursor(Display.getDefault(), getInstance().getImage(cursorImagePath).getImageData(), 8, 8);
			cursorRegistry.put(cursorImagePath, cursor);
		}
		return cursor;

	}

	/**
	 * Private constructor to avoid instantiation.
	 */
	private XYGraphMediaFactory() {
		_colorRegistry = new ColorRegistry();
		_imageRegistry = new ImageRegistry();
		_fontRegistry = new FontRegistry();
		cursorRegistry = new HashMap<String, Cursor>();
		_imageCache = new HashMap<ImageDescriptor, Image>();

		// dispose all images from the image cache, when the display is disposed
		Display.getDefault().addListener(SWT.Dispose, new Listener() {
			public void handleEvent(final Event event) {
				for (Image img : _imageCache.values()) {
					img.dispose();
				}
				disposeResources();
			}
		});

	}

	/**
	 * Return the shared instance of this class.
	 * 
	 * @return The shared instance of this class.
	 */
	public static synchronized XYGraphMediaFactory getInstance() {
		if (_instance == null) {
			_instance = new XYGraphMediaFactory();
		}

		return _instance;
	}

	/**
	 * Create the <code>Color</code> for the given color information.
	 * 
	 * @param r
	 *            red
	 * @param g
	 *            green
	 * @param b
	 *            blue
	 * 
	 * @return The <code>Color</code> for the given color information.
	 */
	public Color getColor(final int r, final int g, final int b) {
		return getColor(new RGB(r, g, b));
	}

	/**
	 * Create the <code>Color</code> for the given <code>RGB</code>.
	 * 
	 * @param rgb
	 *            A <code>RGB</code> object.
	 * @return The <code>Color</code> for the given <code>RGB</code>.
	 */
	public Color getColor(final RGB rgb) {
		assert rgb != null : "rgb!=null"; //$NON-NLS-1$
		Color result = null;

		String key = String.valueOf(rgb.hashCode());

		if (!_colorRegistry.hasValueFor(key)) {
			_colorRegistry.put(key, rgb);
		}

		result = _colorRegistry.get(key);

		return result;
	}

	/**
	 * Create the <code>Font</code> for the given information.
	 * 
	 * @param name
	 *            The font name.
	 * @param height
	 *            The font height.
	 * @param style
	 *            The font style.
	 * @return The <code>Font</code> for the given information.
	 */
	public Font getFont(final String name, final int height, final int style) {
		assert name != null : "name!=null"; //$NON-NLS-1$

		FontData fd = new FontData(name, height, style);

		String key = String.valueOf(fd.hashCode());
		if (!_fontRegistry.hasValueFor(key)) {
			_fontRegistry.put(key, new FontData[] { fd });
		}

		return _fontRegistry.get(key);
	}

	/**
	 * Create the <code>Font</code> for the given <code>FontData</code>.
	 * 
	 * @param fontData
	 *            The <code>FontData</code>
	 * @return The <code>Font</code> for the given <code>FontData</code>
	 */
	public Font getFont(final FontData[] fontData) {
		FontData f = fontData[0];
		return getFont(f.getName(), f.getHeight(), f.getStyle());
	}

	/**
	 * Create the <code>Font</code> for the given <code>FontData</code> and the
	 * given style code.
	 * 
	 * @param fontData
	 *            The <code>FontData</code>
	 * @param style
	 *            The style code.
	 * @return The <code>Font</code> for the given <code>FontData</code> and the
	 *         given style code.
	 */
	public Font getFont(final FontData[] fontData, final int style) {
		FontData f = fontData[0];
		Font font = getFont(f.getName(), f.getHeight(), style);
		return font;
	}

	/**
	 * Create the <code>Font</code> for the given <code>FontData</code> and the
	 * given style code.
	 * 
	 * @param fontData
	 *            The <code>FontData</code>
	 * @return The <code>Font</code> for the given <code>FontData</code> and the
	 *         given style code.
	 */
	public Font getFont(final FontData fontData) {
		Font font = getFont(fontData.getName(), fontData.getHeight(), fontData.getStyle());
		return font;
	}

	/**
	 * Return the system's default font.
	 * 
	 * @param style
	 *            additional styles, e.g. SWT.Bold
	 * @return The system's default font.
	 */
	public Font getDefaultFont(final int style) {
		return Display.getDefault().getSystemFont();
	}

	/**
	 * Register the image to imageRegistry so it can be disposed when Display
	 * disposed.
	 * 
	 * @param key
	 * @param img
	 */
	public void registerImage(final String key, final Image img) {
		_imageRegistry.put(key, img);
	}

	public Image getRegisteredImage(final String key) {
		return _imageRegistry.get(key);
	}

	/**
	 * Load the <code>Image</code> from the given path in the given plugin.
	 * Usually, this is the image found via the the given plug-in relative path.
	 * But this implementation also supports a hack for testing: If no plugin is
	 * running, because for example this is an SWT-only test, the path is used
	 * as is, i.e. relative to the current directory.
	 * 
	 * @param relativePath
	 *            The image's relative path to the root of the plugin.
	 * @return The <code>Image</code> from the given path in the given plugin.
	 */
	public Image getImage(final String relativePath) {
		// Is image already cached in imageRegistry?
		if (_imageRegistry.get(relativePath) == null) {

			InputStream stream = XYGraphMediaFactory.class.getResourceAsStream(relativePath);
			Image image = new Image(Display.getCurrent(), stream);
			try {
				stream.close();
			} catch (IOException ioe) {
			}

			// Must be running as JUnit test or demo w/o plugin environment.
			// The following only works for test code inside this plugin,
			// not when called from other plugins' test code.
			// final Display display = Display.getCurrent();
			// final Image img = new Image(display, relativePath);
			_imageRegistry.put(relativePath, image);

		}
		return _imageRegistry.get(relativePath);
	}

	/**
	 * Register the cursor so it can be disposed when the plugin stopped.
	 * 
	 * @param cursor
	 */
	public void registerCursor(String key, Cursor cursor) {
		cursorRegistry.put(key, cursor);
	}

	/** the color for light blue */
	final static public RGB COLOR_LIGHT_BLUE = new RGB(153, 186, 243);

	/** the color for blue */
	final static public RGB COLOR_BLUE = new RGB(0, 0, 255);

	/** the color for white */
	final static public RGB COLOR_WHITE = new RGB(255, 255, 255);

	/** the color for gray */
	final static public RGB COLOR_GRAY = new RGB(200, 200, 200);

	/** the color for dark gray */
	final static public RGB COLOR_DARK_GRAY = new RGB(150, 150, 150);

	/** the color for black */
	final static public RGB COLOR_BLACK = new RGB(0, 0, 0);

	/** the color for red */
	final static public RGB COLOR_RED = new RGB(255, 0, 0);

	/** the color for green */
	final static public RGB COLOR_GREEN = new RGB(0, 255, 0);

	/** the color for yellow */
	final static public RGB COLOR_YELLOW = new RGB(255, 255, 0);

	/** the color for pink */
	final static public RGB COLOR_PINK = new RGB(255, 0, 255);

	/** the color for cyan */
	final static public RGB COLOR_CYAN = new RGB(0, 255, 255);

	/** the color for orange */
	final static public RGB COLOR_ORANGE = new RGB(255, 128, 0);

	/** the color for orange */
	final static public RGB COLOR_PURPLE = new RGB(128, 0, 255);

	/** the font for Arial in height of 9 */
	final static public FontData FONT_ARIAL = new FontData("Arial", 9, SWT.NONE);

	/** the font for Tahoma in height of 9 */
	final static public FontData FONT_TAHOMA = new FontData("Tahoma", 9, SWT.NONE);
}

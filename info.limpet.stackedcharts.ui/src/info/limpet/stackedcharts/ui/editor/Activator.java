/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/
package info.limpet.stackedcharts.ui.editor;

import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	/**
	 * declare some string constants that we use for a predetermined set of chart
	 * fonts
	 *
	 */
	public static final String FONT_12 = "FONT_12";

	public static final String FONT_10 = "FONT_10";

	public static final String FONT_8 = "FONT_8";

	// The plug-in ID
	public static final String PLUGIN_ID = "info.limpet.stackedcharts.ui"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * our font registry, to reduce object creation
	 *
	 */
	private FontRegistry fontReg;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	public Font getFont(final String name) {
		return fontReg.get(name);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.
	 * BundleContext)
	 */
	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		fontReg = new FontRegistry(Display.getCurrent());

		// initialise some fonts
		fontReg.put(FONT_8, new FontData[] { new FontData("Arial", 8, SWT.None) });
		fontReg.put(FONT_10, new FontData[] { new FontData("Arial", 10, SWT.None) });
		fontReg.put(FONT_12, new FontData[] { new FontData("Arial", 12, SWT.None) });
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(final BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

}

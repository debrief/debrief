/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.cmap.core.property_support;

import java.util.Vector;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.RGB;
import org.mwc.cmap.core.CorePlugin;

public class CustomColorsStore 
{
	IPreferenceStore _store;
	public static final String CUSTOM_COLORS = "CUSTOM_COLORS";
	
	private final String RGB_DELIMITER = "-";
	private final String RGBS_DELIMITER = "/";
	
	public CustomColorsStore()
	{
		_store = CorePlugin.getDefault().getPreferenceStore();
	}
	
	public Vector<RGB> load()
	{
		final Vector<RGB> colors = new Vector<RGB>();
		try
		{
			final String rgbs = _store.getString(CUSTOM_COLORS);
			final String[] rgbs_values = rgbs.split(RGBS_DELIMITER);
			for (final String s: rgbs_values)
			{
				final String[] rgb = s.split(RGB_DELIMITER);
				final int red = Integer.parseInt(rgb[0]);
				final int green = Integer.parseInt(rgb[1]);
				final int blue = Integer.parseInt(rgb[2]);
				colors.add(new RGB(red, green, blue));
			}
		} 
		catch(final Exception e)
		{
			return null;
		}
		return colors;
	}
	
	public void save(final Vector<RGB> initialColors)
	{
		final StringBuffer value = new StringBuffer();
		for (final RGB rgb: initialColors)
		{
			value.append(rgb.red + RGB_DELIMITER + rgb.green + RGB_DELIMITER + rgb.blue);
			value.append(RGBS_DELIMITER);
		}
		_store.setValue(CUSTOM_COLORS, value.toString());
	}
	
	public void addColor(final RGB rgb)
	{
		final StringBuffer res = new StringBuffer();
		res.append(_store.getString(CUSTOM_COLORS));
		res.append(RGBS_DELIMITER);
		res.append(rgb.red + RGB_DELIMITER + rgb.green + RGB_DELIMITER + rgb.blue);
		_store.setValue(CUSTOM_COLORS, res.toString());
	}

}

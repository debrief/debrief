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
		Vector<RGB> colors = new Vector<RGB>();
		try
		{
			String rgbs = _store.getString(CUSTOM_COLORS);
			String[] rgbs_values = rgbs.split(RGBS_DELIMITER);
			for (String s: rgbs_values)
			{
				String[] rgb = s.split(RGB_DELIMITER);
				int red = Integer.parseInt(rgb[0]);
				int green = Integer.parseInt(rgb[1]);
				int blue = Integer.parseInt(rgb[2]);
				colors.add(new RGB(red, green, blue));
			}
		} 
		catch(Exception e)
		{
			return null;
		}
		return colors;
	}
	
	public void save(Vector<RGB> initialColors)
	{
		StringBuffer value = new StringBuffer();
		for (RGB rgb: initialColors)
		{
			value.append(rgb.red + RGB_DELIMITER + rgb.green + RGB_DELIMITER + rgb.blue);
			value.append(RGBS_DELIMITER);
		}
		_store.setValue(CUSTOM_COLORS, value.toString());
	}
	
	public void addColor(RGB rgb)
	{
		StringBuffer res = new StringBuffer();
		res.append(_store.getString(CUSTOM_COLORS));
		res.append(RGBS_DELIMITER);
		res.append(rgb.red + RGB_DELIMITER + rgb.green + RGB_DELIMITER + rgb.blue);
		_store.setValue(CUSTOM_COLORS, res.toString());
	}

}

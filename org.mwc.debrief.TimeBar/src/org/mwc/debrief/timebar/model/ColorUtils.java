package org.mwc.debrief.timebar.model;

import org.eclipse.swt.graphics.Color;

public class ColorUtils 
{
	
	public static Color convertAWTtoSWTColor(final java.awt.Color color)
	{
		return new Color(null, color.getRed(), color.getGreen(), color.getBlue());
	}

}

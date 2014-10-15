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
package org.mwc.debrief.core.gpx.mappers;

import java.awt.Color;
import java.awt.Font;

import org.mwc.debrief.core.gpx.ColourType;
import org.mwc.debrief.core.gpx.FontType;
import org.mwc.debrief.core.gpx.LabelLocationType;

import MWC.GUI.Properties.LocationPropertyEditor;
import MWC.GUI.Shapes.Symbols.SymbolScalePropertyEditor;
import MWC.Utilities.ReaderWriter.XML.Util.ColourHandler;

public final class GpxUtil
{
	public static final ColourHandler COLOUR_HANDLER = new ColourHandler()
	{
		@Override
		public void setColour(final Color res)
		{// empty implementation. we are just interested in the instance of this
			// class
		}
	};

	public static final Color resolveColor(final ColourType colourType)
	{
		Color resolvedColor = COLOUR_HANDLER.resolveColor(colourType.getValue());
		if (resolvedColor == null)
		{
			int r = 0, b = 0, g = 0;

			if (colourType.getCustomRed() != null)
			{
				r = colourType.getCustomRed().intValue();
			}
			else if (colourType.getCustomGreen() != null)
			{
				g = colourType.getCustomGreen().intValue();
			}
			else if (colourType.getCustomBlue() != null)
			{
				b = colourType.getCustomBlue().intValue();
			}
			resolvedColor = new java.awt.Color(r, g, b);
		}
		return resolvedColor;
	}

	public static final Font resolveFont(final FontType fontType)
	{
		if (fontType != null)
		{
			final boolean isBold = fontType.isBold();
			final boolean isItalic = fontType.isItalic();
			int style = Font.PLAIN;
			if (isBold && isItalic)
			{
				style = Font.BOLD | Font.ITALIC;
			}
			else if (isBold)
			{
				style = Font.BOLD;
			}
			else if (isItalic)
			{
				style = Font.ITALIC;
			}

			return new Font(fontType.getFamily(), style, fontType.getSize().intValue());
		}
		return null;
	}

	public static final Integer resolveLabelLocation(final LabelLocationType llt)
	{
		final LocationPropertyEditor locationConverter = new LocationPropertyEditor();
		locationConverter.setAsText(llt.value());
		return (Integer) locationConverter.getValue();
	}

	public static final Double resolveSymbolScale(final String scale)
	{
		final SymbolScalePropertyEditor scaleConverter = new SymbolScalePropertyEditor();
		scaleConverter.setAsText(scale);
		return (Double) scaleConverter.getValue();
	}
}

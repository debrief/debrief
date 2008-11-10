//package symantec.itools.awt.util;
package MWC.GUI.TabPanel;


import java.awt.Color;
import java.util.Hashtable;
import java.lang.IllegalArgumentException;

// 	07/08/97	LAB	Removed checkValidPercent() function, and replaced
//					calls to it with calls to symantec.itools.util.GeneralUtils.checkValidPercent().
// 	08/05/97	LAB	Added lightness, calculateHilightColor, and calculateShadowColor.
//					Updated version to 1.1.  Removed GetColor, and corresponding
//					Hash table for copyright reasons.

// Written by Michael Hopkins, and Levi Brown, 1.0, June 27, 1997.

/**
 * Many useful color related utility functions for color manipulation.
 * <p>
 * @version 1.1, August 5, 1997
 * @author  Symantec
 */
public class ColorUtils
{
    /**
     * Do not use, this is an all-static class.
     */
    public ColorUtils()
    {
    }

	/**
	 * Darkens a given color by the specified percentage.
	 * @param r The red component of the color to darken.
	 * @param g The green component of the color to darken.
	 * @param b The blue component of the color to darken.
	 * @param percent percentage to darken.  Needs to be <= 1 && >= 0.
	 * @return a new Color with the desired characteristics.
     * @exception IllegalArgumentException
     * if the specified percentage value is unacceptable
	 */
	public static Color darken( int r, int g, int b, double percent ) throws IllegalArgumentException
	{
		GeneralUtils.checkValidPercent(percent);

		return new Color( Math.max((int)(r * (1-percent)), 0),
							Math.max((int)(g * (1-percent)),0),
							Math.max((int)(b * (1-percent)),0));
	}

	/**
	 * Darkens a given color by the specified percentage.
	 * @param to the Color to darken.
	 * @param percent percentage to darken.  Needs to be <= 1 && >= 0.
	 * @return a new Color with the desired characteristics.
     * @exception IllegalArgumentException
     * if the specified percentage value is unacceptable
	 */
	public static Color darken( Color c, double percent ) throws IllegalArgumentException
	{
		GeneralUtils.checkValidPercent(percent);

		int r, g, b;
		r = c.getRed();
		g = c.getGreen();
		b = c.getBlue();
		return darken( r, g, b, percent );
	}

	/**
	 * Lightens a given color by the specified percentage.
	 * @param r The red component of the color to lighten.
	 * @param g The green component of the color to lighten.
	 * @param b The blue component of the color to lighten.
	 * @param percent percentage to lighten.  Needs to be <= 1 && >= 0.
	 * @return a new Color with the desired characteristics.
     * @exception IllegalArgumentException
     * if the specified percentage value is unacceptable
	 */
	public static Color lighten( int r, int g, int b, double percent ) throws IllegalArgumentException
	{
		GeneralUtils.checkValidPercent(percent);

		int r2, g2, b2;
		r2 = r + (int)((255 - r) * percent );
		g2 = g + (int)((255 - g) * percent );
		b2 = b + (int)((255 - b) * percent );
		return new Color( r2, g2, b2 );
	}

	/**
	 * Lightens a given color by the specified percentage.
	 * @param to the Color to lighten.
	 * @param percent percentage to lighten.  Needs to be <= 1 && >= 0.
	 * @return a new Color with the desired characteristics.
     * @exception IllegalArgumentException
     * if the specified percentage value is unacceptable
	 */
	public static Color lighten( Color c, double percent ) throws IllegalArgumentException
	{
		GeneralUtils.checkValidPercent(percent);

		int r, g, b;
		r = c.getRed();
		g = c.getGreen();
		b = c.getBlue();
		return lighten( r, g, b, percent );
	}

	/**
	 * Fades from one color to another by the given percentage.
	 * @param from the Color to fade from.
	 * @param to the Color to fade to.
	 * @param percent percentage to fade.  Needs to be <= 1 && >= 0.
	 * @return a new Color with the desired characteristics.
     * @exception IllegalArgumentException
     * if the specified percentage value is unacceptable
	 */
	public static Color fade( Color from, Color to, double percent ) throws IllegalArgumentException
	{
		GeneralUtils.checkValidPercent(percent);

		int from_r, from_g, from_b;
		int to_r, to_g, to_b;
		int r, g, b;

		from_r = from.getRed();
		from_g = from.getGreen();
		from_b = from.getBlue();

		to_r = to.getRed();
		to_g = to.getGreen();
		to_b = to.getBlue();

		if (from_r > to_r)
			r = to_r + (int)((from_r - to_r)* (1 - percent));
		else
			r = to_r - (int)((to_r - from_r)* (1 - percent));
		if (from_g > to_r)
			g = to_g + (int)((from_g - to_g)* (1 - percent));
		else
			g = to_g - (int)((to_g - from_g)* (1 - percent));
		if (from_b > to_b)
			b = to_b + (int)((from_b - to_b)* (1 - percent));
		else
			b = to_b - (int)((to_b - from_b)* (1 - percent));

		return new Color(r, g, b);
	}

	/**
	 * Given a Color this function determines the lightness percent.
	 * @param c The Color to calculate from.  If null, it will return 0.
	 * @return the percent light of the specified color.  This value will be
	 * >= 0 && <= 1.
	 */
	public static double lightness(Color c)
	{
		if(c == null)
			return 0;

		double r, g, b, max, min;
		r = c.getRed();
		g = c.getGreen();
		b = c.getBlue();
		max = (Math.max(r, Math.max(g, b)) / 255) / 2;
		min = (Math.min(r, Math.min(g, b)) / 255) / 2;
		return (max + min);
	}

	/**
	 * Used to calculate a hilight color from a given color.
	 * @param c The color to use in the calculation.  If null, then
	 * it will return null.
	 * @return the newly calculated hilight color.
	 */
	public static Color calculateHilightColor(Color c)
	{
		if(c == null)
			return null;

		double lightness = lightness(c);

		if (lightness >= 0.90)
		{
			return(ColorUtils.darken(c, 0.100));
		}
		else if (lightness <= 0.20)
		{
			return(ColorUtils.lighten(c, 0.600));
		}
		else
		{
			return(ColorUtils.lighten(c, 0.600));
		}
	}

	/**
	 * Used to calculate a shadow color from a given color.
	 * @param c The color to use in the calculation  If null, then
	 * it will return null.
	 * @return the newly calculated shadow color.
	 */
	public static Color calculateShadowColor(Color c)
	{
		if(c == null)
			return null;

		double lightness = lightness(c);

		if (lightness >= 0.90)
		{
			return(ColorUtils.darken(c, 0.250));
		}
		else if (lightness <= 0.20)
		{
			return(ColorUtils.lighten(c, 0.200));
		}
		else
		{
			return(ColorUtils.darken(c, 0.250));
		}
	}
}

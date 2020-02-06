
package com.visutools.nav.bislider;

import java.awt.Color;
import java.awt.SystemColor;
import java.io.Serializable;
import java.text.DecimalFormat;

/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 *******************************************************************************/
public class BiSliderAbstraction implements Serializable {

	// ---------- MODIFIERS|--------------------
	// Type|----------------------------------------------- Name = Init value
	protected final static javax.swing.text.html.parser.ParserDelegator MAXIMUM_VARIABLE_SIZE_FOR_NAME = null;

	static final long serialVersionUID = 4827806780191894336L;

	// Attribut members of this bean
	double MinimumValue = 0;
	double MaximumValue = 100;
	double MinimumColoredValue = MinimumValue;
	double MaximumColoredValue = MaximumValue;
	int SegmentCount = 10;
	double SegmentSize = 10.0f;

	// Used for painting the nn selected gap
	Color SliderBackground = SystemColor.scrollbar;

	// for color table, waht is the color of non selected values ?
	Color DefaultColor = Color.WHITE;
	Color MinimumColor = Color.RED;
	Color MaximumColor = Color.BLUE;

	DecimalFormat DecimalFormater = new DecimalFormat("###.##");
	String Unit = "";
	boolean UniformSegment = false;

	int ArcSize = 0;
	boolean Sound = false;
	boolean Precise = false;

	/**
	 * Description of the Field
	 */
	public int InterpolationMode = SwingBiSlider.CENTRAL_BLACK;

	/**
	 * Constructor
	 */
	public BiSliderAbstraction() {
	}

	/**
	 * return a color table to make the correxpondance between values and colors.
	 *
	 * @return a table of {min value, max value, R, G, B} for each different color
	 *         zone.
	 */
	public double[][] getColorTable() {

		if (SegmentCount == 1) {
			final double ColorTable[][] = new double[1][4];
			ColorTable[0][0] = MinimumColoredValue;
			ColorTable[0][1] = MaximumColoredValue;
			ColorTable[0][2] = MinimumColor.getRGB();
			ColorTable[0][3] = 0; // The segment index
			return ColorTable;
		}

		float Portion = 1f / SegmentCount;

		final int Mini = (int) Math.floor((MinimumColoredValue - MinimumValue) / SegmentSize);
		final int Maxi = (int) Math.ceil((MaximumColoredValue - MinimumValue) / SegmentSize);
		int SelectedSegmentCount = Maxi - Mini;
		if (SelectedSegmentCount <= 0)
			SelectedSegmentCount = 1;
		final double ColorTable[][] = new double[SelectedSegmentCount][4];

		/*
		 * if (InterpolationMode == BiSlider.RGB) { System.err.println(
		 * " Portion="+Portion); System.err.println(
		 * " Maxi="+Maxi+"   Mini="+Mini+"   nbseg="+(Maxi-Mini)); System.err.println(
		 * " Portion="+Portion); System.err.println( " MinimumValue="+MinimumValue);
		 * System.err.println( " MaximumValue="+MaximumValue); System.err.println(
		 * " MinimumColoredValue="+MinimumColoredValue); System.err.println(
		 * " MaximumColoredValue="+MaximumColoredValue); System.err.println(
		 * " SegmentSize="+SegmentSize); System.err.println(
		 * " SegmentCount="+SegmentCount); System.err.println(
		 * " SelectedSegmentCount="+SelectedSegmentCount); System.err.println(
		 * " MinimumColor.getRed()="+ MinimumColor.getRed()); }
		 */
		for (int i = Mini; i < Maxi; i++) {
			ColorTable[i - Mini][0] = MinimumValue + (SegmentSize * i);
			ColorTable[i - Mini][1] = MinimumValue + (SegmentSize * (i + 1));
			ColorTable[i - Mini][3] = i;

			Color NewColor = MinimumColor;
			if (InterpolationMode == SwingBiSlider.RGB) {
				final float dR = ((MaximumColor.getRed() - MinimumColor.getRed()) * Portion);
				final float dG = ((MaximumColor.getGreen() - MinimumColor.getGreen()) * Portion);
				final float dB = ((MaximumColor.getBlue() - MinimumColor.getBlue()) * Portion);
				try {
					NewColor = new Color((int) (MinimumColor.getRed() + i * dR),
							(int) (MinimumColor.getGreen() + i * dG), (int) (MinimumColor.getBlue() + i * dB));
				} catch (final java.lang.IllegalArgumentException IllegalArgumentException_Arg) {
					System.err.println("Error for i=" + i + "dRGB=" + dR + "," + dG + "," + dB + ":" + MinimumColor
							+ ":" + SegmentSize + ":" + SegmentCount);
					IllegalArgumentException_Arg.printStackTrace();
				}
			} else if (InterpolationMode == SwingBiSlider.HSB) {
				final float hsb0[] = Color.RGBtoHSB(MinimumColor.getRed(), MinimumColor.getGreen(),
						MinimumColor.getBlue(), null);
				final float hsb1[] = Color.RGBtoHSB(MaximumColor.getRed(), MaximumColor.getGreen(),
						MaximumColor.getBlue(), null);

				final float dh = (hsb1[0] - hsb0[0]) * Portion;
				final float ds = (hsb1[1] - hsb0[1]) * Portion;
				final float db = (hsb1[2] - hsb0[2]) * Portion;
				/*
				 * System.err.println( "  hsb1[0]="+hsb1[0]); System.err.println(
				 * "  hsb0[0]="+hsb0[0]); System.err.println( "  i      ="+i);
				 * System.err.println( "  dh     ="+dh); System.err.println(
				 * "  i*dh   ="+(dh*i));
				 */
				NewColor = Color.getHSBColor(hsb0[0] + dh * i, hsb0[1] + ds * i, hsb0[2] + db * i);
			} else if (InterpolationMode == SwingBiSlider.CENTRAL_BLACK) {
				Portion = 2f / SegmentCount;
				final float dR1 = ((MaximumColor.getRed() - Color.BLACK.getRed()) * Portion);
				final float dG1 = ((MaximumColor.getGreen() - Color.BLACK.getGreen()) * Portion);
				final float dB1 = ((MaximumColor.getBlue() - Color.BLACK.getBlue()) * Portion);

				final float dR2 = ((Color.BLACK.getRed() - MinimumColor.getRed()) * Portion);
				final float dG2 = ((Color.BLACK.getGreen() - MinimumColor.getGreen()) * Portion);
				final float dB2 = ((Color.BLACK.getBlue() - MinimumColor.getBlue()) * Portion);

				if (SegmentCount == 2 && i == 0)
					NewColor = MinimumColor;
				else if (SegmentCount == 2 && i == 1)
					NewColor = MaximumColor;

				else if (i > SegmentCount / 2) {
					final int k = i - SegmentCount / 2;
					try {
						NewColor = new Color((int) (Color.BLACK.getRed() + k * dR1),
								(int) (Color.BLACK.getGreen() + k * dG1), (int) (Color.BLACK.getBlue() + k * dB1));
					} catch (final Exception Exception_Arg) {
						Exception_Arg.printStackTrace();
						System.out.println("error with ");
						System.out.println("  SegmentCount = " + SegmentCount);
						System.out.println("  dR1 = " + dR1);
						System.out.println("  dG1 = " + dG1);
						System.out.println("  dB1 = " + dB1);
						System.out.println("  Portion = " + Portion);
						System.out.println("  k = " + k);
						NewColor = Color.WHITE;
					}
				} else
					// System.err.println( " i="+i);
					// System.err.println( " dR2="+dR2);
					// System.err.println( " i*dR2="+(dR2*i));
					// System.err.println( " red="+((int)(MinimumColor.getRed() + i * dR2)));
					// System.err.flush();
					NewColor = new Color((int) (MinimumColor.getRed() + i * dR2),
							(int) (MinimumColor.getGreen() + i * dG2), (int) (MinimumColor.getBlue() + i * dB2));

			}

			ColorTable[i - Mini][2] = NewColor.getRGB();

		}

		// xxx bug ici a corriger 0 =out of bound
		if (ColorTable[0][0] != MinimumColoredValue)
			ColorTable[0][0] = MinimumColoredValue;

		if (ColorTable[SelectedSegmentCount - 1][1] != MaximumColoredValue)
			ColorTable[SelectedSegmentCount - 1][1] = MaximumColoredValue;

		return ColorTable;
	} // getColorTable()

	/**
	 * @param DesiredSegmentCount_Arg the SegmentCount we would like to have (if the
	 *                                gap would be divisible by that)
	 * @return the closer possible SegmentCount according a desired SegmentCount
	 */
	public int searchSegmentCount(final int DesiredSegmentCount_Arg) {
		// dumb cases
		if (DesiredSegmentCount_Arg < 1 || ((int) (MaximumValue - MinimumValue)) != (MaximumValue - MinimumValue))
			return 1;

		if (DesiredSegmentCount_Arg > (MaximumValue - MinimumValue))
			return (int) (MaximumValue - MinimumValue);

		// The desired SegmentCount is possible !
		if (((MaximumValue - MinimumValue) % DesiredSegmentCount_Arg) == 0)
			return DesiredSegmentCount_Arg;

		for (int i = 1; DesiredSegmentCount_Arg - i > 1
				|| DesiredSegmentCount_Arg + i < ((MaximumValue - MinimumValue) / 2); i++)
			if (DesiredSegmentCount_Arg - i > 1 && ((MaximumValue - MinimumValue) % (DesiredSegmentCount_Arg - i)) == 0)
				return DesiredSegmentCount_Arg - i;

			else if (DesiredSegmentCount_Arg + i < ((MaximumValue - MinimumValue) / 2)
					&& ((MaximumValue - MinimumValue) % (DesiredSegmentCount_Arg + i)) == 0)
				return DesiredSegmentCount_Arg + i;
		return 1;
	} // searchSegmentCount()

	/**
	 * change the size of the color segments
	 *
	 * @param SegmentSize_Arg The new segmentSize value
	 */
	public void setSegmentSize(final double SegmentSize_Arg) {
		SegmentSize = SegmentSize_Arg;
		SegmentCount = (int) Math.ceil((MaximumValue - MinimumValue) / SegmentSize_Arg);
	}

	/**
	 * display the table of values as a String
	 *
	 * @return Description of the Return Value
	 */
	@Override
	public String toString() {

		final double[][] ColorArray = getColorTable();

		final StringBuffer SB1 = new StringBuffer();
		SB1.append("Color table with " + ColorArray.length + " segments\n");
		SB1.append("MinimumValue        = " + MinimumValue + "\n");
		SB1.append("MinimumColoredValue = " + MinimumColoredValue + "\n");
		SB1.append("MaximumColoredValue = " + MaximumColoredValue + "\n");
		SB1.append("MaximumValue        = " + MaximumValue + "\n");
		SB1.append("SegmentSize         = " + SegmentSize + "\n");
		SB1.append("SegmentCount        = " + SegmentCount + "\n");

		for (int k = 0; k < ColorArray.length; k++) {
			final Color Color1 = new Color((int) ColorArray[k][2]);
			SB1.append(ColorArray[k][0] + "-->" + ColorArray[k][1] + " with R=" + Color1.getRed() + ", G="
					+ Color1.getGreen() + ", B=" + Color1.getBlue() + "\n");
		}
		SB1.append("\n");

		return SB1.toString();
	} // toString()
}

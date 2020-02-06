
package com.visutools.nav.bislider;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;

import com.visutools.nav.bislider.BiSliderPresentation.FormatLong;

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

public class SwingBiSlider extends JComponent {
	// ---------- MODIFIERS|--------------------
	// Type|----------------------------------------------- Name = Init value
	protected final static javax.swing.text.html.parser.ParserDelegator MAXIMUM_VARIABLE_SIZE_FOR_NAME = null;

	static final long serialVersionUID = 8045586992642853136L;

	// The 3 kinds of BiSlider.
	/**
	 * Color shading where purple is between red and blue (for instance)
	 */
	public final static int RGB = 100;

	/**
	 * Color shading where all the rainbow colors are between blue and red (for
	 * instance)
	 */
	public final static int HSB = 101;

	/**
	 * Color shading where black is between red and blue (for instance)
	 */
	public final static int CENTRAL_BLACK = 102;

	/**
	 * Maximum numbre of segments
	 **/
	public static final int MAX_SEGMENT_COUNT = 100;

	// The two others facets of the bean
	protected BiSliderAbstraction Abstr = null;

	protected BiSliderPresentation Pres = null;

	protected ColorisationSupport ColorisationSupport1 = new ColorisationSupport();

	protected ContentPainterSupport ContentPainterSupport1 = new ContentPainterSupport();

	/**
	 * Constructor with HSB interpolation by default.
	 */
	public SwingBiSlider(final FormatLong formatter) {
		this(HSB, formatter);
	} // Constructor

	/**
	 * Constructor of an horizontal BiSlider.
	 *
	 * @param InterpolationMode_Arg how the bean is supposed interpolate colors
	 *                              between the 2 edges.
	 */
	public SwingBiSlider(final int InterpolationMode_Arg, final FormatLong formatter) {
		super();
		setDoubleBuffered(false);

		// setMinimumSize(new Dimension(50, 50));
		// setPreferredSize(new Dimension(50, 50));
		Abstr = new BiSliderAbstraction();
		setBackground(Abstr.DefaultColor);
		Pres = new BiSliderPresentation(this, ContentPainterSupport1, formatter);
		Abstr.InterpolationMode = InterpolationMode_Arg;

	} // Constructor

	/**
	 * Registers ColorisationListener to receive events.
	 *
	 * @param Listener_Arg The listener to register.
	 */
	public synchronized void addColorisationListener(final ColorisationListener Listener_Arg) {
		ColorisationSupport1.addColorisationListener(Listener_Arg);
	}// addColorisationListener()

	/**
	 * Registers ContentPainterListener to receive events.
	 *
	 * @param Listener_Arg The listener to register.
	 */
	public synchronized void addContentPainterListener(final ContentPainterListener Listener_Arg) {
		ContentPainterSupport1.addContentPainterListener(Listener_Arg);
	}// addContentPainterListener()

	/**
	 * Creates a default popup-menu to use with a bislider
	 *
	 * @return the created popup menu ready to be displayed
	 **/
	public JPopupMenu createPopupMenu() {
		final SwingBiSlider Myself = this;
		final ButtonGroup ButtonGroup1 = new ButtonGroup();

		final JPopupMenu JPopupMenu1 = new JPopupMenu("BiSlider");
		JMenuItem JMenuItem1 = new JRadioButtonMenuItem("RGB", getInterpolationMode() == SwingBiSlider.RGB);
		JMenuItem1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent ActionEvent_Arg) {
				setInterpolationMode(SwingBiSlider.RGB);
				repaint();
			}
		});
		ButtonGroup1.add(JMenuItem1);
		JPopupMenu1.add(JMenuItem1);

		JMenuItem1 = new JRadioButtonMenuItem("HSB", getInterpolationMode() == SwingBiSlider.HSB);
		JMenuItem1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent ActionEvent_Arg) {
				setInterpolationMode(SwingBiSlider.HSB);
				repaint();
			}
		});
		ButtonGroup1.add(JMenuItem1);
		JPopupMenu1.add(JMenuItem1);

		JMenuItem1 = new JRadioButtonMenuItem("CENTRAL", getInterpolationMode() == SwingBiSlider.CENTRAL_BLACK);
		JMenuItem1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent ActionEvent_Arg) {
				setInterpolationMode(SwingBiSlider.CENTRAL_BLACK);
				repaint();
			}
		});
		ButtonGroup1.add(JMenuItem1);
		JPopupMenu1.add(JMenuItem1);

		JPopupMenu1.add(new JSeparator());

		JMenuItem1 = new JMenuItem("Shrink");
		JMenuItem1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent ActionEvent_Arg) {
				setMinimumValue(getMinimumColoredValue());
				setMaximumValue(getMaximumColoredValue());
				repaint();
			}
		});
		JPopupMenu1.add(JMenuItem1);

		JMenuItem1 = new JCheckBoxMenuItem("Uniform", isUniformSegment());
		JMenuItem1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent ActionEvent_Arg) {
				final JCheckBoxMenuItem JCheckBoxMenuItem1 = (JCheckBoxMenuItem) ActionEvent_Arg.getSource();
				setUniformSegment(JCheckBoxMenuItem1.isSelected());
				repaint();
			}
		});
		JPopupMenu1.add(JMenuItem1);
		JMenuItem1 = new JCheckBoxMenuItem("Sound", isSound());
		JMenuItem1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent ActionEvent_Arg) {
				final JCheckBoxMenuItem JCheckBoxMenuItem1 = (JCheckBoxMenuItem) ActionEvent_Arg.getSource();
				setSound(JCheckBoxMenuItem1.isSelected());
				repaint();
			}
		});
		JPopupMenu1.add(JMenuItem1);
		JMenuItem1 = new JCheckBoxMenuItem("Precise", isPrecise());
		JMenuItem1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent ActionEvent_Arg) {
				final JCheckBoxMenuItem JCheckBoxMenuItem1 = (JCheckBoxMenuItem) ActionEvent_Arg.getSource();
				setPrecise(JCheckBoxMenuItem1.isSelected());
				repaint();
			}
		});
		JPopupMenu1.add(JMenuItem1);
		/*
		 * JMenuItem1 = new JCheckBoxMenuItem("Horizontal", isHorizontal());
		 * JMenuItem1.addActionListener(new ActionListener(){ public void
		 * actionPerformed(ActionEvent ActionEvent_Arg) { JCheckBoxMenuItem
		 * JCheckBoxMenuItem1 = (JCheckBoxMenuItem)ActionEvent_Arg.getSource();
		 * //setHorizontal(JCheckBoxMenuItem1.isSelected()); repaint(); } });
		 * JPopupMenu1.add(JMenuItem1);
		 */
		if (!isHorizontal()) {
			boolean test = false;
			try {
				test = isMinOnTop();
			} catch (final Exception Exception_Arg) {
				Exception_Arg.printStackTrace();
			}
			JMenuItem1 = new JCheckBoxMenuItem("MinOnTop", test);
			JMenuItem1.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent ActionEvent_Arg) {
					final JCheckBoxMenuItem JCheckBoxMenuItem1 = (JCheckBoxMenuItem) ActionEvent_Arg.getSource();
					try {
						setMinOnTop(JCheckBoxMenuItem1.isSelected());
					} catch (final Exception Exception_Arg) {
						Exception_Arg.printStackTrace();
					}
					repaint();
				}
			});
			JPopupMenu1.add(JMenuItem1);
		}

		JPopupMenu1.add(new JSeparator());

		JMenuItem1 = new JMenuItem("= Min", new Icon() {
			@Override
			public int getIconHeight() {
				return 14;
			}

			@Override
			public int getIconWidth() {
				return 14;
			}

			@Override
			public void paintIcon(final Component Component_Arg, final Graphics Graphics_Arg, final int X_Arg,
					final int Y_Arg) {
				final Color Color1 = Graphics_Arg.getColor();
				Graphics_Arg.setColor(getMinimumColor());
				Graphics_Arg.fillRect(1, 1, Component_Arg.getHeight() - 2, Component_Arg.getHeight() - 2);
				Graphics_Arg.setColor(Color1);
			}
		});
		JMenuItem1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent ActionEvent_Arg) {
				final Color Color1 = JColorChooser.showDialog(Myself, "Choose a color for minimum values",
						getMinimumColor());
				if (Color1 != null)
					setMinimumColor(Color1);
				repaint();
			}
		});
		JPopupMenu1.add(JMenuItem1);

		JMenuItem1 = new JMenuItem("= Max", new Icon() {
			@Override
			public int getIconHeight() {
				return 14;
			}

			@Override
			public int getIconWidth() {
				return 14;
			}

			@Override
			public void paintIcon(final Component Component_Arg, final Graphics Graphics_Arg, final int X_Arg,
					final int Y_Arg) {
				final Color Color1 = Graphics_Arg.getColor();
				Graphics_Arg.setColor(getMaximumColor());
				Graphics_Arg.fillRect(1, 1, Component_Arg.getHeight() - 2, Component_Arg.getHeight() - 2);
				Graphics_Arg.setColor(Color1);
			}
		});
		JMenuItem1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent ActionEvent_Arg) {
				final Color Color1 = JColorChooser.showDialog(Myself, "Choose a color for maximum values",
						getMaximumColor());
				if (Color1 != null)
					setMaximumColor(Color1);
				repaint();
			}
		});
		JPopupMenu1.add(JMenuItem1);

		return JPopupMenu1;
	}// createPopupMenu()

	/**
	 * Gets the ArcSize attribute of the BiSlider object. ArcSize is for rounded
	 * presentation
	 *
	 * @return The ArcSize value
	 */
	public int getArcSize() {
		return Abstr.ArcSize;
	}// getArcSize()

	/**
	 * to produce re-usable colorization we must provide a Colorizer
	 *
	 * @return The colorizer value
	 */
	public Colorizer getColorizer() {
		return ColorisationSupport1.createColorisationEvent(this, Abstr.getColorTable());
	}// getColorizer()

	/**
	 * Gets the colorTable attribute of the BiSlider object. The table describe the
	 * segments and the colors like this one : <br>
	 *
	 * <li>ColorTable[0][0] = 0 ColorTable[0][1] = 5 ColorTable[0][2] =
	 * Color.RED.getRGB();
	 * <li>ColorTable[1][0] = 5 ColorTable[1][1] = 10 ColorTable[1][2] = somewhere
	 * Purple;
	 * <li>ColorTable[2][0] = 10 ColorTable[2][1] = 15 ColorTable[2][2] =
	 * Color.BLUE.getRGB();
	 *
	 * @return The colorTable value
	 */
	public double[][] getColorTable() {
		return Abstr.getColorTable();
	}// getColorTable()

	/**
	 * @return the DecimalFormat used to display nimbers on the BiSlider
	 **/
	public DecimalFormat getDecimalFormater() {
		return Abstr.DecimalFormater;
	}// getDecimalFormater()

	/**
	 * Gets the defaultColor attribute of the BiSlider object. Default backgound
	 * color is white. It is the color of the value outside the gap.
	 *
	 * @return The defaultColor value
	 */
	public Color getDefaultColor() {
		return Abstr.DefaultColor;
	}// getDefaultColor()

	/**
	 * Gets the InterpolationMode attribute of the BiSlider object. How it
	 * interpolates colors between maxi and mini color.
	 *
	 * @return The InterpolationMode value
	 */
	public int getInterpolationMode() {
		return Abstr.InterpolationMode;
	}// getInterpolationMode

	/**
	 * Gets the maximumColor attribute of the BiSlider object. Default color for the
	 * maximum value is blue
	 *
	 * @return The maximumColor value
	 */
	public Color getMaximumColor() {
		return Abstr.MaximumColor;
	}// getMaximumColor()

	/**
	 * Gets the maximumColoredValue attribute of the BiSlider object
	 *
	 * @return The maximumColoredValue value
	 */
	public double getMaximumColoredValue() {
		return Abstr.MaximumColoredValue;
	} // getMaximumColoredValue()

	/**
	 * Gets the maximumValue attribute of the BiSlider object. Above this value
	 * everything will have the default color
	 *
	 * @return The maximumValue value
	 */
	public double getMaximumValue() {
		return Abstr.MaximumValue;
	}// getMaximumValue()

	/**
	 * Gets the minimumColor attribute of the BiSlider object. Default color for
	 * minimum value is red.
	 *
	 * @return The minimumColor value
	 */
	public Color getMinimumColor() {
		return Abstr.MinimumColor;
	}// getMinimumColor

	/**
	 * Gets the minimumColoredValue attribute of the BiSlider object
	 *
	 * @return the minimum colored value. Under this value everything will have the
	 *         default color.
	 */
	public double getMinimumColoredValue() {
		return Abstr.MinimumColoredValue;
	} // getMinimumColoredValue()

	/**
	 * Gets the minimumValue attribute of the BiSlider object
	 *
	 * @return The minimumValue value
	 */
	public double getMinimumValue() {
		return Abstr.MinimumValue;
	} // getMinimumValue()

	/**
	 * Gets the SegmentCount attribute of the BiSlider object. The SegmentCount mean
	 * how many subdivision there are in the coloration. the gap must be able to be
	 * divided by the SegmentCount. Otherwise the system will find a correct one for
	 * you.
	 *
	 * @return The SegmentCount value
	 */
	public int getSegmentCount() {
		return Abstr.SegmentCount;
	} // getSegmentCount()

	/**
	 * Gets the SegmentSize attribute of the BiSlider object. The SegmentSize means
	 * the size of the subdivisions. in UniformSegment Mode, SegmentSize must be a
	 * perfect division of the global gap, otherwise the system will change the
	 * desired size to something else.
	 *
	 * @return The SegmentCount value
	 */
	public double getSegmentSize() {
		return Abstr.SegmentSize;
	}// getSegmentSize()

	/**
	 * Gets the SliderBackground attribute of the BiSlider object. SliderBackground
	 * color is grayby default
	 *
	 * @return The SliderBackground value
	 */
	public Color getSliderBackground() {
		return Abstr.SliderBackground;
	}// getSliderBackground()

	/**
	 * Gets the unit attribute of the BiSlider object (sec, mph, cm, inches, etc.)
	 *
	 * @return The unit value
	 */
	public String getUnit() {
		return Abstr.Unit;
	} // getUnit()

	/**
	 * Gets the Horizontal attribute of the BiSlider object. Default Horizontal
	 * depends on constructor used !
	 *
	 * @return The Horizontal value
	 */
	public boolean isHorizontal() {
		return true;
	}// isHorizontal()

	/**
	 * Gets the MinOnTop attribute of the BiSlider object. Default MinOnTop is true.
	 *
	 * @return The MinOnTop value
	 */
	public boolean isMinOnTop() throws Exception {
		throw (new Exception("can't tell if minimum value is on top for horizontal BiSliders"));
	}// isMinOnTop()

	/**
	 * Gets the Precise attribute of the BiSlider object. Default Precise is true.
	 *
	 * @return The Precise value
	 */
	public boolean isPrecise() {
		return Abstr.Precise;
	}// isPrecise()

	/**
	 * Gets the Sound attribute of the BiSlider object. Default Sound is true.
	 *
	 * @return The Sound value
	 */
	public boolean isSound() {
		return false;
	}// isSound()

	/**
	 * Gets the UniformSegment attribute of the BiSlider object. Default
	 * UniformSegment is true. Tells if all the segments must have the sme size
	 *
	 * @return The UniformSegment value
	 */
	public boolean isUniformSegment() {
		return Abstr.UniformSegment;
	}// isUniformSegment()

	/**
	 * Method called by java when the component needs to be refreshed
	 *
	 * @param Graphics_Arg Description of the Parameter
	 */
	@Override
	public void paint(final Graphics Graphics_Arg) {

		Pres.setRulerValues(Abstr.MinimumColoredValue, Abstr.MaximumColoredValue);

		Pres.paint(Graphics_Arg);
		// super.paintComponent(Graphics_Arg);
	}// paint();

	/**
	 * Removes ColorisationListener from the list of listeners.
	 *
	 * @param Listener_Arg The listener to remove.
	 */
	public synchronized void removeColorisationListener(final ColorisationListener Listener_Arg) {
		ColorisationSupport1.removeColorisationListener(Listener_Arg);
	}// removeColorisationListener()

	/**
	 * Removes ContentPainterListener from the list of listeners.
	 *
	 * @param Listener_Arg The listener to remove.
	 */
	public synchronized void removeContentPainterListener(final ContentPainterListener Listener_Arg) {
		ContentPainterSupport1.removeContentPainterListener(Listener_Arg);
	}// removeContentPainterListener()

	/**
	 * Sets the ArcSize attribute of the BiSlider object. ArcSize is for rounded
	 * presentation
	 *
	 * @param ArcSize_Arg The new ArcSize value
	 */
	public void setArcSize(final int ArcSize_Arg) {
		Abstr.ArcSize = ArcSize_Arg;
	}// setArcSize()

	/**
	 * Sets the coloredValues attribute of the BiSlider object. Under the minimum
	 * value and above the maximum value everything will have the default color
	 *
	 * @param MinValue_Arg The new coloredValues minimum value
	 * @param MaxValue_Arg The new coloredValues maximum value
	 */
	public void setColoredValues(final double MinValue_Arg, final double MaxValue_Arg) {

		if (Abstr != null && Pres != null && MinValue_Arg <= MaxValue_Arg && MinValue_Arg <= Abstr.MaximumValue
				&& MaxValue_Arg >= Abstr.MinimumValue) {

			Abstr.MinimumColoredValue = MinValue_Arg;
			Abstr.MaximumColoredValue = MaxValue_Arg;

			Pres.setRulerValues(Abstr.MinimumColoredValue, Abstr.MaximumColoredValue);
			repaint();
			ColorisationSupport1.fireNewColors(this, Abstr.getColorTable());
		} /*
			 * else { Debug.debug(0, "problem with setColoredValues"); Debug.debug(0,
			 * "  MinValue_Arg = "+MinValue_Arg); Debug.debug(0,
			 * "  MaxValue_Arg = "+MaxValue_Arg); Debug.debug(0,
			 * "  Abstr.MinimumValue = "+Abstr.MinimumValue); Debug.debug(0,
			 * "  Abstr.MaximumValue = "+Abstr.MaximumValue); Debug.debug(0, ""); }
			 */
	} // setColoredValues()

	/**
	 * @param DecimalFormat_Arg the DecimalFormat used to display nimbers on the
	 *                          BiSlider
	 **/
	public void setDecimalFormater(final DecimalFormat DecimalFormat_Arg) {
		Abstr.DecimalFormater = DecimalFormat_Arg;
	}// setDecimalFormater()

	/**
	 * Sets the defaultColor attribute of the BiSlider object. Default backgound
	 * color is white. It is the color of the value outside the gap.
	 *
	 * @param Color_Arg The new defaultColor value
	 */
	public void setDefaultColor(final Color Color_Arg) {
		Abstr.DefaultColor = Color_Arg;
	}// setDefaultColor()

	/**
	 * Sets the Horizontal attribute of the BiSlider object. Default Horizontal
	 * depends on constructor used.
	 *
	 * @param Horizontal_Arg The new Horizontal value
	 */
	public void setHorizontal(final boolean Horizontal_Arg) /* throws Exception */
	{

		removeMouseListener(Pres);
		removeMouseMotionListener(Pres);
		removeComponentListener(Pres);
		Pres = null;
		Pres = new BiSliderPresentation(this, ContentPainterSupport1, null);

		// ((BiSlider_V_Presentation)Pres).Horizontal = Horizontal_Arg;
		// else throw(new Exception("can't tell if min on top for horizzontal
		// BiSliders"));
	}// setHorizontal()

	/**
	 * Sets the InterpolationMode attribute of the BiSlider object. How it
	 * interpolates colors between maxi and mini color.
	 *
	 * @param InterpolationMode_Arg The new maximumColor value
	 */
	public void setInterpolationMode(final int InterpolationMode_Arg) {
		Abstr.InterpolationMode = InterpolationMode_Arg;
		ColorisationSupport1.fireNewColors(this, Abstr.getColorTable());
	}// setInterpolationMode()

	/**
	 * Sets the maximumColor attribute of the BiSlider object. Default color for the
	 * maximum value is blue
	 *
	 * @param MaximumColor_Arg The new maximumColor value
	 */
	public void setMaximumColor(final Color MaximumColor_Arg) {
		Abstr.MaximumColor = MaximumColor_Arg;
		ColorisationSupport1.fireNewColors(this, Abstr.getColorTable());
	}// setMaximumColor()

	/**
	 * Sets the maximumColoredValue attribute of the BiSlider object. Above this
	 * value everything will have the default color
	 *
	 * @param MaximumColoredValue_Arg The new maximumColoredValue value
	 */
	public void setMaximumColoredValue(final double MaximumColoredValue_Arg) {
		if (Abstr != null && Pres != null && MaximumColoredValue_Arg >= Abstr.MinimumValue
				&& MaximumColoredValue_Arg <= Abstr.MaximumValue) {
			Abstr.MaximumColoredValue = MaximumColoredValue_Arg;
			Pres.setRulerValues(Abstr.MinimumColoredValue, Abstr.MaximumColoredValue);
			repaint();
			ColorisationSupport1.fireNewColors(this, Abstr.getColorTable());
		}
	} // setMaximumColoredValue()

	/**
	 * Sets the maximumValue attribute of the BiSlider object. Above this value
	 * everything will have the default color
	 *
	 * @param MaxValue_Arg The new maximumValue value
	 */
	public void setMaximumValue(final double MaxValue_Arg) {

		if (MaxValue_Arg <= Abstr.MinimumValue)
			Abstr.MinimumValue = MaxValue_Arg - 1;

		Abstr.MaximumValue = MaxValue_Arg;

		if (MaxValue_Arg < Abstr.MinimumColoredValue)
			Abstr.MinimumColoredValue = MaxValue_Arg;

		if (MaxValue_Arg < Abstr.MaximumColoredValue)
			Abstr.MaximumColoredValue = MaxValue_Arg;

		if (isUniformSegment() && ((Abstr.MaximumValue - Abstr.MinimumValue) % Abstr.SegmentCount) != 0)
			Abstr.SegmentCount = Abstr.searchSegmentCount(Abstr.SegmentCount);

		Pres.setRulerValues(Abstr.MinimumColoredValue, Abstr.MaximumColoredValue);
		setSegmentSize(Abstr.SegmentSize);
		repaint();
		ColorisationSupport1.fireNewColors(this, Abstr.getColorTable());
	}// setMaximumValue()

	/**
	 * Sets the minimumColor attribute of the BiSlider object. Default color for
	 * minimum value is red.
	 *
	 * @param MinimumColor_Arg The new minimumColor value
	 */
	public void setMinimumColor(final Color MinimumColor_Arg) {
		Abstr.MinimumColor = MinimumColor_Arg;
		ColorisationSupport1.fireNewColors(this, Abstr.getColorTable());
	}// setMinimumColor()

	/**
	 * Sets the minimumColoredValue attribute of the BiSlider object
	 *
	 * @param NewValue_Arg the minimum colored value. Under this value everything
	 *                     will have the default color.
	 */
	public void setMinimumColoredValue(final double NewValue_Arg) {

		if (Abstr != null && Pres != null && NewValue_Arg >= Abstr.MinimumValue && NewValue_Arg <= Abstr.MaximumValue) {
			Abstr.MinimumColoredValue = NewValue_Arg;
			Pres.setRulerValues(Abstr.MinimumColoredValue, Abstr.MaximumColoredValue);
			repaint();
			ColorisationSupport1.fireNewColors(this, Abstr.getColorTable());
		}
	} // setMinimumColoredValue()

	/**
	 * Sets the minimumValue attribute of the BiSlider object. Under this value
	 * everything will have the default color
	 *
	 * @param MinValue_Arg The new minimumValue value
	 */
	public void setMinimumValue(final double MinValue_Arg) {

		if (MinValue_Arg >= Abstr.MaximumValue)
			Abstr.MaximumValue = MinValue_Arg + 1;

		Abstr.MinimumValue = MinValue_Arg;
		if (MinValue_Arg > Abstr.MinimumColoredValue)
			Abstr.MinimumColoredValue = MinValue_Arg;
		if (MinValue_Arg > Abstr.MaximumColoredValue)
			Abstr.MaximumColoredValue = MinValue_Arg;

		if (isUniformSegment() && ((Abstr.MaximumValue - Abstr.MinimumValue) % Abstr.SegmentCount) != 0)
			Abstr.SegmentCount = Abstr.searchSegmentCount(Abstr.SegmentCount);

		Pres.setRulerValues(Abstr.MinimumColoredValue, Abstr.MaximumColoredValue);
		setSegmentSize(Abstr.SegmentSize);
		repaint();
		ColorisationSupport1.fireNewColors(this, Abstr.getColorTable());
	} // setMinimumValue()

	/**
	 * Sets the MinOnTop attribute of the BiSlider object. Default MinOnTop is
	 * false.
	 *
	 * @param MinOnTop_Arg The new MinOnTop value
	 */
	public void setMinOnTop(final boolean MinOnTop_Arg) throws Exception {
		throw (new Exception("can't set the minimun vertical position for horizontal BiSliders"));
	}// setMinOnTop()

	/**
	 * Sets the all the parameters of the BiSlider together.
	 *
	 * @param InterpolationMode_Arg The new maximumColor value
	 */
	public void setParameters(final int InterpolationMode_Arg, final boolean UniformSegment_Arg,
			final double SegmentSize_Arg, final Color MinimumColor_Arg, final Color MaximumColor_Arg,
			final double MinValue_Arg, final double MaxValue_Arg, final double MinimumColoredValue_Arg,
			final double MaximumColoredValue_Arg) {

		Abstr.MinimumColoredValue = MinimumColoredValue_Arg;
		Abstr.MaximumColoredValue = MaximumColoredValue_Arg;
		Abstr.MinimumValue = MinValue_Arg;
		Abstr.MaximumValue = MaxValue_Arg;
		Abstr.MinimumColor = MinimumColor_Arg;
		Abstr.MaximumColor = MaximumColor_Arg;
		Abstr.UniformSegment = UniformSegment_Arg;
		Abstr.InterpolationMode = InterpolationMode_Arg;
		setSegmentSize(SegmentSize_Arg);
	}// setInterpolationMode()

	/**
	 * Sets the Precise attribute of the BiSlider object. Default Precise is false.
	 *
	 * @param Precise_Arg The new Precise value
	 */
	public void setPrecise(final boolean Precise_Arg) {
		Abstr.Precise = Precise_Arg;
	}// setPrecise()

	/**
	 * Sets the SegmentSize attribute of the BiSlider object. The SegmentSize means
	 * the size of the subdivisions. in UniformSegment Mode, SegmentSize must be a
	 * perfect division of the global gap, otherwise the system will change the
	 * desired size to something else.
	 *
	 * @param SegmentSize_Arg The desired Segment size
	 */
	public void setSegmentSize(final double SegmentSize_Arg) {
		if (SegmentSize_Arg == 0d)
			return;

		if (Abstr.UniformSegment && ((long) SegmentSize_Arg) == SegmentSize_Arg) {
			final int NewSegmentCount = Abstr
					.searchSegmentCount((int) ((Abstr.MaximumValue - Abstr.MinimumValue) / SegmentSize_Arg));
			Abstr.SegmentCount = NewSegmentCount;
			Abstr.SegmentSize = (Abstr.MaximumValue - Abstr.MinimumValue) / Abstr.SegmentCount;
		} else if (Abstr.UniformSegment) {
			final int NewSegmentCount = (int) Math.round((Abstr.MaximumValue - Abstr.MinimumValue) / SegmentSize_Arg);
			Abstr.SegmentCount = NewSegmentCount;
			Abstr.SegmentSize = (Abstr.MaximumValue - Abstr.MinimumValue) / Abstr.SegmentCount;
		} else {
			Abstr.SegmentSize = SegmentSize_Arg;
			Abstr.SegmentCount = (int) Math.ceil((Abstr.MaximumValue - Abstr.MinimumValue) / SegmentSize_Arg);
		}

		if (Abstr.SegmentCount > MAX_SEGMENT_COUNT) {
			Abstr.SegmentCount = MAX_SEGMENT_COUNT;
			Abstr.SegmentSize = (Abstr.MaximumValue - Abstr.MinimumValue) / MAX_SEGMENT_COUNT;
			// Abstr.UniformSegment = false;
		}
		if (Abstr.SegmentCount <= 0) {
			Abstr.SegmentCount = 1;
			Abstr.SegmentSize = Abstr.MaximumValue - Abstr.MinimumValue;
		}

		repaint();
		ColorisationSupport1.fireNewColors(this, Abstr.getColorTable());
	}// setSegmentSize()

	/**
	 * Sets the SliderBackground attribute of the BiSlider object. SliderBackground
	 * color is gray by default
	 *
	 * @param Color_Arg The new defaultColor value
	 */
	public void setSliderBackground(final Color Color_Arg) {
		Abstr.SliderBackground = Color_Arg;
	}// setSliderBackground()

	/**
	 * Sets the Sound attribute of the BiSlider object. Default Sound is false.
	 *
	 * @param Sound_Arg The new Sound value
	 */
	public void setSound(final boolean Sound_Arg) {
		Abstr.Sound = Sound_Arg;
	}// setSound()

	/**
	 * Sets the UniformSegment attribute of the BiSlider object. Default
	 * UniformSegment is true. Tells if all the segments must have the sme size
	 *
	 * @param UniformSegment_Arg The new UniformSegment value
	 */
	public void setUniformSegment(final boolean UniformSegment_Arg) {
		Abstr.UniformSegment = UniformSegment_Arg;

		if (UniformSegment_Arg) {
			setSegmentSize(getSegmentSize());
		}
	}// setUniformSegment()

	/**
	 * Sets the unit attribute of the BiSlider object(sec, mph, cm, inches, etc.)
	 *
	 * @param Unit_Arg The new time value
	 */
	public void setUnit(

			final String Unit_Arg) {
		Abstr.Unit = Unit_Arg;
	} // setUnit()

	/**
	 * Sets the 2 Value attribute of the BiSlider object. Because sometimes using
	 * setMaximum before setMinimum mess everything
	 *
	 * @param MaxValue_Arg The new maximumValue value
	 */
	public void setValues(final double MinValue_Arg, final double MaxValue_Arg) {

		if (MinValue_Arg <= MaxValue_Arg)
			Abstr.MinimumValue = MaxValue_Arg - 1;

		Abstr.MinimumValue = MinValue_Arg;
		Abstr.MaximumValue = MaxValue_Arg;

		if (MaxValue_Arg < Abstr.MinimumColoredValue)
			Abstr.MinimumColoredValue = MaxValue_Arg;

		if (MaxValue_Arg < Abstr.MaximumColoredValue)
			Abstr.MaximumColoredValue = MaxValue_Arg;

		if (MinValue_Arg > Abstr.MinimumColoredValue)
			Abstr.MinimumColoredValue = MinValue_Arg;

		if (MinValue_Arg > Abstr.MaximumColoredValue)
			Abstr.MaximumColoredValue = MinValue_Arg;

		if (isUniformSegment() && ((Abstr.MaximumValue - Abstr.MinimumValue) % Abstr.SegmentCount) != 0)
			Abstr.SegmentCount = Abstr.searchSegmentCount(Abstr.SegmentCount);

		Pres.setRulerValues(Abstr.MinimumColoredValue, Abstr.MaximumColoredValue);
		setSegmentSize(Abstr.SegmentSize);
		repaint();
		ColorisationSupport1.fireNewColors(this, Abstr.getColorTable());
	}// setValues()
} // BiSlider

package com.visutools.nav.bislider;

import java.awt.*;
import java.awt.event.*;
import java.io.Serializable;
import java.text.DecimalFormat;

import javax.swing.*;

import com.visutools.nav.bislider.BiSliderPresentation.FormatLong;

/**
 * The interface of the bean with the outside world aka your "application". You should not access the presentation or abstraction,
 * just this object we call the control facet.
 * <br>
 * <br>
 * <table border=1 width = "90%">
 *   <tr>
 *     <td>
 *       Copyright 1997-2005 Frederic Vernier. All Rights Reserved.<br>
 *       <br>
 *       Permission to use, copy, modify and distribute this software and its documentation for educational, research and
 *       non-profit purposes, without fee, and without a written agreement is hereby granted, provided that the above copyright
 *       notice and the following three paragraphs appear in all copies.<br>
 *       <br>
 *       To request Permission to incorporate this software into commercial products contact Frederic Vernier, 19 butte aux
 *       cailles street, Paris, 75013, France. Tel: (+33) 871 747 387. eMail: Frederic.Vernier@laposte.net / Web site: http://vernier.frederic.free.fr
 *       <br>
 *       IN NO EVENT SHALL FREDERIC VERNIER BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL
 *       DAMAGES, INCLUDING LOST PROFITS, ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF FREDERIC
 *       VERNIER HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.<br>
 *       <br>
 *       FREDERIC VERNIER SPECIFICALLY DISCLAIMS ANY WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 *       MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE PROVIDED HERE UNDER IS ON AN "AS IS" BASIS, AND
 *       FREDERIC VERNIER HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 *       <br>
 *     </td>
 *   </tr>
 * </table>
 * <br>
 * <b>Project related :</b> FiCell, FieldExplorer<br>
 * <br>
 * <b>Dates:</b>
 * <li> Format : 15/02/2004
 * <li> Last Modif : 15/02/2004
 * <br>
 * <b>Bugs:</b>
 * <br>
 * <li> ??? <br>
 * <br>
 * <b>To Do:</b>
 * <li> multiple segments ???
 * <li> X any height for horizontal bislider
 * <li> -> maybe histogram instead of simple colors !
 * <li> no text out of component (right and left for horizontal bislider)
 * <li> X better shadow in triangle for vertical bislider
 * <li> X initial size of horizontal bislider to fix
 * <li> X stroke color for white on black bislider to be rethought
 * <li> X Load sounds from jar not from folder !
 * <li> magnetisation of ticks
 * <li> alt or shift pressed -> repaint
 * <li> center axis displayed when alt pressed + value
 * <br>
 * <b>New in 1.3.3:</b>
 * <li> new totally clean version for JD Fekete toolkit.
 * <li> one more bug fixed just after 1.3.0
 * <li> no label when too many segments. first and last label in bold
 * <b>New in 1.3.4 and 1.3.5 :</b>
 * <li> bug fix release. Sounds loaded from jar
 * <br>
 * <li> new paint mechanism (not triggered by the awt thread which may be slowed down by the main visu)
 * <br>
 *
 * @author    Frederic Vernier, Frederic.Vernier@laposte.net
 * @created   16 février 2004
 * @version   1.4.1
 */

public class SwingBiSlider extends JComponent implements Serializable
{
	//---------- MODIFIERS|-------------------- Type|----------------------------------------------- Name = Init value
	protected final static javax.swing.text.html.parser.ParserDelegator MAXIMUM_VARIABLE_SIZE_FOR_NAME = null;

	static final long serialVersionUID = 8045586992642853136L;

	// The two others facets of the bean
	protected BiSliderAbstraction Abstr = null;

	protected BiSliderPresentation Pres = null;

	protected ColorisationSupport ColorisationSupport1 = new ColorisationSupport();

	protected ContentPainterSupport ContentPainterSupport1 = new ContentPainterSupport();

	// The 3 kinds of BiSlider.
	/**
	 * Color shading where purple is between red and blue (for instance)
	 */
	public final static int RGB = 100;

	/**
	 * Color shading where all the rainbow colors are between blue and red (for instance)
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

	/**
	 * Constructor with HSB interpolation by default.
	 */
	public SwingBiSlider(FormatLong formatter)
	{
		this(HSB, formatter);
	} // Constructor

	/**
	 * Constructor of an horizontal BiSlider.
	 *
	 * @param InterpolationMode_Arg  how the bean is supposed interpolate colors between the 2 edges.
	 */
public SwingBiSlider(int InterpolationMode_Arg, FormatLong formatter) {
    super();
    setDoubleBuffered(false);

    //setMinimumSize(new Dimension(50, 50));
    //setPreferredSize(new Dimension(50, 50));
    Abstr = new BiSliderAbstraction();
    setBackground(Abstr.DefaultColor);
    Pres = new BiSliderPresentation(this, ContentPainterSupport1, formatter); 
    Abstr.InterpolationMode = InterpolationMode_Arg;

  } // Constructor

	/**
	 * @return the DecimalFormat used to display nimbers on the BiSlider
	 **/
	public DecimalFormat getDecimalFormater()
	{
		return Abstr.DecimalFormater;
	}// getDecimalFormater()

	/**
	 * @param DecimalFormat_Arg the DecimalFormat used to display nimbers on the BiSlider
	 **/
	public void setDecimalFormater(DecimalFormat DecimalFormat_Arg)
	{
		Abstr.DecimalFormater = DecimalFormat_Arg;
	}// setDecimalFormater()

	/**
	 * Sets the minimumColoredValue attribute of the BiSlider object
	 *
	 * @param NewValue_Arg  the minimum colored value. Under this value everything will have the default color.
	 */
	public void setMinimumColoredValue(double NewValue_Arg)
	{

		if (Abstr != null && Pres != null && NewValue_Arg >= Abstr.MinimumValue
				&& NewValue_Arg <= Abstr.MaximumValue)
		{
			Abstr.MinimumColoredValue = NewValue_Arg;
			Pres.setRulerValues(Abstr.MinimumColoredValue, Abstr.MaximumColoredValue);
			repaint();
			ColorisationSupport1.fireNewColors(this, Abstr.getColorTable());
		}
	} // setMinimumColoredValue()

	/**
	 * Gets the minimumColoredValue attribute of the BiSlider object
	 *
	 * @return   the minimum colored value. Under this value everything will have the default color.
	 */
	public double getMinimumColoredValue()
	{
		return Abstr.MinimumColoredValue;
	} // getMinimumColoredValue()

	/**
	 * Sets the maximumColoredValue attribute of the BiSlider object. Above this value everything will have the default color
	 *
	 * @param MaximumColoredValue_Arg  The new maximumColoredValue value
	 */
	public void setMaximumColoredValue(double MaximumColoredValue_Arg)
	{
		if (Abstr != null && Pres != null && MaximumColoredValue_Arg >= Abstr.MinimumValue
				&& MaximumColoredValue_Arg <= Abstr.MaximumValue)
		{
			Abstr.MaximumColoredValue = MaximumColoredValue_Arg;
			Pres.setRulerValues(Abstr.MinimumColoredValue, Abstr.MaximumColoredValue);
			repaint();
			ColorisationSupport1.fireNewColors(this, Abstr.getColorTable());
		}
	} // setMaximumColoredValue()

	/**
	 * Gets the maximumColoredValue attribute of the BiSlider object
	 *
	 * @return   The maximumColoredValue value
	 */
	public double getMaximumColoredValue()
	{
		return Abstr.MaximumColoredValue;
	} // getMaximumColoredValue()

	/**
	 * Gets the unit attribute of the BiSlider object (sec, mph, cm, inches, etc.)
	 *
	 * @return   The unit value
	 */
	public String getUnit()
	{
		return Abstr.Unit;
	} // getUnit()

	/**
	 * Sets the unit attribute of the BiSlider object(sec, mph, cm, inches, etc.)
	 *
	 * @param Unit_Arg  The new time value
	 */
	public void setUnit(

	String Unit_Arg)
	{
		Abstr.Unit = Unit_Arg;
	} // setUnit()

	/**
	 * Sets the coloredValues attribute of the BiSlider object. Under the minimum value and above the maximum value everything
	 * will have the default color
	 *
	 * @param MinValue_Arg  The new coloredValues minimum value
	 * @param MaxValue_Arg  The new coloredValues maximum value
	 */
	public void setColoredValues(double MinValue_Arg, double MaxValue_Arg)
	{

		if (Abstr != null && Pres != null && MinValue_Arg <= MaxValue_Arg
				&& MinValue_Arg <= Abstr.MaximumValue && MaxValue_Arg >= Abstr.MinimumValue)
		{

			Abstr.MinimumColoredValue = MinValue_Arg;
			Abstr.MaximumColoredValue = MaxValue_Arg;

			Pres.setRulerValues(Abstr.MinimumColoredValue, Abstr.MaximumColoredValue);
			repaint();
			ColorisationSupport1.fireNewColors(this, Abstr.getColorTable());
		} /*else {
		 Debug.debug(0, "problem with setColoredValues");
		 Debug.debug(0, "  MinValue_Arg = "+MinValue_Arg);
		 Debug.debug(0, "  MaxValue_Arg = "+MaxValue_Arg);
		 Debug.debug(0, "  Abstr.MinimumValue = "+Abstr.MinimumValue);
		 Debug.debug(0, "  Abstr.MaximumValue = "+Abstr.MaximumValue);
		 Debug.debug(0, "");
		 } */
	} // setColoredValues()

	/**
	 * Sets the minimumValue attribute of the BiSlider object. Under this value everything will have the default color
	 *
	 * @param MinValue_Arg  The new minimumValue value
	 */
	public void setMinimumValue(double MinValue_Arg)
	{

		if (MinValue_Arg >= Abstr.MaximumValue)
			Abstr.MaximumValue = MinValue_Arg + 1;

		Abstr.MinimumValue = MinValue_Arg;
		if (MinValue_Arg > Abstr.MinimumColoredValue)
			Abstr.MinimumColoredValue = MinValue_Arg;
		if (MinValue_Arg > Abstr.MaximumColoredValue)
			Abstr.MaximumColoredValue = MinValue_Arg;

		if (isUniformSegment()
				&& ((Abstr.MaximumValue - Abstr.MinimumValue) % Abstr.SegmentCount) != 0)
			Abstr.SegmentCount = Abstr.searchSegmentCount(Abstr.SegmentCount);

		Pres.setRulerValues(Abstr.MinimumColoredValue, Abstr.MaximumColoredValue);
		setSegmentSize(Abstr.SegmentSize);
		repaint();
		ColorisationSupport1.fireNewColors(this, Abstr.getColorTable());
	} // setMinimumValue()

	/**
	 * Gets the minimumValue attribute of the BiSlider object
	 *
	 * @return   The minimumValue value
	 */
	public double getMinimumValue()
	{
		return Abstr.MinimumValue;
	} // getMinimumValue()

	/**
	 * Sets the maximumValue attribute of the BiSlider object. Above this value everything will have the default color
	 *
	 * @param MaxValue_Arg  The new maximumValue value
	 */
	public void setMaximumValue(double MaxValue_Arg)
	{

		if (MaxValue_Arg <= Abstr.MinimumValue)
			Abstr.MinimumValue = MaxValue_Arg - 1;

		Abstr.MaximumValue = MaxValue_Arg;

		if (MaxValue_Arg < Abstr.MinimumColoredValue)
			Abstr.MinimumColoredValue = MaxValue_Arg;

		if (MaxValue_Arg < Abstr.MaximumColoredValue)
			Abstr.MaximumColoredValue = MaxValue_Arg;

		if (isUniformSegment()
				&& ((Abstr.MaximumValue - Abstr.MinimumValue) % Abstr.SegmentCount) != 0)
			Abstr.SegmentCount = Abstr.searchSegmentCount(Abstr.SegmentCount);

		Pres.setRulerValues(Abstr.MinimumColoredValue, Abstr.MaximumColoredValue);
		setSegmentSize(Abstr.SegmentSize);
		repaint();
		ColorisationSupport1.fireNewColors(this, Abstr.getColorTable());
	}// setMaximumValue()

	/**
	 * Gets the maximumValue attribute of the BiSlider object. Above this value everything will have the default color
	 *
	 * @return   The maximumValue value
	 */
	public double getMaximumValue()
	{
		return Abstr.MaximumValue;
	}// getMaximumValue()

	/**
	 * Sets the 2 Value attribute of the BiSlider object. Because sometimes using setMaximum before setMinimum mess
	 * everything
	 *
	 * @param MaxValue_Arg  The new maximumValue value
	 */
	public void setValues(double MinValue_Arg, double MaxValue_Arg)
	{

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

		if (isUniformSegment()
				&& ((Abstr.MaximumValue - Abstr.MinimumValue) % Abstr.SegmentCount) != 0)
			Abstr.SegmentCount = Abstr.searchSegmentCount(Abstr.SegmentCount);

		Pres.setRulerValues(Abstr.MinimumColoredValue, Abstr.MaximumColoredValue);
		setSegmentSize(Abstr.SegmentSize);
		repaint();
		ColorisationSupport1.fireNewColors(this, Abstr.getColorTable());
	}// setValues()

	/**
	 * Sets the SegmentSize attribute of the BiSlider object. The SegmentSize means the size of the subdivisions.
	 * in UniformSegment Mode, SegmentSize must be a perfect division of the global gap, otherwise the system will
	 * change the desired size to something else.
	 *
	 * @param SegmentSize_Arg  The desired Segment size
	 */
	public void setSegmentSize(double SegmentSize_Arg)
	{
		if (SegmentSize_Arg == 0d)
			return;

		if (Abstr.UniformSegment && ((long) SegmentSize_Arg) == SegmentSize_Arg)
		{
			int NewSegmentCount = Abstr
					.searchSegmentCount((int) ((Abstr.MaximumValue - Abstr.MinimumValue) / SegmentSize_Arg));
			Abstr.SegmentCount = NewSegmentCount;
			Abstr.SegmentSize = (Abstr.MaximumValue - Abstr.MinimumValue) / Abstr.SegmentCount;
		}
		else if (Abstr.UniformSegment)
		{
			int NewSegmentCount = (int) Math.round((Abstr.MaximumValue - Abstr.MinimumValue)
					/ SegmentSize_Arg);
			Abstr.SegmentCount = NewSegmentCount;
			Abstr.SegmentSize = (Abstr.MaximumValue - Abstr.MinimumValue) / Abstr.SegmentCount;
		}
		else
		{
			Abstr.SegmentSize = SegmentSize_Arg;
			Abstr.SegmentCount = (int) Math.ceil((Abstr.MaximumValue - Abstr.MinimumValue)
					/ SegmentSize_Arg);
		}

		if (Abstr.SegmentCount > MAX_SEGMENT_COUNT)
		{
			Abstr.SegmentCount = MAX_SEGMENT_COUNT;
			Abstr.SegmentSize = (Abstr.MaximumValue - Abstr.MinimumValue) / MAX_SEGMENT_COUNT;
			//Abstr.UniformSegment = false;
		}
		if (Abstr.SegmentCount <= 0)
		{
			Abstr.SegmentCount = 1;
			Abstr.SegmentSize = Abstr.MaximumValue - Abstr.MinimumValue;
		}

		repaint();
		ColorisationSupport1.fireNewColors(this, Abstr.getColorTable());
	}// setSegmentSize()

	/**
	 * Gets the SegmentSize attribute of the BiSlider object. The SegmentSize means the size of the subdivisions.
	 * in UniformSegment Mode, SegmentSize must be a perfect division of the global gap, otherwise the system will
	 * change the desired size to something else.
	 *
	 * @return   The SegmentCount value
	 */
	public double getSegmentSize()
	{
		return Abstr.SegmentSize;
	}// getSegmentSize()

	/**
	 * Gets the SegmentCount attribute of the BiSlider object. The SegmentCount mean how many subdivision there are in the coloration.
	 * the gap must be able to be divided by the SegmentCount. Otherwise the system will find a correct one for you.
	 *
	 * @return   The SegmentCount value
	 */
	public int getSegmentCount()
	{
		return Abstr.SegmentCount;
	} // getSegmentCount()

	/**
	 * Sets the defaultColor attribute of the BiSlider object. Default backgound color is white. It is the color of the value
	 * outside the gap.
	 *
	 * @param Color_Arg  The new defaultColor value
	 */
	public void setDefaultColor(Color Color_Arg)
	{
		Abstr.DefaultColor = Color_Arg;
	}// setDefaultColor()

	/**
	 * Gets the defaultColor attribute of the BiSlider object. Default backgound color is white. It is the color of the value
	 * outside the gap.
	 *
	 * @return   The defaultColor value
	 */
	public Color getDefaultColor()
	{
		return Abstr.DefaultColor;
	}// getDefaultColor()

	/**
	 * Sets the SliderBackground attribute of the BiSlider object. SliderBackground color is gray by default
	 *
	 * @param Color_Arg  The new defaultColor value
	 */
	public void setSliderBackground(Color Color_Arg)
	{
		Abstr.SliderBackground = Color_Arg;
	}// setSliderBackground()

	/**
	 * Gets the SliderBackground attribute of the BiSlider object. SliderBackground color is grayby default
	 *
	 * @return   The SliderBackground value
	 */
	public Color getSliderBackground()
	{
		return Abstr.SliderBackground;
	}// getSliderBackground()

	/**
	 * Sets the ArcSize attribute of the BiSlider object. ArcSize is for rounded presentation
	 *
	 * @param ArcSize_Arg  The new ArcSize value
	 */
	public void setArcSize(int ArcSize_Arg)
	{
		Abstr.ArcSize = ArcSize_Arg;
	}// setArcSize()

	/**
	 * Gets the ArcSize attribute of the BiSlider object. ArcSize is for rounded presentation
	 *
	 * @return   The ArcSize value
	 */
	public int getArcSize()
	{
		return Abstr.ArcSize;
	}// getArcSize()

	/**
	 * Sets the minimumColor attribute of the BiSlider object. Default color for minimum value is red.
	 *
	 * @param MinimumColor_Arg  The new minimumColor value
	 */
	public void setMinimumColor(Color MinimumColor_Arg)
	{
		Abstr.MinimumColor = MinimumColor_Arg;
		ColorisationSupport1.fireNewColors(this, Abstr.getColorTable());
	}// setMinimumColor()

	/**
	 * Gets the minimumColor attribute of the BiSlider object. Default color for minimum value is red.
	 *
	 * @return   The minimumColor value
	 */
	public Color getMinimumColor()
	{
		return Abstr.MinimumColor;
	}// getMinimumColor

	/**
	 * Sets the maximumColor attribute of the BiSlider object. Default color for the maximum value is blue
	 *
	 * @param MaximumColor_Arg  The new maximumColor value
	 */
	public void setMaximumColor(Color MaximumColor_Arg)
	{
		Abstr.MaximumColor = MaximumColor_Arg;
		ColorisationSupport1.fireNewColors(this, Abstr.getColorTable());
	}// setMaximumColor()

	/**
	 * Gets the maximumColor attribute of the BiSlider object. Default color for the maximum value is blue
	 *
	 * @return   The maximumColor value
	 */
	public Color getMaximumColor()
	{
		return Abstr.MaximumColor;
	}// getMaximumColor()

	/**
	 * Sets the UniformSegment attribute of the BiSlider object. Default UniformSegment is true.
	 * Tells if all the segments must have the sme size
	 *
	 * @param UniformSegment_Arg  The new UniformSegment value
	 */
	public void setUniformSegment(boolean UniformSegment_Arg)
	{
		Abstr.UniformSegment = UniformSegment_Arg;

		if (UniformSegment_Arg)
		{
			setSegmentSize(getSegmentSize());
		}
	}// setUniformSegment()

	/**
	 * Gets the UniformSegment attribute of the BiSlider object. Default UniformSegment is true.
	 * Tells if all the segments must have the sme size
	 *
	 * @return   The UniformSegment value
	 */
	public boolean isUniformSegment()
	{
		return Abstr.UniformSegment;
	}// isUniformSegment()

	/**
	 * Sets the Sound attribute of the BiSlider object. Default Sound is false.
	 *
	 * @param Sound_Arg  The new Sound value
	 */
	public void setSound(boolean Sound_Arg)
	{
		Abstr.Sound = Sound_Arg;
	}// setSound()

	/**
	 * Gets the Sound attribute of the BiSlider object. Default Sound is true.
	 *
	 * @return   The Sound value
	 */
	public boolean isSound()
	{
		return false;
	}// isSound()

	/**
	 * Sets the Precise attribute of the BiSlider object. Default Precise is false.
	 *
	 * @param Precise_Arg  The new Precise value
	 */
	public void setPrecise(boolean Precise_Arg)
	{
		Abstr.Precise = Precise_Arg;
	}// setPrecise()

	/**
	 * Gets the Precise attribute of the BiSlider object. Default Precise is true.
	 *
	 * @return   The Precise value
	 */
	public boolean isPrecise()
	{
		return Abstr.Precise;
	}// isPrecise()

	/**
	 * Sets the MinOnTop attribute of the BiSlider object. Default MinOnTop is false.
	 *
	 * @param MinOnTop_Arg  The new MinOnTop value
	 */
	public void setMinOnTop(boolean MinOnTop_Arg) throws Exception
	{
			throw (new Exception(
					"can't set the minimun vertical position for horizontal BiSliders"));
	}// setMinOnTop()

	/**
	 * Gets the MinOnTop attribute of the BiSlider object. Default MinOnTop is true.
	 *
	 * @return   The MinOnTop value
	 */
	public boolean isMinOnTop() throws Exception
	{
			throw (new Exception(
					"can't tell if minimum value is on top for horizontal BiSliders"));
	}// isMinOnTop()

	/**
	 * Gets the Horizontal attribute of the BiSlider object. Default Horizontal depends on constructor used !
	 *
	 * @return   The Horizontal value
	 */
	public boolean isHorizontal()
	{
		return true;
	}// isHorizontal()

	/**
	 * Sets the Horizontal attribute of the BiSlider object. Default Horizontal depends on constructor used.
	 *
	 * @param Horizontal_Arg  The new Horizontal value
	 */
	public void setHorizontal(boolean Horizontal_Arg) /*throws Exception*/
	{

			removeMouseListener(Pres);
			removeMouseMotionListener(Pres);
			removeComponentListener(Pres);
			Pres = null;
			Pres = new BiSliderPresentation(this, ContentPainterSupport1, null);

		// ((BiSlider_V_Presentation)Pres).Horizontal = Horizontal_Arg;
		//else throw(new Exception("can't tell if min on top for horizzontal BiSliders"));
	}// setHorizontal()

	/**
	 * Method called by java when the component needs to be refreshed
	 *
	 * @param Graphics_Arg  Description of the Parameter
	 */
	public void paint(Graphics Graphics_Arg)
	{

		Pres.setRulerValues(Abstr.MinimumColoredValue, Abstr.MaximumColoredValue);

		Pres.paint(Graphics_Arg);
		//super.paintComponent(Graphics_Arg);
	}// paint();

	/**
	 * Gets the colorTable attribute of the BiSlider object. The table describe the segments and the colors like this one :
	 * <br>
	 *
	 * <li> ColorTable[0][0] = 0 ColorTable[0][1] = 5 ColorTable[0][2] = Color.RED.getRGB();
	 * <li> ColorTable[1][0] = 5 ColorTable[1][1] = 10 ColorTable[1][2] = somewhere Purple;
	 * <li> ColorTable[2][0] = 10 ColorTable[2][1] = 15 ColorTable[2][2] = Color.BLUE.getRGB();
	 *
	 * @return   The colorTable value
	 */
	public double[][] getColorTable()
	{
		return Abstr.getColorTable();
	}// getColorTable()

	/**
	 * to produce re-usable colorization we must provide a Colorizer
	 *
	 * @return   The colorizer value
	 */
	public Colorizer getColorizer()
	{
		return ColorisationSupport1.createColorisationEvent(this, Abstr.getColorTable());
	}// getColorizer()

	/**
	 * Registers ColorisationListener to receive events.
	 *
	 * @param Listener_Arg  The listener to register.
	 */
	public synchronized void addColorisationListener(ColorisationListener Listener_Arg)
	{
		ColorisationSupport1.addColorisationListener(Listener_Arg);
	}// addColorisationListener()

	/**
	 * Removes ColorisationListener from the list of listeners.
	 *
	 * @param Listener_Arg  The listener to remove.
	 */
	public synchronized void removeColorisationListener(ColorisationListener Listener_Arg)
	{
		ColorisationSupport1.removeColorisationListener(Listener_Arg);
	}// removeColorisationListener()

	/**
	 * Registers ContentPainterListener to receive events.
	 *
	 * @param Listener_Arg  The listener to register.
	 */
	public synchronized void addContentPainterListener(ContentPainterListener Listener_Arg)
	{
		ContentPainterSupport1.addContentPainterListener(Listener_Arg);
	}// addContentPainterListener()

	/**
	 * Removes ContentPainterListener from the list of listeners.
	 *
	 * @param Listener_Arg  The listener to remove.
	 */
	public synchronized void removeContentPainterListener(
			ContentPainterListener Listener_Arg)
	{
		ContentPainterSupport1.removeContentPainterListener(Listener_Arg);
	}// removeContentPainterListener()

	/**
	 * Gets the InterpolationMode attribute of the BiSlider object. How it interpolates colors between maxi and mini color.
	 *
	 * @return   The InterpolationMode value
	 */
	public int getInterpolationMode()
	{
		return Abstr.InterpolationMode;
	}// getInterpolationMode

	/**
	 * Sets the InterpolationMode attribute of the BiSlider object. How it interpolates colors between maxi and mini color.
	 *
	 * @param InterpolationMode_Arg  The new maximumColor value
	 */
	public void setInterpolationMode(int InterpolationMode_Arg)
	{
		Abstr.InterpolationMode = InterpolationMode_Arg;
		ColorisationSupport1.fireNewColors(this, Abstr.getColorTable());
	}// setInterpolationMode()

	/**
	 * Sets the all the parameters of the BiSlider together.
	 *
	 * @param InterpolationMode_Arg  The new maximumColor value
	 */
	public void setParameters(int InterpolationMode_Arg, boolean UniformSegment_Arg,
			double SegmentSize_Arg, Color MinimumColor_Arg, Color MaximumColor_Arg,
			double MinValue_Arg, double MaxValue_Arg, double MinimumColoredValue_Arg,
			double MaximumColoredValue_Arg)
	{

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
	 * Creates a default popup-menu to use with a bislider
	 * @return the created popup menu ready to be displayed
	 **/
	public JPopupMenu createPopupMenu()
	{
		final SwingBiSlider Myself = this;
		ButtonGroup ButtonGroup1 = new ButtonGroup();

		JPopupMenu JPopupMenu1 = new JPopupMenu("BiSlider");
		JMenuItem JMenuItem1 = new JRadioButtonMenuItem("RGB",
				getInterpolationMode() == SwingBiSlider.RGB);
		JMenuItem1.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ActionEvent_Arg)
			{
				setInterpolationMode(SwingBiSlider.RGB);
				repaint();
			}
		});
		ButtonGroup1.add(JMenuItem1);
		JPopupMenu1.add(JMenuItem1);

		JMenuItem1 = new JRadioButtonMenuItem("HSB", getInterpolationMode() == SwingBiSlider.HSB);
		JMenuItem1.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ActionEvent_Arg)
			{
				setInterpolationMode(SwingBiSlider.HSB);
				repaint();
			}
		});
		ButtonGroup1.add(JMenuItem1);
		JPopupMenu1.add(JMenuItem1);

		JMenuItem1 = new JRadioButtonMenuItem("CENTRAL",
				getInterpolationMode() == SwingBiSlider.CENTRAL_BLACK);
		JMenuItem1.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ActionEvent_Arg)
			{
				setInterpolationMode(SwingBiSlider.CENTRAL_BLACK);
				repaint();
			}
		});
		ButtonGroup1.add(JMenuItem1);
		JPopupMenu1.add(JMenuItem1);

		JPopupMenu1.add(new JSeparator());

		JMenuItem1 = new JMenuItem("Shrink");
		JMenuItem1.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ActionEvent_Arg)
			{
				setMinimumValue(getMinimumColoredValue());
				setMaximumValue(getMaximumColoredValue());
				repaint();
			}
		});
		JPopupMenu1.add(JMenuItem1);

		JMenuItem1 = new JCheckBoxMenuItem("Uniform", isUniformSegment());
		JMenuItem1.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ActionEvent_Arg)
			{
				JCheckBoxMenuItem JCheckBoxMenuItem1 = (JCheckBoxMenuItem) ActionEvent_Arg
						.getSource();
				setUniformSegment(JCheckBoxMenuItem1.isSelected());
				repaint();
			}
		});
		JPopupMenu1.add(JMenuItem1);
		JMenuItem1 = new JCheckBoxMenuItem("Sound", isSound());
		JMenuItem1.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ActionEvent_Arg)
			{
				JCheckBoxMenuItem JCheckBoxMenuItem1 = (JCheckBoxMenuItem) ActionEvent_Arg
						.getSource();
				setSound(JCheckBoxMenuItem1.isSelected());
				repaint();
			}
		});
		JPopupMenu1.add(JMenuItem1);
		JMenuItem1 = new JCheckBoxMenuItem("Precise", isPrecise());
		JMenuItem1.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ActionEvent_Arg)
			{
				JCheckBoxMenuItem JCheckBoxMenuItem1 = (JCheckBoxMenuItem) ActionEvent_Arg
						.getSource();
				setPrecise(JCheckBoxMenuItem1.isSelected());
				repaint();
			}
		});
		JPopupMenu1.add(JMenuItem1);
		/*JMenuItem1 = new JCheckBoxMenuItem("Horizontal", isHorizontal());
		 JMenuItem1.addActionListener(new ActionListener(){
		 public void actionPerformed(ActionEvent ActionEvent_Arg) {
		 JCheckBoxMenuItem JCheckBoxMenuItem1 = (JCheckBoxMenuItem)ActionEvent_Arg.getSource();
		 //setHorizontal(JCheckBoxMenuItem1.isSelected());
		 repaint();
		 }
		 });
		 JPopupMenu1.add(JMenuItem1);
		 */
		if (!isHorizontal())
		{
			boolean test = false;
			try
			{
				test = isMinOnTop();
			}
			catch (Exception Exception_Arg)
			{
				Exception_Arg.printStackTrace();
			}
			JMenuItem1 = new JCheckBoxMenuItem("MinOnTop", test);
			JMenuItem1.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent ActionEvent_Arg)
				{
					JCheckBoxMenuItem JCheckBoxMenuItem1 = (JCheckBoxMenuItem) ActionEvent_Arg
							.getSource();
					try
					{
						setMinOnTop(JCheckBoxMenuItem1.isSelected());
					}
					catch (Exception Exception_Arg)
					{
						Exception_Arg.printStackTrace();
					}
					repaint();
				}
			});
			JPopupMenu1.add(JMenuItem1);
		}

		JPopupMenu1.add(new JSeparator());

		JMenuItem1 = new JMenuItem("= Min", new Icon()
		{
			public int getIconHeight()
			{
				return 14;
			}

			public int getIconWidth()
			{
				return 14;
			}

			public void paintIcon(Component Component_Arg, Graphics Graphics_Arg, int X_Arg,
					int Y_Arg)
			{
				Color Color1 = Graphics_Arg.getColor();
				Graphics_Arg.setColor(getMinimumColor());
				Graphics_Arg.fillRect(1, 1, Component_Arg.getHeight() - 2, Component_Arg
						.getHeight() - 2);
				Graphics_Arg.setColor(Color1);
			}
		});
		JMenuItem1.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ActionEvent_Arg)
			{
				Color Color1 = JColorChooser.showDialog(Myself,
						"Choose a color for minimum values", getMinimumColor());
				if (Color1 != null)
					setMinimumColor(Color1);
				repaint();
			}
		});
		JPopupMenu1.add(JMenuItem1);

		JMenuItem1 = new JMenuItem("= Max", new Icon()
		{
			public int getIconHeight()
			{
				return 14;
			}

			public int getIconWidth()
			{
				return 14;
			}

			public void paintIcon(Component Component_Arg, Graphics Graphics_Arg, int X_Arg,
					int Y_Arg)
			{
				Color Color1 = Graphics_Arg.getColor();
				Graphics_Arg.setColor(getMaximumColor());
				Graphics_Arg.fillRect(1, 1, Component_Arg.getHeight() - 2, Component_Arg
						.getHeight() - 2);
				Graphics_Arg.setColor(Color1);
			}
		});
		JMenuItem1.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ActionEvent_Arg)
			{
				Color Color1 = JColorChooser.showDialog(Myself,
						"Choose a color for maximum values", getMaximumColor());
				if (Color1 != null)
					setMaximumColor(Color1);
				repaint();
			}
		});
		JPopupMenu1.add(JMenuItem1);

		return JPopupMenu1;
	}// createPopupMenu()  
} // BiSlider


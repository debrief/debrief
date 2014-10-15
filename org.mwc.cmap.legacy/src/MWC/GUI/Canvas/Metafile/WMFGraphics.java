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
package MWC.GUI.Canvas.Metafile;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: WMFGraphics.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.3 $
// $Log: WMFGraphics.java,v $
// Revision 1.3  2004/12/06 09:10:05  Ian.Mayo
// Optimise to reduce object creation (fonts)
//
// Revision 1.2  2004/05/25 14:44:07  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:15  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:10  Ian.Mayo
// Initial import
//
// Revision 1.8  2003-01-28 09:44:07+00  ian_mayo
// Comment out d-lines
//
// Revision 1.7  2003-01-14 14:17:18+00  ian_mayo
// Improve d-lines
//
// Revision 1.6  2002-11-28 09:57:07+00  ian_mayo
// Add ability to set direction for fonts
//
// Revision 1.5  2002-11-25 14:43:37+00  ian_mayo
// Improve property naming
//
// Revision 1.4  2002-10-28 09:23:32+00  ian_mayo
// support line widths
//
// Revision 1.3  2002-07-23 08:52:48+01  ian_mayo
// Implement Line width support
//
// Revision 1.2  2002-05-28 09:25:39+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:15:08+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:02:18+01  ian_mayo
// Initial revision
//
// Revision 1.1  2001-08-10 10:12:35+01  administrator
// added new setLineStyle method to allow dotted lines
//
// Revision 1.0  2001-07-17 08:46:31+01  administrator
// Initial revision
//
// Revision 1.2  2001-06-04 09:38:29+01  novatech
// minor typo changes
//
// Revision 1.9  2000-10-31 15:42:30+00  ian_mayo
// perform tidying up to keep JBuilder happy
//
// Revision 1.8  2000-10-16 15:17:13+01  ian_mayo
// correct arc-plotting code
//
// Revision 1.7  2000-10-03 14:13:30+01  ian_mayo
// add correct implementation of drawArc method
//
// Revision 1.6  2000-08-30 14:47:19+01  ian_mayo
// added lots of methods, to make it into a CanvasType
//
// Revision 1.5  2000-08-09 16:02:56+01  ian_mayo
// add missing message signature
//
// Revision 1.4  2000-03-08 16:26:27+00  ian_mayo
// formatting tidied up
//
// Revision 1.3  2000-02-16 16:28:01+00  ian_mayo
// more corrections for formatting, since metafiles not currently working
//
// Revision 1.2  2000-02-03 15:07:54+00  ian_mayo
// First issue to Devron (modified files are mostly related to WMF)
//
//

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.image.ImageObserver;
import java.awt.image.PixelGrabber;

import MWC.GUI.ToolParent;
import MWC.GUI.Canvas.CanvasAdaptor;
import MWC.Utilities.Errors.Trace;

public class WMFGraphics extends Graphics implements MWC.GUI.CanvasType
{

	// /////////////////////////////////
	// member variables
	// ////////////////////////////////

	MWC.Algorithms.PlainProjection _projection;

	WMF wmf;
	Color foreground;
	Color background;
	Font font;
	int penstyle;
	int penwidth;
	int brushfillstyle;
	int brushhatch;
	int fontescapement;
	Image brushpattern;
	int penhandle;
	int brushhandle;
	int fonthandle;

	/**
	 * have our own nice, safe font to save recreating it each time
	 * 
	 */
	private static Font _cachedFont = new Font("Helvetica", 0, 12);

	public WMFGraphics(final WMF wmf, final int width, final int height)
	{
		this(wmf, width, height, Color.black, Color.white);
	}

	public WMFGraphics(final WMF wmf, final int width, final int height, final Color color, final Color color_2_)
	{
		// font = new Font("Helvetica", 0, 12);
		font = _cachedFont;
		penstyle = 0;
		penwidth = 0;
		brushfillstyle = 0;
		brushhatch = 0;
		fontescapement = 0;
		brushpattern = null;
		foreground = color;
		background = color_2_;
		setWMF(wmf, width, height);
		reset();
	}

	public void clearRect(final int i, final int i_3_, final int i_4_, final int i_5_)
	{
		final Color color = foreground;
		setColor(background);
		fillRect(i, i_3_, i_4_, i_5_);
		setColor(color);
	}

	public void clipRect(final int i, final int i_6_, final int i_7_, final int i_8_)
	{
		// System.err.println("clipRect not supported");
	}

	public void copyArea(final int i, final int i_9_, final int i_10_, final int i_11_, final int i_12_,
			final int i_13_)
	{
		// System.err.println("copyArea not supported");
	}

	public Graphics create()
	{
		// System.err.println("create not supported");
		return null;
	}

	public Graphics create(final int i, final int i_14_, final int i_15_, final int i_16_)
	{
		// System.err.println("create not supported");
		return null;
	}

	public void deleteGDIObjects()
	{
		wmf.deleteObject(penhandle);
		wmf.deleteObject(brushhandle);
		wmf.deleteObject(fonthandle);
	}

	public void dispose()
	{
		/* empty */
	}

	/*
	 * @@@ NOTE, we had to switch around the start & end angle parameters (7 & 8)
	 * in the call wmf.arc, since they are in a different order to Java
	 */
	public void drawArc(final int x, final int y, final int width, final int height, final int startAngle,
			final int arcAngle)
	{
		final int i_22_ = x + width / 2;
		final int i_23_ = y + height / 2;
		wmf.arc(
				x,
				y,
				x + width + 1,
				y + height + 1,
				i_22_
						+ (int) Math.round((double) width
								* Math
										.sin(6.283185307179586 * (double) (startAngle + arcAngle + 90) / 360.0)),
				i_23_
						+ (int) Math.round((double) height
								* Math
										.cos(6.283185307179586 * (double) (startAngle + arcAngle + 90) / 360.0)),
				i_22_
						+ (int) Math.round((double) width
								* Math
										.sin(6.283185307179586 * (double) (startAngle + 90) / 360.0)),
				i_23_
						+ (int) Math.round((double) height
								* Math
										.cos(6.283185307179586 * (double) (startAngle + 90) / 360.0)));
	}

	//CS-IGNORE:ON FINAL_PARAMETERS
	public boolean drawImage(final Image image, int i, int i_24_, final int i_25_, final int i_26_,
			int i_27_, int i_28_, final int i_29_, int i_30_, final Color color,
			final ImageObserver imageobserver)
	{
		final int im_width = image.getWidth(imageobserver);
		final int im_height = image.getHeight(imageobserver);
		final int image_array[] = new int[im_width * im_height];
		final PixelGrabber pixelgrabber = new PixelGrabber(image, 0, 0, im_width,
				im_height, image_array, 0, im_width);
		try
		{
			pixelgrabber.grabPixels();
		}
		catch (final InterruptedException interruptedexception)
		{
			return false;
		}

		if ((pixelgrabber.status() & 0x80) != 0)
			return false;

		int i_33_ = i_25_ - i;
		int i_34_ = i_26_ - i_24_;
		int i_35_ = i_29_ - i_27_;
		int i_36_ = i_30_ - i_28_;
		final int i_37_ = i_30_;

		i_30_ = im_height - i_28_;
		i_28_ = im_height - i_37_;

		if ((i_33_ < 0) != (i_35_ < 0))
		{
			flipHorizontal(image_array, im_width, im_height);
			if (i_35_ < 0)
				i_27_ = im_width - i_27_;
			else
				i_27_ = im_width - i_29_;
		}

		if (i_33_ < 0)
		{
			i = i_25_;
			if (i_35_ < 0)
				i_27_ = i_29_;

			i_33_ = -i_33_;
		}
		if (i_35_ < 0)
			i_35_ = -i_35_;

		if ((i_34_ < 0) != (i_36_ < 0))
		{
			flipVertical(image_array, im_width, im_height);
			if (i_36_ < 0)
				i_28_ = im_height - i_28_;
			else
				i_28_ = im_height - i_30_;
		}

		if (i_34_ < 0)
		{
			i_24_ = i_26_;
			if (i_36_ < 0)
				i_28_ = i_30_;
			i_34_ = -i_34_;
		}

		if (i_36_ < 0)
			i_36_ = -i_36_;

		final int i_38_ = color.getRGB();
		for (int i_39_ = 0; i_39_ < image_array.length; i_39_++)
		{
			if ((image_array[i_39_] & 0xff000000) == 0)
				image_array[i_39_] = i_38_;
		}

		wmf.stretchBlt(i, i_24_, i_33_, i_34_, i_27_, i_28_, i_35_, i_36_,
				0xcc0020, image_array, im_width, im_height);
		return true;

	}//CS-IGNORE:OFF FINAL_PARAMETERS
	
	//CS-IGNORE:ON FINAL_PARAMETERS
	public boolean drawImage(final Image image, int tl_x, int tl_y, final int br_x, final int br_y,
			int i_43_, int i_44_, final int im_width, int im_height,
			final ImageObserver imageobserver)
	{

		final int im_width2 = image.getWidth(imageobserver);
		final int im_height2 = image.getHeight(imageobserver);
		final int is[] = new int[im_width2 * im_height2];

		final PixelGrabber pixelgrabber = new PixelGrabber(image, 0, 0, im_width2,
				im_height2, is, 0, im_width2);

		try
		{
			pixelgrabber.grabPixels();
		}
		catch (final InterruptedException interruptedexception)
		{
			return false;
		}

		if ((pixelgrabber.status() & 0x80) != 0)
			return false;

		int im_width3 = br_x - tl_x;
		int im_height3 = br_y - tl_y;
		int i_51_ = im_width - i_43_;
		int i_52_ = im_height - i_44_;
		final int i_53_ = im_height;

		im_height = im_height2 - i_44_;
		i_44_ = im_height2 - i_53_;

		if ((im_width3 < 0) != (i_51_ < 0))
		{
			flipHorizontal(is, im_width2, im_height2);
			if (i_51_ < 0)
				i_43_ = im_width2 - i_43_;
			else
				i_43_ = im_width2 - im_width;
		}

		if (im_width3 < 0)
		{
			tl_x = br_x;
			if (i_51_ < 0)
				i_43_ = im_width;
			im_width3 = -im_width3;
		}

		if (i_51_ < 0)
			i_51_ = -i_51_;

		if (im_height3 < 0 != i_52_ < 0)
		{
			flipVertical(is, im_width2, im_height2);
			if (i_52_ < 0)
				i_44_ = im_height2 - i_44_;
			else
				i_44_ = im_height2 - im_height;
		}

		if (im_height3 < 0)
		{
			tl_y = br_y;
			if (i_52_ < 0)
				i_44_ = im_height;
			im_height3 = -im_height3;
		}

		if (i_52_ < 0)
			i_52_ = -i_52_;

		final int[] is_54_ = new int[is.length];

		for (int i_55_ = 0; i_55_ < is.length; i_55_++)
		{
			if ((is[i_55_] & 0xff000000) == 0)
			{
				is_54_[i_55_] = -1;
				is[i_55_] = 0;
			}
			else
				is_54_[i_55_] = 0;

		}

		wmf.stretchBlt(tl_x, tl_y, im_width3, im_height3, i_43_, i_44_, i_51_,
				i_52_, 0x8800c6, is_54_, im_width2, im_height2);
		wmf.stretchBlt(tl_x, tl_y, im_width3, im_height3, i_43_, i_44_, i_51_,
				i_52_, 0xee0086, is, im_width2, im_height2);
		return true;
	}
	//CS-IGNORE:OFF FINAL_PARAMETERS

	public boolean drawImage(final Image image, final int i, final int i_56_, final int i_57_, final int i_58_,
			final Color color, final ImageObserver imageobserver)
	{
		return drawImage(image, i, i_56_, i + i_57_, i_56_ + i_58_, 0, 0,
				image.getWidth(imageobserver), image.getHeight(imageobserver), color,
				imageobserver);
	}

	public boolean drawImage(final Image image, final int x, final int y, final int width, final int height,
			final ImageObserver imageobserver)
	{
		return drawImage(image, x, y, x + width, y + height, 0, 0,
				image.getWidth(imageobserver), image.getHeight(imageobserver),
				imageobserver);
	}

	public boolean drawImage(final Image image, final int i, final int i_62_, final Color color,
			final ImageObserver imageobserver)
	{
		return drawImage(image, i, i_62_, image.getWidth(imageobserver),
				image.getHeight(imageobserver), color, imageobserver);
	}

	public boolean drawImage(final Image image, final int i, final int i_63_,
			final ImageObserver imageobserver)
	{
		return drawImage(image, i, i_63_, image.getWidth(imageobserver),
				image.getHeight(imageobserver), imageobserver);
	}

	public void drawLine(final int i, final int i_64_, final int i_65_, final int i_66_)
	{
		wmf.moveTo(i, i_64_);
		wmf.lineTo(i_65_, i_66_);
		wmf.setPixel(i_65_, i_66_, getColor());
	}

	public void drawOval(final int i, final int i_67_, final int i_68_, final int i_69_)
	{
		wmf.ellipse(i, i_67_, i + i_68_ + 1, i_67_ + i_69_ + 1);
	}

	public void drawPolygon(final int is[], final int is_70_[], final int i)
	{
		wmf.polygon(is, is_70_, i);
	}

	public void drawPolyline(final int[] is, final int[] is_71_, final int i)
	{
		wmf.polyline(is, is_71_, i);
		wmf.setPixel(is[i - 1], is_71_[i - 1], getColor());
	}

	final public void drawPolyline(final int[] points)
	{
		// get the convenience function to plot this for us
		CanvasAdaptor.drawPolylineForMe(points, this);
	}

	public void drawRect(final int i, final int i_72_, final int i_73_, final int i_74_)
	{
		wmf.rectangle(i, i_72_, i + i_73_ + 1, i_72_ + i_74_ + 1);
	}

	public void drawRoundRect(final int i, final int i_75_, final int i_76_, final int i_77_, final int i_78_,
			final int i_79_)
	{
		wmf.roundRect(i, i_75_, i + i_76_ + 1, i_75_ + i_77_ + 1, i_78_, i_79_);
	}

	public void drawString(final String string, final int i, final int i_80_)
	{
		wmf.textOut(i, i_80_, string);
	}

	// #if WIN32

	// #else
	/*
	 * public void drawString(java.text.AttributedCharacterIterator iterator, int
	 * x, int y) { }
	 */
	// #endif

	public void fillArc(final int i, final int i_82_, final int i_83_, final int i_84_, final int i_85_,
			final int i_86_)
	{
		setGDIFillBrush();
		final int i_87_ = i + i_83_ / 2;
		final int i_88_ = i_82_ + i_84_ / 2;
		wmf.pie(
				i,
				i_82_,
				i + i_83_ + 1,
				i_82_ + i_84_ + 1,
				i_87_
						+ (int) Math.round((double) i_83_
								* Math.sin(6.283185307179586 * (double) (i_85_ + 90) / 360.0)),
				i_88_
						+ (int) Math.round((double) i_84_
								* Math.cos(6.283185307179586 * (double) (i_85_ + 90) / 360.0)),
				i_87_
						+ (int) Math.round((double) i_83_
								* Math
										.sin(6.283185307179586 * (double) (i_85_ + i_86_ + 90) / 360.0)),
				i_88_
						+ (int) Math.round((double) i_84_
								* Math
										.cos(6.283185307179586 * (double) (i_85_ + i_86_ + 90) / 360.0)));
		setGDIHollowBrush();
	}

	public void fillOval(final int i, final int i_89_, final int i_90_, final int i_91_)
	{
		setGDIFillBrush();
		drawOval(i, i_89_, i_90_, i_91_);
		setGDIHollowBrush();
	}

	public void fillPolygon(final int[] is, final int[] is_92_, final int i)
	{
		setGDIFillBrush();
		drawPolygon(is, is_92_, i);
		setGDIHollowBrush();
	}

	public void fillRect(final int i, final int i_93_, final int i_94_, final int i_95_)
	{
		setGDIFillBrush();
		drawRect(i, i_93_, i_94_, i_95_);
		setGDIHollowBrush();
	}

	public void fillRoundRect(final int i, final int i_96_, final int i_97_, final int i_98_, final int i_99_,
			final int i_100_)
	{
		setGDIFillBrush();
		drawRoundRect(i, i_96_, i_97_, i_98_, i_99_, i_100_);
		setGDIHollowBrush();
	}

	private void flipHorizontal(final int is[], final int i, final int i_101_)
	{
		for (int i_102_ = 0; i_102_ < i_101_; i_102_++)
		{
			final int i_103_ = i_102_ * i_101_;
			for (int i_104_ = 0; i_104_ < i / 2; i_104_++)
			{
				final int i_105_ = is[i_103_ + i_104_];
				is[i_103_ + i_104_] = is[(i_103_ + i) - 1 - i_104_];
				is[(i_103_ + i) - 1 - i_104_] = i_105_;
			}
		}
	}

	private void flipVertical(final int[] is, final int i, final int i_106_)
	{
		final int[] is_107_ = new int[i];
		for (int i_108_ = 0; i_108_ < i_106_ / 2; i_108_++)
		{
			System.arraycopy(is, i_108_ * i, is_107_, 0, i);
			System.arraycopy(is, (i_106_ - i_108_ - 1) * i, is, i_108_ * i, i);
			System.arraycopy(is_107_, 0, is, (i_106_ - i_108_ - 1) * i, i);
		}
	}

	public int getBrushFillStyle()
	{
		return brushfillstyle;
	}

	public int getBrushHatch()
	{
		return brushhatch;
	}

	public Image getBrushPattern()
	{
		return brushpattern;
	}

	public Shape getClip()
	{
		// System.err.println("getClip not supported");
		return null;
	}

	public Rectangle getClipBounds()
	{
		// System.err.println("getClipBounds not supported");
		return null;
	}

	public Color getColor()
	{
		return foreground;
	}

	public Font getFont()
	{
		return font;
	}

	public int getFontEscapement()
	{
		return fontescapement;
	}

	@SuppressWarnings("deprecation")
	public FontMetrics getFontMetrics(final Font font1)
	{
		return Toolkit.getDefaultToolkit().getFontMetrics(font1);
	}

	public int getPenStyle()
	{
		return penstyle;
	}

	public int getPenWidth()
	{
		return penwidth;
	}

	public WMF getWMF()
	{
		return wmf;
	}

	public void reset()
	{
		setPenStyle(0);
		setPenWidth(0);
		setBrushFillStyle(0);
		setBrushHatch(0);
		setFontEscapement(0);
	}

	public void setBrushFillStyle(final int i)
	{
		brushfillstyle = i;
	}

	public void setBrushHatch(final int i)
	{
		brushhatch = i;
	}

	public void setBrushPattern(final Image image)
	{
		brushpattern = image;
	}

	public void setClip(final int x, final int i_109_, final int i_110_, final int i_111_)
	{
		// System.err.println("setClip (coords) not supported");
	}

	public void setClip(final Shape shape)
	{
		// if(shape != null)
		// System.err.println("setClip (shape) not supported:" + shape);
		// else
		// System.err.println("resetting clip region (null shape)");
	}

	public void setColor(final Color color)
	{
		foreground = color;
		setGDIPen();
		wmf.setTextColor(foreground);
	}

	public void setFont(final Font font)
	{
		this.font = font;
		setGDIFont();
	}

	public void setDirectedFont(final Font font)
	{
		this.font = font;
	}

	public void setFontEscapement(final int i)
	{
		fontescapement = i;
		setGDIFont();
	}

	public int setGDIFillBrush()
	{
		wmf.deleteObject(brushhandle);
		if (brushfillstyle == 3)
		{
			if (brushpattern != null)
			{
				final int i = brushpattern.getWidth(null);
				final int i_112_ = brushpattern.getHeight(null);
				final int[] is = new int[i * i_112_];
				final PixelGrabber pixelgrabber = new PixelGrabber(brushpattern, 0, 0, i,
						i_112_, is, 0, i);
				try
				{
					pixelgrabber.grabPixels();
					if ((pixelgrabber.status() & 0x80) != 0)
						wmf.createBrushIndirect(0, foreground, brushhatch);
					else
						wmf.createPatternBrush(is, i, i_112_);
				}
				catch (final InterruptedException interruptedexception)
				{
					wmf.createBrushIndirect(0, foreground, brushhatch);
				}
			}
			else
				wmf.createBrushIndirect(0, foreground, brushhatch);
		}
		else
			wmf.createBrushIndirect(brushfillstyle, foreground, brushhatch);

		wmf.selectObject(brushhandle);
		return brushhandle;
	}

	public int setGDIFont()
	{
		wmf.deleteObject(fonthandle);
		wmf.createFont(font, fontescapement, false, false);
		wmf.selectObject(fonthandle);
		return fonthandle;
	}

	public int setGDIHollowBrush()
	{
		wmf.deleteObject(brushhandle);
		wmf.createBrushIndirect(1, foreground, brushhatch);
		wmf.selectObject(brushhandle);
		return brushhandle;
	}

	public int setGDIPen()
	{
		wmf.deleteObject(penhandle);
		wmf.createPenIndirect(penstyle, penwidth, foreground);
		wmf.selectObject(penhandle);
		return penhandle;
	}

	public void setPaintMode()
	{
		// System.err.println("setPaintMode not supported");
	}

	public void setPenStyle(final int i)
	{
		penstyle = i;
		setGDIPen();
	}

	public void setPenWidth(final int i)
	{
		penwidth = i;
		setGDIPen();
	}

	public void setWMF(final WMF wmf, final int width, final int height)
	{
		this.wmf = wmf;
		penhandle = this.wmf.createPenIndirect(penstyle, penwidth, foreground);
		this.wmf.selectObject(penhandle);
		brushhandle = this.wmf.createBrushIndirect(1, foreground, brushhatch);
		this.wmf.selectObject(brushhandle);
		fonthandle = this.wmf.createFont(font, fontescapement, false, false);
		this.wmf.selectObject(fonthandle);
		setup(width, height);
	}

	public void setXORMode(final Color color)
	{
		// System.err.println("setXORMode not supported");
	}

	private void setup(final int width, final int height)
	{
		wmf.setMapMode(8);
		wmf.setWindowOrg(0, 0);
		wmf.setWindowExt(width, height);
		wmf.setTextAlign(24);
		wmf.setBKMode(1);
		wmf.setBKColor(background);
		wmf.setTextColor(foreground);
		wmf.setPolyFillMode(1);
		wmf.setStretchBltMode(3);
		wmf.setROP2(13);
		wmf.setTextCharacterExtra(0);
	}

	public void translate(final int i, final int i_115_)
	{
		wmf.setWindowOrg(-i, -i_115_);
	}

	// /////////////////////////////////
	// constructor
	// ////////////////////////////////

	// /////////////////////////////////
	// member functions
	// ////////////////////////////////

	// /////////////////////////////////
	// nested classes
	// ////////////////////////////////
	public void drawString(final java.text.AttributedCharacterIterator x, final int y, final int z)
	{
		// only inserted to keep the compiler happy
	}

	/**
	 * set the style for the line, using our constants
	 * 
	 */
	public void setLineStyle(final int style)
	{
		setPenStyle(style);
	}

	/**
	 * set the width of the line, in pixels
	 * 
	 */
	public void setLineWidth(final float width)
	{
		setPenWidth((int) width);
	}

	/**
	 * get the width of the line, in pixels
	 * 
	 */
	public float getLineWidth()
	{
		return getPenWidth();
	}

	public java.awt.Dimension getSize()
	{
		return null;
	}

	/**
	 * update the information currently plotted on chart
	 */
	public void updateMe()
	{
	}

	public void drawText(final String str, final int x, final int y)
	{
		this.drawString(str, x, y);
	}

	public void drawDirectedText(final java.awt.Font theFont, final int direction,
			final String str, final int x, final int y)
	{
		final int oldDir = this.getFontEscapement();
		this.setFontEscapement(direction);
		this.drawString(str, x, y);
		this.setFontEscapement(oldDir);
	}

	public void drawText(final java.awt.Font theFont, final String theStr, final int x, final int y)
	{
		this.setFont(theFont);
		drawString(theStr, x, y);
	}

	public int getStringHeight(final java.awt.Font theFont)
	{
		return 0;
	}

	public int getStringWidth(final java.awt.Font theFont, final String theString)
	{
		return 0;
	}

	/**
	 * expose the graphics object, used only for plotting non-persistent graphics
	 * (temporary lines, etc).
	 */
	public java.awt.Graphics getGraphicsTemp()
	{
		return null;
	}

	/** client has finished drawing operation */
	public void endDraw(final Object theVal)
	{

	}

	/** client is about to start drawing operation */
	public void startDraw(final Object theVal)
	{
	}

	public MWC.Algorithms.PlainProjection getProjection()
	{
		return _projection;
	}

	public void setProjection(final MWC.Algorithms.PlainProjection val)
	{
		_projection = val;
	}

	public java.awt.Point toScreen(final MWC.GenericData.WorldLocation val)
	{
		return _projection.toScreen(val);
	}

	public MWC.GenericData.WorldLocation toWorld(final java.awt.Point val)
	{
		return _projection.toWorld(val);
	}

	/**
	 * retrieve the full data area, and do a fit to window
	 */
	public void rescale()
	{
	}

	/**
	 * set/get the background colour
	 */
	public java.awt.Color getBackgroundColor()
	{
		return background;
	}

	public void setBackgroundColor(final java.awt.Color theColor)
	{
		background = theColor;
	}

	public void addPainter(final MWC.GUI.CanvasType.PaintListener listener)
	{
	}

	public void removePainter(final MWC.GUI.CanvasType.PaintListener listener)
	{
	}

	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public java.util.Enumeration getPainters()
	{
		return null;
	}

	/**
   *
   */
	public void setTooltipHandler(final MWC.GUI.CanvasType.TooltipHandler handler)
	{
	}

	@Override
	public void drawText(final String str, final int x, final int y, final float rotate)
	{
		Trace.getParent().logError(ToolParent.WARNING,
				"Rotated text not availble for write to metafile", null);
	}

	@Override
	public void drawText(String str, int x, int y, float rotate, boolean above)
	{
		
	}

}
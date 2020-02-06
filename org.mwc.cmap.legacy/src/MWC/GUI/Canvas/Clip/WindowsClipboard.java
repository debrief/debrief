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

package MWC.GUI.Canvas.Clip;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.ImageObserver;

import MWC.Algorithms.PlainProjection;
import MWC.GUI.CanvasType;
import MWC.GUI.Canvas.CanvasAdaptor;
import MWC.GenericData.WorldLocation;

/**
 *
 * @author Ian.Mayo
 * @version
 */
public class WindowsClipboard implements CanvasType {
	static {
		// System.loadLibrary("MS_Utils");
	}

	public static void main(final String[] args) {
		final WindowsClipboard wc = new WindowsClipboard();
		wc.startDraw(null);
		wc.drawLine(100, 200, 12, 9);
		wc.setColor(Color.orange);
		wc.drawLine(100, 200, 400, 300);
		final java.util.Date gt = new java.util.Date();
		wc.drawText(new java.awt.Font("Times New Roman", java.awt.Font.BOLD, 9), gt.toString(), 30, 40);
		wc.fillRect(50, 50, 10, 20);
		wc.endDraw(null);
	}

	private PlainProjection _myProjection;

	private java.awt.Font _lastFont;

	/** Creates new WindowsClipboard */
	public WindowsClipboard() {
	}

	@Override
	public void addPainter(final CanvasType.PaintListener listener) {
	}

	@Override
	public native void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle);

	// CS-IGNORE:ON FINAL_PARAMETERS
	@Override
	public native boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer);

	@Override
	public native void drawLine(int startX, int startY, int endX, int endY);

	@Override
	public void drawOval(final int x, final int y, final int width, final int height) {
	}

	@Override
	public native void drawPolygon(int[] xPoints, int[] yPoints, int nPoints);

	@Override
	final public void drawPolyline(final int[] points) {
		// get the convenience function to plot this for us
		CanvasAdaptor.drawPolylineForMe(points, this);
	}

	@Override
	public native void drawPolyline(int[] xPoints, int[] yPoints, int nPoints);

	@Override
	public native void drawRect(int startX, int startY, int width, int height);

	public native void drawText(int x, int y, String str, int length);

	@Override
	public void drawText(final java.awt.Font theFont, final String theStr, final int x, final int y) {

		// resend the font data, if we have to
		if (theFont != _lastFont) {
			setFont(theFont);

		}
		// and write the text
		drawText(x, y, theStr, (theStr.length()));
	}

	@Override
	public void drawText(final String str, final int x, final int y) {
		drawText(x, y, str, (str.length()) + 1);
	}

	@Override
	public void drawText(final String str, final int x, final int y, final float rotate) {

	}

	@Override
	public void drawText(final String str, final int x, final int y, final float rotate, final boolean above) {

	}

	@Override
	public native void endDraw(Object theVal);

	@Override
	public native void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle);

	public native void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle, int alpha);

	@Override
	public void fillOval(final int x, final int y, final int width, final int height) {
	}

	@Override
	public native void fillPolygon(int[] xPoints, int[] yPoints, int nPoints);

	@Override
	public native void fillRect(int startX, int startY, int width, int height);

	/**
	 * set/get the background colour
	 */
	@Override
	public java.awt.Color getBackgroundColor() {
		return null;
	}

	/**
	 * expose the graphics object, used only for plotting non-persistent graphics
	 * (temporary lines, etc).
	 */
	@Override
	public java.awt.Graphics getGraphicsTemp() {
		return null;
	}

	@Override
	public native float getLineWidth();

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public java.util.Enumeration getPainters() {
		return null;
	}

	@Override
	public PlainProjection getProjection() {
		return _myProjection;
	}

	@Override
	public java.awt.Dimension getSize() {
		return null;
	}

	@Override
	public int getStringHeight(final java.awt.Font theFont) {
		return 5;
	}

	@Override
	public int getStringWidth(final java.awt.Font theFont, final String theString) {
		return 5;
	}

	@Override
	public void removePainter(final CanvasType.PaintListener listener) {
	}

	/**
	 * retrieve the full data area, and do a fit to window
	 */
	@Override
	public void rescale() {
	}

	public native void semiFillPolygon(int[] xPoints, int[] yPoints, int nPoints);

	@Override
	public void setBackgroundColor(final java.awt.Color theColor) {
	}

	private native void setColor(int r, int g, int b);

	@Override
	public void setColor(final java.awt.Color theCol) {
		setColor(theCol.getRed(), theCol.getGreen(), theCol.getBlue());
	}

	protected native void setDataArea(int wid, int ht);

	public native void setFont(int height, String name, int length);

	@Override
	public void setFont(final java.awt.Font theFont) {
		_lastFont = theFont;
		// get the details of the font
		final int ht = theFont.getSize();

		//
		final String name = theFont.getFamily();
		setFont(ht, name, name.length());
	}

	@Override
	public native void setLineStyle(int style);

	@Override
	public native void setLineWidth(float width);
	// CS-IGNORE:OFF FINAL_PARAMETERS

	@Override
	public void setProjection(final PlainProjection val) {
		_myProjection = val;
		final java.awt.Dimension dim = val.getScreenArea();
		setDataArea(dim.width, dim.height);
	}

	@Override
	public void setTooltipHandler(final CanvasType.TooltipHandler handler) {
	}

	@Override
	public void startDraw(final Object val) {
		//
		startDraw(val, 0, 0);
	}

	public native void startDraw(Object theVal, int wid, int ht);

	@Override
	public java.awt.Point toScreen(final WorldLocation val) {
		return _myProjection.toScreen(val);
	}

	@Override
	public WorldLocation toWorld(final java.awt.Point val) {
		return _myProjection.toWorld(val);
	}

	/**
	 * update the information currently plotted on chart
	 */
	@Override
	public void updateMe() {
	}

}

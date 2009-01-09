/*
 * CanvasAdaptor.java
 *
 * Created on 22 September 2000, 11:49
 */

package MWC.GUI.Canvas;

import MWC.GUI.*;
import MWC.GenericData.*;

import java.awt.*;
import java.awt.image.ImageObserver;

/**
 * 
 * @author IAN MAYO
 * @version
 */

public class CanvasAdaptor implements MWC.GUI.CanvasType {

	private java.awt.Graphics _dest;

	private MWC.Algorithms.PlainProjection _proj;

	/** Creates new CanvasAdaptor */
	public CanvasAdaptor(MWC.Algorithms.PlainProjection proj,
			java.awt.Graphics dest) {
		_proj = proj;
		_dest = dest;
	}

	public void addPainter(CanvasType.PaintListener listener) {
		// nada
	}

	public void drawLine(int x1, int y1, int x2, int y2) {
		_dest.drawLine(x1, y1, x2, y2);
	}

	public boolean drawImage(Image img, int x, int y, int width, int height,
			ImageObserver observer) {
		if (_dest == null)
			return false;

		return _dest.drawImage(img, x, y, width, height, observer);
	}

	/**
	 * draw a filled polygon
	 * 
	 * @param xPoints
	 *            list of x coordinates
	 * @param yPoints
	 *            list of y coordinates
	 * @param nPoints
	 *            length of list
	 */
	public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		_dest.fillPolygon(xPoints, yPoints, nPoints);
	}

	/**
	 * drawPolyline
	 * 
	 * @param xPoints
	 *            list of x coordinates
	 * @param yPoints
	 *            list of y coordinates
	 * @param nPoints
	 *            length of list
	 */
	public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
		_dest.drawPolyline(xPoints, yPoints, nPoints);
	}

	/**
	 * drawPolygon
	 * 
	 * @param xPoints
	 *            list of x coordinates
	 * @param yPoints
	 *            list of y coordinates
	 * @param nPoints
	 *            length of list
	 */
	public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		_dest.drawPolygon(xPoints, yPoints, nPoints);
	}

	public void drawText(java.awt.Font theFont, String theStr, int x, int y) {
		_dest.setFont(theFont);
		_dest.drawString(theStr, x, y);
	}

	/**
	 * set/get the background colour
	 */
	public java.awt.Color getBackgroundColor() {
		return null;
	}

	/**
	 * expose the graphics object, used only for plotting non-persistent
	 * graphics (temporary lines, etc).
	 */
	public java.awt.Graphics getGraphicsTemp() {
		return _dest;
	}

	@SuppressWarnings("unchecked")
	public java.util.Enumeration getPainters() {
		return null;
	}

	public MWC.Algorithms.PlainProjection getProjection() {
		return _proj;
	}

	public java.awt.Dimension getSize() {
		return _proj.getScreenArea().getSize();
	}

	public int getStringHeight(java.awt.Font theFont) {
		return _dest.getFontMetrics(theFont).getHeight();
	}

	public int getStringWidth(java.awt.Font theFont, String theString) {
		return _dest.getFontMetrics(theFont).stringWidth(theString);
	}

	public void removePainter(CanvasType.PaintListener listener) {
		//
	}

	/**
	 * retrieve the full data area, and do a fit to window
	 */
	public void rescale() {
		//
	}

	public void setBackgroundColor(java.awt.Color theColor) {
		//
	}

	public void setProjection(MWC.Algorithms.PlainProjection val) {
		//
	}

	public void setTooltipHandler(CanvasType.TooltipHandler handler) {
		//
	}

	public java.awt.Point toScreen(WorldLocation val) {
		return _proj.toScreen(val);
	}

	public WorldLocation toWorld(java.awt.Point val) {
		return _proj.toWorld(val);
	}

	public void updateMe() {
		//
	}

	public void drawOval(int x, int y, int width, int height) {
		//
		_dest.drawOval(x, y, width, height);
	}

	public void fillOval(int x, int y, int width, int height) {
		//
		_dest.fillOval(x, y, width, height);
	}

	public void drawText(String str, int x, int y) {
		//
		_dest.drawString(str, x, y);
	}

	public void drawRect(int x1, int y1, int wid, int height) {
		//
		_dest.drawRect(x1, y1, wid, height);
	}

	public void fillRect(int x, int y, int wid, int height) {
		//
		_dest.fillRect(x, y, wid, height);
	}

	/** client has finished drawing operation */
	public void endDraw(Object theVal) {
		//
	}

	/** client is about to start drawing operation */
	public void startDraw(Object theVal) {
		//
	}

	/**
	 * set the style for the line, using our constants
	 * 
	 */
	public void setLineStyle(int style) {
		java.awt.BasicStroke stk = MWC.GUI.Canvas.Swing.SwingCanvas
				.getStrokeFor(style);
		java.awt.Graphics2D g2 = (java.awt.Graphics2D) _dest;
		g2.setStroke(stk);
	}

	/**
	 * set the width of the line, in pixels
	 * 
	 */
	public void setLineWidth(float width) {
		java.awt.BasicStroke stk = new BasicStroke(width);
		java.awt.Graphics2D g2 = (java.awt.Graphics2D) _dest;
		g2.setStroke(stk);
	}

	public float getLineWidth() {
		java.awt.Graphics2D g2 = (java.awt.Graphics2D) _dest;
		BasicStroke bs = (BasicStroke) g2.getStroke();
		return bs.getLineWidth();
	}

	public void setColor(java.awt.Color theCol) {
		//
		_dest.setColor(theCol);
	}

	public void fillArc(int x, int y, int width, int height, int startAngle,
			int arcAngle) {
		//
		_dest.fillArc(x, y, width, height, startAngle, arcAngle);
	}

	public void drawArc(int x, int y, int width, int height, int startAngle,
			int arcAngle) {
		//
		_dest.drawArc(x, y, width, height, startAngle, arcAngle);
	}

	final public void drawPolyline(int[] points) {
		// get the convenience function to plot this for us
		CanvasAdaptor.drawPolylineForMe(points, this);
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	static public final class testMe extends junit.framework.TestCase {
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public testMe(final String val) {
			super(val);
		}

		public final void testPolygonMgt() {
			int[] points = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
			// convert to normal format
			int len = points.length / 2;
			int[] xP = new int[len];
			int[] yP = new int[len];

			for (int i = 0; i < points.length; i += 2) {
				xP[i / 2] = points[i];
				yP[i / 2] = points[i + 1];
			}
			assertEquals("array wrong length", len, 5);
			assertEquals("wrong first x", xP[0], 1);
			assertEquals("wrong first y", yP[0], 2);
			assertEquals("wrong last x", xP[len - 1], 9);
			assertEquals("wrong last y", yP[len - 1], 10);
		}
	}

	/** convenience method that allows a canvas implementation to support 
	 * the new polyline method by just converting the data and calling the old 
	 * method
	 * @param points the series of points in the new format
	 * @param canvas the canvas implementation that wants to plot it.
	 */
	public static void drawPolylineForMe(final int[] points,
			final CanvasType canvas) {
		int len = points.length / 2;
		int[] xP = new int[len];
		int[] yP = new int[len];

		// copy bits in to new arrays
		for (int i = 0; i < points.length; i += 2) {
			xP[i / 2] = points[i];
			yP[i / 2] = points[i + 1];
		}

		// do the old-fashioned copy operation
		canvas.drawPolyline(xP, yP, len);

	}
}

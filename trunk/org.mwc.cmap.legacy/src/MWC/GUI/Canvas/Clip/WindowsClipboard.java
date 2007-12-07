/*
 * WindowsClipboard.java
 *
 * Created on 10 October 2000, 14:22
 */

package MWC.GUI.Canvas.Clip;

import MWC.GUI.CanvasType;
import MWC.GUI.Canvas.CanvasAdaptor;
import MWC.Algorithms.PlainProjection;
import java.awt.*;
import java.awt.image.ImageObserver;

import MWC.GenericData.*;

/**
 *
 * @author  Ian.Mayo
 * @version
 */
public class WindowsClipboard implements CanvasType{


  public native boolean drawImage(Image img,
                                  int x,
                                  int y,
                                  int width,
                                  int height,
                                  ImageObserver observer);
  public native void drawLine(int startX,int startY,int endX,int endY);
  public native void drawPolyline(int[] xPoints,
                          int[] yPoints,
                          int nPoints);

	final public void drawPolyline(int[] points) {
		// get the convenience function to plot this for us
		CanvasAdaptor.drawPolylineForMe(points, this);
	}
  public native void drawPolygon(int[] xPoints,
                          int[] yPoints,
                          int nPoints);
  public native void fillPolygon(int[] xPoints,
                          int[] yPoints,
                          int nPoints);

  public native void drawRect(int startX,int startY,int width,int height);
  public native void fillRect(int startX,int startY,int width,int height);
  public native void drawText(int x, int y, String str, int length);
  public native void setFont(int height, String name, int length);
  protected native void setDataArea(int wid, int ht);

  public native void drawArc(int x,int y,int width,int height,int startAngle,int arcAngle) ;
  public native void fillArc(int x,int y,int width,int height,int startAngle,int arcAngle);

  public native void endDraw(Object theVal);
  public native void startDraw(Object theVal, int wid, int ht);
  private native void setColor(int r, int g, int b);

  public native void setLineStyle(int style);

  public native void setLineWidth(float width);
  public native float getLineWidth();

  private PlainProjection _myProjection;
  private java.awt.Font _lastFont;

  static{
  //  System.loadLibrary("MS_Utils");
  }

    /** Creates new WindowsClipboard */
    public WindowsClipboard() {
    }

    public static void main(String[] args)
    {
      WindowsClipboard wc = new WindowsClipboard();
      wc.startDraw(null);
      wc.drawLine(100, 200, 12, 9);
      wc.setColor(Color.orange);
      wc.drawLine(100, 200, 400, 300);
      java.util.Date gt = new java.util.Date();
      wc.drawText(new java.awt.Font("Times New Roman",java.awt.Font.BOLD, 9), gt.toString(), 30, 40);
      wc.fillRect(50, 50, 10, 20);
      wc.endDraw(null);
    }

    /** update the information currently plotted on chart
      */
    public void updateMe() {
    }

    public void drawOval(int x,int y,int width,int height) {
    }

    public void fillOval(int x,int y,int width,int height) {
    }

    public void drawText(String str,int x,int y) {
      drawText(x, y, str, (str.length())+1);
    }

    public void setColor(java.awt.Color theCol) {
      setColor(theCol.getRed(), theCol.getGreen(), theCol.getBlue());
    }


    public void drawText(java.awt.Font theFont,String theStr,int x,int y) {

      // resend the font data, if we have to
      if(theFont != _lastFont)
      {
        _lastFont = theFont;
        // get the details of the font
        int ht = theFont.getSize();

        //
        String name = theFont.getFamily();
        setFont(ht, name, name.length());

      }
      // and write the text
      drawText(x, y, theStr, (theStr.length()));
    }

    public int getStringHeight(java.awt.Font theFont) {
      return 5;
    }

    public int getStringWidth(java.awt.Font theFont,String theString) {
      return 5;
    }

    /** expose the graphics object, used only for
 * plotting non-persistent graphics
 * (temporary lines, etc).
 */
    public java.awt.Graphics getGraphicsTemp() {
      return null;
    }

    public PlainProjection getProjection() {
      return _myProjection;
    }

    public void setProjection(PlainProjection val) {
      _myProjection = val;
      java.awt.Dimension dim = val.getScreenArea();
      setDataArea(dim.width, dim.height);
    }

    public java.awt.Point toScreen(WorldLocation val) {
      return _myProjection.toScreen(val);
    }

    public WorldLocation toWorld(java.awt.Point val) {
      return _myProjection.toWorld(val);
    }

    /** retrieve the full data area, and do a fit to window
 */
    public void rescale() {
    }

    /** set/get the background colour
 */
    public java.awt.Color getBackgroundColor() {
      return null;
    }

    public void setBackgroundColor(java.awt.Color theColor) {
    }

    public java.awt.Dimension getSize() {
      return null;
    }

    public void addPainter(CanvasType.PaintListener listener) {
    }

    public void removePainter(CanvasType.PaintListener listener) {
    }

    public java.util.Enumeration getPainters() {
      return null;
    }

    public void setTooltipHandler(CanvasType.TooltipHandler handler) {
    }

    public void startDraw(Object val)
    {
      //
      startDraw(val, 0, 0);
    }

}

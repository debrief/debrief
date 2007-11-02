 // Copyright MWC 1999, Debrief 3 Project
// $RCSfile: MetafileCanvasGraphics2d.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.5 $
// $Log: MetafileCanvasGraphics2d.java,v $
// Revision 1.5  2006/01/20 14:07:34  Ian.Mayo
// Use valid suffix for tmp file
//
// Revision 1.4  2006/01/20 14:05:54  Ian.Mayo
// Allow writing to temp file
//
// Revision 1.3  2006/01/19 13:01:35  Ian.Mayo
// Provide accessors to help us copy WMFs to clipboard
//
// Revision 1.2  2004/05/25 14:43:56  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:15  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:07  Ian.Mayo
// Initial import
//
// Revision 1.3  2003-01-28 09:44:16+00  ian_mayo
// comment out d-lines
//
// Revision 1.2  2003-01-14 14:17:41+00  ian_mayo
// Handle more graphics calls, improve d-lines
//
// Revision 1.1  2003-01-14 11:58:00+00  ian_mayo
// Initial revision
//
package MWC.GUI.Canvas;

import MWC.GUI.Canvas.Metafile.*;
//import WMFWriter.*;
import MWC.GUI.*;
import MWC.Algorithms.*;
import java.io.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.RectangularShape;
import java.awt.font.GlyphVector;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.image.ImageObserver;
import java.awt.image.BufferedImageOp;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.util.Map;
import java.text.AttributedCharacterIterator;

import com.jrefinery.ui.RefineryUtilities;

public class MetafileCanvasGraphics2d extends Graphics2D implements CanvasType
{

  ///////////////////////////////////
  // member variables
  //////////////////////////////////

  protected PlainProjection _proj;

  WMF wmf;
  WMFGraphics g;

  String _directory;


  private static final boolean DEBUG_OUTPUT = false;

  private Graphics2D _g2;

  /** the last set of dimensions we plotted
   * 
   */
	private static Dimension _lastPlotSize;

  /** the last output filename we used
   * 
   */
  private static String _outputFileName = null;
  
  /** write to tmp file
   * 
   */
  private boolean _writeToTmpFile = false;

  

  ///////////////////////////////////
  // constructor
  //////////////////////////////////

  public MetafileCanvasGraphics2d(String directory, Graphics2D g2)
  {
    _g2 = g2;

    if(directory != null)
    {
      _directory = directory;
    }
  }

  public MetafileCanvasGraphics2d(Graphics2D g2)
  {
  	this(null, g2);
  	
  	_writeToTmpFile = true;
  }
  

  ///////////////////////////////////
  // member functions
  //////////////////////////////////
  public static String getFileName()
  {
    String name="d3_";
    java.util.Date tNow = new java.util.Date();

    java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("mm_ss");

    name = name + "_" + df.format(tNow) + ".wmf";

    return name;
  }
  
  public void endDraw(Object theVal)
  {
    // and now save it
    try
    {
    	if(_writeToTmpFile)
    	{
    		_outputFileName = java.io.File.createTempFile("debrief_plot_", ".wmf").getCanonicalPath();
    	}
    	else
    	{
      	_outputFileName = getFileName();
      	
        if(_directory != null)
        	_outputFileName = _directory + File.separator + _outputFileName;
    	}
    	
      FileOutputStream fo = new FileOutputStream(_outputFileName);

      wmf.writeWMF(fo);
     // wmf.writePlaceableWMF(fo, 5, 5, 200, 200, 200);
      fo.close();
    }
    catch(FileNotFoundException f)
    {
      MWC.GUI.Dialogs.DialogFactory.showMessage("Write WMF", "Sorry, directory name may be invalid, please check properties");
      if(DEBUG_OUTPUT) MWC.Utilities.Errors.Trace.trace(f, "Directory not found");
    }
    catch(IOException e)
    {

      if(DEBUG_OUTPUT) MWC.Utilities.Errors.Trace.trace(e);
    }
  }

  public void startDraw(Object theVal)
  {
    // create the metafile
    wmf = new WMF();
    _lastPlotSize = _proj.getScreenArea();
//    System.out.println("creating metafile of size width:" + d.width + " height:" + d.height);
    g = new WMFGraphics(wmf,
                        _lastPlotSize.width,
                        _lastPlotSize.height);
  }


  public void updateMe()
  {
  }

  public void drawOval(int x, int y, int width, int height)
  {
    g.drawOval(x, y, width, height);
    if(DEBUG_OUTPUT) MWC.Utilities.Errors.Trace.trace("drawOval");
  }

  public void fillOval(int x, int y, int width, int height)
  {
    g.fillOval(x, y, width, height);
    if(DEBUG_OUTPUT) MWC.Utilities.Errors.Trace.trace("fillOval");
  }

  public void drawText(String str, int x, int y)
  {
    // @@@ IM ignore the WMFGraphics method, since it relies on JDK1.2 code (AttributedIterator)
  //	g.drawString(str, x, y);
    wmf.textOut(x, y, str);

    if(DEBUG_OUTPUT) MWC.Utilities.Errors.Trace.trace("drawText");
  }

  public void drawText(Font theFont, String str, int x, int y)
  {
    // remember the current font
    Font ft = g.getFont();

    g.setFont(theFont);

    // @@@ IM ignore the WMFGraphics method, since it relies on JDK1.2 code (AttributedIterator)
    //  g.drawString(str, x, y);
    wmf.textOut(x, y, str);

    // restore the font
    g.setFont(ft);
    if(DEBUG_OUTPUT) MWC.Utilities.Errors.Trace.trace("drawText");
  }

  public void setColor(Color theCol)
  {
    g.setColor(theCol);
    if(DEBUG_OUTPUT) MWC.Utilities.Errors.Trace.trace("setColor");
  }


  /** set the style for the line, using our constants
   *
   */
  public void setLineStyle(int style)
  {
    g.setPenStyle(style);
  }

  /** set the width of the line, in pixels
   *
   */
  public void setLineWidth(float width)
  {
    // not implemented
    g.setPenWidth((int)width);
  }

  /** get the current line width (when supported)
   *
   * @return the width, in pixels
   */
  public float getLineWidth()
  {
    return g.getPenWidth();
  }

  /**
   * draw a filled polygon
   *
   * @param xPoints list of x coordinates
   * @param yPoints list of y coordinates
   * @param nPoints length of list
   */
  public void fillPolygon(int[] xPoints,
                          int[] yPoints,
                          int nPoints)
  {
    g.fillPolygon(xPoints, yPoints, nPoints);
    if(DEBUG_OUTPUT) MWC.Utilities.Errors.Trace.trace("fillPoly");
  }


  /**
   * drawPolygon
   *
   * @param xPoints list of x coordinates
   * @param yPoints list of y coordinates
   * @param nPoints length of list
   */
  public void drawPolygon(int[] xPoints,
                          int[] yPoints,
                          int nPoints)
  {
    g.drawPolygon(xPoints, yPoints, nPoints);
    if(DEBUG_OUTPUT) MWC.Utilities.Errors.Trace.trace("drawPoly");
  }

  /**
   * drawPolyline
   *
   * @param xPoints list of x coordinates
   * @param yPoints list of y coordinates
   * @param nPoints length of list
   */
  public void drawPolyline(int[] xPoints,
                          int[] yPoints,
                          int nPoints)
  {
    g.drawPolyline(xPoints, yPoints, nPoints);
    if(DEBUG_OUTPUT) MWC.Utilities.Errors.Trace.trace("drawPolyline");
  }

  @Override
  public void drawPolyline(int[] points)
  {
      // convert to normal format
      int[] xP = new int[points.length];
      int[] yP = new int[points.length];
      int len = points.length;
      
      for (int i = 0; i < points.length; i+= 2)
      {
          xP[i] = points[i];
          yP[i] = points[i+1];
      }
      drawPolyline(xP, yP, len);        
  }

  public boolean drawImage(Image img,
                                  int x,
                                  int y,
                                  int width,
                                  int height,
                                  ImageObserver observer)
  {
    if(DEBUG_OUTPUT) MWC.Utilities.Errors.Trace.trace("draw image");
    return g.drawImage(img, x, y, width, height, observer);
  }

  public void drawLine(int x1, int y1, int x2, int y2)
  {
    g.drawLine(x1, y1, x2, y2);
    if(DEBUG_OUTPUT) MWC.Utilities.Errors.Trace.trace("drawLine, x:" + x1 + " y:" + y1 + " wid:" + x2 + " ht:" + y2);
  }

  public void fillArc(int x,int y,int width,int height,int startAngle,int arcAngle)
  {
    g.fillArc(x, y, width, height, startAngle, arcAngle);
    if(DEBUG_OUTPUT) MWC.Utilities.Errors.Trace.trace("fillArc");
  }

  public void drawArc(int x,int y,int width,int height,int startAngle,int arcAngle)
  {
    g.drawArc(x, y, width, height, startAngle, arcAngle);
    if(DEBUG_OUTPUT) MWC.Utilities.Errors.Trace.trace("drawArc");
  }

  public void drawRect(int x1, int y1, int wid, int height)
  {
    g.drawRect(x1, y1, wid, height);
    if(DEBUG_OUTPUT) MWC.Utilities.Errors.Trace.trace("drawRect");
  }

  public void fillRect(int x, int y, int wid, int height)
  {
    g.fillRect(x, y, wid, height);
    if(DEBUG_OUTPUT) MWC.Utilities.Errors.Trace.trace("fillRect");
  }

  public int getStringHeight(Font theFont)
  {
    if(DEBUG_OUTPUT) MWC.Utilities.Errors.Trace.trace("getStringHeight");
    FontMetrics fm = null;

    if(theFont != null)
      fm = g.getFontMetrics(theFont);
    else
      fm = g.getFontMetrics();

    int ht = fm.getHeight();
    return ht;
  }

  public int getStringWidth(Font theFont, String theString)
  {
    if(DEBUG_OUTPUT) MWC.Utilities.Errors.Trace.trace("getStringWidth");
    FontMetrics fm = null;

    if(theFont != null)
      fm = g.getFontMetrics(theFont);
    else
      fm = g.getFontMetrics();

    int wid = fm.stringWidth(theString);
    return wid;
  }

  public Graphics getGraphicsTemp()
  {
    if(DEBUG_OUTPUT) MWC.Utilities.Errors.Trace.trace("getGraphicsTemp");
    // return our internal graphics object
    return g;
  }

  public PlainProjection getProjection()
  {
    return _proj;
  }

  public void setProjection(PlainProjection val)
  {
    _proj = val;
  }

  public Point toScreen(MWC.GenericData.WorldLocation val)
  {
    return _proj.toScreen(val);
  }

  public MWC.GenericData.WorldLocation toWorld(Point val)
  {
    return _proj.toWorld(val);
  }

  public void rescale()
  {
//    System.out.println("WARNING - PLOTTING FEATURE NOT IMPLEMENTED (rescale)");
  }

  public Color getBackgroundColor()
  {
    if(DEBUG_OUTPUT) MWC.Utilities.Errors.Trace.trace("getBackgroundColor");
    return g.getBackgroundColor();
  }

  public void setBackgroundColor(Color theColor)
  {
    if(DEBUG_OUTPUT) MWC.Utilities.Errors.Trace.trace("setBackgroundColor");
    g.setBackgroundColor(theColor);

  }

  public void addPainter(CanvasType.PaintListener listener)
  {
//    System.out.println("WARNING - PLOTTING FEATURE NOT IMPLEMENTED (addPainter)");
  }

  public java.util.Enumeration getPainters()
  {
//    System.out.println("WARNING - PLOTTING FEATURE NOT IMPLEMENTED (getPainters)");
    return null;
  }


  public void removePainter(CanvasType.PaintListener listener)
  {
//    System.out.println("WARNING - PLOTTING FEATURE NOT IMPLEMENTED");
  }

  public void setTooltipHandler(CanvasType.TooltipHandler handler)
  {
//    System.out.println("WARNING - PLOTTING FEATURE NOT IMPLEMENTED");
  }

  public Dimension getSize() {
    return null;
  }



  ///////////////////////////////////
  // Graphics2d classes
  //////////////////////////////////
  /**
   * Sets the values of an arbitrary number of preferences for the
   * rendering algorithms.
   * Only values for the rendering hints that are present in the
   * specified <code>Map</code> object are modified.
   * All other preferences not present in the specified
   * object are left unmodified.
   * Hint categories include controls for rendering quality and
   * overall time/quality trade-off in the rendering process.
   * Refer to the <code>RenderingHints</code> class for definitions of
   * some common keys and values.
   * @param hints the rendering hints to be set
   * @see RenderingHints
   */
  public void addRenderingHints(Map hints) {
//    System.out.println("addRenderingHints");
  }

  /**
   * Intersects the current <code>Clip</code> with the interior of the
   * specified <code>Shape</code> and sets the <code>Clip</code> to the
   * resulting intersection.  The specified <code>Shape</code> is
   * transformed with the current <code>Graphics2D</code>
   * <code>Transform</code> before being intersected with the current
   * <code>Clip</code>.  This method is used to make the current
   * <code>Clip</code> smaller.
   * To make the <code>Clip</code> larger, use <code>setClip</code>.
   * The <i>user clip</i> modified by this method is independent of the
   * clipping associated with device bounds and visibility.  If no clip has
   * previously been set, or if the clip has been cleared using
   * {@link Graphics#setClip(Shape) setClip} with a <code>null</code>
   * argument, the specified <code>Shape</code> becomes the new
   * user clip.
   * @param s the <code>Shape</code> to be intersected with the current
   *          <code>Clip</code>.  If <code>s</code> is <code>null</code>,
   *          this method clears the current <code>Clip</code>.
   */
  public void clip(Shape s) {
    g.setClip(s);
  }

  /**
   * Strokes the outline of a <code>Shape</code> using the settings of the
   * current <code>Graphics2D</code> context.  The rendering attributes
   * applied include the <code>Clip</code>, <code>Transform</code>,
   * <code>Paint</code>, <code>Composite</code> and
   * <code>Stroke</code> attributes.
   * @param s the <code>Shape</code> to be rendered
   * @see #setStroke
   * @see #setPaint
   * @see Graphics#setColor
   * @see #transform
   * @see #setTransform
   * @see #clip
   * @see #setClip
   * @see #setComposite
   */
  public void draw(Shape s) {
    PathIterator path = s.getPathIterator(_g2.getTransform());
    float[] coords = new float[6];

    Point lastPoint = null;
    Point newPoint = null;

    while(!path.isDone())
    {
      int type = path.currentSegment(coords);

      switch(type) {
          case PathIterator.SEG_MOVETO:
          {
            lastPoint = new Point((int)coords[0], (int)coords[1]);
            break;
          }
          case PathIterator.SEG_LINETO:
          {
            newPoint = new Point((int)coords[0], (int)coords[1]);
            this.drawLine(lastPoint.x, lastPoint.y, newPoint.x, newPoint.y);
            lastPoint = newPoint;
            break;

          }
          case PathIterator.SEG_QUADTO:
          {
            lastPoint = new Point((int)coords[0], (int)coords[1]);
            newPoint = new Point((int)coords[2], (int)coords[3]);
            this.drawLine(lastPoint.x, lastPoint.y, newPoint.x, newPoint.y);
            break;

          }
          case PathIterator.SEG_CUBICTO:
          {
            break;

          }
          case PathIterator.SEG_CLOSE:
          {
            break;

          }
      }
      path.next();
    }
  }

  /**
   * Renders the text of the specified
   * {@link GlyphVector} using
   * the <code>Graphics2D</code> context's rendering attributes.
   * The rendering attributes applied include the <code>Clip</code>,
   * <code>Transform</code>, <code>Paint</code>, and
   * <code>Composite</code> attributes.  The <code>GlyphVector</code>
   * specifies individual glyphs from a {@link Font}.
   * The <code>GlyphVector</code> can also contain the glyph positions.
   * This is the fastest way to render a set of characters to the
   * screen.
   * @param g the <code>GlyphVector</code> to be rendered
   * @param x position in User Space where the glyphs should
   * be rendered
   *
   * @see Font#createGlyphVector
   * @see GlyphVector
   * @see #setPaint
   * @see Graphics#setColor
   * @see #setTransform
   * @see #setComposite
   * @see #setClip
   */
  public void drawGlyphVector(GlyphVector g, float x, float y) {
//    System.out.println("drawGlyphVector");
  }

  /**
   * Renders a <code>BufferedImage</code> that is
   * filtered with a
   * {@link BufferedImageOp}.
   * The rendering attributes applied include the <code>Clip</code>,
   * <code>Transform</code>
   * and <code>Composite</code> attributes.  This is equivalent to:
   * <pre>
   * img1 = op.filter(img, null);
   * drawImage(img1, new AffineTransform(1f,0f,0f,1f,x,y), null);
   * </pre>
   * @param op the filter to be applied to the image before rendering
   * @param img the <code>BufferedImage</code> to be rendered
   * @param x the location in user space where the upper left
   * corner of the
   * image is rendered
   * @see #transform
   * @see #setTransform
   * @see #setComposite
   * @see #clip
   * @see #setClip
   */
  public void drawImage(BufferedImage img,
                        BufferedImageOp op,
                        int x,
                        int y) {
//    System.out.println("drawImage");
  }

  /**
   * Renders an image, applying a transform from image space into user space
   * before drawing.
   * The transformation from user space into device space is done with
   * the current <code>Transform</code> in the <code>Graphics2D</code>.
   * The specified transformation is applied to the image before the
   * transform attribute in the <code>Graphics2D</code> context is applied.
   * The rendering attributes applied include the <code>Clip</code>,
   * <code>Transform</code>, and <code>Composite</code> attributes.
   * Note that no rendering is done if the specified transform is
   * noninvertible.
   * @param img the <code>Image</code> to be rendered
   * @param xform the transformation from image space into user space
   * @param obs the {@link ImageObserver}
   * to be notified as more of the <code>Image</code>
   * is converted
   * @return <code>true</code> if the <code>Image</code> is
   * fully loaded and completely rendered;
   * <code>false</code> if the <code>Image</code> is still being loaded.
   * @see #transform
   * @see #setTransform
   * @see #setComposite
   * @see #clip
   * @see #setClip
   */
  public boolean drawImage(Image img,
                           AffineTransform xform,
                           ImageObserver obs) {
//    System.out.println("drawImage");
    return false;
  }

  /**
   * Renders a
   * {@link RenderableImage},
   * applying a transform from image space into user space before drawing.
   * The transformation from user space into device space is done with
   * the current <code>Transform</code> in the <code>Graphics2D</code>.
   * The specified transformation is applied to the image before the
   * transform attribute in the <code>Graphics2D</code> context is applied.
   * The rendering attributes applied include the <code>Clip</code>,
   * <code>Transform</code>, and <code>Composite</code> attributes. Note
   * that no rendering is done if the specified transform is
   * noninvertible.
   *<p>
   * Rendering hints set on the <code>Graphics2D</code> object might
   * be used in rendering the <code>RenderableImage</code>.
   * If explicit control is required over specific hints recognized by a
   * specific <code>RenderableImage</code>, or if knowledge of which hints
   * are used is required, then a <code>RenderedImage</code> should be
   * obtained directly from the <code>RenderableImage</code>
   * and rendered using
   *{@link #drawRenderedImage(RenderedImage, AffineTransform) drawRenderedImage}.
   * @param img the image to be rendered
   * @param xform the transformation from image space into user space
   * @see #transform
   * @see #setTransform
   * @see #setComposite
   * @see #clip
   * @see #setClip
   * @see #drawRenderedImage
   */
  public void drawRenderableImage(RenderableImage img,
                                  AffineTransform xform) {
//    System.out.println("drawRenderableImage");
  }

  /**
   * Renders a {@link RenderedImage},
   * applying a transform from image
   * space into user space before drawing.
   * The transformation from user space into device space is done with
   * the current <code>Transform</code> in the <code>Graphics2D</code>.
   * The specified transformation is applied to the image before the
   * transform attribute in the <code>Graphics2D</code> context is applied.
   * The rendering attributes applied include the <code>Clip</code>,
   * <code>Transform</code>, and <code>Composite</code> attributes. Note
   * that no rendering is done if the specified transform is
   * noninvertible.
   * @param img the image to be rendered
   * @param xform the transformation from image space into user space
   * @see #transform
   * @see #setTransform
   * @see #setComposite
   * @see #clip
   * @see #setClip
   */
  public void drawRenderedImage(RenderedImage img,
                                AffineTransform xform) {
//    System.out.println("drawRenderedImage");
  }

  /**
   * Renders the text of the specified iterator, using the
   * <code>Graphics2D</code> context's current <code>Paint</code>. The
   * iterator must specify a font
   * for each character. The baseline of the
   * first character is at position (<i>x</i>,&nbsp;<i>y</i>) in the
   * User Space.
   * The rendering attributes applied include the <code>Clip</code>,
   * <code>Transform</code>, <code>Paint</code>, and
   * <code>Composite</code> attributes.
   * For characters in script systems such as Hebrew and Arabic,
   * the glyphs can be rendered from right to left, in which case the
   * coordinate supplied is the location of the leftmost character
   * on the baseline.
   * @param iterator the iterator whose text is to be rendered
   * @param x the coordinates where the iterator's text is to be
   * rendered
   * @see #setPaint
   * @see Graphics#setColor
   * @see #setTransform
   * @see #setComposite
   * @see #setClip
   */
  public void drawString(AttributedCharacterIterator iterator,
                         float x, float y) {
//    System.out.println("drawString att2");
  }

  /**
   * Renders the text of the specified iterator, using the
   * <code>Graphics2D</code> context's current <code>Paint</code>. The
   * iterator has to specify a font
   * for each character. The baseline of the
   * first character is at position (<i>x</i>,&nbsp;<i>y</i>) in the
   * User Space.
   * The rendering attributes applied include the <code>Clip</code>,
   * <code>Transform</code>, <code>Paint</code>, and
   * <code>Composite</code> attributes.
   * For characters in script systems such as Hebrew and Arabic,
   * the glyphs can be rendered from right to left, in which case the
   * coordinate supplied is the location of the leftmost character
   * on the baseline.
   * @param iterator the iterator whose text is to be rendered
   * @param x the coordinates where the iterator's text is to be
   * rendered
   * @see #setPaint
   * @see Graphics#setColor
   * @see #setTransform
   * @see #setComposite
   * @see #setClip
   */
  public void drawString(AttributedCharacterIterator iterator,
                         int x, int y) {
//    System.out.println("drawString att");
  }

  /**
   * Renders the text specified by the specified <code>String</code>,
   * using the current text attribute state in the <code>Graphics2D</code> context.
   * The baseline of the first character is at position
   * (<i>x</i>,&nbsp;<i>y</i>) in the User Space.
   * The rendering attributes applied include the <code>Clip</code>,
   * <code>Transform</code>, <code>Paint</code>, <code>Font</code> and
   * <code>Composite</code> attributes. For characters in script systems
   * such as Hebrew and Arabic, the glyphs can be rendered from right to
   * left, in which case the coordinate supplied is the location of the
   * leftmost character on the baseline.
   * @param s the <code>String</code> to be rendered
   * @param x the coordinates where the <code>String</code>
   * should be rendered
   * @throws NullPointerException if <code>str</code> is
   *         <code>null</code>
   * @see #setPaint
   * @see Graphics#setColor
   * @see Graphics#setFont
   * @see #setTransform
   * @see #setComposite
   * @see #setClip
   */
  public void drawString(String s, float x, float y) {
    drawText(_g2.getFont(), s, (int)x, (int)y);
  }

  /**
   * Renders the text of the specified <code>String</code>, using the
   * current text attribute state in the <code>Graphics2D</code> context.
   * The baseline of the
   * first character is at position (<i>x</i>,&nbsp;<i>y</i>) in
   * the User Space.
   * The rendering attributes applied include the <code>Clip</code>,
   * <code>Transform</code>, <code>Paint</code>, <code>Font</code> and
   * <code>Composite</code> attributes.  For characters in script
   * systems such as Hebrew and Arabic, the glyphs can be rendered from
   * right to left, in which case the coordinate supplied is the
   * location of the leftmost character on the baseline.
   * @param str the string to be rendered
   * @param x the coordinates where the <code>String</code>
   * should be rendered
   * @throws NullPointerException if <code>str</code> is
   *         <code>null</code>
   * @see         Graphics#drawBytes
   * @see         Graphics#drawChars
   * @since       JDK1.0
   */
  public void drawString(String str, int x, int y) {
    drawText(_g2.getFont(), str, x, y);
  }

  /**
   * Fills the interior of a <code>Shape</code> using the settings of the
   * <code>Graphics2D</code> context. The rendering attributes applied
   * include the <code>Clip</code>, <code>Transform</code>,
   * <code>Paint</code>, and <code>Composite</code>.
   * @param s the <code>Shape</code> to be filled
   * @see #setPaint
   * @see Graphics#setColor
   * @see #transform
   * @see #setTransform
   * @see #setComposite
   * @see #clip
   * @see #setClip
   */
  public void fill(Shape s) {
    // draw a rectangle with these proportions
    if(s instanceof RectangularShape)
    {
      RectangularShape rect = (RectangularShape)s;
      g.fillRect((int)rect.getX(), (int)rect.getY(), (int)rect.getWidth(), (int)rect.getHeight());
    }
//    else
//      System.out.println("fill for this shape not supported");
  }

  /**
   * Returns the background color used for clearing a region.
   * @return the current <code>Graphics2D</code> <code>Color</code>,
   * which defines the background color.
   * @see #setBackground
   */
  public Color getBackground() {
    return _g2.getBackground();
  }

  /**
   * Returns the current <code>Composite</code> in the
   * <code>Graphics2D</code> context.
   * @return the current <code>Graphics2D</code> <code>Composite</code>,
   *              which defines a compositing style.
   * @see #setComposite
   */
  public Composite getComposite() {
    return _g2.getComposite();
  }

  /**
   * Returns the device configuration associated with this
   * <code>Graphics2D</code>.
   * @return the device configuration of this <code>Graphics2D</code>.
   */
  public GraphicsConfiguration getDeviceConfiguration() {
    return _g2.getDeviceConfiguration();
  }

  /**
   * Get the rendering context of the <code>Font</code> within this
   * <code>Graphics2D</code> context.
   * The {@link FontRenderContext}
   * encapsulates application hints such as anti-aliasing and
   * fractional metrics, as well as target device specific information
   * such as dots-per-inch.  This information should be provided by the
   * application when using objects that perform typographical
   * formatting, such as <code>Font</code> and
   * <code>TextLayout</code>.  This information should also be provided
   * by applications that perform their own layout and need accurate
   * measurements of various characteristics of glyphs such as advance
   * and line height when various rendering hints have been applied to
   * the text rendering.
   *
   * @return a reference to an instance of FontRenderContext.
   * @see FontRenderContext
   * @see Font#createGlyphVector
   * @see TextLayout
   * @since     1.2
   */

  public FontRenderContext getFontRenderContext() {
    return _g2.getFontRenderContext();
  }

  /**
   * Returns the current <code>Paint</code> of the
   * <code>Graphics2D</code> context.
   * @return the current <code>Graphics2D</code> <code>Paint</code>,
   * which defines a color or pattern.
   * @see #setPaint
   * @see Graphics#setColor
   */
  public Paint getPaint() {
    return _g2.getPaint();
  }

  /**
   * Returns the value of a single preference for the rendering algorithms.
   * Hint categories include controls for rendering quality and overall
   * time/quality trade-off in the rendering process.  Refer to the
   * <code>RenderingHints</code> class for definitions of some common
   * keys and values.
   * @param hintKey the key corresponding to the hint to get.
   * @return an object representing the value for the specified hint key.
   * Some of the keys and their associated values are defined in the
   * <code>RenderingHints</code> class.
   * @see RenderingHints
   * @see #setRenderingHint(RenderingHints.Key, Object)
   */
  public Object getRenderingHint(RenderingHints.Key hintKey) {
    return _g2.getRenderingHint(hintKey);
  }

  /**
   * Gets the preferences for the rendering algorithms.  Hint categories
   * include controls for rendering quality and overall time/quality
   * trade-off in the rendering process.
   * Returns all of the hint key/value pairs that were ever specified in
   * one operation.  Refer to the
   * <code>RenderingHints</code> class for definitions of some common
   * keys and values.
   * @return a reference to an instance of <code>RenderingHints</code>
   * that contains the current preferences.
   * @see RenderingHints
   * @see #setRenderingHints(Map)
   */
  public RenderingHints getRenderingHints() {
    return _g2.getRenderingHints();
  }

  /**
   * Returns the current <code>Stroke</code> in the
   * <code>Graphics2D</code> context.
   * @return the current <code>Graphics2D</code> <code>Stroke</code>,
   *                 which defines the line style.
   * @see #setStroke
   */
  public Stroke getStroke() {
    return _g2.getStroke();
  }

  /**
   * Returns a copy of the current <code>Transform</code> in the
   * <code>Graphics2D</code> context.
   * @return the current <code>AffineTransform</code> in the
   *             <code>Graphics2D</code> context.
   * @see #transform
   * @see #setTransform
   */
  public AffineTransform getTransform() {
    return _g2.getTransform();
  }

  /**
   * Checks whether or not the specified <code>Shape</code> intersects
   * the specified {@link Rectangle}, which is in device
   * space. If <code>onStroke</code> is false, this method checks
   * whether or not the interior of the specified <code>Shape</code>
   * intersects the specified <code>Rectangle</code>.  If
   * <code>onStroke</code> is <code>true</code>, this method checks
   * whether or not the <code>Stroke</code> of the specified
   * <code>Shape</code> outline intersects the specified
   * <code>Rectangle</code>.
   * The rendering attributes taken into account include the
   * <code>Clip</code>, <code>Transform</code>, and <code>Stroke</code>
   * attributes.
   * @param rect the area in device space to check for a hit
   * @param s the <code>Shape</code> to check for a hit
   * @param onStroke flag used to choose between testing the
   * stroked or the filled shape.  If the flag is <code>true</code>, the
   * <code>Stroke</code> oultine is tested.  If the flag is
   * <code>false</code>, the filled <code>Shape</code> is tested.
   * @return <code>true</code> if there is a hit; <code>false</code>
   * otherwise.
   * @see #setStroke
   * @see #fill
   * @see #draw
   * @see #transform
   * @see #setTransform
   * @see #clip
   * @see #setClip
   */
  public boolean hit(Rectangle rect,
                     Shape s,
                     boolean onStroke) {
//    System.out.println("hit");
    return false;
  }

  /**
   * Concatenates the current <code>Graphics2D</code>
   * <code>Transform</code> with a rotation transform.
   * Subsequent rendering is rotated by the specified radians relative
   * to the previous origin.
   * This is equivalent to calling <code>transform(R)</code>, where R is an
   * <code>AffineTransform</code> represented by the following matrix:
   * <pre>
   *		[   cos(theta)    -sin(theta)    0   ]
   *		[   sin(theta)     cos(theta)    0   ]
   *		[       0              0         1   ]
   * </pre>
   * Rotating with a positive angle theta rotates points on the positive
   * x axis toward the positive y axis.
   * @param theta the angle of rotation in radians
   */
  public void rotate(double theta) {
//    System.out.println("rotate");
  }

  /**
   * Concatenates the current <code>Graphics2D</code>
   * <code>Transform</code> with a translated rotation
   * transform.  Subsequent rendering is transformed by a transform
   * which is constructed by translating to the specified location,
   * rotating by the specified radians, and translating back by the same
   * amount as the original translation.  This is equivalent to the
   * following sequence of calls:
   * <pre>
   *		translate(x, y);
   *		rotate(theta);
   *		translate(-x, -y);
   * </pre>
   * Rotating with a positive angle theta rotates points on the positive
   * x axis toward the positive y axis.
   * @param theta the angle of rotation in radians
   * @param x coordinates of the origin of the rotation
   */
  public void rotate(double theta, double x, double y) {
//    System.out.println("rotate");
  }

  /**
   * Concatenates the current <code>Graphics2D</code>
   * <code>Transform</code> with a scaling transformation
   * Subsequent rendering is resized according to the specified scaling
   * factors relative to the previous scaling.
   * This is equivalent to calling <code>transform(S)</code>, where S is an
   * <code>AffineTransform</code> represented by the following matrix:
   * <pre>
   *		[   sx   0    0   ]
   *		[   0    sy   0   ]
   *		[   0    0    1   ]
   * </pre>
   * @param sx the amount by which X coordinates in subsequent
   * rendering operations are multiplied relative to previous
   * rendering operations.
   * @param sy the amount by which Y coordinates in subsequent
   * rendering operations are multiplied relative to previous
   * rendering operations.
   */
  public void scale(double sx, double sy) {
//    System.out.println("scale");
  }

  /**
   * Sets the background color for the <code>Graphics2D</code> context.
   * The background color is used for clearing a region.
   * When a <code>Graphics2D</code> is constructed for a
   * <code>Component</code>, the background color is
   * inherited from the <code>Component</code>. Setting the background color
   * in the <code>Graphics2D</code> context only affects the subsequent
   * <code>clearRect</code> calls and not the background color of the
   * <code>Component</code>.  To change the background
   * of the <code>Component</code>, use appropriate methods of
   * the <code>Component</code>.
   * @param color the background color that isused in
   * subsequent calls to <code>clearRect</code>
   * @see #getBackground
   * @see Graphics#clearRect
   */
  public void setBackground(Color color) {
//    System.out.println("setBackground");
  }

  /**
   * Sets the <code>Composite</code> for the <code>Graphics2D</code> context.
   * The <code>Composite</code> is used in all drawing methods such as
   * <code>drawImage</code>, <code>drawString</code>, <code>draw</code>,
   * and <code>fill</code>.  It specifies how new pixels are to be combined
   * with the existing pixels on the graphics device during the rendering
   * process.
   * <p>If this <code>Graphics2D</code> context is drawing to a
   * <code>Component</code> on the display screen and the
   * <code>Composite</code> is a custom object rather than an
   * instance of the <code>AlphaComposite</code> class, and if
   * there is a security manager, its <code>checkPermission</code>
   * method is called with an <code>AWTPermission("readDisplayPixels")</code>
   * permission.
   * @throws SecurityException
   *         if a custom <code>Composite</code> object is being
   *         used to render to the screen and a security manager
   *         is set and its <code>checkPermission</code> method
   *         does not allow the operation.
   * @param comp the <code>Composite</code> object to be used for rendering
   * @see Graphics#setXORMode
   * @see Graphics#setPaintMode
   * @see #getComposite
   * @see AlphaComposite
   * @see SecurityManager#checkPermission
   * @see AWTPermission
   */
  public void setComposite(Composite comp) {
//    System.out.println("setComposite");
  }

  /**
   * Sets the <code>Paint</code> attribute for the
   * <code>Graphics2D</code> context.  Calling this method
   * with a <code>null</code> <code>Paint</code> object does
   * not have any effect on the current <code>Paint</code> attribute
   * of this <code>Graphics2D</code>.
   * @param paint the <code>Paint</code> object to be used to generate
   * color during the rendering process, or <code>null</code>
   * @see Graphics#setColor
   * @see #getPaint
   * @see GradientPaint
   * @see TexturePaint
   */
  public void setPaint(Paint paint) {
    if(paint instanceof Color)
    {
      this.setColor((Color)paint);
    }
//    else
//      System.out.println("setPaint");
  }

  /**
   * Sets the value of a single preference for the rendering algorithms.
   * Hint categories include controls for rendering quality and overall
   * time/quality trade-off in the rendering process.  Refer to the
   * <code>RenderingHints</code> class for definitions of some common
   * keys and values.
   * @param hintKey the key of the hint to be set.
   * @param hintValue the value indicating preferences for the specified
   * hint category.
   * @see #getRenderingHint(RenderingHints.Key)
   * @see RenderingHints
   */
  public void setRenderingHint(RenderingHints.Key hintKey, Object hintValue) {
//    System.out.println("setRenderingHint");
  }

  /**
   * Replaces the values of all preferences for the rendering
   * algorithms with the specified <code>hints</code>.
   * The existing values for all rendering hints are discarded and
   * the new set of known hints and values are initialized from the
   * specified {@link Map} object.
   * Hint categories include controls for rendering quality and
   * overall time/quality trade-off in the rendering process.
   * Refer to the <code>RenderingHints</code> class for definitions of
   * some common keys and values.
   * @param hints the rendering hints to be set
   * @see #getRenderingHints
   * @see RenderingHints
   */
  public void setRenderingHints(Map hints) {
//    System.out.println("setRenderingHints");
  }

  /**
   * Sets the <code>Stroke</code> for the <code>Graphics2D</code> context.
   * @param s the <code>Stroke</code> object to be used to stroke a
   * <code>Shape</code> during the rendering process
   * @see BasicStroke
   * @see #getStroke
   */
  public void setStroke(Stroke s) {
    if(s instanceof BasicStroke)
    {
      BasicStroke bs = (BasicStroke)s;
      this.setLineWidth(bs.getLineWidth());

      if (bs.getDashArray() != null)
      {
        this.setLineStyle(CanvasType.SHORT_DASHES);
      }
      else
      {
          this.setLineStyle(CanvasType.SOLID);
      }

    }
//    else
//      System.out.println("setStroke not a BasicStroke");
  }

  /**
   * Overwrites the Transform in the <code>Graphics2D</code> context.
   * WARNING: This method should <b>never</b> be used to apply a new
   * coordinate transform on top of an existing transform because the
   * <code>Graphics2D</code> might already have a transform that is
   * needed for other purposes, such as rendering Swing
   * components or applying a scaling transformation to adjust for the
   * resolution of a printer.
   * <p>To add a coordinate transform, use the
   * <code>transform</code>, <code>rotate</code>, <code>scale</code>,
   * or <code>shear</code> methods.  The <code>setTransform</code>
   * method is intended only for restoring the original
   * <code>Graphics2D</code> transform after rendering, as shown in this
   * example:
   * <pre><blockquote>
   * // Get the current transform
   * AffineTransform saveAT = g2.getTransform();
   * // Perform transformation
   * g2d.transform(...);
   * // Render
   * g2d.draw(...);
   * // Restore original transform
   * g2d.setTransform(saveAT);
   * </blockquote></pre>
   *
   * @param Tx the <code>AffineTransform</code> that was retrieved
   *           from the <code>getTransform</code> method
   * @see #transform
   * @see #getTransform
   * @see AffineTransform
   */
  public void setTransform(AffineTransform Tx) {
//    System.out.println("setTransform");
  }

  /**
   * Concatenates the current <code>Graphics2D</code>
   * <code>Transform</code> with a shearing transform.
   * Subsequent renderings are sheared by the specified
   * multiplier relative to the previous position.
   * This is equivalent to calling <code>transform(SH)</code>, where SH
   * is an <code>AffineTransform</code> represented by the following
   * matrix:
   * <pre>
   *		[   1   shx   0   ]
   *		[  shy   1    0   ]
   *		[   0    0    1   ]
   * </pre>
   * @param shx the multiplier by which coordinates are shifted in
   * the positive X axis direction as a function of their Y coordinate
   * @param shy the multiplier by which coordinates are shifted in
   * the positive Y axis direction as a function of their X coordinate
   */
  public void shear(double shx, double shy) {
//    System.out.println("shear");
  }

  /**
   * Composes an <code>AffineTransform</code> object with the
   * <code>Transform</code> in this <code>Graphics2D</code> according
   * to the rule last-specified-first-applied.  If the current
   * <code>Transform</code> is Cx, the result of composition
   * with Tx is a new <code>Transform</code> Cx'.  Cx' becomes the
   * current <code>Transform</code> for this <code>Graphics2D</code>.
   * Transforming a point p by the updated <code>Transform</code> Cx' is
   * equivalent to first transforming p by Tx and then transforming
   * the result by the original <code>Transform</code> Cx.  In other
   * words, Cx'(p) = Cx(Tx(p)).  A copy of the Tx is made, if necessary,
   * so further modifications to Tx do not affect rendering.
   * @param Tx the <code>AffineTransform</code> object to be composed with
   * the current <code>Transform</code>
   * @see #setTransform
   * @see AffineTransform
   */
  public void transform(AffineTransform Tx) {
//    System.out.println("transform");
  }

  /**
   * Concatenates the current
   * <code>Graphics2D</code> <code>Transform</code>
   * with a translation transform.
   * Subsequent rendering is translated by the specified
   * distance relative to the previous position.
   * This is equivalent to calling transform(T), where T is an
   * <code>AffineTransform</code> represented by the following matrix:
   * <pre>
   *		[   1    0    tx  ]
   *		[   0    1    ty  ]
   *		[   0    0    1   ]
   * </pre>
   * @param tx the distance to translate along the x-axis
   * @param ty the distance to translate along the y-axis
   */
  public void translate(double tx, double ty) {
    // is this a real translation?
    if(tx > 0)
    {
      // yes, we're moving down through the plot
      wmf.setWindowOrg((int)-tx, (int)-ty);
    }
    else
    {
      // no, we're resetting, set the offset to zero
      wmf.setWindowOrg(0,0);
    }
  }


  /**
   * Translates the origin of the <code>Graphics2D</code> context to the
   * point (<i>x</i>,&nbsp;<i>y</i>) in the current coordinate system.
   * Modifies the <code>Graphics2D</code> context so that its new origin
   * corresponds to the point (<i>x</i>,&nbsp;<i>y</i>) in the
   * <code>Graphics2D</code> context's former coordinate system.  All
   * coordinates used in subsequent rendering operations on this graphics
   * context are relative to this new origin.
   * @param  x  the specified coordinates
   * @since   JDK1.0
   */
  public void translate(int x, int y) {
  //  g.translate(x,y);
//    System.err.println("not translate int x: " + x + " y:" + y );
  }

  /**
   * Clears the specified rectangle by filling it with the background
   * color of the current drawing surface. This operation does not
   * use the current paint mode.
   * <p>
   * Beginning with Java&nbsp;1.1, the background color
   * of offscreen images may be system dependent. Applications should
   * use <code>setColor</code> followed by <code>fillRect</code> to
   * ensure that an offscreen image is cleared to a specific color.
   * @param       x the <i>x</i> coordinate of the rectangle to clear.
   * @param       y the <i>y</i> coordinate of the rectangle to clear.
   * @param       width the width of the rectangle to clear.
   * @param       height the height of the rectangle to clear.
   * @see         Graphics#fillRect(int, int, int, int)
   * @see         Graphics#drawRect
   * @see         Graphics#setColor(Color)
   * @see         Graphics#setPaintMode
   * @see         Graphics#setXORMode(Color)
   */
  public void clearRect(int x, int y, int width, int height) {
//    System.out.println("clearRect");
  }

  /**
   * Intersects the current clip with the specified rectangle.
   * The resulting clipping area is the intersection of the current
   * clipping area and the specified rectangle.  If there is no
   * current clipping area, either because the clip has never been
   * set, or the clip has been cleared using <code>setClip(null)</code>,
   * the specified rectangle becomes the new clip.
   * This method sets the user clip, which is independent of the
   * clipping associated with device bounds and window visibility.
   * This method can only be used to make the current clip smaller.
   * To set the current clip larger, use any of the setClip methods.
   * Rendering operations have no effect outside of the clipping area.
   * @param x the x coordinate of the rectangle to intersect the clip with
   * @param y the y coordinate of the rectangle to intersect the clip with
   * @param width the width of the rectangle to intersect the clip with
   * @param height the height of the rectangle to intersect the clip with
   * @see #setClip(int, int, int, int)
   * @see #setClip(Shape)
   */
  public void clipRect(int x, int y, int width, int height) {
//    System.err.println("clipRect");
  }

  /**
   * Copies an area of the component by a distance specified by
   * <code>dx</code> and <code>dy</code>. From the point specified
   * by <code>x</code> and <code>y</code>, this method
   * copies downwards and to the right.  To copy an area of the
   * component to the left or upwards, specify a negative value for
   * <code>dx</code> or <code>dy</code>.
   * If a portion of the source rectangle lies outside the bounds
   * of the component, or is obscured by another window or component,
   * <code>copyArea</code> will be unable to copy the associated
   * pixels. The area that is omitted can be refreshed by calling
   * the component's <code>paint</code> method.
   * @param       x the <i>x</i> coordinate of the source rectangle.
   * @param       y the <i>y</i> coordinate of the source rectangle.
   * @param       width the width of the source rectangle.
   * @param       height the height of the source rectangle.
   * @param       dx the horizontal distance to copy the pixels.
   * @param       dy the vertical distance to copy the pixels.
   */
  public void copyArea(int x, int y, int width, int height,
                       int dx, int dy) {
//    System.out.println("copyArea");
  }

  /**
   * Creates a new <code>Graphics</code> object that is
   * a copy of this <code>Graphics</code> object.
   * @return     a new graphics context that is a copy of
   *                       this graphics context.
   */
  public Graphics create() {
//    System.out.println("create");
    return null;
  }

  /**
   * Disposes of this graphics context and releases
   * any system resources that it is using.
   * A <code>Graphics</code> object cannot be used after
   * <code>dispose</code>has been called.
   * <p>
   * When a Java program runs, a large number of <code>Graphics</code>
   * objects can be created within a short time frame.
   * Although the finalization process of the garbage collector
   * also disposes of the same system resources, it is preferable
   * to manually free the associated resources by calling this
   * method rather than to rely on a finalization process which
   * may not run to completion for a long period of time.
   * <p>
   * Graphics objects which are provided as arguments to the
   * <code>paint</code> and <code>update</code> methods
   * of components are automatically released by the system when
   * those methods return. For efficiency, programmers should
   * call <code>dispose</code> when finished using
   * a <code>Graphics</code> object only if it was created
   * directly from a component or another <code>Graphics</code> object.
   * @see         Graphics#finalize
   * @see         Component#paint
   * @see         Component#update
   * @see         Component#getGraphics
   * @see         Graphics#create
   */
  public void dispose() {
//    System.out.println("dispose");
  }

  /**
   * Draws as much of the specified area of the specified image as is
   * currently available, scaling it on the fly to fit inside the
   * specified area of the destination drawable surface.
   * <p>
   * Transparent pixels are drawn in the specified background color.
   * This operation is equivalent to filling a rectangle of the
   * width and height of the specified image with the given color and then
   * drawing the image on top of it, but possibly more efficient.
   * <p>
   * This method returns immediately in all cases, even if the
   * image area to be drawn has not yet been scaled, dithered, and converted
   * for the current output device.
   * If the current output representation is not yet complete then
   * <code>drawImage</code> returns <code>false</code>. As more of
   * the image becomes available, the process that draws the image notifies
   * the specified image observer.
   * <p>
   * This method always uses the unscaled version of the image
   * to render the scaled rectangle and performs the required
   * scaling on the fly. It does not use a cached, scaled version
   * of the image for this operation. Scaling of the image from source
   * to destination is performed such that the first coordinate
   * of the source rectangle is mapped to the first coordinate of
   * the destination rectangle, and the second source coordinate is
   * mapped to the second destination coordinate. The subimage is
   * scaled and flipped as needed to preserve those mappings.
   * @param       img the specified image to be drawn
   * @param       dx1 the <i>x</i> coordinate of the first corner of the
   *                    destination rectangle.
   * @param       dy1 the <i>y</i> coordinate of the first corner of the
   *                    destination rectangle.
   * @param       dx2 the <i>x</i> coordinate of the second corner of the
   *                    destination rectangle.
   * @param       dy2 the <i>y</i> coordinate of the second corner of the
   *                    destination rectangle.
   * @param       sx1 the <i>x</i> coordinate of the first corner of the
   *                    source rectangle.
   * @param       sy1 the <i>y</i> coordinate of the first corner of the
   *                    source rectangle.
   * @param       sx2 the <i>x</i> coordinate of the second corner of the
   *                    source rectangle.
   * @param       sy2 the <i>y</i> coordinate of the second corner of the
   *                    source rectangle.
   * @param       bgcolor the background color to paint under the
   *                    non-opaque portions of the image.
   * @param       observer object to be notified as more of the image is
   *                    scaled and converted.
   * @return   <code>true</code> if the current output representation
   *           is complete; <code>false</code> otherwise.
   * @see         Image
   * @see         ImageObserver
   * @see         ImageObserver#imageUpdate(Image, int, int, int, int, int)
   * @since       JDK1.1
   */
  public boolean drawImage(Image img,
                           int dx1, int dy1, int dx2, int dy2,
                           int sx1, int sy1, int sx2, int sy2,
                           Color bgcolor,
                           ImageObserver observer) {
//    System.out.println("drawImage");
    return false;
  }

  /**
   * Draws as much of the specified area of the specified image as is
   * currently available, scaling it on the fly to fit inside the
   * specified area of the destination drawable surface. Transparent pixels
   * do not affect whatever pixels are already there.
   * <p>
   * This method returns immediately in all cases, even if the
   * image area to be drawn has not yet been scaled, dithered, and converted
   * for the current output device.
   * If the current output representation is not yet complete then
   * <code>drawImage</code> returns <code>false</code>. As more of
   * the image becomes available, the process that draws the image notifies
   * the specified image observer.
   * <p>
   * This method always uses the unscaled version of the image
   * to render the scaled rectangle and performs the required
   * scaling on the fly. It does not use a cached, scaled version
   * of the image for this operation. Scaling of the image from source
   * to destination is performed such that the first coordinate
   * of the source rectangle is mapped to the first coordinate of
   * the destination rectangle, and the second source coordinate is
   * mapped to the second destination coordinate. The subimage is
   * scaled and flipped as needed to preserve those mappings.
   * @param       img the specified image to be drawn
   * @param       dx1 the <i>x</i> coordinate of the first corner of the
   *                    destination rectangle.
   * @param       dy1 the <i>y</i> coordinate of the first corner of the
   *                    destination rectangle.
   * @param       dx2 the <i>x</i> coordinate of the second corner of the
   *                    destination rectangle.
   * @param       dy2 the <i>y</i> coordinate of the second corner of the
   *                    destination rectangle.
   * @param       sx1 the <i>x</i> coordinate of the first corner of the
   *                    source rectangle.
   * @param       sy1 the <i>y</i> coordinate of the first corner of the
   *                    source rectangle.
   * @param       sx2 the <i>x</i> coordinate of the second corner of the
   *                    source rectangle.
   * @param       sy2 the <i>y</i> coordinate of the second corner of the
   *                    source rectangle.
   * @param       observer object to be notified as more of the image is
   *                    scaled and converted.
   * @return   <code>true</code> if the current output representation
   *           is complete; <code>false</code> otherwise.
   * @see         Image
   * @see         ImageObserver
   * @see         ImageObserver#imageUpdate(Image, int, int, int, int, int)
   * @since       JDK1.1
   */
  public boolean drawImage(Image img,
                           int dx1, int dy1, int dx2, int dy2,
                           int sx1, int sy1, int sx2, int sy2,
                           ImageObserver observer) {
//    System.out.println("drawImage");
    return false;
  }

  /**
   * Draws as much of the specified image as is currently available.
   * The image is drawn with its top-left corner at
   * (<i>x</i>,&nbsp;<i>y</i>) in this graphics context's coordinate
   * space.  Transparent pixels are drawn in the specified
   * background color.
   * <p>
   * This operation is equivalent to filling a rectangle of the
   * width and height of the specified image with the given color and then
   * drawing the image on top of it, but possibly more efficient.
   * <p>
   * This method returns immediately in all cases, even if the
   * complete image has not yet been loaded, and it has not been dithered
   * and converted for the current output device.
   * <p>
   * If the image has not yet been completely loaded, then
   * <code>drawImage</code> returns <code>false</code>. As more of
   * the image becomes available, the process that draws the image notifies
   * the specified image observer.
   * @param    img    the specified image to be drawn.
   * @param    x      the <i>x</i> coordinate.
   * @param    y      the <i>y</i> coordinate.
   * @param    bgcolor the background color to paint under the
   *                         non-opaque portions of the image.
   * @param    observer    object to be notified as more of
   *                          the image is converted.
   * @return   <code>true</code> if the image is completely loaded;
   *           <code>false</code> otherwise.
   * @see      Image
   * @see      ImageObserver
   * @see      ImageObserver#imageUpdate(Image, int, int, int, int, int)
   */
  public boolean drawImage(Image img, int x, int y,
                           Color bgcolor,
                           ImageObserver observer) {
//    System.out.println("drawImage");
    return false;
  }

  /**
   * Draws as much of the specified image as is currently available.
   * The image is drawn with its top-left corner at
   * (<i>x</i>,&nbsp;<i>y</i>) in this graphics context's coordinate
   * space. Transparent pixels in the image do not affect whatever
   * pixels are already there.
   * <p>
   * This method returns immediately in all cases, even if the
   * complete image has not yet been loaded, and it has not been dithered
   * and converted for the current output device.
   * <p>
   * If the image has not yet been completely loaded, then
   * <code>drawImage</code> returns <code>false</code>. As more of
   * the image becomes available, the process that draws the image notifies
   * the specified image observer.
   * @param    img the specified image to be drawn.
   * @param    x   the <i>x</i> coordinate.
   * @param    y   the <i>y</i> coordinate.
   * @param    observer    object to be notified as more of
   *                          the image is converted.
   * @return   <code>true</code> if the image is completely loaded;
   *           <code>false</code> otherwise.
   * @see      Image
   * @see      ImageObserver
   * @see      ImageObserver#imageUpdate(Image, int, int, int, int, int)
   */
  public boolean drawImage(Image img, int x, int y,
                           ImageObserver observer) {
//    System.out.println("drawImage");
    return false;
  }

  /**
   * Draws as much of the specified image as has already been scaled
   * to fit inside the specified rectangle.
   * <p>
   * The image is drawn inside the specified rectangle of this
   * graphics context's coordinate space, and is scaled if
   * necessary. Transparent pixels are drawn in the specified
   * background color.
   * This operation is equivalent to filling a rectangle of the
   * width and height of the specified image with the given color and then
   * drawing the image on top of it, but possibly more efficient.
   * <p>
   * This method returns immediately in all cases, even if the
   * entire image has not yet been scaled, dithered, and converted
   * for the current output device.
   * If the current output representation is not yet complete then
   * <code>drawImage</code> returns <code>false</code>. As more of
   * the image becomes available, the process that draws the image notifies
   * the specified image observer.
   * <p>
   * A scaled version of an image will not necessarily be
   * available immediately just because an unscaled version of the
   * image has been constructed for this output device.  Each size of
   * the image may be cached separately and generated from the original
   * data in a separate image production sequence.
   * @param    img       the specified image to be drawn.
   * @param    x         the <i>x</i> coordinate.
   * @param    y         the <i>y</i> coordinate.
   * @param    width     the width of the rectangle.
   * @param    height    the height of the rectangle.
   * @param    bgcolor   the background color to paint under the
   *                         non-opaque portions of the image.
   * @param    observer    object to be notified as more of
   *                          the image is converted.
   * @return   <code>true</code> if the current output representation
   *           is complete; <code>false</code> otherwise.
   * @see      Image
   * @see      ImageObserver
   * @see      ImageObserver#imageUpdate(Image, int, int, int, int, int)
   */
  public boolean drawImage(Image img, int x, int y,
                           int width, int height,
                           Color bgcolor,
                           ImageObserver observer) {
//    System.out.println("drawImage");
    return false;
  }

  /**
   * Draws an outlined round-cornered rectangle using this graphics
   * context's current color. The left and right edges of the rectangle
   * are at <code>x</code> and <code>x&nbsp;+&nbsp;width</code>,
   * respectively. The top and bottom edges of the rectangle are at
   * <code>y</code> and <code>y&nbsp;+&nbsp;height</code>.
   * @param      x the <i>x</i> coordinate of the rectangle to be drawn.
   * @param      y the <i>y</i> coordinate of the rectangle to be drawn.
   * @param      width the width of the rectangle to be drawn.
   * @param      height the height of the rectangle to be drawn.
   * @param      arcWidth the horizontal diameter of the arc
   *                    at the four corners.
   * @param      arcHeight the vertical diameter of the arc
   *                    at the four corners.
   * @see        Graphics#fillRoundRect
   */
  public void drawRoundRect(int x, int y, int width, int height,
                            int arcWidth, int arcHeight) {
//    System.out.println("drawRoundRect");
  }

  /**
   * Fills the specified rounded corner rectangle with the current color.
   * The left and right edges of the rectangle
   * are at <code>x</code> and <code>x&nbsp;+&nbsp;width&nbsp;-&nbsp;1</code>,
   * respectively. The top and bottom edges of the rectangle are at
   * <code>y</code> and <code>y&nbsp;+&nbsp;height&nbsp;-&nbsp;1</code>.
   * @param       x the <i>x</i> coordinate of the rectangle to be filled.
   * @param       y the <i>y</i> coordinate of the rectangle to be filled.
   * @param       width the width of the rectangle to be filled.
   * @param       height the height of the rectangle to be filled.
   * @param       arcWidth the horizontal diameter
   *                     of the arc at the four corners.
   * @param       arcHeight the vertical diameter
   *                     of the arc at the four corners.
   * @see         Graphics#drawRoundRect
   */
  public void fillRoundRect(int x, int y, int width, int height,
                            int arcWidth, int arcHeight) {
//    System.out.println("fillRoundRect");
  }

  /**
   * Gets the current clipping area.
   * This method returns the user clip, which is independent of the
   * clipping associated with device bounds and window visibility.
   * If no clip has previously been set, or if the clip has been
   * cleared using <code>setClip(null)</code>, this method returns
   * <code>null</code>.
   * @return      a <code>Shape</code> object representing the
   *              current clipping area, or <code>null</code> if
   *              no clip is set.
   * @see         Graphics#getClipBounds
   * @see         Graphics#clipRect
   * @see         Graphics#setClip(int, int, int, int)
   * @see         Graphics#setClip(Shape)
   * @since       JDK1.1
   */
  public Shape getClip() {
    return _g2.getClip();
  }

  /**
   * Returns the bounding rectangle of the current clipping area.
   * This method refers to the user clip, which is independent of the
   * clipping associated with device bounds and window visibility.
   * If no clip has previously been set, or if the clip has been
   * cleared using <code>setClip(null)</code>, this method returns
   * <code>null</code>.
   * The coordinates in the rectangle are relative to the coordinate
   * system origin of this graphics context.
   * @return      the bounding rectangle of the current clipping area,
   *              or <code>null</code> if no clip is set.
   * @see         Graphics#getClip
   * @see         Graphics#clipRect
   * @see         Graphics#setClip(int, int, int, int)
   * @see         Graphics#setClip(Shape)
   * @since       JDK1.1
   */
  public Rectangle getClipBounds() {
    return _g2.getClipBounds();
  }

  /**
   * Gets this graphics context's current color.
   * @return    this graphics context's current color.
   * @see       Color
   * @see       Graphics#setColor(Color)
   */
  public Color getColor() {
    return _g2.getColor();
  }

  /**
   * Gets the current font.
   * @return    this graphics context's current font.
   * @see       Font
   * @see       Graphics#setFont(Font)
   */
  public Font getFont() {
    return _g2.getFont();
  }

  /**
   * Gets the font metrics for the specified font.
   * @return    the font metrics for the specified font.
   * @param     f the specified font
   * @see       Graphics#getFont
   * @see       FontMetrics
   * @see       Graphics#getFontMetrics()
   */
  public FontMetrics getFontMetrics(Font f) {
    return _g2.getFontMetrics();
  }

  /**
   * Sets the current clipping area to an arbitrary clip shape.
   * Not all objects that implement the <code>Shape</code>
   * interface can be used to set the clip.  The only
   * <code>Shape</code> objects that are guaranteed to be
   * supported are <code>Shape</code> objects that are
   * obtained via the <code>getClip</code> method and via
   * <code>Rectangle</code> objects.  This method sets the
   * user clip, which is independent of the clipping associated
   * with device bounds and window visibility.
   * @param clip the <code>Shape</code> to use to set the clip
   * @see         Graphics#getClip()
   * @see         Graphics#clipRect
   * @see         Graphics#setClip(int, int, int, int)
   * @since       JDK1.1
   */
  public void setClip(Shape clip) {
    g.setClip(clip);
  }

  /**
   * Sets the current clip to the rectangle specified by the given
   * coordinates.  This method sets the user clip, which is
   * independent of the clipping associated with device bounds
   * and window visibility.
   * Rendering operations have no effect outside of the clipping area.
   * @param       x the <i>x</i> coordinate of the new clip rectangle.
   * @param       y the <i>y</i> coordinate of the new clip rectangle.
   * @param       width the width of the new clip rectangle.
   * @param       height the height of the new clip rectangle.
   * @see         Graphics#clipRect
   * @see         Graphics#setClip(Shape)
   * @see	    Graphics#getClip
   * @since       JDK1.1
   */
  public void setClip(int x, int y, int width, int height) {
    g.setClip(x, y, width, height);
  }

  /**
   * Sets this graphics context's font to the specified font.
   * All subsequent text operations using this graphics context
   * use this font.
   * @param  font   the font.
   * @see     Graphics#getFont
   * @see     Graphics#drawString(String, int, int)
   * @see     Graphics#drawBytes(byte[], int, int, int, int)
   * @see     Graphics#drawChars(char[], int, int, int, int)
   */
  public void setFont(Font font) {
    _g2.setFont(font);
  }

  /**
   * Sets the paint mode of this graphics context to overwrite the
   * destination with this graphics context's current color.
   * This sets the logical pixel operation function to the paint or
   * overwrite mode.  All subsequent rendering operations will
   * overwrite the destination with the current color.
   */
  public void setPaintMode() {
//    System.out.println("setPaintMode");
  }

  /**
   * Sets the paint mode of this graphics context to alternate between
   * this graphics context's current color and the new specified color.
   * This specifies that logical pixel operations are performed in the
   * XOR mode, which alternates pixels between the current color and
   * a specified XOR color.
   * <p>
   * When drawing operations are performed, pixels which are the
   * current color are changed to the specified color, and vice versa.
   * <p>
   * Pixels that are of colors other than those two colors are changed
   * in an unpredictable but reversible manner; if the same figure is
   * drawn twice, then all pixels are restored to their original values.
   * @param     c1 the XOR alternation color
   */
  public void setXORMode(Color c1) {
//    System.out.println("setXORMode");
  }


  public void drawDirectedText(String txt, int direction, int x, int y)
  {
    g.drawDirectedText(getFont(), direction, txt, x, y);
  }

  //////////////////////////////////////////////////
  // test this class
  //////////////////////////////////////////////////
  public static void main(String[] args) {
    javax.swing.JFrame f2 = new javax.swing.JFrame("here");
    f2.setSize(200,200);
    f2.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
    f2.getContentPane().add(new javax.swing.JButton("here"));
    f2.setVisible(true);

    PlainProjection proj = new MWC.Algorithms.Projections.FlatProjection();
    proj.setScreenArea(new Dimension(300, 300));

    Graphics gr = f2.getContentPane().getGraphics();
    MetafileCanvasGraphics2d m2 = new MetafileCanvasGraphics2d("c:\\",(Graphics2D)gr );

    m2.setProjection(proj);

    m2.startDraw(null);

    int original = m2.g.getFontEscapement();

    m2.drawLine(20, 50, 100, 300);
    m2.drawText("hree:" + m2.g.getFontEscapement() + " " + new java.util.Date(), 150, 50);

    m2.g.setFontEscapement(800);
    m2.drawText("hree:" + m2.g.getFontEscapement() + " " + new java.util.Date(), 50, 50);

    m2.g.setFontEscapement(1800);
    m2.drawText("hree:" + m2.g.getFontEscapement() + " " + new java.util.Date(), 50, 150);

    m2.drawDirectedText("down", 900, 80, 80);

    m2.g.setFontEscapement(original);
    com.jrefinery.ui.RefineryUtilities.drawRotatedString("refinery:" + m2.g.getFontEscapement(),
                                                         m2, 130, 120, Math.PI/2);

    m2.endDraw(null);

  }
  
  /** provide the last filename we wrote to
   * 
   * @return the filename
   */
  public static String getLastFileName()
  {
  	return _outputFileName;
  }

  /** accessor to get the last screen size plotted
   * 
   * @return
   */
  public static Dimension getLastScreenSize()
  {
  	return _lastPlotSize;
  }
  
  //////////////////////////////////////////////////
  // class to let text rotation work under our metafiles
  //////////////////////////////////////////////////
  public static class ModifiedRefineryUtilities extends RefineryUtilities
  {
    /**
     * A utility method for drawing rotated text.
     * <P>
     * A common rotation is -Math.PI/2 which draws text 'vertically' (with the top of the
     * characters on the left).
     *
     * @param text  the text.
     * @param g2  the graphics device.
     * @param x  the x-coordinate.
     * @param y  the y-coordinate.
     * @param rotation  the clockwise rotation (in radians).
     */
    public static void drawRotatedString(String text, Graphics2D g2,
                                         float x, float y, double rotation) {

      if ((text == null) || (text.equals(""))) {
        return;
      }

      AffineTransform saved = g2.getTransform();

      // apply the rotation...
      AffineTransform rotate = AffineTransform.getRotateInstance(rotation, x, y);
      g2.transform(rotate);

      // just check if we're actually drawing to a metafile, in which case we've
      // got to do the rotation ourselves
      int oldDirection = 0;
      if(g2 instanceof MetafileCanvasGraphics2d)
      {
        MetafileCanvasGraphics2d m2 = (MetafileCanvasGraphics2d)g2;
        oldDirection = m2.g.getFontEscapement();
        m2.g.setFontEscapement(10 * (180 + (int)(rotation * 180 / Math.PI)));
      }

      // replaces this code...
      g2.drawString(text, x, y);

      g2.setTransform(saved);

      //restore the direction, if we have to
      if(g2 instanceof MetafileCanvasGraphics2d)
      {
        MetafileCanvasGraphics2d m2 = (MetafileCanvasGraphics2d)g2;
        m2.g.setFontEscapement(oldDirection);
      }

    }

  }
}

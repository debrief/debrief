package Debrief.Wrappers.DynamicTrackShapes;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

/**
 * A simple demo that draws a full donut and a section of a donut
 * on two canvasses.
 */
public class PlotDonutFrame extends Frame {

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static void main(String[] ignore) {
    final PlotDonutFrame frame =
      new PlotDonutFrame("Plot Donut",
                         makeDonutSectionArea(50, 100, 0, 360),
                         makeDonutSectionArea(50, 100, -45, 45));
    frame.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          frame.setVisible(false);
          System.exit(0);
        }
      });
    frame.pack();
    frame.setLocationRelativeTo(null);// center on screen
    frame.setVisible(true);
  }
  /**
   * A simple canvas that draws a single area with a border color and fill.
   * The origin is the center of the canvas.
   * The area is drawn scaled to fit the canvas.
   */
  private static class AreaCanvas extends Canvas {
    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		Area area;
    Color areaBorderColor = Color.decode("#95b770");
    Paint areaFillPaint = Color.decode("#cce9ad");
    public void setArea(Area a) { this.area = a; }
//    public Area getArea() { return this.area; }
//    public Color getAreaBorderColor() { return this.areaBorderColor; }
//    public void setAreaBorerColor(Color c) { this.areaBorderColor = c; }
//    public Paint getAreaFillPaint() { return this.areaFillPaint; }
//    public void setAreaFillPaint(Paint p) { this.areaFillPaint = p; }
    public void paint(Graphics g) {
      super.paint(g);
      Graphics2D g2 = (Graphics2D) g;

      // translate origin to center
      g2.translate(this.getWidth()/2, this.getHeight()/2);

      // scale to 1.1 * max (height or width) distance from origin
      Rectangle areaBounds = this.area.getBounds();
      double x = areaBounds.getX(), y = areaBounds.getY();
      double w = areaBounds.getWidth(), h = areaBounds.getHeight();
      double r = Math.max(Math.max(Math.abs(x), Math.abs(x + w)),
                          Math.max(Math.abs(y), Math.abs(y + h)));
      double scale = Math.min(this.getWidth()/2, this.getHeight()/2)/(1.1*r);
      g2.scale(scale, scale);

      g2.setPaint(this.areaFillPaint);
      g2.fill(area);
      g2.setColor(this.areaBorderColor);
      g2.draw(area);
    }
  }

  /**
   * Create a PlotDonutFrame with the given title and 
   * areas to be displayed.  The areas may be created by 
   * {@link #makeDonutSectionArea}.  Each area is drawn in a separate canvas.
   * The layout is FlowLayout. 
   */
  PlotDonutFrame(String title, Area... areas) {
    super(title);
    setLayout(new FlowLayout());
    for (Area area : areas) {
      AreaCanvas canvas = new AreaCanvas();
      canvas.setArea(area); // what to draw
      canvas.setPreferredSize(new Dimension(200, 200));
      add(canvas);
    }
  }
  /**
   * Create a donut or donut section.  A full donut results if the
   * start and end mod 360 are within 0.1 degree, so don't call this 
   * if the difference between minAngle and maxAngle should be 0.
   *
   * @param innerRadius of donut section
   * @param outerRadius of donut section
   * @param minAngle compass angle of section start
   * @param maxAngle compass angle of section end
   */
  public static Area makeDonutSectionArea(double innerRadius,
                                          double outerRadius,
                                          double minAngle,
                                          double maxAngle) {
    double rO = outerRadius, rI = innerRadius;
    // angles from degrees clockwise from the positive y axis.
    // convert to degress counter-clockwise from positive x axis.
    double aBeg = 90 - maxAngle, aExt = maxAngle - minAngle;
    // x and y are upper left corner of bounding rectangle of full circle
    if (Math.abs(minAngle % 360 - maxAngle % 360) < 0.1) {
      Area outer = new Area(new Ellipse2D.Double(-rO/2, -rO/2, rO, rO));
      Area inner = new Area(new Ellipse2D.Double(-rI/2, -rI/2, rI, rI));
      outer.subtract(inner);
      return outer;
    } else {
      Area outer = new Area(new Arc2D.Double(-rO/2, -rO/2, rO, rO, aBeg, aExt,
                                             Arc2D.PIE));
      Area inner = new Area(new Arc2D.Double(-rI/2, -rI/2, rI, rI, aBeg, aExt,
                                             Arc2D.PIE));
      outer.subtract(inner);
      return outer;
    }
  }



}
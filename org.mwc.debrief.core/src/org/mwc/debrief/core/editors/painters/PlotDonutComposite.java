package org.mwc.debrief.core.editors.painters;

import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.PathIterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * A simple demo that draws a full donut and a section of a donut
 * on two SWT Canvasses.
 */
public class PlotDonutComposite extends Composite {

  public static void main(String[] args) {
    final Display display = new Display();
    final Shell shell = new Shell(display);
    shell.setText("Plot Donut");
    shell.setLayout(new FillLayout());
    @SuppressWarnings("unused")
		PlotDonutComposite composite = 
      new PlotDonutComposite(shell, SWT.EMBEDDED,
                             makeDonutSectionArea(50, 100, 0, 360),
                             makeDonutSectionArea(50, 100, -45, 45));
    shell.pack();

    shell.open();
    while(!shell.isDisposed()) {
      if (!display.readAndDispatch()) display.sleep();
    }
    display.dispose();
  }

  /**
   * A simple canvas that draws a single area with a border color and fill.
   * The origin is the center of the canvas.
   */
  private static class AreaCanvas extends Canvas implements PaintListener {
    Area area;
    RGB areaBorderColor = new RGB(0x95, 0xb7, 0x70);
    RGB areaFillColor = new RGB(0xcc, 0xe9, 0xad);
    AreaCanvas(Composite composite, int style, Area area) {
      super(composite, style);
      this.area = area;
      this.addPaintListener(this);
    }
    public Area getArea() { return this.area; }
    public void setArea(Area a) { this.area = a; }
    public RGB getAreaBorderColor() { return this.areaBorderColor; }
    public void setAreaBorerColor(RGB c) { this.areaBorderColor = c; }
    public RGB getAreaFillColor() { return this.areaFillColor; }
    public void setAreaFillColor(RGB c) { this.areaFillColor = c; }
    public void paintControl(PaintEvent event) {
      final GC gc = event.gc;
      final Device device = gc.getGCData().device;

      // allocate device resources
      Transform transform = new Transform(device);
      Path path = new Path(device);
      Color fillColor = new Color(device, this.areaFillColor);
      Color borderColor = new Color(device, this.areaBorderColor);

      try {
        // transform: translate origin to center
        transform.translate(this.getSize().x/2, this.getSize().y/2);

        // path: add area to device path
        addAreaToPath(path, this.area);
        
        // draw
        gc.setTransform(transform);
        gc.setBackground(fillColor);
        gc.fillPath(path);
        gc.setForeground(borderColor);
        gc.drawPath(path);
      } finally {
        // dispose device resources
        transform.dispose();
        path.dispose();
        fillColor.dispose();
        borderColor.dispose();
      }
    }
  }

  private static Path addAreaToPath(Path path, Area area) {
    float[] vals = new float[6];
    for (PathIterator iter = area.getPathIterator(new AffineTransform());
         !iter.isDone(); iter.next()) {
      int segType = iter.currentSegment(vals);
      switch(segType) {
        case PathIterator.SEG_MOVETO:
          path.moveTo(vals[0], vals[1]);
          break;
        case PathIterator.SEG_LINETO:
          path.lineTo(vals[0], vals[1]);
          break;
        case PathIterator.SEG_QUADTO:
          path.quadTo(vals[0], vals[1], vals[2], vals[3]);
          break;
        case PathIterator.SEG_CUBICTO:
          path.cubicTo(vals[0], vals[1], vals[2], vals[3], vals[4], vals[5]);
          break;
        case PathIterator.SEG_CLOSE:
          path.close();
          break;
        default: throw new AssertionError(segType);
      }
    }
    return path;
  }

  /**
   * Create a PlotDonutComposite with 
   * areas to be displayed.  The areas may be created by 
   * {@link #makeDonutSectionArea}.  Each area is drawn in a separate canvas.
   * The layout is FlowLayout. 
   */
  PlotDonutComposite(Shell shell, int style, Area... areas) {
    super(shell, style);

    setLayout(new RowLayout());
    for (Area area : areas) {
      AreaCanvas canvas = new AreaCanvas(this, style, area);
      canvas.setLayoutData(new RowData(200, 200));
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
    double dO = 2*outerRadius, dI = 2*innerRadius;
    // Angles: From degrees clockwise from the positive y axis,
    // convert to degress counter-clockwise from positive x axis.
    double aBeg = 90 - maxAngle, aExt = maxAngle - minAngle;
    // X and y are upper left corner of bounding rectangle of full circle.
    // Subtract 0.5 so that center is between pixels and drawn width is dO
    // (rather than dO + 1).
    double xO = -dO/2 - 0.5, yO = -dO/2 - 0.5;
    double xI = -dI/2 - 0.5, yI = -dI/2 - 0.5;
    if (Math.abs(minAngle % 360 - maxAngle % 360) < 0.1) {
      Area outer = new Area(new Ellipse2D.Double(xO, yO, dO, dO));
      Area inner = new Area(new Ellipse2D.Double(xI, yI, dI, dI));
      outer.subtract(inner);
      return outer;
    } else {
      Area outer = new Area(new Arc2D.Double(xO, yO, dO, dO, aBeg, aExt,
                                             Arc2D.PIE));
      Area inner = new Area(new Arc2D.Double(xI, yI, dI, dI, aBeg, aExt,
                                             Arc2D.PIE));
      outer.subtract(inner);
      return outer;
    }
  }
}
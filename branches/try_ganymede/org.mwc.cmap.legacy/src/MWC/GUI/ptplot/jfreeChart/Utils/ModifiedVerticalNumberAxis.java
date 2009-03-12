package MWC.GUI.ptplot.jfreeChart.Utils;

import com.jrefinery.chart.VerticalNumberAxis;
import com.jrefinery.chart.Tick;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Line2D;
import java.util.*;

import MWC.GUI.Canvas.MetafileCanvasGraphics2d;

/**
 * Created by IntelliJ IDEA.
 * User: Ian.Mayo
 * Date: Feb 5, 2003
 * Time: 11:02:03 AM
 * To change this template use Options | File Templates.
 */
//////////////////////////////////////////////////
// modified vertical number axis, which handles
// rotations of metafiles
//////////////////////////////////////////////////
public final class ModifiedVerticalNumberAxis extends VerticalNumberAxis
{


    /** The default grid line stroke. */
    public static final Stroke DEFAULT_ORIGIN_STROKE = new BasicStroke(1.5f,
        BasicStroke.CAP_BUTT,
        BasicStroke.JOIN_BEVEL,
        0.0f);

  /**
   * Constructs a vertical number axis, using default attribute values where necessary.
   *
   * @param label  the axis label (null permitted).
   */
  public ModifiedVerticalNumberAxis(final String label) {
    super(label);
  }

  /**
   * Draws the plot on a Java 2D graphics device (such as the screen or a
   * printer).
   *
   * @param g2  the graphics device.
   * @param drawArea  the area within which the chart should be drawn.
   * @param dataArea  the area within which the plot should be drawn (a
   *                  subset of the drawArea).
   */
  public final void draw(final Graphics2D g2, final Rectangle2D drawArea, final Rectangle2D dataArea) {

    if (!visible) {
      return;
    }

    // draw the axis label
    if (this.label == null ? false : !this.label.equals("")) {
      g2.setFont(labelFont);
      g2.setPaint(labelPaint);

      Rectangle2D labelBounds = labelFont.getStringBounds(label, g2.getFontRenderContext());
      if (true) {
        double xx = drawArea.getX() + labelInsets.left + labelBounds.getHeight();
        double yy = dataArea.getY() + dataArea.getHeight() / 2
          + (labelBounds.getWidth() / 2);
        MetafileCanvasGraphics2d.ModifiedRefineryUtilities.drawRotatedString(label, g2,
                                                                             (float) xx, (float) yy, -Math.PI / 2);
      }
      else {
        double xx = drawArea.getX() + labelInsets.left;
        double yy = drawArea.getY() + drawArea.getHeight() / 2
          - labelBounds.getHeight() / 2;
        g2.drawString(label, (float) xx, (float) yy);
      }
    }

    // draw the tick labels and marks and gridlines
    refreshTicks(g2, drawArea, dataArea);
    double xx = dataArea.getX();
    g2.setFont(tickLabelFont);
    
    Iterator<Tick> iterator = ticks.iterator();
    while (iterator.hasNext()) {
      Tick tick = (Tick) iterator.next();
      float yy = (float) translateValueToJava2D(tick.getNumericalValue(), dataArea);
      if (tickLabelsVisible) {
        g2.setPaint(this.tickLabelPaint);
        g2.drawString(tick.getText(), tick.getX(), tick.getY());
      }

      if (tickMarksVisible) {
        g2.setStroke(getTickMarkStroke());
        g2.setPaint(getTickMarkPaint());
        Line2D mark = new Line2D.Double(dataArea.getX() - 2, yy, dataArea.getX() + 2, yy);
        g2.draw(mark);
      }

      if (isGridLinesVisible()) {
        g2.setStroke(getGridStroke());
        g2.setPaint(getGridPaint());
        double thisD;
        thisD = translateJava2DtoValue(yy, dataArea);
        if(Math.abs(thisD) < 1e-6)
          g2.setStroke(DEFAULT_ORIGIN_STROKE);
        Line2D gridline = new Line2D.Double(xx, yy, dataArea.getMaxX(), yy);
        g2.draw(gridline);

      }
    }


  }
}

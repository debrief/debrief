
package org.mwc.debrief.lite.map;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;
import javax.swing.event.MouseInputAdapter;

import org.mwc.debrief.lite.map.RangeBearingTool.RangeBearingMeasure;

/**
 * Draws a line on the parent component (e.g. JMapPane) as the mouse is dragged.
 *
 * @author Ian Mayo = from Michael Bedward's MouseDragRectangle
 *
 */
public class MouseDragLine extends MouseInputAdapter
{

  private static final int FONT_SIZE = 17;
  private static final int FONT_STYLE = Font.BOLD;
  private final JComponent parentComponent;
  private Point startPos;
  private Point endPos;
  private boolean dragged;
  private boolean dragging;
  private Graphics2D graphics;
  private final int MEASURE_X_OFFSET = 30;
  private final int MEASURE_Y_OFFSET = 30;
  private final int MEASURE_X_CENTRE_OFFSET = 0;
  private final int MEASURE_Y_CENTRE_OFFSET = -30;
  private RangeBearingMeasure previousMeasure = null;
  
  private boolean mapRepainted = false;

  /**
   * Creates a new instance to work with the given component.
   *
   * @param component
   *          the component on which the box will be drawn
   */
  public MouseDragLine(final JComponent component)
  {
    parentComponent = component;
    dragged = false;
    dragging = false;
    if ( parentComponent instanceof LiteMapPane )
    {
      ((LiteMapPane) parentComponent).addRepaintListener(
          new ActionListener()
          {
            
            @Override
            public void actionPerformed(ActionEvent e)
            {
              mapRepainted = true;
            }
          }
      );
    }
  }

  private void drawRangeBearingCentred()
  {
    final Font oldFont = graphics.getFont();
    final Font newFont = new Font(oldFont.getName(), FONT_STYLE, FONT_SIZE);
    // Now we put the measure along the line.
    final FontRenderContext renderContext = new FontRenderContext(null, true,
        true);

    final Rectangle2D fontRectangle = newFont.getStringBounds(previousMeasure
        .getShortFormat(), renderContext);
    final int x = (endPos.x + startPos.x) / 2;
    final int y = (endPos.y + startPos.y) / 2;

    final AffineTransform oldTransform = graphics.getTransform();
    graphics.setTransform(AffineTransform.getRotateInstance(Math.toRadians(
        previousMeasure.getPrintBearing()), x, y));
    graphics.setFont(newFont);
    graphics.drawString(previousMeasure.getShortFormat(), (int) (x
        - fontRectangle.getWidth() / 2 - fontRectangle.getX()
        + MEASURE_X_CENTRE_OFFSET), (int) (y - fontRectangle.getHeight() / 2
            - fontRectangle.getY() + MEASURE_Y_CENTRE_OFFSET));
    graphics.setTransform(oldTransform);
    graphics.setFont(oldFont);
  }

  /**
   * Creates and initializes the graphics object if required.
   */
  private void ensureGraphics()
  {
    if (graphics == null)
    {
      graphics = (Graphics2D) parentComponent.getGraphics().create();
      graphics.setColor(Color.WHITE);
      graphics.setXORMode(Color.RED);
      graphics.setStroke(new BasicStroke(3f));
    }
  }

  public void eraseOldDrawing()
  {
    if (previousMeasure != null && !mapRepainted)
    {
      ensureGraphics();
      drawRangeBearingCentred();
      graphics.drawLine(startPos.x, startPos.y, endPos.x, endPos.y);
      previousMeasure = null;
    }
  }

  /**
   * If the line is enabled, draws the line running from the start position to the current mouse
   * position.
   *
   * @param ev
   *          input mouse event
   */
  @Override
  public void mouseDragged(final MouseEvent ev)
  {
    mouseDragged(ev, null);
  }

  public void mouseDragged(final MouseEvent ev,
      final RangeBearingMeasure rangeBearing)
  {
    if (dragging)
    {
      ensureGraphics();

      final Font oldFont = graphics.getFont();
      final Font newFont = new Font(oldFont.getName(), FONT_STYLE, FONT_SIZE);
      graphics.setFont(newFont);
      if (dragged)
      {
        graphics.drawLine(startPos.x, startPos.y, endPos.x, endPos.y);
        if (previousMeasure != null)
        {
          graphics.drawString(previousMeasure.getShortFormat(), endPos.x
              + MEASURE_X_OFFSET, endPos.y + MEASURE_Y_OFFSET);
        }
      }
      previousMeasure = rangeBearing;
      endPos = ev.getPoint();
      graphics.drawLine(startPos.x, startPos.y, endPos.x, endPos.y);
      if (rangeBearing != null)
      {
        graphics.drawString(rangeBearing.getShortFormat(), endPos.x
            + MEASURE_X_OFFSET, endPos.y + MEASURE_Y_OFFSET);
      }

      graphics.setFont(oldFont);
      dragged = true;
      mapRepainted = false;
    }
  }

  /**
   * If the line is enabled, records the start position for subsequent drawing as the mouse is
   * dragged.
   *
   * @param ev
   *          input mouse event
   */
  @Override
  public void mousePressed(final MouseEvent ev)
  {
    if (!dragging)
    {
      ensureGraphics();
      eraseOldDrawing();

      dragging = true;
      startPos = new Point(ev.getPoint());
      endPos = new Point(startPos);
      mapRepainted = false;
    }
  }

  /**
   * If the line is enabled, removes the final line.
   *
   * @param ev
   *          the input mouse event
   */
  @Override
  public void mouseReleased(final MouseEvent ev)
  {
    dragging = false;
    if (dragged)
    {
      ensureGraphics();

      final Font oldFont = graphics.getFont();
      final Font newFont = new Font(oldFont.getName(), FONT_STYLE, FONT_SIZE);
      graphics.setFont(newFont);
      if (previousMeasure != null)
      {
        // Ok , we erase the previous text, to move it to the center.
        graphics.drawString(previousMeasure.getShortFormat(), endPos.x
            + MEASURE_X_OFFSET, endPos.y + MEASURE_Y_OFFSET);

        drawRangeBearingCentred();
      }

      graphics.setFont(oldFont);
      dragged = false;
      graphics.dispose();
      graphics = null;
      mapRepainted = false;
    }
  }
}


package org.mwc.debrief.lite.map;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.event.MouseInputAdapter;

/**
 * Draws a line on the parent component (e.g. JMapPane) as the mouse is dragged.
 *
 * @author Ian Mayo = from Michael Bedward's MouseDragRectangle
 *
 */
public class MouseDragLine extends MouseInputAdapter
{

  private final JComponent parentComponent;
  private Point startPos;
  private Point endPos;
  private boolean dragged;
  private boolean dragging;
  private boolean enabled;
  private Graphics2D graphics;
  private int buttonPressed = -1;

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
    enabled = false;
    dragging = false;
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
    if (enabled && dragging)
    {
      ensureGraphics();
      if (dragged)
      {
        graphics.drawLine(startPos.x, startPos.y, endPos.x, endPos.y);
      }
      endPos = ev.getPoint();
      graphics.drawLine(startPos.x, startPos.y, endPos.x, endPos.y);
      dragged = true;
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
    if (enabled && !dragging)
    {
      dragging = true;
      buttonPressed = ev.getButton();
      startPos = new Point(ev.getPoint());
      endPos = new Point(startPos);
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
    if (buttonPressed == ev.getButton())
    {
      dragging = false;
      if (dragged)
      {
        ensureGraphics();
        graphics.drawLine(startPos.x, startPos.y, endPos.x, endPos.y);
        dragged = false;
        graphics.dispose();
        graphics = null;
      }
    }

  }

  /**
   * Enables or disables the drag box. When enabled, the box is drawn on mouse dragging.
   * 
   * @param state
   *          {@code true} to enable; {@code false} to disable
   */
  public void setEnabled(final boolean state)
  {
    enabled = state;
  }

}

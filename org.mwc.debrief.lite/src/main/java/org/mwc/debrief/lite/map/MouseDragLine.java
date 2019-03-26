
package org.mwc.debrief.lite.map;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.event.MouseInputAdapter;

/**
 * Draws a line on the parent component (e.g. JMapPane) as the mouse 
 * is dragged.
 * 
 * @author Ian Mayo = from Michael Bedward's MouseDragRectangle
 * 
 */
public class MouseDragLine extends MouseInputAdapter {
    
    private final JComponent parentComponent;
    private Point startPos;
    private Point endPos;
    private boolean dragged;
    private boolean enabled;
    private Graphics2D graphics;

    /**
     * Creates a new instance to work with the given component.
     * 
     * @param component the component on which the box will be drawn
     */
    public MouseDragLine(JComponent component) {
        parentComponent = component;
        dragged = false;
        enabled = false;
    }

    /**
     * Enables or disables the drag box. When enabled, the box 
     * is drawn on mouse dragging.
     * 
     * @param state {@code true} to enable; {@code false} to disable
     */
    public void setEnabled(boolean state) {
        enabled = state;
    }

    /**
     * If the line is enabled, records the start position for subsequent
     * drawing as the mouse is dragged.
     * 
     * @param ev input mouse event
     */
    @Override
    public void mousePressed(MouseEvent ev) {
      if(enabled)
      {
        startPos = new Point(ev.getPoint());
        endPos = new Point(startPos);
      }
    }

    /**
     * If the line is enabled, draws the line running from the
     * start position to the current mouse position. 
     * 
     * @param ev input mouse event
     */
    @Override
    public void mouseDragged(MouseEvent ev) {
        if (enabled) {
            ensureGraphics();
            if (dragged) {
                graphics.drawLine(startPos.x, startPos.y, endPos.x, endPos.y);
            }
            endPos = ev.getPoint();
            graphics.drawLine(startPos.x, startPos.y, endPos.x, endPos.y);
            dragged = true;
        }
    }

    /**
     * If the line is enabled, removes the final line.
     * 
     * @param ev the input mouse event
     */
    @Override
    public void mouseReleased(MouseEvent ev) {
        if (dragged) {
            ensureGraphics();
            graphics.drawLine(startPos.x, startPos.y, endPos.x, endPos.y);
            dragged = false;
            graphics.dispose();
            graphics = null;
        }
    }

    /**
     * Creates and initializes the graphics object if required.
     */
    private void ensureGraphics() {
        if (graphics == null) {
            graphics = (Graphics2D) parentComponent.getGraphics().create();
            graphics.setColor(Color.WHITE);
            graphics.setXORMode(Color.RED);
            graphics.setStroke(new BasicStroke(3f));
        }
    }
    
}

/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2018, Deep Blue C Technology Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.debrief.lite.gui.custom;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.SwingConstants;

import org.jdesktop.swingx.JXCollapsiblePane;
import org.jdesktop.swingx.JXLabel;

public class JXCollapsiblePaneWithTitle extends JXCollapsiblePane
{
  /**
   * 
   */
  private static final long serialVersionUID = -2723902372006197600L;

  private final JXLabel titleLabel;

  // Variables used to implement the resize behavior.
  private boolean dragging = false;
  private Point dragLocation = new Point();

  public JXCollapsiblePaneWithTitle(Direction direction, String title)
  {
    super(direction);

    final JXCollapsiblePane collapsiblePaneInstance = this;
    collapsiblePaneInstance.setPreferredSize(new Dimension(400, 400));

    titleLabel = new JXLabel(title, SwingConstants.CENTER);
    setLayout(new BorderLayout());

    if (direction == Direction.LEFT)
    {
      add(titleLabel, BorderLayout.EAST);
    }
    else if (direction == Direction.RIGHT)
    {
      add(titleLabel, BorderLayout.WEST);
    }
    else if (direction == Direction.DOWN)
    {
      add(titleLabel, BorderLayout.NORTH);
    }
    else if (direction == Direction.UP)
    {
      add(titleLabel, BorderLayout.SOUTH);
    }

    if (!direction.isVertical())
    {
      titleLabel.setTextRotation(3 * Math.PI / 2);
    }

    titleLabel.setBackground(Color.black);

    titleLabel.addMouseListener(new MouseAdapter()
    {
      @Override
      public void mouseClicked(MouseEvent e)
      {
        if (e.getClickCount() == 2)
        {
          collapsiblePaneInstance.setCollapsed(!collapsiblePaneInstance
              .isCollapsed());
        }
      }

      @Override
      public void mousePressed(MouseEvent e)
      {
        dragging = true;
        dragLocation = e.getPoint();
      }

      @Override
      public void mouseReleased(MouseEvent e)
      {
        dragging = false;
      }
    });

    titleLabel.addMouseMotionListener(new MouseMotionListener()
    {

      @Override
      public void mouseMoved(MouseEvent event)
      {
        // System.out.println("Mouse is moving over the title");
        /*
         * collapsiblePaneInstance.setSize(collapsiblePaneInstance.getWidth() + 10,
         * collapsiblePaneInstance.getHeight()); collapsiblePaneInstance.doLayout();
         * collapsiblePaneInstance.repaint();
         */
      }

      @Override
      public void mouseDragged(MouseEvent event)
      {
        if (dragging)
        {
          System.out.println("It is dragging");
          System.out.println("dragLocation.getX() = " + dragLocation.getX());
          System.out.println("getWidth() = " + collapsiblePaneInstance.getWidth());
          System.out.println("dragLocation.getY() = " + dragLocation.getY());
          System.out.println("getHeight() = " + collapsiblePaneInstance.getHeight());

          if (dragLocation.getX() > collapsiblePaneInstance.getWidth() - 10 && dragLocation
              .getY() > collapsiblePaneInstance.getHeight() - 10)
          {

            System.out.println("DOING IT");
            collapsiblePaneInstance.setSize((int) (collapsiblePaneInstance
                .getWidth() + (event.getPoint().getX() - dragLocation.getX())),
                (int) (collapsiblePaneInstance.getHeight() + (event.getPoint()
                    .getY() - dragLocation.getY())));
            System.out.println("Set Size(" + (int) (collapsiblePaneInstance
                .getWidth() + (event.getPoint().getX() - dragLocation.getX()))
                + ", " + (int) (collapsiblePaneInstance.getHeight() + (event
                    .getPoint().getY() - dragLocation.getY())) + ")");
            dragLocation = event.getPoint();
          }
        }
      }
    });
  }
}

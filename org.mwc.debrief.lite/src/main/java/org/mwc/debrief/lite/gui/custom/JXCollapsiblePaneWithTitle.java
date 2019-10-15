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
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.SwingConstants;

import org.jdesktop.swingx.JXLabel;

public class JXCollapsiblePaneWithTitle extends JXCollapsiblePane
{
  /**
   *
   */
  private static final long serialVersionUID = -2723902372006197600L;

  private static final Integer MIN_ANIMATION_SIZE = 20;

  private final JXLabel titleLabel;

  // Variables used to implement the resize behavior.
  private boolean dragging = false;
  private Point dragLocation = new Point();

  public JXCollapsiblePaneWithTitle(final Direction direction,
      final String title, final int defaultSize)
  {
    super(direction, MIN_ANIMATION_SIZE, false);

    final JXCollapsiblePane collapsiblePaneInstance = this;
    collapsiblePaneInstance.setPreferredSize(new Dimension(defaultSize,
        defaultSize));
    collapsiblePaneInstance.setName(title);

    titleLabel = new JXLabel(title, SwingConstants.CENTER);
    titleLabel.setName("titleLabel");
    final Dimension titleDimension;

    if (direction.isVertical())
    {
      titleDimension = new Dimension(defaultSize, 20);
    }
    else
    {
      titleDimension = new Dimension(20, defaultSize);
    }
    titleLabel.setPreferredSize(titleDimension);
    setLayout(new BorderLayout());

    if (direction == Direction.LEFT)
    {
      add(titleLabel, BorderLayout.EAST);
      setMinimumSize(new Dimension(30, titleLabel.getHeight()));
    }
    else if (direction == Direction.RIGHT)
    {
      add(titleLabel, BorderLayout.WEST);
    }
    else if (direction == Direction.DOWN)
    {
      add(titleLabel, BorderLayout.NORTH);
      setMinimumSize(new Dimension(titleLabel.getWidth(), 30));
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
      public void mouseClicked(final MouseEvent e)
      {
        if (e.getClickCount() == 2)
        {
          collapsiblePaneInstance.setCollapsed(!collapsiblePaneInstance
              .isCollapsed());
        }
      }

      @Override
      public void mousePressed(final MouseEvent e)
      {
        dragging = true;
        dragLocation = e.getLocationOnScreen();
      }

      @Override
      public void mouseReleased(final MouseEvent e)
      {
        dragging = false;
      }
    });

    titleLabel.addMouseMotionListener(new MouseMotionListener()
    {

      @Override
      public void mouseDragged(final MouseEvent event)
      {
        if (dragging)
        {
          int deltaMultiplier = 1;
          if (direction == Direction.DOWN || direction == Direction.RIGHT)
          {
            deltaMultiplier *= -1;
          }

          int delta;
          if (direction.isVertical())
          {
            final int newDimensionDelta = (int) (event.getLocationOnScreen()
                .getY() - dragLocation.getY());

            delta = newDimensionDelta;
          }
          else
          {
            final int newDimensionDelta = (int) (event.getLocationOnScreen()
                .getX() - dragLocation.getX());

            delta = newDimensionDelta;
          }
          delta *= deltaMultiplier;

          wrapper.getView().setVisible(true);

          int newDimension;
          if (direction.isVertical())
          {
            newDimension = wrapper.getHeight() + delta;
          }
          else
          {
            newDimension = wrapper.getWidth() + delta;
          }
          newDimension = Math.max(newDimension, getMinimunAnimationSize());

          Rectangle bounds = wrapper.getBounds();

          if (direction.isVertical())
          {
            final int oldHeight = bounds.height;
            bounds.height = newDimension;
            wrapper.setBounds(bounds);

            if (direction.getFixedDirection(
                getComponentOrientation()) == Direction.DOWN)
            {
              wrapper.setViewPosition(new Point(0, wrapper.getView()
                  .getPreferredSize().height - newDimension));
            }
            else
            {
              wrapper.setViewPosition(new Point(0, newDimension));
            }

            bounds = getBounds();
            bounds.height = (bounds.height - oldHeight) + newDimension;
            bounds.width = 0;
            currentDimension = bounds.height;
          }
          else
          {
            final int oldWidth = bounds.width;
            bounds.width = newDimension;
            wrapper.setBounds(bounds);

            if (direction.getFixedDirection(
                getComponentOrientation()) == Direction.RIGHT)
            {
              wrapper.setViewPosition(new Point(wrapper.getView()
                  .getPreferredSize().width - newDimension, 0));
            }
            else
            {
              wrapper.setViewPosition(new Point(newDimension, 0));
            }

            bounds = getBounds();
            bounds.width = (bounds.width - oldWidth) + newDimension;
            bounds.height = 0;
            currentDimension = bounds.width;
          }

          collapsiblePaneInstance.collapsed = newDimension == MIN_ANIMATION_SIZE;
          collapsiblePaneInstance.setPreferredSize(new Dimension(bounds.width,
              bounds.height));
          setBounds(bounds);

          validate();

          dragLocation = event.getLocationOnScreen();
        }
      }

      @Override
      public void mouseMoved(final MouseEvent event)
      {
        // Nothing to do here. Added a line to avoid the travis warning
        System.out.print("");
      }
    });

    if (collapsiblePaneInstance.getDirection().isVertical())
    {
      titleLabel.setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
    }
    else
    {
      titleLabel.setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
    }
  }

  @Override
  public String getName()
  {
    return titleLabel.getText();
  }
}

/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2016, Deep Blue C Technology Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package org.mwc.debrief.lite.utils;

/**
 *
 * @author Ayesha
 *
 */
import java.awt.Component;
import java.awt.Container;
import java.awt.Window;

import javax.swing.JMenu;

public class TestUtils
{

  static int counter;

  public static Component getChildIndexed(final Component parent,
      final String klass, final int index)
  {
    counter = 0;

    // Step in only owned windows and ignore its components in JFrame
    if (parent instanceof Window)
    {
      final Component[] children = ((Window) parent).getOwnedWindows();

      for (int i = 0; i < children.length; ++i)
      {
        // take only active windows
        if (children[i] instanceof Window && !((Window) children[i]).isActive())
        {
          continue;
        }

        final Component child = getChildIndexedInternal(children[i], klass,
            index);
        if (child != null)
        {
          return child;
        }
      }
    }

    return null;
  }

  private static Component getChildIndexedInternal(final Component parent,
      final String klass, final int index)
  {

    // Debug line
    // System.out.println("Class: " + parent.getClass() +
    // " Name: " + parent.getName());

    if (parent.getClass().toString().endsWith(klass))
    {
      if (counter == index)
      {
        return parent;
      }
      ++counter;
    }

    if (parent instanceof Container)
    {
      final Component[] children = (parent instanceof JMenu) ? ((JMenu) parent)
          .getMenuComponents() : ((Container) parent).getComponents();

      for (int i = 0; i < children.length; ++i)
      {
        final Component child = getChildIndexedInternal(children[i], klass,
            index);
        if (child != null)
        {
          return child;
        }
      }
    }

    return null;
  }

  public static Component getChildNamed(final Component parent,
      final String name)
  {

    // Debug line
    // System.out.println("Class: " + parent.getClass() +
    // " Name: " + parent.getName());

    if (name.equals(parent.getName()))
    {
      return parent;
    }

    if (parent instanceof Container)
    {
      final Component[] children = (parent instanceof JMenu) ? ((JMenu) parent)
          .getMenuComponents() : ((Container) parent).getComponents();

      for (int i = 0; i < children.length; ++i)
      {
        final Component child = getChildNamed(children[i], name);
        if (child != null)
        {
          return child;
        }
      }
    }

    return null;
  }
}

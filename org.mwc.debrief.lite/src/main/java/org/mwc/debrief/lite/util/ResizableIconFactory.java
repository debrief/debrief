/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *  
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *  
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *******************************************************************************/

package org.mwc.debrief.lite.util;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

import org.pushingpixels.neon.icon.ResizableIcon;

/**
 * @author Ayesha
 *
 */
public class ResizableIconFactory implements ResizableIcon
{
  
  private ResizableIcon delegate;
  
  
  public ResizableIconFactory(ResizableIcon icon)
  {
    delegate = icon;
  }

  @Override
  public void paintIcon(Component c, Graphics g, int x, int y)
  {
    delegate.paintIcon(c,g,x,y);
  }

  @Override
  public int getIconWidth()
  {
    return delegate.getIconWidth();
  }

  @Override
  public int getIconHeight()
  {
    return delegate.getIconHeight();
  }

  @Override
  public void setDimension(Dimension arg0)
  {
    delegate.setDimension(arg0);

  }
  
  public static Factory factory(ResizableIcon icon) {
    return () -> new ResizableIconFactory(icon);
  }

}

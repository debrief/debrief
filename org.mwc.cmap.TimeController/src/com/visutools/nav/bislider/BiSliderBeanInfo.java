
package com.visutools.nav.bislider;

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

public class BiSliderBeanInfo extends java.beans.SimpleBeanInfo {
  // Generated BeanInfo just gives the bean its icons.
  // Small icon is in BiSlider.gif
  // Large icon is in BiSliderL.gif
  // It is expected that the contents of the icon files will be changed to suit your bean.

  public java.awt.Image getIcon(int iconKind) {
    java.awt.Image icon = null;
    switch (iconKind)
    {
      case ICON_COLOR_16x16:
        // The "/" is very important. It doesn't mean the image must
        // be found at the root of the HD but at the root of the
        // classpath entries (the jar of the bean is a kind of root then !)
        icon = loadImage("/images/BiSlider.png");
        break;

      case ICON_COLOR_32x32:
        // The "/" is very important. It doesn't mean the image must
        // be found at the root of the HD but at the root of the
        // classpath entries (the jar of the bean is a kind of root then !)
        icon = loadImage("/images/BiSliderL.png");
        break;

      default:
        icon = loadImage("/images/BiSliderVL.png");
        break;
    }
    return icon;
  }
}

/* BiSliderBeanInfo.java */

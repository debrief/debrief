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

import javax.swing.JComboBox;
import javax.swing.JMenu;

import org.mwc.debrief.lite.DebriefLiteApp;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.common.JCommandMenuButton;
import org.pushingpixels.flamingo.api.common.JScrollablePanel;
import org.pushingpixels.flamingo.api.common.popup.JPopupPanel;
import org.pushingpixels.flamingo.api.ribbon.AbstractRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.RibbonTask;
import org.pushingpixels.flamingo.api.ribbon.synapse.JRibbonComboBox;
import org.pushingpixels.flamingo.internal.ui.ribbon.JBandControlPanel;
import org.pushingpixels.flamingo.internal.ui.ribbon.JRibbonComponent;

public class TestUtils
{

  private static int counter;

  public static Component getChildIndexed(final Component parent,
      final String klass, final int index,boolean owned)
  {
    counter = 0;

    // Step in only owned windows and ignore its components in JFrame
    if (parent instanceof Window)
    {
      final Component[] children;
      if(owned)
      {
        children = ((Window) parent).getOwnedWindows();        
      }
      else {
        children = Window.getWindows();
      }

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
  
  public static RibbonTask getTask(int index)
  {
    return DebriefLiteApp.getInstance().getApplicationFrame().getRibbon().getTask(index);
  }
  
  public static AbstractRibbonBand getRibbonBand(int taskIndex,int bandIndex)
  {
    return DebriefLiteApp.getInstance().getApplicationFrame().getRibbon().getTask(taskIndex).getBand(bandIndex);
  }
  
  public static JComboBox<String> getActiveLayerDropDown()
  {
    JBandControlPanel liteBand = (JBandControlPanel)getRibbonBand(3,1).getComponent(0);
    JRibbonComponent newButton = ((JRibbonComponent)liteBand.getComponent(0));
    @SuppressWarnings("unchecked")
    JRibbonComboBox<String> combo =(JRibbonComboBox<String>) newButton.getComponent(1);
    // getChildNamed(newButton, "select-layer-combo");
    return combo;
  }
  
  public static JCommandButton getSaveButton()
  {
    JBandControlPanel liteBand = (JBandControlPanel)getRibbonBand(1,0).getComponent(0);
    JCommandButton saveButton = ((JCommandButton)liteBand.getComponent(2));
    JPopupPanel panel = (JPopupPanel)saveButton.getPopupCallback().getPopupPanel(saveButton);
    JCommandButton saveDDButton = (JCommandButton)((JScrollablePanel)panel.getComponent(0)).getComponent(1);
    saveDDButton.setFireActionOnRollover(true);
    System.out.println("Save Button:"+saveDDButton);
    return saveDDButton;
  }
  
  public static JCommandButton getSaveASButton()
  {
    JBandControlPanel liteBand = (JBandControlPanel)getRibbonBand(1,0).getComponent(0);
    JCommandButton saveButton = ((JCommandButton)liteBand.getComponent(2));
    JPopupPanel panel = (JPopupPanel)saveButton.getPopupCallback().getPopupPanel(saveButton);
    JCommandButton saveDDButton = (JCommandButton)((JScrollablePanel<JPopupPanel>)panel.getComponent(0)).getComponent(2);
    return saveDDButton;
  }
  public static JCommandMenuButton getSaveAsButton()
  {
    JBandControlPanel liteBand = (JBandControlPanel)getRibbonBand(1,0).getComponent(0);
    JCommandButton saveButton = ((JCommandButton)liteBand.getComponent(2));
    JPopupPanel panel = (JPopupPanel)saveButton.getPopupCallback().getPopupPanel(saveButton);
    JCommandMenuButton saveDDButton = (JCommandMenuButton)TestUtils.getChildNamed(panel, "saveas");
    return saveDDButton;
  }
  
  
}

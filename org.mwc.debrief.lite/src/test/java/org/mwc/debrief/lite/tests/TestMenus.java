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
package org.mwc.debrief.lite.tests;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSlider;

import org.mwc.debrief.lite.DebriefLiteApp;
import org.mwc.debrief.lite.utils.TestUtils;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.common.JCommandToggleButton;
import org.pushingpixels.flamingo.api.ribbon.AbstractRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.JRibbonComponent;
import org.pushingpixels.flamingo.api.ribbon.JRibbonFrame;
import org.pushingpixels.flamingo.api.ribbon.RibbonTask;
import org.pushingpixels.flamingo.internal.ui.ribbon.JBandControlPanel;

/**
 * @author Ayesha
 *
 */
public class TestMenus extends BaseTestCase
{

  public void testLiteMenu() {
    JRibbonFrame ribbonFrame = DebriefLiteApp.getInstance().getApplicationFrame();
    assertEquals(ribbonFrame.getRibbon().getTaskCount(),5);
    assertEquals(ribbonFrame.getRibbon().getTask(0).getTitle(),"Lite");
    RibbonTask liteMenu = ribbonFrame.getRibbon().getTask(0);
    assertTrue(liteMenu.getBandCount()==1);
    doAssertions(liteMenu.getBand(0),"Lite",new String[] {"Help","Exit"},false);
    {
    assertEquals(ribbonFrame.getRibbon().getTask(1).getTitle(),"File");
    RibbonTask fileMenu = ribbonFrame.getRibbon().getTask(1);
    assertTrue(fileMenu.getBandCount()==3);
    doAssertions(fileMenu.getBand(0),"File",new String[] {"New","Open","Save","Close"},false);
    doAssertions(fileMenu.getBand(1),"Import",new String[] {"Replay","Plot","NMEA","TIF"},false);
    doAssertions(fileMenu.getBand(2),"Export",new String[] {"Clipboard"},false);
    }
    {
    assertEquals(ribbonFrame.getRibbon().getTask(2).getTitle(),"View");
    RibbonTask fileMenu = ribbonFrame.getRibbon().getTask(2);
    assertTrue(fileMenu.getBandCount()==3);
    doAssertions(fileMenu.getBand(0),"Mouse mode",new String[] {"Pan","Zoom In","Rng/Brg","Drag Whole Feature","Drag Element"},true);
    doAssertions(fileMenu.getBand(1),"Map commands",new String[] {"Zoom Out","Fit to Window"},false);
    doAssertionTransparencySliderBar(fileMenu.getBand(2),"Background","Transparency:");
    
    }
    {
    assertEquals(ribbonFrame.getRibbon().getTask(3).getTitle(),"Insert");
    RibbonTask fileMenu = ribbonFrame.getRibbon().getTask(3);
    assertTrue(fileMenu.getBandCount()==3);
    doAssertions(fileMenu.getBand(0),"Decorations",new String[] {"Scale","Grid"},false);
    doAssertionActiveLayerCombo(fileMenu.getBand(1));
    doShapesAssertions(fileMenu.getBand(2));
    assertEquals(ribbonFrame.getRibbon().getTask(4).getTitle(),"Time");
    }
    {
      RibbonTask fileMenu = ribbonFrame.getRibbon().getTask(4);
      assertTrue(fileMenu.getBandCount()==3);
      doAssertions(fileMenu.getBand(0),"Display Mode",new String[] {"Normal","Snail"},true);
    }
  }
  
  private void doAssertionActiveLayerCombo(AbstractRibbonBand liteBand1) {
    assertEquals(liteBand1.getTitle(),"Active Layer");
    JBandControlPanel liteBand = (JBandControlPanel)liteBand1.getComponent(0);
    assertTrue(liteBand.getComponent(0) instanceof JRibbonComponent);
    JRibbonComponent newButton = ((JRibbonComponent)liteBand.getComponent(0));
    @SuppressWarnings("unchecked")
    JComboBox<String> combo = (JComboBox<String>)TestUtils.getChildNamed(newButton, "select-layer-combo");
    assertNotNull(combo);
    assertEquals(combo.getItemCount(),1);
    assertEquals(combo.getItemAt(0),"User-selected Layer");
  }
  private void doShapesAssertions(AbstractRibbonBand liteBand1) {
    assertEquals(liteBand1.getTitle(),"Shapes");
    JBandControlPanel liteBand = (JBandControlPanel)liteBand1.getComponent(0);
    assertEquals(7, liteBand.getComponentCount());
    assertTrue(liteBand.getComponent(0) instanceof JCommandButton);
    assertTrue(liteBand.getComponent(2) instanceof JRibbonComponent);
    assertRibbonLabel("Ellipse",(JRibbonComponent)liteBand.getComponent(2));
    assertRibbonLabel("Rectangle",(JRibbonComponent)liteBand.getComponent(3));
    assertRibbonLabel("Circle",(JRibbonComponent)liteBand.getComponent(4));
    assertRibbonLabel("Line",(JRibbonComponent)liteBand.getComponent(5));
    assertRibbonLabel("Arc",(JRibbonComponent)liteBand.getComponent(6));
    JCommandButton labelButton = ((JCommandButton)liteBand.getComponent(0));
    assertTrue(labelButton.getText().equals("Label"));
    assertTrue(liteBand.getComponent(3) instanceof JRibbonComponent);
    }
  
  
  private void assertRibbonLabel(String string, JRibbonComponent component)
  {
    assertTrue(component.getComponent(1) instanceof JCommandButton);
    JCommandButton lbl = (JCommandButton)component.getComponent(1);
    assertEquals(lbl.getText(),string);
    
  }

  private void doAssertionTransparencySliderBar(AbstractRibbonBand liteBand1,String menuName,String itemname) {
    assertEquals(liteBand1.getTitle(),menuName);
    System.out.println("menu name"+menuName);
    JBandControlPanel liteBand = (JBandControlPanel)liteBand1.getComponent(0);
    assertTrue(liteBand.getComponent(0) instanceof JRibbonComponent);
    JRibbonComponent newButton = ((JRibbonComponent)liteBand.getComponent(0));
    assertTrue(newButton.getComponent(0) instanceof JLabel);
    assertEquals(((JLabel)newButton.getComponent(0)).getText(),itemname);
    assertTrue(newButton.getComponent(1) instanceof JSlider);
    JSlider slider = (JSlider)newButton.getComponent(1);
    assertEquals(slider.getMaximum(),100);
    assertEquals(slider.getMinimum(),0);
    assertEquals(slider.getMajorTickSpacing(),20);
  }

  
  private void doAssertions(AbstractRibbonBand liteBand1,String menuName,String[] items,boolean isToggleButton) {
    assertEquals(liteBand1.getTitle(),menuName);
    System.out.println("menu name"+menuName);
    JBandControlPanel liteBand = (JBandControlPanel)liteBand1.getComponent(0);
    assertEquals(liteBand.getComponentCount(),items.length);
    int i=0;
    for(String item:items) {
      if(!item.isEmpty())
      {
        if(isToggleButton) {
          assertTrue(liteBand.getComponent(i) instanceof JCommandToggleButton);
          JCommandToggleButton newButton = ((JCommandToggleButton)liteBand.getComponent(i));
          assertTrue(newButton.getText().equals(item));
        }
        else {
          assertTrue(liteBand.getComponent(i) instanceof JCommandButton);
          JCommandButton newButton = ((JCommandButton)liteBand.getComponent(i));
          assertTrue(newButton.getText().equals(item));
        }
        
      }
      i++;
    }
    
    
    
  }
  
}

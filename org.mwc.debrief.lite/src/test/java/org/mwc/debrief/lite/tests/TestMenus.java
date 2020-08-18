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

package org.mwc.debrief.lite.tests;

import java.util.Arrays;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSlider;

import org.mwc.debrief.lite.DebriefLiteApp;
import org.mwc.debrief.lite.utils.TestUtils;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.common.JCommandButton.CommandButtonKind;
import org.pushingpixels.flamingo.api.common.JCommandButtonStrip;
import org.pushingpixels.flamingo.api.common.JScrollablePanel;
import org.pushingpixels.flamingo.api.common.popup.JPopupPanel;
import org.pushingpixels.flamingo.api.ribbon.AbstractRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.JRibbonFrame;
import org.pushingpixels.flamingo.api.ribbon.RibbonTask;
import org.pushingpixels.flamingo.internal.ui.ribbon.JBandControlPanel;
import org.pushingpixels.flamingo.internal.ui.ribbon.JRibbonComponent;

/**
 * @author Ayesha
 *
 */
public class TestMenus extends BaseTestCase
{

  final static String[] disabledButtons = new String[] {"Close"};
  public void testLiteMenu() {
    JRibbonFrame ribbonFrame = DebriefLiteApp.getInstance().getApplicationFrame();
    assertEquals(ribbonFrame.getRibbon().getTaskCount(),5);
    assertEquals(TestUtils.getTask(0).getTitle(),"Lite");
    RibbonTask liteMenu = ribbonFrame.getRibbon().getTask(0);
    assertTrue(liteMenu.getBandCount()==1);
    doAssertions(TestUtils.getRibbonBand(0,0),"Lite",new String[] {"Help","Exit"},false);
    {
    assertEquals(ribbonFrame.getRibbon().getTask(1).getTitle(),"File");
    RibbonTask fileMenu = TestUtils.getTask(1);
    assertTrue(fileMenu.getBandCount()==3);
    doAssertions(TestUtils.getRibbonBand(1,0),"File",new String[] {"New","Open","Save","Close"},false);
    // TODO: reinstate this test
    // assertSaveAs(TestUtils.getRibbonBand(1,0));
    doAssertions(TestUtils.getRibbonBand(1,1),"Import",new String[] {"Replay","Plot","NMEA","TIF"},false);
    doAssertions(TestUtils.getRibbonBand(1,2),"Export",new String[] {"Clipboard"},false);
    }
    {
    assertEquals(TestUtils.getTask(2).getTitle(),"View");
    RibbonTask viewMenu = TestUtils.getTask(2);
    assertTrue(viewMenu.getBandCount()==3);
    doAssertions(TestUtils.getRibbonBand(2,0),"Mouse mode",new String[] {"Pan","Zoom In","Rng/Brg","Drag Whole Feature","Drag Element"},true);
    doAssertions(TestUtils.getRibbonBand(2,1),"Map commands",new String[] {"Zoom Out","Fit to Window"},false);
    doAssertionTransparencySliderBar(TestUtils.getRibbonBand(2,2),"Background","Transparency:");
    
    }
    {
    assertEquals(TestUtils.getTask(3).getTitle(),"Insert");
    RibbonTask insertMenu = TestUtils.getTask(3);
    assertTrue(insertMenu.getBandCount()==3);
    doAssertions(TestUtils.getRibbonBand(3,0),"Decorations",new String[] {"Scale","Grid"},false);
    doAssertionActiveLayerCombo(TestUtils.getRibbonBand(3, 1));
    doShapesAssertions(TestUtils.getRibbonBand(3, 2));
    
    }
    {
      assertEquals(TestUtils.getTask(4).getTitle(),"Time");
      RibbonTask timeMenu = TestUtils.getTask(4);
      assertTrue(timeMenu.getBandCount()==4);
      //doAssertions(TestUtils.getRibbonBand(4, 0),"Display Mode",new String[] {"Normal","Snail",""},true);
      doAssertionsTimeController(TestUtils.getRibbonBand(4, 2));
      doAssertionsTimeSlider(TestUtils.getRibbonBand(4, 3));
    }
  }
  
  private void doAssertionActiveLayerCombo(AbstractRibbonBand liteBand1) {
    assertEquals(liteBand1.getTitle(),"Active Layer");
    JBandControlPanel liteBand = (JBandControlPanel)liteBand1.getComponent(0);
    assertTrue(liteBand.getComponent(0) instanceof JRibbonComponent);
    JComboBox<String> combo = (JComboBox<String>)TestUtils.getActiveLayerDropDown();
    assertNotNull(combo);
    assertEquals(combo.getItemCount(),3);
    assertEquals(combo.getItemAt(0),"User-selected Layer");
  }
  private void doShapesAssertions(AbstractRibbonBand liteBand1) {
    assertEquals(liteBand1.getTitle(),"Shapes");
    JBandControlPanel liteBand = (JBandControlPanel)liteBand1.getComponent(0);
    assertEquals(7, liteBand.getComponentCount());
    assertTrue(liteBand.getComponent(0) instanceof JCommandButton);
    assertTrue(liteBand.getComponent(2) instanceof JCommandButton);
    assertRibbonLabel("Ellipse",(JCommandButton)liteBand.getComponent(2));
    assertRibbonLabel("Rectangle",(JCommandButton)liteBand.getComponent(3));
    assertRibbonLabel("Circle",(JCommandButton)liteBand.getComponent(4));
    assertRibbonLabel("Arc",(JCommandButton)liteBand.getComponent(5));
    assertRibbonLabel("Line",(JCommandButton)liteBand.getComponent(6));
    JCommandButton labelButton = ((JCommandButton)liteBand.getComponent(0));
    assertTrue(labelButton.getText().equals("Label"));
    assertTrue(labelButton.isEnabled());
    }
  
  
  private void assertRibbonLabel(String string, JCommandButton component)
  {
    assertEquals(component.getText(),string);
    assertTrue(component.isEnabled());   
  }

  private void doAssertionTransparencySliderBar(AbstractRibbonBand liteBand1,String menuName,String itemname) {
    assertEquals(liteBand1.getTitle(),menuName);
    System.out.println("menu name"+menuName);
    JBandControlPanel liteBand = (JBandControlPanel)liteBand1.getComponent(0);
    assertTrue(liteBand.getComponent(0) instanceof JRibbonComponent);
    JRibbonComponent newButton = ((JRibbonComponent)liteBand.getComponent(0));
    assertTrue(newButton.getComponent(0) instanceof JLabel);
 //   assertEquals(((JLabel)newButton.getComponent(0)).getText(),itemname);
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
          assertTrue(liteBand.getComponent(i) instanceof JCommandButton);
          JCommandButton newButton = ((JCommandButton)liteBand.getComponent(i));
          assertTrue(newButton.getText().equals(item));
          if(Arrays.asList(disabledButtons).contains(newButton.getText())) {
            assertFalse(newButton.isEnabled());
          }
          else {
            assertTrue(newButton.isEnabled());
          }
        }
        else {
          assertTrue(liteBand.getComponent(i) instanceof JCommandButton);
          JCommandButton newButton = ((JCommandButton)liteBand.getComponent(i));
          assertTrue(newButton.getText().equals(item));
          if(Arrays.asList(disabledButtons).contains(newButton.getText())) {
        	 // TODO: the close button is enabled here, the test
        	 // thinks it should be disabled
//            assertFalse(newButton.isEnabled());
          }
          else {
            assertTrue(newButton.isEnabled());
          }
        }
        
      }
      i++;
    }
  }
  
  private void assertSaveAs(AbstractRibbonBand rb) {
    JBandControlPanel liteBand = (JBandControlPanel)rb.getComponent(0);
    JCommandButton newButton = ((JCommandButton)liteBand.getComponent(2));
    assertTrue(newButton.getText().equals("Save"));
    if(Arrays.asList(disabledButtons).contains(newButton.getText())) {
      assertFalse(newButton.isEnabled());
    }
    else {
      assertTrue(newButton.isEnabled());
    }
    assertTrue(newButton.getCommandButtonKind().equals(CommandButtonKind.ACTION_AND_POPUP_MAIN_POPUP));
    assertNotNull(newButton.getPopupCallback());
    assertNotNull(newButton.getPopupCallback().getPopupPanel(newButton));
    JPopupPanel panel = (JPopupPanel)newButton.getPopupCallback().getPopupPanel(newButton);
    assertEquals(panel.getComponentCount(),1);
    JCommandButton saveButton = (JCommandButton)TestUtils.getChildNamed(panel, "save");
    assertEquals("Save",saveButton.getText());
    JCommandButton saveAsButton = (JCommandButton)TestUtils.getChildNamed(panel, "saveas");
    assertEquals("Save As",saveAsButton.getText());
    
    @SuppressWarnings("unchecked")
    JScrollablePanel<JCommandButton> scrollPanel = (JScrollablePanel<JCommandButton>)panel.getComponent(0);
    //3 as there is a panel to which the command buttons are added.
    assertEquals(3,scrollPanel.getComponentCount());
  }
  
  private void doAssertionsTimeController(AbstractRibbonBand rb)
  {
    JCommandButtonStrip panel = TestUtils.getTimeControllerCommands();
    assertEquals(8,panel.getComponentCount());
    JCommandButton btn = (JCommandButton)panel.getComponent(0); 
    assertNotNull(btn);
    assertEquals(btn.getProjection().getContentModel().getText().toLowerCase(),"behind");
    assertFalse(btn.getProjection().getContentModel().isActionEnabled());
    btn = (JCommandButton)panel.getComponent(1);  
    assertEquals(btn.getProjection().getContentModel().getText().toLowerCase(),"rewind");
    assertNotNull(btn);
    assertFalse(btn.getProjection().getContentModel().isActionEnabled());
    btn = (JCommandButton)panel.getComponent(2); 
    assertNotNull(btn);
    assertEquals(btn.getProjection().getContentModel().getText().toLowerCase(),"back");
    assertFalse(btn.getProjection().getContentModel().isActionEnabled());
    btn = (JCommandButton)panel.getComponent(3);  
    assertNotNull(btn);
    assertEquals(btn.getProjection().getContentModel().getText().toLowerCase(),"play");
    assertFalse(btn.getProjection().getContentModel().isActionEnabled());
    btn = (JCommandButton)panel.getComponent(4);  
    assertNotNull(btn);
    assertEquals(btn.getProjection().getContentModel().getText().toLowerCase(),"forward");
    assertFalse(btn.getProjection().getContentModel().isActionEnabled());
    btn = (JCommandButton)panel.getComponent(5);  
    assertNotNull(btn);
    assertEquals(btn.getProjection().getContentModel().getText().toLowerCase(),"fast forward");
    assertFalse(btn.getProjection().getContentModel().isActionEnabled());
    btn = (JCommandButton)panel.getComponent(6);  
    assertNotNull(btn);
    assertEquals(btn.getProjection().getContentModel().getText().toLowerCase(),"end");
    assertFalse(btn.getProjection().getContentModel().isActionEnabled());
    
    btn = (JCommandButton)panel.getComponent(7); 
    assertNotNull(btn);
    assertEquals(btn.getProjection().getContentModel().getText().toLowerCase(),"properties");
    assertFalse(btn.getProjection().getContentModel().isActionEnabled());
    JLabel label = TestUtils.getTimeFormatLabel();
    assertNotNull(label);
    assertEquals(label.getText(),"yy/MM/dd HH:mm:ss");
    JCommandButton formatButton = TestUtils.getEditTimeFormatButton();
    assertNotNull(formatButton);
    assertEquals(formatButton.getText(),"Format");
    
  }
  private void doAssertionsTimeSlider(AbstractRibbonBand rb) {
    JBandControlPanel panel = ((JBandControlPanel)rb.getComponent(0));
    assertEquals(2,panel.getComponentCount());
    JRibbonComponent rc = (JRibbonComponent)panel.getComponent(0);
    JSlider slider = (JSlider)rc.getComponent(1);
    assertFalse(slider.isEnabled());
    assertNotNull(slider);
  }
}

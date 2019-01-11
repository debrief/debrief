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
package org.mwc.debrief.lite.menu;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.mwc.debrief.lite.gui.DebriefLiteToolParent;
import org.mwc.debrief.lite.map.GeoToolMapRenderer;
import org.pushingpixels.flamingo.api.common.CommandButtonDisplayState;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.common.icon.ImageWrapperResizableIcon;
import org.pushingpixels.flamingo.api.ribbon.JRibbon;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.JRibbonComponent;
import org.pushingpixels.flamingo.api.ribbon.JRibbonFrame;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;
import org.pushingpixels.flamingo.api.ribbon.RibbonTask;
import org.pushingpixels.flamingo.api.ribbon.resize.CoreRibbonResizePolicies;
import org.pushingpixels.flamingo.api.ribbon.resize.CoreRibbonResizePolicies.IconRibbonBandResizePolicy;
import org.pushingpixels.flamingo.api.ribbon.resize.RibbonBandResizePolicy;

import Debrief.Tools.Palette.CreateShape;
import Debrief.Wrappers.ShapeWrapper;
import MWC.GUI.Layers;
import MWC.GUI.Properties.DebriefColors;
import MWC.GUI.Properties.PropertiesPanel;
import MWC.GUI.Shapes.EllipseShape;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;

/**
 * @author Ayesha <ayesha.ma@gmail.com>
 *
 */
public class DebriefRibbon
{
  private PropertiesPanel _theProperties;
  private Layers _theLayers;
  private DebriefLiteToolParent _toolParent;
  private JRibbonFrame theFrame;
  private JRibbon theRibbon;
  private GeoToolMapRenderer _geoMapRenderer;
  public DebriefRibbon(JRibbonFrame frame,Layers layers,DebriefLiteToolParent parent,GeoToolMapRenderer geoMapRenderer) {
    _theLayers = layers;
    _toolParent = parent;
    theFrame = frame;
    _geoMapRenderer =geoMapRenderer; 
  }

  public void setProperties(PropertiesPanel properties)
  {
    _theProperties = properties;
  }

  public void addMenus() {
    theRibbon = theFrame.getRibbon();
    //add menus here
    addFileMenuTasks();
    addViewMenuTasks();
    addChartFeaturesTasks();
    addDrawingTasks();
    addTimeControllerTasks();



  }

  private void addFileMenuTasks() {
    JRibbonBand fileMenu = new JRibbonBand("File",null);
    MenuUtils.addCommandButton("New", "images/16/new.png", new NewFileAction(), fileMenu,RibbonElementPriority.MEDIUM);
    MenuUtils.addCommandButton("New (default plot)", "images/16/new.png", new NewFileAction(), fileMenu,RibbonElementPriority.MEDIUM);
    MenuUtils.addCommandButton("Open Plot", "images/16/open.png", new NewFileAction(), fileMenu,RibbonElementPriority.MEDIUM);
    fileMenu.setResizePolicies(getStandardRestrictivePolicies(fileMenu));
    JRibbonBand exitMenu = new JRibbonBand("Exit",null);
    MenuUtils.addCommandButton("Exit", "images/16/exit.png", new AbstractAction()
    {

      /**
       * 
       */
      private static final long serialVersionUID = 1L;

      @Override
      public void actionPerformed(ActionEvent e)
      {
        exit();

      }
    }, exitMenu,RibbonElementPriority.MEDIUM);
    exitMenu.setResizePolicies(getStandardRestrictivePolicies(exitMenu));

    JRibbonBand importMenu = new JRibbonBand("Import",null);
    MenuUtils.addCommandButton("Import Replay", "images/16/import.png", new NewFileAction(), importMenu,RibbonElementPriority.MEDIUM);
    importMenu.setResizePolicies(getStandardRestrictivePolicies(importMenu));
    fileMenu.setPreferredSize(new Dimension(150,50));
    importMenu.setPreferredSize(new Dimension(50,50));
    RibbonTask fileTask = new RibbonTask("File", fileMenu,importMenu, exitMenu);
    theRibbon.addTask(fileTask);
    fileMenu.setPreferredSize(new Dimension(50,50));

  }
  protected void exit()
  {
    //_dropSupport.removeFileDropListener(this);
    System.exit(0);

  }
  private List<RibbonBandResizePolicy> getStandardRestrictivePolicies(JRibbonBand ribbonBand){
    List<RibbonBandResizePolicy> policies = new ArrayList<>();
    policies.add(new CoreRibbonResizePolicies.Mirror(ribbonBand));
    //policies.add(new CoreRibbonResizePolicies.Mid2Low(ribbonBand));
    policies.add(new IconRibbonBandResizePolicy(ribbonBand));
    return policies;
  }

  private void addViewMenuTasks() {
    JRibbonBand viewMenu = new JRibbonBand("View",null);
    _geoMapRenderer.addMapTool(viewMenu, theRibbon);
  }
  
  private WorldArea getChartBounds()
  {
    final ReferencedEnvelope env = _geoMapRenderer.getMapComponent().getViewport().getBounds();
    final WorldLocation tl = new WorldLocation(env.getMaxX(), env.getMinY(), 0);
    final WorldLocation br = new WorldLocation(env.getMinX(), env.getMaxY(), 0);
    final WorldArea res = new WorldArea(tl, br);
    return res;
  }
  
  private void addChartFeaturesTasks() {
    JRibbonBand chartfeaturesMenu = new JRibbonBand("Chart Features",null);
    MenuUtils.addCommandButton("Scale", "images/16/scale.png", new NewFileAction(), chartfeaturesMenu,null);
    MenuUtils.addCommandButton("Time Display (Absolute)", null, new NewFileAction(), chartfeaturesMenu,RibbonElementPriority.MEDIUM);
    MenuUtils.addCommandButton("Time Display (Relative)",null, new NewFileAction(), chartfeaturesMenu,RibbonElementPriority.MEDIUM);
    MenuUtils.addCommandButton("4W Grid", "images/16/grid4w.png", new NewFileAction(), chartfeaturesMenu,null);
    MenuUtils.addCommandButton("Grid", "images/16/grid.png", new NewFileAction(), chartfeaturesMenu,null);
    MenuUtils.addCommandButton("Local Grid", "images/16/local_grid.png", new NewFileAction(), chartfeaturesMenu,null);
    MenuUtils.addCommandButton("Coastline", "images/16/coast.png", new NewFileAction(), chartfeaturesMenu,RibbonElementPriority.MEDIUM);
    chartfeaturesMenu.setResizePolicies(getStandardRestrictivePolicies(chartfeaturesMenu));
    RibbonTask chartFeaturesTask = new RibbonTask("Chart Features", chartfeaturesMenu);
    theRibbon.addTask(chartFeaturesTask);
  }
  private void addDrawingTasks() {
    JRibbonBand drawingMenu = new JRibbonBand("Drawing",null);
    CreateShape ellipseShape = new CreateShape(_toolParent, _theProperties, _theLayers,
        "Ellipse", "images/ellipse_add.png")
    {
      protected ShapeWrapper getShape(final WorldLocation centre)
      {
        return new ShapeWrapper("new ellipse", new EllipseShape(centre, 0,
            new WorldDistance(0, WorldDistance.DEGS), new WorldDistance(0,
                WorldDistance.DEGS)), DebriefColors.RED, null);
      }

      @Override
      protected WorldArea getBounds()
      {
        return getChartBounds();
      }
    }; 

    JCommandButton ellipseButton = new JCommandButton("ellipse");
    ellipseButton.setDisplayState(CommandButtonDisplayState.MEDIUM);
    ellipseButton.addActionListener(ellipseShape);
    theRibbon.add(new JRibbonComponent(ImageWrapperResizableIcon.getIcon(MenuUtils.createImage("images/16/ellipse.png"), new Dimension(16,16)), "Ellipse", ellipseButton));
    //MenuUtils.addCommandButton("Ellipse", "images/16/ellipse.png", 
    //  ellipseShape, drawingMenu,RibbonElementPriority.MEDIUM);
    MenuUtils.addCommandButton("Polygon", "images/16/polygon.png", new NewFileAction(), drawingMenu,RibbonElementPriority.MEDIUM);
    MenuUtils.addCommandButton("Line", "images/16/line.png", new NewFileAction(), drawingMenu,RibbonElementPriority.MEDIUM);
    MenuUtils.addCommandButton("Rectangle", "images/16/rectangle.png", new NewFileAction(), drawingMenu,RibbonElementPriority.MEDIUM);
    MenuUtils.addCommandButton("Wheel", "images/16/wheel.png", new NewFileAction(), drawingMenu,RibbonElementPriority.MEDIUM);
    MenuUtils.addCommandButton("Circle", "images/16/circle.png", new NewFileAction(), drawingMenu,RibbonElementPriority.MEDIUM);
    drawingMenu.setResizePolicies(getStandardRestrictivePolicies(drawingMenu));
    RibbonTask drawingTask = new RibbonTask("Drawing", drawingMenu);
    theRibbon.addTask(drawingTask);
  }


  private void addTimeControllerTasks() {
    JRibbonBand timeMenu = new JRibbonBand("Time Controller",null);
    MenuUtils.addCommandButton("Play", null, new NewFileAction(), timeMenu,RibbonElementPriority.MEDIUM);
    MenuUtils.addCommandButton("Record", "images/16/zoomin.png", new NewFileAction(), timeMenu,RibbonElementPriority.MEDIUM);
    timeMenu.setResizePolicies(getStandardRestrictivePolicies(timeMenu));
    RibbonTask timeTask = new RibbonTask("Time Controller", timeMenu);
    theRibbon.addTask(timeTask);
  }

 /* private JCommandButton addCommandButton() {

  }*/



  private static class NewFileAction extends AbstractAction{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e)
    {
      System.out.println("Action clicked");

    }
  }

}

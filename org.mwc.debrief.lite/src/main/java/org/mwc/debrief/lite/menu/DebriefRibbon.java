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
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.swing.JMapPane;
import org.mwc.debrief.lite.DbriefJMapPane1;
import org.mwc.debrief.lite.DebriefLiteApp;
import org.mwc.debrief.lite.gui.DebriefLiteToolParent;
import org.mwc.debrief.lite.map.GeoToolMapRenderer;
import org.pushingpixels.flamingo.api.common.CommandButtonDisplayState;
import org.pushingpixels.flamingo.api.common.FlamingoCommand;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.common.JCommandButtonStrip;
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
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Properties.DebriefColors;
import MWC.GUI.Properties.PropertiesPanel;
import MWC.GUI.Shapes.ArcShape;
import MWC.GUI.Shapes.CircleShape;
import MWC.GUI.Shapes.EllipseShape;
import MWC.GUI.Shapes.LineShape;
import MWC.GUI.Shapes.PolygonShape;
import MWC.GUI.Shapes.RectangleShape;
import MWC.GUI.Tools.PlainTool.BoundsProvider;
import MWC.GUI.Tools.Palette.CreateCoast;
import MWC.GUI.Tools.Palette.CreateGrid;
import MWC.GUI.Tools.Palette.CreateLocalGrid;
import MWC.GUI.Tools.Palette.CreateScale;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

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

  
  private int ctr = 0;
  public DebriefRibbon(JRibbonFrame frame, Layers layers,
      DebriefLiteToolParent parent, GeoToolMapRenderer geoMapRenderer)
  {
    _theLayers = layers;
    _toolParent = parent;
    theFrame = frame;
    _geoMapRenderer = geoMapRenderer;
    theFrame.addComponentListener(new ComponentAdapter() {
	
    	@Override
    	public void componentResized(ComponentEvent e) {
    		// TODO Auto-generated method stub
    		super.componentResized(e);
    		if(ctr >1) {
    		 _geoMapRenderer.resizeMapArea();
    		}
    		ctr++;
    	}
    });
    
    
    final DbriefJMapPane1 mapPane = (DbriefJMapPane1)_geoMapRenderer.getMap();
    mapPane.addComponentListener(new ComponentAdapter() {
    	@Override
    	public void componentResized(ComponentEvent e) {
    		super.componentResized(e);
    		if(DebriefLiteApp.parentLoaded) {
    			//theFrame.setSize(theFrame.getWidth()+1, theFrame.getHeight());
    			//_geoMapRenderer.resizeMapArea();
    		}
      	}
	});
    
    
  }

  public void setProperties(PropertiesPanel properties)
  {
    _theProperties = properties;
  }

  public void addMenus()
  {
    theRibbon = theFrame.getRibbon();
    
    /** some of our tools are interested in the visible data area.  But, 
     * we can't determine it when they're generated.  Instead of 
     * providing a world area, we provide an object that 
     * is capable of providing the _Current_ visible data area
     */
    final BoundsProvider bounds = new BoundsProvider()
    {
      @Override
      public WorldArea getViewport()
      {
        final ReferencedEnvelope env = _geoMapRenderer.getMapComponent()
            .getViewport().getBounds();
        final WorldLocation tl = new WorldLocation(env.getMaxY(), env.getMinX(), 0);
        final WorldLocation br = new WorldLocation(env.getMinY(), env.getMaxX(), 0);
        final WorldArea res = new WorldArea(tl, br);
        return res;
      }
      
      @Override
      public WorldArea getBounds()
      {
        return _theLayers.getBounds();
      }
    };
    
    // add menus here
    addFileMenuTasks();
    addViewMenuTasks();
    addDrawingTasks(bounds);
    addTimeControllerTasks();

  }

  private void addFileMenuTasks()
  {
    JRibbonBand fileMenu = new JRibbonBand("File", null);
    MenuUtils.addCommand("New", "images/16/new.png", new NewFileAction(),
        fileMenu, RibbonElementPriority.MEDIUM);
    MenuUtils.addCommand("New (default plot)", "images/16/new.png",
        new NewFileAction(), fileMenu, RibbonElementPriority.MEDIUM);
    MenuUtils.addCommand("Open Plot", "images/16/open.png",
        new NewFileAction(), fileMenu, RibbonElementPriority.MEDIUM);
    fileMenu.setResizePolicies(getStandardRestrictivePolicies(fileMenu));
    JRibbonBand exitMenu = new JRibbonBand("Exit", null);
    MenuUtils.addCommand("Exit", "images/16/exit.png",
        new AbstractAction()
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
        }, exitMenu, RibbonElementPriority.MEDIUM);
    exitMenu.setResizePolicies(getStandardRestrictivePolicies(exitMenu));

    JRibbonBand importMenu = new JRibbonBand("Import / Export", null);
    MenuUtils.addCommand("Import Replay", "images/16/import.png",
        new NewFileAction(), importMenu, RibbonElementPriority.MEDIUM);
    importMenu.setResizePolicies(getStandardRestrictivePolicies(importMenu));
    MenuUtils.addCommand("Copy Plot to PNG", "images/16/import.png",
        new CopyPlotAsPNG(_geoMapRenderer), importMenu,
        RibbonElementPriority.MEDIUM);
    fileMenu.setPreferredSize(new Dimension(150, 50));
    importMenu.setPreferredSize(new Dimension(50, 50));
    RibbonTask fileTask = new RibbonTask("File", fileMenu, importMenu,
        exitMenu);
    theRibbon.addTask(fileTask);
    fileMenu.setPreferredSize(new Dimension(50, 50));

  }

  protected void exit()
  {
    // _dropSupport.removeFileDropListener(this);
    System.exit(0);

  }

  private List<RibbonBandResizePolicy> getStandardRestrictivePolicies(
      JRibbonBand ribbonBand)
  {
    List<RibbonBandResizePolicy> policies = new ArrayList<>();
    policies.add(new CoreRibbonResizePolicies.Mirror(ribbonBand));
    // policies.add(new CoreRibbonResizePolicies.Mid2Low(ribbonBand));
    policies.add(new IconRibbonBandResizePolicy(ribbonBand));
    return policies;
  }

  private void addViewMenuTasks()
  {
    JRibbonBand viewMenu = new JRibbonBand("View", null);
    _geoMapRenderer.addMapTool(viewMenu, theRibbon);
  }

 

  private void addDrawingTasks(final BoundsProvider bounds)
  {
    JRibbonBand drawingMenu = new JRibbonBand("Shapes", null);
   
    JRibbonBand chartfeaturesMenu = new JRibbonBand("Decorations", null);
    final Layer decs = _theLayers.findLayer(Layers.CHART_FEATURES);
    FlamingoCommand scaleCmd = MenuUtils.addCommand("Scale", "images/16/scale.png",
        new CreateScale(_toolParent, _theProperties, decs,
            _theLayers, bounds), chartfeaturesMenu, null);
    chartfeaturesMenu.startGroup("Time Marker");
    JCommandButton tmaCmd = MenuUtils.addCommandButton("Absolute", null,
        new NewFileAction(), CommandButtonDisplayState.MEDIUM);
    chartfeaturesMenu.addRibbonComponent(new JRibbonComponent(tmaCmd));
    JCommandButton tmrCmd = MenuUtils.addCommandButton("Relative", null,
        new NewFileAction(), CommandButtonDisplayState.MEDIUM);
    chartfeaturesMenu.addRibbonComponent(new JRibbonComponent(tmrCmd));
    chartfeaturesMenu.startGroup("Grid");
    JCommandButton grid4wCmd = MenuUtils.addCommandButton("4W Grid", "images/16/grid4w.png",
        new NewFileAction(), CommandButtonDisplayState.MEDIUM);
    chartfeaturesMenu.addRibbonComponent(new JRibbonComponent(grid4wCmd));
    JCommandButton gridCmd = MenuUtils.addCommandButton("Grid", "images/16/grid.png",
        new CreateGrid(_toolParent, _theProperties, decs,
            _theLayers, bounds), CommandButtonDisplayState.MEDIUM);
    chartfeaturesMenu.addRibbonComponent(new JRibbonComponent(gridCmd));
    JCommandButton localGridCmd = MenuUtils.addCommandButton("Local Grid", "images/16/local_grid.png",
        new CreateLocalGrid(_toolParent, _theProperties,
            decs, _theLayers, bounds), CommandButtonDisplayState.MEDIUM);
    chartfeaturesMenu.addRibbonComponent(new JRibbonComponent(localGridCmd));
    JRibbonBand referenceDataMenu = new JRibbonBand("Reference Data", null);
    FlamingoCommand coastlineCmd = MenuUtils.addCommand("Coastline", "images/16/coast.png",
        new CreateCoast(_toolParent, _theProperties, decs,
            _theLayers, bounds), referenceDataMenu,RibbonElementPriority.MEDIUM);
    FlamingoCommand chartLibraryCmd = MenuUtils.addCommand("Chart Lib", "images/16/coast.png",
        new CreateCoast(_toolParent, _theProperties, decs,
            _theLayers, bounds), referenceDataMenu,RibbonElementPriority.MEDIUM);
    FlamingoCommand naturalEarthCmd = MenuUtils.addCommand("Natural Earth", "images/16/coast.png",
        new CreateCoast(_toolParent, _theProperties, decs,
            _theLayers, bounds), referenceDataMenu,RibbonElementPriority.MEDIUM);
    referenceDataMenu.setResizePolicies(getStandardRestrictivePolicies(referenceDataMenu));
    
    chartfeaturesMenu.setResizePolicies(getStandardRestrictivePolicies(
        chartfeaturesMenu));
    drawingMenu.startGroup("Core");
    JCommandButton ellipseShapeCmd = MenuUtils.addCommandButton("Ellipse", "images/16/ellipse.png",
        new CreateShape(_toolParent, _theProperties,
            _theLayers, "Ellipse", "images/ellipse_add.png", bounds)
        {
          protected ShapeWrapper getShape(final WorldLocation centre)
          {
            return new ShapeWrapper("new ellipse", new EllipseShape(centre, 0,
                new WorldDistance(0, WorldDistance.DEGS), new WorldDistance(0,
                    WorldDistance.DEGS)), DebriefColors.RED, null);
          }
        }, CommandButtonDisplayState.FIT_TO_ICON);
    JCommandButton polygonCmd = MenuUtils.addCommandButton("Polygon", "images/16/polygon.png",
        new CreateShape(_toolParent, _theProperties, _theLayers,
            "Polygon", "images/polygon_add.png", bounds)
        {
          protected ShapeWrapper getShape(final WorldLocation centre)
          {
            return new ShapeWrapper("new polygon", new PolygonShape(null), DebriefColors.RED,
                null);
          }
        }, CommandButtonDisplayState.FIT_TO_ICON);
    
    JCommandButton lineCmd = MenuUtils.addCommandButton("Line", "images/16/line.png",
        new CreateShape(_toolParent, _theProperties, _theLayers,
            "Line", "images/line_add.png", bounds)
        {
          protected ShapeWrapper getShape(final WorldLocation centre)
          {
            return new ShapeWrapper("new line", new LineShape(centre, centre
                .add(new WorldVector(
                    MWC.Algorithms.Conversions.Degs2Rads(45.0), 0.05, 0))),
                    DebriefColors.RED, null);
          }
        }, CommandButtonDisplayState.FIT_TO_ICON);
    JCommandButton rectCmd = MenuUtils.addCommandButton("Rectangle", "images/16/rectangle.png",
        new CreateShape(_toolParent, _theProperties, _theLayers,
            "Rectangle", "images/rectangle_add.png", bounds)
        {
          protected ShapeWrapper getShape(final WorldLocation centre)
          {
            return new ShapeWrapper("new rectangle", new RectangleShape(centre,
                centre.add(new WorldVector(MWC.Algorithms.Conversions
                    .Degs2Rads(45), 0.05, 0))), DebriefColors.RED, null);
          }
        }, CommandButtonDisplayState.FIT_TO_ICON);
    JCommandButton circleCmd = MenuUtils.addCommandButton("Circle", "images/16/circle.png",
        new CreateShape(_toolParent, _theProperties, _theLayers,
            "Circle", "images/circle_add.png", bounds)
        {
          protected ShapeWrapper getShape(final WorldLocation centre)
          {
            return new ShapeWrapper("new circle",
                new CircleShape(centre, 4000), DebriefColors.RED, null);
          }
        }, CommandButtonDisplayState.FIT_TO_ICON);
    JCommandButton arcCmd = MenuUtils.addCommandButton("Arc", "images/arc_add.png", new CreateShape(_toolParent, _theProperties, _theLayers,
            "Arc", "images/arc_add.png", bounds)
        {
          protected ShapeWrapper getShape(final WorldLocation centre)
          {
            return new ShapeWrapper("new arc", new ArcShape(centre,
                new WorldDistance(4000, WorldDistance.YARDS), 135, 90, true,
                false), DebriefColors.RED, null);
          }
        }, CommandButtonDisplayState.FIT_TO_ICON);
    
    JCommandButtonStrip shapesStrip = new JCommandButtonStrip();
    shapesStrip.add(ellipseShapeCmd);
    shapesStrip.add(polygonCmd);
    shapesStrip.add(rectCmd);
    shapesStrip.add(circleCmd);
    shapesStrip.add(lineCmd);
    shapesStrip.add(arcCmd);
    drawingMenu.addRibbonComponent(new JRibbonComponent(shapesStrip));
    
    drawingMenu.setResizePolicies(getStandardRestrictivePolicies(drawingMenu));
    RibbonTask drawingTask = new RibbonTask("Insert", chartfeaturesMenu,referenceDataMenu,drawingMenu);
    theRibbon.addTask(drawingTask);
  }
  
  

  private void addTimeControllerTasks()
  {
    JRibbonBand timeMenu = new JRibbonBand("Time Controller", null);
    MenuUtils.addCommand("Play", null, new NewFileAction(), timeMenu,
        RibbonElementPriority.MEDIUM);
    MenuUtils.addCommand("Record", "images/16/zoomin.png",
        new NewFileAction(), timeMenu, RibbonElementPriority.MEDIUM);
    timeMenu.setResizePolicies(getStandardRestrictivePolicies(timeMenu));
    RibbonTask timeTask = new RibbonTask("Time Controller", timeMenu);
    theRibbon.addTask(timeTask);
  }

  
   private static class CopyPlotAsPNG extends AbstractAction
  {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private final GeoToolMapRenderer mapRenderer;

    public CopyPlotAsPNG(final GeoToolMapRenderer _geoMapRenderer)
    {
      mapRenderer = _geoMapRenderer;
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
      final JMapPane map = (JMapPane) mapRenderer.getMap();
      final RenderedImage image = map.getBaseImage();

      if (image != null)
      {
        Transferable t = new Transferable()
        {

          public DataFlavor[] getTransferDataFlavors()
          {
            return new DataFlavor[]
            {DataFlavor.imageFlavor};
          }

          public boolean isDataFlavorSupported(DataFlavor flavor)
          {
            if (flavor == DataFlavor.imageFlavor)
              return true;
            return false;
          }

          public Object getTransferData(DataFlavor flavor)
              throws UnsupportedFlavorException, IOException
          {
            if (isDataFlavorSupported(flavor))
            {
              return image;
            }
            return null;
          }

        };

        ClipboardOwner co = new ClipboardOwner()
        {

          public void lostOwnership(Clipboard clipboard, Transferable contents)
          {
          }

        };
        Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
        cb.setContents(t, co);
      }
    }
  }

  private static class NewFileAction extends AbstractAction
  {
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

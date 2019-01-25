package org.mwc.debrief.lite.menu;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.mwc.debrief.lite.gui.DebriefLiteToolParent;
import org.mwc.debrief.lite.map.GeoToolMapRenderer;
import org.pushingpixels.flamingo.api.common.CommandButtonDisplayState;
import org.pushingpixels.flamingo.api.common.FlamingoCommand;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.common.JCommandButtonStrip;
import org.pushingpixels.flamingo.api.common.JCommandButtonStrip.StripOrientation;
import org.pushingpixels.flamingo.api.common.JCommandToggleButton;
import org.pushingpixels.flamingo.api.ribbon.JRibbon;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.JRibbonComponent;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;
import org.pushingpixels.flamingo.api.ribbon.RibbonTask;

import Debrief.Tools.Palette.CreateLabel;
import Debrief.Tools.Palette.CreateShape;
import Debrief.Wrappers.ShapeWrapper;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Properties.DebriefColors;
import MWC.GUI.Properties.PropertiesPanel;
import MWC.GUI.Shapes.CircleShape;
import MWC.GUI.Shapes.EllipseShape;
import MWC.GUI.Shapes.LineShape;
import MWC.GUI.Shapes.RectangleShape;
import MWC.GUI.Tools.PlainTool.BoundsProvider;
import MWC.GUI.Tools.Palette.CreateCoast;
import MWC.GUI.Tools.Palette.CreateGrid;
import MWC.GUI.Tools.Palette.CreateScale;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

public class DebriefRibbonInsert
{
  protected static void addInsertTab(final JRibbon ribbon,
      final GeoToolMapRenderer _geoMapRenderer, final Layers _theLayers,
      final PropertiesPanel _theProperties,
      final DebriefLiteToolParent _toolParent)
  {
    /**
     * some of our tools are interested in the visible data area. But, we can't determine it when
     * they're generated. Instead of providing a world area, we provide an object that is capable of
     * providing the _Current_ visible data area
     */
    final BoundsProvider bounds = new BoundsProvider()
    {
      @Override
      public WorldArea getBounds()
      {
        return _theLayers.getBounds();
      }

      @Override
      public WorldArea getViewport()
      {
        final ReferencedEnvelope env = _geoMapRenderer.getMapComponent()
            .getViewport().getBounds();
        final WorldLocation tl = new WorldLocation(env.getMaxY(), env.getMinX(),
            0);
        final WorldLocation br = new WorldLocation(env.getMinY(), env.getMaxX(),
            0);
        final WorldArea res = new WorldArea(tl, br);
        return res;
      }
    };

    final Layer decs = _theLayers.findLayer(Layers.CHART_FEATURES);
    final JRibbonBand chartfeaturesMenu = createDecorations(_theLayers,
        _theProperties, _toolParent, bounds, decs);
    
    final JRibbonBand referenceDataMenu = createReferenceData(_theLayers,
        _theProperties, _toolParent, bounds, decs);

    final JRibbonBand drawingMenu = createShapes(_theLayers, _theProperties,
        _toolParent, bounds);
    
    final RibbonTask drawingTask = new RibbonTask("Insert", chartfeaturesMenu,
        referenceDataMenu, drawingMenu);
    ribbon.addTask(drawingTask);
  }

  private static JRibbonBand createReferenceData(final Layers _theLayers,
      final PropertiesPanel _theProperties,
      final DebriefLiteToolParent _toolParent, final BoundsProvider bounds,
      final Layer decs)
  {
    final JRibbonBand referenceDataMenu = new JRibbonBand("Reference Data",
        null);
    @SuppressWarnings("unused")
    final FlamingoCommand coastlineCmd = MenuUtils.addCommand("Coastline",
        "images/16/coast.png", new CreateCoast(_toolParent, _theProperties,
            decs, _theLayers, bounds), referenceDataMenu,
        RibbonElementPriority.MEDIUM);
    @SuppressWarnings("unused")
    final FlamingoCommand chartLibraryCmd = MenuUtils.addCommand("Chart Lib",
        "images/16/coast.png", new CreateCoast(_toolParent, _theProperties,
            decs, _theLayers, bounds), referenceDataMenu,
        RibbonElementPriority.MEDIUM);
    @SuppressWarnings("unused")
    final FlamingoCommand naturalEarthCmd = MenuUtils.addCommand(
        "Natural Earth", "images/16/coast.png", new CreateCoast(_toolParent,
            _theProperties, decs, _theLayers, bounds), referenceDataMenu,
        RibbonElementPriority.MEDIUM);
    referenceDataMenu.setResizePolicies(MenuUtils
        .getStandardRestrictivePolicies(referenceDataMenu));
    return referenceDataMenu;
  }

  private static JRibbonBand createShapes(final Layers _theLayers,
      final PropertiesPanel _theProperties,
      final DebriefLiteToolParent _toolParent, final BoundsProvider bounds)
  {
    final JRibbonBand drawingMenu = new JRibbonBand("Shapes", null);
    drawingMenu.startGroup("Core");
    final JCommandButton ellipseShapeCmd = MenuUtils.addCommandButton("Ellipse",
        "images/16/ellipse.png", new CreateShape(_toolParent, _theProperties,
            _theLayers, "Ellipse", "images/ellipse_add.png", bounds)
        {
          @Override
          protected ShapeWrapper getShape(final WorldLocation centre)
          {
            return new ShapeWrapper("new ellipse", new EllipseShape(centre, 0,
                new WorldDistance(0, WorldDistance.DEGS), new WorldDistance(0,
                    WorldDistance.DEGS)), DebriefColors.RED, null);
          }
        }, CommandButtonDisplayState.MEDIUM);
    final JCommandButton labelCmd = MenuUtils.addCommandButton("Label",
        "images/16/polygon.png", new CreateLabel(_toolParent, _theProperties,
            _theLayers,bounds, "Polygon", "images/polygon_add.png"),CommandButtonDisplayState.MEDIUM);

    final JCommandButton lineCmd = MenuUtils.addCommandButton("Line",
        "images/16/line.png", new CreateShape(_toolParent, _theProperties,
            _theLayers, "Line", "images/line_add.png", bounds)
        {
          @Override
          protected ShapeWrapper getShape(final WorldLocation centre)
          {
            return new ShapeWrapper("new line", new LineShape(centre, centre
                .add(new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(45.0),
                    0.05, 0))), DebriefColors.RED, null);
          }
        }, CommandButtonDisplayState.MEDIUM);
    final JCommandButton rectCmd = MenuUtils.addCommandButton("Rectangle",
        "images/16/rectangle.png", new CreateShape(_toolParent, _theProperties,
            _theLayers, "Rectangle", "images/rectangle_add.png", bounds)
        {
          @Override
          protected ShapeWrapper getShape(final WorldLocation centre)
          {
            return new ShapeWrapper("new rectangle", new RectangleShape(centre,
                centre.add(new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(
                    45), 0.05, 0))), DebriefColors.RED, null);
          }
        }, CommandButtonDisplayState.MEDIUM);
    final JCommandButton circleCmd = MenuUtils.addCommandButton("Circle",
        "images/16/circle.png", new CreateShape(_toolParent, _theProperties,
            _theLayers, "Circle", "images/circle_add.png", bounds)
        {
          @Override
          protected ShapeWrapper getShape(final WorldLocation centre)
          {
            return new ShapeWrapper("new circle", new CircleShape(centre, 4000),
                DebriefColors.RED, null);
          }
        }, CommandButtonDisplayState.MEDIUM);
    
    final JCommandButtonStrip shapesStrip = new JCommandButtonStrip(StripOrientation.HORIZONTAL);
    shapesStrip.setDisplayState(CommandButtonDisplayState.MEDIUM);
    shapesStrip.add(circleCmd);
    shapesStrip.add(labelCmd);
    shapesStrip.add(ellipseShapeCmd);
    shapesStrip.add(lineCmd);
    drawingMenu.addRibbonComponent(new JRibbonComponent(shapesStrip));
    //drawingMenu.startGroup();
    final JCommandButtonStrip shapesStrip2 = new JCommandButtonStrip(StripOrientation.HORIZONTAL);
    shapesStrip2.setDisplayState(CommandButtonDisplayState.MEDIUM);
    shapesStrip2.add(rectCmd);
    final JCommandToggleButton layerSelectCmd = MenuUtils.addCommandToggleButton("Toggle for select \ntarget layer", "images/16/layer_mgr.png", new AbstractAction()
    {
      
      @Override
      public void actionPerformed(ActionEvent e)
      {
        System.out.println("Action clicked");
      }
    }, CommandButtonDisplayState.MEDIUM);
    shapesStrip2.add(layerSelectCmd);
    drawingMenu.addRibbonComponent(new JRibbonComponent(shapesStrip2));
    drawingMenu.setResizePolicies(MenuUtils.getStandardRestrictivePolicies(
        drawingMenu));
    return drawingMenu;
  }
  
  public void setShapesEnabled(boolean enable) {
  }

  private static JRibbonBand createDecorations(final Layers _theLayers,
      final PropertiesPanel _theProperties,
      final DebriefLiteToolParent _toolParent, final BoundsProvider bounds,
      final Layer decs)
  {
    final JRibbonBand chartfeaturesMenu = new JRibbonBand("Decorations", null);
    @SuppressWarnings("unused")
    final FlamingoCommand scaleCmd = MenuUtils.addCommand("Scale",
        "images/16/scale.png", new CreateScale(_toolParent, _theProperties,
            decs, _theLayers, bounds), chartfeaturesMenu, null);
    chartfeaturesMenu.startGroup("Time Marker");
    final JCommandButton tmaCmd = MenuUtils.addCommandButton("Absolute", null,
        new MenuUtils.TODOAction(), CommandButtonDisplayState.MEDIUM);
    chartfeaturesMenu.addRibbonComponent(new JRibbonComponent(tmaCmd));
    final JCommandButton tmrCmd = MenuUtils.addCommandButton("Relative", null,
        new MenuUtils.TODOAction(), CommandButtonDisplayState.MEDIUM);
    chartfeaturesMenu.addRibbonComponent(new JRibbonComponent(tmrCmd));
    chartfeaturesMenu.startGroup("Grid");
    final JCommandButton gridCmd = MenuUtils.addCommandButton("Grid",
        "images/16/grid.png", new CreateGrid(_toolParent, _theProperties, decs,
            _theLayers, bounds), CommandButtonDisplayState.MEDIUM);
    chartfeaturesMenu.addRibbonComponent(new JRibbonComponent(gridCmd));
    
    chartfeaturesMenu.setResizePolicies(MenuUtils
        .getStandardRestrictivePolicies(chartfeaturesMenu));
    return chartfeaturesMenu;
  }
}

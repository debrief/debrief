package org.mwc.debrief.lite.menu;

import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.mwc.debrief.lite.gui.DebriefLiteToolParent;
import org.mwc.debrief.lite.map.GeoToolMapRenderer;
import org.pushingpixels.flamingo.api.common.CommandButtonDisplayState;
import org.pushingpixels.flamingo.api.common.FlamingoCommand;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.common.icon.ImageWrapperResizableIcon;
import org.pushingpixels.flamingo.api.ribbon.JRibbon;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.JRibbonComponent;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;
import org.pushingpixels.flamingo.api.ribbon.RibbonTask;

import Debrief.Tools.Palette.CreateLabel;
import Debrief.Tools.Palette.CreateShape;
import Debrief.Tools.Palette.ListLayersDialog;
import Debrief.Wrappers.PolygonWrapper;
import Debrief.Wrappers.ShapeWrapper;
import MWC.GUI.BaseLayer;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Properties.DebriefColors;
import MWC.GUI.Properties.PropertiesPanel;
import MWC.GUI.Shapes.ArcShape;
import MWC.GUI.Shapes.CircleShape;
import MWC.GUI.Shapes.EllipseShape;
import MWC.GUI.Shapes.LineShape;
import MWC.GUI.Shapes.PlainShape;
import MWC.GUI.Shapes.PolygonShape;
import MWC.GUI.Shapes.RectangleShape;
import MWC.GUI.Shapes.PolygonShape.PolygonNode;
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
  
  final private static String[] layerItems = new String[] {"Select Layer","User-selected Layer"};
  private static String selectedLayer;
  private static ItemListener selectLayerItemListener;
  private static JComboBox<String> selectLayerCombo;
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

    final JRibbonBand layersMenu = createLayerMenu(_theLayers, _theProperties, _toolParent);
    
    final JRibbonBand drawingMenu = createShapes(_theLayers, _theProperties,
        _toolParent, bounds);
    
    final RibbonTask drawingTask = new RibbonTask("Insert", chartfeaturesMenu,
        referenceDataMenu, layersMenu, drawingMenu);
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
        RibbonElementPriority.TOP);
    @SuppressWarnings("unused")
    final FlamingoCommand naturalEarthCmd = MenuUtils.addCommand(
        "Natural Earth", "images/16/NaturalEarth.png", new CreateCoast(_toolParent,
            _theProperties, decs, _theLayers, bounds), referenceDataMenu,
        RibbonElementPriority.TOP);
    referenceDataMenu.setResizePolicies(MenuUtils
        .getStandardRestrictivePolicies(referenceDataMenu));
    return referenceDataMenu;
  }
  
  private static JRibbonBand createLayerMenu(final Layers _theLayers,
      final PropertiesPanel _theProperties,
      final DebriefLiteToolParent _toolParent) {
    final JRibbonBand layersMenu = new JRibbonBand("Active Layer", null);
    addDropDown(selectLayerItemListener,layersMenu,RibbonElementPriority.TOP,_theLayers);
    return layersMenu;
  }

  private static JRibbonBand createShapes(final Layers _theLayers,
      final PropertiesPanel _theProperties,
      final DebriefLiteToolParent _toolParent, final BoundsProvider bounds)
  {
    final JRibbonBand drawingMenu = new JRibbonBand("Shapes", null);
    final CreateShape ellipseShape = new CreateShape(_toolParent, _theProperties,
        _theLayers, "Ellipse", "images/ellipse_add.png", bounds)
    {
      @Override
      protected ShapeWrapper getShape(final WorldLocation centre)
      {
        return new ShapeWrapper("new ellipse", new EllipseShape(centre, 0,
            new WorldDistance(0, WorldDistance.DEGS), new WorldDistance(0,
                WorldDistance.DEGS)), DebriefColors.RED, null);
      }
    };
    ellipseShape.setSelectedLayerSource(selectLayerCombo);
    final JCommandButton ellipseShapeCmd = MenuUtils.addCommandButton("Ellipse",
        "images/16/ellipse.png", ellipseShape, CommandButtonDisplayState.MEDIUM, null);
    final CreateShape polygonShape = new CreateShape(_toolParent, _theProperties,
        _theLayers, "Polygon", "images/polygon_add.png", bounds)
    {
      @Override
      protected ShapeWrapper getShape(final WorldLocation centre)
      {       
        // create the shape, based on the centre
        final Vector<PolygonNode> path2 = new Vector<PolygonNode>();

        final PolygonShape newShape = new PolygonShape(path2);

        // and now wrap the shape
        final PolygonWrapper theWrapper = new PolygonWrapper("New polygon",
            newShape, PlainShape.DEFAULT_COLOR, null);

        // store the new point
        newShape.add(new PolygonNode("1", centre, (PolygonShape) theWrapper.getShape()));

        return theWrapper;
      }
    };
    polygonShape.setSelectedLayerSource(selectLayerCombo);
    final JCommandButton polygonCmd = MenuUtils.addCommandButton("Polygon",
        "images/16/polygon.png", polygonShape, CommandButtonDisplayState.MEDIUM, null);
    
    final CreateShape rectShape = new CreateShape(_toolParent, _theProperties,
        _theLayers, "Rectangle", "images/rectangle_add.png", bounds)
    {
      @Override
      protected ShapeWrapper getShape(final WorldLocation centre)
      {
        return new ShapeWrapper("new rectangle", new RectangleShape(centre,
            centre.add(new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(
                45), 0.05, 0))), DebriefColors.RED, null);
      }
    };
    rectShape.setSelectedLayerSource(selectLayerCombo);
    final JCommandButton rectCmd = MenuUtils.addCommandButton("Rectangle",
        "images/16/rectangle.png", rectShape, CommandButtonDisplayState.MEDIUM, null);
    
    
    final CreateShape circleShape = new CreateShape(_toolParent, _theProperties,
        _theLayers, "Circle", "images/circle_add.png", bounds)
    {
      @Override
      protected ShapeWrapper getShape(final WorldLocation centre)
      {
        return new ShapeWrapper("new circle", new CircleShape(centre, 4000),
            DebriefColors.RED, null);
      }
    };
    circleShape.setSelectedLayerSource(selectLayerCombo);
    final JCommandButton circleCmd = MenuUtils.addCommandButton("Circle",
        "images/16/circle.png", circleShape, CommandButtonDisplayState.MEDIUM, null);
    
    final CreateShape arcShape =  new CreateShape(_toolParent, _theProperties,
        _theLayers, "Arc", "images/arc_add.png", bounds)
    {
      @Override
      protected ShapeWrapper getShape(final WorldLocation centre)
      {
        return new ShapeWrapper("new arc", new ArcShape(centre,
            new WorldDistance(4000, WorldDistance.YARDS), 135, 90, true,
            false), DebriefColors.RED, null);
      }
    };
    arcShape.setSelectedLayerSource(selectLayerCombo);
    
    final JCommandButton arcCmd = MenuUtils.addCommandButton("Arc",
        "images/arc_add.png",arcShape, CommandButtonDisplayState.MEDIUM, null);
    
   
    
    final CreateShape lineShape = new CreateShape(_toolParent, _theProperties,
        _theLayers, "Line", "images/line_add.png", bounds)
    {
      @Override
      protected ShapeWrapper getShape(final WorldLocation centre)
      {
        return new ShapeWrapper("new line", new LineShape(centre, centre
            .add(new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(45.0),
                0.05, 0))), DebriefColors.RED, null);
      }
    };
    lineShape.setSelectedLayerSource(selectLayerCombo);
    
    final JCommandButton lineCmd = MenuUtils.addCommandButton("Line",
        "images/16/line.png", lineShape, CommandButtonDisplayState.MEDIUM, null);
    
    selectLayerItemListener = new ItemListener()
    {
      
      @Override
      public void itemStateChanged(ItemEvent e)
      {
        if(e.getStateChange() == ItemEvent.SELECTED)  
        {
          @SuppressWarnings("unchecked")
          JComboBox<String> jcombo = (JComboBox<String>)e.getSource();
          if(jcombo.getSelectedItem().equals(layerItems[1])) {
            //popup list layers dialog
            selectedLayer = getLayerName(_theLayers,true);
          }
          else {
            //set this as the layer to which shape is added.
            selectedLayer = (String)jcombo.getSelectedItem();
          }
        }
        
      }
    };
    drawingMenu.startGroup();
    final CreateLabel createLabelShape =  new CreateLabel(_toolParent, _theProperties,
        _theLayers, bounds, "New Label", "icons/24/label_add.png") ;
    createLabelShape.setSelectedLayerSource(selectLayerCombo);
    MenuUtils.addCommand(
        "Label",
        "icons/24/label_add.png",createLabelShape,
            drawingMenu,RibbonElementPriority.TOP);
    drawingMenu.startGroup();
    drawingMenu.addRibbonComponent(new JRibbonComponent(polygonCmd));
    drawingMenu.addRibbonComponent(new JRibbonComponent(ellipseShapeCmd));
    drawingMenu.addRibbonComponent(new JRibbonComponent(rectCmd));
    drawingMenu.addRibbonComponent(new JRibbonComponent(circleCmd));
    drawingMenu.addRibbonComponent(new JRibbonComponent(lineCmd));
    drawingMenu.addRibbonComponent(new JRibbonComponent(arcCmd));
    
    drawingMenu.setResizePolicies(MenuUtils.getStandardRestrictivePolicies(
        drawingMenu));
    return drawingMenu;
  }
  
  private static JRibbonComponent addDropDown(final ItemListener actionToAdd,
      final JRibbonBand mapBand, final RibbonElementPriority priority,final Layers theLayers)
  {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    selectLayerCombo = new JComboBox<String>(layerItems);
    selectLayerCombo.addItemListener(actionToAdd);
    selectLayerCombo.addFocusListener(new FocusAdapter()
    {
      @Override
      public void focusGained(FocusEvent e)
      {
        if(e.getSource() instanceof JComboBox) {
          String[] layers = theLayers.trimmedLayers(false);         
          @SuppressWarnings("unchecked")
          JComboBox<String> jcombo = (JComboBox<String>)e.getSource();
          String selectedItem = (String)jcombo.getSelectedItem();
          System.out.println("Selected item:"+selectedItem);
          jcombo.removeAllItems();
          for(String layer:layers) {
            jcombo.addItem(layer);
          }
          jcombo.addItem(layerItems[1]);
          //remove listener
          jcombo.removeItemListener(selectLayerItemListener);
          if(selectedItem!=null) {
            jcombo.setSelectedItem(selectedItem);
          }
          //add it back
          jcombo.addItemListener(selectLayerItemListener);
        }
      }
    });
    
    panel.add(selectLayerCombo);
    final Image activeLayerImg = MenuUtils.createImage("icons/16/layer_mgr.png");
    ImageWrapperResizableIcon imageIcon = ImageWrapperResizableIcon.getIcon(activeLayerImg, MenuUtils.ICON_SIZE_16);
    JRibbonComponent component = new JRibbonComponent(imageIcon,"",panel);
    component.setDisplayPriority(priority);
    mapBand.addRibbonComponent(component);
    return component;
    
  }
  
  public void setShapesEnabled(boolean enable) {
  }
  
  private static String getLayerName(Layers theLayers,boolean addNewLayerCmd) {
    String[] layers = theLayers.trimmedLayers(addNewLayerCmd);
    ListLayersDialog listDialog = new ListLayersDialog(layers);
    listDialog.setSize(350,300);
    listDialog.setLocationRelativeTo(null);
    listDialog.setModal(true);
    listDialog.setVisible(true);
    String selection = listDialog.getSelectedItem();
    // did user say yes?
    String res = null;
    if (selection != null)
    {
      // hmm, is it our add layer command?
      if (selection.equals(Layers.NEW_LAYER_COMMAND))
      {
        // better create one. Ask the user

        // create input box dialog
        String txt = JOptionPane.showInputDialog(null, "Enter name for new layer");
        // check there's something there
        if (!txt.isEmpty())
        {
          res = txt;
          // create base layer
          final Layer newLayer = new BaseLayer();
          newLayer.setName(res);

          // add to layers object
          theLayers.addThisLayer(newLayer);
          selectedLayer = res;
          selectLayerCombo.setEditable(true);
          selectLayerCombo.insertItemAt(selectedLayer, selectLayerCombo.getItemCount()-1);
          selectLayerCombo.setSelectedItem(selectedLayer);
          selectLayerCombo.setEditable(false);
        }
        else
        {
          res = null;
        }
      }
      else {
        res = selection;
      }
  
    }
    
    return res;
  }
  
  public static final String getSelectedLayer() {
    return (String)selectLayerCombo.getSelectedItem();
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
    @SuppressWarnings("unused")
    final FlamingoCommand gridCmd = MenuUtils.addCommand("Grid",
        "images/16/grid.png", new CreateGrid(_toolParent, _theProperties,
            decs, _theLayers, bounds), chartfeaturesMenu, null);
    
    chartfeaturesMenu.setResizePolicies(MenuUtils
        .getStandardRestrictivePolicies(chartfeaturesMenu));
    return chartfeaturesMenu;
  }
}

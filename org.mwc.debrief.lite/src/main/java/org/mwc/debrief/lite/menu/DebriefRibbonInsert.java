package org.mwc.debrief.lite.menu;

import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.geotools.geometry.jts.ReferencedEnvelope;
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

import Debrief.Tools.Palette.CoreCreateShape;
import Debrief.Tools.Palette.CreateLabel;
import Debrief.Tools.Palette.CreateShape;
import Debrief.Wrappers.PolygonWrapper;
import Debrief.Wrappers.ShapeWrapper;
import MWC.GUI.BaseLayer;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.ToolParent;
import MWC.GUI.Properties.DebriefColors;
import MWC.GUI.Properties.PropertiesPanel;
import MWC.GUI.Shapes.ArcShape;
import MWC.GUI.Shapes.CircleShape;
import MWC.GUI.Shapes.EllipseShape;
import MWC.GUI.Shapes.LineShape;
import MWC.GUI.Shapes.PlainShape;
import MWC.GUI.Shapes.PolygonShape;
import MWC.GUI.Shapes.PolygonShape.PolygonNode;
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
  
  private static String selectedLayer;
  private static ItemListener selectLayerItemListener;
  private static JComboBox<String> selectLayerCombo;
  protected static void addInsertTab(final JRibbon ribbon,
      final GeoToolMapRenderer geoMapRenderer, final Layers theLayers,
      final PropertiesPanel theProperties,
      final ToolParent toolParent)
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
        return theLayers.getBounds();
      }

      @Override
      public WorldArea getViewport()
      {
        final ReferencedEnvelope env = geoMapRenderer.getMapComponent()
            .getViewport().getBounds();
        final WorldLocation tl = new WorldLocation(env.getMaxY(), env.getMinX(),
            0);
        final WorldLocation br = new WorldLocation(env.getMinY(), env.getMaxX(),
            0);
        final WorldArea res = new WorldArea(tl, br);
        return res;
      }
    };

    final Layer decs = theLayers.findLayer(Layers.CHART_FEATURES);
    final JRibbonBand chartfeaturesMenu = createDecorations(theLayers,
        theProperties, toolParent, bounds, decs);
    
    final JRibbonBand referenceDataMenu = createReferenceData(theLayers,
        theProperties, toolParent, bounds, decs);

    final JRibbonBand layersMenu = createLayerMenu(theLayers);
    
    final JRibbonBand drawingMenu = createShapes(theLayers, theProperties,
        toolParent, bounds);
    
    final RibbonTask drawingTask = new RibbonTask("Insert", chartfeaturesMenu,
        referenceDataMenu, layersMenu, drawingMenu);
    ribbon.addTask(drawingTask);
  }

  private static JRibbonBand createReferenceData(final Layers _theLayers,
      final PropertiesPanel _theProperties,
      final ToolParent _toolParent, final BoundsProvider bounds,
      final Layer decs)
  {
    final JRibbonBand referenceDataMenu = new JRibbonBand("Reference Data",
        null);
    @SuppressWarnings("unused")
    final FlamingoCommand coastlineCmd = MenuUtils.addCommand("Coastline",
        "icons/24/coast_add.png", new CreateCoast(_toolParent, _theProperties,
            decs, _theLayers, bounds), referenceDataMenu,
        RibbonElementPriority.TOP);
    @SuppressWarnings("unused")
    final FlamingoCommand naturalEarthCmd = MenuUtils.addCommand(
        "Natural Earth", "icons/24/NaturalEarth.png", new CreateCoast(_toolParent,
            _theProperties, decs, _theLayers, bounds), referenceDataMenu,
        RibbonElementPriority.TOP);
    referenceDataMenu.setResizePolicies(MenuUtils
        .getStandardRestrictivePolicies(referenceDataMenu));
    return referenceDataMenu;
  }
  
  private static JRibbonBand createLayerMenu(final Layers _theLayers) {
    final JRibbonBand layersMenu = new JRibbonBand("Active Layer", null);
    addDropDown(selectLayerItemListener,layersMenu,RibbonElementPriority.TOP,_theLayers);
    return layersMenu;
  }

  private static JRibbonBand createShapes(final Layers _theLayers,
      final PropertiesPanel _theProperties,
      final ToolParent _toolParent, final BoundsProvider bounds)
  {
    final JRibbonBand drawingMenu = new JRibbonBand("Shapes", null);
    final CreateShape ellipseShape = new CreateShape(_toolParent, _theProperties,
        _theLayers, "Ellipse", "icons/ellipse_add.png", bounds)
    {
      @Override
      protected ShapeWrapper getShape(final WorldLocation centre)
      {
        return new ShapeWrapper("new ellipse", new EllipseShape(centre, 30,
            new WorldDistance(5, WorldDistance.KM), new WorldDistance(3,
                WorldDistance.KM)), DebriefColors.RED, null);
      }
    };
    ellipseShape.setSelectedLayerSource(selectLayerCombo);
    final JCommandButton ellipseShapeCmd = MenuUtils.addCommandButton("Ellipse",
        "icons/16/ellipse.png", ellipseShape, CommandButtonDisplayState.MEDIUM, null);
    final CreateShape polygonShape = new CreateShape(_toolParent, _theProperties,
        _theLayers, "Polygon", "icons/polygon_add.png", bounds)
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
        
        // and other node
        final WorldLocation newLoc = centre.add(new WorldVector(Math.PI/4, new WorldDistance(10, WorldDistance.KM),  
            new WorldDistance(0, WorldDistance.KM)));
        newShape.add(new PolygonNode("2", newLoc, (PolygonShape) theWrapper.getShape()));

        final WorldLocation newLoc2 = centre.add(new WorldVector(7 * Math.PI/4, new WorldDistance(10, WorldDistance.KM),  
            new WorldDistance(0, WorldDistance.KM)));
        newShape.add(new PolygonNode("3", newLoc2, (PolygonShape) theWrapper.getShape()));

        return theWrapper;
      }
    };
    polygonShape.setSelectedLayerSource(selectLayerCombo);
    final JCommandButton polygonCmd = MenuUtils.addCommandButton("Polygon",
        "icons/16/polygon.png", polygonShape, CommandButtonDisplayState.MEDIUM, null);
    
    final CreateShape rectShape = new CreateShape(_toolParent, _theProperties,
        _theLayers, "Rectangle", "icons/rectangle_add.png", bounds)
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
        "icons/16/rectangle.png", rectShape, CommandButtonDisplayState.MEDIUM, null);
    
    
    final CreateShape circleShape = new CreateShape(_toolParent, _theProperties,
        _theLayers, "Circle", "icons/circle_add.png", bounds)
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
        "icons/16/circle.png", circleShape, CommandButtonDisplayState.MEDIUM, null);
    
    final CreateShape arcShape =  new CreateShape(_toolParent, _theProperties,
        _theLayers, "Arc", "icons/16/circle.png", bounds)
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
        "icons/16/arc_add.png",arcShape, CommandButtonDisplayState.MEDIUM, null);

    final CreateShape lineShape = new CreateShape(_toolParent, _theProperties,
        _theLayers, "Line", "icons/16/line_add.png", bounds)
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
        "icons/16/line.png", lineShape, CommandButtonDisplayState.MEDIUM, null);
    
    selectLayerItemListener = new ItemListener()
    {
      
      @Override
      public void itemStateChanged(ItemEvent e)
      {
        String previousSelection = selectedLayer;
        if(e.getStateChange() == ItemEvent.SELECTED)  
        {
          @SuppressWarnings("unchecked")
          JComboBox<String> jcombo = (JComboBox<String>)e.getSource();
          if(jcombo.getSelectedItem().equals(Layers.NEW_LAYER_COMMAND)) {
            //popup list layers dialog
            String res = getLayerName(_theLayers);
            if(res == null) {
              jcombo.setSelectedItem(previousSelection);
            }
          }
          else {
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
   
    
    final DefaultComboBoxModel<String>  selectLayerModel = new DefaultComboBoxModel<String>();
    selectLayerModel.addElement(CoreCreateShape.USER_SELECTED_LAYER_COMMAND);
    selectLayerCombo = new JComboBox<String>(selectLayerModel);
    selectLayerCombo.addItemListener(actionToAdd);
   
    selectLayerCombo.addFocusListener(new FocusAdapter()
    {
      @Override
      public void focusLost(FocusEvent e)
      {
        selectLayerModel.removeAllElements();
        selectLayerModel.addElement(CoreCreateShape.USER_SELECTED_LAYER_COMMAND);
      }
      @Override
      public void focusGained(FocusEvent e)
      {
        if(e.getSource() instanceof JComboBox) {
          
          EventQueue.invokeLater(new Runnable()
          {
            
            @Override
            public void run()
            {
              String[] layers = theLayers.trimmedLayers();         
              
              String selectedItem = (String)selectLayerCombo.getSelectedItem();
              selectLayerModel.removeAllElements();

              // start off with our custom layer modes
              selectLayerModel.addElement(CoreCreateShape.USER_SELECTED_LAYER_COMMAND);
              
              // now the list of trimmed layers (which includes `Add layer`)
              for(String layer:layers) {
                selectLayerModel.addElement(layer);
              }
              
              boolean popupVisible = selectLayerCombo.isPopupVisible();
              selectLayerCombo.updateUI();
              if(popupVisible && !selectLayerCombo.isPopupVisible()) {
                selectLayerCombo.showPopup();
              }
              
              //remove listener - so it doesn't get triggered when
              // we set default value
              selectLayerCombo.removeItemListener(selectLayerItemListener);
              
              // if we know selection, assign it
              if(selectedItem!=null) {
                selectLayerCombo.setSelectedItem(selectedItem);
              }
             
              // reinstate listener
              selectLayerCombo.addItemListener(selectLayerItemListener);
              
            }
          });
          
        }
      }
    });
    
    panel.add(selectLayerCombo);
    final Image activeLayerImg = MenuUtils.createImage("icons/24/auto_layer.png");
    ImageWrapperResizableIcon imageIcon = ImageWrapperResizableIcon.getIcon(activeLayerImg, MenuUtils.ICON_SIZE_24);
    JRibbonComponent component = new JRibbonComponent(imageIcon,"",panel);
    component.setDisplayPriority(priority);
    mapBand.addRibbonComponent(component);
    return component;
    
  }
  
  public void setShapesEnabled(boolean enable) {
  }
  
  private static String getLayerName(Layers theLayers) {
            // create input box dialog
    String res = null;
    
    final String txt = JOptionPane.showInputDialog(null, "Please enter name",
        "New Layer", JOptionPane.QUESTION_MESSAGE);

    // check there's something there
    if (txt!=null && !txt.isEmpty())
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
    return res;
  }
  
  public static final String getSelectedLayer() {
    return (String)selectLayerCombo.getSelectedItem();
  }

  private static JRibbonBand createDecorations(final Layers _theLayers,
      final PropertiesPanel _theProperties,
      final ToolParent _toolParent, final BoundsProvider bounds,
      final Layer decs)
  {
    final JRibbonBand chartfeaturesMenu = new JRibbonBand("Decorations", null);
    @SuppressWarnings("unused")
    final FlamingoCommand scaleCmd = MenuUtils.addCommand("Scale",
        "icons/24/scale_add.png", new CreateScale(_toolParent, _theProperties,
            decs, _theLayers, bounds), chartfeaturesMenu, null);
    @SuppressWarnings("unused")
    final FlamingoCommand gridCmd = MenuUtils.addCommand("Grid",
        "icons/24/grid_add.png", new CreateGrid(_toolParent, _theProperties,
            decs, _theLayers, bounds), chartfeaturesMenu, null);
    
    chartfeaturesMenu.setResizePolicies(MenuUtils
        .getStandardRestrictivePolicies(chartfeaturesMenu));
    return chartfeaturesMenu;
  }
}
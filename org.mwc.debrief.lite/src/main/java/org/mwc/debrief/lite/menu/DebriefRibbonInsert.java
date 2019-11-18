package org.mwc.debrief.lite.menu;

import java.awt.Image;
import java.util.Arrays;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.mwc.debrief.lite.map.GeoToolMapRenderer;
import org.mwc.debrief.lite.shapes.actions.ArcShapeCommandAction;
import org.mwc.debrief.lite.shapes.actions.CircleShapeCommandAction;
import org.mwc.debrief.lite.shapes.actions.CoastCommandAction;
import org.mwc.debrief.lite.shapes.actions.CreateGridCommandAction;
import org.mwc.debrief.lite.shapes.actions.CreateScaleCommandAction;
import org.mwc.debrief.lite.shapes.actions.EllipseShapeCommandAction;
import org.mwc.debrief.lite.shapes.actions.LabelShapeCommandAction;
import org.mwc.debrief.lite.shapes.actions.LineShapeCommandAction;
import org.mwc.debrief.lite.shapes.actions.RectangularShapeCommandAction;
import org.mwc.debrief.lite.util.ResizableIconFactory;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.operation.TransformException;
import org.pushingpixels.flamingo.api.common.RichTooltip;
import org.pushingpixels.flamingo.api.common.icon.ImageWrapperResizableIcon;
import org.pushingpixels.flamingo.api.ribbon.JRibbon;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand.PresentationPriority;
import org.pushingpixels.flamingo.api.ribbon.RibbonTask;
import org.pushingpixels.flamingo.api.ribbon.synapse.model.ComponentPresentationModel;
import org.pushingpixels.flamingo.api.ribbon.synapse.model.RibbonComboBoxContentModel;
import org.pushingpixels.flamingo.api.ribbon.synapse.model.RibbonDefaultComboBoxContentModel;
import org.pushingpixels.flamingo.api.ribbon.synapse.projection.RibbonComboBoxProjection;

import Debrief.GUI.Frames.Application;
import Debrief.Tools.Palette.CoreCreateShape;
import MWC.GUI.BaseLayer;
import MWC.GUI.DataListenerAdaptor;
import MWC.GUI.HasEditables;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Layers.DataListener;
import MWC.GUI.Plottable;
import MWC.GUI.ToolParent;
import MWC.GUI.Properties.PropertiesPanel;
import MWC.GUI.Tools.Action;
import MWC.GUI.Tools.PlainTool.BoundsProvider;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

public class DebriefRibbonInsert
{
  
  private static String selectedLayer;
  private static JComboBox<String> selectLayerCombo;
  private static final DataListener _listenForMods = new DataListenerAdaptor()
  {
    @Override
    public void dataExtended(final Layers theData, final Plottable newItem,
        final HasEditables parent)
    {
      //layer was added
      if(newItem == null)
      {
        updateLayers(theData,parent);
      }
      
    }
  };
  protected static void addInsertTab(final JRibbon ribbon,
      final GeoToolMapRenderer geoMapRenderer, final Layers theLayers,
      final PropertiesPanel theProperties,
      final ToolParent toolParent)
  {
    theLayers.addDataExtendedListener(_listenForMods);
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
        WorldArea res = null;

        final ReferencedEnvelope env = geoMapRenderer.getMapComponent()
            .getViewport().getBounds();
        
        // convert to degs
        DirectPosition2D tl = (DirectPosition2D) env.getUpperCorner();
        DirectPosition2D br = (DirectPosition2D) env.getLowerCorner();
        try
        {
          geoMapRenderer.getTransform().transform(tl, tl);
          geoMapRenderer.getTransform().transform(br, br);
          final WorldLocation tlD = new WorldLocation(tl.getY(), tl.getX(),
              0);
          final WorldLocation brD = new WorldLocation(br.getY(), br.getX(),
              0);
          res = new WorldArea(tlD, brD);
        }
        catch (MismatchedDimensionException | TransformException e)
        {
          Application.logError2(ToolParent.ERROR,
              "Failed to convert from metres proj to degs",
              null);
        }
        return res;
      }
    };

    final JRibbonBand chartfeaturesMenu = createDecorations(theLayers,
        theProperties, toolParent, bounds);
    
    final JRibbonBand referenceDataMenu = createReferenceData(theLayers,
        theProperties, toolParent, bounds);

    final JRibbonBand layersMenu = createLayerMenu(theLayers,toolParent);
    
    final JRibbonBand drawingMenu = createShapes(theLayers, theProperties,
        toolParent, bounds);
    
    /** temporarily drop reference data
     * 
     */
//    final RibbonTask drawingTask = new RibbonTask("Insert", chartfeaturesMenu,
//        referenceDataMenu, layersMenu, drawingMenu);
    final RibbonTask drawingTask = new RibbonTask("Insert", chartfeaturesMenu,
         layersMenu, drawingMenu);
    ribbon.addTask(drawingTask);
  }

  protected static void updateLayers(Layers theData, HasEditables parent)
  {
    if(selectLayerCombo!=null) {
      List<String> items = Arrays.asList(theData.trimmedLayers());
      DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>)selectLayerCombo.getModel();
      selectedLayer = model.getSize()>1?(String)model.getSelectedItem():null;
      model.removeAllElements();
      model.addElement(CoreCreateShape.USER_SELECTED_LAYER_COMMAND);
      items.forEach(item->model.addElement(item));
      if(selectedLayer!=null&&!Layers.NEW_LAYER_COMMAND.equalsIgnoreCase(selectedLayer))
      {
        model.setSelectedItem(selectedLayer);
      }
      
      boolean popupVisible = selectLayerCombo.isPopupVisible();
      selectLayerCombo.updateUI();
      if(popupVisible && !selectLayerCombo.isPopupVisible()) {
        selectLayerCombo.showPopup();
      }
      
    }
    
  }

  private static JRibbonBand createReferenceData(final Layers theLayers,
      final PropertiesPanel theProperties,
      final ToolParent toolParent, final BoundsProvider bounds)
  {
    final JRibbonBand referenceDataMenu = new JRibbonBand("Reference Data",
        null);
    MenuUtils.addCommand("Coastline",
        "icons/24/coast_add.png", new CoastCommandAction(toolParent, theProperties,
             theLayers, bounds), referenceDataMenu,
        PresentationPriority.TOP);
    MenuUtils.addCommand(
        "Natural Earth", "icons/24/NaturalEarth.png", new CoastCommandAction(toolParent,
            theProperties, theLayers, bounds), referenceDataMenu,
        PresentationPriority.TOP);
    referenceDataMenu.setResizePolicies(MenuUtils
        .getStandardRestrictivePolicies(referenceDataMenu));
    return referenceDataMenu;
  }
  
  private static JRibbonBand createLayerMenu(final Layers _theLayers,final ToolParent toolParent) {
    final JRibbonBand layersMenu = new JRibbonBand("Active Layer", null);
    RibbonComboBoxProjection projection = new RibbonComboBoxProjection(addDropDown(layersMenu,PresentationPriority.TOP,_theLayers,toolParent),ComponentPresentationModel.withDefaults());
    layersMenu.addRibbonComponent(projection);
    selectLayerCombo = projection.buildComponent();
    selectLayerCombo.setName("select-layer-combo");
    return layersMenu;
  }

  private static JRibbonBand createShapes(final Layers theLayers,
      final PropertiesPanel theProperties,
      final ToolParent toolParent, final BoundsProvider bounds)
  {
    final JRibbonBand drawingMenu = new JRibbonBand("Shapes", null);
    final EllipseShapeCommandAction ellipseShape = new EllipseShapeCommandAction(toolParent, theProperties,
        theLayers, "Ellipse", "icons/ellipse_add.png", bounds);
    
    ellipseShape.setSelectedLayerSource(selectLayerCombo);
    
    
    /**
     * to be added later on
     */
    /*final CreateShape polygonShape = new CreateShape(toolParent, theProperties,
        theLayers, "Polygon", "icons/polygon_add.png", bounds)
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
    @SuppressWarnings("unused")
//    final JCommandButton polygonCmd = MenuUtils.addCommandButton("Polygon",
//        "icons/16/polygon.png", polygonShape, CommandButtonDisplayState.MEDIUM, null);
    */
    final RectangularShapeCommandAction rectShape = new RectangularShapeCommandAction(toolParent, theProperties,
        theLayers, "Rectangle", "icons/rectangle_add.png", bounds);
    rectShape.setSelectedLayerSource(selectLayerCombo);
    final CircleShapeCommandAction circleShape = new CircleShapeCommandAction(toolParent, theProperties,
        theLayers, "Circle", "icons/circle_add.png", bounds);
    circleShape.setSelectedLayerSource(selectLayerCombo);
    final ArcShapeCommandAction arcShape =  new ArcShapeCommandAction(toolParent, theProperties,
        theLayers, "Arc", "icons/16/circle.png", bounds);
    
    arcShape.setSelectedLayerSource(selectLayerCombo);

    final LineShapeCommandAction lineShape = new LineShapeCommandAction(toolParent, theProperties,
        theLayers, "Line", "icons/16/line_add.png", bounds);
    lineShape.setSelectedLayerSource(selectLayerCombo);
   
    MenuUtils.addCommand("Ellipse","icons/16/ellipse.png",ellipseShape,drawingMenu,PresentationPriority.MEDIUM);
    MenuUtils.addCommand("Rectangle","icons/16/rectangle.png",rectShape,drawingMenu,PresentationPriority.MEDIUM);
    MenuUtils.addCommand("Circle","icons/16/circle.png",circleShape,drawingMenu,PresentationPriority.MEDIUM);
    MenuUtils.addCommand("Arc","icons/16/arc_add.png",arcShape,drawingMenu,PresentationPriority.MEDIUM);
    MenuUtils.addCommand("Line","icons/16/line.png",lineShape,drawingMenu,PresentationPriority.MEDIUM);
    
    
    drawingMenu.startGroup();
    final LabelShapeCommandAction createLabelShape =  new LabelShapeCommandAction(toolParent, theProperties,
        theLayers, bounds, "New Label", "icons/24/label_add.png") ;
    createLabelShape.setSelectedLayerSource(selectLayerCombo);
    MenuUtils.addCommand(
        "Label",
        "icons/24/label_add.png",createLabelShape,
            drawingMenu,PresentationPriority.TOP);
    drawingMenu.startGroup();
    
//    #4201, dont add polygon shape.
//    drawingMenu.addRibbonComponent(new JRibbonComponent(polygonCmd));
    
    drawingMenu.setResizePolicies(MenuUtils.getStandardRestrictivePolicies(
        drawingMenu));
    return drawingMenu;
  }
  
  private static RibbonComboBoxContentModel<String> addDropDown(
      final JRibbonBand mapBand, final PresentationPriority priority,final Layers theLayers,ToolParent toolParent)
  {
    
    RibbonComboBoxContentModel<String> selectLayerModel;

    final Image activeLayerImg = MenuUtils.createImage("icons/24/auto_layer.png");
    ImageWrapperResizableIcon imageIcon = ImageWrapperResizableIcon.getIcon(activeLayerImg, MenuUtils.ICON_SIZE_24);
    
    selectLayerModel = RibbonDefaultComboBoxContentModel.<String>builder()
        
        .setRichTooltip(RichTooltip.builder().setTitle("Select Layer")
                        .build())
        .setIconFactory(ResizableIconFactory.factory(imageIcon))
        .setItems(new String[] {CoreCreateShape.USER_SELECTED_LAYER_COMMAND,"test"})
        .build();
    selectLayerModel.addListDataListener(new ListDataListener() {
      Object selected = selectLayerModel.getSelectedItem();
      @Override
      public void contentsChanged(ListDataEvent e) {
          Object newSelection = selectLayerModel.getSelectedItem();
          if (this.selected != newSelection) {
              this.selected = newSelection;
              RibbonDefaultComboBoxContentModel<String> jcombo = (RibbonDefaultComboBoxContentModel<String>)e.getSource();
              if(jcombo.getSelectedItem().equals(Layers.NEW_LAYER_COMMAND)) {
                //popup list layers dialog
                final String layerName = getLayerName(theLayers);

                // sort out the action
                final AddLayerAction addLayerAction = new AddLayerAction(theLayers,
                    layerName);
                addLayerAction.execute();

                // remember it
                toolParent.addActionToBuffer(addLayerAction);
              }
              else
              {
                selectedLayer = (String)jcombo.getSelectedItem();
              }
          }
      }
      @Override
      public void intervalAdded(ListDataEvent e)
      {
      }
      @Override
      public void intervalRemoved(ListDataEvent e)
      {
      }
    });
    
    
    
    
//    panel.add(selectLayerCombo);
//    final Image activeLayerImg = MenuUtils.createImage("icons/24/auto_layer.png");
//    ImageWrapperResizableIcon imageIcon = ImageWrapperResizableIcon.getIcon(activeLayerImg, MenuUtils.ICON_SIZE_24);
//    JRibbonComponent component = new JRibbonComponent(imageIcon,"",panel);
//    component.setPresentationPriority(priority);
//    mapBand.addRibbonComponent(component);
//    return component;
    //return null;
    return selectLayerModel;
  }

  private static class AddLayerAction implements Action
  {
    private final String _layerName;
    private final Layers _theLayers;
    private Layer _theLayer;

    public AddLayerAction(final Layers theLayers, final String layerName)
    {
      _theLayers = theLayers;
      _layerName = layerName;
    }
    @Override
    public boolean isUndoable()
    {
      return true;
    }

    @Override
    public boolean isRedoable()
    {
      return true;
    }

    @Override
    public void undo()
    {
      _theLayer = _theLayers.findLayer(_layerName);
      _theLayers.removeThisLayer(_theLayer);
    }

    @Override
    public void execute()
    {
      
      if(_theLayer == null) {
      _theLayer = new BaseLayer();
      _theLayer.setName(_layerName);
      }
      _theLayers.removeDataExtendedListener(_listenForMods);
      _theLayers.addThisLayer(_theLayer);
      _theLayers.addDataExtendedListener(_listenForMods);
    }
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

  private static JRibbonBand createDecorations(final Layers theLayers,
      final PropertiesPanel theProperties,
      final ToolParent toolParent, final BoundsProvider bounds)
  {
    final JRibbonBand chartfeaturesMenu = new JRibbonBand("Decorations", null);
    MenuUtils.addCommand("Scale",
        "icons/24/scale_add.png", new CreateScaleCommandAction(toolParent, theProperties,
            theLayers, bounds), chartfeaturesMenu, null);
    MenuUtils.addCommand("Grid",
        "icons/24/grid_add.png", new CreateGridCommandAction(toolParent, theProperties,
            theLayers, bounds), chartfeaturesMenu, null);
    
    chartfeaturesMenu.setResizePolicies(MenuUtils
        .getStandardRestrictivePolicies(chartfeaturesMenu));
    return chartfeaturesMenu;
  }
}
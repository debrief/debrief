/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package ASSET.GUI.Core;

import ASSET.Scenario.CoreScenario;
import ASSET.ScenarioType;
import ASSET.Server.CoreServer;
import MWC.GUI.*;
import MWC.GUI.Properties.PropertiesPanel;
import MWC.GUI.Tools.Chart.*;
import MWC.GUI.Tools.MenuItemInfo;
import MWC.GUI.Tools.Operations.RightClickCutCopyAdaptor;
import MWC.GUI.Tools.Operations.ShowLayers;
import MWC.GUI.Tools.Operations.ShowVideo;
import MWC.GUI.Tools.Palette.*;
import MWC.GUI.Tools.PlainTool.BoundsProvider;
import MWC.GUI.Undo.UndoBuffer;
import MWC.GenericData.WorldArea;

import java.util.Enumeration;
import java.util.Vector;


abstract public class CoreGUI
{
  ///////////////////////////////////////////////
  // member variables
  ///////////////////////////////////////////////

  /**
   * the parent class
   */
  protected final CoreGUISwing.ASSETParent _theParent;

  /**
   * the name of this application
   */
  private String _name = "blank";

  /**
   * the list of tools we use
   */
  protected final Vector<MenuItemInfo> _theTools;

  /**
   * the chart we plot
   */
  private PlainChart _theChart;

  /**
   * the toolbar we show
   */
  protected Toolbar _theToolbar;

  /**
   * our property editor
   */
  protected PropertiesPanel _theProperties;

  /**
   * our status bar
   */
  protected StatusBar _theStatusBar;

  /**
   * our layers object
   */
  protected Layers _theData;

  /**
   * our undo buffer
   */
  private transient UndoBuffer _theBuffer = new UndoBuffer();

  /**
   * the cut/copy adaptor.  we keep a handle to this so that we can explicitly clear it when we close
   * - this is to overcome an UndoBuffer memory leak
   */
  private RightClickCutCopyAdaptor _rightClicker;

  /**
   * the scenario we manage
   */
  protected ASSET.Scenario.CoreScenario _theScenario;

  /**
   * the server which contains our scenario
   */
  private ASSET.ServerType _theServer;

  ///////////////////////////////////////////////
  // constructor
  ///////////////////////////////////////////////
  /**
   * constructor for this class
   *
   * @param theParent a parent class which will show busy cursors
   */
  CoreGUI(final CoreGUISwing.ASSETParent theParent)
  {
    _theData = new Layers();


    _theServer = new CoreServer();
    final int index = _theServer.createNewScenario("");
    _theScenario = (CoreScenario) _theServer.getThisScenario(index);
    _theScenario.setName("Un-named");

    _name = "ASSET";
    _theParent = theParent;
    _theTools = new Vector<MenuItemInfo>(0, 1);

    // add the chart features object
    final BaseLayer chartFeatures = new BaseLayer();
    chartFeatures.setName("Chart Features");
    chartFeatures.setBuffered(true);
    _theData.addThisLayer(chartFeatures);

    // have a go at inserting the default layers
    // start with the chart
    final BaseLayer decs = new BaseLayer();
    decs.setName("Decs");
    _theData.addThisLayer(decs);

    // allow any child classes to initialise their data
    constructorSteps();

  }

  ///////////////////////////////////////////////
  // member functions
  ///////////////////////////////////////////////

  /**
   * extra call to allow any CoreGUI-level components the be initialised before we
   * start creating the GUI components
   */
  abstract protected void constructorSteps();


  /**
   * member method to create a toolbar button for this tool, it is intended
   * that this class be overridden within more able GUI environments
   * (in Swing we should have a tabbed interface)
   *
   * @param item the description of this tool
   */
  void addThisTool(final MenuItemInfo item)
  {

    // see if this is an action button, or it toggles as part of a group
    if (item.getToggleGroup() == null)
    {
      _theToolbar.addTool(item.getTool(),
                          item.getShortCut(),
                          item.getMnemonic());
    }
    else
    {
      _theToolbar.addToggleTool(item.getMenuName(),
                                item.getTool(),
                                item.getShortCut(),
                                item.getMnemonic());
    }
  }


  /** method to handle files being dropped into the application
   *
   */
  //  protected void processDroppedFiles(final Vector files)
  //  {
  //    for (int i = 0; i < files.size(); i++)
  //    {
  //      final java.io.File thisFile = (java.io.File) files.elementAt(i);
  //      try
  //      {
  //        // import this data
  //        ASSETReaderWriter.importThis(_theScenario, thisFile.getName(), new java.io.FileInputStream(thisFile));
  //      }
  //      catch(java.io.FileNotFoundException fe)
  //      {
  //        fe.printStackTrace();
  //      }
  //
  //    }
  //  }

  /**
   * <code>buildTheInterface</code> puts the bits together, after the gui has been built
   */
  void buildTheInterface()
  {


    // we've had to do this here, so that we know we've foudn the chart
    addTools();

    // retrieve the tools for this interface
    final Enumeration<MenuItemInfo> enumer = getTools();


    while (enumer.hasMoreElements())
    {
      final MenuItemInfo thisItem = (MenuItemInfo) enumer.nextElement();

      addThisTool(thisItem);
    }

    // setup our double-click editor
    // and add our dbl click listener
    getChart().addCursorDblClickedListener(new DblClickEdit(getProperties()));

    // create our right click editor, and add it's helpers
    final RightClickEdit rc = new RightClickEdit(getProperties());
    rc.addMenuCreator(new MWC.Algorithms.Editors.ProjectionEditPopupMenuAdaptor());
    rc.addMenuCreator(new MWC.GUI.LayerManager.EditLayersPopupMenuAdaptor());
    rc.addMenuCreator(new MWC.GUI.Canvas.EditCanvasPopupMenuAdaptor(getChart().getCanvas()));

    // we also want to try to give these properties to the layers object, so that it can be edited
    // properly by the right-clicking in the Layer Manager
    _theData.setEditor(rc);

    // and add our right-click editor
    getChart().addRightClickListener(rc);

    // do the rest of our building
    addUniqueTools();


  }


  /**
   * return the tools we have created
   */
  private Enumeration<MenuItemInfo> getTools()
  {
    return _theTools.elements();
  }

  /**
   * build the list of ASSET-related tools
   */
  abstract protected void addUniqueTools();

  protected BoundsProvider getBounds()
  {
    return new BoundsProvider()
    {
      
      @Override
      public WorldArea getViewport()
      {
        return _theChart.getCanvas().getProjection().getVisibleDataArea();
      }
      
      @Override
      public WorldArea getBounds()
      {
        return _theChart.getDataArea();
      }
    };
  }

  /**
   * build the list of tools necessary for this type of view
   */
  private void addTools()
  {

    addUniqueTools();
    
    final BoundsProvider bounds = getBounds();

    // NOTE: wrap this next creator, in case we haven't got the right files avaialble
    try
    {
      _theTools.addElement(new MenuItemInfo("File", null, "Record to video",
                                            new ShowVideo(_theParent, _theProperties, _theChart.getPanel()), null, ' '));
    }
    catch (NoClassDefFoundError e)
    {
      System.err.println("Record to video not provided, JMF classes not found");
      //e.printStackTrace();
    }
    _theTools.addElement(new MenuItemInfo("File", null, "File",
                                          new ShowLayers(_theParent, "Show Outline View", _theProperties, _theData),
                                          null, ' '));


    //    _theTools.addElement(new MenuItemInfo("View", null, "Repaint", new Repaint(_theParent, _theChart), null, 'R'));

    //////////////////////////
    // second row
    //////////////////////////

    _theTools.addElement(new MenuItemInfo("View", null, "Repaint", new MWC.GUI.Tools.Chart.Repaint(_theParent, _theChart), null, 'r'));
    _theTools.addElement(new MenuItemInfo("View", null, "Fit", new FitToWin(_theParent, _theChart), null, 'f'));
    _theTools.addElement(new MenuItemInfo("View", "Drag", "Pan", new Pan(_theChart, _theParent, null), null, 'P'));
    _theTools.addElement(new MenuItemInfo("View", "Drag", "Rng Brg", new RangeBearing(_theChart, _theParent, getStatusBar()), null, 'B'));
    _theTools.addElement(new MenuItemInfo("View", "Drag", "Zoom", new ZoomIn(_theChart, _theParent), null, 'I'));
    _theTools.addElement(new MenuItemInfo("View", null, "Zoom Out", new ZoomOut(_theParent, _theChart),
                                          new java.awt.MenuShortcut(java.awt.event.KeyEvent.VK_SUBTRACT), 'O'));


    ////////////////////////////////////////////////////////////
    // now the decorations
    ////////////////////////////////////////////////////////////
    // find the decorations layer
    _theTools.addElement(new MenuItemInfo("Chart Features", null,
                                          "Create Scale",
                                          new CreateScale(_theParent, _theProperties,
                                                          _theData,
                                                          bounds), null, ' '));

    _theTools.addElement(new MenuItemInfo("Chart Features", null,
                                          "Create Grid",
                                          new CreateGrid(_theParent, _theProperties,
                                                         _theData,
                                                         bounds), null, ' '));

    _theTools.addElement(new MenuItemInfo("Chart Features", null,
                                          "Create VPF Layers",
                                          new CreateVPFLayers(_theParent, _theProperties,
                                                              _theData,
                                                              bounds), null, ' '));

    _theTools.addElement(new MenuItemInfo("Chart Features", null,
                                          "Create World Coastline",
                                          new CreateCoast(_theParent, _theProperties,
                                                          _theData,
                                                          bounds), null, ' '));

    _theTools.addElement(new MenuItemInfo("Chart Features", null,
                                          "Create ETOPO Bathy",
                                          new CreateTOPO(_theParent, _theProperties,
                                                         _theData,
                                                         bounds), null, ' '));

  }

  void setChart(final PlainChart theChart)
  {
    _theChart = theChart;
  }

  public void setToolbar(final Toolbar theToolbar)
  {
    _theToolbar = theToolbar;
  }


  void setProperties(final PropertiesPanel theProperties)
  {
    _theProperties = theProperties;
  }

  void setStatusBar(final StatusBar theBar)
  {
    _theStatusBar = theBar;
  }

  private StatusBar getStatusBar()
  {
    return _theStatusBar;
  }

  private PropertiesPanel getProperties()
  {
    return _theProperties;
  }

  public final ScenarioType getScenario()
  {
    return _theScenario;
  }

  /**
   * data has been modified, update panes as necesary
   */
  public void update()
  {
    // check they are valid
    if (_theChart != null)
      _theChart.update();
  }

  /**
   * data has been modified, update panes as necesary
   */
  public void rescale()
  {
    // check they are valid
    if (_theChart != null)
      _theChart.rescale();
  }

  protected PlainChart getChart()
  {
    return _theChart;
  }

  /**
   * fit the data to the current view
   */
  public void FitToWin()
  {
    _theChart.rescale();
    _theChart.repaint();
  }

  /**
   * get ready to close, set all local
   * references to null, to assist garbage collection
   */
  void close()
  {
    // we'll also try to remove all of the tools
    final Enumeration<MenuItemInfo> enumer = _theTools.elements();
    while (enumer.hasMoreElements())
    {
      final MenuItemInfo mn = (MenuItemInfo) enumer.nextElement();
      mn.close();
    }

    // clear the dangling reference to the undo buffer
    _rightClicker.closeMe();

    // now remove references to the tools themselves
    _theTools.removeAllElements();

    _theChart.close();
    _theChart = null;

    if (_theToolbar != null)
      _theToolbar.close();
    _theToolbar = null;
    _theProperties = null;
    _theStatusBar = null;
  }

  protected void finalize()
  {
    try
    {
      super.finalize();
    }
    catch (Throwable t)
    {
      t.printStackTrace();
    }
  }

  public String getName()
  {
    return _name;
  }

  public ToolParent getParent()
  {
    return _theParent;
  }


  UndoBuffer getUndoBuffer()
  {
    return _theBuffer;
  }

}

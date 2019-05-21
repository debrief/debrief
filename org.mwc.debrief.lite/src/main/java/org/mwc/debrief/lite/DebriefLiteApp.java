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
package org.mwc.debrief.lite;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.geotools.map.MapContent;
import org.geotools.swing.JMapPane;
import org.geotools.swing.action.ResetAction;
import org.mwc.debrief.lite.graph.GraphPanelView;
import org.mwc.debrief.lite.gui.FitToWindow;
import org.mwc.debrief.lite.gui.GeoToolMapProjection;
import org.mwc.debrief.lite.gui.LiteStepControl;
import org.mwc.debrief.lite.gui.custom.JXCollapsiblePane.Direction;
import org.mwc.debrief.lite.gui.custom.JXCollapsiblePaneWithTitle;
import org.mwc.debrief.lite.map.GeoToolMapRenderer;
import org.mwc.debrief.lite.map.GeoToolMapRenderer.MapRenderer;
import org.mwc.debrief.lite.map.MapBuilder;
import org.mwc.debrief.lite.menu.DebriefRibbon;
import org.mwc.debrief.lite.menu.DebriefRibbonFile;
import org.mwc.debrief.lite.menu.DebriefRibbonTimeController;
import org.mwc.debrief.lite.menu.MenuUtils;
import org.mwc.debrief.lite.outline.OutlinePanelView;
import org.mwc.debrief.lite.util.DoSaveAs;
import org.pushingpixels.flamingo.api.common.icon.ImageWrapperResizableIcon;
import org.pushingpixels.flamingo.api.ribbon.JRibbonFrame;
import org.pushingpixels.substance.api.SubstanceCortex;
import org.pushingpixels.substance.api.skin.BusinessBlueSteelSkin;

import Debrief.GUI.Tote.Painters.PainterManager;
import Debrief.GUI.Tote.Painters.SnailPainter;
import Debrief.GUI.Tote.Painters.TotePainter;
import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.ReaderWriter.XML.DebriefXMLReaderWriter;
import Debrief.ReaderWriter.XML.SessionHandler;
import Debrief.ReaderWriter.XML.dummy.SATCHandler_Mock;
import Debrief.ReaderWriter.XML.dynamic.DynamicLayerHandler;
import Debrief.ReaderWriter.XML.dynamic.DynamicShapeLayerHandler;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.BaseLayer;
import MWC.GUI.CanvasType;
import MWC.GUI.DataListenerAdaptor;
import MWC.GUI.Defaults;
import MWC.GUI.Defaults.PreferenceProvider;
import MWC.GUI.DynamicPlottable;
import MWC.GUI.Editable;
import MWC.GUI.HasEditables;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Layers.DataListener;
import MWC.GUI.Layers.DataListener2;
import MWC.GUI.PlainChart;
import MWC.GUI.Plottable;
import MWC.GUI.StepperListener;
import MWC.GUI.ToolParent;
import MWC.GUI.Canvas.CanvasAdaptor;
import MWC.GUI.Canvas.ExtendedCanvasAdapter;
import MWC.GUI.Dialogs.DialogFactory;
import MWC.GUI.DragDrop.FileDropSupport;
import MWC.GUI.DragDrop.FileDropSupport.FileDropListener;
import MWC.GUI.Undo.UndoBuffer;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.temporal.PlotOperations;
import MWC.TacticalData.temporal.TimeManager;
import MWC.TacticalData.temporal.TimeProvider;
import MWC.Utilities.Errors.Trace;
import MWC.Utilities.ReaderWriter.ImportManager;
import MWC.Utilities.ReaderWriter.ImportManager.BaseImportCaller;
import MWC.Utilities.ReaderWriter.PlainImporter;

/**
 * @author Ayesha <ayesha.ma@gmail.com>
 * @author Unni Mana <unnivm@gmail.com>
 */

public class DebriefLiteApp implements FileDropListener
{

  /**
   * introduce a preferences helper, particularly to give default font sizes
   *
   */
  private static class LiteProvider implements PreferenceProvider
  {

    private Font _defaultFont;

    @Override
    public Font getDefaultFont()
    {
      if (_defaultFont == null)
      {
        // it's ok if we throw an exception here, just in case we had
        // a platform-specific font initialise string
        _defaultFont = new Font("Arial", Font.PLAIN, 18);
      }
      return _defaultFont;
    }

    @Override
    public String getPreference(final String name)
    {
      final String res;
      if (SensorContactWrapper.TRANSPARENCY.equals(name))
      {
        res = "100";
      }
      else
      {
        return null;
      }
      return res;
    }
  }

  /**
   * helper class
   *
   * @author ian
   *
   */
  private static class ToteSetter implements Runnable
  {
    final private PainterManager _manager;
    final private StepperListener _painter;

    public ToteSetter(final PainterManager manager,
        final StepperListener painter)
    {
      _manager = manager;
      _manager.addPainter(painter);
      _painter = painter;
    }

    @Override
    public void run()
    {
      _manager.setCurrentListener(_painter);
    }
  }

  private static DebriefLiteApp _instance;
  public static final String appName = "Debrief Lite";
  public static final String NOTES_ICON = "icons/16/note.png";

  public static String currentFileName = null;
  public static final String ACTIVE_STATE = "ACTIVE";
  public static final String INACTIVE_STATE = "INACTIVE";

  public static String state = INACTIVE_STATE;

  public static PropertyChangeListener enableDisableButtons =
      new PropertyChangeListener()
      {

        @Override
        public void propertyChange(final PropertyChangeEvent evt)
        {
          final boolean isActive = ACTIVE_STATE.equals(evt.getNewValue());
          DebriefRibbonTimeController.setButtonsEnabled(
              DebriefRibbonTimeController.topButtonsPanel, isActive);
          DebriefRibbonFile.closeButton.setEnabled(isActive);
        }
      };

  private static ArrayList<PropertyChangeListener> stateListeners =
      new ArrayList<>(Arrays.asList(enableDisableButtons));

  protected static boolean _plotDirty;

  private static String defaultTitle = appName + " (" + Debrief.GUI.VersionInfo.getVersion() + ")";

  private final static LiteApplication app = new LiteApplication(
      ImportReplay.IMPORT_AS_OTG, 0L);

  static class JpopupMenuXY extends JPopupMenu
  {
    /**
     * 
     */
    private static final long serialVersionUID = -1716260829875966611L;
    public int x;
    public int y;
    
    @Override
    public void show(Component invoker, int x, int y)
    {
      this.x = x;
      this.y = y;
      super.show(invoker, x, y);
    }
  }
  
  /**
   * creates a scroll pane with map
   *
   * @param geoMapRenderer
   * @param dropSupport
   *
   * @return
   */
  private static JMapPane createMapPane(final GeoToolMapRenderer geoMapRenderer,
      final FileDropSupport dropSupport)
  {
    geoMapRenderer.createMapLayout();
    final MapBuilder builder = new MapBuilder();
    final JMapPane mapPane = (JMapPane) builder.setMapRenderer(geoMapRenderer)
        .build();
    dropSupport.addComponent(mapPane);
    
    final JpopupMenuXY popupMenu = new JpopupMenuXY();
    final JMenuItem copyCursorItem = new JMenuItem("Copy cursor location");
    copyCursorItem.addActionListener(new ActionListener()
    {
      
      @Override
      public void actionPerformed(ActionEvent e)
      {
        final WorldLocation location = _instance.projection.toWorld(
            new Point(popupMenu.x, popupMenu.y));
        
        System.out.println(location);
        String res = "LOC:" + location.getLat() + "," + location
            .getLong() + "," + location.getDepth();
        StringSelection stringSelection = new StringSelection(res);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
      }
    });
    popupMenu.add(copyCursorItem);
    mapPane.addMouseListener(new MouseAdapter()
    {

      @Override
      public void mousePressed(MouseEvent e)
      {
        showPopup(e);
      }

      @Override
      public void mouseReleased(MouseEvent e)
      {
        showPopup(e);
      }
      
      private void showPopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
          popupMenu.show(e.getComponent(),
                    e.getX(), e.getY());
        }
    }
      
    });
    
    return mapPane;
  }

  private static List<TrackWrapper> determineCandidateHosts()
  {
    final List<TrackWrapper> res = new ArrayList<TrackWrapper>();

    final Enumeration<Editable> iter = _instance._theLayers.elements();
    while (iter.hasMoreElements())
    {
      final Editable editable = iter.nextElement();
      if (editable instanceof TrackWrapper)
      {
        res.add((TrackWrapper) editable);
      }
    }

    return res;
  }

  public static ToolParent getDefault()
  {
    return app;
  }

  public static DebriefLiteApp getInstance()
  {
    return _instance;
  }

  public static boolean isDirty()
  {
    return _plotDirty;
  }

  public static void main(final String[] args)
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      @Override
      public void run()
      {
        _instance = new DebriefLiteApp();
      }
    });

  }

  private static void notifyListenersStateChanged(final Object source,
      final String property, final String oldValue, final String newValue)
  {
    for (final PropertyChangeListener event : stateListeners)
    {
      event.propertyChange(new PropertyChangeEvent(source, property, oldValue,
          newValue));
    }
  }

  public static void openDsfFile(final File file) throws FileNotFoundException
  {
    boolean renameFile=true;
    final ImportReplay rep = new ImportReplay();
    rep.setLayers(_instance._theLayers);
    rep.importThis(file.getAbsolutePath(), new FileInputStream(file));
    final Vector<SensorWrapper> sensors = rep.getPendingSensors();

    // see if there are any sensors awaiting a host
    if (sensors.size() >= 1)
    {
      final Iterator<SensorWrapper> sIter = sensors.iterator();
      while (sIter.hasNext())
      {
        final SensorWrapper sensor = sIter.next();
        if (sensor.getHost() == null)
        {
          // have we sorted out the hosts?
          final List<TrackWrapper> candidateHosts =
              determineCandidateHosts();

          if (candidateHosts.size() == 0)
          {
            renameFile = false;
            JOptionPane.showMessageDialog(null,
                "Sensor data can only be loaded after tracks",
                "Loading sensor data", JOptionPane.ERROR_MESSAGE);
            break;
          }

        }
      }
    }
    if (renameFile)
    {
      rep.storePendingSensors();
      _instance._theLayers.fireExtended();
      JOptionPane.showMessageDialog(null,
          "Finished loading sensor data from the file",
          "Loading sensor data", JOptionPane.INFORMATION_MESSAGE);

    }
  }

  public static void openPlotFile(final File file)
  {
    
    try
    {
      _instance.handleImportDPF(file);
    }
    catch (final Exception e)
    {
      Trace.trace(e);
    }
  }

  public static void openRepFile(final File file)
  {
    try
    {
      _instance.handleImportRep(new File[]
      {file});
    }
    catch (final Exception e)
    {
      Trace.trace(e);
    }
  }

  public static void setDirty(final boolean b)
  {

    _plotDirty = b;
    if (currentFileName != null)
    {
      final String name = new File(currentFileName).getName();
      if (b)
      {
        setTitle(name + " *");
      }
      else
      {
        setTitle(name);
      }
    }

  }

  /**
   * State of the application. Inactive will disable all the button.
   *
   * @param newState
   */
  public static void setState(final String newState)
  {
    final String oldState = state;
    state = newState;

    notifyListenersStateChanged(_instance, "STATE", oldState, newState);
  }

  public static void setTitle(final String title)
  {
    if (title.startsWith(defaultTitle))
    {
      _instance.theFrame.setTitle(title);
    }
    else
    {
      _instance.theFrame.setTitle(defaultTitle + " - " + title);
    }
  }

  /**
   * @param filename
   *          autofilled
   */
  private static String suffixOf(final String filename)
  {
    String theSuffix = null;
    final int pos = filename.lastIndexOf(".");
    theSuffix = filename.substring(pos, filename.length());
    return theSuffix.toUpperCase();
  }

  protected DataListener2 _listenForMods;

  private OutlinePanelView layerManager;
  private GraphPanelView graphPanelView;
  private final JXCollapsiblePaneWithTitle outlinePanel =
      new JXCollapsiblePaneWithTitle(Direction.LEFT, "Outline", 400);
  private final JXCollapsiblePaneWithTitle graphPanel =
      new JXCollapsiblePaneWithTitle(Direction.DOWN, "Graph", 150);
  private final JRibbonFrame theFrame;
  final private Layers _theLayers = new Layers();
  private GeoToolMapProjection projection;

  private final LiteSession session;
  private final JLabel statusBar = new JLabel(
      "Status bar for displaying statuses");

  private final JMapPane mapPane;

  private final PlotOperations _myOperations = new PlotOperations()
  {
    // just provide with our complete set of layers
    @Override
    public Object[] getTargets()
    {
      // ok, return our top level layers as objects
      final Vector<Layer> res = new Vector<Layer>(0, 1);
      for (int i = 0; i < _theLayers.size(); i++)
      {
        res.add(_theLayers.elementAt(i));
      }
      return res.toArray();
    }

    /**
     * override performing the operation, since we'll do a screen update on completion
     */
    @Override
    public Vector<Layer> performOperation(final AnOperation operationName)
    {
      // make the actual change
      final Vector<Layer> res = super.performOperation(operationName);

      if (res != null && res.size() != 0)
      {
        for (final Iterator<Layer> iter = res.iterator(); iter.hasNext();)
        {
          final Layer thisL = iter.next();
          // and update the screen
          _theLayers.fireReformatted(thisL);
        }
      }
      return res;
    }
  };

  private final TimeManager timeManager = new TimeManager();

  private final GeoToolMapRenderer geoMapRenderer;

  private final PainterManager painterManager;

  private final LiteTote theTote;

  private final LiteStepControl _stepControl;

  private final Layer safeChartFeatures;

  public DebriefLiteApp()
  {
    // set the substance look and feel
    JFrame.setDefaultLookAndFeelDecorated(true);
    SubstanceCortex.GlobalScope.setSkin(new BusinessBlueSteelSkin());
    final DisplaySplash splashScreen = new DisplaySplash(5);
    final Thread t = new Thread(splashScreen);
    t.start();
    try
    {
      t.join();
    }
    catch (final InterruptedException e)
    {
      // ignore
    }

    // configure the default fonts, etc
    Defaults.setProvider(new LiteProvider());

    // for legacy integration we need to provide a tool-parent
    Trace.initialise(app);

    theFrame = new JRibbonFrame(defaultTitle);

    theFrame.setApplicationIcon(ImageWrapperResizableIcon.getIcon(MenuUtils
        .createImage("icons/d_lite.png"), MenuUtils.ICON_SIZE_32));

    geoMapRenderer = new GeoToolMapRenderer();
    initializeMapContent();

    final FileDropSupport dropSupport = new FileDropSupport();
    dropSupport.setFileDropListener(this, " .REP, .XML, .DSF, .DTF, .DPF");

    // provide some file helpers
    ImportReplay.initialise(app);
    ImportManager.addImporter(new ImportReplay());

    // sort out time control
    final Clipboard _theClipboard = new Clipboard("Debrief");
    session = new LiteSession(_theClipboard, _theLayers);
    _stepControl = new LiteStepControl(app, session);
    session.setStepper(_stepControl);
    app.setSession(session);
    app.setFrame(theFrame);

    _stepControl.setUndoBuffer(session.getUndoBuffer());
    _stepControl.setLayers(session.getData());

    // take a safe copy of the chart features layer
    safeChartFeatures = _theLayers.findLayer(Layers.CHART_FEATURES);

    ImportManager.addImporter(new DebriefXMLReaderWriter(app));
    mapPane = createMapPane(geoMapRenderer, dropSupport);
    final CanvasAdaptor theCanvas = new CanvasAdaptor(projection, mapPane
        .getGraphics());

    timeManager.addListener(_stepControl,
        TimeProvider.PERIOD_CHANGED_PROPERTY_NAME);
    timeManager.addListener(_stepControl,
        TimeProvider.TIME_CHANGED_PROPERTY_NAME);
    timeManager.addListener(new PropertyChangeListener()
    {

      @Override
      public void propertyChange(final PropertyChangeEvent evt)
      {
        redoTimePainter(false, theCanvas, (HiResDate) evt.getOldValue(),
            (HiResDate) evt.getNewValue());
      }
    }, TimeProvider.TIME_CHANGED_PROPERTY_NAME);

    final DataListener dListener = new DataListener()
    {
      @Override
      public void dataExtended(final Layers theData)
      {
        mapPane.repaint();
      }

      @Override
      public void dataModified(final Layers theData, final Layer changedLayer)
      {
        mapPane.repaint();
      }

      @Override
      public void dataReformatted(final Layers theData,
          final Layer changedLayer)
      {
        mapPane.repaint();
      }
    };
    _theLayers.addDataReformattedListener(dListener);
    _theLayers.addDataExtendedListener(dListener);
    _theLayers.addDataModifiedListener(dListener);

    painterManager = new PainterManager(_stepControl);
    final PlainChart theChart = new LiteChart(_theLayers, theCanvas, mapPane);
    theTote = new LiteTote(_theLayers, _stepControl);
    final TotePainter tp = new TotePainter(theChart, _theLayers, theTote);
    tp.setColor(Color.white);
    final SnailPainter sp = new SnailPainter(theChart, _theLayers, theTote);

    final ToteSetter normalT = new ToteSetter(painterManager, tp);
    final ToteSetter snailT = new ToteSetter(painterManager, sp);
    normalT.run();

    // create the components
    initForm();
    createAppPanels(geoMapRenderer, session.getUndoBuffer(), dropSupport,
        mapPane, _stepControl, timeManager, _myOperations, normalT, snailT,
        statusBar);
    _listenForMods = new DataListenerAdaptor()
    {

      @Override
      public void dataExtended(final Layers theData, final Plottable newItem,
          final HasEditables parent)
      {
        update(theData, newItem, parent);
        if (parent != null)
        {
          setDirty(true);
          setState(ACTIVE_STATE);
        }
      }
    };

    // tell the Session handler about the optional dynamic layer handlers
    SessionHandler.addAdditionalHandler(new DynamicLayerHandler());
    SessionHandler.addAdditionalHandler(new DynamicShapeLayerHandler());
    SessionHandler.addAdditionalHandler(new SATCHandler_Mock());

    _theLayers.addDataExtendedListener(_listenForMods);
    _theLayers.addDataModifiedListener(_listenForMods);
    _theLayers.addDataReformattedListener(_listenForMods);
    theFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    theFrame.setVisible(true);
    theFrame.getRibbon().setSelectedTask(DebriefRibbonFile.getFileTask());
  }

  private void addGraphView()
  {
    graphPanelView = new GraphPanelView(_stepControl);
    graphPanel.setCollapsed(true);
    graphPanel.add(graphPanelView, BorderLayout.CENTER);
  }

  private void addOutlineView(final ToolParent toolParent,
      final UndoBuffer undoBuffer)
  {
    layerManager = new OutlinePanelView(undoBuffer, session.getClipboard());
    layerManager.setObject(_theLayers);
    layerManager.setParent(toolParent);
    outlinePanel.add(layerManager, BorderLayout.CENTER);
  }

  private void createAppPanels(final GeoToolMapRenderer geoMapRenderer,
      final UndoBuffer undoBuffer, final FileDropSupport dropSupport,
      final Component mapPane, final LiteStepControl stepControl,
      final TimeManager timeManager, final PlotOperations operation,
      final ToteSetter normalT, final ToteSetter snailT, final JLabel statusBar)
  {
    // final Dimension frameSize = theFrame.getSize();
    // final int width = (int) frameSize.getWidth();

    final JPanel centerPanel = new JPanel();
    centerPanel.setLayout(new BorderLayout());
    mapPane.addComponentListener(new ComponentAdapter()
    {
      @Override
      public void componentResized(final ComponentEvent e)
      {
        // TODO . This must be change once we update geotools.
        mapPane.setVisible(false);
        mapPane.setVisible(true);
      }
    });

    centerPanel.add(mapPane, BorderLayout.CENTER);
    centerPanel.add(graphPanel, BorderLayout.PAGE_END);

    theFrame.add(centerPanel, BorderLayout.CENTER);

    theFrame.add(outlinePanel, BorderLayout.WEST);
    addOutlineView(app, undoBuffer);
    addGraphView();

    theFrame.add(statusBar, BorderLayout.SOUTH);
    final Runnable resetAction = new Runnable()
    {
      @Override
      public void run()
      {
        resetPlot();
      }
    };
    Runnable exitAction = new Runnable()
    {
      @Override
      public void run()
      {
        exit();
      }
    };
    new DebriefRibbon(theFrame.getRibbon(), _theLayers, app, geoMapRenderer,
        stepControl, timeManager, operation, session, resetAction, normalT,
        snailT, statusBar, exitAction);
  }

  protected void doPaint(final Graphics gc)
  {
    final CanvasAdaptor dest;
    if (gc instanceof Graphics2D)
    {
      dest = new ExtendedCanvasAdapter(projection, gc, Color.red);
    }
    else
    {
      final String s = "Lite rendering is expecting a Graphics2D object";
      app.logError(ToolParent.ERROR, s, null);
      throw new IllegalArgumentException(s);
    }

    // ok, are we in snail mode?
    final String current = painterManager.getCurrentPainterObject().toString();
    if (current.equals(TotePainter.NORMAL_NAME))
    {
      // ok, we need to draw in the layers
      dest.setLineWidth(2f);
      dest.startDraw(gc);
      _theLayers.paint(dest);
    }

    // and the time marker
    redoTimePainter(true, dest, null, null);

    dest.endDraw(gc);
  }

  public void exit()
  {
    if (DebriefLiteApp.isDirty())
    {
      final int res = JOptionPane.showConfirmDialog(theFrame,
          "Save before exiting Debrief Lite?", "Warning",
          JOptionPane.YES_NO_CANCEL_OPTION);
      if (res == JOptionPane.OK_OPTION)
      {
        final String currentFileName = DebriefLiteApp.currentFileName;
        String outputFileName = null;
        if (currentFileName != null)
        {
          final File currentFile = new File(currentFileName);
          final File directory = currentFile.getParentFile();
          if (currentFileName.endsWith(".dpf"))
          {
            DebriefRibbonFile.saveChanges(currentFileName, session, theFrame);
            exitApp();
          }
          else
          {
            final String initialName = currentFile.getName().substring(0,
                currentFile.getName().lastIndexOf("."));
            outputFileName = DoSaveAs.showSaveDialog(directory, initialName);
          }
        }
        else
        {
          final File directory;
          final String lastFileLocation = DebriefLiteApp.getDefault()
              .getProperty(DoSaveAs.LAST_FILE_LOCATION);
          if (lastFileLocation != null)
          {
            directory = new File(lastFileLocation);
          }
          else
          {
            directory = null;
          }
          outputFileName = DoSaveAs.showSaveDialog(directory, "DebriefPlot");
        }
        if (outputFileName != null)
        {
          DebriefRibbonFile.saveChanges(outputFileName, session, theFrame);
          exitApp();
        }

      }
      else if (res == JOptionPane.NO_OPTION)
      {
        exitApp();
      }
    }
    else
    {
      exitApp();
    }

  }

  private void exitApp()
  {
    session.close();
    theFrame.dispose();
    System.exit(0);
  }

  @Override
  public void FilesReceived(final Vector<File> files)
  {
    setCursor(Cursor.WAIT_CURSOR);
    File file = null;
    try
    {
      final Enumeration<File> iter = files.elements();

      while (iter.hasMoreElements())
      {
        file = iter.nextElement();

        final String suff = suffixOf(file.getName());
        if (suff.equalsIgnoreCase(".DPL"))
        {
          DialogFactory.showMessage("Open File",
              "Sorry DPL file format no longer supported");
        }
        else
        {
          if (suff.equalsIgnoreCase(".DSF"))
          {
            openDsfFile(file);
          }
          else if ((suff.equalsIgnoreCase(".REP")) || (suff.equalsIgnoreCase(
              ".DTF")))
          {
            // fake wrap it
            final File[] fList = new File[]
            {file};
            handleImportRep(fList);
          }
          else if (suff.equalsIgnoreCase(".XML") || suff.equalsIgnoreCase(
              ".DPF"))
          {
            handleImportDPF(file);
          }
          else
          {
            Trace.trace("This file type not handled:" + suff);
            DialogFactory.showMessage("Open Debrief file",
                "This file type not handled:" + suff);
          }
        }
      }
    }
    catch (final Exception e)
    {
      Trace.trace(e);
      MWC.GUI.Dialogs.DialogFactory.showMessage("Open Debrief file",
          "Error Opening the file: " + e.getMessage());
    }
    restoreCursor();
  }

  public OutlinePanelView getLayerManager()
  {
    return layerManager;
  }

  private void handleImportDPF(final File file)
  {
    long startTime = System.currentTimeMillis();
    System.out.println("Started loading file");
    boolean success = true;
    final DebriefXMLReaderWriter reader = new DebriefXMLReaderWriter(app);
    try
    {
      reader.importThis(file.getName(), new FileInputStream(file), session);

      // update the time panel
      final TimePeriod period = _theLayers.getTimePeriod();
      _myOperations.setPeriod(period);
      timeManager.setPeriod(this, period);
      if (period != null)
      {
        timeManager.setTime(this, period.getStartDTG(), true);
      }
      if (_stepControl.getDateFormat() != null)
      {
        DebriefRibbonTimeController.assignThisTimeFormat(_stepControl
            .getDateFormat(), true, true);
      }
      _theLayers.fireModified(null);
      
      // and the spatial bounds
      new FitToWindow(_theLayers, mapPane).actionPerformed(null);
    }
    catch (final FileNotFoundException e)
    {
      app.logError(ToolParent.ERROR, "Failed to read DPF File", e);
      MWC.GUI.Dialogs.DialogFactory.showMessage("Open Debrief file",
          "Failed to read DPF File" + e.getMessage());
      success = false;
    }
    catch (final PlainImporter.ImportException ie)
    {
      DialogFactory.showMessage("Error in opening file", ie.getMessage());
      success = false;
    }

    if (success)
    {
      resetFileName(file);
    }
    long endTime = System.currentTimeMillis();
    long timeElapsed = endTime-startTime;
    System.out.println("Time taken:"+timeElapsed);
  }

  private void handleImportRep(final File[] fList)
  {
    
    final DebriefLiteApp source = this;
    boolean success = true;
    if(fList.length==1) {
      if(fList[0].getName().endsWith("dsf")) {
        try
        {
          openDsfFile(fList[0]);
        }
        catch (FileNotFoundException e)
        {
          e.printStackTrace();
        }
      }
      else {
        final BaseImportCaller caller = new BaseImportCaller(fList, _theLayers)
        {
          // handle completion of the full import process
          @Override
          public void allFilesFinished(final File[] fNames, final Layers newData)
          {
            finishImport(source);
          }

          // handle the completion of each file
          @Override
          public void fileFinished(final File fName, final Layers newData)
          {

          }

          private void finishImport(final DebriefLiteApp source)
          {
            SwingUtilities.invokeLater(new Runnable()
            {
              @Override
              public void run()
              {
                setCursor(Cursor.WAIT_CURSOR);
                layerManager.createAndInitializeTree();
                mapPane.repaint();
                restoreCursor();
                // update the time panel
                final TimePeriod period = _theLayers.getTimePeriod();
                _myOperations.setPeriod(period);
                timeManager.setPeriod(source, period);
                if (period != null)
                {
                  timeManager.setTime(source, period.getStartDTG(), true);
                }

                theTote.assignWatchables(true);

                // and the spatial bounds
                final FitToWindow fitMe = new FitToWindow(_theLayers, mapPane);
                fitMe.actionPerformed(null);

                populateTote();
              }
            });
          }
        };
        try
        {
          // ok, start loading
          caller.start();
        }
        catch (final PlainImporter.ImportException ie)
        {
          success = false;
        }

        if (success)
        {
          resetFileName(fList[0]);
        }
      }
    }
  }

  /**
   * fill in the UI details
   *
   * @param theToolbar
   */
  private void initForm()
  {
    theFrame.addWindowListener(new WindowAdapter()
    {
      @Override
      public void windowClosing(final java.awt.event.WindowEvent e)
      {
        exit();
      }
    });

    // try to give the application an icon
    final Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

    /*
     * theFrame.getRibbon().setApplicationMenu(new RibbonAppMenuProvider()
     * .createApplicationMenu(theFrame));
     */
    // It cannot be smaller than this size to have the ribbon complete!
    final int sizeWidth = Math.max((int) (dim.width * 0.6), 870);
    final int sizeHeight = (int) (dim.height * 0.6);
    theFrame.setSize(sizeWidth, sizeHeight);
    final Dimension sz = theFrame.getSize();
    theFrame.setLocation((dim.width - sz.width) / 2, (dim.height - sz.height)
        / 2);

    // do any final re-arranging
    theFrame.doLayout();
  }

  private void initializeMapContent()
  {
    geoMapRenderer.loadMapContent();
    final MapContent mapComponent = geoMapRenderer.getMapComponent();
    projection = new GeoToolMapProjection(mapComponent, _theLayers);

    geoMapRenderer.addRenderer(new MapRenderer()
    {

      @Override
      public void paint(final Graphics gc)
      {
        doPaint(gc);
      }
    });
  }

  /**
   * new data has been added - have a look at the times
   */
  protected void layersExtended()
  {

  }

  private void paintDynamicLayers(final CanvasType dest)
  {
    final HiResDate tNow = timeManager.getTime();
    // do we have time?
    if (tNow != null)
    {
      final long timeVal = tNow.getDate().getTime();
      final Enumeration<Editable> lIter = _theLayers.elements();
      while (lIter.hasMoreElements())
      {
        final Editable next = lIter.nextElement();
        if (next instanceof DynamicPlottable)
        {
          final DynamicPlottable dp = (DynamicPlottable) next;
          dp.paint(dest, timeVal);
        }
      }
    }
  }

  private void populateTote()
  {
    //not implemented.
  }

  private void redoTimePainter(final boolean bigPaint, final CanvasAdaptor dest,
      final HiResDate oldDTG, final HiResDate newDTG)
  {
    final StepperListener current = painterManager.getCurrentPainterObject();
    final boolean isNormal = current.toString().equals(TotePainter.NORMAL_NAME);

    // we need to use different XOR background colors depending on if
    // we're in normal or snail mode
    final Color backColor = isNormal ? Color.BLACK : Color.white;

    // and the time marker
    final Graphics graphics = mapPane.getGraphics();

    if (bigPaint)
    {
      final CanvasType.PaintListener thisPainter =
          (CanvasType.PaintListener) painterManager.getCurrentPainterObject();

      // it must be ok
      final CanvasAdaptor adapter = new CanvasAdaptor(projection, dest
          .getGraphicsTemp(), backColor);
      thisPainter.paintMe(adapter);

      // also render dynamic layers
      paintDynamicLayers(adapter);
    }
    else
    {
      if (!isNormal)
      {
        final SnailPainter snail = (SnailPainter) current;
        snail.setVectorStretch(1d);
      }

      final CanvasAdaptor adapter = new CanvasAdaptor(projection, graphics,
          backColor);
      painterManager.newTime(oldDTG, newDTG, adapter);

      // also render dynamic layers
      paintDynamicLayers(adapter);
    }
  }

  private void resetFileName(final File file)
  {
    if (DebriefLiteApp.currentFileName == null)
    {
      DebriefLiteApp.currentFileName = file.getAbsolutePath();
      DebriefLiteApp.setTitle(file.getName());
    }
  }

  public void resetPlot()
  {
    // clear teh data
    _theLayers.clear();
    layerManager.resetTree();

    // special behaviour. The chart creator objects take a point to the
    // target layer on creation. So, we need to keep the same chart features layer
    // for the running session.
    if (safeChartFeatures != null)
    {
      final BaseLayer bl = (BaseLayer) safeChartFeatures;
      bl.removeAllElements();
    }
    _theLayers.addThisLayer(safeChartFeatures);

    // continue with reset processing
    _plotDirty = false;
    setState(INACTIVE_STATE);
    currentFileName = null;
    setTitle(defaultTitle);

    // also clear the tote
    theTote.clear();

    timeManager.setPeriod(this, null);
    timeManager.setTime(this, null, false);

    // and the time format dropdown
    DebriefRibbonTimeController.resetDateFormat();

    // stop the timer
    if (_stepControl.isPlaying())
    {
      _stepControl.startStepping(false);
    }

    // send a reset to the step control
    _stepControl.reset();

    // reset the map
    final ResetAction resetMap = new ResetAction(mapPane);
    resetMap.actionPerformed(null);
    
    graphPanelView.reset();
    graphPanel.setCollapsed(true);
  }

  public final void restoreCursor()
  {
    theFrame.getContentPane().setCursor(null);
  }

  public final void setCursor(final int theCursor)
  {
    theFrame.getContentPane().setCursor(new Cursor(theCursor));
  }

  public void setStatus(final String message)
  {
    statusBar.setText(message);
  }

  protected void update(final Layers theData, final Plottable newItem,
      final HasEditables theLayer)
  {
    getLayerManager().updateData((Layer) theLayer, newItem);
  }
}
